/*
 * Created on 25-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.filtro;

import it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm;
import it.eldasoft.utils.sql.comp.SqlElementoCondizione;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.util.Vector;

/**
 * Raccoglitore di funzioni di controllo per i filtri delle ricerche
 * 
 * @author Francesco De Filippis
 */
public class ControlloFiltri {

  private static String OPERATORE_NOT    = SqlElementoCondizione.STR_OPERATORE_LOGICO_NOT;
  private static String CONDIZIONE       = "CONDIZIONE";
  private static String PARENTESI_APERTA = SqlElementoCondizione.STR_OPERATORE_PARENTESI_APERTA;
  private static String PARENTESI_CHIUSA = SqlElementoCondizione.STR_OPERATORE_PARENTESI_CHIUSA;
  private static String OPERATORE_AND    = SqlElementoCondizione.STR_OPERATORE_LOGICO_AND;
  private static String OPERATORE_OR     = SqlElementoCondizione.STR_OPERATORE_LOGICO_OR;

  /**
   * Funzione di controllo per il numero di parentesi
   * 
   * @param filtro
   * @throws ControlloFiltriException
   */
  public static void checkParentesi(String filtro)
      throws ControlloFiltriException {
    int parentesi = 0;
    int i = 0;

    while (filtro != null && i < filtro.length()) {
      if (filtro.charAt(i) == '(') parentesi++;
      if (filtro.charAt(i) == ')') parentesi--;
      if (parentesi < 0) {
        ControlloFiltriException exception = new ControlloFiltriException(
            ControlloFiltriException.CODICE_ERRORE_NUMERO_PARENTESI_CHIUSE);
        throw exception;
      }
      i++;
    }
    if (parentesi > 0) {
      ControlloFiltriException exception = new ControlloFiltriException(
          ControlloFiltriException.CODICE_ERRORE_NUMERO_PARENTESI_APERTE);
      throw exception;
    }

  }

  /**
   * Metodo che trasforma un Vector di FiltroRicercaForm in una String[] tipizzata
   * gli elementi saranno di tre tipi :
   * operatori (AND,OR,NOT), parentesi, elementi condizione (identificati dalla stringa "condizione")
   * 
   * @param listaFiltri
   * @return String[] del filtro impostato per la ricerca
   */
  public static String[] creaStringaPerControllo(Vector<FiltroRicercaForm> listaFiltri) {
    String[] stringa = null;
    if(listaFiltri != null && listaFiltri.size() > 0){
      stringa = new String[listaFiltri.size()];
      FiltroRicercaForm filtro = null;
      // ciclo per ogni elemento della lista ed effettuo il parsing della
      // stringa nel formato che mi interessa
      for (int i = 0; i < listaFiltri.size(); i++) {
        filtro = (FiltroRicercaForm) listaFiltri.elementAt(i);
        if (filtro.getAliasTabella() == null
            || "".equalsIgnoreCase(filtro.getAliasTabella())) {
          stringa[i] = filtro.getOperatore();
        } else {
          stringa[i] = CONDIZIONE;
        }
      }
    }
    return stringa;
  }

