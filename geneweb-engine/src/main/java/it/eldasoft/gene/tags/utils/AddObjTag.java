/*
 * Created on 07/nov/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.utils;

import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspException;

import it.eldasoft.gene.tags.TagSupportGene;


public class AddObjTag extends TagSupportGene{

  /**
   * 
   */
  private static final long serialVersionUID = 8748811883549841941L;
  
  private Object map=null;
  private Object value=null;
  private String id=null;
  
  public int doStartTag() throws JspException {
    super.doStartTag();
    try {
      if (map instanceof HashMap) {
        HashMap map=(HashMap)this.map;
        if(id==null)
           throw new JspException("AddObjTag: Se si passa una HashMap si deve settare anche l'id dell'oggetto !");
        map.put(id, value);

      } else if (map instanceof List) {
        List l = (List) map;
        if(id!=null)
          throw new JspException("AddObjTag: Se si passa una List non è necessario impostare l'id!");
        l.add(value);        
      }
    } catch (Throwable t) {
      throw new JspException("AddObjTag: L'attributo map può essere solo un HashMap o un List !");
    }
    return SKIP_BODY;
    
  }

  
  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  
  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  
  /**
   * @return the map
   */
  public Object getMap() {
    return map;
  }

  
  /**
   * @param map the map to set
   */
  public void setMap(Object map) {
    this.map = map;
  }


  
  /**
   * @return the value
   */
  public Object getValue() {
    return value;
  }


  
  /**
   * @param value the value to set
   */
  public void setValue(Object value) {
    this.value = value;
  }

}
