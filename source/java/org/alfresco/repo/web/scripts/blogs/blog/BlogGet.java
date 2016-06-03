package org.alfresco.repo.web.scripts.blogs.blog;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.web.scripts.blogs.AbstractBlogWebScript;
import org.alfresco.service.cmr.blog.BlogPostInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteInfo;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the blog.get web script.
 * 
 * @author Neil Mc Erlean (based on existing JavaScript webscript controllers)
 * @since 4.0
 */
public class BlogGet extends AbstractBlogWebScript
{
    @Override
    protected Map<String, Object> executeImpl(SiteInfo site, NodeRef containerNodeRef,
         BlogPostInfo blog, WebScriptRequest req, JSONObject json, Status status, Cache cache) 
    {
       if (blog != null)
       {
          // They appear to have supplied a blog post itself...
          // Oh well, let's hope for the best!
       }
          
       if (containerNodeRef == null && site != null)
       {
          // They want to know about a blog container on a
          //  site where nothing has lazy-created the container
          // Give them info on the site for now, should be close enough!
          containerNodeRef = site.getNodeRef();
       }
       
       if (containerNodeRef == null)
       {
          // Looks like they've asked for something that isn't there 
          throw new WebScriptException(Status.STATUS_NOT_FOUND, "Blog Not Found");
       }

       // Build the response
       // (For now, we just supply the noderef, but when we have a 
       //  proper blog details object we'll use that)
       Map<String, Object> model = new HashMap<String, Object>();
       model.put(ITEM, containerNodeRef);

       return model;
    }
}