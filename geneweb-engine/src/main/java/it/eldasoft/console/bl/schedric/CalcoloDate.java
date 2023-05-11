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
package it.eldasoft.console.bl.schedric;

import it.eldasoft.console.db.domain.schedric.DataSchedulazione;
import it.eldasoft.console.db.domain.schedric.SchedRic;
import it.eldasoft.console.web.struts.schedric.CostantiSchedRic;
import it.eldasoft.utils.utility.UtilityDate;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Raccoglitore di funzioni per il calcolo delle date legate
 * alla schedulazione delle ricerche
 *
 * @author Francesco De Filippis
 */
public class CalcoloDate {


  /**
   * Funzione che calcola la data prossima esecuzione della schedulazione
   *
   * @param schedRic dati della schedulazione della ricerca
   * @param dataAlternativa data di partenza alternativa alla data prima esecuzione
   *        da valorizzare con la data di modifica/inserimento/attivazione
   * @return
   */
  public static DataSchedulazione calcolaDataProxEsec(SchedRic schedRic,Date dataAlternativa) {

    GregorianCalendar dataDiPartenza = new GregorianCalendar();
    Date dataPrimaEsec = schedRic.getDataPrimaEsec();
    Date dataUltEsec = schedRic.getDataUltEsec();
    String tipo = schedRic.getTipo();
    int ore = schedRic.getOraAvvio();
    int minuti = schedRic.getMinutoAvvio();
    GregorianCalendar dataEsec = new GregorianCalendar();
    //F.D. 15/06/07 prima versione
    //scelgo la data di partenza utilizzando come ordine la data ultima esecuzione
    //per prima, la data prima esecuzione come seconda e la data odierna per ultima
    //la scelta deriva dal fatto che le funzione viene usata in vari momenti
    //se siamo in modifica logicamente la data da cui partire è quella odierna
    //se siamo in inserimento partiamo dalla data prima esecuzione se c'è se no
    //dalla data odierna
    //se stiamo attivando una schedulazione dobbiamo partire dalla data odierna come
    //per la modifica
    //quando la schedulazione viene passata in coda ed eseguito il report, la data prox
    //esec viene ricalcolata in base alla data ultima esecuzione che però è valorizzata
    //con la data odierna in quanto si è appena eseguito il report
    /*if (dataUltEsec != null)
      dataDiPartenza.setTime(dataUltEsec);
    else {
      if (dataPrimaEsec!= null)
        dataDiPartenza.setTime(dataPrimaEsec);
      else
        dataDiPartenza = new GregorianCalendar();
    }
     */
    //F.D. 16/06/07 seconda versione
    //utlizzando la funzione di calcolo pure durante l'attivazione delle schedulazioni
    //la scelta della data di partenza per il calcolo varia.
    //nel caso di esecuzione unica la data di partenza risulta essere sempre la dataPrimaEsec
    //scelta dall'utente in fase di input
    //nel caso di schedulazione giornaliera se la data ultima esecuzione è valorizzata andiamo
    //a sceglierela data di partenza prendendo il massimo valore fra quest'ultima e la data odierna
    //per evitare di ottenere una data di prossima esecuzione antecedente alla data di calcolo,
    //nel caso in cui non sia valorizzata la data ultima esecuzione (siamo nel caso di attivazione o di
    //primo calcolo) scegliamo la data di partenza prendendo il massimo valore fra la data prima esecuzione
    //scelta dall'utente e la data odierna anche qui per evitare calcoli con date antecedenti
    //per le schedulazioni settimanali e mensili se la data ultima esecuzione è nulla utilizziamo
    //la data odierna (siamo nel caso di primo calcolo o di attivazione) oppure il massimo fra
    //la data odierna e la data ultima esecuzione in modo anche qui di evitare di ottenere date di
    //prossima esecuzione antecedenti alla data di calcolo

    //tipo di schedulazione Unica
    //la data di esecuzione corrisponde a quella inserita dall'utente
    if (CostantiSchedRic.UNICA.equalsIgnoreCase(tipo)) {
      dataDiPartenza.setTime(dataPrimaEsec);
      dataEsec = pianificazioneUnica(dataDiPartenza, ore, minuti);
    }

    //tipo di schedulazione Giornaliera
    //partendo dalla dataDiPartenza aggiungo i giorni pianificati
    //in modo da ottenere la nuova data
    if (CostantiSchedRic.GIORNO.equalsIgnoreCase(tipo)) {
      int giorno = schedRic.getGiorno().intValue();

      if (dataUltEsec != null)
        dataDiPartenza.setTime(max(dataUltEsec,dataAlternativa));
      else
        dataDiPartenza.setTime(max(dataPrimaEsec,dataAlternativa));

      dataEsec = pianificazioneGiornaliera(dataDiPartenza,giorno,ore,minuti,schedRic.getRipetiDopoMinuti(), dataUltEsec);

    }

    //tipo di schedulazione Settimanale
    //vado a trovare il giorno della prox esecuzione sommando al giorno di partenza i giorni pianificati
    //stando attento che se il giorno è stato superato ("oggi" è mercoledì e la pianificazione è per martedì)
    //devo sommare i giorni per concludere la settimana
    if (CostantiSchedRic.SETTIMANA.equalsIgnoreCase(tipo)) {
      int settimana = schedRic.getSettimana().intValue();
      String giorniSettimana = schedRic.getGiorniSettimana();

      if (dataUltEsec != null)
        dataDiPartenza.setTime(max(dataAlternativa,dataUltEsec));
      else
        dataDiPartenza.setTime(dataAlternativa);
      dataEsec = pianificazioneSettimanale(giorniSettimana,dataDiPartenza,settimana,ore,minuti);

    }

    //tipo di schedulazione Mensile
    //utilizzo le potenzialità di GregorianCalendar sfruttando giorni della settimana
    //giorni del mese e giorni della settimana nel mese
    if (CostantiSchedRic.MESE.equalsIgnoreCase(tipo)) {
      int giorniMese = 0;
      if (schedRic.getGiorniMese()!= null)
        giorniMese = (new Integer(schedRic.getGiorniMese())).intValue();
      int settimana = 0;
      if (schedRic.getSettimana()!= null)
        settimana = schedRic.getSettimana().intValue();
      int giorniSettimana = 0;
      if (schedRic.getGiorniSettimana()!= null)
        giorniSettimana = (new Integer(schedRic.getGiorniSettimana())).intValue();
      String mesi = schedRic.getMese();

      if (dataUltEsec != null)
        dataDiPartenza.setTime(max(dataAlternativa,dataUltEsec));
      else
        dataDiPartenza.setTime(dataAlternativa);

      dataEsec = pianificazioneMensile(dataDiPartenza,giorniMese,settimana,giorniSettimana,mesi,ore,minuti);

    }

    //converto la data in stringa e poi la riconverto in data per azzerare ore minuti e secondi
    String strDataEsecuzione = UtilityDate.convertiData(dataEsec.getTime(), UtilityDate.FORMATO_GG_MM_AAAA);

    DataSchedulazione dataSchedulazione = new DataSchedulazione();
    dataSchedulazione.setData(UtilityDate.convertiData(strDataEsecuzione,UtilityDate.FORMATO_GG_MM_AAAA));
    dataSchedulazione.setOra(dataEsec.get(Calendar.HOUR_OF_DAY));
    dataSchedulazione.setMinuti(dataEsec.get(Calendar.MINUTE));

    return dataSchedulazione;
  }

