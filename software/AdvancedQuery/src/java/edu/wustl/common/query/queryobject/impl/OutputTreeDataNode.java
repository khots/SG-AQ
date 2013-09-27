package edu.wustl.common.query.queryobject.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.query.util.global.AQConstants;

/**
 * @author prafull_kadam
 * Output tree node Class for Advance Query tree.
 * Only SQLGenerator class will instantiate objects of this class,
 * & It is expected that other classes should use getter methods only.
 */
public class OutputTreeDataNode implements Serializable
{

	/**
	 *
	 */
	private static final long serialVersionUID = 4865638917714061869L;

	/**
	 * The auto generated identifier.
	 */
	private final long identifier;

	/**
	 * This increments each time to generate unique identifier.
	 */
	private static long lastId = 0;

	/**
	 * Object of OutputTreeDataNode.
	 */
	private OutputTreeDataNode parentNode;

	/**
	 * List of OutputTreeDataNode objects.
	 */
	private final List<OutputTreeDataNode> children = new ArrayList<OutputTreeDataNode>();

	/**
	 * Object of OutputEntity.
	 */
	private final IOutputEntity outputEntity;

	/**
	 * The tree number.
	 */
	private int treeNo;

	/**
	 * Expression id.
	 */
	private final int expressionId;

	/**
	 * Is in view.
	 */
	private final boolean isInView;
	/**
	 * List of QueryOutputTreeAttributeMetadata objects.
	 */
	private List<QueryOutputTreeAttributeMetadata> attributes =
		new ArrayList<QueryOutputTreeAttributeMetadata>();

	/**
	 * The Constructor to instantiate the object of this class.
	 * @param outputEntity The reference to the output Entity.
	 * @param expressionId The expression id corresponding to this Output tree.
	 * @param treeNo The integer representing tree no.
	 */
	public OutputTreeDataNode(IOutputEntity outputEntity, int expressionId, int treeNo, boolean isInView)
	{
		this.outputEntity = outputEntity;
		this.expressionId = expressionId;
		identifier = lastId++;
		this.treeNo = treeNo;
		this.isInView = isInView;
	}

	/**
	 * @return the isInView
	 */
	public boolean isInView()
	{
		return isInView;
	}
	/**
	 * To get the auto generated id for the class instance.
	 * @return The long value representing auto generated id for the class instance.
	 * @see edu.wustl.common.querysuite.queryobject.IOutputTreeNode#getId()
	 */
	public long getId()
	{
		return identifier;
	}

	/**
	 * To add the child node.
	 * @param outputEntity The output entity of the child node to be added.
	 * @param expressionId The expression id corresponding to this Output tree.
	 * @return The reference to the child node.
	 */
	public OutputTreeDataNode addChild(IOutputEntity outputEntity, int expressionId, boolean isInView)
	{
		OutputTreeDataNode child = new OutputTreeDataNode(outputEntity, expressionId, treeNo, isInView);
		child.parentNode = this;
		children.add(child);

		return child;
	}

	/**
	 * @return The list all children of this node
	 */
	public List<OutputTreeDataNode> getChildren()
	{
		return children;
	}

	/**
	 * @return The reference to the output Entity associated with this node.
	 */
	public IOutputEntity getOutputEntity()
	{
		return outputEntity;
	}

	/**
	 * @return Returns the reference to the parent node, null if its root node.
	 */
	public OutputTreeDataNode getParent()
	{
		return parentNode;
	}

	/**
	 * @return the expressionId The expression id corresponding to this expression.
	 */
	public int getExpressionId()
	{
		return expressionId;
	}

	/**
	 * @return the treeNo
	 */
	public int getTreeNo()
	{
		return treeNo;
	}

	/**
	 * Sets the tree no.
	 *
	 * @param treeNo the new tree no
	 */
	public void setTreeNo(int treeNo)
	{
		this.treeNo = treeNo;
	}

	/**
	 * @param obj the object to be compared.
	 * @return true if following attributes of both object matches:
	 * 			- id
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean flag = false;
		if (this == obj)
		{
			flag = true;
		}
		else
		{
			if (obj != null && this.getClass() == obj.getClass())
			{
				OutputTreeDataNode node = (OutputTreeDataNode) obj;
				if (this.identifier == node.getId())
				{
					flag = true;
				}

			}
		}
		return flag;
	}

	/**
	 * To get the HashCode for the object. It will be calculated based on Following attributes:
	 * 			- treeNo
	 * 	 		- id
	 * @return The hash code value for the object.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		//		hash = hash * Constants.HASH_PRIME + (int) treeNo;
		hash = hash * AQConstants.HASH_PRIME + (int) identifier;
		return hash;
	}

	/**
	 * @return String representation of object in the form: [outputEntity : parentNode]
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + identifier + ":" + outputEntity.toString() + "("
				+ (parentNode == null ? "-" : Long.toString(parentNode.getId())) + ")" + "]";
	}

	/**
	 * @return the attributes
	 */
	public List<QueryOutputTreeAttributeMetadata> getAttributes()
	{
		return attributes;
	}

	/**
	 * @param attributes the attributes to set.
	 */
	public void setAttributes(List<QueryOutputTreeAttributeMetadata> attributes)
	{
		this.attributes = attributes;
	}

	/**
	 * Returns a unique id for each node of each tree.
	 * @return String id
	 */
	public String getUniqueNodeId()
	{
		return this.treeNo + "_" + identifier;
	}

	/**
	 * To add attribute metadata Object in the list.
	 * @param attribute The reference to the attribute metadata object.
	 */
	public void addAttribute(QueryOutputTreeAttributeMetadata attribute)
	{
		attributes.add(attribute);
	}

	/**
	 * TO get the metadata object for the given attribute.
	 * @param attribute The reference to the attribute, this attribute must be part of this outputTreeDataNode.
	 * @return The reference to the QueryOutputTreeAttributeMetadata.
	 */
	public QueryOutputTreeAttributeMetadata getAttributeMetadata(AttributeInterface attribute)
	{
		QueryOutputTreeAttributeMetadata resultAttrMetadata = null;
		for (QueryOutputTreeAttributeMetadata attributeMetadata : attributes)
		{
			if (attributeMetadata.getAttribute().equals(attribute))
			{
				resultAttrMetadata = attributeMetadata;
			}
		}
		return resultAttrMetadata;
	}

}
