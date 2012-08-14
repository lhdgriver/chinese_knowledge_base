package DBHandler;

import java.util.List;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @date 2012-7-12
 * @author lsl
 * @description 
 */
public interface AbstractDBHandler
{
	public abstract ResultSet execSelect(String query);
	public abstract Model execConstruct(String query);
	public abstract Model execDescribe(String query);
	public abstract boolean execAsk(String query);
	//for 'o' starts with "@", it is a literal
	//otherwise, it is a resource
	public abstract boolean insert(String _s, String _p, String _o) throws Exception;
	public abstract List<Resource> getResourcebyLiteral(String _literal) throws Exception;
	//for 'o' starts with "@", it is a literal
	//otherwise, it is a resource
	public List<RDFNode> selectRDFNode(String _s, String _p, String _o);
	//close all the connections and save model to db
	public abstract boolean close() throws Exception;
}
