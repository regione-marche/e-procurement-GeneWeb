/*
 * Created on 07-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.prospetto;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.bl.genric.GestoreVisibilitaDati;
import it.eldasoft.gene.bl.genric.ProspettoManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.genric.DatiGenProspetto;
import it.eldasoft.gene.db.domain.genric.TrovaRicerche;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicercheProspetto;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.TrovaRicercheAction;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Schema;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityNumeri;

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
 * Action per l'apertura della pagina di Edit dei Dati Generali del prospetto in
 * analisi
 *
 * @author Luca Giacomazzo
 */
public class EditDatiGenProspettoAction extends AbstractActionBaseGenRicercheProspetto {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(EditDatiGenProspettoAction.class);

  /**
   * Reference alla classe di business logic per l'accesso ai tabellati
   */
  private TabellatiManager tabellatiManager;
  
  /** Manager dei modelli */
  private ModelliManager      modelliManager;

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi alle
   * ricerche
   */
  private ProspettoManager  prospettoManager;

  /**
   * Reference al Manager per la gestione delle protezioni di tabelle e campi
   * rispetto al profilo attivo
   */
  private GeneManager geneManager;

  /**
   * Reference al manager delle ricerche per estrarre l'elenco dei report
   * usabili come fonte dati per il report con modello
   *
   * @since 1.5.0
   */
  private RicercheManager  ricercheManager;

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setProspettoManager(ProspettoManager prospettoManager) {
    this.prospettoManager = prospettoManager;
  }
  
