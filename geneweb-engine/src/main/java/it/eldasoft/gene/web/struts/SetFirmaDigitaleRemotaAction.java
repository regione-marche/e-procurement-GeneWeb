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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;


public class SetFirmaDigitaleRemotaAction extends DispatchActionAjaxLogged {
  
  Logger             logger = Logger.getLogger(SetFirmaDigitaleRemotaAction.class);
  
  private static final String PROP_INFOCERT_MODAL_AUTO     = "digital-signature-auto-mode";
  
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
    Long iddocdig = null;
    if(iddocdigString!= null){ iddocdig = Long.parseLong(iddocdigString);}
    String modalitaFirma = request.getParameter("modalitaFirma");
    
    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    
    if(profiloUtente == null){
      return null;
    }
    String queryUpdate = null;
    Long syscon;
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();
    JSONObject json = new JSONObject();
    try {
      syscon = (Long) sqlManager.getObject(select,new Object[]{profiloUtente.getId(),alias} );
      
      String passwordEncoded = null;
      if (pin != null && pin.trim().length() > 0) {
        ICriptazioneByte passwordICriptazioneByte = null;
        passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), pin.getBytes(),
            ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
        passwordEncoded = new String(passwordICriptazioneByte.getDatoCifrato());
      }
      
      String mode = "remota";
      if("1".equals(StringUtils.stripToEmpty(ConfigManager.getValore(PROP_INFOCERT_MODAL_AUTO)))){
        mode = "automatica";
      }
      
      transazione = this.sqlManager.startTransaction();
      firmaRemotaManager.sign(idprg, iddocdig, mode, modalitaFirma, alias, pin, otp);
      
      //se documento associato aggiorno l'occorrenza anche in c0oggass
      String c0acod= request.getParameter("c0acod");
      String nome = (String) this.sqlManager.getObject("select DIGNOMDOC from w_docdig where idprg = ? and iddocdig = ?", new Object[]{idprg, iddocdig});
      this.sqlManager.update("update c0oggass set C0ANOMOGG = ? where C0ACOD = ?", new Object[]{nome, c0acod});
      
      if(syscon == null){
        Long id = Long.valueOf(this.genChiaviManager.getNextId("WSLOGIN"));
        queryUpdate = "insert into WSLOGIN(USERNAME,PASSWORD,SERVIZIO,SYSCON,ID) values (?,?,'FIRMAREMOTA',?,?)";
        sqlManager.update(queryUpdate,new Object[]{alias,passwordEncoded,profiloUtente.getId(),id});
      }else{
        queryUpdate = "update WSLOGIN set USERNAME = ?,PASSWORD = ? where SERVIZIO = 'FIRMAREMOTA' and SYSCON = ?";
        sqlManager.update(queryUpdate,new Object[]{alias,passwordEncoded,profiloUtente.getId()});
      }
      
      
      
    } catch (SQLException e) {
        this.aggiungiMessaggio(request, e.getMessage());
        json.put("message", e.getMessage());
    } catch (GestoreException e) {
        this.aggiungiMessaggio(request, e.getMessage());
        json.put("message", e.getMessage());
    } catch (CriptazioneException e) {
        this.aggiungiMessaggio(request, e.getMessage());
        json.put("message", e.getMessage());
    }finally{
      if (transazione != null && json.get("message") == null) {
        try {
          sqlManager.commitTransaction(transazione);
          json.put("message", "ok");
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

}
