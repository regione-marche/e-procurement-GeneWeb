/*
 * Created on 09-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.PdfManager;
import it.eldasoft.gene.bl.docass.DocumentiAssociatiManager;
import it.eldasoft.gene.bl.genmod.CompositoreException;
import it.eldasoft.gene.bl.genmod.GestioneFileModelloException;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.dao.QueryDaoException;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.web.struts.docass.CostantiDocumentiAssociati;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Azione per la composizione di un modello
 *
 * @author marco.franceschin
 */
public class ComponiModelloAction extends DispatchActionBaseNoOpzioni {

  static public final String        FORWARD_ERRORE_COMPOSIZIONE = "errorComposizione";

  static public final String        FORWARD_ERRORE              = "error";

  static public final String        FORWARD_OK_DOWNLOAD_BACK    = "successBackDownload";

  static public final String        FORWARD_OK_ASSOCIA_MODELLO  = "successAssociaModello";

  static public final String        SESSION_MODELLO_COMPOSTO    = "modelloComposto";

  /** logger della classe */
  static Logger                     logger                      = Logger.getLogger(ComponiModelloAction.class);

  /** Manager dei modelli */
  protected ModelliManager          modelliManager;

  /** Manager dei documenti associati */
  private DocumentiAssociatiManager documentiAssociatiManager;

  /**
   * Reference al Manager per la gestione delle protezioni di tabelle e campi
   * rispetto al profilo attivo
   */
  private GeneManager               geneManager;

  /** Manager delle ricerche */
  private RicercheManager           ricercheManager;

  /** Manager per la creazione di file pdf */
  private PdfManager                pdfManager;

  /**
   * @param modelliManager
   *        The modelliManager to set.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * @param documentiAssociatiManager
   *        documentiAssociatiManager da settare internamente alla classe.
   */
  public void setDocumentiAssociatiManager(
      DocumentiAssociatiManager documentiAssociatiManager) {
    this.documentiAssociatiManager = documentiAssociatiManager;
  }

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * @param ricercheManager ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * @param pdfManager pdfManager da settare internamente alla classe.
   */
  public void setPdfManager(PdfManager pdfManager) {
    this.pdfManager = pdfManager;
  }

