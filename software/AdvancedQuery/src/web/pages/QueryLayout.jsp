<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>

<%@ page import="edu.wustl.query.util.global.Constants"%>
<%@ page import="edu.wustl.common.util.global.ApplicationProperties"%>
<%@ page import="edu.wustl.common.util.XMLPropertyHandler"%>
<%@ page import="java.text.MessageFormat"%>

<link rel="STYLESHEET" type="text/css" href="css/advQuery/dhtmlxtabbar.css" />
<link rel="stylesheet" type="text/css" href="css/advQuery/styleSheet.css" />
<link rel="stylesheet" type="text/css" href="css/advQuery/CascadeMenu.css" />
<link rel="stylesheet" type="text/css" href="css/advQuery/catissue_suite.css" />

<script src="jss/advQuery/script.js" type="text/javascript"></script>
<script src="jss/advQuery/overlib_mini.js" type="text/javascript"></script>
<script src="jss/advQuery/calender.js" type="text/javascript"></script>
<script src="jss/splitter.js" type="text/javascript"></script>
<script src="jss/advQuery/ajax.js" type="text/javascript"></script>
<script src="jss/advQuery/caTissueSuite.js" type="text/javascript"></script>

<html>
<tiles:importAttribute />
<head>
<title><tiles:getAsString name="title" ignore="true" /></title>

