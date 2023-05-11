package it.eldasoft.gene.bl.tasks;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

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
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

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
    String selectW_INVCOM = "select idprg, idcom, comintest, commsgogg, commsgtes, committ, commsgtip, idcfg, comnumprot, comdatprot, coment, comkey1 from w_invcom "+
                            " where (idprg = ? or idprg='C0') and comstato = '2' and (compub is null or compub <>1)";
    String updateW_INVCOM = "update w_invcom set comstato = ? where idprg = ? and idcom=?";


    //Gestione anche di soggetti destinatari che non sono imprese
    String selectW_INVCOMDES = "select idprg, idcom, idcomdes, desmail, desintest, comtipma, descodent, descc from w_invcomdes where "
      + " idprg = ? and idcom = ? and desstato is null order by idcom";
    String updateW_INVCOMDES = "update w_invcomdes set desdatinv = ?, desdatinv_s = ? , desstato = ?, deserrore = ?, desesitopec = ?, messageid = ? where idprg = ? and idcom = ? and idcomdes = ?";
    String selectW_DOCDIG = "select idprg, iddocdig, dignomdoc, digdesdoc from w_docdig where digent = ? and digkey1 = ? and digkey2 = ? order by iddocdig";

    String idprg = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);


    int comunicazioniDaGestire = 0;
    int comunicazioniGestite = 0;
    int mailInviate = 0;
    TransactionStatus status = null;
    boolean commitTransaction = false;

    List<Vector<JdbcParametro>> datiW_INVCOM = null;
    try {

      // Lettura delle comunicazione da inviare (sono quelle in stato '2' - 'In
      // uscita')
      datiW_INVCOM = this.sqlManager.getListVector(selectW_INVCOM,
          new Object[] { idprg });
      } catch (SQLException e) {
        logger.error("Si è verificato un errore nella lettura dei dati dalla base dati", e);
      }

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
        boolean erroreLetturaAllegati = false;

        comunicazioniDaGestire = datiW_INVCOM.size();

        for (int i = 0; i < datiW_INVCOM.size(); i++) {

          try {
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
          } catch(Throwable t) {
            logger.error("Si è verificato un errore nella lettura dei dati della comunicazione id:" + idprg + " idcom:" + idcom.toString(), t);
            //si interrompe la gestione della comunicazione corrente e si passa all'occorrenza successiva nel ciclo
            continue;
          }


          String statoInvioComunicazione = "3";

          String[] fileAllegati = null;
          ByteArrayOutputStream[] contenutoFileAllegati = null;

          // Lettura della lista dei documenti associati
          List<Vector<JdbcParametro>> datiW_DOCDIG = null;

          try {
            datiW_DOCDIG = this.sqlManager.getListVector(selectW_DOCDIG,
                new Object[] { "W_INVCOM", idprg, idcom.toString() });
          } catch (SQLException e) {
            logger.error("Si è verificato un errore nella lettura degli allegati della comunicazione id:" + idprg + " idcom:" + idcom.toString(), e);
            //si interrompe la gestione della comunicazione corrente e si passa all'occorrenza successiva nel ciclo
            continue;
          }
          if (datiW_DOCDIG != null && datiW_DOCDIG.size() > 0) {

            fileAllegati = new String[datiW_DOCDIG.size()];
            contenutoFileAllegati = new ByteArrayOutputStream[datiW_DOCDIG.size()];

            erroreLetturaAllegati=false;
            for (int j = 0; j < datiW_DOCDIG.size(); j++) {
              try {
                Long iddocdig = (Long) SqlManager.getValueFromVectorParam(
                    datiW_DOCDIG.get(j), 1).getValue();
                String dignomdoc = (String) SqlManager.getValueFromVectorParam(
                    datiW_DOCDIG.get(j), 2).getValue();

                BlobFile fileAllegatoBlob = this.fileAllegatoDao.getFileAllegato(idprg, iddocdig);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                stream.write(fileAllegatoBlob.getStream());
                stream.close();

                fileAllegati[j] = dignomdoc; /*fileNameAllegatoTemp.getPath();*/
                contenutoFileAllegati[j] = stream;
              }catch(Throwable t) {
                logger.error("Si è verificato un errore nel caricamento degli allegati della comunicazione id:" + idprg + " idcom:" + idcom.toString(), t);
                erroreLetturaAllegati=true;
                break;
              }
            }

            //c'è stato errore nel caricamento degli allegati, si salta l'invio della comunicazione
            if(erroreLetturaAllegati)
              continue;
          }

          // Lettura dei destinatari
          List<Vector<JdbcParametro>> datiW_INVCOMDES=null;

          try {
            datiW_INVCOMDES = this.sqlManager.getListVector(
                selectW_INVCOMDES, new Object[] { idprg, idcom });
          } catch (SQLException e1) {
            logger.error("Si è verificato un errore nella lettura dei destinatari della comunicazione id:" + idprg + " idcom:" + idcom.toString(), e1);
            //si interrompe la gestione della comunicazione corrente e si passa all'occorrenza successiva nel ciclo
            continue;
          }
          if (datiW_INVCOMDES != null && datiW_INVCOMDES.size() > 0) {
            for (int ds = 0; ds < datiW_INVCOMDES.size(); ds++) {

              //Lettura tipo indirizzo (mail o fax)
              Long tipoIndirizzo = null;
              if(SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(ds), 5).getValue()!=null)
                tipoIndirizzo = (Long) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(ds), 5).getValue();
              // i meccanismi di riconciliazione della PEC si attivano per configurazioni PEC
              // che usano IMAP, in modo da poter accedere in futuro alla INBOX per
              // controllare le mail correlate delle ricevute, e se oltre al mittente anche il
              // destinatario e' PEC
              boolean attivaRiconciliazionePEC = false;
              Long desesitopec = null;
              String messageID = null;
              String indirizzoMailMittente = "";
              try {
                final ConfigurazioneMail mailConfig = mailManager.getConfigurazione(idprg, idcfg);
                if (mailConfig!=null && mailConfig.isIMAPConfigurato() && tipoIndirizzo.compareTo(1L) == 0) {
                	attivaRiconciliazionePEC = true;	
                    desesitopec = 1L;
                }
                indirizzoMailMittente = mailConfig.getMailMitt();
              } catch (CriptazioneException e1) {
                logger.error("Si è verificato un errore nella lettura della configurazione del server di posta codapp:" + idprg + " idcfg:" + idcfg, e1);
              }


              if (tipoIndirizzo!=null && tipoIndirizzo.longValue() != 3) {
                Long idcomdes = null;
                try {
                  idcomdes = (Long) SqlManager.getValueFromVectorParam(
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
                  
                  //APPALTI-1097 - invio copia per conoscenza
                  String descc = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(ds), 7).getValue();
                  if("1".equals(descc))
                    oggettoMail = "(p.c.) " + oggettoMail;

                  // Gestione invio email
                  String statoInvioSingolaMail = null;
                  String erroreInvioSingolaMail = null;
                  int numeroTentativi = 5;
                  int numTentativo = 0;
                  boolean mailInviata = true;


                  status = this.sqlManager.startTransaction();
                  // Salvataggio preliminare dello stato (in invio)
                  this.sqlManager.update("update w_invcomdes set desstato = ? where idprg = ? and idcom = ? and idcomdes = ?",
                      new Object[] {"1", idprg, idcom, idcomdes });

                  // Invio della email

                  MailSenderException mailSenderExceptiontmp = null;
                  java.util.Date dataOraInvio = null;
                  try {
                    //CF040716 nuova gestione del mail sender in base a staz.appaltante
                    IMailSender mailSender = MailUtils.getInstance(this.mailManager, idprg, idcfg);
                    //GENEWEB-168: si rimuove la gestione che inietta il tag X-Riferimento-MessageID e si introduce la lettura e salvataggio del messageID inviato
//                    if (attivaRiconciliazionePEC) {
//                        // l'impostazione del reference ID avviene ESCLUSIVAMENTE nelle PEC to PEC con
//                        // riconciliazione attivata, per tutti gli altri non si valorizza nulla
//                        // (ATTENZIONE: AL MOMENTO FUNZIONA SOLO CON ARUBAPEC COME PROVIDER!!!)
//                        mailSender.setReferenceId(idprg + "_" + idcom + "_" + idcomdes);
//                    }
                    mailSender.setNomeMittente(committ);

                    // Tentativi di invio
                    do {
                    	// si imposta il timestamp di invio mail
                    	dataOraInvio = UtilityDate.getDataOdiernaAsDate();
                        
                      //variabili per tracciatura eventi
                      int livEvento = 1;
                      String errMsgEvento = "";
                      numTentativo = numTentativo + 1;
                      try {
                        if (fileAllegati == null) {
                          mailSender.send(indirizzoMailDestinatario, oggettoMail,
                              testoMail,commsgtip);
                        } else {
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
                        if (attivaRiconciliazionePEC) {
                        	// si estrae il message ID per inserirlo nella tabella W_INVCOMDES
                        	messageID = mailSender.getReferenceId();
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
                    desesitopec = null;
                    if(mailSenderExceptiontmp!=null){
                      erroreInvioSingolaMail = mailSenderExceptiontmp.getCodiceErrore()
                      + "\n"
                      + mailSenderExceptiontmp.toString();
                    }

                  }

                  // Aggiornamento della singola mail
                  this.sqlManager.update(updateW_INVCOMDES, new Object[] {
                		  new Timestamp(dataOraInvio.getTime()),
                      UtilityDate.convertiData(UtilityDate.getDataOdiernaAsDate(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
                      statoInvioSingolaMail, erroreInvioSingolaMail, desesitopec, messageID, idprg, idcom,
                      idcomdes });

                  // si aggiorna il contatore di invio mail (globale tra tutte le comunicazioni)
                  mailInviate++;
                  commitTransaction=true;
                } catch(Throwable e) {
                  commitTransaction=false;
                  statoInvioComunicazione = "4";
                  String idcomdesString = "";
                  if(idcomdes!=null)
                    idcomdesString = idcomdes.toString();
                  logger.error("Si è verificato un errore nell'invio della comunicazione del singolo destinatario idprg:" + idprg +
                      " idcom:" + idcom.toString() + " idcomdes:" + idcomdesString, e);
                }finally {
                  if (status != null) {
                    try {
                      if (commitTransaction) {
                        this.sqlManager.commitTransaction(status);
                      } else {
                        this.sqlManager.rollbackTransaction(status);
                      }
                    }catch(SQLException e1) {
                      String idcomdesString = "";
                      if(idcomdes!=null)
                        idcomdesString = idcomdes.toString();
                      logger.error("Si è verificato un errore nell'aggiornamento dello stato della comunicazione del singolo destinatario idprg:" + idprg +
                          " idcom:" + idcom.toString() + " idcomdes:" + idcomdesString, e1);
                    }
                  }
                }
              }
            }//fine ciclo for sui destinatari
          }//fine if sui destinatari


          // Aggiornamento dello stato del'intera comunicazione comunicazione
          try {
            status = this.sqlManager.startTransaction();
            this.sqlManager.update(updateW_INVCOM, new Object[] {
                statoInvioComunicazione, idprg, idcom });

            commitTransaction=true;
            //si aggiorna il numero di comunicazioni gestite per intero
            comunicazioniGestite++;
          } catch (SQLException e) {
            commitTransaction=false;
          }finally {
            if (status != null) {
              try {
                if (commitTransaction) {
                  this.sqlManager.commitTransaction(status);
                } else {
                  this.sqlManager.rollbackTransaction(status);
                }
              }catch(SQLException e1) {
                logger.error("Si è verificato un errore nell'aggiornamento dello stato della comunicazione id:" + idprg + " idcom:" + idcom.toString(), e1);
              }
            }
          }
        }
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
