/*
 * Created on 1-giu-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.bl.tasks;

import it.eldasoft.console.bl.schedric.CalcoloDate;
import it.eldasoft.console.bl.schedric.SchedRicManager;
import it.eldasoft.console.db.domain.schedric.CodaSched;
import it.eldasoft.console.db.domain.schedric.DataSchedulazione;
import it.eldasoft.console.db.domain.schedric.SchedRic;
import it.eldasoft.console.web.struts.schedric.CostantiCodaSched;
import it.eldasoft.console.web.struts.schedric.CostantiSchedRic;
import it.eldasoft.gene.bl.CheckReportPerProfilo;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.LoginManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.genmod.CompositoreException;
import it.eldasoft.gene.bl.genric.ProspettoManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.dao.QueryDaoException;
import it.eldasoft.gene.db.dao.jdbc.ParametroStmt;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.genmod.ParametroComposizione;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.db.domain.genric.CampoRicerca;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.db.domain.genric.DatiGenProspetto;
import it.eldasoft.gene.db.domain.genric.DatiRisultato;
import it.eldasoft.gene.db.domain.genric.ElementoRisultato;
import it.eldasoft.gene.db.domain.genric.RigaRisultato;
import it.eldasoft.gene.db.domain.genric.TabellaRicerca;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.io.export.DatiExport;
import it.eldasoft.utils.io.export.ElementoExport;
import it.eldasoft.utils.io.export.ExportException;
import it.eldasoft.utils.io.export.FactoryExport;
import it.eldasoft.utils.io.export.IExport;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.utils.zip.FileZipper;
import it.eldasoft.utils.zip.FileZipperException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

/**
 * Batch di esecuzione dei report schedulati
 *
 * @author Stefano.Sabbadin
 */
public class ReportScheduler {

  /** Logger Log4J di classe */
  static Logger                 logger                      = Logger.getLogger(ReportScheduler.class);

  private static final String   OGGETTO_MAIL                = " - estrazione report pianificato";
  private static final String   TESTO_MAIL_SUCCESS          = "Gentile utente, l'esecuzione del report schedulato è avvenuta con successo.\nIn allegato è presente il report estratto";
  private static final String   TESTO_MAIL_ERROR            = "Gentile utente, l'esecuzione del report schedulato è andata in errore per il seguente motivo:";

//  /**
//   * Application context di Spring per il batch
//   */
//  private static final ApplicationContext applicationContext          = new ClassPathXmlApplicationContext(

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  private static ResourceBundle resBundleGenerale           = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  private SchedRicManager       schedRicManager;
  private TabellatiManager      tabellatiManager;
  private RicercheManager       ricManager;
  private ProspettoManager      prospettoManager;
  private AccountManager        accountManager;
  private LoginManager          loginManager;
  private GeneManager           geneManager;
  private MailManager           mailManager;

  private String                msg;
  private int                   stato;
  private boolean               bloccaReportVuoto;

  DatiRisultato datiRisultato = null;
  private int idCodaSched;
  
