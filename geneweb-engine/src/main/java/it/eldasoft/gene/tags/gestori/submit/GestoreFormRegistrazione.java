/*
 * Created on marzo 2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.gestori.submit;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.admin.ProfiliManager;
import it.eldasoft.gene.bl.permessi.PermessiManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioAccount;
import it.eldasoft.gene.web.struts.permessi.PermessiAccountEntitaForm;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreTECNI;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.eldasoft.utils.utility.UtilityPassword;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per la registrazione di un nuovo utente applicativo
 *
 *
 */
public class GestoreFormRegistrazione extends AbstractGestoreEntita {

  private static final String REGISTRAZIONE_UTENTE_CAPTCHA_ERRORE              = "registrazione.utente.captcha.errore";

  private static final String MSG_ERROR_LOGIN_DUPLICATO = "gestoreFormRegistrazione.loginDuplicato";

  private static final String MSG_ERROR_ENTE_INESISTENTE = "gestoreFormRegistrazione.enteInesistente";

  private static final String MSG_ERROR_LOGIN_NOCFPIVA = "gestoreFormRegistrazione.loginNoCFPIVA";

  private static final String MSG_ERROR_PASSWORD_LENGTH = "gestoreFormRegistrazione.passwordLen";

  private static final String MSG_ERROR_PASSWORD_CHRNOAMM = "gestoreFormRegistrazione.passwordChrNoAmm";

  private static final String MSG_ERROR_PASSWORD_CHR2NUM = "gestoreFormRegistrazione.passwordChr2Num";

  private static final String MSG_ERROR_PASSWORD_SIMIL_NOME = "gestoreFormRegistrazione.passwordSimilarityNomeUtente";

  private static final String MSG_ERROR_PASSWORD_SIMIL_COGNOME = "gestoreFormRegistrazione.passwordSimilarityCognomeUtente";

  private static final String MSG_ERROR_PASSWORD_SIMIL_CF = "gestoreFormRegistrazione.passwordSimilarityCFUtente";

  private static final String MSG_ERROR_EMAIL_NOPRES = "gestoreFormRegistrazione.emailNoPres";

  private static final String MSG_ERROR_RUOLO_NOPRES = "gestoreFormRegistrazione.ruoloNoPres";

  private static final String MSG_ERROR_APPDISP_NOPRES = "gestoreFormRegistrazione.appDispNoPres";

  private static final String MSG_ERROR_RUOLO_VALNOAMM = "gestoreFormRegistrazione.ruoloValNoAmm";

  private static final String MSG_ERROR_APPSCELTO_NOCONF = "gestoreFormRegistrazione.appSceltoNoConf";

  private static final String MSG_ERROR_APPDISP_NOCONF = "gestoreFormRegistrazione.appDispNoConf";

  private static final String MSG_ERROR_PWDDEF_NOCONF = "gestoreFormRegistrazione.pwdDefNoConf";

  private static final String MSG_ERROR_CFP_DUPLICATO = "gestoreFormRegistrazione.codFiscDuplicato";



  private static final String SELECT_DIM_MAX_TOTALE_FILE = "SELECT dim_tot_allegati FROM w_mail WHERE codapp = ? and idcfg = ? ";

  /**
   * Logger per tracciare messaggio di debug
   */
  static Logger               logger                    = Logger.getLogger(GestoreFormRegistrazione.class);

  @Override
  public String getEntita() {
    return "USRSYS";
  }

  public GestoreFormRegistrazione() {
    super(false);
  }

  public GestoreFormRegistrazione(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }


  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {

  }


  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("preInsert: inizio metodo");
    synchronized (GestoreFormRegistrazione.class) {

      GeneManager gene = (GeneManager) UtilitySpring.getBean("geneManager", this.getServletContext(), GeneManager.class);

      // Gestione USRSYS
      AccountManager accountManager = (AccountManager) UtilitySpring.getBean("accountManager", this.getServletContext(),
          AccountManager.class);
      ProfiliManager profiliManager = (ProfiliManager) UtilitySpring.getBean("profiliManager", this.getServletContext(),
          ProfiliManager.class);

      GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager", this.getServletContext(),
          GenChiaviManager.class);

      //Verifica se siamo in SSO
      String propProtSSO = ConfigManager.getValore(CostantiGenerali.PROP_SSO_PROTOCOLLO);
      propProtSSO = UtilityStringhe.convertiNullInStringaVuota(propProtSSO);


      String nome = datiForm.getString("NOME");
      String cognome = datiForm.getString("COGNOME");
      String email = datiForm.getString("EMAIL");
      String codfisc = datiForm.getString("CODFISC");
      codfisc = codfisc.toUpperCase();
      String login = datiForm.getString("LOGIN");
      String password = datiForm.getString("USRSYS.SYSPWD");
      String flagLdap = datiForm.getString("USRSYS.FLAG_LDAP");
      flagLdap = UtilityStringhe.convertiNullInStringaVuota(flagLdap);
      String dn = datiForm.getString("DN");
      String msgAmm = datiForm.getString("MSGAMM");
      String msg = msgAmm;
      
