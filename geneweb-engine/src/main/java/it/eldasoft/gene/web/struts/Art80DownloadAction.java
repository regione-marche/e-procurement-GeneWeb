package it.eldasoft.gene.web.struts;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.integrazioni.Art80Manager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import net.sf.json.JSONObject;

public class Art80DownloadAction extends ActionAjaxLogged {

  static Logger        logger = Logger.getLogger(Art80DownloadAction.class);

  private Art80Manager art80Manager;

  public Art80Manager getArt80Manager() {
    return art80Manager;
  }

  public void setArt80Manager(Art80Manager art80Manager) {
    this.art80Manager = art80Manager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String target = null;
    String messageKey = null;

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();
    String path = request.getParameter("path");
    String codein = (String) request.getSession().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);

    try {
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

    } catch (Throwable e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (target != null) {
      return mapping.findForward(target);
    } else {
      return null;
    }

  }

}
