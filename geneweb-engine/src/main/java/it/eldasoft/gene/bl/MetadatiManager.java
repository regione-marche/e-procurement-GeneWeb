/*
 * Created on 28-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl;

import it.eldasoft.gene.db.dao.MetadatiDao;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.LegameTabelle;
import it.eldasoft.utils.metadata.domain.NomeFisicoTabella;
import it.eldasoft.utils.metadata.domain.Schema;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

/**
 * Manager che si occupa di gestire tutte le operazioni di estrazione delle
 * informazioni relative ai metadati, ovvero schemi, tabelle, campi
 *
 * @author Stefano.Sabbadin
 */
public class MetadatiManager {

  // private static final String FORMATO_CAMPO_STRINGA_INIZIO = "V";
  private static final String FORMATO_CAMPO_DATA_INIZIO        = "D";
  private static final String FORMATO_CAMPO_INTERO_INIZIO      = "N";
  private static final String FORMATO_CAMPO_DECIMALE_INIZIO    = "F";
  private static final String FORMATO_CAMPO_PERCENTUALE_INIZIO = "R";
  private static final String FORMATO_CAMPO_NOTA_INIZIO        = "T";

  private static final String DOMINIO_TIMESTAMP                = "TIMESTAMP";
  private static final String DOMINIO_DATA                     = "DATA_ELDA";
  private static final String DOMINIO_NOTA                     = "NOTE";
  public static final String  DOMINIO_FLAG                     = "SN";

  /** DAO per l'accesso ai metadati */
  private MetadatiDao         metadatiDao;

  /** Logger Log4J di classe */
  static Logger               logger                           = Logger.getLogger(MetadatiManager.class);

  /**
   * @param metadatiDao
   *        metadatiDao da settare internamente alla classe.
   */
  public void setMetadatiDao(MetadatiDao metadatiDao) {
    this.metadatiDao = metadatiDao;
  }

