/*
 * Created on 24-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.permessi;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Contenitore dei dati del form di associazione utenti con una entita' nella
 * tabella G_PERMESSI
 *
 * @author Luca.Giacomazzo
 */
public class PermessiAccountEntitaForm extends ActionForm {

  /**   UID   */
  private static final long serialVersionUID = 1479650105378024656L;

  String   campoChiave;
  String   valoreChiave;
  String[] condividiEntita;
  String[] idPermesso;
  String[] idAccount;
  String[] autorizzazione;
  String[] proprietario;
  String[] ruolo;
  String[] ruoloUsrsys;

  public PermessiAccountEntitaForm() {
    super();
  }

  public void reset(ActionMapping mapping, HttpServletRequest request){
    super.reset(mapping, request);
    this.campoChiave = null;
    this.valoreChiave = null;
    this.condividiEntita = null;
    this.idPermesso = null;
    this.idAccount = null;
    this.autorizzazione = null;
    this.proprietario = null;
    this.ruolo = null;
    this.ruoloUsrsys = null;
  }

  /**
   * @return Ritorna idPermesso.
   */
  public String[] getIdPermesso() {
    return idPermesso;
  }

  /**
   * @param idPermesso idPermesso da settare internamente alla classe.
   */
  public void setIdPermesso(String[] idPermesso) {
    this.idPermesso = idPermesso;
  }

  /**
   * @return Ritorna idAccount.
   */
  public String[] getIdAccount() {
    return idAccount;
  }

  /**
   * @param idAccount da settare internamente alla classe.
   */
  public void setIdAccount(String[] idAccount) {
    this.idAccount = idAccount;
  }

  /**
   * @return Ritorna autorizzazione.
   */
  public String[] getAutorizzazione() {
    return autorizzazione;
  }

  /**
   * @param autorizzazione autorizzazione da settare internamente alla classe.
   */
  public void setAutorizzazione(String[] autorizzazione) {
    this.autorizzazione = autorizzazione;
  }

  /**
   * @return Ritorna proprietario.
   */
  public String[] getProprietario() {
    return proprietario;
  }

  /**
   * @param proprietario proprietario da settare internamente alla classe.
   */
  public void setProprietario(String[] proprietario) {
    this.proprietario = proprietario;
  }

  /**
   * @return Ritorna campoChiave.
   */
  public String getCampoChiave() {
    return campoChiave;
  }

  /**
   * @param campoChiave campoChiave da settare internamente alla classe.
   */
  public void setCampoChiave(String campoChiave) {
    this.campoChiave = campoChiave;
  }

  /**
   * @return Ritorna valoreChiave.
   */
  public String getValoreChiave() {
    return valoreChiave;
  }

  /**
   * @param valoreChiave valoreChiave da settare internamente alla classe.
   */
  public void setValoreChiave(String valoreChiave) {
    this.valoreChiave = valoreChiave;
  }

  /**
   * @return Ritorna condiviEntita.
   */
  public String[] getCondividiEntita() {
    return condividiEntita;
  }

  /**
   * @param condiviEntita condiviEntita da settare internamente alla classe.
   */
  public void setCondividiEntita(String[] condiviEntita) {
    this.condividiEntita = condiviEntita;
  }

  /**
   * @return Ritorna ruolo.
   */
  public String[] getRuolo() {
    return ruolo;
  }

  /**
   * @param ruolo ruolo da settare internamente alla classe.
   */
  public void setRuolo(String[] ruolo) {
    this.ruolo = ruolo;
  }

  /**
   * @return Ritorna ruolo.
   */
  public String[] getRuoloUsrsys() {
    return ruoloUsrsys;
  }

  /**
   * @param ruoloUsrsys ruolo della usrsys da settare internamente alla classe.
   */
  public void setRuoloUsrsys(String[] ruoloUsrsys) {
    this.ruoloUsrsys = ruoloUsrsys;
  }
}