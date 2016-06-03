
package org.alfresco.repo.transfer;

import java.io.InputStream;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferProgress;

/**
 * @author brian
 * 
 * The transfer progress monitor monitors each transfer
 * <p>
 * It contains a status, current position, end position, and a log.  
 * It can also store an exception. 
 * 
 */
public interface TransferProgressMonitor
{
    /**
     * log an ad-hoc message
     * @param transferId String
     * @param obj Object
     * @throws TransferException
     */
    void logComment(String transferId, Object obj) throws TransferException;
    /**
     * log an ad-hoc message and an exception
     * @param transferId String
     * @param obj Object
     * @param ex Throwable
     * @throws TransferException
     */
    void logException(String transferId, Object obj, Throwable ex) throws TransferException;
    
    /**
     * Log the creation of a new node
     * @param transferId String
     * @param sourceNode NodeRef
     * @param destNode NodeRef
     * @param newPath String
     * @param orphan boolean
     */
    void logCreated(String transferId, NodeRef sourceNode, NodeRef destNode, NodeRef newParent, String newPath, boolean orphan);
    
    /**
     * Log the creation of a new node
     * @param transferId String
     * @param sourceNode NodeRef
     * @param destNode NodeRef
     * @param path The path of the updated node
     */
    void logUpdated(String transferId, NodeRef sourceNode, NodeRef destNode, String path);
  
    
    /**
     * Log the deletion of a node
     * @param transferId String
     * @param sourceNode NodeRef
     * @param destNode NodeRef
     * @param path The path of the deleted node
     */
    void logDeleted(String transferId, NodeRef sourceNode, NodeRef destNode, String path);
    
    /**
     * After the transfer has completed this method reads the log.
     * @param transferId String
     * @param sourceNodeRef NodeRef
     * @param destNodeRef NodeRef
     * @param oldPath String
     * @param newParent NodeRef
     * @param newPath String
     */
    void logMoved(String transferId, NodeRef sourceNodeRef,
            NodeRef destNodeRef, String oldPath, NodeRef newParent, String newPath);
    
    /**
     * update the progress of the specified transfer 
     * @param transferId String
     * @param currPos int
     * @throws TransferException
     */
    void updateProgress(String transferId, int currPos) throws TransferException;
    
    /** 
     * update the progress of the specified transfer and possibly change the end position.
     * @param transferId String
     * @param currPos int
     * @param endPos int
     * @throws TransferException
     */
    void updateProgress(String transferId, int currPos, int endPos) throws TransferException;
    
    /**
     * update the startus of the transfer
     * @param transferId String
     * @param status TransferProgress.Status
     * @throws TransferException
     */
    void updateStatus(String transferId, TransferProgress.Status status) throws TransferException;
    
    /**
     * Read the progress of the 
     * @param transferId String
     * @return the progress of the transfer
     * @throws TransferException
     */
    TransferProgress getProgress(String transferId) throws TransferException;
    
    InputStream getLogInputStream(String transferId) throws TransferException;
    

}
