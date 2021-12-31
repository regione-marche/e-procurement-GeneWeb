package it.eldasoft.gene.tags.js;

import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.tags.decorators.VoceMenuTag;
import it.eldasoft.gene.tags.utils.UtilityTags;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

/**
 * Classe che aggiunge la voce per il richiamo delle maschera di trova
 * dell'archivio
 * 
 * @author marco.franceschin
 * 
 */
public class VoceSubMenuTag extends TagSupportGene {

  // ************************************************************
  // Storia Modifiche:
  // Data Utente Descrizione
  // 25/10/2006 M.F. Gestione standard con i javascript
  // ************************************************************

  static Logger             logger           = Logger.getLogger(VoceSubMenuTag.class);

  /**
   * 
   */
  private static final long serialVersionUID = 1296255678369567739L;

  private String            href;

  private String            etichetta;

  private String            title;

  private int               index;

  private boolean           abilitato;
  
  /** Flag che dice se si deve o meno gestire le protezioni */
  private boolean gestisciProtezioni;
  
  /** Identificativo delle protezioni */
  private String idProtezioni;
  
  public boolean isGestisciProtezioni() {
    return gestisciProtezioni;
  }

  
  public void setGestisciProtezioni(boolean gestisciProtezioni) {
    this.gestisciProtezioni = gestisciProtezioni;
  }

  /**
   * Costruttore di default
   * 
   */
  public VoceSubMenuTag() {
    super("voceSubMenu");
    this.index = 0;
    this.href = null;
    this.etichetta = null;
    this.title = null;
    this.abilitato = true;
    this.gestisciProtezioni=false;
    this.idProtezioni=null;
    
    logger.debug("Create VoceSubMenu");
  }

  /*
   * @see javax.servlet.jsp.tagext.Tag#doStartTag()
   */
  public int doStartTag() throws JspException {
    logger.debug("doStartTag: inizio Metodo");
    // Se sono verificate le protezioni allora verifico che l'utente abbiua i privilegi per poter visualizzare il menu 
    if(this.isGestisciProtezioni()){
      if(this.getIdProtezioni()!=null && this.getIdProtezioni().length()>0){
        if(!UtilityTags.checkProtection(this.getPageContext(),this.getIdProtezioni(),true)){
          return super.doStartTag();
        }
      }
    }
    VoceMenuTag menu = (VoceMenuTag) getParent(VoceMenuTag.class);
    if (menu == null) throw new JspException("null parent of VoceSubMenu");

    try {

      StringBuffer out = new StringBuffer("");
      out.append(menu.getVarJava() + " += creaVoceSubmenu(\"");
      out.append(UtilityTags.convStringa(this.getHref()));
      out.append("\",");
      out.append(this.getIndex());
      out.append(",\"");
      out.append(UtilityTags.convStringa(this.getEtichetta()));
      out.append("\"");
      if(this.isAbilitato())
        out.append(")");
      else
        out.append(", false );");
      if (this.getJavascript() != null)
        this.getJavascript().println(out.toString());
    } catch (Exception e) {
      // Lancio l'eccezzione
      throw new JspException("Errore in VoceArchivioSubMenu", e);
    }
    logger.debug("doStartTag: fine Metodo");
    return super.doStartTag();
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int tabindex) {
    this.index = tabindex;
  }

  public String getEtichetta() {
    return etichetta;
  }

  public void setEtichetta(String etichetta) {
    this.etichetta = etichetta;
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = UtilityTags.convertHREF(href, this.pageContext.getRequest());
    ;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isAbilitato() {
    return abilitato;
  }

  public void setAbilitato(boolean abilitato) {
    this.abilitato = abilitato;
  }


  
  public String getIdProtezioni() {
    return idProtezioni;
  }


  
  public void setIdProtezioni(String idProtezioni) {
    this.idProtezioni = idProtezioni;
  }

}
