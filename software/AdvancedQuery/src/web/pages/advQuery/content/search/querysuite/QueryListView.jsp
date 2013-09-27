<%@page import="edu.wustl.query.bizlogic.DashboardBizLogic"%><META
	HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<%-- TagLibs --%>  
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/multiSelectUsingCombo.tld" prefix="mCombo" %>
<%-- Imports --%>
<%@ 
	page language="java" contentType="text/html"
	import="edu.wustl.query.util.global.AQConstants,org.apache.struts.Globals"%>
<%@ page
	import="org.apache.struts.action.ActionMessages,edu.wustl.query.util.global.Utility"%>
<%@ page import="edu.wustl.query.util.global.AQConstants"%>
<%@ page import="edu.wustl.query.actionForm.SaveQueryForm"%>
 
<%@ page
	import="edu.wustl.cab2b.client.ui.query.IClientQueryBuilderInterface,edu.wustl.cab2b.client.ui.query.ClientQueryBuilder,edu.wustl.query.flex.dag.DAGConstant,edu.wustl.common.querysuite.queryobject.IQuery,edu.wustl.common.querysuite.queryobject.impl.Query,edu.wustl.common.querysuite.queryobject.IParameterizedQuery"%>
<%@ page
	import="java.util.*,java.text.DateFormat,java.text.SimpleDateFormat"%>
<%@ page import="edu.wustl.query.beans.DashboardBean"%>
<head>
<!-- dhtmlx Grid/tree Grid -->
<script type="text/javascript" src="jss/advQuery/SavedQuery.js"></script>
<script type="text/javascript" src="jss/advQuery/queryModule.js"></script>
<script type="text/javascript" src="jss/advQuery/wz_tooltip.js"></script>
<script type="text/javascript" src="jss/tag-popup.js"></script>
<link rel="stylesheet" type="text/css"
	href="css/advQuery/styleSheet.css" />
<link rel="stylesheet" type="text/css" href="css/advQuery/tag-popup.css" />
<link rel="STYLESHEET" type="text/css"
	href="dhtmlx_suite/css/dhtmlxgrid.css">
<link rel="STYLESHEET" type="text/css"
	href="dhtmlx_suite/css/dhtmlxtree.css">
<script src="dhtmlx_suite/js/dhtmlxcommon.js"></script>
<link rel="STYLESHEET" type="text/css"
	href="dhtmlx_suite/dhtml_pop/css/dhtmlxgrid_dhx_skyblue.css" />
<script src="dhtmlx_suite/js/dhtmlxgrid.js"></script>
<script src="dhtmlx_suite/js/dhtmlxtree.js"></script>
<script src="dhtmlx_suite/ext/dhtmlxgrid_filter.js"></script>
<link rel="STYLESHEET" type="text/css"
	href=" dhtmlx_suite/ext/dhtmlxgrid_pgn_bricks.css"/>
<script src="dhtmlx_suite/ext/dhtmlxgrid_pgn.js"></script>
<script src="dhtmlx_suite/js/dhtmlxgridcell.js"></script>
<script src="dhtmlx_suite/dhtml_pop/js/spec_dhtmlXTreeGrid.js"></script>  
<script src="dhtmlx_suite/gridexcells/dhtmlxgrid_excell_link.js"></script> 

<!-- Combo box -->
<script>var imgsrc="/images/de/";</script>
<script language="JavaScript" type="text/javascript"
	src="javascripts/de/scr.js"></script>
<link rel="stylesheet" type="text/css"
	href="css/clinicalstudyext-all.css" />
<script language="JavaScript" type="text/javascript"
	src="javascripts/de/ajax.js"></script>
