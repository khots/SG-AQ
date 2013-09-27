package edu.wustl.query.bizlogic;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.wustl.common.query.queryobject.impl.OutputAssociationColumn;

/**
 * This class contains the details of the expressions and corresponding maps
 * required for storing information of denormalized data.
 * @author pooja_tavase
 *
 */
public class ExpressionRecords
{
	private Map<String, Map<OutputAssociationColumn, Object>> expRecs = new LinkedHashMap<String, Map<OutputAssociationColumn,Object>>();

	public void addRecords(String id,
			Map<OutputAssociationColumn, Object> map)
	{
		expRecs.put(id, map);
	}

	public ExpressionRecords()
	{
	}

	/**
	 * @return the expRecs
	 */
	public Map<String, Map<OutputAssociationColumn, Object>> getExpRecs()
	{
		return expRecs;
	}

	/**
	 * @param expRecs
	 *            the expRecs to set
	 */
	public void setExpRecs(
			Map<String, Map<OutputAssociationColumn, Object>> expRecs)
	{
		this.expRecs = expRecs;
	}

	/**
	 * @return the map
	 */
	public Map<OutputAssociationColumn, Object> getMap(String id)
	{
		Map<OutputAssociationColumn, Object> res = expRecs.get(id);
		if (res == null)
		{
			res = new HashMap<OutputAssociationColumn, Object>();
			expRecs.put(id, res);
		}
		return res;
	}
}
