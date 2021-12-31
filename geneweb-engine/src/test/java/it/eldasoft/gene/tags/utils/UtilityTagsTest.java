/*
 * Created on 01/set/2020
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.tags.utils;

import junit.framework.TestCase;

/**
 * @author stefano.sabbadin
 */
public class UtilityTagsTest extends TestCase {

  /**
   * Se si passa un parametro null la funzione non controlla nulla e ritorna true.
   */
  public void testIsFreeFromSqlInjection_ParametroNull() {
    assertTrue(UtilityTags.isFreeFromSqlInjection(null));
  }

  /**
   * Se si passa una stringa vuota o contenente solo spazi, la funzione non controlla nulla e ritorna true.
   */
  public void testIsFreeFromSqlInjection_ParametroVuoto() {
    assertTrue(UtilityTags.isFreeFromSqlInjection(""));
    assertTrue(UtilityTags.isFreeFromSqlInjection("\t"));
    assertTrue(UtilityTags.isFreeFromSqlInjection("  "));
  }

  /**
   * Si verificano i casi realmente costruiti come condizioni applicative passate come input da usare come condizione SQL.
   */
  public void testIsFreeFromSqlInjection_CasiRealiOK() {
    // Appalti
    assertTrue(UtilityTags.isFreeFromSqlInjection("(V_GARE_TORN.ISARCHI <> ? OR V_GARE_TORN.ISARCHI IS NULL) AND (Upper( V_GARE_TORN.CODICE ) like ? OR Upper(V_GARE_TORN.OGGETTO ) like ? OR Upper(V_GARE_TORN.CODCIG) like ?) AND V_GARE_TORN.TIPGAR IN(1,2,3,5,12,26,13,27,28,107)"));
    assertTrue(UtilityTags.isFreeFromSqlInjection("(  IMPR.CODIMP NOT IN (select DESCODSOG from w_invcomdes where IDPRG = 'PG' and IDCOM = 2644 AND DESCODENT='IMPR' AND DESCODSOG is not null) and (IMPR.TIPIMP is null or (IMPR.TIPIMP <> 3 and IMPR.TIPIMP <> 10)) ) and IMPR.NOMEST like ? "));
    assertTrue(UtilityTags.isFreeFromSqlInjection("W_LOGEVENTI.OGGEVENTO like ? "));
    assertTrue(UtilityTags.isFreeFromSqlInjection("W_LOGEVENTI.CODEVENTO = ? "));
    assertTrue(UtilityTags.isFreeFromSqlInjection("(V_GARE_ELEDITTE.ISARCHI <> ? OR V_GARE_ELEDITTE.ISARCHI IS NULL) AND (Upper( V_GARE_ELEDITTE.CODICE ) like ? OR Upper(V_GARE_ELEDITTE.OGGETTO ) like ?)"));
    assertTrue(UtilityTags.isFreeFromSqlInjection("(GAREAVVISI.ISARCHI <> ? OR GAREAVVISI.ISARCHI IS NULL)"));
    assertTrue(UtilityTags.isFreeFromSqlInjection("(MERIC.ISARCHI <> ? OR MERIC.ISARCHI IS NULL)"));
    assertTrue(UtilityTags.isFreeFromSqlInjection("(v_gare_torn.isarchi <> ? or v_gare_torn.isarchi is null) and v_gare_torn.tipgar in(30,101,105) and v_gare_torn.genere <>1  and v_gare_torn.genere <>3 "));
    assertTrue(UtilityTags.isFreeFromSqlInjection("UPPER( UFFINT.NOMEIN ) like ? and (UFFINT.DATFIN IS NULL)"));
    assertTrue(UtilityTags.isFreeFromSqlInjection("DITG.NGARA5='G00034' AND DITG.CODGAR5='$G00034' AND(DITG.INVOFF <> '2' or DITG.INVOFF is null) AND (DITG.AMMGAR <> '2' or DITG.AMMGAR is null)"));
    assertTrue(UtilityTags.isFreeFromSqlInjection("UPPER( DITG.NOMIMO ) like ? and (DITG.NGARA5='G00034' AND DITG.CODGAR5='$G00034' AND(DITG.INVOFF <> '2' or DITG.INVOFF is null) AND (DITG.AMMGAR <> '2' or DITG.AMMGAR is null)) "));
    assertTrue(UtilityTags.isFreeFromSqlInjection("(V_GARE_TORN.ISARCHI <> ? OR V_GARE_TORN.ISARCHI IS NULL) AND V_GARE_TORN.TIPGAR IN(20,21,22,25) AND V_GARE_TORN.GENERE <>1  AND V_GARE_TORN.GENERE <>3 "));
    // LFS
    assertTrue(UtilityTags.isFreeFromSqlInjection("(PERI.ISARCHI <> ? OR PERI.ISARCHI IS NULL)"));
    assertTrue(UtilityTags.isFreeFromSqlInjection("(PERI.ISARCHI <> ? OR PERI.ISARCHI IS NULL) AND EXISTS (SELECT G_PERMESSI.NUMPER FROM G_PERMESSI WHERE (G_PERMESSI.SYSCON = 48 AND G_PERMESSI.CODLAV = PERI.CODLAV))"));
    assertTrue(UtilityTags.isFreeFromSqlInjection("UPPER(V_TAB4_TAB6.TAB46TIP ) like ? "));
    // DL229
    assertTrue(UtilityTags.isFreeFromSqlInjection("(PROG_ANAG.PROGETTO_ARCHIVIATO <> ? OR PROG_ANAG.PROGETTO_ARCHIVIATO IS NULL)"));
    assertTrue(UtilityTags.isFreeFromSqlInjection("(PROG_ANAG.PROGETTO_ARCHIVIATO <> ? OR PROG_ANAG.PROGETTO_ARCHIVIATO IS NULL) AND (UPPER( PROG_ANAG.CUP ) like ? OR UPPER(PROG_ANAG.TITOLO) like ? ) "));
    assertTrue(UtilityTags.isFreeFromSqlInjection("UPPER(PROG_ANAG.TITOLO ) like ? and ( PROG_ANAG.PROGETTO_ARCHIVIATO = ? or PROG_ANAG.PROGETTO_ARCHIVIATO = ? or PROG_ANAG.PROGETTO_ARCHIVIATO is null ) "));
    assertTrue(UtilityTags.isFreeFromSqlInjection("( PROG_ANAG.PROGETTO_ARCHIVIATO = ? or PROG_ANAG.PROGETTO_ARCHIVIATO = ? or PROG_ANAG.PROGETTO_ARCHIVIATO is null ) "));
  }

