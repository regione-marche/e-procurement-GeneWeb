/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eldasoft.gene.web.struts.login.cohesion.error;

/**
 * Classe copiata dal progetto Grandi Opere
 * 
 * @author Luca.Giacomazzo
 */
public class AuthenticationException extends Exception {
	
	private static final long serialVersionUID = -541954980818245197L;

	String error;
	public final static String USER_NOT_ACTIVE = "USER_NOT_ACTIVE";

	public AuthenticationException() {
		super();
		error = "unknown";
	}

	public AuthenticationException(String err) {
		super(err);
		error = err;
	}

	public String getError() {
		return error;
	}
}