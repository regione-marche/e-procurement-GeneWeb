/*
 * Created on 22-apr-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags;

import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.decorators.wizard.CostantiWizard;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

/**
 * Classe che gestisce le interazioni con le pagine di wizard
 *
 * @author stefano.sabbadin
 */
public class WizardAction extends DispatchActionBaseNoOpzioni {

  /** Logger della classe */
  private static Logger logger = Logger.getLogger(WizardAction.class);

  /**
   * Esegue l'operazione di annullamento della creazione guidata con ritorno
   * alla pagina precedentemente visualizzata prima dell'accesso al wizard
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward annulla(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      // si elimina dalla sessione l'oggetto Wizard prima di tornare alla pagina
      // precedentemente visualizzata
      request.getSession().removeAttribute(
          CostantiWizard.NOME_OGGETTO_WIZARD_SESSIONE);
      return UtilityTags.getUtilityHistory(request.getSession()).back(request);
    } catch (Throwable t) {
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }

  /**
   * Esegue l'operazione di annullamento dell'inserimento dei dati nella pagina
   * visualizzata per tornare alla pagina precedentemente visualizzata
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward indietro(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // FASE 1: set della navigazione

    String tipoPagina = UtilityStruts.getParametroString(request,
        CostantiWizard.HIDDEN_PARAMETRO_TIPO_PAGINA);
    int activePage = Integer.parseInt(UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE));
    int activeSubPage = Integer.parseInt(UtilityStruts.getParametroString(
        request, CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA));

    if (CostantiWizard.TIPO_PAGINA_DETTAGLIO.equalsIgnoreCase(tipoPagina)) {
      // nel caso di indietro da una pagina di dettaglio, si va alla pagina
      // precedente
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE,
          Integer.toString(--activePage));
      request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA,
          new Integer(0));
    } else if (CostantiWizard.TIPO_PAGINA_LISTA_DOMANDA.equalsIgnoreCase(tipoPagina)) {
      // nel caso di indietro da una pagina di domanda inserimento in una lista,
      // si va alla pagina precedente
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE,
          Integer.toString(--activePage));
      request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA,
          new Integer(0));
    } else if (CostantiWizard.TIPO_PAGINA_LISTA_NUOVO.equalsIgnoreCase(tipoPagina)) {
      // nel caso di indietro da una pagina di inserimento elemento nella lista,
      // si va alla pagina di domanda
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE,
          Integer.toString(activePage));
      // request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_TIPO_PAGINA,
      // CostantiWizard.TIPO_PAGINA_LISTA_DOMANDA);
      request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA,
          new Integer(--activeSubPage));
    }

    // FASE 2: lancio di eventuali personalizzazioni

    AbstractCustomWizardAction gestoreNavigazione = this.getGestoreNavigazione(request);
    if (gestoreNavigazione != null) {
      ActionForward forward = gestoreNavigazione.indietro(mapping, form,
          request, response);
      if (forward != null) return forward;
    }

    // FASE 3: chiamata al caricamento della prossima pagina di visualizzare

    try {
      String jsp = UtilityStruts.getParametroString(request,
          UtilityTags.DEFAULT_HIDDEN_FORM_JSP_PATH);

      // Se in precedenza e' stato inserito nella sessione l'attributo
      // Globals.MESSAGE_KEY (a cui e' associato l'oggetto ActionMessages)
      // lo inserisco nel request come attributo con la stessa chiave e lo
      // rimuovo dalla sessione per portarlo così alla pagina di destinazione
      if (request.getSession().getAttribute(Globals.MESSAGE_KEY) != null
          && !((ActionMessages) request.getSession().getAttribute(
              Globals.MESSAGE_KEY)).isEmpty()) {
        request.setAttribute(Globals.MESSAGE_KEY,
            request.getSession().getAttribute(Globals.MESSAGE_KEY));
        request.getSession().removeAttribute(Globals.MESSAGE_KEY);
      }

      if (jsp != null && !UtilityStruts.isValidJspPath(jsp)) {
        // non deve essere possibile redirezionare ad una risorsa non jsp e nemmeno risalire sopra WEB-INF/pages
        String messageKey = "errors.url.notWellFormed";
        String messageError = this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
            jsp);
        logger.error(messageError);
        this.aggiungiMessaggio(request, messageKey, jsp);
        return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
      }

      // si riapre il dettaglio del wizard
      return UtilityStruts.redirectToPage(jsp, jsp != null
          && jsp.indexOf("/WEB-INF/") == 0, request);
    } catch (Throwable t) {
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }

  /**
   * Esegue l'operazione di conferma dell'inserimento dei dati nella pagina
   * visualizzata per andare alla pagina successiva
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward avanti(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // FASE 1: set della navigazione

    String tipoPagina = UtilityStruts.getParametroString(request,
        CostantiWizard.HIDDEN_PARAMETRO_TIPO_PAGINA);
    int activePage = Integer.parseInt(UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE));
    int activeSubPage = Integer.parseInt(UtilityStruts.getParametroString(
        request, CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA));

    if (CostantiWizard.TIPO_PAGINA_DETTAGLIO.equalsIgnoreCase(tipoPagina)) {
      // nel caso di avanti da una pagina di dettaglio, si va alla pagina
      // successiva
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE,
          Integer.toString(++activePage));
      request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA,
          new Integer(0));
    } else if (CostantiWizard.TIPO_PAGINA_LISTA_DOMANDA.equalsIgnoreCase(tipoPagina)) {
      // nel caso di avanti da una pagina di domanda inserimento in una lista,
      // si va alla pagina successiva se non si desidera effettuare alcun
      // inserimento, e si va alla pagina di inserimento se lo si intende fare
      String nomeDiscriminante = request.getParameter(CostantiWizard.HIDDEN_CAMPO_DISCRIMINANTE_DOMANDE);
      int inserisciNuovoElemento = 0;
      try {
        inserisciNuovoElemento = Integer.parseInt(request.getParameter(nomeDiscriminante));
      } catch (Throwable t) {
        return GestoreEccezioni.gestisciEccezioneAction(t, this, request,
            logger, mapping);
      }

      if (inserisciNuovoElemento == 1) {
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE,
            Integer.toString(activePage));
        // request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_TIPO_PAGINA,
        // CostantiWizard.TIPO_PAGINA_LISTA_NUOVO);
        request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA,
            new Integer(++activeSubPage));
      } else {
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE,
            Integer.toString(++activePage));
        request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA,
            new Integer(0));
      }
    } else if (CostantiWizard.TIPO_PAGINA_LISTA_NUOVO.equalsIgnoreCase(tipoPagina)) {
      // nel caso di avanti da una pagina di inserimento elemento nella lista,
      // si va alla pagina di domanda
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE,
          Integer.toString(activePage));
      // request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_TIPO_PAGINA,
      // CostantiWizard.TIPO_PAGINA_LISTA_DOMANDA);
      request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA,
          new Integer(--activeSubPage));
    }

    // FASE 2: trasferimento dei dati della pagina nel contenitore in sessione

    this.setDatiInSessione(request);

    // FASE 3: lancio di eventuali personalizzazioni

    AbstractCustomWizardAction gestoreNavigazione = this.getGestoreNavigazione(request);
    if (gestoreNavigazione != null) {
      ActionForward forward = gestoreNavigazione.avanti(mapping, form, request,
          response);
      if (forward != null) return forward;
    }

    // FASE 4: chiamata al caricamento della prossima pagina di visualizzare

    try {
      String jsp = UtilityStruts.getParametroString(request,
          UtilityTags.DEFAULT_HIDDEN_FORM_JSP_PATH);

      // Se in precedenza e' stato inserito un messaggio di errore/warning/info
      // lo reinserisco nel request come attributo, per portarlo così alla
      // pagina di destinazione
      if (request.getParameter(Globals.MESSAGE_KEY) != null) {
        int indice = UtilityNumeri.convertiIntero(
            request.getParameter(Globals.MESSAGE_KEY)).intValue();

        for (int i = 0; i < indice; i++) {
          String[] messaggio = request.getParameter(Globals.MESSAGE_KEY + i).split(
              "_;_;_");
          switch (messaggio.length) {
          case 1:
            this.aggiungiMessaggio(request, messaggio[0]);
            break;
          case 2:
            this.aggiungiMessaggio(request, messaggio[0], messaggio[1]);
            break;
          case 3:
            this.aggiungiMessaggio(request, messaggio[0], messaggio[1],
                messaggio[2]);
            break;
          case 4:
            this.aggiungiMessaggio(request, messaggio[0], messaggio[1],
                messaggio[2], messaggio[3]);
            break;
          }
        }
      }

      if (jsp != null && !UtilityStruts.isValidJspPath(jsp)) {
        // non deve essere possibile redirezionare ad una risorsa non jsp e nemmeno risalire sopra WEB-INF/pages
        String messageKey = "errors.url.notWellFormed";
        String messageError = this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
            jsp);
        logger.error(messageError);
        this.aggiungiMessaggio(request, messageKey, jsp);
        return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
      }

      // si riapre il dettaglio del wizard
      return UtilityStruts.redirectToPage(jsp, jsp != null
          && jsp.indexOf("/WEB-INF/") == 0, request);
    } catch (Throwable t) {
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }

  /**
   * Esegue l'operazione di conferma dell'inserimento dei dati nella pagina
   * visualizzata per andare alla pagina finale
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward fine(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // FASE 1: set della navigazione

    String tipoPagina = UtilityStruts.getParametroString(request,
        CostantiWizard.HIDDEN_PARAMETRO_TIPO_PAGINA);
    int activePage = Integer.parseInt(UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE));
    int activeSubPage = Integer.parseInt(UtilityStruts.getParametroString(
        request, CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA));
    int finalPage = Integer.parseInt(UtilityStruts.getParametroString(request,
        CostantiWizard.HIDDEN_PARAMETRO_NUMERO_PAGINE)) - 1;

    if (CostantiWizard.TIPO_PAGINA_DETTAGLIO.equalsIgnoreCase(tipoPagina)) {
      // nel caso di avanti da una pagina di dettaglio, si va alla pagina
      // finale
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE,
          Integer.toString(finalPage));
      request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA,
          new Integer(0));
    } else if (CostantiWizard.TIPO_PAGINA_LISTA_DOMANDA.equalsIgnoreCase(tipoPagina)) {
      // nel caso di avanti da una pagina di domanda inserimento in una lista,
      // si va alla pagina successiva se non si desidera effettuare alcun
      // inserimento, e si va alla pagina di inserimento se lo si intende fare
      String nomeDiscriminante = request.getParameter(CostantiWizard.HIDDEN_CAMPO_DISCRIMINANTE_DOMANDE);
      int inserisciNuovoElemento = 0;
      try {
        inserisciNuovoElemento = Integer.parseInt(request.getParameter(nomeDiscriminante));
      } catch (Throwable t) {
        return GestoreEccezioni.gestisciEccezioneAction(t, this, request,
            logger, mapping);
      }

      if (inserisciNuovoElemento == 1) {
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE,
            Integer.toString(activePage));
        // request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_TIPO_PAGINA,
        // CostantiWizard.TIPO_PAGINA_LISTA_NUOVO);
        request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA,
            new Integer(++activeSubPage));
      } else {
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE,
            Integer.toString(finalPage));
        request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA,
            new Integer(0));
      }
    } else if (CostantiWizard.TIPO_PAGINA_LISTA_NUOVO.equalsIgnoreCase(tipoPagina)) {
      // nel caso di avanti da una pagina di inserimento elemento nella lista,
      // si va alla pagina finale
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE,
          Integer.toString(finalPage));
      // request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_TIPO_PAGINA,
      // CostantiWizard.TIPO_PAGINA_LISTA_DOMANDA);
      request.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA,
          new Integer(0));
    }

    // FASE 2: trasferimento dei dati della pagina nel contenitore in sessione

    this.setDatiInSessione(request);

    // FASE 3: lancio di eventuali personalizzazioni

    AbstractCustomWizardAction gestoreNavigazione = this.getGestoreNavigazione(request);
    if (gestoreNavigazione != null) {
      ActionForward forward = gestoreNavigazione.fine(mapping, form, request,
          response);
      if (forward != null) return forward;
    }

    // FASE 4: chiamata al caricamento della prossima pagina di visualizzare

    try {
      String jsp = UtilityStruts.getParametroString(request,
          UtilityTags.DEFAULT_HIDDEN_FORM_JSP_PATH);

      // Se in precedenza e' stato inserito un messaggio di errore/warning/info
      // lo reinserisco nel request come attributo, per portarlo così alla
      // pagina di destinazione
      if (request.getParameter(Globals.MESSAGE_KEY) != null) {
        int indice = UtilityNumeri.convertiIntero(
            request.getParameter(Globals.MESSAGE_KEY)).intValue();

        for (int i = 0; i < indice; i++) {
          String[] messaggio = request.getParameter(Globals.MESSAGE_KEY + i).split(
              "_;_;_");
          switch (messaggio.length) {
          case 1:
            this.aggiungiMessaggio(request, messaggio[0]);
            break;
          case 2:
            this.aggiungiMessaggio(request, messaggio[0], messaggio[1]);
            break;
          case 3:
            this.aggiungiMessaggio(request, messaggio[0], messaggio[1],
                messaggio[2]);
            break;
          case 4:
            this.aggiungiMessaggio(request, messaggio[0], messaggio[1],
                messaggio[2], messaggio[3]);
            break;
          }
        }
      }

      if (jsp != null && !UtilityStruts.isValidJspPath(jsp)) {
        // non deve essere possibile redirezionare ad una risorsa non jsp e nemmeno risalire sopra WEB-INF/pages
        String messageKey = "errors.url.notWellFormed";
        String messageError = this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
            jsp);
        logger.error(messageError);
        this.aggiungiMessaggio(request, messageKey, jsp);
        return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
      }

      // si riapre il dettaglio del wizard
      return UtilityStruts.redirectToPage(jsp, jsp != null
          && jsp.indexOf("/WEB-INF/") == 0, request);
    } catch (Throwable t) {
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }

  /**
   * Esegue l'operazione di con salvataggio dei dati inseriti nella creazione
   * guidata e passa alla pagina successiva
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward salva(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // FASE 1: trasferimento dei dati della pagina nel contenitore in sessione
    DataColumnContainer impl = this.setDatiInSessione(request);

    // FASE 2: aggiornamento mediante il gestore

    String entita = request.getParameter(CostantiWizard.HIDDEN_PARAMETRO_ENTITA_PRINCIPALE_WIZARD);
    String hrefFineWizard = request.getParameter(CostantiWizard.HIDDEN_PARAMETRO_PAGINA_FINE_WIZARD);

    try {
      // Estraggo il gestore che implementa le funzionalità sull'entità
      AbstractGestoreEntita gestore = UtilityStruts.getGestoreEntita(entita,
          request, UtilityStruts.getParametroString(request,
              CostantiWizard.HIDDEN_PARAMETRO_GESTORE_SALVATAGGIO_WIZARD));
      gestore.setAction(this);
      // Eseguo l'inserimento passando le colonne
      gestore.inserisci(null, impl);

      // Creo la variabile con l'elenco delle chiavi per l'apertura
      // della maschera in visualizzazione
      DataColumn keys[] = impl.getColumns(entita, 1);
      StringBuffer chiave = new StringBuffer("");
      for (int i = 0; i < keys.length; i++) {
        if (i > 0) chiave.append(";");
        chiave.append(keys[i].toString());
        chiave.append("=");
        chiave.append(keys[i].getValue().toString(true));

      }
      // setto la chiave nel request per l'eventuale apertura del dettaglio
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
          chiave.toString());

      // si aggiungono due attributi nel request in modo da non leggere più, i
      // corrispondenti parametri ricevuti nel request stesso; in questo modo,
      // terminato un wizard (si è posizionati nell'ultima pagina, ovvero il
      // parametro "activePage", e in modalità inserimento, ovvero "modo"
      // valorizzato con NUOVO) si può proseguire con l'apertura dell'eventuale
      // scheda di dettaglio (è l'operazione di default in seguito al termine
      // del wizard)
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE, "0");
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO,
          UtilityTags.SCHEDA_MODO_VISUALIZZA);

      // si rimuove il contenitore dalla sessione
      request.getSession().removeAttribute(
          CostantiWizard.NOME_OGGETTO_WIZARD_SESSIONE);

      // FASE 3: lancio di eventuali personalizzazioni

      AbstractCustomWizardAction gestoreNavigazione = this.getGestoreNavigazione(request);
      if (gestoreNavigazione != null) {
        ActionForward forward = gestoreNavigazione.salva(mapping, form,
            request, response);
        if (forward != null) return forward;
      }

      // FASE 4: chiamata al caricamento della prossima pagina di visualizzare

      // Se in precedenza e' stato inserito un messaggio di errore/warning/info
      // lo reinserisco nel request come attributo, per portarlo così alla
      // pagina di destinazione
      if (request.getParameter(Globals.MESSAGE_KEY) != null) {
        int indice = UtilityNumeri.convertiIntero(
            request.getParameter(Globals.MESSAGE_KEY)).intValue();

        for (int i = 0; i < indice; i++) {
          String[] messaggio = request.getParameter(Globals.MESSAGE_KEY + i).split(
              "_;_;_");
          switch (messaggio.length) {
          case 1:
            this.aggiungiMessaggio(request, messaggio[0]);
            break;
          case 2:
            this.aggiungiMessaggio(request, messaggio[0], messaggio[1]);
            break;
          case 3:
            this.aggiungiMessaggio(request, messaggio[0], messaggio[1],
                messaggio[2]);
            break;
          case 4:
            this.aggiungiMessaggio(request, messaggio[0], messaggio[1],
                messaggio[2], messaggio[3]);
            break;
          }
        }
      }

      if (hrefFineWizard != null && !UtilityStruts.isValidJspPath(hrefFineWizard)) {
        // non deve essere possibile redirezionare ad una risorsa non jsp e nemmeno risalire sopra WEB-INF/pages
        String messageKey = "errors.url.notWellFormed";
        String messageError = this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
            hrefFineWizard);
        logger.error(messageError);
        this.aggiungiMessaggio(request, messageKey, hrefFineWizard);
        return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
      }

      // si va alla pagina indicata nel tag wizard come pagina successiva alla
      // termine del wizard stesso, o alla scheda dell'entità se non è
      // valorizzato il parametro
      return UtilityStruts.redirectToPage(hrefFineWizard,
          hrefFineWizard != null && hrefFineWizard.indexOf("/WEB-INF/") == 0,
          request);

    } catch (Throwable t) {
      // Salvo nel request che c'è stato un errore durante l'update
      request.setAttribute(SchedaAction.ERRORE_NEL_UPDATE, new Boolean(true));
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO,
          UtilityTags.SCHEDA_MODO_INSERIMENTO);
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }

  /**
   * Esegue un'operazione speciale se prevista nella pagina. La navigazione
   * verso una nuova pagina mediante il salvataggio nel request degli attributi
   * relativi alle costanti UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE e
   * CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA deve essere realizzata ad hoc
   * nel metodo extra del gestore richiamato.<br>
   * Attenzione: i dati non vengono salvati in sessione: l'implementazione del
   * metodo nel gestore deve prevedere eventuali popolamenti nel request e
   * salvataggi in sessione
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward extra(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // FASE 1: lancio di eventuali personalizzazioni

    AbstractCustomWizardAction gestoreNavigazione = this.getGestoreNavigazione(request);
    if (gestoreNavigazione != null) {
      ActionForward forward = gestoreNavigazione.extra(mapping, form, request,
          response);
      if (forward != null) return forward;
    }

    // FASE 2: chiamata al caricamento della prossima pagina di visualizzare

    try {
      String jsp = UtilityStruts.getParametroString(request,
          UtilityTags.DEFAULT_HIDDEN_FORM_JSP_PATH);

      // Se in precedenza e' stato inserito un messaggio di errore/warning/info
      // lo reinserisco nel request come attributo, per portarlo così alla
      // pagina di destinazione
      if (request.getParameter(Globals.MESSAGE_KEY) != null) {
        int indice = UtilityNumeri.convertiIntero(
            request.getParameter(Globals.MESSAGE_KEY)).intValue();

        for (int i = 0; i < indice; i++) {
          String[] messaggio = request.getParameter(Globals.MESSAGE_KEY + i).split(
              "_;_;_");
          switch (messaggio.length) {
          case 1:
            this.aggiungiMessaggio(request, messaggio[0]);
            break;
          case 2:
            this.aggiungiMessaggio(request, messaggio[0], messaggio[1]);
            break;
          case 3:
            this.aggiungiMessaggio(request, messaggio[0], messaggio[1],
                messaggio[2]);
            break;
          case 4:
            this.aggiungiMessaggio(request, messaggio[0], messaggio[1],
                messaggio[2], messaggio[3]);
            break;
          }
        }
      }

      if (jsp != null && !UtilityStruts.isValidJspPath(jsp)) {
        // non deve essere possibile redirezionare ad una risorsa non jsp e nemmeno risalire sopra WEB-INF/pages
        String messageKey = "errors.url.notWellFormed";
        String messageError = this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
            jsp);
        logger.error(messageError);
        this.aggiungiMessaggio(request, messageKey, jsp);
        return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
      }

      // si riapre il dettaglio del wizard
      return UtilityStruts.redirectToPage(jsp, jsp != null
          && jsp.indexOf("/WEB-INF/") == 0, request);
    } catch (Throwable t) {
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }

  /**
   * Salva i dati ricevuti dalla form nel contenitore in sessione per il wizard.
   * Se il contenitore non è presente in sessione, lo crea, altrimenti ne
   * aggiorna i dati provenienti dalla pagina
   *
   * @param request
   *        request HTTP
   *
   * @return dati inseriti in sessione della classe DataColumnContainer
   */
  public DataColumnContainer setDatiInSessione(HttpServletRequest request) {
    DataColumn[] datiRequest = UtilityStruts.getDatiRequest(request);

    DataColumnContainer datiSessione = (DataColumnContainer) request.getSession().getAttribute(
        CostantiWizard.NOME_OGGETTO_WIZARD_SESSIONE);

    if (datiSessione == null) {
      // se non esiste in sessione, lo creo con i dati letti dal request
      datiSessione = new DataColumnContainer(datiRequest);
      request.getSession().setAttribute(
          CostantiWizard.NOME_OGGETTO_WIZARD_SESSIONE, datiSessione);
    } else {
      // se invece esiste, allora ne aggiorno i dati letti dal request
      datiSessione.addColumns(datiRequest, true);
    }

    return datiSessione;
  }

  /**
   * Istanzia, se definito, il gestore della navigazione
   *
   * @param request
   *        request HTTP
   * @return oggetto gestore della navigazione, che rispetta l'interfaccia di
   *         ritorno
   */
  private AbstractCustomWizardAction getGestoreNavigazione(
      HttpServletRequest request) {
    String classeGestore = request.getParameter(UtilityTags.DEFAULT_HIDDEN_NOME_GESTORE);
    AbstractCustomWizardAction gestore = null;
    if (classeGestore != null) {
      Object o = null;
      try {
        // si crea il gestore con un argomento valorizzato con la action stessa
        Class cl = Class.forName(classeGestore);
        java.lang.reflect.Constructor constructor = cl.getConstructor(new Class[] { this.getClass() });
        o = constructor.newInstance(new Object[] { this });

      } catch (Exception e) {
        o = null;
      }
      if (o instanceof AbstractCustomWizardAction)
        gestore = (AbstractCustomWizardAction) o;
    }
    return gestore;
  }

}