/*
 * Created on 5-ott-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.argomenti;

import it.eldasoft.utils.eccezioni.EldaCheckedException;

/**
 * Eccezione emessa in seguito a problemi in fase di determinazione delle join
 * da aggiungere ad una ricerca o del fatto che una tabella non è inseribile
 * nella ricerca
 * 
 * @author Stefano.Sabbadin
 */
public class RouterJoinTabelleException extends EldaCheckedException {
  
  /**
   * UID
   */
  private static final long   serialVersionUID = -5828147261209314168L;

  /** famiglia di eccezioni legate alla generazione/composizione di query */
  private static final String FAMIGLIA         = "JOINTAB";

  /*
   * vari codici errore
   */
  public static final String  CODICE_ERRORE_TABELLA_NON_PIU_UTILIZZABILE          = "NOJOINDISP";

  public RouterJoinTabelleException(String codiceErrore) {
    super(codiceErrore);
  }

  public RouterJoinTabelleException(String codiceErrore, Throwable e) {
    super(codiceErrore, e);
  }

  public RouterJoinTabelleException(String codiceErrore, String message) {
    super(codiceErrore, message);
  }

  public RouterJoinTabelleException(String codiceErrore, String message, Throwable e) {
    super(codiceErrore, message, e);
  }

  /**
   * @see it.eldasoft.utils.eccezioni.EldaCheckedException#getFamiglia()
   */
  public String getFamiglia() {
    return RouterJoinTabelleException.FAMIGLIA;
  }

}
