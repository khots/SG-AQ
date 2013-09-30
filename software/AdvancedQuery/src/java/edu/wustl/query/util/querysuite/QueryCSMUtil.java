/**
 *
 */

package edu.wustl.query.util.querysuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants.InheritanceStrategy;
import edu.wustl.cab2b.client.ui.query.IPathFinder;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.query.impl.CommonPathFinder;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IQueryEntity;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.impl.Rule;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.metadata.util.DyExtnObjectCloner;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.query.bizlogic.QueryCsmBizLogic;
import edu.wustl.query.flex.FlexInterface;
import edu.wustl.query.flex.dag.DAGPanel;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.Variables;
import edu.wustl.security.global.Utility;

/**
 * @author supriya_dankh
 * This class contains all the methods required for CSM integration in query.
 */
public class QueryCSMUtil
{
	private static org.apache.log4j.Logger logger = LoggerConfig
	.getConfiguredLogger(QueryCSMUtil.class);

	/**This method will check if main objects for all the dependent objects are present in query or not.
	 * If yes then will create map of entity as key and main entity list as value.
	 * If not then will set error message in session.
	 * @param query query
	 * @param session session
	 * @param queryDetailsObj queryDetailsObj
	 * @return queryClone queryClone
	 */
	public static IQuery returnQueryClone(
			IQuery query, HttpSession session, QueryDetails queryDetailsObj)
	{
		Map<EntityInterface, List<EntityInterface>> mainEntityMap =
			getMainEntitiesForAllQueryNodes(queryDetailsObj);
		queryDetailsObj.setMainEntityMap(mainEntityMap);

		IQuery originalQuery = queryDetailsObj.getQuery();
		queryDetailsObj.setQuery(query);
		IQuery queryClone = getQueryClone(queryDetailsObj,session);
		queryDetailsObj.setQuery(originalQuery);
		session.setAttribute(AQConstants.MAIN_ENTITY_MAP, mainEntityMap);
		return queryClone;
	}

	/**
	 * This method will prepare the query clone and return it.
	 * @param queryDetailsObj contains group of fields in this Object
	 * @param session HttpSession object
	 * @return query clone.
	 */
	private static IQuery getQueryClone(QueryDetails queryDetailsObj,HttpSession session)
	{
		IQuery queryClone=null;
		try
		{
			boolean isMainObjAdded = false;
			IPathFinder pathFinder = new CommonPathFinder();
			DAGPanel dagPanel = new DAGPanel(pathFinder);
			//iterate through the uniqueIdNodesMap and check if main entities of all the nodes are present
			for (Object element : queryDetailsObj.getUniqueIdNodesMap().entrySet())
			{
				Map.Entry<String, OutputTreeDataNode> idmapValue =
					(Map.Entry<String, OutputTreeDataNode>) element;
				OutputTreeDataNode node = idmapValue.getValue(); // get the node
				//get the entity
				EntityInterface mapEntity = node.getOutputEntity().getDynamicExtensionsEntity();
				// get the main entities list for the entity
				List<EntityInterface> finalMnEntityLst = queryDetailsObj.getMainEntityMap()
						.get(mapEntity);
				List<EntityInterface> mainEntityList = new ArrayList<EntityInterface>();
				FlexInterface flexInterface = new FlexInterface();
				flexInterface.initFlexInterface();
				mainEntityList = setMainEntityList(finalMnEntityLst, mainEntityList);
				if (mainEntityList == null)//mainEntityList is null if the entity itself is main entity;
				{
					if(!isMainObjAdded)
					{
						isMainObjAdded=true;
					}
				}
				else
				{
					queryClone = addMainEntityToQuery(queryDetailsObj, dagPanel, node,
							mainEntityList,session);
				}
			}
			queryClone = modifyQuery(queryDetailsObj, queryClone,
					isMainObjAdded, dagPanel);
		}
		catch(MultipleRootsException e)
		{
			logger.error(e.getMessage(), e);
		}
		return queryClone;
	}

	/**
	 * @param queryDetailsObj queryDetailsObj
	 * @param query query
	 * @param isMainObjAdded isMainObjAdded
	 * @param dagPanel dagPanel
	 * @return queryClone
	 * @throws MultipleRootsException MultipleRootsException
	 */
	private static IQuery modifyQuery(QueryDetails queryDetailsObj,
			IQuery query, boolean isMainObjAdded, DAGPanel dagPanel)
			throws MultipleRootsException
	{
		IQuery queryClone = query;
		EntityInterface firstEntity = queryDetailsObj.getQuery().getConstraints().
		getJoinGraph().getRoot().getQueryEntity().getDynamicExtensionsEntity();
		if(isMainObjAdded)
		{
			queryClone = addMainProtocolObjInQuery(queryDetailsObj, firstEntity,
					dagPanel);
		}
		return queryClone;
	}

	/**
	 * @param finalMnEntityLst finalMnEntityLst
	 * @param mainEntityList mainEntityList
	 * @return mainEntityList
	 */
	private static List<EntityInterface> setMainEntityList(
			List<EntityInterface> finalMnEntityLst,
			List<EntityInterface> tmpMainEntityLst)
	{
		List<EntityInterface> mainEntityList = tmpMainEntityLst;
		if(finalMnEntityLst == null)
		{
			mainEntityList = null;
		}
		else
		{
			updateFinalMainEntityList(finalMnEntityLst, mainEntityList);
		}
		return mainEntityList;
	}

