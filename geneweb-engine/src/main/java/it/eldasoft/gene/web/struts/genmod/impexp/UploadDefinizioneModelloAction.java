/*
 * Created on 20-ago-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod.impexp;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.domain.genmod.ContenitoreDatiImport;
import it.eldasoft.gene.db.domain.genmod.ContenitoreDatiModello;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per l'upload del file XML contenente la definizione del modello da
 * importare, creazione dell'oggetto ContenitoreDatiModello e controllare che il
 * modello deve essere creato con il profilo attivo;
 * 
 * Se il controllo ha esito positivo, allora si aggiorna il codApp all'oggetto
 * ContenitoreDatiImport, si carica lo stesso oggetto in sessione, valorizzando
 * l'attributo ContenitoreDatiGenerali e si fa il forward alla seconda pagina
 * del wizard di importazione
 * 
 * @author Francesco.DeFilippis
 */
public class UploadDefinizioneModelloAction extends ActionBaseNoOpzioni {

  static Logger       logger = Logger.getLogger(UploadDefinizioneModelloAction.class);

  /**
   * Reference al Manager per la gestione delle protezioni di tabelle e campi
   * rispetto al profilo attivo
   */
  private GeneManager geneManager;

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;
    Object contenitore = null;
    ContenitoreDatiImport contenitoreDatiImport = new ContenitoreDatiImport();

