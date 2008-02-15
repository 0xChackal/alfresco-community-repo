/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.bean.rules;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ExecuteAllRulesActionExecuter;
import org.alfresco.repo.rule.RuleModel;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.context.IContextListener;
import org.alfresco.web.app.context.UIContextService;
import org.alfresco.web.bean.dialog.BaseDialogBean;
import org.alfresco.web.bean.dialog.FilterViewSupport;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.alfresco.web.ui.common.component.UIListItem;
import org.alfresco.web.ui.common.component.UIModeList;
import org.alfresco.web.ui.common.component.data.UIRichList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Backing bean for the manage content rules dialog
 *  
 * @author gavinc
 */
public class RulesDialog extends BaseDialogBean implements IContextListener, FilterViewSupport
{
   private static final long serialVersionUID = -1255494344597331464L;
   
   private static final String MSG_REAPPLY_RULES_SUCCESS = "reapply_rules_success";
   private static final String MSG_IGNORE_INHERTIED_RULES = "ignore_inherited_rules";
   private static final String MSG_INCLUDE_INHERITED_RULES = "include_inherited_rules";
   private static final String LOCAL = "local";
   private static final String INHERITED = "inherited";
   private final static String MSG_CLOSE = "close";
   

   
   private static Log logger = LogFactory.getLog(RulesDialog.class);
   
   private String filterModeMode = INHERITED;
   
   transient private RuleService ruleService;
   private List<WrappedRule> rules;
   private Rule currentRule;
   private UIRichList richList;
   transient private ActionService actionService;
   
   
   /**
    * Default constructor
    */
   public RulesDialog()
   {
      super();
      UIContextService.getInstance(FacesContext.getCurrentInstance()).registerBean(this);
   }
   
     
   /**
    * @return The space to work against
    */
   public Node getSpace()
   {
      return this.browseBean.getActionSpace();
   }
   
   /**
    * Returns the current rule 
    * 
    * @return The current rule
    */
   public Rule getCurrentRule()
   {
      return this.currentRule;
   }
   
   /**
    * Returns the list of rules to display
    * 
    * @return List of WrappedRule objects
    */
   public List<WrappedRule> getRules()
   {
      boolean includeInherited = true;
      
      if (this.filterModeMode.equals(LOCAL))
      {
         includeInherited = false;
      }

      // get the rules from the repository
      List<Rule> repoRules = this.getRuleService().getRules(getSpace().getNodeRef(), includeInherited);
      this.rules = new ArrayList<WrappedRule>(repoRules.size());
      
      // wrap them all passing the current space
      for (Rule rule : repoRules)
      {
         Date createdDate = (Date)this.getNodeService().getProperty(rule.getNodeRef(), ContentModel.PROP_CREATED);
         Date modifiedDate = (Date)this.getNodeService().getProperty(rule.getNodeRef(), ContentModel.PROP_MODIFIED);
         boolean isLocal = getSpace().getNodeRef().equals(this.getRuleService().getOwningNodeRef(rule));        
         
         WrappedRule wrapped = new WrappedRule(rule, isLocal, createdDate, modifiedDate);
         this.rules.add(wrapped);
      }
      
      return this.rules;
   }
   
