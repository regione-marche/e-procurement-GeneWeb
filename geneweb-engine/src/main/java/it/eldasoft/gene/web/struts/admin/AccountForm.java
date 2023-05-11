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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.DispatchForm;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Form di gestione dell'Account
 *
 * @author cit_defilippis
 */
public class AccountForm extends DispatchForm {

  /** UID */
  private static final long serialVersionUID    = -989053977808439700L;

  private final String[]          listaTextSiNo       = CostantiGeneraliAccount.LISTA_SI_NO;
  private final String[]          listaTextPrivilegi  = CostantiGeneraliAccount.LISTA_TEXT_PRIVILEGI;
  private final String[]          listaValuePrivilegi = CostantiGeneraliAccount.LISTA_VALUE_PRIVILEGI;
  private final String[]          listaTextPrivilegiArt80  = CostantiGeneraliAccount.LISTA_TEXT_PRIVILEGI_ART80;
  private final String[]          listaValuePrivilegiArt80 = CostantiGeneraliAccount.LISTA_VALUE_PRIVILEGI_ART80;
  private final String[]          listaTextGare       = CostantiGeneraliAccount.LISTA_TEXT_GARE;
  private final String[]          listaTextLavori     = CostantiGeneraliAccount.LISTA_TEXT_LAVORI;
  private final String[]          listaValueLavori    = CostantiGeneraliAccount.LISTA_VALUE_LAVORI;
  private final String[]          listaTextGenric     = CostantiGeneraliAccount.LISTA_TEXT_GENRIC;
  private final String[]          listaValueGenric    = CostantiGeneraliAccount.LISTA_VALUE_GENRIC;
  private final String[]          listaTextGenmod     = CostantiGeneraliAccount.LISTA_TEXT_GENMOD;
  private final String[]          listaValueGenmod    = CostantiGeneraliAccount.LISTA_VALUE_GENMOD;
  private final String[]          listaTextQform     = CostantiGeneraliAccount.LISTA_TEXT_QFORM;
  private final String[]          listaValueQform    = CostantiGeneraliAccount.LISTA_VALUE_QFORM;
  private final String[]          listaTextScadenza  =  CostantiGeneraliAccount.LISTA_TEXT_SCADENZA;
  private final String[]          listaTextAP         = CostantiGeneraliAccount.LISTA_TEXT_AP;
  private final String[]          listaValueAP        = CostantiGeneraliAccount.LISTA_VALUE_AP;
  private final String[]          listaTextSelOp     = CostantiGeneraliAccount.LISTA_TEXT_SELOP;
  private final String[]          listaValueSelOp    = CostantiGeneraliAccount.LISTA_VALUE_SELOP;


  private String            idAccount;
  private String            login;
  private String            password;
  private String            vecchiaPassword;
  private String            nome;
  private OpzioniUtente     opzioniUtente;
  private String[]          opzioniUtenteSys;
  private String[]          opzioniPrivilegi;
  private String            utenteDisabilitato;

  // la variabile modo è usata per stabilire se siamo in inserimento e quindi
  // evitiamo problemi di history
  private String            modo;
  private String            flagLdap;
  private String            dn;
  private boolean           opzioniNote;

  /** Corrisponde al valore * nell'opzione utente ou9* */
  private String            livelloUtenteDaOpzioni;

  private Integer           livelloLavori;
  private String            abilitazioneLavori;
  private Integer           livelloGare;
  private String            abilitazioneGare;
  private Integer           livelloContratti;
  private String            abilitazioneContratti;
  private String            email;
  private String            scadenzaAccount;
  private String            ufficioAppartenenza;
  private String            ruoloUtenteMercatoElettronico;
  private String            categoria;
  private String            codfisc;
  private String            abilitazioneAP;


