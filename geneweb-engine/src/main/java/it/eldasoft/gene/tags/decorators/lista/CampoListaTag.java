package it.eldasoft.gene.tags.decorators.lista;

import it.eldasoft.gene.tags.decorators.campi.AbstractCampoBodyTag;
import it.eldasoft.gene.tags.decorators.campi.CampoDecorator;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Campo di una lista
 *
 * @author cit_franceschin
 *
 */
public class CampoListaTag extends AbstractCampoBodyTag {

  // ************************************************************
  // Storia Modifiche:
  // Data Utente Descrizione
  // 16/11/2006 M.F. Aggiungo il settaggio della larghezza del campo
  // ************************************************************

  /**
   *
   */
  private static final long serialVersionUID = -5568081056454351318L;

  private String            headerClass;

  private int               nCella;

  CampoListaTagImpl         decoratore;

  @Override
  public CampoDecorator getDecoratore() {
    FormListaTag lista = (FormListaTag) getParent(FormListaTag.class);
    if (lista != null) {
      lista.getDecoratore(this, decoratore);
    }
    return this.decoratore;
  }

  @Override
  public void setDecoratore(CampoDecorator decoratore) {
    this.decoratore = (CampoListaTagImpl) decoratore;
  }

  private void setNull() {
    this.headerClass = null;
    this.nCella = -1;
  }

  public CampoListaTag() {
    this.setNull();
  }

  @Override
  public int doStartTag() throws JspException {
    FormListaTag lista = (FormListaTag) getParent(FormListaTag.class);
    if (lista == null)
      throw new JspException(
          "Il campo della lista deve trovarsi all'interno di una lista !");
    // Setto il nome delle form
    this.getDecoratore().setFormName("local" + lista.getId());
    if (this.getEntita() == null
        && !this.isCampoFittizio()
        && !this.isComputed()) this.setEntita(lista.getEntita());
    // Se si tratta della prima interazione allora aggiungo l'header e la
    // cella
    if (lista.isFirstIteration()) {
      Campo lCampo = null;
      // Se il campo è stato settato allora lo estraggo dal dizionario
      if (this.getCampo() != null) {
        if (!lista.isDatiRequest()) {
          // La selezione sulla avviene attraverso una select
          lCampo = DizionarioCampi.getInstance().getCampoByNomeFisico(
              this.getNomeFisico());
          if (this.getDecoratore().getTitle() == null && lCampo != null)
            this.setTitle(lCampo.getDescrizioneBreve());
          this.getDecoratore().setCampo(lCampo, this.pageContext);
        } else {
          this.getDecoratore().setDefinizioneCampo(this.getDefinizione(),
              this.getPageContext());
        }

      }

      // Aggiungo la cella
      this.getDecoratore().setVisualizzazione(true);
      // {MF161106} Aggiunta del settaggio del context path
      this.getDecoratore().setPageContext(
          ((HttpServletRequest) this.pageContext.getRequest()).getContextPath());
      // Se sono impostate le protezioni a livello di lista allora le imposto
      // anche per il campo
      if (lista.isGestisciProtezioni() && !this.isSetGestisciProtezioni()) this.setGestisciProtezioni(true);
      super.doStartTag();
    }
    if (lista.isFirstIteration()) {
      // Aggiungo l'header
      // Aggiungo il titolo della colonna solo se è visibile
      if (this.getDecoratore().isVisibile())
        lista.addHeader(new CellHeaderListaTagImpl(this.getTitle(),
            this.getHeaderClass(), (CampoListaTagImpl) this.getDecoratore(),
            this.getNCella(), this.isOrdinabile()));
    }
    //return BodyTagSupport.EVAL_BODY_BUFFERED;
    ((CampoListaTagImpl) this.getDecoratore()).setAddTabellati(false);
    return lista.isFirstIteration() ? SKIP_BODY : EVAL_BODY_BUFFERED;
  }

  @Override
  public int doEndTag() throws JspException {
    // Ridireziono il bodi sul corpo della cella
    BodyContent body = getBodyContent();
    if (body != null) {
      this.getDecoratore().setBody(body.getString().trim());
      body.clearBody();
    }
    super.doEndTag();
    this.setNull();
    return BodyTagSupport.EVAL_PAGE;
  }

  /**
   * @return Returns the headerClass.
   */
  public String getHeaderClass() {
    return headerClass;
  }

  /**
   * @param headerClass
   *        The headerClass to set.
   */
  public void setHeaderClass(String headerClass) {
    this.headerClass = headerClass;
  }

  /**
   * @return Returns the nCella.
   */
  public int getNCella() {
    return nCella;
  }

  /**
   * @param cella
   *        The nCella to set.
   */
  public void setNCella(int cella) {
    nCella = cella;
  }

  /**
   * @return Returns the width.
   */
  public int getWidth() {
    return ((CampoListaTagImpl) getDecoratore()).getWidth();
  }

  /**
   * @param width
   *        The width to set.
   */
  public void setWidth(int width) {
    ((CampoListaTagImpl) getDecoratore()).setWidth(width);
  }

  /**
   * @return Returns the width.
   */
  public String getTooltip() {
    return ((CampoListaTagImpl) getDecoratore()).getTooltip();
  }

  /**
   * @param tooltip
   *        The tooltip to set.
   */
  public void setTooltip(String tooltip) {
    ((CampoListaTagImpl) getDecoratore()).setTooltip(tooltip);
  }

  public boolean isEdit() {
    return ((CampoListaTagImpl) getDecoratore()).isEdit();
  }

  public void setEdit(boolean edit) {
    ((CampoListaTagImpl) getDecoratore()).setEdit(edit);
  }

  public void setOrdinabile(boolean ordinabile) {
    ((CampoListaTagImpl) this.getDecoratore()).setOrdinabile(ordinabile);
  }

  public boolean isOrdinabile() {
    return ((CampoListaTagImpl) this.getDecoratore()).isOrdinabile();
  }

  /**
   * Imposta l'alias della tabella in caso di utilizzo di più volte la stessa tabella per estrarre record distinti.<br/>
   * <b>Attenzione: l'utilizzo &egrave; disponibile solo nel caso di form lista su campi reali, non nel caso di partenza da un contenitore di dati o da una query.</b>
   *
   * @param alias alias della tabella
   */
  public void setAlias(String alias) {
    ((CampoListaTagImpl) getDecoratore()).setAlias(alias);
  }

  /**
   * @return Ritorna l'eventuale alias della tabella
   */
  public String getAlias() {
    return ((CampoListaTagImpl) getDecoratore()).getAlias();
  }


}