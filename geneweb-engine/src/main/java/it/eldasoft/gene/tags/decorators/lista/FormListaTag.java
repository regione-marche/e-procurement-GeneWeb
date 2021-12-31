package it.eldasoft.gene.tags.decorators.lista;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcColumn;
import it.eldasoft.gene.db.sql.sqlparser.JdbcExpression;
import it.eldasoft.gene.db.sql.sqlparser.JdbcExpressionWhere;
import it.eldasoft.gene.db.sql.sqlparser.JdbcFrom;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.db.sql.sqlparser.JdbcSqlSelect;
import it.eldasoft.gene.db.sql.sqlparser.JdbcTable;
import it.eldasoft.gene.db.sql.sqlparser.JdbcUtils;
import it.eldasoft.gene.db.sql.sqlparser.JdbcWhere;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.TagAttributes;
import it.eldasoft.gene.tags.decorators.archivi.ArchivioRequest;
import it.eldasoft.gene.tags.decorators.scheda.CampiNonDiEntita.EntitaLocal;
import it.eldasoft.gene.tags.decorators.scheda.FormPagineTag;
import it.eldasoft.gene.tags.decorators.scheda.FormSchedaTag;
import it.eldasoft.gene.tags.decorators.trova.FormTrovaTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class FormListaTag extends BodyTagSupportGene {

  private static Logger      logger                            = Logger.getLogger(FormListaTag.class);

  private StringBuffer       body                              = null;

  public static final String PROPERTY_STRING_EMPTYLIST_MESSAGE = "basic.msg.empty_list";

  /**   UID   */
  private static final long  serialVersionUID                  = -7218736784768783334L;

  private FormListaAttributes getAttributes() {
    return (FormListaAttributes) this.getAttributeManager();
  }

  public FormListaTag() {
    super("formLista");
  }

  /**
   * Funzione che aggiunge un campo alla lista dei campi
   *
   * @param cell
   *        Cella da aggiungere
   *
   */
  public void addCell(CampoListaTagImpl cell) {
    // Aggiungo il campo solo se è la prima interazione
    if (this.isFirstIteration()) {
      this.getAttributes().getCells().add(cell);
    }
  }

  /**
   * Funzione che
   *
   * @param headerCell
   */
  public void addHeader(CellHeaderListaTagImpl headerCell) {
    if (this.isFirstIteration()) {
      this.getAttributes().getCellsHeader().add(headerCell);
    }

  }

  /**
   * @return Returns the entita.
   */
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

  /**
   * @return Returns the tableclass.
   */
  public String getTableclass() {
    return getAttributes().getTableclass();
  }

  /**
   * @param tableclass
   *        The tableclass to set.
   */
  public void setTableclass(String tableclass) {
    this.getAttributes().setTableclass(tableclass);
  }

  /**
   * Di default valuto la pagina
   */
  @Override
  public int doStartTag() throws JspException {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 21/11/2006 M.F. Aggiunta delle lettura del numero di righe per pagina dai
    // parametri d'ingresso
    // ************************************************************
    super.doStartTag();
    // Estraggo l'eventuale scheda contenitrice
    FormSchedaTag scheda = (FormSchedaTag) getParent(FormSchedaTag.class);

    String numRighePerPg = UtilityTags.getParametro(this.pageContext,
        FormTrovaTag.CAMPO_RISULTATI_PER_PAGINA);
    if (numRighePerPg != null)
      this.getAttributes().setPagesize(new Integer(numRighePerPg).intValue());
    // Se si trova allinterno di una scheda non gestisco le pagine
    if (scheda != null) {
      this.setPagesize(0);
      this.setId(scheda.getId());
    }

    if (scheda == null)
      this.pageContext.setAttribute(UtilityTags.REQUEST_VAR_TIPO_PAGINA,
          UtilityTags.PAGINA_LISTA, PageContext.REQUEST_SCOPE);
    if (this.getEntita() == null && this.getVarName() == null)
      throw new JspException(
          "Attenzione bisogna settare o il nome della tabella o la variabile che contiene i dati della lista !");
    if (this.getEntita() != null)
      getAttributes().setTabella(
          DizionarioTabelle.getInstance().getDaNomeTabella(this.getEntita()));
    else if (!(this.pageContext.getAttribute(this.getVarName(),
        PageContext.REQUEST_SCOPE) instanceof List)) {
      throw new JspException("La variabile \""
          + this.getVarName()
          + "\" contenente i dati non è una variabile di tipo Lista !");
    } else {
      this.setDatiRequest(true);
      // Imposto l'entita vuota
      this.setEntita(null);
    }

    // Aggiungo l'eventuale gestore dell'archivio sulla lista se è nel request
    this.getAttributes().setArchivio(
        ArchivioRequest.getArchivio(this.pageContext));

    this.impostaValoriNelRequest();
    this.body = new StringBuffer();
    return BodyTag.EVAL_BODY_BUFFERED;
  }

  @Override
  public int doEndTag() throws JspException {
    BodyContent bodyCon = this.getBodyContent();
    if (bodyCon != null) {
      bodyCon.clearBody();
      try {
        this.getPageContext().getOut().print(this.body.toString());
      } catch (IOException e) {
        throw new JspException(e);
      }
    }
    this.body = null;
    return super.doEndTag();
  }

  /**
   * Dopo il corpo verifico se ci sono ancora righe. in tal caso continuo
   * altrimenti chiudo tutto
   */
  @Override
  public int doAfterBody() throws JspException {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 16/10/2006 M.F. Aggiunta delle gestione dei campi aggiunti
    // 08/11/2006 M.F. Aggiunta del path della scheda e della scheda di popup
    // 14/11/2006 M.F. Aggiunta dell'and tra le chiavi
    // 20/03/2007 M.F. Aggiunta dell'implementazione allinterno di una scheda
    // ************************************************************
    // Inizializzazioni
    String lWhere = null;
    String lFrom = null;
    String lParametri = null;
    String elencoCampiChiave = "";
    JdbcSqlSelect select = null;

    // Estraggo l'eventuale scheda contenitrice: in pratica l'idea era di usare
    // dentro una scheda una lista di dati, ma poi è stata sostituita dal
    // ripetersi di una area di dettaglio
    FormSchedaTag scheda = (FormSchedaTag) getParent(FormSchedaTag.class);

    SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.pageContext, SqlManager.class);

    StringBuffer buf = new StringBuffer();
    this.getAttributes().setCurCella(-1);
    // Se si tratta della prima interazione allora eseguo la select sul DB
    if (this.isFirstIteration()) {

      // WE412: anche nella lista va rigenerato l'attributo da porre in sessione
      // prendendolo dal parameter del request (perche' valorizzato in
      // precedenza, essendo una lista dentro un set di pagine di dettaglio)
      if (getParent(FormPagineTag.class) != null) {
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
        String reqEntitaModificabile = this.pageContext.getRequest().getParameter(
            CostantiGenerali.ENTITA_PRINCIPALE_MODIFICABILE);
        // si usa il dato settato nel request, ritenuto prioritario se definito
        if ("1".equals(reqEntitaModificabile)) entitaModificabile = true;
        // settata la variabile, si setta anche l'attributo in sessione
        if (entitaModificabile)
          this.pageContext.getSession().setAttribute(
              CostantiGenerali.ENTITA_PRINCIPALE_MODIFICABILE, "1");
        else
          this.pageContext.getSession().setAttribute(
              CostantiGenerali.ENTITA_PRINCIPALE_MODIFICABILE, "0");
      }


      BodyContent bodyCon = this.getBodyContent();
      if (scheda == null) {
        buf.append("<form ");
        buf.append(UtilityTags.getHtmlAttrib("name", this.getId()));
        buf.append(UtilityTags.getHtmlAttrib("action", this.getContextPath()
            + "/Lista.do"));
        buf.append("method=\"post\">\n");
        // {MF090207} Aggiungo il javasrcipt con la creazione delle form
        this.getJavascript().println(
            "var local"
                + this.getId()
                + " = new FormObj(document."
                + this.getId()
                + ");");
      }
      if (this.isDatiRequest()) {
        int curKey = 0;
        // I dati sono passati come variabile (variabile del tipo Lista di
        // vettori)
        for (Enumeration en = this.getAttributes().getCells().elements(); en.hasMoreElements();) {
          CampoListaTagImpl cell = (CampoListaTagImpl) en.nextElement();
          if (cell.isCampo()) {
            if (cell.getCampo().length() > 0) {
              cell.setNCampo(Integer.parseInt(cell.getCampo().substring(1)) - 1);
            } else
              throw new JspException("Attenzione: il nome del campo \""
                  + cell.getCampo()
                  + "\" non è valorizzato. "
                  + "Questo perché si tratta di una lista da variabile !");
            if (cell.isChiave()) {
              if (curKey > 0) elencoCampiChiave += ";";
              elencoCampiChiave += String.valueOf(cell.getNomeFisico());
              curKey++;
            }
          }
        }
        // Creo il paginatore
        // Estraggo il campo per l'ordinamento
        CampoListaTagImpl[] campiSort = PaginatoreListaImpl.getCampiSort(
            this.pageContext, this);
        // si utilizza elencoCampiSort per contenere campi ordinamento + chiave
        elencoCampiChiave = PaginatoreListaImpl.getCampiSortConChiave(
            campiSort, elencoCampiChiave);
        getAttributes().setPaginator(
            new PaginatoreListaImpl(elencoCampiChiave,
                this.getAttributes().getPagesize(),
                (List) this.getPageContext().getAttribute(this.getVarName(),
                    PageContext.REQUEST_SCOPE)));
        this.getAttributes().setValori(
            this.getAttributes().getPaginator().getDati(this.pageContext));
      } else {
        if (this.getAttributes().getSelect() == null
            || this.getAttributes().getSelect().length() == 0) {
          // Creo la select
          select = new JdbcSqlSelect();
          if (this.getAttributes().getDistinct()!= null)
        	  if (this.getAttributes().getDistinct().equals("true"))
        		  select.setDistinct(true);
          // Aggiungo l'eventuale where per i campi esterni
          JdbcWhere whereExt = new JdbcWhere();
          // Creo la tabella con il nome fisico
          JdbcTable tab = new JdbcTable(
              getAttributes().getTabella().getNomeTabella());
          // Nella from aggiungo la tabella principale
          select.getFrom().append(tab);
          int curCampo = 0;

          whereExt.append(JdbcExpressionWhere.getParentesi(true));
          // Come prima cosa aggiungo tutti i campi chiave se non cerco il distinct

          if (this.getAttributes().getDistinct() == null || !this.getAttributes().getDistinct().equals("true")) {
              for (Campo campo : getAttributes().getTabella().getCampiKey()) {
	            select.getSelect().append(
	                new JdbcExpression(new JdbcColumn(tab, campo.getNomeCampo())));
	            if (curCampo > 0) {
	              whereExt.append(new JdbcExpressionWhere(
	                  JdbcUtils.JDBC_PARTICELLA_AND));
	              elencoCampiChiave += ";";
	            }
	            elencoCampiChiave += campo.getNomeFisicoCampo();
	            whereExt.append(JdbcExpression.getColumn(campo.getNomeFisicoCampo()));
	            whereExt.append(JdbcExpression.getFixed(" = "));
	            whereExt.append(JdbcExpression.getParametro(campo.getTipoColonna(),
	                null));
	            curCampo++;
	          }
	          whereExt.append(JdbcExpressionWhere.getParentesi(false));
          }
          // Successivamente aggiungo il resto dei campi aggiunti
          for (Enumeration en = this.getAttributes().getCells().elements(); en.hasMoreElements();) {
            CampoListaTagImpl cell = (CampoListaTagImpl) en.nextElement();
            // Se si tratta di un campo allora lo aggiungo all'elenco nella
            // select
            if (cell.isCampo()) {
              cell.setNCampo(curCampo);

              // Se si tratta di un campo verifico che appartenga alla
              // medesima tabella principale
              if (cell.isComputed()
                  || getAttributes().getTabella().getNomeTabella().compareToIgnoreCase(
                      cell.getEntita()) == 0) {
                curCampo++;
                if ("TIMESTAMP".equals(cell.getDominio())) {
                  // SS 07/07/2015: introdotta la gestione del dominio TIMESTAMP solo per la visualizzazione del valore formattandolo a
                  // stringa
                  select.getSelect().append(
                      new JdbcExpression(new JdbcColumn(null,
                          sql.getDBFunction("datetimetostring",new String[] {cell.getNomeFisico()}))));
                } else {
                  select.getSelect().append(
                      new JdbcExpression(new JdbcColumn(null,
                          cell.getNomeFisico())));
                }
                if (cell.getFrom() != null && cell.getFrom().length() > 0)
                	select.getFrom().append(new JdbcTable(cell.getFrom()));
                if (cell.getWhere() != null && cell.getWhere().length() > 0) {
                	if (select.getWhere() != null)
                    	select.getWhere().append(new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
                	select.getWhere().append(cell.getWhere());
                }
              } else {
                // identifico la tabella in modo univoco, considerando anche l'eventuale alias
                String idTabella = (cell.getAlias() == null ? cell.getEntita() : cell.getAlias());

                // {MF161006} Campo riferito ad altra entità
                EntitaLocal entit = this.getAttributes().getEntitaDiverse().getEntitaEsterna(
                    idTabella);
                if (entit == null) {
                  // Se non esiste l'entità collegata allora
                  // la creo
                  entit = this.getAttributes().getEntitaDiverse().addEntita(
                      idTabella);
                  entit.select.getWhere().append(whereExt);
                  if (cell.getWhere() != null && cell.getWhere().length() > 0) {
                    entit.select.getWhere().append(
                        new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
                    entit.select.getWhere().append(
                        new JdbcExpressionWhere(true));
                    entit.select.getWhere().append(
                        new JdbcExpression(cell.getWhere(),
                            new JdbcParametro[] {}));
                    entit.select.getWhere().append(
                        new JdbcExpressionWhere(false));
                  }
                  entit.select.getFrom().append(new JdbcTable(this.getEntita()));
                  // aggiungo la tabella alla from con o senza alias a seconda della presenza
                  //if (idTabella.equals(cell.getEntita())) {
                      entit.select.getFrom().append(new JdbcTable(cell.getEntita()));
                  //} else {
                  //  entit.select.getFrom().append(new JdbcTable(cell.getEntita(), idTabella));
                  //}
                  if (cell.getFrom() != null && cell.getFrom().length() > 0) {
                    String lsTmp = cell.getFrom();
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
                    } while (lsTmp.trim().length() > 0);
                  }
                }
                // Appendo il campo alla select
                cell.setNCampo(entit.select.getSelect().getEspressioni().size());

                if ("TIMESTAMP".equals(cell.getDominio())) {
                  // SS 07/07/2015: introdotta la gestione del dominio TIMESTAMP solo per la visualizzazione del valore formattandolo a
                  // stringa
                  entit.select.getSelect().append(
                      new JdbcExpression(new JdbcColumn(null,
                          sql.getDBFunction("datetimetostring",new String[] {cell.getNomeFisico()}))));
                } else {
                  entit.select.getSelect().append(
                      UtilityTags.getJdbcExpression(cell));
                }
              }
            }
          }
        } else {
          // E' stata settata la select sql diretta su database.
          // Creo l'oggetto per la select
          select = new JdbcSqlSelect(this.getSql());
          int curCampo = 0, curKey = 0;

          for (Enumeration en = this.getAttributes().getCells().elements(); en.hasMoreElements();) {
            CampoListaTagImpl cell = (CampoListaTagImpl) en.nextElement();
            if (cell.isCampo()) {
              if (cell.isChiave()) {
                if (curKey > 0) elencoCampiChiave += ";";
                elencoCampiChiave += cell.getNomeFisico();
                curKey++;
              }
              cell.setNCampo(curCampo);
              curCampo++;
            }
          }
        }
        // Aggiungo l'eventuale where
        lWhere = UtilityTags.getParametro(pageContext,
            UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
        if (!UtilityTags.isFreeFromSqlInjection(lWhere)) {
          String message = "Rilevata condizione SQL potenzialmente afflitta da SQL Injection: " + lWhere;
          logger.error(message);
          throw new JspException(message);
        }
        lFrom = UtilityTags.getParametro(pageContext,
            UtilityTags.DEFAULT_HIDDEN_FROM_DA_TROVA);
        lParametri = UtilityTags.getParametro(pageContext,
            UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA);

        // {M.F. 12.10.06} Aggiungo l'eventuale where settata
        if (this.getAttributes().getWhere() != null) {
        	if (select.getWhere() != null && StringUtils.isNotEmpty(select.getWhere().toString()))
        		select.getWhere().append(new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
          // Si tratta di una lista all'interno di una pagina imposto la
          // where da aggiungere
          UtilityTags.addWhereSelezionata(this.pageContext, select.getWhere(),
              this.getAttributes().getWhere());

        }

        // Se non è impostata la where aggiungo la where data dalla form
        // di trova
        if (lWhere != null) {
          select.getWhere().append(
              new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
          select.getWhere().append(new JdbcExpressionWhere(true));
          select.getWhere().append(lWhere,
              UtilityTags.stringToVectorJdbcParametro(lParametri));
          select.getWhere().append(new JdbcExpressionWhere(false));
        }

        // Estraggo il reference al campo con l'ordinamento
        CampoListaTagImpl[] campiSort = null;
        if (scheda == null) {
          campiSort = PaginatoreListaImpl.getCampiSort(this.pageContext, this);
          elencoCampiChiave = PaginatoreListaImpl.getCampiSortConChiave(
              campiSort, elencoCampiChiave);
        } else {
          if (this.getSortColumn() != null) {
            campiSort = this.getCampiSortColumn();
          }
          elencoCampiChiave = PaginatoreListaImpl.getCampiSortConChiave(null,
              elencoCampiChiave);
        }

        // 20080918: corretto l'ordinamento su campi non appartenenti all'entita
        // principale: si aggiunge alla where tutte le condizioni di filtro
        // specificate sul campo, nonchè si aggiungono anche le eventuali
        // tabelle di collegamento all'entità principale

        // Se l'ordinamento e su una entità esterna allora creo l'outer Join
        if ((this.getAttributes().getSelect() == null || this.getAttributes().getSelect().length() == 0)
            && campiSort != null
            // && !campiSort.isComputed()
            && this.getEntita() != null
            && this.getEntita().length() > 0) {
          // && !this.getEntita().equalsIgnoreCase(campoSort.getEntita())) {
          // Si tratta di un campo di un'entità collegata (aggiungo l'outer
          // join)
          for (int i = 0; i < campiSort.length; i++) {
            if (!campiSort[i].isComputed()
                && !this.getEntita().equalsIgnoreCase(campiSort[i].getEntita())) {
              JdbcWhere whereAdd = new JdbcWhere();
              JdbcFrom from = this.getJoinFromWhere(this.getEntita(),
                  campiSort[i].getEntita(), campiSort[i].getFrom(),
                  campiSort[i].getWhere(), whereAdd);
              // solo se ci sono filtri aggiuntivi si aggiungono, anteponendo la
              // AND solo se la where non è vuota
              if (whereAdd.getSezioni().size() > 0) {
                if (select.getWhere().getSezioni().size() > 0)
                  select.getWhere().append(
                      new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
                select.getWhere().append(whereAdd);
              }
              select.getFrom().append(from);
            }
          }
        }

        // Se non è impostata la from aggiungo la where data dalla form
        // di trova
        if (lFrom != null && lFrom.length() > 0) {
            String lsTmp = lFrom;
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
        // }

        // Se devo creo il gestore delle pagine per la paginazione sulla lista

        HttpSession session = ((HttpServletRequest)this.pageContext.getRequest()).getSession();
        if (this.getAttributes().getDistinct() == null || !this.getAttributes().getDistinct().equals("true")) {

	        getAttributes().setPaginator(
	            new PaginatoreListaImpl(select.getFrom(), select.getWhere(),
	                PaginatoreListaImpl.getCampiSortConChiave(campiSort, null),
	                elencoCampiChiave, this.getAttributes().getPagesize(), sql,
	                session));
        }
        else {

	        getAttributes().setPaginator(
	            new PaginatoreListaImpl(select.getFrom(), select.getWhere(),
	                PaginatoreListaImpl.getCampiSortConChiave(campiSort, null),
	                elencoCampiChiave, this.getAttributes().getCells(), this.getAttributes().getPagesize(), sql,
	                session));
        }
        // Verifico se è stata cambiata il numero di pagina
        getAttributes().getPaginator().gestisciCambioPg(select,
            this.pageContext);
        try {
          if (this.getAttributes().getPagesize() > 0) {
            this.getAttributes().setValori(
                sql.getListVector(select, this.getAttributes().getPaginator().getCurPage(), this.getAttributes().getPagesize()));
          } else {
            this.getAttributes().setValori(sql.getListVector(select));
          }
        } catch (Throwable t) {
          throw new JspException("Errore durante la selezione nella lista:\n"
              + t.getMessage(), t);
        }
        logger.debug("Letti: " + this.getAttributes().getValori().size());
      }
      // Setto la pagina per l'apertura in funzione che sia in popUp o meno
      if (UtilityTags.isPopUp(this.pageContext)) {
        pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_TO_JSP,
            this.getPathSchedaPopUp(), PageContext.REQUEST_SCOPE);
      } else {
        pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_TO_JSP,
            this.getPathScheda(), PageContext.REQUEST_SCOPE);
      }
      if (scheda == null) {
        // Aggiunta del settaggio della pagina da aprire di default
        // Aggiungo tutti gli hidden di default
        buf.append(UtilityTags.getHtmlDefaultHidden(this.pageContext));
        buf.append(UtilityTags.getHtmlHideInput("metodo", "apri"));
        buf.append(UtilityTags.getHtmlHideInput(
            UtilityTags.DEFAULT_HIDDEN_NOME_TABELLA, this.getEntita()));
        buf.append(UtilityTags.getHtmlHideInput(
            UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, lWhere));
        buf.append(UtilityTags.getHtmlHideInput(
                UtilityTags.DEFAULT_HIDDEN_FROM_DA_TROVA, lFrom));
        buf.append(UtilityTags.getHtmlHideInput(
            UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, lParametri));
        buf.append(UtilityTags.getHtmlHideInput(
            UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, ""));
        buf.append(UtilityTags.getHtmlHideInput(
            UtilityTags.DEFAULT_HIDDEN_PATH_SCHEDA, this.getPathScheda()));
        buf.append(UtilityTags.getHtmlHideInput(
            UtilityTags.DEFAULT_HIDDEN_PATH_SCHEDA_POPUP,
            this.getPathSchedaPopUp()));

        // {MF021106} Aggiungo l'eventuale chiave esterna

        buf.append(UtilityTags.getHtmlHideInput(
            UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
            UtilityTags.getParametro(pageContext,
                UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT) == null
                ? UtilityTags.getParametro(pageContext,
                    UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA)
                : UtilityTags.getParametro(pageContext,
                    UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT)));
        buf.append(UtilityTags.getHtmlHideInput(
            UtilityTags.DEFAULT_HIDDEN_NOME_GESTORE, this.getGestore()));
        // Aggiungo tutti gli hidden del paginatore
        buf.append(getAttributes().getPaginator().getDefaultHidden(
            this.pageContext));

        // 20090223: aggiunta della centralizzazione dell'edit di una lista;
        // si predispone un campo hidden che qui di default viene cercato se già
        // settato in precedenza negli attributi: in caso positivo viene settato
        // il valore estratto, altrimenti lo si fissa a 0 (pagina non in edit)
        String updateLista = (String) pageContext.getAttribute(
            UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA, PageContext.REQUEST_SCOPE);
        if (updateLista == null) updateLista = "0";
        buf.append(UtilityTags.getHtmlHideInput(
            UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA, updateLista));

        // Aggiungo il body appena letto
        if (bodyCon != null) buf.append(bodyCon.getString());
        buf.append(getAttributes().getPaginator().getHtmlPaginatore(
            !UtilityTags.isNavigazioneDisabilitata(this.getPageContext())));
      }
      // Aquesto punto inserisco la tabella
      buf.append("<table ");
      buf.append(UtilityTags.getHtmlAttrib("class", this.getTableclass()));
      buf.append(UtilityTags.getHtmlAttrib("id", "tab" + this.getId()));
      buf.append(">\n");
      buf.append("<thead><tr>\n");
      // Inserisco tutti gli hendler di colonna
      boolean navigazioneDisabilitata = UtilityTags.isNavigazioneDisabilitata(this.getPageContext());
      for (int i = 0; i < this.getAttributes().getCellsHeader().size(); i++) {
        CellHeaderListaTagImpl cell = (CellHeaderListaTagImpl) this.getAttributes().getCellsHeader().get(
            i);
        buf.append(cell.toString(navigazioneDisabilitata || scheda != null));
      }
      buf.append("</tr></thead>\n");
      buf.append("<tbody>\n");
      this.getAttributes().setCurrentRow(0);
      super.doAfterBody();

      this.body.append(buf);
      // Impostazioni della riga
      this.leggiColonneEsterne();
      this.impostaValoriCelle();
      this.impostaValoriNelRequest();

      if (this.getAttributes().getValori().size() > 0) {
        // SS 20100209: + evidenziazione della riga in una lista
        this.getJavascript().println(
            "addTableRolloverEffect('tab" + this.getId() + "','tableRollOverEffect1');");

        return EVAL_BODY_AGAIN;
      } else
        buf = new StringBuffer("");
    } else {
      super.doAfterBody();
    }

    if (this.getAttributes().getValori().size() == 0) {
      buf.append("<tr>");
      buf.append("<td ");
      buf.append(UtilityTags.getHtmlAttrib("colspan",
          String.valueOf(this.getAttributes().getCells().size())));
      buf.append(">");
      buf.append(ResourceBundle.getBundle(
          CostantiGenerali.RESOURCE_BUNDLE_DISPLAYTAG).getString(
          PROPERTY_STRING_EMPTYLIST_MESSAGE));
      buf.append("</td>");
      buf.append("</tr>");
      // Eseguo la scrittura dei dati
      this.body.append(buf);
    } else if (this.getAttributes().getCurrentRow() >= 0
        && this.getAttributes().getCurrentRow() <= this.getAttributes().getValori().size()) {

      // Dalla seconda interazione in poi scrivo le righe
      // Se la riga corrente è maggiore o uguale a 0 allora inserisco la
      // riga
      buf.append("<tr ");
      // Aggiungo la classe per le righe pari e dispari
      buf.append(UtilityTags.getHtmlAttrib("class",
          (this.getAttributes().getCurrentRow() % 2) == 0 ? "even" : "odd"));
      buf.append(">\n");
      for (int i = 0; i < this.getAttributes().getCells().size(); i++) {
        CampoListaTagImpl lCel = (CampoListaTagImpl)
            this.getAttributes().getCells().get(i);
        // 20090123: se si è in modalità di edit, ogni campo viene generato con
        // un name e id che ha un suffisso "_"+indice della riga
        lCel.setNome(lCel.getNome()
            + "_"
            + (this.getAttributes().getCurrentRow() + 1));
        lCel.setNomeFisico(lCel.getNomeFisico()
            + "_"
            + (this.getAttributes().getCurrentRow() + 1));

        // Modifica di nome e nomeFisico della cella per evitare che nella
        // stessa riga due campi fittizzi abbiano lo stesso id.
        // In pratica da: id="COL_CAMPO_GENERICO_<i>"), si fa in modo che
        // diventi: id="COL_CAMPO_GENERICO_<i>-<j>",  dove i e j sono
        // rispettivamente l'indice di riga e l'indice di colonna
        if(lCel.getNome().indexOf("CAMPO_GENERICO") >= 0){
          lCel.setNome(lCel.getNome() + "-" + (i + 1));
          lCel.setNomeFisico(lCel.getNomeFisico() + "-" + (i + 1));
        }

        if (lCel.isEdit()) {
          // si aggiunge anche il campo ad un oggetto che costruisce l'elenco
          // campi editabili per la form
          if (this.getAttributes().getElencoCampi().length() > 0)
            this.getAttributes().getElencoCampi().append(";");
          this.getAttributes().getElencoCampi().append(lCel.getNome());
        }
        buf.append(lCel.toString());
        // 20090223: se prima è stata inserita una parte finale, ora la si
        // toglie ripristinando il nome corretto
        //if (lCel.isEdit()) {
        lCel.setNome(lCel.getNome().substring(0,
            lCel.getNome().lastIndexOf('_')));
        lCel.setNomeFisico(lCel.getNomeFisico().substring(0,
            lCel.getNomeFisico().lastIndexOf('_')));
        //}
      }

      buf.append("</tr>\n");
      // Eseguo la scrittura dei dati
      this.body.append(buf);

    }
    // Mi sposto sulla prossima riga
    this.getAttributes().setCurrentRow(getAttributes().getCurrentRow() + 1);
    if (this.getAttributes().getCurrentRow() < this.getAttributes().getValori().size()) {
      // Impostazioni della riga
      this.leggiColonneEsterne();
      this.impostaValoriCelle();
      this.impostaValoriNelRequest();
      // Mi sposto alla prossima riga
      return EVAL_BODY_AGAIN;
    }
    // Chiudo le interazioni
    // Se si ha la possibilità di inserire inserisco la parte
    if (this.getAttributes().isInserisciDaArchivio()
        && this.getAttributes().getArchivio() != null
        && this.getAttributes().getArchivio().isInseribile()) {

      this.body.append("<tr>\n<td class=\"comandi-dettaglio\" colspan=\"");
      this.body.append(this.getAttributes().getCellsHeader().size());
      this.body.append("\" style=\"text-align: right;\">\n");
      this.body.append(UtilityTags.getResource(
          "label.tags.template.lista.inserisciArchivio",
          new String[] { this.getAttributes().getArchivio().getTitolo() }, true));

      this.body.append("</td>\n</tr>\n");
    }
    this.body.append("</tbody></table>\n");
    // Aggiungo il paginatore anche alla fine delle lista se si hanno più
    // pagine
    if (getAttributes().getPaginator().getPageCount() > 1 || scheda != null) {
      this.body.append(getAttributes().getPaginator().getHtmlPaginatore(
          !UtilityTags.isNavigazioneDisabilitata(this.getPageContext())));
    }
    // Se è impostato su un archivio allora aggiungo tutti i campi hidden
    // dell'archivio
    if (this.getAttributes().getArchivio() != null) {
      this.body.append(this.getAttributes().getArchivio().toString());
    }

    // 20090223: si introduce il campo hidden elencoCampi con l'elenco dei campi
    // editabili presenti nella form a lista
    if (this.getAttributes().getElencoCampi().length() > 0)
      this.body.append(UtilityTags.getHtmlHideInput(
          UtilityTags.DEFAULT_HIDDEN_ELENCO_CAMPI,
          this.getAttributes().getElencoCampi().toString()));

    if (scheda == null) this.body.append("</form>\n");
    return SKIP_BODY;
  }

  /**
   * Estrae l'elenco delle celle sulle quali effettuare l'ordinamento, a partire
   * dall'attributo sortColumn valorizzato nel tag
   *
   * @throws JspException
   */
  public CampoListaTagImpl[] getCampiSortColumn() throws JspException {
    String[] elencoCampiOrdinamentoDefault = UtilityStringhe.deserializza(
        this.getSortColumn(), ';');
    CampoListaTagImpl campoAppoggio = null;
    Vector elencoCampi = new Vector();
    // si cicla sui campi di ordinamento, e per ognuno si ottiene la cella
    // corrispondente
    for (int i = 0; i < elencoCampiOrdinamentoDefault.length; i++) {
      try {
        int colNum = Math.abs(Integer.parseInt(elencoCampiOrdinamentoDefault[i])) - 1;
        campoAppoggio = this.getCella(colNum);
        if (campoAppoggio == null)
          throw new JspException("La colonna "
              + colNum
              + " non è una colonna valida per l'ordinamento");
        if (campoAppoggio.getCampo() == null
            || campoAppoggio.getCampo().length() == 0)
          throw new JspException(
              "La colonna "
                  + colNum
                  + " non è un campo del database quindi non è possibile eseguire l'ordinamento.\n"
                  + " Modificare l'attributo sortColumn");
        // se si arriva qui si reperisce correttamente la cella, allora si
        // setta anche l'ordinamento
        if (Integer.parseInt(elencoCampiOrdinamentoDefault[i]) < 0)
          campoAppoggio.setSort(-1);
        else
          campoAppoggio.setSort(1);
      } catch (NumberFormatException e) {
        throw new JspException(
            "Il campo sortColumn contiene una serie di interi separati da \";\" non in formato valido: "
                + this.getSortColumn(), e);
      }
      // se si arriva qui, allora il campo è stato reperito nella lista e lo
      // si aggiunge al contenitore
      elencoCampi.add(campoAppoggio);
    }
    return (CampoListaTagImpl[]) elencoCampi.toArray(new CampoListaTagImpl[0]);
  }

  /**
   * Funzione che crea la from di join tra l'entità principale e la secondaria
   * estraendola da una where
   *
   * @param entita
   *        Entità principale
   * @param entita1
   *        Entita secondaria
   * @param asFrom
   *        tabelle aggiuntive per legare l'entità principale con l'entità
   *        secondaria
   * @param asWhere
   *        where con la join (inner join)
   * @param whereAdd
   *        Ulteriore filtro sulla where
   * @return
   */
  private JdbcFrom getJoinFromWhere(String entita, String entita1,
      String asFrom, String asWhere, JdbcWhere whereAdd) {
    JdbcFrom from = new JdbcFrom(asFrom);
    from.appendJoinFromWhere(entita, entita1, asFrom, asWhere, whereAdd,
        SqlManager.getTipoDBperCompositore());
    return from;
  }

  private void impostaValoriCelle() {

    for (int nCella = 0; nCella < this.getAttributes().getCells().size(); nCella++) {
      CampoListaTagImpl cel = this.getCella(nCella);
      if (cel.isCampo()) {
        if (this.getAttributes().getCurrentRow() < this.getAttributes().getValori().size())
          cel.setValue(this.getValore(nCella));
        else
          cel.setValue("");
      }
    }

  }

  /**
   * Funzione che esegue la lettura di tutte le sottoselect
   *
   */
  private void leggiColonneEsterne() throws JspException {
    if (this.getAttributes().getCurrentRow() >= 0
        && this.getAttributes().getCurrentRow() < this.getAttributes().getValori().size()) {
      SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
          this.pageContext, SqlManager.class);
      this.getAttributes().getEntitaDiverse().leggiTutti(
          sql,
          (Vector) this.getAttributes().getValori().get(
              this.getAttributes().getCurrentRow()));
    }
  }

  /**
   * @return Returns the pagesize.
   */
  public int getPagesize() {
    return getAttributes().getPagesize();
  }

  /**
   * @param pagesize
   *        The pagesize to set.
   */
  public void setPagesize(int pagesize) {
    this.getAttributes().setPagesize(pagesize);
  }

  /**
   * @return Returns the firstIteration.
   */
  public boolean isFirstIteration() {
    return this.getNumIteration() == 0;
  }

  /**
   * @return Returns the currentRow.
   */
  public int getCurrentRow() {
    return getAttributes().getCurrentRow();
  }

  /**
   * Funzione che restituisce il nome delle prossima cella libera
   *
   * @return
   */

  public int getNextCella() {
    this.getAttributes().setCurCella(getAttributes().getCurCella() + 1);
    return this.getAttributes().getCurCella();
  }

  /**
   * Funzione che restituisce la cella voluta
   *
   * @param nCella
   * @return
   */
  public CampoListaTagImpl getCella(int nCella) {
    if (nCella >= 0 && nCella < this.getAttributes().getCells().size())
      return (CampoListaTagImpl) this.getAttributes().getCells().get(nCella);
    return null;
  }

  public String getValore(int cella) {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 16/10/2006 M.F. Se il campo è di un'altra tabella allora la estraggo
    // dai campi esterni
    // 09/02/2007 M.F. Aggiunta delle considerazione che si tratti di una lista
    // da dati esterni
    // 24/10/2007 M.F. Gestione del null sul valore della cella
    // ************************************************************

    CampoListaTagImpl cell = this.getCella(cella);
    // Se la cella è valida allora estraggo il valore
    if (cell != null) {
      if (cell.isCampo() && cell.getNCampo() >= 0) {

        if (this.isDatiRequest()
            || (this.getSql() != null && this.getSql().length() > 0)
            || cell.isComputed()
            || cell.getEntita().compareToIgnoreCase(this.getEntita()) == 0) {
          // Se il valore delle cella è vuoto allora do stringa vuota
          if (((Vector) this.getAttributes().getValori().get(
              this.getAttributes().getCurrentRow())).get(cell.getNCampo()) == null)
            return "";
          return ((Vector) this.getAttributes().getValori().get(
              this.getAttributes().getCurrentRow())).get(cell.getNCampo()).toString();
        } else {
          return this.getAttributes().getEntitaDiverse().getValue(cell);
        }
      }
    }
    return "";
  }

  /**
   * Inizializza i valori nel request
   */
  private void impostaValoriNelRequest() {
    // ///////////////////////////////////////////////////////////////
    // Modifiche
    // ----------------
    // 23.10.2007: M.F. Aggiunta del settaggio delle riga e del conteggio delle
    // righe
    // ////////////////////////////////////////////////////////////// /
    String keys = "";
    Vector riga = null;
    // Setto la riga attiva
    this.pageContext.setAttribute(UtilityTags.REQUEST_VAR_CURRENT_ROW,
        new Integer(this.getAttributes().getCurrentRow()),
        PageContext.REQUEST_SCOPE);
    // Ricavo il valore dei campi chiave se la riga corrente è una riga
    // valida
    if (this.getAttributes().getCurrentRow() >= 0
        && this.getAttributes().getCurrentRow() < this.getAttributes().getValori().size()) {
      riga = (Vector) this.getAttributes().getValori().get(
          this.getAttributes().getCurrentRow());

      // Scorro tutti i campi chiave (che sono stati preventivamente
      // immessi all'inizio
      // della select)
      Vector valCampi = new Vector();

      if (getAttributes().getTabella() != null
          && (this.getAttributes().getSelect() == null || this.getAttributes().getSelect().length() == 0)) {

        if (!"true".equals(this.getAttributes().getDistinct())) {
          // la gestione di chiaveRiga è mutuamente esclusiva con la gestione di
          // distinct, in quanto se si imposta l'attributo distinct sul
          // formLista allora si è in assenza di univocità e chiaveRiga perde
          // senso
          for (Campo campo : getAttributes().getTabella().getCampiKey()) {
            StringBuffer buf = new StringBuffer();
            buf.append(campo.getNomeFisicoCampo());
            buf.append("=");
            JdbcParametro lPar = (JdbcParametro) riga.get(valCampi.size());
            buf.append(lPar.toString(true));
            valCampi.add(buf.toString());
          }
        }
      } else {
        for (Enumeration en = this.getAttributes().getCells().elements(); en.hasMoreElements();) {
          CampoListaTagImpl cell = (CampoListaTagImpl) en.nextElement();
          if (cell.isChiave()) {
            StringBuffer buf = new StringBuffer("");
            buf.append(cell.getNomeFisico());
            buf.append("=");
            JdbcParametro lPar = (JdbcParametro) riga.get(cell.getNCampo());
            buf.append(lPar.toString(true));
            valCampi.add(buf.toString());
          }
        }
      }
      keys = UtilityTags.arrayToString(valCampi.toArray());

    }
    // Estraggo la chiave dell'elemento
    this.pageContext.setAttribute(UtilityTags.REQUEST_VAR_KEYS, keys,
        PageContext.REQUEST_SCOPE);
    this.pageContext.setAttribute(UtilityTags.REQUEST_VAR_JAVA_KEYS,
        UtilityTags.replaceWithEliminatore(keys, "'"),
        PageContext.REQUEST_SCOPE);
    // Aggiungo i dati delle riga
    HashMap datiRiga = new HashMap();
    if (this.isDatiRequest()) {
      // Se si tratta di dati da variabile setto tutti i dati
      if (riga != null)
        for (int i = 0; i < riga.size(); i++) {

          datiRiga.put("C" + String.valueOf(i + 1), riga.get(i) != null
              ? riga.get(i).toString()
              : "");
          datiRiga.put("OBJ" + String.valueOf(i + 1), riga.get(i));
        }
    } else {
      for (int nCella = 0; nCella < this.getAttributes().getCells().size(); nCella++) {
        if (this.getCella(nCella).isCampo()) {
          // Aggiungo C davanti se si tratta di lista da dati
          datiRiga.put(this.getCella(nCella).getNome(),
              this.getCella(nCella).getValue());
        }
      }
    }
    // Verifico se c'è il paginatore
    if (this.getAttributes().getPaginator() != null) {
      PaginatoreListaImpl pag = this.getAttributes().getPaginator();
      // Se c'è un paginatore allora calcolo il numero della riga
      datiRiga.put("rowCount", new Long(pag.getRowCount()));
      datiRiga.put("row", new Long(pag.getCurPage()
          * pag.getNumRowForPage()
          + getAttributes().getCurrentRow()
          + 1));
    } else {
      if (this.getAttributes().getValori() != null) {
        datiRiga.put("rowCount", new Long(
            this.getAttributes().getValori().size()));
        datiRiga.put("row", new Long(getAttributes().getCurrentRow() + 1));
      }
    }

    this.pageContext.setAttribute(UtilityTags.REQUEST_VAR_DATI_RIGA, datiRiga,
        PageContext.REQUEST_SCOPE);
    // {M.F. 03.11.2006} Aggiungo il settaggio dei dati dell'archivio
    if (this.getAttributes().getArchivio() != null) {
      this.pageContext.setAttribute(UtilityTags.REQUEST_VAR_ARCHIVIO_ARRAY_JS,
          getAttributes().getArchivio().getJsArrayFromValues(datiRiga),
          PageContext.REQUEST_SCOPE);
    }
  }

  /**
   * @return Returns the where.
   */
  public String getWhere() {
    return getAttributes().getWhere();
  }

  /**
   * @param where
   *        The where to set.
   */
  public void setWhere(String where) {
    this.getAttributes().setWhere(where);
  }

  /**
   * @return Returns the where.
   */
  public String getDistinct() {
    return getAttributes().getDistinct();
  }

  /**
   * @param where
   *        The where to set.
   */
  public void setDistinct(String distinct) {
    this.getAttributes().setDistinct(distinct);
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
   * @return Returns the pathScheda.
   */
  public String getPathScheda() {
    return getAttributes().getPathScheda();
  }

  /**
   * @param pathScheda
   *        The pathScheda to set.
   */
  public void setPathScheda(String pathScheda) {
    this.getAttributes().setPathScheda(pathScheda);
  }

  /**
   * @return Returns the pathSchedaPopUp.
   */
  public String getPathSchedaPopUp() {
    return getAttributes().getPathSchedaPopUp();
  }

  /**
   * @param pathSchedaPopUp
   *        The pathSchedaPopUp to set.
   */
  public void setPathSchedaPopUp(String pathSchedaPopUp) {
    this.getAttributes().setPathSchedaPopUp(pathSchedaPopUp);
  }

  /**
   * @return Returns the select.
   */
  public String getSql() {
    return getAttributes().getSelect();
  }

  /**
   * @param select
   *        The select to set.
   */
  public void setSql(String select) {
    this.getAttributes().setSelect(select);
  }

  /**
   * Funzione che estrae un decoratore
   *
   * @param tag
   *        Tag da cui estrarre il decoratore
   * @param decoratore
   *        Decoratore
   * @return
   */
  public CampoListaTagImpl getDecoratore(CampoListaTag tag,
      CampoListaTagImpl decoratore) {
    if (tag.getNCella() < 0) tag.setNCella(this.getNextCella());
    if (getCella(tag.getNCella()) == null) {
      decoratore = new CampoListaTagImpl();
      decoratore.setJs(this.getJavascript());
      this.addCell(decoratore);
      tag.setDecoratore(decoratore);
    }

    CampoListaTagImpl ret = getCella(tag.getNCella());
    if (decoratore == null) tag.setDecoratore(ret);
    return ret;
  }

  /**
   * @return Returns the sortColumn.
   */
  public String getSortColumn() {
    return getAttributes().getSortColumn();
  }

  /**
   * @param sortColumn
   *        The sortColumn to set.
   */
  public void setSortColumn(String sortColumn) {
    this.getAttributes().setSortColumn(sortColumn);
  }

  /**
   * @return Returns the cells.
   */
  protected Vector getCells() {
    return getAttributes().getCells();
  }

  /**
   * @return Returns the inserisciDaArchivio.
   */
  public boolean isInserisciDaArchivio() {
    return getAttributes().isInserisciDaArchivio();
  }

  /**
   * @param inserisciDaArchivio
   *        The inserisciDaArchivio to set.
   */
  public void setInserisciDaArchivio(boolean inserisciDaArchivio) {
    this.getAttributes().setInserisciDaArchivio(inserisciDaArchivio);
  }

  @Override
  public TagAttributes newTagAttributes() {
    return new FormListaAttributes(this.getTipoVar());
  }

  public String getVarName() {
    return this.getAttributes().getVarName();
  }

  public void setVarName(String varDati) {
    this.getAttributes().setVarName(varDati);
  }

  public boolean isDatiRequest() {
    return this.getAttributes().isDatiRequest();
  }

  private void setDatiRequest(boolean datiRequest) {
    this.getAttributes().setDatiRequest(datiRequest);
  }

  public boolean isGestisciProtezioni() {
    return this.getAttributes().isGestisciProtezioni();
  }

  public void setGestisciProtezioni(boolean gestisciProtezioni) {
    this.getAttributes().setGestisciProtezioni(gestisciProtezioni);
  }

  public boolean isGestisciProtezioniRighe() {
    return this.getAttributes().isGestisciProtezioniRighe();
  }

  public void setGestisciProtezioniRighe(boolean gestisciProprietaRighe) {
    this.getAttributes().setGestisciProtezioniRighe(gestisciProprietaRighe);
  }

}
