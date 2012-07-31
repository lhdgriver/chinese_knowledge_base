

package DBHandler;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @date 2012-7-12
 * @author lsl
 * @description 
 */
public class JenaDBHandler implements AbstractDBHandler
{

    public List<List<Object>> excuteQuery(String command)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean excute(String command)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean insert(String s, String p, String o)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	public List<Resource> getResourcebyLiteral(String literal) {
		return null;
	}

	public List<String> selectID(String s, String p, String o) {
		return null;
	}

}
