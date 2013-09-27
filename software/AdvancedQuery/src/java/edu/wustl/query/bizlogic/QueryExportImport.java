package edu.wustl.query.bizlogic;

import java.io.InputStream;
import java.io.OutputStream;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.beans.SharedQueryBean;


public class QueryExportImport {
 
	public IParameterizedQuery getQuery(InputStream in)throws Exception {
		ParameterizedQuerySerializer serializer = new ParameterizedQueryXmlSerializer();
		return serializer.deserialize(in);
	}
	
	public boolean importQuery(IParameterizedQuery query, SessionDataBean sdb) throws BizLogicException{
		QueryDAO queryDAO = new QueryDAO();
		boolean isTitlePresent = queryDAO.isQueryNamePresent(sdb, query.getName());
		
		if(!isTitlePresent){
			SharedQueryBean bean = new SharedQueryBean();
			bean.setShareTo("all");
			SaveQueryBizLogic saveQueryBizLogic = new SaveQueryBizLogic();
			saveQueryBizLogic.saveQuery(query, sdb, bean);
			return true;
		}
		return false;
	}

	public void exportQuery(SessionDataBean sdb, Long queryId, OutputStream out) throws DAOException {
		DashboardBizLogic bizLogic = new DashboardBizLogic();
		IParameterizedQuery query = bizLogic.getQueryById(queryId);

		ParameterizedQuerySerializer serializer = new ParameterizedQueryXmlSerializer();
		serializer.serialize(query, out);
	}
}
