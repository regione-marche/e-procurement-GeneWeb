/*
 * Created on 08/mag/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.scadenz;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * Manager per la gestione dell'algoritmo di ricalcolo delle date sugli scadenzari (scadenzari e gantt) e per il trasferimento di
 * attivit&agrave; da scadenzario a modello scadenzario e viceversa.
 *
 * @author Stefano.Sabbadin
 * @since 2.0.4
 */
public class ScadenzariManager {

  /**
   * Elenco dei campi da estrarre per l'algoritmo di ricalcolo date su un'attivit&agrave;.
   */
  private static final String CAMPI_ALGORITMO_PREVISIONALE          = "ID, TIPOIN, DURATA, TIPOFI, FINEDOPO, IDATTIV, DATAFI, DATACONS, TIPOCONS, CODEVENTO";

  /**
   * Elenco dei campi da trasferire (a meno degli id, che servono per il mapping) da attivita' di un modello ad attivita' di uno scadenzario
   * e viceversa.
   */
  private static final String CAMPI_TRASFERIMENTO_ATTIVITA_MODELLO  = "ID, IDATTIV, TIT, DESCR, TIPOEV, TIPOIN, DURATA, DATAIN, TIPOFI, FINEDOPO, DATAFI, TIPOCONS, CODEVENTO, DATASCAD, GGPROMEM, REFPROMEM, DESTPROMEM, DISCR";

  /**
   * Elenco dei campi da trasferire (a meno degli id, che servono per il mapping) da una attivita' di partenza ad una attivita' di
   * destinazione.
   */
  private static final String CAMPI_TRASFERIMENTO_ATTIVITA_ATTIVITA = "ID, IDATTIV, TIT, DESCR, TIPOEV, TIPOIN, DURATA, DATAIN, TIPOFI, FINEDOPO, DATAFI, TIPOCONS, CODEVENTO, DATASCAD, GGPROMEM, REFPROMEM, DESTPROMEM, DISCR";

  /** Manager per l'interrogazione della base dati. */
  private SqlManager          sqlManager;

  /** Manager per la richiesta delle chiavi. */
  private GenChiaviManager    genChiaviManager;

  /**
   * @param sqlManager
   *        sqlManager da settare internamente alla classe.
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * @param genChiaviManager
   *        genChiaviManager da settare internamente alla classe.
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * Algoritmo di ricalcolo delle date di uno scadenzario.
   * <p>
   * Ricalcola, per tutte le attivit&agrave; di uno scadenzario di un'entit&agrave;, tutte le date previsionali e di consuntivo in base a
   * quanto specificato in input. Se idAttivita risulta non valorizzato, l'algoritmo si applica su tutte le attivit&agrave;, altrimenti si
   * applica su tutte le attivit&agrave; raggiungibili dall'attivit&agrave; in input (quindi nel suo sottoalbero).
   * </p>
   *
   * @param entita
   *        entit&agrave; a cui risulta collegato lo scadenzario
   * @param chiave
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario
   * @param codiceApplicazione
   *        codice applicazione
   * @param previsionale
   *        true=ricalcola sullo scadenzario salvato come previsionale, false=ricalcola sullo scadenzario effettivo
   * @param idAttivitaStart
   *        id da cui iniziare il ricalcolo, null se va ricalcolato su tutte le attivit&agrave; definite
   * @throws SQLException
   */
  public void updateDateScadenzarioEntita(String entita, Object[] chiave, String codiceApplicazione, boolean previsionale,
      Long idAttivitaStart) throws SQLException {
    Map<String, Date> mappaDateConsuntivo = new HashMap<String, Date>();
    Set<String> viewUtilizzate = new HashSet<String>();
    this.ricalcolaScadenzario(entita, chiave, codiceApplicazione, previsionale, idAttivitaStart, mappaDateConsuntivo, viewUtilizzate);
  }

  /**
   * Isolamento della parte ricorsiva dell'algoritmo.
   *
   * @param entita
   *        entit&agrave; a cui risulta collegato lo scadenzario
   * @param chiave
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario
   * @param codiceApplicazione
   *        codice applicazione
   * @param previsionale
   *        true=ricalcola sullo scadenzario salvato come previsionale, false=ricalcola sullo scadenzario effettivo
   * @param idAttivitaStart
   *        id da cui iniziare il ricalcolo, null se va ricalcolato su tutte le attivit&agrave; definite
   * @param mappaDateConsuntivo
   *        mappa popolata durante la ricorsione con i dati estratti dalle view usate per il consuntivo partendo dagli eventi
   * @param viewUtilizzate
   *        set delle vierw utilizzate per l'estrazione dei dati di consuntivo, in modo da migliorare le performance dell'algoritmo
   * @throws SQLException
   */
  private void ricalcolaScadenzario(String entita, Object[] chiave, String codiceApplicazione, boolean previsionale, Long idAttivitaStart,
      Map<String, Date> mappaDateConsuntivo, Set<String> viewUtilizzate) throws SQLException {
    boolean procedi = true;
    if (idAttivitaStart == null && !previsionale) {
      procedi = isScadenzarioConsuntivabile(entita, chiave, codiceApplicazione);
    }
    if (procedi) {
      List<Vector<JdbcParametro>> listaAttivita = getListaAttivita(entita, chiave, codiceApplicazione, previsionale, idAttivitaStart,
          CAMPI_ALGORITMO_PREVISIONALE);
      if (listaAttivita != null) {
        // si cicla sulle attivita' (ma dovrebbe sempre essere una sola, con certezza quando si entra per idAttivitaStart valorizzato)
        for (Vector<JdbcParametro> attivita : listaAttivita) {
          // si calcolano sempre le date previsionali (e la data scadenza se non esiste il consuntivo)
          this.ricalcolaPrevisionale(attivita);
          if (!previsionale) {
            // si calcola la data di consuntivo e la data scadenza
            this.ricalcolaConsuntivo(attivita, entita, chiave, mappaDateConsuntivo, viewUtilizzate);
          }
          // si estrae la lista attivita' dipendenti dall'attivita' appena processata
          List<Long> listaAttivitaSuccessive = this.getListaAttivitaSuccessive(attivita);
          // RICORSIONE: si riapplica l'algorimo ad ogni attivita' rilevata (si scende nel sottoalbero delle attivita' di un livello)
          // altrimenti si termina perche' si e' arrivati al livello piu' basso dell'albero (foglie)
          for (Long idAttivitaSuccessiva : listaAttivitaSuccessive) {
            this.ricalcolaScadenzario(entita, chiave, codiceApplicazione, previsionale, idAttivitaSuccessiva, mappaDateConsuntivo,
                viewUtilizzate);
          }
        }
      }
    }
  }

