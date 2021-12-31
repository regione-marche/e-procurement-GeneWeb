/*
 * Created on 16-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Classe base per tutte le Action semplici del package di gestione delle
 * ricerche base
 * 
 * @author Francesco.DeFilippis
 */
public abstract class AbstractActionBaseGenRicercheBase extends ActionBase {

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioniRunAction()
   */
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  /**
   * Imposta alcuni dati nel request e nel session in modo da:
   * <ul>
   * <li>poter gestire il prompt del salvataggio nel momento in cui si esce
   * dalla ricerca in caso di modifiche eseguite e non salvate</li>
   * <li>poter segnalare a video all'utente che sono state fatte delle
   * modifiche ai dati</li>
   * </ul>
   * 
   * @param request
   *        request http
   */
  protected void marcaRicercaModificata(HttpServletRequest request) {
    HttpSession session = request.getSession();
    // l'oggetto di sessione è stato modificato, quindi attivo la sentinella
    // delle modifiche
    session.setAttribute(CostantiGenerali.SENTINELLA_OGGETTO_MODIFICATO, "1");
    // set nel request del parameter per disabilitare la navigazione anche in
    // fase di visualizzazione del dato in quanto modificato in sessione
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);
  }

  /**
   * Imposta alcuni dati nel request e nel session in modo da:
   * <ul>
   * <li>poter gestire il prompt del salvataggio nel momento in cui si esce
   * dalla ricerca in caso di modifiche eseguite e non salvate</li>
   * <li>poter segnalare a video all'utente che sono state fatte delle
   * modifiche ai dati</li>
   * </ul>
   * 
   * @param request
   *        request http
   * @param nomeRicerca
   *        nome della ricerca
   */
  protected void marcaRicercaModificata(HttpServletRequest request,
      String nomeRicerca) {
    this.marcaRicercaModificata(request);
    // set in sessione del nome della ricerca di cui si sta facendo il
    // dettaglio
    request.getSession().setAttribute(
        CostantiGenerali.NOME_OGGETTO_SESSION,
        nomeRicerca
            + " "
            + this.resBundleGenerale.getString(CostantiGenRicerche.LABEL_SUFFISSO_NOME_RICERCA_IN_MODIFICA));
  }

}
