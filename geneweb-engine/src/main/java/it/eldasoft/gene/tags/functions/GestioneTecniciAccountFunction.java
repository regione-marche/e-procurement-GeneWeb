/*
 * Created on 16/05/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import java.sql.SQLException;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioAccount;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare la sezione dei tecnici associati ad un utente
 * 
 * @author Sara Santi
 */
public class GestioneTecniciAccountFunction extends
		AbstractFunzioneTag {

	  

	  public GestioneTecniciAccountFunction() {
	    super(2, new Class[] { PageContext.class,String.class});
	}

	public String function(PageContext pageContext, Object[] params)
			throws JspException {
	  
	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	        pageContext, SqlManager.class);
	    
	    String syscon = (String)params[1];
	    Long sysconLong = new Long(syscon);
	    String sysute = null;
	    String ret="";
	    try {
          sysute = (String)sqlManager.getObject("select sysute from usrsys where syscon=?", new Object[]{sysconLong});
        } catch (SQLException e) {
          throw new JspException("Errore nell'estrarre i tecnici associati all'utente " + syscon, e);
        }
	  
	    
	    
	    HttpSession sessione =  pageContext.getSession();
	    Object obj = sessione.getAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB);
	    if (obj != null) {
	      GestioneTab gestoreTab = (GestioneTab) obj;
	      gestoreTab.setTabAttivo(CostantiDettaglioAccount.TECNICI);
	      if(UtilityTags.SCHEDA_MODO_MODIFICA.equals(UtilityTags.getParametro(pageContext,UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))){
	        gestoreTab.setTabSelezionabili(null);  
	      }else{
	        gestoreTab.setTabSelezionabili(new String[] {
                CostantiDettaglioAccount.DETTAGLIO,
                CostantiDettaglioAccount.PROFILI,
                CostantiDettaglioAccount.GRUPPI,
                CostantiDettaglioAccount.UFFICI_INTESTATARI,
                CostantiDettaglioAccount.TECNICI});
	      }
	    } else {
	      GestioneTab gestoreTab = new GestioneTab();
	      gestoreTab.setTabAttivo(CostantiDettaglioAccount.TECNICI);
	      if(UtilityTags.SCHEDA_MODO_MODIFICA.equals(UtilityTags.getParametro(pageContext,UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))){
	        gestoreTab.setTabSelezionabili(null);
	      }else{
	        gestoreTab.setTabSelezionabili(new String[] {
                CostantiDettaglioAccount.DETTAGLIO,
                CostantiDettaglioAccount.PROFILI,
                CostantiDettaglioAccount.GRUPPI,
                CostantiDettaglioAccount.UFFICI_INTESTATARI, 
                CostantiDettaglioAccount.TECNICI});
          }
	      sessione.setAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB,
	          gestoreTab);
	    }
	    
	    if (sysute!= null)
	      ret = " - \"" + sysute + "\"";
		return ret;
	}

	
}