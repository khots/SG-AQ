
package edu.wustl.query.util.querysuite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.query.factory.AbstractQueryGeneratorFactory;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.query.queryobject.util.QueryObjectProcessor;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.exceptions.SqlException;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IOutputAttribute;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.impl.OutputAttribute;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.query.bizlogic.CommonQueryBizLogic;
import edu.wustl.query.bizlogic.DefineGridViewBizLogic;
import edu.wustl.query.bizlogic.QueryOutputSpreadsheetBizLogic;
import edu.wustl.query.generator.ISqlGenerator;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.Utility;
import edu.wustl.security.exception.SMException;

/**
 * @author santhoshkumar_c
 *
 */
public class QueryModuleSearchQueryUtil
{
	/**
	 * Logger.
	 */
	private static final org.apache.log4j.Logger LOGGER = LoggerConfig
			.getConfiguredLogger(QueryModuleSearchQueryUtil.class);

	private final HttpServletRequest request;
	private final HttpSession session;
	private IQuery query;
	private final boolean isSavedQuery;

	private final QueryDetails queryDetailsObj;

	/**
	 * @param request
	 * @param query
	 */
	public QueryModuleSearchQueryUtil(HttpServletRequest request, IQuery query)
	{
		this.request = request;
		session = request.getSession();
		this.query = query;

		isSavedQuery = Boolean.valueOf((String) session.getAttribute(AQConstants.IS_SAVED_QUERY));
		queryDetailsObj = new QueryDetails(session);
	}

	/**
	 * This method extracts query object and forms results for tree and grid.
	 * @param option
	 * @return status
	 */
	public QueryModuleError searchQuery()
	{
		session.removeAttribute(AQConstants.HYPERLINK_COLUMN_MAP);
		QueryModuleError status = QueryModuleError.SUCCESS;
		IQuery newQuery = null;
		try
		{
			session.setAttribute(AQConstants.QUERY_OBJECT, query);
			boolean alreadySavedQuery = Boolean.valueOf((String)session.getAttribute(AQConstants.SAVED_QUERY));
			if (isSavedQuery && !alreadySavedQuery)
			{
				newQuery = processSaveQuery();
			}
			if (queryDetailsObj.getSessionData() != null)
			{
				modifyQuery(newQuery);
			}
		}
		catch (QueryModuleException e)
		{
			status = e.getKey();
			if(!ifValidQuery())
			{
				status = QueryModuleError.INVALID_METADATA;
			}
			LOGGER.error(e.getMessage(),e);
		}
		return status;
	}

	protected boolean ifValidQuery() {
		for (IExpression expression : query.getConstraints()) {
			String entityName = expression.getQueryEntity()
			.getDynamicExtensionsEntity().getName();
			if(AQConstants.TISSUE_SPECIMEN.equalsIgnoreCase(entityName) ||
					AQConstants.CELL_SPECIMEN.equalsIgnoreCase(entityName) ||
					AQConstants.MOLECULAR_SPECIMEN.equalsIgnoreCase(entityName) ||
					AQConstants.FLUID_SPECIMEN.equalsIgnoreCase(entityName) )
			{
				return false;
			}
		}
		return true;
		}

	/**
	 * @param newQuery new query
	 * @throws QueryModuleException QueryModuleException
	 */
	private void modifyQuery(IQuery newQuery) throws QueryModuleException
	{
		IQuery originalQuery=null;
		if(newQuery != null)
		{
			originalQuery = queryDetailsObj.getQuery();
			queryDetailsObj.setQuery(newQuery);
		}
		
		auditQuery();
		boolean hasCondOnIdentifiedField;
		if(newQuery == null)
		{
			hasCondOnIdentifiedField = edu.wustl.query.util.global.Utility
			.isConditionOnIdentifiedField(query);
		}
		else
		{
			queryDetailsObj.setQuery(newQuery);
			hasCondOnIdentifiedField = edu.wustl.query.util.global.Utility
					.isConditionOnIdentifiedField(newQuery);
		}
		setDataInSession(hasCondOnIdentifiedField);
		if(originalQuery != null)
		{
			query = originalQuery;
			session.setAttribute(AQConstants.QUERY_OBJECT, originalQuery);
			queryDetailsObj.setQuery(originalQuery);
		}		
	}

