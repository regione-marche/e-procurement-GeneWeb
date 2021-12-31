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

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.templates.JspTemplateTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

/**
 * Tag principale per la gestione di una creazione guidata. Al suo interno si
 * definiscono le pagine del wizard
 * 
 * @author stefano.sabbadin
 */
public class WizardTag extends BodyTagSupportGene {

  /**
   * UID
   */
  private static final long serialVersionUID = -912729616274455879L;

  /** Flag per definire la gestione delle protezioni */
  private boolean           gestisciProtezioni;

  /**
   * Entità principale associata al wizard, diversa dall'entità indicata
   * singolarmente nei form (possono essere la stessa entità come entità
   * correlate)
   */
  private String            entitaPrincipale;

  /**
   * Nome della classe, comprensiva del package, del gestore da richiamare al
   * salvataggio dei dati nel wizard
   */
  private String            gestoreSalvataggio;

  /**
   * url da richiamare successivamente alla chiusura del wizard
   */
  private String            hrefFineWizard;

  /** Elenco delle pagine */
  Vector                    pagine;

  /** Pagina attiva */
  private int               activePage;

  /** Pagina corrente */
  private int               curPg;

  public WizardTag() {
    super("tab");
    this.reset();
  }

  /**
   * Pulisce il tag per un nuovo utilizzo. Vanno inizializzati tutti gli
   * attributi non obbligatori o non specificati nel descrittore del tag stesso
   */
  private void reset() {
    this.pagine = new Vector();
    this.curPg = -1;
    this.activePage = 0;
    this.gestisciProtezioni = true;
    this.entitaPrincipale = null;
    this.hrefFineWizard = null;
  }

  /**
   * @return Ritorna activePage.
   */
  public int getActivePage() {
    return activePage;
  }

  /**
   * @return Ritorna gestisciProtezioni.
   */
  public boolean isGestisciProtezioni() {
    return gestisciProtezioni;
  }

  /**
   * @param gestisciProtezioni
   *        gestisciProtezioni da settare internamente alla classe.
   */
  public void setGestisciProtezioni(boolean gestisciProtezioni) {
    this.gestisciProtezioni = gestisciProtezioni;
  }

  /**
   * @param entitaPrincipale
   *        entitaPrincipale da settare internamente alla classe.
   */
  public void setEntitaPrincipale(String entitaPrincipale) {
    this.entitaPrincipale = entitaPrincipale;
  }

  /**
   * @param gestoreSalvataggio
   *        gestoreSalvataggio da settare internamente alla classe.
   */
  public void setGestoreSalvataggio(String gestoreSalvataggio) {
    this.gestoreSalvataggio = gestoreSalvataggio;
  }

  /**
   * @param hrefFineWizard
   *        hrefFineWizard da settare internamente alla classe.
   */
  public void setHrefFineWizard(String hrefFineWizard) {
    this.hrefFineWizard = hrefFineWizard;
  }

  /**
   * Aggiunge una pagina visibile al wizard
   * 
   * @param pagina
   *        pagina da aggiungere
   * @return indice della pagina aggiunta
   */
  public int addPg(PaginaWizardImpl pagina) {
    this.curPg++;
    pagina.setIndice(this.curPg);
    this.pagine.add(pagina);
    return curPg;
  }

  /**
   * Apertura del tag
   * 
   * @see it.eldasoft.gene.tags.BodyTagSupportGene#doStartTag()
   */
  public int doStartTag() throws JspException {
    super.doStartTag();

    // si verifica che l'entità principale indicata esista nei metadati
    Tabella tab = DizionarioTabelle.getInstance().getDaNomeTabella(
        this.entitaPrincipale);
    if (tab == null)
      throw new JspException(
          "L'entità principale indicata nel tag wizard non esiste nei metadati");

    this.pageContext.setAttribute(
        CostantiWizard.HIDDEN_PARAMETRO_ENTITA_PRINCIPALE_WIZARD,
        this.entitaPrincipale, PageContext.REQUEST_SCOPE);
    this.pageContext.setAttribute(
        CostantiWizard.HIDDEN_PARAMETRO_GESTORE_SALVATAGGIO_WIZARD,
        this.gestoreSalvataggio, PageContext.REQUEST_SCOPE);

    // se non viene specificata la pagina in cui andare alla fine del wizard,
    // allora si considera il default, ovvero il dettaglio dell'entità
    if (this.hrefFineWizard == null) {
      this.hrefFineWizard = UtilityTags.getPathFromTab(tab) + "scheda.jsp";
    }
    this.pageContext.setAttribute(
        CostantiWizard.HIDDEN_PARAMETRO_PAGINA_FINE_WIZARD,
        this.hrefFineWizard, PageContext.REQUEST_SCOPE);

    // Settaggio della gestione delle protezioni
    if (this.isGestisciProtezioni()) {
      JspTemplateTag parentTemplate = (JspTemplateTag) this.getParent(JspTemplateTag.class);
      if (parentTemplate != null) {
        this.setGestisciProtezioni(parentTemplate.isGestisciProtezioni());
      }
    }

    // si disabilita la navigazione dell'applicativo all'interno di un wizard
    this.pageContext.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE, PageContext.REQUEST_SCOPE);

