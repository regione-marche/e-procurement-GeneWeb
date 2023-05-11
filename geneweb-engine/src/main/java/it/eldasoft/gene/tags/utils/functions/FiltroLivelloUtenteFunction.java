/*
 * Created on 17-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.utils.functions;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.profiles.FiltroLivelloUtente;
import it.eldasoft.utils.profiles.cache.DizionarioLivelli;
import it.eldasoft.utils.profiles.domain.Livello;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Classe che realizza una funzione per ottenere la query SQL per effettuare un
 * filtro sul livello utente per accedere alle informazioni a lui visibili
 *
 * @author Stefano.Sabbadin
 */
public class FiltroLivelloUtenteFunction extends AbstractFunzioneTag {

  public FiltroLivelloUtenteFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  /**
   * @see it.eldasoft.gene.tags.utils.AbstractFunzioneTag#function(javax.servlet.jsp.PageContext,
   *      java.lang.Object[])
   */
  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String entita = (String) params[1];
    ProfiloUtente profiloUtente = (ProfiloUtente) pageContext.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    return getFiltroLivelloUtente(entita, profiloUtente);
  }

  /**
   * @param entita
   * @param profiloUtente
   * @return
   */
  public static String getFiltroLivelloUtente(String entita, ProfiloUtente profiloUtente) {
    // si verifica se è definito un filtro sul livello utente per l'entità
    String filtro = "";
    if (DizionarioLivelli.getInstance().isFiltroLivelloPresente(entita)) {
      // in caso affermativo, si costruisce il filtro sulla base dell'utente
      FiltroLivelloUtente filtroUtente = profiloUtente.getFiltroLivelloUtente();
      // si indica inoltre l'entità per cui eseguire il filtro
      Livello livello = DizionarioLivelli.getInstance().get(entita);
      filtroUtente.setLivello(livello, entita);
      // si genera la stringa da aggiungere alla clausola where opportunamente
      // valorizzata con il valore del filtro da applicare
      if (filtroUtente.getCondizione() != null) {
        filtro = filtroUtente.getCondizione().toString();
        filtro = UtilityStringhe.replace(filtro, "?",
            Integer.toString(filtroUtente.getValore()));
      }
    }
    return filtro;
  }

}