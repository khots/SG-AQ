/**
 *
 */

package edu.wustl.common.query.queryobject.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;

/**
 * @author prafull_kadam
 * This class will contain Utility methods for Query objects.
 */
public class QueryObjectProcessor
{
	/**
	 * To get map of all Children nodes along with their id's under given output tree node.
	 * @param root The root node of the output tree.
	 * @return map of all Children nodes along with their id's under given output tree node.
	 */
	public static Map<Long, OutputTreeDataNode> getAllChildrenNodes(OutputTreeDataNode root)
	{
		Map<Long, OutputTreeDataNode> map = new HashMap<Long, OutputTreeDataNode>();
		map.put(root.getId(), root);
		List<OutputTreeDataNode> children = root.getChildren();
		for (OutputTreeDataNode childNode : children)
		{
			map.putAll(getAllChildrenNodes(childNode));
		}
		return map;
	}

	/**
	 * To get map of all Children nodes along with their id's under given output tree node.
	 * @param root The root node of the output tree.
	 * @param map of all Children nodes along with their id's under given output tree node.
	 */
	private static void addAllChildrenNodes(OutputTreeDataNode root,
			Map<String, OutputTreeDataNode> map)
	{
		map.put(root.getUniqueNodeId(), root);
		List<OutputTreeDataNode> children = root.getChildren();
		for (OutputTreeDataNode childNode : children)
		{
			addAllChildrenNodes(childNode, map);
		}
	}

	/**
	 * It returns all the nodes present all tress in results.
	 * @param keys set of trees
	 * @return Map of uniqueNodeId and tree node
	 */
	public static Map<String, OutputTreeDataNode> getAllChildrenNodes(List<OutputTreeDataNode> keys)
	{
		Map<String, OutputTreeDataNode> map = new HashMap<String, OutputTreeDataNode>();
		for (OutputTreeDataNode root : keys)
		{
			addAllChildrenNodes(root, map);
		}
		return map;
	}
}