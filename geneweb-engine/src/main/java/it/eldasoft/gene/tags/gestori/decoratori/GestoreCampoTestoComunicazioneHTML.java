/*
 * Created on 09/07/2015
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.gestori.decoratori;

import java.util.HashMap;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Gestore HTML applicato al campo CLOB W_INVCM.COMMSGTES.
 * Rispetto al gestore NoteHtml, è stato forzato il dominio in modo da evitare il controllo sulla lunghezza caratteri.
 * 
 * @author Sara.Santi
 */
public class GestoreCampoTestoComunicazioneHTML extends AbstractGestoreCampo {

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

   HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
           PageContext.REQUEST_SCOPE);

   String tipoMessaggio = datiRiga.get("W_INVCOM_COMMSGTIP").toString();

   if (tipoMessaggio.equals("1")){
     return valore;
   }
   else {
     valore=UtilityStringhe.convStringHTML(valore);
     return valore.replaceAll("\n", "<br>\n");
   }
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
        buf.append(UtilityTags.getHtmlAttrib("rows", "8"));
        if(UtilityTags.PAGINA_TROVA.equals(UtilityTags.getParametro(
            this.getPageContext(), UtilityTags.REQUEST_VAR_TIPO_PAGINA)))
          buf.append(UtilityTags.getHtmlAttrib("cols", "70"));
        else
          buf.append(UtilityTags.getHtmlAttrib("cols", "75"));
        if (!abilitato) buf.append(UtilityTags.getHtmlAttrib("readonly", "readOnly"));
        buf.append(">");
        buf.append(this.getCampo().getValue());
        buf.append("</textarea>\n");
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
    this.getCampo().setDominio("CLOB", this.getPageContext());
  }

  @Override
  public String gestisciDaTrova(Vector params, DataColumn colWithValue, String conf, SqlManager manager) {
    return null;
  }

}
