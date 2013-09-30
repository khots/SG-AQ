
package edu.wustl.query.bizlogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.IArithmeticOperand;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IDateLiteral;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionAttribute;
import edu.wustl.common.querysuite.queryobject.IOutputAttribute;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.ITerm;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.TimeInterval;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.common.querysuite.queryobject.impl.OutputAttribute;
import edu.wustl.common.querysuite.utils.QueryUtility;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.Utility;
import edu.wustl.query.util.querysuite.QueryCSMUtil;
import edu.wustl.query.util.querysuite.QueryDetails;
import edu.wustl.query.util.querysuite.QueryModuleError;
import edu.wustl.query.util.querysuite.QueryModuleException;
import edu.wustl.query.util.querysuite.QueryModuleUtil;
import edu.wustl.query.util.querysuite.TemporalColumnMetadata;
import edu.wustl.query.util.querysuite.TemporalColumnUIBean;

/**
 * Creates QueryOutput spreadsheet data as per the inputs given by user on AddLimits and define results section.
 * This loads spreadsheet data in both the cases , once while loading first level (default) tree is shown and
 * secondly when user clicks on any of the node of the tree , the appropriate spreadsheet data is also loaded.
 * @author deepti_shelar
 */
public class QueryOutputSpreadsheetBizLogic
{
	private static org.apache.log4j.Logger logger = LoggerConfig
	.getConfiguredLogger(QueryOutputSpreadsheetBizLogic.class);

	/**
	 * SelectedColumnsMetadata.
	 */
	private SelectedColumnsMetadata selectedColumnMetaData;

	/**
	 * @return the selectedColumnMetaData
	 */
	public SelectedColumnsMetadata getSelectedColumnMetaData()
	{
		return selectedColumnMetaData;
	}

	/**
	 * @param selectedColumnMetaData the selectedColumnMetaData to set
	 */
	public void setSelectedColumnMetaData(
			SelectedColumnsMetadata selectedColumnMetaData)
	{
		this.selectedColumnMetaData = selectedColumnMetaData;
	}

	/**
	 * Processes spreadsheet data for label node which user has clicked.
	 * @param idNodesMap Map<Long, OutputTreeDataNode> map of identifiers and
	 * nodes present in tree.
	 * @param session HttpSession object
	 * @param columnMap map for column names for attributes for each node in query
	 * @param idOfClickedNode idOfClickedNode
	 * @param recordsPerPage number of recordsPerPage
	 * @param selectedColumnMetaData meta data for selected columns
	 * @param beanMap queryResultObjectDataMap
	 * @param constraints constraints
	 * @param outputTermsColumns outputTermsColumns
	 * @param specimenMap 
	 * @return Map for data
	 * @throws DAOException DAOException
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws QueryModuleException 
	 */
	public Map processSpreadsheetForLabelNode(HttpSession session,
			Map<Long, Map<AttributeInterface, String>> columnMap, String idOfClickedNode,
			int recordsPerPage, SelectedColumnsMetadata selectedColumnMetaData,
			boolean hasConditionOnIdentifiedField,
			Map<Long, QueryResultObjectDataBean> beanMap,
			IConstraints constraints, Map<String, IOutputTerm> outputTermsColumns, Map<String, String> specimenMap)
			throws DAOException, ClassNotFoundException, QueryModuleException
	{
		Map<Long, QueryResultObjectDataBean> queryResultDataMap = beanMap;
		QueryDetails queryDetailsObj = new QueryDetails(session);
		this.selectedColumnMetaData = selectedColumnMetaData;
		Map spreadSheetDataMap = new HashMap();
		String tableName = AQConstants.TEMP_OUPUT_TREE_TABLE_NAME
		+ queryDetailsObj.getSessionData().getUserId() + queryDetailsObj.getRandomNumber();
		String[] nodeIds = idOfClickedNode.split(AQConstants.NODE_SEPARATOR);
		String parentNode = nodeIds[0];//data
		String[] spiltParentNodeId = parentNode.split(AQConstants.UNDERSCORE);
		String treeNo = spiltParentNodeId[0];
		Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap;
		if (parentNode.contains("NULL"))
		{
			OutputTreeDataNode root = QueryModuleUtil.getRootNodeOfTree(queryDetailsObj, treeNo);
			QueryResultObjectDataBean queryResulObjectDataBean = QueryCSMUtil
			.getQueryResulObjectDataBean(root, queryDetailsObj);
			List<OutputTreeDataNode> childList = root.getChildren();
			boolean isSpecimen = Boolean.FALSE;
			String entityName = "";
			for (OutputTreeDataNode outputTreeDataNode : childList) 
			{
				entityName = outputTreeDataNode.getOutputEntity().getDynamicExtensionsEntity().getName();
				if(!Validator.isEmpty(entityName) && "edu.wustl.catissuecore.domain.Specimen".equals(entityName))
				{
					isSpecimen = Boolean.TRUE;
					break;
				}
			}
			if(isSpecimen)
				session.setAttribute("entityName", entityName);
			else
				session.setAttribute("entityName", queryResulObjectDataBean.getCsmEntityName());
			
			queryResultObjectDataBeanMap = new HashMap<Long, QueryResultObjectDataBean>();
			queryResultObjectDataBeanMap.put(root.getId(), queryResulObjectDataBean);
			if (selectedColumnMetaData.isDefinedView())
			{
				if (queryResultDataMap == null)
				{
					queryResultDataMap = new HashMap<Long, QueryResultObjectDataBean>();
				}
				spreadSheetDataMap = createSpreadsheetData(treeNo, root, queryDetailsObj, null,
						recordsPerPage, this.selectedColumnMetaData, queryResultDataMap,
						hasConditionOnIdentifiedField, constraints, outputTermsColumns,specimenMap);
				spreadSheetDataMap.put(AQConstants.DEFINE_VIEW_RESULT_MAP,
						queryResultDataMap);
			}
			else
			{
				spreadSheetDataMap = createSpreadsheetData(treeNo, root,
						queryDetailsObj, null,
						recordsPerPage, this.selectedColumnMetaData,
						queryResultObjectDataBeanMap,
						hasConditionOnIdentifiedField, constraints, outputTermsColumns,specimenMap);
				spreadSheetDataMap.put(AQConstants.QUERY_REASUL_OBJECT_DATA_MAP,
						queryResultObjectDataBeanMap);
			}
			this.selectedColumnMetaData.setCurrentSelectedObject(root);
		}
		else
		{
			String parentData = spiltParentNodeId[2];
			String uniqueParentNodeId = treeNo + "_" + spiltParentNodeId[1];
			OutputTreeDataNode parentTreeNode = queryDetailsObj.getUniqueIdNodesMap().get(
					uniqueParentNodeId);

			String parentIdColumnName = QueryModuleUtil.getParentIdColumnName(parentTreeNode);
			String currentNode = nodeIds[1];//label
			String[] spiltCurrentNodeId = currentNode.split(AQConstants.UNDERSCORE);
			String currentNodeId = spiltCurrentNodeId[1];
			String uniqueCurrentNodeId = treeNo + "_" + currentNodeId;
			OutputTreeDataNode currentTreeNode = queryDetailsObj.getUniqueIdNodesMap().get(
					uniqueCurrentNodeId);

			QueryResultObjectDataBean queryResulObjectDataBean = QueryCSMUtil
			.getQueryResulObjectDataBean(currentTreeNode, queryDetailsObj);
			session.setAttribute("entityName", queryResulObjectDataBean.getCsmEntityName());
			queryResultObjectDataBeanMap = new HashMap<Long, QueryResultObjectDataBean>();
			queryResultObjectDataBeanMap.put(currentTreeNode.getId(), queryResulObjectDataBean);

			if (!selectedColumnMetaData.isDefinedView())
			{
				new DefineGridViewBizLogic().getColumnsMetadataForSelectedNode(currentTreeNode,
						this.selectedColumnMetaData, constraints);
			}
			List resultList = createSQL(spreadSheetDataMap, currentTreeNode, parentIdColumnName,
					parentData, tableName, queryResultObjectDataBeanMap, queryDetailsObj,
					outputTermsColumns,specimenMap);

			String selectSql = (String) resultList.get(0);
			queryResultObjectDataBeanMap = (Map<Long, QueryResultObjectDataBean>) resultList.get(1);
			int startIndex = 0;
			QuerySessionData querySessionData;
			querySessionData = getQuerySessionData(queryDetailsObj, recordsPerPage, startIndex,
					spreadSheetDataMap, selectSql, queryResultObjectDataBeanMap,
					hasConditionOnIdentifiedField,null);
			spreadSheetDataMap.put(AQConstants.QUERY_SESSION_DATA, querySessionData);
			this.selectedColumnMetaData.setCurrentSelectedObject(currentTreeNode);
			if (selectedColumnMetaData.isDefinedView())
			{
				spreadSheetDataMap.put(AQConstants.DEFINE_VIEW_RESULT_MAP,
						queryResultObjectDataBeanMap);
				//session.setAttribute(AQConstants.DENORMALIZED_LIST, spreadSheetDataMap.get(AQConstants.SPREADSHEET_DATA_LIST));
			}
			else
			{
				spreadSheetDataMap.put(AQConstants.QUERY_REASUL_OBJECT_DATA_MAP,
						queryResultObjectDataBeanMap);
			}
		}
		List<Integer> expressionIdsInQuery = new ArrayList<Integer>();
		populateExpressionIds(expressionIdsInQuery, queryDetailsObj.getQuery().getConstraints());
		this.selectedColumnMetaData.setNoOfExpr(expressionIdsInQuery.size());
		spreadSheetDataMap.put(AQConstants.SELECTED_COLUMN_META_DATA, this.selectedColumnMetaData);
		return spreadSheetDataMap;
	}

