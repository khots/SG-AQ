<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.*"%>
<%@ page import="edu.wustl.catissuecore.actionForm.ShoppingCartForm"%>
<%@ page import="edu.wustl.catissuecore.util.global.Constants"%>
<%@ page import="edu.wustl.catissuecore.querysuite.QueryShoppingCart"%>
<%@ page import="edu.wustl.catissuecore.util.global.Utility"%>
<%@ page import="edu.wustl.catissuecore.util.global.Variables"%>

<%@ include file="/pages/content/common/AutocompleterCommon.jsp" %> 
<link href="css/catissue_suite.css" rel="stylesheet" type="text/css" /> 

<script src="jss/script.js"></script>

<style>
.active-column-0 {width:30px}
.active-column-4 {width:150px}
.active-column-6 {width:150px}
</style>
<%
	String checkAllPages = (String)session.getAttribute("checkAllPages");
	String pageOf = (String)request.getAttribute(AQConstants.PAGEOF);
	String isSpecimenIdPresent = (String)request.getAttribute(AQConstants.IS_SPECIMENID_PRESENT);
	String isContainerPresent=(String)request.getAttribute(AQConstants.IS_CONTAINER_PRESENT);
	
	if(isSpecimenIdPresent==null)
	 isSpecimenIdPresent = "";
	 
	if(isContainerPresent==null)
	 isContainerPresent = "";
	
	String isSpecimenArrayPresent = (String)request.getAttribute(AQConstants.IS_SPECIMENARRAY_PRESENT);
	
	String disabled = "";
	boolean disabledList = false;
	if(isSpecimenArrayPresent!= null && isSpecimenArrayPresent.equals("true"))
	{
		disabled = "DISABLED";
		disabledList = true;
	}
	
	String disabledOrder = "";
	String disabledShipping="";
	
	boolean disabledButton = false;
	if(isSpecimenIdPresent.equals("false"))
	{
		disabled = "DISABLED";
		disabledList = true;
		disabledButton = true;
		disabledOrder = "DISABLED";
		disabledShipping="DISABLED";
	}
	
	if(isContainerPresent.equals("true"))
	{
		disabledButton = false;
		disabledShipping="";
	}
	
	if(disabledOrder=="DISABLED" && disabledShipping=="DISABLED")
	{
		disabledButton = true;
	}

    QueryShoppingCart cart = (QueryShoppingCart)session.getAttribute(AQConstants.QUERY_SHOPPING_CART);
	List columnList = new ArrayList();
	List dataList = new ArrayList() ;
    
	if(cart!=null)
	{
	  columnList = cart.getColumnList();
	  if(columnList!=null)
	   columnList.add(0,"");
      dataList = cart.getCart();
	}
%>
<head>
<script language="javascript">

function showEvents()
{
   
	 var autoDiv1 = document.getElementById("eventlist1");
     var autoDiv2 = document.getElementById("eventlist2");

	 var chkbox=  document.getElementById("ch1");

	 if(chkbox.checked== true)
	{
		   
		   autoDiv1.style.display  = 'block';
		   autoDiv2.style.display  = 'none';
	}
    else
   {
       
		autoDiv2.style.display  = 'block';
		autoDiv1.style.display  = 'none';
		 
   }
}

function gotoAdvanceQuery()
{
	var action = "QueryWizard.do?";
	document.forms[0].action = action;
	document.forms[0].submit();
}

function onSubmit()
{
	if(document.forms[0].chkName[2].checked == true)
	{
		if(document.getElementById('specimenEventParameter').value == "Transfer")
		{
		    dobulkTransferOperations();
		}
		else if(document.getElementById('specimenEventParameter').value == "Disposal")
		{
			
			dobulkDisposals();
		}
		else
		{
			alert("Only Transfer and Disposal bulk functionality is available in this version");
		}
	}
	else if(document.forms[0].chkName[1].checked == true)
	{
		editMultipleSp();
	}
	else if(document.forms[0].chkName[0].checked == true)
	{
		addToOrderList();
	}
	else if(document.forms[0].chkName[3].checked == true)
	{
		//create Shipment Request
		createShipmentRequest();
	}
	else if(document.forms[0].chkName[4].checked == true)
	{
		//create Shipment
		createShipment();
	}
}

function setCheckBoxState()
		{	
			var chkBox = document.getElementById('checkAll1');
			chkBox.checked = true;
			rowCount = mygrid.getRowsNum();
        	for(i=1;i<=rowCount;i++)
				{
					var cl = mygrid.cells(i,0);
					if(cl.isCheckbox())
					cl.setChecked(true);
				}
		
		}

