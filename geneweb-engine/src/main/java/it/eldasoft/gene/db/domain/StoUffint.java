/*
 * Created on 02/ott/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean per l'interfacciamento con i dati presenti nella tabella STO_UFFINT
 *
 * @author Mirco.Franzoni
 */
public class StoUffint implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = -1590267675316804154L;

  private String            codice;
  private String            denominazione;
  private Date				dataFineValidita;
  private String            indirizzo;
  private String            civico;
  private String            codiceIstat;
  private String            localita;
  private String            provincia;
  private String            cap;
  private String            codiceNazione;
  private String            telefono;
  private String            fax;
  private String            codiceFiscale;
  private String            tipoAmministrazione;
  private String            email;
  private String            pec;
  private String            iscuc;
  private String            cfAnac;
  

  /**
   * @return Ritorna codice.
   */
  public String getCodice() {
    return codice;
  }

  /**
   * @param codice
   *        codice da settare internamente alla classe.
   */
  public void setCodice(String codice) {
    this.codice = codice;
  }

public void setDenominazione(String denominazione) {
	this.denominazione = denominazione;
}

public String getDenominazione() {
	return denominazione;
}

public void setIndirizzo(String indirizzo) {
	this.indirizzo = indirizzo;
}

public String getIndirizzo() {
	return indirizzo;
}

public void setCivico(String civico) {
	this.civico = civico;
}

public String getCivico() {
	return civico;
}

public void setCodiceIstat(String codiceIstat) {
	this.codiceIstat = codiceIstat;
}

public String getCodiceIstat() {
	return codiceIstat;
}

public void setLocalita(String localita) {
	this.localita = localita;
}

public String getLocalita() {
	return localita;
}

public void setProvincia(String provincia) {
	this.provincia = provincia;
}

public String getProvincia() {
	return provincia;
}

public void setCap(String cap) {
	this.cap = cap;
}

public String getCap() {
	return cap;
}

public void setCodiceNazione(String codiceNazione) {
	this.codiceNazione = codiceNazione;
}

public String getCodiceNazione() {
	return codiceNazione;
}

public void setTelefono(String telefono) {
	this.telefono = telefono;
}

public String getTelefono() {
	return telefono;
}

public void setFax(String fax) {
	this.fax = fax;
}

public String getFax() {
	return fax;
}

public void setCodiceFiscale(String codiceFiscale) {
	this.codiceFiscale = codiceFiscale;
}

public String getCodiceFiscale() {
	return codiceFiscale;
}

public void setTipoAmministrazione(String tipoAmministrazione) {
	this.tipoAmministrazione = tipoAmministrazione;
}

public String getTipoAmministrazione() {
	return tipoAmministrazione;
}

public void setEmail(String email) {
	this.email = email;
}

public String getEmail() {
	return email;
}

public void setPec(String pec) {
	this.pec = pec;
}

public String getPec() {
	return pec;
}

public void setIscuc(String iscuc) {
	this.iscuc = iscuc;
}

public String getIscuc() {
	return iscuc;
}

public void setCfAnac(String cfAnac) {
	this.cfAnac = cfAnac;
}

public String getCfAnac() {
	return cfAnac;
}

public void setDataFineValidita(Date dataFineValidita) {
	this.dataFineValidita = dataFineValidita;
}

public Date getDataFineValidita() {
	return dataFineValidita;
}

  


}
