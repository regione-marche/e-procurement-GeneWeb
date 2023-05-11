/*
 * Created on 02-apr-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.RicercaGruppo;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action per accesso alla pagina creanNuovaRicerca.jsp per la scelta del tipo
 * di ricerca da creare.
 * 
 * @author Luca.Giacomazzo
 */
public class CreaNuovaRicercaAction extends AbstractActionBaseGenRicerche {

  private static final String SUCCESS_NUOVO_REPORT_BASE                = "successBase";
  private static final String SUCCESS_NUOVO_REPORT_AVANZATO            = "successAvanzato";
  private static final String SUCCESS_NUOVO_REPORT_PROSPETTO           = "successProspetto";
  private static final String SUCCESS_NUOVO_REPORT_SQL                 = "successReportSql";

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(CreaNuovaRicercaAction.class);
  
  /**
   * Reference alla classe di business logic per accesso ai dati relativi alle
   * ricerche
   */
  private RicercheManager     ricercheManager;

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;
    // L.G. 09/01/2008: modifica per gestire più agevolmente l'annullamento della
    // creazione di una nuova ricerca
    
    // Se in sessione l'attributo CostantiGenerali.ID_OGGETTO_SESSION e' valorizzato
    // allora inserisco in sessione l'attributo CostantiGenRicerche.ID_RICERCA_PRECEDENTE
    // con lo stesso valore e rimuovo sia l'attributo CostantiGenerali.ID_OGGETTO_SESSION 
    // che l'attributo CostantiGenerali.NOME_OGGETTO_SESSION. Questo servira' per
    // ripristinare la ricerca precedentemente visualizzata ogni volta che si
    // annulla la creazione di una ricerca
    if (request.getSession().getAttribute(CostantiGenerali.ID_OGGETTO_SESSION) != null) {
      request.getSession().setAttribute(CostantiGenRicerche.ID_RICERCA_PRECEDENTE, 
          request.getSession().getAttribute(CostantiGenerali.ID_OGGETTO_SESSION));
      request.getSession().removeAttribute(CostantiGenerali.ID_OGGETTO_SESSION);
      request.getSession().removeAttribute(CostantiGenerali.NOME_OGGETTO_SESSION);
    }
    // L.G. 09/01/2008: fine modifica
    
    List<?> listaRicerchePredefinite = new ArrayList();

    // Se la property it.eldasoft.generatoreRicerche.base.schemaViste
    // e' valorizzata allora è possibile creare ricerche base, altrimenti, la
    // voce relativa ai report base non viene visualizzata nella pagina
    // creaNuovaRicerca.jsp

