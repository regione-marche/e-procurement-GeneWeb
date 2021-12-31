/*
 * Created on 16-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.gene.web.struts.genric.prospetto;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.bl.genric.ProspettoManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.dao.QueryDaoException;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.db.domain.genric.DatiGenProspetto;
import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.gene.db.domain.genric.ParametroRicerca;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Ridefinizione della Action CheckParametriModelloAction per la verifica se la
 * ricerca con modello in analisi che si vuole eseguire ha dei parametri
 * definiti o meno
 *
 * @author Luca.Giacomazzo
 */
public class CheckParametriProspettoAction extends ActionBaseNoOpzioni {

  private final String       TORNA_A_LISTA_RICERCHE_PREDEFINITE = "listaReportPredefiniti";
  private final String       TORNA_A_LISTA_RICERCHE             = "listaRicerche";
  private final String       TORNA_A_DETTAGLIO_RICERCA          = "dettaglioProspetto";

  /** logger della classe */
  static Logger              logger                             = Logger.getLogger(CheckParametriProspettoAction.class);

  /** Manager dei modelli */
  protected ModelliManager   modelliManager;

  /** Manager delle ricerche con modello */
  protected ProspettoManager prospettoManager;

  /** Manager dei tabellati */
  protected TabellatiManager tabellatiManager;

  /**
   * Reference alla classe di business logic che gestisce tutte le funzionalita'
   * di base di AL
   */
  private GeneManager        geneManager;

  /** Manager delle ricerche */
  protected RicercheManager  ricercheManager;

  /**
   * @param modelliManager
   *        The modelliManager to set.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * @param prospettoManager
   *        prospettoManager da settare internamente alla classe.
   */
  public void setProspettoManager(ProspettoManager prospettoManager) {
    this.prospettoManager = prospettoManager;
  }

  /**
   * @param tabellatiManager
   *        The tabellatiManager to set.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /*
   * se la action è comune decommentare il codice protected CheckOpzioniUtente
   * getOpzioniRunAction() { return new CheckOpzioniUtente(""); }
   */
  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = "componiModello";
    String messageKey = null;

