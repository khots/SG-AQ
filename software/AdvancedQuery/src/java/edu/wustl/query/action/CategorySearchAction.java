
package edu.wustl.query.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.client.metadatasearch.MetadataSearch;
import edu.wustl.cab2b.common.beans.MatchedClass;
import edu.wustl.cab2b.common.exception.CheckedException;
import edu.wustl.cab2b.common.util.Constants;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Validator;
import edu.wustl.query.actionForm.CategorySearchForm;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import edu.wustl.query.util.querysuite.QueryModuleUtil;

/**
 * This class loads screen for categorySearch.
 * When search button is clicked it checks for the input :
 * Text, check box, radio button, etc. And depending upon the selections made by user,
 * the list of entities is populated. This list is kept in session.
 * @author deepti_shelar
 */

public class CategorySearchAction extends SecureAction
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
	public ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ActionForward actionForward;
		boolean flag = true;
		String target = AQConstants.SUCCESS;
		setQueryStatus(request);
		CategorySearchForm searchForm = (CategorySearchForm) form;
		String currentPage = searchForm.getCurrentPage();
		boolean hideTreeChkVal;

		if(request.getSession().getAttribute(AQConstants.TREE_CHECK_VALUE) != null && !"".equals(request.getSession().getAttribute(AQConstants.TREE_CHECK_VALUE)))
		{
			hideTreeChkVal=Boolean.valueOf(request.getSession().getAttribute(AQConstants.TREE_CHECK_VALUE).toString());
		}
		else
		{
			hideTreeChkVal = searchForm.isShowTree();
		}
		request.setAttribute(AQConstants.HIDE_TREE_CHECK_VALUE, request.getSession().getAttribute(AQConstants.TREE_CHECK_VALUE));
		if (isResultsViewPage(currentPage))
		{
			searchForm = QueryModuleUtil.setDefaultSelections(searchForm);
		}
		else
		{
			String textfieldValue = setTextFieldValue(searchForm, currentPage);
			if (!Validator.isEmpty(textfieldValue))
			{
				setEntities(request, response, searchForm, textfieldValue);
				flag = false;
			}
		}
		searchForm.setShowTree(hideTreeChkVal);
		actionForward = setActionForward(mapping, flag, target);
		return actionForward;
	}

	/**
	 * @param currentPage
	 * @return
	 */
	private boolean isResultsViewPage(String currentPage)
	{
		return currentPage != null && currentPage.equalsIgnoreCase("resultsView");
	}

	/**
	 * @param mapping mapping
	 * @param flag flag
	 * @param target target
	 * @return actionForward
	 */
	private ActionForward setActionForward(ActionMapping mapping,
			 boolean flag, String target)
	{
		ActionForward actionForward = null;
		if(flag)
		{
			actionForward = mapping.findForward(target);
		}
		return actionForward;
	}

	/**
	 * @param searchForm form
	 * @param currentPage current page
	 * @return textfieldValue
	 */
	private String setTextFieldValue(CategorySearchForm searchForm,
			String currentPage)
	{
		String textfieldValue = searchForm.getTextField();
		if (currentPage != null && currentPage.equalsIgnoreCase("prevToAddLimits"))
		{
			textfieldValue = "";
		}
		return textfieldValue;
	}
	/**
	 *
	 * @param request request
	 */
	private void setQueryStatus(HttpServletRequest request)
	{
		String isQuery = request.getParameter("isQuery");
		if (isQuery == null)
		{
			request.setAttribute("isQuery", "false");
		}
		else
		{
			request.setAttribute("isQuery", isQuery);
		}
	}
	/**
	 *
	 * @param request request
	 * @param response response
	 * @param searchForm searchForm
	 * @param textfieldValue textfieldValue
	 * @throws CheckedException CheckedException
	 * @throws IOException IOException
	 */
	private void setEntities(HttpServletRequest request,HttpServletResponse response,
	CategorySearchForm searchForm,String textfieldValue) throws CheckedException, IOException
	{
		int[] searchTarget = prepareSearchTarget(searchForm);
		int basedOn = Constants.BASED_ON_TEXT;
		String[] searchString = prepareSearchString(textfieldValue);
		String attributeChecked = searchForm.getAttributeChecked();
		String prmsblValsChecked = searchForm.getPermissibleValuesChecked();
		StringBuffer entitiesString = new StringBuffer();
		EntityCache cache = EntityCacheFactory.getInstance();
		MetadataSearch advancedSearch = new MetadataSearch(cache);
		MatchedClass matchedClass = advancedSearch.search(searchTarget, searchString, basedOn);
		Set<EntityInterface> entityCollection = matchedClass.getEntityCollection();
		List<EntityInterface> resultList = new ArrayList<EntityInterface>(entityCollection);
		for (int counter = 0; counter < resultList.size(); counter++)
		{
			populateEntityString(entitiesString, resultList, counter);
		}
		if (entitiesString.length()==0)
		{
			entitiesString = new StringBuffer(ApplicationProperties.getValue
					("query.noResultFoundMessage"));
		}
		else
		{
			String keySeparator = edu.wustl.query.util.global.AQConstants.KEY_CODE;
			String key = request.getParameter(keySeparator);
			entitiesString.append(edu.wustl.query.util.global.AQConstants.KEY_SEPARATOR).append(key);
			entitiesString.append(edu.wustl.query.util.global.AQConstants.KEY_SEPARATOR)
			.append(textfieldValue);
			entitiesString.append(edu.wustl.query.util.global.AQConstants.KEY_SEPARATOR)
			.append(attributeChecked);
			entitiesString.append(edu.wustl.query.util.global.AQConstants.KEY_SEPARATOR).
			append(prmsblValsChecked);
		}
		response.setContentType("text/html");
		response.getWriter().write(entitiesString.toString());
	}

	/**
	 * @param entitiesString entity string
	 * @param resultList result list
	 * @param counter counter
	 */
	private void populateEntityString(StringBuffer entitiesString,
			List<EntityInterface> resultList, int counter)
	{
		EntityInterface entity = (EntityInterface) resultList.get(counter);
		String entityName = entity.getName();
		if(!isChildSpecimenEntry(entityName))
		{
			String parsedEntityName = edu.wustl.query.util.global.
			Utility.parseClassName(entityName);
			String entityLabel = Utility.getDisplayLabel(parsedEntityName);
			String entityId = entity.getId().toString();
			String description = entity.getDescription();
			if(description == null || description.equals(""))
			{
				description = AQConstants.NO_DESCRIPTION;
			}
			entitiesString.append(edu.wustl.query.util.global.AQConstants.ENTITY_SEPARATOR).append
			(entityLabel).append(edu.wustl.query.util.global.AQConstants.ATTRIBUTE_SEPARATOR)
			.append(entityId).append(edu.wustl.query.util.global.AQConstants.ATTRIBUTE_SEPARATOR)
			.append(description);
		}
	}

	private boolean isChildSpecimenEntry(String entityName) {
		if(AQConstants.TISSUE_SPECIMEN.equalsIgnoreCase(entityName) ||
				AQConstants.CELL_SPECIMEN.equalsIgnoreCase(entityName) ||
				AQConstants.MOLECULAR_SPECIMEN.equalsIgnoreCase(entityName) ||
				AQConstants.FLUID_SPECIMEN.equalsIgnoreCase(entityName) )
		{
			return true;
		}
		return false;
	}

	/**
	 * Prepares a String to be sent to AdvancedSearch logic.
	 * @param textfieldValue String
	 * @return String[] array of strings , taken from user.
	 */
	private String[] prepareSearchString(String textfieldValue)
	{
		int counter = 0;
		StringTokenizer tokenizer = new StringTokenizer(textfieldValue);
		String[] searchString = new String[tokenizer.countTokens()];
		while (tokenizer.hasMoreTokens())
		{
			searchString[counter] = tokenizer.nextToken();
			counter++;
		}
		return searchString;
	}

	/**
	 * Prepares the int [] for search targets from the check box values selected by user.
	 * @param searchForm action form
	 * @return int[] Integer array of selections made by user.
	 */
	private int[] prepareSearchTarget(CategorySearchForm searchForm)
	{
		String clsCBChecked = searchForm.getClassChecked();
		String attCBChecked = searchForm.getAttributeChecked();
		String pvCBChecked = searchForm.getPermissibleValuesChecked();
		String description = searchForm.getIncludeDescriptionChecked();
		List<Integer> target = new ArrayList<Integer>();
		checkClassDescription(clsCBChecked, description, target);
		checkAttributeDescription(attCBChecked, description, target);
		checkPermissibleValues(pvCBChecked, target);
		int[] searchTarget = new int[target.size()];
		for (int i = 0; i < target.size(); i++)
		{
			searchTarget[i] = target.get(i).intValue();
		}
		return searchTarget;
	}

	/**
	 * @param pvCBChecked string
	 * @param target
	 */
	private void checkPermissibleValues(String pvCBChecked, List<Integer> target)
	{
		if (pvCBChecked != null
				&& (pvCBChecked.equalsIgnoreCase("on") || pvCBChecked
						.equalsIgnoreCase(AQConstants.TRUE)))
		{
			target.add(Integer.valueOf(Constants.PERMISSIBLEVALUE));
		}
	}

	/**
	 * @param clsCBChecked string
	 * @param description description
	 * @param target
	 */
	private void checkClassDescription(String clsCBChecked, String description,
			List<Integer> target)
	{
		if (clsCBChecked != null
				&& (clsCBChecked.equalsIgnoreCase("on") || clsCBChecked
						.equalsIgnoreCase(AQConstants.TRUE)))
		{
			populateTargetForClass(description, target);
		}
	}

	/**
	 * @param description description
	 * @param target
	 */
	private void populateTargetForClass(String description, List<Integer> target)
	{
		if (description != null
				&& (description.equalsIgnoreCase("on") ||
					description.equalsIgnoreCase(AQConstants.TRUE)))
		{
			target.add(Integer.valueOf(Constants.CLASS_WITH_DESCRIPTION));
		}
		else
		{
			target.add(Integer.valueOf(Constants.CLASS));
		}
	}

	/**
	 * @param attCBChecked string
	 * @param description description
	 * @param target
	 */
	private void checkAttributeDescription(String attCBChecked,
			String description, List<Integer> target) {
		if (attCBChecked != null
				&& (attCBChecked.equalsIgnoreCase("on") || attCBChecked
						.equalsIgnoreCase(AQConstants.TRUE)))
		{
			populateTargetForAttribute(description, target);
		}
	}

	/**
	 * @param description description
	 * @param target
	 */
	private void populateTargetForAttribute(String description, List<Integer> target)
	{
		if (description != null
			&& (description.equalsIgnoreCase("on") ||
				description.equalsIgnoreCase(AQConstants.TRUE)))
		{
			target.add(Integer.valueOf(Constants.ATTRIBUTE_WITH_DESCRIPTION));
		}
		else
		{
			target.add(Integer.valueOf(Constants.ATTRIBUTE));
		}
	}
}