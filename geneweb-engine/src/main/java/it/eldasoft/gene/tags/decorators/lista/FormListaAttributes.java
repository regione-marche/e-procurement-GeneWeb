/*
 * Created on 4-gen-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.lista;

import it.eldasoft.gene.tags.TagAttributes;
import it.eldasoft.gene.tags.decorators.archivi.ArchivioRequest;
import it.eldasoft.gene.tags.decorators.scheda.CampiNonDiEntita;
import it.eldasoft.utils.metadata.domain.Tabella;

import java.util.List;
import java.util.Vector;

public class FormListaAttributes extends TagAttributes {

  /**
   *
   */
  private static final long serialVersionUID        = 1L;

  /*
   * Variabili utilizzate dal tag
   */
  /** Nome dell'entita */
  private String            entita                  = null;

  /** Path della scheda da aprire */
  private String            pathScheda;

  /** Path della scheda da aprire */
  private String            pathSchedaPopUp;

  /** Dimensione delle pagine 0 se non paginato */
  private int               pagesize                = 0;

  /** Classe da utilizzare nella tabella */
  private String            tableclass;

  /** Tabella che ha i campi */
  private Tabella           tabella;

  /** Elenco delle celle */
  private Vector            cells;

  /** Elenco delle celle */
  private Vector            cellsHeader;

  /** Numero della riga corrente */
  private int               currentRow;

  /** Elenco di valori */
  private List              valori;

  /** Where aggiuntiva sulla lista */
  private String            where;

  /** Indica se aggiungere l'attributo distinct */
  private String            distinct;

  /** Eventuale gestore che gestisce le eliminazioni dull'entità */
  private String            gestore;

  private int               curCella;

  private ArchivioRequest   archivio;

  /** Gestore di entità non appartenenti allentità principale */
  private CampiNonDiEntita  entitaDiverse;

  /** Eventuale select diretta sul database (senza utilizzo di c0campi e c0entit) */
  private String            select                  = null;

  /** Flag per disabilitare l'inserisci da archivio */
  private boolean           inserisciDaArchivio;

  /** Oggetto che esegue la paginazine sulla lista */
  PaginatoreListaImpl       paginator;

  /** Colonna di default su cui ordinare */
  private String            sortColumn              = "0";

  /** Nome delle variabile nel request che contiene i dati */
  private String            varName                 = null;

  /** Flag che dice se si tratta o meno del settaggio con dati nel request */
  private boolean           datiRequest             = false;

  /** Flag per definire la gestione delle protezioni */
  private boolean           gestisciProtezioni      = false;

  /** Flag per dire di gestire la proprieta dei gruppi sulle righe */
  private boolean           gestisciProtezioniRighe = false;

  /** Elenco dei campi modificabili nella lista e inviati con il submit del form */
  private StringBuffer      elencoCampi;

  /** Gestore di precaricamento della pagina */
  private String            plugin;

  public FormListaAttributes(String tipoVar) {
    super(tipoVar);
    setCells(new Vector());
    setCellsHeader(new Vector());
    setEntitaDiverse(new CampiNonDiEntita());
    setCurrentRow(-1);
    setValori(null);
    setCurCella(-1);
    setArchivio(null);
    setDatiRequest(false);
    setTabella(null);
    setEntita(null);
    setVarName(null);
    setPagesize(0);
    setTableclass("");
    setWhere(null);
    setInserisciDaArchivio(true);
    setTableclass("datilista");
    this.elencoCampi = new StringBuffer("");
    this.plugin = null;
  }

  public ArchivioRequest getArchivio() {
    return archivio;
  }

  public void setArchivio(ArchivioRequest archivio) {
    this.archivio = archivio;
  }

  public Vector getCells() {
    return cells;
  }

  public void setCells(Vector cells) {
    this.cells = cells;
  }

  public Vector getCellsHeader() {
    return cellsHeader;
  }

  public void setCellsHeader(Vector cellsHeader) {
    this.cellsHeader = cellsHeader;
  }

  public int getCurCella() {
    return curCella;
  }

  public void setCurCella(int curCella) {
    this.curCella = curCella;
  }

  public int getCurrentRow() {
    return currentRow;
  }

  public void setCurrentRow(int currentRow) {
    this.currentRow = currentRow;
  }

  public String getEntita() {
    return entita;
  }

  public void setEntita(String entita) {
    this.entita = entita;
  }

  public CampiNonDiEntita getEntitaDiverse() {
    return entitaDiverse;
  }

  public void setEntitaDiverse(CampiNonDiEntita entitaDiverse) {
    this.entitaDiverse = entitaDiverse;
  }

  public String getGestore() {
    return gestore;
  }

  public void setGestore(String gestore) {
    this.gestore = gestore;
  }

  public boolean isInserisciDaArchivio() {
    return inserisciDaArchivio;
  }

  public void setInserisciDaArchivio(boolean inserisciDaArchivio) {
    this.inserisciDaArchivio = inserisciDaArchivio;
  }

  public int getPagesize() {
    return pagesize;
  }

  public void setPagesize(int pagesize) {
    this.pagesize = pagesize;
  }

  public PaginatoreListaImpl getPaginator() {
    return paginator;
  }

  public void setPaginator(PaginatoreListaImpl paginator) {
    this.paginator = paginator;
  }

  public String getPathScheda() {
    return pathScheda;
  }

  public void setPathScheda(String pathScheda) {
    this.pathScheda = pathScheda;
  }

  public String getPathSchedaPopUp() {
    return pathSchedaPopUp;
  }

  public void setPathSchedaPopUp(String pathSchedaPopUp) {
    this.pathSchedaPopUp = pathSchedaPopUp;
  }

  public String getSelect() {
    return select;
  }

  public void setSelect(String select) {
    this.select = select;
  }

  public String getSortColumn() {
    return sortColumn;
  }

  public void setSortColumn(String sortColumn) {
    this.sortColumn = sortColumn;
  }

  public Tabella getTabella() {
    return tabella;
  }

  public void setTabella(Tabella tabella) {
    this.tabella = tabella;
  }

  public String getTableclass() {
    return tableclass;
  }

  public void setTableclass(String tableclass) {
    this.tableclass = tableclass;
  }

  public List getValori() {
    return valori;
  }

  public void setValori(List valori) {
    this.valori = valori;
  }

  public String getWhere() {
    return where;
  }

  public void setWhere(String where) {
    if (where != null && where.length() == 0) where = null;
    this.where = where;
  }

  public String getDistinct() {
	return distinct;
}