  /**
   * Verifica la presenza di almeno un'attivit&agrave; che deve essere consuntivata causa mancanza della data consuntivo. Se tutte le
   * attivit&agrave; risultano consuntivate, allora l'algoritmo non effettuer&agrave; nulla.
   *
   * @param entita
   *        entit&agrave; a cui risulta collegato lo scadenzario
   * @param chiave
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario
   * @param codiceApplicazione
   *        codice applicazione
   *
   * @return true se si necessita il ricalcolo di consuntivi, false se tutte le attivit&agrave; risultano consuntivate per cui lo
   *         scadenzario &egrave; di fatto chiuso
   * @throws SQLException
   */
  private boolean isScadenzarioConsuntivabile(String entita, Object[] chiave, String codiceApplicazione) throws SQLException {
    StringBuilder sb = new StringBuilder("SELECT COUNT(1) FROM G_SCADENZ WHERE DATACONS IS NULL AND PRG=? AND PREV=? AND ENT=?");
    Object[] params = null;
    // si considerano tutte le attivita' che non hanno riferimenti ad altra attivita', considerando entita' e record di collegamento
    Tabella tabella = DizionarioTabelle.getInstance().getDaNomeTabella(entita);
    params = new Object[tabella.getCampiKey().size() + 3];
    params[0] = codiceApplicazione;
    params[1] = 0;
    params[2] = entita;
    for (int j = 0; j < tabella.getCampiKey().size(); j++) {
      sb.append(" AND KEY").append(j + 1).append("=?");
      params[3 + j] = chiave[j];
    }
    Long numeroAttivitaDaConsuntivare = (Long) this.sqlManager.getObject(sb.toString(), params);
    return numeroAttivitaDaConsuntivare > 0;
  }

  /**
   * Estrae l'elenco delle attivit&agrave; a partire dagli input.
   *
   * @param entita
   *        entit&agrave; a cui risulta collegato lo scadenzario
   * @param chiave
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario
   * @param codiceApplicazione
   *        codice applicazione
   * @param previsionale
   *        true=ricalcola sullo scadenzario salvato come previsionale, false=ricalcola sullo scadenzario effettivo
   * @param idAttivitaStart
   *        id da cui iniziare il ricalcolo, null se va ricalcolato su tutte le attivit&agrave; definite
   *
   * @return lista delle attivit&agrave; da analizzare
   * @throws SQLException
   */
  private List<Vector<JdbcParametro>> getListaAttivita(String entita, Object[] chiave, String codiceApplicazione, boolean previsionale,
      Long idAttivitaStart, String campi) throws SQLException {
    StringBuilder sb = new StringBuilder("SELECT ").append(campi).append(" FROM G_SCADENZ WHERE ");
    Object[] params = null;
    if (idAttivitaStart != null) {
      // si parte dall'attivita' in input
      sb.append("ID=?");
      params = new Object[] {idAttivitaStart };
    } else {
      // si considerano tutte le attivita' che non hanno riferimenti ad altra attivita', considerando entita' e record di collegamento
      sb.append("(IDATTIV IS NULL OR IDATTIV=0) AND PRG=? AND PREV=? AND ENT=?");
      Tabella tabella = DizionarioTabelle.getInstance().getDaNomeTabella(entita);
      params = new Object[tabella.getCampiKey().size() + 3];
      params[0] = codiceApplicazione;
      params[1] = previsionale ? 1 : 0;
      params[2] = entita;
      for (int j = 0; j < tabella.getCampiKey().size(); j++) {
        sb.append(" AND KEY").append(j + 1).append("=?");
        params[3 + j] = chiave[j];
      }
    }
    sb.append(" ORDER BY DATASCAD ASC");
    @SuppressWarnings("unchecked")
    List<Vector<JdbcParametro>> listaAttivita = this.sqlManager.getListVector(sb.toString(), params);
    return listaAttivita;
  }