    // La pagina attiva è prima quella indicata nei parametri del request
    if (UtilityTags.getParametro(this.pageContext,
        UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE) != null) {
      this.activePage = new Integer(UtilityTags.getParametro(this.pageContext,
          UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE)).intValue();
    } else
      this.pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE,
          String.valueOf(this.getActivePage()), PageContext.REQUEST_SCOPE);

    // se non è settata la sottopagina del wizard (tipo la prima volta che si
    // apre il wizard), si setta di default la prima sottopagina
    if (this.pageContext.getAttribute(
        CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA, PageContext.REQUEST_SCOPE) == null) {
      this.pageContext.setAttribute(
          CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA, new Integer(0),
          PageContext.REQUEST_SCOPE);
    }
    return BodyTag.EVAL_BODY_BUFFERED;
  }

  /**
   * Alla chiusura del tag creo tutte le pagine
   * 
   * @see it.eldasoft.gene.tags.BodyTagSupportGene#doEndTag()
   */
  public int doEndTag() throws JspException {
    // A questo punto creo la pagina
    StringBuffer buf = new StringBuffer();

    // Inizio il div di avanzamento
    buf.append("<div class=\"avanzamento-wizard\">");

    // Scorro tutte le pagine per la scrittura
    for (int i = 0; i < pagine.size(); i++) {
      PaginaWizardImpl pg = (PaginaWizardImpl) pagine.get(i);

      // sicuramente almeno la prima pagina rientra nelle pagine visitate,
      // quindi apro lo span corrispondente; per tutte le altre pagine, non
      // essendo la prima, le faccio precedere dalla ->
      if (pg.getIndice() == 0)
        buf.append("<span class=\"avanzamento-paginevisitate\">");
      else
        buf.append(" ").append(UtilityStringhe.convStringHTML("->")).append(" ");

      // inserisco il titolo nello span
      buf.append(UtilityStringhe.convStringHTML(pg.getTitle()));

      // chiudo lo span delle pagine visitate, ed apro eventualmente quello
      // delle pagine da visitare se esistono altre pagine successive a quella
      // attuale
      if (pg.getIndice() == this.activePage) {
        buf.append("</span>");
        buf.append("<span class=\"avanzamento-paginedavisitare\">");
      }

      // dopo aver considerato l'ultima pagina si chiude l'unico span aperto
      if (pg.getIndice() == (pagine.size() - 1)) buf.append("</span>");
    }

    // si chiude il div di avanzamento, quindi si apre quello del dettaglio
    buf.append("</div>\n");
    buf.append("<div class=\"contenitore-dettaglio\">\n");

    // Inserisco il dettaglio del tab selezionato
    if (this.getActivePage() >= 0 && this.getActivePage() < this.pagine.size()) {
      PaginaWizardImpl pg = (PaginaWizardImpl) pagine.get(this.getActivePage());

      if (this.getActivePage() == 0 && pg.getSottoPagina() == 0) {
        // se la pagina attiva è la prima (e non è ad esempio relativa alla
        // domanda di una lista di inserimenti), non ha senso la navigazione
        // all'indietro
        // this.pageContext.getRequest().setAttribute("firstPage", "1");
        this.getJavascript().println(
            "document.getElementById('linkIndietro').style.display = 'none';");
        this.getJavascript().println(
            "document.getElementById('btnIndietro').style.display = 'none';");
      }
      if (this.getActivePage() != this.pagine.size() - 1) {
        // se la pagina attiva non è l'ultima, non ha senso la selezione
        // dell'azione di salvataggio, attiva solo nella pagina finale
        this.getJavascript().println(
            "document.getElementById('linkSalva').style.display = 'none';");
        this.getJavascript().println(
            "document.getElementById('btnSalva').style.display = 'none';");
      }
      if (this.getActivePage() == this.pagine.size() - 1) {
        // se la pagina attiva è l'ultima, non ha senso la navigazione in avanti
        // e tantomeno il passaggio all'ultima pagina mediante il bottone Fine
        // this.pageContext.getRequest().setAttribute("lastPage", "1");
        if (pg.getSottoPagina() == 0) {
          this.getJavascript().println(
              "document.getElementById('linkAvanti').style.display = 'none';");
          this.getJavascript().println(
              "document.getElementById('btnAvanti').style.display = 'none';");
        }
        this.getJavascript().println(
            "document.getElementById('linkFine').style.display = 'none';");
        this.getJavascript().println(
            "document.getElementById('btnFine').style.display = 'none';");
      }

      this.getJavascript().println(
          "document.forms[0]."
              + CostantiWizard.HIDDEN_PARAMETRO_NUMERO_PAGINE
              + ".value="
              + this.pagine.size()
              + ";");

      buf.append(pg.getBody());
    }

    // A questo punto inserisco tutto il corpo
    BodyContent body = this.getBodyContent();
    if (body != null) {
      buf.append(body.getString());
      body.clearBody();
    }
    buf.append("</div>\n");

    try {
      this.pageContext.getOut().write(buf.toString());
    } catch (IOException e) {
      throw new JspException(e.getMessage(), e);
    }
    // si pulisce l'oggetto per le prossime iterazioni
    this.reset();

    return super.doEndTag();
  }

}