/*
 * Created on 22/apr/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.scheda;

import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.decorators.archivi.ArchivioTagImpl;

/**
 * Interfaccia da utilizzare per tutti i tag relativi a form scheda
 * 
 * @author Stefano.Sabbadin
 */
public interface IFormScheda {

  /**
   * @return true Se si è alla prima iterazione, false altrimenti
   */
  public boolean isFirstIteration();

  /**
   * @return nome del form
   */
  public String getFormName();

  /**
   * @return nome dell'entità associata al form
   */
  public String getEntita();

  /**
   * @return true se si gestiscono le protezioni mediante profili, false
   *         altrimenti
   */
  public boolean isGestisciProtezioni();

  /**
   * Setta il decoratore nel campo scheda in input
   * 
   * @param tag
   *        campo del form scheda
   * @return oggetto contenente le informazioni contenute nel campo scheda
   */
  public CampoSchedaTagImpl getDecoratore(CampoSchedaTag tag);

  /**
   * Aggiunge l'archivio al form
   * 
   * @param archivio
   *        archivio da aggiungere
   */
  public void addArchivio(ArchivioTagImpl archivio);

  /** 
   * @see it.eldasoft.gene.tags.TagGeneInterface#getPageContext()
   */
  public PageContext getPageContext();
  
  /**
   * Verifica se esiste o meno un campo nell'elenco
   * 
   * @param string
   *        nome del campo
   * @return true se il campo esiste, false altrimenti
   */
  public boolean isCampo(String nomeCampoFisico);
}