  /**
   * Algoritmo per il ricalcolo delle date previsionali (data inizio e data fine, nonch&agrave; data scadenza se il consuntivo non
   * valorizzata) sull'attivit&agrave; in input.
   *
   * @param attivita
   *        attivit&agrave; da ricalcolare
   * @throws SQLException
   */
  private void ricalcolaPrevisionale(Vector<JdbcParametro> attivita) throws SQLException {
    Date dataPrevistaScadenzaRicalcolata = null;
    Date dataScadenzaAggiornataRicalcolata = null;
    
    try {
      // ID, TIPOIN, DURATA, TIPOFI, FINEDOPO, IDATTIV, DATAFI, DATACONS, TIPOCONS, CODEVENTO
      Long idAttivita = SqlManager.getValueFromVectorParam(attivita, 0).longValue();
      Long tipoInizio = SqlManager.getValueFromVectorParam(attivita, 1).longValue();
      Long durataGiorni = SqlManager.getValueFromVectorParam(attivita, 2).longValue();
      Long tipoFine = SqlManager.getValueFromVectorParam(attivita, 3).longValue();
      Long termineDopoGiorni = SqlManager.getValueFromVectorParam(attivita, 4).longValue();
      Long idAttivitaPrecedente = SqlManager.getValueFromVectorParam(attivita, 5).longValue();
      Date dataScadenzaPrevistaAttuale = (Date) SqlManager.getValueFromVectorParam(attivita, 6).getValue();
      Date dataScadenzaEffettiva = (Date) SqlManager.getValueFromVectorParam(attivita, 7).getValue();
      
      if (tipoFine == 2) {
        // Se tipoFine e' pari a 2 significa che le date di scadenza (prevista e aggiornata) dipendono 
        // da una attivita' precedente. In questo caso la data di scadenza prevista (G_SCADENZ.DATAFI) deve essere
        // calcolata sulla base della data di scadenza prevista dell'attivita' precedente.
        // Anche la data di scadenza aggiornata (G_SCADENZ.DATASCAD) deve essere aggiornata sulla 
        // della data di scadenza aggiornata dell'attivita' precedente.
        
        // Gestione e calcolo della data di scadenza prevista
        Date dataPrevistaScadenzaPrecedente = (Date) this.sqlManager.getObject("SELECT DATAFI FROM G_SCADENZ WHERE ID=?",
            new Long[] {idAttivitaPrecedente });
        dataPrevistaScadenzaRicalcolata = DateUtils.addDays(dataPrevistaScadenzaPrecedente, termineDopoGiorni.intValue());
        
        // Gestione e calcolo della data di scadenza aggioranta
        Date dataScadenzaAggiornataPrecedente = (Date) this.sqlManager.getObject("SELECT DATASCAD FROM G_SCADENZ WHERE ID=?",
            new Long[] {idAttivitaPrecedente });
        dataScadenzaAggiornataRicalcolata = DateUtils.addDays(dataScadenzaAggiornataPrecedente, termineDopoGiorni.intValue());

      } else {
        // In questo la data dell'attività e' definita manualmente (data fissa) e non
        // dipende da alcuna attivita' precedente.
        dataPrevistaScadenzaRicalcolata = dataScadenzaPrevistaAttuale;
        dataScadenzaAggiornataRicalcolata = dataScadenzaPrevistaAttuale;
      }
      
      // La data consutivo ha priorita', se esiste sulla data di scadenza aggiornata.
      if (dataScadenzaEffettiva != null) {
        dataScadenzaAggiornataRicalcolata = dataScadenzaEffettiva;
      }

      // si arriva qui con tutte e 3 le date ricalcolate, e si procede all'aggiornamento
      this.sqlManager.update("UPDATE G_SCADENZ SET DATAIN=?, DATAFI=?, DATASCAD=? WHERE ID=?", new Object[] {dataPrevistaScadenzaRicalcolata, dataPrevistaScadenzaRicalcolata,
          dataScadenzaAggiornataRicalcolata, idAttivita });

    } catch (GestoreException e) {
      throw new RuntimeException("Errore inaspettato durante la lettura dei dati di G_SCADENZ", e);
    }
  }

  /**
   * Algoritmo per il ricalcolo della data consuntivo e di conseguenza della data scadenza sull'attivit&agrave; in input.
   *
   * @param attivita
   *        attivit&agrave; da ricalcolare
   * @param entita
   *        entit&agrave; a cui risulta collegato lo scadenzario
   * @param chiave
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario
   * @param mappaDateConsuntivo
   *        mappa popolata durante la ricorsione con i dati estratti dalle view usate per il consuntivo partendo dagli eventi
   * @param viewUtilizzate
   *        set delle vierw utilizzate per l'estrazione dei dati di consuntivo, in modo da migliorare le performance dell'algoritmo
   * @throws SQLException
   */
  private void ricalcolaConsuntivo(Vector<JdbcParametro> attivita, String entita, Object[] chiave, Map<String, Date> mappaDateConsuntivo,
      Set<String> viewUtilizzate) throws SQLException {
    Date dataConsuntivoRicalcolata = null;

    try {
      Long idAttivita = SqlManager.getValueFromVectorParam(attivita, 0).longValue();
      Long tipoConsuntivo = SqlManager.getValueFromVectorParam(attivita, 8).longValue();
      Date dataConsuntivo = (Date) SqlManager.getValueFromVectorParam(attivita, 7).getValue();
      String codEvento = SqlManager.getValueFromVectorParam(attivita, 9).stringValue();

      if (tipoConsuntivo == 2) {
        // data consuntivo e' calcolata sulla base di un evento
        // devo quindi estrarre la data consuntivo specifica, per fare questo verifico se ho gia' letto dati dalla view in cui devo reperire
        // tale data, e se non e' ancora mai stata usata ne leggo i dati e carico nella mappa (cache)
        String view = (String) this.sqlManager.getObject("SELECT FROMVIEW FROM G_EVENTISCADENZ WHERE COD=?", new String[] {codEvento });
        if (!viewUtilizzate.contains(view)) {
          this.popolaCache(view, entita, chiave, mappaDateConsuntivo, viewUtilizzate);
        }
        // a questo punto dalla cache leggo l'eventuale data consuntivo
        dataConsuntivoRicalcolata = mappaDateConsuntivo.get(codEvento);
        if (dataConsuntivoRicalcolata == null) {
          // non ho ancora una data consuntivo
          this.sqlManager.update("UPDATE G_SCADENZ SET DATACONS= ? WHERE ID=?", new Object[] {dataConsuntivoRicalcolata,
              idAttivita });
        } else {
          // ho la data consuntivo, pertanto con lo stesso valore aggiorno anche la scadenza
          this.sqlManager.update("UPDATE G_SCADENZ SET DATACONS=?, DATASCAD=? WHERE ID=?", new Object[] {dataConsuntivoRicalcolata,
              dataConsuntivoRicalcolata, idAttivita });
        }

      } else {
        // data consuntivo va editata a mano, per cui va gia' bene
        // riaggiorno solo data scadenza per il fatto che potrei aver appena consuntivato a mano per cui devo rigenerarne il valore
        if (dataConsuntivo != null) {
          this.sqlManager.update("UPDATE G_SCADENZ SET DATASCAD=DATACONS WHERE ID=?", new Object[] {idAttivita });
        }
      }

    } catch (GestoreException e) {
      throw new RuntimeException("Errore inaspettato durante la lettura dei dati di G_SCADENZ", e);
    }
  }