	/**
	 * Add main entity to the query clone.
	 * @param queryDetailsObj queryDetailsObj
	 * @param dagPanel dagPanel
	 * @param node node
	 * @param mainEntityList mainEntityList
	 * @param session HttpSession object
	 * @return queryClone Modified query object
	 */
	private static IQuery addMainEntityToQuery(QueryDetails queryDetailsObj,
			DAGPanel dagPanel, OutputTreeDataNode node,
			List<EntityInterface> mainEntityList,HttpSession session)
	{
		IQuery queryClone=null;
		EntityInterface mainEntity;
		//check if main entity of the dependent entity is present in the query
		mainEntity = getMainEntity(mainEntityList, node);
		if (mainEntity == null) // if main entity is not present
		{
			queryClone = modifyQueryObject(queryDetailsObj, mainEntityList, dagPanel,session);
		}
		return queryClone;
	}

	/**
	 * Populate main entity list.
	 * @param finalMnEntityLst finalMainEntityList
	 * @param mainEntityList mainEntityList
	 */
	private static void updateFinalMainEntityList(List<EntityInterface> finalMnEntityLst,
			List<EntityInterface> mainEntityList)
	{
		for(EntityInterface mainEntity : finalMnEntityLst)
		{
			if(!mainEntityList.contains(mainEntity))
			{
				mainEntityList.add(mainEntity);
			}
		}
	}

	/**
	 * Adds main protocol object (CS/CP) to the query clone if its not present.
	 * @param queryDetailsObj queryDetailsObj
	 * @param mapEntity mapEntity
	 * @param dagPanel dagPanel
	 * @return queryClone Modified query
	 */
	private static IQuery addMainProtocolObjInQuery(QueryDetails queryDetailsObj,
			EntityInterface mapEntity, DAGPanel dagPanel)
	{
		IQuery queryClone = new DyExtnObjectCloner().clone(queryDetailsObj.getQuery());
		queryDetailsObj.setQuery(queryClone);
		String mainEntityId = "";
		boolean isMainObjPresent = false;
		IQuery originalQuery = queryDetailsObj.getQuery();
		IConstraints constraints = originalQuery.getConstraints();
		Set<IQueryEntity> queryEntities = constraints.getQueryEntities();
		EntityInterface mainEntity = null;
		for(IQueryEntity queryEntity : queryEntities)
		{
			if(queryEntity.getDynamicExtensionsEntity().getName().equals(Variables.mainProtocolObject))
			{
				mainEntity = queryEntity.getDynamicExtensionsEntity();
				mainEntityId = mainEntity.getId().toString();
				isMainObjPresent = true;
				break;
			}
		}
		if(isMainObjPresent && queryDetailsObj.getSessionData().isSecurityRequired())
		{
			StringBuffer strToCreateObject = new StringBuffer();
			Map<RelationalOperator,List<String>> oprVsLstOfVals =
				new HashMap<RelationalOperator, List<String>>();
			callModifyRule(mainEntityId, strToCreateObject, constraints,
					oprVsLstOfVals);
			dagPanel.createQueryObject(strToCreateObject.toString(),
			mainEntity.getId().toString(),AQConstants.EDIT,queryDetailsObj,oprVsLstOfVals);
		}
		else
		{
			dagPanel.createQueryObject(null,mapEntity.getId().toString(),
					AQConstants.ADD,queryDetailsObj,null);
		}
		queryClone = queryDetailsObj.getQuery();
		queryDetailsObj.setQuery(originalQuery);
		return queryClone;
	}

	/**
	 * @param mainEntityId main entity identifier
	 * @param strToCreateObject query string
	 * @param constraints constraints
	 * @param oprVsLstOfVals map
	 */
	private static void callModifyRule(String mainEntityId,
			StringBuffer strToCreateObject, IConstraints constraints,
			Map<RelationalOperator, List<String>> oprVsLstOfVals)
	{
		for(IExpression expression : constraints)
		{
			modifyRule(mainEntityId, strToCreateObject, oprVsLstOfVals, expression);
		}
	}

	/**
	 * Modify the rule by adding/deleting the conditions.
	 * @param mainEntityId mainEntityId
	 * @param strToCreateObject strToCreateObject
	 * @param oprVsLstOfVals operatorAndLstOfValues
	 * @param expression expression
	 */
	private static void modifyRule(String mainEntityId, StringBuffer strToCreateObject,
			Map<RelationalOperator, List<String>> oprVsLstOfVals, IExpression expression)
	{
		IQueryEntity queryEntity = expression.getQueryEntity();
		EntityInterface deEntity =
			queryEntity.getDynamicExtensionsEntity();
		Long deEntityId = deEntity.getId();
		if(String.valueOf(deEntityId).equals(mainEntityId) &&
				expression.numberOfOperands() != 0 &&
				expression.getOperand(0) instanceof Rule)
		{
			processRules(strToCreateObject, oprVsLstOfVals, expression);
		}
	}

