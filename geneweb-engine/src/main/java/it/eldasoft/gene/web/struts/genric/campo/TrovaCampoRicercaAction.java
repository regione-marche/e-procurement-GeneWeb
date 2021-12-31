/*
 * Created on 11-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.campo;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.MetadatiManager;
import it.eldasoft.gene.bl.genric.GestoreVisibilitaDati;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityStringhe;

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
import org.springframework.dao.DataAccessException;

/**
 * Action per l'inserimento di un nuovo campo in una ricerca o per la modifica
 * di un campo esistente
 * 
 * @author Stefano.Sabbadin
 */
public class TrovaCampoRicercaAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger           logger = Logger.getLogger(TrovaCampoRicercaAction.class);

  /** Manager per l'accesso ai metadati in fase di ricerca del campo */
  private MetadatiManager metadatiManager;

  /**
   * @param metadatiManager
   *        metadatiManager da settare internamente alla classe.
   */
  public void setMetadatiManager(MetadatiManager metadatiManager) {
    this.metadatiManager = metadatiManager;
  }

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

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
        sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    CampoRicercaForm campoRicercaForm = (CampoRicercaForm) form;
    String messageKey = null;

    try {
      // per questioni di semplicita' si effettua l'escape incondizionato delle
      // stringhe di ricerca, in modo da non passare troppi parametri al
      // metadatiManager (alla fine filtra solo su 2 campi)      
      String mnemonicoCampo = campoRicercaForm.getMnemonicoCampo();
      if (!"=".equals(campoRicercaForm.getOperatoreMnemonicoCampo()))
        mnemonicoCampo = UtilityStringhe.escapeSqlString(mnemonicoCampo);
      String descrizioneCampo = campoRicercaForm.getDescrizioneCampo();
      if (!"=".equals(campoRicercaForm.getOperatoreDescrizioneCampo()))
        descrizioneCampo = UtilityStringhe.escapeSqlString(descrizioneCampo);
      
      // estrazione dei mnemonici per ricerche
      String[] elencoMnemonici =
        this.metadatiManager.getElencoMnemoniciPerRicerche(
          GestioneOperatoreConfrontoStringa.convertiStringaConfronto(
              campoRicercaForm.getOperatoreMnemonicoCampo(),
              mnemonicoCampo),
          GestioneOperatoreConfrontoStringa.convertiOperatoreConfronto(
              campoRicercaForm.getOperatoreMnemonicoCampo()),
          GestioneOperatoreConfrontoStringa.convertiStringaConfronto(
              campoRicercaForm.getOperatoreDescrizioneCampo(),
              descrizioneCampo), 
          GestioneOperatoreConfrontoStringa.convertiOperatoreConfronto(
              campoRicercaForm.getOperatoreDescrizioneCampo()));

      String mnemonico = null;
      Campo campo = null;
      Tabella tabella = null;
      TabellaRicercaForm tabellaForm = null;
      CampoRicercaForm campoForm = null;
      Vector<CampoRicercaForm> elencoCampi = new Vector<CampoRicercaForm>();
      boolean trovato;

      GestoreVisibilitaDati gestoreVisibilita = this.geneManager.getGestoreVisibilitaDati();
      String profiloAttivo = (String) sessione.getAttribute(CostantiGenerali.PROFILO_ATTIVO);

      if (elencoMnemonici != null && elencoMnemonici.length > 0) {
        // ciclo sui mnemonici
        for (int i = 0; i < elencoMnemonici.length; i++) {
          // estraggo l'elemento i-esimo, ottengo il suo dettaglio dal
          // dizionario, verifico se esiste già nella query la tabella che lo
          // contiene oppure no; in caso affermativo riporta nel risultato il
          // riferimento a tale tabella, altrimenti predispone l'inserimento di
          // una nuova tabella
          mnemonico = elencoMnemonici[i];
          campo = dizCampi.get(mnemonico);

          // è bene verificare se il campo esiste nel dizionario. Il popolamento
          // del dizionario campi avviene sulla base delle tabelle, e quindi
          // andando ad eseguire una query esplicita sulla tabella C0CAMPI si
          // potrebbero ottenere dei campi che poi non sono censiti nel
          // dizionario in quanto non hanno una tabella collegata nella C0ENTIT
          // oppure non vengono utilizzati nel generatore ricerche
          if (campo != null) {

            // verifico se il campo è visibile nel profilo
            if (gestoreVisibilita.checkCampoVisibile(campo, profiloAttivo)) {

              trovato = false;
              for (int j = 0; j < contenitore.getNumeroTabelle(); j++) {
                tabellaForm = contenitore.estraiTabella(j);
                if (tabellaForm.getNomeTabella().equals(campo.getNomeTabella())) {
                  trovato = true;
                  campoForm = new CampoRicercaForm();
                  campoForm.setMnemonicoTabella(tabellaForm.getMnemonicoTabella());
                  campoForm.setAliasTabella(tabellaForm.getAliasTabella());
                  campoForm.setDescrizioneTabella(tabellaForm.getDescrizioneTabella());
                  campoForm.setMnemonicoCampo(campo.getCodiceMnemonico());
                  campoForm.setDescrizioneCampo(campo.getDescrizione());
                  elencoCampi.addElement(campoForm);
                }
              }

              // se l'elemento non è stato trovato nelle tabelle, allora occorre
              // eventualmente inserire una nuova tabella, e quindi si ritorna
              // il
              // risultato con i riferimenti per la nuova tabella
              if (!trovato) {
                tabella = dizTabelle.getDaNomeTabella(campo.getNomeTabella());
                // SS 25/10/2006: inserito controllo se la tabella è visibile
                // nelle ricerche
                if (gestoreVisibilita.checkEntitaVisibile(tabella,
                    profiloAttivo)) {
                  campoForm = new CampoRicercaForm();
                  campoForm.setMnemonicoTabella(tabella.getCodiceMnemonico());
                  campoForm.setAliasTabella(tabella.getNomeTabella());
                  campoForm.setDescrizioneTabella(tabella.getDescrizione());
                  campoForm.setMnemonicoCampo(campo.getCodiceMnemonico());
                  campoForm.setDescrizioneCampo(campo.getDescrizione());
                  elencoCampi.addElement(campoForm);
                }
              }
            }
          }
        }

        request.setAttribute("elencoCampi", elencoCampi);
      }

      if (elencoCampi.size() == 0) {
        target = CostantiGeneraliStruts.FORWARD_NESSUN_RECORD_TROVATO;
        messageKey = "info.search.noRecordFound";
        this.aggiungiMessaggio(request, messageKey);
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

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }
}