package org.alfresco.repo.search.impl.lucene;

import java.io.IOException;

import org.alfresco.repo.search.EmptyResultSet;
import org.alfresco.repo.search.SearcherException;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.base.XPathReader;

/**
 * Alfresco FTS Query language support
 * 
 * @author andyh
 */
public class LuceneAlfrescoXPathQueryLanguage extends AbstractLuceneQueryLanguage
{
    public LuceneAlfrescoXPathQueryLanguage()
    {
        this.setName(SearchService.LANGUAGE_XPATH);
    }

    public ResultSet executeQuery(SearchParameters searchParameters, ADMLuceneSearcherImpl admLuceneSearcher)
    {
        try
        {
            XPathReader reader = new XPathReader();
            LuceneXPathHandler handler = new LuceneXPathHandler();
            handler.setNamespacePrefixResolver(admLuceneSearcher.getNamespacePrefixResolver());
            handler.setDictionaryService(admLuceneSearcher.getDictionaryService());
            // TODO: Handler should have the query parameters to use in
            // building its lucene query
            // At the moment xpath style parameters in the PATH
            // expression are not supported.
            reader.setXPathHandler(handler);
            reader.parse(searchParameters.getQuery());
            Query query = handler.getQuery();
            Searcher searcher = admLuceneSearcher.getClosingIndexSearcher();
            if (searcher == null)
            {
                // no index return an empty result set
                return new EmptyResultSet();
            }
            Hits hits = searcher.search(query);
            ResultSet rs = new LuceneResultSet(hits, searcher, admLuceneSearcher.getNodeService(), admLuceneSearcher.getTenantService(), searchParameters, admLuceneSearcher.getLuceneConfig());
            rs = new PagingLuceneResultSet(rs, searchParameters, admLuceneSearcher.getNodeService());
            return rs;
        }
        catch (SAXPathException e)
        {
            throw new SearcherException("Failed to parse query: " + searchParameters.getQuery(), e);
        }
        catch (IOException e)
        {
            throw new SearcherException("IO exception during search", e);
        }
    }
}