      //personalizzione regione marche, se il campo è presente e settano a 0, il campo PEC è obbligatorio
      String pec = null;
      if(datiForm.isColumn("PEC")){
          pec = datiForm.getString("PEC");
      }

      if(!"".equals(flagLdap)){
        if("1".equals(flagLdap)){
          this.getRequest().setAttribute("login", login);
          this.getRequest().setAttribute("username", login);
          this.getRequest().setAttribute("dn", dn);
          this.getRequest().setAttribute("flagLdap", flagLdap);
        }else if ("3".equals(flagLdap)){
          this.getRequest().setAttribute("nome", nome);
          this.getRequest().setAttribute("cognome", cognome);
          this.getRequest().setAttribute("login", login);
          this.getRequest().setAttribute("codfisc", login);
          this.getRequest().setAttribute("email", email);
          this.getRequest().setAttribute("username", login);
          this.getRequest().setAttribute("flagLdap", flagLdap);
        }
      }else{
        flagLdap = "0";
      }


      String sysabg = "";
      String sysabc = "";
      String sysab3 = "";
      String ruolo = "";

      if(datiForm.isColumn("USRSYS.SYSABG")){
        sysabg = datiForm.getString("USRSYS.SYSABG");
        ruolo = sysabg;
      }else{
        if(datiForm.isColumn("USRSYS.SYSABC")){
          sysabc = datiForm.getString("USRSYS.SYSABC");
          ruolo = sysabc;
        }else{
          sysab3 = datiForm.getString("USRSYS.SYSAB3");
          ruolo = sysab3;
        }
      }

      String telefono = datiForm.getString("TELEFONO");
      String intestazione = datiForm.getString("COGNOME") + " " + datiForm.getString("NOME");

      int id = genChiaviManager.getMaxId("USRSYS", "SYSCON") + 1;
      datiForm.setValue("USRSYS.SYSCON", Integer.toString(id));


      String profiliDisponibili = ConfigManager.getValore(CostantiGenerali.PROP_REG_PROFILI_DISPONIBILI);
      String[] arrayProfiliDisponibili = null;
      String[] arrayApplicativiScelti = new String[1];
      profiliDisponibili = UtilityStringhe.convertiNullInStringaVuota(profiliDisponibili);
      if(!"".equals(profiliDisponibili)){
        arrayProfiliDisponibili = profiliDisponibili.split(";");
        if(arrayProfiliDisponibili.length==1){
          //Se ho un solo profilo in configurazione lo setto automaticamente
          String codProfiloUnico = arrayProfiliDisponibili[0];
          arrayApplicativiScelti[0] = codProfiloUnico;
        }else{
          //Applicativi Disponibili
          arrayApplicativiScelti = this.getRequest().getParameterValues("applicativi");
          if (arrayApplicativiScelti != null && arrayApplicativiScelti.length > 0) {
            ;
          }else{
            throw new GestoreException("Errore: Occorre scegliere almeno uno fra gli applicativi disponibili!", MSG_ERROR_APPDISP_NOPRES, null);
          }
        }
      }





      String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

