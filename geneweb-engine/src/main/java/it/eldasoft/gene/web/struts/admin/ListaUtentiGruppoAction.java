/*
 * Created on 14-lug-2006
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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.admin.AccountGruppo;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.sicurezza.CriptazioneException;

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
 * Action che controlla l'operazione di apertura della pagina Lista Utenti di
 * Gruppo
 * 
 * @author Luca.Giacomazzo
 */
public class ListaUtentiGruppoAction extends AbstractDispatchActionBaseAdmin {

  // Forward predefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private static final String FORWARD_VISUALIZZA = "successVisualizzaLista";
  private static final String FORWARD_EDIT       = "successEditLista";

  /** Logger Log4J di classe */
  static Logger               logger             = Logger.getLogger(ListaUtentiGruppoAction.class);

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  protected ResourceBundle    resBundleGenerale  = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /**
   * Reference alla classe di business logic per il popolamento delle comboBox
   * presenti nella pagina
   */
  private AccountManager      accountManager;

  /**
   * @return Ritorna accountManager.
   */
  public AccountManager getAccountManager() {
    return this.accountManager;
  }

  /**
   * @param accountManager
   *        accountManager da settare internamente alla classe.
   */
  public void setAccountManager(AccountManager utentiManager) {
    this.accountManager = utentiManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action editLista
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEditLista() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }
  
  public ActionForward editLista(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // è un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    if (this.bloccaGestioneGruppiDisabilitata(request))
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);

    if (logger.isDebugEnabled()) {
      logger.debug("editLista: inizio metodo");
    }

    // target di default da modificare nel momento in cui si verificano dei
    // problemi
    // per l'azione 'salvaGruppoConFunzionalita'
    String target = ListaUtentiGruppoAction.FORWARD_EDIT;
    String messageKey = null;

    try {
      // Lettura dal request l'ID_GRUPPO del gruppo in analisi
      int idGruppo = Integer.parseInt((String) request.getParameter("idGruppo"));

      // lista degli utenti con attributo 'associato' di tipo boolean
      // valorizzato a true se l'utente è associato al gruppo in analisi e a 
      // false altrimenti. La lista è ordinata per nome dell'utente
      List<Account> listaUtentiAssociatiGruppo = 
          accountManager.getAccountConAssociazioneGruppo(idGruppo,
              (String)request.getSession().getAttribute(
                  CostantiGenerali.PROFILO_ATTIVO));

      UtentiGruppoForm utentiGruppo = new UtentiGruppoForm();
      Vector<String> tmp = new Vector<String>();
      for (int i = 0; i < listaUtentiAssociatiGruppo.size(); i++)
        if (((AccountGruppo) listaUtentiAssociatiGruppo.get(i)).getAssociato())
          tmp.add(""
              + ((AccountGruppo) listaUtentiAssociatiGruppo.get(i)).getIdAccount());

      utentiGruppo.setIdAccount((String[]) tmp.toArray(new String[0]));

      // set nel request della lista di tutti gli utenti e lo stato di
      // associazione con il gruppo in analisi
      request.setAttribute("listaUtentiAssociatiGruppo",
          listaUtentiAssociatiGruppo);

      // set nel request della stringa necessaria al form di modifica
      // associazione utenti-gruppo
      request.setAttribute("utentiGruppoForm", utentiGruppo);

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // update del menu a tab
      this.setMenuTabEdit(request);

      // set nel request dell'idGruppo del Gruppo in analisi
      request.setAttribute("idGruppo", new Integer(idGruppo));
    } catch (CriptazioneException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = e.getChiaveResourceBundle();
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

    if (logger.isDebugEnabled()) {
      logger.debug("editLista: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action visualizzaLista
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVisualizzaLista() {
    //passo l'ou11 dato che se c'è posso sicuramente visualizzare
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_LETTURA_ADMIN);
  }
  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */

  public ActionForward visualizzaLista(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // è un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    if (this.bloccaGestioneGruppiDisabilitata(request))
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);

    if (logger.isDebugEnabled()) {
      logger.debug("visualizzaLista: inizio metodo");
    }

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = ListaUtentiGruppoAction.FORWARD_VISUALIZZA;
    String messageKey = null;

    try {
      // lettura dal request del idGruppo
      int idGruppo = Integer.parseInt(request.getParameter("idGruppo"));

      // Determinazione della lista degli utenti associati al gruppo in analisi
      List<AccountGruppo> listaUtentiGruppo = this.accountManager.getAccountDiGruppo(idGruppo,
          (String)request.getSession().getAttribute(
              CostantiGenerali.PROFILO_ATTIVO));

      // set nel request del beanForm contenente la lista dei gruppi con le
      // funzionalità
      request.setAttribute("listaUtentiGruppo", listaUtentiGruppo);

      // set nel request dell'ID_GRUPPO del gruppo in analisi per la creazione
      // dei link nel TAB menù
      request.setAttribute("idGruppo", "" + idGruppo);

      // update del menu a tab
      this.setMenuTab(request);

    } catch (CriptazioneException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = e.getChiaveResourceBundle();
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

    if (logger.isDebugEnabled()) {
      logger.debug("visualizzaLista: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione della lista utenti di gruppo
   * 
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.UTENTI);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioGruppo.DETTAGLIO, CostantiDettaglioGruppo.RICERCHE,
          CostantiDettaglioGruppo.MODELLI });
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.UTENTI);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioGruppo.DETTAGLIO, CostantiDettaglioGruppo.RICERCHE,
          CostantiDettaglioGruppo.MODELLI });
      sessione.setAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB,
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
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.UTENTI);
      gestoreTab.setTabSelezionabili(null);
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.UTENTI);
      gestoreTab.setTabSelezionabili(null);
      sessione.setAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }
}