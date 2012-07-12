

package search;

import DBHandler.AbstractDBHandler;
import DBHandler.JenaDBHandler;
import Structure.KBGraph;
import java.util.ArrayList;
import java.util.List;

/**
 * @date 2012-7-12
 * @author lsl
 * @description 
 */
public class TopKSummaryGraphSearch implements AbstractSearch
{
    private static AbstractDBHandler  DBHandler = new JenaDBHandler();
    private String query = null;
    private List<String> keywords = new ArrayList() ;


    private void setQuery(String query)
    {
        this.query = query;
        parseQuery();
    }

    private void parseQuery()
    {
        String[] tks = query.split(".");
        for(int i = 0; i < tks.length; i++)
        {
            keywords.add(tks[i]);
        }
    }

    public KBGraph search(String query)
    {
        setQuery(query);
        return new KBGraph();
    }
}
