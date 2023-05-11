/*
 * Created on 15-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.gene.web.struts.genric.prospetto;

import javax.servlet.http.HttpServletRequest;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.gene.web.struts.genmod.SalvaParametroModelloAction;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;

/**
 * Classe per la gestione dell'aggiornamento nel database dei parametri di un 
 * modello associato ad una ricerca con modello
 * 
 * @author Luca.Giacomazzo
 */
public class SalvaParametroProspettoAction extends SalvaParametroModelloAction {

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL;
  }
  
  /**
   * Ridefinizione della funzione che inizializza nel request la gestione dei tab
   * per
   * 
   * @param request
   */
  public void setMenuTab(HttpServletRequest request,
      boolean abilitaTabSelezionabili) {
    GestioneTab gestoreTab = (GestioneTab) request.getAttribute(CostantiGenModelli.NOME_GESTORE_TAB);

    if (gestoreTab == null) {
      gestoreTab = new GestioneTab();
      request.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
    }

    gestoreTab.setTabAttivo(CostantiGenProspetto.TAB_PARAMETRI);
    if (abilitaTabSelezionabili)
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiGenProspetto.TAB_DATI_GENERALI, CostantiGenProspetto.TAB_GRUPPI });
  }
}