/*
 * Created on 15-feb-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.admin.ProfiliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.ProfiloAccount;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Ritorna l'elenco degli applicativi configurati da properties per l'accesso a
 * partire dall'applicativo stesso. Nel caso sia specificato anche il codice
 * applicazione, si ipotizza di lavorare sullo stesso DB per cui si estrae
 * l'elenco dei profili collegati all'utente in tale applicativo per proporne
 * l'elenco.
 * 
 * @author Stefano.Cestaro
 * @since 1.4.6
 */
public class ListaApplicativiAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger          logger = Logger.getLogger(ListaApplicativiAction.class);

  private ProfiliManager profiliManager;

  /**
   * 
   * @param Ritorna
   *        profiliManager
   */
  public void setProfiliManager(ProfiliManager profiliManager) {
    this.profiliManager = profiliManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;

    // Informazioni sull'utente connesso
    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    List<Object> listaApplicativiProfili = new Vector<Object>();

    // la property con il numero di applicativi e' controllata in fase di
    // startup, per cui la presente action è disponibile solo se il dato è
    // valido. Di conseguenza è impossibile che si verifichi un'eccezione
    // NumberFormaException
    Long numeroApplicativi = new Long(
        ConfigManager.getValore(CostantiGenerali.PROP_ACCESSO_ALTRO_APPLICATIVO_NUMERO));
    if (numeroApplicativi.longValue() > 0) {

      for (int numeroChiave = 0; numeroChiave <= numeroApplicativi.intValue(); numeroChiave++) {

        String descrizioneApplicativo = UtilityStringhe.convertiStringaVuotaInNull(ConfigManager.getValore(CostantiGenerali.PROP_ACCESSO_ALTRO_APPLICATIVO_DESCRIZIONE
            + "."
            + numeroChiave));
        String indirizzoApplicativo = UtilityStringhe.convertiStringaVuotaInNull(ConfigManager.getValore(CostantiGenerali.PROP_ACCESSO_ALTRO_APPLICATIVO_INDIRIZZO
            + "."
            + numeroChiave));
        String codiceApplicativo = UtilityStringhe.convertiStringaVuotaInNull(ConfigManager.getValore(CostantiGenerali.PROP_ACCESSO_ALTRO_APPLICATIVO_CODICE
            + "."
            + numeroChiave));

        if (descrizioneApplicativo != null && indirizzoApplicativo != null) {
          int numeroProfiliAssociati = 0;

          // Caricamento dei profili associati
          if (codiceApplicativo != null) {
            List<ProfiloAccount> listaProfiliUtente = 
              this.profiliManager.getProfiliConAssociazioneUtenteByCodApp(
                profilo.getId(), codiceApplicativo);
            String nomeProfilo = null;
            String descrizioneProfilo = null;
            String codiceProfilo = null;
            for (int i = 0; i < listaProfiliUtente.size(); i++) {
              if (((ProfiloAccount) listaProfiliUtente.get(i)).getAssociato()) {
                numeroProfiliAssociati++;
                if (numeroProfiliAssociati == 1)
                  listaApplicativiProfili.add(((Object) (new Object[] { "T",
                      descrizioneApplicativo, "", "", "" })));
                nomeProfilo = ((ProfiloAccount) listaProfiliUtente.get(i)).getNome();
                descrizioneProfilo = ((ProfiloAccount) listaProfiliUtente.get(i)).getDescrizione();
                codiceProfilo = ((ProfiloAccount) listaProfiliUtente.get(i)).getCodiceProfilo();
                listaApplicativiProfili.add(((Object) (new Object[] { "P",
                    nomeProfilo, descrizioneProfilo, indirizzoApplicativo,
                    codiceProfilo })));
              }
            }
          }

          // Se non ci sono profili associati devo caricare il solo accesso
          // diretto
          if (numeroProfiliAssociati == 0) {
            listaApplicativiProfili.add(((Object) (new Object[] { "I",
                descrizioneApplicativo, "", indirizzoApplicativo, "" })));
          }
        }
      }
    }

    request.setAttribute("listaApplicativiProfili", listaApplicativiProfili);

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

}
