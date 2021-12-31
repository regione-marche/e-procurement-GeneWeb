/*
 * Created on 26-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.risultato;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.genric.CambiaTabAction;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
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
 * Azione per il controllo dei dati obbligatori in una ricerca prima
 * dell'estrazione della stessa nell'area applicativa 'Generatore Ricerche'.
 *
 * @author Luca Giacomazzo
 */
public class ControllaDatiRicercaAction extends ActionBaseNoOpzioni {

  private static final String FORWARD_CONTROLLI_FALLITI = "controlliFalliti";

  /** Logger Log4J di classe */
  static Logger               logger                    = Logger.getLogger(ControllaDatiRicercaAction.class);

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }

    // set del target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    String messageKey = null;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
        session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    // NOTA: i controlli indicati qui sotto prevedono l'apertura del tab
    // mancante di dati obbligatori per l'apertura del report.

    boolean continua = true;

    if (CostantiGenRicerche.REPORT_SQL != contenitore.getTestata().getFamiglia().intValue()) { 
      if (contenitore.getNumeroTabelle() == 0) {
  
        messageKey = "errors.genRic.estraiRicerca.mancaTabella";
        if (logger.isDebugEnabled()) {
          logger.debug(this.resBundleGenerale.getString(messageKey));
        }
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("tab", CambiaTabAction.CODICE_TAB_ARGOMENTI);
        continua = false;
        target = FORWARD_CONTROLLI_FALLITI;
      } else {
        Vector<TabellaRicercaForm> elencoTabelle = contenitore.getElencoArgomenti();
        boolean tabelleVisibili = false;
        int indice = 0;
        // ciclo per verificare se almeno una tra le tabelle presenti
        // nell'elencoTabelle sia visibile
        while (indice < elencoTabelle.size() && !tabelleVisibili) {
          tabelleVisibili = (elencoTabelle.elementAt(indice)).getVisibile();
          indice++;
        }
  
        if (!tabelleVisibili) {
          messageKey = "errors.genRic.estraiRicerca.tabelleNonVisibili";
          if (logger.isDebugEnabled()) {
            logger.debug(this.resBundleGenerale.getString(messageKey));
          }
          
          this.aggiungiMessaggio(request, messageKey);
          request.setAttribute("tab", CambiaTabAction.CODICE_TAB_ARGOMENTI);
          continua = false;
          target = FORWARD_CONTROLLI_FALLITI;
        }
      }
  
      // se non c'è almeno un campo da estrarre, presento un errore ed apro il tab
      // relativo ai campi
      if (continua && (contenitore.getNumeroCampi() == 0)) {
        messageKey = "errors.genRic.estraiRicerca.mancaCampo";
        logger.debug(this.resBundleGenerale.getString(messageKey));
  
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("tab", CambiaTabAction.CODICE_TAB_CAMPI);
        continua = false;
        target = FORWARD_CONTROLLI_FALLITI;
      }
  
      // L.G. 07/06/2007: controllo del numero di join attive. Se il numero di join
      // e' minore del numero di tabelle - 1, allora blocco l'esecuzione della
      // ricerca, altrimenti continuo.
      // S.S. 05/04/2012: controllo se riesco a recuperare delle join dai filtri: se riesco
      // a raggiungere il numero di join allora consento l'esecuzione altrimenti blocco
      if (continua && contenitore.getNumeroGiunzioni() < contenitore.getNumeroTabelle() - 1) {
        Set<String> setJoinEsplicitateComeFiltro = new HashSet<String>();
        for (Iterator<FiltroRicercaForm> iterator = contenitore.getElencoFiltri().iterator(); iterator.hasNext();) {
          FiltroRicercaForm filtro = iterator.next();
          if (FiltroRicercaForm.TIPO_CONFRONTO_CAMPO.equals(filtro.getTipoConfronto())
              && !setJoinEsplicitateComeFiltro.contains(filtro.getAliasTabella() + "-" + filtro.getAliasTabellaConfronto())
              && !setJoinEsplicitateComeFiltro.contains(filtro.getAliasTabellaConfronto() + "-" + filtro.getAliasTabella())) {
            setJoinEsplicitateComeFiltro.add(filtro.getAliasTabella() + "-" + filtro.getAliasTabellaConfronto());
          }
        }
        if (contenitore.getNumeroGiunzioni() + setJoinEsplicitateComeFiltro.size() < contenitore.getNumeroTabelle() - 1) {
          // se dopo aver aggiunto le join esplicitate nei filtri continuo a rimanere sotto il numero minimo di join, allora vuol dire che mi
          // mancano ancora altre join
          messageKey = "errors.genRic.estraiRicerca.numeroCriticoGiunzioniAttive";
          logger.debug(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
          request.setAttribute("tab", CambiaTabAction.CODICE_TAB_JOIN);
          continua = false;
          target = FORWARD_CONTROLLI_FALLITI;
        }
      }
      // L.G. 07/06/2007: fine modifica
      
      /*
      if (continua) {
        OpzioniUtente opzioniUtente = new OpzioniUtente(profiloUtente.getFunzioniUtenteAbilitate());
        // L.G. 16/03/2015: se l'utente puo' eseguire solo report personali e la gestione ufficio 
        // intestatario e' attiva e sul report il filtro per ufficio intestatario e' disattivo,
        // oppure se l'utente puo' eseguire solo report personali e il filtro livello utente e'
        // disattivo, allora si blocca l'esecuzione del report, con un messaggio di incompatbilita'
        // tra la definizione del report e i privielgi dell'utente.
        
        if ((opzioniUtente.isOpzionePresente(CostantiGeneraliAccount.SOLO_REPORT_PERSONALI_GENRIC) &&
              StringUtils.isNotEmpty((String) request.getSession().getAttribute(
                CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO)) &&
                  contenitore.getTestata().getFiltroUfficioIntestatarioEscluso() == true )
            || (opzioniUtente.isOpzionePresente(CostantiGeneraliAccount.SOLO_REPORT_PERSONALI_GENRIC) &&
                contenitore.getTestata().getFiltroUtente() == false))  {
          
          messageKey = "errors.genRic.estraiRicerca.reportPersonale.nonEseguibile";
          
          String labelUffInt = StringUtils.lowerCase(
              this.resBundleGenerale.getString("label.tags.uffint.singolo"));
          
          logger.debug(UtilityStringhe.replaceParametriMessageBundle(
              this.resBundleGenerale.getString(messageKey),
              new String[] {". (Causa dell'errore: il report deve avere il filtro livello utente " +
              	"attivo ed il filtro per " + labelUffInt +
              	" non escluso, potendo l'utente eseguire solo report personali. " +
              	"Modificare il report da utente con gestione report completa)"}));
  
          this.aggiungiMessaggio(request, messageKey, "");
          request.setAttribute("tab", CambiaTabAction.CODICE_TAB_DATI_GENERALI);
          continua = false;
          target = FORWARD_CONTROLLI_FALLITI;
        }
      }
      
      errors.genRic.estraiRicerca.reportPersonale.nonEseguibile=
        Il report non \u00e8 estraibile perch\u00e9 la definizione \u00e8 incompatibile con i 
        privilegi dell'utente. Contattare un amministratore per correggere il report
       */
      
      if (continua) {
        // L.G. 03/03/2015: se la gestione uffici intestatari e' attiva ed nel report e'
        // attivo il filtro per ufficio intestatario, allora si controlla se almeno uno
        // degli argomenti selezionati per la ricerca e' in relazione con la UFFINT.
        if (StringUtils.isNotEmpty((String) request.getSession().getAttribute(
            CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO)) && 
            contenitore.getTestata().getFiltroUfficioIntestatario()) {
          
          DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
          Vector<TabellaRicercaForm> elencoTabelle = contenitore.getElencoArgomenti();
          boolean esisteTabellaInRelazioneUffInt = false;
  
          for (int i = 0; i < contenitore.getNumeroTabelle() && !esisteTabellaInRelazioneUffInt; i++) {
            TabellaRicercaForm tabellaRic = elencoTabelle.get(i);
            if (dizTabelle.getDaNomeTabella("UFFINT").getLegameTabelle(tabellaRic.getNomeTabella()).length > 0) {
              esisteTabellaInRelazioneUffInt = true;
            }
          }
          if (! esisteTabellaInRelazioneUffInt) {
            messageKey = "errors.genRic.salvaRicerca.filtroUfficioIntestatario.nonAttivabile";
            String labelUffInt = StringUtils.lowerCase(
                this.resBundleGenerale.getString("label.tags.uffint.singolo"));
          
            logger.debug(UtilityStringhe.replaceParametriMessageBundle(
                this.resBundleGenerale.getString(messageKey),
                new String[] { labelUffInt }));
            this.aggiungiMessaggio(request, messageKey, labelUffInt);
            request.setAttribute("tab", CambiaTabAction.CODICE_TAB_FILTRI);
            continua = false;
            target = FORWARD_CONTROLLI_FALLITI;
          }
        }
      }
    }
    
    // se si arriva qui, i controlli dei dati obbligatori sono tutti OK
    if (logger.isDebugEnabled()) {
      logger.debug("I dati obbligatori nella ricerca sono presenti");
    }
    
    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }
    return mapping.findForward(target);
  }
}