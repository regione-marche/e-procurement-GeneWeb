/*
 * Created on 27-ott-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.campi.gestori.c0oggass;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.web.struts.docass.CostantiDocumentiAssociati;
import it.eldasoft.utils.properties.ConfigManager;

import java.util.Vector;

/**
 * Gestore del campo c0nomogg della tabella c0oggass
 *
 * @author Luca Giacomazzo
 */
public class GestoreCampoNomeOggetto extends AbstractGestoreCampo {

  private static final String DEFAULT_PATH = "[default]";

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
    // ridefinizione del preHTML per accedere al path definito nel file di properties
    StringBuffer href = new StringBuffer("");

    //il valore del campo dalla pagina e' pari a:
    // <path del docAss> + # + <nome file>
    //Quindi la seguente variabile contiene le due componenti separate dell'URI
    //necessarie per raggiungere il file
    String[] tmp = this.campo.getValue().split("#");

    //Si visualizza solo il nome del file
    this.campo.setValue(tmp[1]);

    if(DEFAULT_PATH.equalsIgnoreCase(tmp[0])){
    tmp[0] = ConfigManager.getValore(
        CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI_CLIENT +
        CostantiGenerali.SEPARATORE_PROPERTIES +
        ConfigManager.getValore(CostantiGenerali.PROP_ID_APPLICAZIONE) +
        CostantiGenerali.SEPARATORE_PROPERTIES +
        this.getPageContext().getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
    }

    if(this.campo.isVisualizzazione())
      href.append("<a ");

    if(this.campo.isVisualizzazione()){
      if(!CostantiDocumentiAssociati.DISABILITA_DOWNLOAD.equals(
          ConfigManager.getValore(
              CostantiDocumentiAssociati.PROP_DOWNLOAD_DOCUMENTI_ASSOCIATI))){
        href.append("href=\"javascript:downloadDocAss('" + tmp[1] + "');");
      } else {
        href.append("href=\"javascript:apriDocumento('" + tmp[0] + tmp[1] + "');");
      }
    }

    if(this.campo.isVisualizzazione())
      return href.append("\">").toString();
    else
      return href.toString();
  }

  @Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {
      return null;
  }

  @Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {
    // ridefinizione del postHTML per accedere al path definito nel file di properties
    if(this.campo.isVisualizzazione())
      return "</a></td>";
    else {
      return null;
    }
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