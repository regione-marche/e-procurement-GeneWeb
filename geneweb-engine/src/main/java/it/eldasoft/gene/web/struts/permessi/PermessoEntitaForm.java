/*
 * Created on 26-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.permessi;

import it.eldasoft.gene.db.domain.permessi.PermessoEntita;

import org.apache.struts.action.ActionForm;

/**
 * ActionForm per portare al client l'oggetto PermessoEntita correttamente
 * valorizzato, specie in fase di visualizzazione
 *
 * @author Luca.Giacomazzo
 */
public class PermessoEntitaForm extends ActionForm {

  /**   UID   */
  private static final long serialVersionUID = 3559134034956409300L;

  private Integer idPermesso;
  private Integer idAccount;
  private String  autorizzazione;
  private boolean proprietario;
  private String  campoChiaveEntita;
  private boolean predefinito;
  private Integer riferimento;
  private Integer ruolo;
  private Integer ruoloUsrsys;

  // Campi che vengono estratti da DB, ma non vengono gestiti in fase di insert
  private String login;
  private String nome;
  private String utenteDisabilitato;

  public PermessoEntitaForm(){
    super();
    this.inizializzaOggetto();
  }

  private void inizializzaOggetto(){
    this.idPermesso        = null;
    this.idAccount         = null;
    this.autorizzazione    = null;
    this.proprietario      = false;
    this.campoChiaveEntita = null;
    this.predefinito       = false;
    this.riferimento       = null;
    this.login             = null;
    this.nome              = null;
    this.ruolo             = null;
    this.ruoloUsrsys       = null;
    this.utenteDisabilitato = null;
  }

  public PermessoEntitaForm(PermessoEntita permessoBean){
    this.idPermesso = permessoBean.getIdPermesso();
    this.idAccount  = permessoBean.getIdAccount();

    if(permessoBean.getAutorizzazione() != null && permessoBean.getAutorizzazione().intValue() == 1)
      this.setAutorizzazione(ListaPermessiEntitaAction.LISTA_TEXT_AUTORIZZAZIONI[1]);
    else
      this.setAutorizzazione(ListaPermessiEntitaAction.LISTA_TEXT_AUTORIZZAZIONI[0]);

    this.setProprietario(permessoBean.getProprietario() != null && permessoBean.getProprietario().intValue() == 1? true: false);
    this.setCampoChiaveEntita(permessoBean.getCampoChiave());
    this.setPredefinito(permessoBean.getPredefinito() != null && permessoBean.getPredefinito().intValue() == 1? true: false);
    this.setRiferimento(permessoBean.getRiferimento() != null && permessoBean.getRiferimento().intValue() > 0? permessoBean.getRiferimento(): null);
    this.setLogin(permessoBean.getLogin());
    this.setUtenteDisabilitato(permessoBean.getUtenteDisabilitato());
    if("1".equals(this.getUtenteDisabilitato()))
      this.setNome(permessoBean.getNome() + " (utente disabilitato)");
    else
      this.setNome(permessoBean.getNome());
    //this.setNome(permessoBean.getNome());
    this.setRuolo(permessoBean.getRuolo());
    this.setRuoloUsrsys(permessoBean.getRuoloUsrsys());
  }

  /**
   * @return Ritorna autorizzazione.
   */
  public String getAutorizzazione() {
    return autorizzazione;
  }

  /**
   * @param autorizzazione autorizzazione da settare internamente alla classe.
   */
  public void setAutorizzazione(String autorizzazione) {
    this.autorizzazione = autorizzazione;
  }

  /**
   * @return Ritorna campoChiaveEntita.
   */
  public String getCampoChiaveEntita() {
    return campoChiaveEntita;
  }

  /**
   * @param campoChiaveEntita campoChiaveEntita da settare internamente alla classe.
   */
  public void setCampoChiaveEntita(String campoChiaveEntita) {
    this.campoChiaveEntita = campoChiaveEntita;
  }

  /**
   * @return Ritorna idAccount.
   */
  public Integer getIdAccount() {
    return idAccount;
  }

  /**
   * @param idAccount idAccount da settare internamente alla classe.
   */
  public void setIdAccount(Integer idAccount) {
    this.idAccount = idAccount;
  }

  /**
   * @return Ritorna idPermesso.
   */
  public Integer getIdPermesso() {
    return idPermesso;
  }

  /**
   * @param idPermesso idPermesso da settare internamente alla classe.
   */
  public void setIdPermesso(Integer idPermesso) {
    this.idPermesso = idPermesso;
  }

  /**
   * @return Ritorna login.
   */
  public String getLogin() {
    return login;
  }

  /**
   * @param login login da settare internamente alla classe.
   */
  public void setLogin(String login) {
    this.login = login;
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
   * @return Ritorna predefinito.
   */
  public boolean getPredefinito() {
    return predefinito;
  }

  /**
   * @param predefinito predefinito da settare internamente alla classe.
   */
  public void setPredefinito(boolean predefinito) {
    this.predefinito = predefinito;
  }

  /**
   * @return Ritorna proprietario.
   */
  public boolean getProprietario() {
    return proprietario;
  }

  /**
   * @param proprietario proprietario da settare internamente alla classe.
   */
  public void setProprietario(boolean proprietario) {
    this.proprietario = proprietario;
  }

  /**
   * @return Ritorna riferimento.
   */
  public Integer getRiferimento() {
    return riferimento;
  }

  /**
   * @param riferimento riferimento da settare internamente alla classe.
   */
  public void setRiferimento(Integer riferimento) {
    this.riferimento = riferimento;
  }

  /**
   * @return Ritorna ruolo.
   */
  public Integer getRuolo() {
    return ruolo;
  }

  /**
   * @param ruolo ruolo da settare internamente alla classe.
   */
  public void setRuolo(Integer ruolo) {
    this.ruolo = ruolo;
  }

  /**
   * @return Ritorna ruolo della usrsys.
   */
  public Integer getRuoloUsrsys() {
    return ruoloUsrsys;
  }

  /**
   * @param ruoloUsrsys ruolo della usrsys da settare internamente alla classe.
   */
  public void setRuoloUsrsys(Integer ruoloUsrsys) {
    this.ruoloUsrsys = ruoloUsrsys;
  }

  /**
   * @return Ritorna sysdisab della usrsys.
   */
  public String getUtenteDisabilitato() {
    return utenteDisabilitato;
  }

  /**
   * @param utenteDisabilitato sysdisab della usrsys da settare internamente alla classe.
   */
  public void setUtenteDisabilitato(String utenteDisabilitato) {
    this.utenteDisabilitato = utenteDisabilitato;
  }
}