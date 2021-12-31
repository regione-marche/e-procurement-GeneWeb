/*
 * Created on 7-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.domain;



/**
 * Classe di costanti generali per l'applicazione.
 *
 * @author Stefano.Sabbadin
 */
public class CostantiGenerali {

  /**
   * particella da preporre al nome della action per ottenere il nome del metodo che restituisce le opzioni di abilitazione all'esecuzione
   * della action
   */
  public static final String   PARTICELLA_OPZIONI                                 = "getOpzioni";

  /**
   * Individua l'oggetto da verificare in sessione per comprendere se la sessione stessa è ancora valida oppure no
   */
  public static final String   SENTINELLA_SESSION_TIMEOUT                         = "sentinellaSessione";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare che il caricamento è avvenuto in modo corretto
   */
  public static final String   SENTINELLA_APPLICAZIONE_CARICATA                   = "appLoaded";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare il titolo dell'applicazione
   */
  public static final String   ATTR_TITOLO                                        = "appTitle";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare il path dei css
   */
  public static final String   ATTR_PATH_CSS                                      = "pathCss";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare il path delle immagini specifiche per linea di
   * prodotto/progetto
   */
  public static final String   ATTR_PATH_IMG                                      = "pathImg";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito request per indicare un messaggio indicante un esito positivo di una operazione
   */
  public static final String   ATTR_MESSAGGIO_ESITO_OPERAZIONE                    = "msgEsito";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare l'abilitazione o la disabilitazione del pulsante di
   * back del browser
   */
  public static final String   ATTR_BACK_ABILITATO                                = "backAbilitato";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare l'abilitazione o la disabilitazione dei gruppi nelle
   * associative
   */
  public static final String   ATTR_GRUPPI_DISABILITATI                           = "gruppiDisabilitati";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare l'abilitazione o la disabilitazione degli uffici
   * intestatari nelle associative con gli utenti
   */
  public static final String   ATTR_UFFINT_ABILITATI                              = "uffintAbilitati";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare l'abilitazione o la disabilitazione dei contesti
   * associati ad un utente
   */
  public static final String   ATTR_CONTESTI_ABILITATI                            = "contestiAbilitati";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare l'avvio dell'applicazione in configurazione chiusa
   */
  public static final String   ATTR_CONFIGURAZIONE_CHIUSA                         = "configurazioneChiusa";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare la gestione o meno di password null
   */
  public static final String   ATTR_PWD_NULLABLE                                  = "passwordNullable";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare l'abilitazione del download in locale di un documento
   * associato. Se non presente, si permette l'apertura del file presente su server.
   */
  public static final String   ATTR_DOWNLOAD_DOCUMENTI_ASSOCIATI                  = "downloadDocumentiAssociati";

  /**
   * Nome dell'attrbiuto da inserire nell'oggetto implicito application per indicare l'abilitazione per ricerche case sensitive o meno nelle
   * pagine di "Trova" (trova ricerche, modelli, schedulazioni, lavori, pratiche, gare, ec..)
   */
  public static final String   ATTR_ATTIVA_CASE_SENSITIVE                         = "attivaCaseSensitive";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare se nella pagina di ricerca è abilitata la
   * visualizzazione avanzata (visualizzazione della seconda colonna con gli operatori)
   */
  public static final String   ATTR_ATTIVA_VISUALIZZAZIONE_AVANZATA               = "attivaVisualizzazioneAvanzata";

  /**
   * Chiave della property contenente l'elenco delle opzioni acquistate dal
   * cliente e utilizzabili nell'applicazione
   */
  public static final String PROP_OPZIONI_DISPONIBILI                           = "it.eldasoft.opzioni";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare l'elenco delle opzioni che sono state acquistate dal
   * cliente
   */
  public static final String   OPZIONI_DISPONIBILI                                = "opzDisponibili";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare la visualizzazione della form di registrazione
   */
  public static final String   ATTR_ATTIVA_FORM_REGISTRAZIONE                     = "attivaFormRegistrazione";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per
   * indicare la visualizzazione della form di registrazione
   */
  public static final String   ATTR_ATTIVA_FORM_RECUPERA_PASSWORD                 = "attivaFormRecuperaPassword";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare la tipologia della form di assistenza (0=non attiva).
   */
  public static final String   ATTR_ATTIVA_FORM_ASSISTENZA                        = "attivaFormAssistenza";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare la visualizzazione del link per l'accesso anonimo
   */
  public static final String   ATTR_ATTIVA_ACCESSO_ANONIMO                        = "attivaAccessoAnonimo";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare l'etichetta del link per l'accesso anonimo
   */
  public static final String   ATTR_ETICHETTA_LINK_ACCESSO_ANONIMO                = "etichettaAccessoAnonimo";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare se e' abilitata la tracciatura di log eventi
   */
  public static final String   ATTR_ATTIVA_LOG_EVENTI                             = "attivaLogEventi";