  /**
   * Esegue il caricamento dei metadati nei dizionari condivisi per la gestione
   * di schemi, tabelle, campi
   */
  public void carica() {

    List<?> listaSchemi = this.metadatiDao.getElencoSchemi();
    List<?> listaNomiFisiciTabelle = this.metadatiDao.getElencoNomiFisiciTabelle();

    NomeFisicoTabella nfTabella = null;
    Tabella tabella = null;
    Vector elencoChiaveEsterneReferenti = null;
    Vector<?> elencoCampiTabella = null;
    Campo campo = null;
    Campo campoDizionario = null;
    Schema schema = null;
    Schema schemaDaElenco = null;

    for (int i = 0; i < listaNomiFisiciTabelle.size(); i++) {
      nfTabella = (NomeFisicoTabella) listaNomiFisiciTabelle.get(i);
      tabella = this.metadatiDao.getTabella(nfTabella.getNomeFisico());

      // Attenzione: l'istruzione precedente non popola l'attributo nomeFisico
      // dell'oggetto tabella!!

      // non si dovrebbe verificare mai, ma dato un nome fisico, se non
      // si riesce a trovare la tabella corrispondente allora si passa
      // a considerare l'elemento successivo
      if (tabella == null) {
        logger.warn("Attenzione: per il nome fisico "
            + nfTabella.getNomeFisico()
            + " non è stata trovata alcuna definizione di tabella");
        continue;
      }

      // si passa ad estrarre i legami di join con altre tabelle
      elencoChiaveEsterneReferenti = new Vector(
          this.metadatiDao.getElencoChiaviEsterneReferenti(nfTabella.getNomeFisico()));

      // si aggiungono i legami di join alla tabella
      tabella.setLegamiTabelle(elencoChiaveEsterneReferenti);

      // si passa ad estrarre le colonne della tabella dalla c0campi
      elencoCampiTabella = new Vector(
          this.metadatiDao.getElencoCampiTabella(nfTabella.getNomeFisico()));

      if (elencoCampiTabella.size() == 0) {
        logger.error("Attenzione: la tabella con nome fisico "
            + nfTabella.getNomeFisico()
            + " non contiene alcun campo all'interno. Verificarne la sua definizione");

      } else {

        for (int contCampi = 0; contCampi < elencoCampiTabella.size(); contCampi++) {
          campo = this.creaCampo((HashMap<String, String>) elencoCampiTabella.get(contCampi));

          campoDizionario = DizionarioCampi.getInstance().getCampoByNomeFisico(
              campo.getNomeFisicoCampo());

          // Va inserito prima la definizione del campo con tipo 'E', quindi solo
          // se non è presente si utilizza quella con codice 'C'. Di conseguenza,
          // se in precedenza è stato inserito un campo ed è un codice 'C', lo
          // sostituisco con quello considerato nell'iterazione solo se è un
          // codice 'E'
          if (campoDizionario == null) {

            // si aggiunge ogni campo al dizionario campi
            DizionarioCampi.getInstance().put(campo.getCodiceMnemonico(), campo);

            // si aggiunge ogni mnemonico campo alla lista per la tabella
            tabella.aggiungiMnemonicoCampo(campo.getCodiceMnemonico());
            // SS 25/10/2006: si gestisce un elenco con i soli elementi visibili
            // nelle ricerche
            if (campo.isVisibileRicerche())
              tabella.aggiungiMnemonicoCampoPerRicerche(campo.getCodiceMnemonico());

            // se è un campo chiave, si aggiunge il mnemonico all'elenco dei campi
            // chiave per la tabella
            // {MF041006} Aggiungo l'elenco con i campi chiave
            if (campo.isCampoChiave()) {
              tabella.getCampiKey().add(campo);
              tabella.aggiungiMnemonicoCampoChiave(campo.getCodiceMnemonico());
            }
          } else {

            if (!campoDizionario.isVisibileRicerche()
                && campo.isVisibileRicerche()) {
              // si elimina ogni riferimento al precedente campo con tipo 'P'(non
              // visibile dalle ricerche, ma configurabile da profilo) e si
              // inserisce il riferimento corretto, con tipo 'E'

              DizionarioCampi.getInstance().remove(
                  campoDizionario.getCodiceMnemonico());
              tabella.rimuoviMnemonicoCampo(campoDizionario.getCodiceMnemonico());
              tabella.rimuoviMnemonicoCampoPerRicerche(campoDizionario.getCodiceMnemonico());
              if (campoDizionario.isCampoChiave()) {
                tabella.getCampiKey().remove(campoDizionario);
                tabella.rimuoviMnemonicoCampoChiave(campoDizionario.getCodiceMnemonico());
              }
              // si inserisce il riferimento corretto, con tipo 'E'
              DizionarioCampi.getInstance().put(campo.getCodiceMnemonico(), campo);
              tabella.aggiungiMnemonicoCampo(campo.getCodiceMnemonico());
              tabella.aggiungiMnemonicoCampoPerRicerche(campo.getCodiceMnemonico());
              if (campo.isCampoChiave()) {
                tabella.getCampiKey().add(campo);
                tabella.aggiungiMnemonicoCampoChiave(campo.getCodiceMnemonico());
              }
            }
          }
        }

        // si aggiunge la tabella al dizionario delle tabelle, solo se la tabella
        // ha almeno un campo di tipo 'E' o 'P'
        if (tabella.getMnemoniciCampi().size() == 0) {
          logger.warn("Attenzione: al nome fisico "
              + nfTabella.getNomeFisico()
              + " e' associata una tabella di tipo "
              + tabella.getTipoEntita()
              + ", ma non e' stata caricata alcuna "
              + "definizione di tabella, in quanto o tutti i suoi campi sono "
              + "di tipo 'C' o non sono stati definiti i campi di tale tabella "
              + "in C0CAMPI");
        } else {

          // ai aggiunge la tabella al dizionario e si aggiunge un reference anche
          // allo schema
          DizionarioTabelle.getInstance().put(tabella.getCodiceMnemonico(),
              tabella);

          // si verifica se esiste già censito lo schema; se non esiste lo si crea
          // e lo si inserisce nel dizionario
          schema = DizionarioSchemi.getInstance().get(campo.getNomeSchema());
          if (schema == null) {
            // cerco nell'elenco degli schemi lo schema corrispondente e lo
            // inserisco nel dizionario
            for (int contSchemi = 0; contSchemi < listaSchemi.size(); contSchemi++) {
              schemaDaElenco = (Schema) listaSchemi.get(contSchemi);
              if (schemaDaElenco.getCodice().equals(campo.getNomeSchema())) {
                schema = schemaDaElenco;
                break;
              }
            }
            DizionarioSchemi.getInstance().put(schema.getCodice(), schema);
          }

          // si aggiunge allo schema il mnemonico della tabella
          schema.aggiungiMnemonicoTabella(tabella.getCodiceMnemonico());
          // SS 25/10/2006: si gestisce un elenco con i soli elementi visibili
          // nelle
          // ricerche
          if (tabella.isVisibileRicerche())
            schema.aggiungiMnemonicoTabellaPerRicerche(tabella.getCodiceMnemonico());
        }

      }

    }
    // Log di completamento caricamento metadati...
    if (logger.isInfoEnabled())
      logger.info("Caricamento metadati completato.");
  }

