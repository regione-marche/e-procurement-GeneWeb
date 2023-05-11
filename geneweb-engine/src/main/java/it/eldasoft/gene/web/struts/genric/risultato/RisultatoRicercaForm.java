/*
 * Created on 21-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.risultato;

import it.eldasoft.gene.db.domain.genric.DatiRisultato;

import java.io.Serializable;
import java.util.Vector;

/**
 * @author Stefano.Sabbadin
 */
public class RisultatoRicercaForm implements Serializable {

  /** UID */
  private static final long serialVersionUID = 9122083731175112293L;

  private String            titoloRicerca;
  private Vector<String>    titoliColonne;
  private int               numeroColonne;
  private int               numeroRighe;
  private DatiRisultato     datiRisultato;
  private int               risPerPagina;
  private boolean           genModelli;
  private boolean           overflow;
  private boolean           linkScheda;

  public RisultatoRicercaForm() {
    super();
    this.inizializzaOggetto();
  }

  private void inizializzaOggetto() {
    this.titoloRicerca = null;
    this.numeroColonne = 0;
    this.numeroRighe = 0;
    this.titoliColonne = new Vector<String>();
    this.datiRisultato = new DatiRisultato();
    this.risPerPagina = 0;
    this.genModelli = false;
    this.overflow = false;
    this.linkScheda = false;
  }

  /**
   * @return Returns the titoloRicerca.
   */
  public String getTitoloRicerca() {
    return this.titoloRicerca;
  }

  /**
   * @param titoloRicerca
   *        The titoloRicerca to set.
   */
  public void setTitoloRicerca(String titoloRicerca) {
    this.titoloRicerca = titoloRicerca;
  }

  /**
   * @return Returns the titoliColonne.
   */
  public Vector<String> getTitoliColonne() {

    return titoliColonne;
  }

  /**
   * @param titoliColonne
   *        The titoliColonne to set.
   */
  public void setTitoliColonne(Vector<String> titoliColonne) {

    this.titoliColonne = titoliColonne;
  }

  /**
   * @return Returns the datiRisultato.
   */
  public DatiRisultato getDatiRisultato() {

    return datiRisultato;
  }

  /**
   * @param datiRisultato
   *        The datiRisultato to set.
   */
  public void setDatiRisultato(DatiRisultato datiRisultato) {

    this.datiRisultato = datiRisultato;
  }

  /**
   * @return Returns the numeroColonne.
   */
  public int getNumeroColonne() {

    return numeroColonne;
  }

  /**
   * @param numeroColonne
   *        The numeroColonne to set.
   */
  public void setNumeroColonne(int numeroColonne) {

    this.numeroColonne = numeroColonne;
  }

  /**
   * @return Returns the numeroRighe.
   */
  public int getNumeroRighe() {

    return numeroRighe;
  }

  /**
   * @param numeroRighe
   *        The numeroRighe to set.
   */
  public void setNumeroRighe(int numeroRighe) {

    this.numeroRighe = numeroRighe;
  }

  /**
   * @return Ritorna risPerPagina.
   */
  public int getRisPerPagina() {
    return risPerPagina;
  }

  /**
   * @param risPerPagina
   *        risPerPagina da settare internamente alla classe.
   */
  public void setRisPerPagina(int risPerPagina) {
    this.risPerPagina = risPerPagina;
  }

  /**
   * @return Ritorna genModelli.
   */
  public boolean isGenModelli() {
    return genModelli;
  }

  /**
   * @param genModelli
   *        genModelli da settare internamente alla classe.
   */
  public void setGenModelli(boolean genModelli) {
    this.genModelli = genModelli;
  }

  /**
   * @return Ritorna overflow.
   */
  public boolean isOverflow() {
    return overflow;
  }

  /**
   * @param overflow
   *        overflow da settare internamente alla classe.
   */
  public void setOverflow(boolean overflow) {
    this.overflow = overflow;
  }

  /**
   * @return Ritorna linkScheda.
   */
  public boolean isLinkScheda() {
    return linkScheda;
  }

  /**
   * @param linkScheda linkScheda da settare internamente alla classe.
   */
  public void setLinkScheda(boolean linkScheda) {
    this.linkScheda = linkScheda;
  }

}