function onDelete()
		{
			var isChecked = updateHiddenFields();
		    
		    if(isChecked == "true")
		    {
				var flag = confirm("Are you sure you want to delete the selected item(s)?");
				if(flag)
				{
					var action = "AddDeleteCart.do?operation=delete";
					document.forms[0].action = action;
					document.forms[0].target = "_parent";
					document.forms[0].submit();
				}
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}

function onExport()
		{
			var isChecked = updateHiddenFields();
		    
		    if(isChecked == "true")
		    {
				var action = "ExportCart.do?operation=export";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}
		
function dobulkTransferOperations()
		{
			var isChecked = updateHiddenFields();
		    
		    if(isChecked == "true")
		    {		
				var action = "BulkCart.do?operation=bulkTransfers";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}
		
function dobulkDisposals()
		{
			var isChecked = updateHiddenFields();
		    
		    if(isChecked == "true")
		    {
				var action = "BulkCart.do?operation=bulkDisposals";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}

function addToOrderList()
		{			
		    var isChecked = updateHiddenFields();
		    if(isChecked == "true")
		    {
				var action = "BulkCart.do?operation=addToOrderList";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}
		function editMultipleSp()
		{
		
			 var isChecked = updateHiddenFields();
		    if(isChecked == "true")
		    {
				var action = "BulkCart.do?operation=editMultipleSp";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}		

function createShipmentRequest()
{
		var isChecked = updateHiddenFields();
		    
		    if(isChecked == "true")
		    {
				var action = "BulkCart.do?operation=createShipmentRequest";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
}

function createShipment()
{
		var isChecked = updateHiddenFields();
		    
		    if(isChecked == "true")
		    {
				var action = "BulkCart.do?operation=createShipment";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
	
}

function checkAll(element)
{
	mygrid.setEditable(true);
	var state=element.checked;
	rowCount = mygrid.getRowsNum();
	for(i=1;i<=rowCount;i++)
	{
		var cl = mygrid.cells(i,0);
		if(cl.isCheckbox())
		cl.setChecked(state);
	}
}
	</script>
</head>
<body onload="setCheckBoxState()">
<html:html>

<html:messages id="messageKey" message="true" header="messages.header" footer="messages.footer">
	<%=messageKey%>
</html:messages>
<html:form action="AddDeleteCart.do">


<table width="100%" border="0" cellpadding="0" cellspacing="0" class="maintable">
  <tr>
    <td class="td_color_bfdcf3"><table border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td class="td_table_head"><span class="wh_ar_b"><bean:message key="shoppingCart.title"/></span></td>
        <td><img src="images/uIEnhancementImages/table_title_corner2.gif" alt="Page Title - My List" width="31" height="24" /></td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td class="tablepadding"><table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td width="90%" valign="bottom" class="td_tab_bg">&nbsp;</td>
      </tr>
    </table>
  
   <table width="100%" border="0" cellpadding="3" cellspacing="0" class="whitetable_bg">
      
     <tr>
        <td colspan="2" align="left" class="toptd">
		<%@ include file="/pages/content/common/ActionErrors.jsp" %>
		</td>
      </tr>
      
	  <tr>
        <td colspan="2" align="left" class="tr_bg_blue1"><span class="blue_ar_b"> &nbsp;<bean:message key="shoppingCart.title" />&nbsp <bean:message key="buttons.view"/> &nbsp;</span></td>
      </tr>
     
      <tr>
        <td colspan="2">
		<%
			if(dataList!=null && dataList.size()!=0)
		   {
		%><table width="99%" border="0" align="center" cellpadding="0" cellspacing="0">
           
			<tr >
	<td class="formFieldNoBordersSimple" align="left">
		<input type='checkbox' name='checkAll1' id='checkAll1' property="" onClick='checkAll(this)'>
				<bean:message key="buttons.checkAll" />
		</td>
		<!--  **************  Code for New Grid  *********************** -->	
			<tr>
			  <td>
			  <script>
					function queryshopingcart(id)
					{
						//do nothing
					} 				

					/* 
						to be used when you want to specify another javascript function for row selection.
						useDefaultRowClickHandler =1 | any value other than 1 indicates you want to use another row click handler.
						useFunction = "";  Function to be used. 	
					*/
					var useDefaultRowClickHandler =2;
					var useFunction = "queryshopingcart";	
			 </script>
			<%@ include file="/pages/content/search/AdvanceGrid.jsp" %> </td>
           </tr>
   <!--  **************  Code for New Grid  *********************** -->
		</table></td>
      </tr>
<tr>
        <td colspan="2">
         &nbsp; &nbsp;<html:button styleClass="black_ar" property="deleteCart" onclick="onDelete()">
				<bean:message key="buttons.delete"/>
			</html:button>
		
			&nbsp;<html:button styleClass="black_ar" property="exportCart" onclick="onExport()">
				<bean:message key="buttons.export"/>
			</html:button>
      </tr>
	<tr>
        <td colspan="2" class="bottomtd"></td>
    </tr>	
	
	<tr>
		
        <td colspan="2" align="left" class="tr_bg_blue1">
			<label for="selectLabel">&nbsp;<span class="blue_ar_b"><bean:message key="mylist.label.selectLabel" /> </span>
			</label>
		</td>
	</tr>
	
	<tr>
        <td colspan="2" class="black_ar">
		<table width="100%" border="0" cellpadding="1" cellspacing="0">
          <tr>
			 <td class="black_ar" width="2%"><input type="radio" name="chkName"      value="OrderSpecimen" onclick="showEvents()" checked=true <%=disabledOrder%> ></td>
             <td class="black_ar" width="23%" ><bean:message key="mylist.label.orderBioSpecimen"/></td>
			 <td class="black_ar" width="2%"><INPUT TYPE='RADIO' NAME='chkName' onclick="showEvents()" value="Specimenpage" <%=disabled%> ></td>
			 <td class="black_ar" width="23%" ><bean:message key="mylist.label.multipleSpecimenPage"/>
               </td>
			<td class="black_ar" width="2%"><INPUT TYPE='RADIO' NAME='chkName'     onclick="showEvents()" id="ch1" value="Events" <%=disabled%> ></INPUT></td>
            
			<td class="black_ar" width="15%" ><bean:message key="mylist.label.specimenEvent"/> </td>
			 
		
			
		   <td class="black_ar" width="33%">
		 	   <div id="eventlist1" style="display:none">
			   
			   <autocomplete:AutoCompleteTag  property="specimenEventParameter" 
						  optionsList = "<%=AQConstants.EVENT_PARAMETERS%>" styleClass="black_ar" size="27"
						  initialValue="Transfer"/>
				 </div> 
		     
			   <div id="eventlist2" style="display:block"><input type="text" styleClass="black_ar" size="25" id="specimenEventParameter1" name="specimenEventParameter" value="Transfer" readonly="true"/></div>
             </td>
		  </tr>
		
          <tr>
			<td class="black_ar"><input type="radio" name="chkName" onclick="showEvents()" value="requestShipment" <%=disabledShipping%> ></td>
            <td class="black_ar" ><bean:message key="shipment.request"/></td>			
			<td class="black_ar"><input type="radio" name="chkName"  value="createShipment" onclick="showEvents()" <%=disabledShipping%> ></td>
            <td class="black_ar" ><bean:message key="shipment.create"/></td>
			<td colspan="3">&nbsp;</td>
		  </tr>
        </table>          
      </tr>
<tr>
			<td class="bottomtd"></td></tr>
<tr>
       <td colspan="2" class="buttonbg"> <html:button styleClass="blue_ar_b" property="proceed" onclick="onSubmit()" disabled="<%=disabledButton%>" >
				<bean:message key="buttons.submit"/>	
			</html:button>&nbsp|&nbsp <html:link page="/ManageAdministrativeData.do" styleClass="cancellink">
		<bean:message key="buttons.cancel" /> </html:link></td>
      </tr>
	
   </table></td></tr>
	<%
	    }
		else
		{
	%>
     <tr>
		<td class="bottomtd"></td>
	</tr>
	 <tr>
		<td class="messagetextsuccess">&nbsp;<bean:message key="ShoppingCart.emptyCartTitle"/>
		&nbsp;<bean:message key="shoppingCart.emptyCartTitle.message"/></td>
	</tr>
	<tr>
		<td class="bottomtd"></td>
	</tr>
	<tr>
		<td class="buttonbg">
			<html:button styleClass="blue_ar_b" property="" onclick="gotoAdvanceQuery()">
				<bean:message key="buttons.advanceQuery"/>	
			</html:button>
			&nbsp|&nbsp <html:link page="/ManageAdministrativeData.do" styleClass="cancellink">
		<bean:message key="buttons.cancel" /> </html:link>
		</td>
	</tr>	
 <%}%>
  </table>
	</body>
	</html:form>
</html:html>