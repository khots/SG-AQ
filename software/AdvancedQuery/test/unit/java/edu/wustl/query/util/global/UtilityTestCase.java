package edu.wustl.query.util.global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.QueryResultObjectData;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.querysuite.EntityCacheFactory;

public class UtilityTestCase extends TestCase
{
	public void testIsCOnditionOnIdentifiedField()
	{
		IQuery query = GenericQueryGeneratorMock.creatParticipantQuery();
		boolean isConditionOnIdData = Utility.isConditionOnIdentifiedField(query);
		assertEquals("is condition on identifier field",false, isConditionOnIdData);
	}

	public void getDisplayNameForColumn()
	{
		EntityCache cache = EntityCacheFactory.getInstance();
		EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
		AttributeInterface attribute = GenericQueryGeneratorMock.findAttribute(participantEntity, "birthDate");
		Utility.getDisplayNameForColumn(attribute);
		Utility.getPrimaryKey(participantEntity);
	}

	public void testCheckFeatureUsage()
	{
		Utility.checkFeatureUsage("app.name");
	}

	public void testGetQueryTitle()
	{
		String title = "Participant Id's Not Null";
		Utility.getQueryTitle(title);
		Utility.getTooltip(title);
		title = "Give me all male and female participants whose birthdate is between 10th Jan 1986 and 16th May 2010 and whose ethnicity is not known";
		Utility.getQueryTitle(title);
	}

	public void testGetAttributeValuesInProperOrder()
	{
		String dataType = "Long";
		String value1 = "210";
		String value2 = "2";
		Utility.getAttributeValuesInProperOrder(dataType, value1, value2);
		dataType = "Double";
		Utility.getAttributeValuesInProperOrder(dataType, value1, value2);

		dataType = "Date";
		value1 = "04-27-2010";
		value2 = "04-01-2010";
		Utility.getAttributeValuesInProperOrder(dataType, value1, value2);
	}

	public void testToNewGridFormatWithHref()
	{
		List<String> row = new ArrayList<String>();
		row.add("");
		row.add("Active");
		row.add("Male Gender");
		row.add("01-01-1957 00:00:00");
		row.add("");
		row.add("1");
		Map<Integer, QueryResultObjectData> hyperlinkColMap = new HashMap<Integer, QueryResultObjectData>();
		List<Integer> identifiedDataColumnIds = new ArrayList<Integer>();
        identifiedDataColumnIds.add(1);
        identifiedDataColumnIds.add(3);
        identifiedDataColumnIds.add(5);
        identifiedDataColumnIds.add(6);
        identifiedDataColumnIds.add(12);
        identifiedDataColumnIds.add(14);

		QueryResultObjectData queryResultObjectData = new QueryResultObjectData();
        queryResultObjectData.setAliasName("Participant");
        queryResultObjectData.setIdentifiedDataColumnIds(identifiedDataColumnIds);
        queryResultObjectData.setIdentifierColumnId(5);
        queryResultObjectData.setDependentColumnIds(new ArrayList());
        queryResultObjectData.setRelatedQueryResultObjects(new ArrayList());
        hyperlinkColMap.put(0, queryResultObjectData);
        Variables.aliasAndPageOfMap.put("Participant", "pageOfQueryModule");
		Utility.toNewGridFormatWithHref(row, hyperlinkColMap, 0);
	}

