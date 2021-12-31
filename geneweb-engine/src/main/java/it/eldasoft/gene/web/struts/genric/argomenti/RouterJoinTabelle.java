/*
 * Created on 4-ott-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.argomenti;

import it.eldasoft.gene.db.domain.genric.GiunzioneRicerca;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.giunzione.GiunzioneRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.LegameTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * @author Stefano.Sabbadin
 */
public class RouterJoinTabelle {

  /** Logger Log4J di classe */
  static Logger                      logger                = Logger.getLogger(RouterJoinTabelle.class);

  private static final int           GRUPPO_NON_ATTRIBUITO = -1;

  /** Dizionario delle tabelle */
  private DizionarioTabelle          dizTabelle;

  /** Contenitore della ricerca */
  private ContenitoreDatiRicercaForm ricerca;

  /** Nuova tabella da inserire nella ricerca */
  private TabellaRicercaForm         nuovaTabella;

  /**
   * Elenco di set degli alias delle tabelle adiacenti, utilizzato
   * dall'algoritmo
   */
  private Vector<Set<String>>          elencoGruppiTabelleAdiacenti;

  /**
   * Elenco delle Join da aggiungere alla ricerca in seguito all'inserimento
   * della tabella
   */
  private Vector<GiunzioneRicercaForm> elencoJoinDaAggiungere;

  /**
   * Info temporanea utilizzata per identificare il set di tabelle contigue
   * contenente la tabella inserita durante l'elaborazione dell'algoritmo
   */
  private int                        indiceGruppoAppartenenzaTabella;

  /**
   * Non appena una tabella viene collegata con una qualche join questo flag
   * diventa true
   */
  private boolean                    tabellaCollegataConJoin;

  /**
   * Diventa true se una o più tabelle con la stessa definizione di nuovaTabella
   * sono già state utilizzate in precedenza e le join possibili sono già state
   * esaurite
   */
  private boolean                    joinUtilizzateInPrecedenza;

  /**
   * Indica che è stata trovata la giunzione che lega la nuova tabella ad una di
   * quelle presenti
   */
  private boolean                    trovataPerInserimento;

  /**
   * Costruttore che inizializza lo stato dell'oggetto e calcola eventuali join
   *
   * @param dizTabelle
   *        dizionario delle tabelle
   * @param contenitore
   *        dati della ricerca
   * @param nuovaTabella
   *        nuova tabella da inserire
   */
  public RouterJoinTabelle(DizionarioTabelle dizTabelle,
      ContenitoreDatiRicercaForm contenitore, TabellaRicercaForm nuovaTabella)
      throws RouterJoinTabelleException {
    if (logger.isDebugEnabled()) logger.debug("costruttore: inizio metodo");

    this.dizTabelle = dizTabelle;
    this.ricerca = contenitore;
    this.nuovaTabella = nuovaTabella;
    this.elencoJoinDaAggiungere = new Vector<GiunzioneRicercaForm>();
    this.indiceGruppoAppartenenzaTabella = GRUPPO_NON_ATTRIBUITO;
    this.tabellaCollegataConJoin = false;
    this.joinUtilizzateInPrecedenza = false;
    this.trovataPerInserimento = false;

    this.calcolaJoin();

    if (logger.isDebugEnabled()) logger.debug("costruttore: fine metodo");
  }

  /**
   * Ritorna l'elenco delle join da aggiungere all'elenco, in seguito
   * all'inserimento della tabella
   *
   * @return Ritorna elencoJoinDaAggiungere.
   */
  public GiunzioneRicercaForm[] getElencoJoinDaAggiungere() {
    GiunzioneRicercaForm[] arrayJoin = new GiunzioneRicercaForm[this.elencoJoinDaAggiungere.size()];
    this.elencoJoinDaAggiungere.copyInto(arrayJoin);
    return arrayJoin;
  }

