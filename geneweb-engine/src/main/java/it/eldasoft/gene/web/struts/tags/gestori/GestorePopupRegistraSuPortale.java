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
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.bl.RegImpresaPortaleManager;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire la registrazione
 * dell'impresa su portale
 *
 * @author Marcello Caminiti
 */
public class GestorePopupRegistraSuPortale extends
    AbstractGestoreEntita {

  static Logger       logger = Logger.getLogger(GestorePopupRegistraSuPortale.class);

  public GestorePopupRegistraSuPortale() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  private RegImpresaPortaleManager regImpresaPortaleManager =null;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);

    regImpresaPortaleManager = (RegImpresaPortaleManager) UtilitySpring.getBean("regImpresaPortaleManager",
        this.getServletContext(), RegImpresaPortaleManager.class);
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
    String user = datiForm.getString("USER");
    String ragioneSoc = datiForm.getString("RAGIONESOC");
    String mail = UtilityStruts.getParametroString(this.getRequest(),"email");
    String pec = UtilityStruts.getParametroString(this.getRequest(),"pec");
    String codfisc = UtilityStruts.getParametroString(this.getRequest(),"codfisc");
    String piva = UtilityStruts.getParametroString(this.getRequest(),"piva");

    if (logger.isDebugEnabled())
      logger.debug("Registrazione su portale impresa (" + codiceDitta + "): Inizio ");

    try {
      String codiceRitorno = regImpresaPortaleManager.insertImpresaSulPortale(codiceDitta,user, true, ragioneSoc, codfisc, piva, mail, pec);
      if(codiceRitorno!=null){
        if("utenteDuplicato".equals(codiceRitorno)){
          this.getRequest().setAttribute("codiceUser", user);
          this.getRequest().setAttribute("userPresente", "1");
          return;
        }else if("mailDuplicata".equals(codiceRitorno)){
          this.getRequest().setAttribute("indirizzoMail", mail);
          this.getRequest().setAttribute("indirizzoPec", pec);
          this.getRequest().setAttribute("mailPresente", "1");
          return;
        }else if(codiceRitorno.indexOf("UNEXP-ERR")>=0){
          String messaggio = codiceRitorno.substring(codiceRitorno.indexOf(" "));
          this.getRequest().setAttribute("erroreServizioPortale", "1");
          logger.error("Errore: " + messaggio);
        }else if(codiceRitorno.indexOf("DUPL-USER")>=0){
          this.getRequest().setAttribute("codiceUser", user);
          this.getRequest().setAttribute("userPresente", "1");
          logger.error("Errore: username duplicato");
        }
        if(codiceRitorno.indexOf("EMPTY-EMAIL")>=0){
          this.getRequest().setAttribute("indirizzoMail", mail);
          this.getRequest().setAttribute("indirizzoPec", pec);
          this.getRequest().setAttribute("mailNonValorizzata", "1");
          logger.error("Errore: mail non valorizzata");
        }
        if(codiceRitorno.indexOf("DUPL-EMAIL")>=0){
          this.getRequest().setAttribute("indirizzoMail", mail);
          this.getRequest().setAttribute("indirizzoPec", pec);
          this.getRequest().setAttribute("mailPresente", "1");
          logger.error("Errore: mail duplicata");
        }
        if(codiceRitorno.indexOf("INVALID-EMAIL")>=0){
          this.getRequest().setAttribute("indirizzoMail", mail);
          this.getRequest().setAttribute("indirizzoPec", pec);
          this.getRequest().setAttribute("mailNonValida", "1");
          logger.error("Errore: indirizzo mail non valido");
        }
        if(codiceRitorno.indexOf("DUPL-CF")>=0){
          this.getRequest().setAttribute("codfisc", codfisc);
          this.getRequest().setAttribute("codfiscPresente", "1");
          logger.error("Errore: codice fiscale duplicato" );
        }
        if(codiceRitorno.indexOf("DUPL-PI")>=0){
          this.getRequest().setAttribute("piva", piva);
          this.getRequest().setAttribute("pivaPresente", "1");
          logger.error("Errore: partita I.V.A. duplicata");
        }

      this.sqlManager.rollbackTransaction(status);
      return;
      }
    }catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la registrazione sul portale ",null, e);

    }

    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    this.getRequest().setAttribute("registrazioneEseguita", "1");
    this.getRequest().setAttribute("userPresente", "0");
    this.getRequest().setAttribute("erroreServizioPortale", "0");
    this.getRequest().setAttribute("mailPresente", "0");

    if (logger.isDebugEnabled())
      logger.debug("Registrazione su portale impresa (" + codiceDitta + "): Fine");
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
