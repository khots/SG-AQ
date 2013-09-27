<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/PagenationTag.tld" prefix="custom"%>

<%-- Imports --%>
<%@
	page language="java" contentType="text/html"
	import="edu.wustl.query.util.global.AQConstants"%>
<%
	ParameterizedQuery query = (ParameterizedQuery) session
	.getAttribute(AQConstants.QUERY_OBJECT);
	String description = query.getDescription();
	if(description == null)
	{
		description = "NA";
	}
%>
<%@page
	import="edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/advQuery/styleSheet.css" />
<script src="jss/advQuery/queryModule.js"></script>
<script type='text/JavaScript' src='jss/advQuery/scwcalendar.js'></script>
<script>
		   function GotoRetriveAction()
		   {
		      var frm = document.forms[0];
		      frm.action="ShowQueryDashboardAction.do";
		      frm.submit();
		   }
		</script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><bean:message key="savequery.queryConditionTitle" /></title>
</head>



<body onunload='closeWaitPage()'>
<html:errors />
<html:form styleId='saveQueryForm' 
	action='<%=AQConstants.EXECUTE_QUERY_ACTION%>'>
		<table width='100%' cellpadding='0' cellspacing='0' border='0' align='center' >
		<tr valign="center" class="bgImage">
				<td width="17%" >&nbsp;
					<img src="images/advQuery/execute-button-1.PNG"  alt="Saved Queries"   width="25" height="25" hspace="5" align="absmiddle"/>
					<span class="savedQueryHeading" > <bean:message	key="configure.query.parameters" /></span>
				</td>
			</tr>
				<td colspan="4">
			<table summary="" cellpadding="3" cellspacing="0" border="0"
				width="100%" >

				<tr>
					<td  width="2%" valign="top" class="savedQueryHeading">
					  <b><bean:message	key="query.title" /></b>
					 </td>
					 <td class="formSaveQueryTitle" align="left">
					 : <%=query.getName()%>
					 </td>
				</tr>
				<tr>
					<td  valign="top" width="2%" class="savedQueryHeading">
						<b><bean:message	key="query.description" /></b>

					 </td>
					 <td  class="formSaveQueryTitle" align="left">
					: <%=description%>
					 </td>
				</tr>

				<tr>
					<td colspan="4" height="20">&nbsp;</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
					<td colspan="4" height="20">&nbsp;</td>
				</tr>
		<tr>
			<td>
			<div
				style="width:100%;  max-height:300px; min-height:50px; overflow-y:auto;">
			<%=request.getAttribute(AQConstants.HTML_CONTENTS)%></div>


			</td>
		</tr>
		<tr>
			<td height="20">&nbsp;</td>
		</tr>
		<tr>
			<td align="left" >  
			 <html:hidden property="queryId" /> <input type="button" name="execute" value="Execute" class="actionButton"
				onClick="ExecuteSavedQuery()" /> <input type="button" name="cancel" class="actionButton"
				value="Cancel" onClick="GotoRetriveAction();" /></td>
		</tr>
	</table>
</html:form>
</body>
</html>
