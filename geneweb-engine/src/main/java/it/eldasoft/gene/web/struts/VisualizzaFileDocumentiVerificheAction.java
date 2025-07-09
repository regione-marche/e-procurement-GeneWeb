package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.verifiche.VerificheInterneManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Esegue il download di un file allegato
 */
public class VisualizzaFileDocumentiVerificheAction extends ActionBaseNoOpzioni {

  static Logger               logger = Logger.getLogger(VisualizzaFileDocumentiVerificheAction.class);

  private SqlManager          sqlManager;

  private VerificheInterneManager          verificheInterneManager;

  /**
   *
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
  *
   */

 public void setVerificheInterneManager(VerificheInterneManager verificheInterneManager) {
   this.verificheInterneManager = verificheInterneManager;
 }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("VisualizzaFileDocumentiVerificheAction-runAction: inizio metodo");

    String target = null;
    String messageKey = null;
    String digkey1 = request.getParameter("digkey1");
    String dignomdoc = request.getParameter("dignomdoc");
    String uffint = request.getParameter("uffint");

    //variabili per tracciatura eventi
    String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
    int livEvento = 3;
    String errMsgEvento = genericMsgErr;

    try {
      List<?> datiDOC_VERIF = this.sqlManager.getVector("select idprg, iddocdig from w_docdig where digent = ? and digkey1 = ?",
              new Object[] { "DOCUMENTI_VERIFICHE", digkey1 });
      if (datiDOC_VERIF != null && !datiDOC_VERIF.isEmpty()) {
        String idprg = (String) SqlManager.getValueFromVectorParam(datiDOC_VERIF, 0).getValue();
        Long iddocdig = (Long) SqlManager.getValueFromVectorParam(datiDOC_VERIF, 1).getValue();
        if(digkey1 != null){
          Long idEntita = new Long(digkey1);
          //Tracciatura eventi
          try {
            LogEvento logEvento = LogEventiUtils.createLogEvento(request);
            logEvento.setLivEvento(1);
            logEvento.setOggEvento(digkey1);
            logEvento.setCodEvento("G_DECIFRATURA_DOCUMENTO");
            logEvento.setDescr("Inizio decifratura documento");
            logEvento.setErrmsg("");
            LogEventiUtils.insertLogEventi(logEvento);
          } catch (Exception le) {
            errMsgEvento = le.getMessage();
            logger.error(genericMsgErr);
          }
          this.verificheInterneManager.downloadFileAllegatoCifrato(dignomdoc, idprg, iddocdig, "DOCUMENTI_VERIFICHE", idEntita, uffint, response);
          //best case
          livEvento = 1;
          errMsgEvento = "";
        }
      } else {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.download.nodocdig";
        errMsgEvento = "Errore durante il download del documento (il documento richiesto non esiste in banca dati)";
        this.aggiungiMessaggio(request, messageKey);
      }

    } catch (Exception io) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.download";
      errMsgEvento = "Errore durante il download del documento";
      this.aggiungiMessaggio(request, messageKey);
    } finally{
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(digkey1);
        logEvento.setCodEvento("G_DECIFRATURA_DOCUMENTO");
        logEvento.setDescr("Fine decifratura documento");
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        logger.error(genericMsgErr);
      }
      logger.info("Decifratura del documento con id = " + digkey1);
    }
    if (logger.isDebugEnabled()) logger.debug("VisualizzaFileDocumentiVerificheAction-runAction: fine metodo");
    if (target != null)
      return mapping.findForward(target);
    else
      return null;
  }

}
