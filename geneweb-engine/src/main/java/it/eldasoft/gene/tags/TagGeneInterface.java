/*
 * Created on 18-gen-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags;

import javax.servlet.jsp.PageContext;

/**
 * Interfaccia del tag di gene
 * @author marco.franceschin
 *
 */
public interface TagGeneInterface {
  
  /**
   * Funzione che da il page contezt del tag
   * @return
   */
  public PageContext getPageContext();
  /**
   * Funzione in cui si può eseguire l'override per creare un oggetto di tipo tag attribute
   * @return
   */
  public TagAttributes newTagAttributes();
  /**
   * Funzione che da o crea un attribute meneger che viene salvato nel request
   * @return
   */
  public TagAttributes getAttributeManager();
  /**
   * Funzione che restituisce il tipo di variabile
   * @return
   */
  public String getTipoVar();
  
}
