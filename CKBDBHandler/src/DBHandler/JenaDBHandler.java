

package DBHandler;

import java.util.List;

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

    public List<String> select(String s, String p, String o)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	@Override
	public List<String> getElementIDbyLiteral(String keyword) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getLiteralbyKeyword(String keyword) 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
