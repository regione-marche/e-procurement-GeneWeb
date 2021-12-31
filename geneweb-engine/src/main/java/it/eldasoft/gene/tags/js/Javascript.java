package it.eldasoft.gene.tags.js;

import java.util.HashSet;

/**
 * Classe di javascript. Al suo interno vengono scritti tutti i javascript
 * 
 * @author marco.franceschin
 * 
 */
public class Javascript {

  private StringBuffer corpo;

  private HashSet      variabili;

  /**
   * Costruttore del javascript
   */
  public Javascript() {
    this.corpo = new StringBuffer();
    this.variabili = new HashSet();

  }

  /**
   * Funzione che aggiunge una parte di script ad un javascript
   * 
   * @param script
   */
  public void print(String script) {
    corpo.append(script);
  }

  /**
   * Funzione che scrive una riga nello script
   * 
   * @param script
   */
  public void println(String script) {
    corpo.append(script);
    corpo.append("\n");
  }

  /**
   * Creo lo script da scrivere nell'html
   */
  public String toString() {
    // ///////////////////////////////////////////////////////////////
    // Modifiche
    // ----------------
    // 24.10.2007: M.F. Inserisco il Javascript all'interno di un div invisibile così non aumento il javascript
    // ////////////////////////////////////////////////////////////// /

    StringBuffer buf = new StringBuffer();
    // Se c'è un corpo allora scrivo lo script
    if (corpo.length() > 0) {
      buf.append("<div style=\"display: none\"><script type=\"text/javascript\" >\n<!--\n");
      buf.append(corpo);
      buf.append("-->\n</script></div>\n");
    }
    return buf.toString();
  }

  /**
   * Funzione che aggiunge una variabile al javascript verificando che la
   * variabile non sia gia stata creata
   * 
   * @param nomeVar
   *        NOme della variabile da creare
   * @return true se create; false se è gia stata creata
   */
  public boolean addVarToJS(String nomeVar) {
    if (this.variabili != null && nomeVar != null) {
      if (this.variabili.add(new String(nomeVar))) {
        // Inserisco l'inizializzazione della variabile
        this.println("var " + nomeVar + "=\"\";");
        return true;
      }
    }
    return false;
  }

  /**
   * Finzione che verifica se è deinita una variabile
   * 
   * @param nomeVar
   * @return
   */
  public boolean existVar(String nomeVar) {
    return this.variabili.contains(nomeVar);
  }

}
