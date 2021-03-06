package id;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

public class PropertyIdAssigner 
{
	private String connectionString = "jdbc:mysql://162.105.71.12/dblp_index?useUnicode=true";
	private String userName = "root";
	private String passWord = "123456";
	private java.sql.Connection conn = null;
	private HashSet<String> newProperties = new HashSet();
	private int maxPropertyId = 0;
	private HashMap<String, Integer> propertyIdMap = new HashMap<String, Integer>();
	private FileWriter fw = null;
	private String rdfPath = null;
	private String propertyPath = null;
	
	public PropertyIdAssigner(String rdfPath, String propertyPath)
	{
		this.rdfPath = rdfPath;
		this.propertyPath = propertyPath;
		try {
			fw = new FileWriter("new_property_id.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void readNewProperties(String fileName) throws IOException
	{
		System.out.println("readNewProperties");
		FileReader fr = null;
		fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		int nn = 0;
		while((line = br.readLine()) != null)
		{
			if(++nn % 100000 == 0)
				System.out.print(nn + "\r");
			newProperties.add(line.trim());
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
		String query = "select max(id) from property_id";
		rs = stmt.executeQuery(query);
		while(rs.next())
			maxPropertyId = rs.getInt(1);
		rs.close();
		stmt.close();
	}
	
	private void deDuplicate() throws SQLException, IOException
	{
		System.out.println("deDuplicate");
		java.sql.Statement stmt = conn.createStatement();
		String query = "select id, property from property_id";
		java.sql.ResultSet rs = stmt.executeQuery(query);
		int nn = 0;
		while(rs.next()) 
		{
			if(++nn % 100000 == 0)
				System.out.print(nn + "\r");
		   String URI = rs.getString(2);
		   if(newProperties.contains(URI))
		   {
			   newProperties.remove(URI);
			   int id = rs.getInt(1);
			   fw.write(URI + "\t" + id + "\n");
			   propertyIdMap.put(URI, id);
		   }
		   
		}	
		rs.close();
		stmt.close();
	}
	
	private void insert() throws SQLException, IOException
	{
		System.out.println("insert");
		java.sql.PreparedStatement stmt = conn.prepareStatement("insert into property_id(id, property) values(?, ?)");
		int id = maxPropertyId + 1;
		int nn = 0;
		for(String URI : newProperties)
		{
			if(++nn % 100000 == 0)
				System.out.print(nn + "\r");
			stmt.setInt(1, id);
			stmt.setString(2, URI);
			stmt.execute();
			fw.write(URI + "\t" + id + "\n");
			propertyIdMap.put(URI, id);
			id++;
		} 
		stmt.close(); 
	}
	
	private void replacePropertyByID() throws IOException
	{
		System.out.println("replacePropertyByID");
		FileReader fr = new FileReader(rdfPath);
		BufferedReader br = new BufferedReader(fr);
		FileWriter lfw = new FileWriter("new_dataset2.txt");
		String line = null;
		int nn = 0;
		while((line = br.readLine()) != null)
		{
			if(++nn % 100000 == 0) 
				System.out.print(nn + "\r");
			String tks[] = line.split("\t");
			lfw.write(tks[0] + "\t" + propertyIdMap.get(tks[1]) + "\t" + tks[2] + "\n");
		}
		fr.close();
		br.close();
		lfw.close();
	}
	
	public void assign()
	{
		try 
		{
			this.readNewProperties(propertyPath);
			this.getConnection();
			this.getMaxId();
			this.deDuplicate();
			this.insert(); 
			replacePropertyByID();
			fw.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		PropertyIdAssigner assigner = new PropertyIdAssigner("new_dataset.txt", "D:\\ChineseKnowledgeBase\\chinese_knowledge_base\\pyfiles\\dblp_properties.txt");
		assigner.assign();
		
	}
}
