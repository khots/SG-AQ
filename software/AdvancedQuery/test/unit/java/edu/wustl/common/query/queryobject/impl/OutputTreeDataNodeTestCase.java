package edu.wustl.common.query.queryobject.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.querysuite.EntityCacheFactory;

public class OutputTreeDataNodeTestCase extends TestCase
{
	public void test()
	{
		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
        IOutputEntity outputEntity = QueryObjectFactory.createOutputEntity(participantEntity);
        outputEntity.getSelectedAttributes().addAll(participantEntity.getEntityAttributesForQuery());
        OutputTreeDataNode outputTreeDataNode = new OutputTreeDataNode(outputEntity, 1, 0);
        List<QueryOutputTreeAttributeMetadata> selectedAttributeMetaDataList = new ArrayList<QueryOutputTreeAttributeMetadata>();
        int i=0;
        for(AttributeInterface attribute : participantEntity.getAllAttributes())
        {
	        String className = edu.wustl.query.util.global.Utility.parseClassName(participantEntity.getName());
	        String attributeLabel = edu.wustl.common.util.Utility.getDisplayLabel(attribute.getName());
	        String displayNmForCol = className+" : "+attributeLabel;
	        QueryOutputTreeAttributeMetadata opTreeMetadata = new QueryOutputTreeAttributeMetadata(attribute, "Column"+i, outputTreeDataNode,
	                displayNmForCol);
	        selectedAttributeMetaDataList.add(opTreeMetadata);
	        outputTreeDataNode.addAttribute(opTreeMetadata);
	        i++;
        }
        OutputTreeDataNode treeDataNode = new OutputTreeDataNode(outputEntity, 1, 1);
        treeDataNode.setAttributes(selectedAttributeMetaDataList);
        AttributeInterface attribute = GenericQueryGeneratorMock.findAttribute(participantEntity, "lastName");
        treeDataNode.getAttributeMetadata(attribute);
	}
}
