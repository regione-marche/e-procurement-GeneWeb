package it.eldasoft.gene.tags.decorators;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.tags.js.Javascript;

public class VoceMenuTag extends TagSupportGene {
	// ************************************************************
	// Storia Modifiche:
	// Data Utente Descrizione
	// 25/10/2006 M.F. Aggiunta delle gestione con i javascript generali
	// ************************************************************

	static Logger logger = Logger.getLogger(VoceMenuTag.class);

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -7859643225849003700L;

	private String title;

	private String etichetta;

	private int index;

	private boolean abilitato;

	public VoceMenuTag() {
		super("voceMenu");
		this.index = 0;
		this.title = null;
		this.abilitato = true;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int doEndTag() throws JspException {
		return TagSupport.EVAL_PAGE;
	}

	public int doStartTag() throws JspException {
		logger.debug("doStartTag: inizio Metodo");
		super.doStartTag();
		try {
			JspWriter out = this.pageContext.getOut();
			out.println("<td title=\"" + this.getTitle() + "\">");
			if (this.abilitato)
				out.println("<a id=\"" + this.getId()
						+ "\" href=\"javascript:showSubmenuNavbar('"
						+ this.getId() + "'," + getVarJava()
						+ ");\" tabindex=\"" + this.getIndex() + "\">");
			out.println(this.getEtichetta());
			if (this.abilitato)
				out.println("</a>");
			out.println("</td>");
			Javascript script = this.getJavascript();
			if (script != null) {
				script.println("var " + getVarJava() + "=\"\";");
			}
		} catch (Exception e) {
			throw new JspException("Errore in Voce Menu: " + e.getMessage(), e);
		}
		return EVAL_BODY_INCLUDE;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int tabindex) {
		this.index = tabindex;
	}

	public boolean isAbilitato() {
		return abilitato;
	}

	public void setAbilitato(boolean abilitato) {
		this.abilitato = abilitato;
	}

	/**
	 * Funzione che restituisce il nome delle variabile java utilizzata
	 * 
	 * @return
	 */
	public String getVarJava() {
		return "jvar" + this.getId();
	}

	public String getEtichetta() {
		return etichetta;
	}

	public void setEtichetta(String etichetta) {
		this.etichetta = etichetta;
	}
}