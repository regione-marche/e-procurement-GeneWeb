/*
 * Created on 15/set/2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.genric;

import it.eldasoft.gene.bl.CheckReportPerProfilo;
import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.LoginManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.genmod.CompositoreException;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiIntegrazioneKronos;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.dao.QueryDaoException;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.ParametroComposizione;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.db.domain.genric.DatiGenProspetto;
import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.gene.db.domain.genric.DatiRisultato;
import it.eldasoft.gene.db.domain.genric.ElementoRisultato;
import it.eldasoft.gene.db.domain.genric.ParametroRicerca;
import it.eldasoft.gene.db.domain.genric.RigaRisultato;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.ws.report.ValParametroType;
import it.eldasoft.report.datatypes.AllegatoType;
import it.eldasoft.report.datatypes.ColonnaRisultatoType;
import it.eldasoft.report.datatypes.DatiRisultatoType;
import it.eldasoft.report.datatypes.DefParametroType;
import it.eldasoft.report.datatypes.DefinizioneReportType;
import it.eldasoft.report.datatypes.ElemDominioType;
import it.eldasoft.report.datatypes.GetAllegatoResponseDocument;
import it.eldasoft.report.datatypes.GetAllegatoResponseDocument.GetAllegatoResponse;
import it.eldasoft.report.datatypes.GetDefinizioneReportResponseDocument;
import it.eldasoft.report.datatypes.GetDefinizioneReportResponseDocument.GetDefinizioneReportResponse;
import it.eldasoft.report.datatypes.GetRisultatoReportResponseDocument;
import it.eldasoft.report.datatypes.GetRisultatoReportResponseDocument.GetRisultatoReportResponse;
import it.eldasoft.report.datatypes.RecordType;
import it.eldasoft.report.datatypes.RisultatoReportType;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.web.context.ServletContextAware;

/**
 * Classe di facade per la business logic del web service Report.
 *
 * @author Stefano.Sabbadin
 */
public class ReportFacade implements ServletContextAware {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(ReportFacade.class);

  private ServletContext   servletContext;

  /** Bean per l'esecuzione della business logic sulle ricerche */
  private RicercheManager  ricercheManager;

  /** Reference al manager per la gestione dei prospetti */
  private ProspettoManager prospettoManager;

  /** Bean per l'esecuzione della business logic sui modelli */
  private ModelliManager   modelliManager;

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi ai
   * tabellati
   */
  private TabellatiManager tabellatiManager;

  /**
   * Reference alla classe di business logic che gestisce tutte le funzionalita'
   * di base generali
   */
  private GeneManager      geneManager;

  private LoginManager     loginManager;

  /** Reference alla classe di business logic per estrarre documenti dalla W_DOCDIG. */
  private FileAllegatoManager fileAllegatoManager;

  public void setServletContext(ServletContext arg0) {
    servletContext = arg0;
  }

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * @param prospettoManager
   *        prospettoManager da settare internamente alla classe.
   */
  public void setProspettoManager(ProspettoManager prospettoManager) {
    this.prospettoManager = prospettoManager;
  }

  /**
   * @param modelliManager
   *        modelliManager da settare internamente alla classe.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
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
   * @param loginManager
   *        loginManager da settare internamente alla classe.
   */
  public void setLoginManager(LoginManager loginManager) {
    this.loginManager = loginManager;
  }

  /**
   * @param fileAllegatoManager fileAllegatoManager da settare internamente alla classe.
   */
  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  public ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /**
   * Estrae la definizione di un report, con la lista degli eventuali parametri
   * e, se necessario, la lista dei valori ammissibili.<br/>
   * <b>NB: per l'integrazione con KRONOS va rivisto il codice, non &egrave;
   * compatibile.</b>
   *
   * @param codice
   *        codice identificativo del report
   *
   * @return definizione del report, o eventualmente il codice di errore se non
   *         e' possibile estrarre la definizione
   */
  public String getDefinizioneReport(String codice) {
    if (logger.isDebugEnabled()) {
      logger.debug("getDefinizioneReport(" + codice + "): inizio metodo");
    }

    GetDefinizioneReportResponseDocument document = GetDefinizioneReportResponseDocument.Factory.newInstance();
    GetDefinizioneReportResponse risultato = document.addNewGetDefinizioneReportResponse();

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato e la funzionalita' abilitata
    if (WebUtilities.isAppNotReady()) {
      risultato.setErrore("APP-NOTREADY");
    } else if (!GeneManager.checkOP(this.servletContext,
        CostantiGenerali.OPZIONE_GESTIONE_PUBBL_REPORT_WS)) {
      logger.error("Accesso non autorizzato alla richiesta della definizione di un report");
      risultato.setErrore("UNAUTHORIZED");
    } else {

      try {
        Integer idRicerca = this.ricercheManager.getIdRicercaByCodReportWS(codice);
        if (idRicerca == null) {
          risultato.setErrore("REPORT-NOTFOUND");
          logger.error("Richiesta dal WEB la definizione di un report con codice inesistente ("
              + codice
              + ")");
        } else {
          DatiGenRicerca datiGenerali = this.ricercheManager.getDatiGenRicerca(idRicerca.intValue());
          String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

          if (CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS.equals(ConfigManager.getValore(CostantiGenerali.PROP_INTEGRAZIONE))
              && CostantiIntegrazioneKronos.TIPO_RICERCA_KRONOS.equals(datiGenerali.getTipo())) {
            // i report per l'integrazione con KRONOS non sono utilizzabili dai
            // servizi a causa della presenza di alcuni dati che dipendono
            // dall'utente loggato
            risultato.setErrore("NOT-MANAGED");
            logger.error("Il report con codice "
                + codice
                + " riguarda l'integrazione con KRONOS e pertanto non viene gestita");

          } else if (!Arrays.asList(codapp.split(";")).contains(
              datiGenerali.getCodApp())) {
            // il client deve richiamare questa funzione nel servizio relativo
            // all'applicativo corretto e non uno qualsiasi
            risultato.setErrore("APP-CHECK");
            logger.error("Il report con codice "
                + codice
                + " appartiene all'applicativo "
                + datiGenerali.getCodApp()
                + " e non a "
                + codapp);
          } else {
            ContenitoreDatiRicerca ricerca = null;

            switch (datiGenerali.getFamiglia().intValue()) {
            case CostantiGenRicerche.REPORT_BASE:
            case CostantiGenRicerche.REPORT_AVANZATO:
              ricerca = this.ricercheManager.getRicercaByIdRicerca(idRicerca.intValue());
              if (this.checkProfiloReport(ricerca)) {
                this.getDefinizioneReportBaseAvanzatoSql(ricerca, risultato);
              } else {
                risultato.setErrore("PROF-CHECK");
                logger.error("Il report con codice "
                    + codice
                    + " non risulta eseguibile nel profilo di appartenenza");
              }
              break;

            case CostantiGenRicerche.REPORT_SQL:
              ricerca = this.ricercheManager.getRicercaByIdRicerca(idRicerca.intValue());
              this.getDefinizioneReportBaseAvanzatoSql(ricerca, risultato);
              break;

            case CostantiGenRicerche.REPORT_PROSPETTO:
              DatiGenProspetto ricercaM = this.prospettoManager.getProspettoById(idRicerca.intValue());
              if (ricercaM.getDatiModello().getIdRicercaSrc() == null) {
                this.getDefinizioneModelloStandard(ricercaM, risultato);
              } else {
                this.getDefinizioneModelloBasatoSuUnaRicerca(ricercaM, risultato);
              }
              break;
            }
          }
        }
      } catch (DataAccessException e) {
        risultato.setErrore("DB-ERROR");
        logger.error(
            this.resBundleGenerale.getString("errors.database.dataAccessException"), e);
      } catch (Throwable t) {
        risultato.setErrore("UNEXP-ERROR");
        logger.error(
            this.resBundleGenerale.getString("errors.applicazione.inaspettataException"), t);
      }
    }

    if (logger.isDebugEnabled()) {
      logger.debug("getDefinizioneReport(" + codice + "): fine metodo");
    }
    return document.toString();
  }

