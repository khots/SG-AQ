<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="edu.wustl.query.util.global.AQConstants"%>
<%@ page import="edu.wustl.common.util.global.ApplicationProperties"%>
<%@ page import="edu.wustl.common.util.XMLPropertyHandler"%>
<%-- <%@ page import="edu.wustl.catissuecore.util.HelpXMLPropertyHandler"%> --%>
<%@ page import="java.text.MessageFormat"%>

<link rel="stylesheet" type="text/css" href="css/advQuery/styleSheet.css" /> 
<link rel="stylesheet" type="text/css" href="css/advQuery/catissue_suite.css" />
<link href="css/advQuery/loadingframe.css" rel="stylesheet" type="text/css" />

<script src="jss/advQuery/script.js" type="text/javascript"></script>
<script src="jss/advQuery/ajax.js" type="text/javascript"></script>

<html>
<tiles:importAttribute />
<head>
<title><tiles:getAsString name="title" ignore="true" /></title>
<%
	/* String URLKey=(String)request.getAttribute("helpURLKey");
	String pageOf=(String)request.getAttribute("pageOf");
	String view=(String)request.getAttribute("view");
	String helpURL=null;
	
	if(null==URLKey)
	{
		helpURL=HelpXMLPropertyHandler.getValue("helpHomePage");
	}
	else
	{
		helpURL=HelpXMLPropertyHandler.getValue(URLKey);
		if(helpURL==null || "".equals(helpURL))
		{
			helpURL=HelpXMLPropertyHandler.getValue("helpHomePage");
		}
	} */
	
	%>

<!--Jitendra -->
<script language="JavaScript">
		var timeOut;
		var advanceTime;
		var lastRefreshTime;//timestamp in millisecond of last accessed through child page
		var pageLoadTime;
		<%
			int timeOut = -1;
			int advanceTime = Integer.parseInt(XMLPropertyHandler.getValue(AQConstants.AQ_SESSION_EXPIRY));
			String tempMsg = ApplicationProperties.getValue("advQuery.app.session.advanceWarning");
			Object[] args = new Object[] {"" + advanceTime};
			String advanceTimeoutMesg = MessageFormat.format(tempMsg,args);

			timeOut = -1;

			if(request.getSession().getAttribute(AQConstants.SESSION_DATA) != null) //if user is logged in
			{
				timeOut = request.getSession().getMaxInactiveInterval();
			}
		%>


		timeOut = "<%= timeOut%>";
		advanceTime = "<%= advanceTime%>";
		pageLoadTime = new Date().getTime(); //timestamp in millisecond of last pageload
		lastRefreshTime = pageLoadTime ; // last refreshtime in millisecond
		setAdvanceSessionTimeout(timeOut);

		function warnBeforeSessionExpiry()
		{
			//check for the last refresh time,whether page is refreshed in child frame after first load.
			if(lastRefreshTime > pageLoadTime)
			{

				var newTimeout = (lastRefreshTime - pageLoadTime)*0.001;
				newTimeout = newTimeout + (advanceTime*60.0);

				pageLoadTime = lastRefreshTime ;
				setAdvanceSessionTimeout(newTimeout);

			}
			else
			{
				var defTimeout = setTimeout('sendToHomePage()', advanceTime*60*1000);
				var choice = confirm("<%= advanceTimeoutMesg %>");

				if(choice == 0) //cancel pressed, extend session
				{
					clearTimeout(defTimeout);
					sendBlankRequest();
					setAdvanceSessionTimeout(timeOut);
				}
			}
		}

		function setAdvanceSessionTimeout(ptimeOut)
		{

			if(ptimeOut > 0)
			{
				var time = (ptimeOut - (advanceTime*60)) * 1000;
				setTimeout('warnBeforeSessionExpiry()', time); //if session timeout, then redirect to Home page
			}
		}


		function sendToHomePage()
		{
				<%
				   Object obj = request.getSession().getAttribute(AQConstants.SESSION_DATA);
				   if(obj != null)
				   {
				%>
				   var timeoutMessage = "<%= ApplicationProperties.getValue("app.session.timeout") %>";
				   alert(timeoutMessage);

				   window.location.href = "QueryLogout.do";
				<%
				   }
				%>
		}

		function getUmlModelLink()
		{
				var  frameUrl="<%=XMLPropertyHandler.getValue("umlmodel.link")%>";
				NewWindow(frameUrl,'name');
		}

		function getUserGuideLink()
		{
			var frameUrl = "<%=XMLPropertyHandler.getValue("userguide.link")%>";
			NewWindow(frameUrl,'name');
		}
