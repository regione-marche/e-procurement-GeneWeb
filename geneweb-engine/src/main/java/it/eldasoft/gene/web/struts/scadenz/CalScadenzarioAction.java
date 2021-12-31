/*
 * Created on 15/mag/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.scadenz;

import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Apre la pagina del calendario dello scadenzario. Di fatto non effettua nulla, serve esclusivamente per aggiungere la voce nell'history
 * (dato che estende ActionBase), e controllare che tra le opzioni acquistate ci sia lo scadenzario.
 *
 * @author Stefano.Sabbadin
 */
public class CalScadenzarioAction extends ActionBase {

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  @Override
  protected String getOpzioneAcquistata() {
    return "OP128";
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioniRunAction()
   */
  @Override
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(null);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    return mapping.findForward(CostantiGeneraliStruts.FORWARD_OK);
  }

}
