
package edu.wustl.query.flex.dag;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.client.ui.dag.PathLink;
import edu.wustl.cab2b.client.ui.dag.ambiguityresolver.AmbiguityObject;
import edu.wustl.cab2b.client.ui.query.ClientQueryBuilder;
import edu.wustl.cab2b.client.ui.query.IClientQueryBuilderInterface;
import edu.wustl.cab2b.client.ui.query.IPathFinder;
import edu.wustl.cab2b.common.beans.MatchedClass;
import edu.wustl.cab2b.common.beans.MatchedClassEntry;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.locator.Position;
import edu.wustl.common.query.queryobject.locator.QueryNodeLocator;
import edu.wustl.common.querysuite.exceptions.CyclicException;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.metadata.associations.IAssociation;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.metadata.path.IPath;
import edu.wustl.common.querysuite.queryobject.ArithmeticOperator;
import edu.wustl.common.querysuite.queryobject.IArithmeticOperand;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConnector;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IDateLiteral;
import edu.wustl.common.querysuite.queryobject.IDateOffsetAttribute;
import edu.wustl.common.querysuite.queryobject.IDateOffsetLiteral;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionAttribute;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IQueryEntity;
import edu.wustl.common.querysuite.queryobject.ITerm;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.TimeInterval;
import edu.wustl.common.querysuite.queryobject.impl.DateLiteral;
import edu.wustl.common.querysuite.queryobject.impl.DateOffsetAttribute;
import edu.wustl.common.querysuite.queryobject.impl.DateOffsetLiteral;
import edu.wustl.common.querysuite.queryobject.impl.Expression;
import edu.wustl.common.querysuite.queryobject.impl.ExpressionAttribute;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.common.querysuite.queryobject.impl.Rule;
import edu.wustl.common.querysuite.utils.QueryUtility;
import edu.wustl.common.util.Collections;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.bizlogic.CreateQueryObjectBizLogic;
import edu.wustl.query.flex.dag.CustomFormulaNode;
import edu.wustl.query.flex.dag.CustomFormulaUIBean;
import edu.wustl.query.flex.dag.DAGConstant;
import edu.wustl.query.flex.dag.DAGNode;
import edu.wustl.query.flex.dag.DAGPath;
import edu.wustl.query.flex.dag.DAGResolveAmbiguity;
import edu.wustl.query.flex.dag.JoinFormulaNode;
import edu.wustl.query.flex.dag.JoinFormulaUIBean;
import edu.wustl.query.flex.dag.JoinQueryNode;
import edu.wustl.query.flex.dag.SingalNodeTemporalQuery;
import edu.wustl.query.flex.dag.SingleNodeCustomFormulaNode;
import edu.wustl.query.flex.dag.TwoNodesTemporalQuery;
import edu.wustl.query.htmlprovider.HtmlProvider;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.Variables;
import edu.wustl.query.util.querysuite.DAOUtil;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import edu.wustl.query.util.querysuite.QueryDetails;
import edu.wustl.query.util.querysuite.QueryModuleError;
import edu.wustl.query.util.querysuite.QueryModuleSearchQueryUtil;
import edu.wustl.query.util.querysuite.QueryModuleUtil;
import edu.wustl.query.util.querysuite.TemporalQueryUtility;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;

/**
 *The class is responsible controlling all activities of Flex DAG.
 *@author aniket_pandit
 */

public class DAGPanel
{
	private static org.apache.log4j.Logger logger = LoggerConfig
	.getConfiguredLogger(DAGPanel.class);

	/**
	 * mQueryObject.
	 */
	private IClientQueryBuilderInterface mQueryObject;

	/**
	 * mPathFinder.
	 */
	private final IPathFinder mPathFinder;

	/**
	 * expression.
	 */
	private IExpression expression;

	/**
	 * mPathMap.
	 */
	private final HashMap<String, IPath> mPathMap = new HashMap<String, IPath>();

	/**
	 * positionMap.
	 */
	private Map<Integer, Position> positionMap;

	/**
	 * jQFormulaMap.
	 */
	private Map<String, JoinFormulaUIBean> jQFormulaMap = null;

	/**
	 * customFormulaMap.
	 */
	private Map<Integer, ICustomFormula> customFormulaMap = new HashMap<Integer, ICustomFormula>();

	/**
	 * customFormulaMapcounter.
	 */
	private int customFormulaMapcounter = 0;

	/**
	 * counter.
	 */
	private int counter = 0;

	/**
	 * finalRootOutputTreeNodeList.
	 */
	public List<OutputTreeDataNode> finalRootOutputTreeNodeList = new ArrayList<OutputTreeDataNode>();

	/**
	 * @return finalRootOutputTreeNodeList finalRootOutputTreeNodeList
	 */
	public List<OutputTreeDataNode> getFinalRootOutputTreeNodeList()
	{
		return finalRootOutputTreeNodeList;
	}

	/**
	 * @param finalRootOutputTreeNodeList finalRootOutputTreeNodeList
	 */
	public void setFinalRootOutputTreeNodeList(List<OutputTreeDataNode> finalRootOutputTreeNodeList)
	{
		this.finalRootOutputTreeNodeList = finalRootOutputTreeNodeList;
	}

	/**
	 * @param pathFinder pathFinder
	 */
	public DAGPanel(IPathFinder pathFinder)
	{
		mPathFinder = pathFinder;
	}

	/**
	 * Create node.
	 * @param expressionId expressionId
	 * @param isOutputView isOutputView
	 * @return dagNode The node that's created
	 */
	private DAGNode createNode(int expressionId, boolean isOutputView)
	{
		IExpression expression = mQueryObject.getQuery().getConstraints().getExpression(
				expressionId);
		IQueryEntity constraintEntity = expression.getQueryEntity();
		DAGNode dagNode = new DAGNode();
		dagNode.setNodeName(edu.wustl.cab2b.common.util.Utility.getOnlyEntityName(constraintEntity
				.getDynamicExtensionsEntity()));
		dagNode.setExpressionId(expression.getExpressionId());
		if (isOutputView)
		{
			dagNode.setNodeType(DAGConstant.VIEW_ONLY_NODE);
		}
		else
		{
			dagNode.setToolTip(expression);
		}
		return dagNode;
	}

	/**
	 * Create query object.
	 * @param queryString strToCreateQueryObject
	 * @param entityName entityName
	 * @param mode mode(Add/Edit)
	 * @param queryDetailsObj queryDetailsObj
	 * @param operatorValueMap Map of operator and the values
	 * @return node The node created
	 */

