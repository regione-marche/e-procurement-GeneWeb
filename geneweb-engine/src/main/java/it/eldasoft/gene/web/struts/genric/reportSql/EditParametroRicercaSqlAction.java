/*
 * Created on 30-mar-2015
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.reportSql;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per l'apertura della pagina di Edit dei un parametro della ricerca in analisi.
 * 
 * @author Luca.Giacomazzo
 */
public class EditParametroRicercaSqlAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(EditParametroRicercaSqlAction.class);

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi alle
   * ricerche
   */
  private TabellatiManager tabellatiManager;

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action runAction
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("execute: inizio metodo");
    }

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    try {
      // lettura dalla sessione dei parametri relativi alla ricerca in analisi
      HttpSession sessione = request.getSession();
      ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
          sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
      
      String id = request.getParameter("id");
      
      if (StringUtils.isEmpty(id)) {
        id = (String) request.getAttribute("id");
      }
      
      if (StringUtils.isNotEmpty(id)) {
        request.setAttribute("parametroRicercaForm", contenitore.estraiParametro(Integer.parseInt(id)));
      }
      
      List<Tabellato> listaValoriTabellati = this.tabellatiManager.getTabellato(
          CostantiGenRicerche.TIPO_VALORE_TABELLATO);

      boolean isAssociazioneUffIntAbilitata = StringUtils.isNotEmpty((String)
          request.getSession().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO));
      
      // Rmozione del tipo 'Dato tabellato' e rimozione 'Ufficio intestatario' se l'associazione
      // ufficio  intestatario non e' abilitata dal tipo di parametri definibili per i report sql.
      for (int i = listaValoriTabellati.size()-1; i >= 0; i--) {
        Tabellato tabe = listaValoriTabellati.get(i);
        if ("T".equalsIgnoreCase(tabe.getTipoTabellato())) {
          listaValoriTabellati.remove(i);
        }
        if ("UI".equalsIgnoreCase(tabe.getTipoTabellato())) {
          if (isAssociazioneUffIntAbilitata) {
            // Cambio della descrizione del tabellato con quanto indicato nel resources
            tabe.setDescTabellato(this.resBundleGenerale.getString(
                "label.tags.uffint.singolo"));
          } else {
            listaValoriTabellati.remove(i);
          }
        }
      }
      
      request.setAttribute("listaValoriTabellati", listaValoriTabellati);
      
      // set nel request del parameter per disabilitare la navigazione
      // in fase di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // Update del menu tab
      this.setMenuTab(request);

    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("execute: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio del SQL di
   * una ricerca
   * 
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_PARAMETRI);
      gestoreTab.setTabSelezionabili(null);
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_PARAMETRI);
      gestoreTab.setTabSelezionabili(null);
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
    }
  }

}
