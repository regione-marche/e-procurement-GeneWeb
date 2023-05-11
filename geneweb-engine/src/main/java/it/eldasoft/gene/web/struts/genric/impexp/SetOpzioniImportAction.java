/*
 * Created on 23-ago-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.impexp;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiImport;
import it.eldasoft.gene.db.domain.genric.TrovaRicerche;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.sql.comp.SqlComposerException;

/**
 * Action per settare nell'oggetto contenitoreDatiImport presente in sessione
 * il tipo di import da effettuare (update del report esistente, insert con
 * nuovo titolo o insert semplice) e l'eventuale nuovo titolo del report
 * 
 * @author Luca.Giacomazzo
 */
public class SetOpzioniImportAction extends ActionBaseNoOpzioni {

  /**   logger di Log4J   */
  static Logger logger = Logger.getLogger(SetOpzioniImportAction.class);
  
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }
  /**
   * Reference alla classe di business logic per l'accesso a W_RICERCHE
   */
  private RicercheManager    ricercheManager;

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
    
    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    
    String target = null;
    String messageKey = null;
    
    ContenitoreDatiImport contenitoreDatiImport = (ContenitoreDatiImport)
        request.getSession().getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    
    boolean configChiusa = request.getSession().getServletContext().getAttribute(
        CostantiGenerali.ATTR_CONFIGURAZIONE_CHIUSA).equals("1"); 
    boolean gruppiDisabilitati = this.bloccaGestioneGruppiDisabilitata(request, false, false);
    
    OpzioniImportForm opzioniImport = (OpzioniImportForm) form;
    
    if(opzioniImport.getTipoImport() != null && opzioniImport.getTipoImport().length() > 0){
      contenitoreDatiImport.setTipoImport(opzioniImport.getTipoImport());

      if(CostantiWizard.IMPORT_SOVRASCRIVI_PARZIALE.equals(opzioniImport.getTipoImport())){
        target = CostantiWizard.SUCCESS_FINE;
      } else {
        if(configChiusa){
          // in configurazione chiusa il report in importazione va comunque pubblicato
          contenitoreDatiImport.setPubblicaReport(true);
          if(gruppiDisabilitati)
            target = CostantiWizard.SUCCESS_FINE;
          else
            target = CostantiWizard.SUCCESS_EDIT_GRUPPI;
        } else {
          target = CostantiWizard.SUCCESS_DOMANDA_PUBBLICA;
        }
      }
    } else {
      contenitoreDatiImport.setTipoImport(null);
    }

    if(opzioniImport.getNuovoTitolo() != null &&
       opzioniImport.getNuovoTitolo().length() > 0){
       if(! this.esisteReport(
           (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO),
           (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO),
           opzioniImport.getNuovoTitolo())){
         contenitoreDatiImport.setNuovoTitoloReport(opzioniImport.getNuovoTitolo());
       } else {
         target = CostantiWizard.ERROR_IMPORT_REPORT;
         messageKey = "errors.genric.import.nuovoTitoloExist";
         logger.error(this.resBundleGenerale.getString(messageKey), null);
         this.aggiungiMessaggio(request, messageKey);
       }
    } else
      contenitoreDatiImport.setNuovoTitoloReport(null);

    request.setAttribute("pageFrom", CostantiWizard.CODICE_PAGINA_DATI_GENERALI);
    
    if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

  /** Metodo per verificare che il nuovo titolo del report non 
   * 
   * @param codApp
   * @param nuovoTitolo
   * @return Ritorna true se la coppia codApp-nuovoTitolo esiste nella base
   *         dati, false altrimenti
   */
  private boolean esisteReport(String codApp, String codiceProfilo, String nuovoTitolo){
    boolean result = false;
    TrovaRicerche trovaRicerche = new TrovaRicerche();
    trovaRicerche.setNomeRicerca(nuovoTitolo);
    trovaRicerche.setCodiceApplicazione(codApp);
    trovaRicerche.setProfiloOwner(codiceProfilo);
    trovaRicerche.setNoCaseSensitive(false);
    try {
      List<?> listaRicerche = this.ricercheManager.getRicerche(trovaRicerche, true);
      if(listaRicerche != null && listaRicerche.size() > 0)
        result = true;
    } catch(SqlComposerException sc){
      String messageKey = sc.getChiaveResourceBundle();
      logger.error(this.resBundleGenerale.getString(messageKey), sc);
      result = false;
    }
    return result;
  }
  
}