	/**
	 * Processes spreadsheet data for data node which user has clicked.
	 * @param session session
	 * @param actualParentNodeId string id of parent
	 * @param recordsPerPage recordsPerPage
	 * @param selectedColumnMetaData selectedColumnMetaData
	 * @param hasConditionOnIdentifiedField hasConditionOnIdentifiedField
	 * @param beanMap queryResultObjectDataMap
	 * @param constraints constraints
	 * @param outputTermsColumns outputTermsColumns
	 * @return Map of spreadsheet data
	 * @throws DAOException DAOException
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws QueryModuleException 
	 */
	public Map processSpreadsheetForDataNode(HttpSession session,
	String actualParentNodeId, int recordsPerPage,SelectedColumnsMetadata
	selectedColumnMetaData, boolean hasConditionOnIdentifiedField,
	Map<Long, QueryResultObjectDataBean> beanMap,IConstraints constraints,
	Map<String, IOutputTerm> outputTermsColumns, Map<String, String> specimenMap)throws DAOException, ClassNotFoundException, QueryModuleException
	{
		Map<Long, QueryResultObjectDataBean> queryResultDataMap = beanMap;
		QueryDetails queryDetailsObj = new QueryDetails(session);
		this.selectedColumnMetaData = selectedColumnMetaData;
		Map spreadSheetDatamap;
		String[] nodeIds;
		nodeIds = actualParentNodeId.split(AQConstants.UNDERSCORE);
		String treeNo = nodeIds[0];
		String parentId = nodeIds[1];
		String parentData = nodeIds[2];
		String uniqueNodeId = treeNo + "_" + parentId;
		OutputTreeDataNode parentNode = queryDetailsObj.getUniqueIdNodesMap().get(uniqueNodeId);
		if (!selectedColumnMetaData.isDefinedView())
		{
			new DefineGridViewBizLogic().getColumnsMetadataForSelectedNode(parentNode,
					this.selectedColumnMetaData, constraints);
		}
		QueryResultObjectDataBean queryResulObjectDataBean = QueryCSMUtil
				.getQueryResulObjectDataBean(parentNode, queryDetailsObj);
		session.setAttribute("entityName", queryResulObjectDataBean.getCsmEntityName());
		Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap = new HashMap<Long, QueryResultObjectDataBean>();
		queryResultObjectDataBeanMap.put(parentNode.getId(), queryResulObjectDataBean);
		if (selectedColumnMetaData.isDefinedView())
		{
			if (queryResultDataMap == null)
			{
				queryResultDataMap = new HashMap<Long, QueryResultObjectDataBean>();
			}
			spreadSheetDatamap = createSpreadsheetData(treeNo, parentNode, queryDetailsObj,
			parentData, recordsPerPage, this.selectedColumnMetaData,queryResultDataMap,
			hasConditionOnIdentifiedField, constraints,outputTermsColumns,specimenMap);
			spreadSheetDatamap.put(AQConstants.DEFINE_VIEW_RESULT_MAP,
					queryResultDataMap);
		}
		else
		{
			spreadSheetDatamap = createSpreadsheetData(treeNo, parentNode, queryDetailsObj,
				parentData, recordsPerPage, this.selectedColumnMetaData,queryResultObjectDataBeanMap,
				hasConditionOnIdentifiedField, constraints,outputTermsColumns,specimenMap);
				spreadSheetDatamap.put(AQConstants.QUERY_REASUL_OBJECT_DATA_MAP,
						queryResultObjectDataBeanMap);}
		this.selectedColumnMetaData.setCurrentSelectedObject(parentNode);
		List<Integer> expressionIdsInQuery = new ArrayList<Integer>();
		populateExpressionIds(expressionIdsInQuery, queryDetailsObj.getQuery().getConstraints());
		this.selectedColumnMetaData.setNoOfExpr(expressionIdsInQuery.size());
		spreadSheetDatamap.put(AQConstants.SELECTED_COLUMN_META_DATA, this.selectedColumnMetaData);
		return spreadSheetDatamap;
	}

	/**
	 * This loads spreadsheet data when first level (default) tree is shown.This method is also called when data is to be
	 * loaded when user clicks on a leaf node of tree.
	 * @param treeNo tree number user has clicked
	 * @param node clicked by user
	 * @param queryDetailsObj queryDetailsObj
	 * @param parentData parentData
	 * @param recordsPerPage recordsPerPage
	 * @param selectedColumnsMetadata selectedColumnsMetadata
	 * @param queryResultObjectDataBeanMap queryResultObjectDataBeanMap
	 * @param hasConditionOnIdentifiedField hasConditionOnIdentifiedField
	 * @param constraints constraints
	 * @param outputTermsColumns outputTermsColumns
	 * @return map having data for column headers and data records.
	 * @throws DAOException  DAOException
	 * @throws QueryModuleException 
	 */
	public Map<String, List<String>> createSpreadsheetData(String treeNo, OutputTreeDataNode node,
			QueryDetails queryDetailsObj, String parentData, int recordsPerPage,
			SelectedColumnsMetadata selectedColumnsMetadata,
			Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap,
			boolean hasConditionOnIdentifiedField, IConstraints constraints,
			Map<String, IOutputTerm> outputTermsColumns, Map<String, String> specimenMap) throws DAOException,
			ClassNotFoundException, QueryModuleException
	{
		selectedColumnMetaData = selectedColumnsMetadata;
		Map spreadSheetDataMap = updateSpreadsheetData(queryDetailsObj, parentData, node,
				recordsPerPage, queryResultObjectDataBeanMap, hasConditionOnIdentifiedField,
				constraints, outputTermsColumns,specimenMap);
		List<Integer> expressionIdsInQuery = new ArrayList<Integer>();
		populateExpressionIds(expressionIdsInQuery, queryDetailsObj.getQuery().getConstraints());
		selectedColumnMetaData.setNoOfExpr(expressionIdsInQuery.size());
		spreadSheetDataMap.put(AQConstants.SELECTED_COLUMN_META_DATA, selectedColumnMetaData);
		return spreadSheetDataMap;
	}

	/**
	 * @param expressionIdsInQuery expressionIdsInQuery
	 * @param constraints constraints
	 */
	private static void populateExpressionIds(
			List<Integer> expressionIdsInQuery, IConstraints constraints)
	{
		for (IExpression expression : constraints)
		{
			if (expression.isVisible())
			{
				expressionIdsInQuery.add(Integer.valueOf(expression.getExpressionId()));
			}
		}
	}

