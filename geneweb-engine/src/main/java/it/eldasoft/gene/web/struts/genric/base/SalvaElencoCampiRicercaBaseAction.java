/*
 * Created on 26-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.base;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicercheBase;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.campo.CampiRicercaForm;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm;
import it.eldasoft.gene.web.struts.genric.ordinamento.OrdinamentoRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.properties.ConfigManager;

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
 * Action per l'inserimento di un elenco di campi in una ricerca base
 *
 * @author Luca.Giacomazzo
 */
public class SalvaElencoCampiRicercaBaseAction extends AbstractActionBaseGenRicercheBase {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(SalvaElencoCampiRicercaBaseAction.class);
//F.D. 07/05/07 la funzione è definita nella classe AbstractActionBaseGenRicercheBase
//  /**
//   * Funzione che restituisce le opzioni per accedere alla action runAction
//   * @return opzioni per accedere alla action
//   */
//  public CheckOpzioniUtente getOpzioniRunAction() {
//    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
//  }

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

    /* Le ricerche base consentono di effettuare la ricerca su campi di una
     * unica tabella. Per questo questa Action inserisce nel contenitore in
     * sessione prima la tabella dei campi selezionati dal client e dopo i campi
     * stessi.
     * Tuttavia in fase di edit, bisogna distinguere due casi:
     * 1. aggiunta di campi della stessa tabella;
     * 2. inserimento di campi di una tabella diversa da quella precedentemente
     *    selezionata. Questo secondo caso comporta anche la cancellazione degli
     *    eventuali filtri, ordinamenti e layout definiti in precedenza;
     */

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    CampiRicercaForm campiRicercaForm = (CampiRicercaForm) form;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
              session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String[] idCampiSelezionati = campiRicercaForm.getCampiSelezionati();
    String id = null;
    String aliasTabella = null;

