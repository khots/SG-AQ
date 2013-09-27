package edu.wustl.query.htmlprovider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IParameter;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import junit.framework.TestCase;

public class HtmlProviderTestCase extends TestCase
{
	/**
	 * Test the HTML generated.
	 */
	public void testGenerateHtml()
	{
		try
		{
			HtmlProvider htmlProvider = new HtmlProvider(null);
			EntityCache cache = EntityCacheFactory.getInstance();
            EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
            participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
			htmlProvider.generateHTML(participantEntity,null);
			assertTrue("Html generated successfully for Demographics.",true);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testGetHtmlForSavedQuery()
	{
		HtmlProvider htmlProvider = new HtmlProvider(null);
		Map<Integer, Map<EntityInterface, List<ICondition>>>  expressionMap = new HashMap<Integer, Map<EntityInterface,List<ICondition>>>();
		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
		ICondition condition = GenericQueryGeneratorMock.createParticipantCondition(participantEntity);
		List<ICondition> conditions = new ArrayList<ICondition>();
		conditions.add(condition);
		Map<EntityInterface, List<ICondition>> conditionMap = new HashMap<EntityInterface, List<ICondition>>();
		conditionMap.put(participantEntity, conditions);
		expressionMap.put(1, conditionMap);
		List<IParameter<?>> parameterList = new ArrayList<IParameter<?>>();
		htmlProvider.getHtmlForSavedQuery(expressionMap, false, "Save Query Page", parameterList);
		assertTrue("Html generated successfully for Demographics.",true);
		GenerateHtml.getEntityExpressionIdListMap(expressionMap);
	}
}