	/**
	 * @param strToCreateObject strToCreateObject
	 * @param oprVsLstOfVals Map
	 * @param expression expression
	 */
	private static void processRules(StringBuffer strToCreateObject,
			Map<RelationalOperator, List<String>> oprVsLstOfVals,
			IExpression expression)
	{
		ICondition idCondition = null;
		Rule rule = ((Rule) (expression.getOperand(0)));
		for(ICondition condition : rule)
		{
			AttributeInterface conditionAttr =
				condition.getAttribute();
			String attrName = conditionAttr.getName();
			if(attrName.equalsIgnoreCase("id"))
			{
				idCondition = processRuleForId
				(oprVsLstOfVals, condition);
			}
			else
			{
				processRuleForOtherAttributes(strToCreateObject, condition,
						conditionAttr, attrName);
			}
		}
		//rule.removeCondition(idCondition);
	}

	/**
	 * Modify rule for attributes other than id.
	 * @param strToCreateObject strToCreateObject
	 * @param condition condition
	 * @param conditionAttr conditionAttr
	 * @param attrName attrName
	 */
	private static void processRuleForOtherAttributes(StringBuffer strToCreateObject,
			ICondition condition, AttributeInterface conditionAttr, String attrName)
	{
		Long attrId = conditionAttr.getId();
		RelationalOperator operator = condition.getRelationalOperator();
		strToCreateObject.append("@#condition#@").append(attrName).
		append(attrId).append(AQConstants.CONDITION_SEPERATOR).append(operator.getStringRepresentation());
		if(operator.equals(RelationalOperator.In) || operator.equals(RelationalOperator.NotIn))
		{
			modifyQueryStringForInNotIn(strToCreateObject, condition.getValues());
		}
		else if(!(operator.equals(RelationalOperator.IsNotNull) || operator.equals(RelationalOperator.IsNull)))
		{
			strToCreateObject.append(AQConstants.CONDITION_SEPERATOR).append(condition.getValue());
		}
		strToCreateObject.append(';');
	}

	/**
	 * @param strToCreateObject query string
	 * @param values values
	 */
	private static void modifyQueryStringForInNotIn(
			StringBuffer strToCreateObject, List<String> values)
	{
		strToCreateObject.append(AQConstants.CONDITION_SEPERATOR);
		for(String val : values)
		{
			strToCreateObject.append('&').append(val);
		}
	}

	/**
	 * Modify rule for id attribute.
	 * @param oprVsLstOfVals operatorAndLstOfValues
	 * @param condition condition
	 * @return idCondition idCondition
	 */
	private static ICondition processRuleForId(
			Map<RelationalOperator, List<String>> oprVsLstOfVals, ICondition condition)
	{
		ICondition idCondition;
		idCondition = condition;
		RelationalOperator operator = condition.getRelationalOperator();
		if(!operator.equals(RelationalOperator.IsNotNull) && !operator.equals(RelationalOperator.IsNull))
		{
			List<String> values = condition.getValues();
			oprVsLstOfVals.put(condition.getRelationalOperator(), values);
		}
		return idCondition;
	}

	/**
	 * Modifies the query object by adding main entity to the query.
	 * @param queryDetailsObj QueryDetails object
	 * @param mainEntityList Main entity list
	 * @param dagPanel DAGPanel object
	 * @param session session
	 * @return queryClone Modified query object
	 */
	private static IQuery modifyQueryObject(QueryDetails queryDetailsObj,
			List<EntityInterface> mainEntityList, DAGPanel dagPanel,HttpSession session)
	{
		IQuery queryClone;
		List<EntityInterface> entityList = new ArrayList<EntityInterface>();
		List<String> strToCreateQueryObjectList = new ArrayList<String>();
		EntityInterface mainEntityObject = getMainEntityObject(mainEntityList);
		entityList.add(mainEntityObject);

		Long identifier = mainEntityObject.getAbstractAttributeByName("id").getId();

		String strToCreateQueryObject = AQConstants.ID_CONDITION+identifier+
		AQConstants.CONDITION_SEPERATOR+"Is Not Null;";
		strToCreateQueryObjectList.add(strToCreateQueryObject);

		IQuery originalQuery = queryDetailsObj.getQuery();
		queryClone = new DyExtnObjectCloner().clone(originalQuery);
		queryDetailsObj.setQuery(queryClone);

		createQueryObject(queryDetailsObj, dagPanel, entityList,strToCreateQueryObjectList);
		boolean alreadySavedQuery = isAlreadySavedQuery(session);
		boolean savedQueryProcessed = isSavedQueryProcessed(session);
		if(alreadySavedQuery && savedQueryProcessed && !originalQuery.equals(queryDetailsObj.getQuery()) && queryClone.equals(queryDetailsObj.getQuery()))
		{
			queryDetailsObj.setQuery(queryClone);
			queryClone = originalQuery;
		}
		else
		{
			queryClone = queryDetailsObj.getQuery();
			queryDetailsObj.setQuery(originalQuery);
		}
		return queryClone;
	}

