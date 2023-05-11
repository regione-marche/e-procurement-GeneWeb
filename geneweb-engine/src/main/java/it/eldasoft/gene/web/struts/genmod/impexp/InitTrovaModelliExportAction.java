/*
 * Created on 15-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod.impexp;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.ArrayList;
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
 * Inizializzazione della pagina TrovaModelli, con verifica della presenza in
 * sessione dell'oggetto TrovaModelliExportForm. Se non trova nessuna istanza di tale
 * classe allora la crea con gli attributi a 'null'. Se trova l'oggetto, allora
 * ne copia gli attributi in un form opportuno, inserendolo nel request.
 *
 * @author Francesco.DeFilippis
 */
public class InitTrovaModelliExportAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(InitTrovaModelliExportAction.class);

  /**
   * Reference alla classe di business logic per la gestione dei tabellati
   */
  private TabellatiManager tabellatiManager;

  /**
   * Reference alla classe di business logic per la gestione dei gruppi
   */
  private GruppiManager    gruppiManager;

  /**
   * Reference alla classe di business logic per la gestione degli account
   */
  private AccountManager    accountManager;


  /**
   * @return tabellatiManager
   *         tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param gruppiManager
   *        gruppiManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }

  /**
   * @param gruppiManager
   *        gruppiManager da settare internamente alla classe.
   */
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioniRunAction()
   */
  @Override
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

  if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

  // target di default
  String target = CostantiGeneraliStruts.FORWARD_OK;
  String messageKey = null;

  try {
    // Rimozione dalla sessione degli oggetti di dettaglio
    // precedentemente caricati
    this.cleanSession(request);

    // Rimozione dalla sessione di oggetti comuni ai vari moduli
    // dell'applicazione, quali
    // CostantiGenerali.ID_OGGETTO_SESSION e
    // CostantiGenerali.NOME_OGGETTO_SESSION
    HttpSession sessione = request.getSession();
    sessione.removeAttribute(CostantiGenerali.ID_OGGETTO_SESSION);
    sessione.removeAttribute(CostantiGenerali.NOME_OGGETTO_SESSION);

    String codiceProfilo = (String) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);
    // Caricamento degli oggetti per popolare le comboBox presenti nella
    // pagina Lista per popolamento comboBox 'Tipo Modello'
    List<Tabellato> listaTipoModello = this.tabellatiManager.getTabellato(TabellatiManager.TIPO_MODELLI);

    // Lista per popolamento comboBox 'Gruppo'
    List<?> listaGruppi = this.gruppiManager.getGruppiOrderByNome(codiceProfilo);

    // Lista per popolamento comboBox 'Utente creatore'
    List<Account> listaUtenti = this.accountManager.getListaAccountByCodProCodApp(
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
        CostantiGenModelli.TROVA_MODELLI_EXPORT) != null) {
      request.setAttribute("trovaModelliExportForm",
          request.getSession().getAttribute(
              CostantiGenModelli.TROVA_MODELLI_EXPORT));
    } else {
      TrovaModelliExportForm trovaModelliExportForm = new TrovaModelliExportForm();
      trovaModelliExportForm.setNoCaseSensitive(Boolean.TRUE.toString());
      request.setAttribute("trovaModelliExportForm", trovaModelliExportForm);
    }

    // Verifica della presenza in sessione dei parametri per trova ricerche,
    // di una precedente ricerca per esportazione, ed agisce di conseguenza
    this.checkParamTrovaModelliExport(request, response);

    this.gestioneVociAvanzamento(request,
        CostantiWizard.CODICE_PAGINA_TROVA_MODELLI_PER_EXPORT);

    // set nel request del parameter per disabilitare la navigazione
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

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
 * Verifica la presenza in sessione dell'oggetto TrovaModelliExportForm. Se non
 * trova nessuna istanza di tale classe allora la crea con gli attributi a
 * 'null'. Se trova l'oggetto, allora ne copia gli attributi in un form
 * opportuno, inserendolo nel request.
 *
 * @param session
 *        sessione dell'utente
 */
private void checkParamTrovaModelliExport(HttpServletRequest request,
    HttpServletResponse response) {
  // ActionForm per la visualizzazione dei parametri di ricerca
  TrovaModelliExportForm form = new TrovaModelliExportForm();
  form.setNoCaseSensitive(Boolean.TRUE.toString());
  form.setRisPerPagina("20");
  form.setOperatoreNomeModello(GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1]);
  form.setOperatoreDescrModello(GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1]);
  form.setOperatoreFileModello(GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1]);

  TrovaModelliExportForm parametriTrovaModelli = (TrovaModelliExportForm)
      request.getSession().getAttribute(CostantiGenModelli.TROVA_MODELLI_EXPORT);

  if (parametriTrovaModelli != null) {
    // In sessione è presente l'oggetto contenente i dati di una ricerca: nel
    // request viene inserito un riferimento all'oggetto in sessione
    form = parametriTrovaModelli;
  }

  // SET nel request del form necessario alla pagina di Trova Ricerche
  request.setAttribute("trovaModelliExportForm", form);
}

  private void gestioneVociAvanzamento(HttpServletRequest request,
      String paginaAttiva) {
    List<String> pagineVisitate = new ArrayList<String>();
    List<String> pagineDaVisitare = new ArrayList<String>();

    if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_TROVA_MODELLI_PER_EXPORT)) {
      pagineVisitate.add(CostantiWizard.TITOLO_TROVA_MODELLI_PER_EXPORT);

      pagineDaVisitare.add(CostantiWizard.TITOLO_LISTA_MODELLI_PER_EXPORT);
    } else if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_LISTA_MODELLI_PER_EXPORT)) {
      pagineVisitate.add(CostantiWizard.TITOLO_TROVA_MODELLI_PER_EXPORT);
      pagineVisitate.add(CostantiWizard.TITOLO_LISTA_MODELLI_PER_EXPORT);
    }
    request.setAttribute("pagineVisitate", pagineVisitate);
    request.setAttribute("pagineDaVisitare", pagineDaVisitare);
  }

}