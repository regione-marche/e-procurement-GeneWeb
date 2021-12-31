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
package it.eldasoft.gene.web.struts.genric.risultato;

import it.eldasoft.gene.bl.MetadatiManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.dao.QueryDaoException;
import it.eldasoft.gene.db.dao.jdbc.InputStmt;
import it.eldasoft.gene.db.dao.jdbc.ParametroStmt;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.db.domain.genric.DatiRisultato;
import it.eldasoft.gene.db.domain.genric.TabellaRicerca;
import it.eldasoft.gene.tags.history.UtilityHistory;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.gene.web.struts.genric.datigen.SalvaDatiGenRicercaAction;
import it.eldasoft.gene.web.struts.genric.parametro.ParametroRicercaForm;
import it.eldasoft.gene.web.struts.tags.GestoreEccezioni;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;

/**
 * Action per l'esecuzione dell'estrazione di una ricerca.
 *
 * @author Stefano.Sabbadin
 */
public class EstraiRicercaAction extends ActionBaseNoOpzioni {

  //Definizione dei target in linea con struts-config-genRic.xml
  private static final String SQL_COMPOSER_ERROR = "sqlComposerError";
  private static final String SQL_COMPOSER_ERROR_REPORT_SQL = "sqlComposerErrorReportSql";

  private static final String SQL_ERROR            = "sqlError";
  private static final String SQL_ERROR_REPORT_SQL = "sqlErrorReportSql";

  private static final String QUERY_DAO_ERROR      = "queryDaoError";
  private static final String QUERY_DAO_ERROR_REPORT_SQL    = "queryDaoErrorReportSql";


  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(EstraiRicercaAction.class);

  /**
   * Reference alla classe di business logic per l'accesso ai dati relative alle
   * ricerche
   */
  private RicercheManager ricercheManager;

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi ai
   * tabellati
   */
  private TabellatiManager    tabellatiManager;

