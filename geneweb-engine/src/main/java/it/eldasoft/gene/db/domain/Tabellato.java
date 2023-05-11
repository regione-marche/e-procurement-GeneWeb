package it.eldasoft.gene.db.domain;

import java.io.Serializable;

public class Tabellato implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4045316503576078286L;
	private String tipoTabellato;
	private String descTabellato;
	private String datoSupplementare;
	private String arcTabellato;
	
	/**
	 * @return Returns the datoSupplementare.
	 */
	public String getDatoSupplementare() {
		return datoSupplementare;
	}
	/**
	 * @param datoSupplementare The datoSupplementare to set.
	 */
	public void setDatoSupplementare(String datoSupplementare) {
		this.datoSupplementare = datoSupplementare;
	}
	/**
	 * @return Returns the descTabellato.
	 */
	public String getDescTabellato() {
		return descTabellato;
	}
	/**
	 * @param descTabellato The descTabellato to set.
	 */
	public void setDescTabellato(String descTabellato) {
		this.descTabellato = descTabellato;
	}
	/**
	 * @return Returns the tipoTabellato.
	 */
	public String getTipoTabellato() {
		return tipoTabellato;
	}
	/**
	 * @param tipoTabellato The tipoTabellato to set.
	 */
	public void setTipoTabellato(String tipoTabellato) {
		this.tipoTabellato = tipoTabellato;
	}
  
    public String getArcTabellato() {
      return arcTabellato;
    }
    
    public void setArcTabellato(String arcTabellato) {
      this.arcTabellato = arcTabellato;
    }
		
}
