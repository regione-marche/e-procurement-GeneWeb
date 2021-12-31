/*
 * Created on Nov 21, 2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.registrazione;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.admin.ProfiliManager;
import it.eldasoft.gene.bl.permessi.PermessiManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioAccount;
import it.eldasoft.gene.web.struts.permessi.PermessiAccountEntitaForm;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore dell'entità UTENT per la registrazione degli utenti
 *
 * @author Francesco De Filippis
 *
 */
public class GestoreUtentRegistrazione extends AbstractGestoreEntita {

  // Costanti per messaggi di errore dal resourcebundle

  private static final String MSG_ERROR_LOGIN_DUPLICATO = "gestoreTecnico.loginDuplicato";

  /**
   * Logger per tracciare messaggio di debug
   */
  static Logger               logger                    = Logger.getLogger(GestoreUtentRegistrazione.class);

  @Override
  public String getEntita() {
    return "UTENT";
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {

  }

  private void inviaMail(Account account) throws GestoreException {

    String nomeMittente = ConfigManager.getValore(CostantiGenerali.PROP_TITOLO_APPLICATIVO);
    String mailAmministratore = ConfigManager.getValore(CostantiGenerali.PROP_INDIRIZZO_MAIL_AMMINISTRATORE);

    boolean creazioneMailSender = false;
    boolean invioMailUtente = false;
    // boolean invioMailAdmin = false;

    try {
      MailManager mailManager = (MailManager) UtilitySpring.getBean("mailManager",
          this.getServletContext(), MailManager.class);

      IMailSender mailSender = MailUtils.getInstance(mailManager, ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE),CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);

      creazioneMailSender = true;
      String testoMail = null;
      String oggettoMail = null;

      // String testoMailUtente = null;
      // String oggettoMailUtente = null;

      ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          account.getLogin().getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
      String login = new String(decriptatore.getDatoNonCifrato());

      decriptatore = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          account.getPassword().getBytes(),
          ICriptazioneByte.FORMATO_DATO_CIFRATO);
      String password = new String(decriptatore.getDatoNonCifrato());

      String strAbilitazione = "Disabilitato";

      if (account.getUtenteDisabilitato().intValue() == new Integer(
          CostantiDettaglioAccount.ABILITATO).intValue()) {
        oggettoMail = resBundleGenerale.getString(CostantiGenerali.RESOURCE_OGGETTO_MAIL_REGISTRAZIONE_AUTOMATICA);
        oggettoMail = UtilityStringhe.replaceParametriMessageBundle(
            oggettoMail, new String[] { nomeMittente });

        testoMail = resBundleGenerale.getString(CostantiGenerali.RESOURCE_TESTO_MAIL_REGISTRAZIONE_AUTOMATICA);
        testoMail = UtilityStringhe.replaceParametriMessageBundle(testoMail,
            new String[] { account.getNome(), nomeMittente, login, password,
                nomeMittente });

        mailSender.send(account.getEmail(), oggettoMail, testoMail);
        strAbilitazione = "Abilitato";
        invioMailUtente = true;
      }

      oggettoMail = resBundleGenerale.getString(CostantiGenerali.RESOURCE_OGGETTO_MAIL_REGISTRAZIONE_AMMINISTRATORE);
      oggettoMail = UtilityStringhe.replaceParametriMessageBundle(oggettoMail,
          new String[] { nomeMittente });

      testoMail = resBundleGenerale.getString(CostantiGenerali.RESOURCE_TESTO_MAIL_REGISTRAZIONE_AMMINISTRATORE);
      testoMail = UtilityStringhe.replaceParametriMessageBundle(testoMail,
          new String[] { account.getNome(), login, password, strAbilitazione,
              nomeMittente });

      mailSender.send(mailAmministratore, oggettoMail, testoMail);

      // invioMailAdmin = true;
    } catch (MailSenderException ms) {

      String logMessageKey = ms.getChiaveResourceBundle();
      String logMessageError = resBundleGenerale.getString(logMessageKey);
      for (int i = 0; ms.getParametri() != null && i < ms.getParametri().length; i++)
        logMessageError = logMessageError.replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(i),
            (String) ms.getParametri()[i]);
      logger.error(logMessageError, ms);
      if (!creazioneMailSender) {
        logger.error(logMessageError, ms);
      } else {
        if (!invioMailUtente) {
          logMessageKey = "warnings.registrazione.mancatoInvioMailUtente";
          logMessageError = resBundleGenerale.getString(logMessageKey);
          UtilityStruts.addMessage(this.getRequest(), "warning", logMessageKey,
              null);
        } else {
          logMessageKey = "warnings.registrazione.mancatoInvioMailAdmin";
          logMessageError = resBundleGenerale.getString(logMessageKey);
        }
        logger.warn(logMessageError, ms);

      }

    } catch (CriptazioneException e) {
      throw new GestoreException(
          "Errore durante l'inserimento dell'occorrenza di USRSYS !",
          "updateUTENT", e);
    }
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    logger.debug("preInsert: inizio metodo");

    synchronized (GestoreUtentRegistrazione.class) {

      GeneManager gene = (GeneManager) UtilitySpring.getBean("geneManager",
          this.getServletContext(), GeneManager.class);

      // Se si ha la codifica automatica allora eseguo il ricalcolo

      String codute = gene.calcolaCodificaAutomatica("UTENT", "CODUTE");
      impl.setValue("UTENT.CODUTE", codute);

      // inserisco l'occorrenza di Usrsys
      AccountManager accountManager = (AccountManager) UtilitySpring.getBean(
          "accountManager", this.getServletContext(), AccountManager.class);

      ProfiliManager profiliManager = (ProfiliManager) UtilitySpring.getBean(
          "profiliManager", this.getServletContext(), ProfiliManager.class);

      String login = impl.getString("USRSYS.SYSLOGIN");
      String password = impl.getString("USRSYS.SYSPWD");
      Account account = new Account();
      String registrazioneAutomatica = ConfigManager.getValore(CostantiGenerali.PROP_REGISTRAZIONE_AUTOMATICA);
      try {
        if (!accountManager.isUsedLogin(login, -1)) {

          // account.setIdAccount((new Integer(codice)).intValue());
          account.setLogin(login);
          account.setPassword(password);
          account.setNome(impl.getString("UTENT.NOMUTE"));
          OpzioniUtente opzioniUtente = new OpzioniUtente(
              ConfigManager.getValore(CostantiGenerali.PROP_OPZIONI_UTENTE_DEFAULT));
          opzioniUtente.setOpzione(CostantiGeneraliAccount.OPZIONI_MENU_STRUMENTI);
          opzioniUtente.setOpzione(CostantiGeneraliAccount.OPZIONI_SICUREZZA_PASSWORD);
          account.setOpzioniUtente(opzioniUtente.toString());
          account.setEmail(impl.getString("UTENT.EMAIL"));
          account.setDataInserimento(new Date());
          account.setAbilitazioneStd(CostantiGeneraliAccount.DEFAULT_ABILITAZIONE_STANDARD);
          account.setFlagLdap(new Integer(0));

          // se l'applicativo è impostato per la registrazione automatica
          // l'utente è subito abilitato
          if ("1".equals(registrazioneAutomatica)) {
            account.setUtenteDisabilitato(new Integer(
                CostantiDettaglioAccount.ABILITATO));

          } else {
            // altrimenti è disabilitato
            account.setUtenteDisabilitato(new Integer(
                CostantiDettaglioAccount.DISABILITATO));
          }

          accountManager.insertAccount(account);

          // inserisco i dati nello storico delle password
          // (STOUTESYS) per evitare che alla prima connessione mi chieda di
          // cambiare la password; utilizzo i dati login e password presi dal
          // bean account perchè sono già criptati
          accountManager.insertStoriaAccount(account.getIdAccount(),
              account.getLogin(), account.getLoginCriptata(), account.getPassword());

          // inserisco l'associazione con il profilo di default che gestisce
          // automaticamente l'associazione al gruppi se abilitata
          boolean gruppiDis = false;
          String gruppiDisabilitati = ConfigManager.getValore(CostantiGenerali.PROP_GRUPPI_DISABILITATI);
          if ("1".equals(gruppiDisabilitati)) gruppiDis = true;

          String codiceApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

          String profiloUtenteDefault = ConfigManager.getValore(CostantiGenerali.PROP_PROFILO_DEFAULT_REGISTRAZIONE);

          profiliManager.updateAssociazioneProfiliAccount(
              account.getIdAccount(), new String[] { profiloUtenteDefault },
              codiceApplicazione, gruppiDis);

          // inserimento occorrenza su g_permessi
          PermessiManager permessiManager = (PermessiManager) UtilitySpring.getBean(
              "permessiManager", this.getServletContext(),
              PermessiManager.class);

          // inserisco la riga in g_permessi per l'utente registrato
          PermessiAccountEntitaForm permessi = new PermessiAccountEntitaForm();
          permessi.setIdPermesso(new String[] { "0" });
          permessi.setIdAccount(new String[] { new Integer(
              account.getIdAccount()).toString() });
          // setto la condivisione a 1 perchè con 0 elimina solo le occorrenze
          permessi.setCondividiEntita(new String[] { "1" });
          permessi.setAutorizzazione(new String[] { "1" });
          permessi.setProprietario(new String[] { "1" });
          permessi.setCampoChiave("CODUTE");
          permessi.setValoreChiave(codute);
          permessiManager.updateAssociazioneAccountEntita(permessi);

        } else {
          throw new GestoreException("Errore: login duplicato!",
              MSG_ERROR_LOGIN_DUPLICATO, null);
        }
      } catch (CriptazioneException e) {
        throw new GestoreException(
            "Errore durante l'inserimento dell'occorrenza di USRSYS",
            "preInsert", e);
      } catch (SqlComposerException e) {
        throw new GestoreException(
            "Errore durante la verifica dei dati in USRSYS",
            "preInsert", e);
      }

      impl.setValue("UTENT.SYSCON", new JdbcParametro(
          JdbcParametro.TIPO_NUMERICO, new Integer(account.getIdAccount())));

    }
    logger.debug("preInsert: fine metodo");

  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {

    String login = impl.getString("USRSYS.SYSLOGIN");
    AccountManager accountManager = (AccountManager) UtilitySpring.getBean(
        "accountManager", this.getServletContext(), AccountManager.class);

//    ICriptazioneByte criptatore;
//    try {
//      criptatore = FactoryCriptazioneByte.getInstance(
//          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
//          login.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
//      login = new String(criptatore.getDatoCifrato());
//    } catch (CriptazioneException e) {
//      throw new GestoreException(
//          "Errore durante l'inserimento dell'occorrenza di USRSYS !",
//          "updateUTENT", e);
//    }

    try {
      Account account = accountManager.getAccountByLogin(login);
      // inserimento avvenuto invio la mail
      inviaMail(account);
    } catch (SqlComposerException e) {
      throw new GestoreException("Errore durante la lettura del dato inserito in USRSYS", "postInsert", e);
    }

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }

}