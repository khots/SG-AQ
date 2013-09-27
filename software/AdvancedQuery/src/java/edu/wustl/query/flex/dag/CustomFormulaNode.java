
package edu.wustl.query.flex.dag;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 *
 */
public class CustomFormulaNode implements Externalizable
{
	/**
	 * Default Serial Version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name.
	 */
	private String name = "";

	/**
	 * Name of the first node.
	 */
	private String firstNodeName = "";

	/**
	 * Name of the second node.
	 */
	private String secondNodeName = "";

	/**
	 * First node Expression id.
	 */
	private int firstNodeExpId = 0;

	/**
	 * Second node expression id.
	 */
	private int secondNodeExpId = 0;

	/**
	 * First selected attribute name.
	 */
	private String firstSelectedAttrName = "";

	/**
	 * Second selected attribute name.
	 */
	private String secondSelectedAttrName = "";

	/**
	 * First selected attribute id.
	 */
	private String firstSelectedAttrId = "";

	/**
	 * Second selected attribute id.
	 */
	private String secondSelectedAttrId = "";

	/**
	 * First selected attribute type.
	 */
	private String firstSelectedAttrType = "";

	/**
	 * Second selected attribute type.
	 */
	private String secondSelectedAttrType = "";

	/**
	 * Selected arithmetic operator.
	 */
	private String selectedArithmeticOp = "";

	/**
	 * Selected logical operator.
	 */
	private String selectedLogicalOp = "";

	/**
	 * Time value.
	 */
	private String timeValue = "";

	/**
	 * Time interval.
	 */
	private String timeInterval = "";

	/**
	 * Operation.
	 */
	private String operation = "";

	/**
	 * Custom column name.
	 */
	private String customColumnName = "";

	/**
	 * X Coordinate.
	 */
	private int xCoordinate;

	/**
	 * Y Coordinate.
	 */
	private int yCoordinate;

	/**
	 * Node view.
	 */
	private String nodeView = "";

	/**
	 * qAttrInterval1.
	 */
	private String qAttrInterval1 = "";

	/**
	 * qAttrInterval2.
	 */
	private String qAttrInterval2 = "";

	/**
	 * cc Interval.
	 */
	private String ccInterval = "";

	/**
	 * @return Returns the ccInterval.
	 */
	public String getCcInterval()
	{
		return ccInterval;
	}

	/**
	 * @param ccInterval The ccInterval to set.
	 */
	public void setCcInterval(String ccInterval)
	{
		this.ccInterval = ccInterval;
	}

	/**
	 * @return Returns the nodeView.
	 */
	public String getNodeView()
	{
		return nodeView;
	}

	/**
	 * @return qAttrInterval1 qAttrInterval1
	 */
	public String getQAttrInterval1()
	{
		return qAttrInterval1;
	}

	/**
	 * @param attrInterval Interval to set.
	 */
	public void setQAttrInterval1(String attrInterval)
	{
		qAttrInterval1 = attrInterval;
	}

	/**
	 * @return qAttrInterval2 qAttrInterval2
	 */
	public String getQAttrInterval2()
	{
		return qAttrInterval2;
	}

	/**
	 * @param attrInterval2 Interval to set.
	 */
	public void setQAttrInterval2(String attrInterval2)
	{
		qAttrInterval2 = attrInterval2;
	}

	/**
	 * @param nodeView The nodeView to set.
	 */
	public void setNodeView(String nodeView)
	{
		this.nodeView = nodeView;
	}

	/**
	 * @return Returns the customColumnName.
	 */
	public String getCustomColumnName()
	{
		return customColumnName;
	}

	/**
	 * @param customColumnName The customColumnName to set.
	 */
	public void setCustomColumnName(String customColumnName)
	{
		this.customColumnName = customColumnName;
	}

	/**
	 * @return Returns the xCoordinate.
	 */
	public int getX()
	{
		return xCoordinate;
	}

	/**
	 * @param xCoordinate The x to set.
	 */
	public void setX(int xCoordinate)
	{
		this.xCoordinate = xCoordinate;
	}

	/**
	 * @return Returns the yCoordinate.
	 */
	public int getY()
	{
		return yCoordinate;
	}

