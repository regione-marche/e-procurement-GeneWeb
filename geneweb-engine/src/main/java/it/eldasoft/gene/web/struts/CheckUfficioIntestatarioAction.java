/*
 * Created on 5-ott-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.admin.UffintManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.UfficioIntestatario;
import it.eldasoft.utils.profiles.OpzioniUtente;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Azione che verifica l'utilizzo di associative tra account e uffici
 * intestatari (tabella UFFINT), ed in tal caso verifica il numero di
 * associazioni:
 * <ul>
 * <li>0: errore</li>
 * <li>1: imposta l'ufficio intestatario e va alla homepage dell'applicativo</li>
 * <li>n: carica la pagina di selezione ufficio intestatario</li>
 * </ul>
 *
 * @author Stefano.Sabbadin
 */
public class CheckUfficioIntestatarioAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger         logger = Logger.getLogger(CheckUfficioIntestatarioAction.class);

  /**
   * Reference alla classe di business logic per la gestione delle associazioni
   * tra l'utente e l'archivio uffici intestatari
   */
  private UffintManager uffintManager;

  /**
   * @param uffintManager
   *        uffintManager da settare internamente alla classe.
   */
  public void setUffintManager(UffintManager uffintManager) {
    this.uffintManager = uffintManager;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

//    String codiceApplicazione = (String) request.getSession().getAttribute(
//        CostantiGenerali.MODULO_ATTIVO);
    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    String profiloApplicativoSelezionato = (String)request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
    // per il profilo acceduto si resettano le chiavi navigate
    HashMap<String, HashSet<String>> hashProfiliKey = (HashMap<String, HashSet<String>>) request.getSession().getAttribute(CostantiGenerali.PROFILI_KEYS);
    HashMap<String, HashSet<String>> hashProfiliKeyParent = (HashMap<String, HashSet<String>>) request.getSession().getAttribute(CostantiGenerali.PROFILI_KEY_PARENTS);
    hashProfiliKey.put(profiloApplicativoSelezionato, new HashSet<String>());
    hashProfiliKeyParent.put(profiloApplicativoSelezionato, new HashSet<String>());

    OpzioniUtente opzioniUtente = new OpzioniUtente(profiloUtente.getFunzioniUtenteAbilitate());

    try {
      List<?> elencoUfficiIntestatari = null;
      if(opzioniUtente.isOpzionePresente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA)){
        logger.debug("sono un amministratore");
        elencoUfficiIntestatari = this.uffintManager.getUfficiIntestatari();
      } else {
        elencoUfficiIntestatari = this.uffintManager.getUfficiIntestatariAccount(profiloUtente.getId());
      }
      switch (elencoUfficiIntestatari.size()) {
      case 0:
        // elimino la sessione in quanto devo bloccare l'accesso
        request.getSession().invalidate();
        // nessuna associazione => errore
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.checkUfficioIntestatario.noUffintAccount";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
        break;
      case 1:
        // una associazione => attribuisco l'ufficio e proseguo arrivando alla
        // homepage
        // rilegge il codice applicazione come fatto nella CheckVersioneAction
        request.getSession().setAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO,
            ((UfficioIntestatario) elencoUfficiIntestatari.get(0)).getCodice());
        request.getSession().setAttribute(CostantiGenerali.NOME_UFFICIO_INTESTATARIO_ATTIVO,
            ((UfficioIntestatario) elencoUfficiIntestatari.get(0)).getNome());
        request.getSession().removeAttribute(
            CostantiGenerali.SENTINELLA_SELEZIONA_UFFICIO_INTESTATARIO);

//        if (logger.isDebugEnabled()) {
//          logger.debug("runAction: fine metodo");
//        }
//
//        // di default si va alla homepage, a meno che non sia presente un
//        // parametro nel request denominato "skipHome" valorizzato a 1 (tale
//        // parametro arriva valorizzato esclusivamente nel caso di unico profilo
//        // e unico ufficio intestatario attribuiti all'utente, e quindi non si
//        // passa per alcuna pagina di selezione profilo e selezione stazione
//        // appaltante, ma dalla login con un'unica richiesta si aprirà la pagina
//        // di destinazione)
//        if (!"1".equals(request.getParameter("skipHome")))
//          return UtilityStruts.redirectToPage("home"
//              + codiceApplicazione
//              + ".jsp", false, request);
//        else
//          return UtilityStruts.redirectToPage(request.getParameter("href"),
//              false, request);
        break;
      default:
        // potrei ricevere nella chiamata un ufficio intestatario da utilizzare,
        // e dopo aver controllato che sia uno di quelli associati all'utente
        // lo utilizzo, altrimenti vado alla pagina di selezione
        String uffint = request.getParameter("uffint");
        boolean trovato = false;
        if (uffint != null) {
          for (int i = 0; i < elencoUfficiIntestatari.size() && !trovato; i++) {
            UfficioIntestatario ufficio = (UfficioIntestatario) elencoUfficiIntestatari.get(i);
            if (ufficio.getCodice().equals(uffint)) {
              request.getSession().setAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO,
                  ufficio.getCodice());
              request.getSession().setAttribute(CostantiGenerali.NOME_UFFICIO_INTESTATARIO_ATTIVO,
                  ufficio.getNome());
              trovato = true;
            }
          }
        }
        if (!trovato) {
          // uffint passato come parametro non associato all'utente oppure
          // più di una associazione => vado alla pagina di selezione
          request.setAttribute("elencoUfficiIntestatari",
              elencoUfficiIntestatari);
          request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
              CostantiGenerali.DISABILITA_NAVIGAZIONE);
          target = target.concat("Lista");
        }
        request.getSession().setAttribute(
            CostantiGenerali.SENTINELLA_SELEZIONA_UFFICIO_INTESTATARIO, "1");
      }

    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.fatal(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }

    return mapping.findForward(target);
  }

}
