package it.eldasoft.gene.bl.tasks;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.dao.DocumentoWDISCALLDao;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.system.ConfigurazioneMail;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class InviaConversazioniManager {

  static Logger                logger = Logger.getLogger(InviaConversazioniManager.class);

  private DocumentoWDISCALLDao documentoWDISCALLDao;
  private SqlManager           sqlManager;
  private MailManager          mailManager;

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
  public void inviaConversazioni() {

    if (WebUtilities.isAppNotReady()) return;

    if (logger.isDebugEnabled()) logger.debug("inviaConversazioni: inizio metodo");

    String selectW_DISCDEST = "select w_discdest.discid_p, " // 0
        + " w_discdest.discid, " // 1
        + " w_discdest.destnum, " // 2
        + " w_discdest.destname, " // 3
        + " w_discdest.destmail, " // 4
        + " w_discuss.discmesstesto, " // 5
        + " w_discuss.discmessope, " // 6
        + " w_discuss_p.discoggetto, " // 7
        + " w_discuss_p.discprg " // 8
        + " from w_discdest, w_discuss, w_discuss_p "
        + " where w_discdest.discid_p = w_discuss.discid_p "
        + " and w_discdest.discid = w_discuss.discid "
        + " and w_discuss.discid_p = w_discuss_p.discid_p "
        + " and w_discuss.discmesspubbl = '1' "
        + " and (w_discdest.destinvstato is null or w_discdest.destinvstato = 0 or w_discdest.destinvstato = 2) "
        + " order by w_discdest.discid_p, w_discdest.discid, w_discdest.destnum ";

    String selectW_DISCALL = "select allnum, "
        + " allname, "
        + " allnote "
        + " from w_discall "
        + " where discid_p = ? "
        + " and discid = ?"
        + " order by allnum";
    
    String selectW_DISCUSS_P = "select disckey1, "
    	+ " discent "
    	+ " from w_discuss_p "
    	+ " where discid_p = ? ";
    	
    String updateW_DISCDEST = "update w_discdest set destinvstato = ?, destinvmess = ? where discid_p = ? and discid = ? and destnum = ?";

    String selectW_MAIL = "select count(*) from w_mail where codapp = ? and idcfg = ?";
    
    String selectCOD_STIPULA = "select codstipula from g1stipula where id = ?";

    try {

      // Lettura delle email da inviare, una per ogni destinatario
      List<?> datiW_DISCDEST = this.sqlManager.getListVector(selectW_DISCDEST, new Object[] {});
      if (datiW_DISCDEST != null && datiW_DISCDEST.size() > 0) {
        for (int de = 0; de < datiW_DISCDEST.size(); de++) {
          Long discid_p = (Long) SqlManager.getValueFromVectorParam(datiW_DISCDEST.get(de), 0).getValue();
          Long discid = (Long) SqlManager.getValueFromVectorParam(datiW_DISCDEST.get(de), 1).getValue();
          Long destnum = (Long) SqlManager.getValueFromVectorParam(datiW_DISCDEST.get(de), 2).getValue();
          String destname = (String) SqlManager.getValueFromVectorParam(datiW_DISCDEST.get(de), 3).getValue();
          String destmail = (String) SqlManager.getValueFromVectorParam(datiW_DISCDEST.get(de), 4).getValue();
          String discmesstesto = (String) SqlManager.getValueFromVectorParam(datiW_DISCDEST.get(de), 5).getValue();
          Long discmessope = (Long) SqlManager.getValueFromVectorParam(datiW_DISCDEST.get(de), 6).getValue();
          String discoggetto = (String) SqlManager.getValueFromVectorParam(datiW_DISCDEST.get(de), 7).getValue();
          String discprg = (String) SqlManager.getValueFromVectorParam(datiW_DISCDEST.get(de), 8).getValue();
          
          String oggettoMail=discoggetto;
          
          List<?> datiW_DISCUSS_P = this.sqlManager.getListVector(selectW_DISCUSS_P, new Object[] {discid_p});
          String disckey1 = (String) SqlManager.getValueFromVectorParam(datiW_DISCUSS_P.get(0), 0).getValue();
          String discent = (String) SqlManager.getValueFromVectorParam(datiW_DISCUSS_P.get(0), 1).getValue();
          if("G1STIPULA".equals(discent)){
        	  String cod_stipula = (String) this.sqlManager.getObject(selectCOD_STIPULA, new Object[] {disckey1});
        	  oggettoMail = cod_stipula;
        	  oggettoMail = "Rif. " + oggettoMail + " - " + discoggetto;
          }
          else {
        	  if("GARE".equals(discent) || "TORN".equals(discent)){
        		  oggettoMail = disckey1;
        		  oggettoMail = "Rif. " + oggettoMail + " - " + discoggetto;
        	  }
          }

          // Lettura e gestione degli eventuali documenti allegati
          String[] fileAllegati = null;
          ByteArrayOutputStream[] contenutoFileAllegati = null;
          List<?> datiW_DISCALL = this.sqlManager.getListVector(selectW_DISCALL, new Object[] { discid_p, discid });
          if (datiW_DISCALL != null && datiW_DISCALL.size() > 0) {
            fileAllegati = new String[datiW_DISCALL.size()];
            contenutoFileAllegati = new ByteArrayOutputStream[datiW_DISCALL.size()];
            for (int al = 0; al < datiW_DISCALL.size(); al++) {
              Long allnum = (Long) SqlManager.getValueFromVectorParam(datiW_DISCALL.get(al), 0).getValue();
              String allname = (String) SqlManager.getValueFromVectorParam(datiW_DISCALL.get(al), 1).getValue();
              String allnote = (String) SqlManager.getValueFromVectorParam(datiW_DISCALL.get(al), 2).getValue();

              HashMap params = new HashMap();
              params.put("discid_p", discid_p);
              params.put("discid", discid);
              params.put("allnum", allnum);

              BlobFile allstream = this.documentoWDISCALLDao.getStream(params);
              ByteArrayOutputStream stream = new ByteArrayOutputStream();
              stream.write(allstream.getStream());
              stream.close();
              fileAllegati[al] = allname;
              contenutoFileAllegati[al] = stream;
            }
          }

          // Invio della email
          Long cntW_MAIL = (Long) this.sqlManager.getObject(selectW_MAIL, new Object[] { discprg,
              CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD_CONV });
          if (cntW_MAIL != null && cntW_MAIL.longValue() > 0) {
            int numeroTentativi = 5;
            int numTentativo = 0;
            boolean mailInviata = true;
            Long statoInvioMail = null;
            String messaggioInvioMail = null;

            MailSenderException mailSenderExceptiontmp = null;
            try {
              IMailSender mailSender = MailUtils.getInstance(this.mailManager, discprg,
                  CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD_CONV);
              String sysute = (String) sqlManager.getObject("select sysute from usrsys where syscon = ?", new Object[] { discmessope });
              mailSender.setNomeMittente(sysute);
              ConfigurazioneMail cfg = this.mailManager.getConfigurazione(discprg, CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD_CONV);

              do {
                numTentativo = numTentativo + 1;

                try {
                  // Gestione per invio senza o con allegati
                  if (fileAllegati == null) {
                    mailSender.send(destmail, oggettoMail, discmesstesto, false);
                  } else {
                    mailSender.send(new String[] { destmail }, null, null, oggettoMail, discmesstesto, fileAllegati, contenutoFileAllegati,
                        false);
                  }

                  // Gestione ritardo tra invii ripetuti
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
                  logger.error("Si è verificato un errore nell'invio della mail (codice errore "
                      + ms.getCodiceErrore()
                      + "), tentativi rimasti="
                      + numeroTentativi, ms);
                }
              } while (numeroTentativi > 0 && !mailInviata);

            } catch (MailSenderException ms) {
              mailSenderExceptiontmp = ms;
              mailInviata = false;
            }

            if (mailInviata) {
              statoInvioMail = new Long(1);
              messaggioInvioMail = "Notifica inviata in data "
                  + UtilityDate.convertiData(UtilityDate.getDataOdiernaAsDate(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
            } else {
              statoInvioMail = new Long(2);
              if (mailSenderExceptiontmp != null) {
                messaggioInvioMail = mailSenderExceptiontmp.getCodiceErrore() + "\n" + mailSenderExceptiontmp.toString();
                if (messaggioInvioMail.length() > 500) {
                  messaggioInvioMail = messaggioInvioMail.substring(0, 499);
                }
              }

            }

            // Aggiornamento della singola mail
            this.sqlManager.update(updateW_DISCDEST, new Object[] { statoInvioMail, messaggioInvioMail, discid_p, discid, destnum });

          }
        }
      }

    } catch (SQLException e) {
      logger.error("Si è verificato un errore nella lettura dei dati dalla base dati", e);
    } catch (IOException e) {
      logger.error("Si è verificato un errore nella gestione dei file allegati", e);
    } catch (Throwable e) {
      logger.error("Si è verificato un errore inaspettato", e);
    }

    if (logger.isDebugEnabled()) logger.debug("inviaConversazioni: fine metodo");
  }

  public void setDocumentoWDISCALLDao(DocumentoWDISCALLDao documentoWDISCALLDao) {
    this.documentoWDISCALLDao = documentoWDISCALLDao;
  }

}
