/*
 * Created on 26-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.permessi;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import it.eldasoft.gene.bl.permessi.PermessiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.permessi.PermessoEntita;

/**
 * Action per il salvataggio dell'associazione utente con l'entita' specificata.
 * In particolare per salvare l'associazione tra:
 * - gli utenti e il lavoro per PL-Web;
 * - gli utenti e la gara per PG-Web;
 *
 * @author Luca.Giacomazzo
 */
public class SalvaPermessiEntitaAction extends ActionBaseNoOpzioni {

  /**   logger di classe   */
  static Logger logger = Logger.getLogger(SalvaPermessiEntitaAction.class);

  /**
   * Reference alla classe di business logic per il popolamento delle comboBox
   * presenti nella pagina
   */
  private PermessiManager      permessiManager;

  /**
   * @param accountManager
   *        accountManager da settare internamente alla classe.
   */
  public void setPermessiManager(PermessiManager permessiManager) {
    this.permessiManager = permessiManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {



    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    try {
      // lettura dal request del nome del campo chiave e del suo valore
      String campoChiave = request.getParameter("campoChiave");
      if(campoChiave == null)
        campoChiave = (String) request.getAttribute("campoChiave");

      String valoreChiave = request.getParameter("valoreChiave");
      if(valoreChiave == null)
        valoreChiave = (String) request.getAttribute("valoreChiave");

      PermessiAccountEntitaForm permessiAccountEntita =
          (PermessiAccountEntitaForm) form;

      if(!"CODGAR".equals(campoChiave) && !"IDMERIC".equals(campoChiave)){
        // Controllo dei diritti di accesso al salvataggio della condivisione del
        // lavoro ai vari utenti. L'accesso a questo funzionalita' e' consentito
        // solo agli utenti proprietari del lavoro o con diritti di amministrazione
        // dei lavori (cioe' USRSYS.SYSAB3 = 'A')
        if(this.bloccaCondivisioneLavori(request)){
          messageKey ="errors.lavori.permessi.noAbilitazione";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
          return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
        }
      }

      boolean inserito = false;
      int numeroTentativi = 0;

      // tento di inserire il record finchè non genero un ID univoco a causa
      // della concorrenza, o raggiungo il massimo numero di tentativi
      while (!inserito
          && numeroTentativi < CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
        try {
          this.permessiManager.updateAssociazioneAccountEntita(permessiAccountEntita);
          inserito = true;
        } catch (DataIntegrityViolationException div) {
          if (numeroTentativi < CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
            logger.error(
                "Fallito tentativo "
                    + (numeroTentativi + 1)
                    + " di inserimento record per chiave duplicata, si ritenta nuovamente",
                div);
            numeroTentativi++;
          }
        }
      }
      if (!inserito
          && numeroTentativi >= CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
        throw new DataIntegrityViolationException(
            "Raggiunto limite massimo di tentativi");
      }

      // set nel request del nome del campo chiave e del suo valore
      request.setAttribute("campoChiave", campoChiave);
      request.setAttribute("valoreChiave", valoreChiave);

    } catch (DataIntegrityViolationException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.inserimento.chiaveDuplicata";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Metodo per stabilire se l'utente puo' accedere alla funzionalita' di condivisione
   * del lavoro sia in visualizzazione che in edit. La funzionalita' e' accessibile
   * se l'utente e' proprietario del lavoro o e' amministratore dei lavori
   *
   * @param request
   * @return Ritorna true se l'utente puo' accedere alla funzionalita' di
   *         condivisione del lavoro, false altrimenti
   */
  private boolean bloccaCondivisioneLavori(HttpServletRequest request){
    boolean result = true;

    // lettura dal request del nome del campo chiave e del suo valore
    String campoChiave = request.getParameter("campoChiave");
    if(campoChiave == null)
      campoChiave = (String) request.getAttribute("campoChiave");

    String valoreChiave = request.getParameter("valoreChiave");
    if(valoreChiave == null)
      valoreChiave = (String) request.getAttribute("valoreChiave");

    if(campoChiave != null && valoreChiave != null){
      ProfiloUtente profiloUtente = (ProfiloUtente)
        request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      String abilitazione = profiloUtente.getAbilitazioneStd();
      //Nel caso di condivisione di una gara si deve controllare il valore del
      //campo USRSYS.SYABG
      if ("CODGAR".equals(campoChiave) || "IDMERIC".equals(campoChiave))
        abilitazione = profiloUtente.getAbilitazioneGare();

      if(!"A".equals(abilitazione)){
        PermessoEntita permessoLavoro =
            this.permessiManager.getPermessoEntitaByIdAccount(campoChiave,
                valoreChiave, profiloUtente.getId());

        if(permessoLavoro != null && permessoLavoro.getProprietario() != null &&
            permessoLavoro.getProprietario().intValue() == 1)
          result = false;
      } else {
        result = false;
      }
    }
    return result;
  }

}