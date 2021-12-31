/*
 * Created on 19-set-2006
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

/**
 * Bean per l'interazione con la tabella W_RICPARAM
 * @author Luca Giacomazzo
 */
public class ParametroRicerca implements Serializable {

  /**   UID   */
  private static final long serialVersionUID = -3524812712061785001L;

  private Integer id;
  private int progressivo;
  private String codice;
  private String nome;
  private String descrizione;
  private String tipo;
  private String tabCod;
  
  public ParametroRicerca(){
    this.id = null;
    this.progressivo = 0;
    this.codice = null;
    this.nome = null;
    this.descrizione = null;
    this.tipo = null;
    this.tabCod = null;
  }
  
  /**
   * Traduce il parametro di una ricerca in parametro di un modello.<br>
   * 
   * @return parametro di un modello, di tipo obbligatorio, in cui vengono
   *         valorizzati tutti gli attributi compatibili con il parametro di una
   *         ricerca
   *         
   * @since 1.5.0
   */
  public ParametroModello getParametroModello() {
    ParametroModello paramModello = new ParametroModello();
    paramModello.setCodice(this.getCodice());
    paramModello.setDescrizione(this.getDescrizione());
    paramModello.setNome(this.getNome());
    paramModello.setObbligatorio(1);
    paramModello.setTabellato(this.getTabCod());
    paramModello.setTipo(this.getTipo());
    return paramModello;
  }
  
  /**
   * @return Ritorna codice.
   */
  public String getCodice() {
    return codice;
  }
  
  /**
   * @param codice codice da settare internamente alla classe.
   */
  public void setCodice(String codice) {
    this.codice = codice;
  }
  
  /**
   * @return Ritorna descrizione.
   */
  public String getDescrizione() {
    return descrizione;
  }
  
  /**
   * @param descrizione descrizione da settare internamente alla classe.
   */
  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }
  
  /**
   * @return Ritorna id.
   */
  public Integer getId() {
    return id;
  }
  
  /**
   * @param id id da settare internamente alla classe.
   */
  public void setId(Integer id) {
    this.id = id;
  }
  
  /**
   * @return Ritorna nome.
   */
  public String getNome() {
    return nome;
  }
  
  /**
   * @param nome nome da settare internamente alla classe.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }
  
  /**
   * @return Ritorna progressivo.
   */
  public int getProgressivo() {
    return progressivo;
  }
  
  /**
   * @param progressivo progressivo da settare internamente alla classe.
   */
  public void setProgressivo(int progressivo) {
    this.progressivo = progressivo;
  }
  
  /**
   * @return Ritorna tabCod.
   */
  public String getTabCod() {
    return tabCod;
  }
  
  /**
   * @param tabCod tabCod da settare internamente alla classe.
   */
  public void setTabCod(String tabcod) {
    this.tabCod = tabcod;
  }
  
  /**
   * @return Ritorna tipo.
   */
  public String getTipo() {
    return tipo;
  }
  
  /**
   * @param tipo tipo da settare internamente alla classe.
   */
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }
}