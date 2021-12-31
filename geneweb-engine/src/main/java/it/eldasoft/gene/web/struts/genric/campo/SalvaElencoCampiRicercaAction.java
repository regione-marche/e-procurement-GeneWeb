/*
 * Created on 12-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.campo;

import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;

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
 * Action per l'inserimento di un elenco di campi in una ricerca
 * 
 * @author Stefano.Sabbadin
 */
public class SalvaElencoCampiRicercaAction extends
    AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(SalvaElencoCampiRicercaAction.class);

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

    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    CampiRicercaForm campiRicercaForm = (CampiRicercaForm) form;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String[] idCampiSelezionati = campiRicercaForm.getCampiSelezionati();
    String id = null;
    String aliasTabella = null;
    String mneCampo = null;
    TabellaRicercaForm tabella = null;
    CampoRicercaForm campo = null;

    // una query non può aver più di un certo numero di colonne; nel generatore
    // ricerche è concesso un massimo di 100 colonne da estrarre
    if (contenitore.getNumeroCampi() + idCampiSelezionati.length > 100) {
      target = "overflowNumeroColonne";
      String messageKey = "errors.genRic.campi.overflowNumeroCampi";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);

    } else {

      for (int i = 0; i < idCampiSelezionati.length; i++) {
        // il campo ricevuto lo si splitta per ottenere l'alias tabella e il
        // mnemonico campo
        id = idCampiSelezionati[i];
        String[] array = id.split("\\.");
        aliasTabella = array[0];
        mneCampo = array[1];
        // si impostano le informazioni relative al campo da inserire
        campo = new CampoRicercaForm();
        campo.setAliasTabella(aliasTabella);
        for (int j = 0; j < contenitore.getNumeroTabelle(); j++) {
          tabella = contenitore.estraiTabella(j);
          if (tabella.getAliasTabella().equals(aliasTabella)) {
            campo.setDescrizioneTabella(tabella.getDescrizioneTabella());
            campo.setMnemonicoTabella(tabella.getMnemonicoTabella());
          }
        }
        campo.setMnemonicoCampo(mneCampo);
        campo.setDescrizioneCampo(dizCampi.get(mneCampo).getDescrizioneBreve());
        // **********************************************************************
        // L.G. 23/02/2007 
        // Al momento dell'inserimento del campo si setta per default come titolo
        // della colonna la descrizione del campo stesso presente in C0CAMPI
        // **********************************************************************
        campo.setTitoloColonna(dizCampi.get(mneCampo).getDescrizione());

        // si aggiunge il campo all'elenco
        contenitore.aggiungiCampo(campo);
      }

      this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
      this.setMenuTab(request);
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menu a tab in fase di visualizzazione del dettaglio dei Dati Generali
   * di una ricerca
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
        CostantiGenRicerche.TAB_ARGOMENTI, CostantiGenRicerche.TAB_JOIN,
        CostantiGenRicerche.TAB_PARAMETRI, CostantiGenRicerche.TAB_FILTRI,
        CostantiGenRicerche.TAB_ORDINAMENTI, CostantiGenRicerche.TAB_LAYOUT});

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }

}
