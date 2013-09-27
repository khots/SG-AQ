<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo"%>
<%@ taglib uri="/WEB-INF/multiSelectUsingCombo.tld" prefix="mCombo" %>
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld"
	prefix="dynamicExtensions"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link rel="stylesheet" type="text/css" href="css/styleSheet.css" />
<link rel="stylesheet" type="text/css"
	href="css/clinicalstudyext-all.css" />

<%@
	page language="java" contentType="text/html"
	import="edu.wustl.query.util.global.AQConstants,edu.wustl.query.actionForm.SaveQueryForm"%>
<%@ page import="edu.wustl.common.beans.NameValueBean"%>
<%@ page import="java.util.*"%>

<script>var imgsrc="/images/de/";</script>
<script language="JavaScript" type="text/javascript"
	src="javascripts/de/prototype.js"></script>
<script language="JavaScript" type="text/javascript"
	src="javascripts/de/scr.js"></script>
<script language="JavaScript" type="text/javascript"
	src="javascripts/de/combobox.js"></script>
<script language="JavaScript" type="text/javascript"
	src="/jss/ext-base.js"></script>
<script language="JavaScript" type="text/javascript"
	src="/jss/ext-all.js"></script>
<script language="JavaScript" type="text/javascript"
	src="javascripts/de/combos.js"></script>
<script language="JavaScript" type="text/javascript"
	src="/jss/ajax.js"></script>

<%
	SaveQueryForm form = (SaveQueryForm) request
			.getAttribute("saveQueryForm");
	String isQuerySaved = (String) request.getAttribute("querySaved");
	String isMyQuery = (String) request
			.getAttribute(AQConstants.IS_MY_QUERY);
	String valueOfchkBox;
	String multiSelectDisplay="display: none";
	String querySharedTo = form.getShareTo();
	if(querySharedTo.equalsIgnoreCase("users"))
	{
		multiSelectDisplay="display:block";
	}
	boolean chkDisabled = false;
	if (isMyQuery == null)
	{
		isMyQuery = "false";
		valueOfchkBox = "false";
		chkDisabled = true;
		form.setTitle("");
		form.setDescription("");
	}
	else
	{
		valueOfchkBox = "true";
	}
	String isSaveButtonDisable = "";
	String selectText = "--Select--";
	Long queryIdLong = form.getQueryId();
	String queryId = "" + queryIdLong;
	boolean isRadioButtonDisabled = false;
	if (isQuerySaved != null)
	{
		isSaveButtonDisable = "disabled='disabled'";
		isRadioButtonDisabled = true;
	}
%>

<script>Ext.onReady(function(){var myUrl= 'ShareQueryAjax.do?';var ds = new Ext.data.Store({proxy: new Ext.data.HttpProxy({url: myUrl}),reader: new Ext.data.JsonReader({root: 'row',totalProperty: 'totalCount',id: 'id'}, [{name: 'id', mapping: 'id'},{name: 'excerpt', mapping: 'field'}])});var combo = new Ext.form.ComboBox({store: ds,hiddenName: 'CB_coord',displayField:'excerpt',valueField: 'id',typeAhead: 'false',pageSize:15,forceSelection: 'true',queryParam : 'query',mode: 'remote',triggerAction: 'all',minChars : 3,queryDelay:500,lazyInit:true,emptyText:'--Select--',valueNotFoundText:'',selectOnFocus:'true',applyTo: 'coord'});combo.on("expand", function() {if(Ext.isIE || Ext.isIE7){combo.list.setStyle("width", "250");combo.innerList.setStyle("width", "250");}else{combo.list.setStyle("width", "auto");combo.innerList.setStyle("width", "auto");}}, {single: true});ds.on('load',function(){if (this.getAt(0) != null && this.getAt(0).get('excerpt').toLowerCase().startsWith(combo.getRawValue().toLowerCase())) {combo.typeAheadDelay=50;} else {combo.typeAheadDelay=60000}});});</script>

