package edu.wustl.query.util.global;

import java.util.List;

import edu.wustl.common.querysuite.queryobject.TermType;
import edu.wustl.common.querysuite.queryobject.YMInterval;
import edu.wustl.query.util.querysuite.TemporalColumnMetadata;

public class QueryCSMValidator
{
	/**
	 * Checks if the user has privilege to view the temporal column.
	 * @param tqColMetadata List of Temporal Column Metadata
	 * @param row The current row of the result
	 * @param isAuthorizedUser To determine if the user is authorized
	 */
	public void hasPrivilegeToViewTemporalColumn(List<Object> tqColMetadata,
			List<String> row,boolean isAuthorizedUser)
	{
		for (Object object : tqColMetadata)
		{
			processTemporalQueryData(row, isAuthorizedUser, object);
		}
	}

	private void processTemporalQueryData(List<String> row,
			boolean isAuthorizedUser, Object object)
	{
		TemporalColumnMetadata tqMetadata = (TemporalColumnMetadata) object;
		String ageString = row.get(tqMetadata.getColumnIndex() - 1);

		if (tqMetadata.getTermType().equals(TermType.Timestamp) || !isAuthorizedUser)
		{
			row.set(tqMetadata.getColumnIndex() - 1, AQConstants.HASH_OUT);
		}
		else if (tqMetadata.getTermType().equals(TermType.DSInterval)
				&& !ageString.equals(AQConstants.PHI_AGE))
		{
			processForDsInterval(row, tqMetadata, ageString);
		}
	}

	/**
	 * @param row row
	 * @param tqMetadata temporal column meta data
	 * @param ageString age
	 */
	private void processForDsInterval(List<String> row,
			TemporalColumnMetadata tqMetadata, String ageString)
	{
		long age = Long.parseLong(ageString);
		age = getAgeAsPerBirthDate(tqMetadata, age);
		if (!tqMetadata.getTimeInterval().name().equals(
					YMInterval.Year.name()))
		{
			age = Math.round((age * tqMetadata.getTimeInterval()
				.numSeconds())/ YMInterval.Year.numSeconds());
		}
		if (Math.abs(age) > 89)
		{
			row.set(tqMetadata.getColumnIndex() - 1, AQConstants.PHI_AGE);
		}
	}

	/**
	 * @param tqMetadata meta data
	 * @param tempAge age
	 * @return age
	 */
	private long getAgeAsPerBirthDate(TemporalColumnMetadata tqMetadata,
			long tempAge)
	{
		long age = tempAge;
		if (tqMetadata.getPHIDate() != null && tqMetadata.isBirthDate())
		{
			java.util.Date todaysDate = new java.util.Date();
			int year = tqMetadata.getPHIDate().getDate().getYear();
			age = todaysDate.getYear()
					- year
					+ age;
		}
		return age;
	}
}
