package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.bl.verifiche.VerificheInterneManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestorePopupArchiviaDocumento extends AbstractGestoreEntita {

	static Logger      logger     = Logger.getLogger(GestorePopupArchiviaDocumento.class);

	@Override
  public String getEntita() {
	    return "DOCUMENTI_VERIFICHE";
	}

	@Override
  public void postDelete(DataColumnContainer datiForm)
			throws GestoreException {


	}

	@Override
  public void postInsert(DataColumnContainer datiForm)
			throws GestoreException {


	}

	@Override
  public void postUpdate(DataColumnContainer datiForm)
			throws GestoreException {

		this.getRequest().setAttribute("RISULTATO", "OPERAZIONEESEGUITA");

	}

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {


	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {


	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	    VerificheInterneManager verificheInterneManager = (VerificheInterneManager) UtilitySpring.getBean(
	        "verificheInterneManager", this.getServletContext(), VerificheInterneManager.class);

		this.getRequest().setAttribute("RISULTATO", "ERRORE");

		if(datiForm.isColumn("DOCUMENTI_VERIFICHE.ISARCHI")){
		  String archiviato = "1";
		  try {

	        datiForm.update("DOCUMENTI_VERIFICHE", sqlManager);

	        Long idVerifica = (Long) datiForm.getColumn("DOCUMENTI_VERIFICHE.ID_VERIFICA").getValue().getValue();

            //calcolo le scadenze
            verificheInterneManager.calcolaScadenzeVerifiche(idVerifica.intValue(), 0, null, null, null, null, null, null, archiviato);


    	  } catch (SQLException e) {
    	        throw new GestoreException("Errore nell'update dell'occorrenza in DOCUMENTI_VERIFICHE",
    	                null, e);
    	  }
		}
	}

}
