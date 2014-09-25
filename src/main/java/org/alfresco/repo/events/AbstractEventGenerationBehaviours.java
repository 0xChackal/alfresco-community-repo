/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.repo.events;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.alfresco.repo.policy.BehaviourDefinition;
import org.alfresco.repo.policy.ClassBehaviourBinding;
import org.alfresco.repo.policy.PolicyComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author steveglover
 *
 */
public abstract class AbstractEventGenerationBehaviours
{
	protected static Log logger = LogFactory.getLog(AbstractEventGenerationBehaviours.class);

	protected Set<String> includeEventTypes;
	protected PolicyComponent policyComponent;

	protected List<BehaviourDefinition<ClassBehaviourBinding>> behaviours = new LinkedList<>();

	protected void addBehaviour(BehaviourDefinition<ClassBehaviourBinding> binding)
	{
		behaviours.add(binding);

		logger.debug("Added policy binding " + binding);
	}
	
	protected void removeBehaviour(BehaviourDefinition<ClassBehaviourBinding> binding)
	{
		removeBehaviourImpl(binding);

		behaviours.remove(binding);
	}

	protected void removeBehaviourImpl(BehaviourDefinition<ClassBehaviourBinding> binding)
	{
		this.policyComponent.removeClassDefinition(binding);

		logger.debug("Removed policy binding " + binding);
	}

	public void cleanUp()
	{
		for(BehaviourDefinition<ClassBehaviourBinding> binding : behaviours)
		{
			removeBehaviourImpl(binding);
		}
	}

	public void setIncludeEventTypes(String includeEventTypesStr)
	{
		StringTokenizer st = new StringTokenizer(includeEventTypesStr, ",");
		this.includeEventTypes = new HashSet<String>();
		while(st.hasMoreTokens())
		{
			String eventType = st.nextToken().trim();
			this.includeEventTypes.add(eventType);
		}
	}

	public void setPolicyComponent(PolicyComponent policyComponent)
	{
		this.policyComponent = policyComponent;
	}
	
	protected boolean includeEventType(String eventType)
	{
		return includeEventTypes.contains(eventType);
	}
}