  /**
   * @param schedRicManager
   *        schedRicManager da settare internamente alla classe.
   */
  public void setSchedRicManager(SchedRicManager schedRicManager) {
    this.schedRicManager = schedRicManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param ricManager
   *        ricManager da settare internamente alla classe.
   */
  public void setRicManager(RicercheManager ricManager) {
    this.ricManager = ricManager;
  }

  /**
   * @param prospettoManager
   *        prospettoManager da settare internamente alla classe.
   */
  public void setProspettoManager(ProspettoManager prospettoManager) {
    this.prospettoManager = prospettoManager;
  }

  /**
   * @param accountManager
   *        accountManager da settare internamente alla classe.
   */
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  /**
   * @param loginManager
   *        loginManager da settare internamente alla classe.
   */
  public void setLoginManager(LoginManager loginManager) {
    this.loginManager = loginManager;
  }

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * @param mailManager
   *        mailManager da settare internamente alla classe.
   */
  public void setMailManager(MailManager mailManager) {
    this.mailManager = mailManager;
  }

  public void schedule() {

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato
    if (WebUtilities.isAppNotReady()) return;

//    boolean startupOk = true;

//    // FASE 1: lettura properties
//    startupOk = caricaProperties();

//    // FASE 2: caricamento metadati ed eventuali livelli
//    if (startupOk) startupOk = caricaMetadati();

//    if (!startupOk) {
//      if (logger.isDebugEnabled())
//        logger.fatal("L'applicazione viene terminata immediatamente a causa "
//            + "di problemi nella configurazione o nell'accesso alla banca dati");
//      System.exit(-1);
//    }

//    schedRicManager = (SchedRicManager) applicationContext.getBean("schedRicManager");
//    tabellatiManager = (TabellatiManager) applicationContext.getBean("tabellatiManager");
//    ricManager = (RicercheManager) applicationContext.getBean("ricercheManager");
//    prospettoManager = (ProspettoManager) applicationContext.getBean("prospettoManager");
//    accountManager = (AccountManager) applicationContext.getBean("accountManager");
//    loginManager = (LoginManager) applicationContext.getBean("loginManager");
//    geneManager = (GeneManager) applicationContext.getBean("geneManager");

    String idApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_ID_APPLICAZIONE);
    String codiceApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

    String pathFileComposto = ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI)
        + ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI_OUTPUT);

    GregorianCalendar momentoDiPartenza = null;
    List<?> listaSchedulazioni = null;
    Date dataDiPartenza = null;
    String logMessageKey = null;
    String msgMessageKey = null;
    String oraDiPartenza = null;
    String codiceUfficioIntestatario = null;
    Integer idSchedulazioneEstratta = null;
    SchedRic schedulazione = null;
    ListIterator<?> iteratorListaSchedulazioni = null;
    msg = null;
    stato = CostantiCodaSched.STATO_INIZIO;
    String fileComposto = "";
    CodaSched schedulazioneInCoda = null;
    String logMessageError = null;
    File fileFisicoComposto = null;

      momentoDiPartenza = new GregorianCalendar();

      oraDiPartenza = "" + momentoDiPartenza.get(Calendar.HOUR_OF_DAY) + ":";
      if (momentoDiPartenza.get(Calendar.MINUTE) < 10) oraDiPartenza += "0";
      oraDiPartenza += momentoDiPartenza.get(Calendar.MINUTE) + ":";
      if (momentoDiPartenza.get(Calendar.SECOND) < 10) oraDiPartenza += "0";
      oraDiPartenza += momentoDiPartenza.get(Calendar.SECOND);

      if (logger.isInfoEnabled())
        logger.info("Inizio tranche di esecuzione per l'orario "
            + oraDiPartenza);

      // caricamento schedulazioni da eseguire
      dataDiPartenza = new Date();
      logMessageKey = null;
      msgMessageKey = null;

      try {
        listaSchedulazioni = schedRicManager.getSchedulazioniPerOrario(
            momentoDiPartenza.get(Calendar.HOUR_OF_DAY),
            momentoDiPartenza.get(Calendar.MINUTE), dataDiPartenza, codiceApplicazione);

        iteratorListaSchedulazioni = listaSchedulazioni.listIterator();

        while (iteratorListaSchedulazioni.hasNext()) {
          // inizio della procedura per la singola schedulazione
          idSchedulazioneEstratta = (Integer) iteratorListaSchedulazioni.next();
          int idSchedric = idSchedulazioneEstratta.intValue();
          schedulazioneInCoda = new CodaSched();
          
          ContenitoreDatiRicerca contenitore = null;
          
          try {
            schedulazione = schedRicManager.getSchedulazioneRicerca(idSchedric);
            schedulazioneInCoda.setCodiceApplicazione(schedulazione.getCodiceApplicazione());
            contenitore = ricManager.getRicercaByIdRicerca(schedulazione.getIdRicerca());
            
            if (contenitore.getDatiGenerali() != null) {
              schedulazione.setNomeRicerca(contenitore.getDatiGenerali().getNome());
              if (logger.isInfoEnabled())
                logger.info("Presa in carico della schedulazione '"
                    + schedulazione.getNome()
                    + "' (id = "
                    + idSchedric
                    + ") collegata al report '"
                    + schedulazione.getNomeRicerca()
                    + "' (id = "
                    + schedulazione.getIdRicerca()
                    + ")");

              fileComposto = eseguiReport(idSchedric, momentoDiPartenza,
                  schedulazioneInCoda, schedulazione, codiceApplicazione,
                  pathFileComposto, idApplicazione, codiceUfficioIntestatario);
            } else {
              // Il report schedulato non viene eseguito, perchè è stato
              // cancellato dalla base dati. Questo viene segnalato con un
              // messaggio di errore e la data di prossima esecuzione viene
              // settata a null.

              logger.debug("Impossibile prendere in carica la schedulazione "
                  + "con id = "
                  + idSchedulazioneEstratta
                  + " perchè è stato "
                  + "cancellato dalla base dati il report a cui era collegato.");

              // Insert in W_CODASCHED della presa in carica della schedulazione
              // con report inesistente
              schedulazioneInCoda.setIdRicerca(schedulazione.getIdRicerca());
              schedulazioneInCoda.setIdSchedRic(schedulazione.getIdSchedRic().intValue());
              schedulazioneInCoda.setDataEsec(momentoDiPartenza.getTime());
              schedulazioneInCoda.setStato(CostantiCodaSched.STATO_INIZIO);
              schedulazioneInCoda.setEsecutore(schedulazione.getEsecutore());
              schedulazioneInCoda.setProfiloOwner(schedulazione.getProfiloOwner());

              schedRicManager.insertCodaSched(schedulazioneInCoda);

              stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
              logMessageKey = "errors.batchReportScheduler.reportCancellato";
              logger.error(resBundleGenerale.getString(logMessageKey));
              msg = resBundleGenerale.getString(logMessageKey);
            }

          } catch (CriptazioneException e) {

            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            msgMessageKey = "errors.applicazione.inaspettataException";
            msg = resBundleGenerale.getString(msgMessageKey);
            logMessageKey = e.getChiaveResourceBundle();
            logger.error(resBundleGenerale.getString(logMessageKey), e);

          } catch (FileNotFoundException fnf) {
            logMessageKey = "errors.applicazione.inaspettataException";
            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            msg = resBundleGenerale.getString(logMessageKey);
            logger.error(msg, fnf);

          } catch (ExportException e) {
            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            msgMessageKey = "errors.export.exportNonEffettuato";
            msg = resBundleGenerale.getString(msgMessageKey);
            logMessageKey = e.getChiaveResourceBundle();
            logger.error(resBundleGenerale.getString(logMessageKey), e);

          } catch (FileZipperException fz) {

            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            msgMessageKey = "errors.zipper.zipNonCreato";
            msg = resBundleGenerale.getString(msgMessageKey);
            logMessageKey = fz.getChiaveResourceBundle();
            logMessageError = resBundleGenerale.getString(logMessageKey);
            for (int i = 0; fz.getParametri() != null
                && i < fz.getParametri().length; i++)
              logMessageError = logMessageError.replaceAll(
                  UtilityStringhe.getPatternParametroMessageBundle(i),
                  (String) fz.getParametri()[i]);
            logger.error(logMessageError, fz);

            // se per caso siamo in eccezione e il file esiste vuol dire che è
            // corrotto quindi lo elimino
            fileFisicoComposto = new File(fileComposto);
            if (fileFisicoComposto.exists()) fileFisicoComposto.delete();

          } catch (SqlComposerException e) {
            logMessageKey = e.getChiaveResourceBundle();
            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            msg = resBundleGenerale.getString(logMessageKey);
            logger.error(msg, e);

          } catch (CompositoreException e) {
            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            msgMessageKey = "errors.batchReportScheduler.compositoreException";
            msg = resBundleGenerale.getString(msgMessageKey);
            // logMessageKey = e.getChiaveResourceBundle();
            // stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            // msg = resBundleGenerale.getString(logMessageKey);
            logMessageKey = e.getChiaveResourceBundle();
            if (e.getParametri() == null) {
              logger.error(resBundleGenerale.getString(logMessageKey), e);
            } else if (e.getParametri().length == 1) {
              logger.error(
                  UtilityStringhe.replaceParametriMessageBundle(
                      resBundleGenerale.getString(logMessageKey),
                      e.getParametri()), e);
            } else {
              logger.error(
                  UtilityStringhe.replaceParametriMessageBundle(
                      resBundleGenerale.getString(logMessageKey),
                      e.getParametri()), e);
            }

          } catch (RemoteException e) {
            logMessageKey = "errors.modelli.compositoreDisattivo";
            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            msg = resBundleGenerale.getString(logMessageKey);
            logger.error(msg, e);

          } catch (IOException e) {
            logMessageKey = "errors.applicazione.inaspettataException";

            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            msg = resBundleGenerale.getString(logMessageKey);
            logger.error(msg, e);
          } catch (DataAccessException e) {
            logMessageKey = "errors.database.dataAccessException";

            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;

            msgMessageKey = "errors.batchReportScheduler.erroriInAvvio";
            if (schedulazione.getNome() == null) {
              msg = resBundleGenerale.getString(msgMessageKey);

            } else {
              msg = UtilityStringhe.replaceParametriMessageBundle(
                  resBundleGenerale.getString(msgMessageKey),
                  new String[] { schedulazione.getNome() });
            }
            logger.error(msg
                + " - "
                + resBundleGenerale.getString(logMessageKey), e);
          } catch (QueryDaoException e) {
            logMessageKey = e.getChiaveResourceBundle();

            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            msg = resBundleGenerale.getString(logMessageKey);
            logger.error(msg, e);
          } catch (FileManagerException e) {
            logMessageError = resBundleGenerale.getString(e.getChiaveResourceBundle());
            for (int i = 0; i < e.getParametri().length; i++)
              logMessageError = logMessageError.replaceAll(
                  UtilityStringhe.getPatternParametroMessageBundle(i),
                  (String) e.getParametri()[i]);

            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            msg = logMessageError;
            logger.error(msg, e);
          } catch (NumberFormatException nf) {
            logMessageKey = "errors.applicazione.inaspettataException";

            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            msg = resBundleGenerale.getString(logMessageKey);
            logger.error(msg, nf);
          }  catch (Throwable t) {
            logMessageKey = "errors.applicazione.inaspettataException";

            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            msg = resBundleGenerale.getString(logMessageKey);
            logger.error(msg, t);
          }

          if (stato != CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE) {
            // prendo lo stato che ho modificato nella eseguiReport
            stato = schedulazioneInCoda.getStato();
            msg = schedulazioneInCoda.getMsg();
          }

          if (stato == CostantiCodaSched.STATO_INIZIO) {
            // se non è cambiato allora ha eseguito tutto con successo
            stato = CostantiCodaSched.STATO_ESEGUITO_CON_SUCCESSO;
          }

          schedulazioneInCoda.setStato(stato);
          schedulazioneInCoda.setMsg(msg);

          try {

            inviaMail(schedulazione, pathFileComposto, fileComposto,
                schedulazioneInCoda);

          } catch (MailSenderException ms) {
            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            msgMessageKey = "errors.batchReportScheduler.mailNonInviata";
            msg = resBundleGenerale.getString(msgMessageKey);
            logMessageKey = ms.getChiaveResourceBundle();
            logMessageError = resBundleGenerale.getString(logMessageKey);
            for (int i = 0; ms.getParametri() != null
                && i < ms.getParametri().length; i++)
              logMessageError = logMessageError.replaceAll(
                  UtilityStringhe.getPatternParametroMessageBundle(i),
                  (String) ms.getParametri()[i]);
            logger.error(logMessageError, ms);
          }

          schedulazioneInCoda.setStato(stato);
          schedulazioneInCoda.setMsg(msg);

          try {

            aggiornaDateEStato(schedRicManager, schedulazione, msg, stato,
                schedulazioneInCoda.getIdCodaSched(), dataDiPartenza,
                fileComposto);
          } catch (DataAccessException e) {
            logMessageKey = "errors.database.dataAccessException";
            msgMessageKey = "errors.batchReportScheduler.erroriInChiusura";
            if (schedulazione.getNome() == null) {
              msg = resBundleGenerale.getString(msgMessageKey);
            } else {
              msg = UtilityStringhe.replaceParametriMessageBundle(
                  resBundleGenerale.getString(msgMessageKey),
                  new String[] { schedulazione.getNome() });
            }
            logger.error(resBundleGenerale.getString(logMessageKey)
                + " - "
                + msg, e);
          }
          
          Integer famiglia = contenitore.getDatiGenerali().getFamiglia();
          boolean flag = false;
          String tabelleDaTracciare = ConfigManager.getValore("it.eldasoft.generatoreRicerche.tracciaTabelle");
          ArrayList<String> arrayTabelleDaTracciare = new ArrayList<String>(Arrays.asList(tabelleDaTracciare.split(";")));
          
          if(famiglia != null && famiglia == CostantiGenRicerche.REPORT_SQL){
            String sql = contenitore.getDatiGenerali().getDefSql().toUpperCase();
            for(int i=0; i<arrayTabelleDaTracciare.size() && !flag; i++){
              if(sql.contains(arrayTabelleDaTracciare.get(i))){
                flag = true;
              }
            }
          }else{
            if(famiglia != null && (famiglia == CostantiGenRicerche.REPORT_AVANZATO || famiglia == CostantiGenRicerche.REPORT_BASE)){
              Vector<TabellaRicerca> vector = contenitore.getElencoArgomenti();
              String tabella = null;
              for(int i=0; i<vector.size() && !flag; i++){
                tabella = vector.get(i).getAliasTabella();
                if(arrayTabelleDaTracciare.contains(tabella)){
                  flag = true;
                }
              }
            }
          }
          if(flag){
          LogEvento logEvento = new LogEvento();
          logEvento.setLivEvento(1);
          logEvento.setCodEvento("REPORT_SCHED");
          logEvento.setOggEvento(""+idCodaSched);
          logEvento.setCodApplicazione(contenitore.getDatiGenerali().getCodApp());
          String descrizione = "Estrazione report con id= " + contenitore.getDatiGenerali().getIdRicerca().toString();
          if(schedulazione.getEmail() != null && schedulazione.getEmail().length() > 0){descrizione = descrizione + ", inviato a: " + schedulazione.getEmail();}
          logEvento.setDescr(descrizione);
          ParametroStmt temp[] = datiRisultato.getParametriSql();
          String parametri = "";
          for(int i=0;i<temp.length;i++){
            if(i>0){parametri = parametri + ", ";}
            parametri = parametri + temp[i].getValore().toString();
          }
          logEvento.setErrmsg("SQL: " + datiRisultato.getQuerySql());
          LogEventiUtils.insertLogEventi(logEvento);
          }
          
          if (logger.isInfoEnabled())
            logger.info("Fine presa in carico della schedulazione '"
                + schedulazione.getNome()
                + "' (id = "
                + idSchedric
                + ")");

          msg = null;
          stato = CostantiCodaSched.STATO_INIZIO;
          fileComposto = "";
          schedulazioneInCoda = null;
        }
      } catch (DataAccessException e) {
        logMessageKey = "errors.database.dataAccessException";
        logger.error(resBundleGenerale.getString(logMessageKey), e);
      }

      GregorianCalendar momentoDiFine = new GregorianCalendar();

      String oraDiFine = "" + momentoDiFine.get(Calendar.HOUR_OF_DAY) + ":";
      if (momentoDiFine.get(Calendar.MINUTE) < 10) oraDiFine += "0";
      oraDiFine += momentoDiFine.get(Calendar.MINUTE) + ":";
      if (momentoDiFine.get(Calendar.SECOND) < 10) oraDiFine += "0";
      oraDiFine += momentoDiFine.get(Calendar.SECOND);

      if (logger.isInfoEnabled())
        logger.info("Fine tranche di esecuzione per l'orario " + oraDiFine);
      // se l'esecuzione è durata meno di 5 minuti allora lo mando in sleep se
      // no ricomincio subito!
      if (momentoDiFine.get(Calendar.MINUTE)
          - momentoDiPartenza.get(Calendar.MINUTE) < 5) {
        // Rimozione dei profili usati nell'ultima trance
//        geneManager.getProfili().removeAll();
//        sospendi(momentoDiFine);
      }
  }