  /**
   * Estrae la definizione di un report base o avanzato, completa di eventuali
   * parametri.
   *
   * @param idRicerca
   *        pk ricerca
   * @param risultato
   *        contenitore della risposta
   */
  private void getDefinizioneReportBaseAvanzatoSql(ContenitoreDatiRicerca ricerca,
      GetDefinizioneReportResponse risultato) {

    // si creano le info dei parametri ed eventuali valori di dominio
    Vector<ParametroRicerca> elencoParametri = ricerca.getElencoParametri();
    List<Tabellato> listaTabellato = null;
    ParametroRicerca parametroIn = null;
    DefParametroType[] parametri = new DefParametroType[elencoParametri.size()];
    DefParametroType parametroOut = null;
    for (int i = 0; i < elencoParametri.size(); i++) {
      parametroIn = elencoParametri.get(i);
      parametroOut = DefParametroType.Factory.newInstance();
      parametroOut.setCodice(parametroIn.getCodice());
      parametroOut.setDescrizione(parametroIn.getNome());
      parametroOut.setObbligatorio(true);
      parametroOut.setTipo(parametroIn.getTipo());

      if (parametroIn.getTipo().equals("T") && parametroIn.getTabCod() != null) {
        // gli unici tabellati senza tabCod valorizzato sono al momento
        // i tabellati in KRONOS; l'importante è proteggere
        // l'applicativo da chiamate indesiderate all'elenco dei valori
        // di tabellato quando non c'è un valore significativo di codice
        listaTabellato = tabellatiManager.getTabellato(parametroIn.getTabCod());
        ElemDominioType[] elementi = new ElemDominioType[listaTabellato.size()];
        Tabellato tabellato = null;
        for (int j = 0; j < listaTabellato.size(); j++) {
          tabellato = listaTabellato.get(j);
          elementi[j] = ElemDominioType.Factory.newInstance();
          elementi[j].setCodice(tabellato.getTipoTabellato());
          elementi[j].setDescrizione(tabellato.getDescTabellato());
        }
        parametroOut.setDominioArray(elementi);
      }
      parametri[i] = parametroOut;
    }
    // si settano le info della definizione nel risultato
    DefinizioneReportType definizione = risultato.addNewDefinizione();
    definizione.setNome(ricerca.getDatiGenerali().getNome());
    definizione.setDescrizione(ricerca.getDatiGenerali().getDescrizione());
    definizione.setTipoOutput("1");
    definizione.setParametroArray(parametri);
    risultato.setDefinizione(definizione);
  }

  /**
   * Estrae la definizione di un report con modello standard, ne controlla il
   * profilo (argomento principale e campi tabellati), e quindi ne ritorna la
   * definizione completa di parametri ed eventuale dominio di valori.<br/>
   * Per semplicit&agrave; e necessit&agrave; di velocit&agrave; di
   * realizzazione, in caso di errori si setta il codice d'errore nel risultato,
   * si traccia nel log e si esce subito, quindi nel codice sono presenti delle
   * return sparse.
   *
   * @param ricerca
   *        report con modello
   * @param risultato
   *        contenitore della risposta
   */
  private void getDefinizioneModelloStandard(DatiGenProspetto ricerca,
      GetDefinizioneReportResponse risultato) {
    // si controlla l'argomento principale
    Tabella tabella = DizionarioTabelle.getInstance().getDaNomeTabella(
        ricerca.getDatiGenRicerca().getEntPrinc());
    if (!this.geneManager.getGestoreVisibilitaDati().checkEntitaVisibile(
        tabella, ricerca.getDatiGenRicerca().getProfiloOwner())) {
      risultato.setErrore("PROF-CHECK");
      logger.error("Il report con codice "
          + ricerca.getDatiGenRicerca().getCodReportWS()
          + " parte dall'argomento principale "
          + ricerca.getDatiGenRicerca().getEntPrinc()
          + " non visibile nel profilo di appartenenza");
      return;
    }

    // si estraggono i parametri per effettuarne i controlli ed estrarne
    // l'eventuale dominio
    List<?> elencoParametri = this.modelliManager.getParametriModello(
        ricerca.getDatiModello().getIdModello());

    ArrayList<DefParametroType> parametri = new ArrayList<DefParametroType>();
    if (!this.checkParametriModello(ricerca, elencoParametri, parametri,
        risultato)) return;

    // se si arriva qui e' tutto ok quindi si settano le info della definizione
    // nel risultato
    DefinizioneReportType definizione = risultato.addNewDefinizione();
    definizione.setNome(ricerca.getDatiGenRicerca().getNome());
    definizione.setDescrizione(ricerca.getDatiGenRicerca().getDescrizione());
    definizione.setTipoOutput("2");
    definizione.setParametroArray(parametri.toArray(new DefParametroType[0]));
    risultato.setDefinizione(definizione);
  }

  /**
   * Verifica i parametri del modello se rispettano il profilo, quindi ne estrae
   * il dominio dei valori dove previsto.
   *
   * @param ricerca
   *        ricerca richiesta
   * @param elencoParametri
   *        elenco dei parametri della ricerca
   * @param parametri
   *        set dei parametri da inserire nella definizione
   * @param risultato
   *        contenitore del risultato
   */
  private boolean checkParametriModello(DatiGenProspetto ricerca,
      List<?> elencoParametri, ArrayList<DefParametroType> parametri,
      GetDefinizioneReportResponse risultato) {
    List<Tabellato> listaTabellato = null;
    ParametroModello parametroIn = null;
    DefParametroType parametroOut = null;
    for (int i = 0; i < elencoParametri.size(); i++) {
      parametroIn = (ParametroModello) elencoParametri.get(i);

      if (!parametroIn.getTipo().equals("U")) {
        parametroOut = DefParametroType.Factory.newInstance();
        parametroOut.setCodice(parametroIn.getCodice());
        parametroOut.setDescrizione(parametroIn.getNome());
        parametroOut.setObbligatorio(parametroIn.getObbligatorio() == 1);
        parametroOut.setTipo(parametroIn.getTipo());

        if (parametroIn.getTipo().equals("T")
            && parametroIn.getTabellato() != null) {
          Campo campo = DizionarioCampi.getInstance().get(
              parametroIn.getTabellato());
          if (campo != null) {
            if (this.geneManager.getGestoreVisibilitaDati().checkCampoVisibile(
                campo, ricerca.getDatiGenRicerca().getProfiloOwner())) {
              listaTabellato = tabellatiManager.getTabellato(campo.getCodiceTabellato());
              ElemDominioType[] elementi = new ElemDominioType[listaTabellato.size()];
              Tabellato tabellato = null;
              for (int j = 0; j < listaTabellato.size(); j++) {
                tabellato = listaTabellato.get(j);
                elementi[j] = ElemDominioType.Factory.newInstance();
                elementi[j].setCodice(tabellato.getTipoTabellato());
                elementi[j].setDescrizione(tabellato.getDescTabellato());
              }
              parametroOut.setDominioArray(elementi);
            } else {
              risultato.setErrore("PROF-CHECKFIELD");
              logger.error("Il report con codice "
                  + ricerca.getDatiGenRicerca().getCodReportWS()
                  + " utilizza il tabellato "
                  + parametroIn.getTabellato()
                  + " non visibile nel profilo di appartenenza");
              return false;
            }
          } else {
            risultato.setErrore("UNKNOWN-FIELD");
            logger.error("Il report con codice "
                + ricerca.getDatiGenRicerca().getCodReportWS()
                + " utilizza il campo tabellato "
                + parametroIn.getTabellato()
                + " inesistente nella base dati");
            return false;
          }
        } else if (parametroIn.getTipo().equals("M")) {
          String[] vociMenu = StringUtils.split(parametroIn.getMenu(), '|');
          ElemDominioType[] elementi = new ElemDominioType[vociMenu.length];
          for (int j = 0; j < vociMenu.length; j++) {
            elementi[j] = ElemDominioType.Factory.newInstance();
            elementi[j].setCodice(String.valueOf(j+1));
            elementi[j].setDescrizione(vociMenu[j]);
          }
          parametroOut.setDominioArray(elementi);
        }
        parametri.add(parametroOut);
      }
    }
    return true;
  }

