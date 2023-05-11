/*
 * Created on 26-0tt-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.docass;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.docass.DocumentiAssociatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.docass.DocumentoAssociato;
import it.eldasoft.gene.tags.history.UtilityHistory;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.GestoreEccezioni;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * DispatchAction per il download/upload di un docmuento associato
 *
 * @author Luca Giacomazzo
 */
public class DocumentoAssociatoAction extends DispatchActionBaseNoOpzioni {

  private static final String SUCCESS_NUOVO            = "successNuovo";
  private static final String SUCCESS_SALVA            = "successSalva";
  private static final String SUCCESS_MODIFICA         = "successModifica";
  private static final String SUCCESS_DOWNLOAD         = null;
  private static final String ERROR_DOWNLOAD_LISTA     = "errorDownloadLista";
  private static final String ERROR_DOWNLOAD_DETTAGLIO = "errorDownloadDettaglio";
  private static final String ERROR_MODIFICA           = "errorModifica";
  private static final String ERROR_INSERT             = "errorModifica"; //"ritornaAInsertDoc";

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(DocumentoAssociatoAction.class);

  /**
   * Reference al manager per la gestione delle operazioni di download e upload
   * di documenti associati
   */
  private DocumentiAssociatiManager documentiAssociatiManager;

  /**
   * Reference al manager per la gestione dei file da gestire dall'applicazione
   * web
   */
  private FileManager fileManager;

  /** Manager dei tabellati */
  private TabellatiManager    tabellatiManager;

  /**
   * @param documentiAssociatiManager
   *        documentiAssociatiManager da settare internamente alla classe.
   */
  public void setDocumentiAssociatiManager(
      DocumentiAssociatiManager documentiAssociatiManager) {
    this.documentiAssociatiManager = documentiAssociatiManager;
  }