  /**
   * Determina il tipo di campo a partire dalle informazioni memorizzate nel
   * database e relative al suo formato e al suo dominio
   *
   * @param formato
   *        formato del campo
   * @param dominio
   *        eventuale dominio (data, importo, ...)
   * @param tabellato
   *        codice del tabellato eventuale
   *
   * @return codice che individua il tipo di campo, tra uno dei valori possibili
   *         presenti come costanti in Campo (TIPO_xxx)
   *
   * @see it.eldasoft.utils.metadata.domain.Campo
   */
  private short determinaTipoCampo(String formato, String dominio, String tabellato) {
    short tipo = Campo.TIPO_STRINGA; // default

    String formatoNonNull = (formato == null ? "" : formato);
    String dominioNonNull = (dominio == null ? "" : dominio);

    // per le casistiche eseguire la query "select distinct c0c_fs, coc_dom from
    // c0campi"
    // ed analizzare le diverse tipologie

    // aggiunto in testa il check del tabellato, se è su TAB1 allora il campo e' numerico
    if (tabellato != null && TabellatiManager.getNumeroTabellaByCodice(tabellato) == 1)
      tipo = Campo.TIPO_INTERO;
    else if (formatoNonNull.startsWith(FORMATO_CAMPO_NOTA_INIZIO)
        || dominioNonNull.startsWith(DOMINIO_NOTA))
      tipo = Campo.TIPO_NOTA;
    else if (dominioNonNull.startsWith(DOMINIO_TIMESTAMP))
      tipo = Campo.TIPO_TIMESTAMP;
    else if (formatoNonNull.startsWith(FORMATO_CAMPO_DATA_INIZIO)
        || dominioNonNull.startsWith(DOMINIO_DATA))
      tipo = Campo.TIPO_DATA;
    else if (formatoNonNull.startsWith(FORMATO_CAMPO_INTERO_INIZIO))
      tipo = Campo.TIPO_INTERO;
    else if (formatoNonNull.startsWith(FORMATO_CAMPO_DECIMALE_INIZIO)
        || formatoNonNull.startsWith(FORMATO_CAMPO_PERCENTUALE_INIZIO))
      tipo = Campo.TIPO_DECIMALE;
    // else if (formatoNonNull.equalsIgnoreCase(FORMATO_CAMPO_FLAG))
    // tipo = Campo.TIPO_FLAG;

    return tipo;
  }

  /**
   * Crea un oggetto di tipo Campo a partire da un oggetto di tipo HashMap
   *
   * @param hash
   *        haspMap contenente i dati necessari per la costruzione dell'oggetto
   *        di ritorno
   * @return Ritorna un oggetto di tipo Campo
   *
   * @see it.eldasoft.utils.metadata.domain.Campo
   */
  private Campo creaCampo(HashMap<String, String> hash) {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 26/09/2006 M.F. Aggiungo il settaggio dei dati non implementati nel
    // campo
    // ************************************************************
    // Le costanti di tipo String usate di seguito sono i nomi delle colonne
    // estratte dalla query
    // e sono usate come chiavi all'interno dell'HashMap.
    String coc_mne_uni = (String) UtilityHashMap.getValueCaseInsensitive(hash,
        "coc_mne_uni");
    String c0c_mne_ber = (String) UtilityHashMap.getValueCaseInsensitive(hash,
        "c0c_mne_ber");
    String coc_des = (String) UtilityHashMap.getValueCaseInsensitive(hash,
        "coc_des");
    String coc_des_frm = (String) UtilityHashMap.getValueCaseInsensitive(hash,
        "coc_des_frm");
    String c0c_fs = (String) UtilityHashMap.getValueCaseInsensitive(hash,
        "c0c_fs");
    String coc_dom = (String) UtilityHashMap.getValueCaseInsensitive(hash,
        "coc_dom");
    String c0c_chi = (String) UtilityHashMap.getValueCaseInsensitive(hash,
        "c0c_chi");
    String c0c_tab1 = (String) UtilityHashMap.getValueCaseInsensitive(hash,
        "c0c_tab1");
    String c0c_tip = (String) UtilityHashMap.getValueCaseInsensitive(hash,
        "c0c_tip");
    String coc_des_web = (String) UtilityHashMap.getValueCaseInsensitive(hash,
        "coc_des_web");

    int len = this.determinaLenCampo(c0c_fs, coc_dom);
    Campo campo = new Campo(c0c_mne_ber, coc_mne_uni, coc_des, coc_des_frm,
        coc_des_web, !"P".equals(c0c_tip), this.determinaTipoCampo(c0c_fs,
            coc_dom, c0c_tab1), c0c_chi != null && c0c_chi.equals("P"), c0c_tab1);
    campo.setDominio(coc_dom);
    campo.setLunghezza(len);
    campo.setDecimali(this.determinaDecimaliCampo(c0c_fs, coc_dom));
    return campo;
  }

