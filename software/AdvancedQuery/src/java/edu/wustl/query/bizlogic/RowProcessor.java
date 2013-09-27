
package edu.wustl.query.bizlogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.emory.mathcs.backport.java.util.Collections;
import edu.wustl.common.query.queryobject.impl.AttributeComparator;
import edu.wustl.common.query.queryobject.impl.OutputAssociationColumn;
import edu.wustl.common.query.queryobject.impl.OutputAttributeColumn;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.QueryExportDataHandler;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.QueryDetails;

public class RowProcessor
{

	private Map<IExpression, ExpressionRecords> expMap;
	private ExprInfoCache exprInfoCache;
	private SelectedColumnsMetadata selectColMetadata;
	private QueryDetails queryDetailsObj;
	private List<String> selectSqlColLst;
	private final Map<IExpression, BaseAbstractAttributeInterface> expVsAssoc = new HashMap<IExpression, BaseAbstractAttributeInterface>();
	private final Map<IExpression, BaseAbstractAttributeInterface> tgtExpVsAssoc = new HashMap<IExpression, BaseAbstractAttributeInterface>();
	private Map<String, String> columnNameMap;

	/**
	 * @return the columnNameMap
	 */
	public Map<String, String> getColumnNameMap()
	{
		return columnNameMap;
	}

	/**
	 * @param columnNameMap the columnNameMap to set
	 */
	public void setColumnNameMap(Map<String, String> columnNameMap)
	{
		this.columnNameMap = columnNameMap;
	}

	/**
	 * @return the expVsAssoc
	 */
	public Map<IExpression, BaseAbstractAttributeInterface> getExpVsAssoc()
	{
		return expVsAssoc;
	}

	/**
	 * @return the tgtExpVsAssoc
	 */
	public Map<IExpression, BaseAbstractAttributeInterface> getTgtExpVsAssoc()
	{
		return tgtExpVsAssoc;
	}

	private void init(String sql, SelectedColumnsMetadata selectColMetadata,
			QueryDetails queryDetailsObj)
	{
		this.selectColMetadata = selectColMetadata;
		this.queryDetailsObj = queryDetailsObj;
		expMap = new IdentityHashMap<IExpression, ExpressionRecords>();
		exprInfoCache = new ExprInfoCache();

		// changed as part of bug 20478
		initColumnList(sql);
	}

	/**
	 * Populates the list containing the list of column names from the define view.
	 * @param selectSql SQL query
	 * @return selectSqlColumnList
	 */
	private void initColumnList(String selectSql)
	{
		List<String> selectSqlColLst = new ArrayList<String>();
		String substring = selectSql.substring(selectSql.indexOf("select") + 6, selectSql
				.indexOf("from"));
		StringTokenizer tokenizer = new StringTokenizer(substring, ",");
		while (tokenizer.hasMoreTokens())
		{
			selectSqlColLst.add(tokenizer.nextToken().trim());
		}
		this.selectSqlColLst = Collections.unmodifiableList(selectSqlColLst);
	}

	public List<Map<OutputAssociationColumn, Object>> populateData(List<List<String>> dataList,
			String sql, SelectedColumnsMetadata selectColMetadata, QueryDetails queryDetailsObj)
			throws MultipleRootsException
	{
		init(sql, selectColMetadata, queryDetailsObj);

		initializeExpMap(queryDetailsObj.getQuery().getConstraints());
		IExpression rootExp = queryDetailsObj.getQuery().getConstraints().getRootExpression();

		for (List<String> list : dataList)
		{
			setColumnNameMap(getColumnNameMap(sql, list));
			populateIdRecordMap(rootExp, list);
		}

		return new ArrayList<Map<OutputAssociationColumn, Object>>(expMap.get(rootExp).getExpRecs()
				.values());
	}

	private void initializeExpMap(IConstraints constraints)
	{
		for (IExpression expression : constraints)
		{
			if (expression.isVisible())
			{
				expMap.put(expression, new ExpressionRecords());
			}
		}

	}

