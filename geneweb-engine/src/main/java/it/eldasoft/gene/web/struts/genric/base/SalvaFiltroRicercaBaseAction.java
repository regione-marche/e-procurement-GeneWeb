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

import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicercheBase;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.sql.comp.SqlElementoCondizione;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per l'inserimento di un nuovo filtro in una ricerca base o la modifica
 * di un filtro esistente
 * 
 * @author Luca.Giacomazzo
 */
public class SalvaFiltroRicercaBaseAction extends AbstractDispatchActionBaseGenRicercheBase {

  private static final String ERROR_INSERT_FILTRO     = "errorAddFiltro";

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(SalvaFiltroRicercaBaseAction.class);
  
  private static final String GRUPPO_OPERATORI      = "<=>=<>NOT LIKE";
  
  public CheckOpzioniUtente getOpzioniInsert() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  public ActionForward insert(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("insert: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String appoggio = this.eseguiInsert(form, request);
    if (appoggio != null) target = appoggio;

    request.setAttribute("tab", CambiaTabRicercaBaseAction.CODICE_TAB_FILTRI);

    if (logger.isDebugEnabled()) logger.debug("insert: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Metodo che effettua l'effettivo insert di un nuovo filtro in sessione,
   * effettuando i controlli del caso. Questo metodo e' invocato anche dal
   * metodo modifica nel caso in cui il filtroRicercaForm presente
   * nell'ActionForm abbia il progressivo non definito
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

    // variabile per indicare se e' possibile salvare il filtro o meno.
    boolean continua = true;

    if (GRUPPO_OPERATORI.indexOf(filtroRicercaForm.getOperatore()) >= 0) {
      // Controllo che il filtro presenti dati corretti
      String appoggio = this.controllaDatiFiltro(filtroRicercaForm, request);
      if (appoggio != null) {
        target = appoggio;
        continua = false;
      }
    }

    if (continua) {
      if(contenitore.getNumeroFiltri() > 0){
        // Se esiste almeno un filtro in sessione, allora inserisco l'operatore 
        // AND prima del filtro ricevuto dal client
        FiltroRicercaForm filtroTmp = new FiltroRicercaForm();
        filtroTmp.setOperatore(SqlElementoCondizione.STR_OPERATORE_LOGICO_AND);
        contenitore.aggiungiFiltro(filtroTmp);
      }
      // Set degli attributi dell'oggetto FiltroRicercaForm necessari ad
      // individuare l'unica tabella della ricerca base
      TabellaRicercaForm tabella = (TabellaRicercaForm) 
              contenitore.getElencoArgomenti().get(0);
      filtroRicercaForm.setMnemonicoTabella(tabella.getMnemonicoTabella());
      filtroRicercaForm.setAliasTabella(tabella.getAliasTabella());
      filtroRicercaForm.setDescrizioneTabella(tabella.getDescrizioneTabella());
      filtroRicercaForm.setDescrizioneCampo(
          DizionarioCampi.getInstance().get(filtroRicercaForm.getMnemonicoCampo()).getDescrizione());
      
      contenitore.aggiungiFiltro(filtroRicercaForm);

      this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    }

    return target;
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
        case Campo.TIPO_STRINGA:
          
          if (campo.getCodiceTabellato() != null && !"".equals(campo.getCodiceTabellato())) {
            // in caso di campo stringa tabellato tolgo gli apici alla stringa prima
            // di salvare e comunico un messaggio di info
            if (valore.indexOf("'") >= 0 ) {
              valore = valore.replaceAll("'","");
              messageKey = "info.genRic.filtro.toltiApiciValoreFiltroTabellato";
              logger.warn(this.resBundleGenerale.getString(messageKey));
              this.aggiungiMessaggio(request, messageKey);
            }
          }
         
          break;

        }

        if (!continua) {
          // Errore: valore specificato nel formato diverso dal formato del
          // campo selezionato nel filtro
          // Si passa alla pagina di edit con i dati del form appena impostato.
          target = SalvaFiltroRicercaBaseAction.ERROR_INSERT_FILTRO;
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
          target = SalvaFiltroRicercaBaseAction.ERROR_INSERT_FILTRO;
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
          if (campo.getCodiceTabellato() != null && !"".equals(campo.getCodiceTabellato()))
            filtro.setValoreConfronto(valore);        
        }
      }
    }
    return target;
  }
  
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  /**
   * Action per l'update di un filtro in una ricerca
   */
  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("modifica: inizio metodo");
    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    FiltroRicercaForm filtro = (FiltroRicercaForm) form;

    // caso in cui si accede alla pagina di edit in seguito ad un errore, il
    // form
    // appena letto e' privo del progressivo e quindi e' necessario effettuare
    // l'insert di tale form
    if (filtro.getProgressivo() == null || filtro.getProgressivo().equals("")) {
      String appoggio = this.eseguiInsert(form, request);
      if (appoggio != null) target = appoggio;
    } else {

      HttpSession session = request.getSession();
      ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
            session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

      boolean isFiltroOK = true;

      String appoggio = this.controllaDatiFiltro(filtro, request);
      if (appoggio != null)
        target = appoggio;

      if (isFiltroOK) {
        // copia del filtro ricevuto dal client nel filtro presente in
        // sessione a meno di id e progressivo.
        this.copiaFiltroInSessione(contenitore, filtro);

        this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
      }
    }
    request.setAttribute("tab", CambiaTabRicercaBaseAction.CODICE_TAB_FILTRI);

    if (logger.isDebugEnabled()) logger.debug("modifica: fine metodo");
    return mapping.findForward(target);
  }
  
  
  /**
   * Copia del filtro ricevuto dal client nel filtro presente in sessione, a
   * meno degli attributi id e progressivo.
   * 
   * @param contenitore
   * @param filtro
   */
  private void copiaFiltroInSessione(ContenitoreDatiRicercaForm contenitore,
      FiltroRicercaForm filtro) {

    // Filtro presente in sessione da aggiornare
    FiltroRicercaForm filtroRicercaForm = contenitore.estraiFiltro(Integer.parseInt(filtro.getProgressivo()));

    filtroRicercaForm.setOperatore(filtro.getOperatore());
    filtroRicercaForm.setMnemonicoTabella(filtro.getMnemonicoTabella());
    filtroRicercaForm.setAliasTabella(filtro.getAliasTabella());
    filtroRicercaForm.setDescrizioneTabella(filtro.getDescrizioneTabella());
    filtroRicercaForm.setMnemonicoCampo(filtro.getMnemonicoCampo());
    filtroRicercaForm.setDescrizioneCampo(filtro.getDescrizioneCampo());
    filtroRicercaForm.setTipoConfronto(filtro.getTipoConfronto());
    filtroRicercaForm.setMnemonicoTabellaConfronto(filtro.getMnemonicoTabellaConfronto());
    filtroRicercaForm.setAliasTabellaConfronto(filtro.getAliasTabellaConfronto());
    filtroRicercaForm.setDescrizioneTabellaConfronto(filtro.getDescrizioneTabellaConfronto());
    filtroRicercaForm.setMnemonicoCampoConfronto(filtro.getMnemonicoCampoConfronto());
    filtroRicercaForm.setDescrizioneCampoConfronto(filtro.getDescrizioneCampoConfronto());
    filtroRicercaForm.setValoreConfronto(filtro.getValoreConfronto());
    filtroRicercaForm.setParametroConfronto(filtro.getParametroConfronto());
    filtroRicercaForm.setNotCaseSensitive(filtro.isNotCaseSensitive());
  }
  
}