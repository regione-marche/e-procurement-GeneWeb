/**
 * 
 */
package it.eldasoft.gene.db.dao.jdbc;

import java.util.List;

/**
 * 
 * @author Luca.Giacomazzo
 */
public class ListaDatiPaginati extends ListaDati {
  
  /** Numero di record da visualizzare in ogni pagina */
  private int numeroRecordPerPagina;
  
  /** Numero della pagina attualmente visualizzata */
  private int numeroPagina;
  
  /** Numero totale della pagine  */
  //private int numeroTotalePagine;

  public ListaDatiPaginati(){
    super();
    //this.numeroTotalePagine    = 0;
    this.numeroPagina          = 0;
    this.numeroRecordPerPagina = 0;
  }
  
  public ListaDatiPaginati(List listaDatiEstratti, int numeroTotRecord,
      int numeroTotPag, int numPaginaAttiva){
    super(listaDatiEstratti, numeroTotRecord);
    this.numeroRecordPerPagina = listaDatiEstratti.size();
    this.numeroPagina = numPaginaAttiva;
    //this.numeroTotalePagine = numeroTotPag;
  }
  
  /**
   * @return Ritorna numeroPagina.
   */
  public int getNumeroPagina() {
    return numeroPagina;
  }
  
  /**
   * @param numeroPagina numeroPagina da settare internamente alla classe.
   */
  public void setNumeroPagina(int numeroPagina) {
    this.numeroPagina = numeroPagina;
  }
  
  /**
   * @return Ritorna numeroRecordPerPagina.
   */
  public int getNumeroRecordPerPagina() {
    return numeroRecordPerPagina;
  }
  
  /**
   * @param numeroRecordPerPagina numeroRecordPerPagina da settare internamente alla classe.
   */
  public void setNumeroRecordPerPagina(int numeroRecordPerPagina) {
    this.numeroRecordPerPagina = numeroRecordPerPagina;
  }
  
  /**
   * @return Ritorna numeroTotalePagine.
   *
  public int getNumeroTotalePagine() {
    return numeroTotalePagine;
  }
  
  /**
   * @param numeroTotalePagine numeroTotalePagine da settare internamente alla classe.
   *
  public void setNumeroTotalePagine(int numeroTotalePagine) {
    this.numeroTotalePagine = numeroTotalePagine;
  }*/

}