/*
 * Created on 30-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.VersioneManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Azione che verifica l'allineamento tra la versione indicata nell'applicazione
 * e quella presente nel DB. Viene associata all'evento di login in modo tale da
 * verificare l'utilizzabilità dell'applicativo prima di accederci.
 *
 * @author Stefano.Sabbadin
 */
public class CheckVersioneAction extends ActionBaseNoOpzioni {

  private static final String SUFFISSO_FILE_VERSIONE = "_VER.TXT";

  /** Logger Log4J di classe */
  static Logger               logger                 = Logger.getLogger(CheckVersioneAction.class);

  /**
   * Reference alla classe di business logic per l'estrazione della versione di
   * un modulo applicativo
   */
  private VersioneManager     versioneManager;

  /**
   * @param versioneManager
   *        versioneManager da settare internamente alla classe.
   */
  public void setVersioneManager(VersioneManager versioneManager) {
    this.versioneManager = versioneManager;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = CostantiGeneraliStruts.FORWARD_OK;

    ServletContext context = request.getSession().getServletContext();
    InputStream stream = null;
    String messageKey = null;
    String versioneDB = null;
    String versioneFile = null;
    String nomeHomePage = null;

    // legge il codice applicazione a partire dal request (parameter o
    // attribute, può arrivare qui da una jsp o da un'altra action)
    String codiceApplicazione = request.getParameter("codApp");
    if (codiceApplicazione == null)
      codiceApplicazione = (String) request.getAttribute("codApp");

    // se non si possiede il codice applicazione allora si termina con errore
    if (codiceApplicazione == null) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.checkVersione.mancaCodiceApplicazione";
      logger.fatal(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }

    // se il codice applicazione è valorizzato, si procede alla verifica che il
    // codice applicazione ricevuto sia uno di quelli specificati nel file di
    // properties
    if (CostantiGeneraliStruts.FORWARD_OK.equals(target)) {
      String[] elencoCodiciApplicazione = ConfigManager.getValore(
          CostantiGenerali.PROP_CODICE_APPLICAZIONE).split(
          CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE);
      if (!Arrays.asList(elencoCodiciApplicazione).contains(codiceApplicazione)) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.checkVersione.codiceApplicazioneNonPrevisto";
        logger.fatal(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
    }

    // se il codice applicazione è valorizzato, si procede all'apertura del
    // file
    if (CostantiGeneraliStruts.FORWARD_OK.equals(target)) {
      String nomeFileVersione = CostantiGenerali.PATH_WEBINF
          + codiceApplicazione
          + SUFFISSO_FILE_VERSIONE;
      stream = context.getResourceAsStream(nomeFileVersione);
      if (stream == null) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.checkVersione.file.inesistente";
        logger.fatal(this.resBundleGenerale.getString(messageKey).replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(0),
            codiceApplicazione));
        this.aggiungiMessaggio(request, messageKey, codiceApplicazione);
      }
    }

