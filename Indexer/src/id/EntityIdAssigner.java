package id;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

import com.mysql.jdbc.Connection;

public class EntityIdAssigner 
{
	private String connectionString = "jdbc:mysql://162.105.71.12/dblp_index?useUnicode=true";
	private String userName = "root";
	private String passWord = "123456";
	private java.sql.Connection conn = null;
	private HashSet<String> newEntities = new HashSet();
	private int maxEntityId = 0;
	private HashMap<String, Integer> entityIdMap = new HashMap<String, Integer>();
	private FileWriter fw = null;
	private String rdfPath = null;
	private String entityPath = null;
	
	public EntityIdAssigner(String rdfPath, String entityPath)
	{
		this.rdfPath = rdfPath;
		this.entityPath = entityPath;
		try {
			fw = new FileWriter("new_entity_id.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void readNewEntities(String fileName) throws IOException
	{
		System.out.println("readNewEntities");
		FileReader fr = null;
		fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		int nn = 0;
		while((line = br.readLine()) != null)
		{
			if(++nn % 100000 == 0)
				System.out.print(nn + "\r");
			newEntities.add(line.trim());
		}
		fr.close();
		br.close();
	}
	
	private void getConnection() throws ClassNotFoundException, SQLException
	{
		System.out.println("getConnection");
		Class.forName("com.mysql.jdbc.Driver");
		conn = java.sql.DriverManager.getConnection(connectionString, userName, passWord);
	}
	
	private void getMaxId() throws SQLException
	{
		System.out.println("getMaxId");
		java.sql.ResultSet rs = null;
		java.sql.Statement stmt = conn.createStatement();
		String query = "select max(id) from entity_id";
		rs = stmt.executeQuery(query);
		while(rs.next())
			maxEntityId = rs.getInt(1);
		rs.close();
		stmt.close();
	}
	
	private void deDuplicate() throws SQLException, IOException
	{
		System.out.println("deDuplicate");
		java.sql.Statement stmt = conn.createStatement();
		String query = "select id, URI from entity_id";
		java.sql.ResultSet rs = stmt.executeQuery(query);
		int nn = 0;
		while(rs.next()) 
		{
			if(++nn % 100000 == 0)
				System.out.print(nn + "\r");
		   String URI = rs.getString(2);
		   if(newEntities.contains(URI))
		   {
			   newEntities.remove(URI);
			   int id = rs.getInt(1);
			   fw.write(URI + "\t" + id + "\n");
			   entityIdMap.put(URI, id);
		   }
		   
		}	
		rs.close();
		stmt.close();
	}
	
	private void insert() throws SQLException, IOException
	{
		System.out.println("insert");
		java.sql.PreparedStatement stmt = conn.prepareStatement("insert into entity_id(id, URI) values(?, ?)");
		int id = maxEntityId + 1;
		int nn = 0;
		for(String URI : newEntities)
		{
			if(++nn % 100000 == 0)
				System.out.print(nn + "\r");
			stmt.setInt(1, id);
			stmt.setString(2, URI);
			stmt.execute();
			fw.write(URI + "\t" + id + "\n");
			entityIdMap.put(URI, id);
			id++;
		} 
		stmt.close(); 
	}
	
	private void replaceURIByID() throws IOException
	{
		System.out.println("replaceURIByID");
		FileReader fr = new FileReader(rdfPath);
		BufferedReader br = new BufferedReader(fr);
		FileWriter lfw = new FileWriter("new_dataset.txt");
		String line = null;
		int nn = 0;
		while((line = br.readLine()) != null)
		{
			if(++nn % 100000 == 0) 
				System.out.print(nn + "\r");
			String tks[] = line.split("\t");
			String s = entityIdMap.containsKey(tks[0])? entityIdMap.get(tks[0]).toString(): tks[0];
			String o = entityIdMap.containsKey(tks[2])? entityIdMap.get(tks[2]).toString(): tks[2];
			lfw.write(s+ "\t" + tks[1] + "\t" + o + "\n");
		}
		fr.close();
		br.close();
		lfw.close();
	}
	
	public void assign()
	{
		try 
		{
			this.readNewEntities(entityPath);
			this.getConnection();
			this.getMaxId();
			this.deDuplicate();
			this.insert(); 
			replaceURIByID();
			fw.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		EntityIdAssigner assigner = new EntityIdAssigner("D:\\ChineseKnowledgeBase\\chinese_knowledge_base\\pyfiles\\dblp.rdf", "D:\\ChineseKnowledgeBase\\chinese_knowledge_base\\pyfiles\\dblp_entities.txt");
		assigner.assign();
		
	}
}
