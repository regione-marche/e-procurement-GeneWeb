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
package it.eldasoft.console.web.struts.schedric;

import it.eldasoft.console.db.domain.schedric.TrovaSchedRic;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Contenitore dei dati del form dei parametri di ricerca
 *
 * @author Francesco De Filippis
 */
public class TrovaSchedRicForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID = 6586631846081078957L;

  private String           tipo;
  private String           attivo;
  private String           nome;
  private String           operatoreNome;
  private String           idRicerca;
  private String           owner;
  private String           esecutore;
  private boolean          noCaseSensitive;
  private String           risPerPagina;
  private boolean          visualizzazioneAvanzata;
  private String           codiceApplicazione;


  public TrovaSchedRicForm() {
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

    this.tipo = null;
    this.attivo = null;
    this.nome = null;
    this.operatoreNome = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.idRicerca = null;
    this.owner = null;
    this.esecutore = null;
    // campo collegato a una checkbox, non va va inizializzato direttamente a true ma variato prima di passarlo alla pagina in quanto il
    // setter viene richiamato solo se la checkbox e' selezionata pertanto non verrebbe mai resettato a false
    this.noCaseSensitive = false;
    this.risPerPagina = "20";
    this.visualizzazioneAvanzata = false;
    this.codiceApplicazione = null;
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



  public TrovaSchedRic getDatiPerModel() {
    String comandoEscape = null;
    try {
      SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
      comandoEscape = composer.getEscapeSql();
    } catch (SqlComposerException e) {
      // non si verifica mai, il caricamento metadati gia' testa che la
      // property sia settata correttamente
    }

    TrovaSchedRic trovaSchedRic = new TrovaSchedRic();
    trovaSchedRic.setTipo(UtilityStringhe.convertiStringaVuotaInNull(this.tipo));

    trovaSchedRic.setOperatoreNome(
        GestioneOperatoreConfrontoStringa.convertiOperatoreConfronto(
            this.operatoreNome));
    String nome = this.nome;
    if (!"=".equals(this.operatoreNome)
        && UtilityStringhe.containsSqlWildCards(this.nome)) {
      trovaSchedRic.setEscapeNome(comandoEscape);
      nome = UtilityStringhe.escapeSqlString(this.nome);
    }
    trovaSchedRic.setNome(UtilityStringhe.convertiStringaVuotaInNull(
        GestioneOperatoreConfrontoStringa.convertiStringaConfronto(
            this.operatoreNome, nome)));
    trovaSchedRic.setIdRicerca(UtilityStringhe.convertiStringaVuotaInNull(this.idRicerca));
    trovaSchedRic.setAttivo(UtilityStringhe.convertiStringaVuotaInNull(this.attivo));
    trovaSchedRic.setOwner(UtilityStringhe.convertiStringaVuotaInNull(this.owner));
    trovaSchedRic.setEsecutore(UtilityStringhe.convertiStringaVuotaInNull(this.esecutore));
    trovaSchedRic.setNoCaseSensitive(this.noCaseSensitive);
    trovaSchedRic.setCodiceApplicazione(this.codiceApplicazione);
    return trovaSchedRic;
  }


  /**
   * @return Returns the attivo.
   */
  public String getAttivo() {
    return attivo;
  }


  /**
   * @param attivo The attivo to set.
   */
  public void setAttivo(String attivo) {
    this.attivo = attivo;
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
   * @return Returns the nome.
   */
  public String getNome() {
    return nome;
  }


  /**
   * @param nome The nome to set.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }


  /**
   * @return Returns the owner.
   */
  public String getOwner() {
    return owner;
  }


  /**
   * @param owner The owner to set.
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }


  /**
   * @return Returns the tipo.
   */
  public String getTipo() {
    return tipo;
  }


  /**
   * @param tipo The tipo to set.
   */
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }


  /**
   * @return Returns the risPerPagina.
   */
  public String getRisPerPagina() {
    return risPerPagina;
  }


  /**
   * @param risPerPagina The risPerPagina to set.
   */
  public void setRisPerPagina(String risPerPagina) {
    this.risPerPagina = risPerPagina;
  }


  /**
   * @return Returns the idRicerca.
   */
  public String getIdRicerca() {
    return idRicerca;
  }


  /**
   * @param idRicerca The idRicerca to set.
   */
  public void setIdRicerca(String idRicerca) {
    this.idRicerca = idRicerca;
  }

  /**
   * @return Ritorna esecutore.
   */
  public String getEsecutore() {
    return esecutore;
  }

  /**
   * @param esecutore esecutore da settare internamente alla classe.
   */
  public void setEsecutore(String esecutore) {
    this.esecutore = esecutore;
  }

  /**
   * @return Ritorna operatoreNome.
   */
  public String getOperatoreNome() {
    return operatoreNome;
  }

  /**
   * @param operatoreNome operatoreNome da settare internamente alla classe.
   */
  public void setOperatoreNome(String operatoreNome) {
    this.operatoreNome = operatoreNome;
  }

  /**
   * @return Ritorna visualizzazioneAvanzata.
   */
  public boolean isVisualizzazioneAvanzata() {
    return visualizzazioneAvanzata;
  }

  /**
   * @param visualizzazioneAvanzata visualizzazioneAvanzata da settare internamente alla classe.
   */
  public void setVisualizzazioneAvanzata(boolean visualizzazioneAvanzata) {
    this.visualizzazioneAvanzata = visualizzazioneAvanzata;
  }

  /**
   * @return Ritorna codiceApplicazione.
   */
  public String getCodiceApplicazione() {
    return codiceApplicazione;
  }

  /**
   * @param codiceApplicazione codiceApplicazione da settare internamente alla classe.
   */
  public void setCodiceApplicazione(String codiceApplicazione) {
    this.codiceApplicazione = codiceApplicazione;
  }

}