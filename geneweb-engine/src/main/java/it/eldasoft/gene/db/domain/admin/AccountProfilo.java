/*
 * Created on 02-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.admin;

/**
 * Bean per l'interfacciamento con i dati presenti nella tabella USRSYS e W_ACCPRO
 *
 * @author Luca.Giacomazzo
 */
public class AccountProfilo extends Account {

  /**   UID   */
  private static final long serialVersionUID = -8495769708527821621L;

  private boolean associato;

  public AccountProfilo(){
    super();
  }

  public AccountProfilo(Account account){
    this.setAbilitazioneContratti(account.getAbilitazioneContratti());
    this.setAbilitazioneGare(account.getAbilitazioneGare());
    this.setAbilitazioneStd(account.getAbilitazioneStd());
    this.setDataInserimento(account.getDataInserimento());
    this.setIdAccount(account.getIdAccount());
    this.setLivelloContratti(account.getLivelloContratti());
    this.setLivelloGare(account.getLivelloGare());
    this.setLivelloStd(account.getLivelloStd());
    this.setLogin(account.getLogin());
    this.setNome(account.getNome());
    this.setOpzioniApplicazione(account.getOpzioniApplicazione());
    this.setOpzioniPrivilegi(account.getOpzioniPrivilegi());
    this.setOpzioniUtente(account.getOpzioniUtente());
    this.setPassword(account.getPassword());
    this.setSiglaProvincia(account.getSiglaProvincia());
    this.setStato(account.getStato());
    this.setRuoloUtenteMercatoElettronico(account.getRuoloUtenteMercatoElettronico());

    this.setAssociato(false);

    this.setUfficioAppartenenza(account.getUfficioAppartenenza());
  }

  /**
   * @return Ritorna associato.
   */
  public boolean getAssociato() {
    return associato;
  }

  /**
   * @param associato associato da settare internamente alla classe.
   */
  public void setAssociato(boolean associato) {
    this.associato = associato;
  }

}