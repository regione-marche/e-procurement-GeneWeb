package it.eldasoft.gene.web.struts.w_config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.bl.PropsConfigManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.WsdmPropsConfig;
import it.eldasoft.utils.properties.ConfigManager;


/**
 * Action per il salvataggio delle proprieta' nella tabella W_CONFIG.
 *
 * @author Luca.Giacomazzo
 */
public class SalvaConfigurazioneWsdmAction extends ActionBaseNoOpzioni {

  private static Logger logger = Logger.getLogger(SalvaConfigurazioneWsdmAction.class);

  private PropsConfigManager propsConfigManager;

  public void setPropsConfigManager(PropsConfigManager propsConfigManager) {
    this.propsConfigManager = propsConfigManager;
  }


  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

      logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;

    String idconfi = null;

    try {
    /* Nella pagina non e' stato indicato il gestore di salvataggio delle proprieta'.
     *
     * Si esegue il salvataggio standard delle proprieta: nel request si cercano tre
     * attributi di tipo String[] di uguale lunghezza, con i nomi codapp, chiave, valore.
     */
    String[] arrayIdconfi = request.getParameterValues("idconfi");
    String[] arrayChiave = request.getParameterValues("chiave");
    String[] arrayValore = request.getParameterValues("valore");

    if (arrayIdconfi.length == arrayChiave.length && arrayChiave.length == arrayValore.length) {
      List<WsdmPropsConfig> listaProp = new ArrayList<WsdmPropsConfig>();
      idconfi = arrayIdconfi[0];
      for (int i=0; i < arrayIdconfi.length; i++) {
        WsdmPropsConfig prop = new WsdmPropsConfig();
        String chiave = arrayChiave[i];
        if (arrayIdconfi != null && StringUtils.isNotEmpty(chiave)) {
          prop.setIdconfi(Long.parseLong(arrayIdconfi[i]));
          prop.setChiave(arrayChiave[i]);
          prop.setValore(StringUtils.stripToNull(arrayValore[i]));
          listaProp.add(prop);
        }
      }
      WsdmPropsConfig[] arrayProp = listaProp.toArray(new WsdmPropsConfig[]{});
      this.propsConfigManager.insertWsdmProperties(arrayProp);
      //Si ricaricano le property appena salvate, così da essere subito disponibili senza bisogno di
      //dovere riavviare la webapp
      for (int i = 0; i < arrayProp.length; i++) {
        String valore = arrayProp[i].getValore();
        if (valore == null) valore = new String("");
        String chiaveProp = arrayProp[i].getChiave() + "." + arrayProp[i].getIdconfi();
        if(ConfigManager.esisteProprietaDB(chiaveProp)){
          ConfigManager.ricaricaProprietaDB(chiaveProp,valore);
        }else{
          ConfigManager.caricaProprietaDB(chiaveProp,valore);
        }
      }
    }

    String descri = request.getParameter("descri");
    request.setAttribute("descri", descri);

    String wsdmProtocollo = request.getParameter("wsdmProtocollo");
    request.setAttribute("wsdmProtocollo", wsdmProtocollo);

    String codapp = request.getParameter("codapp");
    request.setAttribute("codapp", codapp);

    } catch (DataAccessException dae) {
      logger.error("Errore nel update dei parametri gestiti da DB (WSDCONFIPRO)", dae);
      this.aggiungiMessaggio(request, "errors.database.dataAccessException");
      target = "errorUpdate";
    }
    logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

}
