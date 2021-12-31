/*
 * Created on 26/set/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.invcom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Bean che identifica i dati di una comunicazione.
 *
 * @author Stefano.Sabbadin
 * @since 2.14.0
 */
public class InvioComunicazione implements Serializable {

  /**
   * UID.
   */
  private static final long               serialVersionUID                                   = -7362665967286383473L;

  public static final String              STATO_COMUNICAZIONE_BOZZA                          = "1";
  public static final String              STATO_COMUNICAZIONE_IN_USCITA                      = "2";
  public static final String              STATO_COMUNICAZIONE_INVIATA                        = "3";
  public static final String              STATO_COMUNICAZIONE_INVIATA_CON_MESSAGGI           = "4";
  public static final String              STATO_COMUNICAZIONE_EVENTO_DA_PROCESSARE           = "5";
  public static final String              STATO_COMUNICAZIONE_EVENTO_PROCESSATO              = "6";
  public static final String              STATO_COMUNICAZIONE_EVENTO_PROCESSATO_CON_MESSAGGI = "7";

  public static final int                 COMUNICAZIONE_PUBBLICA                             = 1;
  public static final int                 COMUNICAZIONE_RISERVATA                            = 2;

  /** Chiave. */
  private PKInvioComunicazione            pk;
  /** Nome entit&agrave;. */
  private String                          entita;
  /** Valore del primo campo chiave entit&agrave; collegata. */
  private String                          chiave1;
  /** Eventuale secondo campo chiave entit&agrave; collegata. */
  private String                          chiave2;
  /** Eventuale terzo campo chiave entit&agrave; collegata. */
  private String                          chiave3;
  /** Eventuale quarto campo chiave entit&agrave; collegata. */
  private String                          chiave4;
  /** Eventuale quinto campo chiave entit&agrave; collegata. */
  private String                          chiave5;
  /** Codice operatore che inserisce la comunicazione. */
  private Long                            codiceOperatore;
  /** Data di inserimento della comunicazione. */
  private Date                            dataInserimento;
  /** Nome del mittente. */
  private String                          nomeMittente;
  /** Stato della comunicazione. */
  private String                          stato;
  /** Abilita la gestione dell'intestazione variabile. */
  private String                          abilitaIntestazioneVariabile;
  /** Oggetto della comunicazione. */
  private String                          oggetto;
  /** Testo della comunicazione. */
  private String                          testo;
  /** Testo in formato HTML? */
  private String                          testoHtml;
  /** Id del modello per la creazione dell'allegato variabile. */
  private Long                            idModello;
  /** Tipologia della richiesta (usata ad esempio nel portale appalti). */
  private String                          tipoRichiesta;
  /** Comunicazione pubblica? oppure e' riservata. */
  private Integer                         comunicazionePubblica;
  /** Data di pubblicazione della comunicazione. */
  private Date                            dataPubblicazione;
  /** Data di ultimo aggiornamento stato comunicazione. */
  private Date                            dataAggiornamentoStato;
  /** Numero protocollo (utilizzato nelle integrazioni a sistemi di protocollazione). */
  private String                          numeroProtocollo;
  /** Data protocollo (utilizzato nelle integrazioni a sistemi di protocollazione). */
  private Date                            dataProtocollo;
  /** ID configurazione */
  private String                          idcfg;

  /** Elenco dei destinatari della comunicazione. */
  private List<DestinatarioComunicazione> destinatariComunicazione;

  public InvioComunicazione() {
    this.pk = new PKInvioComunicazione();
    this.destinatariComunicazione = new ArrayList<DestinatarioComunicazione>();
  }

  /**
   * @return Ritorna pk.
   */
  public PKInvioComunicazione getPk() {
    return pk;
  }

  /**
   * @param pk
   *        pk da settare internamente alla classe.
   */
  public void setPk(PKInvioComunicazione pk) {
    this.pk = pk;
  }

  /**
   * @return Ritorna entita.
   */
  public String getEntita() {
    return entita;
  }

  /**
   * @param entita
   *        entita da settare internamente alla classe.
   */
  public void setEntita(String entita) {
    this.entita = entita;
  }

  /**
   * @return Ritorna chiave1.
   */
  public String getChiave1() {
    return chiave1;
  }

  /**
   * @param chiave1
   *        chiave1 da settare internamente alla classe.
   */
  public void setChiave1(String chiave1) {
    this.chiave1 = chiave1;
  }

  /**
   * @return Ritorna chiave2.
   */
  public String getChiave2() {
    return chiave2;
  }

  /**
   * @param chiave2
   *        chiave2 da settare internamente alla classe.
   */
  public void setChiave2(String chiave2) {
    this.chiave2 = chiave2;
  }

  /**
   * @return Ritorna chiave3.
   */
  public String getChiave3() {
    return chiave3;
  }

  /**
   * @param chiave3
   *        chiave3 da settare internamente alla classe.
   */
  public void setChiave3(String chiave3) {
    this.chiave3 = chiave3;
  }

  /**
   * @return Ritorna chiave4.
   */
  public String getChiave4() {
    return chiave4;
  }

  /**
   * @param chiave4
   *        chiave4 da settare internamente alla classe.
   */
  public void setChiave4(String chiave4) {
    this.chiave4 = chiave4;
  }

  /**
   * @return Ritorna chiave5.
   */
  public String getChiave5() {
    return chiave5;
  }

  /**
   * @param chiave5
   *        chiave5 da settare internamente alla classe.
   */
  public void setChiave5(String chiave5) {
    this.chiave5 = chiave5;
  }