  /**
   * Costruttore con parametri prepara il form per la visualizzazione e per la
   * modifica
   *
   * @param account
   */
  public AccountForm(Account account) {
    super();

    this.idAccount = String.valueOf(account.getIdAccount());
    this.login = account.getLogin();
    this.password = account.getPassword();
    this.vecchiaPassword = account.getPassword();
    this.nome = account.getNome();
    this.flagLdap = UtilityStringhe.convertiNullInStringaVuota(UtilityNumeri.convertiIntero(account.getFlagLdap()));
    this.dn = UtilityStringhe.convertiNullInStringaVuota(account.getDn());

    if (account.getOpzioniUtente() != null) {
      this.opzioniUtente = new OpzioniUtente(account.getOpzioniUtente());
      this.opzioniUtenteSys = this.opzioniUtente.getElencoOpzioni();
      this.livelloUtenteDaOpzioni = String.valueOf(this.opzioniUtente.getLivello());
    }

    this.opzioniPrivilegi = null;
    if (account.getOpzioniPrivilegi() != null)
    	this.opzioniPrivilegi = account.getOpzioniPrivilegi().split("\\|");
    this.abilitazioneLavori = account.getAbilitazioneStd();
    this.livelloLavori = account.getLivelloStd();
    this.abilitazioneGare = account.getAbilitazioneGare();
    this.livelloGare = account.getLivelloGare();
    this.abilitazioneContratti = account.getAbilitazioneContratti();
    this.livelloContratti = account.getLivelloContratti();
    if (account.getUtenteDisabilitato() == null)
      this.utenteDisabilitato = CostantiDettaglioAccount.ABILITATO;
    else
      this.utenteDisabilitato = account.getUtenteDisabilitato().toString();
    this.ufficioAppartenenza=String.valueOf(account.getUfficioAppartenenza());
    this.email = account.getEmail();
    this.codfisc = account.getCodfisc();
    this.abilitazioneAP = account.getAbilitazioneAP();
    this.scadenzaAccount = UtilityDate.convertiData(account.getScadenzaAccount(),UtilityDate.FORMATO_GG_MM_AAAA);
    if(account.getRuoloUtenteMercatoElettronico()!=null)
      this.ruoloUtenteMercatoElettronico=String.valueOf(account.getRuoloUtenteMercatoElettronico());
    if(account.getCategoria()!=null)
      this.categoria=String.valueOf(account.getCategoria());
  }

  /**
   * Costruttore senza parametri Prepara il form per un nuovo account
   */
  public AccountForm() {
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
    this.password = "";
    this.vecchiaPassword = null;
    this.nome = null;
    this.opzioniUtente = null;
    this.opzioniUtenteSys = null;
    this.opzioniPrivilegi = null;
    this.livelloUtenteDaOpzioni = null;
    this.flagLdap = "0";
    this.dn = null;
    this.opzioniNote = false;
    this.livelloLavori = null;
    this.abilitazioneLavori = null;
    this.livelloGare = null;
    this.abilitazioneGare = null;
    this.livelloContratti = null;
    this.abilitazioneContratti = null;
    this.utenteDisabilitato = null;
    this.ufficioAppartenenza = null;
    this.email = null;
    this.scadenzaAccount = null;
    this.ruoloUtenteMercatoElettronico = null;
    this.categoria = null;
    this.codfisc=null;
    this.abilitazioneAP=null;
  }

  /**
   *
   * Funzione che restituisce l'account per essere salvato
   *
   * @param Account
   *        riceve in input un account che è vuoto se siamo in inserimento,
   *        invece è l'account salvato in db se siamo in modifica
   * @param codiceApplicazione
   *        codice applicazione del modulo attivo dal quale si gestisce
   *        l'aggiornamento dell'utente
   *
   * @return Account
   */
  public Account getDatiPerModel(Account accountDb, String codiceApplicazione) {
    Account account = new Account();
    if (accountDb.getDataInserimento() == null)
      account.setDataInserimento(new Date());
    else
      account.setDataInserimento(accountDb.getDataInserimento());

    int id;
    if (this.idAccount != null) {
      id = Integer.parseInt(this.idAccount);
      account.setIdAccount(id);
    }

    // F.D. 02/09/08 gestione login in lower per dati non case sensitive

    account.setLogin(this.login.trim());

    account.setPassword(this.password.trim());
    account.setNome(this.nome);

    // determinazione delle opzioni utente gestibili dall'applicativo
    String opzioniUtenteGestiteGene = ConfigManager.getValore(CostantiGenerali.PROP_OPZIONI_UTENTE_GESTITE
        + CostantiGenerali.SEPARATORE_PROPERTIES
        + CostantiGenerali.CODICE_APPLICAZIONE_LIBRERIA_GENERALE_WEB);
    String opzioniUtenteGestiteApplicazione = null;
    if (!CostantiGenerali.CODICE_APPLICAZIONE_LIBRERIA_GENERALE_WEB.equals(codiceApplicazione))
      opzioniUtenteGestiteApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_OPZIONI_UTENTE_GESTITE
          + CostantiGenerali.SEPARATORE_PROPERTIES
          + codiceApplicazione);
    // si crea l'elenco delle opzioni gestite come la somma della parte generale
    // più quella specifica dell'applicativo
    OpzioniUtente opzioniUtenteGestite = new OpzioniUtente(
        opzioniUtenteGestiteGene
            + (opzioniUtenteGestiteApplicazione != null
                ? (OpzioniUtente.SEPARATORE_OPZIONI + opzioniUtenteGestiteApplicazione)
                : ""));