<!--Jitendra -->
<script language="JavaScript">
		var timeOut;
		var advanceTime;
		var lastRefreshTime;//timestamp in millisecond of last accessed through child page
		var pageLoadTime;
		<%
			int timeOut = -1;
			int advanceTime = Integer.parseInt(XMLPropertyHandler.getValue(Constants.SESSION_EXPIRY_WARNING_ADVANCE_TIME));
			String tempMsg = ApplicationProperties.getValue("advQuery.app.session.advanceWarning");
			Object[] args = new Object[] {"" + advanceTime};
			String advanceTimeoutMesg = MessageFormat.format(tempMsg,args);
			
			timeOut = -1;
				
			if(request.getSession().getAttribute(Constants.SESSION_DATA) != null) //if user is logged in
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
			
				if(choice) //cancel pressed, extend session
				{
					clearTimeout(defTimeout);
					sendBlankRequest();
					setAdvanceSessionTimeout(timeOut);
				}
				else
				{
					sendToHomePage();
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
				   Object obj = request.getSession().getAttribute(Constants.SESSION_DATA);			  			
				   if(obj != null) 
				   {
				%>			
				   var timeoutMessage = "<%= ApplicationProperties.getValue("advQuery.app.session.timeout") %>";
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
	</script>
<!--Jitendra -->

<!-- Mandar 11-Aug-06 : For calendar changes -->
<script src="jss/advQuery/calendarComponent.js"></script>
<SCRIPT>var imgsrc="images/";</SCRIPT>
<LINK href="css/advQuery/calanderComponent.css" type=text/css rel=stylesheet>
<!-- Mandar 11-Aug-06 : calendar changes end -->

<!-- For Favicon -->
<link rel="shortcut icon" href="images/advQuery/favicon.ico"
	type="image/vnd.microsoft.icon" />
<link rel="icon" href="images/advQuery/favicon.ico"
	type="image/vnd.microsoft.icon" />

</head>
<body>
<table summary="" cellpadding="0" cellspacing="0" border="0"
	width="100%" height="99%">

	<!-- caBIG hdr begins -->
	
	<!-- caBIG hdr ends -->

	<tr>
		<td height="100%" valign="top">
		<table summary="" cellpadding="0" cellspacing="0" border="0"
			height="100%" >
			<!-- 
				 Name : Virender Mehta
		       	 Reviewer: Sachin Lale
				 Bug ID: MoveLogoBugId
				 Patch ID: MoveLogoBugId_2
				 See also: MoveLogoBugId_1
				 Description: Commented code because catissueLogo is moved to upper left position of the page
			 -->	
			<!-- application hdr begins -->
			<!--tr>
				<td colspan="2" height="50"><tiles:insert
					attribute="applicationheader"></tiles:insert></td>
			</tr-->
			<!-- application hdr ends -->
			<tr>
				<td width="180px" valign="top" class="subMenu" id="sideMenuTd">
				
					<table summary="" cellpadding="0" cellspacing="0" border="0" height="100%" width="100%">
						<tr>
							<td class="subMenuPrimaryTitle" height="40" >
							<img src="images/advQuery/shim.gif" alt="application logo" width="170" border="0" height="40px"/>
							&nbsp;
							</td>
						</tr>
						<tr>
							<td>
								<!-- submenu begins -->				
								<tiles:insert attribute="commonmenu">
									<tiles:put name="submenu" beanName="submenu" />
								</tiles:insert> 
								<!-- submenu ends -->
							</td>
						</tr>
					</table>
				</td>
				
				<!--
		         * Name : Kapil Kaveeshwar
		         * Reviewer: Sachin Lale
		         * Bug ID: Menu_Splitter
		         * Patch ID: Menu_Splitter_3
		         * See also: All Menu_Splitter
		         * Description: A TD tag which holds the splitter. On click on this, it calls the JS method
		         * toggleMenuStatus() which toggles the state of menu status
		         -->
				<logic:notEmpty scope="session" name="<%=Constants.SESSION_DATA%>">
					<TD id=menucontainer width="2" class="subMenuPrimaryTitle" valign="center" align="center" 	
						onmouseover="changeMenuStyle(this,'subMenuSecondaryTitleOver'),showCursor()" 
						onmouseout="changeMenuStyle(this,'subMenuPrimaryTitle'),hideCursor()" onclick="toggleSplitterStatus()">
						<SPAN id="splitter"><img src="leftPane_collapseButton.gif"/></SPAN>
					</TD>
					<script>
						initSplitterOpenStatus(<%=Boolean.valueOf("true")%>);
					</script>
				</logic:notEmpty>
				
				<td valign="top" width="100%">
				<table summary="" cellpadding="0" cellspacing="0" border="0"
					width="100%" height="100%">
					<tr>
						<td height="20" width="90%" class="mainMenu"><!-- main menu begins -->
						<tiles:insert attribute="mainmenu"></tiles:insert> <!-- main menu ends -->

						</td>
						<td height="20" class="mainMenu" align="right">
						<table summary="" cellpadding="0" cellspacing="0" border="0"
							height="20">
							<tr>
							
								<td height="20" class="mainMenuItem"
									onclick="document.location.href='QueryHome.do'">
									<logic:empty scope="session" name="<%=Constants.SESSION_DATA%>">
									<html:link styleClass="mainMenuLink" page="/QueryHome.do">
										<bean:message key="advQuery.app.loginMessage" />
									</html:link>
									</logic:empty>
									<logic:notEmpty scope="session" name="<%=Constants.SESSION_DATA%>">
									<html:link styleClass="mainMenuLink" page="/QueryLogout.do">
										<bean:message key="advQuery.app.logoutMessage" />
									</html:link>
									</logic:notEmpty>
								</td>
							</tr>
						</table>
								
						</td>
					</tr>

					<!--_____ main content begins _____-->
					<tr height="90%">
						<td colspan="2" width="100%" valign="top"><!-- target of anchor to skip menus --><a
							name="content" /> <tiles:insert attribute="content"></tiles:insert></td>
					</tr>
					<!--_____ main content ends _____-->

					<tr>
						<td colspan="2" height="20" width="100%" class="footerMenu"><!-- application ftr begins -->
						<tiles:insert attribute="applicationfooter"></tiles:insert> <!-- application ftr ends -->

						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td><!-- footer begins --> <tiles:insert attribute="mainfooter"></tiles:insert>
		<!-- footer ends --></td>
	</tr>
</table>

</body>
</html>
