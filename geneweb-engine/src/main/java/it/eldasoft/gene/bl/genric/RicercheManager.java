/*
 * Created on 20-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.genric;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.MetadatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiIntegrazioneKronos;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.dao.GruppiDao;
import it.eldasoft.gene.db.dao.KronosDao;
import it.eldasoft.gene.db.dao.MetadatiDao;
import it.eldasoft.gene.db.dao.QueryDao;
import it.eldasoft.gene.db.dao.QueryDaoException;
import it.eldasoft.gene.db.dao.RicercheDao;
import it.eldasoft.gene.db.dao.TabellatiDao;
import it.eldasoft.gene.db.dao.jdbc.InputStmt;
import it.eldasoft.gene.db.dao.jdbc.ListaDati;
import it.eldasoft.gene.db.dao.jdbc.ParametroStmt;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.db.domain.admin.RicercaGruppo;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.db.domain.genric.CacheParametroEsecuzione;
import it.eldasoft.gene.db.domain.genric.CampoRicerca;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.gene.db.domain.genric.DatiRisultato;
import it.eldasoft.gene.db.domain.genric.ElementoRisultato;
import it.eldasoft.gene.db.domain.genric.FiltroRicerca;
import it.eldasoft.gene.db.domain.genric.GiunzioneRicerca;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.gene.db.domain.genric.OrdinamentoRicerca;
import it.eldasoft.gene.db.domain.genric.ParametroRicerca;
import it.eldasoft.gene.db.domain.genric.RigaRisultato;
import it.eldasoft.gene.db.domain.genric.TabellaRicerca;
import it.eldasoft.gene.db.domain.genric.TrovaRicerche;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.LegameTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.FiltroLivelloUtente;
import it.eldasoft.utils.profiles.cache.DizionarioLivelli;
import it.eldasoft.utils.profiles.domain.Livello;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlCampo;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlElementoCondizione;
import it.eldasoft.utils.sql.comp.SqlElementoDecorabile;
import it.eldasoft.utils.sql.comp.SqlElementoOrderBy;
import it.eldasoft.utils.sql.comp.SqlElementoSelect;
import it.eldasoft.utils.sql.comp.SqlJoin;
import it.eldasoft.utils.sql.comp.SqlManager;
import it.eldasoft.utils.sql.comp.SqlStringa;
import it.eldasoft.utils.sql.comp.SqlTabella;
import it.eldasoft.utils.sql.comp.SqlValoriInclusione;
import it.eldasoft.utils.sql.comp.func.FactoryFunzione;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityHashMap;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte di gestione delle ricerche e l'associazione o meno ad un gruppo
 *
 * @author Luca.Giacomazzo
 */
public class RicercheManager {

  private static final int    NUMERO_PAGINA_FITTIZIO                = Integer.MIN_VALUE;

  public static final String  PREFISSO_CAMPI_SELECT                 = "CAMPO_";
  private static final String PREFISSO_CAMPI_CHIAVE_SELECT          = "CHIAVE_";

  private static final String PROP_NUMERO_MASSIMO_RECORD_ESTRAIBILI = "it.eldasoft.generatoreRicerche.maxNumRecord";

  /** Logger Log4J di classe */
  static Logger               logger                                = Logger.getLogger(RicercheManager.class);

  /** Reference al Dao per l'interrogazione della tabella W_RICERCHE */
  private RicercheDao         ricercheDao;

  /** Reference al Dao per l'interrogazione della tabella W_GRUPPI */
  private GruppiDao           gruppiDao;

  /** Reference al Dao per l'interrogazione dei tabellati */
  private TabellatiDao        tabellatiDao;

  /** Reference al manager per la gestione della tabella W_GENCHIAVI */
  private GenChiaviManager    genChiaviManager;

  /** Reference al Dao per la gestione delle query generiche su DB */
  private QueryDao            queryDao;

  /** Reference al Dao per la gestione dei metadati */
  private MetadatiDao         metadatiDao;

  /** Reference al manager per la gestione dei prospetti */
  private ProspettoManager    prospettoManager;

  /** Rerefence al Dao per l'integrazione con Kronos */
  private KronosDao           kronosDao;

  /**
   * @param ricercheDao
   *        ricercheDao da settare internamente alla classe.
   */
  public void setRicercheDao(RicercheDao ricercheDao) {
    this.ricercheDao = ricercheDao;
  }

  /**
   * @param genChiaviManager
   *        genChiaviManager da settare internamente alla classe.
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * @param gruppiDao
   *        gruppiDao da settare internamente alla classe.
   */
  public void setGruppiDao(GruppiDao gruppiDao) {
    this.gruppiDao = gruppiDao;
  }

  /**
   * @param queryDao
   *        queryDao da settare internamente alla classe.
   */
  public void setQueryDao(QueryDao queryDao) {
    this.queryDao = queryDao;
  }

  /**
   * @param tabellatiDao
   *        tabellatiDao da settare internamente alla classe.
   */
  public void setTabellatiDao(TabellatiDao tabellatiDao) {
    this.tabellatiDao = tabellatiDao;
  }

  /**
   * @param metadatiManager
   *        metadatiManager da settare internamente alla classe.
   */
  public void setMetadatiDao(MetadatiDao metadatiManager) {
    this.metadatiDao = metadatiManager;
  }

  /**
   * @param prospettoManager
   *        prospettoManager da settare internamente alla classe.
   */
  public void setProspettoManager(ProspettoManager prospettoManager) {
    this.prospettoManager = prospettoManager;
  }

  /**
   * @param kronosDao
   *        kronosDao da settare internamente alla classe.
   */
  public void setKronosDao(KronosDao kronosDao) {
    this.kronosDao = kronosDao;
  }

  /**
   * @return Ritorna la lista delle ricerche associate al gruppo in analisi
   */
  public List<?> getRicercheDiGruppo(int idGruppo, String codiceApplicazione,
      String codiceProfilo) {
    return this.ricercheDao.getRicercheDiGruppo(idGruppo, codiceApplicazione,
        codiceProfilo);
  }

  /**
   * @param idGruppo
   *        id del gruppo
   * @param codiceApplicazione
   *        codice dell'applicazione
   * @param codiceProfilo
   *        codice del profilo attivo
   * @return Ritorna la lista di tutte ricerche associate ad un gruppo, con
   *         l'attributo 'associato' popolato
   *
   */
  public List<?> getRicercheConAssociazioneGruppo(int idGruppo,
      String codiceApplicazione, String codiceProfilo) {
    // Lista di tutte le ricerche
    List<?> listaRicerche = this.ricercheDao.getAllRicercheByCodApp(
        codiceApplicazione, codiceProfilo);
    // Lista delle ricerche associate al gruppo in analisi
    List<?> listaRicercheGruppo = this.ricercheDao.getRicercheDiGruppo(idGruppo,
        codiceApplicazione, codiceProfilo);

    // Osservazione: entrambe le liste appena estratte sono ordinate per nome (e
    // non per idRicerca) e la lunghezza della lista delle ricerche di gruppo è
    // minore o uguale della lunghezza della lista delle ricerche.
    ListIterator<?> iterRicercheGruppo = listaRicercheGruppo.listIterator();
    ListIterator<?> iterRicerche = listaRicerche.listIterator();
    int idRicercaGruppo = 0;
    RicercaGruppo ricerca = null;
    RicercaGruppo ricercaGruppo = null;

    while (iterRicercheGruppo.hasNext()) {
      ricercaGruppo = (RicercaGruppo) iterRicercheGruppo.next();
      idRicercaGruppo = ricercaGruppo.getIdRicerca();

      boolean test = false;
      while (iterRicerche.hasNext() && !test) {
        ricerca = (RicercaGruppo) iterRicerche.next();
        if (idRicercaGruppo == ricerca.getIdRicerca()) {
          ricerca.setAssociato(true);
          test = true;
        } else
          ricerca.setAssociato(false);
      }
    }
    return listaRicerche;
  }

  public void updateAssociazioneRicercheGruppo(int idGruppo,
      String[] idRicercheAssociate, String codiceApplicazione,
      String profiloAttivo) {

    List<Integer> listaRicercheAssociate = new ArrayList<Integer>();

    if (idRicercheAssociate != null) {
      // popolamento della lista appena create
      for (int i = 0; i < idRicercheAssociate.length; i++) {
        listaRicercheAssociate.add(new Integer(idRicercheAssociate[i]));
      }

      // Delete delle ricerche non associate al gruppo in analisi
      this.ricercheDao.deleteRicercheNonAssociate(idGruppo, codiceApplicazione,
          listaRicercheAssociate);

      if (!listaRicercheAssociate.isEmpty()) {
        // select delle ricerche già associate al gruppo in analisi
        List<?> listaRicerchePreAssociate = this.ricercheDao.getRicercheAssociateAGruppoAsList(
            idGruppo, codiceApplicazione, profiloAttivo);

        Map<Integer, RicercaGruppo> mappaRicerchePreAssociate = new HashMap<Integer, RicercaGruppo>();
        Iterator<?> iterRicerchePerAssociate = listaRicerchePreAssociate.iterator();

        RicercaGruppo ricercaGruppo = null;
        while (iterRicerchePerAssociate.hasNext()) {
          ricercaGruppo = (RicercaGruppo) iterRicerchePerAssociate.next();
          mappaRicerchePreAssociate.put(
              new Integer(ricercaGruppo.getIdRicerca()), ricercaGruppo);
        }
        Iterator<?> iter = listaRicercheAssociate.iterator();
        Integer idRicerca = null;

        // insert delle ricerche da associare al gruppo in analisi,
        if (!mappaRicerchePreAssociate.isEmpty()) {
          // Esistono riceche già associate al gruppo in analisi, perciò devo
          // controllare che l'idRicerca delle ricerche da inserire (contenute
          // in listaRicercheAss) siano presenti o meno nella tabella W_GRPRIC
          while (iter.hasNext()) {
            idRicerca = (Integer) iter.next();
            if (!mappaRicerchePreAssociate.containsKey(idRicerca)) {
              this.ricercheDao.insertAssociazioneRicercaGruppo(idGruppo,
                  idRicerca.intValue());
            }
          }
        } else {
          // Non esistono ricerche pre associate al gruppo in analisi e posso
          // quindi inserire le ricerche presenti nella lista
          // 'listaRicercheAssociate' senza alcun controllo
          while (iter.hasNext()) {
            idRicerca = (Integer) iter.next();
            this.ricercheDao.insertAssociazioneRicercaGruppo(idGruppo,
                idRicerca.intValue());
          }

        }
      }
    } else {
      // Delete di tutti i modelli associati al gruppo in analisi
      this.ricercheDao.deleteRicercheNonAssociate(idGruppo, codiceApplicazione,
          listaRicercheAssociate);
    }
  }

  /**
   * Estrae la lista di ricerche esistenti in linea con i filtri impostati nella
   * pagina TrovaRicerche.
   *
   * @param trovaRicerche
   *        criteri di ricerca
   * @return elenco di ricerche
   * @throws SqlComposerException
   * @throws DataAccessException
   */
  public List<?> getRicerche(TrovaRicerche trovaRicerche,
      boolean mostraRicercheBase) throws SqlComposerException {
    return this.ricercheDao.getRicerche(trovaRicerche, mostraRicercheBase);
  }

  /**
   * Estrae la lista di Ricerche Predefinite/Pubblicate filtrate per idAccount e
   * applicazione
   *
   * @param idAccount
   *        id dell'account
   * @param codiceApplicazione
   *        codice dell'applicazione
   * @return
   */
  public List<?> getRicerchePredefinite(int idAccount, String codiceApplicazione,
      String codiceProfilo, boolean mostraReportBase, boolean mostraReportSql) {
    return this.ricercheDao.getRicerchePredefinite(idAccount,
        codiceApplicazione, codiceProfilo, mostraReportBase, mostraReportSql);
  }

  /**
   * Estrae la lista di ricerche esistenti in linea con i filtri impostati nella
   * pagina TrovaRicerche escludendo tutte quelle che necessitano di parametri
   * per l'esecuzione.
   *
   * @param trovaRicerche
   *        criteri di ricerca
   * @return elenco di ricerche
   * @throws SqlComposerException
   * @throws DataAccessException
   */
  public List<?> getRicercheSenzaParametri(TrovaRicerche trovaRicerche,
      boolean mostraRicercheBase) throws SqlComposerException {
    return this.ricercheDao.getRicercheSenzaParametri(trovaRicerche,
        mostraRicercheBase);
  }

  /**
   * Estrae la lista di Ricerche Predefinite/Pubblicate filtrate per idAccount e
   * applicazione escludendo tutte quelle che necessitano di parametri per
   * l'esecuzione
   *
   * @param idAccount
   *        id dell'account
   * @param codiceApplicazione
   *        codice dell'applicazione
   * @return
   */
  public List<?> getRicerchePredefiniteSenzaParametri(int idAccount,
      String codiceApplicazione, String codiceProfilo, boolean mostraReportBase) {
    return this.ricercheDao.getRicerchePredefiniteSenzaParametri(idAccount,
        codiceApplicazione, codiceProfilo, mostraReportBase);
  }

  /**
   * Restituisce true se la ricerca possiede dei parametri
   *
   * @param idRicerca
   * @return
   * @throws DataAccessException
   */
  public boolean isRicercaConParametri(int idRicerca)
      throws DataAccessException {
    boolean isRicercaConParametri = false;
    if (this.ricercheDao.getNumeroParametriRicercaByIdRicerca(idRicerca).intValue() > 0)
      isRicercaConParametri = true;
    if (this.ricercheDao.getNumeroParametriProspettoByIdRicerca(idRicerca).intValue() > 0)
      isRicercaConParametri = true;
    if (this.ricercheDao.getNumeroParametriReportSorgenteProspettoByIdRicerca(
        idRicerca).intValue() > 0) isRicercaConParametri = true;
    return isRicercaConParametri;
  }

  /**
   * Ritorna la famiglia della ricerca
   *
   * @param idRicerca
   * @return
   * @throws DataAccessException
   */
  public Integer getFamigliaRicercaById(int idRicerca)
      throws DataAccessException {
    return this.ricercheDao.getFamigliaRicercaById(idRicerca);
  }

  /**
   * Elimina tutto il contenuto di una singola ricerca:
   * <ul>
   * <li>il record in W_RICERCHE</li>
   * <li>tutti i record in W_GRPRIC associati alla stessa riga in W_RICERCHE</li>
   * <li>tutti i record in tutte le tabelle con informazioni di dettaglio della
   * ricerca, ovvero W_RICTAB, W_RICCAMPI, W_RICJOIN, W_RICPARAM, W_RICFILTRI,
   * W_RICORD, W_GRPRIC</li>
   * </ul>
   *
   * @param idRicerca
   *        chiave primaria della ricerca da eliminare
   */
  public void deleteRicerca(int idRicerca) {
    List<Integer> elencoIdRicerca = new ArrayList<Integer>();
    elencoIdRicerca.add(new Integer(idRicerca));

    // eliminazione della definizione della ricerca
    this.deleteDefinizioneRicerca(elencoIdRicerca);
    // eliminazione della cache dei parametri della ricerca
    this.ricercheDao.deleteCacheParametriEsecuzioneRicerca(idRicerca);

  }

  /**
   * Elimina l'intera definizione di un set di ricerche
   *
   * @param elencoIdRicerca
   *        elenco di idRicerca da eliminare
   */
  private void deleteDefinizioneRicerca(List<?> elencoIdRicerca) {
    this.ricercheDao.deleteGruppiByIdRicerca(elencoIdRicerca);
    this.ricercheDao.deleteTabelleByIdRicerca(elencoIdRicerca);
    this.ricercheDao.deleteCampiByIdRicerca(elencoIdRicerca);
    this.ricercheDao.deleteJoinByIdRicerca(elencoIdRicerca);
    this.ricercheDao.deleteFiltriByIdRicerca(elencoIdRicerca);
    this.ricercheDao.deleteOrdinamentiByIdRicerca(elencoIdRicerca);
    this.ricercheDao.deleteParametriByIdRicerca(elencoIdRicerca);
    this.ricercheDao.deleteRicercheById(elencoIdRicerca);
  }

  /**
   * Elimina tutto il contenuto di un elenco di ricerche.
   *
   * @param idRicerche
   *        elenco delle chiavi primarie della ricerche da eliminare
   * @param codiceApplicazione
   *        codice applicazione per reperire i path ai prospetti da eliminare
   * @throws IOException
   *         eccezione emessa nel qual caso il file del prospetto non sia
   *         eliminabile
   */
  public void deleteRicerche(int[] idRicerche, String codiceApplicazione)
      throws IOException {
    List<Integer> elencoRicercheNonProspetto = new ArrayList<Integer>();
    List<Integer> elencoProspetti = new ArrayList<Integer>();
    DatiGenRicerca datiGenRicerca = null;

    for (int i = 0; i < idRicerche.length; i++) {
      datiGenRicerca = this.ricercheDao.getTestataRicercaByIdRicerca(idRicerche[i]);
      if (datiGenRicerca.getIdProspetto() != null
          && datiGenRicerca.getIdProspetto().intValue() > 0)
        elencoProspetti.add(datiGenRicerca.getIdRicerca());
      else
        elencoRicercheNonProspetto.add(new Integer(idRicerche[i]));
    }

    if (elencoProspetti.size() > 0) {
      for (int i = 0; i < elencoProspetti.size(); i++)
        this.prospettoManager.deleteProspetto(
            (elencoProspetti.get(i)).intValue(), codiceApplicazione);
    }

    if (elencoRicercheNonProspetto.size() > 0) {
      for (int i = 0; i < elencoRicercheNonProspetto.size(); i++)
        this.deleteRicerca((elencoRicercheNonProspetto.get(i)).intValue());
    }
  }

  /**
   * Estrae i soli dati della testata W_RICERCHE di una ricerca a partire
   * dall'id
   *
   * @param idRicerca
   *        id della ricerca
   * @return dati generali della ricerca
   */
  public DatiGenRicerca getDatiGenRicerca(int idRicerca) {
    return this.ricercheDao.getTestataRicercaByIdRicerca(idRicerca);
  }