    // si crea l'elenco delle opzioni di partenza a partire dall'account
    // ricevuto (versione precedente le modifiche oppure un record vuoto nel
    // caso di inserimento; per quest'ultimo allora si prevede
    // l'inizializzazione con le opzioni di default)
    OpzioniUtente opzioniAccountDb = null;
    if (accountDb.getOpzioniUtente() == null)
      opzioniAccountDb = new OpzioniUtente(
          ConfigManager.getValore(CostantiGenerali.PROP_OPZIONI_UTENTE_DEFAULT));
    else
      opzioniAccountDb = new OpzioniUtente(accountDb.getOpzioniUtente());

    // si cicla su tali opzioni per verificarne la configurazione appena settata
    String opzioneUtente = null;
    OpzioniUtente opzioniUtenteForm = new OpzioniUtente(
        UtilityStringhe.serializza(this.getOpzioniUtenteSys(),
            OpzioniUtente.SEPARATORE_OPZIONI));
    for (int i = 0; i < opzioniUtenteGestite.getElencoOpzioni().length; i++) {
      opzioneUtente = opzioniUtenteGestite.getElencoOpzioni()[i];
      // controllo l'opzione gestita: se è settata nel form ricevuto, allora la
      // setto nelle opzioni precedenti le modifiche, altrimenti la deseleziono
      if (opzioniUtenteForm.isOpzionePresente(opzioneUtente))
        opzioniAccountDb.setOpzione(opzioneUtente);
      else
        opzioniAccountDb.unsetOpzione(opzioneUtente);
    }
    // si gestisce l'eventuale livello utente da opzioni; se arriva valorizzato,
    // allora lo si aggiorna, altrimenti se non arriva valorizzato si mantiene
    // quello esistente che se non fosse presente varrebbe comunque 1 di default
    if (this.livelloUtenteDaOpzioni != null)
      opzioniAccountDb.setLivello(Integer.parseInt(this.livelloUtenteDaOpzioni));
    else
      opzioniAccountDb.setLivello(opzioniAccountDb.getLivello());

    // al termine si impostano le opzioni utente sulla base del dato precedenti
    // le modifiche e delle configurazioni impostate nella pagina
    account.setOpzioniUtente(opzioniAccountDb.toString());

    account.setAbilitazioneStd(this.getAbilitazioneLavori());
    account.setLivelloStd(this.getLivelloLavori());
    account.setAbilitazioneGare(this.getAbilitazioneGare());
    account.setLivelloGare(this.getLivelloGare());
    account.setAbilitazioneContratti(this.getAbilitazioneContratti());
    account.setLivelloContratti(this.getLivelloContratti());

    // Valorizzazione per il campo ABILAP
    if (accountDb.getOpzioniApplicazione() != null
        && accountDb.getOpzioniApplicazione().length() > 0)
      account.setOpzioniApplicazione(accountDb.getOpzioniApplicazione());
    else
      account.setOpzioniApplicazione(null);

    // Valorizzazione per il campo SYSPRI
    String opzPriv = "";
    if (this.getOpzioniPrivilegi() != null) {
	    for (int i=0; i < this.getOpzioniPrivilegi().length; ++i)
	    	if (!this.getOpzioniPrivilegi()[i].equals(""))
	    		opzPriv += this.getOpzioniPrivilegi()[i] + "|";
	    account.setOpzioniPrivilegi(opzPriv);
    }
   /* if (accountDb.getOpzioniPrivilegi() != null
        && accountDb.getOpzioniPrivilegi().length() > 0)
      account.setOpzioniPrivilegi(accountDb.getOpzioniPrivilegi());
    else
      account.setOpzioniPrivilegi(null);*/

    // Valorizzazione per il campo SYSABU
    if (accountDb.getSiglaProvincia() != null
        && accountDb.getSiglaProvincia().length() > 0)
      account.setSiglaProvincia(accountDb.getSiglaProvincia());
    //else
    //  account.setSiglaProvincia(CostantiGeneraliAccount.DEFAULT_ABILITAZIONE_UGC);

    account.setFlagLdap(new Integer(this.flagLdap));
    if (this.dn != null && this.dn.length() > 0)
      account.setDn(this.dn);
    else
      account.setDn(null);

