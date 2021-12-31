/*
 * Created on 28-ago-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.datigen;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.List;

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
 * Action per l'apertura della pagina di Edit dei Dati Generali della ricerca in
 * analisi
 * 
 * @author Luca Giacomazzo
 */
public class EditDatiGenRicercaAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(EditDatiGenRicercaAction.class);

  /**
   * Reference alla classe di business logic per l'accesso ai tabellati
   */
  private TabellatiManager tabellatiManager;

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi alle
   * ricerche
   */
  private RicercheManager  ricercheManager;

  /**
   * @return Ritorna tabellatiManager.
   */
  public TabellatiManager getTabellatiManager() {
    return tabellatiManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @return Ritorna ricercheManager.
   */
  public RicercheManager getRicercheManager() {
    return ricercheManager;
  }

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action runAction
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("execute: inizio metodo");
    }

    // target di default
    String target = null;
    String messageKey = null;

    try {
      // Caricamento degli oggetti per popolare le comboBox presenti nella
      // pagina Lista per popolamento comboBox 'Tipo Ricerca'
      List<Tabellato> listaTipoRicerca = this.tabellatiManager.getTabellato(TabellatiManager.TIPO_RICERCHE);

      // lista per il popolamento della comboBox 'Risultati per Pagina'
      String[] listaRisPerPagina = CostantiGenerali.CBX_RIS_PER_PAGINA_REPORT;

      // Set nel request delle liste per il popolamento delle varie combobox
      request.setAttribute("listaTipoRicerca", listaTipoRicerca);
      request.setAttribute("listaRisPerPagina", listaRisPerPagina);

      // lettura dalla sessione dei parametri relativi alla ricerca in analisi
      HttpSession sessione = request.getSession();
      TestataRicercaForm testataRicercaForm = (TestataRicercaForm) ((ContenitoreDatiRicercaForm) sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO)).getTestata();
      if(testataRicercaForm.getFamiglia().intValue() == CostantiGenRicerche.REPORT_AVANZATO)
        target = "successRicercaAvanzata";
      else if(testataRicercaForm.getFamiglia().intValue() == CostantiGenRicerche.REPORT_BASE)
        target = "successRicercaBase";
      else if(testataRicercaForm.getFamiglia().intValue() == CostantiGenRicerche.REPORT_SQL)
        target = "successRicercaSql";
      
      // Set nel request del form per settare le combobox e le textarea
      request.setAttribute("testataRicercaForm", testataRicercaForm);

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // Update del menu tab
      this.setMenuTab(request);

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

    if (logger.isDebugEnabled()) logger.debug("execute: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio dei Dati Generali di
   * una ricerca
   * 
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_DATI_GENERALI);
      gestoreTab.setTabSelezionabili(null);
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_DATI_GENERALI);
      gestoreTab.setTabSelezionabili(null);
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
    }
  }
}