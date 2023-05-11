package it.eldasoft.gene.tags.decorators.archivi;

import java.util.Vector;

import it.eldasoft.gene.tags.decorators.scheda.IFormScheda;
import it.eldasoft.gene.tags.utils.UtilityTags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Implementazione dell'archivio del tag. Questa classe si incarica anche
 * dell'implementazione nel passaggio nel response
 * 
 * @author cit_franceschin
 * 
 */
public class ArchivioTagImpl {

  /*
   * Costanti di passaggio nella form dell'archivio
   */

  public static final String HIDE_INPUT_TITOLO              = "archTitolo";

  public static final String HIDE_INPUT_OBBLIGATORIO        = "archObbligatorio";

  public static final String HIDE_INPUT_LISTA               = "archLista";

  public static final String HIDE_INPUT_SCHEDA              = "archScheda";

  public static final String HIDE_INPUT_SCHEDAPOPUP         = "archSchedapopup";

  public static final String HIDE_INPUT_CAMPI               = "archCampi";

  public static final String HIDE_INPUT_CAMPI_ARCHIVIO      = "archCampiArchivio";

  public static final String HIDE_INPUT_CAMPI_NO_SET        = "archCampiNoSet";

  public static final String HIDE_INPUT_CAMPO_CHANGED       = "archCampoChanged";

  public static final String HIDE_INPUT_TIPO_CAMPO_CHANGED  = "archTipoCampoChanged";

  public static final String HIDE_INPUT_VALUE_CAMPO_CHANGED = "archValueCampoChanged";

  public static final String HIDE_INPUT_CHIAVE              = "archChiave";

  public static final String HIDE_INPUT_VALUE_CHIAVE        = "archValueChiave";

  public static final String HIDE_INPUT_WHERE_SU_LISTA      = "archWhereLista";
  
  public static final String HIDE_INPUT_WHERE_PARAMETRI_SU_LISTA = "archWhereParametriLista";

  public static final String HIDE_INPUT_FUNCTION_ID         = "archFunctionId";

  public static final String HIDE_INPUT_INSERIBILE          = "archSiPuoInserire";

  public static final String HIDE_INPUT_IS_OPEN_IN_POPUP    = "archIsOpenInPopUp";

  private String             id;

  private String             titolo;

  private boolean            obbligatorio;

  private String             lista;

  private String             scheda;

  private String             schedaPopUp;

  private String             campi;

  private String             campiMaschera;

  private PageContext        pageContext;

  private String             formName;

  private String             chiave;

  private int                campoStart;

  private int                campoEnd;

  private String             where;
  
  private String             parametriWhere;
  
  private String             functionId;

  private boolean            inseribile;

  private String             body;

  private String             campiNoSet;
  
  /**
   * Flag per dire se i dati della pagina possono essere dati inputati
   * liberamente e quindi scollegati dall'archivio vero e proprio
   * @since 1.4.6
   */
  private boolean            scollegabile;

  /**
   * Necessario per calcolare se un archivio è collegato o meno con un dato nel
   * db
   * @since 1.4.6
   */
  private Vector             elencoCampiSchedaTag;

  public ArchivioTagImpl(ArchivioTag parent, IFormScheda form,
      PageContext context, String body) {
    this.titolo = parent.getTitolo();
    this.obbligatorio = parent.isObbligatorio();
    this.lista = parent.getLista();
    this.scheda = parent.getScheda();
    this.schedaPopUp = parent.getSchedaPopUp();
    this.campi = parent.getCampi();
    this.campiMaschera = parent.getCampiMaschera();
    this.id = parent.getId();
    this.pageContext = context;
    this.formName = form.getFormName();
    this.chiave = parent.getChiave();
    this.campoStart = parent.getCampoStart();
    this.campoEnd = parent.getCampoEnd();
    this.where = parent.getWhere();
    this.parametriWhere = parent.getParametriWhere();
    this.functionId = parent.getFunctionId();
    this.inseribile = parent.isInseribile();
    if (body == null) body = "";
    this.body = body;
    this.campiNoSet = parent.getCampiNoSet();
    this.scollegabile = parent.isScollegabile();
    this.elencoCampiSchedaTag = parent.getElencoCampiSchedaTag();
  }

  /**
   * @return Returns the campi.
   */
  public String getCampi() {
    return campi;
  }

  /**
   * @return Returns the campiMaschera.
   */
  public String getCampiMaschera() {
    return campiMaschera;
  }
  
  /**
   * @return Returns the lista.
   */
  public String getLista() {
    return lista;
  }

  /**
   * @return Returns the obbligatorio.
   */
  public boolean isObbligatorio() {
    return obbligatorio;
  }

  /**
   * @return Returns the scheda.
   */
  public String getScheda() {
    return scheda;
  }

  /**
   * @return Returns the schedaPopUp.
   */
  public String getSchedaPopUp() {
    return schedaPopUp;
  }

  /**
   * @return Returns the titolo.
   */
  public String getTitolo() {
    return titolo;
  }

  /**
   * @return Returns the id.
   */
  public String getId() {
    return id;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer("");
    ArchivioRequest reqArchivio = new ArchivioRequest(this);
    buf.append(reqArchivio.toStringForm(this.pageContext, this.getId()));

    return buf.toString();
  }

