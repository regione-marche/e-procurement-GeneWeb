package it.eldasoft.gene.tags.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.PlugInBase;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.sql.sqlparser.JdbcColumn;
import it.eldasoft.gene.db.sql.sqlparser.JdbcExpression;
import it.eldasoft.gene.db.sql.sqlparser.JdbcExpressionWhere;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.db.sql.sqlparser.JdbcUtils;
import it.eldasoft.gene.db.sql.sqlparser.JdbcWhere;
import it.eldasoft.gene.tags.TagAttributes;
import it.eldasoft.gene.tags.decorators.campi.CampoDecoratorImpl;
import it.eldasoft.gene.tags.functions.DebugSviluppoFunction;
import it.eldasoft.gene.tags.history.HistoryItem;
import it.eldasoft.gene.tags.history.UtilityHistory;
import it.eldasoft.gene.tags.js.Javascript;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Classe che gestisce le utilities per i javascript e gli html. Vengono
 * centralizzati in metodi statici
 *
 * @author marco.franceschin
 */
public class UtilityTags {

	private static Logger logger = Logger.getLogger(UtilityTags.class);

	// Costanti generali dei tags
	/**
	 * Path di default indicante dov si trovano tutte le pagine JSP dell'applicativo
	 */
	public static final String DEFAULT_PATH_PAGINE_JSP = CostantiGenerali.PATH_WEBINF + "pages/";
	public static final char DEFAULT_CHAR_DIVISORE = ';';

	public static final char DEFAULT_CHAR_ELIMINATORE = '\\';

	/*
	 * Nome dei default Hidden che vengono inseriti nelle form
	 */
	/** Path della jsp */
	public static final String DEFAULT_HIDDEN_FORM_JSP_PATH = "jspPath";

	/**
	 * Path della jsp in cui bisogna ridirezionare l'apertura della nuova maschera o
	 * lista
	 */
	public static final String DEFAULT_HIDDEN_FORM_TO_JSP = "jspPathTo";

	public static final String DEFAULT_HIDDEN_PATH_SCHEDA = "pathScheda";

	public static final String DEFAULT_HIDDEN_PATH_SCHEDA_POPUP = "pathSchedaPopUp";

	/** Default per identificare che si tratta di una finestra popUp */
	public static final String DEFAULT_HIDDEN_IS_POPUP = "isPopUp";

	/**
	 * Numero della popUp se � aperta in popUp (Questo campo viene settato via
	 * Javascript)
	 */
	/** Default per identificare che si tratta di una finestra popUp */
	public static final String DEFAULT_HIDDEN_NUMERO_POPUP = "numeroPopUp";

	/** Pagina attiva se scheda */
	public static final String DEFAULT_HIDDEN_FORM_ACTIVEPAGE = "activePage";

	/** Where aggiunta dalla maschera di trova */
	public static final String DEFAULT_HIDDEN_WHERE_DA_TROVA = "trovaAddWhere";

	public static final String DEFAULT_HIDDEN_FILTRI_ADDIZIONALI_DA_TROVA = "trovaAddFilter";

	public static final String HIDDEN_WHERE_FILTRO_ARCHIVIO = "whereFiltroArchivio";

	/// ** From aggiunta dalla maschera di trova */
	// public static final String DEFAULT_HIDDEN_FROM_DA_TROVA = "trovaAddFrom";

	/** Parametri nelle where dalla maschera di trova */
	public static final String DEFAULT_HIDDEN_PARAMETRI_DA_TROVA = "trovaParameter";

	public static final String HIDDEN_PARAMETRI_FILTRO_ARCHVIO = "filtroArchivioParameter";

	/** Nome dell'entita */
	public static final String DEFAULT_HIDDEN_NOME_TABELLA = "entita";

	/** Chiave della tabella compresa con il nome dei campi */
	public static final String DEFAULT_HIDDEN_KEY_TABELLA = "key";

	/** Eventuale chiave tabella parent (se la lista � su una maschera a pagine ) */
	public static final String DEFAULT_HIDDEN_KEY_TABELLA_PARENT = "keyParent";

	/** Elenco di chiavi selezionate */
	public static final String DEFAULT_HIDDEN_KEYS_SELECTED = "keys";

	/** Modo di apertura (Utilizzato in una scheda) */
	public static final String DEFAULT_HIDDEN_PARAMETRO_MODO = "modo";

	/** Elenco dei campi nella maschera a scheda */
	public static final String DEFAULT_HIDDEN_ELENCO_CAMPI = "elencoCampi";

	/** Inizio del nome che da la definizione del campo in una scheda */
	public static final String DEFAULT_HIDDEN_INIZIO_DEFINIZIONE = "def";

	/** Gestore di default */
	public static final String DEFAULT_HIDDEN_NOME_GESTORE = "gestore";

	/**
	 * Campo nascosto inserito nelle pagine a lista per gestire la modalit� di
	 * update
	 */
	public static final String DEFAULT_HIDDEN_UPDATE_LISTA = "updateLista";

	/*
	 * Modi di apertura di una form a scheda
	 */
	/** Apertura scheda in visualizzazione */
	public static final String SCHEDA_MODO_VISUALIZZA = "VISUALIZZA";

	/** Apertura scheda in modifica */
	public static final String SCHEDA_MODO_MODIFICA = "MODIFICA";

	/** Apertura scheda in inserimento */
	public static final String SCHEDA_MODO_INSERIMENTO = "NUOVO";

	/*
	 * Oggetti messi nel request
	 */
	/** Progressivo dei tag */
	public static final String REQUEST_PROGRESSIVO = "progressivoTag";

	/** Javascript Finale */
	public static final String REQUEST_JAVASCRIPT = "javascript.finale";

	/*
	 * Parametri messi nel request dai tag (durante le interazioni)
	 */
	/** Variabile con le chiavi */
	public static final String REQUEST_VAR_KEYS = "chiaveRiga";

	/**
	 * Variabile con le chiavi da utilizzare in un javascript (utilizzati nella
	 * lista)
	 */
	public static final String REQUEST_VAR_JAVA_KEYS = "chiaveRigaJava";

	/**
	 * Variabile con il numero delle riga corrente (utilizzati nelle interazioni
	 * della lista)
	 */
	public static final String REQUEST_VAR_CURRENT_ROW = "currentRow";

	/** Dati letti nella lista e nella scheda * */
	public static final String REQUEST_VAR_DATI_RIGA = "datiRiga";

	/** Modo di apertura della scheda */
	public static final String REQUEST_VAR_MODO_APERTURA_SCHEDA = "modoAperturaScheda";

	/** Tipo di pagina */
	public static final String REQUEST_VAR_TIPO_PAGINA = "tipoPagina";

	/** Pagina di tipo trova */
	public static final String PAGINA_TROVA = "TROVA";

	/** Pagina di tipo lista */
	public static final String PAGINA_LISTA = "LISTA";

	/** Pagina di tipo scheda */
	public static final String PAGINA_SCHEDA = "SCHEDA";

	/** Dati letti nella lista e nella scheda * */
	public static final String REQUEST_VAR_ARCHIVIO_ARRAY_JS = "datiArchivioArrayJs";

	/** Variabile con l'history count */
	public static final String REQUEST_VAR_HISTORY_SIZE = "historySize";

	/** Nome dell'oggetto nel request in cui viene selvato lo stack dei tag */
	public static final String REQUEST_VAR_TAGS_STACK = "stackGeneralTags";
	/**
	 * Nome dell'oggetto che sul request salva tutti gli attributi dei tag di gene
	 */
	public static final String REQUEST_VAR_TAG_ATTRIBUTES_STACK = "stackGeneralTagAttributes";

	/*
	 * Variabili di sessione
	 */
	public static final String SESSION_VAR_HISTORY_TAG = "historyTags";

	public static final String CONFIGURAZIONE_ENCODING = "it.eldasoft.characterEncoding";

	public static final String CONFIGURAZIONE_DISABILITA_ICONE = "it.eldasoft.taborderIconeDisabilitato";

	/**
	 * Prefisso per le hash memorizzate in sessione e contenenti le definizioni
	 * sensibili per la costruzione delle query sql delle liste.
	 */
	public static final String SESSION_PENDICE_DEF_TROVA = "deftrova";

	public static final String TEMP_DEF_TROVA = "tempdeftrova";

	/** Nome dello schema concettuale della maschera */
	public static final String REQUEST_SCHEMA = "SCHEMA_REQUEST";
	/** Identificativo della maschera settato sul request */
	public static final String REQUEST_ID_MASCHERA = "ID_MASCHERA_REQUEST";
	/** Identificativo della pagina attiva */
	public static final String REQUEST_ID_PAGINA = "ID_PAGINA_REQUEST";

