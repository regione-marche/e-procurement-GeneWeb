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
package it.eldasoft.gene.web.struts.genric.impexp;

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
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.util.ArrayList;
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
 * Inizializzazione della pagina TrovaRicerche, con verifica della presenza in
 * sessione dell'oggetto TrovaRicercheForm. Se non trova nessuna istanza di tale
 * classe allora la crea con gli attributi a 'null'. Se trova l'oggetto, allora
 * ne copia gli attributi in un form opportuno, inserendolo nel request.
 *
 * @author Luca Giacomazzo
 */
public class InitTrovaRicercheExportAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(InitTrovaRicercheExportAction.class);

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

    // Caricamento degli oggetti per popolare le comboBox presenti nella
    // pagina Lista per popolamento comboBox 'Tipo Ricerca'
    List<Tabellato> listaTipoRicerca = this.tabellatiManager.getTabellato(TabellatiManager.TIPO_RICERCHE);

    // Caricamento degli oggetti per popolare la comboBox Famiglia
    List<Tabellato> listaFamigliaRicerca = this.tabellatiManager.getTabellato("W0001", 1);

    Vector<Tabellato> elementiDellaLista = new Vector<Tabellato>();

    for(int i=0; i < listaFamigliaRicerca.size(); i++){
      elementiDellaLista.add(listaFamigliaRicerca.get(i));
    }

    listaFamigliaRicerca = elementiDellaLista;

    // Se la property it.eldasoft.generatoreRicerche.base.schemaViste
    // e' valorizzata allora è possibile cercare le ricerche base, altrimenti no
    String nomeSchemaVista =  ConfigManager.getValore(
        CostantiGenerali.PROP_SCHEMA_VISTE_REPORT_BASE);
    if(nomeSchemaVista == null || nomeSchemaVista.length() == 0)
      for(int i=0; i < listaFamigliaRicerca.size(); i++){
        Tabellato tabellato = (Tabellato) listaFamigliaRicerca.get(i);
        if(CostantiGenRicerche.REPORT_BASE == Integer.parseInt(tabellato.getTipoTabellato())) {
          listaFamigliaRicerca.remove(i);
          break;
        }
      }
    //se non è abilitato nemmeno un tipo di ricerca rimando alla pagina di Opzione non Abilitata
    //caso limite che non dovrebbe mai accadere!!
    if (listaFamigliaRicerca.size() == 0) {
      target = CostantiGeneraliStruts.FORWARD_OPZIONE_NON_ABILITATA;
      messageKey = "errors.opzione.noAbilitazione";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }
    String codiceProfilo = (String) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);
    // Lista per popolamento comboBox 'Gruppo'
    List<?> listaGruppi = this.gruppiManager.getGruppiOrderByNome(codiceProfilo);

    // Lista per popolamento comboBox 'Utente creatore'
    List<Account> listaUtenti = this.accountManager.getListaAccountByCodProCodApp(
        (String) request.getSession().getAttribute(
            CostantiGenerali.MODULO_ATTIVO), codiceProfilo);

    // Set nel request delle liste per il popolamento delle varie combobox
    request.setAttribute("listaTipoRicerca", listaTipoRicerca);
    request.setAttribute("listaFamigliaRicerca", listaFamigliaRicerca);
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


    // Verifica della presenza in sessione dei parametri per trova ricerche,
    // di una precedente ricerca per esportazione, ed agisce di conseguenza
    this.checkParamTrovaRicercheExport(request, response);

    this.gestioneVociAvanzamento(request,
        CostantiWizard.CODICE_PAGINA_TROVA_REPORT_PER_EXPORT);

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
 * Verifica la presenza in sessione dell'oggetto TrovaRicercheForm. Se non
 * trova nessuna istanza di tale classe allora la crea con gli attributi a
 * 'null'. Se trova l'oggetto, allora ne copia gli attributi in un form
 * opportuno, inserendolo nel request.
 *
 * @param session
 *        sessione dell'utente
 */
private void checkParamTrovaRicercheExport(HttpServletRequest request,
    HttpServletResponse response) {
  // ActionForm per la visualizzazione dei parametri di ricerca
  TrovaRicercheExportForm form = new TrovaRicercheExportForm();
  form.setOperatoreNomeRicerca(GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1]);
  form.setOperatoreDescrizioneRicerca(GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1]);
  form.setNoCaseSensitive(Boolean.TRUE.toString());

  TrovaRicercheExportForm parametriTrovaRicerche = (TrovaRicercheExportForm)
      request.getSession().getAttribute(CostantiGenRicerche.TROVA_RICERCHE_EXPORT);

  if (parametriTrovaRicerche != null) {
    // In sessione è presente l'oggetto contenente i dati di una ricerca: nel
    // request viene inserito un riferimento all'oggetto in sessione
    form = parametriTrovaRicerche;
  }

  // SET nel request del form necessario alla pagina di Trova Ricerche
  request.setAttribute("trovaRicercheExportForm", form);
}

  private void gestioneVociAvanzamento(HttpServletRequest request,
      String paginaAttiva) {
    List<String> pagineVisitate = new ArrayList<String>();
    List<String> pagineDaVisitare = new ArrayList<String>();

    if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_TROVA_REPORT_PER_EXPORT)) {
      pagineVisitate.add(CostantiWizard.TITOLO_TROVA_REPORT_PER_EXPORT);

      pagineDaVisitare.add(CostantiWizard.TITOLO_LISTA_REPORT_PER_EXPORT);
    } else if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_LISTA_REPORT_PER_EXPORT)) {
      pagineVisitate.add(CostantiWizard.TITOLO_TROVA_REPORT_PER_EXPORT);
      pagineVisitate.add(CostantiWizard.TITOLO_LISTA_REPORT_PER_EXPORT);
    }
    request.setAttribute("pagineVisitate", pagineVisitate);
    request.setAttribute("pagineDaVisitare", pagineDaVisitare);
  }

}