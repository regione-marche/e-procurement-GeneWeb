package it.eldasoft.gene.web.struts.tags;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.sql.sqlparser.JdbcExpression;
import it.eldasoft.gene.db.sql.sqlparser.JdbcExpressionWhere;
import it.eldasoft.gene.db.sql.sqlparser.JdbcSqlSelect;
import it.eldasoft.gene.db.sql.sqlparser.JdbcUtils;
import it.eldasoft.gene.db.sql.sqlparser.JdbcWhere;
import it.eldasoft.gene.tags.decorators.trova.CampoTrovaTag;
import it.eldasoft.gene.tags.decorators.trova.FormTrovaTag;
import it.eldasoft.gene.tags.decorators.trova.gestori.AbstractGestoreTrova;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.utility.UtilityStringhe;

public class TrovaAction extends DispatchActionBaseNoOpzioni {

  // ************************************************************
  // Storia Modifiche:
  // Data Utente Descrizione
  // 18/10/2006 M.F. Gestione del salvataggio dei dati nella session
  // 14/11/2006 M.F. Aggiunta del salvataggio delle condizioni delle form di
  // trova
  // ************************************************************

  private static Logger      logger                    = Logger.getLogger(TrovaAction.class);

  public static final String SESSION_PENDICE_TROVA     = "trova";

