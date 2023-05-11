/*
 * Created on 29-nov-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.genmod;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * Classe che permette la memorizzazione di un parametro attribuito ad un
 * modello
 *
 * @author Stefano.Sabbadin
 */
public class ParametroModello implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = 3051144366448794092L;

  /** id univoco del modello */
  private int               idModello;

  /** progressivo univoco del parametro per il modello */
  private int               progressivo;

  /** codice del parametro */
  private String            codice;

  /** nome del parametro, ovvero titolo che compare al suo prompt */
  private String            nome;

  /** descrizione del parametro */
  private String            descrizione;

  /** tipo di parametro */
  private String            tipo;

  /** elenco separato da "|" di possibili opzioni a scelta */
  private String            menu;

  /** 1 se il campo è obbligatorio, 0 altrimenti */
  private int               obbligatorio;

  /** codice tabllato del parametro */
  private String            tabellato;

  public ParametroModello() {
    this.idModello = 0;
    this.progressivo = 0;
    this.codice = null;
    this.nome = null;
    this.descrizione = null;
    this.tipo = null;
    this.menu = null;
    this.obbligatorio = 0;
    this.tabellato = null;
  }

  /**
   * @return Ritorna codice.
   */
  public String getCodice() {
    return codice;
  }

  /**
   * @param codice
   *        codice da settare internamente alla classe.
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
   * @param descrizione
   *        descrizione da settare internamente alla classe.
   */
  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  /**
   * @return Ritorna idModello.
   */
  public int getIdModello() {
    return idModello;
  }

  /**
   * @param idModello
   *        idModello da settare internamente alla classe.
   */
  public void setIdModello(int idModello) {
    this.idModello = idModello;
  }

  /**
   * @return Ritorna menu.
   */
  public String getMenu() {
    return menu;
  }

  /**
   * @param menu
   *        menu da settare internamente alla classe.
   */
  public void setMenu(String menu) {
    this.menu = menu;
  }

  /**
   * @return Ritorna nome.
   */
  public String getNome() {
    return nome;
  }

  /**
   * Funzione che restituisce il nome del parametro con convertiti gli apici
   * singoli per i javascript
   *
   * @return Stringa con il replace degli apici singoli
   */
  public String getNomePerJs() {
    if (this.nome == null) return null;
    return StringUtils.replace(this.nome, "'", "\\\'");
  }

  /**
   * @param nome
   *        nome da settare internamente alla classe.
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
   * @param progressivo
   *        progressivo da settare internamente alla classe.
   */
  public void setProgressivo(int progressivo) {
    this.progressivo = progressivo;
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
   * @return Ritorna obbligatorio.
   */
  public int getObbligatorio() {
    return obbligatorio;
  }


  /**
   * @param obbligatorio obbligatorio da settare internamente alla classe.
   */
  public void setObbligatorio(int obbligatorio) {
    this.obbligatorio = obbligatorio;
  }


  /**
   * @return Returns the tabellato.
   */
  public String getTabellato() {
    return tabellato;
  }


  /**
   * @param tabellato The tabellato to set.
   */
  public void setTabellato(String tabellato) {
    this.tabellato = tabellato;
  }



}
