package it.eldasoft.gene.tags;

import it.eldasoft.gene.tags.js.Javascript;
import it.eldasoft.gene.tags.utils.UtilityTags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Classe da cui ereditano tutti i tag generali. in questa classe sono aggiunte
 * le proprietà generali
 * 
 * @author marco.franceschin
 * 
 */
public class TagSupportGene extends TagSupport implements TagGeneInterface {

  // ///////////////////////////////////////////////////////////////
  // Modifiche
  // ----------------
  // 22/02/2007 M.F. Gestione degli attributi sempre nel request
  // ///////////////////////////////////////////////////////////////
  /**
   * 
   */
  private static final long serialVersionUID = 4176886758486085666L;

  /** Nome come deve iniziare il nome creato in automatico */
  private String            tipoVar;

  public TagSupportGene() {
    // Chiamo il costruttore con vuoto
    this(null);
  }

  /**
   * Creazione con il nome del tipo di variabile
   * 
   * @param asTipoVar
   *        inizio del nome della variabile
   */
  public TagSupportGene(String asTipoVar) {
    super();
    id = null;
    // Se l'identificativo non è stato settato allora lo ricava dal request
    if (asTipoVar == null || asTipoVar.length() == 0)
      tipoVar = "geneTag";
    else
      this.tipoVar = asTipoVar;
  }

  /**
   * Funzione che imposta l'identificativo standard aggiungendo il numero
   * progressivo
   * 
   * @param aPageContext
   */
  protected void impostaStandardId() {
    if (this.pageContext != null) {
      // Di default assegna un id progressivo nella request
      if (this.pageContext.getAttribute(UtilityTags.REQUEST_PROGRESSIVO,
          PageContext.REQUEST_SCOPE) == null)
        this.pageContext.setAttribute(UtilityTags.REQUEST_PROGRESSIVO,
            new Integer(0), PageContext.REQUEST_SCOPE);
      // Estraggo il nuovo numero

      int prog = ((Integer) this.pageContext.getAttribute(
          UtilityTags.REQUEST_PROGRESSIVO, PageContext.REQUEST_SCOPE)).intValue() + 1;
      this.pageContext.setAttribute(UtilityTags.REQUEST_PROGRESSIVO,
          new Integer(prog), PageContext.REQUEST_SCOPE);
      // Setto l'identificativo del tag
      this.id = this.tipoVar + String.valueOf(prog);
      this.getAttributeManager().setStandardId(this.id);
    }
  }

  /**
   * Aggiungo il settaggio dell'id se non settato
   */
  public void setPageContext(PageContext aPageContext) {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 25/09/2006 M.F. Prima Versione
    // 28/09/2006 M.F. Se non esiste aggiungo la classe che gestisce i
    // javascript alla fine della pagina
    // 22/03/2007 M.F. Spostato il settaggio del page context all'inizio
    // ************************************************************
    super.setPageContext(aPageContext);
    // Aggiungo l'ogggetto con gli attributi del tag corrente
    UtilityTags.addToStackTagAttributes(aPageContext, this.newTagAttributes());
    // Creo l'oggetto attributi
    // Se non ancora settato setto l'identificativo del tag
    if (this.id == null || this.id.length() == 0) {
      this.impostaStandardId();
    }

  }

  /**
   * Funzione che restituisce l'oggetto con i javascript
   * 
   * @return
   */
  public Javascript getJavascript() {
    return UtilityTags.getJavascript(this.pageContext);
  }

  /**
   * @return Returns the contextPath.
   */
  public String getContextPath() {
    if (this.pageContext != null
        && this.pageContext.getRequest() instanceof HttpServletRequest)
      return ((HttpServletRequest) this.pageContext.getRequest()).getContextPath();
    return "";
  }

  /**
   * 
   */
  public int doStartTag() throws JspException {
    // ///////////////////////////////////////////////////////////////
    // Modifiche
    // ----------------
    // 22/02/2007 M.F. Aggiunto il salvataggio nello stack dell'oggetto
    // ///////////////////////////////////////////////////////////////
    UtilityTags.addToStack(this.getPageContext(), this);
    // Setto l'iterazione come fosse al primo ciclo
    this.getAttributeManager().setNCiclo(0);
    // Aggiungo 1 al nomero d'iterazione
    if (this.getAttributeManager().getStandardId() != null
        && this.id.equals(this.getAttributeManager().getStandardId())
        && this.getAttributeManager().getNIteration() > 0)
      this.impostaStandardId();
    this.getAttributeManager().setNIteration(
        this.getAttributeManager().getNIteration() + 1);
    return super.doStartTag();
  }

  /**
   * Aggiungo l'aggiornamento del numero del ciclo
   */
  public int doAfterBody() throws JspException {
    this.getAttributeManager().setNCiclo(
        this.getAttributeManager().getNCiclo() + 1);
    return super.doAfterBody();
  }

  /**
   * Alla chiusura del tag sbianco l'attribute manager
   */
  public int doEndTag() throws JspException {
    UtilityTags.removeFromStack(this.getPageContext(), this);
    TagAttributes.removeTagFromRequest(this);
    this.id = null;
    return super.doEndTag();
  }

  /**
   * Funzione che da il numero dell'iterazione
   * 
   * @return Numero dell'iterazione
   */
  public int getNumIteration() {
    return this.getAttributeManager().getNCiclo();
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eldasoft.gene.tags.TagGeneInterface#getAttributeManager()
   */
  public TagAttributes getAttributeManager() {
    return TagAttributes.getInstance(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eldasoft.gene.tags.TagGeneInterface#getPageContext()
   */
  public PageContext getPageContext() {
    return this.pageContext;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eldasoft.gene.tags.TagGeneInterface#getTipoVar()
   */
  public String getTipoVar() {
    return this.tipoVar;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eldasoft.gene.tags.TagGeneInterface#newTagAttributes()
   */
  public TagAttributes newTagAttributes() {
    return new TagAttributes(this.tipoVar);
  }

  /**
   * Funzione che restituisce il padre e se non lo trova tra i padri lo ricerca
   * nello stack salvato nel request. Come prima cosa lo ricerco nel request
   * 
   * @param classe
   * @return
   */
  public Tag getParent(Class classe) {
    // Se non è stato trovato allora lo ricerco nello stack
    Object obj = UtilityTags.getFromStack(this.getPageContext(), classe);
    if (obj instanceof Tag) return (Tag) obj;
    return findAncestorWithClass(this, classe);
  }

}
