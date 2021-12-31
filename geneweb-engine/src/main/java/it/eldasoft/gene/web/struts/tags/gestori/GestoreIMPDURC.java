/*
 * Created on 17-lug-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import java.sql.SQLException;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.utils.spring.UtilitySpring;

import org.springframework.transaction.TransactionStatus;


/**
 * Gestore di submit dell'entita' IMPDURC
 * 
 * @author Francesco.DiMattei
 */

public class GestoreIMPDURC extends AbstractGestoreEntita {

  public String getEntita() {
    return "IMPDURC";
  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  public void postDelete(DataColumnContainer datiForm) 
      throws GestoreException {
  }

  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    
    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);
    
    Long nextId = new Long(genChiaviManager.getNextId("IMPDURC"));
    
    datiForm.setValue("IMPDURC.ID", nextId);
    
  }

  public void postInsert(DataColumnContainer datiForm) 
      throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    
  }

  public void postUpdate(DataColumnContainer datiForm) 
      throws GestoreException {
  }

}