  static Date max(Date dataUno,Date dataDue) {
    if (dataUno.getTime() > dataDue.getTime())
      return dataUno;
    else
      return dataDue;
  }

  static GregorianCalendar pianificazioneUnica(GregorianCalendar dataDiPartenza, int ore, int minuti) {
    dataDiPartenza.set(Calendar.HOUR_OF_DAY, ore);
    dataDiPartenza.set(Calendar.MINUTE, minuti);
    return dataDiPartenza;
  }

  static GregorianCalendar pianificazioneGiornaliera(GregorianCalendar dataDiPartenza, int giorno, int ore, int minuti, Integer intervallo,
      Date dataUltimaEsec) {
    GregorianCalendar dataEsec = null;
    // serve per calcolare il passaggio al mese successivo
    int giorniPianificazione = dataDiPartenza.get(Calendar.DAY_OF_MONTH);// + giorno;
    // si calcola la data esecuzione inizialmente come data di partenza con ore e minuti previsti
    dataEsec = new GregorianCalendar(dataDiPartenza.get(Calendar.YEAR), dataDiPartenza.get(Calendar.MONTH), giorniPianificazione, ore,
        minuti);

    // se la data calcolata è inferiore alla data di partenza, va ricalcolata aggiungendo i giorni o i minuti previsti dalla configurazione

    if (giorno == 0) {
      // esecuzione ogni giorno

      if (intervallo.intValue() == 0) {
        // pianificazione ogni giorno una volta al giorno
        if (dataEsec.getTimeInMillis() < dataDiPartenza.getTimeInMillis()) {
          // prendo il giorno successivo
          giorniPianificazione++;
          dataEsec = new GregorianCalendar(dataDiPartenza.get(Calendar.YEAR), dataDiPartenza.get(Calendar.MONTH), giorniPianificazione,
              ore, minuti);
        }
      } else {
        if (dataUltimaEsec == null) {
          // dobbiamo eseguirlo la prima volta, si considera la prima esecuzione a partire dalle ore minuti specificati in configurazione
          if (dataEsec.getTimeInMillis() < dataDiPartenza.getTimeInMillis()) {
            // prendo il giorno successivo
            giorniPianificazione++;
            dataEsec = new GregorianCalendar(dataDiPartenza.get(Calendar.YEAR), dataDiPartenza.get(Calendar.MONTH), giorniPianificazione,
                ore, minuti);
          }
        } else {
          // se c'è stata un'ultima esecuzione, riprendo considerando la cadenza prevista dall'intervallo di ripetizione giornaliera
          // partendo dall'ipotetica data esecuzione calcolata cercando la piu' piccola ricorrenza
          // maggiore della data di partenza
          if (dataEsec.getTimeInMillis() > dataDiPartenza.getTimeInMillis()) {
            while (dataEsec.getTimeInMillis() > dataDiPartenza.getTimeInMillis()) {
              dataEsec.add(Calendar.MINUTE, -intervallo.intValue());
            }
            dataEsec.add(Calendar.MINUTE, intervallo.intValue());
          } else {
            while (dataEsec.getTimeInMillis() < dataDiPartenza.getTimeInMillis()) {
              dataEsec.add(Calendar.MINUTE, intervallo.intValue());
            }
          }
        }
      }
    } else {
      // pianificazione ogni x giorni

      if (dataEsec.getTimeInMillis() < dataDiPartenza.getTimeInMillis()) {
        // guardo se la data ultima esecuzione esiste. se non esiste, sarà il
        // giorno successivo, altrimenti provo ad aggiungere gli x giorni alla
        // data ultima esecuzione, e se ottengo una data successiva alla data di
        // partenza allora uso questa come prossima schedulazione, altrimenti
        // uso il giorno successivo (perchè vuol dire che la data ultima
        // esecuzione è tanto indietro nel tempo, e se sommo i giorni previsti
        // ottengo comunque una nuova data inferiore alla data di partenza)
        if (dataUltimaEsec == null) {
          // prendo il giorno successivo
          giorniPianificazione++;
          dataEsec = new GregorianCalendar(dataDiPartenza.get(Calendar.YEAR), dataDiPartenza.get(Calendar.MONTH), giorniPianificazione,
              ore, minuti);
        } else {
          GregorianCalendar dataProx = new GregorianCalendar();
          dataProx.setTime(dataUltimaEsec);
          // aggiorno la data con l'orario di esecuzione
          dataProx.set(Calendar.HOUR_OF_DAY, ore);
          dataProx.set(Calendar.MINUTE, minuti);
          // aggiungo all'ultima esecuzione x giorni
          dataProx.add(Calendar.DAY_OF_MONTH, giorno);
          if (dataProx.getTimeInMillis() < dataDiPartenza.getTimeInMillis()) {
            // prendo il giorno successivo
            giorniPianificazione++;
            dataEsec = new GregorianCalendar(dataDiPartenza.get(Calendar.YEAR), dataDiPartenza.get(Calendar.MONTH), giorniPianificazione,
                ore, minuti);
          } else
            dataEsec = dataProx;
        }
      }
    }
    return dataEsec;
  }

