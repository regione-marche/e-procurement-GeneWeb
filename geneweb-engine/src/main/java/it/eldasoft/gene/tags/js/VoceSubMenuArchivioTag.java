package it.eldasoft.gene.tags.js;

import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;

import javax.servlet.jsp.JspException;

public class VoceSubMenuArchivioTag extends VoceSubMenuTag {

  /**
   * 
   */
  private static final long serialVersionUID = -443332288552488036L;

  private String            tabella;

  private String            partenza;

  public VoceSubMenuArchivioTag() {
    tabella = null;
    partenza = "trova.jsp";
    this.setId("VoceSubMenuArchivio");
  }

  public String getTabella() {
    return tabella;
  }

  public void setTabella(String archivio) {
    if (archivio != null) archivio = archivio.toUpperCase();
    this.tabella = archivio;
  }

  /*****************************************************************************
   * Voce che legge dai metadati le cose da scrivere sull'archivio
   */
  public int doStartTag() throws JspException {
    try {
      Tabella tab = DizionarioTabelle.getInstance().getDaNomeTabella(
          this.getTabella());
      if (tab == null)
        throw new JspException("La tabella "
            + this.getTabella()
            + " non esiste nello schema concettuale !");
      this.setTitle(tab.getDescrizione());
      this.setEtichetta(tab.getDescrizione());
      // Se non è settato il path da aprire setto quello di default
      if (this.getHref() == null) {
        String nomeTab = tab.getNomeFisico().toLowerCase();
        if (nomeTab.indexOf('.') >= 0)
          nomeTab = nomeTab.substring(0, nomeTab.indexOf('.'));
        this.setHref("$CONTEXT$/ApriPagina.do?href="
            + UtilityTags.getPathFromTab(tab)
            + this.getPartenza().toLowerCase());

      }
    } catch (Exception e) {
      throw new JspException(e.getMessage(), e);
    }
    if (this.isGestisciProtezioni()
        && (this.getIdProtezioni() == null || this.getIdProtezioni().length() == 0)) {
      this.setIdProtezioni("TABS.VIS." + this.getTabella().toUpperCase());
    }
    super.doStartTag();
    this.setHref(null);
    return EVAL_PAGE;
  }

  public String getPartenza() {
    return partenza;
  }

  public void setPartenza(String partenza) {
    if (partenza != null) partenza = partenza.toLowerCase();
    this.partenza = partenza;
  }

}