	public void testGetColWidth()
	{
		List columnNames = new ArrayList();
		columnNames.add(" ");
		columnNames.add(" ");
		columnNames.add("Activity Status : Participant");
		columnNames.add("Social Security Number : Participant");
		columnNames.add("Gender : Participant");
		columnNames.add("Middle Name : Participant");
		columnNames.add("First Name : Participant");
		columnNames.add("ID : Participant");
		columnNames.add("Id : Participant");
		columnNames.add("Birth Date : Participant");
		columnNames.add("Last Name : Participant");
		Utility.getcolWidth(columnNames, false);
		Utility.getcolWidth(columnNames, true);
	}
	public void testGetColumnWidth()
	{
		List columnNames = new ArrayList();
		columnNames.add(" ");
		columnNames.add(" ");
		columnNames.add("Activity Status : Participant");
		columnNames.add("Social Security Number : Participant");
		columnNames.add("Gender : Participant");
		columnNames.add("Middle Name : Participant");
		columnNames.add("First Name : Participant");
		columnNames.add("ID : Participant");
		columnNames.add("Id : Participant");
		columnNames.add("Birth Date : Participant");
		columnNames.add("Last Name : Participant");
		Utility.getColumnWidth(columnNames);
		Utility.getColumnWidthP(columnNames);
		Utility.getGridWidth(columnNames);
	}

	public void testGetColTypes()
	{
		List dataList = new ArrayList();

		List list = new ArrayList();
		list.add("");
		list.add("Active");
		list.add("Male Gender");
		list.add("01-01-1957 00:00:00");
		list.add("");
		list.add("1");
		dataList.add(list);

		list = new ArrayList();
		list.add("");
		list.add("CLosed");
		list.add("Female Gender");
		list.add("01-10-1988 00:00:00");
		list.add("");
		list.add("2");
		dataList.add(list);
		Utility.getcolTypes(dataList);
	}

	public void testGetColumns()
	{
		List columnList = new ArrayList();
		columnList.add("Activity Status : Participant");
		columnList.add("Social Security Number : Participant");
		columnList.add("Gender : Participant");
		columnList.add("Middle Name : Participant");
		columnList.add("First Name : Participant");
		columnList.add("ID : Participant");
		columnList.add("Id : Participant");
		columnList.add("Birth Date : Participant");
		columnList.add("Last Name : Participant");
		String columnNames = Utility.getcolumns(columnList);
	}

	public void testSetReadDeniedSqlMap()
	{
		Utility.setReadDeniedAndEntitySqlMap();
	}

	public void testGetSQLForNode()
	{
		String parentNodeId = "3";
		String tableName = "TEMP_OUTPUTTREE2441_6358";
        String parentIdColumnName = "Column8";
        String selectSql = "select distinct Column10";
        String idColumnOfCurrentNode = "Column10";
        Utility.getSQLForNode(parentNodeId, tableName, parentIdColumnName, selectSql, idColumnOfCurrentNode);
	}

	public void testGetCPIdsList()
	{
		String objName = "Participant";
		Long identifier = 1l;
		SessionDataBean sessionData = new SessionDataBean();
		Variables.mainProtocolObject = "ClinicalStudy";
		Variables.entityCPSqlMap.put("Participant", "SELECT CSR.CLINICAL_STUDY_ID ,PARTICIPANT.IDENTIFIER FROM CATISSUE_CLINICAL_STUDY_REG CSR,CATISSUE_PARTICIPANT PARTICIPANT WHERE CSR.PARTICIPANT_ID =  PARTICIPANT.IDENTIFIER AND PARTICIPANT.IDENTIFIER =");
		sessionData.setCsmUserId("1");
		sessionData.setIpAddress("10.88.199.11");
		sessionData.setUserId(1l);
		sessionData.setUserName("admin@admin.com");
		Utility.getCPIdsList(objName, identifier, sessionData);
	}

