/*
 * Created on 28/set/2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.PropsConfigManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.PropsConfig;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.sql.comp.SqlManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.hda.client.HdaException;
import it.maggioli.eldasoft.hda.client.HdaHelpDesk;
import it.maggioli.eldasoft.hda.client.HdaHelpDeskConfiguration;
import it.maggioli.eldasoft.hda.client.HdaTicket;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ProcessingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Viene inviata via mail o via servizio la richiesta di assistenza inserita mediante form; ovviamente i parametri di posta o di connessione
 * al servizio devono essere opportunamente configurati e verificati.
 *
 * @author Stefano.Sabbadin
 */
public class InviaRichiestaAssistenzaAction extends Action {

  /** Logger Log4J di classe */
  private final Logger             logger            = Logger.getLogger(InviaRichiestaAssistenzaAction.class);

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  private final ResourceBundle     resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /** Manager per l'invio mail. */
  private MailManager        mailManager;

  /** manager per il reperimento delle configurazioni su DB. */
  private PropsConfigManager propsConfigManager;

  /**
   * @param mailManager
   *        mailManager da settare internamente alla classe.
   */
  public void setMailManager(MailManager mailManager) {
    this.mailManager = mailManager;
  }

  /**
   * @param propsConfigManager
   *        propsConfigManager da settare internamente alla classe.
   */
  public void setPropsConfigManager(PropsConfigManager propsConfigManager) {
    this.propsConfigManager = propsConfigManager;
  }

