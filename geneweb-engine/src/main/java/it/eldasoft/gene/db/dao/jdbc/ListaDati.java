/**
 * 
 */
package it.eldasoft.gene.db.dao.jdbc;

import it.eldasoft.utils.metadata.domain.Campo;

import java.util.List;

/**
 * @author Luca.Giacomazzo
 */
public class ListaDati {

  /** lista dei dati visualizzati nella pagina */
  private List listaDati;
  
  /** Numero totale di record  */
  private int numeroTotaleRecord;
  
  private Campo[] arrayCampi;
  
  public ListaDati(){
    this.listaDati             = null;
    this.numeroTotaleRecord    = 0;
    this.arrayCampi            = null;
  }
  
  public ListaDati(List listaDatiEstratti, int numeroTotRecord){
    this.listaDati = listaDatiEstratti;
    this.numeroTotaleRecord = numeroTotRecord;
    this.arrayCampi = null;
  }
  
  public ListaDati(List listaDatiEstratti, int numeroTotRecord, Campo[] arrayCampi){
    this.listaDati = listaDatiEstratti;
    this.numeroTotaleRecord = numeroTotRecord;
    this.arrayCampi = arrayCampi;
  }
  
  /**
   * @return Ritorna listaDati.
   */
  public List getListaDati() {
    return listaDati;
  }
  
  /**
   * @param listaDati listaDati da settare internamente alla classe.
   */
  public void setListaDati(List listaDati) {
    this.listaDati = listaDati;
  }
  
  /**
   * @return Ritorna numeroTotaleRecord.
   */
  public int getNumeroTotaleRecord() {
    return numeroTotaleRecord;
  }
  
  /**
   * @param numeroTotaleRecord numeroTotaleRecord da settare internamente alla classe.
   */
  public void setNumeroTotaleRecord(int numeroTotaleRecord) {
    this.numeroTotaleRecord = numeroTotaleRecord;
  }
  
  /**
   * @return Ritorna arrayCampi.
   */
  public Campo[] getArrayCampi() {
    return arrayCampi;
  }

  
  /**
   * @param arrayCampi arrayCampi da settare internamente alla classe.
   */
  public void setArrayCampi(Campo[] arrayCampi) {
    this.arrayCampi = arrayCampi;
  }
  
}
