

package DBHandler;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.store.DatasetStore;
import com.hp.hpl.jena.sdb.store.StoreFactory;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

/**
 * @date 2012-7-12
 * @author lsl
 * @description 
 * 使用前得先在MySQL中建立相应的数据库(通过SDB的命令行，并且手动添加word_sentence表)
 */
public class JenaDBHandler implements AbstractDBHandler
{
	private Store store = null;
	private Model model = null;
	private Dataset ds = null;
	private Connection conn = null;
	private ArrayList<String> word_arr = new ArrayList<String>();
	private ArrayList<String> sentence_arr = new ArrayList<String>();

	public JenaDBHandler() throws Exception
	{
		//需要将sdb.ttl放在CKBDBHandler文件夹下，里面有MySQL的账号密码设置
		store = StoreFactory.create("sdb.ttl");
		ds = DatasetStore.create(store);
		model = ModelFactory.createDefaultModel();
		Class.forName("com.mysql.jdbc.Driver");
		//改MySQL数据库名字和账号密码
		conn = (Connection) DriverManager
				.getConnection("jdbc:mysql://localhost/jena_sdb?"
						+ "user=root&password=890911");
	}

    public List<List<Object>> excuteQuery(String command)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean excute(String command)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
	//for 'o' starts with "@", it is a literal
	//otherwise, it is a resource
    //同时建立倒排表word_sentence
    public boolean insert(String _s, String _p, String _o) throws Exception
    {
    	Resource s = model.createResource(_s);
    	Property p = model.createProperty(_p);
    	if (_o.startsWith("@")) {
    		//literal 需要被分割为单词，然后转化为小写，然后存入倒排表，最后再加入model中
    		String o = _o.substring(1);
    		String sql_o = o.replace("\"", "\\\"");
    		//通过空格分割，以后有需求再写更复杂的正则表达式
    		String[] words = sql_o.split("\\s+");
    		for (int i = 0; i < words.length; i++) {
    			word_arr.add(words[i]);
    			sentence_arr.add(sql_o);
    		}
    		if (word_arr.size() >= 10000) {
    			Statement stmt = (Statement) conn.createStatement();
    			StringBuffer temp_query = new StringBuffer();
    			temp_query.append("insert into word_sentence (word, sentence) values ");
    			for (int i = 0; i < word_arr.size(); i++) {
    				temp_query.append("(\""+word_arr.get(i) + "\",\"" + sentence_arr.get(i) +"\"),");
    			}
    			String query = temp_query.substring(0, temp_query.length() - 1);
    			stmt.execute(query);
    			word_arr.clear();
    			sentence_arr.clear();
    		}
    		s.addProperty(p, model.createLiteral(o));
    	}
    	else {
    		//Resource
    		Resource o = model.createResource(_o);
    		s.addProperty(p, o);
    	}
        return true;
    }

	public List<String> selectID(String s, String p, String o) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Resource> getResourcebyLiteral(String literal) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean close() throws Exception {
		//store the rest words and sentences into db
		if (word_arr.size() > 0)
		{	
			Statement stmt = (Statement) conn.createStatement();
			StringBuffer temp_query = new StringBuffer();
			temp_query.append("insert into word_sentence (word, sentence) values ");
			for (int i = 0; i < word_arr.size(); i++) {
				temp_query.append("(\""+word_arr.get(i) + "\",\"" + sentence_arr.get(i) +"\"),");
			}
			String query = temp_query.substring(0, temp_query.length() - 1);
			stmt.execute(query);
		}
		
		//close MySQL connection
		conn.close();
		//store model to MySQL
		ds.getDefaultModel().add(model);
		//close SDB connection
		store.getConnection().close();
		store.close();
		return true;
	}

}