  private MetadatiManager metadatiManager;

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * @param tabellatiManager tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param metadatiManager metadatiManager da settare internamente alla classe.
   */
  public void setMetadatiManager(MetadatiManager metadatiManager) {
    this.metadatiManager = metadatiManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    DatiRisultato datiRisultato = null;
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    //set del target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    String messageKey = null;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
          session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    // I controlli sui dati obbligatori della ricerca sono stati effettuati in due momenti diversi:
    // - nel caso di estrazione di una ricerca dall'area applicativa 'Generatore Ricerche' i dati sono stati
    //   verificati dalla Action ControllaDatiRicercaAction, la quale e' una Action precedente a questa.
    // - nel caso di estrazione di una ricerca dall'area applicativa 'Report predefiniti' i dati sono stati
    //   verificati in fase di salvataggio della ricerca stessa (si presume quindi che i dati su DB non
    //   siano stati alterati agendo direttamente su DB).

    // Numero della pagina dei dati da valorizzare: di default e' impostato a 1.
    // Qualora la paginazione del risultato non fosse richiesto, allora questa
    // variabile verra' ignorata dal metodo getRisultatoRicerca della classe
    // RicercheManager
    int numeroPagina = 1;

    Enumeration<?> paramNames = request.getParameterNames();
    while(paramNames.hasMoreElements()) {
      String name = (String) paramNames.nextElement();
      if (name != null && name.startsWith("d-") && name.endsWith("-p")) {
        String pageValue = request.getParameter(name);
        if (pageValue != null)
          numeroPagina = Integer.parseInt(pageValue);
      }
    }

    // Oggetto contentente tutte le informazioni della pagina di dati estratti e
    // i relativi dati
    RisultatoRicercaForm risultato = new RisultatoRicercaForm();

    ContenitoreDatiRicerca contenitorePerModel = contenitore.getDatiPerModel();
    String[] arrayParametriRicerca = null;

    //F.D. 23/04/07: i dati riguardanti la query eseguita devono essere
    //visualizzati e passati solo per gli utenti con ou48
    ProfiloUtente profiloUtente = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    OpzioniUtente opzioniUtente = new OpzioniUtente(profiloUtente.getFunzioniUtenteAbilitate());
    String codiceUfficioIntestatarioAttivo = (String) session.getAttribute(
        CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
    try {

      if (contenitore.getNumeroParametri() > 0) {
        if (logger.isDebugEnabled()) {
          logger.debug("La ricerca presenta dei parametri");
        }
        arrayParametriRicerca = (String[]) session.getAttribute(CostantiGenRicerche.PARAMETRI_PER_ESTRAZIONE);

        // Set nel request di un attributo necessario alla pagina che mostra il risultato
        // della ricerca per visualizzare o meno la azione per ritornare alla pagina in cui
        // definire il valore dei parametri. Questo attributo basta solamente popolarlo per
        // visualizzare la suddetta azione
        request.setAttribute("attivaLinkParametri", "linkParametri");

        if (contenitore.getTestata().getVisParametri() != null
            && contenitore.getTestata().getVisParametri().booleanValue()) {
          Vector<ParametroRicercaForm> elencoParametri = contenitore.getElencoParametri();

          List<Tabellato> listaTabellato = null;
          List<String> listaListeTabellati = new ArrayList<String>();
          ParametroRicercaForm parametro = null;
          for (int i = 0; i < elencoParametri.size(); i++) {
            parametro = elencoParametri.get(i);
            if (parametro.getTipoParametro().equals("T")
                && parametro.getTabCod() != null) {
              // gli unici tabellati senza tabCod valorizzato sono al momento i
              // tabellati in KRONOS; l'importante e' proteggere l'applicativo da
              // chiamate indesiderate all'elenco dei valori di tabellato quando
              // non c'e' un valore significativo di codice
              listaTabellato = tabellatiManager.getTabellato(parametro.getTabCod());
              listaListeTabellati.add(this.preparaLista(listaTabellato));
            }
          }
          request.setAttribute("listaListeTabellati", listaListeTabellati);
        }
      }

      

      boolean isReportInStampa = request.getAttribute("stampaReport") != null;
      request.removeAttribute("stampaReport");

      if (CostantiGenRicerche.REPORT_SQL != contenitore.getTestata().getFamiglia()) {
        if (!isReportInStampa && contenitorePerModel.getDatiGenerali().getRisPerPag() != null) {
          datiRisultato = this.ricercheManager.getRisultatiRicerca(
              contenitorePerModel, codiceUfficioIntestatarioAttivo, arrayParametriRicerca,
              profiloUtente, (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO), numeroPagina);
        } else {
          datiRisultato = this.ricercheManager.getRisultatiRicerca(
              contenitorePerModel, codiceUfficioIntestatarioAttivo, arrayParametriRicerca,
              profiloUtente, (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO));
        }
      } else {
        if (!isReportInStampa && contenitorePerModel.getDatiGenerali().getRisPerPag() != null) {
          datiRisultato = this.ricercheManager.getRisultatiRicercaSql(
              contenitorePerModel, arrayParametriRicerca,
              (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO), numeroPagina);
        } else {
          datiRisultato = this.ricercheManager.getRisultatiRicercaSql(
              contenitorePerModel, arrayParametriRicerca,
              (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO));
        }
      }

      if (datiRisultato.isOverflow()) {
        target = EstraiRicercaAction.SQL_ERROR;
        messageKey = "errors.genRic.estraiRicerca.overflow";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);

        this.setRisultatoRicercaConErrore(risultato);
      } else {

        risultato.setDatiRisultato(datiRisultato);
        risultato.setNumeroRighe(datiRisultato.getNumeroRighe());
        risultato.setNumeroColonne(datiRisultato.getNumeroColonne());
        Integer risPerPagina = contenitorePerModel.getDatiGenerali().getRisPerPag();
        /*risultato.setRisPerPagina(risPerPagina == null
            ? 0
            : Integer.parseInt(risPerPagina));*/
        risultato.setRisPerPagina(risPerPagina == null
            ? 0
            : risPerPagina.intValue());
        risultato.setGenModelli(contenitorePerModel.getDatiGenerali().getVisModelli() == 1);
        risultato.setOverflow(datiRisultato.isOverflow());

        // gestione della generazione del link per l'apertura della scheda di dettaglio
        if (!"W0".equals(session.getAttribute(CostantiGenerali.MODULO_ATTIVO))) {
          risultato.setLinkScheda(contenitorePerModel.getDatiGenerali().getLinkScheda() == 1);
          if (risultato.isLinkScheda()) {
            // si salva la url della jsp di dettaglio scheda
            String nomeTabellaPrincipale = null;
            if (CostantiGenRicerche.REPORT_AVANZATO == contenitorePerModel.getDatiGenerali().getFamiglia().intValue()) {
              nomeTabellaPrincipale = contenitore.getNomeEntitaPrincipale();
            } else {
              nomeTabellaPrincipale = this.metadatiManager.getEntitaPrincipaleVista(contenitorePerModel.getDatiGenerali().getEntPrinc()
                  + CostantiGenerali.SEPARATORE_PROPERTIES
                  + ConfigManager.getValore(CostantiGenerali.PROP_SCHEMA_VISTE_REPORT_BASE));
            }

            request.setAttribute("urlScheda",
                SalvaDatiGenRicercaAction.getJspSchedaDettaglio(nomeTabellaPrincipale));
            // si crea il template di definizione della chiave

            // tabella reale principale (anche nel caso di report base, in cui
            // nel contenitore si indica la view)
            String nomeTabella = datiRisultato.getEntPrinc();
            String[] chiavi = datiRisultato.getCampiChiave();
            StringBuffer sb = new StringBuffer();
            Campo campo = null;
            for (int i = 0; i < chiavi.length; i++) {
              if (i > 0) sb.append(";");
              sb.append(nomeTabella).append(".").append(chiavi[i]).append("=");
              campo = DizionarioCampi.getInstance().getCampoByNomeFisico(
                  nomeTabella + "." + chiavi[i]);
              // i campi chiave sono esclusivamente numeri o stringhe
              switch (campo.getTipoColonna()) {
              case Campo.TIPO_INTERO:
                sb.append("N");
                break;
              default:
                sb.append("T");
              }
              sb.append(":").append(i);
            }
            request.setAttribute("templateDefChiave", sb.toString());
          }
        }

        // impostazione del titolo, preso dalla descrizione della testata se
        // presente, altrimenti e' una stringa di default
        String titoloRicerca = contenitore.getTestata().getNome();
        risultato.setTitoloRicerca(titoloRicerca);

        // creazione titoli delle colonne: sono presi dal titolo colonna
        // presente nelle formattazioni, oppure dalla descrizione
        // del campo memorizzata nei metadati se titolo colonna e' vuoto
        Vector<String> titoliColonne = new Vector<String>();
        String titoloColonna = null;
        if (contenitore.getNumeroCampi() > 0) {
          for (int i = 0; i < contenitore.getNumeroCampi(); i++) {
            CampoRicercaForm campoForm = contenitore.estraiCampo(i);
            titoloColonna = campoForm.getTitoloColonna();

            titoliColonne.addElement(titoloColonna);
          }
        }
        if (datiRisultato.getArrayCampi() != null && datiRisultato.getArrayCampi().length > 0) {
          for (int i = 0; i < datiRisultato.getArrayCampi().length; i++) {
            Campo campo = datiRisultato.getArrayCampi()[i];
            titoloColonna = campo.getDescrizione();
            titoliColonne.addElement(titoloColonna);
          }
        }
        risultato.setTitoliColonne(titoliColonne);

        request.setAttribute("requestURI", mapping.getPath());

        if (opzioniUtente.isOpzionePresente(CostantiGeneraliAccount.LISTA_VALUE_GENRIC[2])) {
          //L.G 06/02/2007: set nel request della query e i relativi parametri
          request.setAttribute("querySql", datiRisultato.getQuerySql());
          request.setAttribute("parametriSql", datiRisultato.toStringParametriSql());
        }
        else {
          request.setAttribute("querySql", "Non si &egrave; autorizzati alla visualizzazione della query");
          request.setAttribute("parametriSql", "Non si &egrave; autorizzati alla visualizzazione dei parametri della query");
        }
        if (request.getSession().getAttribute(
            CostantiGenerali.SENTINELLA_OGGETTO_MODIFICATO) != null)
          // set nel request del parameter per disabilitare la navigazione anche in
          // fase di visualizzazione del dato in quanto modificato in sessione
          request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
              CostantiGenerali.DISABILITA_NAVIGAZIONE);
      }
    } catch (QueryDaoException qd){
      if (QueryDaoException.CODICE_ERRORE_NUMERO_RECORD_ESTRATTI_MAGGIORE_VALORE_MAX.equals(qd.getCodiceErrore())) {
        if (CostantiGenRicerche.REPORT_SQL == contenitore.getTestata().getFamiglia().intValue()) {
          target = EstraiRicercaAction.QUERY_DAO_ERROR_REPORT_SQL;
        } else {
          target = EstraiRicercaAction.QUERY_DAO_ERROR;
        }
        String logMessageKey = qd.getChiaveResourceBundle();
        logger.error(this.resBundleGenerale.getString(logMessageKey), qd);
        messageKey = "errors.genRic.estraiRicerca.overflow";
        this.aggiungiMessaggio(request, messageKey);

        this.setRisultatoRicercaConErrore(risultato);

        this.setQueryIntoRequest(request, contenitorePerModel,
            arrayParametriRicerca, opzioniUtente, codiceUfficioIntestatarioAttivo);
      } else if (QueryDaoException.CODICE_ERRORE_TIPO_DATO_NON_RICONOSCIUTO.equals(qd.getCodiceErrore())) {
        if (CostantiGenRicerche.REPORT_SQL == contenitore.getTestata().getFamiglia().intValue()) {
          target = EstraiRicercaAction.QUERY_DAO_ERROR_REPORT_SQL;
        } else {
          target = EstraiRicercaAction.QUERY_DAO_ERROR;
        }
        String logMessageKey = qd.getChiaveResourceBundle();
        logger.error(this.resBundleGenerale.getString(logMessageKey), qd);
        messageKey = qd.getChiaveResourceBundle();
        this.aggiungiMessaggio(request, messageKey, qd.getMessage());

        this.setRisultatoRicercaConErrore(risultato);

        this.setQueryIntoRequest(request, contenitorePerModel,
            arrayParametriRicerca, opzioniUtente, codiceUfficioIntestatarioAttivo);
      } else if (QueryDaoException.CODICE_ERRORE_REPORT_SQL_QUERY_NON_VALIDA.equals(qd.getCodiceErrore())) {
        if (CostantiGenRicerche.REPORT_SQL == contenitore.getTestata().getFamiglia().intValue()) {
          target = EstraiRicercaAction.QUERY_DAO_ERROR_REPORT_SQL;
        } else {
          target = EstraiRicercaAction.QUERY_DAO_ERROR;
        }
        String logMessageKey = qd.getChiaveResourceBundle();
        logger.error(this.resBundleGenerale.getString(logMessageKey), qd);
        messageKey = qd.getChiaveResourceBundle();
        this.aggiungiMessaggio(request, messageKey, qd.getMessage());

        this.setRisultatoRicercaConErrore(risultato);

        this.setQueryIntoRequest(request, contenitorePerModel,
            arrayParametriRicerca, opzioniUtente, codiceUfficioIntestatarioAttivo);
      } else {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = qd.getChiaveResourceBundle();
        logger.error(this.resBundleGenerale.getString(messageKey), qd);
        this.aggiungiMessaggio(request, messageKey);

        this.setRisultatoRicercaConErrore(risultato);
      }
    } catch (SqlComposerException e) {
      if (CostantiGenRicerche.REPORT_SQL == contenitore.getTestata().getFamiglia().intValue()) {
        target = EstraiRicercaAction.SQL_COMPOSER_ERROR_REPORT_SQL;
      } else {
        target = EstraiRicercaAction.SQL_COMPOSER_ERROR;
      }
      messageKey = e.getChiaveResourceBundle();
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
      request.setAttribute("tab", request.getParameter("tab"));

      this.setRisultatoRicercaConErrore(risultato);

      this.setQueryIntoRequest(request, contenitorePerModel,
          arrayParametriRicerca, opzioniUtente, codiceUfficioIntestatarioAttivo);

    } catch (BadSqlGrammarException e) {
      if (CostantiGenRicerche.REPORT_SQL == contenitore.getTestata().getFamiglia().intValue()) {
        target = EstraiRicercaAction.SQL_ERROR_REPORT_SQL;
        messageKey = "errors.database.sql.reportSql.badSqlException";
        logger.error(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey),
            new String[] { e.getMessage() }), e);
        this.aggiungiMessaggio(request, messageKey, e.getMessage());
      } else {
        target = EstraiRicercaAction.SQL_ERROR;
        messageKey = "errors.database.sql.badSqlException";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);

        this.setQueryIntoRequest(request, contenitorePerModel,
            arrayParametriRicerca, opzioniUtente, codiceUfficioIntestatarioAttivo);
      }

      this.setRisultatoRicercaConErrore(risultato);

    } catch (DataIntegrityViolationException e) {
      target = EstraiRicercaAction.SQL_ERROR;
      messageKey = "errors.genRic.estraiRicerca.dataIntegrityException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);

      this.setRisultatoRicercaConErrore(risultato);

      this.setQueryIntoRequest(request, contenitorePerModel,
          arrayParametriRicerca, opzioniUtente, codiceUfficioIntestatarioAttivo);

    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (OutOfMemoryError e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.genRic.estraiRicerca.outOfMemoryError";
      logger.fatal(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    } finally {
      if(risultato != null){
        Integer famiglia = contenitorePerModel.getDatiGenerali().getFamiglia();
        boolean flagLogEvento = false;
        String tabelleDaTracciare = ConfigManager.getValore(CostantiGenRicerche.PROPERTY_TRACCIA_TABELLE);
        if (StringUtils.isNotEmpty(tabelleDaTracciare)) {
          ArrayList<String> arrayTabelleDaTracciare = new ArrayList<String>(Arrays.asList(tabelleDaTracciare.split(";")));

          if (famiglia != null && famiglia == CostantiGenRicerche.REPORT_SQL) {
            String sql = contenitorePerModel.getDatiGenerali().getDefSql().toUpperCase();
            for (int i=0; i<arrayTabelleDaTracciare.size() && !flagLogEvento; i++) {
              if (sql.contains(arrayTabelleDaTracciare.get(i))) {
                flagLogEvento = true;
              }
            }
          } else {
            if (famiglia != null && (famiglia == CostantiGenRicerche.REPORT_AVANZATO || famiglia == CostantiGenRicerche.REPORT_BASE)) {
              Vector<TabellaRicerca> vector = contenitorePerModel.getElencoArgomenti();
              String tabella = null;
              for (int i=0; i<vector.size() && !flagLogEvento; i++) {
                tabella = vector.get(i).getAliasTabella();
                if (arrayTabelleDaTracciare.contains(tabella)) {
                  flagLogEvento = true;
                }
              }
            }
          }
        }
        request.setAttribute("risultatoRicerca", risultato);
        if (flagLogEvento) {
          LogEvento logEvento = LogEventiUtils.createLogEvento(request);
          logEvento.setLivEvento(1);
          logEvento.setCodEvento("RUN_REPORT");
          logEvento.setDescr("Estrazione report con id= " + contenitorePerModel.getDatiGenerali().getIdRicerca().toString());
          ParametroStmt temp[] = datiRisultato.getParametriSql();
          String parametri = "";
          for (int i=0;i<temp.length;i++) {
            if (i>0) {
              parametri = parametri + ", ";
            }
            parametri = parametri + temp[i].getValore().toString();
          }
          logEvento.setErrmsg("SQL: " + datiRisultato.getQuerySql() + ", PARAMETRI: " + parametri + "");
          LogEventiUtils.insertLogEventi(logEvento);
        }
      }
    }

    // L.G. 06/12/2007: in caso di errore, si sfrutta il flag 'eseguiDaLista'
    // per cambiare il forward di questa action

    ActionForward actForward = null;
    if(!target.equals(CostantiGeneraliStruts.FORWARD_OK)){
      try {
        UtilityHistory history = UtilityTags.getUtilityHistory(request.getSession());
        if(contenitore.isEseguiDaLista()){
          if(history.size(0) < 4)
            history.back(request);
          else
            history. vaiA(2,0, request);
          target = target.concat("Lista");
        } else {
          history. vaiA(2,0, request);
        }
      } catch (Throwable t){
        actForward = GestoreEccezioni.gestisciEccezioneAction(t, this, request,
            logger, mapping);
      }
    }
    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    if(actForward != null)
      return actForward;
    else
      return mapping.findForward(target);
  }

  /**
   * Ricalcola la query SQL ed i parametri inviati per l'estrazione del report
   * in modo da settare 2 attributi nel request e permettere la stampa come
   * commento nella pagina HTML in caso di utente amministratore di ricerche.
   *
   * @param request
   *        request HTTP
   * @param contenitorePerModel
   *        contenitore della ricerca
   * @param arrayParametriRicerca
   *        parametri utente inviati alla ricerca
   * @param opzioniUtente
   *        opzioni dell'utente
   */
  private void setQueryIntoRequest(HttpServletRequest request,
      ContenitoreDatiRicerca contenitorePerModel,
      String[] arrayParametriRicerca, OpzioniUtente opzioniUtente, String ufficioIntestatarioAttivo) {
    if (opzioniUtente.isOpzionePresente(CostantiGeneraliAccount.LISTA_VALUE_GENRIC[2])) {
      try {
        // si ricalcola la query che ha prodotto i problemi in modo da
        // metterla nella pagina come testo nascosto
        InputStmt query = this.ricercheManager.getSqlRicerca(
            contenitorePerModel,
            arrayParametriRicerca,
            (ProfiloUtente) request.getSession().getAttribute(
                CostantiGenerali.PROFILO_UTENTE_SESSIONE),
                ufficioIntestatarioAttivo);
        request.setAttribute("querySql", query.getQuerySql());
        request.setAttribute("parametriSql", query.toStringParametriSql());
      } catch (SqlComposerException sql) {
        logger.error(
            this.resBundleGenerale.getString(sql.getChiaveResourceBundle()),
            sql);
      } catch (Throwable t) {
        logger.error(
            this.resBundleGenerale.getString("errors.applicazione.inaspettataException"),
            t);
      }
    } else {
      request.setAttribute("querySql",
          "Non si &egrave; autorizzati alla visualizzazione della query");
      request.setAttribute("parametriSql",
          "Non si &egrave; autorizzati alla visualizzazione dei parametri della query");
    }
  }

  /**
   * Set dell'oggetto risultato ricerca in caso di errore, al fine di una corretta
   * visualizzazione della pagina risultato ricerca
   *
   * @param risultato
   */
  protected void setRisultatoRicercaConErrore(RisultatoRicercaForm risultato) {
    DatiRisultato datiRisultato = new DatiRisultato();
    datiRisultato.setNumeroRecordTotali(0);
    risultato.setDatiRisultato(datiRisultato);
  }

  /**
   * Metodo che converte la lista di un tabellato in una stringa come
   * concatenazione di: tipoTabellato + '_' + tipoDescTabellato + '_' per
   * ciascun elemento della lista
   *
   * @param lista
   * @return
   */
  protected String preparaLista(List<Tabellato> lista) {
    StringBuffer buffer = new StringBuffer("");
    Tabellato tabellato = null;
    for (int i = 0; i < lista.size(); i++) {
      tabellato = lista.get(i);
      buffer.append(tabellato.getTipoTabellato()
          + "_"
          + tabellato.getDescTabellato()
          + "_");
    }

    return buffer.toString();
  }
}