      if(!"".equals(propProtSSO) && !"0".equals(propProtSSO)){
        //Determinazione della password di default per gli utenti SSO
        String regDefaultPwd = ConfigManager.getValore(CostantiGenerali.PROP_REG_DEFAULT_PASSWORD);
        regDefaultPwd = UtilityStringhe.convertiNullInStringaVuota(regDefaultPwd);
        if ("".equals(regDefaultPwd)) {
          throw new GestoreException("La password sso di default non risulta definita in configurazione", MSG_ERROR_PWDDEF_NOCONF, null);
        }else{
          // Decodifica della password
          ICriptazioneByte regDefaultPwdICriptazioneByte = null;
          try {
            regDefaultPwdICriptazioneByte = FactoryCriptazioneByte.getInstance(
                ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
                regDefaultPwd.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
            String regDefaultPwdDecriptata = new String(
                regDefaultPwdICriptazioneByte.getDatoNonCifrato());
            password = regDefaultPwdDecriptata;
          } catch (CriptazioneException ce) {
            throw new GestoreException("Errore nella determinazione della pwd di default del protocollo sso", null);
          }
        }
      }


      //INIZIO Controlli di sicurezza

      password = UtilityStringhe.convertiNullInStringaVuota(password);
      email = UtilityStringhe.convertiNullInStringaVuota(email);
      pec = UtilityStringhe.convertiNullInStringaVuota(pec);
      ruolo = UtilityStringhe.convertiNullInStringaVuota(ruolo);
      codfisc = UtilityStringhe.convertiNullInStringaVuota(codfisc);

      //Controlli effettuati a livello javascript implementati anche lato server per problematiche di sicurezza
      if(!controlloCF(codfisc)){
        throw new GestoreException("Errore: Login non conforme a codice fiscale/Partita IVA!", MSG_ERROR_LOGIN_NOCFPIVA, null);
      }

      List codiciFiscaliDuplicati = accountManager.getListaUtentiUgualeCodfisc(id, codfisc);
      if(codiciFiscaliDuplicati.size() > 0){
        throw new GestoreException("Errore: Codice fiscale duplicato!", MSG_ERROR_CFP_DUPLICATO, null);
      }

      //Verifico la password se non si tratta di autenticazione SSO (in tal caso viene messa la password di default)
      // 1 - Shibboleth
      // 2 - Cohesion
      // 3 - SSOBart
      if(!"".equals(propProtSSO) && !"0".equals(propProtSSO)){
        if("".equals(password) || password.length() < 8 ){
          throw new GestoreException("Errore: Password non definita o inferiore a 8 caratteri!", MSG_ERROR_PASSWORD_LENGTH, null);
        }
      }

      if("".equals(email)){
        throw new GestoreException("Errore: E-mail non definita!", MSG_ERROR_EMAIL_NOPRES, null);
      }
      
      if("".equals(ruolo)){
        throw new GestoreException("Errore: Ruolo non definito!", MSG_ERROR_RUOLO_NOPRES, null);
      }else{
        if(datiForm.isColumn("USRSYS.SYSABG")){
          if(!"U".equals(ruolo) && !"A".equals(ruolo)){
            throw new GestoreException("Errore: Ruolo non definito!", MSG_ERROR_RUOLO_VALNOAMM, null);
          }
        }else{
          if(datiForm.isColumn("USRSYS.SYSABC")){
            //Mettere i valori corretti
            if(!"".equals(ruolo) && !"".equals(ruolo)){
              throw new GestoreException("Errore: Ruolo non definito!", MSG_ERROR_RUOLO_VALNOAMM, null);
            }
          }else{
            if(!"U".equals(ruolo) && !"A".equals(ruolo)){
              throw new GestoreException("Errore: Ruolo non definito!", MSG_ERROR_RUOLO_VALNOAMM, null);
            }
          }
        }
      }


      if(!"1".equals(flagLdap)){
        if(!UtilityPassword.isPasswordCaratteriAmmessi(password)){
          throw new GestoreException("Errore: La password contiene caratteri non ammessi!", MSG_ERROR_PASSWORD_CHRNOAMM, null);
        }
        if(!UtilityPassword.isPasswordMinimo2Numerici(password)){
          throw new GestoreException("Errore: La password deve contenere almeno 2 caratteri numerici!", MSG_ERROR_PASSWORD_CHR2NUM, null);
        }

        if(UtilityPassword.passwordSimilarity(password,nome)){
          throw new GestoreException("Errore: Password simile al nome dell'utente!", MSG_ERROR_PASSWORD_SIMIL_NOME, null);
        }

        if(UtilityPassword.passwordSimilarity(password,cognome)){
          throw new GestoreException("Errore: Password simile al cognome dell'utente!", MSG_ERROR_PASSWORD_SIMIL_COGNOME, null);
        }

        if(UtilityPassword.passwordSimilarity(password,codfisc)){
          throw new GestoreException("Errore: Password simile al codice fiscale / login dell'utente!", MSG_ERROR_PASSWORD_SIMIL_CF, null);
        }
      }


      //Controllo della configurazione per i profili scelti

      if(arrayProfiliDisponibili != null && arrayProfiliDisponibili.length > 0){
        boolean foundedCod = false;
        for (int i = 0; i < arrayApplicativiScelti.length; i++) {
          String codProfiloScelto = arrayApplicativiScelti[i];
          foundedCod = false;
          for (int j = 0; j < arrayProfiliDisponibili.length; j++) {
            String cod_profilo = arrayProfiliDisponibili[j];
            if(cod_profilo.equals(codProfiloScelto)){
              foundedCod = true;
            }
          }
          if(false == foundedCod){
            throw new GestoreException("Errore: l'applicativo scelto non esiste in configurazione!", MSG_ERROR_APPSCELTO_NOCONF, null);
          }
        }
      }else{
        throw new GestoreException("Errore: non esistono applicativi disponibili in configurazione!", MSG_ERROR_APPDISP_NOCONF, null);
      }


      //FINE controlli di sicurezza

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      String fname = "";
      double dimMaxTotaleFilesByte = 0;
      boolean eseguireControlloDimTotale=false;
      String dimMaxTotaleFiles = null;
      try {
        dimMaxTotaleFiles = (String) sqlManager.getObject(SELECT_DIM_MAX_TOTALE_FILE, new Object[]{codapp,CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD});
        if (dimMaxTotaleFiles == null || "".equals(dimMaxTotaleFiles)) {
            String codappGeneweb = "W_";
            dimMaxTotaleFiles = (String) sqlManager.getObject(SELECT_DIM_MAX_TOTALE_FILE, new Object[]{codappGeneweb,CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD});
        }
      } catch (SQLException ex) {
        throw new GestoreException("Errore nella lettura del parametro 'mail.limiteDimensioneAllegati' "
                        + "per determinare il limite max in Mb del totale dei file allegati", "upload.noMaxMb", ex);
      }
      //Si deve determinare la dimensione massima dei file già allegati e di quello che si sta allegando
      dimMaxTotaleFiles = UtilityStringhe.convertiNullInStringaVuota(dimMaxTotaleFiles);
      if (!"".equals(dimMaxTotaleFiles)) {
          dimMaxTotaleFiles = dimMaxTotaleFiles.trim();
          dimMaxTotaleFilesByte = Math.pow(2, 20) * Double.parseDouble(dimMaxTotaleFiles);
          eseguireControlloDimTotale=true;
      }



      UploadFileForm mf = getForm();
      FormFile ff = mf.getSelezioneFile();
      if (ff!= null){
        fname = ff.getFileName();
        if (!FileAllegatoManager.isEstensioneFileAmmessa(fname)) {
          throw new GestoreException("Il file selezionato da caricare ha un'estensione non accettata",
              "upload.estensioneNonAmmessa", new String[]{fname}, null);
        }
        if(fname.length()!=0 && ff.getFileSize()>0 && (!eseguireControlloDimTotale || (eseguireControlloDimTotale &&ff.getFileSize() <= dimMaxTotaleFilesByte))) {
          try {
            baos.write(ff.getFileData());
            baos.close();
          } catch (FileNotFoundException e) {
             throw new GestoreException("File da caricare non trovato", "upload", e);
          } catch (IOException e) {
            throw new GestoreException(
                "Si è verificato un errore durante la scrittura del buffer nel del file allegato",
                "upload", e);
          }

        }else if(fname.length()!=0 && ff.getFileSize()==0){
          throw new GestoreException("Il file selezionato risulta vuoto. Per continuare specificare un altro file",
              "upload.fileVuoto", null, null);

        }else if(eseguireControlloDimTotale && ff.getFileSize() > dimMaxTotaleFilesByte){
          throw new GestoreException("La dimensione totale dei file da allegare supera il limite consentito "
              + "di " + dimMaxTotaleFiles + " MB", "upload.overflow", new String[]{dimMaxTotaleFiles}, null);
        }

      }


      // Controllo preliminare mediante CAPTCHA
      if (!this.rpHash(this.getRequest().getParameter("realperson")).equals(this.getRequest().getParameter("realpersonHash"))) {
        throw new GestoreException("Il codice di controllo digitato non corrisponde a quello visualizzato",
            REGISTRAZIONE_UTENTE_CAPTCHA_ERRORE, null);
      }

      Account account = new Account();
      String registrazioneAutomatica = ConfigManager.getValore(CostantiGenerali.PROP_REGISTRAZIONE_AUTOMATICA);
      try {
        if (!accountManager.isUsedLogin(login, -1)) {
          account.setLogin(login);
          account.setCodfisc(codfisc);
          account.setPassword(password);
          account.setNome(intestazione);
          OpzioniUtente opzioniUtente = new OpzioniUtente(ConfigManager.getValore(CostantiGenerali.PROP_OPZIONI_UTENTE_DEFAULT));
          opzioniUtente.setOpzione(CostantiGeneraliAccount.OPZIONI_MENU_STRUMENTI);
          if("0".equals(flagLdap)){
            opzioniUtente.setOpzione(CostantiGeneraliAccount.OPZIONI_SICUREZZA_PASSWORD);
          }
          account.setOpzioniUtente(opzioniUtente.toString());
          account.setEmail(datiForm.getString("EMAIL"));
          account.setDataInserimento(new Date());
          if(!"".equals(sysab3)){
            account.setAbilitazioneStd(sysab3);
          }else{
            account.setAbilitazioneStd(CostantiGeneraliAccount.DEFAULT_ABILITAZIONE_LAVORI);
          }
          if(!"".equals(sysabg)){
            account.setAbilitazioneGare(sysabg);
          }else{
            account.setAbilitazioneGare(CostantiGeneraliAccount.DEFAULT_ABILITAZIONE_GARE);
          }
          if(!"".equals(sysabc)){
            account.setAbilitazioneContratti(sysabc);
          }else{
            account.setAbilitazioneContratti(CostantiGeneraliAccount.DEFAULT_ABILITAZIONE_CONTRATTI);
          }

          if(!"".equals(flagLdap)){
            account.setFlagLdap(new Integer(flagLdap));
          }else{
            account.setFlagLdap(new Integer(0));
          }

          if(!"".equals(dn)){
            account.setDn(dn);
          }

          // Abilitazione automatica
          if ("1".equals(registrazioneAutomatica)) {
            account.setUtenteDisabilitato(new Integer(CostantiDettaglioAccount.ABILITATO));
          } else {
            account.setUtenteDisabilitato(new Integer(CostantiDettaglioAccount.DISABILITATO));
          }

          accountManager.insertAccount(account);

          // Inserimento dei dati nello storico delle password (STOUTESYS) per
          // evitare che alla prima connessione il software chieda di cambiare
          // la password; utilizzo i dati login e password presi dal bean
          // account perche' sono gia' criptati

          if(!"1".equals(flagLdap) && !"3".equals(flagLdap)){
            accountManager.insertStoriaAccount(account.getIdAccount(), login, account.getLoginCriptata(), account.getPassword());
          }


          // Inserimento dell'associazione con il profilo di default
          boolean gruppiDis = false;
          String gruppiDisabilitati = ConfigManager.getValore(CostantiGenerali.PROP_GRUPPI_DISABILITATI);
          if ("1".equals(gruppiDisabilitati)) {
            gruppiDis = true;
          }

          //Associazione dei profili scelti
          String descr = "";
          if(arrayApplicativiScelti != null && arrayApplicativiScelti.length > 0){
            profiliManager.updateAssociazioneProfiliAccount(account.getIdAccount(), arrayApplicativiScelti, codapp, gruppiDis);
            descr = "Utente "+ account.getLogin() +" ("+ account.getIdAccount() +") associato ai profili " + Arrays.toString(arrayApplicativiScelti);
          }else{
            descr = "Utente "+ account.getLogin() +" ("+ account.getIdAccount() +")  non associato ad alcun profilo";
          }
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(1);
          logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_CHANGE_PROFILO);
          logEvento.setDescr(descr);
          logEvento.setErrmsg("");
          LogEventiUtils.insertLogEventi(logEvento);



        } else {
          throw new GestoreException("Errore: login duplicato!", MSG_ERROR_LOGIN_DUPLICATO, null);
        }
      } catch (CriptazioneException e) {
        throw new GestoreException("Errore durante l'inserimento dell'occorrenza di USRSYS !", "updateUTENT", e);
      } catch (NumberFormatException e) {
        throw new GestoreException("Errore durante l'inserimento dell'occorrenza di USRSYS !", "updateUTENT", e);
      } catch (SqlComposerException e) {
        throw new GestoreException("Errore durante l'inserimento dell'occorrenza di USRSYS !", "updateUTENT", e);
      }

