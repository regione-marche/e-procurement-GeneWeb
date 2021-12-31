package it.eldasoft.gene.tags.gestori.submit;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreW_TAGS extends AbstractGestoreEntita {

  static Logger logger = Logger.getLogger(GestoreW_TAGS.class);

  @Override
  public String getEntita() {
    return "W_TAGS";
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("GestoreW_TAGS-preDelete: inizio metodo ");

    GeneManager geneManager = this.getGeneManager();

    String codapp = datiForm.getString("W_TAGS.CODAPP");
    String tagcod = datiForm.getString("W_TAGS.TAGCOD");
    geneManager.deleteTabelle(new String[] { "W_TAGSLIST" }, "CODAPP = ? AND TAGCOD = ?", new Object[] { codapp, tagcod });

    if (logger.isDebugEnabled()) logger.debug("GestoreW_TAGS-preDelete: fine metodo ");

  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

}
