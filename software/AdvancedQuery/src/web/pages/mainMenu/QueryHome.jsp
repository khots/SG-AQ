<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ page language="java" isELIgnored="false"%>
<%@ page
	import="edu.wustl.common.util.global.Constants,edu.wustl.common.beans.SessionDataBean"%>

<%
	
	SessionDataBean sessionData = null;
	sessionData=(SessionDataBean)session.getAttribute(Constants.SESSION_DATA) ;
	Long userId=0L;
	if(session.getAttribute(Constants.SESSION_DATA) != null) 
		userId = sessionData.getUserId();

%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" type="text/css" href="css/advQuery/menus.css" />
<link rel="stylesheet" type="text/css" href="css/advQuery/examples.css" />
<link rel="stylesheet" type="text/css" href="css/advQuery/ext-all.css" />
<link rel="stylesheet" type="text/css" href="css/advQuery/catissue_suite.css" />
<script type="text/javascript" src="jss/advQuery/caTissueSuite.js"></script>
<script type="text/javascript" src="jss/advQuery/ext-base.js"></script>
<script type="text/javascript" src="jss/advQuery/ext-all.js"></script>
<script type="text/javascript">
function editUserProfile(item){
	document.location.href = "UserProfileEdit.do?pageOf=pageOfUserProfile";
}
</script>

</head>
<body>
<table width="95%" cellspacing="0" cellpadding="0" border="0" align="top">
	<tr>
		<td width="5%" valign="top"><img width="48" height="24"
			src="images/advQuery/uIEnhancementImages/menustartimg.gif" /></td>
		<td width="95%" align="left">
		<logic:notEmpty scope="session"
			name="<%=Constants.SESSION_DATA%>">
			<script type="text/javascript" src="jss/advQuery/menus.js"></script>
			<div id="toolbarLoggedIn"></div>
		</logic:notEmpty>
		<logic:empty scope="session" name="<%=Constants.SESSION_DATA%>">
			<script type="text/javascript" src="jss/advQuery/menu_home.js"></script>
			<div id="toolbarLoggedOut"></div>
		</logic:empty></td>
	</tr>
	
</table>
</body>


