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
package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;
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
public class InitTrovaRicercheAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(InitTrovaRicercheAction.class);

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
      List<Tabellato> listaTipoRicerca = this.tabellatiManager.getTabellato(
          TabellatiManager.TIPO_RICERCHE);

      // Caricamento degli oggetti per popolare la comboBox Famiglia
      List<Tabellato> listaFamigliaRicerca = this.tabellatiManager.getTabellato(
          TabellatiManager.FAMIGLIA_RICERCA, 1);

      //filtro la listaFamigliaRicerca in base alle opzioniAcquistate e alle opzioniUtente
      ServletContext context = request.getSession().getServletContext();
      Collection<String> opzioni = Arrays.asList((String[])
          context.getAttribute(CostantiGenerali.OPZIONI_DISPONIBILI));

      ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute("profiloUtente");
      OpzioniUtente opzioniUtente = new OpzioniUtente(profiloUtente.getFunzioniUtenteAbilitate());

      CheckOpzioniUtente opzioniPerAbilitazioneBase = new CheckOpzioniUtente(
          CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
      CheckOpzioniUtente opzioniPerAbilitazioneAvanzato = new CheckOpzioniUtente(
          CostantiGeneraliAccount.ABILITAZIONE_REPORT_AVANZATI);
      CheckOpzioniUtente opzioniPerAbilitazioneProspetto = new CheckOpzioniUtente(
          CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
      CheckOpzioniUtente opzioniPerAbilitazioneReportSQL = new CheckOpzioniUtente(
          CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);

      Vector<Tabellato> elementiDellaLista = new Vector<Tabellato>();

      for(int i=0; i < listaFamigliaRicerca.size(); i++){
        Tabellato tabellato = (Tabellato) listaFamigliaRicerca.get(i);
        if((CostantiGenRicerche.REPORT_BASE == Integer.parseInt(tabellato.getTipoTabellato()))
              && ( opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL)
                  && opzioniPerAbilitazioneBase.test(opzioniUtente) )) {
          elementiDellaLista.add(listaFamigliaRicerca.get(i));
        }
        if((CostantiGenRicerche.REPORT_AVANZATO == Integer.parseInt(tabellato.getTipoTabellato()))
            && (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE)
                && (opzioniPerAbilitazioneAvanzato.test(opzioniUtente)))) {
          elementiDellaLista.add(listaFamigliaRicerca.get(i));
        }
        if((CostantiGenRicerche.REPORT_PROSPETTO == Integer.parseInt(tabellato.getTipoTabellato()))
            && (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL)
                && (opzioniPerAbilitazioneProspetto.test(opzioniUtente)))) {
          elementiDellaLista.add(listaFamigliaRicerca.get(i));
        }
        if((CostantiGenRicerche.REPORT_SQL == Integer.parseInt(tabellato.getTipoTabellato()))
            && (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL)
                && (opzioniPerAbilitazioneReportSQL.test(opzioniUtente)))) {
          elementiDellaLista.add(listaFamigliaRicerca.get(i));
        }
      }
      /*
      int n = elementiDaEliminare.size();
      for (int i = 0; i < n; i++) {
        listaFamigliaRicerca.remove(((Integer)elementiDaEliminare.elementAt(i)).intValue());
      }*/
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
      // se non e' abilitato nemmeno un tipo di ricerca rimando alla pagina di Opzione
      // non Abilitatacaso limite che non dovrebbe mai accadere!!
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
      // di una precedente ricerca, ed agisce di conseguenza
      this.checkParamTrovaRicerche(request, response);

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
  private void checkParamTrovaRicerche(HttpServletRequest request,
      HttpServletResponse response) {
    // ActionForm per la visualizzazione dei parametri di ricerca
    TrovaRicercheForm form = new TrovaRicercheForm();
    form.setNoCaseSensitive(Boolean.TRUE.toString());

    TrovaRicercheForm parametriTrovaRicerche = (TrovaRicercheForm)
        request.getSession().getAttribute(CostantiGenRicerche.TROVA_RICERCHE);

    if (parametriTrovaRicerche != null) {
      // In sessione è presente l'oggetto contenente i dati di una ricerca: nel
      // request viene inserito un riferimento all'oggetto in sessione
      form = parametriTrovaRicerche;
    }

    // SET nel request del form necessario alla pagina di Trova Ricerche
    request.setAttribute("trovaRicercheForm", form);
  }

}