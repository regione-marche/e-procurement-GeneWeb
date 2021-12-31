/*
 * Created on 02-ott-2006
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
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Azione che esegue il caricamento della pagina Lista Profili, filtrati per 
 * codice applicazione 
 * 
 * @author Luca.Giacomazzo
 */
public class ListaProfiliAction extends AbstractActionBaseAdmin {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ListaProfiliAction.class);

  /**
   * Reference alla classe di business logic per l'estrazione della lista gruppo
   */
  private ProfiliManager profiliManager;

  /**
   * @return Ritorna gruppiManager.
   */
  public void setProfiliManager(ProfiliManager profiliManager) {
    this.profiliManager = profiliManager;
  }

  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_LETTURA_ADMIN);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(
   *      org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // cancellazione di oggetti in sessione precedentemente creati
    this.cleanSession(request);

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    try {
      // Determinazione della lista dei profili filtrati per codice applicazione
      List<?> listaProfili = this.profiliManager.getListaProfiliByCodApp((String)
          request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));

      // set nel request del beanForm contenente la lista dei profili
      request.setAttribute("listaProfiliForm", listaProfili);

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

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }
    return mapping.findForward(target);
  }
  
}