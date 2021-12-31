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
public class RoleNotFoundException extends Exception {

  private static final long serialVersionUID = -3579673052173452608L;
  String error;

	public RoleNotFoundException() {
		super();
		error = "unknown";
	}

	public RoleNotFoundException(String err) {
		super(err);
		error = err;
	}

	public String getError() {
		return error;
	}
}