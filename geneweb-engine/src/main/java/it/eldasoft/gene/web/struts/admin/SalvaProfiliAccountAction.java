/*
 * Created on 12-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.admin.ProfiliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;

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
 * visualizzazione delle associazioni tra i profili e l'utente in analisi -
 * di apertura della pagina per la modifica delle associazioni tra i profili
 * e l'utente in analisi
 *
 * @author Luca.Giacomazzo
 */
public class SalvaProfiliAccountAction extends AbstractActionBaseAdmin {

  /** Logger Log4J di classe */
  static Logger          logger = Logger.getLogger(SalvaProfiliAccountAction.class);

  /**
   * Reference alla classe di business logic per le operazioni gli
   * utenti/account e il gruppo
   */
  private ProfiliManager profiliManager;
  private AccountManager accountManager;
  /**
   * @param profiliManager
   *        accountManager da settare internamente alla classe.
   */
  public void setProfiliManager(ProfiliManager profiliManager) {
    this.profiliManager = profiliManager;
  }
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }
  
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  public ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // target di default per da modificare nel momento in cui si verificano 
    // dei problemi
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;
	String codApp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
	int livEvento = 1;
	String errMsgEvento = "";
	String login = "";
	ArrayList<String> arrayDescrMsg = new ArrayList<String>();
	try {
      // Lettura dal request l'ID_GRUPPO del gruppo in analisi
      int idAccount = Integer.parseInt((String) request.getParameter("idAccount"));

      ProfiliUtenteForm idProfiliAssociati = (ProfiliUtenteForm) form;
      this.profiliManager.updateAssociazioneProfiliAccount(idAccount,
          idProfiliAssociati.getCodiceProfilo(), codApp,
          this.bloccaGestioneGruppiDisabilitata(request, false, false));
      
      request.setAttribute("idAccount", new Integer(idAccount));

      Account account = this.accountManager.getAccountById(idAccount);
      login = account.getLogin();
      
      if(idProfiliAssociati.getCodiceProfilo() == null || idProfiliAssociati.getCodiceProfilo().length == 0){
        String descrMsg = "Utente "+ login +" ("+ idAccount +") non associato ad alcun profilo";
        arrayDescrMsg.add(descrMsg);
      }else{
        ArrayList<String[]> arrays = new ArrayList<String[]>();
        if(idProfiliAssociati.getCodiceProfilo().length > 19){
          arrays = splitArray(idProfiliAssociati.getCodiceProfilo(),19);
        }else{
          arrays.add(idProfiliAssociati.getCodiceProfilo());
        }
        for(int i=0;i<arrays.size();i++){
          String descrMsg = "Utente " + login + " (" + idAccount + ") associato ai profili: " + StringUtils.join(arrays.get(i), ", ");
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
    return mapping.findForward(target);
  }
  
  public static ArrayList<String[]> splitArray(String[] array, int length){
    ArrayList<String[]> arrayList = new ArrayList<String[]>();
    int i = 0;
    int y = 0;
    for(y=0;y<array.length;y=y+length){
      int size = array.length-y;
      String[] temp;
      if(size < length){
        temp = new String[size];
      }else{
        temp = new String[length];
      }
      for(i=0;i<temp.length;i++){
        temp[i] = array[i+y];
      }
      arrayList.add(temp);
    }
    return arrayList;
  }
  
}