<script type='text/JavaScript' src='jss/advQuery/scwcalendar.js'></script>
<script>
window.onload = function() {  
	<%
		String tagId =(String) request.getAttribute("tagId");
		if(tagId != null){ 
	%>
			doInitGrid('<%=tagId%>');
			doOnRowSelected('<%=tagId%>');
   	<%
		} else { 
	%>
			ajaxQueryGridInitCall("QueryGridInitAction.do");
			doInitGrid();
   	<%}%>
	document.getElementById('protocolCoordinatorIds').style.marginLeft= "15px";
	document.getElementById('addButton_coord').style.marginLeft= "21px";
	document.getElementById('removeButton_coord').style.marginLeft= "21px";
	initCombo();
	 
}  
</script>  
<body>
	<%
		boolean mac = false;
		Object os = request.getHeader("user-agent");
		if (os != null && os.toString().toLowerCase().indexOf("mac") != -1) {
			mac = true;
		}
		String selectText = "--Select--";
		String queryId = "5";
		String message = null;
		String entityTag="QueryTag";
		String entityTagItem="QueryTagItem";
		String popupHeader=(String) request.getAttribute(AQConstants.POPUP_HEADER);
		String popupDeleteMessage=(String) request.getAttribute(AQConstants.POPUP_DELETE_QUERY_MESSAGE);
		String popupAssignMessage=(String) request.getAttribute(AQConstants.POPUP_ASSIGN_MESSAGE);
		String popupAssignConditionMessage=(String) request.getAttribute(AQConstants.POPUP_ASSIGN_QMESSAGE);
		String popupFolderDeleteMessage=(String) request.getAttribute(AQConstants. POPUP_DELETE_QUERY_FOLDER_MESSAGE);
		String popupMessage = (String) request
				.getAttribute(AQConstants.POPUP_MESSAGE);
		String popupText = (String) request
				.getAttribute(AQConstants.POPUP_TEXT);
		String queryOption = (String) request.getAttribute("queryOption");
	%>

 
 
 <html:messages id="messageKey" message="true">
		<%
			message = messageKey;
		%>
	</html:messages>
	<html:form styleId='saveQueryForm'
		action='<%=AQConstants.FETCH_QUERY_ACTION%>'
		style="margin:0;padding:0;">
		<table width='100%' cellpadding='0' cellspacing='0' border='0'
			align='center'>
			<!-- style="width:100%;height:100%;overflow:auto"-->

			<tr valign="center" class="bgImage">
				<td width="50%">&nbsp; <img
					src="images/advQuery/ic_saved_queries.gif" id="savedQueryMenu"
					alt="Saved Queries" width="38" height="48" hspace="5"
					align="absmiddle" /> <span class="savedQueryHeading"> <bean:message
							key="query.savedQueries.label" /> </span></td>

				<td width="1" valign="middle" class="bgImage" align="left"><!-- <img
					src="images/advQuery/dot.gif" width="1" height="25" /> --></td>
			</tr>
			<tr>
				<td style="padding-left:10px;" class="savedQueryHeading">
						<div id="errorDiv" ><html:errors /></div>
						<div id="messageDiv" style="font-size: 0.9em; font-family: verdana; display:none"> <bean:message key="query.deletedSuccessfully.message"/></div>
				</td>
				<td>
				 
						<%
 						String	organizeTarget = "ajaxTreeGridInitCall('"+popupDeleteMessage+"','"+popupFolderDeleteMessage+"','"+entityTag+"','"+entityTagItem+"')";
 						%>
						<div id="navcontainer">
						<ul id="navlist">
						<li id="active" ><input type="button" class="btn" title="Organize" onclick="ajaxTreeGridInitCall('Are you sure you want to delete?','Are you sure you want to delete?','QueryTag','QueryTagItem') " value="Organize"></btton></li>
						</ul>
						<ul id="navlist">
							<li> <button type="input" id="newbtn" style="float: right;" onclick="return showlist();" title="New Query"> New Query <img src="images/advQuery/dropdown_arrow.gif" style=""padding-top:1px;"/></button> 
								<ul id="subnavlist" style="margin-left:3px;"> 
									<li style="height:20px;"><a href="javascript:QueryWizard();" title="Create New Query">Create New Query</a></li> 
										</a>
									</li>
                   				    <li style="height:20px; "><a  href="javascript:openImportPopup();" title="Import New Query">Import New Query</a></li>	    				   
           				 			 
								</ul> 
							</li>		 
						</ul>				
						</div>
				</td> 
			</tr>
			<td height="7px;"> </td>
			<tr>
			</tr>
		</table>
