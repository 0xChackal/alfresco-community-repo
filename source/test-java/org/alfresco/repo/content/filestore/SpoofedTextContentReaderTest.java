package org.alfresco.repo.content.filestore;

import java.io.InputStream;
import java.util.Locale;

import junit.framework.TestCase;

import org.alfresco.repo.content.AbstractContentReader;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.test_category.OwnJVMTestsCategory;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.springframework.util.FileCopyUtils;

/**
 * Text spoofing as a {@link ContentReader}
 * 
 * @see SpoofedTextContentReader
 * 
 * @author Derek Hulley
 * @since 5.1
 */
@Category(OwnJVMTestsCategory.class)
public class SpoofedTextContentReaderTest extends TestCase
{
    @Before
    public void before()
    {
        // Nothing
    }
    
    public void testStaticUrlHandlingErr()
    {
        try
        {
            SpoofedTextContentReader.createContentUrl(null, 12345L, 1024L);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
        try
        {
            SpoofedTextContentReader.createContentUrl(Locale.ENGLISH, 12345L, -1L);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
        try
        {
            SpoofedTextContentReader.createContentUrl(Locale.ENGLISH, 12345L, 1024L, (String) null);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
        try
        {
            SpoofedTextContentReader.createContentUrl(Locale.FRENCH, 12345L, 1024L);
            fail();
        }
        catch (RuntimeException e)
        {
            // Expected
        }
        try
        {
            SpoofedTextContentReader.createContentUrl(
                    Locale.ENGLISH, 12345L, 1024L,
                    "1234567890ABCDEFGHIJ", "1234567890ABCDEFGHIJ", "1234567890ABCDEFGHIJ", "1234567890ABCDEFGHIJ", "1234567890ABCDEFGHIJ",
                    "1234567890ABCDEFGHIJ", "1234567890ABCDEFGHIJ", "1234567890ABCDEFGHIJ", "1234567890ABCDEFGHIJ", "1234567890ABCDEFGHIJ",
                    "1234567890ABCDEFGHIJ", "1234567890ABCDEFGHIJ", "1234567890ABCDEFGHIJ", "1234567890ABCDEFGHIJ", "1234567890ABCDEFGHIJ");
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }
    
    public void testStaticUrlForm_01()
    {
        // To URL
        String url = SpoofedTextContentReader.createContentUrl(Locale.ENGLISH, 12345L, 1024L, "harry");
        assertTrue(url.startsWith("spoof://{"));
        assertTrue(url.contains("\"locale\":\"en\""));
        assertTrue(url.contains("\"seed\":\"12345\""));
        assertTrue(url.contains("\"size\":\"1024\""));
        assertTrue(url.contains("\"words\":[\"harry\"]"));
        assertTrue(url.endsWith("}"));
        // From Reader
        SpoofedTextContentReader reader = new SpoofedTextContentReader(url);
        assertNotNull(reader.getTextGenerator());
        assertEquals(Locale.ENGLISH, reader.getLocale());
        assertEquals(MimetypeMap.MIMETYPE_TEXT_PLAIN, reader.getMimetype());
        assertEquals("UTF-8", reader.getEncoding());
        assertEquals(12345L, reader.getSeed());
        assertEquals(1024L, reader.getSize());
        assertNotNull(reader.getWords());
        assertEquals(1, reader.getWords().length);
        assertEquals("harry", reader.getWords()[0]);
    }
    
    public void testStaticUrlForm_02()
    {
        // To URL
        String url = SpoofedTextContentReader.createContentUrl(Locale.ENGLISH, 12345L, 1024L);
        assertTrue(url.startsWith("spoof://{"));
        assertTrue(url.contains("\"locale\":\"en\""));
        assertTrue(url.contains("\"seed\":\"12345\""));
        assertTrue(url.contains("\"size\":\"1024\""));
        assertTrue(url.contains("\"words\":[]"));
        assertTrue(url.endsWith("}"));
        // From Reader
        SpoofedTextContentReader reader = new SpoofedTextContentReader(url);
        assertNotNull(reader.getTextGenerator());
        assertEquals(Locale.ENGLISH, reader.getLocale());
        assertEquals(MimetypeMap.MIMETYPE_TEXT_PLAIN, reader.getMimetype());
        assertEquals("UTF-8", reader.getEncoding());
        assertEquals(12345L, reader.getSeed());
        assertEquals(1024L, reader.getSize());
        assertNotNull(reader.getWords());
        assertEquals(0, reader.getWords().length);
    }
    
    public void testGetContentString_01()
    {
        // To URL
        String url = SpoofedTextContentReader.createContentUrl(Locale.ENGLISH, 12345L, 56L, "harry");
        // To Reader
        ContentReader reader = new SpoofedTextContentReader(url);
        String readerText = reader.getContentString();
        assertEquals("harry have voice the from countered growth invited      ", readerText);
        // Cannot repeat
        try
        {
            reader.getContentString();
            fail("Should not be able to reread content.");
        }
        catch (ContentIOException e)
        {
            // Expected
        }
        // Get a new Reader
        reader = reader.getReader();
        // Get exactly the same text
        assertEquals(readerText, reader.getContentString());
    }
    
    public void testGetContentBinary_01() throws Exception
    {
        // To URL
        String url = SpoofedTextContentReader.createContentUrl(Locale.ENGLISH, 12345L, 56L, "harry");
        // To Reader
        ContentReader reader = new SpoofedTextContentReader(url);
        InputStream is = reader.getContentInputStream();
        try
        {
            byte[] bytes = FileCopyUtils.copyToByteArray(is);
            assertEquals(56L, bytes.length);
        }
        finally
        {
            is.close();
        }
        // Compare readers
        ContentReader copyOne = reader.getReader();
        ContentReader copyTwo = reader.getReader();
        // Get exactly the same binaries
        assertTrue(AbstractContentReader.compareContentReaders(copyOne, copyTwo));
    }
}
