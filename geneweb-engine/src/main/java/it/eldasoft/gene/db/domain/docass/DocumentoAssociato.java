/*
 * Created on 19-lug-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.docass;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean per l'interfacciamento con la tabella C0OGGASS
 *
 * @author Luca.Giacomazzo
 */
public class DocumentoAssociato implements Serializable {

  /**   UID   */
  private static final long serialVersionUID = -2774185318800541507L;

  private long    id;
  private String  codApp;
  private String  entita;
  private String  campoChiave1;
  private String  campoChiave2;
  private String  campoChiave3;
  private String  campoChiave4;
  private String  campoChiave5;
  private Date    dataInserimento;
  private String  titolo;
  private String  pathDocAss;
  private Boolean documentoInAreaShared;
  private String  nomeDocAss;
  private Integer tipoAccesso;
  private String  annotazioni;
  private String  pubblicare;
  private Integer tipoPubblicazione;
  private String  subDirectoryDocAss;
  private Integer tipoVisibilita;
  private String  numeroProtocollo;
  private Date    dataProtocollo;
  private Date    dataScadenzaDocumento;
  private String  tipoDocumento;
  private String  numeroAtto;
  private Date    dataAtto;

  public DocumentoAssociato(){
    this.id = -1;
    this.codApp = null;
    this.entita = null;
    this.campoChiave1 = null;
    this.campoChiave2 = null;
    this.campoChiave3 = null;
    this.campoChiave4 = null;
    this.campoChiave5 = null;
    this.dataInserimento = null;
    this.titolo = null;
    this.pathDocAss = null;
    this.documentoInAreaShared = null;
    this.nomeDocAss = null;

    /** Questo campo non viene piu' gestito: su DB e' sempre null */
    this.tipoAccesso = null;
    this.annotazioni = null;
    this.pubblicare = null;
    this.tipoPubblicazione = null;
    this.subDirectoryDocAss = null;
    this.tipoVisibilita = null;
    this.numeroProtocollo = null;
    this.dataProtocollo = null;
    this.dataScadenzaDocumento = null;
    this.tipoDocumento = null;
    this.numeroAtto = null;
    this.dataAtto = null;
  }

  /**
   * @return Ritorna campoChiave1.
   */
  public String getCampoChiave1() {
    return campoChiave1;
  }


  /**
   * @param campoChiave1 campoChiave1 da settare internamente alla classe.
   */
  public void setCampoChiave1(String campoChiave1) {
    this.campoChiave1 = campoChiave1;
  }


  /**
   * @return Ritorna campoChiave2.
   */
  public String getCampoChiave2() {
    return campoChiave2;
  }


  /**
   * @param campoChiave2 campoChiave2 da settare internamente alla classe.
   */
  public void setCampoChiave2(String campoChiave2) {
    this.campoChiave2 = campoChiave2;
  }


  /**
   * @return Ritorna campoChiave3.
   */
  public String getCampoChiave3() {
    return campoChiave3;
  }


  /**
   * @param campoChiave3 campoChiave3 da settare internamente alla classe.
   */
  public void setCampoChiave3(String campoChiave3) {
    this.campoChiave3 = campoChiave3;
  }


  /**
   * @return Ritorna campoChiave4.
   */
  public String getCampoChiave4() {
    return campoChiave4;
  }


  /**
   * @param campoChiave4 campoChiave4 da settare internamente alla classe.
   */
  public void setCampoChiave4(String campoChiave4) {
    this.campoChiave4 = campoChiave4;
  }


  /**
   * @return Ritorna campoChiave5.
   */
  public String getCampoChiave5() {
    return campoChiave5;
  }


  /**
   * @param campoChiave5 campoChiave5 da settare internamente alla classe.
   */
  public void setCampoChiave5(String campoChiave5) {
    this.campoChiave5 = campoChiave5;
  }


  /**
   * @return Ritorna codApp.
   */
  public String getCodApp() {
    return codApp;
  }


  /**
   * @param codApp codApp da settare internamente alla classe.
   */
  public void setCodApp(String codApp) {
    this.codApp = codApp;
  }


  /**
   * @return Ritorna dataInserimento.
   */
  public Date getDataInserimento() {
    return dataInserimento;
  }


  /**
   * @param dataInserimento dataInserimento da settare internamente alla classe.
   */
  public void setDataInserimento(Date dataInserimento) {
    this.dataInserimento = dataInserimento;
  }


  /**
   * @return Ritorna entita.
   */
  public String getEntita() {
    return entita;
  }


  /**
   * @param entita entita da settare internamente alla classe.
   */
  public void setEntita(String entita) {
    this.entita = entita;
  }


  /**
   * @return Ritorna nomeDocAss.
   */
  public String getNomeDocAss() {
    return nomeDocAss;
  }


  /**
   * @param nomeDocAss nomeDocAss da settare internamente alla classe.
   */
  public void setNomeDocAss(String nomeDocAss) {
    this.nomeDocAss = nomeDocAss;
  }


  /**
   * @return Ritorna noteFile.
   */
  public String getAnnotazioni() {
    return annotazioni;
  }


  /**
   * @param noteFile noteFile da settare internamente alla classe.
   */
  public void setAnnotazioni(String noteFile) {
    this.annotazioni = noteFile;
  }


