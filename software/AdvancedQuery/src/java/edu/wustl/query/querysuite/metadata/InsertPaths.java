
package edu.wustl.query.querysuite.metadata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * This class is called from an Ant target to insert indirect paths between clinportal/caTissue entities.
 * It takes a text file as an input. The file contains list of entities to be connected on each line.
 * E.g : edu.wustl.clinportal.domain.ClinicalStudy,edu.wustl.clinportal.domain.ClinicalStudyRegistration,
 * edu.wustl.catissue.domain.Participant,edu.wustl.catissue.domain.CollectionProtocol
 * @author deepti_shelar
 *
 */
public final class InsertPaths
{
	private InsertPaths(){}
	private static org.apache.log4j.Logger logger = LoggerConfig
	.getConfiguredLogger(InsertPaths.class);

	static private BufferedWriter writer = null;

	/**
	 * @param args name of the file input for paths
	 */
	public static void main(String[] args)
	{
		String fileName = "paths.txt";
		try
		{
			performMainFunction(fileName);
		}
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (DAOException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (SQLException e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * @param fileName name of the file
	 * @throws IOException IOException
	 * @throws DAOException DAOException
	 * @throws SQLException SQLException
	 */
	private static void performMainFunction(String fileName)
			throws IOException, DAOException, SQLException
	{
		List<List<String>> pathList;
		pathList = parseFile(fileName);
		writer = new BufferedWriter(new FileWriter("PathsLog.txt"));
		String appName = CommonServiceLocator.getInstance().getAppName();
		if(appName == null)
		{
			appName = "Query";
		}
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO dao = daoFactory.getJDBCDAO();
		dao.openSession(null);
		for (List<String> entityList : pathList)
		{
			getPathAndInsert(dao,entityList);
		}
		writer.write("\nCompleted inserting indirect paths between the given entities.");
		writer.flush();
		writer.close();
	}

	/**
	 * Parses the input text file and creates a list of entities.
	 * @param fileName input text file
	 * @return List of entities
	 * @throws IOException exception
	 */
	private static List<List<String>> parseFile(String fileName) throws IOException
	{
		List<List<String>> indirectPaths = new ArrayList<List<String>>();
		List<String> paths = new ArrayList<String>();
		File file = new File(fileName);

		BufferedReader bufRdr = new BufferedReader(new FileReader(file));
		String line;

		while ((line = bufRdr.readLine()) != null)
		{
			paths.add(line);
		}
		for (String path : paths)
		{
			List<String> singlePath = new ArrayList<String>();
			StringTokenizer tokenizer = new StringTokenizer(path, ",");
			while (tokenizer.hasMoreTokens())
			{
				singlePath.add(tokenizer.nextToken());
			}
			indirectPaths.add(singlePath);
		}
		return indirectPaths;
	}

	/**
	 * Gets the string of intermediate path and Inserts a record in path table for given set of entities
	 * @param dao
	 * @param entityList list of entities
	 * @throws IOException
	 * @throws SQLException
	 * @throws DAOException
	 * @throws IOException exception
	 * @throws DAOException exception
	 */
	private static void getPathAndInsert(JDBCDAO dao, List<String> entityList)
	throws DAOException, SQLException, IOException

	{
		List<Long> entityIdList = getEntityIDsList(dao, entityList, null);
		if(entityIdList == null)
		{
			writer.write("\nSkipping these entities as the above entity" +
					" does not present from the list of :"+entityList.toString());
			return;
		}
		List<Long> parentEntityIdList = getParentEntityIdsList(dao, entityIdList);
		List<String> intraModelId = getIntermediatePaths(dao, entityIdList, parentEntityIdList);
		if(intraModelId.isEmpty())
		{
			writer.write("\nNo path present between "+getEntityName(dao,entityIdList));
		}
		else
		{
			StringBuffer intermediatePath = new StringBuffer(intraModelId.get(0));
			for (int index = 1; index < intraModelId.size(); index++)
			{
				intermediatePath.append('_');
				intermediatePath.append(intraModelId.get(index));
			}
			Long maxPathId = getNextPathId(dao);
			insertPath(dao, entityIdList, intermediatePath, maxPathId);
		}
	}
	/**
	 * Inserts a record in path table for given set of entities
	 * @param dao dao
	 * @param entityIdList list of entity id's
	 * @param intermediatePath string path to be inserted
	 * @param maxPathId next path id
	 * @throws DAOException exception
	 * @throws SQLException exception
	 * @throws IOException exception
	 */
	private static void insertPath(JDBCDAO dao, List<Long> entityIdList,
			StringBuffer intermediatePath, Long maxPathId) throws DAOException,
			SQLException, IOException
	{
		LinkedList<Object> data = new LinkedList<Object>();
		Long srcEntId = entityIdList.get(0);
		Long targetEntId = entityIdList.get(entityIdList.size() - 1);
		data.add(srcEntId);
		data.add(targetEntId);
		LinkedList<ColumnValueBean> columnValueBean = populateColumnValueBean(data);
		String sql = "select INTERMEDIATE_PATH from PATH where FIRST_ENTITY_ID= ? and LAST_ENTITY_ID= ?";
		ResultSet resultSet = dao.getResultSet(sql, columnValueBean, null);
		boolean ifSamePathExists = false;
		List<Long> idList = new ArrayList<Long>();
		idList.add(srcEntId);
		idList.add(targetEntId);
		while (resultSet.next())
		{
			ifSamePathExists = ifIntermediatePathExists(dao, intermediatePath,
					resultSet, idList);
			continue;
		}
		dao.closeStatement(resultSet);
		if(!ifSamePathExists)
		{
			data = new LinkedList<Object>();
			data.add(maxPathId);
			data.add(srcEntId);
			data.add(intermediatePath.toString());
			data.add(targetEntId);
			columnValueBean = populateColumnValueBean(data);
			LinkedList<LinkedList<ColumnValueBean>> beanList = new LinkedList<LinkedList<ColumnValueBean>>();
			beanList.add(columnValueBean);
			sql = "INSERT INTO path values(?, ?, ?, ?)";
			dao.executeUpdate(sql, beanList);
			writer.write("\nInserted path between "+getEntityName(dao,idList));
			dao.commit();
		}
	}

	/**
	 * @param dao DAO
	 * @param intermediatePath intermediate path
	 * @param resultSet result set
	 * @param idList list of identifiers
	 * @return ifSamePathExists
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 * @throws DAOException DAOException
	 */
	private static boolean ifIntermediatePathExists(JDBCDAO dao,
			StringBuffer intermediatePath, ResultSet resultSet,
			List<Long> idList) throws SQLException, IOException, DAOException
	{
		boolean ifSamePathExists;
		String interPath = resultSet.getString("INTERMEDIATE_PATH");
		if(intermediatePath.toString().equalsIgnoreCase(interPath))
		{
			writer.write("\nSame path already exists between "+getEntityName(dao,idList));
			ifSamePathExists = true;
		}
		else
		{
			ifSamePathExists = false;
		}
		return ifSamePathExists;
	}
	/**
	 * Gets the maximum path id from Path table
	 * @param dao DAO
	 * @return long id
	 * @throws DAOException exception
	 * @throws SQLException exception
	 */
	private static Long getNextPathId(JDBCDAO dao) throws DAOException,
			SQLException
	{
		ResultSet resultSet;
		Long maxPathId = null;
		resultSet = dao.getQueryResultSet("select max(PATH_ID) from path");
		if (resultSet.next())
		{
			maxPathId = resultSet.getLong(1) + 1;
		}
		dao.closeStatement(resultSet);
		return maxPathId;
	}

	/**
	 * Gets the list of parent entity identifiers
	 * @param dao DAO
	 * @param entityIdList entity identifiers
	 * @return list of parent entity identifiers
	 * @throws SQLException exception
	 * @throws DAOException exception
	 * @throws IOException exception
	 */
	private static List<Long> getParentEntityIdsList(JDBCDAO dao, List<Long> entityIdList)
			throws SQLException, DAOException, IOException
	{
		List<Long> parententityIdList = new ArrayList<Long>();
		ResultSet resultSet = null;
		LinkedList<Object> data;
		LinkedList<ColumnValueBean> columnValueBean;
		for (int c = 0; c < entityIdList.size(); c++)
		{
			Long entityId = entityIdList.get(c);
			data = new LinkedList<Object>();
			data.add(entityId);
			columnValueBean = populateColumnValueBean(data);
			String sql = "select PARENT_ENTITY_ID from DYEXTN_ENTITY where IDENTIFIER=?";
			resultSet = dao.getResultSet(sql, columnValueBean, null);
			while (resultSet.next())
			{
				parententityIdList.add(resultSet.getLong(1));
			}
			dao.closeStatement(resultSet);
		}
		return parententityIdList;
	}

	/**
	 * Gets the list of entity identifiers
	 * @param dao DAO
	 * @param entityIdList entity identifiers
	 * @param entityGrpId group id
	 * @return list of entity identifiers
	 * @throws SQLException exception
	 * @throws DAOException exception
	 * @throws IOException exception
	 */
	private static List<Long> getEntityIDsList(JDBCDAO dao, List<String> entityList,
			Long entityGrpId) throws SQLException, DAOException, IOException
	{
		List<Long> entityIdList = new ArrayList<Long>();
		LinkedList<Object> data;
		LinkedList<ColumnValueBean> columnValueBean;
		String sql;
		ResultSet resultSet = null;
		for (String entityName : entityList)
		{
			data = new LinkedList<Object>();
			data.add(entityName);
			if(entityGrpId == null)
			{
				sql = "select dam.IDENTIFIER from DYEXTN_ABSTRACT_METADATA dam ,DYEXTN_ENTITY de where dam.NAME=?" +
						" and  dam.identifier= de.identifier ";
			}
			else
			{
				data.add(entityGrpId);
				sql = "select dam.IDENTIFIER from DYEXTN_ABSTRACT_METADATA dam ,DYEXTN_ENTITY" +
					" de where dam.NAME= ? and  dam.identifier= de.identifier and de.ENTITY_GROUP_ID = ?";
			}
			columnValueBean = populateColumnValueBean(data);
			resultSet = dao.getResultSet(sql, columnValueBean, null);
			if(resultSet.next())
			{
				entityIdList.add(resultSet.getLong(1));
			}
			else
			{
				writer.write("\nEntity does not exist with name : "+entityName);
				entityIdList = null;
				break;
			}
		}
		dao.closeStatement(resultSet);
		return entityIdList;
	}

	/**
	 * Returns the string of intermediate path
	 * @param dao DAO
	 * @param entityIdList list of entity identifiers
	 * @param parentEntityIdList list of parent entity identifiers
	 * @return path string
	 * @throws SQLException exception
	 * @throws DAOException exception
	 * @throws IOException exception
	 */
	private static List<String> getIntermediatePaths(JDBCDAO dao, List<Long> entityIdList,
			List<Long> parentEntityIdList) throws SQLException, IOException, DAOException
	{
		LinkedList<Object> data;
		LinkedList<ColumnValueBean> columnValueBean;
		List<String> intraModelId = new ArrayList<String>();
		for (int index = 0; index < entityIdList.size() - 1; index++)
		{
			if (index + 1 < entityIdList.size())
			{
				boolean notFound = getIntermediatePath(dao, entityIdList,
						intraModelId, index);
				if (notFound)
				{
					data = new LinkedList<Object>();
					data.add(parentEntityIdList.get(index));
					data.add(entityIdList.get(index + 1));
					columnValueBean = populateColumnValueBean(data);
					ResultSet rs1;
					String sql1 = "select INTERMEDIATE_PATH from PATH where FIRST_ENTITY_ID=?" +
							" and LAST_ENTITY_ID=?";
					rs1 = dao.getResultSet(sql1, columnValueBean, null);
					boolean found = isFound(intraModelId, rs1, false);
					dao.closeStatement(rs1);
					if (!found)
					{
						data = new LinkedList<Object>();
						data.add(entityIdList.get(index));
						data.add(parentEntityIdList.get(index + 1));
						columnValueBean = populateColumnValueBean(data);
						populateIntraModelIds(dao, columnValueBean,
								intraModelId);
					}
				}
			}
		}
		return intraModelId;
	}

	/**
	 * @param intraModelId identifier
	 * @param rs1 result set
	 * @param found found
	 * @return isFound
	 * @throws SQLException SQLException
	 */
	private static boolean isFound(List<String> intraModelId, ResultSet rs1,
			boolean found) throws SQLException
	{
		boolean isFound = found;
		while (rs1.next())
		{
			intraModelId.add(rs1.getString(1));
			isFound = true;
		}
		return isFound;
	}

	/**
	 * @param dao DAO
	 * @param columnValueBean bean
	 * @param intraModelId identifier
	 * @throws DAOException DAOException
	 * @throws SQLException SQLException
	 */
	private static void populateIntraModelIds(JDBCDAO dao,
			LinkedList<ColumnValueBean> columnValueBean,
			List<String> intraModelId) throws DAOException, SQLException
	{
		ResultSet rs2;
		String sql2 = "select INTERMEDIATE_PATH from PATH where FIRST_ENTITY_ID=?" +
				" and LAST_ENTITY_ID=?";
		rs2 = dao.getResultSet(sql2, columnValueBean, null);
		while (rs2.next())
		{
			intraModelId.add(rs2.getString(1));
		}
		dao.closeStatement(rs2);
	}

	/**
	 * @param dao DAO
	 * @param entityIdList entity list
	 * @param intraModelId id's
	 * @param index index
	 * @return notFound
	 * @throws DAOException DAOException
	 * @throws SQLException SQLException
	 */
	private static boolean getIntermediatePath(JDBCDAO dao,
			List<Long> entityIdList, List<String> intraModelId, int index)
			throws DAOException, SQLException
	{
		LinkedList<Object> data;
		LinkedList<ColumnValueBean> columnValueBean;
		ResultSet resultSet;
		data = new LinkedList<Object>();
		data.add(entityIdList.get(index));
		data.add(entityIdList.get(index + 1));
		columnValueBean = populateColumnValueBean(data);
		boolean notFound = true;
		String sql = "select INTERMEDIATE_PATH from PATH where FIRST_ENTITY_ID= ? and LAST_ENTITY_ID= ?";
		resultSet = dao.getResultSet(sql, columnValueBean, null);
		while (resultSet.next())
		{
			notFound = false;
			intraModelId.add(resultSet.getString(1));
		}
		dao.closeStatement(resultSet);
		return notFound;
	}
	/**
	 *
	 * @param dao DAO
	 * @param idList idList
	 * @return entity name
	 * @throws DAOException DAOException
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	private static String getEntityName(JDBCDAO dao,List<Long> idList) throws DAOException, SQLException, IOException
	{
		StringBuffer buffer = new StringBuffer();
		LinkedList<Object> data = new LinkedList<Object>();
		buffer.append('(');
		for (Long id : idList)
		{
			data.add(id);
			buffer.append(",?");
		}
		buffer.append(')');
		if(buffer.charAt(1) == ',')
		{
			buffer.replace(1, 2, "");
		}
		String sql = "select name from DYEXTN_ABSTRACT_METADATA where identifier in "+buffer.toString();
		LinkedList<ColumnValueBean> columnValueBean = populateColumnValueBean(data);
		ResultSet resultSet = dao.getResultSet(sql, columnValueBean, null);
		buffer = new StringBuffer();
		while (resultSet.next())
		{
			buffer.append(resultSet.getString(1));
			buffer.append(',');
		}
		dao.closeStatement(resultSet);
		return buffer.toString();
	}

	/**
	 * @param data data
	 * @return columnValueBean
	 */
	private static LinkedList<ColumnValueBean> populateColumnValueBean(LinkedList<Object> data)
	{
		LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
		ColumnValueBean bean;
		for(Object object : data)
		{
			bean = new ColumnValueBean(object.toString(), object);
			columnValueBean.add(bean);
		}
		return columnValueBean;
	}
}
