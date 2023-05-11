/*
 * Created on 4-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Consente il forward verso una pagina eliminando l'eventuale modulo nel path
 * della richiesta.<br>
 * Classe riadattata in seguito al passaggio a Struts 1.3.10
 * 
 * @author cit_defilippis
 * @author Stefano.Sabbadin
 */
public class ForwardAction extends org.apache.struts.actions.ForwardAction {

  public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    super.execute(mapping, form, request, response);
    // la chiamata a super consente di far eseguire i dovuti controlli e la
    // generazione delle eventuali eccezioni; il forward ritornato dal super
    // viene trascurato, in quanto viene costruito direttamente in questa classe
    ActionForward retVal = new ActionForward(null, mapping.getParameter(),
        false, "");
    return retVal;
  }

}
