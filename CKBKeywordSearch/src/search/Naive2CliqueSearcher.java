package search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import result.NormalResult;

import DBHandler.AbstractDBHandler;
import DBHandler.JenaDBHandler;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class Naive2CliqueSearcher implements AbstractSearcher, Runnable 
{
	private static JenaDBHandler dbHandler = null;
	private String query = null;
	private String[] keywords = null;
	private HashMap<String, HashSet<Integer>> keywordEntitiesDic = new HashMap();
	private HashMap<Integer, HashSet<Integer>> entityNeighboursDic= new HashMap();
	private boolean mustStop = false;
	public String result = null;
	
	private static HashSet<Integer> typeId = new HashSet<Integer>();
	
	static
	{
		try {
			dbHandler = new JenaDBHandler();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		typeId.add(338387);
		typeId.add(439086);
		typeId.add(993989);
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
			try {
				for(Resource resource : dbHandler.getResourcebyLiteral(keyword))
				{
					Integer id = Integer.parseInt(resource.toString()); 
					keywordEntitiesDic.get(keyword).add(id);
					if(entityNeighboursDic.containsKey(id)) 
						continue;
					entityNeighboursDic.put(id, new HashSet<Integer>());
					entityNeighboursDic.get(id).add(id);
					for(Resource neighbour : dbHandler.getDistOneResources(resource))
					{
						int neighbourID = Integer.parseInt(neighbour.toString());
						if(!typeId.contains(neighbourID))
							entityNeighboursDic.get(id).add(neighbourID);
					}
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ArrayList<ArrayList<Integer>> resultList = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> result = new ArrayList<Integer>();
		getResult(0, result, resultList);
		//System.out.print(resultList.size());
		NormalResult ret = new NormalResult();
		ret.format(resultList, dbHandler);
		this.result = ret.toString();
	}

	private void getResult(int i, ArrayList<Integer> result, ArrayList<ArrayList<Integer>> resultList) 
	{
		if(i == keywords.length)
		{
			if(isValid(result))
			{
				ArrayList<Integer> newResult = new ArrayList<Integer>();
				for(Integer integer : result)
					newResult.add(integer);
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
			break;
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
