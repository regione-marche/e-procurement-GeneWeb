package it.eldasoft.gene.tags.decorators.archivi;

import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.decorators.scheda.CampoSchedaTagImpl;
import it.eldasoft.gene.tags.decorators.scheda.FormSchedaTag;
import it.eldasoft.gene.tags.decorators.scheda.IFormScheda;
import it.eldasoft.gene.tags.decorators.wizard.FormSchedaWizardTag;

import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

public class ArchivioTag extends BodyTagSupportGene {

  private static final long serialVersionUID = 4531546196657945723L;

  private String            titolo;

  private boolean           obbligatorio;

  private String            lista;

  private String            scheda;

  private String            schedaPopUp;

  private String            campi;

  private String            campiMaschera;

  private String            chiave;
  
  private int               campoStart;

  private int               campoEnd;

  private String            where;

  private String            formName;
  
  private String            campiNoSet;

  /** Flag per dire che è possibile eseguire l'inserimento da archivio */
  private boolean           inseribile;
  
  /**
   * Flag per dire se i dati della pagina possono essere dati inputati
   * liberamente e quindi scollegati dall'archivio vero e proprio
   * @since 1.4.6
   */
  private boolean           scollegabile;

  /**
   * Necessario per calcolare se un archivio è collegato o meno con un dato nel
   * db
   * @since 1.4.6
   */
  private Vector            elencoCampiSchedaTag;


  private void setNull() {
    this.titolo = null;
    this.lista = null;
    this.scheda = null;
    this.schedaPopUp = null;
    this.campi = null;
    this.campiMaschera = "";
    this.chiave = null;
    this.campoStart = -1;
    this.where = null;
    this.formName = null;
    this.inseribile = true;
    this.campiNoSet=null;
    this.scollegabile = false;
    this.elencoCampiSchedaTag = new Vector();
  }

  public ArchivioTag() {
    super("archivio");
    this.setNull();
  }

  public int doStartTag() throws JspException {
    super.doStartTag();
    // Continua con la valutazione
    return EVAL_PAGE;
  }

  public int doEndTag() throws JspException {

    // si prova prima con form scheda e poi con form scheda wizard
    IFormScheda parent = (FormSchedaTag) getParent(FormSchedaTag.class);
    if (parent == null)
      parent = (FormSchedaWizardTag) getParent(FormSchedaWizardTag.class);
    if (parent == null)
      throw new JspException(
          "Il tag archivio deve trovarsi all'interno di una scheda (formScheda o formSchedaWizard)");

    BodyContent oBody = getBodyContent();
    if (parent.isFirstIteration()) {
      // SABBADIN 04/03/2010 (1.4.6)
      // si crea un decoratore dei dati dell'archivio in modo da inserire una
      // referenza a se stesso in ogni campo all'interno dell'archivio
      // (l'importante è far in modo che i campi scheda possano reperire
      // elencoCampiSchedaTag dall'oggetto archivio nel seguito, in quanto
      // da ogni campo inserito nell'archivio deve essere possibile reperire
      // tutti gli campi inseriti)
      ArchivioTagImpl impl = new ArchivioTagImpl(this, parent,
          parent.getPageContext(), null);
      for (int i = 0; i < impl.getElencoCampiSchedaTag().size(); i++) {
        ((CampoSchedaTagImpl) impl.getElencoCampiSchedaTag().elementAt(i)).setArchivio(impl);
      }
    } else {
      String body = "";
      if (oBody != null) body = oBody.getString().trim();

      parent.addArchivio(new ArchivioTagImpl(this, parent, parent.getPageContext(),
          body));
    }
    if (oBody != null) oBody.clearBody();
    this.setNull();
    super.doEndTag();
    return EVAL_PAGE;
  }

  /**
   * @return Returns the campi.
   */
  public String getCampi() {
    return campi;
  }

  /**
   * @param campi
   *        The campi to set.
   */
  public void setCampi(String campi) {
    this.campi = campi;
  }

  /**
   * @return Returns the lista.
   */
  public String getLista() {
    return lista;
  }

  /**
   * @param lista
   *        The lista to set.
   */
  public void setLista(String lista) {
    this.lista = lista;
  }

  /**
   * @return Returns the obbligatorio.
   */
  public boolean isObbligatorio() {
    return obbligatorio;
  }

  /**
   * @param obbligatorio
   *        The obbligatorio to set.
   */
  public void setObbligatorio(boolean obbligatorio) {
    this.obbligatorio = obbligatorio;
  }

