<%@ page import="java.util.*"%>
<%@ page import="java.lang.*"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ page import="edu.wustl.query.util.global.AQConstants"%>
<%@ page import="edu.wustl.query.util.global.Utility"%>
<%@ page import="edu.wustl.query.util.global.Variables"%>
<%@ page import="edu.wustl.common.query.queryobject.impl.QueryTreeNodeData"%>
<%@ page import="edu.wustl.query.actionForm.CategorySearchForm"%>
<%@ page import="edu.wustl.common.beans.NameValueBean"%>
<%@ page import="edu.wustl.common.util.global.ApplicationProperties"%>
<html>
<head>
	<meta http-equiv="Content-Language" content="en-us">
	<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
	
	
	<script type="text/javascript" src="dhtmlx_suite/js/dhtmlxcommon.js"></script>
	<script type="text/javascript" src="dhtmlx_suite/js/dhtmlxtree.js"></script>
	<link rel="stylesheet" type="text/css" href="dhtmlx_suite/css/dhtmlxtree.css" />
	<link rel="stylesheet" type="text/css" 	href="css/advQuery/catissue_suite.css" />
	<link rel="stylesheet" type="text/css" href="css/advQuery/styleSheet.css" />
	
	<script src="jss/advQuery/script.js"></script>
</head>
<body onload="initTreeView()">

<%
	String callAction = AQConstants.CONFIGURE_GRID_VIEW_ACTION;
	CategorySearchForm form = (CategorySearchForm) request
			.getAttribute("categorySearchForm");
	String currentSelectedNodeInTree = form.getCurrentSelectedNodeInTree();
	String showSelected = "false";
	List selectedColumnNameValueBeanList = form.getSelColNVBeanList();
	String appName = ApplicationProperties.getValue("app.name");	
%>
 
<table cellpadding='0' cellspacing='0' border='0' align='center'
		style="width: 100%; height: 100%;">
	<tr height="1%">
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
	<tr valign="top">
		<td valign="top" width="100%" colspan="3" >
			<html:form method="GET" action="<%=callAction%>">
				<html:hidden property="operation" value="" />
								
				<table border="0" width="400" cellspacing="0" cellpadding="0"
					bgcolor="#FFFFFF" height="90%" bordercolorlight="#000000">
					<tr>
						<td width="1px">&nbsp;</td>
						<td valign="top" width="100"></td>
					</tr>
			
					<tr>
						<td width="1px">&nbsp;</td>
						<td valign="top" colspan="8" width="100%">
						<%if(!appName.equalsIgnoreCase("ClinPortal")){%>
							<bean:message key="query.defineGridResultsView.message" />
						<%}%>
						</td>
					</tr>
					<tr>
						<td width="1px">&nbsp;</td>
						<td valign="top" width="90%" height="90%">
						<div id="treeBox"
							style="background-color: white; overflow: auto; height: 270; width: 260; 
									border-left: solid 1px; border-right: solid 1px; border-top: solid 1px; 
									border-bottom: solid 1px;"></div>
						</td>
						<td width="1%">&nbsp;</td>
						<td align="center" valign="center" width="">
							<html:button styleClass="actionButton" property="shiftRight" styleId="shiftRight"
								onclick="moveOptionsRight(this.form.columnNames, this.form.selectedColumnNames);">
								<bean:message key="buttons.addToView" />
							</html:button> 
						<br /> <br />
							<html:button styleClass="actionButton" property="shiftLeft"
								styleId="shiftLeft"
								onclick="moveOptionsLeft(this.form.selectedColumnNames, this.form.columnNames);">
								<bean:message key="buttons.deleteFromView" />
							</html:button></td>
						<td width="1%">&nbsp;</td>
						<td class="" valign="top" width="60" height="85%"><!-- Mandar : 434 : for tooltip -->
							<html:select property="selectedColumnNames" styleClass=""
								styleId="selectedColNamesId" size="16" multiple="true">
								<html:options collection="selectedColumnNameValueBeanList"
									labelProperty="name" property="value" />
							</html:select></td>
						<td width="1%">&nbsp;</td>
						<td align="center" valign="center">
							<html:button styleClass="actionButton" property="shiftUp"
								styleClass="actionButton" styleId="shiftUp"
								onclick="moveUp(this.form.selectedColumnNames);">
								<bean:message key="buttons.up" />
							</html:button> 
							<br /><br />
			
							<html:button styleClass="actionButton" property="shiftDown"
								styleClass="actionButton" styleId="shiftDown"
								onclick="moveDown(this.form.selectedColumnNames)">
								<bean:message key="buttons.down" />
							</html:button></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td id= "normalizedQueryText" style="visibility:hidden;" align="left" height="2%" colspan="6">
							<html:checkbox property="normalizedQuery" value="true" styleId="normalizedQuery" />
							<span valign="top" class="black_ar">
								<bean:message key="query.normalized.view" />
							</span>			 	 				 
						</td> 
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td colspan="6" align="left" valign="top">
						<table border="0" cellpadding="0" cellspacing="0" width="100%">
							<tr height="1px">
								<td align="center" height="2%"><html:button
									styleClass="actionButton" styleId="cancelBtnId"
									property="configureButton"
									onclick="onSubmit(this.form.selectedColumnNames,'back');">
									<bean:message key="query.back.button" />
								</html:button></td>
								<td align="center" height="2%"><html:button
									styleClass="actionButton" styleId="restoreDefaultBtnId"
									property="redefineButton"
									onclick="onSubmit(this.form.selectedColumnNames,'restore');">
									<bean:message key="query.restoreDefault.button" />
								</html:button></td>
								<td align="center" height="2%"><html:button
									styleClass="actionButton" styleId="finishBtnId"
									property="configButton"
									onclick="onSubmit(this.form.selectedColumnNames,'finish');">
									<bean:message key="query.finish.button" />
								</html:button></td>
								<td width="40%"></td>
							</tr>
						</table>
						</td>
					</tr>
				</table>				
			</html:form>
		</td>
	</tr>
