package it.eldasoft.gene.tags.decorators.scheda;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.decorators.archivi.ArchivioTag;
import it.eldasoft.gene.tags.decorators.campi.AbstractCampoBodyTag;
import it.eldasoft.gene.tags.decorators.campi.CampoDecorator;
import it.eldasoft.gene.tags.decorators.wizard.FormSchedaWizardTag;
import it.eldasoft.gene.tags.link.UtilityPopUpCampiImpl;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.commons.lang.StringUtils;

public class CampoSchedaTag extends AbstractCampoBodyTag {

  /**
   *
   */
  private static final long serialVersionUID = 9006125305114692893L;

  /*
   * Variabili locali
   */

  private int               nCampo           = -1;

  @Override
  public int doStartTag() throws JspException {
    // Aggiungo il settaggio a modificabile se si trova all'interno di un gruppo

    GruppoCampiSchedaTag gruppo = (GruppoCampiSchedaTag) getParent(GruppoCampiSchedaTag.class);
    if (gruppo != null) {
      if (this.isModificabile()) this.setModificabile(gruppo.isModifica());
    }
    // si prova prima con form scheda e poi con form scheda wizard
    IFormScheda parent = (FormSchedaTag) getParent(FormSchedaTag.class);
    if (parent == null)
      parent = (FormSchedaWizardTag) getParent(FormSchedaWizardTag.class);
    if (parent == null)
      throw new JspException(
          "Il tag campoScheda deve trovarsi all'interno di una scheda (formScheda o formSchedaWizard)");
    CampoSchedaTagImpl decoratore = parent.getDecoratore(this);

    if (parent.isFirstIteration()) {

      // SABBADIN 04/03/2010 (1.4.6)
      // Verifico se il campo è contenuto in un archivio, in tal caso inserisco
      // nell'archivio un riferimento al campo per analizzare nel seguito (in
      // seconda interazione) se l'archivio è collegato nel DB
      ArchivioTag archivio = (ArchivioTag) findAncestorWithClass(this,
          ArchivioTag.class);
      if (archivio != null) {
        archivio.addCampoSchedaTag(decoratore);
      }

      // Setto il nome delle form esatto
      this.setFormName(parent.getFormName());
      if (this.getEntita() == null
          && this.getCampo() != null
          && (!this.isCampoFittizio())
          && (!this.isComputed())) this.setEntita(parent.getEntita());

      if (this.getCampo() != null) {
        Campo campo = DizionarioCampi.getInstance().getCampoByNomeFisico(
            this.getNomeFisico());
        decoratore.setCampo(campo, this.pageContext);
        if (this.getTitle() == null && campo != null)
          this.setTitle(campo.getDescrizioneWEB());
        this.setJs(UtilityTags.getJavascript(pageContext));

        if ("TIMESTAMP".equals(decoratore.getDominio())) {
          decoratore.setModificabile(false);
        }
      } else {
        // Se il nome del campo non è stato settato setto il nome campo generico
        if (decoratore.getNome().equals("CAMPO_GENERICO"))
          decoratore.setNome("CAMPO_GENERICO"
              + String.valueOf(this.getNCampo()));
      }
      this.setFormName(parent.getFormName());
      // {MF061106} Aggiunta del settaggio del context path
      decoratore.setPageContext(((HttpServletRequest) this.pageContext.getRequest()).getContextPath());
      // Se sono impostate le protezioni a livello di lista allora le imposto
      // anche per il campo
      if (parent.isGestisciProtezioni() && !this.isSetGestisciProtezioni())
        this.setGestisciProtezioni(true);
      // {MF150207} Aggiunta della gestione dell'obbligatorietà del campo con le
      // protezioni
      if (this.isGestisciProtezioni()
          && (!this.isCampoFittizio() || this.getDecoratore().getMnemonico() != null)) {
        if (this.getNomeFisico() != null && this.getNomeFisico().length() > 0) {
          if (!this.isObbligatorio()) {
            this.setObbligatorio(UtilityTags.checkProtection(this.pageContext,
                "COLS", "MAN", this.getSchema() + "." + this.getNomeFisico(),
                false));
          }
        }
      }

    } else {
      // Se non è la prima iterazione allora

      decoratore.setActive(true);
      // Verifico se il campo è contenuto in un archivio
      ArchivioTag archivio = (ArchivioTag) findAncestorWithClass(this,
          ArchivioTag.class);
      if (archivio != null) {
        archivio.setCampo(this.nCampo);
        // SABBADIN 04/03/2010 (1.4.6)
        // aggiungo al tag archivio l'unica informazione presente nel suo
        // decoratore che va costruita alla prima iterazione sull'archivio e
        // riusata alla seconda
        archivio.setElencoCampiSchedaTag(decoratore.getArchivio().getElencoCampiSchedaTag());

        // {M.F. 22.11.2006 } Visualizzo seleziona da lista solo se il campo è
        // abilitato
        if (archivio.getLista() != null
            && archivio.getLista().length() > 0
            && this.isAbilitato())
          UtilityPopUpCampiImpl.addFromResource(
              "popupmenu.tags.archivio.selezione", new String[] {
                  archivio.getId(), archivio.getTitolo() }, decoratore);
        // SS 09-10-2008
        // si introduce la voce di popup per la visualizzazione del dato in
        // archivio in popup solo se si è nella pagina principale
        // dell'applicativo; nel caso in cui sia in una popup, blocco la
        // possibilità in modo da non avere un'apertura eccessiva di popup e
        // trovarmi con i pulsanti in alto "Torna alla lista" e "Seleziona"
        // tipici di una visualizzazione di un dato in archivio dopo il suo
        // inserimento
        if ("0".equals(UtilityTags.getParametro(this.getPageContext(),
            UtilityTags.DEFAULT_HIDDEN_IS_POPUP))) {
          if (archivio.getSchedaPopUp() != null
              && archivio.getSchedaPopUp().length() > 0)
            UtilityPopUpCampiImpl.addFromResource(
                "popupmenu.tags.archivio.visualizza", new String[] {
                    archivio.getId(), archivio.getTitolo() }, decoratore);
          // Setto il link se è in modalità visualizzazione
          // ed il dato è collegato (controllo introdotto con la definizione
          // di archivi scollegabili, versione 1.4.6)
          if (archivio.getScheda() != null
              && archivio.getScheda().length() > 0
              && this.isArchivioCollegato(archivio)) {
            if (decoratore.getHref() == null
                || decoratore.getHref().length() == 0) {
              decoratore.setHref("javascript:"
                  + UtilityPopUpCampiImpl.getResourceFunction(
                      "popupmenu.tags.archivio.visualizza", new String[] {
                          archivio.getId(), archivio.getTitolo() }));
              decoratore.setTitleHref(UtilityPopUpCampiImpl.getResourceTitle(
                  "popupmenu.tags.archivio.visualizza", new String[] {
                      archivio.getId(), archivio.getTitolo() }));
            }
          }
        }

        // {M.F. 10.11.2006} Se è obbligatorio e primo campo chiave allora
        // imposto il check sulla valorizzazione
        if (archivio.isObbligatorio()) {
          String firstCampoChiave = archivio.getChiave();
          if (firstCampoChiave.indexOf(';') >= 0)
            firstCampoChiave = firstCampoChiave.substring(firstCampoChiave.indexOf(';'));
          if (decoratore.getNome().equals(firstCampoChiave))
            this.getJavascript().println(
                CheckCampoSchedaTag.toString(decoratore.getFormName(),
                    decoratore.getNome(), "\"##\".length>0", "L'archivio "
                        + archivio.getTitolo()
                        + " non è collegato", true, true));
        }
        // Aggiungo il campo all'archivio
        archivio.addCampo(decoratore.getNome());
      }
      super.doStartTag();
    }
    return parent.isFirstIteration() ? SKIP_BODY : EVAL_BODY_BUFFERED;
  }

