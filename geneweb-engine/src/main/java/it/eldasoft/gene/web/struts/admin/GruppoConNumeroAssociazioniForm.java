/*
 * Created on 20-feb-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.db.domain.admin.GruppoConNumeroAssociazioni;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form da visualizzare a video per indicare le informazioni per ogni singolo
 * gruppo nella lista gruppi
 * 
 * @author Stefano.Sabbadin
 */
public class GruppoConNumeroAssociazioniForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID = -4358862057503479968L;

  /** id del gruppo */
  private String            idGruppo;

  /** nome del gruppo */
  private String            nomeGruppo;

  /** descrizione del gruppo */
  private String            descrGruppo;

  private int               numeroUtenti;

  private int               numeroRicerche;

  private int               numeroModelli;

  public GruppoConNumeroAssociazioniForm() {
    super();
    this.inizializzaOggetto();
  }

  public GruppoConNumeroAssociazioniForm(GruppoConNumeroAssociazioni gruppo) {
    super();
    this.setIdGruppo("" + gruppo.getIdGruppo());
    this.setNomeGruppo(gruppo.getNomeGruppo());
    this.setDescrGruppo(gruppo.getDescrGruppo());
    this.setNumeroUtenti(gruppo.getNumeroUtenti());
    this.setNumeroRicerche(gruppo.getNumeroRicerche());
    this.setNumeroModelli(gruppo.getNumeroModelli());
  }

  private void inizializzaOggetto() {
    this.idGruppo = null;
    this.nomeGruppo = null;
    this.descrGruppo = null;
    this.numeroUtenti = 0;
    this.numeroRicerche = 0;
    this.numeroModelli = 0;
  }

  /**
   * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
   *      javax.servlet.http.HttpServletRequest)
   */
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
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

  /**
   * @return Ritorna idGruppo.
   */
  public String getIdGruppo() {
    return idGruppo;
  }

  /**
   * @param idGruppo
   *        idGruppo da settare internamente alla classe.
   */
  public void setIdGruppo(String idGruppo) {
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
   * @return Ritorna numeroModelli.
   */
  public int getNumeroModelli() {
    return numeroModelli;
  }

  /**
   * @param numeroModelli
   *        numeroModelli da settare internamente alla classe.
   */
  public void setNumeroModelli(int numeroModelli) {
    this.numeroModelli = numeroModelli;
  }

  /**
   * @return Ritorna numeroRicerche.
   */
  public int getNumeroRicerche() {
    return numeroRicerche;
  }

  /**
   * @param numeroRicerche
   *        numeroRicerche da settare internamente alla classe.
   */
  public void setNumeroRicerche(int numeroRicerche) {
    this.numeroRicerche = numeroRicerche;
  }

  /**
   * @return Ritorna numeroUtenti.
   */
  public int getNumeroUtenti() {
    return numeroUtenti;
  }

  /**
   * @param numeroUtenti
   *        numeroUtenti da settare internamente alla classe.
   */
  public void setNumeroUtenti(int numeroUtenti) {
    this.numeroUtenti = numeroUtenti;
  }
}
