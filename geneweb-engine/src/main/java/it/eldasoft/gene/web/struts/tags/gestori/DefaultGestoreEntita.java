package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.db.datautils.DataColumnContainer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di default su un'entita
 * 
 * @author cit_franceschin
 */
public class DefaultGestoreEntita extends AbstractGestoreEntita {

  private String           entitaDefault;
  
  public DefaultGestoreEntita(String entita, HttpServletRequest request) {
    super();
    this.entitaDefault = entita;
    this.setRequest(request);
  }
  
  public String getEntita() {
    return this.entitaDefault;
  }

  public void preInsert(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
  }

  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
  }

  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

  public void preDelete(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
  }

  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

}
