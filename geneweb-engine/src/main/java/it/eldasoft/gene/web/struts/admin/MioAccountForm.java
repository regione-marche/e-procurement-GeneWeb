/*
 * Created on 20 - Feb - 2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.DispatchForm;
import it.eldasoft.gene.db.domain.admin.Account;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

/**
 * Form di gestione dell'Account
 *
 * @author cit_defilippis
 */
public class MioAccountForm extends DispatchForm {

  /** UID */
  private static final long serialVersionUID    = -989053977808439700L;

  private final String[]          listaTextSiNo       = CostantiGeneraliAccount.LISTA_SI_NO;
  private final String[]          listaTextPrivilegi  = CostantiGeneraliAccount.LISTA_TEXT_PRIVILEGI;
  private final String[]          listaValuePrivilegi = CostantiGeneraliAccount.LISTA_VALUE_PRIVILEGI;
  private final String[]          listaTextGare       = CostantiGeneraliAccount.LISTA_TEXT_GARE;
  private final String[]          listaTextLavori     = CostantiGeneraliAccount.LISTA_TEXT_LAVORI;
  private final String[]          listaValueLavori    = CostantiGeneraliAccount.LISTA_VALUE_LAVORI;
  private final String[]          listaTextGenric     = CostantiGeneraliAccount.LISTA_TEXT_GENRIC;
  private final String[]          listaValueGenric    = CostantiGeneraliAccount.LISTA_VALUE_GENRIC;
  private final String[]          listaTextGenmod     = CostantiGeneraliAccount.LISTA_TEXT_GENMOD;
  private final String[]          listaValueGenmod    = CostantiGeneraliAccount.LISTA_VALUE_GENMOD;
  private final String[]          listaTextScadenza  =  CostantiGeneraliAccount.LISTA_TEXT_SCADENZA;

  private String            idAccount;
  private String            login;
  private String            nome;
  private String            email;
  private String            codfisc;


  /**
   * Costruttore con parametri prepara il form per la visualizzazione e per la
   * modifica
   *
   * @param account
   */
  public MioAccountForm(Account account) {
    super();

    this.idAccount = String.valueOf(account.getIdAccount());
    this.login = account.getLogin();
    this.nome = account.getNome();
    this.email = account.getEmail();
    this.codfisc = account.getCodfisc();
  }

  /**
   * Costruttore senza parametri Prepara il form per un nuovo account
   */
  public MioAccountForm() {
    super();
    this.inizializzaOggetto();
  }

  /**
   * Funzione di reset del form
   */
  @Override
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }

  /**
   * Funzione di inizializzazione del form per un nuovo account
   */
  private void inizializzaOggetto() {
    this.idAccount = null;
    this.login = null;
    // inizializzo a "" perchè con null legge in cache dei valori sporchi
    // e valorizza il dato in visualizzazione
    this.nome = null;
    this.email = null;
    this.codfisc=null;
  }

  
  public String getIdAccount() {
    return idAccount;
  }

  public void setIdAccount(String idAccount) {
    this.idAccount = idAccount;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String[] getListaTextSiNo() {
    return listaTextSiNo;
  }

  public String[] getListaValuePrivilegi() {
    return listaValuePrivilegi;
  }

  public String[] getListaTextPrivilegi() {
    return listaTextPrivilegi;
  }

  public String[] getListaValueLavori() {
    return listaValueLavori;
  }

  public String[] getListaTextLavori() {
    return listaTextLavori;
  }

  public String[] getListaTextScadenza() {
    return listaTextScadenza;
  }

  public String[] getListaTextGenmod() {
    return listaTextGenmod;
  }

  public String[] getListaTextGenric() {
    return listaTextGenric;
  }

  public String[] getListaValueGenmod() {
    return listaValueGenmod;
  }

  public String[] getListaValueGenric() {
    return listaValueGenric;
  }

  /**
   * @return Ritorna email.
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param email
   *        email da settare internamente alla classe.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @return Ritorna codfisc.
   */
  public String getCodfisc() {
    return codfisc;
  }

  /**
   * @param codfisc
   *        codfisc da settare internamente alla classe.
   */
  public void setCodfisc(String codfisc) {
    this.codfisc = codfisc;
  }

   /**
   * @return Ritorna listaTextGare.
   */
  public String[] getListaTextGare() {
    return listaTextGare;
  }

}