//  /**
//   * Carica le properties che servono all'esecuzione del batch
//   *
//   * @return esito del caricamento
//   */
//  private static boolean caricaProperties() {
//    // FASE 1: lettura properties
//    boolean esito = true;
//
//    String logMessageKey = null;
//    // 10/11/2006 SS: definito metodo protetto che si occupa della decifratura
//    // del file in input fornendo come risultato lo stream decifrato.
//    InputStream streamCifrato = null;
//    InputStream streamAggiuntivo = null;
//    try {
//      streamCifrato = new FileInputStream(new File(
//          "../classes/scheduler.properties"));
//      // si effettua il caricamento delle properties dopo aver decifrato il file
//      if (streamCifrato != null) {
//        ConfigManager.reload(streamCifrato);
//
//        // se è presente la proprietà con l'elenco dei file di properties da
//        // caricare, allora si cicla su tale elenco e si caricano i file nel
//        // Config Manager
//        String elencoFile = ConfigManager.getValore(PROP_ELENCO_FILE);
//        if (elencoFile != null) {
//
//          String[] array = elencoFile.split(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE);
//          for (int i = 0; i < array.length; i++) {
//            streamAggiuntivo = new FileInputStream(new File("../classes/"
//                + array[i]));
//            if (streamAggiuntivo != null) ConfigManager.load(streamAggiuntivo);
//          }
//        }
//      }
//    } catch (FileNotFoundException e) {
//      logMessageKey = "errors.applicazione.inaspettataException";
//      logger.fatal(resBundleGenerale.getString(logMessageKey), e);
//      esito = false;
//    }
//
//    return esito;
//  }

