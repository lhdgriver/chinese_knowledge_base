package result;

import id.IdIndexStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import DBHandler.JenaDBHandler;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;


public class NormalResult 
{
	private ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
	private static IdIndexStore indexStore = IdIndexStore.instance;
	
	public void format(ArrayList<ArrayList<Integer>> resultList, JenaDBHandler dbHandler)
	{
		HashSet<String> visitedStmt = new HashSet<String>();
		ArrayList<String> resultStmts = new ArrayList<String>(); 
		HashSet<Integer> URISet = new HashSet<Integer>();
		HashSet<Integer> propertySet = new HashSet<Integer>();
		
		ArrayList<Statement> neighborStmts = new ArrayList<Statement>();
		HashSet<String> neighbors = new HashSet<String>();
		HashSet<String> commonNeighbors = new HashSet<String>();
		
		for(ArrayList<Integer> result : resultList)
		{
			for(Integer id : result)
				neighbors.add(""+id);
			for(Integer id : result)
				try 
				{
					for (Statement stmt : dbHandler.getDistOne("" + id))
					{
						String subject = stmt.getSubject().toString();
						String object = stmt.getObject().toString();
						if(neighbors.contains(subject))
							commonNeighbors.add(subject);
						neighbors.add(subject);
						if(stmt.getObject().isResource())
						{	
							if(neighbors.contains(object))
								commonNeighbors.add(object);
							neighbors.add(object);
						}
						neighborStmts.add(stmt);
					}
				} 
				catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			ArrayList<String> newResult = new ArrayList<String>();
			for(Statement stmt : neighborStmts)
			{
				String subject = stmt.getSubject().toString();
				String object = stmt.getObject().toString();
				if(!commonNeighbors.contains(subject) || (!commonNeighbors.contains(object) && stmt.getObject().isResource()))
					continue;
				String property = stmt.getPredicate().toString();
				if(!stmt.getObject().isResource())
						object = "\"" + object + "\"";
				String stmtString = subject + "\t" + property + "\t" + object;
				if(visitedStmt.contains(stmtString)) continue;
				visitedStmt.add(stmtString);
				resultStmts.add(stmtString);
				URISet.add(Integer.parseInt(subject));
				if(stmt.getObject().isResource())
				URISet.add(Integer.parseInt(object));
				propertySet.add(Integer.parseInt(property));
			}
		
			
			HashMap<Integer, String> URIMap = indexStore.getURIByIdSet(URISet);
			HashMap<Integer, String> propertyMap = indexStore.getPerprotyByIdSet(propertySet);
			for(String stmt: resultStmts)
			{
				String tks[] = stmt.split("\t");
				StringBuffer resultBuffer = new StringBuffer();
				resultBuffer.append(URIMap.get(Integer.parseInt(tks[0])));
				//resultBuffer.append(tks[0]);
				resultBuffer.append("-----");
				resultBuffer.append(propertyMap.get(Integer.parseInt(tks[1])));
				resultBuffer.append("-----");
				if(tks[2].startsWith("\""))
					resultBuffer.append(tks[2]);
				else
					resultBuffer.append(URIMap.get(Integer.parseInt(tks[2])));
					//resultBuffer.append(tks[2]);
				newResult.add(resultBuffer.toString());
			}
			results.add(newResult);
			visitedStmt.clear();
			resultStmts.clear();
			URISet.clear();
			propertySet.clear();
			neighbors.clear();
			commonNeighbors.clear();
			neighborStmts.clear();
		}
	}
	
	@Override
	public String toString() 
	{
		StringBuffer resultBuffer = new StringBuffer();
		int i = 1;
		for(ArrayList<String> result : results)
		{
			resultBuffer.append("*********************************************************************\n");
			resultBuffer.append("Result " + i++ + "\n");
			for(String line : result)
				resultBuffer.append(line + "\n");
			resultBuffer.append("*********************************************************************\n");
		}
		return resultBuffer.toString();
	}
}
