/*
 * Created on 15-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.admin.ProfiliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Profilo;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
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
 * Azione che estrae i profili a cui l'utente, che si e' appena autentificato, e'
 * asscoaito e per ciascun verifica la loro validita'. Se nessun profilo viene
 * estratto o se nessuno dei profili risulta essere valido, si va alla pagina di
 * errore generale con un apposito messaggio. Se il numero di profili estratti e
 * validi e' maggiore di uno, si va alla pagina di scelta profilo, altrimenti
 * si passa alla action AssociaProfiloUtenteAction
 *
 * @author Luca.Giacomazzo
 */
public class CheckProfiloAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(CheckProfiloAction.class);

  /**
   * Reference alla classe di business logic per l'estrazione dei profili dalla
   * W_PROFILI e W_ACCPRO
   */
  private ProfiliManager     profiliManager;

  /**
   * Reference al mamager delle informazioni  relative alle librerie generali
   * per la gestione dei profili
   */
  private GeneManager        geneManager;

  /**
   * @param ProfiliManager
   *        ProfiliManager da settare internamente alla classe.
   */
  public void setProfiliManager(ProfiliManager profiliManager) {
    this.profiliManager = profiliManager;
  }

  /**
   * @param geneManager geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    // queste 4 righe sono state prese dalla SceltaApplicazioneAction che è
    // stata rimossa
    HttpSession session = request.getSession();
    this.cleanSession(request);
    session.removeAttribute(CostantiGenerali.MODULO_ATTIVO);
    session.removeAttribute(CostantiGenerali.VERSIONE_MODULO_ATTIVO);

    // legge il codice applicazione dal file di properties
    String codiceApplicazione =
        ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

    ProfiloUtente profiloUtente = (ProfiloUtente)
        request.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Integer idAccount = new Integer(profiloUtente.getId());

    List<?> listaProfiliAccountByCodApp = null;

    if(idAccount != null){
      // Siamo nel caso di un unico codice applicazione
      listaProfiliAccountByCodApp = this.profiliManager.getProfiliAccountByCodApp(
          idAccount.intValue(), codiceApplicazione);

      // Caricamento, se non ancora presenti, nell'Application Context dei
      // profili a cui l'utente e' associato. Per ciascun profilo si verifica se
      // esso e' valido oppure non valido (corrotto) e se nono valido lo si
      // elimina dalla lista dei profili estratti
      if(listaProfiliAccountByCodApp.size() > 0){
        Iterator<?> iter = listaProfiliAccountByCodApp.iterator();
        Vector<Integer> profiliDaCancellare = new Vector<Integer>();
        Profilo profilo = null;
        int i = 0;
        while(iter.hasNext()){
          profilo = (Profilo) iter.next();
          if(! this.geneManager.getProfili().getProfilo((profilo.getCodiceProfilo())).isOk())
            profiliDaCancellare.addElement(new Integer(i));

          i++;
        }

        // Rimozione dalla lista dei profili non validi (cioe' corrotti)
        if(profiliDaCancellare.size() > 0)
          for(int j = profiliDaCancellare.size() - 1; j >= 0; j--)
            listaProfiliAccountByCodApp.remove(profiliDaCancellare.get(j));

        if(listaProfiliAccountByCodApp.size() > 0){
          if(listaProfiliAccountByCodApp.size() == 1){
            // Set nel session dell'attributo che permette di capire se l'utente
            // una volta loggato all'applicazione può tornare alla pagina di
            // scelta profilo oppure può solamente disconnettersi dalla
            // applicazione stessa
            request.getSession().setAttribute(
                  CostantiGenerali.SENTINELLA_UNICO_CODICE_PROFILO, "1");

            // Set nel request l'attributo 'profilo' con il codice dell'unico
            // profilo con cui l'utente puo' accedere all'applicativo
            request.setAttribute("profilo", ((Profilo)
                listaProfiliAccountByCodApp.get(0)).getCodiceProfilo());
          } else {
            // potrei ricevere nella chiamata un codice profilo da utilizzare, e
            // dopo aver controllato che sia uno di quelli associati all'utente
            // lo utilizzo, altrimenti vado alla pagina di selezione del profilo
            String codProfilo = request.getParameter("profilo");
            boolean trovato = false;
            if (codProfilo != null) {
              for (int j = 0; j < listaProfiliAccountByCodApp.size() && !trovato; j++) {
                Profilo tmpProfilo = (Profilo) listaProfiliAccountByCodApp.get(j);
                if (tmpProfilo.getCodiceProfilo().equals(codProfilo)) {
                  request.setAttribute("profilo", tmpProfilo.getCodiceProfilo());
                  trovato = true;
                }
              }
            }
            if (!trovato) {
              // profilo passato come parametro non associato all'utente oppure
              // più di una associazione => vado alla pagina di selezione
              request.setAttribute("listaProfiliAccount", listaProfiliAccountByCodApp);
              request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
                  CostantiGenerali.DISABILITA_NAVIGAZIONE);
              target = target.concat("Lista");
            }
            request.getSession().removeAttribute(
                CostantiGenerali.SENTINELLA_UNICO_CODICE_PROFILO);
          }
        } else {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.checkProfilo.profiliCorrotti";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }
      } else {
        // elimino la sessione in quanto devo bloccare l'accesso
        request.getSession().invalidate();
        // termino con errore
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.checkProfilo.noProfiliAccount";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
//    } else {
//      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
//      messageKey = "errors.checkProfilo.mancaIdAccount";
//      logger.fatal(this.resBundleGenerale.getString(messageKey));
//      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

}