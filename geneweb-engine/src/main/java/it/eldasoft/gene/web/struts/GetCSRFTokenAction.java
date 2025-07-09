package it.eldasoft.gene.web.struts;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.owasp.csrfguard.CsrfGuard;

import net.sf.json.JSONObject;

public class GetCSRFTokenAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        response.setHeader("cache-control", "no-cache");
        response.setContentType("text/text;charset=utf-8");
        PrintWriter out = response.getWriter();

        String tokenValue = CsrfGuard.getInstance()
            .getTokenService()
            .getMasterToken(
                request.getSession().getId()
            );
        JSONObject json = new JSONObject();
        json.accumulate("tokenValue", tokenValue);
        out.println(json);
        out.flush();

        return super.execute(mapping, form, request, response);
    }
    
}