  /**
   * Calcola l'elenco delle join da aggiungere alla ricerca in base alle tabelle
   * esistenti, al loro ordine di inserimento, le join definite e la nuova
   * tabella
   *
   * @throws RouterJoinTabelleException
   *         Eccezione emesse nel qual caso esiste la possibilità di legare la
   *         tabella alle altre, ma ogni legame utilizzabile per relazionarla
   *         alle altre tabelle è già stata utilizzata da un'altra tabella (o
   *         più tabelle) identica inserita in precedenca , con alias quindi
   *         diverso
   */
  private void calcolaJoin() throws RouterJoinTabelleException {
    Tabella defNuovaTabella = dizTabelle.get(this.nuovaTabella.getMnemonicoTabella());
    TabellaRicercaForm tabellaCollegabile = null;
    LegameTabelle[] legami = null;
    int indiceLegame = 0;

    this.elencoGruppiTabelleAdiacenti = this.creaGruppiTabelleAdiacenti();
    if (logger.isDebugEnabled())
      logger.debug("Sono stati determinati un numero di gruppi pari a "
          + this.elencoGruppiTabelleAdiacenti.size());

    // FASE 1: si cercano i legami tra la tabella da inserire e le tabelle
    // esistenti, considerandole in ordine inverso rispetto al loro inserimento
    // nella ricerca; si inserisce il PRIMO legame che si identifica con una
    // tabella, in modo da collegare la tabella ad un gruppo di tabelle tra loro
    // adiacenti e collegate da join
    for (int i = this.ricerca.getNumeroTabelle() - 1; i >= 0
        && !trovataPerInserimento; i--) {

      tabellaCollegabile = this.ricerca.estraiTabella(i);
      if (logger.isDebugEnabled())
        logger.debug("tabellaCollegabile = "
            + tabellaCollegabile.getAliasTabella());

      legami = defNuovaTabella.getLegameTabelle(tabellaCollegabile.getNomeTabella());
      if (logger.isDebugEnabled())
        logger.debug("Legami in arrivo a "
            + tabellaCollegabile.getAliasTabella()
            + ": "
            + legami.length);

      if (legami.length > 0) {
        // se esistono legami, allora provo a vedere se ne sono già stati
        // utilizzati in precedenza
        Vector<GiunzioneRicercaForm> elencoJoinUsate = this.estraiJoinTraTabelleDaMnemonicoEAlias(
            this.ricerca.getElencoGiunzioni(),
            this.nuovaTabella.getMnemonicoTabella(),
            tabellaCollegabile.getAliasTabella());
        if (logger.isDebugEnabled())
          logger.debug("Numero di join simili tra una tabella come "
              + this.nuovaTabella.getAliasTabella()
              + " e la tabella "
              + tabellaCollegabile.getAliasTabella()
              + ": "
              + elencoJoinUsate.size());

        indiceLegame = this.individuaPrimaJoinDisponibile(legami,
            elencoJoinUsate);
      }
    }

    if (logger.isDebugEnabled())
      logger.debug("trovataPerInserimento = " + this.trovataPerInserimento);

    // quando termino il ciclo allora o ho trovato che posso inserire la join,
    // oppure non esistono legami disponibili, o non esistono in genere legami
    // con le altre tabelle
    if (this.trovataPerInserimento) {
      this.inserisciGiunzioneInPartenza(tabellaCollegabile,
          legami[indiceLegame]);
      this.tabellaCollegataConJoin = true;

      if (logger.isDebugEnabled()) {
        logger.debug("Inserita join " + legami[indiceLegame].toString());
        logger.debug("Gruppo in cui è stata inserita la tabella: "
            + this.indiceGruppoAppartenenzaTabella);
      }
    }

    // FASE 2: inserimento di tutte le join che arrivano alla nuova tabella a
    // partire da tabelle appartenenti a gruppi dissociati dalla presente
    // tabella; si cerca di eliminare i prodotti cartesiani
    for (int itTab = this.ricerca.getNumeroTabelle() - 1; itTab >= 0; itTab--) {
      tabellaCollegabile = this.ricerca.estraiTabella(itTab);
      if (logger.isDebugEnabled())
        logger.debug("Provo a vedere se la tabella "
            + tabellaCollegabile.getAliasTabella()
            + " referenzia la nuova tabella");

      // se la tabella aggiunta è ancora isolata dal resto, oppure la tabella
      // presa in considerazione nel ciclo appartiene ad un set di tabelle
      // contigue in cui non è presente la tabella inserita...
      if (this.indiceGruppoAppartenenzaTabella == GRUPPO_NON_ATTRIBUITO
          || !(this.elencoGruppiTabelleAdiacenti.elementAt(
              this.indiceGruppoAppartenenzaTabella)).contains(tabellaCollegabile.getAliasTabella())) {

        // ...cerco le join che partono da tabellaCollegabile e vanno verso
        // nuovaTabella; la prima join che trovo la aggiungo
        legami = dizTabelle.get(tabellaCollegabile.getMnemonicoTabella()).getLegameTabelle(
            this.nuovaTabella.getNomeTabella());

        if (logger.isDebugEnabled())
          logger.debug("Legami in partenza da "
              + tabellaCollegabile.getAliasTabella()
              + ": "
              + legami.length);

        if (legami.length > 0) {

          // se ci sono legami con la tabella, allora si collegano le tabelle
          // solo se:
          // 1) la tabella considerata non è già impegnata in un'altra join in
          // partenza dalla stessa (non può avere 2 padri)
          // 2) la tabella inserita non ha già utilizzato la stessa relazione
          // con un'altra tabella omonima

          // si cerca tra le join definite se la tabella è legata già ad un
          // padre: in tal caso non è possibile collegarla anche alla tabella
          // inserita
          boolean tabSenzaPadre = this.isTabSenzaPadre(tabellaCollegabile);

          if (tabSenzaPadre) {
            // allora si analizzano le join,
            Vector<GiunzioneRicercaForm> join = this.estraiJoinTraTabelleDaMnemonicoEAlias(
                this.elencoJoinDaAggiungere,
                tabellaCollegabile.getMnemonicoTabella(),
                this.nuovaTabella.getAliasTabella());
            if (logger.isDebugEnabled())
              logger.debug("Numero di join simili tra una tabella come "
                  + tabellaCollegabile.getAliasTabella()
                  + " e la tabella "
                  + this.nuovaTabella.getAliasTabella()
                  + ": "
                  + join.size());

            // cerco di trovare la prima join non ancora utilizzata su tale
            // tabella
            indiceLegame = this.individuaPrimaJoinDisponibile(legami, join);

            if (logger.isDebugEnabled())
              logger.debug("trovataPerInserimento = " + trovataPerInserimento);

            if (trovataPerInserimento) {
              if (logger.isDebugEnabled())
                logger.debug("Si inserisce la join numero "
                    + indiceLegame
                    + " di tali join, ovvero "
                    + legami[indiceLegame].toString());
              this.inserisciGiunzioneInArrivo(tabellaCollegabile,
                  legami[indiceLegame]);
              this.tabellaCollegataConJoin = true;
            }
          }
        }
      } else {
        if (logger.isDebugEnabled())
          logger.debug("La tabella da inserire è nello stesso gruppo di "
              + tabellaCollegabile.getAliasTabella());
      }
    }

    // se arrivato al termine la tabella non l'ho collegata a nessun'altra
    // tabella, e teoricamente potevo farlo ma la join era stata usata in
    // precedenza, allora non posso inserire nuovamente questa tabella perchè al
    // momento non ci sono tabelle padri che mi permettono di utilizzarla
    if (!this.tabellaCollegataConJoin && this.joinUtilizzateInPrecedenza)
      throw new RouterJoinTabelleException(
          RouterJoinTabelleException.CODICE_ERRORE_TABELLA_NON_PIU_UTILIZZABILE);
  }

