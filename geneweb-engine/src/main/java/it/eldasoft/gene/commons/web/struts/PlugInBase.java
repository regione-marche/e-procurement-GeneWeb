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
package it.eldasoft.gene.commons.web.struts;

import it.eldasoft.gene.bl.PropsConfigManager;
import it.eldasoft.gene.commons.web.LimitatoreConnessioniUtenti;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.domain.PropsConfig;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.CriptazioneStream;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.sql.comp.SqlManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Classe di base da estendere per realizzare plugin Struts per lo startup delle
 * applicazioni. Questa classe effettua il caricamento delle properties da un
 * file di configurazione ed in base al valore di una proprietà con chiave
 * univoca carica gli eventuali altri file di properties in chiaro.<br>
 * Utilizza la libreria <b>Struts</b>:
 * <ul>
 * <li>includere tale libreria in fase di compilazione di questo sorgente</li>
 * <li>nel caso di utilizzo di questa classe all'interno di un progetto
 * qualsiasi si necessita di includere tale libreria anche nel progetto di
 * destinazione, più le eventuali librerie dipendenti specificate nel suo
 * MANIFEST.</li>
 * </ul>
 *
 * @author Stefano.Sabbadin
 */
public abstract class PlugInBase implements PlugIn {

  /** Logger Log4J di classe */
  static Logger               logger                              = Logger.getLogger(PlugInBase.class);

  /**
   * Modalita' di avvio standard dell'applicazione
   */
  public static final short  AVVIO_STANDARD                      = 0;

  /**
   * Chiave che individua nello stream di input un elenco di file da caricare in
   * aggiunta al file individuato dallo stream di input stesso
   */
  private static final String PROP_ELENCO_FILE                    = "it.eldasoft.propFiles";

  /**
   * Chiave della property contenente il path dei fogli di stile dentro la
   * cartella css
   */
  private static final String PROP_PATH_CSS                       = "it.eldasoft.css.path";

  /**
   * Chiave della property contenente il path delle immagini specifiche del
   * progetto/linea di prodotto dentro la cartella img
   */
  private static final String PROP_PATH_IMG                       = "it.eldasoft.img.path";

  /**
   * Chiave della property contenente 1 se si intende abilitare l'uso del
   * pulsante di back del browser, 0 altrimenti
   */
  private static final String PROP_BACK_ABILITATO                 = "it.eldasoft.backAbilitato";

  /**
   * Chiave della property contenente 1 se si intende gestire l'inserimento di
   * password null, false altrimenti
   */
  private static final String PROP_CONSENTI_PASSWORD_NULL         = "it.eldasoft.login.password.consentiNull";

  /**
   * Chiave della property contenente il path ed il nome file relativo al
   * manuale utente
   */
  private static final String PROP_MANUALE_UTENTE                 = "it.eldasoft.manuale";

  /**
   * Chiave per definire la modalità di sviluppo
   */
  private static final String PROP_ELDASOFT_SVILUPPO              = "it.eldasoft.sviluppo";


  private static boolean      sviluppo                            = false;

  /**
   * Chiave che individua l'etichetta da visualizzare per il link per l'accesso
   * anonimo
   */
  private static final String PROP_ETICHETTA_LINK_ACCESSO_ANONIMO = "it.eldasoft.accessoAnonimo.etichettaLink";

  /**
   * Chiave che individua la property da utilizzare per bypassare l'attivazione
   * del software ed utilizzare il solo genep cifrato con la chiave standard
   */
  private static final String PROP_NON_ATTIVARE_APPLICATIVO       = "it.eldasoft.skipAttivazione";

  /**
   * Chiave per attivare il link al recupero della password
   */
  private static final String PROP_ATTIVA_FORM_RECUPERA_PASSWORD      = "it.eldasoft.password.recupera";

  /**
   * @see org.apache.struts.action.PlugIn#destroy()
   */
  public void destroy() {
  }

