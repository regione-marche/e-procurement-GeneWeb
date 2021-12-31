/*
 * Created on 24-lug-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

/**
 * @author cit_defilippis
 */
public class GestoreIMPDTE extends AbstractGestoreEntita {

  public String getEntita() {
    return "IMPDTE";
  }

  public static void gestisciEntitaDaAppa(HttpServletRequest request,
      TransactionStatus status, DataColumnContainer impl)
  throws GestoreException{
    
    // creo l'impl per l'inserimento su impdte da appa
    
    DataColumnContainer implDte = new DataColumnContainer("");
    implDte.addColumn("IMPDTE.CODIMP3", impl.getColumn("APPA.NCODIM"));
    implDte.addColumn("IMPDTE.CODDTE", impl.getColumn("APPA.APCDTE"));
    implDte.addColumn("IMPDTE.NOMDTE", impl.getColumn("APPA.APNDTE"));
    
    //Modifiche per cambiamento chiave entità IMPLEG e IMPDTE
    implDte.addColumn("IMPDTE.ID", JdbcParametro.TIPO_NUMERICO,null);
    implDte.getColumn("IMPDTE.ID").setChiave(true);
    
    AbstractGestoreEntita gestoreIMPDTE = new GestoreIMPDTE();
    gestoreIMPDTE.setRequest(request);
    
    gestoreIMPDTE.inserisci(status, implDte);
    
  }
  
  public static void gestisciEntitaDaImpr(HttpServletRequest request,
      TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    // creo un gestore per l'entità IMPIND in modo da fare gestire
    // automaticamente eliminazione e inserimento
    AbstractGestoreEntita gestoreIMPDTE = new GestoreIMPDTE();
    gestoreIMPDTE.setRequest(request);
    
    String nomeCampoNumeroRecord = "NUMERO_IMPDTE";
    String nomeCampoDelete = "DEL_IMPDTE";
    String nomeCampoMod = "MOD_IMPDTE";
    
    //Modifiche per cambiamento chiave entità IMPLEG e IMPDTE
    String[] nomiCampiChiave = new String[] { "IMPDTE.ID" };
    
    // Vector vectorNomiCampi = new Vector(java.util.Arrays.asList(nomiCampi));
    Vector vectorNomiCampiChiave = new Vector(
        java.util.Arrays.asList(nomiCampiChiave));
    // gestione salvataggio ulteriori indirizzi
    Long numeroDirTecni = impl.getLong(nomeCampoNumeroRecord);

    for (int i = 1; i <= numeroDirTecni.intValue(); i++) {
      // carico l'array dei dati dell'indirizzo in questione
      DataColumn[] campiDirTecni = impl.getColumnsBySuffix("_" + i, false);

      // effettuo il parsing dei campi fittizzi del singolo indirizzo e
      // setto i campi chiave
      for (int j = 0; j < campiDirTecni.length; j++) {
        // setto i campi chiave
        if (vectorNomiCampiChiave.contains(campiDirTecni[j].getNomeFisico()))
          campiDirTecni[j].setChiave(true);
      }
      // creo un impl con i dati della riga
      DataColumnContainer newImpl = new DataColumnContainer(campiDirTecni);
      
      boolean deleteOccorrenza = newImpl.isColumn(nomeCampoDelete)
      	&& "1".equals(newImpl.getString(nomeCampoDelete));
      boolean updateOccorrenza = newImpl.isColumn(nomeCampoMod)
      	&& "1".equals(newImpl.getString(nomeCampoMod));
      
      // Rimozione dei campi fittizi (il campo per la marcatura della delete e
      // tutti gli eventuali campi passati come argomento)
      newImpl.removeColumns(new String[] {
          "IMPDTE." + nomeCampoDelete,
          "IMPDTE." + nomeCampoMod});
      
      if (deleteOccorrenza) {
    	// Se è stata richiesta l'eliminazione e il campo chiave numerica e'
        // diverso da null eseguo l'effettiva eliminazione del record
    	if (newImpl.getString("IMPDTE.CODIMP3") != null)
    		gestoreIMPDTE.elimina(status, newImpl);
        // altrimenti e' stato eliminato un nuovo record non ancora inserito
        // ma predisposto nel form per l'inserimento
      } else {
        if (updateOccorrenza){
          // non è da eliminare
          if (newImpl.getString("IMPDTE.CODIMP3") == null){
            // l'occorrenza è da inserire in quanto è una di quelle senza chiave
            // numerica e risulta attiva (non eliminata)
            newImpl.setValue("IMPDTE.CODIMP3",impl.getString("IMPR.CODIMP"));
            gestoreIMPDTE.inserisci(status, newImpl);
          } else {
            if (newImpl.isModifiedTable("IMPDTE")){
              // l'occorrenza è da modificare
              gestoreIMPDTE.update(status, newImpl);
            }
          }
        }
      }
    }
  }

  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    //Modifiche per cambiamento chiave entità IMPLEG e IMPDTE
    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);    
    Long nextId = new Long(genChiaviManager.getNextId("IMPDTE"));    
    impl.setValue("IMPDTE.ID", nextId);
  }

  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }
}