	/**
	 * @param session session
	 * @return savedQueryProcessed
	 */
	private static boolean isSavedQueryProcessed(HttpSession session)
	{
		boolean savedQueryProcessed = false;
		if(session.getAttribute(AQConstants.PROCESSED_SAVED_QUERY) != null)
		{
			savedQueryProcessed = Boolean.valueOf((String)session.getAttribute(AQConstants.PROCESSED_SAVED_QUERY));
		}
		return savedQueryProcessed;
	}

	/**
	 * @param session session
	 * @return alreadySavedQuery
	 */
	private static boolean isAlreadySavedQuery(HttpSession session)
	{
		boolean alreadySavedQuery=false;
		if(session.getAttribute(AQConstants.SAVED_QUERY) != null)
		{
			alreadySavedQuery = Boolean.valueOf((String)session.getAttribute(AQConstants.SAVED_QUERY));
		}
		return alreadySavedQuery;
	}

	/**
	 * @param queryDetailsObj queryDetailsObj
	 * @param dagPanel dagPanel
	 * @param entityList entityList
	 * @param strToCreateQueryObjectList strToCreateQueryObjectList
	 */
	private static void createQueryObject(QueryDetails queryDetailsObj,
			DAGPanel dagPanel, List<EntityInterface> entityList,
			List<String> strToCreateQueryObjectList)
	{
		for(int counter=0;counter<entityList.size();counter++)
		{
			dagPanel.createQueryObject(strToCreateQueryObjectList.get(counter),
			entityList.get(counter).getId().toString(),AQConstants.ADD,queryDetailsObj,null);
		}
	}

	/**
	 * @param mainEntityList mainEntityList
	 * @return mainEntityObject
	 */
	private static EntityInterface getMainEntityObject(
			List<EntityInterface> mainEntityList)
	{
		EntityInterface mainEntityObject=null;
		if(mainEntityList != null)
		{
			if( mainEntityList.size()>1)
			{
				mainEntityObject = getMainEntityObject(mainEntityList,
						mainEntityObject);
			}
			else
			{
				mainEntityObject = mainEntityList.get(0);
			}
		}
		return mainEntityObject;
	}

	/**
	 * @param mainEntityList list
	 * @param mainObject main entity object
	 * @return mainEntityObject
	 */
	private static EntityInterface getMainEntityObject(
			List<EntityInterface> mainEntityList,
			EntityInterface mainObject)
	{
		EntityInterface mainEntityObject = mainObject;
		for(EntityInterface tempEntity : mainEntityList)
		{
			if(tempEntity.getName().equalsIgnoreCase
				("edu.wustl.clinportal.domain.ClinicalStudyRegistration"))
			{
				mainEntityObject = tempEntity;
				break;
			}
		}
		if(mainEntityObject == null)
		{
			mainEntityObject = mainEntityList.get(0);
		}
		return mainEntityObject;
	}

	/**
	 * This method will return map of a entity as value and list of all the main entities
	 * of this particular entity as value.
	 * @param queryDetailsObj QueryDetails object
	 * @return mainEntityMap Map of all main entities present in query.
	 */
	public static Map<EntityInterface, List<EntityInterface>> getMainEntitiesForAllQueryNodes(
			QueryDetails queryDetailsObj)
	{
		Map<EntityInterface, List<EntityInterface>> mainEntityMap =
			new HashMap<EntityInterface, List<EntityInterface>>();
		for (OutputTreeDataNode queryNode : queryDetailsObj.getUniqueIdNodesMap().values())
		{
			List<EntityInterface> mainEntityList = new ArrayList<EntityInterface>();
			EntityInterface deEntity = queryNode.getOutputEntity()
					.getDynamicExtensionsEntity();
			mainEntityList = getAllMainEntities(deEntity, mainEntityList);
			populateMainEntitiesForANode(mainEntityList, deEntity);
			if (mainEntityList.size() != 1)
			{
				mainEntityList = populateMainEntityList(mainEntityList, deEntity);
			}
			populateMap(mainEntityMap, mainEntityList, deEntity);
		}
		return mainEntityMap;
	}

	/**
	 * @param mainEntityMap mainEntityMap
	 * @param mainEntityList mainEntityList
	 * @param deEntity deEntity
	 */
	private static void populateMap(
			Map<EntityInterface, List<EntityInterface>> mainEntityMap,
			List<EntityInterface> mainEntityList, EntityInterface deEntity)
	{
		if (!(mainEntityList != null && mainEntityList.size() == 1 && mainEntityList.get(0)
				.equals(deEntity)))
		{
			populateMainEntityMap(mainEntityMap, mainEntityList, deEntity);
		}
	}

	/**
	 * @param mainEntityList mainEntityList
	 * @param deEntity deEntity
	 */
	private static void populateMainEntitiesForANode(
			List<EntityInterface> mainEntityList, EntityInterface deEntity)
	{
		EntityInterface tempDeEntity = deEntity;
		List<EntityInterface> tmpMnEntityLst;
		while (true)
		{
			tmpMnEntityLst = new ArrayList<EntityInterface>();
			EntityInterface parentEntity = tempDeEntity.getParentEntity();
			if (parentEntity == null)
			{
				break;
			}
			else
			{
				tmpMnEntityLst = getAllMainEntities(parentEntity, tmpMnEntityLst);
				for (EntityInterface tempMainEntity : tmpMnEntityLst)
				{
					if (!(tempMainEntity.equals(parentEntity)))
					{
						mainEntityList.add(tempMainEntity);
					}
				}
				tempDeEntity = parentEntity;
			}
		}
	}