	/**
	 * @param option
	 * @param outputTreeBizLogic
	 * @param hasCondOnIdentifiedField
	 * @throws QueryModuleException
	 * @throws SQLException 
	 */
	private void setDataInSession(boolean hasCondOnIdentifiedField) throws QueryModuleException
	{		 
		QueryModuleException queryModExp;
		OutputTreeDataNode rootOutputnode = queryDetailsObj.getRootOutputTreeNodeList().get(0);
		try
		{	
			//int count = QueryModuleSqlUtil.getCountForQuery(queryDetailsObj.getSaveGeneratedQuery(), queryDetailsObj);
			updateQueryAuditDetails(rootOutputnode, 0, queryDetailsObj.getAuditEventId());
			processRecords(queryDetailsObj, rootOutputnode, hasCondOnIdentifiedField);	
		} 
		catch (DAOException e)
		{
			queryModExp = new QueryModuleException(e.getMessage(), QueryModuleError.DAO_EXCEPTION);
			throw queryModExp;
		}
		catch (SQLException e)
		{
			queryModExp = new QueryModuleException(e.getMessage(), QueryModuleError.SQL_EXCEPTION);
			throw queryModExp;
		}
	}

	/**
	 * @return outputTreeBizLogic
	 * @throws QueryModuleException
	 */
	private void auditQuery() throws QueryModuleException
	{
	    CommonQueryBizLogic queryBizLogic = new CommonQueryBizLogic();
		QueryModuleException queryModExp;		
		try
		{
			String selectSql = (String) session.getAttribute(AQConstants.SAVE_GENERATED_SQL);
			long auditEventId = getAuditEventId(queryBizLogic, selectSql);
			boolean alreadySavedQuery = Boolean.valueOf((String)session.getAttribute("savedQuery"));
			queryDetailsObj.setAuditEventId(auditEventId);
			getNewQuery(selectSql, alreadySavedQuery);			
		}
		catch (DAOException e)
		{
			queryModExp = new QueryModuleException(e.getMessage(), QueryModuleError.DAO_EXCEPTION);
			throw queryModExp;
		}
		catch (SQLException e)
		{
			queryModExp = new QueryModuleException(e.getMessage(), QueryModuleError.SQL_EXCEPTION);
			throw queryModExp;
		}
		catch (MultipleRootsException e)
		{
			queryModExp = new QueryModuleException(e.getMessage(), QueryModuleError.MULTIPLE_ROOT);
			throw queryModExp;
		}
		catch (SqlException e)
		{
			queryModExp = new QueryModuleException(e.getMessage(), QueryModuleError.SQL_EXCEPTION);
			throw queryModExp;
		}
		catch (RuntimeException e)
		{
			queryModExp = new QueryModuleException(e.getMessage(), QueryModuleError.SQL_EXCEPTION);
			throw queryModExp;
		}
	}

	/**
	 * @param queryBizLogic queryBizLogic
	 * @param selectSql SQL
	 * @return auditEventId
	 * @throws DAOException DAOException
	 * @throws SQLException SQLException
	 */
	private long getAuditEventId(CommonQueryBizLogic queryBizLogic,
			String selectSql) throws DAOException, SQLException {
		long auditEventId;
		if(session.getAttribute(AQConstants.AUDIT_EVENT_ID)== null)
		{
			auditEventId = queryBizLogic.insertQuery(selectSql, queryDetailsObj.getSessionData()); 
			QueryModuleSqlUtil.updateAuditQueryDetails(AQConstants.QUERY_ID, query.getId().toString(), auditEventId);
			session.setAttribute(AQConstants.AUDIT_EVENT_ID,auditEventId);
		}
		else
		{
			auditEventId = (Long)session.getAttribute(AQConstants.AUDIT_EVENT_ID);
		}
		return auditEventId;
	}