	/**
	 * Populates the recordMap with key=id(value of identifier of each node in query)
	 * and value=object(object can be list, map or a string value)
	 * @param rootExp Root Expression
	 * @param list list of records
	 * @throws MultipleRootsException MultipleRootsException
	 */
	private void populateIdRecordMap(IExpression rootExp, List<String> list)
			throws MultipleRootsException
	{
		Integer idIndex = exprInfoCache.idIndex(rootExp);
		if (idIndex < list.size() && idIndex != -1)
		{
			String idValue = list.get(idIndex);


			Map<OutputAssociationColumn, Object> recMap = recMap(rootExp, idValue);
			// this is needed id node is added in view mode & there is no record for it to display then empty record needs to be added in tree after creating complete list.
			if(idValue!=null && !"".equals(idValue))
			{
			List<AbstractAttributeInterface> attributeList = exprInfoCache.attrList(rootExp);
			for (AbstractAttributeInterface attribute : attributeList)
			{
				if (attribute instanceof AssociationInterface)
				{
					List<IExpression> associatedExpList = exprInfoCache.getAssociatedExpressions(
							attribute, rootExp);

					for (IExpression associatedExp : associatedExpList)
					{
						OutputAssociationColumn opAssocCol = new OutputAssociationColumn(attribute,
								rootExp, associatedExp);
						populateIdRecordMap(associatedExp, list);

						List<Map<OutputAssociationColumn, Object>> childList = (List<Map<OutputAssociationColumn, Object>>) recMap
								.get(opAssocCol);
						if (childList == null)
						{
							childList = new ArrayList<Map<OutputAssociationColumn, Object>>();
							recMap.put(opAssocCol, childList);
						}
						Integer childIdIndex = exprInfoCache.idIndex(associatedExp);
						if (childIdIndex < list.size() && childIdIndex != -1)
						{
							String childId = list.get(exprInfoCache.idIndex(associatedExp));
							if (!childList.contains(expMap.get(associatedExp).getMap(childId)))
							{
								childList.add(expMap.get(associatedExp).getMap(childId));
							}
						}
					}

				}
				else if (attribute instanceof AttributeInterface)
				{
					// isPresent should take AttributeInterface as input
					OutputAttributeColumn val = getValueForAttribute(
							(AttributeInterface) attribute, selectColMetadata
									.getSelectedAttributeMetaDataList(), rootExp);
					if (val != null)
					{
						OutputAssociationColumn opAssocCol = new OutputAssociationColumn(attribute,
								rootExp, null);
						recMap.put(opAssocCol, val);
					}
				}
			}
			}
		}
	}

	// caching results of idIndex, attrList, getAssociatedExpressions
	// changed as part of bug 20478
	private class ExprInfoCache
	{

		private Map<IExpression, Integer> expToIdIndex = new HashMap<IExpression, Integer>();
		private Map<IExpression, List<AbstractAttributeInterface>> expToAttrs = new HashMap<IExpression, List<AbstractAttributeInterface>>();

		private class Key
		{

			// TODO should be AssocationInterface??
			private AbstractAttributeInterface attr;
			private IExpression expr;

			/**
			 * @param associationInterface
			 * @param expr
			 */
			public Key(AbstractAttributeInterface attr, IExpression expr)
			{
				this.attr = attr;
				this.expr = expr;
			}

			/* (non-Javadoc)
			 * @see java.lang.Object#hashCode()
			 */
			@Override
			public int hashCode()
			{
				final int prime = 31;
				int result = 1;
				result = prime * result + getOuterType().hashCode();
				result = prime * result + ((attr == null) ? 0 : System.identityHashCode(attr));
				result = prime * result + ((expr == null) ? 0 : System.identityHashCode(expr));
				return result;
			}

			@Override
			public boolean equals(Object obj)
			{
				if (this == obj)
				{
					return true;
				}
				if (obj == null)
				{
					return false;
				}
				if (getClass() != obj.getClass())
				{
					return false;
				}
				Key other = (Key) obj;
				// TODO check no cloning etc... (also change hashCode if this is changed)
				return attr == other.attr && expr == other.expr;
			}

