/*
 * Created on 06/07/2016
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
import it.eldasoft.gene.commons.web.struts.ListaForm;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.sicurezza.CriptazioneException;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action per il caricamento delle configurazioni dei parametri di posta.
 *
 * @author Cristian.Febas
 */
public class ListaConfigurazioniMailAction extends DispatchActionBase {

  static Logger               logger                            = Logger.getLogger(ConfigurazioneMailAction.class);

  private static final String FORWARD_SUCCESS_LISTA             = "successLista";

  private static final String FORWARD_SUCCESS_ELIMINA             = "successElimina";

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
   * Funzione che restituisce le opzioni per accedere alla action lista
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniLista() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public ActionForward lista(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    logger.debug("lista: inizio metodo");

    String target = FORWARD_SUCCESS_LISTA;
    String codapp = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);

    try {
      List<?> listaConfigurazioniMail = this.mailManager.getListaConfigurazioni((String)request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
      request.setAttribute("listaConfigurazioniMailForm", listaConfigurazioniMail);

    } catch (CriptazioneException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      String messageKey = e.getChiaveResourceBundle();
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    logger.debug("lista: fine metodo");
    return mapping.findForward(target);
  }




  /**
   * Funzione che restituisce le opzioni per accedere alla action elimina
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniElimina() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public ActionForward elimina(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    logger.debug("elimina: inizio metodo");

    String target = FORWARD_SUCCESS_ELIMINA;
    String paramIdcfg = request.getParameter("idcfg");
    String codapp = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    try {
      this.mailManager.getConfigurazioneMailManager().deleteConfigurazioneMail(codapp, paramIdcfg);
      List<?> listaConfigurazioniMail = this.mailManager.getListaConfigurazioni((String)request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
      request.setAttribute("listaConfigurazioniMailForm", listaConfigurazioniMail);

    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      String messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (CriptazioneException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      String messageKey = e.getChiaveResourceBundle();
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    logger.debug("elimina: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action eliminaSelez
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEliminaSelez() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  public ActionForward eliminaSelez(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("eliminaSelez: inizio metodo");
    String target = FORWARD_SUCCESS_ELIMINA;
    String messageKey = null;

    ListaForm listaIdConfigurazioneMail = (ListaForm) form;
    String codapp = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    try {
      for(int i=0; i < listaIdConfigurazioneMail.getId().length; i++){
        this.mailManager.getConfigurazioneMailManager().deleteConfigurazioneMail(codapp,listaIdConfigurazioneMail.getId()[i]);
        List<?> listaConfigurazioniMail = this.mailManager.getListaConfigurazioni((String)request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
        request.setAttribute("listaConfigurazioniMailForm", listaConfigurazioniMail);
     }

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
    if (logger.isDebugEnabled()) logger.debug("eliminaSelez: fine metodo");
    return mapping.findForward(target);
  }




}
