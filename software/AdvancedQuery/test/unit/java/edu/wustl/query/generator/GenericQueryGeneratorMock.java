/**
 *
 */

package edu.wustl.query.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.databaseproperties.TableProperties;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ColumnPropertiesInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.common.beans.MatchedClass;
import edu.wustl.cab2b.common.beans.MatchedClassEntry;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.querysuite.exceptions.CyclicException;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.ArithmeticOperator;
import edu.wustl.common.querysuite.queryobject.IArithmeticOperand;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConnector;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IDateOffsetLiteral;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionAttribute;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IQueryEntity;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.ITerm;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.TimeInterval;
import edu.wustl.common.querysuite.queryobject.impl.Expression;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.EntityCacheFactory;

/**
 * @author prafull_kadam
 * To create query on Dummy entity data.
 * It does not use Entity Manager, Test queries on dummy Entity.
 * Specifically designed to create entities & query objects of each data type with possible operators.

 */
public class GenericQueryGeneratorMock extends EntityManager
{
	private static GenericQueryGeneratorMock entityManager = new GenericQueryGeneratorMock();
	private static DomainObjectFactory factory = DomainObjectFactory.getInstance();

	/**
	 * To create one dummy entity.
	 * @param name Name of the Entity.
	 * @return The entity.
	 */
	public static EntityInterface createEntity(String name)
	{
		EntityInterface e = factory.createEntity();
		e.setName(name);
		e.setCreatedDate(new Date());
		e.setDescription("This is a Dummy entity");
		e.setId(1L);
		e.setLastUpdated(new Date());

		((Entity) e).setAbstractAttributeCollection(getAttributes(e));

		TableProperties tableProperties = new TableProperties();
		tableProperties.setName("catissue_temp");
		tableProperties.setId(1L);
		((Entity) e).setTableProperties(tableProperties);
		return e;
	}

	/**
	 * TO create attribute list, which contains all types of attributes.
	 * @param entity the entity to which all attributes belongs.
	 * @return list of attributes.
	 */
	public static ArrayList<AbstractAttributeInterface> getAttributes(EntityInterface entity)
	{
		ArrayList<AbstractAttributeInterface> attributes = new ArrayList<AbstractAttributeInterface>();

		AttributeInterface att1 = factory.createIntegerAttribute();
		att1.setName("long");
		ColumnPropertiesInterface c1 = factory.createColumnProperties();
		c1.setName("LONG_ATTRIBUTE");
		((Attribute) att1).setColumnProperties(c1);
		att1.setIsPrimaryKey(true);
		att1.setEntity(entity);

		AttributeInterface att2 = factory.createDateAttribute();
		att2.setName("date");
		ColumnPropertiesInterface c2 = factory.createColumnProperties();
		c2.setName("DATE_ATTRIBUTE");
		((Attribute) att2).setColumnProperties(c2);
		att2.setEntity(entity);

		AttributeInterface att3 = factory.createLongAttribute();
		att3.setName("int");
		ColumnPropertiesInterface c3 = factory.createColumnProperties();
		c3.setName("INT_ATTRIBUTE");
		((Attribute) att3).setColumnProperties(c3);
		att3.setEntity(entity);

		AttributeInterface att4 = factory.createStringAttribute();
		att4.setName("string");
		ColumnPropertiesInterface c4 = factory.createColumnProperties();
		c4.setName("STRING_ATTRIBUTE");
		((Attribute) att4).setColumnProperties(c4);
		att4.setEntity(entity);

		AttributeInterface att5 = factory.createBooleanAttribute();
		att5.setName("boolean");
		ColumnPropertiesInterface c5 = factory.createColumnProperties();
		c5.setName("BOOLEAN_ATTRIBUTE");
		((Attribute) att5).setColumnProperties(c5);
		att5.setEntity(entity);

		AttributeInterface att6 = factory.createDoubleAttribute();
		att6.setName("double");
		ColumnPropertiesInterface c6 = factory.createColumnProperties();
		c6.setName("DOUBLE_ATTRIBUTE");
		((Attribute) att6).setColumnProperties(c6);
		att6.setEntity(entity);

		AttributeInterface att7 = factory.createFloatAttribute();
		att7.setName("float");
		att7.setEntity(entity);

		ColumnPropertiesInterface c7 = factory.createColumnProperties();
		c7.setName("FLOAT_ATTRIBUTE");
		((Attribute) att7).setColumnProperties(c7);
		(att7).setIsPrimaryKey(new Boolean(true));

		attributes.add(0, att1);
		attributes.add(1, att2);
		attributes.add(2, att3);
		attributes.add(3, att4);
		attributes.add(4, att5);
		attributes.add(5, att6);
		attributes.add(6, att7);

		return attributes;
	}

