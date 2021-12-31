/*
 * Created on 20-ago-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.impexp;

import it.eldasoft.gene.bl.CheckReportPerProfilo;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.domain.admin.RicercaGruppo;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiImport;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiProspetto;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.db.domain.genric.TrovaRicerche;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per l'upload del file XML contenente la definizione del report da
 * importare, creazione dell'oggetto ContenitoreDatiRicerca e controllare che la
 * ricerca deve essere creata con il profilo attivo;
 *
 * Se il controllo ha esito positivo, allora si aggiorna il codApp all'oggetto
 * ContenitoreDatiImport, si carica lo stesso oggetto in sessione, valorizzando
 * l'attributo ContenitoreDatiRicerca o ContenitoreDatiProspetto (a seconda del
 * tipo di report in importazione) e si fa il forward alla seconda pagina del
 * wizard di importazione
 *
 * @author Luca.Giacomazzo
 */
public class UploadDefinizioneRicercaAction extends ActionBaseNoOpzioni {

  static Logger           logger = Logger.getLogger(UploadDefinizioneRicercaAction.class);

  /**
   * Reference al Manager per la gestione delle protezioni di tabelle e campi
   * rispetto al profilo attivo
   */
  private GeneManager     geneManager;

  private RicercheManager ricercheManager;

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * @param ricercheManager ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  @Override
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;
    Object contenitore = null;
    ContenitoreDatiImport contenitoreDatiImport = new ContenitoreDatiImport();

