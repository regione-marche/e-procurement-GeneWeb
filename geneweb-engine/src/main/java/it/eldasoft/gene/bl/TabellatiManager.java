/*
 * Created on 28-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.genric.GestoreVisibilitaDati;
import it.eldasoft.gene.db.dao.TabellatiDao;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.TabellatoWsdm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte di caricamento dei tabellati
 *
 * @author Luca.Giacomazzo
 */
public class TabellatiManager {

  public static final String TIPO_MODELLI     = "AgzC5";

  public static final String TIPO_RICERCHE    = "AgC03";

  /**
   * Codice con cui ricerca in Tab1 i record relativi al tipo di famiglia della ricerca. Sono 3 record
   */
  public static final String FAMIGLIA_RICERCA = "W0001";

  public static final String UFF_APPARTENENZA = "G_022";

  public static final String RUOLO_ME         = "G_058";

  public static final String CATEGORIE_UTENTE = "G_059";

  private GeneManager        geneManager;

  private TabellatiDao       tabellatiDao;

  /** Logger Log4J di classe */
  static Logger              logger           = Logger.getLogger(TabellatiManager.class);

  /**
   * @return Ritorna tabellatiDao.
   */
  public TabellatiDao getTabellatiDao() {
    return tabellatiDao;
  }

  /**
   * @param tabellatiDao
   *        tabellatiDao da settare internamente alla classe.
   */
  public void setTabellatiDao(TabellatiDao tabellatiDao) {
    this.tabellatiDao = tabellatiDao;
  }

  /**
   * Funzione che estrae l'elenco dei tabellati
   *
   * @param codiceTabellato
   *        Codice del tabellato
   * @return Lista di Tabellato con le occorrenze ritrovate
   */
  public List<Tabellato> getTabellato(String codiceTabellato) {
    return this.tabellatiDao.getTabellati(codiceTabellato);
  }

  /**
   * Funzione che estrae l'elenco dei tabellati
   *
   * @param codiceTabellato
   *        Codice del tabellato
   * @param numeroTabella
   *        Tabella in cui si trova il tabellato
   * @return
   */
  public List<Tabellato> getTabellato(String codiceTabellato, int numeroTabella) {
    return this.tabellatiDao.getTabellati(codiceTabellato, numeroTabella);
  }

  /**
   * Funzione che estrae la descrizione del tabellato in funzione del codice e
   * del valore del tabellato
   *
   * @param codiceTabellato
   *        Codice del tabellato
   * @param valoreTabellato
   *        valore del tabellato
   * @return Descrizione del tabellato nel database
   */
  public String getDescrTabellato(String codiceTabellato, String valoreTabellato) {
    // Estraggo il tabellato
    Tabellato datiTabellato = this.tabellatiDao.getTabellato(codiceTabellato,
        valoreTabellato);

    if (datiTabellato != null) {
      return datiTabellato.getDescTabellato();
    }
    return null;
  }

  /**
   * Estrae l'elenco dei tabellati (codice - descrizione) da TAB6 filtrando per
   * codice tabellato, con condizione LIKE
   *
   * @param codiceTabellato
   * @return elenco dei tabellati (codice - descrizione)
   */
  public List<Tabellato> getElencoTabellati(String codiceTabellato) {
    return this.tabellatiDao.getElencoTabellati(codiceTabellato);
  }

  /**
   * Estrae la lista dei campi tabellati visibili nel profilo e associati ai
   * campi dell'entità principale
   *
   * @param codiceProfilo
   * @param schemaViste
   * @param entitaPrincipale
   * @return
   */
  public List<Tabellato> getCampiTabellatiByProfilo(String codiceProfilo,
      String schemaViste, String entitaPrincipale) {
    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();

    Tabella tabellaPrincipale = dizTabelle.getDaNomeTabella(entitaPrincipale);

    List<String> listaCampi = this.tabellatiDao.getCampiTabellati(schemaViste,
        tabellaPrincipale.getNomeFisico());

    Vector<Tabellato> listaCampiTabellati = new Vector<Tabellato>();
    // controlliamo se gli elementi sono abilitati nel profilo
    // se no li eliminaiamo dalla lista
    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    for (Iterator<String> iter = listaCampi.iterator(); iter.hasNext();) {
      String element = iter.next();
      Campo campo = dizCampi.get(element);
      // se il campo è visibile nelle ricerche ed è visibile nel profilo
      if (campo != null
          && this.checkCampo(campo, codiceProfilo)) {
        // creo un oggetto tabellato che contiene i dati dei campi:
        // descrizione breve campo (descrizione tabellato)
        // codice tabellato (dato supplementare tabellato)
        // mnemonico campo (tipo tabellato)
        Tabellato campoTabellato = new Tabellato();
        Tabella tabella = dizTabelle.getDaNomeTabella(campo.getNomeTabella());
        if (tabella != null) {
          String descrizione = campo.getDescrizione();
          /*
           * + " (" + tabella.getCodiceMnemonico() + ")";
           */
          campoTabellato.setDescTabellato(descrizione);
          campoTabellato.setTipoTabellato(campo.getCodiceMnemonico());
          campoTabellato.setDatoSupplementare(campo.getCodiceTabellato());
          listaCampiTabellati.add(campoTabellato);
        }
      }
    }
    return listaCampiTabellati;
  }

