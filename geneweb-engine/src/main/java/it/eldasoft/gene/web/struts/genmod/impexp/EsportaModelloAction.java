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
package it.eldasoft.gene.web.struts.genmod.impexp;

import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.bl.genmod.GestioneFileModelloException;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.db.domain.genmod.ContenitoreDatiModello;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.GruppoModello;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
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
 * Action per l'esportazione su file xml di un modello
 *
 * @author Francesco De Filippis
 */
public class EsportaModelloAction extends DispatchActionBaseNoOpzioni {

  /** logger di Log4J */
  static Logger          logger = Logger.getLogger(EsportaModelloAction.class);

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi ai
   * modelli
   */
  private ModelliManager modelliManager;

  private GruppiManager  gruppiManager;

  /**
   * @param modelliManager
   *        modelliManager da settare internamente alla classe.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * @param gruppiManager
   *        The gruppiManager to set.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }

  public CheckOpzioniUtente getOpzioniEsporta() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }

  public ActionForward esporta(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("esporta: inizio metodo");

    String target = null;
    String messageKey = null;

    String nomeFile = "modello-def.xml";

    ContenitoreDatiModello contenitore = new ContenitoreDatiModello();

    try {
      // Lettura dell'id del modello da esportare in XML
      int idModello = UtilityNumeri.convertiIntero(request.getParameter("id")).intValue();

      // Set dei dati generali del modello
      DatiModello datiModello = this.modelliManager.getModelloById(idModello);
      // L.G. 07/04/2015: set della versione del modello in esportazione
      datiModello.setVersione(CostantiGenModelli.VERSIONE_MODELLO);

      // Estrazione dei parametri associati al modello: tali parametri non sono
      // altro che oggetti di tipo ParametroModello
      List listaParametri = this.modelliManager.getParametriModello(idModello);

      contenitore.setDatiGenModello(datiModello);
      // Inserimento dei parametri nel contenitore dei dati del modello
      Iterator iter = listaParametri.iterator();
      while (iter.hasNext())
        contenitore.aggiungiParametro((ParametroModello) iter.next());

      // Estrazione dei gruppi associati al modello
      List listaGruppi = this.gruppiManager.getGruppiByIdModello(idModello);

      iter = listaGruppi.iterator();
      // Inserimento dei gruppi nel contenitore dei dati del modello
      while (iter.hasNext()) {
        GruppoModello gruppoModello = new GruppoModello((Gruppo) iter.next());
        contenitore.aggiungiGruppo(gruppoModello);
      }

      // Set nel byte[] del file associato al modello
      String nomeFileModello = datiModello.getNomeFile();
      String pathModello = ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI);

      File file = new File(pathModello + nomeFileModello);
      if (!file.exists()) {
        target = CostantiWizard.ERROR_EXPORT_MODELLI;
        messageKey = "errors.genmod.export.modelloInesistente";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      } else {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
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
        } catch (Throwable t) {
          target = CostantiWizard.ERROR_EXPORT_MODELLI;
          messageKey = "errors.genmod.export.letturaModelloKO";
          logger.error(this.resBundleGenerale.getString(messageKey), t);
          this.aggiungiMessaggio(request, messageKey);
        }
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

    // In caso di eccezione rimuovo le eventuali impostazioni per il download
    // del file xml prodotto
    ActionForward forward = null;
    if (target != null) {
      response.reset();
      forward = mapping.findForward(target);
    }

    if (logger.isDebugEnabled()) logger.debug("esporta: fine metodo");

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

  /**
   * Eliminazione di un documento con forward sulla maschera di lista dei
   * modelli per l'export
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward elimina(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("elimina: inizio metodo");
    // Di default rivisualizza il dettaglio del modello
    String target = CostantiGenModelli.FORWARD_MODELLO_EXPORT_ELIMINATO;

    int idModello = -1;
    String messageKey = null;
    try {
      // Estraggo l'identificativo del modello (campo chiave)
      idModello = Integer.parseInt(request.getParameter(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO));
      modelliManager.deleteModello(idModello,
          (String) request.getSession().getAttribute(
              CostantiGenerali.MODULO_ATTIVO));
    } catch (GestioneFileModelloException e) {
      messageKey = "errors.modelli.delete";
      // Aggiungo l'eventuale codice in più
      if (!e.getCodiceErrore().equals("")) messageKey += e.getCodiceErrore();

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
    if (logger.isDebugEnabled()) logger.debug("elimina: fine metodo");

    return mapping.findForward(target);
  }

}