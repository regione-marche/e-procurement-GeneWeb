/*
 * Created on 21-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiIntegrazioneKronos;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.dao.KronosDao;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.web.struts.genric.parametro.ParametroRicercaForm;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
 * Action che nel caso la ricerca preveda dei parametri apre la lista per il
 * setting dei parametri stessi necessari per l'estrazione di una ricerca
 * 
 * @author Luca Giacomazzo
 */
public class SetParametriRicercaAction extends ActionBaseNoOpzioni {

  private static final String SUCCESS_RICERCA       = "successRicerca";
  private static final String SUCCESS_SET_PARAMETRI = "successSetParametri";

  /** Logger Log4J di classe */
  static Logger               logger                = Logger.getLogger(SetParametriRicercaAction.class);

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi ai
   * tabellati
   */
  private TabellatiManager    tabellatiManager;

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi alle
   * ricerche
   */
  private RicercheManager     ricercheManager;

  /**
   * Reference al DAO per l'interrogazione dei dati di KRONOS
   */
  private KronosDao           kronosDao;

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * @param kronosDao
   *        kronosDao da settare internamente alla classe.
   */
  public void setKronosDao(KronosDao kronosDao) {
    this.kronosDao = kronosDao;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    // target di default
    String target = SetParametriRicercaAction.SUCCESS_RICERCA;

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
        sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    if (contenitore.getNumeroParametri() > 0
        || (CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS.equals(
                ConfigManager.getValore(CostantiGenerali.PROP_INTEGRAZIONE))
            && CostantiIntegrazioneKronos.TIPO_RICERCA_KRONOS.equals(
                contenitore.getTestata().getTipoRicerca()))) {
      this.setAttributePerNavigazione(request);

      // modifica del target per il setting dei parametri della ricerca
      target = SetParametriRicercaAction.SUCCESS_SET_PARAMETRI;

      Vector<ParametroRicercaForm> elencoParametri = contenitore.getElencoParametri();

      List<Tabellato> listaTabellato = null;
      List<String> listaListeTabellati = new ArrayList<String>();
      ParametroRicercaForm parametro = null;

      Vector<String> listaValori = new Vector<String>();
      String valore = null;
      try {
        
        for (int i = 0; i < elencoParametri.size(); i++) {
          parametro = (ParametroRicercaForm) elencoParametri.get(i);
          if (parametro.getTipoParametro().equals("T")
              && parametro.getTabCod() != null) {
            // gli unici tabellati senza tabCod valorizzato sono al momento i
            // tabellati in KRONOS; l'importante e' proteggere l'applicativo da
            // chiamate indesiderate all'elenco dei valori di tabellato quando
            // non c'e' un valore significativo di codice
            listaTabellato = tabellatiManager.getTabellato(parametro.getTabCod());
            listaListeTabellati.add(this.preparaLista(listaTabellato));
          } else if (parametro.getTipoParametro().equals("UC")) {
            
          } else if (parametro.getTipoParametro().equals("UI")) {
            
          }
          valore = this.ricercheManager.getCacheParametroRicerca(
              profiloUtente.getId(),
              Integer.parseInt(contenitore.getTestata().getId()),
              parametro.getCodiceParametro());
          listaValori.add(valore);
        }
        request.setAttribute("listaListeTabellati", listaListeTabellati);
        request.setAttribute("listaValori", listaValori);

        // SS 20090317: inserita gestione parametri kronos
        target = this.checkIntegrazioneKronos(request, contenitore,
            profiloUtente.getId(), (String) profiloUtente.getParametriUtente().get(
                CostantiIntegrazioneKronos.PARAM_ESTERNO_UTENTE),
            (String) profiloUtente.getParametriUtente().get(
                CostantiIntegrazioneKronos.PARAM_ESTERNO_RUOLO), target);

        // set nel request del parameter per disabilitare la navigazione in fase
        // di editing
        request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
            CostantiGenerali.DISABILITA_NAVIGAZIONE);

      } catch (DataAccessException e) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        String messageKey = "errors.database.dataAccessException";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      }
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Letto l'attributo "fromPage" dal request necessario alla pagina di set dei
   * parametri per visualizzare il pulsante "Torna a lista report" o il pulsante
   * "Torna a dettaglio report"
   * 
   * @param request
   *        request HTTP
   */
  private void setAttributePerNavigazione(HttpServletRequest request) {
    if (request.getParameter("fromPage") != null)
      request.setAttribute("fromPage", request.getParameter("fromPage"));
    else if (request.getAttribute("fromPage") != null)
      request.setAttribute("fromPage",
          (String) request.getAttribute("fromPage"));
  }

