/*
 * Created on 01/07/24
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.campi.gestori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellatoArc;
import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * Gestore del campo IMP_TIPIMP associato al tabellato Ag008.
 * Vengono rimossi dai possibili valori tipimp 3,10 per le ditte che non sono RT e viceversa
 * @author Manuel Bridda
 */
public class GestoreCampoTIPIMP extends AbstractGestoreCampoTabellatoArc {

  public GestoreCampoTIPIMP() {
    super(false, "N7");
  }

  /**
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato#getSql()
   */
  @Override
  public SqlSelect getSql() {
      String select="select tab1tip,tab1desc,tab1arc from tab1 where tab1cod='Ag008' order by tab1nord,tab1tip";

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
              this.getPageContext(), SqlManager.class);
      String codimp= (String) this.getPageContext().getRequest().getAttribute("codimp");
      Long tipimp = null;
      try {
          tipimp = (Long) sqlManager.getObject("select tipimp from impr where codimp=?", new Object[]{codimp});
          if(tipimp!=null) {
              if (Arrays.asList(3L,10L).contains(tipimp))
                  select = "select tab1tip,tab1desc,tab1arc from tab1 where tab1cod='Ag008' and tab1tip in (3,10) order by tab1nord,tab1tip";
              else
                  select = "select tab1tip,tab1desc,tab1arc from tab1 where tab1cod='Ag008' and tab1tip not in (3,10) order by tab1nord,tab1tip";
          }
      } catch (SQLException e) {
          throw new RuntimeException(e);
      }

    return new SqlSelect(select, null);
  }
}
