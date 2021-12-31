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

import it.cineca.u_gov.ac.sc.ws.WsdtoSoggettoCollettivoResponse;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.integrazioni.CinecaAnagraficaComuneManager;
import it.eldasoft.gene.bl.integrazioni.CinecaWSManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

/**
 * @author cit_defilippis
 *
 */
public class GestoreIMPLEG extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "IMPLEG";
  }

  public static void gestisciEntitaDaAppa(HttpServletRequest request,
      TransactionStatus status, DataColumnContainer impl)
  throws GestoreException{

    // creo l'impl per l'inserimento su impleg da appa

    DataColumnContainer implLeg = new DataColumnContainer("");
    implLeg.addColumn("IMPLEG.CODIMP2", impl.getColumn("APPA.NCODIM"));
    implLeg.addColumn("IMPLEG.CODLEG", impl.getColumn("APPA.APCLEG"));
    implLeg.addColumn("IMPLEG.NOMLEG", impl.getColumn("APPA.APNLEG"));

    //Modifiche per cambiamento chiave entità IMPLEG e IMPDTE
    implLeg.addColumn("IMPLEG.ID", JdbcParametro.TIPO_NUMERICO,null);
    implLeg.getColumn("IMPLEG.ID").setChiave(true);

    AbstractGestoreEntita gestoreIMPLEG = new GestoreIMPLEG();
    gestoreIMPLEG.setRequest(request);

    gestoreIMPLEG.inserisci(status, implLeg);

  }

  public static void gestisciEntitaDaImpr(HttpServletRequest request,
      ServletContext servletContext, TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    String urlIntegrazioneCineca = ConfigManager.getValore("cineca.ws.SoggettoCollettivo.url");
    urlIntegrazioneCineca = UtilityStringhe.convertiNullInStringaVuota(urlIntegrazioneCineca);

    // creo un gestore per l'entità IMPIND in modo da fare gestire
    // automaticamente eliminazione e inserimento
    AbstractGestoreEntita gestoreIMPLEG = new GestoreIMPLEG();
    gestoreIMPLEG.setRequest(request);

    String nomeCampoNumeroRecord = "NUMERO_IMPLEG";
    String nomeCampoDelete = "DEL_IMPLEG";
    String nomeCampoMod = "MOD_IMPLEG";

    //Modifiche per cambiamento chiave entità IMPLEG e IMPDTE
    String[] nomiCampiChiave = new String[] { "IMPLEG.ID" };
    // Vector vectorNomiCampi = new Vector(java.util.Arrays.asList(nomiCampi));
    Vector vectorNomiCampiChiave = new Vector(
        java.util.Arrays.asList(nomiCampiChiave));
    // gestione salvataggio ulteriori indirizzi
    Long numeroLegRap = impl.getLong(nomeCampoNumeroRecord);

    for (int i = 1; i <= numeroLegRap.intValue(); i++) {
      // carico l'array dei dati dell'indirizzo in questione
      DataColumn[] campiLegRap = impl.getColumnsBySuffix("_" + i, false);

      // effettuo il parsing dei campi fittizzi del singolo indirizzo e
      // setto i campi chiave
      for (int j = 0; j < campiLegRap.length; j++) {
        // setto i campi chiave
        if (vectorNomiCampiChiave.contains(campiLegRap[j].getNomeFisico()))
          campiLegRap[j].setChiave(true);
      }

      // creo un impl con i dati della riga
      DataColumnContainer newImpl = new DataColumnContainer(campiLegRap);

      boolean deleteOccorrenza = newImpl.isColumn(nomeCampoDelete)
    	&& "1".equals(newImpl.getString(nomeCampoDelete));
	  boolean updateOccorrenza = newImpl.isColumn(nomeCampoMod)
	   	&& "1".equals(newImpl.getString(nomeCampoMod));

	  // Rimozione dei campi fittizi (il campo per la marcatura della delete e
	  // tutti gli eventuali campi passati come argomento)
	  newImpl.removeColumns(new String[] {
	     "IMPLEG." + nomeCampoDelete,
	     "IMPLEG." + nomeCampoMod});


      if (deleteOccorrenza) {

        //Integrazione Cineca
        if(!"".equals(urlIntegrazioneCineca)){
          CinecaWSManager cinecaWSManager = (CinecaWSManager) UtilitySpring.getBean("cinecaWSManager", servletContext, CinecaWSManager.class);
          //verifico la presenza del soggetto collettivo
          String codimp =impl.getString("IMPR.CODIMP");
          String[] res = cinecaWSManager.getCinecaSoggettoCollettivo(codimp);
          if( new Integer(res[0]) > 0 ){
            String idInternoStr = res[2];
            Long idInterno = null;
            if(idInternoStr != null){//dovrebbe esserlo sempre in questo caso
              idInterno = new Long(idInternoStr);
            }
            HashMap<String, Object> soggettoCollettivo = cinecaWSManager.getDatiSoggettoCollettivo(codimp);
            if("1".equals(res[0])){
              //masterizzo
                  String codEsterno = (String) soggettoCollettivo.get("codEsterno");
                  WsdtoSoggettoCollettivoResponse wsdtoSoggettoCollettivoResponse = cinecaWSManager.wsCinecaMasterizzaSoggettoCollettivo(idInterno,codEsterno);
                  wsdtoSoggettoCollettivoResponse.getCodEsterno();
            }
            //devo popolare il soggetto  con i dati appena modificati
            Boolean modificaCineca = false;
            soggettoCollettivo.put("idInterno", idInterno);
              Date DataInizioIncarico = newImpl.getData("IMPLEG.LEGINI");
              Date DataFineIncarico = newImpl.getData("IMPLEG.LEGFIN");
              if((DataInizioIncarico == null || (DataInizioIncarico != null && UtilityDate.getDataOdiernaAsDate().after(DataInizioIncarico))) &&
                  (DataFineIncarico == null || (DataFineIncarico != null && UtilityDate.getDataOdiernaAsDate().before(DataFineIncarico)))){
                soggettoCollettivo.put("rapprLegale", "");
                modificaCineca = true;

              //modifico i dati esistenti
              if(modificaCineca){
                cinecaWSManager.wsCinecaModificaSoggettoCollettivo(request,soggettoCollettivo);
              }
            }
          }
        }//fine Integrazione Cineca

    	// Se è stata richiesta l'eliminazione e il campo chiave numerica e'
        // diverso da null eseguo l'effettiva eliminazione del record
    	  if (newImpl.getString("IMPLEG.CODIMP2") != null)
    		  gestoreIMPLEG.elimina(status, newImpl);
        // altrimenti e' stato eliminato un nuovo record non ancora inserito
        // ma predisposto nel form per l'inserimento
      } else {
        if (updateOccorrenza){

          //Integrazione Cineca
          if(!"".equals(urlIntegrazioneCineca)){
            CinecaWSManager cinecaWSManager = (CinecaWSManager) UtilitySpring.getBean("cinecaWSManager", servletContext, CinecaWSManager.class);
            CinecaAnagraficaComuneManager cinecaAnagraficaComuneManager = (CinecaAnagraficaComuneManager) UtilitySpring.getBean("cinecaAnagraficaComuneManager", servletContext, CinecaAnagraficaComuneManager.class);
            //verifico la presenza del soggetto collettivo
            String codimp =impl.getString("IMPR.CODIMP");
            String[] res = cinecaWSManager.getCinecaSoggettoCollettivo(codimp);
            if( new Integer(res[0]) > 0 ){
              String idInternoStr = res[2];
              Long idInterno = null;
              if(idInternoStr != null){//dovrebbe esserlo sempre in questo caso
                idInterno = new Long(idInternoStr);
              }
              HashMap<String, Object> soggettoCollettivo = cinecaWSManager.getDatiSoggettoCollettivo(codimp);
              if("1".equals(res[0])){
                //masterizzo
                    String codEsterno = (String) soggettoCollettivo.get("codEsterno");
                    String[] ctrlDOres = null;
                    ctrlDOres = cinecaAnagraficaComuneManager.getDatiObbligatoriAnagrafica("TRACC", null, soggettoCollettivo);
                    if("true".equals(ctrlDOres[0])){
                      WsdtoSoggettoCollettivoResponse wsdtoSoggettoCollettivoResponse = cinecaWSManager.wsCinecaMasterizzaSoggettoCollettivo(idInterno,codEsterno);
                      wsdtoSoggettoCollettivoResponse.getCodEsterno();
                    }else{
                      UtilityStruts.addMessage(request, "warning",
                          "warnings.cineca.mancataIntegrazione",
                          new Object[] {codimp,ctrlDOres[1]});
                    }
              }
              //devo popolare il soggetto  con i dati appena modificati
              Boolean modificaCineca = false;
              soggettoCollettivo.put("idInterno", idInterno);
              if (newImpl.getColumn("IMPLEG.LEGINI").isModified() || newImpl.getColumn("IMPLEG.LEGFIN").isModified() || newImpl.getColumn("IMPLEG.NOMLEG").isModified()) {
                Date DataInizioIncarico = newImpl.getData("IMPLEG.LEGINI");
                Date DataFineIncarico = newImpl.getData("IMPLEG.LEGFIN");
                if((DataInizioIncarico == null || (DataInizioIncarico != null && UtilityDate.getDataOdiernaAsDate().after(DataInizioIncarico))) &&
                    (DataFineIncarico == null || (DataFineIncarico != null && UtilityDate.getDataOdiernaAsDate().before(DataFineIncarico)))){
                  soggettoCollettivo.put("rapprLegale", UtilityStringhe.convertiNullInStringaVuota(newImpl.getString("IMPLEG.NOMLEG")));
                  modificaCineca = true;
                }
                //modifico i dati esistenti
                if(modificaCineca){
                  String[] ctrlDOres = null;
                  ctrlDOres = cinecaAnagraficaComuneManager.getDatiObbligatoriAnagrafica("TRACC", null, soggettoCollettivo);
                  if("true".equals(ctrlDOres[0])){
                    cinecaWSManager.wsCinecaModificaSoggettoCollettivo(request,soggettoCollettivo);
                  }else{
                    UtilityStruts.addMessage(request, "warning",
                        "warnings.cineca.mancataIntegrazione",
                        new Object[] {codimp,ctrlDOres[1]});
                  }
                }
              }
            }
          }//fine Integrazione Cineca

          // non è da eliminare
          if (newImpl.getString("IMPLEG.CODIMP2") == null){
            // l'occorrenza è da inserire in quanto è una di quelle senza chiave
            // numerica e risulta attiva (non eliminata)
            newImpl.setValue("IMPLEG.CODIMP2",impl.getString("IMPR.CODIMP"));
            gestoreIMPLEG.inserisci(status, newImpl);
          } else {
            if (newImpl.isModifiedTable("IMPLEG")){
              // l'occorrenza è da modificare
              gestoreIMPLEG.update(status, newImpl);
            }
          }
        }
      }
    }
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    //Modifiche per cambiamento chiave entità IMPLEG e IMPDTE
    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);
    Long nextId = new Long(genChiaviManager.getNextId("IMPLEG"));
    impl.setValue("IMPLEG.ID", nextId);
  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

}
