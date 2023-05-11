/*
 * Created on 30-lug-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.risultato;

import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.db.domain.genric.DatiRisultato;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.io.export.DatiExport;
import it.eldasoft.utils.io.export.ExportException;
import it.eldasoft.utils.io.export.FactoryExport;
import it.eldasoft.utils.io.export.IExport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
 * Action per l'esportazione di una ricerca dalle pagine di risultato di
 * estrazione di una ricerca e per il download del file appena generato
 *
 * @author Luca.Giacomazzo
 */
public class EsportaRisultatoRicercaAction extends ActionBaseNoOpzioni {

  static Logger logger = Logger.getLogger(EsportaRisultatoRicercaAction.class);

  /**
   * Reference alla classe di business logic per l'accesso ai dati relative alle
   * ricerche
   */
  private RicercheManager ricercheManager;

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    //set del target di default
    String target = null;
    String messageKey = null;

    // Nome del file generato
    String nomeFile = "export.";

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitoreForm = (ContenitoreDatiRicercaForm)
          session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    short formato = Short.parseShort(request.getParameter("formato"));

    try {
      switch(formato){
      case FactoryExport.EXPORT_EXCEL:
        nomeFile = nomeFile.concat("xls");
        break;
      case FactoryExport.EXPORT_CSV:
        nomeFile = nomeFile.concat("csv");
        break;
      case FactoryExport.EXPORT_RTF:
        nomeFile = nomeFile.concat("rtf");
        break;
      case FactoryExport.EXPORT_PDF:
        nomeFile = nomeFile.concat("pdf");
        break;
      case FactoryExport.EXPORT_XLSX:
        nomeFile = nomeFile.concat("xlsx");
        break;
      }

      ContenitoreDatiRicerca contenitorePerModel = contenitoreForm.getDatiPerModel();

      String[] arrayParametriRicerca = null;
      if(contenitorePerModel.getNumeroParametri() > 0){
        if (logger.isDebugEnabled()) logger.debug("La ricerca presenta dei parametri");
        arrayParametriRicerca = (String[])
          session.getAttribute(CostantiGenRicerche.PARAMETRI_PER_ESTRAZIONE);
      }

      String codiceUfficioIntestatarioAttivo = (String) session.getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
      // Estrazione dei dati della ricerca
      DatiRisultato datiRisultato = null;
      DatiExport datiExport = null;
      
      if (CostantiGenRicerche.REPORT_SQL != contenitoreForm.getTestata().getFamiglia().intValue()) {
        datiRisultato = this.ricercheManager.getRisultatiRicerca(
            contenitorePerModel, codiceUfficioIntestatarioAttivo, arrayParametriRicerca, (ProfiloUtente)
            session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE),
            (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO));
        
        // Inizializzazione dell'oggetto che conterra' i dati da esportare
        datiExport = datiRisultato.getDatiExport(
            contenitorePerModel.getDatiGenerali().getNome(),
            contenitorePerModel.getTitoliColonne());
      } else {
        datiRisultato = this.ricercheManager.getRisultatiRicercaSql(
            contenitorePerModel, arrayParametriRicerca,
            (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO));
        
        // Inizializzazione dell'oggetto che conterra' i dati da esportare
        String[] titoliColonne = new String[datiRisultato.getArrayCampi().length];
        for (int u = 0; u < datiRisultato.getArrayCampi().length; u++) {
          titoliColonne[u] = datiRisultato.getArrayCampi()[u].getDescrizione();
        }
        datiExport = datiRisultato.getDatiExport(
            contenitorePerModel.getDatiGenerali().getNome(), titoliColonne);
      }

      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment;filename=\"" +
          nomeFile + "\"");

      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      IExport exp = FactoryExport.getExport(formato);
      exp.setParameters(datiExport, true);
      exp.doExport(baos);
      baos.close();

      response.setContentLength(baos.size());
      baos.writeTo(response.getOutputStream());
    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (OutOfMemoryError e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.genRic.estraiRicerca.outOfMemoryError";
      logger.fatal(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (ExportException e) {
      target = "";
      String logMessageKey = e.getChiaveResourceBundle();
      logger.error(resBundleGenerale.getString(logMessageKey), e);
      messageKey = "errors.export.exportNonEffettuato";
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if(target != null)
      response.reset();

    if (logger.isDebugEnabled()) logger.debug("runAction: FINE metodo");
    return mapping.findForward(target);
  }

}