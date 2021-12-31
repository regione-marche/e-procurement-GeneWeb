/*
 * Created on 02-ott-2007
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
import it.eldasoft.gene.db.domain.admin.AccountProfilo;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
 * Action che controlla l'operazione di apertura della pagina Lista Utenti
 * associati ad un profilo
 *
 * @author Luca.Giacomazzo
 */
public class ListaAccountProfiloAction extends AbstractDispatchActionBaseAdmin {

  // Forward predefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private static final String FORWARD_VISUALIZZA = "successVisualizzaLista";
  private static final String FORWARD_EDIT       = "successEditLista";

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ListaAccountProfiloAction.class);

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
   * Funzione che da l'opzione per la modifica della lista
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEditLista() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  public ActionForward editLista(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("editLista: inizio metodo");

    String target = ListaAccountProfiloAction.FORWARD_EDIT;
    String messageKey = null;

    try {
      // lettura dal request del idAccount
      String codiceProfilo = request.getParameter("codPro");
      if(codiceProfilo == null){
        codiceProfilo = (String) request.getAttribute("codPro");
      }

      // lista degli account con attributo 'associato' di tipo boolean
      // valorizzato a true se l'account e' associato al profilo in analisi e a
      // false altrimenti. La lista è ordinata per nome dell'account
      List<AccountProfilo> listaUtentiProfilo = this.profiliManager.getUtentiConAssociazioneProfiloByCodApp(
          codiceProfilo, (String) request.getSession().getAttribute(
              CostantiGenerali.MODULO_ATTIVO));

      // preparo la lista da passare al form
      UtentiProfiloForm accountProfilo = new UtentiProfiloForm();
      Vector<String> tmp = new Vector<String>();
      for (int i = 0; i < listaUtentiProfilo.size(); i++)
        if (((AccountProfilo) listaUtentiProfilo.get(i)).getAssociato())
          tmp.add(""
              + ((AccountProfilo) listaUtentiProfilo.get(i)).getIdAccount());

      accountProfilo.setIdAccount((String[]) tmp.toArray(new String[0]));
      //gruppoAccount.setIdGruppo((String[]) tmp.toArray(new String[0]));

      // set nel request della lista di tutti gli utenti e lo stato di
      // associazione con il profilo in analisi
      request.setAttribute("listaAccountProfilo", listaUtentiProfilo);

      // set nel request della stringa necessaria al form di modifica
      // associazione utenti-profilo
      request.setAttribute("utentiProfiloForm", accountProfilo);

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // update del menu a tab
      this.setMenuTabEdit(request);

      // set nel request dell'idAccount dell'account in analisi
      request.setAttribute("codPro", codiceProfilo);

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

    if (logger.isDebugEnabled()) {
      logger.debug("editLista: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Funzione che da l'opzione per la modifica della lista
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

    if (logger.isDebugEnabled()) logger.debug("visualizza: inizio metodo");

    String target = ListaAccountProfiloAction.FORWARD_VISUALIZZA;
    String messageKey = null;

    try {
      String codApp = (String)
            request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);

      // lettura dal request del idAccount
      String codProfilo = request.getParameter("codPro");

      // leggo la lista degli utenti associati al profilo
      List<?> listaUtentiProfilo = this.profiliManager.getUtentiProfiloByCodApp(
              codProfilo, codApp);

      // Per ciascun elemento della lista bisogna decriptare la login
      Iterator<?> iter = listaUtentiProfilo.iterator();
      while(iter.hasNext()){
        AccountProfilo utenteProfilo = (AccountProfilo) iter.next();
        utenteProfilo.setAssociato(true);
      }

      // set nel request del beanForm contenente la lista degli utenti con le
      // funzionalità
      request.setAttribute("listaUtentiForm", listaUtentiProfilo);

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
      gestoreTab.setTabAttivo(CostantiDettaglioProfilo.UTENTI);
        gestoreTab.setTabSelezionabili(new String[] {
            CostantiDettaglioProfilo.DETTAGLIO,
            CostantiDettaglioProfilo.UTENTI,
            CostantiDettaglioProfilo.GRUPPI});
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioProfilo.UTENTI);
        gestoreTab.setTabSelezionabili(new String[] {
            CostantiDettaglioProfilo.DETTAGLIO,
            CostantiDettaglioProfilo.UTENTI,
            CostantiDettaglioProfilo.REPORT,
            CostantiDettaglioProfilo.MODELLI});
      sessione.setAttribute(CostantiDettaglioProfilo.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di editing della lista utenti di gruppo
   *
   * @param request
   */
  private void setMenuTabEdit(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioProfilo.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioProfilo.UTENTI);
      gestoreTab.setTabSelezionabili(null);
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioProfilo.UTENTI);
      gestoreTab.setTabSelezionabili(null);
      sessione.setAttribute(CostantiDettaglioProfilo.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }

}