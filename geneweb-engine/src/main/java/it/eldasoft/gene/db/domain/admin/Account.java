/*
 * Created on 13-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.admin;

import it.eldasoft.gene.web.struts.admin.CostantiDettaglioAccount;
import it.eldasoft.utils.sicurezza.CriptazioneException;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean per l'interfacciamento con i dati presenti nella tabella USRSYS
 *
 * @author Stefano.Sabbadin
 */
public class Account implements Serializable, Comparable<Account> {

  /**
   * UID.
   */
  private static final long serialVersionUID = 2572089103460119720L;


  /** Valore assunto dallo stato dell'account quando attivo */
  public static final String STATO_ATTIVO     = "A";
  /** Valore assunto dallo stato dell'account quando sospeso */
  public static final String STATO_SOSPESO    = "S";
  /** Valore assunto dallo stato dell'account quando revocato */
  public static final String STATO_REVOCATO   = "R";

  /** id dell'account (chiave primaria numerica) */
  private int                idAccount;
  /** login per l'accesso dell'utente */
  private String             login;
  /** login criptato dell'utente, rimasto per retrocompatibilit&agrave;. */
  private String             loginCriptata;
  /** password per l'accesso dell'utente */
  private String             password;
  /** nome utente */
  private String             nome;
  /** stato dell'account (1 carattere) */
  private String             stato;
  // F.D. 12/10/2006 modifiche per la gestione della USRSYS
  /** opzioni dell'utente */
  private String             opzioniUtente;
  /** sigla della provincia */
  private String             siglaProvincia;
  /** opzioni dell'applicazione */
  private String             opzioniApplicazione;
  /** data di inserimento */
  private Date               dataInserimento;
  /** opzioni privilegi utente */
  private String             opzioniPrivilegi;
  /** Abilitazione Standard */
  private String             abilitazioneStd;
  /** Abilitazione gare */
  private String             abilitazioneGare;
  /** Abilitazione contratti */
  private String             abilitazioneContratti;
  /** Livello standard */
  private Integer            livelloStd;
  /** Livello gare */
  private Integer            livelloGare;
  /** Livello contratti */
  private Integer            livelloContratti;
  /** Flag per utente importato da LDAP */
  private Integer            flagLdap;
  /** DistinguishedName dell'utente LDAP */
  private String             dn;
  /** L'utente è abilitato ? */
  private Integer            utenteDisabilitato;
   /** E-Mail dell'utente */
  private String             email;
  /** Scadenza account dell'utente */
  private Date               scadenzaAccount;
  /** Ufficio appartenenza dell'utente */
  private Integer             ufficioAppartenenza;
  /** Ruolo dell'utente per il mercato elettronico*/
  private Integer             ruoloUtenteMercatoElettronico;
  /** Categorizzazione dell'utente. */
  private Integer             categoria;
  /** Codice fiscale dell'utente */
  private String             codfisc;
  /** Abilitazione AP **/
  private String            abilitazioneAP;
  /** Ultimo accesso dell'utente */
  private Date               ultimoAccesso;
  /**
   * @return Ritorna UfficioAppartenenza.
   */
  public Integer getUfficioAppartenenza() {
    return ufficioAppartenenza;
  }

  /**
   * @param UfficioAppartenenza
   *        UfficioAppartenenza da settare internamente alla classe.
   */
  public void setUfficioAppartenenza(Integer ufficioAppartenenza) {
    this.ufficioAppartenenza = ufficioAppartenenza;
  }


  /**
   * @return Ritorna ruoloUtenteMercatoElettronico.
   */
  public Integer getRuoloUtenteMercatoElettronico() {
    return ruoloUtenteMercatoElettronico;
  }

  /**
   * @param ruoloUtenteMercatoElettronico
   *        ruoloUtenteMercatoElettronico da settare internamente alla classe.
   */
  public void setRuoloUtenteMercatoElettronico(Integer ruoloUtenteMercatoElettronico) {
    this.ruoloUtenteMercatoElettronico = ruoloUtenteMercatoElettronico;
  }

  /**
   * @return Ritorna categoria.
   */
  public Integer getCategoria() {
    return categoria;
  }