  static GregorianCalendar pianificazioneSettimanale(String giorniSettimana,GregorianCalendar dataDiPartenza,int settimane,int ore,int minuti) {
    GregorianCalendar dataEsec = null;
    String[] listaGiorniSettimana = null;
    if (giorniSettimana != null && giorniSettimana.indexOf("|") > 0 )
      listaGiorniSettimana = giorniSettimana.split("\\|");
    else
      listaGiorniSettimana = new String[] {giorniSettimana};
    //creo la lista delle date utili da confrontare con la data di partenza per trovare la prox esecuzione
    GregorianCalendar[] listaDate = new GregorianCalendar[listaGiorniSettimana.length];
    GregorianCalendar data = null;

    int settimanaAttuale = 0;
    int i;
    int dayOfWeek=0;
    int weekInMonth = 0;
    for (i=0;i<listaGiorniSettimana.length;i++) {

      dayOfWeek = (new Integer(listaGiorniSettimana[i])).intValue();

      settimanaAttuale = dataDiPartenza.get(Calendar.WEEK_OF_MONTH);
      weekInMonth = settimanaAttuale;

      data = new GregorianCalendar(dataDiPartenza.get(Calendar.YEAR),dataDiPartenza.get(Calendar.MONTH),1,ore,minuti);
      data.set(Calendar.WEEK_OF_MONTH,weekInMonth);
      data.set(Calendar.DAY_OF_WEEK,dayOfWeek);


      listaDate[i] = data;

    }
    //se non trovo la data fra quelle della lista cerco x settimane dopo
    //non ciclerà mai per più di una settimana
    while (dataEsec == null ) {
      for (i=0;i<listaDate.length;i++) {
        if (listaDate[i].getTimeInMillis() > dataDiPartenza.getTimeInMillis()){
          dataEsec = listaDate[i];
          break;
        }
      }
      if (dataEsec == null) {
        for (i=0;i<listaDate.length;i++) {
          // si aggiungono a tutte le date della settimane le x settimane della prox schedulazione
          data = new GregorianCalendar(listaDate[i].get(Calendar.YEAR), listaDate[i].get(Calendar.MONTH), listaDate[i].get(Calendar.DAY_OF_MONTH),ore,minuti);
          settimanaAttuale = data.get(Calendar.WEEK_OF_MONTH);
          weekInMonth = settimanaAttuale + settimane;
          data.set(Calendar.WEEK_OF_MONTH,weekInMonth);
          listaDate[i] = data;
        }
      }

    }

    return dataEsec;
  }

