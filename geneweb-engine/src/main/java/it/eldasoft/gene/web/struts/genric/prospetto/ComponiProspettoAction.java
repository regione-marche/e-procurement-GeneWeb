/*
 * Created on 21-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.prospetto;

import it.eldasoft.gene.bl.genmod.CompositoreException;
import it.eldasoft.gene.bl.genmod.GestioneFileModelloException;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.web.struts.genmod.ComponiModelloAction;
import it.eldasoft.gene.web.struts.genmod.ComponiModelloForm;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Ridefinizione della classe ComponiModelloAction per la gestione del prospetto
 * associato ad una ricerca con modello
 * 
 * @author Luca.Giacomazzo
 */
public class ComponiProspettoAction extends ComponiModelloAction {

  /** logger della classe */
  static Logger logger = Logger.getLogger(ComponiProspettoAction.class);

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_DEFAULT;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action download
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniDownload() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
  }

  /**
   * Funzione che esegue il download del file composto e la cancellazione dalle
   * cartelle tmp e/o out del file stesso
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
    if (logger.isDebugEnabled()) logger.debug("Download: inizio metodo");
    String target = null;

    String fileComposto = null;
    String messageKey = null;
    ParametriProspettoForm parametriProspettoForm = null;
    ComponiModelloForm componiModelloForm = null;
    try {
      componiModelloForm = (ComponiModelloForm) form;
      parametriProspettoForm = (ParametriProspettoForm) form;
      request.setAttribute(
          CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_COMPOSIZIONE,
          componiModelloForm);

      fileComposto = componiModelloForm.getFileComposto();
      modelliManager.downloadFileComposto(fileComposto,
          (String) request.getSession().getAttribute(
              CostantiGenerali.MODULO_ATTIVO), response);

    } catch (GestioneFileModelloException e) {
      // Eseguo l'eliminazione del file composto
      if (fileComposto != null && fileComposto.length() > 0) {
        this.modelliManager.eliminaFileComposto(fileComposto,
            (String) request.getSession().getAttribute(
                CostantiGenerali.MODULO_ATTIVO));
      }
      target = ComponiModelloAction.FORWARD_ERRORE;
      messageKey = "errors.modelli.uploaderror";
      // Aggiungo l'eventuale codice in più
      if (!e.getCodiceErrore().equals(""))
        messageKey += "." + e.getCodiceErrore();
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);

    } catch (Throwable t) {
      target = ComponiModelloAction.FORWARD_ERRORE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }
    if (messageKey == null) {
      try {
        // Se in precedenza è stato composto un modello lo elimino dai
        // temporanei
        Object modelloComposto = request.getSession().getAttribute(
            ComponiModelloAction.SESSION_MODELLO_COMPOSTO);
        if (modelloComposto != null) {
          if (modelloComposto instanceof String) {
            // Elimino il modello composto in precedenza
            this.modelliManager.eliminaFileComposto((String) modelloComposto,
                (String) request.getSession().getAttribute(
                    CostantiGenerali.MODULO_ATTIVO));
          }

          request.getSession().setAttribute(
              ComponiModelloAction.SESSION_MODELLO_COMPOSTO, null);
        }
        request.setAttribute(
            CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_COMPOSIZIONE,
            componiModelloForm);

        request.setAttribute("idProspetto",
            parametriProspettoForm.getIdProspetto());

      } catch (CompositoreException e) {
        // Si è verificato l'errore di composizione
        target = ComponiModelloAction.FORWARD_ERRORE;
        messageKey = e.getChiaveResourceBundle();
        if (e.getParametri() == null) {
          logger.error(this.resBundleGenerale.getString(messageKey), e);
          this.aggiungiMessaggio(request, messageKey);
        } else if (e.getParametri().length == 1) {
          logger.error(UtilityStringhe.replaceParametriMessageBundle(
              this.resBundleGenerale.getString(messageKey), e.getParametri()),
              e);
          this.aggiungiMessaggio(request, messageKey, e.getParametri()[0]);
        } else {
          logger.error(UtilityStringhe.replaceParametriMessageBundle(
              this.resBundleGenerale.getString(messageKey), e.getParametri()),
              e);
          this.aggiungiMessaggio(request, messageKey, e.getParametri()[0],
              e.getParametri()[1]);
        }
      } catch (RemoteException r) {
        target = ComponiModelloAction.FORWARD_ERRORE;
        messageKey = "errors.modelli.compositoreDisattivo";
        logger.error(this.resBundleGenerale.getString(messageKey), r);
        this.aggiungiMessaggio(request, messageKey);
      } catch (Throwable t) {
        target = ComponiModelloAction.FORWARD_ERRORE;
        messageKey = "errors.applicazione.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), t);
        this.aggiungiMessaggio(request, messageKey);
      }
    }

    if (logger.isDebugEnabled()) logger.debug("Download: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action download
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniComponiModello() {
    return new CheckOpzioniUtente(CostantiGenerali.ABILITAZIONE_DEFAULT);
  }

  public ActionForward componiModello(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("componiModello: inizio metodo");
    ParametriProspettoForm parametriProspettoForm = (ParametriProspettoForm) form;

    request.setAttribute("idProspetto", parametriProspettoForm.getIdProspetto());
    
	String messageKey = null;

	ComponiModelloForm componiModelloForm = (ComponiModelloForm) form;

	DatiModello datiModello = this.modelliManager.getModelloById(componiModelloForm.getIdModello());

	// Nel caso il modello sia da convertire in PDF
	if (datiModello.getPdf() == 1) {
		String[] listaEstPdf;
		String nomeFile;
		String estensione;

		// Estraggo lista estensioni pdf per cui il modello puo' essere convertito in
		// PDF
		listaEstPdf = this.modelliManager.getEstensioniModelloOutputPDF();
		nomeFile = datiModello.getNomeFile();
		estensione = nomeFile.substring(nomeFile.lastIndexOf(".") + 1).toUpperCase();

		// Verifico se l'estensione del modello è compatibile con
		if (!Arrays.asList(listaEstPdf).contains(estensione) && !(listaEstPdf.length == 1 && "*".equals(listaEstPdf[0]))) {

			messageKey = "warnings.prospetto.caricaProspetto.modelloNonConvertibilePDF";
			logger.warn(this.resBundleGenerale.getString(messageKey));
			this.aggiungiMessaggio(request, messageKey);
		}
	}
    
    ActionForward actForward = super.componiModello(mapping,
        (ComponiModelloForm) form, request, response);

    // TODO: composizione di un prospetto
    // L.G. 30/10/2007: nel caso il prospetto si basi su un'entita' non visibile
    // nel profilo attivo, il prospetto non viene eseguito, ma il messaggio a
    // video e' relativo ad un modello. Questo perche' questo tipo di errore e'
    // un caso poco probabile e gestirlo porterebbe via troppo tempo (Il gioco
    // non vale la candela...)

    if (logger.isDebugEnabled()) logger.debug("componiModello: fine metodo");
    return actForward;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * eliminaComposizione
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEliminaComposizione() {
    return new CheckOpzioniUtente(CostantiGenerali.ABILITAZIONE_DEFAULT);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action associaModello
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniAssociaModello() {
    // questa definizione non ha senso, perchè i report con modello non
    // possono essere associati ad entità
    return new CheckOpzioniUtente(CostantiGenerali.ABILITAZIONE_DEFAULT);
  }
}