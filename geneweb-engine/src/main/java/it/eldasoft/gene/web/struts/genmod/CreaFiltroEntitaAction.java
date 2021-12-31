/*
 * Created on 27-lug-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per l'inizializzazione della popup per la creazione della condizione
 * di filtro sull'entita' selezionata. La condizione viene usata in fase di
 * apertura della lista dei modelli predisposti dalla scheda dell'entita', per
 * filtrare i modelli in base alla stato della scheda stessa
 *
 * @author Luca.Giacomazzo
 */
public class CreaFiltroEntitaAction extends AbstractActionBaseGenModelli {

  /* logger della classe */
  static Logger logger = Logger.getLogger(CreaFiltroEntitaAction.class);

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    DizionarioTabelle dizionarioTabelle = DizionarioTabelle.getInstance();

    String entitaSelezionata = request.getParameter("entita");
    Tabella tabella = dizionarioTabelle.getDaNomeTabella(entitaSelezionata);
    List<Campo> elencoCampi = tabella.getCampiKey();

    // Costruzione della query:
    // SELECT count(*) FROM entita WHERE campoKey1 = '?' and campoKey2 = ? ... and
    StringBuffer query = new StringBuffer("select COUNT(*) from ");
    query.append(tabella.getNomeTabella());
    query.append(" where ");

    for (int i = 0; i < elencoCampi.size(); i++) {
      Campo campo = elencoCampi.get(i);
      query.append(campo.getNomeCampo().concat(" = "));

      if(Campo.TIPO_STRINGA == campo.getTipoColonna())
        query.append("'?'");
      else
        query.append("? ");
      query.append(" and ");
    }
    request.setAttribute("sqlSelect", query.toString());

    if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

}