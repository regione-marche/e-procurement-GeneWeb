/*
 * Created on 30/lug/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.web.struts.login;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.admin.TokenContent;
import it.eldasoft.gene.db.domain.admin.UserInfoResponse;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.AdminAccessForm;
import it.eldasoft.gene.web.struts.RecuperaPasswordAction;
import it.eldasoft.gene.web.struts.login.portoken.PortokenClient;
import it.eldasoft.utils.properties.ConfigManager;
import it.maggioli.eldasoft.mtoken.Mtoken;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;


public class AdminAccessAction extends ActionBaseNoOpzioni {

  private static final long serialVersionUID = -590160936911227418L;
  
  private static final String PORTOKEN        = "portoken";
  private static final String MTOKEN        = "mtoken";
  
  private Logger         logger            = Logger.getLogger(RecuperaPasswordAction.class);

  @SuppressWarnings("unchecked")
  public String loginMtoken(ActionMapping mapping, ActionForm form, HttpServletRequest request) {
      String target = CostantiGeneraliStruts.FORWARD_OK;
      
      AdminAccessForm adminAccessForm = (AdminAccessForm) form;
      
      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(1);
      logEvento.setCodEvento("LOGIN");
      
      String messageKey = "";
      String utente = null;
      String email = null;
      
      try {
          // carica il certificato "ca-admin.maggioli.it.crt" da ...\WEB-INF\
          // [...\PortaleAppalti\WEB-INF\...]
          InputStream caFile =  request
                  .getSession()
                  .getServletContext()
                  .getResourceAsStream(
                          CostantiGenerali.PATH_WEBINF
                                  + "ca-admin.maggioli.it.crt");
      
          Mtoken client = new Mtoken(caFile);

          if(StringUtils.isNotEmpty(adminAccessForm.getCertificatoText())) {
              client.getMtokenCredentials(adminAccessForm.getCertificatoText());
          } else {
              FormFile ff = adminAccessForm.getSelezioneFile();
              client.getMtokenCredentials(ff.getFileData());
          }
          
          if(client.isEsito()) {
              utente = StringUtils.stripToNull(client.getUtente());
              email = StringUtils.stripToNull(client.getEmail());
              
              target = CostantiGeneraliStruts.FORWARD_OK;
              
              logEvento.setDescr(
                      "Login mediante Mtoken da parte dell'amministratore impersonato da " + utente + " (" + email + ") " +
                      "con motivazione di accesso o ticket: " + adminAccessForm.getMotivazione() + " " +
                      "con SHA1 del certificato " + client.getSha1() + "");
              
          }else{
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            messageKey = "errors.mtoken.autenticazioneFallita";
            logEvento.setLivEvento(3);
            String descr = "Tentativo di login dell'amministratore non validato";
            if(utente!=null && !"".equals(utente)){
              descr+="(" + utente + ").";
            }
            logEvento.setDescr(descr);
            this.aggiungiMessaggio(request, messageKey, "Certificato non valido");
            logEvento.setErrmsg("Il certificato M-token inserito è stato rifiutato");
          } 
      } catch (Throwable ex) {
        logEvento.setLivEvento(3);
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.adminAccess.autenticazioneFallita";
        this.aggiungiMessaggio(request, messageKey, ex.getMessage());
        request.setAttribute("metodo", "mtoken");
        String descr = "Tentativo di login dell'amministratore non validato";
        if(utente!=null){
          descr+="(" + utente + ").";
        }
        logEvento.setDescr(descr);
        logEvento.setErrmsg(ex.getMessage());
        logger.error(ex.getMessage(), ex);
      }
      
      LogEventiUtils.insertLogEventi(logEvento);

      return target;
  }
  

  @SuppressWarnings("unchecked")
  public String loginPortoken(ActionMapping mapping, ActionForm form, HttpServletRequest request){
      String target = CostantiGeneraliStruts.FORWARD_OK;
      
      AdminAccessForm adminAccessForm = (AdminAccessForm) form;LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(1);
      logEvento.setCodEvento("LOGIN");
      
      String domainMail = adminAccessForm.getEmailDominio();
      String domainPwd = adminAccessForm.getPasswordDominio();
      String motivazione = adminAccessForm.getMotivazione();
      PortokenClient client = new PortokenClient();
      //String PORT_TOKEN_URL = "https://portoken.maggiolicloud.it/api/jsonws/mggl.token/generate-token/id-utente/-/tipologia-id/-/cod-sap-cliente/-/cod-sap-area/-/cod-sap-famiglia/-/cliente/-/ticket-id/-/stato/-/scadenza/-/aghoritm/-/domini/%22%22/software/portaleappalti/email/-";
      String PORT_TOKEN_URL =  (String)ConfigManager.getValore("sso.portoken.url");
      UserInfoResponse response = client.getPortokenCredentials(PORT_TOKEN_URL, domainMail, domainPwd);
      if(response.isEsito()){
          TokenContent tokenContent = response.getTokenContent();
          String utente = StringUtils.stripToNull(tokenContent.getUtente());
          String email = StringUtils.stripToNull(tokenContent.getEmail());
          logEvento.setDescr("Login mediante Portoken da parte dell'amministratore impersonato da " + utente + " (" + email + ") con motivazione di accesso o ticket: " + motivazione);
      } else {
          logEvento.setDescr("Tentativo di login dell'amministratore non validato. (" + domainMail + ").");
          logEvento.setLivEvento(3);
          String messageKey = "errors.portoken.autenticazioneFallita";
          this.aggiungiMessaggio(request,messageKey);
          request.setAttribute("metodo", PORTOKEN);
          logEvento.setErrmsg(response.getError());
          logger.error(response.getError());
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      }
      LogEventiUtils.insertLogEventi(logEvento);

      return target;
  }
  

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    AdminAccessForm adminAccessForm = (AdminAccessForm) form;
    String metodo = adminAccessForm.getMetodo();
    String target =  CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
    if(MTOKEN.equals(metodo)){
      target = loginMtoken(mapping,form,request);
    }else if (PORTOKEN.equals(metodo)){
      target = loginPortoken(mapping,form,request);
    }
    if(CostantiGeneraliStruts.FORWARD_OK.equals(target)){
      if("1".equals(request.getSession().getAttribute(CostantiGenerali.SENTINELLA_UNICO_CODICE_PROFILO))){
        request.setAttribute("codApp", ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE));
        target = "successSkipProfili";
      }
      request.getSession().removeAttribute(CostantiGenerali.SENTINELLA_ACCESSO_AMMINISTRATORE);
    }
    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }
  
}
