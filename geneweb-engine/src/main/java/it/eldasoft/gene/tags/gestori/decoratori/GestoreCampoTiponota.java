/*
 * Created on 08/05/12
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.gestori.decoratori;

import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreTabellatoNoOpzioneVuota;

/**
 * Gestore del campo tipo nota della tabella g_noteavvisi. Questo gestore elimina la
 * voce "Notifica di sistema" per le nuove occorrenze e per quelle in modifica
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoTiponota extends GestoreTabellatoNoOpzioneVuota {

  public String preHTML(boolean visualizzazione, boolean abilitato) {
    // si elimina l'opzione vuota
    super.preHTML(visualizzazione, abilitato);

    String valore = this.campo.getValue();
    if (valore== null || "".equals(valore) || (!"".equals(valore) && !"3".equals(valore))) {
      ValoreTabellato opzionePortale = new ValoreTabellato("3", "Notifica di sistema");
      int posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);

      // si elimina anche l'opzione "3"
      if (posizionePortale >= 0)
         this.getCampo().getValori().remove(posizionePortale);



    }

    return null;
  }
}
