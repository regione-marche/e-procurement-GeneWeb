package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.sql.sqlparser.JdbcWhere;
import it.eldasoft.gene.tags.decorators.scheda.FormSchedaTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
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

  private GeneManager geneManager;

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

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("VisualizzaFileAllegatoAction-runAction: inizio metodo");

      String target = null;
      String messageKey = null;
      String c0acod = request.getParameter("c0acod");
      String dignomdoc = new String(request.getParameter("dignomdoc"));

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    String entitaDB = null;
    try {
      // per prima cosa si estraggono le chiavi e l'entita' indicata in db filtrando per c0acod
      List<?> datiChiavePadre = this.sqlManager.getVector("select c0aent, c0akey1, c0akey2, c0akey3, c0akey4, c0akey5 from c0oggass where c0acod = ?",
              new Object[] { c0acod });
      entitaDB = (String) SqlManager.getValueFromVectorParam(datiChiavePadre, 0).getValue();
      Tabella tabellaDaMetadati = DizionarioTabelle.getInstance().getDaNomeTabella(entitaDB);
      List<Campo> campiChiaveDaMetadati = tabellaDaMetadati.getCampiKey();
      // si costruisce la chiave nel formato [ENTITA.CAMPO=[T|N]:VALORE[;]]+
      StringBuilder key = new StringBuilder("");
      for (int i = 0; i < campiChiaveDaMetadati.size(); i++) {
        if (i > 0) {
          key.append(";");
        }
        String tipoCampo = campiChiaveDaMetadati.get(i).getTipoColonna() == Campo.TIPO_INTERO ? "N" : "T";
        String valorePorzioneChiave = (String) SqlManager.getValueFromVectorParam(datiChiavePadre, (i+1)).getValue();
        key.append(campiChiaveDaMetadati.get(i).getNomeFisicoCampo()).append("=").append(tipoCampo).append(":").append(valorePorzioneChiave);
      }
      // trattasi di utente loggato, quindi eseguo i controlli sulla visibilita' dei dati
      String chiaveEntitaControllataConPermessi = FormSchedaTag.trovaChiaveEntitaControllataConPermessi(key.toString(), profiloUtente, sqlManager, geneManager);
      // se il collegamento con G_PERMESSI e' stato trovato, allora eseguo una query sull'entita' che la contiene filtrando con la condizione di visibilita'
      if (chiaveEntitaControllataConPermessi != null) {
        String[] datiDaElaborare = chiaveEntitaControllataConPermessi.split("#");
        JdbcWhere where = new JdbcWhere();
        UtilityTags.jdbcAddKeyWhere(where, datiDaElaborare[2]);
        String query = "SELECT COUNT(*) FROM "
                + datiDaElaborare[0]
                + where.toString()
                + " AND "
                + datiDaElaborare[1];

        long numOccorrenze = (Long) sqlManager.getObject(query, SqlManager.getObjectFromPram(where.getParametri()));
        // si sta tentando di forzare l'accesso ad un record non visibile all'utente
        if (numOccorrenze == 0) {
          logger.error("Bloccato accesso al documento associato con chiave " + c0acod + " dell'entita' " + entitaDB + " con chiave " + key + " perche' non e' visibile all'utente " + profiloUtente.getId());
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.download.unauthorized";
          this.aggiungiMessaggio(request, messageKey);
        }
      }
    } catch (Exception e) {
      logger.error("Errore durante le verifiche se il record di c0oggass con chiave " + c0acod + " risulta visibile all'utente " + profiloUtente.getId());
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.download.unauthorized";
      this.aggiungiMessaggio(request, messageKey);
    }

    if (target == null) {
      // se i controlli preventivi sono andati a buon fine, allora si prosegue con la logica applicativa reale
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
