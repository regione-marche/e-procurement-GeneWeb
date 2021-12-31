/*
 * Created on 26-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.base;

import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicercheBase;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.campo.CampiRicercaForm;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
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
 * Azione che gestisce il salvataggio in sessione dei nuovi campi inseriti nella
 * ricerca base
 * 
 * @author Luca.Giacomazzo
 */
public class AddElencoCampiRicercaBaseAction extends AbstractActionBaseGenRicercheBase {

  private static final String SEPARATORE_TABELLA_CAMPO = ".";

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(AddElencoCampiRicercaBaseAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere alla action runAction
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
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
    String target = CostantiGeneraliStruts.FORWARD_OK;

    CampiRicercaForm campiRicercaForm = (CampiRicercaForm) form;

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    String[] elencoCampi = campiRicercaForm.getCampiSelezionati();
    TabellaRicercaForm tabellaRicercaForm = null;
    String nomeTabellaUnivoco = null;
    String mnemonicoCampo = null;
    CampoRicercaForm campoRicercaForm = null;

    // una query non può aver più di un certo numero di colonne; nel generatore
    // ricerche è concesso un massimo di 100 colonne da estrarre
    if (contenitore.getNumeroCampi() + elencoCampi.length > 100) {
      target = "overflowNumeroColonne";
      String messageKey = "errors.genRic.campi.overflowNumeroCampi";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);

    } else {
      for (int i = 0; i < elencoCampi.length; i++) {
        // split dell'identificativo che arriva dalla pagina
        nomeTabellaUnivoco = elencoCampi[i].substring(0,
            elencoCampi[i].indexOf(SEPARATORE_TABELLA_CAMPO));
        mnemonicoCampo = elencoCampi[i].substring(elencoCampi[i].indexOf(SEPARATORE_TABELLA_CAMPO) + 1);
        // ricerca della tabella associata
        for (int j = 0; j < contenitore.getNumeroTabelle(); j++) {
          tabellaRicercaForm = contenitore.estraiTabella(j);
          if (tabellaRicercaForm.getAliasTabella().equals(nomeTabellaUnivoco))
            break;
        }
        // inserimento del campo nel contenitore
        campoRicercaForm = new CampoRicercaForm();
        campoRicercaForm.setAliasTabella(tabellaRicercaForm.getAliasTabella());
        campoRicercaForm.setDescrizioneCampo(dizCampi.get(mnemonicoCampo).getDescrizione());
        campoRicercaForm.setMnemonicoCampo(mnemonicoCampo);
        // **********************************************************************
        // L.G. 23/02/2007 
        // Al momento dell'inserimento del campo si setta per default come titolo
        // della colonna la descrizione del campo stesso presente in C0CAMPI
        // **********************************************************************
        campoRicercaForm.setTitoloColonna(dizCampi.get(mnemonicoCampo).getDescrizione());
        
        contenitore.aggiungiCampo(campoRicercaForm);
      }

      this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
      this.setMenuTab(request);
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio dei Dati Generali di
   * una ricerca base
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
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_CAMPI);
    gestoreTab.setTabSelezionabili(new String[] {
        CostantiGenRicerche.TAB_DATI_GENERALI, CostantiGenRicerche.TAB_GRUPPI,
        CostantiGenRicerche.TAB_CAMPI, CostantiGenRicerche.TAB_FILTRI,
        CostantiGenRicerche.TAB_ORDINAMENTI, CostantiGenRicerche.TAB_LAYOUT});

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }
}