  /**
   * Nome dell'attributo da inserire in sessione e contenente le informazioni dell'utente ed il suo profilo
   */
  public static final String   PROFILO_UTENTE_SESSIONE                            = "profiloUtente";

  /** Nome del resource bundle della parte generale dell'applicazione */
  public static final String   RESOURCE_BUNDLE_APPL_GENERALE                      = "AliceResources";

  /** Resource bundle della libreria open source displaytag */
  public static final String   RESOURCE_BUNDLE_DISPLAYTAG                         = "displaytag";

  /**
   * Property di configurazione che individua il codice attribuito all'applicazione, superiore a qualsiasi codice applicazione. Mentre il
   * codice applicazione ha un significato legato ai prodotti EldaSoft SpA, questo codice serve semplicemente a distinguere le web
   * application tra di loro se si interfacciano con un sistema che deve comprendere qual è l'applicazione che invia la richiesta
   */
  public static final String   PROP_ID_APPLICAZIONE                               = "it.eldasoft.idApplicazioneMaster";

  /**
   * Property di configurazione che individua le opzioni utente di base alla creazione di un account
   */

  public static final String   PROP_OPZIONI_UTENTE_DEFAULT                        = "it.eldasoft.account.opzioniDefault";

  /**
   * Property di configurazione che individua i codici univoci attribuiti all'applicazione
   */
  public static final String   PROP_CODICE_APPLICAZIONE                           = "it.eldasoft.codApp";

  /**
   * Prefisso della property che indica le opzioni utente nel codice applicazione. Va concatenato "." + &lt;codapp&gt;
   */
  public static final String   PROP_OPZIONI_UTENTE_GESTITE                        = "it.eldasoft.account.opzioniGestite";

  /**
   * Individua l'oggetto da verificare in sessione per capire se l'utente può utilizzare un unico profilo
   */
  public static final String   SENTINELLA_UNICO_CODICE_PROFILO                    = "sentinellaCodProfiloUnico";
  
  /**
   * Individua l'oggetto da verificare in sessione per capire se l'utente può utilizzare un unico profilo
   */
  public static final String   SENTINELLA_ACCESSO_AMMINISTRATORE                    = "sentinellaAccessoAmministratore";
  
  /**
   * Individua l'oggetto da verificare in sessione per capire se l'utente può utilizzare un unico ufficio intestatario
   */
  public static final String   SENTINELLA_SELEZIONA_UFFICIO_INTESTATARIO          = "sentinellaSelezionaUffint";

  /**
   * Property di configurazione che individua il codice univoco attribuito all'applicazione
   */
  public static final String   PROP_DATABASE                                      = "it.eldasoft.dbms";

  /**
   * Property di configurazione che individua il codice la tipologia di algoritmo da utilizzare per la cifratura/decifratura delle password
   * nel DB
   */
  public static final String   PROP_TIPOLOGIA_CIFRATURA_DATI                      = "it.eldasoft.cifraturaPassword.algoritmo";

  /**
   * Chiave della property contenente 1 se si intende disabilitare la visualizzazione dei gruppi nelle maschere in cui compaiono come
   * associazione, 0 altrimenti
   */
  public static final String   PROP_GRUPPI_DISABILITATI                           = "it.eldasoft.associazioneGruppiDisabilitata";

  /**
   * Chiave della property contenente 1 se si intende abilitare la visualizzazione delle associazione uffici intestatari con l'utente, 0
   * altrimenti
   */
  public static final String   PROP_UFFINT_ABILITATI                              = "it.eldasoft.associazioneUffintAbilitata";

  /**
   * Chiave della property contenente l'elenco delle entita che vanno filtrate per ufficio intestatario
   */
  public static final String   PROP_UFFINT_ARCHIVIFILTRATI                        = "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati";

  /**
   * Chiave della property contenente l'elenco delle entita che vanno filtrate per ufficio intestatario
   */
  public static final String   PROP_ENTITA_BLOCCATE_ELIMINAZIONE                  = "it.eldasoft.bloccoEliminazioneEntita.elencoEntita";

  /**
   * Chiave della property contenente 1 se si intende abilitare l'utilizzo dei contesti all'utente, 0 altrimenti
   */
  public static final String   PROP_CONTESTI_ABILITATI                            = "it.eldasoft.utilizzoContestiAbilitato";

  /**
   * Property di configurazione che individua la durata massima stabilita per un utente senza fare accessi.
   */
  public static final String   PROP_DURATA_ACCOUNT                               = "account.durata";

  /**
   * Property di configurazione che individua il numero massimo di tentativi di autenticazione oltre il quale viene bloccato l'accesso
   * provvisoriamente.
   */
  public static final String   PROP_NUM_MAX_TENTATIVI_LOGIN                      = "account.loginKO.maxNumTentativi";

