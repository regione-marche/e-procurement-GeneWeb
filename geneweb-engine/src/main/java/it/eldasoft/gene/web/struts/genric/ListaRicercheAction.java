/*
 * Created on 04-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.ListaForm;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Dispatch Action per la gestione delle operazioni su singoli oggetti presenti
 * nella lista delle ricerche
 * 
 * @author Luca.Giacomazzo
 */
public class ListaRicercheAction extends AbstractDispatchActionBaseGenRicerche {

  private static final String SUCCESS_ELIMINA = "successElimina";

  /** Logger Log4J di classe */
  static Logger               logger          = Logger.getLogger(ListaRicercheAction.class);

  /**
   * Reference alla classe di business logic per l'accesso ai dati relative alle
   * ricerche
   */
  private RicercheManager     ricercheManager;

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * eliminaMultiplo
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEliminaMultiplo() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }

  public ActionForward eliminaMultiplo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("eliminaMultiplo: inizio metodo");

    // target di default, da modificare nel momento in cui si verificano degli
    // errori
    String target = ListaRicercheAction.SUCCESS_ELIMINA;
    String messageKey = null;

    try {
      ListaForm ricerche = (ListaForm) form;

      // costruzione dell'elenco degli id delle ricerche da rimuovere
      String id[] = ricerche.getId();
      int[] elencoRicerche = new int[id.length];
      for (int i = 0; i < elencoRicerche.length; i++)
        elencoRicerche[i] = Integer.parseInt(id[i]);

      // L.G. 16/03/2007: modifica per cancellazione multipla di ricerche, fra
      // cui le ricerche con modello, alle quali bisogna cancellare il modello
      // associato ed il file corrispondente
      
      this.ricercheManager.deleteRicerche(elencoRicerche,
          (String) request.getSession().getAttribute(
              CostantiGenerali.MODULO_ATTIVO));

    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (IOException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.modelli.delete";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) logger.debug("eliminaMultiplo: fine metodo");

    return mapping.findForward(target);
  }

}