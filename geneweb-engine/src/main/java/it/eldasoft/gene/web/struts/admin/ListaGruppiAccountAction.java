/*
 * Created on 22-feb-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.admin.GruppoConProfiloAccount;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
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
 * Action che controlla l'operazione di apertura della pagina Lista Utenti di
 * Gruppo
 *
 * @author Francesco.DeFilippis
 */
public class ListaGruppiAccountAction extends AbstractDispatchActionBaseAdmin {

  // Forward predefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private static final String FORWARD_VISUALIZZA = "successVisualizzaLista";
  private static final String FORWARD_EDIT       = "successEditLista";

  /** Logger Log4J di classe */
  static Logger               logger             = Logger.getLogger(ListaGruppiAccountAction.class);

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  protected ResourceBundle    resBundleGenerale  = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /**
   * Reference alla classe di business logic per il popolamento delle comboBox
   * presenti nella pagina
   */
  private AccountManager      accountManager;

  /**
   * @return Ritorna accountManager.
   */
  public AccountManager getAccountManager() {
    return this.accountManager;
  }

  /**
   * @param accountManager
   *        accountManager da settare internamente alla classe.
   */
  public void setAccountManager(AccountManager utentiManager) {
    this.accountManager = utentiManager;
  }

  /**
   * Funzione che da l'opzione per la modifica della lista
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEditLista() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  public ActionForward editLista(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // è un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    if (this.bloccaGestioneGruppiDisabilitata(request))
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);

    if (logger.isDebugEnabled()) {
      logger.debug("editLista: inizio metodo");
    }

    // target di default da modificare nel momento in cui si verificano dei
    // problemi
    // per l'azione 'salvaGruppoConFunzionalita'
    String target = ListaGruppiAccountAction.FORWARD_EDIT;
    String messageKey = null;

    try {
      // lettura dal request del idAccount
      int idAccount = Integer.parseInt(request.getParameter("idAccount"));

      Account account = this.accountManager.getAccountById(idAccount);
      request.setAttribute("nascondiUffint", new OpzioniUtente(account.getOpzioniUtente()).isOpzionePresente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA));

      String codApp = (String) request.getSession().getAttribute(
          CostantiGenerali.MODULO_ATTIVO);

      int numeroAssociazioniProfili = accountManager.getNumeroAssociazioniProfili(idAccount, codApp);

      if (numeroAssociazioniProfili == 0) {
        // Nessun profilo associato all'account, quindi non e' possibile
        // associare alcun gruppo all'account
        target = ListaGruppiAccountAction.FORWARD_VISUALIZZA;
        messageKey = "warnings.gruppi.accountSenzaProfili";
        if(logger.isInfoEnabled())
          logger.warn(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      } else {

        // lista dei gruppi con attributo 'associato' di tipo boolean
        // valorizzato a true se il gruppo
        // è associato all'utente in analisi e a false altrimenti. La lista è
        // ordinata per nome del gruppo
        List<GruppoConProfiloAccount> listaGruppiAccount =
          accountManager.getGruppiConAssociazioneAccount(idAccount,
              codApp,
              (String) request.getSession().getAttribute(
                  CostantiGenerali.PROFILO_ATTIVO));

        if(listaGruppiAccount != null && listaGruppiAccount.size() > 0){
          // preparo la lista da passare al form
          GruppiAccountForm gruppoAccount = new GruppiAccountForm();
          Vector<String> tmp = new Vector<String>();
          for (int i = 0; i < listaGruppiAccount.size(); i++)
            if ((listaGruppiAccount.get(i)).getAssociato())
              tmp.add(""
                  + (listaGruppiAccount.get(i)).getIdGruppo());

          gruppoAccount.setIdGruppo(tmp.toArray(new String[0]));

          // set nel request della lista di tutti i gruppi e lo stato di
          // associazione con l'account in analisi
          request.setAttribute("listaGruppiAccount", listaGruppiAccount);

          // set nel request della stringa necessaria al form di modifica
          // associazione utenti-gruppo
          request.setAttribute("gruppiAccountForm", gruppoAccount);

          // set nel request del parameter per disabilitare la navigazione in fase
          // di editing
          request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
              CostantiGenerali.DISABILITA_NAVIGAZIONE);

          // update del menu a tab
          this.setMenuTabEdit(request);
        } else {
          // Nessun gruppo definito nel profilo , quindi non e' possibile
          // associare alcun gruppo all'account
          target = ListaGruppiAccountAction.FORWARD_VISUALIZZA;
          messageKey = "warnings.gruppi.profiloSenzaGruppi";
          if(logger.isInfoEnabled())
            logger.warn(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }
      }

      // set nel request dell'idAccount dell'account in analisi
      request.setAttribute("idAccount", new Integer(idAccount));

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

    if (logger.isDebugEnabled()) {
      logger.debug("editLista: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Funzione che da l'opzione per la modifica della lista
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVisualizzaLista() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_LETTURA_ADMIN);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */

