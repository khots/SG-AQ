
package edu.wustl.query.util.querysuite;

import java.util.List;
import java.util.Map;

import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;

public class TemporalColumnUIBean
{
	/**
	 * node.
	 */
	private OutputTreeDataNode node;
	/**
	 * query.
	 */
	private String sql;
	/**
	 * column list.
	 */
	private List<String> columnsList;
	/**
	 * output term columns.
	 */
	private Map<String, IOutputTerm> outputTermsColumns;
	/**
	 * column index.
	 */
	private int columnIndex = 0;
	/**
	 * constraints.
	 */
	private IConstraints constraints;
	/**
	 * Empty constructor.
	 */
	public TemporalColumnUIBean()
	{

	}
	/**
	 * Parameterized constructor
	 * @param node OutputTreeDataNode
	 * @param sql SQL
	 * @param columnsList columnsList
	 * @param outputTermsColumns columns added for temporal condition
	 * @param columnIndex index of temporal column
	 * @param constraints constraints
	 */
	public TemporalColumnUIBean(OutputTreeDataNode node, String sql,
			List<String> columnsList, Map<String, IOutputTerm> outputTermsColumns, int columnIndex,
			IConstraints constraints)
	{
		this.node = node;
		this.sql = sql;
		this.columnsList = columnsList;
		this.outputTermsColumns = outputTermsColumns;
		this.columnIndex = columnIndex;
		this.constraints = constraints;
	}

	/**
	 * @return the columnIndex
	 */
	public int getColumnIndex()
	{
		return columnIndex;
	}

	/**
	 * @param columnIndex the columnIndex to set
	 */
	public void setColumnIndex(int columnIndex)
	{
		this.columnIndex = columnIndex;
	}

	/**
	 * @return the columnsList
	 */
	public List<String> getColumnsList()
	{
		return columnsList;
	}

	/**
	 * @param columnsList the columnsList to set
	 */
	public void setColumnsList(List<String> columnsList)
	{
		this.columnsList = columnsList;
	}

	/**
	 * @return the node
	 */
	public OutputTreeDataNode getNode()
	{
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(OutputTreeDataNode node)
	{
		this.node = node;
	}

	/**
	 * @return the outputTermsColumns
	 */
	public Map<String, IOutputTerm> getOutputTermsColumns()
	{
		return outputTermsColumns;
	}

	/**
	 * @param outputTermsColumns the outputTermsColumns to set
	 */
	public void setOutputTermsColumns(Map<String, IOutputTerm> outputTermsColumns)
	{
		this.outputTermsColumns = outputTermsColumns;
	}

	/**
	 * @return the sql
	 */
	public String getSql()
	{
		return sql;
	}

	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql)
	{
		this.sql = sql;
	}

	/**
	 * @return the constraints
	 */
	public IConstraints getConstraints()
	{
		return constraints;
	}

	/**
	 * @param constraints the constraints to set
	 */
	public void setConstraints(IConstraints constraints)
	{
		this.constraints = constraints;
	}

}
