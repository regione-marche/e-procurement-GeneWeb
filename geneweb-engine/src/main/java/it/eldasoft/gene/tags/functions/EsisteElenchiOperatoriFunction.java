/*
 * Created on 08/08/12
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che verifica la condizione per cui visualizzare la pagina
 * 'Categorie d'iscrizione elenco operatori' nell'anagrafica impresa:
 * la pagina viene visualizzata se si tratta dell'applicativo PG e se l'entità GAREALBO è popolata
 *
 * @author Sara Santi
 */
public class EsisteElenchiOperatoriFunction extends AbstractFunzioneTag {

  public EsisteElenchiOperatoriFunction() {
    super(1, new Class[] { PageContext.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String esito="false";
    String moduloAttivo = (String) pageContext.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);

    if ("PG".equals(moduloAttivo)){
      try {
        Long numElenchi = (Long) sqlManager.getObject("select count(codgar) from garealbo",null);
        if(numElenchi!=null && numElenchi.longValue()>0){
          esito="true";
        }
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante i controlli per determinare se esistono degli elenchi operatori", e);
      }
    }

    return esito;

  }

}
