/*
 * Created on 6-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * Questa Action risulta la base di tutte le Action da definire nella web
 * application. Vengono eseguiti all'interno dei controlli che bloccano
 * l'esecuzione della stessa nel qual caso:
 * <ul>
 * <li>sia scaduta la sessione</li>
 * <li>non sia stata acquistata dal cliente l'opzione della funzionalità
 * associata all'azione</li>
 * <li>l'utente non risulta abilitato alla funzionalità associata all'azione</li>
 * </ul>
 * <br>
 * Nel caso in cui i controlli vadano a buon fine, la richiesta viene
 * soddisfatta. Ogni azione che estende la presente necessita l'implementazione
 * del metodo {@link #runAction()}
 *
 * @author Manuel.Bridda
 */
public abstract class ActionBaseNoSessione extends Action implements ActionInterface {

  // ************************************************************
  // Storia Modifiche:
  // Data Utente Descrizione
  // 10/11/2006 M.F. Implemento l'interfaccia delle azioni per utilizzarla nel
  // gestore delle eccezioni
  // ************************************************************

  private static final String PREFISSO_CHIAVE_WARNING = "warnings";
  private static final String PREFISSO_CHIAVE_INFO    = "info";

  private static final String MESSAGGIO_INFO          = "info";
  private static final String MESSAGGIO_SEGNALAZIONE  = "warning";
  public static final String  MESSAGGIO_ERRORE        = "error";

  /** Logger Log4J di classe */
  static Logger               logger                  = Logger.getLogger(ActionBaseNoSessione.class);

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  public ResourceBundle       resBundleGenerale       = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /**
   * @see Action#execute(ActionMapping,
   *      ActionForm,
   *      HttpServletRequest,
   *      HttpServletResponse)
   */
  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 25/10/2006 M.F. Aggiunta del settaggio degli attributi generali per i
    // tags (per essere utilizati nelle JSP con i tag di gene)
    // ************************************************************

    String target = null;
    String messageKey = null;

    DataSourceTransactionManagerBase.setRequest(request);

