/*
 * Created on 30-mar-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.List;

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
 * Inizializzazione della pagina TrovaAccount, con verifica della presenza in
 * sessione dell'oggetto TrovaAccountForm. Se non trova nessuna istanza di tale
 * classe allora la crea con gli attributi a 'null'. Se trova l'oggetto, allora
 * ne copia gli attributi in un form opportuno, inserendolo nel request.
 *
 * @author Stefano.Sabbadin
 */
public class InitTrovaAccountAction extends AbstractActionBaseAdmin {

  /** Logger Log4J di classe. */
  static Logger logger = Logger.getLogger(InitTrovaAccountAction.class);

  /** Manager per la gestione di dati tabellati. */
  protected TabellatiManager tabellatiManager;

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  @Override
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_LETTURA_ADMIN);
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    logger.debug("runAction: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    // cancellazione di oggetti in sessione precedentemente creati
    this.cleanSession(request);

    // Rimozione dalla sessione di oggetti comuni ai vari moduli
    // dell'applicazione, quali
    // CostantiGenerali.ID_OGGETTO_SESSION e
    // CostantiGenerali.NOME_OGGETTO_SESSION
    HttpSession sessione = request.getSession();
    sessione.removeAttribute(CostantiGenerali.ID_OGGETTO_SESSION);
    sessione.removeAttribute(CostantiGenerali.NOME_OGGETTO_SESSION);

    String messageKey = null;
    try {
      // Set nel request delle liste per il popolamento delle varie combobox
      // lista per il popolamento della comboBox 'Risultati per Pagina'
      request.setAttribute("listaRisPerPagina", CostantiGenerali.CBX_RIS_PER_PAGINA);
      // lista per il popolamento della comboBox dei valori della combobox di
      // confronto fra stringhe
      request.setAttribute("listaValueConfrontoStringa",
          GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA);
      // lista per il popolamento della comboBox dei testi della combobox di
      // confronto fra stringhe
      request.setAttribute("listaTextConfrontoStringa",
          GestioneOperatoreConfrontoStringa.CBX_TESTO_CONFRONTO_STRINGA);

      List<Tabellato> listaUffAppartenenza = this.tabellatiManager.getTabellato(TabellatiManager.UFF_APPARTENENZA);
      request.setAttribute("listaUffAppartenenza", listaUffAppartenenza);

      List<Tabellato> listaCategorie = this.tabellatiManager.getTabellato(TabellatiManager.CATEGORIE_UTENTE);
      request.setAttribute("listaCategorie", listaCategorie);

      // Se c'è in sessione l'oggetto trova account la carico nel request per
      // predisporre la pagina di trova, altrimenti lo creo
      if (request.getSession().getAttribute(
          CostantiDettaglioAccount.TROVA_ACCOUNT) != null) {
        request.setAttribute("trovaAccountForm",
            request.getSession().getAttribute(
                CostantiDettaglioAccount.TROVA_ACCOUNT));
      } else {
        TrovaAccountForm trovaAccountForm = new TrovaAccountForm();
        trovaAccountForm.setNoCaseSensitive(Boolean.TRUE.toString());
        request.setAttribute("trovaAccountForm", trovaAccountForm);
      }

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

    logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

}
