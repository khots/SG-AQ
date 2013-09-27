<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/PagenationTag.tld" prefix="custom" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Hashtable"%>
<%@ page import="edu.wustl.catissuecore.actionForm.AdvanceSearchForm"%>
<%@ page import="edu.wustl.catissuecore.util.global.Constants"%>
<%@ page import="edu.wustl.catissuecore.util.global.AppUtility"%>
<%@ page import="edu.wustl.catissuecore.util.global.Variables"%>
<%@ page language="java" isELIgnored="false"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script src="jss/script.js"></script>
<script type="text/javascript" src="jss/ajax.js"></script> 
<style>
.active-column-0 {width:30px}
tr#hiddenCombo
{
 display:none;
}
</style>
<head>
<%
	int pageNum = Integer.parseInt((String)request.getAttribute(Constants.PAGE_NUMBER));
	
	int totalResults = ((Integer)session.getAttribute(Constants.TOTAL_RESULTS)).intValue();
	int numResultsPerPage = Integer.parseInt((String)session.getAttribute(Constants.RESULTS_PER_PAGE));
	String pageName = "SpreadsheetView.do";	
	String checkAllPages = (String)session.getAttribute("checkAllPages");
	AdvanceSearchForm form = (AdvanceSearchForm)session.getAttribute("advanceSearchForm");
	List columnList = (List) session.getAttribute(Constants.SPREADSHEET_COLUMN_LIST);
	if(columnList==null)
		columnList = (List) request.getAttribute(Constants.SPREADSHEET_COLUMN_LIST);

	columnList.add(0," ");
	List dataList = (List) request.getAttribute(Constants.PAGINATION_DATA_LIST);
	
	String pageOf = (String)request.getAttribute(Constants.PAGE_OF);
	String title = pageOf + ".searchResultTitle";
	boolean isSpecimenData = false;	
	int IDCount = 0;
