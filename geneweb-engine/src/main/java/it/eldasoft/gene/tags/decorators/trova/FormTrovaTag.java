package it.eldasoft.gene.tags.decorators.trova;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.tags.history.UtilityHistory;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.TrovaAction;

public class FormTrovaTag extends TagSupportGene {

	/**
	 *
	 */
	private static final long serialVersionUID = 5364509912623905789L;
	/**
	 * Costanti della trova
	 */
	public static final String CAMPO_COUNT = "campiCount";
	public static final String CAMPO_ENTITA = "entita";
	public static final String CAMPO_FILTRO = "filtro";
    public static final String CAMPO_ORIGINAL_FILTER = "originalFilter";
	public static final String CAMPO_CASESENSITIVE = "caseSensitive";
	public static final String CAMPO_RISULTATI_PER_PAGINA = "risultatiPerPagina";
	public static final String JSP_SCHEDA = "schedaPerInserimento";
	public static final String GESTORE_TROVA = "gestoreTrova";
	public static final String CAMPO_VISUALIZZAZIONE_AVANZATA = "visualizzazioneAvanzata";

	private String entita;

	private String filtro;

	private int campo;

	/** Lista che deve essere aperta */
	private String lista;

	/** Scheda d'apertura per l'inserimento */
	private String scheda;

	/** HashMap con i campi salvati precedentemente */
	private HashMap<String, Object> campiSaved = null;

	/** Flag per definire la gestione delle protezioni */
	private boolean gestisciProtezioni;

	/** GEstore del trova */
	private String gestore = null;

	public FormTrovaTag() {
		super("trova");
		this.entita = null;
		this.filtro = "";
		this.campo = 0;
		this.lista = null;
		this.scheda = null;
		this.gestisciProtezioni = false;
	}

	/**
	 * Funzione che restituisce il numero dei campo nella maschera di input
	 *
	 * @return Numero dei campi creati
	 */
	public int getNumCampi() {
		return this.campo;
	}

	/**
	 * Ritorna il prossimo numero di campo
	 *
	 * @return Returns the campo.
	 */
	public int getCampo() {
		int nCampo = this.campo;
		campo++;
		return nCampo;
	}

	/** Inizio del tag */
	@Override
	public int doStartTag() throws JspException {
		super.doStartTag();
		this.pageContext.setAttribute(UtilityTags.REQUEST_VAR_TIPO_PAGINA, UtilityTags.PAGINA_TROVA,
				PageContext.REQUEST_SCOPE);
		this.campo = 0;
		// Estraggo i valori dei campi salvati precedentemente
		if (this.pageContext.getSession()
				.getAttribute(TrovaAction.SESSION_PENDICE_TROVA + this.getEntita()) instanceof HashMap) {
			this.campiSaved = (HashMap<String, Object>) this.pageContext.getSession()
					.getAttribute(TrovaAction.SESSION_PENDICE_TROVA + this.getEntita());
		} else {
			this.campiSaved = null;
		}
		// Se non settata la lista allora setto la lista di default
		if (this.lista == null) {
			this.setLista(UtilityTags.getPathFromEntita(this.getEntita()) + "lista.jsp");
		}
		JspWriter out = this.pageContext.getOut();
		try {
			out.println("<form name=\"trova\" action=\"" + this.getContextPath()
					+ "/Trova.do\" method=\"post\" id=\"trova\">");
			// Setto la pagina della lista che deve essere richiamata
			pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_TO_JSP, this.getLista(),
					PageContext.REQUEST_SCOPE);
			out.print(UtilityTags.getHtmlDefaultHidden(this.pageContext));
			out.print(UtilityTags.getHtmlHideInput("metodo", "trova"));
			out.print(UtilityTags.getHtmlHideInput(CAMPO_ENTITA, this.getEntita()));

			// SQL Injection prevention: si spostano in sessione i campi "filtro" e tutte le
			// where dai campi contenuti, in precedenza definiti
			// come input hidden della form di ricerca
			int popupLevel = UtilityTags.getNumeroPopUp(this.pageContext);
			UtilityTags.createHashAttributeForSqlBuild(this.pageContext.getSession(), this.getEntita(), popupLevel);
			UtilityTags.putAttributeForSqlBuild(this.pageContext.getSession(), this.getEntita(), popupLevel,
					CAMPO_ORIGINAL_FILTER, this.getFiltro());

			out.print(UtilityTags.getHtmlHideInput(JSP_SCHEDA, this.getScheda()));
			out.print(UtilityTags.getHtmlHideInput(GESTORE_TROVA, this.getGestore()));
			out.println("<table class=\"ricerca\">");
			this.getJavascript().println(this.getFormName() + "=new FormObj(document.trova);");
		} catch (IOException e) {
			throw new JspException(e.getMessage(), e);
		}
		// M.F. 22.07.2007 Aggiunta del javascript per eseguire il qubmit della form
		this.getJavascript().println("function submitForm(){");
		this.getJavascript().println("  document.forms[0].submit();");
		this.getJavascript().println("}");

