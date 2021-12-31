/**
 * 
 */
package it.eldasoft.gene.tags.decorators.scheda;


/**
 * @author Luca Giacomazzo
 *
 */
public abstract class AbstractGestoreScheda {

  //E' solo un'idea... da 
  
  /**
   * Metodo da eseguire per operazioni necessarie prima dell'apertura 
   * della scheda
   */
  public abstract void beforeLoadScheda();
  
  /**
   * Metodo per eseguire operazioni necessarie prima dell'operazione di update
   */
  public abstract void beforeUpdateScheda();
  
  /**
   * Metodo per eseguire operazioni necessarie prima dell'operazione di
   * apertura della scheda in creazione
   */
  public abstract void beforeNewScheda();
  
  /**
   * Metodo per eseguire operazioni necessarie prima dell'operazione di 
   * insert di una nuova entita'
   */
  public abstract void beforeInsertScheda();
  
}