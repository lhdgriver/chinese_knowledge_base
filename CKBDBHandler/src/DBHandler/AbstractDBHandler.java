

package DBHandler;

import java.util.List;

/**
 * @date 2012-7-12
 * @author lsl
 * @description 
 */
public interface AbstractDBHandler
{
	public abstract List<List<Object>> excuteQuery(String command);
	public abstract boolean excute(String command);
	public abstract boolean insert(String s, String p, String o);
	public abstract List<String> getElementIDbyLiteral(String keyword);
	public abstract List<String> getLiteralbyKeyword(String keyword);
	public abstract List<String> select(String s, String p, String o);



}