	/**
	 * Populates the main entity list.
	 * @param mainEntityList mainEntityList
	 * @param deEntity dynamicExtensionsEntity
	 * @return mainEntityList mainEntityList
	 */
	private static List<EntityInterface> populateMainEntityList(
			List<EntityInterface> mainEntityList, EntityInterface deEntity)
	{
		List<EntityInterface> temporaryList = new ArrayList<EntityInterface>();

		for (EntityInterface mainEntity : mainEntityList)
		{
			if (!(mainEntity.equals(deEntity)))
			{
				temporaryList.add(mainEntity);
			}
		}
		return temporaryList;
	}

	/**
	 * Populate the main entity map (key->Entity value->List of main entities).
	 * @param mainEntityMap mainEntityMap
	 * @param mainEntityList mainEntityList
	 * @param deEntity dynamicExtensionsEntity
	 */
	private static void populateMainEntityMap(
			Map<EntityInterface, List<EntityInterface>> mainEntityMap,
			List<EntityInterface> mainEntityList, EntityInterface deEntity)
	{
		List<EntityInterface> tmpMnEntityLst;
		tmpMnEntityLst = new ArrayList<EntityInterface>();
		for (EntityInterface mainEntity : mainEntityList)
		{
			if (mainEntity.isAbstract())
			{
				tmpMnEntityLst.addAll(QueryCsmBizLogic.getMainEntityList
					(mainEntity,deEntity));
			}
		}
		mainEntityList.addAll(tmpMnEntityLst);
		mainEntityMap.put(deEntity, mainEntityList);
	}

	/**
	 * This is a method that will create list of all main entities.
	 * (Entities for which entity passed to it is having containment association)
	 * @param entity entity
	 * @param mainEntityList mainEntityList
	 * @return mainEntityList List of main entities
	 */
	public static List<EntityInterface> getAllMainEntities(EntityInterface entity,
			List<EntityInterface> mainEntities)
	{
		List<EntityInterface> mainEntityList = mainEntities;
		try
		{
			List<AssociationInterface> associationList = getIncomingContainmentAssociations(entity);
			if (associationList.isEmpty())
			{
				mainEntityList.add(entity);
			}
			else
			{
				for (AssociationInterface assocoation : associationList)
				{
					mainEntityList=getAllMainEntities(assocoation.getEntity(), mainEntityList);
				}
			}
		}
		catch (DynamicExtensionsSystemException deException)
		{
			logger.error(deException.getMessage(),deException);
		}
		return mainEntityList;
	}

	/**
	 * This method will create queryResultObjectDataBean for a node passed to it.
	 * @param node node for which QueryResultObjectDataBean is to be created.
	 * @param queryDetailsObj QueryDetails object.
	 * @return queryResultObjectDataBean.
	 */
	public static QueryResultObjectDataBean getQueryResulObjectDataBean(OutputTreeDataNode node,
			QueryDetails queryDetailsObj)
	{
		QueryResultObjectDataBean queryResultObjectDataBean = new QueryResultObjectDataBean();
		if (node != null)
		{
            EntityInterface deEntity = node.getOutputEntity().getDynamicExtensionsEntity();
            String entityName;
            Map<String, String> tagKeyValueMap = getTaggedValueMap(deEntity);
            queryResultObjectDataBean.setPrivilegeType(Utility.getInstance().getPrivilegeType(tagKeyValueMap));
            queryResultObjectDataBean.setEntity(deEntity);

            List<EntityInterface> mainEntityList = getMainEntityListFromMap(
					queryDetailsObj, deEntity);
            if (mainEntityList == null)
            {
            	entityName = deEntity.getName();
            }
            else
            {
            	EntityInterface mainEntity = getMainEntity(mainEntityList, node);
                if(mainEntity == null)
                {
                	mainEntity = mainEntityList.get(0);
                }
                queryResultObjectDataBean.setMainEntity(mainEntity);
                entityName = mainEntity.getName();
            }
			queryResultObjectDataBean.setCsmEntityName(entityName);
			setEntityName(queryResultObjectDataBean);
			boolean readDeniedObject = isReadDeniedObject(queryResultObjectDataBean.getCsmEntityName());
			queryResultObjectDataBean.setReadDeniedObject(readDeniedObject);
		}
		return queryResultObjectDataBean;
	}

	/**
	 * @param queryDetailsObj queryDetailsObj
	 * @param deEntity deEntity
	 * @return mainEntityList
	 */
	private static List<EntityInterface> getMainEntityListFromMap(
			QueryDetails queryDetailsObj, EntityInterface deEntity)
	{
		List<EntityInterface> mainEntityList = null;
		if(queryDetailsObj.getMainEntityMap() != null)
		{
		    mainEntityList = queryDetailsObj.getMainEntityMap().get(
		            deEntity);
		}
		return mainEntityList;
	}