    UploadFileForm uploadFile = (UploadFileForm) form;
    // Creazione dell'oggetto XMLDecoder a partire del file in upload
    XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(
        uploadFile.getSelezioneFile().getInputStream()), null, null);
    // Set del contenitore dei dati della ricerca
    try {
      contenitore = decoder.readObject();
    } catch (NoSuchElementException nse) {
      // Eccezione nel caso in cui il file non sia un file xml
      target = CostantiWizard.ERROR_IMPORT_MODELLI;
      messageKey = "errors.genmod.import.fileNoFormatoXml";
      logger.error(this.resBundleGenerale.getString(messageKey), nse);
      this.aggiungiMessaggio(request, messageKey);
    } catch (ArrayIndexOutOfBoundsException aiob) {
      // Eccezione nel caso in cui lo stream associato al file xml non presenti
      // alcun oggetto
      target = CostantiWizard.ERROR_IMPORT_MODELLI;
      messageKey = "errors.genmod.import.fileXmlNoObjects";
      logger.error(this.resBundleGenerale.getString(messageKey), aiob);
      this.aggiungiMessaggio(request, messageKey);
    } finally {
      decoder.close();
    }

    if (CostantiGeneraliStruts.FORWARD_OK.equals(target)) {
      if (contenitore instanceof ContenitoreDatiModello) {
        contenitoreDatiImport.setContenitoreDatiGenerali((ContenitoreDatiModello) contenitore);

        String tmp = null;
        tmp = this.checkModello(
            request,
            contenitoreDatiImport.getContenitoreDatiGenerali().getDatiGenModello(),
            contenitoreDatiImport.getContenitoreDatiGenerali().getElencoParametri());

        if (tmp != null) target = tmp;

        if (target.equals(CostantiGeneraliStruts.FORWARD_OK))
          // Set in sessione del contenitore dati import, che contiene sia le
          // informazioni necessarie al wizard di importazione, sia il
          // contenitore
          // dati del modello
          // ATTENZIONE: in sessione viene inserito l'oggetto
          // ContenitoreDatiImport
          request.getSession().setAttribute(
              CostantiGenModelli.OGGETTO_DETTAGLIO, contenitoreDatiImport);
      } else {
        target = CostantiWizard.ERROR_IMPORT_MODELLI;
        // Errore per importazione di un modello, specificando l'xml dell'export
        // di un report
        messageKey = "errors.genmod.import.fileXML_NoModello";
        logger.error(this.resBundleGenerale.getString(messageKey), null);
        this.aggiungiMessaggio(request, messageKey);
      }
    }
    // Cancellazione dalla sessione del contenitore dati import
    if (target.equals(CostantiWizard.ERROR_IMPORT_MODELLI))
      request.getSession().removeAttribute(
          CostantiGenRicerche.OGGETTO_DETTAGLIO);

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Metodo per la verifica che la entita' sia definita nella base dati. In caso
   * di esito positivo viene prima aggiornato il codApp del modello ed inserito
   * in sessione il modello stesso
   * 
   * @param request
   * @param target
   * @param contenitoreModello
   * @return Ritorna il target della action in funzione dell'operazione
   *         effettuata
   */
  private String checkModello(HttpServletRequest request,
      DatiModello contenitoreModello, Vector parametriModello) {

    if (logger.isDebugEnabled()) logger.debug("checkModello: inizio metodo");

    String target = null;
    String messageKey = null;

    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();

    DizionarioCampi dizCampi = DizionarioCampi.getInstance();
    boolean continua = true;

    String codiceApplicativo = (String) request.getSession().getAttribute(
        CostantiGenerali.MODULO_ATTIVO);
    // ora si esegue la forzatura del codice applicazione nel modello importato
    contenitoreModello.setCodiceApplicativo(codiceApplicativo);

    // l'import di un modello in profili diversi non è permesso
    String codiceProfilo = (String) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);
    // F.D. 11/09/08 WE128: si può importare un modello esportato con un profilo
    // diverso
    // if (continua
    // && (!codiceProfilo.equals(contenitoreModello.getProfiloOwner()) ||
    // !codiceProfilo.equals(contenitoreModello.getProfiloOwner()))) {
    // target = CostantiWizard.ERROR_IMPORT_MODELLI;
    // messageKey = "errors.genmod.import.modelloProfiloDiversoDaProfiloAttivo";
    // logger.error(this.resBundleGenerale.getString(messageKey));
    // this.aggiungiMessaggio(request, messageKey);
    // continua = false;
    // }
    // se il codice del profilo del modello e quello dell'utente che sta
    // importando
    // sono diversi aggiorniamo l'oggetto in modo che in fase di salvataggio il
    // modello
    // sia collegato al profilo che l'ha importato
    if (!codiceProfilo.equals(contenitoreModello.getProfiloOwner())
        || !codiceProfilo.equals(contenitoreModello.getProfiloOwner()))
      contenitoreModello.setProfiloOwner(codiceProfilo);

    if (continua) {
      Tabella entitaPrincipale = dizTabelle.getDaNomeTabella(contenitoreModello.getEntPrinc());

      if (!this.geneManager.getGestoreVisibilitaDati().checkEntitaVisibile(
          entitaPrincipale, codiceProfilo)) {
        target = CostantiWizard.ERROR_IMPORT_MODELLI;
        messageKey = "errors.genmod.import.modelloNonImportabile";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
        continua = false;
      }
    }

    if (continua) {
      // controllo i parametri del modello se ci sono
      if (parametriModello != null && parametriModello.size() > 0) {
        // per ogni parametro di tipo tabellato controllo se il profilo attuale
        // ha il permesso di visualizzazione del campo se no blocco
        // l'importazione
        for (Iterator iter = parametriModello.iterator(); iter.hasNext() && continua;) {
          ParametroModello element = (ParametroModello) iter.next();

          if ("T".equalsIgnoreCase(element.getTipo())) {
            Campo campo = dizCampi.get(element.getTabellato());
            if (campo != null) {
              if (!this.geneManager.getGestoreVisibilitaDati().checkCampoVisibile(
                  campo, codiceProfilo)) {
                target = CostantiWizard.ERROR_IMPORT_MODELLI;
                messageKey = "errors.genmod.import.parametriTabellatiNonVisibili";
                logger.error(this.resBundleGenerale.getString(messageKey));
                this.aggiungiMessaggio(request, messageKey);
                continua = false;
              }
            } else {
              target = CostantiWizard.ERROR_IMPORT_MODELLI;
              messageKey = "errors.genmod.import.tabellatiParametriErrati";
              logger.error(this.resBundleGenerale.getString(messageKey));
              this.aggiungiMessaggio(request, messageKey);
              continua = false;

            }
          } 
        }
      }
    }

    if (logger.isDebugEnabled()) logger.debug("checkModello: fine metodo");

    return target;
  }

}