	/**
	 * @param outputTreeBizLogic outputTreeBizLogic
	 * @param selectSql query
	 * @param alreadySavedQuery alreadySavedQuery
	 * @throws MultipleRootsException MultipleRootsException
	 * @throws SqlException SqlException
	 * @throws DAOException DAOException
	 * @throws SQLException SQLException
	 */
	private void getNewQuery(String selectQuery, boolean alreadySavedQuery)
			throws MultipleRootsException, SqlException, DAOException,
			SQLException
	{
		String selectSql = selectQuery;
		if(isSavedQuery && !alreadySavedQuery)
		{
			String newSql;
			ISqlGenerator sqlGenerator = AbstractQueryGeneratorFactory.getDefaultQueryGenerator();
			if(queryDetailsObj.getSessionData().isSecurityRequired())
			{
				newSql = sqlGenerator.generateSQL(queryDetailsObj.getQuery());
				queryDetailsObj.setColumnValueBean(sqlGenerator.getColumnValueBean());
			}
			else
			{
				newSql = sqlGenerator.generateSQL(query);
			}
			selectSql = newSql;	
		}
		
		int cnt = 0;
		while (selectSql.contains("?")) {
			String value = "'"+queryDetailsObj.getColumnValueBean().get(cnt++).getColumnName()+"'";
			selectSql = selectSql.replaceFirst("\\?", value);//modifying sql for inserting variables in where clause
		}
		queryDetailsObj.setSaveGeneratedQuery(selectSql);
		session.setAttribute(AQConstants.SAVE_GENERATED_SQL, selectSql);
		Map<String, String> columnNameVsAliasMap = Utility.splitQuery(selectSql);
		session.setAttribute("columnNameVsAliasMap", columnNameVsAliasMap);
		queryDetailsObj.setColumnNameVsAliasMap(columnNameVsAliasMap);
	}

	/**
	 * @throws QueryModuleException
	 */
	private IQuery processSaveQuery() throws QueryModuleException
	{
		ISqlGenerator sqlGenerator = AbstractQueryGeneratorFactory.getDefaultQueryGenerator();
		validateQuery(sqlGenerator);
		List<OutputTreeDataNode> rootOutputTreeNodeList = sqlGenerator.getRootOutputTreeNodeList();
		Map<AttributeInterface, String> attributeColumnNameMap = sqlGenerator.getAttributeColumnNameMap();
		
		session.setAttribute(AQConstants.SAVE_TREE_NODE_LIST, rootOutputTreeNodeList);		
		queryDetailsObj.setRootOutputTreeNodeList(rootOutputTreeNodeList);
		queryDetailsObj.setColumnValueBean(sqlGenerator.getColumnValueBean());
		queryDetailsObj.setAttributeColumnNameMap(attributeColumnNameMap);
		Map<String, OutputTreeDataNode> uniqueIdNodesMap = QueryObjectProcessor
		.getAllChildrenNodes(rootOutputTreeNodeList);
		queryDetailsObj.setUniqueIdNodesMap(uniqueIdNodesMap);
		session.setAttribute(AQConstants.ID_NODES_MAP, uniqueIdNodesMap);
		IQuery newQuery=null;
		boolean alreadySavedQuery=false;
		alreadySavedQuery = setSessionAttrForSavedQuery(alreadySavedQuery);
		if(queryDetailsObj.getSessionData().isSecurityRequired() && !alreadySavedQuery)
		{
			newQuery = QueryCSMUtil
				.returnQueryClone(query, request.getSession(), queryDetailsObj);
		}
		if(alreadySavedQuery)
		{
			session.setAttribute(AQConstants.SAVED_QUERY, AQConstants.FALSE);
			session.setAttribute(AQConstants.PROCESSED_SAVED_QUERY, AQConstants.FALSE);
		}
		return newQuery;
	}

