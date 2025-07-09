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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.integrazioni.FirmaRemotaManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.DispatchActionAjaxLogged;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;


public class SetFirmaDigitaleRemotaAction extends DispatchActionAjaxLogged {
  
  Logger             logger = Logger.getLogger(SetFirmaDigitaleRemotaAction.class);
  
  private static final String PROP_INFOCERT_MODAL_AUTO     = "digital-signature-auto-mode";
  
  
  private static final String PROP_PROVIDER                = "digital-signature-provider";
  private static final String PROVIDER_COSIGN              = "3";
  private static final String PROP_CLIENT_ID               = "digital-signature-client-id";
  
  private SqlManager sqlManager;
  private FirmaRemotaManager firmaRemotaManager;
  private GenChiaviManager    genChiaviManager;

  public void setFirmaRemotaManager(FirmaRemotaManager firmaRemotaManager) {
    this.firmaRemotaManager = firmaRemotaManager;
  }
  
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }
  
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
  public final ActionForward getCredenziali(final ActionMapping mapping, final ActionForm form,
      final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException, SQLException, CriptazioneException {
    
    if (logger.isDebugEnabled()) {
      logger.debug("getCampiChiave: inizio metodo");
    }
    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    
    if(profiloUtente == null){
      return null;
    }
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();
    // fare un count per verificare se per uno stesso syscon e servizio = 'FIRMAREMOTA' ho una sola occorrenza,
    // in quel caso popolo username e password
    
    String username = null;
    String password = null;
    String passwordDecoded = null;
    
    String selectCount = "select count(id) from WSLOGIN where servizio = 'FIRMAREMOTA' and syscon = ?";
    
    Long countCredenziali = (Long)sqlManager.getObject(selectCount,new Object[]{profiloUtente.getId()} );
    
    if(countCredenziali != null && countCredenziali.longValue() == 1){
      
      String select = "select USERNAME, PASSWORD from WSLOGIN where servizio = 'FIRMAREMOTA' and syscon = ?";
      
      Vector<JdbcParametro> credenziali = sqlManager.getVector(select,new Object[]{profiloUtente.getId()} );
      
     
     
      if(credenziali!=null && credenziali.size()>0){
        username = (String) (credenziali.get(0)).getValue();
        password = (String) (credenziali.get(1)).getValue();
      }
      
      
      if (password != null && password.trim().length() > 0) {
        ICriptazioneByte passwordICriptazioneByte = null;
        passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), password.getBytes(),
            ICriptazioneByte.FORMATO_DATO_CIFRATO);
        passwordDecoded = new String(passwordICriptazioneByte.getDatoNonCifrato());
      }
    }

    // si popola il risultato in formato JSON
    JSONObject json = new JSONObject();
    json.put("alias", username);
    json.put("pin", passwordDecoded);
    out.println(json);
    if (logger.isDebugEnabled()) {
      logger.debug("Risposta JSON=" + json);
    }
    out.flush();
    
    if (logger.isDebugEnabled()) {
      logger.debug("getCampiChiave: fine metodo");
    }
    return null;
  }
  
  public final ActionForward setFirmaDigitale(final ActionMapping mapping, final ActionForm form,
      final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
  
    TransactionStatus transazione = null;
    String select = "select syscon from WSLOGIN where servizio = 'FIRMAREMOTA' and syscon = ? and username = ?";
    
    String alias= request.getParameter("alias");
    String pin = request.getParameter("pin");
    String idprg= request.getParameter("idprg");
    String iddocdigString = request.getParameter("iddocdig");
    String otp = request.getParameter("otp");
    
    Boolean isMassiva = request.getParameter("isMassiva").equals("massiva");
    Boolean isErroreMassiva = false;
    String messageErroreModMassiva = "";
    String entitaFirma = request.getParameter("entitaFirma");
    String chiaveFirma = request.getParameter("chiaveFirma");
    
    String provider = ConfigManager.getValore(PROP_PROVIDER);
    String clientId = ConfigManager.getValore(PROP_CLIENT_ID);
    String tokenSession = "";

    Long iddocdig = null;
    if(iddocdigString != null && !"".equals(iddocdigString)){ iddocdig = Long.parseLong(iddocdigString);}
    String modalitaFirma = request.getParameter("modalitaFirma");
    
    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    
    if(profiloUtente == null){
      return null;
    }
    
    Long syscon;
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();
    JSONObject json = new JSONObject();
    
    String codiceProfilo = null;
    String ip = null;
    Integer idUtente = null;
    try {
      codiceProfilo = (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
      if(request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE) != null){
        int idUtenteInt = ((ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();
        idUtente = new Integer(idUtenteInt);
      }
      ip = request.getRemoteAddr();
      
      syscon = (Long) sqlManager.getObject(select,new Object[]{profiloUtente.getId(),alias} );
      
      String passwordEncoded = null;
      if (pin != null && pin.trim().length() > 0) {
        ICriptazioneByte passwordICriptazioneByte = null;
        passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), pin.getBytes(),
            ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
        passwordEncoded = new String(passwordICriptazioneByte.getDatoCifrato());
      }
      
      transazione = this.sqlManager.startTransaction();
      
      aggiornaWSLOGIN(syscon, alias, passwordEncoded, profiloUtente.getId());
      
      String mode = "remota";
      if("1".equals(StringUtils.stripToEmpty(ConfigManager.getValore(PROP_INFOCERT_MODAL_AUTO)))){
        mode = "automatica";
      }
      String c0acod = request.getParameter("c0acod");
      c0acod = StringUtils.stripToEmpty(c0acod);
      Long c0acodLong = null;
      if(!"".equals(c0acod)) {
        c0acodLong = Long.valueOf(c0acod);
      }
      
      if(Boolean.TRUE.equals(isMassiva)) {
        
        String query = null;
        switch (entitaFirma) {
        case "DOCSTIPULA":
            query = "select iddocdig, dignomdoc from V_GARE_DOCSTIPULA where digfirma = '1' and idprg = ? and idstipula in ("+chiaveFirma+")";
          break;

        default:
          break;
        }
        
        List<?> listaIddocdig = this.sqlManager.getListVector(query, new Object[]{idprg});
        
        if (listaIddocdig != null && !listaIddocdig.isEmpty()) {
          
          if (PROVIDER_COSIGN.equalsIgnoreCase(provider)) {
            tokenSession = firmaRemotaManager.openSignatureSession(mode, alias, pin, otp, clientId);
          }
          for (int i = 0; i < listaIddocdig.size(); i++) {
            iddocdig = (Long) SqlManager.getValueFromVectorParam(listaIddocdig.get(i), 0).getValue();
            String nomeDocumento = SqlManager.getValueFromVectorParam(listaIddocdig.get(i), 1).getStringValue();
            if(iddocdig != null){
              try {
                firmaRemotaManager.sign(idprg, iddocdig, mode, modalitaFirma, alias, pin, otp, codiceProfilo, idUtente, ip);
                
                //se documento associato aggiorno l'occorrenza anche in c0oggass
                if(c0acodLong != null) {
                  aggiornaOccorrenzeC0oggass(c0acodLong, idprg, iddocdig);
                }
              }catch (Exception e) {
                isErroreMassiva = true;
                if(e.getMessage().contains("ErroreServizioEsternoGestitoCosign:")) {
                  messageErroreModMassiva += "- "+nomeDocumento+": "+e.getMessage().replace("ErroreServizioEsternoGestitoCosign:", "")+"\n";
                } else {
                  messageErroreModMassiva += "- "+nomeDocumento+": "+e.getMessage()+"\n";
                }
                //Errore di autenticazione, uscita anticipata per evitare il lock del profilo
                if(e.getMessage().contains("019") || e.getMessage().contains("010")) { 
                  messageErroreModMassiva = "Uscita anticipata per evitare il lock del profilo "+messageErroreModMassiva;
                  break;
                }
              }
            }
          }
          if (PROVIDER_COSIGN.equalsIgnoreCase(provider)) {
            firmaRemotaManager.closeSignatureSession(mode, alias, pin, otp, clientId, tokenSession);
          }
          if(isErroreMassiva) {
            throw new SQLException("Il sistema di firma non consente di apporre la firma sui seguenti documenti: \n"+messageErroreModMassiva);
          }
        }
      } else { //Firma Puntuale su un documento
        if (PROVIDER_COSIGN.equalsIgnoreCase(provider)) {
          tokenSession = firmaRemotaManager.openSignatureSession(mode, alias, pin, otp, clientId);
        }
        firmaRemotaManager.sign(idprg, iddocdig, mode, modalitaFirma, alias, pin, otp, codiceProfilo, idUtente, ip);
        if (PROVIDER_COSIGN.equalsIgnoreCase(provider)) {
          firmaRemotaManager.closeSignatureSession(mode, alias, pin, otp, clientId, tokenSession);
        }
        //se documento associato aggiorno l'occorrenza anche in c0oggass
        if(c0acodLong != null) {
          aggiornaOccorrenzeC0oggass(c0acodLong, idprg, iddocdig);
        }
      }
      
    } catch (Exception e) {
      if(e.getMessage().contains("ErroreServizioEsternoGestitoCosign:")) {
        this.aggiungiMessaggio(request, e.getMessage().replace("ErroreServizioEsternoGestitoCosign:", ""));
        json.put("message", e.getMessage().replace("ErroreServizioEsternoGestitoCosign:", ""));
      } else {
        this.aggiungiMessaggio(request, e.getMessage());
        json.put("message", e.getMessage());
      }
    }finally{
      if (transazione != null) {
        try {
          sqlManager.commitTransaction(transazione);
          if(json.get("message") == null) {
            json.put("message", "ok");
          }
        } catch (SQLException e) {
        }
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("setFirmaDigitale: conclusa con successo");
      this.aggiungiMessaggio(request, "everything ok");
    }
    
    out.println(json);
    out.flush();
    return null;
  }
  
  private void aggiornaOccorrenzeC0oggass(Long c0acod, String idprg, Long iddocdig) throws SQLException {
    String nome = (String) this.sqlManager.getObject("select DIGNOMDOC from w_docdig where idprg = ? and iddocdig = ?", new Object[]{idprg, iddocdig});
    this.sqlManager.update("update c0oggass set C0ANOMOGG = ? where C0ACOD = ?", new Object[]{nome, c0acod});
  }
  
  private void aggiornaWSLOGIN(Long syscon, String alias, String passwordEncoded, int idprofiloUtente) throws SQLException {
    String queryUpdate = null;
    if(syscon == null){
      Long id = Long.valueOf(this.genChiaviManager.getNextId("WSLOGIN"));
      queryUpdate = "insert into WSLOGIN(USERNAME,PASSWORD,SERVIZIO,SYSCON,ID) values (?,?,'FIRMAREMOTA',?,?)";
      sqlManager.update(queryUpdate,new Object[]{alias,passwordEncoded,idprofiloUtente,id});
    }else{
      queryUpdate = "update WSLOGIN set USERNAME = ?,PASSWORD = ? where SERVIZIO = 'FIRMAREMOTA' and SYSCON = ?";
      sqlManager.update(queryUpdate,new Object[]{alias,passwordEncoded,idprofiloUtente});
    }
  }

}