  /**
   * Inserisce nella mappaDateConsuntivo, che funge da cache, le date consuntivo estraibili dalla view per il record in input.
   *
   * @param view
   *        nome della view da interrogare
   * @param entita
   *        entit&agrave; di partenza, dalla quale ricavare i campi chiave di filtro da usare nella view
   * @param chiave
   *        chiave per il filtro sulla view
   * @param mappaDateConsuntivo
   *        cache da popolare con i dati estratti
   * @param viewUtilizzate
   *        set delle view interrogate
   * @throws SQLException
   */
  private void popolaCache(String view, String entita, Object[] chiave, Map<String, Date> mappaDateConsuntivo, Set<String> viewUtilizzate)
      throws SQLException {
    // costruisco la query sulla view filtrando dinamicamente sulla chiave dell'entita' a cui sono riferiti i dati prelevati
    Tabella tabella = DizionarioTabelle.getInstance().getDaNomeTabella(entita);
    List<Campo> campiChiave = tabella.getCampiKey();
    StringBuilder sb = new StringBuilder("SELECT CODEVENTO, DATACONSUNTIVO FROM ").append(view).append(" WHERE ");
    for (Campo campo : campiChiave) {
      sb.append(campo.getNomeCampo()).append("=? ");
    }
    // eseguo la query per estrarre tutti i consuntivi estraibili nella view per il record in input
    @SuppressWarnings("unchecked")
    List<Vector<JdbcParametro>> listaConsuntivi = this.sqlManager.getListVector(sb.toString(), chiave);
    // popolo la cache con tutti i consuntivi estratti
    for (Vector<JdbcParametro> record : listaConsuntivi) {
      try {
        String codEvento = SqlManager.getValueFromVectorParam(record, 0).stringValue();
        Date dataConsuntivo = SqlManager.getValueFromVectorParam(record, 1).dataValue();
        mappaDateConsuntivo.put(codEvento, dataConsuntivo);
      } catch (GestoreException e) {
        throw new RuntimeException("Errore inaspettato durante la lettura dei dati di consuntivo scadenzario nella view " + view, e);
      }
    }
    // marco la view come utilizzata (quindi in futuro per lo stesso record non devo effettuare nuovamente query)
    viewUtilizzate.add(view);
  }

  /**
   * Estrae, a partire dall'attivit&agrave; corrente, l'elenco delle chiavi di attivit&agrave; che dipendono dalla corrente.
   *
   * @param attivita
   *        attivit&agrave; da cui cercare le dipendenze verso essa
   * @return elenco delle chiavi delle attivit&agrave; figlie/successive all'attivit&agrave; corrente
   * @throws SQLException
   */
  private List<Long> getListaAttivitaSuccessive(Vector<JdbcParametro> attivita) throws SQLException {
    List<Long> lista = new ArrayList<Long>();
    try {
      Long idAttivita = SqlManager.getValueFromVectorParam(attivita, 0).longValue();
      @SuppressWarnings("unchecked")
      List<Vector<JdbcParametro>> listaAttivita = this.sqlManager.getListVector("SELECT ID FROM G_SCADENZ WHERE IDATTIV=?",
          new Long[] {idAttivita });
      // popolo la cache con tutti i consuntivi estratti
      for (Vector<JdbcParametro> record : listaAttivita) {
        Long id = SqlManager.getValueFromVectorParam(record, 0).longValue();
        lista.add(id);
      }
    } catch (GestoreException e) {
      throw new RuntimeException("Errore inaspettato durante la lettura dei dati di G_SCADENZ", e);
    }
    return lista;
  }

  /**
   * Algoritmo per la copia delle attivit&agrave; presenti in un modello all'interno di uno scadenzario.
   *
   * @param codiceModello
   *        modello da cui prelevare le attivit&agrave;
   * @param entita
   *        entit&agrave; di riferimento del modello e dello scadenzario
   * @param chiave
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario
   * @param codiceApplicazione
   *        codice applicazione
   * @throws SQLException
   */
  public void insertAttivitaScadenzarioDaModello(String codiceModello, String entita, Object[] chiave, String codiceApplicazione)
      throws SQLException {
    Map<Long, Long> mappaIdConversione = new HashMap<Long, Long>();
    this.clonaAttivitaScadenzarioDaModello(codiceModello, null, entita, chiave, codiceApplicazione, mappaIdConversione);
  }