<script>
Ext.onReady(function(){
	var myUrl= 'ShareQueryAjax.do?';
	var ds = new Ext.data.Store({proxy: new Ext.data.HttpProxy({url: myUrl}),
	reader: new Ext.data.JsonReader({root: 'row',totalProperty: 'totalCount',id: 'id'}, [{name: 'id', mapping: 'id'},{name: 'excerpt', mapping: 'field'}])});
	var combo = new Ext.form.ComboBox({store: ds,hiddenName: 'principalInvestigatorId',displayField:'excerpt',valueField: 'id',typeAhead: 'true',pageSize:15,forceSelection: 'true',queryParam : 'query',mode: 'remote',lazyInit:'true',triggerAction: 'all',minChars : 3,emptyText:'<%=selectText%>',selectOnFocus:'true',applyTo: 'txtprincipalInvestigatorId' });
	var firsttimePI="true";
	combo.on("expand", function() {
		if(Ext.isIE || Ext.isIE7)
		{
			combo.list.setStyle("width", "250");
			combo.innerList.setStyle("width", "250");
		}
		else
		{
			combo.list.setStyle("width", "auto");
			combo.innerList.setStyle("width", "auto");
		}
		}, 		{single: true});
		ds.on('load',function(){
			if (this.getAt(0) != null && this.getAt(0).get('excerpt').toLowerCase().startsWith(combo.getRawValue().toLowerCase()))
			{combo.typeAheadDelay=50;
			}
			else
			{combo.typeAheadDelay=60000}
			});
		<%/*if (opr.equals(Constants.EDIT) ||  (showErrMsg  && piId!=0) || (deloperation && piId!=0) )
			 {*/%>
			ds.load({params:{start:0, limit:9999,query:''}});
            ds.on('load',function(){

					 if(firsttimePI == "true")
					{ combo.setValue('<%=queryId%>',false); firsttimePI="false";}
            });
		<%//}%>
			});
</script>
<script>
  
function checkForValidation()
{
	var tdId = "multiSelectId";
	var displayStyle = 	document.getElementById(tdId).style.display;
	if(displayStyle == "block")
	{
		var coords = document.getElementById('protocolCoordinatorIds');
		if(coords.options.length == 0)
		{
			alert("Please select atleast one user from the dropdown");
		}
		else
		{
			produceSavedQuery("save");
		}
	}
	else
	{
		produceSavedQuery("save");
	}
}
</script>

<html:html>
<head>
<link rel="stylesheet" type="text/css"
	href="css/advQuery/styleSheet.css" />
<script language="JavaScript" type="text/javascript"
	src="jss/advQuery/queryModule.js"></script>
<script language="JavaScript" type="text/javascript"
	src="jss/advQuery/script.js"></script>
<script language="JavaScript" type="text/javascript"
	src="jss/advQuery/overlib_mini.js"></script>
<script language="JavaScript" type="text/javascript"
	src="javascripts/de/ajax.js"></script>
<script type='text/JavaScript' src='jss/advQuery/scwcalendar.js'></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

</head>

