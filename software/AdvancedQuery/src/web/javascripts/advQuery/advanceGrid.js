
//function to update hidden fields as per check box selections.
function updateHiddenFields() {		
	var isChecked = false;
	var currentPage = mygrid.currentPage;
	var startIndex = (currentPage-1) * recordPerPage;
	var endIndex = startIndex + recordPerPage; 
	endIndex =  mygrid.getRowsNum() > endIndex ? endIndex: mygrid.getRowsNum();
//	var checkedRows = mygrid.getCheckedRows(0);
	 
	for(var i=startIndex; i < endIndex; i++){
		if(mygrid.cells(i, 0).isChecked()) {
			isChecked = true;
			break; 
		}  
	}
	
	if(isChecked == true) {
		for(var i=startIndex; i < endIndex; i++) {
			var cbvalue = document.getElementById("" + i);
			if (cbvalue != null){
				if(mygrid.cells(i, 0).isChecked()) {
					cbvalue.value = "1";
					cbvalue.disabled = false;
				} else {
					cbvalue.value = "0";
					cbvalue.disabled = true;
				}
			} else {
				break;
			}	
		}
	} 		
	return isChecked;
}

function viewSPR(id)
{
	var url = "ViewSurgicalPathologyReport.do?operation=viewSPR&pageOf=gridViewReport&reportId="+id+"&flow=viewReport";
	platform = navigator.platform.toLowerCase();
    if (platform.indexOf("mac") != -1) {
    	NewWindow(url,'name',screen.width,screen.height,'yes');
    } else {
    	NewWindow(url,'name','700','600','yes');
    }
	hideCursor();
}

/************************************ Query Grid **************************************/
var mygrid = new dhtmlXGridObject('gridbox'); ;
var rowCount = 0;
var totalCount = 0;
var colTypes = gridDataJson.columnType
var colDataTypes =  new Array(colTypes.length).join(",ro");
colDataTypes = "ch" + colDataTypes;
var mygridData = gridDataJson.gridData;

var filters = "#master_checkbox";
for(i = 1; i < colTypes.length; i++){
	filters += ","
	if(colTypes[i] != "NumericAttributeTypeInformation" 
		&& colTypes[i] != "DateAttributeTypeInformation" &&  i < filterCounts) {
		filters += "#text_filter"
	} 	
	// for sorting enable
	if(colTypes[i] == "NumericAttributeTypeInformation"){
		colTypes[i] = "int";
	}else {
		colTypes[i] = "str";
	}
}

function initQueryGrid() {		
	mygrid.setImagePath("dhtmlx_suite/imgs/");	
	mygrid.setHeader(gridDataJson.columns.join());
	mygrid.attachHeader(filters);
	mygrid.enableAutoHeigth(false);
	mygrid.setColTypes(colDataTypes);
	mygrid.setInitWidths(colWidth);			 
	mygrid.setSkin("dhx_skyblue");		
	mygrid.init();		 	
	 
	mygrid.enableRowsHover(true, 'grid_hover')
	mygrid.enableMultiselect(true);
	mygrid.setColSorting(colTypes.join());		
	mygrid.enableAlterCss("even", "uneven");
/*	mygrid.enablePaging(true, recordPerPage, null, "pagingArea", false);
	mygrid.setPagingSkin("toolbar", "dhx_skyblue")		*/
	mygrid.setEditable(!checkAllPages);
	mygrid.enablePaging(true,recordPerPage,15,"pagingArea",true);
	mygrid.setPagingSkin("bricks");
	mygrid.splitAt(1);
	var jsonData = gridDataJson.gridData;
	mygrid.parse(jsonData, "json");
	rowCount = jsonData.rows.length;
	totalCount = jsonData.total_count;
	createHiddenElement();
	window.setTimeout("100", 500)
	
	checkBoxCell = getCheckBox();
	checkBoxCell.style.paddingLeft = "0px";
	var checkbox = checkBoxCell.childNodes[0];
	checkbox.onclick = null;
	checkbox.onclick = checkFromCurrentPage;
	gridBOxTag.style.width = (gridBOxTag.offsetWidth - 6 ) + "px"
	if(mygrid.getRowsNum() == fetchRecordSize){
		document.getElementById("messageDiv").style.display = "block";
		document.getElementById("messageDiv").style.color = "blue";
		document.getElementById("messageDiv").textContent = "Showing the top "+fetchRecordSize+" rows. Click on Export button to view all the rows.";
		document.getElementById("messageDiv").innerText ="Showing the top "+fetchRecordSize+" rows. Click on Export button to view all the rows.";
	} else if(mygrid.getRowsNum() == 0){
		document.getElementById("messageDiv").style.display = "block";
		document.getElementById("messageDiv").style.color = "red";
		document.getElementById("messageDiv").textContent = "Zero records found for defined condition, Click on Export button to view all the records.";
		document.getElementById("messageDiv").innerText = "Zero records found for defined condition, Click on Export button to view all the records.";	
	}
}

