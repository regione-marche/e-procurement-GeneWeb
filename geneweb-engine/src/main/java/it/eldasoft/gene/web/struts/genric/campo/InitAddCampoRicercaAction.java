/*
 * Created on 1-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.campo;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.genric.GestoreVisibilitaDati;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;

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
 * Azione che gestisce la predisposizione dei dati da utilizzare nella pagina
 * per l'inserimento di un singolo campo o l'inserimento multiplo di campi nella
 * ricerca
 *
 * @author Stefano.Sabbadin
 */
public class InitAddCampoRicercaAction extends AbstractActionBaseGenRicerche {

  private final String ERROR_ADD_FORWARD = "errorInitAdd";

  /** Logger Log4J di classe */
  static Logger        logger            = Logger.getLogger(InitAddCampoRicercaAction.class);

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
   * Inserisce nella request l'elenco delle tabelle e l'elenco dei campi
   * associati a tali tabelle per eseguire un inserimento di uno o più campi.
   * Questa classe nasce per essere utilizzata in due azioni/eventi diversi,
   * ovvero l'inserimento di un singolo campo e l'inserimento di n campi
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

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
            sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    Vector<TabellaRicercaForm> elencoTabelleForm = contenitore.getElencoArgomentiVisibili();

    if (elencoTabelleForm.size() > 0) {
      Vector<TabellaRicercaForm> elencoTabelle = new Vector<TabellaRicercaForm>();

      DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
      DizionarioCampi dizCampi = DizionarioCampi.getInstance();

      TabellaRicercaForm tabellaForm = null;
      String mnemonicoTabella = null;
      Tabella tabella = null;
      List<String> elencoMnemoniciCampi = null;
      Vector<Campo> elencoCampi = null;
      String mnemonicoCampo = null;
      Campo campo = null;

      GestoreVisibilitaDati gestoreVisibilita = this.geneManager.getGestoreVisibilitaDati();
      String profiloAttivo = (String) sessione.getAttribute(
                CostantiGenerali.PROFILO_ATTIVO);

      // a partire dalle tabelle censite per la ricerca,
      // estrae dai dizionari l'elenco delle tabelle e dei campi,
      // creando una lista di tabelle, e una lista di campi per ogni
      // tabella, ognuna delle quali viene posta nel request sotto un
      // nome "elencoCampi"+nome della tabella dell'elenco dei campi
      for (int i = 0; i < elencoTabelleForm.size(); i++) {
        tabellaForm = (TabellaRicercaForm) elencoTabelleForm.elementAt(i);
        mnemonicoTabella = tabellaForm.getMnemonicoTabella();
        tabella = dizTabelle.get(mnemonicoTabella);
        elencoTabelle.addElement(tabellaForm);
        // SS 25/10/2006: inserita estrazione mnemonici filtrati per visibilità
        // nelle ricerche
        elencoMnemoniciCampi = tabella.getMnemoniciCampiPerRicerche();
        elencoCampi = new Vector<Campo>(elencoMnemoniciCampi.size());
        for (int j = 0; j < elencoMnemoniciCampi.size(); j++) {
          mnemonicoCampo = elencoMnemoniciCampi.get(j);
          campo = dizCampi.get(mnemonicoCampo);
          if (gestoreVisibilita.checkCampoVisibile(campo, profiloAttivo))
            elencoCampi.addElement(campo);
        }
        request.setAttribute("elencoCampi" + tabellaForm.getAliasTabella(),
            elencoCampi);
      }

      request.setAttribute("elencoTabelle", elencoTabelle);

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      this.setMenuTab(request);
    } else {
      // Nessuna tabella definita o non visibile, quindi impossibile definire
      // alcun campo per la ricerca
      target = this.ERROR_ADD_FORWARD;
      String messageKey = null;
      if (contenitore.getNumeroTabelle() == 0)
        messageKey = "errors.genRic.noArgDefCampi";
      else
        messageKey = "errors.genRic.noArgVisCampi";
      if (logger.isDebugEnabled())
        logger.debug(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }
    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio dei Dati Generali di
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
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_CAMPI);
    gestoreTab.setTabSelezionabili(null);

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }

}