    // se non è checkato il flag "utente disabilitato" setto il dato ad
    // "abilitato"
    if (!CostantiDettaglioAccount.DISABILITATO.equals(this.utenteDisabilitato))
      account.setUtenteDisabilitato(new Integer(
          CostantiDettaglioAccount.ABILITATO));
    else
      account.setUtenteDisabilitato(new Integer(
          CostantiDettaglioAccount.DISABILITATO));
    if (this.ufficioAppartenenza != null && this.ufficioAppartenenza.length()>0)
     account.setUfficioAppartenenza(Integer.valueOf(UtilityStringhe.convertiStringaVuotaInNull(this.ufficioAppartenenza)));
     else
       account.setUfficioAppartenenza(null);
    account.setEmail(this.email);
    account.setCodfisc(UtilityStringhe.convertiStringaVuotaInNull(this.codfisc));
    account.setScadenzaAccount(UtilityDate.convertiData(this.scadenzaAccount, UtilityDate.FORMATO_GG_MM_AAAA));
    account.setAbilitazioneAP(this.getAbilitazioneAP());

    if (this.ruoloUtenteMercatoElettronico != null && this.ruoloUtenteMercatoElettronico.length()>0)
      account.setRuoloUtenteMercatoElettronico(Integer.valueOf(UtilityStringhe.convertiStringaVuotaInNull(this.ruoloUtenteMercatoElettronico)));
      else
        account.setRuoloUtenteMercatoElettronico(null);

    if (this.categoria != null && this.categoria.length()>0)
      account.setCategoria(Integer.valueOf(UtilityStringhe.convertiStringaVuotaInNull(this.categoria)));
      else
        account.setCategoria(null);

    return account;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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

  public String[] getListaValuePrivilegiArt80() {
    return listaValuePrivilegiArt80;
  }

  public String[] getListaTextPrivilegiArt80() {
    return listaTextPrivilegiArt80;
  }


  public String[] getListaValueLavori() {
    return listaValueLavori;
  }

  public String[] getListaValueAP() {
    return listaValueAP;
  }

  public String[] getListaTextLavori() {
    return listaTextLavori;
  }

  public String[] getListaTextAP() {
    return listaTextAP;
  }

  public String[] getListaTextScadenza() {
    return listaTextScadenza;
  }


  public String getVecchiaPassword() {
    return vecchiaPassword;
  }