  /**
   * @return Ritorna codiceOperatore.
   */
  public Long getCodiceOperatore() {
    return codiceOperatore;
  }

  /**
   * @param codiceOperatore
   *        codiceOperatore da settare internamente alla classe.
   */
  public void setCodiceOperatore(Long codiceOperatore) {
    this.codiceOperatore = codiceOperatore;
  }

  /**
   * @return Ritorna dataInserimento.
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
   * @return Ritorna nomeMittente.
   */
  public String getNomeMittente() {
    return nomeMittente;
  }

  /**
   * @param nomeMittente
   *        nomeMittente da settare internamente alla classe.
   */
  public void setNomeMittente(String nomeMittente) {
    this.nomeMittente = nomeMittente;
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
   * @return Ritorna abilitaIntestazioneVariabile.
   */
  public String getAbilitaIntestazioneVariabile() {
    return abilitaIntestazioneVariabile;
  }

  /**
   * @param abilitaIntestazioneVariabile
   *        abilitaIntestazioneVariabile da settare internamente alla classe.
   */
  public void setAbilitaIntestazioneVariabile(String abilitaIntestazioneVariabile) {
    this.abilitaIntestazioneVariabile = abilitaIntestazioneVariabile;
  }

  /**
   * @return Ritorna oggetto.
   */
  public String getOggetto() {
    return oggetto;
  }

  /**
   * @param oggetto
   *        oggetto da settare internamente alla classe.
   */
  public void setOggetto(String oggetto) {
    this.oggetto = oggetto;
  }

  /**
   * @return Ritorna testo.
   */
  public String getTesto() {
    return testo;
  }

  /**
   * @param testo
   *        testo da settare internamente alla classe.
   */
  public void setTesto(String testo) {
    this.testo = testo;
  }

  /**
   * @return Ritorna testoHtml.
   */
  public String getTestoHtml() {
    return testoHtml;
  }

  /**
   * @param testoHtml
   *        testoHtml da settare internamente alla classe.
   */
  public void setTestoHtml(String testoHtml) {
    this.testoHtml = testoHtml;
  }

  /**
   * @return Ritorna idModello.
   */
  public Long getIdModello() {
    return idModello;
  }

  /**
   * @param idModello
   *        idModello da settare internamente alla classe.
   */
  public void setIdModello(Long idModello) {
    this.idModello = idModello;
  }

  /**
   * @return Ritorna tipoRichiesta.
   */
  public String getTipoRichiesta() {
    return tipoRichiesta;
  }

  /**
   * @param tipoRichiesta
   *        tipoRichiesta da settare internamente alla classe.
   */
  public void setTipoRichiesta(String tipoRichiesta) {
    this.tipoRichiesta = tipoRichiesta;
  }

  /**
   * @return Ritorna comunicazionePubblica.
   */
  public Integer getComunicazionePubblica() {
    return comunicazionePubblica;
  }

  /**
   * @param comunicazionePubblica
   *        comunicazionePubblica da settare internamente alla classe.
   */
  public void setComunicazionePubblica(Integer comunicazionePubblica) {
    this.comunicazionePubblica = comunicazionePubblica;
  }

  /**
   * @return Ritorna dataPubblicazione.
   */
  public Date getDataPubblicazione() {
    return dataPubblicazione;
  }

  /**
   * @param dataPubblicazione
   *        dataPubblicazione da settare internamente alla classe.
   */
  public void setDataPubblicazione(Date dataPubblicazione) {
    this.dataPubblicazione = dataPubblicazione;
  }

  /**
   * @return Ritorna dataAggiornamentoStato.
   */
  public Date getDataAggiornamentoStato() {
    return dataAggiornamentoStato;
  }

  /**
   * @param dataAggiornamentoStato
   *        dataAggiornamentoStato da settare internamente alla classe.
   */
  public void setDataAggiornamentoStato(Date dataAggiornamentoStato) {
    this.dataAggiornamentoStato = dataAggiornamentoStato;
  }

  /**
   * @return Ritorna numeroProtocollo.
   */
  public String getNumeroProtocollo() {
    return numeroProtocollo;
  }

  /**
   * @param numeroProtocollo
   *        numeroProtocollo da settare internamente alla classe.
   */
  public void setNumeroProtocollo(String numeroProtocollo) {
    this.numeroProtocollo = numeroProtocollo;
  }

  /**
   * @return Ritorna dataProtocollo.
   */
  public Date getDataProtocollo() {
    return dataProtocollo;
  }

  /**
   * @param dataProtocollo
   *        dataProtocollo da settare internamente alla classe.
   */
  public void setDataProtocollo(Date dataProtocollo) {
    this.dataProtocollo = dataProtocollo;
  }

  /**
   * @return Ritorna destinatariComunicazione.
   */
  public List<DestinatarioComunicazione> getDestinatariComunicazione() {
    return destinatariComunicazione;
  }

  /**
   * @param destinatariComunicazione
   *        destinatariComunicazione da settare internamente alla classe.
   */
  public void setDestinatariComunicazione(List<DestinatarioComunicazione> destinatariComunicazione) {
    this.destinatariComunicazione = destinatariComunicazione;
  }

  /**
   * @return Ritorna ID configurazione.
   */
  public String getIdcfg() {
    return idcfg;
  }

  /**
   * @param ID configurazione
   *        ID configurazione da settare internamente alla classe.
   */
  public void setIdcfg(String idcfg) {
    this.idcfg = idcfg;
  }


}
