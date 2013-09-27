package edu.wustl.query.htmlprovider;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IParameter;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.EntityCacheFactory;

import junit.framework.TestCase;

public class GenerateHtmlTestCase extends TestCase
{
	public void testGenerateHTMLForRadioButton()
	{
		List<String> values = new ArrayList<String>();
		GenerateHtml.generateHTMLForRadioButton("isAvailable13761", null);
		values.add(null);
		GenerateHtml.generateHTMLForRadioButton("isAvailable13761", values);
		values = new ArrayList<String>();
		values.add("false");
		GenerateHtml.generateHTMLForRadioButton("isAvailable13761", values);
		values = new ArrayList<String>();
		values.add("true");
		GenerateHtml.generateHTMLForRadioButton("isAvailable13761", values);
		values = new ArrayList<String>();
		values.add("other");
		GenerateHtml.generateHTMLForRadioButton("isAvailable13761", values);
	}

	public void testGenerateCheckBox()
	{
		GenerateHtml.generateCheckBox("isAvailable13761", true);
	}

	public void testGetTags()
	{
		GenerateHtml.getTags(new StringBuffer());
	}

	public void testGetHtmlHeader()
	{
		String attributeCollection = ";activityStatus93;birthDate94;deathDate95;empiId33071;empiIdStatus33072;ethnicity96;firstName97;gender98;id99;lastName100;metaPhoneCode33073;middleName101;sexGenotype102;socialSecurityNumber103;vitalStatus104";
		GenerateHtml.getHtmlHeader("Participant", "91", attributeCollection, false);
		GenerateHtml.getHtmlHeader("Participant", "91", attributeCollection, true);

		GenerateHtml.generatePreHtml(attributeCollection, "edu.wustl.catissuecore.domain.Participant", false);
		GenerateHtml.generatePreHtml(attributeCollection, "edu.wustl.catissuecore.domain.Participant", true);
	}

	public void testGetHtmlAddEditPage()
	{
		GenerateHtml.getHtmlAddEditPage(AQConstants.ADD_EDIT_PAGE, new StringBuffer());
	}

	public void testGetBoldLabel()
	{
		GenerateHtml.getBoldLabel("Label");
	}

	public void testGetAlternateCss()
	{
		GenerateHtml.getAlternateCss(new StringBuffer(), false, "isAvailable13761");
		GenerateHtml.getAlternateCss(new StringBuffer(), true, "isAvailable13761");
	}

	public void testCheckBetweenOperator()
	{
		GenerateHtml.checkBetweenOperator("Between");
	}

	public void testGenerateHTMLForOperator()
	{
		List<String> operatorList = new ArrayList<String>();
		operatorList.add("Equals");
		operatorList.add("Not Equals");
		operatorList.add("Less than");
		operatorList.add("Less than or Equal to");
		operatorList.add("Greater than");
		operatorList.add("Greater than or Equal to");
		GenerateHtml.generateHTMLForOperator("comp0", "Not Equals", operatorList, true);
	}

	public void testGenerateHTMLForTextBox()
	{
		AttributeDetails attributeDetails = new AttributeDetails();
		attributeDetails.setSelectedOperator(RelationalOperator.IsNotNull.toString());
		List<IParameter<?>> parameterList = attributeDetails.getParameterList();
		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
		AttributeInterface attribute = GenericQueryGeneratorMock.findAttribute(participantEntity, "birthDate");
		ICondition participantCondition = GenericQueryGeneratorMock.createParticipantCondition(participantEntity);
		List<ICondition> conditions = new ArrayList<ICondition>();
		conditions.add(participantCondition);
		attributeDetails.setAttrName(attribute.getName());
		attributeDetails.setConditions(conditions);
		attributeDetails.setDataType(attribute.getDataType());
		ICondition condition  = null;
		attributeDetails.setAttributeNameConditionMap(HtmlUtility.getMapOfConditions(conditions));
		if(attributeDetails.getAttributeNameConditionMap()!=null)
		{
			condition = attributeDetails.getAttributeNameConditionMap().
							get(attributeDetails.getAttrName());
		}
		attributeDetails.setEditValues(null);
		attributeDetails.setSelectedOperator(null);
		if(condition != null)
		{
			attributeDetails.setEditValues(condition.getValues());
			attributeDetails.setSelectedOperator
				(condition.getRelationalOperator().getStringRepresentation());
		}
		List<String> operatorList = new ArrayList<String>();
		operatorList.add("Between");
		operatorList.add("Greater than");
		operatorList.add("Equals");
		operatorList.add("Greater than or Equal to");
		operatorList.add("Is Not Null");
		operatorList.add("Is Null");
		operatorList.add("Not Equals");
		operatorList.add("Less than or Equal to");
		attributeDetails.setOperatorsList(operatorList);
		GenerateHtml.generateHTMLForTextBox("birthDate94", attributeDetails);
		GenerateHtml.generateHTMLForOperators("birthDate94", true, attributeDetails);

		/**
		 * For condition : first name in (...)
		 */
		attribute = GenericQueryGeneratorMock.findAttribute(participantEntity, "firstName");
		participantCondition = GenericQueryGeneratorMock.createParticipantConditionForIn(participantEntity);
		conditions = new ArrayList<ICondition>();
		conditions.add(participantCondition);
		attributeDetails.setConditions(conditions);
		attributeDetails.setAttributeNameConditionMap(HtmlUtility.getMapOfConditions(conditions));
		if(attributeDetails.getAttributeNameConditionMap()!=null)
		{
			condition = attributeDetails.getAttributeNameConditionMap().
							get(attributeDetails.getAttrName());
		}
		attributeDetails.setEditValues(null);
		attributeDetails.setSelectedOperator(null);
		if(condition != null)
		{
			attributeDetails.setEditValues(condition.getValues());
			attributeDetails.setSelectedOperator
				(condition.getRelationalOperator().getStringRepresentation());
		}
		GenerateHtml.generateHTMLForTextBox("firstName99", attributeDetails);

		/**
		 * For condition : firstName is not null.
		 */
		attribute = GenericQueryGeneratorMock.findAttribute(participantEntity, "firstName");
		participantCondition = GenericQueryGeneratorMock.createParticipantConditionForNotNull(participantEntity);
		conditions = new ArrayList<ICondition>();
		conditions.add(participantCondition);
		attributeDetails.setConditions(conditions);
		attributeDetails.setAttributeNameConditionMap(HtmlUtility.getMapOfConditions(conditions));
		if(attributeDetails.getAttributeNameConditionMap()!=null)
		{
			condition = attributeDetails.getAttributeNameConditionMap().
							get(attributeDetails.getAttrName());
		}
		attributeDetails.setEditValues(null);
		attributeDetails.setSelectedOperator(null);
		if(condition != null)
		{
			attributeDetails.setEditValues(condition.getValues());
			attributeDetails.setSelectedOperator
				(condition.getRelationalOperator().getStringRepresentation());
		}
		GenerateHtml.generateHTMLForTextBox("firstName99", attributeDetails);
	}
}
