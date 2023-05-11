/*
 * Created on 23/nov/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.web.struts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.struts.DispatchActionAjaxLogged;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;


public class RichiestaOTPAction extends DispatchActionAjaxLogged {
  
  Logger             logger = Logger.getLogger(RichiestaOTPAction.class);
  
  //private SqlManager sqlManager;
  private static final String PROP_INFOCERT_OTP_URL     = "digital-signature-otp-url";
  //private FirmaRemotaManager firmaRemotaManager;
/*
  public void setFirmaRemotaManager(FirmaRemotaManager firmaRemotaManager) {
    this.firmaRemotaManager = firmaRemotaManager;
  }
  
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }*/
  
  /**
   * Caricamento in un oggetto JSON della lista delle chiavi da inserire nei caompi di filtro
   * presenti nella pagina di ricerca della storia delle modifiche (trova-st_trg.jsp)
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   * @throws CriptazioneException 
   */
  
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form,
      final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
  
    String alias= request.getParameter("alias");
    
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();
    JSONObject json = new JSONObject();
    
      
    //richiamo il servizio rest per recuperare l'OTP
    //getsire anche il caso in cui il servizio di cui sopra va in errore
    HttpURLConnection conn = null;
    String url = ConfigManager.getValore(PROP_INFOCERT_OTP_URL);
    url += alias;
    
    try {
      conn = (HttpURLConnection) new URL(url).openConnection();
      //conn.setDoOutput(true);
      conn.setRequestMethod("GET");
      
      int responseCode = conn.getResponseCode();
      System.out.println("GET Response Code :: " + responseCode);
      if (responseCode == HttpURLConnection.HTTP_OK) { // success
          BufferedReader in = new BufferedReader(new InputStreamReader(
                  conn.getInputStream()));
          String inputLine;
          StringBuffer res = new StringBuffer();

          while ((inputLine = in.readLine()) != null) {
              res.append(inputLine);
          }
          in.close();

          System.out.println(res.toString());
      } else {
          System.out.println("GET request not worked");
      }
    }catch (Exception e) {
      System.out.println(e.getMessage());
      
    } finally {
      if (conn != null) conn.disconnect();
      if (logger.isDebugEnabled()) {
        logger.debug("RichiestaOTP: conclusa con successo");
        this.aggiungiMessaggio(request, "everything ok");
      }
    }
    
    out.println(json);
    out.flush();
    return null;
  }

}
