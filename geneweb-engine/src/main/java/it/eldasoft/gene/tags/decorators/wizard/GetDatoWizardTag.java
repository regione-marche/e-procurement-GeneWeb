/*
 * Created on 09/mag/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.wizard;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Classe utilizzata per estrarre, dall'oggetto presente in sessione e
 * rappresentante la collezione di dati utilizzati ed impostati dal wizard,
 * un'informazione.
 * 
 * @author Stefano.Sabbadin
 */
public class GetDatoWizardTag extends TagSupportGene {

  /**
   * UID
   */
  private static final long serialVersionUID = 7923144754383543673L;

  /** Identificativo della variabile di scripting da definire */
  private String            id;

  /** Nome dell'attributo da leggere in sessione */
  private String            name;

  /** Visibilità della variabile creata */
  private String            scope;

  /**
   * Costruttore elementare che svuota il contenuto dell'oggetto
   */
  public GetDatoWizardTag() {
    super();
  }

  /**
   * Svuota il contenuto dell'oggetto
   */
  private void reset() {
    this.id = null;
    this.name = null;
    this.scope = null;
  }

  /**
   * @param id
   *        id da settare internamente alla classe.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @param name
   *        name da settare internamente alla classe.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @param scope
   *        scope da settare internamente alla classe.
   */
  public void setScope(String scope) {
    this.scope = scope;
  }

  public int doStartTag() throws JspException {
    super.doStartTag();

    // di default, se non valorizzato, lo scope è di pagina
    int intScope = PageContext.PAGE_SCOPE;
    if (this.scope != null) {
      if ("page".equals(this.scope))
        intScope = PageContext.PAGE_SCOPE;
      else if ("request".equals(this.scope))
        intScope = PageContext.REQUEST_SCOPE;
      else if ("session".equals(this.scope))
        intScope = PageContext.SESSION_SCOPE;
      else if ("application".equals(this.scope))
        intScope = PageContext.APPLICATION_SCOPE;
      else throw new JspException("Lo scope del tag deve essere uno dei seguenti valori: page, request, session, application");
    }
    
    Object oggetto = null;

    DataColumnContainer datiSessione = (DataColumnContainer) this.pageContext.getSession().getAttribute(
        CostantiWizard.NOME_OGGETTO_WIZARD_SESSIONE);
    if (datiSessione != null) {
      try {
        oggetto = datiSessione.getColumn(this.name).getValue().getValue();
      } catch (GestoreException e) {
        // nel caso l'oggetto non sia presente, l'oggetto rimane null
      }
    }
    this.pageContext.setAttribute(this.id, oggetto, intScope);
    
    this.reset();
    return SKIP_BODY;
  }
}