  /**
   * Property di configurazione che individua il ritardo, espresso in secondi, introdotto in caso di tentativo di autenticazione per un
   * utente bloccato provvisoriamente per superamento del limite di tentativi di autenticazione fallita.
   */
  public static final String   PROP_NUM_SECONDI_DELAY_LOGIN_BLOCCATA             = "account.loginKO.delaySecondi";

  /**
   * Property di configurazione che individua la durata del blocco provvisorio, espresso in minuti, per un utente avente superato il limite
   * di tentativi di autenticazione fallita.
   */
  public static final String   PROP_DURATA_MINUTI_BLOCCO_LOGIN                   = "account.loginKO.durataBloccoMinuti";

  /**
   * Property di configurazione che individua la durata stabilita per le password degli utenti
   */
  public static final String   PROP_DURATA_PASSWORD                               = "it.eldasoft.account.durataPassword";

  /**
   * Property di configurazione che individua l'intervallo minimo che un utente con flag password sicura (ou39) deve far trascorrere tra un cambio di password ed un altro
   */
  public static final String   PROP_INTERVALLO_CAMBIO_PASSWORD                    = "account.intervalloMinimoCambioPassword";

  /**
   * Chiave per attivare il link all'accesso anonimo
   */
  public static final String   PROP_ATTIVA_ACCESSO_ANONIMO                        = "it.eldasoft.accessoAnonimo";

  /**
   * Property di configurazione che individua l'account (login=password)
   */
  public static final String   PROP_ACCOUNT_ACCESSO_ANONIMO                       = "it.eldasoft.accessoAnonimo.account";

  /**
   * Property di configurazione che individua l'account per consentire l'accesso da applicativo esterno
   */
  public static final String   PROP_ACCOUNT_ACCESSO_APPLICATIVO_ESTERNO           = "it.eldasoft.accessoApplEsterno.account";

  public static final String   PROP_RICHIESTA_ASSISTENZA_MODO_NON_ATTIVO          = "0";
  public static final String   PROP_RICHIESTA_ASSISTENZA_MODO_WS                  = "1";
  public static final String   PROP_RICHIESTA_ASSISTENZA_MODO_MAIL                = "2";

  /**
   * Chiave della property contenente 1 se esiste associazione 1 a 1 tra uffici intestatari e utenti,
   * 0 se ad un ufficio intestatario possono essere associati più utenti
   */
  public static final String   PROP_UFFINT_USRSYS  								= "it.eldasoft.associazioneUffintUsrsys";

  /**
   * Opzione di default da utilizzare per le funzionalità standard dell'applicazione, ovvero quelle sempre presenti indipendentemente dalle
   * opzioni acquistate dal cliente
   */
  public static final String   OPZIONE_DEFAULT                                    = "STANDARD";

  /**
   * Abilitazione di default da attribuire ad ogni utente per utilizzare le funzionalità standard dell'applicazione, ovvero quelle sempre
   * presenti indipendentemente dalle opzioni acquistate dal cliente
   */
  public static final String   ABILITAZIONE_DEFAULT                               = "STANDARD";

  /**
   * Opzione amministrazione utenti/gruppi
   */
  public static final String   OPZIONE_ADMIN_UTENTI                               = "OP101";

  /**
   * Opzione generatore ricerche
   */
  public static final String   OPZIONE_GEN_RICERCHE                               = "OP2";

  /**
   * Opzione generatore ricerche professional
   */
  public static final String   OPZIONE_GEN_RICERCHE_PROFESSIONAL                  = "OP98";

  /**
   * Opzione amministrazione aerver LDAP
   */
  public static final String   OPZIONE_ADMIN_LDAP                                 = "OP100";

  /**
   * Opzione generatore modelli
   */
  public static final String   OPZIONE_GEN_MODELLI                                = "OP1";

  /**
   * Opzione gestione generatore attributi
   */
  public static final String   OPZIONE_GESTIONE_GENERATORE_ATTRIBUTI              = "OP97";

  /**
   * Opzione portale pubblico di frontend
   */
  public static final String   OPZIONE_GESTIONE_PORTALE                           = "OP114";

  /**
   * Opzione gestione pubblicazione di report mediante web service
   */
  public static final String   OPZIONE_GESTIONE_PUBBL_REPORT_WS                   = "OP119";

  /**
   * Abilitazione per l'amministrazione utenti/gruppi
   */
  public static final String   ABILITAZIONE_ADMIN_UTENTI                          = "ADMACCGRP";

  /**
   * Abilitazione per l'amministrazione delle ricerche
   */
  public static final String   ABILITAZIONE_GEN_RICERCHE                          = "GENRIC";

