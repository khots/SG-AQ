/**
 *
 */
package edu.wustl.query.flex.dag;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Externalizable;

/**
 * @author sagar_baldwa
 *
 */
public class JoinFormulaNode implements Externalizable
{
	/**
	 * Name of the join formula.
	 */
	private String name;

	/**
	 * First Attribute Identifier.
	 */
	private String firstAttributeId;

	/**
	 * Second attribute identifier.
	 */
	private String secondAttributeId;

	/**
	 * First attribute name.
	 */
	private String firstAttributeName;

	/**
	 * Second attribute name.
	 */
	private String secondAttributeName;

	/**
	 * First attribute data type.
	 */
	private String firstAttributeDataType;

	/**
	 * Second attribute data type.
	 */
	private String secondAttributeDataType;

	/**
	 * @return the firstAttributeId
	 */
	public String getFirstAttributeId()
	{
		return firstAttributeId;
	}

	/**
	 * @param firstAttributeId the firstAttributeId to set
	 */
	public void setFirstAttributeId(String firstAttributeId)
	{
		this.firstAttributeId = firstAttributeId;
	}

	/**
	 * @return the secondAttributeId
	 */
	public String getSecondAttributeId()
	{
		return secondAttributeId;
	}

	/**
	 * @param secondAttributeId the secondAttributeId to set
	 */
	public void setSecondAttributeId(String secondAttributeId)
	{
		this.secondAttributeId = secondAttributeId;
	}

	/**
	 * @return the firstAttributeName
	 */
	public String getFirstAttributeName()
	{
		return firstAttributeName;
	}

	/**
	 * @param firstAttributeName the firstAttributeName to set
	 */
	public void setFirstAttributeName(String firstAttributeName)
	{
		this.firstAttributeName = firstAttributeName;
	}

	/**
	 * @return the secondAttributeName
	 */
	public String getSecondAttributeName()
	{
		return secondAttributeName;
	}

	/**
	 * @param secondAttributeName the secondAttributeName to set
	 */
	public void setSecondAttributeName(String secondAttributeName)
	{
		this.secondAttributeName = secondAttributeName;
	}

	/**
	 * @return the firstAttributeDataType
	 */
	public String getFirstAttributeDataType()
	{
		return firstAttributeDataType;
	}

	/**
	 * @param firstAttributeDataType the firstAttributeDataType to set
	 */
	public void setFirstAttributeDataType(String firstAttributeDataType)
	{
		this.firstAttributeDataType = firstAttributeDataType;
	}

	/**
	 * @return the secondAttributeDataType
	 */
	public String getSecondAttributeDataType()
	{
		return secondAttributeDataType;
	}

	/**
	 * @param secondAttributeDataType the secondAttributeDataType to set
	 */
	public void setSecondAttributeDataType(String secondAttributeDataType)
	{
		this.secondAttributeDataType = secondAttributeDataType;
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
	 * @param objectInput objectInput
	 * @throws IOException IOException
	 * @throws ClassNotFoundException ClassNotFoundException
	 */
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException
	{
		this.name = objectInput.readUTF();
		this.firstAttributeId = objectInput.readUTF();
		this.firstAttributeName = objectInput.readUTF();
		this.firstAttributeDataType = objectInput.readUTF();
		this.secondAttributeId = objectInput.readUTF();
		this.secondAttributeName = objectInput.readUTF();
		this.secondAttributeDataType = objectInput.readUTF();
	}

	/**
	 * @param out out
	 * @throws IOException IOException
	 */
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeUTF(this.name);
		out.writeUTF(this.firstAttributeId);
		out.writeUTF(this.firstAttributeName);
		out.writeUTF(this.firstAttributeDataType);
		out.writeUTF(this.secondAttributeId);
		out.writeUTF(this.secondAttributeName);
		out.writeUTF(this.secondAttributeDataType);
	}
}