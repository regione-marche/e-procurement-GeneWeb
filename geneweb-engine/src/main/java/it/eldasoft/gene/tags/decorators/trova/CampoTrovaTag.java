package it.eldasoft.gene.tags.decorators.trova;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.sql.sqlparser.JdbcColumn;
import it.eldasoft.gene.db.sql.sqlparser.JdbcExpression;
import it.eldasoft.gene.db.sql.sqlparser.JdbcExpressionWhere;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.db.sql.sqlparser.JdbcSqlSelect;
import it.eldasoft.gene.db.sql.sqlparser.JdbcTable;
import it.eldasoft.gene.db.sql.sqlparser.JdbcUtils;
import it.eldasoft.gene.db.sql.sqlparser.JdbcWhere;
import it.eldasoft.gene.tags.decorators.campi.AbstractCampoBodyTag;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.tags.decorators.campi.CampoDecorator;
import it.eldasoft.gene.tags.decorators.campi.UtilityDefinizioneCampo;
import it.eldasoft.gene.tags.link.UtilityPopUpCampiImpl;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

public class CampoTrovaTag extends AbstractCampoBodyTag {

  static Logger              logger            = Logger.getLogger(CampoTrovaTag.class);

  /**
   * UID
   */
  private static final long  serialVersionUID  = -4446596927105531373L;

  public static final String INIZIO_NOME_CAMPO = "Campo";

  private CampoDecorator     decoratore;

  public CampoTrovaTag() {

  }

