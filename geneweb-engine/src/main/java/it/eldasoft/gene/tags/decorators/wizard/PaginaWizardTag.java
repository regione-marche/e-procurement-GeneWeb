/*
 * Created on 21-apr-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.wizard;

import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

/**
 * Tag che consente la definizione di una pagina di un wizard
 * 
 * @author stefano.sabbadin
 */
public class PaginaWizardTag extends BodyTagSupportGene {

  /**
   * UID
   */
  private static final long serialVersionUID = 1281697273697287836L;

  /** Reference al bean che identifica la pagina del wizard */
  private PaginaWizardImpl  pagina;

  /** Titolo della pagina */
  private String            title;

  /** Indica se la pagina del wizard deve essere visibile o meno */
  private boolean           visibile;

  /** Identificativo delle protezioni */
  private String            idProtezioni;

  public PaginaWizardTag() {
    this.reset();
  }

  /**
   * Funzione che reimposta i valori di default
   */
  private void reset() {
    this.title = null;
    this.idProtezioni = null;
    this.visibile = true;
  }

  /**
   * Inizializza il tag per la gestione di una nuova pagina
   */
  private void setNuovaPagina() {
    pagina = new PaginaWizardImpl();
    pagina.setTitle(this.getTitle());
  }

  /**
   * @return Ritorna title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title
   *        title da settare internamente alla classe.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return Ritorna visibile.
   */
  public boolean isVisibile() {
    return visibile;
  }

  /**
   * @param visibile
   *        visibile da settare internamente alla classe.
   */
  public void setVisibile(boolean visibile) {
    this.visibile = visibile;
  }

  /**
   * @return Ritorna idProtezioni.
   */
  public String getIdProtezioni() {
    return idProtezioni;
  }

  /**
   * @param idProtezioni
   *        idProtezioni da settare internamente alla classe.
   */
  public void setIdProtezioni(String idProtezioni) {
    this.idProtezioni = idProtezioni;
  }

  /**
   * Inserisco la nuova pagina
   */
  public int doStartTag() throws JspException {
    super.doStartTag();
    WizardTag parent = (WizardTag) getParent(WizardTag.class);
    if (parent == null)
      throw new JspException(
          "La pagina deve trovarsi all'interno di un tag wizard!");
    // Verifico se è settata la protezione
    if (parent.isGestisciProtezioni()) {
      if (this.getIdProtezioni() != null && this.getIdProtezioni().length() > 0) {
        // Se è visibile verifico anche le protezioni
        if (this.isVisibile()) {
          this.setVisibile(UtilityTags.checkProtection(this.pageContext, "PAGE",
              "VIS", UtilityTags.getIdRequest(this.getPageContext(), false)
                  + "."
                  + this.getIdProtezioni(), true));
        }
      } else {
        // Se si devono gestire le protezioni tutte le pagine devono avere un ID
        throw new JspException(
            "Se si gestiscono le protezioni deve essere impostato un idProtezioni sulla pagina!");
      }
    }
    // Inizializzo la pagina corrente
    this.setNuovaPagina();

    // Se non è visibile non aggiungo la pagina
    if (!this.isVisibile()) return SKIP_BODY;

    // Aggiungo la pagina
    int idx = parent.addPg(pagina);
    // Se non è la pagina attiva non interpreto il corpo
    if (idx != parent.getActivePage()) return SKIP_BODY;

    // Aggiungo l'identificativo delle protezioni se ci troviamo
    // all'interno della pagina
    if (parent.isGestisciProtezioni()) {
      this.getPageContext().setAttribute(UtilityTags.REQUEST_ID_PAGINA,
          this.getIdProtezioni(), PageContext.REQUEST_SCOPE);
    }
    // Valuto il corpo in modo bufferizzato
    return EVAL_BODY_BUFFERED;
  }

  public int doEndTag() throws JspException {
    BodyContent body = this.getBodyContent();
    if (body != null) {
      pagina.setBody(body.getString().trim());
      body.clearBody();
    }
    this.reset();
    super.doEndTag();
    return EVAL_PAGE;
  }
  
  public void setSottoPagina(int sottoPagina) {
    this.pagina.setSottoPagina(sottoPagina);
  }

}