	public void testGetFormattedString()
	{
		List dataList = new ArrayList();

		List list = new ArrayList();
		list.add("");
		list.add("Active");
		list.add("Male Gender");
		list.add("01-01-1957 00:00:00");
		list.add("");
		list.add("1");
		dataList.add(list);

		list = new ArrayList();
		list.add("");
		list.add("CLosed");
		list.add("Female Gender");
		list.add("01-10-1988 00:00:00");
		list.add("");
		list.add("2");
		dataList.add(list);
		Utility.getFormattedOutput(dataList);
	}
	public void testSetGridData()
	{
		List columnList = new ArrayList();
		columnList.add("Activity Status : Participant");
		columnList.add("Social Security Number : Participant");
		columnList.add("Gender : Participant");
		columnList.add("Middle Name : Participant");
		columnList.add("First Name : Participant");
		columnList.add("ID : Participant");
		columnList.add("Id : Participant");
		columnList.add("Birth Date : Participant");
		columnList.add("Last Name : Participant");
		List list = new ArrayList();
		list.add("");
		list.add("Active");
		list.add("Male Gender");
		list.add("01-01-1957 00:00:00");
		list.add("");
		list.add("1");
		List dataList = new ArrayList();
		dataList.add(list);
		list = new ArrayList();
		list.add("");
		list.add("CLosed");
		list.add("Female Gender");
		list.add("01-10-1988 00:00:00");
		list.add("");
		list.add("2");
		dataList.add(list);
		HttpServletRequest request = new HttpServletRequest() {

			public void setCharacterEncoding(String arg0)
					throws UnsupportedEncodingException {
				// TODO Auto-generated method stub

			}

			public void setAttribute(String arg0, Object arg1) {
				// TODO Auto-generated method stub

			}

			public void removeAttribute(String arg0) {
				// TODO Auto-generated method stub

			}

			public boolean isSecure() {
				// TODO Auto-generated method stub
				return false;
			}

			public int getServerPort() {
				// TODO Auto-generated method stub
				return 0;
			}

			public String getServerName() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getScheme() {
				// TODO Auto-generated method stub
				return null;
			}

			public RequestDispatcher getRequestDispatcher(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			public int getRemotePort() {
				// TODO Auto-generated method stub
				return 0;
			}

			public String getRemoteHost() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getRemoteAddr() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getRealPath(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			public BufferedReader getReader() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			public String getProtocol() {
				// TODO Auto-generated method stub
				return null;
			}

			public String[] getParameterValues(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			public Enumeration getParameterNames() {
				// TODO Auto-generated method stub
				return null;
			}

			public Map getParameterMap() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getParameter(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			public Enumeration getLocales() {
				// TODO Auto-generated method stub
				return null;
			}

			public Locale getLocale() {
				// TODO Auto-generated method stub
				return null;
			}

			public int getLocalPort() {
				// TODO Auto-generated method stub
				return 0;
			}

			public String getLocalName() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getLocalAddr() {
				// TODO Auto-generated method stub
				return null;
			}

			public ServletInputStream getInputStream() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			public String getContentType() {
				// TODO Auto-generated method stub
				return null;
			}

			public int getContentLength() {
				// TODO Auto-generated method stub
				return 0;
			}

			public String getCharacterEncoding() {
				// TODO Auto-generated method stub
				return null;
			}

			public Enumeration getAttributeNames() {
				// TODO Auto-generated method stub
				return null;
			}

			public Object getAttribute(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean isUserInRole(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isRequestedSessionIdValid() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isRequestedSessionIdFromUrl() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isRequestedSessionIdFromURL() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isRequestedSessionIdFromCookie() {
				// TODO Auto-generated method stub
				return false;
			}

			public Principal getUserPrincipal() {
				// TODO Auto-generated method stub
				return null;
			}

			public HttpSession getSession(boolean arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			public HttpSession getSession() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getServletPath() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getRequestedSessionId() {
				// TODO Auto-generated method stub
				return null;
			}

			public StringBuffer getRequestURL() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getRequestURI() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getRemoteUser() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getQueryString() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getPathTranslated() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getPathInfo() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getMethod() {
				// TODO Auto-generated method stub
				return null;
			}

			public int getIntHeader(String arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			public Enumeration getHeaders(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			public Enumeration getHeaderNames() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getHeader(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			public long getDateHeader(String arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			public Cookie[] getCookies() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getContextPath() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getAuthType() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		Utility.setGridData(dataList, columnList, request);
	}
}
