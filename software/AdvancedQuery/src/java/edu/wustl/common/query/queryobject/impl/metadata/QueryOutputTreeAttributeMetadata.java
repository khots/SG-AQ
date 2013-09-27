/**
 *
 */

package edu.wustl.common.query.queryobject.impl.metadata;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.query.util.global.AQConstants;

/**
 * @author prafull_kadam
 * Class to store meta data of the output tree attributes.
 * This will store the attribute of an output entity &
 * its corresponding column name in the temporary table created while executing Suite Advance query.
 */
public class QueryOutputTreeAttributeMetadata
{

	/**
	 * Reference to the attribute for which the meta data is to be stored.
	 */
	private final AttributeInterface attribute;
	/**
	 * The name of the column in the temporary table for the given attribute.
	 */
	private final String columnName;

	/**
	 * The name to be displayed.
	 */
	private final String displayName;

	/**
	 * tree data node.
	 */
	private final OutputTreeDataNode treeDataNode;

	/**
	 * Constructor to instantiate this object.
	 * @param attribute The reference to the DE attribute.
	 * @param columnName The name of the column for the attribute passed in the temporary table.
	 * @param treeDataNode TODO
	 * @param displayName TODO
	 */
	public QueryOutputTreeAttributeMetadata(AttributeInterface attribute, String columnName,
			OutputTreeDataNode treeDataNode, String displayName)
	{
		this.attribute = attribute;
		this.columnName = columnName;
		this.treeDataNode = treeDataNode;
		this.displayName = displayName;
	}

	/**
	 * To get the attribute whose meta data is stored in this class.
	 * @return the attribute reference
	 */
	public AttributeInterface getAttribute()
	{
		return attribute;
	}

	/**
	 * TO get the table column name of the attribute.
	 * @return the columnName
	 */
	public String getColumnName()
	{
		return columnName;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * @return the treeDataNode
	 */
	public OutputTreeDataNode getTreeDataNode()
	{
		return treeDataNode;
	}

	/**
	 * Get the unique id.
	 * @return uniqueId
	 */
	public String getUniqueId()
	{
		String uniqueId = this.getTreeDataNode().getExpressionId() + AQConstants.EXPRESSION_ID_SEPARATOR
				+ this.attribute.getId();
		return uniqueId;
	}
}
