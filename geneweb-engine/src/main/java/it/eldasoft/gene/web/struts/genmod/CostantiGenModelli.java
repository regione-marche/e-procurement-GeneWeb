/*
 * Created on 03-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;

/**
 * Costanti in uso nelle pagine di Generatore Modelli.
 * 
 * @author Luca Giacomazzo
 */
public class CostantiGenModelli {

  /**
   * Nome con cui viene inserito in sessione l'oggetto contenente i parametri
   * per la ricerca nella pagina Trova Ricerche.
   */
  public static final String TROVA_MODELLI_SESSION               = "trovaRicFormSession";

  /**
   * Chiave con cui viene inserito in SESSIONE l'oggetto contenente i parametri
   * per la ricerca nella pagina Trova Ricerche per l'esportazione della
   * definizione di un report.
   */
  public static final String TROVA_MODELLI_EXPORT                = CostantiGenerali.PREFISSO_OGGETTO_TROVA
                                                                     + "ModelliExport";
  /**
   * Nome con cui viene inserito nel request l'oggetto contenente i parametri
   * per la ricerca nella pagina Trova Ricerche.
   */
  public static final String FORM_TROVA_MODELLI                  = "trovaRicercheForm";

  /** Nome dell'istanza dell'oggetto GestioneTab in request */
  public static final String NOME_GESTORE_TAB                    = "gestoreModelliTab";

  /** Voci del menu a tab delle pagine di dettaglio ricerche/crea ricerca */
  public static final String TAB_DETTAGLIO                       = "Dettaglio";

  public static final String TAB_GRUPPI                          = "Gruppi";

  public static final String TAB_PARAMETRI                       = "Parametri";

  /**
   * Chiave dell'istanza dell'oggetto ContenitoreDatiRicercaSession presente in
   * sessione
   */
  public static final String OGGETTO_DETTAGLIO                   = CostantiGenerali.PREFISSO_OGGETTO_DETTAGLIO
                                                                     + "Modello";

  /**
   * Etichetta necessaria per filtrare i tipi di parametro da presentare nella
   * comboBox Tipo delle pagine di add e edit Parametro.
   */
  public static final String TABELLATO_TIPO_PARAMETRO_MODELLO    = "G_x02";

  /** Risposte che vengono date dal ModelliAction * */
  public static final String FORWARD_OK_DETTAGLIO_MODELLO        = "successDettaglio";

  public static final String FORWARD_OK_MODIFICA_MODELLO         = "successModifica";

  public static final String FORWARD_OK_ELIMINA_MODELLO          = "successElimina";

  public static final String FORWARD_OK_UPDATE_MODELLO           = "successUpdate";

  public static final String FORWARD_OK_LISTA_GRUPPI             = "successGruppiLista";

  public static final String FORWARD_OK_LISTA_PARAMETRI          = "successParametriLista";

  public static final String FORWARD_OK_EDIT_PARAMETRO_MODELLO   = "successEditParametroModello";

  public static final String FORWARD_OK_DELETE_PARAMETRO         = "successDeleteParametroModello";

  public static final String FORWARD_OK_SPOSTA_PARAMETRO         = "successSpostaParametroModello";

  public static final String FORWARD_OK_NUOVA_RICERCA            = "successNuovaRicerca";

  /**
   * Forward utilizzata sia per il success che per l'errore che riporta alla
   * trovaModelli
   */
  public static final String FORWARD_MODELLO_ELIMINATO           = "forwardTrovaModello";
  /**
   * Forward utilizzata sia per il success che per l'errore che riporta alla
   * trovaModelliExport
   */
  public static final String FORWARD_MODELLO_EXPORT_ELIMINATO    = "forwardTrovaModelloExport";

  public static final String FORWARD_OK_MODIFICA_LISTA_GRUPPI    = "successModificaListaGruppiModello";

  public static final String FORWARD_OK_UPDATE_LISTA_GRUPPI      = "successUpdateListaGruppiModello";

  public static final String FORWARD_ERRORE_INSERIMENTO_MODELLO  = "errorInsertModello";

  public static final String FORWARD_ERRORE_UPDATE_MODELLO       = "errorUpdateModello";
  // vengono utilizzati direttamente i forward alle action di trova anzichè
  // utilizzare 2 forward verso la stesas destinazione
  // public static final String FORWARD_ERRORE_DELETE_MODELLO =
  // "errorDeleteModello";
  //  
  // public static final String FORWARD_ERRORE_DELETE_MODELLO_EXPORT=
  // "errorDeleteModelloExpor";

  public static final String FORWARD_ERRORE_DOWNLOAD_DETTAGLIO   = "errorDownloadDaDettaglio";

