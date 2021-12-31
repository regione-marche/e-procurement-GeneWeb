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
public class UserNotFoundException extends Exception {

  private static final long serialVersionUID = 3968401446900415036L;
  String error;

	public UserNotFoundException() {
		super();
		error = "unknown";
	}

	public UserNotFoundException(String err) {
		super(err);
		error = err;
	}

	public String getError() {
		return error;
	}
}