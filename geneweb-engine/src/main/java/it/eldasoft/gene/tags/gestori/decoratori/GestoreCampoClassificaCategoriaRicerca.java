/*
 * Created on 01/Dic/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.gestori.decoratori;

import java.util.List;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore per i campi Classifica inferiore e superiore con tabellati diversi
 * a seconda del tipo di appalto della categoria (Lavori, forniture o servizi)
 *
 * @author Sara Santi
 */
public class GestoreCampoClassificaCategoriaRicerca extends AbstractGestoreCampoTabellato {

  public GestoreCampoClassificaCategoriaRicerca() {
    super(false, "N2");
  }

  /**
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato#getSql()
   */
  @Override
  public SqlSelect getSql() {

    String tipoDb = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
    String select = "";
    if (tipoDb != null && "MSQ".equals(tipoDb))
      select = "+";
    else
      select = "||";

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getPageContext(), TabellatiManager.class);

    //Recupera descrizione della tipologia di categoria d'iscrizione
    List listaValoriTabellato = tabellatiManager.getTabellato("G_038");
    String[] descTipologia={"","","","",""};
    if(listaValoriTabellato!=null && listaValoriTabellato.size()>0){
      for (int i=0; i<listaValoriTabellato.size(); i++){
        descTipologia[i]=((Tabellato) listaValoriTabellato.get(i)).getTipoTabellato().toString() + "."
            + ((Tabellato) listaValoriTabellato.get(i)).getDescTabellato() + " - ";
      }
    }

      return new SqlSelect("select tab1tip, case when tab1cod='A1015' then '" + descTipologia[0] +
          "' when tab1cod='G_035' then '" + descTipologia[2] +
          "' when tab1cod='G_036' then '" + descTipologia[3] +
          "' when tab1cod='G_037' then '" + descTipologia[1] +
          "' else '" + descTipologia[4] + "' end " + select + " tab1desc tipo_desc " +
    		"from tab1 where tab1cod in ('A1015', 'G_035', 'G_036', 'G_037', 'G_049') and (tab1arc is null or tab1arc<>1) order by tab1cod,tab1nord,tab1tip", null);
  }

}