  public static final String FORWARD_ERRORE_DOWNLOAD_MODIFICA    = "errorDownloadDaModifica";

  /** nome metodi utilizzati per le azioni sui modelli * */
  public static final String METODO_DETTAGLIO_MODELLO            = "dettaglioModello";

  public static final String METODO_MODIFICA_MODELLO             = "modificaModello";

  public static final String METODO_ELIMINA_MODELLO              = "eliminaModello";

  public static final String METODO_CREA_MODELLO                 = "creaModello";

  public static final String METODO_INSERISCI_MODELLO            = "insertModello";

  public static final String METODO_AGGIORNA_MODELLO             = "updateModello";

  public static final String METODO_LISTA_GRUPPI_MODELLO         = "listaGruppiModello";

  public static final String METODO_LISTA_GRUPPI_MODIFICA        = "modificaGruppiModello";

  public static final String METODO_LISTA_PARAMETRI              = "listaParametriModello";

  public static final String METODO_CREA_PARAMETRO               = "creaParametroModello";

  public static final String METODO_INSERT_PARAMETRO             = "insertParametro";

  public static final String METODO_UPDATE_PARAMETRO             = "updateParametro";

  public static final String METODO_DELETE_PARAMETRO             = "deleteParametro";

  /** Proprietà in cui è scritto il path dei modelli */
  public static final String PROP_PATH_MODELLI                   = "it.eldasoft.generatoreModelli.pathModelli";

  /** Path temporanea dei modelli */
  public static final String PROP_PATH_MODELLI_TMP               = "it.eldasoft.generatoreModelli.pathTemporanea.relativa";

  /** Path di output dei modelli composti */
  public static final String PROP_PATH_MODELLI_OUTPUT            = "it.eldasoft.generatoreModelli.pathModelliComposti.relativa";

  /** Path di output dei modelli composti */
  public static final String PROP_URL_WEB_SERVICE                = "it.eldasoft.generatoreModelli.ws.url";

  /** Nome oggetto presente nel request e contenente l'id del modello */
  public static final String ATTRIBUTO_REQUEST_ID_MODELLO        = "idModello";

  /** Nome oggetto presente nel request e contenente il modello */
  public static final String ATTRIBUTO_REQUEST_DATI_MODELLO      = "modelliForm";

  /** Nome oggetto presente nel request e contenente un parametro del modello */
  public static final String ATTRIBUTO_REQUEST_PARAMETRO_MODELLO = "parametroModelloForm";

  public static final String ATTRIBUTO_LISTA_TIPI_PARAMETRI      = "listaValoriTabellati";
  
  public static final String ATTRIBUTO_LISTA_PARAMETRI_TABELLATI  = "listaParametriTabellati";
  
  /**
   * Attributo valorizzato nel solo caso di inserimento di un parametro per un
   * report con modello basato su una ricerca
   * 
   * @since 1.5.0
   */
  public static final String ATTRIBUTO_NO_TABELLATI                  = "noParametriTabellati";
  

  /**
   * Nome oggetto presente nel request e contenente l'entità, la chiave e i dati
   * di base per eseguire la composizione di un modello
   */
  public static final String ATTRIBUTO_REQUEST_DATI_COMPOSIZIONE = "componiModelloForm";

  // Messaggi per l'operazione di associazione ad una entita' di un modello
  // composto
  public static final String MSG_ASSOCIA_MODELLO_OK              = "info.modelli.associaModello.operazioneOK";

  public static final String MSG_COPIA_MODELLO_KO                = "errors.modelli.associaModello.operazioneKO";

  /**
   * Nome del parametro da inserire nel caso di modello riepilogativo, per
   * indicare il numero totale di chiavi passate
   */
  public static final String PARAMETRO_RIEPILOGATIVO_NUM_CHIAVI      = "NUMCHIAVI";

  /**
   * Nome del parametro da inserire nel caso di modello riepilogativo, per
   * indicare ogni singola chiave passata
   */
  public static final String PARAMETRO_RIEPILOGATIVO_PREFISSO_CHIAVE = "CHIAVE";

  /**
   * Proprieta' in cui sono scritti lo schema e l'entita' di default su cui
   * creare i modelli e' scritto nel formato  schema di default con cui 
   */
  public static final String PROP_DEFAULT_SCHEMA                 = "it.eldasoft.generatoreModelli.defaultSchema";

  /**
   * Proprieta' in cui e' scritta l'entita' di default con cui creare i modelli
   */
  public static final String PROP_DEFAULT_ENTITA                 = "it.eldasoft.generatoreModelli.defaultEntita";

  /**
   * Versione del modello in fase di export e di import
   */
  public static final Integer VERSIONE_MODELLO                       = new Integer(2);
}