/*
 * Created on 02-apr-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per accesso alla pagina creanNuovaRicercaWizard.jsp per la scelta del tipo
 * di ricerca da creare.
 * 
 * @author Francesco.DeFilippis
 */
public class CreaNuovaRicercaWizardAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger               logger                         = Logger.getLogger(CreaNuovaRicercaWizardAction.class);

  private static final String SUCCESS_NUOVO_REPORT_BASE      = "successBase";
  private static final String SUCCESS_NUOVO_REPORT_AVANZATO  = "successAvanzato";
  private static final String SUCCESS_NUOVO_REPORT_PROSPETTO = "successProspetto";

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    String target = CostantiGeneraliStruts.FORWARD_OK;
    // Se la property it.eldasoft.generatoreRicerche.base.schemaViste
    // e' valorizzata allora è possibile creare ricerche base, altrimenti, la
    // voce relativa ai report base non viene visualizzata nella pagina
    // creaNuovaRicerca.jsp

    // F.D. 08/05/07 controllo le abilitazioni e bypass della pagina di scelta della ricerca
    // da creare se si è abilitati solo ad un tipo di ricerca e non alle altre rimando
    // direttamente alla creazione bypassando la pagina di scelta controllo le abilitazioni
    // per le opzioniAcquistate e per le opzioniUtente del profilo loggato
    // se si è abilitati ad un solo tipo di ricerca viene modificato il target in modo da
    // rimandare alla creazione diretta senza passare dalla scelta

    ServletContext context = request.getSession().getServletContext();
    Collection<String> opzioni = Arrays.asList((String[])
        context.getAttribute(CostantiGenerali.OPZIONI_DISPONIBILI));

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        "profiloUtente");
    OpzioniUtente opzioniUtente = new OpzioniUtente(
        profiloUtente.getFunzioniUtenteAbilitate());

    CheckOpzioniUtente opzioniPerAbilitazioneBase = new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
    CheckOpzioniUtente opzioniPerAbilitazioneAvanzato = new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_AVANZATI);
    CheckOpzioniUtente opzioniPerAbilitazioneProspetto = new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);

    // FASE 1: test di verifica di quali report sono creabili
    Vector<Integer> famiglieUtilizzabili = new Vector<Integer>();
    if (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL)
        && opzioniPerAbilitazioneBase.test(opzioniUtente)) {
      // un report base si può creare solo se esiste lo schema delle viste associate
      String nomeSchemaVista = ConfigManager.getValore(CostantiGenerali.PROP_SCHEMA_VISTE_REPORT_BASE);
      if (nomeSchemaVista != null && nomeSchemaVista.length() > 0) {
        request.setAttribute("nomeSchemaVista", nomeSchemaVista);
        famiglieUtilizzabili.add(new Integer(CostantiGenRicerche.REPORT_BASE));
      }
    }
    //a differenza della creazione classica delle ricerche per i wizard bisogna 
    //essere abilitati alle ricerche professional in tutti i casi
    if (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL)
        && opzioniPerAbilitazioneAvanzato.test(opzioniUtente)) {
      famiglieUtilizzabili.add(new Integer(CostantiGenRicerche.REPORT_AVANZATO));
    }

    if (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL)
        && opzioniPerAbilitazioneProspetto.test(opzioniUtente)) {
      famiglieUtilizzabili.add(new Integer(CostantiGenRicerche.REPORT_PROSPETTO));
    }

    // FASE 2: bypass all'unico report abilitato (se si rientra in questa casistica)
    if (famiglieUtilizzabili.size() == 1) {
      switch (((Integer) famiglieUtilizzabili.elementAt(0)).intValue()) {
      case CostantiGenRicerche.REPORT_BASE:
        target = SUCCESS_NUOVO_REPORT_BASE;
        break;
      case CostantiGenRicerche.REPORT_AVANZATO:
        target = SUCCESS_NUOVO_REPORT_AVANZATO;
        break;
      case CostantiGenRicerche.REPORT_PROSPETTO:
        target = SUCCESS_NUOVO_REPORT_PROSPETTO;
        break;
      }
      request.setAttribute("famiglia",
          (Integer) famiglieUtilizzabili.elementAt(0));
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

}
