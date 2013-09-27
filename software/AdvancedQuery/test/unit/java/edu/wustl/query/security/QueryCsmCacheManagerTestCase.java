package edu.wustl.query.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.QueryResultObjectData;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.global.AbstractClient;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.global.Variables;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.privilege.PrivilegeType;
import junit.framework.TestCase;

public class QueryCsmCacheManagerTestCase extends TestCase
{
	public void testFilterRow() throws DAOException, SMException
	{
		String appName=CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO jdbcDAO= daofactory.getJDBCDAO();
		jdbcDAO.openSession(null);
		QueryCsmCacheManager cacheManager = new QueryCsmCacheManager(jdbcDAO);
		QueryCsmCache cache = cacheManager.getNewCsmCacheObject();

		SessionDataBean sessionData = new SessionDataBean();
		sessionData.setCsmUserId("1");
		sessionData.setIpAddress("10.88.199.11");
		sessionData.setUserId(1l);
		sessionData.setUserName("admin@admin.com");

		EntityCache entityCache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(entityCache, participantEntity);

        QueryResultObjectDataBean queryResultObjectDataBean = new QueryResultObjectDataBean();
        queryResultObjectDataBean.setEntity(participantEntity);
        queryResultObjectDataBean.setCsmEntityName(participantEntity.getName());
        queryResultObjectDataBean.setEntityId(0);
        queryResultObjectDataBean.setEntityIdIndexMap(new HashMap<EntityInterface, Integer>());
        queryResultObjectDataBean.setFileTypeAtrributeIndexMetadataMap(new HashMap<Integer, Object>());
        queryResultObjectDataBean.setHasAssociatedIdentifiedData(true);
        queryResultObjectDataBean.setClobeType(false);
        queryResultObjectDataBean.setReadDeniedObject(true);
        queryResultObjectDataBean.setMainEntity(participantEntity);
        queryResultObjectDataBean.setMainEntityIdentifierColumnId(10);
        queryResultObjectDataBean.setPrivilegeType(PrivilegeType.ObjectLevel);
        queryResultObjectDataBean.setTqColumnMetadataList(new ArrayList());
        List<Integer> identifiedDataColumnIds = new ArrayList<Integer>();
        identifiedDataColumnIds.add(1);
        identifiedDataColumnIds.add(3);
        identifiedDataColumnIds.add(5);
        identifiedDataColumnIds.add(6);
        identifiedDataColumnIds.add(12);
        identifiedDataColumnIds.add(14);
        queryResultObjectDataBean.setIdentifiedDataColumnIds(identifiedDataColumnIds);

        List<Integer> objectColumnIds = new ArrayList<Integer>();
        objectColumnIds.add(0);
        objectColumnIds.add(1);
        objectColumnIds.add(2);
        objectColumnIds.add(3);
        objectColumnIds.add(4);
        objectColumnIds.add(5);
        objectColumnIds.add(6);
        objectColumnIds.add(7);
        objectColumnIds.add(8);
        objectColumnIds.add(9);
        objectColumnIds.add(10);
        objectColumnIds.add(11);
        objectColumnIds.add(12);
        objectColumnIds.add(13);
        objectColumnIds.add(14);
        queryResultObjectDataBean.setObjectColumnIds(objectColumnIds);

        Map<EntityInterface,List<List<String>>> entityMainIdListMap = new HashMap<EntityInterface, List<List<String>>>();
        List<List<String>> mainEntityIds = new ArrayList<List<String>>();
        List<String> mainEntityList = new ArrayList<String>();
        mainEntityList.add("1");
        mainEntityIds.add(mainEntityList);
        entityMainIdListMap.put(participantEntity, mainEntityIds);

        Map<String, QueryResultObjectDataBean> queryResultObjectDataBeanMap = new HashMap<String, QueryResultObjectDataBean>();
        queryResultObjectDataBeanMap.put("51", queryResultObjectDataBean);

        List aList = new ArrayList();
        aList.add("Active");
        aList.add("Tavase");
        aList.add("Unknown");
        aList.add("Pooja");
        aList.add("Female Gender");
        aList.add("10-Jan-86");
        aList.add("");
        aList.add("TFS");
        aList.add("Alive");
        aList.add("");
        aList.add("1");
        aList.add("");
        aList.add("");
        aList.add("Unknown");
        aList.add("Aravind");

        cacheManager.filterRow(sessionData, queryResultObjectDataBeanMap, aList, cache);
        cacheManager.hasPrivilegeOnIdentifiedData(sessionData, queryResultObjectDataBeanMap, aList, cache);

        QueryResultObjectData queryResultObjectData = new QueryResultObjectData();
        queryResultObjectData.setAliasName("Participant");
        queryResultObjectData.setIdentifiedDataColumnIds(identifiedDataColumnIds);
        queryResultObjectData.setIdentifierColumnId(10);
        queryResultObjectData.setDependentColumnIds(new ArrayList());
        queryResultObjectData.setRelatedQueryResultObjects(new ArrayList());
        Map queryResultObjectDataMap = new HashMap();
        queryResultObjectDataMap.put("Participant", queryResultObjectData);
        AbstractClient.objectTableNames.put("Participant", "CATISSUE_PARTICIPANT");
        cacheManager.filterRowForSimpleSearch(sessionData, queryResultObjectDataMap, aList, cache);
        cacheManager.hasPrivilegeOnIdentifiedDataForSimpleSearch(sessionData, queryResultObjectDataMap, aList, cache);

        queryResultObjectDataBeanMap = new HashMap<String, QueryResultObjectDataBean>();
        queryResultObjectDataBeanMap.put("51", queryResultObjectDataBean);
        cacheManager.filterRow(sessionData, queryResultObjectDataBeanMap, aList, cache);

        cache.addNewObjectInReadPrivilegeMap(Long.valueOf(2), true);
        Boolean isReadDenied = cache.isReadDenied(Long.valueOf(2));
        assertEquals("isReadDenied",Boolean.valueOf(true),isReadDenied);
        cache.removeObjectFromReadPrivilegeMap(Long.valueOf(2));

        cache.addNewObjectInIdentifiedDataAccsessMap(Long.valueOf(2), true);
        Boolean hasHidentifiedAccess = cache.isIdentifedDataAccess(Long.valueOf(2));
        assertEquals("hasHidentifiedAccess",Boolean.valueOf(true),hasHidentifiedAccess);
        cache.removeObjectFromIdentifiedDataAccsessMap(Long.valueOf(2));

        Variables.entityCPSqlMap.put(participantEntity.getName(), "Participant");
        cacheManager.getQueryStringForCP(participantEntity.getName(), 91);

        cacheManager.getValidator();
        Variables.mainProtocolObject = "ClinicalStudy";
        cacheManager.getAccessPrivilegeMap("ClinicalStudy", Long.valueOf(1), sessionData, cache);
        cacheManager.getAccessPrivilegeMap("Participant", Long.valueOf(1), sessionData, cache);

	}
}