  private boolean checkCampo(Campo campo, String codiceProfilo) {
    GestoreVisibilitaDati gestoreVisibilita = this.geneManager.getGestoreVisibilitaDati();
    return gestoreVisibilita.checkCampoVisibile(campo, codiceProfilo);
  }

  /**
   * Funzione che in funzione del codice del tabellato restituisce il numero
   * della tabella associata
   *
   * @param codiceTabellato
   * @return
   */
  public static int getNumeroTabellaByCodice(String codiceTabellato) {
    /*
     * Attraverso il terzo carattere del codice del tabellato è possibile
     * identificare in che tabella si trova quindi faccio il case sul terzo
     * carattere del codice tabellato x e y Sono in tab3 w e k Sono in tab0 v e
     * z Sono in tab2 j Sono in tab 5 altrimenti si trova in tab 1
     */
    String lsChar = codiceTabellato.length() > 2 ? codiceTabellato.substring(2,
        3) : "0";

    if ("xy".indexOf(lsChar) >= 0) {
      // Tabella 3
      return 3;
    }
    if ("wk".indexOf(lsChar) >= 0) {
      // Tabella 0
      return 0;
    }
    if ("vz".indexOf(lsChar) >= 0) {
      // Tabella 2
      return 2;
    }
    if (lsChar.equals("j")) {
      // Tabella 5
      return 5;
    }
    return 1;
  }

  /**
   * Funzione che restituisce se il tabellato ha un valore stringa oppure
   * numerico
   *
   * @param codice
   * @return
   */
  public boolean isTabellatoString(String codice) {
    boolean isString = true;
    // solo i tabellati di Tab1 sono numerici
    if (getNumeroTabellaByCodice(codice) == 1) isString = false;

    return isString;
  }

  /**
   * Funzione che estrae l'elenco dei tabellati
   *
   * @param codiceTabellato
   *        Codice del tabellato
   * @return Lista di Tabellato con le occorrenze ritrovate
   */
  public List<TabellatoWsdm> getTabellatiFromIdconfiCftab(Long idconfi, String cftab) {
    return this.tabellatiDao.getTabellatiFromIdconfiCftab(idconfi, cftab);
  }

  /**
   * Funzione che estrae l'elenco dei tabellati
   *
   * @param codiceTabellato
   *        Codice del tabellato
   * @return Lista di Tabellato con le occorrenze ritrovate
   */
  public List<TabellatoWsdm> getTabellatiWsdm(Long idconfi, String sistema, String codice) {
    return this.tabellatiDao.getTabellatiWsdm(idconfi, sistema, codice);
  }

  /**
   * Funzione che estrae l'elenco dei tabellati
   *
   * @param codiceTabellato
   *        Codice del tabellato
   * @return Lista di Tabellato con le occorrenze ritrovate
   */
  public List<Tabellato> getElencoTabellatiWsdm(String codapp, String sistema, Long idconfi) {
    return this.tabellatiDao.getElencoTabellatiWsdm(codapp, sistema, idconfi);
  }

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * Funzione che estrae la descrizione di un tabellato in funzione del codice e
   * del valore del tabellato
   *
   * @param codiceTabellato
   *        Codice del tabellato
   * @param valoreTabellato
   *        valore del tabellato
   * @return Descrizione supplementare del tabellato nel database
   */
  public String getDescrSupplementare(String codiceTabellato, String valoreTabellato) {
    return this.tabellatiDao.getDescrSupplementare(codiceTabellato, valoreTabellato);
  }
}