			private ExprInfoCache getOuterType()
			{
				return ExprInfoCache.this;
			}

		}

		private Map<Key, List<IExpression>> keyToExpr = new HashMap<Key, List<IExpression>>();

		/**
		 * Returns the index of the identifier of the passed expression.
		 * @param rootExp expression
		 * @return rootIndex Index of identifier of the passed expression.
		 */
		private Integer idIndex(IExpression rootExp)
		{
			if (expToIdIndex.containsKey(rootExp))
			{
				return expToIdIndex.get(rootExp);
			}

			Integer rootIndex = -1;
			Map<EntityInterface, Integer> entityIdIndexMap = new HashMap<EntityInterface, Integer>();
			//			List<String> selectSqlColLst = getColumnList(sql);
			Iterator<OutputTreeDataNode> iterator = queryDetailsObj.getUniqueIdNodesMap().values()
					.iterator();
			while (iterator.hasNext())
			{
				OutputTreeDataNode opTreeDataNode = iterator.next();
				if (opTreeDataNode.getExpressionId() == rootExp.getExpressionId())
				{
					List<QueryOutputTreeAttributeMetadata> attributes = opTreeDataNode
							.getAttributes();
					for (QueryOutputTreeAttributeMetadata attributeMetaData : attributes)
					{
						AttributeInterface attribute = attributeMetaData.getAttribute();
						String sqlColumnName = attributeMetaData.getColumnName().trim();
						if (attribute.getName().equals(AQConstants.IDENTIFIER))
						{
							int index = selectSqlColLst.indexOf(sqlColumnName);

							if (index >= 0)
							{
								if (rootExp.getQueryEntity().getDynamicExtensionsEntity() == attribute
										.getEntity())
								{
									rootIndex = index;
								}
								entityIdIndexMap.put(attribute.getEntity(), index);
								break;
							}
							else
							{
								entityIdIndexMap.put(attribute.getEntity(), selectSqlColLst.size());
								rootIndex = selectSqlColLst.size();
								selectSqlColLst.add(sqlColumnName);
								break;
							}
						}
					}
				}
			}
			expToIdIndex.put(rootExp, rootIndex);
			return rootIndex;
		}

		/**
		 * Retrieves the attribute list(attributes + associations) for the passed expression.
		 * @param rootExp expression
		 * @return attributeList
		 */
		private List<AbstractAttributeInterface> attrList(IExpression rootExp)
		{
			if (expToAttrs.containsKey(rootExp))
			{
				return expToAttrs.get(rootExp);
			}
			QueryExportDataHandler dataHandler = new QueryExportDataHandler(null, null);
			List<AbstractAttributeInterface> attributeList = dataHandler
					.getFinalAttributeList(rootExp.getQueryEntity().getDynamicExtensionsEntity());
			Collections.sort(attributeList, new AttributeComparator());

			expToAttrs.put(rootExp, attributeList);
			return attributeList;
		}