  /**
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String target = CostantiGeneraliStruts.FORWARD_OK;
    boolean continua = true;

    AssistenzaForm assistenzaForm = (AssistenzaForm) form;
    ServletContext context = request.getSession().getServletContext();
    String type = (String) context.getAttribute(CostantiGenerali.ATTR_ATTIVA_FORM_ASSISTENZA);
    // in caso di utente loggato e' settato il codice applicazione in uso nella sua sessione.
    // in caso di utente non loggato, si prende il codice applicazione dal file di properties (occhio che teoricamente, anche se non e'
    // mai successo, il codice indicato nel file di properties potrebbe essere una concatenazione di codici).
    String codapp = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    if (codapp == null) {
      codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
    }
    String versione = getVersione(request, codapp);

    if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MODO_NON_ATTIVO.equals(type)) {
      String chiave = "errors.funzione.nonAttiva";
      logger.error(resBundleGenerale.getString(chiave));
      aggiungiMessaggio(request, chiave);
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      continua = false;
    }

    if (continua && !(this.rpHash(assistenzaForm.getCaptcha()).equals(assistenzaForm.getCaptchaHash()))) {
      String chiave = "errors.captcha";
      logger.error(resBundleGenerale.getString(chiave));
      aggiungiMessaggio(request, chiave);
      assistenzaForm.setCaptcha(null);
      target = "validateError";
      continua = false;
    }

    if (continua && assistenzaForm.getSelezioneFile() != null && assistenzaForm.getSelezioneFile().getFileData().length != 0) {

      if (!FileAllegatoManager.isEstensioneFileAmmessa(assistenzaForm.getSelezioneFile().getFileName())) {
        String chiave = "errors.gestoreException.*.upload.estensioneNonAmmessa";
        String logMessageError = this.resBundleGenerale.getString(chiave);
        logMessageError = logMessageError.replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0), assistenzaForm.getSelezioneFile().getFileName());
        logger.error(logMessageError);

        aggiungiMessaggio(request, chiave, assistenzaForm.getSelezioneFile().getFileName());
        target = "validateError";
        continua = false;
      }

      // se e' stato allegato un file si controlla che non superi la dimensione massima
      PropsConfig propFileSize = this.propsConfigManager.getProperty(codapp, CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_FILE_SIZE);

      if (propFileSize != null && StringUtils.isNotEmpty(propFileSize.getValore())) {
        Long fileSize = new Long(propFileSize.getValore());
        if (assistenzaForm.getSelezioneFile().getFileData().length > (fileSize * Math.pow(2, 20))) {

          String chiave = "errors.gestoreException.*.upload.overflow";
          String logMessageError = this.resBundleGenerale.getString(chiave);
          logMessageError = logMessageError.replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0), fileSize.toString());
          logger.error(logMessageError);

          aggiungiMessaggio(request, propFileSize, chiave);
          target = "validateError";
          continua = false;
        }
      }
    }

    if (continua) {

      String descOggettoSelezionato = assistenzaForm.getOggetto().substring(assistenzaForm.getOggetto().indexOf('-') + 1);
      StringBuilder sbOggetto = new StringBuilder();
      sbOggetto.append(resBundleGenerale.getString("assistenza.mail.oggetto.prefisso")).append(": ").append(descOggettoSelezionato);
      String oggetto = sbOggetto.toString();
      String testo = getTestoMail(assistenzaForm, descOggettoSelezionato, (String)context.getAttribute(CostantiGenerali.ATTR_TITOLO), versione);

      try {
        if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MODO_MAIL.equals(type)) {
          sendEmail(codapp, assistenzaForm, oggetto, testo);
        } else if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MODO_WS.equals(type)) {
          insertTicket(codapp, request, assistenzaForm, oggetto, testo);
        }
      } catch (MailSenderException ms) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        // si traccia nel log l'errore tecnico rilevato
        String logMessageKey = ms.getChiaveResourceBundle();
        String logMessageError = this.resBundleGenerale.getString(logMessageKey);
        for (int i = 0; ms.getParametri() != null && i < ms.getParametri().length; i++) {
          logMessageError = logMessageError.replaceAll(UtilityStringhe.getPatternParametroMessageBundle(i), (String) ms.getParametri()[i]);
        }
        logger.error(logMessageError, ms);
        // si traccia nel log e si manda a video il messaggio per l'utente
        String chiave = "errors.assistenza.mailSender";
        logger.error(resBundleGenerale.getString(chiave));
        aggiungiMessaggio(request, chiave);
      } catch (ProcessingException r) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        String chiave = "errors.hda.hdaDisattivo";
        logger.error(this.resBundleGenerale.getString(chiave), r);
        aggiungiMessaggio(request, chiave);
      } catch (Throwable t) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        String chiave = "errors.applicazione.inaspettataException";
        logger.error(this.resBundleGenerale.getString(chiave), t);
        aggiungiMessaggio(request, chiave);
      }
    }
    return mapping.findForward(target);
  }

  /**
   * Determina la versione dell'applicativo in uso.
   *
   * @param request
   * @return
   */
  private String getVersione(HttpServletRequest request, String codiceApplicazione) {
    // si prova a leggere la versione dalla sessione se l'utente e' loggato
    String versione = (String) request.getSession().getAttribute(CostantiGenerali.VERSIONE_MODULO_ATTIVO);
    // se l'utente non e' loggato, si prova a leggere il file di versione dall'applicativo
    if (versione == null) {
      String nomeFileVersione = codiceApplicazione.toUpperCase() + "_VER.TXT";
      InputStream stream = request.getSession().getServletContext().getResourceAsStream(CostantiGenerali.PATH_WEBINF + nomeFileVersione);
      if (stream != null) {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        try {
          versione = br.readLine().trim();
        } catch (IOException e) {
          // questa eccezione potrebbe avvenire esclusivamente se non viene mai provato l'accesso all'applicativo con successo e il file di
          // versione risulta rimosso (praticamente risulta impossibile che si verifichi un caso simile)
          logger.error("Errore inaspettato durante la lettura della versione applicativo dal file " + nomeFileVersione, e);
        }
      }
    }

    // se si arriva qui senza versione, allora risulta non determinata per qualche motivo (es: utente non loggato in applicativo che
    // gestisce piu' codici applicazione)
    if (versione == null) {
      versione = "Non determinata";
    }

    return versione;
  }

  /**
   * Determina la famiglia di DBMS utilizzata.
   *
   * @return DBMS utilizzato
   */
  private String getDB() {
    String propDBMS = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
    String db = "Oracle";
    if (SqlManager.DATABASE_SQL_SERVER.equals(propDBMS)) {
      db = "Microsoft SQL Server";
    } else if (SqlManager.DATABASE_DB2.equals(propDBMS)) {
      db = "IBM DB2";
    } else if (SqlManager.DATABASE_POSTGRES.equals(propDBMS)) {
      db = "PostgreSQL";
    }
    return db;
  }

