package edu.wustl.query.action;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.query.queryobject.util.ExportSASDataHelper;
import edu.wustl.common.util.ExportReport;
import edu.wustl.common.util.SendFile;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.query.bizlogic.GenerateSAASPGMProcessor;
import edu.wustl.query.util.global.AQConstants;

public class SAASPgmExportAction extends SpreadsheetExportAction {

	/**
	 * 
	 */
	public ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		HttpSession session = request.getSession();
		List<List<String>> exportList = super.getExportList(session);
		List<String> idIndexList = new ArrayList<String>();
		Map<Integer, List<String>> entityIdsMap = (Map<Integer, List<String>>) session
		.getAttribute(AQConstants.ENTITY_IDS_MAP);
		super.generateSpreadsheetData(form, response, request, session, exportList,
				idIndexList, entityIdsMap);
		this.exportAndSend(request, response, session, exportList, idIndexList, entityIdsMap);
		
		return null;
	}

	/**
	 *
	 * @param response response
	 * @param session session
	 * @param exportList exportList
	 * @param idIndexList idIndexList
	 * @param entityIdsMap entityIdsMap
	 * @throws IOException IOException
	 */
	private void exportAndSend(HttpServletRequest request ,HttpServletResponse response,
			HttpSession session, List<List<String>> exportList,
			List<String> idIndexList, Map<Integer, List<String>> entityIdsMap)
			throws IOException
	{
		
		String appName = CommonServiceLocator.getInstance().getAppHome();
		String path = appName + System.getProperty("file.separator");
		
		String sasFileName = generateSasPGM(request, session, path);
		String sasFileZipEntryName =  "SearchResult_SAS_PGM.SAS"; 
				
		ExportReport report;
		String csvfileName = path + AQConstants.SEARCH_RESULT;
		String zipFileName = path + session.getId() + AQConstants.ZIP_FILE_EXTENSION;
		String fileName = path + session.getId() + AQConstants.CSV_FILE_EXTENSION;
		if (entityIdsMap != null && !entityIdsMap.isEmpty())
		{
			report = new ExportReport(path, csvfileName, zipFileName);
			report.writeDataToZip(exportList, AQConstants.DELIMETER, idIndexList);
			ExportSASDataHelper.addSasFileToZip(zipFileName, sasFileName,sasFileZipEntryName);
			SendFile.sendFileToClient(response, zipFileName, AQConstants.EXPORT_ZIP_NAME,
					"application/download");
		}
		else
		{
			report = new ExportReport(fileName);
			report.writeData(exportList, AQConstants.DELIMETER);
			report.closeFile();
			zipFileName = ExportSASDataHelper.createZip(fileName, sasFileName,AQConstants.SEARCH_RESULT,sasFileZipEntryName);
			SendFile.sendFileToClient(response, zipFileName, AQConstants.EXPORT_ZIP_NAME,
					"application/download");
		}
	}

	/**
	 * 
	 * @param request
	 * @param session
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private String generateSasPGM(HttpServletRequest request,
			HttpSession session, String path) throws IOException {
		SelectedColumnsMetadata selectedColumnsMetadata = (SelectedColumnsMetadata) session
		.getAttribute(AQConstants.SELECTED_COLUMN_META_DATA);
		List<String> columnList = (List<String>)session.getAttribute("COLUMNS_LIST");
		GenerateSAASPGMProcessor generateSAASPGMProcessor = new GenerateSAASPGMProcessor(selectedColumnsMetadata,columnList,AQConstants.SEARCH_RESULT);
		String pgm = generateSAASPGMProcessor.generateSAASPgm();
		
		String sas_fileName = path + request.getSession().getId() + "_SAS_PGM.SAS";
		
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(sas_fileName));
			bufferedWriter.write(pgm);
		} finally {
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
		}
		return sas_fileName;
	}
}
