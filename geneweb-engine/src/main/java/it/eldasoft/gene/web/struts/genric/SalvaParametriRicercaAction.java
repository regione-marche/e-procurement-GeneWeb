package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiIntegrazioneKronos;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.genric.CacheParametroEsecuzione;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.util.ArrayList;

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
 * Action per il salvataggio dei valori dei parametri necessari all'estrazione
 * di una ricerca.
 * 
 * @author Luca Giacomazzo
 */
public class SalvaParametriRicercaAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger           logger = Logger.getLogger(SalvaParametriRicercaAction.class);

  private RicercheManager ricercheManager;

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * Effettua il salvataggio nella sessione dei valori dei parametri necessari
   * all'estrazione di una ricerca
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    ParametriRicercaForm datiDalForm = (ParametriRicercaForm) form;

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    // si salvano i parametri in sessione per eseguire il report e agevolare la
    // stampa
    sessione.setAttribute(CostantiGenRicerche.PARAMETRI_PER_ESTRAZIONE,
        datiDalForm.getParametriRicerca());

    // si ricava l'elenco dei codici dei parametri inseriti
    ArrayList<CacheParametroEsecuzione> elencoParametri = new ArrayList<CacheParametroEsecuzione>();
    CacheParametroEsecuzione parametro = null;
    for (int i = 0; i < contenitore.getElencoParametri().size(); i++) {
      parametro = new CacheParametroEsecuzione();
      parametro.setCodice(contenitore.estraiParametro(i).getCodiceParametro());
      parametro.setValore(datiDalForm.getParametriRicerca()[i]);
      elencoParametri.add(parametro);
    }

    // SS 20090317: inserita gestione parametri kronos
    this.addParametriIntegrazioneKronos(request,
        contenitore.getTestata().getTipoRicerca(), elencoParametri);

    try {
      this.ricercheManager.insertParametriEsecuzione(
          (CacheParametroEsecuzione[]) elencoParametri.toArray(new CacheParametroEsecuzione[0]),
          profiloUtente.getId(),
          Integer.parseInt(contenitore.getTestata().getId()));
    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      String messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Aggiunge al contenitore dei parametri i dati inseriti nella form relativi
   * ai parametri per l'integrazione di Kronos
   * 
   * @param request
   *        request HTTP
   * @param tipoRicerca
   *        tipo della ricerca
   * @param parametri
   *        parametri da salvare nel DB
   */
  private void addParametriIntegrazioneKronos(HttpServletRequest request,
      String tipoRicerca, ArrayList<CacheParametroEsecuzione> parametri) {
    if (CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS.equals(ConfigManager.getValore(CostantiGenerali.PROP_INTEGRAZIONE))
        && CostantiIntegrazioneKronos.TIPO_RICERCA_KRONOS.equals(tipoRicerca)) {
      String dato = null;
      CacheParametroEsecuzione parametroKronos = null;
      // inserimento Data inizio validità
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_RICERCA_DATA_INIZIO_VALIDITA);
      parametroKronos = new CacheParametroEsecuzione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_RICERCA_DATA_INIZIO_VALIDITA);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
      // inserimento Data fine validità
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_RICERCA_DATA_FINE_VALIDITA);
      parametroKronos = new CacheParametroEsecuzione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_RICERCA_DATA_FINE_VALIDITA);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
//      // inserimento Filtra per quadratura
//      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_RICERCA_FILTRA_QUADRATURA);
//      parametroKronos = new CacheParametroEsecuzione();
//      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_RICERCA_FILTRA_QUADRATURA);
//      parametroKronos.setValore(dato);
//      parametri.add(parametroKronos);
    }
  }

}