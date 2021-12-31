package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class IsLoginEsistenteAction extends Action {

  private AccountManager accountManager;

  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String login = request.getParameter("login");
    String isLoginCF = ConfigManager.getValore(CostantiGenerali.PROP_REG_LOGINCF);
    isLoginCF = UtilityStringhe.convertiNullInStringaVuota(isLoginCF);
    HashMap<String, Boolean> hMapResult = new HashMap<String, Boolean>();
    if ("1".equals(isLoginCF)){
      if (accountManager.isUsedLogin(login, -1)) {
        hMapResult.put("loginEsistente", Boolean.TRUE);
      } else {
        hMapResult.put("loginEsistente", Boolean.FALSE);
      }
    }else{
      hMapResult.put("loginEsistente", Boolean.FALSE);
    }

    JSONObject jsonResult = JSONObject.fromObject(hMapResult);
    out.println(jsonResult);

    out.flush();
    return null;
  }

}
