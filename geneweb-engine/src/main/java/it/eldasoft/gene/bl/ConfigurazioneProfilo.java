/*
 * Created on 08/ott/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl;

import it.eldasoft.utils.utility.UtilityHashMap;

import java.util.HashMap;

/**
 * Profilo di una hask Map
 * 
 * @author Marco.Franceschin
 * 
 */
public class ConfigurazioneProfilo extends HashMap<String, Object> {

  /** UID */
  private static final long serialVersionUID = 1L;
  private String            idProfilo;
  private boolean           ok;
  private String            nome;

  public class ValoreDatoProfilo {

    /** Valore della protezione */
    private boolean valore;
    /** Flag che dice se il CRC è OK */
    private boolean ok;
    /** Flag che dice se è nei valori di default */
    private boolean isdefault;
    /** Valore di default */
    private Long    valDefault;
    /** Flag che dice se il CRC di default è ok */
    private boolean okDefault;

    public ValoreDatoProfilo(boolean valore, boolean isDefault, boolean ok,
        Long valDefault) {
      this.valore = valore;
      this.ok = ok;
      if (isDefault) this.setOkDefault(ok);
      this.valDefault = valDefault;
      this.isdefault = isDefault;
    }

    /**
     * @return the valore
     */
    public boolean isValore() {
      return valore;
    }

    /**
     * @return the ok
     */
    public boolean isOk() {
      return ok;
    }

    /**
     * @return the valDefault
     */
    public Long getValDefault() {
      return valDefault;
    }

    /**
     * @return the isdefault
     */
    public boolean isDefault() {
      return isdefault;
    }

    /**
     * @param valDefault
     *        the valDefault to set
     */
    public void setValDefault(Long valDefault) {
      this.valDefault = valDefault;
    }

    /**
     * @return the okDefault
     */
    public boolean isOkDefault() {
      return okDefault;
    }

    /**
     * @param okDefault
     *        the okDefault to set
     */
    public void setOkDefault(boolean okDefault) {
      this.okDefault = okDefault;
    }

    public String toString() {
      return this.isValore() ? "true" : "false";
    }

  }

  /**
   * @param id
   *        Identificativo del profilo
   */
  public ConfigurazioneProfilo(String id, String nome, boolean ok) {
    this.idProfilo = id;
    this.nome = nome;
    this.setOk(ok);
  }

  /**
   * @return the idProfilo
   */
  public String getIdProfilo() {
    return idProfilo;
  }

  /**
   * Funzione che aggiunge una protezione al profulo utente
   * 
   * @param tipo
   *        Tipo di oggetto
   * @param azione
   *        Azione sull'oggetto
   * @param oggetto
   *        Nome dell'oggetto che può anche essere diviso da *
   * @param valore
   *        di protezione da impostare
   * @param isDefault
   * @param ok
   *        Flag di validità con il CRC
   * @param valDefault
   *        Valore impostato di default
   * 
   */
  public void addProtec(String tipo, String azione, String oggetto,
      boolean valore, boolean isDefault, boolean ok, Long valDefault) {
    String key;
    if (tipo == null || tipo.length() == 0) tipo = "NULL";
    if (azione == null || azione.length() == 0) azione = "NULL";
    if (oggetto == null || oggetto.length() == 0) oggetto = "*";
    key = tipo + "." + azione + "." + oggetto;
    // Se il valore di default e' nullo allora verifico se esiste
    // gia' sulla mappa e prendo il valore di default precedente
    ValoreDatoProfilo profAdd = new ValoreDatoProfilo(valore, isDefault, ok, valDefault);
    if (!isDefault) {
      // Se non e' un default cerco se esiste gia come default
      ValoreDatoProfilo valTmp = (ValoreDatoProfilo) this.get(key);
      if (valTmp != null && valTmp.isDefault()) {
        profAdd.setValDefault(valTmp.getValDefault());
        profAdd.setOkDefault(valTmp.isOkDefault());
      }
    }
    this.put(key, profAdd);
    // Se il dato non e' ok allora setto il velore come non OK
    if (!ok) this.setOk(false);
  }

  public ValoreDatoProfilo getValDato(String tipo, String azione, String oggetto) {
    if (tipo == null || tipo.length() == 0) tipo = "NULL";
    if (azione == null || azione.length() == 0) azione = "NULL";
    if (oggetto == null || oggetto.length() == 0) oggetto = "*";
    Object ret = this.get(tipo + "." + azione + "." + oggetto);
    if (ret != null && ret instanceof ValoreDatoProfilo)
      return (ValoreDatoProfilo) ret;
    return null;
  }

  public ValoreDatoProfilo getValore(String tipo, String azione, String oggetto) {
    if (tipo == null || tipo.length() == 0) tipo = "NULL";
    if (azione == null || azione.length() == 0) azione = "NULL";
    if (oggetto == null || oggetto.length() == 0) oggetto = "*";
    Object ret = UtilityHashMap.getObjectParent((HashMap) this, tipo
        + "."
        + azione, oggetto, true);
    if (ret != null && ret instanceof ValoreDatoProfilo)
      return (ValoreDatoProfilo) ret;
    return null;

  }

  /**
   * Funzione che restituisce il valore di una protezione
   * 
   * @param tipo
   *        Tipo di oggetto
   * @param azione
   *        Azione sull'oggetto
   * @param oggetto
   *        Nome dell'oggetto
   * @param defaultVal
   *        valore di default
   * 
   * @return Valore impostato nelle protezioni. Se non trovato restituisce false
   *         di default
   */
  public boolean getProtec(String tipo, String azione, String oggetto,
      boolean defaultVal) {
    ValoreDatoProfilo val = getValore(tipo, azione, oggetto);
    if (val != null) return val.isValore();
    return defaultVal;
  }

  /**
   * @return the ok
   */
  public boolean isOk() {
    return ok;
  }

  /**
   * @param ok
   *        the ok to set
   */
  public void setOk(boolean ok) {
    this.ok = ok;
  }

  /**
   * @return the descr
   */
  public String getNome() {
    return nome;
  }

  /**
   * @param descr
   *        the descr to set
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

}
