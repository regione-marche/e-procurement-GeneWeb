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
package it.eldasoft.gene.db.domain.genric;

import it.eldasoft.gene.db.domain.genmod.ParametroModello;

import java.io.Serializable;
import java.util.Vector;

/**
 * Contenitore dei dati relativi ad una ricerca con modello. Viene usato per
 * raccogliere in un unico oggetto tutti i dati di una ricerca con modello,
 * compreso anche il modello ad esso associato
 *
 * @author Luca.Giacomazzo
 */
public class ContenitoreDatiProspetto implements Serializable {

  /** UID */
  private static final long serialVersionUID = -2955349155433129L;

  private DatiGenProspetto  datiGenProspetto;
  private Vector<GruppoRicerca>    elencoGruppi;
  private Vector<ParametroModello> elencoParametri;
  private String            fileModello;
  private String            nomeReportSorgente;
  private Integer           famigliaReportSorgente;

  public ContenitoreDatiProspetto(){
    this.datiGenProspetto = null;
    this.elencoGruppi = new Vector<GruppoRicerca>();
    this.elencoParametri = new Vector<ParametroModello>();
    this.fileModello = null;
    this.nomeReportSorgente = null;
    this.famigliaReportSorgente = null;
  }

  /**
   * @return Ritorna datiGenProspetto.
   */
  public DatiGenProspetto getDatiGenProspetto() {
    return datiGenProspetto;
  }

  /**
   * @param datiGenProspetto datiGenProspetto da settare internamente alla classe.
   */
  public void setDatiGenProspetto(DatiGenProspetto datiGenProspetto) {
    this.datiGenProspetto = datiGenProspetto;
  }

  /**
   * @return Ritorna elencoGruppi.
   */
  public Vector<GruppoRicerca> getElencoGruppi() {
    return elencoGruppi;
  }

  /**
   * @param elencoGruppi elencoGruppi da settare internamente alla classe.
   */
  public void setElencoGruppi(Vector<GruppoRicerca> elencoGruppi) {
    this.elencoGruppi = elencoGruppi;
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

  /**
   * @return Ritorna nomeReportSorgente.
   */
  public String getNomeReportSorgente() {
    return nomeReportSorgente;
  }

  /**
   * @param nomeReportSorgente nomeReportSorgente da settare internamente alla classe.
   */
  public void setNomeReportSorgente(String nomeReportSorgente) {
    this.nomeReportSorgente = nomeReportSorgente;
  }

  /**
   * @return Ritorna famigliaReportSorgente.
   */
  public Integer getFamigliaReportSorgente() {
    return famigliaReportSorgente;
  }

  /**
   * @param famigliaReportSorgente famigliaReportSorgente da settare internamente alla classe.
   */
  public void setFamigliaReportSorgente(Integer famigliaReportSorgente) {
    this.famigliaReportSorgente = famigliaReportSorgente;
  }

  public void aggiungiGruppo(GruppoRicerca record) {
    this.elencoGruppi.addElement(record);
  }

  public void eliminaGruppo(int progressivo) {
    this.elencoGruppi.removeElementAt(progressivo);
  }

  public void aggiungiParametro(ParametroModello record) {
    record.setIdModello(this.datiGenProspetto.getDatiGenRicerca().getIdProspetto().intValue());
    record.setProgressivo(this.elencoParametri.size());
    this.elencoParametri.addElement(record);
  }

  public void eliminaParametro(int progressivo) {
    this.elencoParametri.removeElementAt(progressivo);
    for(int i=0; i < this.elencoParametri.size(); i++)
      this.elencoParametri.get(i).setProgressivo(i);
  }

  public void setIdRicerca(int idRicerca){
    this.datiGenProspetto.getDatiGenRicerca().setIdRicerca(new Integer(idRicerca));
    for (int i = 0; i < this.elencoGruppi.size(); i++) {
      this.elencoGruppi.elementAt(i).setId(new Integer(idRicerca));
    }
    for (int i = 0; i < this.elencoParametri.size(); i++) {
      this.elencoParametri.elementAt(i).setIdModello(
          this.datiGenProspetto.getDatiGenRicerca().getIdProspetto().intValue());
    }
  }

}