  public ActionForward visualizzaLista(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // è un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    if (this.bloccaGestioneGruppiDisabilitata(request))
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);

    if (logger.isDebugEnabled()) logger.debug("visualizzaLista: inizio metodo");

    // target di default
    String target = ListaGruppiAccountAction.FORWARD_VISUALIZZA;
    String messageKey = null;

    try {
      // lettura dal request del idAccount
      int idAccount = Integer.parseInt(request.getParameter("idAccount"));

      Account account = this.accountManager.getAccountById(idAccount);
      request.setAttribute("nascondiUffint", new OpzioniUtente(account.getOpzioniUtente()).isOpzionePresente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA));

      // leggo la lista dei gruppi associati all'account filtrando per modulo
      // attivo, in modo da estrarre tutti i gruppi a cui l'utente e' associato
      // per ogni profilo con cui può accedere all'applicativo
      List<GruppoConProfiloAccount> listaGruppiConProfilo = this.accountManager.getListaGruppiAccount(
          idAccount, (String) request.getSession().getAttribute(
              CostantiGenerali.MODULO_ATTIVO));
      // Conversione da una lista di int (idGruppo) a una lista di oggetti di
      // tipo it.eldasoft.gene.web.struts.admin.GruppoForm
      //ArrayList listaGruppiForm = setDatiPerModel(listaGruppiConProfilo, session);

      // set nel request del beanForm contenente la lista dei gruppi con le
      // funzionalità
      request.setAttribute("listaGruppiForm", listaGruppiConProfilo);
      request.setAttribute("idAccount", "" + idAccount);

      // update del menu a tab
      this.setMenuTab(request);

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
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab
   *
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.GRUPPI);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioAccount.DETTAGLIO,
          CostantiDettaglioAccount.PROFILI,
          CostantiDettaglioAccount.GRUPPI,
          CostantiDettaglioAccount.UFFICI_INTESTATARI,
          CostantiDettaglioAccount.TECNICI});
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.GRUPPI);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioAccount.DETTAGLIO,
          CostantiDettaglioAccount.PROFILI,
          CostantiDettaglioAccount.GRUPPI,
          CostantiDettaglioAccount.UFFICI_INTESTATARI,
          CostantiDettaglioAccount.TECNICI });
      sessione.setAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di editing della lista utenti di gruppo
   *
   * @param request
   */
  private void setMenuTabEdit(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.GRUPPI);
      gestoreTab.setTabSelezionabili(null);
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.GRUPPI);
      gestoreTab.setTabSelezionabili(null);
      sessione.setAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }

  /**
   * Conversione da una lista di int (idGruppo) a una lista di oggetti di tipo
   * it.eldasoft.gene.web.struts.adminGruppoForm
   *
   * @param listaIn
   *        Lista in input
   * @param session
   *        sessione
   * @return
   */
/*  private ArrayList setDatiPerModel(List listaIn, HttpSession session) {
    ArrayList listaOut = new ArrayList();
    Iterator iteratorListaIn = listaIn.iterator();
    Gruppo gruppo = null;
    GruppoForm gruppoForm = null;

    while (iteratorListaIn.hasNext()) {
      gruppoForm = null;
      gruppo = null;
      Object obj = iteratorListaIn.next();
      ;
      Integer idGruppo = null;
      if (obj instanceof Integer)
        idGruppo = (Integer) obj;
      else if (obj instanceof GruppoAccount) {
        idGruppo = new Integer(((GruppoAccount) obj).getIdGruppo());
      }
      gruppo = this.gruppiManager.getGruppoById(idGruppo.intValue(),
          (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO));
      gruppoForm = new GruppoForm();
      gruppoForm.setIdGruppo(gruppo.getIdGruppo());
      gruppoForm.setNomeGruppo(gruppo.getNomeGruppo());
      gruppoForm.setDescrizione(gruppo.getDescrGruppo());
      // Se si tratta di un Gruppo Account allora setto la priorita
      if (obj instanceof GruppoAccount)
        gruppoForm.setPriorita(((GruppoAccount) obj).getPriorita());
      listaOut.add(gruppoForm);
    }
    return listaOut;
  }*/

}