  /**
   * Estrae la definizione di un report con modello standard, ne controlla il
   * profilo (argomento principale e campi tabellati), e quindi ne ritorna la
   * definizione completa di parametri (compresi gli eventuali parametri del
   * report base/avanzato collegato) ed eventuale dominio di valori.<br/>
   * Per semplicit&agrave; e necessit&agrave; di velocit&agrave; di
   * realizzazione, in caso di errori si setta il codice d'errore nel risultato,
   * si traccia nel log e si esce subito, quindi nel codice sono presenti delle
   * return sparse.
   *
   * @param ricerca
   *        report con modello
   * @param risultato
   *        contenitore della risposta
   */
  private void getDefinizioneModelloBasatoSuUnaRicerca(
      DatiGenProspetto ricerca, GetDefinizioneReportResponse risultato) {
    int idModello = ricerca.getDatiModello().getIdModello();

    // si estraggono i parametri per effettuarne i controlli ed estrarne
    // l'eventuale dominio
    ArrayList<ParametroModello> elencoParametri = new ArrayList<ParametroModello>();
    ArrayList<DefParametroType> parametri = new ArrayList<DefParametroType>();

    // estrazione degli eventuali parametri della ricerca sorgente dati
    List<?> listaParametriRicerca = this.ricercheManager.getParametriRicerca(
        ricerca.getDatiModello().getIdRicercaSrc().intValue());

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
      elencoParametri.add((ParametroModello) listaParametriModello.get(i));
    }

    if (!this.checkParametriModello(ricerca, elencoParametri, parametri,
        risultato)) return;

