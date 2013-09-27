
package edu.wustl.query.flex.dag;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import edu.wustl.cab2b.client.ui.util.ClientConstants;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.query.util.global.AQConstants;

/**
 *
 */
public class DAGNode implements Externalizable, Comparable<DAGNode>
{

	/**
	 * Node name.
	 */
	private String nodeName = "";

	/**
	 * Expression id.
	 */
	private int expressionId = 0;

	/**
	 * Tool tip to be displayed.
	 */
	private String toolTip = "";

	/**
	 * The Operator.
	 */
	private String oprBetnAttrAndAssoc = "";

	/**
	 * Node type.
	 */
	private String nodeType = DAGConstant.CONSTRAINT_VIEW_NODE;

	/**
	 * List of associations.
	 */
	public List<DAGNode> associationList = new ArrayList<DAGNode>();

	/**
	 * List of operators.
	 */
	public List<String> operatorList = new ArrayList<String>();

	/**
	 * List of DAG paths.
	 */
	public List<DAGPath> dagpathList = new ArrayList<DAGPath>();

	/**
	 * Error message.
	 */
	private String errorMsg = "";

	/**
	 * X Coordinate.
	 */
	private int xCoordinate;

	/**
	 * Y Coordinate.
	 */
	private int yCoordinate;

	/**
	 * @return xCoordinate xCoordinate
	 */
	public int getX()
	{
		return xCoordinate;
	}

	/**
	 * @param xCoordinate x-coordinate to set
	 */
	public void setX(int xCoordinate)
	{
		this.xCoordinate = xCoordinate;
	}

	/**
	 * @return yCoordinate y-coordinate
	 */
	public int getY()
	{
		return yCoordinate;
	}

	/**
	 * @param yCoordinate y-coordinate to set.
	 */
	public void setY(int yCoordinate)
	{
		this.yCoordinate = yCoordinate;
	}

	/**
	 * Default constructor.
	 */
	public DAGNode()
	{
		setOperatorBetweenAttrAndAssociation(ClientConstants.OPERATOR_AND);
	}

	/**
	 * @return nodeName node name
	 */
	public String getNodeName()
	{
		return nodeName;
	}

	/**
	 * @param nodeName Node name to set.
	 */
	public void setNodeName(String nodeName)
	{
		this.nodeName = nodeName;
	}

	/**
	 * @param expressionId Expression Id to set
	 */
	public void setExpressionId(int expressionId)
	{
		this.expressionId = expressionId;
	}

	/**
	 * @return expressionId expressionId
	 */
	public int getExpressionId()
	{
		return expressionId;
	}

	/**
	 * Sets the tool tip for the given expression.
	 * @param expression expression
	 */
	public void setToolTip(IExpression expression)
	{
		StringBuffer toolTip = new StringBuffer(AQConstants.ONE_TWENTY_EIGHT);
		IRule rule;
		if (expression.containsRule() && expression.getOperand(0) instanceof IRule)
		{
			rule = (IRule) expression.getOperand(0);
		}
		else
		{
			return;
		}
		int totalConditions = rule.size();

		toolTip.append("Condition(s) on  \n");
		generateFormattedString(toolTip, rule, totalConditions);
		this.toolTip = toolTip.toString();
	}

	/**
	 * Generate formatted string.
	 * @param toolTip StringBuffer object
	 * @param rule IRule object
	 * @param totalConditions total conditions.
	 */
	private void generateFormattedString(StringBuffer toolTip, IRule rule, int totalConditions)
	{
		for (int i = 0; i < totalConditions; i++)
		{
			ICondition condition = rule.getCondition(i);
			toolTip.append((i + 1) + ") ");
			String formattedAttrName =
			edu.wustl.cab2b.common.util.Utility.getFormattedString(condition.getAttribute()
					.getName());//common - Utility.
			toolTip.append(formattedAttrName).append(' ');
			List<String> values = condition.getValues();
			RelationalOperator operator = condition.getRelationalOperator();

			toolTip.append(operator.getStringRepresentation()).append(' ');
			checkEquality(toolTip, values);
			toolTip.append('\n');
		}
	}

	/**
	 * @param toolTip StringBuffer object
	 * @param values values
	 */
	private void checkEquality(StringBuffer toolTip, List<String> values)
	{
		int size = values.size();
		if (size > 0)// Special case for 'Not Equals and Equals
		{
			if (size == 1)
			{
				toolTip.append(values.get(0));
			}
			else
			{
				toolTip.append('(');
				generateFomulaString(toolTip, values, size);
				toolTip.append(')');
			}
		}
	}

	/**
	 * @param toolTip StringBuffer object
	 * @param values values
	 * @param size size
	 */
	private void generateFomulaString(StringBuffer toolTip, List<String> values, int size)
	{
		if(values.get(0) != null)
		{
			if (values.get(0).indexOf(",") == -1)
			{
				toolTip.append(values.get(0));
			}
			else
			{
				toolTip.append('\'');
				toolTip.append(values.get(0));
				toolTip.append('\'');
			}
			for (int j = 1; j < size; j++)
			{
				toolTip.append(", ");
				if (values.get(j).indexOf(",") == -1)
				{
					toolTip.append(values.get(j));
				}
				else
				{
					toolTip.append('\'');
					toolTip.append(values.get(j));
					toolTip.append('\'');
				}
			}
		}
	}

