/*
 * Created on 05-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.argomenti;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.genric.GestoreVisibilitaDati;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Schema;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
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

/**
 * Azione per il caricamento della pagina per aggiunta di un argomento ad una
 * ricerca. Vengono caricati nel request diversi vettori: uno e' l'elenco degli
 * schemi esistenti, mentre tutti gli altri sono gli elenchi delle tabelle
 * associate ad ogni schema.
 *
 * @author Luca Giacomazzo
 */
public class InitAddTabellaRicercaAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(InitAddTabellaRicercaAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere alla action runAction
   * @return opzioni per accedere alla action
   */
  @Override
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }

  /**
   * Reference al Manager per la gestione delle protezioni di tabelle e campi
   * rispetto al profilo attivo
   */
  private GeneManager geneManager;

  /**
   * @param geneManager geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * Inserisce nella request l'elenco degli schemi e gli elenchi delle tabelle
   * associate l'elenco dei campi associati a tali tabelle
   *
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    
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
      // SS 25/10/2006: inserita estrazione mnemonici filtrati per visibilita'
      // nelle ricerche
      elencoMnemoniciTabelle = schema.getMnemoniciTabellePerRicerche();
      elencoTabelle = new Vector<Tabella>();
      for (int j = 0; j < elencoMnemoniciTabelle.size(); j++) {
        mnemonicoTabella = elencoMnemoniciTabelle.get(j);
        tabella = dizTabelle.get(mnemonicoTabella);
        if (gestoreVisibilita.checkEntitaVisibile(tabella, profiloAttivo)) {
          elencoTabelle.addElement(tabella);
        } 
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

    if (((TabellaRicercaForm) request.getAttribute("tabellaRicercaForm")) != null)
      request.setAttribute("tabellaRicercaForm", form);
    else
      request.setAttribute("tabellaRicercaForm", new TabellaRicercaForm());

    // set nel request del parameter per disabilitare la navigazione in fase di
    // editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

    // set del menuTab
    this.setMenuTab(request);

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di creazione di un nuovo argomento da aggiungere ad
   * una ricerca
   *
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);

    boolean isInSessione = true;
    if (obj == null) {
      isInSessione = false;
      obj = new GestioneTab();
    }

    GestioneTab gestoreTab = (GestioneTab) obj;
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_ARGOMENTI);
    gestoreTab.setTabSelezionabili(null);

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }
}