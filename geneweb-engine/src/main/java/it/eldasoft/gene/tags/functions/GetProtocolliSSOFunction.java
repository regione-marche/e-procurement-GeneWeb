/*
 * Creato 30/10/2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProtocolloAuth;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 * Funzione che determina e ordina i metodi di autenticazione disponibili
 *
 * @author Manuel Bridda
 */

public class GetProtocolliSSOFunction extends AbstractFunzioneTag {
  
  /** property per indicare se l'autenticazione tramite form è attiva */
  private static final String   PROP_AUTH_FORM                                     = "auth.form";
  /** property per indicare se l'autenticazione tramite SPID è attiva */
  private static final String   PROP_AUTH_SSO_SPID                                 = "auth.sso.spid";
  /** property per indicare se l'autenticazione tramite SPID è attiva */
  private static final String   PROP_AUTH_SSO_SPIDBUSINESS                         = "auth.sso.spidbusiness";
  /** property per indicare se l'autenticazione tramite CIE è attiva */
  private static final String   PROP_AUTH_SSO_CIE                                  = "auth.sso.cie";
  /** property per indicare se l'autenticazione tramite WEBSERVER è attiva */
  private static final String   PROP_AUTH_SSO_WEBSERVER                            = "auth.sso.webserver";
  /** property per indicare se l'autenticazione tramite GATEWAY è attiva */
  private static final String   PROP_AUTH_SSO_GATEWAY                              = "auth.sso.gateway";

  /** property per indicare la posizione per l'autenticazione tramite form*/
  private static final String   PROP_AUTH_FORM_POSITION                            = "auth.form.position";
  /** property per indicare la posizione per l'autenticazione tramite SPID*/
  private static final String   PROP_AUTH_SSO_SPID_POSITION                        = "auth.sso.spid.position";
  /** property per indicare la posizione per l'autenticazione tramite SPID business*/
  private static final String   PROP_AUTH_SSO_SPIDBUSINESS_POSITION                = "auth.sso.spidbusiness.position";
  /** property per indicare la posizione per l'autenticazione tramite CIE*/
  private static final String   PROP_AUTH_SSO_CIE_POSITION                         = "auth.sso.cie.position";
  /** property per indicare la posizione per l'autenticazione tramite WEBSERVER*/
  private static final String   PROP_AUTH_SSO_WEBSERVER_POSITION                   = "auth.sso.webserver.position";
  /** property per indicare la posizione per l'autenticazione tramite GATEWAY*/
  private static final String   PROP_AUTH_SSO_GATEWAY_POSITION                     = "auth.sso.gateway.position";
  /** property per indicare la posizione per l'autenticazione tramite COHESION */
  private static final String   PROP_AUTH_SSO_COHESION_POSITION                    = "auth.sso.cohesion.position";
  
  /** property per indicare il numero di configurazioni definite per l'autenticazione tramite WEBSERVER*/
  private static final String   PROP_AUTH_SSO_WEBSERVER_COUNT                      = "auth.sso.webserver.count";
  /** property per indicare la label del botton per l'autenticazione tramite GATEWAY */
  private static final String   PROP_SSO_GATEWAY_BUTTON                            = "auth.sso.gateway.button";
  private static final String   PROP_SSO_GATEWAY_DESCRIPTION                       = "auth.sso.gateway.description";

  public GetProtocolliSSOFunction() {
    super(1, new Class[] { PageContext.class });
  }
  
  Logger logger = Logger.getLogger(GetProtocolliSSOFunction.class);

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    List<ProtocolloAuth> listaConfigurazioniAttive = new ArrayList<ProtocolloAuth>();

    String[][] autenticazioni = new String[][] {
      {PROP_AUTH_FORM,PROP_AUTH_FORM_POSITION},
      {PROP_AUTH_SSO_SPID, PROP_AUTH_SSO_SPID_POSITION},
      {PROP_AUTH_SSO_CIE, PROP_AUTH_SSO_CIE_POSITION},
      {PROP_AUTH_SSO_WEBSERVER, PROP_AUTH_SSO_WEBSERVER_POSITION},
      {PROP_AUTH_SSO_GATEWAY, PROP_AUTH_SSO_GATEWAY_POSITION},
      {CostantiGenerali.PROP_AUTH_SSO_COHESION, PROP_AUTH_SSO_COHESION_POSITION},
      {PROP_AUTH_SSO_SPIDBUSINESS,PROP_AUTH_SSO_SPIDBUSINESS_POSITION}, 
    };
    
    for(int i=0; i<autenticazioni.length; i++) {
      boolean loginBasedOnFiscalCode = true;
      String name=autenticazioni[i][0];
      String position=autenticazioni[i][1];
      String order = ConfigManager.getValore(position);
      if("1".equals(ConfigManager.getValore(name)))
        if(PROP_AUTH_SSO_WEBSERVER.equals(name)) {
          Integer elementiAttivi =0;
          try {
            elementiAttivi = Integer.parseInt(ConfigManager.getValore(PROP_AUTH_SSO_WEBSERVER_COUNT));
          }
          catch(NumberFormatException e) {
            logger.error("Errore nella lettura del valore numerico per la proprietà: "+PROP_AUTH_SSO_WEBSERVER_COUNT);
          }
          for(int j=0;j<elementiAttivi;j++) {
            int buttonPosition = j+1;
            String label=ConfigManager.getValore("auth.sso.webserver.button."+buttonPosition);
            String description=ConfigManager.getValore("auth.sso.webserver.description."+buttonPosition);
            loginBasedOnFiscalCode=StringUtils.isNotBlank(ConfigManager.getValore("auth.sso.webserver.mapping.fiscalCode."+buttonPosition));
            listaConfigurazioniAttive.add(new ProtocolloAuth (name+"."+buttonPosition, label, position, description, order,loginBasedOnFiscalCode));
          }
        }else {
          String label = null;
          String description = null;
          if(PROP_AUTH_SSO_GATEWAY.equals(name)) {
            label=ConfigManager.getValore(PROP_SSO_GATEWAY_BUTTON);
            description=ConfigManager.getValore(PROP_SSO_GATEWAY_DESCRIPTION);
          }
          
          listaConfigurazioniAttive.add(new ProtocolloAuth (name, label, position, description, order,loginBasedOnFiscalCode));
        }
    }
    listaConfigurazioniAttive.sort((ProtocolloAuth z1,ProtocolloAuth z2) -> (Integer.compare(z1.getOrder(),z2.getOrder())));
    pageContext.getRequest().setAttribute("listaConfigurazioniAttive", listaConfigurazioniAttive);
    
    return null;
  }
}
