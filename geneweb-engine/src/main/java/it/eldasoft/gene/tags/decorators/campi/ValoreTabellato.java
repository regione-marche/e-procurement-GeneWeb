/*
 * Created on 13-feb-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.campi;

import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.utility.UtilityStringhe;

import org.apache.commons.lang.StringUtils;

/**
 * Bean per gestire un valore di tabellato costituito da codice/valore e
 * descrizione
 *
 * @author Stefano.Sabbadin
 */
public class ValoreTabellato {

  private String valore;

  private String descr;

  private String arc;

  public ValoreTabellato(String valore, String descr) {
    this.valore = (valore == null ? "" : valore);
    this.descr = (descr == null ? " " : descr);
    this.arc = " ";
  }

  public ValoreTabellato(String valore, String descr, String arc) {
    this.valore = (valore == null ? "" : valore);
    this.descr = (descr == null ? " " : descr);
    this.arc = (arc == null ? " " : arc);
  }

  /**
   * @return Returns the descr.
   */
  public String getDescr() {
    return descr;
  }

  /**
   * @return Returns the valore.
   */
  public String getValore() {
    return valore;
  }

  /**
   *
   * @return Returns the arc.
   */
  public String getArc() {
    return arc;
  }

  /**
   * Converto il valore in un option
   */
  public String toString(String value) {
    StringBuffer buf = new StringBuffer();
    buf.append("<option value=\"");
    buf.append(UtilityTags.convStringa(this.valore));
    buf.append("\" title=\"");
    buf.append(UtilityStringhe.convStringHTML(this.descr));
    buf.append("\" ");
    // Se è il valore giusto allora lo imposto come selezionato
    if (value != null && value.equals(this.valore))
      buf.append("selected=\"selected\" ");
    // Se l'opzione e' archiviata modifico il colore di sfondo
    if ("1".equals(this.arc)) {
      buf.append("style=\"background-color: #CCCCCC\" ");
    }
    buf.append(">");
    buf.append(UtilityStringhe.convStringHTML(StringUtils.abbreviate(this.descr, 100)));
    buf.append("</option>\n");
    return buf.toString();
  }

  @Override
  public String toString() {
    return UtilityTags.convStringa(this.getDescr());
  }

  @Override
  public boolean equals(Object obj) {
    boolean esito = false;
    // Sono uguali solo se hanno lo stesso valore
    if (obj instanceof ValoreTabellato) {
      String lVal = ((ValoreTabellato) obj).getValore();
      esito = (lVal.equals(this.valore));
    }
    return esito;
  }
}
