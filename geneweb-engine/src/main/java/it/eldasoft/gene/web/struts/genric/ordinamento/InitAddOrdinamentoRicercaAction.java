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

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityStringhe;

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
 * Action per l'apertura della pagina per l'aggiunta di un ordinamento ad una
 * ricerca: caricamento dell'elenco delle tabelle visibili nella ricerca e dei
 * relativi campi.
 *
 * @author Luca Giacomazzo
 */
public class InitAddOrdinamentoRicercaAction extends
    AbstractActionBaseGenRicerche {

  private static final String ERROR_ADD_FORWARD = "errorInitAdd";

  /** Logger Log4J di classe */
  static Logger               logger            = Logger.getLogger(InitAddOrdinamentoRicercaAction.class);

  /**
   * Inserisce nella request l'elenco delle tabelle e l'elenco dei campi
   * associati a tali tabelle per inserire un ordinamento.
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
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    Vector<TabellaRicercaForm> elencoTabelleForm = contenitore.getElencoArgomentiVisibili();

    boolean continua = true;

    if (elencoTabelleForm.size() == 0) {
      // Nessuna tabella definita, quindi impossibile definire alcun
      // ordinamento per la ricerca
      target = InitAddOrdinamentoRicercaAction.ERROR_ADD_FORWARD;
      String messageKey = null;
      if (contenitore.getNumeroTabelle() == 0)
        messageKey = "errors.genRic.noArgDefOrdinamenti";
      else
        messageKey = "errors.genRic.noArgVisOrdinamenti";
      if (logger.isInfoEnabled())
        logger.info(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
      continua = false;
    }

    if (continua && contenitore.getNumeroCampi() == 0) {
      // nessun campo definito, quindi impossibile definire alcun ordinamento
      // per la ricerca
      target = InitAddOrdinamentoRicercaAction.ERROR_ADD_FORWARD;
      String messageKey = "errors.genRic.noCampiDefOrdinamenti";
      if (logger.isInfoEnabled())
        logger.info(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
      continua = false;
    }

    if (continua) {
      Vector<TabellaRicercaForm> elencoTabelle = new Vector<TabellaRicercaForm>();

      DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
      DizionarioCampi dizCampi = DizionarioCampi.getInstance();

      TabellaRicercaForm tabellaForm = null;
      CampoRicercaForm campoForm = null;
      String mnemonicoTabella = null;
      Tabella tabella = null;
      List<String> elencoMnemoniciCampi = null;
      Vector<Campo> elencoCampi = null;
      String mnemonicoCampo = null;
      Campo campo = null;

      // a partire dalle tabelle censite per la ricerca,
      // estrae dai dizionari l'elenco delle tabelle e dei campi,
      // creando una lista di tabelle, e una lista di campi per ogni
      // tabella, ognuna delle quali viene posta nel request sotto un
      // nome "elencoCampi"+nome della tabella dell'elenco dei campi
      for (int i = 0; i < elencoTabelleForm.size(); i++) {
        tabellaForm = (TabellaRicercaForm) elencoTabelleForm.elementAt(i);
        mnemonicoTabella = tabellaForm.getMnemonicoTabella();
        tabella = dizTabelle.get(mnemonicoTabella);
        // SS: 07/11/2006: si mostrano i mnemonici ordinati e solo quelli
        // estratti e relativi a funzioni non statistiche
        elencoMnemoniciCampi = tabella.getMnemoniciCampiPerRicerche();

        // Dall'elenco dei mnemonici dei campi rimuovi quelli usati per gli
        // ordinamenti preesistenti
        if(contenitore.getNumeroOrdinamenti() > 0){
          Vector<OrdinamentoRicercaForm> elencoOrdinamentiEsistenti = contenitore.getElencoOrdinamenti();
          for(int j = 0; j < elencoOrdinamentiEsistenti.size(); j++){
            OrdinamentoRicercaForm tmpOrdinamento = (OrdinamentoRicercaForm) elencoOrdinamentiEsistenti.get(j);
            boolean campoUsatoPerOrdinamento = false;
            for(int l= elencoMnemoniciCampi.size()-1; l >= 0 && !campoUsatoPerOrdinamento; l--){
              mnemonicoCampo = elencoMnemoniciCampi.get(l);
              if((tabellaForm.getAliasTabella().equals(tmpOrdinamento.getAliasTabella())) &&
                  mnemonicoCampo.equals(tmpOrdinamento.getMnemonicoCampo())){
                elencoMnemoniciCampi.remove(l);
                campoUsatoPerOrdinamento = true;
              }
            }
          }
        }

        elencoCampi = new Vector<Campo>();
        for (int j = 0; j < elencoMnemoniciCampi.size(); j++) {
          mnemonicoCampo = elencoMnemoniciCampi.get(j);
          campo = dizCampi.get(mnemonicoCampo);
          for (int z = 0; z < contenitore.getNumeroCampi(); z++) {
            campoForm = contenitore.estraiCampo(z);
            if ((tabellaForm.getAliasTabella().equals(campoForm.getAliasTabella()))
                && (campo.getCodiceMnemonico().equals(campoForm.getMnemonicoCampo()))
                && (UtilityStringhe.convertiStringaVuotaInNull(campoForm.getFunzione()) == null)) {
              elencoCampi.addElement(campo);
            }
          }
        }
        request.setAttribute("elencoCampi"
            + tabellaForm.getNomeTabellaUnivoco(), elencoCampi);
        if (elencoCampi.size() > 0) elencoTabelle.addElement(tabellaForm);
      }

      request.setAttribute("elencoTabelle", elencoTabelle);

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);
      this.setMenuTab(request);
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
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_ORDINAMENTI);
    gestoreTab.setTabSelezionabili(null);

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }
}