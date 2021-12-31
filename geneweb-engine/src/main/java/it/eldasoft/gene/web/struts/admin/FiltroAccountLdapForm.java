/*
 * Created on 28-mar-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import java.util.Iterator;
import java.util.List;

import it.eldasoft.gene.db.domain.admin.AccountLdap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


/**
 * @author cit_defilippis
 *
 */
public class FiltroAccountLdapForm extends ActionForm {

  /**   UID   */
  private static final long serialVersionUID = 4236704651667311749L;

  private String filtroOU;
  private String filtroCn;
  private String[] listaTextOU;
  private String[] listaValueOU;
  
  private void inizializzaOggetto(){
    this.filtroCn = "";
    this.filtroOU = "";
  }
  
  public FiltroAccountLdapForm() {
    super();
    this.inizializzaOggetto();
  }
  
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }
  /**
   * @return Returns the filtroCn.
   */
  public String getFiltroCn() {
    return filtroCn;
  }
  
  /**
   * @param filtroCn The filtroCn to set.
   */
  public void setFiltroCn(String filtroCn) {
    this.filtroCn = filtroCn;
  }
  
  /**
   * @return Returns the filtroOU.
   */
  public String getFiltroOU() {
    return filtroOU;
  }
  
  /**
   * @param filtroOU The filtroOU to set.
   */
  public void setFiltroOU(String filtroOU) {
    this.filtroOU = filtroOU;
  }

  
  /**
   * @return Ritorna listaTextOU.
   */
  public String[] getListaTextOU() {
    return listaTextOU;
  }

  
  /**
   * @param listaTextOU listaTextOU da settare internamente alla classe.
   */
  public void setListaTextOU(String[] listaTextOU) {
    this.listaTextOU = listaTextOU;
  }

  /**
   * @param listaTextOU listaTextOU da settare internamente alla classe.
   */
  public void setListaTextOU(List<?> listaTextOU) {
    this.listaTextOU = new String[listaTextOU.size()];
    int i = 0;
    for (Iterator<?> iter = listaTextOU.iterator(); iter.hasNext();) {
      AccountLdap element = (AccountLdap) iter.next();
      this.listaTextOU[i] = element.getSn();
      i++;
    }
    
  }

  
  /**
   * @return Ritorna listaValueOU.
   */
  public String[] getListaValueOU() {
    return listaValueOU;
  }

  
  /**
   * @param listaValueOU listaValueOU da settare internamente alla classe.
   */
  public void setListaValueOU(String[] listaValueOU) {
    this.listaValueOU = listaValueOU;
  }
  
  /**
   * @param listaValueOU listaValueOU da settare internamente alla classe.
   */
  public void setListaValueOU(List<?> listaValueOU) {
    this.listaValueOU = new String[listaValueOU.size()];
    int i = 0;
    for (Iterator<?> iter = listaValueOU.iterator(); iter.hasNext();) {
      AccountLdap element = (AccountLdap) iter.next();
      this.listaValueOU[i] = element.getDn();
      i++;
    }
    
  }
  
}
