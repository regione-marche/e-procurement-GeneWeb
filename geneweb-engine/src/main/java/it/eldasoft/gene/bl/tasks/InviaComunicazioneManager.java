package it.eldasoft.gene.bl.tasks;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.dao.FileAllegatoDao;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.system.ConfigurazioneMail;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class InviaComunicazioneManager {

  static Logger           logger = Logger.getLogger(InviaComunicazioneManager.class);

  private SqlManager      sqlManager;
  private FileAllegatoDao fileAllegatoDao;
  private MailManager     mailManager;

  /**
   *
   * @param fileAllegatoDao
   */
  public void setFileAllegatoDao(FileAllegatoDao fileAllegatoDao) {
    this.fileAllegatoDao = fileAllegatoDao;
  }

  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }


  /**
   * @param mailManager
   *        mailManager da settare internamente alla classe.
   */
  public void setMailManager(MailManager mailManager) {
    this.mailManager = mailManager;
  }

  /**
   *
   * @throws GestoreException
   */
  @SuppressWarnings("unchecked")
  public void inviaComunicazione() {
    // il task deve operare esclusivamente nel caso di applicativo attivo ...
    if (WebUtilities.isAppNotReady()) return;

    if (logger.isDebugEnabled())
      logger.debug("inviaComunicazione: inizio metodo");

    //Considera solo le comunicazioni riservate
/*    String selectW_INVCOM = "select idprg, idcom, comintest, commsgogg, commsgtes, committ from w_invcom "
        + " where idprg = ? and comstato = '2' and (compub is null or compub <>1)";
    String updateW_INVCOM = "update w_invcom set comstato = ? where idprg = ? and idcom=?";*/
    String selectW_INVCOM = "select idprg, idcom, comintest, commsgogg, commsgtes, committ, commsgtip, idcfg, comnumprot, comdatprot, coment, comkey1 from w_invcom "+
                            " where (idprg = ? or idprg='C0') and comstato = '2' and (compub is null or compub <>1)";
    String updateW_INVCOM = "update w_invcom set comstato = ? where idprg = ? and idcom=?";


    //Gestione anche di soggetti destinatari che non sono imprese
    String selectW_INVCOMDES = "select idprg, idcom, idcomdes, desmail, desintest, comtipma, descodent from w_invcomdes where "
      + " idprg = ? and idcom = ? and desstato is null";
    String updateW_INVCOMDES = "update w_invcomdes set desdatinv = ?, desdatinv_s = ? , desstato = ?, deserrore = ? where idprg = ? and idcom = ? and idcomdes = ?";
    String selectW_DOCDIG = "select idprg, iddocdig, dignomdoc, digdesdoc from w_docdig where digent = ? and digkey1 = ? and digkey2 = ? order by iddocdig";

    String idprg = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);


    int comunicazioniDaGestire = 0;
    int comunicazioniGestite = 0;
    int mailInviate = 0;

    try {

      // Lettura delle comunicazione da inviare (sono quelle in stato '2' - 'In
      // uscita')
      List<Vector<JdbcParametro>> datiW_INVCOM = this.sqlManager.getListVector(selectW_INVCOM,
          new Object[] { idprg });

      if (datiW_INVCOM != null && datiW_INVCOM.size() > 0) {
        Long idcom = null;
        String comintest = null;
        String commsgogg = null;
        String commsgtes = null;
        String committ = null;
        boolean commsgtip = false;
        String idcfg = null;
        String comnumprot = null;
        String coment = null;
        String comkey1 = null;
        Date comdatprot = null;

        //Lettura impostazioni per eventuale invio mediante FAX
        String faxIndirizzoMail = "";
        String faxOggettoMail = "";
        String faxPathCertificato = "";
        boolean isCertificatoAllegato=false;
        String invioFax = ConfigManager.getValore(CostantiGenerali.PROP_FAX_ABILITATO);
        if ("1".equals(invioFax)) {
          faxIndirizzoMail = ConfigManager.getValore(CostantiGenerali.PROP_FAX_INDIRIZZO_MAIL);
          faxOggettoMail = ConfigManager.getValore(CostantiGenerali.PROP_FAX_OGGETTO_MAIL);
          faxPathCertificato = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_FAX_PATH_CERTIFICATO));
          if (faxPathCertificato != null)
            isCertificatoAllegato=true;
        }

        comunicazioniDaGestire = datiW_INVCOM.size();

        for (int i = 0; i < datiW_INVCOM.size(); i++) {
          idcom = (Long) SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i), 1).getValue();
          comintest = (String) SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i), 2).getValue();
          commsgogg = (String) SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i), 3).getValue();
          commsgtes = (String) SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i), 4).getValue();
          committ = (String) SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i), 5).getValue();
          commsgtip = "1".equals((SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i), 6).getValue()));
          idcfg = (String) SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i),7 ).getValue();
          idcfg = UtilityStringhe.convertiNullInStringaVuota(idcfg);
          coment = (String) SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i),10 ).getValue();
          comkey1 = (String) SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i),11 ).getValue();
          if("".equals(idcfg)){
            idcfg = CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD;
          }
          comnumprot = (String) SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i), 8).getValue();
          comdatprot = (Date) SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i), 9).getValue();

          String statoInvioComunicazione = "3";

          String[] fileAllegati = null;
          String[] fileAllegatiConCertificatoFax = null;
          ByteArrayOutputStream[] contenutoFileAllegati = null;
          ByteArrayOutputStream[] contenutoFileAllegatiConCertificatoFax = null;

          // Lettura della lista dei documenti associati
          List<Vector<JdbcParametro>> datiW_DOCDIG = this.sqlManager.getListVector(selectW_DOCDIG,
              new Object[] { "W_INVCOM", idprg, idcom.toString() });
          if (datiW_DOCDIG != null && datiW_DOCDIG.size() > 0) {

            fileAllegati = new String[datiW_DOCDIG.size()];
            contenutoFileAllegati = new ByteArrayOutputStream[datiW_DOCDIG.size()];

            for (int j = 0; j < datiW_DOCDIG.size(); j++) {
              Long iddocdig = (Long) SqlManager.getValueFromVectorParam(
                  datiW_DOCDIG.get(j), 1).getValue();
              String dignomdoc = (String) SqlManager.getValueFromVectorParam(
                  datiW_DOCDIG.get(j), 2).getValue();
              // String digdesdoc = (String)
              // SqlManager.getValueFromVectorParam(datiW_DOCDIG.get(j),3).getValue();

              /*
              File fileNameAllegatoTemp = null;
              fileNameAllegatoTemp = File.createTempFile(
                  "TMP" + FilenameUtils.getBaseName(dignomdoc), "."
                      + FilenameUtils.getExtension(dignomdoc), new File(
                      System.getProperty("java.io.tmpdir")));

              fileNameAllegatoTemp.deleteOnExit();
              */

              BlobFile fileAllegatoBlob = this.fileAllegatoDao.getFileAllegato(idprg, iddocdig);
              /*FileOutputStream stream = new FileOutputStream(
                  fileNameAllegatoTemp);*/
              ByteArrayOutputStream stream = new ByteArrayOutputStream();
              stream.write(fileAllegatoBlob.getStream());
              stream.close();

              fileAllegati[j] = dignomdoc; /*fileNameAllegatoTemp.getPath();*/
              contenutoFileAllegati[j] = stream;

            }
          }

          //Invio mediante Fax: aggiunge agli allegati il certificato, se previsto da properties
          if (isCertificatoAllegato) {
            if (fileAllegati != null) {
              fileAllegatiConCertificatoFax = new String[fileAllegati.length + 1];
              contenutoFileAllegatiConCertificatoFax = new ByteArrayOutputStream[fileAllegati.length + 1];
              for (int z = 0; z < fileAllegati.length; z++) {
                fileAllegatiConCertificatoFax[z] = fileAllegati[z];
                contenutoFileAllegatiConCertificatoFax[z] = contenutoFileAllegati[z];
              }
            } else {
              fileAllegatiConCertificatoFax = new String[1];
              contenutoFileAllegatiConCertificatoFax = new ByteArrayOutputStream[1];
            }
            fileAllegatiConCertificatoFax[fileAllegatiConCertificatoFax.length - 1] = new File(faxPathCertificato).getName();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            /*FileInputStream fileCertificato = new FileInputStream(faxPathCertificato);
            byte[] buffer = new byte[1024];
            while (fileCertificato.read(buffer) != -1) {
              stream.write(buffer);
            }
            stream.close();*/
            File fileCertificato = new File(faxPathCertificato);
            stream.write(FileUtils.readFileToByteArray(fileCertificato));
            contenutoFileAllegatiConCertificatoFax[contenutoFileAllegatiConCertificatoFax.length - 1] = stream;
          }

          // Lettura dei destinatari
          List<Vector<JdbcParametro>> datiW_INVCOMDES = this.sqlManager.getListVector(
              selectW_INVCOMDES, new Object[] { idprg, idcom });
          if (datiW_INVCOMDES != null && datiW_INVCOMDES.size() > 0) {
            for (int ds = 0; ds < datiW_INVCOMDES.size(); ds++) {
              //Lettura tipo indirizzo (mail o fax)
              Long tipoIndirizzo = (Long) SqlManager.getValueFromVectorParam(
                  datiW_INVCOMDES.get(ds), 5).getValue();
              //Se non è abilitato l'invio fax da properties ignora le comunicazioni di tipo fax, che cmq non dovrebbero neanche essere mai inserite
              if (tipoIndirizzo.longValue() != 3 || "1".equals(invioFax)) {
                Long idcomdes = (Long) SqlManager.getValueFromVectorParam(
                    datiW_INVCOMDES.get(ds), 2).getValue();

                // Indirizzo email del destinatario
                String indirizzoMailDestinatario = (String) SqlManager.getValueFromVectorParam(
                    datiW_INVCOMDES.get(ds), 3).getValue();

                // Oggetto della email
                String oggettoMail = commsgogg;


                // Testo della email
                if (commsgtes == null) commsgtes = "";
                String testoMail = null;
                if (comintest != null && "1".equals(comintest)) {
                  String nomest = (String) SqlManager.getValueFromVectorParam(
                      datiW_INVCOMDES.get(ds), 4).getValue();
                  String descodent = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(ds), 6).getValue();
                  if ("TECNI".equals(descodent)) {
                      testoMail = "Gent.le Sig. " + nomest + ",\n" + commsgtes;
                  } else {
                      testoMail = "Spett.le\n" + nomest + ",\n" + commsgtes;
                  }
                } else {
                  testoMail = commsgtes;
                }

                if (oggettoMail == null) oggettoMail = "";
                //if (testoMail == null) testoMail = "";

                ////////////////////////////////
                //Riportare nell'oggetto della mail, solo se valorizzati, il numero e la data protocollo della comunicazione
                String nprot = "";
                if (comnumprot != null && comnumprot != ""){
                  nprot = "Prot.N. " + comnumprot + " ";
                }
                String dprot = "";
                if (comdatprot != null){
                  dprot = "del " +  UtilityDate.convertiData(comdatprot,
                      UtilityDate.FORMATO_GG_MM_AAAA) + " ";
                }
                String trattino = "";
                if (!"".equals(nprot) || !"".equals(dprot)){
                  trattino = "- ";
                }
                oggettoMail = nprot + dprot + trattino + oggettoMail;
                ////////////////////////////////

                //Gestione invio mediante FAX: compone indirizzo e oggetto mail secondo quando indicato da properties
                if (tipoIndirizzo.longValue() == 3) {
                  String numeroFax = indirizzoMailDestinatario;
                  indirizzoMailDestinatario = faxIndirizzoMail.replaceAll("!FAX!", numeroFax);
                  //Se nessuna impostazione da properties, lascia l'oggetto della comunicazione
                  if (faxOggettoMail != null && !"".equals(faxOggettoMail))
                    oggettoMail = faxOggettoMail.replaceAll("!FAX!", numeroFax);
                }

                //fileAllegati = (String[]) fileAllegatiList.toArray(new String[]{});

                // Gestione invio email
                String statoInvioSingolaMail = null;
                String erroreInvioSingolaMail = null;
                int numeroTentativi = 5;
                int numTentativo = 0;
                boolean mailInviata = true;

                // Salvataggio preliminare dello stato (in invio)
                this.sqlManager.update("update w_invcomdes set desstato = ? where idprg = ? and idcom = ? and idcomdes = ?",
                    new Object[] {"1", idprg, idcom, idcomdes });

                // Invio della email

                MailSenderException mailSenderExceptiontmp = null;
                try {
                  //CF040716 nuova gestione del mail sender in base a staz.appaltante
                  IMailSender mailSender = MailUtils.getInstance(this.mailManager, idprg, idcfg);
                  mailSender.setNomeMittente(committ);
                  ConfigurazioneMail cfg = this.mailManager.getConfigurazione(idprg, idcfg);
                  String indirizzoMailMittente = cfg.getMailMitt();
                  // Tentativi di invio
                  do {
                    //variabili per tracciatura eventi
                    int livEvento = 1;
                    String errMsgEvento = "";
                    numTentativo = numTentativo + 1;
                    try {
                      if (fileAllegati == null) {
                        mailSender.send(indirizzoMailDestinatario, oggettoMail,
                            testoMail,commsgtip);
                      } else {
                        if (tipoIndirizzo.longValue() == 3 && isCertificatoAllegato)
                          mailSender.send(
                              new String[] { indirizzoMailDestinatario }, null,
                              null, oggettoMail, testoMail, fileAllegatiConCertificatoFax,contenutoFileAllegatiConCertificatoFax,commsgtip);
                        else
                          mailSender.send(
                              new String[] { indirizzoMailDestinatario }, null,
                              null, oggettoMail, testoMail, fileAllegati,contenutoFileAllegati,commsgtip);
                      }
                      // 20171220: si introduce un eventuale delay di attesa per risolvere gli interfacciamenti con provider di posta che
                      // bloccano attivita massive mediante antispam
                      if (mailSender.getDelay() != null) {
                        try {
                          TimeUnit.MILLISECONDS.sleep(mailSender.getDelay());
                        } catch (InterruptedException e) {
                          logger.error("Errore durante l'attesa tra un invio di una mail e la successiva schedulata", e);
                        }
                      }
                      mailInviata = true;
                    } catch (MailSenderException ms) {
                      mailInviata = false;
                      mailSenderExceptiontmp = ms;
                      numeroTentativi--;
                      livEvento = 3;
                      errMsgEvento = ms.getMessage();
                      logger.error("Si è verificato un errore nell'invio della mail (codice errore "+ ms.getCodiceErrore() + "), tentativi rimasti=" + numeroTentativi, ms);
                    }finally{
                      LogEvento logEvento = new LogEvento();
                      logEvento.setCodApplicazione(idprg);
                      logEvento.setOggEvento(idcom.toString());
                      logEvento.setLivEvento(livEvento);
                      logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_INVCOM);
                      logEvento.setDescr("Invio comunicazione da " + indirizzoMailMittente + " a " + indirizzoMailDestinatario + " - tentativo n. " + String.valueOf(numTentativo) + "(rif. " + coment + " - "+ comkey1 + ")");
                      logEvento.setErrmsg(errMsgEvento);
                      LogEventiUtils.insertLogEventi(logEvento);
                    }
                  } while (numeroTentativi > 0 && !mailInviata);

                } catch (MailSenderException ms) {
                  mailSenderExceptiontmp = ms;
                  mailInviata = false;
                }

                if (mailInviata) {
                  statoInvioSingolaMail = "2";
                } else {
                  statoInvioSingolaMail = "3";
                  statoInvioComunicazione = "4";
                  if(mailSenderExceptiontmp!=null){
                    erroreInvioSingolaMail = mailSenderExceptiontmp.getCodiceErrore()
                    + "\n"
                    + mailSenderExceptiontmp.toString();
                  }

                }

                // Aggiornamento della singola mail
                this.sqlManager.update(updateW_INVCOMDES, new Object[] {
                    new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),
                    UtilityDate.convertiData(UtilityDate.getDataOdiernaAsDate(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
                    statoInvioSingolaMail, erroreInvioSingolaMail, idprg, idcom,
                    idcomdes });

                // si aggiorna il contatore di invio mail (globale tra tutte le comunicazioni)
                mailInviate++;
              }
            }
          }

          // Aggiornamento dello stato del'intera comunicazione comunicazione
          this.sqlManager.update(updateW_INVCOM, new Object[] {
              statoInvioComunicazione, idprg, idcom });

          // si aggiorna il numero di comunicazioni gestite per intero
          comunicazioniGestite++;
        }
      }

    } catch (SQLException e) {
      logger.error("Si è verificato un errore nella lettura dei dati dalla base dati", e);
    } catch (IOException e) {
      logger.error("Si è verificato un errore nella gestione dei file allegati", e);
    } catch (Throwable e) {
      logger.error("Si è verificato un errore inaspettato", e);
    }

    if (comunicazioniDaGestire != comunicazioniGestite) {
      StringBuilder sb = new StringBuilder();
      sb.append("Si è verificato un problema inaspettato e sono state pertanto gestite correttamente ");
      sb.append(comunicazioniGestite);
      sb.append(" comunicazioni su ");
      sb.append(comunicazioniDaGestire);
      sb.append(", per un totale di ");
      sb.append(mailInviate);
      sb.append(" mail inviate.");
      logger.error(sb.toString());
    }

    if (logger.isDebugEnabled())
      logger.debug("inviaComunicazione: fine metodo");
  }

}
