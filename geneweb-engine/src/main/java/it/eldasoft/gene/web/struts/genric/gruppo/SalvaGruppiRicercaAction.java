/**
 * 
 */
package it.eldasoft.gene.web.struts.genric.gruppo;

import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.CambiaTabAction;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action per il salvataggio in sessione della lista dei gruppi associati alla
 * ricerca in analisi o in creazione.
 * 
 * @author Luca Giacomazzo
 */
public class SalvaGruppiRicercaAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger         logger = Logger.getLogger(SalvaGruppiRicercaAction.class);

  private GruppiManager gruppiManager;

  /**
   * @return Ritorna gruppiManager.
   */
  public GruppiManager getGruppiManager() {
    return gruppiManager;
  }

  /**
   * @param gruppiManager
   *        gruppiManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action runAction
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }

    // target di default, da modificare nel momento in cui si verificano dei
    // errori
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    try {
      HttpSession sessione = request.getSession();
      ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
          sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

      GruppiRicercaForm gruppiRicercaForm = (GruppiRicercaForm) form;
      GruppoRicerca gruppo = null;

      // lista di tutti i gruppi esistenti con l'attributo 'associato' di tipo
      // boolean valorizzato a true
      // se il gruppo è associato alla ricerca in analisi e a false altrimenti.
      // La lista è ordinata per nome dei gruppi
      List<?> listaGruppi = this.gruppiManager.getGruppiOrderByNome(
          (String) sessione.getAttribute(CostantiGenerali.PROFILO_ATTIVO));

      // rimozione dal contenitore presente in sessione dell'intero elenco dei
      // gruppi
      // associati alla ricerca in analisi
      contenitore.getElencoGruppi().removeAllElements();

      Set<String> setGruppiAssociati = new HashSet<String>();
      if (gruppiRicercaForm.getIdGruppo() != null) {

        for (int i = 0; i < gruppiRicercaForm.getIdGruppo().length; i++)
          setGruppiAssociati.add((gruppiRicercaForm.getIdGruppo()[i]));

        for (int i = 0; i < listaGruppi.size(); i++) {
          if (setGruppiAssociati.contains(""
              + ((Gruppo) listaGruppi.get(i)).getIdGruppo())) {
            gruppo = new GruppoRicerca((Gruppo) listaGruppi.get(i));
            contenitore.aggiungiGruppo(new GruppoForm(gruppo));
            //F.D. 23/04/07 se la ricerca è associata ad un gruppo non deve essere personale 
            if (contenitore.getTestata().isPersonale())
              contenitore.getTestata().setPersonale(false);
          }
        }
      }

      this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
      request.setAttribute("tab", CambiaTabAction.CODICE_TAB_GRUPPI);

      // L.G. 26/03/2007 modifica per implementazione dei report base
      // Si cambia target se il report e' un report base
      if (CostantiGenRicerche.REPORT_BASE == contenitore.getTestata().getFamiglia().intValue())
        target = target.concat("Base");
      else if (CostantiGenRicerche.REPORT_SQL == contenitore.getTestata().getFamiglia().intValue()) 
        target = target.concat("Sql");
      
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

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }

    return mapping.findForward(target);
  }
}