public void setDistinct(String distinct) {
	this.distinct = distinct;
}

public String getVarName() {
    return varName;
  }

  public void setVarName(String varDati) {
    this.varName = varDati;
  }

  public boolean isDatiRequest() {
    return datiRequest;
  }

  public void setDatiRequest(boolean datiRequest) {
    this.datiRequest = datiRequest;
  }

  public boolean isGestisciProtezioni() {
    return gestisciProtezioni;
  }

  public void setGestisciProtezioni(boolean gestisciProtezioni) {
    this.gestisciProtezioni = gestisciProtezioni;
  }

  /**
   * @return the gestisciProprietaRighe
   */
  public boolean isGestisciProtezioniRighe() {
    return gestisciProtezioniRighe;
  }

  /**
   * @param gestisciProprietaRighe
   *        the gestisciProprietaRighe to set
   */
  public void setGestisciProtezioniRighe(boolean gestisciProtezioniRighe) {
    this.gestisciProtezioniRighe = gestisciProtezioniRighe;
  }

  /**
   * @return Ritorna elencoCampi.
   */
  public StringBuffer getElencoCampi() {
    return elencoCampi;
  }

  /**
   * @return Ritorna plugin.
   */
  public String getPlugin() {
    return plugin;
  }

  /**
   * @param plugin
   *        plugin da settare internamente alla classe.
   */
  public void setPlugin(String plugin) {
    this.plugin = plugin;
  }
}
