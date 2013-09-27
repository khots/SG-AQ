package edu.wustl.query.bizlogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import edu.wustl.security.privilege.PrivilegeType;

public class CommonQueryBizLogicTestCase extends TestCase
{
	public void testInsertQuery() throws DAOException
	{
		CommonQueryBizLogic queryBizLogic = new CommonQueryBizLogic();
		SessionDataBean sessionData = new SessionDataBean();
		sessionData.setCsmUserId("1");
		sessionData.setIpAddress("10.88.199.11");
		sessionData.setUserId(1l);
		sessionData.setUserName("admin@admin.com");
		String sqlQuery = "select distinct Participant_1.ETHNICITY Column0, Participant_1.LAST_NAME Column1, Participant_1.BIRTH_DATE Column2, Participant_1.IDENTIFIER Column3, Participant_1.VITAL_STATUS Column4, Participant_1.FIRST_NAME Column5, Participant_1.MIDDLE_NAME Column6, Participant_1.GENDER Column7, Participant_1.SOCIAL_SECURITY_NUMBER Column8, Participant_1.ACTIVITY_STATUS Column9, Participant_1.DEATH_DATE Column10, Participant_1.GENOTYPE Column11  from (select * from CATISSUE_PARTICIPANT where ACTIVITY_STATUS != 'Disabled') Participant_1  where Participant_1.IDENTIFIER is NOT NULL";
		queryBizLogic.insertQuery(sqlQuery, sessionData);
	}

	public void testExecute() throws DAOException
	{
		int startIndex = 0;
		SessionDataBean sessionData = new SessionDataBean();
		sessionData.setCsmUserId("1");
		sessionData.setIpAddress("10.88.199.11");
		sessionData.setUserId(1l);
		sessionData.setUserName("admin@admin.com");
		sessionData.setSecurityRequired(false);
		sessionData.setAdmin(true);

		QuerySessionData querySessionData = new QuerySessionData();
		querySessionData.setHasConditionOnIdentifiedField(false);
		querySessionData.setSecureExecute(false);
		querySessionData.setSql("select distinct Column0,Column1,Column2,Column3,Column4,Column5,Column6,Column7,Column8,Column9,Column10,Column11,Column12,Column13,Column14 from TEMP_OUTPUTTREE2441_6358 where Column0 is NOT NULL");
		querySessionData.setRecordsPerPage(100);
		querySessionData.setTotalNumberOfRecords(0);

		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);

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
        queryResultObjectDataBean.setMainEntityIdentifierColumnId(0);
        queryResultObjectDataBean.setPrivilegeType(PrivilegeType.ObjectLevel);
        queryResultObjectDataBean.setTqColumnMetadataList(new ArrayList());
        List<Integer> identifiedDataColumnIds = new ArrayList<Integer>();
        identifiedDataColumnIds.add(1);
        identifiedDataColumnIds.add(3);
        identifiedDataColumnIds.add(4);
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

        Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap = new HashMap<Long, QueryResultObjectDataBean>();
        queryResultObjectDataBeanMap.put(5l, queryResultObjectDataBean);
        querySessionData.setQueryResultObjectDataMap(queryResultObjectDataBeanMap);
        CommonQueryBizLogic queryBizLogic = new CommonQueryBizLogic();
        queryBizLogic.execute(sessionData, querySessionData, startIndex);
	}
}
