
package org.alfresco.repo.web.scripts.transfer;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferProgress;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.WrappingWebScriptRequest;
import org.springframework.extensions.webscripts.json.JSONWriter;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

/**
 * This command processor is used to get the server side transfer report.
 * 
 * @author brian
 * 
 */
public class ReportCommandProcessor implements CommandProcessor
{
    private static final String MSG_CAUGHT_UNEXPECTED_EXCEPTION = "transfer_service.receiver.caught_unexpected_exception";

    private TransferReceiver receiver;

    private final static Log logger = LogFactory.getLog(ReportCommandProcessor.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.web.scripts.transfer.CommandProcessor#process(org.alfresco .web.scripts.WebScriptRequest,
     * org.alfresco.web.scripts.WebScriptResponse)
     */
    public int process(WebScriptRequest req, WebScriptResponse resp)
    {   
        //Read the transfer id from the request
        // Unwrap to a WebScriptServletRequest if we have one
        WebScriptServletRequest webScriptServletRequest = null;
        WebScriptRequest current = req;
        do
        {
            if (current instanceof WebScriptServletRequest)
            {
                webScriptServletRequest = (WebScriptServletRequest) current;
                current = null;
            }
            else if (current instanceof WrappingWebScriptRequest)
            {
                current = ((WrappingWebScriptRequest) req).getNext();
            }
            else
            {
                current = null;
            }
        }
        while (current != null);
        HttpServletRequest servletRequest = webScriptServletRequest.getHttpServletRequest();
        String transferId = servletRequest.getParameter("transferId");

        if (transferId == null) 
        {
            logger.debug("transferId is missing");
            resp.setStatus(Status.STATUS_BAD_REQUEST);
            return Status.STATUS_BAD_REQUEST;
        }
        
        try
        {
            OutputStream out = resp.getOutputStream();
            try
            {
                resp.setContentType("text/xml");
                resp.setContentEncoding("utf-8");
            
                BufferedInputStream br = new BufferedInputStream(receiver.getProgressMonitor().getLogInputStream(transferId));
                try
                {
                    byte[] buffer = new byte[1000];
                    int i = br.read(buffer);
                    while(i > 0)
                    {
                        out.write(buffer, 0, i);
                        i = br.read(buffer);
                    }
                }
                finally
                {
                    br.close();
                }
            }
            finally
            {
                out.flush();
                out.close();
            }
         
            return Status.STATUS_OK;
        }
        catch (TransferException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new TransferException(MSG_CAUGHT_UNEXPECTED_EXCEPTION, ex);
        }
    }

    /**
     * @param receiver
     *            the receiver to set
     */
    public void setReceiver(TransferReceiver receiver)
    {
        this.receiver = receiver;
    }

}
