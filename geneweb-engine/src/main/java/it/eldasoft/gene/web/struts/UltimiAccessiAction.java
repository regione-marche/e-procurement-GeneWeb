package it.eldasoft.gene.web.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;

public class UltimiAccessiAction  extends ActionBaseNoOpzioni {
  
static Logger               logger          = Logger.getLogger(UltimiAccessiAction.class);
  
private AccountManager accountManager;
 
public void setAccountManager(AccountManager accountManager) {
  this.accountManager = accountManager;
}

  /**
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */

 @Override
protected ActionForward runAction(ActionMapping mapping, ActionForm form,
     HttpServletRequest request, HttpServletResponse response) {
    
    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profilo.getId());
      
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;
    
    
        
    try {
      
     List<LogEvento> eventi = accountManager.getUltimiAccessi(syscon);
     
     //utilizzo una proprietà del bean non utilizzata (oggevento) come contenitore per presentare le informazioni decodificate 
     for(int i=0; i<eventi.size()-1;i++) {
       switch (eventi.get(i).getLivEvento()) {
          case LogEvento.LIVELLO_INFO:
            eventi.get(i).setOggEvento("Info");
            break;
          case LogEvento.LIVELLO_WARNING:
            eventi.get(i).setOggEvento("Avviso");
            break;
          case LogEvento.LIVELLO_ERROR:
            eventi.get(i).setOggEvento("Errore");
            break;
       }
     }
      
      request.setAttribute("ultimiAccessi", eventi);

    } catch (Exception e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } 
    
    return mapping.findForward(target);
  }
 
}