  /**
   * Isolamento della parte ricorsiva dell'algoritmo di copia attivit&agrave; presenti in un modello all'interno di uno scadenzario.
   *
   * @param codiceModello
   *        modello da cui prelevare le attivit&agrave;
   * @param idAttivitaModelloStart
   *        id da cui iniziare il ricalcolo, null se va ricalcolato su tutte le attivit&agrave; definite
   * @param entita
   *        entit&agrave; di riferimento del modello e dello scadenzario
   * @param chiave
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario
   * @param codiceApplicazione
   *        codice applicazione
   * @param mappaIdConversione
   *        mapping delle attivit&agrave; inserite nello scadenzario con le corrispondenti attivit&agrave; presenti nel modello
   * @throws SQLException
   */
  private void clonaAttivitaScadenzarioDaModello(String codiceModello, Long idAttivitaModelloStart, String entita, Object[] chiave,
      String codiceApplicazione, Map<Long, Long> mappaIdConversione) throws SQLException {
    // select attivita con idattiv=null o l'id in input
    List<Vector<JdbcParametro>> listaAttivita = getListaAttivitaModello(codiceModello, idAttivitaModelloStart);

    // ciclo su ognuna e inserisco le attivita' popolando una mappa di chiavi
    // richiamo ricorsivamente l'algoritmo sui figli
    if (listaAttivita != null) {
      // si cicla sulle attivita'
      for (Vector<JdbcParametro> attivita : listaAttivita) {
        // si crea l'attivita' sullo scadenzario
        this.clonaAttivitaSuScadenzario(attivita, entita, chiave, codiceApplicazione, false, false, mappaIdConversione);
        // si estrae la lista attivita' dipendenti dall'attivita' appena processata
        List<Long> listaAttivitaSuccessive = this.getListaAttivitaModelloSuccessive(codiceModello, attivita);
        // RICORSIONE: si riapplica l'algorimo ad ogni attivita' rilevata (si scende nel sottoalbero delle attivita' di un livello)
        // altrimenti si termina perche' si e' arrivati al livello piu' basso dell'albero (foglie)
        for (Long idAttivitaSuccessiva : listaAttivitaSuccessive) {
          this.clonaAttivitaScadenzarioDaModello(codiceModello, idAttivitaSuccessiva, entita, chiave, codiceApplicazione,
              mappaIdConversione);
        }
      }
    }
  }

  /**
   *
   * Estrae l'elenco delle attivit&agrave; del modello a partire dagli input
   *
   * @param codiceModello
   *        modello da cui prelevare le attivit&agrave;
   * @param idAttivitaModelloStart
   *        id da cui iniziare il ricalcolo, null se va ricalcolato su tutte le attivit&agrave; definite
   * @return elenco delle attivit&agrave; prese dal modello, con idattivit&agrave; null o id pari al parametro in input
   * @return lista delle attivit&agrave; del modello da analizzare
   * @throws SQLException
   */
  private List<Vector<JdbcParametro>> getListaAttivitaModello(String codiceModello, Long idAttivitaModelloStart) throws SQLException {
    StringBuilder sb = new StringBuilder("SELECT " + CAMPI_TRASFERIMENTO_ATTIVITA_MODELLO + " FROM G_DETTMODSCADENZ WHERE COD=? AND ");
    Object[] params = null;
    if (idAttivitaModelloStart != null) {
      // si parte dall'attivita' in input
      sb.append("ID=?");
      params = new Object[] {codiceModello, idAttivitaModelloStart };
    } else {
      // si considerano tutte le attivita' che non hanno riferimenti ad altra attivita', considerando entita' e record di collegamento
      sb.append("(IDATTIV IS NULL OR IDATTIV=0)");
      params = new Object[] {codiceModello };
    }
    sb.append(" ORDER BY DATASCAD ASC");
    @SuppressWarnings("unchecked")
    List<Vector<JdbcParametro>> listaAttivita = this.sqlManager.getListVector(sb.toString(), params);
    return listaAttivita;
  }

  /**
   * Inserisce l'occorrenza dell'attivit&agrave; in input presa dal modello nella G_SCADENZ collegandola all'entit&agrave; identificata
   * dalla chiave in input.
   *
   * @param attivita
   *        attivit&agrave; da clonare
   * @param entita
   *        entit&agrave; di partenza
   * @param chiave
   *        chiave del record dell'entit&agrave; a cui collegare l'occorrenza di scadenzario
   * @param codiceApplicazione
   *        codice applicazione
   * @param previsionale
   *        true=clona come previsionale, false=clona come scadenzario effettivo
   * @param clonaAttivitaDaAttivita
   *        true=clona la attivita da un'altra attivita, false=clona l'attivita da modello
   * @param mappaIdConversione
   *        mappa di appoggio per la ricorsione per il mapping delle referenze tra attivit&agrave; (campi ID e IDATTIV)
   * @throws SQLException
   */
  private void clonaAttivitaSuScadenzario(Vector<JdbcParametro> attivita, String entita, Object[] chiave, String codiceApplicazione,
      boolean previsionale, boolean clonaAttivitaDaAttivita, Map<Long, Long> mappaIdConversione) throws SQLException {
    // fisso la stringa di insert (attenzione: i campi prima sono quelli della select sull'attivita' modello, quindi i campi fissi in base
    // al record), mentre la prima parte del values e' calcolata dinamicamente sui campi presenti nei campi fissi usati nella select
    StringBuilder sb = new StringBuilder("INSERT INTO G_SCADENZ (");
    if (clonaAttivitaDaAttivita)
      sb.append(CAMPI_TRASFERIMENTO_ATTIVITA_ATTIVITA);
    else
      sb.append(CAMPI_TRASFERIMENTO_ATTIVITA_MODELLO);
    sb.append(", PREV, PRG, ENT, KEY1, KEY2, KEY3, KEY4, KEY5)");

    sb.append(" VALUES (");
    if (clonaAttivitaDaAttivita)
      sb.append(StringUtils.repeat("?, ", StringUtils.split(CAMPI_TRASFERIMENTO_ATTIVITA_ATTIVITA, ",").length));
    else
      sb.append(StringUtils.repeat("?, ", StringUtils.split(CAMPI_TRASFERIMENTO_ATTIVITA_MODELLO, ",").length));

    sb.append("?, ?, ?, ?, ?, ?, ?, ?)");
    // popolo i parametri: prima quelli della select, poi quelli fissi, poi la chiave
    Object[] params = new Object[attivita.size() + 8]; // 26
    int i = 0;
    for (; i < attivita.size(); i++) {// 18
      params[i] = SqlManager.getValueFromVectorParam(attivita, i).getValue();
    }
    params[i++] = previsionale ? 1 : 0; // PREV
    params[i++] = codiceApplicazione; // PRG
    params[i++] = entita;
    for (Object valoreChiave : chiave) {// KEYx
      params[i++] = valoreChiave;
    }
    // aggiorno l'id attivita' riferita se valorizzata (in tal caso l'ho considerata, inserita e rimappata in precedenza mediante
    // ricorsione)
    if (params[1] != null) {
      params[1] = mappaIdConversione.get(params[1]);
    }
    // calcolo l'id per l'inserimento e aggiorno la mappa ed il parametro per la insert
    int id = this.genChiaviManager.getMaxId("G_SCADENZ", "ID") + 1;
    mappaIdConversione.put((Long) params[0], new Long(id));
    params[0] = id;
    // creo il record
    this.sqlManager.update(sb.toString(), params);
  }

