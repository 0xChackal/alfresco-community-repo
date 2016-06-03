package org.alfresco.repo.web.scripts.doclink;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.repository.DeleteLinksStatusReport;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the doclink.post webscript doclink.post is a
 * webscript for creating a link of a document within a target destination
 * 
 * @author Ana Bozianu
 * @since 5.1
 */
public class DocLinksDelete extends AbstractDocLink
{

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        NodeRef destinationNodeRef = null;

        /* Parse the template vars */
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        destinationNodeRef = parseNodeRefFromTemplateArgs(templateVars);

        /* Delete links */
        DeleteLinksStatusReport report;
        try
        {
            report = documentLinkService.deleteLinksToDocument(destinationNodeRef);
        }
        catch (IllegalArgumentException ex)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Invalid Arguments: " + ex.getMessage());
        }
        catch (AccessDeniedException e)
        {
            throw new WebScriptException(Status.STATUS_FORBIDDEN, "You don't have permission to perform this operation");
        }

        /* Build response */
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("total_count", report.getTotalLinksFoundCount());
        model.put("deleted_count", report.getDeletedLinksCount());

        Map<String, String> errorDetails = new HashMap<String, String>();
        Iterator<Entry<NodeRef, Throwable>> it = report.getErrorDetails().entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<NodeRef, Throwable> pair = it.next();

            Throwable th = pair.getValue();

            errorDetails.put(pair.getKey().toString(), th.getMessage());
        }

        model.put("error_details", errorDetails);
        

        return model;
    }
}