  /**
   * Verifica se la tabella in input non possiede un padre, ovvero una join in
   * cui la tabella è a sinistra.<br>
   * Il padre in una relazione 1:n è la tabella a livello superiore, eccetto nel
   * caso degli archivi in cui il padre è la tabella in cui viene referenziata.
   *
   * @param tabellaCollegabile
   *        tabella da controllare
   * @return true se la tabella in input è senza padre, false altrimenti
   */
  private boolean isTabSenzaPadre(TabellaRicercaForm tabellaCollegabile) {
    boolean tabSenzaPadre = true;
    for (int itGiunzioni = 0; itGiunzioni < this.ricerca.getNumeroGiunzioni(); itGiunzioni++) {
      if (this.ricerca.estraiGiunzione(itGiunzioni).getAliasTabella1().equals(
          tabellaCollegabile.getAliasTabella())) {
        tabSenzaPadre = false;
        break;
      }
    }
    return tabSenzaPadre;
  }

  /**
   * Individua tra i legami in input la join da utilizzare per legare la nuova
   * tabella a quelle esistenti
   *
   * @param legami
   *        elenco dei legami della tabella
   * @param elencoJoinUsate
   *        elenco delle join su cui eseguire le verifiche
   * @return indice tra i legami della join da utilizzare
   */
  private int individuaPrimaJoinDisponibile(LegameTabelle[] legami,
      Vector<GiunzioneRicercaForm> elencoJoinUsate) {
    int indiceLegame = 0;
    this.trovataPerInserimento = false;

    boolean joinUsata = false;
    GiunzioneRicercaForm giunzione = null;
    // cerco di trovare la prima join non ancora utilizzata su tale tabella
    while (indiceLegame < legami.length && !this.trovataPerInserimento) {
      joinUsata = false;
      if (logger.isDebugEnabled())
        logger.debug("Provo a vedere se la join  "
            + legami[indiceLegame].toString()
            + " è già utilizzata in precedenza");

      for (int itJoin = 0; itJoin < elencoJoinUsate.size(); itJoin++) {
        giunzione = elencoJoinUsate.elementAt(itJoin);

        if (logger.isDebugEnabled())
          logger.debug("Considero la join con progressivo "
              + giunzione.getProgressivo());

        if (giunzione.getCampiTabella1().equals(
            UtilityStringhe.serializza(
                legami[indiceLegame].getElencoCampiTabellaOrigine(),
                GiunzioneRicerca.SEPARATORE_CAMPI_JOIN))
            && giunzione.getCampiTabella2().equals(
                UtilityStringhe.serializza(
                    legami[indiceLegame].getElencoCampiTabellaDestinazione(),
                    GiunzioneRicerca.SEPARATORE_CAMPI_JOIN))) {
          // se la join utilizzata è proprio la stessa, allora provo con una
          // successiva
          if (logger.isDebugEnabled())
            logger.debug("La join è già utilizzata, devo tentare con una successiva");

          joinUsata = true;
          this.joinUtilizzateInPrecedenza = true;
          break;
        }
      }

      // se nel confronto con le join l'ho già trovata, passo alla
      // successiva, altrimenti se non l'ho trovata allora la posso inserire
      if (joinUsata)
        indiceLegame++;
      else
        this.trovataPerInserimento = true;
    }

    return indiceLegame;
  }