  /**
   * Estrae, a partire dall'attivit&agrave; corrente, l'elenco delle chiavi di attivit&agrave; che dipendono dalla corrente nel medesimo
   * modello.
   *
   * @param codiceModello
   *        codice del modello
   * @param attivita
   *        attivit&agrave; da cui cercare le dipendenze verso essa
   * @return elenco delle chiavi delle attivit&agrave; figlie/successive all'attivit&agrave; corrente
   * @throws SQLException
   */
  private List<Long> getListaAttivitaModelloSuccessive(String codiceModello, Vector<JdbcParametro> attivita) throws SQLException {
    List<Long> lista = new ArrayList<Long>();
    try {
      Long idAttivita = SqlManager.getValueFromVectorParam(attivita, 0).longValue();
      @SuppressWarnings("unchecked")
      List<Vector<JdbcParametro>> listaAttivita = this.sqlManager.getListVector(
          "SELECT ID FROM G_DETTMODSCADENZ WHERE COD=? AND IDATTIV=?", new Object[] {codiceModello, idAttivita });
      // popolo la cache con tutti i consuntivi estratti
      for (Vector<JdbcParametro> record : listaAttivita) {
        Long id = SqlManager.getValueFromVectorParam(record, 0).longValue();
        lista.add(id);
      }
    } catch (GestoreException e) {
      throw new RuntimeException("Errore inaspettato durante la lettura dei dati di G_DETTMODSCADENZ", e);
    }
    return lista;
  }

  /**
   * Algoritmo per la copia delle attivit&agrave; presenti in uno scadenzario all'interno di un modello.
   *
   * @param entita
   *        entit&agrave; a cui risulta collegato lo scadenzario
   * @param chiave
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario
   * @param codiceApplicazione
   *        codice applicazione
   * @param codiceModello
   *        modello da cui prelevare le attivit&agrave;
   * @throws SQLException
   */
  public void insertAttivitaModelloDaScadenzario(String entita, Object[] chiave, String codiceApplicazione, String codiceModello)
      throws SQLException {
    Map<Long, Long> mappaIdConversione = new HashMap<Long, Long>();
    this.clonaAttivitaModelloDaScadenzario(null, entita, chiave, codiceApplicazione, codiceModello, mappaIdConversione);
  }

  /**
   * Isolamento della parte ricorsiva dell'algoritmo di copia attivit&agrave; presenti in uno scadenzario all'interno di un modello.
   *
   * @param idAttivitaScadenzarioStart
   *        id da cui iniziare il ricalcolo, null se va ricalcolato su tutte le attivit&agrave; definite
   * @param entita
   *        entit&agrave; di riferimento del modello e dello scadenzario
   * @param chiave
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario
   * @param codiceApplicazione
   *        codice applicazione
   * @param codiceModello
   *        modello in cui inserire le attivit&agrave;
   * @param mappaIdConversione
   *        mapping delle attivit&agrave; inserite nel modello con le corrispondenti attivit&agrave; presenti nello scadenzario
   * @throws SQLException
   */
  private void clonaAttivitaModelloDaScadenzario(Long idAttivitaScadenzarioStart, String entita, Object[] chiave,
      String codiceApplicazione, String codiceModello, Map<Long, Long> mappaIdConversione) throws SQLException {
    // select attivita con idattiv=null o l'id in input
    List<Vector<JdbcParametro>> listaAttivita = getListaAttivita(entita, chiave, codiceApplicazione, false, idAttivitaScadenzarioStart,
        CAMPI_TRASFERIMENTO_ATTIVITA_MODELLO);

    // ciclo su ognuna e inserisco le attivita' popolando una mappa di chiavi
    // richiamo ricorsivamente l'algoritmo sui figli
    if (listaAttivita != null) {
      // si cicla sulle attivita'
      for (Vector<JdbcParametro> attivita : listaAttivita) {
        // si crea l'attivita' sullo scadenzario
        this.clonaAttivitaSuModello(attivita, codiceModello, mappaIdConversione);
        // si estrae la lista attivita' dipendenti dall'attivita' appena processata
        List<Long> listaAttivitaSuccessive = this.getListaAttivitaSuccessive(attivita);
        // RICORSIONE: si riapplica l'algorimo ad ogni attivita' rilevata (si scende nel sottoalbero delle attivita' di un livello)
        // altrimenti si termina perche' si e' arrivati al livello piu' basso dell'albero (foglie)
        for (Long idAttivitaSuccessiva : listaAttivitaSuccessive) {
          this.clonaAttivitaModelloDaScadenzario(idAttivitaSuccessiva, entita, chiave, codiceApplicazione, codiceModello,
              mappaIdConversione);
        }
      }
    }
  }

