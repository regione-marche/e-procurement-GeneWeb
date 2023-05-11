/*
 * Created on 06/feb/2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.system.mail;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.system.ConfigurazioneMail;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.www.PortaleAlice.EsitoOutType;
import it.eldasoft.www.PortaleAlice.PortaleAliceProxy;
import it.eldasoft.www.PortaleAlice.ProtocolloMail;

/**
 * Action per la gestione della sincronizzazione dei parametri di posta sul
 * Portale.
 *
 * @author Stefano.Sabbadin
 */
public class SincronizzaConfigurazioneMailPortaleAction extends ActionBase {

  static Logger               logger                            = Logger.getLogger(ConfigurazioneMailAction.class);

  private final static String COD_ERRORE_SYNC                   = "SYNC-DENIED";

  private MailManager         mailManager;

  /**
   * @param mailManager
   *        mailManager da settare internamente alla classe.
   */
  public void setMailManager(MailManager mailManager) {
    this.mailManager = mailManager;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  @Override
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_GESTIONE_PORTALE;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioniRunAction()
   */
  @Override
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    logger.debug("runAction: inizio metodo");
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;
    String paramIdcfg = request.getParameter("idcfg");
    String codapp = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);

    try {
      ConfigurazioneMail cfg = mailManager.getConfigurazione(codapp,paramIdcfg);

      //Si deve comunicare al portale che vi sono state delle modifiche
      PortaleAliceProxy proxy = new PortaleAliceProxy();
      //indirizzo del servizio letto da properties
      String endPoint = ConfigManager.getValore(CostantiGenerali.PROP_URL_WEB_SERVICE_PORTALE_ALICE);
      proxy.setEndpoint(endPoint);
      // si predispongono i parametri da inoltrare al servizio
      Integer porta = null;
      if (cfg.getPorta() != null) {
        porta = Integer.valueOf(cfg.getPorta());
      }
      ProtocolloMail prot = null;
      if ("0".equals(cfg.getProtocollo())) {
        prot = ProtocolloMail.SMTP;
      } else if ("1".equals(cfg.getProtocollo())) {
        prot = ProtocolloMail.SMTPS;
      } else {
        prot = ProtocolloMail.STARTTLS;
      }
      Integer timeout = null;
      if (cfg.getTimeout() != null) {
        timeout = Integer.valueOf(cfg.getTimeout());
      }
      boolean debug = false;
      if ("1".equals(cfg.getDebug())) {
        debug = true;
      }
      String username = null;
      if (cfg.getPassword() != null) {
        // se si richiede l'autenticazione con password, lo username va valorizzato
        username = (cfg.getUserId() != null ? cfg.getUserId() : cfg.getMailMitt());
      }
      // si effettua la chiamata
      EsitoOutType risultato = proxy.sincronizzaConfigurazioneMail(cfg.getServer(), porta, prot, timeout, debug, username, cfg.getPassword(), cfg.getMailMitt());
      if(risultato.isEsitoOk()){
        this.aggiungiMessaggio(request, "info.mail.sync.portale.ok");
      } else {
        String codErrore= risultato.getCodiceErrore();
        if(COD_ERRORE_SYNC.equals(codErrore))
          this.aggiungiMessaggio(request, "errors.mail.sync.portale.noAutomatica", null);
        else
          this.aggiungiMessaggio(request, "errors.mail.sync.portale.inaspettato", risultato.getCodiceErrore());
      }

    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (RemoteException e) {
      messageKey = "errors.mail.sync.portale.disattivo";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

}
