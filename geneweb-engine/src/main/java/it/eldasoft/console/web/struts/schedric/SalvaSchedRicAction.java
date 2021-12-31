/*
 * Created on 25-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.web.struts.schedric;

import it.eldasoft.console.bl.schedric.SchedRicManager;
import it.eldasoft.console.db.domain.schedric.SchedRic;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * ActionBase che implementa il salvataggio di una schedulazione
 * viene utilizzato sia per salvare i dati generali che queli di schedulazione che nel wizard
 * 
 * @author Francesco De Filippis
 */
public class SalvaSchedRicAction extends ActionBaseNoOpzioni {

  private final String SUCCESS_SALVA = "successSalva";

  /** Logger Log4J di classe */
  static Logger        logger                      = Logger.getLogger(SalvaSchedRicAction.class);

  protected SchedRicManager schedRicManager;

  /**
   * @param schedRicManager
   *        The schedRicManager to set.
   */
  public void setSchedRicManager(SchedRicManager schedRicManager) {
    this.schedRicManager = schedRicManager;
  }

  public ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // target di default
    String target = SUCCESS_SALVA;

    String messageKey = null;
    SchedRicForm schedRicForm = null;
    
    try {
      // Effettuiamo il salvataggio prendendo il form e preparandolo con la
      // getDatiPerModel al salvataggio, nessun controllo particolare si deve
      // fare, nel Manager viene calcolata la data prox esec e la chiave
      // idSchedRic e si effettua il salvataggio

      schedRicForm = (SchedRicForm) form;
      SchedRic schedRic = schedRicForm.getDatiPerModel();

      if (schedRic.getIdSchedRic() == null) {
        ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        schedRic.setOwner(profiloUtente.getId());
        //schedRic.setEsecutore(profiloUtente.getId());
        schedRic.setProfiloOwner((String) request.getSession().getAttribute(
            CostantiGenerali.PROFILO_ATTIVO));
        
        schedRicForm.setOwner(profiloUtente.getId());
        //schedRicForm.setEsecutore(profiloUtente.getId());
        
        if(checkOraAvvioSched(schedRic)){
          int idSchedRic = this.schedRicManager.insertSchedulazioneRicerca(schedRic);
          schedRicForm.setIdSchedRic(new Integer(idSchedRic));
        } else {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE.concat("Wizard");
          messageKey = "errors.schedRic.unica.oraAvvioInvalida";
          logger.error(this.resBundleGenerale.getString(messageKey), null);
          this.aggiungiMessaggio(request, messageKey);
        }
      } else {
        if(checkOraAvvioSched(schedRic)){        
          this.schedRicManager.updateSchedulazioneRicerca(schedRic);
        } else {
          // Set dell'attributo idSchedRic per tornare all'edit della
          // schedulazione
          request.setAttribute("idSchedRic", schedRic.getIdSchedRic());
          
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.schedRic.unica.oraAvvioInvalida";
          logger.error(this.resBundleGenerale.getString(messageKey), null);
          this.aggiungiMessaggio(request, messageKey);
        }
      }
      
      if(messageKey ==  null){
        HttpSession session = request.getSession();
        request.setAttribute("idSchedRic", schedRic.getIdSchedRic());
        // tolgo l'oggetto form dalla sessione
        session.removeAttribute(CostantiSchedRic.OGGETTO_DETTAGLIO);
      }
    } catch (DataIntegrityViolationException div){
      if(schedRicForm.getIdSchedRic() == null)
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE.concat("Wizard");
      else
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.schedRic.nomeSchedRicDuplicato";
      logger.error(this.resBundleGenerale.getString(
          UtilityStringhe.replaceParametriMessageBundle(messageKey,
              new String[]{schedRicForm.getNome()})), div);
      this.aggiungiMessaggio(request, messageKey, schedRicForm.getNome());      
    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Metodo per il controllo se l'istante di avvio della schedulazione da 
   * eseguire una sola volta e' posteriore all'ora attuale
   * 
   * @param schedRic
   * @return Ritorna true se il controllo e' stato superato, false altrimenti
   */
  private boolean checkOraAvvioSched(SchedRic schedRic) {
    boolean result = true;
    
    // Se l'istante di avvio e' precedente o coincide all'ora attuale, allora si 
    // ritorna a video con un messaggio di errore
    if(CostantiSchedRic.UNICA.equals(schedRic.getTipo())){
      GregorianCalendar dataOraAttuale = new GregorianCalendar();
      GregorianCalendar dataOraAvvio = new GregorianCalendar(
          schedRic.getDataPrimaEsec().getYear() + 1900,
          schedRic.getDataPrimaEsec().getMonth(),
          schedRic.getDataPrimaEsec().getDate(),
          schedRic.getOraAvvio(), schedRic.getMinutoAvvio());
      
      if(!dataOraAvvio.after(dataOraAttuale))
        result = false;
    }
    return result;
  }
  
}