  /**
   * Funzione che esegue la composizione del modello
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward componiModello(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("componiModello: inizio metodo");
    String target = CostantiGeneraliStruts.FORWARD_OK;
    ComponiModelloForm componiModelloForm = (ComponiModelloForm) form;;
    String messageKey = null;

    // TODO Valorizzare la variabile...
    String codiceUfficioIntestatario = (String) request.getSession().getAttribute(
        CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);

    boolean isModelloPredisposto = componiModelloForm.getPaginaSorgente() == null
        || componiModelloForm.getPaginaSorgente().length() == 0
        || "scheda".equals(componiModelloForm.getPaginaSorgente());

    try {
      // Se in precedenza è stato composto un modello lo elimino dai
      // temporanei
      Object modelloComposto = request.getSession().getAttribute(
          ComponiModelloAction.SESSION_MODELLO_COMPOSTO);
      if (modelloComposto != null) {
        if (modelloComposto instanceof String) {
          // Elimino il modello composto in precedenza
          if (((String)modelloComposto).toUpperCase().endsWith("PDF")) {
            File f = new File(
                ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI)
                    + ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI_OUTPUT)
                    + modelloComposto);
            f.delete();
          } else {
            this.modelliManager.eliminaFileComposto((String) modelloComposto,
                (String) request.getSession().getAttribute(
                    CostantiGenerali.MODULO_ATTIVO));
          }
        }

        request.getSession().setAttribute(
            ComponiModelloAction.SESSION_MODELLO_COMPOSTO, null);
      }

      DatiModello datiModello = this.modelliManager.getModelloById(componiModelloForm.getIdModello());

      if (datiModello.getIdRicercaSrc() == null) {
        // si tratta di un modello standard
        target = this.componiModelloStandard(request, target, componiModelloForm);
      } else {
        // si tratta di un modello con sorgente dati un report
        target = this.componiModelloConSorgenteDatiReport(request, target, componiModelloForm,
            datiModello, codiceUfficioIntestatario);
      }

      // Sabbadin 12/04/20010
      // se si richiede la generazione in formato pdf, viene immediatamente
      // lanciato il processo che genera il documento pdf e si eliminano gli
      // artefatti generati dal compositore
      if (CostantiGeneraliStruts.FORWARD_OK.equals(target)) {
        if (componiModelloForm.getExportPdf() == 1) {
          String nomeFilePdf = this.pdfManager.convertiDocumento(ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI)
              + ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI_OUTPUT)
              + componiModelloForm.getFileComposto());
          this.modelliManager.eliminaFileComposto(
              componiModelloForm.getFileComposto(),
              (String) request.getSession().getAttribute(
                  CostantiGenerali.MODULO_ATTIVO));
          File filePdf = new File(nomeFilePdf);
          componiModelloForm.setFileComposto(filePdf.getName());
          request.getSession().setAttribute(
              ComponiModelloAction.SESSION_MODELLO_COMPOSTO,
              componiModelloForm.getFileComposto());
        }
      }

    } catch (CompositoreException e) {
      // Si è verificato l'errore di composizione
      target = ComponiModelloAction.FORWARD_ERRORE_COMPOSIZIONE;
      messageKey = e.getChiaveResourceBundle();
      if (e.getParametri() == null) {
        logger.error(this.resBundleGenerale.getString(messageKey), e);
      } else if (e.getParametri().length == 1) {
        logger.error(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey), e.getParametri()), e);
      } else {
        logger.error(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey), e.getParametri()), e);
      }
      if (isModelloPredisposto)
        ActionBase.aggiungiMessaggioInSessione(request, messageKey,
            e.getParametri());
      else {
        if (e.getParametri() == null)
          this.aggiungiMessaggio(request, messageKey);
        else {
          switch (e.getParametri().length) {
          case 1:
            this.aggiungiMessaggio(request, messageKey, e.getParametri()[0]);
            break;
          case 2:
            this.aggiungiMessaggio(request, messageKey, e.getParametri()[0], e.getParametri()[1]);
            break;
          }
        }
      }
        this.aggiungiMessaggio(request, messageKey);
    } catch (RemoteException r) {
      target = ComponiModelloAction.FORWARD_ERRORE;
      messageKey = "errors.modelli.compositoreDisattivo";
      logger.error(this.resBundleGenerale.getString(messageKey), r);
      if (isModelloPredisposto)
        ActionBase.aggiungiMessaggioInSessione(request, messageKey, null);
      else
        this.aggiungiMessaggio(request, messageKey);

    } catch (RuntimeException e) {
      target = ComponiModelloAction.FORWARD_ERRORE;
      messageKey = "errors.modelli.exportPdf";
      logger.error(UtilityStringhe.replaceParametriMessageBundle(
          this.resBundleGenerale.getString(messageKey),
          new String[] { e.getMessage() }), e);
      if (isModelloPredisposto)
        ActionBase.aggiungiMessaggioInSessione(request, messageKey, new String[] {e.getMessage()});
      else
        this.aggiungiMessaggio(request, messageKey, e.getMessage());

    } catch (Throwable t) {
      target = ComponiModelloAction.FORWARD_ERRORE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      if (isModelloPredisposto)
        ActionBase.aggiungiMessaggioInSessione(request, messageKey, null);
      else
        this.aggiungiMessaggio(request, messageKey);
    } finally {
      // elimina i parametri utilizzati durante la composizione a prescindere
      // dal fatto che la composizione sia andata a buon fine o meno
      if (componiModelloForm.getIdSessione() != 0)
        this.modelliManager.deleteParametriComposizione(componiModelloForm.getIdSessione());
    }
    if (logger.isDebugEnabled()) logger.debug("componiModello: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Si verifica se l'entità di partenza del modello è visibile da profilo,
   * quindi si richiama la composizione del modello e si aggiornano i dati in
   * sessione
   *
   * @param request
   * @param target
   * @param componiModelloForm
   * @return
   * @throws RemoteException
   * @throws CompositoreException
   */
  private String componiModelloStandard(HttpServletRequest request,
      String target, ComponiModelloForm componiModelloForm)
      throws RemoteException, CompositoreException {
    String messageKey;
    String fileComposto = "";
    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    Tabella tabella = dizTabelle.getDaNomeTabella(componiModelloForm.getEntita());
    boolean isEntPrincVisibile = this.geneManager.getGestoreVisibilitaDati().checkEntitaVisibile(tabella, (String) request.getSession().getAttribute(
            CostantiGenerali.PROFILO_ATTIVO));

    if (!isEntPrincVisibile) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.modelli.componi.modelloNonComponibile";
      logger.error(this.resBundleGenerale.getString(messageKey));
      ActionBase.aggiungiMessaggioInSessione(request, messageKey, null);
    }