  /**
   * @return Ritorna numeroProtocollo.
   */
  public String getNumeroProtocollo() {
    return numeroProtocollo;
  }


  /**
   * @param numeroProtocollo numeroProtocollo da settare internamente alla classe.
   */
  public void setNumeroProtocollo(String numeroProtocollo) {
    this.numeroProtocollo = numeroProtocollo;
  }

  /**
   * @return Ritorna dataProtocollo.
   */
  public Date getDataProtocollo() {
    return dataProtocollo;
  }

  /**
   * @param dataProtocollo dataProtocollo da settare internamente alla classe.
   */
  public void setDataProtocollo(Date dataProtocollo) {
    this.dataProtocollo = dataProtocollo;
  }

  /**
   * @return Ritorna pathDocAss.
   */
  public String getPathDocAss() {
    return pathDocAss;
  }


  /**
   * @param pathDocAss pathDocAss da settare internamente alla classe.
   */
  public void setPathDocAss(String pathDocAss) {
    this.pathDocAss = pathDocAss;
  }


  /**
   * @return Ritorna progressivo.
   */
  public long getId() {
    return id;
  }


  /**
   * @param progressivo progressivo da settare internamente alla classe.
   */
  public void setId(long progressivo) {
    this.id = progressivo;
  }


  /**
   * @return Ritorna pubblicare.
   */
  public String getPubblicare() {
    return pubblicare;
  }


  /**
   * @param pubblicare pubblicare da settare internamente alla classe.
   */
  public void setPubblicare(String pubblicare) {
    this.pubblicare = pubblicare;
  }


  /**
   * @return Ritorna subDirectory.
   */
  public String getSubDirectoryDocAss() {
    return subDirectoryDocAss;
  }


  /**
   * @param subDirectory subDirectory da settare internamente alla classe.
   */
  public void setSubDirectoryDocAss(String subDirectory) {
    this.subDirectoryDocAss = subDirectory;
  }


  /**
   * @return Ritorna tipoAccesso.
   */
  public Integer getTipoAccesso() {
    return tipoAccesso;
  }


  /**
   * @param tipoAccesso tipoAccesso da settare internamente alla classe.
   */
  public void setTipoAccesso(Integer tipoAccesso) {
    this.tipoAccesso = tipoAccesso;
  }


  /**
   * @return Ritorna tipoPubblicazione.
   */
  public Integer getTipoPubblicazione() {
    return tipoPubblicazione;
  }


  /**
   * @param tipoPubblicazione tipoPubblicazione da settare internamente alla classe.
   */
  public void setTipoPubblicazione(Integer tipoPubblicazione) {
    this.tipoPubblicazione = tipoPubblicazione;
  }


  /**
   * @return Ritorna tipoVisibilita.
   */
  public Integer getTipoVisibilita() {
    return tipoVisibilita;
  }


  /**
   * @param tipoVisibilita tipoVisibilita da settare internamente alla classe.
   */
  public void setTipoVisibilita(Integer tipoVisibilita) {
    this.tipoVisibilita = tipoVisibilita;
  }


  /**
   * @return Ritorna titolo.
   */
  public String getTitolo() {
    return titolo;
  }


  /**
   * @param titolo titolo da settare internamente alla classe.
   */
  public void setTitolo(String titolo) {
    this.titolo = titolo;
  }

  /**
   * @return Ritorna documentoInAreaShared.
   */
  public Boolean getDocumentoInAreaShared() {
    return this.documentoInAreaShared;
  }

  /**
   * @param docInAreaShared docInAreaShared da settare internamente alla classe.
   */
  public void setDocumentoInAreaShared(Boolean docInAreaShared) {
    this.documentoInAreaShared = docInAreaShared;
  }

  /**
   * @return Ritorna dataScadenzaDocumento.
   */
  public Date getDataScadenzaDocumento() {
    return dataScadenzaDocumento;
  }

  /**
   * @param dataScadenzaDocumento dataScadenzaDocumento da settare internamente alla classe.
   */
  public void setDataScadenzaDocumento(Date dataScadenzaDocumento) {
    this.dataScadenzaDocumento = dataScadenzaDocumento;
  }

  /**
   * @return Ritorna tipoDocumento.
   */
  public String getTipoDocumento() {
    return tipoDocumento;
  }

  /**
   * @param tipoDocumento tipoDocumento da settare internamente alla classe.
   */
  public void setTipoDocumento(String tipoDocumento) {
    this.tipoDocumento = tipoDocumento;
  }

  /**
   * @return Ritorna numeroAtto.
   */
  public String getNumeroAtto() {
    return numeroAtto;
  }

  /**
   * @param numeroAtto numeroAtto da settare internamente alla classe.
   */
  public void setNumeroAtto(String numeroAtto) {
    this.numeroAtto = numeroAtto;
  }

  /**
   * @return Ritorna dataAtto.
   */
  public Date getDataAtto() {
    return dataAtto;
  }

  /**
   * @param dataAtto dataAtto da settare internamente alla classe.
   */
  public void setDataAtto(Date dataAtto) {
    this.dataAtto = dataAtto;
  }
}