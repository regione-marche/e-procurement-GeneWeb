package it.eldasoft.gene.tags.decorators.campi.gestori;

import java.util.Vector;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Gestore del campo note per la visualizzazione dell'editor come TinyMCE
 * 
 * @author Marco.Franceschin
 * 
 */
public class GestoreCampoNoteTinyMCE extends AbstractGestoreCampo {

  // private static int NUMERO_CARATTERI_VISUALIZZAZIONE = 200;

  public String getValore(String valore) {
    return null;
  }

  public String getValorePerVisualizzazione(String valore) {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 16/11/2006 M.F. Aggiunta del valore di soglia e del pulsante per lo zoom
    // ************************************************************
    if (valore == null || valore.length() == 0) return null;
    return valore.replaceAll("\n", "<br>\n");

  }

  public String getValorePreUpdateDB(String valore) {
    return null;
  }

  public String preHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  public String getHTML(boolean visualizzazione, boolean abilitato) {
    if (!visualizzazione) {
      StringBuffer buf = new StringBuffer("");
      // Si tratta di un campo note metto una textarea
      buf.append("<textarea ");
      buf.append(this.getDefaultHtml(true));
      buf.append(UtilityTags.getHtmlAttrib("rows", "20"));
      buf.append(UtilityTags.getHtmlAttrib("cols", "90"));
      if (!abilitato) buf.append(UtilityTags.getHtmlAttrib("readonly", "readOnly"));
      buf.append(UtilityTags.getHtmlAttrib("onkeyup",
          "javascript:checkInputLength( this, "
              + String.valueOf(this.campo.getLen())
              + " );"));
      buf.append(UtilityTags.getHtmlAttrib("class","mceEditor"));
      buf.append(">");
      buf.append(this.getCampo().getValue());
      buf.append("</textarea>\n");
      return buf.toString();
    }
    return null;
  }

  public String postHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  public String getClasseEdit() {
    return null;
  }

  public String getClasseVisua() {
    return null;
  }

  protected void initGestore() {

  }

  public String gestisciDaTrova(Vector params, DataColumn colWithValue, String conf, SqlManager manager) {
    return null;
  }

}
