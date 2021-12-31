/*
 * Created on 23/mag/2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.bl.integrazioni.Art80Manager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore dell'entita RAGIMP: Raggruppamento imprese Gestore delle occorrenze
 * dell'entita RAGIMP presenti piu' volte nella pagina impr-raggruppamento.jsp
 * la quale contiene le imprese che costituiscono un raggruppamento
 * 
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 * 
 * @author Luca.Giacomazzo
 */
public class GestoreRAGIMPMultiplo extends AbstractGestoreEntita {

  /** Manager per l'esecuzione di query */
  private Art80Manager        art80Manager   = null;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    art80Manager = (Art80Manager) UtilitySpring.getBean("art80Manager", this.getServletContext(), Art80Manager.class);
  }

  public GestoreRAGIMPMultiplo() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "RAGIMP";
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
    // Gestione delle ulteriori categorie solo se esiste la colonna con la
    // NUMERO_IMPRESE

    String nomeCampoNumeroRecord = "NUMERO_RAGIMP";
    String nomeCampoDelete = "DEL_RAGIMP";
    String nomeCampoMod = "MOD_RAGIMP";

    if (impl.isColumn(nomeCampoNumeroRecord)) {

      AbstractGestoreEntita gestore = new DefaultGestoreEntita("RAGIMP", this.getRequest());
      // Osservazione: si e' deciso di usare la classe DefaultGestoreEntita,
      // invece
      // di creare la classe GestoreRAGIMP come apposito gestore di entita',
      // perche'
      // essa non avrebbe avuto alcuna logica di business

      String[] campiChiave = new String[] { "RAGIMP.CODIME9", "RAGIMP.CODDIC" };
      Vector vectorNomiCampiChiave = new Vector(java.util.Arrays.asList(campiChiave));

      int numeroImprese = impl.getLong(nomeCampoNumeroRecord).intValue();
      String codiceImpresaPadre = null;

      if (impl.isColumn("DITG.DITTAO"))
        codiceImpresaPadre = impl.getString("DITG.DITTAO");
      else
        codiceImpresaPadre = UtilityStruts.getParametroString(this.getRequest(), UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA).split(":")[1];

      // Si deve impedire di inserire più di una mandataria
      int numMandatarie = 0;
      for (int i = 1; i <= numeroImprese; i++) {
        DataColumn[] campiImprese = impl.getColumnsBySuffix("_" + i, false);
        DataColumnContainer newImpl = new DataColumnContainer(campiImprese);
        boolean deleteOccorrenza = newImpl.isColumn(nomeCampoDelete) && "1".equals(newImpl.getString(nomeCampoDelete));
        if (!deleteOccorrenza && newImpl.isColumn("RAGIMP.IMPMAN") && "1".equals(newImpl.getString("RAGIMP.IMPMAN"))) numMandatarie++;
      }
      if (numMandatarie > 1) {
        throw new GestoreException("Non è possibile inserire più di una mandataria", "ragimp.mandatariaMultipla", null, new Exception());
      }

      // Cancellazione delle imprese eliminate nella scheda
      for (int i = 1; i <= numeroImprese; i++) {
        DataColumn[] campiImprese = impl.getColumnsBySuffix("_" + i, false);

        // effettuo il parsing dei campi fittizzi del singolo indirizzo e
        // setto i campi chiave
        for (int j = 0; j < campiImprese.length; j++) {
          // setto i campi chiave
          if (vectorNomiCampiChiave.contains(campiImprese[j].getNomeFisico())) campiImprese[j].setChiave(true);
        }

        DataColumnContainer newImpl = new DataColumnContainer(campiImprese);

        boolean deleteOccorrenza = newImpl.isColumn(nomeCampoDelete) && "1".equals(newImpl.getString(nomeCampoDelete));
        boolean updateOccorrenza = newImpl.isColumn(nomeCampoMod) && "1".equals(newImpl.getString(nomeCampoMod));

        // Rimozione dei campi fittizi (il campo per la marcatura della delete e
        // tutti gli eventuali campi passati come argomento)
        newImpl.removeColumns(new String[] { "RAGIMP." + nomeCampoDelete, "RAGIMP." + nomeCampoMod });

        if (deleteOccorrenza) {
          // Se è stata eliminata e il campo CODIMP1 e' diverso da null
          // eseguo l'effettiva eliminazione del record
          if (newImpl.getString(campiChiave[0]) != null) {
            gestore.elimina(status, newImpl);
          } // altrimenti e' stata eliminata una nuova impresa
        } else {
          if (updateOccorrenza) {
            // l'occorrenza è da inserire in quanto è una di quelle senza chiave
            // e risulta attiva (non eliminata)
            if (newImpl.getString(campiChiave[0]) == null) {
              newImpl.setValue(campiChiave[0], codiceImpresaPadre);
              gestore.inserisci(status, newImpl);
            } else {
              if (newImpl.isModifiedTable("RAGIMP")) {
                // l'occorrenza è da modificare
                gestore.update(status, newImpl);
              }
            }
          }
        }
      }
      
      String codein = (String) this.getServletContext().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
      art80Manager.art80AggiornaProfessionistiStudioAssociato(impl.getString("IMPR.CODIMP"),codein);
      
    }
  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }

}