   /**
    * Handles a rule being clicked ready for an action i.e. edit or delete
    * 
    * @param event The event representing the click
    */
   public void setupRuleAction(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         if (logger.isDebugEnabled())
            logger.debug("Rule clicked, it's id is: " + id);
         
         this.currentRule = this.getRuleService().getRule(new NodeRef(id));
         
         // refresh list
         contextUpdated();
      }
   }  
   
   /**
    * Reapply the currently defines rules to the
    * @param event
    */
   public void reapplyRules(ActionEvent event)
   {
      FacesContext fc = FacesContext.getCurrentInstance();
      
      UserTransaction tx = null;
      
      try
      {
         tx = Repository.getUserTransaction(fc);
         tx.begin();
         
         // Create the the apply rules action
         Action action = this.getActionService().createAction(ExecuteAllRulesActionExecuter.NAME);
         
         // Set the include inherited parameter to match the current filter value
         boolean executeInherited = true;
         if (LOCAL.equals(this.getFilterMode()) == true)
         {
            executeInherited = false;
         }
         action.setParameterValue(ExecuteAllRulesActionExecuter.PARAM_EXECUTE_INHERITED_RULES, executeInherited);
         
         // Execute the action
         this.getActionService().executeAction(action, this.getSpace().getNodeRef());
         
         // TODO how do I get the message here ...
         String msg = Application.getMessage(fc, MSG_REAPPLY_RULES_SUCCESS);
         FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
         String formId = Utils.getParentForm(fc, event.getComponent()).getClientId(fc);
         fc.addMessage(formId + ":rulesList", facesMsg);
         
         // commit the transaction
         tx.commit();
      }
      catch (Throwable e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               fc, Repository.ERROR_GENERIC), e.getMessage()), e);
      }
   }
   
   /**
    * Gets the label id from the ignore inhertied action
    * 
    * @return   the message id  
    */
   public String getIgnoreInheritedRulesLabelId()
   {
      FacesContext fc = FacesContext.getCurrentInstance();
      String result = Application.getMessage(fc, MSG_IGNORE_INHERTIED_RULES);
      
      if (this.getNodeService().hasAspect(this.getSpace().getNodeRef(), RuleModel.ASPECT_IGNORE_INHERITED_RULES) == true)
      {
         result = Application.getMessage(fc, MSG_INCLUDE_INHERITED_RULES);
      }
      return result;
   }
   
   public boolean getIgnoreInheritedRules()
   {
      return this.getNodeService().hasAspect(this.getSpace().getNodeRef(), RuleModel.ASPECT_IGNORE_INHERITED_RULES);
   }
   
   /**
    * Action listener to ignore (or include) inherited rules.
    * 
    * @param event  the action event object  
    */
   public void ignoreInheritedRules(ActionEvent event)
   {
      NodeRef nodeRef = this.getSpace().getNodeRef();
      if (this.getNodeService().hasAspect(nodeRef, RuleModel.ASPECT_IGNORE_INHERITED_RULES) == true)
      {
         this.getNodeService().removeAspect(nodeRef, RuleModel.ASPECT_IGNORE_INHERITED_RULES);
      }
      else
      {
         this.getNodeService().addAspect(nodeRef, RuleModel.ASPECT_IGNORE_INHERITED_RULES, null);
      }
      
      // force the list to be re-queried when the page is refreshed
      if (this.richList != null)
      {
         this.richList.setValue(null);
      }
   }
   
   
    @Override
   protected String finishImpl(FacesContext context, String outcome) throws Exception
   {
      return null;
   }
   
  @Override
   public void restored()
   {
      super.restored();
      contextUpdated();
   }
   
  @Override
   public String getCancelButtonLabel()
   {
      return Application.getMessage(FacesContext.getCurrentInstance(), MSG_CLOSE);
   }

   /**
    * Sets the UIRichList component being used by this backing bean
    * 
    * @param richList UIRichList component
    */
   public void setRichList(UIRichList richList)
   {
      this.richList = richList;
   }
   
   /**
    * Returns the UIRichList component being used by this backing bean
    * 
    * @return UIRichList component
    */
   public UIRichList getRichList()
   {
      return this.richList;
   }
   
   
   /**
    * @param ruleService Sets the rule service to use
    */
   public void setRuleService(RuleService ruleService)
   {
      this.ruleService = ruleService;
   }
   
   protected RuleService getRuleService()
   {
      if (ruleService == null)
      {
         ruleService = Repository.getServiceRegistry(FacesContext.getCurrentInstance()).getRuleService();
      }
      return ruleService;
   }
   
   /**
    * Set the action service to use
    * 
    * @param actionService      the action service
    */
   public void setActionService(ActionService actionService)
   {
      this.actionService = actionService;
   }
   
   private ActionService getActionService()
   {
      if (actionService == null)
      {
         actionService = Repository.getServiceRegistry(FacesContext.getCurrentInstance()).getActionService();
      }
      return actionService;
   }
   
   // ------------------------------------------------------------------------------
   // IContextListener implementation

   /**
    * @see org.alfresco.web.app.context.IContextListener#contextUpdated()
    */
   public void contextUpdated()
   {
      if (this.richList != null)
      {
         this.richList.setValue(null);
      }
   }
   
   /**
    * @see org.alfresco.web.app.context.IContextListener#areaChanged()
    */
   public void areaChanged()
   {
      // nothing to do
   }

   /**
    * @see org.alfresco.web.app.context.IContextListener#spaceChanged()
    */
   public void spaceChanged()
   {
      // nothing to do
   }
   
   /**
    * Inner class to wrap the Rule objects so we can expose a flag to indicate whether
    * the rule is a local or inherited rule
    */
   public static class WrappedRule
   {
      private Rule rule;
      private boolean isLocal;
      private Date createdDate;
      private Date modifiedDate;
      
      /**
       * Constructs a RuleWrapper object
       * 
       * @param rule The rule we are wrapping
       * @param isLocal Whether the rule is defined locally
       * @param createdDate Date the rule was created
       * @param modifiedDate The date the rule was last modified
       */
      public WrappedRule(Rule rule, boolean isLocal, Date createdDate, Date modifiedDate)
      {
         this.rule = rule;
         this.isLocal = isLocal;
         this.createdDate = createdDate;
         this.modifiedDate = modifiedDate;
      }
      
      /**
       * Returns the rule being wrapped
       * 
       * @return The wrapped Rule
       */
      public Rule getRule()
      {
         return this.rule;
      }
      
      /**
       * Determines whether the current rule is a local rule or
       * has been inherited from a parent
       * 
       * @return true if the rule is defined on the current node
       */
      public boolean getLocal()
      {
         return this.isLocal;
      }

      /** Methods to support sorting of the rules list in a table  */
      
      /**
       * Returns the rule id
       * 
       * @return The id
       */
      public String getId()
      {
         return this.rule.getNodeRef().toString();
      }
      
      /**
       * Returns the rule title
       * 
       * @return The title
       */
      public String getTitle()
      {
         return this.rule.getTitle();
      }
      
      /**
       * Returns the rule description
       * 
       * @return The description
       */
      public String getDescription()
      {
         return this.rule.getDescription();
      }
      
      /**
       * Returns the created date
       * 
       * @return The created date
       */
      public Date getCreatedDate()
      {
         return this.createdDate;
      }
      
      /**
       * Returns the modfified date
       * 
       * @return The modified date
       */
      public Date getModifiedDate()
      {
         return this.modifiedDate;
      }
   }

   public void filterModeChanged(ActionEvent event)
   {
      UIModeList viewList = (UIModeList)event.getComponent();
      this.filterModeMode = viewList.getValue().toString();
      
      // force the list to be re-queried when the page is refreshed
      if (this.richList != null)
      {
         this.richList.setValue(null);
      }
   }

   public List<UIListItem> getFilterItems()
   {
      FacesContext context = FacesContext.getCurrentInstance();
      List<UIListItem> items = new ArrayList<UIListItem>(2);
      
      UIListItem item1 = new UIListItem();
      item1.setValue(INHERITED);
      item1.setLabel(Application.getMessage(context, INHERITED));
      items.add(item1);
      
      UIListItem item2 = new UIListItem();
      item2.setValue(LOCAL);
      item2.setLabel(Application.getMessage(context, LOCAL));
      items.add(item2);
      
      return items;
   }

   public String getFilterMode()
   {
      return filterModeMode;
   }

   public void setFilterMode(String filterMode)
   {
      this.filterModeMode = filterMode;
      
      // clear datalist cache ready to change results based on filter setting
      contextUpdated();

   }
   
}
