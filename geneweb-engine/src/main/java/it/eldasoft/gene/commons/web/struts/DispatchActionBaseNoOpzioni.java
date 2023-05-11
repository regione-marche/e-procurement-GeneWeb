package it.eldasoft.gene.commons.web.struts;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;

/**
 * Classe da cui ereditare per azioni senza opzioni da verificare
 * 
 * @author marco.franceschin
 */
public abstract class DispatchActionBaseNoOpzioni extends DispatchActionBase {

  /**
   * @see it.eldasoft.gene.commons.web.struts.DispatchActionBase#getOpzioneAcquistata()
   */
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_DEFAULT;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.DispatchActionBase#verificaAbilitazioneOpzione(javax.servlet.http.HttpServletRequest, org.apache.struts.action.ActionMapping)
   */
  protected boolean verificaAbilitazioneOpzione(HttpServletRequest request, ActionMapping mapping) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    /////////////////////////////////////////////////////////////////
    // Modifiche
    // ----------------
    // 23/02/2007 M.F. Elimino il controllo delle opzioni perche questa Dispatch Action non deve gestire le opzioni
    /////////////////////////////////////////////////////////////////
    return true;
  }

}