  private String getTestoMail(AssistenzaForm assistenzaForm, String descOggettoSelezionato, String nomeProdotto, String versione) {
    StringBuilder testo = new StringBuilder();

    // alcune informazioni sono parametrizzate nel file di resources in modo tale eventualmente da essere customizzabili per il
    // cliente, mentre altre sono fisse in quanto prettamente tecniche e di interesse esclusivamente interno per la nostra assistenza
    testo.append(resBundleGenerale.getString("assistenza.mail.testo.titoloRiferimenti")).append("\n");
    testo.append(resBundleGenerale.getString("assistenza.mail.testo.nominativoEnte")).append(": ").append(
        assistenzaForm.getDenominazioneEnte()).append("\n");
    testo.append(resBundleGenerale.getString("assistenza.mail.testo.referente.nome")).append(": ").append(
        assistenzaForm.getNomeRichiedente()).append("\n");
    testo.append(resBundleGenerale.getString("assistenza.mail.testo.referente.mail")).append(": ").append(
        assistenzaForm.getMailRichiedente()).append("\n");
    if (StringUtils.isNotBlank(assistenzaForm.getTelefonoRichiedente())) {
      testo.append(resBundleGenerale.getString("assistenza.mail.testo.referente.telefono")).append(": ").append(
          assistenzaForm.getTelefonoRichiedente()).append("\n");
    }
    testo.append("\n");
    testo.append(resBundleGenerale.getString("assistenza.mail.testo.titoloRichiesta")).append("\n");
    if (StringUtils.isNotBlank(assistenzaForm.getTesto())) {
      testo.append(assistenzaForm.getTesto()).append("\n");
    } else {
      // se manca il testo nella textarea si rimette l'oggetto della mail
      testo.append(descOggettoSelezionato).append("\n");
    }
    testo.append("\n");
    testo.append("INFORMAZIONI TECNICHE APPLICATIVO").append("\n");
    testo.append("Prodotto: ").append(nomeProdotto).append("\n");
    testo.append("Versione: ").append(versione).append("\n");
    testo.append("Registrato da: ").append(ConfigManager.getValore(CostantiGenerali.PROP_ACQUIRENTE)).append("\n");
//    testo.append("Codice cliente: ").append(ConfigManager.getValore(CostantiGenerali.PROP_CODICE_CLIENTE)).append("\n");
//    testo.append("Configurazione installata: ").append(ConfigManager.getValore(CostantiGenerali.PROP_CODICE_PRODOTTO)).append("\n");
    testo.append("Sistema operativo: ").append(System.getProperty("os.name")).append("\n");
    String db = getDB();
    testo.append("Database utilizzato: ").append(db).append("\n");
    testo.append("\n");
    testo.append("CLIENT RICHIEDENTE").append("\n");
    testo.append(assistenzaForm.getInfoSystem());

    return testo.toString();
  }

  /**
   * Effettua l'invio della richiesta di assistenza mediante email.
   *
   * @param codapp
   * @param assistenzaForm
   * @param oggetto
   * @param testo
   * @throws MailSenderException
   * @throws FileNotFoundException
   * @throws IOException
   */
  private void sendEmail(String codapp, AssistenzaForm assistenzaForm, String oggetto, String testo) throws MailSenderException,
      FileNotFoundException, IOException {
    IMailSender sender = MailUtils.getInstance(mailManager, codapp,CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);
    String mailAssistenza = StringUtils.stripToNull(propsConfigManager.getProperty(codapp,
        CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MAIL).getValore());

    // a seconda della presenza o meno di un allegato si utilizza il metodo corretto per l'invio mail
    if (assistenzaForm.getSelezioneFile() != null && assistenzaForm.getSelezioneFile().getFileData().length != 0) {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      stream.write(assistenzaForm.getSelezioneFile().getFileData());
      stream.close();
      sender.send(new String[] {mailAssistenza }, null, null, oggetto.toString(), testo.toString(),
          new String[] {assistenzaForm.getSelezioneFile().getFileName() }, new ByteArrayOutputStream[] {stream });
    } else {
      sender.send(mailAssistenza, oggetto.toString(), testo.toString());
    }
    logger.info("Inviata mail di richiesta di assistenza inserita da \""
        + assistenzaForm.getMailRichiedente()
        + "\", oggetto \""
        + oggetto
        + "\", testo \""
        + testo
        + "\"");
  }

