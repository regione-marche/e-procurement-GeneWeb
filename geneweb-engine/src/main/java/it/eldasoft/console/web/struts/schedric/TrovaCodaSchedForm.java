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

import it.eldasoft.console.db.domain.schedric.TrovaCodaSched;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Contenitore dei dati del form dei parametri di ricerca
 *
 * @author Francesco De Filippis
 */
public class TrovaCodaSchedForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID = 6586631846081078957L;

  private String           stato;
  private String           nome;
  private String           idSchedRic;
  private String           idRicerca;
  private String           nomeRicerca;
  private String           msg;
  private String           operatoreMsg;
  private String           esecutore;
  private String           dataEsecSuc;
  private String           dataEsecPrec;
  private String           operatoreDataEsec;
  private boolean          noCaseSensitive;
  private String           risPerPagina;
  private boolean          visualizzazioneAvanzata;
  private String           codiceApplicazione;

  public TrovaCodaSchedForm() {
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

    this.stato = null;
    this.nome = null;
    this.idSchedRic = null;
    this.idRicerca = null;
    this.nomeRicerca = null;
    this.msg = null;
    this.operatoreMsg = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.esecutore = null;
    this.dataEsecSuc = null;
    this.dataEsecPrec = null;
    this.operatoreDataEsec = null;
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



  public TrovaCodaSched getDatiPerModel() {
    String comandoEscape = null;
    try {
      SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
      comandoEscape = composer.getEscapeSql();
    } catch (SqlComposerException e) {
      // non si verifica mai, il caricamento metadati gia' testa che la
      // property sia settata correttamente
    }

    TrovaCodaSched trovaCodaSched = new TrovaCodaSched();
    trovaCodaSched.setStato(this.stato);
    trovaCodaSched.setIdSchedRic(this.idSchedRic);
    trovaCodaSched.setNome(UtilityStringhe.convertiStringaVuotaInNull(this.nome));
    trovaCodaSched.setIdRicerca(this.idRicerca);
    trovaCodaSched.setNomeRicerca(UtilityStringhe.convertiStringaVuotaInNull(this.nomeRicerca));

    trovaCodaSched.setOperatoreMsg(
        GestioneOperatoreConfrontoStringa.convertiOperatoreConfronto(
            this.operatoreMsg));
    String msg = this.msg;
    if (!"=".equals(this.msg) && UtilityStringhe.containsSqlWildCards(this.msg)) {
      trovaCodaSched.setEscapeMsg(comandoEscape);
      msg = UtilityStringhe.escapeSqlString(this.msg);
    }
    trovaCodaSched.setMsg(UtilityStringhe.convertiStringaVuotaInNull(
        GestioneOperatoreConfrontoStringa.convertiStringaConfronto(
            this.operatoreMsg, msg)));
    trovaCodaSched.setEsecutore(this.esecutore);
    trovaCodaSched.setDataEsecSuc(UtilityDate.convertiData(this.dataEsecSuc,UtilityDate.FORMATO_GG_MM_AAAA));
    trovaCodaSched.setDataEsecPrec(UtilityDate.convertiData(this.dataEsecPrec,UtilityDate.FORMATO_GG_MM_AAAA));
    String tmpOperatoreDataEsec = this.operatoreDataEsec;
    if(tmpOperatoreDataEsec.indexOf(".") != -1){
      if(tmpOperatoreDataEsec.indexOf("=") != -1){
        trovaCodaSched.setOperatoreDataEsecSuc(">=");
        trovaCodaSched.setOperatoreDataEsecPrec("<=");
      } else {
        trovaCodaSched.setOperatoreDataEsecSuc(">");
        trovaCodaSched.setOperatoreDataEsecPrec("<");
      }
    } else {
      trovaCodaSched.setOperatoreDataEsecPrec(tmpOperatoreDataEsec);
    }
    trovaCodaSched.setNoCaseSensitive(this.noCaseSensitive);
    trovaCodaSched.setCodiceApplicazione(this.codiceApplicazione);
    return trovaCodaSched;
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
  public String getEsecutore() {
    return esecutore;
  }


  /**
   * @param owner The owner to set.
   */
  public void setEsecutore(String owner) {
    this.esecutore = owner;
  }


  /**
   * @return Returns the msg.
   */
  public String getMsg() {
    return msg;
  }


  /**
   * @param msg The msg to set.
   */
  public void setMsg(String msg) {
    this.msg = msg;
  }


  /**
   * @return Ritorna operatoreMsg.
   */
  public String getOperatoreMsg() {
    return operatoreMsg;
  }


  /**
   * @param operatoreMsg operatoreMsg da settare internamente alla classe.
   */
  public void setOperatoreMsg(String operatoreMsg) {
    this.operatoreMsg = operatoreMsg;
  }


  /**
   * @return Returns the nomeRicerca.
   */
  public String getNomeRicerca() {
    return nomeRicerca;
  }


  /**
   * @param nomeRicerca The nomeRicerca to set.
   */
  public void setNomeRicerca(String nomeRicerca) {
    this.nomeRicerca = nomeRicerca;
  }


  /**
   * @return Returns the stato.
   */
  public String getStato() {
    return stato;
  }


  /**
   * @param stato The stato to set.
   */
  public void setStato(String stato) {
    this.stato = stato;
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
   * @return Returns the dataEsecPrec.
   */
  public String getDataEsecPrec() {
    return dataEsecPrec;
  }


  /**
   * @param dataEsecPrec The dataEsecPrec to set.
   */
  public void setDataEsecPrec(String dataEsecPrec) {
    this.dataEsecPrec = dataEsecPrec;
  }


  /**
   * @return Returns the dataEsecSuc.
   */
  public String getDataEsecSuc() {
    return dataEsecSuc;
  }


  /**
   * @param dataEsecSuc The dataEsecSuc to set.
   */
  public void setDataEsecSuc(String dataEsecSuc) {
    this.dataEsecSuc = dataEsecSuc;
  }


  /**
   * @return Ritorna operatoreDataEsec.
   */
  public String getOperatoreDataEsec() {
    return operatoreDataEsec;
  }


  /**
   * @param operatoreDataEsec operatoreDataEsec da settare internamente alla classe.
   */
  public void setOperatoreDataEsec(String operatoreDataEsec) {
    this.operatoreDataEsec = operatoreDataEsec;
  }


  /**
   * @return Returns the idSchedRic.
   */
  public String getIdSchedRic() {
    return idSchedRic;
  }


  /**
   * @param idSchedRic The idSchedRic to set.
   */
  public void setIdSchedRic(String idSchedRic) {
    this.idSchedRic = idSchedRic;
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