  /**
   * @param modelliManager modelliManager da settare internamente alla classe.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * @param geneManager geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * @param ricercheManager ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
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
    	
       String[] listaEstPdf;
    	
       // Estraggo lista estensioni pdf per cui il modello puo' essere convertito in PDF
       listaEstPdf=this.modelliManager.getEstensioniModelloOutputPDF();
       request.setAttribute("listaEstPdf", listaEstPdf);
        
      //Lettura dal request l'id del prospetto da modificare. Se non si trova
      //tale parametro, allora si sta per creare una nuova ricerca con modello
      String idRicerca = request.getParameter("idRicerca");

      DatiGenProspettoForm datiGenProspettoForm = null;
      if(idRicerca != null){
        //Lettura del modello a partire dall'id
        DatiGenProspetto datiGenProspetto = this.prospettoManager.getProspettoById(
            UtilityNumeri.convertiIntero(idRicerca).intValue());
        datiGenProspettoForm = new DatiGenProspettoForm(datiGenProspetto);
      } else if (request.getAttribute("datiGenProspettoForm") != null) {
        // si ritorna a questa pagina in seguito ad un errore verificatosi
        // durante il salvataggio, per cui il form è già presente nel request
        datiGenProspettoForm = (DatiGenProspettoForm)request.getAttribute("datiGenProspettoForm");
      } else {
        //Inizializzazione dell'oggetto DatiGenProspettoForm in fase di
        //creazione di un nuovo report con modello
        datiGenProspettoForm = new DatiGenProspettoForm();
        datiGenProspettoForm.setIdProspetto(new Integer(0));
        datiGenProspettoForm.setRicercaDisponibile(true);
        datiGenProspettoForm.setPersonale(true);
        datiGenProspettoForm.setOwner(new Integer(((ProfiloUtente)
            request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId()));
        datiGenProspettoForm.setCodApp(
            (String)request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
        datiGenProspettoForm.setProfiloOwner((String)
            request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));

        String tmpSchema = ConfigManager.getValore(CostantiGenModelli.PROP_DEFAULT_SCHEMA);
        String tmpEntita = ConfigManager.getValore(CostantiGenModelli.PROP_DEFAULT_ENTITA);
        if(tmpSchema != null && tmpSchema.length() > 0)
          datiGenProspettoForm.setSchemaPrinc(tmpSchema.trim());
        if(tmpEntita != null && tmpEntita.length() > 0)
          datiGenProspettoForm.setEntPrinc(tmpEntita.trim());
      }

      this.isEntPrincVisibile(request, datiGenProspettoForm);

      //Set nel request dell'oggetto DatiGenProspettoForm
      request.setAttribute("datiGenProspettoForm", datiGenProspettoForm);

      // Caricamento degli oggetti per popolare le comboBox presenti nella
      // pagina Lista per popolamento comboBox 'Tipo Ricerca'
      List<Tabellato> listaTipoRicerca = this.tabellatiManager.getTabellato(TabellatiManager.TIPO_RICERCHE);

      // Set nel request delle liste per il popolamento delle varie combobox
      request.setAttribute("listaTipoRicerca", listaTipoRicerca);

      // Sabbadin 12/03/2010: versione 1.5.0
      // Lista di valori non tabellato per semplicità su DB in quanto è usato
      // esclusivamente nella pagina
      List<Tabellato> listaTipoFonte = new ArrayList<Tabellato>();
      Tabellato t = new Tabellato();
      t.setTipoTabellato("0");
      t.setDescTabellato("Base dati");
      listaTipoFonte.add(t);
      t = new Tabellato();
      t.setTipoTabellato("1");
      t.setDescTabellato("Report");
      listaTipoFonte.add(t);
      request.setAttribute("listaTipoFonte", listaTipoFonte);

      // Sabbadin 12/03/2010: versione 1.5.0
      // Si estrae l'elenco dei report base e avanzati visibili all'utente
      TrovaRicerche trovaRicerche = new TrovaRicerche();
      Vector<Integer> famiglie = TrovaRicercheAction.getFamiglieReportAbilitateUtente(request);
      int indiceReportConProspetto = famiglie.indexOf(new Integer(
          CostantiGenRicerche.REPORT_PROSPETTO));
      if (indiceReportConProspetto != -1)
        famiglie.remove(indiceReportConProspetto);
      trovaRicerche.setFamiglia(famiglie);
      trovaRicerche.setCodiceApplicazione((String) request.getSession().getAttribute(
          CostantiGenerali.MODULO_ATTIVO));
      trovaRicerche.setProfiloOwner((String) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_ATTIVO));
      ProfiloUtente profiloUtente = (ProfiloUtente)request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      OpzioniUtente opzioniUtente = new OpzioniUtente(profiloUtente.getFunzioniUtenteAbilitate());
      CheckOpzioniUtente opzioniPerAbilitazioneBase = new CheckOpzioniUtente(CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
      if (!opzioniPerAbilitazioneBase.test(opzioniUtente)) {
        // se l'utente può fare solo report personali, si impostano gli stessi
        // filtri applicati implicitamente nella form di trova
        trovaRicerche.setPersonale("1");
        trovaRicerche.setDisponibile("1");
        trovaRicerche.setOwner(String.valueOf(profiloUtente.getId()));
      }
      List<?> listaRicerche = this.ricercheManager.getRicerche(trovaRicerche, true);
      request.setAttribute("listaRicerche", listaRicerche);

      //Set nel request della lista degli schemi e delle tabelle per la
      //definizione dell'entita' principale
      this.setElencoSchemiEntitaNelRequest(request);

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // Update del menu tab
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

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio dei Dati Generali di
   * una ricerca con modello
   *
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiGenRicerche.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiGenProspetto.TAB_DATI_GENERALI);
      gestoreTab.setTabSelezionabili(null);
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiGenProspetto.TAB_DATI_GENERALI);
      gestoreTab.setTabSelezionabili(null);
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
    }
  }

  /**
   * Aggiunge al request l'elenco degli schemi e delle tabelle utilizzabili
   *
   * N.B: Metodo copiato dalla classe ModelliAction
   * @param request
   */
  private void setElencoSchemiEntitaNelRequest(HttpServletRequest request) {
    // si popola il request per valorizzare correttamente le combobox degli
    // schemi e degli argomenti
    DizionarioSchemi dizSchemi = DizionarioSchemi.getInstance();
    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();

    List<String> elencoMnemoniciSchemi = dizSchemi.getMnemoniciPerRicerche();
    Vector<Schema> elencoSchemi = new Vector<Schema>();
    String mnemonicoSchema = null;
    Schema schema = null;
    List<String> elencoMnemoniciTabelle = null;
    Vector<Tabella> elencoTabelle = null;
    String mnemonicoTabella = null;
    Tabella tabella = null;

    GestoreVisibilitaDati gestoreVisibilita = this.geneManager.getGestoreVisibilitaDati();
    String profiloAttivo = (String) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);
    // estrae dai dizionari l'elenco degli schemi e delle tabelle,
    // creando una lista di schemi, e una lista di tabelle per ogni
    // schema, ognuno dei quali viene posto nel request sotto un
    // nome "elencoTabelle"+nome dello schema dell'elenco delle tabelle
    for (int i = 0; i < elencoMnemoniciSchemi.size(); i++) {
      mnemonicoSchema = elencoMnemoniciSchemi.get(i);
      schema = dizSchemi.get(mnemonicoSchema);
      elencoMnemoniciTabelle = schema.getMnemoniciTabellePerRicerche();
      elencoTabelle = new Vector<Tabella>();
      for (int j = 0; j < elencoMnemoniciTabelle.size(); j++) {
        mnemonicoTabella = elencoMnemoniciTabelle.get(j);
        tabella = dizTabelle.get(mnemonicoTabella);
        if (gestoreVisibilita.checkEntitaVisibile(tabella, profiloAttivo))
          elencoTabelle.addElement(tabella);
      }
      // set nel request dell'elenco dell'elenco delle tabelle associate al
      // j-esimo schema con nome elencoTabelle<codiceSchema>
      if(elencoTabelle.size() > 0){
        request.setAttribute("elencoTabelle" + schema.getCodice(), elencoTabelle);
        elencoSchemi.addElement(schema);
      }
    }
    // set nel request dell'elenco degli schemi
    request.setAttribute("elencoSchemi", elencoSchemi);
  }

  /**
   * Metodo per controllare se l'entita' principale e' visibile nel profilo
   * attivo
   *
   * @param request
   * @param datiGenProspettoForm
   */
  private void isEntPrincVisibile(HttpServletRequest request,
      DatiGenProspettoForm datiGenProspettoForm) {

    String messageKey;
    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    if (datiGenProspettoForm.getEntPrinc() != null && !"".equals(datiGenProspettoForm.getEntPrinc())) {
      Tabella tabella = dizTabelle.getDaNomeTabella(datiGenProspettoForm.getEntPrinc());
      boolean isEntPrincVisibile = this.geneManager.getGestoreVisibilitaDati().checkEntitaVisibile(
          tabella,
          (String) request.getSession().getAttribute(
              CostantiGenerali.PROFILO_ATTIVO));

      if(! isEntPrincVisibile){
        messageKey = "warnings.prospetto.caricaProspetto.prospettoModificatoDaProfilo";
        logger.warn(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);

        // Reset degli attributi dell'oggetto DatiGenProspettoForm associati alla
        // entita' non piu' visibile nel profilo attivo
        datiGenProspettoForm.setDescEntPrinc(null);
        datiGenProspettoForm.setDescSchemaPrinc(null);
        datiGenProspettoForm.setEntPrinc("");
        datiGenProspettoForm.setSchemaPrinc("");
        datiGenProspettoForm.setMneEntPrinc(null);
      }
    }
  }

}