/**
 *
 */
package edu.wustl.query.flex.dag;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

/**
 * @author sagar_baldwa
 *
 */

public class JoinQueryNode implements Externalizable
{
	/**
	 * Name.
	 */
	private String name;
	/**
	 * Join Formula Node list.
	 */
	private List<JoinFormulaNode> jFormulaNodeList;
	/**
	 * First Node Expression id.
	 */
	private int firstNodeExprId;

	/**
	 * Second Expression Node id.
	 */
	private int secondNodeExprId;

	/**
	 * First Entity Name.
	 */
	private String firstEntityName;

	/**
	 * Second Entity name.
	 */
	private String secondEntityName;

	/**
	 * Operation.
	 */
	private String operation;

	/**
	 * Node view.
	 */
	private String nodeView = "";

	/**
	 * @return the nodeView
	 */
	public String getNodeView()
	{
		return nodeView;
	}

	/**
	 * @param nodeView the nodeView to set
	 */
	public void setNodeView(String nodeView)
	{
		this.nodeView = nodeView;
	}

	/**
	 * @return the operation
	 */
	public String getOperation()
	{
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation)
	{
		this.operation = operation;
	}

	/**
	 * @return first expression id
	 */
	public int getFirstNodeExpressionId()
	{
		return firstNodeExprId;
	}

	/**
	 * @param firstNodeExprId first node expression id
	 */
	public void setFirstNodeExpressionId(int firstNodeExprId)
	{
		this.firstNodeExprId = firstNodeExprId;
	}

	/**
	 * @return secondNodeExpressionId
	 */
	public int getSecondNodeExpressionId()
	{
		return secondNodeExprId;
	}

	/**
	 * @param secondNodeExprId secondNodeExpressionId
	 */
	public void setSecondNodeExpressionId(int secondNodeExprId)
	{
		this.secondNodeExprId = secondNodeExprId;
	}

	/**
	 * @return firstEntityName
	 */
	public String getFirstEntityName()
	{
		return firstEntityName;
	}

	/**
	 * @param firstEntityName firstEntityName
	 */
	public void setFirstEntityName(String firstEntityName)
	{
		this.firstEntityName = firstEntityName;
	}

	/**
	 * @return secondEntityName
	 */
	public String getSecondEntityName()
	{
		return secondEntityName;
	}

	/**
	 * @param secondEntityName secondEntityName
	 */
	public void setSecondEntityName(String secondEntityName)
	{
		this.secondEntityName = secondEntityName;
	}

	/**
	 * @return the joinFormulaNodeList
	 */
	public List<JoinFormulaNode> getJoinFormulaNodeList()
	{
		return jFormulaNodeList;
	}

	/**
	 * @param jFormulaNodeList the joinFormulaNodeList to set
	 */
	public void setJoinFormulaNodeList(
			List<JoinFormulaNode> jFormulaNodeList)
	{
		this.jFormulaNodeList = jFormulaNodeList;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @param input input.
	 * @throws IOException IOException
	 * @throws ClassNotFoundException ClassNotFoundException
	 */
	public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException
	{
		this.name = input.readUTF();
		this.firstNodeExprId = input.readInt();
		this.secondNodeExprId = input.readInt();
		this.firstEntityName = input.readUTF();
		this.secondEntityName = input.readUTF();
		this.operation = input.readUTF();
		this.jFormulaNodeList = (List<JoinFormulaNode>)input.readObject();
		this.nodeView = input.readUTF();
	}

	/**
	 * @param output output
	 * @throws IOException IOException
	 */
	public void writeExternal(ObjectOutput output) throws IOException
	{
		output.writeUTF(this.name);
		output.writeInt(this.firstNodeExprId);
		output.writeInt(this.secondNodeExprId);
		output.writeUTF(this.firstEntityName);
		output.writeUTF(this.secondEntityName);
		output.writeUTF(this.operation);
		output.writeObject(this.jFormulaNodeList);
		output.writeUTF(this.nodeView);
	}
}