

package search;

import DBHandler.AbstractDBHandler;
import DBHandler.JenaDBHandler;
import Structure.KBGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    //hash or list ---> tolerant with duplicated keywords
    private Map<String, List<String>> keyLiterals = new HashMap();
    private Map<String, List<String>> literalElements = new HashMap();
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
    // main entry
    public KBGraph search(String query) throws IOException, ClassNotFoundException
    {
        setQuery(query);
        //Element Mapping
        for(int i = 0; i < keywords.size(); i++)
        {
        	List<String> literals = DBHandler.getLiteralbyKeyword(keywords.get(i));
        	keyLiterals.put(keywords.get(i), literals);
        	for(String literal : literals)
        		literalElements.put(literal, DBHandler.getElementIDbyLiteral(literal));
        	
        }
        //Augmented Graph
        KBGraph augGraph = KBGraph.deserialize(" ");
        Map<String, HashSet<String>> literalTypes = new HashMap();
        for(Map.Entry<String, List<String>> pair : literalElements.entrySet())
        {
        	String literal = pair.getKey();
        	HashSet<String> types = new HashSet();
        	for(String element : pair.getValue())
        	{
        		///////////////////
        		//how about no type
        		///////////////////
        		types.add(DBHandler.select(literal, "type", "").get(0));
        	}
        	literalTypes.put(literal, types);
        }
        for(Map.Entry<String, HashSet<String>> pair : literalTypes.entrySet())
        {
        	String literal = pair.getKey();
        	for(String type : pair.getValue())
        		augGraph.addEdge(literal, type, "unname");
        }
        
        return new KBGraph();
    }
    
    
}
