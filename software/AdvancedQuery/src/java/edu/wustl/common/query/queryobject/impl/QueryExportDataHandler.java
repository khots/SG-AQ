package edu.wustl.common.query.queryobject.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;

/**
 * This class is responsible for exporting the query results
 * (i.e. displaying results in spreadsheet in denormalized form)
 * @author pooja_tavase
 *
 */
public class QueryExportDataHandler
{
	protected Map<QueryHeaderData, Integer> entityVsMaxCount =
		new HashMap<QueryHeaderData, Integer>();
	private final EntityInterface rootEntity;
	private final IConstraints constraints;
	private Map<IExpression,BaseAbstractAttributeInterface> expVsAssoc =
		new HashMap<IExpression, BaseAbstractAttributeInterface>();
	private Map<IExpression,BaseAbstractAttributeInterface> tgtExpVsAssoc =
		new HashMap<IExpression, BaseAbstractAttributeInterface>();

	public QueryExportDataHandler(IExpression rootExp, IConstraints constraints)
	{
		if(rootExp == null)
		{
			rootEntity = null;
		}
		else
		{
			rootEntity = rootExp.getQueryEntity().getDynamicExtensionsEntity();
		}
		this.constraints = constraints;
	}

	public EntityInterface getRootExp()
	{
		return rootEntity;
	}

	/**
	 * @param entityVsAssoc the entityVsAssoc to set
	 */
	public void setExpVsAssoc(Map<IExpression,BaseAbstractAttributeInterface> expVsAssoc)
	{
		this.expVsAssoc = expVsAssoc;
	}

	/**
	 * @return the entityVsAssoc
	 */
	public Map<IExpression,BaseAbstractAttributeInterface> getExpVsAssoc()
	{
		return expVsAssoc;
	}

	public void setTgtExpVsAssoc(
			Map<IExpression, BaseAbstractAttributeInterface> tgtExpVsAssoc)
	{
		this.tgtExpVsAssoc = tgtExpVsAssoc;
	}

	/**
	 * @return the tgtEntityVsAssoc
	 */
	public Map<IExpression, BaseAbstractAttributeInterface> getTgtExpVsAssoc()
	{
		return tgtExpVsAssoc;
	}

	/**
	 * Gets the list of associations & attributes for the passed entity.
	 * @param expression entity
	 * @return finalList finalList
	 */
	private Collection<AbstractAttributeInterface> getAbstractAttributeCollection(
			IExpression expression)
	{
		Collection<AbstractAttributeInterface> finalList = new ArrayList<AbstractAttributeInterface>();
		Collection<AttributeInterface> attributeList =
			expression.getQueryEntity().getDynamicExtensionsEntity().getAllAttributes();
		Collection<AbstractAttributeInterface> associationList = getActualAssociations(expression);
		finalList.addAll(attributeList);
		finalList.addAll(associationList);
		for(IExpression exp : expVsAssoc.keySet())
		{
			if(exp.equals(expression))
			{
				AbstractAttributeInterface assocInterface = (AbstractAttributeInterface)expVsAssoc.get(exp);
				if(assocInterface != null && !finalList.contains(assocInterface))
				{
					finalList.add(assocInterface);
				}
			}
		}
		return finalList;
	}

	/**
	 * Gets only those associations that are present in the formed query.
	 * @param expression expression
	 * @return finalList
	 */
	private List<AbstractAttributeInterface> getActualAssociations(
			IExpression expression)
	{
		List<AbstractAttributeInterface> assocList = new ArrayList<AbstractAttributeInterface>();
		JoinGraph joinGraph = (JoinGraph)constraints.getJoinGraph();
		IIntraModelAssociation association;
		for(IExpression exp : constraints)
		{
			if(joinGraph.containsAssociation(expression, exp))
			{
				association =
					(IIntraModelAssociation)joinGraph.getAssociation(expression, exp);
				if(!assocList.contains(association.getDynamicExtensionsAssociation()))
				{
					assocList.add(association.getDynamicExtensionsAssociation());
				}
			}
		}
		return assocList;
	}

