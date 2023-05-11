/*
 * Created on 11/apr/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Estrae l'elenco dei valori del tabellato 'G_061', lo associa al valore presente su WLSEZIO.IMPR e lo inserisce nel request con un attributo denominato come il parametro
 * in input.
 *
 * @author Francesco.DiMattei
 */
public class GetListaSezioniIscrizioneFunction extends AbstractFunzioneTag {

  public GetListaSezioniIscrizioneFunction() {
    super(3, new Class[] {PageContext.class, String.class, String.class });
  }

  /**
   * @see it.eldasoft.gene.tags.utils.AbstractFunzioneTag#function(javax.servlet.jsp.PageContext, java.lang.Object[])
   */
  @Override
  public String function(PageContext pageContext, Object[] args) throws JspException {
    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", pageContext, TabellatiManager.class);

    String codice = "G_061";
    String codimp = (String) args[1];
    String nomeAttributo = (String) args[2];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
        "sqlManager", pageContext, SqlManager.class);
    
    List<Tabellato> lista = tabellatiManager.getTabellato(codice);
    String wlsezio = "";
    String[] wlsezioArray = null;
    List listaTabWlsezio = new ArrayList();

    try {
      String select = "select wlsezio from impr where codimp=?";
      wlsezio = (String) sqlManager.getObject(select, new Object[] {codimp});
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura del campo 'Sezioni di iscrizione' da IMPR" ,e);
    }

    if (wlsezio != null && wlsezio != "") {
      wlsezioArray = wlsezio.split("-");
    }
    if (lista != null && lista.size() > 0) {
      for (int i = 0; i < lista.size(); i++) {
        Tabellato tabellato = (Tabellato)lista.get(i);
        String tipo = tabellato.getTipoTabellato();
        String descrizione = tabellato.getDescTabellato();
        boolean tipoTabPresente = false;
        if (wlsezioArray != null && wlsezioArray.length > 0) {
          for (int j = 0; j < wlsezioArray.length; j++) {
            if (tipo.equals(wlsezioArray[j])) {
              tipoTabPresente = true;
              break;
            }
          }
        }
        listaTabWlsezio.add(i,new Object[] {tipo, descrizione, tipoTabPresente });
      }
    }
    pageContext.setAttribute(nomeAttributo, listaTabWlsezio, PageContext.REQUEST_SCOPE);
    return null;
  }

}
