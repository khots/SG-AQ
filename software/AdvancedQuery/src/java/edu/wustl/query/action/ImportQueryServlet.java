package edu.wustl.query.action;

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.query.bizlogic.QueryExportImport;

public class ImportQueryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final org.apache.log4j.Logger LOGGER = LoggerConfig
			.getConfiguredLogger(ImportQueryServlet.class);

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		InputStream xmlStream = null;
		IParameterizedQuery query = null;
		try {		
			
			HttpSession session = request.getSession();
			SessionDataBean sdb = (SessionDataBean) session
					.getAttribute(edu.wustl.common.util.global.Constants.SESSION_DATA);
			
			String queryName = request.getParameter("queryName");	
			QueryExportImport queryExim = new QueryExportImport();	
			
			if (request.getContentType() != null && 
				request.getContentType().toLowerCase().indexOf("multipart/form-data") > -1 ) 
			{
				DiskFileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(request);
				if (items != null || ! items.isEmpty()){
					xmlStream = items.get(1).getInputStream();
					query = queryExim.getQuery(xmlStream);
				}
			} else if (queryName != null || ! queryName.isEmpty() ){
				query =	(IParameterizedQuery) session.getAttribute("deSerializedQueryObj");	
				query.setName(queryName);
			}
			
			boolean isQueryImported = queryExim.importQuery(query, sdb);
			
			if (isQueryImported) {
				response.getWriter().write("Success :'" + query.getName() + "'");
				session.removeAttribute("deSerializedQueryObj");
			} else {
				session.setAttribute("deSerializedQueryObj", query);
				response.getWriter().write("Error:");
			}
			
		} catch (Exception e) {
			 throw new RuntimeException("Error occured during importing query", e);
		}
	}
}
