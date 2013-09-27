package edu.wustl.query.bizlogic;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentCollectionConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedSetConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernateProxyConverter;
import com.thoughtworks.xstream.hibernate.mapper.HibernateMapper;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import edu.common.dynamicextensions.domain.Association;
import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.EntityGroup;
import edu.common.dynamicextensions.domain.Role;
import edu.common.dynamicextensions.domain.StringValue;
import edu.common.dynamicextensions.domain.databaseproperties.ColumnProperties;
import edu.common.dynamicextensions.domain.databaseproperties.ConstraintKeyProperties;
import edu.common.dynamicextensions.domain.databaseproperties.ConstraintProperties;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.querysuite.metadata.associations.IAssociation;
import edu.wustl.common.querysuite.metadata.associations.impl.IntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.IArithmeticOperand;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionAttribute;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.querysuite.queryobject.IOutputAttribute;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.IQueryEntity;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.ITerm;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.impl.BaseQueryObject;
import edu.wustl.common.querysuite.queryobject.impl.Condition;
import edu.wustl.common.querysuite.queryobject.impl.Connector;
import edu.wustl.common.querysuite.queryobject.impl.Constraints;
import edu.wustl.common.querysuite.queryobject.impl.CustomFormula;
import edu.wustl.common.querysuite.queryobject.impl.Expression;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.common.querysuite.queryobject.impl.OutputAttribute;
import edu.wustl.common.querysuite.queryobject.impl.Parameter;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.impl.QueryEntity;
import edu.wustl.common.querysuite.queryobject.impl.Rule;
import edu.wustl.common.util.Graph;

public class ParameterizedQueryXmlSerializer implements ParameterizedQuerySerializer {
	
