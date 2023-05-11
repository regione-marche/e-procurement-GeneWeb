package it.eldasoft.gene.tags.gestori.submit;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore per update dei dati della pagina sedute di gara
 *
 * @author Diego.Pavan
 */
public class GestoreWSDMTAB extends AbstractGestoreEntita {

    @Override
  public String getEntita() {
        return "WSDMTAB";
    }

    @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
            throws GestoreException {
    }

    @Override
  public void postDelete(DataColumnContainer datiForm)
            throws GestoreException {
    }

    @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
            throws GestoreException {

      GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
          this.getServletContext(), GenChiaviManager.class);

      int id = genChiaviManager.getNextId("WSDMTAB");

      datiForm.getColumn("WSDMTAB.ID").setChiave(true);
      datiForm.setValue("WSDMTAB.ID", new Long(id));

    }

    @Override
  public void postInsert(DataColumnContainer datiForm)
            throws GestoreException {
      this.getRequest().setAttribute("salvataggioOK", "true");
    }

    @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
            throws GestoreException {

    }

    @Override
  public void postUpdate(DataColumnContainer datiForm)
            throws GestoreException {
    }

}