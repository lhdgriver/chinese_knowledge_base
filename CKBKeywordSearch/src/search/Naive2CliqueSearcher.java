package search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import DBHandler.AbstractDBHandler;
import DBHandler.JenaDBHandler;

import com.hp.hpl.jena.rdf.model.Resource;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class Naive2CliqueSearcher implements AbstractSearcher, Runnable 
{
	private static JenaDBHandler dbHandler = null;
	private String query = null;
	private String[] keywords = null;
	private HashMap<String, HashSet<Integer>> keywordEntitiesDic = new HashMap();
	private HashMap<Integer, HashSet<Integer>> entityNeighboursDic= new HashMap();
	private boolean mustStop = false;
	
	static
	{
		try {
			dbHandler = new JenaDBHandler();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void setQuery(String query) 
	{
		this.query = query;
		keywords = query.split(" ");
	}

	@Override
	public void run() 
	{
		if(dbHandler == null)
			return;
		if(keywords.length == 0)
			return;
		for (String keyword : keywords) 
		{
			if(keywordEntitiesDic.containsKey(keyword))
				continue;
			keywordEntitiesDic.put(keyword, new HashSet<Integer>());
			//for(Resource resource : dbHandler.getResourcebyLiteral(keyword))
			{
				//Integer id = Integer.parseInt(resource.getLocalName()); 
				//keywordEntitiesDic.get(keyword).add(id);
				//if(entityNeighboursDic.containsKey(id)) 
				//	continue;
				//entityNeighboursDic.put(id, new HashSet<Integer>());
				//for(Resource neighbour : dbHandler.getNeighbours(resource))
					//entityNeighboursDic.get(id).add(Integer.parseInt(neighbour.getLocalName()));
			}
		}
		
		ArrayList<ArrayList<Integer>> resultList = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> result = new ArrayList<Integer>();
		getResult(0, result, resultList);
	}

	private void getResult(int i, ArrayList<Integer> result, ArrayList<ArrayList<Integer>> resultList) 
	{
		if(i == keywords.length)
		{
			if(isValid(result))
			{
				ArrayList<Integer> newResult = new ArrayList<Integer>();
				for(Integer integer : result)
					newResult.add(i);
				resultList.add(newResult);
			}
			return;
		}
		for(Integer e : keywordEntitiesDic.get(keywords[i]))
		{
			result.add(e);
			getResult(i+1, result, resultList);
			result.remove(result.size()-1);
		}
	}

	private boolean isValid(ArrayList<Integer> result) 
	{
		HashSet<Integer> resultSet = new HashSet<Integer>();
		HashSet<Integer> neighbours = new HashSet<Integer>();
		for(Integer id: result)
			resultSet.add(id);
		for(Integer id: resultSet)
		{	
			for(Integer ne : entityNeighboursDic.get(id))
					neighbours.add(ne);
			resultSet.remove(id);
		}
		while(resultSet.size() > 0)
		{
			boolean isConnected = false;
			for(Integer id: resultSet)
			{
				for(Integer ne : entityNeighboursDic.get(id))
					if(neighbours.contains(ne))
					{
						isConnected = true;
						break;
					}
				if(isConnected)
				{
					resultSet.remove(id);
					for(Integer ne : entityNeighboursDic.get(id))
						neighbours.add(ne);
					break;
				}
			}
			if(!isConnected)
				return false;
		}
		return true;
	}

	@Override
	public void clearAndStop() 
	{
		mustStop = true;
	}
	
}
