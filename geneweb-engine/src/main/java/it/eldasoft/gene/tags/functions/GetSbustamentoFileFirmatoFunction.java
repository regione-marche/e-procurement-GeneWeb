package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


public class GetSbustamentoFileFirmatoFunction extends AbstractFunzioneTag {

  public GetSbustamentoFileFirmatoFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    Date ckdate = null;
    String state = null;
    String message = null;
    String dignomdoc = null;
    String dignomdoc_tsd = null;
    String dignomdoc_p7m = null;
    String dignomdoc_doc = null;
    
    try {
      String idprg = (String) params[1];
      Long iddocdig = null;
      if ((String) params[2] != null) {
        iddocdig = new Long((String) params[2]);
      }

      dignomdoc = (String) sqlManager.getObject("select dignomdoc from w_docdig where idprg = ? and iddocdig = ?", new Object[] {
          idprg, iddocdig });

      if (dignomdoc != null) {
    	  if (dignomdoc.toLowerCase().endsWith(".tsd")) {
    	      dignomdoc_tsd = dignomdoc;
    		  dignomdoc_p7m = dignomdoc.substring(0, dignomdoc.toLowerCase().indexOf(".tsd"));
          } else {
            // si presuppone sia un .p7m (senza estensione .tsd)
    		  dignomdoc_p7m = dignomdoc;
    	  }
    	  if (dignomdoc_p7m.toLowerCase().indexOf(".p7m") >= 0) {
    	    // si tolgono tutte le estensioni p7m e si recupera il documento originariamente firmato
    		  dignomdoc_doc = dignomdoc_p7m.substring(0, dignomdoc_p7m.toLowerCase().indexOf(".p7m"));
    	  } else {
    	    // trattasi di un documento marcato temporalmente, senza firma digitale applicata
    		  dignomdoc_doc = dignomdoc_p7m;
    		  dignomdoc_p7m = null;
    	  }
       
      } else {
        state = "NO-DATA-FOUND";
      }
    } catch (Exception e) {
      state = "ERROR";
      message = e.getMessage();
      if (e.getCause() != null) message += " (" + e.getCause().toString() + ")";
    }
    pageContext.setAttribute("dignomdoc_tsd", dignomdoc_tsd, PageContext.REQUEST_SCOPE);
   	pageContext.setAttribute("dignomdoc_p7m", dignomdoc_p7m, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("dignomdoc_doc", dignomdoc_doc, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("ckdateformat", UtilityDate.convertiData(ckdate, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("state", state, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("message", message, PageContext.REQUEST_SCOPE);

    return null;
  }
}
