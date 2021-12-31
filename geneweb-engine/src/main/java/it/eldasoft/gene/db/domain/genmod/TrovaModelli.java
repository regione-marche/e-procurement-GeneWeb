/*
 * Created on 03-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.genmod;

import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;

import java.io.Serializable;

public class TrovaModelli implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = -8421383261717015283L;

  private String            tipoDocumento;
  private String            nomeModello;
  private String            operatoreNomeModello;
  private String            escapeNomeModello;
  private String            descrModello;
  private String            operatoreDescrModello;
  private String            escapeDescrModello;
  private String            fileModello;
  private String            escapeFileModello;
  private String            operatoreFileModello;
  private String            disponibile;
  private String            idGruppo;
  private String            codiceApplicazione;
  private boolean           noCaseSensitive;
  private String            personale;
  private String            owner;
  private String            codiceProfiloAttivo;

  /**
   * Costruttore vuoto
   */
  public TrovaModelli() {
    // ATTENZIONE: gli operatori nell'oggetto di trova di default sono impostati
    // a
    // "uguale" in modo da effettuare il match esatto

    this.tipoDocumento = null;
    this.nomeModello = null;
    this.operatoreNomeModello = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[0];
    this.escapeNomeModello = null;
    this.descrModello = null;
    this.operatoreDescrModello = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[0];
    this.escapeDescrModello = null;
    this.fileModello = null;
    this.operatoreFileModello = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[0];
    this.escapeFileModello = null;
    this.disponibile = null;
    this.idGruppo = null;
    this.codiceApplicazione = null;
    this.noCaseSensitive = false;
    this.personale = null;
    this.owner = null;
    this.codiceProfiloAttivo = null;
  }

  /**
   * @return Returns the disponibile.
   */
  public String getDisponibile() {
    return disponibile;
  }

  /**
   * @param disponibile
   *        The disponibile to set.
   */
  public void setDisponibile(String disponibile) {
    this.disponibile = disponibile;
  }

  /**
   * @return Returns the fileModello.
   */
  public String getFileModello() {
    return fileModello;
  }

  /**
   * @param fileModello
   *        The fileModello to set.
   */
  public void setFileModello(String fileModello) {
    this.fileModello = fileModello;
  }

  /**
   * @return Returns the idGruppo.
   */
  public String getIdGruppo() {
    return idGruppo;
  }

  /**
   * @param idGruppo
   *        The idGruppo to set.
   */
  public void setIdGruppo(String idGruppo) {
    this.idGruppo = idGruppo;
  }

  /**
   * @return Returns the nomeModello.
   */
  public String getNomeModello() {
    return nomeModello;
  }

  /**
   * @param nomeModello
   *        The nomeModello to set.
   */
  public void setNomeModello(String nomeModello) {
    this.nomeModello = nomeModello;
  }

  /**
   * @return Returns the tipoDocumento.
   */
  public String getTipoDocumento() {
    return tipoDocumento;
  }

  /**
   * @param tipoDocumento
   *        The tipoDocumento to set.
   */
  public void setTipoDocumento(String tipoDocumento) {
    this.tipoDocumento = tipoDocumento;
  }

  /**
   * @return Ritorna codiceApplicazione.
   */
  public String getCodiceApplicazione() {
    return codiceApplicazione;
  }

  /**
   * @param codiceApplicazione
   *        codiceApplicazione da settare internamente alla classe.
   */
  public void setCodiceApplicazione(String codiceApplicazione) {
    this.codiceApplicazione = codiceApplicazione;
  }

  /**
   * @return Ritorna caseSensitive.
   */
  public boolean getNoCaseSensitive() {
    return noCaseSensitive;
  }

  /**
   * @return Ritorna caseSensitive.
   */
  public boolean isNoCaseSensitive() {
    return noCaseSensitive;
  }

  /**
   * @param caseSensitive
   *        caseSensitive da settare internamente alla classe.
   */
  public void setNoCaseSensitive(boolean noCaseSensitive) {
    this.noCaseSensitive = noCaseSensitive;
  }

  /**
   * @return Returns the owner.
   */
  public String getOwner() {
    return owner;
  }

  /**
   * @param owner
   *        The owner to set.
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * @return Returns the personale.
   */
  public String getPersonale() {
    return personale;
  }

  /**
   * @param personale
   *        The personale to set.
   */
  public void setPersonale(String personale) {
    this.personale = personale;
  }

  /**
   * @return Returns the descrModello.
   */
  public String getDescrModello() {
    return descrModello;
  }

  /**
   * @param descrModello
   *        The descrModello to set.
   */
  public void setDescrModello(String descrModello) {
    this.descrModello = descrModello;
  }

  /**
   * @return Ritorna codiceProfiloAttivo.
   */
  public String getCodiceProfiloAttivo() {
    return codiceProfiloAttivo;
  }

  /**
   * @param codiceProfiloAttivo
   *        codiceProfiloAttivo da settare internamente alla classe.
   */
  public void setCodiceProfiloAttivo(String codiceProfiloAttivo) {
    this.codiceProfiloAttivo = codiceProfiloAttivo;
  }

  /**
   * @return Ritorna operatoreDescrModello.
   */
  public String getOperatoreDescrModello() {
    return operatoreDescrModello;
  }

  /**
   * @param operatoreDescrModello
   *        operatoreDescrModello da settare internamente alla classe.
   */
  public void setOperatoreDescrModello(String operatoreDescrModello) {
    this.operatoreDescrModello = operatoreDescrModello;
  }

  /**
   * @return Ritorna operatoreFileModello.
   */
  public String getOperatoreFileModello() {
    return operatoreFileModello;
  }

  /**
   * @param operatoreFileModello
   *        operatoreFileModello da settare internamente alla classe.
   */
  public void setOperatoreFileModello(String operatoreFileModello) {
    this.operatoreFileModello = operatoreFileModello;
  }

  /**
   * @return Ritorna operatoreNomeModello.
   */
  public String getOperatoreNomeModello() {
    return operatoreNomeModello;
  }

  /**
   * @param operatoreNomeModello
   *        operatoreNomeModello da settare internamente alla classe.
   */
  public void setOperatoreNomeModello(String operatoreNomeModello) {
    this.operatoreNomeModello = operatoreNomeModello;
  }

  /**
   * @return Ritorna escapeNomeModello.
   */
  public String getEscapeNomeModello() {
    return escapeNomeModello;
  }

  /**
   * @param escapeNomeModello
   *        escapeNomeModello da settare internamente alla classe.
   */
  public void setEscapeNomeModello(String escapeNomeModello) {
    this.escapeNomeModello = escapeNomeModello;
  }

  /**
   * @return Ritorna escapeDescrModello.
   */
  public String getEscapeDescrModello() {
    return escapeDescrModello;
  }

  /**
   * @param escapeDescrModello
   *        escapeDescrModello da settare internamente alla classe.
   */
  public void setEscapeDescrModello(String escapeDescrModello) {
    this.escapeDescrModello = escapeDescrModello;
  }

  /**
   * @return Ritorna escapeFileModello.
   */
  public String getEscapeFileModello() {
    return escapeFileModello;
  }

  /**
   * @param escapeFileModello
   *        escapeFileModello da settare internamente alla classe.
   */
  public void setEscapeFileModello(String escapeFileModello) {
    this.escapeFileModello = escapeFileModello;
  }

}