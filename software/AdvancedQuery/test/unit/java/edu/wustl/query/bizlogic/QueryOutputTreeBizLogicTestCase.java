package edu.wustl.query.bizlogic;

import java.sql.SQLException;
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
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.query.queryobject.util.QueryObjectProcessor;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import edu.wustl.query.util.querysuite.QueryDetails;
import edu.wustl.security.exception.SMException;

import junit.framework.TestCase;

public class QueryOutputTreeBizLogicTestCase extends TestCase
{
	private SessionDataBean getSessionData()
	{
		SessionDataBean sessionData= new SessionDataBean();
		sessionData.setAdmin(true);
		sessionData.setCsmUserId("1");
		sessionData.setFirstName("admin");
		sessionData.setLastName("admin");
		sessionData.setSecurityRequired(Boolean.FALSE);
		sessionData.setUserId(2441l);
		sessionData.setUserName("admin@admin.com");
		return sessionData;
	}
	public void testCreateDefaultOutputTreeData() throws DAOException, BizLogicException, SMException, ClassNotFoundException, SQLException
	{
		int treeNo = 0;
		HttpSession session = new HttpSession()
		{
			public void setMaxInactiveInterval(int arg0) {
			}

			public void setAttribute(String arg0, Object arg1) {
		}

			public void removeValue(String arg0) {
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

		List<OutputTreeDataNode> rootOutputTreeNodeList = new ArrayList<OutputTreeDataNode>();
		Map<String, OutputTreeDataNode> uniqueIdNodesMap = new HashMap<String, OutputTreeDataNode>();
		Map<EntityInterface, List<EntityInterface>> mainEntityMap= new HashMap<EntityInterface, List<EntityInterface>>();
		SessionDataBean sessionData= getSessionData();

		Map<AttributeInterface, String> attributeColumnNameMap = new HashMap<AttributeInterface, String>();
		Map<String, IOutputTerm> outputTermsColumns = new HashMap<String, IOutputTerm>();
		LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
		IQuery query = GenericQueryGeneratorMock.createParticipantFNameNotNullQuery();
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

		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
        IOutputEntity outputEntity = QueryObjectFactory.createOutputEntity(participantEntity);
        outputEntity.getSelectedAttributes().addAll(participantEntity.getEntityAttributesForQuery());
        OutputTreeDataNode outputTreeDataNode = new OutputTreeDataNode(outputEntity, 1, 0);
        int i=0;
        for(AttributeInterface attribute : participantEntity.getAllAttributes())
        {
	        String className = edu.wustl.query.util.global.Utility.parseClassName(participantEntity.getName());
	        String attributeLabel = edu.wustl.common.util.Utility.getDisplayLabel(attribute.getName());
	        String displayNmForCol = className+" : "+attributeLabel;
	        outputTreeDataNode.addAttribute(new QueryOutputTreeAttributeMetadata(attribute, "Column"+i, outputTreeDataNode,
	                displayNmForCol));
	        i++;
        }

		QueryOutputTreeBizLogic treeBizLogic = new QueryOutputTreeBizLogic();
		treeBizLogic.createDefaultOutputTreeData(treeNo, outputTreeDataNode, false, queryDetails);

	       try
			{
				String appName=CommonServiceLocator.getInstance().getAppName();
			    IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
	            JDBCDAO jdbcDao = daoFactory.getJDBCDAO();
				jdbcDao.openSession(sessionData);
				jdbcDao.deleteTable("TEMP_OUTPUTTREE1_2040");
				jdbcDao.commit();
				jdbcDao.closeSession();
			}
			catch (DAOException ex)
			{
				ex.printStackTrace();
			}
			queryDetails.setRandomNumber("_2040");
			sessionData.setUserId(1l);
			queryDetails.setSessionData(sessionData);
			String createTableSql = " select distinct Participant_1.GENDER Column0, Participant_1.MIDDLE_NAME Column1, Participant_1.FIRST_NAME Column2, Participant_1.VITAL_STATUS Column3, Participant_1.IDENTIFIER Column4, Participant_1.BIRTH_DATE Column5, Participant_1.LAST_NAME Column6, Participant_1.ETHNICITY Column7, Participant_1.SOCIAL_SECURITY_NUMBER Column8, Participant_1.ACTIVITY_STATUS Column9, Participant_1.DEATH_DATE Column10, Participant_1.GENOTYPE Column11  from (select * from CATISSUE_PARTICIPANT where ACTIVITY_STATUS != 'Disabled') Participant_1  where Participant_1.FIRST_NAME is NOT NULL";
			treeBizLogic.createOutputTreeTable(createTableSql, queryDetails);

        EntityInterface cprEntity = GenericQueryGeneratorMock.createEntity("CollectionProtocolRegistration");
        cprEntity = GenericQueryGeneratorMock.getEntity(cache, cprEntity);
        IOutputEntity cprOutputEntity = QueryObjectFactory.createOutputEntity(cprEntity);
        cprOutputEntity.getSelectedAttributes().addAll(cprEntity.getAllAttributes());
        OutputTreeDataNode cprOutputTreeDataNode = new OutputTreeDataNode(cprOutputEntity, 2, 0);
    	int index=0;
        for(AttributeInterface attribute : cprEntity.getAllAttributes())
        {
	        String className = edu.wustl.query.util.global.Utility.parseClassName(cprEntity.getName());
	        String attributeLabel = edu.wustl.common.util.Utility.getDisplayLabel(attribute.getName());
	        String displayNmForCol = className+" : "+attributeLabel;
	        cprOutputTreeDataNode.addAttribute(new QueryOutputTreeAttributeMetadata(attribute, "Column"+index, cprOutputTreeDataNode,
	                displayNmForCol));
	        index++;
        }
        queryDetails.setQuery(query);
        treeBizLogic.updateTreeForDataNode("UQ0_2_Label::0_2_0", outputTreeDataNode, "0", queryDetails);

        IQuery associationQuery = GenericQueryGeneratorMock.createInheritanceQueryWithAssociation1();
        queryDetails.setQuery(associationQuery);
        outputTreeDataNode.addChild(cprOutputEntity, 2);
        rootOutputTreeNodeList.add(outputTreeDataNode);
        rootOutputTreeNodeList.add(cprOutputTreeDataNode);
        uniqueIdNodesMap = QueryObjectProcessor.getAllChildrenNodes(rootOutputTreeNodeList);
        queryDetails.setUniqueIdNodesMap(uniqueIdNodesMap);
        queryDetails.setRandomNumber("_6358");
		sessionData.setUserId(2441l);
		queryDetails.setSessionData(sessionData);
        String nodeId = "UQ0_"+outputTreeDataNode.getId()+"_0::0_"+cprOutputTreeDataNode.getId()+"_Label";
        treeBizLogic.updateTreeForLabelNode(nodeId, queryDetails, false);
        queryDetails.setRandomNumber("_2040");
		sessionData.setUserId(1l);
		queryDetails.setSessionData(sessionData);
	}

	public void testEncryptId()
	{
		QueryOutputTreeBizLogic bizLogic = new QueryOutputTreeBizLogic();
		bizLogic.encryptId("0_2_Label");
	}

	public void testDecryptId()
	{
		QueryOutputTreeBizLogic bizLogic = new QueryOutputTreeBizLogic();
		bizLogic.decryptId("UQ0_2_Label::0_2_0");
	}
}
