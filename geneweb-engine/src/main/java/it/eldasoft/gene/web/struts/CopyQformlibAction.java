package it.eldasoft.gene.web.struts;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import net.sf.json.JSONObject;

public class CopyQformlibAction extends Action {

  private SqlManager sqlManager;
  private GenChiaviManager genChiaviManager;
  private GeneManager geneManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String id = request.getParameter("id");
    String cod = request.getParameter("cod");

    int livEvento = 1;
    String codEvento="QFORMLIB_COPIA";
    String descEvento="Inserimento modello Q-form mediante copia (cod.modello sorgente " + cod + ")";
    String msgErr="";
    String codNuovo ="";

    if (id != null) {
      TransactionStatus status = null;
      boolean commitTransaction = false;
      JSONObject result = new JSONObject();

      String insert="insert into qformlib(id,codmodello,titolo,descrizione,tipologia,dataini,datafine,modinterno,stato,genere,busta,daimporto,aimporto,tiplav,oggetto) "
          + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

      try {
        status = this.sqlManager.startTransaction();
        Long idNuovo = new Long(genChiaviManager.getNextId("QFORMLIB"));
        codNuovo = geneManager.calcolaCodificaAutomatica("QFORMLIB","CODMODELLO");
        Object par[]=new Object[15];
        String titolo=null;
        String descrizione=null;
        Long tipologia = null;
        Timestamp dataini = null;
        Timestamp datafine = null;
        Long genere = null;
        Long busta = null;
        Double daimporto = null;
        Double aimporto = null;
        Long tiplav = null;
        String oggetto = null;
        Vector<?> dati = this.sqlManager.getVector("select titolo,descrizione,tipologia,dataini,datafine,genere,busta,daimporto,aimporto,tiplav,oggetto,modinterno from qformlib where id=?", new Object[] {new Long(id)});
        if(dati!=null) {
          titolo=SqlManager.getValueFromVectorParam(dati, 0).getStringValue();
          descrizione=SqlManager.getValueFromVectorParam(dati, 1).getStringValue();
          tipologia=SqlManager.getValueFromVectorParam(dati, 2).longValue();
          dataini = SqlManager.getValueFromVectorParam(dati, 3).dataValue();
          datafine = SqlManager.getValueFromVectorParam(dati, 4).dataValue();
          genere = SqlManager.getValueFromVectorParam(dati, 5).longValue();
          busta = SqlManager.getValueFromVectorParam(dati, 6).longValue();
          daimporto = SqlManager.getValueFromVectorParam(dati, 7).doubleValue();
          aimporto = SqlManager.getValueFromVectorParam(dati, 8).doubleValue();
          tiplav = SqlManager.getValueFromVectorParam(dati, 9).longValue();
          oggetto=SqlManager.getValueFromVectorParam(dati, 10).getStringValue();
        }
        par[0]=idNuovo;
        par[1]=codNuovo;
        par[2]=titolo;
        par[3]=descrizione;
        par[4]=tipologia;
        par[5]=dataini;
        par[6]=datafine;
        par[7]="2";
        par[8]=new Long(1);
        par[9]=genere;
        par[10]=busta;
        par[11]=daimporto;
        par[12]=aimporto;
        par[13]=tiplav;
        par[14]=oggetto;
        this.sqlManager.update(insert, par);
        commitTransaction = true;
      } catch (Exception e) {
        livEvento = 3;
        commitTransaction = false;
        msgErr = e.getMessage();
        throw e;
      } finally {
        if (status != null) {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        }
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(codNuovo);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descEvento);
        logEvento.setErrmsg(msgErr);
        LogEventiUtils.insertLogEventi(logEvento);
      }

      result.put("esito", new Boolean(commitTransaction));
      result.put("messaggio", msgErr);
      out.println(result);
      out.flush();
    }

    return null;

  }

}
