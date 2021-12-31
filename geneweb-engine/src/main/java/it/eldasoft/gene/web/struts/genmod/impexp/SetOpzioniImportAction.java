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
package it.eldasoft.gene.web.struts.genmod.impexp;

import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.genmod.ContenitoreDatiImport;
import it.eldasoft.gene.db.domain.genmod.TrovaModelli;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per settare nell'oggetto contenitoreDatiImport presente in sessione
 * il tipo di import da effettuare (update del modello esistente, insert con
 * nuovo titolo o insert semplice) e l'eventuale nuovo titolo del modello
 * 
 * @author Francesco.DeFilippis
 */
public class SetOpzioniImportAction extends ActionBaseNoOpzioni {

  /**   logger di Log4J   */
  static Logger logger = Logger.getLogger(SetOpzioniImportAction.class);
  
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }
  /**
   * Reference alla classe di business logic per l'accesso a W_MODELLI
   */
  private ModelliManager    modelliManager;

  /**
   * @param modelliManager
   *        modelliManager da settare internamente alla classe.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
    
    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    
    String target = null;
    String messageKey = null;
    
    ContenitoreDatiImport contenitoreDatiImport = (ContenitoreDatiImport)
        request.getSession().getAttribute(CostantiGenModelli.OGGETTO_DETTAGLIO);
    
    boolean configChiusa = request.getSession().getServletContext().getAttribute(
        CostantiGenerali.ATTR_CONFIGURAZIONE_CHIUSA).equals("1"); 
    boolean gruppiDisabilitati = this.bloccaGestioneGruppiDisabilitata(request, false, false);
    
    OpzioniImportModelloForm opzioniImport = (OpzioniImportModelloForm) form;
    
    if(opzioniImport.getTipoImport() != null && opzioniImport.getTipoImport().length() > 0){
      contenitoreDatiImport.setTipoImport(opzioniImport.getTipoImport());

      if(CostantiWizard.IMPORT_SOVRASCRIVI_PARZIALE.equals(opzioniImport.getTipoImport())){
        target = CostantiWizard.SUCCESS_FINE;
      } else {
        if(configChiusa){
          // in configurazione chiusa il modello in importazione va comunque pubblicato
          contenitoreDatiImport.setPubblicaNuovoModello(true);
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
       if(! this.esisteModello(
           (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO),
           (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO),
           opzioniImport.getNuovoTitolo())){
         contenitoreDatiImport.setNuovoTitoloModello(opzioniImport.getNuovoTitolo());
       } else {
         target = CostantiWizard.ERROR_IMPORT_MODELLI;
         messageKey = "errors.genmod.import.nuovoTitoloExist";
         logger.error(this.resBundleGenerale.getString(messageKey), null);
         this.aggiungiMessaggio(request, messageKey);
       }
    } else
      contenitoreDatiImport.setNuovoTitoloModello(null);

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
  private boolean esisteModello(String codApp, String codiceProfilo, String nuovoTitolo){
    boolean result = false;
    TrovaModelli trovaModelli = new TrovaModelli();
    trovaModelli.setNomeModello(nuovoTitolo);
    trovaModelli.setCodiceProfiloAttivo(codiceProfilo);
    trovaModelli.setNoCaseSensitive(false);
    try {
      List listaModelli = this.modelliManager.getModelli(trovaModelli);
      if(listaModelli != null && listaModelli.size() > 0)
        result = true;
    } catch(SqlComposerException sc){
      String messageKey = sc.getChiaveResourceBundle();
      logger.error(this.resBundleGenerale.getString(messageKey), sc);
      result = false;
    }
    return result;
  }
  
}