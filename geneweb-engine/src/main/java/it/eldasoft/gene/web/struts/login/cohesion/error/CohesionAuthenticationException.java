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
public class CohesionAuthenticationException extends Exception {
	
	private static final long serialVersionUID = -541954980818245197L;

	String error;
	public final static String GENERIC_ERROR = "GENERIC_ERROR";
	public final static String WEAK_PWD_ERROR = "WEAK_PWD_ERROR";

	public CohesionAuthenticationException() {
		super();
		error = "unknown";
	}

	public CohesionAuthenticationException(String err) {
		super(err);
		error = err;
	}

	public String getError() {
		return error;
	}
}