var flag =true;	
var grid;
function doInitGrid(tagId) 
{
	grid = new dhtmlXGridObject('mygrid_container');
	grid.setImagePath("dhtmlx_suite/dhtml_pop/imgs/");
 	grid.setHeader("My Folders");
 	grid.setInitWidthsP("100");
 	grid.setColAlign("left");
 	grid.setSkin("dhx_skyblue"); // (xp, mt, gray, light, clear, modern)
 	grid.enableRowsHover(true,'grid_hover')
 	grid.setEditable(false);
   	grid.attachEvent("onRowSelect", doOnRowSelected);
 	grid.init();
 	grid.load ("TagGridInItAction.do",function() {
  	   grid.selectRowById(tagId);
  	});
}

function doOnRowSelected(rId)
{
	ajaxQueryGridInitCall("QueryGridInitAction.do?tagId="+rId);
	document.getElementById('myQueries').className='btn1';
	document.getElementById('allQueries').className='btn1';
	document.getElementById('sharedQueries').className='btn1';
}
 
var xmlHttpobj = false;	 
function ajaxQueryGridInitCall(url) {
	xmlHttpobj = false;
	initAjaxPostCall(url,showGrid);
}

function initAjaxPostCall(url,successCall){
	if (window.XMLHttpRequest){
		xmlHttpobj=new XMLHttpRequest();
	}else {
		xmlHttpobj=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlHttpobj.onreadystatechange = successCall;
	xmlHttpobj.open("POST", url,false);
	xmlHttpobj.send(null);
}

 
var responseString; 
var queryGrid;
function showGrid() {
	if (xmlHttpobj.readyState == 4) 
	{
		 responseString =xmlHttpobj.responseText;
		 queryGrid = new dhtmlXGridObject('mygrid_right_container');
		 queryGrid.setImagePath("dhtmlx_suite/dhtml_pop/imgs/");
		 queryGrid.setHeader(",<b>ID,<b>Title,<b>Results,<b>Executed On,<b>Owner,<b>Actions ");
		 queryGrid.attachHeader("#rspan,#numeric_filter,#text_filter,#text_filter,#rspan,#select_filter,#rspan"); 
		 queryGrid.setInitWidthsP("3,5,*,20,11,12,15");
		 queryGrid.setColAlign("center,center,left,left,center,center,left");
		 queryGrid.setColTypes("txt,txt,link,txt,txt,txt,txt");
		 queryGrid.setColSorting("str,int,str_custom,str,str,str");
		 queryGrid.setSkin("dhx_skyblue"); // (xp, mt, gray, light, clear, modern)
		 queryGrid.enableRowsHover(true,'grid_hover')
		 queryGrid.setEditable(false);
		 queryGrid.enableTooltips("false,false,true,false,false,false,false");
		 queryGrid.setCustomSorting(str_custom, 2);
		 queryGrid.clearAll(true);
		 queryGrid.init();
		 queryGrid.enablePaging(true,20,10,"pagingArea",true);
		 queryGrid.setPagingSkin("bricks");
		 queryGrid.loadXMLString(responseString); 
		 document.getElementById('messageDiv').style.display = "none";
		 document.getElementById('file').value="";
	}
}

function setHeader(isQueryChecked)
{
	if(isQueryChecked == true){		 
		document.getElementById("poupHeader").textContent ="Assign the query(s) to folder";
		document.getElementById("poupHeader").innerText ="Assign the query(s) to folder";
	}else{
		document.getElementById("poupHeader").textContent ="Share the folder(s) with user";
		document.getElementById("poupHeader").innerText ="Share the folder(s) with user";
	} 
}

function fileUpload(form, actionUrl) {
	var uploadFileName = document.getElementById('file').value;
	var fileNameArray = uploadFileName.split(".");
	var arraySize = fileNameArray.length;
	if(fileNameArray[arraySize -1] != 'xml')
	{
		alert("Please select a XML file (.xml).");
	} else {
		var iframe = document.createElement("iframe");
		iframe.setAttribute("id", "upload_iframe");
		iframe.setAttribute("name", "upload_iframe");
		iframe.setAttribute("width", "0");
		iframe.setAttribute("height", "0"); 
		iframe.setAttribute("border", "0");
		iframe.setAttribute("style", "width: 0; height: 0; border: none;");
		parent.window.document.getElementById("superiframe").className="HiddenFrame";
		// Add to document...
		form.parentNode.appendChild(iframe);
		window.frames['upload_iframe'].name = "upload_iframe";

		iframeId = document.getElementById("upload_iframe");

		// Add event...
		var eventHandler = function () {
       	 	if (iframeId.detachEvent) {
				iframeId.detachEvent("onload", eventHandler);
			} else {
				iframeId.removeEventListener("load", eventHandler, false);
			}

        	// Message from server...
        	if (iframeId.contentDocument) {
          	  	content = iframeId.contentDocument.body.innerHTML;
          		if (content.indexOf("Success")>= 0){  			
          			var contentArray = content.split("'");
          	    	ajaxQueryGridInitCall("QueryGridInitAction.do");
          	    	popup('new_PopUpDiv');	
          	    	document.getElementById('errorDiv').style.display = "none";
        			document.getElementById('popMessageDiv').style.display = "none";
        			document.getElementById("messageDiv").style.color = "green";
        			document.getElementById('messageDiv').style.display = "block";
           			document.getElementById("messageDiv").textContent = "The query '"+contentArray[1]+"' has been imported successfully.";
          		} else if (content.indexOf("Error")>= 0){			
          			importFailureChanges();
          		}     
        	} else if (iframeId.contentWindow) {
         	  		content = iframeId.contentWindow.document.body.innerHTML;
         	  		document.getElementById("errorDiv").style.display = "none";
         	  		var test = new String(content);		
         	  		if(test.indexOf("Success")>=0){
         	  			ajaxQueryGridInitCall("QueryGridInitAction.do");
         	  			popup('new_PopUpDiv');
         	  			document.getElementById('messageDiv').style.display = "block"
         	  			document.getElementById("messageDiv").style.color = "green";
         	  			document.getElementById("messageDiv").textContent = "The query has been imported successfully.";	
         	  			document.getElementById("messageDiv").innerText = "The query has been imported successfully.";
         	  		}else {
         	  			importFailureChanges();
        			} 
        	} else if (iframeId.document) {
           		content = iframeId.document.body.innerHTML;
        	}
        	// Del the iframe...
        	setTimeout('iframeId.parentNode.removeChild(iframeId)', 250);
    	}

		if (iframeId.addEventListener) {
			iframeId.addEventListener("load", eventHandler, true);
		}
	
		if (iframeId.attachEvent) {
			iframeId.attachEvent("onload", eventHandler);
		}

		// Set properties of form...
		form.setAttribute("target", "upload_iframe");
		form.setAttribute("action", actionUrl);
		form.setAttribute("method", "post");
		form.setAttribute("enctype", "multipart/form-data");
		form.setAttribute("encoding", "multipart/form-data");

		// Submit the form...
		form.submit();
		parent.window.document.getElementById("superiframe").className="HiddenFrame";
		document.getElementById('queryName').value = "";
	}
}

function importFailureChanges(){
	document.getElementById('popMessageDiv').style.display = "block";
	document.getElementById('popMessageDiv').className ="alert alert-error";
	document.getElementById("popMessageDiv").style.color = "red";	
	document.getElementById('queryNameTr').style.display = "block";
	document.getElementById('saveBtnTr').style.display = "block";
	document.getElementById('fileTr').style.display = "none";
	document.getElementById('importBtnTr').style.display = "none";     
	document.getElementById("popMessageDiv").textContent = " Given query already exist. Please provide new title.";
	document.getElementById("popMessageDiv").innerText = " Given query already exist. Please provide new title.";
}

function openImportPopup(){
	popup('new_PopUpDiv');
	document.getElementById('file').value="";
	document.getElementById('queryName').value = "";
	document.getElementById('errorDiv').style.display = "none";
	document.getElementById('messageDiv').style.display = "none";
	document.getElementById('popMessageDiv').style.display = "none";
	document.getElementById('queryNameTr').style.display = "none";
	document.getElementById('saveBtnTr').style.display = "none";
	document.getElementById('fileTr').style.display = "block";
	document.getElementById('importBtnTr').style.display = "block";
}

function avoidEnter(event){
	 if( (event.keyCode == 13) && (validationFunction() == false) ) {
	      event.preventDefault();
	      return false;
	 }
}

function exportQuery(queryId)
{
	var url = "ExportQueryAction.do?queryId="+queryId;
	window.open(url);
}

function saveQuery()
{
	var queryName = document.getElementById('queryName').value;
	var url = null;
	if(queryName == ''){
		alert("Please insert title for the query.");
	} else {
		url = "ImportQuery?queryName="+queryName;
		initAjaxPostCall(url,showMessages)
	}
}
function showMessages(){
	if (xmlHttpobj.readyState == 4) 
	{
		 var respoString = xmlHttpobj.responseText;
		 if(respoString.indexOf("Error")>= 0){
			 document.getElementById('queryName').value = "";
			 document.getElementById('popMessageDiv').style.display = "block";
			 document.getElementById('popMessageDiv').className ="alert alert-error";
			 document.getElementById("popMessageDiv").style.color = "red";
			 document.getElementById("popMessageDiv").textContent = "Given query already exist. Please provide new title.";
			 document.getElementById("popMessageDiv").innerText ="Given query already exist. Please provide new title.";
		 } else if (respoString.indexOf("Success")>= 0){
			 var contentArray = respoString.split("'");
			 ajaxQueryGridInitCall("QueryGridInitAction.do");
			 popup('new_PopUpDiv');
			 document.getElementById('errorDiv').style.display = "none";
			 document.getElementById('popMessageDiv').style.display = "none";
			 document.getElementById("messageDiv").style.color = "green";
			 document.getElementById('messageDiv').style.display = "block";
			 document.getElementById("messageDiv").textContent = "The query '"+contentArray[1]+"' has been imported successfully.";	 
			 document.getElementById("messageDiv").innerText = "The query '"+contentArray[1]+"' has been imported successfully.";
		 }
	}
}


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
			ajaxShareTagFunctionCall("ShareTagAction.do","Select at least one existing folder.") 
		}
	}
}
function initCombo()
{
	var myUrl= 'ShareQueryAjax.do?';
	var ds = new Ext.data.Store({proxy: new Ext.data.HttpProxy({url: myUrl}),
	reader: new Ext.data.JsonReader({root: 'row',totalProperty: 'totalCount',id: 'id'}, [{name: 'id', mapping: 'id'},{name: 'excerpt', mapping: 'field'}])});
	var combo = new Ext.form.ComboBox({store: ds,hiddenName: 'CB_coord',displayField:'excerpt',valueField: 'id',typeAhead: 'false',pageSize:15,forceSelection: 'true',queryParam : 'query',mode: 'remote',triggerAction: 'all',minChars : 3,queryDelay:500,lazyInit:true,emptyText:'--Select--',valueNotFoundText:'',selectOnFocus:'true',applyTo: 'coord'});

	combo.on("expand", function() {
	if(Ext.isIE || Ext.isIE7)
	{
		combo.list.setStyle("width", "250");
		combo.innerList.setStyle("width", "250");
	}else{
		combo.list.setStyle("width", "250");
		combo.innerList.setStyle("width", "250");
	}
	}, {single: true});
	ds.on('load',function(){
		if (this.getAt(0) != null && this.getAt(0).get('excerpt')) 
		{combo.typeAheadDelay=50;
		} else {combo.typeAheadDelay=60000}
	});
}

function QueryWizard()
{
	var rand_no = Math.random();
	document.forms[0].action='QueryWizard.do?random='+rand_no;
	document.forms[0].submit();
}

function showlist(){
	if(flag == false){
		document.getElementById('subnavlist').style.display="none";
		flag = true;
	}else {
		document.getElementById('subnavlist').style.display="block";
		document.getElementById('subnavlist').className="subnavlist-class";
		flag = false;
	}
}

//function called for AllQueries,SharedQueries,MyQueries
function submitTheForm(url,btn) {
  	ajaxQueryGridInitCall(url);
    if (btn == "myQueries")
	{
		document.getElementById('myQueries').className='activebtn';
		document.getElementById('allQueries').className='btn1';
		document.getElementById('sharedQueries').className='btn1';
	}
	else if (btn == "allQueries")
	{
		document.getElementById('myQueries').className='btn1';
		document.getElementById('allQueries').className='activebtn';
		document.getElementById('sharedQueries').className='btn1';
	}
	else
	{
		document.getElementById('myQueries').className='btn1';
		document.getElementById('allQueries').className='btn1';
		document.getElementById('sharedQueries').className='activebtn';
	}	
}
