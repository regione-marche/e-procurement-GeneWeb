/*
 * Created on 23-ago-2007
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
import it.eldasoft.gene.bl.genmod.CompositoreException;
import it.eldasoft.gene.bl.genmod.GestioneFileModelloException;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.db.domain.genmod.ContenitoreDatiImport;
import it.eldasoft.gene.db.domain.genmod.ContenitoreDatiModello;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.GruppoModello;
import it.eldasoft.gene.db.domain.genmod.TrovaModelli;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.DatoBase64;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

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
 * Action per l'operazione di insert/update della definizione di un modello
 * contenuta nel file xml specificato nella prima pagina del wizard di import
 * 
 * @author Francesco De Filippis
 */
public class EseguiImportModelloAction extends ActionBaseNoOpzioni {

  static Logger logger = Logger.getLogger(EseguiImportModelloAction.class);

  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }

  /**
   * Reference al manager per l'accesso alla tabella W_GRUPPI e W_GRPMOD
   */
  private GruppiManager gruppiManager;

  /**
   * @param gruppiManager
   *        gruppiManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }

  private ModelliManager modelliManager;

  /**
   * @param modelliManager
   *        The modelliManager to set.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String target = CostantiGeneraliStruts.FORWARD_OK;

    String pageFrom = request.getParameter("pageFrom");
    if (pageFrom == null || pageFrom.length() == 0)
      pageFrom = (String) request.getAttribute("pageFrom");

    ContenitoreDatiImport contenitore = (ContenitoreDatiImport) request.getSession().getAttribute(
        CostantiGenModelli.OGGETTO_DETTAGLIO);

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    String tmpTarget = null;

    try {
      if (contenitore.getContenitoreDatiGenerali() != null) {
        tmpTarget = this.eseguiImportModello(request, contenitore,
            profiloUtente.getId(), (String) request.getSession().getAttribute(
                CostantiGenerali.MODULO_ATTIVO));
      }
    } catch (Throwable t) {
      tmpTarget = CostantiWizard.ERROR_IMPORT_MODELLI;
      String messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }

    // In caso di errore ritorna alla pagina da cui e' stato richiamato il
    // salvataggio dei dati
    if (tmpTarget != null) {
      request.setAttribute("pageTo", pageFrom);
      target = tmpTarget;
    }

    // set del messaggio che l'importazione è avvenuta con successo in caso
    // positivo
    if (CostantiGeneraliStruts.FORWARD_OK.equals((target))) {
      this.aggiungiMessaggio(request, "info.genMod.import.success");

      // Cancellazione dalla sessione del contenitore dati import qualsiasi sia
      // l'esito dell'operazione di insert del modello da importare
      request.getSession().removeAttribute(CostantiGenModelli.OGGETTO_DETTAGLIO);
    }

    // set nel request del parameter per disabilitare la navigazione
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

    return mapping.findForward(target);
  }

  /**
   * Metodo che esegue l'insert/update su DB di un modello
   * 
   * @param request
   * @param contenitoreDatiImport
   * @param idUtenteImportatore
   * @param codApp
   * @return Ritorna null se l'operazione di insert va a buon fine, altrimenti
   *         ritorna il target di destinazione
   */
  private String eseguiImportModello(HttpServletRequest request,
      ContenitoreDatiImport contenitoreDatiImport, int idUtenteImportatore,
      String codApp) {

    String target = null;
    Integer result = null;

    boolean pubblicaModello = contenitoreDatiImport.isPubblicaNuovoModello();
    boolean esisteModello = contenitoreDatiImport.isEsisteModello();
    String tipoImport = contenitoreDatiImport.getTipoImport();
    String nuovoTitoloModello = contenitoreDatiImport.getNuovoTitoloModello();
    GruppiModelliImportForm gruppiModelliForm = contenitoreDatiImport.getGruppiModelliForm();
    ContenitoreDatiModello contenitoreDatiModello = contenitoreDatiImport.getContenitoreDatiGenerali();
    
    // A prescindere dalla pubblicazione o meno del modello in importazione
    // cancello l'oggetto contenente l'associazione modello-gruppi, perchè va
    // comunque ridefinita
    contenitoreDatiModello.getElencoGruppi().removeAllElements();
    
    DatiModello datiModello = contenitoreDatiModello.getDatiGenModello();
    datiModello.setProfiloOwner((String) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO));

    try {
      if (pubblicaModello) {
        datiModello.setDisponibile(1);
        datiModello.setPersonale(0);

        // Nel caso la gestione dei gruppi sia disabilitata e visto che l'utente
        // ha scelto di pubblicare, allora inserisco nell'elenco dei gruppi il
        // gruppo di default
        if (this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
          ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
              CostantiGenerali.PROFILO_UTENTE_SESSIONE);
          int idGruppo = -1;

          if (profiloUtente.getIdGruppi() != null
              && profiloUtente.getIdGruppi().length > 0) {
            idGruppo = profiloUtente.getIdGruppi()[0].intValue();

            Gruppo gruppoDefault = this.gruppiManager.getGruppoById(idGruppo);
            contenitoreDatiModello.aggiungiGruppo(new GruppoModello(
                gruppoDefault));
          } else {
            // se per caso tale id di default non risulta valorizzato quando
            // richiesto, allora si termina con un errore generale
            target = CostantiWizard.ERROR_IMPORT_MODELLI;
            String messageKey = "errors.applicazione.idGruppoDefaultNull";
            logger.error(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
          }
        } else {
          // In questo caso l'utente, avendo scelto di pubblicare, ha potuto
          // definire l'associazione modelli-gruppi

          // lista di tutti i gruppi esistenti ordinata per nome
          List listaGruppi = this.gruppiManager.getGruppiOrderByNome((String) request.getSession().getAttribute(
              CostantiGenerali.PROFILO_ATTIVO));

          if(gruppiModelliForm != null && gruppiModelliForm.getIdGruppo() != null){
            Set insiemeGruppiAssociati = new HashSet();
  
            for (int i = 0; i < gruppiModelliForm.getIdGruppo().length; i++)
              insiemeGruppiAssociati.add((gruppiModelliForm.getIdGruppo()[i]));
  
            // elimino tutti i gruppi esistenti e ricreo le corrispondenze
            contenitoreDatiModello.getElencoGruppi().clear();
            for (int i = 0; i < listaGruppi.size(); i++)
              if (insiemeGruppiAssociati.contains(""
                  + ((Gruppo) listaGruppi.get(i)).getIdGruppo())) {
                contenitoreDatiModello.aggiungiGruppo(new GruppoModello(
                    (Gruppo) listaGruppi.get(i)));
  
              }
          }
        }
      } else {
        // In questo caso bisogna settare nell'oggetto ContenitoreDatiModello
        // costruito a partire dal file XML le informazioni su disponibilità,
        // associazione modello-gruppi del modello esistente su DB
        if (tipoImport.equals(CostantiWizard.IMPORT_SOVRASCRIVI_PARZIALE)) {
          try {
            TrovaModelli trovaModelli = new TrovaModelli();
            trovaModelli.setNomeModello(datiModello.getNomeModello());
            trovaModelli.setCodiceProfiloAttivo(datiModello.getProfiloOwner());

            List listaModelli = this.modelliManager.getModelli(trovaModelli);
            DatiModello modelloDB = (DatiModello) listaModelli.get(0);

            int idModelloEsistente = modelloDB.getIdModello();
            List listaGruppiModello = gruppiManager.getGruppiByIdModello(idModelloEsistente);
            Vector tmpElencoGruppi = new Vector();
            for (int i = 0; i < listaGruppiModello.size(); i++)
              tmpElencoGruppi.addElement(new GruppoModello(
                  (Gruppo) listaGruppiModello.get(i)));

            datiModello.setCodiceApplicativo(modelloDB.getCodiceApplicativo());
            datiModello.setDisponibile(modelloDB.getDisponibile());
            datiModello.setPersonale(modelloDB.getPersonale());
            datiModello.setOwner(modelloDB.getOwner());
            contenitoreDatiModello.setElencoGruppi(tmpElencoGruppi);

          } catch (SqlComposerException sc) {
            target = CostantiWizard.ERROR_IMPORT_MODELLI;
            String messageKey = sc.getChiaveResourceBundle();
            logger.error(this.resBundleGenerale.getString(messageKey), sc);
            this.aggiungiMessaggio(request, messageKey);
          }
        } else {
          // SS 01/10/2008
          // il modello, anche se rimane personale, deve essere disponibile,
          // altrimenti non si riesce a listare se l'utente non è un
          // amministratore
          datiModello.setDisponibile(1);
          datiModello.setPersonale(1);
          contenitoreDatiModello.setElencoGruppi(new Vector());
        }
      }
    } catch (DataAccessException e) {
      target = CostantiWizard.ERROR_IMPORT_MODELLI;
      String messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiWizard.ERROR_IMPORT_MODELLI;
      String messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (target == null) {
      // Il file contenente il modello associato deve essere
      // decodificato dalla base 64 in caratteri ASCII
      DatoBase64 contenutoFileModello = new DatoBase64(
          contenitoreDatiModello.getFileModello(), DatoBase64.FORMATO_BASE64);

      if (esisteModello) {
        if (tipoImport.equals(CostantiWizard.IMPORT_SOVRASCRIVI_ESISTENTE)
            || tipoImport.equals(CostantiWizard.IMPORT_SOVRASCRIVI_PARZIALE)) {
          result = this.insertModelloEsistente(request, contenitoreDatiModello,
              codApp, datiModello.getOwner().intValue(),
              contenutoFileModello.getByteArrayDatoAscii());

        } else if (tipoImport.equals(CostantiWizard.IMPORT_INSERT_CON_NUOVO_TITOLO)) {
          // Cambio del titolo del modello che si va ad importare
          datiModello.setNomeModello(nuovoTitoloModello);
          result = this.insertModello(request, contenitoreDatiModello, codApp,
              idUtenteImportatore, contenutoFileModello.getByteArrayDatoAscii());
        }
      } else {
        result = this.insertModello(request, contenitoreDatiModello, codApp,
            idUtenteImportatore, contenutoFileModello.getByteArrayDatoAscii());
      }
    }

    if (result != null) {
      // L'operazione di import è andata a buon fine, allora carico in sessione
      // il necessario per la visualizzazione del dettaglio e faccio forward
      // verso la action di dettaglio
      // target = CostantiGenModelli.FORWARD_OK_UPDATE_MODELLO;
      // ModelliForm modelliForm = new ModelliForm(datiModello);
      //      
      Integer idModello = new Integer(datiModello.getIdModello());
      //      
      // request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_MODELLO,
      // modelliForm);
      //      
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          idModello);

      // request.removeAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA);
      // // set in sessione del nome del modello di cui si sta facendo il
      // dettaglio
      // request.getSession().setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
      // datiModello.getNomeModello());
    } else {
      target = CostantiWizard.ERROR_IMPORT_MODELLI;
    }
    return target;
  }

  private Integer insertModello(HttpServletRequest request,
      ContenitoreDatiModello contenitoreDatiModello, String codApp,
      int idUtenteOwner, byte[] contenutoFileModello) {
    Integer result = null;
    String messageKey = null;

    DatiModello datiGenerali = contenitoreDatiModello.getDatiGenModello();
    
    ProfiloUtente profiloUtente = (ProfiloUtente)request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    String contesto = null;
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI)))
      contesto = profiloUtente.getUfficioAppartenenza();

    try {
      // Inserimento del nuovo modello
      this.modelliManager.importModello(contenitoreDatiModello, codApp,
          idUtenteOwner, contenutoFileModello, contesto);
      result = new Integer(datiGenerali.getIdModello());
    } catch (DataAccessException da) {
      // L'istruzione modelliManager.importModello emette sempre eccezioni
      // di tipo DataAccessException (o una sua classe figlia). Tuttavia se
      // il messaggio di tale eccezione è null, allora l'eccezione originale è
      // stata wrappata con una DataAccessException (o una sua classe figlia).
      if (da.getCause() != null
          && da.getCause() instanceof CompositoreException) {
        // Gestione dell'eccezione in compilazione
        // target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
        CompositoreException e = (CompositoreException) da.getCause();
        messageKey = e.getChiaveResourceBundle();
        if (e.getParametri() == null) {
          logger.error(this.resBundleGenerale.getString(messageKey), e);
          this.aggiungiMessaggio(request, messageKey);
        } else if (e.getParametri().length == 1) {
          logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              (String) e.getParametri()[0]), e);
          this.aggiungiMessaggio(request, messageKey, e.getParametri()[0]);
        } else {
          logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              (String) e.getParametri()[0]).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(1),
              (String) e.getParametri()[1]), e);
          this.aggiungiMessaggio(request, messageKey, e.getParametri()[0],
              e.getParametri()[1]);
        }
      } else if (da.getCause() != null
          && da.getCause() instanceof RemoteException) {
        // target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
        messageKey = "errors.modelli.compositoreDisattivo";
        logger.error(this.resBundleGenerale.getString(messageKey),
            da.getCause());
        this.aggiungiMessaggio(request, messageKey);

      } else if (da.getCause() != null
          && da.getCause() instanceof GestioneFileModelloException) {
        GestioneFileModelloException e = (GestioneFileModelloException) da.getCause();
        // target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
        messageKey = "errors.modelli.uploaderror";
        // Aggiungo l'eventuale codice in più
        if (!e.getCodiceErrore().equals(""))
          messageKey += "." + e.getCodiceErrore();
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);

      } else if (da.getCause() != null && da.getCause() instanceof IOException) {
        // target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
        IOException e = (IOException) da.getCause();
        messageKey = "errors.modelli.delete";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);

      } else if (da.getMessage() != null) {
        // L'eccezione emessa è effettivamente una DataAccessException o una
        // classe figlia
        if (da instanceof DataIntegrityViolationException) {
          messageKey = "errors.genmod.import.salva.vincoloUnique";
          logger.error(this.resBundleGenerale.getString(messageKey), da);
          this.aggiungiMessaggio(request, messageKey);
        } else if (da instanceof DataAccessException) {
          messageKey = "errors.database.dataAccessException";
          logger.error(this.resBundleGenerale.getString(messageKey), da);
          this.aggiungiMessaggio(request, messageKey);
        }
      }
    } catch (Throwable t) {
      // target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);

    }
    return result;
  }

  private Integer insertModelloEsistente(HttpServletRequest request,
      ContenitoreDatiModello contenitoreDatiModello, String codApp,
      int idUtenteOwner, byte[] contenutoFileModello) {

    Integer result = null;
    String messageKey = null;

    DatiModello datiGenerali = contenitoreDatiModello.getDatiGenModello();

    TrovaModelli trovaModelli = new TrovaModelli();
    trovaModelli.setNomeModello(datiGenerali.getNomeModello());
    trovaModelli.setCodiceProfiloAttivo(datiGenerali.getProfiloOwner());

    ProfiloUtente profiloUtente = (ProfiloUtente)request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    String contesto = null;
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI)))
      contesto = profiloUtente.getUfficioAppartenenza();

    try {
      List listaModelli = this.modelliManager.getModelli(trovaModelli);

      DatiModello datiGenModelloInDb = (DatiModello) listaModelli.get(0);

      datiGenerali.setIdModello(datiGenModelloInDb.getIdModello());
      datiGenerali.setCodiceApplicativo(datiGenModelloInDb.getCodiceApplicativo());
      // WE 190 (Sabbadin 16/03/2010): si ripristina il nome del file del
      // modello originario, in modo da aggiornare lo stesso file
      datiGenerali.setNomeFile(datiGenModelloInDb.getNomeFile());

      contenitoreDatiModello.setDatiGenModello(datiGenerali);
      
      this.modelliManager.importModelloEsistente(
          contenitoreDatiModello, codApp, idUtenteOwner, contenutoFileModello, contesto);
      
      result = new Integer(datiGenerali.getIdModello());

    } catch (DataAccessException da) {
      // L'istruzione modelliManager.importModello emette sempre eccezioni
      // di tipo DataAccessException (o una sua classe figlia). Tuttavia se
      // il messaggio di tale eccezione è null, allora l'eccezione originale è
      // stata wrappata con una DataAccessException (o una sua classe figlia).
      if (da.getMessage() != null) {
        // L'eccezione emessa è effettivamente una DataAccessException o una
        // classe figlia
        if (da instanceof DataIntegrityViolationException) {
          messageKey = "errors.prospetti.salva.vincoloUnique";
          logger.error(this.resBundleGenerale.getString(messageKey), da);
          this.aggiungiMessaggio(request, messageKey);
        } else if (da instanceof DataAccessException) {
          messageKey = "errors.database.dataAccessException";
          logger.error(this.resBundleGenerale.getString(messageKey), da);
          this.aggiungiMessaggio(request, messageKey);
        }
      } else if (da.getCause() != null
          && da.getCause() instanceof RemoteException) {
        // target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
        messageKey = "errors.modelli.compositoreDisattivo";
        logger.error(this.resBundleGenerale.getString(messageKey),
            da.getCause());
        this.aggiungiMessaggio(request, messageKey);

      } else if (da.getCause() != null
          && da.getCause() instanceof GestioneFileModelloException) {
        GestioneFileModelloException e = (GestioneFileModelloException) da.getCause();
        // target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
        messageKey = "errors.modelli.uploaderror";
        // Aggiungo l'eventuale codice in più
        if (!e.getCodiceErrore().equals(""))
          messageKey += "." + e.getCodiceErrore();
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      } else if (da.getCause() != null && da.getCause() instanceof IOException) {
        IOException e = (IOException) da.getCause();
        messageKey = "errors.modelli.delete";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      } else if (da.getCause() != null
          && da.getCause() instanceof CompositoreException) {
        // Gestione dell'eccezione in compilazione
        // target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
        CompositoreException e = (CompositoreException) da.getCause();
        messageKey = e.getChiaveResourceBundle();
        if (e.getParametri() == null) {
          logger.error(this.resBundleGenerale.getString(messageKey), e);
          this.aggiungiMessaggio(request, messageKey);
        } else if (e.getParametri().length == 1) {
          logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              (String) e.getParametri()[0]), e);
          this.aggiungiMessaggio(request, messageKey, e.getParametri()[0]);
        } else {
          logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              (String) e.getParametri()[0]).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(1),
              (String) e.getParametri()[1]), e);
          this.aggiungiMessaggio(request, messageKey, e.getParametri()[0],
              e.getParametri()[1]);
        }
      }
    } catch (Throwable t) {
      // target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    return result;
  }
}