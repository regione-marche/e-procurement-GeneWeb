/*
 * Created on 14-set-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.parametro;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiIntegrazioneKronos;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.dao.KronosDao;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per l'apertura della pagina per l'aggiunta di un parametro ad una
 * ricerca: caricamento dell'elenco degli operatori, delle tabelle visibili
 * nella ricerca e dei relativi campi.
 * 
 * @author Luca Giacomazzo
 */
public class InitAddParametroRicercaAction extends
    AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(InitAddParametroRicercaAction.class);

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi alle
   * ricerche
   */
  private TabellatiManager tabellatiManager;

  private KronosDao        kronosDao;

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param kronosDao
   *        kronosDao da settare internamente alla classe.
   */
  public void setKronosDao(KronosDao kronosDao) {
    this.kronosDao = kronosDao;
  }

  /**
   * Inserisce nella request l'elenco degli operatori, delle tabelle e l'elenco
   * dei campi associati a tali tabelle per inserire un filtro.
   * 
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
        session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    if (CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS.equals(ConfigManager.getValore(CostantiGenerali.PROP_INTEGRAZIONE))
        && CostantiIntegrazioneKronos.TIPO_RICERCA_KRONOS.equals(contenitore.getTestata().getTipoRicerca())) {
      request.setAttribute(CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS, "1");
      List<String> listaVariabiliUtente = this.kronosDao.getVariabiliUTE(CostantiIntegrazioneKronos.PREFISSO_VARIABILE_UTENTE);
      if (listaVariabiliUtente.size() > 1) {
        // nel caso di estrazione di più variabili, si presentano in un
        // ordinamento corretto in base al numero progressivo di variabile
        Integer[] elencoNumeriVariabili = new Integer[listaVariabiliUtente.size()];
        String variabileUtente = null;
        String parteNumerica = null;
        try {
          // si estraggono tutti i numeri di variabile
          for (int i = 0; i < listaVariabiliUtente.size(); i++) {
            variabileUtente = (String) listaVariabiliUtente.get(i);
            parteNumerica = variabileUtente.substring(CostantiIntegrazioneKronos.PREFISSO_VARIABILE_UTENTE.length());
            elencoNumeriVariabili[i] = Integer.valueOf(parteNumerica);
          }
          // si ordinano i numeri
          Arrays.sort(elencoNumeriVariabili);
          // si ricrea l'elenco di variabili correttamente ordinato
          listaVariabiliUtente = new ArrayList<String>();
          for (int i = 0; i < elencoNumeriVariabili.length; i++) {
            listaVariabiliUtente.add(CostantiIntegrazioneKronos.PREFISSO_VARIABILE_UTENTE
                + elencoNumeriVariabili[i].intValue());
          }

        } catch (NumberFormatException e) {
          // non si dovrebbe mai verificare in quanto il formato delle
          // variabili è prefissato e non dipende da noi
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          logger.error("La variabile utente "
              + variabileUtente
              + " non è definita nel formato previsto", e);
          this.aggiungiMessaggio(request,
              "errors.applicazione.inaspettataException");
        }
      }

      request.setAttribute("listaVariabiliUtente", listaVariabiliUtente);
    } else {
      // questo è codice obsoleto, una volta era gestito l'inserimento di
      // parametri, ma ora non viene neanche mai eseguito
      List<Tabellato> listaValoriTabellati = this.tabellatiManager.getTabellato(CostantiGenRicerche.TIPO_VALORE_TABELLATO);
      List<Tabellato> elencoTabellati = this.tabellatiManager.getElencoTabellati(CostantiGenRicerche.TIPO_TABELLATO);

      request.setAttribute("listaValoriTabellati", listaValoriTabellati);
      request.setAttribute("elencoTabellati", elencoTabellati);
    }

    // set nel request del parameter per disabilitare la navigazione in fase di
    // editing e della gestione dei tab
    if (CostantiGeneraliStruts.FORWARD_OK.equals(target)) {
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);
      this.setMenuTab(request);
    }

    if (CostantiGenRicerche.REPORT_SQL == contenitore.getTestata().getFamiglia()) {
      target = target.concat("Sql");
    }
    
    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio dei Dati Generali di
   * una ricerca
   * 
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);

    boolean isInSessione = true;
    if (obj == null) {
      isInSessione = false;
      obj = new GestioneTab();
    }

    GestioneTab gestoreTab = (GestioneTab) obj;
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_PARAMETRI);
    gestoreTab.setTabSelezionabili(null);

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }
}