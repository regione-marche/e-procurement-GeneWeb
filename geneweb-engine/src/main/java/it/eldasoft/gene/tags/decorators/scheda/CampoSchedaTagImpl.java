package it.eldasoft.gene.tags.decorators.scheda;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.decorators.archivi.ArchivioTagImpl;
import it.eldasoft.gene.tags.decorators.campi.CampoDecorator;
import it.eldasoft.gene.tags.link.UtilityPopUpCampiImpl;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Classe che gestisce l'implementazione di un campo di una scheda
 *
 * @author marco.franceschin
 */
public class CampoSchedaTagImpl extends CampoDecorator {

  /** Sostituisce la vecchia property it.eldasoft.campoChiave.regExpCaratteriAmmessi. */
  private static final String REGULAR_EXPRESSION_CARATTERI_AMMESSI_CHIAVE = "A-Za-z0-9./ \\\\$_\\\\-@";

  private int             numeroCampo;

  private boolean         obbligatorio;

  private boolean         keyCheck;

  private boolean         addTr;

  private boolean         hideTitle;

  /**
   * Referenza al decoratore archivio introdotta per fare in modo che dal campo
   * scheda si possano reperire tutti i campi definiti all'interno del tag
   * archivio per l'estrazione poi dei valori dei campi chiave del record in
   * archivio
   * @since 1.4.6
   */
  private ArchivioTagImpl archivio;

  /**
   * Costruttore della classe
   *
   * @param campoScheda
   * @param pageContext
   * @throws JspException
   */
  public CampoSchedaTagImpl() {
    this.numeroCampo = -1;
    this.obbligatorio = false;
    this.keyCheck = false;
    this.setActive(false);
    this.addTr = true;
    this.hideTitle = false;
    this.archivio = null;
  }

  private void addCheckKey() {
    if (!this.isVisualizzazione() && this.getJs() != null) {
      // Se si tratta di un campo testo
      if (this.getTipo() != null
          && this.getTipo().length() > 0
          && this.getTipo().charAt(0) == JdbcParametro.TIPO_TESTO) {

        if (this.isChiave() || this.isKeyCheck()) {
          String caratteri, messaggio;
          caratteri = REGULAR_EXPRESSION_CARATTERI_AMMESSI_CHIAVE;
          messaggio = UtilityTags.getResource(
              "match.tags.template.dettaglio.campoChiave.messaggio",
              new String[] {}, true);
          this.getJs().println(
              CheckCampoSchedaTag.toString(this.getFormName(), this.getNome(),
                  "\"##\" == \"\" || \"##\".match(\"^["
                      + caratteri
                      + "]+$\")!=null", "!\""
                      + messaggio
                      + "\".replace(\"{0}\",\"##\".match(\"[^"
                      + caratteri
                      + "]+\"))", true, true));
          // this.getJs().println(this.getFormName()
          // + ".getCampo(\""+ this.getNome()+
          // "\").fnValidazione=checkCampoKey;");
        }
      }
    }
  }