  /**
   * Inserisce l'occorrenza dell'attivit&agrave; in input presa da G_SCADENZ nel modello in G_DETTMODSCADENZ collegandola al modello in
   * input.
   *
   * @param attivita
   *        attivit&agrave; da clonare
   * @param codiceModello
   *        codice del modello in cui inserire l'attivit&agrave;
   * @param mappaIdConversione
   *        mappa di appoggio per la ricorsione per il mapping delle referenze tra attivit&agrave; (campi ID e IDATTIV)
   * @throws SQLException
   */
  private void clonaAttivitaSuModello(Vector<JdbcParametro> attivita, String codiceModello, Map<Long, Long> mappaIdConversione)
      throws SQLException {
    // fisso la stringa di insert (attenzione: i campi prima sono quelli della select sull'attivita' modello, quindi i campi fissi in base
    // al record), mentre la prima parte del values e' calcolata dinamicamente sui campi presenti nei campi fissi usati nella select
    StringBuilder sb = new StringBuilder("INSERT INTO G_DETTMODSCADENZ (").append(CAMPI_TRASFERIMENTO_ATTIVITA_MODELLO).append(
        ", PREV, COD)");
    sb.append(" VALUES (");
    sb.append(StringUtils.repeat("?, ", StringUtils.split(CAMPI_TRASFERIMENTO_ATTIVITA_MODELLO, ",").length));
    sb.append("?, ?)");
    // popolo i parametri: prima quelli della select, poi quelli fissi, poi la chiave
    Object[] params = new Object[attivita.size() + 2]; // 20
    int i = 0;
    for (; i < attivita.size(); i++) { // 18
      params[i] = SqlManager.getValueFromVectorParam(attivita, i).getValue();
    }
    params[i++] = 0; // PREV
    params[i++] = codiceModello; // COD
    // aggiorno l'id attivita' riferita se valorizzata (in tal caso l'ho considerata, inserita e rimappata in precedenza mediante
    // ricorsione)
    if (params[1] != null) {
      params[1] = mappaIdConversione.get(params[1]);
    }
    // calcolo l'id per l'inserimento (contatore progressivo di attivita' sul modello) e aggiorno la mappa ed il parametro per la insert
    int id = mappaIdConversione.size() + 1;
    mappaIdConversione.put((Long) params[0], new Long(id));
    params[0] = id;
    // creo il record
    this.sqlManager.update(sb.toString(), params);
  }

  /**
   * Estrae, a partire dall'attivit&agrave; corrente, l'elenco delle attivit&agrave; sul medesimo scadenzario che possono essere
   * referenziate come attivit&agrave; da cui dipendere. Da tale set di attivit&agrave; si escludono l'attivit&agrave; stessa e tutti gli
   * elementi presenti nel sottoalbero individuati da tale nodo.
   *
   * @param codiceApplicazione
   *        codice applicazione
   * @param entita
   *        entit&agrave; di riferimento del modello e dello scadenzario
   * @param chiave
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario
   * @param idAttivita
   *        attivit&agrave; da cui partire a reperire i nodi referenziabili, null nel caso di inserimento di nuova attivit&agrave;
   * @return lista delle attivit&agrave; referenziabili, costituite da un codice e da una descrizione
   * @throws SQLException
   */
  public List<Tabellato> getAttivitaValideReferenziabili(String codiceApplicazione, String entita, Object[] chiavi, Long idAttivita)
      throws SQLException {
    Set<Long> listaId = new HashSet<Long>();
    // si estrae l'elenco degli id attivita' validi e quindi referenziabili per l'attivita' in input
    this.getListaIdAttivitaReferenziabili(null, codiceApplicazione, entita, chiavi, idAttivita, listaId);
    // a partire dagli id, si estraggono le attivita' per il risultato finale
    List<Tabellato> lista = this.getAttivitaValideReferenziabili(listaId);
    return lista;
  }

  /**
   * Isolamento della parte ricorsiva dell'algoritmo di estrazione degli id attivit&agrave; a partire dal record in input o considerando
   * tutti i record dello scadenzario
   *
   * @param idAttivitaAttuale
   *        attivit&agrave; considerata nel seguente step dell'algorimo ricorsivo; in sua assenza usa i filtri su per reperire occorrenze in
   *        G_SCADENZ
   * @param codiceApplicazione
   *        codice applicazione
   * @param entita
   *        entit&agrave; di riferimento del modello e dello scadenzario
   * @param chiave
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario
   * @param idAttivita
   *        attivit&agrave; da cui partire a reperire i nodi referenziabili, null nel caso di inserimento di nuova attivit&agrave;
   * @return lista degli id attivit&agrave; referenziabili
   * @throws SQLException
   */
  private void getListaIdAttivitaReferenziabili(Long idAttivitaAttuale, String codiceApplicazione, String entita, Object[] chiavi,
      Long idAttivita, Set<Long> listaId) throws SQLException {
    List<Vector<JdbcParametro>> listaAttivita = this.getListaAttivita(entita, chiavi, codiceApplicazione, false, idAttivitaAttuale, "ID");

    // ciclo su ognuna ed aggiorno il set di id attivita'
    // richiamo ricorsivamente l'algoritmo sui figli
    if (listaAttivita != null) {
      // si cicla sulle attivita'
      for (Vector<JdbcParametro> attivita : listaAttivita) {
        try {
          Long idAttivitaEstratto = SqlManager.getValueFromVectorParam(attivita, 0).longValue();
          if (idAttivita == null || !idAttivitaEstratto.equals(idAttivita)) {
            // considero l'elemento estratto solo se non e' idAttivita, perche' arrivato a tale nodo l'algoritmo si interrompe
            listaId.add(idAttivitaEstratto);
            // si estrae la lista attivita' dipendenti dall'attivita' appena processata
            List<Long> listaAttivitaSuccessive = this.getListaAttivitaSuccessive(attivita);
            // RICORSIONE: si riapplica l'algorimo ad ogni attivita' rilevata (si scende nel sottoalbero delle attivita' di un livello)
            // altrimenti si termina perche' si e' arrivati al livello piu' basso dell'albero (foglie)
            for (Long idAttivitaSuccessiva : listaAttivitaSuccessive) {
              this.getListaIdAttivitaReferenziabili(idAttivitaSuccessiva, codiceApplicazione, entita, chiavi, idAttivita, listaId);
            }
          }
        } catch (GestoreException e) {
          throw new RuntimeException("Errore inaspettato durante la lettura dei dati di G_SCADENZ", e);
        }
      }
    }
  }

