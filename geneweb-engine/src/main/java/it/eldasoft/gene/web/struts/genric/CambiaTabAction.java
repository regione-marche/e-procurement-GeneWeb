/*
 * Created on 21-ago-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.CostantiIntegrazioneKronos;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Azione per la gestione del cambio Tab, nelle pagine di creazione e/o modifica
 * di una ricerca
 * 
 * @author Luca.Giacomazzo
 */
public class CambiaTabAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger              logger                   = Logger.getLogger(CambiaTabAction.class);

  /**
   * Codice inviato tramite request e che individua la selezione del tab dati
   * generali
   */
  public static final String CODICE_TAB_DATI_GENERALI = "DG";

  /**
   * Codice inviato tramite request e che individua la selezione del tab gruppi
   */
  public static final String CODICE_TAB_GRUPPI        = "GRP";

  /**
   * Codice inviato tramite request e che individua la selezione del tab
   * argomenti/tabelle
   */
  public static final String CODICE_TAB_ARGOMENTI     = "TAB";

  /**
   * Codice inviato tramite request e che individua la selezione del tab campi
   */
  public static final String CODICE_TAB_CAMPI         = "CAM";

  /**
   * Codice inviato tramite request e che individua la selezione del tab campi
   */
  public static final String CODICE_TAB_JOIN          = "JOI";

  /**
   * Codice inviato tramite request e che individua la selezione del tab
   * parametri
   */
  public static final String CODICE_TAB_PARAMETRI     = "PAR";

  /**
   * Codice inviato tramite request e che individua la selezione del tab filtri
   */
  public static final String CODICE_TAB_FILTRI        = "FIL";

  /**
   * Codice inviato tramite request e che individua la selezione del tab
   * ordinamenti
   */
  public static final String CODICE_TAB_ORDINAMENTI   = "ORD";

  /**
   * Codice inviato tramite request e che individua la selezione del tab layout
   */
  public static final String CODICE_TAB_LAYOUT        = "LAY";

  /**
   * Codice inviato tramite request e che individua la selezione del tab SQL (solo per report SQL)
   */
  public static final String CODICE_TAB_SQL           = "SQL";
  
  /**
   * Reference alla classe di business logic per accesso ai dati relativi ai
   * tabellati
   */
  private TabellatiManager   tabellatiManager;

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    String target = null;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    // il codice del tab prova a reperirlo dal request, prima come attributo
    // proveniente da un'altra action, e poi come parametro di una richiesta
    String codiceTab = (String) request.getAttribute("tab");
    if (codiceTab == null) codiceTab = request.getParameter("tab");

    if (CODICE_TAB_GRUPPI.equals(codiceTab)) {

      if (this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        String messageKey = "errors.applicazione.gruppiDisabilitati";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      } else {
        ProfiloUtente account = (ProfiloUtente)
            session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        // carico le opzioni per cui è abilitato l'utente loggato
        OpzioniUtente opzioniUtente = new OpzioniUtente(
            account.getFunzioniUtenteAbilitate());
        CheckOpzioniUtente opzioniPerAbilitazione = new CheckOpzioniUtente(
            CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
        // l'entrata nel tab dei gruppi è valida solo per utenti con gestione
        // completa delle ricerche
        if (!opzioniPerAbilitazione.test(opzioniUtente)) {
          target = CostantiGeneraliStruts.FORWARD_OPZIONE_NON_ABILITATA;
          String messageKey = "errors.opzione.noAbilitazione";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        } else {
          target = "gruppi";
          this.setMenuTab(request, CostantiGenRicerche.TAB_GRUPPI);
        }
      }
    } else if (CODICE_TAB_ARGOMENTI.equals(codiceTab)) {
      target = "argomenti";
      this.setMenuTab(request, CostantiGenRicerche.TAB_ARGOMENTI);
    } else if (CODICE_TAB_CAMPI.equals(codiceTab)) {
      target = "campi";
      this.setMenuTab(request, CostantiGenRicerche.TAB_CAMPI);
    } else if (CODICE_TAB_JOIN.equals(codiceTab)) {

      // se il numero di join è inferiore al numero delle tabelle meno 1
      // segnalo che potrebbero esserci prodotti cartesiani
      if (contenitore.getNumeroGiunzioni() < contenitore.getNumeroTabelle() - 1) {
        String messageKey = "warnings.genRic.numeroCriticoGiunzioniAttive";
        if (logger.isInfoEnabled())
          logger.info(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
      target = "join";
      this.setMenuTab(request, CostantiGenRicerche.TAB_JOIN);

    } else if (CODICE_TAB_PARAMETRI.equals(codiceTab)) {
      target = "parametri";
      this.setMenuTab(request, CostantiGenRicerche.TAB_PARAMETRI);

      // set nel request della listaValoriTabellato per lasciare alla pagina JSP
      // la conversione di tipoTabellato in codiceTabellato
      List<Tabellato> listaValoriTabellati = this.tabellatiManager.getTabellato(CostantiGenRicerche.TIPO_VALORE_TABELLATO);
      request.setAttribute("listaValoriTabellati", listaValoriTabellati);
     
      if (CostantiGenRicerche.REPORT_SQL != contenitore.getTestata().getFamiglia()) {
        List<Tabellato> elencoTabellati = this.tabellatiManager.getElencoTabellati(CostantiGenRicerche.TIPO_TABELLATO);
        request.setAttribute("elencoTabellati", elencoTabellati);
      }
      
      if (CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS.equals(ConfigManager.getValore(CostantiGenerali.PROP_INTEGRAZIONE))
          && CostantiIntegrazioneKronos.TIPO_RICERCA_KRONOS.equals(contenitore.getTestata().getTipoRicerca())) {
        // questo flag permetterà di inserire il pulsante di aggiungi parametro
        // per l'aggiunta della selezione delle variabili UTE_VARXX nelle query
        // di KRONOS
        request.setAttribute(CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS, "1");
      }

    } else if (CODICE_TAB_FILTRI.equals(codiceTab)) {
      // L.G. 30/03/2007 modifiche per implementazione ricerche base: la
      // visualizzazione della lista dei filtri di una ricerca base non deve
      // visualizzare gli operatori logici AND tra le condizioni di filtro
      session = request.getSession();
      if (CostantiGenRicerche.REPORT_BASE == contenitore.getTestata().getFamiglia().intValue()) {
        Vector<FiltroRicercaForm> elencoFiltriSessione = contenitore.getElencoFiltri();
        List<FiltroRicercaForm> elencoFiltri = null;

        // Creazione dell'elenco di filtri da presentare a video: la lista
        // non deve mostrare gli operatori AND tra due condizioni
        if (elencoFiltriSessione != null && elencoFiltriSessione.size() > 0) {
          elencoFiltri = new ArrayList<FiltroRicercaForm>();
          for (int i = 0; i < elencoFiltriSessione.size(); i += 2)
            elencoFiltri.add((FiltroRicercaForm) elencoFiltriSessione.get(i));
        }
        request.setAttribute("elencoFiltri", elencoFiltri);
      }

      if (StringUtils.isNotEmpty((String) session.getAttribute(
          CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO))) {
        request.setAttribute("isAssociazioneUffIntAbilitata", "true");
      } else {
        request.setAttribute("isAssociazioneUffIntAbilitata", "false");
      }
      
      target = "filtri";
      this.setMenuTab(request, CostantiGenRicerche.TAB_FILTRI);

      // set nel request della listaValoriTabellato per lasciare alla pagina JSP
      // la conversione di tipoTabellato in codiceTabellato
      List<Tabellato> listaValoriTabellati = this.tabellatiManager.getTabellato(
          CostantiGenRicerche.TIPO_VALORE_TABELLATO);
      List<Tabellato> elencoTabellati = this.tabellatiManager.getElencoTabellati(
          CostantiGenRicerche.TIPO_TABELLATO);
      request.setAttribute("listaValoriTabellati", listaValoriTabellati);
      request.setAttribute("elencoTabellati", elencoTabellati);

    } else if (CODICE_TAB_ORDINAMENTI.equals(codiceTab)) {
      target = "ordinamenti";
      this.setMenuTab(request, CostantiGenRicerche.TAB_ORDINAMENTI);
    } else if (CODICE_TAB_LAYOUT.equals(codiceTab)) {
      target = "layout";
      this.setMenuTab(request, CostantiGenRicerche.TAB_LAYOUT);
    } else if (CODICE_TAB_SQL.equals(codiceTab)) {
      String lineSeparator = System.getProperty("line.separator");
      if (StringUtils.isNotEmpty(contenitore.getTestata().getDefSql())) {
        String sql4html = StringUtils.replace(
            new String(StringEscapeUtils.escapeHtml(
                contenitore.getTestata().getDefSql())), lineSeparator, "<br/>");
        request.setAttribute("defSql", sql4html);
      }
      target = "sql";
      this.setMenuTab(request, CostantiGenRicerche.TAB_SQL);
    } else {

      if (codiceTab != null && !CODICE_TAB_DATI_GENERALI.equals(codiceTab)) {
        // si è tentato l'accesso ad un tab non previsto, e quindi si effettua
        // la
        // tracciatura su log e si visualizza la pagina di testata (dati
        // generali)
        // come default
        String messageKey = "info.changeTab.default";
        logger.warn(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }

      target = "datiGenerali";
      this.setMenuTab(request, CostantiGenRicerche.TAB_DATI_GENERALI);

      // set nel request della listaTipoRicerca per lasciare alla pagina JSP la
      // conversione di idTipoRicerca in nomeRicerca
      List<Tabellato> listaTipoRicerca = this.tabellatiManager.getTabellato(TabellatiManager.TIPO_RICERCHE);
      request.setAttribute("listaTipoRicerca", listaTipoRicerca);

      // set nel request della listaFamigliaRicerca per lasciare alla pagina JSP
      // la conversione del codice del tabellato famiglia alla descrizione dello
      // stesso tabellato
      List<Tabellato> listaFamigliaRicerca = this.tabellatiManager.getTabellato(
          TabellatiManager.FAMIGLIA_RICERCA, 1);
      request.setAttribute("listaFamigliaRicerca", listaFamigliaRicerca);
    }

    // set nel request del parameter per disabilitare la navigazione anche in
    // fase di visualizzazione del dato in quanto modificato in sessione solo se
    // non si deve andare alla pagina di errore generale
    if (!CostantiGeneraliStruts.FORWARD_OPZIONE_NON_ABILITATA.equals(target)
        && request.getSession().getAttribute(
            CostantiGenerali.SENTINELLA_OGGETTO_MODIFICATO) != null) {
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);
    }
    if (!CostantiGeneraliStruts.FORWARD_OPZIONE_NON_ABILITATA.equals(target) && 
       !contenitore.isStatoReportNelProfiloAttivo()) {
      this.aggiungiMessaggio(request,
          "warnings.genric.caricaRicerca.reportModificatoDaProfilo");
    }

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }
    return mapping.findForward(target);

  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * dei tab in fase di editing del dettaglio della lista dei gruppi associati
   * ad una ricerca
   * 
   * @param request
   *        request http
   * @param tabAttivo
   *        tab da rendere attivo
   */
  private void setMenuTab(HttpServletRequest request, String tabAttivo) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);

    boolean isInSessione = true;
    if (obj == null) {
      isInSessione = false;
      obj = new GestioneTab();
    }

    GestioneTab gestoreTab = (GestioneTab) obj;
    gestoreTab.setTabAttivo(tabAttivo);
    gestoreTab.setTabSelezionabili(new String[] {
        CostantiGenRicerche.TAB_DATI_GENERALI, CostantiGenRicerche.TAB_GRUPPI,
        CostantiGenRicerche.TAB_ARGOMENTI, CostantiGenRicerche.TAB_CAMPI,
        CostantiGenRicerche.TAB_JOIN, CostantiGenRicerche.TAB_SQL,
        CostantiGenRicerche.TAB_PARAMETRI, CostantiGenRicerche.TAB_FILTRI,
        CostantiGenRicerche.TAB_ORDINAMENTI, CostantiGenRicerche.TAB_LAYOUT });

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }

}