package edu.wustl.query.generator;

import junit.framework.TestCase;

public class SqlKeyWordsTestCase extends TestCase
{
	public void test()
	{
		String from = SqlKeyWords.FROM;
		String innerJoin = SqlKeyWords.INNER_JOIN;
		String join = SqlKeyWords.JOIN_ON;
		String leftJoin = SqlKeyWords.LEFT_JOIN;
		String select = SqlKeyWords.SELECT;
		String selectDistinct = SqlKeyWords.SELECT_DISTINCT;
		String where = SqlKeyWords.WHERE;
	}
}
