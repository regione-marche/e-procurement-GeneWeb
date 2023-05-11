/*
 * Created on 30-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

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
 * DispatchAction per la creazione e l'eliminazione di una ricerca
 * 
 * @author Francesco.DeFilippis
 */
public class CreaEliminaRicercaAction extends
    AbstractDispatchActionBaseGenRicerche {

  // Forward predefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private static final String TORNA_A_LISTA                     = "lista";
  private static final String SUCCESS_CREA_REPORT_BASE          = "successCreaReportBase";
  private static final String SUCCESS_CREA_REPORT_AVANZATO      = "successCrea";
  private static final String SUCCESS_CREA_REPORT_CON_PROSPETTO = "successCreaProspetto";
  private static final String SUCCESS_CREA_REPORT_SQL           = "successCreaReportSql";

  /** Logger Log4J di classe */
  static Logger               logger                            = Logger.getLogger(CreaEliminaRicercaAction.class);

  /**
   * Reference alla classe di business logic per accesso ai dati relativi alle
   * ricerche
   */
  private RicercheManager     ricercheManager;
  
  /**
   * Reference alla clase di business logic per l'accesso ai dati relativi ai
   * modelli, o meglio, ai report con modello
   * 
   * @since 1.5.0
   */
  private ModelliManager      modelliManager;

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * @param modelliManager modelliManager da settare internamente alla classe.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action elimina
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniElimina() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }

  public ActionForward elimina(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("elimina: inizio metodo");

    // F.D. 11/04/08 Il codice della deleteReport è generalizzato per poterlo
    // utilizzare anche nell'eliminazione dei report dalla lista di quelli
    // esportabili

    // target di default, da modificare nel momento in cui si verificano degli
    // errori
    String target = CreaEliminaRicercaAction.TORNA_A_LISTA;
    target = this.eliminaReport(request, target);

    if (logger.isDebugEnabled()) logger.debug("elimina: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Metodo che effettua l'eliminazione di un report e restituisce il forward
   * che in caso di errore sarà cambiato mentre in caso di success sarà quello
   * passato come parametro
   * 
   * @param request
   * @param target
   * @return
   */
  private String eliminaReport(HttpServletRequest request, String target) {
    String messageKey = null;

    try {
      int idRicerca = Integer.parseInt(request.getParameter("idRicerca"));
      if (this.modelliManager.getNumeroModelliCollegatiASorgenteReport(idRicerca).intValue() == 0)
        this.ricercheManager.deleteRicerca(idRicerca);
      else {
        target = CreaEliminaRicercaAction.TORNA_A_LISTA;
        messageKey = "errors.genRic.eliminaRicerca.referenzeComeSorgente";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }

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
    return target;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action crea
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniCrea() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }

  public ActionForward crea(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("crea: inizio metodo");
    }

    HttpSession session = request.getSession();

    // cancellazione di oggetti in sessione precedentemente creati
    this.cleanSession(request);

    // L.G. 27/02/2007: inizio modifica per creazione di un nuovo report
    // Ora è possibile creare 3 diversi tipi di report e quindi la creazione è
    // funzione del tipo di famiglia

    // Lettura dal request del parametro per il tipo di report da creare
    String famigliaRicerca = request.getParameter("famiglia");

    String target = null;

    // Set del target di in funzione della famiglia di appartenenza del report
    // da creare
    if (famigliaRicerca != null && famigliaRicerca.length() == 1) 
      switch (Integer.parseInt(famigliaRicerca)) {
      case CostantiGenRicerche.REPORT_BASE:
        target = CreaEliminaRicercaAction.SUCCESS_CREA_REPORT_BASE;
        break;
      case CostantiGenRicerche.REPORT_AVANZATO:
        target = CreaEliminaRicercaAction.SUCCESS_CREA_REPORT_AVANZATO;
        break;
      case CostantiGenRicerche.REPORT_PROSPETTO:
        target = CreaEliminaRicercaAction.SUCCESS_CREA_REPORT_CON_PROSPETTO;
        break;
      case CostantiGenRicerche.REPORT_SQL:
        target = CreaEliminaRicercaAction.SUCCESS_CREA_REPORT_SQL;
      }

    // L.G. 07/03/2007: modifica per implementazione ricerca con modello
    // Se si crea una ricerca di base o una ricerca avanzata, allora si crea in
    // sessione l'oggetto ContenitoreDatiRicercaForm, altrimenti si passa alla
    // Action predisposta alla inizializzazione della pagina di creazione di una
    // ricerca con modello
    if (Integer.parseInt(famigliaRicerca) != CostantiGenRicerche.REPORT_PROSPETTO) {
      ContenitoreDatiRicercaForm contenitore = new ContenitoreDatiRicercaForm();
      contenitore.getTestata().setDisp(true);
      // la paginazione di default è quella con il maggior numero di righe,
      // ovvero 100
      contenitore.getTestata().setRisPerPag(
          CostantiGenerali.CBX_RIS_PER_PAGINA[CostantiGenerali.CBX_RIS_PER_PAGINA.length - 1]);
      contenitore.getTestata().setOwner(
          new Integer(
              ((ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId()));
      contenitore.getTestata().setFamiglia(new Integer(famigliaRicerca));
      contenitore.getTestata().setIdProspetto(null);
      contenitore.getTestata().setPersonale(true);
      contenitore.getTestata().setProfiloOwner(
          (String) request.getSession().getAttribute(
              CostantiGenerali.PROFILO_ATTIVO));
      contenitore.getTestata().setVisParametri(Boolean.FALSE);
      contenitore.getTestata().setLinkScheda(Boolean.FALSE);

      session.setAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO, contenitore);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("crea: fine metodo");
    }
    return mapping.findForward(target);
  }

}