package DBHandler;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @date 2012-7-12
 * @author lsl
 * @description 
 */
public interface AbstractDBHandler
{
	public abstract List<List<Object>> excuteQuery(String command);
	public abstract boolean excute(String command);
	//for 'o' starts with "@", it is a literal
	//otherwise, it is a resource
	public abstract boolean insert(String _s, String _p, String _o) throws Exception;
	public abstract List<Resource> getResourcebyLiteral(String literal);
	public abstract List<String> selectID(String s, String p, String o);
	//close all the connections and save model to db
	public abstract boolean close() throws Exception;
}