	/**
	 * @param yCoordinate The y to set.
	 */
	public void setY(int yCoordinate)
	{
		this.yCoordinate = yCoordinate;
	}

	/**
	 * @return Returns the firstNodeExpId.
	 */
	public int getFirstNodeExpId()
	{
		return firstNodeExpId;
	}

	/**
	 * @param firstNodeExpId The firstNodeExpId to set.
	 */
	public void setFirstNodeExpId(int firstNodeExpId)
	{
		this.firstNodeExpId = firstNodeExpId;
	}

	/**
	 * @return Returns the firstNodeName.
	 */
	public String getFirstNodeName()
	{
		return firstNodeName;
	}

	/**
	 * @param firstNodeName The firstNodeName to set.
	 */
	public void setFirstNodeName(String firstNodeName)
	{
		this.firstNodeName = firstNodeName;
	}

	/**
	 * @return Returns the firstSelectedAttrId.
	 */
	public String getFirstSelectedAttrId()
	{
		return firstSelectedAttrId;
	}

	/**
	 * @param firstSelectedAttrId The firstSelectedAttrId to set.
	 */
	public void setFirstSelectedAttrId(String firstSelectedAttrId)
	{
		this.firstSelectedAttrId = firstSelectedAttrId;
	}

	/**
	 * @return Returns the firstSelectedAttrName.
	 */
	public String getFirstSelectedAttrName()
	{
		return firstSelectedAttrName;
	}

	/**
	 * @param firstSelectedAttrName The firstSelectedAttrName to set.
	 */
	public void setFirstSelectedAttrName(String firstSelectedAttrName)
	{
		this.firstSelectedAttrName = firstSelectedAttrName;
	}

	/**
	 * @return Returns the firstSelectedAttrType.
	 */
	public String getFirstSelectedAttrType()
	{
		return firstSelectedAttrType;
	}

