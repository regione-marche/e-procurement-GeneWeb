/*
 * Created on 09-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 * 
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.bl.genmod.GestioneFileModelloException;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.ListaForm;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;


/**
 * Action per la cancellazione di un insieme di modelli a partire dalla pagina
 * di lista di modelli
 * 
 * @author Luca.Giacomazzo
 */
public class ListaModelliAction extends AbstractActionBaseGenModelli {

  /* logger della classe */
  static Logger logger = Logger.getLogger(ListaModelliAction.class);

  /** Manager dei modelli */
  private ModelliManager   modelliManager;

  /**
   * @param modelliManager
   *        The modelliManager to set.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action deleteModello
   * 
   * @return opzioni per accedere alla action
   */
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    // Di default rivisualizza il dettaglio del modello
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;
    
    ListaForm listaIdAccount = (ListaForm) form;
    
    try {
      for(int i=0; i < listaIdAccount.getId().length; i++){
        this.modelliManager.deleteModello(
            UtilityNumeri.convertiIntero(listaIdAccount.getId()[i]).intValue(),
            (String) request.getSession().getAttribute(
            CostantiGenerali.MODULO_ATTIVO));
      }
    } catch (GestioneFileModelloException e) {
      target = CostantiGenModelli.FORWARD_MODELLO_ELIMINATO;
      messageKey = "errors.modelli.delete";
      // Aggiungo l'eventuale codice in più
      if (!e.getCodiceErrore().equals("")) messageKey += e.getCodiceErrore();

      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);

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
    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

}