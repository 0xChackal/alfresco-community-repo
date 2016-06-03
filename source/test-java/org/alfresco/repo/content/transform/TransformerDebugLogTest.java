package org.alfresco.repo.content.transform;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test class for TransformerDebugLog.
 * 
 * @author Alan Davis
 */
public class TransformerDebugLogTest
{
    @Mock
    private TransformerDebug transformerDebug;
    
    @Mock
    private TransformerConfig transformerConfig;
    
    private TransformerDebugLog log;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);
        
        log = new TransformerDebugLog();
        log.setTransformerDebug(transformerDebug);
        log.setTransformerConfig(transformerConfig);
    }

    static void assertDebugEntriesEquals(String[] expected, String[] actual)
    {
        for (int i=actual.length-1; i >= 0; i--)
        {
            // Strip the fist line with the date
            int beginIndex = actual[i].indexOf('\n');
            actual[i] = actual[i].substring(beginIndex+1);
        }
        assertStringArrayEquals(expected, actual);
    }
    
    @Test
    public void noEntriesDisabledTest()
    {
        assertStringArrayEquals(new String[] {"No entries are available. transformer.debug.entries must be set to a number between 1 and 100"}, log.getEntries(10));
    }
    
    private static void assertStringArrayEquals(String[] expected, String[] actual)
    {
        for (int i=0; i<Math.min(expected.length, actual.length); i++)
        {
            assertEquals("path["+i+"]", expected[i].toString(), actual[i].toString());
        }
        assertEquals("Equal lengths", expected.length, actual.length);
    }

    @Test
    public void oneTest()
    {
        when(transformerConfig.getProperty("transformer.debug.entries")).thenReturn("3");
        log.debug("56 one");
        log.debug("56 Finished in 23 ms");
        
        assertDebugEntriesEquals(new String[] {"56 one\n56 Finished in 23 ms"}, log.getEntries(10));
    }

    @Test
    public void incompleteTest()
    {
        when(transformerConfig.getProperty("transformer.debug.entries")).thenReturn("3");
        log.debug("56 one");
        
        assertDebugEntriesEquals(new String[] {"56 one\n             <<-- INCOMPLETE -->>"}, log.getEntries(10));
    }

    @Test
    public void nullEntryTest()
    {
        when(transformerConfig.getProperty("transformer.debug.entries")).thenReturn("3");
        log.debug(null);
        
        assertDebugEntriesEquals(new String[] {}, log.getEntries(10));
    }

    @Test
    public void zeroLengthIdEntryTest()
    {
        when(transformerConfig.getProperty("transformer.debug.entries")).thenReturn("3");
        log.debug("one"); // as the 1st char is not a digit the id is taken to be ""
        
        assertDebugEntriesEquals(new String[] {"one\n             <<-- INCOMPLETE -->>"}, log.getEntries(10));
    }
    
    @Test
    public void twoAndAHalfTest()
    {
        when(transformerConfig.getProperty("transformer.debug.entries")).thenReturn("3");
        log.debug("56 one");
        log.debug("56 Finished in 23 ms");
        log.debug("57 one");
        log.debug("57 two");
        log.debug("57   Finished in 123 ms");
        log.debug("58 one");
        log.debug("58 two");
        
        assertDebugEntriesEquals(new String[]
                {
                 "58 one\n58 two\n             <<-- INCOMPLETE -->>",
                 "57 one\n57 two\n57   Finished in 123 ms",
                 "56 one\n56 Finished in 23 ms"
                 }, log.getEntries(10));
    }
    
    @Test
    public void mixupOrderTest()
    {
        // The sequence will still be based on the first entry of each request,
        // but subsequent debug may be out of order. 
        when(transformerConfig.getProperty("transformer.debug.entries")).thenReturn("3");
        log.debug("56 one");
        log.debug("57 one");
        log.debug("56 Finished in 23 ms");
        log.debug("57 two");
        log.debug("58 one");
        log.debug("57   Finished in 123 ms");
        log.debug("58 two");
        
        assertDebugEntriesEquals(new String[]
                {
                 "58 one\n58 two\n             <<-- INCOMPLETE -->>",
                 "57 one\n57 two\n57   Finished in 123 ms",
                 "56 one\n56 Finished in 23 ms"
                 }, log.getEntries(10));
    }
}