  private CampoSchedaTagImpl getCampoScheda() {
    return (CampoSchedaTagImpl) getDecoratore();
  }

  @Override
  public int doEndTag() throws JspException {

    BodyContent body = this.getBodyContent();
    if (body != null) {
      this.getCampoScheda().setBody(body.getString().trim());
      body.clearBody();
    }
    this.setNCampo(-1);
    this.setDecoratore(null);
    return super.doEndTag();
  }

  @Override
  public CampoDecorator getDecoratore() {
    FormSchedaTag scheda = (FormSchedaTag) getParent(FormSchedaTag.class);
    if (scheda != null) {
      return scheda.getDecoratore(this);
    }
    FormSchedaWizardTag schedaWizard = (FormSchedaWizardTag) getParent(FormSchedaWizardTag.class);
    if (schedaWizard != null) {
      return schedaWizard.getDecoratore(this);
    }
    return null;
  }

  @Override
  public void setDecoratore(CampoDecorator decoratore) {

  }

  /**
   * @return Returns the modificabile.
   */
  public boolean isModificabile() {
    return ((CampoSchedaTagImpl) this.getDecoratore()).isModificabile();
  }

  /**
   * @param modificabile
   *        The modificabile to set.
   */
  public void setModificabile(boolean modificabile) {
    ((CampoSchedaTagImpl) this.getDecoratore()).setModificabile(modificabile);
  }

  /**
   * @return Returns the obbligatorio.
   */
  @Override
  public boolean isObbligatorio() {
    return ((CampoSchedaTagImpl) this.getDecoratore()).isObbligatorio();
  }

