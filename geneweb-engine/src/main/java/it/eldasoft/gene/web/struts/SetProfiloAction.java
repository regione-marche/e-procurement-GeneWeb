/*
 * Created on 16-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.admin.ProfiliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.admin.Profilo;
import it.eldasoft.gene.tags.history.UtilityHistory;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Azione per settare in sessione il profilo attivo scelto al momento della
 * autentificazione dell'utente
 *
 * @author Luca.Giacomazzo
 */
public class SetProfiloAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger          logger = Logger.getLogger(SetProfiloAction.class);

  private ProfiliManager profiliManager;

  private AccountManager accountManager;

  public void setProfiliManager(ProfiliManager profiliManager) {
    this.profiliManager = profiliManager;
  }

  public void setAccountManager(AccountManager AccountManager) {
    this.accountManager = AccountManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;
    String codiceProfilo = null;

    int livEvento = 3;
    String errMsgEvento = this.resBundleGenerale.getString("errors.applicazione.inaspettataException");

    try{
      if (request.getParameter("profilo") != null)
        codiceProfilo = request.getParameter("profilo");
      else
        codiceProfilo = (String) request.getAttribute("profilo");

      if (codiceProfilo != null) {
        Profilo profiloSelezionato = this.profiliManager.getProfiloByCodProfilo(codiceProfilo);
        // Set nel request il codice applicazione attivo o modulo attivo
        request.setAttribute("codApp", profiloSelezionato.getCodApp());
        // Set in sessione il codice e il nome del profilo attivo
        request.getSession().setAttribute(CostantiGenerali.PROFILO_ATTIVO,
            codiceProfilo);
        request.getSession().setAttribute(CostantiGenerali.NOME_PROFILO_ATTIVO,
            profiloSelezionato.getNome());
        request.getSession().setAttribute(CostantiGenerali.DESC_PROFILO_ATTIVO,
            profiloSelezionato.getDescrizione());
        //Set in sessione modulo attivo
        request.getSession().setAttribute(CostantiGenerali.MODULO_ATTIVO, profiloSelezionato.getCodApp());
        // si va a verificare se esiste un prefisso $<valore>$ nel codice profilo,
        // ed in tal caso si valorizza il filtro di profilo in sessione
        String filtroProfilo = null;
        if (codiceProfilo.startsWith("$") && codiceProfilo.indexOf('$', 1) > 1) {
          filtroProfilo = codiceProfilo.substring(1,
              codiceProfilo.indexOf('$', 1));
        }
        if (filtroProfilo != null) {
          request.getSession().setAttribute(
              CostantiGenerali.FILTRO_PROFILO_ATTIVO, filtroProfilo);
        } else {
          request.getSession().removeAttribute(
              CostantiGenerali.FILTRO_PROFILO_ATTIVO);
        }
        ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);

        // Set nell'oggetto ProfiloUtente presente in sessione della lista dei
        // gruppi associati all'utente, filtrati il codice profilo attivo
        List<Integer> listaGruppi = this.accountManager.getListaGruppiAccountByCodAppCodPro(
            profiloUtente.getId(), profiloSelezionato.getCodApp(), codiceProfilo);

        if (this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
          if (listaGruppi.size() == 1)
            profiloUtente.setIdGruppi(new Integer[] { listaGruppi.get(0) });
          else {
            if (listaGruppi.size() < 1){
              messageKey = "errors.setProfilo.noGruppoDefault";
            }else{
              messageKey = "errors.setProfilo.noGruppoDefaultUnivoco";
            }
            livEvento = 3;
            errMsgEvento = this.resBundleGenerale.getString(messageKey);
            // elimino la sessione in quanto devo bloccare l'accesso
            request.getSession().invalidate();
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            logger.error(UtilityStringhe.replaceParametriMessageBundle(
                this.resBundleGenerale.getString(messageKey),
                new String[] { profiloSelezionato.getNome() }));
            this.aggiungiMessaggio(request, messageKey,
                profiloSelezionato.getNome());
          }
        } else {
          if (listaGruppi != null && listaGruppi.size() > 0) {
            Integer[] idGruppi = listaGruppi.toArray(new Integer[0]);
            profiloUtente.setIdGruppi(idGruppi);
          }
        }
        if (target.equals(CostantiGeneraliStruts.FORWARD_OK)){
          livEvento = 1;
          errMsgEvento = "";
          logger.info("L'utente "
              + profiloUtente.getNome()
              + "(id = "
              + profiloUtente.getId()
              + ") accede all'applicativo (codApp = "
              + profiloSelezionato.getCodApp()
              + ") con il profilo "
              + profiloSelezionato.getNome()
              + "(Codice Profilo = "
              + codiceProfilo
              + ")");
        }
      } else {
        // elimino la sessione in quanto devo bloccare l'accesso
        request.getSession().invalidate();

        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.setProfilo.noProfilo";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }

    }finally{
      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(livEvento);
      logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_SET_PROFILO);
      logEvento.setDescr("Accesso al profilo applicativo");
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    if(CostantiGeneraliStruts.FORWARD_OK.equals(target)){
      UtilityTags.getUtilityHistory(request.getSession()).clear(
          UtilityStruts.getNumeroPopUp(request));
    }
    
    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

}