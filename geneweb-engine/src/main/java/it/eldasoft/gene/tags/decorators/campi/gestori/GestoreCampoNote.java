package it.eldasoft.gene.tags.decorators.campi.gestori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.util.Vector;

public class GestoreCampoNote extends AbstractGestoreCampo {

  // private static int NUMERO_CARATTERI_VISUALIZZAZIONE = 200;

  @Override
  public String getValore(String valore) {
    return null;
  }

  @Override
  public String getValorePerVisualizzazione(String valore) {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 16/11/2006 M.F. Aggiunta del valore di soglia e del pulsante per lo zoom
    // ************************************************************
    if(valore == null || valore.length()==0)
       return null;
    valore=UtilityStringhe.convStringHTML(valore);
    return valore.replaceAll("\n", "<br>\n");

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
    if(this.getCampo().isVisibile()){
      if (!visualizzazione && abilitato) {
        StringBuffer buf = new StringBuffer("");
        // Si tratta di un campo note metto una textarea
        buf.append("<textarea ");
        buf.append(this.getDefaultHtml(true));
        buf.append(UtilityTags.getHtmlAttrib("rows", "4"));
        if(UtilityTags.PAGINA_TROVA.equals(UtilityTags.getParametro(
            this.getPageContext(), UtilityTags.REQUEST_VAR_TIPO_PAGINA)))
          buf.append(UtilityTags.getHtmlAttrib("cols", "70"));
        else
          buf.append(UtilityTags.getHtmlAttrib("cols", "75"));
        if (!abilitato) buf.append(UtilityTags.getHtmlAttrib("readonly", "readOnly"));
        //buf.append(UtilityTags.getHtmlAttrib("onkeyup", "javascript:checkInputLength( this, "+String.valueOf(this.campo.getLen())+" );"));
        buf.append(">");
        buf.append(this.getCampo().getValue());
        buf.append("</textarea>\n");
        this.getCampo().getJs().println("$('textarea[name=\""+ this.campo.getNome() + "\"]').bind('input propertychange', function() {checkInputLength( $(this)[0], " + String.valueOf(this.campo.getLen()) +  ")});");
        return buf.toString();
      } else
        return null;
    } else {
      StringBuffer buf = new StringBuffer("");
      buf.append("<input type=\"hidden\" ");
      buf.append(this.getDefaultHtml(false));
      buf.append(" value=\"" + this.getCampo().getValue() + "\"");
      buf.append("/>");
      return buf.toString();
    }

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

  }

  @Override
  public String gestisciDaTrova(Vector params, DataColumn colWithValue, String conf, SqlManager manager) {
    return null;
  }

}
