package it.eldasoft.gene.web.struts;

import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import it.eldasoft.gene.bl.admin.UffintManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.UfficioIntestatario;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

/**
 * Azione per settare in sessione l'ufficio intestatario come entry point appplicativa in configurazione multiente.
 *
 * @author Stefano.Sabbadin
 */
public class SetEnteLoginAction extends Action {

  /** Logger Log4J di classe */
  static Logger         logger = Logger.getLogger(SetEnteLoginAction.class);
  
  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  public ResourceBundle       resBundleGenerale       = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  private UffintManager uffintManager;

  /**
   * @param uffintManager
   *        uffintManager da settare internamente alla classe.
   */
  public void setUffintManager(UffintManager uffintManager) {
    this.uffintManager = uffintManager;
  }

  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("execute: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    String idUffint = request.getParameter("uffint");
    UfficioIntestatario ufficio = this.uffintManager.getUfficioIntestatarioByPK(idUffint);
    
    if (ufficio == null) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      String messageKey = "errors.listaEntiLogin.inesistente";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    } else {
      // set in sessione dell'id dell'ufficio e del suo nome
      request.getSession().setAttribute(
          CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO, ufficio.getCodice());
      request.getSession().setAttribute(
          CostantiGenerali.NOME_UFFICIO_INTESTATARIO_ATTIVO, ufficio.getNome());

      int livEvento = 1;
      String errMsgEvento = "";

      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(livEvento);
      logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_SET_UFFINT);
      logEvento.setDescr("Selezione ufficio intestatario in configurazione multi ente (" + ufficio.getCodice() + ")");
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    if (logger.isDebugEnabled()) {
      logger.debug("execute: fine metodo");
    }

    return mapping.findForward(target);
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

}