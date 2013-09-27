package edu.ustl.query.util.querysuite;

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

import junit.framework.TestCase;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.ParseException;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.QueryDetails;
import edu.wustl.query.util.querysuite.QueryModuleError;
import edu.wustl.query.util.querysuite.QueryModuleException;
import edu.wustl.query.util.querysuite.QueryModuleSqlUtil;
import edu.wustl.security.exception.SMException;

public class QueryModuleSqlUtilTestCase extends TestCase
{
	public void test() throws DAOException, SQLException
	{
		String columnName = "ROOT_ENTITY_NAME";
		String newColumnValue = "edu.wustl.catissuecore.domain.Participant";
		long auditEventId = 29894;

		QueryModuleSqlUtil.updateAuditQueryDetails(columnName, newColumnValue, auditEventId);
		QueryModuleError status = QueryModuleError.EMPTY_DAG;
		int errorCode = status.getErrorCode();
		assertEquals("Error code", errorCode,1);

		QueryModuleException queryModExp = new QueryModuleException("DAG is Empty", status);
		QueryModuleError key = queryModExp.getKey();
		assertEquals("Error code", key.getErrorCode(),1);
		queryModExp = new QueryModuleException("DAG is Empty");
	}

	public void testGetSqlForRootNode()
	{
		String tableName = "TEMP_OUTPUTTREE_16";
		Map<String, String> columnNameIndexMap = new HashMap<String, String>();
		columnNameIndexMap.put("index", "6");
		columnNameIndexMap.put("columnNames", "Column0 , Column1, Column6, Column3, Column4");
		String selectSql = QueryModuleSqlUtil.getSQLForRootNode(tableName, columnNameIndexMap);
		assertEquals("SQL for root node",selectSql,"select distinct Column0 , Column1, Column6, Column3, Column4 from TEMP_OUTPUTTREE_16 where Column0  is not null::6");
	}

	public void testGetCountForQuery() throws DAOException, SMException, ClassNotFoundException, ParseException
	{
		String tableName = "TEMP_OUTPUTTREE1_2040";
		String createTableSql = " select distinct Participant_1.GENDER Column0, Participant_1.MIDDLE_NAME Column1, Participant_1.FIRST_NAME Column2, Participant_1.VITAL_STATUS Column3, Participant_1.IDENTIFIER Column4, Participant_1.BIRTH_DATE Column5, Participant_1.LAST_NAME Column6, Participant_1.ETHNICITY Column7, Participant_1.SOCIAL_SECURITY_NUMBER Column8, Participant_1.ACTIVITY_STATUS Column9, Participant_1.DEATH_DATE Column10, Participant_1.GENOTYPE Column11  from (select * from CATISSUE_PARTICIPANT where ACTIVITY_STATUS != 'Disabled') Participant_1  where Participant_1.FIRST_NAME is NOT NULL";
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
		queryDetails.setSessionData(sessionData);
		String sql = "select distinct Column1 , Column3, Column5, Column6, Column10 from TEMP_OUTPUTTREE2441_6358 where Column10  is not null";
		QueryModuleSqlUtil.getCountForQuery(sql, queryDetails);

		try
		{
			String appName=CommonServiceLocator.getInstance().getAppName();
		    IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
            JDBCDAO jdbcDao = daoFactory.getJDBCDAO();
			jdbcDao.openSession(sessionData);
			jdbcDao.deleteTable(tableName);
			jdbcDao.commit();
			jdbcDao.closeSession();
		}
		catch (DAOException ex)
		{
			ex.printStackTrace();
		}
		String path = "test/unit/java/AdvancedQuery.xml";
		XMLPropertyHandler.init(path);
		QueryModuleSqlUtil.executeCreateTable(tableName, createTableSql, queryDetails);
	}
}