	/**
	 * @param queryDetailsObj queryDetailsObj
	 * @param parentData parentData
	 * @param node node
	 * @param recordsPerPage recordsPerPage
	 * @param queryResultObjectDataBeanMap queryResultObjectDataBeanMap
	 * @param hasConditionOnIdentifiedField hasConditionOnIdentifiedField
	 * @param constraints constraints
	 * @param outputTermsColumns outputTermsColumns
	 * @return spreadSheetDataMap
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws DAOException DAOException
	 * @throws QueryModuleException 
	 */
	private Map updateSpreadsheetData(QueryDetails queryDetailsObj, String parentData,
			OutputTreeDataNode node, int recordsPerPage,
			Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap,
			boolean hasConditionOnIdentifiedField, IConstraints constraints,
			Map<String, IOutputTerm> outputTermsColumns, Map<String, String> specimenMap) throws ClassNotFoundException,
			DAOException, QueryModuleException
	{
		Map spreadSheetDataMap = new HashMap();
		String sql = queryDetailsObj.getSaveGeneratedQuery();
		String parentIdColumnName = QueryModuleUtil.getParentIdColumnName(node);
		String selectSql = createSQL(parentData, sql, spreadSheetDataMap, parentIdColumnName,
				node, queryResultObjectDataBeanMap, queryDetailsObj, constraints,
				outputTermsColumns,specimenMap);
		int startIndex = 0;
		QuerySessionData querySessionData = getQuerySessionData(queryDetailsObj, recordsPerPage,
				startIndex, spreadSheetDataMap, selectSql, queryResultObjectDataBeanMap,
				hasConditionOnIdentifiedField,null);
		List dataList = (List)spreadSheetDataMap.get(AQConstants.SPREADSHEET_DATA_LIST);
		if(dataList == null || dataList.isEmpty()){
			throw new QueryModuleException("Query Returns Zero Records", QueryModuleError.NO_RESULT_PRESENT);
		}
		spreadSheetDataMap.put(AQConstants.QUERY_SESSION_DATA, querySessionData);
		spreadSheetDataMap
				.put(AQConstants.QUERY_REASUL_OBJECT_DATA_MAP, queryResultObjectDataBeanMap);
		spreadSheetDataMap
				.put(AQConstants.DEFINE_VIEW_RESULT_MAP, queryResultObjectDataBeanMap);

		return spreadSheetDataMap;
	}

	/**
	 * Creates query string for given data.
	 * @param parentData parentData
	 * @param tableName name of the table
	 * @param spreadSheetDataMap map to store data list
	 * @param parentIdColumnName String column name of id of parents node
	 * @param node OutputTreeDataNode
	 * @param queryResultObjectDataBeanMap queryResultObjectDataBeanMap
	 * @param queryDetailsObj queryDetailsObj
	 * @param constraints constraints
	 * @param outputTermsColumns outputTermsColumns
	 * @return query string
	 */
	private String createSQL(String parentData, String originalSQL, Map spreadSheetDataMap,
			String parentIdColumnName, OutputTreeDataNode node,
			Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap,
			QueryDetails queryDetailsObj, IConstraints constraints,
			Map<String, IOutputTerm> outputTermsColumns, Map<String, String> specimenMap)
	{
		String selectSql = "";
		String idColumnOfCurrentNode = "";
		int actualColumnSize = 0;
		List<String> columnsList = new ArrayList<String>();
		List<IOutputAttribute> selectedOutputAttributeList = new ArrayList<IOutputAttribute>();
		QueryResultObjectDataBean queryResultObjectDataBean = new QueryResultObjectDataBean();
		if (!selectedColumnMetaData.isDefinedView())
		{
			queryResultObjectDataBean = queryResultObjectDataBeanMap.get(node.getId());
		}
		List<Integer> identifiedDataColumnIds = new ArrayList<Integer>();
		List<Integer> objectDataColumnIds = new ArrayList<Integer>();
		Map<Integer, QueryOutputTreeAttributeMetadata> fileTypeAttrMap =
			new HashMap<Integer, QueryOutputTreeAttributeMetadata>(); 
		int columnIndex = 0;
				
		List<QueryOutputTreeAttributeMetadata> attributes = new ArrayList<QueryOutputTreeAttributeMetadata>();
		attributes.addAll(node.getAttributes());
		
		for(OutputTreeDataNode child: QueryModuleUtil.getInViewChildren(node)) {
			attributes.addAll(child.getAttributes());
		}
		
		for (QueryOutputTreeAttributeMetadata attributeMetaData : attributes)
		{
			AttributeInterface attribute = attributeMetaData.getAttribute();
			String sqlColumnName = attributeMetaData.getColumnName();
			
			if (!selectedColumnMetaData.isDefinedView()
					&& (attribute.getIsIdentified() != null && attribute.getIsIdentified()))
			{
				identifiedDataColumnIds.add(columnIndex);
				queryResultObjectDataBean.setIdentifiedDataColumnIds(identifiedDataColumnIds);
				queryResultObjectDataBean.setHasAssociatedIdentifiedData(true);
			}
			if (attribute.getName().equals(AQConstants.IDENTIFIER) && idColumnOfCurrentNode.isEmpty())
			{
				idColumnOfCurrentNode = sqlColumnName;
				if (!selectedColumnMetaData.isDefinedView())
				{
					queryResultObjectDataBean.setMainEntityIdentifierColumnId(columnIndex);
					queryResultObjectDataBean.setEntityId(columnIndex);
				}
			}
			if (attribute.getAttributeTypeInformation().getDataType().equalsIgnoreCase("file"))
			{
				int fileTypeIndex = columnIndex + fileTypeAttrMap.size();
				queryResultObjectDataBean.setClobeType(true);
				fileTypeAttrMap.put(fileTypeIndex, attributeMetaData);
			}
			else
			{
				selectSql = selectSql + sqlColumnName + ",";				
				columnsList.add(attributeMetaData.getDisplayName());
				IOutputAttribute attr = new OutputAttribute(constraints.getExpression(node.getExpressionId()), attribute);
				selectedOutputAttributeList.add(attr);
				objectDataColumnIds.add(columnIndex);
				columnIndex++;
			}
		}
		if (queryResultObjectDataBean.isClobeType())
		{
			queryResultObjectDataBean
					.setFileTypeAtrributeIndexMetadataMap(fileTypeAttrMap);
		}
		if (selectedColumnMetaData.isDefinedView())
		{
			queryResultObjectDataBeanMap.clear();
			originalSQL = getSQLForSelectedColumns(spreadSheetDataMap, queryResultObjectDataBeanMap,
					queryDetailsObj, outputTermsColumns, parentData,specimenMap);
			columnsList = (List<String>) spreadSheetDataMap.get(AQConstants.SPREADSHEET_COLUMN_LIST);		
		}
		else
		{
			selectSql = selectSql.substring(0, selectSql.lastIndexOf(','));
			spreadSheetDataMap.put(AQConstants.SPREADSHEET_COLUMN_LIST, columnsList);
			selectedColumnMetaData.setSelectedAttributeMetaDataList(attributes);
		}
		actualColumnSize = columnsList.size();
		if (parentData != null && parentData.equals(AQConstants.HASHED_NODE_ID) && false)
		{
			addHashedRow(spreadSheetDataMap);
			selectSql = "";
		}
		else
		{
			if (!selectedColumnMetaData.isDefinedView())
			{
				if (!outputTermsColumns.isEmpty())
				{
					TemporalColumnUIBean temporalColumnUIBean = new TemporalColumnUIBean(node,
							selectSql, columnsList, outputTermsColumns, columnIndex, constraints);
					List tqColumnMetadataList = modifySqlForTemporalColumns(temporalColumnUIBean,
							queryDetailsObj, parentData);
					queryResultObjectDataBean.setTqColumnMetadataList(tqColumnMetadataList);
					selectSql = temporalColumnUIBean.getSql();
					columnIndex = temporalColumnUIBean.getColumnIndex();
					actualColumnSize += outputTermsColumns.size(); 
				}
				if (queryResultObjectDataBean.getMainEntityIdentifierColumnId() == -1)
				{
					Map<EntityInterface, Integer> entityIdIndexMap = new HashMap<EntityInterface, Integer>();
					selectSql = QueryCSMUtil.updateEntityIdIndexMap(queryResultObjectDataBean,
							columnIndex, selectSql, null, entityIdIndexMap, queryDetailsObj,specimenMap);
					entityIdIndexMap = queryResultObjectDataBean.getEntityIdIndexMap();
					if (entityIdIndexMap.get(queryResultObjectDataBean.getMainEntity()) != null)
					{
						queryResultObjectDataBean.setMainEntityIdentifierColumnId(entityIdIndexMap
								.get(queryResultObjectDataBean.getMainEntity()));
					}
				}
			}
			selectSql = selectSql + " from " + originalSQL;
			if (parentData == null)
			{
				selectSql = selectSql + " where " + idColumnOfCurrentNode + " "
				+ RelationalOperator.getSQL(RelationalOperator.IsNotNull)+" ORDER BY "+idColumnOfCurrentNode;
			}
			else
			{
				selectSql = selectSql + " where (" + parentIdColumnName + " = '" + parentData + "' "
				+ LogicalOperator.And + " " + idColumnOfCurrentNode + " "
				+ RelationalOperator.getSQL(RelationalOperator.IsNotNull) + ") ORDER BY "+idColumnOfCurrentNode;
			}
			if (!selectedColumnMetaData.isDefinedView())
			{
				if (!identifiedDataColumnIds.isEmpty())
				{
					queryResultObjectDataBean.setHasAssociatedIdentifiedData(true);
				}
				queryResultObjectDataBean.setObjectColumnIds(objectDataColumnIds);
			}
			if (selectSql.indexOf(AQConstants.SELECT_DISTINCT) == -1 && !selectedColumnMetaData.isDefinedView())
			{
				selectSql = AQConstants.SELECT_DISTINCT + selectSql;
			}
			if(selectSql.indexOf("select") != 0) {
				selectSql = "select " + selectSql;
			}
		}
		queryDetailsObj.setColumnSize(actualColumnSize);
		return originalSQL;
	}

