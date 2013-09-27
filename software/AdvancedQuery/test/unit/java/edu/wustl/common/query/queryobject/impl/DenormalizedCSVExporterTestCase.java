package edu.wustl.common.query.queryobject.impl;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.querysuite.EntityCacheFactory;

import junit.framework.TestCase;

public class DenormalizedCSVExporterTestCase extends TestCase
{
	public void testGetFinalDataList()
	{
		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
        IOutputEntity participantOutputEntity = QueryObjectFactory.createOutputEntity(participantEntity);
        participantOutputEntity.getSelectedAttributes().addAll(participantEntity.getEntityAttributesForQuery());
        IExpression partExp = GenericQueryGeneratorMock.createExpression(participantEntity);

        EntityInterface pmiEntity = GenericQueryGeneratorMock.createEntity("ParticipantMedicalIdentifier");
        pmiEntity = GenericQueryGeneratorMock.getEntity(cache, pmiEntity);
        IOutputEntity pmiOutputEntity = QueryObjectFactory.createOutputEntity(pmiEntity);
        pmiOutputEntity.getSelectedAttributes().addAll(pmiEntity.getEntityAttributesForQuery());
        IExpression pmiExp = GenericQueryGeneratorMock.createExpression(pmiEntity);

		AttributeInterface lastName = GenericQueryGeneratorMock.findAttribute(participantEntity,"lastName");
        AttributeInterface pmiId = GenericQueryGeneratorMock.findAttribute(pmiEntity,"id");
        AttributeInterface mrnNo = GenericQueryGeneratorMock.findAttribute(pmiEntity,"medicalRecordNumber");
        AttributeInterface participantId = GenericQueryGeneratorMock.findAttribute(participantEntity,"id");
		List<Object> resultList = new ArrayList<Object>();
		OutputAttributeColumn data = new OutputAttributeColumn("1", 0, participantId, partExp, null);
		resultList.add(data);
		data = new OutputAttributeColumn("abc", 1, lastName, partExp, null);
		resultList.add(data);

		data = new OutputAttributeColumn("1", 2, pmiId, pmiExp, null);
		resultList.add(data);
		data = new OutputAttributeColumn("2", 2, pmiId, pmiExp, null);
		resultList.add(data);

		data = new OutputAttributeColumn("mrn_1", 3, mrnNo, pmiExp, null);
		resultList.add(data);
		data = new OutputAttributeColumn("mrn_2", 2, pmiId, pmiExp, null);
		resultList.add(data);

		QueryExportDataHandler dataHandler = new QueryExportDataHandler(partExp, null);
		QueryHeaderData queryHeaderData = new QueryHeaderData(participantEntity, partExp);
		dataHandler.entityVsMaxCount.put(queryHeaderData, 1);
		queryHeaderData = new QueryHeaderData(pmiEntity, pmiExp);
		dataHandler.entityVsMaxCount.put(queryHeaderData, 2);

		DenormalizedCSVExporter csvExporter = new DenormalizedCSVExporter();
		csvExporter.setHeaderList(new ArrayList<String>());
		csvExporter.getFinalDataList(resultList, dataHandler);
	}
}