  /**
   * Abilitazione per l'amministrazione dei modelli
   */
  public static final String   ABILITAZIONE_GEN_MODELLI                           = "GENMOD";

  /**
   * Abilitazione per l'amministrazione delle maschere
   */
  public static final String   ABILITAZIONE_GEN_MASCHERE                          = "GENMASCH";

  /**
   * Separatore utilizzato nel file di properties cifrato per indicare le opzioni licenziate al cliente
   */
  public static final String   SEPARATORE_OPZIONI_LICENZIATE                      = "|";

  /**
   * Separatore utilizzato nei file di properties, non cifrati e quello cifrato, per indicare elenchi di valori attribuiti ad una chiave
   */
  public static final String   SEPARATORE_PROPERTIES_MULTIVALORE                  = ";";

  /**
   * Separatore utilizzato nel nome delle properties per comporre il nome della properties stessa in funzione del codice applicazione
   */
  public static final String   SEPARATORE_PROPERTIES                              = ".";

  /**
   * Individua l'oggetto da inserire in sessione ogni volta che si accede alle pagine relative al dettaglio di un qualche oggetto (es:
   * dettaglio gruppo, dettaglio ricerca, dettaglio modello). Contiene un valore identificativo dell'oggetto di cui si vuole vedere/si sta
   * analizzando il dettaglio.
   */
  public static String         ID_OGGETTO_SESSION                                 = "idOggetto";

  /**
   * Individua l'oggetto da inserire in sessione ogni volta che si accede alle pagine relative al dettaglio di un qualche oggetto (es:
   * dettaglio gruppo, dettaglio ricerca, dettaglio modello). Contiene il nome dell'oggetto di cui si vuole vedere il/si sta analizzando il
   * dettaglio.
   */
  public static String         NOME_OGGETTO_SESSION                               = "nomeOggetto";

  /**
   * Prefisso della chiave usata per inserire in sessione o per accedere ai parametri dell'ultima ricerca effettuata (trovaRicerche,
   * trovaModelli, ecc...)
   */
  public static String         PREFISSO_OGGETTO_TROVA                             = "trova";

  /**
   * Prefisso della chiave usata per inserire in sessione o per accedere ai parametri del record di dettaglio (es.: dettaglio ricerca,
   * dettaglio modello, ecc...)
   */
  public static String         PREFISSO_OGGETTO_DETTAGLIO                         = "recordDett";

  /**
   * Chiave dell'oggetto sentinella che indica un eventuale oggetto modificato rispetto al DB e le cui modifiche sono presenti in sessione.
   * Viene utilizzato nelle ricerche
   */
  public static final String   SENTINELLA_OGGETTO_MODIFICATO                      = CostantiGenerali.PREFISSO_OGGETTO_DETTAGLIO
                                                                                      + "Modificato";
  /**
   * Voci con cui popolare la combobox Risultati per pagina nelle pagine 'Trova report', 'Trova modelli', 'Trova report', 'Trova
   * schedulazioni', 'Trova coda schedulazioni'
   */
  public static final String[] CBX_RIS_PER_PAGINA                                 = new String[] {"5", "10", "20", "50", "100" };

  /**
   * Voci con cui popolare la combobox 'Risultati per pagina' nella pagina 'Dati generali' di un report
   */
  public static final String[] CBX_RIS_PER_PAGINA_REPORT                          = new String[] {"Tutti", "5", "10", "20", "50", "100" };

  /**
   * Disabilitazione delle navigazione delle pagine. Viene usato nelle pagine di editing per obbligare l'utente a salvare i dati in edit o
   * annullare l'operazione
   */
  public static final String   DISABILITA_NAVIGAZIONE                             = "1";

  /**
   * Chiave con cui viene inserita nel request il parametro per la disabilitazione della navigazione delle pagine
   */
  public static final String   NAVIGAZIONE_DISABILITATA                           = "isNavigazioneDisabilitata";

  /** Chiave con cui viene settato in sessione il modulo attivo */
  public static final String   MODULO_ATTIVO                                      = "moduloAttivo";

  /** Chiave con il profilo attivo salvato in sessione */
  public static final String   PROFILO_ATTIVO                                     = "profiloAttivo";

  /** Chiave con il nome del profilo attivo salvato in sessione */
  public static final String   NOME_PROFILO_ATTIVO                                = "nomeProfiloAttivo";

  /** Chiave con la descrizione del profilo attivo salvato in sessione */
  public static final String   DESC_PROFILO_ATTIVO                                = "descProfiloAttivo";

  /**
   * Chiave con il filtro del profilo attivo se il codice profilo contiene in testa $<valore>$
   */
  public static final String   FILTRO_PROFILO_ATTIVO                              = "filtroProfiloAttivo";

