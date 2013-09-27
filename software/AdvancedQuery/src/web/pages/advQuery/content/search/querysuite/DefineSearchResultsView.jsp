<%@ page import="java.util.*"%>
<%@ page import="edu.wustl.query.util.global.AQConstants"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>

<html>
<head>
<link rel="STYLESHEET" type="text/css" href="dhtmlx_suite/css/dhtmlXTree.css">
<meta http-equiv="Content-Language" content="en-us">
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<script src="jss/advQuery/queryModule.js"></script>
<script type="text/javascript" src="jss/advQuery/wz_tooltip.js"></script>
<script language="JavaScript" type="text/javascript" src="dhtmlx_suite/js/dhtmxcommon.js"></script>
<script language="JavaScript" type="text/javascript" src="dhtmlx_suite/js/dhtmlxtree.js"></script>

</head>
<body  onunload='closeWaitPage()' onresize='ViewResultResize()'>
<!-- Make the Ajax javascript available -->
<script type="text/javascript" src="jss/advQuery/ajax.js"></script>
<html:errors />
<%
	String formAction = AQConstants.ViewSearchResultsAction;
	String defineSearchResultsViewAction = AQConstants.DefineSearchResultsViewAction;
	Map treesMap = (Map) request.getAttribute("treesMap");
%>
<html:form method="GET" action="<%=defineSearchResultsViewAction%>" style="margin:0;padding:0;">
<html:hidden property="currentPage" value="prevToAddLimits"/>
<input type="hidden" name="isQuery" value="true">
<table bordercolor="#000000" border="0" width="100%" cellspacing="0" cellpadding="0"  height="100%" >

	<tr>
		<td valign="top">
			<table border="0" width="100%" cellspacing="0" cellpadding="0" height="100%" id="table1" >
				<tr height="90%">
					<td valign="top">
						<table border="0" width="100%" cellspacing="0" cellpadding="0" height="100%" bordercolor="#000000" id="table2" >
							<tr  height="1%" >
								<td width="33%" align="center" class="bgWizardImage">
									<img src="images/advQuery/1_inactive.gif" /> <!-- width="118" height="25" /-->
								</td>
								<td width="33%" align="center" class="bgWizardImage">
									<img src="images/advQuery/2_active.gif" /> <!-- width="199" height="38" /-->
								</td>
								<td width="33%" align="center" class="bgWizardImage">
									<img src="images/advQuery/3_inactive.gif" /> <!--  width="139" height="38" /-->
								</td>
							</tr>
							<tr height="1">
								<td></td>
							</tr>
							<tr valign="top"  height="100%" width="100%">
								<td colspan="4" valign="top" height="100%" width="100%">
							<!--	<div id="ResultViewDiv" style="height:100%; overflow:auto">
									<script>
										ResultViewContent = document.getElementById('ResultViewDiv');
										ResultViewContent.style.height = (document.body.clientHeight - 160)+ 'px';
									</script>-->
									<table border="0" cellspacing="0" cellpadding="0" valign="top"  height="100%" width="100%">
									<tr valign="top">
										<td valign="top" height="100%" colspan="4" width="100%">
											<!--		tiles insert -->
											<tiles:insert attribute="content"></tiles:insert>
										</td>
									</tr>
									</table>
									<!--</div>-->
								</td>
							</tr>
						</table>
					</td>
				</tr>

				<tr bgcolor="#EAEAEA">
					<td colspan="4" valign="top">
					<table border="0" width="100%" cellspacing="0" cellpadding="0"  height="24" background="images/bot_bg_wiz.gif">
					<tr valign="center">
						<td width="2%" valign="center" >&nbsp;</td>
						<td width="50%" align="left">
						  <table border="0" cellspacing="0" cellpadding="0">
							<tr>
							  <td><img src="images/advQuery/b_save.gif" width="51"  hspace="3" onclick="validateQuery('save');" id="saveImgId"/></td>
							  <td><img src="images/advQuery/b_search.gif"  hspace="3" onclick="validateQuery('search');" id="searchImgId"/></td>
							</tr>
						  </table>
						</td>

						<td width="50%" align="right">
						  <table border="0" cellspacing="0" cellpadding="0">
						  <tr>
							<td><img src="images/advQuery/b_prev.gif" width="72"  hspace="3" onclick="previousFromDefineResults()" id="prevImgId"/></td>
							<td><img src="images/advQuery/b_next.gif"   hspace="3" onclick="validateQuery('search');" id="nextImgId"/></td>
						  </tr>
						</table>
					   </td>
						<td width="2%">&nbsp;</td>
					</tr>
				</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>

</table>
</html:form>

</body>
</html>