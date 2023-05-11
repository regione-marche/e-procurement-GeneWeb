package it.eldasoft.gene.tags.decorators.campi;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.decorators.scheda.GruppoCampiSchedaTag;
import it.eldasoft.gene.tags.js.Javascript;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Classe di campo generale utilizzato da tutti
 *
 * @author cit_franceschin
 *
 */
public abstract class AbstractCampoBodyTag extends BodyTagSupportGene implements
    CampoInterface {

  // ************************************************************
  // Storia Modifiche:
  // Data Utente Descrizione
  // 09/11/2006 M.F. Aggiunta del campo definizione per la definizione del
  // campo non prendendolo da c0campi
  // ************************************************************

  /**
   *
   */
  private static final long serialVersionUID = -2335610771769412922L;

  private String            gestore          = null;

  /*
   * Variabili per implementare il setter e ghetter
   */

  public AbstractCampoBodyTag() {
    super("campo");
  }

  /**
   * Aggiungo il settaggio dello javascript solo dopo aver settato il page
   * context (eliminato dal costructor perche non abbiamo ancora il page
   * context)
   */
  @Override
  public void setPageContext(PageContext aPageContext) {
    super.setPageContext(aPageContext);
    this.setJs(this.getJavascript());
  }

  /**
   * Funzione che da il decoratore utilizzato e se non è stato creato deve
   * crearlo
   *
   * @return Returns the decoratore.
   */
  abstract public CampoDecorator getDecoratore();

  /**
   * Settaggio del decoratore
   *
   * @param decoratore
   *        The decoratore to set.
   */
  abstract public void setDecoratore(CampoDecorator decoratore);

  /**
   * @return Returns the abilitato.
   */
  @Override
  public boolean isAbilitato() {
    return this.getDecoratore().isAbilitato();
  }

  /**
   * @param abilitato
   *        The abilitato to set.
   */
  @Override
  public void setAbilitato(boolean abilitato) {
    this.getDecoratore().setAbilitato(abilitato);
  }

  public boolean isObbligatorio(){
    return this.getDecoratore().isObbligatorio();
  }

  public void setObbligatorio(boolean obbligatorio){
    this.getDecoratore().setObbligatorio(obbligatorio);
  }

  /**
   * @return Returns the body.
   */
  @Override
  public String getBody() {
    return this.getDecoratore().getBody();
  }

  /**
   * @param body
   *        The body to set.
   */
  @Override
  public void setBody(String body) {
    this.getDecoratore().setBody(body);
  }

  /**
   * @return Returns the campo.
   */
  @Override
  public String getCampo() {
    return this.getDecoratore().getCampo();
  }

  /**
   * @param campo
   *        The campo to set.
   */
  @Override
  public void setCampo(String campo) {
    this.getDecoratore().setCampo(campo);
  }

  /**
   * @return Returns the entita.
   */
  @Override
  public String getEntita() {
    return this.getDecoratore().getEntita();
  }

  /**
   * @param entita
   *        The entita to set.
   */
  @Override
  public void setEntita(String entita) {
    this.getDecoratore().setEntita(entita);
  }

  /**
   * @return Returns the formName.
   */
  @Override
  public String getFormName() {
    return this.getDecoratore().getFormName();
  }

  /**
   * @param formName
   *        The formName to set.
   */
  @Override
  public void setFormName(String formName) {
    this.getDecoratore().setFormName(formName);
  }

  /**
   * @return Returns the from.
   */
  @Override
  public String getFrom() {
    return this.getDecoratore().getFrom();
  }

  /**
   * @param from
   *        The from to set.
   */
  @Override
  public void setFrom(String from) {
    this.getDecoratore().setFrom(from);
  }

  /**
   * @return Returns the href.
   */
  @Override
  public String getHref() {
    return this.getDecoratore().getHref();
  }

  /**
   * @param href
   *        The href to set.
   */
  @Override
  public void setHref(String href) {
    this.getDecoratore().setHref(href);
  }

  /**
   * @return Returns the js.
   */
  @Override
  public Javascript getJs() {
    return this.getDecoratore().getJs();
  }

  /**
   * @param js
   *        The js to set.
   */
  @Override
  public void setJs(Javascript js) {
    if (this.getDecoratore() != null) this.getDecoratore().setJs(js);
  }

  /**
   * @return Returns the nome.
   */
  @Override
  public String getNome() {
    return this.getDecoratore().getNome();
  }

  /**
   * @param nome
   *        The nome to set.
   */
  @Override
  public void setNome(String nome) {
    this.getDecoratore().setNome(nome);
  }

  /**
   * @return Returns the id.
   */
  @Override
  public String getId() {
    return this.getDecoratore().getId();
  }

  /**
   * @param id
   *        The id to set.
   */
  @Override
  public void setId(String id) {
    this.getDecoratore().setId(id);
  }

  /**
   * @return Returns the nomeFisico.
   */
  @Override
  public String getNomeFisico() {
    return this.getDecoratore().getNomeFisico();
  }

  /**
   * @param nomeFisico
   *        The nomeFisico to set.
   */
  @Override
  public void setNomeFisico(String nomeFisico) {
    this.getDecoratore().setNomeFisico(nomeFisico);
  }

  /**
   * @return Returns the tipo.
   */
  @Override
  public String getTipo() {
    return this.getDecoratore().getTipo();
  }

  /**
   * @param tipo
   *        The tipo to set.
   */
  @Override
  public void setTipo(String tipo) {
    this.getDecoratore().setTipo(tipo);
  }

  /**
   * @return Returns the title.
   */
  @Override
  public String getTitle() {
    return this.getDecoratore().getTitle();
  }

  /**
   * @param title
   *        The title to set.
   */
  @Override
  public void setTitle(String title) {
    this.getDecoratore().setTitle(title);
  }

  /**
   * @return Returns the value.
   */
  @Override
  public String getValue() {
    return this.getDecoratore().getValue();
  }

  /**
   * @param value
   *        The value to set.
   */
  @Override
  public void setValue(String value) {
    this.getDecoratore().setValue(value);
  }

  /**
   * @return Returns the visualizzazione.
   */
  @Override
  public boolean isVisualizzazione() {
    return this.getDecoratore().isVisualizzazione();
  }

  /**
   * @param visualizzazione
   *        The visualizzazione to set.
   */
  @Override
  public void setVisualizzazione(boolean visualizzazione) {
    this.getDecoratore().setVisualizzazione(visualizzazione);
  }

  /**
   * @return Returns the where.
   */
  @Override
  public String getWhere() {
    return this.getDecoratore().getWhere();
  }

  /**
   * @param where
   *        The where to set.
   */
  @Override
  public void setWhere(String where) {
    this.getDecoratore().setWhere(where);
  }

  @Override
  public int doStartTag() throws JspException {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 13/02/2007 M.F. Aggiunta del controllo sulle protezioni
    // 19/02/2007 M.F. Aggiunta del controllo che il tag si ritrovi all'interno
    // di un gruppo
    // ************************************************************

    // {MF190207} Verifico se il campo si trova all'interno di un gruppo di campi
    // Estraggo l'eventuale gruppo d'appartenenza
    GruppoCampiSchedaTag gruppo = (GruppoCampiSchedaTag) getParent(GruppoCampiSchedaTag.class);
    if (gruppo != null) {
      if (this.isVisibile())
      // Se il campo è visibile lo setto visibile
        this.setVisibile(gruppo.isVisibile());
      if (this.isAbilitato()) this.setAbilitato(gruppo.isModifica());
    }

    // Se è settato il nome fisico
    if (this.getNomeFisico() != null
        && this.getNomeFisico().length() > 0
        && this.getCampo() != null
        && this.getCampo().length() > 0) {

      if (this.isGestisciProtezioni()
          && (!this.isCampoFittizio() || this.getDecoratore().getMnemonico() != null)) {
        String nomeFisico = this.getNomeFisicoPerProt();
        // Se è visibile verifico tale proprieta' da profilo
        if (this.isVisibile()){
          this.setVisibile(UtilityTags.checkProtection(this.pageContext, "COLS",
              "VIS", nomeFisico, true));
        }

        // Se è abilitato verifico tale proprietà da profilo
        if (this.isAbilitato()) {
          this.setAbilitato(UtilityTags.checkProtection(this.pageContext, "COLS",
              "MOD", nomeFisico, true));
        }
        // 20171219: si aggiunge una configurazione editabile in w_config per bloccare in tutti i profili un set di campi indipendentemente
        // dal profilo, a patto che l'utente non sia amministratore di sistema
        String[] campiBloccatiIndipendentementeProfilo = (String[])this.pageContext.getAttribute(CostantiGenerali.ATTR_CAMPI_BLOCCATI_SOLA_LETTURA);
        if (campiBloccatiIndipendentementeProfilo == null) {
          ProfiloUtente profiloUtente = (ProfiloUtente) this.pageContext.getSession().getAttribute(
              CostantiGenerali.PROFILO_UTENTE_SESSIONE);
          OpzioniUtente opzioniUtente = new OpzioniUtente(profiloUtente.getFunzioniUtenteAbilitate());
          if (new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA).test(opzioniUtente)) {
            campiBloccatiIndipendentementeProfilo = new String[0];
          } else {
            String elencoCampi = ConfigManager.getValore(CostantiGenerali.PROP_CAMPI_BLOCCATI_SOLA_LETTURA);
            campiBloccatiIndipendentementeProfilo = StringUtils.split(StringUtils.stripToEmpty(elencoCampi), ';');
          }
          this.pageContext.setAttribute(CostantiGenerali.ATTR_CAMPI_BLOCCATI_SOLA_LETTURA, campiBloccatiIndipendentementeProfilo);
        }
        String lsModo = UtilityTags.getParametro(this.pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO);
        if (UtilityTags.SCHEDA_MODO_MODIFICA.equals(lsModo) && this.isAbilitato() && ArrayUtils.contains(campiBloccatiIndipendentementeProfilo, this.getNomeFisico())) {
          // solo nel caso di modifica, se il campo rientra in uno dei quelli da mantenere in sola lettura, va disabilitato
          this.setAbilitato(false);
        }

        // Se non e' obbligatorio verifica tale proprieta' da profilo
        if(!this.isObbligatorio()){
          this.setObbligatorio(UtilityTags.checkProtection(this.pageContext, "COLS",
              "MAN", nomeFisico, false));
        }
      }
    }

    return super.doStartTag();
  }

  private String getNomeFisicoPerProt() {
    StringBuffer ret = new StringBuffer("");

    if (this.getDecoratore().getMnemonico() != null
        && this.getDecoratore().getMnemonico().length() > 0) {
      Campo campo = DizionarioCampi.getInstance().get(
          this.getDecoratore().getMnemonico());
      if (campo != null) {
        ret.append(campo.getNomeSchema());
        ret.append(".");
        ret.append(campo.getNomeFisicoCampo());
        return ret.toString();
      }
    }
    ret.append(getSchema());
    ret.append(".");
    ret.append(getNomeFisico());
    return ret.toString();
  }

  @Override
  public int doEndTag() throws JspException {
    this.setDecoratore(null);
    this.gestore = null;
    return super.doEndTag();
  }

  /**
   * Funzione che setta il gestore del campo
   *
   * @param gestore
   * @return
   */
  public void setGestore(String gestore) {
    if (this.gestore == null || !this.gestore.equals(gestore)) {
      CampoDecorator decoratore = this.getDecoratore();
      decoratore.setServletContext(this.pageContext.getServletContext());
      decoratore.setGestore(gestore, this.pageContext);
      if (decoratore.getGestore() == null)
        throw new RuntimeException("Gestore di campo: "
            + gestore
            + " non è un gestore valido !");
      this.gestore = gestore;
    }
  }

  @Override
  public void addPopUp(String titolo, String href) {
    this.getDecoratore().addPopUp(titolo, href);
  }

  /**
   * @return Returns the active.
   */
  @Override
  public boolean isActive() {
    return this.getDecoratore().isActive();
  }

  /**
   * @param active
   *        The active to set.
   */
  @Override
  public void setActive(boolean active) {
    this.getDecoratore().setActive(active);
  }

  @Override
  public void setVisibile(boolean visibile) {
    this.getDecoratore().setVisibile(visibile);
  }

  @Override
  public boolean isVisibile() {
    return this.getDecoratore().isVisibile();
  }

  @Override
  public void setTitleHref(String titolo) {
    this.getDecoratore().setTitleHref(titolo);
  }

  @Override
  public String getTitleHref() {
    return this.getDecoratore().getTitleHref();

  }

  @Override
  public void setDefaultValue(String valore) {
    this.getDecoratore().setDefaultValue(valore);
  }

  @Override
  public String getDefaultValue() {
    return this.getDecoratore().getDefaultValue();

  }

  /**
   * @return Returns the definizione.
   */
  @Override
  public String getDefinizione() {
    return this.getDecoratore().getDefinizione();
  }

  /**
   * @param definizione
   *        The definizione to set.
   */
  @Override
  public void setDefinizione(String definizione) {
    this.getDecoratore().setDefinizione(definizione);
  }

  /**
   * @return Returns the gestore.
   */
  public String getGestore() {
    return gestore;
  }

  /**
   * Funzione che setta il campo come fittizio
   */
  @Override
  public void setCampoFittizio(boolean fittizio) {
    this.getDecoratore().setCampoFittizio(fittizio);
  }

  @Override
  public boolean isCampoFittizio() {
    return this.getDecoratore().isCampoFittizio();
  }

  @Override
  public boolean isComputed() {
    return this.getDecoratore().isComputed();
  }

  @Override
  public void setComputed(boolean computed) {
    this.getDecoratore().setComputed(computed);
  }

  @Override
  public boolean isGestisciProtezioni() {
    return this.getDecoratore().isGestisciProtezioni();
  }

  @Override
  public void setGestisciProtezioni(boolean gestisciProtezioni) {
    this.getDecoratore().setGestisciProtezioni(gestisciProtezioni);
  }

  /**
   * Funzione che verifica se è stato settato il campo gestisci protezioni
   *
   * @return
   */
  public boolean isSetGestisciProtezioni() {
    return this.getDecoratore().isSetGestisciProtezioni();
  }

  @Override
  public String getSchema() {
    if (this.getEntita() == null) return "";
    Tabella tab = DizionarioTabelle.getInstance().getDaNomeTabella(
        this.getEntita());
    if (tab != null) return tab.getNomeSchema();
    return "";
  }

  /**
   * Restituisce true se nel tag è stato settato positivo
   */
  @Override
  public boolean isSpeciale() {
    return this.getDecoratore().isSpeciale();
  }

  /**
   * Setta il valore dell'attibuto "speciale"
   */
  @Override
  public void setSpeciale(boolean speciale) {
    this.getDecoratore().setSpeciale(speciale);
  }
}