		/**
		 * Checks if the association between the given expressions is present in the graph.
		 * @param attribute attribute
		 * @param treeDataNode treeDataNode
		 * @param constraints constraints
		 * @return entity
		 * @throws MultipleRootsException MultipleRootsException
		 */
		private List<IExpression> getAssociatedExpressions(AbstractAttributeInterface attribute,
				IExpression entityExpression) throws MultipleRootsException
		{
			Key k = new Key(attribute, entityExpression);
			if (keyToExpr.containsKey(k))
			{
				return keyToExpr.get(k);
			}
			IConstraints constraints = queryDetailsObj.getQuery().getConstraints();
			List<IExpression> finalExpList = new ArrayList<IExpression>();
			JoinGraph joinGraph = (JoinGraph) constraints.getJoinGraph();
			BaseAbstractAttributeInterface assocInterface;
			IExpression finalExp;
			for (IExpression expression : constraints)
			{
				if (joinGraph.containsAssociation(expression, entityExpression))
				{
					IIntraModelAssociation association = (IIntraModelAssociation) joinGraph
							.getAssociation(expression, entityExpression);
					if (association != null)
					{
						assocInterface = association
								.getDynamicExtensionsAssociation();
						if (assocInterface.equals(attribute))
						{
							tgtExpVsAssoc.put(entityExpression, assocInterface);
							finalExp = populateEntityVsAssoc(joinGraph, expression,
									entityExpression, assocInterface);
							//finalExpList.add(finalExp);
						}
					}
				}
				else if (joinGraph.containsAssociation(entityExpression, expression))
				{
					IIntraModelAssociation association = (IIntraModelAssociation) joinGraph
							.getAssociation(entityExpression, expression);
					if (association != null)
					{
						assocInterface = association
								.getDynamicExtensionsAssociation();
						if (assocInterface.equals(attribute))
						{
							expVsAssoc.put(entityExpression, assocInterface);
							finalExp = populateTgtEntityVsAssoc(joinGraph, expression,
									assocInterface);
							finalExpList.add(finalExp);
						}
					}
				}
			}
			keyToExpr.put(k, finalExpList);
			return finalExpList;
		}
	}

	/**
	 * Retrieves the record map from the expression map corresponding to the passed identifier.
	 * @param rootExp expression
	 * @param idValue identifier
	 * @return record map
	 */
	private Map<OutputAssociationColumn, Object> recMap(IExpression rootExp, String idValue)
	{
		ExpressionRecords exprRecs = expMap.get(rootExp);
		expMap.put(rootExp, exprRecs);
		return exprRecs.getMap(idValue);
	}

	/**
	 * Populate tgtEntityVsAssoc map, where key-> the target entity of the passed association
	 * which is present in the DAG and value->Association.
	 * @param joinGraph joinGraph
	 * @param expression expression
	 * @param entityExp dynamicExtensionsEntity
	 * @param assocInterface associationInterface
	 * @return entity
	 */
	private IExpression populateTgtEntityVsAssoc(JoinGraph joinGraph, IExpression expression,
			BaseAbstractAttributeInterface assocInterface)
	{
		IExpression finalExp = null;
		if (!joinGraph.getChildrenList(expression).isEmpty() && !expression.isVisible())
		{
			IExpression exp = joinGraph.getChildrenList(expression).get(0);
			exp = getExpression(joinGraph, exp);
			if (exp.isVisible())
			{
				populateTgtVsAssocMap(assocInterface, exp);
				finalExp = exp;
			}
		}
		else
		{
			tgtExpVsAssoc.put(expression, assocInterface);
			finalExp = expression;
		}
		return finalExp;
	}

	/**
	 * Put the appropriate association in tgtEntityVsAssoc map.
	 * @param entityExpr dynamicExtensionsEntity
	 * @param assocInterface associationInterface
	 * @param exp expression
	 */
	private void populateTgtVsAssocMap(BaseAbstractAttributeInterface assocInterface,
			IExpression exp)
	{
		BaseAbstractAttributeInterface association = tgtExpVsAssoc.get(exp);
		if (association == null)
		{
			tgtExpVsAssoc.put(exp, assocInterface);
		}
	}

	/**
	 * Return the corresponding value for the attribute passed, from the columnNameMap.
	 * @param attribute attribute
	 * @param selectAttrMetaDataLst selectedAttributeMetaDataList
	 * @return opAttrCol
	 */
	public OutputAttributeColumn getValueForAttribute(AttributeInterface attribute,
			List<QueryOutputTreeAttributeMetadata> selectAttrMetaDataLst, IExpression expression)
	{
		OutputAttributeColumn opAttrCol = null;
		String value;
		int columnIndex = -1;

		for (QueryOutputTreeAttributeMetadata outputTreeAttributeMetadata : selectAttrMetaDataLst)
		{
			columnIndex++;
			BaseAbstractAttributeInterface presentAttribute = outputTreeAttributeMetadata
					.getAttribute();
			if (presentAttribute.equals(attribute)
					&& outputTreeAttributeMetadata.getTreeDataNode().getExpressionId() == expression
							.getExpressionId())
			{
				value = columnNameMap.get(outputTreeAttributeMetadata.getColumnName());
				opAttrCol = new OutputAttributeColumn(value, columnIndex, attribute, expression,
						null);
				break;
			}
		}
		return opAttrCol;
	}

