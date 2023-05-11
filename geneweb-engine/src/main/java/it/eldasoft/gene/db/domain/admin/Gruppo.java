/*
 * Created on 28-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.admin;

import java.io.Serializable;

/**
 * Bean per l'interfacciamento con i dati presenti nella tabella W_GRUPPI
 * 
 * @author Luca.Giacomazzo
 */
public class Gruppo implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = 5727241108074887540L;

  /** chiave primaria */
  private int               idGruppo;
  
  /** nome del gruppo */
  private String            nomeGruppo;

  /** descrizione del gruppo */
  private String            descrGruppo;

  public Gruppo() {
    this.idGruppo = -1;
    this.nomeGruppo = null;
    this.descrGruppo = null;
  }

  /**
   * @return Ritorna idGruppo.
   */
  public int getIdGruppo() {
    return idGruppo;
  }

  /**
   * @param idGruppo
   *        idGruppo da settare internamente alla classe.
   */
  public void setIdGruppo(int idGruppo) {
    this.idGruppo = idGruppo;
  }

  /**
   * @return Ritorna nomeGruppo.
   */
  public String getNomeGruppo() {
    return nomeGruppo;
  }

  /**
   * @param nomeGruppo
   *        nomeGruppo da settare internamente alla classe.
   */
  public void setNomeGruppo(String nomeGruppo) {
    this.nomeGruppo = nomeGruppo;
  }

  /**
   * @return Ritorna descrGruppo.
   */
  public String getDescrGruppo() {
    return descrGruppo;
  }

  /**
   * @param descrGruppo
   *        descrGruppo da settare internamente alla classe.
   */
  public void setDescrGruppo(String descrGruppo) {
    this.descrGruppo = descrGruppo;
  }
}
