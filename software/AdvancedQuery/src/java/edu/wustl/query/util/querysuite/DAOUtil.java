
package edu.wustl.query.util.querysuite;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;


public class DAOUtil
{
	/**
	 *
	 * @return
	 * @throws DAOException
	 */
	public static JDBCDAO getJDBCDAO(SessionDataBean sessionDataBean) throws DAOException
	{

		String appName =AdvanceQueryDAO.getInstance().getAppName();
		IDAOFactory factory=DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO jdbcDao = factory.getJDBCDAO();
		jdbcDao.openSession(sessionDataBean);
		return jdbcDao;
	}

	/**
	 *
	 * @return
	 * @throws DAOException
	 *
	 */
	public static HibernateDAO getHibernateDAO(SessionDataBean sessionDataBean) throws DAOException
	{
		String appName = ApplicationProperties.getValue("app.name");//AdvanceQueryDAO.getInstance().getAppName();
		HibernateDAO hibernateDao = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory(appName).getDAO();
		hibernateDao.openSession(sessionDataBean);
		return hibernateDao;
	}

	/**
	 *
	 * @param jdbcDao
	 * @throws DAOException
	 */
	public static void closeJDBCDAO(JDBCDAO jdbcDao) throws DAOException
	{
		jdbcDao.closeSession();
	}

	/**
	 *
	 * @param hibernateDao
	 * @throws DAOException
	 */
	public static void closeHibernateDAO(HibernateDAO hibernateDao) throws DAOException
	{
		hibernateDao.closeSession();
	}
//	public static Session getCleanSession(SessionDataBean sessionDataBean)throws DAOException
//	{
//		String appName = CommonServiceLocator.getInstance().getAppName();
//		 IDAOFactory daoFactory = DAOConfigFactory.getInstance ().getDAOFactory (appName);
//		 DAO dao = daoFactory.getDAO ();
//
//		 dao. openSession(sessionDataBean);
//
//	}
}
