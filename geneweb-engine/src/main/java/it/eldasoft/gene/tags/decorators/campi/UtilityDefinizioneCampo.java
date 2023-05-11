package it.eldasoft.gene.tags.decorators.campi;

import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Utility che gestisce la definizione del campo
 *
 * @author franceschin
 */
public class UtilityDefinizioneCampo {

  public static String getDefinizioneCampo(CampoDecoratorImpl campo) {
    StringBuffer buf = new StringBuffer();
    buf.append(campo.getNomeFisico());
    buf.append(";");
    buf.append(campo.getTipo());
    buf.append(";");
    buf.append(campo.isChiave() ? "S" : "N");
    buf.append(";");
    buf.append(campo.getValue());
    return buf.toString();
  }

  public static String getDefinizioneCampoWithOriginalValue(CampoDecoratorImpl campo) {
    StringBuffer buf = new StringBuffer();
    buf.append(campo.getNomeFisico());
    buf.append(";");
    buf.append(campo.getTipo());
    buf.append(";");
    buf.append(campo.isChiave() ? "S" : "N");
    buf.append(";");
    buf.append(campo.getOriginalValue());
    return buf.toString();
  }

  /**
   * Funzione che restituisce il campo invisibile con la definizione del campo
   *
   * @param campo
   *        Campo da cui estrarre i dati
   */
  public static String getHtmlDefinizioneCampo(CampoDecoratorImpl campo) {
    StringBuffer buf = new StringBuffer("");

    buf.append(UtilityTags.getHtmlHideInputWithId(
        UtilityTags.DEFAULT_HIDDEN_INIZIO_DEFINIZIONE + campo.getNome(),
        UtilityTags.DEFAULT_HIDDEN_INIZIO_DEFINIZIONE + (campo.getId() != null ? campo.getId() : campo.getNome()),
        getDefinizioneCampo(campo)));
    return buf.toString();
  }

  /**
   * Funzione che restituisce il campo invisibile con la definizione del campo ed il valore originario del campo
   *
   * @param campo
   *        Campo da cui estrarre i dati
   */
  public static String getHtmlDefinizioneCampoWithOriginalValue(CampoDecoratorImpl campo) {
    StringBuffer buf = new StringBuffer("");

    buf.append(UtilityTags.getHtmlHideInputWithId(
        UtilityTags.DEFAULT_HIDDEN_INIZIO_DEFINIZIONE + campo.getNome(),
        UtilityTags.DEFAULT_HIDDEN_INIZIO_DEFINIZIONE + (campo.getId() != null ? campo.getId() : campo.getNome()),
        getDefinizioneCampoWithOriginalValue(campo)));
    return buf.toString();
  }

  private static int startPos(String definizione, int parte) {
    if (parte == 0) return 0;
    int pos = -1;
    for (int i = 0; i < parte; i++) {
      pos = definizione.indexOf(';', pos + 1);
      if (pos < 0) return -1;
    }
    return pos + 1;
  }

  /**
   * Funzione che estrae la parte dalla divisione
   *
   * @param definizione
   * @param parte
   * @return
   */
  private static String getParte(String definizione, int parte) {
    int pos = startPos(definizione, parte);
    if (pos < 0) return "";
    if (definizione.indexOf(';', pos) >= 0) {
      return definizione.substring(pos, definizione.indexOf(';', pos + 1));
    }
    return definizione.substring(pos);
  }

  /**
   * Funzione che estrae il nome fisico da una definizione
   *
   * @param definizione
   * @return
   */
  public static String getNomeFisicoFromDef(String definizione) {
    return getParte(definizione, 0);
  }

  /**
   * Funzione che estrae il tipo di campo da una definizione
   *
   * @param definizione
   * @return
   */
  public static String getTipoFromDef(String definizione) {
    return getParte(definizione, 1);
  }

  /**
   * Funzione che verifica se è un campo chiave
   *
   * @param definizione
   * @return
   */
  public static boolean isKey(String definizione) {
    String key = getParte(definizione, 2);
    if (key == null || !key.equals("S")) return false;
    return true;
  }

  /**
   * Funzione che estrae il valore dalla definizione
   *
   * @param definizione
   * @return
   */
  public static String getValue(String definizione) {
    int pos = startPos(definizione, 3);
    if (pos < 0) return "";
    return definizione.substring(pos);
  }

  /**
   * Funzione che restituisce il tipo di campo dalla definizione
   *
   * @param definizione
   * @return
   */
  public static char getTipoCharFromDef(String definizione) {
    String tipo = getTipoFromDef(definizione);
    if (tipo != null && tipo.length() > 0) {
      if (tipo.charAt(0) == JdbcParametro.TIPO_ENUMERATO)
        return tipo.length() > 1 ? tipo.charAt(1) : JdbcParametro.TIPO_TESTO;
      return tipo.charAt(0);
    }
    return JdbcParametro.TIPO_TESTO;
  }

}
