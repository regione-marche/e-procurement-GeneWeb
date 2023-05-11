/*
 * Created on 13-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.ordinamento;

import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per l'inserimento di un nuovo ordinamento in una ricerca o la modifica
 * di un ordinamento esistente
 * 
 * @author Luca Giacomazzo
 */
public class SalvaOrdinamentoRicercaAction extends
    AbstractDispatchActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(SalvaOrdinamentoRicercaAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere alla action insert
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniInsert() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Action per l'insert di un nuovo ordinamento in una ricerca
   */
  public ActionForward insert(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    OrdinamentoRicercaForm ordinamentoRicercaForm = (OrdinamentoRicercaForm) form;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    // Il campo risulta essere usato per la prima volta, quindi posso
    // considerarlo per
    // inserire un nuovo ordinanmento nell'elencoOrdinamenti presente in
    // sessione
    TabellaRicercaForm tabellaRicercaForm = null;
    for (int i = 0; i < contenitore.getNumeroTabelle(); i++) {
      tabellaRicercaForm = contenitore.estraiTabella(i);
      if (tabellaRicercaForm.getAliasTabella().equals(
          ordinamentoRicercaForm.getAliasTabella())) break;
    }

    Campo campo = dizCampi.get(ordinamentoRicercaForm.getMnemonicoCampo());
    // si impostano le informazioni relative alla tabella
    ordinamentoRicercaForm.setMnemonicoTabella(tabellaRicercaForm.getMnemonicoTabella());
    ordinamentoRicercaForm.setDescrizioneTabella(tabellaRicercaForm.getDescrizioneTabella());
    ordinamentoRicercaForm.setDescrizioneCampo(dizCampi.get(
        campo.getCodiceMnemonico()).getDescrizione());
    // gli attributi mnemonicoCampo, mnemonicoTabella e ordinamento sono già
    // popolati

    // si aggiunge il campo all'elenco
    contenitore.aggiungiOrdinamento(ordinamentoRicercaForm);
    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request);

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Action per l'update di un ordinamento in una ricerca
   */
  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    OrdinamentoRicercaForm ordinamento = (OrdinamentoRicercaForm) form;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    OrdinamentoRicercaForm ordinamentoRicercaForm = contenitore.estraiOrdinamento(Integer.parseInt(ordinamento.getProgressivo()));

    Campo campo = dizCampi.get(ordinamento.getMnemonicoCampo());

    // si impostano le informazioni relative alla tabella

    ordinamentoRicercaForm.setMnemonicoCampo(ordinamento.getMnemonicoCampo());
    ordinamentoRicercaForm.setDescrizioneCampo(campo.getDescrizione());
    ordinamentoRicercaForm.setOrdinamento(ordinamento.getOrdinamento());
    // gli attributi mnemonicoCampo, mnemonicoTabella e ordinamento sono già
    // popolati

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request);

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menu a tab in fase di visualizzazione del dettaglio della lista degli
   * ordinamenti.
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
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_ORDINAMENTI);
    gestoreTab.setTabSelezionabili(new String[] {
        CostantiGenRicerche.TAB_DATI_GENERALI, CostantiGenRicerche.TAB_GRUPPI,
        CostantiGenRicerche.TAB_ARGOMENTI, CostantiGenRicerche.TAB_CAMPI,
        CostantiGenRicerche.TAB_JOIN, CostantiGenRicerche.TAB_PARAMETRI,
        CostantiGenRicerche.TAB_FILTRI,CostantiGenRicerche.TAB_LAYOUT });

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }
}