	/**
	 * @param alreadySavedQuery alreadySavedQuery
	 * @return
	 */
	private boolean setSessionAttrForSavedQuery(boolean savedQuery)
	{
		boolean alreadySavedQuery = savedQuery;
		if(session.getAttribute(AQConstants.SAVED_QUERY) != null)
		{
			alreadySavedQuery = Boolean.valueOf((String)session.getAttribute(AQConstants.SAVED_QUERY));
			if(alreadySavedQuery)
			{
				session.setAttribute(AQConstants.PROCESSED_SAVED_QUERY, AQConstants.TRUE);
			}
		}
		return alreadySavedQuery;
	}
	/**
	 * Calls sqlGenerator which validates IQuery object and throws appropriate exceptions
	 * @param sqlGenerator sqlGenerator object
	 * @throws QueryModuleException Exception e
	 */
	private void validateQuery(ISqlGenerator sqlGenerator)
			throws QueryModuleException
	{
		QueryModuleException queryModExp;
		try
		{
			session.setAttribute(AQConstants.SAVE_GENERATED_SQL, sqlGenerator.generateSQL(query));
			Map<AttributeInterface, String> attributeColumnNameMap = sqlGenerator.getAttributeColumnNameMap();
			session.setAttribute(AQConstants.ATTRIBUTE_COLUMN_NAME_MAP, attributeColumnNameMap);
			session.setAttribute(AQConstants.OUTPUT_TERMS_COLUMNS, sqlGenerator.getOutputTermsColumns());
			session.setAttribute(AQConstants.COLUMN_VALUE_BEAN, sqlGenerator.getColumnValueBean());
		}
		catch (MultipleRootsException e)
		{
			queryModExp = new QueryModuleException(e.getMessage(), QueryModuleError.MULTIPLE_ROOT);
			throw queryModExp;
		}
		catch (SqlException e)
		{
			queryModExp = new QueryModuleException(e.getMessage(), QueryModuleError.SQL_EXCEPTION);
			throw queryModExp;
		}
		catch (DAOException e)
		{
			queryModExp = new QueryModuleException(e.getMessage(), QueryModuleError.SQL_EXCEPTION);
			throw queryModExp;
		}
		catch (RuntimeException e)
		{
			queryModExp = new QueryModuleException(e.getMessage(), QueryModuleError.SQL_EXCEPTION);
			throw queryModExp;
		}
	}

	/**
	 * @param QueryDetailsObj details of query object
	 * @param node OutputTreeDataNode
	 * @param hasCondOnIdentifiedField required for CSM filtering
	 * @throws QueryModuleException exception
	 */
	public void processRecords(QueryDetails QueryDetailsObj, OutputTreeDataNode node,
			boolean hasCondOnIdentifiedField) throws QueryModuleException
	{
		SelectedColumnsMetadata selectedColumnsMetadata = getAppropriateSelectedColumnMetadata(
				query, (SelectedColumnsMetadata) session.getAttribute(AQConstants.SELECTED_COLUMN_META_DATA));
		selectedColumnsMetadata.setCurrentSelectedObject(node);
		QueryModuleException queryModExp;
		int recordsPerPage = setFetchRecordSize();
		checkForSavedQuery(QueryDetailsObj, selectedColumnsMetadata);

		QueryResultObjectDataBean queryResulObjectDataBean = QueryCSMUtil
																.getQueryResulObjectDataBean(node, QueryDetailsObj);
		Map<Long, QueryResultObjectDataBean> queryResultObjDataBeanMap = new HashMap<Long, QueryResultObjectDataBean>();
		queryResultObjDataBeanMap.put(node.getId(), queryResulObjectDataBean);
		QueryOutputSpreadsheetBizLogic outputSpreadsheetBizLogic = new QueryOutputSpreadsheetBizLogic();
		try
		{
			ISqlGenerator sqlGenerator = AbstractQueryGeneratorFactory.getDefaultQueryGenerator();
			Map<String, IOutputTerm> outputTermsColumns = populateOutputTerms(sqlGenerator);
			session.setAttribute("temporalColumnSize", outputTermsColumns.size());
			session.setAttribute(AQConstants.OUTPUT_TERMS_COLUMNS, outputTermsColumns);
			Map<String, List<String>> spreadSheetDatamap = outputSpreadsheetBizLogic
					.createSpreadsheetData(AQConstants.TREENO_ZERO, node, QueryDetailsObj, null,
							recordsPerPage, selectedColumnsMetadata, queryResultObjDataBeanMap,
							hasCondOnIdentifiedField, query.getConstraints(), outputTermsColumns,null);
			setQuerySessionData(selectedColumnsMetadata, spreadSheetDatamap);
		}
		catch (DAOException e)
		{
			queryModExp = new QueryModuleException(e.getMessage(), QueryModuleError.DAO_EXCEPTION);
			throw queryModExp;
		}
		catch (ClassNotFoundException e)
		{
			queryModExp = new QueryModuleException(e.getMessage(), QueryModuleError.CLASS_NOT_FOUND);
			throw queryModExp;
		}
	}

