package it.eldasoft.gene.web.struts.tags;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.decorators.trova.FormTrovaTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.LegameTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

/**
 * Oggetto che gestisce le interazioni con le schede
 *
 * @author marco.franceschin
 *
 */
public class SchedaAction extends DispatchActionBaseNoOpzioni {

  /** Logger della classe */
  private static Logger      logger            = Logger.getLogger(SchedaAction.class);

  /*
   * Costanti di scheda
   */
  public static final String ERRORE_NEL_UPDATE = "schedaErrUpdate";

  /**
   * Apertura della maschera in modalità apertura semplice
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward apri(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    try {
      String entita;
      String outPath;
      String defaultJsp = UtilityStruts.getParametroString(request,
          UtilityTags.DEFAULT_HIDDEN_FORM_TO_JSP);

      if (defaultJsp != null) {
        outPath = (String) request.getAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_TO_JSP);
      } else {
        // Esecuzione dell'apertura della scheda
        entita = UtilityStruts.getParametroString(request,
            FormTrovaTag.CAMPO_ENTITA);
        if (entita == null)
          throw new Exception(
              "Nell'apertura della scheda non è impostata l'entità principale di partenza !");
        //
        outPath = UtilityTags.getPathFromEntita(entita);
        // Verifico se la scheda deve essere aperta in modalità popUp
        if (UtilityStruts.getParametroString(request,
            UtilityTags.DEFAULT_HIDDEN_IS_POPUP) != null
            && UtilityStruts.getParametroString(request,
                UtilityTags.DEFAULT_HIDDEN_IS_POPUP).equals("1"))
          outPath += "scheda-popup.jsp";
        else
          // Ridireziono sulla pagina di default
          outPath += "scheda.jsp";
      }
      // Setto l'apertura in modalità visualizzazione
      request.setAttribute(UtilityStruts.PARAMETRO_MODO_APERTURA, "visualizza");

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

      if (!UtilityStruts.isValidJspPath(outPath)) {
        // non deve essere possibile redirezionare ad una risorsa non jsp e nemmeno risalire sopra WEB-INF/pages
        String messageKey = "errors.url.notWellFormed";
        String messageError = this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
            outPath);
        logger.error(messageError);
        this.aggiungiMessaggio(request, messageKey, outPath);
        return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
      }

      // {MF081106} Verifico se il path è un path assoluto o no
      return UtilityStruts.redirectToPage(outPath, outPath != null
          && outPath.indexOf("/WEB-INF/") == 0, request);
    } catch (Throwable t) {
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }

  }

  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // {MF150207} Aggiunta delle gestione del controllo sulle protezioni
    try {
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO,
          UtilityTags.SCHEDA_MODO_MODIFICA);
      return this.apri(mapping, form, request, response);
    } catch (Throwable t) {
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }

  }

  public ActionForward nuovo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // {MF150207} Aggiunta delle gestione del controllo sulle protezioni
    try {
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO,
          UtilityTags.SCHEDA_MODO_INSERIMENTO);
    } catch (Throwable t) {
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
    return this.apri(mapping, form, request, response);
  }

  /**
   * Esecuzione dell'update su di un entità
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward update(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // Estraggo il modo di update
    String modo = UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO);
    try {
      // Esecuzione dell'apertura della scheda
      String entita = UtilityStruts.getParametroString(request,
          FormTrovaTag.CAMPO_ENTITA);
      if (entita == null)
        throw new Exception(
            "Nell'apertura della scheda non è impostata l'entità principale di partenza");

      // Estraggo il gestore che implementa le funzionalità sull'entità
      AbstractGestoreEntita gestore = UtilityStruts.getGestoreEntita(entita,
          request, UtilityStruts.getParametroString(request,
              UtilityTags.DEFAULT_HIDDEN_NOME_GESTORE));
      gestore.setAction(this);
      gestore.setForm((UploadFileForm)form);

      return this.update(mapping, form, request, response, modo, entita,
          gestore);

    } catch (Throwable t) {
      // Salvo nel request che c'è stato un errore durante l'update
      request.setAttribute(SchedaAction.ERRORE_NEL_UPDATE, new Boolean(true));
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO, modo);
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }

  /**
   * Esegue la vera e propria gestione dell'aggiornamento dei dati
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @param modo
   * @param entita
   * @param gestore
   * @return
   * @throws GestoreException
   * @throws ServletException
   * @throws IOException
   */
  private ActionForward update(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response, String modo,
      String entita, AbstractGestoreEntita gestore) throws GestoreException,
      ServletException, IOException {
    // Estraggo l'elenco dei campi
    String elencoCampi[] = UtilityTags.stringToArray(
        UtilityStruts.getParametroString(request,
            UtilityTags.DEFAULT_HIDDEN_ELENCO_CAMPI), ';');
    Vector campiAdd = new Vector();

    // Scorro tutti i campi
    for (int i = 0; i < elencoCampi.length; i++) {
      DataColumn col = UtilityStruts.getColumnWithValue(request,
          elencoCampi[i]);
      if (col != null) {
        campiAdd.add(col);
      }
    }

    String profiloApplicativo = (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);

    DataColumnContainer impl = new DataColumnContainer(campiAdd);
    if (UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo)) {
      // si verifica che l'eventuale keyParent ricevuto sia uno tra quelli navigati in precedenza e quindi sia un dato di possesso dell'utente
      HashMap<String, HashSet<String>> hashProfiliKeyParents = (HashMap<String, HashSet<String>>) request.getSession().getAttribute(CostantiGenerali.PROFILI_KEY_PARENTS);
      String keyParent = UtilityStruts.getParametroString(request, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT);
      try {
        if (profiloApplicativo != null && StringUtils.isNotBlank(keyParent)) {
          if (!hashProfiliKeyParents.get(profiloApplicativo).contains(keyParent)) {
            throw new GestoreException("Bloccato tentativo di inserimento di un'occorrenza legata ad un record non visibile", "violazioneKeyParent");
          }
          DataColumn[] campiChiaveOriginali = UtilityTags.stringKeysToColumnWithParam(keyParent);
          for (DataColumn campoChiave : campiChiaveOriginali) {
            if (impl.isColumn(campoChiave.getNomeFisico()) && !impl.getColumn(campoChiave.getNomeFisico()).getValue().toString().equals(campoChiave.getValue().toString()))
              // la chiave deve essere presente anche come campi della maschera, e non devono essere stati alterati
              throw new GestoreException("Bloccato tentativo di modifica di un'occorrenza legata ad un record non visibile", "violazioneKeyParent");
          }
        }
      } catch (GestoreException e) {
        return GestoreEccezioni.gestisciEccezioneAction(e, this, request, logger,
            mapping, true);
      }

      // Eseguo l'inserimento passando le colonne
      gestore.inserisci(null, impl);
      // Creo la variabile con l'elenco delle chiavi per l'apertura
      // della maschera in visualizzazione
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
          this.getKey(entita, impl));

    } else if (UtilityTags.SCHEDA_MODO_MODIFICA.equals(modo)) {
      // si verifica che l'eventuale key ricevuto sia uno tra quelli navigati in precedenza e quindi sia un dato di possesso dell'utente
      HashMap<String, HashSet<String>> hashProfiliKeys = (HashMap<String, HashSet<String>>) request.getSession().getAttribute(CostantiGenerali.PROFILI_KEYS);
      String key = UtilityStruts.getParametroString(request, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
      try {
        if (profiloApplicativo != null && StringUtils.isNotBlank(key)) {
          if (!hashProfiliKeys.get(profiloApplicativo).contains(key)) {
            throw new GestoreException("Bloccato tentativo di modifica di un'occorrenza legata ad un record non visibile", "violazioneKey");
          }
          DataColumn[] campiChiaveOriginali = UtilityTags.stringKeysToColumnWithParam(key);
          for (DataColumn campoChiave : campiChiaveOriginali) {
            if (impl.isColumn(campoChiave.getNomeFisico()) && !impl.getColumn(campoChiave.getNomeFisico()).getValue().toString().equals(campoChiave.getValue().toString()))
              // la chiave deve essere presente anche come campi della maschera, e non devono essere stati alterati
              throw new GestoreException("Bloccato tentativo di modifica di un'occorrenza legata ad un record non visibile", "violazioneKey");
          }
        }
      } catch (GestoreException e) {
        return GestoreEccezioni.gestisciEccezioneAction(e, this, request, logger,
            mapping, true);
      }
      // Eseguo l'inserimento passando le colonne
      gestore.update(null, impl);
      // Aggiunto il risettaggio dei campi chiave con i valori
      // originali della maschera in visualizzazione
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
          this.getKey(entita, impl));
    } else {
      throw new ServletException("Modo non gestito nell'update della scheda: "
          + modo);
    }
    request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO,
        UtilityTags.SCHEDA_MODO_VISUALIZZA);
    request.setAttribute(SchedaAction.ERRORE_NEL_UPDATE, new Boolean(false));
    return this.apri(mapping, form, request, response);
  }

  /**
   * Ritorna il campo key della scheda, costruendolo dal dizionario tabelle e dai
   * valori dei campi presenti nell'oggetto impl. Questo costruire il campo key
   * sempre nello stesso modo. Prima succedeva che tale campo venisse costruito
   * in modo differente e per i documenti associati e le note/avvvisi questo
   * poteva creare degli errori. Infatti per tali funzionalita' avere i seguenti
   * due valori per il campo:
   *
   * key="CAMPO1=T:VALORE1;CAMPO2=N:2;CAMPO3=N:1"
   * key="CAMPO2=N:2;CAMPO1=T:VALORE1;CAMPO3=N:1"
   *
   * campo l'estrazione di record diversi o addirittura la mancata estrazione
   * dei record esistenti.
   *
   * @param entita
   * @param impl
   * @throws GestoreException
   */
  private String getKey(String entita, DataColumnContainer impl)
      throws GestoreException {
    StringBuffer chiave = new StringBuffer("");
    DizionarioTabelle dizionarioTabelle = DizionarioTabelle.getInstance();
    Tabella tabella = dizionarioTabelle.getDaNomeTabella(entita);
    List<Campo> vettoreCampiChiave = tabella.getCampiKey();

    for(int j=0; j < vettoreCampiChiave.size(); j++){
      Campo campo = vettoreCampiChiave.get(j);
      DataColumn campoKey = null;
      if(impl.isColumn(campo.getNomeFisicoCampo()))
        campoKey = impl.getColumn(campo.getNomeFisicoCampo());
      else if(impl.isColumn(campo.getNomeFisico()))
        campoKey = impl.getColumn(campo.getNomeCampo());

      if(campoKey != null){
        if(j > 0)
          chiave.append(";");
        chiave.append(campoKey.toString());
        chiave.append("=");
        chiave.append(campoKey.getValue().toString(true));
      }
    }
    return chiave.toString();
  }

  /**
   * Esecuzione dell'aggiornamento su di un entità dinamica figlia 1:N di
   * un'entità principale. Il motivo per cui è stato definito questo metodo
   * dipende dal fatto che la creazione di un nuovo record potrebbe aver bisogno
   * della determinazione in automatico del valore di un campo contatore
   * progressivo rispetto alla chiave dell'entità padre.<br>
   * Va perciò creato un gestore che si basa sulla chiave numerica, e vanno
   * indicati i parametri per la sua istanziazione nel corretto rispetto della
   * definizione dell'entità da aggiornare
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward updateFiglia1N(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // Estraggo il modo di update
    String modo = UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO);
    try {
      // Esecuzione dell'apertura della scheda
      String entita = UtilityStruts.getParametroString(request,
          FormTrovaTag.CAMPO_ENTITA);
      if (entita == null)
        throw new Exception(
            "Nell'apertura della scheda non è impostata l'entità principale di partenza");

      // si estrae l'entità dai metadati associata all'entità principale della
      // form
      DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
      Tabella tabella = dizTabelle.getDaNomeTabella(entita);
      if (tabella == null)
        throw new Exception("L'entità "
            + entita
            + " non è definita nei metadati");

      // Istanzio il gestore che implementa le funzionalità sull'entità
      AbstractGestoreEntita gestore = getGestoreEntitaFiglia1N(request, tabella);

      return this.update(mapping, form, request, response, modo, entita,
          gestore);

    } catch (Throwable t) {
      // Salvo nel request che c'è stato un errore durante l'update
      request.setAttribute(SchedaAction.ERRORE_NEL_UPDATE, new Boolean(true));
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO, modo);
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }

  /**
   * Istanzia il gestore per l'entità figlia, che può essere un
   * DefaultGestoreEntita oppure un DefaultGestoreEntitaChiaveParzialeNumerica,
   * a seconda dei campi che costituiscono la chiave, ed a seconda del formato
   * dell'eventuale campo aggiuntivo rispetto all'entità padre e presente nella
   * chiave dell'entità
   *
   * @param request
   * @param tabella
   *        oggetto della classe Tabella che individua l'entità figlia 1:N
   * @return gestore opportuno istanziato
   */
  private AbstractGestoreEntita getGestoreEntitaFiglia1N(
      HttpServletRequest request, Tabella tabella) {

    AbstractGestoreEntita gestore = null;

    // si estrae il legame dell'entità (è uno solo) con l'entità padre
    String keyParent = UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT);
    String entitaParent = keyParent.substring(0, keyParent.indexOf('.'));
    LegameTabelle[] legami = tabella.getLegameTabelle(entitaParent);

    ArrayList<Campo> appoggio = (ArrayList<Campo>) tabella.getCampiKey();
    List<Campo> elencoCampiChiave = (List<Campo>) appoggio.clone();

    //Vector elencoCampiChiave = (Vector) tabella.getCampiKey().clone();
    Vector elencoCampiJoin = new Vector();
    String campoChiaveNoJoin = null;
    // si cicla sui campi dell'entità che partecipano alla join con l'entità
    // padre, e si aggiungono ad elencoCampiJoin; se rimane uno solo campo in
    // elencoCampiChiave allora lo si setta in campoChiaveNoJoin
    if (legami != null && legami[0] != null) {
      for (int i = 0; i < legami[0].getElencoCampiTabellaOrigine().length; i++) {
        String strCampoChiave = legami[0].getElencoCampiTabellaOrigine()[i];
        Campo campo = DizionarioCampi.getInstance().getCampoByNomeFisico(
            tabella.getNomeTabella() + "." + strCampoChiave);
        if (elencoCampiChiave.contains(campo)) {
          elencoCampiChiave.remove(campo);
          elencoCampiJoin.add(strCampoChiave);
        }
      }
      if (elencoCampiChiave.size() == 1) {
        campoChiaveNoJoin = (elencoCampiChiave.get(0)).getNomeCampo();
      }
    }

    boolean isRichiestoGestoreStandard = true;
    if (campoChiaveNoJoin != null) {
      // si tratta di un campo in più rispetto alla chiave del padre, quindi
      // si verifica la tipologia di tale campo: se è numerico, allora è un
      // progressivo automatico, se è altro allora deve essere inserito
      // dall'utente
      Campo campo = DizionarioCampi.getInstance().getCampoByNomeFisico(
          tabella.getNomeTabella() + "." + campoChiaveNoJoin);
      if (campo.getTipoColonna() == Campo.TIPO_INTERO)
        isRichiestoGestoreStandard = false;
    } else {
      // se non ci sono campi in più, l'associazione è 1:1 con il padre,
      // quindi si può utilizzare un gestore standard
    }

    if (isRichiestoGestoreStandard) {
      // Definisco il gestore che implementa le funzionalità sull'entità
      gestore = new DefaultGestoreEntita(tabella.getNomeTabella(), request);
    } else {
      // Definisco il gestore che utilizza un campo progressivo numerico nella
      // chiave costituita da più campi
      gestore = new DefaultGestoreEntitaChiaveNumerica(
          tabella.getNomeTabella(), campoChiaveNoJoin,
          (String[]) elencoCampiJoin.toArray(new String[0]), request);
    }
    gestore.setAction(this);
    return gestore;
  }

  public ActionForward annulla(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // Setraggo il modo di update
    String modo = UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO);

    String profiloApplicativo = (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);

    try {

      if (modo.equals(UtilityTags.SCHEDA_MODO_INSERIMENTO)) {

      } else if (modo.equals(UtilityTags.SCHEDA_MODO_MODIFICA)
          || modo.equals(UtilityTags.SCHEDA_MODO_VISUALIZZA)) {
        // si verifica che l'eventuale key ricevuto sia uno tra quelli navigati in precedenza e quindi sia un dato di possesso dell'utente
        HashMap<String, HashSet<String>> hashProfiliKeys = (HashMap<String, HashSet<String>>) request.getSession().getAttribute(CostantiGenerali.PROFILI_KEYS);
        String key = UtilityStruts.getParametroString(request, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
        if (profiloApplicativo != null && StringUtils.isNotBlank(key)) {
          if (!hashProfiliKeys.get(profiloApplicativo).contains(key)) {
            throw new GestoreException("Bloccato tentativo di annullamento modifica di un'occorrenza legata ad un record non visibile", "violazioneKey");
          }
        }
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO,
          UtilityTags.SCHEDA_MODO_VISUALIZZA);
        return this.apri(mapping, form, request, response);
      } else {
        throw new ServletException(
            "Modo non gestito nell'update della scheda: " + modo);
      }
      return UtilityTags.getUtilityHistory(request.getSession()).back(request);
    } catch (Throwable t) {
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO, modo);
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping, true);
    }
  }

  public ActionForward ricercaDocAss(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    try {
      String entita;
      String key;
      String outPath;

      // Esecuzione dell'apertura della scheda
      entita = UtilityStruts.getParametroString(request,
          FormTrovaTag.CAMPO_ENTITA);
      if (entita == null)
        throw new Exception(
            "Nell'apertura della scheda non è impostata l'entità della quale sono stati richiesti i documenti associati!");
      // leggere il campo ENTITA' e KEY dal request... e farne in qualche
      // modo il forward...
      key = UtilityStruts.getParametroString(request, "key");

      // Per caricare i documenti associati cambio entita': la pongo pari
      // a c0oggass
      outPath = UtilityTags.getPathFromEntita("C0OGGASS") + "lista.jsp";

      // Setto l'apertura in modalità visualizzazione
      request.setAttribute(UtilityStruts.PARAMETRO_MODO_APERTURA, "apri");
      if (request.getParameter(UtilityTags.DEFAULT_HIDDEN_FORM_JSP_PATH).indexOf(
          "-lista.jsp") < 0)
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT, key);
      else
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
            request.getParameter(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA));

      return UtilityStruts.redirectToPage(outPath, false, request);

    } catch (Throwable t) {
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }
}