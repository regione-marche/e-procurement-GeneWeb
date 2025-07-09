package it.eldasoft.gene.tags.decorators.scheda;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

import it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction;
import it.eldasoft.utils.metadata.domain.LegameTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.properties.ConfigManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.sql.sqlparser.JdbcColumn;
import it.eldasoft.gene.db.sql.sqlparser.JdbcExpression;
import it.eldasoft.gene.db.sql.sqlparser.JdbcExpressionWhere;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.db.sql.sqlparser.JdbcSqlSelect;
import it.eldasoft.gene.db.sql.sqlparser.JdbcTable;
import it.eldasoft.gene.db.sql.sqlparser.JdbcUtils;
import it.eldasoft.gene.db.sql.sqlparser.JdbcWhere;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.TagAttributes;
import it.eldasoft.gene.tags.decorators.archivi.ArchivioRequest;
import it.eldasoft.gene.tags.decorators.archivi.ArchivioTagImpl;
import it.eldasoft.gene.tags.decorators.campi.UtilityDefinizioneCampo;
import it.eldasoft.gene.tags.decorators.scheda.CampiNonDiEntita.EntitaLocal;
import it.eldasoft.gene.tags.utils.KeyParamValidator;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.SchedaAction;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.profiles.FiltroLivelloUtente;
import it.eldasoft.utils.profiles.cache.DizionarioLivelli;
import it.eldasoft.utils.profiles.domain.Livello;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore del tag per la scheda
 *
 * @author marco.franceschin
 */
public class FormSchedaTag extends BodyTagSupportGene implements IFormScheda {

  /**
   * UID
   */
  private static final long serialVersionUID   = 5827229077707951417L;

  private static Logger     logger             = Logger.getLogger(FormSchedaTag.class);

  private StringBuffer      body               = null;
  private String            firstIterationPage = null;

  private FormSchedaAttributes getAttributes() {
    Object obj = this.getAttributeManager();
    return (FormSchedaAttributes) obj;
  }

  private void setNull() {
    getAttributes().setCampoAttivo(0);
    getAttributes().setTable(null);
  }

  public FormSchedaTag() {
    super("formScheda");
  }

  protected FormSchedaTag(String tipo) {
    super(tipo);
  }

  /** Aggiungo il campo */
  public void addCampo(CampoSchedaTagImpl campo) {
    getAttributes().getElencoCampi().add(campo);
  }

  @Override
  public int doStartTag() throws JspException {
    int ret = super.doStartTag();
    String lsModo = UtilityTags.getParametro(this.pageContext,
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO);
    if (lsModo == null) lsModo = UtilityTags.SCHEDA_MODO_VISUALIZZA;
    // imposto una variabile JS che contenga la modalità di apertura scheda
    this.getJavascript().println("var modoAperturaScheda = '" + lsModo + "';");
    // Setto il modo di apertura nel request
    this.pageContext.setAttribute(UtilityTags.REQUEST_VAR_MODO_APERTURA_SCHEDA,
        lsModo, PageContext.REQUEST_SCOPE);
    this.pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO,
        lsModo, PageContext.REQUEST_SCOPE);
    this.pageContext.setAttribute(UtilityTags.REQUEST_VAR_TIPO_PAGINA,
        UtilityTags.PAGINA_SCHEDA, PageContext.REQUEST_SCOPE);

    this.setNull();

    this.getJavascript().println(
        this.getFormName() + "=new FormObj(document." + this.getId() + ");");
    // Estraggo la tabella
    if (this.getEntita() != null && this.getEntita().length() > 0) {
      getAttributes().setTable(
          DizionarioTabelle.getInstance().getDaNomeTabella(this.getEntita()));
      // Elimino l'errore se non esiste la tabella nei metadati
      /*
       * if (getAttributes().getTable() == null) throw new JspException("La
       * tabella " + this.getEntita() + " non esiste nei metadati !");
       */
    }
    // Se siamo in situazione di modifica allora setto nel request
    // il blocco della navigazione
    if (UtilityTags.isInModifica(this.pageContext.getRequest()))
      this.pageContext.getRequest().setAttribute(
          CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);
    this.body = new StringBuffer();
    if (this.getAttributes().getTableClass() == null) {
      if (this.getParent(PaginaPagineTag.class) == null)
        this.getAttributes().setTableClass("dettaglio-notab");
      else
        this.getAttributes().setTableClass("dettaglio-tab");
    }

    // si esegue la chiamata all'eventuale plugin per il popolamento di dati
    // nel request da usare nella pagina
    AbstractGestorePreload plugin = this.getPluginInstance();
    if (plugin != null)
      plugin.doBeforeBodyProcessing(this.pageContext, lsModo);

