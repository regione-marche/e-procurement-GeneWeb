/*
 * Created on 21-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.genric;

import java.io.Serializable;
import java.util.Vector;

/**
 * Contenitore di dati relativi ad una ricerca. Viene usato per raccogliere in
 * un unico oggetto tutti i dati di una ricerca
 *
 * @author Luca Giacomazzo
 */
public class ContenitoreDatiRicerca implements Serializable {

  /** UID */
  private static final long          serialVersionUID = -4444191706906822655L;

  public static final int            SPOSTAMENTO_SU   = -1;
  public static final int            SPOSTAMENTO_GIU  = 1;

  private DatiGenRicerca             datiGenerali;
  private Vector<GruppoRicerca>      elencoGruppi;
  private Vector<TabellaRicerca>     elencoArgomenti;
  private Vector<CampoRicerca>       elencoCampi;
  private Vector<GiunzioneRicerca>   elencoGiunzioni;
  private Vector<ParametroRicerca>   elencoParametri;
  private Vector<FiltroRicerca>      elencoFiltri;
  private Vector<OrdinamentoRicerca> elencoOrdinamenti;

  public ContenitoreDatiRicerca() {
    this.datiGenerali = new DatiGenRicerca();
    this.elencoGruppi = new Vector<GruppoRicerca>();
    this.elencoArgomenti = new Vector<TabellaRicerca>();
    this.elencoCampi = new Vector<CampoRicerca>();
    this.elencoGiunzioni = new Vector<GiunzioneRicerca>();
    this.elencoParametri = new Vector<ParametroRicerca>();
    this.elencoFiltri = new Vector<FiltroRicerca>();
    this.elencoOrdinamenti = new Vector<OrdinamentoRicerca>();
  }

  /**
   * @return Returns the datiGenerali.
   */
  public DatiGenRicerca getDatiGenerali() {
    return datiGenerali;
  }

  /**
   * @param datiGenerali
   *        The datiGenerali to set.
   */
  public void setDatiGenerali(DatiGenRicerca testata) {
    this.datiGenerali = testata;
  }

  /**
   * @return Returns the elencoCampi.
   */
  public Vector<CampoRicerca> getElencoCampi() {
    return elencoCampi;
  }

  /**
   * @return Returns the elencoFiltri.
   */
  public Vector<FiltroRicerca> getElencoFiltri() {
    return elencoFiltri;
  }

  /**
   * @return Returns the elencoGiunzioni.
   */
  public Vector<GiunzioneRicerca> getElencoGiunzioni() {
    return elencoGiunzioni;
  }

  /**
   * @return Returns the elencoOrdinamenti.
   */
  public Vector<OrdinamentoRicerca> getElencoOrdinamenti() {
    return elencoOrdinamenti;
  }

  /**
   * @return Returns the elencoArgomenti.
   */
  public Vector<TabellaRicerca> getElencoArgomenti() {
    return elencoArgomenti;
  }

  /**
   * @return Returns the elencoGruppi.
   */
  public Vector<GruppoRicerca> getElencoGruppi() {
    return elencoGruppi;
  }

  /**
   * @return Ritorna elencoParametri.
   */
  public Vector<ParametroRicerca> getElencoParametri() {
    return elencoParametri;
  }
  
  /**
   * Imposta in tutte le informazioni all'interno del contenitore l'id della
   * ricerca
   *
   * @param id
   *        id della ricerca
   */
  public void setIdRicerca(int id) {
    Integer idRicerca = new Integer(id);
    this.datiGenerali.setIdRicerca(idRicerca);
    for (int i = 0; i < this.elencoGruppi.size(); i++) {
      (this.elencoGruppi.elementAt(i)).setId(idRicerca);
    }
    for (int i = 0; i < this.elencoArgomenti.size(); i++) {
      (this.elencoArgomenti.elementAt(i)).setId(idRicerca);
    }
    for (int i = 0; i < this.elencoCampi.size(); i++) {
      (this.elencoCampi.elementAt(i)).setId(idRicerca);
    }
    for (int i = 0; i < this.elencoGiunzioni.size(); i++) {
      (this.elencoGiunzioni.elementAt(i)).setId(idRicerca);
    }
    for (int i = 0; i < this.elencoFiltri.size(); i++) {
      (this.elencoFiltri.elementAt(i)).setId(idRicerca);
    }
    for (int i = 0; i < this.elencoParametri.size(); i++) {
      (this.elencoParametri.elementAt(i)).setId(idRicerca);
    }
    for (int i = 0; i < this.elencoOrdinamenti.size(); i++) {
      (this.elencoOrdinamenti.elementAt(i)).setId(idRicerca);
    }
  }

