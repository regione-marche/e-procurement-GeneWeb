/*
 * Created on 2-ott-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.admin.UffintManager;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.ListaForm;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action che controlla l'operazione di salvataggio delle associazioni tra gli uffici intestatari e l'account in analisi
 *
 * @author Stefano.Sabbadin
 */
public class SalvaUfficiIntestatariAccountAction extends AbstractActionBaseAdmin {

  /** Logger Log4J di classe */
  static Logger          logger = Logger.getLogger(SalvaUfficiIntestatariAccountAction.class);

  /**
   * Reference alla classe di business logic per le operazioni gli
   * utenti/account e il gruppo
   */
  private UffintManager uffintManager;

  /**
   * @param uffintManager uffintManager da settare internamente alla classe.
   */
  public void setUffintManager(UffintManager uffintManager) {
    this.uffintManager = uffintManager;
  }

  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  public ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // target di default per da modificare nel momento in cui si verificano 
    // dei problemi
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    try {
      // Lettura dal request l'ID_GRUPPO del gruppo in analisi
      int idAccount = Integer.parseInt((String) request.getParameter("idAccount"));

      ListaForm idUfficiAssociati = (ListaForm) form;

      this.uffintManager.updateAssociazioneUfficiIntestatariAccount(idAccount,
          idUfficiAssociati.getId());
      
      request.setAttribute("idAccount", new Integer(idAccount));

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

}