/*
 * Created on 13-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.filtro;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.parametro.ParametroRicercaForm;
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per l'inserimento di un nuovo filtro in una ricerca o la modifica di
 * un filtro esistente
 * 
 * @author Luca Giacomazzo
 */
public class SalvaFiltroRicercaAction extends
    AbstractDispatchActionBaseGenRicerche {

  private static final String FORWARD_NUOVO_PARAMETRO = "forwardNuovoParametro";
  private static final String ERROR_INSERT_FILTRO     = "errorAddFiltro";

  /** Logger Log4J di classe */
  static Logger               logger                  = Logger.getLogger(SalvaFiltroRicercaAction.class);

  // NON CANCELLARE LA RIGA SOTTOSTANTE: anche se non usata può servire in
  // futuro!!
  // private static final String GRUPPO_OPERATORI_1 = "()ANDORNOT";
  private static final String GRUPPO_OPERATORI_2      = "IS NOT NULLIS NULL";
  private static final String GRUPPO_OPERATORI_3      = "<=>=<>NOT LIKEINNOT IN";

  /**
   * Funzione che restituisce le opzioni per accedere alla action insert
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniInsert() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Action per l'insert di un nuovo ordinamento in una ricerca
   */
  public ActionForward insert(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("insert: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String appoggio = this.eseguiInsert(form, request);
    if (appoggio != null) target = appoggio;

    this.setMenuTab(request);

    if (StringUtils.isNotEmpty((String) request.getSession().getAttribute(
        CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO))) {
      request.setAttribute("isAssociazioneUffIntAbilitata", "true");
    } else {
      request.setAttribute("isAssociazioneUffIntAbilitata", "false");
    }
    
    if (logger.isDebugEnabled()) logger.debug("insert: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
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
      ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

      boolean isFiltroOK = true;

      if ((GRUPPO_OPERATORI_2.indexOf(filtro.getOperatore()) >= 0 || GRUPPO_OPERATORI_3.indexOf(filtro.getOperatore()) >= 0)
          && !filtro.getOperatore().equals(
              SqlElementoCondizione.STR_OPERATORE_LOGICO_NOT)) {

        String appoggio = this.controllaDatiFiltro(filtro, request);
        if (appoggio != null) {
          target = appoggio;
          if (!SalvaFiltroRicercaAction.FORWARD_NUOVO_PARAMETRO.equals(appoggio))
            isFiltroOK = false;
        }
      }
      // altrimenti l'operatore scelto appartiene al GRUPPO_OPERATORI_1

      if (isFiltroOK) {
        // Filtro presente in sessione da aggiornare
        FiltroRicercaForm filtroContenitore = (FiltroRicercaForm) contenitore.estraiFiltro(Integer.parseInt(filtro.getProgressivo()));

        /*
         * controlli tra il filtro ricevuto dal client e quello presente in
         * sessione nel caso in cui il filtro in sessione usi un parametro. Si
         * distinguono i seguenti casi: 1. filtro in sessione usa 'ParametroA' e
         * il filtro dal client richiede un nuovo parametro; 2. filtro in
         * sessione usa 'ParametroA' e il filtro dal client richiede di usare
         * 'ParametroB'; 3. filtro in sessione usa 'ParametroA' e il filtro dal
         * client non usa alcun parametro; Nei casi 1,2,3: il 'ParametroA' e'
         * usato in altri filtri? Se si, il 'ParametroA' non viene cancellato.
         * Se no, il 'ParametroA' viene cancellato. Se il filtro in sessione non
         * usa un parametro, allora non sono necessarie ulteriori operazioni,
         * oltre a quella di aggiornamento del filtro in modifica nell'elenco
         * filtri in sessione
         */
        if (FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO.equals(filtroContenitore.getTipoConfronto())) {
          if ( // CASO 1 || CASO 2 || CASO 3
              (FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO.equals(filtro.getTipoConfronto()) &&
              (filtro.getParametroConfronto() != null && 
               filtro.getParametroConfronto().equals("")))
              // condizione nel caso si scelga di assegnare un nuovo parametro
           || (FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO.equals(filtro.getTipoConfronto()) &&
               (filtro.getParametroConfronto() != null && 
               !filtro.getParametroConfronto().equals(filtroContenitore.getParametroConfronto())))
              // condizione nel caso si scelga di assegnare un parametro
              // esistente
           || (!FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO.equals(filtro.getTipoConfronto()))) {
            // condizione nel caso si scelga di assegnare un NON parametro

            // ciclo sull'elenco dei filtri, verificando se il parametro del
            // filtro in sessione e'
            // usato una volta o più' volte: nel primo caso il parametro viene
            // cancellato, mentre
            // nel secondo caso il parametro NON viene cancellato. In entrambi i
            // casi il nuovo
            // filtro viene copiato sopra al filtro in sessione (usando la
            // chiamata 'copiaFiltroInSessione' di seguito inserita)
            int numeroFiltriConParametro = 0;
            FiltroRicercaForm filtroTMP = null;
            for (int i = 0; i < contenitore.getNumeroFiltri()
                && numeroFiltriConParametro <= 1; i++) {
              filtroTMP = contenitore.estraiFiltro(i);
              if (filtroContenitore.getParametroConfronto().equals(
                  filtroTMP.getParametroConfronto()))
                numeroFiltriConParametro++;
            }
            if (numeroFiltriConParametro == 1) {
              ParametroRicercaForm parametro = null;
              boolean trovatoParametro = false;
              int progressivo = -1;
              for (int i = 0; i < contenitore.getNumeroParametri()
                  && !trovatoParametro; i++) {
                parametro = contenitore.estraiParametro(i);
                if (filtroContenitore.getParametroConfronto().equals(
                    parametro.getCodiceParametro())) {
                  progressivo = Integer.parseInt(parametro.getProgressivo());
                  trovatoParametro = true;
                }
              }
              if (CostantiGeneraliStruts.FORWARD_OK.equals(target))
                contenitore.eliminaParametro(progressivo);
              else
                request.setAttribute("parametroDaCancellare", "" + progressivo);
            }
          }
        }

        if (CostantiGenRicerche.STR_OPERATORI_PARENTESI.equals(filtro.getOperatore())) {
          int posizione = Integer.parseInt(filtro.getProgressivo());
          // Cancellazione del filtro da modificare
          contenitore.eliminaFiltro(posizione);
          // inserimento della doppia parentesi nella posizione indicata
          this.inserisciDoppiaParentesi(contenitore, posizione);
        } else
          // copia del filtro ricevuto dal client nel filtro presente in
          // sessione a meno di id e progressivo.
          this.copiaFiltroInSessione(contenitore, filtro);

        this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
      }
    }
    this.setMenuTab(request);

    if (logger.isDebugEnabled()) logger.debug("modifica: fine metodo");
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

    if ((GRUPPO_OPERATORI_2.indexOf(filtroRicercaForm.getOperatore()) >= 0 || 
         GRUPPO_OPERATORI_3.indexOf(filtroRicercaForm.getOperatore()) >= 0) &&
         !filtroRicercaForm.getOperatore().equals(
            SqlElementoCondizione.STR_OPERATORE_LOGICO_NOT)) {

      // Controllo che il filtro presenti dati corretti
      String appoggio = this.controllaDatiFiltro(filtroRicercaForm, request);
      if (appoggio != null) {
        target = appoggio;
        if (!SalvaFiltroRicercaAction.FORWARD_NUOVO_PARAMETRO.equals(appoggio))
          continua = false;
      }
    }

    if (continua) {
      // se l'ultimo elemento dell'elenco è basato non su un operatore logico
      // e non è nemmeno una parentesi aperta, allora aggiungo anche
      // un'operatore logico AND per velocizzare la stesura della query
      if (contenitore.getNumeroFiltri() > 0) {
        String operatore = contenitore.estraiFiltro(
            contenitore.getNumeroFiltri() - 1).getOperatore();
        if (!(SqlElementoCondizione.STR_OPERATORE_LOGICO_AND.equals(operatore)
            || SqlElementoCondizione.STR_OPERATORE_LOGICO_OR.equals(operatore)
            || SqlElementoCondizione.STR_OPERATORE_LOGICO_NOT.equals(operatore)
            || SqlElementoCondizione.STR_OPERATORE_PARENTESI_APERTA.equals(operatore))) {
          FiltroRicercaForm filtro = new FiltroRicercaForm();
          filtro.setOperatore(SqlElementoCondizione.STR_OPERATORE_LOGICO_AND);
          contenitore.aggiungiFiltro(filtro);
        }
      }
      if (CostantiGenRicerche.STR_OPERATORI_PARENTESI.equals(filtroRicercaForm.getOperatore()))
        this.inserisciDoppiaParentesi(contenitore, -1);
      else
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
    boolean result = true;
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

    if (GRUPPO_OPERATORI_3.indexOf(filtro.getOperatore()) >= 0
        && !filtro.getOperatore().equals(
            SqlElementoCondizione.STR_OPERATORE_LOGICO_NOT)) {

      if (FiltroRicercaForm.TIPO_CONFRONTO_CAMPO.equals(filtro.getTipoConfronto())) {
        if (logger.isDebugEnabled())
          logger.debug("Definizione di una condizione di filtro di confronto con un campo.");

        tabellaRicercaForm = null;
        for (int i = 0; i < contenitore.getNumeroTabelle(); i++) {
          tabellaRicercaForm = contenitore.estraiTabella(i);
          if (tabellaRicercaForm.getAliasTabella().equals(
              filtro.getAliasTabellaConfronto())) break;
        }
        Campo campoConfronto = dizCampi.get(filtro.getMnemonicoCampoConfronto());

        if (campo.getTipoColonna() == campoConfronto.getTipoColonna()) {
          filtro.setMnemonicoTabellaConfronto(tabellaRicercaForm.getMnemonicoTabella());
          filtro.setDescrizioneTabellaConfronto(tabellaRicercaForm.getDescrizioneTabella());
          filtro.setDescrizioneCampoConfronto(dizCampi.get(
              campoConfronto.getCodiceMnemonico()).getDescrizione());
          filtro.setValoreConfronto(null);
          filtro.setParametroConfronto(null);
        } else {
          // errore, perche' e' stato scelto di fare il confronto tra due campi
          // che sono di tipo diverso.
          // Si ritorna alla pagina di edit con i dati del form appena
          // impostato.
          target = SalvaFiltroRicercaAction.ERROR_INSERT_FILTRO;
          messageKey = "errors.genRic.filtroTipoCampiDiversi";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);

          // reset dei valori errati che hanno causato il mancato salvataggio
          // del filtro
          filtro.setMnemonicoTabellaConfronto(null);
          filtro.setDescrizioneTabellaConfronto(null);
          filtro.setDescrizioneCampoConfronto(null);
          filtro.setMnemonicoCampoConfronto(null);
          request.setAttribute("filtroRicercaForm", filtro);
          result = false;
        }
      } else if (FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO.equals(filtro.getTipoConfronto())) {
        // Definizione di una condizione di filtro con il parametro: si crea un
        // nuovo parametro
        if (logger.isDebugEnabled())
          logger.debug("Definizione di una condizione di filtro di confronto con un parametro.");

        boolean isNuovoParametro = false;

        if (filtro.getParametroConfronto().equals("")
            || filtro.getParametroConfronto() == null) {
          // creazione di un nuovo parametro. Se questa riga non viene eseguita,
          // allora si sta definendo un filtro con parametro, che usa un
          // parametro già definito
          filtro.setParametroConfronto(CostantiGenRicerche.CODICE_PARAMETRO
              + (contenitore.getMaxCodiceParametro() + 1));
          isNuovoParametro = true;
        } else {
          // si riutilizza un parametro in un altro filtro: bisogna controllare
          // che il campo ed il filtro siano dati dello stesso tipo

          // Ricerca nella lista dei parametri del parametro selezionato dal
          // client
          boolean trovatoParametro = false;
          ParametroRicercaForm parametro = null;
          for (int i = 0; i < contenitore.getNumeroParametri()
              && !trovatoParametro; i++) {
            parametro = contenitore.estraiParametro(i);
            if (parametro.getCodiceParametro().equals(
                filtro.getParametroConfronto())) trovatoParametro = true;
          }
          // Verifica che il campo selezionato nel filtro e il parametro siano
          // dello stesso tipo
          boolean continua = true;
          String tipoParametro = null;
          if (campo.getCodiceTabellato() != null) {
            // Il campo ha un tabellato associato
            if (parametro.getTabCod() != null) {
              if (!campo.getCodiceTabellato().equals(parametro.getTabCod())) {
                continua = false;
                tipoParametro = "'" + parametro.getCodiceParametro() + "'";
                messageKey = "errors.genRic.filtroCampoEParametroTabellatiDiversi";
                continua = false;
                // errors.genRic.filtroCampoEParametroTabellatiDiversi=Non è
                // possibile salvare il filtro: il campo selezionato ed il
                // parametro {0} usano tabellati diversi. Cambiare parametro o
                // definirne uno di nuovo
              } // else campo e parametro sono entrambi di tipo tabellato e
              // hanno lo stesso codiceTabellato
            } else {
              messageKey = "errors.genRic.filtroCampoTabellatoParametroNonTabellato";
              tipoParametro = "'" + parametro.getCodiceParametro() + "'";
              continua = false;
              // errors.genRic.filtroCampoTabellatoParametroNonTabellato=Non è
              // possibile salvare il filtro: il campo selezionato usa
              // tabellato, mentre il parametro {0} non e' un dato tabellato.
              // Cambiare parametro o definirne uno di nuovo
            }
          } else {
            // Il campo non ha un tabellato associato
            switch (campo.getTipoColonna()) {
            case Campo.TIPO_DATA:
              if (!"D".equals(parametro.getTipoParametro())) {
                continua = false;
                tipoParametro = "data";
              }
              break;
            case Campo.TIPO_DECIMALE:
              if (!"F".equals(parametro.getTipoParametro())) {
                continua = false;
                tipoParametro = "numero con virgola";
              }
              break;
            case Campo.TIPO_INTERO:
              if (!"I".equals(parametro.getTipoParametro())) {
                continua = false;
                tipoParametro = "intero";
              }
              break;
            case Campo.TIPO_STRINGA:
              if (!"S".equals(parametro.getTipoParametro())) {
                continua = false;
                tipoParametro = "stringa";
              }
              break;
            case Campo.TIPO_NOTA:
              if (!"S".equals(parametro.getTipoParametro())) {
                continua = false;
                tipoParametro = "stringa";
              }
              break;
            }
            messageKey = "errors.genRic.filtroFormatoParametroDiversoDaCampo";
            // errors.genRic.filtroFormatoParametroDiversoDaCampo=Non è
            // possibile salvare il filtro: il campo selezionato ed il parametro
            // usato non sono dello stesso formato. Selezionare un parametro nel
            // formato {0} o definirne uno di nuovo
          }

          if (!continua) {
            // Errore: valore specificato nel formato diverso dal formato del
            // campo selezionato nel filtro
            // Si passa alla pagina di edit con i dati del form appena
            // impostato.
            target = SalvaFiltroRicercaAction.ERROR_INSERT_FILTRO;
            logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
                UtilityStringhe.getPatternParametroMessageBundle(0),
                tipoParametro));
            this.aggiungiMessaggio(request, messageKey, tipoParametro);

            // reset del valore che ha causato il mancato salvataggio del filtro
            filtro.setParametroConfronto(null);
            // set nel request dei parametri di fare l'edit
            request.setAttribute("filtroRicercaForm", filtro);
            result = false;
          }

        }
        // reset degli attributi non necessari al filtro
        filtro.setMnemonicoTabellaConfronto(null);
        filtro.setDescrizioneTabellaConfronto(null);
        filtro.setDescrizioneCampoConfronto(null);
        filtro.setValoreConfronto(null);

        // se si salva il filtro, si salva il relativo parametro solo se e'
        // nuovo
        if (isNuovoParametro && result) {
          // passaggio al metodo insert della DispatchAction
          // 'SalvaParametroRicercaAction' delle variabili
          // necessarie alla creazione di un nuovo parametro.
          request.setAttribute("campo", campo);
          target = SalvaFiltroRicercaAction.FORWARD_NUOVO_PARAMETRO;
        }
      } else if (FiltroRicercaForm.TIPO_CONFRONTO_VALORE.equals(filtro.getTipoConfronto())) {
        if (logger.isDebugEnabled())
          logger.debug("Definizione di una condizione di filtro di confronto con un valore o una lista.");
        
        //L.G. 01/02/2007: gestione dell'operatore IN
        boolean continua = true;
        
        if (SqlElementoCondizione.STR_OPERATORE_CONFRONTO_IN.equals(filtro.getOperatore()) ||
           SqlElementoCondizione.STR_OPERATORE_CONFRONTO_NOT_IN.equals(filtro.getOperatore())) {
          String erroreListaValori = null;
          Vector<String> listaValoriSeparati = null;
          
          String listaValori = filtro.getValoreConfronto().trim();
          switch (campo.getTipoColonna()) {
          case Campo.TIPO_DATA:           
            if (listaValori.indexOf(",") > 0) {

              listaValoriSeparati = new Vector<String>(listaValori.split(",").length);
              String tmp = null;
              for (int i=0; i < listaValori.split(",").length && continua; i++) {
                tmp = listaValori.split(",")[i].trim();
                if (UtilityDate.convertiData(tmp, UtilityDate.FORMATO_GG_MM_AAAA) == null) {
                  continua = false;
                  erroreListaValori = "valoriFormatoDiversoData";
                } else 
                  listaValoriSeparati.add(tmp);
              }
            } else if (listaValori.indexOf(",") < 0) {
              //La lista e' costituita da un unico valore
              if (UtilityDate.convertiData(listaValori, UtilityDate.FORMATO_GG_MM_AAAA) == null) {
                continua = false;
                erroreListaValori = "valoriFormatoDiversoData";
              } else {
                listaValoriSeparati = new Vector<String>();
                listaValoriSeparati.add(listaValori);
              }
            } else {
              // La lista dei valori e' stata inserita in un formato errato: 
              // infatti inizia con la virgola ( ", 01/03/2000, ...)
              continua = false;
              erroreListaValori = "formatoListaNonAmmesso";
            }
            break;
          case Campo.TIPO_DECIMALE:
          case Campo.TIPO_INTERO:
            if (listaValori.indexOf(",") > 0) {
              //Divisione della lista nei singoli valori e trim di ciascun valore
              listaValoriSeparati = new Vector<String>(listaValori.split(",").length);
              String tmp = null;
              for (int i=0; i < listaValori.split(",").length && continua; i++) {
                tmp = listaValori.split(",")[i].trim();
                if (UtilityNumeri.convertiIntero(tmp) != null || 
                    UtilityNumeri.convertiDouble(tmp) != null)
                  listaValoriSeparati.add(tmp);
                else {
                  continua = false;
                  erroreListaValori = "valoriFormatoDiversoNumero";
                }
              }
            } else if (listaValori.indexOf(",") < 0) {
              //La lista e' costituita da un unico valore
              if (UtilityNumeri.convertiIntero(listaValori) != null ||
                  UtilityNumeri.convertiDouble(listaValori) != null) {
                listaValoriSeparati = new Vector<String>();
                listaValoriSeparati.add(listaValori);
              } else {
                continua = false;
                erroreListaValori = "valoriFormatoDiversoNumero";
              }
            } else {
              // La lista dei valori e' stata inserita in un formato errato: 
              // infatti inizia con la virgola ( ", 10.2, 32, 41.3, ...)
              continua = false;
              erroreListaValori = "formatoListaNonAmmesso";
            }
            break;
          case Campo.TIPO_STRINGA:
          case Campo.TIPO_NOTA:
            //Parsing della lista dei valori inseriti dal client

            // F.D. 23/06/08 è possibile inserire una lista di stringhe senza apici 
            // dato che al momento dell'esecuzione della ricerca li inseriamo manualmente
            if (listaValori.indexOf("'") != 0 || listaValori.lastIndexOf("'") != (listaValori.length()-1)) {
              if (campo.getCodiceTabellato() == null || "".equals(campo.getCodiceTabellato())) {
                continua = false;
                erroreListaValori = "formatoListaNonAmmesso";
              } else {
                
                listaValori = listaValori.replaceAll("'","");
                messageKey = "info.genRic.filtro.toltiApiciValoreFiltroTabellato";
                logger.warn(this.resBundleGenerale.getString(messageKey));
                this.aggiungiMessaggio(request, messageKey);
              }
              
              // nel caso in cui non ci sono ' nella stringa la carico così com'è 
              // senza fare controlli di nessun tipo
              listaValoriSeparati = new Vector<String>();
              listaValoriSeparati.addElement(listaValori);
            } else {
              // la lista dei valori è con gli apici
              if (campo.getCodiceTabellato() != null && !"".equals(campo.getCodiceTabellato())) {
                // in caso di campo stringa tabellato tolgo gli apici alla stringa prima
                // di salvare e comunico un masseggio di info
                //String[] listaValoriSplittati = listaValori.split(",");
                //boolean messaggioInserito = false;
                //for (int i = 0; i < listaValoriSplittati.length; i++) {
                  //String valore = listaValoriSplittati[i];
                  
                if (listaValori.indexOf("'") >= 0) {
                    listaValori = listaValori.replaceAll("'","");
                    
                      messageKey = "info.genRic.filtro.toltiApiciValoreFiltroTabellato";
                      logger.warn(this.resBundleGenerale.getString(messageKey));
                      this.aggiungiMessaggio(request, messageKey);
                      //messaggioInserito = true;
                    
                } 
                 
                //listaValori = UtilityStringhe.serializza(listaValoriSplittati,',');
                
              }
              if (listaValori.indexOf(",") == 0) {
                // La lista dei valori e' stata inserita in un formato errato: 
                // infatti inizia con la virgola ( ", 'asa', 'sdwe', '4fewwqw', ...)
                continua = false;
                erroreListaValori = "formatoListaNonAmmesso";
              } else if (listaValori.indexOf(",") < 0) {
                //La lista e' costituita da un'unica stringa                
                if (this.isCasoAnomalo(listaValori)) {
                  erroreListaValori = "casoAnormaloDaVerificare";
                }
                listaValoriSeparati = new Vector<String>();
                listaValoriSeparati.addElement(listaValori.trim());
              } else {
                if (campo.getCodiceTabellato() == null || "".equals(campo.getCodiceTabellato())) {
                  //Ciclo per parsing della lista dei valori 
                
                  // L'argomento dell'istruzione split usata qui sotto è una 
                  // espressione regolare: tale regEx cerca tutte le stringhe del
                  // tipo: ',' e tutte le possibili varianti con un qualsiasi 
                  // numero di spazi prima e dopo la virgola.
                  // Ad esempio: "' , '" oppure "', '" oppure "'   , '", ecc...
                  // Esempio di esecuzione dell'operazione di split con la regEx:
                  // se listaValori = "'casa', 'cosa' , 'caso' ,    'cos, e' , 'case, assa , 'papa'   ' , '  cassa  '";
                  // gli elementi dell'array di stringhe sono i seguenti:
                  // strTmpArray[0] = "'casa";
                  // strTmpArray[1] = "cosa";
                  // strTmpArray[2] = "caso";
                  // strTmpArray[3] = "cos, e' , 'case, assa , 'papa'   ";
                  // strTmpArray[4] = "  cassa  '";
                  String[] strTmpArray = listaValori.split("'[ ]*,[ ]*'");
                  
                  int i = 0;
                
                  listaValoriSeparati = new Vector<String>();
                  // Primo elemento della lista
                  if (continua && this.isCasoAnomalo(strTmpArray[0].concat("'")))
                    erroreListaValori = "casoAnormaloDaVerificare";
                  listaValoriSeparati.addElement(strTmpArray[0].concat("'"));
                  
                  // Elementi centrali della lista
                  for (i = 1; i < strTmpArray.length - 1; i++) {
                    if (continua && this.isCasoAnomalo(strTmpArray[i].concat("'")))
                      erroreListaValori = "casoAnormaloDaVerificare";
                    listaValoriSeparati.addElement("'".concat(strTmpArray[i].concat("'")));
                  }
                  //Ultimo elemento della lista
                  if (continua && strTmpArray.length > 1) {
                    if (this.isCasoAnomalo(strTmpArray[strTmpArray.length-1]))
                      erroreListaValori = "casoAnormaloDaVerificare";
                    if (strTmpArray[strTmpArray.length-1].lastIndexOf("'") != strTmpArray[strTmpArray.length-1].length()-1)
                      strTmpArray[strTmpArray.length-1] = strTmpArray[strTmpArray.length-1].concat("'"); 
                    listaValoriSeparati.addElement("'".concat(strTmpArray[strTmpArray.length-1]));
                  }
                } else {
                  listaValoriSeparati = new Vector<String>();
                  listaValoriSeparati.addElement(listaValori);
                }
              }
            }
            break;
          }
          
          if (continua) {
            //Ricostruire la lista dei valori in una stringa e aggiornare il valore sul filtro
            //Attenzione maxLength su DB del campo VAL_CONFR = 200
            StringBuffer strBuffer = new StringBuffer("");
            for (int i=0; i < listaValoriSeparati.size()-1; i++)
              strBuffer.append((String)listaValoriSeparati.get(i) + ",");
            strBuffer.append(listaValoriSeparati.get(listaValoriSeparati.size()-1));
            filtro.setValoreConfronto(strBuffer.toString());
            
            // reset degli attributi non necessari al filtro
            filtro.setMnemonicoTabellaConfronto(null);
            filtro.setDescrizioneTabellaConfronto(null);
            filtro.setDescrizioneCampoConfronto(null);
            filtro.setParametroConfronto(null);
            
            if (erroreListaValori != null) {
              //Messaggio di warning per presenza di un caso atipico
              messageKey = "warnings.genRic.filtro.operatoreINoNOT_IN.casoAnormaloDaVerificare";
              
              logger.warn(this.resBundleGenerale.getString(messageKey));
              this.aggiungiMessaggio(request, messageKey);
            }
          } else {
            // Errore: valore specificato nel formato diverso dal formato del
            // campo selezionato nel filtro
            // Si passa alla pagina di edit con i dati del form appena impostato.
            target = SalvaFiltroRicercaAction.ERROR_INSERT_FILTRO;
            
            // Il messaggio dipende dal valore assunto dalla variabile
            // 'erroreListaValori'. Vedi GeneResources.properties
            messageKey = "errors.genRic.filtro.operatoreINoNOT_IN." + erroreListaValori;
            
            logger.error(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
  
            //non effettuo il reset del valore che ha causato il mancato 
            //salvataggio del filtro per permettere al client di correggere la 
            //lista inserita in precedenza: quindi commento la riga seguente
            //filtro.setValoreConfronto(null);
            // set nel request dei parametri di fare l'edit
            request.setAttribute("filtroRicercaForm", filtro);
          }
        } else {
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
              // di salvare e comunico un masseggio di info
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
            target = SalvaFiltroRicercaAction.ERROR_INSERT_FILTRO;
            messageKey = "errors.genRic.filtroFormatoValoreDiversoDaCampo";
            //Non è possibile salvare il filtro: il confronto con la data odierna
            //richiede che il campo selezionato sia di tipo data. Selezionare un
            //campo di tipo data o cambiare il tipo di confronto
            
            logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
                UtilityStringhe.getPatternParametroMessageBundle(0), tipoCampo));
            this.aggiungiMessaggio(request, messageKey, tipoCampo);
  
            // reset del valore che ha causato il mancato salvataggio del filtro
            filtro.setValoreConfronto(null);
            // set nel request dei parametri di fare l'edit
            request.setAttribute("filtroRicercaForm", filtro);
          } else {
            // reset degli attributi non necessari al filtro
            filtro.setMnemonicoTabellaConfronto(null);
            filtro.setDescrizioneTabellaConfronto(null);
            filtro.setDescrizioneCampoConfronto(null);
            filtro.setParametroConfronto(null);
            // se non ci sono stati errori e il campo è tabellato 
            // setto il valore di confronto con quello senza apici
            if (campo.getCodiceTabellato() != null && !"".equals(campo.getCodiceTabellato())) 
              filtro.setValoreConfronto(valore);
          }
        }
      } else if (FiltroRicercaForm.TIPO_CONFRONTO_DATA_ODIERNA.equals(filtro.getTipoConfronto())) {
        if (logger.isDebugEnabled())
          logger.debug("Definizione di una condizione di filtro di confronto con" +
                " la data odierna.");
        
        //Verifico che il campo selezionato sia di tipo data:
        if (Campo.TIPO_DATA == campo.getTipoColonna()) {
          filtro.setValoreConfronto("Data odierna");
          
          // reset degli attributi non necessari al filtro
          filtro.setMnemonicoTabellaConfronto(null);
          filtro.setDescrizioneTabellaConfronto(null);
          filtro.setDescrizioneCampoConfronto(null);
          filtro.setParametroConfronto(null);
        } else {
          // Errore: il campo selezionato nel filtro e' di tipo diverso dal tipo data
          target = SalvaFiltroRicercaAction.ERROR_INSERT_FILTRO;
          messageKey = "errors.genRic.filtroCampoDiversoFormatoDaData";
          //messageKey = "errors.genRic.filtroFormatoValoreDiversoDaCampo";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);

          // reset del valore che ha causato il mancato salvataggio del filtro
          filtro.setValoreConfronto(null);
          // set nel request dei parametri di fare l'edit
          request.setAttribute("filtroRicercaForm", filtro);
        }
      } else if (FiltroRicercaForm.TIPO_CONFRONTO_UTENTE_CONNESSO.equals(filtro.getTipoConfronto())) {
        if (logger.isDebugEnabled())
          logger.debug("Definizione di una condizione di filtro di confronto con l'utente connesso.");
        
        //Verifico che il campo selezionato sia di tipo intero:
        if (Campo.TIPO_INTERO == campo.getTipoColonna()) {
          filtro.setValoreConfronto("Utente connesso");
          
          // reset degli attributi non necessari al filtro
          filtro.setMnemonicoTabellaConfronto(null);
          filtro.setDescrizioneTabellaConfronto(null);
          filtro.setDescrizioneCampoConfronto(null);
          filtro.setParametroConfronto(null);
        } else {
          // Errore: il campo selezionato nel filtro e' di tipo diverso dal tipo data
          target = SalvaFiltroRicercaAction.ERROR_INSERT_FILTRO;
          messageKey = "errors.genRic.filtroCampoDiversoFormatoDaIntero";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);

          // reset del valore che ha causato il mancato salvataggio del filtro
          filtro.setValoreConfronto(null);
          // set nel request dei parametri di fare l'edit
          request.setAttribute("filtroRicercaForm", filtro);
          
        }

      } else if (FiltroRicercaForm.TIPO_CONFRONTO_UFFICIO_INTESTATARIO.equals(filtro.getTipoConfronto())) {
        if (logger.isDebugEnabled())
          logger.debug("Definizione di una condizione di filtro di confronto con l'ufficio intestatario.");
        
        //Verifico che il campo selezionato sia di tipo stringa:
        if (Campo.TIPO_STRINGA == campo.getTipoColonna()) {
          filtro.setValoreConfronto(this.resBundleGenerale.getString("label.tags.uffint.singolo"));
          
          // reset degli attributi non necessari al filtro
          filtro.setMnemonicoTabellaConfronto(null);
          filtro.setDescrizioneTabellaConfronto(null);
          filtro.setDescrizioneCampoConfronto(null);
          filtro.setParametroConfronto(null);
        } else {
          // Errore: il campo selezionato nel filtro e' di tipo diverso dal tipo data
          target = SalvaFiltroRicercaAction.ERROR_INSERT_FILTRO;
          messageKey = "errors.genRic.filtroCampoDiversoFormatoDaStringa";
          
          String labelUffInt = StringUtils.lowerCase(
              this.resBundleGenerale.getString("label.tags.uffint.singolo"));
          
          logger.error(UtilityStringhe.replaceParametriMessageBundle(
              this.resBundleGenerale.getString(messageKey), new String[] { labelUffInt} ));
          this.aggiungiMessaggio(request, messageKey, labelUffInt);

          // reset del valore che ha causato il mancato salvataggio del filtro
          filtro.setValoreConfronto(null);
          // set nel request dei parametri di fare l'edit
          request.setAttribute("filtroRicercaForm", filtro);
        }
      }
      
      // Sabbadin 07/12/2011: aggiunta la possibilita' di filtrare non case
      // sensitive per i campi nota
      if ((Campo.TIPO_STRINGA != campo.getTipoColonna() && Campo.TIPO_NOTA != campo.getTipoColonna())
          && filtro.isNotCaseSensitive()) {
        filtro.setNotCaseSensitive(false);
        target = SalvaFiltroRicercaAction.ERROR_INSERT_FILTRO;
        messageKey = "errors.genRic.filtroNoCaseSensitiveSuCampoNonStringa";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
    }
    
    if (SalvaFiltroRicercaAction.ERROR_INSERT_FILTRO.equals(target)) {
      if (StringUtils.isNotEmpty((String) request.getSession().getAttribute(
          CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO))) {
        request.setAttribute("isAssociazioneUffIntAbilitata", "true");
      } else {
        request.setAttribute("isAssociazioneUffIntAbilitata", "false");
      }
    }

    return target;
  }

  /**
   * Metodo che verifica se la stringa passata per argomento può rappresentare
   * un caso anomalo o meno. In particolare se la stringa d'ingresso contiene 
   * una delle seguenti stringhe: "''", "'," ",'" o una delle possibili varianti
   * con un qualsiasi numero di spazi tra i due apici o tra l'apice e la virgola,
   * allora questa puo' rappresentare un caso anomalo che viene segnalato con un
   * messaggio di tipo warning al client
   * @param valore
   * @return Ritorna true se la stringa valore rappresenta un caso anomalo, 
   *         false altrimenti
   */
  private boolean isCasoAnomalo(String valore) {
    boolean result = false;
    if (valore.split("'[ ]*'").length > 1)
      result = true;
    else
      result = false;
    
    if (valore.split("'[ ]*,").length > 1)
      result = true;
    else
      result = false;
    
    if (valore.split(",[ ]*'").length > 1)
      result = true;
    else
      result = false;
    
    return result;
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

  /**
   * Inserisci in sessione l'operatore '()', che comporta l'inserimento prima
   * della parentesi aperta e dopo della parentesi chiusa
   * 
   * @param contenitore
   * @param posizione
   *        Indica la posizione in cui inserire i due operatori. Se negativo, i
   *        due opertaori vengono inseriti in coda all'elenco dei filtri
   */
  private void inserisciDoppiaParentesi(ContenitoreDatiRicercaForm contenitore,
      int posizione) {
    // Inserimento dell'operatore '()', che comporta l'inserimento prima della
    // parentesi
    // aperta e dopo della parentesi chiusa
    FiltroRicercaForm filtro = new FiltroRicercaForm();
    if (posizione < 0) {
      filtro.setOperatore(SqlElementoCondizione.STR_OPERATORE_PARENTESI_APERTA);
      contenitore.aggiungiFiltro(filtro);
      filtro = new FiltroRicercaForm();
      filtro.setOperatore(SqlElementoCondizione.STR_OPERATORE_PARENTESI_CHIUSA);
      contenitore.aggiungiFiltro(filtro);
    } else {
      filtro.setOperatore(SqlElementoCondizione.STR_OPERATORE_PARENTESI_APERTA);
      contenitore.aggiungiFiltro(posizione, filtro);
      filtro = new FiltroRicercaForm();
      filtro.setOperatore(SqlElementoCondizione.STR_OPERATORE_PARENTESI_CHIUSA);
      contenitore.aggiungiFiltro(posizione + 1, filtro);
    }
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menu a tab in fase di visualizzazione del dettaglio della lista degli
   * ordinamenti.
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
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_FILTRI);
    gestoreTab.setTabSelezionabili(new String[] {
        CostantiGenRicerche.TAB_DATI_GENERALI, CostantiGenRicerche.TAB_GRUPPI,
        CostantiGenRicerche.TAB_ARGOMENTI, CostantiGenRicerche.TAB_CAMPI,
        CostantiGenRicerche.TAB_JOIN, CostantiGenRicerche.TAB_PARAMETRI,
        CostantiGenRicerche.TAB_ORDINAMENTI, CostantiGenRicerche.TAB_LAYOUT});

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }

}