  public void aggiungiGruppo(GruppoRicerca record) {
    this.elencoGruppi.addElement(record);
  }

  public void aggiungiTabella(TabellaRicerca record) {
    record.setId(this.datiGenerali.getIdRicerca());
    record.setProgressivo(this.elencoArgomenti.size());
    this.elencoArgomenti.addElement(record);
  }

  public void aggiungiCampo(CampoRicerca record) {
    record.setId(this.datiGenerali.getIdRicerca());
    record.setProgressivo(this.elencoCampi.size());
    this.elencoCampi.addElement(record);
  }

  public void aggiungiGiunzione(GiunzioneRicerca record) {
    record.setId(this.datiGenerali.getIdRicerca());
    record.setProgressivo(this.elencoGiunzioni.size());
    this.elencoGiunzioni.addElement(record);
  }

  public void aggiungiFiltro(FiltroRicerca record) {
    record.setId(this.datiGenerali.getIdRicerca());
    record.setProgressivo(this.elencoFiltri.size());
    this.elencoFiltri.addElement(record);
  }

  public void aggiungiParametro(ParametroRicerca record) {
    record.setId(this.datiGenerali.getIdRicerca());
    record.setProgressivo(this.elencoParametri.size());
    this.elencoParametri.addElement(record);
  }

  public void aggiungiOrdinamento(OrdinamentoRicerca record) {
    record.setId(this.datiGenerali.getIdRicerca());
    record.setProgressivo(this.elencoOrdinamenti.size());
    this.elencoOrdinamenti.addElement(record);
  }

  public void eliminaTabella(int progressivo) {
    this.elencoArgomenti.removeElementAt(progressivo);
    for (int i = 0; i < this.elencoArgomenti.size(); i++)
      (this.elencoArgomenti.elementAt(i)).setProgressivo(i);
  }

  public void eliminaCampo(int progressivo) {
    this.elencoCampi.removeElementAt(progressivo);
    for (int i = 0; i < this.elencoCampi.size(); i++)
      (this.elencoCampi.elementAt(i)).setProgressivo(i);
  }

  public void eliminaGiunzione(int progressivo) {
    this.elencoGiunzioni.removeElementAt(progressivo);
    for (int i = 0; i < this.elencoGiunzioni.size(); i++)
      (this.elencoGiunzioni.elementAt(i)).setProgressivo(i);
  }

  public void eliminaParametro(int progressivo) {
    this.elencoParametri.removeElementAt(progressivo);
    for (int i = 0; i < this.elencoParametri.size(); i++)
      (this.elencoParametri.elementAt(i)).setProgressivo(i);
  }

  public void eliminaFiltro(int progressivo) {
    this.elencoFiltri.removeElementAt(progressivo);
    for (int i = 0; i < this.elencoFiltri.size(); i++)
      (this.elencoFiltri.elementAt(i)).setProgressivo(i);
  }

  public void eliminaOrdinamento(int progressivo) {
    this.elencoOrdinamenti.removeElementAt(progressivo);
    for (int i = 0; i < this.elencoOrdinamenti.size(); i++)
      (this.elencoOrdinamenti.elementAt(i)).setProgressivo(i);
  }

  public GruppoRicerca estraiGruppo(int progressivo) {
    return this.elencoGruppi.elementAt(progressivo);
  }

  public TabellaRicerca estraiArgomento(int progressivo) {
    return this.elencoArgomenti.elementAt(progressivo);
  }

  public CampoRicerca estraiCampo(int progressivo) {
    return this.elencoCampi.elementAt(progressivo);
  }

  public GiunzioneRicerca estraiGiunzione(int progressivo) {
    return this.elencoGiunzioni.elementAt(progressivo);
  }

  public ParametroRicerca estraiParametro(int progressivo) {
    return this.elencoParametri.elementAt(progressivo);
  }

  public FiltroRicerca estraiFiltro(int progressivo) {
    return this.elencoFiltri.elementAt(progressivo);
  }

  public OrdinamentoRicerca estraiOrdinamento(int progressivo) {
    return this.elencoOrdinamenti.elementAt(progressivo);
  }

  public int getNumeroGruppi() {
    return this.elencoGruppi.size();
  }

  public int getNumeroTabelle() {
    return this.elencoArgomenti.size();
  }

