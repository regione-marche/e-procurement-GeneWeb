package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.SqlManager;

import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.log4j.Logger;

/**
 * @author Cristian.Febas
 *
 */
public class GetListaEntiAction extends Action {
	
	private static final Logger LOGGER = Logger.getLogger(GetListaEntiAction.class);

	private static final String SELECT_UFFINT_QUERY = 
			"SELECT nomein, cfein, codein FROM uffint WHERE cfein LIKE ? OR ivaein LIKE ? ORDER BY nomein";
	
	private static final String CFEIN_INTEGRITY_CHECK = "^[a-zA-Z0-9]{1,16}$";
	
	private static final String CFEIN_INTEGRITY_ERR_MSG = "Parametro inserito non corretto: %s";
	
  /**
   * Manager per la gestione delle interrogazioni di database.
   */
  private SqlManager sqlManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String cfein = request.getParameter("cfamm");
    if (!Pattern.matches(CFEIN_INTEGRITY_CHECK, cfein)) {
    	final String errorMsg = String.format(CFEIN_INTEGRITY_ERR_MSG, cfein);
    	
    	LOGGER.error(errorMsg);
    	throw new JspException(errorMsg);
    }
    
    List<?> datiUFFINT = sqlManager.getListVector(SELECT_UFFINT_QUERY, new Object[] {cfein + "%", cfein + "%"});

    JSONArray jsonArrayUFFINT = null;
    if (datiUFFINT != null && datiUFFINT.size() > 0) {
    	jsonArrayUFFINT= JSONArray.fromObject(datiUFFINT.toArray());
    } else {
    	jsonArrayUFFINT = new JSONArray();
    }

    out.println(jsonArrayUFFINT);


    out.flush();
    return null;
  }

}
