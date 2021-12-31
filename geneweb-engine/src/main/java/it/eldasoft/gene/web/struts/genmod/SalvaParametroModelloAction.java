/*
 * Created on 29-nov-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.db.domain.genric.ParametroRicerca;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Classe che gestisce l'aggiornamento nel database dei parametri
 * 
 * @author Stefano.Sabbadin
 */
public class SalvaParametroModelloAction extends
    AbstractDispatchActionBaseGenModelli {

  private static final String ERROR_VIOLAZIONE_UNIQUE_NOME      = "errorUnique";

  //private static final String ERROR_VIOLAZIONE_CODICE_TABELLATO = "errorTabellato";

  /* logger della classe */
  static Logger               logger                            = Logger.getLogger(SalvaParametroModelloAction.class);

  /** Manager dei modelli */
  private ModelliManager      modelliManager;

  /** Manager dei tabellati */
  private TabellatiManager    tabellatiManager;
  
  /** Manager dei tabellati */
  private RicercheManager    ricercheManager;

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
   * @param ricercheManager ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * insertParametro
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniInsertParametro() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  /**
   * Inserisce nel database il parametro attribuito al modello
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward insertParametro(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("insertParametro: inizio metodo");
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    ParametroModelloForm parametroModelloForm = (ParametroModelloForm) form;

    // Setto l'identificativo del modello
    request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
        new Integer(parametroModelloForm.getIdModello()));

    try {
      if ((!parametroModelloForm.getTipo().equalsIgnoreCase("U"))
          || ("U".equalsIgnoreCase(parametroModelloForm.getTipo()) && checkTipoParametriEsistenti(parametroModelloForm.getIdModello()))) {
        
        parametroModelloForm.setCodice(parametroModelloForm.getCodice().toUpperCase());
        
        String targetCheck = checkUnivocitaConParametriRicercaSorgente(request,
            parametroModelloForm);

        if (targetCheck == null) {
          this.modelliManager.insertParametro(parametroModelloForm.getDatiPerModel());
          this.setMenuTab(request, true);
        } else
          target = targetCheck;
        
      } else {
        // Nella lista esiste gia' un parametro di tipo 'U' e non si può
        // inserirne un altro. Si ritorna alla pagina di insert parametro con
        // un apposito messaggio
        target = ERROR_VIOLAZIONE_UNIQUE_NOME;
        messageKey = "errors.modelli.salva.parametro.unicoParametroTipoU";
        logger.error(this.resBundleGenerale.getString(messageKey), null);
        this.aggiungiMessaggio(request, messageKey);

        this.setAttributiPaginaDiEdit(request, parametroModelloForm);
      }
    } catch (DataIntegrityViolationException e) {
      target = ERROR_VIOLAZIONE_UNIQUE_NOME;
      messageKey = "errors.modelli.salva.parametro.vincoloUnique";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);

      this.setAttributiPaginaDiEdit(request, parametroModelloForm);

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

    if (logger.isDebugEnabled()) logger.debug("insertParametro: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Verifica se il modello è collegato ad un report sorgente dati, e quindi
   * confronta il codice del parametro inserito per il modello con quelli del
   * report sorgente, in modo da evitare conflitti causati da parametri di
   * report e modello con lo stesso codice
   * 
   * @param request
   *        request HTTP
   * @param target
   *        target Struts
   * @param parametroModelloForm form con i dati del parametro 
   * @return target eventualmente modificato, null se i controlli sono andati a
   *         buon fine
   */
  private String checkUnivocitaConParametriRicercaSorgente(
      HttpServletRequest request, 
      ParametroModelloForm parametroModelloForm) {
    String target = null;
    DatiModello modello = this.modelliManager.getModelloById(parametroModelloForm.getIdModello());
    if (modello.getIdRicercaSrc() != null) {
      List listaParametriRicerca = this.ricercheManager.getParametriRicerca(modello.getIdRicercaSrc().intValue());
      if (listaParametriRicerca != null) {
        ParametroRicerca paramRicerca = null;
        for (int i = 0; i < listaParametriRicerca.size(); i++) {
          paramRicerca = (ParametroRicerca)listaParametriRicerca.get(i);
          if (parametroModelloForm.getCodice().equals(paramRicerca.getCodice())) {
            target = ERROR_VIOLAZIONE_UNIQUE_NOME;
            String messageKey = "errors.modelli.salva.parametro.conflittoParametriReport";
            logger.error(this.resBundleGenerale.getString(messageKey), null);
            this.aggiungiMessaggio(request, messageKey);
            this.setAttributiPaginaDiEdit(request, parametroModelloForm);
            break;
          }
        }
      }
    }
    return target;
  }

  /**
   * Metodo per rimettere nel request tutti gli attributi necessari per riaprire
   * la pagina di inserimento nuovo parametro
   * 
   * @param request
   * @param parametroModelloForm
   */
  private void setAttributiPaginaDiEdit(HttpServletRequest request,
      ParametroModelloForm parametroModelloForm) {

    request.setAttribute(
        CostantiGenModelli.ATTRIBUTO_REQUEST_PARAMETRO_MODELLO,
        parametroModelloForm);

    DatiModello modello = modelliManager.getModelloById(parametroModelloForm.getIdModello());
    ParametriModelliAction.popolaTabellatiPagina(request, modello, this.tabellatiManager);
    
    // set nel request del parameter per disabilitare la navigazione in fase
    // di editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);
    // Setto i dati per la gestione dei tab
    this.setMenuTab(request, false);
  }

  /**
   * Metodo che verifica l'esistenza del tabellato collegato al codice inserito
   * 
   * @param codice
   * @return
   */
//  private boolean checkEsistenzaTabellato(String codice) {
//    boolean esiste = false;
//    if (tabellatiManager.getTabellato(codice).size() != 0) esiste = true;
//    return esiste;
//  }

  /**
   * Metodo per determinare se nessuno fra i parametri esistenti del modello in
   * analisi e' di tipo 'U' (Identificativo Utente).
   * 
   * @param idModello
   *        id del modello in analisi
   * @return Ritorna true se nessuno dei parametri esistenti e' di tipo 'U',
   *         false altrimenti
   */
  private boolean checkTipoParametriEsistenti(int idModello) {
    boolean result = true;
    List listaParametriModello = this.modelliManager.getParametriModello(idModello);
    if (listaParametriModello != null && listaParametriModello.size() > 0) {
      Iterator iter = listaParametriModello.iterator();
      while (iter.hasNext() && result) {
        ParametroModello parametroModello = (ParametroModello) iter.next();
        if ("U".equalsIgnoreCase(parametroModello.getTipo())) result = false;
      }
    }
    return result;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * updateParametro
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniUpdateParametro() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  /**
   * Aggiorna nel database il parametro in input
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward updateParametro(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("updateParametro: inizio metodo");
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    ParametroModelloForm parametroModelloForm = (ParametroModelloForm) form;

    // Setto l'identificativo del modello
    request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
        new Integer(parametroModelloForm.getIdModello()));

    try {
      if ((!parametroModelloForm.getTipo().equalsIgnoreCase("U"))
          || ("U".equalsIgnoreCase(parametroModelloForm.getTipo()) && checkTipoParametriEsistenti(parametroModelloForm.getIdModello()))) {

        parametroModelloForm.setCodice(parametroModelloForm.getCodice().toUpperCase());

        String targetCheck = checkUnivocitaConParametriRicercaSorgente(request,
            parametroModelloForm);

        if (targetCheck == null) {
          this.modelliManager.updateParametro(parametroModelloForm.getDatiPerModel());
          this.setMenuTab(request, true);
        } else
          target = targetCheck;

        this.setMenuTab(request, true);
      } else {
        // Nella lista esiste gia' un parametro di tipo 'U' e non si può
        // inserirne un altro. Si ritorna alla pagina di insert parametro con
        // un apposito messaggio
        target = ERROR_VIOLAZIONE_UNIQUE_NOME;
        messageKey = "errors.modelli.salva.parametro.unicoParametroTipoU";
        logger.error(this.resBundleGenerale.getString(messageKey), null);
        this.aggiungiMessaggio(request, messageKey);

        this.setAttributiPaginaDiEdit(request, parametroModelloForm);
      }
    } catch (DataIntegrityViolationException e) {
      target = ERROR_VIOLAZIONE_UNIQUE_NOME;
      messageKey = "errors.modelli.salva.parametro.vincoloUnique";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);

      this.setAttributiPaginaDiEdit(request, parametroModelloForm);

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

    if (logger.isDebugEnabled()) logger.debug("updateParametro: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che inizializza nel request la gestione dei tab
   * 
   * @param request
   */
  public void setMenuTab(HttpServletRequest request,
      boolean abilitaTabSelezionabili) {
    GestioneTab gestoreTab = (GestioneTab) request.getAttribute(CostantiGenModelli.NOME_GESTORE_TAB);

    if (gestoreTab == null) {
      gestoreTab = new GestioneTab();
      request.setAttribute(CostantiGenModelli.NOME_GESTORE_TAB, gestoreTab);
    }

    gestoreTab.setTabAttivo(CostantiGenModelli.TAB_PARAMETRI);
    if (abilitaTabSelezionabili)
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiGenModelli.TAB_DETTAGLIO, CostantiGenModelli.TAB_GRUPPI });
  }

}
