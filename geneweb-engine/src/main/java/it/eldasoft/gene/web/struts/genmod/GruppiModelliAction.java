/*
 * Created on 09-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

public class GruppiModelliAction extends AbstractDispatchActionBaseGenModelli {

  /** Logger per l'azione * */
  Logger                 logger = Logger.getLogger(GruppiModelliAction.class);

  /** Manager dei modelli */
  private ModelliManager modelliManager;

  /**
   * @param modelliManager
   *        The modelliManager to set.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * listaGruppiModello
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniListaGruppiModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.GESTIONE_COMPLETA_GENMOD);
  }

  /**
   * Azione per la visualizzazione della lista dei gruppi
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward listaGruppiModello(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // è un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    if (this.bloccaGestioneGruppiDisabilitata(request))
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);

    int idModello = -1;
    String messageKey = null;
    if (logger.isDebugEnabled())
      logger.debug("listaGruppiModello: inizio metodo");
    // Di default setto la visualizzazione della lista dei gruppi
    String target = CostantiGenModelli.FORWARD_OK_LISTA_GRUPPI;
    try {
      idModello = Integer.parseInt(request.getParameter("idModello"));
      // Setto i dati del modello
      request.setAttribute("listaGruppiModello",
          modelliManager.getGruppiModello(idModello));
      // Settaggio dell'identificativo del modello
      request.setAttribute("idModello", new Integer(idModello));
      // Setto i dati per la gestione dei tab
      ModelliAction.setMenuTab(request, true);
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
    if (logger.isDebugEnabled())
      logger.debug("listaGruppiModello: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * modificaGruppiModello
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModificaGruppiModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.GESTIONE_COMPLETA_GENMOD);
  }

  /**
   * Funzione che chiama la maschera con la lista di tutti i gruppi e
   * associazione degli stessi
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward modificaGruppiModello(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // è un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    if (this.bloccaGestioneGruppiDisabilitata(request))
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);

    // Di default setto la visualizzazione della lista dei gruppi
    String target = CostantiGenModelli.FORWARD_OK_MODIFICA_LISTA_GRUPPI;
    int idModello = -1;
    String messageKey = null;
    if (logger.isDebugEnabled())
      logger.debug("modificaGruppiModello: inizio metodo");
    try {
      idModello = Integer.parseInt(request.getParameter("idModello"));
      // Setto i dati del modello
      request.setAttribute("listaGruppiModello",
          modelliManager.getGruppiModelloPerModifica(idModello,
              (String) request.getSession().getAttribute(
                  CostantiGenerali.PROFILO_ATTIVO)));

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // Settaggio dell'identificativo del modello
      request.setAttribute("idModello", new Integer(idModello));
      // Setto i dati per la gestione dei tab
      ModelliAction.setMenuTab(request, false);
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
    if (logger.isDebugEnabled())
      logger.debug("modificaGruppiModello: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * updateGruppiModello
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniUpdateGruppiModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.GESTIONE_COMPLETA_GENMOD);
  }

  public ActionForward updateGruppiModello(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // è un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    if (this.bloccaGestioneGruppiDisabilitata(request))
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);

    String target = CostantiGenModelli.FORWARD_OK_UPDATE_LISTA_GRUPPI;
    int idModello = -1;
    String messageKey = null;
    if (logger.isDebugEnabled())
      logger.debug("updateGruppiModello: inizio metodo");
    try {

      // Copio tutti gli id
      GruppiModelliForm formGruppi = (GruppiModelliForm) form;
      idModello = Integer.parseInt(request.getParameter("idModello"));
      // F.D. 24/04/07 se è stato associato almeno un gruppo il modello non è
      // più personale!
      if (formGruppi.getGruppiAssociati() != null)
        modelliManager.updateGruppiEPubblicaModello(idModello,
            formGruppi.getGruppiAssociati(), (String)
            request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));
      else
        modelliManager.updateGruppiModello(idModello,
            formGruppi.getGruppiAssociati(), (String)
            request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));
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
    if (logger.isDebugEnabled())
      logger.debug("updateGruppiModello: fine metodo");
    return mapping.findForward(target);
  }

}
