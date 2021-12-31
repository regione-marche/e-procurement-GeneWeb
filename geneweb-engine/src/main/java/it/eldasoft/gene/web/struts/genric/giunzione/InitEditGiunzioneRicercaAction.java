/*
 * Created on 11-set-2005
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.giunzione;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.genric.GiunzioneRicerca;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.LegameTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per per l'apertura dell'edit di una giunzione della ricerca in analisi
 * 
 * @author Luca Giacomazzo
 */

public class InitEditGiunzioneRicercaAction extends
    AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(InitEditGiunzioneRicercaAction.class);

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;

    // lettura dal request del progressivo dall'argomento da editare
    Integer progressivo = UtilityNumeri.convertiIntero(request.getParameter("prog"));
    if (progressivo == null)
      progressivo = UtilityNumeri.convertiIntero((String) request.getAttribute("prog"));
    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    Vector<GiunzioneRicercaForm> vec = contenitore.getElencoGiunzioni();

    GiunzioneRicercaForm giunzioneRicercaForm = (GiunzioneRicercaForm) vec.elementAt(progressivo.intValue());
    // set nel request dei dati necessari per il popolamento della pagina

    request.setAttribute("giunzioneRicercaForm", giunzioneRicercaForm);

    Tabella tabOrigine = DizionarioTabelle.getInstance().get(
        giunzioneRicercaForm.getMnemonicoTabella1());
    String nomeTabDestinazione = DizionarioTabelle.getInstance().get(
        giunzioneRicercaForm.getMnemonicoTabella2()).getNomeTabella();

    // estraggo, a partire dalla tabella di origine, tutte le join verso la
    // tabella destinazione.
    LegameTabelle[] legami = tabOrigine.getLegameTabelle(nomeTabDestinazione);
    Vector<String> elencoCampi1 = new Vector<String>();
    String chiaveConcatenata = null;
    for (int i = 0; i < legami.length; i++) {
      chiaveConcatenata = UtilityStringhe.serializza(
          legami[i].getElencoCampiTabellaOrigine(),
          GiunzioneRicerca.SEPARATORE_CAMPI_JOIN);
      if (!elencoCampi1.contains(chiaveConcatenata))
        elencoCampi1.add(chiaveConcatenata);
    }
    request.setAttribute("elencoCampi1", elencoCampi1);

    Vector<String> elencoCampi2 = new Vector<String>();
    for (int i = 0; i < legami.length; i++) {
      chiaveConcatenata = UtilityStringhe.serializza(
          legami[i].getElencoCampiTabellaDestinazione(),
          GiunzioneRicerca.SEPARATORE_CAMPI_JOIN);
      if (!elencoCampi2.contains(chiaveConcatenata))
        elencoCampi2.add(chiaveConcatenata);
    }
    request.setAttribute("elencoCampi2", elencoCampi2);

    // set nel request del parameter per disabilitare la navigazione in fase di
    // editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

    // update del menu tab per l'edit
    this.setMenuTab(request);

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di creazione di una giunzione associata ad una
   * ricerca
   * 
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);

    boolean isInSessione = true;
    if (obj == null) {
      isInSessione = false;
      obj = new GestioneTab();
    }
    GestioneTab gestoreTab = (GestioneTab) obj;
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_JOIN);
    gestoreTab.setTabSelezionabili(null);

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }
}