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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

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
 * @author Stefano.Sabbadin
 */
public abstract class ActionBase extends Action implements ActionInterface {

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
  static Logger               logger                  = Logger.getLogger(ActionBase.class);

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  public ResourceBundle       resBundleGenerale       = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /**
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
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

    if (!this.verificaSessionePresente(request)) {
      target = CostantiGeneraliStruts.FORWARD_SESSION_TIMEOUT;
      messageKey = "errors.session.timeOut";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }

    if (target == null && !this.verificaAcquistoOpzione(request)) {
      // in caso non sia stata acquistata l'opzione si introduce un
      // messaggio d'errore nel request e si termina l'elaborazione
      // redirezionando l'utente alla pagina principale dell'applicazione
      // con il messaggio d'errore stesso
      target = CostantiGeneraliStruts.FORWARD_OPZIONE_NON_DISPONIBILE;
      messageKey = "errors.opzione.noDisponibilita";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }

    if (target == null && !this.verificaAbilitazioneOpzione(request)) {
      // in caso non sia stata abilitata l'opzione si introduce un
      // messaggio d'errore nel request e si termina l'elaborazione
      // redirezionando l'utente alla pagina principale dell'applicazione con il
      // messaggio d'errore stesso
      target = CostantiGeneraliStruts.FORWARD_OPZIONE_NON_ABILITATA;
      messageKey = "errors.opzione.noAbilitazione";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }

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
  private boolean verificaSessionePresente(HttpServletRequest request) {
    boolean esito = false;

    HttpSession session = request.getSession();
    String sentinella = (String) session.getAttribute(CostantiGenerali.SENTINELLA_SESSION_TIMEOUT);
    if (sentinella != null) esito = true;

    return esito;
  }

  /**
   * Verifica che l'applicazione Web sia stata acquistata con il pacchetto
   * necessario per l'abilitazione della funzionalità a cui appartiene l'azione
   * richiesta.
   *
   * @param request
   *        request http
   *
   * @return true se l'opzione richiesta è presente tra quelle disponibili per
   *         l'applicazione, false altrimenti
   */
  private boolean verificaAcquistoOpzione(HttpServletRequest request) {
    boolean esito = false;

    ServletContext context = request.getSession().getServletContext();
    Collection<String> opzioni = Arrays.asList((String[]) context.getAttribute(CostantiGenerali.OPZIONI_DISPONIBILI));

    if (opzioni != null && this.getOpzioneAcquistata() != null) {
      esito = opzioni.contains(this.getOpzioneAcquistata());
    }

    return esito;
  }

  /**
   * Verifica che l'utente richiedente l'azione appartenente ad una determinata
   * funzionalità sia tra quelle a lui abilitate.
   *
   * @param request
   *        request http
   *
   * @return true se l'opzione richiesta è presente tra quelle disponibili per
   *         l'applicazione, false altrimenti
   */
  protected boolean verificaAbilitazioneOpzione(HttpServletRequest request) {
    HttpSession session = request.getSession();
    ProfiloUtente account = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    // carico le opzioni per cui è abilitato l'utente loggato
    OpzioniUtente opzioniUtente = new OpzioniUtente(
        account.getFunzioniUtenteAbilitate());
    // F.D. 13/03/07
    // tramite la funzione astratta getOpzioniRunAction mi carico le opzioni
    // che permettono l'abilitazione all'esecuzione della action
    // eseguo il metodo getOpzioniRunAction ottenendo una stringa con le
    // abilitazioni che controllerò con il profilo utente per dare accesso alla
    // action all'utente loggato
    CheckOpzioniUtente opzioniPerAbilitazione = this.getOpzioniRunAction();

    // restituisco il test effettuato per le opzioniPerAbilitazione (opzioni
    // prese dalla action
    // per verificare le abilitazioni) sulle opzioniUtente dell'utente loggato
    return opzioniPerAbilitazione.test(opzioniUtente);
  }

  /**
   * @return ritorna l'opzione alla funzionalità acquistata dal cliente e
   *         necessaria a qualsiasi utente per poter eseguire l'azione
   */
  protected abstract String getOpzioneAcquistata();

