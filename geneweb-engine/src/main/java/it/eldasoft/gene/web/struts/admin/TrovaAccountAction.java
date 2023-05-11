/*
 * Created on 30/mar/09
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
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action per la gestione dell'estrazione della lista degli account
 *
 * @author Stefano.Sabbadin
 */
public class TrovaAccountAction extends AbstractDispatchActionBaseAdmin {

  /** Logger Log4J di classe */
  static Logger          logger = Logger.getLogger(TrovaAccountAction.class);

  /** Manager per la gestione della business logic per l'account */
  private AccountManager accountManager;

  /** Manager per la gestione di dati tabellati. */
  protected TabellatiManager tabellatiManager;

  /**
   * @param accountManager
   *        accountManager da settare internamente alla classe.
   */
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  public CheckOpzioniUtente getOpzioniTrovaAccount() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_LETTURA_ADMIN);
  }

  /**
   * Estrae l'elenco degli utenti che soddisfano i criteri di ricerca
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward trovaAccount(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("trovaAccount: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    TrovaAccountForm trovaAccountForm = (TrovaAccountForm) form;
    
    try {

      // Nel caso in cui si deve ritornare alla pagina Lista il form
      // contiene un oggetto di tipo ActionForm con tutti gli argomenti pari al
      // default anche se la lista e' filtrata per qualche parametro.
      if (trovaAccountForm.getDescrizione() == null
          && trovaAccountForm.getNome() == null
          && trovaAccountForm.getUtenteDisabilitato() == null
          && trovaAccountForm.getUtenteLDAP() == null
          && "20".equals(trovaAccountForm.getRisPerPagina())
          && !(Boolean.valueOf(trovaAccountForm.getNoCaseSensitive()))
          && trovaAccountForm.getUfficioAppartenenza() == null
          && trovaAccountForm.getCategoria() == null 
          && trovaAccountForm.getCodiceFiscale() == null
          && trovaAccountForm.geteMail() == null 
          && trovaAccountForm.getUffint() == null
          && trovaAccountForm.getAmministratore() == null
          && trovaAccountForm.getGestioneUtenti() == null){
        
        // l'azione è stata richiamata per ripetere l'ultima ricerca effettuata,
        // quindi i dati relativi al form sono da leggere dalla sessione
        trovaAccountForm = (TrovaAccountForm) request.getSession().getAttribute(
            CostantiDettaglioAccount.TROVA_ACCOUNT);
        // alla prima esecuzione, se non vengono impostati filtri si entra
        // comunque qui, e si rischia di aggiornare trovaAccountForm con un
        // reference ad un bean che non esiste ancora in sessione
        if (trovaAccountForm == null) {
          trovaAccountForm = new TrovaAccountForm();
          trovaAccountForm.setNoCaseSensitive(Boolean.TRUE.toString());
        }
      }

      request.getSession().setAttribute(CostantiDettaglioAccount.TROVA_ACCOUNT,
          trovaAccountForm);

      List<Tabellato> listaUffAppartenenza = this.tabellatiManager.getTabellato(TabellatiManager.UFF_APPARTENENZA);
      Map<Integer, Tabellato> hashUffAppartenenza = new TreeMap<Integer, Tabellato>();
      for (Tabellato t: listaUffAppartenenza) {
        hashUffAppartenenza.put(Integer.parseInt(t.getTipoTabellato()), t);
      }
      request.setAttribute("hashUffAppartenenza", hashUffAppartenenza);

      List<Tabellato> listaCategorie = this.tabellatiManager.getTabellato(TabellatiManager.CATEGORIE_UTENTE);
      request.setAttribute("listaCategorie", listaCategorie);
      
       
      List<Account> listaAccount = this.accountManager.getListaAccount(trovaAccountForm.getDatiPerModel());
      if (listaAccount != null
          && !listaAccount.isEmpty()
          && listaAccount.size() > 0) {
        
        // Set nel request della lista di ricerche
        request.setAttribute("listaAccount", listaAccount);
        request.setAttribute("risultatiPerPagina",
            "Tutti".equals(trovaAccountForm.getRisPerPagina())
                ? null
                : trovaAccountForm.getRisPerPagina());

      } else {
        target = CostantiGeneraliStruts.FORWARD_NESSUN_RECORD_TROVATO;
        messageKey = "info.search.noRecordFound";
        this.aggiungiMessaggio(request, messageKey);
      }

    } catch (SqlComposerException sc) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = sc.getChiaveResourceBundle();
      logger.error(this.resBundleGenerale.getString(messageKey), sc);
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
    if (logger.isDebugEnabled()) logger.debug("trovaAccount: fine metodo");
    return mapping.findForward(target);
  }

  public CheckOpzioniUtente getOpzioniNuovaRicerca() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_LETTURA_ADMIN);
  }

  /**
   * Svuota dalla sessione i criteri dell'ultima selezione di account
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward nuovaRicerca(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("nuovaRicerca: inizio metodo");

    String target = "successNuovaRicerca";

    TrovaAccountForm trovaAccountForm = new TrovaAccountForm();
    trovaAccountForm.setNoCaseSensitive(Boolean.TRUE.toString());
    request.getSession().setAttribute(CostantiDettaglioAccount.TROVA_ACCOUNT,
        trovaAccountForm);

    if (logger.isDebugEnabled()) logger.debug("nuovaRicerca: fine metodo");
    return mapping.findForward(target);
  }

}
