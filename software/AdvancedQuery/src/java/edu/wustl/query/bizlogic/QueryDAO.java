package edu.wustl.query.bizlogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.Session;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.dao.query.generator.DBTypes;
import edu.wustl.query.dto.QueryDTO;
import edu.wustl.query.util.querysuite.DAOUtil;
import gov.nih.nci.security.authorization.domainobjects.User;

public class QueryDAO {
	
	public Session session = null;
	
	private static final org.apache.log4j.Logger LOGGER = LoggerConfig
			.getConfiguredLogger(QueryDAO.class);
	
	protected final static String GET_ALL_QUERIES = 
		  " SELECT" 
		+ " pq.identifier, pq.query_name, pq.description, ae.event_timestamp, aeq.root_entity_name, aeq.count_of_root_records" 
		+ " FROM" 
		+ " query_parameterized_query pq" 
		+ " LEFT OUTER JOIN catissue_audit_event_query_log aeq ON aeq.query_id = pq.identifier" 
		+ " LEFT OUTER JOIN catissue_audit_event ae ON aeq.audit_event_id = ae.identifier" 
		+ " WHERE" 
		+ " aeq.query_id is null" 
		+ " AND pq.status = 'ACTIVE'" 
		+ " OR (ae.event_timestamp =" 
				+ " (SELECT MAX(iae.event_timestamp)" 
				+ " FROM catissue_audit_event iae, catissue_audit_event_query_log iaeq" 
				+ " WHERE iaeq.audit_event_id = iae.identifier" 
				+ " AND iaeq.query_id = pq.identifier" 
				+ " AND pq.status = 'ACTIVE'))";
	
	protected final static String GET_QUERIES_BY_ID =  
		  " SELECT" 
		+ " pq.identifier, pq.query_name, pq.description, ae.event_timestamp, aeq.root_entity_name, aeq.count_of_root_records" 
		+ " FROM" 
		+ " query_parameterized_query pq" 
		+ " LEFT OUTER JOIN catissue_audit_event_query_log aeq ON aeq.query_id = pq.identifier" 
		+ " LEFT OUTER JOIN catissue_audit_event ae ON aeq.audit_event_id = ae.identifier" 
		+ " WHERE pq.identifier IN (%s)" 
		+ " AND pq.status = 'ACTIVE'" 
		+ " AND (aeq.query_id is null" 
		+ " OR(ae.event_timestamp =" 
				+ " (SELECT MAX(iae.event_timestamp)" 
				+ " FROM catissue_audit_event iae, catissue_audit_event_query_log iaeq" 
				+ " WHERE iaeq.audit_event_id = iae.identifier" 
				+ " AND iaeq.query_id = pq.identifier" 
				+ " AND pq.status = 'ACTIVE')))";
	
	private final static String UPDATE_QUERY_STATUS = 
		  " UPDATE query_parameterized_query pq"
		+ " SET pq.status = ?"
		+ " WHERE"
		+ " pq.identifier = ?";
	
	private final static String DELETE_TAGITEMS = "DELETE FROM query_tag_items WHERE obj_id = ?";
	
	private static final String IS_QUERY_NAME_EXIST = "SELECT name FROM ParameterizedQuery WHERE name = :queryName ";
	
	public List<QueryDTO> getAllQueries() 
	throws BizLogicException, DAOException {
		String appName=CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO jdbcDAO = daofactory.getJDBCDAO(); 
		ResultSet rs = null;
		try {
			jdbcDAO.openSession(null);
			rs = jdbcDAO.getResultSet(GET_ALL_QUERIES, null,null);
			return createQueryDTOs(rs);
		} catch (DAOException e) {
			throw new BizLogicException(null,e,"DAOException: while Retrieving queries");
		} finally {
			if (rs != null) {
				jdbcDAO.closeStatement(rs);
			}
			jdbcDAO.commit();	 
			jdbcDAO.closeSession();
		}
	}
	
	public List<QueryDTO> getQueriesById(Collection<Long> queryIds) throws BizLogicException, DAOException  
	{
		String appName=CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO jdbcDAO = daofactory.getJDBCDAO(); 
		ResultSet rs = null;
		try {
			jdbcDAO.openSession(null);
			String query = String.format(GET_QUERIES_BY_ID, getParamPlaceHolders(queryIds.size()));	 
			
			List<ColumnValueBean> parameters = new ArrayList<ColumnValueBean>();
			for (Long queryId : queryIds) {
			    parameters.add(new ColumnValueBean(queryId, DBTypes.LONG));
			}
			
			if(queryIds != null && !queryIds.isEmpty()){		
				rs = jdbcDAO.getResultSet(query, parameters, null);
			}
			
			 
			return createQueryDTOs(rs);
		} catch (DAOException e) {	
			throw new BizLogicException(null,e,"DAOException: while Retrieving queries");
		} finally {
			if (rs != null) {
				jdbcDAO.closeStatement(rs);
			}
			jdbcDAO.commit();
			jdbcDAO.closeSession();
		}
	}
	
