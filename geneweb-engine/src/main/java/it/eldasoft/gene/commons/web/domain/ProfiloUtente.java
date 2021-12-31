/*
 * Created on 6-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.domain;

import it.eldasoft.utils.profiles.FiltroLivelloUtente;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.lang.StringUtils;

/**
 * Contenitore da utilizzare come oggetto da porre in sessione per l'utente
 * autenticato
 *
 * @author Stefano.Sabbadin
 */
public class ProfiloUtente implements Serializable, HttpSessionActivationListener {

  /**
   * UID.
   */
  private static final long serialVersionUID = -472172412974490373L;

  /** identificativo univoco nel database dell'utente */
  private int       id;

  /** Login utilizzato per l'autenticazione dell'utente */
  private String    login;

  /** L'autenticazione &egrave; avvenuta mediante single sign on? */
  private boolean   autenticazioneSSO;

  /** Nome dell'utente autenticato e connesso all'applicazione */
  private String    nome;

  /** Password utente connesso all'applicazione (in formato criptato) */
  private String    pwd;

  /*
   * L.G. 17/10/2006: aggiunto un array di Stringhe contenente le opzioni
   * specifiche per ciascun utente
   */
  /** Set delle funzionalità dell'applicazione abilitate all'utente */
  private String[]  funzioniUtenteAbilitate;


  /** Set delle funzionalità dell'applicazione abilitate all'utente */
  private String[]  opzioniPrivilegi;

  /**
   * Array con l'elenco dei gruppi di appartenenza dell'utente, filtrati per
   * codice profilo attivo
   */
  private Integer[] idGruppi;

  /** Opzioni utente direttamente dal campo SYSPWBOU di USRSYS */
  private String    opzioniUtente;
  /** Abilitazione standard */
  private String    abilitazioneStd;
  /** Abilitazione gare */
  private String    abilitazioneGare;
  /** Abilitazione contratti */
  private String    abilitazioneContratti;
  /** Livello standard */
  private String    livelloStd;
  /** Livello gare */
  private String    livelloGare;
  /** Livello contratti */
  private String    livelloContratti;

  /** L'utente è un account di dominio */
  private Integer   utenteLdap;

  /** Ulteriori dati parametrici da settare all'utente all'atto della login */
  private HashMap<String, String>   parametriUtente;

  /** Livello standard */
  private String    ufficioAppartenenza;

  /** Ruolo utente Mercato elettronico */
  private String    ruoloUtenteMercatoElettronico;

  /** Indirizzo IP da cui risulta connesso l'utente. */
  private String ip;

  /** Data di accesso dell'utente al sistema. */
  private Date dataAccesso;

  /** Indirizzo mail dell'utente. */
  private String mail;

  /** Codice fiscale dell'utente. */
  private String codiceFiscale;

  /** Abilitazione AP **/
  private String abilitazioneAP;

  /**
   * Costruttore vuoto
   */
  public ProfiloUtente() {
    this.id = 0;
    this.login = null;
    this.autenticazioneSSO = false;
    this.nome = null;
    this.pwd = null;
    this.idGruppi = null;
    this.funzioniUtenteAbilitate = null;
    this.opzioniUtente = null;
    this.abilitazioneStd = null;
    this.abilitazioneGare = null;
    this.abilitazioneContratti = null;
    this.livelloStd = null;
    this.livelloGare = null;
    this.livelloContratti = null;
    this.utenteLdap = null;
    this.parametriUtente = new HashMap<String, String>();
    this.ufficioAppartenenza = null;
    this.ip = null;
    this.dataAccesso = null;
    this.ruoloUtenteMercatoElettronico=null;
    this.mail=null;
    this.codiceFiscale=null;
    this.abilitazioneAP=null;
  }

  /**
   * @return Ritorna id.
   */
  public int getId() {
    return id;
  }

