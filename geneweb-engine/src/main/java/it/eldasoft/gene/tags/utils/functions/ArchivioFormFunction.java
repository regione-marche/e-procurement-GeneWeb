/*
 * Created on 23-nov-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.utils.functions;

import it.eldasoft.gene.tags.decorators.archivi.ArchivioRequest;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class ArchivioFormFunction extends AbstractFunzioneTag {

  public ArchivioFormFunction() {
    super(1, new Class[] { PageContext.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    if (params[0] != null) {
      PageContext context = (PageContext) params[0];
      // Aggiungo l'archivio solo se siamo alla seconda pagina (visualizzazione
      // del dettaglio della scheda)
      ArchivioRequest arch = ArchivioRequest.getArchivio(context);
      if (arch != null) {
        // Se l'archivio è stato creato allora lo aggiungo
        return arch.toStringForm(context, "archivioSchedaForm");
      }
    }
    return "";
  }
}
