package edu.wustl.query.action;
import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.SendFile;
import edu.wustl.query.bizlogic.QueryExportImport;


public class ExportQueryAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long queryId = Long.parseLong(request.getParameter("queryId"));
		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession()
				.getAttribute(edu.wustl.common.util.global.Constants.SESSION_DATA);
		File exportFile = File.createTempFile("query", ".xml");
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(exportFile);
			QueryExportImport queryExim = new QueryExportImport();
			queryExim.exportQuery(sessionDataBean, queryId, out);
			SendFile.sendFileToClient(response, exportFile.getAbsolutePath() , "query_" + queryId + ".xml", "application/download");
		} catch (Exception e) {
			throw new RuntimeException("Error occured during export of query", e);
		} finally {
			if (out != null) {
				try { out.close(); } catch (Exception e) { }
			}
			exportFile.delete();
		}
		return null;
	}
}

