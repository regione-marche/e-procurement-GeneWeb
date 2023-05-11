/*
 * Created on 08/05/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.gestori.decoratori;

import it.eldasoft.gene.bl.scadenz.ScadenzariManager;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

/**
 * Gestore per il campo unimis.gcap come tabellato dalla UNIMIS
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoIdAttivita extends AbstractGestoreCampoTabellato {

  public GestoreCampoIdAttivita() {
    super(false, "N12");
  }

  @Override
  protected void initGestore() {
    super.initGestore();

    ScadenzariManager scadenzariManager = (ScadenzariManager) UtilitySpring.getBean("scadenzariManager",
        this.getPageContext(), ScadenzariManager.class);

    String moduloAttivo = (String) this.getPageContext().getAttribute("moduloAttivo",
        PageContext.SESSION_SCOPE);

    String entita = (String) this.getPageContext().getAttribute("entitaPartenza",
        PageContext.PAGE_SCOPE);

    if(entita!=null && !"".equals(entita)){
      String idAttivCorrente = (String) this.getPageContext().getAttribute("idAttivCorrente",
          PageContext.PAGE_SCOPE);
      Long idAttivita=null;
      if(idAttivCorrente!=null && !"".equals(idAttivCorrente))
        idAttivita = new Long(idAttivCorrente);

        String key1 = (String) this.getPageContext().getAttribute("KEY1",
            PageContext.PAGE_SCOPE);
        String key2 = (String) this.getPageContext().getAttribute("KEY2",
            PageContext.PAGE_SCOPE);
        String key3 = (String) this.getPageContext().getAttribute("KEY3",
            PageContext.PAGE_SCOPE);
        String key4 = (String) this.getPageContext().getAttribute("KEY4",
            PageContext.PAGE_SCOPE);
        String key5 = (String) this.getPageContext().getAttribute("KEY5",
            PageContext.PAGE_SCOPE);

        Vector<String> valoriChiave = new Vector<String>();
        if(key1!=null && !"".equals(key1))
          valoriChiave.add(0, key1);
        if(key2!=null && !"".equals(key2))
          valoriChiave.add(1, key2);
        if(key3!=null && !"".equals(key3))
          valoriChiave.add(2, key3);
        if(key4!=null && !"".equals(key4))
          valoriChiave.add(3, key4);
        if(key5!=null && !"".equals(key5))
          valoriChiave.add(4, key5);


        try {
          List listaAttivita = scadenzariManager.getAttivitaValideReferenziabili(moduloAttivo, entita, valoriChiave.toArray(), idAttivita);
          if(listaAttivita!=null && listaAttivita.size()>0){
            for (int i = 0; i < listaAttivita.size(); i++) {
              Tabellato row = (Tabellato) listaAttivita.get(i);
              String cod = row.getTipoTabellato();
              String descr = row.getDescTabellato();

              this.getCampo().addValore(cod, descr);
            }
          }
        } catch (SQLException e) {
          throw new RuntimeException("Errore durante la creazione della lista delle attività per il campo G_SCADENZ.IDATTIV",e);
        }
    }


  }

  /**
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato#getSql()
   */
  @Override
  public SqlSelect getSql() {

    return null;

  }

}