package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.integrazioni.Art80Manager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

public class Art80ConsultaFunction extends AbstractFunzioneTag {

  static Logger logger = Logger.getLogger(Art80ConsultaFunction.class);

  public Art80ConsultaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object params[]) throws JspException {

    Art80Manager art80Manager = (Art80Manager) UtilitySpring.getBean("art80Manager", pageContext, Art80Manager.class);

    String codimp = (String) params[1];
    String codein = (String) params[2];

    LogEvento logEvento = LogEventiUtils.createLogEvento((HttpServletRequest) pageContext.getRequest());
    logEvento.setLivEvento(LogEvento.LIVELLO_INFO);
    logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_READ_ART80);
    logEvento.setDescr("Consultazione dei documenti dell'operatore economico " + codimp);

    try {
      HashMap<String, Object> responseHMap = art80Manager.art80ConsultaImpresaCODIMP(codimp, codein);
      pageContext.setAttribute("get_oe_data_response", responseHMap.get("get_oe_data_response"));
      pageContext.setAttribute("id", responseHMap.get("id"));
      pageContext.setAttribute("ragione_sociale", responseHMap.get("ragione_sociale"));
      pageContext.setAttribute("stato", responseHMap.get("stato"));
      pageContext.setAttribute("stato_servizio", responseHMap.get("stato_servizio"));
      pageContext.setAttribute("stato_art_80", responseHMap.get("stato_art_80"));
      pageContext.setAttribute("stato_art_80_descrizione", responseHMap.get("stato_art_80_descrizione"));
      pageContext.setAttribute("stato_doc_sca", responseHMap.get("stato_doc_sca"));
      pageContext.setAttribute("data_inserimento", responseHMap.get("data_inserimento"));
      pageContext.setAttribute("cf", responseHMap.get("cf"));
      pageContext.setAttribute("iva", responseHMap.get("iva"));
      pageContext.setAttribute("documents", responseHMap.get("documents"));
      pageContext.setAttribute("url", responseHMap.get("url"));
      pageContext.setAttribute("token", responseHMap.get("token"));

    } catch (GestoreException e) {
      pageContext.setAttribute("error", e.getMessage());
      logEvento.setLivEvento(LogEvento.LIVELLO_ERROR);
      logEvento.setErrmsg("Errore nel tentativo di lettura dei documenti");
      
      if (logger.isDebugEnabled()) {
        logger.debug("__art80Consulta: " + e);
      }
      
    } finally {
      LogEventiUtils.insertLogEventi(logEvento);
    }
    return null;

  }

}