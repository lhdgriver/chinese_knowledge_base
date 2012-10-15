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
		
		for(ArrayList<Integer> result : resultList)
		{
			ArrayList<String> newResult = new ArrayList<String>();
			for(Integer id : result)
				try {
					for (Statement stmt : dbHandler.getDistOne("" + id))
					{
						String subject = stmt.getSubject().toString();
						String property = stmt.getPredicate().toString();
						String object = stmt.getObject().toString();
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
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
			resultBuffer.append("Result " + i + "\n");
			for(String line : result)
				resultBuffer.append(line + "\n");
			resultBuffer.append("*********************************************************************\n");
		}
		return resultBuffer.toString();
	}
}
