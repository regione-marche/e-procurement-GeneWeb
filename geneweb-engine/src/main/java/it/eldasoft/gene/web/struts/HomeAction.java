/*
 * Created on 24-05-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per il forward alla home pagina dell'applicazione
 * @author Luca.Giacomazzo
 */
public class HomeAction extends ActionBaseNoOpzioni {
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {

    // Prima di tornare alla home page faccio alcune operazioni:
    // 1. Cancello dalla sessione i seguenti attributi: nomeOggetto, idOggetto, 
    //   ricerca caricata in sessione e l'id della ricerca precedentemente 
    //   caricata in sessione
    request.getSession().removeAttribute(CostantiGenerali.NOME_OGGETTO_SESSION);
    request.getSession().removeAttribute(CostantiGenerali.ID_OGGETTO_SESSION);
    this.cleanSession(request);    
    
    String codiceApplicazione = (String)
        request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    
    return UtilityStruts.redirectToPage("home" + codiceApplicazione + ".jsp",
        false, request);
  }
}