</table>

<SCRIPT LANGUAGE="JavaScript">

	function addOption(theSel, theText, theValue)
    {
	    var newOpt = new Option(theText, theValue);
	    var selLength = theSel.length;
	    var exists="false";
	    for(var i=0;i<selLength; i++)
	    {
			if(theSel.options[i].value==theValue) //&& (theSel.options[i].indexof("class")) != 0 && (theSel.options[i].indexof("root"))!=0)
				{
					exists="true";
					break;
				}
	    }
	    if(exists=="false")
		    theSel.options[selLength] = newOpt;
	}


    function moveOptionsRight(theSelFrom, theSelTo)
    {
		var list=tree.getAllChecked();
		var selectedAttrs = list.split(",");
	    var selLength = selectedAttrs.length;
	    var selectedText = new Array();
	    var selectedValues = new Array();
	    var selectedCount = 0;
	    if(list=="")
		{
			alert("Please select column name.");
		} else
		{
			var i;

			// Find the selected Options in reverse order
			// and delete them from the 'from' Select.
			for(i=selLength-1; i>=0; i--)
			{
				var selectedOption = selectedAttrs[i];
				if(selectedOption.indexOf("Root") != 0)
				{
					if(selectedOption.indexOf("class") != 0)
					{
						var nodetext = tree.getItemText(selectedOption);
						var parentId = tree.getParentId(selectedOption);
						var parentNodeText = tree.getItemText(parentId);
						var displaySelectedColumn = parentNodeText + " : " + nodetext;
						selectedText[selectedCount] = displaySelectedColumn;
						selectedValues[selectedCount] = selectedOption;
						//deleteOption(theSelFrom, i);
						selectedCount++;
					}
				}
			}

			// Add the selected text/values in reverse order.
			// This will add the Options to the 'to' Select
			// in the same order as they were in the 'from' Select.
			for(i=selectedCount-1; i>=0; i--)
			{
				//alert("selectedText[i].."+selectedText[i]+"  selectedValues[i].."+selectedValues[i]);
				addOption(theSelTo, selectedText[i], selectedValues[i]);
			}
		}
    }
   	function moveOptionsLeft(theSelFrom, theSelTo)
	{
		var selLength = theSelFrom.length;
		var selectedCount = 0;
		var i;
		for(i=selLength-1; i>=0; i--)
		{
		    if(theSelFrom.options[i].selected)
		    {
		    	selectedCount++;
	  			deleteOption(theSelFrom, i);
    		}
		}
		if(selectedCount==0)
			alert("Please select column name.");
	}
	function deleteOption(theSel, theIndex)
    {
	    var selLength = theSel.length;
	    if(selLength>0)
	    {
			var selItem = theSel.options[theIndex].value;
			tree.setCheck(selItem,false);
			theSel.options[theIndex] = null;

	    }
   	}
	function moveUpAllSelected(theSelFrom)
	{
		var selLength = theSelFrom.length;
		var selectedCount = 0;
		var i;
		for(i=selLength-1; i>=0; i--)
		{
		    if(theSelFrom.options[i].selected)
		    {
		    	selectedCount++;
				moveUpOneByOne(theSelFrom,i);
			}
		}
	}
	function moveUpOneByOne(obj,index)
	{
	  var currernt;
	  var reverse;
	  var currerntValue;
	  var reverseValue;
	  //obj.options[obj.options.selectedIndex].
	  if(index > 0)
	  {
	    current = obj.options[index].text;
	    currentValue = obj.options[index].value;
	    reverse = obj.options[index-1].text;
	    reverseValue = obj.options[index-1].value;
	    obj.options[index].text = reverse;
	    obj.options[index].value = reverseValue;
	    obj.options[index-1].text = current;
	    obj.options[index-1].value = currentValue;
	    self.focus();
	    index--;
	  }
	}
    function typeChange(namesArray,valuesArray)
    {
	    var columnsList = "columnNames";
	    ele = document.getElementById(columnsList);
	    //To Clear the Combo Box
	    ele.options.length = 0;

	    //ele.options[0] = new Option('-- Select --','-1');
	    var j=0;
	    //Populating the corresponding Combo Box
	    for(i=0;i<namesArray.length;i++)
	    {
	    	ele.options[j++] = new Option(namesArray[i],valuesArray[i]);
	    }
    }


	function moveUp(obj)
	{
	  var currernt;
	  var reverse;
	  var currerntValue;
	  var reverseValue;

	  if(obj.options[obj.options.selectedIndex].index > 0)
	  {
	    current = obj.options[obj.options.selectedIndex].text;
	    currentValue = obj.options[obj.options.selectedIndex].value;
	    reverse = obj.options[obj.options[obj.options.selectedIndex].index-1].text;
	    reverseValue = obj.options[obj.options[obj.options.selectedIndex].index-1].value;
	    obj.options[obj.options.selectedIndex].text = reverse;
	    obj.options[obj.options.selectedIndex].value = reverseValue;
	    obj.options[obj.options[obj.options.selectedIndex].index-1].text = current;
	    obj.options[obj.options[obj.options.selectedIndex].index-1].value = currentValue;
	    self.focus();
	    obj.options.selectedIndex--;
	  }
	}

	function moveDown(obj)
	{
	  var currernt;
	  var reverse;
	  var currerntValue;
	  var reverseValue;
	  if(obj.options[obj.options.selectedIndex].index != obj.length-1)
	  {
	    current = obj.options[obj.options.selectedIndex].text;
	    currentValue = obj.options[obj.options.selectedIndex].value;
	    reverse = obj.options[obj.options[obj.options.selectedIndex].index+1].text;
	    reverseValue = obj.options[obj.options[obj.options.selectedIndex].index+1].value;
	    obj.options[obj.options.selectedIndex].text = reverse;
	    obj.options[obj.options.selectedIndex].value = reverseValue;
	    obj.options[obj.options[obj.options.selectedIndex].index+1].text = current;
	    obj.options[obj.options[obj.options.selectedIndex].index+1].value = currentValue;
	    self.focus();
	    obj.options.selectedIndex++;
	  }
	}
    function selectOptions(element)
	{
		for(i=0;i<element.length;i++)
		{
			element.options[i].selected=true;
		}
	}
    function onSubmit(theSelTo,operation)
	{


		if(operation == 'finish')
		{
			if(theSelTo.length==0)
			{
				alert("We need to add atleast one column to define view");
				return ;
			}
			selectOptions(document.forms[0].selectedColumnNames);
		}
		document.forms[0].operation.value = operation;
		
		
		document.forms[0].normalizedQuery.value = document.forms[0].normalizedQuery.checked
		document.forms[0].action =  "ConfigureGridView.do";
		document.forms[0].submit();
	}