  /**
   * Funzione che determina la larghezza del campo
   *
   * @param c0c_fs
   *        Tipo di campo del C0Campi
   * @param c0c_dom
   *        Dominio del campo perche se DATA_ELDA la dimensione è 10
   * @return Dimensione
   *
   * @author marco.franceschin
   *
   */
  private int determinaLenCampo(String c0c_fs, String c0c_dom) {
    // Inizializzazioni
    String lsTmp;
    int liRet = 0;
    if (c0c_dom != null) {
      if (DOMINIO_DATA.equals(c0c_dom)) return 10;
      if (DOMINIO_TIMESTAMP.equals(c0c_dom)) return 19;
    }
    lsTmp = c0c_fs.substring(1);
    if (lsTmp.indexOf('.') >= 0)
      lsTmp = lsTmp.substring(0, lsTmp.indexOf('.'));
    // Lo metto tra try catch perché di default deve rimanere 0 senza dare
    // errori
    try {
      liRet = Integer.parseInt(lsTmp);
    } catch (Throwable t) {

    }
    return liRet;
  }

  /**
   * Funzione che determina il numero di decimali definito per il campo
   *
   * @param c0c_fs
   *        Tipo di campo
   * @param c0c_dom
   *        Dominio del campo
   * @return
   */
  private int determinaDecimaliCampo(String c0c_fs, String c0c_dom) {
    // Inizializzazioni
    String lsTmp;
    int liRet = 0;
    if (c0c_dom != null) {
      if (c0c_dom.equals("MONEY"))
        return 2;
      else if (c0c_dom.equals("MONEY5")) return 5;
    }
    if (c0c_fs != null && c0c_fs.indexOf('.') > 0 && c0c_fs.charAt(0) == 'F') {
      lsTmp = c0c_fs.substring(c0c_fs.indexOf('.') + 1);
      // Lo metto tra try catch perché di default deve rimanere 0 senza dare
      // errori
      try {
        liRet = Integer.parseInt(lsTmp);
      } catch (Throwable t) {

      }
    }
    return liRet;
  }

  /**
   * Utilizza il Dao per ottenere l'elenco dei mnemonici che soddisfano i
   * criteri di filtro individuati dai parametri di input
   *
   * @param mnemonico
   *        mnemonico da ricercare
   * @param descrizione
   *        descrizione da ricercare
   * @return elenco di mnemonici
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public String[] getElencoMnemoniciPerRicerche(String mnemonico,
      String operatoreMnemonico, String descrizione, String operatoreDescrizione)
          throws DataAccessException {
    String[] risultato = null;
    List<?> lista = this.metadatiDao.getElencoMnemoniciPerRicerche(mnemonico,
        operatoreMnemonico, descrizione, operatoreDescrizione);
    if (lista != null) risultato = (String[]) lista.toArray(new String[0]);
    return risultato;
  }

  /**
   * Estrazione del campo C0E_KEY della tabella C0ENTIT, a partire
   * dall'id, per risalire dalla vista delle ricerche base alla
   * tabella fisica principale utilizzata nella vista
   *
   * @param idC0entit identificativo dell'entit&agrave; nel formato ENTITA.SCHEMA
   * @return nome dell'entit&agrave; principale, null se non valorizzata
   * @throws DataAccessException
   */
  public String getEntitaPrincipaleVista(String idC0entit) throws DataAccessException {
    String nomeTabella = null;

    String concatenazioneCampiChiave = this.metadatiDao.getC0eKeyById(idC0entit);
    if (concatenazioneCampiChiave != null) {
      LegameTabelle legameTabelle = new LegameTabelle();
      legameTabelle.setElencoCampiFisiciOrigine(concatenazioneCampiChiave);
      Tabella argomento = DizionarioTabelle.getInstance().getDaNomeTabella(legameTabelle.getTabellaOrigine());
      nomeTabella = argomento.getNomeTabella();
    }

    return nomeTabella;
  }
}