//  /**
//   * Carica tutti i dizionari dei campi (mnemonici, nomi campi...)
//   *
//   * @return eisto del caricamento
//   */
//  private static boolean caricaMetadati() {
//    // FASE 2: caricamento metadati ed eventuali livelli
//    boolean esito = true;
//    String logMessageKey = null;
//    try {
//      MetadatiManager mm = (MetadatiManager) applicationContext.getBean("metadatiManager");
//      mm.carica();
//      LivelliManager lm = (LivelliManager) applicationContext.getBean("livelliManager");
//      lm.carica();
//    } catch (Throwable e) {
//      logMessageKey = "errors.applicazione.inaspettataException";
//      logger.fatal(resBundleGenerale.getString(logMessageKey), e);
//      esito = false;
//    }
//
//    return esito;
//  }

//  /**
//   * Manda in sleep il batch fino alla prossima scadenza dei 5 minuti
//   *
//   * @param momentoDiPartenza
//   */
//  private static void sospendi(GregorianCalendar momentoDiPartenza) {
//    int minuti = momentoDiPartenza.get(Calendar.MINUTE);
//    int secondi = momentoDiPartenza.get(Calendar.SECOND);
//
//    int secondiDaAspettare = (60 - secondi);
//    int minutiDaAspettare = 0;
//
//    if (minuti < 5)
//      minutiDaAspettare = 4 - minuti;
//    else
//      minutiDaAspettare = 4 - (minuti % 5);
//    secondiDaAspettare = secondiDaAspettare + (minutiDaAspettare * 60);
//
//    try {
//      // System.out.println("Restare in attesa per "
//      // + secondiDaAspettare
//      // + " secondi\n");
//      if (logger.isInfoEnabled())
//        logger.info("Sospensione batch per " + secondiDaAspettare + " secondi");
//      Thread.sleep(secondiDaAspettare * 1000);
//    } catch (InterruptedException e) {
//      logger.error(
//          resBundleGenerale.getString("errors.applicazione.inaspettataException"),
//          e);
//      System.exit(-1);
//    }
//  }

  /**
   * Esporta il risultato nel formato previsto nella schedulazione
   *
   * @param formato
   * @param strFormato
   * @param idCodaSched
   * @param datiRisultato
   * @param contenitore
   * @return nome del file esportato
   * @throws ExportException
   * @throws IOException
   */
  private static String exportRisultato(int formato, String strFormato,
      int idCodaSched, DatiRisultato datiRisultato,
      ContenitoreDatiRicerca contenitore) throws ExportException, IOException {
    if (logger.isDebugEnabled())
      logger.debug("exportRisultato: inizio metodo");

    // Il percorso in cui trovare il file prodotto dalla schedulazione
    String pathSchedulazioni = ConfigManager.getValore(CostantiCodaSched.PROP_PATH_FILE);
    //String nomeFile = "" + idCodaSched + "." + strFormato;
    String nomeFile ="";
    if (controlloPath(pathSchedulazioni, 1)) {
      nomeFile = pathSchedulazioni + idCodaSched + "." + strFormato;
      DatiExport datiExport = null;
      if(datiRisultato.getNumeroRecordTotali()!=0){
        if (CostantiGenRicerche.REPORT_SQL != contenitore.getDatiGenerali().getFamiglia().intValue()) {
          datiExport = datiRisultato.getDatiExport(
            contenitore.getDatiGenerali().getNome(), contenitore.getTitoliColonne());
        } else {
          // Inizializzazione dell'oggetto che conterra' i dati da esportare
          String[] titoliColonne = new String[datiRisultato.getArrayCampi().length];
          for (int u = 0; u < datiRisultato.getArrayCampi().length; u++) {
            titoliColonne[u] = datiRisultato.getArrayCampi()[u].getDescrizione();
          }
          datiExport = datiRisultato.getDatiExport(
              contenitore.getDatiGenerali().getNome(), titoliColonne);
        }
      }else{
        datiExport = new DatiExport();
        datiExport.setCaption(contenitore.getDatiGenerali().getNome());
        datiExport.setTitoliColonne(new String[]{"Il report non ha estratto alcun dato"});
      }
      OutputStream os = new FileOutputStream(new File(nomeFile));

      IExport exp = FactoryExport.getExport((short) formato);
      exp.setParameters(datiExport, true);
      exp.doExport(os);

      os.close();
    }

    if (logger.isDebugEnabled()) logger.debug("exportRisultato: fine metodo");
    return nomeFile;
  }

  /**
   * Esegue il singolo report schedulato
   *
   * @param idSchedric
   * @param momentoDiPartenza
   * @param schedulazioneInCoda
   * @param schedulazione
   * @param codiceApplicazione
   * @param pathFileComposto
   * @param idApplicazione
   * @return nome del file composto
   * @throws SqlComposerException
   * @throws DataAccessException
   * @throws QueryDaoException
   * @throws CompositoreException
   * @throws RemoteException
   * @throws FileManagerException
   * @throws NumberFormatException
   * @throws IOException
   * @throws FileZipperException
   * @throws ExportException
   * @throws TransformerException
   * @throws TransformerFactoryConfigurationError
   * @throws FactoryConfigurationError
   * @throws ParserConfigurationException
   * @throws TransformerConfigurationException
   */
  private String eseguiReport(int idSchedric,
      GregorianCalendar momentoDiPartenza, CodaSched schedulazioneInCoda,
      SchedRic schedulazione, String codiceApplicazione, String pathFileComposto, String idApplicazione,
      String codiceUfficioIntestatario) throws SqlComposerException, DataAccessException,
        QueryDaoException, CompositoreException, RemoteException,
        FileManagerException, NumberFormatException, IOException,
        FileZipperException, ExportException, CriptazioneException,
        TransformerConfigurationException, ParserConfigurationException,
        FactoryConfigurationError, TransformerFactoryConfigurationError,
        TransformerException {

    if (logger.isDebugEnabled()) logger.debug("eseguiReport: inizio metodo");

    // Il percorso in cui trovare il file prodotto dalla schedulazione
    String pathSchedulazioni = ConfigManager.getValore(CostantiCodaSched.PROP_PATH_FILE);

    int idRicerca = schedulazione.getIdRicerca();
    String fileComposto = "";
    String nomeFileComposto = "";
    bloccaReportVuoto = false;

    schedulazioneInCoda.setIdRicerca(idRicerca);
    schedulazioneInCoda.setIdSchedRic(schedulazione.getIdSchedRic().intValue());
    schedulazioneInCoda.setDataEsec(momentoDiPartenza.getTime());
    schedulazioneInCoda.setStato(CostantiCodaSched.STATO_INIZIO);
    schedulazioneInCoda.setEsecutore(schedulazione.getEsecutore());
    schedulazioneInCoda.setProfiloOwner(schedulazione.getProfiloOwner());

    schedRicManager.insertCodaSched(schedulazioneInCoda);
    idCodaSched = schedulazioneInCoda.getIdCodaSched();
    if (logger.isInfoEnabled())
      logger.info("Inserita l'esecuzione della schedulazione nella coda risultati con id = "
          + idCodaSched
          + " per essere eseguita");

    // String fileComposto = null;
    // String msg = "";
    String logMessageKey = null;

    // L.G. 11/09/2007: e' stata modificata la query getSchedRicById in seguito
    // al fatto che alle schedulazioni associate ai report con modello non
    // e' possibile associare il tipo di report e quindi su DB il campo
    // W_SCHEDRIC.FORMATO può assumere il valore <NULL>.
    // Ora la query getSchedById non estrae la descrizione del formato, quindi
    // nel caso in cui venga estratta una schedulazione con il campo FORMATO
    // diverso da <NULL>, bisogna caricare dal tabellato TAB1, con TAB1COD =
    // 'W0003' la relativa descrizione
    if (schedulazione.getFormato() != null)
      schedulazione.setDescFormato(tabellatiManager.getDescrTabellato(
          CostantiSchedRic.TABELLATO_FORMATO_SCHEDRIC,
          schedulazione.getFormato().toString()));
    // L.G. 11/09/2007: fine modifica
    String strFormato = schedulazione.getDescFormato();
    Integer formato = schedulazione.getFormato();
    ContenitoreDatiRicerca contenitore = null;

    Account account = accountManager.getAccountById(new Integer(
        schedulazione.getEsecutore()));

    ProfiloUtente profilo = null;
    if (account != null)
      profilo = loginManager.getProfiloUtente(account, codiceApplicazione);

    String pathFileReport = "";

    switch (ricManager.getFamigliaRicercaById(idRicerca).intValue()) {
    case CostantiGenRicerche.REPORT_BASE:
      if (controlloPath(pathSchedulazioni, 1)) {
        // esecuzione ricerca base
        contenitore = ricManager.getRicercaByIdRicerca(idRicerca);
        if (contenitore != null) {
          boolean isReportEseguibile = false;

          CheckReportPerProfilo reportChecking = new CheckReportPerProfilo(
              geneManager.getGestoreVisibilitaDati(),
              contenitore.getDatiGenerali().getProfiloOwner(), contenitore);

          // In questo modo aggiorno il contenitore eliminando gli oggetti non
          // visibili nel profilo attivo
          isReportEseguibile = reportChecking.isReportEseguibile();

          if (isReportEseguibile) {
            if (ricManager.isRicercaConParametri(idRicerca)) {
              stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
              logMessageKey = "errors.batchReportScheduler.reportConParametri";
              msg = resBundleGenerale.getString(logMessageKey);
            } else {
              contenitore.getDatiGenerali().setRisPerPag(null);

              if (account != null
                  || (account == null && contenitore.getDatiGenerali().getFiltroUtente() == 0)) {

                datiRisultato = ricManager.getRisultatiRicerca(contenitore, codiceUfficioIntestatario,
                    null, profilo, codiceApplicazione);

                if (datiRisultato.getNumeroRecordTotali() == 0) {
                  logger.warn(resBundleGenerale.getString(
                      "warnings.batchReportScheduler.reportVuotoForLog").replaceAll(
                      UtilityStringhe.getPatternParametroMessageBundle(0),
                      contenitore.getDatiGenerali().getNome()));
                }

                if (datiRisultato.getNumeroRecordTotali() == 0 && schedulazione.getNoOutputVuoto() == 1) {
                  stato = CostantiCodaSched.STATO_ESEGUITO_CON_WARNING;
                  logMessageKey = "warnings.batchReportScheduler.reportVuoto";
                  msg = resBundleGenerale.getString(logMessageKey);
                  bloccaReportVuoto = true;
                } else {
                  if (CostantiSchedRic.FORMATO_EXCEL.equalsIgnoreCase(strFormato))
                    strFormato = CostantiSchedRic.ESTENSIONE_EXCEL;
                  nomeFileComposto = exportRisultato(formato.intValue(),
                      strFormato, idCodaSched, datiRisultato, contenitore);
                }

              } else {
                stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
                logMessageKey = "errors.batchReportScheduler.reportNonEseguibileEsecutoreCancellato";
                logger.error(resBundleGenerale.getString(logMessageKey));
                msg = resBundleGenerale.getString(logMessageKey);
              }
            }
          } else {
            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            logMessageKey = "errors.batchReportScheduler.reportNonEseguibileNelProfiloAttivo";
            logger.error(resBundleGenerale.getString(logMessageKey));
            msg = resBundleGenerale.getString(logMessageKey);
          }
        } else {
          stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
          logMessageKey = "errors.batchReportScheduler.reportInesistente";
          msg = resBundleGenerale.getString(logMessageKey);
        }
      } else {
        stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
        logger.error(resBundleGenerale.getString(
            "errors.batchReportScheduler.pathInesistente.perLog").replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(0),
            pathSchedulazioni));
        logMessageKey = "errors.batchReportScheduler.pathInesistente";
        msg = resBundleGenerale.getString(logMessageKey);
      }
      break;

    case CostantiGenRicerche.REPORT_AVANZATO:
      // esecuzione ricerca avanzata
      if (controlloPath(pathSchedulazioni, 1)) {
        contenitore = ricManager.getRicercaByIdRicerca(idRicerca);
        if (contenitore != null) {

          boolean isReportEseguibile = false;

          CheckReportPerProfilo reportChecking = new CheckReportPerProfilo(
              geneManager.getGestoreVisibilitaDati(),
              contenitore.getDatiGenerali().getProfiloOwner(), contenitore);

          // In questo modo aggiorno il contenitore eliminando gli oggetti non
          // visibili nel profilo attivo
          isReportEseguibile = reportChecking.isReportEseguibile();

          if (isReportEseguibile) {
            if (ricManager.isRicercaConParametri(idRicerca)) {
              stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
              logMessageKey = "errors.batchReportScheduler.reportConParametri";
              msg = resBundleGenerale.getString(logMessageKey);
            } else {
              if (account != null
                  || (account == null && contenitore.getDatiGenerali().getFiltroUtente() == 0)) {

                datiRisultato = ricManager.getRisultatiRicerca(contenitore, codiceUfficioIntestatario,
                    null, profilo, codiceApplicazione);

                if (datiRisultato.getNumeroRecordTotali() == 0) {
                  logger.warn(resBundleGenerale.getString(
                  "warnings.batchReportScheduler.reportVuotoForLog").replaceAll(
                  UtilityStringhe.getPatternParametroMessageBundle(0),
                  contenitore.getDatiGenerali().getNome()));
                }

                if (datiRisultato.getNumeroRecordTotali() == 0 && schedulazione.getNoOutputVuoto() == 1) {
                  stato = CostantiCodaSched.STATO_ESEGUITO_CON_WARNING;
                  logMessageKey = "warnings.batchReportScheduler.reportVuoto";
                  msg = resBundleGenerale.getString(logMessageKey);
                  bloccaReportVuoto = true;
                } else {
                  if (CostantiSchedRic.FORMATO_EXCEL.equalsIgnoreCase(strFormato))
                    strFormato = CostantiSchedRic.ESTENSIONE_EXCEL;
                  nomeFileComposto = exportRisultato(formato.intValue(),
                      strFormato, idCodaSched, datiRisultato, contenitore);
                }
              } else {
                stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
                logMessageKey = "errors.batchReportScheduler.reportNonEseguibileEsecutoreCancellato";
                logger.error(resBundleGenerale.getString(logMessageKey));
                msg = resBundleGenerale.getString(logMessageKey);
              }
            }
          } else {
            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            logMessageKey = "errors.batchReportScheduler.reportNonEseguibileNelProfiloAttivo";
            logger.error(resBundleGenerale.getString(logMessageKey));
            msg = resBundleGenerale.getString(logMessageKey);
          }
        } else {
          stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
          logMessageKey = "errors.batchReportScheduler.reportInesistente";
          msg = resBundleGenerale.getString(logMessageKey);
        }
      } else {
        stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;

        logger.error(resBundleGenerale.getString(
            "errors.batchReportScheduler.pathInesistente.perLog").replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(0),
            pathSchedulazioni));
        logMessageKey = "errors.batchReportScheduler.pathInesistente";
        msg = resBundleGenerale.getString(logMessageKey);
      }
      break;

    case CostantiGenRicerche.REPORT_PROSPETTO:
      // esecuzione ricerca con modello
      pathFileReport = pathFileComposto;
      if (controlloPath(pathSchedulazioni, 1)
          && controlloPath(pathFileComposto, 0)) {
        DatiGenProspetto datiProspetto = prospettoManager.getProspettoById(idRicerca);
        if (datiProspetto != null) {
          if (isProspettoEseguibile(datiProspetto)) {
            if (ricManager.isRicercaConParametri(idRicerca)) {
              stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
              logMessageKey = "errors.batchReportScheduler.reportConParametri";
              msg = resBundleGenerale.getString(logMessageKey);
            } else {
              if (datiProspetto.getDatiModello().getIdRicercaSrc() == null)
                nomeFileComposto = eseguiReportConModelloConSorgenteDB(
                    schedulazione, idCodaSched, datiProspetto, account, codiceApplicazione);
              else
                nomeFileComposto = eseguiReportConModelloConSorgenteReport(
                    schedulazione, idCodaSched, datiProspetto, profilo, codiceApplicazione, idApplicazione,
                    codiceUfficioIntestatario);
            }
          } else {
            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            logMessageKey = "errors.batchReportScheduler.prospettoNonEseguibileNelProfiloAttivo";
            logger.error(resBundleGenerale.getString(logMessageKey));
            msg = resBundleGenerale.getString(logMessageKey);
          }
        } else {
          stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
          logMessageKey = "errors.batchReportScheduler.reportInesistente";
          msg = resBundleGenerale.getString(logMessageKey);
        }
      } else {
        stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;

        if (controlloPath(pathFileComposto, 0))
          logger.error(resBundleGenerale.getString(
              "errors.batchReportScheduler.pathInesistente.perLog").replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              pathFileComposto));
        if (controlloPath(pathSchedulazioni, 1))
          logger.error(resBundleGenerale.getString(
              "errors.batchReportScheduler.pathInesistente.perLog").replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              pathSchedulazioni));

        logMessageKey = "errors.batchReportScheduler.pathInesistente";
        msg = resBundleGenerale.getString(logMessageKey);

      }
      break;

    case CostantiGenRicerche.REPORT_SQL:
      // esecuzione ricerca avanzata
      if (controlloPath(pathSchedulazioni, 1)) {
        contenitore = ricManager.getRicercaByIdRicerca(idRicerca);
        if (contenitore != null) {
          if (ricManager.isRicercaConParametri(idRicerca)) {
            stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
            logMessageKey = "errors.batchReportScheduler.reportConParametri";
            msg = resBundleGenerale.getString(logMessageKey);
          } else {
            if (account != null
                || (account == null && contenitore.getDatiGenerali().getFiltroUtente() == 0)) {

              datiRisultato = ricManager.getRisultatiRicercaSql(contenitore, null, codiceApplicazione);

              if (datiRisultato.getNumeroRecordTotali() == 0) {
                logger.warn(resBundleGenerale.getString(
                "warnings.batchReportScheduler.reportVuotoForLog").replaceAll(
                UtilityStringhe.getPatternParametroMessageBundle(0),
                contenitore.getDatiGenerali().getNome()));
              }

              if (datiRisultato.getNumeroRecordTotali() == 0 && schedulazione.getNoOutputVuoto() == 1) {
                stato = CostantiCodaSched.STATO_ESEGUITO_CON_WARNING;
                logMessageKey = "warnings.batchReportScheduler.reportVuoto";
                msg = resBundleGenerale.getString(logMessageKey);
                bloccaReportVuoto = true;
              } else {
                if (CostantiSchedRic.FORMATO_EXCEL.equalsIgnoreCase(strFormato))
                  strFormato = CostantiSchedRic.ESTENSIONE_EXCEL;
                nomeFileComposto = exportRisultato(formato.intValue(),
                    strFormato, idCodaSched, datiRisultato, contenitore);
              }
            } else {
              stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
              logMessageKey = "errors.batchReportScheduler.reportNonEseguibileEsecutoreCancellato";
              logger.error(resBundleGenerale.getString(logMessageKey));
              msg = resBundleGenerale.getString(logMessageKey);
            }
          }
        } else {
          stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
          logMessageKey = "errors.batchReportScheduler.reportInesistente";
          msg = resBundleGenerale.getString(logMessageKey);
        }
      } else {
        stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;

        logger.error(resBundleGenerale.getString(
            "errors.batchReportScheduler.pathInesistente.perLog").replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(0),
            pathSchedulazioni));
        logMessageKey = "errors.batchReportScheduler.pathInesistente";
        msg = resBundleGenerale.getString(logMessageKey);
      }

      break;

      default:
        stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
        logger.error("Report non eseguito: tipo di report non supportato");
        logMessageKey = "errors.batchReportScheduler.tipoReportNonSupportato";
        msg = resBundleGenerale.getString(logMessageKey);
        break;
    }
    schedulazioneInCoda.setStato(stato);
    schedulazioneInCoda.setMsg(msg);
    if (stato == CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE) {
      logger.error("Report eseguito con errori");
    } else if (stato == CostantiCodaSched.STATO_ESEGUITO_CON_WARNING) {
      logger.warn("Report eseguito con segnalazioni");
     } else {
      fileComposto = zippaESpostaFile(nomeFileComposto, pathSchedulazioni,
          idCodaSched, pathFileReport, codiceApplicazione);
      if (logger.isInfoEnabled()) {
        logger.info("Report eseguito con successo");
      }
    }

    if (logger.isDebugEnabled()) logger.debug("eseguiReport: fine metodo");

    return fileComposto;
  }

  /**
   * Esegue il report con modello basato su sorgente dati DB (quindi è
   * specificata l'entità di partenza)
   *
   * @param schedulazione
   * @param idCodaSched
   * @param datiProspetto
   * @param account
   * @param codiceApplicazione
   * @return nome del file composto
   * @throws SqlComposerException
   * @throws QueryDaoException
   * @throws CriptazioneException
   * @throws RemoteException
   * @throws CompositoreException
   * @throws FileZipperException
   * @throws IOException
   */
  private String eseguiReportConModelloConSorgenteDB(
      SchedRic schedulazione, int idCodaSched, DatiGenProspetto datiProspetto, Account account, String codiceApplicazione)
      throws SqlComposerException, QueryDaoException, CriptazioneException,
      RemoteException, CompositoreException, FileZipperException, IOException {
    String nomeFileComposto = "";
    String logMessage = null;
    // carico i campi chiave della tabella principale e i loro valori
    DizionarioTabelle dizionario = DizionarioTabelle.getInstance();

    Tabella tabellaPrinc = dizionario.getDaNomeTabella(datiProspetto.getDatiGenRicerca().getEntPrinc());
    String[] valoriCampiChiave = prospettoManager.getChiavePrimoRecordEntitaPerCompositore(tabellaPrinc);

    if (valoriCampiChiave != null) {
      if (StringUtils.isNotEmpty(valoriCampiChiave[0])) {
        List<Campo> campiChiave = tabellaPrinc.getCampiKey();

        String nomiCampiChiave = "";
        for (int i = 0; i < campiChiave.size(); i++) {
          Campo campoKey = campiChiave.get(0);
          nomiCampiChiave = nomiCampiChiave.concat(campoKey.getNomeCampo()).concat(
              ";");
        }
        nomiCampiChiave = nomiCampiChiave.substring(0,
            nomiCampiChiave.length() - 1);

        // Se esiste l'utente esecutore, allora posso eseguire il
        // report con modello, altrimenti no.
        if (account == null) {
          stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
          logMessage = resBundleGenerale.getString("errors.batchReportScheduler.reportNonEseguibileEsecutoreCancellato");
          logger.error(logMessage);
          msg = logMessage;
        } else {

          String contesto = null;
          if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI))
              && account.getUfficioAppartenenza() != null)
            contesto = account.getUfficioAppartenenza().toString();

          int idSessione = 0;
          // Verifica che il report con modello abbia o meno come parametro
          // l'idUtente. In caso positivo si effettua l'inserimento del parametro
          // nella W_COMPARAM e si invoca la composizione con parametri.
          // Dopo la composizione, che l'esito sia positivo o negativo, bisogna
          // cancellare il parametro appena inserito nella W_COMPARAM.
          List<?> listaParametri = prospettoManager.getParametriModello(datiProspetto.getDatiGenRicerca().getIdProspetto().intValue());
          if (listaParametri != null && listaParametri.size() == 1) {

            ParametroModello parametroModello = (ParametroModello) listaParametri.get(0);

            ParametroComposizione parametro = new ParametroComposizione();

            parametro.setCodice(parametroModello.getCodice());
            parametro.setDescrizione(parametroModello.getDescrizione());
            parametro.setValore("" + schedulazione.getEsecutore());

            idSessione = prospettoManager.insertParametriComposizione(new ParametroComposizione[] { parametro });
          }
          try {
            nomeFileComposto = prospettoManager.componiModello(
                datiProspetto.getDatiModello().getIdModello(),
                datiProspetto.getDatiGenRicerca().getEntPrinc(),
                nomiCampiChiave, valoriCampiChiave, codiceApplicazione,
                schedulazione.getEsecutore(), contesto, idSessione);
          } finally {
            if (idSessione > 0)
              prospettoManager.deleteParametriComposizione(idSessione);
          }

        }
      } else {
        stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
        logMessage = resBundleGenerale.getString("errors.batchReportScheduler.eseguiProspetto.noDatiEntitaPrincipale");
        logger.error(logMessage);
        msg = logMessage;
      }
      return nomeFileComposto;
    } else {
      stato = CostantiCodaSched.STATO_ESEGUITO_CON_ERRORE;
      logMessage = resBundleGenerale.getString("errors.batchReportScheduler.eseguiProspetto.noCampiChiaveEntitaPrincipale");
      logger.error(logMessage);
      msg = logMessage;
      return nomeFileComposto;
    }
  }

  /**
   * Esegue il report con modello basato su sorgente dati report (quindi viene
   * eseguito un report, generato un file xml dal risultato estratto, e quindi
   * richiamato il compositore)
   *
   * @param schedulazione
   * @param idCodaSched
   * @param datiProspetto
   * @param codiceApplicazione
   * @param idApplicazione
   * @return nome del file composto
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws TransformerException
   * @throws TransformerFactoryConfigurationError
   * @throws FactoryConfigurationError
   * @throws TransformerConfigurationException
   * @throws FileNotFoundException
   * @throws QueryDaoException
   * @throws SqlComposerException
   */
  private String eseguiReportConModelloConSorgenteReport(
      SchedRic schedulazione, int idCodaSched, DatiGenProspetto datiProspetto,
      ProfiloUtente profiloUtente, String codiceApplicazione, String idApplicazione,
      String codiceUfficioIntestatario) throws ParserConfigurationException,
        SqlComposerException, QueryDaoException, FileNotFoundException,
        TransformerConfigurationException, FactoryConfigurationError,
        TransformerFactoryConfigurationError, TransformerException, IOException {

    // esegue il report base/avanzato sorgente dati e crea il file xml di input
    // per il modello
    String nomeFileSorgenteDati = ricManager.getFileXmlRisultatoReport(
        datiProspetto.getDatiModello(), codiceUfficioIntestatario, profiloUtente, codiceApplicazione,
        idApplicazione);

    String contesto = null;
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI)))
      contesto = profiloUtente.getUfficioAppartenenza();

    // esegue la composizione del modello
    return prospettoManager.componiModelloSenzaConnessioneDB(
        datiProspetto.getDatiModello().getIdModello(),
        nomeFileSorgenteDati,
        codiceApplicazione, profiloUtente.getId(),
        contesto);
  }


  /**
   * Metodo per stabilire se il report con modello schedulato è eseguibile nel
   * profilo attivo o meno.
   *
   * @param datiGenProspetto
   * @return Ritorna true se il report con modello è eseguibile nel profilo
   *         attivo, false altrimenti
   */
  private boolean isProspettoEseguibile(DatiGenProspetto datiGenProspetto) {
    boolean esito = true;
    // Sabbadin 01/04/2010: nel caso di report con modello collegato ad un
    // report base/avanzato come sorgente dati, non c'è alcun controllo da
    // effettuare in quanto l'entità principale non è valorizzata; pertanto è
    // stato introdotto il controllo che il report con modello sia collegato ad
    // un'entità principale
    if (datiGenProspetto.getDatiGenRicerca().getEntPrinc() != null) {
      DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
      Tabella tabellaPrincipale = dizTabelle.getDaNomeTabella(datiGenProspetto.getDatiGenRicerca().getEntPrinc());
      esito = geneManager.getGestoreVisibilitaDati().checkEntitaVisibile(
          tabellaPrincipale,
          datiGenProspetto.getDatiGenRicerca().getProfiloOwner());
    }
    return esito;
  }

  /**
   * Comprime il file contenente il report generato e lo sposta nella cartella
   * predefinita
   *
   * @param fileComposto
   * @param pathSchedulazioni
   * @param idCodaSched
   * @param pathFileReport
   * @param codiceApplicazione
   *
   * @return nome del file zippato
   *
   * @throws FileZipperException
   * @throws IOException
   */
  private String zippaESpostaFile(String fileComposto,
      String pathSchedulazioni, int idCodaSched, String pathFileReport, String codiceApplicazione)
      throws FileZipperException, IOException {
    if (logger.isDebugEnabled())
      logger.debug("zippaESpostaFile: inizio metodo");
    String[] filesToZip = new String[1];
    filesToZip[0] = pathFileReport + fileComposto;
    String zipFileName = pathSchedulazioni + idCodaSched + ".zip";

    FileOutputStream fos = new FileOutputStream(zipFileName);
    FileZipper.zip(fos, filesToZip);
    fos.close();

    if ("".equals(pathFileReport)) {
      File fileFisicoComposto = new File(filesToZip[0]);
      if (fileFisicoComposto.exists()) fileFisicoComposto.delete();
    } else {
      // si elimina il modello composto ed i file temporanei (.err, .xml, ....)
      prospettoManager.eliminaFileComposto(fileComposto, codiceApplicazione);
    }

    if (logger.isInfoEnabled())
      logger.info("File " + idCodaSched + ".zip creato con successo");

    if (logger.isDebugEnabled()) logger.debug("zippaESpostaFile: fine metodo");
    return zipFileName;

  }

  /**
   * Aggiorna date e stato della schedulazione
   */
  private void aggiornaDateEStato(SchedRicManager schedRicManager,
      SchedRic schedulazione, String msg, int stato, int idCodaSched,
      Date dataDiPartenza, String fileComposto) throws DataAccessException {

    if (logger.isDebugEnabled())
      logger.debug("aggiornaDateEStato: inizio metodo");
    if (fileComposto != null && fileComposto.length() > 0) {
      int start = fileComposto.lastIndexOf("/");
      if (start == -1) {
        start = fileComposto.lastIndexOf("\\");
      }
      schedRicManager.updateStatoCodaSched(idCodaSched, stato, msg,
          fileComposto.substring(start + 1));
    } else {
      schedRicManager.updateStatoCodaSched(idCodaSched, stato, msg, null);
    }
    // Aggiornamento stato della coda effettuato

    if (logger.isInfoEnabled())
      logger.info("Aggiornato stato della coda a " + stato);

    schedulazione.setDataUltEsec(dataDiPartenza);

    DataSchedulazione dataProxEsec = null;
    String strDataProxEsec = "";

    if (schedulazione.getNomeRicerca() != null) {
      // in caso di schedulazione unica non deve essere aggiornata la data
      // prossima esecuzione dato che la unica esecuzione è stata effettuata
      if (!CostantiSchedRic.UNICA.equalsIgnoreCase(schedulazione.getTipo())) {
        dataProxEsec = CalcoloDate.calcolaDataProxEsec(schedulazione,
            new Date());
        strDataProxEsec = UtilityDate.convertiData(dataProxEsec.getData(),
            UtilityDate.FORMATO_GG_MM_AAAA);
      } else {
        dataProxEsec = new DataSchedulazione();
        // si imposta ora e minuti identici a quelli utilizzati, mentre la data
        // rimane non valorizzata in modo da non pianificare nuovamente la
        // schedulazione
        dataProxEsec.setOra(schedulazione.getOraAvvio());
        dataProxEsec.setMinuti(schedulazione.getMinutoAvvio());
        strDataProxEsec = "Schedulazione unica, non esiste una prossima esecuzione";
      }
    } else {
      strDataProxEsec = "Non esiste una data prossima esecuzione, perchè non "
          + "esiste il report associato alla schedulazione";
    }

    schedRicManager.updateDataProxEsecSchedRic(
        schedulazione.getIdSchedRic().intValue(), dataProxEsec.getData(),
        dataProxEsec.getOra(), dataProxEsec.getMinuti(), dataDiPartenza);

    if (logger.isInfoEnabled())
      logger.info("Aggiornata data prossima esecuzione ("
          + strDataProxEsec
          + ") della schedulazione");

    if (logger.isDebugEnabled())
      logger.debug("aggiornaDateEStato: fine metodo");
  }

  /**
   * Invia la mail di avvenuta esecuzione all'indirizzo settato nella
   * schedulazione con l'esito e l'eventuale allegato di risultato. L'invio non
   * avviene nel caso di configurazione della schedulazione che non deve
   * produrre un risultato per report vuoto.
   *
   * @param schedulazione
   * @param pathFileComposto
   * @param fileComposto
   * @param schedulazioneInCoda
   * @throws MailSenderException
   */
  private void inviaMail(SchedRic schedulazione,
      String pathFileComposto, String fileComposto,
      CodaSched schedulazioneInCoda) throws MailSenderException {
    if (logger.isDebugEnabled()) logger.debug("inviaMail: inizio metodo");

    if (schedulazione.getEmail() != null
        && schedulazione.getEmail().length() > 0 && !bloccaReportVuoto) {

      // Se la schedulazione in esecuzione ha specificato un indirizzo mail, e
      // il report eventualmente estratto va inviato solo se non vuoto
      // allora bisogna inviare la mail con l'esito e l'eventuale report
      // prodotto

      // Lettura delle properties per configurazione degli oggetti per invio
      // mail
      IMailSender mailSender = MailUtils.getInstance(this.mailManager, schedulazione.getCodiceApplicazione(), CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);

      String nomeApplicativo = ConfigManager.getValore(CostantiGenerali.PROP_TITOLO_APPLICATIVO);

      String testoMail = null;
      if (schedulazioneInCoda.getStato() == CostantiCodaSched.STATO_ESEGUITO_CON_SUCCESSO) {
        testoMail = TESTO_MAIL_SUCCESS + ".\n\n"  + nomeApplicativo;
        if (schedulazione.getEmail().indexOf(",") > 0) {
          mailSender.send(schedulazione.getEmail().split(","), null, null,
              nomeApplicativo + OGGETTO_MAIL + " '" + schedulazione.getNomeRicerca() + "' ",
              testoMail, new String[] { fileComposto });
        } else {
          mailSender.send(new String[] { schedulazione.getEmail() }, null,
              null,
              nomeApplicativo + OGGETTO_MAIL + " '" + schedulazione.getNomeRicerca() + "' ",
              testoMail, new String[] { fileComposto });
        }
      } else {
        testoMail = TESTO_MAIL_ERROR
            + schedulazioneInCoda.getMsg()
            + ".\n\n" + nomeApplicativo;

        // Preparo l'oggetto della mail...
        String tmpOggetto = nomeApplicativo + OGGETTO_MAIL;
        if (schedulazione.getNomeRicerca() != null) {
          tmpOggetto = tmpOggetto.concat(" '"
              + schedulazione.getNomeRicerca()
              + "' ");
        }

        if (schedulazione.getEmail().indexOf(",") > 0) {
          mailSender.send(schedulazione.getEmail().split(","), null, null,
              tmpOggetto, testoMail, null);
        } else {
          mailSender.send(new String[] { schedulazione.getEmail() }, null,
              null, tmpOggetto, testoMail, null);
        }
      }

      if (logger.isInfoEnabled())
        logger.info("Mail inviata all'indirizzo " + schedulazione.getEmail());
    }
    if (logger.isDebugEnabled()) logger.debug("inviaMail: fine metodo");
  }

  /**
   * Metodo per il controllo se un path esiste ed e' accessibile dal batch
   *
   * @param path
   *        Percorso
   * @param modalitaDiAccesso
   *        <li>0 = accesso in lettura</li>
   *        <li>1 = accesso in lettura/scrittura</li>
   * @return Ritorna true se il path esiste ed e' accessibile nella modalita'
   *         specificata, false altrimenti
   */
  private static boolean controlloPath(String path, int modalitaDiAccesso) {
    boolean result = false;
    if (path != null) {
      File percorso = new File(path);
      if (percorso != null) {
        switch (modalitaDiAccesso) {
        case 0:
          if (percorso.isDirectory() && percorso.canRead()) result = true;
          break;
        case 1:
          if (percorso.isDirectory()
              && percorso.canRead()
              && percorso.canWrite()) result = true;
          break;
        }
      }
    }
    return result;
  }

}