	/**
	 * @param sqlGenerator sqlGenerator
	 * @return outputTermsColumns
	 */
	private Map<String, IOutputTerm> populateOutputTerms(
			ISqlGenerator sqlGenerator)
			{
		Map<String, IOutputTerm> outputTermsColumns = sqlGenerator.getOutputTermsColumns();
		if (outputTermsColumns == null)
		{
			outputTermsColumns = (Map<String, IOutputTerm>) session
					.getAttribute(AQConstants.OUTPUT_TERMS_COLUMNS);
		}
		return outputTermsColumns;
	}

	/**
	 * @param QueryDetailsObj QueryDetailsObj
	 * @param selectedColumnsMetadata selectedColumnsMetadata
	 */
	private void checkForSavedQuery(QueryDetails QueryDetailsObj,
			SelectedColumnsMetadata selectedColumnsMetadata)
	{
		if (query.getId() != null && isSavedQuery)
		{
			getSelectedColumnsMetadata(QueryDetailsObj, selectedColumnsMetadata);
		}
	}

	/**
	 * @param query query
	 * @param columnsMetadata columnsMetadata
	 * @return selectedColumnsMetadata
	 */
	private static SelectedColumnsMetadata getAppropriateSelectedColumnMetadata(IQuery query,
			SelectedColumnsMetadata columnsMetadata)
	{
		SelectedColumnsMetadata selectedColumnsMetadata = columnsMetadata;
		boolean isQueryChanged = false;
		if (query != null && selectedColumnsMetadata != null)
		{
			List<Integer> expressionIdsInQuery = new ArrayList<Integer>();
			IConstraints constraints = query.getConstraints();
			List<QueryOutputTreeAttributeMetadata> selAttributeMetaDataList = selectedColumnsMetadata
					.getSelectedAttributeMetaDataList();
			populateExpressionIds(expressionIdsInQuery, constraints);
			isQueryChanged = isQueryChanged(expressionIdsInQuery, selAttributeMetaDataList,selectedColumnsMetadata);
		}
		selectedColumnsMetadata = setDefinedViewInMetadata(selectedColumnsMetadata, isQueryChanged);
		return selectedColumnsMetadata;
	}

