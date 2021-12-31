package it.eldasoft.gene.web.struts.w_config;

import it.eldasoft.gene.bl.PropsConfigManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.PropsConfig;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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


/**
 * Action per il salvataggio delle proprieta' nella tabella W_CONFIG.
 *
 * @author Luca.Giacomazzo
 */
public class SalvaConfigurazioneAction extends ActionBaseNoOpzioni {

  private static Logger logger = Logger.getLogger(Logger.class);

  private PropsConfigManager propsConfigManager;

  public void setPropsConfigManager(PropsConfigManager propsConfigManager) {
    this.propsConfigManager = propsConfigManager;
  }


  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }

    String target = CostantiGeneraliStruts.FORWARD_OK;

    String classeGestore = request.getParameter("gestoreProprieta");
    String titoloSezione = request.getParameter("sezione");

    AbstractGestoreProprieta gestoreProprieta = null;

    try {
    if (StringUtils.isNotEmpty(classeGestore)) {

      Object obj = UtilityTags.createObject(classeGestore);

      if (obj != null && (obj instanceof AbstractGestoreProprieta)) {
        AbstractGestoreProprieta gest = (AbstractGestoreProprieta) obj;
        gest.setRequest(request);
        gestoreProprieta = (AbstractGestoreProprieta) obj;

        //Nel gestore si deve implementare la logica per il ricaricamento delle property appena salvate, così da
        //essere subito disponibili senza bisogno di dovere riavviare la webapp
        gestoreProprieta.update();

      } else {
        // se non esiste il gestore indicato o non è una AbstractGestoreEntita
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;

        logger.error("Il gestore indicato nel parameter gestoreProprieta non esiste o non e' di tipo AbstractGestoreProprieta " +
        		"(gestoreProprieta=" + gestoreProprieta + ")" );
        this.aggiungiMessaggio(request, "errors.gestoreException.gestore.classeErrata");
      }

    } else {

      /* Nella pagina non e' stato indicato il gestore di salvataggio delle proprieta'.
       *
       * Si esegue il salvataggio standard delle proprieta: nel request si cercano tre
       * attributi di tipo String[] di uguale lunghezza, con i nomi codapp, chiave, valore.
       */
      String[] arrayCodapp = request.getParameterValues("codapp");
      String[] arrayChiave = request.getParameterValues("chiave");
      String[] arrayValore = request.getParameterValues("valore");

      if (arrayCodapp.length == arrayChiave.length && arrayChiave.length == arrayValore.length) {
        //List<PropsConfig> listaProp = new ArrayList<PropsConfig>();
        HashMap<String,List<PropsConfig>> hm = new HashMap<String,List<PropsConfig>>();
        for (int i=0; i < arrayCodapp.length; i++) {
          PropsConfig prop = new PropsConfig();
          String codApp = arrayCodapp[i];
          String chiave = arrayChiave[i];
          if (StringUtils.isNotEmpty(codApp) && StringUtils.isNotEmpty(chiave)) {
            prop.setCodApp(arrayCodapp[i]);
            prop.setChiave(arrayChiave[i]);
            prop.setValore(StringUtils.stripToNull(arrayValore[i]));
            if(hm.containsKey(codApp)){
              hm.get(codApp).add(prop);
            }else{
              hm.put(codApp, new ArrayList<PropsConfig>());
              hm.get(codApp).add(prop);
            }
            //listaProp.add(prop);
          }
        }
        for (List<PropsConfig> value : hm.values()) {
          PropsConfig[] arrayProp = value .toArray(new PropsConfig[]{});
          this.propsConfigManager.insertProperties(arrayProp);
          //Si ricaricano le property appena salvate, così da essere subito disponibili senza bisogno di
          //dovere riavviare la webapp
          for (int i = 0; i < arrayProp.length; i++) {
            String valore = arrayProp[i].getValore();
            if (valore == null) valore = new String("");
            ConfigManager.ricaricaProprietaDB(arrayProp[i].getChiave(),valore);
          }
        }
      }
    }

    } catch (DataAccessException dae) {
      logger.error("Errore nel update dei parametri gestiti da DB (W_CONFIG)", dae);
      this.aggiungiMessaggio(request, "errors.database.dataAccessException");
      target = "errorUpdate";

    } catch (Throwable t) {
      target = "errorUpdate";
      logger.error("Errore inaspettato nel update dei parametri gestiti da DB (W_CONFIG)", t);

    }
    

    titoloSezione = UtilityStringhe.convertiNullInStringaVuota(titoloSezione);
    if(!"".equals(titoloSezione)){
      String descrEvento = "";
      descrEvento = "Modifica parametri di configurazione della sezione '" + titoloSezione + "'";
      
      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(1);
      logEvento.setOggEvento("");
      logEvento.setCodEvento("SET_CONFIG_SEZIONE");
      logEvento.setDescr(descrEvento);
      logEvento.setErrmsg("");
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }

    return mapping.findForward(target);
  }

}
