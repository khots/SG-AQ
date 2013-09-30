/**
 *
 */

package edu.wustl.query.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ColumnPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintKeyPropertiesInterface;
import edu.wustl.cab2b.common.beans.MatchedClass;
import edu.wustl.cab2b.common.beans.MatchedClassEntry;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.QueryResultObjectData;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.AbstractClient;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.dao.util.HibernateMetaData;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.QueryCSMValidator;
import edu.wustl.query.util.global.Variables;
import edu.wustl.query.util.querysuite.DAOUtil;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.global.Permissions;
import edu.wustl.security.privilege.IValidator;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;

/**
 * @author supriya_dankh
 * This class is a cache manager class for CsmCache.
 * This class will add CP and privileges related to it in CsmCache
 * for Read as well as Identified Data Access.
 * Also it filters results by using checkPermission() method of SecurityManager.
 */
public class QueryCsmCacheManager
{
	/**
	 * Object of JDBCDAO.
	 */
	private JDBCDAO jdbcDAO;

	/**
	 * IValidator instance.
	 */
	private IValidator validator;

	private Map<Long, Boolean> phiAccessMap = null;
	/**
	 * Parameterized constructor.
	 * @param jdbcDAO Instance of JDBCDAO
	 */
	public QueryCsmCacheManager(JDBCDAO jdbcDAO)
	{
		this.jdbcDAO = jdbcDAO;
		validator = getValidatorInstance();
		phiAccessMap = new HashMap<Long, Boolean>();
	}

	/**
	 * Return the appropriate Validator instance.
	 * @return Validator instance
	 */
	private IValidator getValidatorInstance()
	{
		IValidator validatorInstance;
		if(Variables.validatorClassname == null || Variables.validatorClassname.length()==0)
		{
			validatorInstance =  null;
		}
		else
		{
			validatorInstance = (IValidator) Utility.getObject(Variables.validatorClassname);
		}
		return validatorInstance;
	}

	/**
	 * Get the QueryCsmCache object.
	 * @return csmCache Object of QueryCsmCache
	 */
	public QueryCsmCache getNewCsmCacheObject()
	{
		return new QueryCsmCache();
	}

	/**
	 * This method checks user's permissions (Read, Identified data access) for every entity in query result
	 * and filters result accordingly.
	 * @param sessionDataBean A data bean that contains information related to user logged in.
	 * @param queryResultObjectDataMap queryResultObjectDataMap
	 * @param aList List of records
	 * @param cache QueryCsmCache object
	 * @throws SMException Security Manager Exception
	 */
	public void filterRow(SessionDataBean sessionDataBean,
			Map<String, QueryResultObjectDataBean> queryResultObjectDataMap, List aList,
			QueryCsmCache cache) throws SMException
	{
		Boolean isAuthorisedUser = true;
		Boolean hasPrivilegeOnID = true;
		if (queryResultObjectDataMap != null)
		{
			Set keySet = queryResultObjectDataMap.keySet();
			for (Object key : keySet)
			{
				QueryResultObjectDataBean queryResultObjectDataBean = queryResultObjectDataMap
						.get(key);
				String entityName = queryResultObjectDataBean.getCsmEntityName();
				int mainEntityId = -1;
				long finalMainEntityId = mainEntityId;
				mainEntityId = getMainEntityIdFromBean(aList,
						queryResultObjectDataBean, mainEntityId);
				//Check if user has read privilege on particular object or not.
				if ((mainEntityId != -1) && (queryResultObjectDataBean.isReadDeniedObject()))
				{
					int mainProtocolIdIndex = queryResultObjectDataBean.getMainProtocolIdIndex();
					List<List<String>> cpIdsList = null;
					if(mainProtocolIdIndex != -1)
					{
						cpIdsList = getCSIdFromDataList(aList, finalMainEntityId, mainProtocolIdIndex);
					}
					else
					{
						finalMainEntityId = getFinalMainEntityId(
								queryResultObjectDataBean, mainEntityId, finalMainEntityId);
						cpIdsList =
							getCpIdsListForGivenEntityId(entityName, finalMainEntityId);
					}
					//if this object is not associated to any CP
					//then user will not have identified privilege on it.
					//hasPrivilegeOnID = checkHasPrivilegeOnId(sessionDataBean, cache, cpIdsList);
				}
				removeUnauthorizedData(aList, isAuthorisedUser,
						hasPrivilegeOnID,queryResultObjectDataBean);
			}
		}
	}

	/**
	 * @param aList list
	 * @param queryResultObjectDataBean bean
	 * @param mainEntityId main entity id
	 * @return mainEntityId
	 */
	private int getMainEntityIdFromBean(List aList,
			QueryResultObjectDataBean queryResultObjectDataBean,
			int mainEntityId)
	{
		int entityId = mainEntityId;
		if (queryResultObjectDataBean.getMainEntityIdentifierColumnId() != -1)
		{
			String tempEntityId = (String) aList.get
			(queryResultObjectDataBean.getMainEntityIdentifierColumnId());
			if(tempEntityId != null && tempEntityId.length() != 0)
			{
				entityId = Integer.parseInt((String) aList.get
					(queryResultObjectDataBean.getMainEntityIdentifierColumnId()));
			}
		}
		return entityId;
	}

	/**
	 * @param sessionDataBean A data bean that contains information related to user logged in.
	 * @param cache cache
	 * @param cpIdsList list
	 * @return <CODE>true</CODE> user has privilege on identified data,
	 * <CODE>false</CODE> otherwise
	 * @throws SMException Security Manager Exception
	 */
	public Boolean checkHasPrivilegeOnId(SessionDataBean sessionDataBean,
			QueryCsmCache cache, List<List<String>> cpIdsList)
			throws SMException
	{
		Boolean hasPrivilegeOnID;
		if (cpIdsList.isEmpty())
		{
			hasPrivilegeOnID =
				checkPermissionOnGlobalParticipant(sessionDataBean);
		}
		else
		{
			List<Boolean> iPrivilegeList = new ArrayList<Boolean>();
			for (int i = 0; i < cpIdsList.size(); i++)
			{
				List<String> cpIdList = cpIdsList.get(i);
				Long cpId = cpIdList.get(0) != null ? Long.parseLong(cpIdList.get(0)) : -1;
				Boolean isAllowed = phiAccessMap.get(cpId);
				if (isAllowed == null) {
					isAllowed = checkPermission(sessionDataBean, Variables.mainProtocolObject, cpId, Permissions.PHI);
					phiAccessMap.put(cpId, isAllowed);
						
				} 
				
				iPrivilegeList.add(isAllowed);				
			}
			hasPrivilegeOnID = isAuthorizedUser(iPrivilegeList, false);
		}
		return hasPrivilegeOnID;
	}

