/*
 * Created on 20-apr-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Azione che effettua il forward alla pagina di errore generale. La action
 * cerca nel request due parameter 'messageKey' e 'numParam' Il primo
 * rappresenta la chiave (es.: errors.chgPsw.....) con cui leggere nel file
 * *Resources.properties ed inserire nella pagina di destinazione il valore
 * esplicito Il secondo rappresenta il numero di parametri (opzionali) di cui il
 * messaggio necessita. Ad esempio se numParam = 2, la action cerca nel request
 * due parameter con chiave 'arg0' e 'arg1' ed invocherà la funzione aggiungi
 * messaggio opportuna
 * 
 * @author Luca.Giacomazzo
 */
public class ErroreGeneraleAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ErroreGeneraleAction.class);

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    int numeroParametri = 0;

    String target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
    String messageKey = request.getParameter("messageKey");
    String numParam = request.getParameter("numParam");

    String[] valoriArgomentiMessaggio = null;
    if (messageKey != null && messageKey.length() > 0) {
      if (numParam != null) {
        numeroParametri = Integer.parseInt(numParam);
        valoriArgomentiMessaggio = new String[numeroParametri];
        for (int i = 0; i < numeroParametri; i++)
          valoriArgomentiMessaggio[i] = request.getParameter("arg" + i);
      }
      switch (numeroParametri) {
      case 0:
        if (logger.isDebugEnabled())
          logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
        break;
      case 1:
        if (logger.isDebugEnabled()) {
          logger.error(UtilityStringhe.replaceParametriMessageBundle(
              this.resBundleGenerale.getString(messageKey),
              valoriArgomentiMessaggio));
        }
        this.aggiungiMessaggio(request, messageKey, valoriArgomentiMessaggio[0]);
        break;
      case 2:
        if (logger.isDebugEnabled()) {
          logger.error(UtilityStringhe.replaceParametriMessageBundle(
              this.resBundleGenerale.getString(messageKey),
              valoriArgomentiMessaggio));
        }
        this.aggiungiMessaggio(request, messageKey,
            valoriArgomentiMessaggio[0], valoriArgomentiMessaggio[1]);
        break;
      case 3:
        if (logger.isDebugEnabled()) {
          logger.error(UtilityStringhe.replaceParametriMessageBundle(
              this.resBundleGenerale.getString(messageKey),
              valoriArgomentiMessaggio));
        }
        this.aggiungiMessaggio(request, messageKey,
            valoriArgomentiMessaggio[0], valoriArgomentiMessaggio[1],
            valoriArgomentiMessaggio[2]);
        break;
      }
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }
}