package it.eldasoft.gene.tags.history;

import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.tags.functions.ResourceFunction;
import it.eldasoft.gene.tags.utils.UtilityTags;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;

/**
 * Classe che esegue l'inserimento nell'history dei vari tag
 *
 * @author cit_franceschin
 *
 */
public class HistoryTag extends TagSupportGene {

  /**
   *
   */
  private static final long serialVersionUID = -5125378941710868816L;

  @Override
  public int doStartTag() throws JspException {
    String keyResource = "html.tags.history.elemento";
    // Se la navigazione è disabilitata allora estraggo il tag disabilitato
    if (UtilityTags.isNavigazioneDisabilitata(this.pageContext)) {
      keyResource = "html.tags.history.elementoDisabilitato";
    }
    StringBuffer buf = new StringBuffer("");
    UtilityHistory history = UtilityTags.getUtilityHistory(this.pageContext.getSession());
    int numElementiHistory = history.size(UtilityTags.getNumeroPopUp(this.pageContext));
    for (int i = 0; i < numElementiHistory; i++) {
      if (i == 0) {
        buf.append(ResourceFunction.get(keyResource, "Home", "Torna alla Homepage", String.valueOf(-1), (String.valueOf(1600 - 1))));
      }
      buf.append(" ").append(UtilityTags.getResource("html.tags.history.separatore", null, false)).append(" ");
      String title = history.get(i,
          UtilityTags.getNumeroPopUp(this.pageContext)).getTitle();
      String label = title;
      if (title.length() > 30) {
        label = StringUtils.substring(title, 0, 30) + "...";
        int indiceECommerciale = title.substring(0, 30).lastIndexOf("&");
        int indicePuntoEVirgola = title.substring(30).indexOf(";");
        if (indiceECommerciale != -1 & indicePuntoEVirgola != -1) {
          // se abbiamo un carattere codificato tipo &agrave; a cavallo dei 30 caratteri, lo includiamo nel titolo abbreviato
          label = StringUtils.substring(title, 0, 30 + indicePuntoEVirgola + 1);
          // se l'ultimo carattere è proprio il terminatore di carattere codificato, allora non servono i puntini (vuol dire che l'ultimo
          // carattere, al piu' il trentesimo, e' codificato), altrimenti lo si mette
          if (title.length() > (30 + indicePuntoEVirgola + 1)) {
            label += "...";
          }
        }
      }
      if (i < numElementiHistory - 1) {
        buf.append(ResourceFunction.get(keyResource, label, "Torna a " + title, String.valueOf(i), (String.valueOf(1600 + i))));
      } else {
        buf.append(ResourceFunction.get("html.tags.history.elementoDisabilitato", label, title, String.valueOf(i),
            (String.valueOf(1600 + i))));
      }
    }
    // il codice commentato qui sotto servirà se si decidesse in futuro di
    // inserire nell'history anche la voce relativa alla pagina correntemente
    // visualizzata
    // buf.append(ResourceFunction.get("html.tags.history.elementoDisabilitato",history.get(numElementiHistory-1,UtilityTags.getNumeroPopUp(this.pageContext)).getTitle(),String.valueOf(numElementiHistory-1)));
    // buf.append("\n");
    try {
      this.pageContext.getOut().write(buf.toString());
    } catch (IOException e) {
      throw new JspException("History Tag: " + e.getMessage());
    }

    return SKIP_BODY;
  }

}
