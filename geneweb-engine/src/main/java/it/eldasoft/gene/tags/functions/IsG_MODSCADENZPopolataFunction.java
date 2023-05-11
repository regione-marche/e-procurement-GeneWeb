/*
 * Creato 08/05/13
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringEscapeUtils;
/**
 * Funzione che determina se l'entita G_MODSCADENZ è popolata, e nel caso sia popolata
 * ritorna la lista di valori
 *
 * @author Marcello Caminiti
 */
public class IsG_MODSCADENZPopolataFunction extends AbstractFunzioneTag {

  public IsG_MODSCADENZPopolataFunction(){
    super(4, new Class[]{PageContext.class, String.class, String.class,String.class  });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String prg = (String) params[1];
    String ent = (String) params[2];
    String discr = (String) params[3];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ret = "false";

    try {
      String select="select count(cod) from g_modscadenz where PRG=? and ENT=? and DISCR=?";
      Long numOccorrenze = (Long)sqlManager.getObject(select, new Object[]{prg, ent, discr});
      if(numOccorrenze!=null && numOccorrenze.longValue()>0){
        ret = "true";
        select="select cod,tit,descr from g_modscadenz where PRG=? and ENT=? and DISCR=? order by cod";
        @SuppressWarnings("unchecked")
        List<Vector<JdbcParametro>> listaModelli = sqlManager.getListVector(select, new Object[]{prg, ent, discr});
        if(listaModelli!=null && listaModelli.size()>0){
          List<Vector<String>> lista = new ArrayList<Vector<String>>();
          for (Vector<JdbcParametro> modello : listaModelli) {
            Vector<String> riga = new Vector<String>();
            riga.add(modello.get(0).stringValue());
            riga.add(modello.get(1).stringValue());
            riga.add(modello.get(2).stringValue());
            // vado ad effettuare un escape per javascript per evitare caratteri speciali o acapo nella stringa
            riga.add(StringEscapeUtils.escapeJavaScript(modello.get(1).stringValue()));
            riga.add(StringEscapeUtils.escapeJavaScript(modello.get(2).stringValue()));
            lista.add(riga);
          }
          pageContext.setAttribute("listaModelliAttivita", lista,
              PageContext.REQUEST_SCOPE);
        }
      }
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dei dati della tabella g_modscadenz",e);
    } catch (GestoreException e) {
      throw new JspException("Errore durante la lettura dei dati della tabella g_modscadenz",e);
    }

    return ret;
  }

}