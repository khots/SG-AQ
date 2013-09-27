package edu.wustl.query.bizlogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.global.AQConstants;
import junit.framework.TestCase;

public class ValidateQueryBizLogicTestCase extends TestCase
{
	public void testGetValidateMessage()
	{
		IQuery query = GenericQueryGeneratorMock.creatParticipantQuery();
		HttpSession session = new HttpSession() {

			public void setMaxInactiveInterval(int arg0) {
				// TODO Auto-generated method stub

			}

			public void setAttribute(String arg0, Object arg1) {
				// TODO Auto-generated method stub

			}

			public void removeValue(String arg0) {
				// TODO Auto-generated method stub

			}

			public void removeAttribute(String arg0) {
				// TODO Auto-generated method stub

			}

			public void putValue(String arg0, Object arg1) {
				// TODO Auto-generated method stub

			}

			public boolean isNew() {
				// TODO Auto-generated method stub
				return false;
			}

			public void invalidate() {
				// TODO Auto-generated method stub

			}

			public String[] getValueNames() {
				// TODO Auto-generated method stub
				return null;
			}

			public Object getValue(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			public HttpSessionContext getSessionContext() {
				// TODO Auto-generated method stub
				return null;
			}

			public ServletContext getServletContext() {
				// TODO Auto-generated method stub
				return null;
			}

			public int getMaxInactiveInterval() {
				// TODO Auto-generated method stub
				return 0;
			}

			public long getLastAccessedTime() {
				// TODO Auto-generated method stub
				return 0;
			}

			public String getId() {
				// TODO Auto-generated method stub
				return null;
			}

			public long getCreationTime() {
				// TODO Auto-generated method stub
				return 0;
			}

			public Enumeration getAttributeNames() {
				// TODO Auto-generated method stub
				return null;
			}

			public Object getAttribute(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		SessionDataBean sessionData = new SessionDataBean();
		sessionData.setCsmUserId("1");
		sessionData.setIpAddress("10.88.199.11");
		sessionData.setUserId(1l);
		sessionData.setUserName("admin@admin.com");
		session.setAttribute(AQConstants.SESSION_DATA, sessionData);
		session.setAttribute(AQConstants.IS_SAVED_QUERY, AQConstants.FALSE);
		for(IExpression expression : query.getConstraints())
		{
			expression.setInView(false);
		}
		new ValidateQueryBizLogic().getValidationMessage(null, query);
		query = QueryObjectFactory.createQuery();
		new ValidateQueryBizLogic().getValidationMessage(null, query);
	}
}
