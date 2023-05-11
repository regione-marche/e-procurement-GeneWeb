/*
 * Created on 17/ago/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.web.struts.login.portoken;

import it.eldasoft.gene.db.domain.admin.TokenContent;
import it.eldasoft.gene.db.domain.admin.UserInfoResponse;

import java.nio.charset.Charset;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;


public class PortokenClient {

    private static final String ERROR_GENERIC = "portokenerrorgeneric";
    
    private static final String ERROR_CREDENTIALS = "portokenerrorcredentials";
    
    public UserInfoResponse getPortokenCredentials(String url, String domainMail, String password){
      
      UserInfoResponse result = new UserInfoResponse();
      try {
          Client client = ClientBuilder.newClient();
          WebTarget webTarget = client.target(url);
          String authorization = domainMail.trim() + ":" + password.trim();
          String encodedAuthorization = new String (new Base64().encode(authorization.getBytes(Charset.forName("UTF-8")))).trim();
          Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).header("Authorization","Basic "+encodedAuthorization);
          Response response = invocationBuilder.get( Response.class );
          int statusCode = response.getStatus();
          
          if(statusCode != Response.Status.OK.getStatusCode()){
              result.setEsito(false);
              if(statusCode == Response.Status.UNAUTHORIZED.getStatusCode() || statusCode == Response.Status.FORBIDDEN.getStatusCode()){
                  result.setError(ERROR_CREDENTIALS);
              } else {
                  result.setError(ERROR_GENERIC);
              }
          } else {
              String jwtToken = (response.readEntity( Object.class )).toString();
              String[] split_string = jwtToken.split("\\.");
              String base64EncodedBody = split_string[1];
              Base64 base64Url = new Base64(true);
              String body = new String(base64Url.decode(base64EncodedBody));
              TokenContent tokenContent = new ObjectMapper().readValue(body, TokenContent.class);
              result.setEsito(true);
              result.setTokenContent(tokenContent);
          }
      } catch(Exception ex){
          result.setEsito(false);
          result.setError(ERROR_GENERIC);
      }
      return result;
  }

}
