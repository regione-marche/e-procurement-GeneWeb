/*
 * Created on 22-apr-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.wizard;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.TagAttributes;
import it.eldasoft.gene.tags.decorators.archivi.ArchivioRequest;
import it.eldasoft.gene.tags.decorators.archivi.ArchivioTagImpl;
import it.eldasoft.gene.tags.decorators.scheda.CampoSchedaTag;
import it.eldasoft.gene.tags.decorators.scheda.CampoSchedaTagImpl;
import it.eldasoft.gene.tags.decorators.scheda.IFormScheda;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.log4j.Logger;

/**
 * Tag per la gestione del form a scheda sulla singola pagina del wizard
 *
 * @author stefano.sabbadin
 */
public class FormSchedaWizardTag extends BodyTagSupportGene implements
    IFormScheda {

  /**
   * UID
   */
  private static final long serialVersionUID   = 2148160947017700105L;

  private static Logger     logger             = Logger.getLogger(FormSchedaWizardTag.class);

  private StringBuffer      body               = null;
  private String            firstIterationPage = null;

  public FormSchedaWizardTag() {
    super("formSchedaWizard");
  }

  /**
   * @see it.eldasoft.gene.tags.BodyTagSupportGene#newTagAttributes()
   */
  public TagAttributes newTagAttributes() {
    return new FormSchedaWizardAttributes(this.getTipoVar());
  }

  private FormSchedaWizardAttributes getAttributes() {
    Object obj = this.getAttributeManager();
    return (FormSchedaWizardAttributes) obj;
  }

  /**
   * @see it.eldasoft.gene.tags.decorators.scheda.IFormScheda#isGestisciProtezioni()
   */
  public boolean isGestisciProtezioni() {
    return this.getAttributes().isGestisciProtezioni();
  }

  public void setGestisciProtezioni(boolean gestisciProtezioni) {
    this.getAttributes().setGestisciProtezioni(gestisciProtezioni);
  }

  public String getEntita() {
    return getAttributes().getEntita();
  }

  public void setEntita(String entita) {
    this.getAttributes().setEntita(entita);
  }

  public String getGestoreNavigazione() {
    return getAttributes().getGestore();
  }

  public void setGestoreNavigazione(String gestore) {
    this.getAttributes().setGestore(gestore);
  }

  /**
   * @return Ritorna plugin.
   */
  public String getPlugin() {
    return this.getAttributes().getPlugin();
  }

  /**
   * @param plugin
   *        plugin da settare internamente alla classe.
   */
  public void setPlugin(String plugin) {
    this.getAttributes().setPlugin(plugin);
  }

  public String getTableClass() {
    return this.getAttributes().getTableClass();
  }

  public void setTableClass(String tableClass) {
    this.getAttributes().setTableClass(tableClass);
  }

  public String getTipoPagina() {
    return this.getAttributes().getTipoPagina();
  }

  public void setTipoPagina(String tipoPagina) {
    this.getAttributes().setTipoPagina(tipoPagina);
  }

  private void setNuovoForm() {
    getAttributes().setCampoAttivo(0);
    getAttributes().setTable(null);
  }

  /** Aggiungo il campo all'elenco dei campi della form */
  public void addCampo(CampoSchedaTagImpl campo) {
    getAttributes().getElencoCampi().add(campo);
  }

  public int doStartTag() throws JspException {
    int ret = super.doStartTag();
    String modo = UtilityTags.getParametro(this.pageContext,
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO);
    if (modo == null) modo = UtilityTags.SCHEDA_MODO_INSERIMENTO;
    // imposto una variabile JS che contenga la modalità di apertura scheda
    this.getJavascript().println("var modoAperturaScheda = '" + modo + "';");
    // Setto il modo di apertura nel request
    this.pageContext.setAttribute(UtilityTags.REQUEST_VAR_MODO_APERTURA_SCHEDA,
        modo, PageContext.REQUEST_SCOPE);
    this.pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO,
        modo, PageContext.REQUEST_SCOPE);
    this.pageContext.setAttribute(UtilityTags.REQUEST_VAR_TIPO_PAGINA,
        UtilityTags.PAGINA_SCHEDA, PageContext.REQUEST_SCOPE);

    this.setNuovoForm();

    this.getJavascript().println(
        this.getFormName() + "=new FormObj(document." + this.getId() + ");");
    // Estraggo la tabella e la setto negli attributi
    if (this.getEntita() != null && this.getEntita().length() > 0) {
      this.getAttributes().setTable(
          DizionarioTabelle.getInstance().getDaNomeTabella(this.getEntita()));
    }

    this.body = new StringBuffer();
    if (this.getAttributes().getTableClass() == null)
      this.getAttributes().setTableClass("dettaglio-notab");

    if (this.getTipoPagina() == null)
      this.getAttributes().setTipoPagina(CostantiWizard.TIPO_PAGINA_DETTAGLIO);
    this.pageContext.setAttribute(CostantiWizard.HIDDEN_PARAMETRO_TIPO_PAGINA,
        this.getTipoPagina(), PageContext.REQUEST_SCOPE);

    // si esegue la chiamata all'eventuale plugin per il popolamento di dati
    // nel request da usare nella pagina
    AbstractGestorePreload plugin = this.getPluginInstance();
    if (plugin != null) plugin.doBeforeBodyProcessing(this.pageContext, modo);

    return ret;
  }

  public int doAfterBody() throws JspException {
    if (this.isFirstIteration()) {
      this.doAfterBodyFirstIteration();
      super.doAfterBody();
      return EVAL_BODY_AGAIN;
    } else {
      this.doAfterBodySecondIteration();
      super.doAfterBody();
      this.setNuovoForm();
      return SKIP_BODY;
    }
  }

  /**
   * Corpo del doAfterBody dopo la prima valutazione del body.<br>
   * Si settano i dati delle colonne, o come il valore di default, o come il
   * valore estratto dall'oggetto in sessione se già inseriti in precedenza
   *
   * @throws JspException
   */
  private void doAfterBodyFirstIteration() throws JspException {
    if (this.getBodyContent() != null)
      this.firstIterationPage = this.getBodyContent().getString();

    // il wizard prevede sempre e solamente una situazione di inserimento
    for (int i = 0; i < this.getAttributes().getElencoCampi().size(); i++) {
      CampoSchedaTagImpl campo = (CampoSchedaTagImpl) this.getAttributes().getElencoCampi().get(
          i);
      // si attribuisce il valore di default se il campo non è in sessione,
      // altrimenti si legge il valore presente in sessione
      DataColumnContainer datiSessione = (DataColumnContainer) this.pageContext.getAttribute(
          CostantiWizard.NOME_OGGETTO_WIZARD_SESSIONE,
          PageContext.SESSION_SCOPE);
      if (datiSessione != null && datiSessione.isColumn(campo.getNomeFisico()))
        try {
          campo.setValue(datiSessione.getColumn(campo.getNomeFisico()).getValue().getStringValue());
        } catch (GestoreException e) {
          throw new JspException(e.getMessage(), e);
        }
      else
        campo.setValue(campo.getDefaultValue());
    }

    this.addDatiRiga();

    AbstractGestorePreload plugin = this.getPluginInstance();
    if (plugin != null)
      plugin.doAfterFetch(this.pageContext, UtilityTags.getParametro(
          this.pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO));

    this.setNCampo(0);
  }

  /**
   * Corpo del doAfterBody dopo la seconda valutazione del body.<br>
   * Viene generato il form HTML con tutti i campi all'interno
   *
   * @param modifica
   *        true se i dati sono in modifica
   * @throws JspException
   */
  private void doAfterBodySecondIteration() throws JspException {
    boolean modifica = UtilityTags.isInModifica(this.pageContext.getRequest());

    StringBuffer buf = new StringBuffer("");
    StringBuffer elencoCampi = new StringBuffer("");
    buf.append("<form ");
    buf.append(UtilityTags.getHtmlAttrib("name", this.getId()));
    buf.append(UtilityTags.getHtmlAttrib("action", this.getContextPath()
        + "/Wizard.do"));
    buf.append(UtilityTags.getHtmlAttrib("method", "post"));
    // {MF23102006} Aggiunta dell'onSubmit della form
    buf.append(UtilityTags.getHtmlAttrib("onSubmit", "javascript:return local"
        + this.getId()
        + ".onsubmit();"));
    buf.append(">\n");
    // Aggiungo gli hidden di default
    buf.append(UtilityTags.getHtmlDefaultHidden(this.pageContext));
    // Aggiungo la chiave pel parent alla scheda
    buf.append(UtilityTags.getHtmlHideInput(
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
        UtilityTags.getParametro(this.pageContext,
            UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT)));
    buf.append(UtilityTags.getHtmlHideInput("entita", this.getEntita()));
    buf.append(UtilityTags.getHtmlHideInput("metodo", "apri"));
    buf.append(UtilityTags.getHtmlHideInput(
        UtilityTags.DEFAULT_HIDDEN_NOME_GESTORE, this.getGestoreNavigazione()));
    // Aggiunta del parametro del modo
    buf.append(UtilityTags.getHtmlHideInputFromParam(this.pageContext,
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO));
    buf.append(UtilityTags.getHtmlHideInputFromParam(this.pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA));

    String tipoPaginaWizard = UtilityTags.getParametro(this.pageContext,
        CostantiWizard.HIDDEN_PARAMETRO_TIPO_PAGINA);
    // sicuramente è valorizzato perchè setto il default nel doStartTag se non è
    // valorizzato il parametro del tag
    buf.append(UtilityTags.getHtmlHideInput(
        CostantiWizard.HIDDEN_PARAMETRO_TIPO_PAGINA, tipoPaginaWizard));

    // sicuramente presente perchè è settato da WizardAction come risultato
    // dell'operazione richiesta
    buf.append(UtilityTags.getHtmlHideInput(
        CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA,
        this.pageContext.getAttribute(
            CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA,
            PageContext.REQUEST_SCOPE)));
    PaginaWizardTag parent = (PaginaWizardTag) getParent(PaginaWizardTag.class);
    parent.setSottoPagina(((Integer) this.pageContext.getAttribute(
        CostantiWizard.HIDDEN_PARAMETRO_SOTTO_PAGINA, PageContext.REQUEST_SCOPE)).intValue());

    buf.append(UtilityTags.getHtmlHideInput(
        CostantiWizard.HIDDEN_PARAMETRO_ENTITA_PRINCIPALE_WIZARD,
        this.pageContext.getAttribute(
            CostantiWizard.HIDDEN_PARAMETRO_ENTITA_PRINCIPALE_WIZARD,
            PageContext.REQUEST_SCOPE)));
    buf.append(UtilityTags.getHtmlHideInput(
        CostantiWizard.HIDDEN_PARAMETRO_GESTORE_SALVATAGGIO_WIZARD,
        this.pageContext.getAttribute(
            CostantiWizard.HIDDEN_PARAMETRO_GESTORE_SALVATAGGIO_WIZARD,
            PageContext.REQUEST_SCOPE)));
    buf.append(UtilityTags.getHtmlHideInput(
        CostantiWizard.HIDDEN_PARAMETRO_PAGINA_FINE_WIZARD,
        this.pageContext.getAttribute(
            CostantiWizard.HIDDEN_PARAMETRO_PAGINA_FINE_WIZARD,
            PageContext.REQUEST_SCOPE)));
    // il numero di pagine lo setto a 0, poi lo si cambia via JS con il codice
    // inserito nel doEndTag di WizardTag. questo perchè devo prima processare
    // tutti i tag paginaWizard, e solo alla fine so quante pagine totali ci
    // sono
    buf.append(UtilityTags.getHtmlHideInput(
        CostantiWizard.HIDDEN_PARAMETRO_NUMERO_PAGINE, new Integer(0)));

    if (this.firstIterationPage != null) buf.append(this.firstIterationPage);

    buf.append("<table class=\"");
    buf.append(this.getTableClass());
    buf.append("\">\n");
    getAttributes().setArchivio(false);
    for (int i = 0; i < this.getAttributes().getElencoCampi().size(); i++) {
      CampoSchedaTagImpl campo = (CampoSchedaTagImpl) this.getAttributes().getElencoCampi().get(
          i);
      buf.append(this.getHtmlStartArchivio(i));
      if (campo.isVisibile()) {
        buf.append(campo.toString(!modifica, this.getAttributes().isArchivio(),
            this.getPageContext()));
      }
      buf.append(this.getHtmlEndArchivio(i));
      // {MF231006} Aggiunta del flag con l'elenco dei campi
      if (i > 0) elencoCampi.append(";");
      elencoCampi.append(campo.getNome());
    }

    buf.append("</table>\n");
    // Appendo tutti i campi non visibili (per gestione con standard HTML i
    // campi vuoti non possono essere allinterno)
    for (int i = 0; i < this.getAttributes().getElencoCampi().size(); i++) {
      CampoSchedaTagImpl campo = (CampoSchedaTagImpl) this.getAttributes().getElencoCampi().get(
          i);
      if (!campo.isVisibile()) {
        buf.append(campo.toString(!modifica, this.getAttributes().isArchivio(),
            this.getPageContext()));
      }
    }

    buf.append(UtilityTags.getHtmlHideInput(
        UtilityTags.DEFAULT_HIDDEN_ELENCO_CAMPI, elencoCampi.toString()));
    // Aggiungo l'eventuale archivio di passaggio (se richiamato da un
    // archivio
    ArchivioRequest archReq = ArchivioRequest.getArchivio(this.pageContext);
    if (archReq != null) {
      buf.append(archReq.toString());
    }

    buf.append("</form>");
    // A questo punto aggiungo tutte le form per gli archivi
    for (int i = 0; i < this.getAttributes().getArchivi().size(); i++) {
      ArchivioTagImpl impl = (ArchivioTagImpl) this.getAttributes().getArchivi().get(
          i);
      // Aggiungo l'archivio
      buf.append(impl.toString());
      this.getJavascript().println(impl.getCreateJsObject());
    }
    this.body.append(buf);
  }

  public int doEndTag() throws JspException {
    BodyContent bodyCon = this.getBodyContent();
    if (bodyCon != null) {
      bodyCon.clearBody();
    }
    try {
      this.pageContext.getOut().write(body.toString());
    } catch (IOException e) {
      throw new JspException(e);
    }
    this.body = null;

    return super.doEndTag();
  }

  private void addDatiRiga() {
    // Oggetto nel request con i dati della riga
    HashMap datiRiga = new HashMap();
    for (int i = 0; i < this.getAttributes().getElencoCampi().size(); i++) {
      CampoSchedaTagImpl campo = (CampoSchedaTagImpl) this.getAttributes().getElencoCampi().get(
          i);
      if (campo.getNome() != null && campo.getNome().length() > 0) {
        datiRiga.put(campo.getNome(), campo.getValue() == null
            ? ""
            : campo.getValue());
      }
    }
    // Aggiungo nel request i dati della riga
    this.pageContext.setAttribute(UtilityTags.REQUEST_VAR_DATI_RIGA, datiRiga,
        PageContext.REQUEST_SCOPE);
  }

  private String getHtmlStartArchivio(int campo) {
    for (int i = 0; i < this.getAttributes().getArchivi().size(); i++) {
      String ret = ((ArchivioTagImpl) this.getAttributes().getArchivi().get(i)).getHTMLStartArchivio(campo);
      if (ret != null) {
        this.getAttributes().setArchivio(true);
        return ret;
      }
    }
    return "";
  }

  private String getHtmlEndArchivio(int campo) {
    for (int i = 0; i < this.getAttributes().getArchivi().size(); i++) {
      String ret = ((ArchivioTagImpl) this.getAttributes().getArchivi().get(i)).getHTMLEndArchivio(campo);
      if (ret != null) {
        this.getAttributes().setArchivio(false);
        return ret;
      }
    }
    return "";
  }

  /**
   * Funzione che dice se siamo alla prima iterazione
   *
   * @return true Se si è alla perima iterazione. false Non è la prima
   *         iterazione
   */
  public boolean isFirstIteration() {
    return this.getNumIteration() == 0;
  }

  public CampoSchedaTagImpl getCampo(int numero) {
    if (numero < 0 || numero >= this.getAttributes().getElencoCampi().size())
      return null;
    return (CampoSchedaTagImpl) this.getAttributes().getElencoCampi().get(
        numero);
  }

  /**
   * Funzione che da il numero del campo
   *
   * @return
   */
  public int getNCampo() {
    int ret = this.getAttributes().getCampoAttivo();
    this.setNCampo(ret + 1);
    return ret;
  }

  /**
   * @see it.eldasoft.gene.tags.decorators.scheda.IFormScheda#getFormName()
   */
  public String getFormName() {
    return "local" + this.getId();
  }

  public void addArchivio(ArchivioTagImpl archivio) {
    this.getAttributes().getArchivi().add(archivio);
  }

  public CampoSchedaTagImpl getDecoratore(CampoSchedaTag tag) {
    CampoSchedaTagImpl ret = null;
    if (tag.getNCampo() < 0) tag.setNCampo(this.getNCampo());
    if (getCampo(tag.getNCampo()) == null) {
      ret = new CampoSchedaTagImpl();
      ret.setJs(this.getJavascript());
      this.addCampo(ret);
      tag.setDecoratore(ret);
      return ret;
    }
    ret = getCampo(tag.getNCampo());
    tag.setDecoratore(ret);
    return ret;

  }

  /**
   * @param campo
   *        The nCampo to set.
   */
  public void setNCampo(int campo) {
    getAttributes().setCampoAttivo(campo);
  }

  /**
   * Verifica se esiste o meno un campo nell'elenco
   *
   * @param string
   *        nome del campo
   * @return true se il campo esiste, false altrimenti
   */
  public boolean isCampo(String nomeCampoFisico) {
    for (int i = 0; i < getAttributes().getElencoCampi().size(); i++) {
      CampoSchedaTagImpl campo = (CampoSchedaTagImpl) getAttributes().getElencoCampi().get(
          i);
      if (campo.getNomeFisico() != null
          && campo.getNomeFisico().equalsIgnoreCase(nomeCampoFisico))
        return true;
    }
    return false;
  }

  /**
   * @return istanza del plugin specificato nel tag
   */
  private AbstractGestorePreload getPluginInstance() {
    Object o = null;
    if (this.getAttributes().getPlugin() != null) {
      try {
        // si crea il plugin con un argomento valorizzato con il tag stesso
        Class cl = Class.forName(this.getAttributes().getPlugin());
        java.lang.reflect.Constructor constructor = cl.getConstructor(new Class[] { BodyTagSupportGene.class });
        o = constructor.newInstance(new Object[] { this });
      } catch (Exception e) {
        logger.warn("Errore durante l'istanziazione del plugin, si considera la definizione come assente", e);
        o = null;
      }
    }
    return (AbstractGestorePreload) o;
  }

}