<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ page import="java.util.List, java.util.ArrayList"%>
<%@ page import="edu.wustl.query.actionForm.QueryAdvanceSearchForm"%>
<%@ page import="edu.wustl.query.util.global.AQConstants"%>
<%@ page import="edu.wustl.query.util.global.Utility"%>
<%@ page import="edu.wustl.query.util.global.Variables"%>
<%@ page language="java" isELIgnored="false"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<head>
<link rel="STYLESHEET" type="text/css" href="css/advQuery/simpleSearchDataView.css">

<script type="text/javascript" src="jss/advQuery/queryModule.js"></script>
<script type="text/javascript" src="jss/advQuery/script.js"></script>
<script type="text/javascript" src="jss/advQuery/ajax.js"></script>

<%		
	String checkAllPages = (String) session.getAttribute("checkAllPages");
	QueryAdvanceSearchForm form = (QueryAdvanceSearchForm) session.getAttribute("advanceSearchForm");
	List dataList = (List) request.getAttribute(AQConstants.PAGINATION_DATA_LIST);
	String pageOf = (String) request.getAttribute(AQConstants.PAGEOF);
		
	List columnList = new ArrayList((List)session.getAttribute(AQConstants.SPREADSHEET_COLUMN_LIST));
	columnList.add(0, " ");
	
	int idColIndex = columnList.indexOf("ID");
	int specimentIdColIndex = columnList.indexOf("Specimen : Id");

	String configAction = new String();
	String redefineQueryAction = new String();
	
	if (pageOf.equals(AQConstants.PAGEOF_SIMPLE_QUERY_INTERFACE)) {
		configAction = "onSimpleConfigure()";
		redefineQueryAction = "onRedefineSimpleQuery()";
	} else if (pageOf.equals("pageOfQueryModule")) {
		configAction = "onQueryResultsConfigure()";
		redefineQueryAction = "onRedefineDAGQuery()";
	} else {
		configAction = "onAdvanceConfigure()";
		redefineQueryAction = "onRedefineAdvanceQuery()";
	}
	
%>
<script type="text/javascript">
	var isQueryModule = "<%=pageOf.equals(AQConstants.PAGEOF_QUERY_MODULE)%>";
	var checkAllPages = <%=(checkAllPages != null && checkAllPages.equals("true"))%>	
	var specimentIdColIndex = <%=specimentIdColIndex%>
</script>
<script type="text/javascript" src="jss/advQuery/simpleSearchDataView.js"></script>
 
</head>
<body>

<table cellpadding='0' cellspacing='0' border='0' align='center' width="100%">
	<tr>
		<td width="33%" align="center" class="bgWizardImage">
			<img src="images/advQuery/1_inactive.gif" /> <!-- width="118" height="25" /-->
		</td>
		<td width="33%" align="center" class="bgWizardImage">
			<img src="images/advQuery/2_inactive.gif" /> <!-- width="199" height="38" /-->
		</td>
		<td width="33%" align="center" class="bgWizardImage">
			<img src="images/advQuery/3_active.gif" /> <!--  width="139" height="38" /-->
		</td>
	</tr>	
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0" >
	<logic:equal name="pageOf" value="<%=AQConstants.PAGEOF_SIMPLE_QUERY_INTERFACE%>">
		<tr height="95%"><td>
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class=""><span class=""> Simple Query </span></td>
					<td>
						<img src="images/advQuery/uIEnhancementImages/table_title_corner2.gif"
							alt="Page Title - Search Results" width="31" height="24"
							hspace="0" vspace="0" /></td>
				</tr>
			</table></td>
		</tr>
	</logic:equal>
	<tr>
		<td>
		<html:form action="QueryWizard.do" style="margin:0;padding:0;">
			<html:hidden property="checkAllPages" value="" />
				
			<table width="100%" border="0" cellpadding="0" cellspacing="0">
				<tr >
					<td id="errorStatus"><%@ include file="/pages/advQuery/content/common/ActionErrors.jsp"%></td>
				</tr>
				<%
					if (dataList == null && pageOf.equals(AQConstants.PAGEOF_QUERY_RESULTS)) {
				%>
					<tr><td><bean:message key="advanceQuery.noRecordsFound" /></td></tr>					
				<%
					} else {
												
						if (pageOf.equals(AQConstants.PAGEOF_QUERY_RESULTS)) {
								String[] selectedColumns = form.getSelectedColumnNames();
				%>

				<tr id="hiddenCombo" rowspan="2" >
					<td class="black_new"><!-- Mandar : 434 : for tooltip --> 
						<html:select property="selectedColumnNames" styleClass="selectedColumnNames"
							size="1" styleId="selectedColumnNames" multiple="true"
							onmouseover="showTip(this.id)" onmouseout="hideTip(this.id)">
						<%
							for (int j = 0; j < selectedColumns.length; j++) {
						%>
							<html:option value="<%=selectedColumns[j]%>"><%=selectedColumns[j]%></html:option>
						<%
							}
						%>
					</html:select></td>
				</tr>
				<%
						}
				%>

				<tr>
					<td>									
						<%@ include file="/pages/advQuery/content/search/AdvanceGrid.jsp"%>
					</td>
				</tr>

				<tr>
					<td style="padding: 5px 0 0 3px;">
					<table cellpadding="0" cellspacing="0" border="0" width="99.9%">
						<tr>			
							<td width="10%">									
								<button type="button" class="nav_button" id="addToListImgId" onclick="getData()">
									<img src="images/advQuery/list2_add.gif" style="margin-right: 5px"/>
										Add To My List</button>
							</td>
					 
							<td width="10%">
								<button type="button" class="nav_button" id="exportImgId" onclick="onExport()">
									<img src="images/advQuery/excel.gif" style="margin-right: 5px"/>
									Export CSV</button></td>	
									<input id="jsonData" type="hidden" name="jsonData" >							
							<td>&nbsp;</td>
							<td width="10%">
								<input type="button" value="Define View" id="defineViewId"
									onclick="<%=configAction%>" class="nav_button"></td>
							<td width="10%">
								<input type="button" value="Redefine Query" id="redefineQueryId"
									onclick="<%=redefineQueryAction%>" class="nav_button"></td>
							<td width="10%">
								<input type="button" value="Save" id="saveResultImgId"
									onclick="saveClientQueryToServer('save')" class="nav_button"></td>							
						</tr>
					</table>
					</td>
				</tr>
				<%
					}
				%>
				<tr>
					<td>
						<html:hidden property="operation" value="" />
						<input type="hidden" name="isQuery" value="true">
					</td>
				</tr>
			</table>			
		</html:form>
	</td></tr>
</table>
 