  /**
   * Verifica se l'applicativo prevede l'integrazione con KRONOS, per cui va ad
   * inserire nel request un attributo in modo da abilitare determinate parti
   * nella pagina di inserimento parametri, nonchè si leggono i valori dei
   * parametri specifici per tale integrazione, che non sono censiti nei
   * parametri standard, ma che si necessitano nell'inserimento dei parametri
   * 
   * @param request
   *        request HTTP
   * @param contenitoreRicerca
   *        contenitore della definizione della ricerca
   * @param idAccount
   *        id dell'utente
   * @param username
   *        username dell'utente
   * @param idRuolo
   *        identificativo del ruolo di accesso dell'utente
   * @param target
   *        target Struts, che verrà modificato nel caso di integrazione con
   *        Kronos
   * @return target modificato a "successSetParametri" se è prevista
   *         l'integrazione con Kronos
   */
  private String checkIntegrazioneKronos(HttpServletRequest request,
      ContenitoreDatiRicercaForm contenitoreRicerca, int idAccount,
      String username, String idRuolo, String target) {
    // verifica se si tratta dell'integrazione kronos
    if (CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS.equals(ConfigManager.getValore(CostantiGenerali.PROP_INTEGRAZIONE))
        && CostantiIntegrazioneKronos.TIPO_RICERCA_KRONOS.equals(contenitoreRicerca.getTestata().getTipoRicerca())) {
      request.setAttribute(CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS, "1");
      this.setAttributePerNavigazione(request);
      target = SetParametriRicercaAction.SUCCESS_SET_PARAMETRI;
      // si passa alla lettura dal DB dei valori impostati l'ultima volta per i
      // parametri
      String valore = null;
      // parametro data inizio validita'
      valore = this.ricercheManager.getCacheParametroRicerca(idAccount,
          Integer.parseInt(contenitoreRicerca.getTestata().getId()),
          CostantiIntegrazioneKronos.PARAM_RICERCA_DATA_INIZIO_VALIDITA);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_RICERCA_DATA_INIZIO_VALIDITA, valore);
      // parametro data fine validita'
      valore = this.ricercheManager.getCacheParametroRicerca(idAccount,
          Integer.parseInt(contenitoreRicerca.getTestata().getId()),
          CostantiIntegrazioneKronos.PARAM_RICERCA_DATA_FINE_VALIDITA);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_RICERCA_DATA_FINE_VALIDITA, valore);
      // // parametro filtra sulla quadratura
      // valore = this.ricercheManager.getCacheParametroRicerca(idAccount,
      // idRicerca, CostantiIntegrazioneKronos.PARAM_RICERCA_FILTRA_QUADRATURA);
      // request.setAttribute(
      // CostantiIntegrazioneKronos.PARAM_RICERCA_FILTRA_QUADRATURA, valore);

      // si verifica se tra i parametri esistono le variabili utente UTE_VAR
      ParametroRicercaForm parametro = null;
      for (int i = 0; i < contenitoreRicerca.getNumeroParametri(); i++) {
        parametro = contenitoreRicerca.estraiParametro(i);
        if (parametro.getCodiceParametro().startsWith(
            CostantiIntegrazioneKronos.PREFISSO_VARIABILE_UTENTE)) {
          // se il parametro è una variabile utente, allora si estraggono i
          // valori e si inseriscono nel request in modo da consentire la
          // selezione all'utente
          List<?> elencoValori = this.kronosDao.getValoriVariabileUTE(
              parametro.getCodiceParametro(), username, idRuolo);
          request.setAttribute("listaValori" + parametro.getCodiceParametro(),
              elencoValori);
        }
      }
    }
    return target;
  }

  /**
   * Metodo che converte la lista di un tabellato in una stringa come
   * concatenazione di: tipoTabellato + '_' + tipoDescTabellato + '_' per
   * ciascun elemento della lista
   * 
   * @param lista
   * @return
   */
  private String preparaLista(List<Tabellato> lista) {
    StringBuffer buffer = new StringBuffer("");
    Tabellato tabellato = null;
    for (int i = 0; i < lista.size(); i++) {
      tabellato = lista.get(i);
      buffer.append(tabellato.getTipoTabellato()
          + "_"
          + tabellato.getDescTabellato()
          + "_");
    }

    return buffer.toString();
  }
}