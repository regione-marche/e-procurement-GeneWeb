/*
 * Created on 12-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.admin.ProfiliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.admin.ProfiloAccount;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
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
 * Action che controlla l'operazione di apertura della pagina Lista Profili
 * a cui l'utente in analisi e' associato
 *
 * @author Luca.Giacomazzo
 */
public class ListaProfiliAccountAction extends AbstractDispatchActionBaseAdmin {

  // Forward predefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private static final String FORWARD_VISUALIZZA = "successVisualizzaLista";
  private static final String FORWARD_EDIT       = "successEditLista";

  /** Logger Log4J di classe */
  static Logger               logger             = Logger.getLogger(ListaProfiliAccountAction.class);

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  protected ResourceBundle    resBundleGenerale  = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  private AccountManager      accountManager;
  private ProfiliManager      profiliManager;

  /**
   * @param accountManager accountManager da settare internamente alla classe.
   */
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  /**
   *
   * @param Ritorna profiliManager
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

    // target di default da modificare nel momento in cui si verificano dei
    // problemi
    String target = ListaProfiliAccountAction.FORWARD_EDIT;
    String messageKey = null;

    String codApp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

    try {
      // lettura dal request del idAccount
      Integer idAccount = null;
      if(request.getParameter("idAccount") != null)
        idAccount = new Integer(request.getParameter("idAccount"));
      else
         idAccount = (Integer) request.getAttribute("idAccount");

      Account account = this.accountManager.getAccountById(idAccount);
      request.setAttribute("nascondiUffint", new OpzioniUtente(account.getOpzioniUtente()).isOpzionePresente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA));

      // lista dei profili a cui l'utente in analisi e' associato
      List<ProfiloAccount> listaProfiliUtente =
          this.profiliManager.getProfiliConAssociazioneUtenteByCodApp(
              idAccount.intValue(), codApp);

      // preparo la lista da passare al form
      ProfiliUtenteForm profiliUtente = new ProfiliUtenteForm();
      Vector<String> tmp = new Vector<String>();
      for (int i = 0; i < listaProfiliUtente.size(); i++)
        if ((listaProfiliUtente.get(i)).getAssociato())
          tmp.add(""
              + (listaProfiliUtente.get(i)).getCodiceProfilo());

      profiliUtente.setCodiceProfilo(tmp.toArray(new String[0]));

      // set nel request della lista di tutti i profili e lo stato di
      // associazione con l'account in analisi
      request.setAttribute("listaProfiliUtente", listaProfiliUtente);

      // set nel request della stringa necessaria al form di modifica
      // associazione profili-utente
      request.setAttribute("profiliUtenteForm", profiliUtente);

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // update del menu a tab
      this.setMenuTabEdit(request);

      // set nel request dell'idAccount dell'account in analisi
      request.setAttribute("idAccount", idAccount);

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
   * Metodo per apertura in visualizzazione la lista dei profili a cui l'utente
   * in analisi e' associato
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward visualizza(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("visualizzaLista: inizio metodo");

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = ListaProfiliAccountAction.FORWARD_VISUALIZZA;
    String messageKey = null;

	String codApp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

    try {
      // lettura dal request del idAccount
      int idAccount = Integer.parseInt(request.getParameter("idAccount"));

      Account account = this.accountManager.getAccountById(idAccount);
      request.setAttribute("nascondiUffint", new OpzioniUtente(account.getOpzioniUtente()).isOpzionePresente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA));

      // leggo la lista dei gruppi associati all'account
      List<?> listaProfiliUtente =
          this.profiliManager.getProfiliAccountByCodApp(idAccount, codApp);

      // set nel request del beanForm contenente la lista dei gruppi con le
      // funzionalitą
      request.setAttribute("listaProfiliForm", listaProfiliUtente);

      request.setAttribute("idAccount", "" + idAccount);

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

    if (logger.isDebugEnabled()) {
      logger.debug("visualizzaLista: fine metodo");
    }

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
    Object obj = sessione.getAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.PROFILI);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioAccount.DETTAGLIO,
          CostantiDettaglioAccount.PROFILI,
          CostantiDettaglioAccount.GRUPPI,
          CostantiDettaglioAccount.UFFICI_INTESTATARI,
          CostantiDettaglioAccount.TECNICI});
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.PROFILI);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioAccount.DETTAGLIO,
          CostantiDettaglioAccount.PROFILI,
          CostantiDettaglioAccount.GRUPPI,
          CostantiDettaglioAccount.UFFICI_INTESTATARI,
          CostantiDettaglioAccount.TECNICI});
      sessione.setAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB,
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
    Object obj = sessione.getAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.PROFILI);
      gestoreTab.setTabSelezionabili(null);
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.PROFILI);
      gestoreTab.setTabSelezionabili(null);
      sessione.setAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }

}