  /** Chiave con cui viene settata in sessione la versione del modulo attivo */
  public static final String   VERSIONE_MODULO_ATTIVO                             = "versioneModuloAttivo";

  /**
   * Nome della chiave in sessione con il codice dell'ufficio intestatario attivo
   */
  public static final String   UFFICIO_INTESTATARIO_ATTIVO                        = "uffint";

  /**
   * Nome della chiave in sessione con il nome dell'ufficio intestatario attivo
   */
  public static final String   NOME_UFFICIO_INTESTATARIO_ATTIVO                   = "nomeUffint";

  /** Chiave con cui viene settato in sessione il set delle chiavi record di dettaglio navigate, differenziato per profilo. */
  public static final String   PROFILI_KEYS                                       = "keys";

  /** Chiave con cui viene settato in sessione il set delle chiavi padre dei record di dettaglio navigati, differenziato per profilo. */
  public static final String   PROFILI_KEY_PARENTS                                = "keyParents";

  /**
   * Chiave con cui viene settato nel context il path + nomefile del manuale utente
   */
  public static final String   MANUALE                                            = "manualeUtente";

  /**
   * Massimo numero di tentativi di inserimento record possibili per chiave duplicata
   */
  public static final int      NUMERO_MAX_TENTATIVI_INSERT                        = 10;

  /**
   * Property che individua il nome dello schema contenente le viste delle tabelle per le ricerche base
   */
  public static final String   PROP_SCHEMA_VISTE_REPORT_BASE                      = "it.eldasoft.generatoreRicerche.base.schemaViste";

  /**
   * Property che individua il codice cliente
   */
  public static final String   PROP_CODICE_CLIENTE                                = "it.eldasoft.codiceCliente";

  /**
   * Chiave con cui viene inserita la denominazione dell'acquirente del software
   */
  public static final String   PROP_ACQUIRENTE                                    = "it.eldasoft.acquirenteSW";

  /**
   * Chiave con cui viene inserita la denominazione del responsabile per il cliente
   */
  public static final String   PROP_RESPONSABILE_CLIENTE                          = "it.eldasoft.responsabileCliente";

  /**
   * Chiave con cui viene inserito il riferimento email del responsabile per il cliente.
   */
  public static final String   PROP_EMAIL_RESPONSABILE_CLIENTE                    = "it.eldasoft.responsabileClienteEmail";

  /**
   * Chiave con cui viene inserita la data di attivazione del software.
   */
  public static final String   PROP_DATA_ATTIVAZIONE                              = "it.eldasoft.dataAttivazione";

  /**
   * Property che individua il codice prodotto
   */
  public static final String   PROP_CODICE_PRODOTTO                               = "it.eldasoft.codiceProdotto";

  /**
   * File di licenza
   */
  public static final String   NOME_FILE_LICENZA                                  = "licenza.txt";

  /**
   * Property che individua la chiave di accesso
   */
  public static final String   PROP_CHIAVE_DI_ACCESSO                             = "it.eldasoft.chiaveAccesso";

  /**
   * Property che individua la chiave di attivazione
   */
  public static final String   PROP_CHIAVE_DI_ATTIVAZIONE                         = "it.eldasoft.chiaveAttivazione";

  /**
   * Chiave della property contenente un numero intero che individua quanti utenti al massimo possono utilizzare contemporaneamente
   * l'applicativo
   */
  public static final String   PROP_NUMERO_MAX_UTENTI_CONNESSI                    = "it.eldasoft.multiUtenza.numMax";

  /**
   * Nome dell'attributo da inserire nell'oggetto implicito application per indicare che l'applicazione deve essere registrata con il codice
   * di sblocco
   */
  public static final String   SENTINELLA_BLOCCO_ATTIVAZIONE                      = "bloccoAttivazione";

  /**
   * Nome della chiave di accesso con cui viene inserito nel context la chiave di accesso calcolata dalla classe PlugInBase in fase di avvio
   * della web app
   */
  public static final String   CHIAVE_DI_ACCESSO                                  = "chiaveAccesso";

  /**
   * Percorso interno alla cartella di sistema nascosta WEB-INF
   */
  public static final String   PATH_WEBINF                                        = "/WEB-INF/";

  /**
   * Percorso di default in cui recuperare i file di properties
   */
  public static final String   DEFAULT_PATH_CARTELLA_PROPERTIES                   = PATH_WEBINF + "classes/";
  /**
   * Nome di default del file di properties criptato con la chiave standard presente fin dall'installazione dell'applicazione
   */
  public static final String   DEFAULT_NOME_FILE_PROPERTIES                       = "genep_noreg.properties";

  /**
   * Nome di default del file di properties criptato generato con l'attivazione dell'applicazione
   */
  public static final String   DEFAULT_NOME_FILE_PROPERTIES_ATTIVAZIONE           = "genep.properties";

