
package edu.wustl.query.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.ui.util.Constants;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.query.actionForm.SaveQueryForm;
import edu.wustl.query.beans.SharedQueryBean;
import edu.wustl.query.bizlogic.SaveQueryBizLogic;
import edu.wustl.query.bizlogic.ShareQueryBizLogic;
import edu.wustl.query.htmlprovider.SavedQueryHtmlProvider;
import edu.wustl.query.util.global.AQConstants;
/**
 * Loads data for save query page.
 * @author deepti_shelar
 *
 */
public class LoadSaveQueryPageAction extends SecureAction
{
	/**
	 * This action loads all the conditions from the query.
	 * @param mapping mapping
	 * @param form form
	 * @param request request
	 * @param response response
	 * @throws Exception Exception
	 * @return ActionForward actionForward
	 */
	public ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		IQuery queryObject = (IQuery) request.getSession().getAttribute(AQConstants.QUERY_OBJECT);
		String target = AQConstants.FAILURE;
		if (queryObject != null)
		{
//			request.getParameter("showTree");
			target = setAppropriateTarget(form, request, queryObject);
			SaveQueryForm savedQueryForm = (SaveQueryForm) form;
			if(request.getParameter("showTree") != null)
			{
				savedQueryForm.setShowTree(Boolean.valueOf(request.getParameter("showTree")));
			}
			else if(request.getSession().getAttribute("treeChkVal") != null)
			{
				savedQueryForm.setShowTree(Boolean.valueOf(request.getSession().getAttribute("treeChkVal").toString()));
			}
			ShareQueryBizLogic bizLogic = new ShareQueryBizLogic();

			saveCoordinatorList(request, savedQueryForm, bizLogic);
			String errorMessage = (String) request.getSession().getAttribute("errorMessageForEditQuery");
			if (errorMessage != null)
			{
				setActionError(request, errorMessage);
				request.getSession().removeAttribute("errorMessageForEditQuery");
			}
		}
		return mapping.findForward(target);
	}

	/**
	 * @param request request
	 * @param savedQueryForm form
	 * @param bizLogic bizLogic
	 * @throws BizLogicException BizLogicException
	 */
	private void saveCoordinatorList(HttpServletRequest request,
			SaveQueryForm savedQueryForm, ShareQueryBizLogic bizLogic)
			throws BizLogicException
	{
		List<NameValueBean> coordinators = new ArrayList<NameValueBean>();
		long[] protocolCoordIds = savedQueryForm.getProtocolCoordinatorIds();
		List users;
		users = bizLogic.getCSMUsers();
		if (protocolCoordIds != null && protocolCoordIds.length > 0)
		{
			List<Long> prtCordIds = new ArrayList<Long>();

			for (int i = 0; i < protocolCoordIds.length; i++)
			{
				prtCordIds.add(protocolCoordIds[i]);
			}
			populateCoordinatorList(coordinators, users, prtCordIds);
		}
		request.setAttribute(Constants.SELECTED_VALUES, coordinators);
	}

	/**
	 * @param coordinators coordinators
	 * @param users users
	 * @param prtCordIds protocol Coordinator identifiers
	 */
	private void populateCoordinatorList(List<NameValueBean> coordinators,
			List users, List<Long> prtCordIds)
	{
		for (Object object : users)
		{
			NameValueBean nameValueBean = (NameValueBean) object;
			if (prtCordIds.contains(Long.parseLong(nameValueBean.getValue())))
			{
				coordinators.add(nameValueBean);
			}
		}
	}

	/**
	 * @param form form
	 * @param request request
	 * @param queryObject queryObject
	 * @return target
	 * @throws BizLogicException Exception
	 */
	private String setAppropriateTarget(ActionForm form,
			HttpServletRequest request, IQuery queryObject)
			throws BizLogicException
	{
		String target;
		boolean isDagEmpty;
		isDagEmpty = isDagEmpty(queryObject);
		if (isDagEmpty)
		{
			target = AQConstants.SUCCESS;
			String errorMsg = ApplicationProperties.getValue("query.noLimit.error");
			setActionError(request, errorMsg);
			request.setAttribute(AQConstants.IS_QUERY_SAVED, AQConstants.IS_QUERY_SAVED);
		}
		else
		{
			target = getSavedQueryDetails(form, request, queryObject);
		}
		return target;
	}

	/**
	 * @param form form
	 * @param request request
	 * @param queryObject queryObject
	 * @return target
	 * @throws BizLogicException Exception
	 */
	private String getSavedQueryDetails(ActionForm form,
			HttpServletRequest request, IQuery queryObject)
			throws BizLogicException
	{
		String target;
		boolean isShowAll = request.getParameter(AQConstants.SHOW_ALL) == null ? false : true;
		Map<Integer, ICustomFormula> cFIndexMap = new HashMap<Integer, ICustomFormula>();
		String htmlContents = new SavedQueryHtmlProvider().getHTMLForSavedQuery(queryObject,
				isShowAll, AQConstants.SAVE_QUERY_PAGE, cFIndexMap);
		request.getSession().setAttribute(AQConstants.CUSTOM_FORMULA_INDEX_MAP,
				cFIndexMap);
		request.setAttribute(AQConstants.HTML_CONTENTS, htmlContents);
		String showAllLink = isShowAll ? AQConstants.SHOW_SELECTED_ATTRIBUTE
				: AQConstants.SHOW_ALL_ATTRIBUTE;
		request.setAttribute(AQConstants.SHOW_ALL_LINK, showAllLink);
		if (!isShowAll)
		{
			request.setAttribute(AQConstants.SHOW_ALL, AQConstants.TRUE);
		}
		target = AQConstants.SUCCESS;
		populateForm(form, request, queryObject);
		return target;
	}

	/**
	 * @param form form
	 * @param request request
	 * @param queryObject request
	 * @throws BizLogicException Exception
	 */
	private void populateForm(ActionForm form, HttpServletRequest request,
			IQuery queryObject) throws BizLogicException
	{
		if (queryObject.getId() != null && queryObject instanceof ParameterizedQuery)
		{
			SaveQueryForm savedQueryForm = (SaveQueryForm) form;
			savedQueryForm.setDescription(((ParameterizedQuery) queryObject).getDescription());
			savedQueryForm.setTitle(((ParameterizedQuery) queryObject).getName());
			SessionDataBean sessionDataBean = (SessionDataBean) request.getSession()
			.getAttribute(edu.wustl.common.util.global.Constants.SESSION_DATA);
			SaveQueryBizLogic bizLogic = new SaveQueryBizLogic();
			String csmUserId = sessionDataBean.getCsmUserId();
			SharedQueryBean bean = bizLogic.getSharingDetailsBean(queryObject);
			if(csmUserId.equalsIgnoreCase(bean.getCsmUserId()))
			{
				request.setAttribute(AQConstants.IS_MY_QUERY, "true");
			}
			populateSharingDetails(bean,savedQueryForm);
		}
	}
	/**
	 * checks if the DAG contains any expression
	 * @param queryObject query
	 * @return true / false
	 */
	private boolean isDagEmpty(IQuery queryObject)
	{
		boolean isDagEmpty = true;
		IConstraints constraints = queryObject.getConstraints();
		if(constraints.size() != 0)
		{
			isDagEmpty = false;
		}
		return isDagEmpty;
	}
	/**
	 * populates the form from the data of the bean.
	 * @param bean share query bean
	 * @param savedQueryForm form
	 */
	private void populateSharingDetails(SharedQueryBean bean, SaveQueryForm savedQueryForm)
	{
		savedQueryForm.setShareTo(bean.getShareTo());
		savedQueryForm.setProtocolCoordinatorIds(bean.getProtocolCoordinatorIds());
	}

	/**
	 * This method sets the error action
	 * @param request request
	 * @param errorMessage message
	 */
	private void setActionError(HttpServletRequest request, String errorMessage)
	{
		ActionErrors errors = new ActionErrors();
		ActionError error = new ActionError("errors.item", errorMessage);
		errors.add(ActionErrors.GLOBAL_ERROR, error);
		saveErrors(request, errors);
	}
}