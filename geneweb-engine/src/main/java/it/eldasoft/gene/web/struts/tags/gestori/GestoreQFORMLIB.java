
package it.eldasoft.gene.web.struts.tags.gestori;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreQFORMLIB extends AbstractGestoreEntita {

  /**
   * Logger per tracciare messaggio di debug
   */
  static Logger               logger                    = Logger.getLogger(GestoreQFORMLIB.class);

  @Override
  public String getEntita() {
    return "QFORMLIB";
  }


  public GestoreQFORMLIB() {
    super(false);
  }

  public GestoreQFORMLIB(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }



  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

    String errMsg="";
    int livEvento = 1;
    String oggEvento ="";
    try {
      GeneManager gene = this.getGeneManager();

      oggEvento = gene.calcolaCodificaAutomatica("QFORMLIB",
          "CODMODELLO");

      // La codifica automatica è sempre attiva
      impl.setValue("QFORMLIB.CODMODELLO", oggEvento);
      Long tipologia = impl.getLong("QFORMLIB.TIPOLOGIA");
      Long genere = impl.getLong("QFORMLIB.GENERE");
      String tipologiaString="1";
      if(tipologia!=null)
        tipologiaString = tipologia.toString();
      String genereString ="1";
      if(genere!=null)
        genereString = genere.toString();
      String surveyGenere = ", \"surveyGenere\": \"" + genereString + "\"";
      String initOggetto="{\"survey\": {\"surveyType\": \"" + tipologiaString +  "\"" + surveyGenere + " }}";
      impl.setValue("QFORMLIB.OGGETTO", initOggetto);
      impl.setValue("QFORMLIB.ID", Long.valueOf(genChiaviManager.getNextId("QFORMLIB")));
      impl.insert("QFORMLIB", sqlManager);
    }catch(Exception e) {
      errMsg = e.getMessage();
      livEvento = 3;
    }finally {
      LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(oggEvento);
      logEvento.setCodEvento("QFORMLIB_CREAZIONE");
      logEvento.setDescr("Inserimento modello Q-form");
      logEvento.setErrmsg(errMsg);
      LogEventiUtils.insertLogEventi(logEvento);
    }
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    try {
      impl.update("QFORMLIB", sqlManager);
    }catch(Exception e) {
      throw new GestoreException("Errore nel salvataggio di QFORMLIB",null,e);
    }
  }


  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    String errMsg="";
    int livEvento = 1;
    String oggEvento ="";
    JdbcParametro id = datiForm.getColumn("QFORMLIB.ID").getValue();
    JdbcParametro codmodello = datiForm.getColumn("QFORMLIB.CODMODELLO").getValue();

    if(id!= null ) {
      oggEvento=codmodello.getStringValue();
      try {
        this.getSqlManager().update("delete from QFORMLIB where ID = ?",new Object[] { id.getValue()});
      }catch(Exception e) {
        errMsg = e.getMessage();
        livEvento = 3;
      }finally {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(oggEvento);
        logEvento.setCodEvento("QFORMLIB_ELIMINAZIONE");
        logEvento.setDescr("Eliminazione  modello Q-form");
        logEvento.setErrmsg(errMsg);
        LogEventiUtils.insertLogEventi(logEvento);
      }
    }

  }


  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }


  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }


  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }





}