    UploadFileForm uploadFile = (UploadFileForm) form;
    // Creazione dell'oggetto XMLDecoder a partire del file in upload
    XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(
        uploadFile.getSelezioneFile().getInputStream()), null, null);
    // Set del contenitore dei dati della ricerca
    try {
      contenitore = decoder.readObject();
    } catch (NoSuchElementException nse) {
      // Eccezione nel caso in cui il file non sia un file xml
      target = CostantiWizard.ERROR_IMPORT_REPORT;
      messageKey = "errors.genric.import.fileNoFormatoXml";
      logger.error(this.resBundleGenerale.getString(messageKey), nse);
      this.aggiungiMessaggio(request, messageKey);
    } catch (ArrayIndexOutOfBoundsException aiob) {
      // Eccezione nel caso in cui lo stream associato al file xml non presenti
      // alcun oggetto
      target = CostantiWizard.ERROR_IMPORT_REPORT;
      messageKey = "errors.genric.import.fileXmlNoObjects";
      logger.error(this.resBundleGenerale.getString(messageKey), aiob);
      this.aggiungiMessaggio(request, messageKey);
    } finally {
      decoder.close();
    }


    if (CostantiGeneraliStruts.FORWARD_OK.equals(target)) {
      if(contenitore instanceof ContenitoreDatiRicerca || contenitore instanceof ContenitoreDatiProspetto){
        // Determino la famiglia del report da importare
        int famigliaReport = -1;
        if (contenitore instanceof ContenitoreDatiRicerca) {
          contenitoreDatiImport.setContenitoreDatiRicerca((ContenitoreDatiRicerca) contenitore);
          famigliaReport = contenitoreDatiImport.getContenitoreDatiRicerca().getDatiGenerali().getFamiglia().intValue();
        } else {
          contenitoreDatiImport.setContenitoreDatiProspetto((ContenitoreDatiProspetto) contenitore);
          famigliaReport = CostantiGenRicerche.REPORT_PROSPETTO;
        }

        Collection<String> opzioni = Arrays.asList((String[]) request.getSession().getServletContext().getAttribute(
            CostantiGenerali.OPZIONI_DISPONIBILI));

        String tmp = null;

        // Variabile per determinare se il report può essere importato, in linea
        // con le opzioni vendute in configurazione aperta: in particolare se si
        // e' in configurazione aperta e il report e' un report base o con
        // prospetto
        // e non è stato venduto l'OP98, allora in report non importabile
        boolean isReportImportabile = true;
        if (request.getSession().getServletContext().getAttribute(
            CostantiGenerali.ATTR_CONFIGURAZIONE_CHIUSA).equals("0")
            && (famigliaReport == CostantiGenRicerche.REPORT_PROSPETTO || famigliaReport == CostantiGenRicerche.REPORT_BASE)
            && !opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL)) {
          isReportImportabile = false;
        }

        if (isReportImportabile) {
          if (famigliaReport == CostantiGenRicerche.REPORT_PROSPETTO)
            tmp = this.checkReportProspetto(request,
                contenitoreDatiImport.getContenitoreDatiProspetto());
          else
            tmp = this.checkReportBaseAvanzatoSql(request, contenitoreDatiImport);
        } else {
          tmp = CostantiWizard.ERROR_IMPORT_REPORT;
          messageKey = "errors.genric.import.reportProfesNonImportabile";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }

        if (tmp != null) target = tmp;

        if (target.equals(CostantiGeneraliStruts.FORWARD_OK))
          // Set in sessione del contenitore dati import, che contiene sia le
          // informazioni necessarie al wizard di importazione, sia il contenitore
          // dati del report (base o avanzato o con modello)
          // ATTENZIONE: in sessione viene inserito l'oggetto
          // ContenitoreDatiImport
          request.getSession().setAttribute(
              CostantiGenRicerche.OGGETTO_DETTAGLIO, contenitoreDatiImport);
      } else {
        target = CostantiWizard.ERROR_IMPORT_REPORT;
        // Errore per importazione di un report, specificando l'xml dell'export
        // di un modello
        messageKey = "errors.genric.import.fileXML_NoReport";
        logger.error(this.resBundleGenerale.getString(messageKey), null);
        this.aggiungiMessaggio(request, messageKey);
      }
    }

    // Cancellazione dalla sessione del contenitore dati import
    if (target.equals(CostantiWizard.ERROR_IMPORT_REPORT))
      request.getSession().removeAttribute(
          CostantiGenRicerche.OGGETTO_DETTAGLIO);

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Metodo per la verifica che le entita' e i campi usati dal report avanzato o
   * base da importare siano vsibili nel profilo attivo. In caso di esito
   * positivo viene prima aggiornato il codApp del report ed inserito in
   * sessione il report stesso
   *
   * @param request
   * @param contenitoreReport
   * @return Ritorna il target della action in funzione dell'operazione
   *         effettuata
   */
  private String checkReportBaseAvanzatoSql(HttpServletRequest request,
      ContenitoreDatiImport contenitoreDatiImport) {

    if (logger.isDebugEnabled()) {
      logger.debug("checkReportBaseAvanzatoSql: inizio metodo");
    }

    ContenitoreDatiRicerca contenitoreReport = contenitoreDatiImport.getContenitoreDatiRicerca();

    String target = null;
    String messageKey = null;

    //boolean continua = true;

    String codiceApplicativo = (String) request.getSession().getAttribute(
        CostantiGenerali.MODULO_ATTIVO);

    // nel caso di import da versioni precedenti setto i campi con il default se arriva non valorizzato
    if (contenitoreReport.getDatiGenerali().getVisParametri() == null)
      contenitoreReport.getDatiGenerali().setVisParametri(0);
    if (contenitoreReport.getDatiGenerali().getLinkScheda() == null)
      contenitoreReport.getDatiGenerali().setLinkScheda(0);

    // nel caso di WebConsole i link per l'apertura scheda sono tassativamente
    // disabilitati dato che l'applicativo fornisce sola reportistica
    if ("W0".equals(codiceApplicativo)) {
      contenitoreReport.getDatiGenerali().setLinkScheda(0);
    }

    // S.S. 01/10/2008 WE128: si può importare un report esportato anche con un applicativo diverso
//    if (continua
//        && (!codiceApplicativo.equals(contenitoreReport.getDatiGenerali().getCodApp()))) {
//      target = CostantiWizard.ERROR_IMPORT_REPORT;
//      messageKey = "errors.genric.import.codAppErrato";
//      logger.error(this.resBundleGenerale.getString(messageKey));
//      this.aggiungiMessaggio(request, messageKey);
//      continua = false;
//    }
    // ora si esegue la forzatura del codice applicazione nel report importato
    contenitoreReport.getDatiGenerali().setCodApp(codiceApplicativo);

    // l'import di un report in profili diversi non è permesso
    String codiceProfilo = (String) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);
    // F.D. 11/09/08 WE128: si può importare un report esportato con un profilo diverso