  /**
   * @param id
   *        id da settare internamente alla classe.
   */
  public void setId(int id) {
    this.id = id;
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
   */
  public void setLogin(String login) {
    this.login = login;
  }

  /**
   * @return Ritorna autenticazioneSSO.
   */
  public boolean isAutenticazioneSSO() {
    return autenticazioneSSO;
  }

  /**
   * @param autenticazioneSSO autenticazioneSSO da settare internamente alla classe.
   */
  public void setAutenticazioneSSO(boolean autenticazioneSSO) {
    this.autenticazioneSSO = autenticazioneSSO;
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
   * @return Ritorna idGruppi.
   */
  public Integer[] getIdGruppi() {
    return idGruppi;
  }

  /**
   * @param idGruppi
   *        idGruppi da settare internamente alla classe.
   */
  public void setIdGruppi(Integer[] idGruppi) {
    this.idGruppi = idGruppi;
  }

  /**
   * @return Ritorna opzioniUtenteAbilitate.
   */
  public String[] getFunzioniUtenteAbilitate() {
    return funzioniUtenteAbilitate;
  }

  /**
   * @param opzioniUtenteAbilitate
   *        opzioniUtenteAbilitate da settare internamente alla classe.
   */
  public void setFunzioniUtenteAbilitate(String[] opzioniUtenteAbilitate) {
    this.funzioniUtenteAbilitate = opzioniUtenteAbilitate;
  }

  public String[] getOpzioniPrivilegi() {
	return opzioniPrivilegi;
}

public void setOpzioniPrivilegi(String[] privilegi) {
	this.opzioniPrivilegi = privilegi;
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
  public String getLivelloContratti() {
    return livelloContratti;
  }

  /**
   * @param syslic
   *        syslic da settare internamente alla classe.
   */
  public void setLivelloContratti(String syslic) {
    this.livelloContratti = syslic;
  }

  /**
   * @return Ritorna syslig.
   */
  public String getLivelloGare() {
    return livelloGare;
  }

  /**
   * @param syslig
   *        syslig da settare internamente alla classe.
   */
  public void setLivelloGare(String syslig) {
    this.livelloGare = syslig;
  }

  /**
   * @return Ritorna sysliv.
   */
  public String getLivelloStd() {
    return livelloStd;
  }

  /**
   * @param sysliv
   *        sysliv da settare internamente alla classe.
   */
  public void setLivelloStd(String sysliv) {
    this.livelloStd = sysliv;
  }

  /**
   * @return Ritorna sysuffapp.
   */
  public String getUfficioAppartenenza() {
    return ufficioAppartenenza;
  }

  /**
   * @param sysuffapp
   *        sysuffapp da settare internamente alla classe.
   */
  public void setUfficioAppartenenza(String sysuffapp) {
    this.ufficioAppartenenza = sysuffapp;
  }


  /**
   * @return Ritorna meruolo.
   */
  public String getRuoloUtenteMercatoElettronico() {
    return ruoloUtenteMercatoElettronico;
  }

  /**
   * @param sysuffapp
   *        sysuffapp da settare internamente alla classe.
   */
  public void setRuoloUtenteMercatoElettronico(String meruolo) {
    this.ruoloUtenteMercatoElettronico = meruolo;
  }

  /**
   * @return Ritorna opzioniUtente.
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
   * @return Returns the utenteLdap.
   */
  public Integer getUtenteLdap() {
    return utenteLdap;
  }

  /**
   * @param utenteLdap
   *        The utenteLdap to set.
   */
  public void setUtenteLdap(Integer utenteLdap) {
    this.utenteLdap = utenteLdap;
  }

  /**
   * @return Ritorna parametriUtente.
   */
  public HashMap<String, String> getParametriUtente() {
    return parametriUtente;
  }

  /**
   * @param parametriUtente
   *        parametriUtente da settare internamente alla classe.
   */
  public void setParametriUtente(HashMap<String, String> parametriUtente) {
    this.parametriUtente = parametriUtente;
  }

  /**
   * @return Ritorna pwd.
   */
  public String getPwd() {
    return pwd;
  }

  /**
   * @param pwd pwd da settare internamente alla classe.
   */
  public void setPwd(String pwd) {
    this.pwd = pwd;
  }

  /**
   * @return Ritorna ip.
   */
  public String getIp() {
    return ip;
  }

  /**
   * @param ip ip da settare internamente alla classe.
   */
  public void setIp(String ip) {
    this.ip = ip;
  }

  /**
   * @return Ritorna dataAccesso.
   */
  public Date getDataAccesso() {
    return dataAccesso;
  }

  /**
   * @param dataAccesso dataAccesso da settare internamente alla classe.
   */
  public void setDataAccesso(Date dataAccesso) {
    this.dataAccesso = dataAccesso;
  }

  /**
   * @return the mail
   */
  public String getMail() {
    return mail;
  }

  /**
   * @param mail the mail to set
   */
  public void setMail(String mail) {
    this.mail = mail;
  }

  /**
   * @return the codiceFiscale
   */
  public String getCodiceFiscale() {
    return codiceFiscale;
  }

  /**
   * @param codiceFiscale the codiceFiscale to set
   */
  public void setCodiceFiscale(String codiceFiscale) {
    this.codiceFiscale = codiceFiscale;
  }

  /**
   * @return abilitazioneAP
   */
  public String getAbilitazioneAP() {
    return abilitazioneAP;
  }


  /**
   * @param abilitazioneAP
   */
  public void setAbilitazioneAP(String abilitazioneAP) {
    this.abilitazioneAP = abilitazioneAP;
  }


  /**
   * Metodo per ottenere l'oggetto FiltroLivelloUtente a partire dal profilo
   * dell'utente stesso
   *
   * @return Ritorna l'oggetto FiltroLivelloUtente relativo al profilo utente in
   *         analisi
   */
  public FiltroLivelloUtente getFiltroLivelloUtente() {
    return new FiltroLivelloUtente(this.id, this.opzioniUtente,
        this.abilitazioneStd != null ? this.abilitazioneStd : "N",
        this.abilitazioneGare != null ? this.abilitazioneGare : "NDEFM",
        this.abilitazioneContratti != null
            ? this.abilitazioneContratti
            : "NDEFM",
        // this.livelloGare != null ? new Integer(this.livelloGare).intValue() :
        // 0,
        this.livelloContratti != null
            ? new Integer(this.livelloContratti).intValue()
            : 0);
  }

  /**
   * Nel momento in cui si ricostruisce l'oggetto in sessione si aggiunge la sessione in un elenco temporaneo parcheggiato nel context, per
   * essere poi letto allo startup dell'applicativo.
   *
   * @param event
   *        evento contenente la sessione interessata da riattivazione/deserializzazione
   */
  public void sessionDidActivate(HttpSessionEvent event) {
    ServletContext context = event.getSession().getServletContext();
    synchronized (context) {
      @SuppressWarnings("unchecked")
      Map<String, HttpSession> hashSessioni = (Map<String, HttpSession>)context.getAttribute(CostantiGenerali.ID_MAP_TEMPORANEA_SESSIONI_RIATTIVATE);
      if (hashSessioni == null) {
        hashSessioni = new HashMap<String, HttpSession>();
        context.setAttribute(CostantiGenerali.ID_MAP_TEMPORANEA_SESSIONI_RIATTIVATE, hashSessioni);
      }
      hashSessioni.put(event.getSession().getId(), event.getSession());
    }
  }

  public void sessionWillPassivate(HttpSessionEvent arg0) {
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("");

    str.append("id=");
    str.append(StringUtils.isNotEmpty(""+this.id) ? this.id : "");
    str.append(", ");

    str.append("isSSO=");
    str.append(this.autenticazioneSSO);
    str.append(", ");

    str.append("login=");
    str.append(StringUtils.isNotEmpty(this.login) ? this.login : "");
    str.append(", ");

    str.append("pwd=");
    str.append(StringUtils.isNotEmpty(this.pwd) ? this.pwd : "");
    str.append(", ");

    str.append("idGruppi=[");
    StringBuilder tmpStr = new StringBuilder("");
    for (int i=0; i < this.idGruppi.length; i++) {
      tmpStr.append(this.idGruppi[i]);
      tmpStr.append(";");
    }
    str.append(StringUtils.isNotEmpty(tmpStr.toString()) ? tmpStr.toString() : "");
    str.append("], ");

    str.append("funzioniUtenteAbilitate=[");
    tmpStr = new StringBuilder("");
    for (int i=0; i < this.funzioniUtenteAbilitate.length; i++) {
      tmpStr.append(this.funzioniUtenteAbilitate[i]);
      tmpStr.append(";");
    }
    str.append(StringUtils.isNotEmpty(tmpStr.toString()) ? tmpStr.toString() : "");
    str.append("], ");

    str.append("opzioniUtente=");
    str.append(StringUtils.isNotEmpty(this.opzioniUtente) ? this.opzioniUtente : "");
    str.append(", ");

    str.append("abilitazioneStd=");
    str.append(StringUtils.isNotEmpty(this.abilitazioneStd) ? this.abilitazioneStd : "");
    str.append(", ");

    str.append("abilitazioneGare=");
    str.append(StringUtils.isNotEmpty(this.abilitazioneGare) ? this.abilitazioneGare : "");
    str.append(", ");

    str.append("abilitazioneContratti=");
    str.append(StringUtils.isNotEmpty(this.abilitazioneContratti) ? this.abilitazioneContratti : "");
    str.append(", ");

    str.append("livelloStd=");
    str.append(StringUtils.isNotEmpty(this.livelloStd) ? this.livelloStd : "");
    str.append(", ");

    str.append("livelloGare=");
    str.append(StringUtils.isNotEmpty(this.livelloGare) ? this.livelloGare : "");
    str.append(", ");

    str.append("livelloContratti=");
    str.append(StringUtils.isNotEmpty(this.livelloContratti) ? this.livelloContratti : "");
    str.append(", ");

    str.append("utenteLdap=");
    if (this.utenteLdap != null) {
      str.append(StringUtils.isNotEmpty(this.utenteLdap.toString()) ? this.utenteLdap.toString() : "");
    }
    str.append(", ");

    str.append("parametriUtente={");
    if (this.parametriUtente != null && this.parametriUtente.size() > 0) {
      Iterator<Entry<String, String>> iter = this.parametriUtente.entrySet().iterator();
      while (iter.hasNext()) {
        Entry<String, String> entry = iter.next();
        str.append(StringUtils.isNotEmpty(entry.getKey()) ? entry.getKey() : "");
        str.append("=");
        str.append(StringUtils.isNotEmpty(entry.getValue()) ? entry.getValue() : "");
        str.append("; ");
      }
    }
    str.append("}, ");

    str.append("ufficioAppartenenza=");
    str.append(StringUtils.isNotEmpty(this.ufficioAppartenenza) ? this.ufficioAppartenenza : "");
    str.append(", ");

    str.append("ruoloUtenteMercatoElettronico=");
    str.append(StringUtils.isNotEmpty(this.ruoloUtenteMercatoElettronico) ? this.ruoloUtenteMercatoElettronico : "");
    str.append(", ");

    str.append("ip=");
    str.append(StringUtils.isNotEmpty(this.ip) ? this.ip : "");
    str.append(", ");

    str.append("dataAccesso=");
    if (this.dataAccesso != null) {
      str.append(UtilityDate.convertiData(this.dataAccesso, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    }
    str.append(", ");

    str.append("mail=");
    if (this.mail != null) {
      str.append(this.mail);
    }
    str.append(", ");

    str.append("codiceFiscale=");
    if (this.codiceFiscale != null) {
      str.append(this.codiceFiscale);
    }

    str.append(", ");
    str.append("abilitazioneAP=");
    if (this.abilitazioneAP != null) {
      str.append(this.abilitazioneAP);
    }


    return str.toString();
  }



}