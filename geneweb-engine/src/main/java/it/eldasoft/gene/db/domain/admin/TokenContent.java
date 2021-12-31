package it.eldasoft.gene.db.domain.admin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenContent {

	@JsonProperty(value = "utente")
	private String utente;
	@JsonProperty(value = "sub")
	private String sub;
	@JsonProperty(value = "stato")
	private String stato;
	@JsonProperty(value = "cliente")
	private String  cliente;
	@JsonProperty(value = "software")
	private String software;
	@JsonProperty(value = "tipologia-id")
	private String tipologiaId;
	@JsonProperty(value = "iss")
	private String iss;
	@JsonProperty(value = "codice-sap-cliente")
	private String codiceSap;
	@JsonProperty(value = "exp")
	private String exp;
	@JsonProperty(value = "uuid")
	private String uuid;
	@JsonProperty(value = "email")
	private String email;
	@JsonProperty(value = "domini")
	private List<String> domini;
	@JsonProperty(value = "message")
	private String message;
	@JsonProperty(value = "error")
	private String error;
	@JsonProperty(value = "cod_error")
	private String codError;
	
	
	public String getUtente() {
		return utente;
	}
	public void setUtente(String utente) {
		this.utente = utente;
	}
	public String getSub() {
		return sub;
	}
	public void setSub(String sub) {
		this.sub = sub;
	}
	public String getStato() {
		return stato;
	}
	public void setStato(String stato) {
		this.stato = stato;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public String getSoftware() {
		return software;
	}
	public void setSoftware(String software) {
		this.software = software;
	}
	public String getTipologiaId() {
		return tipologiaId;
	}
	public void setTipologiaId(String tipologiaId) {
		this.tipologiaId = tipologiaId;
	}
	public String getIss() {
		return iss;
	}
	public void setIss(String iss) {
		this.iss = iss;
	}
	public String getCodiceSap() {
		return codiceSap;
	}
	public void setCodiceSap(String codiceSap) {
		this.codiceSap = codiceSap;
	}
	public String getExp() {
		return exp;
	}
	public void setExp(String exp) {
		this.exp = exp;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<String> getDomini() {
		return domini;
	}
	public void setDomini(List<String> domini) {
		this.domini = domini;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getCodError() {
		return codError;
	}
	public void setCodError(String codError) {
		this.codError = codError;
	}
	
}
