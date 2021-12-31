/*
 * Created on 27-apr-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.base.wizard;

import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicercheBase;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.campo.CampiRicercaForm;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm;
import it.eldasoft.gene.web.struts.genric.ordinamento.OrdinamentoRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * DispatchAction per la memorizzazione in sessione dei campi della ricerca base
 * 
 * @author Luca.Giacomazzo
 */
public class CampiAction extends AbstractDispatchActionBaseGenRicercheBase {

  private final String SUCCESS_SALVA      = "successSalva";
  private final String SUCCESS_SALVA_FINE = "successFine";

  static Logger        logger             = Logger.getLogger(CampiAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere al metodo salvaCampi della
   * DispatchAction
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSalvaCampi() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  /**
   * Metodo per il salvataggio dei campi da estrarre in una ricerca base
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward salvaCampi(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("SalvaCampi: inizio metodo");

    // target di default
    String target = null;
    String tmp = request.getParameter("pageFrom");
    if (tmp != null && tmp.length() > 0) {
      target = SUCCESS_SALVA_FINE;
      request.setAttribute("pageFrom", request.getParameter("pageFrom"));
    } else {
      target = SUCCESS_SALVA;
    }

    /*
     * Le ricerche base consentono di effettuare la ricerca su campi di una
     * unica tabella. Per questo motivo questa Action inserisce nel contenitore
     * in sessione i campi stessi. Tuttavia in fase di edit, bisogna distinguere
     * due casi: 1. aggiunta di campi della stessa tabella; 2. inserimento di
     * campi di una tabella diversa da quella precedentemente selezionata.
     * Questo secondo caso comporta anche la cancellazione degli eventuali
     * filtri, ordinamenti e layout definiti in precedenza;
     */

    CampiRicercaForm campiRicercaForm = (CampiRicercaForm) form;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
          session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String[] idCampiSelezionati = campiRicercaForm.getCampiSelezionati();

    // una query non può aver più di un certo numero di colonne; nel generatore
    // ricerche è concesso un massimo di 100 colonne da estrarre
    if (contenitore.getNumeroCampi() + idCampiSelezionati.length > 100) {
      target = "overflowNumeroColonne";
      String messageKey = "errors.genRic.campi.overflowNumeroCampi";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    } else {
      // Il contenitore deve contenere una tabella, della quale si inseriscono
      // nel contenitore i campi ricevuti dal form.
      // I campi ricevuti nel form sono della tabella gia' presente in sessione,
      // i campi vengono semplicemente inseriti nel contenitore.

      if (contenitore.getNumeroCampi() > 0) {
        // In questo caso il client ha modificato la lista dei campi della
        // ricerca:
        // bisogna controllare se sono dalla nuova lista di campi sono stati
        // eliminati dei campi precedentemente inseriti. Se si bisogna eliminare
        // i filtri e gli ordinamenti precedentemente inseriti associati a
        // tali campi
        Vector<CampoRicercaForm> listaCampiContenitore = contenitore.getElencoCampi();

        CampoRicercaForm campoTmp = null;
        Vector<Integer> listaCampiDaCancellareDalContenitore = new Vector<Integer>();
        for (int i = 0; i < listaCampiContenitore.size(); i++) {
          campoTmp = (CampoRicercaForm) listaCampiContenitore.get(i);
          boolean isCampoInListaCampiSelezionati = false;

          for (int j = 0; j < idCampiSelezionati.length; j++) {
            String mneCampoTmp = idCampiSelezionati[j].split("\\.")[1];
            if (mneCampoTmp.equalsIgnoreCase(campoTmp.getMnemonicoCampo()))
              isCampoInListaCampiSelezionati = true;
          }
          if (!isCampoInListaCampiSelezionati)
            listaCampiDaCancellareDalContenitore.addElement(new Integer(i));
        }

        // Continuo ad operare se la lista dei
        if (listaCampiDaCancellareDalContenitore.size() > 0) {
          for (int i = 0; i < listaCampiDaCancellareDalContenitore.size(); i++)
            listaCampiContenitore.remove(((Integer) listaCampiDaCancellareDalContenitore.get(i)).intValue()
                - i);
        }
        // A questo punto nell'oggetto listaCampiContenitore (e quindi in
        // sessione) sono rimasti solo i campi da non rimuovere dalla sessione.
        // Su tali campi si deve però mantenere il titolo della colonna

        // Determino la lista dei campi da inserire in sessione come Vector di
        // oggetti CampoRicercaForm
        Vector<CampoRicercaForm> listaCampiDaCopiareInSessione = new Vector<CampoRicercaForm>();
        for (int i = 0; i < idCampiSelezionati.length; i++)
          listaCampiDaCopiareInSessione.add(this.creaCampoDaCopiareInSessione(idCampiSelezionati[i]));

        // Alla lista dei campi appena determinata, copio i titoli dei campi
        // rimasti in sessione, a parita' di mnemonico campo ovviamente
        for (int i = 0; i < listaCampiContenitore.size(); i++) {
          CampoRicercaForm campo1 = (CampoRicercaForm) listaCampiContenitore.get(i);
          for (int j = 0; j < listaCampiDaCopiareInSessione.size(); j++) {
            CampoRicercaForm campo2 = (CampoRicercaForm) listaCampiDaCopiareInSessione.get(j);
            if (campo1.getMnemonicoCampo().equals(campo2.getMnemonicoCampo()))
              campo1.setTitoloColonna(campo2.getTitoloColonna());
          }
        }

        // Ora posso inserire la lista dei campi nel contenitore dati ricerca
        // in sessione, rimuovendo prima quelli eventualmente presenti
        for (int i = contenitore.getNumeroCampi() - 1; i >= 0; i--)
          contenitore.eliminaCampo(i);
        for (int i = 0; i < listaCampiDaCopiareInSessione.size(); i++)
          contenitore.aggiungiCampo((CampoRicercaForm) listaCampiDaCopiareInSessione.get(i));

        // Tra i campi selezionati dal client cerco se qualcuno di essi ha dei
        // filtri associati. Se no tali filtri li cancello
        if (contenitore.getNumeroFiltri() > 0) {
          for (int i = contenitore.getNumeroFiltri() - 1; i >= 0; i--) {
            FiltroRicercaForm filtro1 = contenitore.estraiFiltro(i);

            boolean isCampoInListaFiltri = false;
            for (int j = 0; j < listaCampiDaCopiareInSessione.size()
                && !isCampoInListaFiltri; j++) {
              CampoRicercaForm campo1 = (CampoRicercaForm) listaCampiDaCopiareInSessione.get(j);
              if (campo1.getMnemonicoCampo().equals(filtro1.getMnemonicoCampo()))
                isCampoInListaFiltri = true;
            }
            if (!isCampoInListaFiltri) 
              contenitore.eliminaFiltro(i);
          }
        }
        // Tra i campi selezionati dal client cerco se qualcuno di essi ha degli
        // ordinamenti associati. Se no tali ordinamenti li cancello
        if (contenitore.getNumeroOrdinamenti() > 0) {
          for (int i = contenitore.getNumeroOrdinamenti() - 1; i >= 0; i--) {
            OrdinamentoRicercaForm ordinamento1 = contenitore.estraiOrdinamento(i);

            boolean isCampoInListaOrdinamenti = false;
            for (int j = 0; j < listaCampiDaCopiareInSessione.size()
                && !isCampoInListaOrdinamenti; j++) {
              CampoRicercaForm campo1 = (CampoRicercaForm) listaCampiDaCopiareInSessione.get(j);
              if (campo1.getMnemonicoCampo().equals(
                  ordinamento1.getMnemonicoCampo()))
                isCampoInListaOrdinamenti = true;
            }
            if (!isCampoInListaOrdinamenti) 
              contenitore.eliminaOrdinamento(i);
          }
        }
      } else {
        // Inserimento nel contenitore dei campi ricevuti dal form
        for (int i = 0; i < idCampiSelezionati.length; i++)
          contenitore.aggiungiCampo(this.creaCampoDaCopiareInSessione(idCampiSelezionati[i]));
      }
    }
    if (logger.isDebugEnabled()) logger.debug("SalvaCampi: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Metodo per la creazione del campo da copiare nel contenitore dati ricerca
   * in sessione
   * 
   * della tabella
   * 
   * @param idCampiSelezionati
   * @return
   */
  private CampoRicercaForm creaCampoDaCopiareInSessione(
      String idCampoSelezionato) {
    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    String aliasTabella = null;
    String[] array = null;
    String mneCampo = null;
    CampoRicercaForm campo = null;

    // il campo ricevuto lo si splitta per ottenere l'alias tabella e il
    // mnemonico campo
    array = idCampoSelezionato.split("\\.");
    aliasTabella = array[0];
    mneCampo = array[1];
    // si impostano le informazioni relative al campo da inserire
    campo = new CampoRicercaForm();
    campo.setAliasTabella(aliasTabella);
    Tabella tabella1 = dizTabelle.getDaNomeTabella(aliasTabella);

    campo.setDescrizioneTabella(tabella1.getDescrizione());
    campo.setMnemonicoTabella(tabella1.getCodiceMnemonico());
    campo.setMnemonicoCampo(mneCampo);
    campo.setDescrizioneCampo(dizCampi.get(mneCampo).getDescrizione());

    // Al momento dell'inserimento del campo si setta per default come titolo
    // della colonna la descrizione del campo stesso presente in C0CAMPI
    campo.setTitoloColonna(dizCampi.get(mneCampo).getDescrizione());

    return campo;
  }

}