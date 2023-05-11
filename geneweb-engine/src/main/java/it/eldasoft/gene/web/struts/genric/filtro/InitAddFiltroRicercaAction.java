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
package it.eldasoft.gene.web.struts.genric.filtro;

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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per l'apertura della pagina per l'aggiunta di un filtro ad una ricerca:
 * caricamento dell'elenco degli operatori, delle tabelle visibili nella ricerca
 * e dei relativi campi.
 *
 * @author Luca Giacomazzo
 */
public class InitAddFiltroRicercaAction extends AbstractActionBaseGenRicerche {

  private static final String ERROR_ADD_FORWARD = "errorInitAdd";

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(InitAddFiltroRicercaAction.class);

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
   * Inserisce nella request l'elenco degli operatori, delle tabelle e l'elenco dei campi
   * associati a tali tabelle per inserire un filtro.
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

    //Nel caso in cui si verifichi un errore in fase di inserimento di un filtro,
    //si ritorna alla medesima pagina, caricando il form
    FiltroRicercaForm filtro = (FiltroRicercaForm) request.getAttribute("filtroRicercaForm");

    if(elencoTabelleForm.size() > 0){
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
        // SS 06/11/2006: inserita estrazione mnemonici filtrati per visibilità
        // nelle ricerche
        elencoMnemoniciCampi = tabella.getMnemoniciCampiPerRicerche();
        elencoCampi = new Vector<Campo>(elencoMnemoniciCampi.size());
        for (int j = 0; j < elencoMnemoniciCampi.size(); j++) {
          mnemonicoCampo = elencoMnemoniciCampi.get(j);
          campo = dizCampi.get(mnemonicoCampo);
          if (gestoreVisibilita.checkCampoVisibile(campo, profiloAttivo))
            elencoCampi.addElement(campo);
        }
        if(elencoCampi.size() > 0){
          request.setAttribute("elencoCampi" + tabellaForm.getNomeTabellaUnivoco(),
              elencoCampi);
          elencoTabelle.addElement(tabellaForm);
        }
      }

      request.setAttribute("elencoTabelle", elencoTabelle);
      request.setAttribute("elencoOperatori", CostantiGenRicerche.CBX_OPERATORI_VALUE);
      request.setAttribute("elencoOperatoriLabel", CostantiGenRicerche.CBX_OPERATORI_LABEL);
      //Il codice del parametro viene definito pari a: 'PARAMETRO' + (n. dei parametri definiti + 1)
      request.setAttribute("parametroConfronto", CostantiGenRicerche.CODICE_PARAMETRO + (contenitore.getNumeroParametri()+1));

      request.setAttribute("listaParametriConfronto", contenitore.getElencoParametri());

      if(filtro != null){
        request.setAttribute("filtroRicercaForm", filtro);
      }

      this.setMenuTab(request);

      if (StringUtils.isNotEmpty((String) request.getSession().getAttribute(
          CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO))) {
        request.setAttribute("isAssociazioneUffIntAbilitata", "true");
      } else {
        request.setAttribute("isAssociazioneUffIntAbilitata", "false");
      }
    } else {
      // Nessuna tabella definita, quindi impossibile definire alcuna
      // condizione di filtro  per la ricerca
      target = InitAddFiltroRicercaAction.ERROR_ADD_FORWARD;
      String messageKey = null;
      if(contenitore.getNumeroTabelle() == 0)
        messageKey = "errors.genRic.noArgDefFiltri";
      else
        messageKey = "errors.genRic.noArgVisFiltri";
      if(logger.isDebugEnabled())
        logger.debug(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }

    //set nel request del parameter per disabilitare la navigazione in fase di editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA, CostantiGenerali.DISABILITA_NAVIGAZIONE);

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
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_FILTRI);
    gestoreTab.setTabSelezionabili(null);

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }
}