%>
		

	<script language="javascript">
		var colZeroDir='ascending';

		function getData()
		{	//ajax call to get update data from server
			var request = newXMLHTTPReq();			
			var handlerFunction = getReadyStateHandler(request,displayValidationMessage,true);	
			request.onreadystatechange = handlerFunction;			
			var actionURL = "updateSessionData=updateSessionData";		
			var url = "ValidateQuery.do";
			request.open("POST",url,true);	
			request.setRequestHeader("Content-Type","application/x-www-form-urlencoded");	
			request.send(actionURL);		
		}

		function displayValidationMessage(message)
		{
			//var message contains space " " if the message is not to be shown.   
			if (message != null && message == " ") 		// do not show popup
			{
				onAddToCart();
			}
			else
			{
				var isChecked = updateHiddenFields(); // if atleast one check box is checked.
				if (isChecked == "true")
				{
					var r=confirm(message);
					if (r==true)
					{
						onAddToCart();
					}
				}
				else
				{
					alert("Please select at least one checkbox");
				}
			}
		}
		
		function onAddToCart()
		{
			var isChecked = updateHiddenFields();
			var chkBox = document.getElementById('checkAll');
			var isCheckAllAcrossAllChecked = chkBox.checked;
			
		    if(isChecked == "true")
		    {
			    var pageNum = "<%=pageNum%>";
				var action;
                var isQueryModule = "<%=pageOf.equals(Constants.PAGE_OF_QUERY_MODULE)%>";
                <%if (pageOf.equals(Constants.PAGE_OF_QUERY_MODULE))
                {%>
				
				 action = "AddDeleteCart.do?operation=add&pageNum="+pageNum+"&isCheckAllAcrossAllChecked="+isCheckAllAcrossAllChecked;
				  document.forms[0].target = "gridFrame";
				<%} else {%>
				
				
			     action = "ShoppingCart.do?operation=add&pageNum="+pageNum+"&isCheckAllAcrossAllChecked="+isCheckAllAcrossAllChecked ;
				 document.forms[0].target = "myframe1";
				<%}%>

				document.forms[0].operation.value="add";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}
		
		function onExport()
		{
			var isChecked = updateHiddenFields();
			  var pageNum = "<%=pageNum%>";
			var chkBox = document.getElementById('checkAll');
			var isCheckAllAcrossAllChecked = chkBox.checked;
		    if(isChecked == "true")
		    {
				var action = "SpreadsheetExport.do?pageNum="+pageNum+"&isCheckAllAcrossAllChecked="+isCheckAllAcrossAllChecked ;
				document.forms[0].operation.value="export";
				document.forms[0].action = action;
				//document.forms[0].target = "_blank";
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}
		//function that is called on click of Define View button for the configuration of search results
		function onSimpleConfigure()
		{
				action="ConfigureSimpleQuery.do?pageOf=pageOfSimpleQueryInterface";
				document.forms[0].action = action;
				document.forms[0].target = "_parent";
				document.forms[0].submit();
		}

		function onAdvanceConfigure()
		{
				action="ConfigureAdvanceSearchView.do?pageOf=pageOfQueryResults";
				document.forms[0].action = action;
				document.forms[0].target = "myframe1";
				document.forms[0].submit();
		}
		function onQueryResultsConfigure()
		{
			action="DefineQueryResultsView.do?pageOf=pageOfQueryModule";
			document.forms[0].action = action;
			document.forms[0].target = "<%=Constants.GRID_DATA_VIEW_FRAME%>";
			document.forms[0].submit();
		}
		function onRedefineSimpleQuery()
		{
			action="SimpleQueryInterface.do?pageOf=pageOfSimpleQueryInterface&operation=redefine";
			document.forms[0].action = action;
			document.forms[0].target = "_parent";
			document.forms[0].submit();
		}
		function onRedefineAdvanceQuery()
		{
			action="AdvanceQueryInterface.do?pageOf=pageOfAdvanceQueryInterface&operation=redefine";
			document.forms[0].action = action;
			document.forms[0].target = "_parent";
			document.forms[0].submit();
		}
		function onRedefineDAGQuery()
		{
			waitCursor();
			document.forms[0].action='SearchCategory.do?currentPage=resultsView';
			document.forms[0].target = "_parent";
			document.forms[0].submit();
			hideCursor();
		}
		var selected;

		function addCheckBoxValuesToArray(checkBoxName)
		{
			var theForm = document.forms[0];
		    selected=new Array();
		
		    for(var i=0,j=0;i<theForm.elements.length;i++)
		    {
		 	  	if(theForm[i].type == 'checkbox' && theForm[i].checked==true)
			        selected[j++]=theForm[i].value;
			}
		}
		
		function setDefaultView(element)
		{
			action="DefaultSpecimenView.do?pageOf=pageOfQueryResults&<%=Constants.SPECIMENT_VIEW_ATTRIBUTE%>="+element.checked+"&view=<%=Constants.SPECIMEN%>"+"&isPaging=false";
			document.forms[0].action = action;
			document.forms[0].target = "myframe1";
			document.forms[0].submit();
		}
		function callAction(action)
		{
			document.forms[0].action = action;
			document.forms[0].submit();
		}
		function setCheckBoxState()
		{
		   if(document.getElementById('checkAll'))
		   {
			var chkBox = document.getElementById('checkAll');
			var isCheckAllAcrossAllChecked = chkBox.checked;
		<%if(checkAllPages != null && checkAllPages.equals("true"))
			{%>
			chkBox.checked = true;
				rowCount = mygrid.getRowsNum();
				for(i=1;i<=rowCount;i++)
				{
					var cl = mygrid.cells(i,0);
					if(cl.isCheckbox())
					cl.setChecked(true);
				}
		<%}%>
		}
		}
//this function is called after executing ajax call from checkAllOnThisPage function.
function checkAllOnThisPageResponse()
{
}

//document.forms[0].checkAllPages.value = true;

	</script>
	<%
		String configAction = new String();
			String redefineQueryAction = new String();
			if(pageOf.equals(Constants.PAGE_OF_SIMPLE_QUERY_INTERFACE))
			{
		configAction = "onSimpleConfigure()";
		redefineQueryAction = "onRedefineSimpleQuery()";
			}
			else if(pageOf.equals("pageOfQueryModule"))
			{
		configAction = "onQueryResultsConfigure()";
		redefineQueryAction = "onRedefineDAGQuery()";
			}
			else
			{
		configAction = "onAdvanceConfigure()";
		redefineQueryAction = "onRedefineAdvanceQuery()";
			}
			boolean mac = false;
			Object os = request.getHeader("user-agent");
			if(os!=null && os.toString().toLowerCase().indexOf("mac")!=-1)
			{
		mac = true;
			}
			String height = "100%";		
			if(mac) 
			{
		/* mac gives problem if the values aer specified in percentage*/
			  height="500";
			}
	%>
	<!-- Mandar : 434 : for tooltip -->
	<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>
</head>
<body onload="setCheckBoxState()">

<!-------new--->
 <!--Prafull:Added errors tag inside the table-->
<table width="100%" border="0" cellpadding="0" cellspacing="0" class="maintable">
<tr>
		<td class="td_color_bfdcf3">
<logic:equal name="pageOf" value="<%=Constants.PAGE_OF_SIMPLE_QUERY_INTERFACE%>">
			<table border="0" cellpadding="0" cellspacing="0">
		      <tr>
				<td class="td_table_head">
					<span class="wh_ar_b">
						Simple Query
					</span>
				</td>
		        <td>
					<img src="images/uIEnhancementImages/table_title_corner2.gif" alt="Page Title - Search Results" width="31" height="24" hspace="0" vspace="0" />
				</td>
		      </tr>
		    </table>
 
		</td>
	  </tr>
	   <tr>
		<td class="tablepadding">
			<table width="100%" border="0" cellpadding="0" cellspacing="0">
			
      <tr>
        <td width="90%" valign="bottom" class="td_tab_bg">&nbsp;</td>
      </tr>
	 
    </table>
	</logic:equal>
	<table width="100%" border="0" cellpadding="1" cellspacing="0" class="whitetable_bg">    
      <tr>
        <td align="left" ><%@ include file="/pages/content/common/ActionErrors.jsp" %></td>
      </tr>
       
<html:form action="<%=Constants.SPREADSHEET_EXPORT_ACTION%>" style="margin:0;padding:0;">
<html:hidden property="checkAllPages" value=""/>	

	 
		
		<tr valign="top" width="100%">
			<td  width="100%" valign="top" >
<!--  **************  Code for New Grid  *********************** -->
				<script>
					/* 
						to be used when you want to specify another javascript function for row selection.
						useDefaultRowClickHandler =1 | any value other than 1 indicates you want to use another row click handler.
						useFunction = "";  Function to be used. 	
					*/
					var useDefaultRowClickHandler =1;
					var useFunction = "search";	
				</script>
				<%@ include file="/pages/content/search/AdvanceGrid.jsp" %>
<!--  **************  Code for New Grid  *********************** -->
			</td>
		</tr>
 
	<tr>
		<td><html:hidden property="operation" value=""/></td>
	</tr>
	<input type="hidden" name="isQuery" value="true">
</html:form>
</table>
</td>
</tr>
</table>
	