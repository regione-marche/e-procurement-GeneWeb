package it.eldasoft.gene.tags.gestori.submit;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore per update dei dati della pagina sedute di gara
 *
 * @author Diego.Pavan
 */
public class GestoreWSDMCONFI extends AbstractGestoreEntita {

    @Override
  public String getEntita() {
        return "WSDMCONFI";
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

      int id = genChiaviManager.getNextId("WSDMCONFI");

      datiForm.getColumn("WSDMCONFI.ID").setChiave(true);
      datiForm.setValue("WSDMCONFI.ID", new Long(id));
      

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