	/**
	 * Get the column name of identifier.
	 * @param expression expression
	 * @param attributeColumnNameMap attributeColumnNameMap
	 * @return column name of identifier
	 */
	private String getIdColumnName(IExpression expression,
			Map<AttributeInterface, String> attributeColumnNameMap)
	{
		AttributeInterface idAttribute = expression.getQueryEntity().getDynamicExtensionsEntity()
				.getAttributeByName(AQConstants.IDENTIFIER);
		return attributeColumnNameMap.get(idAttribute);
	}

	/**
	 * @param clickedExpression clickedExpression
	 * @param expressionsInTerm expressionsInTerm
	 * @return expression
	 */
	private IExpression getAnotherExpressionInCF(IExpression clickedExpression, Set<IExpression>
	expressionsInTerm)
	{
		IExpression expression = null;
		for(IExpression exp : expressionsInTerm)
		{
			if(!exp.equals(clickedExpression))
			{
				expression = exp;
				break;
			}
		}
		return expression;
	}

	/**
	 * Modify query as per the temporal condition.
	 * @param temporalColumnUIBean temporalColumnUIBean
	 * @param queryDetailsObj queryDetailsObj
	 * @param parentData parentData
	 * @return tQColumnMetataDataList
	 */
	public List modifySqlForTemporalColumns(TemporalColumnUIBean temporalColumnUIBean,
			QueryDetails queryDetailsObj, String parentData)
	{
		OutputTreeDataNode node = temporalColumnUIBean.getNode();

		Map<String, IOutputTerm> outputTermsColumns = temporalColumnUIBean.getOutputTermsColumns();
		Set<String> keySet = outputTermsColumns.keySet();
		Iterator<String> iterator1 = keySet.iterator();
		List tQColumnMetataDataList = new ArrayList();
		while (iterator1.hasNext())
		{
			String columnName = iterator1.next();
			IOutputTerm outputTerm = outputTermsColumns.get(columnName);
			ITerm term = outputTerm.getTerm();
			TimeInterval<?> timeInterval = outputTerm.getTimeInterval();
			Set<IExpression> expressionsInTerm = QueryUtility.getExpressionsInTerm(term);

			if(node == null || (expressionsInTerm.size() == 1 && isContainingExpressionInTQ(node,
					expressionsInTerm)))
			{
				modifyTemporalColumnBean(temporalColumnUIBean,outputTerm.getName(),columnName);
				setTQColumnMetadata(temporalColumnUIBean.getColumnIndex(),
						tQColumnMetataDataList, term,timeInterval);
			}
			else if(isContainingExpressionInTQ(node, expressionsInTerm))
			{
				boolean  isValidCardinality= isValidCardinality(temporalColumnUIBean, queryDetailsObj,
						parentData,
						node,expressionsInTerm);
				if(isValidCardinality)
				{
					modifyTemporalColumnBean(temporalColumnUIBean,outputTerm.getName(),columnName);
					setTQColumnMetadata(temporalColumnUIBean.getColumnIndex(),
							tQColumnMetataDataList, term,timeInterval);
				} else {
					temporalColumnUIBean.getColumnsList().add(outputTerm.getName());
				}
			}
			
		}
		return  tQColumnMetataDataList;
	}

	public void setTQColumnMetadata(int columnIndex,
			List tQColumnMetataDataList, ITerm term, TimeInterval<?> timeInterval)
	{
		TemporalColumnMetadata temporalColumnMetada = new TemporalColumnMetadata();
		temporalColumnMetada.setColumnIndex(columnIndex);
		temporalColumnMetada.setTimeInterval(timeInterval);
		temporalColumnMetada.setTermType(term.getTermType());
		for (int i = 0; i < term.numberOfOperands(); i++)
		{
			IArithmeticOperand operand = term.getOperand(i);
			if (operand instanceof IExpressionAttribute)
			{
			    IExpressionAttribute expressionAttribute = (IExpressionAttribute) operand;
                Map<String,String> tagKeyValueMap = new HashMap<String, String>();
                Collection<TaggedValueInterface> taggedValueCollection = expressionAttribute.
                getAttribute().getTaggedValueCollection();
                for (TaggedValueInterface taggedValueInterface : taggedValueCollection)
                {
                    tagKeyValueMap.put(taggedValueInterface.getKey(),taggedValueInterface.getValue());
                }
                    temporalColumnMetada.setBirthDate(edu.wustl.security.global.Utility.getInstance()
                            .getIsBirthDate(tagKeyValueMap));

			}
			else if (operand instanceof IDateLiteral)
			{
				IDateLiteral date = (IDateLiteral)term.getOperand(i);
				temporalColumnMetada.setPHIDate(date);
			}
		}
		tQColumnMetataDataList.add(temporalColumnMetada);
	}

	/**
	 * Check if the cardinality is valid.
	 * @param temporalColumnUIBean temporalColumnUIBean
	 * @param queryDetailsObj queryDetailsObj
	 * @param parentData parentData
	 * @param node node
	 * @param expressionsInTerm expressionsInTerm
	 * @return <CODE>true</CODE> cardinality is valid,
	 * <CODE>false</CODE> otherwise
	 */
	private boolean isValidCardinality(TemporalColumnUIBean temporalColumnUIBean,
			QueryDetails queryDetailsObj, String parentData, OutputTreeDataNode node, Set<IExpression>
	expressionsInTerm)
	{
		String sql = queryDetailsObj.getSaveGeneratedQuery();
		boolean isValidCardinality = false;
		IConstraints constraints = temporalColumnUIBean.getConstraints();
		IExpression clickedExpression = constraints.getExpression(node.getExpressionId());
		IExpression anotherExpressionInCF = getAnotherExpressionInCF(clickedExpression,expressionsInTerm);

		String anotherExpIdColumn = getIdColumnName(anotherExpressionInCF,queryDetailsObj.
				getAttributeColumnNameMap());
		String clickedExpIdColumn = getIdColumnName(clickedExpression,queryDetailsObj.
				getAttributeColumnNameMap());
		String sqlTofindOutCardinality;
		if(parentData == null)
		{
			sqlTofindOutCardinality = "select count(*) from (select "+clickedExpIdColumn + ", " +
					"count(distinct "+anotherExpIdColumn+") as card from ("+sql+") temp group by "+
					clickedExpIdColumn+" having card >1) x ";
			int intCount = executeSqlToGetCount(sqlTofindOutCardinality);
			if(intCount == 0)
			{
				isValidCardinality = true;
			}
		}
		else
		{
			sqlTofindOutCardinality = "select count(distinct "+anotherExpIdColumn + ") from ("+sql+") temp "+
			" where "+clickedExpIdColumn +"= "+parentData;
			int intCount = executeSqlToGetCount(sqlTofindOutCardinality);
			if(intCount <= 1)
			{
				isValidCardinality = true;
			}
		}
		return isValidCardinality;
	}

