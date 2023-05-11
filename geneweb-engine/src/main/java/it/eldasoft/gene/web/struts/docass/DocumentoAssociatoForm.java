/*
 * Created on 08-Nov-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.docass;

import it.eldasoft.gene.db.domain.docass.DocumentoAssociato;
import it.eldasoft.utils.utility.UtilityBool;
import it.eldasoft.utils.utility.UtilityDate;

import javax.servlet.ServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 * Form con i tutti i campi di un documento associato
 *
 * @author Luca Giacomazzo
 */
public class DocumentoAssociatoForm extends ActionForm {

  /**   UID   */
  private static final long serialVersionUID = 2612072101980020410L;

  private Long    id;
  private String  codApp;
  private String  entita;
  private String[] valoriCampiChiave;
  private String  dataInserimento;
  private String  titolo;
  private String  pathDocAss;
  private String  nomeDocAss;
  private Boolean documentoInAreaShared;
  private String  key;
  private String  keyParent;

  /** Questo campo non viene piu' gestito: su DB e' sempre null e quindi
   *  e' inutile portarlo a video*/
  private Integer tipoAccesso;
  private String  annotazioni;
  private Boolean pubblicare;
  private Integer tipoPubblicazione;
  private String  subDirectoryDocAss;
  private Integer tipoVisibilita;
  private String  numeroProtocollo;
  private String  dataProtocollo;

  private FormFile selezioneFile;

  private String  dataScadenzaDocumento;
  private String  tipoDocumento;
  private String  numeroAtto;
  private String  dataAtto;

  public DocumentoAssociatoForm(){
    super();
    this.inizializzaOggetto();
  }

  private void inizializzaOggetto(){
    this.id = null;
    this.codApp = null;
    this.entita = null;
    this.valoriCampiChiave = null;
    this.dataInserimento = null;
    this.titolo = null;
    this.pathDocAss = null;
    this.nomeDocAss = null;
    this.documentoInAreaShared = null;
    this.key = null;
    this.keyParent = null;
    this.tipoAccesso = null;
    this.annotazioni = null;
    this.pubblicare = null;
    this.tipoPubblicazione = null;

    /** Sottodirectory del file associato usato quanto il modello generato
     * ha delle immagini al suo interno*/
    this.subDirectoryDocAss = null;
    this.tipoVisibilita = null;
    this.numeroProtocollo = null;
    this.dataProtocollo = null;

    this.selezioneFile = null;

    this.dataScadenzaDocumento = null;
    this.tipoDocumento = null;
    this.numeroAtto = null;
    this.dataAtto = null;
  }

