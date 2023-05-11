/*
 * Created on 02-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.base.wizard;

import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicercheBase;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.sql.comp.SqlElementoCondizione;
import it.eldasoft.utils.utility.UtilityDate;
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
 * DispatchAction per la memorizazzione e la modifica in sessione del filtro
 * della ricerca base;
 * 
 * @author Luca.Giacomazzo
 */
public class FiltriAction extends AbstractDispatchActionBaseGenRicercheBase {

  private final String SUCCESS_ADD           = "successAdd";
  private final String SUCCESS_SALVA         = "successSalva";
  private final String SUCCESS_ANNULLA_LISTA = "successAnnullaLista";
  private final String SUCCESS_FINE          = "successFine";
  private final String ERROR_SALVA           = "errorSalva";
  private final String SUCCESS_ELIMINA       = "successElimina";
  private final String SUCCESS_MODIFICA      = "successModifica";
  
  private final String GRUPPO_OPERATORI = "<=>=<>NOT LIKE";
  
  static Logger logger = Logger.getLogger(FiltriAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere al metodo salvaFiltro 
   * della DispatchAction
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSalvaFiltro(){
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  public ActionForward salvaFiltro(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {

    if(logger.isDebugEnabled()) logger.debug("salvaFiltro: inizio metodo");
    
    // target di default
    String target = SUCCESS_SALVA;

    String appoggio = this.eseguiInsert(form, request);
    if (appoggio != null) target = appoggio;

    if(logger.isDebugEnabled()) logger.debug("salvaFiltro: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere al metodo salvaFiltro 
   * della DispatchAction
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniAnnullaListaFiltri(){
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  public ActionForward annullaListaFiltri(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {

    if(logger.isDebugEnabled()) logger.debug("annullaListaFiltri: inizio metodo");
    
    // target di default
    String target = SUCCESS_ANNULLA_LISTA;
    
    String pageFrom = request.getParameter("pageFrom");
    if(pageFrom != null){
      target = SUCCESS_FINE;
      request.setAttribute("pageFrom", pageFrom);
    }
      
    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
            session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    // Cancello dalla sessione la lista dei filtri esistente
    for(int i = contenitore.getNumeroFiltri() - 1; i >= 0; i--)
      contenitore.eliminaFiltro(i);
    
    if(logger.isDebugEnabled()) logger.debug("annullaListaFiltri: fine metodo");    
    return mapping.findForward(target);
  }
  
  /**
   * Metodo che effettua l'effettivo insert di un nuovo filtro in sessione,
   * effettuando i controlli del caso.
   * 
   * @param form
   * @param request
   */
  private String eseguiInsert(ActionForm form, HttpServletRequest request) {
    String target = null;

    FiltroRicercaForm filtroRicercaForm = (FiltroRicercaForm) form;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
            session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    if (GRUPPO_OPERATORI.indexOf(filtroRicercaForm.getOperatore()) >= 0) {
      // Controllo che il filtro presenti dati corretti
      String appoggio = this.controllaDatiFiltro(filtroRicercaForm, request);
      if (appoggio != null) target = appoggio;
    }

    if (target == null) {
      // Set degli attributi dell'oggetto FiltroRicercaForm necessari ad
      // individuare l'unica tabella della ricerca base senza l'operatore AND tra
      // una condizione e l'altra
      TabellaRicercaForm tabella = (TabellaRicercaForm)
              contenitore.getElencoArgomenti().get(0);
      filtroRicercaForm.setMnemonicoTabella(tabella.getMnemonicoTabella());
      filtroRicercaForm.setAliasTabella(tabella.getAliasTabella());
      filtroRicercaForm.setDescrizioneTabella(tabella.getDescrizioneTabella());
      filtroRicercaForm.setDescrizioneCampo(
          DizionarioCampi.getInstance().get(filtroRicercaForm.getMnemonicoCampo()).getDescrizione());
      // Se l'oggetto filtroRicercaForm non settato il campo progressivo, allora
      // si e' nel caso di inserimento di una nuova condizione di filtro.
      // Altrimenti si e' nel caso di modifica di una condizione di filtro
      // esistente.
      if(filtroRicercaForm.getProgressivo().length() == 0)
        contenitore.aggiungiFiltro(filtroRicercaForm);
      else {
        FiltroRicercaForm filtroSessione = (FiltroRicercaForm) 
          contenitore.getElencoFiltri().get(new Integer(filtroRicercaForm.getProgressivo()).intValue());
        filtroSessione.setMnemonicoCampo(filtroRicercaForm.getMnemonicoCampo());
        filtroSessione.setDescrizioneCampo(
            DizionarioCampi.getInstance().get(filtroRicercaForm.getMnemonicoCampo()).getDescrizione());
        filtroSessione.setOperatore(filtroRicercaForm.getOperatore());
        filtroSessione.setValoreConfronto(filtroRicercaForm.getValoreConfronto());
        filtroSessione.setNotCaseSensitive(filtroRicercaForm.isNotCaseSensitive());
        
        // Cambio del target per tornare alla pagina di riepilogo dei filtri dopo
        // aver modificato un filtro esistente
        target = SUCCESS_MODIFICA;
      }
    }

    return target;
  }

  /**
   * Funzione che restituisce le opzioni per accedere al metodo modifica
   * della DispatchAction
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica(){
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  public ActionForward modifica(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {

    if(logger.isDebugEnabled()) logger.debug("Modifica: inizio metodo");
    
    // target di default
    String target = SUCCESS_ADD;
    
    int idFiltro = new Integer(request.getParameter("id")).intValue();
    
    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
            session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    
    FiltroRicercaForm filtroRicerca = (FiltroRicercaForm) contenitore.getElencoFiltri().get(idFiltro);
    request.setAttribute("filtroRicercaForm", filtroRicerca);
    
    Vector<CampoRicercaForm> elencoCampiForm = contenitore.getElencoCampi();
    
    if(elencoCampiForm.size() > 0){
      request.setAttribute("elencoCampi", elencoCampiForm);

      request.setAttribute("elencoOperatori", CostantiGenRicerche.CBX_OPERATORI_VALUE_REPORT_BASE);
      request.setAttribute("elencoOperatoriLabel", CostantiGenRicerche.CBX_OPERATORI_LABEL_REPORT_BASE);
    }

    if(logger.isDebugEnabled()) logger.debug("Modifica: fine metodo");
    return mapping.findForward(target);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere al metodo elimina
   * della DispatchAction
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniElimina(){
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  public ActionForward elimina(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {

    if(logger.isDebugEnabled()) logger.debug("Elimina: inizio metodo");
    
    // target di default
    String target = SUCCESS_ELIMINA;
    
    int idFiltro = new Integer(request.getParameter("id")).intValue();
    
    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
            session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    
    if(contenitore.getNumeroFiltri() > idFiltro)
      contenitore.eliminaFiltro(idFiltro);
    
    if(contenitore.getNumeroFiltri() == 0)
      target = SUCCESS_SALVA;

    if(logger.isDebugEnabled()) logger.debug("Elimina: fine metodo");
    return mapping.findForward(target);
  }
  
  /**
   * Metodo di controllo di filtro ricevuto dal client: controllo che il filtro
   * effettui il confronto fra dati dello stesso tipo.
   * 
   * @param filtro
   * @return Ritorna 'true' se il filtro può essere salvato, 'false' altrimenti.
   *         Se il filtro non puo' essere salvato, allora vengono impostate
   *         tutti le variabili per il ritorno alla pagina di edit con il
   *         relativo messaggio di errore
   */
  private String controllaDatiFiltro(FiltroRicercaForm filtro,
      HttpServletRequest request) {
    // variabile che indica se e' possibile salvare il filtro o meno

    String target = null;
    String messageKey = null;

    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
             session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    TabellaRicercaForm tabellaRicercaForm = null;
    for (int i = 0; i < contenitore.getNumeroTabelle(); i++) {
      tabellaRicercaForm = contenitore.estraiTabella(i);
      if (tabellaRicercaForm.getAliasTabella().equals(filtro.getAliasTabella()))
        break;
    }
    Campo campo = dizCampi.get(filtro.getMnemonicoCampo());

    // si impostano le informazioni relative alla tabella
    filtro.setMnemonicoTabella(tabellaRicercaForm.getMnemonicoTabella());
    filtro.setDescrizioneTabella(tabellaRicercaForm.getDescrizioneTabella());
    filtro.setDescrizioneCampo(dizCampi.get(campo.getCodiceMnemonico()).getDescrizione());

    if (GRUPPO_OPERATORI.indexOf(filtro.getOperatore()) >= 0
        && !filtro.getOperatore().equals(
            SqlElementoCondizione.STR_OPERATORE_LOGICO_NOT)) {

      if (FiltroRicercaForm.TIPO_CONFRONTO_VALORE.equals(filtro.getTipoConfronto())) {
        if (logger.isDebugEnabled())
          logger.debug("Definizione di una condizione di filtro di confronto con un valore");
        
        boolean continua = true;
      
        // Controllo che il valore inserito nel form, sia dello stesso tipo
        // del campo con cui va confrontato
        String valore = filtro.getValoreConfronto();
        String tipoCampo = null;
        switch (campo.getTipoColonna()) {
        case Campo.TIPO_DATA:
          if (UtilityDate.convertiData(valore, UtilityDate.FORMATO_GG_MM_AAAA) == null)
            continua = false;
          tipoCampo = "data";
          break;
        case Campo.TIPO_INTERO:
          if (UtilityNumeri.convertiIntero(valore) == null) continua = false;
          tipoCampo = "intero";
          break;
        case Campo.TIPO_DECIMALE:
          if (UtilityNumeri.convertiDouble(valore) == null) continua = false;
          tipoCampo = "numero con virgola";
          break;
        // Nel caso sia attesa una stringa, non si effettua alcun controllo
        }

        if (!continua) {
          // Errore: valore specificato nel formato diverso dal formato del
          // campo selezionato nel filtro
          // Si passa alla pagina di edit con i dati del form appena impostato.
          target = ERROR_SALVA;
          messageKey = "errors.genRic.filtroFormatoValoreDiversoDaCampo";
          //Non è possibile salvare il filtro: il confronto con la data odierna
          //richiede che il campo selezionato sia di tipo data. Selezionare un
          //campo di tipo data o cambiare il tipo di confronto
          
          logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0), tipoCampo));
          this.aggiungiMessaggio(request, messageKey, tipoCampo);

          // reset del valore che ha causato il mancato salvataggio del filtro
          filtro.setValoreConfronto(null);
        }

        // si effettua il check del case sensitive
        if (Campo.TIPO_STRINGA != campo.getTipoColonna() && filtro.isNotCaseSensitive()) {
          continua = false;
          filtro.setNotCaseSensitive(false);
          target = ERROR_SALVA;
          messageKey = "errors.genRic.filtroNoCaseSensitiveSuCampoNonStringa";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }
       
        if (!continua) {
          // set nel request dei parametri per rifare l'edit
          request.setAttribute("filtroRicercaForm", filtro);
        } else {
          // reset degli attributi non necessari al filtro
          filtro.setMnemonicoTabellaConfronto(null);
          filtro.setDescrizioneTabellaConfronto(null);
          filtro.setDescrizioneCampoConfronto(null);
          filtro.setParametroConfronto(null);
        }
      }
    }
    return target;
  }
}