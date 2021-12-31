/*
 * Created on 8-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.tags.utils.UtilityTags;

import java.util.Iterator;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

/**
 * Questa Action risulta la base di tutte le Dispatch Action da definire nella
 * web application. Non effettua controlli su sessione e opzioni acquistate.
 * Ogni azione azione vera e propria viene richiamata per reflection in maniera
 * del tutto analoga a come avviene utilizzando la DispatchAction. Dal punto di
 * vista della stesura del codice, l'unica modifica da realizzare è l'estensione
 * di questa classe e non della DispatchAction.
 *
 * @author Francesco.DeFilippis
 */
public abstract class DispatchActionBaseNoSessione extends DispatchAction
    implements ActionInterface {

  /** Logger Log4J di classe */
  static Logger         logger            = Logger.getLogger(DispatchActionBase.class);

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  public ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /**
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    if (isCancelled(request)) {
      ActionForward af = cancelled(mapping, form, request, response);
      if (af != null) return af;
    }
    String parameter = getParameter(mapping, form, request, response);
    String name = getMethodName(mapping, form, request, response, parameter);
    if ("execute".equals(name) || "perform".equals(name)) {
      String message = messages.getMessage("dispatch.recursive",
          mapping.getPath());
      log.error(message);
      throw new ServletException(message);
    } else {

      UtilityTags.preAction(request);
      ActionForward ret = dispatchMethod(mapping, form, request, response, name);
      UtilityTags.postAction(request);
      return ret;
    }
  }

  /**
   * Crea un ActionMessages nel request, popolato con il messaggio individuato
   * dalla chiave in input. Se la chiave del messaggio da inserire e' gia'
   * presente il messaggio non viene inserito la seconda volta
   *
   * @param request
   *        request http
   * @param chiave
   *        chiave del messaggio da inserire
   */
  protected void aggiungiMessaggio(HttpServletRequest request, String chiave) {
    String tipoMessaggio = ActionBase.getTipoMessaggioFromChiave(chiave);
    /*
     * L.G. 07/06/2007: modifica per evitare di inserire più di una volta lo
     * stesso messaggio. Se la chiave del messaggio da inserire e' gia' presente
     * nel oggetto ActionMessages del request, il messaggio non viene inserito
     * la seconda volta, altrimenti viene inserito
     */
    ActionMessages errorsInTheRequest = this.getMessages(request);
    @SuppressWarnings("unchecked")
    Iterator<ActionMessage> iter = errorsInTheRequest.get(tipoMessaggio);

    boolean isMessaggioPresente = false;
    while (iter.hasNext() && !isMessaggioPresente) {
      ActionMessage message = iter.next();
      if (message.getKey().equals(chiave)) isMessaggioPresente = true;
    }
    if (!isMessaggioPresente) {
      ActionMessages errors = new ActionMessages();
      errors.add(tipoMessaggio, new ActionMessage(chiave));
      if (!errors.isEmpty()) this.addMessages(request, errors);
    }
  }

  /**
   * Crea un ActionMessages nel request, popolato con il messaggio individuato
   * dalla chiave in input e con un argomento. Se la chiave e l'argomento del
   * messaggio da inserire sono gia' presenti il messaggio non viene inserito la
   * seconda volta
   *
   * @param request
   *        request http
   * @param chiave
   *        chiave del messaggio da inserire
   * @param argomento1
   *        argomento 1
   */
  protected void aggiungiMessaggio(HttpServletRequest request, String chiave,
      String argomento1) {
    String tipoMessaggio = ActionBase.getTipoMessaggioFromChiave(chiave);
    /*
     * L.G. 07/06/2007: modifica per evitare di inserire più di una volta lo
     * stesso messaggio. Se la chiave e l'argomento del messaggio da inserire
     * sono gia' presenti nel oggetto ActionMessages del request, il messaggio
     * non viene inserito la seconda volta, altrimenti viene inserito
     */
    ActionMessages errorsInTheRequest = this.getMessages(request);
    @SuppressWarnings("unchecked")
    Iterator<ActionMessage> iter = errorsInTheRequest.get(tipoMessaggio);
    boolean isMessaggioPresente = false;
    while (iter.hasNext() && !isMessaggioPresente) {
      ActionMessage message = iter.next();
      if (message.getKey().equals(chiave)
          && ((String) message.getValues()[0]).equals(argomento1))
        isMessaggioPresente = true;
    }
    if (!isMessaggioPresente) {
      ActionMessages errors = new ActionMessages();
      errors.add(tipoMessaggio, new ActionMessage(chiave, argomento1));
      if (!errors.isEmpty()) this.addMessages(request, errors);
    }
  }

  /**
   * Crea un ActionMessages nel request, popolato con il messaggio individuato
   * dalla chiave in input e con due argomenti. Se la chiave e i due argomenti
   * del messaggio da inserire sono gia' presenti il messaggio non viene
   * inserito la seconda volta
   *
   * @param request
   *        request http
   * @param chiave
   *        chiave del messaggio da inserire
   * @param argomento1
   *        argomento 1
   * @param argomento2
   *        argomento 2
   */
  protected void aggiungiMessaggio(HttpServletRequest request, String chiave,
      String argomento1, String argomento2) {
    String tipoMessaggio = ActionBase.getTipoMessaggioFromChiave(chiave);
    /*
     * L.G. 07/06/2007: modifica per evitare di inserire più di una volta lo
     * stesso messaggio. Se la chiave e i due argomenti del messaggio da
     * inserire sono gia' presenti nel oggetto ActionMessages del request, il
     * messaggio non viene inserito la seconda volta, altrimenti viene inserito
     */
    ActionMessages errorsInTheRequest = this.getMessages(request);
    @SuppressWarnings("unchecked")
    Iterator<ActionMessage> iter = errorsInTheRequest.get(tipoMessaggio);
    boolean isMessaggioPresente = false;
    while (iter.hasNext() && !isMessaggioPresente) {
      ActionMessage message = iter.next();
      if (message.getKey().equals(chiave)
          && ((String) message.getValues()[0]).equals(argomento1)
          && ((String) message.getValues()[1]).equals(argomento2))
        isMessaggioPresente = true;
    }
    if (!isMessaggioPresente) {
      ActionMessages errors = new ActionMessages();
      errors.add(tipoMessaggio, new ActionMessage(chiave, argomento1,
          argomento2));
      if (!errors.isEmpty()) this.addMessages(request, errors);
    }
  }

  public void publicSaveMessages(HttpServletRequest request,
      ActionMessages errors) {
    this.saveMessages(request, errors);
  }
}