<table width='100%' height='100%' cellpadding='0' cellspacing='0' border='0'
			align='center'>
<tr>
<td>
		<div id="left">
			<table class="tags" width="100%" cellspacing="0" cellpadding="0"
				border="0">
 
				<tbody>
					<tr>
						<td><div style="height: 30px; width: 166px;" id="toolbarObj1">
									<input type="button" value="My Queries" id="myQueries" title ="My Queries"
										onclick="submitTheForm('QueryGridInitAction.do?pageOf=myQueries',this.id);"
										class="activebtn">
							</div>
						</td>
					</tr>
					<tr>
						<td><div style="height: 30px; width: 166px;" id="toolbarObj2">
									<input type="button" value="All Queries" id="allQueries" title="All Queries"
										onclick="submitTheForm('QueryGridInitAction.do?pageOf=allQueries',this.id);"
										class="btn1">
							</div>
						</td>
					</tr>
					<tr>
						<td><div style="height: 30px; width: 166px;" id="toolbarObj3">
									<input type="button" value="Shared Queries" id="sharedQueries" title="Shared Queries"
										onclick="submitTheForm('QueryGridInitAction.do?pageOf=sharedQueries',this.id);"
										class="btn1">
							</div>
						</td>
					</tr>
					<tr>
						<td height=100%><div id="mygrid_container" height="26.5em" width="97%"></div></td>
					</tr>
				</tbody>
			</table>

		</div>
  
		<div id="wrapper">
			<div id="mainContent">
				<!--POPUP-->
			
				<div id="blanket" style="display: none;"></div>
				<div id="popUpDiv" style="display: none; top: 100px; left: 210.5px;">
					<a onclick="popup('popUpDiv')"><img style="float: right; cursor:pointer;"
						height='23' width='24' title="Close" src='images/advQuery/close_button.gif'
						border='0'> </a>
					 
					<table class=" manage tags" width="100%" cellspacing="0"
						cellpadding="0" border="0">
							<tr valign="center" height="35" bgcolor="#d5e8ff">
								<td id="poupHeader" width="28%" align="left">
									<p>
										&nbsp&nbsp&nbsp&nbsp<b> <%=popupHeader%></b>
									</p>
								</td>
							</tr>
							<tr>
								<td align="left">
									<div id="treegridbox"
											style="width: 530px; height: 170px; background-color: white;"></div>
								</td>
							</tr>
							<tr>
								<td  align="left">
									<p>
											<label id="newTagLabel" width="28%" align="left"
											style="margin-left :20px; font-size: .82em; font-family: verdana;"><b> <%=popupText%>
											: </b> </label> <input type="text" id="newTagName" name="newTagName"
											size="17" onclick="this.value='';" maxlength="50" />
										 	<label id="shareLabel" width="28%" align="left"
											style="margin-left :20px; font-size: .82em; font-family: verdana;"><b> Share to users :
					 						</b> </label>
									</p>
								</td>
							</tr>
							<tr>
								<td  align="left">
									<div id="multiSelectId" class="black_ar_new" style="display:none; margin-left:35px">
											&nbsp&nbsp&nbsp<mCombo:multiSelectUsingCombo identifier="coord" styleClass="black_ar_new"  size="15" addButtonOnClick="moveOptions('coord','protocolCoordinatorIds', 'add')" removeButtonOnClick="moveOptions('protocolCoordinatorIds','coord', 'edit')" selectIdentifier="protocolCoordinatorIds" collection="<%=(List)request.getAttribute("selectedCoordinators")%>"/>
								 
									</div>
								</td>
							</tr>
							<tr>
								<td height="10px;"> </td>
							</tr>
							<tr>
								<td>	 
									<p> 			
									<%
 String	assignTarget = "ajaxAssignTagFunctionCall('AssignTagAction.do','"+popupAssignMessage+"','"+popupAssignConditionMessage+"')";
 									%>
 										&nbsp&nbsp&nbsp
										<input type="button" id="assignButton" value="ASSIGN" title="Assign" onclick="<%=assignTarget%> "
							onkeydown="<%=assignTarget%> " class="btn3">
				 
 										&nbsp&nbsp
										<input type="button"  id="shareButton" value="SHARE FOLDER" title="Share Folder (Folder will be visible to the users you choose)" onclick="checkForValidation()"
												onkeydown="checkForValidation()" style="width:120px; display:none" class="btn3">
										<img id="loadingImg" style="float:left; padding-left:5px; display:none;"
												height='25px' width='120px' src='images/advQuery/loading_circle.gif'
										border='0'>
									</p>
								</td>
							</tr>
					</table>
				</div>
			</div>
 

			<div id="right">
				<table width="100%" cellpadding='0' cellspacing='0' border='0'>
						<tr>
							<td>
								<div id="mygrid_right_container" height="31em" width="100%"></div>
								<div id="pagingArea"></div>
							</td>
						</tr>
				</table>
			</div>

		</div>
		
		    
