/**
 * 
 */
package it.eldasoft.gene.db.domain.genmod;

import it.eldasoft.gene.web.struts.genmod.impexp.GruppiModelliImportForm;

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
  
  private ContenitoreDatiModello contenitoreDatiGenerali;
  
  
  private boolean                  utenteOwner;
  private boolean                  esisteModello;
  private int                      modelloEsistenteDisponibile;

  private String                   tipoImport;
  private boolean                  pubblicaNuovoModello;
  private GruppiModelliImportForm  gruppiModelliForm;
  private String                   nuovoTitoloModello;
  
  public ContenitoreDatiImport(){
    this.utenteOwner            = false;
    this.esisteModello           = false;
    this.modelloEsistenteDisponibile = 0;
    this.tipoImport             = null;
    this.pubblicaNuovoModello = false;
    this.gruppiModelliForm      = null;
    this.nuovoTitoloModello      = null;
  }

  
  /**
   * @return Returns the contenitoreDatiGenerali.
   */
  public ContenitoreDatiModello getContenitoreDatiGenerali() {
    return contenitoreDatiGenerali;
  }

  
  /**
   * @param contenitoreDatiGenerali The contenitoreDatiGenerali to set.
   */
  public void setContenitoreDatiGenerali(ContenitoreDatiModello contenitoreDatiGenerali) {
    this.contenitoreDatiGenerali = contenitoreDatiGenerali;
  }

  
  /**
   * @return Returns the esisteModello.
   */
  public boolean isEsisteModello() {
    return esisteModello;
  }

  
  /**
   * @param esisteModello The esisteModello to set.
   */
  public void setEsisteModello(boolean esisteModello) {
    this.esisteModello = esisteModello;
  }

  
  /**
   * @return Returns the gruppiModelliForm.
   */
  public GruppiModelliImportForm getGruppiModelliForm() {
    return gruppiModelliForm;
  }

  
  /**
   * @param gruppiModelliForm The gruppiModelliForm to set.
   */
  public void setGruppiModelliForm(GruppiModelliImportForm gruppiModelliForm) {
    this.gruppiModelliForm = gruppiModelliForm;
  }

  
  /**
   * @return Returns the modelloEsistentePubblicato.
   */
  public int getModelloEsistenteDisponibile() {
    return modelloEsistenteDisponibile;
  }

  
  /**
   * @param modelloEsistentePubblicato The modelloEsistentePubblicato to set.
   */
  public void setModelloEsistenteDisponibile(int modelloEsistenteDisponibile) {
    this.modelloEsistenteDisponibile = modelloEsistenteDisponibile;
  }

  
  /**
   * @return Returns the nuovoTitoloReport.
   */
  public String getNuovoTitoloModello() {
    return nuovoTitoloModello;
  }

  
  /**
   * @param nuovoTitoloReport The nuovoTitoloReport to set.
   */
  public void setNuovoTitoloModello(String nuovoTitoloModello) {
    this.nuovoTitoloModello = nuovoTitoloModello;
  }

  
  /**
   * @return Returns the pubblicaNuovoModello.
   */
  public boolean isPubblicaNuovoModello() {
    return pubblicaNuovoModello;
  }

  
  /**
   * @param pubblicaNuovoModello The pubblicaNuovoModello to set.
   */
  public void setPubblicaNuovoModello(boolean pubblicaNuovoModello) {
    this.pubblicaNuovoModello = pubblicaNuovoModello;
  }

  
  /**
   * @return Returns the tipoImport.
   */
  public String getTipoImport() {
    return tipoImport;
  }

  
  /**
   * @param tipoImport The tipoImport to set.
   */
  public void setTipoImport(String tipoImport) {
    this.tipoImport = tipoImport;
  }

  
  /**
   * @return Returns the utenteOwner.
   */
  public boolean isUtenteOwner() {
    return utenteOwner;
  }

  
  /**
   * @param utenteOwner The utenteOwner to set.
   */
  public void setUtenteOwner(boolean utenteOwner) {
    this.utenteOwner = utenteOwner;
  }
  
  
  
}