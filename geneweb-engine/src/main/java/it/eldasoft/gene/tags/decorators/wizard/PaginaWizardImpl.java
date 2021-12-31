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

/**
 * Bean per la gestione dei dati di una pagina del wizard
 * 
 * @author stefano.sabbadin
 */
public class PaginaWizardImpl {

  /** Titolo delle pagina */
  private String title;
  /** Corpo della pagina */
  private String body;
  /** Indice della pagina */
  private int    indice;
  // /** Indica se la pagina è stata visitata o è la pagina attuale */
  // private boolean isPaginaVisitata;
  /** Tipologia di pagina: DETTAGLIO, LISTA_NUOVO, LISTA_DOMANDA */
  private String tipoPagina;
  /** Progressivo di sottopagina per la tipologia di pagina del wizard */
  private int    sottoPagina;

  /**
   * Costruttore di default standard
   */
  public PaginaWizardImpl() {
    this.title = null;
    this.body = null;
    this.indice = -1;
    // this.isPaginaVisitata = false;
    this.tipoPagina = null;
    this.sottoPagina = 0;
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
   * @return Ritorna body.
   */
  public String getBody() {
    return body;
  }

  /**
   * @param body
   *        body da settare internamente alla classe.
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * @return Ritorna indice.
   */
  public int getIndice() {
    return indice;
  }

  /**
   * @param indice
   *        indice da settare internamente alla classe.
   */
  public void setIndice(int indice) {
    this.indice = indice;
  }

  // /**
  // * @return Ritorna isPaginaVisitata.
  // */
  // public boolean isPaginaVisitata() {
  // return isPaginaVisitata;
  // }
  //
  // /**
  // * @param isPaginaVisitata
  // * isPaginaVisitata da settare internamente alla classe.
  // */
  // public void setPaginaVisitata(boolean isPaginaVisitata) {
  // this.isPaginaVisitata = isPaginaVisitata;
  // }

  /**
   * @return Ritorna tipoPagina.
   */
  public String getTipoPagina() {
    return tipoPagina;
  }

  /**
   * @param tipoPagina
   *        tipoPagina da settare internamente alla classe.
   */
  public void setTipoPagina(String tipoPagina) {
    this.tipoPagina = tipoPagina;
  }

  /**
   * @return Ritorna sottoPagina.
   */
  public int getSottoPagina() {
    return sottoPagina;
  }

  /**
   * @param sottoPagina
   *        sottoPagina da settare internamente alla classe.
   */
  public void setSottoPagina(int sottoPagina) {
    this.sottoPagina = sottoPagina;
  }

}
