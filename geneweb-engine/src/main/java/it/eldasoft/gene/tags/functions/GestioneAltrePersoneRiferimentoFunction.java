/*
 * Created on 20/lug/09
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
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare le sezioni delle altre persone di riferimento per
 * gli uffici intestatari
 * 
 * @author Marcello Caminiti
 */
public class GestioneAltrePersoneRiferimentoFunction extends
    AbstractFunzioneTag {

  public GestioneAltrePersoneRiferimentoFunction() {
    super(1, new Class[] { String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    // Precarico le occorrenze della G2FUNZ solo se si accede alla scheda di
    // un ufficio intestatario in visualizzazione e in modifica
    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(
        pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {
      String chiave = (String) params[0];
      String codei = chiave.substring(chiave.indexOf(":") + 1);

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
      try {
        List<?> listaPersoneRiferimento = sqlManager.getListVector(
            "select CODEI, NUMFUN, CODFUN, INCFUN, DINFUN, DTEFUN "
                + "from G2FUNZ where CODEI = ? order by NUMFUN",
            new Object[] { codei });
        if (listaPersoneRiferimento != null
            && listaPersoneRiferimento.size() > 0) {
          List<String> listaNomTec = new ArrayList<String>(listaPersoneRiferimento.size());

          for (int i = 0; i < listaPersoneRiferimento.size(); i++) {
            String nomtec = (String) sqlManager.getObject(
                "select nomtec from tecni where CODTEC= ?",
                new Object[] { SqlManager.getValueFromVectorParam(
                    listaPersoneRiferimento.get(i), 2).getStringValue() });
            listaNomTec.add(nomtec);
          }
          pageContext.setAttribute("personeRiferimento",
              listaPersoneRiferimento, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("nomeTecnico", listaNomTec,
              PageContext.REQUEST_SCOPE);
        }
      } catch (SQLException e) {
        throw new JspException(
            "Errore nell'estrarre le persone di riferimento "
                + "dell'ufficio intestatario "
                + codei, e);
      }
    }
    return null;
  }

}