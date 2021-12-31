/*
 * Created on 16-feb-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Consente l'accesso diretto all'applicativo mediante inserimento delle
 * credenziali e dell'eventuale codice profilo
 * 
 * @author Stefano.Cestaro
 * @since 1.4.6
 */
public class AccediAltroApplicativoAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(AccediAltroApplicativoAction.class);

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String url = request.getParameter("url");
    String profilo = request.getParameter("profilo");

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    String username = profiloUtente.getLogin();
    String passwordCifrata = profiloUtente.getPwd();
    String password = "";
    if (passwordCifrata != null) {
      ICriptazioneByte decriptatore;
      try {
        decriptatore = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
            passwordCifrata.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
        password = new String(decriptatore.getDatoNonCifrato());
      } catch (CriptazioneException e) {
        // non si verificherà mai, in quanto il dato in precedenza è stato
        // cifrato, e ora viene decifrato
        logger.error(
            this.resBundleGenerale.getString(e.getChiaveResourceBundle()), e);
      }
    }

    // HTML da inviare al browser volutamente mantenuto dentro la action e non
    // gestito mediante forward in modo da mascherare più possibile come vengono
    // passati i dati tra un applicativo alice e un altro per consentire
    // l'acceso all'utente
    StringBuffer html = new StringBuffer();
    html.append("<html>");
    html.append("<head>");
    html.append("<META http-equiv=\"Pragma\" content=\"no-cache\">");
    html.append("<META http-equiv=\"Expires\" content=\"-1\">");
    html.append("</head>");
    html.append("<body onload=\"javascript:document.forms[0].submit();\">");
    html.append("  <form action=\"").append(url).append(
        "/Login.do\" method=\"post\" >");
    html.append("  <input type=\"hidden\" name=\"username\" value=\"").append(
        username).append("\"/>");
    html.append("  <input type=\"hidden\" name=\"password\" value=\"").append(
        password).append("\"/>");
    if (profilo != null)
      html.append("  <input type=\"hidden\" name=\"profilo\" value=\"").append(
          profilo).append("\"/>");
    html.append("   </form>");
    html.append("</body>");
    html.append("</html>");

    response.getWriter().write(html.toString());

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(null);
  }

}