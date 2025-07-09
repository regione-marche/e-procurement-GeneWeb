package it.eldasoft.gene.tags.functions;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.login.IsUserLoggedAction;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;

/**
 * Verifica le opzioni assegnate all'utente, e se non sono presenti le opzioni indicate in input, si redireziona alla pagina di errore
 * generale.
 * 
 * @author stefano.sabbadin
 */
public class CheckOpzioniUtenteFunction extends AbstractFunzioneTag {

  Logger logger = Logger.getLogger(CheckOpzioniUtenteFunction.class);

  public CheckOpzioniUtenteFunction() {
    super(2, new Class[] {PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    boolean autorizzato = false;

    HttpSession session = pageContext.getSession();
    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    ProfiloUtente account = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    if (account != null) {
      // carico le opzioni per cui è abilitato l'utente loggato
      OpzioniUtente opzioniUtente = new OpzioniUtente(account.getFunzioniUtenteAbilitate());
      // verifico le opzioni utente richieste ed indicate da parametro
      CheckOpzioniUtente opzioniPerAbilitazione = new CheckOpzioniUtente((String) params[1]);
      autorizzato = opzioniPerAbilitazione.test(opzioniUtente);
    }

    if (!autorizzato) {
      logger.error("Bloccato accesso non autorizzato richiesto dall'utente " + account.getLogin() + " alla url " + request.getRequestURL());
      ActionMessages messages = (ActionMessages) request.getAttribute(Globals.MESSAGE_KEY);

      if (messages == null) {
        messages = new ActionMessages();
      }
      @SuppressWarnings("unchecked")
      Iterator<ActionMessage> iter = messages.get(ActionBase.MESSAGGIO_ERRORE);

      boolean isMessaggioPresente = false;
      while (iter.hasNext() && !isMessaggioPresente) {
        ActionMessage message = iter.next();
        if (message.getKey().equals("errors.opzione.noAbilitazione")) isMessaggioPresente = true;
      }
      if (!isMessaggioPresente) {
        messages.add(ActionBase.MESSAGGIO_ERRORE, new ActionMessage("errors.opzione.noAbilitazione"));
        // Save the messages
        request.setAttribute(Globals.MESSAGE_KEY, messages);
      }

      pageContext.getResponse().reset();
      try {
        pageContext.forward("/Errore.do");
      } catch (ServletException | IOException e) {
        logger.error("Errore inaspettato durante l'inoltro alla pagina di errore generale", e);
      }

    }
    return "";

  }

}
