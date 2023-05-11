/**
 * 
 */
package it.eldasoft.gene.db.domain.genric;

import it.eldasoft.gene.web.struts.genric.gruppo.GruppiRicercaForm;

import java.io.Serializable;


/**
 * Contenitore di dati relativi ad un report da importare. Viene usato per
 * raccogliere in un unico oggetto tutti i dati di un report e le informazioni
 * relative all tipo di operazione di import da effettuare durante il relativo
 * wizard.
 *  
 * @author Luca.Giacomazzo
 */
public class ContenitoreDatiImport implements Serializable {

  /**   UID   */
  private static final long serialVersionUID = -6420480069006615014L;
  
  private ContenitoreDatiRicerca   contenitoreDatiRicerca;
  private ContenitoreDatiProspetto contenitoreDatiProspetto;
  
  /** Flag per inidividuare se l'utente che esegue l'import del report e' il
   * proprietario o meno del report */
  private boolean                  utenteOwner;
  /** Flag per individuare se il report in importazione esiste già su DB */
  private boolean                  esisteReport;
  /** Flag per individuare se il report in importazione esiste già su DB e tale
   *  report e' pubblicato ad altri utenti */
  private boolean                  reportEsistentePubblicato;
  // variabile che indica se il report in importazione è della stessa famiglia
  // del report esistente su DB o meno
  /** Flag per individuare se il report in importazione e' della stessa famiglia
   * del report esistente su DB */
  private boolean                  famigliaUguale;
  /** Tipo di importazione: 4 valori possibili, specificati nella classe
   * CostantiWizard.java. In particolare:
   * - IMPORT_SOVRASCRIVI_ESISTENTE per l'update di un report esistente;
   * - IMPORT_SOVRASCRIVI_PARZIALE per l'update di un report esistente a meno
   *   delle informazioni relative alla pubblicazione e ai gruppi;
   * - IMPORT_INSERT_CON_NUOVO_TITOLO per l'inserimento di un report con un nuovo titolo;
   * - IMPORT_INSERT_NUOVO_REPORT per l'inserimento di un nuovo report */
  private String                   tipoImport;
  /** Flag per individuare se il report in importazione deve venir pubblicato
   *  ad altri utenti */
  private boolean                  pubblicaReport;
  private GruppiRicercaForm        gruppiRicercaForm;
  /** Nuovo titolo del report in impotazione */
  private String                   nuovoTitoloReport;
  
  public ContenitoreDatiImport(){
    this.utenteOwner               = false;
    this.esisteReport              = false;
    this.reportEsistentePubblicato = false;
    this.famigliaUguale            = false;
    this.tipoImport                = null;
    this.pubblicaReport            = false;
    this.gruppiRicercaForm         = null;
    this.nuovoTitoloReport         = null;
  }
  
  /**
   * @return Ritorna contenitoreDatiProspetto.
   */
  public ContenitoreDatiProspetto getContenitoreDatiProspetto() {
    return contenitoreDatiProspetto;
  }
  
  /**
   * @param contenitoreDatiProspetto contenitoreDatiProspetto da settare internamente alla classe.
   */
  public void setContenitoreDatiProspetto(
      ContenitoreDatiProspetto contenitoreDatiProspetto) {
    this.contenitoreDatiProspetto = contenitoreDatiProspetto;
  }
  
  /**
   * @return Ritorna contenitoreDatiRicerca.
   */
  public ContenitoreDatiRicerca getContenitoreDatiRicerca() {
    return contenitoreDatiRicerca;
  }
  
  /**
   * @param contenitoreDatiRicerca contenitoreDatiRicerca da settare internamente alla classe.
   */
  public void setContenitoreDatiRicerca(
      ContenitoreDatiRicerca contenitoreDatiRicerca) {
    this.contenitoreDatiRicerca = contenitoreDatiRicerca;
  }
  
  /**
   * @return Ritorna isReportEsistente.
   */
  public boolean isEsisteReport() {
    return esisteReport;
  }
  
  /**
   * @return Ritorna isReportEsistente.
   */
  public boolean getEsisteReport() {
    return esisteReport;
  }
  
  /**
   * @param isReportEsistente isReportEsistente da settare internamente alla classe.
   */
  public void setEsisteReport(boolean isReportEsistente) {
    this.esisteReport = isReportEsistente;
  }
  
  /**
   * @return Ritorna isUtenteOwner.
   */
  public boolean isUtenteOwner() {
    return utenteOwner;
  }
  
  /**
   * @return Ritorna isUtenteOwner.
   */
  public boolean getUtenteOwner() {
    return utenteOwner;
  }
  
  /**
   * @param isUtenteOwner isUtenteOwner da settare internamente alla classe.
   */
  public void setUtenteOwner(boolean isUtenteOwner) {
    this.utenteOwner = isUtenteOwner;
  }
  
  /**
   * @return Ritorna pubblicareReport.
   */
  public boolean isPubblicaReport() {
    return pubblicaReport;
  }
  
  /**
   * @return Ritorna pubblicareReport.
   */
  public boolean getPubblicaReport() {
    return pubblicaReport;
  }
  
  /**
   * @param pubblicareReport pubblicareReport da settare internamente alla classe.
   */
  public void setPubblicaReport(boolean pubblicareReport) {
    this.pubblicaReport = pubblicareReport;
  }

  /**
   * @return Ritorna tipoDiImport.
   */
  public String getTipoImport() {
    return tipoImport;
  }

  /**
   * @param tipoDiImport tipoDiImport da settare internamente alla classe.
   */
  public void setTipoImport(String tipoDiImport) {
    this.tipoImport = tipoDiImport;
  }

  /**
   * @return Ritorna gruppiRicercaForm.
   */
  public GruppiRicercaForm getGruppiRicercaForm() {
    return gruppiRicercaForm;
  }
  
  /**
   * @param gruppiRicercaForm gruppiRicercaForm da settare internamente alla classe.
   */
  public void setGruppiRicercaForm(GruppiRicercaForm gruppiRicercaForm) {
    this.gruppiRicercaForm = gruppiRicercaForm;
  }

  /**
   * @return Ritorna nuovoTitoloReport.
   */
  public String getNuovoTitoloReport() {
    return nuovoTitoloReport;
  }
  
  /**
   * @param nuovoTitoloReport nuovoTitoloReport da settare internamente alla classe.
   */
  public void setNuovoTitoloReport(String nuovoTitoloReport) {
    this.nuovoTitoloReport = nuovoTitoloReport;
  }
  
  /**
   * @return Ritorna famiglieDiversa.
   */
  public boolean isFamigliaUguale() {
    return famigliaUguale;
  }
  
  /**
   * @return Ritorna famiglieDiversa.
   */
  public boolean getFamigliaDiversa() {
    return famigliaUguale;
  }
  
  /**
   * @param famiglieDiversa famiglieDiversa da settare internamente alla classe.
   */
  public void setFamigliaUguale(boolean famiglieDiversa) {
    this.famigliaUguale = famiglieDiversa;
  }
  
  /**
   * @return Ritorna reportPubblicato.
   */
  public boolean isReportEsistentePubblicato() {
    return reportEsistentePubblicato;
  }
  
  /**
   * @return Ritorna reportPubblicato.
   */
  public boolean getReportEsistentePubblicato() {
    return reportEsistentePubblicato;
  }
  
  /**
   * @param reportPubblicato reportPubblicato da settare internamente alla classe.
   */
  public void setReportEsistentePubblicato(boolean reportPubblicato) {
    this.reportEsistentePubblicato = reportPubblicato;
  }
  
}