  public int getNumeroCampi() {
    return this.elencoCampi.size();
  }

  public int getNumeroGiunzioni() {
    return this.elencoGiunzioni.size();
  }

  public int getNumeroParametri() {
    return this.elencoParametri.size();
  }

  public int getNumeroFiltri() {
    return this.elencoFiltri.size();
  }

  public int getNumeroOrdinamenti() {
    return this.elencoOrdinamenti.size();
  }

  public void spostaCampo(int progressivo, int offset) {
    CampoRicerca recordDaSpostare = this.elencoCampi.elementAt(progressivo
        + offset);
    CampoRicerca recordSelezionato = this.elencoCampi.remove(progressivo);
    int progressivoTmp = recordDaSpostare.getProgressivo();
    recordDaSpostare.setProgressivo(recordSelezionato.getProgressivo());
    recordSelezionato.setProgressivo(progressivoTmp);
    this.elencoCampi.add(progressivo + offset, recordSelezionato);
  }

  public void spostaOrdinamento(int progressivo, int offset) {
    OrdinamentoRicerca recordDaSpostare = this.elencoOrdinamenti.elementAt(progressivo
        + offset);
    OrdinamentoRicerca recordSelezionato = this.elencoOrdinamenti.remove(progressivo);
    int progressivoTmp = recordDaSpostare.getProgressivo();
    recordDaSpostare.setProgressivo(recordSelezionato.getProgressivo());
    recordSelezionato.setProgressivo(progressivoTmp);
    this.elencoOrdinamenti.add(progressivo + offset, recordSelezionato);
  }

  public void spostaFiltro(int progressivo, int offset) {
    FiltroRicerca recordDaSpostare = this.elencoFiltri.elementAt(progressivo
        + offset);
    FiltroRicerca recordSelezionato = this.elencoFiltri.remove(progressivo);
    int progressivoTmp = recordDaSpostare.getProgressivo();
    recordDaSpostare.setProgressivo(recordSelezionato.getProgressivo());
    recordSelezionato.setProgressivo(progressivoTmp);
    this.elencoFiltri.add(progressivo + offset, recordSelezionato);
  }

  /**
   * Ritorna l'array contenente titoli da inserire delle colonne da estrarre
   *
   * @return elenco dei titoli delle colonne
   */
  public String[] getTitoliColonne() {
    String[] titoliColonne = new String[this.getNumeroCampi()];
    String titoloColonna = null;
    for (int i = 0; i < this.getNumeroCampi(); i++) {
      CampoRicerca campo = this.estraiCampo(i);
      titoloColonna = campo.getTitoloColonna();
      titoliColonne[i] = titoloColonna;
    }
    return titoliColonne;
  }

  /**
   * @param elencoArgomenti
   *        elencoArgomenti da settare internamente alla classe.
   */
  public void setElencoArgomenti(Vector<TabellaRicerca> elencoArgomenti) {
    this.elencoArgomenti = elencoArgomenti;
  }

  /**
   * @param elencoCampi
   *        elencoCampi da settare internamente alla classe.
   */
  public void setElencoCampi(Vector<CampoRicerca> elencoCampi) {
    this.elencoCampi = elencoCampi;
  }

  /**
   * @param elencoFiltri
   *        elencoFiltri da settare internamente alla classe.
   */
  public void setElencoFiltri(Vector<FiltroRicerca> elencoFiltri) {
    this.elencoFiltri = elencoFiltri;
  }

  /**
   * @param elencoGiunzioni
   *        elencoGiunzioni da settare internamente alla classe.
   */
  public void setElencoGiunzioni(Vector<GiunzioneRicerca> elencoGiunzioni) {
    this.elencoGiunzioni = elencoGiunzioni;
  }

  /**
   * @param elencoGruppi
   *        elencoGruppi da settare internamente alla classe.
   */
  public void setElencoGruppi(Vector<GruppoRicerca> elencoGruppi) {
    this.elencoGruppi = elencoGruppi;
  }

  /**
   * @param elencoOrdinamenti
   *        elencoOrdinamenti da settare internamente alla classe.
   */
  public void setElencoOrdinamenti(Vector<OrdinamentoRicerca> elencoOrdinamenti) {
    this.elencoOrdinamenti = elencoOrdinamenti;
  }

  /**
   * @param elencoParametri
   *        elencoParametri da settare internamente alla classe.
   */
  public void setElencoParametri(Vector<ParametroRicerca> elencoParametri) {
    this.elencoParametri = elencoParametri;
  }
  
}