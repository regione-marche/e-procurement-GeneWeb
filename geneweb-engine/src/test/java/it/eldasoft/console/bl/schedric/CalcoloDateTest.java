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
package it.eldasoft.console.bl.schedric;

import it.eldasoft.console.db.domain.schedric.DataSchedulazione;
import it.eldasoft.console.db.domain.schedric.SchedRic;
import it.eldasoft.utils.utility.UtilityDate;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class CalcoloDateTest extends TestCase {

  private SchedRic schedRicSettimanale;
  private SchedRic schedRicGiornaliero;
  private SchedRic schedRicUnico;
  private SchedRic schedRicMensile;
  /*
   * @see TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

//  unica esecuzione
    this.schedRicUnico = new SchedRic();
    this.schedRicUnico.setTipo("U");
    this.schedRicUnico.setDataPrimaEsec(UtilityDate.convertiData("15/06/2007 10:00:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    this.schedRicUnico.setOraAvvio(12);
    this.schedRicUnico.setMinutoAvvio(10);

//  giornaliero ogni giorno
    this.schedRicGiornaliero = new SchedRic();
    this.schedRicGiornaliero.setTipo("G");
    this.schedRicGiornaliero.setDataPrimaEsec(UtilityDate.convertiData("12/06/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    this.schedRicGiornaliero.setGiorno(new Integer(0));
    this.schedRicGiornaliero.setOraAvvio(15);
    this.schedRicGiornaliero.setMinutoAvvio(30);
    this.schedRicGiornaliero.setRipetiDopoMinuti(new Integer(0));

    //settimanale ogni settimana mercoledì venerdì
    this.schedRicSettimanale = new SchedRic();
    this.schedRicSettimanale.setTipo("S");
    this.schedRicSettimanale.setSettimana(new Integer(1));
    this.schedRicSettimanale.setGiorniSettimana("4|6|");
    this.schedRicSettimanale.setOraAvvio(15);
    this.schedRicSettimanale.setMinutoAvvio(30);

    //mensile ogni 5 del mese
    this.schedRicMensile = new SchedRic();
    this.schedRicMensile.setTipo("M");
    this.schedRicMensile.setMese("1|2|3|4|5|6|7|8|9|10|11|12|");
    this.schedRicMensile.setGiorniMese("5");
    this.schedRicMensile.setDataUltEsec(UtilityDate.convertiData("05/05/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    this.schedRicMensile.setOraAvvio(15);
    this.schedRicMensile.setMinutoAvvio(30);


  }

  /*
   * @see TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public CalcoloDateTest() {
    super();
  }

  public void testPianificazioneUnica() {
    GregorianCalendar risultatoAtteso = new GregorianCalendar(2007,5,14, 12, 10);
    GregorianCalendar risultatoOttenuto = CalcoloDate.pianificazioneUnica(new GregorianCalendar(2007,5,14), 12, 10);

    assertEquals(risultatoAtteso, risultatoOttenuto);
    assertEquals(12, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(10, risultatoOttenuto.get(Calendar.MINUTE));

    DataSchedulazione sched = CalcoloDate.calcolaDataProxEsec(this.schedRicUnico,UtilityDate.convertiData("19/06/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("15/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(12, sched.getOra());
    assertEquals(10, sched.getMinuti());
  }


  public void testPianificazioneGiornalieraOgniGiorno() {
    // test metodo nested: calcolo il giorno dopo perchè e' passato l'orario
    GregorianCalendar risultatoAtteso = new GregorianCalendar(2007,5,15,22,50);
    GregorianCalendar risultatoOttenuto = CalcoloDate.pianificazioneGiornaliera(new GregorianCalendar(2007,5,14,23,0),0,22,50,new Integer(0), null);
    assertEquals(UtilityDate.convertiData(risultatoAtteso.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.convertiData(risultatoOttenuto.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(22, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(50, risultatoOttenuto.get(Calendar.MINUTE));

    // test metodo nested: calcolo il giorno stesso (son passati anni dall'ultima esecuzione)
    risultatoAtteso = new GregorianCalendar(2011,7,5,22,50);
    risultatoOttenuto = CalcoloDate.pianificazioneGiornaliera(new GregorianCalendar(2011,7,5,10,0),0,22,50,new Integer(0), new GregorianCalendar(2007,5,15,22,50).getTime());
    assertEquals(UtilityDate.convertiData(risultatoAtteso.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.convertiData(risultatoOttenuto.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(22, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(50, risultatoOttenuto.get(Calendar.MINUTE));

    // test metodo nested: calcolo il giorno dopo perchè e' passato l'orario (son passati anni dall'ultima esecuzione)
    risultatoAtteso = new GregorianCalendar(2011,7,6,22,50);
    risultatoOttenuto = CalcoloDate.pianificazioneGiornaliera(new GregorianCalendar(2011,7,5,23,0),0,22,50,new Integer(0), new GregorianCalendar(2007,5,15,22,50).getTime());
    assertEquals(UtilityDate.convertiData(risultatoAtteso.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.convertiData(risultatoOttenuto.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(22, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(50, risultatoOttenuto.get(Calendar.MINUTE));

    // il giorno dopo la prima esecuzione prevista
    DataSchedulazione sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("13/06/2007 10:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("13/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

    // il giorno dopo la prima esecuzione prevista, ma è passata l'ora
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("13/06/2007 17:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("14/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

    this.schedRicGiornaliero.setDataUltEsec(UtilityDate.convertiData("14/06/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    // calcolo la prox esecuzione immediatamente dopo l'ultima esecuzione
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("14/06/2007 15:30:01",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("15/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    // ...o dopo un po'
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("14/06/2007 17:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("15/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    // calcolo il giorno dopo l'ultima esecuzione
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("15/06/2007 17:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("16/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    // ... e dopo molto tempo
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("31/12/2007 10:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("31/12/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("31/12/2007 17:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("01/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
  }

  public void testPianificazioneGiornalieraOgniGiornoOgniXMinuti() {
    // test metodo nested: calcolo il giorno dopo perchè e' passato l'orario
    GregorianCalendar risultatoAtteso = new GregorianCalendar(2007,5,15,22,50);
    GregorianCalendar risultatoOttenuto = CalcoloDate.pianificazioneGiornaliera(new GregorianCalendar(2007,5,14,23,0),0,22,50,new Integer(20), null);
    assertEquals(UtilityDate.convertiData(risultatoAtteso.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.convertiData(risultatoOttenuto.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(22, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(50, risultatoOttenuto.get(Calendar.MINUTE));

    // test metodo nested: calcolo l'intervallo successivo visto che un'esecuzione è stata appena fatta
    risultatoAtteso = new GregorianCalendar(2007,5,14,23,10);
    risultatoOttenuto = CalcoloDate.pianificazioneGiornaliera(new GregorianCalendar(2007,5,14,23,0),0,22,50,new Integer(20), new GregorianCalendar(2007,5,14,22,50).getTime());
    assertEquals(UtilityDate.convertiData(risultatoAtteso.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.convertiData(risultatoOttenuto.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(23, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(10, risultatoOttenuto.get(Calendar.MINUTE));

    // test metodo nested: calcolo il giorno stesso (son passati anni dall'ultima esecuzione)
    risultatoAtteso = new GregorianCalendar(2011,7,5,10,50);
    risultatoOttenuto = CalcoloDate.pianificazioneGiornaliera(new GregorianCalendar(2011,7,5,10,0),0,22,50,new Integer(60), new GregorianCalendar(2007,5,15,22,50).getTime());
    assertEquals(UtilityDate.convertiData(risultatoAtteso.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.convertiData(risultatoOttenuto.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(10, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(50, risultatoOttenuto.get(Calendar.MINUTE));

    // test metodo nested: calcolo il giorno dopo perchè sommando i minuti si va al giorno successivo
    risultatoAtteso = new GregorianCalendar(2011,7,6,2,50);
    risultatoOttenuto = CalcoloDate.pianificazioneGiornaliera(new GregorianCalendar(2011,7,5,23,0),0,22,50,new Integer(240), new GregorianCalendar(2007,5,15,22,50).getTime());
    assertEquals(UtilityDate.convertiData(risultatoAtteso.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.convertiData(risultatoOttenuto.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(2, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(50, risultatoOttenuto.get(Calendar.MINUTE));

    this.schedRicGiornaliero.setRipetiDopoMinuti(new Integer(120));

    // il giorno dopo la prima esecuzione prevista
    DataSchedulazione sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("13/06/2007 10:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("13/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

    // è passata l'ora, quindi schedulo la prima il giorno dopo
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("13/06/2007 17:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("14/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

    // è passata parecchio e la schedulazione va al giorno successivo
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("13/06/2007 23:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("14/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

    this.schedRicGiornaliero.setDataUltEsec(UtilityDate.convertiData("14/06/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

    // calcolo la prox esecuzione immediatamente dopo l'ultima esecuzione
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("14/06/2007 15:30:01",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("14/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(17, sched.getOra());
    assertEquals(30, sched.getMinuti());
    // ...o dopo un po'
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("14/06/2007 17:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("14/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(19, sched.getOra());
    assertEquals(30, sched.getMinuti());
    // calcolo il giorno dopo l'ultima esecuzione
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("15/06/2007 20:20:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("15/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(21, sched.getOra());
    assertEquals(30, sched.getMinuti());
    // ... e dopo molto tempo
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("31/12/2007 10:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("31/12/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(11, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("31/12/2007 17:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("31/12/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(19, sched.getOra());
    assertEquals(30, sched.getMinuti());
  }

  public void testPianificazioneGiornalieraOgniXGiorni() {

    // test metodo nested: calcolo il giorno dopo perchè e' passato l'orario
    GregorianCalendar risultatoAtteso = new GregorianCalendar(2007,5,15,22,50);
    GregorianCalendar risultatoOttenuto = CalcoloDate.pianificazioneGiornaliera(new GregorianCalendar(2007,5,14,23,0),3,22,50,new Integer(0), null);
    assertEquals(UtilityDate.convertiData(risultatoAtteso.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.convertiData(risultatoOttenuto.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(22, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(50, risultatoOttenuto.get(Calendar.MINUTE));

    // test metodo nested: calcolo il giorno stesso (son passati anni dall'ultima esecuzione)
    risultatoAtteso = new GregorianCalendar(2011,7,5,22,50);
    risultatoOttenuto = CalcoloDate.pianificazioneGiornaliera(new GregorianCalendar(2011,7,5,10,0),3,22,50,new Integer(0), new GregorianCalendar(2007,5,15,22,50).getTime());
    assertEquals(UtilityDate.convertiData(risultatoAtteso.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.convertiData(risultatoOttenuto.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(22, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(50, risultatoOttenuto.get(Calendar.MINUTE));

    // test metodo nested: calcolo il giorno dopo perchè e' passato l'orario (son passati anni dall'ultima esecuzione)
    risultatoAtteso = new GregorianCalendar(2011,7,6,22,50);
    risultatoOttenuto = CalcoloDate.pianificazioneGiornaliera(new GregorianCalendar(2011,7,5,23,0),3,22,50,new Integer(0), new GregorianCalendar(2007,5,15,22,50).getTime());
    assertEquals(UtilityDate.convertiData(risultatoAtteso.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.convertiData(risultatoOttenuto.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(22, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(50, risultatoOttenuto.get(Calendar.MINUTE));

    //pianificazione ogni 5 giorni (mai eseguita)
    this.schedRicGiornaliero.setGiorno(new Integer(5));
    this.schedRicGiornaliero.setDataUltEsec(null);
    // la prima esecuzione
    DataSchedulazione sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("12/06/2007 10:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("12/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    // la prima esecuzione non e' partita, ricalcolo
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("12/06/2007 19:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("13/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    // si setta quindi la data ultima esecuzione
    this.schedRicGiornaliero.setDataUltEsec(UtilityDate.convertiData("12/06/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    // ricalcolo subito dopo la prima esecuzione
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("12/06/2007 15:30:01",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("17/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    // setto la nuova data ultima esecuzione
    this.schedRicGiornaliero.setDataUltEsec(UtilityDate.convertiData("17/06/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    // ricalcolo subito dopo la prossima data
    Date d = UtilityDate.convertiData("17/06/2007 17:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
    d.setTime(d.getTime()+1); // aggiungo una frazione di secondo per dire che e' trascorso del tempo dall'ultima esecuzione
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,d);
    assertEquals(UtilityDate.convertiData("22/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    // calcolo dopo un po' (rimane la stessa data)
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("17/06/2007 17:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("22/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("21/06/2007 17:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("22/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("22/06/2007 15:29:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("22/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    // passata la data prossima esecuzione si riparte dal giorno successivo
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("22/06/2007 15:31:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("23/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    // nuova data ultima esecuzione
    this.schedRicGiornaliero.setDataUltEsec(UtilityDate.convertiData("14/06/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    // immediatamente dopo ricalcolo sempre 5 gg dopo
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("14/06/2007 15:30:01",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("19/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("14/06/2007 17:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("19/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("15/06/2007 17:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("19/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    // dopo un po' schedulo per il giorno stesso o il successivo a seconda dell'orario
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("31/12/2007 10:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("31/12/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicGiornaliero,UtilityDate.convertiData("31/12/2007 17:45:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("01/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
  }


  public void testPianificazioneSettimanale() {
    GregorianCalendar risultatoAtteso = new GregorianCalendar(2007,5,15,15,30);
    GregorianCalendar risultatoOttenuto = CalcoloDate.pianificazioneSettimanale("3|6",new GregorianCalendar(2007,5,14,16,37),1,15,30);

    assertEquals(UtilityDate.convertiData(risultatoAtteso.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),UtilityDate.convertiData(risultatoOttenuto.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(15, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(30, risultatoOttenuto.get(Calendar.MINUTE));

    DataSchedulazione sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("10/06/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("13/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("13/06/2007 22:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("15/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

    this.schedRicSettimanale.setDataUltEsec(UtilityDate.convertiData("13/06/2007 15:30:00", UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("10/06/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("15/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

    this.schedRicSettimanale.setDataUltEsec(UtilityDate.convertiData("15/06/2007 15:30:00", UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("10/06/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("20/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("11/07/2007 10:00:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("11/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("11/07/2007 16:00:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("13/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("10/07/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("11/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("05/01/2008",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("09/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
  }

  public void testPianificazioneSettimanaleOgniXSettimane() {
    this.schedRicSettimanale.setSettimana(new Integer(3));

    GregorianCalendar risultatoAtteso = new GregorianCalendar(2007,5,15,15,30);
    GregorianCalendar risultatoOttenuto = CalcoloDate.pianificazioneSettimanale("3|6",new GregorianCalendar(2007,5,14,16,37),3,15,30);

    assertEquals(UtilityDate.convertiData(risultatoAtteso.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),UtilityDate.convertiData(risultatoOttenuto.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(15, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(30, risultatoOttenuto.get(Calendar.MINUTE));

    risultatoAtteso = new GregorianCalendar(2007,6,3,15,30);
    risultatoOttenuto = CalcoloDate.pianificazioneSettimanale("3|6",new GregorianCalendar(2007,5,15,16,37),3,15,30);

    assertEquals(UtilityDate.convertiData(risultatoAtteso.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),UtilityDate.convertiData(risultatoOttenuto.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(15, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(30, risultatoOttenuto.get(Calendar.MINUTE));

    //passo ad una pianificazione ogni 3 settimane (data prima esecuzione 11/06/07)
    this.schedRicSettimanale.setDataUltEsec(UtilityDate.convertiData("01/06/2007 15:30:00", UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

    DataSchedulazione sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("10/06/2007 10:00:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("27/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("13/06/2007 22:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("15/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

    this.schedRicSettimanale.setDataUltEsec(UtilityDate.convertiData("13/06/2007 15:30:00", UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("10/06/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("15/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("16/06/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("04/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("05/01/2008",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("23/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

    this.schedRicSettimanale.setDataUltEsec(UtilityDate.convertiData("15/06/2007 15:30:00", UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("10/06/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("04/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("11/07/2007 10:00:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("11/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("11/07/2007 16:00:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("13/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("10/07/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("11/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicSettimanale,UtilityDate.convertiData("05/01/2008",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("23/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

  }

  public void testPianificazioneMensile() {
    GregorianCalendar risultatoAtteso = new GregorianCalendar(2007,5,22,14,50);
    GregorianCalendar risultatoOttenuto = CalcoloDate.pianificazioneMensile(new GregorianCalendar(2007,5,16,15,0),0,4,6,"1|2|3|4|5|6|7|8|9|10|11|12|",14,50);

    assertEquals(UtilityDate.convertiData(risultatoAtteso.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),UtilityDate.convertiData(risultatoOttenuto.getTime(),UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(14, risultatoOttenuto.get(Calendar.HOUR_OF_DAY));
    assertEquals(50, risultatoOttenuto.get(Calendar.MINUTE));

    DataSchedulazione sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("25/06/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("05/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("05/06/2007 10:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("05/06/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("05/06/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("05/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("31/12/2007 10:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("05/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("05/12/2007 10:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("05/12/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("05/12/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("05/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

    this.schedRicMensile.setDataUltEsec(UtilityDate.convertiData("05/06/2007 15:30:00", UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("25/06/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("05/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("05/06/2007 10:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("05/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("05/06/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("05/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

    this.schedRicMensile.setDataUltEsec(UtilityDate.convertiData("05/12/2007 15:30:00", UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("25/12/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("05/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("05/12/2007 10:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("05/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("05/12/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("05/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

    //passo ad una pianificazione ogni terzo lunedì del mese
    this.schedRicMensile.setSettimana(new Integer(3));
    this.schedRicMensile.setGiorniSettimana("2");
    this.schedRicMensile.setGiorniMese(null);
    this.schedRicMensile.setDataUltEsec(UtilityDate.convertiData("18/06/2007 15:30:00", UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("25/06/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("16/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("18/06/2007 10:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("16/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("18/06/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("16/07/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("20/12/2007 10:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("21/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("17/12/2007 10:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("17/12/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("17/12/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("21/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

    this.schedRicMensile.setDataUltEsec(UtilityDate.convertiData("19/11/2007 15:30:00", UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("25/12/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("21/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("17/12/2007 10:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("17/12/2007",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("17/12/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("21/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

    this.schedRicMensile.setDataUltEsec(UtilityDate.convertiData("17/12/2007 15:30:00", UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("25/12/2007",UtilityDate.FORMATO_GG_MM_AAAA));
    assertEquals(UtilityDate.convertiData("21/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("17/12/2007 10:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("21/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());
    sched = CalcoloDate.calcolaDataProxEsec(this.schedRicMensile,UtilityDate.convertiData("17/12/2007 15:30:00",UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    assertEquals(UtilityDate.convertiData("21/01/2008",UtilityDate.FORMATO_GG_MM_AAAA),sched.getData());
    assertEquals(15, sched.getOra());
    assertEquals(30, sched.getMinuti());

  }


}
