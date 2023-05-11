package it.eldasoft.gene.tags.decorators.scheda;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.templates.JspTemplateTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.web.tags.tab.TabBodyImpl;
import it.eldasoft.web.tags.tab.VoceTabImpl;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

/**
 * Tag che gestisce le form a pagina
 *
 * @author marco.franceschin
 *
 */
public class FormPagineTag extends BodyTagSupportGene {

  /**   UID   */
  private static final long serialVersionUID = 90765787445161140L;

  /** Elenco delle pagine */
  Vector                    pagine;

  /** Pagina attiva */
  private int               activePage;

  /** Pagina corrente */
  private int               curPg;

  /** Flag per definire la gestione delle protezioni */
  private boolean           gestisciProtezioni;

  private void setNull() {
    this.pagine = new Vector();
    this.curPg = -1;
  }

  public FormPagineTag() {
    super("tab");
    this.setNull();
    this.activePage = 0;
    this.gestisciProtezioni = true;
  }

  public int addPg(PaginaTagImpl pagina) {
    this.curPg++;
    // Aggiungo la pagina
    pagina.setIndice(this.curPg);
    pagine.add(pagina);
    return curPg;
  }

  @Override
  public int doStartTag() throws JspException {
    super.doStartTag();
    // Settaggio della gestione della protezioni
    if (this.isGestisciProtezioni()) {
      JspTemplateTag parentTemplate = (JspTemplateTag) this.getParent(JspTemplateTag.class);
      if (parentTemplate != null) {
        this.setGestisciProtezioni(parentTemplate.isGestisciProtezioni());
      }
    }
    // Setto il modo di apertura nel request
    this.pageContext.setAttribute(UtilityTags.REQUEST_VAR_TIPO_PAGINA, "",
        PageContext.REQUEST_SCOPE);
    this.setNull();
    super.doStartTag();
    // Se settata setto la pagina attiva
    if (UtilityTags.getParametro(this.pageContext,
        UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE) != null) {
      this.setActivePage(new Integer(UtilityTags.getParametro(this.pageContext,
          UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE)).intValue());
    } else
      this.pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE,
          String.valueOf(this.getActivePage()), PageContext.REQUEST_SCOPE);
    return BodyTag.EVAL_BODY_BUFFERED;
  }

  /**
   * Alla chiusura del tag creo tutte le pagine
   */
  @Override
  public int doEndTag() throws JspException {
    boolean inModifica = UtilityTags.isInModifica(this.pageContext.getRequest());
    boolean navigazioneDisabilitata = UtilityTags.isNavigazioneDisabilitata(pageContext);
    // A questo punto creo la pagina
    StringBuffer buf = new StringBuffer();
    // Scrivo la stringa appena inserita
    TabBodyImpl tabBody = new TabBodyImpl();
    tabBody.setContextPath(this.getContextPath());
    tabBody.setImgPath((String) this.pageContext.getServletContext().getAttribute(
        CostantiGenerali.ATTR_PATH_IMG));

    // Variabili necessarie per memorizzare le varie righe di tab, e poi
    // spostare la riga contenente il tab attivo nell'ultima riga dei tab
    Vector righeTab = new Vector();
    StringBuffer rigaTemporanea = new StringBuffer();
    int indiceRigaTab = 0;
    int indiceRigaConTabAttivo = -1;

    // Inizio il tab
    rigaTemporanea.append(tabBody.toString());

    // Scorro tutte le pagine per la scrittura
    VoceTabImpl voceTab = new VoceTabImpl();
    voceTab.setTabBody(tabBody);
    // Setto gli a capo in funzione del numero di caratteri stampato
    int numChars = 0;
    for (int i = 0; i < pagine.size(); i++) {
      PaginaTagImpl pg = (PaginaTagImpl) pagine.get(i);
      if (inModifica || navigazioneDisabilitata)
        voceTab.setSelezionabile(false);
      else
        voceTab.setSelezionabile(pg.isSelezionabile());
      voceTab.setDescrizione(pg.getTitle());
      if (pg.getIdProtezioni() != null)
        voceTab.setId("tab"+pg.getIdProtezioni());

      // Per determinare la lunghezza dei tab inseriti, e determinare quindi se
      // andare a capo o meno, viene usato il 4 come numero di caratteri
      // equivalenti tra il titolo di un tab ed il successivo
      if(pg.getTitle() != null && (numChars + pg.getTitle().length() + 4) < 170){
        numChars += pg.getTitle().length() + 4;
      } else {
        numChars = pg.getTitle().length() + 4;
        // Chiudo il tab e ne aggiungo un'altro
        indiceRigaTab++;
        rigaTemporanea.append(tabBody.toStringPerChiusura());
        righeTab.add(rigaTemporanea.toString());

        rigaTemporanea = new StringBuffer();
        tabBody = new TabBodyImpl();
        tabBody.setContextPath(this.getContextPath());
        tabBody.setImgPath((String) this.pageContext.getServletContext().getAttribute(
            CostantiGenerali.ATTR_PATH_IMG));
        // Inizio il tab
        rigaTemporanea.append(tabBody.toString());
        voceTab.setTabBody(tabBody);
      }

      voceTab.setAttivo(i == this.getActivePage());
      if (i == this.getActivePage()) indiceRigaConTabAttivo = indiceRigaTab;
      voceTab.setHref("selezionaPagina(" + i + ")");
      // Aggiungo effettivamente la pagina
      rigaTemporanea.append(voceTab.toString());
    }
    // Continuo il tab
    rigaTemporanea.append(tabBody.toStringPerChiusura());
    righeTab.add(rigaTemporanea.toString());

    // Sposto la riga di tab contenente il tab attivo nella riga più in basso
    // come avviene con qualsiasi applicativo desktop in modo da simulare la
    // scheda in primo piano
    /*if (indiceRigaConTabAttivo != indiceRigaTab) {
      Object rigaConTabAttivo = righeTab.remove(indiceRigaConTabAttivo);
      righeTab.add(rigaConTabAttivo);
    }*/

    // L.G. 24/09/09: modifica gestione delle righe dei tab. Ora le righe dei
    // tab sono gestite in modo circolare
    Vector righeTabInverse = new Vector(righeTab.size());
    for(int i=0; i < righeTab.size(); i++){
      righeTabInverse.add(righeTab.get(righeTab.size()-1-i));
    }

    if(indiceRigaConTabAttivo > 0){
      int tmp = righeTab.size() - indiceRigaConTabAttivo;
      int indice = -1;
      do {
        indice++;
        Object tmpObj = righeTabInverse.remove(0);
        righeTabInverse.add(tmpObj);
      } while ((indice+1) != tmp);
    }
    // L.G. 24/09/09: fine modifica

    // A questo punto ciclo per inserire le varie righe di tab
    for (int i = 0; i < righeTabInverse.size(); i++)
      buf.append((String) righeTabInverse.elementAt(i));

    // Inserisco il dettaglio del tab selezionato
    if (this.getActivePage() >= 0 && this.getActivePage() < this.pagine.size()) {
      PaginaTagImpl pg = (PaginaTagImpl) pagine.get(this.getActivePage());
      buf.append(pg.getBody());
    }
    // Aggiungo la form che gestisce lo spostamento di pagine
    buf.append("<form ");
    buf.append(UtilityTags.getHtmlAttrib("name", "pagineForm"));
    buf.append(UtilityTags.getHtmlAttrib("action", this.getContextPath()
        + "/Scheda.do"));
    buf.append("method=\"post\">\n");
    // Aggiungo tutti gli hidden di default
    // {MF 20.11.2006} Setto il path di destinazione come lo stesso da dove
    // viene
    this.pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_TO_JSP,
        ((HttpServletRequest) pageContext.getRequest()).getServletPath(),
        PageContext.REQUEST_SCOPE);
    buf.append(UtilityTags.getHtmlDefaultHidden(this.pageContext));
    buf.append(UtilityTags.getHtmlHideInput("metodo", "apri"));
    buf.append(UtilityTags.getHtmlHideInputFromParam(this.pageContext,
        UtilityTags.DEFAULT_HIDDEN_NOME_TABELLA));
    // buf.append(UtilityTags.getHtmlHideInputFromParam(this.pageContext,
    // UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA));
    // buf.append(UtilityTags.getHtmlHideInputFromParam(this.pageContext,
    // UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA));
    buf.append(UtilityTags.getHtmlHideInputFromParam(this.pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA));
    // WE412: si passa nel form per il cambio pagina il calcolo se l'entita'
    // principale e' modificabile
    buf.append(UtilityTags.getHtmlHideInput(
        CostantiGenerali.ENTITA_PRINCIPALE_MODIFICABILE,
        this.pageContext.getSession().getAttribute(
            CostantiGenerali.ENTITA_PRINCIPALE_MODIFICABILE)));

    // A questo punto inserisco tutto il corpo
    BodyContent body = this.getBodyContent();
    if (body != null) {
      buf.append(body.getString());
      body.clearBody();
    }
    buf.append("</form>");

    try {
      this.pageContext.getOut().write(buf.toString());
    } catch (IOException e) {
      throw new JspException(e.getMessage(), e);
    }
    this.activePage = 0;
    return super.doEndTag();
  }

  /**
   * @return Returns the activePage.
   */
  public int getActivePage() {
    return activePage;
  }

  /**
   * @param activePage
   *        The activePage to set.
   */
  public void setActivePage(int activePage) {
    this.activePage = activePage;
  }

  public boolean isGestisciProtezioni() {
    return gestisciProtezioni;
  }

  public void setGestisciProtezioni(boolean gestisciProtezioni) {
    this.gestisciProtezioni = gestisciProtezioni;
  }

}