  /**
   * Genera l'elenco delle attivit&agrave; a partire dall'elenco degli id delle stesse da leggere.
   *
   * @param listaId
   *        set di id attivit&agrave; da leggere
   * @return elenco delle coppie (id, titolo) ordinate per titolo
   * @throws SQLException
   */
  private List<Tabellato> getAttivitaValideReferenziabili(Set<Long> listaId) throws SQLException {
    List<Tabellato> lista = new ArrayList<Tabellato>();
    if (listaId.size() > 0) {
      StringBuilder sb = new StringBuilder("SELECT ID, TIT FROM G_SCADENZ WHERE ID IN (");
      sb.append(StringUtils.repeat("?, ", listaId.size() - 1));
      sb.append("?) ORDER BY TIT ASC");
      @SuppressWarnings("unchecked")
      List<Vector<JdbcParametro>> listaAttivita = this.sqlManager.getListVector(sb.toString(), listaId.toArray());
      for (Vector<JdbcParametro> attivita : listaAttivita) {
        Tabellato tab = new Tabellato();
        try {
          tab.setTipoTabellato(SqlManager.getValueFromVectorParam(attivita, 0).getStringValue());
          tab.setDescTabellato(SqlManager.getValueFromVectorParam(attivita, 1).stringValue());
        } catch (GestoreException e) {
          throw new RuntimeException("Errore inaspettato durante la lettura dei dati di G_SCADENZ", e);
        }
        lista.add(tab);
      }
    }
    return lista;
  }

  /**
   * Algoritmo per la copia delle attivit&agrave; presenti in uno scadenzario sorgenta ad uno scadenzario destinazione.
   *
   * @param codiceApplicazione
   *        codice applicazione
   * @param entita
   *        entit&agrave; di riferimento dello scadenzario
   * @param chiaveFrom
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario da copiare
   * @param chiaveTo
   *        chiave del record dello scadenzario in cui copiare le entit&agrave;
   * @param previsionale
   *        true=clona come previsionale, false=clona come scadenzario effettivo
   * @throws SQLException
   */
  public void insertClonazioneAttivitaScadenzario(String codiceApplicazione, String entita, Object[] chiaveFrom, Object[] chiaveTo,
      boolean previsionale)
      throws SQLException {
    Map<Long, Long> mappaIdConversione = new HashMap<Long, Long>();
    this.insertClonazioneAttivitaScadenzario(null, codiceApplicazione, entita, chiaveFrom, chiaveTo, previsionale, mappaIdConversione);
  }

  /**
   * Isolamento della parte ricorsiva dell'algoritmo di copia attivit&agrave; di uno scadenzario di partenza in uno di arrivo.
   *
   * @param idAttivitaModelloStart
   *        id da cui iniziare il ricalcolo, null se va ricalcolato su tutte le attivit&agrave; definite
   * @param codiceApplicazione
   *        codice applicazione
   * @param entita
   *        entit&agrave; di riferimento dello scadenzario
   * @param chiaveFrom
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario da copiare
   * @param chiaveTo
   *        chiave del record dello scadenzario in cui copiare le entit&agrave;
   * @param previsionale
   *        true=clona come previsionale, false=clona come scadenzario effettivo
   * @param mappaIdConversione
   *        mapping delle attivit&agrave; inserite nello scadenzario con le corrispondenti attivit&agrave; presenti nel modello
   * @throws SQLException
   */
  private void insertClonazioneAttivitaScadenzario(Long idAttivitaScadenzarioStart, String codiceApplicazione, String entita,
      Object[] chiaveFrom, Object[] chiaveTo, boolean previsionale, Map<Long, Long> mappaIdConversione) throws SQLException {
    List<Vector<JdbcParametro>> listaAttivita = getListaAttivita(entita, chiaveFrom, codiceApplicazione, false, idAttivitaScadenzarioStart,
        CAMPI_TRASFERIMENTO_ATTIVITA_ATTIVITA);
    if (listaAttivita != null) {
      // si cicla sulle attivita' (ma dovrebbe sempre essere una sola, con certezza quando si entra per idAttivitaStart valorizzato)
      for (Vector<JdbcParametro> attivita : listaAttivita) {
        // si crea l'attivita' sullo scadenzario
        this.clonaAttivitaSuScadenzario(attivita, entita, chiaveTo, codiceApplicazione, previsionale, true, mappaIdConversione);
        // si estrae la lista attivita' dipendenti dall'attivita' appena processata
        List<Long> listaAttivitaSuccessive = this.getListaAttivitaSuccessive(attivita);
        // RICORSIONE: si riapplica l'algorimo ad ogni attivita' rilevata (si scende nel sottoalbero delle attivita' di un livello)
        // altrimenti si termina perche' si e' arrivati al livello piu' basso dell'albero (foglie)
        for (Long idAttivitaSuccessiva : listaAttivitaSuccessive) {
          insertClonazioneAttivitaScadenzario(idAttivitaSuccessiva, codiceApplicazione, entita, chiaveFrom, chiaveTo, previsionale,
              mappaIdConversione);
        }
      }
    }
  }

}