</td>
</tr> 
</table>
<html:hidden styleId="queryId" property="queryId" />
</html:form>
<form method="POST" id="uploadForm" class="upload-form" enctype="multipart/form-data">
	<div id="new_PopUpDiv" style="display: none; margin-top:5%; margin-left:12%;">
		<a onclick="javascript: openImportPopup();"><img style="float:right; cursor:pointer;"
			height='23' width='24' title="Close" src='images/advQuery/close_button.gif'
			border='0'> </a>
		<table class="manage tags" width="100%">
			<tr height="30px" class="alert alert-title" bgcolor="#d5e8ff">
				<td width="28%" align="left">		 
					<p>
					<label  width="28%" align="left"
						style="font-size: .82em; font-family: verdana;"><b> Choose Query XML File</b> </label>
					</p>
				</td>
			</tr>
			<tr height="30px">
				<td> 
				<div id="popMessageDiv" class="alert alert-error" style="display:none"></div>
			</tr>
			<tr id ="queryNameTr" style="display:none;">
				<td> 
					<label id="newTagLabel" width="28%" align="left"
						style="margin-left :15px; font-size: .82em; font-family: verdana;">
						<b> Title : </b>
				 		<input type="text" id="queryName" name="queryName" size="24" onkeydown="javascript:avoidEnter();" value="" onclick="this.value='';" maxlength="255" />
				 </label>
				 </td>
			</tr>
			<tr id ="fileTr" height="35px">
				<td width="28%" align="left">
					 <input type="file"  id="file" style="margin-left :15px; cursor: pointer; z-index:1;" name="file"/> 
				</td>
			</tr>
			<tr height="15px"></tr>
			<tr id ="importBtnTr"height="35px">
				<td width="28%" align="left">
				<button type="button" name="submitfile" id="newbtn" style="margin-left :15px; margin-top:0px"  
				    onClick="fileUpload(this.form, 'ImportQuery'); return false;"> Import Query </button>
				</td>
			</tr>
			<tr id ="saveBtnTr" height="35px" style="display:none;"> 
				<td width="28%" align="left">
				<button type="button" name="savebtn" id="newbtn" style="margin-left :15px; margin-top:0px"  
				    onClick="saveQuery();"> Save Query </button>
				</td>
			</tr>
		</table>	 
	</div>
</form>			 
</body>
 