    return ret;
  }

  @SuppressWarnings({"unused", "unchecked" })
  @Override
  public int doAfterBody() throws JspException {
    boolean modifica = UtilityTags.isInModifica(this.pageContext.getRequest());
    
    // Recupero le chiavi e le valido
    final String key = UtilityTags.getParametro(this.pageContext, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
    final String keyParent = UtilityTags.getParametro(this.pageContext, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT);

    SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.pageContext, SqlManager.class);
    GeneManager gene = (GeneManager) UtilitySpring.getBean(
            "geneManager", this.pageContext, GeneManager.class);

    // Set in sessione dell'attributo
    // CostantiGenerali.ENTITA_PRINCIPALE_MODIFICABILE:
    // "1" -> entita' modificabile, "0" entita' non modificabile.
    if (!modifica && this.isFirstIteration()) {
    	// Valido le eventuali chiavi ricevute nella request
    	if (StringUtils.isNotBlank(key))
    		KeyParamValidator.validate(key);
    	if (StringUtils.isNotBlank(keyParent))
    		KeyParamValidator.validate(keyParent);
    	
      // per prima cosa si rimuove l'attributo in sessione relativo ad un
      // precedente dettaglio
      if (this.pageContext.getSession().getAttribute(
          CostantiGenerali.ENTITA_PRINCIPALE_MODIFICABILE) != null)
        this.pageContext.getSession().removeAttribute(
            CostantiGenerali.ENTITA_PRINCIPALE_MODIFICABILE);
      // dopodichè si controlla se per caso nella pagina è stato settato un
      // attributo nel request "entitaPrincipaleModificabile": in tal caso si
      // utilizza tale valore, altrimenti se non esiste si determina
      // l'editabilità in base alla gestione dei livelli utente/permessi
      boolean entitaModificabile = false;
      // WE412: si legge il dato eventualmente anche come parametro del request
      String reqEntitaModificabile = UtilityTags.getParametro(this.pageContext,
          CostantiGenerali.ENTITA_PRINCIPALE_MODIFICABILE);
      // String reqEntitaModificabile = (String) this.pageContext.getAttribute(
      // CostantiGenerali.ENTITA_PRINCIPALE_MODIFICABILE,
      // PageContext.REQUEST_SCOPE);
      if (reqEntitaModificabile != null) {
        // si usa il dato settato nel request, ritenuto prioritario se definito
        if ("1".equals(reqEntitaModificabile)) entitaModificabile = true;
      } else {
        // si usa la gestione dei livelli utente/permessi
        if (this.isEntitaModificabile()) entitaModificabile = true;
      }
      // settata la variabile, si setta anche l'attributo in sessione
      if (entitaModificabile)
        this.pageContext.getSession().setAttribute(
            CostantiGenerali.ENTITA_PRINCIPALE_MODIFICABILE, "1");
      else
        this.pageContext.getSession().setAttribute(
            CostantiGenerali.ENTITA_PRINCIPALE_MODIFICABILE, "0");
    }

    // Eseguo la lettura solo se è la prima iterazione
    if (this.isFirstIteration()) {

      if (this.getBodyContent() != null)
        this.firstIterationPage = this.getBodyContent().getString();
      // Estraggo il parametro per vedere se è aperto in modifica o
      // inserimento.
      String lsModo = UtilityTags.getParametro(this.pageContext,
          UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO);
      if (lsModo == null) lsModo = UtilityTags.SCHEDA_MODO_VISUALIZZA;

      // qui vanno aggiunti i controlli bloccanti per non accedere al dettaglio se non autorizzati.
      // i controlli devono essere:
      // 1. se la uffint risulta impostata in sessione deve corrispondere all'eventuale campo presente nella entita principale
      // 2. sulle autorizzazioni dell'utente sull'entita

      if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(lsModo) && (this.getEntita() != null && !this.getEntita().isEmpty())) {
        String uffintAttivo = (String) this.pageContext.getSession().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
        // 1. verifica sulla uffint
        if (uffintAttivo != null && key != null) {
          // controllo anche key perche' si possono creare ad esempio le popup di messaggi in cui si usa un formScheda
          // ma non si passa key o keyparent ma si passa chiaveRiga e si usa per valorizzare dei campi ad hoc in pagina
          String chiaveEntitaCollegataUffint = FormSchedaTag.trovaChiaveEntitaCollegataUffint(key, sql, gene);

          // se il campo di collegamento con UFFINT e' stato trovato, allora eseguo una query sull'entita' che la contiene filtrando per il campo key
          if (chiaveEntitaCollegataUffint != null) {
            String[] datiDaElaborare = chiaveEntitaCollegataUffint.split("#");
            JdbcWhere where = new JdbcWhere();
            UtilityTags.jdbcAddKeyWhere(where, datiDaElaborare[2]);
            String query = "SELECT " + datiDaElaborare[1] + " FROM " + datiDaElaborare[0] + where.toString();

            String uffintRecord = null;
            try {
              uffintRecord = (String) sql.getObject(query, SqlManager.getObjectFromPram(where.getParametri()));
            } catch (SQLException e) {
              throw new JspException("Errore durante l'estrazione del riferimento ad ufficio intestatario, key " + key + ": "
                      + e.getMessage(), e);
            }

            // si sta tentando di forzare l'accesso ad un record non relativo all'uffint? this.getPageContext().getRequest()).getAttribute("forzaRedirect")
            if (uffintRecord != null && !uffintRecord.equals(uffintAttivo)) {
              logger.error("Bloccato accesso all'entita' " + this.getEntita() + " con chiave " + key + " perche' non e' relativo all'ufficio intestatario attivo (" + uffintAttivo + ") ma e' relativo all'ufficio intestatario " + uffintRecord);
              this.getPageContext().setAttribute("forzaRedirect", Boolean.TRUE, PageContext.REQUEST_SCOPE);
              // svuoto l'history
              UtilityTags.getUtilityHistory(((HttpServletRequest)
                      this.getPageContext().getRequest()).getSession()).clear(UtilityStruts.getNumeroPopUp(this.getPageContext().getRequest()), this.getPageContext().getSession());
              return SKIP_BODY;
            }
          }
        }

        // 2. Verifica delle autorizzazioni dell'utente sull'entita'
        ProfiloUtente profiloUtente = (ProfiloUtente) this.pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        if (key != null) {
          String chiaveEntitaControllataConPermessi = FormSchedaTag.trovaChiaveEntitaControllataConPermessi(key, profiloUtente, sql, gene);
          // se il campo di collegamento con UFFINT e' stato trovato, allora eseguo una query sull'entita' che la contiene filtrando per il campo key
          if (chiaveEntitaControllataConPermessi != null) {
            String[] datiDaElaborare = chiaveEntitaControllataConPermessi.split("#");
            JdbcWhere where = new JdbcWhere();
            UtilityTags.jdbcAddKeyWhere(where, datiDaElaborare[2]);
            String query = "SELECT COUNT(*) FROM "
                    + datiDaElaborare[0]
                    + where.toString()
                    + " AND "
                    + datiDaElaborare[1];
            try {
              long numOccorrenze = (Long) sql.getObject(query, SqlManager.getObjectFromPram(where.getParametri()));
              // si sta tentando di forzare l'accesso ad un record non visibile all'utente
              if (numOccorrenze == 0) {
                logger.error("Bloccato accesso all'entita' " + this.getEntita() + " con chiave " + key + " perche' non e' visibile all'utente " + profiloUtente.getId());
                this.getPageContext().setAttribute("forzaRedirect", Boolean.TRUE, PageContext.REQUEST_SCOPE);
                // svuoto l'history
                UtilityTags.getUtilityHistory(((HttpServletRequest)
                        this.getPageContext().getRequest()).getSession()).clear(UtilityStruts.getNumeroPopUp(this.getPageContext().getRequest()), this.getPageContext().getSession());
                return SKIP_BODY;
              }

            } catch (SQLException e) {
              throw new JspException("Errore durante la verifica visibilita' di un record,  key " + key + ": "
                      + e.getMessage(), e);
            }
          }
        }
      }

      // Se non si ha l'entita allora ? stato gestito in modo generale
      if (this.getEntita() != null && this.getEntita().length() == 0) {

        // Aggiungo i dati delle riga
        this.addDatiRiga();
      } else if (!lsModo.equals(UtilityTags.SCHEDA_MODO_INSERIMENTO)) {

        // Eseguo la lettura di tutti i dati che mi interessano
        JdbcSqlSelect select = new JdbcSqlSelect();
        JdbcWhere filtroRiga = null;
        // Setto la tabella nel form
        select.getFrom().append(new JdbcTable(this.getEntita()));
        // Se è in modalità di modifica calcolo la where per le chiavi

        // {MF160307} Se esiste una where allora imposto tale where come where
        // per l'estrazione dell'occorrenza
        filtroRiga = new JdbcWhere();
        // Se è impostato un filtro allora lo imposto sulla select
        if (this.getAttributes().getWhere() != null) {
          UtilityTags.addWhereSelezionata(pageContext, filtroRiga,
              this.getAttributes().getWhere());
        } else
          UtilityTags.jdbcAddKeyWhere(filtroRiga, key);

        int nCampo = 0;
        // Come prima cosa aggiungo tutti i campi chiave
        for (int i = 0; i < this.getAttributes().getElencoCampi().size(); i++) {
          CampoSchedaTagImpl campo = (CampoSchedaTagImpl) this.getAttributes().getElencoCampi().get(
              i);
          if (campo.isCampoDB()) {
            campo.setNumeroCampo(nCampo);
            // Verifico che il campo sia appartenente alla tabella
            // principale
            if (campo.isComputed()
                || (campo.getEntita() != null && campo.getEntita().equalsIgnoreCase(
                    this.getEntita()))) {
              // Si tratta di un campo della medesima tabella
              if ("TIMESTAMP".equals(campo.getDominio())) {
                // SS 07/07/2015: introdotta la gestione del dominio TIMESTAMP solo per la visualizzazione del valore formattandolo a
                // stringa
                select.getSelect().append(
                    new JdbcExpression(new JdbcColumn(null,
                        sql.getDBFunction("datetimetostring",new String[] {campo.getNomeFisico()}))));
              } else {
                select.getSelect().append(
                    UtilityTags.getJdbcExpression(campo));
              }

              nCampo++;
              if (campo.isComputed()) {
                if (campo.getFrom() != null && campo.getFrom().length() > 0)
                  select.getFrom().append(new JdbcTable(campo.getFrom()));
                if (campo.getWhere() != null && campo.getWhere().length() > 0) {
                  if (select.getWhere() != null)
                      select.getWhere().append(new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
                  select.getWhere().append(campo.getWhere());
                }
              }
            } else {
              EntitaLocal entit = this.getAttributes().getEntitaDiverse().getEntitaEsterna(
                  campo.getEntita());
              if (entit == null) {
                // Se non esiste neancora l'entità collegata
                // allora
                // la creo
                entit = this.getAttributes().getEntitaDiverse().addEntita(
                    campo.getEntita());
                entit.select.getWhere().append(filtroRiga);
                if (campo.getWhere() != null && campo.getWhere().length() > 0) {
                  entit.select.getWhere().append(
                      new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
                  entit.select.getWhere().append(new JdbcExpressionWhere(true));
                  entit.select.getWhere().append(
                      new JdbcExpression(campo.getWhere(),
                          new JdbcParametro[] {}));
                  entit.select.getWhere().append(new JdbcExpressionWhere(false));
                }
                entit.select.getFrom().append(new JdbcTable(this.getEntita()));
                entit.select.getFrom().append(new JdbcTable(campo.getEntita()));
                if (campo.getFrom() != null && campo.getFrom().length() > 0) {
                  String lsTmp = campo.getFrom();
                  do {
                    int pos = lsTmp.indexOf(',');
                    if (pos >= 0) {
                      entit.select.getFrom().append(
                          new JdbcTable(lsTmp.substring(0, pos)));
                      lsTmp = lsTmp.substring(pos + 1);
                    } else {
                      entit.select.getFrom().append(new JdbcTable(lsTmp));
                      lsTmp = "";
                    }
                  } while (lsTmp.indexOf(',') >= 0);
                }
              }
              // Appendo il campo alla select
              campo.setNumeroCampo(entit.select.getSelect().getEspressioni().size());
              if ("TIMESTAMP".equals(campo.getDominio())) {
                // SS 07/07/2015: introdotta la gestione del dominio TIMESTAMP solo per la visualizzazione del valore formattandolo a
                // stringa
                entit.select.getSelect().append(
                    new JdbcExpression(new JdbcColumn(null,
                        sql.getDBFunction("datetimetostring",new String[] {campo.getNomeFisico()}))));
              } else {
                entit.select.getSelect().append(
                    UtilityTags.getJdbcExpression(campo));
              }
            }

          }
        }
        // {MF160307} Aggiungo la where per l'estrazione della chiave
    	if (select.getWhere() != null)
    		select.getWhere().append(new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
        select.getWhere().append(filtroRiga);

        try {
          if (lsModo.equals(UtilityTags.SCHEDA_MODO_VISUALIZZA)) {
            // si estraggono dei conteggi per la visualizzazione nei link
            // corrispondenti
            long numNoteAvvisi = gene.contaOccorrenzeNoteAvvisi(
                this.getEntita(), select.getWhere().toString(false),
                SqlManager.getObjectFromPram(select.getParemetri()));
            if (numNoteAvvisi > 0)
              this.pageContext.setAttribute("numRecordNoteAvvisi", new Long(
                  numNoteAvvisi), PageContext.REQUEST_SCOPE);
            long numDocAssociati = gene.contaOccorrenzeOggettiAssociati(
                this.getEntita(), select.getWhere().toString(false),
                SqlManager.getObjectFromPram(select.getParemetri()));
            if (numDocAssociati > 0)
              this.pageContext.setAttribute("numRecordDocAssociati", new Long(
                  numDocAssociati), PageContext.REQUEST_SCOPE);
          }

        // A questo punto eseguo la vera e propria selezione sul
        // database
        // logger.debug("SQL:" + select.toString());

        if (!isErroreInUpdate()) {
          // Eseguo la lattura di tutte le entità non principali
          this.getAttributes().getEntitaDiverse().leggiTutti(sql);
            Vector valori = sql.getVector(select);
            if (valori == null) {
              // si sta tentando di forzare l'accesso ad un record non presente in db
              logger.error("Bloccato accesso all'entita' " + this.getEntita() + " con chiave " + key + " perche' non e' presente in db");
              this.getPageContext().setAttribute("forzaRedirect", Boolean.TRUE, PageContext.REQUEST_SCOPE);
              // svuoto l'history
              UtilityTags.getUtilityHistory(((HttpServletRequest)
                  this.getPageContext().getRequest()).getSession()).clear(UtilityStruts.getNumeroPopUp(this.getPageContext().getRequest()), this.getPageContext().getSession());
              return SKIP_BODY;
            }
            for (int i = 0; i < this.getAttributes().getElencoCampi().size(); i++) {
              CampoSchedaTagImpl campo = (CampoSchedaTagImpl) this.getAttributes().getElencoCampi().get(
                  i);
              if (campo.isCampoDB()) {
                if (campo.getNumeroCampo() >= 0) {
                  if (campo.isComputed()
                      || campo.getEntita().equalsIgnoreCase(this.getEntita())) {
                    if (valori != null
                        && campo.getNumeroCampo() < valori.size())
                      campo.setValue(valori.get(campo.getNumeroCampo()).toString());
                  } else {
                    campo.setValue(getAttributes().getEntitaDiverse().getValue(
                        campo));
                  }
                }
              }
            }
            // Funzione che aggiunge i dati riga
            this.addDatiRiga();
        } else {
          // Se ci sono stati errori durante l'update ripristino i valori
          for (int i = 0; i < this.getAttributes().getElencoCampi().size(); i++) {
            CampoSchedaTagImpl campo = (CampoSchedaTagImpl) this.getAttributes().getElencoCampi().get(
                i);
            campo.setValue(UtilityTags.getParametro(this.pageContext,
                campo.getNome()));
            String definizione = UtilityStruts.getParametroString(
                (HttpServletRequest) this.pageContext.getRequest(),
                UtilityTags.DEFAULT_HIDDEN_INIZIO_DEFINIZIONE + campo.getNome());
            if (definizione != null)
              campo.setOriginalValue(UtilityDefinizioneCampo.getValue(definizione));
          }
          this.addDatiRiga();
        }
        } catch (Throwable t) {
          throw new JspException("Errore durante la selezione nella lista:\n"
              + t.getMessage(), t);
        }
      } else {
        // Siamo in situazione di inserimento
        // Setto tutti i valori di default
        boolean errore = isErroreInUpdate();
        for (int i = 0; i < this.getAttributes().getElencoCampi().size(); i++) {
          CampoSchedaTagImpl campo = (CampoSchedaTagImpl) this.getAttributes().getElencoCampi().get(
              i);
          if (!errore)
            campo.setValue(campo.getDefaultValue());
          else {
            campo.setValue(UtilityTags.getParametro(this.pageContext,
                campo.getNome()));
            String definizione = UtilityStruts.getParametroString(
                (HttpServletRequest) this.pageContext.getRequest(),
                UtilityTags.DEFAULT_HIDDEN_INIZIO_DEFINIZIONE + campo.getNome());
            if (definizione != null)
              campo.setOriginalValue(UtilityDefinizioneCampo.getValue(definizione));
          }

        }
        this.addDatiRiga();
      }

      AbstractGestorePreload plugin = this.getPluginInstance();
      if (plugin != null) plugin.doAfterFetch(this.pageContext, lsModo);

      this.setNCampo(0);
      super.doAfterBody();
      return EVAL_BODY_AGAIN;
    }

    String profiloApplicativo = (String) this.pageContext.getAttribute(CostantiGenerali.PROFILO_ATTIVO, PageContext.SESSION_SCOPE);

    StringBuffer buf = new StringBuffer("");
    StringBuffer elencoCampi = new StringBuffer("");
    buf.append("<form ");
    buf.append(UtilityTags.getHtmlAttrib("name", this.getId()));
    buf.append(UtilityTags.getHtmlAttrib("action", this.getContextPath()
        + "/Scheda.do"));
    buf.append(UtilityTags.getHtmlAttrib("method", "post"));
    // {MF23102006} Aggiunta dell'onSubmit della form
    buf.append(UtilityTags.getHtmlAttrib("onSubmit", "javascript:return local"
        + this.getId()
        + ".onsubmit();"));
    buf.append(">\n");
    // Aggiungo gli hidden di default
    buf.append(UtilityTags.getHtmlDefaultHidden(this.pageContext));
    // Aggiungo la chiave pel parent alla scheda
    buf.append(UtilityTags.getHtmlHideInput(
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
        keyParent));
    if (profiloApplicativo != null && StringUtils.isNotBlank(keyParent)) {
      // si aggiunge il keyParent acceduto tra quelli navigati nel profilo in uso
      HashMap<String, HashSet<String>> hashProfiliKeyParents = (HashMap<String, HashSet<String>>)this.pageContext.getAttribute(CostantiGenerali.PROFILI_KEY_PARENTS, PageContext.SESSION_SCOPE);
      hashProfiliKeyParents.get(profiloApplicativo).add(keyParent);
    }

    HashMap<String, HashSet<String>> hashProfiliKey = (HashMap<String, HashSet<String>>)this.pageContext.getAttribute(CostantiGenerali.PROFILI_KEYS, PageContext.SESSION_SCOPE);
    buf.append(UtilityTags.getHtmlHideInput("entita", this.getEntita()));
    buf.append(UtilityTags.getHtmlHideInput("metodo", "apri"));
    buf.append(UtilityTags.getHtmlHideInput(
        UtilityTags.DEFAULT_HIDDEN_NOME_GESTORE, this.getGestore()));
    // Aggiunta del parametro del modo
    buf.append(UtilityTags.getHtmlHideInputFromParam(this.pageContext,
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO));
    // L.G. 17/08/2009: se in modalita' inserimento, il campo key NON viene valorizzato
    if(UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(
        UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO)))
      buf.append(UtilityTags.getHtmlHideInput(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, ""));
    else
      buf.append(UtilityTags.getHtmlHideInputFromParam(this.pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA));

    if (profiloApplicativo != null && StringUtils.isNotBlank(key)) {
      // si aggiunge il key acceduto tra quelli navigati nel profilo in uso
      HashMap<String, HashSet<String>> hashProfiliKeys = (HashMap<String, HashSet<String>>) this.pageContext.getAttribute(CostantiGenerali.PROFILI_KEYS, PageContext.SESSION_SCOPE);
      hashProfiliKeys.get(profiloApplicativo).add(key);
    }

    if (this.firstIterationPage != null) buf.append(this.firstIterationPage);

    buf.append("<table class=\"");
    buf.append(this.getTableClass());
    buf.append("\">\n");
    getAttributes().setArchivio(false);
    for (int i = 0; i < this.getAttributes().getElencoCampi().size(); i++) {
      CampoSchedaTagImpl campo = (CampoSchedaTagImpl) this.getAttributes().getElencoCampi().get(
          i);
      buf.append(this.getHtmlStartArchivio(i));
      if (campo.isVisibile()) {
        buf.append(campo.toString(!modifica, this.getAttributes().isArchivio(),
            this.getPageContext()));
      }
      buf.append(this.getHtmlEndArchivio(i));
      // {MF231006} Aggiunta del flag con l'elenco dei campi
      if (i > 0) elencoCampi.append(";");
      elencoCampi.append(campo.getNome());
    }

    buf.append("</table>\n");
    // Appendo tutti i campi non visibili (per gestione con standard HTML i
    // campi vuoti non possono essere allinterno)
    for (int i = 0; i < this.getAttributes().getElencoCampi().size(); i++) {
      CampoSchedaTagImpl campo = (CampoSchedaTagImpl) this.getAttributes().getElencoCampi().get(
          i);
      if (!campo.isVisibile()) {
        buf.append(campo.toString(!modifica, this.getAttributes().isArchivio(),
            this.getPageContext()));
      }
    }

    buf.append(UtilityTags.getHtmlHideInput(
        UtilityTags.DEFAULT_HIDDEN_ELENCO_CAMPI, elencoCampi.toString()));
    // Aggiungo l'eventuale archivio di passaggio (se richiamato da un archivio
    ArchivioRequest archReq = ArchivioRequest.getArchivio(this.pageContext);
    if (archReq != null) {
      buf.append(archReq.toString());
    }

    buf.append("</form>");

    // Creazione dell'array Javascript contentente tutti i campi visibili (e
    // non nascosti via Javascript) obbligatori presenti nella scheda
    int indice = 0;
    this.getJavascript().println("var arrayCampiObbligatori = new Array();");
    for (int i = 0; i < this.getAttributes().getElencoCampi().size(); i++) {
      CampoSchedaTagImpl campo = (CampoSchedaTagImpl) this.getAttributes().getElencoCampi().get(
          i);
      if (campo.isVisibile() && (campo.isObbligatorio() ||
          UtilityTags.checkProtection(this.pageContext, "COLS", "MAN",
              campo.getNomeFisico(), false))) {
        this.getJavascript().println("arrayCampiObbligatori[" + indice + "] = " + "\"" + campo.getNome() + "\";");
        indice++;
      }
    }

    // A questo punto aggiungo tutte le form per gli archivi
    for (int i = 0; i < this.getAttributes().getArchivi().size(); i++) {
      ArchivioTagImpl impl = (ArchivioTagImpl) this.getAttributes().getArchivi().get(
          i);
      // Aggiungo l'archivio
      buf.append(impl.toString());
      this.getJavascript().println(impl.getCreateJsObject());
    }
    this.body.append(buf);
    super.doAfterBody();
    this.setNull();
    return EVAL_PAGE;
  }

  @Override
  public int doEndTag() throws JspException {
    BodyContent bodyCon = this.getBodyContent();
    if (bodyCon != null) {
      bodyCon.clearBody();
    }
    try {
      this.pageContext.getOut().write(body.toString());
    } catch (IOException e) {
      throw new JspException(e);
    }
    this.body = null;

    return super.doEndTag();
  }

  private void addDatiRiga() {
    // Oggetto nel request con i dati della riga
    HashMap datiRiga = new HashMap();
    for (int i = 0; i < this.getAttributes().getElencoCampi().size(); i++) {
      CampoSchedaTagImpl campo = (CampoSchedaTagImpl) this.getAttributes().getElencoCampi().get(
          i);
      if (campo.getNome() != null && campo.getNome().length() > 0) {
        datiRiga.put(campo.getNome(), campo.getValue() == null
            ? ""
            : campo.getValue());
      }
    }
    // Aggiungo nel request i dati della riga
    this.pageContext.setAttribute(UtilityTags.REQUEST_VAR_DATI_RIGA, datiRiga,
        PageContext.REQUEST_SCOPE);

  }

  private String getHtmlEndArchivio(int campo) {
    for (int i = 0; i < this.getAttributes().getArchivi().size(); i++) {
      String ret = ((ArchivioTagImpl) this.getAttributes().getArchivi().get(i)).getHTMLEndArchivio(campo);
      if (ret != null) {
        this.getAttributes().setArchivio(false);
        return ret;
      }
    }
    return "";
  }

  private String getHtmlStartArchivio(int campo) {
    for (int i = 0; i < this.getAttributes().getArchivi().size(); i++) {
      String ret = ((ArchivioTagImpl) this.getAttributes().getArchivi().get(i)).getHTMLStartArchivio(campo);
      if (ret != null) {
        this.getAttributes().setArchivio(true);
        return ret;
      }
    }
    return "";
  }

  /**
   * @return Returns the entita.
   */
  @Override
  public String getEntita() {
    return getAttributes().getEntita();
  }

  /**
   * @param entita
   *        The entita to set.
   */
  public void setEntita(String entita) {
    this.getAttributes().setEntita(entita);
  }

  public String getWhere() {
    return getAttributes().getWhere();
  }

  public void setWhere(String where) {
    this.getAttributes().setWhere(where);
  }

  /**
   * @return Returns the gestore.
   */
  public String getGestore() {
    return getAttributes().getGestore();
  }

  /**
   * @param gestore
   *        The gestore to set.
   */
  public void setGestore(String gestore) {
    this.getAttributes().setGestore(gestore);
  }

  /**
   * @return Ritorna plugin.
   */
  public String getPlugin() {
    return this.getAttributes().getPlugin();
  }

  /**
   * @param plugin
   *        plugin da settare internamente alla classe.
   */
  public void setPlugin(String plugin) {
    this.getAttributes().setPlugin(plugin);
  }

  /**
   * Funzione che dice se siamo alla prima iterazione
   *
   * @return true Se si è alla perima iterazione. false Non è la prima
   *         iterazione
   */
  @Override
  public boolean isFirstIteration() {
    return this.getNumIteration() == 0;
  }

  public CampoSchedaTagImpl getCampo(int numero) {
    if (numero < 0 || numero >= this.getAttributes().getElencoCampi().size())
      return null;
    return (CampoSchedaTagImpl) this.getAttributes().getElencoCampi().get(
        numero);
  }

  /**
   * Funzione che da il numero del campo
   *
   * @return
   */
  public int getNCampo() {
    int ret = this.getAttributes().getCampoAttivo();
    this.setNCampo(ret + 1);
    return ret;
  }

  @Override
  public String getFormName() {
    return "local" + this.getId();
  }

  @Override
  public void addArchivio(ArchivioTagImpl archivio) {
    this.getAttributes().getArchivi().add(archivio);
  }

  @Override
  public CampoSchedaTagImpl getDecoratore(CampoSchedaTag tag) {
    CampoSchedaTagImpl ret = null;
    if (tag.getNCampo() < 0) tag.setNCampo(this.getNCampo());
    if (getCampo(tag.getNCampo()) == null) {
      ret = new CampoSchedaTagImpl();
      ret.setJs(this.getJavascript());
      this.addCampo(ret);
      tag.setDecoratore(ret);
      return ret;
    }
    ret = getCampo(tag.getNCampo());
    tag.setDecoratore(ret);
    return ret;

  }

  /**
   * @param campo
   *        The nCampo to set.
   */
  public void setNCampo(int campo) {
    getAttributes().setCampoAttivo(campo);
    // logger.debug("SetCampo: " + campo + ":" + this.toString());
  }

  private boolean isErroreInUpdate() {
    Object ret = this.pageContext.getAttribute(SchedaAction.ERRORE_NEL_UPDATE,
        PageContext.REQUEST_SCOPE);
    if (ret instanceof Boolean) return ((Boolean) ret).booleanValue();
    return false;

  }

  @Override
  public TagAttributes newTagAttributes() {
    return new FormSchedaAttributes(this.getTipoVar());
  }

  @Override
  public boolean isGestisciProtezioni() {
    return this.getAttributes().isGestisciProtezioni();
  }

  public void setGestisciProtezioni(boolean gestisciProtezioni) {
    this.getAttributes().setGestisciProtezioni(gestisciProtezioni);
  }

  /**
   * @return the tableClass
   */
  public String getTableClass() {
    return this.getAttributes().getTableClass();
  }

  /**
   * @param tableClass
   *        the tableClass to set
   */
  public void setTableClass(String tableClass) {
    this.getAttributes().setTableClass(tableClass);
  }

  /**
   * Funzione che verifica se esiste o meno un campo nell'elenco
   *
   * @param nomeCampoFisico nome del campo
   * @return
   */
  @Override
  public boolean isCampo(String nomeCampoFisico) {
    for (int i = 0; i < getAttributes().getElencoCampi().size(); i++) {
      CampoSchedaTagImpl campo = (CampoSchedaTagImpl) getAttributes().getElencoCampi().get(
          i);
      if (campo.getNomeFisico() != null
          && campo.getNomeFisico().equalsIgnoreCase(nomeCampoFisico))
        return true;
    }
    return false;
  }

  /**
   * Metodo per determinare se l'entita' di partenza e' modificabile in base ai
   * diritti dell'utente sull'entita' stessa
   *
   * @return Ritorna true se l'entita' e' modificabile dall'utente, false
   *         altrimenti
   * @throws JspException
   */
  private boolean isEntitaModificabile() throws JspException {
    boolean result = true;

    DizionarioLivelli dizLivelli = DizionarioLivelli.getInstance();
    String keyParent = UtilityTags.getParametro(this.pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT);
    String key = UtilityTags.getParametro(this.pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);

    // Determino quale attributo utilizzare tra key e keyParent per determinare
    // se l'entita' e' modificabile dall'utente o meno
    String chiaveEntita = null;
    if (keyParent != null)
      chiaveEntita = keyParent;
    else
      chiaveEntita = key;

    if (chiaveEntita != null) {
      String entita = chiaveEntita.substring(0, chiaveEntita.indexOf("."));

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
          "sqlManager", this.pageContext,
          SqlManager.class);
      String[] valoriCampiChiave = chiaveEntita.split(";");

      try {

        if ("GARE".equals(entita)||"GAREAVVISI".equals(entita)) {
          // nel caso di GARE, nella G_PERMESSI devo filtrare per TORN
          chiaveEntita = "TORN.CODGAR=T:"
              + ((String) sqlManager.getObject(
                  "SELECT CODGAR1 FROM GARE WHERE NGARA=?",
                  new Object[] { valoriCampiChiave[0].split(":")[1] }));
          entita = chiaveEntita.substring(0, chiaveEntita.indexOf("."));
          valoriCampiChiave = chiaveEntita.split(";");
        }

        if (dizLivelli.isFiltroLivelloPresente(entita)) {
          ProfiloUtente profiloUtente = (ProfiloUtente) this.pageContext.getSession().getAttribute(
              CostantiGenerali.PROFILO_UTENTE_SESSIONE);
          FiltroLivelloUtente filtroUtente = profiloUtente.getFiltroLivelloUtente();

          Livello livello = dizLivelli.get(entita);
          filtroUtente.setLivello(livello);
          if (filtroUtente.getEntitaModificabile() != null)
            result = filtroUtente.getEntitaModificabile().booleanValue();
          else {
            Object[] obj = new Object[2];
            obj[0] = new Integer(profiloUtente.getId());
            obj[1] = valoriCampiChiave[0].split(":")[1];
            String strValoriCampiChiave = valoriCampiChiave[0];
            int equalIndex = strValoriCampiChiave.indexOf("=");
            if(equalIndex > 0){
              String typeValoriCampiChiave = strValoriCampiChiave.substring(equalIndex + 1,equalIndex + 2);
              if("N".equals(typeValoriCampiChiave)){
                obj[1] = new Long(valoriCampiChiave[0].split(":")[1]);
              }
            }

            // TODO: la valorizzazione dell'oggetto obj e' corretta per gli
            // applicativi PL-Web e Concessioni Stradali Web. Potrebbe non
            // funzionare per altri applicativi che si andranno a sviluppare e
            // che in particolare non useranno la tabella G_PERMESSI per la
            // gestioni dei diritti di un utente su una entita'
            Long numeroRecordEstratti = (Long) sqlManager.getObject(
                filtroUtente.getSqlPermessiUtente(), obj);
            if (numeroRecordEstratti.longValue() == 0) result = false;
          }
        }
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante l'estrazione del livello utente sull'oggetto principale della scheda:\n"
                + e.getMessage(), e);
      }

    } else {
      throw new JspException("In visualizzazione impostare i parameter KEY o KEYPARENT");
    }
    return result;
  }

  /**
   * @return istanza del plugin specificato nel tag
   */
  private AbstractGestorePreload getPluginInstance() {
    Object o = null;
    if (this.getAttributes().getPlugin() != null) {
      try {
        // si crea il plugin con un argomento valorizzato con il tag stesso
        Class cl = Class.forName(this.getAttributes().getPlugin());
        java.lang.reflect.Constructor constructor = cl.getConstructor(new Class[] { BodyTagSupportGene.class });
        o = constructor.newInstance(new Object[] { this });
      } catch (Exception e) {
        logger.warn("Errore durante l'istanziazione del plugin " + this.getAttributes().getPlugin() + ", si considera la definizione come assente", e);
        o = null;
      }
    }
    return (AbstractGestorePreload) o;
  }

  /**
   * Partendo dalla chiave di una entita, si va a controllare se e' collegata ad UFFINT. Se non risulta collegata, si richiama ricorsivamente sull'entita padre collegata all'entita stessa.
   * @param chiaveEntita chiave serializzata in formato stringa
   * @param sqlManager esecutore query
   * @return stringa serializzata contenente entita, campo di collegamento ad uffint, e valore della chiave, separati da #
   * @throws JspException
   */
  public static String trovaChiaveEntitaCollegataUffint(String chiaveEntita, SqlManager sqlManager, GeneManager geneManager) throws JspException {
    String entita = chiaveEntita.substring(0, chiaveEntita.indexOf("."));

    if ("UFFINT".equals(entita)) {
      // caso base, si termina immediatamente, la UFFINT non si filtra su se stessa
      return null;
    }

    String archiviFiltrati = ConfigManager.getValore("it.eldasoft.associazioneUffintAbilitata.archiviFiltrati");
    if (archiviFiltrati.contains(entita)) {
      // nel caso di configurazione con filtro sugli archivi, si verifica se l'entita' oggetto della ricerca e'
      // uno di quelli filtrati, ed in tal caso si costruisce il risultato, in quanto non esiste alcuna mappatura
      // a livello di dizionario tabelle, campi e join tra tabelle
      if ("TEIM".equals(entita)) {
       return "TEIM#CGENTIM#" + chiaveEntita;
      }
    }

    // verifico che la tabella che devo controllare effettivamente esista
    Tabella tabellaPrincipale = DizionarioTabelle.getInstance().getDaNomeTabella(entita);
    if (tabellaPrincipale == null || !geneManager.esisteTabella(entita)) {
      return null;
    }

    // arrivati qui si analizzano i metadati per comprendere i legami tra la tabella ed eventuali altre tabelle

    Tabella tabellaUffint = DizionarioTabelle.getInstance().getDaNomeTabella("UFFINT");
    String campoFKUffint = null;
    String risultato = null;

    // per prima cosa si cerca nella UFFINT la relazione con la presente tabella principale
    LegameTabelle[] legami = tabellaUffint.getLegameTabelle(entita);
    if (legami.length == 1) {
      campoFKUffint = legami[0].getElencoCampiTabellaDestinazione()[0];
      risultato = entita + "#" + campoFKUffint + "#" + chiaveEntita;
    }

    // se non la trovo, cerco nei legami della tabella principale la presenza di un legame con la tabella UFFINT
    // (non dovrebbe accadere mai, ma per qualche tabella e' stato fatto qui il mapping della relazione anziche' su UFFINT)
    if (campoFKUffint == null && tabellaPrincipale != null) {
      legami = tabellaPrincipale.getLegameTabelle("UFFINT");
      if (legami.length == 1) {
        campoFKUffint = legami[0].getElencoCampiTabellaOrigine()[0];
        risultato = entita + "#" + campoFKUffint + "#" + chiaveEntita;
      }
    }

    if (campoFKUffint == null && tabellaPrincipale != null) {
      // estraggo i legami della tabella principale, per ognuno eseguo la query per ottenere la chiave dell'entita' collegata e passo il risultato alla funzione ricorsiva
      LegameTabelle[] legamiPrincipale = tabellaPrincipale.getLegamiTabelle().toArray(new LegameTabelle[0]);
      String schemaTabellaPrincipale = tabellaPrincipale.getNomeSchema();
      for (LegameTabelle legame : legamiPrincipale) {
        String entitaCollegata = legame.getTabellaDestinazione();
        Tabella tabellaCollegata = DizionarioTabelle.getInstance().getDaNomeTabella(entitaCollegata);
        // per evitare risalite nella gerarchia delle tabelle (es: un archivio generale viene collegato ad entita' di
        // tanti schemi), si esegue la risalita ricorsiva solo su entita' relative allo STESSO schema concettuale, in modo
        // da arrivare alla propria entita' principale di riferimento su cui viene pilotata la visibilita'
        if (tabellaCollegata != null && schemaTabellaPrincipale.equals(tabellaCollegata.getNomeSchema())) {

          // si costruisce la query per estrarre il valore della FK verso l'entita collegata
          JdbcWhere where = new JdbcWhere();
          UtilityTags.jdbcAddKeyWhere(where, chiaveEntita);
          StringBuilder query = new StringBuilder("SELECT ").append(StringUtils.join(legame.getElencoCampiTabellaOrigine(), ",")).append(" FROM ").append(entita).append(where.toString());

          // si estrae la FK (chiave del padre)
          StringBuilder campiPadreSerializzati = new StringBuilder();
          try {
            Vector<JdbcParametro> campiPadre = sqlManager.getVector(query.toString(), SqlManager.getObjectFromPram(where.getParametri()));
            if (campiPadre != null) {
              // solo se trovo una occorrenza nella tabella continuo a navigare nella tabella collegata
              for (int i = 0; i < legame.getElencoCampiTabellaDestinazione().length; i++) {
                String campoChiavePadre = legame.getElencoCampiTabellaDestinazione()[i];
                if (i > 0) {
                  campiPadreSerializzati.append(";");
                }
                campiPadreSerializzati.append(entitaCollegata).append(".").append(campoChiavePadre).append("=");
                campiPadreSerializzati.append(campiPadre.get(i).getTipo()).append(":").append(campiPadre.get(i).getStringValue());
              }
            }
          } catch (SQLException e) {
            throw new JspException("Errore durante l'estrazione del riferimento ad entita padre da chiave figlio " + chiaveEntita + ": "
                    + e.getMessage(), e);
          }

          // si richiama ricorsivamente l'algoritmo sulla chiave dell'entita' collegata, solo se esiste
          if (campiPadreSerializzati.length() > 0) {
            String risultatoRicorsivo = trovaChiaveEntitaCollegataUffint(campiPadreSerializzati.toString(), sqlManager, geneManager);
            if (risultatoRicorsivo != null) {
              return risultatoRicorsivo;
            }
          }
        }
      }
    }

    return risultato;
  }

  /**
   * Partendo dalla chiave di una entita, si va a controllare se ci sono filtri di riga mediante G_PERMESSI. Se non ci sono, si richiama ricorsivamente sull'entita padre collegata all'entita stessa.
   * @param chiaveEntita chiave serializzata in formato stringa
   * @param profiloUtente profilo dell'utente loggato
   * @param sqlManager esecutore query
   * @param geneManager esecutore funzioni utilita' su db
   * @return stringa serializzata contenente entita, filtro sql da applicare, e valore della chiave, separati da #
   * @throws JspException
   */
  public static String trovaChiaveEntitaControllataConPermessi(String chiaveEntita, ProfiloUtente profiloUtente, SqlManager sqlManager, GeneManager geneManager) throws JspException {
    String entita = chiaveEntita.substring(0, chiaveEntita.indexOf("."));

    // verifico che la tabella che devo controllare effettivamente esista
    Tabella tabellaPrincipale = DizionarioTabelle.getInstance().getDaNomeTabella(entita);
    if (tabellaPrincipale == null || !geneManager.esisteTabella(entita)) {
      return null;
    }

    // arrivati qui si analizzano i metadati per comprendere i legami tra la tabella ed eventuali altre tabelle

    // se l'entita' e' gestita con filtro utente allora termino immediatamente
    String filtroLivelloUtente = FiltroLivelloUtenteFunction.getFiltroLivelloUtente(entita, profiloUtente);
    if  (filtroLivelloUtente != null && !filtroLivelloUtente.trim().isEmpty()) {
      return entita + "#" + filtroLivelloUtente + "#" + chiaveEntita ;
    }

    // se l'entita' non e' correlata ad una regola di filtro livello utente, la si cerca ricorsivamente nelle entita' correlate, nello stesso schema
    if (tabellaPrincipale != null) {
      // estraggo i legami della tabella principale, per ognuno eseguo la query per ottenere la chiave dell'entita' collegata e passo il risultato alla funzione ricorsiva
      LegameTabelle[] legamiPrincipale = tabellaPrincipale.getLegamiTabelle().toArray(new LegameTabelle[0]);
      String schemaTabellaPrincipale = tabellaPrincipale.getNomeSchema();
      for (LegameTabelle legame : legamiPrincipale) {
        String entitaCollegata = legame.getTabellaDestinazione();
        Tabella tabellaCollegata = DizionarioTabelle.getInstance().getDaNomeTabella(entitaCollegata);
        // per evitare risalite nella gerarchia delle tabelle (es: un archivio generale viene collegato ad entita' di
        // tanti schemi), si esegue la risalita ricorsiva solo su entita' relative allo STESSO schema concettuale, in modo
        // da arrivare alla propria entita' principale di riferimento su cui viene pilotata la visibilita'
        if (tabellaCollegata != null && schemaTabellaPrincipale.equals(tabellaCollegata.getNomeSchema())) {

          // si costruisce la query per estrarre il valore della FK verso l'entita collegata
          JdbcWhere where = new JdbcWhere();
          UtilityTags.jdbcAddKeyWhere(where, chiaveEntita);
          StringBuilder query = new StringBuilder("SELECT ").append(StringUtils.join(legame.getElencoCampiTabellaOrigine(), ",")).append(" FROM ").append(entita).append(where.toString());

          // si estrae la FK (chiave del padre)
          StringBuilder campiPadreSerializzati = new StringBuilder();
          try {
            Vector<JdbcParametro> campiPadre = sqlManager.getVector(query.toString(), SqlManager.getObjectFromPram(where.getParametri()));
            if (campiPadre != null) {
              // solo se trovo una occorrenza nella tabella continuo a navigare nella tabella collegata
              for (int i = 0; i < legame.getElencoCampiTabellaDestinazione().length; i++) {
                String campoChiavePadre = legame.getElencoCampiTabellaDestinazione()[i];
                if (i > 0) {
                  campiPadreSerializzati.append(";");
                }
                campiPadreSerializzati.append(entitaCollegata).append(".").append(campoChiavePadre).append("=");
                campiPadreSerializzati.append(campiPadre.get(i).getTipo()).append(":").append(campiPadre.get(i).getStringValue());
              }
            }
          } catch (SQLException e) {
            throw new JspException("Errore durante l'estrazione del riferimento ad entita padre da chiave figlio " + chiaveEntita + ": "
                    + e.getMessage(), e);
          }

          // si richiama ricorsivamente l'algoritmo sulla chiave dell'entita' collegata, solo se esiste
          if (campiPadreSerializzati.length() > 0) {
            String risultatoRicorsivo = trovaChiaveEntitaControllataConPermessi(campiPadreSerializzati.toString(), profiloUtente, sqlManager, geneManager);
            if (risultatoRicorsivo != null) {
              return risultatoRicorsivo;
            }
          }
        }
      }
    }
    return null;
  }

}