//    if (continua
//        && (!codiceProfilo.equals(contenitoreReport.getDatiGenerali().getProfiloOwner()))) {
//      target = CostantiWizard.ERROR_IMPORT_REPORT;
//      messageKey = "errors.genric.import.reportProfiloDiversoDaProfiloAttivo";
//      logger.error(this.resBundleGenerale.getString(messageKey));
//      this.aggiungiMessaggio(request, messageKey);
//      continua = false;
//    }
    // ora si esegue la forzatura del codice profilo nel report importato
    contenitoreReport.getDatiGenerali().setProfiloOwner(codiceProfilo);

    //if (continua) {
    if (CostantiGenRicerche.REPORT_SQL != contenitoreReport.getDatiGenerali().getFamiglia().intValue()) {
      int numeroCampi = contenitoreReport.getNumeroCampi();
      CheckReportPerProfilo checkReportPerProfilo = new CheckReportPerProfilo(
          this.geneManager.getGestoreVisibilitaDati(), codiceProfilo, contenitoreReport);

      if (checkReportPerProfilo.isReportEseguibile()) {
        if (numeroCampi != contenitoreReport.getNumeroCampi()) {
          // In questo caso il report e' eseguibile e la definizione e' stata
          // modificata. Segnalare che alcuni campi estratti dal report sono
          // stati rimossi dalla definizione perche' non visibili nel profilo
          // attivo
          messageKey = "warnings.genRic.import.reportImportabileModificato";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }
      } else {
        // In questo caso il report NON e' eseguibile e la definizione e' stata
        // modificata, quindi il report non può essere importato
        target = CostantiWizard.ERROR_IMPORT_REPORT;
        messageKey = "errors.genRic.import.reportNonImportabile";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
        //continua = false;
      }
    }

    if (logger.isDebugEnabled()) {
      logger.debug("checkReportBaseAvanzatoSql: fine metodo");
    }

    return target;
  }

  /**
   * Metodo per la verifica che la entita' sia definita nella base dati. In caso
   * di esito positivo viene prima aggiornato il codApp del report ed inserito
   * in sessione il report stesso
   *
   * @param request
   * @param target
   * @param contenitoreProspetto
   * @return Ritorna il target della action in funzione dell'operazione
   *         effettuata
   */
  private String checkReportProspetto(HttpServletRequest request,
      ContenitoreDatiProspetto contenitoreProspetto) {

    if (logger.isDebugEnabled()) {
      logger.debug("checkReportProspetto: inizio metodo");
    }

    String target = null;
    String messageKey = null;

    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();

    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    boolean continua = true;

    String codiceApplicativo = (String) request.getSession().getAttribute(
        CostantiGenerali.MODULO_ATTIVO);
    // S.S. 01/10/2008 WE128: si può importare un report esportato anche con un applicativo diverso
//    if (continua
//        && (!codiceApplicativo.equals(contenitoreProspetto.getDatiGenProspetto().getDatiGenRicerca().getCodApp()) || !codiceApplicativo.equals(contenitoreProspetto.getDatiGenProspetto().getDatiModello().getCodiceApplicativo()))) {
//      target = CostantiWizard.ERROR_IMPORT_REPORT;
//      messageKey = "errors.genric.import.codAppErrato";
//      logger.error(this.resBundleGenerale.getString(messageKey));
//      this.aggiungiMessaggio(request, messageKey);
//      continua = false;
//    }
    // ora si esegue la forzatura del codice applicazione nel report importato
    contenitoreProspetto.getDatiGenProspetto().getDatiGenRicerca().setCodApp(codiceApplicativo);
    contenitoreProspetto.getDatiGenProspetto().getDatiModello().setCodiceApplicativo(codiceApplicativo);

    // l'import di un report in profili diversi non è permesso
    String codiceProfilo = (String) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);
