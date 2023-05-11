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

import it.eldasoft.gene.bl.PropsConfigManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.PropsConfig;
import it.eldasoft.utils.properties.ConfigManager;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Verifica se l'applicativo &egrave; configurato per aprire la presente pagina e nell'eventualit&agrave; inizializza i dati.
 *
 * @author Stefano.Sabbadin
 */
public class OpenRichiestaAssistenzaAction extends Action {

  /** Logger Log4J di classe */
  private Logger             logger            = Logger.getLogger(OpenRichiestaAssistenzaAction.class);

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  private ResourceBundle     resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /**
   * manager per il reperimento delle configurazioni su DB.
   */
  private PropsConfigManager propsConfigManager;

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
      throws Exception {
    String target = CostantiGeneraliStruts.FORWARD_OK;
    boolean continua = true;

    ServletContext context = request.getSession().getServletContext();
    String type = (String) context.getAttribute(CostantiGenerali.ATTR_ATTIVA_FORM_ASSISTENZA);
    // in caso di utente loggato e' settato il codice applicazione in uso nella sua sessione.
    // in caso di utente non loggato, si prende il codice applicazione dal file di properties (occhio che teoricamente, anche se non e'
    // mai successo, il codice indicato nel file di properties potrebbe essere una concatenazione di codici).
    String codapp = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    if (codapp == null) {
      codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
    }

    if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MODO_NON_ATTIVO.equals(type)) {
      String chiave = "errors.funzione.nonAttiva";
      logger.error(resBundleGenerale.getString(chiave));
      aggiungiMessaggio(request, chiave);
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      continua = false;
    }

    if (continua) {
      // si controlla per sicurezza che non sia stato sbiancato mediante script la proprieta'
      if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MODO_MAIL.equals(type)) {
        if (continua) {
          PropsConfig propMail = this.propsConfigManager.getProperty(codapp, CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MAIL);
          if (propMail == null || StringUtils.isBlank(propMail.getValore())) {
            logger.error("Popolare la configurazione '" + CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MAIL + "'");
            String chiave = "errors.assistenza.configurazioneErrata";
            logger.error(resBundleGenerale.getString(chiave));
            aggiungiMessaggio(request, chiave);
            target = "error";
            continua = false;
          }
        }
      }

      // nel caso di configurazione HDA si controlla la valorizzazione dei diversi parametri in quanto potrebbe non essere specificata la
      // password, e nel qual caso venga fatta una configurazione da script se per errore un parametro non risulta presente e' meglio
      // evitare all'utente di usare la funzionalita' e bloccarlo in fase di invio ma piuttosto lo si blocca subito
      if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MODO_WS.equals(type)) {

        if (continua) {
          PropsConfig propHdaURL = this.propsConfigManager.getProperty(codapp, CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_URL);
          if (propHdaURL == null || StringUtils.isBlank(propHdaURL.getValore())) {
            logger.error("Popolare la configurazione '" + CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_URL + "'");
            String chiave = "errors.assistenza.configurazioneErrata";
            logger.error(resBundleGenerale.getString(chiave));
            aggiungiMessaggio(request, chiave);
            target = "error";
            continua = false;
          }
        }

        if (continua) {
          PropsConfig propHdaLogin = this.propsConfigManager.getProperty(codapp, CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_USR);
          if (propHdaLogin == null || StringUtils.isBlank(propHdaLogin.getValore())) {
            logger.error("Popolare la configurazione '" + CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_USR + "'");
            String chiave = "errors.assistenza.configurazioneErrata";
            logger.error(resBundleGenerale.getString(chiave));
            aggiungiMessaggio(request, chiave);
            target = "error";
            continua = false;
          }
        }

        if (continua) {
          PropsConfig propHdaPassword = this.propsConfigManager.getProperty(codapp,
              CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_PWD);
          if (propHdaPassword == null || StringUtils.isBlank(propHdaPassword.getValore())) {
            logger.error("Popolare la configurazione '" + CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_PWD + "'");
            String chiave = "errors.assistenza.configurazioneErrata";
            logger.error(resBundleGenerale.getString(chiave));
            aggiungiMessaggio(request, chiave);
            target = "error";
            continua = false;
          }
        }

        if (continua) {
          PropsConfig propHdaProductId = this.propsConfigManager.getProperty(codapp, CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_ID_PRODOTTO);
          if (propHdaProductId == null || StringUtils.isBlank(propHdaProductId.getValore())) {
            logger.error("Popolare la configurazione '" + CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_ID_PRODOTTO + "'");
            String chiave = "errors.assistenza.configurazioneErrata";
            logger.error(resBundleGenerale.getString(chiave));
            aggiungiMessaggio(request, chiave);
            target = "error";
            continua = false;
          }
        }

      }
    }

    if (continua) {
      // arrivati qui senza errori si procede con il caricamento delle possibili casistiche di errore e l'apertura della pagina
      String oggettiConcatenati = StringUtils.stripToNull(propsConfigManager.getProperty(codapp,
          CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_OGGETTO).getValore());
      String[] oggetti = oggettiConcatenati.split(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE);
      request.setAttribute("oggetti", oggetti);
    }

    return mapping.findForward(target);
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

}
