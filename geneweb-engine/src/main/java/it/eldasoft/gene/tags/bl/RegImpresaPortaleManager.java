/*
 * Created on 29/04/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.bl;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.PortaleAlice.EsitoOutType;
import it.eldasoft.www.PortaleAlice.PortaleAliceProxy;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.xml.rpc.JAXRPCException;

import org.apache.log4j.Logger;

public class RegImpresaPortaleManager {

  static Logger           logger = Logger.getLogger(RegImpresaPortaleManager.class);

  private SqlManager      sqlManager;

  private AnagraficaManager       anagraficaManager;



  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   *
   * @param anagraficaManager
   */
  public void setAnagraficaManager(AnagraficaManager anagraficaManager) {
    this.anagraficaManager = anagraficaManager;
  }


  /**
   * Metodo che effettua la registrazione di una impresa su portale alice.
   * Se il metodo viene richiamato per una registrazione massiva allora vengono effettuati
   * dei controlli preliminari sui requisiti minimi per la registrazione.
   *
   * @param ditta
   * @param user
   * @param chiamataSingola
   * @param ragSoc
   * @param codFisc
   * @param piva
   * @param mail
   * @param pec
   * @return String contenente i controlli non superati, altrimenti null
   * @throws GestoreException
   *
   */
  public String insertImpresaSulPortale(String ditta, String user, boolean chiamataSingola, String ragSoc,
      String codFisc, String piva, String mail, String pec) throws GestoreException {
    String ret=null;

    try {
      String messaggio=null;

      //Controllo sui requisiti richiesti per la registrazione della ditta,
      //da fare solo per la registrazione massiva
      if(!chiamataSingola)
        messaggio = this.anagraficaManager.controlliImpresaRegistrabile(ditta,false);

      if(messaggio!=null){
        ret = messaggio;
      }else{
        //Registrazione impresa
        String select="select count(IDUSER) from W_PUSER where USERNOME = ?";
        Long numOccorrenzeUser = (Long) sqlManager.getObject(
            select, new Object[]{user});
        if (numOccorrenzeUser!= null && numOccorrenzeUser.longValue()>0){
          if(chiamataSingola)
            ret="utenteDuplicato";
          else
            ret = "Il nome utente " + user + " è già utilizzato da un'altra impresa registrata su portale.";
          logger.error("Errore: Il nome utente " + user + " è già utilizzato da un'altra impresa registrata su portale.");
          return ret;
        }

        long newIdUser=0;
        select="select max(IDUSER) from W_PUSER";
        Long maxIdUseer = (Long) sqlManager.getObject(select, null);
        if (maxIdUseer==null || maxIdUseer.longValue()==0)
          newIdUser++;
        else
          newIdUser = maxIdUseer.longValue() + 1;

        String update="insert into W_PUSER (IDUSER,USERNOME,USERDESC,USERENT,USERKEY1) values (?,?,?,?,?) ";
        this.sqlManager.update(update, new Object[] { new Long(newIdUser), user,ragSoc,"IMPR",ditta});


        //Chiamata al servizio
        PortaleAliceProxy proxy = new PortaleAliceProxy();
        //indirizzo del servizio letto da properties
        String endPoint = ConfigManager.getValore(CostantiGenerali.PROP_URL_WEB_SERVICE_PORTALE_ALICE);
        proxy.setEndpoint(endPoint);
        EsitoOutType risultato = proxy.inserisciImpresa(user, mail, pec, ragSoc, codFisc, piva);
        if(!risultato.isEsitoOk()){
          String codiceErrore = risultato.getCodiceErrore();
          if(chiamataSingola){
            //Chiamata dal gestore per registazione singola, i messaggi sono gestiti nel gestore
            ret = codiceErrore;
          }else{
            if (codiceErrore!=null && !"".equals(codiceErrore)){
              if(codiceErrore.indexOf("UNEXP-ERR")>=0){
                String msg = codiceErrore.substring(codiceErrore.indexOf(" "));
                logger.error("Errore: " + msg);
                ret = "Si è presentato il seguente errore:" + msg;
              }
              if(codiceErrore.indexOf("DUPL-USER")>=0){
                ret = "Il nome utente " + user + " è già utilizzato da un'altra impresa registrata su portale. ";
                logger.error("Errore: Il nome utente " + user + " è già utilizzato da un'altra impresa registrata su portale.");
              }
              if(codiceErrore.indexOf("EMPTY-EMAIL")>=0){
                ret = "L'indirizzo mail " + mail + " oppure pec " + pec + " non è valorizzato. ";
                logger.error("Errore: L'indirizzo mail " + mail + " oppure pec " + pec + " non è valorizzato.");
              }
              if(codiceErrore.indexOf("DUPL-EMAIL")>=0){
                String msgMail = "";
                if(mail!=null && !"".equals(mail)){
                  msgMail=" mail '" + mail + "'";
                }
                if(pec!=null && !"".equals(pec)){
                  if(mail!=null && !"".equals(mail)){
                    msgMail+=" oppure pec '" + pec + "'";
                  }else{
                    msgMail+=" pec '" + pec + "'";
                  }
                }
                ret = "L'indirizzo" + msgMail + " è già utilizzato da un'altra impresa registrata su portale. ";
                logger.error("Errore: L'indirizzo mail " + mail + " oppure pec " + pec + " è già utilizzato da un'altra impresa registrata su portale.");
              }
              if(codiceErrore.indexOf("INVALID-EMAIL")>=0){
                ret="L'indirizzo mail " + mail + " oppure pec " + pec + " non è valido.";
                logger.error("L'indirizzo mail " + mail + " oppure pec " + pec + " non è valido.");
              }
              if(codiceErrore.indexOf("DUPL-CF")>=0){
                ret="Il codice fiscale " + codFisc + " è già utilizzato da un'altra impresa registrata su portale.";
                logger.error("Il codice fiscale " + codFisc + " è già utilizzato da un'altra impresa registrata su portale." );
              }
              if(codiceErrore.indexOf("DUPL-PI")>=0){
                ret = "La partita I.V.A. "+ piva + " è già utilizzata da un'altra impresa registrata su portale.";
                logger.error("La partita I.V.A. >"+ piva + " è già utilizzata da un'altra impresa registrata su portale.");
              }
            }
            throw new GestoreException(ret, null);
          }

        }else{
          //Aggiorna la data registrazione in IMPR
          sqlManager.update(
              "update IMPR set DINVREG = null, DELAREG = ? where CODIMP = ?",
              new Object[] {new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),ditta});
        }



      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura dei dati della ditta: " + ditta, "registrazionePortale", e);
    } catch (RemoteException e) {
      String codice="registrazionePortale";
      String messaggio = e.getMessage();
      String msg ="Errore durante la registrazione dell'impresa su portale.";
      if(messaggio.indexOf("Connection refused")>0){
        codice+=".noServizio";
        msg+=" Non è attivo il servizio";
      }if(messaggio.indexOf("Connection timed out")>0 || messaggio.indexOf("404")>0){
        codice+=".noServer";
        msg+=" Non è possibile accedere al portale";
      }
      throw new GestoreException(msg,codice, e);
    } catch (JAXRPCException e){
      throw new GestoreException(
          "Errore durante la registrazione sul portale. Non è possibile accedere al portale","registrazionePortale.noServer", e);
    }
    return ret;

  }

}
