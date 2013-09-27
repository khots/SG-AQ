
package edu.wustl.query.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.cab2b.common.cache.AbstractEntityCache;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.query.actionForm.CategorySearchForm;
import edu.wustl.query.flex.dag.DAGConstant;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.QueryModuleUtil;

/**
 * Action is called when user clicks on Advance Search link on search menu.
 * @author deepti_shelar
 */
public class QueryWizardAction extends SecureAction
{

	/**
	 * This method loads the data required for categorySearch.jsp.
	 * @param mapping mapping
	 * @param form form
	 * @param request request
	 * @param response response
	 * @throws Exception Exception
	 * @return ActionForward actionForward
	 */
	@Override
	public ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ActionForward actionForward;
		if(AbstractEntityCache.isCacheReady)
		{
			HttpSession session = request.getSession();
			CategorySearchForm searchForm = (CategorySearchForm) form;
			session.removeAttribute(AQConstants.QUERY_OBJECT);
			session.removeAttribute(AQConstants.SELECTED_COLUMN_META_DATA);
			session.removeAttribute(AQConstants.IS_SAVED_QUERY);
			session.removeAttribute(AQConstants.IS_SIMPLE_SEARCH);
			session.removeAttribute(DAGConstant.ISREPAINT);
			session.removeAttribute(DAGConstant.TQUIMAP);
			session.removeAttribute(DAGConstant.JQUIMAP);
			session.removeAttribute(AQConstants.EXPORT_DATA_LIST);
			session.removeAttribute(AQConstants.ENTITY_IDS_MAP);
			session.removeAttribute(AQConstants.AUDIT_EVENT_ID);
			session.removeAttribute("savedQuery");
			session.removeAttribute("savedQueryProcessed");
			session.removeAttribute(AQConstants.TREE_CHECK_VALUE);
		  //session.removeAttribute(AQConstants.DENORMALIZED_LIST);
			searchForm = QueryModuleUtil.setDefaultSelections(searchForm);
			actionForward = mapping.findForward(edu.wustl.query.util.global.AQConstants.SUCCESS);
		}
		else
		{
			ActionErrors errors = new ActionErrors();
			String errorMessage = ApplicationProperties.getValue("entityCache.error");
			ActionError error = new ActionError("query.errors.item",errorMessage);
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);
			actionForward = mapping.findForward(edu.wustl.query.util.global.AQConstants.FAILURE);
		}
		return actionForward;
	}
}
