/*
 * Created on 27/05/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.tasks;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.scadenz.AbstractGestorePromemoriaScadenzario;
import it.eldasoft.gene.bl.scadenz.DefaultGestorePromemoriaScadenzario;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class NotificaPromemoriaScadenzariScheduler {

  static Logger       logger = Logger.getLogger(NotificaPromemoriaScadenzariScheduler.class);

  private SqlManager  sqlManager;

  private MailManager mailManager;

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
   * Task attivato per verificare se ci sono attivit&agrave; in scadenza per le quali va generato il promemoria.
   *
   * @throws GestoreException
   */
  @SuppressWarnings("unchecked")
  public void notificaPromemoriaScadenzari() throws GestoreException {

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato ed inoltre deve esistere l'OP128
    if (WebUtilities.isAppNotReady()) return;
    ServletContext context = SpringAppContext.getServletContext();
    if (!GeneManager.checkOP(context, "OP128")) return;
    String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

    ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

    logger.debug("notificaPromemoriaScadenzari: inizio metodo");

    // Lettura dei dati dello scadenzario
    String select = "select id,ent,datascad,ggpromem,refpromem,destpromem,key1,key2,key3,key4,key5,discr from g_scadenz where prg=? and prev=? and (destpromem is not null or refpromem is not null)"
        + " and ggpromem <> ? and (stpromem is null or stpromem <> 1)order by id";

    List<Vector<JdbcParametro>> datiG_SCADENZ = null;
    try {
      datiG_SCADENZ = this.sqlManager.getListVector(select, new Object[] {codapp, new Long((0)), new Long((0)) });
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura dei dati dello scadenzario per l'applicativo: " + codapp, null, e);
    }

    if (datiG_SCADENZ != null && datiG_SCADENZ.size() > 0) {
      Long id = null;
      String ent = null;
      Timestamp datascad = null;
      Long ggpromem = null;
      String refpromem = null;
      String destpromem = null;
      Object chiave[] = new Object[5];
      String discriminante = null;
      String oggetto = null;
      String testo = null;
      String nomeApplicativo = ConfigManager.getValore(CostantiGenerali.PROP_TITOLO_APPLICATIVO);

      for (int i = 0; i < datiG_SCADENZ.size(); i++) {
        id = SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), 0).longValue();
        ent = SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), 1).stringValue();
        datascad = SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), 2).dataValue();
        ggpromem = SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), 3).longValue();
        refpromem = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), 4).stringValue());
        destpromem = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), 5).stringValue());
        destpromem  = StringUtils.remove(destpromem, ' ');
        for (int j = 0; j < 5; j++)
          chiave[j] = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), j + 6).stringValue());

        discriminante = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), 11).stringValue());

        // Si devono considerare solo le occorrenze per cui Data attuale>=DATASCAD-GGPROMEM
        // Date dataOdierna = UtilityDate.getDataOdiernaAsDate();
        // Date dataOdierna = GregorianCalendar.getInstance().getTime();
        Date dataOdierna = UtilityDate.convertiData(UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA),
            UtilityDate.FORMATO_GG_MM_AAAA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(datascad.getTime()));
        calendar.add(Calendar.DATE, -ggpromem.intValue());

        // Si invia la mail se refpromem e destpromem sono valorizzati
        if (dataOdierna.compareTo(calendar.getTime()) >= 0 && !(refpromem == null && destpromem == null)) {
          List<String> inviaA = new ArrayList<String>();
          try {
            if (refpromem != null) {
              String selectView = "select fromview,ent from g_refpromscadenz where cod=?";
              Vector<JdbcParametro> datiFromview = this.sqlManager.getVector(selectView, new Object[] {refpromem });
              if (datiFromview != null && datiFromview.size() > 0) {
                String view = (String) (datiFromview.get(0)).getValue();
                String entita = (String) (datiFromview.get(1)).getValue();

                Object[] params = null;
                Tabella tabella = DizionarioTabelle.getInstance().getDaNomeTabella(entita);
                params = new Object[tabella.getCampiKey().size() + 1];
                // params = new Object[tabella.getCampiKey().size()];
                params[0] = refpromem;
                StringBuilder sb = new StringBuilder("SELECT mail FROM ");
                sb.append(view);
                sb.append(" where codreferente=?");
                for (int j = 0; j < tabella.getCampiKey().size(); j++) {
                  sb.append(" and ").append(tabella.getCampiKey().get(j).getNomeCampo()).append("=?");
                  params[1 + j] = chiave[j];
                }
                inviaA.add((String) this.sqlManager.getObject(sb.toString(), params));
              }
            }
            if (destpromem != null) {
              inviaA.addAll(Arrays.asList(destpromem.split(",")));
            }

            // Oggetto della mail
            oggetto = nomeApplicativo
                + ": notifica scadenza attività in data "
                + UtilityDate.convertiData(datascad, UtilityDate.FORMATO_GG_MM_AAAA);

            // Testo della mail
            String selectGestore = "select gestore from g_genpromscadenz where prg=? and ent=? and discr=?";
            String gestore = StringUtils.stripToNull((String) this.sqlManager.getObject(selectGestore, new Object[] {codapp, ent,
                discriminante }));

            // Se gestore è nullo si istanzia la classe di Default
            AbstractGestorePromemoriaScadenzario gestorePromemoria = this.getGestorePromemoriaScadenzario(context, gestore);
            if (gestorePromemoria != null) {
              testo = gestorePromemoria.getTestoMail(codapp, id, ent, chiave);

              if (gestore == null) {
                String msgWarn = "Manca un gestore promemoria scadenzario per PRG="
                    + codapp
                    + ", ENT="
                    + ent
                    + ", DISCR="
                    + discriminante
                    + ". Utilizzato in sua assenza il gestore di default";
                logger.warn(msgWarn);
              }
              // Invio della mail
              IMailSender iMailSender = MailUtils.getInstance(mailManager,
                  ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE),CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);
              iMailSender.send(inviaA.toArray(new String[inviaA.size()]), null, null, oggetto, testo, null);

              // L'invio della mail e' avvenuto correttamente, imposto il valore del campo STPROMEM
              this.sqlManager.update("update g_scadenz set stpromem = ? where id = ?", new Object[] {new Long(1), id});

              // 20171220: si introduce un eventuale delay di attesa per risolvere gli interfacciamenti con provider di posta che
              // bloccano attivita massive mediante antispam
              if (iMailSender.getDelay() != null) {
                try {
                  TimeUnit.MILLISECONDS.sleep(iMailSender.getDelay());
                } catch (InterruptedException e) {
                  logger.error("Errore durante l'attesa tra un invio di una mail di promemoria e la successiva", e);
                }
              }
            } else {
              logger.error("Impossibile inviare il promemoria, non esiste il gestore:" + gestore);
            }

          } catch (SQLException e) {
            // throw new GestoreException("Errore nella lettura dei dati per l'invio del promemoria dell'attivita: " + id, null, e);
            logger.error("Errore nella lettura dei dati per l'invio del promemoria dell'attivita: " + id, e);
          } catch (MailSenderException e) {
            String logMessageKey = e.getChiaveResourceBundle();
            String logMessageError = resBundleGenerale.getString(logMessageKey);
            for (int z = 0; e.getParametri() != null && i < e.getParametri().length; i++) {
              logMessageError = logMessageError.replaceAll(UtilityStringhe.getPatternParametroMessageBundle(z),
                  (String) e.getParametri()[z]);
            }
            logger.error(logMessageError, e);
          } catch (Exception e) {
            logger.error("Errore nella creazione del testo del promemoria per l'attivita: " + id, e);
          }

        }

      }
    }

    logger.debug("notificaPromemoriaScadenzari: fine metodo");

  }

  /**
   * Sfruttando la reflection viene stabilito quale classe istanziare per il gestore del modello del promemoria.
   *
   * @param context
   *        context applicativo
   * @param classe
   *        nome del gestore da istanziare, se nullo viene instanziato il gestore di default
   * @return AbstractGestorePromemoriaScadenzario gestore istanziato
   * @throws GestoreException
   */

  private AbstractGestorePromemoriaScadenzario getGestorePromemoriaScadenzario(ServletContext context, String classe)
      throws GestoreException {

    AbstractGestorePromemoriaScadenzario gestoreResult = null;
    // se non è assegnato gestore ne assegno uno di default
    if (classe == null) {
      gestoreResult = new DefaultGestorePromemoriaScadenzario();
      gestoreResult.setServletContext(context);
      // gestoreResult.setContextPath(contextPath)
    } else {
      // se è stato indicato un gestore controllo che sia una classe esistente e
      // del giusto tipo
      Object obj = UtilityTags.createObject(classe);
      if (obj != null && (obj instanceof AbstractGestorePromemoriaScadenzario)) {
        AbstractGestorePromemoriaScadenzario gest = (AbstractGestorePromemoriaScadenzario) obj;
        gest.setServletContext(context);
        gestoreResult = (AbstractGestorePromemoriaScadenzario) obj;
      } else {
        // se non esiste il gestore indicato o non è una AbstractGestoreEntita
        logger.error("Il gestore di promemoria " + classe + "non esiste");
      }

    }
    return gestoreResult;
  }

}