  @Override
  public int doStartTag() throws JspException {
    FormTrovaTag form = (FormTrovaTag) getParent(FormTrovaTag.class);
    if (form == null)
      throw new JspException(
          "Attenzione il campo di trova deve trovarsi all'iterno "
              + "di una form Trova !");
    if ((this.getEntita() == null || this.getEntita().length() == 0)&& (!this.isComputed()))
      this.setEntita(form.getEntita());

    // anche per il campo
    if (form.isGestisciProtezioni() && !this.isSetGestisciProtezioni())
      this.setGestisciProtezioni(true);

    // Se non impostata prendo l'entita dalla form
    if ((this.getEntita() == null) && (!this.isComputed())) this.setEntita(form.getEntita());
    String nomeCampo;
    if (this.getEntita()!=null)
        nomeCampo = this.getEntita() + "." + this.getCampo();
    else
        nomeCampo = this.getCampo();
    Campo campo = DizionarioCampi.getInstance().getCampoByNomeFisico(nomeCampo);
    StringBuffer out = new StringBuffer();
    // Creo il nome del campo
    nomeCampo = INIZIO_NOME_CAMPO + form.getCampo();
    // Creo l'id
    String idCampo;
    if (this.getId() != null) {
      idCampo = this.getId();
    } else {
      idCampo = nomeCampo;
    }

    this.getDecoratore().setCampo(campo, this.pageContext);
    if (this.getDecoratore().getGestore() != null)
      this.setGestore(this.getDecoratore().getGestore().getClass().getName());
    this.getDecoratore().setNome(nomeCampo);
    this.getDecoratore().setFormName(form.getFormName());

    int ret = super.doStartTag();
    // SS 18-11-2009: modifica "congelata" causa gravi impatti su campi
    // visibili ma non editabili, oltretutto non di tipo tabellato
//    this.setAbilitato(this.getDecoratore().isAbilitato());
    this.setAbilitato(true);

    if (this.isVisibile()) {
      ProfiloUtente profilo = (ProfiloUtente) this.pageContext.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      boolean visualizzaVociStandardPopup = false;
      if (profilo != null) {
        OpzioniUtente opzioniUtente = new OpzioniUtente(
            profilo.getFunzioniUtenteAbilitate());
        CheckOpzioniUtente opzioniGenRicCompleto = new CheckOpzioniUtente(
            CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
        CheckOpzioniUtente opzioniGenRicPersonale = new CheckOpzioniUtente(
            CostantiGeneraliAccount.SOLO_REPORT_PERSONALI_GENRIC);
        CheckOpzioniUtente opzioniGenModCompleto = new CheckOpzioniUtente(
            CostantiGeneraliAccount.GESTIONE_COMPLETA_GENMOD);
        CheckOpzioniUtente opzioniGenModPersonale = new CheckOpzioniUtente(
            CostantiGeneraliAccount.SOLO_MODELLI_PERSONALI_GENMOD);

        if ((opzioniGenRicCompleto.test(opzioniUtente)
            || opzioniGenRicPersonale.test(opzioniUtente)
            || opzioniGenModCompleto.test(opzioniUtente) || opzioniGenModPersonale.test(opzioniUtente)))
          visualizzaVociStandardPopup = true;
      }

      if ("TIMESTAMP".equals(this.getDecoratore().getDominio())) {
        // Sabbadin 31/07/2015: nel caso di TIMESTAMP creo un campo di ricerca che filtra solo sulla parte data e non ora
        this.getDecoratore().setDominio("DATA_ELDA", pageContext);
        SqlManager sqlManager = (SqlManager)UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
        this.getDecoratore().setNomeFisico(sqlManager.getDBFunction("datetimetodate", new String[] {this.getDecoratore().getCampo()}));
        this.getDecoratore().setComputed(true);
      }

      out.append("<tr id=\"row" + idCampo + "\">");
      out.append("<td class=\"etichetta-dato\">" + this.getTitle() + "</td>\n");
      out.append("<td class=\"operatore-trova\">\n");
      // Imposto tutti gli elementi nascosti con i dati obbligatori
      out.append(UtilityTags.getHtmlHideInput(nomeCampo + "_where",
          this.getWhere()));

      out.append(UtilityTags.getHtmlHideInput(nomeCampo + "_from",
            this.getFrom()));
      out.append(UtilityTags.getHtmlHideInput(nomeCampo + "_computed",
              Boolean.toString(this.isComputed())));
      // Aggiungo l'eventuale gestore
      out.append(UtilityTags.getHtmlHideInput(nomeCampo + "_gestore",
          this.getGestore()));
      // In funzione del tipo di campo aggiungo l'espressione di
      // confronto per la selezione da parte dell'utente
      String conf = null;

      // SS 18-11-2009: modifica "congelata" causa gravi impatti su campi
      // visibili ma non editabili, oltretutto non di tipo tabellato

//      // R.F. 21/09/09 Se il campo non è abilitato allora non serve la drop down di conf
//      if (!this.getDecoratore().isAbilitato())
//    	  out.append("<input type=\"hidden\" name=\"" + nomeCampo + "_conf\" id=\"" + nomeCampo +
//    			  "_conf\" value=\"=\"/>&nbsp;</td>\n<td class=\"valore-dato-trova\">");
//      else {
	      switch ((this.getDecoratore().getTipo().startsWith("E"))
	          ? JdbcParametro.TIPO_ENUMERATO
	          : this.getDecoratore().getTipoPerJS(campo).charAt(0)) {
	      case JdbcParametro.TIPO_ENUMERATO:
	    	  out.append("<input type=\"hidden\" name=\"" + nomeCampo + "_conf\" id=\"" + idCampo +
	    			  "_conf\" value=\"=\"/>&nbsp;</td>\n<td class=\"valore-dato-trova\">");
	        break;
	      case JdbcParametro.TIPO_DECIMALE: // Float
	      case JdbcParametro.TIPO_NUMERICO: // Numerico
	        conf = form.getValueCampo(nomeCampo + "_conf");
	        out.append("<select name=\"" + nomeCampo + "_conf\" id=\"" + idCampo + "_conf\" >\n");
	        // Scorro tutti i valori
	        out.append("<option value=\"=\" ");
	        if (conf.equals("=")) out.append("selected ");
	        out.append(">=</option>\n");
	        out.append("<option value=\"<\" ");
	        if (conf.equals("<")) out.append("selected ");
	        out.append(">&lt;</option>\n");
	        out.append("<option value=\"<=\" ");
	        if (conf.equals("<=")) out.append("selected ");
	        out.append(">&lt;=</option>\n");
	        out.append("<option value=\">\" ");
	        if (conf.equals(">")) out.append("selected ");
	        out.append(">&gt;</option>\n");
	        out.append("<option value=\">=\" ");
	        if (conf.equals(">=")) out.append("selected ");
	        out.append(">&gt;=</option>\n");
	        out.append("</select>\n");
	        out.append("</td>\n<td class=\"valore-dato-trova\">");
	        break;
	      case JdbcParametro.TIPO_DATA: // Data
	        conf = form.getValueCampo(nomeCampo + "_conf");
	        out.append("<select name=\"" + nomeCampo
	            + "_conf\" id=\"" + idCampo
	            + "_conf\" onchange=\"javascript:trovaVisualizzaDataConfronto('"
	            + nomeCampo + "', '"
                + idCampo + "', true);\">\n");
	        // Scorro tutti i valori
	        out.append("<option value=\"=\" ");
	        if (conf.equals("=")) out.append("selected ");
	        out.append(">=</option>\n");
	        out.append("<option value=\"<\" ");
	        if (conf.equals("<")) out.append("selected ");
	        out.append(">&lt;</option>\n");
	        out.append("<option value=\"<=\" ");
	        if (conf.equals("<=")) out.append("selected ");
	        out.append(">&lt;=</option>\n");
	        out.append("<option value=\">\" ");
	        if (conf.equals(">")) out.append("selected ");
	        out.append(">&gt;</option>\n");
	        out.append("<option value=\">=\" ");
	        if (conf.equals(">=")) out.append("selected ");
	        out.append(">&gt;=</option>\n");
	        out.append("<option value=\"<.<\" ");
	        if (conf.equals("<.<")) out.append("selected ");
	        out.append(">&gt;&nbsp;e&nbsp;&lt;</option>\n");
	        out.append("<option value=\"<=.<=\" ");
	        if (conf.equals("<=.<=")) out.append("selected ");
	        out.append(">&gt;=&nbsp;e&nbsp;&lt;=</option>\n");
            out.append("<option value=\"IS NOT NULL\" ");
            if (conf.equals("IS NOT NULL")) out.append("selected ");
            out.append(">valorizzato</option>\n");
            out.append("<option value=\"IS NULL\" ");
            if (conf.equals("IS NULL")) out.append("selected ");
            out.append(">non valorizzato</option>\n");
	        out.append("</select>\n");
	        out.append("</td>\n<td class=\"valore-dato-trova\">");

	        //Creazione del campo nomeCampo+'Da' che rappresentqa il campo in cui inserire
	        // il limite inferiore della data
	        // Imposto tutti gli elementi nascosti con i dati obbligatori
	        out.append(UtilityTags.getHtmlHideInput(nomeCampo + "Da_where",
	            this.getWhere()));
	        out.append(UtilityTags.getHtmlHideInput(nomeCampo + "Da_from",
	            this.getFrom()));
	        // Aggiungo l'eventuale gestore
	        out.append(UtilityTags.getHtmlHideInput(nomeCampo + "Da_gestore",
	            this.getGestore()));
	        out.append(UtilityTags.getHtmlHideInput(nomeCampo + "Da_conf",
	            form.getValueCampo(nomeCampo + "Da_conf")));

	        this.getDecoratore().setPageContext(
	            ((HttpServletRequest) this.pageContext.getRequest()).getContextPath());
	        //Cambio del nome del campo
	        this.getDecoratore().setNome(nomeCampo + "Da");
	        this.getDecoratore().setId(idCampo + "Da");
	        //Cambio del valore del campo
	        if ("".equals(form.getValueCampo(nomeCampo + "Da")))
	          this.getDecoratore().setValue(getDefaultValue());
	        else
	          this.getDecoratore().setValue(form.getValueCampo(nomeCampo + "Da"));

	        UtilityPopUpCampiImpl.addMenuStandardCampoTrova(this.getDecoratore());
	        // F.D. 30/04/08 aggiungo le voci di menù standard che prima venivano
	        // fatte nell'addMenuStandardCampoTrova in modo che posso gestire
	        // l'aggiunta a seconda delle opzioni utente disponibili
	        // le voci di menù del popup vengono inserite solo se si
	        // ha almeno un'abilitazione al generatore ricerche o modelli
	        // carico il profilo utente con le opzioni in modo che aggiungo al
	        // menù popup l'elemento "info campo" solo se
	        // l'utente è abilitato almeno ad una gestione personale di report o
	        // modelli
	        if(visualizzaVociStandardPopup)
	          UtilityPopUpCampiImpl.addMenuStandardCampo(this.getDecoratore());

	        out.append("<span id=\"span_" + idCampo + "Da\" >");
	        out.append(this.getDecoratore().toString(form.getJavascript()));
	        out.append("<span id=\"span_" + idCampo + "_testo\" ></span>");
	        out.append("</span>");
	        form.getJavascript().print("trovaVisualizzaDataConfronto('"
	            + nomeCampo + "', '"
                + idCampo + "', false);");

	        this.getDecoratore().getPopupItems().removeAllElements();
	        // Ripristino del nome del campo
	        this.getDecoratore().setNome(nomeCampo);
	        this.getDecoratore().setId(idCampo);
	        // Ripristino del valore del campo
	        if ("".equals(form.getValueCampo(nomeCampo)))
	          this.getDecoratore().setValue(getDefaultValue());
	        else
	          this.getDecoratore().setValue(form.getValueCampo(nomeCampo));
	        break;
	      case JdbcParametro.TIPO_TESTO: // Testo
            conf = form.getValueCampo(nomeCampo + "_conf");
            // Per i campi di tipo CLOB, l'operatore di confronto viene prefissato
            // al valore 'contiene' in modo da ricercare la stringa specificata
            // dall'utente come: TABELLA.CAMPO like '%<testo da ricercare>%'
            if(campo != null && "CLOB".equals(campo.getDominio())){
              out.append("<input type=\"hidden\" name=\"" + nomeCampo + "_conf\" id=\"" +
                  idCampo + "_conf\" value=\"contiene\"/>&nbsp;</td>\n<td class=\"valore-dato-trova\">");
            } else {
              out.append("<select name=\"" + nomeCampo + "_conf\" id=\"" + idCampo + "_conf\" >\n");
              // Scorro tutti i valori
              // Opzione 'uguale'
              out.append("<option value=\"" + GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[0] + "\" ");
              if (conf.equals(GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[0]))
                out.append("selected ");
              out.append(">" + GestioneOperatoreConfrontoStringa.CBX_TESTO_CONFRONTO_STRINGA[0] + "</option>\n");

              // Opzione 'contiene'
              out.append("<option value=\"" + GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1] + "\" ");
              if (conf.equals(GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1]) || conf.equals("") )
                out.append("selected ");
              out.append(">" + GestioneOperatoreConfrontoStringa.CBX_TESTO_CONFRONTO_STRINGA[1] + "</option>\n");
              //Opzione 'inizia per'
              out.append("<option value=\"" + GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[2] + "\" ");
              if (conf.equals(GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[2]))
                out.append("selected ");
              out.append(">" + GestioneOperatoreConfrontoStringa.CBX_TESTO_CONFRONTO_STRINGA[2] + "</option>\n");
              //Opzione 'termina per'
              out.append("<option value=\"" + GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[3] + "\" ");
              if (conf.equals(GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[3]))
                out.append("selected ");
              out.append(">" + GestioneOperatoreConfrontoStringa.CBX_TESTO_CONFRONTO_STRINGA[3] + "</option>\n");
              //Opzione 'valorizzato'
              out.append("<option value=\"" + "IS NOT NULL" + "\" ");
              //TODO: sistemare utilizzando le costanti definite negli array
              if (conf.equals("IS NOT NULL"))
                out.append("selected ");
              out.append(">" + "valorizzato" + "</option>\n");
              //Opzione 'non valorizzato'
              out.append("<option value=\"" + "IS NULL" + "\" ");
              if (conf.equals("IS NULL"))
                out.append("selected ");
              out.append(">" + "non valorizzato" + "</option>\n");
              out.append("</select>\n");
              out.append("</td>\n<td class=\"valore-dato-trova\">");
              break;
              //out.append(UtilityTags.getHtmlHideInput(nomeCampo + "_conf", "="));
          }
//        }
      }
      // Aggiungo tutti i popup del campo standard
      UtilityPopUpCampiImpl.addMenuStandardCampoTrova(this.getDecoratore());
      // F.D. 30/04/08 aggiungo le voci di menù standard che prima venivano
      // fatte nell'addMenuStandardCampoTrova in modo che posso gestire
      // l'aggiunta a seconda delle opzioni utente disponibili
      // le voci di menù del popup vengono inserite solo se si
      // ha almeno un'abilitazione al generatore ricerche o modelli
      // carico il profilo utente con le opzioni in modo che aggiungo al
      // menù popup l'elemento "info campo" solo se
      // l'utente è abilitato almeno ad una gestione personale di report o
      // modelli
      if(visualizzaVociStandardPopup)
        UtilityPopUpCampiImpl.addMenuStandardCampo(this.getDecoratore());

      // Ripristino del valore del campo
      if ("".equals(form.getValueCampo(nomeCampo)))
        this.getDecoratore().setValue(getDefaultValue());
      else
        this.getDecoratore().setValue(form.getValueCampo(nomeCampo));

      // Adesso devo inserire il campo
      // {MF071106} Aggiunta del settaggio del context path
      this.getDecoratore().setPageContext(
          ((HttpServletRequest) this.pageContext.getRequest()).getContextPath());

      // SS 18-11-2009: modifica "congelata" causa gravi impatti su campi
      // visibili ma non editabili, oltretutto non di tipo tabellato

//      // F.R. 21/09/09
//      // Aggiunta gestione del campo Abilitato. Se uguale a false inserisce la prima riga del tabellato
//      // inserendo due input type. Uno hidden con id campoN con il valore del tabellato e un text readOnly con
//      // la descrizione
//      if (!this.getDecoratore().isAbilitato()) {
//    	  this.getDecoratore().setTipo("T" + ((ValoreTabellato)this.getDecoratore().getValori().get(0)).getDescr().length());
//    	  this.getDecoratore().setValue(((ValoreTabellato)this.getDecoratore().getValori().get(0)).getValore());
//    	  this.getDecoratore().setVisibile(false);
//    	  out.append(this.getDecoratore().toString(form.getJavascript()));
//    	  this.getDecoratore().setVisibile(true);
//    	  this.getDecoratore().setValue(((ValoreTabellato)this.getDecoratore().getValori().get(0)).getDescr());
//    	  this.getDecoratore().setNome(this.getDecoratore().getNome() + "_valore");
//    	  out.append(this.getDecoratore().toString(form.getJavascript()));
//      }
//      else {
    	  out.append(this.getDecoratore().toString(form.getJavascript()));
//      }
      out.append("</td>");
      out.append("</tr>");

      try {
        this.pageContext.getOut().print(out.toString());
      } catch (IOException e) {
        throw new JspException("Errore nella scrittura della pagina !", e);
      }
    }