    // se il file è stato aperto, si procede alla lettura dello stesso
    if (CostantiGeneraliStruts.FORWARD_OK.equals(target)) {
      try {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        versioneFile = br.readLine().trim();
        if (versioneFile == null) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.checkVersione.mancaVersioneInFile";
          logger.fatal(this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              codiceApplicazione));
          this.aggiungiMessaggio(request, messageKey, codiceApplicazione);
        }
      } catch (IOException e) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.checkVersione.file.error";
        logger.fatal(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
    }

    // se la lettura da file è andata a buon fine, si procede alla lettura nel
    // DB
    if (CostantiGeneraliStruts.FORWARD_OK.equals(target)) {
      try {
        versioneDB = this.versioneManager.getVersione(codiceApplicazione);
        if (versioneDB == null) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.checkVersione.mancaVersioneInDB";
          logger.fatal(this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              codiceApplicazione));
          this.aggiungiMessaggio(request, messageKey, codiceApplicazione);
        }
      } catch (DataAccessException e) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.database.dataAccessException";
        logger.fatal(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      }
    }

    // se le estrazioni delle versioni da file e db sono andate a buon fine,
    // allora vengono comparate
    if (CostantiGeneraliStruts.FORWARD_OK.equals(target)
        && !versioneFile.equals(versioneDB)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.checkVersione.disallineamento";
      logger.fatal(this.resBundleGenerale.getString(messageKey).replaceAll(
          UtilityStringhe.getPatternParametroMessageBundle(0),
          codiceApplicazione).replaceAll(
          UtilityStringhe.getPatternParametroMessageBundle(1), versioneDB).replaceAll(
          UtilityStringhe.getPatternParametroMessageBundle(2), versioneFile));
      this.aggiungiMessaggio(request, messageKey, codiceApplicazione,
          versioneDB, versioneFile);
    }

    if (CostantiGeneraliStruts.FORWARD_OK.equals(target)) {
      nomeHomePage = "home" + codiceApplicazione + ".jsp";
      stream = context.getResourceAsStream(UtilityTags.DEFAULT_PATH_PAGINE_JSP
          + nomeHomePage);
      if (stream == null) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.checkVersione.homePage.inesistente";
        logger.fatal(this.resBundleGenerale.getString(messageKey).replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(0),
            codiceApplicazione));
        this.aggiungiMessaggio(request, messageKey, codiceApplicazione);
      }
    }

    // il controllo è andato a buon fine, si setta il codice applicazione e la
    // versione attualmente attivi
    if (CostantiGeneraliStruts.FORWARD_OK.equals(target)) {
      HttpSession session = request.getSession();
      this.cleanSession(request);

      if ("1".equals(ConfigManager.getValore(CostantiGenerali.SKIP_USO_PROFILI))) {
        session.setAttribute(CostantiGenerali.MODULO_ATTIVO, codiceApplicazione);
      }
      session.setAttribute(CostantiGenerali.VERSIONE_MODULO_ATTIVO,
          versioneFile);

      String profiloApplicativoSelezionato = (String)request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
      // per il profilo acceduto si resettano le chiavi navigate
      HashMap<String, HashSet<String>> hashProfiliKey = (HashMap<String, HashSet<String>>) session.getAttribute(CostantiGenerali.PROFILI_KEYS);
      HashMap<String, HashSet<String>> hashProfiliKeyParent = (HashMap<String, HashSet<String>>) session.getAttribute(CostantiGenerali.PROFILI_KEY_PARENTS);
      hashProfiliKey.put(profiloApplicativoSelezionato, new HashSet<String>());
      hashProfiliKeyParent.put(profiloApplicativoSelezionato, new HashSet<String>());

      // se è prevista la gestione delle associazioni con uffici intestatari, si
      // passa al controllo relativo, altrimenti si passa alla homepage
      // dell'applicativo
      // Revisione del 26/06/2015
      // property nel global = 0 => non si filtra per uffint mai
      // property nel global = 1 => si può filtrare per uffint, e il default nella w_azioni indica che si filtra

      if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_UFFINT_ABILITATI))) {
        // Prevista gestione UFFINT, in funzione della voce di profilo "ALT.GENE.associazioneUffintAbilitata"
        GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
            request.getSession().getServletContext(),GeneManager.class);
        if (geneManager.getProfili().checkProtec(
            profiloApplicativoSelezionato,
            "FUNZ",
            "VIS",
            "ALT.GENE.associazioneUffintAbilitata")){
          target = "uffint";
          request.getSession().setAttribute(CostantiGenerali.ATTR_UFFINT_ABILITATI, "1");

        } else {
          request.getSession().removeAttribute(CostantiGenerali.SENTINELLA_SELEZIONA_UFFICIO_INTESTATARIO);
          request.getSession().removeAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
          request.getSession().removeAttribute(CostantiGenerali.NOME_UFFICIO_INTESTATARIO_ATTIVO);
          request.getSession().setAttribute(CostantiGenerali.ATTR_UFFINT_ABILITATI, "0");
        }
      } else {
        // Non e' prevista la gestione UFFINT nel global.properties.
        request.getSession().removeAttribute(CostantiGenerali.SENTINELLA_SELEZIONA_UFFICIO_INTESTATARIO);
        request.getSession().removeAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
        request.getSession().removeAttribute(CostantiGenerali.NOME_UFFICIO_INTESTATARIO_ATTIVO);
        request.getSession().setAttribute(CostantiGenerali.ATTR_UFFINT_ABILITATI, "0");

//
//        if (logger.isDebugEnabled()) {
//          logger.debug("runAction: fine metodo");
//        }
//        // di default si va alla homepage, a meno che non sia presente un
//        // parametro nel request denominato "skipHome" valorizzato a 1
//        if (!"1".equals(request.getParameter("skipHome")))
//          return UtilityStruts.redirectToPage("home"
//              + codiceApplicazione
//              + ".jsp", false, request);
//        else
//          return UtilityStruts.redirectToPage(request.getParameter("href"),
//              false, request);
      }
    }

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }

    return mapping.findForward(target);
  }

}
