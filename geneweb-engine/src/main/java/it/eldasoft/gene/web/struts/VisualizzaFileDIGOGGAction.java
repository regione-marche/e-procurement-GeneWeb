package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.verifiche.VerificheInterneManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Esegue il download di un file allegato
 */
public class VisualizzaFileDIGOGGAction extends ActionBaseNoOpzioni {

  static Logger               logger = Logger.getLogger(VisualizzaFileDIGOGGAction.class);

  private FileAllegatoManager fileAllegatoManager;

  private SqlManager          sqlManager;

  private VerificheInterneManager          verificheInterneManager;

  /**
   *
   * @param fileAllegatoManager
   */
  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
  *
  * @param verificheInterneManager
  */

 public void setVerificheInterneManager(VerificheInterneManager verificheInterneManager) {
   this.verificheInterneManager = verificheInterneManager;
 }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("VisualizzaFileAllegatoAction-runAction: inizio metodo");

      String target = null;
      String messageKey = null;
      String c0acod = request.getParameter("c0acod");
      String dignomdoc = new String(request.getParameter("dignomdoc"));
      String digent = request.getParameter("digent");
      digent = UtilityStringhe.convertiNullInStringaVuota(digent);
      String uffint = request.getParameter("uffint");
      if("DOCUMENTI_VERIFICHE".equals(digent)){
        //variabili per tracciatura eventi
        String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
        int livEvento = 3;
        String errMsgEvento = genericMsgErr;

        try {
          List<?> datiDOC_VERIF = this.sqlManager.getVector("select idprg, iddocdig from w_docdig where digent = ? and digkey1 = ?",
                new Object[] { "DOCUMENTI_VERIFICHE", c0acod });
          if (datiDOC_VERIF != null && datiDOC_VERIF.size() > 0) {
            String idprg = (String) SqlManager.getValueFromVectorParam(datiDOC_VERIF, 0).getValue();
            Long iddocdig = (Long) SqlManager.getValueFromVectorParam(datiDOC_VERIF, 1).getValue();
            if(c0acod != null){
              Long idEntita = new Long(c0acod);
              //Tracciatura eventi
              try {
                LogEvento logEvento = LogEventiUtils.createLogEvento(request);
                logEvento.setLivEvento(1);
                logEvento.setOggEvento(""+c0acod);
                logEvento.setCodEvento("G_DECIFRATURA_DOCUMENTO");
                logEvento.setDescr("Inizio decifratura documento");
                logEvento.setErrmsg("");
                LogEventiUtils.insertLogEventi(logEvento);
              } catch (Exception le) {
                livEvento = 3;
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
            livEvento = 3;
            errMsgEvento = "Errore durante il download del documento (il documento richiesto non esiste in banca dati)";
            this.aggiungiMessaggio(request, messageKey);
          }

        } catch (Exception io) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.download";
          livEvento = 3;
          errMsgEvento = "Errore durante il download del documento";
          this.aggiungiMessaggio(request, messageKey);
        } finally{
          try {
            LogEvento logEvento = LogEventiUtils.createLogEvento(request);
            logEvento.setLivEvento(livEvento);
            logEvento.setOggEvento(""+c0acod);
            logEvento.setCodEvento("G_DECIFRATURA_DOCUMENTO");
            logEvento.setDescr("Fine decifratura documento");
            logEvento.setErrmsg(errMsgEvento);
            LogEventiUtils.insertLogEventi(logEvento);
          } catch (Exception le) {
            logger.error(genericMsgErr);
          }
          logger.info("Decifratura del documento con id = " + c0acod);
        }

      }else{

        try {
          List<?> datiC0OGGASS = this.sqlManager.getVector("select idprg, iddocdig from w_docdig where digent = ? and digkey1 = ?", new Object[] { "C0OGGASS", c0acod });
          if (datiC0OGGASS != null && datiC0OGGASS.size() > 0) {
            String idprg = (String) SqlManager.getValueFromVectorParam(datiC0OGGASS, 0).getValue();
            Long iddocdig = (Long) SqlManager.getValueFromVectorParam(datiC0OGGASS, 1).getValue();

            String codiceApplicazione = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
            codiceApplicazione = UtilityStringhe.convertiNullInStringaVuota(codiceApplicazione);
            if("".equals(codiceApplicazione)){
              codiceApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
            }
            String codiceProfilo = (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
            Integer idUtente = null;
            if(request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE) != null){
              int idUtenteInt = ((ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();
              idUtente = new Integer(idUtenteInt);
            }
            String ip = request.getRemoteAddr();
            this.fileAllegatoManager.downloadFileAllegato(dignomdoc, idprg, iddocdig, codiceApplicazione, codiceProfilo, idUtente, ip,response);
          } else {
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            messageKey = "errors.download.nodocdig";
            this.aggiungiMessaggio(request, messageKey);
          }
        } catch (Exception io) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.download";
          this.aggiungiMessaggio(request, messageKey);
        }

      }


    if (logger.isDebugEnabled()) logger.debug("VisualizzaFileAllegatoAction-runAction: fine metodo");

    if (target != null)
      return mapping.findForward(target);
    else
      return null;
  }

}