    if (!CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE.equals(target)) {
      ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);

      String contesto = null;
      if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI)))
        contesto = profiloUtente.getUfficioAppartenenza();

      // esegue la composizione del modello
      fileComposto = this.modelliManager.componiModello(
          componiModelloForm.getIdModello(), componiModelloForm.getEntita(),
          componiModelloForm.getNomeChiavi(),
          componiModelloForm.getValChiavi(),
          (String) request.getSession().getAttribute(
              CostantiGenerali.MODULO_ATTIVO), profiloUtente.getId(),
          contesto, componiModelloForm.getIdSessione());

      // setta il nome del file generato dalla composizione
      componiModelloForm.setFileComposto(fileComposto);

      // Se tutto è andato a buon fine allora reimposto i parametri per la
      // rilettura delle lista dei modelli da comporre
      // Aggiungo agli attributi la form con i dati di composizione
      request.setAttribute(
          CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_COMPOSIZIONE,
          componiModelloForm);
      // Salvo nella sessione il modello composto
      request.getSession().setAttribute(
          ComponiModelloAction.SESSION_MODELLO_COMPOSTO, fileComposto);
    }
    return target;
  }

  /**
   * Esegue il report sorgente dati, crea l'xml contenente i dati estratti e i
   * parametri inseriti dall'utente, quindi richiama la composizione del report
   * con modello con dati xml
   *
   * @param request
   * @param target
   * @param componiModelloForm
   * @param datiModello
   * @return
   * @throws QueryDaoException
   * @throws SqlComposerException
   * @throws DataAccessException
   * @throws ParserConfigurationException
   * @throws TransformerFactoryConfigurationError
   * @throws TransformerException
   * @throws IOException
   */
  private String componiModelloConSorgenteDatiReport(
      HttpServletRequest request, String target,
      ComponiModelloForm componiModelloForm, DatiModello datiModello, String codiceUfficioIntestatario)
        throws DataAccessException, SqlComposerException, QueryDaoException,
          ParserConfigurationException, TransformerFactoryConfigurationError,
          TransformerException, IOException {

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    String codiceApplicazione = (String) request.getSession().getAttribute(
        CostantiGenerali.MODULO_ATTIVO);
    String idApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_ID_APPLICAZIONE);

    // esegue il report base/avanzato sorgente dati e crea il file xml di input
    // per il modello
    String nomeFileSorgenteDati = this.ricercheManager.getFileXmlRisultatoReport(
        datiModello, codiceUfficioIntestatario, profiloUtente, codiceApplicazione, idApplicazione);

    // si richiama il compositore per la generazione del documento
    if (!CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE.equals(target)) {

      String contesto = null;
      if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI)))
        contesto = profiloUtente.getUfficioAppartenenza();

      // esegue la composizione del modello
      String fileComposto = this.modelliManager.componiModelloSenzaConnessioneDB(
          componiModelloForm.getIdModello(),
          nomeFileSorgenteDati,
          (String) request.getSession().getAttribute(
              CostantiGenerali.MODULO_ATTIVO), profiloUtente.getId(),
          contesto);

      // setta il nome del file generato dalla composizione
      componiModelloForm.setFileComposto(fileComposto);

      // Se tutto è andato a buon fine allora reimposto i parametri per la
      // rilettura delle lista dei modelli da comporre
      // Aggiungo agli attributi la form con i dati di composizione
      request.setAttribute(
          CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_COMPOSIZIONE,
          componiModelloForm);
      // Salvo nella sessione il modello composto
      request.getSession().setAttribute(
          ComponiModelloAction.SESSION_MODELLO_COMPOSTO, fileComposto);
    }

    return target;
  }

  /**
   * Funzione che esegue il download del file composto
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward download(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("downloadFile: inizio metodo");
    String target = null;

    String fileComposto = null;
    String messageKey;
    String codiceApplicazione = (String) request.getSession().getAttribute(
        CostantiGenerali.MODULO_ATTIVO);
    ComponiModelloForm componiModelloForm = (ComponiModelloForm) form;

    try {
      request.setAttribute(
          CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_COMPOSIZIONE,
          componiModelloForm);
      fileComposto = componiModelloForm.getFileComposto();
      modelliManager.downloadFileComposto(fileComposto, codiceApplicazione,
          response);

    } catch (GestioneFileModelloException e) {
      // Eseguo l'eliminazione del file composto
      if (fileComposto != null && fileComposto.length() > 0) {
        if (fileComposto.toUpperCase().endsWith("PDF")) {
          File f = new File(
              ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI)
                  + ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI_OUTPUT)
                  + fileComposto);
          f.delete();
        } else {
          this.modelliManager.eliminaFileComposto(fileComposto,
              (String) request.getSession().getAttribute(
                  CostantiGenerali.MODULO_ATTIVO));
        }
      }
      target = ComponiModelloAction.FORWARD_ERRORE;
      messageKey = "errors.modelli.uploaderror";
      // Aggiungo l'eventuale codice in più
      if (!e.getCodiceErrore().equals(""))
        messageKey += "." + e.getCodiceErrore();
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      ActionBase.aggiungiMessaggioInSessione(request, messageKey, null);

    } catch (Throwable t) {
      target = ComponiModelloAction.FORWARD_ERRORE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      ActionBase.aggiungiMessaggioInSessione(request, messageKey, null);
    }

    ActionForward forward = null;
    if (target != null) {
      response.reset();
      forward = mapping.findForward(target);
    }

    if (logger.isDebugEnabled()) logger.debug("download: fine metodo");

    return forward;
  }

  /**
   * Funzione che ritorna alla lista dei file composti dopo aver ripulito
   * l'ambiente da tutto ciò che è legato alla composizione, ovvero i parametri
   * e il file prodotto.
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward eliminaComposizione(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("eliminaComposizione: inizio metodo");
    String target = ComponiModelloAction.FORWARD_OK_DOWNLOAD_BACK;
    String messageKey;

    try {

      ComponiModelloForm componiModelloForm = (ComponiModelloForm) form;
      // Se in precedenza è stato composto un modello lo elimino dai
      // temporanei
      Object modelloComposto = request.getSession().getAttribute(
          ComponiModelloAction.SESSION_MODELLO_COMPOSTO);
      if (modelloComposto != null) {
        if (modelloComposto instanceof String) {
          // Elimino il modello composto in precedenza
          if (((String)modelloComposto).toUpperCase().endsWith("PDF")) {
            File f = new File(ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI)
                + ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI_OUTPUT) + modelloComposto);
            f.delete();
          } else {
            this.modelliManager.eliminaFileComposto((String) modelloComposto,
                (String) request.getSession().getAttribute(
                    CostantiGenerali.MODULO_ATTIVO));
          }
        }

        request.getSession().setAttribute(
            ComponiModelloAction.SESSION_MODELLO_COMPOSTO, null);
      }
      request.setAttribute(
          CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_COMPOSIZIONE,
          componiModelloForm);

    } catch (CompositoreException e) {
      // Si è verificato l'errore di composizione
      target = ComponiModelloAction.FORWARD_ERRORE;
      messageKey = e.getChiaveResourceBundle();
      if (e.getParametri() == null) {
        logger.error(this.resBundleGenerale.getString(messageKey), e);
      } else if (e.getParametri().length == 1) {
        logger.error(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey), e.getParametri()), e);
      } else {
        logger.error(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey), e.getParametri()), e);
      }
      ActionBase.aggiungiMessaggioInSessione(request, messageKey,
          e.getParametri());
    } catch (RemoteException r) {
      target = ComponiModelloAction.FORWARD_ERRORE;
      messageKey = "errors.modelli.compositoreDisattivo";
      logger.error(this.resBundleGenerale.getString(messageKey), r);
      ActionBase.aggiungiMessaggioInSessione(request, messageKey, null);
    } catch (Throwable t) {
      target = ComponiModelloAction.FORWARD_ERRORE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      ActionBase.aggiungiMessaggioInSessione(request, messageKey, null);
    }

    if (logger.isDebugEnabled())
      logger.debug("eliminaComposizione: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che effettua l'associazione del modello generato all'entita'
   * principale dalla quale e' stata lanciata la composizione e sposta il
   * modello nella cartella dei documenti associati
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward associaModello(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("associaModello: inizio metodo");

    String target = ComponiModelloAction.FORWARD_OK_ASSOCIA_MODELLO;
    String messageKey = null;
    String nomeFileDocAss = null;

    String idApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_ID_APPLICAZIONE);
    String moduloAttivo = (String) request.getSession().getAttribute(
        CostantiGenerali.MODULO_ATTIVO);

    ComponiModelloForm componiModelloForm = (ComponiModelloForm) form;
    GregorianCalendar dataOdierna = new GregorianCalendar();

    String pathServerDocAss = ConfigManager.getValore(CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI);

    String pathClientDocAss = CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT;

    String pathClientDocAssPerEditFile = ConfigManager.getValore(CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI_CLIENT
        + CostantiGenerali.SEPARATORE_PROPERTIES
        + idApplicazione
        + CostantiGenerali.SEPARATORE_PROPERTIES
        + moduloAttivo);

    String pathModelliOut = ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI)
        + ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI_OUTPUT);

    try {
      DatiModello datiModello = this.modelliManager.getModelloById(componiModelloForm.getIdModello());


      // N.B.: nel campo di C0OGGASS.C0ATIT viene posto il nome del modello,
      // presente nell'oggetto ComponiModelloForn come attributo nomeModello
      // (che equivale al campo W_MODELLI.NOME)
      nomeFileDocAss = this.documentiAssociatiManager.associaModello(
          moduloAttivo, componiModelloForm.getEntita(),
          componiModelloForm.getValChiavi()[0], UtilityDate.convertiData(""
              + dataOdierna.get(Calendar.DAY_OF_MONTH)
              + "/"
              + (dataOdierna.get(Calendar.MONTH) + 1)
              + "/"
              + dataOdierna.get(Calendar.YEAR)
              + " "
              + dataOdierna.get(Calendar.HOUR_OF_DAY)
              + ":"
              + dataOdierna.get(Calendar.MINUTE)
              + ":"
              + dataOdierna.get(Calendar.SECOND),
              UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
          componiModelloForm.getNomeModello(),
          componiModelloForm.getFileComposto(), pathServerDocAss,
          pathClientDocAss, pathModelliOut, datiModello.getTipoModello());

      // Metto nel request il form componiModelloForm
      request.setAttribute(
          CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_COMPOSIZIONE,
          componiModelloForm);

      if (!CostantiDocumentiAssociati.DISABILITA_DOWNLOAD.equals(ConfigManager.getValore(CostantiDocumentiAssociati.PROP_DOWNLOAD_DOCUMENTI_ASSOCIATI))) {
        request.setAttribute("download", "1");
      }
      // Metto nel request il path + nomeFile del documento da aprire
      request.setAttribute("nomeFileDocAss", StringUtils.defaultString(pathClientDocAssPerEditFile)
          + nomeFileDocAss);

    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      if (e instanceof DataIntegrityViolationException)
        messageKey = "errors.database.inserimento.chiaveDuplicata";
      else
        messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      ActionBase.aggiungiMessaggioInSessione(request, messageKey, null);
    } catch (IOException io) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = CostantiGenModelli.MSG_COPIA_MODELLO_KO;
      logger.error(this.resBundleGenerale.getString(messageKey), io);
      ActionBase.aggiungiMessaggioInSessione(request, messageKey, null);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      ActionBase.aggiungiMessaggioInSessione(request, messageKey, null);
    }

    if (logger.isDebugEnabled()) logger.debug("associaModello: fine metodo");
    return mapping.findForward(target);
  }

}