	/**
	 * Returns a list which contains data related only to the entity passed
	 * (This is possible when the query contains an association twice & so there is list of data
	 * for more than one entity corresponding to same association).
	 * @param tempList tempList
	 * @param expression entity
	 * @param selectedColumnsMetadata
	 * @return newList
	 */
	public List updateTempList(List tempList, IExpression expression, SelectedColumnsMetadata selectedColumnsMetadata)
	{
		List newList = new ArrayList();
		Map<OutputAssociationColumn, Object> newMap;
		Collection<AbstractAttributeInterface> attributeList = getAbstractAttributeCollection(expression);
		for(int cnt=0;cnt<tempList.size();cnt++)
		{
			newMap = new HashMap<OutputAssociationColumn, Object>();
			Map<OutputAssociationColumn, Object> obj =
				(Map<OutputAssociationColumn, Object>) tempList.get(cnt);
			for(OutputAssociationColumn attribute : obj.keySet())
			{
				if(attributeList.contains(attribute.getAbstractAttr()))
				{
					newMap.put(attribute, obj.get(attribute));
				}
			}
			// this is needed id node is added in view mode & there is no record for it to display then empty record needs to be added in tree.
			if(obj.isEmpty())
			{
				newMap = getEmptyRecordMap(expression,selectedColumnsMetadata);
			}
			if(!newMap.isEmpty())
			{
				newList.add(newMap);
			}
		}


		return newList;
	}

	private Map<OutputAssociationColumn, Object> getEmptyRecordMap(IExpression expression,
			SelectedColumnsMetadata selectedColumnsMetadata)
	{
		Map<OutputAssociationColumn, Object> record = new HashMap<OutputAssociationColumn, Object>();

		Collection<AttributeInterface> attributeList = expression.getQueryEntity()
				.getDynamicExtensionsEntity().getAttributeCollection();

		for (AttributeInterface attribute : attributeList)
		{

			OutputAttributeColumn opAttrCol = null;
			String value;
			int columnIndex = -1;

			for (QueryOutputTreeAttributeMetadata outputTreeAttributeMetadata : selectedColumnsMetadata
					.getSelectedAttributeMetaDataList())
			{
				columnIndex++;
				BaseAbstractAttributeInterface presentAttribute = outputTreeAttributeMetadata
						.getAttribute();
				if (presentAttribute.equals(attribute)
						&& outputTreeAttributeMetadata.getTreeDataNode().getExpressionId() == expression
								.getExpressionId())
				{
					value = " ";
					opAttrCol = new OutputAttributeColumn(value, columnIndex, attribute,
							expression, null);
					break;
				}
			}

			if (opAttrCol != null)
			{
				OutputAssociationColumn opAssocCol = new OutputAssociationColumn(attribute,
						expression, null);
				record.put(opAssocCol, opAttrCol);
			}

		}

		return record;
	}
	/**
	 * Add all the associations (of parents) and then
	 * filter the list to contain only the required attributes and associations.
	 * @param queryHeaderData queryHeaderData
	 * @return attributeList
	 */
	public List<AbstractAttributeInterface> getFinalAttributeList(
			EntityInterface entity)
	{
		List<AbstractAttributeInterface> attributeList =
			(List<AbstractAttributeInterface>) entity.getAllAbstractAttributes();
		List<AbstractAttributeInterface> newAttributeList = new ArrayList<AbstractAttributeInterface>();
		for(AbstractAttributeInterface attribute : attributeList)
		{
			populateNewAttributeList(entity, newAttributeList, attribute);
		}
		for(IExpression exp : expVsAssoc.keySet())
		{
			populateNewAttrList(entity, newAttributeList, exp);
		}
		return newAttributeList;
	}

	/**
	 * Populate the final attribute list by adding all the associations (of parents) and then
	 * filter the list to contain only the required attributes and associations.
	 * @param entity entity
	 * @param newAttributeList  newAttributeList
	 * @param exp expression
	 */
	private void populateNewAttrList(EntityInterface entity,
			List<AbstractAttributeInterface> newAttributeList, IExpression exp) {
		if(exp.getQueryEntity().getDynamicExtensionsEntity().equals(entity))
		{
			AbstractAttributeInterface assocInterface = (AbstractAttributeInterface)expVsAssoc.get(exp);
			if(assocInterface != null && !newAttributeList.contains(assocInterface))
			{
				newAttributeList.add(assocInterface);
			}
		}
	}

	/**
	 * Populate new attribute list.
	 * @param entity entity
	 * @param newAttributeList newAttributeList
	 * @param attribute attribute
	 */
	private void populateNewAttributeList(EntityInterface entity,
			List<AbstractAttributeInterface> newAttributeList,
			AbstractAttributeInterface attribute)
	{
		if(attribute instanceof AssociationInterface)
		{
			if(!((AssociationInterface)attribute).getTargetEntity().getName().
					equals(entity.getName()))
			{
				newAttributeList.add(attribute);
			}
		}
		else
		{
			newAttributeList.add(attribute);
		}
	}

	/**
	 * Get maximum record count for a particular QueryHeaderData object.
	 * @param queryHeaderData queryHeaderData
	 * @return maxCount
	 */
	public Integer getMaxRecordCountForQueryHeader(
			QueryHeaderData queryHeaderData)
	{
		Integer maxCount = entityVsMaxCount.get(queryHeaderData);
		if (maxCount == null)
		{
			maxCount = 0;
		}
		return maxCount;
	}
}