	/**
	 * To create expression for Dummy entity. with rule as [name in (1,2,3,4)]
	 * @return the Expression.
	 */
	public static IExpression createExpression(EntityInterface entity)
	{
		IQueryEntity queryEntity = QueryObjectFactory.createQueryEntity(entity);

		IExpression expression = new Expression(queryEntity, 1);
		expression.addOperand(createRule(queryEntity.getDynamicExtensionsEntity(), "int"));
		return expression;
	}

	/**
	 * Create Rule for given Participant as : name in (1,2,3,4)
	 * @param entity The Dynamic Extension Entity Participant
	 * @return The Rule Object.
	 */
	public static IRule createRule(EntityInterface entity, String name)
	{
		List<ICondition> conditions = new ArrayList<ICondition>();
		conditions.add(createInCondition(entity, name));
		IRule rule = QueryObjectFactory.createRule(conditions);
		return rule;
	}

	/**
	 * Cretate Condition for given entity & attributeName : name in (1,2,3,4)
	 * @param entity The Dynamic Extension Entity
	 * @return The Condition object.
	 */
	public static ICondition createInCondition(EntityInterface entity, String name)
	{
		List<String> values = new ArrayList<String>();
		values.add("1");
		values.add("2");
		values.add("3");
		values.add("4");
		AttributeInterface attribute = findAttribute(entity, name);
		ICondition condition = QueryObjectFactory.createCondition(attribute, RelationalOperator.In,
				values);
		return condition;
	}

	/**
	 * To search attribute in the Entity.
	 * @param entity The Dynamic Extension Entity Participant.
	 * @param attributeName The name of the attribute to search.
	 * @return The corresponding attribute.
	 */
	public static AttributeInterface findAttribute(EntityInterface entity, String attributeName)
	{
		Collection<AbstractAttributeInterface> attributes = entity.getAbstractAttributeCollection();
		for (AbstractAttributeInterface attribute: attributes)
		{
			if (attribute.getName().equals(attributeName))
				return (AttributeInterface)attribute;
		}
		return null;
	}

