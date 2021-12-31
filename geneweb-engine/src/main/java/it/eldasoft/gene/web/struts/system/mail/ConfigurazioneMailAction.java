/*
 * Created on 01/feb/2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.system.mail;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBase;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.system.ConfigurazioneMail;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action per il caricamento della configurazione dei parametri di posta.
 *
 * @author Stefano.Sabbadin
 */
public class ConfigurazioneMailAction extends DispatchActionBase {

  static Logger               logger                            = Logger.getLogger(ConfigurazioneMailAction.class);

  private static final String FORWARD_SUCCESS_VISUALIZZA        = "successVisualizza";
  private static final String FORWARD_SUCCESS_MODIFICA          = "successModifica";
  private static final String FORWARD_SUCCESS_MODIFICA_PASSWORD = "successModificaPassword";
  private static final String FORWARD_SUCCESS_APRI_VERIFICA     = "successApriVerifica";

  private MailManager         mailManager;

  private SqlManager sqlManager;

  /**
   * @param mailManager
   *        mailManager da settare internamente alla classe.
   */
  public void setMailManager(MailManager mailManager) {
    this.mailManager = mailManager;
  }

  /**
   * @return Ritorna sqlManager.
   */
  public SqlManager getSqlManager() {
    return sqlManager;
  }