  /**
   * Funzione di controllo per la validità del filtro inserito
   * 
   * @param filtro
   * @throws ControlloFiltriException
   */
  public static void checkFiltro(String[] filtro)
      throws ControlloFiltriException {
    /*
     * l'approccio per la verifica del filtro sara' il seguente: si considera il
     * filtro composto da 4 tipi di elemento operatori AND e OR operatore NOT
     * condizione parentesi
     * 
     * per ognuno di questi elementi si controllera' il successivo, se rientra
     * nella casistica corretta allora la funzione restituira' true se no false
     * 
     * il primo controllo e' chiaramente legato al corretto numero di parentesi
     * se questo test viene superato allora si procede a fare un parsing della
     * stringa e controllare i valori
     * 
     */
    int i;
    // uso la serializza di UtilityStringhe mettendo un carattere fittizio che
    // non fa gioco
    checkParentesi(UtilityStringhe.serializza(filtro, 'X'));
    //controllo che il primo elemento non sia un operatore AND/OR
    if (filtro != null && filtro.length > 0) {
      if (OPERATORE_AND.equalsIgnoreCase(filtro[0]) || OPERATORE_OR.equalsIgnoreCase(filtro[0])) {
      ControlloFiltriException exception = new ControlloFiltriException(
          ControlloFiltriException.CODICE_ERRORE_ELEMENTO_INIZIALE);
      throw exception;
      }
    }

    for (i = 0; filtro != null && i < filtro.length - 1; i++) {
      if (PARENTESI_APERTA.equalsIgnoreCase(filtro[i])
          && !CheckPostParentesiAperta(filtro[i + 1])) {
        ControlloFiltriException exception = new ControlloFiltriException(
            ControlloFiltriException.CODICE_ERRORE_PARENTESI_APERTA);
        throw exception;
      }
      if (PARENTESI_CHIUSA.equalsIgnoreCase(filtro[i])
          && !CheckPostParentesiChiusa(filtro[i + 1])) {
        ControlloFiltriException exception = new ControlloFiltriException(
            ControlloFiltriException.CODICE_ERRORE_PARENTESI_CHIUSA);
        throw exception;
      }
      if ((OPERATORE_AND.equalsIgnoreCase(filtro[i]) || OPERATORE_OR.equalsIgnoreCase(filtro[i]))
          && !CheckPostOperatori(filtro[i + 1])) {
        ControlloFiltriException exception = new ControlloFiltriException(
            ControlloFiltriException.CODICE_ERRORE_OPERATORI);
        throw exception;
      }
      if (CONDIZIONE.equalsIgnoreCase(filtro[i])
          && !CheckPostCondizione(filtro[i + 1])) {
        ControlloFiltriException exception = new ControlloFiltriException(
            ControlloFiltriException.CODICE_ERRORE_CONDIZIONE);
        throw exception;
      }
      if (OPERATORE_NOT.equalsIgnoreCase(filtro[i])
          && !CheckPostOperatoreNot(filtro[i + 1])) {
        ControlloFiltriException exception = new ControlloFiltriException(
            ControlloFiltriException.CODICE_ERRORE_OPERATORE_NOT);
        throw exception;
      }
    }
    // controllo che l'ultimo elemento sia una parentesi chiusa oppure una
    // condizione
    if (i > 0) {
      if (!PARENTESI_CHIUSA.equalsIgnoreCase(filtro[i])
          && !CONDIZIONE.equalsIgnoreCase(filtro[i])) {
        ControlloFiltriException exception = new ControlloFiltriException(
            ControlloFiltriException.CODICE_ERRORE_ELEMENTO_FINALE);
        throw exception;
      }
    }

  }

  private static boolean CheckPostParentesiAperta(String elemento) {
    boolean esito = true;

    if (OPERATORE_NOT.equalsIgnoreCase(elemento)
        || PARENTESI_APERTA.equalsIgnoreCase(elemento)
        || CONDIZIONE.equalsIgnoreCase(elemento))
      esito = true;
    else
      esito = false;

    return esito;
  }

  private static boolean CheckPostParentesiChiusa(String elemento) {
    boolean esito = true;

    if (OPERATORE_AND.equalsIgnoreCase(elemento)
        || PARENTESI_CHIUSA.equalsIgnoreCase(elemento)
        || OPERATORE_OR.equalsIgnoreCase(elemento))
      esito = true;
    else
      esito = false;

    return esito;
  }

  private static boolean CheckPostOperatori(String elemento) {
    boolean esito = true;

    if (OPERATORE_NOT.equalsIgnoreCase(elemento)
        || PARENTESI_APERTA.equalsIgnoreCase(elemento)
        || CONDIZIONE.equalsIgnoreCase(elemento))
      esito = true;
    else
      esito = false;

    return esito;
  }

  private static boolean CheckPostCondizione(String elemento) {
    boolean esito = true;

    if (OPERATORE_AND.equalsIgnoreCase(elemento)
        || PARENTESI_CHIUSA.equalsIgnoreCase(elemento)
        || OPERATORE_OR.equalsIgnoreCase(elemento))
      esito = true;
    else
      esito = false;

    return esito;
  }

  private static boolean CheckPostOperatoreNot(String elemento) {
    boolean esito = true;

    if (PARENTESI_APERTA.equalsIgnoreCase(elemento)
        || CONDIZIONE.equalsIgnoreCase(elemento))
      esito = true;
    else
      esito = false;

    return esito;
  }

}