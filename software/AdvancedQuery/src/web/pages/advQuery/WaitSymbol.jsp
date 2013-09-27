<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html><head>



	
		<meta http-equiv="content-type" content="text/html;charset=ISO-8859-1"><title>Dead Centre</title>
		<style type="text/css" media="screen"><!--
body 
	{
	margin: 0px
	}

#horizon        
	{
	color: white;
	text-align: center;
	position: absolute;
	top: 50%;
	left: 0px;
	width: 100%;
	height:79%;
	overflow: visible;
	visibility: visible;
	display: block;
	}

#content    
	{
	font-family: Verdana, Geneva, Arial, sans-serif;
	margin-left: -125px;
	position: absolute;
	top: -35px;
	left: 50%;
	width: 250px;
	height: 70px;
	visibility: visible
	}

	#cancel
	{
		text-align: right;
		height:10%;
	}
--></style>
<script>
function cancelWait()
{
	parent.window.document.getElementById("superiframe").className="HiddenFrame";
}
</script>
</head><body>
<div id="cancel">
<a href="javascript:cancelWait()"><img src="../../images/advQuery/bigger_close.gif" alt="Close" border="0" height="18" width="18"></a>
</div>
		<div id="horizon">	
			<div id="content">
				<img src="../../images/advQuery/loading_circle.gif" alt="Loading...">
			</div>
		</div>
	</body></html>