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
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.QueryDetails;

public class QueryDetailsTestCase extends TestCase
{
	public void testAllMethods()
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

		List<OutputTreeDataNode> rootOutputTreeNodeList = new ArrayList<OutputTreeDataNode>();
		Map<String, OutputTreeDataNode> uniqueIdNodesMap = new HashMap<String, OutputTreeDataNode>();
		Map<EntityInterface, List<EntityInterface>> mainEntityMap= new HashMap<EntityInterface, List<EntityInterface>>();
		SessionDataBean sessionData= new SessionDataBean();
		String randomNumber="123";

		Map<AttributeInterface, String> attributeColumnNameMap = new HashMap<AttributeInterface, String>();
		Map<String, IOutputTerm> outputTermsColumns = new HashMap<String, IOutputTerm>();
		long auditEventId=1l;
		LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
		String sqlForDefineView = "SELECT COLUMN0,COLUMN1 FROM TEMP_OUTPUTTREE_123";
		IQuery query = GenericQueryGeneratorMock.creatParticipantQuery();
		session.setAttribute(AQConstants.SAVE_TREE_NODE_LIST, rootOutputTreeNodeList);
		session.setAttribute(AQConstants.ID_NODES_MAP, uniqueIdNodesMap);
		session.setAttribute(AQConstants.MAIN_ENTITY_MAP,mainEntityMap);
		session.setAttribute(AQConstants.ATTRIBUTE_COLUMN_NAME_MAP, attributeColumnNameMap);
		session.setAttribute(AQConstants.OUTPUT_TERMS_COLUMNS,outputTermsColumns);
		session.setAttribute(AQConstants.QUERY_OBJECT, query);
		session.setAttribute(AQConstants.COLUMN_VALUE_BEAN,columnValueBean);
		session.setAttribute("AUDIT_EVENT_ID", Long.valueOf("1"));
		QueryDetails queryDetails = new QueryDetails(session);

		queryDetails.getAttributeColumnNameMap();
		queryDetails.getAuditEventId();
		queryDetails.getColumnValueBean();
		queryDetails.getMainEntityMap();
		queryDetails.getOutputTermsColumns();
		queryDetails.getQuery();
		queryDetails.getRandomNumber();
		queryDetails.getRootOutputTreeNodeList();
		queryDetails.getSessionData();
		queryDetails.getUniqueIdNodesMap();

		queryDetails.setAttributeColumnNameMap(attributeColumnNameMap);
		queryDetails.setAuditEventId(auditEventId);
		queryDetails.setColumnValueBean(columnValueBean);
		queryDetails.setMainEntityMap(mainEntityMap);
		queryDetails.setOutputTermsColumns(outputTermsColumns);
		queryDetails.setQuery(query);
		queryDetails.setRandomNumber(randomNumber);
		queryDetails.setRootOutputTreeNodeList(rootOutputTreeNodeList);
		queryDetails.setSessionData(sessionData);
		queryDetails.setUniqueIdNodesMap(uniqueIdNodesMap);
	}
}