  /**
   * Avvio dell'applicazione web: si distinguono due tipi di avvio:
   * <ol>
   * <li> <b>primo avvio</b>: nella cartella specificata nel metodo privato
   * getPathCartellaProperties deve esistere il file solo il file
   * genep_noreg.properties. L'applicazione si avvia caricando tale file di
   * properties, cifrato con la chiave standard;</li>
   * <li> <b>avvio standard</b>: l'applicazione si avvia caricando i file di
   * properties genep_noreg.properties e genep.properties dalla cartella
   * specificata nel metodo privato getPathCartellaProperties. Il file genep e'
   * cifrato con il MACADDRESS del server in cui e' installata l'applicazione
   * stessa.</li>
   * </ol>
   *
   * @see org.apache.struts.action.PlugIn#init(org.apache.struts.action.ActionServlet,
   *      org.apache.struts.config.ModuleConfig)
   */
  public void init(ActionServlet servlet, ModuleConfig moduleConfig)
      throws ServletException {
    ServletContext context = servlet.getServletContext();

    ResourceBundle rb = ResourceBundle.getBundle("AliceResources");
    boolean appCorrettamenteConfigurata = false;

    // *************************************
    // Caricamento del file di proprieta' iniziale. Contiene alcune proprieta' (la sezione fissa)
    // presenti originariamente nel file genep criptato.
    InputStream inputStreamPropertiesPlainText = context.getResourceAsStream(this.getPathCartellaProperties() + this.getNomeFilePropertiesPlainText());
    ConfigManager.reload(inputStreamPropertiesPlainText);
    try {
      inputStreamPropertiesPlainText.close();
    } catch (IOException e) {

    }
    appCorrettamenteConfigurata = initApplicazione(servlet, moduleConfig, context, rb, AVVIO_STANDARD);

    // Gestione dell'attivazione dell'applicativo. Gestione le proprieta' originariamente
    // presenti nella sezione variabile del file genep criptato.
    String codiceCliente = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_CLIENTE);
    String acquirenteSW = ConfigManager.getValore(CostantiGenerali.PROP_ACQUIRENTE);


    if (codiceCliente == null || (codiceCliente != null && "".equals(codiceCliente.trim()))
        || acquirenteSW == null || (acquirenteSW != null && "".equals(acquirenteSW.trim()))) {
      context.setAttribute(CostantiGenerali.SENTINELLA_BLOCCO_ATTIVAZIONE, "1");
      appCorrettamenteConfigurata = true;
    }


    // *************************************


    // si inserisce il marcatore nell'application che indica che
    // l'applicazione se è disponibile o meno
    if (ConfigManager.isLoadOk() && appCorrettamenteConfigurata) {
      if (logger.isInfoEnabled()) {
				logger.info(rb.getString("info.appLoaded"));
			}
      context.setAttribute(CostantiGenerali.SENTINELLA_APPLICAZIONE_CARICATA,
          "1");
    } else {
      // logger.fatal(UtilityStringhe.replaceParametriMessageBundle(
      // rb.getString("errors.appNotLoaded.noDecifraFileStartup"),
      // new String[] { this.getNomeFileProperties() }));
      context.setAttribute(CostantiGenerali.SENTINELLA_APPLICAZIONE_CARICATA,
          "0");
    }

