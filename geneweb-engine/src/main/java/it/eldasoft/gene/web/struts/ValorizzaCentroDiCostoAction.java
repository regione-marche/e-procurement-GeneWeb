/*
 * Created on 11-dec-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;

import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.utils.properties.ConfigManager;
import it.maggioli.keycloakclient.ITokenAuth;
import it.maggioli.keycloakclient.auth.EAuthToken;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Consente di interrogare il gateway per l'invio dati a PCP per recuperare il Codice AUSA a partire dal codice fiscale
 * 
 * @author Manuel.Bridda
 */
public class ValorizzaCentroDiCostoAction extends ActionAjaxLogged{

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ValorizzaCentroDiCostoAction.class);

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    JSONObject result = new JSONObject();
    PrintWriter out = response.getWriter();
    String errori = "";
    
    String url = ConfigManager.getValore("ws.inviopcp.url");
    if (StringUtils.isBlank(url)) {
       errori +="L'indirizzo per la connessione al web service non e' definito.\n";
    }
    String cfEin=request.getParameter("cfEin");
    if(StringUtils.isBlank(cfEin)) {
      if("true".equals(request.getParameter("cfCUC")))
        errori+="Per procedere è necessario impostare il codice fiscale fittizio.\n";
      else
        errori+="Per procedere è necessario impostare il codice fiscale.\n";
    }
    String tenantId = ConfigManager.getValore("ws.inviopcp.tenantid");
    if (StringUtils.isBlank(tenantId)) {
      errori+="Il parametro tenant ID per l'utilizzo del servizio di invio a PCP non e' definito.\n";
    }
    
    String keycloakBaseUrl = ConfigManager.getValore("keycloak.realms.baseurl");
    String keycloakRealm = ConfigManager.getValore("keycloak.progettoappalti.subpath");
    String keycloakClientId = ConfigManager.getValore("keycloak.progettoappalti.client.id");
    String keycloakClientSecret = ConfigManager.getValore("keycloak.progettoappalti.client.secret");
    
    if(StringUtils.isBlank(errori)) {
    
      try {
        MultivaluedMap <String,Object> headers= new MultivaluedHashMap<String,Object>();
        headers.putSingle("X-tenantId", tenantId);
        
        if(StringUtils.isNotBlank(keycloakBaseUrl) && StringUtils.isNotBlank(keycloakRealm)) {
          ITokenAuth tokenAuth = EAuthToken.newKongTokenBuilder()
                  .withClientId(keycloakClientId)
                  .withClientSecret(keycloakClientSecret)
                  .withUrl(keycloakBaseUrl+keycloakRealm)   //Url base
                  .withType(2)   //autenticazione clientId+clientSecret 
              .build();
          headers.putSingle("Authorization", "Bearer "+tokenAuth.getToken());
        }
        
      Client client = ClientBuilder.newClient();
      client.property(ClientProperties.CONNECT_TIMEOUT, 60000);
      result = client.target(url+"/protected/ausa/getBy")
          .queryParam("codiceFiscale",cfEin)
          .request(MediaType.APPLICATION_JSON_TYPE)
          .headers(headers)
          .get()
          .readEntity(JSONObject.class);
  
      if(result.has("done") && "Y".equals(result.getString("done"))){
        JSONArray items = result.getJSONObject("data").getJSONArray("items");
        for(int i=0; i<items.size();i++) {
          JSONObject stazioneAppaltante = items.getJSONObject(i).getJSONObject("scheda").getJSONObject("stazioneAppaltante");
          if("ATTIVO".equals(stazioneAppaltante.getJSONObject("statoAUSA").get("stato"))){//filtro per le sole stazioni appaltanti attive
            result.put("codausa",stazioneAppaltante.getString("codiceAusa"));
            break;
          }
        }
      }else {
        logger.error("Il servizio per l'invio a PCP ha risposto negativamente: "+result);
        }
      }catch(Exception e) {
        logger.error("Errore inaspettato durante l'esecuzione della chiamata: "+e);
        if(e instanceof MessageBodyProviderNotFoundException && (StringUtils.isBlank(keycloakBaseUrl) || StringUtils.isBlank(keycloakRealm))) {
          result.put("pgMessage", "Il servizio gateway ha risposto con un formato dati non conforme."
              + " L'errore potrebbe essere dovuto alla mancata configurazione dell'accesso tramite servizi keycloak.");
        }else {
          result.put("pgMessage", e.getMessage());
        }
      }
    }else {
      result.put("pgMessage", errori);
    }
    
    out.print(result);
    out.flush();
    
    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return null;
  }

}