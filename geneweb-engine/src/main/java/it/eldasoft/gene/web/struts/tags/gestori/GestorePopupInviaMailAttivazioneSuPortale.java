/*
 * Created on 13/10/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.www.PortaleAlice.PortaleAliceProxy;
import it.eldasoft.www.PortaleAlice.EsitoOutType;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire l'invio della mail di attivazione
 * dell'utente su portale
 * 
 * @author Sara Santi
 */
public class GestorePopupInviaMailAttivazioneSuPortale extends
    AbstractGestoreEntita {
  
  static Logger       logger = Logger.getLogger(GestorePopupInviaMailAttivazioneSuPortale.class);
  
  public GestorePopupInviaMailAttivazioneSuPortale() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
  }

  public String getEntita() {
    return "IMPR";
  }

  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    
    
    
    // lettura dei parametri di input
    String codiceDitta = datiForm.getString("CODIMP");
    String email = datiForm.getString("EMAIL");
    String codiceErrore = "";
    
    if (logger.isDebugEnabled())
      logger.debug("Invio mail di attivazione utenza su portale per l'impresa (" + codiceDitta + "): Inizio ");
    
    try {
      //Recupera l'utenza associata all'impresa
      List<?> datiW_PUSER = sqlManager.getVector("select USERNOME, IDUSER from W_PUSER where USERENT = ? and USERKEY1 = ?", new Object[]{"IMPR", codiceDitta});
      
      // Ragione sociale, partita IVA, codice fiscale
      String user = (String) SqlManager.getValueFromVectorParam(datiW_PUSER, 0).getValue();
      Long iduser = (Long) SqlManager.getValueFromVectorParam(datiW_PUSER, 1).getValue();
      
      if (user != null && !"".equals(user)){
        //Chiamata al servizio
        PortaleAliceProxy proxy = new PortaleAliceProxy();
        //indirizzo del servizio letto da properties
        String endPoint = ConfigManager.getValore(CostantiGenerali.PROP_URL_WEB_SERVICE_PORTALE_ALICE);
        proxy.setEndpoint(endPoint);
        EsitoOutType risultato = proxy.inviaMailAttivazioneImpresa(user, email);
        if(!risultato.isEsitoOk()){
          codiceErrore = risultato.getCodiceErrore();
          if (codiceErrore!=null && !"".equals(codiceErrore)){
            if(codiceErrore.indexOf("UNEXP-ERR")>=0){
              String messaggio = codiceErrore.substring(codiceErrore.indexOf(" "));
              this.getRequest().setAttribute("erroreServizioPortale", "1");
              logger.error("Errore durante la chiamata del servizio inviaMailAttivazioneImpresa: " + messaggio);
            }
            if(codiceErrore.indexOf("INVALID-EMAIL")>=0){
              this.getRequest().setAttribute("codice", codiceDitta);
              this.getRequest().setAttribute("email", email);
              this.getRequest().setAttribute("mailNonValida", "1");
              logger.error("Errore durante la chiamata del servizio inviaMailAttivazioneImpresa: " +
              		"indirizzo mail (" + email + ") non valido");
            }
            if(codiceErrore.indexOf("ACTIVE-USER")>=0){
              this.getRequest().setAttribute("user", user);
              this.getRequest().setAttribute("utenteAttivo", "1");
              logger.error("Errore durante la chiamata del servizio inviaMailAttivazioneImpresa: " +
              		"utente (" + user + ") già attivo");
            }
            if(codiceErrore.indexOf("UNKNOWN-USER")>=0){
              this.getRequest().setAttribute("utenteNonDefinito", "1");
              logger.error("Errore durante la chiamata del servizio inviaMailAttivazioneImpresa: utente (" + user + ") non definito" );
              sqlManager.update("delete from w_puser where iduser = ? ", new Object[]{iduser});
              sqlManager.update("update impr set DAESTERN=null, DINVREG=null, DELAREG=null where codimp = ? ", new Object[]{codiceDitta});
            }

          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'invio della mail di attivazione sul portale ",null, e);
    } catch (RemoteException e) {
      String codice="invioMailAttivazionePortale";
      String messaggio = e.getMessage();
      if(messaggio.indexOf("Connection refused")>0)
        codice+=".noServizio";
      if(messaggio.indexOf("Connection timed out")>0)
        codice+=".noServer";
      throw new GestoreException(
          "Errore durante l'invio della mail di attivazione sul portale ",codice, e);
    }
    
    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    if (codiceErrore == null || "".equals(codiceErrore))
      this.getRequest().setAttribute("invioMailAttivazioneEseguito", "1");
    
    if (logger.isDebugEnabled())
      logger.debug("Invio mail di attivazione utenza su portale per l'impresa (" + codiceDitta + "): Fine");
  }

  
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