    sviluppo = "1".equals(ConfigManager.getValore(PROP_ELDASOFT_SVILUPPO));
  }

  /**
   * Metodo per l'inizializzazione dell'applicazione in funzione della modalita
   * di avvio: - se l'avvio e' standard (cioe' dopo la registrazione della
   * applicazione stessa) vengono caricate tutte le informazioni per un uso
   * completo dell'applicazione stessa; - se e' il primo avvio dell'applicazione
   * allora vengono caricate le informazioni per un uso limitato
   * dell'applicazione per consentire lo sola operazione di
   * registrazione/sblocco dell'applicazione stessa
   *
   * @param servlet
   * @param moduleConfig
   * @param context
   * @param rb
   * @param modalitaAvvio
   * @return Ritorna true se l'applicazione e' stata correttamente configurata,
   *         false altrimenti
   */
  private boolean initApplicazione(ActionServlet servlet,
      ModuleConfig moduleConfig, ServletContext context, ResourceBundle rb,
      short modalitaAvvio) {
    boolean result = false;
    // se è presente la proprietà con l'elenco dei file di properties da
    // caricare, allora si cicla su tale elenco e si caricano i file nel
    // Config Manager
    String elencoFile = ConfigManager.getValore(PROP_ELENCO_FILE);
    if (elencoFile != null) {
      InputStream streamAggiuntivo;

      String[] array = elencoFile.split(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE);
			for (String array1 : array) {
				streamAggiuntivo = context.getResourceAsStream(this.getPathCartellaProperties() + array1);
				if (streamAggiuntivo != null) {
				  ConfigManager.load(streamAggiuntivo);
				  try {
                    streamAggiuntivo.close();
                  } catch (IOException e) {

                  }
				}
			}
      // Carico i metadati solo se l'applicazione viene avviata in modalita'
      // standard
      if (AVVIO_STANDARD == modalitaAvvio) {
				this.initConfigurabile(servlet, moduleConfig);
			}
      // se il caricamento va a buon fine, si estraggono le opzioni acquistate
      // dal cliente e si marca nell'application il fatto che l'applicazione è
      // disponibile
      if (ConfigManager.isLoadOk()) {
        result = loadOpzioniApplicazioneDisponibile(context, rb, modalitaAvvio);
        result = (result && this.restoreSessioniPerLicenze(context, rb));
      }
    } else {
      logger.fatal(UtilityStringhe.replaceParametriMessageBundle(
          rb.getString("errors.appNotLoaded.noDecifraFileStartup"),
          new String[] { this.getNomeFileProperties() }));
    }
    return result;
  }

  /**
   * Ripristina le informazioni per gestire l'accesso ad un numero limitato di utenti all'applicativo, in quanto con le sessioni
   * serializzabili &egrave; possibile ripristinare le medesime ad un reload dell'applicativo mantenendo l'utente loggato e connesso
   * all'applicativo.
   *
   * @param context
   *        contesto della web application
   */
  private boolean restoreSessioniPerLicenze(ServletContext context, ResourceBundle rb) {
    boolean esito = true;
    synchronized (context) {
      @SuppressWarnings("unchecked")
      Map<String, HttpSession> hashSessioni = (Map<String, HttpSession>) context.getAttribute(CostantiGenerali.ID_MAP_TEMPORANEA_SESSIONI_RIATTIVATE);
      if (hashSessioni != null) {
        // se il numero di utenti accettabili risulta inferiore al numero di sessioni da ripristinare, si termina con errori
        if (LimitatoreConnessioniUtenti.getInstance().getNumeroConnessioniDisponibili() > hashSessioni.size()) {
          // se il numero di connessioni risulta inferiore al numero di sessioni, si ripristinano tutte
          for (String key : hashSessioni.keySet()) {
            HttpSession session = hashSessioni.get(key);
            LimitatoreConnessioniUtenti.getInstance().allocaConnessione(session.getId());
            ProfiloUtente profiloUtente = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
            LimitatoreConnessioniUtenti.getInstance().setDatiSessioneUtente(session.getId(), profiloUtente.getIp(),
                profiloUtente.getLogin(),
                UtilityDate.convertiData(profiloUtente.getDataAccesso(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), session);
          }
          context.removeAttribute(CostantiGenerali.ID_MAP_TEMPORANEA_SESSIONI_RIATTIVATE);

        } else {
          esito = false;
          logger.fatal(rb.getString("errors.appNotLoaded.riduzioneConnessioniAccettabili"));
        }
      }
    }
    return esito;
  }

  /**
   * Metodo per il caricamento delle opzioni acquistate dal cliente e marcatura
   * nell'application context che l'applicazione è disponibile
   *
   * @param context
   * @param rb
   * @return Ritorna true se l'applicazione e' configurata correttamente, false
   *         altrimenti
   */
  public static boolean loadOpzioniApplicazioneDisponibile(ServletContext context,
      ResourceBundle rb, short modalitaAvvio) {
    boolean appCorrettamenteConfigurata = true;

    if (AVVIO_STANDARD == modalitaAvvio) {
      // si inserisce nell'application il set di opzioni acquistate
      String elencoOpzioni = ConfigManager.getValore(CostantiGenerali.PROP_OPZIONI_DISPONIBILI);
      if (elencoOpzioni != null) {
        List<String> listaOpzioni = new ArrayList<String>();
        listaOpzioni.add(CostantiGenerali.OPZIONE_DEFAULT);
        String[] array = elencoOpzioni.split("\\"
            + CostantiGenerali.SEPARATORE_OPZIONI_LICENZIATE);
				listaOpzioni.addAll(Arrays.asList(array));
        context.setAttribute(CostantiGenerali.OPZIONI_DISPONIBILI,
            listaOpzioni.toArray(new String[0]));

        // 26/10/2007 L.G.: avvio dell'applicazione in configurazione chiusa
        Collection<String> opzioni = listaOpzioni;

        if (opzioni.contains(CostantiGenerali.OPZIONE_GEN_MODELLI)
            && opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE)) {
					context.setAttribute(CostantiGenerali.ATTR_CONFIGURAZIONE_CHIUSA, "0");
				} else {
					context.setAttribute(CostantiGenerali.ATTR_CONFIGURAZIONE_CHIUSA, "1");
				}
      }

      // 24/10/2006 L.G.: gestione del pulsante back
      context.setAttribute(
          CostantiGenerali.ATTR_BACK_ABILITATO,
          ("1".equals(ConfigManager.getValore(PROP_BACK_ABILITATO)) ? "1" : "0"));

      // 09/11/2006 SS: gestione dei gruppi
      context.setAttribute(
          CostantiGenerali.ATTR_GRUPPI_DISABILITATI,
          ("0".equals(ConfigManager.getValore(CostantiGenerali.PROP_GRUPPI_DISABILITATI))
              ? "0"
              : "1"));

      // 15/10/2009 SS: gestione degli uffici intestatari
      context.setAttribute(
          CostantiGenerali.ATTR_UFFINT_ABILITATI,
          ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_UFFINT_ABILITATI))
              ? "1"
              : "0"));

      // 20/11/2009 CF: gestione dei contesti
      context.setAttribute(
          CostantiGenerali.ATTR_CONTESTI_ABILITATI,
          ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI))
              ? "1"
              : "0"));

      // 06/11/2006 SS: gestione delle password null
      context.setAttribute(CostantiGenerali.ATTR_PWD_NULLABLE,
          ("1".equals(ConfigManager.getValore(PROP_CONSENTI_PASSWORD_NULL))
              ? "1"
              : "0"));

      // 17/02/2010 SC: controllo se nel file di properties
      // è configurata la possibilità di connettersi ad un altro applicativo
      try {
    	Long numeroApplicativi = new Long(ConfigManager.getValore(CostantiGenerali.PROP_ACCESSO_ALTRO_APPLICATIVO_NUMERO));
		if (numeroApplicativi > 0) {
			context.setAttribute(CostantiGenerali.SENTINELLA_ACCESSO_ALTRO_APPLICATIVO,"1");
		} else {
			context.setAttribute(CostantiGenerali.SENTINELLA_ACCESSO_ALTRO_APPLICATIVO,"0");
		}
      } catch (NumberFormatException e) {
    	  context.setAttribute(CostantiGenerali.SENTINELLA_ACCESSO_ALTRO_APPLICATIVO,"0");
      }

      // 30/11/2006 LG: inserimento nel context di path + nome file relativo al
      // manuale utente dell'applicativo
      if (ConfigManager.getValore(PROP_MANUALE_UTENTE) != null)
        context.setAttribute(CostantiGenerali.MANUALE,
            ConfigManager.getValore(PROP_MANUALE_UTENTE));

      // 07/03/2007 SS: inserita sentinella per comprendere se ho un unico
      // codice applicazione
      String codiceApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
      if (codiceApplicazione == null) {
        logger.fatal(rb.getString("errors.appNotLoaded.notFound." + CostantiGenerali.PROP_CODICE_APPLICAZIONE));
        appCorrettamenteConfigurata = false;
      }

      // 21/04/2008 FD: inserimento nel context della property dell'attivazione
      // del form di registrazione e della jsp da usare
      context.setAttribute(CostantiGenerali.ATTR_ATTIVA_FORM_REGISTRAZIONE,
          ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_ATTIVA_FORM_REGISTRAZIONE))
              ? "1"
              : "0"));

      if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_ATTIVA_FORM_REGISTRAZIONE))) {
        String nomePagina = ConfigManager.getValore(CostantiGenerali.PROP_REGISTRAZIONE_NOME_PAGINA);
        if (nomePagina == null || nomePagina.trim().length() == 0) {
          nomePagina = "registrazione-account.jsp?modo=NUOVO";
          logger.warn(rb.getString("warnings.notFound." + CostantiGenerali.PROP_REGISTRAZIONE_NOME_PAGINA));
          //appCorrettamenteConfigurata = false;
		}
        context.setAttribute(CostantiGenerali.REGISTRAZIONE_NOME_PAGINA,
            nomePagina);
      }

	  // Attivazione del form di recupero password dalla maschera di login
      context.setAttribute(CostantiGenerali.ATTR_ATTIVA_FORM_RECUPERA_PASSWORD,
          ("1".equals(ConfigManager.getValore(PROP_ATTIVA_FORM_RECUPERA_PASSWORD))
              ? "1"
              : "0"));

      // si imposta il flag che indica se è previsto il link per l'accesso
      // anonimo
      context.setAttribute(
          CostantiGenerali.ATTR_ATTIVA_ACCESSO_ANONIMO,
          ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_ATTIVA_ACCESSO_ANONIMO))
              ? "1"
              : "0"));

      if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_ATTIVA_ACCESSO_ANONIMO))) {
        String accountAnonimo = ConfigManager.getValore(CostantiGenerali.PROP_ACCOUNT_ACCESSO_ANONIMO);
        if (accountAnonimo == null || accountAnonimo.trim().length() == 0) {
          logger.warn(rb.getString("warnings.notFound." + CostantiGenerali.PROP_ACCOUNT_ACCESSO_ANONIMO));
          //appCorrettamenteConfigurata = false;
        }
        String etichettaLinkAccessoAnonimo = ConfigManager.getValore(PROP_ETICHETTA_LINK_ACCESSO_ANONIMO);
        if (etichettaLinkAccessoAnonimo == null
            || etichettaLinkAccessoAnonimo.trim().length() == 0) {
          logger.warn(rb.getString("warnings.notFound." + PROP_ETICHETTA_LINK_ACCESSO_ANONIMO));
          etichettaLinkAccessoAnonimo = "Accesso anonimo";
          //appCorrettamenteConfigurata = false;
        }
        context.setAttribute(
            CostantiGenerali.ATTR_ETICHETTA_LINK_ACCESSO_ANONIMO,
            etichettaLinkAccessoAnonimo);
      }

      try {
        Long numeroApplicativi = new Long(ConfigManager.getValore(CostantiGenerali.PROP_ACCESSO_ALTRO_APPLICATIVO_NUMERO));
        if (numeroApplicativi.longValue() > 0) {
            context.setAttribute(CostantiGenerali.SENTINELLA_ACCESSO_ALTRO_APPLICATIVO,"1");
        } else {
            context.setAttribute(CostantiGenerali.SENTINELLA_ACCESSO_ALTRO_APPLICATIVO,"0");
        }
      } catch (NumberFormatException e) {
          context.setAttribute(CostantiGenerali.SENTINELLA_ACCESSO_ALTRO_APPLICATIVO,"0");
      }

      // 28/09/2012 Sabbadin: inserimento nel context della property dell'attivazione
      // del form di richiesta assistenza
      // 04/04/2014 Sabbadin: variato il significato del campo in seguito alla proposta di diverse modalita' di invio di richieste; comunque
      // il valore 0 continua ad indicare nessuna assistenza attiva, oppure una modalita' inserita in configurazione non prevista
      ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
      PropsConfigManager propsConfigManager = (PropsConfigManager) ctx.getBean("propsConfigManager");
      PropsConfig  maProp = propsConfigManager.getProperty(codiceApplicazione, CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MODO);
			String modoAssistenza = null;
			if (maProp != null) {
				modoAssistenza = StringUtils.stripToNull(maProp.getValore());
			}
      if (modoAssistenza == null
          || !(CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MODO_WS.equals(modoAssistenza) || CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MODO_MAIL.equals(modoAssistenza))) {
        modoAssistenza = CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MODO_NON_ATTIVO;
      }
      context.setAttribute(CostantiGenerali.ATTR_ATTIVA_FORM_ASSISTENZA, modoAssistenza);

      // 17/10/2013 Giacomazzo: inserimento nel context dell'attributo per attivare o meno
      // l'accesso all'applicativo tramite Cohesion. Se l'URL di accesso a Cohesion e' stato
      // configurato, allora l'applicativo viene avviato con l'attributo cohesionAttivo=1.
      //context.setAttribute(CostantiGenerali.ATTR_ATTIVA_COHESION,
      //(StringUtils.isNotBlank(ConfigManager.getValore(CostantiGenerali.PROP_ATTIVA_COHESION))
      //? "1" : "0"));
      // 23/03/2016 CF, centralizzato il controllo per SSO (1-Shibboleth; 2-Cohesion; 3-SSOBART)
      String propProtSSO = ConfigManager.getValore(CostantiGenerali.PROP_SSO_PROTOCOLLO);
      propProtSSO = UtilityStringhe.convertiNullInStringaVuota(propProtSSO);
      if(!"".equals(propProtSSO) && "2".equals(propProtSSO)){
        context.setAttribute(CostantiGenerali.ATTR_ATTIVA_COHESION, "1");
      }else{
        context.setAttribute(CostantiGenerali.ATTR_ATTIVA_COHESION, "0");
      }
    }

    // si inserisce il titolo dell'applicazione
    String titolo = ConfigManager.getValore(CostantiGenerali.PROP_TITOLO_APPLICATIVO);
    if (titolo == null) {
      titolo = "Prodotto Maggioli S.p.A.";
      logger.warn(rb.getString("warnings.notFound." + CostantiGenerali.PROP_TITOLO_APPLICATIVO));
      //appCorrettamenteConfigurata = false;
    }
    context.setAttribute(CostantiGenerali.ATTR_TITOLO, titolo);

    // si inserisce il path da utilizzare per i fogli di stile
    String pathCss = ConfigManager.getValore(PROP_PATH_CSS);
    if (pathCss == null) {
      pathCss = "std/";
      logger.warn(rb.getString("warnings.notFound." + PROP_PATH_CSS));
      //appCorrettamenteConfigurata = false;
    }
    context.setAttribute(CostantiGenerali.ATTR_PATH_CSS, pathCss);

    // 16/03/2007 SS: si inserisce il path da utilizzare per le immagini
    // specifiche in base alla linea di prodotto/progetto
    String pathImg = ConfigManager.getValore(PROP_PATH_IMG);
    if (pathImg == null) {
      pathImg = "std/";
      logger.warn(rb.getString("warnings.notFound." + PROP_PATH_IMG));
      //appCorrettamenteConfigurata = false;
    }
    context.setAttribute(CostantiGenerali.ATTR_PATH_IMG, pathImg);

    // 07/05/2008 SS: si controlla che la property del DBMS sia valorizzata e
    // sia valida
    String tipoDBMS = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
    if (!SqlManager.isDatabaseValido(tipoDBMS)) {
      logger.fatal(rb.getString("errors.appNotLoaded.notValid." + CostantiGenerali.PROP_DATABASE));
      appCorrettamenteConfigurata = false;
    } else {
      // 20/02/2008 LG: si inserisce nell'application context un attributo per
      // individuare se il DBMS è Microsoft SqlServer, per il quale le ricerche
      // dalle pagine di trova devono essere case insensitive
      if (SqlManager.DATABASE_SQL_SERVER.equalsIgnoreCase(tipoDBMS)) {
				context.setAttribute(CostantiGenerali.ATTR_ATTIVA_CASE_SENSITIVE,
								Boolean.FALSE);
			} else {
				context.setAttribute(CostantiGenerali.ATTR_ATTIVA_CASE_SENSITIVE,
								Boolean.TRUE);
			}
    }

    if (ConfigManager.getValore(CostantiGenerali.PROP_NUMERO_MAX_UTENTI_CONNESSI) == null) {
      logger.fatal(rb.getString("errors.appNotLoaded.notFound." + CostantiGenerali.PROP_NUMERO_MAX_UTENTI_CONNESSI));
      appCorrettamenteConfigurata = false;
    }

    return appCorrettamenteConfigurata;
  }

  /**
   * Metodo che processa lo stream individuato dal path in input e lo ritorna in
   * formato in chiaro
   *
   * @param streamDaInterpretare
   *        stream in input dal quale partire per interpretarne il contenuto
   *        eseguendo una decifratura dei dati
   * @param serialNumber
   *        serial number da utilizzare per la decifratura
   * @return stream di input in chiaro con i dati interpretati, oppure null se
   *         lo stream non è decifrabile
   */
  protected InputStream getStreamInChiaroFileAttivazione(
      InputStream streamDaInterpretare, String serialNumber) {
    InputStream streamInChiaro = null;

    OutputStream streamDecifrato = new ByteArrayOutputStream();
    CriptazioneStream cript = null;
    try {
      cript = new CriptazioneStream(streamDaInterpretare, streamDecifrato,
          CriptazioneStream.FORMATO_STREAM_CIFRATO, serialNumber);
      streamInChiaro = cript.getStreamNonCifrato();
    } catch (CriptazioneException e) {
      // in questa fase, in cui non si risponde ad alcuna richiesta ma
      // semplicemente si va a configurare il server, si utilizza il Locale di
      // default (altrimenti basta utilizzare l'oggetto request)
      ResourceBundle rb = ResourceBundle.getBundle("AliceResources");
      logger.error(rb.getString(e.getChiaveResourceBundle()), e);
    }

    return streamInChiaro;
  }

  /**
   * Metodo che processa lo stream individuato dal path in input e lo ritorna in
   * formato in chiaro, usando la chiave di default
   *
   * @param streamDaInterpretare
   *        stream in input dal quale partire per interpretarne il contenuto
   *        eseguendo una decifratura dei dati
   * @return stream di input in chiaro con i dati interpretati, oppure null se
   *         lo stream non è decifrabile
   */
  private InputStream getStreamInChiaroFileStartup(
      InputStream streamDaInterpretare) {
    InputStream streamInChiaro = null;

    OutputStream streamDecifrato = new ByteArrayOutputStream();
    CriptazioneStream cript;
    try {
      // utilizza la decifratura con la chiave standard
      cript = new CriptazioneStream(streamDaInterpretare, streamDecifrato,
          CriptazioneStream.FORMATO_STREAM_CIFRATO);
      streamInChiaro = cript.getStreamNonCifrato();
    } catch (CriptazioneException e) {
      // in questa fase, in cui non si risponde ad alcuna richiesta ma
      // semplicemente si va a configurare il server, si utilizza il Locale di
      // default (altrimenti basta utilizzare l'oggetto request)
      ResourceBundle rb = ResourceBundle.getBundle("AliceResources");
      logger.error(rb.getString(e.getChiaveResourceBundle()), e);
    }

    return streamInChiaro;
  }

  /**
   * Permette l'inserimento di una parte personalizzabile nella fase di
   * inizializzazione
   *
   * @param servlet
   * @param moduleConfig
   */
  protected abstract void initConfigurabile(ActionServlet servlet,
      ModuleConfig moduleConfig);

  /**
   * @return ritorna la cartella nella quale sono memorizzati i file di
   *         properties da caricare (es: "/WEB-INF/classes/")
   */
  public abstract String getPathCartellaProperties();

  /**
   * @return ritorna il nome del file di properties cifrato, presente nella
   *         cartella individuata con {@link #getPathCartellaProperties()} (es:
   *         genep.properties), generato in seguito all'attivazione
   *         dell'applicativo
   */
  public abstract String getNomeFilePropertiesAttivazione();

  /**
   * @return ritorna il nome del file di properties cifrato, presente nella
   *         cartella individuata con {@link #getPathCartellaProperties()} (es:
   *         genep_noreg.properties) utilizzato per lo startup di tutti gli
   *         applicativi WEB
   */
  public abstract String getNomeFileProperties();

  /**
   * @return restituisce il nome del file con le proprieta' in chiaro: si
   *         tratta delle stesse proprieta' originariamente definite nel file
   *         genep criptato.
   */
  public abstract String getNomeFilePropertiesPlainText();

  /**
   * @return the sviluppo
   */
  public static boolean isSviluppo() {
    return sviluppo;
  }
}
