package it.eldasoft.gene.tags.decorators.campi;

import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.tags.link.UtilityPopUpCampiImpl;
import it.eldasoft.gene.tags.utils.UtilityTags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

/**
 * Tag che si incarica di aggiungere un popup ad un campo
 * 
 * @author cit_franceschin
 * 
 */
public class PopUpCampoTag extends TagSupportGene {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4367259453165104466L;

	private String titolo=null;

	private String href=null;
	
	private String resource=null;
	
	private String parametri=null;
	
	

	private AbstractCampoBodyTag getParentCampo() {
		Tag obj = this.getParent();
		do {
			if (obj != null) {
				if (obj instanceof AbstractCampoBodyTag)
					return (AbstractCampoBodyTag) obj;
				obj = obj.getParent();
			}
		} while (obj != null);
		return null;
	}

	public int doStartTag() throws JspException {

		AbstractCampoBodyTag campo = getParentCampo();
		if (campo == null)
			throw new JspException(
					"Il campo popupCampo deve trovarsi all'interno di una campo o scheda o lista o di un trova !");

		if(this.getTitolo()!=null){
			// Aggiungo la voce effettiva
			campo.addPopUp(this.getTitolo(), this.getHref());
		}else{
			String params[];
			if(this.getParametri()!=null)
				params=UtilityTags.stringToArray(this.getParametri(),';');
			else
				params=new String[]{};
			UtilityPopUpCampiImpl.addFromResource(this.getResource(),params,campo.getDecoratore());
		}
		return SKIP_BODY;
	}

	/**
	 * @return Returns the href.
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @param href
	 *            The href to set.
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * @return Returns the titolo.
	 */
	public String getTitolo() {
		return titolo;
	}

	/**
	 * @param titolo
	 *            The titolo to set.
	 */
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	/**
	 * @return Returns the parametri.
	 */
	public String getParametri() {
		return parametri;
	}

	/**
	 * @param parametri The parametri to set.
	 */
	public void setParametri(String parametri) {
		this.parametri = parametri;
	}

	/**
	 * @return Returns the resource.
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * @param resource The resource to set.
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}

}
