package it.eldasoft.gene.db.domain;

import java.io.Serializable;

public class TabellatoWsdm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4045316503576078286L;
	private Long id;
	private Long idcftab;
	private Long idconfi;
	private String codice;
	private String sistema;
	private String descri;
	private String valore;
	private String isarchi;
	
    public void setId(Long id) {
      this.id = id;
    }
    
    public Long getId() {
      return id;
    }
    
    public void setIdcftab(Long idcftab) {
      this.idcftab = idcftab;
    }
    
    public Long getIdcftab() {
      return idcftab;
    }
    
    public void setIdconfi(Long idconfi) {
      this.idconfi = idconfi;
    }
    
    public Long getIdconfi() {
      return idconfi;
    }
    
    public void setSistema(String sistema) {
      this.sistema = sistema;
    }
    
    public String getSistema() {
      return sistema;
    }
    
    public void setDescri(String descri) {
      this.descri = descri;
    }
    
    public String getDescri() {
      return descri;
    }
    
    public void setValore(String valore) {
      this.valore = valore;
    }
    
    public String getValore() {
      return valore;
    }
    
    public void setArchi(String archi) {
      this.isarchi = archi;
    }
    
    public String getArchi() {
      return isarchi;
    }

    public void setCodice(String codice) {
      this.codice = codice;
    }

    public String getCodice() {
      return codice;
    }
		
}
