/*
 * Created on 01/mar/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.tags.gestori.decoratori;


import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato;
import it.eldasoft.gene.web.struts.admin.AccountAction;
import it.eldasoft.utils.spring.UtilitySpring;


public class GestoreCampoCodEvento extends AbstractGestoreCampoTabellato {

  static Logger logger = Logger.getLogger(AccountAction.class);
  
  public GestoreCampoCodEvento() {
    super(false,"T40");
  }
  
  @Override
  protected void initGestore() {
    super.initGestore();
    
    String cod;
    String moduloAttivo = (String) this.getPageContext().getAttribute("moduloAttivo",
        PageContext.SESSION_SCOPE);
    String selectQuery = "SELECT DISTINCT CODEVENTO FROM W_LOGEVENTI WHERE CODAPP=? ORDER BY CODEVENTO";
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getPageContext(), SqlManager.class);

    try {

      List<?> tab = sqlManager.getListVector(selectQuery,new Object[]{moduloAttivo} );
      //eseguo la query sulla tabella w_profili per ottenere tutti i valori che poi scriverò nel file
      if (tab != null && tab.size() > 0) {
        logger.debug("QUERY SIZE "+ tab.size());
        logger.debug("TROVATO: "+tab.get(0).toString());
        for (int i = 0; i < tab.size(); i++) {
          cod = (String) SqlManager.getValueFromVectorParam(
              tab.get(i), 0).getValue();
          logger.debug("QUERY OK : "+ cod);
          this.getCampo().addValore(cod, cod);
        }
    } 
    } catch (SQLException e) {
      logger.error("Errore nell'estrazione dei codici evento", e);
     }
  }

  @Override
  public SqlSelect getSql() {
    
    return null;
  }

}
