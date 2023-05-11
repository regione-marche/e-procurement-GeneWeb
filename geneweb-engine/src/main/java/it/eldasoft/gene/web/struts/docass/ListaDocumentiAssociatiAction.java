/*
 * Created on 19-lug-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.docass;

import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.bl.docass.DocumentiAssociatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.docass.DocumentoAssociato;
import it.eldasoft.gene.tags.history.UtilityHistory;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.GestoreEccezioni;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Dispatch Action per l'apertura della lista dei documenti associati e gestione
 * delle relative operazioni effettuabili dalla lista stessa
 * 
 * @author Luca.Giacomazzo
 */
public class ListaDocumentiAssociatiAction extends DispatchActionBaseNoOpzioni {

  private static final String       SUCCESS_DELETE = "successDelete";

  private static final String       FORWARD_LISTA_DOCUMENTI_ASSOCIATI_DB   = "listaDocumentiAssociatiDB";

  /** Logger Log4J di classe */
  static Logger                     logger         = Logger.getLogger(ListaDocumentiAssociatiAction.class);

  /**
   * Reference al manager per la gestione delle operazioni di download e upload
   * di documenti associati
   */
  private DocumentiAssociatiManager documentiAssociatiManager;

  /**
   * @param documentiAssociatiManager
   *        documentiAssociatiManager da settare internamente alla classe.
   */
  public void setDocumentiAssociatiManager(DocumentiAssociatiManager documentiAssociatiManager) {
    this.documentiAssociatiManager = documentiAssociatiManager;
  }

