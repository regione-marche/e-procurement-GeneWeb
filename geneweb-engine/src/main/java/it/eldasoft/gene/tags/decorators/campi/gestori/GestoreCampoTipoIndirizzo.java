/*
 * Created on 26/apr/2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.campi.gestori;

import java.util.Vector;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityBool;


/**
 *   Gestore del campo 'Tipo indirizzo' dei destinatari delle comunicazioni (COMTIPMA.W_INVCOMDES).
 *   Verifica se abilitata da configurazione la gestione dell'invio comunicazioni mediante fax,
 *   altrimenti toglie la voce 'fax' dal tabellato
 *   
 *    @author Sara.Santi
 */
public class GestoreCampoTipoIndirizzo extends AbstractGestoreCampo {

  public String gestisciDaTrova(Vector params, DataColumn col, String conf,
      SqlManager manager) {
    return null;
  }

  public String getClasseEdit() {
    return null;
  }

  public String getClasseVisua() {
    return null;
  }

  public String getHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  public String getValore(String valore) {
    return null;
  }

  public String getValorePerVisualizzazione(String valore) {
    return null;
  }

  public String getValorePreUpdateDB(String valore) {
    return null;
  }

  protected void initGestore() {

  }

  public String postHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  public String preHTML(boolean visualizzazione, boolean abilitato) {
    
    String invioFax = ConfigManager.getValore(CostantiGenerali.PROP_FAX_ABILITATO);
    if (invioFax==null || "".equals(invioFax) || "0".equals(invioFax)){
      ValoreTabellato opzioneFax = new ValoreTabellato("3", "Fax");
      int posizioneFax = this.getCampo().getValori().indexOf(opzioneFax);
      if (posizioneFax >= 0)
         this.getCampo().getValori().remove(posizioneFax);
    }
    
    return null;
  }

}
