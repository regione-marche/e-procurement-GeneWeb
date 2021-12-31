/*
 * Created on 23-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.permessi;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.permessi.PermessiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.permessi.PermessoEntita;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Classe per la gestione della lista dei permessi degli utenti rispetto ad
 * un lavoro
 *
 * @author Luca.Giacomazzo
 */
public class ListaPermessiEntitaAction extends DispatchActionBaseNoOpzioni {

  /** Testo per popolare la combobox delle autorizzazioni */
  public static final String[] LISTA_TEXT_AUTORIZZAZIONI = {"Solo lettura", "Modifica"};

  /** Lista dei valori per la combo box delle autorizzazioni */
  public static final String[] LISTA_VALUE_AUTORIZZAZIONI = {"2", "1"};

  private static final String FORWARD_VISUALIZZA  = "successVisualizza";
  private static final String FORWARD_EDIT        = "successEdit";
  private static final String FORWARD_PREDEFINITI = "successPredefiniti";

  // Valore di default per il campo G_PERMESSI.PREDEF a seconda
  // dell'applicazione in uso
  private static final Integer VALORE_DEFAULT_PREDEFINITO_LAVORI = new Integer(1);
  private static final Integer VALORE_DEFAULT_PREDEFINITO_GARE   = new Integer(2);
  private static final Integer VALORE_DEFAULT_PREDEFINITO_INS    = new Integer(1);
  private static final Integer VALORE_DEFAULT_PREDEFINITO_229    = new Integer(1);

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ListaPermessiEntitaAction.class);

  /**
   * Reference alla classe di business logic per il popolamento delle comboBox
   * presenti nella pagina
   */
  private PermessiManager      permessiManager;


  private TabellatiManager tabellatiManager;

  /**
   * @param accountManager
   *        accountManager da settare internamente alla classe.
   */
  public void setPermessiManager(PermessiManager permessiManager) {
    this.permessiManager = permessiManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {



    if (logger.isDebugEnabled()) logger.debug("editLista: inizio metodo");

    // target di default
    String target = ListaPermessiEntitaAction.FORWARD_EDIT;
    String messageKey = null;

    try {
      // lettura dal request del nome del campo chiave e del suo valore
      String campoChiave = request.getParameter("campoChiave");
      if(campoChiave == null)
        campoChiave = (String) request.getAttribute("campoChiave");

      String valoreChiave = request.getParameter("valoreChiave");
      if(valoreChiave == null)
        valoreChiave = (String) request.getAttribute("valoreChiave");

      if(!"CODGAR".equals(campoChiave) && !"IDMERIC".equals(campoChiave)){
        // Controllo dei diritti di accesso all'edit della condivisione del lavoro
        // ai vari utenti. L'accesso a questo funzionalita' e' consentito solo agli
        // utenti proprietari del lavoro o con diritti di amministrazione dei lavori
        // (cioe' USRSYS.SYSAB3 = 'A')
        if(this.bloccaCondivisioneLavori(request)){
          messageKey ="errors.permessi.noAbilitazione";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
          return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
        }
      }

      //Nel caso di Gare si legge anche il genereGara
      if("CODGAR".equals(campoChiave)){
        String genereGara = request.getParameter("genereGara");
        if(genereGara == null)
          genereGara = (String) request.getAttribute("genereGara");
        request.setAttribute("genereGara", genereGara);

      }

      List<Tabellato> listaRuoli = this.tabellatiManager.getTabellato(TabellatiManager.RUOLO_ME);

      ProfiloUtente Utente = (ProfiloUtente)request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      int idAccount = Utente.getId();
      String codiceUffint = StringUtils.stripToNull((String)request.getSession().getAttribute("uffint"));

      // Lista degli account con i permessi sul lavoro in analisi
      List<?> listaUtentiPermessiEntita =
          this.permessiManager.getAccountConPermessiEntita(campoChiave, valoreChiave, idAccount,codiceUffint);

      // set nel request della lista di tutti i gruppi e lo stato di
      // associazione con l'account in analisi
      request.setAttribute("listaPermessiEntitaUtenti", listaUtentiPermessiEntita);

      // set nel request della lista delle autorizzazioni e i relativi valori
      // per creare la relativa combobox
      request.setAttribute("listaTextAutorizzazioni", ListaPermessiEntitaAction.LISTA_TEXT_AUTORIZZAZIONI);
      request.setAttribute("listaValueAutorizzazioni", ListaPermessiEntitaAction.LISTA_VALUE_AUTORIZZAZIONI);

      // set nel request del nome del campo chiave e del suo valore
      request.setAttribute("campoChiave", campoChiave);
      request.setAttribute("valoreChiave", valoreChiave);

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      request.setAttribute("listaRuoli", listaRuoli);

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

    if (logger.isDebugEnabled()) logger.debug("editLista: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Metodo per stabilire se l'utente puo' accedere alla funzionalita' di condivisione
   * del lavoro sia in visualizzazione che in edit. La funzionalita' e' accessibile
   * se l'utente e' proprietario del lavoro o e' amministratore dei lavori
   *
   * @param request
   * @return Ritorna true se l'utente puo' accedere alla funzionalita' di
   *         condivisione del lavoro, false altrimenti
   */
  private boolean bloccaCondivisioneLavori(HttpServletRequest request){
    boolean result = true;

    // lettura dal request del nome del campo chiave e del suo valore
    String campoChiave = request.getParameter("campoChiave");
    if(campoChiave == null)
      campoChiave = (String) request.getAttribute("campoChiave");

    String valoreChiave = request.getParameter("valoreChiave");
    if(valoreChiave == null)
      valoreChiave = (String) request.getAttribute("valoreChiave");

    if(campoChiave != null && valoreChiave != null){
      ProfiloUtente profiloUtente = (ProfiloUtente)
        request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      String abilitazione = profiloUtente.getAbilitazioneStd();
      //Nel caso di condivisione di una gara si deve controllare il valore del
      //campo USRSYS.SYABG
      if ("CODGAR".equals(campoChiave) || "IDMERIC".equals(campoChiave))
        abilitazione = profiloUtente.getAbilitazioneGare();
      if(!"A".equals(abilitazione)){
        PermessoEntita permessoLavoro =
            this.permessiManager.getPermessoEntitaByIdAccount(campoChiave,
                valoreChiave, profiloUtente.getId());

        if(permessoLavoro != null && permessoLavoro.getProprietario() != null &&
            permessoLavoro.getProprietario().intValue() == 1)
          result = false;
      } else {
        result = false;
      }
    }
    return result;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward visualizza(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {


    if (logger.isDebugEnabled()) logger.debug("visualizza: inizio metodo");

    // target di default
    String target = ListaPermessiEntitaAction.FORWARD_VISUALIZZA;
    String messageKey = null;

    try {
      String campoChiave = request.getParameter("campoChiave");
      if(campoChiave == null)
        campoChiave = (String) request.getAttribute("campoChiave");

      String valoreChiave = request.getParameter("valoreChiave");
      if(valoreChiave == null)
        valoreChiave = (String) request.getAttribute("valoreChiave");

      //Nel caso di gare e ricerche di mercato la lista viene visualizzata da tutti
      //gli utenti
      if(!"CODGAR".equals(campoChiave) && !"IDMERIC".equals(campoChiave)){
        // Controllo dei diritti di accesso all'edit della condivisione del lavoro
        // ai vari utenti. L'accesso a questo funzionalita' e' consentito solo agli
        // utenti proprietari del lavoro o con diritti di amministrazione dei lavori
        // (cioe' USRSYS.SYSAB3 = 'A')
        if(this.bloccaCondivisioneLavori(request)){
          messageKey ="errors.permessi.noAbilitazione";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
          return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
        }
      }

      //Nel caso di Gare si legge anche il genereGara
      if("CODGAR".equals(campoChiave)){
        String genereGara = request.getParameter("genereGara");
        if(genereGara == null)
          genereGara = (String) request.getAttribute("genereGara");
        request.setAttribute("genereGara", genereGara);

      }

      // leggo la lista degli utenti associati al lavoro in analisi
      List<?> listaPermessiEntitaTemp = this.permessiManager.getListaPermessiEntita(
          campoChiave, valoreChiave);

      List<PermessoEntitaForm> listaPermessiEntita = new ArrayList<PermessoEntitaForm>();
      // Trasformazione: dalla lista di oggetti di tipo PermessoEntita in lista di oggetti
      // di oggetti di tipo PermessoEntitaForm (per la visualizzazione)
      PermessoEntitaForm permessoEntita = null;
      for (int i = 0; i < listaPermessiEntitaTemp.size(); i++) {
        permessoEntita = new PermessoEntitaForm((PermessoEntita) listaPermessiEntitaTemp.get(i));
        listaPermessiEntita.add(permessoEntita);
      }

      // Nel caso la lista dei permessi dell'entita' sia vuota non si visualizza
      // il link per avviare la predefinizione dei permessi. Questo lo si effettua
      // settando l'attributo 'setPermessiPredefiniti' ad un qualsiasi valore
      if (listaPermessiEntita.size() == 0) {
        request.setAttribute("setPermessiPredefiniti", "0");
      }
      request.setAttribute("listaPermessiEntita", listaPermessiEntita);
      request.setAttribute("campoChiave", campoChiave);
      request.setAttribute("valoreChiave", valoreChiave);

      // Si determina se l'entità di cui si stanno visualizzando i permessi
      // possiede una condivisione predefinita o meno
      int utenteDiRiferimento = ((ProfiloUtente)request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();

      String codApp = (String) request.getSession().getAttribute(
          CostantiGenerali.MODULO_ATTIVO);

      if(this.permessiManager.hasAccountCondivisionePredefinita(
          new Integer(utenteDiRiferimento),
          this.getValorePredefinitoEntita(codApp)))
        request.setAttribute("esisteCondivisionePredefinita", "1");

      List<Tabellato> listaRuoli = this.tabellatiManager.getTabellato(TabellatiManager.RUOLO_ME);
      Map<Integer, Tabellato> hashRuoli = new TreeMap<Integer, Tabellato>();
      for (Tabellato t: listaRuoli) {
        hashRuoli.put(Integer.parseInt(t.getTipoTabellato()), t);
      }
      request.setAttribute("hashRuoli", hashRuoli);

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

    if (logger.isDebugEnabled()) logger.debug("visualizzaLista: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Metodo per impostare la attuale condivisione dell'entita' in analisi
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward setPermessiPredefiniti(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {



    if (logger.isDebugEnabled()) logger.debug("visualizza: inizio metodo");

    // target di default
    String target = ListaPermessiEntitaAction.FORWARD_PREDEFINITI;
    String messageKey = null;

    try {
      String campoChiave = request.getParameter("campoChiave");
      if(campoChiave == null)
        campoChiave = (String) request.getAttribute("campoChiave");

      String valoreChiave = request.getParameter("valoreChiave");
      if(valoreChiave == null)
        valoreChiave = (String) request.getAttribute("valoreChiave");

      if(!"CODGAR".equals(campoChiave) && !"IDMERIC".equals(campoChiave)){
        // Controllo dei diritti di accesso all'edit della condivisione del lavoro
        // ai vari utenti. L'accesso a questo funzionalita' e' consentito solo agli
        // utenti proprietari del lavoro o con diritti di amministrazione dei lavori
        // (cioe' USRSYS.SYSAB3 = 'A')
        if(this.bloccaCondivisioneLavori(request)){
          messageKey ="errors.permessi.noAbilitazione";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
          return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
        }
      }

      //Nel caso di Gare si legge anche il genereGara
      if("CODGAR".equals(campoChiave)){
        String genereGara = request.getParameter("genereGara");
        if(genereGara == null)
          genereGara = (String) request.getAttribute("genereGara");
        request.setAttribute("genereGara", genereGara);

      }

      List<?> listaPermessiEntita = this.permessiManager.getListaPermessiEntita(
          campoChiave, valoreChiave);

      int utenteDiRiferimento = ((ProfiloUtente)request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();

      String codApp = (String) request.getSession().getAttribute(
          CostantiGenerali.MODULO_ATTIVO);

      if(listaPermessiEntita.size() > 0){
        boolean inserito = false;
        int numeroTentativi = 0;

        // tento di inserire il record finchè non genero un ID univoco a causa
        // della concorrenza, o raggiungo il massimo numero di tentativi
        while (!inserito
            && numeroTentativi < CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
          try {
            this.permessiManager.insertPermessiPredefiniti(campoChiave,
                valoreChiave, new Integer(utenteDiRiferimento),
                this.getValorePredefinitoEntita(codApp), listaPermessiEntita);
            inserito = true;
          } catch (DataIntegrityViolationException div) {
            if (numeroTentativi < CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
              logger.error(
                  "Fallito tentativo "
                      + (numeroTentativi + 1)
                      + " di inserimento record per chiave duplicata, si ritenta nuovamente",
                  div);
              numeroTentativi++;
            }
          }
        }
        if (!inserito
            && numeroTentativi >= CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
          throw new DataIntegrityViolationException(
              "Raggiunto limite massimo di tentativi");
        }
      } else {
        messageKey = "errors.permessi.noPermessiPredefiniti";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }

      // set nel request del nome del campo chiave e del suo valore
      request.setAttribute("campoChiave", campoChiave);
      request.setAttribute("valoreChiave", valoreChiave);

    } catch (DataIntegrityViolationException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.inserimento.chiaveDuplicata";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
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

    if (logger.isDebugEnabled()) logger.debug("visualizzaLista: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Determina il valore predefinito associato al campo G_PERMESSI.PREDEF in
   * funzione del codice applicazione in uso.
   * Infatti tale campo assuma i seguenti valori:
   * - 1 per Lavori (PL)
   * - 2 per Gare (PG)
   *
   * @param codiceApplicazione
   * @return Ritorna il valore predefinito associato al campo G_PERMESSI.PREDEF,
   *         altrimenti emmette una NullPointerException (che viene visto come
   *         un errore inaspettato)
   */
  private Integer getValorePredefinitoEntita(String codiceApplicazione){
    Integer result = null;

    if("PL".equals(codiceApplicazione))
      result = ListaPermessiEntitaAction.VALORE_DEFAULT_PREDEFINITO_LAVORI;
    else if("PG".equals(codiceApplicazione))
      result = ListaPermessiEntitaAction.VALORE_DEFAULT_PREDEFINITO_GARE;
    else if("AI".equals(codiceApplicazione))
      result = ListaPermessiEntitaAction.VALORE_DEFAULT_PREDEFINITO_INS;
    else if("229".equals(codiceApplicazione))
      result = ListaPermessiEntitaAction.VALORE_DEFAULT_PREDEFINITO_229;
    // else if("")....
    // TODO discriminare il valore di default del campo G_PERMESSI.PREDEF
    // in funzione dell'applicazione in uso. Infatti questo campo assuma i
    // seguenti valori:
    // - 1 per Lavori;
    // - 1 per Lavori autostrade (INS);
    // - 2 per Gare;

    // TODO modificare la logica con cui determinare il valore predefinito del
    // campo G_PERMESSI.PREDEF: associare il valore predefinito 1, 2, ... in
    // base all'entita' da cui si invoca la funzione. In questo modo si slega
    // il valore predefinito dal codice applicativo
    if(result == null)
      throw new NullPointerException("Il valore predefinito da associare al " +
            "campo G_PERMESSI.PREDEF non e' stato definito per il codice di " +
            "applicazione '".concat(codiceApplicazione).concat("'). Modificare" +
            "il metodo privato getValorePredefinitoEntita della classe " +
            "ListaPermessiEntitaAction per definire tale valore."));
      // Viene emessa una NullPointerException nel caso non sia stato modificato
      // questo metodo per gestire il valore predefinito per il codice applicazione
      // in uso
    return result;
  }

}