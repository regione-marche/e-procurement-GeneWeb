/*
 * Created on 31-ott-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.utils.functions;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class DebugFunctionTag extends AbstractFunzioneTag {

  public DebugFunctionTag() {
    super(1, new Class[] { String.class });
  }

  public String function(PageContext pageContext, Object parametri[])
      throws JspException {
    Object paramerto = parametri[0];
    StringBuffer buf = new StringBuffer("");
    String param = "";
    if (paramerto != null) param = paramerto.toString();
    ServletRequest request = pageContext.getRequest();
    if (param.equalsIgnoreCase("parametri")) {
      buf.append("<big><b>Parametri:</b></big><br/>\n");
      for (Enumeration en = request.getParameterNames(); en.hasMoreElements();) {
        String nome = (String) en.nextElement();
        buf.append("<b>");
        buf.append(nome);
        buf.append("=</b>");
        buf.append(request.getParameter(nome).toString());
        buf.append("<br/>\n");

      }
    } else if (param.equalsIgnoreCase("attributi")) {
      buf.append("<big><b>Attributi:</b></big><br/>\n");
      for (Enumeration en = request.getAttributeNames(); en.hasMoreElements();) {
        String nome = (String) en.nextElement();
        buf.append("<b>");
        buf.append(nome);
        buf.append("=</b>");
        buf.append(request.getAttribute(nome).toString());
        buf.append("<br/>\n");
      }
    } else if (param.equalsIgnoreCase("sessione.attributi")) {
      HttpSession session = pageContext.getSession();
      buf.append("<big><b>Attributi della sessione :</b></big><br/>\n");
      for (Enumeration en = session.getAttributeNames(); en.hasMoreElements();) {
        String nome = (String) en.nextElement();
        buf.append("<b>");
        buf.append(nome);
        buf.append("=</b>");
        buf.append(session.getAttribute(nome).toString());
        buf.append("<br/>\n");
      }

    } else if (param.equalsIgnoreCase("beans")) {
      ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
      buf.append("<big><b>BEANS :</b></big><br/>\n");
      String beans[] = ctx.getBeanDefinitionNames();
      for (int i = 0; i < beans.length; i++) {
        buf.append("<b>");
        buf.append(beans[i]);
        buf.append("=</b>");
        try {
          buf.append(ctx.getBean(beans[i]).toString());
        } catch (Throwable t) {
          buf.append("<I><small>Errore:" + t.getMessage() + "</small></i>");
        }
        buf.append("<br/>\n");
      }
    }
    return buf.toString();
  }

}
