package it.eldasoft.gene.tags.templates;

import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;

import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

/**
 * Tag che gestisce i template: tutti i template si trovano sulla directory
 * \WEB-INF\pages\templates (assunto come default)
 * 
 * @author marco.franceschin
 */
public class JspTemplateTag extends TagSupportGene {

  static Logger             logger             = Logger.getLogger(JspTemplateTag.class);

  /**
   * UID
   */
  private static final long serialVersionUID   = 4284423857435108641L;

  /** Nome del file template da usare */
  private String            file;

  /** blocchi definiti con il tipo di template blocks */
  private HashMap           blocks;

  /** Definizione di stringhe */
  private HashMap           strings;

  /** Schema concettuale della pagina */
  private String            schema             = null;

  /** Identificativo della pagina nelle protezioni */
  private String            idMaschera         = null;

  /** Flag per dire se gestire o meno le protezioni */
  private boolean           gestisciProtezioni = false;

  private void setNull() {
    this.file = null;
    this.blocks = new HashMap();
    this.strings = new HashMap();
  }

  /**
   * Costruttore della classe
   */
  public JspTemplateTag() {
    super("JspTemplateTag");
    setNull();
  }

  /**
   * Nome del file template da usare
   * 
   * @return
   */
  public String getFile() {
    return file;
  }

  /**
   * Settaggio del template da utilizzare
   * 
   * @param file
   */
  public void setFile(String file) {
    this.file = file;
  }

  /**
   * @return Returns the blocks.
   */
  public HashMap getBlocks() {
    return blocks;
  }

  public int doStartTag() throws JspException {
    super.doStartTag();
    // Verifico se sono settati i campi per la gestione delle protezioni
    if (this.isGestisciProtezioni()) {
      if (this.getSchema() == null || this.getIdMaschera() == null)
        throw new JspException(
            "Se si gestiscono le protezioni nel template deve essere settato lo schema e l'idMaschera");
      else {
        // Verifico che lo schema sia uno schema valido
        if (DizionarioSchemi.getInstance().get(this.getSchema()) == null) {
          throw new JspException("Lo schema definito nel template \""
              + this.getSchema()
              + "\" non è uno schema valido");
        }
      }

      // Setto nel request il nome dello schema e l'identificativo della
      // maschera
      this.getPageContext().setAttribute(UtilityTags.REQUEST_SCHEMA,
          this.getSchema(), PageContext.REQUEST_SCOPE);
      this.getPageContext().setAttribute(UtilityTags.REQUEST_ID_MASCHERA,
          this.getIdMaschera(), PageContext.REQUEST_SCOPE);
      // Sbianco l'identificativo della pagina (verrà settato in succassione)
      this.getPageContext().setAttribute(UtilityTags.REQUEST_ID_PAGINA, "",
          PageContext.REQUEST_SCOPE);
    }
    return EVAL_PAGE;
  }

  /**
   * Includo il tepmlate prima di chiudere il tag
   */
  public int doAfterBody() throws JspException {
    // Scrivo tutti i blocchi da scrivere
    try {
      // Alla fine includo la pagina template da includere
      String path = UtilityTags.DEFAULT_PATH_PAGINE_JSP
          + "templates/"
          + this.getFile();
      this.pageContext.include(path);
    } catch (Exception e) {
      throw new JspException(e.getMessage(), e);
    }
    // Non lo reiterpreto di default
    return EVAL_PAGE;
  }

  public int doEndTag() throws JspException {
    setNull();
    return super.doEndTag();
  }

  /**
   * @return Returns the strings.
   */
  public HashMap getStrings() {
    return strings;
  }

  /**
   * Funzione che estrae l'ultimo parent
   * 
   * @param pageContext
   * @return
   */
  public static JspTemplateTag getLastTemplateTag(PageContext pageContext) {
    return (JspTemplateTag) UtilityTags.getFromStack(pageContext,
        JspTemplateTag.class);
  }

  /**
   * @return the schema
   */
  public String getSchema() {
    return schema;
  }

  /**
   * @param schema
   *        the schema to set
   */
  public void setSchema(String schema) {
    this.schema = schema;
  }

  /**
   * @return the idMaschera
   */
  public String getIdMaschera() {
    return idMaschera;
  }

  /**
   * @param idMaschera
   *        the idMaschera to set
   */
  public void setIdMaschera(String idMaschera) {
    this.idMaschera = idMaschera;
  }

  /**
   * @return the gestisciProtezioni
   */
  public boolean isGestisciProtezioni() {
    return gestisciProtezioni;
  }

  /**
   * @param gestisciProtezioni
   *        the gestisciProtezioni to set
   */
  public void setGestisciProtezioni(boolean gestisciProtezioni) {
    this.gestisciProtezioni = gestisciProtezioni;
  }

}
