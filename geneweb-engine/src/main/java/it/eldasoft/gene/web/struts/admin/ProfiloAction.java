/*
 * Created on 02-Ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.admin.ProfiliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Profilo;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action di gestione del profilo in sola visualizzazione
 * 
 * @author Luca.Giacomazzo
 */
public class ProfiloAction extends AbstractActionBaseAdmin {

  static Logger logger = Logger.getLogger(ProfiloAction.class);

  protected ProfiliManager    profiliManager;

  public void setProfiliManager(ProfiliManager profiliManager) {
    this.profiliManager = profiliManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action editLista
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_LETTURA_ADMIN);
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    String target = null;
    String codProfilo = request.getParameter("codPro");
    String messageKey = null;

    try {
      Profilo profilo = this.profiliManager.getProfiloByCodProfilo(codProfilo);
      ProfiloForm formProfilo = new ProfiloForm(profilo);
      target = CostantiGeneraliStruts.FORWARD_OK;
      this.setMenuTab(request);
      HttpSession sessione = request.getSession();
      sessione.setAttribute(CostantiGenerali.ID_OGGETTO_SESSION, codProfilo);
      sessione.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
          profilo.getNome());
      request.setAttribute("profiloForm", formProfilo);

    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }
    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab
   * 
   * @param request
   */
  protected void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioProfilo.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioProfilo.DETTAGLIO);
      gestoreTab.setTabSelezionabili(new String[] {
            CostantiDettaglioProfilo.DETTAGLIO,
            CostantiDettaglioProfilo.UTENTI,
            CostantiDettaglioProfilo.GRUPPI});
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioProfilo.DETTAGLIO);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioProfilo.DETTAGLIO,
          CostantiDettaglioProfilo.UTENTI,
          CostantiDettaglioProfilo.GRUPPI});
      sessione.setAttribute(CostantiDettaglioProfilo.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }

}