var checkedAllPages = new Array();
var checkedRowIds = new Array();
var checkedIndexes = new Array();

function checkFromCurrentPage(){
	var checkBoxCell = getCheckBox();
	var checkbox_status = checkBoxCell.childNodes[0].checked ;
	var currentPage = mygrid.currentPage;
	var startIndex = (currentPage-1) * recordPerPage;
	var endIndex = startIndex + recordPerPage; 
	endIndex =  mygrid.getRowsNum() > endIndex ? endIndex: mygrid.getRowsNum();
	
	if(checkbox_status){
		checkedAllPages[checkedAllPages.length] = currentPage;
	}else {
		checkedAllPages.splice(checkedAllPages.indexOf(currentPage), 1);
	}

	for(var i=startIndex; i < endIndex; i++){
		var rowId = mygrid.getRowId(i);
		var checked = mygrid.cells(rowId, 0).isChecked();	
		if(checked != checkbox_status){
			var cell = mygrid.cells(rowId, 0).cell.childNodes[0];			
			(new eXcell_ch(cell.parentNode)).changeState(); 
		}
	}
}

var sortIndex;
var sortDirection = "asc";

mygrid.attachEvent("onBeforeSorting",function(ind, type, direction){
	sortDirection = direction;
	if(type != "ch"){
		checkRowStatus()
		unCheckGridRows();
	}
	return true;   
});
 
mygrid.attachEvent("onFilterStart", function(ind){
	mygrid.clearAll();
	getGridData();
})
	
mygrid.attachEvent("onBeforePageChanged", function(ind, count){
	var checkBoxCell = getCheckBox();
	var currentPage = mygrid.currentPage;
	checkBoxCell.childNodes[0].checked = false;
	checkRowStatus();	
	document.getElementById("messageDiv").style.display = "none";
	return true;
}); 

mygrid.attachEvent("onPageChanged", function(ind, count){
	var checkBoxCell = getCheckBox();
	//checkBoxCell.childNodes[0].checked = false;
	if(checkedAllPages.indexOf(mygrid.currentPage) >= 0){
		checkBoxCell.childNodes[0].checked = true;
	}
	
	return true;
}); 

function checkRowStatus(){
	var currentPage = mygrid.currentPage;
	var startIndex = (currentPage-1) * recordPerPage;
	var endIndex = startIndex + recordPerPage; 	
	endIndex =  mygrid.getRowsNum() > endIndex ? endIndex: mygrid.getRowsNum();
	
	for(var i=startIndex; i < endIndex; i++){
                var rowId = mygrid.getRowId(i);
		var checked = mygrid.cells(rowId, 0).isChecked();	
		if(checked){
			if(checkedRowIds.indexOf(rowId) < 0){
				checkedRowIds[checkedRowIds.length] = rowId;
				checkedIndexes[checkedIndexes.length] = i
			}
		}else {
			if(checkedRowIds.indexOf(rowId) >= 0){
				checkedRowIds.splice(checkedRowIds.indexOf(rowId), 1);
				checkedIndexes.splice(checkedIndexes.indexOf(i), 1);
			}
		}
	}	
}

