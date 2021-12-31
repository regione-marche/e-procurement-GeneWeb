/*
 * Created on 15/10/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se una impresa è già registrata sul portale
 *
 * @author Marcello Caminiti
 */
public class ImpresaRegistrataSuPortaleFunction extends
		AbstractFunzioneTag {

	public ImpresaRegistrataSuPortaleFunction() {
		super(3, new Class[] { PageContext.class,String.class, String.class});
	}

	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

	  String codiceDitta = (String) params[1];

	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
          "sqlManager", pageContext, SqlManager.class);

	  String result = "NO";

	  try {
        String select="select count(IDUSER) from W_PUSER where USERENT = ?  and USERKEY1 = ?";
        Long numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{"IMPR",codiceDitta});
        if(numOccorrenze!=null && numOccorrenze.longValue()>0)
          result = "SI";
      } catch (SQLException e) {
          throw new JspException(
              "Errore durante la registrazione sul portale ",e);

      }

	  if(GeneManager.checkOP(pageContext.getServletContext(), CostantiGenerali.OPZIONE_GESTIONE_PORTALE)){

    	  ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(
              CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    	  String abilitazioneGara=profilo.getAbilitazioneGare();
    	  if(abilitazioneGara!=null && "A".equals(abilitazioneGara))
    	    pageContext.setAttribute("isAmministratoreGare", "SI",
                PageContext.REQUEST_SCOPE);
	  }
	  return result;
	}

}