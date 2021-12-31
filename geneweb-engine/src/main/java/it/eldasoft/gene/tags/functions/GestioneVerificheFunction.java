/*
 * Created on 06-02-2020
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per l'inizializzazione della lista delle verifiche
 *
 * @author Cristian Febasi
 */
public class GestioneVerificheFunction extends AbstractFunzioneTag {

  public GestioneVerificheFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String modo = null;
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String idVerifica = (String) params[1];
    idVerifica = UtilityStringhe.convertiNullInStringaVuota(idVerifica);
    String selVerifiche="select codimp,gg_validita from verifiche where id = ?";
    String selDocVerifiche="select count(*) from documenti_verifiche where id_verifica = ?";
    String updateLista = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA);
    if (updateLista == null || updateLista.length() == 0){
      updateLista = "0";
    }
    pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA, updateLista, PageContext.REQUEST_SCOPE);

    try {
      if(!"".equals(idVerifica)){
        Vector datiVERIFICA = sqlManager.getVector(selVerifiche, new Object[]{new Long(idVerifica)});
        if(datiVERIFICA!=null && datiVERIFICA.size()>0 ){
          String codimp = (String) SqlManager.getValueFromVectorParam(datiVERIFICA, 0).getValue();
          Long ggValiditaVerifica = (Long) SqlManager.getValueFromVectorParam(datiVERIFICA, 1).getValue();
          pageContext.setAttribute("codimp", codimp, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("ggvalidita", ggValiditaVerifica, PageContext.REQUEST_SCOPE);
        }

      }
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre il codice impresa della verifica con id = " + idVerifica, e);
    }


    return modo;
  }

}