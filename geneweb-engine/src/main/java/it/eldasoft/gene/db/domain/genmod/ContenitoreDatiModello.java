/*
 * Created on 02-ago-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.genmod;

import java.io.Serializable;
import java.util.Vector;

/**
 * Contenitore dei dati relativi ad una ricerca con modello. Viene usato per
 * raccogliere in un unico oggetto tutti i dati di una ricerca con modello,
 * compreso anche il modello ad esso associato
 *
 * @author Luca.Giacomazzo
 */
public class ContenitoreDatiModello implements Serializable {

  /**   UID   */
  private static final long serialVersionUID = 4529463234668269149L;

  private DatiModello datiGenModello;
  private Vector<ParametroModello>           elencoParametri;
  private Vector<GruppoModello>           elencoGruppi;
  private String           fileModello;

  public ContenitoreDatiModello(){
    this.datiGenModello    = null;
    this.elencoGruppi     = new Vector<GruppoModello>();
    this.elencoParametri     = new Vector<ParametroModello>();
    this.fileModello         = null;
  }



  /**
   * @return Returns the datiGenModello.
   */
  public DatiModello getDatiGenModello() {
    return datiGenModello;
  }



  /**
   * @param datiGenModello The datiGenModello to set.
   */
  public void setDatiGenModello(DatiModello datiGenModello) {
    this.datiGenModello = datiGenModello;
  }


  /**
   * @return Ritorna elencoParametri.
   */
  public Vector<ParametroModello> getElencoParametri() {
    return elencoParametri;
  }

  /**
   * @param elencoParametri elencoParametri da settare internamente alla classe.
   */
  public void setElencoParametri(Vector<ParametroModello> elencoParametri) {
    this.elencoParametri = elencoParametri;
  }

  /**
   * @return Ritorna fileModello.
   */
  public String getFileModello() {
    return fileModello;
  }

  /**
   * @param fileModello fileModello da settare internamente alla classe.
   */
  public void setFileModello(String fileModello) {
    this.fileModello = fileModello;
  }


  public void aggiungiParametro(ParametroModello record) {
    record.setIdModello(this.datiGenModello.getIdModello());
    record.setProgressivo(this.elencoParametri.size());
    this.elencoParametri.addElement(record);
  }

  public void eliminaParametro(int progressivo) {
    this.elencoParametri.removeElementAt(progressivo);
    for(int i=0; i < this.elencoParametri.size(); i++)
      (this.elencoParametri.get(i)).setProgressivo(i);
  }

  public void setIdRicerca(int idRicerca){
    this.datiGenModello.setIdModello(idRicerca);

    for (int i = 0; i < this.elencoParametri.size(); i++) {
      (this.elencoParametri.elementAt(i)).setIdModello(
          this.datiGenModello.getIdModello());
    }
  }

  public void aggiungiGruppo(GruppoModello record) {
    this.elencoGruppi.addElement(record);
  }

  public void eliminaGruppo(int progressivo) {
    this.elencoGruppi.removeElementAt(progressivo);
  }




  /**
   * @return Returns the elencoGruppi.
   */
  public Vector<GruppoModello> getElencoGruppi() {
    return elencoGruppi;
  }




  /**
   * @param elencoGruppi The elencoGruppi to set.
   */
  public void setElencoGruppi(Vector<GruppoModello> elencoGruppi) {
    this.elencoGruppi = elencoGruppi;
  }
}