package edu.wustl.query.action;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.action.SecureAction;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.query.htmlprovider.GenerateHTMLDetails;
import edu.wustl.query.htmlprovider.SavedQueryHtmlProvider;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.QueryModuleError;
import edu.wustl.query.util.querysuite.QueryModuleException;

/**
 * AJAX handler to sho all/selected attributes on Save Query page
 *
 * @author vijay_pande
 *
 */
public class ShowParamQueryAttributesAction extends SecureAction
{

    /*
     * (non-Javadoc)
     *
     * @see edu.wustl.common.action.BaseAction#executeAction(
     * org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected ActionForward executeSecureAction(final ActionMapping mapping,
            final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws QueryModuleException
    {
        final Boolean isChecked = Boolean.parseBoolean(request
                .getParameter("isChecked"));
        final HttpSession session = request.getSession();
        final IQuery queryObject = (IQuery) session
                .getAttribute(AQConstants.QUERY_OBJECT);
        final Map<Integer, ICustomFormula> customFormulaIndexMap = new HashMap<Integer, ICustomFormula>();
        /**
         * isChecked = showAll, isChecked boolean value directly used to show
         * all conditions isChecked = true : Show all conditional parameters
         * isChecked = false : Show only those parameters on which user had set
         * any condition
         */
        final GenerateHTMLDetails generateHTMLDetails = new GenerateHTMLDetails();
        generateHTMLDetails.setSearchString("");
        generateHTMLDetails.setAttributeChecked(false);
        generateHTMLDetails.setPermissibleValuesChecked(false);

        String htmlContents;
        try
        {
            htmlContents = new SavedQueryHtmlProvider().getHTMLForSavedQuery(
                    queryObject, isChecked, AQConstants.SAVE_QUERY_PAGE,
                    customFormulaIndexMap);

           final Writer writer = response.getWriter();
            writer.write(htmlContents);
        }
        catch (final IOException ex)
        {
            throw new QueryModuleException(ex.getMessage(),ex,
                    QueryModuleError.GENERIC_EXCEPTION);
        }

        return null;
    }

}
