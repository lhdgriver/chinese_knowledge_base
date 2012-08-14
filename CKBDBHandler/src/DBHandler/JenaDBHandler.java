

package DBHandler;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
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
 * ʹ��ǰ������MySQL�н�����Ӧ�����ݿ�(ͨ��SDB�������У������ֶ����word_sentence��)
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
		//��Ҫ��sdb.ttl����CKBDBHandler�ļ����£�������MySQL���˺���������
		store = StoreFactory.create("sdb.ttl");
		ds = DatasetStore.create(store);
		model = ModelFactory.createDefaultModel();
		Class.forName("com.mysql.jdbc.Driver");
		//��MySQL���ݿ����ֺ��˺�����
		conn = (Connection) DriverManager
				.getConnection("jdbc:mysql://localhost/jena_sdb?"
						+ "user=root&password=890911");
	}

	public com.hp.hpl.jena.query.ResultSet execSelect(String query) {
		  QueryExecution qe = QueryExecutionFactory.create(query, ds) ;
		  com.hp.hpl.jena.query.ResultSet rs = null;
	        try {
	            rs = qe.execSelect() ;
	            //for test only
	            ResultSetFormatter.out(rs) ;
	        } finally { qe.close() ; }
		return rs;
	}


	public Model execConstruct(String query) {
		 QueryExecution qe = QueryExecutionFactory.create(query, ds) ;
		 Model m = null;
	        try {
	            m = qe.execConstruct() ;
	        } finally { qe.close() ; }
		return m;
	}

	public Model execDescribe(String query) {
		QueryExecution qe = QueryExecutionFactory.create(query, ds) ;
		 Model m = null;
	        try {
	            m = qe.execDescribe() ;
	        } finally { qe.close() ; }
		return m;
	}

	public boolean execAsk(String query) {
		QueryExecution qe = QueryExecutionFactory.create(query, ds) ;
		 boolean b = false;
	        try {
	            b = qe.execAsk() ;
	        } finally { qe.close() ; }
		return b;
	}

	//for 'o' starts with "@", it is a literal
	//otherwise, it is a resource
    //ͬʱ�������ű�word_sentence
    public boolean insert(String _s, String _p, String _o) throws Exception
    {
    	Resource s = model.createResource(_s);
    	Property p = model.createProperty(_p);
    	if (_o.startsWith("@")) {
    		//literal ��Ҫ���ָ�Ϊ���ʣ�Ȼ��ת��ΪСд��Ȼ����뵹�ű�����ټ���model��
    		String o = _o.substring(1);
    		String sql_o = o.replace("\"", "\\\"");
    		//ͨ���ո�ָ�Ժ���������д�����ӵ�������ʽ
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
    
    //for 'o' starts with "@", it is a literal
  	//otherwise, it is a resource
    //the item you want to search should be null, and only one null is allowed
	public List<RDFNode> selectRDFNode(String _s, String _p, String _o) {
		model = ds.getDefaultModel();
		List<RDFNode> ret = new ArrayList<RDFNode>();
		if (_o == null) {
			Resource s = model.createResource(_s);
			Property p = model.createProperty(_p);
			StmtIterator iter = model.listStatements(s, p, "");
			while(iter.hasNext()) {
				com.hp.hpl.jena.rdf.model.Statement stmt = iter.nextStatement();
				ret.add(stmt.getObject());
			}	
		} else {
			RDFNode o = null;
			if (_o.startsWith("@")) {o = model.createLiteral(_o.substring(1));} 
			else {o = model.createResource(_o);}
			if (_s == null) {
				Property p = model.createProperty(_p);
				StmtIterator iter = model.listStatements(null, p, o);
				while(iter.hasNext()) {
					com.hp.hpl.jena.rdf.model.Statement stmt = iter.nextStatement();
					ret.add(stmt.getSubject());
				}
			} else {
				Resource s = model.createResource(_s);
				StmtIterator iter = model.listStatements(s, null, o);
				while(iter.hasNext()) {
					com.hp.hpl.jena.rdf.model.Statement stmt = iter.nextStatement();
					ret.add(stmt.getPredicate());
				}
			}			
		}
		return ret;
	}

	//ͨ��literal���õ���Ӧ��resource��ע�����literal���ڵ��ű�word_sentence���ҵ�������Ӧ������sentence
	public List<Resource> getResourcebyLiteral(String _literal) throws Exception {
		List<Resource> ret = new ArrayList<Resource>();
		Statement query_stmt = (Statement) conn.createStatement();
		ResultSet rs = query_stmt.executeQuery("select sentence from word_sentence where word = " + _literal.toLowerCase());
		while(rs.next()) {
			Literal literal = model.createLiteral(rs.getString(1));
			StmtIterator iter = model.listStatements(null, null, literal);
			while(iter.hasNext()) {
				com.hp.hpl.jena.rdf.model.Statement stmt =  iter.nextStatement();
				ret.add(stmt.getSubject());
			}
		}
		return ret;
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
