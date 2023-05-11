package it.eldasoft.gene.tags.decorators.scheda;

import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

public class PaginaPagineTag extends BodyTagSupportGene {

  /**
   *
   */
  private static final long serialVersionUID = 5407809392698845959L;

  private PaginaTagImpl     pagina;

  private String            title;

  private String            tooltip;

  private boolean           selezionabile;

  private boolean           visibile;

  /** Identificativo delle protezioni */
  private String            idProtezioni;

  private void setNull() {
    pagina = new PaginaTagImpl();
    pagina.setSelezionabile(true);
    pagina.setTitle(this.getTitle());
    pagina.setTooltip(this.getTooltip());
    pagina.setSelezionabile(this.isSelezionabile());
    pagina.setIdProtezioni(this.getIdProtezioni());
  }

  /**
   * Funzione che reimposta i valori di default
   *
   */
  private void reset() {
    this.selezionabile = true;
    this.idProtezioni = null;
    this.visibile = true;
  }

  public PaginaPagineTag() {
    reset();
  }

  /**
   * @return Returns the title.
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * @param title
   *        The title to set.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return Returns the tooltip.
   */
  public String getTooltip() {
    return this.tooltip;
  }

  /**
   * @param tooltip
   *        The tooltip to set.
   */
  public void setTooltip(String tooltip) {
    this.tooltip = tooltip;
  }

  /**
   * Inserisco la nuova pagina
   */
  @Override
  public int doStartTag() throws JspException {
    // ///////////////////////////////////////////////////////////////
    // Modifiche
    // ----------------
    // 08.10.2007: M.F. Gestione standard dei profili
    // ////////////////////////////////////////////////////////////// /

    super.doStartTag();
    FormPagineTag parent = (FormPagineTag) getParent(FormPagineTag.class);
    if (parent == null)
      throw new JspException(
          "La pagina deve trovarsi allinterno di un tag formPagine !");
    // Verifico se è settata la protezione
    if (parent.isGestisciProtezioni()) {
      if (this.getIdProtezioni() != null && this.getIdProtezioni().length() > 0) {
        // Se è visibile verifico anche le protezioni
        if (this.isVisibile()) {
          this.setVisibile(UtilityTags.checkProtection(
              this.pageContext, "PAGE", "VIS",
              UtilityTags.getIdRequest(this.getPageContext(), false)
                  + "."
                  + this.getIdProtezioni(),true));
        }
      }else{
        // Se si devono gestire le protezioni tutte le pagine devono avere un ID
        throw new JspException(
        "Se si gestiscono le protezioni deve essere impostato un idProtezioni sulla pagina !");
      }
    }
    // Inizializzo la pagina corrente
    this.setNull();

    // Se non è visibile non aggiungo la pagina
    if (!this.isVisibile()) return SKIP_BODY;
    // Aggiungo la pagina a tutte le pagine
    int idx = parent.addPg(pagina);
    // Se non è la pagina attiva non interpreto il corpo
    if (idx != parent.getActivePage()) return SKIP_BODY;
    if (!this.isSelezionabile()) {
      parent.setActivePage(idx + 1);
      return SKIP_BODY;
    }
    // {M.F.07/10/07} Aggiungo l'identificativo delle protezioni se ci troviamo
    // all'interno della pagina
    if (parent.isGestisciProtezioni()) {
      this.getPageContext().setAttribute(UtilityTags.REQUEST_ID_PAGINA,
          this.getIdProtezioni(),PageContext.REQUEST_SCOPE);
    }
    // Valuto il corpo in modo bufferizzato
    return EVAL_BODY_BUFFERED;
  }

  @Override
  public int doEndTag() throws JspException {
    BodyContent body = this.getBodyContent();
    if (body != null) {
      pagina.setBody(body.getString().trim());
      body.clearBody();
    }
    reset();
    super.doEndTag();
    return EVAL_PAGE;
  }

  /**
   * @return Returns the selezionabile.
   */
  public boolean isSelezionabile() {
    return this.selezionabile;
  }

  /**
   * @param selezionabile
   *        The selezionabile to set.
   */
  public void setSelezionabile(boolean selezionabile) {
    this.selezionabile = selezionabile;
  }

  public String getIdProtezioni() {
    return idProtezioni;
  }

  public void setIdProtezioni(String idProtezioni) {
    this.idProtezioni = idProtezioni;
  }

  public boolean isVisibile() {
    return visibile;
  }

  public void setVisibile(boolean visibile) {
    this.visibile = visibile;
  }

}
