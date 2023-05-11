/*
 * Created on 05-dic-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

/**
 * Contenitore dei dati del form parametri associati ad una modello in fase di
 * composizione dello stesso
 * 
 * @author Stefano.Sabbadin
 */
public class ParametriModelloForm extends ComponiModelloForm {

  /**
   * UID
   */
  private static final long serialVersionUID = -5426978071033904566L;

  private String[]          parametriModello = null;

  public ParametriModelloForm() {
    super();
    this.parametriModello = null;
  }

  public ParametriModelloForm(ComponiModelloForm componiModelloForm){
    super();
    // Copia di tutti i campi dell'oggetto componiModelloForm nel nuovo
    // oggetto parametriModelloForm (che lo estende)
    this.setEntita(componiModelloForm.getEntita());
    this.setTipo(componiModelloForm.getTipo());
    this.setFileComposto(componiModelloForm.getFileComposto());
    this.setIdModello(componiModelloForm.getIdModello());
    this.setIdSessione(componiModelloForm.getIdSessione());
    this.setNoFiltroEntitaPrincipale(componiModelloForm.getNoFiltroEntitaPrincipale());
    this.setNomeChiavi(componiModelloForm.getNomeChiavi());
    this.setNomeModello(componiModelloForm.getNomeModello());
    this.setPaginaSorgente(componiModelloForm.getPaginaSorgente());
    this.setValChiavi(componiModelloForm.getValChiavi());
    this.setValori(componiModelloForm.getValori());
    this.setMultipartRequestHandler(componiModelloForm.getMultipartRequestHandler());
    this.setRiepilogativo(componiModelloForm.getRiepilogativo());
        
    this.setParametriModello(null);
  }
  
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.parametriModello = null;
  }

  /**
   * @return Ritorna parametriModello.
   */
  public String[] getParametriModello() {
    return parametriModello;
  }

  /**
   * @param parametriModello
   *        parametriModello da settare internamente alla classe.
   */
  public void setParametriModello(String[] parametriModello) {
    this.parametriModello = parametriModello;
  }
}