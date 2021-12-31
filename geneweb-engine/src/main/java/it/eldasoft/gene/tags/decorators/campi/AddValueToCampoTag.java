/*
 * Created on 15/nov/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.campi;

import javax.servlet.jsp.JspException;

import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.tags.decorators.lista.CampoListaTag;
import it.eldasoft.gene.tags.decorators.lista.CampoListaTagImpl;
import it.eldasoft.gene.tags.decorators.lista.FormListaTag;
import it.eldasoft.gene.tags.decorators.scheda.FormSchedaTag;
import it.eldasoft.gene.tags.decorators.trova.FormTrovaTag;

/**
 * Tag per aggiungere un valore enumerato su un campo generale
 * 
 * @author Marco.Franceschin
 * 
 */
public class AddValueToCampoTag extends TagSupportGene {

  /**
   * 
   */
  private static final long serialVersionUID = 5886182391404814799L;
  /** Valore da impostare nell'enumerato */
  private String            value            = null;
  /** Descrizione dell'enumerato */
  private String            descr            = null;
  /** Flag per dire che si tratta del primo valore (per utilizzi futuri ) */
  private boolean           first            = false;

  /**
   * @return the first
   */
  public boolean isFirst() {
    return first;
  }

  /**
   * @param first
   *        the first to set
   */
  public void setFirst(boolean first) {
    this.first = first;
  }

  public int doStartTag() throws JspException {
    boolean ok = false;
    AbstractCampoBodyTag parent = (AbstractCampoBodyTag) this.getParent(AbstractCampoBodyTag.class);
    if (parent == null)
      throw new JspException("addValue deve trovarsi all'interno di un campo !");
    if (this.getParent(FormTrovaTag.class) != null)
      ok = true;
    else if (this.getParent(FormSchedaTag.class) != null) {
      FormSchedaTag scheda = (FormSchedaTag) this.getParent(FormSchedaTag.class);
      ok = !scheda.isFirstIteration();

    } else if (this.getParent(FormListaTag.class) != null) {
      FormListaTag lista = (FormListaTag) this.getParent(FormListaTag.class);
      ok = !lista.isFirstIteration();
      // Se è il primo tabellato aggiunto eseguo il clear
      CampoListaTag campo = (CampoListaTag) this.getParent(CampoListaTag.class);
      if (!((CampoListaTagImpl) campo.getDecoratore()).isAddTabellati()) {
        // Se non si sono ancora aggiunti i tabellati allora sbianco prima di
        // inserire il primo
        // Questo per gestire l'interazione del campo su ogni riga
        campo.getDecoratore().getValori().clear();
        ((CampoListaTagImpl) campo.getDecoratore()).setAddTabellati(true);
      }
    }
    if (ok) {
      // Se il campo non è di tipo enumerato allora lo setto come tale
      if (parent.getDecoratore().getTipo() != null
          && parent.getDecoratore().getTipo().charAt(0) != JdbcParametro.TIPO_ENUMERATO)
        parent.getDecoratore().setTipo(
            JdbcParametro.TIPO_ENUMERATO + parent.getDecoratore().getTipo());
      // Se è il primo valore allora sbianca gli altri
      if (this.first) parent.getDecoratore().getValori().clear();

      // Aggiungo il valore solo se è alla prima interazione
      parent.getDecoratore().addValore(value, descr);

    }

    return super.doStartTag();
  }

  /**
   * @return the descr
   */
  public String getDescr() {
    return descr;
  }

  /**
   * @param descr
   *        the descr to set
   */
  public void setDescr(String descr) {
    this.descr = descr;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value
   *        the value to set
   */
  public void setValue(String value) {
    this.value = value;
  }

}