	public DAGNode createQueryObject(String queryString, String entityName,
		String mode, QueryDetails queryDetailsObj,
		Map<RelationalOperator,List<String>> operatorValueMap)
	{
		DAGNode node = null;
		EntityInterface entity;
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		HttpSession session = null;
		IQuery query;
		if(request == null && queryDetailsObj != null)
		{
			query = queryDetailsObj.getQuery();
		}
		else
		{
			session = request.getSession();
			// Get existing Query object from server
			query = (IQuery) session.getAttribute(DAGConstant.QUERY_OBJECT);
		}
		query = setQueryObject(query);
		if(session!=null)
		{
			session.setAttribute(DAGConstant.QUERY_OBJECT, query);
		}
		IExpression originalRootExp = null;
		try
		{
			if(queryDetailsObj != null)
			{
				originalRootExp = query.getConstraints().getRootExpression();
			}
			Long entityId = Long.parseLong(entityName);
			entity = EntityCache.getCache().getEntityById(entityId);
			if((queryString != null && mode.equalsIgnoreCase(AQConstants.ADD))
					|| (queryDetailsObj == null))
			{
				CreateQueryObjectBizLogic queryBizLogic = new CreateQueryObjectBizLogic();
				node = createQueryObjectLogic(queryString, mode,
						 entity, queryBizLogic);
			}
			if(queryDetailsObj != null)
			{
				modifyQueryObject(queryString, entityName, mode, queryDetailsObj,
						operatorValueMap,query,originalRootExp,node);
			}
		}
		catch (DynamicExtensionsSystemException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (DynamicExtensionsApplicationException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (MultipleRootsException e)
		{
			logger.error(e.getMessage(), e);
		}
		return node;
	}

	/**
	 * @param mQuery mQuery
	 * @return query
	 */
	private IQuery setQueryObject(IQuery mQuery)
	{
		IQuery query = mQuery;
		if(mQueryObject == null)
		{
			IClientQueryBuilderInterface queryObject = new ClientQueryBuilder();
			mQueryObject = queryObject;
		}
		if (query == null)
		{
			query = mQueryObject.getQuery();
		}
		else
		{
			mQueryObject.setQuery(query);
		}
		return query;
	}

	/**
	 * Modify query object by adding main entity if its not present
	 * and also adding main protocol object and link it if its not present in the query.
	 * @param queryObject strToCreateQueryObject
	 * @param entityName entityName
	 * @param mode mode
	 * @param queryDetailsObj queryDetailsObj
	 * @param operatorValueMap operatorAndLstOfValues
	 * @param query query
	 * @param originalRootExp originalRootExp
	 */
	private void modifyQueryObject(String queryObject, String entityName, String mode,
			QueryDetails queryDetailsObj,
			Map<RelationalOperator, List<String>> operatorValueMap,
			IQuery query, IExpression originalRootExp,DAGNode node)
	{
		String queryString = queryObject;
		int secondExpId = 0;
		int rootExpId=0;
		boolean isPathPresent = true;
		EntityInterface deEntity = null;
		IConstraints constraints = query.getConstraints();
		for(IExpression expression : constraints)
		{
			IQueryEntity queryEntity = expression.getQueryEntity();
			deEntity = queryEntity.getDynamicExtensionsEntity();
			Long deEntityId = deEntity.getId();
			if(String.valueOf(deEntityId).equals(entityName))
			{
				secondExpId = expression.getExpressionId();
				break;
			}
		}
		Map<EntityInterface, List<EntityInterface>> mainEntityMap =
			queryDetailsObj.getMainEntityMap();
		Set<EntityInterface> keySet = mainEntityMap.keySet();
		Iterator<EntityInterface> iterator = keySet.iterator();
		if(queryString != null && node != null)
		{
			rootExpId = addMainEntityExpression(secondExpId, deEntity, constraints,
					mainEntityMap, iterator);
		}
		if(rootExpId != 0 && secondExpId != 0 && queryString != null)
		{
			isPathPresent = linkInvisibleNodes(secondExpId, rootExpId, constraints);
			if(!isPathPresent)
			{
				isPathPresent = linkInvisibleNodes( originalRootExp.getExpressionId(), secondExpId, constraints);
			}
		}
		if(queryDetailsObj.getSessionData().isSecurityRequired())
		{
			if(queryString == null)
			{
				queryString = "";
			}
			Set<IQueryEntity> queryEntities = constraints.getQueryEntities();
			boolean isMainObjPresent = isMainObjectPresent(queryEntities);
			if((mode.equalsIgnoreCase(AQConstants.EDIT)) ||
					(!isMainObjPresent && mode.equalsIgnoreCase(AQConstants.ADD)))
			{
				int mainProtocolExpId = addMainProtocolObjectInQuery
				(query,queryDetailsObj.getSessionData().getUserName(),mode,
						operatorValueMap,queryString);
				if((mainProtocolExpId != -1) && mode.equalsIgnoreCase(AQConstants.ADD))
				{
					IExpression mainProtocol =
						constraints.getExpression(mainProtocolExpId);
					mainProtocol.setInView(false);
					isPathPresent = linkInvisibleNodes(mainProtocolExpId, secondExpId, constraints);
					if(!isPathPresent)
					{
						isPathPresent = linkInvisibleNodes(originalRootExp.getExpressionId(),mainProtocolExpId, constraints);
					}
				}
			}
		}
	}

	/**
	 * @param queryEntities queryEntities
	 * @return isMainObjPresent
	 */
	private boolean isMainObjectPresent(Set<IQueryEntity> queryEntities)
	{
		boolean isMainObjPresent = false;
		for(IQueryEntity queryEntity : queryEntities)
		{
			if(queryEntity.getDynamicExtensionsEntity().getName().
					equals(Variables.mainProtocolObject))
			{
				isMainObjPresent = true;
				break;
			}
		}
		return isMainObjPresent;
	}

	/**
	 * Adds the main entity in the query and links it to the existing node.
	 * @param secondExpId secondExpId
	 * @param tEntity deEntity
	 * @param constraints constraints
	 * @param mainEntityMap mainEntityMap
	 * @param iterator iterator
	 * @return rootExpId rootExpId
	 */
	private int addMainEntityExpression(int secondExpId, EntityInterface tEntity,
			IConstraints constraints, Map<EntityInterface, List<EntityInterface>> mainEntityMap,
			Iterator<EntityInterface> iterator)
	{
		EntityInterface deEntity = tEntity;
		int rootExpId = 0;
		while(iterator.hasNext())
		{
			EntityInterface tempEntity = iterator.next();
			List<EntityInterface> mainEntityList = mainEntityMap.get(tempEntity);
			List<EntityInterface> mainEntityList1 = getMainEntityList1(mainEntityList);
			mainEntityList = mainEntityList1;
			for(EntityInterface mainEntity : mainEntityList)
			{
				if(mainEntity.equals(deEntity))
				{
					for(IExpression expression : constraints)
					{
						IQueryEntity queryEntity = expression.getQueryEntity();
						deEntity = queryEntity.getDynamicExtensionsEntity();
						if(deEntity.equals(tempEntity))
						{
							IExpression mainEntityExpression =
								constraints.getExpression(secondExpId);
							rootExpId = expression.getExpressionId();
							mainEntityExpression.setInView(false);
							break;
						}
					}
				}
			}
		}
		return rootExpId;
	}

	/**
	 * @param mainEntityList mainEntityList
	 * @return mainEntityList1
	 */
	private List<EntityInterface> getMainEntityList1(
			List<EntityInterface> mainEntityList)
	{
		List<EntityInterface> mainEntityList1 = new ArrayList<EntityInterface>();
		for(EntityInterface mainEntity : mainEntityList)
		{
			if(!mainEntityList1.contains(mainEntity))
			{
				mainEntityList1.add(mainEntity);
			}
		}
		return mainEntityList1;
	}

	/**
	 * Adds main protocol object(CP/CS) in query.
	 * @param query query
	 * @param userName userName
	 * @param mode mode
	 * @param operatorValueMap operatorAndLstOfValues
	 * @param strToCreateObject strToCreateObject
	 * @return mainProtocolExpId mainProtocolExpId
	 */
	private int addMainProtocolObjectInQuery(IQuery query, String userName,
	String mode, Map<RelationalOperator,List<String>> operatorValueMap,String strToCreateObject)
	{
		List<Long> readDeniedIds = new ArrayList<Long>();
		List<Long> allMainObjectIds = new ArrayList<Long>();
		allMainObjectIds = populateAllMainProtocolObjectIds(userName, readDeniedIds);

		EntityCache cache = EntityCacheFactory.getInstance();
		EntityInterface mainProtocol = DomainObjectFactory.getInstance().createEntity();
		mainProtocol.setName(Variables.mainProtocolObject.
		substring(Variables.mainProtocolObject.lastIndexOf('.')+1, Variables.mainProtocolObject.length()));
		mainProtocol.setDescription(null);
		Long entityId = null;
		Long attributeId = null;
		StringBuffer formattedIds = new StringBuffer();
		Collection<EntityInterface> entityCollection = new HashSet<EntityInterface>();
		entityCollection.add(mainProtocol);
		MatchedClass matchedClass = cache.getEntityOnEntityParameters(entityCollection);
		MatchedClass resultantMatchedClass = new MatchedClass();
		for (MatchedClassEntry matchedClassEntry : matchedClass.getMatchedClassEntries())
		{
			resultantMatchedClass.addMatchedClassEntry(matchedClassEntry);
		}
		resultantMatchedClass.setEntityCollection(resultantMatchedClass.getSortedEntityCollection());
		for(EntityInterface entity : resultantMatchedClass.getEntityCollection())
		{
			if(entity.getName().equals(Variables.mainProtocolObject))
			{
				mainProtocol = entity;
				attributeId = mainProtocol.getAbstractAttributeByName("id").getId();
				entityId = mainProtocol.getId();
				break;
			}
		}
		if(!readDeniedIds.isEmpty())
		{
			if(operatorValueMap != null && !operatorValueMap.isEmpty())
			{
					strToCreateObject = modifyQueryForIdCondition(operatorValueMap,
				strToCreateObject, readDeniedIds,allMainObjectIds, formattedIds,attributeId);
			}
			else
			{
				for(Long csId : readDeniedIds)
				{
					formattedIds.append('&').append(String.valueOf(csId));
				}
			}
		}
		else
		{
			
			return -1;
			/*if(operatorValueMap != null && !operatorValueMap.isEmpty())
			{
				Set<RelationalOperator> keySet = operatorValueMap.keySet();
				Iterator<RelationalOperator> iterator = keySet.iterator();
				if(iterator.hasNext())
				{
					RelationalOperator operator = iterator.next();
					List<String> conditionValues = operatorValueMap.get(operator);
					strToCreateObject = AQConstants.ID_CONDITION+attributeId+
					AQConstants.CONDITION_SEPERATOR+operator+AQConstants.CONDITION_SEPERATOR;
					for(String condition : conditionValues)
					{
						strToCreateObject = strToCreateObject+AQConstants.CONDITION_SEPERATOR+condition;
					}
					strToCreateObject = strToCreateObject+";";
				}
			}*/
		}
		int mainProtocolExpId = 0;
		EntityInterface deEntity = null;

		CreateQueryObjectBizLogic queryBizLogic = new CreateQueryObjectBizLogic();
		DAGNode node = null;
		try
		{
			if(operatorValueMap == null || ((operatorValueMap != null)
					&& (operatorValueMap.isEmpty())))
			{
				strToCreateObject = populateStrToCreateObject(strToCreateObject, readDeniedIds,
						attributeId, formattedIds);
			}
			if(mode.equalsIgnoreCase(AQConstants.EDIT))
			{
				getMnProtocolExpForEdit(query, entityId);
			}
			node = createQueryObjectLogic(strToCreateObject, mode,
					mainProtocol, queryBizLogic);
			IConstraints constraints = query.getConstraints();
			for(IExpression expression : constraints)
			{
				IQueryEntity queryEntity = expression.getQueryEntity();
				deEntity = queryEntity.getDynamicExtensionsEntity();
				Long deEntityId = deEntity.getId();
				if(deEntityId.equals(entityId))
				{
					mainProtocolExpId = expression.getExpressionId();
					break;
				}
			}
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
		}
		return mainProtocolExpId;
	}

	/**
	 * @param entityId entityId
	 * @param mainExpId mainExpId
	 * @param constraints constraints
	 * @return mainProtocolExpId
	 */
	private int getMainExpId(Long entityId, int mainExpId,
			IConstraints constraints)
	{
		int mainProtocolExpId = mainExpId;
		EntityInterface deEntity;
		for(IExpression expression : constraints)
		{
			IQueryEntity queryEntity = expression.getQueryEntity();
			deEntity = queryEntity.getDynamicExtensionsEntity();
			Long deEntityId = deEntity.getId();
			if(deEntityId.equals(entityId))
			{
				mainProtocolExpId = expression.getExpressionId();
				break;
			}
		}
		return mainProtocolExpId;
	}

	/**
	 * @param readDeniedIds readDeniedIds
	 * @param formattedIds formattedIds
	 */
	private void populateFormattedIds(List<Long> readDeniedIds,
			StringBuffer formattedIds)
	{
		for(Long csId : readDeniedIds)
		{
			formattedIds.append('&').append(String.valueOf(csId));
		}
	}

	/**
	 * @param cache cache
	 * @param mainProtocol mainProtocol
	 * @return resultantMatchedClass
	 */
	private MatchedClass getResultantMatchedClass(EntityCache cache,
			EntityInterface mainProtocol)
	{
		Collection<EntityInterface> entityCollection = new HashSet<EntityInterface>();
		entityCollection.add(mainProtocol);
		MatchedClass matchedClass = cache.getEntityOnEntityParameters(entityCollection);
		MatchedClass resultantMatchedClass = new MatchedClass();
		for (MatchedClassEntry matchedClassEntry : matchedClass.getMatchedClassEntries())
		{
			resultantMatchedClass.addMatchedClassEntry(matchedClassEntry);
		}
		resultantMatchedClass.setEntityCollection(resultantMatchedClass.getSortedEntityCollection());
		return resultantMatchedClass;
	}

	/**
	 * Set the this.expression to the main protocol object already present
	 * in the query for editing its conditions.
	 * @param query query
	 * @param entityId entityId
	 */
	private void getMnProtocolExpForEdit(IQuery query, Long entityId)
	{
		EntityInterface deEntity;
		IConstraints constraints = query.getConstraints();
		for(IExpression expression : constraints)
		{
			IQueryEntity queryEntity = expression.getQueryEntity();
			deEntity = queryEntity.getDynamicExtensionsEntity();
			Long deEntityId = deEntity.getId();
			if(deEntityId.equals(entityId))
			{
				this.expression = expression;
				break;
			}
		}
	}

	/**
	 * Populates the string to create node for main protocol object(CS/CP).
	 * @param strToCreateObject strToCreateObject
	 * @param readDeniedIds readDeniedIds
	 * @param attributeId attributeId
	 * @param formattedIds formattedIds
	 * @return strToCreateObject
	 */
	private String populateStrToCreateObject(String strToCreateObject, List<Long> readDeniedIds,
			Long attributeId, StringBuffer formattedIds)
	{
		String tempStr;
		if(readDeniedIds.isEmpty())
		{
			tempStr = strToCreateObject + AQConstants.ID_CONDITION+attributeId+
			AQConstants.CONDITION_SEPERATOR+"Is Not Null;";
		}
		else
		{
			tempStr = strToCreateObject + AQConstants.ID_CONDITION+attributeId+
			AQConstants.CONDITION_SEPERATOR+"Not In"+AQConstants.CONDITION_SEPERATOR+formattedIds+";";
		}
		return tempStr;
	}

	/**
	 * Get the main protocol ids from the privilege cache.
	 * @param userName userName
	 * @param readDeniedIds readDeniedIds
	 * @return allMainObjectIds allMainObjectIds
	 */
	private List<Long> populateAllMainProtocolObjectIds(String userName, List<Long> readDeniedIds)
	{
		HibernateDAO hibernateDAO = null;
		List<Long> allMainObjectIds = new ArrayList<Long>();
		try
		{
			PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
			PrivilegeCache privilegeCache = privilegeManager.getPrivilegeCache(userName);
			Map<String, List<NameValueBean>>  privilegeMap =
				privilegeCache.getPrivilegesforPrefix(Variables.mainProtocolObject+"_");
			Set<String> keySet = privilegeMap.keySet();
			Iterator<String> privilegeMapItr = keySet.iterator();
			while(privilegeMapItr.hasNext())
			{
				populateReadDeniedIds(readDeniedIds, privilegeMap, privilegeMapItr);
			}
			hibernateDAO = DAOUtil.getHibernateDAO(null);
			String sql = Variables.mainProtocolQuery;
			if(sql != null)
			{
				allMainObjectIds = hibernateDAO.executeQuery(sql);
			}
		}
		catch (SMException e1)
		{
			logger.error(e1.getMessage(), e1);
		}
		catch(DAOException dao)
		{
			logger.error(dao.getMessage(), dao);
		}
		finally
		{
			try
			{
				DAOUtil.closeHibernateDAO(hibernateDAO);
			}
			catch (DAOException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
		return allMainObjectIds;
	}

	/**
	 * Get the ids of the main protocols on which the current logged in user has
	 * 'READ_DENIED' privilege.
	 * @param readDeniedIds readDeniedIds
	 * @param privilegeMap privilegeMap
	 * @param privilegeMapItr privilegeMapItr
	 */
	private void populateReadDeniedIds(List<Long> readDeniedIds,
			Map<String, List<NameValueBean>> privilegeMap, Iterator<String> privilegeMapItr)
	{
		String objectId = privilegeMapItr.next();
		List<NameValueBean> privilegeList = privilegeMap.get(objectId);
		for(NameValueBean privilegeName : privilegeList)
		{
			if(privilegeName.getName().equalsIgnoreCase("READ_DENIED"))
			{
				String identifier = objectId.substring(objectId.lastIndexOf('_')+1,
						objectId.length());
				readDeniedIds.add(Long.valueOf(identifier));
			}
		}
	}

	/**
	 * If the query contains main protocol and there's a condition on its 'id' attribute,
	 * the id condition is modified such that the read denied ids are not retrieved from the query.
	 * @param operatorAndLstOfValues operatorAndLstOfValues
	 * @param strToCreateObject strToCreateObject
	 * @param readDeniedIds readDeniedIds
	 * @param allMainProtocolIds allMainProtocolIds
	 * @param formattedIds formattedIds
	 * @param attributeId attributeId
	 * @return strToCreateObject strToCreateObject
	 */
	private String modifyQueryForIdCondition(
			Map<RelationalOperator, List<String>> operatorAndLstOfValues, String strToCreateObject,
			List<Long> readDeniedIds, List<Long> allMainProtocolIds,
			StringBuffer formattedIds,Long attributeId)
	{
		Set<RelationalOperator> keySet = operatorAndLstOfValues.keySet();
		Iterator<RelationalOperator> iterator = keySet.iterator();
		if(iterator.hasNext())
		{
			RelationalOperator operator = iterator.next();
			List<String> conditionValues = operatorAndLstOfValues.get(operator);
			if(operator.equals(RelationalOperator.NotIn))
			{
				strToCreateObject = modifyConditionForNotIn(strToCreateObject, readDeniedIds,
						formattedIds, attributeId, conditionValues);
			}
			else if (operator.equals(RelationalOperator.Equals) ||
					operator.equals(RelationalOperator.NotEquals))
			{
				strToCreateObject = modifyConditionForEqNotEq(strToCreateObject, readDeniedIds,
						formattedIds, attributeId, operator, conditionValues);
			}
			else if(operator.equals(RelationalOperator.In))
			{
				modifyConditionForIn(readDeniedIds, formattedIds, conditionValues);
			}
			else if(operator.equals(RelationalOperator.Between))
			{
				modifyConditionForBetween(readDeniedIds, allMainProtocolIds, formattedIds,
						conditionValues);
			}
			else if(operator.equals(RelationalOperator.GreaterThan))
			{
				modifyCOnditionForGreaterThan(readDeniedIds, allMainProtocolIds, formattedIds,
						conditionValues);
			}
			else if(operator.equals(RelationalOperator.GreaterThanOrEquals))
			{
				modifyConditionForGreaterThanEquals(readDeniedIds, allMainProtocolIds,
						formattedIds, conditionValues);
			}
			else if (operator.equals(RelationalOperator.LessThan))
			{
				modifyConditionForLessThan(readDeniedIds, allMainProtocolIds, formattedIds,
						conditionValues);
			}
			else if (operator.equals(RelationalOperator.LessThanOrEquals))
			{
				modifyCnditionForLessThanEquals(readDeniedIds, allMainProtocolIds, formattedIds,
						conditionValues);
			}
			if(strToCreateObject.length() == 0)
			{
				if(formattedIds.length() ==0)
				{
					strToCreateObject = strToCreateObject + AQConstants.ID_CONDITION+attributeId+
					AQConstants.CONDITION_SEPERATOR+"Is Null"+";";
				}
				else
				{
					strToCreateObject = strToCreateObject + AQConstants.ID_CONDITION+attributeId+
					AQConstants.CONDITION_SEPERATOR+"In"+AQConstants.CONDITION_SEPERATOR+formattedIds+";";
				}
			}
			else if(formattedIds.length() ==0)
			{
				strToCreateObject = AQConstants.ID_CONDITION+attributeId+
				AQConstants.CONDITION_SEPERATOR+"Is Null"+";";
			}
		}
		return strToCreateObject;
	}

	/**
	 * Modify the id condition when operator = Equals or Not Equals.
	 * @param queryObject strToCreateObject
	 * @param readDeniedIds readDeniedIds
	 * @param formattedIds formattedIds
	 * @param attributeId attributeId
	 * @param operator operator
	 * @param conditionValues conditionValues
	 * @return strToCreateObject strToCreateObject
	 */
	private String modifyConditionForEqNotEq(String queryObject, List<Long> readDeniedIds,
			StringBuffer formattedIds, Long attributeId, RelationalOperator operator,
			List<String> conditionValues)
	{
		String strToCreateObject = queryObject;
		String value = conditionValues.get(0);
		if(!readDeniedIds.contains(Long.valueOf(value)))
		{
			formattedIds.append('&').append(value);
		}
		strToCreateObject = strToCreateObject + AQConstants.ID_CONDITION+attributeId+
		AQConstants.CONDITION_SEPERATOR+operator+AQConstants.CONDITION_SEPERATOR+formattedIds+";";
		return strToCreateObject;
	}

	/**
	 * Modify the id condition when operator = less than or equals.
	 * @param readDeniedIds readDeniedIds
	 * @param allMainProtocolIds allMainProtocolIds
	 * @param formattedIds formattedIds
	 * @param conditionValues conditionValues
	 */
	private void modifyCnditionForLessThanEquals(List<Long> readDeniedIds,
			List<Long> allMainProtocolIds, StringBuffer formattedIds, List<String> conditionValues)
	{
		String value = conditionValues.get(0);
		for(Long mainProtocolId : allMainProtocolIds)
		{
			if(mainProtocolId<=Long.valueOf(value) && !readDeniedIds.contains(mainProtocolId))
			{
				formattedIds.append('&').append(mainProtocolId);
			}
		}
	}

	/**
	 * Modify the id condition when operator = less than or equals.
	 * @param readDeniedIds readDeniedIds
	 * @param allMainProtocolIds allMainProtocolIds
	 * @param formattedIds formattedIds
	 * @param conditionValues conditionValues
	 */
	private void modifyConditionForLessThan(List<Long> readDeniedIds,
			List<Long> allMainProtocolIds, StringBuffer formattedIds, List<String> conditionValues)
	{
		String value = conditionValues.get(0);
		for(Long mainProtocolId : allMainProtocolIds)
		{
			if(mainProtocolId<Long.valueOf(value) && !readDeniedIds.contains(mainProtocolId))
			{
				formattedIds.append('&').append(mainProtocolId);
			}
		}
	}

	/**
	 * Modify the id condition when operator = less than or equals.
	 * @param readDeniedIds readDeniedIds
	 * @param allMainProtocolIds allMainProtocolIds
	 * @param formattedIds formattedIds
	 * @param conditionValues conditionValues
	 */
	private void modifyConditionForGreaterThanEquals(List<Long> readDeniedIds,
			List<Long> allMainProtocolIds, StringBuffer formattedIds, List<String> conditionValues)
	{
		String value = conditionValues.get(0);
		for(Long mainProtocolId : allMainProtocolIds)
		{
			if(mainProtocolId>=Long.valueOf(value) && !readDeniedIds.contains(mainProtocolId))
			{
				formattedIds.append('&').append(mainProtocolId);
			}
		}
	}

	/**
	 * Modify the id condition when operator = Greater Than.
	 * @param readDeniedIds readDeniedIds
	 * @param allMainProtocolIds allMainProtocolIds
	 * @param formattedIds formattedIds
	 * @param conditionValues conditionValues
	 */
	private void modifyCOnditionForGreaterThan(List<Long> readDeniedIds,
			List<Long> allMainProtocolIds, StringBuffer formattedIds, List<String> conditionValues)
	{
		String value = conditionValues.get(0);
		for(Long mainProtocolId : allMainProtocolIds)
		{
			if(mainProtocolId>Long.valueOf(value) && !readDeniedIds.contains(mainProtocolId))
			{
				formattedIds.append('&').append(mainProtocolId);
			}
		}
	}

	/**
	 * Modify the id condition when operator = Between.
	 * @param readDeniedIds readDeniedIds
	 * @param allMainProtocolIds allMainProtocolIds
	 * @param formattedIds formattedIds
	 * @param conditionValues conditionValues
	 */
	private void modifyConditionForBetween(List<Long> readDeniedIds, List<Long> allMainProtocolIds,
			StringBuffer formattedIds, List<String> conditionValues)
	{
		for(Long mainProtocolId : allMainProtocolIds)
		{
			if(mainProtocolId>=Long.valueOf(conditionValues.get(0)) &&
			mainProtocolId<=Long.valueOf(conditionValues.get(1))
			&& !readDeniedIds.contains(mainProtocolId))
			{
				formattedIds.append('&').append(mainProtocolId);
			}
		}
	}

	/**
	 * Modify the id condition when operator = lIn.
	 * @param readDeniedIds readDeniedIds
	 * @param formattedIds formattedIds
	 * @param conditionValues conditionValues
	 */
	private void modifyConditionForIn(List<Long> readDeniedIds, StringBuffer formattedIds,
			List<String> conditionValues)
	{
		for(String value : conditionValues)
		{
			if(!readDeniedIds.contains(Long.valueOf(value)))
			{
				formattedIds.append('&').append(value);
			}
		}
	}

	/**
	 * Modify the id condition when operator = Not In.
	 * @param strToCreateObject strToCreateObject
	 * @param readDeniedIds readDeniedIds
	 * @param formattedIds formattedIds
	 * @param attributeId attributeId
	 * @param conditionValues conditionValues
	 * @return strToCreateObject strToCreateObject
	 */
	private String modifyConditionForNotIn(String strToCreateObject, List<Long> readDeniedIds,
			StringBuffer formattedIds, Long attributeId, List<String> conditionValues)
	{
		for(String value : conditionValues)
		{
			formattedIds.append('&').append(value);
		}
		for(Long readDeniedId : readDeniedIds)
		{
			if(!conditionValues.contains(readDeniedId.toString()))
			{
				formattedIds.append('&').append(readDeniedId);
			}
		}
		strToCreateObject = strToCreateObject + AQConstants.ID_CONDITION+attributeId+
		AQConstants.CONDITION_SEPERATOR+"Not In"+AQConstants.CONDITION_SEPERATOR+formattedIds+";";
		return strToCreateObject;
	}

	/**
	 * This method links the nodes where one node is added through backend
	 * (like main entity or main protocol object).
	 * @param mainEntityExpId mainEntityExpId
	 * @param rootExpId rootExpId
	 * @param constraints constraints
	 * @return <CODE>true</CODE> path is present,
	 * <CODE>false</CODE> otherwise
	 */
	private boolean linkInvisibleNodes(int mainEntityExpId, int rootExpId, IConstraints constraints)
	{
		boolean isPathPresent = true;
		try
		{
			IExpression expression = constraints.getExpression(rootExpId);
			IQueryEntity sourceEntity = expression.getQueryEntity();

			expression = constraints.getExpression(mainEntityExpId);
			IQueryEntity destinationEntity = expression.getQueryEntity();

			List<IPath> paths = getAllPaths(sourceEntity, destinationEntity);
			if(paths.isEmpty())
			{
				isPathPresent = false;
			}
			if(isPathPresent)
			{
				List<DAGPath> dagPathList = new ArrayList<DAGPath>();
				IPath path = paths.get(0);
				try
				{
					PathLink link = linkTwoNodes(path, rootExpId, mainEntityExpId);
					int destId = link.getLogicalConnectorExpressionId();
					mQueryObject.setLogicalConnector(rootExpId,destId,
					edu.wustl.cab2b.client.ui.query.Utility.getLogicalOperator("AND"), false);
					String pathStr = Long.valueOf(path.getPathId()).toString();
					DAGPath dagPath = new DAGPath();
					dagPath.setToolTip(getPathDisplayString(path));
					dagPath.setId(pathStr);
					dagPath.setSourceExpId(mainEntityExpId);
					dagPath.setDestinationExpId(rootExpId);
					dagPathList.add(dagPath);
					String key = pathStr + "_" + mainEntityExpId + "_"+ rootExpId;
					mPathMap.put(key, path);
				}
				catch (CyclicException e)
				{
					logger.error(e.getMessage(), e);
				}
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		return isPathPresent;
	}


	/**
	 * Get all paths between source and destination entity.
	 * @param sourceEntity sourceEntity
	 * @param destinationEntity destinationEntity
	 * @return paths List of paths
	 */
	private List<IPath> getAllPaths(IQueryEntity sourceEntity, IQueryEntity destinationEntity)
	{
		Map<AmbiguityObject, List<IPath>> map;
		AmbiguityObject ambiguityObject;
		ambiguityObject = new AmbiguityObject(sourceEntity.getDynamicExtensionsEntity(),
				destinationEntity.getDynamicExtensionsEntity());
		DAGResolveAmbiguity resolveAmbigity = new DAGResolveAmbiguity
		(ambiguityObject,mPathFinder);
		map = resolveAmbigity.getPathsForAllAmbiguities();

		return map.get(ambiguityObject);
	}

	/**
	 * @param strToCreateQueryObject strToCreateQueryObject
	 * @param mode mode (Add/Edit)
	 * @param node node
	 * @param entity entity
	 * @param queryBizLogic queryBizLogic
	 * @return node Updated node
	 * @throws DynamicExtensionsSystemException DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException DynamicExtensionsApplicationException
	 */
	private DAGNode createQueryObjectLogic(String strToCreateQueryObject, String mode,
			 EntityInterface entity, CreateQueryObjectBizLogic queryBizLogic)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		DAGNode node = null;
		Map ruleDetailsMap;
		if (!strToCreateQueryObject.equalsIgnoreCase(""))
		{
			ruleDetailsMap = queryBizLogic.getRuleDetailsMap(strToCreateQueryObject, entity
					.getEntityAttributesForQuery());
			List<AttributeInterface> attributes = (List<AttributeInterface>) ruleDetailsMap
					.get(AQConstants.ATTRIBUTES);
			List<String> attributeOperators = (List<String>) ruleDetailsMap
					.get(AQConstants.ATTRIBUTE_OPERATORS);
			List<List<String>> conditionValues = (List<List<String>>) ruleDetailsMap
					.get(AQConstants.ATTR_VALUES);
			String errMsg = (String) ruleDetailsMap.get(AQConstants.ERROR_MESSAGE);
			node = checkErrorMessages(mode, entity, attributes, attributeOperators,
					conditionValues, errMsg);
		}
		return node;
	}

	/**
	 * Creates the node and checks if there's any error.
	 * @param mode mode
	 * @param entity entity
	 * @param attributes attributes
	 * @param attributeOperators attributeOperators
	 * @param conditionValues conditionValues
	 * @param errMsg errMsg
	 * @return node node
	 */
	private DAGNode checkErrorMessages(String mode, EntityInterface entity,
			List<AttributeInterface> attributes, List<String> attributeOperators,
			List<List<String>> conditionValues, String errMsg)
	{
		DAGNode node;
		int expressionId;
		if ("".equals(errMsg))
		{
			if ("Edit".equals(mode))
			{
				node = modifyRule(attributes, attributeOperators,
						conditionValues);
			}
			else
			{
				expressionId = mQueryObject.addRule(attributes, attributeOperators,
						conditionValues, entity);
				node = createNode(expressionId, false);
			}
			node.setErrorMsg(errMsg);
		}
		else
		{
			node = new DAGNode();
			node.setErrorMsg(errMsg);
		}
		return node;
	}

	/**
	 * Modify the rule.
	 * @param attributes attributes
	 * @param attributeOperators attributeOperators
	 * @param conditionValues conditionValues
	 * @return node
	 */
	private DAGNode modifyRule(List<AttributeInterface> attributes,
			List<String> attributeOperators, List<List<String>> conditionValues)
	{
		DAGNode node;
		int expressionId;
		Rule rule;
		List<ICondition> conditionsList = ((ClientQueryBuilder) mQueryObject)
				.getConditions(attributes, attributeOperators, conditionValues);
		if(expression.numberOfOperands() == 0)
		{
			rule = (Rule)QueryObjectFactory.createRule(conditionsList);
			expression.addOperand(rule);
		}
		else if(expression.getOperand(0) instanceof Rule)
		{
			rule = ((Rule) (expression.getOperand(0)));
			rule.removeAllConditions();
			for (ICondition condition : conditionsList)
			{
				rule.addCondition(condition);
			}
		}
		else
		{
			rule = (Rule)QueryObjectFactory.createRule(conditionsList);
			expression.addOperand(QueryObjectFactory
					.createLogicalConnector(LogicalOperator.And),rule);
		}
		expressionId = expression.getExpressionId();
		node = createNode(expressionId, false);
		return node;
	}

	/**
	 * Sets ClientQueryBuilder object.
	 * @param queryObject queryObject
	 */
	public void setQueryObject(IClientQueryBuilderInterface queryObject)
	{
		mQueryObject = queryObject;
	}

	/**
	 * Sets Expression.
	 * @param expression expression
	 */
	public void setExpression(IExpression expression)
	{
		this.expression = expression;
	}

	/**
	 * Links two nodes.
	 * @param sourceNode sourceNode
	 * @param destNode destNode
	 * @param paths paths
	 * @return dagPathList List of paths
	 */
	public List<DAGPath> linkNode(final DAGNode sourceNode, final DAGNode destNode,
			List<IPath> paths)
	{

		List<DAGPath> dagPathList = null;
		if (paths != null && !paths.isEmpty())
		{
			dagPathList = new ArrayList<DAGPath>();
			int sourceExpressionId = sourceNode.getExpressionId();
			int destExpressionId = destNode.getExpressionId();
			if (!mQueryObject.isPathCreatesCyclicGraph(sourceExpressionId, destExpressionId, paths
					.get(0)))
			{
				for (int i = 0; i < paths.size(); i++)
				{
					IPath path = paths.get(i);
					linkTwoNode(sourceNode, destNode, paths.get(i));
					String pathStr = Long.valueOf(path.getPathId()).toString();
					DAGPath dagPath = new DAGPath();
					dagPath.setToolTip(getPathDisplayString(path));
					dagPath.setId(pathStr);
					dagPath.setSourceExpId(sourceNode.getExpressionId());
					dagPath.setDestinationExpId(destNode.getExpressionId());
					dagPathList.add(dagPath);
					String key = pathStr + "_" + sourceNode.getExpressionId() + "_"
							+ destNode.getExpressionId();
					mPathMap.put(key, path);
				}
			}
		}
		return dagPathList;
	}

	/**
	 * Gets list of paths between two nodes.
	 * @param sourceNode sourceNode
	 * @param destNode destNode
	 * @return List<IPath> List of available paths
	 */
	public List<IPath> getPaths(DAGNode sourceNode, DAGNode destNode)
	{
		Map<AmbiguityObject, List<IPath>> map = null;
		AmbiguityObject ambiguityObject = null;
		try
		{
			IQuery query = mQueryObject.getQuery();
			IConstraints constraints = query.getConstraints();
			int expressionId = sourceNode.getExpressionId();
			IExpression expression = constraints.getExpression(expressionId);
			IQueryEntity sourceEntity = expression.getQueryEntity();
			expressionId = destNode.getExpressionId();
			expression = constraints.getExpression(expressionId);
			IQueryEntity destinationEntity = expression.getQueryEntity();

			ambiguityObject = new AmbiguityObject(sourceEntity.getDynamicExtensionsEntity(),
					destinationEntity.getDynamicExtensionsEntity());
			//			ResolveAmbiguity resolveAmbigity = new ResolveAmbiguity(
			//			ambiguityObject, m_pathFinder);
			DAGResolveAmbiguity resolveAmbigity = new DAGResolveAmbiguity(ambiguityObject,
					mPathFinder);
			map = resolveAmbigity.getPathsForAllAmbiguities();

		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		return map.get(ambiguityObject);
	}

	/**
	 * @param query query
	 * @param nodeExpressionId nodeExpressionId
	 * @param firstAttributeId firstAttributeId
	 * @return srcAttributeByIdentifier
	 */
	private AttributeInterface getAttributeIdentifier(IQuery query, int nodeExpressionId,
			String firstAttributeId)
	{
		Long identifier = Long.valueOf(firstAttributeId);
		IConstraints constraints = query.getConstraints();
		IExpression expression = constraints.getExpression(nodeExpressionId);
		IQueryEntity sourceEntity = expression.getQueryEntity();
		AttributeInterface srcAttributeByIdentifier = null;
		EntityInterface dynamicExtensionsEntity = sourceEntity.getDynamicExtensionsEntity();
		while (srcAttributeByIdentifier == null)
		{
			srcAttributeByIdentifier = dynamicExtensionsEntity.getAttributeByIdentifier(identifier);
			dynamicExtensionsEntity = dynamicExtensionsEntity.getParentEntity();
		}
		return srcAttributeByIdentifier;
	}

	/**
	 * Creates a single node custom formula.
	 * @param node node
	 * @param operation operation
	 * @return node v
	 */
	public SingleNodeCustomFormulaNode formSingleNodeFormula(SingleNodeCustomFormulaNode node,
			String operation)
	{
		//Forming custom Formula
		IQuery query = mQueryObject.getQuery();
		IConstraints constraints = query.getConstraints();
		SingalNodeTemporalQuery singalNodeTq = new SingalNodeTemporalQuery();
		int nodeExpressionId = node.getNodeExpressionId();
		singalNodeTq.setEntityExpressionId(nodeExpressionId);
		IExpression entityExpression = constraints.getExpression(nodeExpressionId);
		singalNodeTq.setEntityIExpression(entityExpression);
		//Setting Attribute by ID
		singalNodeTq.setAttributeById(getAttributeIdentifier(query, node.getNodeExpressionId(),
				node.getAttributeID()));
		//Setting attribute type
		singalNodeTq.setAttributeType(node.getAttributeType());
		//Setting Arithmetic Operator
		singalNodeTq.setArithOp(getArithmeticOperator(node.getSelectedArithmeticOp()));
		//Setting Relational Operator
		singalNodeTq.setRelOp(getRelationalOperator(node.getSelectedLogicalOp()));
		singalNodeTq.setICon(QueryObjectFactory
				.createArithmeticConnector(singalNodeTq.getArithOp()));
		//Creating HHS Literals
		singalNodeTq.createLeftLiterals(node.getLhsTimeValue(), node.getLhsTimeInterval());
		//Create Expressions
		singalNodeTq.createExpressions();
		checkSelectedLogicalOperator(node, operation, query, singalNodeTq, entityExpression);
		String oprs = setOperation(node.getOperation());
		if (oprs != null)
		{
			node.setOperation(oprs);
		}
		return node;
	}

	/**
	 * Checks the operator and accordingly populates the necessary things
	 * required for custom formula.
	 * @param node node
	 * @param operation operation
	 * @param query query
	 * @param singalNodeTq singalNodeTq
	 * @param entityExpression entityExpression
	 */
	private void checkSelectedLogicalOperator(SingleNodeCustomFormulaNode node, String operation,
			IQuery query, SingalNodeTemporalQuery singalNodeTq, IExpression entityExpression)
	{
		if (!((node.getSelectedLogicalOp().equals(DAGConstant.NULL_STRING))
				&& (node.getTimeValue().equals(DAGConstant.NULL_STRING)) && (node.getTimeInterval()
				.equals(DAGConstant.NULL_STRING))))
		{
			//Creating RHS Literals
			singalNodeTq.createRightLiterals(node.getTimeValue(), node.getTimeInterval());

			//Create LHS Terms and RHS Terms
			singalNodeTq.createLHSAndRHS();
			ICustomFormula customFormula = createSingleNodeCustomFormula(singalNodeTq, operation,
					node.getName());
			if (operation.equalsIgnoreCase(AQConstants.ADD))
			{
				CustomFormulaUIBean bean = createTQUIBean(customFormula, null, node, null);
				bean.setCalculatedResult(false);
				populateUIMap(node.getName(), bean);
				entityExpression.addOperand(getAndConnector(), customFormula);
				entityExpression.setInView(true);
			}
			else if (operation.equalsIgnoreCase(AQConstants.EDIT))
			{
				updateSingleNodeCN(node);
			}
			addOutputTermsToQuery(query, customFormula, node.getCustomColumnName());
		}
	}

	/**
	 * @param node node
	 */
	private void updateSingleNodeCN(SingleNodeCustomFormulaNode node)
	{
		String nodeId = node.getName();
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		HttpSession session = request.getSession();
		Map<String, CustomFormulaUIBean> tQUIMap = (Map<String, CustomFormulaUIBean>) session
				.getAttribute(DAGConstant.TQUIMAP);
		if (tQUIMap != null)
		{
			CustomFormulaUIBean uiBean = tQUIMap.get(nodeId);
			if (uiBean != null && uiBean.getSingleNode() != null)
			{
				uiBean.setSingleNode(node);
			}
		}
	}

	/**
	 * Forms the temporal query.
	 * @param node node
	 * @param operation operation
	 * @return node Custom Formula Node
	 */
	public CustomFormulaNode formTemporalQuery(CustomFormulaNode node, String operation)
	{
		TwoNodesTemporalQuery tqBean = new TwoNodesTemporalQuery();
		IQuery query = mQueryObject.getQuery();
		IConstraints constraints = query.getConstraints();
		int srcExpressionId = node.getFirstNodeExpId();
		tqBean.setSrcExpressionId(srcExpressionId);
		IExpression srcIExpression = constraints.getExpression(srcExpressionId);
		//Setting the source IExpression
		tqBean.setSrcIExpression(srcIExpression);
		tqBean.setSrcAttributeById(getAttributeIdentifier(query, node.getFirstNodeExpId(), node
				.getFirstSelectedAttrId()));
		int destExpressionId = node.getSecondNodeExpId();
		tqBean.setDestExpressionId(destExpressionId);
		tqBean.setDestAttributeById(getAttributeIdentifier(query, node.getSecondNodeExpId(), node
				.getSecondSelectedAttrId()));
		//Setting the destination IExpression
		tqBean.setDestIExpression(constraints.getExpression(destExpressionId));
		//Setting the attribute Types
		tqBean.setFirstAttributeType(node.getFirstSelectedAttrType());
		tqBean.setSecondAttributeType(node.getSecondSelectedAttrType());
		//Setting the Arithmetic operator
		tqBean.setArithOp(getArithmeticOperator(node.getSelectedArithmeticOp()));
		//Setting the Relational Operator
		tqBean.setRelOp(getRelationalOperator(node.getSelectedLogicalOp()));

		setQAttrInterval(node, tqBean);

		//Creating all expressions
		tqBean.createExpressions(false);
		if (tqBean.getArithOp() != null)
		{
			tqBean.setICon(QueryObjectFactory.createArithmeticConnector(tqBean.getArithOp()));
		}

		if (!((node.getSelectedLogicalOp().equals(DAGConstant.NULL_STRING))
				&& (node.getTimeValue().equals(DAGConstant.NULL_STRING)) && (node.getTimeInterval()
				.equals(DAGConstant.NULL_STRING))))
		{
			tqBean.createLiterals(node.getTimeInterval(), node.getTimeValue());
			tqBean.createLHSAndRHS();
			ICustomFormula customFormula = createCustomFormula(tqBean, operation, node.getName(),
					null);

			if (operation.equalsIgnoreCase(AQConstants.ADD))
			{
				CustomFormulaUIBean bean = createTQUIBean(customFormula, node, null, null);
				bean.setCalculatedResult(false);
				populateUIMap(node.getName(), bean);
				srcIExpression.addOperand(getAndConnector(), customFormula);
				srcIExpression.setInView(true);
			}
			else if (operation.equalsIgnoreCase(AQConstants.EDIT))
			{
				updateTwoNodesCN(node);
			}
			addOutputTermsToQuery(query, customFormula, node.getCustomColumnName());
			String oprs = setOperation(node.getOperation());
			if (oprs != null)
			{
				node.setOperation(oprs);
			}
		}
		return node;
	}

	/**
	 * Forms a join query.
	 * @param joinQueryNode joinQueryNode
	 * @param operation operation
	 * @return joinQueryNode joinQueryNode
	 */
	public JoinQueryNode formJoinQuery(JoinQueryNode joinQueryNode, String operation)
	{
		jQFormulaMap = new HashMap<String, JoinFormulaUIBean>();
		IQuery query = mQueryObject.getQuery();
		IConstraints constraints = query.getConstraints();
		int srcExpressionId = joinQueryNode.getFirstNodeExpressionId();
		IExpression srcIExpression = constraints.getExpression(srcExpressionId);
		int destExpressionId = joinQueryNode.getSecondNodeExpressionId();
		List<JoinFormulaNode> joinFormulaNodeList = joinQueryNode.getJoinFormulaNodeList();
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		HttpSession session = request.getSession();
		Map<String, Map<String, JoinFormulaUIBean>> jQUIMap =
			(Map<String, Map<String, JoinFormulaUIBean>>) session
				.getAttribute(DAGConstant.JQUIMAP);
		if (operation.equalsIgnoreCase(AQConstants.EDIT))
		{
			populateJoinBeanForEdit(joinQueryNode, srcIExpression, jQUIMap);
		}
		for (JoinFormulaNode joinFormulaNode : joinFormulaNodeList)
		{
			TwoNodesTemporalQuery tqBean = new TwoNodesTemporalQuery();
			tqBean.setSrcExpressionId(srcExpressionId);
			tqBean.setSrcIExpression(srcIExpression);
			tqBean.setSrcAttributeById(getAttributeIdentifier(query, joinQueryNode
					.getFirstNodeExpressionId(), joinFormulaNode.getFirstAttributeId()));
			tqBean.setDestExpressionId(destExpressionId);
			tqBean.setDestAttributeById(getAttributeIdentifier(query, joinQueryNode
					.getSecondNodeExpressionId(), joinFormulaNode.getSecondAttributeId()));
			tqBean.setDestIExpression(constraints.getExpression(destExpressionId));
			tqBean.setFirstAttributeType(joinFormulaNode.getFirstAttributeDataType());
			tqBean.setSecondAttributeType(joinFormulaNode.getSecondAttributeDataType());
			tqBean.setArithOp(null); //Setting the Arithmetic operator
			tqBean.setRelOp(getRelationalOperator(DAGConstant.EQUALS));
			tqBean.createExpressions(true); //Creating all expressions
			tqBean.createLHSAndRHS();
			ICustomFormula customFormula = createCustomFormula(tqBean, operation, joinFormulaNode
					.getName(), joinQueryNode.getName());
			JoinFormulaUIBean joinFormulaUIBean = createJQUIBean(customFormula, joinFormulaNode);
			populateUIMapForJoinQuery(joinFormulaNode.getName(), joinFormulaUIBean);
			srcIExpression.addOperand(getAndConnector(), customFormula);
			srcIExpression.setInView(true);
		}
		populateJoinFormulaMap(joinQueryNode, session, jQUIMap);
		return joinQueryNode;
	}

	/**
	 * @param joinQueryNode joinQueryNode
	 * @param session session
	 * @param joinQueryMap map
	 */
	private void populateJoinFormulaMap(JoinQueryNode joinQueryNode,
			HttpSession session,
			Map<String, Map<String, JoinFormulaUIBean>> joinQueryMap)
	{
		Map<String, Map<String, JoinFormulaUIBean>> jQUIMap = joinQueryMap;
		if (jQUIMap == null)
		{
			jQUIMap = new HashMap<String, Map<String, JoinFormulaUIBean>>();
			session.setAttribute(DAGConstant.JQUIMAP, jQUIMap);
		}
		jQUIMap.put(joinQueryNode.getName(), jQFormulaMap);
	}

	/**
	 * @param joinQueryNode joinQueryNode
	 * @param srcIExpression source expression
	 * @param jQUIMap map
	 */
	private void populateJoinBeanForEdit(JoinQueryNode joinQueryNode,
			IExpression srcIExpression,
			Map<String, Map<String, JoinFormulaUIBean>> jQUIMap)
	{
		Map<String, JoinFormulaUIBean> innerMap = jQUIMap.get(joinQueryNode.getName());
		Set<String> keySet = innerMap.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext())
		{
			String joinFormulaNodeId = iterator.next();
			JoinFormulaUIBean joinFormulaUIBean = innerMap.get(joinFormulaNodeId);
			ICustomFormula customFormula = joinFormulaUIBean.getICustomFormula();
			srcIExpression.removeOperand(customFormula);
		}
		jQUIMap.remove(joinQueryNode.getName());
	}

	/**
	 * @param node node
	 * @param tqBean tqBean
	 */
	private void setQAttrInterval(CustomFormulaNode node, TwoNodesTemporalQuery tqBean)
	{
		//Setting the qAttrInterval1
		if (node.getQAttrInterval1().equals(DAGConstant.NULL_STRING))
		{
			tqBean.setQAttrInterval1(null);
		}
		else
		{
			tqBean.setQAttrTInterval1(node.getQAttrInterval1());
		}
		//Setting the qAttrInterval2
		if (node.getQAttrInterval2().equals(DAGConstant.NULL_STRING))
		{
			tqBean.setQAttrInterval2(null);
		}
		else
		{
			tqBean.setQAttrTInterval2(node.getQAttrInterval2());
		}
	}

	/**
	 * @param node node
	 */
	private void updateTwoNodesCN(CustomFormulaNode node)
	{
		String nodeId = node.getName();
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		HttpSession session = request.getSession();
		Map<String, CustomFormulaUIBean> tQUIMap = (Map<String, CustomFormulaUIBean>) session
				.getAttribute(DAGConstant.TQUIMAP);
		if (tQUIMap != null)
		{
			CustomFormulaUIBean uiBean = tQUIMap.get(nodeId);
			if (uiBean != null && uiBean.getTwoNode() != null)
			{
				uiBean.setTwoNode(node);
			}
		}
	}

	/**
	 * @param customFormula customFormula
	 * @param twoNode twoNode
	 * @param singleNode singleNode
	 * @param outputTerm outputTerm
	 *  @return CustomFormulaUIBean
	 */
	private CustomFormulaUIBean createTQUIBean(ICustomFormula customFormula, CustomFormulaNode twoNode,
			SingleNodeCustomFormulaNode singleNode, IOutputTerm outputTerm)
	{
		return new CustomFormulaUIBean(customFormula, twoNode, singleNode, outputTerm);
	}

	/**
	 * @param iCustomFormula iCustomFormula
	 * @param joinFormulaNode joinFormulaNode
	 * @return JoinFormulaUIBean
	 */
	private JoinFormulaUIBean createJQUIBean(ICustomFormula iCustomFormula,
			JoinFormulaNode joinFormulaNode)
	{
		return new JoinFormulaUIBean(iCustomFormula, joinFormulaNode);
	}

	/**
	 * Setting the corresponding operation.
	 * @param nodeOperation nodeOperation
	 * @return operation operation
	 */
	private String setOperation(String nodeOperation)
	{
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		HttpSession session = request.getSession();
		String isRepaint = (String) session.getAttribute(DAGConstant.ISREPAINT);
		String operation = null;
		if (isRepaint == null)
		{
			session.setAttribute(DAGConstant.ISREPAINT, DAGConstant.ISREPAINT_FALSE);
		}
		else
		{
			if (!(isRepaint.equals(DAGConstant.ISREPAINT_FALSE)))
			{
				if ((isRepaint.equals(DAGConstant.ISREPAINT_TRUE))
						&& (nodeOperation.equals(DAGConstant.EDIT_OPERATION)))
				{
					operation = DAGConstant.REPAINT_EDIT;
				}
				else
				{
					operation = DAGConstant.REPAINT_CREATE;
				}
			}
		}
		return operation;
	}

	/**
	 * Creates output terms and adds it to Query. This will display temporal columns in results.
	 * @param query query
	 * @param customFormula customFormula
	 * @param customColumnName customColumnName
	 */
	private void addOutputTermsToQuery(IQuery query, ICustomFormula customFormula,
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

	/**
	 * Create single node custom formula.
	 * @param singleNodeTq singleNodeTq
	 * @param operation operation
	 * @param nodeId nodeId
	 * @return ICustomFormula
	 */
	private ICustomFormula createSingleNodeCustomFormula(SingalNodeTemporalQuery singleNodeTq,
			String operation, String nodeId)
	{
		ICustomFormula customFormula = null;
		if (operation.equalsIgnoreCase(AQConstants.ADD))
		{
			customFormula = QueryObjectFactory.createCustomFormula();
		}
		else
		{
			customFormula = getExistingCustomFormula(nodeId);
		}
		return getSingleNodeCustomFormula(customFormula,
				singleNodeTq);
	}

	/**
	 * @param customFormula customFormula
	 * @param singleNodeTq singleNodeTq
	 * @return customFormula customFormula
	 */
	private ICustomFormula getSingleNodeCustomFormula(ICustomFormula customFormula,
			SingalNodeTemporalQuery singleNodeTq)
	{
		customFormula.setLhs(singleNodeTq.getLhsTerm());
		customFormula.getAllRhs().clear();
		customFormula.addRhs(singleNodeTq.getRhsTerm());
		customFormula.setOperator(singleNodeTq.getRelOp());
		return customFormula;
	}

	/**
	 * Create custom formula.
	 * @param tqBean tqBean
	 * @param operation operation
	 * @param nodeId nodeId
	 * @param joinQueryNodeId joinQueryNodeId
	 * @return ICustomFormula
	 */
	private ICustomFormula createCustomFormula(TwoNodesTemporalQuery tqBean, String operation,
			String nodeId, String joinQueryNodeId)
	{
		ICustomFormula customFormula = null;
		if (operation.equalsIgnoreCase(AQConstants.ADD) || joinQueryNodeId != null)
		{
			customFormula = QueryObjectFactory.createCustomFormula();
		}
		else
		{
			customFormula = getExistingCustomFormula(nodeId);
		}
		return getCustomFormula(customFormula, tqBean);
	}

	/**
	 * Populate UI map for Temporal Query.
	 * @param identifier identifier
	 * @param customFormulaUIBean customFormulaUIBean
	 */
	private void populateUIMap(String identifier, CustomFormulaUIBean customFormulaUIBean)
	{
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		HttpSession session = request.getSession();
		Map<String, CustomFormulaUIBean> tQUIMap = (Map<String, CustomFormulaUIBean>) session
				.getAttribute(DAGConstant.TQUIMAP);
		if (tQUIMap == null)
		{
			tQUIMap = new HashMap<String, CustomFormulaUIBean>();
			session.setAttribute(DAGConstant.TQUIMAP, tQUIMap);
		}
		tQUIMap.put(identifier, customFormulaUIBean);
	}

	/**
	 * Populate UI map for Join Query.
	 * @param joinFormulaId joinFormulaId
	 * @param joinFormulaUIBean joinFormulaUIBean
	 */
	private void populateUIMapForJoinQuery(String joinFormulaId, JoinFormulaUIBean joinFormulaUIBean)
	{
		if (jQFormulaMap == null)
		{
			jQFormulaMap = new HashMap<String, JoinFormulaUIBean>();
			jQFormulaMap.put(joinFormulaId, joinFormulaUIBean);
		}
		jQFormulaMap.put(joinFormulaId, joinFormulaUIBean);
	}

	/**
	 * @param customFormula customFormula
	 * @param tqBean tqBean
	 * @return customFormula
	 */
	private ICustomFormula getCustomFormula(ICustomFormula customFormula,
			TwoNodesTemporalQuery tqBean)
	{
		if (tqBean.getRhsTerm() == null)
		{
			//Then custom formula will have only LHS and relational Operator
			customFormula.setLhs(tqBean.getLhsTerm());
			customFormula.setOperator(tqBean.getRelOp());
		}
		else
		{
			customFormula.setLhs(tqBean.getLhsTerm());
			customFormula.getAllRhs().clear();
			customFormula.addRhs(tqBean.getRhsTerm());
			customFormula.setOperator(tqBean.getRelOp());
		}
		return customFormula;
	}

	/**
	 * Get existing custom formula.
	 * @param identifier id
	 * @return customFormula
	 */
	public ICustomFormula getExistingCustomFormula(String identifier)
	{
		ICustomFormula customFormula = null;
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		HttpSession session = request.getSession();
		Map<String, CustomFormulaUIBean> tQUIMap = (Map<String, CustomFormulaUIBean>) session
				.getAttribute("TQUIMap");
		if (tQUIMap != null)
		{
			CustomFormulaUIBean customFormulaUIBean = tQUIMap.get(identifier);
			if (!customFormulaUIBean.isCalculatedResult())
			{
				customFormula = customFormulaUIBean.getCf();
				deleteOutputTerm(customFormulaUIBean);
			}
		}
		return customFormula;
	}

	/**
	 * Get existing join formula.
	 * @param formulaNodeId formulaNodeId
	 * @param joinQueryNodeId joinQueryNodeId
	 * @return customFormula
	 */
	public ICustomFormula getExistingJoinFormula(String formulaNodeId, String joinQueryNodeId)
	{
		ICustomFormula customFormula = null;
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		HttpSession session = request.getSession();

		Map<String, Map<String, JoinFormulaUIBean>> jQUIMap =
			(Map<String, Map<String, JoinFormulaUIBean>>) session
				.getAttribute("JQUIMap");
		if (jQUIMap != null)
		{
			Map<String, JoinFormulaUIBean> formulaMap = jQUIMap.get(joinQueryNodeId);
			JoinFormulaUIBean joinFormulaUIBean = formulaMap.get(formulaNodeId);
			if (joinFormulaUIBean != null)
			{
				customFormula = joinFormulaUIBean.getICustomFormula();
			}
		}
		return customFormula;
	}

	/**
	 * @param customFormulaUIBean customFormulaUIBean
	 */
	private void deleteOutputTerm(CustomFormulaUIBean customFormulaUIBean)
	{
		IQuery query = mQueryObject.getQuery();
		List<IOutputTerm> outputTerms = query.getOutputTerms();
		IOutputTerm termToDelete = null;
		for (IOutputTerm term : outputTerms)
		{
			if (customFormulaUIBean.getCf().getLhs().equals(term.getTerm()))
			{
				termToDelete = term;
				break;
			}
		}
		if (termToDelete != null)
		{
			query.getOutputTerms().remove(termToDelete);
		}
	}

	/**
	 * Get relational operator.
	 * @param relationalOp relationalOp
	 * @return relOp
	 */
	private RelationalOperator getRelationalOperator(String relationalOp)
	{
		RelationalOperator relOp = null;
		for (RelationalOperator operator : RelationalOperator.values())
		{
			if (operator.getStringRepresentation().equals(relationalOp))
			{
				relOp = operator;
				break;
			}
		}
		return relOp;
	}

	/**
	 * Get arithmetic operator.
	 * @param arithmeticOp arithmeticOp
	 * @return arithOp
	 */
	private ArithmeticOperator getArithmeticOperator(String arithmeticOp)
	{
		ArithmeticOperator arithOp = null;
		for (ArithmeticOperator operator : ArithmeticOperator.values())
		{
			if (operator.mathString().equals(arithmeticOp))
			{
				arithOp = operator;
				break;
			}
		}
		return arithOp;
	}

	/**
	 * @return Connector
	 */
	private static IConnector<LogicalOperator> getAndConnector()
	{
		return QueryObjectFactory.createLogicalConnector(LogicalOperator.And);
	}

	/**
	 * @param dagNode dagNode
	 * @return <CODE>true</CODE> node is valid,
	 * <CODE>false</CODE> otherwise
	 */
	public boolean checkForNodeValidAttributes(DAGNode dagNode)
	{
		return checkIfValidNode(dagNode);
	}

	/**
	 * @param linkedNodeList linkedNodeList
	 * @return <CODE>true</CODE> nods are valid,
	 * <CODE>false</CODE> otherwise
	 */
	public boolean checkForValidAttributes(List<DAGNode> linkedNodeList)
	{
		boolean areNodesValid = false;
		DAGNode sourceNode = linkedNodeList.get(0);
		DAGNode destinationNode = linkedNodeList.get(1);
		boolean isSourceNodeValid = checkIfValidNode(sourceNode);
		boolean isDestNodeValid = checkIfValidNode(destinationNode);

		if ((isSourceNodeValid && isDestNodeValid))
		{
			areNodesValid = true;
		}
		return areNodesValid;
	}

	/**
	 * @param sourceNode sourceNode
	 * @return <CODE>true</CODE> node is valid,
	 * <CODE>false</CODE> otherwise
	 */
	private boolean checkIfValidNode(DAGNode sourceNode)
	{
		boolean isValid = false;
		IQuery query = mQueryObject.getQuery();
		IConstraints constraints = query.getConstraints();
		int expressionId = sourceNode.getExpressionId();
		IExpression expression = constraints.getExpression(expressionId);

		/**
		 * Checking if source node has a Date attribute
		 */
		IQueryEntity sourceEntity = expression.getQueryEntity();
		Collection<AttributeInterface> sourceAttributeCollection = sourceEntity
				.getDynamicExtensionsEntity().getEntityAttributesForQuery();

		for (AttributeInterface attribute : sourceAttributeCollection)
		{
			isValid = checkSrcNodeDateAttribute(isValid, attribute);
		}
		return isValid;
	}

	/**
	 * @param ifValid ifValid
	 * @param attribute attribute
	 * @return <CODE>true</CODE> attribute and its data type is valid,
	 * <CODE>false</CODE> otherwise
	 */
	private boolean checkSrcNodeDateAttribute(boolean ifValid, AttributeInterface attribute)
	{
		boolean isValid = ifValid;
		if (((attribute.getDataType().equals(AQConstants.DATE_TYPE)
				|| attribute.getDataType().equals(AQConstants.INTEGER_TYPE)
				|| attribute.getDataType().equals(AQConstants.DOUBLE_TYPE)
				|| attribute.getDataType().equals(AQConstants.LONG_TYPE) || attribute.getDataType()
				.equals(AQConstants.FLOAT_TYPE)) || attribute.getDataType().equals(
				AQConstants.SHORT_TYPE))
				&& (!attribute.getName().equals("id")))
		{
			isValid = true;
		}
		return isValid;
	}

	/**
	 * Returns collection of attributes.
	 * @param nodeExpId nodeExpId
	 * @return sourceAttributeCollection
	 */
	private Collection<AttributeInterface> getAttributeCollection(int nodeExpId)
	{
		IQuery query = mQueryObject.getQuery();
		IConstraints constraints = query.getConstraints();
		IExpression expression = constraints.getExpression(nodeExpId);
		IQueryEntity sourceEntity = expression.getQueryEntity();
		Collection<AttributeInterface> sourceAttributeCollection = sourceEntity
				.getDynamicExtensionsEntity().getEntityAttributesForQuery();
		return sourceAttributeCollection;

	}

	/**
	 * @param sourceExpId sourceExpId
	 * @param nodeName nodeName
	 * @return queryDataMap
	 */
	public Map getSingleNodeQueryData(int sourceExpId, String nodeName)
	{
		Map<String, Object> queryDataMap = new HashMap<String, Object>();
		Map<String, List<String>> nodeAttributesMap = new HashMap<String, List<String>>();

		//Setting the Entity Name as Label
		String nodeNameLabel = Utility.getDisplayLabel(nodeName);
		List<String> entityLabelsList = new ArrayList<String>();
		entityLabelsList.add(nodeNameLabel);

		//Getting the Entity's data Map
		Collection<AttributeInterface> nodeAttributeCollection = getAttributeCollection(sourceExpId);
		populateMap(nodeAttributesMap, nodeAttributeCollection);

		List<String> timeIntervalList = TemporalQueryUtility.getTimeIntervals();
		List<String> arithmeticOperatorList = getArithmeticOperators();
		List<String> relationalOperatorsList = TemporalQueryUtility.getRelationalOperators();

		queryDataMap.put(AQConstants.ARITHMETIC_OPERATORS, arithmeticOperatorList);
		queryDataMap.put(AQConstants.SECOND_NODE_ATTRIBUTES, nodeAttributesMap);
		queryDataMap.put(AQConstants.RELATIONAL_OPERATORS, relationalOperatorsList);
		queryDataMap.put(AQConstants.TIME_INTERVALS_LIST, timeIntervalList);
		queryDataMap.put(AQConstants.ENTITY_LABEL_LIST, entityLabelsList);
		return queryDataMap;
	}

	/**
	 * @param sourceExpId sourceExpId
	 * @param destExpId destExpId
	 * @param sourceNodeName sourceNodeName
	 * @param destNodeName destNodeName
	 * @param isJoinQueryButtonSelected isJoinQueryButtonSelected
	 * @return queryDataMap
	 */
	public Map getQueryData(int sourceExpId, int destExpId, String sourceNodeName,
			String destNodeName, boolean isJoinQueryButtonSelected)
	{
		Map<String, Object> queryDataMap = new HashMap<String, Object>();
		Map<String, List<String>> sourceNodeAttributesMap = new HashMap<String, List<String>>();
		Map<String, List<String>> destNodeAttributesMap = new HashMap<String, List<String>>();
		List<String> entityLabelsList = getEntityLabelsList(sourceNodeName, destNodeName);
		Collection<AttributeInterface> sourceAttributeCollection = getAttributeCollection(sourceExpId);
		if (isJoinQueryButtonSelected)
		{
			populateMapForJoinQuery(sourceNodeAttributesMap, sourceAttributeCollection);
		}
		else
		{
			populateMap(sourceNodeAttributesMap, sourceAttributeCollection);
		}

		Collection<AttributeInterface> destAttributeCollection = getAttributeCollection(destExpId);
		List<String> timeIntervalList = TemporalQueryUtility.getTimeIntervals();
		if (isJoinQueryButtonSelected)
		{
			populateMapForJoinQuery(destNodeAttributesMap, destAttributeCollection);
		}
		else
		{
			populateMap(destNodeAttributesMap, destAttributeCollection);
		}

		List<String> arithmeticOperatorList = getArithmeticOperators();
		List<String> relationalOperatorsList = TemporalQueryUtility.getRelationalOperators();
		if (isJoinQueryButtonSelected)
		{
			queryDataMap.put(AQConstants.FIRST_NODE_ATTRIBUTES, sourceNodeAttributesMap);
			queryDataMap.put(AQConstants.SECOND_NODE_ATTRIBUTES, destNodeAttributesMap);
			queryDataMap.put("entityList", entityLabelsList);
		}
		else
		{
			queryDataMap.put(AQConstants.FIRST_NODE_ATTRIBUTES, sourceNodeAttributesMap);
			queryDataMap.put(AQConstants.ARITHMETIC_OPERATORS, arithmeticOperatorList);
			queryDataMap.put(AQConstants.SECOND_NODE_ATTRIBUTES, destNodeAttributesMap);
			queryDataMap.put(AQConstants.RELATIONAL_OPERATORS, relationalOperatorsList);
			queryDataMap.put("timeIntervals", timeIntervalList);
			queryDataMap.put("entityList", entityLabelsList);
		}
		return queryDataMap;
	}

	/**
	 * @param srcNodeName srcNodeName
	 * @param destNodeName destNodeName
	 * @return entityList List of entity labels.
	 */
	private List<String> getEntityLabelsList(String srcNodeName, String destNodeName)
	{
		List<String> entityList = new ArrayList<String>();

		String nodeName1 = Utility.getDisplayLabel(srcNodeName);
		String nodeName2 = Utility.getDisplayLabel(destNodeName);

		entityList.add(0, nodeName1);
		entityList.add(1, nodeName2);
		return entityList;
	}

	/**
	 * @param destNodeAttributesMap destNodeAttributesMap
	 * @param destAttributeCollection destAttributeCollection
	 */
	private void populateMap(Map<String, List<String>> destNodeAttributesMap,
			Collection<AttributeInterface> destAttributeCollection)
	{
		List<String> destNodeList;
		/**
		 * Storing all attributes of destination entity having DataType as Date
		 */
		for (AttributeInterface attribute : destAttributeCollection)
		{
			String destDataType = attribute.getDataType();
			if (destDataType.equals(AQConstants.DATE_TYPE))
			{
				destNodeList = new ArrayList<String>();
				//Putting attribute name and attribute data type in Map
				destNodeList.add(0, attribute.getId().toString());
				destNodeList.add(1, attribute.getDataType());
				destNodeAttributesMap.put(attribute.getName(), destNodeList);
			}
			else
			{
				populateMapForPrimitiveTypes(destNodeAttributesMap, attribute,
						destDataType);
			}
		}
	}

	/**
	 * @param destNodeAttributesMap Map
	 * @param attribute attribute
	 * @param destDataType destination data type
	 */
	private void populateMapForPrimitiveTypes(
			Map<String, List<String>> destNodeAttributesMap,
			AttributeInterface attribute, String destDataType)
	{
		List<String> destNodeList;
		if ((destDataType.equals(AQConstants.INTEGER_TYPE)
				|| destDataType.equals(AQConstants.LONG_TYPE)
				|| destDataType.equals(AQConstants.DOUBLE_TYPE)
				|| destDataType.equals(AQConstants.FLOAT_TYPE) || destDataType
				.equals(AQConstants.SHORT_TYPE))
				&& (!attribute.getName().equals("id")))
		{
			destNodeList = new ArrayList<String>();
			destNodeList.add(0, attribute.getId().toString());
			destNodeList.add(1, AQConstants.INTEGER_TYPE);
			destNodeAttributesMap.put(attribute.getName(), destNodeList);
		}
	}

	/**
	 * @param destNodeAttributesMap destNodeAttributesMap
	 * @param destAttributeCollection destAttributeCollection
	 */
	private void populateMapForJoinQuery(Map<String, List<String>> destNodeAttributesMap,
			Collection<AttributeInterface> destAttributeCollection)
	{
		List<String> destNodeList;
		/**
		 * Storing all attributes of destination entity having DataType as Date.
		 */
		for (AttributeInterface attribute : destAttributeCollection)
		{
			destNodeList = new ArrayList<String>();
			String formattedAttributeLabel = Utility.getDisplayLabel(attribute.getName());
			//Putting attribute name and attribute data type in Map
			destNodeList.add(0, attribute.getId().toString());
			destNodeList.add(1, attribute.getDataType());
			destNodeList.add(AQConstants.TWO, attribute.getName());
			destNodeAttributesMap.put(formattedAttributeLabel, destNodeList);
		}
	}

	/**
	 * Get all arithmetic operators.
	 * @return arithmeticOperatorList
	 */
	private List<String> getArithmeticOperators()
	{
		List<String> arithmeticOperatorList = new ArrayList<String>();
		/**
		 * Getting all arithmetic operators.
		 */
		for (ArithmeticOperator operator : ArithmeticOperator.values())
		{
			if ((!operator.mathString().equals("")) && (!operator.mathString().equals("*"))
					&& (!operator.mathString().equals("/")))
			{
				arithmeticOperatorList.add(operator.mathString());
			}
		}
		return arithmeticOperatorList;
	}

	/**
	 * This method removes the Custom formula from query on delete of custom Node.
	 * @param customNodeId Identifier of the custom node
	 */
	public void removeCustomFormula(String customNodeId)
	{
		IQuery query = mQueryObject.getQuery();
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		HttpSession session = request.getSession();
		Map<String, CustomFormulaUIBean> tQUIMap = (Map<String, CustomFormulaUIBean>) session
				.getAttribute("TQUIMap");
		if (tQUIMap != null)
		{
			clearTQUIMap(customNodeId, query, tQUIMap);
		}
	}

	/**
	 * @param customNodeId customNodeId
	 * @param query query
	 * @param tQUIMap Map
	 */
	private void clearTQUIMap(String customNodeId, IQuery query,
			Map<String, CustomFormulaUIBean> tQUIMap)
	{
		CustomFormulaUIBean customFormulaUIBean = tQUIMap.get(customNodeId);
		ICustomFormula customFormula = customFormulaUIBean.getCf();
		if (customFormula != null && !customFormulaUIBean.isCalculatedResult())
		{
			IConstraints constraints = query.getConstraints();
			deleteOutputTerm(customFormulaUIBean);
			for (IExpression expression2 : constraints)
			{
				expression2.removeOperand(customFormula);
			}
		}
		else
		{
			deleteOutputTerm(query, customFormulaUIBean);
		}
		tQUIMap.remove(customNodeId);
	}

	/**
	 * @param query query
	 * @param customFormulaUIBean customFormulaUIBean
	 */
	private void deleteOutputTerm(IQuery query,
			CustomFormulaUIBean customFormulaUIBean)
	{
		IOutputTerm termToDelete = customFormulaUIBean.getOutputTerm();
		if (termToDelete != null)
		{
			query.getOutputTerms().remove(termToDelete);
		}
	}

	/**
	 * Removes the join formula from the query.
	 * @param joinQueryNodeId Join query node id
	 */
	public void removeJoinFormula(String joinQueryNodeId)
	{
		IQuery query = mQueryObject.getQuery();
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		HttpSession session = request.getSession();
		Map<String, Map<String, JoinFormulaUIBean>> jQUIMap =
		(Map<String, Map<String, JoinFormulaUIBean>>) session.getAttribute(DAGConstant.JQUIMAP);
		if (jQUIMap != null)
		{
			Map<String, JoinFormulaUIBean> joinFormulaMap = jQUIMap.get(joinQueryNodeId);
			for (int i = 1; i <= joinFormulaMap.size(); i++)
			{
				String joinFormulNodeId = joinQueryNodeId + "_" + i;
				JoinFormulaUIBean joinFormulaUIBean = joinFormulaMap.get(joinFormulNodeId);
				ICustomFormula iCusFormula = joinFormulaUIBean.getICustomFormula();

				if (iCusFormula != null)
				{
					IConstraints constraints = query.getConstraints();
					for (IExpression expression2 : constraints)
					{
						expression2.removeOperand(iCusFormula);
					}
				}
			}
			jQUIMap.remove(joinQueryNodeId);
		}
	}

	/**
	 * @param joinQueryNode Object of JoinQueryNode.
	 * @return formatedLabelList List of formatted labels for join query
	 */
	public List getFormattedLabelForJQ(JoinQueryNode joinQueryNode)
	{
		List<String> formatedLabelList = new ArrayList<String>();
		try
		{
			List<JoinFormulaNode> joinFormulaNodeList = joinQueryNode.getJoinFormulaNodeList();
			for (JoinFormulaNode joinFormulaNode : joinFormulaNodeList)
			{
				String firstAttributeNameLabel = Utility.getDisplayLabel(joinFormulaNode
						.getFirstAttributeName());
				String secondAttributeNameLabel = Utility.getDisplayLabel(joinFormulaNode
						.getSecondAttributeName());
				formatedLabelList.add(firstAttributeNameLabel + "_" + secondAttributeNameLabel);
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		return formatedLabelList;
	}

	/**
	 * Link 2 nodes.
	 * @param sourceNode Source node
	 * @param destNode Destination node
	 * @param path path
	 */
	private void linkTwoNode(final DAGNode sourceNode, final DAGNode destNode, final IPath path)
	{
		try
		{
			int sourceexpressionId = sourceNode.getExpressionId();
			int destexpressionId = destNode.getExpressionId();
			PathLink link = linkTwoNodes(path, sourceexpressionId, destexpressionId);
			updateQueryObject(link, sourceNode);
		}
		catch (CyclicException e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Link two nodes on the DAG.
	 * @param path path
	 * @param sourceexpressionId sourceexpressionId
	 * @param destexpressionId destexpressionId
	 * @return link
	 * @throws CyclicException CyclicException
	 */
	private PathLink linkTwoNodes(final IPath path, int sourceexpressionId, int destexpressionId)
			throws CyclicException
	{
		List<Integer> intermediateExpressions;
		intermediateExpressions = mQueryObject.addPath(sourceexpressionId, destexpressionId,
				path);
		PathLink link = new PathLink();
		link.setAssociationExpressions(intermediateExpressions);
		link.setDestinationExpressionId(destexpressionId);
		link.setSourceExpressionId(sourceexpressionId);
		link.setPath(path);
		return link;
	}

	/**
	 * Updates query object.
	 * @param link Object of PathLink
	 * @param sourceNode Source node
	 */
	private void updateQueryObject(PathLink link, DAGNode sourceNode)
	{
		//TODO required to modify code logic will not work for multiple association
		int sourceexpressionId = sourceNode.getExpressionId();

		// If the first association is added, put operator between attribute condition and association
		String operator = null;

		operator = sourceNode.getOperatorBetweenAttrAndAssociation();

		// Get the expressionId between which to add logical operator
		int destId = link.getLogicalConnectorExpressionId();

		mQueryObject.setLogicalConnector(sourceexpressionId, destId,
				edu.wustl.cab2b.client.ui.query.Utility.getLogicalOperator(operator), false);
	}

	/**
	 * Gets display path string.
	 * @param path Path object
	 * @return text Display string
	 */
	public static String getPathDisplayString(IPath path)
	{
		String text = "";
		List<IAssociation> pathList = path.getIntermediateAssociations();
		text = text.concat(edu.wustl.cab2b.common.util.Utility.getOnlyEntityName(path
				.getSourceEntity()));
		for (int i = 0; i < pathList.size(); i++)
		{
			text = text.concat(">>");
			IAssociation association = pathList.get(i);
			if (association instanceof IIntraModelAssociation)
			{
				IIntraModelAssociation iAssociation = (IIntraModelAssociation) association;
				AssociationInterface dynamicExtensionsAssociation = iAssociation
						.getDynamicExtensionsAssociation();
				String role = "(" + dynamicExtensionsAssociation.getTargetRole().getName() + ")";
				text = text.concat(role + ">>");
			}
			text = text.concat(edu.wustl.cab2b.common.util.Utility.getOnlyEntityName(association
					.getTargetEntity()));
		}
		Logger.out.debug(text);
		return text;

	}

	/**
	 * Generates SQL query.
	 * @return error code
	 */
	public int search()
	{
		QueryModuleError status = QueryModuleError.SUCCESS;
		IQuery query = mQueryObject.getQuery();
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		boolean isRulePresentInDag = QueryModuleUtil.checkIfRulePresentInDag(query);
		QueryModuleSearchQueryUtil qMSearchQuery = new QueryModuleSearchQueryUtil(request, query);
		if (isRulePresentInDag)
		{
			status = qMSearchQuery.searchQuery();
		}
		else
		{
			status = QueryModuleError.EMPTY_DAG;
		}
		return status.getErrorCode();
	}

	/**
	 * Checks whether the key set contains the custom node name.
	 * @param customNodeName Name of the custom node.
	 * @param keySet keySet
	 * @return isContains
	 */
	private boolean isKeySetContainsNodeName(String customNodeName, Set keySet)
	{
		boolean isContains = false;
		Iterator keySetItr = keySet.iterator();
		while (keySetItr.hasNext())
		{
			String key = (String) keySetItr.next();
			if (customNodeName.equals(key))
			{
				isContains = true;
				break;
			}
		}
		return isContains;
	}

	/**
	 * Returns the custom node name.
	 * @param nodeName node name
	 * @param tQUIMap  TQUIMap
	 * @return customNodeName custom node name
	 */
	private String getCustomNodeName(String nodeName, Map<String, CustomFormulaUIBean> tQUIMap)
	{
		String customNodeName = " ";
		int customNodeNumber = 1;
		boolean isContains = false;
		Set keySet = tQUIMap.keySet();
		if (keySet.isEmpty())
		{
			//This is the Initial case
			customNodeName = nodeName + "_" + customNodeNumber;
		}
		else
		{
			while (customNodeNumber <= keySet.size())
			{
				customNodeName = nodeName + "_" + customNodeNumber;
				isContains = isKeySetContainsNodeName(customNodeName, keySet);
				if (isContains)
				{
					customNodeNumber++;
				}
				else
				{
					break;
				}
			}
		}
		if (customNodeNumber == (keySet.size() + 1) && isContains)
		{
			//By this time,customNodeNumber already exceeds the length of the KeySet,
			//so new  customNodeName is
			customNodeName = nodeName + "_" + customNodeNumber;
		}
		return customNodeName;
	}

	/**
	 * Repaints DAG.
	 * @return nodeMap nodeMap
	 */
	public Map<String, Object> repaintDAG()
	{
		List<DAGNode> nodeList = new ArrayList<DAGNode>();
		customFormulaMap = new HashMap<Integer, ICustomFormula>();
		customFormulaMapcounter = 0;
		counter = 0;
		List<CustomFormulaNode> customNodeList = new ArrayList<CustomFormulaNode>();
		List<SingleNodeCustomFormulaNode> sNcustomNodeList = new ArrayList<SingleNodeCustomFormulaNode>();
		List<JoinQueryNode> joinQueryNodeList = new ArrayList<JoinQueryNode>();
		Map<String, Object> nodeMap = new HashMap<String, Object>();
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		HttpSession session = request.getSession();
		IQuery query = (IQuery) session.getAttribute(DAGConstant.QUERY_OBJECT);
		mQueryObject.setQuery(query);
		IConstraints constraints = query.getConstraints();
		positionMap = new QueryNodeLocator(DAGConstant.FOUR_HUNDRED, query).getPositionMap();
		setRepaintAttribute(session);
		Set<Integer> visibleExpression = populateNodeList(nodeList, constraints);
		Map<String, CustomFormulaUIBean> tQUIMap = (Map<String, CustomFormulaUIBean>) session
				.getAttribute(DAGConstant.TQUIMAP);
		Map<String, Map<String, JoinFormulaUIBean>> jQUIMap =
			(Map<String, Map<String, JoinFormulaUIBean>>) session
				.getAttribute(DAGConstant.JQUIMAP);

		if (tQUIMap == null && jQUIMap == null)
		{
			repaintFromSavedQuery(customNodeList, sNcustomNodeList, joinQueryNodeList, session,
					query, constraints, visibleExpression, tQUIMap, jQUIMap);
		}
		else
		{
			repaintFromSessionQuery(customNodeList, sNcustomNodeList, joinQueryNodeList, query,
					constraints, visibleExpression, tQUIMap, jQUIMap);
		}
		nodeMap.put(DAGConstant.DAG_NODE_LIST, nodeList);
		nodeMap.put(DAGConstant.CUSTOM_FORMULA_NODE_LIST, customNodeList);
		nodeMap.put(DAGConstant.SINGLE_NODE_CF_LIST, sNcustomNodeList);
		nodeMap.put(DAGConstant.JOIN_QUERY_NODE_LIST, joinQueryNodeList);
		return nodeMap;
	}

	/**
	 * @param session session
	 */
	private void setRepaintAttribute(HttpSession session)
	{
		String isRepaint = (String) session.getAttribute(DAGConstant.ISREPAINT);
		if ((isRepaint == null) || (isRepaint.equals(DAGConstant.ISREPAINT_FALSE)))
		{
			session.setAttribute(DAGConstant.ISREPAINT, DAGConstant.ISREPAINT_TRUE);
		}
	}

	/**
	 * @param nodeList nodeList
	 * @param constraints constraints
	 * @return visibleExpression
	 */
	private Set<Integer> populateNodeList(List<DAGNode> nodeList,
			IConstraints constraints)
	{
		Set<Integer> visibleExpression = addVisibleExpressions(constraints);
		for (Integer expressionId : visibleExpression)
		{
			IExpression exp = constraints.getExpression(expressionId.intValue());
			IQueryEntity constraintEntity = exp.getQueryEntity();
			String nodeDisplayName = edu.wustl.cab2b.common.util.Utility
					.getOnlyEntityName(constraintEntity.getDynamicExtensionsEntity());
			DAGNode dagNode = new DAGNode();
			dagNode.setExpressionId(exp.getExpressionId());
			dagNode.setNodeName(nodeDisplayName);
			dagNode.setToolTip(exp);
			Position position = positionMap.get(exp.getExpressionId());
			setNodeType(exp, dagNode, position);
			nodeform(expressionId, dagNode, constraints, new ArrayList<IIntraModelAssociation>());
			int numOperands = exp.numberOfOperands();
			int numOperator = 0;
			for (int i = 0; i < numOperands; i++)
			{
				if(!(exp.getOperand(i) instanceof Rule))
				{
					numOperator++;
				}
			}
			for (int i = 0; i < numOperator; i++)
			{
				String operator = exp.getConnector(i, i + 1).getOperator().toString();
				dagNode.setOperatorList(operator.toUpperCase());
			}
			nodeList.add(dagNode);
		}
		return visibleExpression;
	}

	/**
	 * @param constraints constraints
	 * @return visibleExpression
	 */
	private Set<Integer> addVisibleExpressions(IConstraints constraints)
	{
		Set<Integer> visibleExpression = new HashSet<Integer>();
		for (IExpression expression : constraints)
		{
			if (expression.isVisible())
			{
				visibleExpression.add(Integer.valueOf(expression.getExpressionId()));
			}
		}
		return visibleExpression;
	}

	/**
	 * @param exp Object of IExpression.
	 * @param dagNode Object of DAGNode
	 * @param position Position object
	 */
	private void setNodeType(IExpression exp, DAGNode dagNode, Position position)
	{
		if (position != null)
		{
			dagNode.setX(position.getX());
			dagNode.setY(position.getY());
		}
		if (!exp.containsRule())
		{
			dagNode.setNodeType(DAGConstant.VIEW_ONLY_NODE);
		}
		if (!exp.isInView())
		{
			dagNode.setNodeType(DAGConstant.CONSTRAINT_ONLY_NODE);
		}
	}

	/**
	 *
	 * @param customNodeList List of custom formulae.
	 * @param SNcustomNodeList List of Single node custom formulae
	 * @param joinQueryNodeList List of join query nodes
	 * @param query Object of IQuery
	 * @param constraints constraints on the query
	 * @param visibleExpression visible expression
	 * @param tQUIMap map of temporal queries
	 * @param jQUIMap map of join queries
	 */
	private void repaintFromSessionQuery(List<CustomFormulaNode> customNodeList,
			List<SingleNodeCustomFormulaNode> SNcustomNodeList,
			List<JoinQueryNode> joinQueryNodeList, IQuery query, IConstraints constraints,
			Set<Integer> visibleExpression, Map<String, CustomFormulaUIBean> tQUIMap,
			Map<String, Map<String, JoinFormulaUIBean>> jQUIMap)
	{
		for (Integer expressionId : visibleExpression)
		{
			IExpression exp = constraints.getExpression(expressionId.intValue());
			if (exp.containsCustomFormula())
			{
				Set<ICustomFormula> customFormulas = QueryUtility.getCustomFormulas(exp);
				if (!customFormulas.isEmpty())
				{
					if (tQUIMap != null)
					{
						for (ICustomFormula c : customFormulas)
						{
							Set keySet = tQUIMap.keySet();
							Iterator keySetItr = keySet.iterator();
							while (keySetItr.hasNext())
							{
								String key = (String) keySetItr.next();
								String[] ids = key.split("_");
								if (!ids[0].equals("c"))
								{
									//This is the case of TQ
									String id1 = ids[0];
									String id2 = ids[1];
									if (id1.equals(id2))
									{
										sessionQSingleNodeCNode(SNcustomNodeList, query, tQUIMap,
												c, key);
									}
									else
									{
										sessionQTwoNodesCNode(customNodeList, query, tQUIMap, c,
												key);
									}
								}
							}
						}
					}
					if (jQUIMap != null)
					{
						populateJoinQueryNodeList(joinQueryNodeList,
								constraints, jQUIMap, customFormulas);
					}
				}
			}
		}
	}

	/**
	 * @param joinQueryNodeList joinQueryNodeList
	 * @param constraints
	 * @param jQUIMap join query map
	 * @param customFormulas custom formulae
	 */
	private void populateJoinQueryNodeList(
			List<JoinQueryNode> joinQueryNodeList, IConstraints constraints,
			Map<String, Map<String, JoinFormulaUIBean>> jQUIMap,
			Set<ICustomFormula> customFormulas)
	{
		String id1 = null;
		String id2 = null;
		Set keySet = jQUIMap.keySet();
		Iterator keySetItr = keySet.iterator();
		while (keySetItr.hasNext())
		{
			String key = (String) keySetItr.next();
			JoinQueryNode joinQueryNode = new JoinQueryNode();
			List<JoinFormulaNode> joinFormulaList = new ArrayList<JoinFormulaNode>();
			String[] ids = key.split("_");
			boolean flag = false;
			for (ICustomFormula c : customFormulas)
			{
				flag = sessionQTwoNodesJNode(joinFormulaList, jQUIMap, c, key);
			}
			if (flag)
			{
				id1 = ids[1];
				id2 = ids[2];
				IExpression exp1 = constraints.getExpression(Integer.parseInt(id1));
				IExpression exp2 = constraints.getExpression(Integer.parseInt(id2));
				IQueryEntity constraintEntity1 = exp1.getQueryEntity();
				IQueryEntity constraintEntity2 = exp2.getQueryEntity();
				String firstEntityName = edu.wustl.cab2b.common.util.Utility
				.getOnlyEntityName(constraintEntity1
						.getDynamicExtensionsEntity());
				String secondEntityName = edu.wustl.cab2b.common.util.Utility
				.getOnlyEntityName(constraintEntity2
						.getDynamicExtensionsEntity());
				joinQueryNode.setFirstNodeExpressionId(Integer.parseInt(id1));
				joinQueryNode.setSecondNodeExpressionId(Integer.parseInt(id2));
				joinQueryNode.setFirstEntityName(firstEntityName);
				joinQueryNode.setSecondEntityName(secondEntityName);
				joinQueryNode.setJoinFormulaNodeList(joinFormulaList);
				joinQueryNode.setOperation("rePaint");
				joinQueryNode.setNodeView("AddLimit");
				joinQueryNode.setName(key);
				joinQueryNodeList.add(joinQueryNode);
			}
		}
	}

	/**
	 * @param joinFormulaList joinFormulaList
	 * @param jQUIMap JQUIMap
	 * @param customFormula customFormula
	 * @param key key
	 * @return flag
	 */
	private boolean sessionQTwoNodesJNode(List<JoinFormulaNode> joinFormulaList,
			Map<String, Map<String, JoinFormulaUIBean>> jQUIMap,
			ICustomFormula customFormula, String key)
	{
		boolean flag = false;
		Map<String, JoinFormulaUIBean> joinFormulaBeanMap = jQUIMap.get(key);
		//List<JoinFormulaNode> joinFormulaNodeList = new ArrayList<JoinFormulaNode>();
		/*JoinQueryNode joinQueryNode = new JoinQueryNode();

		joinQueryNode.setName(key);
		joinQueryNode.setFirstNodeExpressionId(Integer.parseInt(expId1));
		joinQueryNode.setSecondNodeExpressionId(Integer.parseInt(expId2));*/

		Set keySet = joinFormulaBeanMap.keySet();
		Iterator keySetItr = keySet.iterator();
		while (keySetItr.hasNext())
		{
			String innerKey = (String) keySetItr.next();
			JoinFormulaUIBean beanObj = joinFormulaBeanMap.get(innerKey);
			JoinFormulaNode joinFormulaNode = null;
			if (beanObj.getICustomFormula().equals(customFormula))
			{
				flag = true;
				joinFormulaNode = beanObj.getJoinFormulaNode();
				if (joinFormulaNode != null)
				{
					addInfoToJNCNode(innerKey, joinFormulaNode);
				}
				joinFormulaList.add(joinFormulaNode);
			}
		}
		return flag;
	}

	/**
	 * @param customNodeList customNodeList
	 * @param query query
	 * @param tQUIMap tQUIMap
	 * @param customFormula customFormula
	 * @param key key
	 */
	private void sessionQTwoNodesCNode(List<CustomFormulaNode> customNodeList, IQuery query,
			Map<String, CustomFormulaUIBean> tQUIMap, ICustomFormula customFormula, String key)
	{
		//This is the case for Multiple Node TQ
		CustomFormulaUIBean beanObj = tQUIMap.get(key);
		CustomFormulaNode customNode = null;
		if ((!beanObj.isCalculatedResult()) && (beanObj.getCf().equals(customFormula)))
		{
			customNode = beanObj.getTwoNode();
			if (customNode != null)
			{
				addInfoToTNCNode(query, key, customNode);
			}
		}
		if (customNode != null)
		{
			removeFromTwoNodesList(customNodeList, customNode.getName());
			customNodeList.add(customNode);
		}
	}

	/**
	 * @param customNodeList customNodeList
	 * @param customNodeName customNodeName
	 */
	private void removeFromTwoNodesList(List<CustomFormulaNode> customNodeList,
			String customNodeName)
	{
		for (int i = 0; i < customNodeList.size(); i++)
		{
			CustomFormulaNode cNode = customNodeList.get(i);
			if (cNode.getName().equals(customNodeName))
			{
				customNodeList.remove(i);
				break;
			}
		}
	}

	/**
	 * Sets the name of the join formula.
	 * @param key key
	 * @param joinFormulaNode joinFormulaNode
	 */
	private void addInfoToJNCNode(String key, JoinFormulaNode joinFormulaNode)
	{
		//String customColumnName;
		joinFormulaNode.setName(key);
		//customColumnName = setCustomColumnName(query);
		//joinFormulaNode.setCustomColumnName(customColumnName);
		//joinFormulaNode.setOperation(DAGConstant.REPAINT_OPERATION);
	}

	/**
	 * Add custom formula information like name,etc.
	 * @param query query
	 * @param key key
	 * @param customNode customNode
	 */
	private void addInfoToTNCNode(IQuery query, String key, CustomFormulaNode customNode)
	{
		String customColumnName;
		customNode.setName(key);
		customColumnName = setCustomColumnName(query);
		customNode.setCustomColumnName(customColumnName);
		customNode.setOperation(DAGConstant.REPAINT_OPERATION);
	}

	/**
	 * @param sNcustomNodeList sNcustomNodeList
	 * @param query query
	 * @param tQUIMap tQUIMap
	 * @param customFormula customFormula
	 * @param key key
	 */
	private void sessionQSingleNodeCNode(List<SingleNodeCustomFormulaNode> sNcustomNodeList,
			IQuery query, Map<String, CustomFormulaUIBean> tQUIMap,
			ICustomFormula customFormula, String key)
	{
		//This is the case for Single node TQ
		SingleNodeCustomFormulaNode singleNodeCF = null;
		CustomFormulaUIBean beanObj = tQUIMap.get(key);

		//when node is not a calculated result node and custom formulae are equal
		if ((!beanObj.isCalculatedResult()) && beanObj.getCf().equals(customFormula))
		{
			singleNodeCF = beanObj.getSingleNode();
			if (singleNodeCF != null)
			{
				addInfoToSNCNode(query, key, singleNodeCF);
			}
		}
		if (singleNodeCF != null)
		{
			removeFromSingleNode(sNcustomNodeList, singleNodeCF.getName());
			sNcustomNodeList.add(singleNodeCF);
		}
	}

	/**
	 * @param sNcustomNodeList sNcustomNodeList
	 * @param cfNodeName cfNodeName
	 */
	private void removeFromSingleNode(List<SingleNodeCustomFormulaNode> sNcustomNodeList,
			String cfNodeName)
	{
		for (int i = 0; i < sNcustomNodeList.size(); i++)
		{
			SingleNodeCustomFormulaNode node = sNcustomNodeList.get(i);
			if (node.getName().equals(cfNodeName))
			{
				sNcustomNodeList.remove(i);
				break;
			}
		}
	}

	/**
	 * @param query query
	 * @param key key
	 * @param singleNodeCF singleNodeCF
	 */
	private void addInfoToSNCNode(IQuery query, String key, SingleNodeCustomFormulaNode singleNodeCF)
	{
		String customColumnName;
		singleNodeCF.setName(key);
		customColumnName = setCustomColumnName(query);
		singleNodeCF.setCustomColumnName(customColumnName);
		singleNodeCF.setOperation(DAGConstant.REPAINT_OPERATION);
	}

	/**
	 * Repaint DAg when user navigates back from saved query.
	 * @param customNodeList customNodeList
	 * @param sNcustomNodeList sNcustomNodeList
	 * @param joinQueryNodeList joinQueryNodeList
	 * @param session session
	 * @param query query
	 * @param constraints constraints
	 * @param visibleExpression visibleExpression
	 * @param temporalMap temporalMap
	 * @param joinQueryMap joinQueryMap
	 */
	private void repaintFromSavedQuery(List<CustomFormulaNode> customNodeList,
			List<SingleNodeCustomFormulaNode> sNcustomNodeList,
			List<JoinQueryNode> joinQueryNodeList, HttpSession session, IQuery query,
			IConstraints constraints, Set<Integer> visibleExpression,
			Map<String, CustomFormulaUIBean> temporalMap,
			Map<String, Map<String, JoinFormulaUIBean>> joinQueryMap)
	{
		Map<String, CustomFormulaUIBean> tQUIMap = temporalMap;
		Map<String, Map<String, JoinFormulaUIBean>> jQUIMap = joinQueryMap;
		//Then this is the case of saved Query, so populate the map with Saved Query
		/*if (tQUIMap == null && jQUIMap == null)
		{*/
			tQUIMap = new HashMap<String, CustomFormulaUIBean>();
			jQUIMap = new HashMap<String, Map<String, JoinFormulaUIBean>>();
		//}
		for (Integer expressionId : visibleExpression)
		{
			IExpression exp = constraints.getExpression(expressionId.intValue());
			if (exp.containsCustomFormula())
			{
				Set<ICustomFormula> customFormulas = QueryUtility.getCustomFormulas(exp);
				checkingCustomFormulas(customNodeList, sNcustomNodeList, joinQueryNodeList, query,
						 tQUIMap, jQUIMap, exp, customFormulas);
			}
		}
		session.setAttribute(DAGConstant.TQUIMAP, tQUIMap);
		session.setAttribute(DAGConstant.JQUIMAP, jQUIMap);
	}

	/**
	 * @param customNodeList customNodeList
	 * @param sNcustomNodeList sNcustomNodeList
	 * @param joinQueryNodeList joinQueryNodeList
	 * @param query query
	 * @param constraints constraints
	 * @param tQUIMap tQUIMap
	 * @param jQUIMap jQUIMap
	 * @param exp expression
	 * @param customFormulas customFormulas
	 */
	private void checkingCustomFormulas(List<CustomFormulaNode> customNodeList,
			List<SingleNodeCustomFormulaNode> sNcustomNodeList,
			List<JoinQueryNode> joinQueryNodeList, IQuery query,
			Map<String, CustomFormulaUIBean> tQUIMap,
			Map<String, Map<String, JoinFormulaUIBean>> jQUIMap, IExpression exp,
			Set<ICustomFormula> customFormulas)
	{
		if (!customFormulas.isEmpty())
		{
			for (ICustomFormula c : customFormulas)
			{
				Set<IExpression> expressionSet = QueryUtility.getExpressionsInFormula(c);
				String lhsTerm = c.getLhs().getStringRepresentation();
				if ((!expressionSet.isEmpty()) && (expressionSet.size() == AQConstants.TWO))
				{
					if (!(lhsTerm.contains("-") || lhsTerm.contains("+")))
					{
						populateJoinNodeInfo(c, exp, joinQueryNodeList);
					}
					else
					{
						CustomFormulaNode customNode =
							populateCustomNodeInfo(c, exp);
						if (customNode != null)
						{
							savedQTwoNodesCNode(customNodeList,
									query, tQUIMap, c, customNode);
						}
					}
				}
				else if ((!expressionSet.isEmpty()) && (expressionSet.size() == 1))
				{
					savedQSingleNodeCNode(sNcustomNodeList,
							query, tQUIMap, exp, c);
				}
			}
			if (!joinQueryNodeList.isEmpty())
			{
				populateJQUIMapForSavedQuery(joinQueryNodeList,jQUIMap);
			}
		}
	}

	/**
	 * Populate join query UI map for saved query.
	 * @param joinQueryList joinQueryList
	 * @param jQUIMap jQUIMap
	 */
	private void populateJQUIMapForSavedQuery(List<JoinQueryNode> joinQueryList,
			Map<String, Map<String, JoinFormulaUIBean>> jQUIMap)
	{
		for (JoinQueryNode joinQueryNode : joinQueryList)
		{
			getJoinQueryNodeName(joinQueryNode, jQUIMap);
		}
	}

	/**
	 * @param joinQueryNode joinQueryNode
	 * @param jQUIMap jQUIMap
	 */
	private void getJoinQueryNodeName(JoinQueryNode joinQueryNode,
			Map<String, Map<String, JoinFormulaUIBean>> jQUIMap)
	{
		if (joinQueryNode.getName() == null)
		{
			String joinQueryNodeName = "";
			int joinNodeNumber = 1;
			boolean isContains = false;
			Set keySet = jQUIMap.keySet();

			if (keySet.isEmpty())
			{
				//This is the Initial case
				joinQueryNodeName = "j_" + joinQueryNode.getFirstNodeExpressionId() + "_"
						+ joinQueryNode.getSecondNodeExpressionId() + "_" + joinNodeNumber;
				joinQueryNode.setName(joinQueryNodeName);
				setJoinFormulaNodeName(joinQueryNode, jQUIMap);
			}
			else
			{
				while (joinNodeNumber <= keySet.size())
				{
					joinQueryNodeName = "j_" + joinQueryNode.getFirstNodeExpressionId() + "_"
					+ joinQueryNode.getSecondNodeExpressionId() + "_" + joinNodeNumber;
					joinQueryNode.setName(joinQueryNodeName);
					isContains = isKeySetContainsNodeName(joinQueryNodeName, keySet);
					if (isContains)
					{
						joinNodeNumber++;
					}
					else
					{
						break;
					}
				}
				setJoinFormulaNodeName(joinQueryNode, jQUIMap);
			}
			if (joinNodeNumber == (keySet.size() + 1) && isContains)
			{
				//By this time, customNodeNumber already exceeds the length of the KeySet,
				//so new  customNodeName is
				joinQueryNodeName = "j_" + joinQueryNode.getFirstNodeExpressionId() + "_"
						+ joinQueryNode.getSecondNodeExpressionId() + "_" + joinNodeNumber;
				joinQueryNode.setName(joinQueryNodeName);
				setJoinFormulaNodeName(joinQueryNode, jQUIMap);
			}
		}
	}

	/**
	 * @param joinQueryNode joinQueryNode
	 * @param jQUIMap jQUIMap
	 */
	private void setJoinFormulaNodeName(JoinQueryNode joinQueryNode,
			Map<String, Map<String, JoinFormulaUIBean>> jQUIMap)
	{
		int index = 1;
		Map<String, JoinFormulaUIBean> formulaBeanMap = new HashMap<String, JoinFormulaUIBean>();
		List<JoinFormulaNode> joinFormulaList = joinQueryNode.getJoinFormulaNodeList();
		for (JoinFormulaNode joinFormulaNode : joinFormulaList)
		{
			joinFormulaNode.setName(joinQueryNode.getName() + "_" + index);
			index++;
			ICustomFormula customFormula = customFormulaMap.get(counter++);
			JoinFormulaUIBean joinFormulaUIBean = createJQUIBean(customFormula, joinFormulaNode);
			formulaBeanMap.put(joinFormulaNode.getName(), joinFormulaUIBean);
		}
		joinQueryNode.setOperation(DAGConstant.REPAINT_OPERATION);
		joinQueryNode.setNodeView(DAGConstant.ADD_LIMIT_VIEW);
		jQUIMap.put(joinQueryNode.getName(), formulaBeanMap);
	}

	/**
	 * @param sNcustomNodeList sNcustomNodeList
	 * @param query query
	 * @param tQUIMap tQUIMap
	 * @param expression expression
	 * @param customFormula customFormula
	 */
	private void savedQSingleNodeCNode(List<SingleNodeCustomFormulaNode> sNcustomNodeList,
			IQuery query, Map<String, CustomFormulaUIBean> tQUIMap,
			IExpression expression, ICustomFormula customFormula)
	{
		SingleNodeCustomFormulaNode singleNodeCF =
			populateSingleNodeInfo(customFormula,expression);
		String singleNodeName = getCustomNodeName(singleNodeCF.getName(), tQUIMap);
		singleNodeCF.setName(singleNodeName);
		String customColumnName = setCustomColumnName(query);
		singleNodeCF.setCustomColumnName(customColumnName);
		singleNodeCF.setOperation(DAGConstant.REPAINT_OPERATION);

		//Node Limit is Set to "Add Limit" as it's a custom formula node
		//and not the calculated result node
		singleNodeCF.setNodeView(DAGConstant.ADD_LIMIT_VIEW);
		sNcustomNodeList.add(singleNodeCF);

		//Setting the node In the Map
		CustomFormulaUIBean bean = createTQUIBean(customFormula, null, singleNodeCF, null);
		tQUIMap.put(singleNodeName, bean);
	}

	/**
	 * @param customNodeList customNodeList
	 * @param query query
	 * @param tQUIMap tQUIMap
	 * @param customFormula customFormula
	 * @param customNode customNode
	 */
	private void savedQTwoNodesCNode(List<CustomFormulaNode> customNodeList, IQuery query,
			Map<String, CustomFormulaUIBean> tQUIMap, ICustomFormula customFormula,
			CustomFormulaNode customNode)
	{
		//Setting the custom Column Name
		String customColumnName = setCustomColumnName(query);
		String name = getCustomNodeName(customNode.getName(), tQUIMap);
		customNode.setName(name);
		customNode.setCustomColumnName(customColumnName);
		customNode.setOperation(DAGConstant.REPAINT_OPERATION);

		//Node Limit is Set to "Add Limit" as it's a custom formula node and not the calculated result node
		customNode.setNodeView(DAGConstant.ADD_LIMIT_VIEW);
		customNodeList.add(customNode);

		//Setting the node In the Map
		CustomFormulaUIBean bean = createTQUIBean(customFormula, customNode, null, null);
		tQUIMap.put(name, bean);
	}

	/**
	 * @param customFormula customFormula
	 * @param expression expression
	 * @return singleCNode
	 */
	private SingleNodeCustomFormulaNode populateSingleNodeInfo(ICustomFormula customFormula,
			 IExpression expression)
	{
		// TODO Auto-generated method stub
		SingleNodeCustomFormulaNode singleCNode = new SingleNodeCustomFormulaNode();
		//See how the name set is done
		singleCNode.setName(expression.getExpressionId() + "_" + expression.getExpressionId());
		singleCNode.setNodeExpressionId(expression.getExpressionId());
		//Setting the Entity Name
		String fullyQualifiedEntityName = expression.getQueryEntity().getDynamicExtensionsEntity()
				.getName();
		String entityName = Utility.parseClassName(fullyQualifiedEntityName);
		entityName = Utility.getDisplayLabel(entityName);
		singleCNode.setEntityName(entityName);
		//Seting the Arithmetic and Relational Operator
		ITerm lhs = customFormula.getLhs();
		IConnector<ArithmeticOperator> connector = lhs.getConnector(0, 1);
		singleCNode.setSelectedArithmeticOp(connector.getOperator().mathString());
		RelationalOperator relOperator = customFormula.getOperator();
		singleCNode.setSelectedLogicalOp(relOperator.getStringRepresentation());

		//Getting LHS Date Picker time Value
		/*IArithmeticOperand  lhsOperand =  lhs.getOperand(0);
		if(lhsOperand instanceof IDateLiteral)
		{
			IDateLiteral dateLit = (IDateLiteral)lhsOperand;
			singleCNode.setLhsTimeValue(getDateInGivenFormat(dateLit.getDate()));
			singleCNode.setLhsTimeInterval(DAGConstant.NULL_STRING);
		}*/
		//Handling RHS part
		List<ITerm> allRhs = customFormula.getAllRhs();
		if (!allRhs.isEmpty())
		{
			setDateOperand(singleCNode, allRhs);
		}

		//Handling LHS
		for (IArithmeticOperand element : lhs)
		{
			setArithmeticOperand(singleCNode, element);
		}
		return singleCNode;
	}

	/**
	 * @param singleCNode singleCNode
	 * @param element element
	 */
	private void setArithmeticOperand(SingleNodeCustomFormulaNode singleCNode,
			IArithmeticOperand element)
	{
		if (element instanceof DateLiteral)
		{
			IDateLiteral dateLit = (IDateLiteral) element;
			singleCNode.setLhsTimeValue(getDateInGivenFormat(dateLit.getDate()));
			singleCNode.setLhsTimeInterval(DAGConstant.NULL_STRING);

		}
		else if (element instanceof DateOffsetAttribute)
		{
			//This is the case of offset Attribute, means having datePicker as left operand
			//and dateoffset attribute as 2nd operand
			IDateOffsetAttribute dateOffSetAttr = (IDateOffsetAttribute) element;
			AttributeInterface attribute = dateOffSetAttr.getAttribute();
			singleCNode.setAttributeID(attribute.getId().toString());
			singleCNode.setAttributeName(attribute.getName());
			singleCNode.setAttributeType(attribute.getDataType());
			singleCNode.setQAttrInterval(dateOffSetAttr.getTimeInterval().name() + "s");
		}
		else if (element instanceof ExpressionAttribute)
		{
			IExpressionAttribute expAttr = (IExpressionAttribute) element;
			AttributeInterface attribute = expAttr.getAttribute();

			singleCNode.setAttributeID(attribute.getId().toString());
			singleCNode.setAttributeName(attribute.getName());
			singleCNode.setAttributeType(attribute.getDataType());
			singleCNode.setQAttrInterval(DAGConstant.NULL_STRING);
		}
	}

	/**
	 * @param singleCNode singleCNode
	 * @param allRhs allRhs
	 */
	private void setDateOperand(SingleNodeCustomFormulaNode singleCNode, List<ITerm> allRhs)
	{
		ITerm term = allRhs.get(0);
		IArithmeticOperand operand = term.getOperand(0);
		if (operand instanceof DateOffsetLiteral)
		{
			DateOffsetLiteral dateOffSetLit = (DateOffsetLiteral) operand;
			singleCNode.setTimeValue(dateOffSetLit.getOffset());
			singleCNode.setTimeInterval(dateOffSetLit.getTimeInterval().toString() + "s");
		}
		else if (operand instanceof DateLiteral)
		{
			IDateLiteral dateLit = (DateLiteral) operand;

			if (dateLit.getDate() == null)
			{
				singleCNode.setTimeValue("");
			}
			else
			{
				singleCNode.setTimeValue(getDateInGivenFormat(dateLit.getDate()));
			}
		}
	}

	/**
	 * @param query query
	 * @return customColumnName
	 */
	private String setCustomColumnName(IQuery query)
	{
		List<IOutputTerm> outputTermList = query.getOutputTerms();
		IOutputTerm outputTerm = outputTermList.get(0);

		String columnName = outputTerm.getName();
		//As custom column name consists of column Name , ( and Time Interval ),
		//so we need to parse it to get the exact column name
		int index = columnName.lastIndexOf('(');
		return columnName.substring(0, index);
	}

	/**
	 * @param customFormula customFormula
	 * @param srcExp srcExp
	 * @param joinQueryNodeList joinQueryNodeList
	 */
	private void populateJoinNodeInfo(ICustomFormula customFormula,
			IExpression srcExp, List<JoinQueryNode> joinQueryNodeList)
	{
		boolean flag = true;
		List<JoinFormulaNode> joinFormulaNodeList = new ArrayList<JoinFormulaNode>();
		JoinFormulaNode joinFormulaNode = new JoinFormulaNode();
		Set<IExpression> containingExpressions = QueryUtility.getExpressionsInFormula(customFormula);
		String secondEntityName = null;
		//IExpression destExp = null;
		int srcExpId = srcExp.getExpressionId();
		int destExpId = 0;
		String fullyQualifiedEntityName = srcExp.getQueryEntity().getDynamicExtensionsEntity()
				.getName();
		String firstEntityName = Utility.parseClassName(fullyQualifiedEntityName);
		firstEntityName = Utility.getDisplayLabel(firstEntityName);
		ITerm lhs = customFormula.getLhs();
		List<ITerm> rhsTermList = customFormula.getAllRhs();
		IArithmeticOperand rhs = rhsTermList.get(0).getOperand(0);
		for (IExpression exp : containingExpressions)
		{
			if (!exp.equals(srcExp))
			{
				//destExp = exp;
				destExpId = exp.getExpressionId();
				fullyQualifiedEntityName = exp.getQueryEntity().getDynamicExtensionsEntity()
						.getName();
				secondEntityName = Utility.parseClassName(fullyQualifiedEntityName);
				secondEntityName = Utility.getDisplayLabel(secondEntityName);
			}
		}
		for (JoinQueryNode queryNode : joinQueryNodeList)
		{
			if (queryNode.getFirstNodeExpressionId() == srcExpId
					&& queryNode.getSecondNodeExpressionId() == destExpId)
			{
				for (IArithmeticOperand element : lhs)
				{
					joinFormulaNode = populateJoinFormulaNode(element, rhs);
					customFormulaMap.put(customFormulaMapcounter++, customFormula);
				}
				queryNode.getJoinFormulaNodeList().add(joinFormulaNode);
				flag = false;
			}
		}
		if (flag)
		{
			JoinQueryNode joinQueryNode = new JoinQueryNode();
			joinQueryNode.setFirstNodeExpressionId(srcExpId);
			joinQueryNode.setFirstEntityName(firstEntityName);
			joinQueryNode.setSecondNodeExpressionId(destExpId);
			joinQueryNode.setSecondEntityName(secondEntityName);
			for (IArithmeticOperand element : lhs)
			{
				joinFormulaNode = populateJoinFormulaNode(element, rhs);
				customFormulaMap.put(customFormulaMapcounter++, customFormula);
			}
			joinFormulaNodeList.add(joinFormulaNode);
			joinQueryNode.setJoinFormulaNodeList(joinFormulaNodeList);
			joinQueryNodeList.add(joinQueryNode);
		}
	}

	/**
	 * Populate join formula node.
	 * @param lhs lhs
	 * @param rhs rhs
	 * @return joinFormulaNode
	 */
	private JoinFormulaNode populateJoinFormulaNode(IArithmeticOperand lhs,
			IArithmeticOperand rhs)
	{
		JoinFormulaNode joinFormulaNode = new JoinFormulaNode();

		ExpressionAttribute expAttr = (ExpressionAttribute) lhs;
		AttributeInterface attribute = expAttr.getAttribute();
		String dataType = getAttributeDataType(attribute);

		joinFormulaNode.setFirstAttributeId(attribute.getId().toString());
		joinFormulaNode.setFirstAttributeName(attribute.getName());
		joinFormulaNode.setFirstAttributeDataType(dataType);

		expAttr = (ExpressionAttribute) rhs;
		attribute = expAttr.getAttribute();
		dataType = getAttributeDataType(attribute);

		joinFormulaNode.setSecondAttributeId(attribute.getId().toString());
		joinFormulaNode.setSecondAttributeName(attribute.getName());
		joinFormulaNode.setSecondAttributeDataType(dataType);

		return joinFormulaNode;

	}

	/**
	 * Populate custom formula information.
	 * @param customFormula customFormula
	 * @param srcExp srcExp
	 * @return cNode
	 */
	private CustomFormulaNode populateCustomNodeInfo(ICustomFormula customFormula,
			IExpression srcExp)
	{
		CustomFormulaNode cNode = new CustomFormulaNode();
		Set<IExpression> containingExpressions = QueryUtility.getExpressionsInFormula(customFormula);
		String customNodeId = srcExp.getExpressionId() + "_";
		cNode.setFirstNodeExpId(srcExp.getExpressionId());
		ITerm lhs = customFormula.getLhs();
		IConnector<ArithmeticOperator> connector = lhs.getConnector(0, 1);
		cNode.setSelectedArithmeticOp(connector.getOperator().mathString());
		RelationalOperator relOperator = customFormula.getOperator();
		cNode.setSelectedLogicalOp(relOperator.getStringRepresentation());
		List<ITerm> allRhs = customFormula.getAllRhs();
		if (!allRhs.isEmpty())
		{
			populateCustomFormulaNode(cNode, allRhs);
		}
		for (IArithmeticOperand element : lhs)
		{
			setDateExpressionAttribute(srcExp, cNode, element);
		}
		customNodeId = setExpressionInCNode(srcExp, cNode,
				containingExpressions, customNodeId);
		cNode.setName(customNodeId);
		return cNode;
	}

	/**
	 * @param srcExp srcExp
	 * @param cNode cNode
	 * @param containingExpressions containingExpressions
	 * @param customNodeId customNodeId
	 * @return customNodeId
	 */
	private String setExpressionInCNode(IExpression srcExp,
			CustomFormulaNode cNode, Set<IExpression> containingExpressions,
			String customNodeId)
	{
		String fullyQualifiedEntityName = srcExp.getQueryEntity().getDynamicExtensionsEntity()
				.getName();
		String entityName = Utility.parseClassName(fullyQualifiedEntityName);
		entityName = Utility.getDisplayLabel(entityName);

		cNode.setFirstNodeName(entityName);
		for (IExpression exp : containingExpressions)
		{
			if (!exp.equals(srcExp))
			{
				cNode.setSecondNodeExpId(exp.getExpressionId());
				customNodeId = customNodeId + exp.getExpressionId();
				fullyQualifiedEntityName = exp.getQueryEntity().getDynamicExtensionsEntity()
						.getName();
				entityName = Utility.parseClassName(fullyQualifiedEntityName);
				entityName = Utility.getDisplayLabel(entityName);
				cNode.setSecondNodeName(entityName);
			}
		}
		return customNodeId;
	}

	/**
	 * @param cNode cNode
	 * @param allRhs allRhs
	 */
	private void populateCustomFormulaNode(CustomFormulaNode cNode,
			List<ITerm> allRhs)
	{
		ITerm term = allRhs.get(0);
		IArithmeticOperand operand = term.getOperand(0);
		if (operand instanceof DateOffsetLiteral)
		{
			DateOffsetLiteral dateOffSetLit = (DateOffsetLiteral) operand;
			cNode.setTimeValue(dateOffSetLit.getOffset());
			cNode.setTimeInterval(dateOffSetLit.getTimeInterval().toString() + "s");
		}
		else if (operand instanceof DateLiteral)
		{
			DateLiteral dateLit = (DateLiteral) operand;
			if (dateLit.getDate() == null)
			{
				cNode.setTimeValue("");
			}
			else
			{
				cNode.setTimeValue(getDateInGivenFormat(dateLit.getDate()));
				cNode.setTimeInterval(DAGConstant.NULL_STRING);
			}
		}
	}

	/**
	 * @param srcExp srcExp
	 * @param cNode cNode
	 * @param element element
	 */
	private void setDateExpressionAttribute(IExpression srcExp, CustomFormulaNode cNode,
			IArithmeticOperand element)
	{
		if (element instanceof DateOffsetAttribute)
		{
			DateOffsetAttribute dateOffSetAttr = (DateOffsetAttribute) element;
			AttributeInterface attribute = dateOffSetAttr.getAttribute();
			String dataType = getAttributeDataType(attribute);
			if (dateOffSetAttr.getExpression().getExpressionId() == srcExp.getExpressionId())
			{
				cNode.setFirstSelectedAttrId(attribute.getId().toString());
				cNode.setFirstSelectedAttrName(attribute.getName());
				cNode.setFirstSelectedAttrType(dataType);
				cNode.setQAttrInterval1(dateOffSetAttr.getTimeInterval().name() + "s");
			}
			else
			{
				cNode.setSecondSelectedAttrId(attribute.getId().toString());
				cNode.setSecondSelectedAttrName(attribute.getName());
				cNode.setSecondSelectedAttrType(dataType);
				cNode.setQAttrInterval2(dateOffSetAttr.getTimeInterval().name() + "s");
			}
		}
		else if (element instanceof ExpressionAttribute)
		{
			ExpressionAttribute expAttr = (ExpressionAttribute) element;
			AttributeInterface attribute = expAttr.getAttribute();
			String dataType = getAttributeDataType(attribute);

			if (expAttr.getExpression().getExpressionId() == srcExp.getExpressionId())
			{
				cNode.setFirstSelectedAttrId(attribute.getId().toString());
				cNode.setFirstSelectedAttrName(attribute.getName());
				cNode.setFirstSelectedAttrType(dataType);
				cNode.setQAttrInterval1(DAGConstant.NULL_STRING);
			}
			else
			{
				cNode.setSecondSelectedAttrId(attribute.getId().toString());
				cNode.setSecondSelectedAttrName(attribute.getName());
				cNode.setSecondSelectedAttrType(dataType);
				cNode.setQAttrInterval2(DAGConstant.NULL_STRING);
			}
		}
	}

	/**
	 * Get date in MM/dd/yyyy format.
	 * @param date date
	 * @return strDate
	 */
	private String getDateInGivenFormat(java.sql.Date date)
	{
		String strDate = "";

		SimpleDateFormat formatter = new SimpleDateFormat(CommonServiceLocator.getInstance().getDatePattern());

		strDate = formatter.format(date);

		return strDate;
	}

	/**
	 * Get data type of the specified attribute.
	 * @param attribute attribute
	 * @return dataType
	 */
	private String getAttributeDataType(AttributeInterface attribute)
	{
		String dataType = attribute.getDataType();
		if (dataType.equals(AQConstants.DATE_TYPE))
		{
			dataType = AQConstants.DATE_TYPE;
		}
		else
		{
			if ((dataType.equals(AQConstants.INTEGER_TYPE) || dataType.equals(AQConstants.LONG_TYPE)
					|| dataType.equals(AQConstants.DOUBLE_TYPE)
					|| dataType.equals(AQConstants.FLOAT_TYPE) || dataType
					.equals(AQConstants.SHORT_TYPE))
					&& (!attribute.getName().equals("id")))
			{
				dataType = AQConstants.INTEGER_TYPE;
			}
		}
		return dataType;
	}

	/**
	 * @param expressionId expressionId
	 * @param node node
	 * @param constraints constraints
	 * @param intraModelAssociationList intraModelAssociationList
	 */
	private void nodeform(int expressionId, DAGNode node, IConstraints constraints,
			List<IIntraModelAssociation> intraModelAssociationList)
	{
		IJoinGraph graph = constraints.getJoinGraph();
		IExpression expression = constraints.getExpression(expressionId);

		List<IExpression> childList = graph.getChildrenList(expression);

		for (IExpression exp : childList)
		{
			//int newExpId = childList.get(i);
			//IExpression exp = constraints.getExpression(newExpId);
			IQueryEntity constraintEntity = exp.getQueryEntity();
			/*	Code to get IPath Object*/
			IIntraModelAssociation association = (IIntraModelAssociation) (graph.getAssociation(
					expression, exp));

			intraModelAssociationList.add(association);

			if (exp.isVisible())
			{
				IPath pathObj = mPathFinder.getPathForAssociations(intraModelAssociationList);
				long pathId = pathObj.getPathId();

				DAGNode dagNode = new DAGNode();
				dagNode.setExpressionId(exp.getExpressionId());
				dagNode.setNodeName(edu.wustl.cab2b.common.util.Utility
						.getOnlyEntityName(constraintEntity.getDynamicExtensionsEntity()));
				dagNode.setToolTip(exp);

				/*	Adding Dag Path in each visible list which have childrens*/
				String pathStr = Long.valueOf(pathId).toString();
				DAGPath dagPath = new DAGPath();
				dagPath.setToolTip(getPathDisplayString(pathObj));
				dagPath.setId(pathStr);
				dagPath.setSourceExpId(node.getExpressionId());
				dagPath.setDestinationExpId(dagNode.getExpressionId());

				String key = pathStr + "_" + node.getExpressionId() + "_"
						+ dagNode.getExpressionId();
				mPathMap.put(key, pathObj);

				node.setDagpathList(dagPath);
				node.setAssociationList(dagNode);
				intraModelAssociationList.clear();
			}
			else
			{
				nodeform(exp.getExpressionId(), node, constraints, intraModelAssociationList);
			}
		}
	}

	/**
	 * Update the logical operator.
	 * @param parentExpId parentExpId
	 * @param parentIndex parentIndex
	 * @param operator operator
	 */

	public void updateLogicalOperator(int parentExpId, int parentIndex, String operator)
	{
		int parentExpressionId = parentExpId;
		IQuery query = mQueryObject.getQuery();
		IExpression parentExpression = query.getConstraints().getExpression(parentExpressionId);
		LogicalOperator logicOperator = edu.wustl.cab2b.client.ui.query.Utility
				.getLogicalOperator(operator);
		int childIndex = parentIndex + 1;
		parentExpression.setConnector(parentIndex, childIndex, QueryObjectFactory
				.createLogicalConnector(logicOperator));
		mQueryObject.setQuery(query);

	}

	/**
	 * Edit limit.
	 * @param expId expId
	 * @return map
	 */
	public Map editAddLimitUI(int expId)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		int expressionId = expId;
		IExpression expression = mQueryObject.getQuery().getConstraints().getExpression(
				expressionId);
		EntityInterface entity = expression.getQueryEntity().getDynamicExtensionsEntity();
		HtmlProvider generateHTMLBizLogic = new HtmlProvider(null);
		Rule rule = ((Rule) (expression.getOperand(0)));
		List<ICondition> conditions = Collections.list(rule);
		String html = generateHTMLBizLogic.generateHTML(entity, conditions);
		map.put(DAGConstant.HTML_STR, html);
		map.put(DAGConstant.EXPRESSION, expression);
		return map;
	}

	/**
	 * Add node to output view.
	 * @param nodeId id
	 * @return node
	 */
	public DAGNode addNodeToOutPutView(String nodeId)
	{
		DAGNode node = null;
		if (!nodeId.equalsIgnoreCase(""))
		{
			Long entityId = Long.parseLong(nodeId);
			EntityInterface entity = EntityCache.getCache().getEntityById(entityId);
			int expressionId = ((ClientQueryBuilder) mQueryObject).addExpression(entity);
			node = createNode(expressionId, true);
		}
		return node;
	}

	/**
	 * Restore query object.
	 */
	public void restoreQueryObject()
	{
		HttpServletRequest request = flex.messaging.FlexContext.getHttpRequest();
		HttpSession session = request.getSession();
		IQuery query = (IQuery) session.getAttribute(DAGConstant.QUERY_OBJECT);
		mQueryObject.setQuery(query);
	}

	/**
	 * Delete expression.
	 * @param expId expId
	 */
	public void deleteExpression(int expId)
	{
		int expressionId = expId;
		mQueryObject.removeExpression(expressionId);
		boolean hasCustomFormula = false;

		IConstraints constraints = mQueryObject.getQuery().getConstraints();
		for (IExpression exp : constraints)
		{
			List<ICustomFormula> customFormulaList = new ArrayList<ICustomFormula>();
			if (exp.containsCustomFormula())
			{
				int noOfOperands = exp.numberOfOperands();
				for (int i = 0; i < noOfOperands; i++)
				{
					if (exp.getOperand(i) instanceof ICustomFormula)
					{
						ICustomFormula customFormula = (ICustomFormula) exp.getOperand(i);
						ITerm lhs = customFormula.getLhs();
						int noOfLhsOperands = lhs.numberOfOperands();
						for (int j = 0; j < noOfLhsOperands; j++)
						{
							StringBuffer expIdFromLhs = processForTerms(lhs, j);
							int expIdIntValue = Integer.parseInt(expIdFromLhs.toString());
							if (expIdIntValue == expId)
							{
								hasCustomFormula = true;
								break;
							}
						}

						if (!hasCustomFormula)
						{
							List<ITerm> rhsList = customFormula.getAllRhs();
							for (ITerm rhs : rhsList)
							{
								int noOfRhsOperands = rhs.numberOfOperands();
								for (int k = 0; k < noOfRhsOperands; k++)
								{
									StringBuffer expIdFromLhs = processForTerms(
											rhs, k);
									int expIdIntValue = Integer.parseInt(expIdFromLhs.toString());
									if (expIdIntValue == expId)
									{
										hasCustomFormula = true;
										break;
									}
								}
								if (hasCustomFormula)
								{
									break;
								}
							}
						}
						if (hasCustomFormula)
						{
							customFormulaList.add(customFormula);
						}
					}
				}
			}
			for (ICustomFormula custFormula : customFormulaList)
			{
				exp.removeOperand(custFormula);
			}
		}
	}

	/**
	 * @param term term
	 * @param cnt counter
	 * @return expIdFromLhs
	 */
	private StringBuffer processForTerms(ITerm term, int cnt)
	{
		StringBuffer expIdFromTerm = new StringBuffer();
		String arithmeticTermOp = term.getOperand(cnt).toString();
		int index = arithmeticTermOp.indexOf(':');
		String subString = arithmeticTermOp.substring
		(index, arithmeticTermOp.length() - 1);
		for (int l = 0; l < subString.length(); l++)
		{
			if (subString.charAt(l) == ',')
			{
				break;
			}
			if (subString.charAt(l) != ':' && subString.charAt(l) != ' ')
			{
				expIdFromTerm.append(subString.charAt(l));
			}
		}
		return expIdFromTerm;
	}

	/**
	 * Add expression to view.
	 * @param expId expId
	 */
	public void addExpressionToView(int expId)
	{
		int expressionId = expId;
		Expression expression = (Expression) mQueryObject.getQuery().getConstraints()
				.getExpression(expressionId);
		expression.setInView(true);
	}

	/**
	 * Delete expression from view.
	 * @param expId expId
	 */
	public void deleteExpressionFormView(int expId)
	{
		int expressionId = expId;
		Expression expression = (Expression) mQueryObject.getQuery().getConstraints()
				.getExpression(expressionId);
		expression.setInView(false);
	}

	/**
	 * Delete path.
	 * @param pathName pathName
	 * @param linkedNodeList linkedNodeList
	 */
	public void deletePath(String pathName, List<DAGNode> linkedNodeList)
	{
		IPath path = mPathMap.remove(pathName);
		int sourceexpressionId = linkedNodeList.get(0).getExpressionId();
		int destexpressionId = linkedNodeList.get(1).getExpressionId();
		List<IAssociation> associations = path.getIntermediateAssociations();
		IConstraints constraints = mQueryObject.getQuery().getConstraints();
		JoinGraph graph = (JoinGraph) constraints.getJoinGraph();
		IExpression srcExpression = constraints.getExpression(sourceexpressionId);
		IExpression destExpression = constraints.getExpression(destexpressionId);
		List<IExpression> expressions = graph.getIntermediateExpressions(srcExpression,
				destExpression, associations);
		// If the association is direct association, remove the respective association
		if (expressions.isEmpty())
		{
			mQueryObject.removeAssociation(sourceexpressionId, destexpressionId);
		}
		else
		{
			for (int i = 0; i < expressions.size(); i++)
			{
				//m_queryObject.removeExpression(expression.getExpressionId());
				mQueryObject.removeExpression(expressions.get(i).getExpressionId());
			}
		}
	}
}