  public void testIsFreeFromSqlInjection_KOTautology() {
    // tautologia in coda
    assertFalse(UtilityTags.isFreeFromSqlInjection("w_logeventi.oggevento like ? or 1=1"));
    assertFalse(UtilityTags.isFreeFromSqlInjection("W_LOGEVENTI.OGGEVENTO like ? OR 123 = 123"));
    assertFalse(UtilityTags.isFreeFromSqlInjection("W_LOGEVENTI.OGGEVENTO like ? OR 123 =\t123"));
    // tautologia in testa
    assertFalse(UtilityTags.isFreeFromSqlInjection("W_LOGEVENTI.OGGEVENTO = W_LOGEVENTI.OGGEVENTO OR W_LOGEVENTI.OGGEVENTO like ?"));
    assertFalse(UtilityTags.isFreeFromSqlInjection("W_LOGEVENTI.OGGEVENTO=W_LOGEVENTI.OGGEVENTO OR W_LOGEVENTI.OGGEVENTO like ?"));
  }

  public void testIsFreeFromSqlInjection_KOEndOfLineComment() {
    assertFalse(UtilityTags.isFreeFromSqlInjection("1=1 --w_logeventi.oggevento like ?"));
  }

  public void testIsFreeFromSqlInjection_KOPiggyBackedQuery() {
    assertFalse(UtilityTags.isFreeFromSqlInjection("W_LOGEVENTI.OGGEVENTO like ? and 1=0; SELECT FAKE FROM TABLE"));
  }

  public void testIsFreeFromSqlInjection_KOUnionQuery() {
    assertFalse(UtilityTags.isFreeFromSqlInjection("W_LOGEVENTI.OGGEVENTO like ? AND 1=0 union SELECT FAKE FROM TABLE"));
    assertFalse(UtilityTags.isFreeFromSqlInjection("W_LOGEVENTI.OGGEVENTO like ? AND 1=0 UNION\tSELECT FAKE FROM TABLE"));
  }

  public void testIsFreeFromSqlInjection_KOInfoDBMSVersion() {
    // si testa se e' Oracle versione 12.1.0.2.0
    assertFalse(UtilityTags.isFreeFromSqlInjection(" codapp='PG' and  exists (select banner from v$version where banner like '%12.1.0.2.0%')"));
    // si testa se e' SQL Server 2012
    assertFalse(UtilityTags.isFreeFromSqlInjection("UPPER(V_TAB4_TAB6.TAB46TIP ) like ? and SUBSTRING(@@version,22,4) = '2012'"));
    // si testa se e' Postgres 9.5.4
    assertFalse(UtilityTags.isFreeFromSqlInjection("(MERIC.ISARCHI <> ? OR MERIC.ISARCHI IS NULL) and SUBSTRING(version(),12,5) = '9.5.4'"));
  }

}
