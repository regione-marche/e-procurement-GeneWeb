package it.eldasoft.gene.tags.functions;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.integrazioni.QformManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;


public class GestionePreviewParametriQformFunction extends AbstractFunzioneTag {

  public GestionePreviewParametriQformFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    QformManager qformManager = (QformManager) UtilitySpring.getBean("qformManager",
        pageContext, QformManager.class);

    String idPreview = (String) params[1];
    String id = (String) params[2];
    String ent = (String) params[3];
    try {
      String select=null;
      Object par[]=null;

      if(idPreview!=null && !"".equals(idPreview)) {
        select ="select DESCRI,CHIAVE,VALORE,VALARRAY from QFORMCONFITEMP where idpreview=? order by id";
        par= new Object[] {new Long(idPreview)};
      }else {
        Long genere=qformManager.getGenere(new Long(id), ent);
        select ="select DESCRI,CHIAVE,VALORE,VALARRAY from QFORMCONFI where  (genere=? or genere is null) order by id";
        par=new Object[] {genere};
      }

      List<?> listaParametri = sqlManager.getListVector(select, par);

      if (listaParametri != null && listaParametri.size() > 0) {
        pageContext.setAttribute("parametriImpostati", listaParametri, PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura di QFORMCONFI", e);
    }
    return null;
  }

}