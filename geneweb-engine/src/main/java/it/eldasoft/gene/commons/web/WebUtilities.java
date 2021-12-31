/*
 * Created on 09/set/2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.utils.spring.SpringAppContext;

import javax.servlet.ServletContext;

/**
 * Classe di utilit&agrave; statiche per applicazioni WEB.
 * 
 * @author Stefano.Sabbadin
 */
public class WebUtilities {

  /**
   * Testa se l'applicativo risulta attivo, correttamente configurato, ed
   * attivato.
   * 
   * @return true se l'applicativo non e' disponibile agli utenti, false
   *         altrimenti (applicativo fruibile dagli utenti finali)
   */
  public static boolean isAppNotReady() {
    ServletContext context = SpringAppContext.getServletContext();
    String appDisponibile = (String) context.getAttribute(CostantiGenerali.SENTINELLA_APPLICAZIONE_CARICATA);
    String appBloccata = (String) context.getAttribute(CostantiGenerali.SENTINELLA_BLOCCO_ATTIVAZIONE);
    boolean applicativoNonAvviato = (appDisponibile == null
        || !"1".equals(appDisponibile) || appBloccata != null);
    return applicativoNonAvviato;
  }
}
