/*
 * Created on 18/feb/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.tags.functions;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.integrazioni.WsdmConfigManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;


public class GetWSDMConfiAttivaFunction extends AbstractFunzioneTag{

  public GetWSDMConfiAttivaFunction() {
    super(3, new Class[] { PageContext.class,String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    
    WsdmConfigManager wsdmConfigManager = (WsdmConfigManager) UtilitySpring.getBean(
        "wsdmConfigManager", pageContext, WsdmConfigManager.class);
    
    String uffint = (String) params[1];
    String codapp = (String) params[2];
    Long codiceConfi = null;
    String res = "";
    try {
      codiceConfi = wsdmConfigManager.getWsdmConfigurazione(uffint, codapp);
      
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura della configurazione WSDM ", e);
    }
    if(codiceConfi !=null){
      res = codiceConfi.toString();
    }
    return res;
  }

}
