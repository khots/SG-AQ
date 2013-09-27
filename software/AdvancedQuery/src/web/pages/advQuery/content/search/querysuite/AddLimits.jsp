<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ page import="java.util.*"%>
<%@ page import="edu.wustl.query.util.global.AQConstants"%>
<%@ page import="edu.wustl.cab2b.client.ui.query.ClientQueryBuilder"%>
<%@ page import="edu.wustl.query.actionForm.CategorySearchForm"%>


<html>
<head>
<meta http-equiv="Content-Language" content="en-us">
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1252">
<script src="jss/advQuery/queryModule.js"></script>
<script type="text/javascript" src="jss/advQuery/wz_tooltip.js"></script>
<script type="text/javascript" src="jss/advQuery/ajax.js"></script>
</head>
<body onunload='closeWaitPage()'>
<script type='text/JavaScript' src='jss/advQuery/scwcalendar.js'></script>
<html:errors />
<%
	String formAction = AQConstants.SearchCategory;

	String defineSearchResultsViewAction = AQConstants.DefineSearchResultsViewAction;
	String isQuery =(String) request.getAttribute("isQuery");;
	if(isQuery==null)
	{
		isQuery="false";
	}
	System.out.println("isQuery ====>"+isQuery);
%>
<script>
function hideTreeChecked()
{
	document.getElementById('showTree').value=document.getElementById('hiddenCheckBox').checked;

}
</script>
<html:form method="GET" action="<%=formAction%>"
	style="margin:0;padding:0;">
	<html:hidden property="stringToCreateQueryObject" value="" />
	<html:hidden property="nextOperation" value="" />
	<html:hidden property="showTree" styleId="showTree"/>

	<table border="0" width="100%" cellspacing="0" cellpadding="0"
		height="450">

		<tr>
			<td width="33%" align="center" class="bgWizardImage" valign="top">
			<img src="images/advQuery/1_active.gif" /> <!-- width="118" height="25" /-->
			</td>
			<td width="33%" align="center" class="bgWizardImage" valign="top">
			<img src="images/advQuery/2_inactive.gif" /> <!-- width="199" height="38" /-->
			</td>
			<td width="33%" align="center" class="bgWizardImage" valign="top">
			<img src="images/advQuery/3_inactive.gif" /> <!--  width="139" height="38" /-->
			</td>
		</tr>
		<tr>
			<td colspan="3">
			<table border="0" width="100%" cellspacing="0" cellpadding="0"
				height="100%" id="table1">
				<tr>
					<td>
					<table border="0" width="100%" cellspacing="0" cellpadding="0"
						height="100%" bordercolor="#000000" id="table2">

						<tr>
							<td height="60%" valign="top" width="100%" colspan="4">
							<table border="0" height="100%" width="100%" cellpadding="1"
								cellspacing="3">
								<tr>
									<td valign="top" width="10%"><%@ include
										file="/pages/advQuery/content/search/querysuite/ChooseSearchCategory.jsp"%>
									</td>



									<td valign="top" height="60%">
									<table border="0" width="100%" cellspacing="0" cellpadding="0"
										bgcolor="#FFFFFF" height="100%" bordercolorlight="#000000">

										<tr>
											<td>
											<table border="0" width="100%" cellspacing="0"
												cellpadding="0" bgcolor="#FFFFFF" height="30%"
												bordercolorlight="#000000" class='tbBordersAllbordersBlack'>

												<tr id="rowMsg">
													<td id="validationMessagesSection"
														class='validationMessageCss'>
													<div id="validationMessagesRow"
														class='validationMessageCss'height:50;display:none"></div>
													</td>
												</tr>
												<tr id="AddLimitsButtonMsg" border="0">
													<td id="AddLimitsButtonSection" height="10">
													<div id="AddLimitsMsgRow" border="0"></div>
													</td>
												</tr>
												<tr>
													<td height="200" width="100%" id="addLimitsSection">
													<div id="addLimits"
														style="overflow: auto; height: 100%; width: 100%"></div>
													</td>
												</tr>
											</table>
											</td>
										</tr>
										<tr id="AddLimitsButtonMsg" border="0">
											<td valign="top" id="AddLimitsButtonSection">
											<div id="AddLimitsButtonRow" align="right" border="0"></div>
											</td>
										</tr>
										<tr>
											<td valign="top">
											<table border="1" width="100%" cellspacing="0"
												cellpadding="0" bgcolor="#FFFFFF" height="100%"
												bordercolorlight="#000000">
												<tr>
													<td height="400px">
													<div id="queryTableTd"
														style="overflow: auto; height: 400; width: 100%"><object
														classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
														id="DAG" width="100%" height="100%"
														codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">
														<param name="movie"
															value="flexclient/advQuery/dag/DAG.swf?view=AddLimit&isQuery=<%=isQuery%>" />
														<param name="quality" value="high" />

														<param name="bgcolor" value="#869ca7" />
														<param name="allowScriptAccess" value="sameDomain" />
														<embed
															src="flexclient/advQuery/dag/DAG.swf?view=AddLimit&isQuery=<%=isQuery%>"
															quality="high" bgcolor="#869ca7" width="100%"
															height="100%" name="DAG" align="middle" play="true"
															loop="false" quality="high"
															allowScriptAccess="sameDomain"
															type="application/x-shockwave-flash"
															pluginspage="http://www.adobe.com/go/getflashplayer">
														</embed> </object></div>
													</td>
												</tr>
											</table>
											</td>
										</tr>
									</table>
								</td>

								</tr>

							</table>

							</td>
						</tr>

					</table>
					</td>
				</tr>
				<tr>
					<td colspan="4">
					<table border="0" width="100%" cellspacing="0" cellpadding="0"
						height="24" background="images/advQuery/bot_bg_wiz.gif">
						<tr valign="bottom">
							<td width="2%" valign="bottom">&nbsp;</td>
							<td width="50%" align="left">
							<table border="0" width="100%" cellspacing="0" cellpadding="0">
								<tr>
									<td><img src="images/advQuery/b_save.gif" id="saveBtn"
										width="51" hspace="3" onclick="validateQuery('save');" /></td>
									<td><img src="images/advQuery/b_search.gif" id="searchBtn"
										hspace="3" onclick="validateQuery('search');" /></td>
									<td class="showTreeChkBoxHeading" align="right">
										<input type="checkBox" name="hiddenCheckBox" id="hiddenCheckBox" onclick="hideTreeChecked()"/>&nbsp;
										<span valign="top"><bean:message key="query.checkBox.hideTree"/></span>
									<script>
										var hideTreeVal=document.getElementById('showTree').value;
										if(hideTreeVal == 'true')
										{
											document.getElementById('hiddenCheckBox').checked=true;
										}
									</script>

									</td>
								</tr>
							</table>
							</td>

							<td width="50%" align="right">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>

									<td><img src="images/advQuery/b_next.gif" id="nextBtn"
										hspace="3" onclick="saveClientQueryToServer('next');" /></td>
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