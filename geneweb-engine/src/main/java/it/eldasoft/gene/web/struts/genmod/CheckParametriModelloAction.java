/*
 * Created on 4-dic-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiIntegrazioneKronos;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Esegue la verifica per il modello scelto della necessità di inserire dei
 * parametri a runtime. In caso affermativo, carica le definizioni dei parametri
 * nel request in modo da essere proposto l'inserimento del dato prima
 * dell'elaborazione del modello
 * 
 * @author Stefano.Sabbadin
 * 
 */
public class CheckParametriModelloAction extends ActionBaseNoOpzioni {

  /** logger della classe */
  static Logger            logger                      = Logger.getLogger(CheckParametriModelloAction.class);

  private static String    FORWARD_ERROR_LISTA_MODELLI = "errorListaModelli";
  /** Manager dei modelli */
  private ModelliManager   modelliManager;

  private TabellatiManager tabellatiManager;

  private GeneManager      geneManager;

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * @param tabellatiManager
   *        The tabellatiManager to set.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param modelliManager
   *        The modelliManager to set.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = "componi";
    String messageKey = null;

    ComponiModelloForm componiModelloForm = (ComponiModelloForm) form;

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    String codiceProfilo = (String) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);
    boolean continua = true;
    String valore = null;
    Vector listaValori = new Vector();

    try {
      // si legge il tipo del modello per utilizzarlo per eventuali integrazioni
      DatiModello datiModello = this.modelliManager.getModelloById(componiModelloForm.getIdModello());
      componiModelloForm.setTipo(datiModello.getTipoModello());
      // si leggono i parametri
      List elencoParametri = this.modelliManager.getParametriModello(componiModelloForm.getIdModello());
      if (elencoParametri != null && elencoParametri.size() > 0) {
        for(int j=0; j < elencoParametri.size(); j++){
          ParametroModello parametroModello = (ParametroModello) elencoParametri.get(j);
          parametroModello.setNome(UtilityStringhe.convStringHTML(
              parametroModello.getNome()));
        }
        if (elencoParametri.size() > 1) {
          target = "setParametri";

          request.setAttribute("listaParametri", elencoParametri);

          // Inserimento identificativo utente nel request per popolare il
          // parametro hidden nella lista dei parametri per comporre il modello
          // stesso
          request.setAttribute("idAccount", new Integer(profiloUtente.getId()));
          // cicliamo sulla lista dei parametri e per ogni parametro di tipo
          // Tabellato mettiamo
          // nel request la lista degli elementi, il nome della lista sarà
          // composto da "lista" + codice tabellato
          Iterator iteratorElencoParametri = elencoParametri.iterator();
          ParametroModello parametro = null;
          List listaTabellato = null;
          String nomeLista = "lista";
          while (iteratorElencoParametri.hasNext() && continua) {
            parametro = (ParametroModello) iteratorElencoParametri.next();
            valore = this.modelliManager.getCacheParametroModello(
                profiloUtente.getId(), componiModelloForm.getIdModello(),
                parametro.getCodice());

            if ("T".equalsIgnoreCase(parametro.getTipo())) {

              // nell'attributo tabellato troviamo il mnemonico del campo
              // associato al
              // tabellato che dobbiamo presentare
              Campo campo = dizCampi.get(parametro.getTabellato());
              if (campo != null) {
                if (this.checkMnemoniciParametriTabellati(campo, codiceProfilo)) {
                  listaTabellato = tabellatiManager.getTabellato(campo.getCodiceTabellato());

                  request.setAttribute(nomeLista + parametro.getTabellato(),
                      listaTabellato);

                } else {
                  target = FORWARD_ERROR_LISTA_MODELLI;
                  messageKey = "errors.modelli.componi.parametriNonVisibili";
                  logger.error(this.resBundleGenerale.getString(messageKey));
                  ActionBase.aggiungiMessaggioInSessione(request, messageKey, null);
                  continua = false;
                }
              } else {
                target = FORWARD_ERROR_LISTA_MODELLI;
                messageKey = "errors.modelli.componi.parametriErrati";
                logger.error(this.resBundleGenerale.getString(messageKey));
                ActionBase.aggiungiMessaggioInSessione(request, messageKey, null);
                continua = false;
              }
            }
            if ("D".equalsIgnoreCase(parametro.getTipo()))
              valore = UtilityStringhe.replace(valore, ".", "/");
            if ("F".equals(parametro.getTipo()))
              valore = UtilityStringhe.replace(valore, ".", ",");

            listaValori.add(valore);
          }

        } else {
          // Il modello richiede un solo parametro: bisogna controllare se
          // quell'unico parametro e' di tipo U (cioè Identificativo utente) o
          // meno. Se il parametro e' di tipo U, bisogna inserire nel request
          // l'attributo ParametriModelloForm contentente nell'attributo
          // parametriModello (di tipo String[]) l'idAccount dell'utente
          // che sta lanciando la composizione del modello
          ParametroModello defParametro = (ParametroModello) elencoParametri.get(0);
          if ("U".equals(defParametro.getTipo())) {
            target = "salvaEComponi";

            ParametriModelloForm parametriModelloForm = new ParametriModelloForm(
                componiModelloForm);
            String[] tmp = new String[] { "" + profiloUtente.getId() };

            // Set dell'unico parametro nell'array dei valori dei parametri
            parametriModelloForm.setParametriModello(tmp);

            // new String[]{"" + profiloUtente.getId()});
            request.setAttribute("parametriModelloconIdUtenteForm",
                parametriModelloForm);
          } else {
            target = "setParametri";

            request.setAttribute("listaParametri", elencoParametri);

            valore = this.modelliManager.getCacheParametroModello(
                profiloUtente.getId(), componiModelloForm.getIdModello(),
                defParametro.getCodice());
            listaValori.add(valore);

            // Inserimento identificativo utente nel request per popolare il
            // parametro hidden nella lista dei parametri per comporre il
            // modello
            // stesso
            request.setAttribute("idAccount",
                new Integer(profiloUtente.getId()));

            // se il parametro è di tipo Tabellato mettiamo
            // nel request la lista degli elementi, il nome della lista sarà
            // composto da "lista" + codice tabellato

            List listaTabellato = null;
            String nomeLista = "lista";

            if ("T".equalsIgnoreCase(defParametro.getTipo())) {

              // nell'attributo tabellato troviamo il mnemonico del campo
              // associato al
              // tabellato che dobbiamo presentare
              Campo campo = dizCampi.get(defParametro.getTabellato());
              if (campo != null) {
                if (this.checkMnemoniciParametriTabellati(campo, codiceProfilo)) {
                  listaTabellato = tabellatiManager.getTabellato(campo.getCodiceTabellato());

                  request.setAttribute(nomeLista + defParametro.getTabellato(),
                      listaTabellato);
                } else {
                  target = FORWARD_ERROR_LISTA_MODELLI;
                  messageKey = "errors.modelli.componi.parametriNonVisibili";
                  logger.error(this.resBundleGenerale.getString(messageKey));
                  ActionBase.aggiungiMessaggioInSessione(request, messageKey, null);
                  continua = false;
                }
              } else {
                target = FORWARD_ERROR_LISTA_MODELLI;
                messageKey = "errors.modelli.componi.parametrierrati";
                logger.error(this.resBundleGenerale.getString(messageKey));
                ActionBase.aggiungiMessaggioInSessione(request, messageKey, null);
                continua = false;
              }
            }

          }
        }
        componiModelloForm.setIdSessione(0);
      } else {
        // SS 20090309
        // siamo nel caso di nessun parametro richiesto, ma potrebbe essere un
        // modello riepilogativo per cui vanno inseriti i parametri
        if (componiModelloForm.getRiepilogativo() == 1) {
          target = "salvaEComponi";

          ParametriModelloForm parametriModelloForm = new ParametriModelloForm(
              componiModelloForm);
          String[] tmp = new String[] {};
          parametriModelloForm.setParametriModello(tmp);
          // il nome dell'attributo è infelice, si usa lo stesso attributo per
          // il caso di modello con solo id utente, ma torna utile per andare
          // all'azione di salvataggio per eseguire il salvataggio dei dati
          // delle chiavi
          request.setAttribute("parametriModelloconIdUtenteForm",
              parametriModelloForm);
        }
      }

      // SS 20090311: inserita la gestione della lettura dei dati per
      // l'integrazione di KRONOS
      if (continua) {
        target = this.checkIntegrazioneKronos(request,
            componiModelloForm.getIdModello(), componiModelloForm.getTipo(),
            profiloUtente.getId(), target);
      }

      if (continua) {
        request.setAttribute(
            CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_COMPOSIZIONE,
            componiModelloForm);
      }
      
      // se si va alla pagina di inserimento parametri, allora si inseriscono i
      // valori precompilati da proporre
      if (target.equals("setParametri"))
        request.setAttribute("listaValori", listaValori);

    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);

    } catch (SQLException e) {
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

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

  private boolean checkMnemoniciParametriTabellati(Campo campo,
      String codiceProfilo) {
    boolean continua = true;

    if (!this.geneManager.getGestoreVisibilitaDati().checkCampoVisibile(campo,
        codiceProfilo)) {
      continua = false;
    }

    return continua;
  }

  /**
   * Verifica se l'applicativo prevede l'integrazione con KRONOS, per cui va ad
   * inserire nel request un attributo in modo da abilitare determinate parti
   * nella pagina di inserimento parametri, nonchè si leggono i valori dei
   * parametri specifici per tale integrazione, che non sono censiti nei
   * parametri standard, ma che si necessitano nell'inserimento dei parametri
   * 
   * @param request
   *        request HTTP
   * @param idModello
   *        id del modello
   * @param tipoModello
   *        tipo del modello
   * @param idUtente
   *        id dell'utente
   * @param target
   *        target Struts, che verrà modificato nel caso di integrazione con
   *        Kronos
   * @return target modificato a "setParametri" se è prevista l'integrazione con
   *         Kronos
   * @throws SQLException
   */
  private String checkIntegrazioneKronos(HttpServletRequest request,
      int idModello, String tipoModello, int idUtente, String target)
      throws SQLException {
    // verifica se si tratta dell'integrazione kronos
    if (CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS.equals(ConfigManager.getValore(CostantiGenerali.PROP_INTEGRAZIONE))
        && CostantiIntegrazioneKronos.TIPO_MODELLO_GIUSTIFICATIVO_KRONOS.equals(tipoModello)) {
      target = "setParametri";
      request.setAttribute(CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS, "1");
      // si passa alla lettura dal DB dei valori impostati l'ultima volta per i
      // parametri
      String valore = null;
      // parametro data inizio periodo
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello,
          CostantiIntegrazioneKronos.PARAM_MODELLO_DATA_INIZIO_PERIODO);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_MODELLO_DATA_INIZIO_PERIODO,
          UtilityStringhe.replace(valore, ".", "/"));
      // parametro data fine periodo
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello, CostantiIntegrazioneKronos.PARAM_MODELLO_DATA_FINE_PERIODO);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_MODELLO_DATA_FINE_PERIODO,
          UtilityStringhe.replace(valore, ".", "/"));
      // parametro raggruppamento
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello, CostantiIntegrazioneKronos.PARAM_MODELLO_RAGGRUPPAMENTO);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_MODELLO_RAGGRUPPAMENTO, valore);
      // parametro gruppo
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello, CostantiIntegrazioneKronos.PARAM_MODELLO_GRUPPO);
      request.setAttribute(CostantiIntegrazioneKronos.PARAM_MODELLO_GRUPPO,
          valore);
      // parametro giustificativo
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello, CostantiIntegrazioneKronos.PARAM_MODELLO_GIUSTIFICATIVO);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_MODELLO_GIUSTIFICATIVO, valore);
      // parametro escludi sabati
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello, CostantiIntegrazioneKronos.PARAM_MODELLO_ESCLUDI_SABATI);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_MODELLO_ESCLUDI_SABATI, valore);
      // parametro escludi domeniche
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello, CostantiIntegrazioneKronos.PARAM_MODELLO_ESCLUDI_DOMENICHE);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_MODELLO_ESCLUDI_DOMENICHE, valore);
      // parametro escludi feste
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello, CostantiIntegrazioneKronos.PARAM_MODELLO_ESCLUDI_FESTE);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_MODELLO_ESCLUDI_FESTE, valore);
      // parametro dettaglio dipendente
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello,
          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_DETT_DIPENDENTE);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_DETT_DIPENDENTE, valore);
      // parametro totali generali
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello,
          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_TOTALI_GENERALI);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_TOTALI_GENERALI, valore);
      // parametro totali dipendente
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello,
          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_TOTALI_DIPENDENTE);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_TOTALI_DIPENDENTE,
          valore);
      // parametro raggruppa giustificativo
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello,
          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_RAGGRUPPA_GIUSTIFICATIVO);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_RAGGRUPPA_GIUSTIFICATIVO,
          valore);
      // parametro descrizione turno
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello,
          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_DESCRIZIONE_TURNO);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_DESCRIZIONE_TURNO,
          valore);
