/*
 * Created on 01/ott/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.invcom;

import java.io.Serializable;

/**
 * Bean che identifica i dati di un modello di comunicazione.
 *
 * @author Stefano.Sabbadin
 * @since 2.0.14
 */
public class ModelloComunicazione implements Serializable {

  /**
   * UID.
   */
  private static final long serialVersionUID = 3762828135270448248L;

  /** Chiave del modello. */
  private Long              pk;
  /** Genere. */
  private Long              genere;
  /** Titolo del modello. */
  private String            titolo;
  /** Descrizione del modello. */
  private String            descrizione;
  /** Numero d'ordine (per generare l'ordinamento nelle liste). */
  private Double            numeroOrdine;
  /** Abilita la gestione dell'intestazione variabile. */
  private String            abilitaIntestazioneVariabile;
  /** Oggetto della comunicazione. */
  private String            oggetto;
  /** Testo della comunicazione. */
  private String            testo;
  /** Filtro sui soggetti destinatari della comunicazione. */
  private String            filtroSoggetti;
  /** Criterio di composizione del testo della comunicazione. */
  private Long              criterioComposizione;

  /**
   * @return Ritorna pk.
   */
  public Long getPk() {
    return pk;
  }

  /**
   * @param pk
   *        pk da settare internamente alla classe.
   */
  public void setPk(Long pk) {
    this.pk = pk;
  }

  /**
   * @return Ritorna genere.
   */
  public Long getGenere() {
    return genere;
  }

  /**
   * @param genere
   *        genere da settare internamente alla classe.
   */
  public void setGenere(Long genere) {
    this.genere = genere;
  }

  /**
   * @return Ritorna titolo.
   */
  public String getTitolo() {
    return titolo;
  }

  /**
   * @param titolo
   *        titolo da settare internamente alla classe.
   */
  public void setTitolo(String titolo) {
    this.titolo = titolo;
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
   * @return Ritorna numeroOrdine.
   */
  public Double getNumeroOrdine() {
    return numeroOrdine;
  }

  /**
   * @param numeroOrdine
   *        numeroOrdine da settare internamente alla classe.
   */
  public void setNumeroOrdine(Double numeroOrdine) {
    this.numeroOrdine = numeroOrdine;
  }

  /**
   * @return Ritorna abilitaIntestazioneVariabile.
   */
  public String getAbilitaIntestazioneVariabile() {
    return abilitaIntestazioneVariabile;
  }

  /**
   * @param abilitaIntestazioneVariabile
   *        abilitaIntestazioneVariabile da settare internamente alla classe.
   */
  public void setAbilitaIntestazioneVariabile(String abilitaIntestazioneVariabile) {
    this.abilitaIntestazioneVariabile = abilitaIntestazioneVariabile;
  }

  /**
   * @return Ritorna oggetto.
   */
  public String getOggetto() {
    return oggetto;
  }

  /**
   * @param oggetto
   *        oggetto da settare internamente alla classe.
   */
  public void setOggetto(String oggetto) {
    this.oggetto = oggetto;
  }

  /**
   * @return Ritorna testo.
   */
  public String getTesto() {
    return testo;
  }

  /**
   * @param testo
   *        testo da settare internamente alla classe.
   */
  public void setTesto(String testo) {
    this.testo = testo;
  }

  /**
   * @return Ritorna filtroSoggetti.
   */
  public String getFiltroSoggetti() {
    return filtroSoggetti;
  }

  /**
   * @param filtroSoggetti
   *        filtroSoggetti da settare internamente alla classe.
   */
  public void setFiltroSoggetti(String filtroSoggetti) {
    this.filtroSoggetti = filtroSoggetti;
  }

  /**
   * @return Ritorna criterioComposizione.
   */
  public Long getCriterioComposizione() {
    return criterioComposizione;
  }

  /**
   * @param criterioComposizione
   *        criterioComposizione da settare internamente alla classe.
   */
  public void setCriterioComposizione(Long criterioComposizione) {
    this.criterioComposizione = criterioComposizione;
  }

}
