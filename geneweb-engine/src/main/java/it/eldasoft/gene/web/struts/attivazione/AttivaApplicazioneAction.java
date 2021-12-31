/*
 * Created on 31-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.attivazione;

import it.eldasoft.gene.bl.AttConfigManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.PlugInBase;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

public class AttivaApplicazioneAction extends Action {

  static Logger logger = Logger.getLogger(AttivaApplicazioneAction.class);

  @Override
  public final ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("execute: inizio metodo");

    DataSourceTransactionManagerBase.setRequest(request);

    String target = null;

    AttConfigManager attConfigManager = (AttConfigManager) UtilitySpring.getBean("attConfigManager", this.getServlet().getServletContext(),
        AttConfigManager.class);

    try {
      String codiceCliente = request.getParameter("codiceCliente");
      String acquirenteSW = request.getParameter("acquirenteSW");
      String responsabileCliente = request.getParameter("responsabileCliente");
      String responsabileClienteEmail = request.getParameter("responsabileClienteEmail");
      String opzioniDisponibili = request.getParameter("opzioniDisponibili");

      String codapp = ConfigManager.getValore("it.eldasoft.codApp");
      attConfigManager.saveAttConfig(codapp, CostantiGenerali.PROP_CODICE_CLIENTE, codiceCliente);
      attConfigManager.saveAttConfig(codapp, CostantiGenerali.PROP_ACQUIRENTE, acquirenteSW);
      attConfigManager.saveAttConfig(codapp, CostantiGenerali.PROP_RESPONSABILE_CLIENTE, responsabileCliente);
      attConfigManager.saveAttConfig(codapp, CostantiGenerali.PROP_EMAIL_RESPONSABILE_CLIENTE, responsabileClienteEmail);
      attConfigManager.saveAttConfig(codapp, CostantiGenerali.PROP_DATA_ATTIVAZIONE, UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
      attConfigManager.saveAttConfig(codapp, CostantiGenerali.PROP_OPZIONI_DISPONIBILI, opzioniDisponibili);

      this.getServlet().getServletContext().removeAttribute(CostantiGenerali.SENTINELLA_BLOCCO_ATTIVAZIONE);
      PlugInBase.loadOpzioniApplicazioneDisponibile(this.getServlet().getServletContext(), ResourceBundle.getBundle("AliceResources"),
          PlugInBase.AVVIO_STANDARD);

      target = CostantiGeneraliStruts.FORWARD_OK;

    } catch (Exception e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      String messageKey = "errors.attivazione.errore";
      logger.error(ResourceBundle.getBundle("AliceResources").getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) logger.debug("execute: fine metodo");
    return mapping.findForward(target);

  }


  /**
   * Crea un ActionMessages nel request, popolato con il messaggio individuato
   * dalla chiave in input
   *
   * @param request
   *        request http
   * @param chiave
   *        chiave del messaggio da inserire
   */
  public void aggiungiMessaggio(HttpServletRequest request, String chiave) {
    String tipoMessaggio = ActionBase.getTipoMessaggioFromChiave(chiave);

    ActionMessages errors = new ActionMessages();
    errors.add(tipoMessaggio, new ActionMessage(chiave));
    if (!errors.isEmpty()) this.addMessages(request, errors);
  }

}