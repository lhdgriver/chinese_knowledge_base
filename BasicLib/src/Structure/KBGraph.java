

package Structure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @date 2012-7-12
 * @author lsl
 * @description undirected
 */
public class KBGraph
{
    private int edgeCount;
    private int nodeCount;
    //<beginNode, <endNode, edge>>
    private Map<Integer, HashMap<Integer, Integer>> edges = new HashMap();
    private Map<String, Integer> nodeDic = new HashMap();
    private Map<String, Integer> edgeDic = new HashMap();

    public int getEdgeCount(){  return edgeCount;}
    public int getNodeCount(){  return nodeCount;}
    public boolean addEdge(String beginNode, String endNode, String edge)
    {
        if(!nodeDic.containsKey(beginNode))
        {
            nodeDic.put(beginNode, nodeDic.size());
            nodeCount++;
            edges.put(nodeDic.get(beginNode), new HashMap());
        }
        if(!nodeDic.containsKey(endNode))
        {
            nodeDic.put(endNode, nodeDic.size());
            nodeCount++;
            edges.put(nodeDic.get(endNode), new HashMap());
        }
        if(!edgeDic.containsKey(edge))
            edgeDic.put(edge, edgeDic.size());
        int beginIndex =nodeDic.get(beginNode);
        int endIndex = nodeDic.get(endNode);
        int edgeIndex = edgeDic.get(edge);
        if(edges.get(beginIndex).containsKey(endIndex) && edges.get(beginIndex).get(endIndex) == edgeIndex)
            return false;

        edges.get(beginIndex).put(endIndex, edgeIndex);
        edges.get(endIndex).put(beginIndex, edgeIndex);
        edgeCount++;
        return true;
    }

    public boolean addNode(String node)
    {
        if(nodeDic.containsKey(node))
            return false;
        nodeDic.put(node, nodeDic.size());
        nodeCount++;
        edges.put(nodeDic.get(node), new HashMap());
        return true;
    }
}
