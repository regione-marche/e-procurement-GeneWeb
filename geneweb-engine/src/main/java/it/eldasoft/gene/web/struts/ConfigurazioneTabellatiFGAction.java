package it.eldasoft.gene.web.struts;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ConfigurazioneTabellatiFGAction extends ActionAjaxLogged {

  static Logger               logger               = Logger.getLogger(ConfigurazioneTabellatiFGAction.class);

  private SqlManager          sqlManager;

  private static final String OPERAZIONE_GETTAB    = "GETTAB";
  private static final String OPERAZIONE_SETCONFIG = "SETCONFIG";

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String target = null;
    String messageKey = null;

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String operazione = request.getParameter("operazione");

    try {

      if (OPERAZIONE_GETTAB.equals(operazione)) {
        // Lettura dei tabellati coninvolti e lettura delle eventuali
        // configurazioni gia' presenti in W_CONFIG.
        JSONArray jsonArray = new JSONArray();

        List<?> datiTabG_043 = sqlManager.getListVector("select tab1tip, tab1desc, tab1arc from tab1 where tab1cod = ? order by tab1tip",
            new Object[] { "G_043" });
        List<?> datiTabKynisi = sqlManager.getListVector("select tab1tip, tab1desc from tab1 where tab1cod = ? order by tab1tip",
            new Object[] { "G_071" });

        if (datiTabG_043 != null && datiTabG_043.size() > 0) {
          for (int t = 0; t < datiTabG_043.size(); t++) {

            Long t_tab1tip = (Long) SqlManager.getValueFromVectorParam(datiTabG_043.get(t), 0).getValue();
            String t_tab1desc = (String) SqlManager.getValueFromVectorParam(datiTabG_043.get(t), 1).getValue();
            String t_tab1arc = (String) SqlManager.getValueFromVectorParam(datiTabG_043.get(t), 2).getValue();

            // La chiave deve essere composta dalla parte fissa "art80.natgiui."
            // concatenata con il valore tab1tip del tabellato G_043
            String t_chiave = "art80.natgiui." + t_tab1tip.toString();
            _addTab_G_043(jsonArray, datiTabKynisi, t_tab1tip, t_tab1desc, t_tab1arc, t_chiave);

          }
        }

        out.print(jsonArray);
        out.flush();

      } else if (OPERAZIONE_SETCONFIG.equals(operazione)) {

        // Salvataggio della configurazione in W_CONFIG.
        String codapp = request.getParameter("codapp");
        String chiave = request.getParameter("chiave");
        String valore = request.getParameter("valore");
        String criptato = request.getParameter("criptato");
        _setW_CONFIG(codapp, chiave, valore, criptato);

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

  private void _addTab_G_043(JSONArray jsonArray, List<?> datiTabKynisi, Long t_tab1tip, String t_tab1desc, String t_tab1arc,
      String t_chiave) {
    JSONObject _oTabG_043 = new JSONObject();
    _oTabG_043.accumulate("t_tab1tip", t_tab1tip);
    _oTabG_043.accumulate("t_tab1desc", t_tab1desc);
    _oTabG_043.accumulate("t_tab1arc", t_tab1arc);
    _oTabG_043.accumulate("t_chiave", t_chiave);

    Long tab1tip_associato = null;
    if (ConfigManager.esisteProprieta(t_chiave) == true) {
      String w_s = ConfigManager.getValore(t_chiave);
      if (w_s != null && !"".equals(w_s)) tab1tip_associato = new Long(w_s);
    }
    _oTabG_043.accumulate("tab1tip_associato", tab1tip_associato);

    for (int w = 0; w < datiTabKynisi.size(); w++) {
      Long kynisi_tab1tip = (Long) SqlManager.getValueFromVectorParam(datiTabKynisi.get(w), 0).getValue();
      String kynisi_tab1desc = (String) SqlManager.getValueFromVectorParam(datiTabKynisi.get(w), 1).getValue();

      JSONObject _oTabKynisi = new JSONObject();
      _oTabKynisi.accumulate("kynisi_tab1tip", kynisi_tab1tip);
      _oTabKynisi.accumulate("kynisi_tab1desc", kynisi_tab1desc);
      if (tab1tip_associato != null && tab1tip_associato.equals(kynisi_tab1tip)) {
        _oTabKynisi.accumulate("associato", "true");
      } else {
        _oTabKynisi.accumulate("associato", "false");
      }

      _oTabG_043.accumulate("tabKynisi", _oTabKynisi);

    }

    jsonArray.add(_oTabG_043);
  }

  private void _setW_CONFIG(String codapp, String chiave, String valore, String criptato) throws SQLException {
    TransactionStatus status = null;
    boolean commitTransaction = false;
    try {
      status = this.sqlManager.startTransaction();

      if ("1".equals(criptato) && valore != null && !"".equals(valore.trim())) {
        ICriptazioneByte valoreICriptazioneByte = null;
        valoreICriptazioneByte = FactoryCriptazioneByte.getInstance(ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
            valore.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
        valore = new String(valoreICriptazioneByte.getDatoCifrato());
      }

      Long cnt = (Long) this.sqlManager.getObject("select count(*) from w_config where codapp = ? and chiave = ?",
          new Object[] { codapp, chiave });
      if (cnt != null && cnt.longValue() > 0) {
        this.sqlManager.update("update w_config set valore = ? where codapp = ? and chiave = ?", new Object[] { valore, codapp, chiave });
      } else {
        this.sqlManager.update("insert into w_config (codapp, chiave, valore, criptato) values (?,?,?,?)",
            new Object[] { codapp, chiave, valore, criptato });
      }

      if (valore == null) valore = new String("");
      if (ConfigManager.esisteProprietaDB(chiave)) {
        ConfigManager.ricaricaProprietaDB(chiave, valore);
      } else {
        ConfigManager.caricaProprietaDB(chiave, valore);
      }

      commitTransaction = true;
    } catch (Exception e) {
      commitTransaction = false;
    } finally {
      if (status != null) {
        if (commitTransaction) {
          this.sqlManager.commitTransaction(status);
        } else {
          this.sqlManager.rollbackTransaction(status);
        }
      }
    }

  }

}
