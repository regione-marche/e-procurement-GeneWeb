/*
 * Created on 18-ott-2006
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
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;

/**
 * @author Marcello Caminiti
 *
 */
public class GestoreCampoPersona extends AbstractGestoreCampo{

  public String getValore(String valore) {
    
    return null;
  }

  public String getValorePerVisualizzazione(String valore) {
    
    return null;
  }

  public String getValorePreUpdateDB(String valore) {
    
    return null;
  }

  public String preHTML(boolean visualizzazione, boolean abilitato) {
    
    return null;
  }

  public String getHTML(boolean visualizzazione, boolean abilitato) {
    
    return null;
  }

  public String postHTML(boolean visualizzazione, boolean abilitato) {
    
    return null;
  }

  public String getClasseEdit() {
    
    return null;
  }

  public String getClasseVisua() {
    
    return null;
  }

  /**
   * Inizializzo la combobox con i valori "F"(fisica) e "G"(giuridica)
   */
  protected void initGestore() {
    this.getCampo().setTipo("ET2");
    this.getCampo().getValori().clear();
    this.getCampo().addValore("", "");
    this.getCampo().addValore("F", "fisico");
    this.getCampo().addValore("G", "giuridico");
    this.getCampo().setDefaultValue("F");
    
  }

  public String gestisciDaTrova(Vector params, DataColumn colWithValue, String conf, SqlManager manager) {
    return null;
  }

}
