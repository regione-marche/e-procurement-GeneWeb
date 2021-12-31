/*
 * Created on 27-apr-2007
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
import it.eldasoft.gene.web.struts.genric.datigen.TestataRicercaForm;
import it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.sql.comp.SqlElementoCondizione;

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
 * DispatchAction per la memorizzazione in sessione dei dati generali di una
 * ricerca base
 * 
 * @author Luca.Giacomazzo
 */
public class DatiGeneraliAction extends AbstractDispatchActionBaseGenRicercheBase {

  private final String SUCCESS_SALVA = "successSalva";
  
  static Logger logger = Logger.getLogger(DatiGeneraliAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere al metodo salvaDatiGen 
   * della DispatchAction
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSalvaDatiGen(){
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  /**
   * Metodo per salvare in sessione i dati generali di una ricerca base e poi 
   * per salvare su DB l'intera ricerca e passare poi alla pagina di dettaglio
   * di una ricerca base
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward salvaDatiGen(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response) 
  throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("editDatiGen: inizio metodo");

    // target di default
    String target = SUCCESS_SALVA;

    // L'oggetto TestataRicercaForm che ricevo dal form non presenta tutti gli 
    // attributi della testata, mentre quello presente in sessione e' gia' stato
    // opportunamente inizializzato a valori di default (vedi il metodo crea
    // della classe DettaglioRicercaAction.java.
    // Quindi bisogna aggiornare la testata presente in sessione prima di
    // effettuare l'insert su DB
    TestataRicercaForm testata = (TestataRicercaForm) form;
    
    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
            sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    
    contenitore.getTestata().setTipoRicerca(testata.getTipoRicerca());
    contenitore.getTestata().setNome(testata.getNome());
    contenitore.getTestata().setDescrizione(testata.getDescrizione());
    contenitore.getTestata().setProfiloOwner(
        (String) sessione.getAttribute(CostantiGenerali.PROFILO_ATTIVO));
    // Prima di salvare l'intera ricerca su DB controllo se sono state definite
    // almeno due condizioni di filtro. In caso positivo vado ad inserire 
    // l'operatore tra una condizione di filtro e la successiva
    Vector<FiltroRicercaForm> nuovoElencoFiltri = new Vector<FiltroRicercaForm>();
    FiltroRicercaForm filtroTmp = null;

    if(contenitore.getNumeroFiltri() > 1){
      for(int i = 0; i < contenitore.getNumeroFiltri(); i++){
        nuovoElencoFiltri.addElement(contenitore.getElencoFiltri().get(i));
        // Inserisco l'operatore AND prima della successiva condizione di filtro
        filtroTmp = new FiltroRicercaForm();
        filtroTmp.setOperatore(SqlElementoCondizione.STR_OPERATORE_LOGICO_AND);
        nuovoElencoFiltri.addElement(filtroTmp);
        filtroTmp = null;
      }
      //Rimuovo l'ultimo operatore di AND in eccesso dal nuovo elenco
      nuovoElencoFiltri.removeElementAt(nuovoElencoFiltri.size()-1);
      
      // Svuoto l'elenco filtri in sessione
      for(int i = contenitore.getNumeroFiltri() - 1; i >= 0; i--)
        contenitore.eliminaFiltro(i);
      
      // Copio il nuovo elenco in sessione
      for(int i = 0; i < nuovoElencoFiltri.size(); i++)
        contenitore.aggiungiFiltro((FiltroRicercaForm) nuovoElencoFiltri.get(i));
    }
    
    // Dopo il salvataggio su DB, devo caricare la pagina di dettaglio dei dati
    // generali di una ricerca base, percio' setto nel request il tab desiderato
    request.setAttribute("tab", CostantiWizard.CODICE_PAGINA_DATI_GENERALI);
    
    if (logger.isDebugEnabled()) logger.debug("editDatiGen: fine metodo");
    
    return mapping.findForward(target);
  }
}