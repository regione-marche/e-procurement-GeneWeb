/*
 * Created on 09/nov/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.campi.gestori;

import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Gestore per il campo password utilizzato con codifica standard ad esempio per
 * USRSYS.SYSPWD. In edit viene generato il campo di input password
 * 
 * @author Stefano.Sabbadin
 * @since 1.4.4
 */
public class GestoreCampoPassword extends GestoreCampoLogin {

  public String getHTML(boolean visualizzazione, boolean abilitato) {
    String risultato = null;
    if (!visualizzazione && abilitato) {
      StringBuffer buf = new StringBuffer("");
      buf.append("<input ");
      buf.append(this.getDefaultHtml(true));
      buf.append(UtilityTags.getHtmlAttrib("class", "testo"));
      buf.append(UtilityTags.getHtmlAttrib("type", "password"));
      buf.append(UtilityTags.getHtmlAttrib("size",
          String.valueOf(this.getCampo().getLenForInput())));
      buf.append(UtilityTags.getHtmlAttrib("value", this.getCampo().getValue()));
      buf.append(UtilityTags.getHtmlAttrib("maxlength",
          String.valueOf(this.getCampo().getLen())));
      buf.append("/>");
      risultato = buf.toString();
    }

    return risultato;
  }

}
