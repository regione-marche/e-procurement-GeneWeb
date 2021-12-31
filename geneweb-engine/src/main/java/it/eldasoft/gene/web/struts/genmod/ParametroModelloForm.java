/*
 * Created on 29-nov-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.commons.web.struts.DispatchForm;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

/**
 * Parametro del modello
 * 
 * @author Stefano.Sabbadin
 */
public class ParametroModelloForm extends DispatchForm {

  /**
   * UID
   */
  private static final long serialVersionUID = -1810948597270687710L;

  /** id univoco del modello */
  private int               idModello;

  /** progressivo univoco del parametro per il modello */
  private Integer           progressivo;

  /** codice del parametro */
  private String            codice;

  /** nome del parametro, ovvero titolo che compare al suo prompt */
  private String            nome;

  /** descrizione del parametro */
  private String            descrizione;

  /** tipo di parametro */
  private String            tipo;

  /** descrizione del tipo di parametro */
  private String            descrizioneTipo;

  /** elenco separato da "|" di possibili opzioni a scelta */
  private String            menu;

  /** true se il parametro è obbligatorio, false altrimenti */
  private boolean           obbligatorio;
  
  /** codice del tabellato collegato al parametro */
  private String            tabellato;

  public ParametroModelloForm() {
    super();
    this.inizializzaOggetto();
  }

  public ParametroModelloForm(ParametroModello datiModel) {
    this.idModello = datiModel.getIdModello();
    this.progressivo = new Integer(datiModel.getProgressivo());
    this.codice = datiModel.getCodice();
    this.nome = datiModel.getNome();
    this.descrizione = datiModel.getDescrizione();
    this.tipo = datiModel.getTipo();
    this.descrizioneTipo = null;
    this.menu = datiModel.getMenu();
    this.obbligatorio = (datiModel.getObbligatorio() == 1 ? true : false);
    this.tabellato = datiModel.getTabellato();
  }

  public ParametroModello getDatiPerModel() {
    ParametroModello parametro = new ParametroModello();

    parametro.setIdModello(this.idModello); // obbligatorio
    parametro.setProgressivo(this.progressivo != null
        ? this.progressivo.intValue()
        : -1); // obbligatorio
    parametro.setCodice(this.codice); // obbligatorio
    parametro.setNome(this.nome); // obbligatorio
    parametro.setDescrizione(UtilityStringhe.convertiStringaVuotaInNull(this.descrizione));
    parametro.setTipo(this.tipo); // obbligatorio
    parametro.setMenu(UtilityStringhe.convertiStringaVuotaInNull(UtilityStringhe.replace(this.menu,"\r\n","")));
    parametro.setObbligatorio(this.obbligatorio ? 1 : 0);
    parametro.setTabellato(this.tabellato);

    return parametro;
  }

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }

  private void inizializzaOggetto() {
    this.idModello = 0;
    this.progressivo = null;
    this.codice = null;
    this.nome = null;
    this.descrizione = null;
    this.tipo = null;
    this.descrizioneTipo = null;
    this.menu = null;
    this.obbligatorio = false;
    this.tabellato = null;
  }

  /**
   * @return Ritorna codice.
   */
  public String getCodice() {
    return codice;
  }

  /**
   * @param codice
   *        codice da settare internamente alla classe.
   */
  public void setCodice(String codice) {
    this.codice = codice;
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
   * @return Ritorna idModello.
   */
  public int getIdModello() {
    return idModello;
  }

  /**
   * @param idModello
   *        idModello da settare internamente alla classe.
   */
  public void setIdModello(int idModello) {
    this.idModello = idModello;
  }

  /**
   * @return Ritorna menu.
   */
  public String getMenu() {
    return menu;
  }

  /**
   * @param menu
   *        menu da settare internamente alla classe.
   */
  public void setMenu(String menu) {
    this.menu = menu;
  }

  /**
   * @return Ritorna nome.
   */
  public String getNome() {
    return nome;
  }

  /**
   * Funzione che restituisce il nome del parametro con convertiti gli apici
   * singoli per i javascript
   * 
   * @return Stringa con il replace degli apici singoli
   */
  public String getNomePerJs() {
    if (this.nome == null) return null;
    return UtilityStringhe.replace(this.nome, "'", "\\\'");
  }

  /**
   * @param nome
   *        nome da settare internamente alla classe.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * @return Ritorna progressivo.
   */
  public Integer getProgressivo() {
    return progressivo;
  }

  /**
   * @param progressivo
   *        progressivo da settare internamente alla classe.
   */
  public void setProgressivo(Integer progressivo) {
    this.progressivo = progressivo;
  }

  /**
   * @return Ritorna tipo.
   */
  public String getTipo() {
    return tipo;
  }

  /**
   * @param tipo
   *        tipo da settare internamente alla classe.
   */
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  /**
   * @return Ritorna descrizioneTipo.
   */
  public String getDescrizioneTipo() {
    return descrizioneTipo;
  }

  /**
   * @param descrizioneTipo
   *        descrizioneTipo da settare internamente alla classe.
   */
  public void setDescrizioneTipo(String descrizioneTipo) {
    this.descrizioneTipo = descrizioneTipo;
  }

  /**
   * @return Ritorna obbligatorio.
   */
  public boolean isObbligatorio() {
    return obbligatorio;
  }

  /**
   * @param obbligatorio
   *        obbligatorio da settare internamente alla classe.
   */
  public void setObbligatorio(boolean obbligatorio) {
    this.obbligatorio = obbligatorio;
  }

  
  /**
   * @return Returns the tabellato.
   */
  public String getTabellato() {
    return tabellato;
  }

  
  /**
   * @param tabellato The tabellato to set.
   */
  public void setTabellato(String tabellato) {
    this.tabellato = tabellato;
  }

}
