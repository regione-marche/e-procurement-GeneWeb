/*
 * Created on 24-nov-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.utils;

import it.eldasoft.gene.bl.MetadatiManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.spring.UtilitySpring;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Classe che gestisce l'help su un mnemonico
 * 
 * @author cit_franceschin
 * 
 */
public class HelpMnemonico {

  private static final String PATH_MNEMONICI = "/help/it/mne/";
  private String              mnemonico;
  Campo                       campo;
  Tabella                     tabella;
  String                      pathFileHelp;
  PageContext                 page;

  /**
   * Costruttore di default della classe
   * 
   * @param request
   */
  public HelpMnemonico(PageContext page, String mnemonico) {
    if (mnemonico == null)
      this.mnemonico = page.getRequest().getParameter("mnemonico");
    else
      this.mnemonico = mnemonico;
    this.campo = DizionarioCampi.getInstance().get(this.mnemonico);
    if (this.campo != null) {
      this.tabella = DizionarioTabelle.getInstance().getDaNomeTabella(
          campo.getNomeTabella());
    }
    this.page = page;
  }

  /**
   * Restituisce il path della spiegazione del campo
   * 
   * @return path se esiste, "" se non esiste
   * @throws JspException
   */
  public String getPathFileHelp() throws JspException {
    String path = PATH_MNEMONICI + this.getMnemonico() + ".jsp";
    URL percorso = null;
    try {
      percorso = page.getSession().getServletContext().getResource(path);
    } catch (MalformedURLException e) {
      throw new JspException(e.getMessage(), e);
    }
    if (percorso != null)
      return path;
    else
      return "";
  }

  /**
   * @return Returns the mnemonico.
   */
  public String getMnemonico() {
    return mnemonico;
  }

  public String getDominio() {
    if (campo != null)
      return campo.getDominio() != null ? campo.getDominio() : "";
    else
      return "";
  }

  public String getTipoColonna() {
    String result = "";
    if (campo != null) {
      switch (campo.getTipoColonna()) {
      case Campo.TIPO_DATA:
        result = "Data";
        break;
      case Campo.TIPO_DECIMALE:
        result = "Numero con virgola ";
        // result += campo.getLunghezza() + ".2";
        break;
      case Campo.TIPO_INTERO:
        result = "Numero intero ";
        // result += campo.getLunghezza();
        break;
      case Campo.TIPO_NOTA:
        result = "Nota ";
        // result += campo.getLunghezza();
        break;
      case Campo.TIPO_STRINGA:
        result = "Stringa ";
        // result += campo.getLunghezza();
        break;
      }
    }
    return result;
  }

  public String getDescrizione() {
    if (campo != null) return campo.getDescrizione();
    return "";
  }
  
  public String getNomeFisico() {
    if (campo != null) return campo.getNomeFisicoCampo();
    return "";    
  }

  public String getEntita() {
    if (tabella != null) {
      return tabella.getCodiceMnemonico();
    }
    return "";
  }

  public String getSchema() {
    if (tabella != null) {
      return tabella.getNomeSchema();
    }
    return "";
  }

  public String getDescrSchema() {
    String result = "";
    if (tabella != null) {
      DizionarioSchemi dizSchemi = DizionarioSchemi.getInstance();
      if (dizSchemi.get(tabella.getNomeSchema()) != null) {
        result = dizSchemi.get(tabella.getNomeSchema()).getDescrizione();
      }
    }

    return result;
  }

  public String getDescrEntita() {
    if (tabella != null) {
      return tabella.getDescrizione();
    }
    return "";
  }

  public String getChiavi() {
    StringBuffer buf = new StringBuffer("");
    if (tabella != null) {
      for (int i = 0; i < tabella.getCampiKey().size(); i++) {
        if (i > 0) buf.append(" - ");
        Campo key = (Campo) tabella.getCampiKey().get(i);
        buf.append(key.getCodiceMnemonico());
      }
    }
    return buf.toString();

  }

  public boolean isTabellato() {
    if (campo != null)
      return campo.getCodiceTabellato() != null
          && campo.getCodiceTabellato().length() > 0;
    return false;
  }

  public String getCodTab() {
    if (campo != null) return campo.getCodiceTabellato();
    return "";
  }

  public String getElencoValori() {
    StringBuffer buf = new StringBuffer("");
    if (this.isTabellato()) {
      // Si tratta di un tabellato quindi devo eseguire la select sul
      // database
      TabellatiManager mm = (TabellatiManager) UtilitySpring.getBean(
          "tabellatiManager", page.getServletContext(), TabellatiManager.class);
      List tab = mm.getTabellato(campo.getCodiceTabellato());
      // Scorro tutti i valori tabellati
      for (int i = 0; i < tab.size(); i++) {
        Tabellato val = (Tabellato) tab.get(i);
        buf.append(val.getTipoTabellato());
        buf.append(" - ");
        buf.append(val.getDescTabellato());
        buf.append("<br/>");
      }
    } else if (this.isFlag()) {
      buf.append("0 - Non valorizzato<br/>");
      buf.append("1 - Si<br/>");
      buf.append("2 - No<br/>");
    }
    return buf.toString();
  }

  /**
   * @return true se il campo è un flag, false altrimenti
   */
  public boolean isFlag() {
    return MetadatiManager.DOMINIO_FLAG.equals(this.campo.getDominio());
  }
}
