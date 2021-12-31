package it.eldasoft.gene.tags.decorators.scheda;

/**
 * Classe che gestisce una pagina
 * @author marco.franceschin
 *
 */
public class PaginaTagImpl {
	/** Titolo delle pagina */
	private String title;
	/** ToolTip della pagina */
	private String tooltip;
	/** Corpo della pagina */
	private String body;
	/** Indice della pagina */
	private int indice;
	/** Flag per rendere la pagina selezionabile */
	private boolean selezionabile;
    /** Identificativo delle protezioni */
    private String  idProtezioni;

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return Returns the tooltip.
	 */
	public String getTooltip() {
		return tooltip;
	}
	/**
	 * @param tooltip The tooltip to set.
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	/**
	 * @return Returns the body.
	 */
	public String getBody() {
		return body;
	}
	/**
	 * @param body The body to set.
	 */
	public void setBody(String body) {
		this.body = body;
	}
	/**
	 * @return Returns the indice.
	 */
	public int getIndice() {
		return indice;
	}
	/**
	 * @param indice The indice to set.
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}
	/**
	 * @return Returns the selezionabile.
	 */
	public boolean isSelezionabile() {
		return selezionabile;
	}
	/**
	 * @param selezionabile The selezionabile to set.
	 */
	public void setSelezionabile(boolean selezionabile) {
		this.selezionabile = selezionabile;
	}
  /**
   * @return Ritorna idProtezioni.
   */
  public String getIdProtezioni() {
    return idProtezioni;
  }
  /**
   * @param idProtezioni idProtezioni da settare internamente alla classe.
   */
  public void setIdProtezioni(String idProtezioni) {
    this.idProtezioni = idProtezioni;
  }

}