  /**
   * @param sqlManager
   *        sqlManager da settare internamente alla classe.
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }


  @Override
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_DEFAULT;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action visualizza
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVisualizza() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  /**
   * In visualizzazione, alla prima apertura, si cerca di aprire la configurazione specifica se esistente e non &egrave; stata selezionata
   * una configurazione particolare (si arriva dal menu principale).
   *
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public ActionForward visualizza(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    logger.debug("visualizza: inizio metodo");

    String target = FORWARD_SUCCESS_VISUALIZZA;
    String codapp = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    String paramIdcfg = request.getParameter("idcfg");

    target = this.getConfig(target, request, codapp, paramIdcfg);

    logger.debug("visualizza: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action nuovo
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniNuovo() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }


  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public ActionForward nuovo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    logger.debug("nuovo: inizio metodo");

    String target = FORWARD_SUCCESS_MODIFICA;
    String codapp = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);

    ConfigurazioneMail cfg = new ConfigurazioneMail();
    cfg.setCodapp(codapp);

    String selectDEF = "select count(idcfg) from w_mail where codapp = ? and idcfg = ? ";
    String selectSA = "select codein, nomein from uffint where codein not in " +
    " (select idcfg from w_mail where codapp = ?) order by nomein";

    List<Tabellato> listaStazioniAppaltanti = new ArrayList<Tabellato>();
    try {
      List listaSA = sqlManager.getListVector(selectSA, new Object[] { codapp});
      Long confDef = (Long) sqlManager.getObject(selectDEF, new Object[]{codapp,CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD});
      if(new Long(0).equals(confDef)){
        Tabellato tabSA = new Tabellato();
        tabSA.setTipoTabellato(CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);
        tabSA.setDescTabellato("Default");
        listaStazioniAppaltanti.add(tabSA);
      }
      if (listaSA != null && listaSA.size() > 0) {
        for (int i = 0; i < listaSA.size(); i++) {
          Tabellato tabSA = new Tabellato();
          Vector sa = (Vector) listaSA.get(i);
          String codein = ((JdbcParametro) sa.get(0)).getStringValue();
          String nomein = ((JdbcParametro) sa.get(1)).getStringValue();
          if(nomein.length() > 90){nomein = nomein.substring(0,90) + "...";}
          tabSA.setTipoTabellato(codein);
          tabSA.setDescTabellato(codein + " - " + nomein);
          listaStazioniAppaltanti.add(tabSA);
        }
      }
    } catch (SQLException e) {
      this.aggiungiMessaggio(request, "errors.query.db", e.getMessage());
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
    }

    request.setAttribute("listaStazioniAppaltanti", listaStazioniAppaltanti);

    // nel caso di apertura in modifica della configurazione specifica, se siamo
    // nel caso di nuova configurazione, si propongono i parametri di
    // configurazione del server di posta applicati per la configurazione di
    // default
      ConfigurazioneMailForm configForm = new ConfigurazioneMailForm(cfg);
        configForm.setCodapp(codapp);
        configForm.setServer(null);
        configForm.setIdcfg(null);
        configForm.setPorta(null);
        configForm.setProtocollo(null);
        configForm.setTimeout("10000");
        configForm.setDebug(false);
        configForm.setMail(null);
        configForm.setPassword(null);
        configForm.setUserId(null);
        configForm.setMaxMb("5");
        configForm.setDelay("20000");

        request.setAttribute("cfgMailForm", configForm);

    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA, CostantiGenerali.DISABILITA_NAVIGAZIONE);

    logger.debug("nuovo: fine metodo");
    return mapping.findForward(target);
  }


  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public ActionForward modifica(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    logger.debug("modifica: inizio metodo");

    String target = FORWARD_SUCCESS_MODIFICA;
    String codapp = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    String idcfg = request.getParameter("idcfg");

    target = this.getConfig(target, request, codapp, idcfg);

    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA, CostantiGenerali.DISABILITA_NAVIGAZIONE);

    logger.debug("modifica: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action di modifica password
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModificaPassword() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public ActionForward modificaPassword(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    return mapping.findForward(FORWARD_SUCCESS_MODIFICA_PASSWORD);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action apriVerificaConfigurazione
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniApriVerificaConfigurazione() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public ActionForward apriVerificaConfigurazione(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {
    return mapping.findForward(FORWARD_SUCCESS_APRI_VERIFICA);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action verificaConfigurazione
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVerificaConfigurazione() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public ActionForward verificaConfigurazione(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {
    logger.debug("verificaConfigurazione: inizio metodo");

    String target = FORWARD_SUCCESS_VISUALIZZA;
    String paramIdcfg = request.getParameter("idcfg");
    String codapp = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    String messageKey = null;
    String destinatario = StringUtils.stripToNull(request.getParameter("mail"));

    boolean creazioneMailSender = false;
    try {

      // qui va istanziato un mail sender ed inviata una mail
      IMailSender mailSender = MailUtils.getInstance(this.mailManager, codapp, paramIdcfg);
      creazioneMailSender = true;
      mailSender.send(destinatario, "TEST invio mail",
          "Se hai ricevuto questa mail di prova vuol dire che la configurazione di posta inserita funziona!");

      // non è andato in errore significa che ha connesso il server senza problemi
      messageKey = "info.mail.testCompletato";
      logger.info(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);

    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (MailSenderException ms) {
      String logMessageKey = ms.getChiaveResourceBundle();
      String logMessageError = this.resBundleGenerale.getString(logMessageKey);
      for (int i = 0; ms.getParametri() != null && i < ms.getParametri().length; i++)
        logMessageError = logMessageError.replaceAll(UtilityStringhe.getPatternParametroMessageBundle(i), (String) ms.getParametri()[i]);
      logger.error(logMessageError, ms);

      if (!creazioneMailSender) {
        this.aggiungiMessaggio(request, "errors.applicazione.inaspettataException");
      } else {
        logMessageKey = "errors.mail.test.connessioneFallita";
        this.aggiungiMessaggio(request, logMessageKey);
      }
    } catch (Throwable t) {
      messageKey = "errors.mail.test.connessioneFallita";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    } finally {
      if (FORWARD_SUCCESS_VISUALIZZA.equals(target)) {
        // si ricaricano i dati della configurazione per riaprire la pagina di
        // dettaglio, senza riaggiornare il target
        this.getConfig(target, request, codapp, paramIdcfg);
      }
    }

    logger.debug("verificaConfigurazione: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Estrae i dati della configurazione della connessione al provider di posta e popola il request con i dati estratti.
   *
   * @param target
   *        target della action struts, eventualmente da modificare
   * @param request
   *        request HTTP
   * @param codapp
   *        codice applicazione per cui estrarre la configurazione
   * @param idcfg
   *        id configurazione per cui estrarre la configurazione
   * @return target della action struts, eventualmente modificato
   */
  private String getConfig(String target, HttpServletRequest request, String codapp, String idcfg) {
    String messageKey = null;
    try {
      ConfigurazioneMail cfg = mailManager.getConfigurazione(codapp, idcfg);
        // indico che la password esiste, ma non la passo nel form per non
        // mandarla erroneamente nell'HTML
        if (cfg.getPassword() != null) cfg.setPassword("IMPOSTATA");
        request.setAttribute("cfgMailForm", new ConfigurazioneMailForm(cfg));
    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }
    return target;
  }

}