//  F.D. 11/09/08 WE128: si può importare un report esportato con un profilo diverso
//    if (continua
//        && (!codiceProfilo.equals(contenitoreProspetto.getDatiGenProspetto().getDatiGenRicerca().getProfiloOwner()) || !codiceProfilo.equals(contenitoreProspetto.getDatiGenProspetto().getDatiModello().getProfiloOwner()))) {
//      target = CostantiWizard.ERROR_IMPORT_REPORT;
//      messageKey = "errors.genric.import.reportProfiloDiversoDaProfiloAttivo";
//      logger.error(this.resBundleGenerale.getString(messageKey));
//      this.aggiungiMessaggio(request, messageKey);
//      continua = false;
//    }
    // ora si esegue la forzatura del codice profilo nel report importato
    contenitoreProspetto.getDatiGenProspetto().getDatiGenRicerca().setProfiloOwner(codiceProfilo);
    contenitoreProspetto.getDatiGenProspetto().getDatiModello().setProfiloOwner(codiceProfilo);

    if (continua) {
      String nomeEntitaPrincipale = contenitoreProspetto.getDatiGenProspetto().getDatiGenRicerca().getEntPrinc();
      if (nomeEntitaPrincipale != null) {
        // caso standard, ovvero report con modello collegato al db
        Tabella entitaPrincipale = dizTabelle.getDaNomeTabella(nomeEntitaPrincipale);

        if (!this.geneManager.getGestoreVisibilitaDati().checkEntitaVisibile(
            entitaPrincipale, codiceProfilo)) {
          target = CostantiWizard.ERROR_IMPORT_REPORT;
          messageKey = "errors.genric.import.prospettoNonImportabile.argomento";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
          continua = false;
        }
      } else {
        // caso speciale di report con modello collegato a sorgente dati report base/avanzato
        TrovaRicerche trovaRicerche = new TrovaRicerche();
        trovaRicerche.setCodiceApplicazione(codiceApplicativo);
        trovaRicerche.setProfiloOwner(codiceProfilo);
        trovaRicerche.setNomeRicerca(contenitoreProspetto.getNomeReportSorgente());
        Vector<Integer> famiglia = new Vector<Integer>();
        famiglia.add(contenitoreProspetto.getFamigliaReportSorgente());
        trovaRicerche.setFamiglia(famiglia);
        try {
          List<?> ricercaSorgente = this.ricercheManager.getRicerche(trovaRicerche, true);
          if (ricercaSorgente.size() == 0) {
            target = CostantiWizard.ERROR_IMPORT_REPORT;
            messageKey = "errors.genric.import.prospettoNonImportabile.reportSorgente";
            logger.error(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
            continua = false;
          } else {
            // si aggiorna l'id ricerca con quello definito nel seguente profilo e applicativo
            contenitoreProspetto.getDatiGenProspetto().getDatiModello().setIdRicercaSrc(
                new Integer(
                    ((RicercaGruppo) ricercaSorgente.get(0)).getIdRicerca()));
          }
        } catch (SqlComposerException e) {
          // non si verificherà mai, l'applicativo non parte se il DB e'
          // indicato errato
        }
      }
    }
    // controllo se i parametri del prospetto sono associati a campi tabellati visibili
    Vector<ParametroModello> parametriProspetto = contenitoreProspetto.getElencoParametri();
    if (continua) {
      // controllo i parametri del modello se ci sono
      if (parametriProspetto != null && parametriProspetto.size() > 0) {
        // per ogni parametro di tipo tabellato controllo se il profilo attuale
        // ha il permesso di visualizzazione del campo se no blocco
        // l'importazione
        for (Iterator<?> iter = parametriProspetto.iterator(); iter.hasNext() && continua;) {
          ParametroModello element = (ParametroModello) iter.next();

          if ("T".equalsIgnoreCase(element.getTipo())) {
            Campo campo = dizCampi.get(element.getTabellato());
            if (campo != null) {
              if (!this.geneManager.getGestoreVisibilitaDati().checkCampoVisibile(
                  campo, codiceProfilo)) {
                target = CostantiWizard.ERROR_IMPORT_REPORT;
                messageKey = "errors.genRic.import.parametriTabellatiNonVisibili";
                logger.error(this.resBundleGenerale.getString(messageKey));
                this.aggiungiMessaggio(request, messageKey);
                continua = false;
              }
            } else {
              target = CostantiWizard.ERROR_IMPORT_REPORT;
              messageKey = "errors.genRic.import.tabellatiParametriErrati";
              logger.error(this.resBundleGenerale.getString(messageKey));
              this.aggiungiMessaggio(request, messageKey);
              continua = false;
            }
          }
        }
      }
    }

    if (logger.isDebugEnabled()) {
      logger.debug("checkReportProspetto: fine metodo");
    }

    return target;
  }

}