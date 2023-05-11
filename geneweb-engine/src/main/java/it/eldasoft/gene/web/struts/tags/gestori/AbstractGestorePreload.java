/*
 * Created on 26/mag/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.tags.BodyTagSupportGene;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Classe da estendere per introdurre un plugin di pagina che effettua tutta una
 * serie di query e valorizzazioni negli oggetti impliciti in modo da snellire
 * le pagine JSP da codice SQL, ed in modo da isolare la business logic da tutto
 * ciò che deve essere visualizzazione pura
 * 
 * @author Stefano.Sabbadin
 * 
 */
public abstract class AbstractGestorePreload {

  /** Tag chiamante */
  private BodyTagSupportGene tag;

  public AbstractGestorePreload(BodyTagSupportGene tag) {
    this.tag = tag;
  }

  /**
   * @return Ritorna tag.
   */
  public BodyTagSupportGene getTag() {
    return tag;
  }

  /**
   * Esegue un'elaborazione specifica al termine dell'avvio del processing del
   * tag.<br>
   * Questo metodo va implementato nel caso in cui si intenda effettuare delle
   * query o dei settaggi indipendenti dai valori estratti per i campi del form.
   * 
   * @param page
   *        page context
   * @param modoAperturaScheda
   *        modalità di apertura della scheda (NUOVO, MODIFICA, VISUALIZZA)
   * 
   * @throws JspException
   */
  public abstract void doBeforeBodyProcessing(PageContext page,
      String modoAperturaScheda) throws JspException;

  /**
   * Esegue un'elaborazione dopo il popolamento del contenitore dei dati
   * denominato <b>datiRiga</b> (ad esempio mediante l'esecuzione della query
   * per l'estrazione dei dati in visualizzazione o modifica per il form di una
   * scheda).<br>
   * Questo metodo va implementato esclusivamente nel caso in cui si intenda
   * effettuare delle query o dei settaggi che dipendono dai valori dei campi
   * presenti nel form.
   * 
   * @param page
   *        page context
   * @param modoAperturaScheda
   *        modalità di apertura della scheda (NUOVO, MODIFICA, VISUALIZZA)
   * 
   * @throws JspException
   */
  public abstract void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException;

}