  /**
   * Trasformazione in stringa del campo
   *
   * @param visualizzazione
   * @param archivio
   *        Flag che dice che il campo si trova allinterno di un archivio
   * @param context
   * @return String Stringa da inserire nell'HTML
   */
  public String toString(boolean visualizzazione, boolean archivio,
      PageContext context) {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 18/10/2006 M.F. Aggiunta dell'id di riga per poter rendere visibili o
    // invisibili le righe attraverso i javascript
    // ************************************************************

    StringBuffer buf = new StringBuffer("");

    // Creo l'id
    String idCampo;
    if (this.getId() != null) {
      idCampo = this.getId();
    } else {
      idCampo = this.getNome();
    }


    if (this.gestore != null) this.gestore.setPageContext(context);
    if (this.isVisibile()) {
      boolean lTr = this.addTr;
      // Se è visibile lo inserisco normalmente
      if (lTr) {
        buf.append("<tr ");
        buf.append(UtilityTags.getHtmlAttrib("id", "row" + idCampo));
        buf.append(">\n");
      }
      if (!this.hideTitle && this.getTitle() != null) {
        buf.append("<td class=\"etichetta-dato");
        // Se è all'interno di un archivio aggiungo archivio alla classe
        if (archivio) buf.append("-archivio");
        buf.append("\">");
        buf.append(this.getTitle());
        if (this.isObbligatorio()
            && !(visualizzazione || !this.isModificabile()))
          buf.append(" (*)");
        buf.append("</td>\n");
      }

      // Setto in visualizzazione anche se non è modificabile
      this.setVisualizzazione(visualizzazione);

      if (this.getCampo() != null) {
        buf.append("<td class=\"valore-dato\"");
        if (this.hideTitle || this.getTitle() == null) {
          buf.append(" colspan=\"2\"");
        }
        buf.append(">");
        if (!visualizzazione) {
          // Sabbadin 27/09/2012: si elimina l'inserimento della voce di popup da calendario in quanto sostituita dal datepicker di jquery
          //          // Se si tratta di un campo data allora aggiungo il menu per la
          //          // visualizzazione del calendario
          //          if (this.isModificabile() && this.getTipo().charAt(0) == 'D')
          //            UtilityPopUpCampiImpl.addFromResource("menu.tags.campo.calendario",
          //                new String[] { this.getNome() }, this);

          // F.D. 28/04/08 le voci di menù del popup vengono inserite solo se si
          // ha almeno un'abilitazione al generatore ricerche o modelli
          // carico il profilo utente con le opzioni in modo che aggiungo al
          // menù popup l'elemento "info campo" solo se
          // l'utente è abilitato almeno ad una gestione personale di report o
          // modelli
          ProfiloUtente profilo = (ProfiloUtente) context.getSession().getAttribute(
              CostantiGenerali.PROFILO_UTENTE_SESSIONE);
          if (profilo != null) {
            OpzioniUtente opzioniUtente = new OpzioniUtente(
                profilo.getFunzioniUtenteAbilitate());
            CheckOpzioniUtente opzioniGenRicCompleto = new CheckOpzioniUtente(
                CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
            CheckOpzioniUtente opzioniGenRicPersonale = new CheckOpzioniUtente(
                CostantiGeneraliAccount.SOLO_REPORT_PERSONALI_GENRIC);
            CheckOpzioniUtente opzioniGenModCompleto = new CheckOpzioniUtente(
                CostantiGeneraliAccount.GESTIONE_COMPLETA_GENMOD);
            CheckOpzioniUtente opzioniGenModPersonale = new CheckOpzioniUtente(
                CostantiGeneraliAccount.SOLO_MODELLI_PERSONALI_GENMOD);

            if ((opzioniGenRicCompleto.test(opzioniUtente)
                || opzioniGenRicPersonale.test(opzioniUtente)
                || opzioniGenModCompleto.test(opzioniUtente) || opzioniGenModPersonale.test(opzioniUtente)))
              UtilityPopUpCampiImpl.addMenuStandardCampo(this);
          }

        }
        // Se è iun campo collegato ad un archivio setto la classe di
        // visualizzazione dell'edit
        if (archivio) this.setClassEdit("edit-archivio");
        // {MF150207} Aggiunta dell'edit style come mandatory se obbligatorio
        // {SS061107} Tolto se dell'edit style obbligatorio, dato che non ha una
        // visualizzazione specifica
        // if (this.isObbligatorio()) this.setClassEdit("edit-mandatory");
        buf.append(this.toString());
        // Se non è in visualizzazione aggiungo il menu contestuale

        buf.append(this.getBody());
        buf.append("</td>\n");
      } else {
        if (this.getTitle() == null) {
          buf.append(this.getBody());
        } else {
          buf.append("<td class=\"valore-dato\">");
          buf.append(this.getBody());
          buf.append("<td>\n");
        }
      }
      if (lTr) {
        buf.append("</tr>\n");
      }
      // Aggiungo l'eventuale controllo sul campo chiave obbligatorio
      this.addCheckKey();
      // Se è obbligatorio allora aggiungo il controllo sul campo
      if (this.isObbligatorio()) {
        this.getJs().println(
            CheckCampoSchedaTag.toString(this.getFormName(), this.getNome(),
                "\"##\".length>0", "Il campo \""
                    + this.getTitle()
                    + "\" è obbligatorio", true, true));
      }
    } else {
      // Il campo non deve essere visibile
      this.setVisualizzazione(true);
      buf.append(this.toString());
    }
    return buf.toString();
  }

  /**
   * @return Returns the numeroCampo.
   */
  public int getNumeroCampo() {
    return numeroCampo;
  }

  /**
   * @param numeroCampo
   *        The numeroCampo to set.
   */
  public void setNumeroCampo(int numeroCampo) {
    this.numeroCampo = numeroCampo;
  }

  /**
   * Funzione che verifica che sia collegato al database
   *
   * @return
   */
  public boolean isCampoDB() {
    if (this.getCampo() != null && !this.isCampoFittizio()) return true;
    return false;
  }

  /**
   * @return Returns the modificabile.
   */
  public boolean isModificabile() {
    return this.isAbilitato();
  }

  /**
   * @param modificabile
   *        The modificabile to set.
   */
  public void setModificabile(boolean modificabile) {
    this.setAbilitato(modificabile);
  }

  /**
   * @return Returns the obbligatorio.
   */
  @Override
  public boolean isObbligatorio() {
    return obbligatorio;
  }

  /**
   * @param obbligatorio
   *        The obbligatorio to set.
   */
  @Override
  public void setObbligatorio(boolean obbligatorio) {
    this.obbligatorio = obbligatorio;
  }

  /**
   * Funzione che setta i valori sensibili prendendoli ca un tipo CampoSchedaTag
   *
   * @param tag
   */
  public void setValoriSensibili(CampoSchedaTag tag) {
    this.setWhere(tag.getWhere());
    this.setFrom(tag.getFrom());
    if (tag.getTitle() != null && tag.getTitle().length() > 0)
      this.setTitle(tag.getTitle());
    this.setVisibile(tag.isVisibile());
    this.setObbligatorio(tag.isObbligatorio());
    this.setModificabile(this.isModificabile());
    this.setHref(this.getHref());
    this.setDefaultValue(this.getDefaultValue());

  }

  public boolean isKeyCheck() {
    return keyCheck;
  }

  public void setKeyCheck(boolean keyCheck) {
    this.keyCheck = keyCheck;
  }

  public boolean isAddTr() {
    return addTr;
  }

  public void setAddTr(boolean addTr) {
    this.addTr = addTr;
  }

  /**
   * @return Ritorna hideTitle.
   */
  public boolean isHideTitle() {
    return hideTitle;
  }


  /**
   * @param hideTitle hideTitle da settare internamente alla classe.
   */
  public void setHideTitle(boolean hideTitle) {
    this.hideTitle = hideTitle;
  }

  /**
   * @return Ritorna archivio.
   */
  public ArchivioTagImpl getArchivio() {
    return archivio;
  }


  /**
   * @param archivio archivio da settare internamente alla classe.
   */
  public void setArchivio(ArchivioTagImpl archivio) {
    this.archivio = archivio;
  }


}