  /**
   * @param obbligatorio
   *        The obbligatorio to set.
   */
  @Override
  public void setObbligatorio(boolean obbligatorio) {
    ((CampoSchedaTagImpl) this.getDecoratore()).setObbligatorio(obbligatorio);
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
   * @return Returns the tooltip.
   */
  public String getTooltip() {
    return ((CampoSchedaTagImpl) getDecoratore()).getTooltip();
  }

  /**
   * @param tooltip
   *        The tooltip to set.
   */
  public void setTooltip(String tooltip) {
    ((CampoSchedaTagImpl) getDecoratore()).setTooltip(tooltip);
  }

  public boolean isKeyCheck() {
    return ((CampoSchedaTagImpl) this.getDecoratore()).isKeyCheck();
  }

  public void setKeyCheck(boolean keyCheck) {
    ((CampoSchedaTagImpl) this.getDecoratore()).setKeyCheck(keyCheck);
  }

  public boolean isAddTr() {
    return ((CampoSchedaTagImpl) this.getDecoratore()).isAddTr();
  }

  public void setAddTr(boolean addTr) {
    ((CampoSchedaTagImpl) this.getDecoratore()).setAddTr(addTr);
  }

  public boolean isHideTitle() {
    return ((CampoSchedaTagImpl) this.getDecoratore()).isHideTitle();
  }

  public void setHideTitle(boolean hideTitle) {
    ((CampoSchedaTagImpl) this.getDecoratore()).setHideTitle(hideTitle);
  }

  /**
   * Verifica se i dati riferiti all'archivio contengono una chiave di
   * un'occorrenza nell'archivio che realmente esiste
   *
   * @param archivio
   *        tag archivio
   * @return true se esiste l'occorrenza individuata dai valori dei campi
   *         collegati alla chiave dell'archivio, false altrimenti (occorrenza
   *         non individuata su db, campi chiave nulli o mancanti)
   *
   * @since 1.4.6
   */
  private boolean isArchivioCollegato(ArchivioTag archivio) {
    boolean esito = false;
    // si estrae il nome dell'entità dell'archivio a partire dal nome della
    // tabella indicata nel path della scheda
    //ArchivioTagImpl archivio = decoratore.getArchivio();
    int primoSlash = archivio.getScheda().indexOf('/');
    int secondoSlash = archivio.getScheda().indexOf('/', primoSlash + 1);
    String nomeEntitaArchivio = archivio.getScheda().substring(primoSlash + 1,
        secondoSlash).toUpperCase();
    // si estrae l'elenco dei campi chiave della tabella dell'archivio
    Tabella tabArchivio = DizionarioTabelle.getInstance().getDaNomeTabella(
        nomeEntitaArchivio);
    List<Campo> campiChiaveArchivio = tabArchivio.getCampiKey();

    // si costruisce la condizione di filtro per estrarre l'occorrenza
    // nell'archivio a partire dal valore della sua chiave
    // si cercano i campoScheda associati ai campi chiave dell'archivio, quindi
    // si determina il valore
    StringBuffer where = new StringBuffer("");
    Object[] params = new Object[campiChiaveArchivio.size()];
    String[] campi = UtilityStringhe.deserializza(archivio.getCampi(), ';');

    boolean mancaCampoChiave = false;
    for (int i = 0; i < campiChiaveArchivio.size() && !mancaCampoChiave; i++) {
      Campo campoChiave = campiChiaveArchivio.get(i);
      String valore = null;
      for (int j = 0; j < campi.length; j++) {
        if (campi[j].equals(campoChiave.getNomeTabella()
            + "."
            + campoChiave.getNomeCampo())) {
          CampoSchedaTagImpl campoScheda = (CampoSchedaTagImpl) archivio.getElencoCampiSchedaTag().get(
              j);
          valore = campoScheda.getValue();
          // la chiave è sempre valorizzata, ed è un campo testo o un campo
          // numerico, per cui si semplifica la traduzione del valore nel tipo
          // corrispondente
          if (campoScheda.getTipo().indexOf(JdbcParametro.TIPO_NUMERICO) == 0) {
            if (StringUtils.isNotBlank(valore)) {
              params[i] = Long.parseLong(valore);
            }
          } else {
            params[i] = valore;
          }
        }
      }
      if (valore == null) {
        mancaCampoChiave = true;
      } else {
        if (where.length() > 0) where.append(" AND ");
        where.append(campoChiave.getNomeCampo()).append("=?");
      }
    }
    if (!mancaCampoChiave) {
      // se ho reperito tutti i valori che compongono la chiave, allora provo a
      // fare la select per verificare l'esistenza del record in archivio
      GeneManager geneManager = (GeneManager) UtilitySpring.getBean(
          "geneManager", pageContext, GeneManager.class);
      esito = (geneManager.countOccorrenze(nomeEntitaArchivio,
          where.toString(), params) == 1);
    }

    return esito;
  }

}
