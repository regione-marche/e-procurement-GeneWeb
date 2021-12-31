package it.eldasoft.gene.tags.decorators.campi.gestori;

import java.util.Vector;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.utils.properties.ConfigManager;


/**
 * Gestore del campo CODAPP nel form di trova della W_LOGEVENTI.
 * 
 * @author Luca.Giacomazzo
 */
public class GestoreCampoModuloAttivo extends AbstractGestoreCampo {

  @Override
  public String getValore(String valore) {
    return null;
  }

  @Override
  public String getValorePerVisualizzazione(String valore) {
    return null;
  }

  @Override
  public String getValorePreUpdateDB(String valore) {
    return null;
  }

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  @Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  @Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  @Override
  public String getClasseEdit() {
    return null;
  }

  @Override
  public String getClasseVisua() {
    return null;
  }

  @Override
  protected void initGestore() {
    
    String codiceApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
    String moduloAttivo = (String) this.getPageContext().getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    
    if (!StringUtils.equalsIgnoreCase(codiceApplicazione, moduloAttivo)) {
      
      this.getCampo().setTipo("ET2");
      this.getCampo().getValori().clear();
      this.getCampo().addValore("", "");
      
      String[] moduli = codiceApplicazione.split(";");
      
      for (int i=0; i < moduli.length; i++) {
        this.getCampo().addValore(moduli[i], moduli[i]);
      }
        
      this.getPageContext().setAttribute("codAppVisibile", Boolean.TRUE , PageContext.REQUEST_SCOPE);
    } else {
      this.getPageContext().setAttribute("codAppVisibile", Boolean.FALSE , PageContext.REQUEST_SCOPE);
    }

  }

  @Override
  public String gestisciDaTrova(Vector params, DataColumn col, String conf,
      SqlManager manager) {
    return null;
  }

}
