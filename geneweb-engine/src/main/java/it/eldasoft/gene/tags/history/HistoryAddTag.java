package it.eldasoft.gene.tags.history;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

/**
 * Classe che esegue il clear dell'history dell'utente
 * @author cit_franceschin
 *
 */
public class HistoryAddTag extends TagSupportGene{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5125378941710868816L;
	
	private String titolo;
	private String id;
    private String replaceParam=null;
	
	public int doStartTag() throws JspException {
		final UtilityHistory history = UtilityTags.getUtilityHistory(this.pageContext.getSession()); 
		history.add(id, titolo, pageContext, replaceParam);
		
		final String entity = pageContext.getRequest().getParameter(UtilityTags.SESSION_PENDICE_DEF_TROVA);
		if (StringUtils.isNotBlank(entity)) {
			UtilityTags.restoreHashAttributeForSqlBuild((HttpServletRequest) pageContext.getRequest(), entity, UtilityStruts.getNumeroPopUp(pageContext.getRequest()));
		}
		
		return SKIP_BODY;
	}
	
	/**
	 * @return Returns the title.
	 */
	public String getTitolo() {
		return titolo;
	}

	/**
	 * @param title The title to set.
	 */
	public void setTitolo(String title) {
		this.titolo = title;
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

  
  /**
   * @return Returns the replaceParam.
   */
  public String getReplaceParam() {
    return replaceParam;
  }

  
  /**
   * @param replaceParam The replaceParam to set.
   */
  public void setReplaceParam(String replaceParam) {
    this.replaceParam = replaceParam;
  }
  
}
