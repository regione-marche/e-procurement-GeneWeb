/*
 * Created on 13/nov/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.tags.js.Javascript;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che estrae il titolo di un campo per l'espansione con dettagli
 * Parametri: <br>
 * <li>pageContext</li>
 * <li>String campo come entita.campo </li>
 * <li>String elenco campi di dettaglio divisi da ; sempre come entita.campo;
 * sono i campi da visualizzare o nascondere</li>
 * 
 * @author Marco.Franceschin
 */
public class GetTitleWithExpandCollapseFunction extends AbstractFunzioneTag {

  public GetTitleWithExpandCollapseFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    if (pageContext.getRequest().getAttribute("datiRiga") == null) return null;
    StringBuffer buf = new StringBuffer();
    Campo campo = DizionarioCampi.getInstance().getCampoByNomeFisico(
        (String) params[1]);
    if (campo != null) {
      Tabella tab = DizionarioTabelle.getInstance().getDaNomeTabella(
          campo.getNomeTabella());
      // Verifico che il campo sia visibile
      if (UtilityTags.checkProtection(pageContext, "COLS", "VIS",
          tab.getNomeSchema() + "." + params[1], true)) {
        boolean visDettaglio = false;
        String campi[] = UtilityTags.stringToArray((String) params[2], ';');
        // Verifico che almeno un dettaglio sia visibile
        for (int i = 0; i < campi.length && !visDettaglio; i++) {
          Campo campo1 = DizionarioCampi.getInstance().getCampoByNomeFisico(
              campi[i]);
          if (campo1 != null) {
            Tabella tab1 = DizionarioTabelle.getInstance().getDaNomeTabella(
                campo1.getNomeTabella());
            if (tab1 != null
                && UtilityTags.checkProtection(pageContext, "COLS", "VIS",
                    tab1.getNomeSchema() + "." + campo1.getNomeFisicoCampo(),
                    true)) visDettaglio = true;
          }

        }
        String id = campo.getCodiceMnemonico();
        // Se può essere visibile il dettaglio allora aggiungo l'icona
        if (visDettaglio) {
          buf.append("<a class=\"left\" href=\"javascript:showDett");
          buf.append(id);
          buf.append("()\"><img id=\"on");
          buf.append(id);
          buf.append("\" src=\"");
          buf.append(((HttpServletRequest) pageContext.getRequest()).getContextPath());
          buf.append("/img/TreeExpand.png\" alt=\"Apri dettaglio\" title=\"Apri dettaglio\" >");

          buf.append("<img id=\"off");
          buf.append(id);
          buf.append("\" src=\"");
          buf.append(((HttpServletRequest) pageContext.getRequest()).getContextPath());
          buf.append("/img/TreeCollapse.png\" alt=\"Chiudi dettaglio\" title=\"Chiudi dettaglio\" style=\"display: none\" >");

          buf.append("</a>");
        }
        
        Javascript script = UtilityTags.getJavascript(pageContext);
        
        // Creo la funzione per la visualizzazione o meno della sezione di righe
        // (SS 15-12-2008) NB: la funzione viene creata sempre, al più vuota se
        // non esistono elementi visibili al suo interno
        script.println("function showDett" + id + "(){");
        if (visDettaglio) {
          script.println("var obj=getObjectById(\"off" + id + "\");");
          script.println("var visibile=obj.style.display==\"none\";");
          script.print("showObj(\"off");
          script.print(id);
          script.println("\",visibile);");
          script.print("showObj(\"on");
          script.print(id);
          script.println("\",!visibile);");
          for (int i = 0; i < campi.length; i++) {
            script.print("showObj(\"row");
            script.print(UtilityStringhe.replace(campi[i], ".", "_"));
            script.println("\",visibile);");
          }
        }
        script.println("}");

        // (SS 15-12-2008) le inizializzazioni per i campi della sezione vengono
        // eseguite separaramente, e sempre esclusivamente se almeno un campo è visibile
        if (visDettaglio) {
          for (int i = 0; i < campi.length; i++) {
            script.print("showObj(\"row");
            script.print(UtilityStringhe.replace(campi[i], ".", "_"));
            script.println("\",false);");
          }
        }
      }
      buf.append(campo.getDescrizioneWEB());
    }

    return buf.toString();
  }

}
