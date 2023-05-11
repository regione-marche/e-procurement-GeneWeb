package it.eldasoft.gene.tags.decorators.lista;

import it.eldasoft.gene.tags.decorators.campi.CampoDecorator;
import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Decoratore per la lista
 *
 * @author cit_franceschin
 *
 */
public class CampoListaTagImpl extends CampoDecorator {

  // ************************************************************
  // Storia Modifiche:
  // Data Utente Descrizione
  // 16/11/2006 M.F. Aggiungo la larghezza del campo
  // ************************************************************

  private int     nCampo;
  private int     width;
  private int     sort;
  private boolean addTabellati = false;

  /**
   * Flag che dice se il campo deve permettere l'ordinamento rispetto a se
   * stesso
   */
  private boolean ordinabile;

  /** Eventuale alias del campo nel caso di uso più volte della stessa from ma per individuare record diversi della stessa tabella */
  private String  alias;

  /**
   * Costruttore di default
   *
   */
  public CampoListaTagImpl() {
    this.nCampo = -1;
    this.width = -1;
    this.sort = 0;
    this.ordinabile = true; // di default il campo è ordinabile
    // Non scrivo gli input sulla lista
    this.setOutInput(false);
    this.setEdit(false);
  }

  /**
   * Funzione che verifica se si tratta di un campo
   *
   * @return
   */
  public boolean isCampo() {
    if (this.getCampo() == null || this.isCampoFittizio()) return false;
    return true;
  }

  @Override
  public String toString() {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 16/11/2006 M.F. Aggiungo l'eventuale dimensione della colonna
    // 24.11.2006 M.F. Aggiunta nei campi anche i campi con entità ! (campi
    // fittizzi)
    // ************************************************************

    StringBuffer buf = new StringBuffer();
    if (this.isVisibile()) {
      buf.append("<td ");
      if (this.width > 0) {
        buf.append(UtilityTags.getHtmlAttrib("width", new Integer(
            this.getWidth()).toString()));
      }
      buf.append(">");
      buf.append("<span id=\"col" + this.getNome() + "\">");
    }
    // Se si riferisce ad un link allora
    if (isCampo() || this.isCampoFittizio()) {
      // Se non è visibile ed è editabile setto come out dell'imput
      if (!this.isVisualizzazione() && this.isEdit()) this.setOutInput(true);
      buf.append(super.toString());
    }
    buf.append(this.getBody());
    if (this.isVisibile()){
      buf.append("</span>");
      buf.append("</td>\n");
    }
    return buf.toString();
  }

  /**
   * @return Returns the nCampo.
   */
  public int getNCampo() {
    return nCampo;
  }

  /**
   * @param campo
   *        The nCampo to set.
   */
  public void setNCampo(int campo) {
    nCampo = campo;
  }

  /**
   * @return Returns the width.
   */
  public int getWidth() {
    return width;
  }

  /**
   * @param width
   *        The width to set.
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * @return Returns the sort.
   */
  public int getSort() {
    return sort;
  }

  /**
   * @param sort
   *        The sort to set.
   */
  public void setSort(int sort) {
    this.sort = sort;
  }

  /**
   * @return Ritorna ordinabile.
   */
  public boolean isOrdinabile() {
    return ordinabile;
  }

  /**
   * @param ordinabile
   *        ordinabile da settare internamente alla classe.
   */
  public void setOrdinabile(boolean ordinabile) {
    this.ordinabile = ordinabile;
  }

  /**
   * @return Ritorna alias.
   */
  public String getAlias() {
    return alias;
  }

  /**
   * @param alias alias da settare internamente alla classe.
   */
  public void setAlias(String alias) {
    this.alias = alias;
  }

  public boolean isEdit() {
    return !this.isVisualizzazione();
  }

  public void setEdit(boolean edit) {
    this.setVisualizzazione(!edit);
  }

  /**
   * @return the addTabellati
   */
  public boolean isAddTabellati() {
    return addTabellati;
  }

  /**
   * @param addTabellati
   *        the addTabellati to set
   */
  public void setAddTabellati(boolean addTabellati) {
    this.addTabellati = addTabellati;
  }

}