  /**
   * @return Returns the scheda.
   */
  public String getScheda() {
    return scheda;
  }

  /**
   * @param scheda
   *        The scheda to set.
   */
  public void setScheda(String scheda) {
    this.scheda = scheda;
  }

  /**
   * @return Returns the schedaPopUp.
   */
  public String getSchedaPopUp() {
    return schedaPopUp;
  }

  /**
   * @param schedaPopUp
   *        The schedaPopUp to set.
   */
  public void setSchedaPopUp(String schedaPopUp) {
    this.schedaPopUp = schedaPopUp;
  }

  /**
   * @return Returns the titolo.
   */
  public String getTitolo() {
    return titolo;
  }

  /**
   * @param titolo
   *        The titolo to set.
   */
  public void setTitolo(String titolo) {
    this.titolo = titolo;
  }

  /**
   * Funzione che aggiunge un campo collegato all'archivio
   * 
   * @param nome
   *        Nome del campo da collegare@param campo Campo
   */
  public void addCampo(String nome) {
    if (this.campiMaschera.length() > 0) this.campiMaschera += ";";
    this.campiMaschera += nome;
    return;
  }

  /**
   * @return Returns the campiMaschera.
   */
  public String getCampiMaschera() {
    return campiMaschera;
  }

  /**
   * @return Returns the chiave.
   */
  public String getChiave() {
    return chiave;
  }

  /**
   * @param chiave
   *        The chiave to set.
   */
  public void setChiave(String chiave) {
    this.chiave = chiave;
  }

  /**
   * @return Returns the campoEnd.
   */
  public int getCampoEnd() {
    return campoEnd;
  }

  /**
   * @param campoEnd
   *        The campoEnd to set.
   */
  public void setCampo(int campo) {
    // ///////////////////////////////////////////////////////////////
    // Modifiche
    // ----------------
    // 13/02/2007 M.F. Setto sempre il campo di fine. (problema con i garchivi
    // con un solo campo di collefamento)
    // ///////////////////////////////////////////////////////////////
    if (campoStart == -1) campoStart = campo;
    campoEnd = campo;
  }

  /**
   * @return Returns the campoStart.
   */
  public int getCampoStart() {
    return campoStart;
  }

  /**
   * @return Returns the where.
   */
  public String getWhere() {
    return where;
  }

  /**
   * @param where
   *        The where to set.
   */
  public void setWhere(String where) {
    this.where = where;
  }

  /**
   * @return Returns the formName.
   */
  public String getFormName() {
    return formName;
  }

  /**
   * @param formName
   *        The formName to set.
   */
  public void setFormName(String formName) {
    this.formName = formName;
  }

  /**
   * Se è settato il nome del form restituisco quello come identificativo
   */
  public String getId() {
    if (this.formName != null && this.formName.length() > 0)
      return this.formName;
    return super.getId();
  }

  /**
   * @return Returns the inseribile.
   */
  public boolean isInseribile() {
    return inseribile;
  }

  /**
   * @param inseribile
   *        The inseribile to set.
   */
  public void setInseribile(boolean inseribile) {
    this.inseribile = inseribile;
  }

  
  /**
   * @return the campiNoSet
   */
  public String getCampiNoSet() {
    return campiNoSet;
  }

  
  /**
   * @param campiNoSet the campiNoSet to set
   */
  public void setCampiNoSet(String campiNoSet) {
    this.campiNoSet = campiNoSet;
  }

  
  /**
   * @return Ritorna scollegabile.
   */
  public boolean isScollegabile() {
    return scollegabile;
  }

  
  /**
   * @param scollegabile scollegabile da settare internamente alla classe.
   */
  public void setScollegabile(boolean scollegabile) {
    this.scollegabile = scollegabile;
  }
  
  public void addCampoSchedaTag(CampoSchedaTagImpl campo) {
    this.elencoCampiSchedaTag.add(campo);
  }

  /**
   * @param elencoCampiSchedaTag elencoCampiSchedaTag da settare internamente alla classe.
   */
  public void setElencoCampiSchedaTag(Vector elencoCampiSchedaTag) {
    this.elencoCampiSchedaTag = elencoCampiSchedaTag;
  }

  /**
   * @return Ritorna elencoCampiTag.
   */
  public Vector getElencoCampiSchedaTag() {
    return elencoCampiSchedaTag;
  }
  
  
  
  

}
