package edu.wustl.common.query.queryobject.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import junit.framework.TestCase;

public class AssociationDataHandlerTestCase extends TestCase
{
	public void testUpdateRowDataList()
	{
		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
        IOutputEntity participantOutputEntity = QueryObjectFactory.createOutputEntity(participantEntity);
        participantOutputEntity.getSelectedAttributes().addAll(participantEntity.getEntityAttributesForQuery());

        EntityInterface pmiEntity = GenericQueryGeneratorMock.createEntity("ParticipantMedicalIdentifier");
        pmiEntity = GenericQueryGeneratorMock.getEntity(cache, pmiEntity);
        IOutputEntity pmiOutputEntity = QueryObjectFactory.createOutputEntity(pmiEntity);
        pmiOutputEntity.getSelectedAttributes().addAll(pmiEntity.getEntityAttributesForQuery());

        AttributeInterface lastName = GenericQueryGeneratorMock.findAttribute(participantEntity,"lastName");
        AttributeInterface pmiId = GenericQueryGeneratorMock.findAttribute(pmiEntity,"id");
        AttributeInterface mrnNo = GenericQueryGeneratorMock.findAttribute(pmiEntity,"medicalRecordNumber");
        AttributeInterface participantId = GenericQueryGeneratorMock.findAttribute(participantEntity,"id");
        List<AttributeInterface> partAttributeList = new ArrayList<AttributeInterface>();
        List<AttributeInterface> pmiAttributeList = new ArrayList<AttributeInterface>();
        partAttributeList.add(participantId);
        partAttributeList.add(lastName);
        pmiAttributeList.add(pmiId);
        pmiAttributeList.add(mrnNo);

        IExpression partExp = GenericQueryGeneratorMock.createExpression(participantEntity);
        IExpression pmiExp = GenericQueryGeneratorMock.createExpression(pmiEntity);

        List<Map<OutputAssociationColumn,Object>> denormalizedLst =
        	new ArrayList<Map<OutputAssociationColumn,Object>>();
        Map<OutputAssociationColumn,Object> denormMap = new HashMap<OutputAssociationColumn, Object>();

        OutputAssociationColumn opAssocColumn = new OutputAssociationColumn(participantId, partExp, null);
        OutputAttributeColumn opAttrColumn = new OutputAttributeColumn("1", 0, participantId, partExp, null);
        denormMap.put(opAssocColumn, opAttrColumn);

        opAssocColumn = new OutputAssociationColumn(lastName, partExp, null);
        opAttrColumn = new OutputAttributeColumn("abc", 1, lastName, partExp, null);
        denormMap.put(opAssocColumn, opAttrColumn);
        denormalizedLst.add(denormMap);
        QueryExportDataHandler dataHandler = new QueryExportDataHandler(partExp, null);

        AssociationDataHandler assocDataHandler = new AssociationDataHandler();
        assocDataHandler.updateRowDataList(denormalizedLst, partExp, dataHandler);
	}
}
