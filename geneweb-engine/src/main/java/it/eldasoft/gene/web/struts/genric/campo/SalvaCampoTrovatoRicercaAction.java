/*
 * Created on 14-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.campo;

import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.domain.genric.GiunzioneRicerca;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.giunzione.GiunzioneRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.LegameTabelle;
import it.eldasoft.utils.metadata.domain.Schema;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.profiles.cache.DizionarioLivelli;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per l'inserimento di un nuovo campo in una ricerca o per la modifica
 * di un campo esistente
 * 
 * @author Stefano.Sabbadin
 */
public class SalvaCampoTrovatoRicercaAction extends
    AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(SalvaCampoTrovatoRicercaAction.class);

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // target di default
    String target = "closeAndRefresh";

    DizionarioSchemi dizSchemi = DizionarioSchemi.getInstance();
    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    String campoDaInserire = request.getParameter("campo");
    
    String[] array = campoDaInserire.split("\\.");
    String tabella = array[0];
    String campo = array[1];

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        "profiloUtente");

    TabellaRicercaForm tabellaRicercaForm = null;
    CampoRicercaForm campoRicercaForm = null;
    boolean trovato = false;

    // ciclo sulle tabelle già esistenti nella ricerca e verifico se il campo
    // appartiene ad una di queste
    for (int i = 0; i < contenitore.getNumeroTabelle() && !trovato; i++) {
      tabellaRicercaForm = contenitore.estraiTabella(i);
      if (tabellaRicercaForm.getAliasTabella().equals(tabella)) {
        campoRicercaForm = new CampoRicercaForm();
        campoRicercaForm.setMnemonicoTabella(tabellaRicercaForm.getMnemonicoTabella());
        campoRicercaForm.setAliasTabella(tabellaRicercaForm.getAliasTabella());
        campoRicercaForm.setDescrizioneTabella(tabellaRicercaForm.getDescrizioneTabella());
        campoRicercaForm.setMnemonicoCampo(campo);
        campoRicercaForm.setDescrizioneCampo(dizCampi.get(campo).getDescrizioneBreve());
        
        // **********************************************************************
        // L.G. 23/02/2007 
        // Al momento dell'inserimento del campo si setta per default come titolo
        // della colonna la descrizione del campo stesso presente in C0CAMPI
        // **********************************************************************
        campoRicercaForm.setTitoloColonna(dizCampi.get(campo).getDescrizione());
        contenitore.aggiungiCampo(campoRicercaForm);
        trovato = true;
        // nel caso in cui la tabella sia presente ma non visibile, visto che
        // ora estraggo un campo la rendo visibile
        if (!tabellaRicercaForm.getVisibile())
          tabellaRicercaForm.setVisibile(true);
      }
    }

    // se non ho trovato il campo nelle tabelle già esistenti, allora devo
    // aggiungere anche la tabella
    if (!trovato) {

      Tabella tab = dizTabelle.getDaNomeTabella(tabella);

      tabellaRicercaForm = new TabellaRicercaForm();
      tabellaRicercaForm.setMnemonicoTabella(tab.getCodiceMnemonico());
      tabellaRicercaForm.setNomeTabella(tabella);
      tabellaRicercaForm.setAliasTabella(tabella);
      tabellaRicercaForm.setDescrizioneTabella(tab.getDescrizione());

      // si impostano le informazioni relative alla tabella
      Schema schema = dizSchemi.get(tab.getNomeSchema());
      tabellaRicercaForm.setDescrizioneSchema(schema.getDescrizione());
      tabellaRicercaForm.setNomeTabella(dizTabelle.get(
          tabellaRicercaForm.getMnemonicoTabella()).getNomeTabella());

      // Impostazione di default per ogni nuovo argomento aggiunto ad una
      // ricerca
      tabellaRicercaForm.setVisibile(true);

      // si impostano le informazioni relative alle giunzioni con l'inserimento
      // della nuova tabella
      GiunzioneRicercaForm giunzioneRicercaForm = null;
      Vector<TabellaRicercaForm> tabelleRicercaForm = contenitore.getElencoArgomenti();

      TabellaRicercaForm tabellaFormInseritaInPrecedenza = null;
      Tabella tabellaInseritaInPrecedenza = null;
      Vector<GiunzioneRicercaForm> elencoGiunzioniDaAggiungere = new Vector<GiunzioneRicercaForm>();

      for (int i = 0; i < tabelleRicercaForm.size(); i++) {
        tabellaFormInseritaInPrecedenza = (TabellaRicercaForm) tabelleRicercaForm.elementAt(i);
        tabellaInseritaInPrecedenza = dizTabelle.get(tabellaFormInseritaInPrecedenza.getMnemonicoTabella());

        // ciclo sui legami della tabella già presente nell'elenco per
        // verificare
        // se ha legami con la nuova

        if (tabellaInseritaInPrecedenza.getLegameTabelle(tab.getNomeTabella()).length > 0) {
          giunzioneRicercaForm = new GiunzioneRicercaForm();
          giunzioneRicercaForm.setGiunzioneAttiva(true);
          giunzioneRicercaForm.setMnemonicoTabella1(tabellaFormInseritaInPrecedenza.getMnemonicoTabella());
          giunzioneRicercaForm.setMnemonicoTabella2(tabellaRicercaForm.getMnemonicoTabella());
          giunzioneRicercaForm.setAliasTabella1(tabellaFormInseritaInPrecedenza.getAliasTabella());
          giunzioneRicercaForm.setAliasTabella2(tabellaRicercaForm.getAliasTabella());
          giunzioneRicercaForm.setDescrizioneTabella1(tabellaFormInseritaInPrecedenza.getDescrizioneTabella());
          giunzioneRicercaForm.setDescrizioneTabella2(tabellaRicercaForm.getDescrizioneTabella());

          // si prende il primo legame e lo si inserisce nella join
          LegameTabelle legame = tabellaInseritaInPrecedenza.getLegameTabelle(tab.getNomeTabella())[0];
          giunzioneRicercaForm.setCampiTabella1(UtilityStringhe.serializza(
              legame.getElencoCampiTabellaOrigine(),
              GiunzioneRicerca.SEPARATORE_CAMPI_JOIN));
          giunzioneRicercaForm.setCampiTabella2(UtilityStringhe.serializza(
              legame.getElencoCampiTabellaDestinazione(),
              GiunzioneRicerca.SEPARATORE_CAMPI_JOIN));

          elencoGiunzioniDaAggiungere.addElement(giunzioneRicercaForm);
        }

        // controllo rovesciato: verifico se la nuova tabella ha legami con una
        // quella già presente
        if (tab.getLegameTabelle(tabellaInseritaInPrecedenza.getNomeTabella()).length > 0) {
          giunzioneRicercaForm = new GiunzioneRicercaForm();
          giunzioneRicercaForm.setGiunzioneAttiva(true);
          giunzioneRicercaForm.setMnemonicoTabella1(tabellaRicercaForm.getMnemonicoTabella());
          giunzioneRicercaForm.setMnemonicoTabella2(tabellaFormInseritaInPrecedenza.getMnemonicoTabella());
          giunzioneRicercaForm.setAliasTabella1(tabellaRicercaForm.getAliasTabella());
          giunzioneRicercaForm.setAliasTabella2(tabellaFormInseritaInPrecedenza.getAliasTabella());
          giunzioneRicercaForm.setDescrizioneTabella1(tabellaRicercaForm.getDescrizioneTabella());
          giunzioneRicercaForm.setDescrizioneTabella2(tabellaFormInseritaInPrecedenza.getDescrizioneTabella());

          // si prende il primo legame e lo si inserisce nella join
          LegameTabelle legame = tab.getLegameTabelle(tabellaInseritaInPrecedenza.getNomeTabella())[0];
          giunzioneRicercaForm.setCampiTabella1(UtilityStringhe.serializza(
              legame.getElencoCampiTabellaOrigine(),
              GiunzioneRicerca.SEPARATORE_CAMPI_JOIN));
          giunzioneRicercaForm.setCampiTabella2(UtilityStringhe.serializza(
              legame.getElencoCampiTabellaDestinazione(),
              GiunzioneRicerca.SEPARATORE_CAMPI_JOIN));

          elencoGiunzioniDaAggiungere.addElement(giunzioneRicercaForm);
        }
      }

      // si aggiungono le giunzioni
      for (int i = 0; i < elencoGiunzioniDaAggiungere.size(); i++) {
        contenitore.aggiungiGiunzione((GiunzioneRicercaForm) elencoGiunzioniDaAggiungere.elementAt(i));
      }

      // si aggiunge la tabella all'elenco
      contenitore.aggiungiArgomento(tabellaRicercaForm);
      contenitore.getTestata().setEntPrinc(tabellaRicercaForm.getAliasTabella());
      
      // WE439: nel caso di utente con soli report personali, si
      // imposta il filtro sul livello utente se l'entita' inserita prevede
      // delle regole
      if (!contenitore.getTestata().getFiltroUtente()) {
        OpzioniUtente opzioniUtente = new OpzioniUtente(
            profiloUtente.getFunzioniUtenteAbilitate());
        CheckOpzioniUtente opzioniPerSoliReportPersonali = new CheckOpzioniUtente(
            CostantiGeneraliAccount.SOLO_REPORT_PERSONALI_GENRIC);
        if (opzioniPerSoliReportPersonali.test(opzioniUtente)) {
          // Si controlla se la tabella aggiunta e' in relazione con l'id
          // utente. Se si, allora setto l'attributo della testata
          // filtroIdUtente a true, altrimenti lo setto a false
          DizionarioLivelli dizLivelli = DizionarioLivelli.getInstance();
          if (dizLivelli.isFiltroLivelloPresente(tab.getNomeTabella()))
            contenitore.getTestata().setFiltroUtente(true);
        }
      }

      // si aggiunge il campo all'elenco
      campoRicercaForm = new CampoRicercaForm();
      campoRicercaForm.setMnemonicoTabella(tabellaRicercaForm.getMnemonicoTabella());
      campoRicercaForm.setAliasTabella(tabellaRicercaForm.getAliasTabella());
      campoRicercaForm.setDescrizioneTabella(tabellaRicercaForm.getDescrizioneTabella());
      campoRicercaForm.setMnemonicoCampo(campo);
      campoRicercaForm.setDescrizioneCampo(dizCampi.get(campo).getDescrizioneBreve());
      
      // **********************************************************************
      // L.G. 23/02/2007 
      // Al momento dell'inserimento del campo si setta per default come titolo
      // della colonna la descrizione del campo stesso presente in C0CAMPI
      // **********************************************************************
      campoRicercaForm.setTitoloColonna(dizCampi.get(campo).getDescrizione());
      contenitore.aggiungiCampo(campoRicercaForm);
    }
    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());

    request.setAttribute("url", "/geneGenric/CambiaTabRicerca.do?tab=CAM");

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }
}