    // una query non può aver più di un certo numero di colonne; nel generatore
    // ricerche è concesso un massimo di 100 colonne da estrarre
    if (contenitore.getNumeroCampi() + idCampiSelezionati.length > 100) {
      target = "overflowNumeroColonne";
      String messageKey = "errors.genRic.campi.overflowNumeroCampi";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    } else {
      // Se il contenitore non contiene alcuna tabella, allora si inseriscono nel
      // contenitore la tabella relativa ai campi ricevuti dal form e i campi stessi.
      // Se il contenitore contiene gia' una tabella, si verifica se i campi
      // ricevuti nel form sono relativi alla stessa tabella. Se i campi ricevuti
      // nel form sono della tabella gia' presente in sessione, i campi vengono
      // semplicemente inseriti nel contenitore, altrimenti dal contenitore:
      // 1. si cancella la tabella precedentemente inserita;
      // 2. si cancellano tutti i campi precedentemente inseriti;
      // 3. si cancellano tutti i filtri precedentemente inseriti;
      // 4. si cancellano tutti gli ordinamenti precedentemente inseriti;
      // 5. si inseriscono nel contenitore la tabella relativa ai campi
      //    ricevuti dal form e i campi stessi

      if(contenitore.getNumeroTabelle() > 0){
        // Alias dell'unica tabella presente nel contenitore
        String aliasTabellaContenitore =((TabellaRicercaForm)
            contenitore.getElencoArgomenti().get(0)).getAliasTabella();
        id = idCampiSelezionati[0];
        String[] array = id.split("\\.");
        aliasTabella = array[0];
        if(!aliasTabellaContenitore.equals(aliasTabella)){
          // Cancello l'unica tabella inserita nel contenitore
          contenitore.eliminaTabella(0);
          // Cancello tutti i campi inseriti nel contenitore
          contenitore.getElencoCampi().removeAllElements();
          // Cancello tutti i filtri inseriti nel contenitore
          contenitore.getElencoFiltri().removeAllElements();
          // Cancello tutti gli ordinamenti inseriti nel contenitore
          contenitore.getElencoOrdinamenti().removeAllElements();

          TabellaRicercaForm tab = this.creaTestataRicercaForm(aliasTabella,
              (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
          contenitore.aggiungiArgomento(tab);
          contenitore.getTestata().setEntPrinc(tab.getAliasTabella());

          // Inserimento nel contenitore dei campi ricevuti dal form
          for (int i = 0; i < idCampiSelezionati.length; i++)
            contenitore.aggiungiCampo(
                this.creaCampoDaCopiareInSessione(idCampiSelezionati[i]));

        } else {
          // In questo caso il client ha modificato la lista dei campi della ricerca:
          // bisogna controllare se sono dalla nuova lista di campi sono stati
          // eliminati dei campi precedentemente inseriti. Se si bisogna eliminare
          // i filtri e gli ordinamenti precedentemente inseriti associati a
          // tali campi
          Vector<CampoRicercaForm> listaCampiContenitore = contenitore.getElencoCampi();

          CampoRicercaForm campoTmp = null;
          Vector<Integer> listaCampiDaCancellareDalContenitore = new Vector<Integer>();
          for(int i=0; i < listaCampiContenitore.size(); i++){
            campoTmp = (CampoRicercaForm)listaCampiContenitore.get(i);
            boolean isCampoInListaCampiSelezionati = false;

            for(int j=0; j < idCampiSelezionati.length; j++){
              String mneCampoTmp = idCampiSelezionati[j].split("\\.")[1];
              if(mneCampoTmp.equalsIgnoreCase(campoTmp.getMnemonicoCampo()))
                isCampoInListaCampiSelezionati = true;
            }
            if(! isCampoInListaCampiSelezionati)
              listaCampiDaCancellareDalContenitore.addElement(new Integer(i));
          }

          // Continuo ad operare se la lista dei
          if(listaCampiDaCancellareDalContenitore.size() > 0){
            for(int i=0; i < listaCampiDaCancellareDalContenitore.size();i++)
              listaCampiContenitore.remove(
                  ((Integer)listaCampiDaCancellareDalContenitore.get(i)).intValue()-i);
          }
          // A questo punto nell'oggetto listaCampiContenitore (e quindi in
          // sessione) sono rimasti solo i campi da non rimuovere dalla sessione.
          // Su tali campi si deve però mantenere il titolo della colonna

          // Determino la lista dei campi da inserire in sessione come Vector di
          // oggetti CampoRicercaForm
          Vector<CampoRicercaForm> listaCampiDaCopiareInSessione = new Vector<CampoRicercaForm>();
          for(int i=0; i < idCampiSelezionati.length; i++)
            listaCampiDaCopiareInSessione.add(
                this.creaCampoDaCopiareInSessione(idCampiSelezionati[i]));

          // Alla lista dei campi appena determinata, copio i titoli dei campi
          // rimasti in sessione, a parita' di mnemonico campo ovviamente
          for(int i = 0; i < listaCampiContenitore.size(); i++){
            CampoRicercaForm campo1 = (CampoRicercaForm) listaCampiContenitore.get(i);
            for(int j = 0; j < listaCampiDaCopiareInSessione.size(); j++){
              CampoRicercaForm campo2 = (CampoRicercaForm) listaCampiDaCopiareInSessione.get(j);
              if(campo1.getMnemonicoCampo().equals(campo2.getMnemonicoCampo()))
                campo1.setTitoloColonna(campo2.getTitoloColonna());
            }
          }

          // Ora posso inserire la lista dei campi nel contenitore dati ricerca
          // in sessione, rimuovendo prima quelli eventualmente presenti
          for(int i = contenitore.getNumeroCampi() -1; i >= 0; i--)
            contenitore.eliminaCampo(i);
          for(int i = 0; i < listaCampiDaCopiareInSessione.size(); i++)
            contenitore.aggiungiCampo((CampoRicercaForm) listaCampiDaCopiareInSessione.get(i));

          // Tra i campi selezionati dal client cerco se qualcuno di essi ha dei
          // filtri associati. Se no tali filtri li cancello
          Vector<FiltroRicercaForm> listaFiltri = contenitore.getElencoFiltri();
          if(listaFiltri != null && listaFiltri.size() > 0){
            int numeroFiltri = -1;
            for(int i = 0; i < listaFiltri.size(); i++){
              FiltroRicercaForm filtro1 = (FiltroRicercaForm) listaFiltri.get(i);
              numeroFiltri = listaFiltri.size();
              boolean isCampoInListaFiltri = false;
              for(int j = 0; j < listaCampiDaCopiareInSessione.size() && !isCampoInListaFiltri; j++){
                CampoRicercaForm campo1 = (CampoRicercaForm) listaCampiDaCopiareInSessione.get(j);
                if(campo1.getMnemonicoCampo().equals(filtro1.getMnemonicoCampo()))
                  isCampoInListaFiltri = true;
              }

              if(!isCampoInListaFiltri){
                if (i == 0) {
                  // Cancellazione del primo filtro dalla lista
                  contenitore.eliminaFiltro(i);
                  if (numeroFiltri > 1)
                  // Cancellazione dell'operatore di AND successivo
                    contenitore.eliminaFiltro(i);
                } else if (i == (numeroFiltri - 1)) {
                  // Cancellazione dell'ultimo filtro dalla lista
                  contenitore.eliminaFiltro(i);
                  // Cancellazione dell'operatore di AND precedente
                  contenitore.eliminaFiltro(i - 1);
                } else {
                  // Cancellazione dalla lista del filtro indicato con l'id
                  contenitore.eliminaFiltro(i);
                  // Cancellazione dell'operatore di AND successivo
                  contenitore.eliminaFiltro(i);
                }
              }
            }
          }
          // Tra i campi selezionati dal client cerco se qualcuno di essi ha degli
          // ordinamenti associati. Se no tali ordinamenti li cancello
          Vector<OrdinamentoRicercaForm> listaOrdinamenti = contenitore.getElencoOrdinamenti();
          if(listaOrdinamenti != null && listaOrdinamenti.size() > 0){
            for(int i = 0; i < listaOrdinamenti.size(); i++){
              OrdinamentoRicercaForm ordinamento1 = (OrdinamentoRicercaForm) listaOrdinamenti.get(i);

              boolean isCampoInListaOrdinamenti = false;
              for(int j = 0; j < listaCampiDaCopiareInSessione.size() && !isCampoInListaOrdinamenti; j++){
                CampoRicercaForm campo1 = (CampoRicercaForm) listaCampiDaCopiareInSessione.get(j);
                if(campo1.getMnemonicoCampo().equals(ordinamento1.getMnemonicoCampo()))
                  isCampoInListaOrdinamenti = true;
              }
              if(!isCampoInListaOrdinamenti) listaOrdinamenti.remove(i);
            }
          }
        }

        // Reset delle variabili
        id = null;
        array = null;
        aliasTabella = null;
      } else {
        aliasTabella = idCampiSelezionati[0].split("\\.")[0];
        TabellaRicercaForm tab = this.creaTestataRicercaForm(aliasTabella,
            (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
        contenitore.aggiungiArgomento(tab);
        contenitore.getTestata().setEntPrinc(tab.getAliasTabella());

        // Inserimento nel contenitore dei campi ricevuti dal form
        for (int i = 0; i < idCampiSelezionati.length; i++)
          contenitore.aggiungiCampo(
              this.creaCampoDaCopiareInSessione(idCampiSelezionati[i]));
      }

      this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
      this.setMenuTab(request);
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Metodo per la creazione dell'oggetto TabellaRicercaForm a partire dal nome
   * della tabella
   *
   * @param nomeTabella
   * @param request
   * @return
   */
  private TabellaRicercaForm creaTestataRicercaForm(String nomeTabella,
      String moduloAttivo){

    String nomeSchemaViste = ConfigManager.getValore(
        CostantiGenerali.PROP_SCHEMA_VISTE_REPORT_BASE);

    // Inserimento nel contenitore della tabella relativa ai campi
    // ricevuti dal form
    DizionarioSchemi dizSchemi = DizionarioSchemi.getInstance();
    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();

    Tabella tabella1 = dizTabelle.getDaNomeTabella(nomeTabella);

    TabellaRicercaForm tabellaRicercaForm = new TabellaRicercaForm();
    tabellaRicercaForm.setAliasTabella(tabella1.getNomeTabella());
    tabellaRicercaForm.setDescrizioneSchema(dizSchemi.get(nomeSchemaViste).getDescrizione());
    tabellaRicercaForm.setDescrizioneTabella(tabella1.getDescrizione());
    tabellaRicercaForm.setId(null);
    tabellaRicercaForm.setMnemonicoSchema(nomeSchemaViste);
    tabellaRicercaForm.setMnemonicoTabella(tabella1.getCodiceMnemonico());
    tabellaRicercaForm.setNomeTabella(tabella1.getNomeTabella());
    tabellaRicercaForm.setProgressivo(0);
    tabellaRicercaForm.setVisibile(true);

    return tabellaRicercaForm;
  }

  /**
   * Metodo per la creazione del campo da copiare nel contenitore dati ricerca
   * in sessione
   *
   * della tabella
   * @param idCampiSelezionati
   * @return
   */
  private CampoRicercaForm creaCampoDaCopiareInSessione(String idCampoSelezionato){
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


  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menu a tab in fase di visualizzazione del dettaglio dei Dati Generali
   * di una ricerca
   *
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);

    boolean isInSessione = true;
    if (obj == null) {
      isInSessione = false;
      obj = new GestioneTab();
    }

    GestioneTab gestoreTab = (GestioneTab) obj;
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_CAMPI);
    gestoreTab.setTabSelezionabili(new String[] {
        CostantiGenRicerche.TAB_DATI_GENERALI, CostantiGenRicerche.TAB_GRUPPI,
        CostantiGenRicerche.TAB_ARGOMENTI, CostantiGenRicerche.TAB_JOIN,
        CostantiGenRicerche.TAB_PARAMETRI, CostantiGenRicerche.TAB_FILTRI,
        CostantiGenRicerche.TAB_ORDINAMENTI, CostantiGenRicerche.TAB_LAYOUT});

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }

}