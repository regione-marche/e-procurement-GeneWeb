/*
 * Created on 17-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.admin.ProfiliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action che controlla l'operazione di apertura della pagina Lista Gruppi
 * associati ad un profilo
 * 
 * @author Luca.Giacomazzo
 */
public class ListaGruppiProfiloAction extends AbstractDispatchActionBaseAdmin {

  // Forward predefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private static final String FORWARD_VISUALIZZA = "successVisualizzaLista";

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ListaGruppiProfiloAction.class);

  /**
   * Reference alla classe di business logic per il popolamento delle comboBox
   * presenti nella pagina
   */
  private ProfiliManager      profiliManager;

  /**
   * @param accountManager
   *        accountManager da settare internamente alla classe.
   */
  public void setProfiliManager(ProfiliManager profiliManager) {
    this.profiliManager = profiliManager;
  }

  /**
   * Funzione che da l'opzione per accedere alla lista
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVisualizza() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_LETTURA_ADMIN);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward visualizza(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // è un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    if (this.bloccaGestioneGruppiDisabilitata(request))
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
    
    if (logger.isDebugEnabled()) logger.debug("visualizza: inizio metodo");

    String target = ListaGruppiProfiloAction.FORWARD_VISUALIZZA;
    String messageKey = null;
    
    try {
      String codApp = (String)
            request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
      
      // lettura dal request del idAccount
      String codProfilo = request.getParameter("codPro");
      
      // leggo la lista degli utenti associati al profilo
      List<?> listaGruppiProfilo = this.profiliManager.getGruppiProfiloByCodApp(
              codProfilo, codApp);
     
      // set nel request del beanForm contenente la lista dei gruppi associati
      // al profilo in analisi
      request.setAttribute("listaGruppiForm", listaGruppiProfilo);

      request.setAttribute("id", codProfilo);

      // update del menu a tab
      this.setMenuTab(request);

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

    if (logger.isDebugEnabled()) logger.debug("visualizza: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab
   * 
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioProfilo.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioProfilo.GRUPPI);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioProfilo.DETTAGLIO,
          CostantiDettaglioProfilo.UTENTI,
          CostantiDettaglioProfilo.GRUPPI});
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioProfilo.GRUPPI);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioProfilo.DETTAGLIO,
          CostantiDettaglioProfilo.UTENTI,
          CostantiDettaglioProfilo.GRUPPI});
      sessione.setAttribute(CostantiDettaglioProfilo.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }

}