package it.eldasoft.gene.tags.utils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

public class SqlSelectTag extends BodyTagSupportGene {

  /**
   * 
   */
  private static final long serialVersionUID = -1019978957263022616L;

  private String            nome             = null;

  private String            parametri        = null;

  private HashMap           pars             = null;

  private String            tipoOut          = null;

  public int doStartTag() throws JspException {
    // Se i parametri sono settati setto nel request i parametri
    if (this.getParametri() != null) {
      this.pars = UtilityTags.stringParamsToHashMap(parametri, null);
      this.pageContext.setAttribute("parametri", this.pars,
          PageContext.REQUEST_SCOPE);
    } else
      this.pars = null;
    super.doStartTag();
    return EVAL_BODY_BUFFERED;
  }

  public static HashMap convertHasMapString(HashMap map){
    if(map!=null){
      for (Iterator kIter = map.keySet().iterator(); kIter.hasNext();) {
        Object obj = kIter.next();
        Object val = map.get(obj);
        if (val == null) {
          map.put(obj, "");
        } else {
          map.put(obj, val.toString());
        }
      }
    }
    return map;
  }
  public int doAfterBody() throws JspException {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 20/10/2006 M.F. Converto la hashMap in stringhe
    // ************************************************************

    BodyContent lBody = this.getBodyContent();
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    if (lBody != null) {
      String sql;
      Vector params = new Vector();

      sql = lBody.getString().trim();
      if (this.pars != null) {
        sql = UtilityTags.replaceParametri(params, sql, this.pars);
        this.pageContext.removeAttribute("parametri", PageContext.REQUEST_SCOPE);
      }
      Object oParams[] = UtilityTags.vectorParamToObjectArray(params);

      // A questo punto eseguo l'SQL di selezione
      try {
        Object retVal = null;
        if (this.tipoOut != null && tipoOut.toUpperCase().equals("HASHMAP")) {
          retVal=sqlManager.getHashMap(sql, oParams);
        } else if (this.tipoOut != null && tipoOut.toUpperCase().equals("VECTOR")) {
          retVal=sqlManager.getVector(sql, oParams);
        } else if (this.tipoOut != null && tipoOut.toUpperCase().equals("VECTORSTRING")) {
          retVal=convertVectorString(sqlManager.getVector(sql, oParams));
        } else {
          retVal=convertHasMapString(sqlManager.getHashMap(sql, oParams));
        }
        this.pageContext.setAttribute(this.getNome(), retVal,
            PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException(e.getMessage(), e);
      }

      lBody.clearBody();
    }
    this.parametri = null;
    this.pars = null;
    return super.doAfterBody();
  }
  
  public static Vector convertVectorString(Vector vector) {
    if(vector!=null){
      for(int i=0;i<vector.size();i++){
        if(vector.get(i)==null){
          vector.set(i,"");
        }else
          vector.set(i,vector.get(i).toString());
      }
    }
    return vector;
  }

  /**
   * @return Returns the name.
   */
  public String getNome() {
    return nome;
  }

  /**
   * @param name
   *        The name to set.
   */
  public void setNome(String name) {
    this.nome = name;
  }

  /**
   * @return Returns the parametri.
   */
  public String getParametri() {
    return parametri;
  }

  /**
   * @param parametri
   *        The parametri to set.
   */
  public void setParametri(String parametri) {
    this.parametri = parametri;
  }

  public String getTipoOut() {
    return tipoOut;
  }

  public void setTipoOut(String tipoOut) {
    this.tipoOut = tipoOut;
  }

}