	public void serialize(IParameterizedQuery query, OutputStream out) {
		CompactWriter writer = null;
		XStream xstream = null;
		try {
			xstream = initializeXStream();
			writer = new CompactWriter(new OutputStreamWriter(out));
			xstream.marshal(query, writer);
		} catch (Exception e) {
			throw new RuntimeException("Error serializing parameterized query", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public IParameterizedQuery deserialize(InputStream in) {
		IParameterizedQuery query = null;
		XStream xstream = null;
		try {
			xstream = initializeXStream();
			query = (IParameterizedQuery) xstream.fromXML(in);
			hydrateQuery(query);
			return query;
		} catch (Exception e) {
			throw new RuntimeException("Error de-serializing input xml", e);
		}  
	}

	private XStream initializeXStream() {	
		XStream xstream = new XStream(new DomDriver()) {
			protected MapperWrapper	wrapMapper(MapperWrapper next) {
				return new HibernateMapper(next) {
					public boolean shouldSerializeMember(Class definedIn, String fieldName) {
						Class<?> entityClass = Entity.class;
						Class<?> entityGroupClass = EntityGroup.class;
						Class<?> attributeClass = Attribute.class;

						if (definedIn.isAssignableFrom(entityClass) && !fieldName.equals("name") && !fieldName.equals("entityGroup")) {
							return false;
						} else if (definedIn.isAssignableFrom(entityGroupClass) && !fieldName.equals("name")) {
							return false;
						} else if (definedIn.isAssignableFrom(attributeClass) && !fieldName.equals("name") && !fieldName.equals("entity")) { 
							 
							return false;
						} else if (fieldName.equals("id") || fieldName.equals("identifier")){ 
							return false;
						} 
						
						return super.shouldSerializeMember(definedIn, fieldName);
					}
				};
			}
		};

		setAlias(xstream);
		registerHibernateConverters(xstream);
		xstream.setMode(XStream.ID_REFERENCES);
		return xstream;
	}

	private void setAlias(XStream xstream) {
		xstream.alias("role",                    Role.class);		
		xstream.alias("rule",                    Rule.class);
		xstream.alias("entityGroup",             EntityGroup.class);
		xstream.alias("entity",                  Entity.class);
		xstream.alias("attribute",               Attribute.class);
		xstream.alias("association",             Association.class);
		
		xstream.alias("baseQueryObject",         BaseQueryObject.class);
		xstream.alias("constraints",             Constraints.class);
		xstream.alias("expression",              Expression.class);
		xstream.alias("queryEntity",             QueryEntity.class);
		xstream.alias("outputAttribute",         OutputAttribute.class);
		xstream.alias("condition",               Condition.class);
		xstream.alias("connector",               Connector.class);
		xstream.alias("logicalOperator",         LogicalOperator.class);
		xstream.alias("parameter",               Parameter.class);
		xstream.alias("stringValue",             StringValue.class);
		
		xstream.alias("columnProperties",        ColumnProperties.class);
		xstream.alias("parameterizedQuery",      ParameterizedQuery.class);
		xstream.alias("constraintProperties",    ConstraintProperties.class);
		xstream.alias("intraModelAssociation",   IntraModelAssociation.class);
		xstream.alias("constraintKeyProperties", ConstraintKeyProperties.class);
	}

	private void registerHibernateConverters(XStream xstream)
	{
		xstream.registerConverter(new HibernateProxyConverter());
		xstream.registerConverter(new HibernatePersistentCollectionConverter(xstream.getMapper()));
		xstream.registerConverter(new HibernatePersistentMapConverter(xstream.getMapper()));
		xstream.registerConverter(new HibernatePersistentSortedMapConverter(xstream.getMapper()));
		xstream.registerConverter(new HibernatePersistentSortedSetConverter(xstream.getMapper()));
	}

	private void hydrateQuery(IParameterizedQuery query) {
		hydrateEntities(query);
		hydrateAttributes(query);
		hydrateAssociations(query);
		hydrateOutputAttributes(query);
	}

	private void hydrateEntities(IParameterizedQuery query) {
		Set<IQueryEntity> queryEntities = query.getConstraints().getQueryEntities();
		for(IQueryEntity queryEntity : queryEntities) {
			if (!(queryEntity instanceof QueryEntity)) {
				continue;
			}			
			((QueryEntity)queryEntity).setDynamicExtensionsEntity(getHydratedEntity(queryEntity.getDynamicExtensionsEntity()));
		}
	}

	private void hydrateAttributes(IParameterizedQuery query) {
		Iterator<IExpression> exprIter = query.getConstraints().iterator();

		while (exprIter.hasNext()) { // iterating expressions
			IExpression expr = exprIter.next();
			Iterator<IExpressionOperand> exprOperandIter = expr.iterator();
			while (exprOperandIter.hasNext()) { // iterating expression operands
				IExpressionOperand exprOperand =  exprOperandIter.next();
				if (exprOperand instanceof IRule) {
					Iterator<ICondition> exprConditionIter =((IRule)exprOperand).iterator();
					while (exprConditionIter.hasNext()) {
						ICondition condition = exprConditionIter.next();
						AbstractAttributeInterface attr = getHydratedAttribute(condition.getAttribute());
						condition.setAttribute((AttributeInterface)attr);
					}
				} else if (exprOperand instanceof CustomFormula) {
					List<ITerm> rhsTerms =((CustomFormula)exprOperand).getAllRhs();
					for (ITerm rhsTerm : rhsTerms) {
						hydrateTermAttributes(rhsTerm);
					}

					hydrateTermAttributes(((CustomFormula)exprOperand).getLhs());
				}
			}
		}
	}

	private void hydrateAssociations(IParameterizedQuery query) {
		IJoinGraph joinGraph = (IJoinGraph) query.getConstraints().getJoinGraph();
		if (!(joinGraph instanceof JoinGraph)) {
			return;
		}

		Graph<IExpression, IAssociation> graph = ((JoinGraph)joinGraph).getGraph();
		for (IExpression exp : graph.getVertices()) {
			Map<IExpression, IAssociation> edges = graph.getOutgoingEdges(exp);
			for (Map.Entry<IExpression, IAssociation> edge : edges.entrySet()) {
				IAssociation assoc = edge.getValue();

				EntityInterface source = getHydratedEntity(assoc.getSourceEntity());
				EntityInterface target = getHydratedEntity(assoc.getTargetEntity());
					
				AssociationInterface hydratedAssoc = (AssociationInterface)source.getAssociation(target);
				if (assoc instanceof IntraModelAssociation) {
					IntraModelAssociation intraAssoc = (IntraModelAssociation)assoc;
					intraAssoc.setDynamicExtensionsAssociation(hydratedAssoc);
				}
			}
		}
	}

	private void hydrateOutputAttributes(IParameterizedQuery query) {
		List<IOutputAttribute> outputAttributes = query.getOutputAttributeList();
		for(IOutputAttribute outputAttribute: outputAttributes) {
			if(outputAttribute instanceof OutputAttribute) {
				AbstractAttributeInterface attr = getHydratedAttribute(outputAttribute.getAttribute());
				((OutputAttribute)outputAttribute).setAttribute((AttributeInterface) attr);
			}
		}
	}

	private EntityInterface getHydratedEntity(EntityInterface entity) {
		return getEntity(entity.getEntityGroup().getName(), entity.getName());
	}

	private EntityInterface getEntity(String entityGroupName, String entityName) {
		EntityCache entityCache = EntityCache.getInstance();
		EntityGroupInterface eg = (EntityGroupInterface)entityCache.getEntityGroupByName(entityGroupName);
		return (EntityInterface)eg.getEntityByName(entityName);
	}

	private AbstractAttributeInterface getHydratedAttribute(AbstractAttributeInterface attr) {
		EntityInterface entity = getEntity(attr.getEntity().getEntityGroup().getName(), attr.getEntity().getName());
		return entity.getAbstractAttributeByName(attr.getName());
	}

	private void hydrateTermAttributes(ITerm term) {
		Iterator<IArithmeticOperand> operandsIter = term.iterator();
		while (operandsIter.hasNext()) {
			IArithmeticOperand operand = operandsIter.next();
			if (operand instanceof IExpressionAttribute) {
				IExpressionAttribute exprAttr = (IExpressionAttribute)operand;
				exprAttr.setAttribute((AttributeInterface) getHydratedAttribute(exprAttr.getAttribute()));
			}
		}
	}
}
