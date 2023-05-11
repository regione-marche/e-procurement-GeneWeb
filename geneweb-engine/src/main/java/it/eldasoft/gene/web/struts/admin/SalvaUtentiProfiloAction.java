/*
 * Created on 11-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.admin.ProfiliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action che controlla le operazioni - di apertura della pagina per la
 * visualizzazione delle associazioni tra gli utenti ed il gruppo in analisi -
 * di apertura della pagina per la modifica delle associazioni tra gli utenti ed
 * il gruppo in analisi
 * 
 * @author Luca.Giacomazzo
 */
public class SalvaUtentiProfiloAction extends AbstractActionBaseAdmin {

  /** Logger Log4J di classe */
  static Logger          logger = Logger.getLogger(SalvaUtentiProfiloAction.class);

  /**
   * Reference alla classe di business logic per le operazioni gli
   * utenti/account e il profilo
   */
  private ProfiliManager profiliManager;

  /**
   * @param profiliManager
   *        profiliManager da settare internamente alla classe.
   */
  public void setProfiliManager(ProfiliManager profiliManager) {
    this.profiliManager = profiliManager;
  }

  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  public ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // è un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    //if (this.bloccaGestioneGruppiDisabilitata(request))
    //  return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    
    int livEvento = 1;
    String errMsgEvento = "";
    
    // target di default per l'azione 'updateProfiloConFunzionalita', da
    // modificare nel momento in cui si verificano dei problemi
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    String codApp = (String) request.getSession().getAttribute(
        CostantiGenerali.MODULO_ATTIVO);
    
    ArrayList<String> arrayDescrMsg = new ArrayList<String>();
    
    try {
      // Lettura dal request del codice del profilo in analisi
      String codPro = request.getParameter("codPro");

      UtentiProfiloForm idAccountAssociati = (UtentiProfiloForm) form;

      this.profiliManager.updateAssociazioneAccountProfilo(codPro, codApp,
          idAccountAssociati.getIdAccount(),
          this.bloccaGestioneGruppiDisabilitata(request, false, false));
      
      if(idAccountAssociati.getIdAccount() == null || idAccountAssociati.getIdAccount().length == 0){
        String descrMsg = "Profilo "+ codPro +" non associato ad alcun utente";
        arrayDescrMsg.add(descrMsg);
      }else{
        ArrayList<String[]> arrays = new ArrayList<String[]>();
        if(idAccountAssociati.getIdAccount().length > 30){
          arrays = SalvaProfiliAccountAction.splitArray(idAccountAssociati.getIdAccount(),30);
        }else{
          arrays.add(idAccountAssociati.getIdAccount());
        }
        for(int i=0;i<arrays.size();i++){
          String descrMsg = "Profilo " + codPro + " associato agli utenti con id: " + StringUtils.join(arrays.get(i), ", ");
          arrayDescrMsg.add(descrMsg);
        }
      }
    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      livEvento = 3;
      errMsgEvento = this.resBundleGenerale.getString(messageKey);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      livEvento = 3;
      errMsgEvento = this.resBundleGenerale.getString(messageKey);
      this.aggiungiMessaggio(request, messageKey);
    }finally{
      for(int i=0;i<arrayDescrMsg.size();i++){
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(livEvento);
        logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_CHANGE_PROFILO);
        logEvento.setDescr(arrayDescrMsg.get(i));
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      }
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }
  
}