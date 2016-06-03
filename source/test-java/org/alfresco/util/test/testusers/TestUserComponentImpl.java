
package org.alfresco.util.test.testusers;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.util.PropertyMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class TestUserComponentImpl implements TestUserComponent
{
    private static final Log log = LogFactory.getLog(TestUserComponentImpl.class);
    
    protected MutableAuthenticationService authService;
    protected PersonService                personService;
    
    public void setAuthenticationService(MutableAuthenticationService service) { this.authService = service; }
    public void setPersonService(PersonService service)                        { this.personService = service; }
    
    // Fixed defaults for the usual Alfresco cm:person metadata.
    protected static final String PASSWORD   = "PWD";
    protected static final String FIRST_NAME = "firstName";
    protected static final String LAST_NAME  = "lastName";
    protected static final String EMAIL      = "email@email.com";
    protected static final String JOB_TITLE  = "jobTitle";
    
    @Override public NodeRef createTestUser(final String userName)
    {
        // Pre-create a person, if not already created.
        return AuthenticationUtil.runAs(new RunAsWork<NodeRef>()
        {
            @Override public NodeRef doWork() throws Exception
            {
                if (! authService.authenticationExists(userName))
                {
                    log.debug("Creating authentication " + userName + "...");
                    authService.createAuthentication(userName, PASSWORD.toCharArray());
                }
                
                NodeRef person;
                
                if (personService.personExists(userName))
                {
                    person = personService.getPerson(userName, false);
                }
                else
                {
                    log.debug("Creating personNode " + userName + "...");
                    
                    PropertyMap ppOne = new PropertyMap();
                    ppOne.put(ContentModel.PROP_USERNAME,  userName);
                    ppOne.put(ContentModel.PROP_FIRSTNAME, FIRST_NAME);
                    ppOne.put(ContentModel.PROP_LASTNAME,  LAST_NAME);
                    ppOne.put(ContentModel.PROP_EMAIL,     EMAIL);
                    ppOne.put(ContentModel.PROP_JOBTITLE,  JOB_TITLE);
                    
                    person = personService.createPerson(ppOne);
                }
                
                return person;
            }
        }, AuthenticationUtil.getAdminUserName());
    }
    
    @Override public void deleteTestUser(final String userName)
    {
        // And tear down afterwards.
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override public Void doWork() throws Exception
            {
                try
                {
                    if (personService.personExists(userName))
                    {
                        log.debug("Deleting person " + userName + "...");
                        personService.deletePerson(userName);
                    }
                } catch (InvalidNodeRefException ignoreIfThrown)
                {
                    // It seems that in cloud code, asking if a person exists when the tenant they would be in also doesn't
                    // exist, can give this exception.
                }
                return null;
            }
        }, AuthenticationUtil.getAdminUserName());
    }
}