	/**
	 * @param columnsMetadata columnsMetadata
	 * @param isQueryChanged isQueryChanged
	 * @return selectedColumnsMetadata
	 */
	private static SelectedColumnsMetadata setDefinedViewInMetadata(
			SelectedColumnsMetadata columnsMetadata,
			boolean isQueryChanged)
	{
		SelectedColumnsMetadata selectedColumnsMetadata = columnsMetadata;
		if (isQueryChanged || selectedColumnsMetadata == null)
		{
			selectedColumnsMetadata = new SelectedColumnsMetadata();
			selectedColumnsMetadata.setDefinedView(false);
		}
		return selectedColumnsMetadata;
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
	 * @param expressionIdsInQuery expressionIdsInQuery
	 * @param selAttributeMetaDataList selAttributeMetaDataList
	 * @param selectedColumnsMetadata
	 * @return isQueryChanged
	 */
	private static boolean isQueryChanged(List<Integer> expressionIdsInQuery,
			List<QueryOutputTreeAttributeMetadata> selAttributeMetaDataList, SelectedColumnsMetadata selectedColumnsMetadata)
	{
		boolean isQueryChanged = false;
		int expressionId;
		if(expressionIdsInQuery.size() != selectedColumnsMetadata.getNoOfExpr())
		{
			isQueryChanged = true;
		}
		else
		{
			for (QueryOutputTreeAttributeMetadata element : selAttributeMetaDataList)
			{
				expressionId = element.getTreeDataNode().getExpressionId();
				if (!expressionIdsInQuery.contains(Integer.valueOf(expressionId)))
				{
					isQueryChanged = true;
					break;
				}
			}
		}
		return isQueryChanged;
	}

	/** It will set the results per page.
	 * @return recordsPerPage
	 */
	private void setRecordsPerPage()
	{		
		String recordsPerPgSessionValue = (String) session.getAttribute(AQConstants.RESULTS_PER_PAGE);
		if (recordsPerPgSessionValue == null)
		{
			recordsPerPgSessionValue = XMLPropertyHandler.getValue(AQConstants.RECORDS_PER_PAGE_PROPERTY_NAME);
			session.setAttribute(AQConstants.RESULTS_PER_PAGE, recordsPerPgSessionValue);
		}		
	}
	
	private int setFetchRecordSize()
	{
		setRecordsPerPage();
		String fetchDataSize = XMLPropertyHandler.getValue(AQConstants.FETCH_RECORD_SIZE_PROPERTY_NAME);
		session.setAttribute(AQConstants.FETCH_RECORD_SIZE, fetchDataSize);
		return Integer.parseInt(fetchDataSize);
	}

	/**
	 * Set the data in session.
	 * @param selectedColumnsMetadata
	 * @param spreadSheetDatamap
	 */
	public void setQuerySessionData(SelectedColumnsMetadata selectedColumnsMetadata,
			Map<String, List<String>> spreadSheetDatamap)
	{
		QuerySessionData querySessionData = (QuerySessionData) spreadSheetDatamap
				.get(AQConstants.QUERY_SESSION_DATA);
		int totalNoOfRecords = querySessionData.getTotalNumberOfRecords();
		session.setAttribute(AQConstants.QUERY_SESSION_DATA, querySessionData);
		session.setAttribute(AQConstants.TOTAL_RESULTS, Integer.valueOf(totalNoOfRecords));
		//		QueryShoppingCart cart = (QueryShoppingCart)session.getAttribute(Constants.QUERY_SHOPPING_CART);
		//		String message = QueryModuleUtil.getMessageIfIdNotPresentForOrderableEntities(
		//				selectedColumnsMetadata, cart);
		//		session.setAttribute(Constants.VALIDATION_MESSAGE_FOR_ORDERING, message);
		session.setAttribute(AQConstants.PAGINATION_DATA_LIST, spreadSheetDatamap
				.get(AQConstants.SPREADSHEET_DATA_LIST));
		session.setAttribute(AQConstants.SPREADSHEET_COLUMN_LIST, spreadSheetDatamap
				.get(AQConstants.SPREADSHEET_COLUMN_LIST));
		session.setAttribute(AQConstants.SELECTED_COLUMN_META_DATA, spreadSheetDatamap
				.get(AQConstants.SELECTED_COLUMN_META_DATA));
		session.setAttribute(AQConstants.QUERY_REASUL_OBJECT_DATA_MAP, spreadSheetDatamap
				.get(AQConstants.QUERY_REASUL_OBJECT_DATA_MAP));
		session.setAttribute(AQConstants.DEFINE_VIEW_RESULT_MAP,
				spreadSheetDatamap.get(AQConstants.DEFINE_VIEW_RESULT_MAP));
		//session.setAttribute(AQConstants.DENORMALIZED_LIST, spreadSheetDatamap.get(AQConstants.SPREADSHEET_DATA_LIST));
		
	}

	/**
	 * @param QueryDetailsObj
	 * @param selectedColumnsMetadata
	 */
	public void getSelectedColumnsMetadata(QueryDetails QueryDetailsObj,
			SelectedColumnsMetadata selectedColumnsMetadata)
	{
		OutputTreeDataNode currentSelectedObject = selectedColumnsMetadata.getCurrentSelectedObject();
		List<IOutputAttribute> selAttributeList;
		boolean isDefinedView = true;
		List<IOutputAttribute> selectedOutputAttributeList = new ArrayList<IOutputAttribute>();
		List<Integer> expressionIdsInQuery = new ArrayList<Integer>();
		populateExpressionIds(expressionIdsInQuery, query.getConstraints());
		selectedColumnsMetadata.setNoOfExpr(expressionIdsInQuery.size());
		if (query instanceof ParameterizedQuery)
		{
			ParameterizedQuery savedQuery = (ParameterizedQuery) query;
			selAttributeList = savedQuery.getOutputAttributeList();
			if (!selAttributeList.isEmpty())
			{
				DefineGridViewBizLogic gridViewBizLogic = new DefineGridViewBizLogic();
				if(selectedColumnsMetadata.getSelectedOutputAttributeList() == null)
				{
					selectedColumnsMetadata.setSelectedOutputAttributeList(selAttributeList);
				}
				else if(selectedColumnsMetadata.getSelectedOutputAttributeList().isEmpty())
				{
					isDefinedView = populateSelectedColMetadata(
							selectedColumnsMetadata, currentSelectedObject,
							selectedOutputAttributeList);
				}
				gridViewBizLogic.getSelectedColumnMetadataForSavedQuery(QueryDetailsObj
						.getUniqueIdNodesMap().values(), selectedColumnsMetadata.getSelectedOutputAttributeList(), selectedColumnsMetadata);
				setDefinedView(selectedColumnsMetadata, isDefinedView);
			}
		}
	}

	/**
	 * @param selectedColumnsMetadata selectedColumnsMetadata
	 * @param isDefinedView isDefinedView
	 */
	private void setDefinedView(
			SelectedColumnsMetadata selectedColumnsMetadata,
			boolean isDefinedView)
	{
		if(isDefinedView)
		{
			selectedColumnsMetadata.setDefinedView(true);
		}
	}

	/**
	 * @param selectedColumnsMetadata selectedColumnsMetadata
	 * @param currentSelectedObject currentSelectedObject
	 * @param selectedOutputAttributeList selectedOutputAttributeList
	 * @return isDefinedView
	 */
	private boolean populateSelectedColMetadata(
			SelectedColumnsMetadata selectedColumnsMetadata,
			OutputTreeDataNode currentSelectedObject,
			List<IOutputAttribute> selectedOutputAttributeList)
	{
		boolean isDefinedView;
		AttributeInterface attribute;
		OutputAttribute attr;
		for (QueryOutputTreeAttributeMetadata metadata : selectedColumnsMetadata.getSelectedAttributeMetaDataList())
		{
			attribute = metadata.getAttribute();
			attr = new OutputAttribute(query.getConstraints().getExpression(currentSelectedObject
					.getExpressionId()), attribute);
			selectedOutputAttributeList.add(attr);
		}
		selectedColumnsMetadata.setSelectedOutputAttributeList(selectedOutputAttributeList);
		selectedColumnsMetadata.setDefinedView(false);
		isDefinedView = false;
		return isDefinedView;
	}
	
	private void updateQueryAuditDetails(OutputTreeDataNode root, int cntOfRootRecs,
    		long auditEventId) throws DAOException, SQLException
    {
		String rootEntityName = root.getOutputEntity().getDynamicExtensionsEntity().getName();
		QueryModuleSqlUtil.updateAuditQueryDetails(AQConstants.ROOT_ENTITY_NAME,
				rootEntityName, auditEventId);
		QueryModuleSqlUtil.updateAuditQueryDetails(AQConstants.COUNT_OF_ROOT_RECORDS,
				Integer.toString(cntOfRootRecs), auditEventId);
	}
}
