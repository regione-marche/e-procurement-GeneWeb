package it.eldasoft.gene.tags.link;

import it.eldasoft.gene.tags.js.Javascript;

/**
 * Oggetto che gestisce il menu popup di un determinato campo
 * @author marco.franceschin
 *
 */
public class PopUpCampoImpl extends PopUpGenericoImpl {
	
	private String nomeCampo;
	public PopUpCampoImpl(String nomeCampo, String id, String contextPath, Javascript js) {
		super(id, contextPath, js);
		this.nomeCampo=nomeCampo;
	}
	/**
	 * @return Returns the nomeCampo.
	 */
	public String getNomeCampo() {
		return nomeCampo;
	}
	/**
	 * @param nomeCampo The nomeCampo to set.
	 */
	public void setNomeCampo(String nomeCampo) {
		this.nomeCampo = nomeCampo;
	}
	

}
