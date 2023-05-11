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

import it.eldasoft.gene.bl.genric.ProspettoManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.db.domain.genric.DatiGenProspetto;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.gene.web.struts.genmod.ParametriModelliAction;
import it.eldasoft.gene.web.struts.genmod.ParametroModelloForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Classe per la gestione degli eventi della pagina lista parametri per una
 * ricerca con modello. Questa classe e' una estensione della classe
 * ParametriModelliAction che gestisce la pagina lista parametri dei modelli
 * 
 * @author Luca.Giacomazzo
 */
public class ParametriProspettiAction extends ParametriModelliAction {

  static Logger            logger = Logger.getLogger(ParametriProspettiAction.class);

  /** Manager delle ricerche con modello */
  private ProspettoManager prospettoManager;

  /**
   * @param prospettoManager
   *        prospettoManager da settare internamente alla classe.
   */
  public void setProspettoManager(ProspettoManager prospettoManager) {
    this.prospettoManager = prospettoManager;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * parametriModello
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniListaParametriModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
  }

  /**
   * Azione per la visualizzazione della lista dei parametri a partire dall'id
   * della ricerca con modello in analisi. Questo metodo e' stato ridefinito,
   * copiandolo dall'analogo metodo definito nella classe padre, perchè nei tab
   * Dati Generali e Gruppi viene sempre usato idRicerca (oltre ad essere
   * presente in sessione con la chiave CostantiGenerali.ID_OGGETTO_SESSION) e
   * solo nel tab paramentri e' necessario usare l'idModello per accedere ai
   * parametri associati della ricerca con modello che altro non sono che
   * parametri di un modello
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
      logger.debug("listaParametriModello: inizio metodo");

    int idRicerca = -1;
    int idModello = -1;
    String messageKey = null;
    // Di default setto la visualizzazione della lista dei gruppi
    String target = CostantiGenModelli.FORWARD_OK_LISTA_PARAMETRI;
    try {
      // Determino l'idModello associato alla ricerca con modello a partire
      // dall'idProspetto della ricerca in analisi
      String idPros = request.getParameter("idRicerca");
      if (idPros != null) {
        idRicerca = Integer.parseInt(idPros);
      } else {
        // In questo caso si ritorna alla lista dei parametri, dopo aver
        // inserito
        // o modificato un parametro usando la action SalvaParametroModello, la
        // quale non inserisce nel request l'id della ricerca con modello in
        // analisi
        idRicerca = Integer.parseInt((String) request.getSession().getAttribute(
            CostantiGenerali.ID_OGGETTO_SESSION));
      }

      DatiGenProspetto datiGenProspetto = this.prospettoManager.getProspettoById(idRicerca);
      idModello = datiGenProspetto.getDatiModello().getIdModello();
      // Settaggio dell'identificativo del modello
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idModello));

      List<?> elencoParametri = this.modelliManager.getParametriModello(idModello);
      ParametroModello parametro = null;
      ParametroModelloForm parametroForm = null;
      Vector<ParametroModelloForm> elencoParametriForm = new Vector<ParametroModelloForm>();
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
   * Ridefinizione del metodo che inizializza nel request la gestione dei tab.
   * E' stato necessario farlo perche'
   * 
   * @param request
   */
  public void setMenuTab(HttpServletRequest request) {

    GestioneTab gestoreTab = (GestioneTab) request.getAttribute(CostantiGenModelli.NOME_GESTORE_TAB);

    if (gestoreTab == null) {
      gestoreTab = new GestioneTab();
      request.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
    }

    gestoreTab.setTabAttivo(CostantiGenProspetto.TAB_PARAMETRI);

    Vector<String> metodi = new Vector<String>();
    metodi.add(CostantiGenModelli.METODO_LISTA_PARAMETRI);
    metodi.add(CostantiGenModelli.METODO_CREA_PARAMETRO);

    int idxMetodo = metodi.indexOf(request.getParameter("metodo"));
    switch (idxMetodo) {
    case 0:
      if (idxMetodo == 0)
        gestoreTab.setTabSelezionabili(new String[] {
            CostantiGenProspetto.TAB_DATI_GENERALI,
            CostantiGenProspetto.TAB_GRUPPI });
      break;
    }
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * creaParametroModello
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniCreaParametroModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * modificaParametroModello
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModificaParametroModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * eliminaParametroModello
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEliminaParametroModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaSu
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaSu() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaGiù
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaGiu() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * spostaInPosizioneMarcata
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaInPosizioneMarcata() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
  }

}
