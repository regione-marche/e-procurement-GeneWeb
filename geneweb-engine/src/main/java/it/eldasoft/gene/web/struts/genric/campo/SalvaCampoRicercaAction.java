/*
 * Created on 11-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.campo;

import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.ordinamento.OrdinamentoRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
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
 * Action per l'inserimento di un nuovo campo in una ricerca o per la modifica
 * di un campo esistente
 * 
 * @author Stefano.Sabbadin
 */
public class SalvaCampoRicercaAction extends AbstractDispatchActionBaseGenRicerche {

  private static final String FORWARD_ERRORE_CAMPO = "forwardErroreCampo";

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(SalvaCampoRicercaAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere alla action inserisci
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniInserisci() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  } 
  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward inserisci(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("inserisci: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    CampoRicercaForm campoRicercaForm = (CampoRicercaForm) form;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String nomeTabellaUnivoco = campoRicercaForm.getAliasTabella();
    TabellaRicercaForm tabellaRicercaForm = null;
    for (int i = 0; i < contenitore.getNumeroTabelle(); i++) {
      tabellaRicercaForm = contenitore.estraiTabella(i);
      if (tabellaRicercaForm.getAliasTabella().equals(nomeTabellaUnivoco))
        break;
    }

    // si impostano le informazioni relative alla tabella
    campoRicercaForm.setMnemonicoTabella(tabellaRicercaForm.getMnemonicoTabella());
    campoRicercaForm.setAliasTabella(tabellaRicercaForm.getAliasTabella());
    
    // Controllo che se e' stata selezionata una funzione statistica, essa
    // rispetti questa regola: le funzioni statistiche 'sum' e 'avg' sono
    // applicabili solo a campi di tipo numerico
    String appoggio = controllaFunzioneStatistica(request, campoRicercaForm,
        contenitore);
    if (appoggio != null)
      target = appoggio;
    else {
      // si aggiunge il campo all'elenco
      contenitore.aggiungiCampo(campoRicercaForm);
      // le funzioni statistiche non sono compatibili con il generatore modelli
      // in quanto non si estraggono record ma partizionamenti degli stessi e
      // nemmeno con la visualizzazione del link alla scheda
      if (UtilityStringhe.convertiStringaVuotaInNull(campoRicercaForm.getFunzione()) != null
          && (contenitore.getTestata().getVisModelli() || contenitore.getTestata().getLinkScheda().booleanValue())) {

        if (contenitore.getTestata().getVisModelli()) {
          contenitore.getTestata().setVisModelli(false);
          String messageKey = "info.genRic.visModelli.disabilitato";
          if (logger.isInfoEnabled())
            logger.info(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }

        if (contenitore.getTestata().getLinkScheda().booleanValue()) {
          contenitore.getTestata().setLinkScheda(Boolean.FALSE);
          String messageKey = "info.genRic.linkScheda.disabilitato";
          if (logger.isInfoEnabled())
            logger.info(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }
      }

      this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
      this.setMenuTab(request);
    }
    if (logger.isDebugEnabled()) logger.debug("inserisci: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action controllaFunzioneStatistica
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniControllaFunzioneStatistica() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }  
  
  /**
   * Controllo che se e' stata selezionata una funzione statistica, essa
   * rispetti questa regola: le funzioni statistiche 'sum' e 'avg' sono
   * applicabili solo a campi di tipo numerico
   * 
   * @param request
   * @param campoRicercaForm
   * @param contenitore
   * @param isCampoOK
   * @return Ritorna true se il campo e' presente dati corretti, false
   *         altrimenti
   */
  private String controllaFunzioneStatistica(HttpServletRequest request,
      CampoRicercaForm campoRicercaForm, ContenitoreDatiRicercaForm contenitore) {
    boolean isCampoOK = true;
    String target = null;

    if (UtilityStringhe.convertiStringaVuotaInNull(campoRicercaForm.getFunzione()) != null) {
      DizionarioCampi dizCampi = DizionarioCampi.getInstance();
      Campo campo = dizCampi.get(campoRicercaForm.getMnemonicoCampo());

      // non possono essere utilizzate funzioni statistiche di somma e media su
      // campi di tipo non numerico
      if (("SUM".equals(campoRicercaForm.getFunzione()) || "AVG".equals(campoRicercaForm.getFunzione()))
          && (campo.getTipoColonna() != Campo.TIPO_DECIMALE && campo.getTipoColonna() != Campo.TIPO_INTERO)) {

        target = FORWARD_ERRORE_CAMPO;
        String messageKey = "errors.genRic.campi.funzioneStatisticaNonAmmessa";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
        isCampoOK = false;
        request.setAttribute("campoRicercaForm", campoRicercaForm);
      }

      // non si può applicare una funzione statistica ad un campo utilizzato per
      // l'ordinamento del risultato
      if (isCampoOK) {
        OrdinamentoRicercaForm ordinamento = null;
        for (int i = 0; i < contenitore.getNumeroOrdinamenti(); i++) {
          ordinamento = contenitore.estraiOrdinamento(i);
          if (campoRicercaForm.getAliasTabella().equals(
              ordinamento.getAliasTabella())
              && campoRicercaForm.getMnemonicoCampo().equals(
                  ordinamento.getMnemonicoCampo())) {
            target = FORWARD_ERRORE_CAMPO;
            String messageKey = "errors.genRic.campi.funzioneStatisticaSuCampoOrdinamento";
            logger.error(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
            isCampoOK = false;
            request.setAttribute("campoRicercaForm", campoRicercaForm);
            break;
          }
        }
      }
    }
    return target;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  } 
  
  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("modifica: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;
    
    CampoRicercaForm campoRicercaForm = (CampoRicercaForm) form;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String appoggio = controllaFunzioneStatistica(request, campoRicercaForm,
        contenitore);
    if (appoggio != null)
      target = appoggio;
    else {
      // aggiornamento del dato in sessione con i dati modificati nella pagina
      CampoRicercaForm campoNelContenitore = contenitore.estraiCampo(Integer.parseInt(campoRicercaForm.getProgressivo()));
      boolean titoloCambiato = false;
      
      // **********************************************************************
      // L.G. 23/02/2007: gestione dell'attributo titcolonna nel caso in cui al
      // campo sia applicata una funzione statistica
      // Diversi sono i casi:
      // 1. se si applica una funzione statistica ad un campo il titolo diventa
      //    pari a: <prefisso> + <titolo preesistente>;
      // 2. se si cambia la funzione statistica ad un campo il titolo cambia da:
      //    <prefisso1> + titolo preesistente a <prefisso2> + <titolo preesistente>
      // 3. se si rimuove la funzione statistica ad un campo il titolo cambia da:
      //    <prefisso> + titolo preesistente a <titolo preesistente>
      // 4. se viene cambiato il campo e non la funzione statistica, allora il
      //    titolo diventa: <titolo del nuovo campo>
      // 5. se vengono cambiati sia il campo che la funzione statistica, allora
      //    il titolo diventa: <prefisso nuovo> + <titolo del nuovo campo>
      // Il testo specificato nel prefisso dipende dalla funzione statistica che
      // si va ad applicare. In particolare:
      //
      //    Funzione statistica  |  Prefisso 
      //   ---------------------------------------
      //        COUNT            |  Conta il
      //         MAX             |  Massimo di
      //         AVG             |  Media di
      //         MIN             |  Minimo di
      //         SUM             |  Somma di
      // 
      // Qualora <titolo preesistente> non sia definito viene posto pari alla 
      // descrizione del campo presente in C0CAMPI
      // Inoltre nel caso venga cambiato il campo, bisogna rimuovere l'eventuale
      // ordinamento associato al vecchio campo e segnarlo all'utente con un
      // messaggio di warning
      // **********************************************************************  
      if(campoNelContenitore.getMnemonicoCampo().equals(campoRicercaForm.getMnemonicoCampo())){
        if((campoNelContenitore.getFunzione() == null &&
            campoRicercaForm.getFunzione().length() == 0) ||
           (campoNelContenitore.getFunzione() != null &&
            campoRicercaForm.getFunzione().length() > 0 &&
            campoNelContenitore.getFunzione().equals(campoRicercaForm.getFunzione()))){
          //Non si deve fare null
        } else if(campoNelContenitore.getFunzione() == null && campoRicercaForm.getFunzione().length() > 0){
          // si applica una funzione statistica ad un campo
          campoNelContenitore.setTitoloColonna(
              this.getPrefissoTitolo(campoRicercaForm.getFunzione()) +
              campoNelContenitore.getTitoloColonna());
          titoloCambiato = true;
        } else if(campoNelContenitore.getFunzione() != null && campoRicercaForm.getFunzione().length() > 0){
          // si cambia una funzione statistica ad un campo
          campoNelContenitore.setTitoloColonna(
              this.getPrefissoTitolo(campoRicercaForm.getFunzione()) +
                UtilityStringhe.convertiNullInStringaVuota(
                  campoNelContenitore.getTitoloColonna().replaceFirst(
                    this.getPrefissoTitolo(campoNelContenitore.getFunzione()), "")));
          titoloCambiato = true;
        } else if(campoNelContenitore.getFunzione() != null &&
                      campoRicercaForm.getFunzione().length() == 0){
          // si rimuove una funzione statistica ad un campo
          campoNelContenitore.setTitoloColonna(
              UtilityStringhe.convertiNullInStringaVuota(
                campoNelContenitore.getTitoloColonna().replaceFirst(
                  this.getPrefissoTitolo(campoNelContenitore.getFunzione()), "")));
          titoloCambiato = true;
        }
      } else {
        // Verifica se il campo presente nel contenitore presenta un ordinamento
        // o meno. Se si, si va a rimuovere tale ordinamento e lo si segnala al
        // client con un messaggio di warning
        Vector<OrdinamentoRicercaForm> listaOrdinamenti = contenitore.getElencoOrdinamenti();
        for(int i = listaOrdinamenti.size()-1; i >= 0; i--){
          OrdinamentoRicercaForm ordinamento = (OrdinamentoRicercaForm) listaOrdinamenti.get(i);
          if(ordinamento.getMnemonicoCampo().equals(campoNelContenitore.getMnemonicoCampo())){
            String msgKey = "info.genRic.campi.ordinamentoColonna.rimozione";
            this.aggiungiMessaggio(request, msgKey);
            contenitore.eliminaOrdinamento(i);
          }
        }
        
        campoNelContenitore.setMnemonicoCampo(campoRicercaForm.getMnemonicoCampo());
        //E' stato cambiato il campo, quindi ricalcolo il titolo della colonna
        DizionarioCampi dizCampi = DizionarioCampi.getInstance();
        Campo campo = dizCampi.get(campoRicercaForm.getMnemonicoCampo());
        campoNelContenitore.setTitoloColonna(
            UtilityStringhe.convertiNullInStringaVuota(
                this.getPrefissoTitolo(campoRicercaForm.getFunzione())) + 
                campo.getDescrizione());
        titoloCambiato = true;
      }

      //Controllo della lunghezza del titolo della colonna: se maggiore di 
      //CostantiGenRicerche.MAX_LEN_TITOLO_COLONNA caratteri il titolo viene
      //tagliato al 50-esimo carattere. Il tutto viene segnalato al client con
      //un messaggio di tipo warning
      if (titoloCambiato) {
        if (campoNelContenitore.getTitoloColonna().length() > CostantiGenRicerche.MAX_LEN_TITOLO_COLONNA) {
          campoNelContenitore.setTitoloColonna(campoNelContenitore.getTitoloColonna().substring(
              0, CostantiGenRicerche.MAX_LEN_TITOLO_COLONNA));
          messageKey = "info.genRic.campi.titoloColonna.tagliatoAMaxLunghezza";
        } else
          messageKey = "info.genRic.campi.titoloColonna.cambiatoTitolo";
        this.aggiungiMessaggio(request, messageKey);
      }

      campoNelContenitore.setFunzione(
          UtilityStringhe.convertiStringaVuotaInNull(campoRicercaForm.getFunzione()));
      campoNelContenitore.setDescrizioneCampo(DizionarioCampi.getInstance().get(
          campoNelContenitore.getMnemonicoCampo()).getDescrizioneBreve());

      // le funzioni statistiche non sono compatibili con il generatore modelli
      // in quanto non si estraggono record ma partizionamenti degli stessi e
      // nemmeno con la visualizzazione del link alla scheda
      if (UtilityStringhe.convertiStringaVuotaInNull(campoRicercaForm.getFunzione()) != null
          && (contenitore.getTestata().getVisModelli() || contenitore.getTestata().getLinkScheda().booleanValue())) {

        if (contenitore.getTestata().getVisModelli()) {
          contenitore.getTestata().setVisModelli(false);
          messageKey = "info.genRic.visModelli.disabilitato";
          if (logger.isInfoEnabled())
            logger.info(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }

        if (contenitore.getTestata().getLinkScheda().booleanValue()) {
          contenitore.getTestata().setLinkScheda(Boolean.FALSE);
          messageKey = "info.genRic.linkScheda.disabilitato";
          if (logger.isInfoEnabled())
            logger.info(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }
      }

      this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
      this.setMenuTab(request);
    }

    if (logger.isDebugEnabled()) logger.debug("modifica: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Metodo per la determinazione del prefisso da applicare al titolo della colonna
   * a cui e' applicata la funzione statistica
   * @param funzioneStatistica
   * @return Ritorna il prefisso per il titolo del campo a cui e' applicata la
   *         funzione statistica. Il prefisso dipende dalla funzione statistica
   *         stessa
   */
  private String getPrefissoTitolo(String funzioneStatistica){
    String result = null;
    if("SUM".equals(funzioneStatistica))
      result = "Somma di ";
    else if("MAX".equals(funzioneStatistica))
      result = "Massimo di ";
    else if("MIN".equals(funzioneStatistica))
      result = "Minimo di ";
    else if("AVG".equals(funzioneStatistica))
      result = "Media di ";
    else if("COUNT".equals(funzioneStatistica))
      result = "Conta il ";
    return result;
  }
  
  
  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menu a tab in fase di visualizzazione del dettaglio dei Dati Generali
   * di una ricerca
   * 
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);

    boolean isInSessione = true;
    if (obj == null) {
      isInSessione = false;
      obj = new GestioneTab();
    }

    GestioneTab gestoreTab = (GestioneTab) obj;
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_CAMPI);
    gestoreTab.setTabSelezionabili(new String[] {
        CostantiGenRicerche.TAB_DATI_GENERALI, CostantiGenRicerche.TAB_GRUPPI,
        CostantiGenRicerche.TAB_ARGOMENTI, CostantiGenRicerche.TAB_JOIN,
        CostantiGenRicerche.TAB_PARAMETRI, CostantiGenRicerche.TAB_FILTRI,
        CostantiGenRicerche.TAB_ORDINAMENTI, CostantiGenRicerche.TAB_LAYOUT});

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }

}