  public ContenitoreDatiRicerca getRicercaByIdRicerca(int idRicerca) {
    ContenitoreDatiRicerca contenitore = new ContenitoreDatiRicerca();

    Iterator<?> iter = null;

    DatiGenRicerca ricerca = null;
    GruppoRicerca gruppoRicerca = null;

    TabellaRicerca tabellaRicerca = null;
    CampoRicerca campoRicerca = null;
    GiunzioneRicerca joinRicerca = null;
    ParametroRicerca parametroRicerca = null;
    FiltroRicerca filtroRicerca = null;
    OrdinamentoRicerca ordinamentoRicerca = null;

    // Tabella principale di testata: W_RICERCHE
    ricerca = this.ricercheDao.getTestataRicercaByIdRicerca(idRicerca);
    contenitore.setDatiGenerali(ricerca);

    // Tabella secondaria per i ruoli: W_GRPRIC
    List<?> lista = this.gruppiDao.getGruppiByIdRicerca(idRicerca);
    iter = lista.iterator();
    while (iter.hasNext()) {
      gruppoRicerca = new GruppoRicerca((Gruppo) iter.next());
      contenitore.aggiungiGruppo(gruppoRicerca);
    }

    lista = new ArrayList();
    // Tabella secondaria per le tabelle da utilizzare : W_RICTAB
    lista = this.ricercheDao.getTabelleRicercaByIdRicerca(idRicerca);
    iter = lista.iterator();
    while (iter.hasNext()) {
      tabellaRicerca = (TabellaRicerca) iter.next();
      contenitore.aggiungiTabella(tabellaRicerca);
    }

    lista = new ArrayList();
    // Tabella secondaria per i campi da estrarre : W_RICCAMPI
    lista = this.ricercheDao.getCampiRicercaByIdRicerca(idRicerca);
    iter = lista.iterator();
    while (iter.hasNext()) {
      campoRicerca = (CampoRicerca) iter.next();
      contenitore.aggiungiCampo(campoRicerca);
    }

    lista = new ArrayList();
    // Tabella secondaria per le join da utilizzare : W_RICJOIN
    lista = this.ricercheDao.getGiunzioniRicercaByIdRicerca(idRicerca);
    iter = lista.iterator();
    while (iter.hasNext()) {
      joinRicerca = (GiunzioneRicerca) iter.next();
      contenitore.aggiungiGiunzione(joinRicerca);
    }

    lista = new ArrayList();
    // Tabella secondaria per i parametri da utilizzare : W_RICPARAM
    lista = this.ricercheDao.getParametriRicercaByIdRicerca(idRicerca);
    iter = lista.iterator();
    while (iter.hasNext()) {
      parametroRicerca = (ParametroRicerca) iter.next();
      contenitore.aggiungiParametro(parametroRicerca);
    }

    // Tabella secondaria per le condizioni di filtro da utilizzare :
    // W_RICFILTRI
    lista = new ArrayList();
    lista = this.ricercheDao.getFiltriRicercaByIdRicerca(idRicerca);
    iter = lista.iterator();
    while (iter.hasNext()) {
      filtroRicerca = (FiltroRicerca) iter.next();
      contenitore.aggiungiFiltro(filtroRicerca);
    }

    // Tabella secondaria per gli ordinamenti da applicare : W_RICORD
    lista = new ArrayList();
    lista = this.ricercheDao.getOrdinamentiRicercaByIdRicerca(idRicerca);
    iter = lista.iterator();
    while (iter.hasNext()) {
      ordinamentoRicerca = (OrdinamentoRicerca) iter.next();
      contenitore.aggiungiOrdinamento(ordinamentoRicerca);
    }

    return contenitore;
  }

  /**
   * Inserisce una ricerca nella base dati, inserendo in un unica transazione
   * sia i dati di testata che tutti i dati collegati
   *
   * @param ricerca
   *        ricerca da inserire
   * @return ricerca aggiornata con l'id attribuito
   */
  public ContenitoreDatiRicerca insertRicerca(ContenitoreDatiRicerca ricerca) {

    // Generazione di idRicerca da associare alla nuova ricerca
    int idRicerca = this.genChiaviManager.getNextId("W_RICERCHE");

    // set di tutti gli attributi che rappresentano 'idRicerca' all'interno
    // degli oggetti
    // contenuti nei vari elenchi di cui ContenitoreDatiRicerca e' costituito
    ricerca.setIdRicerca(idRicerca);

    this.insertContenitoreRicerca(ricerca);

    return ricerca;
  }

  /**
   * Inserisce una ricerca nella base dati, inserendo in un unica transazione
   * sia i dati di testata che tutti i dati collegati
   *
   * @param ricerca
   *        ricerca da inserire
   * @return ricerca aggiornata con l'id attribuito
   */
  private void insertContenitoreRicerca(ContenitoreDatiRicerca ricerca) {

    DatiGenRicerca testataRicerca = null;
    GruppoRicerca gruppoRicerca = null;
    TabellaRicerca tabellaRicerca = null;
    CampoRicerca campoRicerca = null;
    GiunzioneRicerca joinRicerca = null;
    ParametroRicerca parametroRicerca = null;
    FiltroRicerca filtroRicerca = null;
    OrdinamentoRicerca ordinamentoRicerca = null;

    // inserisco i dati di testata della ricerca
    testataRicerca = ricerca.getDatiGenerali();
    this.ricercheDao.insertTestataRicerca(testataRicerca);

    // inserisco i gruppi degli utenti associati alla ricerca
    for (int i = 0; i < ricerca.getNumeroGruppi(); i++) {
      gruppoRicerca = ricerca.estraiGruppo(i);
      this.ricercheDao.insertGruppoRicerca(gruppoRicerca);
    }

    // inserisco tutte le tabelle che entrano in gioco
    for (int i = 0; i < ricerca.getNumeroTabelle(); i++) {
      tabellaRicerca = ricerca.estraiArgomento(i);
      this.ricercheDao.insertTabellaRicerca(tabellaRicerca);
    }

    // inserisco tutti campi da estrarre
    for (int i = 0; i < ricerca.getNumeroCampi(); i++) {
      campoRicerca = ricerca.estraiCampo(i);
      this.ricercheDao.insertCampoRicerca(campoRicerca);
    }

    // inserisco tutte le join da utilizzare per legare le tabelle
    for (int i = 0; i < ricerca.getNumeroGiunzioni(); i++) {
      joinRicerca = ricerca.estraiGiunzione(i);
      this.ricercheDao.insertGiunzioneRicerca(joinRicerca);
    }

    // inserisco tutte le condizioni da utilizzare per filtrare i dati
    for (int i = 0; i < ricerca.getNumeroFiltri(); i++) {
      filtroRicerca = ricerca.estraiFiltro(i);
      this.ricercheDao.insertFiltroRicerca(filtroRicerca);
    }

    // inserisco tutti i parametri della ricerca
    for (int i = 0; i < ricerca.getNumeroParametri(); i++) {
      parametroRicerca = ricerca.estraiParametro(i);
      this.ricercheDao.insertParametroRicerca(parametroRicerca);
    }

    // inserisco l'ordinamento da applicare
    for (int i = 0; i < ricerca.getNumeroOrdinamenti(); i++) {
      ordinamentoRicerca = ricerca.estraiOrdinamento(i);
      this.ricercheDao.insertOrdinamentoRicerca(ordinamentoRicerca);
    }
  }

  /**
   * Aggiorna la ricerca individuata dal contenitore in input eseguendo una
   * eliminazione di tutti i dati associati alla ricerca ed un successivo
   * inserimento di tutti i nuovi dati
   *
   * @param contenitorePerModel
   *        ricerca da aggiornare
   */
  public void updateRicerca(ContenitoreDatiRicerca contenitorePerModel) {
    List<Integer> elencoIdRicerca = new ArrayList<Integer>();
    elencoIdRicerca.add(contenitorePerModel.getDatiGenerali().getIdRicerca());
    this.deleteDefinizioneRicerca(elencoIdRicerca);
    this.insertContenitoreRicerca(contenitorePerModel);
  }