	/**
     * To create IQuery for the Participant as: [activityStatus = 'Active']
     *
     * @param expression The Expression reference created by function
     *            creatParticipantExpression2()
     * @return The corresponding join Graph reference.
     */
    public static IQuery creatParticipantQuery()
    {
        IQuery query = null;
        try
        {
            query = QueryObjectFactory.createQuery();
            IConstraints constraints = QueryObjectFactory.createConstraints();
            query.setConstraints(constraints);

            // creating Participant Expression.
            EntityCache cache = EntityCacheFactory.getInstance();
            EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
            participantEntity = getEntity(cache, participantEntity);
            IQueryEntity participantConstraintEntity = QueryObjectFactory.createQueryEntity(participantEntity);
            IExpression participantExpression = constraints.addExpression(participantConstraintEntity);
            participantExpression.addOperand(createParticipantRule(participantEntity));
            //
            // // creating output tree.
            participantExpression.setInView(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return query;
    }

    public static IQuery createPMIQuery()
    {
        IQuery query = null;
        try
        {
            query = QueryObjectFactory.createQuery();
            IConstraints constraints = QueryObjectFactory.createConstraints();
            query.setConstraints(constraints);

            // creating Participant Expression.
            EntityCache cache = EntityCacheFactory.getInstance();
            EntityInterface pmiEntity = GenericQueryGeneratorMock.createEntity("ParticipantMedicalIdentifier");
            pmiEntity = getEntity(cache, pmiEntity);
            IQueryEntity pmiConstraintEntity = QueryObjectFactory.createQueryEntity(pmiEntity);
            IExpression pmiExpression = constraints.addExpression(pmiConstraintEntity);
            pmiExpression.addOperand(createPMIRule(pmiEntity));
            //
            // // creating output tree.
            pmiExpression.setInView(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return query;
    }

    private static IExpressionOperand createPMIRule(EntityInterface pmiEntity)
    {
        List<ICondition> conditions = new ArrayList<ICondition>();
        conditions.add(createPMICondition(pmiEntity));
        IRule rule = QueryObjectFactory.createRule(conditions);

        return rule;
    }

	private static ICondition createPMICondition(EntityInterface pmiEntity)
	{
        List<String> values = new ArrayList<String>();
        AttributeInterface attribute = findAttribute(pmiEntity, "id");
        ICondition condition = QueryObjectFactory.createCondition(attribute, RelationalOperator.IsNotNull, values);

        return condition;
    }

	public static IQuery createParticipantFNameNotNullQuery()
    {
    	IQuery query = null;
        try
        {
            query = QueryObjectFactory.createQuery();
            IConstraints constraints = QueryObjectFactory.createConstraints();
            query.setConstraints(constraints);

            // creating Participant Expression.
            EntityCache cache = EntityCacheFactory.getInstance();
            EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
            participantEntity = getEntity(cache, participantEntity);
            IQueryEntity participantConstraintEntity = QueryObjectFactory.createQueryEntity(participantEntity);
            IExpression participantExpression = constraints.addExpression(participantConstraintEntity);
            participantExpression.addOperand(createRuleFNameNotNull(participantEntity));
            //
            // // creating output tree.
            participantExpression.setInView(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return query;
    }
    /**
     * Create Rule for given Participant as : activityStatus = 'Active'
     *
     * @param participantEntity The Dynamic Extension Entity Participant
     * @return The Rule reference.
     */
    public static IRule createParticipantRule(EntityInterface participantEntity)
    {
        List<ICondition> conditions = new ArrayList<ICondition>();
        conditions.add(createParticipantCondition(participantEntity));
        IRule rule = QueryObjectFactory.createRule(conditions);

        return rule;
    }

    public static IRule createRuleFNameNotNull(EntityInterface participantEntity)
    {
    	List<ICondition> conditions = new ArrayList<ICondition>();
        conditions.add(createParticipantConditionForNotNull(participantEntity));
        IRule rule = QueryObjectFactory.createRule(conditions);
        return rule;
    }
    /**
     * Create Condition for given Participant Entity: activityStatus = 'Active'.
     *
     * @param participantEntity the Dynamic Extension entity for participant.
     * @return The Condition object.
     */
    public static ICondition createParticipantCondition(EntityInterface participantEntity)
    {
        List<String> values = new ArrayList<String>();
        values.add("Active");
        AttributeInterface attribute = findAttribute(participantEntity, "activityStatus");
        ICondition condition = QueryObjectFactory.createCondition(attribute, RelationalOperator.Equals, values);

        return condition;
    }

    /**
     * Create condition for the given participant entity :firstName is not null.
     * @param participantEntity the Dynamic Extension entity for participant.
     * @return The Condition object.
     */
    public static ICondition createParticipantConditionForNotNull(EntityInterface participantEntity)
    {
        List<String> values = new ArrayList<String>();
        AttributeInterface attribute = findAttribute(participantEntity, "firstName");
        ICondition condition = QueryObjectFactory.createCondition(attribute, RelationalOperator.IsNotNull, values);

        return condition;
    }

    /**
     * Create condition for the given participant entity :firstName in 'ABC','XYZ'.
     * @param participantEntity the Dynamic Extension entity for participant.
     * @return The Condition object.
     */
    public static ICondition createParticipantConditionForIn(EntityInterface participantEntity)
    {
        List<String> values = new ArrayList<String>();
        values.add("ABC");
        values.add("XYZ");
        AttributeInterface attribute = findAttribute(participantEntity, "firstName");
        ICondition condition = QueryObjectFactory.createCondition(attribute, RelationalOperator.In, values);

        return condition;
    }

    /**
     * TO create query for the TABLE_PER_SUB_CLASS inheritance strategy. It will
     * create Query for Clinical Study class as: cs.unsignedConsentDocumentURL =
     * 'XYZ' and cs.activityStatus = 'Active' Here, 1. unsignedConsentDocumentURL
     * attribute is in the derived class i.e. Clinical Study. 2.
     * activityStatus attribute is in the base class of Clinical Study i.e.
     * SpecimenProtocol
     *
     * @return The reference to Query object.
     */
    public static IQuery createInheritanceQuery()
    {
        IQuery query = null;
        try
        {
            query = QueryObjectFactory.createQuery();;
            IConstraints constraints = QueryObjectFactory.createConstraints();
            query.setConstraints(constraints);

            EntityCache cache = EntityCacheFactory.getInstance();
            EntityInterface clinicalStudyEntity = GenericQueryGeneratorMock.createEntity("CollectionProtocol");
            clinicalStudyEntity = getEntity(cache, clinicalStudyEntity);

            // creating expression for Clinical Study.
            IQueryEntity csConstraintEntity = QueryObjectFactory.createQueryEntity(clinicalStudyEntity);
            IExpression csExpression = constraints.addExpression(csConstraintEntity);

            List<String> csExpressionRule1Values1 = new ArrayList<String>();
            csExpressionRule1Values1.add("XYZ");

            ICondition csExpressionRule1Condition1 = QueryObjectFactory.createCondition(findAttribute(
                    clinicalStudyEntity, "unsignedConsentDocumentURL"), RelationalOperator.Equals,
                    csExpressionRule1Values1);
            IRule csExpressionRule1 = QueryObjectFactory.createRule(null);
            csExpressionRule1.addCondition(csExpressionRule1Condition1);

            List<String> csExpressionRule1Values2 = new ArrayList<String>();
            csExpressionRule1Values2.add("Active");
            ICondition csExpressionRule1Condition2 = QueryObjectFactory.createCondition(findAttribute(
                    clinicalStudyEntity.getParentEntity(), "activityStatus"), RelationalOperator.Equals,
                    csExpressionRule1Values2);
            csExpressionRule1.addCondition(csExpressionRule1Condition2);
            csExpression.addOperand(csExpressionRule1);

            setAllExpressionInView(constraints);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return query;
    }

    /**
     * To set all Expressions in the view.
     * @param constraints The reference to constraints object.
     */
    private static void setAllExpressionInView(IConstraints constraints)
    {
        for(IExpression expression : constraints)
        {
            expression.setInView(true);
        }
    }

    /**
     * Get the entity.
     * @param cache cache
     * @param entity entity
     * @return entity
     */
    public static EntityInterface getEntity(EntityCache cache, EntityInterface entity)
	{
		Collection<EntityInterface> entityCollection = new HashSet<EntityInterface>();
		entityCollection.add(entity);
		MatchedClass matchedClass = cache.getEntityOnEntityParameters(entityCollection);
		MatchedClass resultantMatchedClass = new MatchedClass();
		for (MatchedClassEntry matchedClassEntry : matchedClass.getMatchedClassEntries())
		{
			resultantMatchedClass.addMatchedClassEntry(matchedClassEntry);
		}
		resultantMatchedClass.setEntityCollection(resultantMatchedClass.getSortedEntityCollection());
		for(EntityInterface tEntity : resultantMatchedClass.getEntityCollection())
		{
			if(tEntity.getName().equals("edu.wustl.catissuecore.domain."+entity.getName()))
			{
				entity = tEntity;
				break;
			}
		}
		return entity;
	}

    public static ICustomFormula createCustomFormulaParticipantCPR()
    {

        try
        {
            IQuery query = QueryObjectFactory.createQuery();
            IConstraints constraints = query.getConstraints();
            EntityCache cache = EntityCacheFactory.getInstance();
            EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
            participantEntity = getEntity(cache,participantEntity);
            IQueryEntity participantConstraintEntity = QueryObjectFactory.createQueryEntity(participantEntity);
            IExpression participant = constraints.addExpression(participantConstraintEntity);

            EntityInterface csrEntity = GenericQueryGeneratorMock.createEntity("CollectionProtocolRegistration");
            csrEntity = getEntity(cache,csrEntity);
            IQueryEntity csrConstraintEntity = QueryObjectFactory.createQueryEntity(csrEntity);
            IExpression csr = constraints
                    .addExpression(csrConstraintEntity);

            participant.addOperand(csr);

            AssociationInterface partCPR = getAssociationFrom(entityManager.getAssociation(
                    "edu.wustl.catissuecore.domain.Participant", "participant"),
                    "edu.wustl.catissuecore.domain.CollectionProtocolRegistration");

            constraints.getJoinGraph().putAssociation(participant, csr,
                    QueryObjectFactory.createIntraModelAssociation(partCPR));

            IExpressionAttribute birthDate = QueryObjectFactory.createExpressionAttribute(
                    participant, findAttribute(participantEntity,
                            "birthDate"),false);
            IExpressionAttribute registrationDate = QueryObjectFactory.createExpressionAttribute(csr,
                    findAttribute(csrEntity,
                            "registrationDate"),false);

            ITerm lhs = QueryObjectFactory.createTerm();
            lhs.addOperand(registrationDate);
            lhs.addOperand(conn(ArithmeticOperator.Minus), birthDate);

            IDateOffsetLiteral offSet = QueryObjectFactory.createDateOffsetLiteral("30", TimeInterval.Minute);
            ITerm rhs = QueryObjectFactory.createTerm();
            rhs.addOperand(offSet);

            ICustomFormula formula = QueryObjectFactory.createCustomFormula();
            participant.addOperand(getAndConnector(), formula);
            formula.setLhs(lhs);
            formula.addRhs(rhs);
            formula.setOperator(RelationalOperator.GreaterThan);

            participant.setInView(true);
            return formula;
        }
        catch (DynamicExtensionsSystemException e)
        {
			e.printStackTrace();
		}
        catch (DynamicExtensionsApplicationException e)
        {
			e.printStackTrace();
		}
        catch (CyclicException e)
        {
			e.printStackTrace();
		}
        return null;

    }
    /**
     * <pre>
     * Participant (P)
     *      CSR (C)
     *      Temporal (C.registrationDate - P.birthDate &gt; 30 minutes)
     * </pre>
     *
     * @return
     */
    public static IQuery createTemporalQueryParticipantCPR()
    {
        try
        {
            IQuery query = QueryObjectFactory.createQuery();
            IConstraints constraints = query.getConstraints();
            EntityCache cache = EntityCacheFactory.getInstance();
            EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
            participantEntity = getEntity(cache,participantEntity);
            IQueryEntity participantConstraintEntity = QueryObjectFactory.createQueryEntity(participantEntity);
            IExpression participant = constraints.addExpression(participantConstraintEntity);

            EntityInterface csrEntity = GenericQueryGeneratorMock.createEntity("CollectionProtocolRegistration");
            csrEntity = getEntity(cache,csrEntity);
            IQueryEntity csrConstraintEntity = QueryObjectFactory.createQueryEntity(csrEntity);
            IExpression csr = constraints
                    .addExpression(csrConstraintEntity);

            participant.addOperand(csr);

            AssociationInterface partCPR = getAssociationFrom(entityManager.getAssociation(
                    "edu.wustl.catissuecore.domain.Participant", "participant"),
                    "edu.wustl.catissuecore.domain.CollectionProtocolRegistration");

            constraints.getJoinGraph().putAssociation(participant, csr,
                    QueryObjectFactory.createIntraModelAssociation(partCPR));

            IExpressionAttribute birthDate = QueryObjectFactory.createExpressionAttribute(
                    participant, findAttribute(participantEntity,
                            "birthDate"),false);
            IExpressionAttribute registrationDate = QueryObjectFactory.createExpressionAttribute(csr,
                    findAttribute(csrEntity,
                            "registrationDate"),false);

            ITerm lhs = QueryObjectFactory.createTerm();
            lhs.addOperand(registrationDate);
            lhs.addOperand(conn(ArithmeticOperator.Minus), birthDate);

            IDateOffsetLiteral offSet = QueryObjectFactory.createDateOffsetLiteral("30", TimeInterval.Minute);
            ITerm rhs = QueryObjectFactory.createTerm();
            rhs.addOperand(offSet);

            ICustomFormula formula = QueryObjectFactory.createCustomFormula();
            participant.addOperand(getAndConnector(), formula);
            formula.setLhs(lhs);
            formula.addRhs(rhs);
            formula.setOperator(RelationalOperator.GreaterThan);

            participant.setInView(true);
            return query;
        }
        catch (DynamicExtensionsSystemException e)
        {
			e.printStackTrace();
		}
        catch (DynamicExtensionsApplicationException e)
        {
			e.printStackTrace();
		}
        catch (CyclicException e)
        {
			e.printStackTrace();
		}
        return null;
    }

    public static IQuery createParticipantPMIQuery()
    {
        IQuery query = null;
        try
        {
            query = QueryObjectFactory.createQuery();;
            IConstraints constraints = QueryObjectFactory.createConstraints();
            query.setConstraints(constraints);
            IJoinGraph joinGraph = constraints.getJoinGraph();

            EntityCache cache = EntityCacheFactory.getInstance();
            EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
            participantEntity = getEntity(cache, participantEntity);

            // creating expression for Clinical Study.
            IQueryEntity participantConstraintEntity = QueryObjectFactory.createQueryEntity(participantEntity);
            IExpression participantExpression = constraints.addExpression(participantConstraintEntity);
            List<String> participantExpressionRule1Values = new ArrayList<String>();
            participantExpressionRule1Values.add("XYZ");
            ICondition partExpressionRule1Condition1 = QueryObjectFactory.createCondition(findAttribute(
                    participantEntity, "lastName"), RelationalOperator.Equals, participantExpressionRule1Values);
            IRule csExpressionRule1 = QueryObjectFactory.createRule(null);
            csExpressionRule1.addCondition(partExpressionRule1Condition1);
            participantExpression.addOperand(csExpressionRule1);


            EntityInterface pmiEntity = GenericQueryGeneratorMock.createEntity("ParticipantMedicalIdentifier");
            pmiEntity = getEntity(cache, pmiEntity);

            // creating PMI Expression under first CS
            // Expression.
            IQueryEntity pmiConstraintEntity = QueryObjectFactory
                    .createQueryEntity(pmiEntity);
            IExpression pmiExpression1 = constraints
                    .addExpression(pmiConstraintEntity);

            participantExpression.addOperand(getAndConnector(), pmiExpression1);
            AssociationInterface partAndPMIAssociation = getAssociationFrom(entityManager
                    .getAssociation("edu.wustl.catissuecore.domain.Participant", ""),
                    "edu.wustl.caticcuecore.domain.ParticipantMedicalIdentifier");
            IIntraModelAssociation iPartAndPMIAssociation = QueryObjectFactory
                    .createIntraModelAssociation(partAndPMIAssociation);
            joinGraph.putAssociation(participantExpression, pmiExpression1
                    , iPartAndPMIAssociation);

            IRule pmiExpression1Rule1 = QueryObjectFactory.createRule(null);
            pmiExpression1.addOperand(pmiExpression1Rule1);

            List<String> pmiExpression1Rule1Values1 = new ArrayList<String>();
            ICondition pmiExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
                    findAttribute(pmiEntity, "id"), RelationalOperator.IsNotNull,
                    pmiExpression1Rule1Values1);
            pmiExpression1Rule1.addCondition(pmiExpression1Rule1Condition1);

            setAllExpressionInView(constraints);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return query;
    }

    public static IQuery createMultipleRootQuery()
    {

        IQuery query = null;
        try
        {
            query = QueryObjectFactory.createQuery();;
            IConstraints constraints = QueryObjectFactory.createConstraints();
            query.setConstraints(constraints);

            EntityCache cache = EntityCacheFactory.getInstance();
            EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
            participantEntity = getEntity(cache, participantEntity);

            // creating expression for Clinical Study.
            IQueryEntity participantConstraintEntity = QueryObjectFactory.createQueryEntity(participantEntity);
            IExpression participantExpression = constraints.addExpression(participantConstraintEntity);
            List<String> participantExpressionRule1Values = new ArrayList<String>();
            participantExpressionRule1Values.add("XYZ");
            ICondition partExpressionRule1Condition1 = QueryObjectFactory.createCondition(findAttribute(
                    participantEntity, "lastName"), RelationalOperator.Equals, participantExpressionRule1Values);
            IRule csExpressionRule1 = QueryObjectFactory.createRule(null);
            csExpressionRule1.addCondition(partExpressionRule1Condition1);
            participantExpression.addOperand(csExpressionRule1);


            EntityInterface pmiEntity = GenericQueryGeneratorMock.createEntity("ParticipantMedicalIdentifier");
            pmiEntity = getEntity(cache, pmiEntity);

            // creating PMI Expression under first CS
            // Expression.
            IQueryEntity pmiConstraintEntity = QueryObjectFactory
                    .createQueryEntity(pmiEntity);
            IExpression pmiExpression1 = constraints
                    .addExpression(pmiConstraintEntity);

            IRule pmiExpression1Rule1 = QueryObjectFactory.createRule(null);
            pmiExpression1.addOperand(pmiExpression1Rule1);

            List<String> pmiExpression1Rule1Values1 = new ArrayList<String>();
            ICondition pmiExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
                    findAttribute(pmiEntity, "id"), RelationalOperator.IsNotNull,
                    pmiExpression1Rule1Values1);
            pmiExpression1Rule1.addCondition(pmiExpression1Rule1Condition1);

            setAllExpressionInView(constraints);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return query;
    }

    private static void addOutputTermsToQuery(IQuery query, ICustomFormula customFormula,
			String customColumnName)
	{
		IOutputTerm outputTerm = QueryObjectFactory.createOutputTerm();
		outputTerm.setTerm(customFormula.getLhs());
		List<ITerm> allRhs = customFormula.getAllRhs();
		String timeIntervalName = "";
		String dateFormat = "";
		for (ITerm rhs : allRhs)
		{
			if (rhs.getTermType().toString().equalsIgnoreCase("timestamp"))
			{
				dateFormat = AQConstants.DATE_FORMAT;
			}
			IArithmeticOperand operand = rhs.getOperand(0);
			if (operand instanceof IDateOffsetLiteral)
			{
				IDateOffsetLiteral dateOffLit = (IDateOffsetLiteral) operand;
				TimeInterval<?> timeInterval = dateOffLit.getTimeInterval();
				outputTerm.setTimeInterval(timeInterval);
				timeIntervalName = timeInterval.name();
			}
		}

		String tqColumnName = null;
		if (timeIntervalName.length() == 0 && dateFormat.length() == 0)
		{
			tqColumnName = customColumnName;
		}
		else if (dateFormat.length() != 0)
		{
			tqColumnName = customColumnName + " (" + dateFormat + ")";
		}
		else
		{
			tqColumnName = customColumnName + " (" + timeIntervalName + ")";
		}
		outputTerm.setName(tqColumnName);
		query.getOutputTerms().add(outputTerm);
	}

    public static AssociationInterface getAssociationFrom(Collection<AssociationInterface> associations,
            String targetEntityName)
    {
        for (Iterator<AssociationInterface> iter = associations.iterator(); iter.hasNext();)
        {
            AssociationInterface theAssociation = iter.next();
            if (theAssociation == null || theAssociation.getTargetEntity() == null)
            {
                System.out.println("FFOOOOO");
            }
            if (theAssociation.getTargetEntity().getName().equals(targetEntityName))
            {
                return theAssociation;
            }
        }
        return null;
    }

    /**
     * To instantiate Logical connector for AND operator.
     *
     * @return reference to logical connector containing 'AND' logical operator.
     */
    private static IConnector<LogicalOperator> getAndConnector()
    {
        return QueryObjectFactory.createLogicalConnector(LogicalOperator.And);
    }

    private static IConnector<ArithmeticOperator> conn(ArithmeticOperator operator)
    {
        return QueryObjectFactory.createArithmeticConnector(operator);
    }

    /**
     * TO create Query with Inherited Entity, where Parent Expression's Entity
     * is Inherited Entity.
     *
     * <pre>
     * 	MolecularSpecimen: type equals 'DNA'
     * 		SCHAR: Tissue Site Equals &quot;PROSTATE GLAND&quot;
     * </pre>
     *
     * @return reference to Query Object
     */
    public static IQuery createInheritanceQueryWithAssociation1()
    {
        IQuery query = null;
        try
        {
            query = QueryObjectFactory.createQuery();;
            IConstraints constraints = QueryObjectFactory.createConstraints();
            query.setConstraints(constraints);
            IJoinGraph joinGraph = constraints.getJoinGraph();

            EntityCache cache = EntityCacheFactory.getInstance();
            EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
            participantEntity = getEntity(cache, participantEntity);

            // creating expression for Clinical Study.
            IQueryEntity participantConstraintEntity = QueryObjectFactory.createQueryEntity(participantEntity);
            IExpression participantExpression = constraints.addExpression(participantConstraintEntity);
            List<String> participantExpressionRule1Values = new ArrayList<String>();
            participantExpressionRule1Values.add("XYZ");
            ICondition partExpressionRule1Condition1 = QueryObjectFactory.createCondition(findAttribute(
                    participantEntity, "lastName"), RelationalOperator.Equals, participantExpressionRule1Values);
            IRule csExpressionRule1 = QueryObjectFactory.createRule(null);
            csExpressionRule1.addCondition(partExpressionRule1Condition1);
            participantExpression.addOperand(csExpressionRule1);


            EntityInterface csrEntity = GenericQueryGeneratorMock.createEntity("CollectionProtocolRegistration");
            csrEntity = getEntity(cache, csrEntity);

            // creating CSR Expression under first CS
            // Expression.
            IQueryEntity csrConstraintEntity = QueryObjectFactory
                    .createQueryEntity(csrEntity);
            IExpression csrExpression1 = constraints
                    .addExpression(csrConstraintEntity);

            participantExpression.addOperand(getAndConnector(), csrExpression1);
            AssociationInterface cSAndCSRAssociation = getAssociationFrom(entityManager
                    .getAssociation("edu.wustl.catissuecore.domain.Participant", ""),
                    "edu.wustl.catissuecore.domain.CollectionProtocolRegistration");
            IIntraModelAssociation iCSAndCSRAssociation = QueryObjectFactory
                    .createIntraModelAssociation(cSAndCSRAssociation);
            joinGraph.putAssociation(participantExpression, csrExpression1
                    , iCSAndCSRAssociation);

            IRule csrExpression1Rule1 = QueryObjectFactory.createRule(null);
            csrExpression1.addOperand(csrExpression1Rule1);

            List<String> csrExpression1Rule1Values1 = new ArrayList<String>();
            csrExpression1Rule1Values1.add("Active");
            ICondition csrExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
                    findAttribute(csrEntity, "activityStatus"), RelationalOperator.Equals,
                    csrExpression1Rule1Values1);
            csrExpression1Rule1.addCondition(csrExpression1Rule1Condition1);

            setAllExpressionInView(constraints);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return query;
    }

	public static IQuery createTemporalQueryParticipantCSR()
	{
        try
        {
            IQuery query = QueryObjectFactory.createQuery();
            IConstraints constraints = query.getConstraints();
            EntityCache cache = EntityCacheFactory.getInstance();
            EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
            participantEntity = getEntity(cache,participantEntity);
            IQueryEntity participantConstraintEntity = QueryObjectFactory.createQueryEntity(participantEntity);
            IExpression participant = constraints.addExpression(participantConstraintEntity);

            EntityInterface csrEntity = GenericQueryGeneratorMock.createEntity("CollectionProtocolRegistration");
            csrEntity = getEntity(cache,csrEntity);
            IQueryEntity csrConstraintEntity = QueryObjectFactory.createQueryEntity(csrEntity);
            IExpression csr = constraints
                    .addExpression(csrConstraintEntity);

            participant.addOperand(csr);

            AssociationInterface partCPR = getAssociationFrom(entityManager.getAssociation(
                    "edu.wustl.catissuecore.domain.Participant", "participant"),
                    "edu.wustl.catissuecore.domain.CollectionProtocolRegistration");

            constraints.getJoinGraph().putAssociation(participant, csr,
                    QueryObjectFactory.createIntraModelAssociation(partCPR));

            IExpressionAttribute birthDate = QueryObjectFactory.createExpressionAttribute(
                    participant, findAttribute(participantEntity,
                            "birthDate"),false);
            IExpressionAttribute registrationDate = QueryObjectFactory.createExpressionAttribute(csr,
                    findAttribute(csrEntity,
                            "registrationDate"),false);

            ITerm lhs = QueryObjectFactory.createTerm();
            lhs.addOperand(registrationDate);
            lhs.addOperand(conn(ArithmeticOperator.Minus), birthDate);

            IDateOffsetLiteral offSet = QueryObjectFactory.createDateOffsetLiteral("30", TimeInterval.Minute);
            ITerm rhs = QueryObjectFactory.createTerm();
            rhs.addOperand(offSet);

            ICustomFormula formula = QueryObjectFactory.createCustomFormula();
            participant.addOperand(getAndConnector(), formula);
            formula.setLhs(lhs);
            formula.addRhs(rhs);
            formula.setOperator(RelationalOperator.GreaterThan);

            participant.setInView(true);
            addOutputTermsToQuery(query, formula, "Age at reg");
            return query;
        }
        catch (DynamicExtensionsSystemException e)
        {
			e.printStackTrace();
		}
        catch (DynamicExtensionsApplicationException e)
        {
			e.printStackTrace();
		}
        catch (CyclicException e)
        {
			e.printStackTrace();
		}
        return null;
	}

	public static AssociationInterface getAssociation(EntityInterface source, EntityInterface target)
	{
		AssociationInterface association = null;
		try
		{
			association = getAssociationFrom(entityManager.getAssociation(
			        "edu.wustl.catissuecore.domain.Participant", "participant"),
			"edu.wustl.catissuecore.domain.ParticipantMedicalIdentifier");
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
		}
		return association;
	}
}
