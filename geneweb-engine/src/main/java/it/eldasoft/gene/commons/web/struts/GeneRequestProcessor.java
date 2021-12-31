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
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.attivazione.AttivaApplicazioneAction;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.tiles.TilesRequestProcessor;

/**
 * Estensione del Request Processor Tiles di Struts; verifica che l'applicazione
 * è stata caricata correttamente, altrimenti blocca l'esecuzione
 * dell'operazione richiesta
 *
 * @author Stefano.Sabbadin
 */



public class GeneRequestProcessor extends TilesRequestProcessor {



  /**
   * @see org.apache.struts.action.RequestProcessor#processActionPerform(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse,
   *      org.apache.struts.action.Action, org.apache.struts.action.ActionForm,
   *      org.apache.struts.action.ActionMapping)
   */
  @Override
  protected ActionForward processActionPerform(HttpServletRequest request,
      HttpServletResponse response, Action action, ActionForm form,
      ActionMapping mapping) throws IOException, ServletException {

    ServletContext context = this.getServletContext();
    String appDisponibile = (String)
        context.getAttribute(CostantiGenerali.SENTINELLA_APPLICAZIONE_CARICATA);

    String appBloccata = (String) context.getAttribute(CostantiGenerali.SENTINELLA_BLOCCO_ATTIVAZIONE);
    String actionName = mapping.getType();

    if (appDisponibile == null || !"1".equals(appDisponibile)) {
      String messageKey = "errors.appNotLoaded";
      ActionMessages errors = new ActionMessages();
      errors.add(ActionBase.getTipoMessaggioFromChiave(messageKey), new ActionMessage(messageKey));

      // In questo punto non viene aggiunto un messaggio di errore nel request,
      // bensì viene settato l'unico messaggio che verrà visualizzato al client
      // in seguito la verifica che l'applicazione non è disponibile.
      // N.B.: questa classe non è in relazione con la classe
      // org.apache.struts.action.Action e quindi il metodo protetto saveMessages
      // qui di seguito non e' in relazione con l'analogo metodo della classe stessa
      if (!errors.isEmpty()) this.saveMessages(request, errors);
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_APPLICAZIONE_NON_DISPONIBILE);
    } else if(appBloccata != null && "1".equals(appBloccata) && !actionName.equals((AttivaApplicazioneAction.class).toString().substring(6))){

        // Primo avvio dell'applicativo: si effettua il forward alla pagina di
        // attivazione dell'applicativo passando, eventualmente, i valori gia' presenti in W_ATT
        request.setAttribute("codiceCliente", ConfigManager.getValore(CostantiGenerali.PROP_CODICE_CLIENTE));
        request.setAttribute("acquirenteSW", ConfigManager.getValore(CostantiGenerali.PROP_ACQUIRENTE));
        request.setAttribute("responsabileCliente", ConfigManager.getValore(CostantiGenerali.PROP_RESPONSABILE_CLIENTE));
        request.setAttribute("responsabileClienteEmail", ConfigManager.getValore(CostantiGenerali.PROP_EMAIL_RESPONSABILE_CLIENTE));

        // Prodotto e versione
        request.setAttribute("prodotto", ConfigManager.getValore(CostantiGenerali.PROP_TITOLO_APPLICATIVO));
        String pathFileVersione = CostantiGenerali.PATH_WEBINF + ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE) + "_VER.TXT";
        InputStream inputStreamVersione = context.getResourceAsStream(pathFileVersione);
        if (inputStreamVersione != null) {
          BufferedReader br = new BufferedReader(new InputStreamReader(inputStreamVersione));
          try {
            request.setAttribute("versione", br.readLine());
          } finally {
            br.close();
            inputStreamVersione.close();
          }
        }

        // Lettura di tutte le opzioni disponibile dal file di resource
        String resource = CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE;
        Enumeration<?> enumResource = ResourceBundle.getBundle(resource).getKeys();
        Vector opzioniDisponibiliResource = new Vector();
        while (enumResource.hasMoreElements()) {
          String el = (String) enumResource.nextElement();
          if (el.startsWith("opzione.")) opzioniDisponibiliResource.add(el);
        }

        // Predisposizione delle opzioni, delle descrizioni e settaggio delle opzioni di default o gia' selezionate
        String opzioniDisponibiliDefault = UtilityTags.getResource("opzioni.default", null, false);
        String opzioniDisponibiliSelezionate = ConfigManager.getValore(CostantiGenerali.PROP_OPZIONI_DISPONIBILI);
        String[] arrayConfrontoOpzioniDefaultOSelezionate = null;
        if (opzioniDisponibiliSelezionate != null && !"".equals(opzioniDisponibiliSelezionate)) {
          arrayConfrontoOpzioniDefaultOSelezionate = opzioniDisponibiliSelezionate.split("\\" + CostantiGenerali.SEPARATORE_OPZIONI_LICENZIATE);
        } else if (opzioniDisponibiliDefault != null) {
          arrayConfrontoOpzioniDefaultOSelezionate = opzioniDisponibiliDefault.split("\\" + CostantiGenerali.SEPARATORE_OPZIONI_LICENZIATE);
        }

        List<Object> listaOpzioniSelezionabili = new Vector<Object>();
        for (int opz = 0; opz < opzioniDisponibiliResource.size(); opz++) {
          String opzioneDisponibile = ((String) opzioniDisponibiliResource.get(opz)).substring(8);
          String descrizioneOpzioneDisponibile = UtilityTags.getResource((String) opzioniDisponibiliResource.get(opz), null, false);
          boolean opzioneDefaultOSelezionata = false;
          if (arrayConfrontoOpzioniDefaultOSelezionate != null) {
            if (Arrays.asList(arrayConfrontoOpzioniDefaultOSelezionate).contains(opzioneDisponibile.toUpperCase())) {
              opzioneDefaultOSelezionata = true;
            }
          }
          listaOpzioniSelezionabili.add(new Object[] {opzioneDisponibile.toUpperCase(), descrizioneOpzioneDisponibile, opzioneDefaultOSelezionata});
        }

        request.setAttribute("listaOpzioniSelezionabili", listaOpzioniSelezionabili);


        // Gestione della licenza
        String pathFileLicenza = CostantiGenerali.PATH_WEBINF + CostantiGenerali.NOME_FILE_LICENZA;
        InputStream inputStreamLicenza = context.getResourceAsStream(pathFileLicenza);
        if (inputStreamLicenza != null) {
          BufferedReader br = new BufferedReader(new InputStreamReader(inputStreamLicenza));
          try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
              sb.append(line);
              sb.append("\n");
              line = br.readLine();
            }
            request.setAttribute("licenza", sb.toString());
          } finally {
            br.close();
            inputStreamLicenza.close();
          }
        }

        return mapping.findForward(CostantiGeneraliStruts.FORWARD_APPLICAZIONE_DA_ATTIVARE);
      } else if(appBloccata != null && "1".equals(appBloccata) && actionName.equals((AttivaApplicazioneAction.class).toString().substring(6))) {
        return super.processActionPerform(request, response, action, form, mapping);
      } else {
        return super.processActionPerform(request, response, action, form, mapping);
      }
  }

  protected void saveMessages(HttpServletRequest request, ActionMessages messages) {
    if(messages == null || messages.isEmpty())
    {
        request.removeAttribute("org.apache.struts.action.ACTION_MESSAGE");
        return;
    } else
    {
        request.setAttribute("org.apache.struts.action.ACTION_MESSAGE", messages);
        return;
    }
  }
}
