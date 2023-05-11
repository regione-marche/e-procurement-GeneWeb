/*
 * Created on 09-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.gene.db.domain.genmod.TrovaModelli;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class TrovaModelliForm extends ActionForm {

  /** UID */
  private static final long serialVersionUID = -567074881185922939L;

  private String            tipoDocumento;
  private String            nomeModello;
  private String            operatoreNomeModello;
  private String            descrModello;
  private String            operatoreDescrModello;
  private String            fileModello;
  private String            operatoreFileModello;
  private String            disponibile;
  private String            idGruppo;
  private String            risPerPagina;
  // F.D. 09/03/07 aggiungo i due nuovi campi per la gestione dei modelli
  // personali (personale e owner)
  private String            personale;
  private String            owner;
  private boolean           noCaseSensitive;
  private boolean           visualizzazioneAvanzata;

  /**
   * Costruttore della classe: inizializza le variabili a vuote
   */
  public TrovaModelliForm() {
    super();
    this.inizializzaOggetto();
  }

  /**
   * Costruttore con la form di trova modelli di input
   *
   * @param form
   */
  public TrovaModelli getDatiPerModel() {
    String comandoEscape = null;
    try {
      SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
      comandoEscape = composer.getEscapeSql();
    } catch (SqlComposerException e) {
      // non si verifica mai, il caricamento metadati gia' testa che la
      // property sia settata correttamente
    }

    TrovaModelli trovaModelli = new TrovaModelli();
    trovaModelli.setTipoDocumento(UtilityStringhe.convertiStringaVuotaInNull(this.tipoDocumento));

    trovaModelli.setOperatoreNomeModello(GestioneOperatoreConfrontoStringa.convertiOperatoreConfronto(this.operatoreNomeModello));
    String nomeModello = this.nomeModello;
    if (!"=".equals(this.operatoreNomeModello) && UtilityStringhe.containsSqlWildCards(this.nomeModello)) {
      trovaModelli.setEscapeNomeModello(comandoEscape);
      nomeModello = UtilityStringhe.escapeSqlString(this.nomeModello);
    }
    trovaModelli.setNomeModello(UtilityStringhe.convertiStringaVuotaInNull(GestioneOperatoreConfrontoStringa.convertiStringaConfronto(
        this.operatoreNomeModello, nomeModello)));

    trovaModelli.setOperatoreDescrModello(GestioneOperatoreConfrontoStringa.convertiOperatoreConfronto(this.operatoreDescrModello));
    String descrModello = this.descrModello;
    if (!"=".equals(this.operatoreDescrModello) && UtilityStringhe.containsSqlWildCards(this.descrModello)) {
      trovaModelli.setEscapeDescrModello(comandoEscape);
      descrModello = UtilityStringhe.escapeSqlString(this.descrModello);
    }
    trovaModelli.setDescrModello(UtilityStringhe.convertiStringaVuotaInNull(GestioneOperatoreConfrontoStringa.convertiStringaConfronto(
        this.operatoreDescrModello, descrModello)));

    trovaModelli.setOperatoreFileModello(GestioneOperatoreConfrontoStringa.convertiOperatoreConfronto(this.operatoreFileModello));
    String fileModello = this.fileModello;
    if (!"=".equals(this.operatoreFileModello) && UtilityStringhe.containsSqlWildCards(this.fileModello)) {
      trovaModelli.setEscapeFileModello(comandoEscape);
      fileModello = UtilityStringhe.escapeSqlString(this.fileModello);
    }
    trovaModelli.setFileModello(UtilityStringhe.convertiStringaVuotaInNull(GestioneOperatoreConfrontoStringa.convertiStringaConfronto(
        this.operatoreFileModello, fileModello)));

    trovaModelli.setDisponibile(UtilityStringhe.convertiStringaVuotaInNull(this.disponibile));
    trovaModelli.setIdGruppo(UtilityStringhe.convertiStringaVuotaInNull(this.idGruppo));
    trovaModelli.setPersonale(UtilityStringhe.convertiStringaVuotaInNull(this.personale));
    trovaModelli.setOwner(UtilityStringhe.convertiStringaVuotaInNull(this.owner));
    trovaModelli.setNoCaseSensitive(this.noCaseSensitive);
    return trovaModelli;
  }

  /**
   * Esecuzione del reset
   */
  @Override
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    this.inizializzaOggetto();
  }

  /**
   * Inizializza un oggetto vuoto
   */
  private void inizializzaOggetto() {
    // ATTENZIONE: gli operatori nella form di trova di default sono impostati a
    // "contiene" in modo da effettuare il like
    // Nel momento in cui si crea l'oggetto per il model, si va a sovrascrivere
    // l'impostazione di default degli operatori nell'oggetto del model

    this.tipoDocumento = null;
    this.nomeModello = null;
    this.operatoreNomeModello = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.descrModello = null;
    this.operatoreDescrModello = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.fileModello = null;
    this.operatoreFileModello = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.disponibile = null;
    this.idGruppo = null;
    this.risPerPagina = "20";
    // campo collegato a una checkbox, non va va inizializzato direttamente a true ma variato prima di passarlo alla pagina in quanto il
    // setter viene richiamato solo se la checkbox e' selezionata pertanto non verrebbe mai resettato a false
    this.noCaseSensitive = false;
    this.personale = null;
    this.owner = null;
    this.visualizzazioneAvanzata = false;
  }

  /**
   * @return Returns the serialVersionUID.
   */
  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  /**
   * @return Returns the disponibile.
   */
  public String getDisponibile() {
    return disponibile;
  }

  /**
   * @param disponibile
   *        The disponibile to set.
   */
  public void setDisponibile(String disponibile) {
    this.disponibile = disponibile;
  }

  /**
   * @return Returns the fileModello.
   */
  public String getFileModello() {
    return fileModello;
  }

  /**
   * @param fileModello
   *        The fileModello to set.
   */
  public void setFileModello(String fileModello) {
    this.fileModello = fileModello;
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
  public void setIdGruppo(String idGruppo) {
    this.idGruppo = idGruppo;
  }

  /**
   * @return Returns the nomeModello.
   */
  public String getNomeModello() {
    return nomeModello;
  }

  /**
   * @param nomeModello
   *        The nomeModello to set.
   */
  public void setNomeModello(String nomeModello) {
    this.nomeModello = nomeModello;
  }

  /**
   * @return Returns the risPerPagina.
   */
  public String getRisPerPagina() {
    return risPerPagina;
  }

  /**
   * @param risPerPagina
   *        The risPerPagina to set.
   */
  public void setRisPerPagina(String risPerPagina) {
    this.risPerPagina = risPerPagina;
  }

  /**
   * @return Returns the tipoDocumento.
   */
  public String getTipoDocumento() {
    return tipoDocumento;
  }

  /**
   * @param tipoDocumento
   *        The tipoDocumento to set.
   */
  public void setTipoDocumento(String tipoDocumento) {
    this.tipoDocumento = tipoDocumento;
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
   * @return Returns the owner.
   */
  public String getOwner() {
    return owner;
  }

  /**
   * @param owner
   *        The owner to set.
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * @return Returns the personale.
   */
  public String getPersonale() {
    return personale;
  }

  /**
   * @param personale
   *        The personale to set.
   */
  public void setPersonale(String personale) {
    this.personale = personale;
  }

  /**
   * @return Returns the descrModello.
   */
  public String getDescrModello() {
    return descrModello;
  }

  /**
   * @param descrModello
   *        The descrModello to set.
   */
  public void setDescrModello(String descrModello) {
    this.descrModello = descrModello;
  }

  /**
   * @return Ritorna operatoreDescrModello.
   */
  public String getOperatoreDescrModello() {
    return operatoreDescrModello;
  }

  /**
   * @param operatoreDescrModello
   *        operatoreDescrModello da settare internamente alla classe.
   */
  public void setOperatoreDescrModello(String operatoreDescrModello) {
    this.operatoreDescrModello = operatoreDescrModello;
  }

  /**
   * @return Ritorna operatoreFileModello.
   */
  public String getOperatoreFileModello() {
    return operatoreFileModello;
  }

  /**
   * @param operatoreFileModello
   *        operatoreFileModello da settare internamente alla classe.
   */
  public void setOperatoreFileModello(String operatoreFileModello) {
    this.operatoreFileModello = operatoreFileModello;
  }

  /**
   * @return Ritorna operatoreNomeModello.
   */
  public String getOperatoreNomeModello() {
    return operatoreNomeModello;
  }

  /**
   * @param operatoreNomeModello
   *        operatoreNomeModello da settare internamente alla classe.
   */
  public void setOperatoreNomeModello(String operatoreNomeModello) {
    this.operatoreNomeModello = operatoreNomeModello;
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