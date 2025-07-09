package it.eldasoft.gene.commons.web.domain;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class ProtocolloAuth implements Serializable{

  private static final long serialVersionUID = 7870398843897398599L;
  
  Logger logger = Logger.getLogger(ProtocolloAuth.class);
  
  private String name = null;
  private String label = null;
  private String description = null;
  private Integer order = 0;
  private boolean loginBasedOnFiscalCode = true;
  
  public ProtocolloAuth(String name, String label, String orderProperty, String description, String order, boolean loginBasedOnFiscalCode){
    this.name=name;
    this.label=label;
    this.description=description;
    this.loginBasedOnFiscalCode = loginBasedOnFiscalCode;
    Integer tmpOrder = 99;
    try {
      tmpOrder = Integer.parseInt(order);
    }
    catch(NumberFormatException e) {
      logger.error("Errore nella lettura del valore numerico per la proprietà: "+orderProperty);
    }
    this.order=tmpOrder;
  }
  
  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  /**
   * @return the order
   */
  public Integer getOrder() {
    return order;
  }
  
  /**
   * @param order the order to set
   */
  public void setOrder(Integer order) {
    this.order = order;
  }
  
  /**
   * @return the label
   */
  public String getLabel() {
    return label;
  }
  
  /**
   * @param label the label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }
  
  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }
  
  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }
  
  public boolean isLoginBasedOnFiscalCode() {
    return loginBasedOnFiscalCode;
  }

  public void setLoginBasedOnFiscalCode(boolean loginBasedOnFiscalCode) {
    this.loginBasedOnFiscalCode = loginBasedOnFiscalCode;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("");
    
    str.append("name=");
    if (this.name != null) {
      str.append(this.name);
    }
    str.append(", ");
    
    str.append("label=");
    if (this.label != null) {
      str.append(this.label);
    }
    str.append(", ");
    
    str.append("order=");
    if (this.order != null) {
      str.append(this.order);
    }
    
    str.append("description=");
    if (this.description != null) {
      str.append(this.description);
    }
    
    str.append("loginBasedOnFiscalCode=");
    str.append(this.loginBasedOnFiscalCode);
    
    return str.toString();
  }


}