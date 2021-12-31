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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.db.domain.genric.DatiRisultato;
import it.eldasoft.gene.db.domain.genric.RigaRisultato;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
 * Azione che gestisce l'apertura dell'elenco dei modelli per la composizione
 * 
 * @author marco.franceschin
 */
public class ApriElencoModelliAction extends ActionBaseNoOpzioni {

  /** logger della classe */
  static Logger           logger = Logger.getLogger(ApriElencoModelliAction.class);

  /** Manager dei modelli */
  private ModelliManager  modelliManager;

  /** Manager delle ricerche */
  private RicercheManager ricercheManager;

  /** Manager sql generico */
  private SqlManager sqlManager;
  
  /**
   * @param modelliManager
   *        modelliManager da settare internamente alla classe.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }
  
  /**
   * @param sqlManager sqlManager da settare internamente alla classe.
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 14/09/2006 M.F. Prima Versione
    // ************************************************************

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // Setto di default il target come OK
    String target = CostantiGeneraliStruts.FORWARD_OK;

    ComponiModelloForm componiModelloForm = (ComponiModelloForm) form;
    
    this.spostaMessaggiDallaSessione(request);
    
    target = this.estraiElencoModelli(request, componiModelloForm, target);

    request.setAttribute(
        CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_COMPOSIZIONE, form);

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

  //
  // public ActionForward impostaFiltro(ActionMapping mapping, ActionForm form,
  // HttpServletRequest request, HttpServletResponse response)
  // throws IOException, ServletException {
  // if (logger.isDebugEnabled()) logger.debug("impostaFiltro: inizio metodo");
  //
  // // Setto di default il target come OK
  // String target = CostantiGeneraliStruts.FORWARD_OK;
  // ComponiModelloForm componiModelloForm = (ComponiModelloForm) form;
  // target = this.estraiElencoModelli(request, componiModelloForm, target);
  //
  // request.setAttribute("componiModelloForm", form);
  //
  // if (logger.isDebugEnabled()) logger.debug("impostaFiltro: fine metodo");
  // return mapping.findForward(target);
  // }
  //

  /**
   * Estrae l'elenco dei modelli filtrando eventualmente per entità di
   * provenienza se necessario
   * 
   * @param request
   *        request http
   * @param form
   *        il form ricevuto contenente i dati da utilizzare per la composizione
   * @param target
   *        target Struts iniziale
   * @return target modificato
   */
  private String estraiElencoModelli(HttpServletRequest request,
      ComponiModelloForm form, String target) {
    String messageKey = null;
    // A questo punto eseguo il carico dei dati
    try {
      // Esecuzione della logica di business per recuperare la lista dei
      // modelli
      HttpSession session = request.getSession();
      ProfiloUtente profiloUtente = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      String codiceUfficioIntestatario = (String) session.getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO); 

      // if introdotto per eseguire la composizione di un modello per tutti i
      // record estratti di tutte le pagine dopo aver eseguito un report avanzato
      // e non aver selezionato alcuna occorrenza dalla lista
      String[] tmp = form.getValChiavi();
      if (tmp == null && form.getIdRicerca() != null) {
        // si estrae la definizione della ricerca
        ContenitoreDatiRicerca contenitore = this.ricercheManager.getRicercaByIdRicerca(form.getIdRicerca().intValue());
        // si eliminano i campi estratti in modo da reperire solamente i campi
        // che costituiscono la chiave dell'entità principale
        for (int i = contenitore.getNumeroCampi() - 1; i >= 0; i--)
          contenitore.eliminaCampo(i);
        // si recuperano gli eventuali parametri salvati in sessione
        String[] parametriUtente = (String[]) session.getAttribute(CostantiGenRicerche.PARAMETRI_PER_ESTRAZIONE);
        // si estrae il risultato con l'elenco delle chiavi
        DatiRisultato datiRisultato = this.ricercheManager.getRisultatiRicerca(
            contenitore, codiceUfficioIntestatario,parametriUtente, profiloUtente,
            (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO));

        tmp = new String[datiRisultato.getNumeroRecordTotali()];
        RigaRisultato rigaRisultato = null;
        String idPerModello = null;
        // si cicla sulle righe estratte
        for (int cRighe = 0; cRighe < datiRisultato.getNumeroRecordTotali(); cRighe++) {
          rigaRisultato = (RigaRisultato) datiRisultato.getRigheRisultato().get(
              cRighe);
          idPerModello = "";
          // per ogni riga si costruisce la chiave a partire dalle colonne
          // corrispondenti
          for (int cColonne = 0; cColonne < rigaRisultato.getNumeroColonneChiave(); cColonne++) {
            if (cColonne > 0) idPerModello += ";";
            idPerModello += (String) rigaRisultato.getColonneChiave().elementAt(
                cColonne);
          }
          tmp[cRighe] = idPerModello;
        }
      }

      // L.G. 03/04/2007: modifica per gestione univocita' dei valori dei campi
      // chiave delle entita': infatti il report può estrarre più record a
      // parità
      // di valori dei campi chiavi. Questo controllo rimuove dall'array dei
      // valori
      // dei campi chiave dell'oggetto ComponiModelloForm.
      // Per rimuovere i valori duplicati dei campi chiave inserisco in un
      // oggetto
      // List tutti i valori presenti nell'array dei valori dei campi chiave
      // dell'oggetto ComponiModelloForm una sola volta.
      // Dopo inserisco il nuovo array di valori dei campi chiave nell'oggetto
      // ComponiModelloForm.
      ArrayList<String> listaChiaviUnivoche = new ArrayList<String>();
      for (int i = 0; i < tmp.length; i++) {
        if (!listaChiaviUnivoche.contains(tmp[i])) {
          listaChiaviUnivoche.add(tmp[i]);
        }
      }
      tmp = (String[]) listaChiaviUnivoche.toArray(new String[0]);
      form.setValChiavi(tmp);
      // L.G. - fine modifica per gestione univocita' dei valori dei campi
      // chiavi -
      
      // Lista delle query eseguite per ciascun modello definito sull'entita' di
      // partenza, per determinare se l'occorrenza da cui si lancia la lista dei
      // modelli soddisfa alla condizione definita nel modello al momento della
      // sua definizione
      List<String[]> listaSqlSelect = new ArrayList<String[]>();;

      boolean isRiepilogativo = (form.getPaginaSorgente() == null ||
          form.getPaginaSorgente().length() == 0); 
      
      List<?> listaModelli = this.modelliManager.getModelliPredefiniti(
          profiloUtente.getId(),
          (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO),
          (String) session.getAttribute(CostantiGenerali.PROFILO_ATTIVO),
          isRiepilogativo, form.getEntita());

      if (!isRiepilogativo && form.getNoFiltroEntitaPrincipale() == 0){
        if(listaModelli != null && !listaModelli.isEmpty()){
          DizionarioCampi dizionarioCampi = DizionarioCampi.getInstance();
          
          StringBuffer preSqlSelect = new StringBuffer("select COUNT(1) from ");
          preSqlSelect.append(form.getEntita().toUpperCase().concat(" where "));
          
          String[] campiChiave = form.getNomeChiavi().split(";");
          String[] valoriCampiChiave = form.getValChiavi()[0].split(";");
          
          Object[] sqlParam = new Object[campiChiave.length];
          
          // Creazione condizioni di filtro sui campi chiave
          for(int j=0; j < campiChiave.length; j++){
            preSqlSelect.append(campiChiave[j] + " = ? and ");
            Campo campo = dizionarioCampi.getCampoByNomeFisico(form.getEntita() + "." + campiChiave[j]);
            if(Campo.TIPO_STRINGA == campo.getTipoColonna())
              sqlParam[j] = valoriCampiChiave[j];
            else
              sqlParam[j] = new Long(valoriCampiChiave[j]);
          }
          
          StringBuffer tmpStrParams = new StringBuffer("[ ");
          for(int u=0; u < sqlParam.length; u++){
            tmpStrParams.append(sqlParam[u].toString());
            if(u < sqlParam.length -1)
              tmpStrParams.append(", ");
          }
          tmpStrParams.append(" ]");
          request.setAttribute("sqlParams", tmpStrParams);
          
          StringBuffer nomeModelliCondizioneFiltroErrata = new StringBuffer("");
          
          for(int i = listaModelli.size()-1; i >= 0 ; i--){
            DatiModello modello = (DatiModello) listaModelli.get(i);
            if(modello.getFiltroEntPrinc() != null && modello.getFiltroEntPrinc().length() > 0){
              
              // Aggiungo la condizione di filtro per l'entita principale
              String sqlSelect = preSqlSelect.toString() + modello.getFiltroEntPrinc();
              // Memorizzo la query
              listaSqlSelect.add(new String[]{modello.getNomeModello(), sqlSelect.toString()});
              try {
              Long numeroOccorrenze = (Long) this.sqlManager.getObject(
                  sqlSelect.toString(), sqlParam);
              if(numeroOccorrenze == null ||
                  (numeroOccorrenze != null && numeroOccorrenze.longValue() == 0))
                listaModelli.remove(i);
              } catch(SQLException s){
                logger.error("Errore durante l'esecuzione della query per verificare il filtro del modello sul record", s);
                nomeModelliCondizioneFiltroErrata.append(
                    "'" + modello.getNomeModello() + "', ");
              }
            }
          }
          if(nomeModelliCondizioneFiltroErrata.length() > 0){
            String strTmp = nomeModelliCondizioneFiltroErrata.toString();
            strTmp = strTmp.substring(0, strTmp.lastIndexOf(","));
            this.aggiungiMessaggio(request,
                "errors.modelli.listaModelliPredisposti.condizioneFiltro", 
                strTmp);
          }
        }
      }

      // Setto l'elenco dei modelli per la composizione
      request.setAttribute("listaModelli", listaModelli);
      // Setto l'elenco delle query eseguite per determinare se visualizzare i
      // modelli nell'elenco in base allo stato della scheda in visualizzazione
      request.setAttribute("listaSqlSelect", listaSqlSelect);
      
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

    return target;
  }

}