  /**
   * Nome di default del file di properties in chiaro per l'avvio
   * dell'applicativo (sostituisce la gestione con i file genep)
   */
  public static final String   DEFAULT_NOME_FILE_PROPERTIES_PLAINTEXT             = "gene.properties";

  /**
   * Codice applicazione generale web
   */
  public static final String   CODICE_APPLICAZIONE_LIBRERIA_GENERALE_WEB          = "W_";

  /** Properties per lo skip sull'uso dei profili */
  public static final String   SKIP_USO_PROFILI                                   = "it.eldasoft.profili.skipUsoProfili";

  /**
   * Chiave con cui viene settato in sessione se l'entita e' modificabile dall'utente: "1" -> entita' modificabile, "0" entita' non
   * modificabile. L'attributo viene settata a livello di scheda, dalla classe FormSchedaTag, in modalita' visualizzazione dell'entita'
   * stessa
   */
  public static final String   ENTITA_PRINCIPALE_MODIFICABILE                     = "entitaPrincipaleModificabile";

  public static final String   PROP_TITOLO_APPLICATIVO                            = "it.eldasoft.titolo";

  /* Properties di configurazione dell'e-mail */

  public static final String   PROP_MAIL_IMPLEMENTAZIONE_API                      = "it.eldasoft.mail.api.implementation";

  public static final String   PROP_INDIRIZZO_MAIL_AMMINISTRATORE                 = "it.eldasoft.registrazione.mailAmministratore";

  public static final String   PROP_CONFIGURAZIONE_MAIL_STANDARD                  = "STD";
  
  public static final String   PROP_CONFIGURAZIONE_MAIL_STANDARD_CONV             = "CONVERSAZIONI";

  /**
   * Properties per l'invio comunicazioni mediante fax
   */
  public static final String   PROP_FAX_ABILITATO                                 = "it.eldasoft.invioFax";

  public static final String   PROP_FAX_INDIRIZZO_MAIL                            = "it.eldasoft.invioFax.indirizzoMail";

  public static final String   PROP_FAX_OGGETTO_MAIL                              = "it.eldasoft.invioFax.oggettoMail";

  public static final String   PROP_FAX_PATH_CERTIFICATO                          = "it.eldasoft.invioFax.pathCertificatoAllegato";

  public static final String   PROP_FAX_ALLEGATO_OBBLIGATORIO                     = "it.eldasoft.invioFax.allegatoObbligatorio";

  public static final String   PROP_FAX_NUMERO_MAX_ALLEGATI                       = "it.eldasoft.invioFax.numeroMaxAllegati";

  public static final String   PROP_FAX_FORMATO_ALLEGATI                          = "it.eldasoft.invioFax.formatoAllegati";

  /**
   * Chiave con cui viene settato nel context della property con il nome della jsp di registrazione
   */
  public static final String   REGISTRAZIONE_NOME_PAGINA                          = "paginaRegistrazione";

  public static final String   PROP_REGISTRAZIONE_AUTOMATICA                      = "it.eldasoft.registrazione.automatica";

  public static final String   RESOURCE_OGGETTO_MAIL_REGISTRAZIONE_AMMINISTRATORE = "registrazione.mail.amministratore.oggetto";

  public static final String   RESOURCE_TESTO_MAIL_REGISTRAZIONE_AMMINISTRATORE   = "registrazione.mail.amministratore.testo";

  public static final String   RESOURCE_OGGETTO_MAIL_REGISTRAZIONE_AUTOMATICA     = "registrazione.mail.automatica.oggetto";

  public static final String   RESOURCE_TESTO_MAIL_REGISTRAZIONE_AUTOMATICA       = "registrazione.mail.automatica.testo";

  public static final String   RESOURCE_OGGETTO_MAIL_ABILITAZIONE_UTENTE          = "registrazione.mail.attiva.oggetto";

  public static final String   RESOURCE_TESTO_MAIL_ABILITAZIONE_UTENTE            = "registrazione.mail.attiva.testo";

  public static final String   RESOURCE_OGGETTO_MAIL_DISABILITAZIONE_UTENTE       = "registrazione.mail.disattiva.oggetto";

  public static final String   RESOURCE_TESTO_MAIL_DISABILITAZIONE_UTENTE         = "registrazione.mail.disattiva.testo";

  public static final String   PROP_PROFILO_DEFAULT_REGISTRAZIONE                 = "it.eldasoft.registrazione.profiloUtenteDefault";

  public static final String   PROP_PROFILO_DEFAULT_INSERIMENTO                   = "it.eldasoft.account.insert.profiloDefault";

  /** property per indicare il tipo protocollo per la SSO Attiva */
  public static final String   PROP_SSO_PROTOCOLLO                                = "sso.protocollo";