  /**
   * Invia la richiesta di assistenza mediante ticket HDA.
   * @param codapp
   * @param request
   * @param assistenzaForm
   * @param oggetto
   * @param testo
   * @throws CriptazioneException
   * @throws FileNotFoundException
   * @throws IOException
   * @throws HdaException
   */
  private void insertTicket(String codapp, HttpServletRequest request, AssistenzaForm assistenzaForm, String oggetto, String testo) throws CriptazioneException, FileNotFoundException, IOException, HdaException {
    // si estraggono i parametri necessari per fruire il servizio
    String login = null;
    PropsConfig propHdaLogin = this.propsConfigManager.getProperty(codapp, CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_USR);
    login = propHdaLogin.getValore();

    String password = null;
    PropsConfig propHdaPassword = this.propsConfigManager.getProperty(codapp, CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_PWD);
    password = propHdaPassword.getValore();
    ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(
        ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), password.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
    password = new String(decriptatore.getDatoNonCifrato());

    String url = null;
    PropsConfig propHdaURL = this.propsConfigManager.getProperty(codapp, CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_URL);
    url = propHdaURL.getValore();

    String productId = null;
    PropsConfig propHdaProductId = this.propsConfigManager.getProperty(codapp, CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_ID_PRODOTTO);
    productId = propHdaProductId.getValore();

    // nel caso di ticket HDA non e' presente un oggetto, pertanto si concatena l'oggetto come prima riga del testo
    testo = oggetto + "\n\n" + testo;

    HdaHelpDeskConfiguration hdaConf = new HdaHelpDeskConfiguration();
    hdaConf.setHostname(url);
    hdaConf.setUsername(login);
    hdaConf.setPassword(password);

    HdaHelpDesk hhd = new HdaHelpDesk(hdaConf, null);
    HdaTicket ticket = new HdaTicket(new Long(productId), testo.toString(), false);
    FileInputStream fis = null;

    String chiave = "info.hda.inviorichiesta.success";

    hhd.login();
    try {
        // inserisci l'allegato...
        if(assistenzaForm.getSelezioneFile().getInputStream() != null) {
            ticket.addFile(assistenzaForm.getSelezioneFile().getInputStream(), assistenzaForm.getSelezioneFile().getFileName());
        }

        // invia la richiesta...
        String ticketId = hhd.insertTicket(ticket);

        String logMessage = resBundleGenerale.getString(chiave);
        logMessage = logMessage.replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0), ticketId);
        logger.info(logMessage);

        // inserisce il ticketId come attributo per la visualizzazione nella pagina di esito
        request.setAttribute("ticketId", ticketId);
    } catch (HdaException e) {
      chiave = "errors.hda.inviorichiesta.save";
      logger.error(resBundleGenerale.getString(chiave), e);
      aggiungiMessaggio(request, chiave);
      throw e;
    } finally {
      // se mi sono connesso e ho trasmesso il ticket con l'eventuale allegato, posso chiudere i riferimenti e sara' praticamente
      // impossibile ottenere errori in questa fase, pertanto l'eccezione risulta non gestita
      try {
        hhd.logout();
      } catch (Exception e) {
        logger.error("Errore durante la disconnessione dal servizio HDA", e);
      }

      if (fis != null) {
        try {
          fis.close();
        } catch (Exception t) {
          logger.error("Errore durante la chiusura dello stream contenente il file allegato trasmesso nella richiesta assistenza via HDA", t);
        }
      }
    }
  }


  /**
   * @param request
   * @param propFileSize
   * @param chiave
   */
  private void aggiungiMessaggio(HttpServletRequest request, PropsConfig propFileSize, String chiave) {
    ActionMessages errors = new ActionMessages();
    errors.add(ActionBase.getTipoMessaggioFromChiave(chiave), new ActionMessage(chiave, propFileSize.getValore()));
    if (!errors.isEmpty()) {
      this.addMessages(request, errors);
    }
  }

  /**
   * @param request
   * @param chiave
   */
  private void aggiungiMessaggio(HttpServletRequest request, String chiave) {
    ActionMessages errors = new ActionMessages();
    errors.add(ActionBase.getTipoMessaggioFromChiave(chiave), new ActionMessage(chiave));
    if (!errors.isEmpty()) {
      this.addMessages(request, errors);
    }
  }

  private void aggiungiMessaggio(HttpServletRequest request, String chiave, String parametro) {
    ActionMessages errors = new ActionMessages();
    errors.add(ActionBase.getTipoMessaggioFromChiave(chiave), new ActionMessage(chiave, parametro));
    if (!errors.isEmpty()) {
      this.addMessages(request, errors);
    }
  }

  /**
   * Calcola la hash per il controllo del captcha inserito.
   *
   * @param value
   *        valore da convertire
   * @return hash del valore
   */
  private String rpHash(String value) {
    int hash = 5381;
    value = value.toUpperCase();
    for (int i = 0; i < value.length(); i++) {
      hash = ((hash << 5) + hash) + value.charAt(i);
    }
    return String.valueOf(hash);
  }


}
