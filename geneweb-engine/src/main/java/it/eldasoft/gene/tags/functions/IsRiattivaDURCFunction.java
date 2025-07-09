/*
 * Created on 22-01-2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Viene effettuato il controllo sulla presenza dell'integrazione e
 * Questa funzione viene usata dall’utente nei casi in cui si voglia forzare la richiesta per CF 
 * con un precedente annullamento (campo: esitoRichiestaDurc) in stato (8) o con esito irregolare (1) o per i quali il CF non risulta ad INPS (3)
 * 
 * @author Alex Mancini
 */

public class IsRiattivaDURCFunction extends AbstractFunzioneTag {

  public IsRiattivaDURCFunction() {
    super(2, new Class[] { PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String wsUrlRichiestaDurc = ConfigManager.getValore("richiesta.durc.ws.client.url");
    String wsUrlVerificaRegolarita = ConfigManager.getValore("verifica.regolarita.ws.client.url");

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String codImpresa = (String) params[1];

    String result = "0";

    if (wsUrlRichiestaDurc != null && !wsUrlRichiestaDurc.isEmpty() && wsUrlVerificaRegolarita != null && !wsUrlVerificaRegolarita.isEmpty()){
      if (codImpresa != null && !codImpresa.isEmpty()){
        String select="select distinct i1.codimp, i1.cfimp "
            + "from impr i1 "
            + "    join impdurc imd1 on imd1.codimp = i1.codimp "
            + "    join ditg d on d.dittao=i1.codimp "
            + "    join garealbo e on e.codgar=d.codgar5 and e.ngara=d.ngara5 "
            + "    join( "
            + "        select MAX(datric) AS datric2, codimp AS codimp2 "
            + "        from impdurc "
            + "        GROUP BY codimp "
            + "    ) imd2 on imd1.datric = imd2.datric2 and imd1.codimp = imd2.codimp2 "
            + "where imd1.tipo_richiesta = 2 "
            + "and imd1.esito_richiesta_durc in ('1','3','8') "
            + "and imd1.codimp = ?";
        try {
          Vector datiImpdurcByImpresa = sqlManager.getVector(select, new Object[] { codImpresa });
          if(datiImpdurcByImpresa!=null && !datiImpdurcByImpresa.isEmpty()){
            result = "1";
          }
        } catch (SQLException e) {
          throw new JspException(
              "Errore durante la lettura della tabella impdurc ", e);
        }
      }
    }

    return result;
  }
}
