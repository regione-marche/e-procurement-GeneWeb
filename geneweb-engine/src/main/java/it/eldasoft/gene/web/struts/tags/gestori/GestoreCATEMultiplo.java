/*
 * Created on 07/lug/2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore delle occorrenze dell'entita CATE presenti piu' volte nella
 * pagina impr-categorieIscrizione.jsp la quale contiene le categorie
 * d'iscrizione delle imprese
 * 
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete 
 *  
 * @author Luca.Giacomazzo
 */
public class GestoreCATEMultiplo extends AbstractGestoreEntita {

  public GestoreCATEMultiplo() {
    super(false);
  }

  public String getEntita() {
    return "CATE";
  }

  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    // Gestione delle ulteriori categorie solo se esiste la colonna con la 
    // NUMERO_CATEGORIE_ISCRIZIONE
    if (impl.isColumn("NUMERO_CATEGORIE_ISCRIZIONE")) {
      
      AbstractGestoreEntita gestoreCATE = new DefaultGestoreEntita("CATE",
          this.getRequest());
      // Osservazione: si e' deciso di usare la classe DefaultGestoreEntita, invece
      // di creare la classe GestoreCATE come apposito gestore di entita', perche'
      // essa non avrebbe avuto alcuna logica di business

      int numeroCategorieIscriz = impl.getLong("NUMERO_CATEGORIE_ISCRIZIONE").intValue();
      String codiceImpresaPadre = UtilityStruts.getParametroString(
          this.getRequest(), UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA).split(":")[1];

      // Nomi fisici dei campi chiave
      String[] campiChiave = new String[]{"CATE.CODIMP1", "CATE.CATISC"};

      // Nome dell'entita' dinamica associata all'entita CATE
      String nomeEntitaDinamica = this.getGeneManager().getNomeEntitaDinamica(this.getEntita());
      
      // Cancellazione delle categorie eliminate nella scheda
      for (int i = 1; i <= numeroCategorieIscriz; i++) {
        DataColumn[] campiCategoriaIscrizione = impl.getColumnsBySuffix("_" + i, false);
        DataColumnContainer newImpl = new DataColumnContainer(campiCategoriaIscrizione);
       
        if("1".equals(newImpl.getString("DEL_CATEGORIA"))) {
          // Se è stata eliminata e il campo CODIMP1 e' diverso da null 
          // eseguo l'effettiva eliminazione del record
          if(newImpl.getString(campiChiave[0]) != null){
            gestoreCATE.elimina(status, newImpl);
          } // altrimenti e' stata eliminata una nuova categoria d'iscrizione
        }
      }
      
      for (int i = 1; i <= numeroCategorieIscriz; i++) {
        DataColumn[] campiCategoriaIscrizione = impl.getColumnsBySuffix("_" + i, false);
        DataColumnContainer newImpl = new DataColumnContainer(campiCategoriaIscrizione);
       
        boolean categoriaModificata = newImpl.isModifiedTable(this.getEntita());
        boolean categoriaGenAttrModificata = newImpl.isModifiedTable(nomeEntitaDinamica);
        
        // Modifica o inserimento delle categorie non eliminate dalla scheda
        if("0".equals(newImpl.getString("DEL_CATEGORIA"))) {
          if(categoriaModificata || categoriaGenAttrModificata){
            if (newImpl.getString(campiChiave[0]) == null){
              // Set del campo chiave per la nuova occorrenza di CATE
              newImpl.setValue(campiChiave[0], codiceImpresaPadre);
              gestoreCATE.inserisci(status, newImpl);
            } else {
              gestoreCATE.update(status, newImpl);
            }
          }
        }
      }
    }
  }

  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

}