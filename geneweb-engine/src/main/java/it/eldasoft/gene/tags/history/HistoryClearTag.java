package it.eldasoft.gene.tags.history;

import javax.servlet.jsp.JspException;

import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Classe che aggiunge nell'history
 * @author cit_franceschin
 *
 */
public class HistoryClearTag extends TagSupportGene{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5125378941710868816L;
	
	public int doStartTag() throws JspException {
		UtilityTags.getUtilityHistory(this.pageContext.getSession()).clear(UtilityTags.getNumeroPopUp(this.pageContext));
		return SKIP_BODY;
	}

}
