/*
 * Created on 02-ago-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.impexp;

import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.bl.genric.ProspettoManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiProspetto;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.db.domain.genric.DatiGenProspetto;
import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.DatoBase64;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action per l'esportazione su file xml di ricerche base ed avanzate e con
 * prospetto, nonchè l'eliminazione dall'elenco
 *
 * @author Luca.Giacomazzo
 */
public class EsportaRicercaAction extends DispatchActionBaseNoOpzioni {

  /** logger di Log4J */
  static Logger            logger = Logger.getLogger(EsportaRicercaAction.class);

  /**
   * Reference alla classe di business logic per l'accesso ai dati relative alle
   * ricerche
   */
  private RicercheManager  ricercheManager;

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi ai
   * prospetti
   */
  private ProspettoManager prospettoManager;

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi alle
   * ricerche
   */
  private GruppiManager    gruppiManager;

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi ai
   * modelli
   */
  private ModelliManager    modelliManager;

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * @param prospettoManager
   *        prospettoManager da settare internamente alla classe.
   */
  public void setProspettoManager(ProspettoManager prospettoManager) {
    this.prospettoManager = prospettoManager;
  }

  /**
   * @param prospettoManager
   *        prospettoManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }

  /**
   * @param modelliManager modelliManager da settare internamente alla classe.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  public CheckOpzioniUtente getOpzioniEsportaRicerca() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }

  public ActionForward esportaRicerca(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("esportaRicerca: inizio metodo");
    String target = null;
    String messageKey = null;

    String nomeFile = "report-def.xml";
    ContenitoreDatiRicerca contenitore = null;

    try {
      // Bisogna distinguere se l'esportazione e' stata richiesta dalla lista
      // delle ricerche (in questo caso viene passato il parameter 'id' contenente
      // l'id della ricerca da esportare) o dal dettaglio della ricerca stessa (in
      // questo caso si usa direttamente l'oggetto presente in sessione
      if (request.getParameter("id") != null) {
        int idRicerca = UtilityNumeri.convertiIntero(request.getParameter("id")).intValue();
        contenitore = this.ricercheManager.getRicercaByIdRicerca(idRicerca);
      } else {
        contenitore = ((ContenitoreDatiRicercaForm) request.getSession().getAttribute(
            CostantiGenRicerche.OGGETTO_DETTAGLIO)).getDatiPerModel();
      }

      contenitore.getDatiGenerali().setVersione(new Integer(CostantiGenRicerche.VERSIONE_REPORT));

      if (CostantiGenRicerche.REPORT_SQL == contenitore.getDatiGenerali().getFamiglia().intValue()) {
        String tipoDBMS = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
        contenitore.getDatiGenerali().setTipoDB(tipoDBMS);
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      XMLEncoder encoder = new XMLEncoder(baos);
      encoder.writeObject(contenitore);
      encoder.close();
      baos.close();

      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment;filename=\""
          + nomeFile
          + "\"");
      response.setContentLength(baos.size());

      baos.writeTo(response.getOutputStream());

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

    // In caso di eccezione rimuovo le eventuali impostazioni per il download
    // del file xml prodotto
    ActionForward forward = null;
    if (target != null) {
      response.reset();
      forward = mapping.findForward(target);
    }

    if (logger.isDebugEnabled()) logger.debug("esportaRicerca: fine metodo");
    return forward;
  }

  public CheckOpzioniUtente getOpzioniEsportaProspetto() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }

  public ActionForward esportaProspetto(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("esportaProspetto: inizio metodo");

    String target = null;
    String messageKey = null;

    String nomeFile = "report-def.xml";
    ContenitoreDatiProspetto contenitore = new ContenitoreDatiProspetto();

    try {
      // Lettura dell'id del report con modello da esportare in XML
      int idProspetto = UtilityNumeri.convertiIntero(request.getParameter("id")).intValue();

      // Set dei dati generali del report con modello
      DatiGenProspetto datiGenProspetto = this.prospettoManager.getProspettoById(idProspetto);
      contenitore.setDatiGenProspetto(datiGenProspetto);

      // Estrazione dei parametri associati al prospetto: tali parametri non
      // sono altro che oggetti di tipo ParametroModello
      List<?> listaParametri = this.prospettoManager.getParametriModello(
          datiGenProspetto.getDatiGenRicerca().getIdProspetto().intValue());

      // Inserimento dei parametri nel contenitore dei dati del prospetto
      Iterator<?> iter = listaParametri.iterator();
      while (iter.hasNext())
        contenitore.aggiungiParametro((ParametroModello) iter.next());

      // Estrazione dei gruppi associati al prospetto
      List<?> listaGruppi = this.gruppiManager.getGruppiByIdRicerca(idProspetto);

      iter = listaGruppi.iterator();
      // Inserimento dei gruppi nel contenitore dei dati del prospetto
      while (iter.hasNext()) {
        GruppoRicerca gruppoRicerca = new GruppoRicerca((Gruppo) iter.next());
        contenitore.aggiungiGruppo(gruppoRicerca);
      }

      // Sabbadin 02/04/2010: nel caso di export di un report con modello basato
      // su un altro report come sorgente dati, occorre inserire i riferimenti
      // della sorgente dati
      if (datiGenProspetto.getDatiModello().getIdRicercaSrc() != null) {
        DatiGenRicerca reportSorgente = this.ricercheManager.getDatiGenRicerca(datiGenProspetto.getDatiModello().getIdRicercaSrc().intValue());
        contenitore.setNomeReportSorgente(reportSorgente.getNome());
        contenitore.setFamigliaReportSorgente(reportSorgente.getFamiglia());
      }

      // Set nel byte[] del file associato al prospetto
      String nomeFileModello = datiGenProspetto.getDatiModello().getNomeFile();
      String pathModello = ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI);

      File file = new File(pathModello + nomeFileModello);
      if (file.exists()) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        FileInputStream stream = new FileInputStream(file);

        byte[] buffer = new byte[1024];
        int bytesRead = stream.read(buffer);
        while (bytesRead >= 0) {
          if (bytesRead > 0) baos.write(buffer, 0, bytesRead);
          bytesRead = stream.read(buffer);
        }
        stream.close();
        baos.flush();
        baos.close();

        DatoBase64 db64 = new DatoBase64(baos.toByteArray(),
            DatoBase64.FORMATO_ASCII);
        contenitore.setFileModello(new String(db64.getByteArrayDatoBase64()));

        baos = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(baos);
        encoder.writeObject(contenitore);
        encoder.close();
        baos.close();

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=\""
            + nomeFile
            + "\"");
        response.setContentLength(baos.size());

        baos.writeTo(response.getOutputStream());
      } else {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.prospetti.export.modelloInesistente";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
    } catch (IOException e) {
      // la IOException è gestita per l'eventuale errore nella lettura dello
      // stream del file
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.prospetti.export.letturaModelloKO";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
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

    // In caso di eccezione rimuovo le eventuali impostazioni per il download
    // del file xml prodotto
    ActionForward forward = null;
    if (target != null) {
      response.reset();
      forward = mapping.findForward(target);
    }

    if (logger.isDebugEnabled()) logger.debug("esportaProspetto: fine metodo");
    return forward;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action elimina
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniElimina() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }

  public ActionForward elimina(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("elimina: inizio metodo");

    // target di default, da modificare nel momento in cui si verificano degli
    // errori
    String target = "successElimina";
    String messageKey = null;

    try {
      int idRicerca = Integer.parseInt(request.getParameter("idRicerca"));
      if (this.modelliManager.getNumeroModelliCollegatiASorgenteReport(idRicerca).intValue() == 0) {
        // Si usa il metodo ricercheManager.deleteRicerche, perche' esso prima di
        // cancellare divide i report da cancellare in base al tipo (base,
        // avanzato, e con modello). Per i report con modello cancella anche il
        // modello associato
        this.ricercheManager.deleteRicerche(new int[] { idRicerca },
            (String) request.getSession().getAttribute(
                CostantiGenerali.MODULO_ATTIVO));
      } else {
        messageKey = "errors.genRic.eliminaRicerca.referenzeComeSorgente";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }

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

    if (logger.isDebugEnabled()) logger.debug("elimina: fine metodo");

    return mapping.findForward(target);
  }

}