  /**
   * Inserisce una join che parte dalla tabella inserita a va verso la tabella
   * collegabile in input
   *
   * @param tabellaCollegabile
   *        tabella collegata dalla join alla tabella inserita
   * @param legame
   *        definizione della join tra le due tabelle
   */
  private void inserisciGiunzioneInPartenza(
      TabellaRicercaForm tabellaCollegabile, LegameTabelle legame) {
    Set<String> tmpSet = null;
    GiunzioneRicercaForm giunzionePerInserimento = new GiunzioneRicercaForm();
    giunzionePerInserimento.setGiunzioneAttiva(true);
    giunzionePerInserimento.setMnemonicoTabella1(this.nuovaTabella.getMnemonicoTabella());
    giunzionePerInserimento.setMnemonicoTabella2(tabellaCollegabile.getMnemonicoTabella());
    giunzionePerInserimento.setAliasTabella1(this.nuovaTabella.getAliasTabella());
    giunzionePerInserimento.setAliasTabella2(tabellaCollegabile.getAliasTabella());
    giunzionePerInserimento.setDescrizioneTabella1(this.nuovaTabella.getDescrizioneTabella());
    giunzionePerInserimento.setDescrizioneTabella2(tabellaCollegabile.getDescrizioneTabella());
    giunzionePerInserimento.setCampiTabella1(UtilityStringhe.serializza(
        legame.getElencoCampiTabellaOrigine(),
        GiunzioneRicerca.SEPARATORE_CAMPI_JOIN));
    giunzionePerInserimento.setCampiTabella2(UtilityStringhe.serializza(
        legame.getElencoCampiTabellaDestinazione(),
        GiunzioneRicerca.SEPARATORE_CAMPI_JOIN));
    this.elencoJoinDaAggiungere.addElement(giunzionePerInserimento);

    // inserisco la nuova tabella di partenza dov'è presente la tabella di
    // destinazione
    for (int i = 0; i < this.elencoGruppiTabelleAdiacenti.size(); i++) {
      tmpSet = this.elencoGruppiTabelleAdiacenti.elementAt(i);
      if (tmpSet.contains(tabellaCollegabile.getAliasTabella())) {
        tmpSet.add(this.nuovaTabella.getAliasTabella());
        this.indiceGruppoAppartenenzaTabella = i;
      }
    }
  }