      // Gestione dell'ente.
      String codein = null;
      if(datiForm.isColumn("UFFINT.CFEIN")){
        // Se il codice fiscale indicato dall'utente corrisponde ad un ente gia'
        // esistente,
        // il nuovo utente deve essere associati (USR_EIN) a questo ente.
        String cfein = datiForm.getString("UFFINT.CFEIN");

     // Gestione UFFINT
        codein = datiForm.getString("UFFINT.CODEIN");
        try {

          if (codein == null) {
            codein = (String) this.sqlManager.getObject("select codein from uffint where cfein = ?", new Object[] { cfein });
            codein = UtilityStringhe.convertiNullInStringaVuota(codein);
          }
          if (!"".equals(codein.trim())) {
            // La stazione appaltante esiste gia'.
            // si tratta di associare il nuovo utente creato in USRSYS
            // con la stazione appaltante
          } else {
              throw new GestoreException("Errore: ente non presente!", MSG_ERROR_ENTE_INESISTENTE, null);
          }

          // Associazione ente - utente
          datiForm.addColumn("USR_EIN.SYSCON", JdbcParametro.TIPO_NUMERICO, new Long(account.getIdAccount()));
          datiForm.addColumn("USR_EIN.CODEIN", JdbcParametro.TIPO_TESTO, codein);
          datiForm.insert("USR_EIN", gene.getSql());

        } catch (SQLException e) {
          throw new GestoreException("Errore durante l'inserimento dell'ente", "updateUTENT", e);
        }

      }

