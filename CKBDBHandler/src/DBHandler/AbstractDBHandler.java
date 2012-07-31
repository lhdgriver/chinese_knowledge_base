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
	public abstract boolean insert(String s, String p, String o);
	//搜索所有包含有literal的Literal，然后返回对应的Resource列表
	public abstract List<Resource> getResourcebyLiteral(String literal);
	//查询时有且只有一个参数为null
	public abstract List<String> selectID(String s, String p, String o);
}