  /**
   * @return ritorna le opzioni minime per accedere alla action
   */
  protected abstract CheckOpzioniUtente getOpzioniRunAction();

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
    String tipoMessaggio = ActionBase.getTipoMessaggioFromChiave(chiave);
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
    String tipoMessaggio = ActionBase.getTipoMessaggioFromChiave(chiave);
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
    String tipoMessaggio = ActionBase.getTipoMessaggioFromChiave(chiave);
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
    String tipoMessaggio = ActionBase.getTipoMessaggioFromChiave(chiave);

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
   * Crea un ActionMessages in sessione, popolato con il messaggio individuato
   * dalla chiave in input e con gli argomenti. Se la chiave e gli argomenti del
   * messaggio da inserire sono gia' presenti il messaggio non viene inserito
   * una seconda volta.<br>
   * Questo metodo statico va usato per parcheggiare i messaggi in sessione per
   * poter riprendere gli stessi e inserirli nell'oggetto previsto da Struts nel
   * request nel qual caso si debba chiamare una JSP che effettua un submit
   * immediato all'onload generando un nuovo request HTTP che non consentirebbe
   * il passaggio dei messaggi
   *
   * @param request
   *        request http
   * @param chiave
   *        chiave del messaggio da inserire
   * @param argomenti
   *        array di argomenti
   */
  public static void aggiungiMessaggioInSessione(HttpServletRequest request,
      String chiave, String[] argomenti) {
    // reperimento contenitore in sessione
    ActionMessages messagesSessione = (ActionMessages) request.getSession().getAttribute(
        CostantiGeneraliStruts.MESSAGGI_ERRORE_ACTION);
    if (messagesSessione == null) {
      messagesSessione = new ActionMessages();
      request.getSession().setAttribute(
          CostantiGeneraliStruts.MESSAGGI_ERRORE_ACTION, messagesSessione);
    }

    String tipoMessaggio = ActionBase.getTipoMessaggioFromChiave(chiave);

    int numArgomenti = 0;
    if (argomenti != null) numArgomenti = argomenti.length;

    /*
     * Se la chiave e gli argomenti del messaggio da inserire sono gia' presenti
     * nel oggetto ActionMessages del request, il messaggio non viene inserito
     * una seconda volta
     */
    @SuppressWarnings("unchecked")
    Iterator<ActionMessage> iter = messagesSessione.get(tipoMessaggio);
    boolean isMessaggioPresente = false;
    while (iter.hasNext() && !isMessaggioPresente) {
      ActionMessage message = iter.next();
      if (message.getKey().equals(chiave)
          && (numArgomenti == 0
              || (numArgomenti == 1 && ((String) message.getValues()[0]).equals(argomenti[0]))
              || (numArgomenti == 2
                  && ((String) message.getValues()[0]).equals(argomenti[0]) && ((String) message.getValues()[1]).equals(argomenti[1])) || (numArgomenti == 3
              && ((String) message.getValues()[0]).equals(argomenti[0])
              && ((String) message.getValues()[1]).equals(argomenti[1]) && ((String) message.getValues()[2]).equals(argomenti[2])))) {
        isMessaggioPresente = true;
      }
    }
    if (!isMessaggioPresente) {
      switch (numArgomenti) {
      case 0:
        messagesSessione.add(tipoMessaggio, new ActionMessage(chiave));
        break;
      case 1:
        messagesSessione.add(tipoMessaggio, new ActionMessage(chiave,
            argomenti[0]));
        break;
      case 2:
        messagesSessione.add(tipoMessaggio, new ActionMessage(chiave,
            argomenti[0], argomenti[1]));
        break;
      case 3:
        messagesSessione.add(tipoMessaggio, new ActionMessage(chiave,
            argomenti[0], argomenti[1], argomenti[2]));
      }
    }
  }

  /**
   * Sposta gli eventuali messaggi inseriti dal contenitore in sessione al
   * request ed elimina il contenitore dalla sessione stessa
   *
   * @param request
   *        request HTTP
   */
  public void spostaMessaggiDallaSessione(HttpServletRequest request) {
    // reperimento contenitore in sessione
    ActionMessages messagesSessione = (ActionMessages) request.getSession().getAttribute(
        CostantiGeneraliStruts.MESSAGGI_ERRORE_ACTION);
    if (messagesSessione != null) {
      // se il contenitore non è vuoto, aggiungo i messaggi nel request e quindi
      // rimuovo l'oggetto dalla sessione
      if (!messagesSessione.isEmpty())
        this.addMessages(request, messagesSessione);
      request.getSession().removeAttribute(
          CostantiGeneraliStruts.MESSAGGI_ERRORE_ACTION);
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

  /**
   * Pulisce la sessione dagli oggetti che risultano inutili da ora in poi
   * nell'esecuzione. Se eliminano sostanzialmente gli oggetti di dettaglio
   * memorizzati in sessione, in modo da non ritrovarsi una sessione ricca di
   * oggetti usati occasionalmente. Nel momento in cui serviranno, basterà
   * ricreare tali oggetti e riposizionarli in sessione.<br>
   * Si consiglia di utilizzare il presente metodo all'ingresso di ogni
   * funzionalità, in modo da ripulire la sessione dai dettagli definiti in
   * precedenza con le altre funzionalità (o meglio, per induzione, rimuovere il
   * dettaglio definito con l'ultima funzionalità utilizzata).
   *
   * @param request
   *        request http
   */
  protected void cleanSession(HttpServletRequest request) {
    HttpSession session = request.getSession();
    @SuppressWarnings("unchecked")
    Enumeration<String> enumNomi = session.getAttributeNames();
    while (enumNomi.hasMoreElements()) {
      String element = enumNomi.nextElement();
      if (element.startsWith(CostantiGenerali.PREFISSO_OGGETTO_DETTAGLIO)) {
        session.removeAttribute(element);
      }
    }
  }

  public void publicSaveMessages(HttpServletRequest request,
      ActionMessages errors) {
    this.addMessages(request, errors);
  }

  /**
   * Genera il target di errore se verifica che la presente richiesta è
   * associata ad una gestione dei gruppi disabilitata
   *
   * @param request
   * @return true se l'elaborazione va bloccata, false altrimenti
   */
  protected boolean bloccaGestioneGruppiDisabilitata(HttpServletRequest request) {
    return this.bloccaGestioneGruppiDisabilitata(request, true, true);
  }

  /**
   * Genera il target di errore se verifica che la presente richiesta è
   * associata ad una gestione dei gruppi disabilitata
   *
   * @param request
   * @param log
   *        true se si richiede la stampa su log, false altrimenti
   * @param setMsg
   *        true se si richiede il set nel request di un messaggio da inviare
   *        all'utente nella pagina
   * @return true se l'elaborazione va bloccata, false altrimenti
   */
  protected boolean bloccaGestioneGruppiDisabilitata(
      HttpServletRequest request, boolean log, boolean setMsg) {
    boolean esito = false;
    if ("1".equals(request.getSession().getServletContext().getAttribute(
        CostantiGenerali.ATTR_GRUPPI_DISABILITATI))) {
      esito = true;
      String messageKey = "errors.applicazione.gruppiDisabilitati";
      if (log) logger.error(this.resBundleGenerale.getString(messageKey));
      if (setMsg) this.aggiungiMessaggio(request, messageKey);
    }
    return esito;
  }

}