  /** property per indicare la URL di default per l'accesso SSO */
  public static final String   PROP_SSO_LOGIN_URL                                 = "sso.login.url";

  /** property per indicare la URL di default per la disconnessione SSO */
  public static final String   PROP_SSO_LOGOUT_URL                                = "sso.logout.url";

  /** property per decriptare il token per la autenticazione SSO Cohesion */
  public static final String   PROP_SSO_COHESION_ENCRYPTION_KEY                   = "sso.cohesion.encryption.key";

  /** property per indicare il nome del campo contenente la login per l'autenticazione SSO */
  public static final String   PROP_SSO_ATTRIBUTE_LOGIN                           = "sso.attribute.login";

  /** property per indicare il nome del campo contenente il nome per l'autenticazione SSO */
  public static final String   PROP_SSO_ATTRIBUTE_FIRST_NAME                      = "sso.attribute.firstName";

  /** property per indicare il nome del campo contenente il cognome per l'autenticazione SSO */
  public static final String   PROP_SSO_ATTRIBUTE_LAST_NAME                       = "sso.attribute.lastName";

  /** property per indicare il nome del campo contenente la descrizione (nome+cognome) per l'autenticazione SSO */
  public static final String   PROP_SSO_ATTRIBUTE_DESCRIPTION                     = "sso.attribute.description";

  /** property per indicare il nome del campo contenente l'email per l'autenticazione SSO */
  public static final String   PROP_SSO_ATTRIBUTE_EMAIL                           = "sso.attribute.mail";

  /** property per indicare il nome del campo contenente il codice fiscale per l'autenticazione SSO */
  public static final String   PROP_SSO_ATTRIBUTE_FISCAL_CODE                     = "sso.attribute.fiscalCode";

  /** property SPID */
  public static final String   PROP_SSO_SPID_WS_AUTHSERVICESPID_URL               = "sso.spid.ws.auth.url";

  public static final String   PROP_SSO_SPID_SERVICEPROVIDER                      = "sso.spid.service.provider";

  public static final String   PROP_SSO_SPID_AUTHLEVEL                            = "sso.spid.authlevel";

  public static final String   PROP_SSO_SPID_AUTHLEVEL_URL                        = "sso.spid.authlevel.url";






  /**
   * Chiave per attivare il link alla registrazione degli utenti
   */
  public static final String PROP_ATTIVA_FORM_REGISTRAZIONE      = "it.eldasoft.registrazione.attivaForm";

  /**
   * Chiave contenente il nome della jsp di registrazione
   */
  public static final String PROP_REGISTRAZIONE_NOME_PAGINA      = "it.eldasoft.registrazione.pagina";


  /** property per indicare in registrazione il ruolo applicativo */
  public static final String   PROP_REG_RUOLO                                    = "registrazione.ruolo";

  /** property per indicare in registrazione gli applicativi disponibili */
  public static final String   PROP_REG_PROFILI_DISPONIBILI                   = "registrazione.profiliDisponibili";

  /** property per indicare la in registrazione la password di default  */
  public static final String   PROP_REG_DEFAULT_PASSWORD                          = "registrazione.sso.defaultPassword";

  /** property per indicare in registrazione il path/nome del modello fac-simile */
  public static final String   PROP_REG_FACSIMILE                                 = "registrazione.facsimile";

  /** property che forza la valorizzazione del campo login con il codice fiscale del soggetto.
   *  Valori ammessi: 0 [default] = login libera, 1 = login codice fiscale */
  public static final String   PROP_REG_LOGINCF                                 = "registrazione.loginCF";

  /** property per indicare in registrazione l'integrazione con anagrafiche(TECNI,UTENTE) */
  public static final String   PROP_REG_INTEGRAZIONE_ANAGRAFICA                   = "registrazione.integrazioneAnagrafica";

  /** property per indicare eventuali integrazioni inserite nell'applicativo */
  public static final String   PROP_INTEGRAZIONE                                  = "it.eldasoft.integrazione";

  /**
   * codice di errore del messaggio del resource bundle per il caso di errore in fase di download di un documento
   */
  public static final String   RESOURCE_ERRORE_DOWNLOAD                           = "errors.download";

  /**
   * Individua l'oggetto TempFileDeleter da inserire in sessione per memorizzare nell'oggetto i nomi di file da cancellare dalla directory
   * temporanea dell'application server alla momento dell'invalidazione/time-out della sessione dell'utente.
   */
  public static final String   TEMP_FILES_NAME_SESSION                            = "TEMP_FILES_NAME_SESSION";

  /*
   * Properties per la gestione della connessione ad altro applicativo
   */
  public static final String   PROP_ACCESSO_ALTRO_APPLICATIVO_NUMERO              = "it.eldasoft.accessoAltroApplicativo.numeroApplicativi";

