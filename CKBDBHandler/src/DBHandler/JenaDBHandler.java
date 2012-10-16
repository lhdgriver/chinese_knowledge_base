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
 * @author lh
 * @description 
 */
public class JenaDBHandler implements AbstractDBHandler
{
	private Store store = null;
	private Model model = null;
	private Dataset ds = null;
	private Connection conn = null;
	private ArrayList<String> word_arr = new ArrayList<String>();
	private ArrayList<Integer> entity_id_arr = new ArrayList<Integer>();
	
	public JenaDBHandler() throws Exception
	{
		store = StoreFactory.create("sdb.ttl");
		ds = DatasetStore.create(store);
		model = ModelFactory.createDefaultModel();
		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager
				.getConnection("jdbc:mysql://162.105.71.12/new_sdb?"
						+ "user=root&password=123456");
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
    
    public boolean insert(String _s, String _p, String _o) throws Exception
    {
    	Resource s = model.getResource(_s);
    	Property p = model.getProperty(_p);
    	if (_o.startsWith("@")) {
    		String o = _o.substring(1);
    		o = o.replace("\\", "\\\\");
    		String sql_o = o.replace("\"", "\\\"").replace("\'", "\\\'");
    		int entity_id = Integer.valueOf(_s);
    		String[] words = sql_o.split("\\s+");
    		for (int i = 0; i < words.length; i++) {
    			word_arr.add(words[i]);
    			entity_id_arr.add(entity_id);
    		}
    		if (word_arr.size() >= 5000) {
    			Statement stmt = (Statement) conn.createStatement();
    			StringBuffer temp_query = new StringBuffer();
    			temp_query.append("insert into dblp_index.word_entity_id (word, entity_id) values ");
    			for (int i = 0; i < word_arr.size(); i++) {
    				temp_query.append("(\""+word_arr.get(i) + "\",\"" + entity_id_arr.get(i) +"\"),");
    			}
    			String query = temp_query.substring(0, temp_query.length() - 1);
    			stmt.execute(query);
    			word_arr.clear();
    			entity_id_arr.clear();
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

	public List<Resource> getResourcebyLiteral(String _literal) throws Exception {
		model = ds.getDefaultModel();
		Resource r = null;
		List<Resource> ret = new ArrayList<Resource>();
		Statement query_stmt = (Statement) conn.createStatement();
		ResultSet rs = query_stmt.executeQuery("select distinct(entity_id) from dblp_index.word_entity_id where word = \"" + _literal.toLowerCase()+"\"");
		while(rs.next()) {
			System.out.println(rs.getString(1));
			r = model.getResource(rs.getString(1));
			ret.add(r);
			
			
			//getDistOne(r);
			
			
		}
		return ret;
	}

	public boolean close() throws Exception {
		//store the rest words and sentences into db
		if (word_arr.size() > 0)
		{	
			Statement stmt = (Statement) conn.createStatement();
			StringBuffer temp_query = new StringBuffer();
			temp_query.append("insert into dblp_index.word_entity_id (word, entity_id) values ");
			for (int i = 0; i < word_arr.size(); i++) {
				temp_query.append("(\""+word_arr.get(i) + "\",\"" + entity_id_arr.get(i) +"\"),");
			}
			String query = temp_query.substring(0, temp_query.length() - 1);
			stmt.execute(query);
		}
		word_arr.clear();
		//close MySQL connection
		conn.close();
		//store model to MySQL
		ds.getDefaultModel().add(model);
		//close SDB connection
		store.getConnection().close();
		store.close();
		return true;
	}

	@Override
	public List<com.hp.hpl.jena.rdf.model.Statement> getDistOne(Resource r) throws Exception {		
		
		model = ds.getDefaultModel();
		List<com.hp.hpl.jena.rdf.model.Statement> ret = new ArrayList<com.hp.hpl.jena.rdf.model.Statement>();
		
		StmtIterator iter = r.listProperties();
		com.hp.hpl.jena.rdf.model.Statement stmt = null;
		while(iter.hasNext()) {
			stmt = iter.nextStatement();
			System.out.println(stmt.getObject().toString());
			ret.add(stmt);
		}
		iter = model.listStatements(null, null, r);
		while(iter.hasNext()) {
			stmt = iter.nextStatement();
			System.out.println(stmt.getSubject().toString());
			ret.add(stmt);
		}
		
		return ret;
	}
	
	public List<com.hp.hpl.jena.rdf.model.Statement> getDistOne(String  r) throws Exception 
	{		
		return getDistOne(model.createResource(r));
	}

	
	public List<Resource> getDistOneResources(Resource r) throws Exception {		
		
		model = ds.getDefaultModel();
		List<Resource> ret = new ArrayList<Resource>();
		
		StmtIterator iter = r.listProperties();
		com.hp.hpl.jena.rdf.model.Statement stmt = null;
		while(iter.hasNext()) {
			stmt = iter.nextStatement();
			System.out.println(stmt.getObject().toString());
			if(stmt.getObject().isResource())
				ret.add(stmt.getObject().asResource());
		}
		iter = model.listStatements(null, null, r);
		while(iter.hasNext()) {
			stmt = iter.nextStatement();
			System.out.println(stmt.getSubject().toString());
			ret.add(stmt.getSubject().asResource());
		}
		
		return ret;
	}

}