	/**
	 * @param queryResultObjectDataBean bean
	 * @param mainEntityId main entity id
	 * @param finalMainEntityId final main entity id
	 * @return final main entity id
	 */
	private long getFinalMainEntityId(
			QueryResultObjectDataBean queryResultObjectDataBean,
			int mainEntityId, long finalMainEntityId)
	{
		long tempMainEntityId = finalMainEntityId;
		if(queryResultObjectDataBean.getMainEntity() != null &&
				!queryResultObjectDataBean.getEntity().equals
				(queryResultObjectDataBean.getMainEntity()))
		{
			tempMainEntityId = getMainEntityId(queryResultObjectDataBean,
					mainEntityId, finalMainEntityId);
		}
		if(tempMainEntityId == -1)
		{
			tempMainEntityId = mainEntityId;
		}
		return tempMainEntityId;
	}

	/**
	 * @param queryResultObjectDataBean queryResultObjectDataBean
	 * @param mainEntityId mainEntityId
	 * @param finalMainEntityId finalMainEntityId
	 * @return mainEntityId
	 */
	private long getMainEntityId(QueryResultObjectDataBean queryResultObjectDataBean,
			int mainEntityId, long finalMainEntityId)
	{
		long entityId = finalMainEntityId;
		FileInputStream inputStream = null;

		HibernateDAO hibernateDAO = null;
		try
		{
			String appName = CommonServiceLocator.getInstance().getAppHome();
		    File file = new File(appName+ System.getProperty("file.separator")+"WEB-INF"+
		    		System.getProperty("file.separator")+"classes"+System.getProperty("file.separator")
		    		+"mainProtocolObjectQueries.properties");
		    if(file.exists())
		    {
		       inputStream = new FileInputStream(file);
		       Properties mainProtObjFile = new Properties();
		       mainProtObjFile.load(inputStream);
		       String sql = mainProtObjFile.getProperty(queryResultObjectDataBean.getEntity().getName());
		       hibernateDAO = DAOUtil.getHibernateDAO(null);
		       if(sql == null)
		       {
		    	   entityId = getMainEntityIdForDE(queryResultObjectDataBean,
					mainEntityId, finalMainEntityId, hibernateDAO, mainProtObjFile);
		       }
		       else	
		       {
		    	   sql = sql + mainEntityId;
		    	  /* List<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
		    	   ColumnValueBean bean = new ColumnValueBean("mainEntityId",Integer.valueOf(mainEntityId));
		    	   columnValueBean.add(bean);*/
		    	   List<Long> allMainObjectIds = hibernateDAO.executeQuery(sql);
		    	   if(!allMainObjectIds.isEmpty() && allMainObjectIds.get(0) != null)
		    	   {
		    		   entityId = allMainObjectIds.get(0).longValue();
		    	   }
		       }
		    }
		}
		catch (DAOException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				DAOUtil.closeHibernateDAO(hibernateDAO);
				if(inputStream != null)
				{
					inputStream.close();
				}
			}
			catch (DAOException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return entityId;
	}

	/**
	 * @param queryResultObjectDataBean queryResultObjectDataBean
	 * @param mainEntityId mainEntityId
	 * @param finalMainEntityId finalMainEntityId
	 * @param hibernateDAO hibernateDAO
	 * @param mainProtObjFile mainProtObjFile
	 * @return main entity id
	 * @throws DAOException DAOException
	 * @throws SQLException SQLException
	 */
	private long getMainEntityIdForDE(QueryResultObjectDataBean queryResultObjectDataBean,
			int mainEntityId, long finalMainEntityId, HibernateDAO hibernateDAO,
			Properties mainProtObjFile) throws DAOException, SQLException
	{
		long entityId = finalMainEntityId;
		EntityInterface originalEntity = queryResultObjectDataBean.getEntity();
		EntityCache entityCache = EntityCacheFactory.getInstance();
		List<String> entityNames = getHookEntities(mainProtObjFile);
		EntityInterface associatedHookEntity = null;
		for(String hookEntityName : entityNames)
		{
			EntityInterface hookEntity = DomainObjectFactory.getInstance().createEntity();
			hookEntity.setName(hookEntityName.
					substring(hookEntityName.lastIndexOf('.')+1, hookEntityName.length()));
			hookEntity.setDescription(null);
			hookEntity = populateHookEntity(entityCache, hookEntity, hookEntityName);
			AssociationMetadataInterface association = hookEntity.getAssociation(originalEntity);
			if(association != null)
			{
				associatedHookEntity = hookEntity;
				break;
			}
		}
		if(associatedHookEntity!=null)
		{
			Long hookEntityId = getAssociationDetails(mainEntityId, originalEntity,
					associatedHookEntity,queryResultObjectDataBean.getMainEntity());
	  	   String sql = mainProtObjFile.getProperty(associatedHookEntity.getName());
	  	   if(sql == null)
	  	   {
	  		 entityId = hookEntityId;
	  	   }
	  	   else
	  	   {
	  		   sql = sql + hookEntityId;
			   List<Long> allMainObjectIds = hibernateDAO.executeQuery(sql);
			   if(!allMainObjectIds.isEmpty())
			   {
				   entityId = allMainObjectIds.get(0).longValue();
			   }
	  	   }
		}
  	   return entityId;
	}

	/**
	 * Get the list of possible hook entities.
	 * @param mainProtObjFile property file
	 * @return hookEntityList
	 */
	private List<String> getHookEntities(Properties mainProtObjFile)
	{
		List<String> hookEntityList = new ArrayList<String>();
		String hookEntityString = mainProtObjFile.getProperty("hookEntity");
		StringTokenizer tokens = new StringTokenizer(hookEntityString, ",");
		while(tokens.hasMoreTokens())
		{
			hookEntityList.add(tokens.nextToken());
		}

		return hookEntityList;
	}

	/**
	 * @param entityCache entityCache
	 * @param hookEntity hookEntity
	 * @param hookEntityName hookEntityName
	 * @return hookEntity
	 */
	private EntityInterface populateHookEntity(EntityCache entityCache, EntityInterface hookEntity,
			String hookEntityName)
	{
		EntityInterface tempHookEntity = hookEntity;
		Collection<EntityInterface> entityCollection = new HashSet<EntityInterface>();
		entityCollection.add(tempHookEntity);
		MatchedClass matchedClass = entityCache.getEntityOnEntityParameters(entityCollection);
		MatchedClass resultMatchClass = new MatchedClass();
		for (MatchedClassEntry matchedClassEntry : matchedClass.getMatchedClassEntries())
		{
			resultMatchClass.addMatchedClassEntry(matchedClassEntry);
		}
		resultMatchClass.setEntityCollection(resultMatchClass.getSortedEntityCollection());
		for(EntityInterface entity : resultMatchClass.getEntityCollection())
		{
			if(entity.getName().equals(hookEntityName))
			{
				tempHookEntity = entity;
				break;
			}
		}
		return tempHookEntity;
	}

	/**
	 * @param mainEntityId mainEntityId
	 * @param originalEntity originalEntity
	 * @param hookEntity hookEntity
	 * @param csmEntity csmEntity
	 * @return hookEntityId
	 * @throws DAOException DAOException
	 * @throws SQLException SQLException
	 */
	private Long getAssociationDetails(int mainEntityId, EntityInterface originalEntity,
			EntityInterface hookEntity, EntityInterface csmEntity) throws DAOException, SQLException
	{
		String sql;
		AssociationInterface association = getActualAssociation(originalEntity, hookEntity);
		if(association == null)
		{
			association = getActualAssociation(originalEntity, csmEntity);
		}
		if(association == null)
		{
			association = getMainContainerAssociation(originalEntity,
					hookEntity);
		}
		String tableName = association.getConstraintProperties().getName();
		Collection<ConstraintKeyPropertiesInterface> tgrKeyColl =
		association.getConstraintProperties().getTgtEntityConstraintKeyPropertiesCollection();
		String columnName = null;
		for(ConstraintKeyPropertiesInterface tgtConstraintKey : tgrKeyColl)
		{
			ColumnPropertiesInterface colProperties =
				tgtConstraintKey.getTgtForiegnKeyColumnProperties();
			columnName = colProperties.getName();
			break;
		}
  	   sql = "SELECT "+columnName+" FROM "+tableName+" WHERE IDENTIFIER = ?";
  	   LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
	   ColumnValueBean bean = new ColumnValueBean("mainEntityId",Integer.valueOf(mainEntityId));
	   columnValueBean.add(bean);
  	   ResultSet resultSet = jdbcDAO.getResultSet(sql, columnValueBean,null);
  	   Long foreignKey = null;
  	   Long hookEntityId = getHookEntity(resultSet, foreignKey);
  	   return hookEntityId;
	}

	/**
	 * @param hookEntityId hook Entity Id
	 * @param resultSet result Set
	 * @param foreignKey foreign Key
	 * @return hookEntityId
	 * @throws SQLException SQLException
	 * @throws DAOException DAOException
	 */
	private Long getHookEntity(ResultSet resultSet,
			Long foreignKey) throws SQLException, DAOException
	{
		Long hookEntityId = null;
		if(resultSet != null)
		   {
		  	   if(resultSet.next())
		  	   {
				   foreignKey = resultSet.getLong(1);
		  	   }
		  	   jdbcDAO.closeStatement(resultSet);
		   }
		   if(foreignKey != null)
		   {
			   hookEntityId = foreignKey;
		   }
		return hookEntityId;
	}

	/**
	 * If the association between hook entity and DE entity is null, then get the association
	 * between hook entity and the main container (like Annotations, etc.).
	 * @param originalEntity originalEntity
	 * @param hookEntity hookEntity
	 * @return association object
	 * @throws DAOException DAOException
	 * @throws SQLException SQLException
	 */
	private AssociationInterface getMainContainerAssociation(
			EntityInterface originalEntity, EntityInterface hookEntity)
			throws DAOException, SQLException
	{
		AssociationInterface association;
		String pathSql = "SELECT INTERMEDIATE_PATH FROM PATH WHERE FIRST_ENTITY_ID = ? " +
		" AND LAST_ENTITY_ID = ? ";

		LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
		ColumnValueBean bean = new ColumnValueBean("FIRST_ENTITY_ID",hookEntity.getId());
		columnValueBean.add(bean);
		bean = new ColumnValueBean("LAST_ENTITY_ID",originalEntity.getId());
		columnValueBean.add(bean);
		ResultSet resultSet1 = jdbcDAO.getResultSet(pathSql, columnValueBean, null);
		String intermediatePath = "";
		if(resultSet1 != null && resultSet1.next())
		{
			intermediatePath = resultSet1.getString(1);
			intermediatePath = intermediatePath.substring(0, intermediatePath.indexOf('_'));
		}
		jdbcDAO.closeStatement(resultSet1);
		pathSql = "SELECT DE_ASSOCIATION_ID FROM INTRA_MODEL_ASSOCIATION WHERE ASSOCIATION_ID = ?";
		columnValueBean = new LinkedList<ColumnValueBean>();
		bean = new ColumnValueBean("intermediatePath",intermediatePath);
		columnValueBean.add(bean);
		resultSet1 = jdbcDAO.getResultSet(pathSql, columnValueBean, null);
		Long associationId = null;
		if(resultSet1 != null && resultSet1.next())
		{
			associationId = resultSet1.getLong(1);
		}
		jdbcDAO.closeStatement(resultSet1);
		association = hookEntity.getAssociationByIdentifier(Long.valueOf(associationId));
		return association;
	}

	/**
	 * @param originalEntity originalEntity
	 * @param hookEntity hookEntity
	 * @return association
	 */
	private AssociationInterface getActualAssociation(EntityInterface originalEntity,
			EntityInterface hookEntity)
	{
		AssociationInterface association = null;
		Collection<AssociationInterface> associationColl = hookEntity.getAssociationCollection();

		for(AssociationInterface assoc : associationColl)
		{
			if(assoc.getTargetEntity().getId().equals(originalEntity.getId()))
			{
				association = assoc;
				break;
			}
		}
		return association;
	}

	/**
	 * Remove the data that the user is not authorized to see.
	 * @param aList List of records
	 * @param isAuthorisedUser to specify if the user is authorized or not
	 * @param hasPrivilegeOnID to specify if the user has privilege to view identified data
	 * @param queryResultObjectDataBean Object of QueryResultObjectDataBean
	 */
	public void removeUnauthorizedData(List aList, Boolean isAuthorisedUser,
			Boolean hasPrivilegeOnID,QueryResultObjectDataBean queryResultObjectDataBean)
	{
		if (isAuthorisedUser)
		{
			//If user is not authorized to see identified data then
			//replace identified column values by ##
			if (!hasPrivilegeOnID)
			{
				removeUnauthorizedFieldsData(aList,true,queryResultObjectDataBean);
			}
		}
		else
		{
			removeUnauthorizedFieldsData(aList,false,queryResultObjectDataBean);
		}
	}

	
	
	/**
	 * Checks if the user has privilege on identified data.
	 * @param sessionDataBean SessionDataBean object
	 * @param queryResultObjectDataMap queryResultObjectDataMap
	 * @param aList List of records
	 * @param cache QueryCsmCache object
	 * @return <CODE>true</CODE> user has privilege on identified data,
	 * <CODE>false</CODE> otherwise
	 * @throws SMException Security Manager Exception
	 */
	public boolean hasPrivilegeOnIdentifiedData(SessionDataBean sessionDataBean,
			Map<String, QueryResultObjectDataBean> queryResultObjectDataMap, List aList,
			QueryCsmCache cache) throws SMException
	{
		Boolean hasPrivilegeOnIdentifiedData = true;
		if (queryResultObjectDataMap != null)
		{
			Set keySet = queryResultObjectDataMap.keySet();
			for (Object key : keySet)
			{
				QueryResultObjectDataBean queryResultObjectDataBean = queryResultObjectDataMap
						.get(key);
				String entityName = queryResultObjectDataBean.getCsmEntityName();
				int mainEntityId = -1;
				mainEntityId = getMainEntityIdFromBean(aList,
						queryResultObjectDataBean, mainEntityId);
				long finalMainEntityId = mainEntityId;
				//Check if user has identified data access on particular object or not.
				if (mainEntityId != -1)
				{
					finalMainEntityId = getFinalMainEntityId(
							queryResultObjectDataBean, mainEntityId,
							finalMainEntityId);
					List<List<String>> cpIdsList =
						getCpIdsListForGivenEntityId(entityName, finalMainEntityId);
					//if this object is not associated to any CP
					//then user will not have identified privilege on it.
					hasPrivilegeOnIdentifiedData = checkForIDPrivilege(
							sessionDataBean, cache, cpIdsList);
					break;	
				}
			}
		}
		return hasPrivilegeOnIdentifiedData;
	}

	/**
	 * @param sessionDataBean A data bean that contains information related to user logged in.
	 * @param cache cache
	 * @param cpIdsList list
	 * @return <CODE>true</CODE> user has privilege on identified data,
	 * <CODE>false</CODE> otherwise
	 * @throws SMException Security manager exception
	 */
	public Boolean checkForIDPrivilege(SessionDataBean sessionDataBean,
			QueryCsmCache cache, List<List<String>> cpIdsList)
			throws SMException
	{
		Boolean hasPrivilegeOnIdentifiedData;
		if (cpIdsList.isEmpty())
		{
			hasPrivilegeOnIdentifiedData = checkPermissionOnGlobalParticipant(sessionDataBean);
		}
		else
		{
			List<Boolean> identifiedPrivilegeList = new ArrayList<Boolean>();
			for (int i = 0; i < cpIdsList.size(); i++)
			{
				List<String> cpIdList = cpIdsList.get(i);
				updatePrivilegeList(sessionDataBean, cache, null, identifiedPrivilegeList, cpIdList);
			}
			hasPrivilegeOnIdentifiedData = isAuthorizedUser(identifiedPrivilegeList, false);
		}
		return hasPrivilegeOnIdentifiedData;
	}

	/**
	 * Checks user's permissions (Read, Identified data access) for every entity in query result.
	 * and filters result accordingly for simple search.
	 * @param sessionDataBean A data bean that contains information related to user logged in.
	 * @param queryResultObjectDataMap queryResultObjectDataMap
	 * @param aList List of records
	 * @param cache QueryCsmCache object
	 * @throws SMException Security manager exception
	 */
	public void filterRowForSimpleSearch(SessionDataBean sessionDataBean,
			Map queryResultObjectDataMap, List aList, QueryCsmCache cache) throws SMException
	{
		Boolean isAuthorisedUser = true;

		Set keySet = queryResultObjectDataMap.keySet();
		Iterator keyIterator = keySet.iterator();

		while(keyIterator.hasNext())
		{
			isAuthorisedUser = checkIfAuthorized(sessionDataBean,
					queryResultObjectDataMap, aList, cache, isAuthorisedUser,
					keyIterator);
		}
	}

	/**
	 * @param sessionDataBean A data bean that contains information related to user logged in.
	 * @param queryResultObjectDataMap queryResultObjectDataMap
	 * @param aList aList
	 * @param cache cache
	 * @param isAuthorisedUser isAuthorisedUser
	 * @param keyIterator keyIterator
	 * @return isAuthorisedUser
	 * @throws SMException SMException
	 */
	private Boolean checkIfAuthorized(SessionDataBean sessionDataBean,
			Map queryResultObjectDataMap, List aList, QueryCsmCache cache,
			Boolean isAuthorisedUser, Iterator keyIterator) throws SMException
	{
		Boolean ifAuthorizedUser = isAuthorisedUser;
		Boolean hasPrivilegeOnIdentifiedData;
		QueryResultObjectData queryResultObjectData;
		queryResultObjectData = (QueryResultObjectData) queryResultObjectDataMap
				.get(keyIterator.next());

		int entityId = Integer.parseInt(aList
				.get(queryResultObjectData.getIdentifierColumnId()).toString());
		String entityName = getEntityName(queryResultObjectData);

		List<List<String>> cpIdsList = getCpIdsListForGivenEntityId(entityName, entityId);

		//if this object is not associated to any CP
		//then user will not have identified privilege on it.
		if (cpIdsList.isEmpty())
		{
			hasPrivilegeOnIdentifiedData = checkPermissionOnGlobalParticipant(sessionDataBean);
		}
		else
		{
			List<Boolean> readPrivilegeList = new ArrayList<Boolean>();
			List<Boolean> identifiedPrivilegeList = new ArrayList<Boolean>();

			for (int i = 0; i < cpIdsList.size(); i++)
			{
				List<String> cpIdList = cpIdsList.get(i);
				updatePrivilegeList(sessionDataBean, cache, readPrivilegeList,
						identifiedPrivilegeList, cpIdList);
			}
			ifAuthorizedUser = isAuthorizedUser(readPrivilegeList, true);
			hasPrivilegeOnIdentifiedData = isAuthorizedUser(identifiedPrivilegeList, false);
		}
		//If user is not authorized to read the data
		//then remove all data related to this particular from row.
		List identifiedColumnIdentifiers = queryResultObjectData.getIdentifiedDataColumnIds();
		List objectColumnIdentifiers = queryResultObjectData.getDependentColumnIds();
		removeUnauthorizedData(aList, ifAuthorizedUser, hasPrivilegeOnIdentifiedData,
				identifiedColumnIdentifiers, objectColumnIdentifiers, true);
		return ifAuthorizedUser;
	}

	/**
	 * Checks if the user has privilege on identified data for simple search.
	 * @param sessionDataBean A data bean that contains information related to user logged in.
	 * @param queryResultObjectDataMap queryResultObjectDataMap
	 * @param aList List of records
	 * @param cache QueryCsmCache object
	 * @return <CODE>true</CODE> user has privilege on identified data,
	 * <CODE>false</CODE> otherwise
	 * @throws SMException Security Manager Exception
	 */
	public boolean hasPrivilegeOnIdentifiedDataForSimpleSearch(SessionDataBean sessionDataBean,
			Map queryResultObjectDataMap, List aList, QueryCsmCache cache) throws SMException
	{
		// boolean that indicates whether user has privilege on identified data
		boolean hasPrivilegeOnIdentifiedData = true;

		Set keySet = queryResultObjectDataMap.keySet();
		Iterator keyIterator = keySet.iterator();
		QueryResultObjectData queryResultObjectData;

		while(keyIterator.hasNext())
		{
			queryResultObjectData = (QueryResultObjectData) queryResultObjectDataMap
					.get(keyIterator.next());

			int entityId = Integer.parseInt(aList
					.get(queryResultObjectData.getIdentifierColumnId()).toString());

			String entityName = getEntityName(queryResultObjectData);

			List<List<String>> cpIdsList = getCpIdsListForGivenEntityId(entityName, entityId);

			hasPrivilegeOnIdentifiedData = checkIDPrivilegeForSimpleSearch(
					sessionDataBean, cache, cpIdsList);
		}
		return hasPrivilegeOnIdentifiedData;
	}

	/**
	 * @param sessionDataBean A data bean that contains information related to user logged in.
	 * @param cache cache
	 * @param cpIdsList list
	 * @return <CODE>true</CODE> user has privilege on identified data,
	 * <CODE>false</CODE> otherwise
	 * @throws SMException Security manager exception
	 */
	private boolean checkIDPrivilegeForSimpleSearch(
			SessionDataBean sessionDataBean, QueryCsmCache cache,
			List<List<String>> cpIdsList) throws SMException
	{
		boolean hasPrivilegeOnIdentifiedData;
		//if this object is not associated to any CP then
		//user will not have identified privilege on it.
		if (cpIdsList.isEmpty())
		{
			hasPrivilegeOnIdentifiedData = checkPermissionOnGlobalParticipant(sessionDataBean);
		}
		else
		{
			List<Boolean> identifiedPrivilegeList = new ArrayList<Boolean>();
			for (int i = 0; i < cpIdsList.size(); i++)
			{
				List<String> cpIdList = cpIdsList.get(i);
				updatePrivilegeList(sessionDataBean, cache, null, identifiedPrivilegeList,
						cpIdList);
			}
			hasPrivilegeOnIdentifiedData = isAuthorizedUser(identifiedPrivilegeList, false);
		}
		return hasPrivilegeOnIdentifiedData;
	}

	/**
	 * Updates the privilege list.
	 * @param sessionDataBean A data bean that contains information related to user logged in.
	 * @param cache QueryCsmCache object
	 * @param readPrivilegeList List that contains the read privileges
	 * @param identifiedPrivilegeList List of identified privileges
	 * @param cpIdList List of Collection Protocol/Clinical Study id's
	 * @throws SMException Security Manager Exception
	 */
	private void updatePrivilegeList(SessionDataBean sessionDataBean, QueryCsmCache cache,
			List<Boolean> readPrivilegeList, List<Boolean> identifiedPrivilegeList,
			List<String> cpIdList) throws SMException
	{
		Boolean hasPrivilegeOnIdentifiedData;
		Boolean isAuthorisedUser;
		String entityName;
		Long cpId = cpIdList.get(0) != null ? Long.parseLong(cpIdList.get(0)) : -1;
		entityName = Variables.mainProtocolObject;

		if (readPrivilegeList == null)
        {
              hasPrivilegeOnIdentifiedData = checkIdentifiedDataAccess(sessionDataBean, cache,
                          entityName, cpId);
              identifiedPrivilegeList.add(hasPrivilegeOnIdentifiedData);
        }
        else
        {
              isAuthorisedUser = checkReadDenied(sessionDataBean, cache, entityName, cpId);
              readPrivilegeList.add(isAuthorisedUser);

              //If user is authorized to read data then check for identified data access.
              if (isAuthorisedUser)
              {
                    hasPrivilegeOnIdentifiedData = checkIdentifiedDataAccess(sessionDataBean, cache,
                                entityName, cpId);
                    identifiedPrivilegeList.add(hasPrivilegeOnIdentifiedData);
              }
        }
	}

	/**
	 * @param sessionDataBean sessionDataBean
	 * @param cache cache
	 * @param entityName entityName
	 * @param cpId cpId
	 * @return
	 * @throws SMException SMException
	 */
	private Boolean checkReadDenied(SessionDataBean sessionDataBean, QueryCsmCache cache,
			String entityName, Long cpId) throws SMException
	{
		Boolean isAuthorisedUser;
		if (cache.isReadDenied(cpId) == null)
		{
			isAuthorisedUser = checkPermission(sessionDataBean, entityName, cpId,
			Permissions.READ_DENIED);
			cache.addNewObjectInReadPrivilegeMap(cpId, isAuthorisedUser);
		}
		else
		{
			isAuthorisedUser = cache.isReadDenied(cpId);
		}
		return isAuthorisedUser;
	}
	/**
	 * To retrieve the entity name.
	 * @param queryResultObjectData Object of QueryResultObjectData
	 * @return entityName Name of entity
	 */
	private String getEntityName(QueryResultObjectData queryResultObjectData)
	{
		String tableName = (String) AbstractClient.objectTableNames.get(queryResultObjectData
				.getAliasName());
		HibernateDAO hibernateDAO = null;
		try
		{
			hibernateDAO = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory(
					CommonServiceLocator.getInstance().getAppName()).getDAO();
		}
		catch (DAOException e)
		{
			e.printStackTrace();
		}
		HibernateMetaData hibernateMetaData = hibernateDAO.getHibernateMetaData();
		return hibernateMetaData.getClassName(tableName);
	}

	/**
	 * If a object say participant-1 is registered to CP-1, CP-2
	 * this method will check what are privileges of the
	 * user on both CPs and will return true if user is having privilege on any one CP.
	 * @param privilegeList List of privileges that a object id is having
	 * for every CP to which this object is registered.
	 * @param isReadDenied to specify if the user has read denied privilege
	 * @return <CODE>true</CODE> User is authorized,
	  * <CODE>false</CODE> otherwise
	 */
	private Boolean isAuthorizedUser(List<Boolean> privilegeList, boolean isReadDenied)
	{
		for (int i = 0; i < privilegeList.size(); i++)
		{
			Boolean isAuthorized = privilegeList.get(i);
			if (isReadDenied && !(isAuthorized))
			{
				return false;
			}
			else if (!(isReadDenied) && isAuthorized)
			{
				return true;
			}
		}
		
		return isReadDenied;
	}

	/**
	 * This method will internally call removeUnauthorizedFieldsData
	 * depending on the value of isAuthorisedUser and hasPrivilegeOnIdentifiedData.
	 * @param aList List of records
	 * @param isAuthorisedUser to specify if the user is authorized to view the results
	 * @param hasPrivilegeOnIdentifiedData to specify if the user has privilege to view
	 * identified data
	 * @param identifiedColumnIdentifiers List of identified column identifiers
	 * @param objectColumnIdentifiers List of object column identifiers
	 * @param isSimpleSearch to specify if the call is from Simple Search
	 */
	private void removeUnauthorizedData(List aList, Boolean isAuthorisedUser,
			Boolean hasPrivilegeOnIdentifiedData, List identifiedColumnIdentifiers,
			List objectColumnIdentifiers, boolean isSimpleSearch)
	{
		if (isAuthorisedUser)
		{
			//If user is not authorized to see identified data
			//then replace identified column values by ##
			if (!hasPrivilegeOnIdentifiedData)
			{
				removeUnauthorizedFieldsData(aList, identifiedColumnIdentifiers,
						objectColumnIdentifiers, true, isSimpleSearch);
			}
		}
		else
		{
			removeUnauthorizedFieldsData(aList, identifiedColumnIdentifiers,
					objectColumnIdentifiers, false, isSimpleSearch);
		}
	}

	/**
	 * Check if user is having Identified data access on a object id passed to method.
	 * And update cache accordingly.
	 * @param sessionDataBean A data bean that contains information related to user logged in.
	 * @param cache cache object that maintains information
	 * related to permissions of user on every CP object.
	 * @param entityName Name of entity for which identified data access is to be checked.
	 * @param entityId Entity id
	 * @throws SMException Security Manager Exception
	 * @return <CODE>true</CODE> User has identified access,
	  * <CODE>false</CODE> otherwise
	 */
	private Boolean checkIdentifiedDataAccess(SessionDataBean sessionDataBean, QueryCsmCache cache,
			String entityName, Long entityId) throws SMException
	{
		Boolean hasPrivilegeOnIdentifiedData;
		if (cache.isIdentifedDataAccess(entityId) == null)
		{
			hasPrivilegeOnIdentifiedData =
				((checkPermission(sessionDataBean, entityName, entityId,
						Permissions.PHI)) ||
				(checkPermission(sessionDataBean, entityName, entityId,
					    Permissions.REGISTRATION))
					);

			cache.addNewObjectInIdentifiedDataAccsessMap(entityId,
					hasPrivilegeOnIdentifiedData);
		}
		else
		{
			hasPrivilegeOnIdentifiedData = cache.isIdentifedDataAccess(entityId);
		}
		return hasPrivilegeOnIdentifiedData;
	}

	/**
	 * This method will fire a query on catissue database to get CP id's
	 * related to a entity Id passed to this method.
	 * @param entityName Entity name
	 * @param entityId entity id
	 * @return cpIdsList List of main protocol object id's
	 */
	private List<List<String>> getCpIdsListForGivenEntityId(String entityName, long entityId)
	{
		String sql = Variables.entityCPSqlMap.get(entityName);
		List<List<String>> cpIdsList = new ArrayList<List<String>>();
		if (entityName.equals(Variables.mainProtocolObject))
		{
			List<String> cpIdList = new ArrayList<String>();
			cpIdList.add(String.valueOf(entityId));
			cpIdsList.add(cpIdList);
		}
		else if (sql != null)
		{
			sql = sql + " ?";
			LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
			ColumnValueBean bean = new ColumnValueBean("entityId",Long.valueOf(entityId));
			columnValueBean.add(bean);
			try
			{
				cpIdsList = executeQuery(sql,columnValueBean);
			}
			catch (Exception e)
			{
				Logger.out.error("Error occured while getting CP ids for entity : " + entityName);
				e.printStackTrace();
			}
		}
		return cpIdsList;
	}
	
	public List<List<String>> getCpIdsListForGivenEntityIdList(String entityName, Set<String> entityIds)
	{
		String sql = Variables.entityCPSqlMap.get(entityName);
		List<List<String>> cpIdsList = new ArrayList<List<String>>();
		if (sql != null)
		{
			StringBuilder inClause = null;
			for(String entityId : entityIds)
			{				
				if(inClause == null){
					inClause = new StringBuilder(" " + entityId);
				} else {
					inClause.append(", ").append(entityId) ;
				}
			} 
			
			long t1 = System.currentTimeMillis();
			sql = sql.substring(0, sql.lastIndexOf("=")) + " in ( "+ inClause+ " )";			
			try
			{
				cpIdsList = executeQuery(sql, null);
			}
			catch (Exception e)
			{
				Logger.out.error("Error occured while getting CP ids for entity : " + entityName);
				e.printStackTrace();
			}
		}
		return cpIdsList;
	}

	/**
	 * @param entityName entity name
	 * @param entityId entity id
	 * @return the query
	 */
	public static String getQueryStringForCP(String entityName, int entityId)
	{
		StringBuffer queryString = new StringBuffer();
		String str;
		if (entityName == null || entityId == 0)
		{
			str = null;
		}
		else
		{
			queryString.append(Variables.entityCPSqlMap.get(entityName));
			queryString.append(entityId);
			str = queryString.toString();
		}
		return str;
	}

	/**
	 * This method will internally call checkPermission of SecurityManager
	 * and will return if a user is authorized user or not.
	 * @param sessionDataBean A data bean that contains information related to user logged in.
	 * @param entityName Entity name
	 * @param entityId Entity id
	 * @param permission Permission
	 * @throws SMException Security Manager Exception
	 * @return <CODE>true</CODE> If the user is authorized,
	  * <CODE>false</CODE> otherwise
	 */
	public Boolean checkPermission(SessionDataBean sessionDataBean, String entityName,
			Long entityId, String permission) throws SMException
	{
		// To get privilegeCache through
		// Singleton instance of PrivilegeManager, requires User LoginName
		PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
		PrivilegeCache privilegeCache = privilegeManager.getPrivilegeCache(sessionDataBean
				.getUserName());

		// Call to SecurityManager.checkPermission bypassed &
		// instead, call redirected to privilegeCache.hasPrivilege
		Boolean isAuthorisedUser = privilegeCache.hasPrivilege(entityName + "_" + entityId,
				permission);

		if (Permissions.READ_DENIED.equals(permission))
		{
			isAuthorisedUser = !isAuthorisedUser;
		}

		if (!isAuthorisedUser && validator != null)
		{
			isAuthorisedUser = validator.hasPrivilegeToView(sessionDataBean, entityId
					.toString(), permission);
		}
		return isAuthorisedUser;
	}

	/**
	 * To check for Authorization for Global Participants - not registered to any CP.
	 * @param sessionDataBean A data bean that contains information related to user logged in.
	 * @return <CODE>true</CODE> If the user is authorized,
	  * <CODE>false</CODE> otherwise
	 */
	private boolean checkPermissionOnGlobalParticipant(SessionDataBean sessionDataBean)
	{
		boolean isAuthorisedUser = false;
		validator = getValidatorInstance();
		if (validator != null)
		{
			isAuthorisedUser = validator.hasPrivilegeToViewGlobalParticipant(sessionDataBean);
		}
		return isAuthorisedUser;
	}

	/**
	 * Executes Query to get CP id's for given entity id on database.Results are added in List<List<String>>
	 * and this list is returned.
	 * @param sql The query
	 * @throws DAOException DAOException
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws SQLException SQLException
	 * @return aList List of records.
	 */
	private List<List<String>> executeQuery(String sql,LinkedList<ColumnValueBean> columnValueBean)
			throws DAOException, ClassNotFoundException, SQLException
	{
		List<List<String>> aList = new ArrayList<List<String>>();
		ResultSet resultSet = null;
		resultSet = jdbcDAO.getResultSet(sql, columnValueBean, null);
		if(resultSet != null)
		{
			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (resultSet.next())
			{
				int counter = 1;
				List<String> entityIdsList = new ArrayList<String>();
				while (counter <= columnCount)
				{
					entityIdsList.add(resultSet.getObject(counter).toString());
					counter++;
				}
				aList.add(entityIdsList);
			}
			jdbcDAO.closeStatement(resultSet);
		}
		return aList;
	}

	/**
	 * This method removes data from list aList.
	 * It could be all data related to QueryResultObjectDataBean
	 * or only the identified fields depending on
	 * the value of boolean removeOnlyIdentifiedData
	 * @author supriya_dankh
	 * @param aList List of records
	 * @param identifiedColumnIdentifiers List of identified column identifiers
	 * @param objectColumnIdentifiers List of object column identifiers
	 * @param removeOnlyIdentifiedData to specify if only identified data has to be removed
	 * @param isSimpleSearch to specify if its simple search
	 */
	private void removeUnauthorizedFieldsData(List aList, List identifiedColumnIdentifiers,
			List objectColumnIdentifiers, boolean removeOnlyIdentifiedData, boolean isSimpleSearch)
	{
		Vector objectColumnIds = new Vector();

		if (removeOnlyIdentifiedData)
		{
			objectColumnIds.addAll(identifiedColumnIdentifiers);
		}
		else
		{
			objectColumnIds.addAll(objectColumnIdentifiers);
		}
		Logger.out.debug("objectColumnIds:" + objectColumnIds);
		if (objectColumnIds != null)
		{
			addHashStringToList(aList, isSimpleSearch, objectColumnIds);
		}
	}

	/**
	 * @param aList list
	 * @param isSimpleSearch isSImpleSearch
	 * @param objectColumnIds object column identifiers
	 */
	private void addHashStringToList(List aList, boolean isSimpleSearch,
			Vector objectColumnIds)
	{
		for (int k = 0; k < objectColumnIds.size(); k++)
		{
			if (isSimpleSearch)
			{
				aList.set(((Integer) objectColumnIds.get(k)).intValue() - 1,
						edu.wustl.security.global.Constants.HASHED_OUT);
			}
			else
			{
				aList.set(((Integer) objectColumnIds.get(k)).intValue(),
						edu.wustl.security.global.Constants.HASHED_OUT);
			}
		}
	}

	/**
	 * Removed unauthorized data.
	 * @param aList List of records
	 * @param removeOnlyIdentifiedData to specify if only identified data has to be removed
	 * @param queryResultObjectDataBean Object of QueryResultObjectDataBean
	 */
	private void removeUnauthorizedFieldsData(List aList,boolean removeOnlyIdentifiedData,
			QueryResultObjectDataBean queryResultObjectDataBean)
	{
		Vector objectColumnIds = new Vector();
		boolean isAuthorizedUser = true;
		if (removeOnlyIdentifiedData)
		{
			objectColumnIds.addAll(queryResultObjectDataBean.getIdentifiedDataColumnIds());
		}
		else
		{
			objectColumnIds.addAll(queryResultObjectDataBean.getObjectColumnIds());
			isAuthorizedUser = false;
		}
		if (objectColumnIds != null)
		{
			for (int k = 0; k < objectColumnIds.size(); k++)
			{
				aList.set(((Integer) objectColumnIds.get(k)).intValue(),
						edu.wustl.query.util.global.AQConstants.HASHED_OUT);
			}
		}

		List tqColumnMetadataList = queryResultObjectDataBean
		.getTqColumnMetadataList();
		QueryCSMValidator queryCSMValidator = new QueryCSMValidator();
		//queryCSMValidator.hasPrivilegeToViewTemporalColumn(tqColumnMetadataList, aList, isAuthorizedUser);
	}
	/**
	 * This method is called from CacoreAppServiceDelegator.
	 * It checks whether the user is read denied or has access on PHI data.
	 * @param objName name of the object
	 * @param identifier identifier of the object
	 * @param sessionDataBean A data bean that contains information related to user logged in.
	 * @param cache CSM cache object for storing CP access data
	 * @throws SMException Security Manager Exception
	 * @return map having values for readDenied and phiAccess for each collection protocol
	 */
	public Map<String,Boolean> getAccessPrivilegeMap(String objName, Long identifier,
			SessionDataBean sessionDataBean,QueryCsmCache cache) throws SMException
	{
		Boolean isAuthorisedUser = true;
		Boolean hasPrivilegeOnIdentifiedData = true;
		Map<String,Boolean> accessprivilegeMap = new HashMap<String, Boolean>();
		List<List<String>> cpIdsList = getCpIdsListForGivenEntityId(objName, identifier.intValue());

		if(cpIdsList.isEmpty())
		{
			hasPrivilegeOnIdentifiedData = checkPermissionOnGlobalParticipant(sessionDataBean);
		}
		else
		{
			List<Boolean> readPrivilegeList = new ArrayList<Boolean>();
			List<Boolean> identifiedPrivilegeList = new ArrayList<Boolean>();

			for (int i = 0; i < cpIdsList.size(); i++)
			{
				List<String> cpIdList = cpIdsList.get(i);
				updatePrivilegeList(sessionDataBean, cache, readPrivilegeList,
						identifiedPrivilegeList, cpIdList);
			}
			isAuthorisedUser = isAuthorizedUser(readPrivilegeList, true);
			hasPrivilegeOnIdentifiedData = isAuthorizedUser(identifiedPrivilegeList,
					false);
		}
		accessprivilegeMap.put(AQConstants.IS_READ_DENIED, !isAuthorisedUser);
		accessprivilegeMap.put(AQConstants.HAS_PHI_ACCESS, hasPrivilegeOnIdentifiedData);

		return accessprivilegeMap;
	}
	/**
	 * This method gets Clinical Study ids from the datalist available. This is to improve the 
	 * performance when a scientist user exports data. Here we are saving DB calls which were being made to fetch CP id 
	 * for given main object.
	 * @param aList
	 * @param queryResultObjectDataBean
	 * @param finalMainEntityId
	 * @return
	 */
	public List<List<String>> getCSIdFromDataList(List aList, long finalMainEntityId,int cpIdIndex) {
		List<List<String>> cpIdsList = new ArrayList<List<String>>();
		String cpId = (String)aList.get(cpIdIndex);
		List<String> cpIdList = new ArrayList<String>();
		cpIdList.add(String.valueOf(cpId));
		cpIdList.add(String.valueOf(finalMainEntityId));
		cpIdsList.add(cpIdList);
		return cpIdsList;
	}
}