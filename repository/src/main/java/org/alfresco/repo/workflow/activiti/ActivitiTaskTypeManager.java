/*
 * #%L
 * Alfresco Repository
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.alfresco.repo.workflow.activiti;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.task.Task;
import org.alfresco.repo.workflow.WorkflowObjectFactory;
import org.alfresco.service.cmr.dictionary.TypeDefinition;

/**
 * @author Nick Smith
 * @since 3.4.e
 */
public class ActivitiTaskTypeManager
{
    private final WorkflowObjectFactory factory;

    public ActivitiTaskTypeManager(WorkflowObjectFactory factory)
    {
        this.factory = factory;
    }

    public TypeDefinition getStartTaskDefinition(String taskTypeName) 
    {
        return factory.getTaskFullTypeDefinition(taskTypeName, true);
    }
    
    public TypeDefinition getFullTaskDefinition(Task task)
    {
        return getFullTaskDefinition(task.getId(), task.getFormKey());
    }

    public TypeDefinition getFullTaskDefinition(DelegateTask delegateTask)
    {
        TaskEntity taskEntity = (TaskEntity) delegateTask;
        return getFullTaskDefinition(delegateTask.getId(), taskEntity.getFormKey());
    }
    
    public TypeDefinition getFullTaskDefinition(String typeName)
    {
        return getFullTaskDefinition(typeName, null);
    }
    
    private TypeDefinition getFullTaskDefinition(String taskDefinitionKey, String taskFormDataKey)
    {
        String formKey = null;
        if (taskFormDataKey != null)
        {
            formKey = taskFormDataKey;
        }
        else
        {
            // Revert to task definition key
            formKey = taskDefinitionKey;
        }
        // Since Task instances are never the start-task, it's safe to always be false
        return factory.getTaskFullTypeDefinition(formKey, false);
    }
}
