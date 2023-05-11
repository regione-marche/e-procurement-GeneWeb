/*
 * Created on 18-set-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.prospetto;

import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.bl.genric.ProspettoManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiProspetto;
import it.eldasoft.gene.db.domain.genric.DatiGenProspetto;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.DettaglioRicercaAction;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityNumeri;

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
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Action per la copia di un report con modello secondo lo stile di windows: a
 * partire dall'id del report con modello si recuperano tutte le informazioni
 * relative a tale report (dati generali ricerca, dati generali modello, gruppi,
 * parametri) e li si inserisce nell'oggetto ContenitoreDatiProspetto.
 * Una volta generato il nuovo titolo del report (e quindi del modello) si
 * effettua l'insert del prospetto sfruttando il metodo importProspetto della
 * classe ProspettoManager. In caso di eccezione DataIntegrityViolation, si
 * riprova aumentando il progressivo.
 *
 * @author Luca.Giacomazzo
 */
public class CopiaProspettoAction extends AbstractActionBaseGenRicerche {

  /**   logger di Log4J   */
  static Logger logger = Logger.getLogger(CopiaProspettoAction.class);

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi ai
   * prospetti
   */
  private ProspettoManager prospettoManager;

  /**
   * @param prospettoManager
   *        prospettoManager da settare internamente alla classe.
   */
  public void setProspettoManager(ProspettoManager prospettoManager) {
    this.prospettoManager = prospettoManager;
  }

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi alle
   * ricerche
   */
  private GruppiManager gruppiManager;

  /**
   * @param prospettoManager
   *        prospettoManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {

    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    ContenitoreDatiProspetto contenitore = new ContenitoreDatiProspetto();

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    String contesto = null;
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI)))
      contesto = profiloUtente.getUfficioAppartenenza();


    try {
      // Lettura dell'id del report con modello da esportare in XML
      int idRicerca = UtilityNumeri.convertiIntero(request.getParameter("id")).intValue();

      // Set dei dati generali del report con modello
      DatiGenProspetto datiGenProspetto = this.prospettoManager.getProspettoById(idRicerca);

      // il codice di pubblicazione su web viene sbiancato in quanto deve essere
      // univoco e si decidera' che fare una volta generato il report
      datiGenProspetto.getDatiGenRicerca().setCodReportWS(null);

      contenitore.setDatiGenProspetto(datiGenProspetto);

      // Estrazione dei parametri associati al prospetto: tali parametri non sono
      // altro che oggetti di tipo ParametroModello
      List<?> listaParametri = this.prospettoManager.getParametriModello(
          datiGenProspetto.getDatiGenRicerca().getIdProspetto().intValue());

      // Inserimento dei parametri nel contenitore dei dati del prospetto
      Iterator<?> iter = listaParametri.iterator();
      while(iter.hasNext())
        contenitore.aggiungiParametro((ParametroModello) iter.next());

      // Estrazione dei gruppi associati al prospetto
      List<?> listaGruppi = this.gruppiManager.getGruppiByIdRicerca(idRicerca);

      iter = listaGruppi.iterator();
      // Inserimento dei gruppi nel contenitore dei dati del prospetto
      while(iter.hasNext()){
        GruppoRicerca gruppoRicerca = new GruppoRicerca((Gruppo) iter.next());
        contenitore.aggiungiGruppo(gruppoRicerca);
      }

      // Set nel byte[] del file associato al prospetto
      String nomeFileModello = datiGenProspetto.getDatiModello().getNomeFile();
      String pathModello = ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI);

      File file = new File(pathModello + nomeFileModello);
      if(!file.exists()){
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.prospetti.export.modelloInesistente";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        FileInputStream stream = new FileInputStream(file);

        byte[] buffer = new byte[1024];
        int bytesRead = stream.read(buffer);
        while (bytesRead >= 0) {
          if (bytesRead > 0)
            baos.write(buffer, 0, bytesRead);
          bytesRead = stream.read(buffer);
        }
        stream.close();
        baos.flush();
        baos.close();
      } catch (Throwable t) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.prospetti.export.letturaModelloKO";
        logger.error(this.resBundleGenerale.getString(messageKey), t);
        this.aggiungiMessaggio(request, messageKey);
      }
      // Fine della lettura del report con modello da copiare

      // Per copiare il report prima si carica in sessione il report esistente,
      // dopo gli si cambia il titolo ed in fine, sfruttando il metodo
      // importProspetto della classe prospettoManager, si effettua l'inserimento
      // del report copiato.

      // Determino il nuovo titolo del report:
      String nomeRicercaOriginale = contenitore.getDatiGenProspetto().getDatiGenRicerca().getNome();
      // si modifica il nome univoco impostando un prefisso in "stile Windows"
      String nomeRicercaClonata = null;
      int progressivo = 1;
      boolean inserimentoValido = false;
      do {
        nomeRicercaClonata = DettaglioRicercaAction.PREFISSO1_NOME_COPIATURA
            + (progressivo == 1 ? "" : ("(" + progressivo + ") "))
            + DettaglioRicercaAction.PREFISSO2_NOME_COPIATURA
            + nomeRicercaOriginale;
        if (nomeRicercaClonata.length() > 50)
          nomeRicercaClonata = nomeRicercaClonata.substring(0, 50);

        contenitore.getDatiGenProspetto().getDatiGenRicerca().setNome(nomeRicercaClonata);
        contenitore.getDatiGenProspetto().getDatiModello().setNomeModello(nomeRicercaClonata);
        // si definisce come owner l'utente che richiede la copia
        contenitore.getDatiGenProspetto().getDatiGenRicerca().setOwner(new Integer(profiloUtente.getId()));
        contenitore.getDatiGenProspetto().getDatiModello().setOwner(new Integer(profiloUtente.getId()));
        // si effettua l'inserimento dell'oggetto clonato
        try {
          // Esecuzione dell'insert del report con modello
          this.prospettoManager.importProspetto(contenitore,
              (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO),
              profiloUtente.getId(), baos.toByteArray(), contesto);

          inserimentoValido = true;
        } catch (DataIntegrityViolationException e) {
          logger.error("Fallito tentativo " + progressivo +
              " di inserimento record per chiave duplicata, si ritenta " +
              "nuovamente", e);
          inserimentoValido = false;
          progressivo++;
        }
      } while (!inserimentoValido);
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

    if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

}