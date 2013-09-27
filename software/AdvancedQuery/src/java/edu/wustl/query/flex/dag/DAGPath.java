
package edu.wustl.query.flex.dag;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 *
 */
public class DAGPath implements Externalizable
{
	/**
	 * Tool tip.
	 */
	private String toolTip = null;

	/**
	 * identifier.
	 */
	private String identifier = null;

	/**
	 * isSelected.
	 */
	private boolean isSelected = false;

	/**
	 * Source expression id.
	 */
	private int sourceExpId = 0;

	/**
	 * Destination expression id.
	 */
	private int destinationExpId = 0;

	/**
	 * Returns the tool tip.
	 * @return toolTip
	 */
	public String getToolTip()
	{
		return toolTip;
	}

	/**
	 * Sets the tool tip.
	 * @param toolTip toolTip
	 */
	public void setToolTip(String toolTip)
	{
		this.toolTip = toolTip;
	}

	/**
	 * @return isSelected.
	 */
	public boolean isSelected()
	{
		return isSelected;
	}

	/**
	 * Set isSelected value.
	 * @param isSelected isSelected
	 */
	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}

	/**
	 * Reads the values from input stream.
	 * @param objectInput ObjectInput object
	 * @throws IOException IOException
	 * @throws ClassNotFoundException ClassNotFoundException
	 */
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException
	{
		// TODO Auto-generated method stub
		toolTip = objectInput.readUTF();
		identifier = objectInput.readUTF();
		isSelected = objectInput.readBoolean();
		sourceExpId = objectInput.readInt();
		destinationExpId = objectInput.readInt();

	}

	/**
	 * Writes the values to the output stream.
	 * @param out out
	 * @throws IOException IOException
	 */
	public void writeExternal(ObjectOutput out) throws IOException
	{
		// TODO Auto-generated method stub
		out.writeUTF(toolTip);
		out.writeUTF(identifier);
		out.writeBoolean(isSelected);
		out.writeInt(sourceExpId);
		out.writeInt(destinationExpId);
	}

	/**
	 * Returns the identifier.
	 * @return identifier
	 */
	public String getId()
	{
		return identifier;
	}

	/**
	 * @param identifier identifier
	 */
	public void setId(String identifier)
	{
		this.identifier = identifier;
	}

	/**
	 * @return sourceExpId
	 */
	public int getSourceExpId()
	{
		return sourceExpId;
	}

	/**
	 * @param sourceExpId Source expression id
	 */
	public void setSourceExpId(int sourceExpId)
	{
		this.sourceExpId = sourceExpId;
	}

	/**
	 * @return destinationExpId
	 */
	public int getDestinationExpId()
	{
		return destinationExpId;
	}

	/**
	 * @param destinationExpId destinationExpId
	 */
	public void setDestinationExpId(int destinationExpId)
	{
		this.destinationExpId = destinationExpId;
	}
}
