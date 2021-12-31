/*
 * Created on 22-ago-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.gene.web.struts.genric.gruppo;

import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author Luca.Giacomazzo
 */
public class GruppoForm extends ActionForm {

  /** UID */
  private static final long serialVersionUID = -8259311284282711878L;

  private String            id               = null;
  private String            idGruppo         = null;
  private String            nomeGruppo       = null;
  private String            descrizione      = null;

  public GruppoForm() {
    super();
    this.inizializzaOggetto();
  }
  
  private void inizializzaOggetto() {
    this.id = null;
    this.idGruppo = null;
    this.nomeGruppo = null;
    this.descrizione = null;    
  }

  public GruppoForm(GruppoRicerca datiModel) {
    if (datiModel.getId() != null) this.id = datiModel.getId().toString();
    this.idGruppo = Integer.toString(datiModel.getIdGruppo());
    this.nomeGruppo = datiModel.getNomeGruppo();
    this.descrizione = datiModel.getDescrGruppo();
  }

  public GruppoRicerca getDatiPerModel() {
    GruppoRicerca gruppo = new GruppoRicerca();

    gruppo.setId(UtilityNumeri.convertiIntero(this.id));
    gruppo.setIdGruppo(Integer.parseInt(this.idGruppo));
    gruppo.setNomeGruppo(this.nomeGruppo);
    gruppo.setDescrGruppo(UtilityStringhe.convertiStringaVuotaInNull(this.descrizione));

    return gruppo;
  }

  /**
   * @return Ritorna id.
   */
  public String getId() {
    return id;
  }

  /**
   * @param id
   *        id da settare internamente alla classe.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return Returns the nomeGruppo.
   */
  public String getNomeGruppo() {
    return nomeGruppo;
  }

  /**
   * @param nomeGruppo
   *        The nomeGruppo to set.
   */
  public void setNomeGruppo(String codiceRuolo) {
    this.nomeGruppo = codiceRuolo;
  }

  /**
   * @return Returns the descrizione.
   */
  public String getDescrizione() {
    return descrizione;
  }

  /**
   * @param descrizione
   *        The descrizione to set.
   */
  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  /**
   * @return Returns the idGruppo.
   */
  public String getIdGruppo() {
    return idGruppo;
  }

  /**
   * @param idGruppo
   *        The idGruppo to set.
   */
  public void setIdGruppo(String id) {
    this.idGruppo = id;
  }

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }
}