    int idProspetto = 0;

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    String codiceProfilo = (String) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);

    try {
      // estrazione id prospetto
      String tmpId = request.getParameter("idRicerca");
      if (tmpId != null)
        idProspetto = Integer.parseInt(tmpId);
      else
        idProspetto = ((Integer) request.getAttribute("idRicerca")).intValue();

      // estrazione pagina di partenza, per eventuali ritorni in caso di errore
      String fromPage = request.getParameter("fromPage");
      if (fromPage == null)
        fromPage = (String) request.getAttribute("fromPage");

      // estrazione del report con modello
      DatiGenProspetto datiGenProspetto = this.prospettoManager.getProspettoById(idProspetto);

      if (datiGenProspetto.getDatiModello().getIdRicercaSrc() == null) {
        target = processaModelloStandard(request, target, idProspetto,
            profiloUtente, codiceProfilo, fromPage, datiGenProspetto);
      } else {
        target = processaModelloBasatoSuUnaRicerca(request, target,
            idProspetto,
            datiGenProspetto.getDatiModello().getIdRicercaSrc().intValue(),
            profiloUtente, codiceProfilo, fromPage, datiGenProspetto);
      }

    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);

    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Processa i report con modello standard, verificando se la tabella di
   * partenza è visibile da profilo, se esistono dati nella tabella, e se i
   * parametri tabellati sono basati su campi visibili da profilo.<br>
   * In tal caso si inseriscono i dati nel request per la gestione
   * dell'inserimento dei parametri, altrimenti se non serve l'interazione
   * dell'utente o alcuni controlli non sono andati a buon fine allora si
   * gestisce la messaggistica e il redirezionamento alla pagina/azione corretta
   * per il caso.
   *
   * @param request
   * @param target
   * @param idProspetto
   * @param profiloUtente
   * @param codiceProfilo
   * @param fromPage
   * @param datiGenProspetto
   * @return target modificato
   * @throws SqlComposerException
   * @throws QueryDaoException
   */
  private String processaModelloStandard(HttpServletRequest request,
      String target, int idProspetto, ProfiloUtente profiloUtente,
      String codiceProfilo, String fromPage, DatiGenProspetto datiGenProspetto)
      throws SqlComposerException, QueryDaoException {

    String messageKey = null;
    ParametriProspettoForm parametriProspettoForm = null;
    int idModello;
    boolean noErrori = true;
    Vector<Object> listaValori = new Vector<Object>();

    if (this.isReportConProspettoEseguibile(request,
        datiGenProspetto.getDatiGenRicerca())) {
      String[] valoriCampiChiave = this.prospettoManager.getChiavePrimoRecordEntitaPerCompositore(
          DizionarioTabelle.getInstance().getDaNomeTabella(
          datiGenProspetto.getDatiGenRicerca().getEntPrinc()));

      if (valoriCampiChiave != null) {
        if (StringUtils.isNotEmpty(valoriCampiChiave[0])) {
          // Inizializzazione dell'oggetto componiModelloForm
          parametriProspettoForm = this.setParametriProspettoForm(idProspetto,
              datiGenProspetto.getDatiModello(), valoriCampiChiave);
          parametriProspettoForm.setPaginaSorgente(fromPage);
  
          idModello = datiGenProspetto.getDatiModello().getIdModello();
  
          List<?> elencoParametri = this.modelliManager.getParametriModello(idModello);
          if (elencoParametri != null && elencoParametri.size() > 0) {
            if (elencoParametri.size() > 1
                || (elencoParametri.size() == 1 && !"U".equalsIgnoreCase(((ParametroModello) elencoParametri.get(0)).getTipo()))) {
              if (fromPage.equalsIgnoreCase("listaRicerche")
                  || fromPage.equalsIgnoreCase("dettaglioRicerca")) {
                target = "setParametri";
                parametriProspettoForm.setPaginaSorgente(fromPage + "-Parametro");
              } else {
                target = "setParametriPredefiniti";
                parametriProspettoForm.setPaginaSorgente("listaPredefinite-Parametro");
              }
              // Inserimento identificativo utente nel request per popolare il
              // parametro hidden nella lista dei parametri per comporre il
              // modello stesso
              request.setAttribute("idAccount",
                  new Integer(profiloUtente.getId()));
              request.setAttribute("listaParametri", elencoParametri);
  
              // cicliamo sulla lista dei parametri e per ogni parametro di tipo
              // Tabellato mettiamo
              // nel request la lista degli elementi, il nome della lista sarà
              // composto da "lista" + codice tabellato
              noErrori = this.addValoriParametriETabellati(request,
                  profiloUtente, codiceProfilo, parametriProspettoForm, noErrori,
                  listaValori, elencoParametri);
  
            } else if (elencoParametri.size() == 1
                && "U".equalsIgnoreCase(((ParametroModello) elencoParametri.get(0)).getTipo())) {
              parametriProspettoForm.setParametriModello(new String[] { ""
                  + profiloUtente.getId() });
              target = "salvaEComponi";
            }
            if (noErrori) parametriProspettoForm.setIdSessione(0);
          }
  
          if (noErrori) {
            if (target.indexOf("setParametri") >= 0) {
              request.setAttribute("parametriProspettoForm",
                  parametriProspettoForm);
              request.setAttribute("listaValori", listaValori);
            } else if (target.indexOf("salvaEComponi") >= 0)
              request.setAttribute("parametriModelloconIdUtenteForm",
                  parametriProspettoForm);
            else {
              request.setAttribute("componiModelloConIdUtenteForm",
                  parametriProspettoForm);
            }
  
            // Disabilito la navigazione dei vari menu'
            request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
                CostantiGenerali.DISABILITA_NAVIGAZIONE);
          } else {
            target = this.getDestinationTarget(request, idProspetto, fromPage);
          }
          request.setAttribute("idProspetto", new Integer(idProspetto));
        } else {
          messageKey = "errors.prospetti.eseguiProspetto.noDatiEntitaPrincipale";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
  
          target = this.getDestinationTarget(request, idProspetto, fromPage);
        }
      } else {
        messageKey = "errors.prospetti.eseguiProspetto.noCampiChiaveEntitaPrincipale";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);

        target = this.getDestinationTarget(request, idProspetto, fromPage);
      }
    } else {
      target = this.getDestinationTarget(request, idProspetto, fromPage);
    }
    return target;
  }

  /**
   * Processa i report con modello basato su un report base/avanzato come
   * sorgente dati.<br>
   * Si recuperano gli eventuali parametri del report sorgente dati, quindi i
   * parametri del modello vero e proprio, e si verifica se gli eventuali
   * parametri tabellati sono visibili da profilo.<br>
   * In tal caso si inseriscono i dati nel request per la gestione
   * dell'inserimento dei parametri, altrimenti se non serve l'interazione
   * dell'utente o alcuni controlli non sono andati a buon fine allora si
   * gestisce la messaggistica e il redirezionamento alla pagina/azione corretta
   * per il caso.
   *
   * @param request
   * @param target
   * @param idProspetto
   * @param idRicercaSrc
   * @param profiloUtente
   * @param codiceProfilo
   * @param fromPage
   * @param datiGenProspetto
   * @return target modificato
   * @throws SqlComposerException
   * @throws QueryDaoException
   */
  private String processaModelloBasatoSuUnaRicerca(HttpServletRequest request,
      String target, int idProspetto, int idRicercaSrc,
      ProfiloUtente profiloUtente, String codiceProfilo, String fromPage,
      DatiGenProspetto datiGenProspetto) throws SqlComposerException,
      QueryDaoException {

    ParametriProspettoForm parametriProspettoForm = null;
    boolean noErrori = true;
    Vector<Object> listaValori = new Vector<Object>();
    List<Object> elencoParametri = new ArrayList<Object>();

    int idModello = datiGenProspetto.getDatiModello().getIdModello();

    // Inizializzazione dell'oggetto componiModelloForm
    parametriProspettoForm = this.setParametriProspettoForm(idProspetto,
        datiGenProspetto.getDatiModello(), null);
    parametriProspettoForm.setPaginaSorgente(fromPage);

    // estrazione degli eventuali parametri della ricerca sorgente dati
    List<?> listaParametriRicerca = this.ricercheManager.getParametriRicerca(idRicercaSrc);

    // inserimento dei parametri della ricerca nell'elenco dei parametri
    // "modello" da richiedere all'utente
    if (listaParametriRicerca != null) {
      ParametroRicerca paramRicerca = null;
      ParametroModello paramModello = null;
      for (int i = 0; i < listaParametriRicerca.size(); i++) {
        paramRicerca = (ParametroRicerca) listaParametriRicerca.get(i);
        paramModello = paramRicerca.getParametroModello();
        paramModello.setIdModello(idModello);
        elencoParametri.add(paramModello);
      }
    }

    // a questo punto si estraggono i parametri del modello e si aggiungono
    // all'elenco parametri
    List<?> listaParametriModello = this.modelliManager.getParametriModello(idModello);
    for (int i = 0; i < listaParametriModello.size(); i++) {
      elencoParametri.add(listaParametriModello.get(i));
    }

    if (elencoParametri.size() > 0) {
      if (elencoParametri.size() > 1
          || (elencoParametri.size() == 1 && !"U".equalsIgnoreCase(((ParametroModello) elencoParametri.get(0)).getTipo()))) {
        // siamo nel caso di almeno un parametro da richeidere interattivamente
        if (fromPage.equalsIgnoreCase("listaRicerche")
            || fromPage.equalsIgnoreCase("dettaglioRicerca")) {
          target = "setParametri";
          parametriProspettoForm.setPaginaSorgente(fromPage + "-Parametro");
        } else {
          target = "setParametriPredefiniti";
          parametriProspettoForm.setPaginaSorgente("listaPredefinite-Parametro");
        }

        // Inserimento identificativo utente nel request per popolare il
        // parametro hidden nella lista dei parametri per comporre il
        // modello stesso
        request.setAttribute("idAccount", new Integer(profiloUtente.getId()));
        request.setAttribute("listaParametri", elencoParametri);

        // cicliamo sulla lista dei parametri e per ogni parametro di tipo
        // Tabellato mettiamo nel request la lista degli elementi, il nome della
        // lista sarà composto da "lista" + codice tabellato
        noErrori = this.addValoriParametriETabellati(request, profiloUtente,
            codiceProfilo, parametriProspettoForm, noErrori, listaValori,
            elencoParametri);
      } else if (elencoParametri.size() == 1
          && "U".equalsIgnoreCase(((ParametroModello) elencoParametri.get(0)).getTipo())) {
        // nel caso di unico parametro pari all'id utente, non si deve chiedere
        // il parametro interattivamente ma va comunque salvato prima di
        // lanciare il processing del modello
        parametriProspettoForm.setParametriModello(new String[] { ""
            + profiloUtente.getId() });
        target = "salvaEComponi";
      }
      if (noErrori) parametriProspettoForm.setIdSessione(0);
    }

    // gestione del target di destinazione nel caso non si siano verificati
    // errori
    if (noErrori) {
      if (target.indexOf("setParametri") >= 0) {
        request.setAttribute("parametriProspettoForm", parametriProspettoForm);
        request.setAttribute("listaValori", listaValori);
      } else if (target.indexOf("salvaEComponi") >= 0)
        request.setAttribute("parametriModelloconIdUtenteForm",
            parametriProspettoForm);
      else {
        request.setAttribute("componiModelloConIdUtenteForm",
            parametriProspettoForm);
      }

      // Disabilito la navigazione dei vari menu'
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);
    } else {
      target = this.getDestinationTarget(request, idProspetto, fromPage);
    }

    request.setAttribute("idProspetto", new Integer(idProspetto));

    return target;
  }

  /**
   * Cicla sui parametri recuperando dalla cache l'ultimo valore indicato
   * dall'utente, e recuperando, in caso di parametri di tipo tabellato,
   * l'elenco dei valori ammessi, in modo da proporli nella schermata di
   * impostazione parametri
   *
   * @param request
   * @param profiloUtente
   * @param codiceProfilo
   * @param parametriProspettoForm
   * @param noErrori
   * @param listaValori
   * @param elencoParametri
   * @return true se non si sono verificati errori, false altrimenti
   */
  private boolean addValoriParametriETabellati(HttpServletRequest request,
      ProfiloUtente profiloUtente, String codiceProfilo,
      ParametriProspettoForm parametriProspettoForm, boolean noErrori,
      Vector<Object> listaValori, List<?> elencoParametri) {
    Iterator<?> iteratorElencoParametri = elencoParametri.iterator();
    ParametroModello parametro = null;
    List<Tabellato> listaTabellato = null;
    String valore = null;
    String messageKey = null;
    while (iteratorElencoParametri.hasNext() && noErrori) {
      parametro = (ParametroModello) iteratorElencoParametri.next();

      // si recupera l'ultimo valore indicato in cache
      valore = this.modelliManager.getCacheParametroModello(
          profiloUtente.getId(), parametriProspettoForm.getIdModello(),
          parametro.getCodice());

      if ("T".equalsIgnoreCase(parametro.getTipo())) {
        // nel caso di parametro tabellato, si verifica che il campo sia
        // visibile da profilo ed in tal caso si recupera il dominio di valori
        // ammessi
        Campo campo = DizionarioCampi.getInstance().get(
            parametro.getTabellato());
        if (campo != null) {
          if (this.checkMnemoniciParametriTabellati(campo, codiceProfilo)) {
            listaTabellato = tabellatiManager.getTabellato(campo.getCodiceTabellato());
            request.setAttribute("lista" + parametro.getTabellato(),
                listaTabellato);
          } else {
            messageKey = "errors.prospetti.eseguiProspetto.parametriNonVisibili";
            logger.error(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
            noErrori = false;
          }
        } else {
          messageKey = "errors.prospetti.eseguiProspetto.parametriErrati";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
          noErrori = false;
        }
      }
      // nel caso di parametri di tipo data o numero decimale, occorre
      // effettuare un'opportuna conversione rispetto al formato memorizzato in cache
      if ("D".equalsIgnoreCase(parametro.getTipo()))
        valore = StringUtils.replace(valore, ".", "/");
      if ("F".equals(parametro.getTipo()))
        valore = StringUtils.replace(valore, ".", ",");

      listaValori.add(valore);
    }

    return noErrori;
  }

  /**
   * Determina il target di destinazione a seconda della pagina dalla quale si
   * proviene
   *
   * @param request
   * @param idProspetto
   * @param fromPage
   * @return target calcolato
   */
  private String getDestinationTarget(HttpServletRequest request,
      int idProspetto, String fromPage) {
    String target;
    if (fromPage != null && fromPage.length() > 0) {
      if (fromPage.equalsIgnoreCase("listaRicerche")) {
        target = TORNA_A_LISTA_RICERCHE;
      } else if (fromPage.equalsIgnoreCase("dettaglioRicerca")) {
        target = TORNA_A_DETTAGLIO_RICERCA;
        request.setAttribute("idProspetto", new Integer(idProspetto));
      } else {
        target = TORNA_A_LISTA_RICERCHE_PREDEFINITE;
      }
    } else {
      target = TORNA_A_LISTA_RICERCHE_PREDEFINITE;
    }
    return target;
  }

  /**
   * Metodo per stabilire se un report con modello e' eseguibile nel profilo
   * attivo, cioe' se l'entita' principale e' visibile nel profilo attivo
   *
   * @param request
   * @param contenitore
   * @return Ritorna treu se il report con modello e' eseguibile, false
   *         altrimenti
   */
  private boolean isReportConProspettoEseguibile(HttpServletRequest request,
      DatiGenRicerca datiGenRicerca) {
    boolean result = true;

    String messageKey = null;

    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    Tabella tabella = dizTabelle.getDaNomeTabella(datiGenRicerca.getEntPrinc());
    boolean isEntPrincVisibile = this.geneManager.getGestoreVisibilitaDati().checkEntitaVisibile(
        tabella,
        (String) request.getSession().getAttribute(
            CostantiGenerali.PROFILO_ATTIVO));

    if (!isEntPrincVisibile) {
      result = false;
      messageKey = "errors.prospetti.eseguiProspetto.prospettoNonEseguibileNelProfilo";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }

    return result;
  }

  /**
   * Metodo per settare gli attributi dell'oggetto ParametriProspettoForm
   *
   * @param datiModello
   * @return
   */
  private ParametriProspettoForm setParametriProspettoForm(int idProspetto,
      DatiModello datiModello, String[] valoriCampiChiave) {

    ParametriProspettoForm componiModello = new ParametriProspettoForm();
    componiModello.setIdProspetto("" + idProspetto);
    componiModello.setIdModello(datiModello.getIdModello());
    componiModello.setEntita(datiModello.getEntPrinc());
    componiModello.setFileComposto(datiModello.getNomeFile());
    componiModello.setNomeModello(datiModello.getNomeModello());
    componiModello.setValChiavi(valoriCampiChiave);

    if (datiModello.getEntPrinc() != null) {
      DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
      Tabella tabellaPrinc = dizTabelle.getDaNomeTabella(datiModello.getEntPrinc());
      List<Campo> campiChiave = tabellaPrinc.getCampiKey();
      StringBuffer nomiFisici = new StringBuffer("");
      StringBuffer nomiCampi = new StringBuffer("");
      for (Iterator<?> iter = campiChiave.iterator(); iter.hasNext();) {
        Campo campoKey = (Campo) iter.next();
        if (nomiFisici.length() > 0) nomiFisici.append(';');
        nomiFisici.append(campoKey.getNomeFisico());
        if (nomiCampi.length() > 0) nomiCampi.append(';');
        nomiCampi.append(campoKey.getNomeCampo());
      }
      componiModello.setNomeChiavi(nomiCampi.toString());
      componiModello.setValori(nomiCampi.toString());
    }

    return componiModello;
  }

  private boolean checkMnemoniciParametriTabellati(Campo campo,
      String codiceProfilo) {
    boolean continua = true;

    if (!this.geneManager.getGestoreVisibilitaDati().checkCampoVisibile(campo,
        codiceProfilo)) {
      continua = false;
    }

    return continua;
  }

}