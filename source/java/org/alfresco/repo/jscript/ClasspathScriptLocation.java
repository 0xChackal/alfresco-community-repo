package org.alfresco.repo.jscript;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.cmr.repository.ScriptLocation;
import org.springframework.extensions.surf.util.ParameterCheck;

/**
 * Classpath script location object.
 * 
 * @author Roy Wetherall
 */
public class ClasspathScriptLocation implements ScriptLocation 
{
	/** Classpath location **/
	private final String location;
	
	/**
	 * Constructor
	 * 
	 * @param location	the classpath location
	 */
	public ClasspathScriptLocation(String location)
	{
		ParameterCheck.mandatory("Location", location);
		this.location = location;
	}
    
    /**
     * @see org.alfresco.service.cmr.repository.ScriptLocation#getInputStream()
     */
    public InputStream getInputStream()
    {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(location);
        if (stream == null)
        {
            throw new AlfrescoRuntimeException("Unable to load classpath resource: " + location);
        }
        return stream;
    }

	/**
	 * @see org.alfresco.service.cmr.repository.ScriptLocation#getReader()
	 */
	public Reader getReader() 
	{
		Reader reader = null;
        try
        {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(location);
            if (stream == null)
            {
                throw new AlfrescoRuntimeException("Unable to load classpath resource: " + location);
            }
            reader = new InputStreamReader(stream);
        }
        catch (Throwable err)
        {
            throw new ScriptException("Failed to load classpath resource '" + location + "': " + err.getMessage(), err);
        }
        
        return reader;
	}
    
	/**
     * @see org.alfresco.service.cmr.repository.ScriptLocation#getPath()
     */
    public String getPath()
    {
        return this.location;
    }
    
    public boolean isCachable()
    {
        return true;
    }

    public boolean isSecure()
    {
        return true;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj == null || !(obj instanceof ClasspathScriptLocation))
        {
            return false;
        }
        ClasspathScriptLocation other = (ClasspathScriptLocation)obj;
        return this.location.equals(other.location);
    }

    @Override
    public int hashCode()
    {
        return 37 * this.location.hashCode();
    }

    @Override
    public String toString()
    {
        return this.location.toString();
    }
}