  /**
   * Inserisce il legame tra la tabella inserita e la tabella collegabile,
   * individuato dall'indice in input
   *
   * @param tabellaCollegabile
   *        tabella collegata alla tabella inserita
   * @param legame
   *        legame in arrivo dalla tabella collegata alla inserita da utilizzare
   *        per l'inserimento
   */
  private void inserisciGiunzioneInArrivo(
      TabellaRicercaForm tabellaCollegabile, LegameTabelle legame) {
    Set<String> tmpSet = null;
    GiunzioneRicercaForm giunzionePerInserimento = new GiunzioneRicercaForm();
    giunzionePerInserimento.setGiunzioneAttiva(true);
    giunzionePerInserimento.setMnemonicoTabella1(tabellaCollegabile.getMnemonicoTabella());
    giunzionePerInserimento.setMnemonicoTabella2(this.nuovaTabella.getMnemonicoTabella());
    giunzionePerInserimento.setAliasTabella1(tabellaCollegabile.getAliasTabella());
    giunzionePerInserimento.setAliasTabella2(this.nuovaTabella.getAliasTabella());
    giunzionePerInserimento.setDescrizioneTabella1(tabellaCollegabile.getDescrizioneTabella());
    giunzionePerInserimento.setDescrizioneTabella2(this.nuovaTabella.getDescrizioneTabella());
    giunzionePerInserimento.setCampiTabella1(UtilityStringhe.serializza(
        legame.getElencoCampiTabellaOrigine(),
        GiunzioneRicerca.SEPARATORE_CAMPI_JOIN));
    giunzionePerInserimento.setCampiTabella2(UtilityStringhe.serializza(
        legame.getElencoCampiTabellaDestinazione(),
        GiunzioneRicerca.SEPARATORE_CAMPI_JOIN));
    this.elencoJoinDaAggiungere.addElement(giunzionePerInserimento);

    // cerco il gruppo di appartenenza di tabellaCollegabile
    int indiceGruppoTabCollegabile = this.calcolaIndiceGruppoTabella(tabellaCollegabile.getAliasTabella());

    if (logger.isDebugEnabled())
      logger.debug("la tabella "
          + tabellaCollegabile.getAliasTabella()
          + " appartiene al gruppo "
          + indiceGruppoTabCollegabile);

    // ora si devono fondere i gruppi
    if (indiceGruppoAppartenenzaTabella == GRUPPO_NON_ATTRIBUITO) {
      // inserisco la nuova tabella nel gruppo se non è ancora mai stata
      // inserita in precedenza
      tmpSet = this.elencoGruppiTabelleAdiacenti.elementAt(indiceGruppoTabCollegabile);
      tmpSet.add(this.nuovaTabella.getAliasTabella());
      indiceGruppoAppartenenzaTabella = indiceGruppoTabCollegabile;
    } else {
      // se nuova tabella appartiene ad un gruppo, fondo i due gruppi ed
      // elimino quello con indice maggiore
      if (indiceGruppoTabCollegabile < indiceGruppoAppartenenzaTabella) {
        (this.elencoGruppiTabelleAdiacenti.elementAt(
            indiceGruppoTabCollegabile)).addAll(
                this.elencoGruppiTabelleAdiacenti.elementAt(indiceGruppoAppartenenzaTabella));
        this.elencoGruppiTabelleAdiacenti.removeElementAt(indiceGruppoAppartenenzaTabella);
        indiceGruppoAppartenenzaTabella = indiceGruppoTabCollegabile;
      } else {
        (this.elencoGruppiTabelleAdiacenti.elementAt(
            indiceGruppoAppartenenzaTabella)).addAll(
                this.elencoGruppiTabelleAdiacenti.elementAt(indiceGruppoTabCollegabile));
        this.elencoGruppiTabelleAdiacenti.removeElementAt(indiceGruppoTabCollegabile);
      }
    }
  }

