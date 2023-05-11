/*
 * Created on 23-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.web.struts.schedric;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.genric.TrovaRicerche;
import it.eldasoft.utils.profiles.OpzioniUtente;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Inizializzazione della pagina TrovaSchedulazioni
 *
 * @author Francesco De Filippis
 */
public class InitTrovaSchedRicAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(InitTrovaSchedRicAction.class);

  /**
   * Reference alla classe di business logic per la gestione delle ricerche
   */
  private RicercheManager ricercheManager;

  /**
   * Reference alla classe di business logic per la gestione dei tabellati
   */
  private TabellatiManager tabellatiManager;

  /**
   * Reference alla classe di business logic per la gestione degli account
   */
  private AccountManager    accountManager;

  /**
   * @param accountManager The accountManager to set.
   */
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  /**
   * @param tabellatiManager The tabellatiManager to set.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    this.cleanSession(request);
    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    try {

      // Caricamento degli oggetti per popolare le comboBox presenti nella
      // pagina Lista per popolamento comboBox 'Tipo schedulazione'
      List<Tabellato> listaTipoSched = this.tabellatiManager.getTabellato(CostantiSchedRic.TABELLATO_TIPO_SCHEDRIC);

      //se l'utente connesso ha la gestione completa delle ricerche allora la lista contiene tutti i report
      //se invece ha limitazioni vedrà solamente le ricerche predefinite legate alla sua utenza
      ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
      "profiloUtente");
      OpzioniUtente opzioniUtente = new OpzioniUtente(
      profiloUtente.getFunzioniUtenteAbilitate());
      List<?> listaRicerche = null;

      if (opzioniUtente.isOpzionePresente(
            CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_SCHEDULAZIONI)) {
        //Lista ricerche: carico tutte le ricerche passando come parametro alla
        //funzione un oggetto TrovaRicerche vuoto in modo da non imporre filtri
        TrovaRicerche filtroRicerca = new TrovaRicerche();
        filtroRicerca.setCodiceApplicazione((String)
            request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
        filtroRicerca.setProfiloOwner((String)
            request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));
        listaRicerche = this.ricercheManager.getRicercheSenzaParametri(filtroRicerca,true);

        // Lista per popolamento comboBox 'Utente creatore'
        List<Account> listaUtenti = this.accountManager.getListaAccountByCodProCodApp(
            (String) request.getSession().getAttribute(
                CostantiGenerali.MODULO_ATTIVO),
          (String) request.getSession().getAttribute(
                CostantiGenerali.PROFILO_ATTIVO));

        request.setAttribute("listaUtenti", listaUtenti);
      } else {
        listaRicerche = this.ricercheManager.getRicerchePredefiniteSenzaParametri(
            profiloUtente.getId(), (String)
            request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO),
            (String)
            request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO),
            true);
      }

      // lista per il popolamento della comboBox 'Risultati per Pagina'
      String[] listaRisPerPagina = CostantiGenerali.CBX_RIS_PER_PAGINA;

      // Set nel request delle liste per il popolamento delle varie combobox
      request.setAttribute("listaTipoSched", listaTipoSched);
      request.setAttribute("listaRicerche", listaRicerche);

      request.setAttribute("listaRisPerPagina", listaRisPerPagina);
      // lista per il popolamento della comboBox dei valori della combobox di
      // confronto fra stringhe
      request.setAttribute("listaValueConfrontoStringa",
          GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA);
      // lista per il popolamento della comboBox dei testi della combobox di
      // confronto fra stringhe
      request.setAttribute("listaTextConfrontoStringa",
          GestioneOperatoreConfrontoStringa.CBX_TESTO_CONFRONTO_STRINGA);

      if(request.getSession().getAttribute(CostantiSchedRic.TROVA_SCHEDRIC) != null) {
        request.setAttribute("trovaSchedRicForm",
            request.getSession().getAttribute(
                CostantiSchedRic.TROVA_SCHEDRIC));
      } else {
        TrovaSchedRicForm trovaSchedRicForm = new TrovaSchedRicForm();
        trovaSchedRicForm.setNoCaseSensitive(Boolean.TRUE.toString());
        request.setAttribute("trovaSchedRicForm", trovaSchedRicForm);
      }

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

  /**
   * @param ricercheManager The ricercheManager to set.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

}