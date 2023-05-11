/*
 * Created on 29-mar-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.base;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.ListaForm;
import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicercheBase;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.cache.DizionarioLivelli;
import it.eldasoft.utils.sql.comp.SqlElementoCondizione;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * DispatchAction per la gestione di tutte le azioni che possono essere lanciate
 * dalla pagina 'Lista Filtri' per una ricerca base
 * 
 * @author Luca.Giacomazzo
 */

public class ListaFiltriRicercaBaseAction extends
    AbstractDispatchActionBaseGenRicercheBase {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ListaFiltriRicercaBaseAction.class);

  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("modifica: inizio metodo");

    // target di default
    String target = "apriModifica";

    String id = request.getParameter("id");

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    FiltroRicercaForm filtro = null;
    if (id != null)
      filtro = (FiltroRicercaForm) contenitore.estraiFiltro(Integer.parseInt(id));
    else
      filtro = (FiltroRicercaForm) request.getAttribute("filtroRicercaForm");

    Vector<TabellaRicercaForm> elencoTabelle = contenitore.getElencoArgomenti();
    Vector<CampoRicercaForm> elencoCampi = contenitore.getElencoCampi();
    Vector<String> elencoTabellatiCampi = new Vector<String>(elencoCampi.size());
    DizionarioCampi dizCampi = DizionarioCampi.getInstance();
    // gli argomenti sono composti da una sola vista quindi basta una unica lista dei tabellati  
    for (int i = 0; i < elencoCampi.size(); i++) {
      String mnemonicoCampo = (String) ((CampoRicercaForm)elencoCampi.get(i)).getMnemonicoCampo();
      Campo campo = dizCampi.get(mnemonicoCampo);
      elencoTabellatiCampi.addElement(campo.getCodiceTabellato());
    }

    if (elencoTabelle.size() > 0 && elencoCampi.size() > 0) {
      request.setAttribute("elencoTabelle", elencoTabelle);
      request.setAttribute("elencoCampi", elencoCampi);
      request.setAttribute("elencoTabellatiCampi", elencoTabellatiCampi);
      request.setAttribute("elencoOperatori",
          CostantiGenRicerche.CBX_OPERATORI_VALUE_REPORT_BASE);
      request.setAttribute("elencoOperatoriLabel",
          CostantiGenRicerche.CBX_OPERATORI_LABEL_REPORT_BASE);

      if (filtro != null) {
        request.setAttribute("filtroRicercaForm", filtro);
      }
    }
    // set nel request del parameter per disabilitare la navigazione in fase di
    // editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);
    request.setAttribute("tab", CambiaTabRicercaBaseAction.CODICE_TAB_FILTRI);

    if (logger.isDebugEnabled()) logger.debug("modifica: fine metodo");
    return mapping.findForward(target);
  }

  public CheckOpzioniUtente getOpzioniElimina() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  public ActionForward elimina(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("elimina: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String idFiltro = request.getParameter("id");
    this.eliminaFiltro(contenitore, idFiltro);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    request.setAttribute("tab", CambiaTabRicercaBaseAction.CODICE_TAB_FILTRI);

    if (logger.isDebugEnabled()) logger.debug("elimina: fine metodo");

    return mapping.findForward(target);
  }

  private void eliminaFiltro(ContenitoreDatiRicercaForm contenitore,
      String idFiltro) {
    int id = Integer.parseInt(idFiltro);
    int numeroFiltri = contenitore.getNumeroFiltri();

    if (id == 0) {
      // Cancellazione del primo filtro dalla lista
      contenitore.eliminaFiltro(id);
      if (numeroFiltri > 1)
      // Cancellazione dell'operatore di AND successivo
        contenitore.eliminaFiltro(id);
    } else if (id == (numeroFiltri - 1)) {
      // Cancellazione dell'ultimo filtro dalla lista
      contenitore.eliminaFiltro(id);
      // Cancellazione dell'operatore di AND precedente
      contenitore.eliminaFiltro(id - 1);
    } else {
      // Cancellazione dalla lista del filtro indicato con l'id
      contenitore.eliminaFiltro(id);
      // Cancellazione dell'operatore di AND successivo
      contenitore.eliminaFiltro(id);
    }
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * eliminaMultiplo
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEliminaMultiplo() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  public ActionForward eliminaMultiplo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("eliminaMultiplo: inizio metodo");

    // target di default, da modificare nel momento in cui si verificano degli
    // errori
    String target = CostantiGeneraliStruts.FORWARD_OK;

    ListaForm filtri = (ListaForm) form;

    // costruzione dell'elenco degli id delle ricerche da rimuovere
    String id[] = filtri.getId();

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    for (int i = id.length-1; i >= 0; i--)
      this.eliminaFiltro(contenitore, id[i]);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    request.setAttribute("tab", CambiaTabRicercaBaseAction.CODICE_TAB_FILTRI);
    
    if (logger.isDebugEnabled()) logger.debug("eliminaMultiplo: fine metodo");

    return mapping.findForward(target);
  }

  public ActionForward spostaSu(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    int id = Integer.parseInt(request.getParameter("id"));
    contenitore.spostaFiltro(id, id - 2);

    this.verificaListaFiltri(contenitore);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    request.setAttribute("tab", CambiaTabRicercaBaseAction.CODICE_TAB_FILTRI);
    return mapping.findForward(target);
  }

  public ActionForward spostaGiu(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    int id = Integer.parseInt(request.getParameter("id"));
    contenitore.spostaFiltro(id, id + 2);

    this.verificaListaFiltri(contenitore);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    request.setAttribute("tab", CambiaTabRicercaBaseAction.CODICE_TAB_FILTRI);
    return mapping.findForward(target);
  }

  public ActionForward spostaInPosizioneMarcata(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("spostaInPosizioneMarcata: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String id1 = request.getParameter("id");
    String id2 = request.getParameter("idNew");
    contenitore.spostaFiltro(Integer.parseInt(id1), Integer.parseInt(id2));

    this.verificaListaFiltri(contenitore);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    request.setAttribute("tab", CambiaTabRicercaBaseAction.CODICE_TAB_FILTRI);
    if (logger.isDebugEnabled())
      logger.debug("spostaInPosizioneMarcata: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Metodo che dopo le operazioni di spostamento controlla la successione dei
   * filtri sia sempre: filtro, AND, filtro, AND, .... , filtro, AND.
   * L'algoritmo di seguito implementato controlla che nelle posizioni con
   * indici dispari dell'elenco filtri sia un operatore AND. Se così non è
   * allora in tale posizione inserisce tale operatore
   * 
   * @param elencoFiltri
   */
  private void verificaListaFiltri(ContenitoreDatiRicercaForm contenitore) {
    Vector<FiltroRicercaForm> elencoFiltri = contenitore.getElencoFiltri();

    if (elencoFiltri.size() > 1) {
      FiltroRicercaForm filtroRicerca = null;
      FiltroRicercaForm filtroPrecedente = null;
      FiltroRicercaForm filtroSuccessivo = null;

      filtroRicerca = (FiltroRicercaForm) elencoFiltri.get(0);
      // Se il primo elemento nell'elenco filtri e' un operatore AND lo rimuovo
      if (SqlElementoCondizione.STR_OPERATORE_LOGICO_AND.equals(filtroRicerca.getOperatore()))
        elencoFiltri.remove(0);

      filtroRicerca = (FiltroRicercaForm) elencoFiltri.get(elencoFiltri.size() - 1);
      // Se l'ultimo elemento nell'elenco filtri e' un operatore AND lo rimuovo
      if (SqlElementoCondizione.STR_OPERATORE_LOGICO_AND.equals(filtroRicerca.getOperatore()))
        elencoFiltri.remove(elencoFiltri.size() - 1);

      int i = 1;
      while (i < elencoFiltri.size() - 1) {
        filtroRicerca = (FiltroRicercaForm) elencoFiltri.get(i);
        filtroPrecedente = (FiltroRicercaForm) elencoFiltri.get(i - 1);
        filtroSuccessivo = (FiltroRicercaForm) elencoFiltri.get(i + 1);

        if (SqlElementoCondizione.STR_OPERATORE_LOGICO_AND.equals(filtroRicerca.getOperatore())) {
          if (SqlElementoCondizione.STR_OPERATORE_LOGICO_AND.equals(filtroPrecedente.getOperatore()))
            elencoFiltri.remove(i);
          else if (SqlElementoCondizione.STR_OPERATORE_LOGICO_AND.equals(filtroSuccessivo.getOperatore()))
            elencoFiltri.remove(i);
        } else {
          if (!SqlElementoCondizione.STR_OPERATORE_LOGICO_AND.equals(filtroPrecedente.getOperatore())) {
            FiltroRicercaForm filtroTmp = new FiltroRicercaForm();
            filtroTmp.setOperatore(SqlElementoCondizione.STR_OPERATORE_LOGICO_AND);
            elencoFiltri.addElement(filtroTmp);
            contenitore.spostaFiltro(elencoFiltri.size() - 1, i);

          } else if (!SqlElementoCondizione.STR_OPERATORE_LOGICO_AND.equals(filtroSuccessivo.getOperatore())) {
            FiltroRicercaForm filtroTmp = new FiltroRicercaForm();
            filtroTmp.setOperatore(SqlElementoCondizione.STR_OPERATORE_LOGICO_AND);
            elencoFiltri.addElement(filtroTmp);
            contenitore.spostaFiltro(elencoFiltri.size() - 1, i + 1);
          }
        }
        i++;
      }
    }
  }

  public ActionForward filtroPerIdUtente(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("filtroPerIdUtente: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    DatiGenRicerca testata = contenitore.getTestata().getDatiPerModel();

    if (testata.getFiltroUtente() == 1) {
      // In questo caso si disattiva il filtro per id Utente precedentemente
      // attivato, quindi non è necessario effettuare alcun controllo
      contenitore.getTestata().setFiltroUtente(false);
      this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    } else {
      // In questo caso si attiva il filtro per id utente: si controlla se nella
      // lista della tabelle selezionate per la ricerca ne esiste almeno una che
      // e' in relazione con l'id utente. Se si, allora setto l'attributo della
      // testata filtroIdUtente a true, altrimenti invio al client un messaggio
      // che spiega l'impossibilità di applicare tale filtro
      DizionarioLivelli dizLivelli = DizionarioLivelli.getInstance();
      boolean esisteTabellaInRelazioneIdUtente = false;
      for (int i = 0; i < contenitore.getNumeroTabelle(); i++)
        if (dizLivelli.isFiltroLivelloPresente(contenitore.estraiTabella(i).getNomeTabella()))
          esisteTabellaInRelazioneIdUtente = true;
      if (esisteTabellaInRelazioneIdUtente) {
        contenitore.getTestata().setFiltroUtente(true);
        this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
      } else {
        String messageKey = "warnings.genRic.base.filtroIdUtente.nonAttivabile";
        if (logger.isDebugEnabled()) logger.debug(messageKey);
        this.aggiungiMessaggio(request, messageKey);
      }
    }

    request.setAttribute("tab", CambiaTabRicercaBaseAction.CODICE_TAB_FILTRI);

    if (logger.isDebugEnabled())
      logger.debug("filtroPerIdUtente: fine metodo");

    return mapping.findForward(target);
  }

  public ActionForward filtroPerUfficioIntestatario(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("filtroPerUfficioIntestatario: inizio metodo");
    }
    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
        request.getSession().getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    DatiGenRicerca testata = contenitore.getTestata().getDatiPerModel();

    //if (testata.getFiltroUfficioIntestatarioEscluso() == 0) {
    if (testata.getFiltroUfficioIntestatario() == 1) {
      // In questo caso si esclude il filtro per ufficio intestatario
      // precedentemente attivato, quindi non è necessario effettuare alcun controllo
      //contenitore.getTestata().setFiltroUfficioIntestatarioEscluso(true);
      contenitore.getTestata().setFiltroUfficioIntestatario(false);
      this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    } else {
      // In questo caso si attiva il filtro per ufficio intestatario: si controlla
      // se nella lista della tabelle selezionate per la ricerca e visibili ne
      // esiste almeno una che e' in relazione con la tabella UFFINT. Se si,
      // allora setto l'attributo della testata filtroUfficioIntestatario a false,
      // altrimenti invio al client un messaggio che spiega l'impossibilita'
      // di applicare tale filtro.
      
      if (contenitore.getElencoArgomentiVisibili().size() > 0) {
        DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
        Vector<TabellaRicercaForm> elencoTabelle = contenitore.getElencoArgomentiVisibili();
        boolean esisteTabellaInRelazioneUffInt = false;

        for (int i = 0; i < contenitore.getNumeroTabelle() && !esisteTabellaInRelazioneUffInt; i++) {
          TabellaRicercaForm tabellaRic = elencoTabelle.get(i);
          if (dizTabelle.getDaNomeTabella("UFFINT").getLegameTabelle(tabellaRic.getNomeTabella()).length > 0) {
            esisteTabellaInRelazioneUffInt = true;
          }
        }
        if (esisteTabellaInRelazioneUffInt) {
          //contenitore.getTestata().setFiltroUfficioIntestatarioEscluso(false);
          contenitore.getTestata().setFiltroUfficioIntestatario(true);
          this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
        } else {
          String messageKey = "warnings.genRic.filtroUfficioIntestatario.nonAttivabile";
          String labelUffInt = StringUtils.lowerCase(
              this.resBundleGenerale.getString("label.tags.uffint.singolo"));
          
          if (logger.isDebugEnabled()) {
            logger.debug(UtilityStringhe.replaceParametriMessageBundle(
                this.resBundleGenerale.getString(messageKey),
                new String[] { labelUffInt }));
          }
          this.aggiungiMessaggio(request, messageKey, labelUffInt);
        }
      } else {
        String messageKey = null;
        if (contenitore.getNumeroTabelle() == 0) {
          messageKey = "errors.genRic.noArgDefFiltri";
        } else {
          messageKey = "errors.genRic.noArgVisFiltri";
        }
        this.aggiungiMessaggio(request, messageKey);
        
        if (logger.isDebugEnabled()) {
          logger.debug(this.resBundleGenerale.getString(messageKey));
        }
      }
    }

    if (StringUtils.isNotEmpty((String) request.getSession().getAttribute(
        CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO))) {
      request.setAttribute("isAssociazioneUffIntAbilitata", "true");
    } else {
      request.setAttribute("isAssociazioneUffIntAbilitata", "false");
    }
    
    request.setAttribute("tab", CambiaTabRicercaBaseAction.CODICE_TAB_FILTRI);

    if (logger.isDebugEnabled()) {
      logger.debug("filtroPerUfficioIntestatario: fine metodo");
    }
    return mapping.findForward(target);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaSu
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaSu() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaGiu
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaGiu() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * spostaInPosizioneMarcata
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaInPosizioneMarcata() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * filtroPerIdUtente
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniFiltroPerIdUtente() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * filtroPerUfficioIntestatario
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniFiltroPerUfficioIntestatario() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
}