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
package it.eldasoft.gene.web.struts.genric.filtro;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.genric.GestoreVisibilitaDati;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.ListaForm;
import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.parametro.ParametroRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.cache.DizionarioLivelli;
import it.eldasoft.utils.utility.UtilityStringhe;

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
 * DispatchAction per la gestione di tutte le azioni che possono essere lanciate
 * dalla pagina 'Lista Filtri'
 *
 * @author Luca.Giacomazzo
 */
public class ListaFiltriRicercaAction extends
    AbstractDispatchActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger       logger = Logger.getLogger(ListaFiltriRicercaAction.class);

  /**
   * Reference al Manager per la gestione delle protezioni di tabelle e campi
   * rispetto al profilo attivo
   */
  private GeneManager geneManager;

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("modifica: inizio metodo");

    // target di default
    String target = "apriModifica";
    String id = request.getParameter("id");

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    FiltroRicercaForm filtro = null;
    if (id != null)
      filtro = contenitore.estraiFiltro(Integer.parseInt(id));
    else
      filtro = (FiltroRicercaForm) request.getAttribute("filtroRicercaForm");

    Vector<TabellaRicercaForm> elencoTabelleForm = contenitore.getElencoArgomentiVisibili();
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
    String profiloAttivo = (String) sessione.getAttribute(CostantiGenerali.PROFILO_ATTIVO);

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
        if (gestoreVisibilita.checkCampoVisibile(campo, profiloAttivo)) {
          elencoCampi.addElement(campo);
        }
      }
      if (elencoCampi.size() > 0) {
        request.setAttribute("elencoCampi"
            + tabellaForm.getNomeTabellaUnivoco(), elencoCampi);
        elencoTabelle.addElement(tabellaForm);
      }
    }

    request.setAttribute("elencoTabelle", elencoTabelle);
    request.setAttribute("elencoOperatori",
        CostantiGenRicerche.CBX_OPERATORI_VALUE);
    request.setAttribute("elencoOperatoriLabel",
        CostantiGenRicerche.CBX_OPERATORI_LABEL);
    request.setAttribute("elencoParametri", contenitore.getElencoParametri());
    request.setAttribute("filtroRicercaForm", filtro);
    request.setAttribute("elencoCampi", elencoCampi);

    if (StringUtils.isNotEmpty((String) request.getSession().getAttribute(
        CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO))) {
      request.setAttribute("isAssociazioneUffIntAbilitata", "true");
    } else {
      request.setAttribute("isAssociazioneUffIntAbilitata", "false");
    }
    
    // set nel request del parameter per disabilitare la navigazione in fase di
    // editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

    this.setMenuTab(request, target);

    if (logger.isDebugEnabled()) logger.debug("modifica: fine metodo");

    return mapping.findForward(target);
  }

  public ActionForward elimina(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("elimina: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String id = request.getParameter("id");

    // FiltroRicercaForm filtro =
    // contenitore.estraiFiltro(Integer.parseInt(id));
    // // eliminazione del parametro, se presente, e utilizzato solo in questo
    // // filtro
    // if
    // (FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO.equals(filtro.getTipoConfronto())
    // && filtro.getParametroConfronto() != null) {
    // String codiceParametro = filtro.getParametroConfronto();
    // short contatoreUtilizzi = 0;
    // FiltroRicercaForm filtroTmp = null;
    // // verifico quante volte viene utilizzato
    // for (int i = 0; i < contenitore.getNumeroFiltri(); i++) {
    // filtroTmp = contenitore.estraiFiltro(i);
    // if
    // (FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO.equals(filtroTmp.getTipoConfronto())
    // && codiceParametro.equals(filtroTmp.getParametroConfronto())) {
    // contatoreUtilizzi++;
    // }
    // }
    //
    // // se viene utilizzato una sola volta, ovvero nel filtro da eliminare,
    // // allora lo elimino
    // if (contatoreUtilizzi == 1) {
    // ParametroRicercaForm parametro = null;
    // int indiceParametroDaEliminare = 0;
    // boolean trovato = false;
    // while (indiceParametroDaEliminare < contenitore.getNumeroParametri()
    // && !trovato) {
    // parametro = contenitore.estraiParametro(indiceParametroDaEliminare);
    // if (codiceParametro.equals(parametro.getCodiceParametro())) {
    // trovato = true;
    // } else
    // indiceParametroDaEliminare++;
    // }
    // // per scrupolo si controlla il flag, ma sicuramente va trovato il
    // // parametro
    // if (trovato) {
    // if (logger.isDebugEnabled())
    // logger.debug("Si elimina anche il parametro associato al filtro da
    // eliminare");
    // contenitore.eliminaParametro(indiceParametroDaEliminare);
    // }
    // }
    // }
    //
    // contenitore.eliminaFiltro(Integer.parseInt(id));

    this.eliminaFiltro(contenitore, id);
    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request, target);
    if (logger.isDebugEnabled()) logger.debug("elimina: fine metodo");

    return mapping.findForward(target);
  }

  private void eliminaFiltro(ContenitoreDatiRicercaForm contenitore, String id) {
    FiltroRicercaForm filtro = contenitore.estraiFiltro(Integer.parseInt(id));
    // eliminazione del parametro, se presente, e utilizzato solo in questo
    // filtro
    if (FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO.equals(filtro.getTipoConfronto())
        && filtro.getParametroConfronto() != null) {
      String codiceParametro = filtro.getParametroConfronto();
      short contatoreUtilizzi = 0;
      FiltroRicercaForm filtroTmp = null;
      // verifico quante volte viene utilizzato
      for (int i = 0; i < contenitore.getNumeroFiltri(); i++) {
        filtroTmp = contenitore.estraiFiltro(i);
        if (FiltroRicercaForm.TIPO_CONFRONTO_PARAMETRO.equals(filtroTmp.getTipoConfronto())
            && codiceParametro.equals(filtroTmp.getParametroConfronto())) {
          contatoreUtilizzi++;
        }
      }

      // se viene utilizzato una sola volta, ovvero nel filtro da eliminare,
      // allora lo elimino
      if (contatoreUtilizzi == 1) {
        ParametroRicercaForm parametro = null;
        int indiceParametroDaEliminare = 0;
        boolean trovato = false;
        while (indiceParametroDaEliminare < contenitore.getNumeroParametri()
            && !trovato) {
          parametro = contenitore.estraiParametro(indiceParametroDaEliminare);
          if (codiceParametro.equals(parametro.getCodiceParametro())) {
            trovato = true;
          } else
            indiceParametroDaEliminare++;
        }
        // per scrupolo si controlla il flag, ma sicuramente va trovato il
        // parametro
        if (trovato) {
          if (logger.isDebugEnabled())
            logger.debug("Si elimina anche il parametro associato al filtro da eliminare");
          contenitore.eliminaParametro(indiceParametroDaEliminare);
        }
      }
    }

    contenitore.eliminaFiltro(Integer.parseInt(id));
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

    for (int i = id.length-1; i >= 0; i--)
      this.eliminaFiltro(contenitore, id[i]);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request, target);
    if (logger.isDebugEnabled()) logger.debug("eliminaMultiplo: fine metodo");

    return mapping.findForward(target);
  }

  public ActionForward spostaSu(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    int id = Integer.parseInt(request.getParameter("id"));
    contenitore.spostaFiltro(id, id - 1);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request, target);
    return mapping.findForward(target);
  }

  public ActionForward spostaGiu(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    int id = Integer.parseInt(request.getParameter("id"));
    contenitore.spostaFiltro(id, id + 1);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request, target);
    return mapping.findForward(target);
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
    contenitore.spostaFiltro(Integer.parseInt(id1), Integer.parseInt(id2));

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request, target);
    if (logger.isDebugEnabled())
      logger.debug("spostaInPosizioneMarcata: fine metodo");

    return mapping.findForward(target);
  }

  public ActionForward filtroPerIdUtente(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("filtroPerIdUtente: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
        session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    DatiGenRicerca testata = contenitore.getTestata().getDatiPerModel();

    if (testata.getFiltroUtente() == 1) {
      // In questo caso si disattiva il filtro per id Utente precedentemente
      // attivato, quindi non è necessario effettuare alcun controllo
      contenitore.getTestata().setFiltroUtente(false);
      this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    } else {
      // In questo caso si attiva il filtro per id utente: si controlla se nella
      // lista della tabelle selezionate per la ricerca ne esiste almeno una che
      // e' in relazione con l'id utente. Se si, allora setto l'attributo della
      // testata filtroIdUtente a true, altrimenti invio al client un messaggio
      // che spiega l'impossibilita' di applicare tale filtro
      DizionarioLivelli dizLivelli = DizionarioLivelli.getInstance();
      boolean esisteTabellaInRelazioneIdUtente = false;
      for (int i = 0; i < contenitore.getNumeroTabelle() && !esisteTabellaInRelazioneIdUtente; i++) {
        if (dizLivelli.isFiltroLivelloPresente(contenitore.estraiTabella(i).getNomeTabella())) {
            esisteTabellaInRelazioneIdUtente = true;
        }
      }
      if (esisteTabellaInRelazioneIdUtente) {
        contenitore.getTestata().setFiltroUtente(true);
        this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
      } else {
        String messageKey = "warnings.genRic.filtroIdUtente.nonAttivabile";
        if (logger.isDebugEnabled()) {
          logger.debug(messageKey);
        }
        this.aggiungiMessaggio(request, messageKey);
      }
    }

    if (StringUtils.isNotEmpty((String) request.getSession().getAttribute(
        CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO))) {
      request.setAttribute("isAssociazioneUffIntAbilitata", "true");
    } else {
      request.setAttribute("isAssociazioneUffIntAbilitata", "false");
    }
    
    this.setMenuTab(request, target);

    if (logger.isDebugEnabled()) {
      logger.debug("filtroPerIdUtente: fine metodo");
    }
    return mapping.findForward(target);
  }

  public ActionForward filtroPerUfficioIntestatario(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("filtroPerUfficioIntestatario: inizio metodo");
    }
    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
        request.getSession().getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    DatiGenRicerca testata = contenitore.getTestata().getDatiPerModel();

    //if (testata.getFiltroUfficioIntestatarioEscluso() == 0) {
    if (testata.getFiltroUfficioIntestatario() == 1) {
      // In questo caso si esclude il filtro per ufficio intestatario
      // precedentemente attivato, quindi non è necessario effettuare alcun controllo
      //contenitore.getTestata().setFiltroUfficioIntestatarioEscluso(true);
      contenitore.getTestata().setFiltroUfficioIntestatario(false);
      this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    } else {
      // In questo caso si attiva il filtro per ufficio intestatario: si controlla
      // se nella lista della tabelle selezionate per la ricerca e visibili ne
      // esiste almeno una che e' in relazione con la tabella UFFINT. Se si,
      // allora setto l'attributo della testata filtroUfficioIntestatario a false,
      // altrimenti invio al client un messaggio che spiega l'impossibilita'
      // di applicare tale filtro.
      
      if (contenitore.getElencoArgomentiVisibili().size() > 0) {
        DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
        Vector<TabellaRicercaForm> elencoTabelle = contenitore.getElencoArgomentiVisibili();
        boolean esisteTabellaInRelazioneUffInt = false;

        for (int i = 0; i < contenitore.getNumeroTabelle() && !esisteTabellaInRelazioneUffInt; i++) {
          TabellaRicercaForm tabellaRic = elencoTabelle.get(i);
          if (dizTabelle.getDaNomeTabella("UFFINT").getLegameTabelle(tabellaRic.getNomeTabella()).length > 0) {
            esisteTabellaInRelazioneUffInt = true;
          }
        }
        if (esisteTabellaInRelazioneUffInt) {
          //contenitore.getTestata().setFiltroUfficioIntestatarioEscluso(false);
          contenitore.getTestata().setFiltroUfficioIntestatario(true);
          this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
        } else {
          String messageKey = "warnings.genRic.filtroUfficioIntestatario.nonAttivabile";
          String labelUffInt = StringUtils.lowerCase(
              this.resBundleGenerale.getString("label.tags.uffint.singolo"));
          
          if (logger.isDebugEnabled()) {
            logger.debug(UtilityStringhe.replaceParametriMessageBundle(
                this.resBundleGenerale.getString(messageKey),
                new String[] { labelUffInt }));
          }
          this.aggiungiMessaggio(request, messageKey, labelUffInt);
        }
      } else {
        String messageKey = null;
        if (contenitore.getNumeroTabelle() == 0) {
          messageKey = "errors.genRic.noArgDefFiltri";
        } else {
          messageKey = "errors.genRic.noArgVisFiltri";
        }
        this.aggiungiMessaggio(request, messageKey);
        
        if (logger.isDebugEnabled()) {
          logger.debug(this.resBundleGenerale.getString(messageKey));
        }
      }
    }

    if (StringUtils.isNotEmpty((String) request.getSession().getAttribute(
        CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO))) {
      request.setAttribute("isAssociazioneUffIntAbilitata", "true");
    } else {
      request.setAttribute("isAssociazioneUffIntAbilitata", "false");
    }
    
    this.setMenuTab(request, target);

    if (logger.isDebugEnabled()) {
      logger.debug("filtroPerUfficioIntestatario: fine metodo");
    }
    return mapping.findForward(target);
  }
  
  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio dei Filtri di una
   * ricerca
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
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_FILTRI);
    if (!"apriModifica".equals(target)) {
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiGenRicerche.TAB_DATI_GENERALI,
          CostantiGenRicerche.TAB_GRUPPI, CostantiGenRicerche.TAB_ARGOMENTI,
          CostantiGenRicerche.TAB_CAMPI, CostantiGenRicerche.TAB_JOIN,
          CostantiGenRicerche.TAB_PARAMETRI,
          CostantiGenRicerche.TAB_ORDINAMENTI, CostantiGenRicerche.TAB_LAYOUT });
    } else {
      gestoreTab.setTabSelezionabili(null);
    }
    if (!isInSessione) {
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
    }
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
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

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaSu
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaSu() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaGiu
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaGiu() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
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

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * filtroPerIdUtente
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniFiltroPerIdUtente() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * filtroPerUfficioIntestatario
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniFiltroPerUfficioIntestatario() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
}