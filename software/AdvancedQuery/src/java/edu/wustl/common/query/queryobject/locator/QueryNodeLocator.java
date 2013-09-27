/**
 *
 */

package edu.wustl.common.query.queryobject.locator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.query.util.global.AQConstants;

/**
 * To locate the DAG nodes, so that they will appear on the Dag As directed routed aCyclic graph.
 * @author prafull_kadam
 */
public class QueryNodeLocator
{
	/**
	 * maxNodeAtLevel maximum nodes at each level.
	 */
	private List<Integer> maxNodeAtLevel;

	/**
	 * List of visible expression list level wise.
	 */
	private List<List<Integer>> visibleExpListLevelWise;

	/**
	 * The Max X coordinate.
	 */
	private final int maxX;

	/**
	 * The query constraints.
	 */
	private final IConstraints constraints;

	/**
	 * Object of IJoinGraph.
	 */
	private final IJoinGraph graph;

	/**
	 * The position map.
	 */
	private Map<Integer, Position> positionMap;

	/**
	 * X offset.
	 */
	private static final int X_OFFSET = AQConstants.TEN;

	/**
	 * Width of the node in the dag.
	 */
	private static final int WIDTH_OF_NODE = AQConstants.TWO_HUNDRED_TWENTY;

	/**
	 * Constructor to instantiate the Object.
	 * @param maxX The Max X coordinate.
	 * @param query The reference to the Query Object.
	 */
	public QueryNodeLocator(int maxX, IQuery query)
	{
		this.maxX = maxX;
		constraints = query.getConstraints();
		graph = constraints.getJoinGraph();
		int noOfRoots = countNodeAtLevel();
		if (noOfRoots == 1)
		{
			createPositionMap();
		}
	}

	/**
	 * To get the Map of the visible nodes verses the x & y positions.
	 * @return Map of the visible nodes verses the x & y positions.
	 */
	public Map<Integer, Position> getPositionMap()
	{
		return positionMap;
	}

	/**
	 * This method will create position Map.
	 *
	 */
	private void createPositionMap()
	{
		positionMap = new HashMap<Integer, Position>();
		int width = X_OFFSET;
		for (int level = 0; level < maxNodeAtLevel.size(); level++)
		{
			List<Integer> list = visibleExpListLevelWise.get(level);
			int nodesAtThisLevel = list.size();
			int yDiff = (maxX) / (nodesAtThisLevel + 1);
			int yPos = yDiff;
			for (Integer expId : list)
			{
				positionMap.put(expId, new Position(width, yPos));
				yPos += yDiff;
			}
			width += WIDTH_OF_NODE;
		}
	}

	/**
	 * This method will count visible node at each level.
	 * @return size count visible node at each level.
	 */
	private int countNodeAtLevel()
	{
		List<IExpression> allRootExprs = graph.getAllRoots();
		List<Integer> allRoots = idsList(allRootExprs);
		maxNodeAtLevel = new ArrayList<Integer>();

		int size = allRoots.size();
		if (size == 1)
		{
			visibleExpListLevelWise = new ArrayList<List<Integer>>();
			maxNodeAtLevel.add(1);
			addToVisibleMap(allRoots.get(0), 1);
			process(allRoots.get(0), 1);
		}
		else
		{
			positionMap = new HashMap<Integer, Position>();
			int yIncrement = X_OFFSET * AQConstants.SIX;
			int yCoordinate = yIncrement;
			int xCoordinate = X_OFFSET;
			for (IExpression expression : constraints)
			{
				if (expression.isVisible())
				{
					positionMap.put(expression.getExpressionId(),
							new Position(xCoordinate, yCoordinate));
					xCoordinate += WIDTH_OF_NODE / AQConstants.TWO;
					yCoordinate += yIncrement;
				}
			}
		}
		return size;
	}

	/**
	 * To add the node in visible node list.
	 * @param expId expression Id
	 * @param level The level of the node in the Graph.
	 */
	private void addToVisibleMap(Integer expId, int level)
	{
		List<Integer> list;
		if (level <= visibleExpListLevelWise.size() - 1)
		{
			list = visibleExpListLevelWise.get(level);
		}
		else
		{
			list = new ArrayList<Integer>();
			visibleExpListLevelWise.add(list);
		}
		list.add(expId);
	}

	/**
	 * To Process the expression node with the given level.
	 * @param expId expression Id
	 * @param level The level of the node in the Graph.
	 */
	private void process(Integer expId, int level)
	{
		List<Integer> childrenList = idsList(graph
				.getChildrenList(constraints.getExpression(expId)));
		for (Integer child : childrenList)
		{
			IExpression expression = constraints.getExpression(child);
			if (expression.isInView())
			{
				if (maxNodeAtLevel.size() <= level)
				{
					maxNodeAtLevel.add(1);
					addToVisibleMap(child, level + 1);
				}
				else
				{
					maxNodeAtLevel.set(level, maxNodeAtLevel.get(level) + 1);
					addToVisibleMap(child, level);
				}
				process(child, level + 1);
			}
			else
			{
				process(child, level);
			}
		}
	}

	/**
	 * @param exprList List of expressions
	 * @return res list of expression ids
	 */
	private List<Integer> idsList(List<IExpression> exprList)
	{
		List<Integer> res = new ArrayList<Integer>();
		for (IExpression expr : exprList)
		{
			res.add(expr.getExpressionId());
		}
		return res;
	}
}
