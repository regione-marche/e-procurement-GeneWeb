/*
 * Created on Jun 13, 2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.filtro;

import it.eldasoft.gene.web.struts.genric.filtro.ControlloFiltri;
import it.eldasoft.gene.web.struts.genric.filtro.ControlloFiltriException;
import it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.util.Vector;

import junit.framework.TestCase;

/*
 * Classe di test per la classe ControlloFiltri per le ricerche
 */

public class ControlloFiltriTest extends TestCase {

  FiltroRicercaForm filtro      = new FiltroRicercaForm();
  Vector<FiltroRicercaForm> listaFiltri = new Vector<FiltroRicercaForm>();

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    filtro.setAliasTabella("LAVO");
    listaFiltri.add(0, filtro);
    filtro = new FiltroRicercaForm();
    filtro.setOperatore("AND");
    listaFiltri.add(1, filtro);
    filtro = new FiltroRicercaForm();
    filtro.setOperatore("(");
    listaFiltri.add(2, filtro);
    filtro = new FiltroRicercaForm();
    filtro.setOperatore(")");
    listaFiltri.add(3, filtro);
    filtro = new FiltroRicercaForm();
    filtro.setAliasTabella("LAVO");
    listaFiltri.add(4, filtro);
    filtro = new FiltroRicercaForm();
    filtro.setOperatore("OR");
    listaFiltri.add(5, filtro);
    filtro = new FiltroRicercaForm();
    filtro.setOperatore("(");
    listaFiltri.add(6, filtro);
  }

  /*
   * @see TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public ControlloFiltriTest() {
    super();
  }

  public void testCreaStringaPerControlloListaVuota(){
    assertNull(ControlloFiltri.creaStringaPerControllo(new Vector<FiltroRicercaForm>()));
  }
  
  public void testCreaStringaPerControlloListaNull(){
    assertNull(ControlloFiltri.creaStringaPerControllo(null));
  }
  
  public void testCreaStringaPerControllo() {
    assertEquals(UtilityStringhe.serializza(new String[] { "CONDIZIONE", "AND",
        "(", ")", "CONDIZIONE", "OR", "(" }, 'X'), UtilityStringhe.serializza(
        ControlloFiltri.creaStringaPerControllo(listaFiltri), 'X'));
  }

  public void testCheckParentesiEccezioneParentesiAperteSbilanciate() {

    try {
      ControlloFiltri.checkParentesi("((ciao)");
      fail("mancata eccezione per parentesi aperte maggiori");
    } catch (ControlloFiltriException e) {
      assertEquals(
          ControlloFiltriException.CODICE_ERRORE_NUMERO_PARENTESI_APERTE,
          e.getCodiceErrore());
    }

    try {
      ControlloFiltri.checkFiltro(new String[] { "(", "condizione", "AND", "(",
          "condizione", ")" });
      fail("mancata eccezione per parentesi aperte maggiori");
    } catch (ControlloFiltriException e) {
      assertEquals(
          ControlloFiltriException.CODICE_ERRORE_NUMERO_PARENTESI_APERTE,
          e.getCodiceErrore());
    }

    try {
      ControlloFiltri.checkFiltro(new String[] { "condizione", "AND", "(", ")",
          "condizione", "AND", "(" });
      fail("mancata eccezione per parentesi aperte maggiori");
    } catch (ControlloFiltriException e) {
      assertEquals(
          ControlloFiltriException.CODICE_ERRORE_NUMERO_PARENTESI_APERTE,
          e.getCodiceErrore());
    }
  }

  public void testCheckParentesiEccezioneParentesiChiuseSbilanciate() {

    try {
      ControlloFiltri.checkParentesi("(ciao))");
      fail("mancata eccezione per parentesi chiuse maggiori");
    } catch (ControlloFiltriException e) {
      assertEquals(
          ControlloFiltriException.CODICE_ERRORE_NUMERO_PARENTESI_CHIUSE,
          e.getCodiceErrore());
    }

    try {
      ControlloFiltri.checkFiltro(new String[] { "(", "condizione", ")", "AND",
          "(", "NOT", "condizione", ")", "(" });
      fail("mancata eccezione per parentesi aperte maggiori ");
    } catch (ControlloFiltriException e) {
      assertEquals(
          ControlloFiltriException.CODICE_ERRORE_NUMERO_PARENTESI_APERTE,
          e.getCodiceErrore());
    }

  }

  public void testCheckFiltroEccezioneParentesiAperte() {

    try {
      ControlloFiltri.checkFiltro(new String[] { "(", "AND", "condizione", ")",
          "(", "condizione", ")" });
      fail("mancata eccezione per parentesi aperta dopo parentesi chiusa");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_PARENTESI_APERTA,
          e.getCodiceErrore());
    }

    try {
      ControlloFiltri.checkFiltro(new String[] { "(", "OR", "condizione", ")",
          "NOT", "(", "condizione", ")" });
      fail("mancata eccezione per parentesi aperta dopo operatore not");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_PARENTESI_APERTA,
          e.getCodiceErrore());
    }

    try {
      ControlloFiltri.checkFiltro(new String[] { "(", ")", "condizione",
          "NOT", "(", "condizione", ")" });
      fail("mancata eccezione per parentesi aperta dopo operatore not");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_PARENTESI_APERTA,
          e.getCodiceErrore());
    }
  }

  public void testCheckFiltroEccezioneParentesiChiuse() {

    try {
      ControlloFiltri.checkFiltro(new String[] { "(", "condizione", ")", "(",
          "condizione", ")" });
      fail("mancata eccezione per parentesi aperta dopo parentesi chiusa");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_PARENTESI_CHIUSA,
          e.getCodiceErrore());
    }

    try {
      ControlloFiltri.checkFiltro(new String[] { "(", "condizione", ")", "NOT",
          "(", "condizione", ")" });
      fail("mancata eccezione per parentesi aperta dopo operatore not");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_PARENTESI_CHIUSA,
          e.getCodiceErrore());
    }

    try {
      ControlloFiltri.checkFiltro(new String[] { "(", "condizione", ")", "NOT",
          "AND", "(", "condizione", ")" });
      fail("mancata eccezione per operatore AND dopo operatore not");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_PARENTESI_CHIUSA,
          e.getCodiceErrore());
    }

    try {
      ControlloFiltri.checkFiltro(new String[] { "(", "condizione", ")", "AND",
          "(", "NOT", "condizione", ")", "NOT" });
      fail("mancata eccezione per operatore not a fine filtro");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_PARENTESI_CHIUSA,
          e.getCodiceErrore());
    }

    try {
      ControlloFiltri.checkFiltro(new String[] { "(", "condizione", ")", "AND",
          "(", "NOT", "condizione", ")", "(", ")" });
      fail("mancata eccezione per parentesi aperta a fine filtro");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_PARENTESI_CHIUSA,
          e.getCodiceErrore());
    }
  }

  public void testCheckFiltroEccezioneElementoIniziale() {

    try {
      ControlloFiltri.checkFiltro(new String[] { "AND","(", "condizione", ")", "AND",
          "(", "NOT", "condizione", ")", "AND" });
      fail("mancata eccezione per operatore and a inizio filtro");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_ELEMENTO_INIZIALE,
          e.getCodiceErrore());
    }

    try {
      ControlloFiltri.checkFiltro(new String[] { "OR","(", "condizione", ")", "AND",
          "(", "NOT", "condizione", ")", "OR" });
      fail("mancata eccezione per operatore or a inizio filtro");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_ELEMENTO_INIZIALE,
          e.getCodiceErrore());
    }

  }
  
  public void testCheckFiltroEccezioneElementoFinale() {

    try {
      ControlloFiltri.checkFiltro(new String[] { "(", "condizione", ")", "AND",
          "(", "NOT", "condizione", ")", "AND" });
      fail("mancata eccezione per operatore not a fine filtro");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_ELEMENTO_FINALE,
          e.getCodiceErrore());
    }

    try {
      ControlloFiltri.checkFiltro(new String[] { "(", "condizione", ")", "AND",
          "(", "NOT", "condizione", ")", "OR" });
      fail("mancata eccezione per operatore not a fine filtro");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_ELEMENTO_FINALE,
          e.getCodiceErrore());
    }

  }

  public void testCheckFiltroEccezioneCondizione() {

    try {
      ControlloFiltri.checkFiltro(new String[] { "(", "condizione", "NOT", ")",
          "AND", "(", "condizione", ")" });
      fail("mancata eccezione per operatore not dopo condizione");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_CONDIZIONE,
          e.getCodiceErrore());
    }
    
    try {
      ControlloFiltri.checkFiltro(new String[] { "(", "condizione", "condizione", ")",
          "AND", "(", "condizione", ")" });
      fail("mancata eccezione per condizione dopo condizione");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_CONDIZIONE,
          e.getCodiceErrore());
    }
  }

  public void testCheckFiltroEccezioneOperatori() {

    try {
      ControlloFiltri.checkFiltro(new String[] { "(","condizione","AND",  ")", "(",
          "condizione", ")" });
      fail("mancata eccezione per parentesi chiusa dopo operatore and");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_OPERATORI,
          e.getCodiceErrore());
    }

    try {
      ControlloFiltri.checkFiltro(new String[] { "(","condizione","OR",  ")",
          "NOT", "(", "condizione", ")" });
      fail("mancata eccezione per parentesi chiusa dopo operatore or");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_OPERATORI,
          e.getCodiceErrore());
    }

    try {
      ControlloFiltri.checkFiltro(new String[] { "(", "condizione", ")", "AND", "AND",
          "NOT", "(", "condizione", ")" });
      fail("mancata eccezione per operatore and dopo operatore and");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_OPERATORI,
          e.getCodiceErrore());
    }
  }
  
  public void testCheckFiltroEccezioneOperatoreNot() {

    try {
      ControlloFiltri.checkFiltro(new String[] { "(","condizione",  ")","AND","NOT","AND", "(",
          "condizione", ")" });
      fail("mancata eccezione per operatore and dopo operatore not");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_OPERATORE_NOT,
          e.getCodiceErrore());
    }

    try {
      ControlloFiltri.checkFiltro(new String[] { "(","condizione","AND","NOT","OR",  ")",
          "NOT", "(", "condizione", ")" });
      fail("mancata eccezione per operatore OR dopo operatore not");
    } catch (ControlloFiltriException e) {
      assertEquals(ControlloFiltriException.CODICE_ERRORE_OPERATORE_NOT,
          e.getCodiceErrore());
    }

  }
}