	/**
	 * Populates entityVsAssoc map where key->the source entity of the given association and
	 * value->Association.
	 * @param joinGraph joinGraph
	 * @param expression expression
	 * @param entityExp
	 * @param assocInterface
	 */
	private IExpression populateEntityVsAssoc(JoinGraph joinGraph, IExpression expression,
			IExpression entityExp, BaseAbstractAttributeInterface assocInterface)
	{
		IExpression finalExp = null;
		if (!joinGraph.getParentList(expression).isEmpty() && !expression.isVisible())
		{
			IExpression exp = joinGraph.getParentList(expression).get(0);
			exp = getParentExpression(joinGraph, exp);
			if (exp.isVisible())
			{
				populateEntityvsAssocMap(entityExp, assocInterface, exp);
				finalExp = exp;
			}
		}
		else
		{
			expVsAssoc.put(expression, assocInterface);
			finalExp = expression;
		}
		return finalExp;
	}

	/**
	 * Put the appropriate association in entityVsAssoc map.
	 * @param entityExp dynamicExtensionsEntity
	 * @param assocInterface associationInterface
	 * @param exp expression
	 */
	private void populateEntityvsAssocMap(IExpression entityExp,
			BaseAbstractAttributeInterface assocInterface, IExpression exp)
	{
		BaseAbstractAttributeInterface association = expVsAssoc.get(entityExp);
		if (association == null)
		{
			expVsAssoc.put(exp, assocInterface);
		}
	}

	/**
	 * Get the parent expression of the passed expression.
	 * @param joinGraph joinGraph
	 * @param expression expression
	 * @return finalExp
	 */
	private IExpression getParentExpression(JoinGraph joinGraph, IExpression expression)
	{
		IExpression finalExp = expression;
		if (!expression.isVisible())
		{
			IExpression parentExp = joinGraph.getParentList(expression).get(0);
			finalExp = getParentExpression(joinGraph, parentExp);
		}
		return finalExp;
	}

	/**
	 * Get the child expression of the passed expression.
	 * @param joinGraph joinGraph
	 * @param expression expression
	 * @return finalExp
	 */
	private IExpression getExpression(JoinGraph joinGraph, IExpression expression)
	{
		IExpression finalExp = expression;
		if (!expression.isVisible() && !joinGraph.getChildrenList(expression).isEmpty())
		{
			IExpression parentExp = joinGraph.getChildrenList(expression).get(0);
			finalExp = getExpression(joinGraph, parentExp);
		}
		return finalExp;
	}

	/**
	 * Forms a columnNameMap (columnName-->Value)
	 * @param selectSql selectSql
	 * @param dataList dataList
	 * @return columnNameMap
	 */
	public Map<String, String> getColumnNameMap(String selectSql, List<String> dataList)
	{
		String modifiedSql;
		if (selectSql.indexOf(AQConstants.DISTINCT) == 0)
		{
			modifiedSql = selectSql.substring(selectSql.indexOf(AQConstants.DISTINCT) + 9,
					selectSql.indexOf(AQConstants.FROM_CLAUSE) - 1);
		}
		else
		{
			modifiedSql = selectSql.substring(selectSql.indexOf(AQConstants.SELECT_CLAUSE) + 7,
					selectSql.indexOf(AQConstants.FROM_CLAUSE) - 1);
		}
		StringTokenizer tokenizer = new StringTokenizer(modifiedSql, ",");
		Map<String, String> columnNameMap = new HashMap<String, String>();
		int index = 0;
		while (tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			String data = dataList.get(index);
			if (data.length() == 0)
			{
				data = " ";
			}
			columnNameMap.put(token.trim(), data);
			index++;
		}
		return columnNameMap;
	}
}
