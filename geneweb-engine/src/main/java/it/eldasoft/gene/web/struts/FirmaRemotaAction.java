/*
 * 	Created on 09/04/2018
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.integrazioni.FirmaRemotaManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.utils.utility.UtilityWeb;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class FirmaRemotaAction extends ActionBaseNoOpzioni {

  protected static final String FORWARD_SUCCESS = "firmaremotasignsuccess";
  protected static final String FORWARD_ERROR   = "firmaremotasignerror";

  static Logger                 logger          = Logger.getLogger(FirmaRemotaAction.class);

  private FileAllegatoManager   fileAllegatoManager;

  private SqlManager            sqlManager;

  private FirmaRemotaManager    firmaRemotaManager;

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public FirmaRemotaManager getFirmaRemotaManager() {
    return firmaRemotaManager;
  }

  public void setFirmaRemotaManager(FirmaRemotaManager firmaRemotaManager) {
    this.firmaRemotaManager = firmaRemotaManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("FirmaRemotaAction: inizio metodo");

    String target = FORWARD_SUCCESS;
    String messageKey = null;

    try {

      String idprg = request.getParameter("idprg");
      Long iddocdig = null;
      if (request.getParameter("iddocdig") != null) {
        iddocdig = new Long(request.getParameter("iddocdig"));
      }
      String operazione = request.getParameter("operazione");
      String modalita = request.getParameter("modalita");
      String formato = request.getParameter("formato");
      String alias = request.getParameter("alias");
      String pin = request.getParameter("pin");
      String otp = request.getParameter("otp");

      String report = null;
      
      String requestOtpStatus = null;

      BlobFile bfOriginal = null;

      if ("verifica".equals(operazione)) {
        report = firmaRemotaManager.verify(idprg, iddocdig);
      } else if ("estrae".equals(operazione)) {
        bfOriginal = firmaRemotaManager.extract(idprg, iddocdig);
      } else if ("firma".equals(operazione)) {
        firmaRemotaManager.sign(idprg, iddocdig, modalita, formato, alias, pin, otp);
      } else if ("richiesta-otp".equals(operazione)) {
        requestOtpStatus = firmaRemotaManager.remoteRequestOtp(alias);
      }

      request.getSession().setAttribute("idprg", idprg);
      request.getSession().setAttribute("iddocdig", iddocdig);
      request.getSession().setAttribute("operazione", operazione);
      request.getSession().setAttribute("modalita", modalita);
      request.getSession().setAttribute("formato", formato);
      request.getSession().setAttribute("alias", alias);
      request.getSession().setAttribute("pin", pin);
      request.getSession().setAttribute("otp", otp);

      request.getSession().setAttribute("report", report);
      
      request.getSession().setAttribute("requestOtpStatus", requestOtpStatus);

      if ("firma".equals(operazione)) {
        String dignomdoc = (String) this.sqlManager.getObject("select dignomdoc from w_docdig where idprg = ? and iddocdig = ?",
            new Object[] { idprg, iddocdig });
        BlobFile bf = this.fileAllegatoManager.getFileAllegato(idprg, iddocdig);
        UtilityWeb.download(dignomdoc, bf.getStream(), response);
      }

      if ("estrae".equals(operazione)) {
        UtilityWeb.download(bfOriginal.getNome(), bfOriginal.getStream(), response);
      }

    } catch (Exception e) {
      target = FORWARD_ERROR;
      messageKey = "errors.firmaremota.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (messageKey != null) response.reset();
    if (logger.isDebugEnabled()) logger.debug("FirmaRemotaAction: fine metodo");

    return mapping.findForward(target);

  }

}