  /**
   * @param fileManager fileManager da settare internamente alla classe.
   */
  public void setFileManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }

  /**
   * @param tabellatiManager
   *        The tabellatiManager to set.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * Richiesta di download da parte dell'utente
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

    if (logger.isDebugEnabled()) logger.debug("download: inizio metodo");
    String messageKey = null;
    String target = SUCCESS_DOWNLOAD;

    // Variabile per distinguere se il download avviene dalla lista dei documenti
    // associati o dal dettaglio di un documento associato:
    // pageFrom = "lista" --> download dalla lista dei documenti associati
    // pageFrom = "dett" o null --> download dal dettaglio di un documento associato
    String pageFrom = null;
    pageFrom = request.getParameter("pageFrom");

    String idDoc = request.getParameter("id");
    if(idDoc == null)
      idDoc = (String) request.getAttribute("id");
    long id = Long.parseLong(idDoc);

    DocumentoAssociato documento = null;
    try {
      // Estraggo da DB il bean DocumentoAssociato a partire dall'Id, per risalire
      // al nome del file da scaricare
      documento = this.documentiAssociatiManager.getDocumentoAssociatobyId(id);

      String pathDocAss = null;
      if(CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT.equals(documento.getPathDocAss())){
        pathDocAss = ConfigManager.getValore(CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI);
      } else {
        pathDocAss = documento.getPathDocAss();
      }
      if (logger.isDebugEnabled())
        logger.debug("Download documento associato: " + pathDocAss +
          documento.getNomeDocAss());

      // Download del documento associato richiesto tramite fileManager
      this.fileManager.download(pathDocAss, documento.getNomeDocAss(), response);

    } catch (FileManagerException fm) {
      String logMessage = this.resBundleGenerale.getString(
          fm.getChiaveResourceBundle());
      for(int i=0; i < fm.getParametri().length; i++)
        logMessage = logMessage.replaceAll("\\{" + i + "\\}", fm.getParametri()[i].toString());
      logger.error(logMessage, fm);

      messageKey = "errors.documentiAssociati.download";
      this.aggiungiMessaggio(request, messageKey,documento.getNomeDocAss());
    } catch (DataAccessException da){
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), da);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    } finally {
      if(messageKey != null){
        // Si e' verificata un'eccezione e quindi bisogna ricaricare la pagina
        // da cui e' stato fatto il download del documento associato. Per
        //distinguere, uso la variabile pageFrom
        if("lista".equals(pageFrom)){
          target = ERROR_DOWNLOAD_LISTA;
          // Set nel request dei parametri per aprire la lista dei documenti
          // associati di partenza
          request.setAttribute("entita" , documento.getEntita());
          request.setAttribute("valori" ,
              "campo1:" + documento.getCampoChiave1() + ";" +
              "campo2:" + documento.getCampoChiave2() + ";" +
              "campo3:" + documento.getCampoChiave3() + ";" +
              "campo4:" + documento.getCampoChiave4() + ";" +
              "campo5:" + documento.getCampoChiave5());
        } else if("dett".equals(pageFrom) || pageFrom == null)
          target = ERROR_DOWNLOAD_DETTAGLIO;
          // Set nel request dell'id del documento di cui si stava
          // visualizzando il dettaglio
          request.setAttribute("id", "" + documento.getId());
      }
    }
    if (logger.isDebugEnabled()) logger.debug("download: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Action per la visualizzazione del dettaglio di un documento associato a
   * partire dall'id del documento stesso
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward visualizza(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("Visualizza: inizio metodo");

    String target = this.caricaDocumentoAssociato(request);

    // Se e' abilitato il download dei documenti associati, allora il client
    // può solo effettuare il download in locale di una copia del file, altrimenti
    // può aprire su una nuova finestra il file ed eventualmente modificarlo.
    // Nel caso in cui sia abilitato il download dei documenti associati, allora
    // inserisco nel request un attributo che permette di scegliere al momento
    // della compilazione della pagina jsp quale chiamata JS effettuare
    if(!CostantiDocumentiAssociati.DISABILITA_DOWNLOAD.equals(
        ConfigManager.getValore(
            CostantiDocumentiAssociati.PROP_DOWNLOAD_DOCUMENTI_ASSOCIATI)))
      request.setAttribute("download", "1");

    if(target.equals(CostantiGeneraliStruts.FORWARD_OK))
      target = target.concat("Visualizza");

    if (logger.isDebugEnabled()) logger.debug("Visualizza: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Metodo per annullare la modifica del dettaglio di un documento associato e
   * ritornare alla visualizzazione del dettaglio stesso
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward annullaModifiche(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("annulla: inizio metodo");
    String target = "annullaModificaDettaglio";

    DocumentoAssociatoForm docAssForm = (DocumentoAssociatoForm) form;
    if(docAssForm.getId() != null && docAssForm.getId().longValue() > 0)
      request.setAttribute("id", "" + docAssForm.getId().longValue());
    else
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;

    if (logger.isDebugEnabled()) logger.debug("annulla: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Metodo per l'annullamento dell'edit del documento associato e per tornare
   * al dettaglio del documento stesso o alla lista dei documenti associati
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward annulla(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("annulla: inizio metodo");

    UtilityHistory history = UtilityTags.getUtilityHistory(request.getSession());
    ActionForward actForward = null;
    try {
      actForward = history.back(request);
    } catch (Throwable t) {
      actForward = GestoreEccezioni.gestisciEccezioneAction(t, this, request,
          logger, mapping);
    }

    if (logger.isDebugEnabled()) logger.debug("annulla: fine metodo");
    return actForward;
  }

  /**
   * Action per l'apertura in edit del dettaglio di un documento associato a
   * partire dall'id del documento stesso
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("Modifica: inizio metodo");

    String target = caricaDocumentoAssociato(request);

    if(target.equals(CostantiGeneraliStruts.FORWARD_OK))
      target = SUCCESS_MODIFICA;

    // set nel request del parameter per disabilitare la navigazione in fase
    // di editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

    // Se e' abilitato il download dei documenti associati, allora il client
    // in fase di edit dell'occorrenza del documento associato può modificare
    // anche il file relativo. Settanto nel request l'attributo 'uploadfile'
    // nella pagina HTML generata compare anche l'oggetto input di tipo file.
    if(!CostantiDocumentiAssociati.DISABILITA_DOWNLOAD.equals(
        ConfigManager.getValore(
            CostantiDocumentiAssociati.PROP_DOWNLOAD_DOCUMENTI_ASSOCIATI))){
      request.setAttribute("uploadFile", "1");
    }

    if (logger.isDebugEnabled()) logger.debug("Modifica: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Metodo per l'apertura in edit del dettaglio di un documento associato per
   * effettuare l'inserimento di un documento associato nella base dati e nel
   * server
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward nuovo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("Nuovo: inizio metodo");

    String target = SUCCESS_NUOVO;
    String messageKey = null;

    try {
      DocumentoAssociatoForm docAssForm = (DocumentoAssociatoForm) form;
      docAssForm.setCodApp((String) request.getSession().getAttribute(
          CostantiGenerali.MODULO_ATTIVO));
      docAssForm.setDataInserimento(
          UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

      // Cancellazione dalla sessione degli oggetti idOggetto, nomeOggetto
      HttpSession sessione = request.getSession();
      sessione.removeAttribute(CostantiGenerali.ID_OGGETTO_SESSION);
      sessione.removeAttribute(CostantiGenerali.NOME_OGGETTO_SESSION);

      // Set nel request del form
      request.setAttribute("documento", docAssForm);
      // Set dell'attributo uploadFile che permettera' di effettuare l'upload
      // del file relativo al documento associato
      request.setAttribute("uploadFile", "1");

      // Salvo nel request la lista dei tipi di modelli
      request.setAttribute("listaTipoDocumento",
          this.tabellatiManager.getTabellato(TabellatiManager.TIPO_MODELLI));

      // set nel request del parameter per disabilitare la navigazione
      // in fase di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) logger.debug("Nuovo: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Metodo per l'insert su DB di un documento associato e upload del relativo file
   * o per l'update di un documento associato esistente
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward salva(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("Nuovo: inizio metodo");

    String target = SUCCESS_SALVA;
    String messageKey = null;

    DocumentoAssociatoForm docAssForm = (DocumentoAssociatoForm) form;

    // Memorizzo il nome del file del documento associato, prima dell'update,
    // per poter ripristinare tale attrbituo nel caso di modifica di un documento
    // associato con upload di un file di dimensione nulla (0 byte)
    String backupNomeDocAss = docAssForm.getNomeDocAss();

    if(docAssForm.getId() != null){
      try {
        // Si effettua l'update del record e del relativo file solo se e'
        // abilitato il download dei documenti associati, allora il client
        // in fase di edit dell'occorrenza del documento associato può modificare
        // anche il file relativo.
        if(!CostantiDocumentiAssociati.DISABILITA_DOWNLOAD.equals(
            ConfigManager.getValore(
                CostantiDocumentiAssociati.PROP_DOWNLOAD_DOCUMENTI_ASSOCIATI))){
          if(docAssForm.getSelezioneFile() != null &&
             docAssForm.getSelezioneFile().getFileName().length() > 0 &&
             docAssForm.getSelezioneFile().getFileSize() > 0){
            docAssForm.setNomeDocAss(docAssForm.getSelezioneFile().getFileName());
          }
        }

        if (docAssForm.getSelezioneFile() != null && docAssForm.getSelezioneFile().getFileName().length() > 0) {
          if (!FileAllegatoManager.isEstensioneFileAmmessa(docAssForm.getSelezioneFile().getFileName())) {
            GestioneFileDocumentiAssociatiException gfda = new GestioneFileDocumentiAssociatiException(GestioneFileDocumentiAssociatiException.ERROR_ESTENSIONE_NON_AMMESSA);
            gfda.setParametri(new Object[] { docAssForm.getSelezioneFile().getFileName() });
            throw gfda;
          }
        }

        this.documentiAssociatiManager.updateDocAss(
            docAssForm.getDatiPerModel(),
           (docAssForm.getSelezioneFile() != null &&
            docAssForm.getSelezioneFile().getFileName().length() > 0) ?
            docAssForm.getSelezioneFile().getFileData() : null);

        // Set nel request dell'id del documento di cui bisogna mostrare il dettaglio
        request.setAttribute("id", "" + docAssForm.getId().longValue());
      } catch (GestioneFileDocumentiAssociatiException gfda){
        if(GestioneFileDocumentiAssociatiException.ERROR_UPLOAD_DB_KO.equals(gfda.getCodiceErrore())){
          // Eccezione durante l'accesso alla base dati
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.database.dataAccessException";
          logger.error(this.resBundleGenerale.getString(messageKey), gfda);
          this.aggiungiMessaggio(request, messageKey);
        } else if(GestioneFileDocumentiAssociatiException.ERROR_INASPETTATO_UPLOAD.equals(gfda.getCodiceErrore())){
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.applicazione.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), gfda);
          this.aggiungiMessaggio(request, messageKey);
        } else if(GestioneFileDocumentiAssociatiException.ERROR_UPLOAD_FILE_VUOTO.equals(gfda.getCodiceErrore())){
          if(docAssForm.getId() != null){
            docAssForm.setNomeDocAss(backupNomeDocAss);
            request.setAttribute("documento", docAssForm);
            request.setAttribute("uploadFile", "1");
            target = ERROR_MODIFICA;
          } else {
            // qui non entrera' mai, visto che in testa c'è la condizione if(docAssForm.getId() != null)
            target = ERROR_INSERT;
          }
          messageKey = gfda.getChiaveResourceBundle();
          logger.error(this.resBundleGenerale.getString(messageKey), gfda);
          this.aggiungiMessaggio(request, messageKey);
        } else {
          target = ERROR_MODIFICA;
          String logMessage = this.resBundleGenerale.getString(gfda.getChiaveResourceBundle());
          for(int i=0; i < gfda.getParametri().length; i++)
            logMessage = logMessage.replaceAll("\\{" + i + "\\}", gfda.getParametri()[i].toString());
          logger.error(logMessage, gfda);

          messageKey = "errors.documentiAssociati.update";
          this.aggiungiMessaggio(request, messageKey, logMessage);
        }
      } catch (Throwable t){
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.applicazione.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), t);
        this.aggiungiMessaggio(request, messageKey);

      } finally {
        if(ERROR_MODIFICA.equals(target))
          request.setAttribute("id", "" + docAssForm.getId());
      }
    } else {
      // Si effettua l'insert del record e del relativo file (se possibile)
      // Set del path dei documenti associati
      String pathDocAss = CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT;
      try {
        docAssForm.setPathDocAss(CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT);
        docAssForm.setNomeDocAss(docAssForm.getSelezioneFile().getFileName());
        if(docAssForm.getCodApp() == null)
          docAssForm.setCodApp((String) request.getSession().getAttribute(
              CostantiGenerali.MODULO_ATTIVO));

        if (docAssForm.getSelezioneFile() != null && docAssForm.getSelezioneFile().getFileName().length() > 0) {
          if (!FileAllegatoManager.isEstensioneFileAmmessa(docAssForm.getSelezioneFile().getFileName())) {
            GestioneFileDocumentiAssociatiException gfda = new GestioneFileDocumentiAssociatiException(GestioneFileDocumentiAssociatiException.ERROR_ESTENSIONE_NON_AMMESSA);
            gfda.setParametri(new Object[] { docAssForm.getSelezioneFile().getFileName() });
            throw gfda;
          }
        }

        long newIdDoc = -1;

        newIdDoc = this.insertDocAss(docAssForm.getDatiPerModel(),
            docAssForm.getSelezioneFile().getFileData(), pathDocAss);

        // Set nel request dell'id del documento di cui bisogna mostrare il dettaglio
        request.setAttribute("id", "" + newIdDoc);
      } catch (GestioneFileDocumentiAssociatiException gfda){
        if(GestioneFileDocumentiAssociatiException.ERROR_UPLOAD_DB_KO.equals(gfda.getCodiceErrore())){
          // Eccezione durante l'accesso alla base dati
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.database.dataAccessException";
          logger.error(this.resBundleGenerale.getString(messageKey), gfda);
          this.aggiungiMessaggio(request, messageKey);
        } else if(GestioneFileDocumentiAssociatiException.ERROR_INASPETTATO_UPLOAD.equals(gfda.getCodiceErrore())){
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.applicazione.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), gfda);
          this.aggiungiMessaggio(request, messageKey);
        } else if(GestioneFileDocumentiAssociatiException.ERROR_UPLOAD_FILE_VUOTO.equals(gfda.getCodiceErrore())){
          messageKey = gfda.getChiaveResourceBundle();
          logger.error(this.resBundleGenerale.getString(messageKey), gfda);
          this.aggiungiMessaggio(request, messageKey);

          docAssForm.setId(null);
          docAssForm.setNomeDocAss(null);
          docAssForm.setPathDocAss(null);
          docAssForm.setDataInserimento(UtilityDate.getDataOdiernaAsString(
            UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
          docAssForm.setSelezioneFile(null);
          request.setAttribute("documento", docAssForm);

          // Set dell'attributo uploadFile che permettera' di effettuare
          // l'upload del file relativo al documento associato
          request.setAttribute("uploadFile", "1");

          target = ERROR_INSERT;
        } else {
          target = ERROR_INSERT;
          String logMessage = this.resBundleGenerale.getString(gfda.getChiaveResourceBundle());
          for(int i=0; i < gfda.getParametri().length; i++)
            logMessage = logMessage.replaceAll("\\{" + i + "\\}", gfda.getParametri()[i].toString());
          logger.error(logMessage, gfda);

          messageKey = "errors.documentiAssociati.update";
          this.aggiungiMessaggio(request, messageKey, logMessage);
        }
      } catch (DataAccessException e) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        if (e instanceof DataIntegrityViolationException)
          messageKey = "errors.database.inserimento.chiaveDuplicata";
        else
          messageKey = "errors.database.dataAccessException";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      } catch (Throwable t) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.applicazione.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), t);
        this.aggiungiMessaggio(request, messageKey);
      }
    }
    return mapping.findForward(target);
  }

  /**
   * Metodo per effettuare l'insert di un documento associato, gestendo il caso
   * di inserimento contemporaneo di più documenti: in caso di eccezione di tipo
   * DataIntegrityViolationException si ripete l'operazione di insert, fino ad
   * un numero massimo di tentativi. Raggiunto il numero massimo di tentativi
   * il metodo esce con una eccezione
   */
  private long insertDocAss(DocumentoAssociato docAss, byte[] fileData, String pathDocAss)
        throws GestioneFileDocumentiAssociatiException {
    long idDocAss = -1;

    boolean inserito = false;
    int numeroTentativi = 0;

    // tento di inserire il record finchè non genero un ID univoco a causa
    // della concorrenza, o raggiungo il massimo numero di tentativi
    while(!inserito &&
           numeroTentativi < CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
      try {
        idDocAss = this.documentiAssociatiManager.insertDocAss(docAss, fileData,
            pathDocAss);
        inserito = true;
      } catch (DataIntegrityViolationException div) {
        if (numeroTentativi < CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
          logger.error(
            "Fallito tentativo " + (numeroTentativi + 1) +
            " di inserimento record per chiave duplicata, si ritenta nuovamente",
            div);
          numeroTentativi++;
        }
      }
    }
    if(!inserito &&
        numeroTentativi >= CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
      throw new DataIntegrityViolationException("Raggiunto limite massimo di tentativi");
    }
    return idDocAss;
  }

  /**
   * Metodo privato per caricare da DB il documento associato a partire dall'ID
   * del documento stesso ed inserirlo nel request per la pagina jsp successiva
   *
   * @param request
   * @return Ritorna il target dell'action a seconda che l'operazione sia andata
   *         a buon fine o meno
   */
  private String caricaDocumentoAssociato(HttpServletRequest request) {
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;
    long id = -1;
    String idDocumento = request.getParameter("id");
    if(idDocumento == null)
      idDocumento = (String) request.getAttribute("id");
    try {
      if(idDocumento != null){
        id = Long.parseLong(idDocumento);

        // Salvo nel request la lista dei tipi di modelli
        request.setAttribute("listaTipoDocumento",
            this.tabellatiManager.getTabellato(TabellatiManager.TIPO_MODELLI));

      // Estrazione da DB del dettaglio del documento associato da visualizzare
      DocumentoAssociato documento =
        this.documentiAssociatiManager.getDocumentoAssociatobyId(id);

      if(CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT.equals(documento.getPathDocAss())){
        documento.setPathDocAss(ConfigManager.getValore(
            CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI_CLIENT +
            CostantiGenerali.SEPARATORE_PROPERTIES +
            ConfigManager.getValore(CostantiGenerali.PROP_ID_APPLICAZIONE) +
            CostantiGenerali.SEPARATORE_PROPERTIES +
            (String) request.getSession().getAttribute(
                CostantiGenerali.MODULO_ATTIVO)));
        documento.setDocumentoInAreaShared(Boolean.TRUE);
      } else {
        if(documento.getPathDocAss().indexOf(":") > 0)
          documento.setPathDocAss(StringUtils.replace(documento.getPathDocAss(), "\\", "/"));
        else
          documento.setPathDocAss(documento.getPathDocAss().substring(0,4).concat(
              StringUtils.replace(documento.getPathDocAss().substring(4),
                  "\\", "/")));
        documento.setDocumentoInAreaShared(Boolean.FALSE);
      }

      // Set nel request del documento associato estratto
      request.setAttribute("documento", new DocumentoAssociatoForm(documento));
      } else {
        throw new Throwable("Impossibile reperire nel request l'id del documento da visualizzare.");
      }
    } catch (DataAccessException e){
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
    return target;
  }

}