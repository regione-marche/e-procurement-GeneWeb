package it.eldasoft.gene.tags.gestori.submit;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

public class GestoreW_APPLICATION_KEYS extends AbstractGestoreEntita {

  public String getEntita() {
    return "W_APPLICATION_KEYS";
  }

  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
   
  }

  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
  throws GestoreException {

	  String clientKey = datiForm.getString("KEY_DECRYPT");
	  // conversione della clientKey in SHA-256
	  MessageDigest md;
	  try {
		  md = MessageDigest.getInstance("SHA-256");
	  } catch(NoSuchAlgorithmException ex) {
		  throw new GestoreException("Errore durante la conversione del ClientKey", null, ex);
	  }
	  md.update(clientKey.getBytes());
	  byte byteData[] = md.digest();

	  // convert the byte to hex format method 1
	  StringBuffer sb = new StringBuffer();
	  for (int i = 0; i < byteData.length; i++) {
		  sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
				  .substring(1));
	  }
	  datiForm.setValue("W_APPLICATION_KEYS.CHIAVE", sb.toString());
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

	  String clientKey = datiForm.getString("KEY_DECRYPT");
	  // conversione della clientKey in SHA-256
	  MessageDigest md;
	  try {
		  md = MessageDigest.getInstance("SHA-256");
	  } catch(NoSuchAlgorithmException ex) {
		  throw new GestoreException("Errore durante la conversione del ClientKey", null, ex);
	  }
	  md.update(clientKey.getBytes());
	  byte byteData[] = md.digest();

	  // convert the byte to hex format method 1
	  StringBuffer sb = new StringBuffer();
	  for (int i = 0; i < byteData.length; i++) {
		  sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
				  .substring(1));
	  }
	  datiForm.setValue("W_APPLICATION_KEYS.CHIAVE", sb.toString());
  }

}
