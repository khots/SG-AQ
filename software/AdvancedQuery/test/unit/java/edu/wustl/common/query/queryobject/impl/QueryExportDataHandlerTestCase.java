package edu.wustl.common.query.queryobject.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import junit.framework.TestCase;

public class QueryExportDataHandlerTestCase extends TestCase
{
	public void testUpdateRowDataList()
	{
		Map<BaseAbstractAttributeInterface,Object> denormalizationMap =
			new HashMap<BaseAbstractAttributeInterface, Object>();
		Map<BaseAbstractAttributeInterface,Object> innerMap =
			new HashMap<BaseAbstractAttributeInterface, Object>();
		List<Map<BaseAbstractAttributeInterface,Object>> innerMapList =
			new ArrayList<Map<BaseAbstractAttributeInterface,Object>>();
		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
        AttributeInterface lastName = GenericQueryGeneratorMock.findAttribute(participantEntity, "lastName");
        AttributeInterface ethnicity = GenericQueryGeneratorMock.findAttribute(participantEntity, "ethnicity");
        AttributeInterface firstName = GenericQueryGeneratorMock.findAttribute(participantEntity, "firstName");

        IExpression partExp = GenericQueryGeneratorMock.createExpression(participantEntity);
        List ethnicities = new ArrayList();
        Map<BaseAbstractAttributeInterface,Object> tempMap =
        	new HashMap<BaseAbstractAttributeInterface, Object>();
        tempMap.put(ethnicity, new OutputAttributeColumn("eth1", 0, ethnicity, partExp, null));
        tempMap.put(lastName, new OutputAttributeColumn("eth1", 1, lastName, partExp, null));
        tempMap.put(firstName, new OutputAttributeColumn("eth1", 2, firstName,partExp, null));
        ethnicities.add(tempMap);

        EntityInterface pmiEntity = GenericQueryGeneratorMock.createEntity("ParticipantMedicalIdentifier");
        pmiEntity = GenericQueryGeneratorMock.getEntity(cache, pmiEntity);
        IExpression pmiExp = GenericQueryGeneratorMock.createExpression(pmiEntity);
        AttributeInterface mrnNo = GenericQueryGeneratorMock.findAttribute(pmiEntity, "medicalRecordNumber");
        AttributeInterface id = GenericQueryGeneratorMock.findAttribute(pmiEntity, "id");

        AssociationInterface association = GenericQueryGeneratorMock.getAssociation(participantEntity, pmiEntity);
        innerMap.put(id, new OutputAttributeColumn("1", 0, id,pmiExp, null));
        innerMap.put(mrnNo, new OutputAttributeColumn("mrn1", 1, mrnNo,pmiExp, null));
        innerMapList.add(innerMap);

        innerMap =new HashMap<BaseAbstractAttributeInterface, Object>();
        innerMap.put(id, new OutputAttributeColumn("2", 0, id,pmiExp, null));
        innerMap.put(mrnNo, new OutputAttributeColumn("mrn2", 1, mrnNo,pmiExp, null));
        innerMapList.add(innerMap);

        denormalizationMap.put(lastName, new OutputAttributeColumn("abc", 1, lastName,partExp, null));
        denormalizationMap.put(ethnicity, ethnicities);
        denormalizationMap.put(association, innerMapList);
        IQuery query = GenericQueryGeneratorMock.createParticipantPMIQuery();
        QueryExportDataHandler dataHandler = new QueryExportDataHandler(partExp,query.getConstraints());
        /*dataHandler.updateRowDataList(denormalizationMap, 0);
        dataHandler.getDataList(0);*/
        dataHandler.getRootExp();
       // DenormalizedCSVExporter csvExporter = new DenormalizedCSVExporter();
        //csvExporter.prepareDataList(1, dataHandler);
        dataHandler.getExpVsAssoc();
        dataHandler.getFinalAttributeList(participantEntity);
        dataHandler.getTgtExpVsAssoc();
        //dataHandler.getHeaderList();
	}
}
