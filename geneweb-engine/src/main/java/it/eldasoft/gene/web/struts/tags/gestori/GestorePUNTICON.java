/*
 * Created on 29/mag/2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore dei punti di contatto
 *
 * @author Marco.Perazzetta
 */
public class GestorePUNTICON extends AbstractGestoreChiaveNumerica {

	public String getEntita() {
		return "PUNTICON";
	}

	@Override
	public String getCampoNumericoChiave() {
		return "NUMPUN";
	}

	@Override
	public String[] getAltriCampiChiave() {
		return new String[]{"CODEIN"};
	}

	public void preDelete(TransactionStatus status, DataColumnContainer impl)
					throws GestoreException {

		//Prima di procedere con l'eliminazione si deve controllare 
		//che non sia collegato a qualche entita
		GeneManager gene = getGeneManager();
		String codiceUffint = impl.getString("PUNTICON.CODEIN");
		Long numeroPunticon = impl.getLong("PUNTICON.NUMPUN");
		gene.checkConstraints("PUNTICON", new Object[]{codiceUffint, numeroPunticon});
	}

	public void postDelete(DataColumnContainer impl) throws GestoreException {
	}

	public void postInsert(DataColumnContainer impl) throws GestoreException {
	}

	public void preUpdate(TransactionStatus status, DataColumnContainer impl)
					throws GestoreException {
	}

	public void postUpdate(DataColumnContainer impl) throws GestoreException {
	}
}
