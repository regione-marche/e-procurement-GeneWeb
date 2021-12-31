package it.eldasoft.gene.db.domain.admin;

public class UserInfoResponse {
	
	private TokenContent tokenContent;
	private String error;
	private boolean esito;
	
	public TokenContent getTokenContent() {
		return tokenContent;
	}
	public void setTokenContent(TokenContent tokenContent) {
		this.tokenContent = tokenContent;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public boolean isEsito() {
		return esito;
	}
	public void setEsito(boolean esito) {
		this.esito = esito;
	}
}
