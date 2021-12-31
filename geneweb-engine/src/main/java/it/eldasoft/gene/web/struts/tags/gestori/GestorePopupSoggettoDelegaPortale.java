/*
 * Created on 24/05/16
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
import it.eldasoft.www.PortaleAlice.EsitoOutType;
import it.eldasoft.www.PortaleAlice.PortaleAliceProxy;

import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire la
 * funzione 'Soggetto con delega in Portale Appalti'
 *
 * @author Sara Santi
 */
public class GestorePopupSoggettoDelegaPortale extends
    AbstractGestoreEntita {

  static Logger       logger = Logger.getLogger(GestorePopupSoggettoDelegaPortale.class);

  public GestorePopupSoggettoDelegaPortale() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
  }

  @Override
  public String getEntita() {
    return "IMPR";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {



    // lettura dei parametri di input
    String codiceDitta = datiForm.getString("CODIMP");
    String codFisc = datiForm.getString("CODFISC");
    String username = datiForm.getString("USERNAME");
    String codiceErrore = "";

    if (logger.isDebugEnabled())
      logger.debug("Soggetto con delega in Portale Appalti per l'impresa (" + codiceDitta + "): Inizio ");

    try {
      //Chiamata al servizio
      PortaleAliceProxy proxy = new PortaleAliceProxy();
      //indirizzo del servizio letto da properties
      String endPoint = ConfigManager.getValore(CostantiGenerali.PROP_URL_WEB_SERVICE_PORTALE_ALICE);
      proxy.setEndpoint(endPoint);
      EsitoOutType risultato = proxy.aggiornaUtenteDelegatoImpresa(username, codFisc);
      if(!risultato.isEsitoOk()){
        codiceErrore = risultato.getCodiceErrore();
        if (codiceErrore!=null && !"".equals(codiceErrore)){
          if(codiceErrore.indexOf("UNKNOWN-USER")>=0){
            this.getRequest().setAttribute("utenteNonDefinito", "1");
            logger.error("Errore durante la chiamata del servizio aggiornaUtenteDelegatoImpresa: utente (" + username + ") non definito" );
          }
          if(codiceErrore.indexOf("EMPTY-DELEGATE")>=0){
            this.getRequest().setAttribute("delegatoNonDefinito", "1");
            logger.error("Errore durante la chiamata del servizio aggiornaUtenteDelegatoImpresa: delegato non definito" );
          }
          if(codiceErrore.indexOf("UNEXP-ERR")>=0){
            this.getRequest().setAttribute("erroreNonDefinito", "1");
            logger.error("Errore durante la chiamata del servizio aggiornaUtenteDelegatoImpresa: errore non definito" );
          }
        }
      }
     } catch (RemoteException e) {
      String codice="delegaPortaleAppalti";
      String messaggio = e.getMessage();
      if(messaggio.indexOf("Connection refused")>0)
        codice+=".noServizio";
      if(messaggio.indexOf("Connection timed out")>0)
        codice+=".noServer";
      throw new GestoreException(
          "Errore durante la delega in Portale Appalti ",codice, e);
    }

    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    if (codiceErrore == null || "".equals(codiceErrore))
      this.getRequest().setAttribute("delegaPortaleEseguita", "1");

    if (logger.isDebugEnabled())
      logger.debug("Soggetto con delega in Portale Appalti per l'impresa (" + codiceDitta + "): Fine");
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
