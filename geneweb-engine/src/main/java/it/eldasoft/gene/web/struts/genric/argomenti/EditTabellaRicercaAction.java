/*
 * Created on 06-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.argomenti;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.CambiaTabAction;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.gene.web.struts.genric.datigen.SalvaDatiGenRicercaAction;
import it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm;
import it.eldasoft.gene.web.struts.genric.giunzione.GiunzioneRicercaForm;
import it.eldasoft.gene.web.struts.genric.ordinamento.OrdinamentoRicercaForm;
import it.eldasoft.gene.web.struts.genric.parametro.ParametroRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Schema;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.profiles.cache.DizionarioLivelli;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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
 * DispatchAction per la gestione delle operazioni editing, di update, di
 * inserimento di una nuova tabella
 * 
 * @author Luca Giacomazzo
 */
public class EditTabellaRicercaAction extends
    AbstractDispatchActionBaseGenRicerche {

  // Forward della DispatchAction in linea con il file struts-config-genric.xml
  private static final String SUCCESS_MODIFICA_FORWARD    = "successUpdate";
  private static final String ERROR_MODIFICA_FORWARD      = "errorUpdate";
  private static final String SUCCESS_INSERT_FORWARD      = "successAdd";
  private static final String ERROR_INSERT_FORWARD        = "errorAdd";
  private static final String SUCCESS_ELIMINA_FORWARD     = "successDelete";
  private static final String SUCCESS_SETENTPRINC_FORWARD = "successSetEntPrinc";

  /** Logger Log4J di classe */
  static Logger               logger                      = Logger.getLogger(EditTabellaRicercaAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere alla action update
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniUpdate() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Action per l'aggiornamento di un argomento associato ad una ricerca dalla
   * pagina editArgomentoRicerca.jsp
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward update(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("update: inizio metodo");

    // set del target di default
    String target = EditTabellaRicercaAction.SUCCESS_MODIFICA_FORWARD;

    // lettura dell'oggetto TabellaRicercaForm dal request
    TabellaRicercaForm tabellaRicercaForm = (TabellaRicercaForm) form;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    // Caricamento dalla sessione dell'oggetto TabellaRicercaForm da aggiornare
    TabellaRicercaForm tabellaContenitore = contenitore.estraiTabella(tabellaRicercaForm.getProgressivo());

    if (!tabellaRicercaForm.getVisibile() && tabellaContenitore.getVisibile()) {

      // si verifica se e' possibile effettuare l'update: infatti se nel form
      // ricevuto dal client la tabella e' stata definita non visibile mentre
      // prima era
      // visibile, allora si impedisce l'upgrade se la ricerca presenta dei
      // campi, dei
      // filtri o degli ordinamenti che si basano su campi di tale tabella
      String result = this.verificaDipendenzaDatiRicercaDaTabella(
          tabellaContenitore, contenitore);

      if (result != null) {
        logger.debug("result="
            + result
            + "\ntabellaRicercaForm.getAliasTabella()="
            + tabellaContenitore.getAliasTabella());
        // ERRORE: si ricarica la pagina di edit con il relativo messaggio
        // d'errore
        target = EditTabellaRicercaAction.ERROR_MODIFICA_FORWARD;
        String messageKey = result;
        if (logger.isDebugEnabled())
          logger.debug(this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              tabellaContenitore.getAliasTabella()));
        this.aggiungiMessaggio(request, messageKey,
            tabellaContenitore.getAliasTabella());

        request.setAttribute("prog", new Integer(
            tabellaContenitore.getProgressivo()));

        // se si giunge a questa riga, allora si sta per rendere non visibile
        // una
        // tabella visibile non usata nella ricerca
      } else {
        // Update dell'attributo 'visibile' dell'oggetto TabellaRicercaForm in
        // sessione
        tabellaContenitore.setVisibile(tabellaRicercaForm.getVisibile());
        this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
      }
    }

    // set del request del tab da caricare dopo il salvataggio
    request.setAttribute("tab", CambiaTabAction.CODICE_TAB_ARGOMENTI);

    if (logger.isDebugEnabled()) logger.debug("update: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action verificaDipendenzaDatiRicercaDaTabella
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVerificaDipendenzaDatiRicercaDaTabella() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  private String verificaDipendenzaDatiRicercaDaTabella(
      TabellaRicercaForm tabellaRicerca, ContenitoreDatiRicercaForm contenitore) {
    String result = null;
    boolean isTabellaUsata = false;
    Vector<?> elenco = contenitore.getElencoCampi();
    CampoRicercaForm campo = null;
    for (int i = 0; i < elenco.size() && !isTabellaUsata; i++) {
      campo = (CampoRicercaForm) elenco.get(i);
      {
        if (campo.getAliasTabella().equals(tabellaRicerca.getAliasTabella())
            && campo.getMnemonicoTabella().equals(
                tabellaRicerca.getMnemonicoTabella())) isTabellaUsata = true;
        result = "errors.genRic.campoDipendeDaTabella";
      }
    }
    if (result == null) {
      elenco = contenitore.getElencoFiltri();
      FiltroRicercaForm filtro = null;
      for (int i = 0; i < elenco.size() && !isTabellaUsata; i++) {
        filtro = (FiltroRicercaForm) elenco.get(i);
        {
          if ((filtro.getAliasTabella().equals(tabellaRicerca.getAliasTabella()) && filtro.getMnemonicoTabella().equals(
              tabellaRicerca.getMnemonicoTabella()))
              || (filtro.getAliasTabellaConfronto().equals(
                  tabellaRicerca.getAliasTabella()) && filtro.getMnemonicoTabellaConfronto().equals(
                  tabellaRicerca.getMnemonicoTabella())))
            isTabellaUsata = true;
          result = "errors.genRic.filtroDipendeDaTabella";
        }
      }
    }
    if (result == null) {
      elenco = contenitore.getElencoOrdinamenti();
      OrdinamentoRicercaForm ordinamento = null;
      for (int i = 0; i < elenco.size() && !isTabellaUsata; i++) {
        ordinamento = (OrdinamentoRicercaForm) elenco.get(i);
        {
          if (ordinamento.getAliasTabella().equals(
              tabellaRicerca.getAliasTabella())
              && ordinamento.getMnemonicoTabella().equals(
                  tabellaRicerca.getMnemonicoTabella())) isTabellaUsata = true;
          result = "errors.genRic.ordinamentoDipendeDaTabella";
        }
      }
    }
    return result;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action insert
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniInsert() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Action per l'inserimento di una nuova tabella nell'elenco delle tabelle in
   * sessione
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward insert(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("insert: inizio metodo");
    }

    String target = EditTabellaRicercaAction.SUCCESS_INSERT_FORWARD;

    TabellaRicercaForm tabellaRic = (TabellaRicercaForm) form;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
        session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        "profiloUtente");

    try {
      DizionarioSchemi dizSchemi = DizionarioSchemi.getInstance();
      DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();

      // si impostano le informazioni relative alla tabella
      Schema schema = dizSchemi.get(tabellaRic.getMnemonicoSchema());
      tabellaRic.setDescrizioneSchema(schema.getDescrizione());
      tabellaRic.setNomeTabella(dizTabelle.get(tabellaRic.getMnemonicoTabella()).getNomeTabella());

      // Set di aliasTabella pari a nomeTabella per default, seguito da un
      // eventuale progressivo nel caso la tabella sia inserito nella ricerca
      // piu' volte (il progressivo parte da 2 e non da 1)
      Integer progrTabella = contenitore.getMaxProgressivoTabella(tabellaRic);
      if (progrTabella == null) {
        tabellaRic.setAliasTabella(tabellaRic.getNomeTabella());
      } else {
        tabellaRic.setAliasTabella(tabellaRic.getNomeTabella() + progrTabella);
      }
      
      // Impostazione di default per ogni nuovo argomento aggiunto ad una ricerca
      tabellaRic.setVisibile(true);

      // si impostano le informazioni relative alle giunzioni con
      // l'inserimento della nuova tabella
      RouterJoinTabelle routerJoin = new RouterJoinTabelle(
          DizionarioTabelle.getInstance(), contenitore, tabellaRic);
      GiunzioneRicercaForm[] giunzioni = routerJoin.getElencoJoinDaAggiungere();
      for (int i = 0; i < giunzioni.length; i++) {
        contenitore.aggiungiGiunzione(giunzioni[i]);
      }
      
      // se e' il primo inserimento di una tabella, allora imposto l'entita'
      // principale pari a tale unica entita' inserita
      if (contenitore.getNumeroTabelle() == 0) {
        contenitore.getTestata().setEntPrinc(tabellaRic.getAliasTabella());
      }
      
      // WE439: nel caso di utente con soli report personali, si imposta il
      // filtro sul livello utente se l'entita' inserita prevede delle regole
      OpzioniUtente opzioniUtente = new OpzioniUtente(
          profiloUtente.getFunzioniUtenteAbilitate());

      if (!contenitore.getTestata().getFiltroUtente() &&
          opzioniUtente.isOpzionePresente(CostantiGeneraliAccount.SOLO_REPORT_PERSONALI_GENRIC)) {
          
        // Si controlla se la tabella aggiunta e' in relazione con l'id
        // utente. Se si, allora setto l'attributo della testata
        // filtroIdUtente a true, altrimenti lo setto a false
        DizionarioLivelli dizLivelli = DizionarioLivelli.getInstance();
        if (dizLivelli.isFiltroLivelloPresente(tabellaRic.getNomeTabella())) {
          contenitore.getTestata().setFiltroUtente(true);
        }
      }
      
      // L.G. (09/03/2015): nel caso di utente con soli report personali, si
      // imposta il filtro per ufficio intestatario se l'entita' inserita
      // e' in relazione con la tabella UFFINT.GENE.
        
      if (opzioniUtente.isOpzionePresente(CostantiGeneraliAccount.SOLO_REPORT_PERSONALI_GENRIC)) {
        if (StringUtils.isNotEmpty((String) request.getSession().getAttribute(
            CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO))) {
          // Si controlla se la tabella aggiunta e' in relazione con UFFINT.
          // Se si, allora setto l'attributo della testata
          // filtro a true, altrimenti lo setto a false
          if (dizTabelle.getDaNomeTabella("UFFINT").getLegameTabelle(tabellaRic.getNomeTabella()).length > 0
              ||
              dizTabelle.getDaNomeTabella(tabellaRic.getNomeTabella()).getLegameTabelle("UFFINT").length > 0) {
            contenitore.getTestata().setFiltroUfficioIntestatario(true);
          } else {
            contenitore.getTestata().setFiltroUfficioIntestatario(false);
          }
        } else {
          contenitore.getTestata().setFiltroUfficioIntestatario(false);
        }
      }
      
      // per ultimo si aggiunge la tabella all'elenco
      contenitore.aggiungiArgomento(tabellaRic);

      // set del request del tab da caricare dopo il salvataggio
      request.setAttribute("tab", CambiaTabAction.CODICE_TAB_ARGOMENTI);

      this.marcaRicercaModificata(request, contenitore.getTestata().getNome());

    } catch (RouterJoinTabelleException e) {
      target = EditTabellaRicercaAction.ERROR_INSERT_FORWARD;
      logger.error(
          this.resBundleGenerale.getString(e.getChiaveResourceBundle()), e);
      this.aggiungiMessaggio(request, e.getChiaveResourceBundle());
    }

    if (logger.isDebugEnabled()) {
      logger.debug("insert: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action elimina
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniElimina() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Action per la cancellazione dell'elenco delle tabelle presente in sessione
   * la tabella il cui progressivo e' passato nel request.
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward elimina(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("elimina: inizio metodo");
    }
    String messageKey = null;
    // set del target di default
    String target = EditTabellaRicercaAction.SUCCESS_ELIMINA_FORWARD;

    Integer progressivo = UtilityNumeri.convertiIntero(request.getParameter("prog"));
    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
        session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        "profiloUtente");

    TabellaRicercaForm tabellaRic = contenitore.estraiTabella(progressivo.intValue());

    // L.G 05/03/07: gestione del filtro per Id Utente
    // Se e' attiva il filtro per Id Utente e si elimina l'ultima tabella in
    // relazione con l'Id Utente, allora si disattiva il filtro per Id Utente e
    // lo si segnala al client con un messaggio informativo

    if (contenitore.getTestata().getFiltroUtente()) {
      DizionarioLivelli dizLivelli = DizionarioLivelli.getInstance();

      // Vettore conterra' i progressivi delle tabelle selezionate per la ricerca
      // che sono in relazione con l'Id Utente
      Vector<Integer> progressiviTabelleIdUtente = new Vector<Integer>();

      for (int i = 0; i < contenitore.getNumeroTabelle(); i++) {
        if (dizLivelli.isFiltroLivelloPresente(contenitore.estraiTabella(i).getNomeTabella()))
          progressiviTabelleIdUtente.addElement(new Integer(i));
      }
      // Verifico se si sta eliminando l'unica tabella che e' in relazione
      // con l'ID Utente: qualora il test sia positivo si setta a false
      // l'attributo filtroIdUtente dell'oggetto TestataRicercaForm presente
      // in sessione. Inoltre se in sessione rimarra' almeno una tabella,
      // allora viene inviato al client un messaggio informativo, altrimenti,
      // visto che in sessione non vi sono piu' tabelle, non viene inviato alcun
      // messaggio poiche' cancellare tutte le tabelle implica l'annullamento
      // del filtraggio per Id Utente
      if (progressiviTabelleIdUtente.size() == 1
          && ((Integer) progressiviTabelleIdUtente.get(0)).intValue() == progressivo.intValue()) {
        // Si sta per eliminare l'unica tabella che e' in relazione con l'Id
        // Utente
        contenitore.getTestata().setFiltroUtente(false);

        if (contenitore.getNumeroTabelle() > 1) {
          messageKey = "info.genRic.filtroIdUtente.disattivato";
          if (logger.isInfoEnabled())
            logger.info(this.resBundleGenerale.getString(messageKey));
          // WE439: si stampa il messaggio solamente se non si tratta di un
          // utente che puo' effettuare solo report personali (in quanto non sa
          // che viene applicato in automatico un filtro sul livello utente)
          OpzioniUtente opzioniUtente = new OpzioniUtente(
              profiloUtente.getFunzioniUtenteAbilitate());
          CheckOpzioniUtente opzioniPerSoliReportPersonali = new CheckOpzioniUtente(
              CostantiGeneraliAccount.SOLO_REPORT_PERSONALI_GENRIC);
          if (!opzioniPerSoliReportPersonali.test(opzioniUtente))
            this.aggiungiMessaggio(request, messageKey);
        }
      }
    }
    // ******************* L.G. 05/03/07: fine modifica *******************

    // L.G. 12/03/2015: se la gestione ufficio intestatario e' attiva ed attivo
    // il filtro per ufficio intestatario, allora nel rimuovere un argomento si
    // controlla se fra gli argomenti rimanenti ve n'e' almeno uno in relazione
    // con la tabella UFFINT.GENE
    
    if (session.getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO) != null &&
        contenitore.getTestata().getFiltroUfficioIntestatario()) {
      DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();

      // Vettore conterra' i progressivi delle tabelle selezionate per
      // la ricerca che sono in relazione con la tabella UFFINT.GENE
      Vector<Integer> progressiviTabelleFiltroUffInt = new Vector<Integer>();

      for (int i = 0; i < contenitore.getNumeroTabelle(); i++) {
        TabellaRicercaForm tabellaRicercaForm = contenitore.estraiTabella(i);

        if (dizTabelle.getDaNomeTabella("UFFINT").getLegameTabelle(tabellaRicercaForm.getNomeTabella()).length > 0 
            ||
            dizTabelle.getDaNomeTabella(tabellaRicercaForm.getNomeTabella()).getLegameTabelle("UFFINT").length > 0) 
          progressiviTabelleFiltroUffInt.addElement(new Integer(i));
      }
      // Verifico se si sta eliminando l'unica tabella che e' in relazione con
      // la tabella UFFINT.GENE: qualora il test sia positivo si setta a true
      // l'attributo filtroUfficioIntestatarioEscluso dell'oggetto
      // TestataRicercaForm presente in sessione. Inoltre se in sessione
      // rimarra' almeno una tabella, allora viene inviato al client un
      // messaggio informativo, altrimenti, visto che in sessione non vi sono
      // piu' tabelle, non viene inviato alcun messaggio poiche' cancellare
      // tutte le tabelle implica l'annullamento del filtraggio per filtro
      // ufficio intestatario
      if (progressiviTabelleFiltroUffInt.size() == 1
          && ((Integer) progressiviTabelleFiltroUffInt.get(0)).intValue() == progressivo.intValue()) {
        // Si sta per eliminare l'unica tabella che e' in relazione
        // con la tabella UFFINT.GENE
        contenitore.getTestata().setFiltroUfficioIntestatario(false);

        if (contenitore.getNumeroTabelle() > 1) {
          messageKey = "info.genRic.filtroUffint.disattivato";
          String labelUffInt = StringUtils.lowerCase(
              this.resBundleGenerale.getString("label.tags.uffint.singolo"));
          
          if (logger.isInfoEnabled()) {
            logger.info(UtilityStringhe.replaceParametriMessageBundle(
                this.resBundleGenerale.getString(messageKey),
                new String[] { labelUffInt } ));
          }
          // Si stampa il messaggio solamente se non si tratta di un utente
          // che puo' effettuare solo report personali (in quanto non sa che
          // viene applicato in automatico un filtro sull'ufficio intestatario)
          OpzioniUtente opzioniUtente = new OpzioniUtente(
              profiloUtente.getFunzioniUtenteAbilitate());
          CheckOpzioniUtente opzioniPerSoliReportPersonali = new CheckOpzioniUtente(
              CostantiGeneraliAccount.SOLO_REPORT_PERSONALI_GENRIC);
          if (!opzioniPerSoliReportPersonali.test(opzioniUtente)) {
            this.aggiungiMessaggio(request, messageKey, labelUffInt);
          }
        }
      }
    }
    
    Vector<Integer> indiciRecordDaEliminare = new Vector<Integer>();

    // Aggiornamento di tutti i campi riferiti alla tabella
    CampoRicercaForm campoRicerca = null;
    for (int i = 0; i < contenitore.getNumeroCampi(); i++) {
      campoRicerca = (CampoRicercaForm) contenitore.estraiCampo(i);
      if (tabellaRic.getMnemonicoTabella().equals(
          campoRicerca.getMnemonicoTabella())
          && tabellaRic.getAliasTabella().equals(campoRicerca.getAliasTabella())) {
        indiciRecordDaEliminare.addElement(new Integer(i));
      }
    }

    for (int j = indiciRecordDaEliminare.size() - 1; j >= 0; j--)
      contenitore.eliminaCampo(((Integer) indiciRecordDaEliminare.elementAt(j)).intValue());

    indiciRecordDaEliminare.removeAllElements();

    // Aggiornamento di tutti gli ordinamenti riferiti alla tabella
    OrdinamentoRicercaForm ordinamentoRicerca = null;
    for (int i = 0; i < contenitore.getNumeroOrdinamenti(); i++) {
      ordinamentoRicerca = (OrdinamentoRicercaForm) contenitore.estraiOrdinamento(i);
      if (tabellaRic.getMnemonicoTabella().equals(
          ordinamentoRicerca.getMnemonicoTabella())
          && tabellaRic.getAliasTabella().equals(
              ordinamentoRicerca.getAliasTabella())) {
        indiciRecordDaEliminare.addElement(new Integer(i));
      }
    }

    for (int j = indiciRecordDaEliminare.size() - 1; j >= 0; j--)
      contenitore.eliminaOrdinamento(((Integer) indiciRecordDaEliminare.elementAt(j)).intValue());

    indiciRecordDaEliminare.removeAllElements();

    // Aggiornamento di tutti i filtri riferiti alla tabella
    FiltroRicercaForm filtroRicerca = null;
    for (int i = 0; i < contenitore.getNumeroFiltri(); i++) {
      filtroRicerca = (FiltroRicercaForm) contenitore.estraiFiltro(i);

      if ((tabellaRic.getMnemonicoTabella().equals(
          filtroRicerca.getMnemonicoTabella()) && tabellaRic.getAliasTabella().equals(
          filtroRicerca.getAliasTabella()))
          || (tabellaRic.getMnemonicoTabella().equals(
              filtroRicerca.getMnemonicoTabellaConfronto()) && tabellaRic.getAliasTabella().equals(
              filtroRicerca.getAliasTabellaConfronto()))) {
        indiciRecordDaEliminare.addElement(new Integer(i));
      }
    }

    for (int j = indiciRecordDaEliminare.size() - 1; j >= 0; j--)
      contenitore.eliminaFiltro(((Integer) indiciRecordDaEliminare.elementAt(j)).intValue());

    indiciRecordDaEliminare.removeAllElements();

    // Vista la associazione tra filtro e parametri, e visto che i filtri che
    // usavano la tabella  in cancellazione sono appena stati cancellati,
    // bisogna eliminare tutti i parametri il cui codiceParametro non e' piu'
    // usato dai filtri rimasti nell'elenco filtri.
    // Se la lista filtri e' vuota, cancello tutti i parametri
    if (contenitore.getNumeroFiltri() == 0) {
      for (int i = 0; i < contenitore.getNumeroParametri(); i++)
        contenitore.eliminaParametro(i);
    } else {
      ParametroRicercaForm parametroRicerca = null;
      filtroRicerca = null;
      Vector<String> parametriDaCancellare = new Vector<String>();
      Set<String> setCodiciParametriUsatiNeiFiltri = new HashSet<String>();
      for (int i = 0; i < contenitore.getNumeroFiltri(); i++) {
        filtroRicerca = contenitore.estraiFiltro(i);
        setCodiciParametriUsatiNeiFiltri.add(filtroRicerca.getParametroConfronto());
      }
      for (int i = 0; i < contenitore.getNumeroParametri(); i++) {
        parametroRicerca = contenitore.estraiParametro(i);
        if (!setCodiciParametriUsatiNeiFiltri.contains(parametroRicerca.getCodiceParametro()))
          parametriDaCancellare.add(parametroRicerca.getProgressivo());
      }
      for (Iterator<String> iter = parametriDaCancellare.iterator(); iter.hasNext();) {
        String element = (String) iter.next();
        contenitore.eliminaParametro(Integer.parseInt(element));
      }
    }

    // Aggiornamento di tutte le join riferite alla tabella
    GiunzioneRicercaForm giunzioneRicerca = null;
    for (int i = 0; i < contenitore.getNumeroGiunzioni(); i++) {
      giunzioneRicerca = (GiunzioneRicercaForm) contenitore.estraiGiunzione(i);

      if ((tabellaRic.getMnemonicoTabella().equals(
          giunzioneRicerca.getMnemonicoTabella1()) && tabellaRic.getAliasTabella().equals(
          giunzioneRicerca.getAliasTabella1()))
          || (tabellaRic.getMnemonicoTabella().equals(
              giunzioneRicerca.getMnemonicoTabella2()) && tabellaRic.getAliasTabella().equals(
              giunzioneRicerca.getAliasTabella2()))) {
        indiciRecordDaEliminare.addElement(new Integer(i));
      }
    }

    GiunzioneRicercaForm[] giunzioniRimosse = new GiunzioneRicercaForm[indiciRecordDaEliminare.size()];
    for (int j = indiciRecordDaEliminare.size() - 1; j >= 0; j--) {
      giunzioniRimosse[j] = contenitore.estraiGiunzione(((Integer) indiciRecordDaEliminare.elementAt(j)).intValue());
      contenitore.eliminaGiunzione(((Integer) indiciRecordDaEliminare.elementAt(j)).intValue());
    }

    indiciRecordDaEliminare.removeAllElements();

    // Cancellazione della tabella dalla sessione
    contenitore.eliminaTabella(progressivo.intValue());

    // se si elimina proprio l'entita' principale allora si resetta tale
    // informazione e si spegne l'eventuale attivazione del collegamento alla scheda
    if (contenitore.getNumeroTabelle() == 0) {
      contenitore.getTestata().setEntPrinc(null);
      contenitore.getTestata().setLinkScheda(Boolean.FALSE);
    }

    // se rimane un'unica tabella allora l'entita' principale diventa l'unica
    // tabella presente
    else if (contenitore.getNumeroTabelle() == 1) {
      contenitore.getTestata().setEntPrinc(
          contenitore.estraiTabella(0).getAliasTabella());
      if (contenitore.getTestata().getLinkScheda().booleanValue()) {
        // nel caso di link sulla scheda abilitato, occorre verificare se esiste
        // la scheda di dettaglio anche per la nuova entit&agrave; principale
        if (!SalvaDatiGenRicercaAction.existsJspSchedaDettaglio(request,
            contenitore)) {
          messageKey = "warnings.genRic.setEntPrinc.nonEsisteJspDettaglio";
          logger.warn(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
          contenitore.getTestata().setLinkScheda(Boolean.FALSE);
        }
      }
    }

    // altrimenti se si elimina l'entita' principale ma rimangono altre tabelle,
    // allora si preimposta la prima tabella come entita' principale, e si invia
    // un messaggio informativo all'utente
    else if (contenitore.getTestata().getEntPrinc().equals(
        tabellaRic.getAliasTabella())) {
      contenitore.getTestata().setEntPrinc(
          contenitore.estraiTabella(0).getAliasTabella());
      messageKey = "info.genRic.setEntPrinc.attribuitoDefault";
      if (logger.isInfoEnabled())
        logger.info(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
      if (contenitore.getTestata().getLinkScheda().booleanValue()) {
        // nel caso di link sulla scheda abilitato, occorre verificare se esiste
        // la scheda di dettaglio anche per la nuova entit&agrave; principale
        if (!SalvaDatiGenRicercaAction.existsJspSchedaDettaglio(request,
            contenitore)) {
          messageKey = "warnings.genRic.setEntPrinc.nonEsisteJspDettaglio";
          logger.warn(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
          contenitore.getTestata().setLinkScheda(Boolean.FALSE);
        }
      }
    }

    this.ricalcolaJoinDopoEliminazione(request, contenitore, giunzioniRimosse);

    // set del request del tab da caricare dopo il salvataggio
    request.setAttribute("tab", CambiaTabAction.CODICE_TAB_ARGOMENTI);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());

    if (logger.isDebugEnabled()) logger.debug("elimina: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Si procede all'analisi di quante join sono state eliminate:
   * <ul>
   * <li>0: e' stata eliminata una tabella isolata</li>
   * <li>1: e' stata eliminata una tabella agli estremi di una relazione, dotata
   * di una sola relazione in partenza oppure in arrivo</li>
   * <li>da 2 in su: la tabella ha piu' legami con altre tabelle, e potrebbe
   * essere una tabella intermedia per cui si crea un prodotto cartesiano
   * rimuovendola, oppure le tabelle collegate sono archivi che diventano
   * isolati, e per i quali si tenta il collegamento con le tabelle rimanenti</li>
   * </ul>
   * 
   * @param request
   * @param contenitore
   *        contenitore della ricerca da gestire
   * @param giunzioniRimosse
   *        giunzioni rimosse in seguito all'eliminazione della tabella
   */
  private void ricalcolaJoinDopoEliminazione(HttpServletRequest request,
      ContenitoreDatiRicercaForm contenitore,
      GiunzioneRicercaForm[] giunzioniRimosse) {
    String messageKey;

    if (giunzioniRimosse.length >= 2) {
      Vector<Integer> indiciArchiviPotenziali = new Vector<Integer>();
      TabellaRicercaForm tabellaRimasta = null;
      TabellaRicercaForm tabellaScollegata = null;
      GiunzioneRicercaForm giunzioneTemporanea = null;
      int numeroLegamiTabellaScollegata = 0;
      // ciclo sulle tabelle rimaste in elenco per individuare quali di queste
      // risultano completamente isolate
      for (int i = 0; i < contenitore.getNumeroTabelle(); i++) {
        tabellaRimasta = contenitore.estraiTabella(i);
        tabellaScollegata = null;
        for (int j = 0; j < giunzioniRimosse.length; j++) {
          if (tabellaRimasta.getAliasTabella().equals(
              giunzioniRimosse[j].getAliasTabella1())
              || tabellaRimasta.getAliasTabella().equals(
                  giunzioniRimosse[j].getAliasTabella2())) {
            // se e' una tabella scollegata da quella appena rimossa allora la
            // marco
            tabellaScollegata = tabellaRimasta;
            break;
          }
        }
        if (tabellaScollegata != null) {
          numeroLegamiTabellaScollegata = 0;
          for (int j = 0; j < contenitore.getNumeroGiunzioni(); j++) {
            giunzioneTemporanea = contenitore.estraiGiunzione(j);
            if (tabellaScollegata.getAliasTabella().equals(
                giunzioneTemporanea.getAliasTabella1())
                || tabellaScollegata.getAliasTabella().equals(
                    giunzioneTemporanea.getAliasTabella2())) {
              // la tabella scollegata da quella eliminata risulta collegata ad
              // altre tabelle
              numeroLegamiTabellaScollegata++;
            }
          }
          // se la tabella scollegata dalla tabella eliminata e' ora isolata,
          // allora potrebbe anche essere un archivio, per cui tentero' un
          // reinserimento della stessa
          if (numeroLegamiTabellaScollegata == 0)
            indiciArchiviPotenziali.addElement(new Integer(i));
        }
      }

      // determinato l'elenco dei potenziali archivi, procedo alla rimozione
      // forzata in ordine inverso di inserimento e poi si riprocede con
      // l'inserimento e l'aggiunta di eventuali join che si creano con le
      // tabelle esistenti
      Vector<TabellaRicercaForm> archiviPotenziali = new Vector<TabellaRicercaForm>();
      for (int j = indiciArchiviPotenziali.size() - 1; j >= 0; j--) {
        archiviPotenziali.addElement(contenitore.estraiTabella(((Integer) indiciArchiviPotenziali.elementAt(j)).intValue()));
        contenitore.eliminaTabella(((Integer) indiciArchiviPotenziali.elementAt(j)).intValue());
      }
      int numeroGiunzioniReintrodotte = 0;
      for (int j = archiviPotenziali.size() - 1; j >= 0; j--) {
        try {
          RouterJoinTabelle routerJoin = new RouterJoinTabelle(
              DizionarioTabelle.getInstance(), contenitore,
              (TabellaRicercaForm) archiviPotenziali.elementAt(j));
          GiunzioneRicercaForm[] giunzioni = routerJoin.getElencoJoinDaAggiungere();
          for (int i = 0; i < giunzioni.length; i++) {
            numeroGiunzioniReintrodotte++;
            contenitore.aggiungiGiunzione(giunzioni[i]);
          }
        } catch (RouterJoinTabelleException e) {
          logger.warn(
              this.resBundleGenerale.getString(e.getChiaveResourceBundle()), e);
        }
        // comunque vada, riaggiungo la tabella, che al limite si trova isolata
        // se non esistono legami o esistono tabelle omonime che esauriscono i
        // legami con le tabelle preesistenti
        contenitore.aggiungiArgomento((TabellaRicercaForm) archiviPotenziali.elementAt(j));
      }

      if (numeroGiunzioniReintrodotte < (giunzioniRimosse.length - 1)) {
        // vuol dire che non e' stato trovato un legame alternativo per qualche
        // tabella rimasta isolata dopo la cancellazione della tabella da parte
        // dell'utente, quindi lo segnalo a video
        messageKey = "errors.genRic.eliminaTabella.koJoinSostitutiva";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      } else {
        messageKey = "info.genRic.eliminaTabella.okJoinSostitutiva";
        logger.info(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
    }
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action setEntPrinc
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSetEntPrinc() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  public ActionForward setEntPrinc(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("setEntPrinc: inizio metodo");

    // set del target di default
    String target = EditTabellaRicercaAction.SUCCESS_SETENTPRINC_FORWARD;

    Integer progressivo = UtilityNumeri.convertiIntero(request.getParameter("prog"));
    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    TabellaRicercaForm tabellaRic = contenitore.estraiTabella(progressivo.intValue());
    contenitore.getTestata().setEntPrinc(tabellaRic.getAliasTabella());
    
    if (contenitore.getTestata().getLinkScheda().booleanValue()) {
      // nel caso di link sulla scheda abilitato, occorre verificare se esiste
      // la scheda di dettaglio anche per la nuova entit&agrave; principale
      if (!SalvaDatiGenRicercaAction.existsJspSchedaDettaglio(request,
          contenitore)) {
        String messageKey = "warnings.genRic.setEntPrinc.nonEsisteJspDettaglio";
        logger.warn(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
        contenitore.getTestata().setLinkScheda(Boolean.FALSE);
      }
    }

    // set del request del tab da caricare dopo il salvataggio
    request.setAttribute("tab", CambiaTabAction.CODICE_TAB_ARGOMENTI);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());

    if (logger.isDebugEnabled()) logger.debug("setEntPrinc: fine metodo");

    return mapping.findForward(target);
  }
}