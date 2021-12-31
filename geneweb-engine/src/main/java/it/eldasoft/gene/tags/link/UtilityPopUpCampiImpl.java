package it.eldasoft.gene.tags.link;

import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.decorators.campi.CampoDecorator;
import it.eldasoft.gene.tags.functions.ResourceFunction;
import it.eldasoft.gene.tags.utils.UtilityTags;

public class UtilityPopUpCampiImpl {

  /**
   * Funzione che aggiunge un menu prendendolo da un resource
   *
   * @param idResource
   *        Identificativo del resource
   * @param parametri
   *        Elenco di parametri da impostare nella stringa trovata
   * @param campo
   *        Campo in cui aggiungere il menu
   */
  public static void addFromResource(String idResource, String parametri[],
      CampoDecorator campo) {
    String resource = ResourceFunction.get(idResource, parametri);
    String title = "", function = "";
    int pos = UtilityTags.indexOf(resource, ';', '\\', 0);
    if (pos >= 0) {
      title = resource.substring(0, pos);
      function = resource.substring(pos + 1);
    } else
      title = resource;

    campo.addPopUp(title, function);

  }

  public static void addMenuStandardCampo(CampoDecorator campo) {
    // AGgiungo solo se il mnemonico è unh mnemodico valido
    if (campo.getMnemonico() != null && campo.getMnemonico().length() > 0) {
      // Menu standard di tutti i campi
      addFromResource("menu.tags.campo.standard.helpMnemonico",
          new String[] { campo.getMnemonico() }, campo);
    }
  }

  /**
   * Conversione a stringa e aggiunta mei javascript delle righe del menu
   *
   * @param tipoJs
   *        Tipo campo gestito nei javascript
   * @return
   * @see it.eldasoft.gene.tags.decorators.campi.CampoDecoratorImpl.setTipo
   */
  public static void addMenuStandardCampoTrova(CampoDecorator campo) {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 09/11/2006 M.F. Eliminazione di tutti i menu con valori (perche non
    // gestiti momentaneamente)
    // ************************************************************

    char tipo;
    tipo = campo.getTipo().charAt(0);
    //if (tipo == JdbcParametro.TIPO_ENUMERATO) tipo = campo.getTipo().charAt(1);
    /*
     * addFromResource("menu.tags.campo.trova.tuttiIValori", new String[] {
     * campo.getNome() }, campo);
     */

    switch (tipo) {
    case JdbcParametro.TIPO_DATA:
      // Sabbadin 27/09/2012: si elimina l'inserimento della voce di popup da calendario in quanto sostituita dal datepicker di jquery
//    // Aggiungo la voce di apertura del calendario con passaggio
//    // dell'identificatico del campo
//    addFromResource("menu.tags.campo.calendario",
//        new String[] { campo.getNome() }, campo);
//    break;
    case JdbcParametro.TIPO_DECIMALE:
    case JdbcParametro.TIPO_NUMERICO:
    case JdbcParametro.TIPO_ENUMERATO:
      // Non si aggiunge nessu menu in più
      break;
    case JdbcParametro.TIPO_TESTO:
      /*
       * addFromResource("menu.tags.campo.trova.valoriInizianoPer", new String[] {
       * campo.getNome() }, campo);
       * addFromResource("menu.tags.campo.trova.valoriTerminanoPer", new
       * String[] { campo.getNome() }, campo);
       * addFromResource("menu.tags.campo.trova.valoriContengono", new String[] {
       * campo.getNome() }, campo);
       */
      /*addFromResource("menu.tags.campo.trova.iniziaPer",
          new String[] { campo.getNome() }, campo);
      addFromResource("menu.tags.campo.trova.terminaPer",
          new String[] { campo.getNome() }, campo);
      addFromResource("menu.tags.campo.trova.contiene",
          new String[] { campo.getNome() }, campo);*/
      break;
    }
    // F.D. 30/04/08 commento l'aggiunta del menù standard che viene effettuato
    // adesso nell'implementazione del campo (CampoTrovaTag)
    // Aggiungo il menu standard
    // addMenuStandardCampo(campo);
  }

  /**
   * Funzione che estrae la funzione dal resource
   *
   * @param key
   *        Chieve del resource
   * @param params
   *        Parametri
   * @return
   */
  public static String getResourceFunction(String key, String[] params) {
    String resource = ResourceFunction.get(key, params);
    int pos = UtilityTags.indexOf(resource, ';', '\\', 0);
    if (pos >= 0) return resource.substring(pos + 1);

    return "";
  }

  /**
   * Funzione che restituiece il titolo del resource
   *
   * @param key
   *        Chiave del resource
   * @param params
   *        Parametri
   * @return
   */
  public static String getResourceTitle(String key, String[] params) {
    String resource = ResourceFunction.get(key, params);
    int pos = UtilityTags.indexOf(resource, ';', '\\', 0);
    if (pos >= 0) return resource.substring(0, pos);

    return "";
  }

}