//      // parametro conteggio a mesi
//      valore = this.modelliManager.getCacheParametroModello(idUtente,
//          idModello,
//          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_CONTEGGIO_MESI);
//      request.setAttribute(
//          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_CONTEGGIO_MESI, valore);
      // parametro giustificativo con note
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello,
          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_GIUSTIFICATIVO_NOTE);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_GIUSTIFICATIVO_NOTE,
          valore);
      // parametro mostra note
      valore = this.modelliManager.getCacheParametroModello(idUtente,
          idModello, CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_MOSTRA_NOTE);
      request.setAttribute(
          CostantiIntegrazioneKronos.PARAM_MODELLO_OPZ_MOSTRA_NOTE, valore);

      // valorizzazione lista raggruppamenti
      List listaRaggruppamenti = this.geneManager.getSql().getElencoTabellati(
          "select COD_RAGGR, DES_RAGGR from V_KRAGGRUPP order by DES_RAGGR", null);
      request.setAttribute("listaRaggruppamenti", listaRaggruppamenti);
      
      // valorizzazione lista gruppi
      List listaGruppi = this.geneManager.getSql().getElencoTabellati(
          "select distinct COD_GRPACCUMP, DES_GRPACCUMP from V_KGRUPPO order by DES_GRPACCUMP",
          null);
      request.setAttribute("listaGruppi", listaGruppi);
      
      // valorizzazione lista giustificativi
      List listaGiustificativi = this.geneManager.getSql().getElencoTabellati(
          "select COD_GIUSTIF, DES_GIUSTIF from V_KDD_GIUSTIF order by DES_GIUSTIF",
          null);
      request.setAttribute("listaGiustificativi", listaGiustificativi);
    }
    return target;
  }

}