      //INTEGRAZIONE ANAGRAFICA
      String integrazioneAnagrafica = ConfigManager.getValore(CostantiGenerali.PROP_REG_INTEGRAZIONE_ANAGRAFICA);
      integrazioneAnagrafica = UtilityStringhe.convertiNullInStringaVuota(integrazioneAnagrafica);
      String[] esitoVerificaCFPIVA = new String[]{"0",""};
      if(!"".equals(integrazioneAnagrafica)){
       if( "TECNI".equals(integrazioneAnagrafica)){

         // Calcolo delle chiave di TECNI
         //Effettuo queste operazioni solo se sono in presenza di integrazione anagrafiche
         String codtec = gene.calcolaCodificaAutomatica("TECNI", "CODTEC");

         DataColumnContainer implTECNI = new DataColumnContainer("");
         implTECNI.addColumn("TECNI.CODTEC", codtec);
         implTECNI.addColumn("TECNI.NOMETEI", nome);
         implTECNI.addColumn("TECNI.COGTEI", cognome);
         //implTECNI.addColumn("TECNI.PROTEC", codtec);
         implTECNI.addColumn("TECNI.CFTEC", codfisc);
         implTECNI.addColumn("TECNI.TELTEC", telefono);
         implTECNI.addColumn("TECNI.EMATEC", email);
         implTECNI.addColumn("TECNI.EMA2TEC", pec);
         // Associazione USRSYS - TECNI
         implTECNI.addColumn("TECNI.SYSCON", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(account.getIdAccount())));
         // Associazione UFFINT - TECNI
         implTECNI.addColumn("TECNI.CGENTEI", JdbcParametro.TIPO_TESTO, codein);
         // Intestazione
         implTECNI.addColumn("TECNI.NOMTEC", JdbcParametro.TIPO_TESTO, intestazione);

         //instanzio il gestore di TECNI
         GestoreTECNI gestoreTECNI = new GestoreTECNI();
         gestoreTECNI.setRequest(this.getRequest());

         gestoreTECNI.getRequest().setAttribute("modo", "REGISTRAZIONE");

         gestoreTECNI.verificaCodiceFiscalePartitaIVA(implTECNI, esitoVerificaCFPIVA);
         
         if("0".equals(esitoVerificaCFPIVA[0])){
           this.setPermessiAssociazioneAccountEntita(codtec, new Integer(account.getIdAccount()));
           gestoreTECNI.inserisci(status, implTECNI);
         }else if("1".equals(esitoVerificaCFPIVA[0])){
           this.setPermessiAssociazioneAccountEntita(codtec, new Integer(account.getIdAccount()));
           msg += "\n\n" + esitoVerificaCFPIVA[1];
           gestoreTECNI.inserisci(status, implTECNI);
         }else if("2".equals(esitoVerificaCFPIVA[0])){
           //qui viene bloccata la registrazione e
           // si effettua la notifica a video
           ;
         }
       }

      }
      //INVIO MAIL
      inviaMail(login, password, account.getUtenteDisabilitato().intValue(), account.getNome(), telefono, account.getEmail(), msg, fname, baos);
    }

    //Al termine dell'esecuzione viene fatto il forward su redirect.jsp,
    //che a sua volta reindirizzerà alla action speciaficata nel attributo "url"
    this.getRequest().setAttribute("url", "ConfermaRegistrazione.do");

    if (logger.isDebugEnabled()) logger.debug("preInsert: fine metodo");

  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }


  /**
   * Invio mail di registrazione.
   *
   * @param account
   * @throws GestoreException
   */
  private void inviaMail(String login, String password, int utenteDisabilitato, String nome, String telefono, String email, String msg,
      String nomeFile, ByteArrayOutputStream stream) {

    String strAbilitazione = "Disabilitato";

       try {
        MailManager mailManager = (MailManager) UtilitySpring.getBean("mailManager", this.getServletContext(), MailManager.class);
        IMailSender mailSender = MailUtils.getInstance(mailManager, ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE),CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);
        //INVIO RICEVUTA SOGGETTO CHE SI REGISTRA
        if (utenteDisabilitato == new Integer(CostantiDettaglioAccount.ABILITATO).intValue()) {
          this.inviaMailRicevuta(mailManager, mailSender, login, password, utenteDisabilitato, nome, email, msg, nomeFile, stream);
          strAbilitazione = "Abilitato";
        }
        //INVIO NOTIFICA AMMINISTRATORE
        this.inviaMailNotifica(mailManager, mailSender, login, password, utenteDisabilitato, nome, telefono, email, msg, nomeFile, stream, strAbilitazione);
      } catch (MailSenderException ms) {

        String logMessageKey = ms.getChiaveResourceBundle();
        String logMessageError = resBundleGenerale.getString(logMessageKey);
        for (int i = 0; ms.getParametri() != null && i < ms.getParametri().length; i++){
          logMessageError = logMessageError.replaceAll(UtilityStringhe.getPatternParametroMessageBundle(i), (String) ms.getParametri()[i]);
        }
        logger.error(logMessageError, ms);
      }
  }

  /**
   * Invio mail di registrazione per l'Amministratore.
   */
  private void inviaMailNotifica(MailManager mailManager, IMailSender mailSender, String login, String password, int utenteDisabilitato, String nome, String telefono, String email, String msgAmm,
      String nomeFile, ByteArrayOutputStream stream, String strAbilitazione) {

    String nomeMittente = ConfigManager.getValore(CostantiGenerali.PROP_TITOLO_APPLICATIVO);
    String mailAmministratore = ConfigManager.getValore(CostantiGenerali.PROP_INDIRIZZO_MAIL_AMMINISTRATORE);

    try {

      String testoMail = null;
      String oggettoMail = null;

      if (mailAmministratore != null && !"".equals(mailAmministratore.trim())) {
        oggettoMail = resBundleGenerale.getString(CostantiGenerali.RESOURCE_OGGETTO_MAIL_REGISTRAZIONE_AMMINISTRATORE);
        oggettoMail = UtilityStringhe.replaceParametriMessageBundle(oggettoMail, new String[] { nomeMittente });

        testoMail = this.getTestoMail(nome, login, password, strAbilitazione, nomeMittente, telefono, msgAmm, 1);

        nomeFile = UtilityStringhe.convertiNullInStringaVuota(nomeFile);
        if(!"".equals(nomeFile)){
          mailSender.send(new String[] {mailAmministratore}, null, null, oggettoMail, testoMail,
              new String[] {nomeFile}, new ByteArrayOutputStream[] {stream });
        }else{
          mailSender.send(mailAmministratore, oggettoMail, testoMail);
        }
      }

    } catch (Exception e) {
      String logMessageKey = "warnings.registrazione.mancatoInvioMailAdmin";
      String logMessageError = resBundleGenerale.getString(logMessageKey);
      if (e instanceof MailSenderException) {
        MailSenderException ms = (MailSenderException)e;
        logMessageKey = ms.getChiaveResourceBundle();
        logMessageError = resBundleGenerale.getString(logMessageKey);
        for (int i = 0; ms.getParametri() != null && i < ms.getParametri().length; i++)
          logMessageError = logMessageError.replaceAll(UtilityStringhe.getPatternParametroMessageBundle(i), (String) ms.getParametri()[i]);
        logger.error(logMessageError, e);
        logMessageKey = "warnings.registrazione.mancatoInvioMailAdmin";
        logMessageError = resBundleGenerale.getString(logMessageKey);
      } else {
        logger.error(logMessageError, e);
      }
      UtilityStruts.addMessage(this.getRequest(), "warning", logMessageKey, null);
      logger.warn(logMessageError, e);
    }

  }

  /**
   * Invio mail di registrazione per soggetto che si registra.
   */
  private void inviaMailRicevuta(MailManager mailManager, IMailSender mailSender, String login, String password, int utenteDisabilitato, String nome, String email, String msgUtente,
      String nomeFile, ByteArrayOutputStream stream) {

    String nomeMittente = ConfigManager.getValore(CostantiGenerali.PROP_TITOLO_APPLICATIVO);
    String testoMail = null;
    String oggettoMail = null;

    try {

      oggettoMail = resBundleGenerale.getString(CostantiGenerali.RESOURCE_OGGETTO_MAIL_REGISTRAZIONE_AUTOMATICA);
      oggettoMail = UtilityStringhe.replaceParametriMessageBundle(oggettoMail, new String[] { nomeMittente });
      testoMail = this.getTestoMail(nome, login, password, null, nomeMittente, null, msgUtente, 2);
      mailSender.send(email, oggettoMail, testoMail);

    } catch (Exception e) {
      String logMessageKey = "warnings.registrazione.mancatoInvioMailUtente";
      String logMessageError = resBundleGenerale.getString(logMessageKey);
      if (e instanceof MailSenderException) {
        MailSenderException ms = (MailSenderException)e;
        logMessageKey = ms.getChiaveResourceBundle();
        logMessageError = resBundleGenerale.getString(logMessageKey);
        for (int i = 0; ms.getParametri() != null && i < ms.getParametri().length; i++)
          logMessageError = logMessageError.replaceAll(UtilityStringhe.getPatternParametroMessageBundle(i), (String) ms.getParametri()[i]);
        logger.error(logMessageError, e);
        logMessageKey = "warnings.registrazione.mancatoInvioMailUtente";
        logMessageError = resBundleGenerale.getString(logMessageKey);
      } else {
        logger.error(logMessageError, e);
      }
      UtilityStruts.addMessage(this.getRequest(), "warning", logMessageKey, null);
      logger.warn(logMessageError, e);
    }

  }

  /**
   * Viene costruito il testo delle mail per la notifica e la ricevuta.
   *
   * @param nome
   *        nome
   * @param login
   *        login/codice fiscale
   * @param password
   *        password
   * @param statoAbilitazione
   *        stato abilitazione: Abilitato;Disabilitato
   * @param nomeMittente
   *        nomeMittente
   * @param telefono
   *        telefono
   * @param msgAmministratore
   *        messaggio per l'Amministratore
   * @param tipo
   *        tipologia mail: 1 = notifica; 2 = ricevuta
   * @throws Exception
   *
   * @return Testo composto secondo il modello
   * @throws SQLException
   *
   */
  private String getTestoMail(String nome, String login, String password, String statoAbilitazione, String nomeMittente,
      String telefono, String msg, int tipo) throws SQLException  {

    VelocityEngine velocityEngine = new VelocityEngine();
    velocityEngine.setProperty(Velocity.RESOURCE_LOADER, "file");
    velocityEngine.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
    velocityEngine.setProperty(Velocity.FILE_RESOURCE_LOADER_CACHE, "false");
    velocityEngine.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
    //velocityEngine.setProperty("runtime.log.logsystem.log4j.category", "velocity");
    velocityEngine.setProperty(Log4JLogChute.RUNTIME_LOG_LOG4J_LOGGER, logger.getName());

    String velocityModelPath = this.getServletContext().getRealPath("/velocitymodel/");
    velocityEngine.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, velocityModelPath);
    velocityEngine.init();


    VelocityContext velocityContext = new VelocityContext();
    StringWriter writerUtente = new StringWriter();

    String velocityModel = null;
    if(tipo==1){
      velocityModel = "registrazione-mail-notifica.txt";
    }else{
      velocityModel = "registrazione-mail-ricevuta.txt";
    }

    Template templateUtente = velocityEngine.getTemplate(velocityModel);
    if(tipo==1){
      this.popolaContesto(nome, login, password, statoAbilitazione, nomeMittente, telefono, msg, 1, velocityContext);
    }else{
      if(tipo==2){
        this.popolaContesto(nome, login, password, null, nomeMittente, null, msg, 2, velocityContext);
      }
    }

    templateUtente.merge(velocityContext, writerUtente);
    String testoMail = writerUtente.toString();
    return testoMail;
  }
  /**
   * Viene popolato il contesto di velocity da usare per la composizione del modello.
   *
   * @param nome
   *        nome
   * @param login
   *        login/codice fiscale
   * @param statoAbilitazione
   *        stato abilitazione: Abilitato;Disabilitato
   * @param nomeMittente
   *        nomeMittente
   * @param telefono
   *        telefono
   * @param msgAmministratore
   *        messaggio per l'Amministratore
   * @param tipo
   *        tipologia mail: 1 = notifica; 2 = ricevuta
   * @param velocityContext
   *        contesto di velocity
   * @throws SQLException
   */

  private void popolaContesto(String nome, String login, String password, String statoAbilitazione, String nomeMittente, String telefono, String msg, int tipo, VelocityContext velocityContext)
  throws SQLException {
  //Caricamento dei dati nel modello velocity
    switch (tipo) {
        case 1:
          velocityContext.put("NOME", nome);
          velocityContext.put("LOGIN", login);
          velocityContext.put("STATOAB", statoAbilitazione);
          velocityContext.put("NOMEMITT", nomeMittente);
          velocityContext.put("TELEFONO", telefono);
          velocityContext.put("MSGAMM", msg);
          break;
        case 2:
          velocityContext.put("NOME", nome);
          velocityContext.put("LOGIN", login);
          velocityContext.put("NOMEMITT", nomeMittente);
          break;
        default:
            break;
    }
  }
  
  /**
   * Inserimento occorrenza su G_PERMESSI.
   *
   * @param String codtec, Integer idAccount
   * @return
   */
  private void setPermessiAssociazioneAccountEntita(String codtec, Integer idAccount) {

    // Inserimento occorrenza su G_PERMESSI (l'utente mantiene la
    // proprietà della propria occorrenza di TECNI)
    PermessiManager permessiManager = (PermessiManager) UtilitySpring.getBean("permessiManager", this.getServletContext(),
        PermessiManager.class);
    
    PermessiAccountEntitaForm permessi = new PermessiAccountEntitaForm();
    permessi.setIdPermesso(new String[] { "0" });
    permessi.setIdAccount(new String[] { idAccount.toString() });
    permessi.setCondividiEntita(new String[] { "1" });
    permessi.setAutorizzazione(new String[] { "1" });
    permessi.setProprietario(new String[] { "1" });
    permessi.setCampoChiave("CODTEC");
    permessi.setValoreChiave(codtec);
    permessi.setRuolo(new String[] { null });
    permessi.setRuoloUsrsys(new String[] { null });
    permessiManager.updateAssociazioneAccountEntita(permessi);
    
  }
  
  /**
   * Gestione della HashMap per il controllo mediante CAPTCHA.
   *
   * @param value
   * @return
   */
  private String rpHash(String value) {
    int hash = 5381;
    value = value.toUpperCase();
    for (int i = 0; i < value.length(); i++) {
      hash = ((hash << 5) + hash) + value.charAt(i);
    }
    return String.valueOf(hash);
  }

  /**
   * Metodo che controlla la validità del codice fiscale
   *
   * @param cf
   *
   * @return boolean,   true codice fiscale valido
   *                    false altrimenti
   *
   */
  private boolean controlloCF(String cf) {
    boolean ret= true;

    // Se il primo carattere e un numero si tratta di una partita iva
    if("1234567890".indexOf(cf.charAt(0))>=0)
        return UtilityFiscali.isValidPartitaIVA(cf,true);

    ret = UtilityFiscali.isValidCodiceFiscale(cf);

    return ret;
  }

}