    if (target != null)
      return mapping.findForward(target);
    else {
      // {M.F. 25.10.2006} Aggiungo il richiamo delle funzione che avviene
      // prima dell'esecuzione di una Action.
      UtilityTags.preAction(request);
      ActionForward ret = this.runAction(mapping, form, request, response);
      UtilityTags.postAction(request);
      return ret;
    }
  }

  /**
   * Verifica che la sessione sia attiva
   *
   * @param request
   *        request http
   * @return true se la sessione del client è attiva, false altrimenti
   */
  protected final boolean verificaSessionePresente(HttpServletRequest request) {
    boolean esito = false;

    HttpSession session = request.getSession();
    String sentinella = (String) session.getAttribute(CostantiGenerali.SENTINELLA_SESSION_TIMEOUT);
    if (sentinella != null) esito = true;

    return esito;
  }
  



  /**
   * Metodo da implementare per svolgere la logica di business necessaria
   * all'azione stessa. Viene richiamata all'interno della {@link #execute()}
   *
   * @param mapping
   *        mapping delle azioni Struts
   * @param form
   *        form popolato con i dati provenienti dal request
   * @param request
   *        http request
   * @param response
   *        http response
   */
  protected abstract ActionForward runAction(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException;

  /**
   * Crea un ActionMessages nel request, popolato con il messaggio individuato
   * dalla chiave in input. Se la chiave del messaggio da inserire e' gia'
   * presente il messaggio non viene inserito la seconda volta
   *
   * @param request
   *        request http
   * @param chiave
   *        chiave del messaggio da inserire
   */
  protected void aggiungiMessaggio(HttpServletRequest request, String chiave) {
    String tipoMessaggio = ActionBaseNoSessione.getTipoMessaggioFromChiave(chiave);
    /*
     * L.G. 07/06/2007: modifica per evitare di inserire più di una volta lo
     * stesso messaggio. Se la chiave del messaggio da inserire e' gia' presente
     * nel oggetto ActionMessages del request, il messaggio non viene inserito
     * la seconda volta, altrimenti viene inserito
     */
    ActionMessages errorsInTheRequest = this.getMessages(request);
    @SuppressWarnings("unchecked")
    Iterator<ActionMessage> iter = errorsInTheRequest.get(tipoMessaggio);

    boolean isMessaggioPresente = false;
    while (iter.hasNext() && !isMessaggioPresente) {
      ActionMessage message = iter.next();
      if (message.getKey().equals(chiave)) isMessaggioPresente = true;
    }
    if (!isMessaggioPresente) {
      ActionMessages errors = new ActionMessages();
      errors.add(tipoMessaggio, new ActionMessage(chiave));
      if (!errors.isEmpty()) this.addMessages(request, errors);
    }
  }

  /**
   * Crea un ActionMessages nel request, popolato con il messaggio individuato
   * dalla chiave in input e con un argomento. Se la chiave e l'argomento del
   * messaggio da inserire sono gia' presenti il messaggio non viene inserito la
   * seconda volta
   *
   * @param request
   *        request http
   * @param chiave
   *        chiave del messaggio da inserire
   * @param argomento1
   *        argomento 1
   */
  protected void aggiungiMessaggio(HttpServletRequest request, String chiave,
      String argomento1) {
    String tipoMessaggio = ActionBaseNoSessione.getTipoMessaggioFromChiave(chiave);
    /*
     * L.G. 07/06/2007: modifica per evitare di inserire più di una volta lo
     * stesso messaggio. Se la chiave e l'argomento del messaggio da inserire
     * sono gia' presenti nel oggetto ActionMessages del request, il messaggio
     * non viene inserito la seconda volta, altrimenti viene inserito
     */
    ActionMessages errorsInTheRequest = this.getMessages(request);
    @SuppressWarnings("unchecked")
    Iterator<ActionMessage> iter = errorsInTheRequest.get(tipoMessaggio);
    boolean isMessaggioPresente = false;
    while (iter.hasNext() && !isMessaggioPresente) {
      ActionMessage message = iter.next();
      if (message.getKey().equals(chiave)
          && ((String) message.getValues()[0]).equals(argomento1))
        isMessaggioPresente = true;
    }
    if (!isMessaggioPresente) {
      ActionMessages errors = new ActionMessages();
      errors.add(tipoMessaggio, new ActionMessage(chiave, argomento1));
      if (!errors.isEmpty()) this.addMessages(request, errors);
    }
  }

  /**
   * Crea un ActionMessages nel request, popolato con il messaggio individuato
   * dalla chiave in input e con due argomenti. Se la chiave e i due argomenti
   * del messaggio da inserire sono gia' presenti il messaggio non viene
   * inserito la seconda volta
   *
   * @param request
   *        request http
   * @param chiave
   *        chiave del messaggio da inserire
   * @param argomento1
   *        argomento 1
   * @param argomento2
   *        argomento 2
   */
  protected void aggiungiMessaggio(HttpServletRequest request, String chiave,
      String argomento1, String argomento2) {
    String tipoMessaggio = ActionBaseNoSessione.getTipoMessaggioFromChiave(chiave);
    /*
     * L.G. 07/06/2007: modifica per evitare di inserire più di una volta lo
     * stesso messaggio. Se la chiave e i due argomenti del messaggio da
     * inserire sono gia' presenti nel oggetto ActionMessages del request, il
     * messaggio non viene inserito la seconda volta, altrimenti viene inserito
     */
    ActionMessages errorsInTheRequest = this.getMessages(request);
    @SuppressWarnings("unchecked")
    Iterator<ActionMessage> iter = errorsInTheRequest.get(tipoMessaggio);
    boolean isMessaggioPresente = false;
    while (iter.hasNext() && !isMessaggioPresente) {
      ActionMessage message = iter.next();
      if (message.getKey().equals(chiave)
          && ((String) message.getValues()[0]).equals(argomento1)
          && ((String) message.getValues()[1]).equals(argomento2))
        isMessaggioPresente = true;
    }
    if (!isMessaggioPresente) {
      ActionMessages errors = new ActionMessages();
      errors.add(tipoMessaggio, new ActionMessage(chiave, argomento1,
          argomento2));
      if (!errors.isEmpty()) this.addMessages(request, errors);
    }
  }

  /**
   * Crea un ActionMessages nel request, popolato con il messaggio individuato
   * dalla chiave in input e con tre argomenti. Se la chiave e i tre argomenti
   * del messaggio da inserire sono gia' presenti il messaggio non viene
   * inserito la seconda volta
   *
   * @param request
   *        request http
   * @param chiave
   *        chiave del messaggio da inserire
   * @param argomento1
   *        argomento 1
   * @param argomento2
   *        argomento 2
   * @param argomento3
   *        argomento 3
   */
  @SuppressWarnings("unchecked")
  protected void aggiungiMessaggio(HttpServletRequest request, String chiave,
      String argomento1, String argomento2, String argomento3) {
    String tipoMessaggio = ActionBaseNoSessione.getTipoMessaggioFromChiave(chiave);

    /*
     * L.G. 07/06/2007: modifica per evitare di inserire più di una volta lo
     * stesso messaggio. Se la chiave e i ree argomenti del messaggio da
     * inserire sono gia' presenti nel oggetto ActionMessages del request, il
     * messaggio non viene inserito la seconda volta, altrimenti viene inserito
     */
    ActionMessages errorsInTheRequest = this.getMessages(request);
    Iterator<ActionMessage> iter = errorsInTheRequest.get(tipoMessaggio);
    boolean isMessaggioPresente = false;
    while (iter.hasNext() && !isMessaggioPresente) {
      ActionMessage message = iter.next();
      if (message.getKey().equals(chiave)
          && ((String) message.getValues()[0]).equals(argomento1)
          && ((String) message.getValues()[1]).equals(argomento2)
          && ((String) message.getValues()[2]).equals(argomento3))
        isMessaggioPresente = true;
    }
    if (!isMessaggioPresente) {
      ActionMessages errors = new ActionMessages();
      errors.add(tipoMessaggio, new ActionMessage(chiave, argomento1,
          argomento2, argomento3));
      if (!errors.isEmpty()) this.addMessages(request, errors);
    }
  }



  /**
   * Ritorna la prima parte della chiave, fino al primo punto. Tale stringa
   * individua la tipologia di messaggio "errors", "warning", "info"
   *
   * @param chiave
   *        chiave da cui estrarre il tipo messaggio
   * @return tipo di messaggio
   */
  public static String getTipoMessaggioFromChiave(String chiave) {
    int posPrimoPuntoChiave = chiave.indexOf(".");
    String prefisso = null;
    if (posPrimoPuntoChiave != -1)
      prefisso = chiave.substring(0, posPrimoPuntoChiave);

    String tipoMessaggio = MESSAGGIO_ERRORE;
    if (PREFISSO_CHIAVE_WARNING.equals(prefisso))
      tipoMessaggio = MESSAGGIO_SEGNALAZIONE;
    if (PREFISSO_CHIAVE_INFO.equals(prefisso)) tipoMessaggio = MESSAGGIO_INFO;
    return tipoMessaggio;
  }

  public void publicSaveMessages(HttpServletRequest request,
      ActionMessages errors) {
    this.addMessages(request, errors);
  }

}