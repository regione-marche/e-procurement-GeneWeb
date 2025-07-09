package it.eldasoft.gene.web.struts;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
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
import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.bl.admin.UffintManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.UfficioIntestatario;

/**
 * Azione che estrae l'elenco delle stazioni appaltanti presenti nell'installazione.
 *
 * @author Stefano.Sabbadin
 */
public class ListaEntiLoginAction extends Action {

  /** Logger Log4J di classe */
  static Logger         logger = Logger.getLogger(ListaEntiLoginAction.class);

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  public ResourceBundle       resBundleGenerale       = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /**
   * Reference alla classe di business logic per la gestione delle associazioni
   * tra l'utente e l'archivio uffici intestatari
   */
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

    if (logger.isDebugEnabled()) {
      logger.debug("execute: inizio metodo");
    }

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    try {
      List<?> elencoUfficiIntestatari = this.uffintManager.getUfficiIntestatari();
      switch (elencoUfficiIntestatari.size()) {
      case 0:
        // elimino la sessione in quanto devo bloccare l'accesso
        request.getSession().invalidate();
        // nessuna associazione => errore
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.listaEntiLogin";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
        break;
      case 1:
        // una associazione => attribuisco l'ufficio e proseguo arrivando alla
        // homepage
        // rilegge il codice applicazione come fatto nella CheckVersioneAction
        request.getSession().setAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO,
            ((UfficioIntestatario) elencoUfficiIntestatari.get(0)).getCodice());
        request.getSession().setAttribute(CostantiGenerali.NOME_UFFICIO_INTESTATARIO_ATTIVO,
            ((UfficioIntestatario) elencoUfficiIntestatari.get(0)).getNome());
        request.getSession().removeAttribute(
            CostantiGenerali.SENTINELLA_SELEZIONA_UFFICIO_INTESTATARIO);
        break;
      default:
        // potrei ricevere nella chiamata un ufficio intestatario da utilizzare,
        // e dopo aver controllato che sia uno di quelli associati all'utente
        // lo utilizzo, altrimenti vado alla pagina di selezione
        String uffint = request.getParameter("uffint");
        boolean trovato = false;
        if (uffint != null) {
          for (int i = 0; i < elencoUfficiIntestatari.size() && !trovato; i++) {
            UfficioIntestatario ufficio = (UfficioIntestatario) elencoUfficiIntestatari.get(i);
            if (ufficio.getCodice().equals(uffint)) {
              request.getSession().setAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO,
                  ufficio.getCodice());
              request.getSession().setAttribute(CostantiGenerali.NOME_UFFICIO_INTESTATARIO_ATTIVO,
                  ufficio.getNome());
              trovato = true;
            }
          }
        }
        if (!trovato) {
          // uffint passato come parametro non associato all'utente oppure
          // più di una associazione => vado alla pagina di selezione
          request.setAttribute("elencoUfficiIntestatari",
              elencoUfficiIntestatari);
          request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
              CostantiGenerali.DISABILITA_NAVIGAZIONE);
          target = target.concat("Lista");
        }
        request.getSession().setAttribute(
            CostantiGenerali.SENTINELLA_SELEZIONA_UFFICIO_INTESTATARIO, "1");
      }

    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.fatal(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
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
