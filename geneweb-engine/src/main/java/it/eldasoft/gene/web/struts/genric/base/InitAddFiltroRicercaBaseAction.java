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
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicercheBase;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;

import java.io.IOException;
import java.util.Iterator;
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
 * Action per l'apertura della pagina per l'aggiunta di un filtro ad una ricerca
 * base: caricamento dell'elenco ridotto degli operatori e dei campi selezionati
 * nella ricerca.
 *  
 * @author Luca Giacomazzo
 */
public class InitAddFiltroRicercaBaseAction extends AbstractActionBaseGenRicercheBase {

  private static final String ERROR_ADD_FORWARD = "errorInitAdd";
  
  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(InitAddFiltroRicercaBaseAction.class);
// F.D. 07/05/07 la funzione è definita nella classe AbstractActionBaseGenRicercheBase 
//  /**
//   * Funzione che restituisce le opzioni per accedere alla action runAction
//   * @return opzioni per accedere alla action
//   */
//  public CheckOpzioniUtente getOpzioniRunAction() {
//    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
//  }
  
  /**
   * Inserisce nella request l'elenco degli operatori, delle tabelle e l'elenco dei campi
   * associati a tali tabelle per inserire un filtro.
   *  
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
         sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    
    //Vector elencoCampiForm = contenitore.getElencoCampi();
    
    DizionarioCampi dizCampi = DizionarioCampi.getInstance();
    
    Vector<CampoRicercaForm> elencoCampi = contenitore.getElencoCampi();
    String mnemonicoCampo = null;
    Campo campo = null;
    Vector<String> elencoTabellatiCampi = new Vector<String>(elencoCampi.size());
    
    for (Iterator<CampoRicercaForm> iter = elencoCampi.iterator(); iter.hasNext();) {
      CampoRicercaForm element = (CampoRicercaForm) iter.next();
      mnemonicoCampo = (String) element.getMnemonicoCampo();
      campo = dizCampi.get(mnemonicoCampo);
      elencoTabellatiCampi.addElement(campo.getCodiceTabellato());
    }
    
    if(elencoTabellatiCampi.size() > 0){
      request.setAttribute("elencoTabellatiCampi", elencoTabellatiCampi);
      request.setAttribute("elencoCampi", elencoCampi);
      request.setAttribute("elencoOperatori", CostantiGenRicerche.CBX_OPERATORI_VALUE_REPORT_BASE);
      request.setAttribute("elencoOperatoriLabel", CostantiGenRicerche.CBX_OPERATORI_LABEL_REPORT_BASE);

      this.setMenuTab(request);
      
    } else {
      // Nessun campo e' definito, quindi impossibile definire alcuna 
      // condizione di filtro  per la ricerca
      target = InitAddFiltroRicercaBaseAction.ERROR_ADD_FORWARD;
      String messageKey = "errors.genRic.ricercaBase.noCampiDefFiltri";
      if(logger.isDebugEnabled())
        logger.debug(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }
    
    //set nel request del parameter per disabilitare la navigazione in fase di editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);
    
    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    
    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio dei Dati Generali di
   * una ricerca base
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
    gestoreTab.setTabSelezionabili(null);

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }
}