  public void setVecchiaPassword(String vecchiaPassword) {
    this.vecchiaPassword = vecchiaPassword;
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

  public String[] getListaTextQform() {
    return listaTextQform;
  }

  public String[] getListaValueQform() {
    return listaValueQform;
  }

  /**
   * @param opzioniGestioneProtezioni
   *        The opzioniGestioneProtezioni to set.
   */
  public void setOpzioniUtenteSys(String[] opzioniGestioneProtezioni) {
    this.opzioniUtenteSys = opzioniGestioneProtezioni;
  }

  /**
   * @param opzioniGestioneProtezioni
   *        The opzioniGestioneProtezioni to set.
   */
  public void setOpzioniPrivilegi(String[] opzioniGestionePrivilegi) {
    this.opzioniPrivilegi = opzioniGestionePrivilegi;
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
   * @return Returns the flagLdap.
   */
  public String getFlagLdap() {
    return flagLdap;
  }

  /**
   * @param flagLdap
   *        The flagLdap to set.
   */
  public void setFlagLdap(String flagLdap) {
    this.flagLdap = flagLdap;
  }

  /**
   * @return Returns the modo.
   */
  public String getModo() {
    return modo;
  }

  /**
   * @param modo
   *        The modo to set.
   */
  public void setModo(String modo) {
    this.modo = modo;
  }

  /**
   * @return Returns the opzioniNote.
   */
  public boolean isOpzioniNote() {
    return opzioniNote;
  }

  /**
   * @param opzioniNote
   *        The opzioniNote to set.
   */
  public void setOpzioniNote(boolean opzioniNote) {
    this.opzioniNote = opzioniNote;
  }

  /**
   * @return Ritorna abilitazioneContratti.
   */
  public String getAbilitazioneContratti() {
    return abilitazioneContratti;
  }

  /**
   * @param abilitazioneContratti
   *        abilitazioneContratti da settare internamente alla classe.
   */
  public void setAbilitazioneContratti(String abilitazioneContratti) {
    this.abilitazioneContratti = abilitazioneContratti;
  }

  /**
   * @return Ritorna abilitazioneGare.
   */
  public String getAbilitazioneGare() {
    return abilitazioneGare;
  }

  /**
   * @param abilitazioneGare
   *        abilitazioneGare da settare internamente alla classe.
   */
  public void setAbilitazioneGare(String abilitazioneGare) {
    this.abilitazioneGare = abilitazioneGare;
  }

  /**
   * @return Ritorna abilitazioneStd.
   */
  public String getAbilitazioneLavori() {
    return abilitazioneLavori;
  }

  /**
   * @param abilitazioneStd
   *        abilitazioneStd da settare internamente alla classe.
   */
  public void setAbilitazioneLavori(String abilitazioneStd) {
    this.abilitazioneLavori = abilitazioneStd;
  }

  /**
   * @return Ritorna livelloContratti.
   */
  public Integer getLivelloContratti() {
    return livelloContratti;
  }

  /**
   * @param livelloContratti
   *        livelloContratti da settare internamente alla classe.
   */
  public void setLivelloContratti(Integer livelloContratti) {
    this.livelloContratti = livelloContratti;
  }

  /**
   * @return Ritorna livelloGare.
   */
  public Integer getLivelloGare() {
    return livelloGare;
  }

  /**
   * @param livelloGare
   *        livelloGare da settare internamente alla classe.
   */
  public void setLivelloGare(Integer livelloGare) {
    this.livelloGare = livelloGare;
  }

  /**
   * @return Ritorna livelloStd.
   */
  public Integer getLivelloLavori() {
    return livelloLavori;
  }

  /**
   * @param livelloStd
   *        livelloStd da settare internamente alla classe.
   */
  public void setLivelloLavori(Integer livelloStd) {
    this.livelloLavori = livelloStd;
  }

  /**
   * @return Returns the opzioniUtenteSys.
   */
  public String[] getOpzioniUtenteSys() {
    return opzioniUtenteSys;
  }

  /**
   * @return Returns the opzioniPrivilegi.
   */
  public String[] getOpzioniPrivilegi() {
    return opzioniPrivilegi;
  }

  /**
   * @return Ritorna opzioniUtente.
   */
  public OpzioniUtente getOpzioniUtente() {
    return opzioniUtente;
  }

  /**
   * @param opzioniUtente
   *        opzioniUtente da settare internamente alla classe.
   */
  public void setOpzioniUtente(OpzioniUtente opzioniUtente) {
    this.opzioniUtente = opzioniUtente;
  }

  /**
   * @return Ritorna livelloUtenteDaOpzioni.
   */
  public String getLivelloUtenteDaOpzioni() {
    return livelloUtenteDaOpzioni;
  }

  /**
   * @param livelloUtenteDaOpzioni
   *        livelloUtenteDaOpzioni da settare internamente alla classe.
   */
  public void setLivelloUtenteDaOpzioni(String livelloUtenteDaOpzioni) {
    this.livelloUtenteDaOpzioni = livelloUtenteDaOpzioni;
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
   * @return Returns the ufficioAppartenenza.
   */
  public String getUfficioAppartenenza() {
    return ufficioAppartenenza;
  }

  /**
   * @param ufficioAppartenenza
   *        The ufficioAppartenenza to set.
   */
  public void setUfficioAppartenenza(String ufficioAppartenenza) {
    this.ufficioAppartenenza = ufficioAppartenenza;
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
   * @return Rit. ScadenzaAccount.
   */
  public String getScadenzaAccount() {
    return scadenzaAccount;
  }

  /**
   * @param ScadenzaAccount
   *        ScadenzaAccount da settare internamente alla classe.
   */
  public void setScadenzaAccount(String scadenzaAccount) {
    this.scadenzaAccount = scadenzaAccount;
  }

  /**
   * @return Returns the ruoloUtenteMercatoElettronico.
   */
  public String getRuoloUtenteMercatoElettronico() {
    return ruoloUtenteMercatoElettronico;
  }

  /**
   * @param ufficioAppartenenza
   *        The ufficioAppartenenza to set.
   */
  public void setRuoloUtenteMercatoElettronico(String ruoloUtenteMercatoElettronico) {
    this.ruoloUtenteMercatoElettronico = ruoloUtenteMercatoElettronico;
  }

  /**
   * @return Ritorna categoria.
   */
  public String getCategoria() {
    return categoria;
  }

  /**
   * @param categoria categoria da settare internamente alla classe.
   */
  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }

  /**
   * @return Ritorna listaTextGare.
   */
  public String[] getListaTextGare() {
    return listaTextGare;
  }


  public String getAbilitazioneAP() {
    return abilitazioneAP;
  }


  public void setAbilitazioneAP(String abilitazioneAP) {
    this.abilitazioneAP = abilitazioneAP;
  }

  public String[] getListaTextSelOp() {
    return listaTextSelOp;
  }

  public String[] getListaValueSelOp() {
    return listaValueSelOp;
  }

}
