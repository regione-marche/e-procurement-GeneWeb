/*
 * Created on 20 - Feb - 2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.system.LdapManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.IdForm;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.admin.AccountLdap;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;

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
 * Action di gestione dell'account: crea un nuovo Account (form), carica il form
 * dell'Account, elimina un Account ed elimina gli Account selezionati
 *
 * @author cit_defilippis
 */
public class CreaAccountLdapAction extends AbstractActionBaseAdmin {

  static Logger         logger = Logger.getLogger(CreaAccountLdapAction.class);

  protected LdapManager ldapManager;

  protected TabellatiManager tabellatiManager;

  /**
   * @param ldapManager
   *        The ldapManager to set.
   */
  public void setLdapManager(LdapManager ldapManager) {
    this.ldapManager = ldapManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action nuovo
   *
   * @return opzioni per accedere alla action
   */
  @Override
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  public ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("nuovoLdap: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;
    IdForm idForm = (IdForm) form;
    String dn = idForm.getId();

    HttpSession sessione = request.getSession();
    String metodo = request.getParameter("metodo");

    try {

      AccountLdap accountLdap = ldapManager.getAccountLdapByDn(dn );

      Account account = new Account(accountLdap);
      AccountForm formUtente = new AccountForm(account);

      request.setAttribute("accountForm", formUtente);
      request.setAttribute("metodo", metodo);

      formUtente.setAbilitazioneLavori(CostantiGeneraliAccount.DEFAULT_ABILITAZIONE_LAVORI);
      formUtente.setLivelloLavori(CostantiGeneraliAccount.DEFAULT_LIVELLO_LAVORI);
      formUtente.setAbilitazioneContratti(CostantiGeneraliAccount.DEFAULT_ABILITAZIONE_CONTRATTI);
      formUtente.setLivelloContratti(CostantiGeneraliAccount.DEFAULT_LIVELLO_CONTRATTI);
      formUtente.setAbilitazioneGare(CostantiGeneraliAccount.DEFAULT_ABILITAZIONE_GARE);
      formUtente.setLivelloGare(CostantiGeneraliAccount.DEFAULT_LIVELLO_GARE);
      formUtente.setAbilitazioneAP(CostantiGeneraliAccount.DEFAULT_ABILITAZIONE_AP);

      formUtente.setOpzioniUtente(new OpzioniUtente(CostantiGeneraliAccount.OPZIONI_MENU_STRUMENTI));
      formUtente.setOpzioniUtenteSys(formUtente.getOpzioniUtente().getElencoOpzioni());

      List<Tabellato> listaUffAppartenenza = this.tabellatiManager.getTabellato(TabellatiManager.UFF_APPARTENENZA);
      List<Tabellato> listaRuoliME = this.tabellatiManager.getTabellato(TabellatiManager.RUOLO_ME);
      List<Tabellato> listaCategorie = this.tabellatiManager.getTabellato(TabellatiManager.CATEGORIE_UTENTE);
      request.setAttribute("listaUffAppartenenza", listaUffAppartenenza);
      request.setAttribute("listaRuoliME", listaRuoliME);
      request.setAttribute("listaCategorie", listaCategorie);

      sessione.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION, "");

      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);
      this.setMenuTabEdit(request);
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
    if (logger.isDebugEnabled()) logger.debug("nuovoLdap: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action elimina
   *
   * @return opzioni per accedere alla action
   *
   * public CheckOpzioniUtente getOpzioniElimina() { return new
   * CheckOpzioniUtente( CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN); }
   *
   * public ActionForward elimina(ActionMapping mapping, ActionForm form,
   * HttpServletRequest request, HttpServletResponse response) throws
   * IOException, ServletException { if (logger.isDebugEnabled())
   * logger.debug("elimina: inizio metodo"); String target =
   * FORWARD_SUCCESS_ELIMINA; String messageKey = null;
   *
   * String id = request.getParameter("idAccount"); Integer i =
   * Integer.valueOf(id); try { this.accountManager.deleteAccount(i);
   * request.setAttribute("metodo", "visualizzaLista"); } catch
   * (DataAccessException e) { target =
   * CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE; messageKey =
   * "errors.database.dataAccessException";
   * logger.error(this.resBundleGenerale.getString(messageKey), e);
   * this.aggiungiMessaggio(request, messageKey); } catch (Throwable t) { target =
   * CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE; messageKey =
   * "errors.applicazione.inaspettataException";
   * logger.error(this.resBundleGenerale.getString(messageKey), t);
   * this.aggiungiMessaggio(request, messageKey); } if (logger.isDebugEnabled())
   * logger.debug("elimina: fine metodo"); return mapping.findForward(target); }
   *
   * /** Funzione che restituisce le opzioni per accedere alla action
   * eliminaSelez
   *
   * @return opzioni per accedere alla action
   *
   * public CheckOpzioniUtente getOpzioniEliminaSelez() { return new
   * CheckOpzioniUtente( CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN); }
   *
   * public ActionForward eliminaSelez(ActionMapping mapping, ActionForm form,
   * HttpServletRequest request, HttpServletResponse response) throws
   * IOException, ServletException {
   *
   * if (logger.isDebugEnabled()) logger.debug("eliminaSelez: inizio metodo");
   * String target = FORWARD_SUCCESS_ELIMINA; String messageKey = null;
   *
   * ListaForm listaIdAccount = (ListaForm) form;
   *
   * //String keys[] = request.getParameter("keys").split("-"); try { for(int
   * i=0; i < listaIdAccount.getId().length; i++){
   * this.accountManager.deleteAccount(new Integer(listaIdAccount.getId()[i])); }
   * /*for (int i = 1; i < keys.length; i++) { String s = keys[i];
   * this.accountManager.deleteAccount(new Integer(s)); }
   */
  /*
   * } catch (DataAccessException e) { target =
   * CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE; messageKey =
   * "errors.database.dataAccessException";
   * logger.error(this.resBundleGenerale.getString(messageKey), e);
   * this.aggiungiMessaggio(request, messageKey); } catch (Throwable t) { target =
   * CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE; messageKey =
   * "errors.applicazione.inaspettataException";
   * logger.error(this.resBundleGenerale.getString(messageKey), t);
   * this.aggiungiMessaggio(request, messageKey); } if (logger.isDebugEnabled())
   * logger.debug("eliminaSelez: fine metodo"); return
   * mapping.findForward(target); }
   */

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab
   *
   * @param request
   */
  protected void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.DETTAGLIO);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioAccount.DETTAGLIO, CostantiDettaglioAccount.GRUPPI });
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.DETTAGLIO);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioAccount.DETTAGLIO, CostantiDettaglioAccount.GRUPPI });
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
  protected void setMenuTabEdit(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.DETTAGLIO);
      gestoreTab.setTabSelezionabili(null);
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.DETTAGLIO);
      gestoreTab.setTabSelezionabili(null);
      sessione.setAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }

}