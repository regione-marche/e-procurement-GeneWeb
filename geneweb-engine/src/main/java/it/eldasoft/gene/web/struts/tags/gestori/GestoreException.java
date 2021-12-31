package it.eldasoft.gene.web.struts.tags.gestori;

import java.sql.SQLException;

/**
 * Gestore standard delle eccezioni su un gestore
 * 
 * @author cit_franceschin
 * 
 */
public class GestoreException extends Exception {

  private static final long serialVersionUID = -2284134826757825347L;

  private String            codice;

  private Object            parameters[];

  public GestoreException(String message, String codice, Throwable t) {
    super(message, t);
    this.codice = codice;
    this.parameters = null;

  }

  public GestoreException(String message, String codice) {
    super(message);
    this.codice = codice;
    this.parameters = null;
  }

  public GestoreException(String message, String codice, Object[] params,
      Throwable t) {
    super(message, t);
    this.codice = codice;
    this.parameters = params;
  }

  /**
   * @return Returns the codice.
   */
  public String getCodice() {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 29/11/2006 M.F. Aggiungo l'eventuale codice dell'eccezione
    // ************************************************************

    String ret = this.codice;
    if (ret == null) ret = "";
    // Se esiste una causa la aggiungo
    if (this.getCause() != null) {
      if (ret.length() > 0) ret += ".";
      ret += this.getCause().getClass().getName().substring(
          this.getCause().getClass().getName().lastIndexOf('.') + 1);
      if (this.getCause() instanceof SQLException) {
        SQLException e = (SQLException) this.getCause();
        ret += ".err" + e.getErrorCode();
      }
    }
    return ret;
  }

  /**
   * @return Returns the parameters.
   */
  public Object[] getParameters() {
    return parameters;
  }

  /**
   * @param codice
   *        The codice to set.
   */
  public void prependCodice(String codice) {
    if (codice != null && codice.length() > 0) {
      this.codice = codice
          + (this.codice != null && this.codice.length() > 0 ? "."
              + this.codice : "");
    }
  }

  /**
   * @param codice
   *        The codice to set.
   */
  public void addCodice(String codice) {
    if (codice != null && codice.length() > 0) {
      this.codice = (this.codice != null && this.codice.length() > 0
          ? this.codice + "."
          : "")
          + codice;
    }
  }

}
