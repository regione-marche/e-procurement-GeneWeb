/*
 * Created on 30/mar/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.admin;

import java.io.Serializable;

/**
 * Bean contenente i parametri di filtro nella query per l'estrazione degli
 * utenti dalla USRSYS
 *
 * @author Stefano.Sabbadin
 */
public class TrovaAccount implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = 4468871267759725715L;

  /** descrizione dell'utente, corrispondente al campo USRSYS.SYSUTE */
  private String            descrizione;
  /** operatore di confronto del campo USRSYS.SYSUTE */
  private String            operatoreDescrizione;
  /** comando di escape per la descrizione con caratteri speciali */
  private String            escapeDescrizione;
  /** login dell'utente, corrispondente al campo USRSYS.SYSLOGIN */
  private String            nome;
  /** operatore di confronto del campo USRSYS.SYSLOGIN */
  private String            operatoreNome;
  /** comando di escape per il nome con caratteri speciali */
  private String            escapeNome;
  /** utente disabilitato, corrispondente al campo USRSYS.SYSDISAB */
  private String            utenteDisabilitato;
  /** utente LDAP, corrispondente al campo USRSYS.FLAG_LDAP */
  private String            utenteLDAP;
  /** true se la ricerca non è case sensitive, false altrimenti */
  private boolean           noCaseSensitive;
  /** ufficio di appartenenza. */
  private String            ufficioAppartenenza;
  /** categoria. */
  private String            categoria;
  /** descrizione dell'utente, corrispondente al campo USRSYS.SYSUTE */
  private String            codiceFiscale;
  /** operatore di confronto del campo operatoreCodiceFiscale */
  private String            operatoreCodiceFiscale;
  /** comando di escape per la descrizione con caratteri speciali */
  private String            escapeCodiceFiscale;
  /** campo eMail inserito dall'utente */
  private String            eMail;
  /** operatore di confronto del campo eMail*/
  private String            operatoreEMail;
  /** comando di escape per la descrizione con caratteri speciali */
  private String            escapeEMail;
  /** campo uffint inserito dall'utente */
  private String            uffint;
  /** operatore di confronto del campo uffint */
  private String            operatoreUffint;
  /** comando di escape per la descrizione con caratteri speciali */
  private String            escapeUffint;
  
  private String            gestioneUtenti;
  
  private String            amministratore;

  /**
   * Costruttore vuoto
   */
  public TrovaAccount() {
    this.descrizione = null;
    this.operatoreDescrizione = null;
    this.escapeDescrizione = null;
    this.nome = null;
    this.operatoreNome = null;
    this.escapeNome = null;
    this.utenteDisabilitato = null;
    this.utenteLDAP = null;
    this.noCaseSensitive = false;
    this.ufficioAppartenenza = null;
    this.categoria = null;
    this.codiceFiscale = null;
    this.operatoreCodiceFiscale = null;
    this.eMail = null;
    this.operatoreEMail = null;
    this.uffint = null;
    this.operatoreUffint = null;
    this.gestioneUtenti = null;
    this.amministratore = null;
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
   * @return Ritorna descrizione_conf.
   */
  public String getOperatoreDescrizione() {
    return operatoreDescrizione;
  }

  /**
   * @param descrizione_conf descrizione_conf da settare internamente alla classe.
   */
  public void setOperatoreDescrizione(String descrizione_conf) {
    this.operatoreDescrizione = descrizione_conf;
  }

  /**
   * @return Ritorna escapeDescrizione.
   */
  public String getEscapeDescrizione() {
    return escapeDescrizione;
  }

  /**
   * @param escapeDescrizione escapeDescrizione da settare internamente alla classe.
   */
  public void setEscapeDescrizione(String escapeDescrizione) {
    this.escapeDescrizione = escapeDescrizione;
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
   * @return Ritorna escapeNome.
   */
  public String getEscapeNome() {
    return escapeNome;
  }

  /**
   * @param escapeNome escapeNome da settare internamente alla classe.
   */
  public void setEscapeNome(String escapeNome) {
    this.escapeNome = escapeNome;
  }

  /**
   * @return Ritorna utenteDisabilitato.
   */
  public String getUtenteDisabilitato() {
    return utenteDisabilitato;
  }

  /**
   * @param utenteDisabilitato
   *        utenteDisabilitato da settare internamente alla classe.
   */
  public void setUtenteDisabilitato(String utenteDisabilitato) {
    this.utenteDisabilitato = utenteDisabilitato;
  }

  /**
   * @return Ritorna utenteLDAP.
   */
  public String getUtenteLDAP() {
    return utenteLDAP;
  }

  /**
   * @param utenteLDAP
   *        utenteLDAP da settare internamente alla classe.
   */
  public void setUtenteLDAP(String utenteLDAP) {
    this.utenteLDAP = utenteLDAP;
  }

  /**
   * @return Ritorna noCaseSensitive.
   */
  public boolean isNoCaseSensitive() {
    return noCaseSensitive;
  }

  /**
   * @param noCaseSensitive
   *        noCaseSensitive da settare internamente alla classe.
   */
  public void setNoCaseSensitive(boolean noCaseSensitive) {
    this.noCaseSensitive = noCaseSensitive;
  }


  /**
   * @return Ritorna ufficioAppartenenza.
   */
  public String getUfficioAppartenenza() {
    return ufficioAppartenenza;
  }


  /**
   * @param ufficioAppartenenza ufficioAppartenenza da settare internamente alla classe.
   */
  public void setUfficioAppartenenza(String ufficioAppartenenza) {
    this.ufficioAppartenenza = ufficioAppartenenza;
  }

  /**
   * @param codiceFiscale
   */
  public void setCodiceFiscale(String codiceFiscale) {
    this.codiceFiscale = codiceFiscale;
  }
  
  /**
   * @return Ritorna codiceFiscale.
   */
  public String getCodiceFiscale() {
    return codiceFiscale;
  }
  
  /**
   * @param operatoreCodiceFiscale operatoreCodiceFiscale da settare internamente alla classe.
   */
  public void setOperatoreCodiceFiscale(String operatoreCodiceFiscale) {
    this.operatoreCodiceFiscale = operatoreCodiceFiscale;
  }
  
  /**
   * @return Ritorna OperatoreCodiceFiscale.
   */
  public String getOperatoreCodiceFiscale() {
    return operatoreCodiceFiscale;
  }
  
  /**
  * @param eMail
  */
  public void seteMail(String eMail) {
    this.eMail = eMail;
  }
  
  /**
   * @return Ritorna eMail.
   */
  public String geteMail() {
    return eMail;
  }
  

  /**
   * @param operatoreEMail operatoreEMail da settare internamente alla classe.
   */
  public void setOperatoreEMail(String operatoreEMail) {
    this.operatoreEMail = operatoreEMail;
  }
  
  /**
   * @return Ritorna operatoreEMail.
   */
  public String getOperatoreEMail() {
    return operatoreEMail;
  }
  
  /**
   * @param escapeEMail escapeEMail da settare internamente alla classe.
   */
  public void setEscapeEMail(String escapeEMail) {
    this.escapeEMail = escapeEMail;
  }
  
  /**
   * @return Ritorna escapeEMail.
   */
  public String getEscapeEMail() {
    return escapeEMail;
  }
  
  /**
   * @param escapeCodiceFiscale escapeCodiceFiscale da settare internamente alla classe.
   */
  public void setEscapeCodiceFiscale(String escapeCodiceFiscale) {
    this.escapeCodiceFiscale = escapeCodiceFiscale;
  }
  
  /**
   * @return Ritorna escapeCodiceFiscale.
   */
  public String getEscapeCodiceFiscale() {
    return escapeCodiceFiscale;
  }
  
  /**
   * @return Ritorna categoria.
   */
  public String getCategoria() {
    return categoria;
  }

  /**
   * @param categoria.
   */
  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }
  
  /**
   * @return Ritorna uffint.
   */
  public String getUffint() {
    return uffint;
  }
  
  /**
   * @param uffint codice ufficio intesattario.
   */
  public void setUffint(String uffint) {
    this.uffint = uffint;
  }
  
  /**
   * @param operatoreUffint operatoreUffint da settare internamente alla classe.
   */
  public void setOperatoreUffint(String operatoreUffint) {
    this.operatoreUffint = operatoreUffint;
  }
  
  /**
   * @return Ritorna operatoreUffint.
   */
  public String getOperatoreUffint() {
    return operatoreUffint;
  }
  
  /**
   * @param escapeUffint escapeUffint da settare internamente alla classe.
   */
  public void setEscapeUffint(String escapeUffint) {
    this.escapeUffint = escapeUffint;
  }
  
  /**
   * @return Ritorna escapeUffint.
   */
  public String getEscapeUffint() {
    return escapeUffint;
  }

  
  public String getGestioneUtenti() {
    return gestioneUtenti;
  }

  
  public void setGestioneUtenti(String gestioneUtenti) {
    this.gestioneUtenti = gestioneUtenti;
  }

  
  public String getAmministratore() {
    return amministratore;
  }

  
  public void setAmministratore(String amministratore) {
    this.amministratore = amministratore;
  }
  
  
}