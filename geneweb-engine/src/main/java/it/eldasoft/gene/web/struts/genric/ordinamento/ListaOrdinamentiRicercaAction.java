/*
 * Created on 21-ago-2006
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
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.ListaForm;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.utility.UtilityNumeri;
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
 * @author Stefano.Sabbadin
 */
public class ListaOrdinamentiRicercaAction extends
    AbstractDispatchActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ListaOrdinamentiRicercaAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }

  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("modifica: inizio metodo");

    // target di default
    String target = "apriModifica";

    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    // lettura dal request del progressivo del campo da editare
    int progressivo = UtilityNumeri.convertiIntero(request.getParameter("id")).intValue();

    OrdinamentoRicercaForm ordinamento = contenitore.estraiOrdinamento(progressivo);
    // si valorizza la descrizione tabella
    Tabella tabella = dizTabelle.get(ordinamento.getMnemonicoTabella());
    ordinamento.setDescrizioneTabella(tabella.getDescrizione());

    List<String> elencoMnemoniciCampi = this.getMnemoniciCampiDaTabella(tabella);
    String mnemonicoCampo = null;

    // Dall'elenco dei mnemonici dei campi rimuovi quelli usati per gli
    // ordinamenti preesistenti, a meno di quello che vado a modificare
    if (contenitore.getNumeroOrdinamenti() > 0) {
      Vector<OrdinamentoRicercaForm> elencoOrdinamentiEsistenti = contenitore.getElencoOrdinamenti();
      for (int j = 0; j < elencoOrdinamentiEsistenti.size(); j++) {
        OrdinamentoRicercaForm tmpOrdinamento = (OrdinamentoRicercaForm) elencoOrdinamentiEsistenti.get(j);
        boolean campoUsatoPerOrdinamento = false;
        for (int i = elencoMnemoniciCampi.size() - 1; i >= 0
            && !campoUsatoPerOrdinamento; i--) {
          mnemonicoCampo = elencoMnemoniciCampi.get(i);
          if (!mnemonicoCampo.equals(ordinamento.getMnemonicoCampo())
              && mnemonicoCampo.equals(tmpOrdinamento.getMnemonicoCampo())) {
            elencoMnemoniciCampi.remove(i);
            campoUsatoPerOrdinamento = true;
          }
        }
      }
    }

    mnemonicoCampo = null;
    // si cicla sull'elenco delle colonne della tabella e si crea tale elenco da
    // inserire nel request
    Vector<OrdinamentoRicercaForm> elencoCampi = new Vector<OrdinamentoRicercaForm>();

    Campo c = null;
    CampoRicercaForm campoForm = null;
    OrdinamentoRicercaForm orf = null;

    for (int j = 0; j < elencoMnemoniciCampi.size(); j++) {
      mnemonicoCampo = elencoMnemoniciCampi.get(j);
      c = dizCampi.get(mnemonicoCampo);
      for (int z = 0; z < contenitore.getNumeroCampi(); z++) {
        campoForm = contenitore.estraiCampo(z);
        if ((ordinamento.getAliasTabella().equals(campoForm.getAliasTabella()))
            && (c.getCodiceMnemonico().equals(campoForm.getMnemonicoCampo()))
            && (UtilityStringhe.convertiStringaVuotaInNull(campoForm.getFunzione()) == null)) {
          orf = new OrdinamentoRicercaForm();
          // per questione di comodità si invia alla pagina campi di tipo
          // OrdinamentoRicercaForm esclusivamente perchè possiedono i metodi
          // per la generazione del value e del text per elementi di una
          // combobox
          orf.setMnemonicoCampo(c.getCodiceMnemonico());
          orf.setDescrizioneCampo(c.getCodiceMnemonico()
              + " - "
              + c.getDescrizione());
          elencoCampi.addElement(orf);
        }
      }
    }

    request.setAttribute("ordinamentoRicercaForm", ordinamento);
    request.setAttribute("elencoCampi", elencoCampi);

    // set nel request del parameter per disabilitare la navigazione in fase di
    // editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

    this.setMenuTab(request, target);

    if (logger.isDebugEnabled()) logger.debug("modifica: fine metodo");

    return mapping.findForward(target);
  }

  protected List<String> getMnemoniciCampiDaTabella(Tabella tabella) {
    return tabella.getMnemoniciCampiPerRicerche();
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action elimina
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniElimina() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }

  public ActionForward elimina(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("elimina: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String id = request.getParameter("id");
    contenitore.eliminaOrdinamento(Integer.parseInt(id));

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request, target);
    if (logger.isDebugEnabled()) logger.debug("elimina: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * eliminaMultiplo
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEliminaMultiplo() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }

  public ActionForward eliminaMultiplo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("eliminaMultiplo: inizio metodo");

    // target di default, da modificare nel momento in cui si verificano degli
    // errori
    String target = CostantiGeneraliStruts.FORWARD_OK;

    ListaForm filtri = (ListaForm) form;

    // costruzione dell'elenco degli id delle ricerche da rimuovere
    String id[] = filtri.getId();

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    for (int i = id.length-1; i >= 0 ; i--)
      contenitore.eliminaOrdinamento(Integer.parseInt(id[i]));

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request, target);
    if (logger.isDebugEnabled()) logger.debug("eliminaMultiplo: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaSu
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaSu() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }

  public ActionForward spostaSu(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("spostaSu: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    int id = Integer.parseInt(request.getParameter("id"));
    contenitore.spostaOrdinamento(id, id - 1);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request, target);
    if (logger.isDebugEnabled()) logger.debug("spostaSu: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaSu
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaGiu() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }

  public ActionForward spostaGiu(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("spostaGiu: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    int id = Integer.parseInt(request.getParameter("id"));
    contenitore.spostaOrdinamento(id, id + 1);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request, target);
    if (logger.isDebugEnabled()) logger.debug("spostaGiu: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * spostaInPosizioneMarcata
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaInPosizioneMarcata() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }

  public ActionForward spostaInPosizioneMarcata(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("spostaInPosizioneMarcata: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String id1 = request.getParameter("id");
    String id2 = request.getParameter("idNew");
    contenitore.spostaOrdinamento(Integer.parseInt(id1), Integer.parseInt(id2));

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request, target);
    if (logger.isDebugEnabled())
      logger.debug("spostaInPosizioneMarcata: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio degli Ordinamenti di
   * una ricerca
   *
   * @param request
   */
  private void setMenuTab(HttpServletRequest request, String target) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);

    boolean isInSessione = true;
    if (obj == null) {
      isInSessione = false;
      obj = new GestioneTab();
    }

    GestioneTab gestoreTab = (GestioneTab) obj;
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_ORDINAMENTI);
    if (!"apriModifica".equals(target))
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiGenRicerche.TAB_DATI_GENERALI,
          CostantiGenRicerche.TAB_GRUPPI, CostantiGenRicerche.TAB_ARGOMENTI,
          CostantiGenRicerche.TAB_CAMPI, CostantiGenRicerche.TAB_JOIN,
          CostantiGenRicerche.TAB_PARAMETRI, CostantiGenRicerche.TAB_FILTRI,
          CostantiGenRicerche.TAB_LAYOUT });
    else
      gestoreTab.setTabSelezionabili(null);
    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }
}