package id;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.org.apache.bcel.internal.generic.Select;
import components.CKBLogger;

public class IdIndexStore 
{
	private String connectionString = "jdbc:mysql://162.105.71.12/dblp_index?useUnicode=true";
	private String userName = "root";
	private String passWord = "123456";
	private java.sql.Connection conn = null;
	public static IdIndexStore instance = new IdIndexStore();
	
	private IdIndexStore()
	{
		try 
		{
			getConnection();
		} 
		catch (Exception e) 
		{
			CKBLogger.log(e.getMessage());
		}
	}
	
	private void getConnection() throws ClassNotFoundException, SQLException
	{
		System.out.println("getConnection");
		Class.forName("com.mysql.jdbc.Driver");
		conn = java.sql.DriverManager.getConnection(connectionString, userName, passWord);
	}
	
	public HashMap<Integer, String> getNameByIdSet(ArrayList<Integer> ids)
	{
		HashMap<Integer, String> idNameDic = new HashMap<Integer, String>();
		StringBuffer query = new StringBuffer("Select id, URI from entity_index where id in (");
		for(Integer id : ids)
			query.append(id).append(",");
		query.setCharAt(query.length() - 1 , ')');
		query.append(";");
		java.sql.Statement stmt;
		try 
		{
			stmt = conn.createStatement();
			java.sql.ResultSet rs = stmt.executeQuery(query.toString());
			while(rs.next()) 
			{
				Integer id = rs.getInt(1);
				String URI = rs.getString(2);
				idNameDic.put(id, URI);
			}	
		} 
		catch (Exception e) 
		{
			CKBLogger.log(e.getMessage());
		}
		
		return idNameDic; 
	}
	
}
