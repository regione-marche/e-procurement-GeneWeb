/*
 * Created on 13-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.db.domain.schedric;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean per l'interfacciamento con i dati presenti nella tabella W_SCHEDRIC
 * 
 * @author Francesco.DeFilippis
 */
public class CodaSched implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = 944115733233619505L;

  private int               idCodaSched;
  private int               idSchedRic;
  private String            nomeSchedRic;
  private String            msg;
  private int               stato;
  private String            nomeFile;
  private int               esecutore;
  private int               idRicerca;
  private String            nomeRicerca;
  private String            descStato;
  private Date              dataEsec;
  private String            profiloOwner;
  private String            codiceApplicazione;

  public CodaSched() {
    this.idCodaSched = 0;
    this.idSchedRic = 0;
    this.nomeSchedRic = null;
    this.idRicerca = -1;
    this.nomeRicerca = null;
    this.msg = null;
    this.stato = 0;
    this.nomeFile = null;
    this.descStato = null;
    this.esecutore = 0;
    this.dataEsec = null;
    this.profiloOwner = null;
    this.codiceApplicazione = null;
  }
  
  /**
   * @return Returns the descStato.
   */
  public String getDescStato() {
    return descStato;
  }

  /**
   * @param descStato
   *        The descStato to set.
   */
  public void setDescStato(String descStato) {
    this.descStato = descStato;
  }

  /**
   * @return Returns the idRicerca.
   */
  public int getIdRicerca() {
    return idRicerca;
  }

  /**
   * @param idRicerca
   *        The idRicerca to set.
   */
  public void setIdRicerca(int idRicerca) {
    this.idRicerca = idRicerca;
  }

  /**
   * @return Returns the nomeRicerca.
   */
  public String getNomeRicerca() {
    return nomeRicerca;
  }

  /**
   * @param nomeRicerca
   *        The nomeRicerca to set.
   */
  public void setNomeRicerca(String nomeRicerca) {
    this.nomeRicerca = nomeRicerca;
  }

  /**
   * @return Returns the owner.
   */
  public int getEsecutore() {
    return esecutore;
  }

  /**
   * @param esecutore
   *        The owner to set.
   */
  public void setEsecutore(int esecutore) {
    this.esecutore = esecutore;
  }

  /**
   * @return Returns the idSchedric.
   */
  public int getIdSchedRic() {
    return idSchedRic;
  }

  /**
   * @param idSchedRic
   *        The idSchedRic to set.
   */
  public void setIdSchedRic(int idSchedRic) {
    this.idSchedRic = idSchedRic;
  }

  /**
   * @return Returns the idCodaSched.
   */
  public int getIdCodaSched() {
    return idCodaSched;
  }

  /**
   * @param idCodaSched
   *        The idCodaSched to set.
   */
  public void setIdCodaSched(int idCodaSched) {
    this.idCodaSched = idCodaSched;
  }

  /**
   * @return Ritorna nomeSchedRic.
   */
  public String getNomeSchedRic() {
    return nomeSchedRic;
  }

  /**
   * @param nomeSchedRic
   *        nomeSchedRic da settare internamente alla classe.
   */
  public void setNomeSchedRic(String nomeSchedRic) {
    this.nomeSchedRic = nomeSchedRic;
  }

  /**
   * @return Returns the msg.
   */
  public String getMsg() {
    return msg;
  }

  /**
   * @param msg
   *        The msg to set.
   */
  public void setMsg(String msg) {
    this.msg = msg;
  }

  /**
   * @return Returns the nomeFile.
   */
  public String getNomeFile() {
    return nomeFile;
  }

  /**
   * @param nomeFile
   *        The nomeFile to set.
   */
  public void setNomeFile(String nomeFile) {
    this.nomeFile = nomeFile;
  }

  /**
   * @return Returns the stato.
   */
  public int getStato() {
    return stato;
  }

  /**
   * @param stato
   *        The stato to set.
   */
  public void setStato(int stato) {
    this.stato = stato;
  }

  /**
   * @return Returns the dataEsec.
   */
  public Date getDataEsec() {
    return dataEsec;
  }

  /**
   * @param dataEsec
   *        The dataEsec to set.
   */
  public void setDataEsec(Date dataEsec) {
    this.dataEsec = dataEsec;
  }
  
  /**
   * @return Ritorna profiloOwner.
   */
  public String getProfiloOwner() {
    return profiloOwner;
  }
  
  /**
   * @param profiloOwner profiloOwner da settare internamente alla classe.
   */
  public void setProfiloOwner(String profiloOwner) {
    this.profiloOwner = profiloOwner;
  }
  
  /**
   * @return Ritorna codiceApplicazione.
   */
  public String getCodiceApplicazione() {
    return codiceApplicazione;
  }
  
  /**
   * @param codiceApplicazione codiceApplicazione da settare internamente alla classe.
   */
  public void setCodiceApplicazione(String codiceApplicazione) {
    this.codiceApplicazione = codiceApplicazione;
  }
  
}