  /**
   * Determina l'indice del gruppo contenente la tabella indicata in input
   *
   * @param aliasTabella
   *        tabella da ricercare
   * @return indice del set contenente la tabella cercata
   */
  private int calcolaIndiceGruppoTabella(String aliasTabella) {
    int indice = GRUPPO_NON_ATTRIBUITO;
    Set<String> tmpSet = null;
    for (int i = 0; i < this.elencoGruppiTabelleAdiacenti.size(); i++) {
      tmpSet = this.elencoGruppiTabelleAdiacenti.elementAt(i);
      if (tmpSet.contains(aliasTabella)) {
        indice = i;
        break;
      }
    }
    return indice;
  }

  /**
   * Dato il mnemonico della tabella di partenza e l'alias della tabella di
   * destinazione, cerca e ritorna le join presenti nel contenitore aventi la
   * tabella di partenza individuata dal mnemonico e la tabella di destinazione
   * che è esattamente la tabella indicata
   *
   * @param contenitore
   *        contenitore in cui eseguire la ricerca
   * @param mnemonicoTabellaPartenza
   *        mnemonico della tabella di partenza
   * @param aliasTabellaDestinazione
   *        alias della tabella di destinazione
   * @return vettore vuoto se non esistono join, altrimenti l'elenco delle join
   *         che partono da altre tabelle identiche a quella di partenza e vanno
   *         alla stessa tabella di destinazione
   */
  private Vector<GiunzioneRicercaForm> estraiJoinTraTabelleDaMnemonicoEAlias(Vector<GiunzioneRicercaForm> contenitore,
      String mnemonicoTabellaPartenza, String aliasTabellaDestinazione) {
    Vector<GiunzioneRicercaForm> elencoJoin = new Vector<GiunzioneRicercaForm>();
    GiunzioneRicercaForm giunzione = null;
    for (int itJoin = 0; itJoin < contenitore.size(); itJoin++) {
      giunzione = contenitore.elementAt(itJoin);

      // ...cercando quelle che da una tabella identica a quella in
      // inserimento vanno proprio verso la tabella collegabile
      if (giunzione.getMnemonicoTabella1().equals(mnemonicoTabellaPartenza)
          && giunzione.getAliasTabella2().equals(aliasTabellaDestinazione)) {
        elencoJoin.addElement(giunzione);
      }
    }

    return elencoJoin;
  }