<%-- 		
		function getHelpURL()
		{
			var URL="<%=helpURL%>";
			if(URL!="")
			{
				window.open(URL,'_blank');
			}		
		} --%>
	</script>
<!--Jitendra -->

<!-- Mandar 11-Aug-06 : For calendar changes -->
<script src="jss/calendarComponent.js"></script>
<SCRIPT>var imgsrc="images/";</SCRIPT>
<LINK href="css/advQuery/calanderComponent.css" type=text/css rel=stylesheet>
<!-- Mandar 11-Aug-06 : calendar changes end -->

<!-- For Favicon -->
<link rel="shortcut icon" href="images/favicon.ico"
	type="image/vnd.microsoft.icon" />
<link rel="icon" href="images/advQuery/favicon.ico"
	type="image/vnd.microsoft.icon" />

</head>
<body>
<iframe class="HiddenFrame" name = "superiframe" id = "superiframe" frameborder="0" src="pages/advQuery/WaitSymbol.jsp"></iframe>
<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%">
	<tr height="1%">
		<td>
		<table width="100%" border="0" cellpadding="0" cellspacing="0"
			bgcolor="#FFFFFF">
			<tr>
				<td width="20%" rowspan="2" style="border-top:4px solid #558dc0;"><tiles:insert
					attribute="applicationheader">
				</tiles:insert></td>
				<td valign="top"><tiles:insert attribute="header"></tiles:insert></td>
			</tr>
			<tr>
				<td width="80%" align="right" valign="top"><tiles:insert
					attribute="mainmenu">
				</tiles:insert></td>
			</tr>

		</table>
		</td>
	</tr>
	<tr height="99%">
		<td>
			<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%">
				<tr height="100%">
					<td colspan="2" width="100%" valign="top"><!-- target of anchor to skip menus -->
					<a name="content" /> <tiles:insert attribute="content"></tiles:insert></td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</body>
</html>
<script>
		function showHourGlass()
		{
			var superiframe=document.getElementById('superiframe');
			superiframe.className="VisibleFrame";
		//	superiframe.src="pages/WaitSymbol.jsp";
		}
		function newsubmit()
	{
		showHourGlass();
		this._submit();
	}
	
	<%-- function getHelpURL()
		{
			var URL;
			<%
			if(null!=helpURL) 
			{
				if(null==pageOf && null!=view && "cpBasedView".equals(view))
				{%>
					URL=document.getElementById('cpFrameNew').contentWindow.updateHelpURL();
				<%}
				else if(!"".equals(helpURL))
				{%>
					URL="<%=helpURL%>";
				<%}
			}%>
			if(URL!="")
			{
				window.open(URL,'_blank');
			}		
			
		} --%>

		if (window.ActiveXObject) // code for IE
		{
		/* 	var formControls=document.forms;
			var originalSubmit;

			for(var counter=0;counter<formControls.length;counter++)
			{
				originalSubmit=formControls[counter].submit;
				formControls[counter].submit=function newsubmit() {
								showHourGlass();
								originalSubmit();
								}
			} */
		}
		else
		{
			HTMLFormElement.prototype._submit = HTMLFormElement.prototype.submit;
			HTMLFormElement.prototype.submit = newsubmit;
		}
		
	</script>
