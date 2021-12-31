/*
 * Created on 31-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.tags.history.UtilityHistory;
import it.eldasoft.gene.tags.utils.UtilityTags;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Action per cancellare tutti i profili dall'application context e ricaricare
 * l'ultima pagina visualizzata, usando l'oggetto history
 * 
 * @author Luca.Giacomazzo
 */
public class ClearProfiliAction extends ActionBaseNoOpzioni {

  static Logger logger = Logger.getLogger(ClearProfiliAction.class);
  
  private GeneManager geneManager;
  
  public void setGeneManager(GeneManager geneManager){
    this.geneManager = geneManager;
  }
  
  /**   UID   */
  private static final long serialVersionUID = 1052666864975712071L;
  
  public ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException {
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String message = null;
    ActionForward actForward = null;

    //GeneManager geneManager = (GeneManager) ctx.getBean("geneManager");
    if(this.geneManager != null && this.geneManager.getProfili() != null){
      this.geneManager.getProfili().removeAll();

      UtilityHistory history = UtilityTags.getUtilityHistory(request.getSession());
      try {
        if(history.size(0) > 0){
          int idx = history.size(0) - 1 >= 0 ? history.size(0) - 1 : 0; 
          actForward = history.vaiA(idx, 0,request);
        }
      } catch (JspException j){
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        message = "Errore nell'accesso dell'oggetto History presente nel context dell'applicazione";
      }
    } else {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      message = "Impossibile recuperare il geneManager nel context dell'applicazione"; 
    }
    
    if(message != null){
      logger.error(message);
      ActionMessages actMes = new ActionMessages();
      actMes.add("a", new ActionMessage(message));
      this.addMessages(request, actMes);
    }
    
    if(actForward != null)
      return actForward;
    else
      return mapping.findForward(target);
  }

}