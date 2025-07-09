/*
 * Created on 06-10-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

/**
 * La funzione permette di reindirizzare l'utente sulla pagina di errore direttamente dalla jsp
 *
 * @author Manuel Bridda
 */

public class SetErroreFunction extends AbstractFunzioneTag {

  Logger logger = Logger.getLogger(SetErroreFunction.class);

  public SetErroreFunction() {
    super(3, new Class[] { PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    HttpSession session = pageContext.getSession();
    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    ProfiloUtente account = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    String errorDescription = "";
    if(StringUtils.isNotBlank((String) params[2]))
      errorDescription=". Motivazione: "+params[2];

    logger.error("Bloccato accesso richiesto dall'utente " + account.getLogin() +" (ID: "+account.getId()+")" + " alla url " + request.getRequestURL()+errorDescription);


    ActionMessages messages = (ActionMessages) request.getAttribute(Globals.MESSAGE_KEY);

    if (messages == null) {
      messages = new ActionMessages();
    }
    @SuppressWarnings("unchecked")
    Iterator<ActionMessage> iter = messages.get(ActionBase.MESSAGGIO_ERRORE);

    boolean isMessaggioPresente = false;
    while (iter.hasNext() && !isMessaggioPresente) {
      ActionMessage message = iter.next();
      if (message.getKey().equals((String) params[1])) isMessaggioPresente = true;
    }
    if (!isMessaggioPresente) {
      messages.add(ActionBase.MESSAGGIO_ERRORE, new ActionMessage((String) params[1]));
      // Save the messages
      request.setAttribute(Globals.MESSAGE_KEY, messages);
    }

    pageContext.getResponse().reset();
    try {
      pageContext.forward("/Errore.do");
    } catch (ServletException | IOException e) {
      logger.error("Errore inaspettato durante l'inoltro alla pagina di errore generale", e);
    }
    return "";
  }

}
