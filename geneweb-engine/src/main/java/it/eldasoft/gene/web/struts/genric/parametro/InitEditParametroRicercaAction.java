/**
 * 
 */
package it.eldasoft.gene.web.struts.genric.parametro;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;

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

/**
 * @author Luca Giacomazzo
 */
public class InitEditParametroRicercaAction extends
    AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(InitEditParametroRicercaAction.class);

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

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("modifica: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    String id = request.getParameter("id");

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    ParametroRicercaForm parametro = (ParametroRicercaForm) contenitore.estraiParametro(Integer.parseInt(id));

    List<Tabellato> listaValoriTabellati = this.tabellatiManager.getTabellato(CostantiGenRicerche.TIPO_VALORE_TABELLATO);
    String descrizioneTabCod = null;

    for (int i = 0; i < listaValoriTabellati.size(); i++) {
      Tabellato tabellato = (Tabellato) listaValoriTabellati.get(i);
      if (tabellato.getTipoTabellato().equals(parametro.getTipoParametro()))
        descrizioneTabCod = tabellato.getDescTabellato();
    }

    request.setAttribute("parametroRicercaForm", parametro);
    request.setAttribute("descrizioneTabCod", descrizioneTabCod);

    // set nel request del parameter per disabilitare la navigazione in fase di
    // editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

    this.setMenuTab(request);
    if (logger.isDebugEnabled()) logger.debug("modifica: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio dei Filtri di una
   * ricerca
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
    gestoreTab.setTabSelezionabili(new String[] {
        CostantiGenRicerche.TAB_DATI_GENERALI, CostantiGenRicerche.TAB_GRUPPI,
        CostantiGenRicerche.TAB_ARGOMENTI, CostantiGenRicerche.TAB_CAMPI,
        CostantiGenRicerche.TAB_JOIN, CostantiGenRicerche.TAB_FILTRI,
        CostantiGenRicerche.TAB_ORDINAMENTI, CostantiGenRicerche.TAB_LAYOUT});
    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }
}