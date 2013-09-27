package edu.ustl.query.util.querysuite;

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
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.query.actionForm.CategorySearchForm;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import edu.wustl.query.util.querysuite.QueryDetails;
import edu.wustl.query.util.querysuite.QueryModuleUtil;
import edu.wustl.security.privilege.PrivilegeType;

public class QueryModuleUtilTestCase extends TestCase
{
	public void testGetColumnNamesForSelectPart()
	{
		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
        IOutputEntity outputEntity = QueryObjectFactory.createOutputEntity(participantEntity);
        outputEntity.getSelectedAttributes().addAll(participantEntity.getEntityAttributesForQuery());
        OutputTreeDataNode outputTreeDataNode = new OutputTreeDataNode(outputEntity, 1, 1);

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
		rootOutputTreeNodeList.add(outputTreeDataNode);
		Map<String, OutputTreeDataNode> uniqueIdNodesMap = new HashMap<String, OutputTreeDataNode>();
		Map<EntityInterface, List<EntityInterface>> mainEntityMap= new HashMap<EntityInterface, List<EntityInterface>>();
		SessionDataBean sessionData= new SessionDataBean();
		sessionData.setAdmin(true);
		sessionData.setCsmUserId("1");
		sessionData.setFirstName("admin");
		sessionData.setIpAddress("10.88.199.11");
		sessionData.setLastName("admin");
		sessionData.setSecurityRequired(false);
		sessionData.setUserId(1l);
		sessionData.setUserName("admin@admin.com");

		Map<AttributeInterface, String> attributeColumnNameMap = new HashMap<AttributeInterface, String>();
		Map<String, IOutputTerm> outputTermsColumns = new HashMap<String, IOutputTerm>();
		LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
		IQuery query = GenericQueryGeneratorMock.creatParticipantQuery();
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
        identifiedDataColumnIds.add(4);
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

        for(AttributeInterface attribute : participantEntity.getAllAttributes())
        {
        	int i=1;
	        String className = edu.wustl.query.util.global.Utility.parseClassName(participantEntity.getName());
	        String attributeLabel = edu.wustl.common.util.Utility.getDisplayLabel(attribute.getName());
	        String displayNmForCol = className+" : "+attributeLabel;
	        outputTreeDataNode.addAttribute(new QueryOutputTreeAttributeMetadata(attribute, "Column"+i, outputTreeDataNode,
	                displayNmForCol));
	        i++;
        }
        QueryModuleUtil.getColumnNamesForSelectpart(outputTreeDataNode.getAttributes(), queryDetails, queryResultObjectDataBean);
        queryDetails.setRootOutputTreeNodeList(rootOutputTreeNodeList);
        QueryModuleUtil.getRootNodeOfTree(queryDetails, "1");
        QueryModuleUtil.getParentIdColumnName(outputTreeDataNode);
        session.removeAttribute(AQConstants.RANDOM_NUMBER);
        String randomNumber = QueryModuleUtil.generateRandomNumber(session);
        assertNotNull(randomNumber);
        queryResultObjectDataBean.getPrivilegeType();
        queryResultObjectDataBean.isHasAssociatedIdentifiedData();
        queryResultObjectDataBean.getEntityId();
        queryResultObjectDataBean.getFileTypeAtrributeIndexMetadataMap();
        queryResultObjectDataBean.getEntityIdIndexMap();
        queryResultObjectDataBean.getObjectColumnIds();
        queryResultObjectDataBean.isMainEntity();
        queryResultObjectDataBean.isClobeType();
	}

	public void testIsPresentInArray()
	{
		String attributeName = "firstName";
		boolean isPresent = QueryModuleUtil.isPresentInArray(attributeName, AQConstants.ATTR_NAME_TREENODE_LBL);
		assertEquals("Is present in array",true,isPresent);
	}

	public void testSetDefaultSelections()
	{
		CategorySearchForm searchForm = new CategorySearchForm();
		searchForm = QueryModuleUtil.setDefaultSelections(searchForm);
	}
}