	/**
	 * @param deEntity deEntity
	 * @return tagKeyValueMap
	 */
	private static Map<String, String> getTaggedValueMap(
			EntityInterface deEntity)
	{
		Map<String,String> tagKeyValueMap = new HashMap<String, String>();
		Collection<TaggedValueInterface> taggedValueColl =
			deEntity.getTaggedValueCollection();
		for (TaggedValueInterface taggedValueInterface : taggedValueColl)
		{
		    tagKeyValueMap.put(taggedValueInterface.getKey(),taggedValueInterface.getValue());
		}
		return tagKeyValueMap;
	}

	/**If main entity is inherited from an entity (e.g. Fluid Specimen is inherited from Specimen)
	 * and present in INHERITED_ENTITY_NAMES
	 * then csmEntityName of queryResultObjectDataBean will be set to it's parent entity name.
	 * (as SQL for getting CP id's id retrieved
	 * according to parent entity name from entityCPSqlMap in Variables class).
	 * @param queryResultObjectDataBean queryResultObjectDataBean
	 */
	private static void setEntityName(QueryResultObjectDataBean queryResultObjectDataBean)
	{
	    if (queryResultObjectDataBean.getEntity().getInheritanceStrategy()
                .equals(InheritanceStrategy.TABLE_PER_HEIRARCHY))
        {
            EntityInterface parentEntity = queryResultObjectDataBean
                    .getEntity().getParentEntity();
            if (parentEntity != null)
            {
                queryResultObjectDataBean.setCsmEntityName(parentEntity
                        .getName());
            }
        }
	}

	/**This method will check if for an entity read denied has to checked or not.
	 * All theses entities are present in
	 * Variables.queryReadDeniedObjectList list.
	 * @param entityName entityName
	 * @return <CODE>true</CODE> its read denied object,
	 * <CODE>false</CODE> otherwise
	 */
	private static boolean isReadDeniedObject(String entityName)
	{
		boolean isReadDenied = false;
		if (Variables.queryReadDeniedObjectList.contains(entityName))
		{
			isReadDenied = true;
		}
		return isReadDenied;
	}

	/**
	 * Searches for main entity in parent hierarchy or child hierarchy.
	 * @param mainEntityList - list of all main Entities
	 * @param node - current node
	 * @return - main Entity if found in parent or child hierarchy. Returns null if not found
	 */
	private static EntityInterface getMainEntity(List<EntityInterface> mainEntityList,
			OutputTreeDataNode node)
	{
		//check if node itself is main entity
		EntityInterface entity = null;
		// check if main entity is present in parent hierarchy
		if (node.getParent() != null)
		{
			entity = getMainEntityFromParentHierarchy(mainEntityList, node.getParent());
		}
		if (entity == null)
		{
			//check if main entity is present in child hierarchy
			entity = getMainEntityFromChildHierarchy(mainEntityList, node);
		}
		return entity;
	}

	/**
	 * To check whether the given Entity in OutputTreeDataNode is mainEntity or not.
	 * @param mainEntityList the list of main entities.
	 * @param node the OutputTreeDataNode
	 * @return The reference to entity in the OutputTreeDataNode, if its present in the mainEntityList.
	 */
	private static EntityInterface isMainEntity(List<EntityInterface> mainEntityList,
			OutputTreeDataNode node)
	{
		EntityInterface dynamicExtensionsEntity = node.getOutputEntity()
				.getDynamicExtensionsEntity();
		if (!mainEntityList.contains(dynamicExtensionsEntity))
		{
			dynamicExtensionsEntity = null;
		}
		return dynamicExtensionsEntity;
	}

	/**
	 * Recursively checks in parent hierarchy for main entity.
	 * @param mainEntityList mainEntityList
	 * @param node node
	 * @return main Entity if found in parent Hierarchy
	 */
	private static EntityInterface getMainEntityFromParentHierarchy(
			List<EntityInterface> mainEntityList, OutputTreeDataNode node)
	{
		EntityInterface entity = isMainEntity(mainEntityList, node);
		if (entity == null)
		{
			if (node.getParent() != null)
			{
				entity = getFinalMainEntity(mainEntityList, node);
			}
			else if (node.getChildren().size() != 0)
			{
				entity = getMainEntityFromChildHierarchy(mainEntityList,node);
			}
		}
		return entity;
	}

	/**
	 * @param mainEntityList mainEntityList
	 * @param node node
	 * @return entity
	 */
	private static EntityInterface getFinalMainEntity(
			List<EntityInterface> mainEntityList, OutputTreeDataNode node)
	{
		EntityInterface entity;
		entity = getMainEntityFromParentHierarchy(mainEntityList, node.getParent());
		if(entity == null && node.getChildren().size() !=0)
		{
			entity = getMainEntityFromChildHierarchy(mainEntityList,node);
		}
		return entity;
	}

	/**
	 * Recursively checks in child hierarchy for main entity.
	 * @param mainEntityList mainEntityList
	 * @param node node
	 * @return main Entity if found in child Hierarchy
	 */
	private static EntityInterface getMainEntityFromChildHierarchy(
			List<EntityInterface> mainEntityList, OutputTreeDataNode node)
	{
		EntityInterface entity = isMainEntity(mainEntityList, node);
		if (entity == null)
		{
			List<OutputTreeDataNode> children = node.getChildren();

			for (OutputTreeDataNode childNode : children)
			{
				entity = getMainEntityFromChildHierarchy(mainEntityList, childNode);
				if (entity != null)
				{
					break;
				}
			}
		}
		return entity;
	}