    // se si arriva qui e' tutto ok quindi si settano le info della definizione
    // nel risultato
    DefinizioneReportType definizione = risultato.addNewDefinizione();
    definizione.setNome(ricerca.getDatiGenRicerca().getNome());
    definizione.setDescrizione(ricerca.getDatiGenRicerca().getDescrizione());
    definizione.setTipoOutput("2");
    definizione.setParametroArray(parametri.toArray(new DefParametroType[0]));
    risultato.setDefinizione(definizione);
  }

  /**
   * Esegue un report passando gli eventuali parametri. Nel caso di report base
   * ed avanzato &egrave; possibile specificare anche la paginazione e la
   * dimensione massima della pagina.
   *
   * @param codice
   *        codice identificativo del report
   * @param codiceUfficioIntestatarioAttivo
   *        codice ufficio intestatario attivo per filtrare i dati
   * @param parametro
   *        lista dei parametri per l'esecuzione del report
   * @param pagina
   *        numero di pagina da estrarre (solo per report base ed avanzati che
   *        estraggono griglie di risultati)
   * @param maxDimensionePagina
   *        dimensione massima di una pagina (solo per report base ed avanzati
   *        che estraggono griglie di risultati)
   * @return risultato del report, o eventualmente il codice di errore se non e'
   *         possibile estrarre il risultato
   */
  public String getRisultatoReport(String codice, String codiceUfficioIntestatarioAttivo,
      ValParametroType[] parametro, Integer pagina, Integer maxDimensionePagina) {
    if (logger.isDebugEnabled()) {
      logger.debug("getRisultatoReport("
          + codice
          + ","
          + (StringUtils.isNotEmpty(codiceUfficioIntestatarioAttivo) ? codiceUfficioIntestatarioAttivo : "null")
          + ","
          + (parametro == null ? "null" : String.valueOf(parametro.length))
          + ","
          + pagina
          + ","
          + maxDimensionePagina
          + "): inizio metodo");
    }

    GetRisultatoReportResponseDocument document = GetRisultatoReportResponseDocument.Factory.newInstance();
    GetRisultatoReportResponse risultato = document.addNewGetRisultatoReportResponse();

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato e la funzionalita' abilitata
    if (WebUtilities.isAppNotReady()) {
      risultato.setErrore("APP-NOTREADY");
    } else if (!GeneManager.checkOP(this.servletContext,
        CostantiGenerali.OPZIONE_GESTIONE_PUBBL_REPORT_WS)) {
      logger.error("Accesso non autorizzato alla richiesta di esecuzione di un report");
      risultato.setErrore("UNAUTHORIZED");
    } else {

      try {
        Integer idRicerca = this.ricercheManager.getIdRicercaByCodReportWS(codice);
        if (idRicerca == null) {
          risultato.setErrore("REPORT-NOTFOUND");
          logger.error("Richiesta dal WEB la definizione di un report con codice inesistente ("
              + codice
              + ")");
        } else {
          DatiGenRicerca datiGenerali = this.ricercheManager.getDatiGenRicerca(idRicerca.intValue());
          String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

          if (CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS.equals(ConfigManager.getValore(CostantiGenerali.PROP_INTEGRAZIONE))
              && CostantiIntegrazioneKronos.TIPO_RICERCA_KRONOS.equals(datiGenerali.getTipo())) {
            // i report per l'integrazione con KRONOS non sono utilizzabili dai
            // servizi a causa della presenza di alcuni dati che dipendono
            // dall'utente loggato
            risultato.setErrore("NOT-MANAGED");
            logger.error("Il report con codice "
                + codice
                + " riguarda l'integrazione con KRONOS e pertanto non viene gestita");

          } else if (!Arrays.asList(codapp.split(";")).contains(
              datiGenerali.getCodApp())) {
            // il client deve richiamare questa funzione nel servizio relativo
            // all'applicativo corretto e non uno qualsiasi
            risultato.setErrore("APP-CHECK");
            logger.error("Il report con codice "
                + codice
                + " appartiene all'applicativo "
                + datiGenerali.getCodApp()
                + " e non a "
                + codapp);
          } else {

            boolean controlliOk = true;

            switch (datiGenerali.getFamiglia().intValue()) {
            case CostantiGenRicerche.REPORT_BASE:
            case CostantiGenRicerche.REPORT_AVANZATO:

              // correzione dei parametri in modo da proteggere l'esecuzione
              if (maxDimensionePagina != null
                  && maxDimensionePagina.intValue() <= 0) {
                // dimensione pagina a 0 o negativa => trascuro la paginazione
                maxDimensionePagina = null;
              }
              if (pagina != null && pagina.intValue() <= 0) {
                // pagina 0 o negativa => trascuro il numero pagina da estrarre
                pagina = null;
              }

              ContenitoreDatiRicerca ricerca = this.ricercheManager.getRicercaByIdRicerca(idRicerca.intValue());
              String[] arrayParametriRicerca = new String[ricerca.getElencoParametri().size()];
              ProfiloUtente utente = null;
              DatiRisultato datiEstratti = null;

              controlliOk = this.checkProfiloReport(ricerca);
              if (controlliOk) {
                controlliOk = this.checkParametriReportBaseAvanzato(codice,
                    parametro, ricerca, risultato);
              } else {
                risultato.setErrore("PROF-CHECK");
                logger.error("Il report con codice "
                    + codice
                    + " non risulta eseguibile nel profilo di appartenenza");
              }

              if (controlliOk) {
                // se si arriva qui allora i controlli sul parametro sono
                // andati tutti a buon fine per cui si popola l'elenco dei
                // parametri da passare all'oggetto che si occupa di eseguire
                // la ricerca
                for (int j = 0; j < arrayParametriRicerca.length; j++) {
                  arrayParametriRicerca[j] = parametro[j].getValore();
                }

                // costruzione ProfiloUtente a partire dall'owner della ricerca
                Account account = this.loginManager.getAccountById(ricerca.getDatiGenerali().getOwner());
                utente = this.loginManager.getProfiloUtente(account,
                    ricerca.getDatiGenerali().getCodApp());
              }

              // esecuzione della ricerca
              if (controlliOk) {
                // popolamento del risultato
                datiEstratti = this.getRisultatiRicercaBaseAvanzata(pagina,
                    maxDimensionePagina, ricerca, arrayParametriRicerca,
                    utente, codiceUfficioIntestatarioAttivo);
                if (datiEstratti.isOverflow()) {
                  risultato.setErrore("OVERFLOW");
                  logger.error("Il report con codice "
                      + codice
                      + " estrae un numero di record superiore al massimo previsto");
                } else {
                  this.setRisultatoReportBaseAvanzato(pagina,
                      maxDimensionePagina, ricerca, datiEstratti, risultato);
                }
              }

              break;
            case CostantiGenRicerche.REPORT_SQL:

              // correzione dei parametri in modo da proteggere l'esecuzione
              if (maxDimensionePagina != null
                  && maxDimensionePagina.intValue() <= 0) {
                // dimensione pagina a 0 o negativa => trascuro la paginazione
                maxDimensionePagina = null;
              }
              if (pagina != null && pagina.intValue() <= 0) {
                // pagina 0 o negativa => trascuro il numero pagina da estrarre
                pagina = null;
              }

              ContenitoreDatiRicerca ricercaSql = this.ricercheManager.getRicercaByIdRicerca(idRicerca.intValue());
              String[] arrayParametriRicercaSql = new String[ricercaSql.getElencoParametri().size()];
              DatiRisultato datiEstrattiSql = null;

              if (controlliOk) {
                // se si arriva qui allora i controlli sul parametro sono
                // andati tutti a buon fine per cui si popola l'elenco dei
                // parametri da passare all'oggetto che si occupa di eseguire
                // la ricerca
                for (int j = 0; j < arrayParametriRicercaSql.length; j++) {
                  arrayParametriRicercaSql[j] = parametro[j].getValore();
                }
              }

              // esecuzione della ricerca
              if (controlliOk) {
                // popolamento del risultato
                datiEstrattiSql = this.getRisultatiRicercaSql(pagina,
                    maxDimensionePagina, ricercaSql, arrayParametriRicercaSql);
                if (datiEstrattiSql.isOverflow()) {
                  risultato.setErrore("OVERFLOW");
                  logger.error("Il report con codice "
                      + codice
                      + " estrae un numero di record superiore al massimo previsto");
                } else {
                  this.setRisultatoReportSql(pagina, maxDimensionePagina, ricercaSql,
                      datiEstrattiSql, risultato);
                }
              }

              if (datiEstrattiSql.isOverflow()) {
                risultato.setErrore("OVERFLOW");
                logger.error("Il report con codice "
                    + codice
                    + " estrae un numero di record superiore al massimo previsto");
              } else {
                this.setRisultatoReportSql(pagina, maxDimensionePagina, ricercaSql,
                    datiEstrattiSql, risultato);
              }

              break;
            case CostantiGenRicerche.REPORT_PROSPETTO:
              DatiGenProspetto ricercaM = this.prospettoManager.getProspettoById(idRicerca.intValue());

              DatiModello datiModello = ricercaM.getDatiModello();

              if (datiModello.getIdRicercaSrc() == null) {
                // si tratta di un modello standard
                controlliOk = this.componiModelloStandard(ricercaM, parametro,
                    risultato);
              } else {
                // si tratta di un modello con sorgente dati un report
                controlliOk = this.componiModelloConSorgenteDatiReport(
                    ricercaM, codiceUfficioIntestatarioAttivo, parametro, risultato);
              }

              if (controlliOk) {
                // salvo il file nell'xml della risposta e lo elimino dalla
                // cartella in cui l'ho prodotto
                File file = new File(
                    ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI)
                        + ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI_OUTPUT)
                        + risultato.getRisultatoReport().getNomeFile());
                risultato.getRisultatoReport().setContenutoFile(
                    FileUtils.readFileToByteArray(file));
                this.modelliManager.eliminaFileComposto(
                    risultato.getRisultatoReport().getNomeFile(),
                    ricercaM.getDatiGenRicerca().getCodApp());
              }

              break;
            }
          }
        }
      } catch (QueryDaoException e) {
        if (QueryDaoException.CODICE_ERRORE_NUMERO_RECORD_ESTRATTI_MAGGIORE_VALORE_MAX.equals(e.getCodiceErrore())) {
          risultato.setErrore("QUERYOVERFLOW-ERROR");
        } else {
          risultato.setErrore("QUERYCONFIG-ERROR");
        }
        logger.error(
            this.resBundleGenerale.getString(e.getChiaveResourceBundle()), e);
      } catch (SqlComposerException e) {
        risultato.setErrore("SQL-ERROR");
        logger.error(
            this.resBundleGenerale.getString(e.getChiaveResourceBundle()), e);
      } catch (DataAccessException e) {
        risultato.setErrore("DB-ERROR");
        logger.error(
            this.resBundleGenerale.getString("errors.database.dataAccessException"),
            e);
      } catch (CompositoreException e) {
        risultato.setErrore("COMPO-ERROR");
        logger.error(
            this.resBundleGenerale.getString("errors.applicazione.inaspettataException"),
            e);
      } catch (RemoteException e) {
        risultato.setErrore("RMI-ERROR");
        logger.error(
            this.resBundleGenerale.getString("errors.applicazione.inaspettataException"),
            e);
      } catch (Throwable t) {
        risultato.setErrore("UNEXP-ERROR");
        logger.error(
            this.resBundleGenerale.getString("errors.applicazione.inaspettataException"),
            t);
      }
    }

    if (logger.isDebugEnabled())
      logger.debug("getRisultatoReport(" + codice + "): fine metodo");

    return document.toString();
  }

  /**
   * Verifica se il report base o avanzato &egrave; eseguibile nel profilo di
   * definizione.
   *
   * @param ricerca
   *        ricerca da eseguire
   * @return true se il report &egrave; eseguibile, false altrimenti
   */
  private boolean checkProfiloReport(ContenitoreDatiRicerca ricerca) {
    boolean esito = true;
    // check del report nel rispetto del profilo di appartenenza
    CheckReportPerProfilo reportChecking = new CheckReportPerProfilo(
        this.geneManager.getGestoreVisibilitaDati(),
        ricerca.getDatiGenerali().getProfiloOwner(), ricerca);

    if (!reportChecking.isReportEseguibile()) {
      esito = false;
      // break;
    }
    return esito;
  }

  /**
   * Si effettuano tutti i controlli di validit&agrave; sui parametri ricevuti
   * nel rispetto della definizione dei parametri della ricerca da eseguire e
   * dell'eventuale dominio di tali parametri.
   *
   * @param codice
   *        codice del report da eseguire
   * @param parametri
   *        parametri ricevuti
   * @param ricerca
   *        ricerca da eseguire
   * @param risultato
   *        contenitore del risultato della ricerca
   * @return true se tutti i controlli sono andati a buon fine, false altrimenti
   */
  private boolean checkParametriReportBaseAvanzato(String codice,
      ValParametroType[] parametri, ContenitoreDatiRicerca ricerca,
      GetRisultatoReportResponse risultato) {
    boolean esito = true;

    // si controllano i parametri ricevuti in conformita' con la
    // definizione
    Vector<ParametroRicerca> elencoParametri = ricerca.getElencoParametri();

    if (elencoParametri.size() == 0
        && parametri != null
        && parametri.length > 0) {
      // sono arrivati dei parametri quando il report non li prevede
      risultato.setErrore("PARAMS-NOTDEF");
      logger.error("Il report con codice "
          + codice
          + " non prevede parametri, ma ne sono stati ricevuti "
          + parametri.length);
      esito = false;
      // break;
    }

    if (esito) {
      if (parametri != null && parametri.length != elencoParametri.size()) {
        // sono arrivati dei parametri in piu' rispetto a quelli che il report
        // non li prevede
        risultato.setErrore("PARAMS-UNKNOWN");
        logger.error("Il report con codice "
            + codice
            + " prevede "
            + elencoParametri.size()
            + " parametri, ma ne sono stati ricevuti "
            + parametri.length);
        esito = false;
        // break;
      }
    }

    List<?> listaTabellato = null;
    ParametroRicerca parametroIn = null;

    for (int i = 0; i < elencoParametri.size() && esito; i++) {
      parametroIn = elencoParametri.get(i);

      // se mi aspetto dei parametri, devono essere nella posizione
      // corretta
      if (parametri == null
          || i >= parametri.length
          || !parametroIn.getCodice().equals(parametri[i].getCodice())) {
        risultato.setErrore("PARAM-NOTFOUND[CODE="
            + parametroIn.getCodice()
            + ",POS="
            + i
            + "]");
        logger.error("Il report con codice "
            + codice
            + " prevede in posizione "
            + i
            + " il parametro "
            + parametroIn.getCodice());
        esito = false;
      }

      // se il codice parametro e' corretto, controllo la presenza del
      // valore
      if (esito) {
        if (parametri[i].getValore() == null) {
          risultato.setErrore("PARAM-NOTSET[CODE="
              + parametroIn.getCodice()
              + ",POS="
              + i
              + "]");
          logger.error("Il report con codice "
              + codice
              + " prevede in posizione "
              + i
              + " un parametro obbligatorio");
          esito = false;
        }
      }

      // se il valore esiste, controllo il formato
      if (esito) {
        if (parametroIn.getTipo().equals("D")
            && UtilityDate.convertiData(parametri[i].getValore(),
                UtilityDate.FORMATO_GG_MM_AAAA) == null) {
          risultato.setErrore("PARAM-FORMAT[CODE="
              + parametroIn.getCodice()
              + ",POS="
              + i
              + "]");
          logger.error("Ricevuta una data errata ("
              + parametri[i].getValore()
              + ") in posizione "
              + i
              + "  nel report con codice "
              + codice);
          esito = false;
        }

        if (parametroIn.getTipo().equals("I")
            && UtilityNumeri.convertiIntero(parametri[i].getValore()) == null) {
          risultato.setErrore("PARAM-FORMAT[CODE="
              + parametroIn.getCodice()
              + ",POS="
              + i
              + "]");
          logger.error("Ricevuto un intero errato ("
              + parametri[i].getValore()
              + ") in posizione "
              + i
              + "  nel report con codice "
              + codice);
          esito = false;
        }

        if (parametroIn.getTipo().equals("F")) {
          // il parametro se arriva valorizzato contiene il punto decimale e va
          // trasformato in virgola decimale per essere accettato dal manager
          // delle ricerche
          parametri[i].setValore(StringUtils.replace(parametri[i].getValore(), ".", ","));
          if (UtilityNumeri.convertiDouble(parametri[i].getValore()) == null) {
            risultato.setErrore("PARAM-FORMAT[CODE="
                + parametroIn.getCodice()
                + ",POS="
                + i
                + "]");
            logger.error("Ricevuto un numero decimale errato ("
                + parametri[i].getValore()
                + ") in posizione "
                + i
                + "  nel report con codice "
                + codice);
            esito = false;
          }
        }

        if (parametroIn.getTipo().equals("T")
            && parametroIn.getTabCod() != null) {
          // gli unici tabellati senza tabCod valorizzato sono al
          // momento i tabellati in KRONOS; l'importante è proteggere
          // l'applicativo da chiamate indesiderate all'elenco dei valori di
          // tabellato quando non c'è un valore significativo di codice
          listaTabellato = tabellatiManager.getTabellato(parametroIn.getTabCod());
          boolean codiceTrovato = false;
          for (int j = 0; j < listaTabellato.size() && !codiceTrovato; j++) {
            Tabellato tabellato = (Tabellato) listaTabellato.get(j);
            if (tabellato.getTipoTabellato().equals(parametri[i].getValore()))
              codiceTrovato = true;
          }
          if (!codiceTrovato) {
            risultato.setErrore("PARAM-FORMAT[CODE="
                + parametroIn.getCodice()
                + ",POS="
                + i
                + "]");
            logger.error("Ricevuto un dato tabellato errato ("
                + parametri[i].getValore()
                + ") in posizione "
                + i
                + "  nel report con codice "
                + codice);
            esito = false;
          }
        }
      }
    }
    // if (!parametriOk) controlliOk = false; // break; // interrompo le
    // operazioni
    return esito;
  }

  /**
   * Estrae i dati richiesti per un report base o avanzato
   *
   * @param pagina
   *        pagina eventuale da estrarre
   * @param maxDimensionePagina
   *        dimensione dell'eventuale pagina da estrarre
   * @param ricerca
   *        ricerca da eseguire
   * @param arrayParametriRicerca
   *        parametri della ricerca da utilizzare in esecuzione
   * @param utente
   *        utilizzato per l'esecuzione
   * @param codiceUfficioIntestatarioAttivo
   *        codice dell'ufficio intestatari oattivo per filtrare i dati
   * @return dati estratti mediante la query sql
   * @throws SqlComposerException
   * @throws QueryDaoException
   */
  private DatiRisultato getRisultatiRicercaBaseAvanzata(Integer pagina,
      Integer maxDimensionePagina, ContenitoreDatiRicerca ricerca,
      String[] arrayParametriRicerca, ProfiloUtente utente, String codiceUfficioIntestatarioAttivo)
        throws SqlComposerException, QueryDaoException {
    DatiRisultato datiEstratti;
    if (maxDimensionePagina != null) {
      ricerca.getDatiGenerali().setRisPerPag(maxDimensionePagina);
      datiEstratti = this.ricercheManager.getRisultatiRicerca(ricerca, codiceUfficioIntestatarioAttivo,
          arrayParametriRicerca, utente, ricerca.getDatiGenerali().getCodApp(),
          (pagina != null ? pagina.intValue() : 1));
    } else {
      datiEstratti = this.ricercheManager.getRisultatiRicerca(ricerca, codiceUfficioIntestatarioAttivo,
          arrayParametriRicerca, utente, ricerca.getDatiGenerali().getCodApp());
    }
    return datiEstratti;
  }

  /**
   * Estrae i dati richiesti per un report sql
   *
   * @param pagina
   *        pagina eventuale da estrarre
   * @param maxDimensionePagina
   *        dimensione dell'eventuale pagina da estrarre
   * @param ricerca
   *        ricerca da eseguire
   * @param arrayParametriRicerca
   *        parametri della ricerca da utilizzare in esecuzione
   * @return dati estratti mediante la query sql
   * @throws SqlComposerException
   * @throws QueryDaoException
   */
  private DatiRisultato getRisultatiRicercaSql(Integer pagina,
      Integer maxDimensionePagina, ContenitoreDatiRicerca ricerca,
      String[] arrayParametriRicerca)
        throws SqlComposerException, QueryDaoException {
    DatiRisultato datiEstratti;
    if (maxDimensionePagina != null) {
      ricerca.getDatiGenerali().setRisPerPag(maxDimensionePagina);
      datiEstratti = this.ricercheManager.getRisultatiRicercaSql(ricerca, arrayParametriRicerca,
          ricerca.getDatiGenerali().getCodApp(), (pagina != null ? pagina.intValue() : 1));
    } else {
      datiEstratti = this.ricercheManager.getRisultatiRicercaSql(ricerca, arrayParametriRicerca,
          ricerca.getDatiGenerali().getCodApp());
    }
    return datiEstratti;
  }

  /**
   * Popola il risultato con i dati estratti.
   *
   * @param pagina
   *        numero di pagina eventualmente estratta
   * @param maxDimensionePagina
   *        dimensione della pagina eventualmente estratta
   * @param ricerca
   *        ricerca da eseguire
   * @param datiEstratti
   *        dati estratti dalla ricerca
   * @param risultato
   *        risultato da popolare
   */
  private void setRisultatoReportBaseAvanzato(Integer pagina,
      Integer maxDimensionePagina, ContenitoreDatiRicerca ricerca,
      DatiRisultato datiEstratti, GetRisultatoReportResponse risultato) {
    RisultatoReportType datiReport = risultato.addNewRisultatoReport();
    DatiRisultatoType dati = datiReport.addNewDati();
    datiReport.setTipoOutput("1");

    dati.setTotRecord(datiEstratti.getNumeroRecordTotali());
    if (maxDimensionePagina != null) {
      dati.setMaxDimPagina(maxDimensionePagina.intValue());
      dati.setTotPagine(((datiEstratti.getNumeroRecordTotali() - 1)
          / maxDimensionePagina.intValue() + 1));
      dati.setRecordPagina(datiEstratti.getNumeroRighe());
      dati.setPagina(pagina != null ? pagina.intValue() : 1);
    }

    if (datiEstratti.getNumeroRecordTotali() > 0) {
      RigaRisultato riga = null;
      ElementoRisultato elemento = null;

      for (int i = 0; i < datiEstratti.getRigheRisultato().size(); i++) {
        RecordType record = dati.addNewRecord();
        riga = datiEstratti.getRigheRisultato().get(i);
        if (i == 0) {
          // se siamo alla prima iterazione, popolo la definizione delle
          // colonne
          ColonnaRisultatoType[] defColonne = new ColonnaRisultatoType[riga.getNumeroColonneRisultato()];
          ColonnaRisultatoType defColonna = null;
          for (int j = 0; j < riga.getColonneRisultato().size(); j++) {
            elemento = riga.getColonneRisultato().get(j);
            defColonna = ColonnaRisultatoType.Factory.newInstance();
            switch (elemento.getTipo()) {
            case Campo.TIPO_DATA:
              defColonna.setTipo("D");
              break;
            case Campo.TIPO_DECIMALE:
              defColonna.setTipo("F");
              break;
            case Campo.TIPO_INTERO:
              defColonna.setTipo("I");
              break;
            default:
              defColonna.setTipo("S");
            }
            defColonna.setTitolo((ricerca.getElencoCampi().get(j)).getTitoloColonna());
            defColonne[j] = defColonna;
          }
          dati.setDefColonnaRisultatoArray(defColonne);
        }

        // si passa a popolare i dati estratti
        String[] valoriRiga = new String[riga.getColonneRisultato().size()];
        for (int j = 0; j < riga.getColonneRisultato().size(); j++) {
          elemento = riga.getColonneRisultato().get(j);
          valoriRiga[j] = (String) elemento.getValore();
        }
        record.setCampoArray(valoriRiga);
      }
    }
  }

  /**
   * Popola il risultato con i dati estratti.
   *
   * @param pagina
   *        numero di pagina eventualmente estratta
   * @param maxDimensionePagina
   *        dimensione della pagina eventualmente estratta
   * @param ricerca
   *        ricerca da eseguire
   * @param datiEstratti
   *        dati estratti dalla ricerca
   * @param risultato
   *        risultato da popolare
   */
  private void setRisultatoReportSql(Integer pagina,
      Integer maxDimensionePagina, ContenitoreDatiRicerca ricerca,
      DatiRisultato datiEstratti, GetRisultatoReportResponse risultato) {

    RisultatoReportType datiReport = risultato.addNewRisultatoReport();
    DatiRisultatoType dati = datiReport.addNewDati();
    datiReport.setTipoOutput("1");

    dati.setTotRecord(datiEstratti.getNumeroRecordTotali());
    if (maxDimensionePagina != null) {
      dati.setMaxDimPagina(maxDimensionePagina.intValue());
      dati.setTotPagine(((datiEstratti.getNumeroRecordTotali() - 1)
          / maxDimensionePagina.intValue() + 1));
      dati.setRecordPagina(datiEstratti.getNumeroRighe());
      dati.setPagina(pagina != null ? pagina.intValue() : 1);
    }

    if (datiEstratti.getNumeroRecordTotali() > 0) {
      RigaRisultato riga = null;
      ElementoRisultato elemento = null;

      for (int i = 0; i < datiEstratti.getRigheRisultato().size(); i++) {
        RecordType record = dati.addNewRecord();
        riga = datiEstratti.getRigheRisultato().get(i);
        if (i == 0) {
          // se siamo alla prima iterazione, popolo la definizione delle
          // colonne
          ColonnaRisultatoType[] defColonne = new ColonnaRisultatoType[riga.getNumeroColonneRisultato()];
          ColonnaRisultatoType defColonna = null;
          for (int j = 0; j < riga.getColonneRisultato().size(); j++) {
            elemento = riga.getColonneRisultato().get(j);
            defColonna = ColonnaRisultatoType.Factory.newInstance();
            switch (elemento.getTipo()) {
            case Campo.TIPO_DATA:
              defColonna.setTipo("D");
              break;
            case Campo.TIPO_DECIMALE:
              defColonna.setTipo("F");
              break;
            case Campo.TIPO_INTERO:
              defColonna.setTipo("I");
              break;
            default:
              defColonna.setTipo("S");
            }
            defColonna.setTitolo((datiEstratti.getArrayCampi()[j]).getDescrizione());
            defColonne[j] = defColonna;
          }
          dati.setDefColonnaRisultatoArray(defColonne);
        }

        // si passa a popolare i dati estratti
        String[] valoriRiga = new String[riga.getColonneRisultato().size()];
        for (int j = 0; j < riga.getColonneRisultato().size(); j++) {
          elemento = riga.getColonneRisultato().get(j);
          valoriRiga[j] = (String) elemento.getValore();
        }
        record.setCampoArray(valoriRiga);
      }
    }
  }

  /**
   * Effettua il controllo del report con prospetto nel rispetto del profilo,
   * estrae la chiave, controlla i parametri e quindi lancia la composizione del
   * report con modello.
   *
   * @param ricerca
   *        ricerca da eseguire
   * @param parametri
   *        parametri in input
   * @param risultato
   *        risultato da popolare
   * @return true se la composizione e' andata a buon fine e senza errori (sia
   *         nella composizione, sia nei controli pre elaborazione), false
   *         altrimenti
   */
  private boolean componiModelloStandard(DatiGenProspetto ricerca,
      ValParametroType[] parametri, GetRisultatoReportResponse risultato)
      throws CompositoreException, RemoteException, SqlComposerException,
      QueryDaoException, CriptazioneException {
    // si controlla l'argomento principale
    Tabella tabella = DizionarioTabelle.getInstance().getDaNomeTabella(
        ricerca.getDatiGenRicerca().getEntPrinc());
    if (!this.geneManager.getGestoreVisibilitaDati().checkEntitaVisibile(
        tabella, ricerca.getDatiGenRicerca().getProfiloOwner())) {
      risultato.setErrore("PROF-CHECK");
      logger.error("Il report con codice "
          + ricerca.getDatiGenRicerca().getCodReportWS()
          + " parte dall'argomento principale "
          + ricerca.getDatiGenRicerca().getEntPrinc()
          + " non visibile nel profilo di appartenenza");
      return false;
    }

    // si estrae una chiave per richiamare il compositore (il primo record
    // dell'entita' di partenza)
    String[] valoriCampiChiave = this.prospettoManager.getChiavePrimoRecordEntitaPerCompositore(
        DizionarioTabelle.getInstance().getDaNomeTabella(
            ricerca.getDatiGenRicerca().getEntPrinc()));
    if (valoriCampiChiave == null) {
      risultato.setErrore("NOCAMPICHIAVE");
      logger.error("Il report con codice "
          + ricerca.getDatiGenRicerca().getCodReportWS()
          + " parte dall'argomento principale "
          + ricerca.getDatiGenRicerca().getEntPrinc()
          + " la cui definizione e' priva dei campi chiave");
      return false;
    } else if (StringUtils.isEmpty(valoriCampiChiave[0])) {
      risultato.setErrore("NODATA");
      logger.error("Il report con codice "
          + ricerca.getDatiGenRicerca().getCodReportWS()
          + " parte dall'argomento principale "
          + ricerca.getDatiGenRicerca().getEntPrinc()
          + " nella cui tabella non sono presenti dati");
      return false;
    }

    // si estraggono i campi che costituiscono la chiave dell'entita' principale
    StringBuffer nomeCampiChiave = new StringBuffer("");
    if (ricerca.getDatiModello().getEntPrinc() != null) {
      DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
      Tabella tabellaPrinc = dizTabelle.getDaNomeTabella(ricerca.getDatiModello().getEntPrinc());
      List<Campo> campiChiave = tabellaPrinc.getCampiKey();
      for (Iterator<?> iter = campiChiave.iterator(); iter.hasNext();) {
        Campo campoKey = (Campo) iter.next();
        if (nomeCampiChiave.length() > 0) nomeCampiChiave.append(';');
        nomeCampiChiave.append(campoKey.getNomeCampo());
      }
    }

    // costruzione ProfiloUtente a partire dall'owner della ricerca
    Account account = this.loginManager.getAccountById(ricerca.getDatiGenRicerca().getOwner());
    ProfiloUtente utente = this.loginManager.getProfiloUtente(account,
        ricerca.getDatiGenRicerca().getCodApp());

    String contesto = null;
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI)))
      contesto = utente.getUfficioAppartenenza();

    // si controllano i parametri
    List<ValParametroType> parametriCompleto = this.copyParametriIntoCollection(parametri);
    List<ParametroComposizione> parametriComposizione = new ArrayList<ParametroComposizione>();
    boolean esito = this.checkAndSetParametriModello(ricerca,
        parametriCompleto, parametriComposizione, risultato);

    // eseguo la chiamata per l'inserimento dei parametri nella tabella di
    // appoggio per il compositore
    int idSessione = -1;
    if (esito) {
      idSessione = this.modelliManager.insertParametriComposizione(
          parametriComposizione.toArray(new ParametroComposizione[0]),
          utente.getId(), ricerca.getDatiModello().getIdModello());

      // esegue la composizione del modello
      String fileComposto = this.modelliManager.componiModello(
          ricerca.getDatiModello().getIdModello(),
          ricerca.getDatiModello().getEntPrinc(), nomeCampiChiave.toString(),
          valoriCampiChiave, ricerca.getDatiGenRicerca().getCodApp(),
          utente.getId(), contesto, idSessione);

      // setta il nome del file generato dalla composizione
      RisultatoReportType ris = risultato.addNewRisultatoReport();
      ris.setNomeFile(fileComposto);
    }

    return esito;
  }

  /**
   * @param parametri
   * @return
   */
  private List<ValParametroType> copyParametriIntoCollection(ValParametroType[] parametri) {
    List<ValParametroType> array = new ArrayList<ValParametroType>();
    if (parametri != null) {
      for (int i = 0; i < parametri.length; i++) {
        array.add(parametri[i]);
      }
    }
    return array;
  }

  /**
   * Effettua il controllo del report con prospetto nel rispetto del profilo,
   * estrae la chiave, controlla i parametri e quindi lancia la composizione del
   * report con modello previa esecuzione del report collegato come sorgente
   * dati.
   *
   * @param ricerca
   *        ricerca da eseguire
   * @param parametri
   *        parametri in input
   * @param risultato
   *        risultato da popolare
   * @return true se la composizione e' andata a buon fine e senza errori (sia
   *         nella composizione, sia nei controli pre elaborazione), false
   *         altrimenti
   * @throws CriptazioneException
   * @throws IOException
   * @throws TransformerException
   * @throws TransformerFactoryConfigurationError
   * @throws ParserConfigurationException
   * @throws FactoryConfigurationError
   * @throws TransformerConfigurationException
   * @throws FileNotFoundException
   * @throws QueryDaoException
   * @throws SqlComposerException
   */
  private boolean componiModelloConSorgenteDatiReport(DatiGenProspetto ricerca,
      String codiceUfficioIntestatario, ValParametroType[] parametri,
      GetRisultatoReportResponse risultato)
        throws CriptazioneException, SqlComposerException, QueryDaoException,
          FileNotFoundException, TransformerConfigurationException,
          FactoryConfigurationError, ParserConfigurationException,
          TransformerFactoryConfigurationError, TransformerException, IOException {

    // costruzione ProfiloUtente a partire dall'owner della ricerca
    Account account = this.loginManager.getAccountById(ricerca.getDatiGenRicerca().getOwner());
    ProfiloUtente utente = this.loginManager.getProfiloUtente(account,
        ricerca.getDatiGenRicerca().getCodApp());

    String contesto = null;
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI)))
      contesto = utente.getUfficioAppartenenza();

    // si controllano i parametri
    List parametriCompleto = this.copyParametriIntoCollection(parametri);
    List parametriComposizione = new ArrayList();
    boolean esito = this.checkAndSetParametriModello(ricerca,
        parametriCompleto, parametriComposizione, risultato);

    if (esito) {
      // esegue il report base/avanzato sorgente dati e crea il file xml di
      // input
      // per il modello
      String nomeFileSorgenteDati = this.ricercheManager.getFileXmlRisultatoReport(
          ricerca.getDatiModello(), codiceUfficioIntestatario, utente,
          ricerca.getDatiGenRicerca().getCodApp(),
          ConfigManager.getValore(CostantiGenerali.PROP_ID_APPLICAZIONE));

      // esegue la composizione del modello
      String fileComposto = this.modelliManager.componiModelloSenzaConnessioneDB(
          ricerca.getDatiModello().getIdModello(), nomeFileSorgenteDati,
          ricerca.getDatiGenRicerca().getCodApp(), utente.getId(), contesto);

      // setta il nome del file generato dalla composizione
      RisultatoReportType ris = risultato.addNewRisultatoReport();
      ris.setNomeFile(fileComposto);
    }

    return esito;
  }

  /**
   * Si effettuano tutti i controlli di validit&agrave; sui parametri ricevuti
   * nel rispetto della definizione dei parametri della ricerca da eseguire e
   * dell'eventuale dominio di tali parametri e si aggiungono eventuali
   * parametri id utente nella posizione prevista
   *
   * @param ricerca
   *        ricerca da eseguire
   * @param parametri
   *        parametri ricevuti
   * @param parametriComposizione
   *        parametri da costruire per il salvataggio nella W_COMPARAM
   * @param risultato
   *        contenitore del risultato della ricerca
   * @return true se tutti i controlli sono andati a buon fine, false altrimenti
   */
  private boolean checkAndSetParametriModello(DatiGenProspetto ricerca,
      List<ValParametroType> parametri, List<ParametroComposizione> parametriComposizione,
      GetRisultatoReportResponse risultato) {
    boolean esito = true;

    String codice = ricerca.getDatiGenRicerca().getCodReportWS();

    List elencoParametri = new ArrayList();

    if (ricerca.getDatiModello().getIdRicercaSrc() != null) {
      // si aggiungono i parametri della ricerca sorgente dati
      List<?> elencoDefParametriRicerca = this.ricercheManager.getParametriRicerca(
          ricerca.getDatiModello().getIdRicercaSrc().intValue());
      if (elencoDefParametriRicerca != null) {
        ParametroRicerca paramRicerca;
        ParametroModello paramModello;
        for (int i = 0; i < elencoDefParametriRicerca.size(); i++) {
          paramRicerca = (ParametroRicerca) elencoDefParametriRicerca.get(i);
          paramModello = paramRicerca.getParametroModello();
          paramModello.setIdModello(ricerca.getDatiModello().getIdModello());
          elencoParametri.add(paramModello);
        }
      }
    }

    List<?> elencoDefParametriModello = this.modelliManager.getParametriModello(
        ricerca.getDatiModello().getIdModello());
    elencoParametri.addAll(elencoDefParametriModello);

    // si controllano i parametri ricevuti in conformita' con la
    // definizione

    if (elencoParametri.size() == 0
        && parametri != null
        && parametri.size() > 0) {
      // sono arrivati dei parametri quando il report non li prevede
      risultato.setErrore("PARAMS-NOTDEF");
      logger.error("Il report con codice "
          + codice
          + " non prevede parametri, ma ne sono stati ricevuti "
          + parametri.size());
      esito = false;
      // break;
    }

    List<Tabellato> listaTabellato = null;
    ParametroModello parametroIn = null;
    ValParametroType parametro = null;

    for (int i = 0; i < elencoParametri.size() && esito; i++) {
      parametroIn = (ParametroModello) elencoParametri.get(i);

      if (parametroIn.getTipo().equals("U")) {
        // il parametro id utente si aggiunge nella posizione prevista
        parametro = new ValParametroType();
        parametro.setCodice(parametroIn.getCodice());
        parametro.setValore(ricerca.getDatiGenRicerca().getOwner().toString());
        parametri.add(i, parametro);
      } else {
        // se non e' il parametro id utente lo si controlla

        // se mi aspetto dei parametri, devono essere nella posizione
        // corretta
        if (parametri == null
            || i >= parametri.size()
            || !parametroIn.getCodice().equals(
                (parametri.get(i)).getCodice())) {
          risultato.setErrore("PARAM-NOTFOUND[CODE="
              + parametroIn.getCodice()
              + ",POS="
              + i
              + "]");
          logger.error("Il report con codice "
              + codice
              + " prevede in posizione "
              + i
              + " il parametro "
              + parametroIn.getCodice());
          esito = false;
        }

        // se il codice parametro e' corretto, controllo la presenza del
        // valore e la correttezza dello stesso
        if (esito) {
          parametro = (parametri.get(i));
          if (parametro.getValore() == null) {
            // se non e' valorizzato, allora deve essere facoltativo altrimenti
            // e' un errore
            if (parametroIn.getObbligatorio() == 1) {
              risultato.setErrore("PARAM-NOTSET[CODE="
                  + parametroIn.getCodice()
                  + ",POS="
                  + i
                  + "]");
              logger.error("Il report con codice "
                  + codice
                  + " prevede in posizione "
                  + i
                  + " un parametro obbligatorio");
              esito = false;
            }
          } else {
            // parametro con valore, controllo il formato
            if (parametroIn.getTipo().equals("D")
                && UtilityDate.convertiData(parametro.getValore(),
                    UtilityDate.FORMATO_GG_MM_AAAA) == null) {
              risultato.setErrore("PARAM-FORMAT[CODE="
                  + parametroIn.getCodice()
                  + ",POS="
                  + i
                  + "]");
              logger.error("Ricevuta una data errata ("
                  + parametro.getValore()
                  + ") in posizione "
                  + i
                  + "  nel report con codice "
                  + codice);
              esito = false;
            } else {
              // si setta il valore nel formato da salvare nella tabella di
              // appoggio per il compositore
              parametro.setValore(StringUtils.replace(parametro.getValore(), "/", "."));
            }

            if (parametroIn.getTipo().equals("I")
                && UtilityNumeri.convertiIntero(parametro.getValore()) == null) {
              risultato.setErrore("PARAM-FORMAT[CODE="
                  + parametroIn.getCodice()
                  + ",POS="
                  + i
                  + "]");
              logger.error("Ricevuto un intero errato ("
                  + parametro.getValore()
                  + ") in posizione "
                  + i
                  + "  nel report con codice "
                  + codice);
              esito = false;
            }

            if (parametroIn.getTipo().equals("F")) {
              // il parametro se arriva valorizzato contiene il punto decimale
              // per cui per fare il controllo ci cambio il punto con la virgola
              // decimale, ma poi lo lascio inalterato perche' e' nel formato
              // corretto per essere salvato nella tabella di appoggio per il
              // compositore
              if (UtilityNumeri.convertiDouble(StringUtils.replace(
                  parametro.getValore(), ".", ",")) == null) {
                risultato.setErrore("PARAM-FORMAT[CODE="
                    + parametroIn.getCodice()
                    + ",POS="
                    + i
                    + "]");
                logger.error("Ricevuto un numero decimale errato ("
                    + parametro.getValore()
                    + ") in posizione "
                    + i
                    + "  nel report con codice "
                    + codice);
                esito = false;
              }
            }

            if (parametroIn.getTipo().equals("T")
                && parametroIn.getTabellato() != null) {
              // gli unici tabellati senza tabCod valorizzato sono al
              // momento i tabellati in KRONOS; l'importante è proteggere
              // l'applicativo da chiamate indesiderate all'elenco dei valori di
              // tabellato quando non c'è un valore significativo di codice
              listaTabellato = tabellatiManager.getTabellato(parametroIn.getTabellato());
              boolean codiceTrovato = false;
              for (int j = 0; j < listaTabellato.size() && !codiceTrovato; j++) {
                Tabellato tabellato = listaTabellato.get(j);
                if (tabellato.getTipoTabellato().equals(parametro.getValore()))
                  codiceTrovato = true;
              }
              if (!codiceTrovato) {
                risultato.setErrore("PARAM-FORMAT[CODE="
                    + parametroIn.getCodice()
                    + ",POS="
                    + i
                    + "]");
                logger.error("Ricevuto un dato tabellato errato ("
                    + parametro.getValore()
                    + ") in posizione "
                    + i
                    + "  nel report con codice "
                    + codice);
                esito = false;
              } else {
                // se il dato tabellato e' corretto, aggiungo il parametro con
                // la descrizione del tabellato scelto
                Campo campo = DizionarioCampi.getInstance().get(
                    parametroIn.getTabellato());
                String descrizioneTabellato = this.tabellatiManager.getDescrTabellato(
                    campo.getCodiceTabellato(), parametro.getValore());

                ParametroComposizione pc = new ParametroComposizione();
                pc.setCodice(parametroIn.getCodice() + "DESC");
                pc.setDescrizione(parametroIn.getDescrizione());
                pc.setValore(descrizioneTabellato);
                parametriComposizione.add(pc);
              }
            }

            if (parametroIn.getTipo().equals("M")) {
              int voceMenu = -1;
              try {
                voceMenu = Integer.parseInt(parametro.getValore());
              } catch (NumberFormatException e) {
              }
              if (voceMenu < 0
                  || voceMenu > parametroIn.getMenu().split("|").length) {
                risultato.setErrore("PARAM-FORMAT[CODE="
                    + parametroIn.getCodice()
                    + ",POS="
                    + i
                    + "]");
                logger.error("Ricevuto un dato a menu errato ("
                    + parametro.getValore()
                    + ") in posizione "
                    + i
                    + "  nel report con codice "
                    + codice);
                esito = false;
              }
            }
          }
        }
      }

      if (esito) {
        // se i controlli sono andati a buon fine si crea il parametro della
        // composizione
        ParametroComposizione pc = new ParametroComposizione();
        pc.setCodice(parametroIn.getCodice());
        pc.setDescrizione(parametroIn.getDescrizione());
        pc.setValore(parametro.getValore());
        parametriComposizione.add(pc);
      }
    }

    if (esito) {
      if (parametri != null && parametri.size() != elencoParametri.size()) {
        // sono arrivati dei parametri in piu' rispetto a quelli che il report
        // prevede
        risultato.setErrore("PARAMS-UNKNOWN");
        logger.error("Il report con codice "
            + codice
            + " prevede "
            + elencoParametri.size()
            + " parametri, ma ne sono stati ricevuti "
            + parametri.size());
        esito = false;
        // break;
      }
    }

    return esito;
  }

  /**
   * Estrae un documento digitale allegato mediante l'applicativo
   *
   * @param idProgramma
   *        eventuale id del programma collegato al documento
   * @param idDocumento
   *        identificativo del documento
   * @return documento allegato (nome + contenuto), o eventualmente il codice di errore se non e' possibile estrarlo
   */
  public String getAllegato(String idProgramma, long idDocumento) {
    if (logger.isDebugEnabled()) logger.debug("getAllegato(" + idProgramma + ", " + idDocumento + "): inizio metodo");

    GetAllegatoResponseDocument document = GetAllegatoResponseDocument.Factory.newInstance();
    GetAllegatoResponse risultato = document.addNewGetAllegatoResponse();

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato e la funzionalita' abilitata
    if (WebUtilities.isAppNotReady()) {
      risultato.setErrore("APP-NOTREADY");
    } else if (!GeneManager.checkOP(this.servletContext, CostantiGenerali.OPZIONE_GESTIONE_PUBBL_REPORT_WS)) {
      logger.error("Accesso non autorizzato alla richiesta estrazione di un allegato");
      risultato.setErrore("UNAUTHORIZED");
    } else {
      // se idProgramma non e' valorizzato, lo si ricava dalle properties (attenzione, il sistema non funziona con property settata con piu'
      // codici applicazione)
      String idPrg = StringUtils.stripToNull(idProgramma);
      if (idPrg == null) {
        idPrg = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
      }
      try {
        BlobFile file = this.fileAllegatoManager.getFileAllegato(idPrg, idDocumento);
        if (file == null) {
          logger.error("Richiesto un allegato inesistente per idprg=" + idPrg + ", idcom=" + idDocumento);
          risultato.setErrore("NOT-FOUND");
        } else {
          AllegatoType allegato = risultato.addNewAllegato();
          allegato.setNomeFile(file.getNome());
          allegato.setContenutoFile(file.getStream());
        }
      } catch (IOException e) {
        risultato.setErrore("DB-ERROR");
        logger.error(this.resBundleGenerale.getString("errors.database.dataAccessException"), e);
      } catch (DataAccessException e) {
        risultato.setErrore("DB-ERROR");
        logger.error(this.resBundleGenerale.getString("errors.database.dataAccessException"), e);
      } catch (Throwable t) {
        risultato.setErrore("UNEXP-ERROR");
        logger.error(this.resBundleGenerale.getString("errors.applicazione.inaspettataException"), t);
      }
    }

    if (logger.isDebugEnabled()) logger.debug("getAllegato(" + idProgramma + ", " + idDocumento + "): fine metodo");

    return document.toString();
  }

}
