<%@ page import="edu.wustl.query.actionForm.CategorySearchForm"%>
<%@ page import="edu.wustl.query.util.global.AQConstants"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<html>
<head>
<script src="jss/advQuery/queryModule.js"></script>
<script type="text/javascript" src="jss/advQuery/ajax.js"></script>
<script>
<%String height = "";
			String currentPage = (String) request
					.getAttribute(AQConstants.CURRENT_PAGE);
			System.out.println("currentPage         " + currentPage);
			String formName = AQConstants.categorySearchForm;
			String SearchCategory = AQConstants.SearchCategory;%>
	if(navigator.appName == "Microsoft Internet Explorer")
		{
			<%height = "60%";%>
		}
		else
		{
			<%height = "600";%>
		}

	function checkKey(event)
	{
		var platform = navigator.platform.toLowerCase();
		var key;
		if (event.keyCode) key = event.keyCode;
			else if (event.which) key = event.which;
		if (platform.indexOf("mac") != -1)
		{
			if (key == 13) { event.returnValue=false; }
		}
		else
		{
			event.returnValue=true;
		}
		retrieveEntities(key);
	}

	function retrieveEntities(key)
	{
		var string = document.forms[0].textField.value;
		if (string == "")
		{
			var element = document.getElementById('resultSet');
			element.innerHTML = string;
			return ;
		}
		if(string.length > 4)
		{
			retriveSearchedEntities('<%=SearchCategory%>','<%=formName%>','<%=currentPage%>', key);
			return ;
		}
	}

</script>
</head>
<%! boolean mac = false; %>
<%
	Object os = request.getHeader("user-agent");
	if (os != null && os.toString().toLowerCase().indexOf("mac") != -1) {
		mac = true;
	}%>
	

<body>

<html:errors />

<html:form method="GET" action="SearchCategory.do" focus="textField">
	<html:hidden property="currentPage" value="" />
	<html:hidden property="showTree" styleId="showTree"/>
	<table border="0" width="100%" cellspacing="0" cellpadding="0"
		bgcolor="#FFFFFF" height="100%" bordercolorlight="#000000"
		id="table11">
		<tr>
			<td valign="top">
			<table border="0" width="100%" valign="top" cellspacing="0"
				height="100%">

				<tr bordercolorlight="#000000">
					<td height="1%">&nbsp;</td>
					<td width="75%" valign="top">
						<%if (mac) {%>
						<html:text property="textField"
							styleId="searchTextBoxId" onkeydown="setFocusOnSearchButton(event)" onkeypress="checkKey(event)"/>
							<%
							} else {
						%>
						<html:text property="textField"
							styleId="searchTextBoxId" onkeydown="setFocusOnSearchButton(event)" onkeyup="checkKey(event)"/>
						<%} %>
					</td>
					<td width="25%" valign="top"><input type="button"
						value="      Go       " name="searchButton" id="searchButton"
						onclick="retriveSearchedEntities('<%=SearchCategory%>','<%=formName%>','<%=currentPage%>');" />
					</td>
				</tr>
				<tr height="1%">
					<td height="1%">&nbsp;</td>
					<td height="1%" align="left" valign="top" colspan="3"><font
						face="Arial" size="2"><bean:message
						key="query.chooseCategoryLabel" /></font></td>
				</tr>
				<tr id="collapsableHeader" valign="top" class="row" width="97%">
					<td valign="top" height="1%">&nbsp;</td>
					<td id="advancedSearchHeaderTd" height="1%" valign="top"
						bgcolor="#EAEAEA"
						style="border-left: solid 1px; border-top: solid 1px; border-bottom: solid 1px; bordercolorlight: #EAEAEA">
					<b><font face="Arial" size="2"><bean:message
						key="query.advancedSearchHeader" /></font></b></td>
					<th id="imageContainer" valign="center" align="right" height="1%"
						bgcolor="#EAEAEA"
						style="border-top: solid 1px; border-bottom: solid 1px; border-right: solid 1px; bordercolorlight: #EAEAEA;">
					<a id="image" onClick="expand('<%=currentPage%>')"
						style="display: block"> <img
						src="images/advQuery/nolines_plus.gif" hspace="3" vspace="3" id="collapsableImgId"/> </a></th>
					<td height="1%">&nbsp;</td>
				</tr>
				<tr valign="top">
					<td valign="top" style="display: none" id="td1">&nbsp;</td>
					<td colspan="2" valign="top" height="1px">
					<table valign="top" class="collapsableTable" style="display: none"
						width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF"
						id="collapsableTable">
						<tr id="class_view">
							<td class="standardTextQuery"><html:checkbox
								property="classChecked" styleId="advancedClassId"
								onclick="setIncludeDescriptionValue()" value="on">
								<bean:message key="query.class" />
							</html:checkbox></td>
						</tr>
						<tr id="attribute_view">
							<td class="standardTextQuery"><html:checkbox
								property="attributeChecked" styleId="advancedAttributeId"
								onclick="setIncludeDescriptionValue()" value="on">
								<bean:message key="query.attribute" />
							</html:checkbox></td>
						</tr>
						<tr id="permissible_view">
							<td class="standardTextQuery"><html:checkbox
								property="permissibleValuesChecked"
								styleId="advancedPermissibleId"
								onclick="permissibleValuesSelected(this)" value="on">
								<bean:message key="query.permissibleValues" />
							</html:checkbox></td>
						</tr>
						<tr id="description_view">
							<td class="standardTextQuery"><html:checkbox
								property="includeDescriptionChecked"
								styleId="advancedDescriptionId" value="on">
								<bean:message key="query.includeDescription" />
							</html:checkbox></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr id="radio_view">
							<td class="standardTextQuery"><!-- Bug #5131: Removing the radios until Concept Code search is fixed  -->
							<!-- html:radio property="selected" value="text_radioButton" onclick="radioButtonSelected(this)"/--><!--bean:message key="query.text"/-->
							<!-- html:radio property="selected" value="conceptCode_radioButton" onclick="radioButtonSelected(this)" disabled="true" /--><!--bean:message key="query.conceptCode"/-->
							</td>
						</tr>
					</table>
					</td>
					<td valign="top" style="display: none" id="td3">&nbsp;</td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
				<tr valign="top" class="row" width="98%" height="1%">
					<td height="1%">&nbsp;</td>
					<td colspan="2" height="1%" valign="top" bgcolor="#EAEAEA"
						class="tdWithoutBottomBorder"><img
						src="images/advQuery/ic_search.gif" id="searchImg" hspace="1"
						vspace="1" /> <b><font face="Arial" size="2"
						id="searchResultsId"><bean:message
						key="query.searchResults" /></font> </b></td>
					<td height="1%">&nbsp;</td>
				</tr>
				<tr valign="top" class="row" width="100%" height="100%">
					<td width="100%" height="100%">&nbsp;</td>
					<td height="100%" width="100%" colspan="2" id='resultSetTd'
						class="tdWithoutTopBorder">
					<div id="resultSet"
						style="border: padding :   4px; width: 230px; height: 550px; overflow: auto;"></div>
					</td>
					<script>
							if('<%=currentPage%>' == 'DefineResultsView')
							{
								resultSetTag = document.getElementById("resultSet");
								resultSetTag.style.height= (document.body.clientHeight - 230)+ 'px';
							}
						</script>
					<td>&nbsp;</td>
				</tr>

			</table>
			</td>
		</tr>
	</table>
</html:form>
</body>
</html>