/*
 * Created on 24/ott/07
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
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.HelpMnemonico;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.spring.UtilitySpring;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GestioneHelpPaginaFunction extends AbstractFunzioneTag {

  // Path dell'help delle pagina
  private static final String PATH_HELP_PAGINE = "/help/it/pag/";

  public GestioneHelpPaginaFunction() {
    super(4, new Class[] { PageContext.class,String.class,String.class,String.class });
  }

  public static String getNota(SqlManager sql, int tipo, String idObj, String modalitaVisualizzazione) throws SQLException {
    if (idObj != null) {
      List ret = sql.getListVector(
          "select nota from W_NOTE where tipo = ? and oggetto = ? and modovis= ? order by prog",
          new Object[] { new Long(tipo), idObj, modalitaVisualizzazione });
      if (ret != null) {
        StringBuilder buf = new StringBuilder("");
        for (int i = 0; i < ret.size(); i++) {
          buf.append(SqlManager.getValueFromVectorParam(ret.get(i), 0).toString(
              false));
        }
        return buf.toString();
      }
    }
    return null;
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    String tipoString = (String) params[1];
    int tipo = 1;
    if(tipoString!=null && !"".equals(tipoString))
      tipo = Integer.parseInt(tipoString);
    String idObj = (String) params[2];
    String modoVisualizzazione = (String) params[3];

    HashMap help = new HashMap();
    try {
      switch (tipo) {
      case 1: // Si tratta di un mnemonico di campo
        HelpMnemonico helpMne = new HelpMnemonico(pageContext, idObj);
        help.put("descrizione", helpMne.getDescrizione());
        help.put("nomeFisico", helpMne.getNomeFisico());
        help.put("pathFileHelp", helpMne.getPathFileHelp());
        help.put("tipoColonna", helpMne.getTipoColonna());
        help.put("isFlag", new Boolean(helpMne.isFlag()));
        help.put("elencoValori", helpMne.getElencoValori());
        help.put("schema", helpMne.getSchema());
        help.put("descrSchema", helpMne.getDescrSchema());
        help.put("entita", helpMne.getEntita());
        help.put("descrEntita", helpMne.getDescrEntita());
        help.put("chiavi", helpMne.getChiavi());
        help.put("isTabellato", new Boolean(helpMne.isTabellato()));
        help.put("codTab", helpMne.getCodTab());
        break;

      case 2: // Si tratta di una maschera/pagina
        boolean isPagina = idObj.indexOf('.') > 0
            && idObj.indexOf('.', idObj.indexOf('.') + 1) > 0;
        String schema = idObj.substring(0, idObj.indexOf('.'));
        String descr = "";

        if (DizionarioSchemi.getInstance().get(schema) != null)
          schema += " - "
              + DizionarioSchemi.getInstance().get(schema).getDescrizione();
        help.put("schema", schema);

        Vector ret = sql.getVector(
            "select descr from W_OGGETTI where tipo = ? and oggetto = ?",
            new Object[] { isPagina ? "PAGE" : "MASC", idObj });
        if (ret != null) {
          descr = ret.get(0).toString();
        }

        String idMaschera = isPagina
            ? idObj.substring(0, idObj.lastIndexOf('.'))
            : "";
        String descMaschera = "";
        // Se si tratta di una pagina allora aggiungo la descrizione delle pagina
        // all'inizio
        if (isPagina) {
          ret = sql.getVector(
              "select descr from W_OGGETTI where tipo = ? and oggetto = ?",
              new Object[] { "MASC", idMaschera });
          if (ret != null) {
            descr = ret.get(0).toString() + " - " + descr;
            descMaschera = ret.get(0).toString();
          }
        }

        help.put("descrizione", descr);
        help.put("idMaschera", idMaschera);
        help.put("descMaschera", descMaschera);
        // Ora verifico se c'è la pagina aggiuntiva
        String path = PATH_HELP_PAGINE + idObj + ".jsp";
        URL percorso = null;
        percorso = pageContext.getServletContext().getResource(path);

        if (percorso != null)
          help.put("pathFile", path);
        else
          help.put("pathFile", "");
        break;
      }
      if("4".equals(modoVisualizzazione)){
        //Si vogliono leggere le note per tutte le modalità di visualizzazione
        help.put("nota1", this.getNota(sql, tipo, idObj, "1"));
        help.put("nota2", this.getNota(sql, tipo, idObj, "2"));
        help.put("nota3", this.getNota(sql, tipo, idObj, "3"));
      }else{
        String nota = this.getNota(sql, tipo, idObj, modoVisualizzazione);
        String nomeCampoNota = "nota";
        if(!"#".equals(modoVisualizzazione))
          nomeCampoNota+=modoVisualizzazione;
        help.put(nomeCampoNota, nota);
      }
      } catch (MalformedURLException e) {
      throw new JspException(e.getMessage(), e);
    } catch (SQLException e) {
      throw new JspException(e.getMessage(), e);
    }

    pageContext.setAttribute("help", help, PageContext.REQUEST_SCOPE);

    return null;
  }
}