		if (StringUtils.isNotBlank(getEntita())) {
			UtilityHistory history = UtilityTags.getUtilityHistory(pageContext.getSession());
			history.addQueriedEntity(getEntita());
		}
		return EVAL_PAGE;
	}

	public String getFormName() {
		return "local" + this.getId();
	}

	@Override
	public int doEndTag() throws JspException {
		// ************************************************************
		// Storia Modifiche:
		// Data Utente Descrizione
		// 29/09/2006 M.F. Aggiunta dell'hidden con il numero dei campi
		// 21/11/2006 M.F. Aggiunta del numero di righe
		// 01/12/2006 M.F. Eliminato il pulsante annulla
		// ************************************************************

		JspWriter out = this.pageContext.getOut();
		try {
			// Inserisco i pulsanti di default
			out.println("<tr>");
			out.println("<td class=\"etichetta-dato\">Opzioni</td>");
			out.println("<td colspan=\"2\" class=\"flag-sensitive\">");
			out.print("<div class=\"opzioniTrova\"><select ");
			out.print(UtilityTags.getHtmlAttrib("id", CAMPO_RISULTATI_PER_PAGINA));
			out.print(UtilityTags.getHtmlAttrib("name", CAMPO_RISULTATI_PER_PAGINA));
			out.println("  >\n");
			String valPrec = this.getValueCampo(CAMPO_RISULTATI_PER_PAGINA);
			// Se vuoto il valore di default è 20
			if (valPrec == null || valPrec.length() == 0)
				valPrec = "20";
			String risultati[] = UtilityTags.stringToArray(
					UtilityTags.getResource("label.tags.template.trova.numeroRisultati.valori", null, false), ';');

			for (int i = 0; i < risultati.length - 1; i += 2) {
				out.println("<option value=\"" + risultati[i] + "\" "
						+ (valPrec.equals(risultati[i]) ? "selected " : "") + ">" + risultati[i + 1] + "</option>\n");
			}
			out.println("</select>&nbsp;");
			out.print(UtilityTags.getResource("label.tags.template.trova.numeroRisultati", null, true));
			out.println("</div>");
			if (((Boolean) this.pageContext.getServletContext()
					.getAttribute(CostantiGenerali.ATTR_ATTIVA_CASE_SENSITIVE)).booleanValue()) {
				out.print("<div class=\"opzioniTrova\"><input ");
				out.print(UtilityTags.getHtmlAttrib("type", "checkbox"));
				out.print(UtilityTags.getHtmlAttrib("name", CAMPO_CASESENSITIVE));
				if (!this.getValueCampo(CAMPO_CASESENSITIVE).equals("1"))
					out.print(" checked=\"checked\" ");
				out.print("/>&nbsp;");
				out.println(UtilityTags.getResource("label.tags.template.trova.trovaCaseSensitive", null, true));
				out.println("</div>");
			} else {
				out.print("<input ");
				out.print(UtilityTags.getHtmlAttrib("type", "hidden"));
				out.print(UtilityTags.getHtmlAttrib("name", CAMPO_CASESENSITIVE));
				if (!this.getValueCampo(CAMPO_CASESENSITIVE).equals("1")) {
					out.print(" value=\"1\" ");
				} else {
					out.print(" value=\"0\" ");
				}
				out.println("/>&nbsp;");
			}
			// Campo per la visualizzazione avanzata degli operatori
			out.print("<div class=\"opzioniTrova\"><input ");
			out.print(UtilityTags.getHtmlAttrib("type", "checkbox"));
			out.print(UtilityTags.getHtmlAttrib("name", CAMPO_VISUALIZZAZIONE_AVANZATA));
			out.print(UtilityTags.getHtmlAttrib("id", CAMPO_VISUALIZZAZIONE_AVANZATA));
			String valVisualizzazioneAvanzata = this.getValueCampo(CAMPO_VISUALIZZAZIONE_AVANZATA);
			if (valVisualizzazioneAvanzata == null || valVisualizzazioneAvanzata.length() == 0)
				valVisualizzazioneAvanzata = "0";
			if (!"0".equals(valVisualizzazioneAvanzata)) {
				out.print(" checked=\"checked\" ");
			}
			out.print(UtilityTags.getHtmlAttrib("onclick", "trovaVisualizzazioneAvanzata()"));
			out.print("/>&nbsp;");
			out.println(UtilityTags.getResource("label.tags.template.trova.trovaVisualizzazioneAvanzata", null, true));
			out.println("</div>");
			out.println("</td></tr>");

			// Inserisco i pulsanti di default
			out.println("		<tr>");
			out.println("			<td class=\"comandi-dettaglio\" colSpan=\"3\">");
			// Aggiungo i campi count
			out.print(UtilityTags.getHtmlHideInput(CAMPO_COUNT, String.valueOf(this.getNumCampi())));
			out.println("				<SPAN id=\"contenitore-comandi-dettaglio\">");
			out.println(
					"					<INPUT type=\"button\" class=\"bottone-azione\" value=\"Trova\" title=\"Trova\" onclick=\"javascript:trovaEsegui();\">");
			out.println(
					"					<INPUT type=\"button\" class=\"bottone-azione\" value=\"Reimposta\" title=\"Reimposta\" onclick=\"javascript:trovaNuova();\">");
			out.println("				</SPAN>");
			// out.println(" <INPUT type=\"button\" class=\"bottone-azione\"
			// value=\"Annulla\" title=\"Annulla\"
			// onclick=\"javascript:return trovaClear()\">");
			out.println("				&nbsp;");
			out.println("				</td>");
			out.println("			</tr>");
			// Chiudo la table
			out.println("	</table>");
			// Chiudo il form
			out.println("</form>");

			out.println("<script type=\"text/javascript\">");
			if ("0".equals(valVisualizzazioneAvanzata)) {
				out.println("trovaVisualizzazioneOperatori('nascondi');");
			} else {
				out.println("trovaVisualizzazioneOperatori('visualizza');");
			}

			out.println("</script>");

		} catch (IOException e) {
			throw new JspException(e.getMessage(), e);
		}

		return super.doEndTag();
	}

	/**
	 * @return Returns the entita.
	 */
	public String getEntita() {
		return entita;
	}

	/**
	 * @param entita The entita to set.
	 */
	public void setEntita(String entita) {
		// Eseguo sempre l'uppercase dellentita
		if (entita != null)
			entita = entita.toUpperCase();
		this.entita = entita;
	}

	/**
	 * @return Returns the filtro.
	 */
	public String getFiltro() {
		return filtro;
	}

	/**
	 * @param filtro The filtro to set.
	 */
	public void setFiltro(String filtro) {
		this.filtro = filtro;
	}

	/**
	 * @return Returns the lista.
	 */
	public String getLista() {
		return lista;
	}

	/**
	 * @param lista The lista to set.
	 */
	public void setLista(String lista) {
		this.lista = lista;
	}

	/**
	 * Estrazione del valore del campo
	 *
	 * @param nomeCampo Nome del campo
	 * @return
	 */
	public String getValueCampo(String nomeCampo) {
		if (campiSaved != null) {
			if (campiSaved.get(nomeCampo) != null)
				return campiSaved.get(nomeCampo).toString();
		}
		return "";
	}

	public boolean isGestisciProtezioni() {
		return gestisciProtezioni;
	}

	public void setGestisciProtezioni(boolean gestisciProtezioni) {
		this.gestisciProtezioni = gestisciProtezioni;
	}

	/**
	 * @return the scheda
	 */
	public String getScheda() {
		return scheda;
	}

	/**
	 * @param scheda the scheda to set
	 */
	public void setScheda(String scheda) {
		this.scheda = scheda;
	}

	/**
	 * @return the gestore
	 */
	public String getGestore() {
		return gestore;
	}

	/**
	 * @param gestore the gestore to set
	 */
	public void setGestore(String gestore) {
		this.gestore = gestore;
	}

}