  public ActionForward visualizza(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("VisualizzaLista: inizio metodo");

    String target = null;

    if ("1".equals(ConfigManager.getValore(CostantiDocumentiAssociati.PROP_DOCUMENTI_DB))) {
      target = FORWARD_LISTA_DOCUMENTI_ASSOCIATI_DB;
    } else {
      target = CostantiGeneraliStruts.FORWARD_OK;
      String messageKey = null;

      String key = request.getParameter("key");
      String keyParent = request.getParameter("keyParent");

      // Se non trovo i due parametri come parameter, li cerco come attributi
      if (key == null && keyParent == null) {
        key = (String) request.getAttribute("key");
        keyParent = (String) request.getAttribute("keyParent");
      }

      String entita = request.getParameter("entita");
      String valori = request.getParameter("valori");

      // Se non trovo i due parametri come parameter, li cerco come attributi
      if (entita == null && valori == null) {
        entita = (String) request.getAttribute("entita");
        valori = (String) request.getAttribute("valori");
      }

      DocumentoAssociatoForm docAssForm = new DocumentoAssociatoForm();
      docAssForm.setEntita(entita);
      docAssForm.setValoriCampiChiave(ListaDocumentiAssociatiAction.getValoriCampiChiave(valori));
      docAssForm.setKey(key);
      docAssForm.setKeyParent(keyParent);

      try {
        List<?> listaDoumentiAssociati = this.documentiAssociatiManager.getListaDocumentiAssociati(docAssForm.getDatiPerModel());

        // Se e' abilitato il download dei documenti associati, allora il client
        // può solo effettuare il download in locale di una copia del file,
        // altrimenti
        // può aprire su una nuova finestra il file ed eventualmente
        // modificarlo.
        // Nel caso in cui sia abilitato il download dei documenti associati,
        // allora
        // inserisco nel request un attributo che permette di scegliere al
        // momento
        // della compilazione della pagina jsp quale chiamata JS effettuare
        if (!CostantiDocumentiAssociati.DISABILITA_DOWNLOAD.equals(ConfigManager.getValore(CostantiDocumentiAssociati.PROP_DOWNLOAD_DOCUMENTI_ASSOCIATI))) {
          request.setAttribute("download", "1");
        }

        Iterator<?> iter = listaDoumentiAssociati.iterator();
        DocumentoAssociato tmpDocAss = null;
        String tmpPath = null;
        while (iter.hasNext()) {
          tmpDocAss = (DocumentoAssociato) iter.next();
          if (CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT.equals(tmpDocAss.getPathDocAss())) {
            tmpPath = ConfigManager.getValore(CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI_CLIENT
                + CostantiGenerali.SEPARATORE_PROPERTIES
                + ConfigManager.getValore(CostantiGenerali.PROP_ID_APPLICAZIONE)
                + CostantiGenerali.SEPARATORE_PROPERTIES
                + (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
            tmpDocAss.setDocumentoInAreaShared(Boolean.TRUE);
          } else {
            if (tmpDocAss.getPathDocAss().indexOf(":") > 0)
              tmpPath = StringUtils.replace(tmpDocAss.getPathDocAss(), "\\", "/");
            else
              tmpPath = tmpDocAss.getPathDocAss().substring(0, 4).concat(
                  StringUtils.replace(tmpDocAss.getPathDocAss().substring(4), "\\", "/"));
            tmpDocAss.setDocumentoInAreaShared(Boolean.FALSE);
          }
          tmpDocAss.setPathDocAss(tmpPath);
        }

        // Set nel request della lista dei dati estratti e il form
        request.setAttribute("listaDocAss", listaDoumentiAssociati);
        request.setAttribute("docAssForm", docAssForm);

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
    }

    if (logger.isDebugEnabled()) logger.debug("VisualizzaLista: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Metodo per la cancellazione di uno piu' documenti associati presenti nella
   * lista
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward eliminaMultiplo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    /*
     * Questa operazione consiste nella cancellazione di un singolo record da DB
     * e del relativo file, in modo transazionale. Qualora si verifichi
     * un'eccezione durante la cancellazione dell'i-esimo documento associato,
     * l'operazione viene bloccata segnalando all'utente il file che ha creato
     * problemi e il numero di documenti associati cancellati rispetto al numero
     * di documenti associati da cancellare
     */

    if (logger.isDebugEnabled()) logger.debug("eliminaMultiplo: inizio metodo");

    String moduloAttivo = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    String target = SUCCESS_DELETE;
    String messageKey = null;
    DocumentoAssociato documento = null;
    int lunghezzaElenco = -1;
    String arrayIdDocAss[] = null;
    ActionForward actForward = null;

    int i = -1;
    try {
      ListaDocumentiAssociatiForm documenti = (ListaDocumentiAssociatiForm) form;

      // costruzione dell'elenco degli id dei documenti da rimuovere
      arrayIdDocAss = documenti.getId();
      lunghezzaElenco = arrayIdDocAss.length;

      for (i = 0; i < arrayIdDocAss.length; i++) {
        // Estraggo i dati dell'i-esimo documento associato da elimiare
        // per recuperare il path e il nome del file
        documento = this.documentiAssociatiManager.getDocumentoAssociatobyId(Long.parseLong(arrayIdDocAss[i]));
        try {
          this.documentiAssociatiManager.deleteDocumento(Long.parseLong(arrayIdDocAss[i]), documento.getPathDocAss(),
              documento.getNomeDocAss(), moduloAttivo,
              (documenti.getCancellazioneFile() != null && documenti.getCancellazioneFile().equals("1")));
        } catch (GestioneFileDocumentiAssociatiException gfda) {
          if (GestioneFileDocumentiAssociatiException.CODICE_ERRORE_KO_CANCELLA_FILE_PIU_OCCORRENZE.equals(gfda.getCodiceErrore())) {
            // Il file non e' stato cancellato perche' e' associato a piu'
            // occorrenze
            // della tabella C0OGGASS
            logger.warn(this.resBundleGenerale.getString(gfda.getChiaveResourceBundle()), gfda);
            messageKey = "warnings.documentiAssociati.delete.filePiuOccorrenze";
            this.aggiungiMessaggio(request, messageKey, (String) gfda.getParametri()[0]);
          } else {
            throw gfda;
          }
        }
      }
    } catch (FileManagerException fm) {
      if (FileManagerException.CODICE_ERRORE_FILE_ESISTENTE_NO_ACCESSIBILE.equals(fm.getCodiceErrore())) {
        // Il file e' stato cancellato perche' esiste ma non si hanno i diritti
        // di scrittura su tale file, oppure il file e' aperto da un client
        String logMessage = this.resBundleGenerale.getString(fm.getChiaveResourceBundle());
        logger.error(logMessage.replaceAll("\\{0\\}", (String) fm.getParametri()[0]), fm);

        messageKey = "errors.documentiAssociati.deleteLista";
        String tmp = this.resBundleGenerale.getString("warnings.documentiAssociati.delete.filePiuOccorrenze");
        tmp = UtilityStringhe.replaceParametriMessageBundle(tmp, new String[] { (String) fm.getParametri()[0] });
        this.aggiungiMessaggio(request, messageKey, "" + i, "" + lunghezzaElenco, tmp);
      } else if (FileManagerException.CODICE_ERRORE_FILE_INESISTENTE.equals(fm.getCodiceErrore())) {
        // Il file e' stato cancellato perche' non esiste oppure non e'
        // raggiungibile dal server
        String logMessage = this.resBundleGenerale.getString(fm.getChiaveResourceBundle());
        logger.error(logMessage.replaceAll("\\{0\\}", (String) fm.getParametri()[0]), fm);

        messageKey = "errors.documentiAssociati.deleteLista";
        String tmp = this.resBundleGenerale.getString("errors.documentiAssociati.delete.fileInesistente");
        tmp = UtilityStringhe.replaceParametriMessageBundle(tmp, new String[] { (String) fm.getParametri()[0] });
        this.aggiungiMessaggio(request, messageKey, "" + i, "" + lunghezzaElenco, tmp);
      } else if (FileManagerException.CODICE_ERRORE_CANCELLAZIONE_FILE_ARG_NULL.equals(fm.getCodiceErrore())) {
        // Il file e' stato cancellato perche' path e/o nome file sono nulli
        String logMessage = this.resBundleGenerale.getString(fm.getChiaveResourceBundle());
        logger.error(logMessage.replaceAll("\\{0\\}", (String) fm.getParametri()[0]), fm);

        String tmp = this.resBundleGenerale.getString("errors.documentiAssociati.delete.causaNonPrevista");
        tmp = UtilityStringhe.replaceParametriMessageBundle(tmp, new String[] { (String) fm.getParametri()[0] });
        this.aggiungiMessaggio(request, messageKey, "" + i, "" + lunghezzaElenco, tmp);

        this.aggiungiMessaggio(request, messageKey);
      } else if (FileManagerException.CODICE_ERRORE_CANCELLAZIONE_FILE.equals(fm.getCodiceErrore())) {
        // Il file e' stato cancellato per una causa non prevista
        String logMessage = this.resBundleGenerale.getString(fm.getChiaveResourceBundle());
        logger.error(logMessage.replaceAll("\\{0\\}", (String) fm.getParametri()[0]), fm);

        String tmp = this.resBundleGenerale.getString("errors.documentiAssociati.delete.causaNonPrevista");
        tmp = UtilityStringhe.replaceParametriMessageBundle(tmp, new String[] { (String) fm.getParametri()[0] });
        this.aggiungiMessaggio(request, messageKey, "" + i, "" + lunghezzaElenco, tmp);
      }
    } catch (SqlComposerException sc) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = sc.getChiaveResourceBundle();
      logger.error(this.resBundleGenerale.getString(messageKey), sc);
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
    } finally {
      if (!CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE.equals(target)) {
        // Set nel request degli attributi necessari al ricaricamento della
        // lista,
        // usando i valori dei campi chiave dell'ultima occorrenza del documento
        // associato appena cancellato
        /*
         * request.setAttribute("entita", documento.getEntita());
         * request.setAttribute("valori", "campo1:" +
         * documento.getCampoChiave1() + ";" + "campo2:" +
         * documento.getCampoChiave2() + ";" + "campo3:" +
         * documento.getCampoChiave3() + ";" + "campo4:" +
         * documento.getCampoChiave4() + ";" + "campo5:" +
         * documento.getCampoChiave5());
         */
        UtilityHistory history = UtilityTags.getUtilityHistory(request.getSession());
        try {
          actForward = history.last(request);
        } catch (Throwable t) {
          actForward = GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger, mapping);
        }
      }
    }

    if (logger.isDebugEnabled()) logger.debug("eliminaMultiplo: fine metodo");
    if (actForward != null)
      return actForward;
    else
      return mapping.findForward(target);
  }

  /**
   * Metodo per la cancellazione di una singola occorrenza della lista dei
   * documenti associati a partire dal menu popup
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward elimina(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("elimina: inizio metodo");

    String moduloAttivo = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    String target = SUCCESS_DELETE;
    String messageKey = null;
    DocumentoAssociato documento = null;
    boolean cancellaFile = false;
    ActionForward actForward = null;

    try {
      String idDocumento = request.getParameter("id");
      if (request.getParameter("delete") != null) cancellaFile = true;

      // Caricamento del record da cancellare per acquisire l'entita e i valori
      // dei campi chiave del record stesso utili per ricaricare la lista
      documento = this.documentiAssociatiManager.getDocumentoAssociatobyId(Long.parseLong(idDocumento));

      // Gestione del path del documento associato: se su DB il path del
      // documento
      // associato e' pari a CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT,
      // il path viene convertito con il path specificato nelle properties.
      String pathDocAss = documento.getPathDocAss();
      if (CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT.equals(pathDocAss))
        documento.setDocumentoInAreaShared(new Boolean(true));
      else
        documento.setDocumentoInAreaShared(new Boolean(false));

      // Delete del record dalla tabella c0oggass e del file se il parametro
      // cancellaFile e' settato a true
      this.documentiAssociatiManager.deleteDocumento(Long.parseLong(idDocumento), pathDocAss, documento.getNomeDocAss(), moduloAttivo,
          cancellaFile);
    } catch (GestioneFileDocumentiAssociatiException gfda) {
      if (GestioneFileDocumentiAssociatiException.CODICE_ERRORE_KO_CANCELLA_FILE_PIU_OCCORRENZE.equals(gfda.getCodiceErrore())) {
        // Il file non e' stato cancellato perche' e' associato a piu'
        // occorrenze
        // della tabella C0OGGASS
        String logMessage = this.resBundleGenerale.getString(gfda.getChiaveResourceBundle());
        logger.warn(logMessage.replaceAll("\\{0\\}", (String) gfda.getParametri()[0]), gfda);

        messageKey = "warnings.documentiAssociati.delete.filePiuOccorrenze";
        this.aggiungiMessaggio(request, messageKey, (String) gfda.getParametri()[0]);
      }
    } catch (FileManagerException fm) {
      if (FileManagerException.CODICE_ERRORE_FILE_ESISTENTE_NO_ACCESSIBILE.equals(fm.getCodiceErrore())) {
        // Il file e' stato cancellato perche' esiste ma non si hanno i diritti
        // di scrittura su tale file, oppure il file e' aperto da un client
        String logMessage = this.resBundleGenerale.getString(fm.getChiaveResourceBundle());
        logger.warn(logMessage.replaceAll("\\{0\\}", (String) fm.getParametri()[0]), fm);

        messageKey = "errors.documentiAssociati.delete.fileNonAccessibile";
        this.aggiungiMessaggio(request, messageKey, (String) fm.getParametri()[0]);
      } else if (FileManagerException.CODICE_ERRORE_FILE_INESISTENTE.equals(fm.getCodiceErrore())) {
        // Il file e' stato cancellato perche' non esiste oppure non e'
        // raggiungibile dal server
        String logMessage = this.resBundleGenerale.getString(fm.getChiaveResourceBundle());
        logger.warn(logMessage.replaceAll("\\{0\\}", (String) fm.getParametri()[0]), fm);

        messageKey = "errors.documentiAssociati.delete.fileInesistente";
        this.aggiungiMessaggio(request, messageKey, (String) fm.getParametri()[0]);
      } else if (FileManagerException.CODICE_ERRORE_CANCELLAZIONE_FILE_ARG_NULL.equals(fm.getCodiceErrore())) {
        // Il file e' stato cancellato perche' path e/o nome file sono nulli
        String logMessage = this.resBundleGenerale.getString(fm.getChiaveResourceBundle());
        logger.warn(logMessage.replaceAll("\\{0\\}", (String) fm.getParametri()[0]), fm);

        messageKey = "errors.documentiAssociati.delete.argomentiNulli";
        this.aggiungiMessaggio(request, messageKey, (String) fm.getParametri()[0]);
      } else if (FileManagerException.CODICE_ERRORE_CANCELLAZIONE_FILE.equals(fm.getCodiceErrore())) {
        // Il file e' stato cancellato per una causa non prevista
        String logMessage = this.resBundleGenerale.getString(fm.getChiaveResourceBundle());
        logger.warn(logMessage.replaceAll("\\{0\\}", (String) fm.getParametri()[0]), fm);

        messageKey = "errors.documentiAssociati.delete.causaNonPrevista";
        this.aggiungiMessaggio(request, messageKey);
      }
    } catch (SqlComposerException sc) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = sc.getChiaveResourceBundle();
      logger.error(this.resBundleGenerale.getString(messageKey), sc);
      this.aggiungiMessaggio(request, messageKey);
    } catch (DataAccessException da) {

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
      if (SUCCESS_DELETE.equals(target)) {
        // Set nel request degli attributi necessari al ricaricamento della
        // lista
        request.setAttribute("entita", documento.getEntita());
        request.setAttribute("valori", "campo1:"
            + documento.getCampoChiave1()
            + ";"
            + "campo2:"
            + documento.getCampoChiave2()
            + ";"
            + "campo3:"
            + documento.getCampoChiave3()
            + ";"
            + "campo4:"
            + documento.getCampoChiave4()
            + ";"
            + "campo5:"
            + documento.getCampoChiave5());

        // Set nel request degli attributi necessari al ricaricamento della
        // lista,
        // usando i valori dei campi chiave dell'ultima occorrenza del documento
        // associato appena cancellato
        /*
         * request.setAttribute("entita", documento.getEntita());
         * request.setAttribute("valori", "campo1:" +
         * documento.getCampoChiave1() + ";" + "campo2:" +
         * documento.getCampoChiave2() + ";" + "campo3:" +
         * documento.getCampoChiave3() + ";" + "campo4:" +
         * documento.getCampoChiave4() + ";" + "campo5:" +
         * documento.getCampoChiave5());
         */
        UtilityHistory history = UtilityTags.getUtilityHistory(request.getSession());
        try {
          actForward = history.last(request);
        } catch (Throwable t) {
          actForward = GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger, mapping);
        }

      }
    }

    if (logger.isDebugEnabled()) logger.debug("elimina: fine metodo");
    if (actForward != null)
      return actForward;
    else
      return mapping.findForward(target);
  }

  /**
   * @param valori
   *        stringa che arriva direttamente dal browser del cliente così
   *        costituita
   *        PERI.CODLAV=T:1001_04;PERI.ASDASD=T:31231;PERI.REDRERE=N:31; con al
   *        piu' 5 campi separati da ';'
   * @return Ritorna un array di lunghezza 5 contenente i valori della chiave
   *         primaria dell'entita necessari per individuare i documenti
   *         associati della entita stessa. Se la stringa in ingresso contiene
   *         meno di 5 campi, l'array viene completato dal valore '#'
   */
  protected static String[] getValoriCampiChiave(String valori) {
    String[] valoriPK = new String[] { "#", "#", "#", "#", "#" };
    String[] tmp = valori.split(";");
    for (int i = 0; i < tmp.length; i++)
      valoriPK[i] = tmp[i].split(":")[1];

    return valoriPK;
  }

}