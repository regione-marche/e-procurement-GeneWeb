/*
 * Created on 26-apr-2007
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
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicercheBase;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;

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
 * DispatchAction per la memorizzazione in sessione dell'argomento della ricerca
 * base
 * 
 * @author Luca.Giacomazzo
 */
public class ArgomentoAction extends AbstractDispatchActionBaseGenRicercheBase {

  private final String SUCCESS_SALVA = "successSalva";
  
  static Logger logger = Logger.getLogger(ArgomentoAction.class);
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action runAction
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSalvaArgomento() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  /**
   * Metodo per il salvataggio dell'argomento di una ricerca base
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward salvaArgomento(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("SalvaArgomento: inizio metodo");

    // target di default
    String target = SUCCESS_SALVA;

    TabellaRicercaForm tabellaRic = (TabellaRicercaForm) form;

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
            sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    
    if (contenitore.getNumeroTabelle() > 0) {
      if (!contenitore.getTestata().getEntPrinc().equals(tabellaRic.getAliasTabella())) {
        // Cancellazione di tutti gli elementi presenti nella ricerca solo nel caso
        // in cui l'argomento ricervuto nel form e' diverso da quello presente in 
        // sessione
  
        // Cancello tutti gli ordinamenti inseriti nel contenitore
        for(int i = contenitore.getNumeroOrdinamenti()-1; i >=0; i--)
          contenitore.eliminaOrdinamento(i);
        // Cancello tutti i filtri inseriti nel contenitore
        for(int i= contenitore.getNumeroFiltri() -1; i >= 0; i--)
          contenitore.eliminaFiltro(i);
        // Cancello tutti i campi inseriti nel contenitore
        for(int i= contenitore.getNumeroCampi() -1; i >= 0; i--)
          contenitore.eliminaCampo(i);
        // Cancello l'unica tabella inserita nel contenitore
        contenitore.eliminaTabella(0);
      
        TabellaRicercaForm tab = this.creaTestataRicercaForm(tabellaRic.getAliasTabella(),
            (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
        contenitore.aggiungiArgomento(tab);
        contenitore.getTestata().setEntPrinc(tab.getAliasTabella());
      }
    } else {
      TabellaRicercaForm tab = this.creaTestataRicercaForm(tabellaRic.getAliasTabella(),
          (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
      contenitore.aggiungiArgomento(tab);
      contenitore.getTestata().setEntPrinc(tab.getAliasTabella());      
    }

    if (logger.isDebugEnabled()) logger.debug("SalvaArgomento: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Metodo per la creazione dell'oggetto TabellaRicercaForm a partire dal nome
   * della tabella
   * 
   * @param nomeTabella
   * @param request
   * @return
   */
  private TabellaRicercaForm creaTestataRicercaForm(String nomeTabella,
      String moduloAttivo){
    
    String nomeSchemaViste = ConfigManager.getValore(
        CostantiGenerali.PROP_SCHEMA_VISTE_REPORT_BASE);
    
    // Inserimento nel contenitore della tabella relativa ai campi
    // ricevuti dal form
    DizionarioSchemi dizSchemi = DizionarioSchemi.getInstance();
    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    
    Tabella tabella1 = dizTabelle.getDaNomeTabella(nomeTabella);
    
    TabellaRicercaForm tabellaRicercaForm = new TabellaRicercaForm(); 
    tabellaRicercaForm.setAliasTabella(tabella1.getNomeTabella());
    tabellaRicercaForm.setDescrizioneSchema(dizSchemi.get(nomeSchemaViste).getDescrizione());
    tabellaRicercaForm.setDescrizioneTabella(tabella1.getDescrizione());
    tabellaRicercaForm.setId(null);
    tabellaRicercaForm.setMnemonicoSchema(nomeSchemaViste);
    tabellaRicercaForm.setMnemonicoTabella(tabella1.getCodiceMnemonico());
    tabellaRicercaForm.setNomeTabella(tabella1.getNomeTabella());
    tabellaRicercaForm.setProgressivo(0);
    tabellaRicercaForm.setVisibile(true);
    
    return tabellaRicercaForm;
  }
}