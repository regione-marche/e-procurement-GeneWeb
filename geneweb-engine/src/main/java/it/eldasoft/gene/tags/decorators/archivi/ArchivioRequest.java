package it.eldasoft.gene.tags.decorators.archivi;

import java.util.HashMap;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Clase che gestisce l'archivio nel request
 * 
 * @author cit_franceschin
 * 
 */
public class ArchivioRequest {

  public static final String REQUEST_VAR_ARCHIVIO = "archivioReq";

  private String             titolo;

  private boolean            obbligatorio;

  private String             campiArchivio;

  private String             campiScheda;

  private String             campoChanged;

  private String             valueCampoChanged;

  private String             chiavi;

  private String             valueChiavi;

  private String             tipoCampoChanged;

  private String             where;

  private String             lista;
  private String             scheda;
  private String             schedaPopUp;
  /** Flag che dice se è possibile o meno inserire nell'archivio */
  private boolean            inseribile;

  private String             body                 = "";
  
  private String             campiNoSet;

  private void setValoriDaRequest(HttpServletRequest request) {
    this.titolo = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_TITOLO);
    this.obbligatorio = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_OBBLIGATORIO).equals("1");
    this.campiArchivio = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_CAMPI_ARCHIVIO);
    this.campiScheda = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_CAMPI);
    this.campoChanged = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_CAMPO_CHANGED);
    this.valueCampoChanged = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_VALUE_CAMPO_CHANGED);
    this.tipoCampoChanged = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_TIPO_CAMPO_CHANGED);
    this.chiavi = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_CHIAVE);
    this.valueChiavi = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_VALUE_CHIAVE);
    this.where = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_WHERE_SU_LISTA);
    this.lista = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_LISTA);
    this.scheda = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_SCHEDA);
    this.schedaPopUp = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_SCHEDAPOPUP);
    this.inseribile = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_INSERIBILE).equals("1");
    this.campiNoSet=UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_CAMPI_NO_SET);
  }

  public ArchivioRequest(HttpServletRequest request) {
    this.setValoriDaRequest(request);
  }

  /**
   * Costruttore per la creazione dell'oggetto partendo dal request
   * 
   * @param request
   *        Request dell'azione
   * @param lista
   *        Flag per dire se si tratta dell'apertura per la visualizzazione di
   *        una lista
   */
  public ArchivioRequest(HttpServletRequest request, boolean lista) {

    setValoriDaRequest(request);
    // Estraggo tutti i dati
    if (lista) {
      String where = "";
      // Se si tratta di una lista allora aggiungo la where per la
      // selezione
      if (this.campoChanged != null) {
        // Verifico il tipo di campo
        String tipoCampo = this.tipoCampoChanged;
        where = this.campoChanged + " = ?";
        if (tipoCampo == null) tipoCampo = "T";
        // Solo con i tipo testo faccio con il like
        if (tipoCampo.charAt(0) == JdbcParametro.TIPO_TESTO) {
          where = ((SqlManager)UtilitySpring.getBean("sqlManager",request.getSession().getServletContext(),SqlManager.class)).getDBFunction("UPPER")+"( " + this.campoChanged + " ) like ?";
          if (UtilityStringhe.containsSqlWildCards(this.valueCampoChanged)) {
            this.valueCampoChanged = UtilityStringhe.escapeSqlString(this.valueCampoChanged);
            try {
              SqlComposer composer = it.eldasoft.utils.sql.comp.SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
              where += " " + composer.getEscapeSql();
            } catch (SqlComposerException e) {
              // non si verifica mai, il caricamento metadati gia' testa che la
              // property sia settata correttamente
            }
          }
          this.valueCampoChanged = "%" + this.valueCampoChanged.toUpperCase() + "%";
        }
        if (this.getWhere() != null && this.getWhere().trim().length() > 0) {
          where = where.trim();
          if (where.length() > 0)
            where += " and (" + this.getWhere() + ")";
          else
            where = this.getWhere();
        }
        // Valore del campo modificato
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA,
            this.valueCampoChanged);
      } else {
        // Se si ha una where l'aggiungo
        if (this.getWhere() != null && this.getWhere().trim().length() > 0) {
          where = this.getWhere().trim();
          // Valore del campo modificato
          request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA,
              "");
        }
      }
      // Ora setto i parametri per il filtro sulla lista
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
      // Aggiungo l'oggetto al request per il passaggio alla lista
      request.setAttribute(REQUEST_VAR_ARCHIVIO, this);
    } else {
      // Se si ha una where l'aggiungo
      if (this.getWhere() != null && this.getWhere().trim().length() > 0) {
        // Ora setto i parametri per il filtro sulla lista
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA,
            this.getWhere().trim());
        // Valore del campo modificato
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, "");
      }
    }
  }

  public ArchivioRequest(ArchivioTagImpl impl) {
    this.titolo = impl.getTitolo();
    this.obbligatorio = impl.isObbligatorio();
    this.campiArchivio = impl.getCampi();
    this.campiNoSet= impl.getCampiNoSet();
    this.campiScheda = impl.getCampiMaschera();
    this.chiavi = impl.getChiave();
    this.where = impl.getWhere();
    this.lista = impl.getLista();
    this.scheda = impl.getScheda();
    this.schedaPopUp = impl.getSchedaPopUp();
    this.inseribile = impl.isInseribile();

    // Sbianco i valori che detivano da cambiamenti nella pagina
    this.campoChanged = "";
    this.valueCampoChanged = "";
    this.tipoCampoChanged = "";
    this.valueChiavi = "";
    this.body=impl.getBody();
  }

  public String toStringForm(PageContext context, String id) {
    StringBuffer buf = new StringBuffer();
    buf.append("<form ");
    buf.append(UtilityTags.getHtmlAttrib("id", id));
    buf.append(UtilityTags.getHtmlAttrib("name", id));
    buf.append(UtilityTags.getHtmlAttrib("action",
        ((HttpServletRequest) context.getRequest()).getContextPath()
            + "/Archivio.do"));
    buf.append(UtilityTags.getHtmlAttrib("method", "post"));
    buf.append(">\n");
    buf.append(this.getBody());
    buf.append(UtilityTags.getHtmlHideInput("metodo", "lista"));
    // Aggiungo gli hidden di default
    buf.append(UtilityTags.getHtmlDefaultHidden(context));
    // Salvo anche il modo perche se modifica o nuovo allora
    buf.append(UtilityTags.getHtmlHideInputFromParam(context,
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO));
    buf.append(this.toString());
    // {M.F. 23.11.2006} Aggiungo l'higgen che imposta se è stato aperto in
    // popup
    buf.append(UtilityTags.getHtmlHideInput(
        ArchivioTagImpl.HIDE_INPUT_IS_OPEN_IN_POPUP, ""));
    // Aggiungo tutti i campi dell'archivio
    buf.append("</form>");
    return buf.toString();
  }

  public String toString() {
    StringBuffer buf = new StringBuffer("");

    buf.append(UtilityTags.getHtmlHideInput(ArchivioTagImpl.HIDE_INPUT_TITOLO,
        this.titolo));
    // Aggiungo tutte le proprietà
    buf.append(UtilityTags.getHtmlHideInput(
        ArchivioTagImpl.HIDE_INPUT_OBBLIGATORIO, this.isObbligatorio()
            ? "1"
            : "0"));
    buf.append(UtilityTags.getHtmlHideInput(ArchivioTagImpl.HIDE_INPUT_LISTA,
        this.lista));
    buf.append(UtilityTags.getHtmlHideInput(ArchivioTagImpl.HIDE_INPUT_SCHEDA,
        this.scheda));
    buf.append(UtilityTags.getHtmlHideInput(
        ArchivioTagImpl.HIDE_INPUT_SCHEDAPOPUP, this.schedaPopUp));
    buf.append(UtilityTags.getHtmlHideInput(ArchivioTagImpl.HIDE_INPUT_CAMPI,
        this.campiScheda));
    buf.append(UtilityTags.getHtmlHideInput(
        ArchivioTagImpl.HIDE_INPUT_CAMPI_ARCHIVIO, this.campiArchivio));
    buf.append(UtilityTags.getHtmlHideInput(
        ArchivioTagImpl.HIDE_INPUT_CAMPI_NO_SET, this.campiNoSet));
    buf.append(UtilityTags.getHtmlHideInput(ArchivioTagImpl.HIDE_INPUT_CHIAVE,
        this.chiavi));
    buf.append(UtilityTags.getHtmlHideInput(
        ArchivioTagImpl.HIDE_INPUT_VALUE_CHIAVE, this.valueChiavi));
    buf.append(UtilityTags.getHtmlHideInput(
        ArchivioTagImpl.HIDE_INPUT_TIPO_CAMPO_CHANGED, this.tipoCampoChanged));
    buf.append(UtilityTags.getHtmlHideInput(
        ArchivioTagImpl.HIDE_INPUT_CAMPO_CHANGED, this.campoChanged));
    buf.append(UtilityTags.getHtmlHideInput(
        ArchivioTagImpl.HIDE_INPUT_VALUE_CAMPO_CHANGED, this.valueCampoChanged));
    buf.append(UtilityTags.getHtmlHideInput(
        ArchivioTagImpl.HIDE_INPUT_WHERE_SU_LISTA, this.where));
    // {M.F. 22.11.2006} Aggiungo il flag di inseribile
    buf.append(UtilityTags.getHtmlHideInput(
        ArchivioTagImpl.HIDE_INPUT_INSERIBILE, this.isInseribile() ? "1" : "0"));
    
    return buf.toString();
  }

  /**
   * Funzione che restituisce il javascript con l'elenco dei valori per il
   * passaggio alla funzione di copia
   * 
   * @param valoriCampi
   * @return String il create dell'array in javascript
   */
  public String getJsArrayFromValues(HashMap<?,?> valoriCampi) {
    StringBuffer buf = new StringBuffer("");
    buf.append("new Array(");
    String campiArch[] = UtilityTags.stringToArray(this.getCampiArchivio(), ';');
    for (int i = 0; i < campiArch.length; i++) {
      String nomeCampo = StringUtils.replace(campiArch[i], ".", "_");
      String val = (String) valoriCampi.get(nomeCampo);
      if (val == null) val = "";
      if (i > 0) buf.append(",");
      buf.append("\"");
      buf.append(UtilityTags.convStringa(val));
      buf.append("\"");
    }
    buf.append(")");
    return UtilityTags.convStringaHREFforJS(buf.toString());
  }

  /**
   * @return Returns the campiArchivio.
   */
  public String getCampiArchivio() {
    return campiArchivio;
  }

  /**
   * @return Returns the campiScheda.
   */
  public String getCampiScheda() {
    return campiScheda;
  }

  /**
   * @return Returns the campoChanged.
   */
  public String getCampoChanged() {
    return campoChanged;
  }

  /**
   * @return Returns the chiavi.
   */
  public String getChiavi() {
    return chiavi;
  }

  /**
   * @return Returns the obbligatorio.
   */
  public boolean isObbligatorio() {
    return obbligatorio;
  }

  /**
   * @return Returns the tipoCampoChanged.
   */
  public String getTipoCampoChanged() {
    return tipoCampoChanged;
  }

  /**
   * @return Returns the valueCampoChanged.
   */
  public String getValueCampoChanged() {
    return valueCampoChanged;
  }

  /**
   * @return Returns the valueChiavi.
   */
  public String getValueChiavi() {
    return valueChiavi;
  }

  /**
   * @return Returns the rEQUEST_VAR_ARCHIVIO.
   */
  public static String getREQUEST_VAR_ARCHIVIO() {
    return REQUEST_VAR_ARCHIVIO;
  }

  /**
   * @return Returns the where.
   */
  public String getWhere() {
    return where;
  }

  public static ArchivioRequest getArchivio(PageContext pageContext) {
    HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
    if (req.getAttribute(ArchivioRequest.REQUEST_VAR_ARCHIVIO) instanceof ArchivioRequest) {
      return (ArchivioRequest) req.getAttribute(ArchivioRequest.REQUEST_VAR_ARCHIVIO);
    }
    if (UtilityTags.getParametro(pageContext,
        ArchivioTagImpl.HIDE_INPUT_OBBLIGATORIO) != null) {
      return new ArchivioRequest(req);
    }
    return null;
  }

  /**
   * @return Returns the inseribile.
   */
  public boolean isInseribile() {
    return inseribile;
  }

  /**
   * @return Returns the titolo.
   */
  public String getTitolo() {
    return titolo;
  }

  
  /**
   * @return Returns the body.
   */
  public String getBody() {
    return body;
  }
}