<body onload="hideMultiselect()">
<html:errors />
<html:form styleId='saveQueryForm'
	action='<%=AQConstants.SAVE_QUERY_ACTION%>'>
	<html:hidden property="currentPage" value="prevToAddLimits" />
	<html:hidden property="showTree"/>
	<input type="hidden" name="isQuery" value="true">

	<table summary="" cellpadding="0" cellspacing="0" border="0"
		class="contentPage" width="100%" height="90%">

		<tr height="10%">
			<td colspan="4">
			<table summary="" cellpadding="3" cellspacing="0" border="0"
				width="100%">
				<tr class="tr_bg_blue1">
					<td colspan='4' height="1%"><span class="savedQueryHeading">
					<bean:message key="save.query" /> </span></td>
				</tr>
				<tr>
					<td width='5' class="formFieldNoBordersQuery">*</td>
					<td style="font-family: verdana; font-size: 0.8em;"><b> <bean:message
						key="query.title" /> </b></td>
					<td class="formFieldNoBordersQuery"><html:text
						styleClass="formFieldSized" maxlength="255" styleId="title"
						property="title" /></td>
				</tr>
				<tr>
					<td width='5' class="formFieldNoBordersQuery"></td>
					<td style="font-family: verdana; font-size: 0.8em;"><b><bean:message
						key="query.description" /></b></td>
					<td class="formFieldNoBordersQuery"><html:textarea
						styleClass="formFieldSized" cols="32" rows="2"
						property="description" styleId="description">
					</html:textarea></td>
				</tr>
				<tr><td colspan='4' style="padding-bottom:5px;">
									<table cellpadding="0" cellspacing="0" width="100%" >
									<tr>
									<td id="checkbox2" align="right"  >
									<input type="checkbox"  id='showAll'  onclick="javaScript:showAttributes(this);" value="${entity.id}">
										<label style="font-size: 0.7em; font-family: verdana;">
									   <b>Show all Attributes</b>
									   </label></td>
									</tr>
								</table>
								</td>
				</tr>
							
				<tr>
					<td colspan="4" height="2">&nbsp;</td>
				</tr>
				<tr class="tr_bg_blue1">
					<td colspan='3' height="20"><span class="savedQueryHeading">
					<bean:message key="savequery.setConditionParametersTitle" /> </span></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr valign='top'>

								<td valign='top'  colspan='4' height="150px">
								<div  valign='top' id="parameterlist" style=" min-height:50px;overflow-x:auto;">
								   <%=request.getAttribute(AQConstants.HTML_CONTENTS)%>
			    				 </div>
								</td>
							</tr>
							
							
	
		<tr>
			<td colspan="4" height="20" />
		</tr>
		<tr>
			<td></td>
		</tr>

		<tr>
			<td width="14%" align="left"><label
				style="font-size: 1em; font-family: verdana;"><b><bean:message
				key="shareTo.title" /></label></td>
			<td><label style="font-size: 1em; font-family: verdana;"
				valign="center"><b><html:radio property="shareTo"
				styleId="noneRadio" value="none" onclick="hideMultiselect()"
				disabled='<%=isRadioButtonDisabled%>' /> <bean:message
				key="shareTo.none" /></b> </label>&nbsp;<label
				style="font-size: 1em; font-family: verdana;"><bean:message
				key="shareTo.none.message" /></label></td>
			<td></td>

		</tr>
		<tr>
			<td></td>
			<td><label style="font-size: 1em; font-family: verdana;"><b><html:radio
				property="shareTo" styleId="allRadio" value="all"
				onclick="hideMultiselect()" disabled='<%=isRadioButtonDisabled%>' />
			<bean:message key="shareTo.all" /> </b></label>&nbsp;<label
				style="font-size: 1em; font-family: verdana;"><bean:message
				key="shareTo.all.message" /></label></td>
		</tr>
		<tr>
			<td></td>
			<td><label style="font-size: 1em; font-family: verdana;"><b><html:radio
				property="shareTo" styleId="shareToRadio" value="users"
				onclick="userSelected()" disabled='<%=isRadioButtonDisabled%>' /> <bean:message
				key="shareTo.users" /></b> </label>&nbsp;<label
				style="font-size: 1em; font-family: verdana;"><bean:message
				key="shareTo.users.message" /></label></td>
		</tr>
		<tr>
		<tr height="15"></tr>
			<td></td>
			<td id="multiSelectId" nowrap="" colspan="1" class="black_ar_new" style="display:none">
			<mCombo:multiSelectUsingCombo identifier="coord" styleClass="black_ar_new"  size="15" addButtonOnClick="moveOptions('coord','protocolCoordinatorIds', 'add')" removeButtonOnClick="moveOptions('protocolCoordinatorIds','coord', 'edit')" selectIdentifier="protocolCoordinatorIds" collection='<%=(List)request.getAttribute("selectedCoordinators")%>'/>
			</td>
		</tr>
		<tr>
			<td class="small_txt_grey" colspan="3"><html:checkbox
				property="editQuery" styleId="editQuery" value="<%=valueOfchkBox%>"
				disabled='<%=chkDisabled%>' />&nbsp; <bean:message key="edit.query" /></td>
		</tr>
		<tr>
			<td colspan='4' align="left">&nbsp;</td>
		</tr>
		<tr>
			<td colspan='4' align="left"><input type="hidden"
				name="queryString" id="queryString" value="" /> <input
				type="hidden" name="buildQueryString" id="buildQueryString" value="" />
			<%
				if (isQuerySaved != null && isQuerySaved.equals("true")) {
			%> <input type="button" name="close" value="Back to Dashboard"
				class="actionButton" onClick="showdashboard()" /> <%
 	} else {
 %> <input type="button" name="save" id="saveBtn" value="Save"
				class="actionButton" onClick="checkForValidation()"
				<%=isSaveButtonDisable%> /> <input type="button" name="cancel"
				id="cancelBtn" value="Cancel" class="actionButton"
				onClick="cancelFromSaveQuery();" /> <%
 	}
 %>
			</td>
		</tr>
	</table>
</html:form>
</body>
</html:html>
