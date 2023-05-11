package it.eldasoft.gene.commons.web.struts;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Classe da cui ereditare per azioni Ajax da proteggere in modo tale che siano
 * fruibili solo se l'utente prima si &egrave; loggato.
 * 
 * @author manuel.bridda
 */
public abstract class DispatchActionAjaxLogged extends DispatchActionBase {


  /**
   * Si ridefinisce il metodo del padre in modo da escludere alcuni controlli
   * legati ad opzioni licenziate ed opzioni utente, ma soprattutto per escludere
   * il popolamento degli elementi dell'history e di alcuni attributi derivati da
   * richieste provenienti da pagine costruite con la tag library gene, che per
   * azioni Ajax non hanno senso.
   * 
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    String parameter = getParameter(mapping, form, request, response);
    String name = getMethodName(mapping, form, request, response, parameter);

    String target = null;
    String messageKey = null;

    if (!this.verificaSessionePresente(request)) {
      target = CostantiGeneraliStruts.FORWARD_SESSION_TIMEOUT;
      messageKey = "errors.session.timeOut";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }

    if (target == null && !this.verificaDoppiaAutenticazione(request)) {
      // in caso sia presente il flag della doppia autenticazione
      // vuol dire che l'utente admin non ha effettuato la doppia 
      // autenticazione quindi si introduce un 
      // messaggio d'errore nel request e si termina l'elaborazione
      // redirezionando l'utente alla pagina principale dell'applicazione
      // con il messaggio d'errore stesso
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.opzione.noDoppiaAutenticazione";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }

    if (target != null)
      return mapping.findForward(target);
    else {
      ActionForward ret = dispatchMethod(mapping, form, request, response, name);
      return ret;
    }    
  }

  /**
   * Metodo non pi&ugrave; utilizzato visto l'override del metodo execute rispetto
   * alla classe padre.
   */
  @Override
  protected String getOpzioneAcquistata() {
    return null;
  }

  /**
   * Metodo non pi&ugrave; utilizzato visto l'override del metodo execute rispetto
   * alla classe padre.
   */
  @Override
  protected boolean verificaAbilitazioneOpzione(HttpServletRequest request, ActionMapping mapping) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    return true;
  }

}