  static GregorianCalendar pianificazioneMensile(GregorianCalendar dataDiPartenza,int giorniMese,int settimana,int giorniSettimana,String mesi,int ore,int minuti) {
    GregorianCalendar dataEsec = null;
    GregorianCalendar data = null;
    GregorianCalendar dataAnnoSuccessivo = null;


    String[] listaMesi = null;
    if (mesi != null && mesi.indexOf("|") > 0 )
      listaMesi = mesi.split("\\|");
    //setto i mesi e creo la lista in base al giorno settato precedentemente
    //e alla lista dei mesi che mi arriva
    GregorianCalendar[] listaDate = new GregorianCalendar[listaMesi.length];

    int i = 0;
    for (i=0;i<listaMesi.length;i++) {

      int mese = (new Integer(listaMesi[i])).intValue()-1;
      //parto dal primo del mese (dell'anno corrente e dell'anno successivo)
      data = new GregorianCalendar(dataDiPartenza.get(Calendar.YEAR),mese,1,ore,minuti);
      dataAnnoSuccessivo = new GregorianCalendar(dataDiPartenza.get(Calendar.YEAR)+1,mese,1,ore,minuti);

      if (giorniMese == 0) {
        data.set(Calendar.DAY_OF_WEEK_IN_MONTH,settimana);
        data.set(Calendar.DAY_OF_WEEK,giorniSettimana);
        dataAnnoSuccessivo.set(Calendar.DAY_OF_WEEK_IN_MONTH,settimana);
        dataAnnoSuccessivo.set(Calendar.DAY_OF_WEEK,giorniSettimana);
      } else {
        data.set(Calendar.DAY_OF_MONTH,giorniMese);
        dataAnnoSuccessivo.set(Calendar.DAY_OF_MONTH,giorniMese);
      }
      //se la data calcolata per l'anno corrente è successiva a quella di partenza
      //allora inserisco nella lista la data per l'anno successivo
      if (data.getTimeInMillis() <= dataDiPartenza.getTimeInMillis())
        data = dataAnnoSuccessivo;


      listaDate[i] = data;
    }

    dataEsec = trovaMinimo(listaDate);
    return dataEsec;
  }

  private static GregorianCalendar trovaMinimo(GregorianCalendar[] listaDate) {
    GregorianCalendar minimo = listaDate[0];
    for (int i=1;i<listaDate.length;i++){
      if (listaDate[i].getTimeInMillis() < minimo.getTimeInMillis())
        minimo = listaDate[i];
    }
    return minimo;
  }
}