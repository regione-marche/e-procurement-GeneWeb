package it.eldasoft.gene.tags.utils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

public class SqlSelectListTag extends BodyTagSupportGene {

  /**
   * 
   */
  private static final long serialVersionUID = -1019978957263022616L;

  private String            nome             = null;
  
  private String            status             = null;

  private String            parametri        = null;

  private HashMap           pars             = null;

  private String            tipoOut          = null;

  private String            sql              = null;

  public int doStartTag() throws JspException {

    // Se i parametri sono settati setto nel request i parametri
    if (this.getParametri() != null) {
      this.pars = UtilityTags.stringParamsToHashMap(parametri, null);
      this.pageContext.setAttribute("parametri", this.pars,
          PageContext.REQUEST_SCOPE);
    } else
      this.pars = null;
    super.doStartTag();
    // Se è impostato l'sql allora eseguo la select a priori
    if (this.getSql() != null) {
      HashMap attr = new HashMap();
      List ret = getValues(pageContext, this.getSql());
      attr.put("vals", ret);
      attr.put("row", new Integer(0));
      attr.put("rowCount", new Integer(ret != null ? ret.size() : 0));
      if(ret!=null && ret.size()>0){
        pageContext.setAttribute(this.getNome(), ret.get(0));
        if(this.getStatus()!=null)
          pageContext.setAttribute(this.getStatus(), attr);
      }else{
        return SKIP_BODY;
      }
      SqlSelectAttributes.getInstance(pageContext).push(attr);
      return EVAL_BODY_INCLUDE;
    } else
      // Aggiungo nello stack un valore vuoto
      SqlSelectAttributes.getInstance(pageContext).push(null);
    return EVAL_BODY_BUFFERED;
  }

  public int doAfterBody() throws JspException {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 20/10/2006 M.F. Converto la hashMap in stringhe
    // ************************************************************
    SqlSelectAttributes att = SqlSelectAttributes.getInstance(pageContext);
    if (att.lastElement() == null) {
      BodyContent lBody = this.getBodyContent();

      if (lBody != null) {
        List ret = this.getValues(pageContext, lBody.getString().trim());
        this.pageContext.setAttribute(this.getNome(), ret,
            PageContext.PAGE_SCOPE);
        lBody.clearBody();
      }
      this.parametri = null;
      this.pars = null;
    } else {
      HashMap map=(HashMap)att.lastElement();
      List valori=(List)map.get("vals");
      Integer row=(Integer)map.get("row");
      // Se si hanno ancora righe allora mi sposto nella prossima e continuo la valutazione
      if((row.intValue()+1)<valori.size()){
        map.put("row", new Integer(row.intValue()+1));
        pageContext.setAttribute(this.getNome(), valori.get(row.intValue()+1) );
        return EVAL_BODY_AGAIN;
      }
    }
    // Elimino l'ultimo valore
    att.pop();
    return super.doAfterBody();
  }

  private List getValues(PageContext pageContext, String sql)
      throws JspException {
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    Vector params = new Vector();

    if (this.pars != null) {
      sql = UtilityTags.replaceParametri(params, sql, this.pars);
      this.pageContext.removeAttribute("parametri", PageContext.REQUEST_SCOPE);
    }
    Object oParams[] = UtilityTags.vectorParamToObjectArray(params);

    // A questo punto eseguo l'SQL di selezione
    try {
      List ret = null;
      if (this.tipoOut != null && tipoOut.toUpperCase().equals("HASHMAP")) {
        ret = sqlManager.getListHashMap(sql, oParams);
      } else if (this.tipoOut != null && tipoOut.toUpperCase().equals("VECTOR")) {
        ret = sqlManager.getListVector(sql, oParams);
      } else if (this.tipoOut != null
          && tipoOut.toUpperCase().equals("VECTORSTRING")) {
        ret = sqlManager.getListVector(sql, oParams);
        // Converto tutti vettori in valori stringa
        for (int i = 0; i < ret.size(); i++) {
          ret.set(i, SqlSelectTag.convertVectorString((Vector) ret.get(i)));
        }
      } else {
        ret = sqlManager.getListHashMap(sql, oParams);
        // Converto tutti gli HashMap in valori stringa
        for (int i = 0; i < ret.size(); i++) {
          ret.set(i, SqlSelectTag.convertHasMapString((HashMap) ret.get(i)));
        }
      }
      return ret;
    } catch (SQLException e) {
      throw new JspException(e.getMessage(), e);
    }
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

  /**
   * @return the sql
   */
  public String getSql() {
    return sql;
  }

  /**
   * @param sql
   *        the sql to set
   */
  public void setSql(String sql) {
    this.sql = sql;
  }

  
  /**
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  
  /**
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

}