  public String getCreateJsObject() throws JspException {
    String campiArchivio[] = UtilityTags.stringToArray(this.getCampi(), ';');
    String campiScheda[] = UtilityTags.stringToArray(this.getCampiMaschera(),
        ';');
    if (campiArchivio.length != campiScheda.length)
      throw new JspException(
          "Il tag Archivio il numero dei campi dell'archivio deve essere uguale al numero dei campi della scheda !");
    StringBuffer buf = new StringBuffer("");
    buf.append("new ArchivioObj( \"");
    buf.append(this.getId());
    buf.append("\",");
    buf.append(formName);
    buf.append(",\"");
    buf.append(UtilityTags.convStringa(this.getLista()));
    buf.append("\",\"");
    buf.append(UtilityTags.convStringa(this.getScheda()));
    buf.append("\",\"");
    buf.append(UtilityTags.convStringa(this.getSchedaPopUp()));
    buf.append("\",new Array( ");
    for (int i = 0; i < campiArchivio.length; i++) {
      if (i > 0) buf.append(",");
      buf.append("\"");
      buf.append(UtilityTags.convStringa(campiArchivio[i]));
      buf.append("\",\"");
      buf.append(UtilityTags.convStringa(campiScheda[i]));
      buf.append("\"");
    }
    buf.append(")");
    buf.append(",new Array( ");
    String chiavi[] = UtilityTags.stringToArray(this.chiave, ';');
    for (int i = 0; i < chiavi.length; i++) {
      if (i > 0) buf.append(",");
      buf.append("\"");
      buf.append(UtilityTags.convStringa(chiavi[i]));
      buf.append("\"");
    }
    buf.append(")");

    // SABBADIN 04/03/2010 (1.4.6)
    // campiNoSet non è più l'ultimo parametro passato ad ArchivioObj, bensì
    // viene aggiunto anche il parametro scollegabile; di conseguenza viene 
    // aggiunto l'else all'if seguente, in modo da rispettare il numero di
    // parametri della chiamata
    if (this.campiNoSet != null && this.campiNoSet.length() > 0) {
      buf.append(",\";");
      buf.append(UtilityTags.convStringa(this.campiNoSet));
      buf.append(";\"");
    } else {
      buf.append(",null");
    }

    buf.append(",").append(this.isScollegabile());
    buf.append(");");
    return buf.toString();
  }

  /**
   * @return Returns the chiave.
   */
  public String getChiave() {
    return chiave;
  }

  /**
   * @return Returns the campoEnd.
   */
  public int getCampoEnd() {
    return campoEnd;
  }

  /**
   * @return Returns the campoStart.
   */
  public int getCampoStart() {
    return campoStart;
  }
  
  /**
   * Funzione che da l'inizio dell'archivio
   * 
   * @param ncampo
   *        numero campo
   * @return Se il campo è l'inizio dell'archivio allora restiuisce l'inizio
   */
  public String getHTMLStartArchivio(int ncampo) {

    if (ncampo == this.getCampoStart()) {
      return "";
      /*
       * StringBuffer buf = new StringBuffer(""); // Il campo è quello giusto
       * //buf.append("<tr><td colspan=\"2\">\n\t<table width=\"100%\">\n");
       * buf .append("<tr><td colspan=\"2\" > <i>Archivio: </i>");
       * buf.append(UtilityStringhe.capitalize(this.getTitolo())); buf.append("</td></tr>\n");
       * return buf.toString();
       */
    }
    return null;

  }

  /**
   * Funzione che restituisce la fine dell'archivio
   * 
   * @param ncampo
   *        numero campo
   * @return se il campo è alla fine dell'archivio allora restituisce la fine
   */
  public String getHTMLEndArchivio(int ncampo) {

    if (ncampo == this.getCampoEnd()) {
      return "";
      /*
       * StringBuffer buf = new StringBuffer(""); // Il campo è quello giusto
       * 
       * buf .append("<tr><td colspan=\"2\" height=\"5\"></td></tr>\n");
       * //buf.append("</table></td></tr>\n"); return buf.toString();
       */
    }

    return null;
  }

  /**
   * @return Returns the where.
   */
  public String getWhere() {
    return where;
  }
  
  /**
   * @return Returns the parametriWhere.
   */
  public String getParametriWhere() {
    return parametriWhere;
  }
  
  /**
   * @return Returns the function ID.
   */
  public String getFunctionId() {
    return functionId;
  }
  
  /**
   * @return Returns the inseribile.
   */
  public boolean isInseribile() {
    return inseribile;
  }

  /**
   * @return Returns the body.
   */
  public String getBody() {
    return body;
  }

  
  /**
   * @return the campiNoSet
   */
  public String getCampiNoSet() {
    return campiNoSet;
  }

  
  /**
   * @return Ritorna formName.
   */
  protected String getFormName() {
    return formName;
  }

  
  /**
   * @return Ritorna scollegabile.
   */
  public boolean isScollegabile() {
    return scollegabile;
  }

  
  /**
   * @param scollegabile scollegabile da settare internamente alla classe.
   */
  public void setScollegabile(boolean scollegabile) {
    this.scollegabile = scollegabile;
  }

  
  /**
   * @return Ritorna elencoCampiSchedaTag.
   */
  public Vector getElencoCampiSchedaTag() {
    return elencoCampiSchedaTag;
  }

}