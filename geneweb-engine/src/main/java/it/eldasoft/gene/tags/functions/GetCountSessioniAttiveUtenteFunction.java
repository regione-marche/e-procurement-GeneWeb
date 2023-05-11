/*
 * Created on 10/mar/2022
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.tags.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.commons.web.LimitatoreConnessioniUtenti;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

public class GetCountSessioniAttiveUtenteFunction extends
AbstractFunzioneTag{

  public GetCountSessioniAttiveUtenteFunction() {
    super(1, new Class[] { PageContext.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    
    ProfiloUtente profiloUtente = (ProfiloUtente) pageContext.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE, PageContext.SESSION_SCOPE);
    HashMap<String, String[]> sessioni = LimitatoreConnessioniUtenti.getInstance().getDatiSessioniUtentiConnessi();
    List<String> ipList= new ArrayList<String>();
    
    int sessioniAttiveUtente = 0;
    
    if (profiloUtente != null) {
    	// nota bene: i controlli per verificare se l'utenza e' gia' connessa, ha senso se riesco ad estrarre l'utente dalla sessione.
    	// non ha senso invece per gli accessi a pagine fuori applicativo (vedi form di registrazione)
        for(String key :sessioni.keySet()) {
            if(profiloUtente.getLogin().equalsIgnoreCase(sessioni.get(key)[1])){
              sessioniAttiveUtente++;
              //verifichiamo se ci sono ip multipli
              String ip = sessioni.get(key)[0];
              if(!ipList.contains(ip)) {
                  ipList.add(ip);  
              }
            }
            }
    }      
    pageContext.setAttribute("ipConnessioniUtente", ipList);
    
    return sessioniAttiveUtente+"";
  }

}
