/*
 * Created on 30-nov-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Classe per la gestione delle azioni a livello generale su una lista di
 * parametri. L'input è sempre l'identificativo del modello, e se si agisce su
 * un parametro specifico il progressivo del parametro
 * 
 * @author Stefano.Sabbadin
 */
public class ParametriModelliAction extends
    AbstractDispatchActionBaseGenModelli {

  /**
   * di default in creazione viene proposto come codice "MOD_INPUTxx" dove xx è
   * un intero che indica il numero d'ordine del nuovo parametro inserito
   */
  private static final String CODICE_DEFAULT = "MOD_INPUT";

  /**
   * di default in creazione viene proposto come descrizione per l'inserimento
   * "Inserire PARAM-MODxx" dove xx è un intero che indica il numero d'ordine
   * del nuovo parametro inserito
   */
  private static final String NOME_DEFAULT   = "Inserire MOD_INPUT";

  /**
   * i default in creazione viene proposto il tipo stringa
   */
  private static final String TIPO_DEFAULT   = "S";

  /*
   * logger della classe
   */
  static Logger               logger         = Logger.getLogger(ParametriModelliAction.class);

  /** Manager dei modelli */
  protected ModelliManager    modelliManager;

  /** Manager dei tabellati */
  protected TabellatiManager  tabellatiManager;

  /**
   * @param modelliManager
   *        The modelliManager to set.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * @param tabellatiManager
   *        The tabellatiManager to set.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * listaParametriModello
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniListaParametriModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  /**
   * Azione per la visualizzazione della lista dei parametri
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward listaParametriModello(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("listaParametriModello: Inizio metodo");

    int idModello = -1;
    String messageKey = null;
    // Di default setto la visualizzazione della lista dei gruppi
    String target = CostantiGenModelli.FORWARD_OK_LISTA_PARAMETRI;
    try {
      idModello = Integer.parseInt(request.getParameter("idModello"));
      // Settaggio dell'identificativo del modello
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idModello));

      List elencoParametri = this.modelliManager.getParametriModello(idModello);
      ParametroModello parametro = null;
      ParametroModelloForm parametroForm = null;
      Vector elencoParametriForm = new Vector();
      if (elencoParametri != null) {
        for (int i = 0; i < elencoParametri.size(); i++) {
          parametro = (ParametroModello) elencoParametri.get(i);
          parametroForm = new ParametroModelloForm(parametro);
          parametroForm.setDescrizioneTipo(this.tabellatiManager.getDescrTabellato(
              CostantiGenModelli.TABELLATO_TIPO_PARAMETRO_MODELLO,
              parametro.getTipo()));
          elencoParametriForm.addElement(parametroForm);
        }
      }
      // Setto i dati dell'elenco dei parametri definiti per il modello
      request.setAttribute("listaParametriModello", elencoParametriForm);

      // Setto i dati per la gestione dei tab
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
    if (logger.isDebugEnabled())
      logger.debug("listaParametriModello: Fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * creaParametroModello
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniCreaParametroModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  public ActionForward creaParametroModello(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGenModelli.FORWARD_OK_EDIT_PARAMETRO_MODELLO;
    if (logger.isDebugEnabled())
      logger.debug("creaParametroModello: inizio metodo");
    int idModello = -1;
    String messageKey = null;
    try {

      idModello = Integer.parseInt(request.getParameter("idModello"));

      // Setto l'identificativo del modello
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idModello));

      // Carico l'entita principale del modello
      DatiModello modello = modelliManager.getModelloById(idModello);

      // Salvo nel request un oggetto vuoto parametro
      ParametroModelloForm parametro = new ParametroModelloForm();
      parametro.setIdModello(idModello);
      parametro.setMetodo(CostantiGenModelli.METODO_INSERT_PARAMETRO);
      int progressivo = 1 + this.modelliManager.getNuovoProgressivoParametroModello(idModello);
      parametro.setCodice(ParametriModelliAction.CODICE_DEFAULT + progressivo);
      parametro.setNome(ParametriModelliAction.NOME_DEFAULT + progressivo);
      parametro.setTipo(ParametriModelliAction.TIPO_DEFAULT);
      request.setAttribute(
          CostantiGenModelli.ATTRIBUTO_REQUEST_PARAMETRO_MODELLO, parametro);

      ParametriModelliAction.popolaTabellatiPagina(request, modello, this.tabellatiManager);        

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // Setto i dati per la gestione dei tab
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

    if (logger.isDebugEnabled())
      logger.debug("creaParametroModello: fine metodo");
    return mapping.findForward(target);

  }

  /**
   * Popola il tabellato con i tipi di parametri e l'elenco dei tabellati
   * collegati all'entità principale del modello. Nel caso di modello utilizzato
   * come output di una ricerca si elimina la tipologia di parametro "Tabellato"
   * e non si popola l'elenco dei campi tabellati in quanto non esiste un'entità
   * principale del modello
   * 
   * @param request
   *        request HTTP in cui settare le liste di tabellati
   * @param modello
   *        modello sul quale gestire i parametri
   */
  public static void popolaTabellatiPagina(HttpServletRequest request,
      DatiModello modello, TabellatiManager tabellatiManager) {
    // Salvo nel request il tipo di parametro che si può indicare
    List listaValoriTipiParametri = tabellatiManager.getTabellato(CostantiGenModelli.TABELLATO_TIPO_PARAMETRO_MODELLO);
    
    List listaCampiTabellati = null;
    // Sabbadin 15-03-2010
    if (modello.getIdRicercaSrc() == null) {
      // carico la lista dei possibili tabellati associabili ad un parametro in
      // base al profilo
      String codiceProfilo = (String) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_ATTIVO);

      String schemaViste = ConfigManager.getValore(
          CostantiGenerali.PROP_SCHEMA_VISTE_REPORT_BASE);
      
      String entitaPrincipale = modello.getEntPrinc();

      listaCampiTabellati = tabellatiManager.getCampiTabellatiByProfilo(
          codiceProfilo, schemaViste, entitaPrincipale);

    } else {
      modificaTipiParametriPerModelliDiLayout(listaValoriTipiParametri);
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_NO_TABELLATI, "1");
    }
    
    request.setAttribute(CostantiGenModelli.ATTRIBUTO_LISTA_TIPI_PARAMETRI,
        listaValoriTipiParametri);

    request.setAttribute(
        CostantiGenModelli.ATTRIBUTO_LISTA_PARAMETRI_TABELLATI,
        listaCampiTabellati);
  }

  /**
   * Nel caso di report con modello utilizzato come layout di una ricerca base o
   * avanzata, si elimina il tipo di parametro tabellato in quanto non ha senso
   * non avendo l'entità di partenza, ed inoltre non ha senso neppure in quanto
   * non verrà effettuato accesso al db all'interno del testo da comporre
   * 
   * @param listaValoriTipiParametri lista dei parametri tabellati
   */
  private static void modificaTipiParametriPerModelliDiLayout(
      List listaValoriTipiParametri) {
    for (int i = listaValoriTipiParametri.size() - 1; i >= 0; i--) {
      Tabellato tab = (Tabellato) listaValoriTipiParametri.get(i);
      if ("T".equals(tab.getTipoTabellato())) {
        listaValoriTipiParametri.remove(i);
      }
    }
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * modificaParametroModello
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModificaParametroModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  public ActionForward modificaParametroModello(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGenModelli.FORWARD_OK_EDIT_PARAMETRO_MODELLO;
    if (logger.isDebugEnabled())
      logger.debug("modificaParametroModello: inizio metodo");
    int idModello = -1;
    int progressivo = -1;
    String messageKey = null;
    try {

      idModello = Integer.parseInt(request.getParameter("idModello"));
      progressivo = Integer.parseInt(request.getParameter("progressivo"));

      // Setto l'identificativo del modello
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idModello));

      ParametroModello parametro = this.modelliManager.getParametroModello(
          idModello, progressivo);
      ParametroModelloForm parametroModelloForm = new ParametroModelloForm(
          parametro);
      parametroModelloForm.setMetodo(CostantiGenModelli.METODO_UPDATE_PARAMETRO);
      request.setAttribute(
          CostantiGenModelli.ATTRIBUTO_REQUEST_PARAMETRO_MODELLO,
          parametroModelloForm);

      DatiModello modello = modelliManager.getModelloById(idModello);

      ParametriModelliAction.popolaTabellatiPagina(request, modello, this.tabellatiManager);
      
      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // Setto i dati per la gestione dei tab
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

    if (logger.isDebugEnabled())
      logger.debug("modificaParametroModello: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * eliminaParametroModello
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEliminaParametroModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  public ActionForward eliminaParametroModello(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGenModelli.FORWARD_OK_DELETE_PARAMETRO;
    if (logger.isDebugEnabled())
      logger.debug("eliminaParametroModello: inizio metodo");
    int idModello = -1;
    int progressivo = -1;
    String messageKey = null;
    try {
      idModello = Integer.parseInt(request.getParameter("idModello"));
      progressivo = Integer.parseInt(request.getParameter("progressivo"));

      // Setto l'identificativo del modello
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idModello));

      this.modelliManager.deleteParametro(idModello, progressivo);

      // Setto i dati per la gestione dei tab
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

    if (logger.isDebugEnabled())
      logger.debug("eliminaParametroModello: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaSu
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaSu() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  public ActionForward spostaSu(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGenModelli.FORWARD_OK_SPOSTA_PARAMETRO;
    if (logger.isDebugEnabled()) logger.debug("spostaSu: inizio metodo");
    int idModello = -1;
    int progressivo = -1;
    String messageKey = null;
    try {
      idModello = Integer.parseInt(request.getParameter("idModello"));
      progressivo = Integer.parseInt(request.getParameter("progressivo"));

      // Setto l'identificativo del modello
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idModello));

      this.modelliManager.updateSpostaSuParametro(idModello, progressivo);

      // Setto i dati per la gestione dei tab
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

    if (logger.isDebugEnabled()) logger.debug("spostaSu: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaGiù
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaGiu() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  public ActionForward spostaGiu(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGenModelli.FORWARD_OK_SPOSTA_PARAMETRO;
    if (logger.isDebugEnabled()) logger.debug("spostaGiu: inizio metodo");
    int idModello = -1;
    int progressivo = -1;
    String messageKey = null;
    try {
      idModello = Integer.parseInt(request.getParameter("idModello"));
      progressivo = Integer.parseInt(request.getParameter("progressivo"));

      // Setto l'identificativo del modello
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idModello));

      this.modelliManager.updateSpostaGiuParametro(idModello, progressivo);

      // Setto i dati per la gestione dei tab
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

    if (logger.isDebugEnabled()) logger.debug("spostaGiu: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * spostaInPosizioneMarcata
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaInPosizioneMarcata() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  public ActionForward spostaInPosizioneMarcata(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGenModelli.FORWARD_OK_SPOSTA_PARAMETRO;
    if (logger.isDebugEnabled())
      logger.debug("spostaInPosizioneMarcata: inizio metodo");
    int idModello = -1;
    int progressivo = -1;
    int progressivoNew = -1;
    String messageKey = null;
    try {
      idModello = Integer.parseInt(request.getParameter("idModello"));
      progressivo = Integer.parseInt(request.getParameter("progressivo"));
      progressivoNew = Integer.parseInt(request.getParameter("progressivoNew"));

      // Setto l'identificativo del modello
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idModello));

      this.modelliManager.updateSpostaPosizioneMarcataParametro(idModello,
          progressivo, progressivoNew);

      // Setto i dati per la gestione dei tab
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

    if (logger.isDebugEnabled())
      logger.debug("spostaInPosizioneMarcata: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che inizializza nel request la gestione dei tab
   * 
   * @param request
   */
  public void setMenuTab(HttpServletRequest request) {
    GestioneTab gestoreTab = (GestioneTab) request.getAttribute(CostantiGenModelli.NOME_GESTORE_TAB);

    if (gestoreTab == null) {
      gestoreTab = new GestioneTab();
      request.setAttribute(CostantiGenModelli.NOME_GESTORE_TAB, gestoreTab);
    }

    gestoreTab.setTabAttivo(CostantiGenModelli.TAB_PARAMETRI);

    Vector metodi = new Vector();
    metodi.add(CostantiGenModelli.METODO_LISTA_PARAMETRI);
    metodi.add(CostantiGenModelli.METODO_CREA_PARAMETRO);

    int idxMetodo = metodi.indexOf(request.getParameter("metodo"));
    switch (idxMetodo) {
    case 0:
      if (idxMetodo == 0)
        gestoreTab.setTabSelezionabili(new String[] {
            CostantiGenModelli.TAB_DETTAGLIO, CostantiGenModelli.TAB_GRUPPI });
      break;
    }
  }

}