  /**
   * Funzione che restituisce le opzioni per accedere alla action trova
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniTrova() {
    return new CheckOpzioniUtente("");
  }

  /**
   * Esegue il trova e ridireziona sulla lista
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward trova(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // ///////////////////////////////////////////////////////////////
    // Modifiche
    // ----------------
    // 21.11.2007: M.F. Aggiunta del gestore per la form di trova
    // ////////////////////////////////////////////////////////////// /

    String messageKey;
    String target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;

    if (logger.isDebugEnabled()) logger.debug("trova: inizio");
    try {

      AbstractGestoreTrova gestore = null;
      JdbcWhere where = new JdbcWhere();
      int count;
      // Estraggo tutti i dati con i parametri
      if (UtilityStruts.getParametroString(request, FormTrovaTag.CAMPO_COUNT) == null) {
        throw new Exception(
            "Nella richiesta non c'� il campo con il conteggio dei campi");
      }
      if (UtilityStruts.getParametroString(request, FormTrovaTag.GESTORE_TROVA) != null) {
        String classGestore = UtilityStruts.getParametroString(request,
            FormTrovaTag.GESTORE_TROVA);
        Object obj = null;
        try {
          // get String Class
          Class cl = Class.forName(classGestore);
          // get the constructor
          java.lang.reflect.Constructor constructor = cl.getConstructor(new Class[] { HttpServletRequest.class, String.class });
          // create an instance
          obj = constructor.newInstance(new Object[] { request, UtilityStruts.getParametroString(request, FormTrovaTag.CAMPO_ENTITA) });

        } catch (Exception e) {
          throw new GestoreException(
              "Errore nella creazione del gestore per trova: "
                  + classGestore
                  + " !", "erroreGestoreTrova", e);
        }
        if (obj instanceof AbstractGestoreTrova) {
          gestore = (AbstractGestoreTrova) obj;
        } else {
          throw new GestoreException("Errore "
              + classGestore
              + " non � ereditato da AbstractGestoreTrova !",
              "erroreGestoreTrova");
        }
      }

      // SQL Injection prevention: si spostano in sessione i campi "filtro" e tutte le where dai campi contenuti, in precedenza definiti
      // come input hidden della form di ricerca
      int popupLevel = UtilityStruts.getNumeroPopUp(request);
      if (gestore != null) { 
    	  gestore.trova();
    	  popupLevel = gestore.getPopUpLevel();
      }
      
      final String originalFilter = UtilityTags.getAttributeForSqlBuild(request.getSession(), UtilityStruts.getParametroString(request,
          FormTrovaTag.CAMPO_ENTITA), popupLevel, FormTrovaTag.CAMPO_ORIGINAL_FILTER);
      String filtro = UtilityTags.getAttributeForSqlBuild(request.getSession(), UtilityStruts.getParametroString(request,
          FormTrovaTag.CAMPO_ENTITA), popupLevel, FormTrovaTag.CAMPO_FILTRO);

        if (StringUtils.isBlank(filtro)) {
            filtro = originalFilter;
        } else if (StringUtils.isNotBlank(originalFilter)) {
            filtro += " AND " + originalFilter;
        }

      // Aggiungo alla where il filtro se � stato settato
      if (StringUtils.isNotBlank(filtro)) {
        // Aggiungo il filtro impostato dell'utente
        where.append(new JdbcExpressionWhere(true));
        where.append(new JdbcExpression(filtro));
        where.append(new JdbcExpressionWhere(false));
        // Dal momento che, una volta consumato il filtro e messo in sessione, potrei aver bisogno di ricostruire un nuovo filtro,
        // pulisco il filtro appena consumato. Questo perch� nei gestori di ricerca, nel metodo utilizzato per costruire il filtro,
        // viene passato il cosidetto "originalFilter", ovvero il filtro costruito PRIMA di richiamare il gestore. Quest'ultimo
        // viene normalmente composto dal normale funzionamento dai campi della form di ricerca ed ha la necessit� di essere
        // preservato.
        UtilityTags.removeAttributeForSqlBuild(request.getSession(), UtilityStruts.getParametroString(request,
        		FormTrovaTag.CAMPO_ENTITA), popupLevel, FormTrovaTag.CAMPO_FILTRO);
      }
      // Aggiungo il flag di case sensitive
      String caseSensitive = UtilityStruts.getParametroString(request,
          FormTrovaTag.CAMPO_CASESENSITIVE);
      boolean lbCaseSensitive = !(caseSensitive != null && caseSensitive.equals("on"));

      // Estraggo il conteggio dei campi
      count = Integer.parseInt(UtilityStruts.getParametroString(request,
          FormTrovaTag.CAMPO_COUNT));
      HashMap trovaSession = new HashMap();
      // hash nella quale memorizzare le select per verificare condizioni su
      // tabelle diverse dall'entit� principale mediante exists
      HashMap filtriAltreTabelle = new HashMap();
      trovaSession.put(FormTrovaTag.CAMPO_COUNT, new Integer(count));
      for (int i = 0; i < count; i++) {
        String nomeCampo = CampoTrovaTag.INIZIO_NOME_CAMPO + i;
        String nomeCampoDa = CampoTrovaTag.INIZIO_NOME_CAMPO + i + "Da";
        CampoTrovaTag.addExpressionToWhere(request, nomeCampo, where,
            lbCaseSensitive, filtriAltreTabelle);
        trovaSession.put(nomeCampo, UtilityStruts.getParametroString(request,
            nomeCampo));
        trovaSession.put(nomeCampo + "_conf",
            UtilityStruts.getParametroString(request, nomeCampo + "_conf"));

        if(UtilityStruts.getParametroString(request, nomeCampoDa) != null){
          CampoTrovaTag.addExpressionToWhere(request, nomeCampoDa, where,
              lbCaseSensitive, filtriAltreTabelle);
          trovaSession.put(nomeCampoDa, UtilityStruts.getParametroString(request,
              nomeCampoDa));
        }
      }
      // terminato il ciclo di generazione delle condizioni di filtro, si
      // controlla l'esistenza di eventuali filtri su entit� esterne e si
      // aggiungono in coda alla where
      //JdbcFrom from = new JdbcFrom();

      for (Iterator iterator = filtriAltreTabelle.keySet().iterator(); iterator.hasNext();) {
        String chiave = (String) iterator.next();
        JdbcSqlSelect selectTabella = (JdbcSqlSelect) filtriAltreTabelle.get(chiave);
        where.append(new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
        where.append(new JdbcExpression("exists",
            new JdbcExpression[] { new JdbcExpression(selectTabella) }));
        if (StringUtils.isNotBlank(selectTabella.getFrom().toString(false))) {
        	if (where.getFrom() == null) {
        		where.setFrom(selectTabella.getFrom());
        	} else {
        		where.getFrom().append(selectTabella.getFrom());
        	}
        }
        //where.append(selectTabella.getWhere());
        //from.append(selectTabella.getFrom());
      }

      trovaSession.put(FormTrovaTag.CAMPO_CASESENSITIVE, lbCaseSensitive
          ? "1" : "2");

      String righePerPagina = UtilityStruts.getParametroString(
          request, FormTrovaTag.CAMPO_RISULTATI_PER_PAGINA);
      trovaSession.put(FormTrovaTag.CAMPO_RISULTATI_PER_PAGINA, righePerPagina);

      String visualizzazioneAvanzata = UtilityStruts.getParametroString(
    	  request, FormTrovaTag.CAMPO_VISUALIZZAZIONE_AVANZATA);
      trovaSession.put(FormTrovaTag.CAMPO_VISUALIZZAZIONE_AVANZATA,visualizzazioneAvanzata);

      // Aggiungo nel session l'oggetto con i dati di filtro
      request.getSession().setAttribute(
          SESSION_PENDICE_TROVA
              + UtilityStruts.getParametroString(request, FormTrovaTag.CAMPO_ENTITA), trovaSession);
      

      // SQL Injection prevention: si spostano in sessione i dati di filtro in modo da rimuovere l'inserimento di campi hidden nella form di
      // lista
      if (StringUtils.isNotBlank(where.toString(false))) {
	      UtilityTags.putAttributeForSqlBuild(request.getSession(), UtilityStruts.getParametroString(request, FormTrovaTag.CAMPO_ENTITA),
	          popupLevel, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where.toString(false), true);
      } else {
    	  UtilityTags.removeAttributeForSqlBuild(request.getSession(), UtilityStruts.getParametroString(request, FormTrovaTag.CAMPO_ENTITA), 
    			  popupLevel, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
      }

      // Dal momento in cui, durante la costruzione di filtri consecutivi, c'� la possibilit� che i parametri passati alle query
      // non vengano ripuliti o gestiti correttamente, si eliminano i parametri presenti nella mappa attuale. Questa azione, 
      // tuttavia, viene eseguita solo se nei prepare statement sono presenti delle wildcard "?". Facendo ci�, non � garantita
      // la corretta estrazione dei dati cercati, in quanto questa deve essere gestita dalla persona che implementa il gestore di
      // ricerca, ma quantomeno non vengono lanciate eccezioni bloccanti.
      if (!where.toString(false).contains("?")) {
    	  // verifica formale: se non ci sono parametri nella query non ha senso tenere i parametri
    	  UtilityTags.removeAttributeForSqlBuild(request.getSession(), UtilityStruts.getParametroString(request, 
    			  FormTrovaTag.CAMPO_ENTITA), popupLevel, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA);
      }
      
      if (where.getParametri().size() > 0) {
        UtilityTags.putAttributeForSqlBuild(request.getSession(), UtilityStruts.getParametroString(request, FormTrovaTag.CAMPO_ENTITA),
            popupLevel, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, UtilityTags.parametriToString(where.getParametri()));
      }

      // Aggiungo nel request i dati
      String pathLista = UtilityStruts.getParametroString(request,
          UtilityTags.DEFAULT_HIDDEN_FORM_TO_JSP);
      if (pathLista == null) {
        pathLista = UtilityTags.getPathFromEntita(UtilityStruts.getParametroString(
            request, FormTrovaTag.CAMPO_ENTITA))
            + "lista.jsp";
      }

      if (!UtilityStruts.isValidJspPath(pathLista)) {
        // non deve essere possibile redirezionare ad una risorsa non jsp e nemmeno risalire sopra WEB-INF/pages
        messageKey = "errors.url.notWellFormed";
        String messageError = this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
            pathLista);
        logger.error(messageError);
        this.aggiungiMessaggio(request, messageKey, pathLista);
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      } else {
        request.setAttribute(UtilityStruts.DATI_PATH_FILE, pathLista);
        // Ridireziono al path delle lista
        return UtilityStruts.redirectToPage(pathLista, false, request);
      }

    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) logger.debug("trova: fine");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action nuova
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniNuova() {
    return new CheckOpzioniUtente("");
  }

  /**
   * Sbianca tutti i campi della ricerca
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward nuova(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("nuova: inizio");
    UtilityTags.debugRequest(request, logger);
    // Aggiungo nel session l'oggetto con i dati di filtro
    request.getSession().removeAttribute(
        SESSION_PENDICE_TROVA
            + UtilityStruts.getParametroString(request,
                FormTrovaTag.CAMPO_ENTITA));
    if (logger.isDebugEnabled()) logger.debug("nuova: fine");
    return UtilityStruts.redirectToSamePage(request);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action clear
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniClear() {
    return new CheckOpzioniUtente("");
  }

  /**
   * Annulla le modifiche effettuate dall'utente
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward clear(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    return UtilityStruts.redirectToSamePage(request);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action nuovo
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniNuovo() {
    return new CheckOpzioniUtente("");
  }

  /**
   * Chiama l'eventuale creazione dell'oggetto riferito
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward nuovo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("Eseguo eseguo la ridirezione alla creazione della scheda");
    try {
      return UtilityStruts.redirectToPage("/Scheda.do", true, request);
    } catch (Throwable t) {
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }

}
