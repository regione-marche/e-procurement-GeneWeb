package it.eldasoft.gene.web.struts;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.DispatchActionAjaxLogged;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RiattivaVerificaDURCAction extends DispatchActionAjaxLogged {

  static Logger           logger = Logger.getLogger(RiattivaVerificaDURCAction.class);

  private SqlManager sqlManager;

  private GenChiaviManager    genChiaviManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {
    if (logger.isDebugEnabled()){
      logger.debug("RiattivaVerificaDURCAction: inizio metodo");
    }
    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject jsonOutput = new JSONObject();

    String codiceImpresa = request.getParameter("codImp");

    TransactionStatus status = null;
    boolean doCommit = true;
    int livEvento = 1;
    String errMsgEvento = "";
    String idRichiesta = "";
    try {
      String urlS = ConfigManager.getValore("verifica.regolarita.ws.client.url");
      if(urlS != null && !"".equals(urlS)){

        HttpURLConnection connection = null;
        URL url = new URL(urlS);

        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
        connection.setRequestProperty("Host", url.getHost());

        JSONArray codiciFiscaliArray = new JSONArray();
        List<String> datiImprese = this.sqlManager.getListVector("select distinct i1.codimp, i1.cfimp "
            + "from impr i1 "
            + "    join impdurc imd1 on imd1.codimp = i1.codimp "
            + "    join ditg d on d.dittao=i1.codimp "
            + "    join garealbo e on e.codgar=d.codgar5 and e.ngara=d.ngara5 "
            + "    join( "
            + "        select MAX(datric) AS datric2, codimp AS codimp2 "
            + "        from impdurc "
            + "        GROUP BY codimp "
            + "    ) imd2 on imd1.datric = imd2.datric2 and imd1.codimp = imd2.codimp2 "
            + "where imd1.tipo_richiesta = 2 "
            + "and imd1.esito_richiesta_durc in ('1','3','8') "
            + "and imd1.codimp = ?", new Object[]{ codiceImpresa });
        if(datiImprese!=null && !datiImprese.isEmpty()){
          for (int i = 0; i < datiImprese.size() && i <= 100; i++) {
            codiciFiscaliArray.add((String) SqlManager.getValueFromVectorParam(datiImprese.get(i), 1).getValue());
          }
        }

        idRichiesta = "VR_"+UUID.randomUUID().toString();
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("codice", "ELE-ME-15032023");
        jsonInput.put("idrichiesta", idRichiesta);
        jsonInput.put("username", "utente connesso");
        jsonInput.put("codicifiscali", codiciFiscaliArray);

        DataOutputStream output = new DataOutputStream(connection.getOutputStream());
        output.write(jsonInput.toString().getBytes());
        output.flush();

        int httpStatus = connection.getResponseCode();
        String res = "";
        if (httpStatus == HttpURLConnection.HTTP_OK) {
          status = this.sqlManager.startTransaction();
          res = getDatiDaResponse(connection.getInputStream());
          jsonOutput = JSONObject.fromObject(res);

          if (jsonOutput.getInt("Esito") == 1) {
            String query = "INSERT INTO IMPDURC (ID,CODIMP,DATRIC,ID_RICHIESTA,TIPO_RICHIESTA)"
                + " VALUES (?,?,?,?,?)";
            int nextId = this.genChiaviManager.getNextId("IMPDURC");
            this.sqlManager.update(query, new Object[] {nextId, codiceImpresa, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), idRichiesta, 1});
          } else {
            if(jsonOutput.has("Messaggio"))
              errMsgEvento = jsonOutput.getString("Messaggio");
          }
        } else {
          errMsgEvento = getDatiDaResponse(connection.getErrorStream());
        }

        System.out.println("Esito: "+ jsonOutput.getString("Esito"));
        System.out.println("Messaggio: "+ jsonOutput.getString("Messaggio"));
        System.out.println("res: "+ res);

      } 

      if (logger.isDebugEnabled()){
        logger.debug("RiattivaVerificaDURCAction: fine metodo");
      }
    }catch (Exception e) {
      doCommit = false;
      errMsgEvento = "Errore in RiattivaVerificaDURCAction: "+e.getMessage();
      livEvento = 3;
    } finally {
      if (status != null) {
        try {
          if (doCommit) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        } catch (SQLException ex) {
          throw new GestoreException("", "", ex);
        }
      }
      try{
        LogEvento logEvento =  new LogEvento();
        logEvento.setCodApplicazione("PG");
        logEvento.setLivEvento(livEvento);
        String oggetto = "";
        logEvento.setOggEvento(oggetto);
        logEvento.setCodEvento("GA_WSDURC_REQ_VERIFICA_REGOLARITA");
        logEvento.setDescr("Request Verifica Regolarita idRichiesta: "+idRichiesta);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      }catch(Exception e){
        logger.error("Errore inaspettato durante la tracciatura su w_logeventi");
      }
    }



    out.println(jsonOutput);
    out.flush();


    return null;
  }

  private String getDatiDaResponse(InputStream input) throws IOException {
    StringBuilder sb = new StringBuilder();
    if(input != null) {
      BufferedReader br = new BufferedReader(new InputStreamReader(input));
      String strLine;
      while ((strLine = br.readLine()) != null) {
        sb.append(strLine);
      }
      br.close();
    }
    return sb.toString();
  }

}
