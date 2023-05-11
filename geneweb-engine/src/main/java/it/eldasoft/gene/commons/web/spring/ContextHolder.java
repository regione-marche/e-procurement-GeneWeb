/*
 * Created on 18/gen/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.spring;

/**
 * Classe per la memorizzazione nel thread delle informazioni necessarie al set
 * dei contesti oracle
 *
 * @author Stefano.Sabbadin
 * @since 1.4.5.1
 */
public class ContextHolder {

  private static ThreadLocal<String> context = new ThreadLocal<String>();

  private static ThreadLocal<String> userId  = new ThreadLocal<String>();

  /**
   * Sets the database context to use for the current thread = current user.
   *
   * @param name
   *        name of the schema
   */
  public static void setContext(String name) {
    context.set(name);
  }

  /**
   * Get's the current context name bound to this thread.
   */
  public static String getContext() {
    return context.get();
  }

  /**
   * Sets the user id to use for the current thread = current user.
   *
   * @param name
   *        name of the schema
   */
  public static void setUserId(String id) {
    userId.set(id);
  }

  /**
   * Get's the current user id bound to this thread.
   */
  public static String getUserId() {
    return userId.get();
  }
}
