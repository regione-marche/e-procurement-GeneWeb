package it.eldasoft.gene.web.struts;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.utility.UtilityDate;

public class SetW_MESSAGEAction extends ActionAjaxLogged {

  static Logger               logger                        = Logger.getLogger(SetW_MESSAGEAction.class);

  private static final String MESSAGGIO_IN_ARRIVO           = "IN";
  private static final String MESSAGGIO_IN_USCITA_O_INVIATO = "OUT";

  private static final String INSERIMENTO_MESSAGGIO         = "INSERT";
  private static final String CANCELLAZIONE_MESSAGGIO       = "DELETE";
  private static final String MARCA_LETTURA_MESSAGGIO       = "READ";

  private SqlManager          sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String target = null;
    String messageKey = null;

    DataSourceTransactionManagerBase.setRequest(request);

    try {

      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      if (profilo != null) {
        Long syscon = new Long(profilo.getId());

        String type = request.getParameter("type");
        String operation = request.getParameter("operation");
        String id = request.getParameter("id");
        String recipientuff = request.getParameter("recipientuff");
        String recipientusr = request.getParameter("recipientusr");
        String subject = request.getParameter("subject");
        String body = request.getParameter("body");
        String read = request.getParameter("read");

        TransactionStatus status = null;
        boolean commitTransaction = false;
        try {
          status = this.sqlManager.startTransaction();

          // Messaggio in uscita o gia' inviato
          if (MESSAGGIO_IN_USCITA_O_INVIATO.equals(type)) {

            // Inserimento di un nuovo messaggio
            if (INSERIMENTO_MESSAGGIO.equals(operation)) {
              String insertW_MESSAGE_OUT = "insert into w_message_out (message_id, message_date, message_subject, message_body, message_sender_syscon) values (?,?,?,?,?)";
              Long maxMessageIdOut = (Long) this.sqlManager.getObject("select max(message_id) from w_message_out", new Object[] {});
              if (maxMessageIdOut == null) maxMessageIdOut = new Long(0);
              maxMessageIdOut = new Long(maxMessageIdOut.longValue() + 1);
              this.sqlManager.update(insertW_MESSAGE_OUT,
                  new Object[] { maxMessageIdOut, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), subject, body, syscon });

              HashSet<Long> rhs = new HashSet<Long>();

              // Gestione uffici intestatari
              if (recipientuff != null && !"".equals(recipientuff)) {
                String recipientUffArray[] = recipientuff.split(",");
                if (recipientUffArray != null && recipientUffArray.length > 0) {
                  for (int ir = 0; ir < recipientUffArray.length; ir++) {
                    List<?> datiUSR_EIN = sqlManager.getListVector(
                        "select distinct syscon from usr_ein, w_accpro where w_accpro.id_account = usr_ein.syscon and usr_ein.codein = ?",
                        new Object[] { recipientUffArray[ir] });
                    if (datiUSR_EIN != null && datiUSR_EIN.size() > 0) {
                      for (int ue = 0; ue < datiUSR_EIN.size(); ue++) {
                        rhs.add((Long) SqlManager.getValueFromVectorParam(datiUSR_EIN.get(ue), 0).getValue());
                      }
                    }
                  }
                }
              }

              // Gestione utenti
              if (recipientusr != null && !"".equals(recipientusr)) {
                String recipientUsrArray[] = recipientusr.split(",");
                if (recipientUsrArray != null && recipientUsrArray.length > 0) {
                  for (int ir = 0; ir < recipientUsrArray.length; ir++) {
                    if ((new Long(-999999)).equals(new Long(recipientUsrArray[ir]))) {
                      // Si devono aggiungere tutti gli utenti della USRSYS
                      List<?> datiUSRSYS = sqlManager.getListVector(
                          "select distinct syscon from usrsys, w_accpro where w_accpro.id_account = usrsys.syscon", new Object[] {});
                      if (datiUSRSYS != null && datiUSRSYS.size() > 0) {
                        for (int us = 0; us < datiUSRSYS.size(); us++) {
                          rhs.add((Long) SqlManager.getValueFromVectorParam(datiUSRSYS.get(us), 0).getValue());
                        }
                      }
                    } else {
                      rhs.add(new Long(recipientUsrArray[ir]));
                    }
                  }
                }
              }

              for (Iterator<Long> ir = rhs.iterator(); ir.hasNext();) {
                inserimentoMessaggioInUscita(syscon, subject, body, maxMessageIdOut, ir.next());
              }

              commitTransaction = true;
            }

            // Cancellazione di un messaggio esistente
            if (CANCELLAZIONE_MESSAGGIO.equals(operation)) {
              int righeEliminate = this.sqlManager.update("delete from w_message_out where message_id = ? and message_sender_syscon = ?",
                  new Object[] { new Long(id), syscon });
              if (righeEliminate == 1) {
                // solo l'utente proprietario del messaggio puo' cancellarsi i
                // messaggi inviati e relativi destinatari, altrimenti non si
                // cancella nulla
                this.sqlManager.update("delete from w_message_out_rec where message_id = ?", new Object[] { new Long(id) });
              }
              commitTransaction = true;
            }

          }

          // Messaggio in arrivo
          if (MESSAGGIO_IN_ARRIVO.equals(type)) {

            // Marca stato lettura messaggio esistente
            if (MARCA_LETTURA_MESSAGGIO.equals(operation)) {
              this.sqlManager.update(
                  "update w_message_in set message_recipient_read = ? where message_id = ? and message_recipient_syscon = ?",
                  new Object[] { new Long(read), new Long(id), syscon });
              commitTransaction = true;
            }

            // Cancellazione messaggio esistente
            if (CANCELLAZIONE_MESSAGGIO.equals(operation)) {
              this.sqlManager.update("delete from w_message_in where message_id = ? and message_recipient_syscon = ?",
                  new Object[] { new Long(id), syscon });
              commitTransaction = true;
            }

          }

        } catch (Exception e) {
          commitTransaction = false;
        } finally {
          if (status != null) {
            if (commitTransaction) {
              this.sqlManager.commitTransaction(status);
            } else {
              this.sqlManager.rollbackTransaction(status);
            }
          }
        }
      }

    } catch (Throwable e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (target != null) {
      return mapping.findForward(target);
    } else {
      return null;
    }

  }

  /**
   * Inserimento lista destinatari in W_MESSAGE_OUT_REC e messaggio per ogni
   * destinatario in W_MESSAGE_IN.
   *
   * @param syscon
   * @param subject
   * @param body
   * @param maxMessageIdOut
   * @param ra_recipient
   * @throws SQLException
   */
  private void inserimentoMessaggioInUscita(Long syscon, String subject, String body, Long maxMessageIdOut, Long syscon_recipient)
      throws SQLException {
    String insertW_MESSAGE_OUT_REC = "insert into w_message_out_rec (recipient_id, message_id, recipient_syscon) values (?,?,?)";
    Long maxRecipientId = (Long) this.sqlManager.getObject("select max(recipient_id) from w_message_out_rec", new Object[] {});
    if (maxRecipientId == null) maxRecipientId = new Long(0);
    maxRecipientId = new Long(maxRecipientId.longValue() + 1);
    this.sqlManager.update(insertW_MESSAGE_OUT_REC, new Object[] { maxRecipientId, maxMessageIdOut, syscon_recipient });

    String insertW_MESSAGE_IN = "insert into w_message_in (message_id, message_date, message_subject, message_body, message_sender_syscon, message_recipient_syscon, message_recipient_read) values (?,?,?,?,?,?,?)";
    Long maxMessageIdIn = (Long) this.sqlManager.getObject("select max(message_id) from w_message_in", new Object[] {});
    if (maxMessageIdIn == null) maxMessageIdIn = new Long(0);
    maxMessageIdIn = new Long(maxMessageIdIn.longValue() + 1);
    this.sqlManager.update(insertW_MESSAGE_IN, new Object[] { maxMessageIdIn, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),
        subject, body, syscon, syscon_recipient, new Long(0) });
  }
}