	private List<QueryDTO> createQueryDTOs(ResultSet rs) 
	throws BizLogicException {
		List<QueryDTO> queries = new ArrayList<QueryDTO>();
		try {
			if(rs != null){
				while (rs.next()) {
					QueryDTO queryDTO = new QueryDTO();
					queryDTO.setQueryId(rs.getLong(1));
					queryDTO.setQueryName(StringEscapeUtils.escapeXml(rs.getString(2)));
					queryDTO.setQueryDescription(getDescription(rs.getString(3)));	
					queryDTO.setExecutedOn(getExecutedOnTime(rs.getTimestamp(4))); 
					queryDTO.setRootEntityName(getRootEntityName(rs.getString(5)));
					queryDTO.setCountOfRootRecords(getRootRecordCount(rs.getString(6)));
					queryDTO.setOwnerName(StringEscapeUtils.escapeXml(getOwnerName(rs.getString(1))));
					queries.add(queryDTO);
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Error while creating QueryDTOs");
		}
		return queries;	
	}
	
	private String getRootRecordCount(String recordCount) {
		String count = "N/A";
		if(recordCount != null && !recordCount.isEmpty()) {
			count = recordCount;
		}
		return count;
	}
	
	private String getDescription(String position) {
		String description = "Not Available";
		String desc = StringEscapeUtils.escapeXml(position);
		if(desc != null && !desc.isEmpty()) {
			description = desc;
		}
		return description;
	}
	
	private String getRootEntityName(String rootEntityName) {	 
		String rootEnName = "N/A";
		if(rootEntityName != null && !rootEntityName.isEmpty()) {
			rootEnName = rootEntityName.substring(rootEntityName.lastIndexOf('.') + 1);
		}
		return rootEnName;
	}
	
	private String getExecutedOnTime(Timestamp executedOnTime) {
		String executionTime = "N/A";
		if (executedOnTime != null){ 
			executionTime = new SimpleDateFormat(CommonServiceLocator.getInstance()
							.getDatePattern()).format(executedOnTime);
		}
		return executionTime;
	}
	
	//
	// TODO: Need to move out this method 
	//
	private String getOwnerName(String queryId) throws BizLogicException
	{
		DashboardBizLogic dashboardBizLogic = new DashboardBizLogic();
		User user = dashboardBizLogic.getQueryOwner(queryId);
		String ownerName = user.getLastName() + "," + user.getFirstName();
		return ownerName;
	}
	
	private String getParamPlaceHolders(int size) {
		StringBuilder parameters = new StringBuilder();
		for (int i = 0; i < size; i++) {
		    parameters.append("?, ");
		}

		if (size != 0) {
		    // remove trailing comma
		    parameters.delete(parameters.length() - 2, parameters.length());
		}
		
		return parameters.toString();
	}	
	
	public void deleteQuery(Long queryId) throws DAOException, BizLogicException
	{
		String appName=CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO jdbcDAO = daofactory.getJDBCDAO(); 
		try 
		{
			jdbcDAO.openSession(null);
			List<ColumnValueBean> parameters = new ArrayList<ColumnValueBean>();
			parameters.add(new ColumnValueBean("DELETED", DBTypes.VARCHAR));
			parameters.add(new ColumnValueBean(queryId, DBTypes.LONG));	
			jdbcDAO.executeUpdate(UPDATE_QUERY_STATUS, parameters); 
			jdbcDAO.commit();	 
		} 
		catch (DAOException e) 
		{
			throw new BizLogicException(null,e,"DAOException: while deleting query");
		} 
		finally 
		{
			jdbcDAO.closeSession();
		}
	}
	
	public void deleteQueryTagItem(Long queryId) throws DAOException, BizLogicException
	{
		String appName=CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO jdbcDAO = daofactory.getJDBCDAO(); 
		try 
		{
			jdbcDAO.openSession(null);
			List<ColumnValueBean> parameters = new ArrayList<ColumnValueBean>();
			parameters.add(new ColumnValueBean(queryId, DBTypes.LONG));
			jdbcDAO.executeUpdate(DELETE_TAGITEMS, parameters); 
			jdbcDAO.commit();	 
		} 
		catch (DAOException e) 
		{
			throw new BizLogicException(null,e,"DAOException: while deleting tag queries");
		} 
		finally 
		{
			jdbcDAO.closeSession();
		}
	}
	
	public boolean isQueryNamePresent(SessionDataBean sdb, String queryName) throws BizLogicException{	
		boolean isQueryNamePresent = false;
		HibernateDAO hibernateDao = null;
		List result = null;
		try
		{
			hibernateDao = DAOUtil.getHibernateDAO(sdb);
			List<ColumnValueBean> parameters = new ArrayList<ColumnValueBean>(); 
			parameters.add(new ColumnValueBean("queryName", queryName)); 
			result = hibernateDao.executeParamHQL(IS_QUERY_NAME_EXIST, parameters);
			if(! result.isEmpty()){
				isQueryNamePresent = true;
			}
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			if (hibernateDao != null)
			{
				try
				{
					DAOUtil.closeHibernateDAO(hibernateDao);
				}
				catch (DAOException e)
				{
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		return isQueryNamePresent;	
	}
}