  public static final String   PROP_ACCESSO_ALTRO_APPLICATIVO_DESCRIZIONE         = "it.eldasoft.accessoAltroApplicativo.descrizioneApplicativo";

  public static final String   PROP_ACCESSO_ALTRO_APPLICATIVO_INDIRIZZO           = "it.eldasoft.accessoAltroApplicativo.indirizzoApplicativo";

  public static final String   PROP_ACCESSO_ALTRO_APPLICATIVO_CODICE              = "it.eldasoft.accessoAltroApplicativo.codiceApplicativo";

  public static final String   SENTINELLA_ACCESSO_ALTRO_APPLICATIVO               = "sentinellaAccessoAltroApplicativo";

  /*
   * Property per la gestione della conversione a PDF di documenti
   */
  public static final String   ATTR_ATTIVA_CONVERSIONE_PDF                        = "attivaConversionePdf";

  /*
   * Properties per la gestione della lettura dei feed RSS
   */
  public static final String   PROP_FEED_RSS_NUMERO                               = "it.eldasoft.feedrss.numero";

  public static final String   PROP_FEED_RSS_URL                                  = "it.eldasoft.feedrss.url";

  public static final String   PROP_FEED_RSS_NUMERO_MASSIMO_ELEMENTI              = "it.eldasoft.feedrss.numeromassimoelementi";

  /**
   * Property per la url del web service del portale ALICE
   */
  public static final String   PROP_URL_WEB_SERVICE_PORTALE_ALICE                 = "it.eldasoft.portaleAlice.ws.url";

  /**
   * Mappa temporanea di parcheggio delle sessioni riattivate in seguito al reload della webapp o a riavvio regolare. La mappa viene
   * svuotata allo startup dell'applicativo.
   */
  public static final String   ID_MAP_TEMPORANEA_SESSIONI_RIATTIVATE              = "reactivatedSessions";

  /**
   * Property per la gestione della tracciatura su W_LOGEVENTI
   */
  public static final String  PROP_LOG_EVENTI                                     = "eventi.log";

  /**
   * Attributo per la gestione dell'accesso agli applicativi tramite Cohesion
   */
  public static final String   ATTR_ATTIVA_COHESION                               = "cohesionAttivo";

  /**
   * Property contenente la lista di campi ENTITA.CAMPO separati da ";" relativi a campi visibili ma mai editabili in modifica.
   */
  public static final String   PROP_CAMPI_BLOCCATI_SOLA_LETTURA                   = "elencoCampi.solaLettura";
  /**
   * Chiave dell'attributo di pagina con l'elenco dei campi bloccati in modifica indipendentemente dal profilo.
   */
  public static final String   ATTR_CAMPI_BLOCCATI_SOLA_LETTURA                   = "campiBloccatiModifica";

  /* Properties di configurazione della richiesta di assistenza */
  public static final String   PROP_RICHIESTA_ASSISTENZA_PREFISSO                 = "assistenza.";

  public static final String   PROP_RICHIESTA_ASSISTENZA_MODO                     = "assistenza.modo";

  public static final String   PROP_RICHIESTA_ASSISTENZA_OGGETTO                  = "assistenza.oggetto";

  public static final String   PROP_RICHIESTA_ASSISTENZA_MAIL                     = "assistenza.mail";

  public static final String   PROP_RICHIESTA_ASSISTENZA_SERVIZIO_URL             = "assistenza.servizio.url";

  public static final String   PROP_RICHIESTA_ASSISTENZA_SERVIZIO_USR             = "assistenza.servizio.usr";

  public static final String   PROP_RICHIESTA_ASSISTENZA_SERVIZIO_PWD             = "assistenza.servizio.pwd";

  public static final String   PROP_RICHIESTA_ASSISTENZA_ID_PRODOTTO              = "assistenza.servizio.productId";

  public static final String   PROP_RICHIESTA_ASSISTENZA_FILE_SIZE                = "assistenza.filesize";

  public static final String   CFG_RICHIESTA_ASSISTENZA_FORM                      = "cfgRichiestaAssistenzaForm";

  public static final String   DEFAULT_PROP_RICHIESTA_ASSISTENZA_OGGETTO          = "1-Segnalazione di un malfunzionamento;2-Segnala una non conformità normativa;3-Richiesta di informazioni o istruzioni;4-Richiesta assistenza su modelli o report;5-Richiesta offerta per personalizzazioni e nuove funzioni";

  public static final String   DEFAULT_PROP_RICHIESTA_ASSISTENZA_SERVIZIO_URL     = "http://helpdesk.maggioli.it";

  /** Chiave della configurazione che fornisce l'elenco delle estensioni ammesse in fase di upload file. */
  public static final String   PROP_ESTENSIONI_AMMESSE                            = "uploadFile.estensioniAmmesse";
}