    // F.D. 08/05/07 controllo le abilitazioni e bypass della pagina di scelta
    // della ricerca da creare se si e' abilitati solo ad un tipo di ricerca e
    // non alle altre rimando direttamente alla creazione  bypassando la pagina
    // di scelta controllo le abilitazioni per le opzioniAcquistate e per le
    // opzioniUtente del profilo loggato se si e' abilitati ad un solo tipo di
    // ricerca viene modificato il target in modo da rimandare alla creazione
    // diretta senza passare dalla scelta
    boolean abilitazioneReportPersonali = false;
    String moduloAttivo = (String) request.getSession().getAttribute(
        CostantiGenerali.MODULO_ATTIVO);
    String profiloAttivo = (String) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);
    ServletContext context = request.getSession().getServletContext();
    Collection<String> opzioni = Arrays.asList((String[]) 
        context.getAttribute(CostantiGenerali.OPZIONI_DISPONIBILI));

    ProfiloUtente profiloUtente = (ProfiloUtente)
        request.getSession().getAttribute("profiloUtente");
    OpzioniUtente opzioniUtente = new OpzioniUtente(
        profiloUtente.getFunzioniUtenteAbilitate());

    CheckOpzioniUtente opzioniPerAbilitazioneBase = new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
    CheckOpzioniUtente opzioniPerAbilitazioneAvanzato = new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_AVANZATI);
    CheckOpzioniUtente opzioniPerAbilitazioneProspetto = new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
    CheckOpzioniUtente opzioniPerAbilitazioneReportSQL = new CheckOpzioniUtente(
        CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
    
    // FASE 1: test di verifica di quali report sono creabili
    Vector<Integer> famiglieUtilizzabili = new Vector<Integer>();
    if (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL)
        && opzioniPerAbilitazioneBase.test(opzioniUtente)) {
      // un report base si può creare solo se esiste lo schema delle viste associate
      String nomeSchemaVista = ConfigManager.getValore(
          CostantiGenerali.PROP_SCHEMA_VISTE_REPORT_BASE);
      if (nomeSchemaVista != null && nomeSchemaVista.length() > 0) {
        request.setAttribute("nomeSchemaVista", nomeSchemaVista);
        famiglieUtilizzabili.add(new Integer(CostantiGenRicerche.REPORT_BASE));
      }
    }

    if (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE)
        && opzioniPerAbilitazioneAvanzato.test(opzioniUtente)) {
      famiglieUtilizzabili.add(new Integer(CostantiGenRicerche.REPORT_AVANZATO));
    }

    if (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL)
        && opzioniPerAbilitazioneProspetto.test(opzioniUtente)) {
      famiglieUtilizzabili.add(new Integer(CostantiGenRicerche.REPORT_PROSPETTO));
    }

    if (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL)
        && opzioniPerAbilitazioneReportSQL.test(opzioniUtente)) {
      famiglieUtilizzabili.add(new Integer(CostantiGenRicerche.REPORT_SQL));
    }
    
    //L.G. 23/05/2007 implementazione della possibilita' di creazione di un report
    //(avanzato o base) a partire da uno esistente per i gli utenti che possono
    //creare solo report personali
    CheckOpzioniUtente opzioniPerSoliReportPersonali = new CheckOpzioniUtente(
        CostantiGeneraliAccount.SOLO_REPORT_PERSONALI_GENRIC);
    
    abilitazioneReportPersonali = opzioniPerSoliReportPersonali.test(opzioniUtente);
    
    if (abilitazioneReportPersonali) {
      //estrazione della lista dei report predefiniti, da cui copiare per creare 
      //un nuovo report. Questa funzionalita' e' utilizzabile solo per chi puo' 
      //fare solo report personali
      boolean mostraReportBase = false;
      
      if (famiglieUtilizzabili.contains(new Integer(CostantiGenRicerche.REPORT_BASE))) {
        mostraReportBase = true;
      }
      // I report Sql non possono essere usati e modificati per creare nuovi report
      // dagli utenti che possono creare solo report personali 
      boolean mostraReportSql = false;
      try {
        //Estrazione delle ricerche predefinite, le quali possono contenere delle
        //ricerche che non sono accessibili all'utente
        listaRicerchePredefinite = ricercheManager.getRicerchePredefinite(
            profiloUtente.getId(), moduloAttivo, profiloAttivo, mostraReportBase,
            mostraReportSql);

        if (listaRicerchePredefinite.size() > 0) {
          //Ciclo sulla lista appena estratta per rimuovere le eventuali ricerche
          //la cui famiglia non e' accessibile all'utente oppure e' un report con 
          //prospetto
          ListIterator<?> iter = listaRicerchePredefinite.listIterator();
          while (iter.hasNext()) {
            RicercaGruppo ricercaGruppo = (RicercaGruppo) iter.next();
            if ((!famiglieUtilizzabili.contains(new Integer(ricercaGruppo.getFamiglia()))) ||
               Integer.parseInt(ricercaGruppo.getFamiglia()) == CostantiGenRicerche.REPORT_PROSPETTO)
              iter.remove();
          }
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
    }
    //L.G. 23/05/2007: fine modifica
    
    // SS 21/06/2007: sposto fuori il set nel request delle ricerche predefinite e il bypass
    // FASE 2: bypass all'unico report abilitato (se si rientra in questa casistica)
    if (listaRicerchePredefinite.size() > 0) {
      request.setAttribute("listaRicerchePredefinite", listaRicerchePredefinite);
    } else {
      if (famiglieUtilizzabili.size() == 1) {
        switch (((Integer) famiglieUtilizzabili.elementAt(0)).intValue()) {
        case CostantiGenRicerche.REPORT_BASE:
          target = SUCCESS_NUOVO_REPORT_BASE;
          break;
        case CostantiGenRicerche.REPORT_AVANZATO:
          target = SUCCESS_NUOVO_REPORT_AVANZATO;
          break;
        case CostantiGenRicerche.REPORT_PROSPETTO:
          target = SUCCESS_NUOVO_REPORT_PROSPETTO;
          break;
        case CostantiGenRicerche.REPORT_SQL:
          target = SUCCESS_NUOVO_REPORT_SQL;
          break;
        }
      }
    }
    
    // set nel request del parameter per disabilitare la navigazione in fase
    // di editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);
    
    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }
    return mapping.findForward(target);
  }

}