</script>
<script>
	var tree;
	function initTreeView()
	{
		tree = new dhtmlXTreeObject("treeBox", "100%", "100%", 0);
		tree.setImagePath("dhtmlx_suite/imgs/");
		//tree.setOnClickHandler();
		tree.enableCheckBoxes(1);
		tree.enableThreeStateCheckboxes(true);

		<%List treeData = (List) request.getSession().getAttribute(AQConstants.TREE_DATA);
			if (treeData != null && treeData.size() != 0) {

				Iterator itr = treeData.iterator();
				String nodeColapseCode = "";
				while (itr.hasNext()) {
					QueryTreeNodeData data = (QueryTreeNodeData) itr.next();
					String parentId = "0";
					if (!data.getParentIdentifier().equals("0")) {
						parentId = data.getParentIdentifier().toString();
					}
					String nodeId = data.getIdentifier().toString();
					if (currentSelectedNodeInTree != null
							&& currentSelectedNodeInTree.equals(nodeId)) {
						showSelected = "true";
					}
					String img = "results.gif";
		%>
		tree.insertNewChild("<%=parentId%>","<%=nodeId%>","<%=data.getDisplayName()%>",0,"<%=img%>","<%=img%>","<%=img%>","");
		tree.setUserData("<%=nodeId%>","<%=nodeId%>","<%=data%>");
		tree.setItemText("<%=nodeId%>","<%=data.getDisplayName()%>","<%=data.getDisplayName()%>");
		if("<%=showSelected%>" == "true")
		{
			tree.setCheck("<%=currentSelectedNodeInTree%>",true);
			tree.openItem("<%=currentSelectedNodeInTree%>");
		}
					 <%if (selectedColumnNameValueBeanList != null) {
						for (int i = 0; i < selectedColumnNameValueBeanList
								.size(); i++) {
							NameValueBean nameValueBean = (NameValueBean) selectedColumnNameValueBeanList
									.get(i);
							String name = nameValueBean.getName();
							String value = nameValueBean.getValue();
							if (nodeId.equalsIgnoreCase(value)) {
					%>
		tree.setCheck("<%=value%>",true);
		tree.openItem("<%=value%>");
					<%		} //end of if
						}//end of for
					} //end of if
					%>

		<%	}// end of while
		} // end of if
		%>
	}

	function shiftRight()
	{
		var list=tree.getAllChecked();
		alert(list);
	}
</script>
</body>
</html>