    return ret;
  }

  /**
   * Aggiunge il filtro sul campo nella where
   *
   * @param where
   *        Where in cui appendere l'espressione
   * @param col
   *        Nome della colonna
   * @param conf
   *        Tipologia del confronto
   * @param val
   *        Valore
   * @param tipo
   *        Tipo di campo
   * @param caseSensitive
   *        Indica se sui campi stringa la ricerca deve essere case sensitive
   * @param gestore
   *        Gestore della form di trova
   * @param gestoreCampo
   *        Eventuale gestore di campo
   */
  private static void addExpressionToWhere(JdbcWhere where, JdbcColumn col,
      String conf, Object val, char tipo, boolean caseSensitive,
      SqlManager manager, AbstractGestoreTrova gestore, String gestoreCampo) {
    if (val == null && !("IS NULL".equals(conf) || "IS NOT NULL".equals(conf))) return;
    if (gestore != null || (gestoreCampo != null && gestoreCampo.length() > 0)) {
      // Se c'è un gestore allora verifico se è gestita la colonna
      DataColumn colWithValue = new DataColumn(col.getTable(), col.getName(),
          tipo);
      colWithValue.setValue(new JdbcParametro(tipo, val));
      // Se è gestito dal gestore allora esce
      if (gestore != null
          && gestore.gestisciCampo(where, colWithValue, conf, manager)) return;
      if (gestoreCampo != null && gestoreCampo.length() > 0) {
        Object lGestore = UtilityTags.createObject(gestoreCampo);
        // Setto il gestore solo se è un gestore astratto di campo
        if (lGestore instanceof AbstractGestoreCampo) {
          AbstractGestoreCampo gestCampo = (AbstractGestoreCampo) lGestore;
          // Chiamo la funzione di personalizzazione sul campo
          Vector params = new Vector();
          String whereTmp = gestCampo.gestisciDaTrova(params, colWithValue,
              conf, manager);
          if (whereTmp != null) {
            JdbcParametro lPar[] = new JdbcParametro[params.size()];
            for (int i = 0; i < params.size(); i++) {
              lPar[i] = new JdbcParametro(JdbcParametro.TIPO_INDEFINITO,
                  params.get(i));
            }
            where.append(new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
            where.append(new JdbcExpressionWhere(true));
            where.append(new JdbcExpression(whereTmp, lPar));
            where.append(new JdbcExpressionWhere(false));
            return;
          }
        }
      }

    }
    // Verifico se e impostato un gestore di campo

    switch (tipo) {
    case JdbcParametro.TIPO_TESTO:
      where.append(new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
      JdbcExpression colExpr = new JdbcExpression(col);
      if (caseSensitive)
        where.append(colExpr);
      else {
        where.append(new JdbcExpression(manager.getDBFunction("UPPER"),
            new JdbcExpression[] { colExpr }));
      }

      // escape del valore: va usato solo in caso di like in cui si fa il match
      // di stringhe che usano i caratteri speciali SQL
      boolean escapeRequired = false;

      // Verifico se si trova il % significa like
      if ("=".equals(conf) || "IS NULL".equals(conf) || "IS NOT NULL".equals(conf))
        where.append(new JdbcExpression(conf));
      else if ("IN".equals(conf)) {
          where.append(new JdbcExpression("IN"));
          where.append(new JdbcExpression((String) val));
      }
      else {
        where.append(new JdbcExpression("like"));
        // si verifica se son presenti caratteri speciali sql nel valore
        // in tal caso si effettua l'escaping dei caratteri speciali
        escapeRequired = UtilityStringhe.containsSqlWildCards((String) val);
        if (escapeRequired)
          val = UtilityStringhe.escapeSqlString((String) val);
        if(conf.equals("iniziaPer"))
          val = ((String) val).concat("%");
        else if(conf.equals("terminaPer"))
          val = "%".concat((String) val);
        else
          val = "%".concat((String) val).concat("%");
      }
      // Aggiungo il parametro
      if (!"IN".equals(conf) && !"IS NULL".equals(conf) && !"IS NOT NULL".equals(conf)) {
        if (caseSensitive)
          where.append(new JdbcExpression(val, tipo));
        else {
          if (val != null && val instanceof String) {
            where.append(new JdbcExpression(((String) val).toUpperCase(), tipo));
          } else {
            where.append(new JdbcExpression(val, tipo));
          }
        }
        // se è richiesto l'escape, allora si inserisce il comando di escape
        // nella query
        if (escapeRequired) {
          try {
            SqlComposer composer = it.eldasoft.utils.sql.comp.SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
            where.append(composer.getEscapeSql());
          } catch (SqlComposerException e) {
            // non si verifica mai, il caricamento metadati gia' testa che la
            // property sia settata correttamente
            logger.error(
                "Tipo di database non configurato correttamente nel file di configurazione",
                e);
          }
        }
      }
      break;
    case JdbcParametro.TIPO_DECIMALE:
    case JdbcParametro.TIPO_NUMERICO:
      where.append(new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
      where.append(new JdbcExpression(col));
      where.append(new JdbcExpression(conf));
      // Aggiungo il parametro
      where.append(new JdbcExpression(val, tipo));
      break;
    case JdbcParametro.TIPO_DATA:
      if("<.<".equals(conf))
        conf = "<";
      else if("<=.<=".equals(conf))
        conf = "<=";

      where.append(new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
      where.append(new JdbcExpression(col));
      where.append(new JdbcExpression(conf));
      // Aggiungo il parametro solo se necessario
      if (!"IS NULL".equals(conf) && !"IS NOT NULL".equals(conf)) {
        where.append(new JdbcExpression(val, tipo));
      }
      break;
    }
  }

  /**
   * Funzione che aggiunge l'espressione di un campo nella where prendendola
   * dalla definizione dei dati se appartiene all'entità principale della
   * pagina, altrimenti inserisce o aggiorna la select per l'entità secondaria a
   * cui va applicata nella hash filtriAltreTabelle.
   *
   * @param request
   *        HTTP request
   * @param dati
   *        Dati contenenti la request
   * @param campo
   *        Numero del campo da estrarre
   * @param where
   *        Where principale in cui aggiungere l'espressione se relativa
   *        all'entità principale della form di ricerca
   * @param caseSensitive
   *        indica che si tratta di una selezione case sensitive
   * @param gestore
   *        gestore di campo trova
   * @param filtriAltreTabelle
   *        hash contenente le select associate alle tabelle diverse dall'entità
   *        principale; queste vanno aggiornate con i filtri mano a mano che si
   *        trovano filtri impostati su tali entità
   * @return true se settato un qualche filtro, false altrimenti
   */
  public static boolean addExpressionToWhere(HttpServletRequest request,
      String nomeCampo, JdbcWhere where, boolean caseSensitive,
      AbstractGestoreTrova gestore, HashMap filtriAltreTabelle) {
    boolean esito = true;

    SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
        request.getSession().getServletContext(), SqlManager.class);
    // Imposto il nome del campo
    //String nomeCampo = INIZIO_NOME_CAMPO + campo;
    String definizioneCampo = UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_INIZIO_DEFINIZIONE + nomeCampo);
    if (definizioneCampo == null)
      return false;

    String valore = UtilityStruts.getParametroString(request, nomeCampo);
    String entita = UtilityStruts.getParametroString(request,
        FormTrovaTag.CAMPO_ENTITA);
    String sWhere = UtilityStruts.getParametroString(request, nomeCampo
        + "_where");
    String sFrom = UtilityStruts.getParametroString(request, nomeCampo
        + "_from");
    String sConf = UtilityStruts.getParametroString(request, nomeCampo
        + "_conf");
    String sGestore = UtilityStruts.getParametroString(request, nomeCampo
        + "_gestore");
    String sComputed = UtilityStruts.getParametroString(request, nomeCampo
            + "_computed");

    // Aggiungo la where solo se è stato settato un valore
    if (valore == null && !("IS NULL".equals(sConf) || "IS NOT NULL".equals(sConf)))
      esito = false;
    else {
      JdbcColumn col = new JdbcColumn(null,
          UtilityDefinizioneCampo.getNomeFisicoFromDef(definizioneCampo));
      Object par = null;
      // controllo aggiunto per evitare problemi nei casi IS NULL e IS NOT NULL
      // (non hanno argomento)
      if (valore != null
          && !("IS NULL".equals(sConf) || "IS NOT NULL".equals(sConf)))
        par = UtilityStruts.getParameter(valore,
            UtilityDefinizioneCampo.getTipoFromDef(definizioneCampo));
      char tipo = UtilityStruts.getTipo(UtilityDefinizioneCampo.getTipoFromDef(definizioneCampo));
      //if ((col.getName() != null) || (col.getTable() != null && entita.compareToIgnoreCase(col.getTable().toString()) == 0)) {
      if (col.getTable() == null || entita.compareToIgnoreCase(col.getTable().toString()) == 0) {
        addExpressionToWhere(where, col, sConf, par, tipo, caseSensitive, sql,
            gestore, sGestore);
      } else {
        // se è un filtro non sull'entità principale provo a estrarlo dalla
        // hash, e se non lo trovo creo l'occorrenza corrispondente con la parte
        // select e from inizializzata
        JdbcSqlSelect select = null;
        if (col.getTable() != null)
            select = (JdbcSqlSelect) filtriAltreTabelle.get(col.getTable().toString());
        if (select == null) {
          select = new JdbcSqlSelect();
          select.getSelect().append(new JdbcExpression("1"));
          if (!sComputed.equals("true"))
              select.getFrom().append(new JdbcTable(col.getTable().toString()));
          // Aggiungo le tabelle intermedie
          if (sFrom != null && sFrom.length() > 0) {
            String lsTmp = sFrom;
            do {
              int pos = lsTmp.indexOf(',');
              if (pos >= 0) {
                select.getFrom().append(new JdbcTable(lsTmp.substring(0, pos)));
                lsTmp = lsTmp.substring(pos + 1);
              } else {
                select.getFrom().append(new JdbcTable(lsTmp));
                lsTmp = "";
              }
            } while (lsTmp.trim().length() > 0);
          }
          // Aggiungo la where per la join
          if (sWhere != null && sWhere.length() > 0)
            select.getWhere().append(new JdbcExpression(sWhere));

          if (col.getTable() != null)
              filtriAltreTabelle.put(col.getTable().toString(), select);
        }

        // a questo punto si aggiunge la specifica condizione di filtro
        // impostata sul campo
        addExpressionToWhere(select.getWhere(), col, sConf, par, tipo,
            caseSensitive, sql, gestore, sGestore);
      }
    }

    return esito;
  }

  @Override
  public CampoDecorator getDecoratore() {
    if (this.decoratore == null) {
      this.decoratore = new CampoDecorator(true);
    }
    return this.decoratore;
  }

  @Override
  public void setDecoratore(CampoDecorator decoratore) {
    this.decoratore = decoratore;
  }

  /**
   * @return Returns the tooltip.
   */
  public String getTooltip() {
    return this.decoratore.getTooltip();
  }

  /**
   * @param tooltip
   *        The tooltip to set.
   */
  public void setTooltip(String tooltip) {
    this.decoratore.setTooltip(tooltip);
  }
}