	/**
	 * @param sqlTofindOutCardinality sqlTofindOutCardinality
	 * @return intCount
	 */
	private int executeSqlToGetCount(String sqlTofindOutCardinality)
	{
		List<List<String>> list = new ArrayList<List<String>>();
		int intCount = -1;
		try
		{
		    list = edu.wustl.query.util.global.Utility.executeSQL(sqlTofindOutCardinality,null);
		}
		catch (DAOException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (ClassNotFoundException e)
		{
			logger.error(e.getMessage(), e);
		}
		Iterator iterator = list.iterator();
		while (iterator.hasNext())
		{
			List<String> row = (List<String>) iterator.next();
			String count = row.get(0);
			intCount = Integer.parseInt(count);
		}
		return intCount;
	}
	/**
	 * @param temporalColumnUIBean temporalColumnUIBean
	 * @param displayColumnName displayColumnName
	 * @param columnName columnName
	 */
	private void modifyTemporalColumnBean(TemporalColumnUIBean temporalColumnUIBean,
			String displayColumnName, String columnName)
	{
		temporalColumnUIBean.getColumnsList().add(displayColumnName);
	//	String sql = temporalColumnUIBean.getSql() + ", " + columnName;
	//	temporalColumnUIBean.setSql(sql);
		temporalColumnUIBean.setColumnIndex(temporalColumnUIBean.getColumnIndex() + 1);

	}

	/**
	 * @param node node
	 * @param expressionsInTerm expressionsInTerm
	 * @return <CODE>true</CODE> expression term is present in node,
	 * <CODE>false</CODE> otherwise
	 */
	private boolean isContainingExpressionInTQ(OutputTreeDataNode node,
			Set<IExpression> expressionsInTerm)
	{
		boolean isExpInTQ = false;
		for (IExpression ex : expressionsInTerm)
		{
			if (node.getExpressionId() == ex.getExpressionId())
			{
				isExpInTQ = true;
				break;
			}
		}
		return isExpInTQ;
	}

	/**
	 * @param spreadSheetDataMap spreadSheetDataMap
	 */
	private void addHashedRow(Map spreadSheetDataMap)
	{
		List<String> columnsList = (List<String>) spreadSheetDataMap
				.get(AQConstants.SPREADSHEET_COLUMN_LIST);
		List<List<String>> dataList = new ArrayList<List<String>>();
		List<String> row = new ArrayList<String>();
		for (int columnIndex = 0; columnIndex < columnsList.size(); columnIndex++)
		{
			row.add(AQConstants.HASHED_OUT);
		}
		dataList.add(row);
		spreadSheetDataMap.put(AQConstants.SPREADSHEET_DATA_LIST, dataList);
	}

	/**
	 * @param spreadSheetDataMap map to store data list
	 * @param queryResultObjectDataBeanMap queryResultObjectDataBeanMap
	 * @param queryDetailsObj queryDetailsObj
	 * @param outputTermsColumns outputTermsColumns
	 * @param parentData parentData
	 * @return String sql
	 */
	private String getSQLForSelectedColumns(Map spreadSheetDataMap,
			Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap,
			QueryDetails queryDetailsObj, Map<String, IOutputTerm> outputTermsColumns,
			String parentData, Map<String, String> specimenMap)
	{
		String selectSql = queryDetailsObj.getSaveGeneratedQuery();
		List<String> definedColumnsList = new ArrayList<String>();
		StringBuffer sqlColumnNames = new StringBuffer();
		List<QueryOutputTreeAttributeMetadata> selectedAttributeMetaDataList = selectedColumnMetaData
				.getSelectedAttributeMetaDataList();
		
		Map<String, String> columnNameVsAliasMap= queryDetailsObj.getColumnNameVsAliasMap();
		int columnIndex = 0;
		int addedFileTypeAttributes = 0;
		List<EntityInterface> defineViewNodeList = new ArrayList<EntityInterface>();
		List<NameValueBean> selectedColumnNameValue = new ArrayList<NameValueBean>();
		Set<String> tableAliasNames = new HashSet<String>();
		List<IOutputAttribute> selAttrList = selectedColumnMetaData.getSelectedOutputAttributeList();
		for (IOutputAttribute outputAttribute : selAttrList)
		{
			for (QueryOutputTreeAttributeMetadata metaData : selectedAttributeMetaDataList)
			{
				AttributeInterface attribute = metaData.getAttribute();
				
				if (metaData.getAttribute().equals(outputAttribute.getAttribute())
					&& metaData.getTreeDataNode().getExpressionId() == outputAttribute.getExpression().getExpressionId())
				{
					NameValueBean nameValueBean = new NameValueBean();
					QueryResultObjectDataBean queryResultObjectDataBean = queryResultObjectDataBeanMap
					.get(metaData.getTreeDataNode().getId());
					String attributeWithClassName = metaData.getDisplayName();
					String treeAttributeNodeId = metaData.getUniqueId();
					nameValueBean.setName(attributeWithClassName);
					nameValueBean.setValue(treeAttributeNodeId);
					selectedColumnNameValue.add(nameValueBean);
					if (queryResultObjectDataBean == null)
					{
						queryResultObjectDataBean = QueryCSMUtil.getQueryResulObjectDataBean(metaData
								.getTreeDataNode(), queryDetailsObj);
						defineViewNodeList.add(attribute.getEntity());
					}
					if (attribute.getName().equalsIgnoreCase(AQConstants.IDENTIFIER))
					{
						queryResultObjectDataBean.setMainEntityIdentifierColumnId(columnIndex);
						queryResultObjectDataBean.setEntityId(columnIndex);
					}
					if (attribute.getIsIdentified() != null && attribute.getIsIdentified())
					{
						queryResultObjectDataBean.getIdentifiedDataColumnIds().add(columnIndex);
						queryResultObjectDataBean.setHasAssociatedIdentifiedData(true);
					}
					if (attribute.getDataType().equalsIgnoreCase(AQConstants.FILE_TYPE))
					{
						queryResultObjectDataBean.setClobeType(true);
						Map beanMetadataMap = queryResultObjectDataBean
						.getFileTypeAtrributeIndexMetadataMap();
						int fileTypeIndex = columnIndex + addedFileTypeAttributes;
						addedFileTypeAttributes++;
						beanMetadataMap.put(fileTypeIndex, metaData);
						queryResultObjectDataBean.setFileTypeAtrributeIndexMetadataMap(beanMetadataMap);
					}
					else
					{
						//String sqlColumnName = metaData.getColumnName();
						String actualColumnName = columnNameVsAliasMap.get(metaData.getColumnName());
						tableAliasNames.add(actualColumnName.split("\\.")[0]); 
						String sqlColumnName = actualColumnName+" "+ metaData.getColumnName();
						sqlColumnNames.append(sqlColumnName);
						sqlColumnNames.append(", ");
						String columnDisplayName = metaData.getDisplayName();
						definedColumnsList.add(columnDisplayName);
						queryResultObjectDataBean.getObjectColumnIds().add(columnIndex);
						columnIndex++;
					}
					queryResultObjectDataBeanMap.put(metaData.getTreeDataNode().getId(),
							queryResultObjectDataBean);
					break;
				}
			}
		}
		selectedColumnMetaData.setSelColNVBeanList(selectedColumnNameValue);
		int lastindexOfComma = sqlColumnNames.lastIndexOf(",");
		List tqColumnList = new ArrayList();
		if (!outputTermsColumns.isEmpty())
		{
			IConstraints constraints = queryDetailsObj.getQuery().getConstraints();
			TemporalColumnUIBean temporalColumnUIBean = new TemporalColumnUIBean(null, selectSql,
					definedColumnsList, outputTermsColumns, columnIndex, constraints);
			tqColumnList = modifySqlForTemporalColumns(temporalColumnUIBean, queryDetailsObj, parentData);
			sqlColumnNames.append(" " + columnNameVsAliasMap.get("temporal")+ " ") ;
			columnIndex = temporalColumnUIBean.getColumnIndex();
			lastindexOfComma = sqlColumnNames.length() -1;
		}
		if (lastindexOfComma != -1 || "".equals(selectSql))
		{
			String hiddenIdColumns = "";
			String columnsInSql = "";
			if (lastindexOfComma != -1)
			{
				columnsInSql = sqlColumnNames.toString().substring(0, lastindexOfComma);
			}
			if (!"".equals(selectSql))
			{
				queryDetailsObj.setColumnSize(definedColumnsList.size());
				hiddenIdColumns = Utility.generateHiddenIds(tableAliasNames, columnsInSql);
				//columnsInSql = columnsInSql + selectSql;
			
			}
			Map<EntityInterface, Integer> entityIdIndexMap = new HashMap<EntityInterface, Integer>();
			columnsInSql = QueryCSMUtil.updateEntityIdIndexMap(null, columnIndex, columnsInSql,
					defineViewNodeList, entityIdIndexMap, queryDetailsObj,specimenMap);
			Iterator<QueryResultObjectDataBean> iterator = queryResultObjectDataBeanMap.values()
					.iterator();
			while (iterator.hasNext())
			{
				setMainEntityColumnIdentifier(tqColumnList, entityIdIndexMap,
						iterator);
			}
			selectSql =  "SELECT DISTINCT "+ hiddenIdColumns+ " " + columnsInSql +" "+ selectSql.substring(selectSql.indexOf("from"));
		}
		spreadSheetDataMap.put(AQConstants.SPREADSHEET_COLUMN_LIST, definedColumnsList);
		return selectSql;
	}

	/**
	 * @param tqColumnList tqColumnList
	 * @param entityIdIndexMap entityIdIndexMap
	 * @param iterator iterator
	 */
	private void setMainEntityColumnIdentifier(List tqColumnList,
			Map<EntityInterface, Integer> entityIdIndexMap,
			Iterator<QueryResultObjectDataBean> iterator)
	{
		QueryResultObjectDataBean element = iterator.next();
		element.setTqColumnMetadataList(tqColumnList);
	/*	if (element.getMainEntityIdentifierColumnId() == -1)
		{
			if (!element.isMainEntity() && entityIdIndexMap.get(element
					.getMainEntity())!=null)
			{
					element.setMainEntityIdentifierColumnId(entityIdIndexMap.get(element
						.getMainEntity()));
			}
			else
			{
				element.setMainEntityIdentifierColumnId(entityIdIndexMap.get(element
								.getEntity()));
			}
		}*/
		element.setEntityIdIndexMap(entityIdIndexMap);
		QueryCSMUtil.setMainProtocolIdIndex(element);
	}

	/**
	 * @param queryDetailsObj queryDetailsObj
	 * @param recordsPerPage recordsPerPage
	 * @param startIndex startIndex
	 * @param spreadSheetDataMap spreadSheetDataMap
	 * @param selectSql selectSql
	 * @param queryResultObjectDataBeanMap queryResultObjectDataBeanMap
	 * @param hasConditionOnIdentifiedField hasConditionOnIdentifiedField
	 * @param selectedColumnsMetadata  selectedColumnsMetadata
	 * @return querySessionData
	 * @throws ClassNotFoundException
	 * @throws DAOException
	 * @throws QueryModuleException 
	 */
	public QuerySessionData getQuerySessionData(QueryDetails queryDetailsObj, int recordsPerPage,
			int startIndex, Map spreadSheetDataMap, String selectSql,
			Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap,
			boolean hasConditionOnIdentifiedField, SelectedColumnsMetadata selectedColumnsMetadata) throws ClassNotFoundException, DAOException, QueryModuleException
	{
		SelectedColumnsMetadata selectedColMetadata = handleDefineViewCase(selectedColumnsMetadata);
		QuerySessionData querySessionData = new QuerySessionData();
		querySessionData.setSql(selectSql);
		querySessionData.setQueryResultObjectDataMap(queryResultObjectDataBeanMap);
		querySessionData.setRecordsPerPage(recordsPerPage);
		//boolean isContPresent = isContainmentPresent(queryDetailsObj.getQuery());;
		/*if(selectedColMetadata.isDefinedView())
		{ 
			StringBuffer modifiedSql = modifySqlForDefineView(selectSql, queryDetailsObj);
			querySessionData.setSql(modifiedSql.toString());			
		}*/
		querySessionData.setSecureExecute(queryDetailsObj.getSessionData().isSecurityRequired());
		querySessionData.setHasConditionOnIdentifiedField(hasConditionOnIdentifiedField);
 
		CommonQueryBizLogic qBizLogic = new CommonQueryBizLogic();
 
		PagenatedResultData pagenatedResultData = qBizLogic.execute(queryDetailsObj
				.getSessionData(), querySessionData, startIndex, queryDetailsObj.getColumnSize());
		
		List<List<String>> dataList = pagenatedResultData.getResult();
		List<List<String>> listForFileType = dataList;
		querySessionData.setTotalNumberOfRecords(pagenatedResultData.getTotalRecords());
		// if denormalization is on then execute below block else do not execute this block.
		/*
		if(queryDetailsObj.getQuery().getConstraints().size() != 1 && isContPresent 
				&& !queryDetailsObj.getQuery().getIsNormalizedResultQuery()))
		{
			SpreadsheetDenormalizationBizLogic  denormalizationBizLogic =
				new SpreadsheetDenormalizationBizLogic();
			Map<String,Object> exportDetailsMap = denormalizationBizLogic.scanIQuery
			(queryDetailsObj, dataList, selectedColMetadata, querySessionData);
			if(!exportDetailsMap.isEmpty())
			{
				dataList = (List<List<String>>)exportDetailsMap.get("dataList");
				List<String>colList = (List<String>)exportDetailsMap.get("headerList");
				spreadSheetDataMap.put(AQConstants.SPREADSHEET_COLUMN_LIST, colList);

				selectedColMetadata.setActualTotalRecords(pagenatedResultData.getTotalRecords());
			}
		}*/

		for (Long id : queryResultObjectDataBeanMap.keySet())
		{
			QueryResultObjectDataBean queryResultObjectDataBean = queryResultObjectDataBeanMap.get(id);
			if (queryResultObjectDataBean.isClobeType())
			{
				List<String> columnList = (List<String>) spreadSheetDataMap
						.get(AQConstants.SPREADSHEET_COLUMN_LIST);	
				Map<Integer, Integer> fileTypeIndxMap = updateSpreadSheetColumnList(
						columnList, queryResultObjectDataBeanMap,selectedColMetadata.isDefinedView());
				Map exportMetataDataMap = updateDataList(dataList, fileTypeIndxMap);
				spreadSheetDataMap.put(AQConstants.ENTITY_IDS_MAP, exportMetataDataMap
						.get(AQConstants.ENTITY_IDS_MAP));
				spreadSheetDataMap.put(AQConstants.EXPORT_DATA_LIST, exportMetataDataMap
						.get(AQConstants.EXPORT_DATA_LIST));
				break;
			}
		}
		spreadSheetDataMap.put(AQConstants.SPREADSHEET_DATA_LIST, dataList);
		/**
		 * Name: Prafull
		 * Description: Query performance issue. Instead of saving complete query results in session,
		 * result will be fetched for each result page navigation.
		 * object of class QuerySessionData will be saved session, which will contain the required
		 * information for query execution while navigation through query result pages.
		 * saving required query data in Session so that can be used later on while navigation through
		 * result pages using pagination.
		 */
		 
		return querySessionData;
	}

	private StringBuffer modifySqlForDefineView(String selectSql, QueryDetails queryDetailsObj)
	{
		StringBuffer sql = new StringBuffer(selectSql);
		List<String> selectSqlColumnList = getListOfSelectedColumns(selectSql);
			Iterator<OutputTreeDataNode> iterator = queryDetailsObj.getUniqueIdNodesMap().values().iterator();
			while (iterator.hasNext())
			{
				OutputTreeDataNode outputTreeDataNode = iterator.next();
				List<QueryOutputTreeAttributeMetadata> attributes = outputTreeDataNode.getAttributes();
				for (QueryOutputTreeAttributeMetadata attributeMetaData : attributes)
				{
					AttributeInterface attribute = attributeMetaData.getAttribute();
					String sqlColumnName = attributeMetaData.getColumnName().trim();
					if (attribute.getName().equals(AQConstants.IDENTIFIER))
					{
						int index = selectSqlColumnList.indexOf(sqlColumnName);

						if (index < 0)
						{
							QueryCSMUtil.appendColNameToSql(selectSql, sql, sqlColumnName);
							break;
						}
					}
				}			
			}
		return sql;
	}

	private List<String> getListOfSelectedColumns(String selectSql)
	{
		List<String> columnList = new ArrayList<String>();
		String substring = selectSql.substring(selectSql.indexOf("select")+6, selectSql.indexOf("from"));
		StringTokenizer tokenizer = new StringTokenizer(substring,",");
		while(tokenizer.hasMoreTokens())
		{
			columnList.add(tokenizer.nextToken().trim());
		}
		return columnList;
	}
	/**
	 * Populates the appropriate SelectedColumnsMetadata object and
	 * maintains the order of attributes in case of saved query.
	 * @param selectedColumnsMetadata selectedColumnsMetadata
	 * @return selectedColMetadata
	 */
	private SelectedColumnsMetadata handleDefineViewCase(
			SelectedColumnsMetadata selectedColumnsMetadata)
	{
		SelectedColumnsMetadata selectedColMetadata;
		if(selectedColumnsMetadata == null)
		{
			selectedColMetadata = selectedColumnMetaData;
			if(selectedColMetadata.isDefinedView())
			{
				updateSelectedColumnsMetadataForSavedQuery(selectedColMetadata);
			}
		}
		else
		{
			selectedColMetadata = selectedColumnsMetadata;
		}
		return selectedColMetadata;
	}

	public boolean isContainmentPresent(IQuery query)
	{
		boolean isContPresent = false;
		IConstraints constraints = query.getConstraints();
		JoinGraph joinGraph = (JoinGraph) constraints.getJoinGraph();
		for(IExpression expression : constraints)
		{
			if(expression.isVisible())
			{
				List<IExpression> children = joinGraph.getChildrenList(expression);
				for(IExpression child : children)
				{
					IIntraModelAssociation association = (IIntraModelAssociation)
					joinGraph.getAssociation(expression, child);
					if(association.getDynamicExtensionsAssociation().getAssociationType()
							== AssociationType.CONTAINTMENT)
					{
						isContPresent = true;
						break;
					}
				}
			}
			if(isContPresent)
			{
				break;
			}
		}
		return isContPresent;
	}
 
	/**
	 * To set the order of attributes to be displayed on spreadsheet
	 * as that of define view in case of save query.
	 * @param selectedColMetadata selectedColMetadata
	 */
	private void updateSelectedColumnsMetadataForSavedQuery(
			SelectedColumnsMetadata selectedColMetadata)
	{
		List<QueryOutputTreeAttributeMetadata> attributeMetadataList =
			new ArrayList<QueryOutputTreeAttributeMetadata>();
		List<IOutputAttribute> outputAttributeList =
			selectedColMetadata.getSelectedOutputAttributeList();
		for(IOutputAttribute outputAttribute : outputAttributeList)
		{
			for(QueryOutputTreeAttributeMetadata attributeMetadata :
				selectedColMetadata.getSelectedAttributeMetaDataList())
			{
				if(outputAttribute.getAttribute().equals(attributeMetadata.getAttribute()) &&
						outputAttribute.getExpression().getExpressionId() == attributeMetadata.getTreeDataNode().getExpressionId())
				{
					attributeMetadataList.add(attributeMetadata);
					break;
				}
			}
		}
		selectedColMetadata.setSelectedAttributeMetaDataList(attributeMetadataList);
	}

	/**
	 * Gets the column name to determine the number of rows to be displayed per page and
	 * total number of records.
	 * @param selectSql selectSql
	 * @param mainIdColumnIndex mainIdColumnIndex
	 * @return column
	 */
	public String getColumnName(String selectSql, int mainIdColumnIndex)
	{
		List<String> columnList = new ArrayList<String>();
		String substring = selectSql.substring(selectSql.indexOf("select")+6, selectSql.indexOf("from"));
		StringTokenizer tokenizer = new StringTokenizer(substring,",");
		while(tokenizer.hasMoreTokens())
		{
			columnList.add(tokenizer.nextToken());
		}
		String column = selectSql.substring(selectSql.lastIndexOf(' ')+1,selectSql.length());
		if(mainIdColumnIndex != -1)
		{
			column = columnList.get(mainIdColumnIndex);
		}
		return column;
	}
	
	/**
	 * If file type attribute column is present in the spreadsheet view
	 * add the column to the data list.
	 * @param dataList dataList
	 * @param fileTypeIndxMap fileTypeIndexMainEntityIndexMap
	 * @return exportMetataDataMap
	 */
	public static Map updateDataList(List<List<String>> dataList,
			Map<Integer, Integer> fileTypeIndexMainEntityIndexMap)
	{
		Map<Integer, String> entityIdIndexMainEntityIdMap = new HashMap<Integer, String>();
		Map<String, Object> exportMetataDataMap = new HashMap<String, Object>();
		List<List<String>> newDataList = new ArrayList<List<String>>();
		List<String> exportRow = new ArrayList<String>();
		Map<Integer, List> entityIdsMap = new HashMap<Integer, List>();
		int rowNo = 0;
		for (List<String> row : dataList)
		{
			List<String> entityIdsList = new ArrayList<String>();
			exportRow = new ArrayList<String>();
			exportRow.addAll(row);
			for (Iterator<Integer> fileTypeIterator = fileTypeIndexMainEntityIndexMap.keySet()
					.iterator(); fileTypeIterator.hasNext();)
			{
				int fileTypeIndex = fileTypeIterator.next();
				int mainEntityIdIndex = fileTypeIndexMainEntityIndexMap.get(fileTypeIndex);
				String mainEntityId = row.get(mainEntityIdIndex);
				entityIdIndexMainEntityIdMap.put(fileTypeIndex, mainEntityId);
			}
			int fileTypeIndex = 0;
			for (Iterator<Integer> fileTypeIterator = fileTypeIndexMainEntityIndexMap.keySet()
					.iterator(); fileTypeIterator.hasNext();)
			{
				fileTypeIndex = fileTypeIterator.next();
				String mainEntityId = entityIdIndexMainEntityIdMap.get(fileTypeIndex);
				String newColumn = "<img src='images/ic_view_up_file.gif' onclick='javascript:viewSPR(\""
						+ mainEntityId + "\")' alt='click here' style='cursor:pointer;'>";
				String fileName = AQConstants.EXPORT_FILE_NAME_START + mainEntityId + ".txt";
				row.add(fileTypeIndex, newColumn);
				exportRow.add(fileTypeIndex, fileName);
				entityIdsList.add(mainEntityId);
			}
			newDataList.add(exportRow);
			entityIdsMap.put(rowNo, entityIdsList);
			rowNo++;
		}
		exportMetataDataMap.put(AQConstants.ENTITY_IDS_MAP, entityIdsMap);
		exportMetataDataMap.put(AQConstants.EXPORT_DATA_LIST, newDataList);
		return exportMetataDataMap;
	}

	public Map<Integer, Integer> updateSpreadSheetColumnList(List<String> spreadsheetColumnsList,
			Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap)
	{
		 return updateSpreadSheetColumnList(spreadsheetColumnsList, queryResultObjectDataBeanMap, false);
	}
	/**
	 * @param spreadsheetColumnsList spreadsheetColumnsList
	 * @param queryResultObjectDataBeanMap queryResultObjectDataBeanMap
	 * @return fileTypeIndexMainEntityIndexMap
	 */
	public Map<Integer, Integer> updateSpreadSheetColumnList(List<String> spreadsheetColumnsList,
			Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap, boolean isDefineView)
	{
		Map<Integer, String> fileTypeIndexColumnNameMap = new TreeMap<Integer, String>();
		Map<Integer, Integer> fileTypeIndxMap = new TreeMap<Integer, Integer>();
		// Stores all the file type attribute column names of all the entities in the map i.e. indexDisplayNameMap
		for (Long id : queryResultObjectDataBeanMap.keySet())
		{
			QueryResultObjectDataBean queryResultObjectDataBean = queryResultObjectDataBeanMap
					.get(id);
			if (queryResultObjectDataBean.isClobeType())
			{
				Map<Integer, QueryOutputTreeAttributeMetadata> fileTypeAttrMap =
					(Map<Integer, QueryOutputTreeAttributeMetadata>) queryResultObjectDataBean
						.getFileTypeAtrributeIndexMetadataMap();
				int mainEntityIdColumn = queryResultObjectDataBean
						.getMainEntityIdentifierColumnId();
				for (Iterator<Integer> iterator = fileTypeAttrMap.keySet()
						.iterator(); iterator.hasNext();)
				{
					int fileTypeColumnId = iterator.next();
					QueryOutputTreeAttributeMetadata metaData = fileTypeAttrMap
							.get(fileTypeColumnId);
					String displayName = metaData.getDisplayName();
					fileTypeIndexColumnNameMap.put(fileTypeColumnId, displayName);
					fileTypeIndxMap.put(fileTypeColumnId, mainEntityIdColumn);
				}
			}
		}
		if(!isDefineView)
		{
			for (Iterator<Integer> columnNameIterator = fileTypeIndexColumnNameMap.keySet().iterator();
			columnNameIterator.hasNext();)
			{
				populateSpreadsheetColumnList(spreadsheetColumnsList,
						fileTypeIndexColumnNameMap, columnNameIterator);
			}
		}
		return fileTypeIndxMap;
	}

	/**
	 * @param spreadsheetColumnsList spreadsheetColumnsList
	 * @param fileTypeIndexColumnNameMap fileTypeIndexColumnNameMap
	 * @param columnNameIterator columnNameIterator
	 */
	private void populateSpreadsheetColumnList(
			List<String> spreadsheetColumnsList,
			Map<Integer, String> fileTypeIndexColumnNameMap,
			Iterator<Integer> columnNameIterator)
	{
		int index = columnNameIterator.next();
		String displayName = fileTypeIndexColumnNameMap.get(index);
		if (spreadsheetColumnsList.contains(displayName))
		{
			int indexOfColumn = spreadsheetColumnsList.lastIndexOf(displayName);
			if (index == spreadsheetColumnsList.size())
			{
				spreadsheetColumnsList.add(index, displayName);
			}
			else
			{
				if ((indexOfColumn != index)
						&& (!spreadsheetColumnsList.get(index).equals(displayName)))
				{
					spreadsheetColumnsList.add(index, displayName);
				}
			}
		}
		else
		{
			spreadsheetColumnsList.add(index, displayName);
		}
	}

	/**
	 * @param spreadSheetDataMap spreadSheetDataMap
	 * @param node node
	 * @param parentIdColumnName parentIdColumnName
	 * @param parentData parentData
	 * @param tableName tableName
	 * @param queryResultObjectDataBeanMap queryResultObjectDataBeanMap
	 * @param queryDetailsObj queryDetailsObj
	 * @param outputTermsColumns outputTermsColumns
	 * @param specimenMap 
	 * @return resultList
	 */
	private List createSQL(Map spreadSheetDataMap, OutputTreeDataNode node,
			String parentIdColumnName, String parentData, String tableName,
			Map<Long, QueryResultObjectDataBean> beanMap,
			QueryDetails queryDetailsObj, Map<String, IOutputTerm> outputTermsColumns, Map<String, String> specimenMap)
	{
		Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap = beanMap;
		String selectSql = AQConstants.SELECT_DISTINCT;
		List<String> columnsList = new ArrayList<String>();
		String idColumnOfCurrentNode = "";
		QueryResultObjectDataBean queryResultObjectDataBean = queryResultObjectDataBeanMap.get(node
				.getId());
		List<QueryOutputTreeAttributeMetadata> attributes = node.getAttributes();
		List<Integer> identifiedDataColumnIds = new ArrayList<Integer>();
		List<Integer> objectColumnIds = new ArrayList<Integer>();
		List resultList = new ArrayList();
		int columnIndex = 0;
		int addedFileTypeAttributes = 0;
		Map<Integer, QueryOutputTreeAttributeMetadata> fileTypeAttrMap = new
		HashMap<Integer, QueryOutputTreeAttributeMetadata>();
		for (QueryOutputTreeAttributeMetadata attributeMetaData : attributes)
		{
			AttributeInterface attribute = attributeMetaData.getAttribute();
			String sqlColumnName = attributeMetaData.getColumnName();

			if (attribute.getName().equalsIgnoreCase(AQConstants.IDENTIFIER))
			{
				idColumnOfCurrentNode = sqlColumnName;
				queryResultObjectDataBean.setMainEntityIdentifierColumnId(columnIndex);
				queryResultObjectDataBean.setEntityId(columnIndex);
			}
			if (attribute.getIsIdentified() != null && attribute.getIsIdentified())
			{
				identifiedDataColumnIds.add(columnIndex);
				queryResultObjectDataBean.setIdentifiedDataColumnIds(identifiedDataColumnIds);
			}
			if (attribute.getAttributeTypeInformation().getDataType().equalsIgnoreCase(
					AQConstants.FILE_TYPE))
			{
				queryResultObjectDataBean.setClobeType(true);
				int fileTypeIndex = columnIndex + addedFileTypeAttributes;
				fileTypeAttrMap.put(fileTypeIndex, attributeMetaData);
				addedFileTypeAttributes++;
			}
			else
			{
				objectColumnIds.add(columnIndex);
				selectSql = selectSql + sqlColumnName + ",";
				sqlColumnName = sqlColumnName.substring(AQConstants.COLUMN_NAME.length(),
						sqlColumnName.length());
				columnsList.add(attributeMetaData.getDisplayName());
				columnIndex++;
			}
		}
		if (queryResultObjectDataBean.isClobeType())
		{
			queryResultObjectDataBean
					.setFileTypeAtrributeIndexMetadataMap(fileTypeAttrMap);
		}
		if (selectedColumnMetaData.isDefinedView())
		{
			queryResultObjectDataBeanMap = new HashMap<Long, QueryResultObjectDataBean>();
			selectSql = getSQLForSelectedColumns(spreadSheetDataMap, queryResultObjectDataBeanMap,
					queryDetailsObj, outputTermsColumns, parentData,null);
			columnsList = (List<String>) spreadSheetDataMap.get(AQConstants.SPREADSHEET_COLUMN_LIST);
		}
		else
		{
			spreadSheetDataMap.put(AQConstants.SPREADSHEET_COLUMN_LIST, columnsList);
			selectSql = selectSql.substring(0, selectSql.lastIndexOf(','));
			if (!outputTermsColumns.isEmpty())
			{
				IConstraints constraints = queryDetailsObj.getQuery().getConstraints();
				TemporalColumnUIBean temporalColumnUIBean = new TemporalColumnUIBean(node,
						selectSql, columnsList, outputTermsColumns, columnIndex, constraints);
				queryResultObjectDataBean.setTqColumnMetadataList(modifySqlForTemporalColumns
						(temporalColumnUIBean, queryDetailsObj, parentData));
				selectSql = temporalColumnUIBean.getSql();
				columnIndex = temporalColumnUIBean.getColumnIndex();
			}
			selectedColumnMetaData.setSelectedAttributeMetaDataList(attributes);
		}
		if (!selectedColumnMetaData.isDefinedView()
				&& queryResultObjectDataBean.getMainEntityIdentifierColumnId() == -1)
		{
			selectSql = populateQueryForDefaultView(queryDetailsObj, selectSql,
					queryResultObjectDataBean, columnIndex,specimenMap);
		}
		selectSql = selectSql + " from " + tableName;
		if (parentData == null)
		{
			selectSql = selectSql + " where " + idColumnOfCurrentNode + " "
			+ RelationalOperator.getSQL(RelationalOperator.IsNotNull)+" ORDER BY "+idColumnOfCurrentNode;
		}
		else
		{
			selectSql = selectSql + " where (" + parentIdColumnName + " = '" + parentData + "' "
					+ LogicalOperator.And + " " + idColumnOfCurrentNode + " "
					+ RelationalOperator.getSQL(RelationalOperator.IsNotNull) + ") ORDER BY "+idColumnOfCurrentNode;
		}
		if (!identifiedDataColumnIds.isEmpty())
		{
			queryResultObjectDataBean.setHasAssociatedIdentifiedData(true);
		}
		queryResultObjectDataBean.setObjectColumnIds(objectColumnIds);

		resultList.add(selectSql);
		resultList.add(queryResultObjectDataBeanMap);
		return resultList;
	}

	/**
	 * @param queryDetailsObj Query Details Object.
	 * @param selectSql select query
	 * @param queryResultObjectDataBean queryResultObjectDataBean
	 * @param columnIndex columnIndex
	 * @param specimenMap 
	 * @return selectSql
	 */
	private String populateQueryForDefaultView(QueryDetails queryDetailsObj,
			String sql,
			QueryResultObjectDataBean queryResultObjectDataBean, int columnIndex, Map<String, String> specimenMap)
	{
		String selectSql = sql;
		Map<EntityInterface, Integer> entityIdIndexMap = new HashMap<EntityInterface, Integer>();
		selectSql = QueryCSMUtil.updateEntityIdIndexMap(queryResultObjectDataBean, columnIndex,
				selectSql, null, entityIdIndexMap, queryDetailsObj,specimenMap);
		entityIdIndexMap = queryResultObjectDataBean.getEntityIdIndexMap();
		if (entityIdIndexMap.get(queryResultObjectDataBean.getMainEntity()) == null)
		{
			queryResultObjectDataBean.setMainEntityIdentifierColumnId(-1);
		}
		else
		{
			queryResultObjectDataBean.setMainEntityIdentifierColumnId(entityIdIndexMap
					.get(queryResultObjectDataBean.getMainEntity()));
		}
		return selectSql;
	}

	/**
	 * @param idNodesMap Map<Long, OutputTreeDataNode> map of identifiers and nodes present in tree
	 * @param actualParentNodeId string id of parent
	 * @return <CODE>true</CODE> if node is a leafNode,
	 * <CODE>false</CODE>
	 */
	public boolean isLeafNode(Map<String, OutputTreeDataNode> idNodesMap, String actualParentNodeId)
	{
		boolean isLeafNode;
		String[] nodeIds;
		nodeIds = actualParentNodeId.split(AQConstants.UNDERSCORE);
		String treeNo = nodeIds[0];
		String parentId = nodeIds[1];
		String uniqueNodeId = treeNo + "_" + parentId;
		OutputTreeDataNode parentNode = idNodesMap.get(uniqueNodeId);
		if (parentNode.getChildren().isEmpty())
		{
			isLeafNode = true;
		}
		else
		{
			isLeafNode = false;
		}
		return isLeafNode;
	}
}