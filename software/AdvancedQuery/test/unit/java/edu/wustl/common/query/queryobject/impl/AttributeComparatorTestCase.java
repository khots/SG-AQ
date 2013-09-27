package edu.wustl.common.query.queryobject.impl;

import java.util.List;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.emory.mathcs.backport.java.util.Collections;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import junit.framework.TestCase;

public class AttributeComparatorTestCase extends TestCase
{
	public void testCompare()
	{
		EntityCache cache = EntityCacheFactory.getInstance();
		EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
        List<AbstractAttributeInterface> attributeList = (List<AbstractAttributeInterface>)participantEntity.getAllAbstractAttributes();
        Collections.sort(attributeList, new AttributeComparator());
	}
}