	/**
	 * Funzione che verifica se sono state disabilitate le icone da properties
	 *
	 * @return
	 */
	public static boolean isIconeDisabilitate() {
		if ("1".equals(ConfigManager.getValore(CONFIGURAZIONE_DISABILITA_ICONE)))
			return true;
		else
			return false;
	}

	/**
	 * Funzione che converte il nome fisico nel nome da mettere nell'HTML
	 *
	 * @param nomeCampo
	 * @return
	 */
	public static String getNomeFisicoPerHTML(String nomeCampo) {
		return nomeCampo.replace('.', '_');
	}

	/**
	 * Funzione che converte una stringa per immetterla in una stringa javascript o
	 * in una stringa dentro un HTML
	 *
	 * @param stringa
	 * @return
	 */
	public static String convStringa(String stringa) {
		return replaceWithEliminatore(stringa, "\"");
	}

	/**
	 * Funzione che partendo da una tabella ritrova il path per l'apertura
	 *
	 * @param tab entit�
	 * @return percorso "schema/entita/entita-"
	 */
	public static String getPathFromTab(Tabella tab) {
		if (tab != null) {

			StringBuffer buf = new StringBuffer();
			buf.append(tab.getNomeSchema().toLowerCase());
			buf.append("/");
			buf.append(tab.getNomeTabella().toLowerCase());
			buf.append("/");
			buf.append(tab.getNomeTabella().toLowerCase());
			buf.append("-");
			return buf.toString();
		}
		return null;
	}

	/**
	 * Partendo da un'entit� ritrova il path per l'apertura
	 *
	 * @param entita
	 * @return
	 */
	public static String getPathFromEntita(String entita) {
		return UtilityTags.getPathFromTab(DizionarioTabelle.getInstance().getDaNomeTabella(entita));
	}

	@SuppressWarnings("rawtypes")
	public static void debugRequest(ServletRequest request, Logger logger) {
		StringBuffer buf = new StringBuffer();
		buf.append("Attributi:\n");
		for (Enumeration en = request.getAttributeNames(); en.hasMoreElements();) {
			String att = (String) en.nextElement();
			buf.append(att + "=" + request.getAttribute(att) + "\n");
		}
		logger.debug(buf);
		buf.setLength(0);
		buf.append("Parametri:\n");
		for (Enumeration en = request.getParameterNames(); en.hasMoreElements();) {
			String att = (String) en.nextElement();
			buf.append(att + "=" + request.getParameter(att) + "\n");
		}
		logger.debug(buf);

	}

	@SuppressWarnings("rawtypes")
	private static void appendScopeAttrib(StringBuffer buf, PageContext pageContext, int scope) {
		for (Enumeration en = pageContext.getAttributeNamesInScope(scope); en.hasMoreElements();) {
			String name = (String) en.nextElement();
			buf.append(name + "=" + pageContext.getAttribute(name, scope) + "\n");

		}
	}

	public static void debugPageContext(PageContext pageContext, Logger logger) {
		StringBuffer buf = new StringBuffer();
		buf.append("Debuf PageContext:\n");
		buf.append("PageContext.APPLICATION_SCOPE:\n");
		appendScopeAttrib(buf, pageContext, PageContext.APPLICATION_SCOPE);
		buf.append("PageContext.PAGE_SCOPE:\n");
		appendScopeAttrib(buf, pageContext, PageContext.PAGE_SCOPE);
		buf.append("PageContext.REQUEST_SCOPE:\n");
		appendScopeAttrib(buf, pageContext, PageContext.REQUEST_SCOPE);
		buf.append("PageContext.SESSION_SCOPE:\n");
		appendScopeAttrib(buf, pageContext, PageContext.SESSION_SCOPE);
		logger.debug(buf.toString());
	}

	/**
	 * Funzione che restituisce la stringa di un campo di tipo hidden
	 *
	 * @param nome   Nome del campo
	 * @param valore Valore del campo
	 * @return String HTML per la creazione del campo di input Hidden
	 */
	public static String getHtmlHideInput(String nome, Object valore) {
		StringBuffer buf = new StringBuffer();
		String lVal = "";
		if (valore != null)
			lVal = valore.toString();
		buf.append("<input type=\"hidden\" ");
		buf.append(getHtmlAttrib("name", nome));
		buf.append(getHtmlAttrib("value", lVal));
		buf.append("/>\n");

		return buf.toString();
	}

	/**
	 * Funzione che restituisce la stringa di un campo di tipo hidden
	 *
	 * @param nome   Nome del campo
	 * @param id     Id del campo
	 * @param valore Valore del campo
	 * @return String HTML per la creazione del campo di input Hidden
	 */
	public static String getHtmlHideInputWithId(String nome, String id, Object valore) {
		StringBuffer buf = new StringBuffer();
		String lVal = "";
		if (valore != null)
			lVal = valore.toString();
		buf.append("<input type=\"hidden\" ");
		buf.append(getHtmlAttrib("name", nome));
		buf.append(getHtmlAttrib("id", id));
		buf.append(getHtmlAttrib("value", lVal));
		buf.append("/>\n");

		return buf.toString();
	}

	/**
	 * Funzione privata che esegue la conversione dei caratteri nell'elenco
	 * aggiungendo i caratteri eliminatore davanti. Ovviamente si aggiunge il
	 * carattere eliminatore anche davanti all'eliminatore
	 *
	 * @param stringa
	 * @param elencoChars
	 * @param eliminatore
	 * @return
	 */
	public static String replaceWithEliminatore(String stringa, String elencoChars) {
		StringBuffer buf = new StringBuffer();
		if (elencoChars == null)
			elencoChars = "";
		if (stringa == null)
			return "";
		// Aggiungo l'eliminatore all'elenco dei caratteri
		elencoChars += DEFAULT_CHAR_ELIMINATORE;
		for (int i = 0; i < stringa.length(); i++) {
			// Se � nell'elenco dei caratteri da aggiungere l'eliminatore lo
			// aggiungo
			if (elencoChars.indexOf(stringa.charAt(i)) >= 0)
				buf.append(DEFAULT_CHAR_ELIMINATORE);
			buf.append(stringa.charAt(i));
		}
		return buf.toString();
	}

	/**
	 * Funzione che converte un array di elementi in una stringa
	 *
	 * @param valori   Elenco di oggetti che contengono i valori
	 * @param divisore Carattere utilizzato per dividere le varie parti
	 * @return
	 */

