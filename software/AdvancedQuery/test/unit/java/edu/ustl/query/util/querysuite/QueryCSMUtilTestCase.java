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

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.query.queryobject.util.QueryObjectProcessor;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import edu.wustl.query.util.querysuite.QueryCSMUtil;
import edu.wustl.query.util.querysuite.QueryDetails;
import junit.framework.TestCase;

public class QueryCSMUtilTestCase extends TestCase
{
	public void testReturnQueryClone()
	{
		HttpSession session = new HttpSession() {

			public void setMaxInactiveInterval(int arg0) {
				// TODO Auto-generated method stub

			}

			public void setAttribute(String arg0, Object arg1) {
				// TODO Auto-generated method stub

			}

			public void removeValue(String arg0) {
				// TODO Auto-generated method stub

			}

			public void removeAttribute(String arg0) {
				// TODO Auto-generated method stub

			}

			public void putValue(String arg0, Object arg1) {
				// TODO Auto-generated method stub

			}

			public boolean isNew() {
				// TODO Auto-generated method stub
				return false;
			}

			public void invalidate() {
				// TODO Auto-generated method stub

			}

			public String[] getValueNames() {
				// TODO Auto-generated method stub
				return null;
			}

			public Object getValue(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			public HttpSessionContext getSessionContext() {
				// TODO Auto-generated method stub
				return null;
			}

			public ServletContext getServletContext() {
				// TODO Auto-generated method stub
				return null;
			}

			public int getMaxInactiveInterval() {
				// TODO Auto-generated method stub
				return 0;
			}

			public long getLastAccessedTime() {
				// TODO Auto-generated method stub
				return 0;
			}

			public String getId() {
				// TODO Auto-generated method stub
				return null;
			}

			public long getCreationTime() {
				// TODO Auto-generated method stub
				return 0;
			}

			public Enumeration getAttributeNames() {
				// TODO Auto-generated method stub
				return null;
			}

			public Object getAttribute(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		};

		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface pmiEntity = GenericQueryGeneratorMock.createEntity("ParticipantMedicalIdentifier");
        pmiEntity = GenericQueryGeneratorMock.getEntity(cache, pmiEntity);
        IOutputEntity outputEntity = QueryObjectFactory.createOutputEntity(pmiEntity);
        outputEntity.getSelectedAttributes().addAll(pmiEntity.getEntityAttributesForQuery());
        OutputTreeDataNode outputTreeDataNode = new OutputTreeDataNode(outputEntity, 1, 1);

        for(AttributeInterface attribute : pmiEntity.getAllAttributes())
        {
        	int i=1;
	        String className = edu.wustl.query.util.global.Utility.parseClassName(pmiEntity.getName());
	        String attributeLabel = edu.wustl.common.util.Utility.getDisplayLabel(attribute.getName());
	        String displayNmForCol = className+" : "+attributeLabel;
	        outputTreeDataNode.addAttribute(new QueryOutputTreeAttributeMetadata(attribute, "Column"+i, outputTreeDataNode,
	                displayNmForCol));
	        i++;
        }

		List<OutputTreeDataNode> rootOutputTreeNodeList = new ArrayList<OutputTreeDataNode>();
		rootOutputTreeNodeList.add(outputTreeDataNode);
		Map<String, OutputTreeDataNode> uniqueIdNodesMap = QueryObjectProcessor.getAllChildrenNodes(rootOutputTreeNodeList);
		Map<EntityInterface, List<EntityInterface>> mainEntityMap= new HashMap<EntityInterface, List<EntityInterface>>();
		SessionDataBean sessionData= new SessionDataBean();
		sessionData.setAdmin(true);
		sessionData.setCsmUserId("1");
		sessionData.setFirstName("admin");
		sessionData.setLastName("admin");
		sessionData.setSecurityRequired(Boolean.FALSE);
		sessionData.setUserId(1l);
		sessionData.setUserName("admin@admin.com");

		Map<AttributeInterface, String> attributeColumnNameMap = new HashMap<AttributeInterface, String>();
		Map<String, IOutputTerm> outputTermsColumns = new HashMap<String, IOutputTerm>();
		LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
		IQuery query = GenericQueryGeneratorMock.createPMIQuery();
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
		queryDetails.setRandomNumber("_2040");
		queryDetails.setUniqueIdNodesMap(uniqueIdNodesMap);
		QueryCSMUtil.returnQueryClone(query, session, queryDetails);
		QueryCSMUtil.getQueryResulObjectDataBean(outputTreeDataNode, queryDetails);
	}

	public void testGetAllMainEntities() throws DynamicExtensionsSystemException
	{
		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
        List<EntityInterface> mainEntityList = new ArrayList<EntityInterface>();
        QueryCSMUtil.getAllMainEntities(participantEntity, mainEntityList);
        QueryCSMUtil.getIncomingContainmentAssociations(participantEntity);
	}
}
