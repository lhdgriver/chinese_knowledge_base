package search;

import DBHandler.AbstractDBHandler;

/**
 * @date 2012-7-12
 * @author lsl
 * @description 
 */
public interface AbstractSearcher
{
    public abstract void setQuery(String query);
    public abstract void clearAndStop();
}