  public DocumentoAssociatoForm(DocumentoAssociato datiModel){
    this.id = new Long(datiModel.getId());
    this.codApp = datiModel.getCodApp();
    this.entita = datiModel.getEntita();
    this.valoriCampiChiave = new String[]{
        datiModel.getCampoChiave1() != null ? datiModel.getCampoChiave1() : "#",
        datiModel.getCampoChiave2() != null ? datiModel.getCampoChiave2() : "#",
        datiModel.getCampoChiave3() != null ? datiModel.getCampoChiave3() : "#",
        datiModel.getCampoChiave4() != null ? datiModel.getCampoChiave4() : "#",
        datiModel.getCampoChiave5() != null ? datiModel.getCampoChiave5() : "#"
      };
    this.dataInserimento = UtilityDate.convertiData(datiModel.getDataInserimento(),
        UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
    this.titolo = datiModel.getTitolo();
    this.pathDocAss = datiModel.getPathDocAss();
    this.nomeDocAss = datiModel.getNomeDocAss();
    this.documentoInAreaShared = datiModel.getDocumentoInAreaShared();
    this.tipoAccesso = datiModel.getTipoAccesso();
    this.annotazioni = datiModel.getAnnotazioni();
    this.pubblicare = UtilityBool.convertiBooleano(datiModel.getPubblicare());
    this.tipoPubblicazione = datiModel.getTipoPubblicazione();
    this.subDirectoryDocAss = datiModel.getSubDirectoryDocAss();
    this.tipoVisibilita = datiModel.getTipoVisibilita();
    this.numeroProtocollo = datiModel.getNumeroProtocollo();
    this.dataProtocollo = UtilityDate.convertiData(datiModel.getDataProtocollo(),
        UtilityDate.FORMATO_GG_MM_AAAA);

    this.selezioneFile = null;

    this.dataScadenzaDocumento = UtilityDate.convertiData(datiModel.getDataScadenzaDocumento(),
        UtilityDate.FORMATO_GG_MM_AAAA);
    this.tipoDocumento = datiModel.getTipoDocumento();

    this.numeroAtto = datiModel.getNumeroAtto();
    this.dataAtto = UtilityDate.convertiData(datiModel.getDataAtto(),
        UtilityDate.FORMATO_GG_MM_AAAA);
  }

  public DocumentoAssociato getDatiPerModel(){
    DocumentoAssociato documentoAssociato = new DocumentoAssociato();

    documentoAssociato.setId(this.id != null ? this.id.longValue() : -1);
    documentoAssociato.setCodApp(this.codApp);
    documentoAssociato.setEntita(this.entita);
    documentoAssociato.setCampoChiave1(this.valoriCampiChiave[0]);
    documentoAssociato.setCampoChiave2(this.valoriCampiChiave[1]);
    documentoAssociato.setCampoChiave3(this.valoriCampiChiave[2]);
    documentoAssociato.setCampoChiave4(this.valoriCampiChiave[3]);
    documentoAssociato.setCampoChiave5(this.valoriCampiChiave[4]);
    documentoAssociato.setDataInserimento(UtilityDate.convertiData(this.dataInserimento,
        UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    documentoAssociato.setTitolo(this.titolo);
    documentoAssociato.setPathDocAss(this.pathDocAss);
    documentoAssociato.setNomeDocAss(this.nomeDocAss);
    documentoAssociato.setTipoAccesso(this.tipoAccesso);
    documentoAssociato.setAnnotazioni(this.annotazioni);
    documentoAssociato.setPubblicare("");
    documentoAssociato.setTipoPubblicazione(this.tipoPubblicazione);
    documentoAssociato.setSubDirectoryDocAss(this.subDirectoryDocAss);
    documentoAssociato.setTipoVisibilita(this.tipoVisibilita);
    documentoAssociato.setNumeroProtocollo(this.numeroProtocollo);
    documentoAssociato.setDataProtocollo(UtilityDate.convertiData(this.dataProtocollo,
        UtilityDate.FORMATO_GG_MM_AAAA));
    documentoAssociato.setDataScadenzaDocumento(UtilityDate.convertiData(this.dataScadenzaDocumento,
        UtilityDate.FORMATO_GG_MM_AAAA));
    if (StringUtils.isNotBlank(this.tipoDocumento)) {
      documentoAssociato.setTipoDocumento(this.tipoDocumento);
    }
    documentoAssociato.setNumeroAtto(this.numeroAtto);
    documentoAssociato.setDataAtto(UtilityDate.convertiData(this.dataAtto,
        UtilityDate.FORMATO_GG_MM_AAAA));

    return documentoAssociato;
  }

  @Override
  public void reset(ActionMapping mapping, ServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }


  /**
   * @return Ritorna valoriCampiChiave.
   */
  public String[] getValoriCampiChiave() {
    return this.valoriCampiChiave;
  }


  /**
   * @param valoriCampiChiave valoriCampiChiave da settare internamente alla classe.
   */
  public void setValoriCampiChiave(String[] valoriCampiChiave) {
    this.valoriCampiChiave = valoriCampiChiave;
  }


  /**
   * @return Ritorna pubblicare.
   */
  public Boolean getPubblicare() {
    return pubblicare;
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
  public String getDataInserimento() {
    return dataInserimento;
  }


  /**
   * @param dataInserimento dataInserimento da settare internamente alla classe.
   */
  public void setDataInserimento(String dataInserimento) {
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
  public String getDataProtocollo() {
    return dataProtocollo;
  }

  /**
   * @param dataProtocollo dataProtocollo da settare internamente alla classe.
   */
  public void setDataProtocollo(String dataProtocollo) {
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
  public Long getId() {
    return id;
  }


  /**
   * @param progressivo progressivo da settare internamente alla classe.
   */
  public void setId(Long progressivo) {
    this.id = progressivo;
  }


  /**
   * @return Ritorna pubblicare.
   */
  public Boolean isPubblicare() {
    return pubblicare;
  }


  /**
   * @param pubblicare pubblicare da settare internamente alla classe.
   */
  public void setPubblicare(Boolean pubblicare) {
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
   * @return Ritorna selezioneFile.
   */
  public FormFile getSelezioneFile() {
    return selezioneFile;
  }


  /**
   * @param selezioneFile selezioneFile da settare internamente alla classe.
   */
  public void setSelezioneFile(FormFile selezioneFile) {
    this.selezioneFile = selezioneFile;
  }

  /**
   * @return Ritorna dataScadenzaDocumento.
   */
  public String getDataScadenzaDocumento() {
    return dataScadenzaDocumento;
  }

  /**
   * @param dataScadenzaDocumento dataScadenzaDocumento da settare internamente alla classe.
   */
  public void setDataScadenzaDocumento(String dataScadenzaDocumento) {
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
  public String getDataAtto() {
    return dataAtto;
  }

  /**
   * @param dataAtto dataAtto da settare internamente alla classe.
   */
  public void setDataAtto(String dataAtto) {
    this.dataAtto = dataAtto;
  }

  /**
   * @return Ritorna documentoInAreaShared.
   */
  public Boolean getDocumentoInAreaShared() {
    return this.documentoInAreaShared;
  }

  /**
   * @param documentoInAreaShared documentoInAreaShared da settare internamente alla classe.
   */
  public void setDocumentoInAreaShared(Boolean documentoInAreaShared) {
    this.documentoInAreaShared = documentoInAreaShared;
  }

  public Boolean isDocumentoInAreaShared(){
    return this.documentoInAreaShared;
  }

  /**
   * @return Ritorna key.
   */
  public String getKey() {
    return key;
  }

  /**
   * @param key key da settare internamente alla classe.
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * @return Ritorna keyParent.
   */
  public String getKeyParent() {
    return keyParent;
  }

  /**
   * @param keyParent keyParent da settare internamente alla classe.
   */
  public void setKeyParent(String keyParent) {
    this.keyParent = keyParent;
  }

}