	/**
	 * @param firstSelectedAttrType The firstSelectedAttrType to set.
	 */
	public void setFirstSelectedAttrType(String firstSelectedAttrType)
	{
		this.firstSelectedAttrType = firstSelectedAttrType;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return Returns the secondNodeExpId.
	 */
	public int getSecondNodeExpId()
	{
		return secondNodeExpId;
	}

	/**
	 * @param secondNodeExpId The secondNodeExpId to set.
	 */
	public void setSecondNodeExpId(int secondNodeExpId)
	{
		this.secondNodeExpId = secondNodeExpId;
	}

	/**
	 * @return Returns the secondNodeName.
	 */
	public String getSecondNodeName()
	{
		return secondNodeName;
	}

	/**
	 * @param secondNodeName The secondNodeName to set.
	 */
	public void setSecondNodeName(String secondNodeName)
	{
		this.secondNodeName = secondNodeName;
	}

	/**
	 * @return Returns the secondSelectedAttrId.
	 */
	public String getSecondSelectedAttrId()
	{
		return secondSelectedAttrId;
	}

	/**
	 * @param secondSelectedAttrId The secondSelectedAttrId to set.
	 */
	public void setSecondSelectedAttrId(String secondSelectedAttrId)
	{
		this.secondSelectedAttrId = secondSelectedAttrId;
	}

	/**
	 * @return Returns the secondSelectedAttrName.
	 */
	public String getSecondSelectedAttrName()
	{
		return secondSelectedAttrName;
	}

	/**
	 * @param secondSelectedAttrName The secondSelectedAttrName to set.
	 */
	public void setSecondSelectedAttrName(String secondSelectedAttrName)
	{
		this.secondSelectedAttrName = secondSelectedAttrName;
	}

	/**
	 * @return Returns the secondSelectedAttrType.
	 */
	public String getSecondSelectedAttrType()
	{
		return secondSelectedAttrType;
	}

	/**
	 * @param secondSelectedAttrType The secondSelectedAttrType to set.
	 */
	public void setSecondSelectedAttrType(String secondSelectedAttrType)
	{
		this.secondSelectedAttrType = secondSelectedAttrType;
	}

	/**
	 * @return Returns the selectedArithmeticOp.
	 */
	public String getSelectedArithmeticOp()
	{
		return selectedArithmeticOp;
	}

	/**
	 * @param selectedArithmeticOp The selectedArithmeticOp to set.
	 */
	public void setSelectedArithmeticOp(String selectedArithmeticOp)
	{
		this.selectedArithmeticOp = selectedArithmeticOp;
	}

	/**
	 * @return Returns the selectedLogicalOp.
	 */
	public String getSelectedLogicalOp()
	{
		return selectedLogicalOp;
	}

	/**
	 * @param selectedLogicalOp The selectedLogicalOp to set.
	 */
	public void setSelectedLogicalOp(String selectedLogicalOp)
	{
		this.selectedLogicalOp = selectedLogicalOp;
	}

	/**
	 * @return Returns the timeInterval.
	 */
	public String getTimeInterval()
	{
		return timeInterval;
	}

	/**
	 * @param timeInterval The timeInterval to set.
	 */
	public void setTimeInterval(String timeInterval)
	{
		this.timeInterval = timeInterval;
	}

	/**
	 * @return Returns the timeValue.
	 */
	public String getTimeValue()
	{
		return timeValue;
	}

	/**
	 * @param timeValue The timeValue to set.
	 */
	public void setTimeValue(String timeValue)
	{
		this.timeValue = timeValue;
	}

	/**
	 * Reading serialized data.
	 * @param objectInput Object of ObjectInput
	 * @throws IOException IOException
	 * @throws ClassNotFoundException ClassNotFoundException
	 */
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException
	{
		name = objectInput.readUTF();
		firstNodeName = objectInput.readUTF();
		secondNodeName = objectInput.readUTF();
		firstNodeExpId = objectInput.readInt();
		secondNodeExpId = objectInput.readInt();
		firstSelectedAttrName = objectInput.readUTF();
		secondSelectedAttrName = objectInput.readUTF();
		firstSelectedAttrId = objectInput.readUTF();
		secondSelectedAttrId = objectInput.readUTF();
		firstSelectedAttrType = objectInput.readUTF();
		secondSelectedAttrType = objectInput.readUTF();
		selectedArithmeticOp = objectInput.readUTF();
		selectedLogicalOp = objectInput.readUTF();
		timeValue = objectInput.readUTF();
		timeInterval = objectInput.readUTF();
		operation = objectInput.readUTF();
		customColumnName = objectInput.readUTF();
		xCoordinate = objectInput.readInt();
		yCoordinate = objectInput.readInt();
		nodeView = objectInput.readUTF();
		qAttrInterval1 = objectInput.readUTF();
		qAttrInterval2 = objectInput.readUTF();
		ccInterval = objectInput.readUTF();
	}

	/**
	 * Writing serialized Id.
	 * @param objectOutput Object of ObjectOutput
	 * @throws IOException IOException
	 */
	public void writeExternal(ObjectOutput objectOutput) throws IOException
	{
		objectOutput.writeUTF(name);
		objectOutput.writeUTF(firstNodeName);
		objectOutput.writeUTF(secondNodeName);
		objectOutput.writeInt(firstNodeExpId);
		objectOutput.writeInt(secondNodeExpId);
		objectOutput.writeUTF(firstSelectedAttrName);
		objectOutput.writeUTF(secondSelectedAttrName);
		objectOutput.writeUTF(firstSelectedAttrId);
		objectOutput.writeUTF(secondSelectedAttrId);
		objectOutput.writeUTF(firstSelectedAttrType);
		objectOutput.writeUTF(secondSelectedAttrType);
		objectOutput.writeUTF(selectedArithmeticOp);
		objectOutput.writeUTF(selectedLogicalOp);
		objectOutput.writeUTF(timeValue);
		objectOutput.writeUTF(timeInterval);
		objectOutput.writeUTF(operation);
		objectOutput.writeUTF(customColumnName);
		objectOutput.writeInt(xCoordinate);
		objectOutput.writeInt(yCoordinate);
		objectOutput.writeUTF(nodeView);
		objectOutput.writeUTF(qAttrInterval1);
		objectOutput.writeUTF(qAttrInterval2);
		objectOutput.writeUTF(ccInterval);
	}

	/**
	 * @return Returns the operation.
	 */
	public String getOperation()
	{
		return operation;
	}

	/**
	 * @param operation The operation to set.
	 */
	public void setOperation(String operation)
	{
		this.operation = operation;
	}
}
