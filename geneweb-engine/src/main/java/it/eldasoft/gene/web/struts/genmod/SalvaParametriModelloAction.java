/*
 * Created on 4-dic-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiIntegrazioneKronos;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.ParametroComposizione;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.db.domain.genric.ParametroRicerca;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Gestisce le azioni provenienti dalla pagina di inserimento dei parametri
 * runtime per la composizione di un modello. Prevede il salvataggio dei dati
 * per l'inoltro al compositore, oppure l'abort dell'elaborazione con ritorno
 * all'elenco dei modelli
 * 
 * @author Stefano.Sabbadin
 * 
 */
public class SalvaParametriModelloAction extends
    DispatchActionBaseNoOpzioni {

  /** logger della classe */
  static Logger            logger = Logger.getLogger(SalvaParametriModelloAction.class);

  /** Manager dei modelli */
  private ModelliManager   modelliManager;

  /** Manager dei tabellati */
  private TabellatiManager tabellatiManager;
  
  /** Manager delle ricerche */
  private RicercheManager  ricercheManager;

  /**
   * @param modelliManager
   *        The modelliManager to set.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }
  
  /**
   * @param ricercheManager ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * Esegue il salvataggio dei parametri, dopodichè richiama la pagina
   * successiva che indica la composizione in corso ed esegue la chiamata al web
   * service.
   */
  public ActionForward salvaEComponiModello(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("salvaEComponiModello: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    ParametriModelloForm parametriModelloForm = null;
    if (request.getAttribute("parametriModelloconIdUtenteForm") != null) {
      parametriModelloForm = (ParametriModelloForm) request.getAttribute("parametriModelloconIdUtenteForm");
    } else {
      parametriModelloForm = (ParametriModelloForm) form;
    }

    try {
      // si estrae il dettaglio del modello
      DatiModello datiModello = this.modelliManager.getModelloById(parametriModelloForm.getIdModello());
      
      int numeroParametriRicerca = 0;
      List elencoDefParametri = new ArrayList();
      
      // 18/03/2010 Sabbadin: introdotti i report con modello con sorgente dati
      // un report base/avanzato: in tal caso occorre estrarre anche gli
      // eventuali parametri del report sorgente dati
      if (datiModello.getIdRicercaSrc() != null) {
        // siamo nel caso di modello con report come sorgente dati, per cui si
        // reperiscono i parametri del report
        List elencoDefParametriRicerca = this.ricercheManager.getParametriRicerca(datiModello.getIdRicercaSrc().intValue());
        if (elencoDefParametriRicerca != null) {
          numeroParametriRicerca = elencoDefParametriRicerca.size();
          ParametroRicerca paramRicerca;
          ParametroModello paramModello; 
          for (int i = 0; i <numeroParametriRicerca; i++) {
            paramRicerca = (ParametroRicerca) elencoDefParametriRicerca.get(i);
            paramModello = paramRicerca.getParametroModello();
            paramModello.setIdModello(parametriModelloForm.getIdModello());
            elencoDefParametri.add(paramModello);
          }
        }
      }

      // si estraggono i parametri del modello e si aggiungono al contenitore
      // delle definizioni dei parametri
      List elencoDefParametriModello = this.modelliManager.getParametriModello(parametriModelloForm.getIdModello());
      elencoDefParametri.addAll(elencoDefParametriModello);

      // creo l'elenco dei parametri da inserire nel DB
      ParametroModello defParametro = null;
      Vector parametri = new Vector();
      ParametroComposizione parametro = null;
      ParametroComposizione parametroTabellato = null;
      for (int i = 0; i < elencoDefParametri.size(); i++) {
        defParametro = (ParametroModello) elencoDefParametri.get(i);

        parametro = new ParametroComposizione();
        parametro.setCodice(defParametro.getCodice());
        parametro.setDescrizione(defParametro.getDescrizione());
        parametri.add(parametro);
        
        String valore = parametriModelloForm.getParametriModello()[i];

        // se è un parametro tabellato
        if ("T".equals(defParametro.getTipo()) && i >= numeroParametriRicerca) {
          // inserisco anche il parametro supplementare per la descrizione del
          // tabellato solo se si riferisce ad un parametro effettivo del
          // modello e non della ricerca sorgente
          DizionarioCampi dizCampi = DizionarioCampi.getInstance();

          // nell'attributo tabellato troviamo il mnemonico del campo associato
          // al tabellato che dobbiamo presentare
          Campo campo = dizCampi.get(defParametro.getTabellato());
          
          String descrizioneTabellato = null;
          if (valore != null && valore.length() > 0)
            descrizioneTabellato = this.tabellatiManager.getDescrTabellato(
                campo.getCodiceTabellato(), valore);
          parametroTabellato = new ParametroComposizione();
          parametroTabellato.setCodice(defParametro.getCodice() + "DESC");
          parametroTabellato.setValore(descrizioneTabellato);
          // aggiungo il parametro alla lista di quelli da inserire...
          parametri.add(parametroTabellato);
        }
        // la data va indicata nel formato GG.MM.AAAA
        if ("D".equals(defParametro.getTipo()))
          valore = UtilityStringhe.replace(valore, "/", ".");
        // un numero decimale ha come separatore dei decimali il "."
        if ("F".equals(defParametro.getTipo()))
          valore = UtilityStringhe.replace(valore, ",", ".");

        parametro.setValore(valore);
      }

      // SS 20090311: inserita gestione parametri kronos
      this.addParametriIntegrazioneKronos(request,
          parametriModelloForm.getTipo(), parametri);

      // SS 20090309: inserita la gestione dei riepilogativi con il salvataggio
      // delle chiavi
      SalvaParametriModelloAction.setDatiChiaviModelloRiepilogativo(
          parametriModelloForm, parametri);

      ParametroComposizione[] parametriComposizione = new ParametroComposizione[parametri.size()];
      // creo l'array da passare al manager per essere inserito
      parametri.toArray(parametriComposizione);

      ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);

      // eseguo la chiamata per l'inserimento
      int idSessione = this.modelliManager.insertParametriComposizione(
          parametriComposizione, profiloUtente.getId(),
          parametriModelloForm.getIdModello());

      // aggiorno i dati da mettere nel request
      parametriModelloForm.setIdSessione(idSessione);
      request.setAttribute("componiModelloForm",
          (ComponiModelloForm) parametriModelloForm);

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

    if (logger.isDebugEnabled())
      logger.debug("salvaEComponiModello: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Se il modello è riepilogativo, allora vanno inseriti nei parametri anche le
   * chiavi degli elementi selezionati
   * 
   * @param parametriModelloForm
   *        form ricevuto con le chiavi degli elementi selezionati e il flag
   *        riepilogativo
   * @param parametri
   *        elenco dei parametri da memorizzare, nel quale aggiungere i
   *        parametri per le chiavi
   */
  public static void setDatiChiaviModelloRiepilogativo(
      ParametriModelloForm parametriModelloForm, Vector parametri) {
    if (parametriModelloForm.getRiepilogativo() == 1) {
      ParametroComposizione parametroPerChiavi = null;
      // si salva il parametro che indica il numero di chiavi
      parametroPerChiavi = new ParametroComposizione();
      parametroPerChiavi.setCodice(CostantiGenModelli.PARAMETRO_RIEPILOGATIVO_NUM_CHIAVI);
      parametroPerChiavi.setValore(String.valueOf(parametriModelloForm.getValChiavi().length));
      parametri.add(parametroPerChiavi);
      int numeroCifre = String.valueOf(
          parametriModelloForm.getValChiavi().length).length();
      // si salvano i parametri delle chiavi
      for (int j = 0; j < parametriModelloForm.getValChiavi().length; j++) {
        parametroPerChiavi = new ParametroComposizione();
        parametroPerChiavi.setCodice(CostantiGenModelli.PARAMETRO_RIEPILOGATIVO_PREFISSO_CHIAVE
            + UtilityStringhe.fillLeft(String.valueOf(j + 1), '0', numeroCifre));
        parametroPerChiavi.setValore(parametriModelloForm.getValChiavi()[j]);
        parametri.add(parametroPerChiavi);
      }
      // tengo solo la prima chiave, fittizia in quanto si useranno le chiavi
      // inserite come parametri, ma al fine di lanciare la composizione una
      // volta sola
      parametriModelloForm.setValChiavi(new String[] { parametriModelloForm.getValChiavi()[0] });
    }
  }

  /**
   * Torna alla pagina con l'elenco dei modelli per richiedere una nuova
   * composizione su un altro modello
   */
  public ActionForward listaModelli(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("listaModelli: inizio metodo");

    String target = "listaModelli";
    request.setAttribute(
        CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_COMPOSIZIONE,
        (ComponiModelloForm) form);

    if (logger.isDebugEnabled()) logger.debug("listaModelli: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Aggiunge al contenitore dei parametri i dati inseriti nella form relativi
   * ai parametri per l'integrazione di Kronos
   * 
   * @param request
   *        request HTTP
   * @param tipoModello
   *        tipo del modello
   * @param parametri
   *        parametri da salvare nel DB
   */
  private void addParametriIntegrazioneKronos(HttpServletRequest request,
      String tipoModello, Vector parametri) {
    if (CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS.equals(ConfigManager.getValore(CostantiGenerali.PROP_INTEGRAZIONE))
        && CostantiIntegrazioneKronos.TIPO_MODELLO_GIUSTIFICATIVO_KRONOS.equals(tipoModello)) {
      String dato = null;
      ParametroComposizione parametroKronos = null;
      // inserimento Data inizio periodo
      dato = UtilityStringhe.replace(
          request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_DATA_INIZIO_PERIODO),
          "/", ".");
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_DATA_INIZIO_PERIODO);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
      // inserimento Data fine periodo
      dato = UtilityStringhe.replace(
          request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_DATA_FINE_PERIODO),
          "/", ".");
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_DATA_FINE_PERIODO);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
      // inserimento Raggruppamento
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_RAGGRUPPAMENTO);
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_RAGGRUPPAMENTO);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
      // inserimento Gruppo
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_GRUPPO);
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_GRUPPO);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
      // inserimento Giustificativo
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_GIUSTIFICATIVO);
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_GIUSTIFICATIVO);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
      // inserimento Escludi Sabati
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_ESCLUDI_SABATI);
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_ESCLUDI_SABATI);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
      // inserimento Escludi Domeniche
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_ESCLUDI_DOMENICHE);
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_ESCLUDI_DOMENICHE);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
      // inserimento Escludi Feste
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_ESCLUDI_FESTE);
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_ESCLUDI_FESTE);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
      // inserimento Dettaglio dipendente
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_DETT_DIPENDENTE);
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_DETT_DIPENDENTE);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
      // inserimento Totali generali
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_TOTALI_GENERALI);
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_TOTALI_GENERALI);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
      // inserimento Totali dipendente
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_TOTALI_DIPENDENTE);
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_TOTALI_DIPENDENTE);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
      // inserimento Raggruppa giustificativo
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_RAGGRUPPA_GIUSTIFICATIVO);
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_RAGGRUPPA_GIUSTIFICATIVO);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
      // inserimento Descrizione turno
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_DESCRIZIONE_TURNO);
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_DESCRIZIONE_TURNO);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
//      // inserimento Conteggio a mesi
//      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_CONTEGGIO_MESI);
//      parametroKronos = new ParametroComposizione();
//      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_CONTEGGIO_MESI);
//      parametroKronos.setValore(dato);
//      parametri.add(parametroKronos);
      // inserimento Giustificativo con note
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_GIUSTIFICATIVO_NOTE);
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_GIUSTIFICATIVO_NOTE);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
      // inserimento Mostra note
      dato = request.getParameter(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_MOSTRA_NOTE);
      parametroKronos = new ParametroComposizione();
      parametroKronos.setCodice(CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_MOSTRA_NOTE);
      parametroKronos.setValore(dato);
      parametri.add(parametroKronos);
    }
  }

}
