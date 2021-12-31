/*
 * Created on 09-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Classe form per la composizione di un modello
 * 
 * @author marco.franceschin
 */
public class ComponiModelloForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID = -6637383076410305005L;

  private int               idModello;
  private String            tipo;
  private String            entita;
  private String            nomeChiavi;
  private String[]          valChiavi;
  private String            valori;
  private String            fileComposto;
  private String            nomeModello;
  private int               idSessione;
  private int               noFiltroEntitaPrincipale;
  private String            paginaSorgente;
  private int               riepilogativo;
  private Integer           idRicerca;
  private int               exportPdf;

  public ComponiModelloForm() {
    super();
    this.inizializzaOggetto();
  }

  private void inizializzaOggetto() {
    this.idModello = 0;
    this.tipo = null;
    this.entita = null;
    this.nomeChiavi = null;
    this.valChiavi = null;
    this.valori = null;
    this.fileComposto = null;
    this.nomeModello = null;
    this.idSessione = 0;
    this.noFiltroEntitaPrincipale = 0;
    this.paginaSorgente = null;
    this.riepilogativo = 0;
    this.idRicerca = null;
    this.exportPdf = 0;
  }

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }

  /**
   * @return Returns the entita.
   */
  public String getEntita() {
    return entita;
  }

  /**
   * @param entita
   *        The entita to set.
   */
  public void setEntita(String entita) {
    this.entita = entita;
  }

  /**
   * @return Returns the idModello.
   */
  public int getIdModello() {
    return idModello;
  }

  /**
   * @param idModello
   *        The idModello to set.
   */
  public void setIdModello(int idModello) {
    this.idModello = idModello;
  }

  /**
   * @return Returns the nomeChiavi.
   */
  public String getNomeChiavi() {
    return nomeChiavi;
  }

  /**
   * @param nomeChiavi
   *        The nomeChiavi to set.
   */
  public void setNomeChiavi(String nomeChiavi) {
    this.nomeChiavi = nomeChiavi;
  }

  /**
   * @return Returns the valChiavi.
   */
  public String[] getValChiavi() {
    return valChiavi;
  }

  /**
   * @param valChiavi
   *        The valChiavi to set.
   */
  public void setValChiavi(String[] valChiavi) {
    this.valChiavi = valChiavi;
  }

  /**
   * @return Returns the valori.
   */
  public String getValori() {
    return valori;
  }

  /**
   * @param valori
   *        The valori to set.
   */
  public void setValori(String valori) {
    this.valori = valori;
  }

  /**
   * @return Returns the fileComposto.
   */
  public String getFileComposto() {
    return fileComposto;
  }

  /**
   * @param fileComposto
   *        The fileComposto to set.
   */
  public void setFileComposto(String fileComposto) {
    this.fileComposto = fileComposto;
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
   * @return Ritorna idSessione.
   */
  public int getIdSessione() {
    return idSessione;
  }

  /**
   * @param idSessione
   *        idSessione da settare internamente alla classe.
   */
  public void setIdSessione(int idSessione) {
    this.idSessione = idSessione;
  }

  /**
   * Funzione che restituisce il nome del modello con convertiti gli apici
   * singoli per i javascript
   * 
   * @return Stringa con il replace degli apici singoli
   */
  public String getNomeModelloPerJs() {
    if (this.nomeModello == null) return null;
    return UtilityStringhe.replace(this.nomeModello, "'", "\\\'");
  }

  /**
   * @return Ritorna noFiltroEntitaPrincipale.
   */
  public int getNoFiltroEntitaPrincipale() {
    return noFiltroEntitaPrincipale;
  }

  /**
   * @param noFiltroEntitaPrincipale
   *        noFiltroEntitaPrincipale da settare internamente alla classe.
   */
  public void setNoFiltroEntitaPrincipale(int noFiltroEntitaPrincipale) {
    this.noFiltroEntitaPrincipale = noFiltroEntitaPrincipale;
  }

  /**
   * @return Ritorna paginaSorgente.
   */
  public String getPaginaSorgente() {
    return paginaSorgente;
  }

  /**
   * @param paginaSorgente
   *        paginaSorgente da settare internamente alla classe.
   */
  public void setPaginaSorgente(String paginaSorgente) {
    this.paginaSorgente = paginaSorgente;
  }

  /**
   * @return Ritorna riepilogativo.
   */
  public int getRiepilogativo() {
    return riepilogativo;
  }

  /**
   * @param riepilogativo
   *        riepilogativo da settare internamente alla classe.
   */
  public void setRiepilogativo(int riepilogativo) {
    this.riepilogativo = riepilogativo;
  }

  /**
   * @return Ritorna tipo.
   */
  public String getTipo() {
    return tipo;
  }

  /**
   * @param tipo
   *        tipo da settare internamente alla classe.
   */
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  /**
   * @return Ritorna idRicerca.
   */
  public Integer getIdRicerca() {
    return idRicerca;
  }

  /**
   * @param idRicerca
   *        idRicerca da settare internamente alla classe.
   */
  public void setIdRicerca(Integer idRicerca) {
    this.idRicerca = idRicerca;
  }
  
  /**
   * @return Ritorna exportPdf.
   */
  public int getExportPdf() {
    return exportPdf;
  }

  /**
   * @param exportPdf exportPdf da settare internamente alla classe.
   */
  public void setExportPdf(int exportPdf) {
    this.exportPdf = exportPdf;
  }

}