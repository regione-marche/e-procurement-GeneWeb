/**
 * 
 */
package it.eldasoft.gene.web.struts.genric.prospetto;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

/**
 * Questa classe estende la classe CheckParametriProspettoAction e ridefinisce il
 * metodo getOpzioniRunAction solamente per gestire correttamente i diritti di
 * accesso alle diverse Action.
 * In particolare questa classe e' una Action invocata esclusivamente durante
 * l'esecuzione di un report con modello PREDEFINITO che richiede il settaggio
 * dei parametri. La classe padre invece viene invocata solo in fase di 
 * esecuzione di un report con modello NON predefinito.
 *  
 * @author Luca.Giacomazzo
 */
public class CheckParametriProspettoPredefinitoAction extends
    CheckParametriProspettoAction {

  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_DEFAULT;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action runAction
   * @return opzioni per accedere alla action
   */
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente("");
  }
}