	/**
	 * @return toolTip generated tool tip.
	 */
	public String getToolTip()
	{
		return toolTip;
	}

	/**
	 * @return operatorBetweenAttrAndAssociation The operator.
	 */
	public String getOperatorBetweenAttrAndAssociation()
	{
		return oprBetnAttrAndAssoc;
	}

	/**
	 * @param oprBetnAttrAndAssoc Operator to set.
	 */
	public void setOperatorBetweenAttrAndAssociation(String oprBetnAttrAndAssoc)
	{
		this.oprBetnAttrAndAssoc = oprBetnAttrAndAssoc;
	}

	/**
	 * @param objectInput ObjectInput object.
	 * @throws IOException IOException
	 * @throws ClassNotFoundException ClassNotFoundException
	 */
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException
	{
		// TODO Auto-generated method stub
		nodeName = objectInput.readUTF();
		toolTip = objectInput.readUTF();
		expressionId = objectInput.readInt();
		oprBetnAttrAndAssoc = objectInput.readUTF();
		nodeType = objectInput.readUTF();
		associationList = (List<DAGNode>) objectInput.readObject();
		operatorList = (List<String>) objectInput.readObject();
		//	pathList = (List<String>) in.readObject();
		dagpathList = (List<DAGPath>) objectInput.readObject();
		errorMsg = objectInput.readUTF();
		xCoordinate = objectInput.readInt();
		yCoordinate = objectInput.readInt();
	}

	/**
	 * @param objectOutput ObjectOutput object.
	 * @throws IOException IOException
	 */
	public void writeExternal(ObjectOutput objectOutput) throws IOException
	{
		// TODO Auto-generated method stub
		objectOutput.writeUTF(nodeName);
		objectOutput.writeUTF(toolTip);
		objectOutput.writeInt(expressionId);
		objectOutput.writeUTF(oprBetnAttrAndAssoc);
		objectOutput.writeUTF(nodeType);
		objectOutput.writeObject(associationList);
		objectOutput.writeObject(operatorList);
		//out.writeObject(pathList);
		objectOutput.writeObject(dagpathList);
		objectOutput.writeUTF(errorMsg);
		objectOutput.writeInt(xCoordinate);
		objectOutput.writeInt(yCoordinate);
	}

	/**
	 * Returns the string representation.
	 * @return buff
	 */
	@Override
	public String toString()
	{
		StringBuffer buff = new StringBuffer(AQConstants.ONE_TWENTY_EIGHT);
		buff.append("\n nodeName: ").append(nodeName).append("\n toolTip: ").append(toolTip)
				.append("\n expressionId: ").append(expressionId).append(
						"\n operatorBetweenAttrAndAssociation:").append(
						oprBetnAttrAndAssoc);
		return buff.toString();
	}

	/**
	 * @param node DAGNode object.
	 * @return 0
	 */
	public int compareTo(DAGNode node)
	{
		// TODO Auto-generated method stub

		return 0;
	}

	/**
	 * @param obj DAGNode Object.
	 * @return <CODE>true</CODE> expression ids are equal,
	 * <CODE>false</CODE> otherwise
	 */
	@Override
	public boolean equals(Object obj)
	{
		DAGNode node = (DAGNode) obj;
		boolean equal = false;
		if (this.expressionId == node.expressionId)
		{
			equal = true;
		}
		return equal;
	}

	/**
	 * @return associationList List of associations.
	 */
	public List<DAGNode> getAssociationList()
	{
		return associationList;
	}

	/**
	 * @param node DAGNode object
	 */
	public void setAssociationList(DAGNode node)
	{
		this.associationList.add(node);
	}

	/**
	 * @return operatorList List of operators.
	 */
	public List<String> getOperatorList()
	{
		return operatorList;
	}

	/**
	 * @param operator Operator to add to the operatorList.
	 */
	public void setOperatorList(String operator)
	{
		this.operatorList.add(operator);
	}

	/*	public List<String> getPathList() {
			return pathList;
		}


		public void setPathList(String path) {
			this.pathList.add(path);
		}*/

	/**
	 * @return nodeType Node Type.
	 */
	public String getNodeType()
	{
		return nodeType;
	}

	/**
	 * @param nodeType Node Type.
	 */
	public void setNodeType(String nodeType)
	{
		this.nodeType = nodeType;
	}

	/**
	 * @return dagpathList List of DAG paths.
	 */
	public List<DAGPath> getDagpathList()
	{
		return dagpathList;
	}

	/**
	 * @param dagpath dagpath to add in the dagpathList.
	 */
	public void setDagpathList(DAGPath dagpath)
	{
		this.dagpathList.add(dagpath);
	}

	/**
	 * @return errorMsg Error message.
	 */
	public String getErrorMsg()
	{
		return errorMsg;
	}

	/**
	 * @param errorMsg Error message to set.
	 */
	public void setErrorMsg(String errorMsg)
	{
		this.errorMsg = errorMsg;
	}
}