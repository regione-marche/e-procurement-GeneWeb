/*
 * Created on 26/ott/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.utils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;

public class SetIdPaginaTag extends TagSupportGene {

  /**
   * 
   */
  private static final long serialVersionUID = -6279848075327063444L;
  private String schema;
  private String maschera;
  private String pagina;

  public int doStartTag() throws JspException {
    if (DizionarioSchemi.getInstance().get(getSchema()) == null)
      throw new JspException("gene:setIdPagina lo schema: "
          + getSchema()
          + " non esiste nei metadati !");
    // Setto nel request il nome dello schema e l'identificativo della
    // maschera
    this.getPageContext().setAttribute(UtilityTags.REQUEST_SCHEMA,
        this.getSchema(), PageContext.REQUEST_SCOPE);
    this.getPageContext().setAttribute(UtilityTags.REQUEST_ID_MASCHERA,
        this.getMaschera(), PageContext.REQUEST_SCOPE);
    this.getPageContext().setAttribute(UtilityTags.REQUEST_ID_PAGINA,
        this.getPagina(), PageContext.REQUEST_SCOPE);

    return SKIP_BODY;
  }

  /**
   * @return the maschera
   */
  public String getMaschera() {
    return maschera;
  }

  /**
   * @param maschera
   *        the maschera to set
   */
  public void setMaschera(String maschera) {
    this.maschera = maschera;
  }

  /**
   * @return the pagina
   */
  public String getPagina() {
    return pagina;
  }

  /**
   * @param pagina
   *        the pagina to set
   */
  public void setPagina(String pagina) {
    this.pagina = pagina;
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

}
