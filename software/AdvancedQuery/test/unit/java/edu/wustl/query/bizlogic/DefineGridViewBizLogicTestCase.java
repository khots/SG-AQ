package edu.wustl.query.bizlogic;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import junit.framework.TestCase;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.QueryTreeNodeData;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.query.queryobject.util.QueryObjectProcessor;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.query.actionForm.CategorySearchForm;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import edu.wustl.query.util.querysuite.QueryDetails;

public class DefineGridViewBizLogicTestCase extends TestCase
{
	public void testGetClassName()
	{
		DefineGridViewBizLogic bizLogic = new DefineGridViewBizLogic();
		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
        IOutputEntity outputEntity = QueryObjectFactory.createOutputEntity(participantEntity);
        outputEntity.getSelectedAttributes().addAll(participantEntity.getEntityAttributesForQuery());
        OutputTreeDataNode outputTreeDataNode = new OutputTreeDataNode(outputEntity, 1, 1);
        String className = bizLogic.getClassName(outputTreeDataNode);
        assertEquals("Expected Class Name","edu.wustl.catissuecore.domain.Participant",className);
	}

	public void testCreateSQLForSelectedColumn()
	{
		String columnNames = "Column2,Column4,Column5,Column6";
		String sql = "select distinct Column0,Column1,Column2,Column3,Column4,Column5,Column6,Column7,Column8,Column9,Column10,Column11 from TEMP_OUTPUTTREE1_86034 where Column4 is NOT NULL";
		DefineGridViewBizLogic gridViewBizLogic = new DefineGridViewBizLogic();
		gridViewBizLogic.createSQLForSelectedColumn(columnNames, sql);
	}

	public void testGetSelectedColumnsMetadata()
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
		CategorySearchForm searchForm = new CategorySearchForm();
		String[] columnNameList = {"1:97","1:99","1:100"};
		searchForm.setSelectedColumnNames(columnNameList);

		List<OutputTreeDataNode> rootOutputTreeNodeList = new ArrayList<OutputTreeDataNode>();
		Map<String, OutputTreeDataNode> uniqueIdNodesMap = new HashMap<String, OutputTreeDataNode>();
		Map<EntityInterface, List<EntityInterface>> mainEntityMap= new HashMap<EntityInterface, List<EntityInterface>>();
		SessionDataBean sessionData= new SessionDataBean();
		sessionData.setAdmin(true);
		sessionData.setCsmUserId("1");
		sessionData.setFirstName("admin");
		sessionData.setLastName("admin");
		sessionData.setSecurityRequired(Boolean.FALSE);
		sessionData.setUserId(2441l);
		sessionData.setUserName("admin@admin.com");

		Map<AttributeInterface, String> attributeColumnNameMap = new HashMap<AttributeInterface, String>();
		Map<String, IOutputTerm> outputTermsColumns = new HashMap<String, IOutputTerm>();
		LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
		IQuery query = GenericQueryGeneratorMock.creatParticipantQuery();

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
        rootOutputTreeNodeList.add(outputTreeDataNode);
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
		uniqueIdNodesMap = QueryObjectProcessor.getAllChildrenNodes(rootOutputTreeNodeList);
        queryDetails.setUniqueIdNodesMap(uniqueIdNodesMap);
		SelectedColumnsMetadata selectedColumnsMetadata = new SelectedColumnsMetadata();
        selectedColumnsMetadata.setCurrentSelectedObject(outputTreeDataNode);
        selectedColumnsMetadata.setDefinedView(true);
        selectedColumnsMetadata.setSelColNVBeanList(null);
        selectedColumnsMetadata.setSelectedAttributeMetaDataList(selectedAttributeMetaDataList);
        selectedColumnsMetadata.setSelectedOutputAttributeList(null);

        DefineGridViewBizLogic gridViewBizLogic = new DefineGridViewBizLogic();
        gridViewBizLogic.getSelectedColumnsMetadata(searchForm, queryDetails, selectedColumnsMetadata, query.getConstraints());
        gridViewBizLogic.getSelectedColumnMetadataForSavedQuery(queryDetails.getUniqueIdNodesMap().values(), selectedColumnsMetadata.getSelectedOutputAttributeList(), selectedColumnsMetadata);

        Vector<QueryTreeNodeData> treeDataVector = new Vector<QueryTreeNodeData>();
        gridViewBizLogic.createTree(searchForm, queryDetails, treeDataVector, outputTreeDataNode, null);
	}

	public void testGetColumnsMetadataForSelectedNodes()
	{
		IQuery query = GenericQueryGeneratorMock.creatParticipantQuery();
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
	        i++;
        }
		SelectedColumnsMetadata selectedColumnsMetadata = new SelectedColumnsMetadata();
        selectedColumnsMetadata.setCurrentSelectedObject(outputTreeDataNode);
        selectedColumnsMetadata.setDefinedView(true);
        selectedColumnsMetadata.setSelColNVBeanList(null);
        selectedColumnsMetadata.setSelectedAttributeMetaDataList(selectedAttributeMetaDataList);
        selectedColumnsMetadata.setSelectedOutputAttributeList(null);
        DefineGridViewBizLogic gridViewBizLogic = new DefineGridViewBizLogic();
        gridViewBizLogic.getColumnsMetadataForSelectedNode(outputTreeDataNode, selectedColumnsMetadata, query.getConstraints());
	}
}
