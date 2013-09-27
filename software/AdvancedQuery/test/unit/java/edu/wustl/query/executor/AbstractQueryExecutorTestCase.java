package edu.wustl.query.executor;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.global.Utility;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.privilege.PrivilegeType;
import junit.framework.TestCase;

public class AbstractQueryExecutorTestCase extends TestCase
{
	public void testGetQueryResultList() throws DAOException, SMException
	{
		String query = "select distinct Column8 , Column10, Column1, Column9, Column6 from TEMP_OUTPUTTREE2441_6358 where Column10  is not null";
		Connection connection = null;
		SessionDataBean sessionData = new SessionDataBean();
		sessionData.setCsmUserId("1");
		sessionData.setIpAddress("10.88.199.11");
		sessionData.setUserId(1l);
		sessionData.setUserName("admin@admin.com");
		sessionData.setSecurityRequired(false);
		sessionData.setAdmin(true);

		boolean isSecureExecute = true;
		boolean hasConditionOnIdentifiedField = false;
		Map queryResultObjectDataMap = new HashMap();
		int startIndex = 0;
		int noOfRecords = 200;

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
        queryResultObjectDataMap.put("51", queryResultObjectDataBean);
        AbstractQueryExecutor queryExecutor = Utility.getQueryExecutor();
        queryExecutor.getQueryResultList(query, connection, sessionData, isSecureExecute, hasConditionOnIdentifiedField, queryResultObjectDataMap, startIndex, noOfRecords);
	}
}
