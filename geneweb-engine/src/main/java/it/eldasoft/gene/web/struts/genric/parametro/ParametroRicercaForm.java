/*
 * Created on 19-set-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.parametro;


import it.eldasoft.gene.db.domain.genric.ParametroRicerca;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author Luca Giacomazzo
 */
public class ParametroRicercaForm extends ActionForm {

  /**   UID   */
  private static final long serialVersionUID = 8095615644147373770L;

  private String id;
  private String progressivo;
  private String codiceParametro;
  private String nome;
  private String descrizione;
  private String tipoParametro;
  private String tabCod;
  
  public ParametroRicercaForm(){
    this.id = null;
    this.progressivo = null;
    this.codiceParametro = null;
    this.nome = null;
    this.descrizione = null;
    this.tipoParametro = null;
    this.tabCod = null;
  }

  public ParametroRicercaForm(ParametroRicerca datiModel){
    this.id = datiModel.getId().toString();
    this.progressivo = "" + datiModel.getProgressivo();
    this.codiceParametro = datiModel.getCodice();
    this.nome = datiModel.getNome();
    this.descrizione = datiModel.getDescrizione();
    this.tipoParametro = datiModel.getTipo();
    this.tabCod = datiModel.getTabCod();
  }
  
  public ParametroRicerca getDatiPerModel() {
    ParametroRicerca parametro = new ParametroRicerca();
        
    parametro.setId(UtilityNumeri.convertiIntero(this.id)); //obbligatorio
    parametro.setProgressivo(new Integer(this.progressivo).intValue()); //obbligatorio
    parametro.setCodice(this.codiceParametro); //obbligatorio
    parametro.setNome(this.nome); //obbligatorio
    parametro.setDescrizione(UtilityStringhe.convertiStringaVuotaInNull(this.descrizione));
    parametro.setTipo(this.tipoParametro); //obbligatorio
    parametro.setTabCod(UtilityStringhe.convertiStringaVuotaInNull(this.tabCod));
  
    return parametro;
  }
  
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    this.id = null;
    this.progressivo = null;
    this.codiceParametro = null;
    this.nome = null;
    this.descrizione = null;
    this.tipoParametro = null;
    this.tabCod = null;
  }
  
  /**
   * @return Ritorna codice.
   */
  public String getCodiceParametro() {
    return codiceParametro;
  }

  /**
   * @param codice codice da settare internamente alla classe.
   */
  public void setCodiceParametro(String codice) {
    this.codiceParametro = codice;
  }
 
  /**
   * @return Ritorna descrizione.
   */
  public String getDescrizione() {
    return descrizione;
  }
  
  /**
   * @param descrizione descrizione da settare internamente alla classe.
   */
  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }
  
  /**
   * @return Ritorna id.
   */
  public String getId() {
    return id;
  }
  
  /**
   * @param id id da settare internamente alla classe.
   */
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * @return Ritorna nome.
   */
  public String getNome() {
    return nome;
  }
  
  /**
   * @param nome nome da settare internamente alla classe.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }
  
  /**
   * @return Ritorna progressivo.
   */
  public String getProgressivo() {
    return progressivo;
  }
  
  /**
   * @param progressivo progressivo da settare internamente alla classe.
   */
  public void setProgressivo(String progressivo) {
    this.progressivo = progressivo;
  }
  
  /**
   * @return Ritorna tabCod.
   */
  public String getTabCod() {
    return tabCod;
  }
  
  /**
   * @param tabCod tabCod da settare internamente alla classe.
   */
  public void setTabCod(String tabcod) {
    this.tabCod = tabcod;
  }

  /**
   * @return Ritorna tipo.
   */
  public String getTipoParametro() {
    return tipoParametro;
  }
  
  /**
   * @param tipo tipo da settare internamente alla classe.
   */
  public void setTipoParametro(String tipo) {
    this.tipoParametro = tipo;
  }
    
}