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
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;

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
 * Inizializzazione della pagina TrovaRicerche, con verifica della presenza in
 * sessione dell'oggetto TrovaRicercheForm. Se non trova nessuna istanza di tale
 * classe allora la crea con gli attributi a 'null'. Se trova l'oggetto, allora
 * ne copia gli attributi in un form opportuno, inserendolo nel request.
 *
 * @author Luca Giacomazzo
 */
public class InitTrovaModelliAction extends AbstractActionBaseGenModelli {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(InitTrovaModelliAction.class);

  /**
   * Reference alla classe di business logic per la gestione degli account
   */
  private AccountManager   accountManager;

  /**
   * Reference alla classe di business logic per
   */
  private TabellatiManager tabellatiManager;

  /**
   * Reference alla classe di business logic per
   */
  private GruppiManager    gruppiManager;

  /**
   * @return Ritorna tabellatiManager.
   */
  public TabellatiManager getTabellatiManager() {
    return tabellatiManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @return Ritorna gruppiManager.
   */
  public GruppiManager getGruppiManager() {
    return gruppiManager;
  }

  /**
   * @param gruppiManager
   *        gruppiManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }

  /**
   * @return Ritorna accountManager.
   */
  public AccountManager getAccountManager() {
    return accountManager;
  }

  /**
   * @param accountManager
   *        accountManager da settare internamente alla classe.
   */
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // cancellazione di oggetti in sessione precedentemente creati
    this.cleanSession(request);

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    try {
      String codiceProfilo = (String) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_ATTIVO);
      // Caricamento degli oggetti per popolare le comboBox presenti nella
      // pagina Lista per popolamento comboBox 'Tipo Modello'
      List listaTipoModello = this.tabellatiManager.getTabellato(TabellatiManager.TIPO_MODELLI);

      // Lista per popolamento comboBox 'Gruppo'
      List listaGruppi = this.gruppiManager.getGruppiOrderByNome(codiceProfilo);

      // Lista per popolamento comboBox 'Utente creatore'
      List listaUtenti = this.accountManager.getListaAccountByCodProCodApp(
          (String) request.getSession().getAttribute(
              CostantiGenerali.MODULO_ATTIVO), codiceProfilo);

      // Set nel request delle liste per il popolamento delle varie combobox
      request.setAttribute("listaTipoModello", listaTipoModello);
      request.setAttribute("listaGruppi", listaGruppi);
      request.setAttribute("listaUtenti", listaUtenti);
      // lista per il popolamento della comboBox 'Risultati per Pagina'
      request.setAttribute("listaRisPerPagina", CostantiGenerali.CBX_RIS_PER_PAGINA);
      // lista per il popolamento della comboBox dei valori della combobox di
      // confronto fra stringhe
      request.setAttribute("listaValueConfrontoStringa",
          GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA);
      // lista per il popolamento della comboBox dei testi della combobox di
      // confronto fra stringhe
      request.setAttribute("listaTextConfrontoStringa",
          GestioneOperatoreConfrontoStringa.CBX_TESTO_CONFRONTO_STRINGA);

      // Se c'è in sezione un inizializzazione di trova modelli la ricarico
      if (request.getSession().getAttribute(
          CostantiGenModelli.TROVA_MODELLI_SESSION) != null) {
        request.setAttribute("trovaModelliForm",
            request.getSession().getAttribute(
                CostantiGenModelli.TROVA_MODELLI_SESSION));
      } else {
        TrovaModelliForm trovaModelliForm = new TrovaModelliForm();
        trovaModelliForm.setNoCaseSensitive(Boolean.TRUE.toString());
        request.setAttribute("trovaModelliForm", trovaModelliForm);
      }

      // Rimozione dalla sessione di oggetti comuni ai vari moduli
      // dell'applicazione, quali
      // CostantiGenerali.ID_OGGETTO_SESSION e
      // CostantiGenerali.NOME_OGGETTO_SESSION
      HttpSession sessione = request.getSession();
      sessione.removeAttribute(CostantiGenerali.ID_OGGETTO_SESSION);
      sessione.removeAttribute(CostantiGenerali.NOME_OGGETTO_SESSION);

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