  /**
   * Crea l'elenco di gruppi di tabelle unite tra loro da join a partire dal
   * contenitore della ricerca
   *
   * @return elenco di set, uno per ogni gruppo di tabelle adiacenti; l'ideale e
   *         corretto è possedere un unico set con tutte le tabelle; in tal caso
   *         non si effettuano prodotti cartesiani (a meno di join disattivate)
   */
  private Vector<Set<String>> creaGruppiTabelleAdiacenti() {
    Vector<Set<String>> elencoGruppiTabelleAdiacenti = new Vector<Set<String>>();
    GiunzioneRicercaForm giunzione = null;
    TabellaRicercaForm tabella = null;
    Set<String> tmpSetTabelleGruppo = null;
    Set<String> tmpSetTabelle = null;
    boolean trovatoGruppoPerInserimento = false;
    boolean trovatoGruppoAppartenenza = false;
    int indiceInserimentoGruppo = 0;

    if (this.ricerca.getNumeroTabelle() == 1) {
      // una sola tabella censita in precedenza, quindi nessuna join
      tmpSetTabelleGruppo = new HashSet<String>();
      tmpSetTabelleGruppo.add(this.ricerca.estraiTabella(0).getAliasTabella());
      elencoGruppiTabelleAdiacenti.add(tmpSetTabelleGruppo);

    } else {
      if (this.ricerca.getNumeroGiunzioni() > 0) {
        // almeno una join censita in precedenza, ciclo per creare i vari gruppi

        // inserisco le tabelle della prima join nel primo gruppo
        giunzione = this.ricerca.estraiGiunzione(0);
        tmpSetTabelleGruppo = new HashSet<String>();
        tmpSetTabelleGruppo.add(giunzione.getAliasTabella1());
        tmpSetTabelleGruppo.add(giunzione.getAliasTabella2());
        elencoGruppiTabelleAdiacenti.add(tmpSetTabelleGruppo);

        // ciclo sugli elementi successivi
        for (int i = 1; i < this.ricerca.getNumeroGiunzioni(); i++) {
          giunzione = this.ricerca.estraiGiunzione(i);

          indiceInserimentoGruppo = 0;
          // determino e aggiorno il gruppo di tabelle con le tabelle oggetto
          // della join
          while (indiceInserimentoGruppo < elencoGruppiTabelleAdiacenti.size()
              & !trovatoGruppoPerInserimento) {

            tmpSetTabelleGruppo = elencoGruppiTabelleAdiacenti.elementAt(indiceInserimentoGruppo);
            if (tmpSetTabelleGruppo.contains(giunzione.getAliasTabella1())
                || tmpSetTabelleGruppo.contains(giunzione.getAliasTabella2())) {
              tmpSetTabelleGruppo.add(giunzione.getAliasTabella1());
              tmpSetTabelleGruppo.add(giunzione.getAliasTabella2());
              trovatoGruppoPerInserimento = true;
            }

            if (!trovatoGruppoPerInserimento) indiceInserimentoGruppo++;
          }

          // se non è stato trovato il gruppo in cui inserire, allora si
          // inserisce
          // in coda
          if (!trovatoGruppoPerInserimento) {
            tmpSetTabelleGruppo = new HashSet<String>();
            tmpSetTabelleGruppo.add(giunzione.getAliasTabella1());
            tmpSetTabelleGruppo.add(giunzione.getAliasTabella2());
            elencoGruppiTabelleAdiacenti.add(tmpSetTabelleGruppo);
          } else {
            // altrimenti è stato inserito in un gruppo esistente, e quindi
            // occorre procedere con la eventuale fusione con i gruppi
            // successivi
            int iterPerFusione = indiceInserimentoGruppo + 1;
            boolean trovatoPerFusione = false;
            while (iterPerFusione < elencoGruppiTabelleAdiacenti.size()
                && !trovatoPerFusione) {
              // verifico se una delle due tabelle oggetto della join inserita è
              // usata nel gruppo oggetto dell'analisi
              tmpSetTabelle = elencoGruppiTabelleAdiacenti.elementAt(iterPerFusione);
              if (tmpSetTabelle.contains(giunzione.getAliasTabella1())
                  || tmpSetTabelle.contains(giunzione.getAliasTabella2())) {
                // in caso sia presente, allora il gruppo appena individuato va
                // eliminato e le tabelle vanno trasferite nel gruppo
                // individuato
                // in precedenza
                trovatoPerFusione = true;

                // popolamento del set del gruppo originario
                tmpSetTabelleGruppo.addAll(tmpSetTabelle);
                // eliminazione del gruppo e del set originario
                elencoGruppiTabelleAdiacenti.removeElementAt(iterPerFusione);
              }

              if (!trovatoPerFusione) iterPerFusione++;
            }
          }
        }
      }

      // ciclo per aggiungere le eventuali tabelle isolate in gruppi distinti e
      // diversi da quelli determinati in precedenza
      for (int i = 0; i < this.ricerca.getNumeroTabelle(); i++) {
        tabella = this.ricerca.estraiTabella(i);
        trovatoGruppoAppartenenza = false;
        indiceInserimentoGruppo = 0;
        while (indiceInserimentoGruppo < elencoGruppiTabelleAdiacenti.size()
            & !trovatoGruppoAppartenenza) {
          tmpSetTabelle = elencoGruppiTabelleAdiacenti.elementAt(indiceInserimentoGruppo);
          if (tmpSetTabelle.contains(tabella.getAliasTabella()))
            trovatoGruppoAppartenenza = true;
          else
            indiceInserimentoGruppo++;
        }
        if (!trovatoGruppoAppartenenza) {
          tmpSetTabelleGruppo = new HashSet<String>();
          tmpSetTabelleGruppo.add(tabella.getAliasTabella());
          elencoGruppiTabelleAdiacenti.add(tmpSetTabelleGruppo);
        }

      }
    }

    return elencoGruppiTabelleAdiacenti;
  }

}