function getGridData() {	
	var param = "Data=" + getJsonForFilter() + "&pageNum="+1+"&recordPerPage="+10000;
	var url = "QueryGridFilter.do";
	var xmlhttp = newXMLHTTPReq();
	xmlhttp.onreadystatechange =  function() {
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				var json = eval('(' + xmlhttp.responseText + ')');
				setColumn(json.columns);
				mygridData = json.gridData;
				mygrid.parse(mygridData, "json");
				rowCount = json.gridData.rows.length;
				createHiddenElement();
				checkBoxCell = getCheckBox();
				checkBoxCell.getElementsByTagName('input')[0].checked = false;
				setPageDivStyle();
			}				  		
		}
	xmlhttp.open("POST", url,  true);
	xmlhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	xmlhttp.send(param);
}

function getJsonForFilter() {
	var columns = []
	var values = []
	var j = 0;
	for(i = 1; i < filterCounts; i++) {
		if(mygrid.getFilterElement(i)){
			var value = mygrid.getFilterElement(i).value;
			if(value != "") {
				columns[j] = mygrid.getColumnLabel(i, 0);
				values[j++] = value;				
			}
		}
	}
	var sortColumn = sortIndex ? mygrid.getColumnLabel(sortIndex, 0): "";
	sortDirection = sortDirection == "asc" ? sortDirection: "desc";
	
	return JSON.stringify({"columns": columns, "values": values, "sortColumn": sortColumn ,"sortDir": sortDirection});
}

function createHiddenElement() {
	var cont = document.getElementById("hiddenBox")
	cont.innerHTML  = '';
	for(var row = 0; row < rowCount; row++)
	{						
		var el = document.createElement("input");
		el.type = "hidden";
		el.id = row;
		el.name = "value1(CHK_" + row + ")";
		el.disabled = true;
		
		cont.appendChild(el);					
	}	
}

function setColumn(columnList) {
			
	for(i = 1; i < columnList.length; i++) {
		var columnLabel = mygrid.getColumnLabel(i, 0)
		if( columnLabel != columnList[i] ) {
			mygrid.insertColumn(i, columnList[i], 'ro', "100");
		}
	}	
}

/*function addRecordPerPageOption() {		
	toolbar = mygrid.aToolBar;
	toolbar.setWidth('perpagenum', 130);
	var  opt = [10, 50, 100, 500, 1000, 5000];
	
	for(i = 5; i < 35; i += 5) {
		toolbar.removeListOption('perpagenum', 'perpagenum_'+i);
	}
	
	for(i = 0; i < opt.length; i++) {
		toolbar.addListOption('perpagenum', 'perpagenum_'+ opt[i], NaN, "button", opt[i]+" "+ mygrid.i18n.paging.perpage);
	}

	toolbar.setListOptionSelected('perpagenum', 'perpagenum_' + recordPerPage);
	toolbar.addText("blanck_space",10,"");
	toolbar.addText("total_count_txt",11 ," Total Records : "+totalCount);
	setPageDivStyle();
}*/

function getCheckBox() {
	if (document.getElementsByClassName) { 		
		return document.getElementsByClassName('hdrcell')[1]; 
	} else { 		
		var els = (gridBOxTag.getElementsByTagName('td'))[1].childNodes;			
		return els[0];			
	} 
}

function setPageDivStyle() {
	if (document.getElementsByClassName) { 		
		pageDiv = document.getElementsByClassName('dhx_toolbar_poly_dhx_skyblue')[0]; 
		pageDiv.style.maxHeight = "210px"
		pageDiv.style.overflowY = "auto"
	} else { 		
		var elements = document.getElementsByTagName('div');
		for(i = 0; i < elements.length; i++) {
			if(elements[i].className == 'dhx_toolbar_poly_dhx_skyblue') {
				pageDiv = elements[i];				
				if(pageDiv.childNodes.length < 10) {
					pageDiv.style.height = 21 * pageDiv.childNodes.length + "px"					
				} else {
					pageDiv.style.height = 21 * 10 + "px"
					pageDiv.style.width = "96px";
					pageDiv.style.overflowY = "auto"
				}				
				break;
			}
		}
	} 
}
