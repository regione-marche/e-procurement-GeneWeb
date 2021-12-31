package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.integrazioni.Art80Manager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class Art80DownloadAction extends Action {

  private Art80Manager art80Manager;

  public Art80Manager getArt80Manager() {
    return art80Manager;
  }

  public void setArt80Manager(Art80Manager art80Manager) {
    this.art80Manager = art80Manager;
  }

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();
    String path = request.getParameter("path");
    String codein = (String) request.getSession().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);

    LogEvento logEvento = LogEventiUtils.createLogEvento(request);
    logEvento.setLivEvento(LogEvento.LIVELLO_INFO);
    logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_READ_ART80);
    logEvento.setDescr("Download del documento " + path);

    try {
      String link = this.art80Manager.art80Download(path, codein);
      JSONObject jsonResult = new JSONObject();
      jsonResult.put("link", link);
      out.print(jsonResult);
      out.flush();
    } catch (Exception e) {
      logEvento.setLivEvento(LogEvento.LIVELLO_ERROR);
      logEvento.setErrmsg("Errore durante il tentativo di download del documento " + path);
    } finally {
      LogEventiUtils.insertLogEventi(logEvento);
    }

    return null;

  }

}