  /**
   * Estrae il risultato della ricerca a partire dai criteri individuati e dai
   * parametri indicati in input
   *
   * @param contenitorePerModel
   *        ricerca da cui partire per estrarre i dati
   * @param codiceUfficioIntestatarioAttivo
   *        codice dell'ufficio intestatario con cui filtrare i dati
   * @param parametriUtente
   *        parametri valorizzati all'atto dell'esecuzione da parte dell'utente
   * @param profiloUtente
   *        profilo dell'utente
   * @param codApp
   *        codice dell'appicazione in uso
   * @param numeroPagina
   *        numero di pagina dei dati da estrarre
   * @return contenitore con i dati estratti
   * @throws SqlComposerException
   * @throws QueryDaoException
   * @throws DataAccessException
   */
  public DatiRisultato getRisultatiRicerca(ContenitoreDatiRicerca ricerca,
      String codiceUfficioIntestatarioAttivo, String[] parametriUtente,
      ProfiloUtente profiloUtente, String codApp, int numeroPagina)
          throws SqlComposerException, DataAccessException, QueryDaoException {

    if (logger.isDebugEnabled())
      logger.debug("getRisultatiRicerca: inizio metodo");

    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    DatiRisultato datiRisultato = new DatiRisultato();

    Vector<Campo> elencoCampiChiave = this.buildSqlEParametri(ricerca, parametriUtente,
        profiloUtente, codiceUfficioIntestatarioAttivo, datiRisultato);

    if (logger.isDebugEnabled())
      logger.debug("Esecuzione query di ricerca = " + datiRisultato.getQuerySql());
    // Log dei parametri della ricerca
    if (logger.isDebugEnabled())
      logger.debug("Parametri della ricerca = " + datiRisultato.toStringParametriSql());

    // Set del testo della query nel risultato della ricerca
    Vector<String> setTabellati = new Vector<String>();
    Campo[] campi = this.estraiCampi(ricerca, elencoCampiChiave, dizCampi,
        setTabellati);

    // FASE 5: si esegue la query
    int numeroMaxRecord = 10000;
    if (UtilityNumeri.convertiIntero(ConfigManager.getValore(RicercheManager.PROP_NUMERO_MASSIMO_RECORD_ESTRAIBILI)) != null)
      numeroMaxRecord = UtilityNumeri.convertiIntero(
          ConfigManager.getValore(RicercheManager.PROP_NUMERO_MASSIMO_RECORD_ESTRAIBILI)).intValue();

    ListaDati listaDatiEstratti = null;

    if (ricerca.getDatiGenerali().getRisPerPag() != null) {
      listaDatiEstratti = this.queryDao.getDatiSelect(
          datiRisultato.getQuerySql(), datiRisultato.getParametriSql(), campi,
          numeroPagina, ricerca.getDatiGenerali().getRisPerPag().intValue(),
          numeroMaxRecord);
    } else {
      listaDatiEstratti = this.queryDao.getDatiSelect(
          datiRisultato.getQuerySql(), datiRisultato.getParametriSql(), campi,
          numeroMaxRecord, true);
    }

    List<?> datiEstratti = listaDatiEstratti.getListaDati();

    // FASE 6: popolamento risultato
    RigaRisultato rigaRisultato = null;
    ElementoRisultato elemento = null;
    if (datiEstratti != null && datiEstratti.size() > 0) {

      if (logger.isInfoEnabled())
        logger.info("Sono state estratte " + datiEstratti.size() + " righe");

      // si estraggono tutti i tabellati necessari nella ricerca
      Hashtable<String, List<Tabellato>> hashTabellati = new Hashtable<String, List<Tabellato>>();
      for (int i = 0; i < setTabellati.size(); i++) {
        String codiceTabellato = setTabellati.elementAt(i);
        hashTabellati.put(codiceTabellato,
            this.tabellatiDao.getTabellati(codiceTabellato));
      }

      for (int cRighe = 0; cRighe < datiEstratti.size(); cRighe++) {
        rigaRisultato = new RigaRisultato();
        // si popola la riga del risultato
        for (int cColonne = 0; cColonne < campi.length
            - elencoCampiChiave.size(); cColonne++) {
          elemento = new ElementoRisultato();
          // elemento.setValore(this.convertiOggettoAStringa(((HashMap)
          // datiEstratti.get(cRighe)).get(PREFISSO_CAMPI_SELECT
          // + (cColonne + 1))));
          elemento.setValore(UtilityHashMap.getValueCaseInsensitive(
              (HashMap<?,?>) datiEstratti.get(cRighe), PREFISSO_CAMPI_SELECT
                  + (cColonne + 1)));
          // se il campo è un tabellato allora si esegue la decodifica del
          // codice con la corrispondente descrizione
          if (campi[cColonne].getCodiceTabellato() != null) {
            elemento.setTipo(Campo.TIPO_STRINGA);
            elemento.setValore(this.estraiDescrizioneTabellato(
                hashTabellati.get(campi[cColonne].getCodiceTabellato()),
                this.convertiOggettoAStringa(elemento.getValore())));
          } else
            elemento.setTipo(campi[cColonne].getTipoColonna());

          // si impostano ulteriori informazioni per la generazione dell'output
          // formattato opportunamente
          switch (elemento.getTipo()) {
          case Campo.TIPO_DATA:
            elemento.setFormattazioneHtml("text-align:center;");
            elemento.setValore(UtilityDate.convertiData(
                (Date) elemento.getValore(), UtilityDate.FORMATO_GG_MM_AAAA));
            // elemento.setFormatoDisplay("{0,date,dd/MM/yyyy}");
            break;
          case Campo.TIPO_TIMESTAMP:
            elemento.setFormattazioneHtml("text-align:center;");
            elemento.setValore(UtilityDate.convertiData(
                (Date) elemento.getValore(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
            // elemento.setFormatoDisplay("{0,date,dd/MM/yyyy}");
            break;
          case Campo.TIPO_DECIMALE:
            elemento.setFormattazioneHtml("text-align:right;");
            // WE315: introdotto controllo per formattazione differenziata per
            // importi (a 2 o 5 decimali) e gli altri numeri (decimali senza
            // separatore di migliaia)
            if ("MONEY5".equals(campi[cColonne].getDominio()))
              elemento.setValore(UtilityNumeri.convertiImporto(
                  (Double) elemento.getValore(), 5));
            else if ("MONEY".equals(campi[cColonne].getDominio()))
              elemento.setValore(UtilityNumeri.convertiImporto(
                  (Double) elemento.getValore(), 2));
            else
              elemento.setValore(UtilityNumeri.convertiDouble(
                  (Double) elemento.getValore(),
                  UtilityNumeri.FORMATO_DOUBLE_CON_VIRGOLA_DECIMALE,
                  campi[cColonne].getDecimali()));
            break;
          case Campo.TIPO_INTERO:
            elemento.setFormattazioneHtml("text-align:right;");
            elemento.setValore(UtilityNumeri.convertiIntero((Long) elemento.getValore()));
            break;

          case Campo.TIPO_STRINGA:
            if (campi[cColonne].getDominio() != null
                && campi[cColonne].getDominio().equals(
                    MetadatiManager.DOMINIO_FLAG)) {
              if (elemento.getValore() == null)
                elemento.setValore("");
              else if (elemento.getValore() != null
                  && Integer.parseInt((String) elemento.getValore()) == 0)
                elemento.setValore("");
              else if (elemento.getValore() != null
                  && Integer.parseInt((String) elemento.getValore()) == 1)
                elemento.setValore("Si");
              else if (elemento.getValore() != null
                  && Integer.parseInt((String) elemento.getValore()) == 2)
                elemento.setValore("No");
            }
            break;
          }
          rigaRisultato.addColonnaRisultato(elemento);
        }
        datiRisultato.addRigaRisultato(rigaRisultato);

        // si popola la chiave della riga del risultato
        for (int cColonne = campi.length - elencoCampiChiave.size(); cColonne < campi.length; cColonne++) {
          rigaRisultato.addColonnaChiave(this.convertiOggettoAStringa(UtilityHashMap.getValueCaseInsensitive(
              (HashMap<?,?>) datiEstratti.get(cRighe), PREFISSO_CAMPI_CHIAVE_SELECT
                  + (cColonne - campi.length + elencoCampiChiave.size() + 1))));
        }
      }
      if (ricerca.getDatiGenerali().getRisPerPag() != null)
        datiRisultato.setNumeroPaginaAttiva(numeroPagina);

      datiRisultato.setNumeroRecordTotali(listaDatiEstratti.getNumeroTotaleRecord());
    }

    if (logger.isDebugEnabled())
      logger.debug("getRisultatiRicerca: fine metodo");

    return datiRisultato;
  }

  /**
   * Estrae il risultato della ricerca a partire dai criteri individuati e dai
   * parametri indicati in input
   *
   * @param contenitorePerModel
   *        ricerca da cui partire per estrarre i dati
   * @param codiceUfficioIntestatarioAttivo
   *        codice dell'ufficio intestatario con cui filtrare i dati
   * @param parametriUtente
   *        parametri valorizzati all'atto dell'esecuzione da parte dell'utente
   * @param profiloUtente
   *        profilo dell'utente
   * @param codApp
   *        codice dell'appicazione in uso
   * @param numeroPagina
   *        numero di pagina dei dati da estrarre
   * @return contenitore con i dati estratti
   * @throws SqlComposerException
   * @throws QueryDaoException
   * @throws DataAccessException
   */
  public DatiRisultato getRisultatiRicercaSql(ContenitoreDatiRicerca ricerca,
      String[] parametriUtente, String codApp, int numeroPagina)
          throws DataAccessException, QueryDaoException {

    if (logger.isDebugEnabled()) {
      logger.debug("getRisultatiRicercaSql: inizio metodo");
    }

    // Stringa usata per determinare l'ordine dei parametri da passare alla preparedStatement
    String querySqlConParametri = new String(ricerca.getDatiGenerali().getDefSql());

    // Stringa utilizzata per l'esecuzione della query e passata alla preparedStatement:
    // tutte le righe di commento vengono rimosse;
    // tutti i parametri vengono rimossi con il punto interrogativo;
    String querySql = new String(ricerca.getDatiGenerali().getDefSql());
    String lineSeparator = System.getProperty("line.separator");

    if (StringUtils.contains(querySql, lineSeparator)) {
      String[] sqlRighe = StringUtils.split(querySql, lineSeparator);

      querySql = "";

      for (int i=0; i < sqlRighe.length; i++) {
        if (!sqlRighe[i].trim().startsWith("--")) {
          if (StringUtils.isEmpty(querySql)) {
            querySql = sqlRighe[i];
          } else {
            querySql = querySql.concat(lineSeparator);
            querySql = querySql.concat(sqlRighe[i]);
          }
        }
      }
    }

    Vector<ParametroRicerca> elencoParametri = ricerca.getElencoParametri();
    HashMap<Integer, ParametroRicerca> hashMapParametri = new HashMap<Integer, ParametroRicerca>();
    Vector<Integer> posizioneDeiParametri = new Vector<Integer>();
    ParametroStmt[] parametri = null;

    if (ricerca.getNumeroParametri() > 0) {
      if (parametriUtente != null && parametriUtente.length == ricerca.getNumeroParametri()) {

        for (int i = 0; i < elencoParametri.size(); i++) {
          ParametroRicerca param = elencoParametri.get(i);

          String parametro = "#".concat(param.getCodice()).concat("#");
          if (StringUtils.countMatches(querySqlConParametri, parametro) == 1) {
            int pos = StringUtils.indexOf(querySqlConParametri, parametro);

            hashMapParametri.put(new Integer(pos), param);
            posizioneDeiParametri.add(new Integer(pos));

            // Sostituzione del codice del parametro con il punto interrogativo
            querySql = StringUtils.replace(querySql, parametro, "?");

          } else if (StringUtils.countMatches(querySqlConParametri, parametro) > 1) {
            int pos = 0;

            pos = StringUtils.indexOf(querySqlConParametri, parametro);
            while (pos >= 0) {
              hashMapParametri.put(new Integer(pos), param);
              posizioneDeiParametri.add(new Integer(pos));
              pos = StringUtils.indexOf(querySqlConParametri, parametro, pos+param.getCodice().length());
            }

            // Sostituzione del codice del parametro con il punto interrogativo
            querySql = StringUtils.replace(querySql, parametro, "?");
          } else {
            // Errore nei parametri: un parametro definito nella ricerca non e'
            // usato nella query
            // TODO da gestire come warning??
          }
        }
      } else {
        if (parametriUtente == null) {
          // Errorraccio!!!
          // All'utente non e' stato richiesto di valorizzare alcun parametro
          // definito nel report sql.
        } else {
          // Errorraccio!!!
          // All'utente e' stato richiesto di valorizzare un numero di parametri
          // diverso dal numero di parametri definiti nel report sql.
          //TODO Come gestirlo??
        }
      }

      // Preparazione dei parametri specificati dall'utente per la preparedStatement.

      // Ordinamento della posizione dei parametri
      Collections.sort(posizioneDeiParametri);

      // Array dei parametri per la preparedStatement
      parametri = new ParametroStmt[posizioneDeiParametri.size()];

      for (int i = 0; i < posizioneDeiParametri.size(); i++) {
        ParametroRicerca param = hashMapParametri.get(posizioneDeiParametri.get(i));

        boolean trovato = false;
        int j = 0;
        ParametroRicerca paramRic = null;

        for (; j < elencoParametri.size() && !trovato; j++) {
          if (elencoParametri.get(j).getCodice().equals(param.getCodice())) {
            paramRic = elencoParametri.get(j);
            trovato = true;

            ParametroStmt parStmt = null;
            if ("D".equals(paramRic.getTipo())) {
              parStmt = new ParametroStmt("", parametriUtente[j], Campo.TIPO_DATA);
            } else if ("I".equals(paramRic.getTipo()) || "UC".equals(paramRic.getTipo())) {
              parStmt = new ParametroStmt("", parametriUtente[j], Campo.TIPO_INTERO);
            } else if ("F".equals(paramRic.getTipo())) {
              parStmt = new ParametroStmt("", parametriUtente[j], Campo.TIPO_DECIMALE);
            } else if ("S".equals(paramRic.getTipo())) {
              parStmt = new ParametroStmt("", parametriUtente[j], Campo.TIPO_STRINGA);
            } else if ("T".equals(paramRic.getTipo()) || "UI".equals(paramRic.getTipo())) {
              parStmt = new ParametroStmt("", parametriUtente[j], Campo.TIPO_STRINGA);
            }
            parametri[i] = parStmt;
          }
        }
      }
    } else {
      parametri = new ParametroStmt[0];
    }

    String querySqlDefinitiva = null;
    String[] arrayFormatoCampi = null;
    if (StringUtils.contains(querySql, "@@")) {
      // Sostituzione di tutti i caratteri 'a capo' con un numero di spazi pari a lineSeparator.length
      String querySqlNoLineSeparator = StringUtils.replace(querySql, lineSeparator,
          StringUtils.rightPad("", lineSeparator.length()));

      // Suddivisione della query in due parti:
      // prima parte: dall'inizio (SELECT escluso) a FROM (escluso);
      // seconda parte: da FROM (escluso) fine alla fine;

      // Visto che la parola chiave FROM puo' essere presente in sotto select usate sia per
      // l'estrazione di valori, sia per creare condizioni di where, e' necessario individuare
      // univocamente la posizione corretta del FROM principale della query. Per fare questo:
      // - se il numero di SELECT == 1 e numero di FROM == 1, allora e' l'unico presente nella query
      // - altrimenti se il numero di SELECT == numero di FROM e sono maggiori entrambi maggiori di
      //    uno, allora,a partire dal settimo carattere (trascurando quindi il primo select) si cerca
      //    il primo from che sia in una posizione precedente al successivo select oppure il primo from
      //    che non ha un select a seguire.
      int numeroRipetizioniFROM = StringUtils.countMatches(querySqlNoLineSeparator.toUpperCase(), " FROM ");
      int numeroRipetizioniSELECT = StringUtils.countMatches(querySqlNoLineSeparator.toUpperCase(), "SELECT ");

      int posizioneFROM = 0;
      if (numeroRipetizioniFROM == 1 && numeroRipetizioniSELECT == 1) {
        posizioneFROM = StringUtils.indexOf(querySqlNoLineSeparator.toUpperCase(), " FROM ");
      } else {
        if (numeroRipetizioniSELECT > 0 && numeroRipetizioniFROM > 0 && numeroRipetizioniSELECT == numeroRipetizioniFROM) {
          // Posizione nella stringa querySqlUpperCase
          int indiceQuerySql = 7;  // il valore e' pari a: "SELECT ".lenght

          int indiceStrSelect = 0;
          int indiceStrFrom = 0;

          do {
            indiceStrFrom = StringUtils.indexOf(querySqlNoLineSeparator.toUpperCase(), " FROM ", indiceQuerySql);
            indiceStrSelect = StringUtils.indexOf(querySqlNoLineSeparator.toUpperCase(), "SELECT ", indiceQuerySql);

            if (indiceStrSelect >= 0) {
              if (indiceStrSelect < indiceStrFrom) {
                indiceQuerySql = indiceStrFrom+6;  // si
              } else {
                posizioneFROM = indiceStrFrom;  // trovato FROM pricipale della query
              }
            } else {
              posizioneFROM = indiceStrFrom;  // trovato FROM pricipale della query
            }
          } while (posizioneFROM < 1 && indiceQuerySql < querySql.length());

        } else {
          // query sintatticamente non corretta
          String strTemp = "Query sintatticamente non corretta: la parola 'SELECT' e' ripetuta "
            + numeroRipetizioniSELECT + " volte, mentre la parola 'FROM' e' ripetuta "
            + numeroRipetizioniFROM + " volte." ;
          logger.error(strTemp);

          throw new QueryDaoException(QueryDaoException.CODICE_ERRORE_REPORT_SQL_QUERY_NON_VALIDA, strTemp);
        }
      }

      String querySqlPrimaParte   = null;
      String querySqlSecondaParte = null;
      if (posizioneFROM > 0) {
        querySqlPrimaParte   = StringUtils.substring(querySqlNoLineSeparator, 6, posizioneFROM).trim();
        querySqlSecondaParte = StringUtils.substring(querySqlNoLineSeparator, posizioneFROM + 6).trim();
      } else {
        querySqlPrimaParte   = StringUtils.substring(querySqlNoLineSeparator, 6).trim();
        querySqlSecondaParte = StringUtils.substring(querySqlNoLineSeparator, 6).trim();
      }

      // Ricerca del formato/dominio dei dati estratti nella query. Indicati con @<dominio/formato>@.
      // Esempi:
      // - @MONEY@   -->  campo importo, visualizzato con due cifre decimali
      // - @MONEY5@  -->  campo importo, visualizzato con cinque cifre decimali
      // - @DATA_ELDA@ -->  campo date o timestamp, visualizzato in formato GG/MM/AAAA
      // - @TIMESTAMP@ -->  campo timestamp, visualizzato in formato GG/MM/AAAA HH:MM:SS
      // - @SN@      -->  campo Si/No

      StringBuffer bufferQuerySql = null;
      String[] arrayCampiConFormatoDati = null;

      // Sono supportati due modi di elencare i campi da estrarre:
      // 1) elenco dei campi separati da virgola (per rispettare la sintassi SQL) con l'eventuale formato/dominio,
      //    ma senza nessun carattere per andare a capo. Ai campi si possono applicare solo funzioni SQL con un
      //    unico argomento (esempio: select campo1, campo2, UPPER(campo3), campo4, campo5.... )
      // 2) elenco dei campi separati da virgola (per rispettare la sintassi SQL) con l'eventuale formato/dominio,
      //    ma ogni campo e' indicato in una riga. Ai campi si possono applicare tutte le funzioni SQL
      //    (esempio: CAST(campo1 as NUMBER(24,5)) @@MONEY@@,....  )

      int modalitaSqlQuery = 0;
      if (querySqlPrimaParte.contains(lineSeparator)) {
        // modalita' 2
        arrayCampiConFormatoDati = StringUtils.split(querySqlPrimaParte.trim(), lineSeparator);
        modalitaSqlQuery = 2;
      } else {
        // modalita' 1
        arrayCampiConFormatoDati = StringUtils.split(querySqlPrimaParte.trim(), ",");
        modalitaSqlQuery = 1;
      }

      bufferQuerySql = new StringBuffer("SELECT ");
      arrayFormatoCampi = new String[arrayCampiConFormatoDati.length];

      for (int e=0; e < arrayCampiConFormatoDati.length; e++) {
        if (StringUtils.countMatches(arrayCampiConFormatoDati[e], "@@") == 2) {

          arrayFormatoCampi[e] = StringUtils.substringBetween(arrayCampiConFormatoDati[e], "@@");
          bufferQuerySql.append(StringUtils.substringBefore(arrayCampiConFormatoDati[e], "@@"));
          bufferQuerySql.append(StringUtils.substringAfterLast(arrayCampiConFormatoDati[e], "@@"));
        } else {
          bufferQuerySql.append(arrayCampiConFormatoDati[e]);
        }

        if (e < (arrayCampiConFormatoDati.length - 1)) {
          if (modalitaSqlQuery == 1) {
            bufferQuerySql.append(", ");
          } else {
            bufferQuerySql.append(" ");
          }
        } else {
          bufferQuerySql.append(" ");
        }
      }

      if (bufferQuerySql != null) {
        bufferQuerySql.append(" FROM ");
        bufferQuerySql.append(querySqlSecondaParte);
        querySqlDefinitiva = bufferQuerySql.toString();
      }
    } else {
      querySqlDefinitiva = new String (querySql);
    }

    DatiRisultato datiRisultato = new DatiRisultato();

    // La variabile querySql contiene la query da eseguire (con i punti interrogativi
    // al posto del nome dei parametri)
    datiRisultato.setQuerySql(querySqlDefinitiva);
    datiRisultato.setParametriSql(parametri);

    if (logger.isDebugEnabled()) {
      // Log della query e dei parametri della ricerca
      logger.debug("Esecuzione query di ricerca = " + datiRisultato.getQuerySql());
      logger.debug("Parametri della ricerca = " + datiRisultato.toStringParametriSql());
    }

    // FASE 5: si esegue la query
    int numeroMaxRecord = 10000;
    if (UtilityNumeri.convertiIntero(ConfigManager.getValore(
        RicercheManager.PROP_NUMERO_MASSIMO_RECORD_ESTRAIBILI)) != null)
      numeroMaxRecord = UtilityNumeri.convertiIntero(
          ConfigManager.getValore(RicercheManager.PROP_NUMERO_MASSIMO_RECORD_ESTRAIBILI)).intValue();

    ListaDati listaDatiEstratti = null;

    if (ricerca.getDatiGenerali().getRisPerPag() != null) {
      listaDatiEstratti = this.queryDao.getDatiSelect(querySqlDefinitiva, parametri, null, numeroPagina,
          ricerca.getDatiGenerali().getRisPerPag().intValue(), numeroMaxRecord);
    } else {
      listaDatiEstratti = this.queryDao.getDatiSelect(querySqlDefinitiva, parametri, null, numeroMaxRecord, true);
    }

    List<?> datiEstratti = listaDatiEstratti.getListaDati();
    Campo[] campi = listaDatiEstratti.getArrayCampi();

    if (datiEstratti != null && datiEstratti.size() > 0) {
      if (campi != null && campi.length > 0) {
        if (arrayFormatoCampi != null && arrayFormatoCampi.length == campi.length) {
          for (int e=0; e < campi.length; e++) {
            if (campi[e] != null) {
              String formatoTmp = arrayFormatoCampi[e];
              if (StringUtils.isNotEmpty(formatoTmp)) {
                if (StringUtils.equals("MONEY", formatoTmp)) {
                  campi[e] =  new Campo(campi[e].getCodiceMnemonico(), campi[e].getNomeFisico(), campi[e].getDescrizione(), campi[e].getDescrizioneBreve(), campi[e].getDescrizioneWEB(),
                      campi[e].isVisibileRicerche(), Campo.TIPO_DECIMALE, campi[e].isCampoChiave(), campi[e].getCodiceTabellato());
                  campi[e].setDominio("MONEY");
                  campi[e].setDecimali(2);
                } else if (StringUtils.equals("MONEY5", formatoTmp)) {
                  campi[e] =  new Campo(campi[e].getCodiceMnemonico(), campi[e].getNomeFisico(), campi[e].getDescrizione(), campi[e].getDescrizioneBreve(), campi[e].getDescrizioneWEB(),
                      campi[e].isVisibileRicerche(), Campo.TIPO_DECIMALE, campi[e].isCampoChiave(), campi[e].getCodiceTabellato());
                  campi[e].setDominio("MONEY5");
                  campi[e].setDecimali(5);
                } else if (StringUtils.equals("SN", formatoTmp)) {
                  campi[e] =  new Campo(campi[e].getCodiceMnemonico(), campi[e].getNomeFisico(), campi[e].getDescrizione(), campi[e].getDescrizioneBreve(), campi[e].getDescrizioneWEB(),
                      campi[e].isVisibileRicerche(), Campo.TIPO_STRINGA, campi[e].isCampoChiave(), campi[e].getCodiceTabellato());
                  campi[e].setDominio(MetadatiManager.DOMINIO_FLAG);
                } else if (StringUtils.equals("DATA_ELDA", formatoTmp)) {
                  campi[e] =  new Campo(campi[e].getCodiceMnemonico(), campi[e].getNomeFisico(), campi[e].getDescrizione(), campi[e].getDescrizioneBreve(), campi[e].getDescrizioneWEB(),
                      campi[e].isVisibileRicerche(), Campo.TIPO_DATA, campi[e].isCampoChiave(), campi[e].getCodiceTabellato());
                  campi[e].setDominio("DATA_ELDA");
                } else if (StringUtils.equals("TIMESTAMP", formatoTmp)) {
                  campi[e] =  new Campo(campi[e].getCodiceMnemonico(), campi[e].getNomeFisico(), campi[e].getDescrizione(), campi[e].getDescrizioneBreve(), campi[e].getDescrizioneWEB(),
                      campi[e].isVisibileRicerche(), Campo.TIPO_TIMESTAMP, campi[e].isCampoChiave(), campi[e].getCodiceTabellato());
                  campi[e].setDominio("TIMESTAMP");
                }
              }
            }
          }
        }

        List<Integer> listaCampiVuoti = new ArrayList<Integer>();
        for (int za = 0; za < campi.length; za++) {
          if (campi[za] == null) {
            listaCampiVuoti.add(new Integer(za+1));
          }
        }
        if (!listaCampiVuoti.isEmpty()) {
          // la mappatura dei campi non e' riuscita per nessun campo
          StringBuffer strBuf = new StringBuffer("");
          if (listaCampiVuoti.size() == 1) {
            strBuf.append(" la colonna ");
            strBuf.append("" + listaCampiVuoti.get(0));
          } else {
            for (int za = 0; za < listaCampiVuoti.size(); za++) {
              if (za > 0) {
                strBuf.append(", " + listaCampiVuoti.get(za));
              } else {
                strBuf.append("le colonne " + listaCampiVuoti.get(za));
              }
            }
          }
          logger.error("Per alcuni campi del report non e' stato possibile determinare il tipo di dato. "
              + "Si consiglia di forzare il tipo di dato estratto direttamente nella query per "
              + strBuf.toString());

          throw new QueryDaoException(QueryDaoException.CODICE_ERRORE_TIPO_DATO_NON_RICONOSCIUTO, strBuf.toString());
        }

      } else {
        // la mappatura dei campi non e' riuscita per nessun campo
        logger.error("Per tutti i campi del report non e' stato possibile determinare il tipo di dato. " +
                "Si consiglia di forzare il tipo di dato estratto direttamente nella query.");

        throw new QueryDaoException(QueryDaoException.CODICE_ERRORE_TIPO_DATO_NON_RICONOSCIUTO, "tutte le colonne");
      }

      datiRisultato.setArrayCampi(campi);

    //if (datiEstratti != null && datiEstratti.size() > 0) {
      // FASE 6: popolamento risultato
      RigaRisultato rigaRisultato = null;
      ElementoRisultato elemento = null;

      if (logger.isInfoEnabled()) {
        logger.info("Sono state estratte " + datiEstratti.size() + " righe");
      }

      for (int cRighe = 0; cRighe < datiEstratti.size(); cRighe++) {
        rigaRisultato = new RigaRisultato();
        // si popola la riga del risultato
         for (int cColonne = 0; cColonne < campi.length; cColonne++) {
          elemento = new ElementoRisultato();

          elemento.setValore(UtilityHashMap.getValueCaseInsensitive(
              (HashMap< ?, ? >) datiEstratti.get(cRighe), campi[cColonne].getDescrizione()));
          elemento.setTipo(campi[cColonne].getTipoColonna());

          // si impostano ulteriori informazioni per la generazione dell'output
          // formattato opportunamente
          switch (elemento.getTipo()) {
          case Campo.TIPO_DATA:
            elemento.setFormattazioneHtml("text-align:center;");
            elemento.setValore(UtilityDate.convertiData(
                (Date) elemento.getValore(), UtilityDate.FORMATO_GG_MM_AAAA));
            // elemento.setFormatoDisplay("{0,date,dd/MM/yyyy}");
            break;
          case Campo.TIPO_TIMESTAMP:
            elemento.setFormattazioneHtml("text-align:center;");
            if ("DATA_ELDA".equals(campi[cColonne].getDominio())) {
              elemento.setValore(UtilityDate.convertiData(
                  (Date) elemento.getValore(), UtilityDate.FORMATO_GG_MM_AAAA));
            } else {
              elemento.setValore(UtilityDate.convertiData(
                  (Date) elemento.getValore(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
            }
            break;
          case Campo.TIPO_DECIMALE:
            elemento.setFormattazioneHtml("text-align:right;");
            // WE315: introdotto controllo per formattazione differenziata per
            // importi (a 2 o 5 decimali) e gli altri numeri (decimali senza
            // separatore di migliaia)
            Double valoreDouble = null;
            if (elemento.getValore() instanceof Double) {
              valoreDouble = (Double) elemento.getValore();
            } else if (elemento.getValore() instanceof Integer) {
              valoreDouble = new Double(((Integer) elemento.getValore()).doubleValue());
            }
            if ("MONEY5".equals(campi[cColonne].getDominio())) {
              elemento.setValore(UtilityNumeri.convertiImporto(valoreDouble, 5));
            } else if ("MONEY".equals(campi[cColonne].getDominio())) {
              elemento.setValore(UtilityNumeri.convertiImporto(valoreDouble, 2));
            } else {
              elemento.setValore(UtilityNumeri.convertiDouble(valoreDouble,
                  UtilityNumeri.FORMATO_DOUBLE_CON_VIRGOLA_DECIMALE,
                  campi[cColonne].getDecimali()));
            }
            break;
          case Campo.TIPO_INTERO:
            elemento.setFormattazioneHtml("text-align:right;");
            elemento.setValore(UtilityNumeri.convertiIntero((Integer) elemento.getValore()));
            break;

          case Campo.TIPO_STRINGA:
            if (campi[cColonne].getDominio() != null
                && campi[cColonne].getDominio().equals(
                    MetadatiManager.DOMINIO_FLAG)) {
              if (elemento.getValore() == null) {
                elemento.setValore("");
              } else if (elemento.getValore() != null
                  && Integer.parseInt((String) elemento.getValore()) == 0) {
                elemento.setValore("");
              } else if (elemento.getValore() != null
                  && Integer.parseInt((String) elemento.getValore()) == 1) {
                elemento.setValore("Si");
              } else if (elemento.getValore() != null
                  && Integer.parseInt((String) elemento.getValore()) == 2) {
                elemento.setValore("No");
              }
            }
            break;
          }
          rigaRisultato.addColonnaRisultato(elemento);
        }
        datiRisultato.addRigaRisultato(rigaRisultato);
      }

      if (ricerca.getDatiGenerali().getRisPerPag() != null) {
        datiRisultato.setNumeroPaginaAttiva(numeroPagina);
      }

      datiRisultato.setNumeroRecordTotali(listaDatiEstratti.getNumeroTotaleRecord());
    }

    if (logger.isDebugEnabled())
      logger.debug("getRisultatiRicercaSql: fine metodo");

    return datiRisultato;
  }

  /**
   * Estrae l'SQL di estrazione della ricerca e ridefinisce i parametri
   * eventualmente aggiungendo quelli previsti per i filtri sul livello utente o
   * le integrazioni.
   *
   * @param ricerca
   *        ricerca da cui partire per estrarre i dati
   * @param parametriUtente
   *        parametri valorizzati all'atto dell'esecuzione da parte dell'utente
   * @param profiloUtente
   *        profilo dell'utente
   * @param codiceUfficioIntestatarioAttivo
   *        codice dell'ufficio intestatario attivo per filtrare i dati
   * @return contenitore con la query sql da eseguire mediante prepared
   *         statement e gli eventuali parametri
   * @throws SqlComposerException
   */
  public InputStmt getSqlRicerca(ContenitoreDatiRicerca ricerca,
      String[] parametriUtente, ProfiloUtente profiloUtente,
      String codiceUfficioIntestatarioAttivo) throws SqlComposerException {

    if (logger.isDebugEnabled())
      logger.debug("getSqlRicerca: inizio metodo");

    DatiRisultato datiRisultato = new DatiRisultato();
    this.buildSqlEParametri(ricerca, parametriUtente, profiloUtente,
        codiceUfficioIntestatarioAttivo, datiRisultato);

    if (logger.isDebugEnabled())
      logger.debug("getSqlRicerca: fine metodo");

    return datiRisultato;
  }

  /**
   * Costruisce la query SQL da eseguire per l'estrazione della ricerca e
   * l'elenco dei parametri aggiornato con eventuali filtri sugli utenti,
   * filtri per ufficio intestatario e filtri aggiuntivi dovuti alle
   * integrazioni, nonch&agrave; popola alcune informazioni presenti nel
   * contenitore del risultato.
   *
   * @param ricerca
   *        ricerca da cui partire per estrarre i dati
   * @param parametriUtente
   *        parametri valorizzati all'atto dell'esecuzione da parte dell'utente
   * @param profiloUtente
   *        profilo dell'utente
   * @param codiceUfficioIntestatarioAttivo
   *        codice dell'ufficio intestatario con cui filtrare i dati
   * @param datiRisultato
   *        contenitore del risultato della ricerca
   * @return elenco di stringhe contenente gli eventuali campi chiave
   *         dell'argomento principale della ricerca, per l'eventuale
   *         collegamento di una scheda o lancio di un modello predisposto
   * @throws SqlComposerException
   */
  private Vector<Campo> buildSqlEParametri(ContenitoreDatiRicerca ricerca, String[] parametriUtente,
      ProfiloUtente profiloUtente, String codiceUfficioIntestatarioAttivo,
      DatiRisultato datiRisultato) throws SqlComposerException {

    Vector<FiltroRicerca> elencoParametriFiltri = new Vector<FiltroRicerca>();
    Vector<String> elencoParametriFiltroLivelloUtente = new Vector<String>();
    Vector<String> elencoParametriFiltroUfficioIntestatario = new Vector<String>();
    Vector<ParametroStmt> elencoParametriIntegrazioni = new Vector<ParametroStmt>();

    Vector<Campo> elencoCampiChiave = this.setEntitaEChiave(ricerca, datiRisultato);
    String query = this.buildSqlRicerca(ricerca, profiloUtente, codiceUfficioIntestatarioAttivo,
        parametriUtente, elencoParametriFiltri, elencoParametriFiltroLivelloUtente,
        elencoParametriFiltroUfficioIntestatario, elencoParametriIntegrazioni,
        elencoCampiChiave);
    datiRisultato.setQuerySql(query);

    ParametroStmt[] parametri = this.buildParametriSqlRicerca(ricerca, profiloUtente,
        codiceUfficioIntestatarioAttivo, parametriUtente,
        elencoParametriFiltri, elencoParametriFiltroLivelloUtente,
        elencoParametriFiltroUfficioIntestatario, elencoParametriIntegrazioni);
    datiRisultato.setParametriSql(parametri);
    return elencoCampiChiave;
  }

  /**
   * Analizza la ricerca, e nel caso di ricerca con abilitazione dei modelli
   * predisposti o collegamento alla scheda di dettaglio, vanno reperiti
   * l'argomento principale della ricerca ed i suoi campi chiave.
   *
   * @param ricerca
   *        ricerca da cui partire per estrarre i dati
   * @param datiRisultato
   *        contenitore del risultato della ricerca
   * @return elenco di stringhe contenente gli eventuali campi chiave
   *         dell'argomento principale della ricerca, per l'eventuale
   *         collegamento di una scheda o lancio di un modello predisposto
   */
  private Vector<Campo> setEntitaEChiave(ContenitoreDatiRicerca ricerca,
      DatiRisultato datiRisultato) {
    Vector<Campo> elencoCampiChiave = new Vector<Campo>();

    String entPrinc = ricerca.getDatiGenerali().getEntPrinc();
    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    // FASE 0: popolamento delle informazioni associate all'entità principale se
    // è possibile gestire l'eventuale successiva composizione di modelli sulle
    // righe del risultato oppure la visualizzazione della scheda
    if (ricerca.getDatiGenerali().getVisModelli() == 1
        || ricerca.getDatiGenerali().getLinkScheda() == 1) {
      if (CostantiGenRicerche.REPORT_AVANZATO == ricerca.getDatiGenerali().getFamiglia().intValue()) {
        // Gestione dell'entita' principale e dei campi chiavi per ricerche
        // avanzate
        for (int i = 0; i < ricerca.getNumeroTabelle(); i++) {
          if (ricerca.estraiArgomento(i).getAliasTabella().equals(entPrinc)) {
            TabellaRicerca argomentoRicerca = ricerca.estraiArgomento(i);
            Tabella argomento = dizTabelle.get(argomentoRicerca.getMneTabella());
            String[] campiChiave = new String[argomento.getMnemoniciCampiChiave().size()];
            for (int j = 0; j < argomento.getMnemoniciCampiChiave().size(); j++) {
              campiChiave[j] = dizCampi.get(
                  argomento.getMnemoniciCampiChiave().get(j)).getNomeCampo();
              elencoCampiChiave.add(dizCampi.get(argomento.getMnemoniciCampiChiave().get(j)));
            }
            datiRisultato.setEntPrinc(argomento.getNomeTabella());
            datiRisultato.setCampiChiave(campiChiave);
          }
        }
      } else {
        // Gestione dell'entita' principale e dei campi chiavi per ricerche base:
        // il compositore modelli non puo' accedere alle tabelle delle viste
        // (perche' hanno il campo C0C_TIP settato a 'C'), quindi bisogna
        // passargli la tabella e i campi associati alla vista stessa

        // Determino l'id del campo chiave della tabella C0ENTIT
        String idC0Entit = entPrinc
            + CostantiGenerali.SEPARATORE_PROPERTIES
            + ConfigManager.getValore(CostantiGenerali.PROP_SCHEMA_VISTE_REPORT_BASE);
        String concatenazioneCampiChiave = this.metadatiDao.getC0eKeyById(idC0Entit);

        if (concatenazioneCampiChiave != null) {
          LegameTabelle legameTabelle = new LegameTabelle();
          legameTabelle.setElencoCampiFisiciOrigine(concatenazioneCampiChiave);

          Tabella argomento = dizTabelle.getDaNomeTabella(legameTabelle.getTabellaOrigine());
          String[] campiChiave = legameTabelle.getElencoCampiTabellaOrigine();

          for (int j = 0; j < argomento.getMnemoniciCampiChiave().size(); j++) {
            campiChiave[j] = dizCampi.get(
                argomento.getMnemoniciCampiChiave().get(j)).getNomeCampo();
            elencoCampiChiave.add(dizCampi.get(argomento.getMnemoniciCampiChiave().get(j)));
          }
          datiRisultato.setEntPrinc(argomento.getNomeTabella());
          datiRisultato.setCampiChiave(campiChiave);
        }

      }
    }

    return elencoCampiChiave;
  }

  /**
   * Definisce l'sql da eseguire in base alla ricerca, ad eventuali filtri sul
   * livello utente da applicare ed eventuali integrazioni.
   *
   * @param ricerca
   *        ricerca da cui partire per estrarre i dati
   * @param profiloUtente
   *        profilo dell'utente
   * @param codiceUfficioIntestatarioAttivo
   *        codice dell'ufficio intestatario con cui filtrare i dati
   * @param parametriUtente
   *        parametri valorizzati all'atto dell'esecuzione da parte dell'utente
   * @param elencoParametriFiltri
   *        elenco da popolare con i parametri legati ai filtri applicati
   * @param elencoParametriFiltroLivelloUtente
   *        elenco da popolare con i parametri legati al livello utente
   * @param elencoParametriIntegrazioni
   *        elenco da popolare con i parametri legati ad integrazioni
   * @param elencoCampiChiave
   * @return stringa sql da eseguire mediante prepared statement
   *
   * @throws SqlComposerException
   */
  private String buildSqlRicerca(ContenitoreDatiRicerca ricerca, ProfiloUtente profiloUtente,
      String codiceUfficioIntestatarioAttivo, String[] parametriUtente,
      Vector<FiltroRicerca> elencoParametriFiltri, Vector<String> elencoParametriFiltroLivelloUtente,
      Vector<String> elencoParametriFiltroUfficioIntestatario,
      Vector<ParametroStmt> elencoParametriIntegrazioni, Vector<Campo> elencoCampiChiave)
          throws SqlComposerException {

    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));

    // FASE 1: costruire i parametri da passare al composer SQL
    Vector<SqlElementoSelect> vettoreCampiSelect = new Vector<SqlElementoSelect>();
    Vector<SqlCampo> vettoreCampiGroupBy = new Vector<SqlCampo>();
    this.estraiCampiSelectEGroupBy(ricerca, elencoCampiChiave,
        vettoreCampiSelect, vettoreCampiGroupBy, dizCampi, dizTabelle);
    SqlElementoSelect[] elencoCampi = new SqlElementoSelect[vettoreCampiSelect.size()];
    vettoreCampiSelect.copyInto(elencoCampi);
    SqlCampo[] elencoCampiGroupBy = new SqlCampo[vettoreCampiGroupBy.size()];
    if (elencoCampiGroupBy.length > 0)
      vettoreCampiGroupBy.copyInto(elencoCampiGroupBy);

    SqlTabella[] elencoTabelle = this.estraiTabelle(ricerca, dizCampi,
        dizTabelle);
    SqlJoin[] elencoLegami = this.estraiLegamiTabelle(ricerca, dizCampi,
        dizTabelle);
    SqlElementoCondizione[] elencoCondizioni = this.estraiCondizioni(ricerca,
        dizCampi, dizTabelle, elencoParametriFiltri, composer, profiloUtente,
        codiceUfficioIntestatarioAttivo);

    // L.G. 06/03/2007: se e' attivo il filtro per Id Utente, allora aggiungo le
    // condizioni di filtro per Id Utente in funzione degli argomenti
    // selezionati per la ricerca
    if (ricerca.getDatiGenerali().getFiltroUtente() == 1) {
      SqlElementoCondizione[] elencoTMP = this.estraiCondizioniFiltroUtente(
          ricerca.getElencoArgomenti(), dizTabelle, elencoCondizioni,
          profiloUtente.getFiltroLivelloUtente(),
          elencoParametriFiltroLivelloUtente);
      if (elencoTMP != null && elencoTMP.length > 0) {
        // Se esistono delle condizioni di filtro e bisogna filtrare per livello
        // utente, allora si separano le condizioni di filtro dalle condizioni di
        // filtro per livello utente, racchiudendo le prime tra  parentesi, cioè:
        // where (( condizioni di filtro ) and (condizioni di filtro per livello
        // utente))
        if (elencoCondizioni != null && elencoCondizioni.length > 0) {
          SqlElementoCondizione[] elencoTMP1 = new SqlElementoCondizione[elencoCondizioni.length + 2];
          // Prima condizione = Parentesi aperta
          elencoTMP1[0] = new SqlElementoCondizione(
              SqlElementoCondizione.OPERATORE_PARENTESI_APERTA);
          for (int i = 0; i < elencoCondizioni.length; i++)
            elencoTMP1[i + 1] = elencoCondizioni[i];
          // Ultima condizione = Parentesi chiusa
          elencoTMP1[elencoTMP1.length - 1] = new SqlElementoCondizione(
              SqlElementoCondizione.OPERATORE_PARENTESI_CHIUSA);
          elencoCondizioni = new SqlElementoCondizione[elencoTMP1.length
              + elencoTMP.length];
          for (int i = 0; i < elencoTMP1.length; i++)
            elencoCondizioni[i] = elencoTMP1[i];
          for (int i = 0; i < elencoTMP.length; i++)
            elencoCondizioni[elencoTMP1.length + i] = elencoTMP[i];
        } else {
          elencoCondizioni = elencoTMP;
        }
      }
    }

    // L.G. 24/02/2015: se codiceUfficioIntestatarioAttivo e' valorizzto e se il filtro per
    // ufficio intestatario e' attivo, allora aggiungo le condizioni di filtro per ufficio
    // intestatario in funzione degli argomenti selezionati per la ricerca.
    if (StringUtils.isNotEmpty(codiceUfficioIntestatarioAttivo) &&
        ricerca.getDatiGenerali().getFiltroUfficioIntestatario() == 1) {

      SqlElementoCondizione[] elencoTMP = this.estraiCondizioniFiltroUfficioIntestatario(
          ricerca.getElencoArgomenti(), dizTabelle, elencoCondizioni, profiloUtente,
          elencoParametriFiltroUfficioIntestatario, codiceUfficioIntestatarioAttivo);
      if (elencoTMP != null && elencoTMP.length > 0) {
        // Se esistono delle condizioni di filtro e bisogna filtrare per livello
        // utente, allora si separano le condizioni di filtro dalle condizioni
        // di filtro per livello utente, racchiudendo le prime tra parentesi,
        // cioe':
        // where (( condizioni di filtro ) and (condizioni di filtro per livello
        // utente) and (condizioni di filtro per ufficio intestatario))
        if (elencoCondizioni != null && elencoCondizioni.length > 0) {
          SqlElementoCondizione[] elencoTMP1 = new SqlElementoCondizione[elencoCondizioni.length + 2];
          // Prima condizione = Parentesi aperta
          elencoTMP1[0] = new SqlElementoCondizione(
              SqlElementoCondizione.OPERATORE_PARENTESI_APERTA);
          for (int i = 0; i < elencoCondizioni.length; i++)
            elencoTMP1[i + 1] = elencoCondizioni[i];
          // Ultima condizione = Parentesi chiusa
          elencoTMP1[elencoTMP1.length - 1] = new SqlElementoCondizione(
              SqlElementoCondizione.OPERATORE_PARENTESI_CHIUSA);
          elencoCondizioni = new SqlElementoCondizione[elencoTMP1.length
              + elencoTMP.length];
          for (int i = 0; i < elencoTMP1.length; i++)
            elencoCondizioni[i] = elencoTMP1[i];
          for (int i = 0; i < elencoTMP.length; i++)
            elencoCondizioni[elencoTMP1.length + i] = elencoTMP[i];
        } else {
          elencoCondizioni = elencoTMP;
        }
      }
    }

    // SS20090318: integrazione parametri di KRONOS
    elencoCondizioni = this.checkCondizioniIntegrazioneKronos(ricerca,
        parametriUtente, profiloUtente, elencoCondizioni,
        elencoParametriIntegrazioni);

    SqlElementoOrderBy[] elencoCampiOrderBy = this.estraiCampiOrderBy(ricerca,
        dizCampi, dizTabelle);

    // if (elencoCampiGroupBy.length == elencoCampi.length)
    // // se si inseriscono nei campi della group by tutti i campi della select,
    // // allora non sono presenti funzioni statistiche, di conseguenza si
    // // svuota l'elenco perchè inutile
    // elencoCampiGroupBy = new SqlCampo[0];
    // else {
    // // altrimenti, se utilizzo funzioni statistiche, devo eliminare le
    // colonne
    // // dei campi chiave dall'estrazione dei dati in quanto ottengo un
    // // partizionamento dei record e non i record stessi
    // }

    // FASE 2: invio parametri al composer
    composer.setDistinct(ricerca.getDatiGenerali().getValDistinti() == 1);
    composer.setCampiSelect(elencoCampi);
    composer.setTabelle(elencoTabelle);
    composer.setLegamiJoin(elencoLegami);
    composer.setCondizioni(elencoCondizioni);
    composer.setCampiGroupBy(elencoCampiGroupBy);
    composer.setCampiOrderBy(elencoCampiOrderBy);

    // si costruisce la stringa da passare alla prepared statement
    String query = composer.getSelect();
    return query;
  }

  /**
   * Compone l'elenco dei parametri da inviare per l'estrazione della ricerca.
   *
   * @param ricerca
   *        ricerca da cui partire per estrarre i dati
   * @param parametriUtente
   *        parametri valorizzati all'atto dell'esecuzione da parte dell'utente
   * @param elencoFiltri
   *        elenco dei filtri della ricerca
   * @param elencoParametriFiltroLivelloUtente
   *        elenco dei parametri legati ai filtri sul livello utente
   * @param parametriAggiuntivi
   *        elenco dei parametri aggiuntivi legati ad integrazioni eventuali
   * @return array di parametri pronti per l'esecuzione della query e le
   *         sostituzioni nella prepared statement
   */
  private ParametroStmt[] buildParametriSqlRicerca(ContenitoreDatiRicerca ricerca,
      ProfiloUtente profiloUtente, String codiceUffIntAttivo, String[] parametriUtente,
      Vector<?> elencoFiltri, Vector<?> elencoParametriFiltroLivelloUtente,
      Vector<String> elencoParametriFiltroUfficioIntestatario, Vector<ParametroStmt> parametriAggiuntivi) {

    // FASE 3: creazione prepared statement per l'esecuzione della query,
    // inserendo le varie costanti opportunamente tipizzate

    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    // valorizzare i parametri della prepared statement in modo opportuno (con
    // il giusto ordine, e tipizzati opportunamente)
    ParametroStmt[] parametri = new ParametroStmt[elencoFiltri.size()];

    // si valorizza la prepared statement con i parametri
    FiltroRicerca filtroRicerca = null;
    Campo campo = null;
    String valore = null;

    int indice = 0;
    for (int i = 0; i < elencoFiltri.size(); i++) {
      filtroRicerca = (FiltroRicerca) elencoFiltri.elementAt(i);

      // switch (Short.parseShort(filtroRicerca.getTipoConfronto())) {
      switch (filtroRicerca.getTipoConfronto().shortValue()) {
      case FiltroRicerca.TIPO_CONFRONTO_VALORE:
        // si inserisce il valore fisso cablato nel filtro ed associato al campo
        campo = dizCampi.get(filtroRicerca.getMnemonicoCampo());
        if (SqlElementoCondizione.OPERATORE_CONFRONTO_IN == SqlElementoCondizione.getTipoOperatore(filtroRicerca.getOperatore())
            || SqlElementoCondizione.OPERATORE_CONFRONTO_NOT_IN == SqlElementoCondizione.getTipoOperatore(filtroRicerca.getOperatore())) {
          String[] arrayValori = null;
          if (Campo.TIPO_STRINGA == campo.getTipoColonna()
              || Campo.TIPO_NOTA == campo.getTipoColonna()) {
            arrayValori = this.parseListaValori(
                filtroRicerca.getValoreConfronto(),
                filtroRicerca.getNotCaseSensitive().intValue() == 1);
          } else {
            arrayValori = filtroRicerca.getValoreConfronto().split(",");
          }
          // Creazione di un nuovo array di parametri per la ricerca, con
          // capacità maggiore
          ParametroStmt[] nuoviParametri = new ParametroStmt[parametri.length
              + arrayValori.length - 1];

          ArrayList<ParametroStmt> tmpParametri = new ArrayList<ParametroStmt>();
          // Copia degli elementi dell'array parametriRicerca in un ArrayList
          // temporaneo al primo elemento nullo, la copia si blocca
          for (int j = 0; j < parametri.length && parametri[j] != null; j++) {
            tmpParametri.add(parametri[j]);
          }
          // Aggiungo in coda i parametri per la lista dei valori per
          // l'operatore IN
          for (int k = 0; k < arrayValori.length; k++) {
            tmpParametri.add(new ParametroStmt(campo.getCodiceMnemonico(),
                arrayValori[k], campo.getTipoColonna()));
          }
          // Porto la lista completa dei parametri in un nuovo array di
          // parametri stmt
          for (int k = 0; k < tmpParametri.size(); k++) {
            nuoviParametri[k] = tmpParametri.get(k);
          }
          parametri = null;
          parametri = nuoviParametri;
          indice = tmpParametri.size();
        } else {
          String valoreConfronto = filtroRicerca.getValoreConfronto();
          // nel caso di confronto con un valore stringa, se indicata la ricerca
          // non sensibile a maiuscole minuscole, va fatto l'uppercase del
          // valore
          if ((Campo.TIPO_STRINGA == campo.getTipoColonna() || Campo.TIPO_NOTA == campo.getTipoColonna())
              && filtroRicerca.getNotCaseSensitive().intValue() == 1) {
            valoreConfronto = valoreConfronto.toUpperCase();
          }
          parametri[indice] = new ParametroStmt(campo.getCodiceMnemonico(),
              valoreConfronto, campo.getTipoColonna());
          indice++;
        }
        break;
      case FiltroRicerca.TIPO_CONFRONTO_PARAMETRO:
        // si inserisce il parametro inserito dall'utente all'atto della
        // richiesta di esecuzione della ricerca
        campo = dizCampi.get(filtroRicerca.getMnemonicoCampo());
        for (int j = 0; j < ricerca.getNumeroParametri(); j++) {
          if (ricerca.estraiParametro(j).getCodice().equals(
              filtroRicerca.getParametroConfronto())) {
            valore = parametriUtente[ricerca.estraiParametro(j).getProgressivo()];
            // nel caso di parametro inserito di tipo stringa, se si effettua un
            // confronto non sensibile a maiuscole/minuscole occorre porre il
            // valore inserito in maiuscolo per il confronto corretto nella query
            if ((Campo.TIPO_STRINGA == campo.getTipoColonna() || Campo.TIPO_NOTA == campo.getTipoColonna())
                && filtroRicerca.getNotCaseSensitive().intValue() == 1) {
              valore = valore.toUpperCase();
            }
          }
        }
        parametri[indice] = new ParametroStmt(campo.getCodiceMnemonico(),
            valore, campo.getTipoColonna());
        indice++;
        break;
      case FiltroRicerca.TIPO_CONFRONTO_DATA_ODIERNA:
        campo = dizCampi.get(filtroRicerca.getMnemonicoCampo());
        parametri[indice] = new ParametroStmt(campo.getCodiceMnemonico(),
            filtroRicerca.getValoreConfronto(), campo.getTipoColonna());
        indice++;
        break;
      case FiltroRicerca.TIPO_CONFRONTO_UTENTE_CONNESSO:
        campo = dizCampi.get(filtroRicerca.getMnemonicoCampo());
        parametri[indice] = new ParametroStmt(campo.getCodiceMnemonico(),
            filtroRicerca.getValoreConfronto(), campo.getTipoColonna());
        indice++;
        break;
      case FiltroRicerca.TIPO_CONFRONTO_UFFICIO_INTESTATARIO:
        campo = dizCampi.get(filtroRicerca.getMnemonicoCampo());
        parametri[indice] = new ParametroStmt(campo.getCodiceMnemonico(),
            filtroRicerca.getValoreConfronto(), campo.getTipoColonna());
        indice++;
        break;
      }
    }
    // L.G. 03/04/2007: modifica per inserire i parametri necessari alle
    // condizioni di filtro: infatti se esistono delle condizioni di filtro per
    // livello utente, le accodo all'array contenente i valori dei parametri
    if (elencoParametriFiltroLivelloUtente.size() > 0) {
      ParametroStmt[] tmp = parametri;
      parametri = new ParametroStmt[tmp.length
          + elencoParametriFiltroLivelloUtente.size()];
      // Ricopio l'array tmp nell'array parametri
      for (int i = 0; i < tmp.length; i++) {
        parametri[i] = tmp[i];
      }
      // Inserisco nell'array parametri i valori dei parametri per le condizioni
      // di filtro per livello utente
      for (int i = 0; i < elencoParametriFiltroLivelloUtente.size(); i++) {
        parametri[tmp.length + i] = new ParametroStmt("",
            (String) elencoParametriFiltroLivelloUtente.get(i),
            ParametroStmt.TIPO_INTERO);
      }
    }
    // L.G.: - fine modifica per parametri per filtro livello utente -

    // L.G.: inizio modifica per inserire i parametri necessari alle condizioni
    // per filtro ufficio intestatario: infatti se esistono delle condizioni di
    // filtro per ufficio intestatario, le accodo all'array dei parametri
    if (elencoParametriFiltroUfficioIntestatario.size() > 0) {
      ParametroStmt[] tmp = parametri;
      parametri = new ParametroStmt[tmp.length
          + elencoParametriFiltroUfficioIntestatario.size()];
      // Ricopio l'array tmp nell'array parametri
      for (int i = 0; i < tmp.length; i++) {
        parametri[i] = tmp[i];
      }
      // Inserisco nell'array parametri i valori dei parametri per le condizioni
      // di filtro per ufficio intestatario
      for (int i = 0; i < elencoParametriFiltroUfficioIntestatario.size(); i++) {
        parametri[tmp.length + i] = new ParametroStmt("",
            elencoParametriFiltroUfficioIntestatario.get(i),
            ParametroStmt.TIPO_STRINGA);
      }
    }
    // L.G.: fine modifica per parametri per filtro ufficio intestatario

    // SS20090318: integrazione parametri di KRONOS
    parametri = this.addParametriAggiuntivi(parametri, parametriAggiuntivi);
    return parametri;
  }

  /**
   * Aggiunge all'array di parametri ulteriori parametri
   *
   * @param parametri
   *        elenco dei parametri presenti nella query
   * @param parametriDaAggiungere
   *        parametri da aggiungere all'elenco parametri
   * @return elenco dei parametri modificati
   */
  private ParametroStmt[] addParametriAggiuntivi(ParametroStmt[] parametri,
      Vector<ParametroStmt> parametriDaAggiungere) {
    if (parametriDaAggiungere != null && parametriDaAggiungere.size() > 0) {
      Vector<ParametroStmt> parametriTMP = new Vector<ParametroStmt>();
      parametriTMP.addAll(Arrays.asList(parametri));
      parametriTMP.addAll(parametriDaAggiungere);
      parametri = new ParametroStmt[parametriTMP.size()];
      parametriTMP.copyInto(parametri);
    }
    return parametri;
  }

  /**
   * Verifica se vanno applicate le condizioni relative alle ricerche di tipo
   * KRONOS nella versione con integrazione KRONOS, gestendo i vari parametri
   * custom con l'aggiunta di una serie di condizioni di filtro
   *
   * @param ricerca
   *        ricerca da eseguire
   * @param parametriUtente
   *        parametri standard inseriti dall'utente
   * @param profiloUtente
   *        profilo dell'utente
   * @param elencoCondizioni
   *        elenco delle condizioni
   * @param elencoParametriDaAggiungere
   *        elenco dei parametri da aggiungere alla query in base alle nuove
   *        condizioni definite
   * @return elenco delle condizioni modificate
   * @throws SqlComposerException
   * @throws SQLException
   */
  private SqlElementoCondizione[] checkCondizioniIntegrazioneKronos(
      ContenitoreDatiRicerca ricerca, String[] parametriUtente,
      ProfiloUtente profiloUtente, SqlElementoCondizione[] elencoCondizioni,
      Vector<ParametroStmt> elencoParametriDaAggiungere)
      throws SqlComposerException {
    if (CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS.equals(ConfigManager.getValore(CostantiGenerali.PROP_INTEGRAZIONE))
        && CostantiIntegrazioneKronos.TIPO_RICERCA_KRONOS.equals(ricerca.getDatiGenerali().getTipo())) {

      boolean isIntegrazione = false;
      TabellaRicerca tabella = null;
      for (int i = 0; i < ricerca.getNumeroTabelle() && !isIntegrazione; i++) {
        // verifico e cerco la tabella principale per l'integrazione con KRONOS
        tabella = ricerca.getElencoArgomenti().elementAt(i);
        if (CostantiIntegrazioneKronos.MNEMONICO_ARGOMENTO_PRINCIPALE.equals(tabella.getMneTabella()))
          isIntegrazione = true;
      }
      if (isIntegrazione) {
        Vector<SqlElementoCondizione> condizioniAggiuntive = new Vector<SqlElementoCondizione>();

        // si aggiunge incondizionatamente il filtro sull'impresa
        condizioniAggiuntive.add(new SqlElementoCondizione(
            SqlElementoCondizione.OPERATORE_CONFRONTO_UGUALE, new SqlCampo(
                tabella.getAliasTabella(), "COD_IMPRESA")));
        elencoParametriDaAggiungere.add(new ParametroStmt(
            CostantiIntegrazioneKronos.PARAM_ESTERNO_IMPRESA,
            profiloUtente.getParametriUtente().get(
                CostantiIntegrazioneKronos.PARAM_ESTERNO_IMPRESA),
            ParametroStmt.TIPO_STRINGA));

        // si aggiunge incondizionatamente il filtro sulla quadratura
        condizioniAggiuntive.add(new SqlElementoCondizione(
            SqlElementoCondizione.OPERATORE_LOGICO_AND));
        condizioniAggiuntive.add(new SqlElementoCondizione(
            SqlElementoCondizione.OPERATORE_CONFRONTO_UGUALE, new SqlCampo(
                tabella.getAliasTabella(), "COD_QUADRATURA")));
        elencoParametriDaAggiungere.add(new ParametroStmt(
            CostantiIntegrazioneKronos.PARAM_ESTERNO_QUADRATURA,
            profiloUtente.getParametriUtente().get(
                CostantiIntegrazioneKronos.PARAM_ESTERNO_QUADRATURA),
            ParametroStmt.TIPO_STRINGA));

        // si aggiungono i filtri sulla validità
        this.setFiltriDataValiditaKronos(ricerca, profiloUtente,
            elencoParametriDaAggiungere, condizioniAggiuntive);

        // si verifica se esistono filtri su variabili utente, ed in tal caso si
        // aggiungono alla query
        this.setFiltriVariabiliUtente(ricerca, parametriUtente, profiloUtente,
            condizioniAggiuntive);

        if (elencoCondizioni != null && elencoCondizioni.length > 0) {
          // se esistono dei filtri, allora i filtri relativi all'integrazione
          // vanno accodati. L'espressione risultante sarà:
          // ( <condizioni precedenti> ) AND <condizioni integrazione>
          Vector<SqlElementoCondizione> elencoTMP = new Vector<SqlElementoCondizione>();
          elencoTMP.addAll(Arrays.asList(elencoCondizioni));
          elencoTMP.add(0, new SqlElementoCondizione(
              SqlElementoCondizione.OPERATORE_PARENTESI_APERTA));
          elencoTMP.add(new SqlElementoCondizione(
              SqlElementoCondizione.OPERATORE_PARENTESI_CHIUSA));
          elencoTMP.add(new SqlElementoCondizione(
              SqlElementoCondizione.OPERATORE_LOGICO_AND));
          elencoTMP.addAll(condizioniAggiuntive);
          elencoCondizioni = new SqlElementoCondizione[elencoTMP.size()];
          elencoTMP.copyInto(elencoCondizioni);
        } else {
          // altrimenti si usano solo i filtri dell'integrazione
          elencoCondizioni = new SqlElementoCondizione[condizioniAggiuntive.size()];
          condizioniAggiuntive.copyInto(elencoCondizioni);
        }
      }
    }
    return elencoCondizioni;
  }

  /**
   * Si aggiungono i filtri sulla data validità per le diverse tabelle che
   * rientrano nella ricerca
   *
   * @param ricerca
   *        definizione ricerca da eseguire
   * @param profiloUtente
   *        profilo dell'utente
   * @param elencoParametriDaAggiungere
   *        parametri da aggiungere
   * @param condizioniAggiuntive
   *        nuove condizioni da appendere alla query
   * @throws SqlComposerException
   */
  private void setFiltriDataValiditaKronos(ContenitoreDatiRicerca ricerca,
      ProfiloUtente profiloUtente, Vector<ParametroStmt> elencoParametriDaAggiungere,
      Vector<SqlElementoCondizione> condizioniAggiuntive) throws SqlComposerException {
    TabellaRicerca tabella;
    // imposta incondizionatamente il filtro sulle date validita'
    // si predispone l'elenco delle tabelle su cui ciclare
    Vector<String> elencoNomiTabelle = new Vector<String>();
    Vector<TabellaRicerca> elencoTabelle = new Vector<TabellaRicerca>();

    // tabella usata per popolare l'elenco nomi tabelle, e poi per ciclare
    // sugli elementi durante l'analisi della temporalità
    String nomeTabella = null;
    for (int i = 0; i < ricerca.getNumeroTabelle(); i++) {
      tabella = ricerca.getElencoArgomenti().elementAt(i);
      if (CostantiIntegrazioneKronos.MNEMONICO_ARGOMENTO_PRINCIPALE.equals(tabella.getMneTabella())) {
        elencoNomiTabelle.addAll(Arrays.asList(CostantiIntegrazioneKronos.ELENCO_TAB_VISTA_ARG_PRINCIPALE));
        // si aggiunge N volte la view principale dei dipendenti, una volta
        // per ogni entita contenuta
        for (int j = 0; j < elencoNomiTabelle.size(); j++)
          elencoTabelle.add(tabella);
      } else {
        nomeTabella = DizionarioTabelle.getInstance().get(
            tabella.getMneTabella()).getNomeTabella();
        if (nomeTabella.startsWith(CostantiIntegrazioneKronos.PREFISSO_VIEW_KRONOS))
          nomeTabella = nomeTabella.substring(3);
        elencoNomiTabelle.add(nomeTabella);
        elencoTabelle.add(tabella);
      }
    }

    String temporalita = null;
    String prefissoCampo = null;
    String dataInizioValidita = this.getCacheParametroRicerca(
        profiloUtente.getId(),
        ricerca.getDatiGenerali().getIdRicerca().intValue(),
        CostantiIntegrazioneKronos.PARAM_RICERCA_DATA_INIZIO_VALIDITA);
    String dataFineValidita = this.getCacheParametroRicerca(
        profiloUtente.getId(),
        ricerca.getDatiGenerali().getIdRicerca().intValue(),
        CostantiIntegrazioneKronos.PARAM_RICERCA_DATA_FINE_VALIDITA);

    for (int i = 0; i < elencoNomiTabelle.size(); i++) {
      nomeTabella = elencoNomiTabelle.elementAt(i);
      temporalita = this.kronosDao.getTemporalita(nomeTabella);
      tabella = elencoTabelle.elementAt(i);

      if (CostantiIntegrazioneKronos.MNEMONICO_ARGOMENTO_PRINCIPALE.equals(tabella.getMneTabella())) {
        // se la tabella è usata nella view principale dei dipendenti,
        // allora il nome nella view dei campi data contiene come
        // prefisso il nome della tabella originaria
        prefissoCampo = nomeTabella + "_";
      } else {
        prefissoCampo = "";
      }

      if ("PV".equals(temporalita) || "PT".equals(temporalita)) {
        if ("ANAGRLAV".equals(nomeTabella) || "PREVPOSTO".equals(nomeTabella)) {
          condizioniAggiuntive.add(new SqlElementoCondizione(
              SqlElementoCondizione.OPERATORE_LOGICO_AND));
          condizioniAggiuntive.add(new SqlElementoCondizione(
              SqlElementoCondizione.OPERATORE_PARENTESI_APERTA));
          condizioniAggiuntive.add(new SqlElementoCondizione(
              SqlElementoCondizione.OPERATORE_CONFRONTO_MINORE_UGUALE,
              new SqlCampo(tabella.getAliasTabella(), prefissoCampo
                  + "DTA_INIZIO")));
          condizioniAggiuntive.add(new SqlElementoCondizione(
              SqlElementoCondizione.OPERATORE_LOGICO_AND));
          condizioniAggiuntive.add(new SqlElementoCondizione(
              SqlElementoCondizione.OPERATORE_CONFRONTO_MAGGIORE_UGUALE,
              new SqlCampo(tabella.getAliasTabella(), prefissoCampo
                  + "DTA_FINE")));
          condizioniAggiuntive.add(new SqlElementoCondizione(
              SqlElementoCondizione.OPERATORE_PARENTESI_CHIUSA));
          elencoParametriDaAggiungere.add(new ParametroStmt(
              CostantiIntegrazioneKronos.PARAM_RICERCA_DATA_FINE_VALIDITA,
              dataFineValidita, ParametroStmt.TIPO_DATA));
          elencoParametriDaAggiungere.add(new ParametroStmt(
              CostantiIntegrazioneKronos.PARAM_RICERCA_DATA_INIZIO_VALIDITA,
              dataInizioValidita, ParametroStmt.TIPO_DATA));
        }
      }
      if ("VT".equals(temporalita)) {
        String aliasTabellaInner = "com" + i;
        StringBuffer sb = new StringBuffer("");
        sb.append("(SELECT MAX(").append(aliasTabellaInner).append(
            ".DTA_INIZIO)");
        sb.append(" FROM ").append(
            CostantiIntegrazioneKronos.PREFISSO_VIEW_KRONOS).append(nomeTabella).append(
            " ").append(aliasTabellaInner);
        sb.append(" WHERE ").append(aliasTabellaInner).append(
            ".DTA_INIZIO <= ?");
        elencoParametriDaAggiungere.add(new ParametroStmt(
            CostantiIntegrazioneKronos.PARAM_RICERCA_DATA_FINE_VALIDITA,
            dataFineValidita, ParametroStmt.TIPO_DATA));
        if ("INCARLAV".equals(nomeTabella)) {
          // se la tabella è INCARLAV va aggiunta una condizione di filtro
          sb.append(" AND ").append(aliasTabellaInner).append(
              ".IND_INCPRINC = ?");
          elencoParametriDaAggiungere.add(new ParametroStmt("IND_INCPRINC",
              "Y", ParametroStmt.TIPO_STRINGA));
        }
        // ora si determinano e si aggiungono tutte le condizioni di legame
        // con le tabelle presenti nella select principale
        List<?> listaLegami = this.kronosDao.getCampiJoin(nomeTabella);
        HashMap<?,?> record = null;
        TabellaRicerca tab = null;
        Tabella defTabella = null;
        for (int z = 0; z < listaLegami.size(); z++) {
          record = (HashMap<?,?>) listaLegami.get(z);
          // lettura dei dati estratti
          String campo = (String) UtilityHashMap.getValueCaseInsensitive(record, "campo");
          String tabellaPk = (String) UtilityHashMap.getValueCaseInsensitive(record, "tabella_pk");
          String campoPk = (String) UtilityHashMap.getValueCaseInsensitive(record, "campo_pk");
          for (int contArg = 0; contArg < ricerca.getElencoArgomenti().size(); contArg++) {
            tab = ricerca.getElencoArgomenti().get(contArg);
            defTabella = DizionarioTabelle.getInstance().get(
                tab.getMneTabella());
            // se l'argomento considerato è la view sui dipendenti, e la
            // tabella da cercare è usata nella view, oppure se la tabella è
            // proprio l'argomento considerato, allora si aggiunge la join
            if ((CostantiIntegrazioneKronos.MNEMONICO_ARGOMENTO_PRINCIPALE.equals(tab.getMneTabella()) && Arrays.asList(
                CostantiIntegrazioneKronos.ELENCO_TAB_VISTA_ARG_PRINCIPALE).contains(
                tabellaPk))
                || (defTabella.getNomeTabella().equals(CostantiIntegrazioneKronos.PREFISSO_VIEW_KRONOS
                    + tabellaPk))) {
              sb.append(" AND ").append(aliasTabellaInner).append(".").append(
                  campo);
              sb.append(" = ").append(tab.getAliasTabella()).append(".").append(
                  campoPk);
            }
          }
        }

        sb.append(")");

        // si aggiunge quindi la condizione finale
        condizioniAggiuntive.add(new SqlElementoCondizione(
            SqlElementoCondizione.OPERATORE_LOGICO_AND));
        condizioniAggiuntive.add(new SqlElementoCondizione(
            SqlElementoCondizione.OPERATORE_CONFRONTO_UGUALE, new SqlCampo(
                tab.getAliasTabella(), prefissoCampo + "DTA_INIZIO"),
            new SqlStringa(sb.toString())));
      }
    }
  }

  /**
   * Si aggiungono i filtri sulle variabili utente
   *
   * @param ricerca
   *        definizione ricerca da eseguire
   * @param parametriUtente
   *        parametri standard inseriti dall'utente
   * @param profiloUtente
   *        profilo dell'utente
   * @param condizioniAggiuntive
   *        nuove condizioni da appendere alla query
   * @throws SqlComposerException
   */
  private void setFiltriVariabiliUtente(ContenitoreDatiRicerca ricerca,
      String[] parametriUtente, ProfiloUtente profiloUtente,
      Vector<SqlElementoCondizione> condizioniAggiuntive) throws SqlComposerException {
    ParametroRicerca parametro = null;
    String filtroSelezionato = null;
    String aliasArgomentoPrincipale = null;
    // si cerca la view principale di base per tutte le ricerche sui dipendenti
    for (int i = 0; i < ricerca.getNumeroTabelle(); i++) {
      if (ricerca.estraiArgomento(i).getMneTabella().equals(
          CostantiIntegrazioneKronos.MNEMONICO_ARGOMENTO_PRINCIPALE)) {
        aliasArgomentoPrincipale = ricerca.estraiArgomento(i).getAliasTabella();
        break;
      }
    }
    // si controlla se sono previsti parametri di tipo variabile utente
    for (int i = 0; i < ricerca.getNumeroParametri(); i++) {
      parametro = ricerca.estraiParametro(i);
      if (parametro.getCodice().startsWith(
          CostantiIntegrazioneKronos.PREFISSO_VARIABILE_UTENTE)) {
        filtroSelezionato = parametriUtente[i];
        // si sostituiscono i filtri sulle tabelle di origine con filtri sulla
        // view principale
        for (int j = 0; j < CostantiIntegrazioneKronos.ELENCO_TAB_VISTA_ARG_PRINCIPALE.length; j++)
          filtroSelezionato = StringUtils.replace(filtroSelezionato,
              CostantiIntegrazioneKronos.ELENCO_TAB_VISTA_ARG_PRINCIPALE[j]
                  + ".", aliasArgomentoPrincipale + ".");
        // si definisce il nuovo filtro
        condizioniAggiuntive.add(new SqlElementoCondizione(
            SqlElementoCondizione.OPERATORE_LOGICO_AND));
        condizioniAggiuntive.add(new SqlElementoCondizione(
            SqlElementoCondizione.OPERATORE_NON_PRESENTE, new SqlStringa(
                filtroSelezionato)));
      }
    }
  }

  /**
   * Estrae tutti i record del risultato della ricerca a partire dai criteri
   * individuati e dai parametri indicati in input
   *
   * @param contenitorePerModel
   *        ricerca da cui partire per estrarre i dati
   * @param parametriUtente
   *        parametri valorizzati all'atto dell'esecuzione da parte dell'utente
   * @param codApp
   *        codice dell'appicazione in uso
   * @return contenitore con i dati estratti
   * @throws SqlComposerException
   * @throws QueryDaoException
   * @throws DataAccessException
   */
  public DatiRisultato getRisultatiRicercaSql(ContenitoreDatiRicerca ricerca, String[] parametriUtente,
      String codApp) throws SqlComposerException, DataAccessException, QueryDaoException {

    // Alla ricerca in sessione setto il numero di risultati per pagina a null
    // in modo da estrarre tutti i record della ricerca
    ricerca.getDatiGenerali().setRisPerPag(null);

    // Richiamo il metodo getRisultatiRicercaSql con numero di pagina, passando
    // pero' un numero di pagina fittizio
    return this.getRisultatiRicercaSql(ricerca, parametriUtente, codApp, NUMERO_PAGINA_FITTIZIO);
  }

  public DatiRisultato getRisultatiRicerca(ContenitoreDatiRicerca ricerca,
      String codiceUfficioIntestatarioAttivo, String[] parametriUtente,
      ProfiloUtente profiloUtente, String codApp)
          throws SqlComposerException, DataAccessException, QueryDaoException {

    // Alla ricerca in sessione setto il numero di risultati per pagina a null
    // in modo da estrarre tutti i record della ricerca
    ricerca.getDatiGenerali().setRisPerPag(null);

    // Richiamo il metodo getRisultatiRicerca con numero di pagina, passando
    // pero' un numero di pagina fittizio
    return this.getRisultatiRicerca(ricerca, codiceUfficioIntestatarioAttivo,
        parametriUtente, profiloUtente, codApp, NUMERO_PAGINA_FITTIZIO);
  }


  private void estraiCampiSelectEGroupBy(ContenitoreDatiRicerca ricerca,
      Vector<Campo> elencoCampiChiave, Vector<SqlElementoSelect> elencoCampiSelect,
      Vector<SqlCampo> elencoCampiGroupBy, DizionarioCampi dizCampi,
      DizionarioTabelle dizTabelle) throws SqlComposerException {

    Campo campo = null;
    CampoRicerca campoRicerca = null;
    SqlElementoDecorabile campoTmp = null;
    SqlElementoDecorabile campoDecoratoTmp = null;
    boolean isStatistichePresenti = false;

    for (int i = 0; i < ricerca.getNumeroCampi(); i++) {
      campoRicerca = ricerca.estraiCampo(i);
      campo = dizCampi.get(campoRicerca.getMneCampo());
      campoTmp = new SqlCampo(campoRicerca.getAliasTabella(),
          campo.getNomeCampo());
      if (UtilityStringhe.convertiStringaVuotaInNull(campoRicerca.getFunzione()) != null) {
        campoDecoratoTmp = FactoryFunzione.getFunzione(campoTmp, campoRicerca.getFunzione());
        isStatistichePresenti = true;
      } else {
        campoDecoratoTmp = campoTmp;
        elencoCampiGroupBy.addElement(new SqlCampo(
            campoRicerca.getAliasTabella(), campo.getNomeCampo()));
      }

      elencoCampiSelect.addElement(new SqlElementoSelect(campoDecoratoTmp,
          PREFISSO_CAMPI_SELECT + (i + 1)));
    }

    // si aggiungono i campi che costituiscono la chiave dell'entità principale
    // solo se non sono presenti funzioni statistiche che implicano un
    // raggruppamento e quindi un partizionamento dei dati estratti
    if (!isStatistichePresenti) {
      // si inseriscono i campi chiave solo se è richiesta la visualizzazione di
      // modelli predisposti
      if (ricerca.getDatiGenerali().getVisModelli() == 1
          || ricerca.getDatiGenerali().getLinkScheda() == 1) {
        for (int i = 0; i < elencoCampiChiave.size(); i++) {
          campo = elencoCampiChiave.elementAt(i);
          elencoCampiSelect.addElement(new SqlElementoSelect(new SqlCampo(
              ricerca.getDatiGenerali().getEntPrinc(), campo.getNomeCampo()),
              PREFISSO_CAMPI_CHIAVE_SELECT + (i + 1)));
        }
      }
      // non essendoci statistiche, svuoto l'elenco dei campi di raggruppamento
      elencoCampiGroupBy.removeAllElements();
    } else {
      // se si utilizzano le funzioni statistiche, allora non ha è corretto
      // riportare i campi chiave, i quali vengono svuotati
      elencoCampiChiave.removeAllElements();
    }

  }

  /**
   * Estrae l'elenco delle definizioni dei campi da estrarre, ed inoltre
   * valorizza il set dei tabellati con l'elenco dei tabellati da utilizzare
   * nella ricerca
   *
   * @param ricerca
   *        contenitore che censisce la ricerca
   * @param elencoCampiChiave
   *        elenco dei campi che costituiscono la chiave dell'entità principale
   *        della ricerca
   * @param dizCampi
   *        dizionario dei campi
   * @param setTabellati
   *        set delle famiglie di tabellati utilizzati; è un parametro
   *        valorizzato in uscita
   * @return elenco delle definizioni dei campi
   */
  private Campo[] estraiCampi(ContenitoreDatiRicerca ricerca,
      Vector<Campo> elencoCampiChiave, DizionarioCampi dizCampi, Vector<String> setTabellati) {
    Campo[] campi = new Campo[ricerca.getNumeroCampi()
        + elencoCampiChiave.size()];
    Campo campoTmp = null;
    CampoRicerca campoRicerca = null;
    short tipoColonna;

    for (int i = 0; i < ricerca.getNumeroCampi(); i++) {
      campoRicerca = ricerca.getElencoCampi().elementAt(i);
      campoTmp = dizCampi.get(campoRicerca.getMneCampo());
      tipoColonna = campoTmp.getTipoColonna();
      if ("COUNT".equals(campoRicerca.getFunzione()))
        tipoColonna = Campo.TIPO_INTERO;

      campi[i] = new Campo(campoTmp.getCodiceMnemonico(),
          campoTmp.getNomeFisico(), campoTmp.getDescrizione(),
          campoTmp.getDescrizioneBreve(), campoTmp.getDescrizioneWEB(),
          campoTmp.isVisibileRicerche(), tipoColonna, campoTmp.isCampoChiave(),
          campoTmp.getCodiceTabellato());

      if (campoTmp.getCodiceTabellato() != null){
        setTabellati.add(campoTmp.getCodiceTabellato());}

      campi[i].setDominio(campoTmp.getDominio());
      // WE315: aggiunto settaggio mancante di lunghezza e decimali
      campi[i].setLunghezza(campoTmp.getLunghezza());
      campi[i].setDecimali(campoTmp.getDecimali());
    }

    for (int i = 0; i < elencoCampiChiave.size(); i++) {
      campoTmp = elencoCampiChiave.elementAt(i);
      campi[ricerca.getNumeroCampi() + i] = new Campo(
          campoTmp.getCodiceMnemonico(), campoTmp.getNomeFisico(),
          campoTmp.getDescrizione(), campoTmp.getDescrizioneBreve(),
          campoTmp.getDescrizioneWEB(), campoTmp.isVisibileRicerche(),
          campoTmp.getTipoColonna(), campoTmp.isCampoChiave(),
          campoTmp.getCodiceTabellato());

      campi[ricerca.getNumeroCampi() + i].setDominio(campoTmp.getDominio());
      // WE315: aggiunto settaggio mancante di lunghezza e decimali
      campi[ricerca.getNumeroCampi() + i].setLunghezza(campoTmp.getLunghezza());
      campi[ricerca.getNumeroCampi() + i].setDecimali(campoTmp.getDecimali());
    }

    return campi;
  }

  /**
   * Estrae la descrizione di un elemento di un tabellato a partire dalla lista
   * contenente tutti i valori possibili e la chiave da ricercare
   *
   * @param listaTabellati
   *        elenco dei possibili valori tabellati
   * @param valore
   *        chiave dell'elemento del tabellato
   * @return descrizione associata al tabellato stesso
   */
  private String estraiDescrizioneTabellato(List<Tabellato> listaTabellati, String valore) {
    String risultato = valore;
    for (int i = 0; i < listaTabellati.size(); i++) {
      Tabellato tabellato = listaTabellati.get(i);

      if (tabellato.getTipoTabellato().equals(valore))
        risultato = tabellato.getDescTabellato();
    }
    return risultato;
  }

  private SqlElementoOrderBy[] estraiCampiOrderBy(
      ContenitoreDatiRicerca ricerca, DizionarioCampi dizCampi,
      DizionarioTabelle dizTabelle) throws SqlComposerException {
    SqlElementoOrderBy[] elencoCampi = new SqlElementoOrderBy[ricerca.getNumeroOrdinamenti()];

    Campo campo = null;
    OrdinamentoRicerca ordinamentoRicerca = null;
    String aliasTabella = null;
    for (int i = 0; i < ricerca.getNumeroOrdinamenti(); i++) {
      ordinamentoRicerca = ricerca.estraiOrdinamento(i);
      campo = dizCampi.get(ordinamentoRicerca.getMnemonicoCampo());
      aliasTabella = ordinamentoRicerca.getAliasTabella();
      elencoCampi[i] = new SqlElementoOrderBy(new SqlCampo(aliasTabella,
          campo.getNomeCampo()), (short) ordinamentoRicerca.getOrdinamento());
    }

    return elencoCampi;
  }

  private SqlTabella[] estraiTabelle(ContenitoreDatiRicerca ricerca,
      DizionarioCampi dizCampi, DizionarioTabelle dizTabelle)
      throws SqlComposerException {
    SqlTabella[] elencoTabelle = new SqlTabella[ricerca.getNumeroTabelle()];

    Tabella tabella = null;
    TabellaRicerca tabellaRicerca = null;
    for (int i = 0; i < ricerca.getNumeroTabelle(); i++) {
      tabellaRicerca = ricerca.estraiArgomento(i);
      tabella = dizTabelle.get(tabellaRicerca.getMneTabella());
      elencoTabelle[i] = new SqlTabella(tabella.getNomeTabella(),
          tabellaRicerca.getAliasTabella());
    }

    return elencoTabelle;
  }

  private SqlJoin[] estraiLegamiTabelle(ContenitoreDatiRicerca ricerca,
      DizionarioCampi dizCampi, DizionarioTabelle dizTabelle)
      throws SqlComposerException {
    SqlJoin[] elencoLegami = null;

    Vector<SqlJoin> vettoreGiunzioni = new Vector<SqlJoin>();

    GiunzioneRicerca giunzioneRicerca = null;
    Tabella tabella1 = null;
    Tabella tabella2 = null;
    String[] campiLegameTabella1 = null;
    String[] campiLegameTabella2 = null;
    SqlTabella sqlTabella1 = null;
    SqlTabella sqlTabella2 = null;
    SqlCampo[] sqlCampi1 = null;
    SqlCampo[] sqlCampi2 = null;
    String aliasTabella = null;

    for (int i = 0; i < ricerca.getNumeroGiunzioni(); i++) {
      giunzioneRicerca = ricerca.estraiGiunzione(i);

      // giunzione attiva passa a short quindi 1 true, 0 false
      if (giunzioneRicerca.getGiunzioneAttiva() == 1) {
        // estrazione metadati associati

        tabella1 = dizTabelle.get(giunzioneRicerca.getMnemonicoTabella1());
        tabella2 = dizTabelle.get(giunzioneRicerca.getMnemonicoTabella2());

        campiLegameTabella1 = UtilityStringhe.deserializza(
            giunzioneRicerca.getCampiTabella1(),
            GiunzioneRicerca.SEPARATORE_CAMPI_JOIN);
        campiLegameTabella2 = UtilityStringhe.deserializza(
            giunzioneRicerca.getCampiTabella2(),
            GiunzioneRicerca.SEPARATORE_CAMPI_JOIN);

        // costruzione parametri
        sqlTabella1 = new SqlTabella(tabella1.getNomeTabella(),
            giunzioneRicerca.getAliasTabella1());
        sqlTabella2 = new SqlTabella(tabella2.getNomeTabella(),
            giunzioneRicerca.getAliasTabella2());
        sqlCampi1 = new SqlCampo[campiLegameTabella1.length];
        sqlCampi2 = new SqlCampo[campiLegameTabella2.length];
        for (int contCampiLegame = 0; contCampiLegame < campiLegameTabella1.length; contCampiLegame++) {
          aliasTabella = sqlTabella1.getAlias();
          sqlCampi1[contCampiLegame] = new SqlCampo(aliasTabella,
              campiLegameTabella1[contCampiLegame]);
          aliasTabella = sqlTabella2.getAlias();
          sqlCampi2[contCampiLegame] = new SqlCampo(aliasTabella,
              campiLegameTabella2[contCampiLegame]);
        }

        // costruzione del legame
        vettoreGiunzioni.addElement(new SqlJoin(sqlTabella1, sqlCampi1,
            sqlTabella2, sqlCampi2, giunzioneRicerca.getTipoGiunzione()));
      }
    }
    elencoLegami = new SqlJoin[vettoreGiunzioni.size()];
    vettoreGiunzioni.copyInto(elencoLegami);

    return elencoLegami;
  }

  private SqlElementoCondizione[] estraiCondizioni(ContenitoreDatiRicerca ricerca,
      DizionarioCampi dizCampi, DizionarioTabelle dizTabelle, Vector<FiltroRicerca> elencoParametriFiltri,
      SqlComposer sqlComposer, ProfiloUtente profiloUtente,
      String codiceUfficioIntestatarioAttivo) throws SqlComposerException {

    SqlElementoCondizione[] elencoCondizioni = new SqlElementoCondizione[ricerca.getNumeroFiltri()];

    short tipoOperatore = 0;
    Campo campo = null;
    SqlElementoDecorabile sqlCampo = null;
    SqlElementoDecorabile sqlCampoConfronto = null;
    FiltroRicerca filtroRicerca = null;

    for (int i = 0; i < ricerca.getNumeroFiltri(); i++) {
      filtroRicerca = ricerca.estraiFiltro(i);
      tipoOperatore = SqlElementoCondizione.getTipoOperatore(filtroRicerca.getOperatore());

      if ("".equals(UtilityStringhe.convertiNullInStringaVuota(filtroRicerca.getMnemonicoTabella()))) {
        // e' presente solo l'operatore, e non la tabella principale
        elencoCondizioni[i] = new SqlElementoCondizione(tipoOperatore);
      } else {
        campo = dizCampi.get(filtroRicerca.getMnemonicoCampo());
        sqlCampo = new SqlCampo(filtroRicerca.getAliasTabella(),
            campo.getNomeCampo());

        if (filtroRicerca.getTipoConfronto() == null) {
          // si tratta di un operatore unario
          elencoCondizioni[i] = new SqlElementoCondizione(tipoOperatore,
              sqlCampo);
        } else {
          // si tratta di un operatore binario
          // switch (Short.parseShort(filtroRicerca.getTipoConfronto())) {

          boolean confrontoNonCaseSensitive = filtroRicerca.getNotCaseSensitive().intValue() == 1;

          // nel caso di confronto non case sensitive, va fatto l'uppercase del
          // campo per effettuare il controllo tutto in maiuscole
          if ((Campo.TIPO_STRINGA == campo.getTipoColonna() || Campo.TIPO_NOTA == campo.getTipoColonna())
              && confrontoNonCaseSensitive) {
            sqlCampo = new SqlStringa(sqlComposer.getFunzioneUpperCase()
                + "("
                + sqlCampo.toString()
                + ")");
          }

          switch (filtroRicerca.getTipoConfronto().shortValue()) {
          case FiltroRicerca.TIPO_CONFRONTO_CAMPO:
            campo = dizCampi.get(filtroRicerca.getMnemonicoCampoConfronto());
            sqlCampoConfronto = new SqlCampo(
                filtroRicerca.getAliasTabellaConfronto(), campo.getNomeCampo());
            if ((Campo.TIPO_STRINGA == campo.getTipoColonna() || Campo.TIPO_NOTA == campo.getTipoColonna())
                && confrontoNonCaseSensitive) {
              // nel caso di confronto non case sensitive, va fatto l'uppercase
              // di entrambe gli argomenti
              sqlCampoConfronto = new SqlStringa(
                  sqlComposer.getFunzioneUpperCase()
                      + "("
                      + sqlCampoConfronto.toString()
                      + ")");
            }
            elencoCondizioni[i] = new SqlElementoCondizione(tipoOperatore,
                sqlCampo, sqlCampoConfronto);
            break;
          case FiltroRicerca.TIPO_CONFRONTO_VALORE:
            if (SqlElementoCondizione.OPERATORE_CONFRONTO_IN == tipoOperatore
                || SqlElementoCondizione.OPERATORE_CONFRONTO_NOT_IN == tipoOperatore) {
              String[] listaValori = null;
              if (Campo.TIPO_STRINGA == campo.getTipoColonna() || Campo.TIPO_NOTA == campo.getTipoColonna()) {
                listaValori = this.parseListaValori(filtroRicerca.getValoreConfronto(),
                    confrontoNonCaseSensitive);
              } else {
                listaValori = filtroRicerca.getValoreConfronto().split(",");
              }

              elencoCondizioni[i] = new SqlElementoCondizione(tipoOperatore,
                  sqlCampo, new SqlValoriInclusione(listaValori.length));
              elencoParametriFiltri.addElement(filtroRicerca);
            } else {
              // Nel caso di operatore in alla lista dei valori aggiungo le
              // la parentesi aperta e la parentesi chiusa
              elencoCondizioni[i] = new SqlElementoCondizione(tipoOperatore, sqlCampo);
              elencoParametriFiltri.addElement(filtroRicerca);
            }
            break;
          case FiltroRicerca.TIPO_CONFRONTO_PARAMETRO:
            elencoCondizioni[i] = new SqlElementoCondizione(tipoOperatore, sqlCampo);
            elencoParametriFiltri.addElement(filtroRicerca);
            break;
          // L.G. 01/02/2007: modifica per nuovo tipo di confronto: data odierna
          case FiltroRicerca.TIPO_CONFRONTO_DATA_ODIERNA:
            elencoCondizioni[i] = new SqlElementoCondizione(tipoOperatore, sqlCampo);
            filtroRicerca.setValoreConfronto(UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA));
            elencoParametriFiltri.addElement(filtroRicerca);
            break;
          case FiltroRicerca.TIPO_CONFRONTO_UTENTE_CONNESSO:
            elencoCondizioni[i] = new SqlElementoCondizione(tipoOperatore, sqlCampo);
            filtroRicerca.setValoreConfronto("" + profiloUtente.getId());
            elencoParametriFiltri.addElement(filtroRicerca);
            break;
          case FiltroRicerca.TIPO_CONFRONTO_UFFICIO_INTESTATARIO:
            elencoCondizioni[i] = new SqlElementoCondizione(tipoOperatore, sqlCampo);
            filtroRicerca.setValoreConfronto(codiceUfficioIntestatarioAttivo);
            elencoParametriFiltri.addElement(filtroRicerca);
          default:
            break;
          }
        }
      }
    }

    return elencoCondizioni;
  }

  /**
   * Creazione delle condizioni di filtro per il filtraggio per Id Utente. Tali
   * condizioni saranno comprese fra parentesi e, se esistono altre condizioni
   * di filtro, accodate alle altre con un operatore di AND. In questo modo si
   * otterrà un comando sql del tipo: .... WHERE ( condizioni di filtro definite
   * dal client ) AND (condizioni di filtro su Id Utente)
   *
   * oppure .... WHERE (condizioni di filtro su Id Utente)
   *
   * a seconda che esistano o meno altre condizioni di filtro
   *
   * @param ricerca
   * @param dizTabelle
   * @return Ritorna l'array di oggetti SqlElementoCondizione
   */
  private SqlElementoCondizione[] estraiCondizioniFiltroUtente(
      Vector<TabellaRicerca> elencoTabelle, DizionarioTabelle dizTabelle,
      SqlElementoCondizione[] elencoCondizioni,
      FiltroLivelloUtente filtroLivelloUtente,
      Vector<String> elencoParametriFiltroLivelloUtente) throws SqlComposerException {

    Vector<SqlElementoCondizione> elencoCondizioneFiltroIdUtente = null;
    // Vettore che conterra' le condizioni di filtro aggiuntive per filtrare
    // per Id Utente
    elencoCondizioneFiltroIdUtente = new Vector<SqlElementoCondizione>();

    DizionarioLivelli dizLivelli = DizionarioLivelli.getInstance();
    Tabella tabella = null;
    boolean condizioniDiFiltroPreesistenti = elencoCondizioni.length > 0;

    // Nel caso la ricerca abbia delle condizione di filtro oltre al filtro per
    // Id Utente inserisco l'operatore logico AND e la parentesi aperta per
    if (condizioniDiFiltroPreesistenti) {
      elencoCondizioneFiltroIdUtente.addElement(new SqlElementoCondizione(
          SqlElementoCondizione.OPERATORE_LOGICO_AND));
      elencoCondizioneFiltroIdUtente.addElement(new SqlElementoCondizione(
          SqlElementoCondizione.OPERATORE_PARENTESI_APERTA));
    }

    boolean inseritoPrimoFiltroUtente = false;
    for (int i = 0; i < elencoTabelle.size(); i++) {
      tabella = dizTabelle.get((elencoTabelle.get(i)).getMneTabella());
      if (dizLivelli.isFiltroLivelloPresente(tabella.getNomeTabella())) {
        Livello livello = dizLivelli.get(tabella.getNomeTabella());
        filtroLivelloUtente.setLivello(livello,
            (elencoTabelle.get(i)).getAliasTabella());
        if (filtroLivelloUtente.getCondizione() != null) {
          if (inseritoPrimoFiltroUtente)
            elencoCondizioneFiltroIdUtente.addElement(new SqlElementoCondizione(
                SqlElementoCondizione.OPERATORE_LOGICO_AND));
          else
            inseritoPrimoFiltroUtente = true;
          elencoCondizioneFiltroIdUtente.addElement(filtroLivelloUtente.getCondizione());
          elencoParametriFiltroLivelloUtente.addElement(""
              + (filtroLivelloUtente.getValore()));
        }
      }
    }

    // Nel caso la ricerca abbia delle condizione di filtro oltre al filtro per
    // Id Utente inserisco la parentesi chiusa
    if (condizioniDiFiltroPreesistenti) {
      elencoCondizioneFiltroIdUtente.addElement(new SqlElementoCondizione(
          SqlElementoCondizione.OPERATORE_PARENTESI_CHIUSA));
    }

    // SS 05/07/2007: correzione bug
    // il filtro è definito e va restituito se il contenitore lo possiede, ma
    // bisogna stare attenti che nel contenitore non ci siano AND (...) nel caso
    // in cui ci siano altri filtri esistenti
    if ((condizioniDiFiltroPreesistenti && elencoCondizioneFiltroIdUtente.size() > 3)
        || (!condizioniDiFiltroPreesistenti && elencoCondizioneFiltroIdUtente.size() > 0)) {
      return elencoCondizioneFiltroIdUtente.toArray(new SqlElementoCondizione[0]);
    } else {
      elencoParametriFiltroLivelloUtente.removeAllElements();
      return null;
    }
  }

  /**
   * Creazione delle condizioni di filtro per il filtraggio per ufficio intestatario. Tali
   * condizioni saranno comprese fra parentesi e, se esistono altre condizioni
   * di filtro, accodate alle altre con un operatore di AND. In questo modo si
   * otterrà un comando sql del tipo: .... WHERE ( condizioni di filtro definite
   * dal client ) AND (condizioni di filtro su ufficio intestatario)
   *
   * oppure .... WHERE (condizioni di filtro su ufficio intestatario)
   *
   * a seconda che esistano o meno altre condizioni di filtro
   *
   * @param ricerca
   * @param dizTabelle
   * @return Ritorna l'array di oggetti SqlElementoCondizione
   */
  private SqlElementoCondizione[] estraiCondizioniFiltroUfficioIntestatario(
      Vector<TabellaRicerca> elencoTabelle, DizionarioTabelle dizTabelle,
      SqlElementoCondizione[] elencoCondizioni, ProfiloUtente profiloUtente,
      Vector<String> elencoParametriFiltroUfficioIntestatario,
      String codiceUfficioIntestatariotAttivo) throws SqlComposerException {

    Vector<SqlElementoCondizione> elencoCondizioneFiltroUffInt = null;
    // Vettore che conterra' le condizioni di filtro aggiuntive per filtrare
    // per ufficio intestatario
    elencoCondizioneFiltroUffInt = new Vector<SqlElementoCondizione>();

    //DizionarioLivelli dizLivelli = DizionarioLivelli.getInstance();
    Tabella tabella = null;
    boolean condizioniDiFiltroPreesistenti = elencoCondizioni.length > 0;

    // Nel caso la ricerca abbia delle condizione di filtro oltre al filtro per
    // ufficio intestatario inserisco l'operatore logico AND e la parentesi aperta
    if (condizioniDiFiltroPreesistenti) {
      elencoCondizioneFiltroUffInt.addElement(new SqlElementoCondizione(
          SqlElementoCondizione.OPERATORE_LOGICO_AND));
      elencoCondizioneFiltroUffInt.addElement(new SqlElementoCondizione(
          SqlElementoCondizione.OPERATORE_PARENTESI_APERTA));
    }

    boolean inseritoPrimoFiltroUffInt = false;
    for (int i = 0; i < elencoTabelle.size(); i++) {
      tabella = dizTabelle.get((elencoTabelle.get(i)).getMneTabella());

      LegameTabelle[] legamiTabelle = null;
      SqlElementoCondizione sqlCondizione = null;

      legamiTabelle = dizTabelle.getDaNomeTabella("UFFINT").getLegameTabelle(
          tabella.getNomeTabella());

      if (legamiTabelle.length > 0) {
        // Se la UFFINT e' in relazione con la i-esima tabella selezionata
        // per il report, allora si considera il primo fra i possibili legami
        // esistenti tra le due tabelle
        LegameTabelle legameTabelle = legamiTabelle[0];

        String nomeTabella = legameTabelle.getTabellaDestinazione();
        String[] campiTabella = legameTabelle.getElencoCampiTabellaDestinazione();

        sqlCondizione = new SqlElementoCondizione(SqlElementoCondizione.OPERATORE_CONFRONTO_UGUALE,
            new SqlCampo(nomeTabella, campiTabella[0]));
      } else {
        // Se la UFFINT non e' in relazione con la i-esima tabella selezionata
        // per il report, allora si cerca se esistono relazioni tra la i-esima
        // tabella selezionata e la UFFINT.
        legamiTabelle = null;
        legamiTabelle = dizTabelle.getDaNomeTabella(
            tabella.getNomeTabella()).getLegameTabelle("UFFINT");

        if (legamiTabelle.length > 0) {
          // Se esistono tali relazioni, allora si considera il primo fra i
          // possibili legami esistenti tra le due tabelle
          LegameTabelle legameTabelle = legamiTabelle[0];

          String nomeTabella = legameTabelle.getTabellaOrigine();
          String[] campiTabella = legameTabelle.getElencoCampiTabellaOrigine();

          sqlCondizione = new SqlElementoCondizione(SqlElementoCondizione.OPERATORE_CONFRONTO_UGUALE,
              new SqlCampo(nomeTabella, campiTabella[0]));
        }
      }

      if (sqlCondizione != null) {
        if (inseritoPrimoFiltroUffInt) {
          elencoCondizioneFiltroUffInt.addElement(new SqlElementoCondizione(
              SqlElementoCondizione.OPERATORE_LOGICO_AND));
        } else {
          inseritoPrimoFiltroUffInt = true;
        }

        elencoCondizioneFiltroUffInt.addElement(sqlCondizione);
        elencoParametriFiltroUfficioIntestatario.addElement(codiceUfficioIntestatariotAttivo);
      }
    }

    // Nel caso la ricerca abbia delle condizione di filtro oltre al filtro per
    // ufficio intestatario inserisco la parentesi chiusa
    if (condizioniDiFiltroPreesistenti) {
      elencoCondizioneFiltroUffInt.addElement(new SqlElementoCondizione(
          SqlElementoCondizione.OPERATORE_PARENTESI_CHIUSA));
    }

    // SS 05/07/2007: correzione bug
    // il filtro è definito e va restituito se il contenitore lo possiede, ma
    // bisogna stare attenti che nel contenitore non ci siano AND (...) nel caso
    // in cui ci siano altri filtri esistenti
    if ((condizioniDiFiltroPreesistenti && elencoCondizioneFiltroUffInt.size() > 3)
        || (!condizioniDiFiltroPreesistenti && elencoCondizioneFiltroUffInt.size() > 0)) {
      return elencoCondizioneFiltroUffInt.toArray(new SqlElementoCondizione[0]);
    } else {
      elencoParametriFiltroUfficioIntestatario.removeAllElements();
      return null;
    }
  }

  private String[] parseListaValori(String listaValori, boolean noCaseSensitive) {

    Vector<String> listaValoriSeparati = null;
    // Ciclo per parsing della lista dei valori
    boolean apiceAperto = false;
    boolean continua = true;

    String[] strTmpArray = null;
    strTmpArray = listaValori.split(",");
    // se non ci sono gli apici è sicuramente una lista di valori di un
    // tabellato
    if (listaValori.indexOf("'") < 0) {
      for (int i = 0; i < strTmpArray.length; i++) {
        strTmpArray[i] = "'" + strTmpArray[i].trim() + "'";
      }
    }
    String tmp = null;
    int i = 0;
    listaValoriSeparati = new Vector<String>();

    while (i < strTmpArray.length - 1 && continua) {
      tmp = tmp == null ? strTmpArray[i] : tmp;
      if (tmp.trim().indexOf("'") == 0
          && (tmp.trim().lastIndexOf("'") == (tmp.trim().length() - 1))) {
        // Caso corretto
        listaValoriSeparati.addElement(tmp.trim());
        tmp = null;
        i++;
        if (apiceAperto) apiceAperto = false;
      } else {
        if (tmp.trim().indexOf("'") == 0) {
          // Caso in cui la stringa potrebbe avere una virgola al suo interno
          apiceAperto = true;
          tmp = tmp.concat(",").concat(strTmpArray[i + 1]);
          i++;
        } else {
          continua = false;
        }
      }
    }

    if (continua) {
      // Ultimo elemento dell'array
      tmp = tmp == null ? strTmpArray[i] : tmp;
      if (tmp.trim().indexOf("'") == 0
          && (tmp.trim().lastIndexOf("'") == (tmp.trim().length() - 1))) {
        // Caso corretto
        listaValoriSeparati.addElement(tmp.trim());
        tmp = null;
      } else {
        continua = false;
      }
    }
    if (continua) {
      String[] arrayValori = new String[listaValoriSeparati.size()];
      for (int l = 0; l < listaValoriSeparati.size(); l++) {
        arrayValori[l] = listaValoriSeparati.get(l);
        // tolgo gli apici alla stringa
        arrayValori[l] = arrayValori[l].substring(1,
            arrayValori[l].length() - 1);
        // nel caso di ricerca non sensibile a maiuscole/minuscole, converto i
        // valori in maiuscole per inserirli nella query
        if (noCaseSensitive) arrayValori[l] = arrayValori[l].toUpperCase();
      }
      return arrayValori;
    } else
      return null;
  }

  /**
   * In base alla tipologia dell'oggetto, si effettua l'opportuna conversione a
   * stringa applicando la funzione appropriata
   *
   * @param oggetto
   *        un oggetto di una qualche classe dei tipi fondamentali (
   *        String,Double,Integer,Date)
   *
   * @return oggetto convertito opportunamente a stringa
   */
  private String convertiOggettoAStringa(Object oggetto) {
    String risultato = null;

    if (oggetto != null) {

      if (oggetto instanceof java.util.Date) {
        risultato = UtilityDate.convertiData((java.util.Date) oggetto,
            UtilityDate.FORMATO_GG_MM_AAAA);
      } else if (oggetto instanceof Double) {
        risultato = UtilityNumeri.convertiDouble((Double) oggetto);
      } else if (oggetto instanceof Long) {
        risultato = UtilityNumeri.convertiIntero((Long) oggetto);
      } else if (oggetto instanceof Integer) {
        risultato = UtilityNumeri.convertiIntero((Integer) oggetto);
      } else {
        risultato = (String) oggetto;
      }
    }

    return risultato;
  }

  /**
   * Inserisce i valori dei parametri della ricerca in una cache per agevolare
   * l'inputazione da parte dell'utente all'atto dell'esecuzione, mantenendo
   * traccia degli ultimi valori inseriti in precedenza per la ricerca,
   * indipendentemente dal fatto che sia nella sessione di lavoro attuale oppure
   * no
   *
   * @param listaParametri
   *        elenco dei record da inserire
   * @param idAccount
   *        identificativo univoco dell'account
   * @param idRicerca
   *        identificativo univoco della ricerca
   */
  public void insertParametriEsecuzione(
      CacheParametroEsecuzione[] listaParametri, int idAccount, int idRicerca) {
    this.ricercheDao.deleteCacheParametriEsecuzione(idAccount, idRicerca);
    for (int i = 0; i < listaParametri.length; i++) {
      // prima di eseguire l'inserimento, forza i dati dell'associativa per
      // sicurezza e per ovviare a dover inserire preventivamente questi dati
      listaParametri[i].setIdAccount(idAccount);
      listaParametri[i].setIdRicerca(idRicerca);
      this.ricercheDao.insertCacheParametroEsecuzione(listaParametri[i]);
    }
  }

  public String getCacheParametroRicerca(int idAccount, int idRicerca,
      String codice) {
    return this.ricercheDao.getCacheParametroEsecuzione(idAccount, idRicerca,
        codice);
  }

  /**
   * Ritorna l'elenco dei soli parametri definiti in una ricerca
   *
   * @param idRicerca
   *        identificativo univoco della ricerca
   * @return lista di occorrenze della classe ParametroRicerca
   */
  public List<?> getParametriRicerca(int idRicerca) {
    return this.ricercheDao.getParametriRicercaByIdRicerca(idRicerca);
  }

  /**
   * Estrae il risultato del report sorgente dati e crea un file xml nella
   * cartella di composizione dei modelli
   *
   * @param datiModello
   * @param profiloUtente
   * @param codiceApplicazione
   * @param idApplicazione
   * @param ricercheManager
   * @param modelliManager
   *
   * @return nome del file sorgente dati xml per il modello da comporre
   *
   * @throws FactoryConfigurationError
   * @throws ParserConfigurationException
   * @throws SqlComposerException
   * @throws QueryDaoException
   * @throws FileNotFoundException
   * @throws TransformerConfigurationException
   * @throws TransformerFactoryConfigurationError
   * @throws TransformerException
   * @throws IOException
   */
  public String getFileXmlRisultatoReport(DatiModello datiModello, String codiceUfficioIntestatario,
      ProfiloUtente profiloUtente, String codiceApplicazione,
      String idApplicazione) throws FactoryConfigurationError,
      ParserConfigurationException, SqlComposerException, QueryDaoException,
      FileNotFoundException, TransformerConfigurationException,
      TransformerFactoryConfigurationError, TransformerException, IOException {
    if (logger.isDebugEnabled())
      logger.debug("getFileXmlRisultatoReport: inizio metodo");

    String valore = null;

    // con il risultato estratto, ed i parametri, si crea l'xml da inoltrare al
    // compositore
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    Document document = documentBuilder.newDocument();
    Element rootElement = document.createElement("dati");
    document.appendChild(rootElement);

    // si inseriscono i parametri utilizzabili dal modello
    List<?> parametriModello = this.prospettoManager.getParametriModello(datiModello.getIdModello());
    if (parametriModello != null) {
      ParametroModello parametroModello = null;
      Element elParametro = null;
      for (int i = 0; i < parametriModello.size(); i++) {
        parametroModello = (ParametroModello) parametriModello.get(i);

        valore = this.prospettoManager.getCacheParametroModello(
            profiloUtente.getId(), datiModello.getIdModello(),
            parametroModello.getCodice());
        // nel caso di parametri di tipo data, occorre effettuare un'opportuna
        // conversione rispetto al formato memorizzato in cache
        if ("D".equalsIgnoreCase(parametroModello.getTipo()))
          valore = StringUtils.replace(valore, ".", "/");

        elParametro = document.createElement("parametro");
        rootElement.appendChild(elParametro);
        elParametro.setAttribute("cod", parametroModello.getCodice());
        elParametro.setAttribute("tip", parametroModello.getTipo());
        if (valore != null) elParametro.setAttribute("val", valore);
      }
    }

    // si carica la ricerca
    ContenitoreDatiRicerca ricerca = this.getRicercaByIdRicerca(datiModello.getIdRicercaSrc().intValue());

    // si popola l'elenco dei parametri da inviare all manager della ricerca
    // riprendendoli dalla cache
    String[] parametriUtente = new String[ricerca.getNumeroParametri()];
    ParametroRicerca parametroRicerca = null;
    for (int i = 0; i < ricerca.getNumeroParametri(); i++) {
      parametroRicerca = ricerca.getElencoParametri().get(i);
      // si recupera l'ultimo valore indicato in cache
      valore = this.prospettoManager.getCacheParametroModello(
          profiloUtente.getId(), datiModello.getIdModello(),
          parametroRicerca.getCodice());
      if ("D".equalsIgnoreCase(parametroRicerca.getTipo()))
        valore = StringUtils.replace(valore, ".", "/");
      if ("F".equals(parametroRicerca.getTipo()))
        valore = StringUtils.replace(valore, ".", ",");
      parametriUtente[i] = valore;
    }

    // si calcola il risultato della ricerca
    DatiRisultato datiRisultato = this.getRisultatiRicerca(ricerca, codiceUfficioIntestatario,
        parametriUtente, profiloUtente, codiceApplicazione);

    // si inseriscono i dati reali
    RigaRisultato riga = null;
    ElementoRisultato cella = null;
    Element elRecord = null;
    Element elCampo = null;
    String tipo = null;
    String codiceTabellato = null;
    HashMap<String, List<Tabellato>> hashTabellati = new HashMap<String, List<Tabellato>>();
    for (int i = 0; i < datiRisultato.getNumeroRecordTotali(); i++) {
      riga = datiRisultato.getRigheRisultato().get(i);
      elRecord = document.createElement("record");
      rootElement.appendChild(elRecord);
      for (int j = 0; j < riga.getNumeroColonneRisultato(); j++) {
        cella = riga.getColonneRisultato().get(j);
        CampoRicerca campoRicerca = ricerca.getElencoCampi().get(
            j);
        Campo dizCampo = DizionarioCampi.getInstance().get(
            campoRicerca.getMneCampo());

        if (i == 0) {
          // nella prima riga si caricano tutti i tabellati usati
          if (dizCampo.getCodiceTabellato() != null
              && !hashTabellati.containsKey(dizCampo.getCodiceTabellato()))
            hashTabellati.put(dizCampo.getCodiceTabellato(),
                this.tabellatiDao.getTabellati(dizCampo.getCodiceTabellato()));
        }
        elCampo = document.createElement("campo");
        elRecord.appendChild(elCampo);
        elCampo.setAttribute("mne", campoRicerca.getMneCampo());
        valore = (String) cella.getValore();
        tipo = RicercheManager.convertiTipoDatoEstratto(dizCampo.getTipoColonna());
        if ("F".equals(tipo) && valore != null) {
          // traduce nel formato standard con il solo punto decimale
          valore = StringUtils.replace(valore, ".", "");
          valore = StringUtils.replace(valore, ",", ".");
        }
        if (dizCampo.getCodiceTabellato() != null && valore != null) {
          codiceTabellato = dizCampo.getCodiceTabellato();
          List<Tabellato> codifica = hashTabellati.get(codiceTabellato);
          boolean trovato = false;
          for (int z = 0; z < codifica.size() && !trovato; z++) {
            Tabellato tab = codifica.get(z);
            if (valore.equals(tab.getDescTabellato())) {
              elCampo.setAttribute("cod", tab.getTipoTabellato());
              trovato = true;
            }
          }
        }
        elCampo.setAttribute("val", valore);
        elCampo.setAttribute("tip", tipo);
        if (dizCampo.getDominio() != null)
          elCampo.setAttribute("dom", dizCampo.getDominio());
      }
    }

    // si scrive il file xml nella cartella di composizione modelli
    String nomeFileSorgenteDati = datiModello.getNomeFile().substring(0,
        datiModello.getNomeFile().lastIndexOf('.'))
        + "_"
        + new Date().getTime()
        + ".xml";
    String pathXml = ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI)
        + ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI_OUTPUT)
        + nomeFileSorgenteDati;
    File file = new File(pathXml);
    OutputStream outputStream = new FileOutputStream(file);
    Result result = new StreamResult(outputStream);
    Transformer xformer = TransformerFactory.newInstance().newTransformer();
    Source source = new DOMSource(document);
    xformer.transform(source, result);
    if (outputStream != null) {
      outputStream.close();
    }

    if (logger.isDebugEnabled())
      logger.debug("getFileXmlRisultatoReport: fine metodo");
    return nomeFileSorgenteDati;
  }

  private static String convertiTipoDatoEstratto(short tipo) {
    String risultato = null;
    switch (tipo) {
    case Campo.TIPO_DATA:
      risultato = "D";
      break;
    case Campo.TIPO_DECIMALE:
      risultato = "F";
      break;
    case Campo.TIPO_INTERO:
      risultato = "I";
      break;
    default:
      risultato = "S";
      break;
    }
    return risultato;
  }

  /**
   * Ritorna l'id ricerca avente il codice di pubblicazione nel WEB valorizzato
   * con il parametro in input
   *
   * @param codReportWS
   *        codice di pubblicazione report nel WEB
   * @return id della ricerca corrispondente al codice pubblicazione report nel
   *         WEB, null altrimenti
   */
  public Integer getIdRicercaByCodReportWS(String codReportWS) {
    return this.ricercheDao.getIdRicercaByCodReportWS(codReportWS);
  }

}