	public static String arrayToString(Object[] valori, char divisore) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < valori.length; i++) {
			String val;
			if (i > 0)
				buf.append(divisore);
			if (valori[i] != null)
				// Eseguo l'aggiunta dell'eliminatore alla stringa passata
				val = replaceWithEliminatore(valori[i].toString(), String.valueOf(divisore));
			else
				val = "";
			buf.append(val);
		}
		return buf.toString();
	}

	/**
	 * Funzione che converte i parametri in una stringa divisa dal divisore di
	 * default
	 *
	 * @param elencoParametri
	 * @return
	 */
	public static String parametriToString(Vector<JdbcParametro> elencoParametri) {
		Vector<String> stringArray = new Vector<String>();
		for (int i = 0; i < elencoParametri.size(); i++) {
			if (elencoParametri.get(i) instanceof JdbcParametro) {
				JdbcParametro par = elencoParametri.get(i);
				stringArray.add(par.toString(true));
			}
		}
		// Creo l'array con il divisore
		return arrayToString(stringArray.toArray(), DEFAULT_CHAR_DIVISORE);
	}

	/**
	 * Converte una stringa divisa dal carattere divisore.
	 *
	 * @param stringa
	 * @param divisore
	 * @return array di stringhe, oppure stringa vuota se la stringa di input � null
	 */
	public static String[] stringToArray(String stringa, char divisore) {
		String[] risultato = UtilityStringhe.deserializza(stringa, divisore);
		if (risultato == null)
			risultato = new String[] { "" };
		return risultato;
	}

	/**
	 * Trasforma una stringa con l'elenco dei parametri divisi da ; nel vettore con
	 * l'elenco dei parametri
	 *
	 * @param elencoParametri
	 * @return
	 */
	public static Vector<JdbcParametro> stringToVectorJdbcParametro(String elencoParametri) {
		Vector<JdbcParametro> vRet = new Vector<JdbcParametro>();
		if (elencoParametri == null || elencoParametri.length() == 0)
			return vRet;
		String valori[] = stringToArray(elencoParametri, DEFAULT_CHAR_DIVISORE);
		// WE335: si risolve parzialmente il problema dello split dei parametri
		// quando nei parametri come valore esiste il separatore
		if (elencoParametri.length() > 2 && JdbcParametro.isValidType(elencoParametri.charAt(0))
				&& elencoParametri.charAt(1) == ':') {
			// siamo nel caso di parametri tipizzati, allora la gestione del ";" nei
			// valori pu� essere gestita
			String valore = "";
			for (int i = 0; i < valori.length; i++) {
				if (valori[i].length() > 2 && JdbcParametro.isValidType(valori[i].charAt(0))
						&& valori[i].charAt(1) == ':') {
					if (valore.length() != 0) {
						// processo il precedente elemento
						vRet.add(JdbcParametro.getParametro(valore));
					}
					// e poi memorizzo il presente
					valore = valori[i];
				} else {
					valore += DEFAULT_CHAR_DIVISORE + valori[i];
				}
			}
			if (valore.length() != 0) {
				// processo quant'ho composto finora e poi azzero
				vRet.add(JdbcParametro.getParametro(valore));
			}
		} else {
			// siamo nel caso di archivio, e cmq di parametri senza <tipo>:, per cui
			// non e' possibile distinguere l'uso di ";" come dato dall'uso come
			// separatore
			for (int i = 0; i < valori.length; i++) {
				vRet.add(JdbcParametro.getParametro(valori[i]));
			}
		}
		return vRet;
	}

	/**
	 * Trasforma una stringa con l'elenco dei parametri divisi da ; nell'array di
	 * JdbcParametro con l'elenco dei parametri
	 *
	 * @param elencoParametri serializzato
	 * @return array di JdbcParametro, eventualmente vuoto se non sono presenti
	 *         elementi
	 * @since 1.4.0
	 */
	public static JdbcParametro[] stringToArrayJdbcParametro(String elencoParametri) {
		// WE335: si risolve parzialmente il problema dello split dei parametri
		// quando nei parametri come valore esiste il separatore
		Vector<JdbcParametro> v = stringToVectorJdbcParametro(elencoParametri);
		if (v.size() == 0)
			return new JdbcParametro[0];
		else
			return v.toArray(new JdbcParametro[] {});
	}

	/**
	 * Funzione che converte un attributo da mettere dentro un tag HTML
	 *
	 * @param nome   Nome in cui i caratteri speciali venvono convertiti in _
	 * @param valore Valore in cui i " vangono convertiti in \"
	 * @return Stringa da inserire nell'HTML
	 */
	public static String getHtmlAttrib(String nome, String valore) {
		// ************************************************************
		// Storia Modifiche:
		// Data Utente Descrizione
		// 14/12/2006 M.F. Trasformazione del " in &#34;
		// ************************************************************

		StringBuffer buf = new StringBuffer();
		buf.append(getNomeFisicoPerHTML(nome));
		buf.append("=\"");
		buf.append(convStringaHREFforAttrib(valore));
		buf.append("\" ");
		return buf.toString();
	}

	/**
	 * Funzione che estrae il javascript dal page context
	 *
	 * @param pageContext
	 * @return
	 */
	public static Javascript getJavascript(PageContext pageContext) {
		if (pageContext == null)
			return null;
		// Se non esiste ancora l'oggetto lo istanzio
		if (pageContext.getAttribute(UtilityTags.REQUEST_JAVASCRIPT, PageContext.REQUEST_SCOPE) == null)
			pageContext.setAttribute(UtilityTags.REQUEST_JAVASCRIPT, new Javascript(), PageContext.REQUEST_SCOPE);
		return (Javascript) pageContext.getAttribute(UtilityTags.REQUEST_JAVASCRIPT, PageContext.REQUEST_SCOPE);
	}

	/**
	 * Serializza il contenuto di un array in una stringa utilizzando ";"
	 *
	 * @param strings
	 * @return
	 */
	public static String arrayToString(Object[] strings) {
		return arrayToString(strings, DEFAULT_CHAR_DIVISORE);
	}

	/**
	 * Aggiunge tutti gli hidden di default a una form
	 *
	 * @param context PageContext del tag
	 * @return Html da aggiungere
	 */
	public static String getHtmlDefaultHidden(PageContext context) {
		StringBuffer buf = new StringBuffer();
		HttpServletRequest req = (HttpServletRequest) context.getRequest();
		// Adesso aggiungo tutti gli hidden di default
		buf.append(getHtmlHideInput(DEFAULT_HIDDEN_FORM_JSP_PATH, req.getServletPath()));
		// Adesso aggiungo tutti gli hidden di default
		buf.append(getHtmlHideInput(DEFAULT_HIDDEN_FORM_TO_JSP,
				context.getAttribute(DEFAULT_HIDDEN_FORM_TO_JSP, PageContext.REQUEST_SCOPE)));
		// Pagina attiva
		buf.append(getHtmlHideInput(DEFAULT_HIDDEN_FORM_ACTIVEPAGE,
				getParametro(context, DEFAULT_HIDDEN_FORM_ACTIVEPAGE)));
		buf.append(getHtmlHideInput(DEFAULT_HIDDEN_IS_POPUP, getParametro(context, DEFAULT_HIDDEN_IS_POPUP)));
		buf.append(getHtmlHideInput(DEFAULT_HIDDEN_NUMERO_POPUP, ""));
		return buf.toString();
	}

	/**
	 * Crea un campo hidden per una form con il nome del parametro ed il valore del
	 * parametro
	 *
	 * @param context       PageContext
	 * @param nomeParametro Nome del parametro
	 * @return
	 */
	public static String getHtmlHideInputFromParam(PageContext context, String nomeParametro) {
		String valore = getParametro(context, nomeParametro);
		return getHtmlHideInput(nomeParametro, valore);
	}

	/**
	 * Estrae un parametro dal request
	 *
	 * @param context
	 * @param nomeParametro
	 * @return
	 */
	public static String getParametro(PageContext context, String nomeParametro) {
		if (context.getRequest() instanceof HttpServletRequest) {
			return UtilityStruts.getParametroString((HttpServletRequest) context.getRequest(), nomeParametro);
		}
		return null;
	}

	/**
	 * Crea un'espressione jdbc partendo dal metadati dei campi
	 *
	 * @param campo
	 * @return
	 */
	public static JdbcExpression getJdbcExpression(Campo campo) {
		if (campo != null) {
			return new JdbcExpression(new JdbcColumn(null, campo.getNomeFisicoCampo()));
		}
		return null;
	}

	/**
	 * Restituisce un'espressione di campo
	 *
	 * @param campo
	 * @return
	 */
	public static JdbcExpression getJdbcExpression(CampoDecoratorImpl campo) {
		if (campo != null) {
			return new JdbcExpression(new JdbcColumn(null, campo.getNomeFisico()));
		}
		return null;
	}

	/**
	 * Aggiunge la where sui campi chiave su un jdbcWhere
	 *
	 * @param where
	 * @param keys  Campi chiave nel modo [NOME_CAMPO]=[VALORE];+
	 */
	public static void jdbcAddKeyWhere(JdbcWhere where, String keys) {

		String param[] = stringToArray(keys, DEFAULT_CHAR_DIVISORE);
		for (int i = 0; i < param.length; i++) {
			String campo = param[i];
			if (campo.indexOf('=') >= 0) {
				where.append(new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
				String valore = campo.substring(campo.indexOf('=') + 1);
				campo = campo.substring(0, campo.indexOf('='));
				where.append(new JdbcExpression(new JdbcColumn(null, campo)));
				where.append(new JdbcExpression("="));
				where.append(new JdbcExpression(JdbcParametro.getParametro(valore)));
			}
		}

	}

	/**
	 * Crea una classe partendo da un nome mediante reflection
	 *
	 * @param nomeClasse Nome completo della classe
	 * @return Oggetto creato altrimenti null se non creato
	 */
	@SuppressWarnings("rawtypes")
	public static Object createObject(String nomeClasse) {
		Object obj = null;
		try {
			// get String Class
			Class cl = Class.forName(nomeClasse);
			// get the constructor
			java.lang.reflect.Constructor constructor = cl.getConstructor(new Class[] {});
			// create an instance
			obj = constructor.newInstance(new Object[] {});

		} catch (Exception e) {
			obj = null;
		}
		return obj;
	}

	/**
	 * Aggiunge una where facendo il replace dei campi chiave
	 *
	 * @param context  page context con i valori dei campi chiave
	 * @param objWhere Oggetto in cui aggiungere l'eventuale where
	 * @param where    Where in cui eseguire il replace dei valori tra ##
	 */
	public static void addWhereSelezionata(PageContext context, JdbcWhere objWhere, String where) {
		HashMap<String, JdbcParametro> map = stringParamsToHashMap(
				getParametro(context, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA), null);

		Vector<JdbcParametro> parameters = new Vector<JdbcParametro>();
		String lsWhere = replaceParametri(parameters, where, map);
		// A questo punto inserisco la parte di where
		JdbcParametro param[] = new JdbcParametro[parameters.size()];
		for (int i = 0; i < parameters.size(); i++) {
			param[i] = parameters.get(i);
		}
		objWhere.append(new JdbcExpression(lsWhere, param));
	}

	/**
	 * Esegue il replace dei parametri
	 *
	 * @param parameters Vettore che conterr� tutti i parametri
	 * @param sql        Sql in cui eseguire il replace dei parametri
	 * @param map        Stringa con la mappa dei parametri
	 * @return
	 */
	public static String replaceParametri(Vector<JdbcParametro> parameters, String sql, String map) {
		return replaceParametri(parameters, sql, stringParamsToHashMap(map, null));
	}

	/**
	 * Esegue il replace dei parametri definiti nella HashMap
	 *
	 * @param sql Sql con i parametri divisi all'interno di ##
	 * @param map Mappa con i parametri
	 * @return
	 */
	public static String replaceParametri(Vector<JdbcParametro> parameters, String sql,
			HashMap<String, JdbcParametro> map) {
		StringBuffer ret = new StringBuffer("");
		int pos = 0;
		do {
			pos = sql.indexOf('#');
			if (pos >= 0) {
				int pos1 = sql.indexOf('#', pos + 1);
				if (pos1 > 0) {
					// Trovato un campo chiave
					String campo = sql.substring(pos + 1, pos1);
					ret.append(sql.substring(0, pos));
					ret.append("?");
					sql = sql.substring(pos1 + 1);
					// Verifico se esiste il parametro
					if (map.get(campo) instanceof JdbcParametro) {
						parameters.add(map.get(campo));
					} else
						throw new RuntimeException(
								"Attenzione il parametro: " + campo + " non � di tipo JdbcParametro !");
				} else
					pos = 0;
			}
		} while (pos >= 0);
		ret.append(sql);
		return ret.toString();
	}

	/**
	 * Converte un elenco di parametri divisi da ";" in una HashMap di
	 * JdbcParametri<br>
	 * I parametri decono essere del tipo
	 * [NOME_CAMPO=]PARAMETRO[;[NOME_CAMPO=]PARAMETRO]* il parametro deve essere del
	 * formato come JdbcParametro [[TIPO_PAR]:]VALORE
	 *
	 * @param parametri Stringa di parametri
	 * @param map       Mappa alla quele aggiungere i parametri. se null viene
	 *                  creata
	 * @return
	 */
	public static HashMap<String, JdbcParametro> stringParamsToHashMap(String parametri,
			HashMap<String, JdbcParametro> map) {
		// Se non ancora creata la creo
		if (map == null)
			map = new HashMap<String, JdbcParametro>();
		String vals[] = stringToArray(parametri, DEFAULT_CHAR_DIVISORE);
		// Scorro tutti i parametri
		for (int i = 0; i < vals.length; i++) {
			String campo = String.valueOf(map.size());
			if (vals[i] != null && vals[i].indexOf('=') > 0 && (vals[i].length() <= 2 || vals[i].charAt(1) != ':')) {
				campo = vals[i].substring(0, vals[i].indexOf('='));
				vals[i] = vals[i].substring(vals[i].indexOf('=') + 1);
			}
			map.put(campo, new JdbcParametro(vals[i]));
			// {MF260307} Aggiungo anche il campo senza nome tabella (per facilitare
			// le ricerche del parametro con il solo nome)
			if (campo != null && campo.length() > 0 && campo.indexOf('.') >= 0) {
				map.put(campo.substring(campo.indexOf('.') + 1), new JdbcParametro(vals[i]));
			}
		}
		return map;
	}

	/**
	 * Converte una stringa di parametri in un vettore di JdbcParametro
	 *
	 * @param parametri
	 * @param vect
	 * @return
	 */
	public static Vector<JdbcParametro> stringParamsToVector(String parametri, Vector<JdbcParametro> vect) {
		// Se non ancora creata la creo
		if (vect == null)
			vect = new Vector<JdbcParametro>();
		String vals[] = stringToArray(parametri, DEFAULT_CHAR_DIVISORE);
		// Scorro tutti i parametri
		for (int i = 0; i < vals.length; i++) {
			if (vals[i] != null && vals[i].indexOf('=') > 0 && (vals[i].length() <= 2 || vals[i].charAt(1) != ':')) {
				vals[i] = vals[i].substring(vals[i].indexOf('=') + 1);
			}
			vect.add(new JdbcParametro(vals[i]));
		}
		return vect;
	}

	/**
	 * Trasforma un vettore di DataColumn in array di DataColumn
	 *
	 * @param vect
	 * @return
	 */
	public static DataColumn[] vectorToArrayOfDataColumn(Vector<DataColumn> vect) {
		DataColumn ret[] = new DataColumn[vect.size()];
		for (int i = 0; i < vect.size(); i++) {
			if (vect.get(i) instanceof DataColumn)
				ret[i] = vect.get(i);
			else
				ret[i] = null;
		}

		return ret;
	}

	/**
	 * Restituisce l'array di oggetti da un vettore di JdbcParametro
	 *
	 * @param params Vettore di JdbcParametro
	 * @return
	 */
	public static Object[] vectorParamToObjectArray(Vector<?> params) {
		LobHandler lobHandler = new DefaultLobHandler(); // reusable object
		// Sabbadin 22/07/2014: commentata sezione in quanto dava problemi con JNDI
		// if
		// (SqlManager.DATABASE_ORACLE.equalsIgnoreCase(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE)))
		// {
		// // oracle necessita di una gestione ad hoc e mediante JDBC
		// lobHandler = new OracleLobHandler();
		// ((OracleLobHandler) lobHandler).setNativeJdbcExtractor(new
		// SimpleNativeJdbcExtractor());
		// }

		if (params == null)
			return new Object[] {};
		Object ret[] = new Object[params.size()];
		for (int i = 0; i < params.size(); i++) {
			if (params.get(i) instanceof JdbcParametro) {
				JdbcParametro par = (JdbcParametro) params.get(i);
				if (par.getTipo() == JdbcParametro.TIPO_BINARIO) {
					// il tipo di dato binario deve essere un SqlLobValue
					try {
						ret[i] = new SqlLobValue(par.byteArrayOutputStreamValue().toByteArray(), lobHandler);
					} catch (GestoreException e) {
						// non si verifichera' mai perche' si controlla il tipo
					}
				} else
					ret[i] = par.getValue();
			} else if (params.get(i) instanceof String) {
				String val = (String) params.get(i);
				if (val != null && val.length() > 1 && JdbcParametro.isValidType(val.charAt(0))
						&& val.charAt(1) == ':') {
					ret[i] = new JdbcParametro(val).getValue();
				} else
					ret[i] = params.get(i);

			} else
				ret[i] = params.get(i);
		}
		return ret;
	}

	/**
	 * Trasforma una stringa nel formato TABELLA.CAMPO=VALORE. in un tipo DataColumn
	 *
	 * @param valore Stringa nel formato
	 * @return
	 */
	public static DataColumn getDataColumn(String valore) {
		String campo = "campo";
		if (valore.indexOf('=') >= 0) {
			campo = valore.substring(0, valore.indexOf('='));
			valore = valore.substring(valore.indexOf('=') + 1);
		} else
			throw new RuntimeException("getDataColumn: Formato della stringa di input noon valido: " + valore);
		JdbcParametro val = new JdbcParametro(valore);
		DataColumn colonna = new DataColumn(null, campo, val.getTipo());
		colonna.setValue(val);
		return colonna;
	}

	/**
	 * Ricava un array di DataColumn da una stringa con i dati formattati
	 *
	 * @param parametroString
	 * @return
	 */
	public static DataColumn[] stringKeysToColumnWithParam(String parametroString) {
		// ************************************************************
		// Storia Modifiche:
		// Data Utente Descrizione
		// 10/11/2006 M.F. Aggiungo il settaggio delle colonne come chiavi
		// ************************************************************

		String vals[] = UtilityTags.stringToArray(parametroString, UtilityTags.DEFAULT_CHAR_DIVISORE);
		DataColumn colVal[] = new DataColumn[vals.length];
		for (int i = 0; i < vals.length; i++) {
			colVal[i] = getDataColumn(vals[i]);
			colVal[i].setChiave(true);
		}
		return colVal;
	}

	/**
	 * Funzione che verifica che la maschera non sia aperta in modifica
	 *
	 * @param request ServletRequest da cui estrarre il modo
	 * @return true Se � in modifica false altrimenti
	 */

	public static boolean isInModifica(ServletRequest request) {
		String lsModo = UtilityStruts.getParametroString((HttpServletRequest) request,
				UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO);
		if (lsModo == null)
			lsModo = UtilityTags.SCHEDA_MODO_VISUALIZZA;

		return lsModo.equals(UtilityTags.SCHEDA_MODO_MODIFICA) || lsModo.equals(UtilityTags.SCHEDA_MODO_INSERIMENTO);
	}

	/**
	 * Funzione che converte un href con l'eventuale contextPath
	 *
	 * @param href    Stringa di riferimento
	 * @param request request
	 * @return
	 */
	public static String convertHREF(String href, ServletRequest request) {
		if (href != null) {
			if (href.indexOf("$CONTEXT$") >= 0) {
				// Faccio il replace con il context path
				if (request instanceof HttpServletRequest) {
					// replace della stringa
					href = StringUtils.replace(href, "$CONTEXT$", ((HttpServletRequest) request).getContextPath());
				}

			}
		}
		return href;
	}

	public static void salvaCurrentAction(ServletRequest request) {
		getUtilityHistory(((HttpServletRequest) request).getSession()).saveCurrent(request);
	}

	/**
	 * Funzione che setta gli attributi generali dei tags, prendendoli dai
	 * parametri, in modo da usarli nelle JSP
	 *
	 * @param request
	 */
	private static void settaAttributiGeneraliTags(ServletRequest request) {
		// ************************************************************
		// Storia Modifiche:
		// Data Utente Descrizione
		// 22/11/2006 M.F. Aggiunto l'attributo del numero del popUp
		// ************************************************************

		String elencoAttributi[] = new String[] { DEFAULT_HIDDEN_FORM_JSP_PATH, DEFAULT_HIDDEN_FORM_TO_JSP,
				DEFAULT_HIDDEN_FORM_ACTIVEPAGE, DEFAULT_HIDDEN_KEY_TABELLA, DEFAULT_HIDDEN_NOME_TABELLA,
				DEFAULT_HIDDEN_PARAMETRI_DA_TROVA,
				// DEFAULT_HIDDEN_FROM_DA_TROVA,
				DEFAULT_HIDDEN_WHERE_DA_TROVA, DEFAULT_HIDDEN_PARAMETRO_MODO, DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
				DEFAULT_HIDDEN_NUMERO_POPUP };
		// Scorro tutti gli elementi da settare nel request
		for (int i = 0; i < elencoAttributi.length; i++) {
			// Se esiste il parametro
			if (request.getParameter(elencoAttributi[i]) != null) {
				// Se non esiste gia l'attributo
				if (request.getAttribute(elencoAttributi[i]) == null) {
					request.setAttribute(elencoAttributi[i], request.getParameter(elencoAttributi[i]));
				}
			}
		}
		if (request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) request;
			if (UtilityStruts.getNumeroPopUp(req) > 0)
				request.setAttribute(DEFAULT_HIDDEN_IS_POPUP, "1");
			else
				request.setAttribute(DEFAULT_HIDDEN_IS_POPUP, "0");

		}

		addDefaultAttributi(request);
	}

	public static UtilityHistory getUtilityHistory(HttpSession session) {
		UtilityHistory objRet = null;
		objRet = (UtilityHistory) session.getAttribute(SESSION_VAR_HISTORY_TAG);
		// Se non esiste ancora l'oggetto lo creo
		if (objRet == null) {
			objRet = new UtilityHistory(session);
			session.setAttribute(SESSION_VAR_HISTORY_TAG, objRet);
		}

		return objRet;
	}

	/**
	 * Funzione che ritrova un carattere saltando l'eliminatore
	 *
	 * @param stringa     Stringa in cui cercare il carattere
	 * @param toFind      Carattere da trovare
	 * @param eliminatore Eventuale eliminatore che fa saltare il carattere
	 * @param startPos    Posizione di partenza
	 * @return Posizione in cui � stato trovato
	 */
	public static int indexOf(String stringa, char toFind, char eliminatore, int startPos) {
		if (startPos < 0)
			startPos = 0;
		if (startPos >= stringa.length())
			return -1;
		int pos = startPos - 1;
		do {
			pos = stringa.indexOf(toFind, pos + 1);
			// Se il carattere precedente � un eliminatore continuo la ricerca
		} while (pos > 0 && stringa.charAt(pos - 1) == eliminatore);
		return pos;
	}

	public static String convStringaHREFParemeter(String val) {
		try {
			return URLEncoder.encode(val, ConfigManager.getValore(CONFIGURAZIONE_ENCODING));
		} catch (UnsupportedEncodingException e) {
			logger.error("Errore in codifica dell'URL: " + e.getMessage());
		}
		return val;
	}

	/**
	 * Funzione che converte i " in %22
	 *
	 * @param string
	 * @return
	 */
	public static String convStringaHREFforJS(String href) {
		return StringUtils.replace(href, "\"", "%22");
	}

	/**
	 * Funzione che converte la stringa per metterla negli attributi
	 *
	 * @param href
	 * @return
	 */
	public static String convStringaHREFforAttrib(String href) {
		return StringUtils.replace(href, "\"", "&#34;");
	}

	/**
	 * Funzione che setta rli attributi di dafault sul request (Attributi
	 * utilizzabili dai tags)
	 *
	 * @param request
	 */
	public static void addDefaultAttributi(ServletRequest request) {
		// ************************************************************
		// Storia Modifiche:
		// Data Utente Descrizione
		// 10/11/2006 M.F. Eliminato i campi chiave perch� non necessario
		// ************************************************************
		request.setAttribute(REQUEST_VAR_HISTORY_SIZE,
				new Integer(UtilityTags.getUtilityHistory(((HttpServletRequest) request).getSession())
						.size(UtilityStruts.getNumeroPopUp(request))));
	}

	/**
	 * Ritorna un messaggio valorizzato a partire dal resource di default
	 *
	 * @param key    Chiave del resource
	 * @param params Elenco dei parametri per il replace
	 * @param html   flag che dice se deve essere messo all'interno di un HTML. Solo
	 *               per l'errore perch� se non viene trovato viene colorato di
	 *               rosso
	 * @return messaggio opportunamente valorizzato
	 */
	public static String getResource(String key, String[] params, boolean html) {
		return UtilityTags.getResource(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE, key, params, html);
	}

	/**
	 * Ritorna un messaggio valorizzato da un resource
	 *
	 * @param resource resource bundle in cui ricercare il messaggio identificato
	 *                 dalla key
	 * @param key      Chiave del resource
	 * @param params   Elenco dei parametri per il replace
	 * @param html     flag che dice se deve essere messo all'interno di un HTML.
	 *                 Solo per l'errore perche se non viene trovato viene colorato
	 *                 di rosso
	 * @return messaggio opportunamente valorizzato
	 */
	public static String getResource(String resource, String key, String[] params, boolean html) {
		String lsKey = "";
		if (key != null)
			lsKey = key.toString();

		if (params == null)
			params = new String[] {};

		String ret = "";
		try {
			ret = ResourceBundle.getBundle(resource).getString(lsKey);
			// Se ci sono parametri nella stringa li restituisco
			if (params != null && params.length > 0) {
				// Sostituisco tutti i parametri
				for (int i = 0; i < params.length; i++) {
					if (params[i] == null)
						params[i] = "";
					ret = StringUtils.replace(ret, "{" + i + "}", params[i]);
				}
			}
			// A questo punto annullo titti i {nn}

		} catch (Throwable t) {
			// Se si verifica un errore allora lo restituisco
			StringBuffer buf = new StringBuffer("");
			if (html)
				buf.append("<font color=\"#ff0000\"><b>");
			buf.append(key);
			if (html)
				buf.append("</b> <small><i>");
			buf.append("[");
			buf.append(t.getMessage());
			buf.append("]");
			if (html)
				buf.append("</i></small></font>");
			return buf.toString();
		}
		return ret;
	}

	/**
	 * Funzione che cerca l'indice dell'elemento in un array
	 *
	 * @param obj     Oggetto da cercare
	 * @param objects Arrai di oggetti in cui cercare l'elemento
	 * @return
	 */
	public static int indexOf(Object obj, Object[] objects) {
		if (objects != null && objects.length > 0) {
			for (int i = 0; i < objects.length; i++) {
				if (obj != null) {
					if (objects[i] != null && obj.equals(objects[i]))
						return i;
				} else if (objects[i] == null)
					return i;
			}
		}
		return -1;
	}

	/**
	 * Funzione che verifica se siamo in situazione di popUp
	 *
	 * @param context
	 * @return
	 */
	public static boolean isPopUp(PageContext context) {
		return UtilityStruts.isPopUp((HttpServletRequest) context.getRequest());
	}

	public static int getNumeroPopUp(PageContext context) {
		return UtilityStruts.getNumeroPopUp(context.getRequest());
	}

	/**
	 * Funzione che viene chiamata prima dell'esecuzione di una action
	 *
	 * @param request
	 */
	public static void preAction(ServletRequest request) {
		// Setto tutti gli attributi generali utiolizzati nei tags
		settaAttributiGeneraliTags(request);
		salvaCurrentAction(request);
	}

	/**
	 * Funzione che scatta dopo un azione struts
	 *
	 * @param request
	 */
	public static void postAction(ServletRequest request) {
		UtilityHistory historyUtil = getUtilityHistory(((HttpServletRequest) request).getSession());
		if (historyUtil != null) {
			historyUtil.setDefaultAttribToCurrent(request);
		}
	}

	public static boolean isNavigazioneDisabilitata(PageContext pageContext) {
		String navDisabilitata = getParametro(pageContext, CostantiGenerali.NAVIGAZIONE_DISABILITATA);
		if (navDisabilitata != null && navDisabilitata.equals(CostantiGenerali.DISABILITA_NAVIGAZIONE)) {
			return true;
		}
		return false;
	}

	/**
	 * Funzione che controlla una protezione estraendola dal profilo dell'utente
	 *
	 * @param pageContext
	 * @param key
	 * @return
	 */
	public static boolean checkProtection(PageContext page, String key, boolean defaultVal) {
		String tipo = "", azione = "", oggetto = "";
		if (key.indexOf('.') >= 0) {
			tipo = key.substring(0, key.indexOf('.'));
			key = key.substring(key.indexOf('.') + 1);
		}
		if (key.indexOf('.') >= 0) {
			azione = key.substring(0, key.indexOf('.'));
			key = key.substring(key.indexOf('.') + 1);
		}
		oggetto = key;
		// session.getAttribute(arg0)
		return checkProtection(page, tipo, azione, oggetto, defaultVal);
	}

	public static boolean checkProtection(PageContext page, String tipo, String azione, String oggetto,
			boolean defaultVal) {
		boolean ret = false;

		GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager", page, GeneManager.class);
		// Estraggo il profilo dell'utente
		if (geneManager != null) {
			ret = geneManager.getProfili().checkProtec(
					(String) page.getAttribute(CostantiGenerali.PROFILO_ATTIVO, PageContext.SESSION_SCOPE), tipo,
					azione, oggetto, defaultVal);
		}
		if (PlugInBase.isSviluppo())
			DebugSviluppoFunction.addProtecCheck(page, tipo + "." + azione + "." + oggetto, ret);
		return ret;

	}

	public static ProfiloUtente getProfileUtente(HttpSession session) {
		return (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
	}

	/**
	 * Aggiunge nello stack dei tags un oggetto
	 *
	 * @param request
	 * @param tag
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addToStack(PageContext page, Object tag) {
		if (tag != null) {
			Stack<Object> stack = null;
			if (page.getRequest().getAttribute(REQUEST_VAR_TAGS_STACK) instanceof Stack) {
				stack = (Stack<Object>) page.getRequest().getAttribute(REQUEST_VAR_TAGS_STACK);
			} else {
				stack = new Stack<Object>();
				page.getRequest().setAttribute(REQUEST_VAR_TAGS_STACK, stack);
			}
			// Aggiungo l'oggetto allo stack
			stack.push(tag);
		}

	}

	/**
	 * Elimina un oggetto dallo stack
	 *
	 * @param request
	 * @param tag
	 */
	@SuppressWarnings("rawtypes")
	public static void removeFromStack(PageContext page, Object tag) {
		if (tag != null) {
			Stack stack = null;
			if (page.getRequest().getAttribute(REQUEST_VAR_TAGS_STACK) instanceof Stack) {
				stack = (Stack) page.getRequest().getAttribute(REQUEST_VAR_TAGS_STACK);
				Object obj;
				if (stack.contains(tag))
					while (stack.size() > 0) {
						obj = stack.pop();
						if (obj == tag)
							return;
					}
			}
		}
	}

	/**
	 * Estrae dallo stack l'ultimo oggetto del tipo voluto
	 *
	 * @param request
	 * @param tipoobj
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Object getFromStack(PageContext page, Class tipoobj) {
		Stack stack = null;
		if (page.getRequest().getAttribute(REQUEST_VAR_TAGS_STACK) instanceof Stack) {
			stack = (Stack) page.getRequest().getAttribute(REQUEST_VAR_TAGS_STACK);
			for (int i = stack.size() - 1; i >= 0; i--) {
				if (stack.get(i).getClass().equals(tipoobj))
					return stack.get(i);
			}
		}
		return null;
	}

	/**
	 * Aggiunge nello stack degli attributi dei tag degli attributi
	 *
	 * @param pageContext
	 * @param attribs
	 */
	@SuppressWarnings("unchecked")
	public static void addToStackTagAttributes(PageContext pageContext, TagAttributes attribs) {
		if (attribs != null) {
			Stack<TagAttributes> stack = null;
			if (pageContext.getRequest().getAttribute(REQUEST_VAR_TAG_ATTRIBUTES_STACK) instanceof Stack) {
				stack = (Stack<TagAttributes>) pageContext.getRequest().getAttribute(REQUEST_VAR_TAG_ATTRIBUTES_STACK);
			} else {
				stack = new Stack<TagAttributes>();
				pageContext.getRequest().setAttribute(REQUEST_VAR_TAG_ATTRIBUTES_STACK, stack);
			}
			// Aggiungo l'oggetto allo stack
			stack.push(attribs);
		}
	}

	/**
	 * Funzione che estrae dallo stack degli attributi l'ultimo attributo del tipo
	 *
	 * @param pageContext pageContext
	 * @param varName     Nome delle variabile
	 * @return
	 */
	public static TagAttributes getFromStackTagAttributes(PageContext pageContext, String varName) {
		if (pageContext.getRequest().getAttribute(REQUEST_VAR_TAG_ATTRIBUTES_STACK) instanceof Stack) {
			@SuppressWarnings("unchecked")
			Stack<TagAttributes> stack = (Stack<TagAttributes>) pageContext.getRequest()
					.getAttribute(REQUEST_VAR_TAG_ATTRIBUTES_STACK);
			for (int i = stack.size() - 1; i >= 0; i--) {
				if (stack.get(i) instanceof TagAttributes) {
					TagAttributes attrib = stack.get(i);
					if (attrib.getNomeVarRequest().equals(varName)) {
						return attrib;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Funzione che rimuove un tag attributes del tipo dallo stack
	 *
	 * @param pageContext
	 * @param varName
	 */
	public static void removeFromStackTagAttributes(PageContext pageContext, TagAttributes attrib) {

		if (pageContext.getRequest().getAttribute(REQUEST_VAR_TAG_ATTRIBUTES_STACK) instanceof Stack) {
			if (attrib != null) {
				@SuppressWarnings("unchecked")
				Stack<TagAttributes> stack = (Stack<TagAttributes>) pageContext.getRequest()
						.getAttribute(REQUEST_VAR_TAG_ATTRIBUTES_STACK);
				Object obj;
				if (stack.contains(attrib))
					while (stack.size() > 0) {
						obj = stack.pop();
						if (obj == attrib)
							return;
					}
			}
		}
	}

	/**
	 * Funzione che ricava l'id nel request
	 *
	 * @param pageContext
	 * @return
	 */
	public static String getIdRequest(PageContext pageContext) {
		return getIdRequest(pageContext, true);
	}

	/**
	 * Funzione che estrae l'id iniziale nel request.
	 *
	 * @param pageContext
	 * @param addPagina   Flag per dire di aggiungere o meno l'id della pagina
	 * @return Id iniziale del request creato come:
	 *         <i>[SCHEMA].[MASCHERA].[PAGINA]</i>
	 *
	 */
	public static String getIdRequest(PageContext page, boolean addPagina) {
		StringBuffer buf = new StringBuffer("");
		String sezioni[] = new String[] { UtilityTags.REQUEST_SCHEMA, UtilityTags.REQUEST_ID_MASCHERA,
				UtilityTags.REQUEST_ID_PAGINA };

		for (int i = 0; i < (addPagina ? sezioni.length : 2); i++) {
			Object sez = page.getAttribute(sezioni[i], PageContext.REQUEST_SCOPE);
			if (sez == null || sez.toString().length() == 0) {
				if (i < 2)
					return null;
				return buf.toString();
			}
			if (i > 0)
				buf.append(".");
			buf.append(sez);
		}
		return buf.toString();

	}

	/**
	 * Testa l'iniezione di sql malevolo nel campo di input trovaAddWhere ricevuto
	 * dalle form e che viene attivamente utilizzato dalle tag libraries interne per
	 * costruire le query sulla base delle condizioni di filtro indicate.
	 *
	 * @param sqlCondition condizione sql ricevuta come parameter nella request
	 * @param digest       digest sha256 della condizione (verifica la non
	 *                     manipolazione da richiesta)
	 *
	 * @return true se il controllo viene bypassato oppure si considera la stringa
	 *         sql non pericolosa, false se individua un pattern potenzialmente
	 *         pericoloso e riconducibile a sql injection oppure il digest non
	 *         corrisponde alla condizione sql in input
	 */
	public static boolean isFreeFromSqlInjection(String sqlCondition, String digest) {
		boolean esito = true;
		String propSaltaControllo = ConfigManager.getValore("sqlInjection.disabilitaControllo");
		if (!"1".equals(propSaltaControllo) && StringUtils.isNotBlank(sqlCondition)) {
			// se la property di configurazione non e' esistente oppure non e' impostata a
			// 1, si effettuano tutti i controlli. Al primo controllo
			// fallito automaticamente tutti i successivi controlli vengono saltati

			if (esito) {
				// controllo del digest, che deve essere coerente con quanto si ricalcola sulla
				// condzione sql
				esito = UtilityTags.genSha256(sqlCondition).equals(digest);
			}

			sqlCondition = sqlCondition.toUpperCase();
			sqlCondition = StringUtils.replaceChars(sqlCondition, '\t', ' ');

			if (esito) {
				// controllo 1: End-of-line comment
				// l'uso del commento -- invalida il resto dello statement, mentre la prima
				// parte e' composta dai caratteri digitati dall'hacker
				esito = !StringUtils.contains(sqlCondition, "--");
			}
			if (esito) {
				// controllo 2: Piggybacked queries
				// tramite il carattere ';' di concatenazione statement vengono aggiunte
				// ulteriori query da eseguirsi dopo quella prevista dallo
				// sviluppatore
				esito = !StringUtils.contains(sqlCondition, ";");
			}
			if (esito) {
				// controllo 3: Union queries
				// tramite la UNION si concatena alla query legittima quella voluta dall'hacker
				esito = !StringUtils.contains(sqlCondition, " UNION ");
			}
			if (esito) {
				// controllo 4: Tautology
				// iniezione di codice della forma 'OR 1==1' per rendere sempre vere delle
				// condizioni e bypassare dei controlli
				String[] elementi = StringUtils.split(sqlCondition, ' ');
				int i = 0;
				while (i < elementi.length && esito) {
					// ciclo se ci sono ancora elementi da controllare e non ho ancora trovato una
					// tautologia
					if ("=".equals(elementi[i]) && (i - 1 >= 0) && (i + 1 < elementi.length)) {
						// se i due termini dell'uguaglianza (prima e dopo dell'uguale) sono identici ho
						// una tautologia
						if (elementi[i - 1].equals(elementi[i + 1])) {
							esito = false;
						}
					} else if (StringUtils.contains(elementi[i], '=')) {
						// se e' una espressione di uguaglianza senza spazi controllo i due elementi a
						// sx e dx dell'operatore
						String operandoSx = StringUtils.substringBefore(elementi[i], "=");
						String operandoDx = StringUtils.substringAfter(elementi[i], "=");
						if (operandoSx.equals(operandoDx)) {
							esito = false;
						}
					}
					i++;
				}
			}
			if (esito) {
				// controllo 5: si bloccano comandi contenenti la lettura della versione,
				// potrebbero essere usati per comprendere la versione esatta
				// del DBMS sottostante per attaccarlo successivamente
				esito = esito && !StringUtils.contains(sqlCondition, "V$VERSION")
						&& !StringUtils.contains(sqlCondition, "@@VERSION")
						&& !StringUtils.contains(sqlCondition, "VERSION()");
			}

		}

		return esito;
	}

	/**
	 * Genera il digest sha256 di una stringa.
	 *
	 * @param value stringa in input
	 * @return sha256 della stringa in input ("" se in input il parametro vale null)
	 */
	public static String genSha256(String value) {
		String result = "";
		if (value != null) {
			result = DigestUtils.sha256Hex(value);
		}
		return result;
	}

	/**
	 * Costruisce in sessione la hash per la memorizzazione delle informazioni
	 * necessarie alla costruzione delle query SQL nelle pagine a lista. I dati
	 * memorizzabili nella hash, una volta presenti come campi hidden nelle form,
	 * sono stati spostati in sessione al fine di non consentirne la manipolazione
	 * dall'esterno agendo sul valore dei campi stessi prima del POST di una
	 * richiesta.
	 *
	 * @param session    sessione http
	 * @param sessionKey identificativo oggetto di sessione, da concatenare al
	 *                   prefisso che identifica gli oggetti memorizzati per questo
	 *                   scopo
	 * @param popupLevel livello di popup (0=pagina principale, 1=popup di primo
	 *                   livello, 2=popup di secondo livello, ...)
	 */
	public static void createHashAttributeForSqlBuild(HttpSession session, String sessionKey, int popupLevel) {
		String key = UtilityTags.SESSION_PENDICE_DEF_TROVA + sessionKey + "-" + popupLevel;
		session.removeAttribute(key);

		HashMap<String, String> defTrovaSession = new HashMap<String, String>();
		// si costruisce la chiave con un prefisso, l'identificativo ricevuto come
		// parametro, ed il livello di popup in modo da non sporcare gli
		// oggetti parcheggiati per la pagina principale rispetto ad eventuali popup di
		// selezione da archivio
		session.setAttribute(key, defTrovaSession);
		UtilityHistory utilityHistory = getUtilityHistory(session);
		HistoryItem peek = utilityHistory.peek(popupLevel);
		if (peek != null)
			peek.addCreatedDeftrova(sessionKey);
	}
	
	/**
	 * Quando necessario, salva una mappa "deftrova", descritta dall'entit� e dal numero popup, negli attributi della request.
	 * Utilizzato principalmente quando c'� la necessit� o l'obbligo di eliminare tale mappa dalla sessione. Se in sessione
	 * non � presente la mappa cercata, il programma continua con il suo normale funzionamento.
	 * 
	 * @param request
	 * 				Request HTTP della servlet
	 * @param sessionKey
	 * 				Chiave della mappa in sessione (entit�)
	 * @param popupLevel
	 * 				Livello di pop-up
	 */
	public static void saveHashAttributeForSqlBuild(final HttpServletRequest request, final String sessionKey, final int popupLevel) {
		final HttpSession session = request.getSession();
		final String key = UtilityTags.SESSION_PENDICE_DEF_TROVA + sessionKey + "-" + popupLevel;
		
		
		@SuppressWarnings("unchecked")
		final Map<String, String> defTrovaToSave = (Map<String, String>) session.getAttribute(key);
		if (defTrovaToSave != null) {
			request.setAttribute(key + "_SAVED", defTrovaToSave);
		} else {
			if (logger.isDebugEnabled()) logger.warn("Non � stato trovato un deftrova per l'entit�" + sessionKey);
		}
	}
	
	/**
	 * Ripristina una mappa "deftrova", precedentemente salvata tra gli attributi della request, nella sessione.
	 * Se non la mappa non � presente nella request, il programma continua con il suo normale funzionamento.
	 * 
	 * @param request
	 * 				Request HTTP della servlet
	 * @param sessionKey
	 * 				Chiave della mappa in sessione (entit�)
	 * @param popupLevel
	 * 				Livello di popup
	 */
	public static void restoreHashAttributeForSqlBuild(final HttpServletRequest request, final String sessionKey, final int popupLevel) {
		final HttpSession session = request.getSession();
		final String key = UtilityTags.SESSION_PENDICE_DEF_TROVA + sessionKey + "-" + popupLevel;
		
		@SuppressWarnings("unchecked")
		final Map<String, String> savedDeftrova = (Map<String, String>) request.getAttribute(key + "_SAVED");
		if (request.getAttribute(key + "_SAVED") != null) {
			
			session.setAttribute(key, savedDeftrova);
			request.removeAttribute(key + "_SAVED");
		} else {
			if (logger.isDebugEnabled()) logger.warn("Non � stato trovato un deftrova per l'entit�" + sessionKey);
		}
	}

	/**
	 * Rimuove una mappa "deftrova", descritta dall'entit� interrogata e dal livello di popup, dalla sessione.
	 * 
	 * @param session
	 * 				Sessione HTTP
	 * @param sessionKey
	 * 				Chiave della mappa in sessione (entit�)
	 * @param popupLevel
	 * 				Livello di popup
	 */
	public static void deleteHashAttributesForSqlBuild(HttpSession session, String sessionKey, int popupLevel) {
		session.removeAttribute(SESSION_PENDICE_DEF_TROVA + sessionKey + "-" + popupLevel);
	}
	
	/**
	 * Rimuove un entry da una mappa "deftrova", descritta dall'entit� interrogata e dal livello di popup, in sessione
	 * 
	 * @param session
	 * 				Sessione HTTP
	 * @param sessionKey
	 * 				Chiave della mappa in sessione (entit�)
	 * @param popupLevel
	 * 				Livello di popup
	 * @param attributeName
	 * 				Nome dell'attributo da rimuovere
	 */
	public static void removeAttributeForSqlBuild(final HttpSession session, final String sessionKey, final int popupLevel, 
			final String attributeName) {
		final String key = UtilityTags.SESSION_PENDICE_DEF_TROVA + sessionKey + "-" + popupLevel;
		
		@SuppressWarnings("unchecked")
		final Map<String, String> deftrova = (Map<String, String>) session.getAttribute(key);
		// Elimino l'attributo solo se trovo la mappa. 
		// TODO verificare se necessario notificare la presenza di tale mappa nella sessione tramite il logger.
		if (deftrova != null) {
			deftrova.remove(attributeName);
		}
	}

	/**
	 * Indica se � gi� stato creato o meno un attributo hash in sessione.
	 * 
	 * @param session    Sessione HTTP
	 * @param sessionKey Identificativo oggetto di sessione, da concatenare al
	 *                   prefisso che indica gli oggetti memorizzati per questo
	 *                   scopo
	 * @param popupLevel Livello di popup (0 = pagina principale, 1 = popup di primo
	 *                   livello, 2 = popup di secondo livello, ...)
	 * 
	 * @return True se l'hash � gi� stato creato
	 */
	public static boolean existsHashAttributeForSqlBuild(HttpSession session, String sessionKey, int popupLevel) {
		final String key = UtilityTags.SESSION_PENDICE_DEF_TROVA + sessionKey + "-" + popupLevel;

		return session.getAttribute(key) != null;
	}

	/**
	 * Costruisce in sessione una hash temporanea per la memorizzazione di alcune
	 * condizioni where che hanno la necessit� di essere passate tra liste/archivi.
	 * 
	 * @param session    Sessione HTTP
	 * @param sessionKey Chiave di sessione
	 */
	public static void createTempHashAttributeForSqlBuild(HttpSession session, String sessionKey) {
		HashMap<String, String> tempDefTrova = new HashMap<String, String>();
		String key = UtilityTags.TEMP_DEF_TROVA + sessionKey;
		session.setAttribute(key, tempDefTrova);
	}

	/**
	 * Inserisce nella hash definita in sessione i dati necessari per la corretta
	 * costruzione della query SQL. Questi dati, una volta presenti come campi
	 * hidden nelle form, sono stati spostati in sessione al fine di non consentirne
	 * la manipolazione dall'esterno agendo sul valore dei campi stessi prima del
	 * POST di una richiesta.<br/>
	 *
	 * @param session       sessione http
	 * @param sessionKey    identificativo oggetto di sessione, da concatenare al
	 *                      prefisso che identifica gli oggetti memorizzati per
	 *                      questo scopo
	 * @param popupLevel    livello di popup (0=pagina principale, 1=popup di primo
	 *                      livello, 2=popup di secondo livello, ...)
	 * @param attributeName nome attributo
	 * @param value         valore dell'attributo; se null si ottimizza la gestione
	 *                      senza inserirlo nella hash
	 * @param force			forza la riscrittura del parametro se gi� presente
	 *
	 * @return valore dell'attributo, null se non presente nel contenitore in
	 *         sessione oppure se non &egrave; presente il contenitore in sessione
	 */
	public static void putAttributeForSqlBuild(HttpSession session, String sessionKey, int popupLevel,
			String attributeName, String value) {
		// si costruisce la chiave con un prefisso, l'identificativo ricevuto come
		// parametro, ed il livello di popup in modo da non sporcare gli
		// oggetti parcheggiati per la pagina principale rispetto ad eventuali popup di
		// selezione da archivio
		String key = UtilityTags.SESSION_PENDICE_DEF_TROVA + sessionKey + "-" + popupLevel;
		if (session.getAttribute(key) == null) {
			createHashAttributeForSqlBuild(session, sessionKey, popupLevel);
		}
		@SuppressWarnings("unchecked")
		HashMap<String, String> defTrovaSession = (HashMap<String, String>) session.getAttribute(key);
		if (StringUtils.isNotBlank(value)) {
			defTrovaSession.put(attributeName, value);
		}
	}
	
	public static void putAttributeForSqlBuild(HttpSession session, String sessionKey, int popupLevel,
			String attributeName, String value, boolean force) {
		// si costruisce la chiave con un prefisso, l'identificativo ricevuto come
		// parametro, ed il livello di popup in modo da non sporcare gli
		// oggetti parcheggiati per la pagina principale rispetto ad eventuali popup di
		// selezione da archivio
		String key = UtilityTags.SESSION_PENDICE_DEF_TROVA + sessionKey + "-" + popupLevel;
		if (session.getAttribute(key) == null) {
			createHashAttributeForSqlBuild(session, sessionKey, popupLevel);
		}
		@SuppressWarnings("unchecked")
		HashMap<String, String> defTrovaSession = (HashMap<String, String>) session.getAttribute(key);
		if (StringUtils.isNotBlank(value) || force) {
			defTrovaSession.put(attributeName, value);
		}
	}

	/**
	 * Inserisce nella hash temporanea i dati necessari per la corretta costruzione
	 * della query SQL.
	 * 
	 * @param session       Sessione HTTP
	 * @param sessionKey    Chiave di sessione
	 * @param attributeName Nome dell'attributo
	 * @param value         Valore dell'attributo
	 */
	public static void putTempAttributeForSqlBuild(HttpSession session, String sessionKey, String attributeName,
			String value) {
		String key = UtilityTags.TEMP_DEF_TROVA + sessionKey;
		@SuppressWarnings("unchecked")
		HashMap<String, String> tempDefTrova = (HashMap<String, String>) session.getAttribute(key);
		if (StringUtils.isNotBlank(value)) {
			tempDefTrova.put(attributeName, value);
		}
	}

	/**
	 * Estrae dalla hash definita in sessione i dati necessari per la corretta
	 * costruzione della query SQL. Questi dati, una volta presenti come campi
	 * hidden nelle form, sono stati spostati in sessione al fine di non consentirne
	 * la manipolazione dall'esterno agendo sul valore dei campi stessi prima del
	 * POST di una richiesta.
	 *
	 * @param session       sessione http
	 * @param sessionKey    identificativo oggetto di sessione, da concatenare al
	 *                      prefisso che identifica gli oggetti memorizzati per
	 *                      questo scopo
	 * @param popupLevel    livello di popup (0=pagina principale, 1=popup di primo
	 *                      livello, 2=popup di secondo livello, ...)
	 * @param attributeName nome attributo
	 *
	 * @return valore dell'attributo, null se non presente nel contenitore in
	 *         sessione oppure se non &egrave; presente il contenitore in sessione
	 */
	public static String getAttributeForSqlBuild(HttpSession session, String sessionKey, int popupLevel,
			String attributeName) {
		String value = null;
		String key = UtilityTags.SESSION_PENDICE_DEF_TROVA + sessionKey + "-" + popupLevel;
		@SuppressWarnings("unchecked")
		HashMap<String, String> defTrovaSession = (HashMap<String, String>) session.getAttribute(key);
		if (defTrovaSession != null) {
			// si potrebbe anche arrivare in una lista in cui non si � passati per form di
			// ricerca e pertanto non ci sono oggetti in sessione
			value = defTrovaSession.get(attributeName);
		}

		return value;
	}

	/**
	 * Estrae dalla hash temporanea il valore di un attributo specificato da
	 * 'attributeName' e lo rimuove. Se, una volta rimosso l'elemento, l'hash �
	 * vuota, la elimina dalla sessione.
	 * 
	 * @param session       Sessione HTTP
	 * @param sessionKey    Chiave della sessione
	 * @param attributeName Nome dell'attributo
	 * 
	 * @return Valore dell'attributo
	 */
	public static String popTempAttributeForSqlBuild(HttpSession session, String sessionKey, String attributeName) {
		String value = null;
		String key = UtilityTags.TEMP_DEF_TROVA + sessionKey;
		@SuppressWarnings("unchecked")
		HashMap<String, String> tempDefTrova = (HashMap<String, String>) session.getAttribute(key);
		if (tempDefTrova != null) {
			value = tempDefTrova.get(attributeName);
			tempDefTrova.remove(attributeName);
			if (tempDefTrova.size() == 0) {
				session.removeAttribute(key);
			}
		}

		return value;
	}

}
