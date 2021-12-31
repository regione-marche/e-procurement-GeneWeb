/*
 * Created on 09-mag-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.base.wizard;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.DettaglioRicercaAction;
import it.eldasoft.gene.web.struts.genric.base.CambiaTabRicercaBaseAction;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.profiles.cache.DizionarioLivelli;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * DispatchAction che estende DettaglioRicercaAction per la ridefinizione del 
 * metodo salva, al fine di effettuare il salvataggio di un report base costruito
 * con la procedura guidata, con la possibilita' di gestire gli errori e i 
 * relativi forward  
 * 
 * @author Luca.Giacomazzo
 */
public class SalvaWizardBaseAction extends DettaglioRicercaAction {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(SalvaWizardBaseAction.class);
  
  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL;
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere al metodo salva 
   * della Action
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSalva(){
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  public ActionForward salva(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response) 
  throws IOException, ServletException {
    if (logger.isDebugEnabled()) {
      logger.debug("salva: inizio metodo");
    }

    String tabOrigine = null; 
    
    if(request.getParameter("pageFrom") != null)
      tabOrigine = request.getParameter("pageFrom");
    else if(request.getAttribute("pageFrom") != null)
      tabOrigine = (String) request.getAttribute("pageFrom");
    
    // target di default
    String target = DettaglioRicercaAction.SUCCESS_SALVA;

    //L.G 28/05/2007: modifica per attivare in modo automatico il filtro utente
    //in base alla tabella utilizzata nella ricerca base. Il filtro livello utente
    //e' attivabile in modo automatico e' solo agli l'utente che possono creare 
    //esclusivamente report base personali. Se la tabella selezionata e' in 
    //relazione con il livello utente, allora in automatico viene attivato il
    //filtro per livello utente, settando il relativo campo a true
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
        request.getSession().getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        "profiloUtente");
    OpzioniUtente opzioniUtente = new OpzioniUtente(
        profiloUtente.getFunzioniUtenteAbilitate());
    
    if (opzioniUtente.isOpzionePresente(
                CostantiGeneraliAccount.SOLO_REPORT_PERSONALI_GENRIC)) {

      // Si controlla se nella lista della tabelle selezionate per la ricerca ne
      // esiste almeno una che e' in relazione con l'id utente. Se si, allora
      // setto l'attributo della testata filtroIdUtente a true, altrimenti lo setto
      // a false
      DizionarioLivelli dizLivelli = DizionarioLivelli.getInstance();
      boolean esisteTabellaInRelazioneIdUtente = false;
      for (int i=0; i < contenitore.getNumeroTabelle(); i++) {
        if (dizLivelli.isFiltroLivelloPresente(contenitore.estraiTabella(i).getNomeTabella())) {
          esisteTabellaInRelazioneIdUtente = true;
        }
      }
      if (esisteTabellaInRelazioneIdUtente) {
        contenitore.getTestata().setFiltroUtente(true);
      } else {
        contenitore.getTestata().setFiltroUtente(false);
      }
      
      // L.G. 09/03/2015: in questo caso l'utente puo' creare solo report personali:
      // se e' attiva la gestione uffici intestatari e l'argomento selezionato e' in 
      // relazione con la tabella UFFINT.GENE, allora se setta il filtro per ufficio
      // intestatario, altrimenti non lo si setta.
      //if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_UFFINT_ABILITATI))) {
      if (StringUtils.isNotEmpty((String) request.getSession().getAttribute(
          CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO))) {
        DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
        
        for (int i=0; i < contenitore.getNumeroTabelle(); i++) {
          if (dizTabelle.getDaNomeTabella("UFFINT").getLegameTabelle(
              contenitore.estraiTabella(i).getNomeTabella()).length > 0
              ||
              dizTabelle.getDaNomeTabella(
                  contenitore.estraiTabella(i).getNomeTabella()).getLegameTabelle(
                      "UFFINT").length > 0) {
            contenitore.getTestata().setFiltroUfficioIntestatario(true);
          } else {
            contenitore.getTestata().setFiltroUfficioIntestatario(false);
          }
        }
      }
    } else {
      contenitore.getTestata().setFiltroUtente(false);
      contenitore.getTestata().setFiltroUfficioIntestatario(false);
    }
    // L.G. 28/05/2007: fine modifica
    
    String appoggio = this.memorizza(request, tabOrigine);
    if (appoggio != null)
      target = appoggio;

    if (DettaglioRicercaAction.SUCCESS_SALVA.equals(target) ||
        DettaglioRicercaAction.REPORT_BASE_SUCCESS_SALVA.equals(target)) {
      // set del tab di destinazione dopo l'operazione di insert/update:
      // si ritorna al tab che da cui e' stato scatenata l'azione.
      request.setAttribute("tab", CambiaTabRicercaBaseAction.CODICE_TAB_DATI_GENERALI);
      
      //Inserisco nel request un attributo che permettera' alla pagina di destinazione
      //di lanciare immediatamente l'estrazione della ricerca
      request.setAttribute("avviaEstrazione", "1");
      
    } else {
      if (DettaglioRicercaAction.SUCCESS_ERROR_SALVA.equals(target)) {
        target = DettaglioRicercaAction.REPORT_BASE_SUCCESS_ERROR_SALVA;
        request.setAttribute("pageFrom", tabOrigine);
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("salva: fine metodo");
    }
    
    return mapping.findForward(target);
  }
}