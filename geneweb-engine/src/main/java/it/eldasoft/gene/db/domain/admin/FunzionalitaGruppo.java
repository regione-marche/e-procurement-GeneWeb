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

/**
 * Bean per l'interfacciamento con i dati presenti nelle tabelle W_FUNZGRP
 * 
 * @author Luca.Giacomazzo
 */
public class FunzionalitaGruppo {

  /**
   * UID
   */
  private static final long serialVersionUID = -8092633173197438326L;

  /** id del gruppo */
  private int               idGruppo         = -1;

  /** funzionalita' abilitata al gruppo */
  private String            funzionalita     = null;

  /**
   * codice applicazione per cui vale l'attribuzione della funzionalità al
   * gruppo
   */
  private String            codiceApplicazione;

  /**
   * Costruttore vuoto
   */
  public FunzionalitaGruppo() {
    this.idGruppo = -1;
    this.codiceApplicazione = null;
    this.funzionalita = null;
  }

  /**
   * @return Ritorna funzionalita.
   */
  public String getFunzionalita() {
    return funzionalita;
  }

  /**
   * @param funzionalita
   *        funzionalita da settare internamente alla classe.
   */
  public void setFunzionalita(String funzionalita) {
    this.funzionalita = funzionalita;
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

}
