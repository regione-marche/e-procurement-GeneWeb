/*
 * Created on 01/giu/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.tags.utils.UtilityTags;

import javax.servlet.jsp.PageContext;

/**
 * Funzione che estrae il titolo per gli archivi generali
 * 
 * @author Marco.Franceschin
 * 
 */
public class GetTitleFunction extends AbstractGetTitleFunction {

  public String[] initFunction() {
    return new String[] {
        "UTENT|Nuovo soggetto|Soggetto {0}"
            + "||select codute, nomute from utent where codute = #UTENT.CODUTE#",
        "TECNI|Nuovo tecnico|Tecnico {0}"
            + "||select codtec, nomtec from tecni where codtec = #TECNI.CODTEC#",
        "TEIM|Nuovo tecnico|Tecnico {0}"
            + "||select codtim, nomtim from teim where codtim = #TEIM.CODTIM#",
        "IMPR|Nuova impresa|Impresa {0}"
            + "||select codimp, nomimp from impr where codimp = #IMPR.CODIMP#",
        "ASTRA|Nuova via|Via {0}"
            + "||select codvia, viapia from astra where codvia = #ASTRA.CODVIA#",
        "ASTRADET|Nuovo dettaglio strada|Dettaglio strada {0}"
            + "||select codvia, viapia from astradet where codvia = #ASTRADET.CODVIA#" ,
        "IMPDURC|Titolo inserimento|Titolo modifica/visualizzazione||",
        "IMPCASE|Nuova comunicazione casellario giudiziale per l'impresa {0} "
            + "|Comunicazione casellario giudiziale per l'impresa {0} "
            + "|select codimp from impr where codimp = #CODIMP# "
            + "|select codimp, nomimp from impr where codimp = #IMPCASE.CODIMP# ",
        "IMPANTIMAFIA|Nuovo accertamento antimafia per l'impresa {0} "
            + "|Accertamento antimafia per l'impresa {0} "
            + "|select codimp from impr where codimp = #CODIMP# "
            + "|select codimp from impr where codimp = #IMPANTIMAFIA.CODIMP# ",
        "UFFINT|"
            + this.resBundleGenerale.getString("label.tags.uffint.nuovo")
            + " "
            + this.resBundleGenerale.getString("label.tags.uffint.singolo").toLowerCase()
            + "|"
            + this.resBundleGenerale.getString("label.tags.uffint.singolo")
            + " {1}"
            + "||select codein, nomein from uffint where codein = #UFFINT.CODEIN#" };
  }

  protected String getTitleInserimento(PageContext pageContext, String table) {
    String ret=this.getTitoloIMPDURC(pageContext, table, "NUOVO");
    return ret;
  }

  protected String getTitleModifica(PageContext pageContext, String table,
      String keys) {
    String ret=this.getTitoloIMPDURC(pageContext, table, "MODIFICA");
    return ret;
  }
  
  /**
   * Viene creato il titolo per la pagina del DURC on line
   * @param pageContext
   * @param table
   * @param modo
   * @return
   */
  private String getTitoloIMPDURC(PageContext pageContext, String table, String modo){
    String ret=null;
    if("IMPDURC".equals(table)){
      if("NUOVO".equals(modo))
        ret = "Nuova richiesta DURC on line per l'impresa";
      else
        ret = "Richiesta DURC on line per l'impresa";
      String key = UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT);
      String codimp=GeneralTagsFunction.getValCampo(key, "IMPR.CODIMP");
      ret+= " " + codimp;
    }
    return ret;
  }
  
}
