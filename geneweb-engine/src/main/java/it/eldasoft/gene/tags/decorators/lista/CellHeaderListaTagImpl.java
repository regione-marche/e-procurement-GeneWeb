package it.eldasoft.gene.tags.decorators.lista;

import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Implementazione della cella di header di lista
 *
 * @author marco.franceschin
 *
 */
public class CellHeaderListaTagImpl {

  // ************************************************************
  // Storia Modifiche:
  // Data Utente Descrizione
  // 20/11/2006 M.F. Aggiunta del campo per l'ordinamento
  // ************************************************************

  private String            headerClass;
  private String            title;
  private CampoListaTagImpl campo;

  private int               numeroColonna;

  private boolean           ordinabile;

  public CellHeaderListaTagImpl(String title, String aClass,
      CampoListaTagImpl campo, int numeroColonna, boolean ordinabile) {
    this.headerClass = aClass;
    this.title = title;
    this.campo = campo;
    this.numeroColonna = numeroColonna;
    this.ordinabile = ordinabile;
  }

  /**
   * Funzione che converte un header di una lista in HTML
   *
   * @param navigazioneDisabilitata
   * @return
   */
  public String toString(boolean navigazioneDisabilitata) {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 09/02/2007 M.F. Aggiunta delle gestione con la navigazione disabilitata
    // ************************************************************

    StringBuffer buf = new StringBuffer();
    // Se si tratta di un campo aggiungo l'ordinamento
    if (this.campo.isCampo()) if (campo.getSort() != 0) {
      if (campo.getSort() > 0) {
        this.headerClass = "sortable sorted order1";
      } else
        this.headerClass = "sortable sorted order2";

    } else {
      this.headerClass = "sortable";
    }

    buf.append("<th ").append("id=\"tit" + campo.getNome() + "\" ");
    // Se si ha settato la classe la setto
    if (this.headerClass != null)
      buf.append(UtilityTags.getHtmlAttrib("class", this.headerClass));
    buf.append(">");
    if (this.ordinabile && this.campo.isCampo() && !navigazioneDisabilitata) {
      buf.append("<a ");

      switch (campo.getSort()) {
      case 0:

      case -1:
        buf.append(UtilityTags.getHtmlAttrib("title", UtilityTags.getResource(
            "label.tags.template.lista.sort.ordinaAsc", null, false)));
        buf.append(UtilityTags.getHtmlAttrib("href",
            "javascript:listaOrdinaPer('" + this.getNumeroColonna() + "');"));

        break;
      default:
        buf.append(UtilityTags.getHtmlAttrib("title", UtilityTags.getResource(
            "label.tags.template.lista.sort.ordinaDesc", null, false)));
        buf.append(UtilityTags.getHtmlAttrib("href",
            "javascript:listaOrdinaPer('!" + this.getNumeroColonna() + "');"));
      }
      buf.append(">");
    }
    buf.append(this.title);
    if (this.ordinabile && this.campo.isCampo() && !navigazioneDisabilitata)
      buf.append("</a>");
    buf.append("</th>");

    return buf.toString();
  }

  /**
   * @return Returns the numeroColonna.
   */
  public int getNumeroColonna() {
    return numeroColonna;
  }
}
