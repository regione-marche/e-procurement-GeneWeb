/*
 * Created on 04-04-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.properties.ConfigManager;

/**
 * Funzione che elabora i filtri impostati nella pagina
 * popup-trova-filtroAttivita.jsp per impostare la where da inserire
 * nella variabile di sessione "filtroAttivita"
 *
 * @author Marcello Caminiti
 */

public class InitFiltroListaSessioneFunction extends AbstractFunzioneTag {

  Logger logger = Logger.getLogger(InitFiltroListaSessioneFunction.class);

  public InitFiltroListaSessioneFunction() {
    super(3, new Class[] { PageContext.class, String.class,String.class });
  }

  @Override

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    HttpSession sessione = pageContext.getSession();
    String trovaAddWhere = UtilityTags.getParametro(pageContext,UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
    String trovaParameter = UtilityTags.getParametro(pageContext,UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA);

    String parametro = (String)params[1];

    if(parametro!=null && "1".equals(parametro) ){
      sessione.removeAttribute("filtroAttivita");
    }else if(trovaAddWhere!=null && !"".equals(trovaAddWhere)){

      if(trovaParameter!=null && !"".equals(trovaParameter)){
        String[] parametri = trovaParameter.split(";");

        int len = parametri.length;

        for(int i=0;i<len;i++){
          String[] par = parametri[i].split(":");
          String tipo = par[0];
          String valore = par[1];
          if("T".equals(tipo)){
            boolean sostituzioneCarattere=false;
            if(valore.indexOf("$")>0){
              valore = StringUtils.replace(valore, "$", "#");
              sostituzioneCarattere = true;
            }
            trovaAddWhere = trovaAddWhere.replaceFirst("\\?", "'" + valore +"'");
            if(sostituzioneCarattere)
              trovaAddWhere = StringUtils.replace(trovaAddWhere, "#", "$");
          }else if("N".equals(tipo)){
            trovaAddWhere = trovaAddWhere.replaceFirst("\\?", valore);
          }else if("D".equals(tipo)){
            String db =ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
            if("ORA".equals(db)){
              trovaAddWhere = trovaAddWhere.replaceFirst("\\?", "to_date('" + valore + "','dd/mm/yyyy')");
            }else if("MSQ".equals(db)){
              trovaAddWhere = trovaAddWhere.replaceFirst("\\?", "CONVERT (datetime , CONVERT (char, '" + valore + "', 3))");
            }else if("POS".equals(db)){
              trovaAddWhere = trovaAddWhere.replaceFirst("\\?", "to_date('" + valore + "','DD/MM/YYYY')");
            }

          }

        }
      }

      sessione.setAttribute("filtroAttivita", " and " + trovaAddWhere);
    }


    return null;
  }
}
