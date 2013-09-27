package edu.wustl.query.bizlogic;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import junit.framework.TestCase;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import edu.wustl.query.util.querysuite.QueryDetails;
import edu.wustl.security.privilege.PrivilegeType;

public class QueryOutputSpreadsheetBizLogicTestCase extends TestCase
{
	private SessionDataBean getSessionData()
	{
		SessionDataBean sessionData = new SessionDataBean();
		sessionData.setAdmin(true);
		sessionData.setCsmUserId("1");
		sessionData.setFirstName("admin");
		sessionData.setLastName("admin");
		sessionData.setSecurityRequired(Boolean.FALSE);
		sessionData.setUserId(2441l);
		sessionData.setUserName("admin@admin.com");
		return sessionData;
	}
	public void testCreateSpreadsheetData() throws DAOException, ClassNotFoundException
	{
		HttpSession session = new HttpSession()
		{
			public void setMaxInactiveInterval(int arg0)
			{
			}

			public void setAttribute(String arg0, Object arg1)
			{
			}

			public void removeValue(String arg0)
			{
			}

			public void removeAttribute(String arg0)
			{
			}

			public void putValue(String arg0, Object arg1)
			{
			}

			public boolean isNew()
			{
				return false;
			}

			public void invalidate()
			{
			}

			public String[] getValueNames()
			{
				return null;
			}

			public Object getValue(String arg0)
			{
				return null;
			}

			public HttpSessionContext getSessionContext()
			{
				return null;
			}

			public ServletContext getServletContext()
			{
				return null;
			}

			public int getMaxInactiveInterval()
			{
				return 0;
			}

			public long getLastAccessedTime()
			{
				return 0;
			}

			public String getId()
			{
				return null;
			}

			public long getCreationTime()
			{
				return 0;
			}

			public Enumeration getAttributeNames()
			{
				return null;
			}

			public Object getAttribute(String arg0)
			{
				return null;
			}
		};

		List<OutputTreeDataNode> rootOutputTreeNodeList = new ArrayList<OutputTreeDataNode>();
		Map<String, OutputTreeDataNode> uniqueIdNodesMap = new HashMap<String, OutputTreeDataNode>();
		Map<EntityInterface, List<EntityInterface>> mainEntityMap= new HashMap<EntityInterface, List<EntityInterface>>();
		SessionDataBean sessionData= getSessionData();

		Map<AttributeInterface, String> attributeColumnNameMap = new HashMap<AttributeInterface, String>();
		Map<String, IOutputTerm> outputTermsColumns = new HashMap<String, IOutputTerm>();
		LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
		IQuery query = GenericQueryGeneratorMock.createTemporalQueryParticipantCSR();
		List<IOutputTerm> terms = query.getOutputTerms();
		int index=15;
		for(IOutputTerm term : terms)
		{
			outputTermsColumns.put("Column"+index, term);
			index++;
		}
		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
        IOutputEntity outputEntity = QueryObjectFactory.createOutputEntity(participantEntity);
        outputEntity.getSelectedAttributes().addAll(participantEntity.getEntityAttributesForQuery());
        OutputTreeDataNode outputTreeDataNode = new OutputTreeDataNode(outputEntity, 1, 0);
        List<QueryOutputTreeAttributeMetadata> selectedAttributeMetaDataList = new ArrayList<QueryOutputTreeAttributeMetadata>();
        int i=0;
        for(AttributeInterface attribute : participantEntity.getAllAttributes())
        {
	        String className = edu.wustl.query.util.global.Utility.parseClassName(participantEntity.getName());
	        String attributeLabel = edu.wustl.common.util.Utility.getDisplayLabel(attribute.getName());
	        String displayNmForCol = className+" : "+attributeLabel;
	        QueryOutputTreeAttributeMetadata opTreeMetadata = new QueryOutputTreeAttributeMetadata(attribute, "Column"+i, outputTreeDataNode,
	                displayNmForCol);
	        selectedAttributeMetaDataList.add(opTreeMetadata);
	        outputTreeDataNode.addAttribute(opTreeMetadata);
	        attributeColumnNameMap.put(attribute, "Column"+i);
	        i++;
        }
		session.setAttribute(AQConstants.SAVE_TREE_NODE_LIST, rootOutputTreeNodeList);
		session.setAttribute(AQConstants.ID_NODES_MAP, uniqueIdNodesMap);
		session.setAttribute(AQConstants.MAIN_ENTITY_MAP,mainEntityMap);
		session.setAttribute(AQConstants.ATTRIBUTE_COLUMN_NAME_MAP, attributeColumnNameMap);
		session.setAttribute(AQConstants.OUTPUT_TERMS_COLUMNS,outputTermsColumns);
		session.setAttribute(AQConstants.QUERY_OBJECT, query);
		session.setAttribute(AQConstants.COLUMN_VALUE_BEAN,columnValueBean);
		session.setAttribute("AUDIT_EVENT_ID", Long.valueOf("1"));
		session.setAttribute(AQConstants.SESSION_DATA, sessionData);
		QueryDetails queryDetails = new QueryDetails(session);
		queryDetails.setSessionData(sessionData);
		queryDetails.setRandomNumber("_6358");
		queryDetails.setAttributeColumnNameMap(attributeColumnNameMap);
		queryDetails.setQuery(query);

        QueryResultObjectDataBean queryResultObjectDataBean = new QueryResultObjectDataBean();
        queryResultObjectDataBean.setEntity(participantEntity);
        queryResultObjectDataBean.setCsmEntityName(participantEntity.getName());
        queryResultObjectDataBean.setEntityId(0);
        queryResultObjectDataBean.setEntityIdIndexMap(new HashMap<EntityInterface, Integer>());
        queryResultObjectDataBean.setFileTypeAtrributeIndexMetadataMap(new HashMap<Integer, Object>());
        queryResultObjectDataBean.setHasAssociatedIdentifiedData(true);
        queryResultObjectDataBean.setClobeType(false);
        queryResultObjectDataBean.setReadDeniedObject(true);
        queryResultObjectDataBean.setMainEntity(participantEntity);
        queryResultObjectDataBean.setMainEntityIdentifierColumnId(0);
        queryResultObjectDataBean.setPrivilegeType(PrivilegeType.ObjectLevel);
        queryResultObjectDataBean.setTqColumnMetadataList(new ArrayList());
        List<Integer> identifiedDataColumnIds = new ArrayList<Integer>();
        identifiedDataColumnIds.add(1);
        identifiedDataColumnIds.add(3);
        identifiedDataColumnIds.add(5);
        identifiedDataColumnIds.add(6);
        identifiedDataColumnIds.add(12);
        identifiedDataColumnIds.add(14);
        queryResultObjectDataBean.setIdentifiedDataColumnIds(identifiedDataColumnIds);

        List<Integer> objectColumnIds = new ArrayList<Integer>();
        objectColumnIds.add(0);
        objectColumnIds.add(1);
        objectColumnIds.add(2);
        objectColumnIds.add(3);
        objectColumnIds.add(4);
        objectColumnIds.add(5);
        objectColumnIds.add(6);
        objectColumnIds.add(7);
        objectColumnIds.add(8);
        objectColumnIds.add(9);
        objectColumnIds.add(10);
        objectColumnIds.add(11);
        objectColumnIds.add(12);
        objectColumnIds.add(13);
        objectColumnIds.add(14);
        queryResultObjectDataBean.setObjectColumnIds(objectColumnIds);
        Map<EntityInterface,List<List<String>>> entityMainIdListMap = new HashMap<EntityInterface, List<List<String>>>();
        List<List<String>> mainEntityIds = new ArrayList<List<String>>();
        List<String> mainEntityList = new ArrayList<String>();
        mainEntityList.add("1");
        mainEntityIds.add(mainEntityList);
        entityMainIdListMap.put(participantEntity, mainEntityIds);
        Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap = new HashMap<Long, QueryResultObjectDataBean>();
        queryResultObjectDataBeanMap.put(outputTreeDataNode.getId(), queryResultObjectDataBean);

        SelectedColumnsMetadata selectedColumnsMetadata = new SelectedColumnsMetadata();
        selectedColumnsMetadata.setCurrentSelectedObject(outputTreeDataNode);
        selectedColumnsMetadata.setDefinedView(false);
        selectedColumnsMetadata.setSelColNVBeanList(null);
        selectedColumnsMetadata.setSelectedAttributeMetaDataList(selectedAttributeMetaDataList);
        selectedColumnsMetadata.setSelectedOutputAttributeList(null);
        int recordPerPage = 100;
        String treeNo = "zero";

        QueryOutputSpreadsheetBizLogic spreadsheetBizLogic = new QueryOutputSpreadsheetBizLogic();
        spreadsheetBizLogic.createSpreadsheetData(treeNo, outputTreeDataNode, queryDetails, null, recordPerPage, selectedColumnsMetadata, queryResultObjectDataBeanMap, false, query.getConstraints(), outputTermsColumns);

        spreadsheetBizLogic.getRecordsPerPage(queryDetails, "Column1", 100);
	}

	public void testGetColumnName()
	{
		String sql = "Select Column1,Column2,Column3,Column4 from catissue_participant";
		int mainIdColumnIndex = 1;
		new QueryOutputSpreadsheetBizLogic().getColumnName(sql, mainIdColumnIndex);
	}
}
