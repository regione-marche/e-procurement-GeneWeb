/*
 * Created on 22-gen-2008
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che genera l'icona per l'espansione o collassamento di tutti i
 * raggruppamenti definiti nella pagina. Parametri: <br>
 * <li>pageContext </li>
 * <li>String identificativo </li>
 * <li>String elenco campi di dettaglio divisi da ; sempre come entita.campo</li>
 * 
 * @author Stefano.Sabbadin
 */
public class ExpandCollapseAllFunction extends AbstractFunzioneTag {

  public ExpandCollapseAllFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    if (pageContext.getRequest().getAttribute("datiRiga") == null) return null;
    StringBuffer buf = new StringBuffer();
    String idIcone = (String) params[1];
    if (idIcone != null) {
      // Verifico che il campo sia visibile
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
      // Se può essere visibile il dettaglio allora aggiungo l'icona
      if (visDettaglio) {
        String id = idIcone;
        buf.append("<a href=\"javascript:showDett");
        buf.append(id);
        buf.append("()\"><img id=\"on");
        buf.append(id);
        buf.append("\" align=\"left\" src=\"");
        buf.append(((HttpServletRequest) pageContext.getRequest()).getContextPath());
        buf.append("/img/TreeExpand.png\" alt=\"Apri tutti i dettagli\" title=\"Apri tutti i dettagli\" >");

        buf.append("<img id=\"off");
        buf.append(id);
        buf.append("\" align=\"left\" src=\"");
        buf.append(((HttpServletRequest) pageContext.getRequest()).getContextPath());
        buf.append("/img/TreeCollapse.png\" alt=\"Chiudi tutti i dettagli\" title=\"Chiudi tutti i dettagli\" style=\"display: none\" >");

        buf.append("</a>&nbsp;");
        Javascript script = UtilityTags.getJavascript(pageContext);
        // Creo la funzione con la visualizzazione o meno del messaggio
        script.println("function showDett" + id + "(){");
        // verifico se l'oggetto "+" non è visibile: in tal caso vuol dire che
        // la struttura è espansa e va compressa, altrimenti vale il contrario
        script.println("var obj=getObjectById(\"on" + id + "\");");
        script.println("var espanso=obj.style.display==\"none\";");
        // se è espanso, va contratto (reso visibile il "+" e invisibile il "-")
        script.print("showObj(\"on");
        script.print(id);
        script.println("\",espanso);");
        script.print("showObj(\"off");
        script.print(id);
        script.println("\",!espanso);");
        Campo campo = null;
        // le sottosezioni vanno portate alla stessa situazione del comando
        // generale per poi arrivare alla stessa situazione finale (tutti con
        // "+" o con "-")
        for (int i = 0; i < campi.length; i++) {
          campo = DizionarioCampi.getInstance().getCampoByNomeFisico(campi[i]);
          if (UtilityTags.checkProtection(pageContext, "COLS", "VIS",
                  campo.getNomeSchema() + "." + campo.getNomeFisicoCampo(),
                  true)) {
            script.print("showObj(\"off");
            script.print(campo.getCodiceMnemonico());
            script.println("\",espanso);");
            script.print("showObj(\"on");
            script.print(campo.getCodiceMnemonico());
            script.println("\",!espanso);");
            script.print("showDett");
            script.print(campo.getCodiceMnemonico());
            script.println("();");
          }
        }
        script.println("}");
      }
    }
    // Come prima cosa verifico se il campo può essere visibile
    return buf.toString();
  }

}