	/**
	 * This method will internally call  getIncomingAssociationIds of DE which will return
	 * all incoming associations
	 * for entities passed.This method will filter out all incoming containment associations
	 * and return list of them.
	 * @param entity entity
	 * @throws DynamicExtensionsSystemException DynamicExtensionsSystemException
	 * @return list List of incoming containment associations.
	 */
	public static List<AssociationInterface> getIncomingContainmentAssociations(
			EntityInterface entity) throws DynamicExtensionsSystemException
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		ArrayList<Long> allIds = (ArrayList<Long>) entityManager.getIncomingAssociationIds(entity);
		List<AssociationInterface> list = new ArrayList<AssociationInterface>();
		for (Long id : allIds)
		{
			AssociationInterface associationById = EntityCache.getInstance().getAssociationById(id);

			RoleInterface targetRole = associationById.getTargetRole();
			if (targetRole.getAssociationsType().getValue().equals(
							AQConstants.CONTAINTMENT_ASSOCIATION))
			{
				list.add(associationById);
			}
		}
		return list;
	}

	/**
	 * @param queryResultObjectDataBean queryResultObjectDataBean
	 * @param colIndex colIndex
	 * @param tempSql tempSql
	 * @param defineViewNodeList defineViewNodeList
	 * @param entityIdIndexMap entityIdIndexMap
	 * @param queryDetailsObj queryDetailsObj
	 * @return selectSql selectSql
	 */
	public static String updateEntityIdIndexMap(
			QueryResultObjectDataBean queryResultObjectDataBean, int colIndex, String tempSql,
			List<EntityInterface> defineViewNodeList,
			Map<EntityInterface, Integer> entityIdIndexMap, QueryDetails queryDetailsObj, Map<String, String> specimenMap)
	{
		String selectSql = tempSql;
		int columnIndex = colIndex;
		List<String> selectSqlColumnList = getListOfSelectedColumns(selectSql);
		Map<String, String> columnNameVsAliasMap = queryDetailsObj.getColumnNameVsAliasMap();
		if (defineViewNodeList == null)
		{
			OutputTreeDataNode outputTreeDataNode = getMatchingEntityNode(queryResultObjectDataBean
					.getMainEntity(), queryDetailsObj);
			Map sqlIndexMap = putIdColumnsInSql(columnIndex, selectSql, entityIdIndexMap,
					selectSqlColumnList, outputTreeDataNode,specimenMap, columnNameVsAliasMap);
			selectSql = (String) sqlIndexMap.get(AQConstants.SQL);
			columnIndex = (Integer) sqlIndexMap.get(AQConstants.ID_COLUMN_ID);
		}
		else
		{
			Set<String> keySet = queryDetailsObj.getUniqueIdNodesMap().keySet();
			for (Object nextObject : keySet)
			{
				if (nextObject instanceof String)
				{
					String key = (String) nextObject;
					OutputTreeDataNode outputTreeDataNode =
						queryDetailsObj.getUniqueIdNodesMap().get(key);
					Map sqlIndexMap = putIdColumnsInSql
					(columnIndex, selectSql, entityIdIndexMap,
							selectSqlColumnList, outputTreeDataNode,specimenMap, columnNameVsAliasMap);
					selectSql = (String) sqlIndexMap.get(AQConstants.SQL);
					columnIndex = (Integer) sqlIndexMap.get(AQConstants.ID_COLUMN_ID);
				}
			}
		}
		populateBean(queryResultObjectDataBean, entityIdIndexMap);
		return selectSql;
	}

	/**
	 * @param queryResultObjectDataBean queryResultObjectDataBean
	 * @param entityIdIndexMap entityIdIndexMap
	 */
	private static void populateBean(
			QueryResultObjectDataBean queryResultObjectDataBean,
			Map<EntityInterface, Integer> entityIdIndexMap)
	{
		if (queryResultObjectDataBean != null)
		{
			queryResultObjectDataBean.setEntityIdIndexMap(entityIdIndexMap);
			setMainProtocolIdIndex(queryResultObjectDataBean);
			if (entityIdIndexMap.get(queryResultObjectDataBean.getMainEntity()) != null)
			{
				queryResultObjectDataBean.setMainEntityIdentifierColumnId(entityIdIndexMap
						.get(queryResultObjectDataBean.getMainEntity()));
			}
		}
	}

	/**
	 * To add the Id columns of MainEntities in the SQL if its not present.
	 * It will also populate entityIdIndexMap passes it.
	 * @param columnIndex columnIndex
	 * @param selectSql selectSql
	 * @param entityIdIndexMap entityIdIndexMap
	 * @param selectSqlColumnList selectSqlColumnList
	 * @param outputTreeDataNode outputTreeDataNode
	 * @return The modified SQL string.
	 */
	private static Map putIdColumnsInSql(int colIndex, String selectSql,
			Map<EntityInterface, Integer> entityIdIndexMap, List<String> selectSqlColumnList,
			OutputTreeDataNode outputTreeDataNode, Map<String, String> specimenMap, Map<String, String> columnNameVsAliasMap)
	{
		int columnIndex = colIndex;
		StringBuffer sql = new StringBuffer(selectSql);
		Map sqlIndexMap = new HashMap();
		if (outputTreeDataNode != null)
		{
			List<QueryOutputTreeAttributeMetadata> attributes = outputTreeDataNode.getAttributes();
			for (QueryOutputTreeAttributeMetadata attributeMetaData : attributes)
			{
				AttributeInterface attribute = attributeMetaData.getAttribute();
				String sqlColumnName = attributeMetaData.getColumnName().trim();
				if (attribute.getName().equals(AQConstants.IDENTIFIER))
				{
					int index = selectSqlColumnList.indexOf(columnNameVsAliasMap.get(sqlColumnName)+" "+sqlColumnName);

					if (index >= 0)
					{
						entityIdIndexMap.put(attribute.getEntity(), index);
						break;
					}
					else
					{
						//appendColNameToSql(selectSql, sql, sqlColumnName);
						entityIdIndexMap.put(attribute.getEntity(), null);
						columnIndex++;
						if(outputTreeDataNode.getOutputEntity().getDynamicExtensionsEntity().getName().equals("edu.wustl.catissuecore.domain.Specimen"))
						{
							if(specimenMap == null)
							{
								specimenMap = new HashMap<String, String>();
							}
							specimenMap.put("specimenKey", "edu.wustl.catissuecore.domain.Specimen");
							specimenMap.put("columnIndex", String.valueOf(columnIndex-1));
						}
						break;
					}
				}
			}
		}
		sqlIndexMap.put(AQConstants.SQL, sql.toString());
		sqlIndexMap.put(AQConstants.ID_COLUMN_ID, columnIndex);
		return sqlIndexMap;
	}

	/**
	 * @param selectSql selectSql
	 * @param sql sql
	 * @param sqlColumnName sqlColumnName
	 */
	public static void appendColNameToSql(String selectSql, StringBuffer sql,
			String sqlColumnName)
	{
		if ("".equals(selectSql))
		{
			sql.append(selectSql).append(sqlColumnName);
		}
		else
		{
			sql.append(", ").append(sqlColumnName);
		}
	}

	/**
	 * TO the list of selectColumn Names in the selectSql.
	 * @param selectSql the Select part of SQL.
	 * @return The list of selectColumn Names in the selectSql.
	 */
	private static List<String> getListOfSelectedColumns(String selectSql)
	{
		String[] selectSqlColumnArray = selectSql.split(",");
		List<String> selectSqlColumnList = new ArrayList<String>();
		for (String element : selectSqlColumnArray)
		{
			selectSqlColumnList.add(element.trim());
		}
		return selectSqlColumnList;
	}

	/**This method will return node corresponding to an entity from query.
	 * @param entity entity
	 * @param queryDetailsObj queryDetailsObj
	 * @return outputTreeDataNode outputTreeDataNode
	 */
	private static OutputTreeDataNode getMatchingEntityNode(EntityInterface entity,
			QueryDetails queryDetailsObj)
	{
		OutputTreeDataNode outputTreeDataNode = null;
		Iterator<OutputTreeDataNode> iterator = queryDetailsObj.getUniqueIdNodesMap().values()
				.iterator();
		while (iterator.hasNext())
		{
			OutputTreeDataNode outputNode = iterator.next();
			if (outputNode.getOutputEntity().getDynamicExtensionsEntity().equals(entity))
			{
				outputTreeDataNode = outputNode;
				break;
			}
		}
		return outputTreeDataNode;
	}
	
	public static void setRequestAttr(HttpServletRequest request,
			Map<String, String> specimenMap) {
		Iterator<String> itr = specimenMap.keySet().iterator();
		while(itr.hasNext())
		{
			String key = itr.next();
			if(key.equals("specimenKey"))
			{
				request.getSession().removeAttribute("entityName");
				request.setAttribute("isSpecPresent", Boolean.TRUE);
			}
			else
				request.setAttribute("isSpecPresent", Boolean.FALSE);
			if(key.equals("columnIndex"))
			{
				request.setAttribute("specIdColumnIndex", specimenMap.get(key));
			}
		}
	}
	/**
	 * This method sets index of main protocol object's identifier column from the data list.
	 * 
	 * @param queryResultObjectDataBean
	 */
	public static void setMainProtocolIdIndex(
			QueryResultObjectDataBean queryResultObjectDataBean) {
		Integer cpIdIndex = null;
		Iterator<EntityInterface> iterator = queryResultObjectDataBean.getEntityIdIndexMap().keySet().iterator();
		while(iterator.hasNext())
		{
			EntityInterface next = iterator.next();
			if(next.getName().equalsIgnoreCase(Variables.mainProtocolObject))
			{
				cpIdIndex = queryResultObjectDataBean.getEntityIdIndexMap().get(next);
				if(cpIdIndex != null)
					queryResultObjectDataBean.setMainProtocolIdIndex(cpIdIndex);
				break;
			}
		}
	}
}