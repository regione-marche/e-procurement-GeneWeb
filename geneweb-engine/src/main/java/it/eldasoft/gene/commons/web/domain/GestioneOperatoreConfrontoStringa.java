/*
 * Created on 22-lug-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.domain;

/**
 * Contenitore per la gestione dell'operatore di confronto per i campi stringa 
 * 
 * @author Luca.Giacomazzo
 */
public class GestioneOperatoreConfrontoStringa {
  
  //TODO: sistemare aggiungendo le costanti commentate IS NULL e IS NOT NULL agli array
  
  /**
   * Valori delle opzioni della combobox per l'operatore di confronto per le
   * stringhe 
   */
  public static final String[] CBX_VALORI_CONFRONTO_STRINGA = new String[] {
      "=", "contiene", "iniziaPer", "terminaPer"}; //, "IS NOT NULL", "IS NULL" };

  /**
   * Testo visualizzato nelle opzioni della combobox per l'operatore di 
   * confronto per le stringhe
   */
  public static final String[] CBX_TESTO_CONFRONTO_STRINGA = new String[] {
      "uguale", "contiene", "inizia per", "termina per"}; //, "valorizzato", "non valorizzato" };

  /**
   * Ritorna la stringa da ricercare in funzione del tipo di operatore di confronto
   * 
   * @param operatore
   * @param valoreConfronto
   * @return Ritorna la stringa da ricercare in funzione del tipo di operatore
   *         di confronto
   */
  public static String convertiStringaConfronto(String operatore, 
      String valoreConfronto){
    String result = null;
    if(valoreConfronto != null && valoreConfronto.length() > 0){
      if(CBX_VALORI_CONFRONTO_STRINGA[0].equals(operatore))
        result = valoreConfronto;  // Operatore 'uguale a'
      else if(CBX_VALORI_CONFRONTO_STRINGA[1].equals(operatore))
        result = "%" + valoreConfronto + "%";  // Operatore 'contiene'
      else if(CBX_VALORI_CONFRONTO_STRINGA[2].equals(operatore))
        result = valoreConfronto + "%";  // Operatore 'inizia per'
      else if(CBX_VALORI_CONFRONTO_STRINGA[3].equals(operatore))
        result = "%" + valoreConfronto;  // Operatore 'termina per'
    }
    return result;
  }

  /**
   * Ritorna l'operatore di confronto, convertendo le stringhe 'contiene',
   * 'iniziaPer', terminaPer' con l'operatore LIKE 
   * 
   * @param operatore
   * @return Ritorna l'operatore di confronto, convertendo le stringhe 'contiene',
   * 'iniziaPer', terminaPer' con l'operatore LIKE
   */
  public static String convertiOperatoreConfronto(String operatore){
    if (GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[0].equals(operatore))
      return "=";
//TODO: scommentare i seguenti if    
//    else if (GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[4].equals(operatore))
//      return "IS NOT NULL";
//    else if (GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[5].equals(operatore))
//      return "IS NULL";
    else
      return "LIKE";
  }
  
}