  /**
   * @param categoria categoria da settare internamente alla classe.
   */
  public void setCategoria(Integer categoria) {
    this.categoria = categoria;
  }

  /**
   * @return Ritorna scadenza account.
   */
  public Date getScadenzaAccount() {
    return scadenzaAccount;
  }

  /**
   * @param scadenza Account
   *        scadenzaAccount da settare internamente alla classe.
   */
  public void setScadenzaAccount(Date scadenzaAccount) {
    this.scadenzaAccount = scadenzaAccount;
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


  public Account() {
    super();
  }

  public Account(AccountLdap accountLdap) {
    super();
    this.dn = accountLdap.getDn();
    this.login = accountLdap.getSn();
    this.nome = accountLdap.getCn();
    this.flagLdap = new Integer(1);
  }

  /**
   * @return Ritorna idAccount.
   */
  public int getIdAccount() {
    return idAccount;
  }

  /**
   * @param idAccount
   *        idAccount da settare internamente alla classe.
   */
  public void setIdAccount(int idAccount) {
    this.idAccount = idAccount;
  }

  /**
   * @return Ritorna login.
   */
  public String getLogin() {
    return login;
  }

  /**
   * @param login
   *        login da settare internamente alla classe.
   * @throws CriptazioneException
   */
  public void setLogin(String login) {
    this.login = login;
  }

  /**
   * @return Ritorna loginCriptata.
   */
  public String getLoginCriptata() {
    return loginCriptata;
  }

  /**
   * @param loginCriptata loginCriptata da settare internamente alla classe.
   */
  public void setLoginCriptata(String loginCriptata) {
    this.loginCriptata = loginCriptata;
  }

  /**
   * @return Ritorna nome.
   */
  public String getNome() {
    return nome;
  }

  /**
   * @param nome
   *        nome da settare internamente alla classe.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * @return Ritorna password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password
   *        password da settare internamente alla classe.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return Ritorna stato.
   */
  public String getStato() {
    return stato;
  }

  /**
   * @param stato
   *        stato da settare internamente alla classe.
   */
  public void setStato(String stato) {
    this.stato = stato;
  }

  /**
   * @return Ritorna data di inserimento
   */
  public Date getDataInserimento() {
    return dataInserimento;
  }

  /**
   * @param dataInserimento
   *        dataInserimento da settare internamente alla classe.
   */
  public void setDataInserimento(Date dataInserimento) {
    this.dataInserimento = dataInserimento;
  }

  /**
   * @return Ritorna opzioni applicazione
   */
  public String getOpzioniApplicazione() {
    return opzioniApplicazione;
  }

  /**
   * @param opzioniApplicazione
   *        opzioniApllicazione da settare internamente alla classe.
   */
  public void setOpzioniApplicazione(String opzioniApplicazione) {
    this.opzioniApplicazione = opzioniApplicazione;
  }

  /**
   * @return Ritorna opzioni privilegi
   */
  public String getOpzioniPrivilegi() {
    return opzioniPrivilegi;
  }

  /**
   * @param opzioniPrivilegi
   *        opzioniPrivilagi da settare internamente alla classe.
   */
  public void setOpzioniPrivilegi(String opzioniBollettiniUtenza) {
    this.opzioniPrivilegi = opzioniBollettiniUtenza;
  }

  /**
   * @return Ritorna opzioni utente
   */
  public String getOpzioniUtente() {
    return opzioniUtente;
  }

  /**
   * @param opzioniUtente
   *        opzioniUtente da settare internamente alla classe.
   */
  public void setOpzioniUtente(String opzioniUtente) {
    this.opzioniUtente = opzioniUtente;
  }

  /**
   * @return Ritorna sigla provincia
   */
  public String getSiglaProvincia() {
    return siglaProvincia;
  }

  /**
   * @param siglaProvincia
   *        siglaProvincia da settare internamente alla classe.
   */
  public void setSiglaProvincia(String siglaProvincia) {
    this.siglaProvincia = siglaProvincia;
  }

  /**
   * @return Ritorna sysab3.
   */
  public String getAbilitazioneStd() {
    return abilitazioneStd;
  }

  /**
   * @param sysab3
   *        sysab3 da settare internamente alla classe.
   */
  public void setAbilitazioneStd(String sysab3) {
    this.abilitazioneStd = sysab3;
  }




  /**
   * @return Ritorna sysabc.
   */
  public String getAbilitazioneContratti() {
    return abilitazioneContratti;
  }

  /**
   * @param sysabc
   *        sysabc da settare internamente alla classe.
   */
  public void setAbilitazioneContratti(String sysabc) {
    this.abilitazioneContratti = sysabc;
  }

  /**
   * @return Ritorna sysabg.
   */
  public String getAbilitazioneGare() {
    return abilitazioneGare;
  }

  /**
   * @param sysabg
   *        sysabg da settare internamente alla classe.
   */
  public void setAbilitazioneGare(String sysabg) {
    this.abilitazioneGare = sysabg;
  }

  /**
   * @return Ritorna syslic.
   */
  public Integer getLivelloContratti() {
    return livelloContratti;
  }

  /**
   * @param syslic
   *        syslic da settare internamente alla classe.
   */
  public void setLivelloContratti(Integer syslic) {
    this.livelloContratti = syslic;
  }

  /**
   * @return Ritorna syslig.
   */
  public Integer getLivelloGare() {
    return livelloGare;
  }

  /**
   * @param syslig
   *        syslig da settare internamente alla classe.
   */
  public void setLivelloGare(Integer syslig) {
    this.livelloGare = syslig;
  }

  /**
   * @return Ritorna sysliv.
   */
  public Integer getLivelloStd() {
    return livelloStd;
  }

  /**
   * @param sysliv
   *        sysliv da settare internamente alla classe.
   */
  public void setLivelloStd(Integer sysliv) {
    this.livelloStd = sysliv;
  }

  /**
   * @return Returns the flagLdap.
   */
  public Integer getFlagLdap() {
    return flagLdap;
  }

  /**
   * @param flagLdap
   *        The flagLdap to set.
   */
  public void setFlagLdap(Integer flagLdap) {
    this.flagLdap = flagLdap;
  }

  /**
   * @return Returns the dn.
   */
  public String getDn() {
    return dn;
  }

  /**
   * @param dn
   *        The dn to set.
   */
  public void setDn(String dn) {
    this.dn = dn;
  }

  /**
   * @return Ritorna utenteAbilitato.
   */
  public Integer getUtenteDisabilitato() {
    return utenteDisabilitato;
  }

  public boolean isNotAbilitato() {
    boolean result = false;
    if (utenteDisabilitato != null) {
      if (CostantiDettaglioAccount.DISABILITATO.equals(utenteDisabilitato.toString()))
        result = true;
    }
    return result;
  }

  /**
   * @param utenteAbilitato
   *        utenteAbilitato da settare internamente alla classe.
   */
  public void setUtenteDisabilitato(Integer utenteAbilitato) {
    this.utenteDisabilitato = utenteAbilitato;
  }

  /**
   * Metodo di comparazione utilizzato in fase di login per potere sfruttare la
   * sort della classe Collections
   *
   * @param utente
   * @return
   */
  @Override
  public int compareTo(Account utente) {
    Account account = utente;
    return login.compareTo(account.getLogin());
  }

  /**
   * @return Ritorna codfisc.
   */
  public String getCodfisc() {
    return codfisc;
  }

  /**
   * @param codfisc
   *        codice fiscale da settare internamente alla classe.
   */
  public void setCodfisc(String codfisc) {
    this.codfisc = codfisc;
  }

  /**
   *
   * @return abilitazioneAP
   */
  public String getAbilitazioneAP() {
    return abilitazioneAP;
  }

  /**
   *
   * @param abilitazioneAP
   */
  public void setAbilitazioneAP(String abilitazioneAP) {
    this.abilitazioneAP = abilitazioneAP;
  }

  /**
   * @return Ritorna ultimoAccesso.
   */
  public Date getUltimoAccesso() {
    return ultimoAccesso;
  }

  /**
   * @param ultimoAccesso ultimoAccesso da settare internamente alla classe.
   */
  public void setUltimoAccesso(Date ultimoAccesso) {
    this.ultimoAccesso = ultimoAccesso;
  }

}