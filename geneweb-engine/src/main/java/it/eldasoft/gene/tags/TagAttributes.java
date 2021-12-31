/*
 * Created on 4-gen-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags;

import java.util.HashMap;

import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Classe per gli attributi di un tag (Tutti gli attributi che variano in
 * funzione dei dati presenti)
 * 
 * @author marco.franceschin
 * 
 */
public class TagAttributes extends HashMap {

  // ///////////////////////////////////////////////////////////////
  // Modifiche
  // ----------------
  // 22/02/2007 M.F. Il tag Attributes estende l'hashMap per poter aggiungere attributi a runtime
  // ///////////////////////////////////////////////////////////////
  /**
   * 
   */
  private static final long serialVersionUID = -2375097351934243192L;

  /** NOme della variabile impostata nel request */
  private String            nomeVarRequest;

  private int               nIteration;

  private String            standardId;

  private int               nCiclo           = 0;
  
  
  public TagAttributes(String nomeVar) {
    this.nomeVarRequest=nomeVar;
  }
  

  /**
   * Estraggo il tag attribute in ordine nello stack dal request
   * 
   * @param tag
   *        Tag di cui estrarre gli attributi
   * @return
   */
  public static TagAttributes getInstance(TagGeneInterface tag) {
    return UtilityTags.getFromStackTagAttributes(tag.getPageContext(),
        tag.getTipoVar());
  }

  public static void removeTagFromRequest(TagGeneInterface tag) {
    UtilityTags.removeFromStackTagAttributes(tag.getPageContext(),
        getInstance(tag));
  }

  public String getNomeVarRequest() {
    return nomeVarRequest;
  }

  public int getNCiclo() {
    return nCiclo;
  }

  public void setNCiclo(int ciclo) {
    nCiclo = ciclo;
  }

  public int getNIteration() {
    return nIteration;
  }

  public void setNIteration(int iteration) {
    nIteration = iteration;
  }

  public String getStandardId() {
    return standardId;
  }

  public void setStandardId(String standardId) {
    this.standardId = standardId;
  }

}
