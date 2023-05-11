package it.eldasoft.gene.tags.gestori.plugin;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore per il caricamento del dettaglio di un record della ST_TRG_DETT (storia delle modifche).
 * 
 * @author Luca.Giacomazzo
 */
public class GestoreDettaglioStoriaModifiche extends AbstractGestorePreload {

  public GestoreDettaglioStoriaModifiche(BodyTagSupportGene tag) {
    super(tag);
  }
  
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", page, SqlManager.class);
    String key = (String) page.getAttribute("key", PageContext.REQUEST_SCOPE);
    
    Long stSeq = Long.parseLong(key.split(":")[1]);
    try {
      List<?> listaCampiModificati = sqlManager.getListHashMap(
          "select ST_CNAME, ST_C_NEWVALUE, ST_C_OLDVALUE from ST_TRG_DETT where ST_SEQ = ? "
           + " and ( (st_c_newvalue is not null and st_c_oldvalue is null) "
           + " or "
           + " (st_c_newvalue is null and st_c_oldvalue is not null) "
           + " or "
           + " (st_c_newvalue <> st_c_oldvalue) )",
          new Object[] { stSeq });
      
      if (listaCampiModificati != null && listaCampiModificati.size() > 0) {
        page.setAttribute("listaCampiModificati", listaCampiModificati, PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrazione del record dalla ST_TRG_DETT con ST_SEQ=" + stSeq, e);
    }
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

}
