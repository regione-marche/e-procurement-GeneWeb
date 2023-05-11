/*
 * Created on 19-feb-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.scheda;

import javax.servlet.jsp.JspException;

import it.eldasoft.gene.tags.TagAttributes;
import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.tags.decorators.lista.FormListaTag;
import it.eldasoft.gene.tags.decorators.trova.FormTrovaTag;
import it.eldasoft.gene.tags.decorators.wizard.FormSchedaWizardTag;
import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Tag che gestisce il ragruppamento tra campi
 * 
 * @author marco.franceschin
 * 
 */
public class GruppoCampiSchedaTag extends TagSupportGene {

  /**
   * 
   */
  private static final long serialVersionUID = -4633819081889150655L;

  
  public TagAttributes newTagAttributes() {
    return new GruppoCampiAttributes(this.getTipoVar());
  }
  
  private GruppoCampiAttributes getAttributes(){
    return (GruppoCampiAttributes)TagAttributes.getInstance(this);
  }

  public GruppoCampiSchedaTag() {
    
  }

  public int doStartTag() throws JspException {
    
    super.doStartTag();
    boolean gestisciProtez = false;
    // Verifico se si trova all'interno di una maschera a pagine
    FormSchedaTag schedaParent = (FormSchedaTag) getParent(FormSchedaTag.class);
    if (schedaParent != null)
      gestisciProtez = schedaParent.isGestisciProtezioni();
    FormListaTag listaParent = (FormListaTag) getParent(FormListaTag.class);
    if (listaParent != null)
      gestisciProtez = listaParent.isGestisciProtezioni();
    FormTrovaTag trovaParent = (FormTrovaTag) getParent(FormTrovaTag.class);
    if (trovaParent != null)
      gestisciProtez = trovaParent.isGestisciProtezioni();
    FormSchedaWizardTag trovaWizardParent = (FormSchedaWizardTag) getParent(FormSchedaWizardTag.class);
    if (trovaWizardParent != null)
      gestisciProtez = trovaWizardParent.isGestisciProtezioni();

    if (schedaParent == null && listaParent == null && trovaParent == null && trovaWizardParent == null)
      throw new JspException(
          "Il tag gruppoCampi deve trovarsi all'interno di un formTrova, formLista, formScheda, o formSchedaWizard");
    // Verifico se si tratta di un gruppo di campi
    if (gestisciProtez
        && getIdProtezioni() != null
        && getIdProtezioni().length() > 0) {
      if(this.isVisibile())
        if (!UtilityTags.checkProtection(this.getPageContext(), "SEZ",
            "VIS", UtilityTags.getIdRequest(this.getPageContext())
                + "."
                + this.getIdProtezioni(),true)) this.setVisibile(false);
      if(!UtilityTags.checkProtection(this.getPageContext(), "SEZ",
          "MOD", UtilityTags.getIdRequest(this.getPageContext())
          + "."
          + this.getIdProtezioni(),true)) this.setModifica(false);
    }
    // Se è all'interno di una trova e non deve essere visibile eseguo lo skip
    // del corpo
    if (trovaParent != null && !this.isVisibile()) return SKIP_BODY;
    return EVAL_PAGE;
  }

  /**
   * Alla fine del tag sbianco le variabili
   */
  public int doEndTag() throws JspException {
    return super.doEndTag();
  }

  public String getIdProtezioni() {
    return getAttributes().getIdProtezioni();
  }

  public void setIdProtezioni(String idProtezioni) {
    getAttributes().setIdProtezioni(idProtezioni);
  }

  public boolean isVisibile() {
    return getAttributes().isVisibile();
  }

  public void setVisibile(boolean visibile) {
    getAttributes().setVisibile(visibile);
  }

  
  /**
   * @return the modifica
   */
  public boolean isModifica() {
    return getAttributes().isModifica();
  }

  
  /**
   * @param modifica the modifica to set
   */
  public void setModifica(boolean modifica) {
    getAttributes().setModifica(modifica);
  }

}
