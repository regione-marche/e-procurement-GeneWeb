/*
 * Created on 28-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.datigen;

import it.eldasoft.gene.bl.MetadatiManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.CambiaTabAction;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action per salvare in sessione dati generali della ricerca in analisi
 *
 * @author Luca Giacomazzo
 */
public class SalvaDatiGenRicercaAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(SalvaDatiGenRicercaAction.class);

  private MetadatiManager metadatiManager;
  private RicercheManager ricercheManager;

  /**
   * @param metadatiManager metadatiManager da settare internamente alla classe.
   */
  public void setMetadatiManager(MetadatiManager metadatiManager) {
    this.metadatiManager = metadatiManager;
  }

  /**
   * @param ricercheManager ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action runAction
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("execute: inizio metodo");

    // target di default
    String target = null;
    String messageKey = null;

    try {
      TestataRicercaForm testataRicercaForm = (TestataRicercaForm) form;
      // aggiornamento dell'oggetto testataRicercaForm presente in sessione
      target = this.aggiornaTestataRicercaSession(request, testataRicercaForm);

      this.marcaRicercaModificata(request, testataRicercaForm.getNome());
      request.setAttribute("tab", CambiaTabAction.CODICE_TAB_DATI_GENERALI);

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

    if (logger.isDebugEnabled()) logger.debug("execute: fine metodo");

    return mapping.findForward(target);
  }

  private String aggiornaTestataRicercaSession(HttpServletRequest request,
      TestataRicercaForm testata) {

    // correzione di questi 2 parametri, che se arrivano null, dipendono dal
    // fatto che da interfaccia sono presentati ma le checkbox non sono
    // selezionate, quindi vanno tradotte con un false
    if (testata.getVisParametri() == null)
      testata.setVisParametri(Boolean.FALSE);
    if (testata.getLinkScheda() == null)
      testata.setLinkScheda(Boolean.FALSE);

    // lettura dalla sessione dei parametri relativi alla ricerca in analisi
    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    TestataRicercaForm testataSessione = contenitore.getTestata();
    testataSessione.setTipoRicerca(testata.getTipoRicerca());
    // L.G 06/03/07: non aggiorno la famiglia perche' e' stata settata in
    // precedenza dalla pagina di selezione della famiglia del report: report
    // base, avanzato o con modello
    // testataSessione.setFamiglia(testata.getFamiglia());
    testataSessione.setNome(testata.getNome());
    testataSessione.setDescrizione(testata.getDescrizione());
    testataSessione.setDisp(testata.getDisp());
    testataSessione.setRisPerPag(testata.getRisPerPag());
    testataSessione.setPersonale(testata.getPersonale());
    testataSessione.setVisParametri(testata.getVisParametri());
    
    if (!this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
      // L.G. 08/06/2007: modifica per controllare il settaggio della ricerca a
      // personale. Se la ricerca e' gia' stata associata/pubblicata ad almeno
      // un gruppo la ricerca non puo' ritornare ad essere personale
      if(testata.getPersonale() && contenitore.getNumeroGruppi() > 0) {
        testataSessione.setPersonale(false);
        testataSessione.setDisp(true);
        String messageKey = "warnings.genric.datiGenerali.noRicercaPersonaleSeGiaPubblicata";
        logger.warn(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
    }
    // L.G. 08/06/2007: fine modifica

    boolean isFunzioniStatistichePresenti = false;
    if (testata.getVisModelli() || testata.getLinkScheda().booleanValue()) {
      for (int i = 0; i < contenitore.getNumeroCampi(); i++) {
        if (UtilityStringhe.convertiStringaVuotaInNull(((CampoRicercaForm) contenitore.getElencoCampi().elementAt(
            i)).getFunzione()) != null) isFunzioniStatistichePresenti = true;
      }
    }
    // si aggiorna l'opzione di visualizzazione modelli solo se la ricerca non
    // estrae dati raggruppati e con statistiche
    if (testata.getVisModelli()) {
      if (!isFunzioniStatistichePresenti)
        testataSessione.setVisModelli(testata.getVisModelli());
      else {
        String messageKey = "warnings.genRic.visModelli.nonAttivabile";
        if (logger.isInfoEnabled())
          logger.info(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
    } else
      testataSessione.setVisModelli(testata.getVisModelli());

    // si aggiorna l'opzione di collegamento alla scheda solo se la ricerca non
    // estrae dati raggruppati e con statistiche
    if (testata.getLinkScheda().booleanValue()) {
      if (!isFunzioniStatistichePresenti) {
        // verifico se esiste la pagina che consentirebbe l'apertura della
        // scheda di dettaglio
        String aliasEntitaPrincipale = null;
        if (CostantiGenRicerche.REPORT_AVANZATO == testataSessione.getFamiglia().intValue()) {
          aliasEntitaPrincipale = testataSessione.getEntPrinc();
        } else {
          // nel report base l'entita' e' quella vera e propria
          aliasEntitaPrincipale = this.metadatiManager.getEntitaPrincipaleVista(testataSessione.getEntPrinc()
              + CostantiGenerali.SEPARATORE_PROPERTIES
              + ConfigManager.getValore(CostantiGenerali.PROP_SCHEMA_VISTE_REPORT_BASE));
        }
        if (aliasEntitaPrincipale != null) {
          boolean esisteJsp = false;
          if (CostantiGenRicerche.REPORT_AVANZATO == testataSessione.getFamiglia().intValue()) {
            esisteJsp = SalvaDatiGenRicercaAction.existsJspSchedaDettaglio(request,
                contenitore);
          } else {
            esisteJsp = SalvaDatiGenRicercaAction.existsJspSchedaDettaglio(request, aliasEntitaPrincipale);
          }

          if (esisteJsp) {
            testataSessione.setLinkScheda(testata.getLinkScheda());
          } else {
            String messageKey = "warnings.genRic.linkScheda.nonEsisteJspDettaglio";
            logger.warn(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
          }
        } else {
          String messageKey = "warnings.genRic.linkScheda.nonEsisteEntitaPrincipale";
          logger.warn(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }
      } else {
        String messageKey = "warnings.genRic.linkScheda.nonAttivabile";
        if (logger.isInfoEnabled())
          logger.info(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
    } else
      testataSessione.setLinkScheda(testata.getLinkScheda());

    // si aggiorna il codice solo se non esiste un altro report nel db con lo stesso codice
    if (StringUtils.stripToNull(testata.getCodReportWS()) != null) {
      Integer id = this.ricercheManager.getIdRicercaByCodReportWS(testata.getCodReportWS());
      if (id == null)
        testataSessione.setCodReportWS(testata.getCodReportWS());
      else {
        if (testataSessione.getId() == null || !id.toString().equals(testataSessione.getId())) {
          String messageKey = "warnings.genRic.codReportWS.nonModificabile";
          logger.warn(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }
      }
      //if (testataSessione.getId() != null && testataSessione.getId().)
    } else
      testataSessione.setCodReportWS(null);

    // set in sessione del nome della ricerca di cui si sta facendo il
    // dettaglio
    sessione.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
        contenitore.getTestata().getNome());

    // L.G. 26/03/2007: modifica per implementazione dei report base.
    // Si setta il target in funzione del tipo di report che si sta modificando
    String result = null;
    if (CostantiGenRicerche.REPORT_AVANZATO == testataSessione.getFamiglia().intValue())
      result = CostantiGeneraliStruts.FORWARD_OK;
    else if (CostantiGenRicerche.REPORT_BASE == testataSessione.getFamiglia().intValue())
      result = CostantiGeneraliStruts.FORWARD_OK + "Base";
    else if (CostantiGenRicerche.REPORT_SQL == testataSessione.getFamiglia().intValue())
      result = CostantiGeneraliStruts.FORWARD_OK + "Sql";
    return result;
  }

  /**
   * A partire dall'entit&agrave; principale indicata nella ricerca avanzata, si verifica
   * se esiste la JSP corrispondente alla scheda di dettaglio.
   *
   * @param request
   *        request HTTP
   * @param contenitoreRicerca
   *        ricerca
   * @param aliasEntitaPrincipale
   *        alias dell'entit&agrave; principale
   * @return true se la jsp esiste nella web application, false altrimenti
   */
  public static boolean existsJspSchedaDettaglio(HttpServletRequest request,
      ContenitoreDatiRicercaForm contenitoreRicerca) {
    String nomeTabella = contenitoreRicerca.getNomeEntitaPrincipale();
    return existsJspSchedaDettaglio(request, nomeTabella);
  }

  /**
   * A partire dall'entit&agrave; in input, si verifica
   * se esiste la JSP corrispondente alla scheda di dettaglio.
   *
   * @param nomeTabella
   *        nome dell'entit&agrave;
   * @return true se la jsp esiste nella web application, false altrimenti
   */
  public static boolean existsJspSchedaDettaglio(HttpServletRequest request,
      String nomeTabella) {
    InputStream stream = null;
    String pathAppoggio = getJspSchedaDettaglio(nomeTabella);
    if (pathAppoggio != null) {
      String pathJsp = CostantiGenerali.PATH_WEBINF + "pages/" + pathAppoggio;
      ServletContext context = request.getSession().getServletContext();
      stream = context.getResourceAsStream(pathJsp);
    }
    return stream != null;
  }

  /**
   * Costruisce il path della Jsp di scheda relativa alla WEB-INF/pages
   *
   * @param nomeTabella
   *        nome dell'entit&agrave;
   * @return percorso dell'eventuale JSP, null se l'entit&agrave; non esiste
   */
  public static String getJspSchedaDettaglio(String nomeTabella) {
    Tabella t = DizionarioTabelle.getInstance().getDaNomeTabella(nomeTabella);
    String pathJsp = null;
    if (t != null) {
      pathJsp = t.getNomeSchema().toLowerCase()
          + "/"
          + nomeTabella.toLowerCase()
          + "/"
          + nomeTabella.toLowerCase()
          + "-scheda.jsp";
    }
    return pathJsp;
  }

}
