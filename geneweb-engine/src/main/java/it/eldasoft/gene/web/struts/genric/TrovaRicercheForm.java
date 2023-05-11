/*
 * Created on 13-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.gene.db.domain.genric.TrovaRicerche;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Contenitore dei dati del form dei parametri di ricerca
 *
 * @author Luca.Giacomazzo
 */
public class TrovaRicercheForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID = 6586631846081078957L;

  private String            tipoRicerca;
  private String            nomeRicerca;
  private String            operatoreNomeRicerca;
  private String            descrizioneRicerca;
  private String            operatoreDescrizioneRicerca;
  private String            disponibile;
  private String            idGruppo;
  private String            risPerPagina;
  private boolean           noCaseSensitive;
  private String            famiglia;
  private String            personale;
  private String            owner;
  private boolean			visualizzazioneAvanzata;

  public TrovaRicercheForm() {
    super();
    inizializzaOggetto();
  }

  /**
   * Inizializza l'oggetto vuoto
   */
  private void inizializzaOggetto() {
    // ATTENZIONE: gli operatori nella form di trova di default sono impostati a
    // "contiene" in modo da effettuare il like
    // Nel momento in cui si crea l'oggetto per il model, si va a sovrascrivere
    // l'impostazione di default degli operatori nell'oggetto del model

    this.tipoRicerca = null;
    this.nomeRicerca = null;
    this.operatoreNomeRicerca = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.descrizioneRicerca = null;
    this.operatoreDescrizioneRicerca = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.disponibile = null;
    this.idGruppo = null;
    this.risPerPagina = "20";
    // campo collegato a una checkbox, non va va inizializzato direttamente a true ma variato prima di passarlo alla pagina in quanto il
    // setter viene richiamato solo se la checkbox e' selezionata pertanto non verrebbe mai resettato a false
    this.noCaseSensitive = false;
    this.famiglia = null;
    this.personale = null;
    this.owner = null;
    this.visualizzazioneAvanzata = false;
  }

  /**
   * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
   *      javax.servlet.http.HttpServletRequest)
   */
  @Override
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    inizializzaOggetto();
  }

  public TrovaRicerche getDatiPerModel() {
    String comandoEscape = null;
    try {
      SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
      comandoEscape = composer.getEscapeSql();
    } catch (SqlComposerException e) {
      // non si verifica mai, il caricamento metadati gia' testa che la
      // property sia settata correttamente
    }

    TrovaRicerche trovaRicerche = new TrovaRicerche();
    trovaRicerche.setTipoRicerca(UtilityStringhe.convertiStringaVuotaInNull(this.tipoRicerca));

    trovaRicerche.setOperatoreNomeRicerca(GestioneOperatoreConfrontoStringa.convertiOperatoreConfronto(this.getOperatoreNomeRicerca()));
    String nomeRicerca = this.nomeRicerca;
    if (!"=".equals(this.operatoreNomeRicerca)
        && UtilityStringhe.containsSqlWildCards(this.nomeRicerca)) {
      trovaRicerche.setEscapeNomeRicerca(comandoEscape);
      nomeRicerca = UtilityStringhe.escapeSqlString(this.nomeRicerca);
    }
    trovaRicerche.setNomeRicerca(UtilityStringhe.convertiStringaVuotaInNull(GestioneOperatoreConfrontoStringa.convertiStringaConfronto(
        this.operatoreNomeRicerca, nomeRicerca)));

    trovaRicerche.setOperatoreDescrizioneRicerca(GestioneOperatoreConfrontoStringa.convertiOperatoreConfronto(this.getOperatoreDescrizioneRicerca()));
    String descrizioneRicerca = this.descrizioneRicerca;
    if (!"=".equals(this.operatoreDescrizioneRicerca)
        && UtilityStringhe.containsSqlWildCards(this.descrizioneRicerca)) {
      trovaRicerche.setEscapeDescrizioneRicerca(comandoEscape);
      descrizioneRicerca = UtilityStringhe.escapeSqlString(this.descrizioneRicerca);
    }
    trovaRicerche.setDescrizioneRicerca(UtilityStringhe.convertiStringaVuotaInNull(GestioneOperatoreConfrontoStringa.convertiStringaConfronto(
        this.operatoreDescrizioneRicerca, descrizioneRicerca)));

    trovaRicerche.setIdGruppo(UtilityStringhe.convertiStringaVuotaInNull(this.idGruppo));
    trovaRicerche.setDisponibile(UtilityStringhe.convertiStringaVuotaInNull(this.disponibile));
    trovaRicerche.setNoCaseSensitive(this.noCaseSensitive);
    Vector<Integer> famiglia = new Vector<Integer>();
    if (UtilityStringhe.convertiStringaVuotaInNull(this.famiglia) != null)
      famiglia.add(new Integer(UtilityStringhe.convertiStringaVuotaInNull(this.famiglia)));
    trovaRicerche.setFamiglia(famiglia);
    trovaRicerche.setPersonale(UtilityStringhe.convertiStringaVuotaInNull(this.personale));
    trovaRicerche.setOwner(UtilityStringhe.convertiStringaVuotaInNull(this.owner));
    return trovaRicerche;
  }

  /**
   * @return Ritorna disponibile.
   */
  public String getDisponibile() {
    return disponibile;
  }

  /**
   * @param disponibile
   *        disponibile da settare internamente alla classe.
   */
  public void setDisponibile(String disponibile) {
    this.disponibile = disponibile;
  }

  /**
   * @return Ritorna idGruppo.
   */
  public String getIdGruppo() {
    return idGruppo;
  }

  /**
   * @param idRicerca
   *        idRicerca da settare internamente alla classe.
   */
  public void setIdGruppo(String idGruppo) {
    this.idGruppo = idGruppo;
  }

  /**
   * @return Ritorna risPerPagina.
   */
  public String getRisPerPagina() {
    return risPerPagina;
  }

  /**
   * @param risPerPagina
   *        risPerPagina da settare internamente alla classe.
   */
  public void setRisPerPagina(String idRisPerPagina) {
    this.risPerPagina = idRisPerPagina;
  }

  /**
   * @return Ritorna tipoRicerca.
   */
  public String getTipoRicerca() {
    return tipoRicerca;
  }

  /**
   * @param tipoRicerca
   *        tipoRicerca da settare internamente alla classe.
   */
  public void setTipoRicerca(String tipoRicerca) {
    this.tipoRicerca = tipoRicerca;
  }

  /**
   * @return Ritorna nomeRicerca.
   */
  public String getNomeRicerca() {
    return nomeRicerca;
  }

  /**
   * @param nomeRicerca
   *        nomeRicerca da settare internamente alla classe.
   */
  public void setNomeRicerca(String nomeRicerca) {
    this.nomeRicerca = nomeRicerca;
  }

  /**
   * @return Ritorna operatoreNomeRicerca.
   */
  public String getOperatoreNomeRicerca() {
    return operatoreNomeRicerca;
  }

  /**
   * @param operatoreNomeRicerca operatoreNomeRicerca da settare internamente alla classe.
   */
  public void setOperatoreNomeRicerca(String operatoreNomeRicerca) {
    this.operatoreNomeRicerca = operatoreNomeRicerca;
  }

  /**
   * @return Ritorna noCaseSensitive.
   */
  public String getNoCaseSensitive() {
    return String.valueOf(noCaseSensitive);
  }

  /**
   * @param noCaseSensitive
   *        noCaseSensitive da settare internamente alla classe.
   */
  public void setNoCaseSensitive(String noCaseSensitive) {
    if (Boolean.parseBoolean(noCaseSensitive)) {
      this.noCaseSensitive = true;
    } else {
      this.noCaseSensitive = false;
    }
  }

  /**
   * @return Ritorna descrizione.
   */
  public String getDescrizioneRicerca() {
    return descrizioneRicerca;
  }

  /**
   * @param descrizione
   *        descrizione da settare internamente alla classe.
   */
  public void setDescrizioneRicerca(String descrizione) {
    this.descrizioneRicerca = descrizione;
  }

  /**
   * @return Ritorna operatoreDescrizioneRicerca.
   */
  public String getOperatoreDescrizioneRicerca() {
    return operatoreDescrizioneRicerca;
  }

  /**
   * @param operatoreDescrizioneRicerca operatoreDescrizioneRicerca da settare internamente alla classe.
   */
  public void setOperatoreDescrizioneRicerca(String operatoreDescrizioneRicerca) {
    this.operatoreDescrizioneRicerca = operatoreDescrizioneRicerca;
  }

  /**
   * @return Returns the famiglia.
   */
  public String getFamiglia() {
    return famiglia;
  }

  /**
   * @param famiglia
   *        The famiglia to set.
   */
  public void setFamiglia(String famiglia) {
    this.famiglia = famiglia;
  }

  /**
   * @return Ritorna personale.
   */
  public String getPersonale() {
    return personale;
  }

  /**
   * @param personale
   *        personale da settare internamente alla classe.
   */
  public void setPersonale(String personale) {
    this.personale = personale;
  }

  /**
   * @return Ritorna owner.
   */
  public String getOwner() {
    return owner;
  }

  /**
   * @param owner
   *        owner da settare internamente alla classe.
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }


  /**
   * @return Ritorna visualizzazioneAvanzata
   */
  public boolean isVisualizzazioneAvanzata() {
	    return visualizzazioneAvanzata;
	  }


  /**
   *
   * @param visualizzazioneAvanzata
   */
  public void setVisualizzazioneAvanzata(boolean visualizzazioneAvanzata) {
	    this.visualizzazioneAvanzata = visualizzazioneAvanzata;
  }

}