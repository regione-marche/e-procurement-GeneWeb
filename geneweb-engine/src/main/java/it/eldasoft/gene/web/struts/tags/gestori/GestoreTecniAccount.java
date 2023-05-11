/*
 * Created on 17/05/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.db.datautils.DataColumnContainer;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per il salvataggio dei dati delle sezioni dinamiche
 * della pagina Tecnici associati all'utente
 * 
 * @author Marcello Caminiti
 */
public class GestoreTecniAccount extends AbstractGestoreEntita {

  public String getEntita() {
    return "TECNI";
  }

  public GestoreTecniAccount() {
    super(false);
  }

  
  public GestoreTecniAccount(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }
  
  public void postDelete(DataColumnContainer impl) throws GestoreException {

  }

  public void postInsert(DataColumnContainer impl) throws GestoreException {

  }

  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }

  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    

  }

  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
     
    Long syscon = impl.getLong("USRSYS.SYSCON");
    try {
      //Vengono sbiancate tutte le occorrenze di Tecni associate all'utente corrente
      this.sqlManager.update("update tecni set syscon = null where syscon = ?", new Object[] { syscon});
      
      //Gestione della sezione dinamica
      String nomeCampoNumeroRecord = "NUMERO_TECNIACCOUNT";
      String nomeCampoDelete = "DEL_TECNIACCOUNT";
      String nomeCampoMod = "MOD_TECNIACCOUNT";
      String entita="TECNI";
      // Gestione delle pubblicazioni bando solo se esiste la colonna con il
      // numero di occorrenze
      if (impl.isColumn(nomeCampoNumeroRecord)) {
        // Estraggo dal dataColumnContainer tutte le occorrenze dei campi
        // dell'entità definita per il gestore
        DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
            impl.getColumns(entita, 0));

        int numeroRecord = impl.getLong(nomeCampoNumeroRecord).intValue();

        for (int i = 1; i <= numeroRecord; i++) {  
          DataColumnContainer newDataColumnContainer = new DataColumnContainer(
              tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

          boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
              && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));
          
          // Rimozione dei campi fittizi (il campo per la marcatura della delete e
          // tutti gli eventuali campi passati come argomento)
          newDataColumnContainer.removeColumns(new String[] {
              entita + "." + nomeCampoDelete,
              entita + "." + nomeCampoMod});
          
          String codtec = newDataColumnContainer.getString("TECNI.CODTEC");
          if (!deleteOccorrenza && (codtec!=null && !"".equals(codtec))) {
            String select ="update tecni set syscon = ? where codtec = ?";
            this.sqlManager.update(select, new Object[] { syscon,codtec});
            
          }
        }
      }
      
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante il salvataggio dell'associazione dell'utente ai tecnici ", null,  e);
    }
  }

  

}
