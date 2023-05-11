/*
 * Created on 03-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.web.struts.schedric;

import java.util.Iterator;
import java.util.Vector;

import it.eldasoft.console.db.domain.schedric.SchedRic;
import it.eldasoft.console.web.struts.schedric.wizard.WizardSchedRicAction;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import org.apache.struts.action.ActionForm;

/**
 * 
 * @author Francesco De Filippis
 */
public class SchedRicForm extends ActionForm {

  /** UID */
  private static final long serialVersionUID = -1613614683730264712L;

  private Integer           idSchedRic;
  private int               idRicerca;
  private String            nomeRicerca;
  private String            tipo;
  private String            descTipo;
  private int               attivo;
  private String            nome;
  private Integer           oraAvvio;
  private String            strOraAvvio;
  private Integer           minutoAvvio;
  private String            strMinutoAvvio;
  private String            dataPrimaEsec;
  private int               radioGiorno;
  private int               radioGiornoG;
  private int               radioGiornoM;
  private Integer           giorno;
  private Integer           settimana;
  private Integer           settimanaM;
  private Integer           settimanaS;
  private String            giorniSettimana;
  private boolean           opzioneLunedi;
  private boolean           opzioneMartedi;
  private boolean           opzioneMercoledi;
  private boolean           opzioneGiovedi;
  private boolean           opzioneVenerdi;
  private boolean           opzioneSabato;
  private boolean           opzioneDomenica;
  // private String[] mese;
  private boolean           opzioneGennaio;
  private boolean           opzioneFebbraio;
  private boolean           opzioneMarzo;
  private boolean           opzioneAprile;
  private boolean           opzioneMaggio;
  private boolean           opzioneGiugno;
  private boolean           opzioneLuglio;
  private boolean           opzioneAgosto;
  private boolean           opzioneSettembre;
  private boolean           opzioneOttobre;
  private boolean           opzioneNovembre;
  private boolean           opzioneDicembre;
  private String            giorniMese;
  private Integer           formato;
  private String            descFormato;
  private String            email;
  private String            dataUltEsec;
  private String            dataProxEsec;
  private int               owner;
  private String            metodo;
  private String            periodoOgni;
  private int               esecutore;
  private boolean           noOutputVuoto;
  private String            codiceApplicazione;
  private Integer           ripetiDopoMinuti;

  /**
   * @return Returns the descTipo.
   */
  public String getDescTipo() {
    return descTipo;
  }

  /**
   * @param descTipo
   *        The descTipo to set.
   */
  public void setDescTipo(String descTipo) {
    this.descTipo = descTipo;
  }

  /**
   * @return Returns the periodoOgni.
   */
  public String getPeriodoOgni() {
    return periodoOgni;
  }

  /**
   * @param periodoOgni
   *        The periodoOgni to set.
   */
  public void setPeriodoOgni(String periodoOgni) {
    this.periodoOgni = periodoOgni;
  }

  /**
   * @return Returns the metodo.
   */
  public String getMetodo() {
    return metodo;
  }

  /**
   * @param metodo
   *        The metodo to set.
   */
  public void setMetodo(String metodo) {
    this.metodo = metodo;
  }

  public SchedRicForm() {
    this.idSchedRic = null;
    this.idRicerca = 0;
    this.nomeRicerca = null;
    this.tipo = null;
    this.descTipo = null;
    this.attivo = 1;
    this.nome = null;
    this.oraAvvio = null;
    this.minutoAvvio = null;
    this.strOraAvvio = null;
    this.strMinutoAvvio = null;
    this.dataPrimaEsec = null;
    this.radioGiorno = 0;
    this.radioGiornoG = 0;
    this.radioGiornoM = 0;
    this.giorno = null;
    this.settimana = null;
    this.settimanaS = null;
    this.settimanaM = null;
    this.giorniSettimana = null;
    this.opzioneLunedi = false;
    this.opzioneMartedi = false;
    this.opzioneMercoledi = false;
    this.opzioneGiovedi = false;
    this.opzioneVenerdi = false;
    this.opzioneSabato = false;
    this.opzioneDomenica = false;
    // this.mese = null;
    this.opzioneGennaio = false;
    this.opzioneFebbraio = false;
    this.opzioneMarzo = false;
    this.opzioneAprile = false;
    this.opzioneMaggio = false;
    this.opzioneGiugno = false;
    this.opzioneLuglio = false;
    this.opzioneAgosto = false;
    this.opzioneSettembre = false;
    this.opzioneOttobre = false;
    this.opzioneNovembre = false;
    this.opzioneDicembre = false;
    this.giorniMese = null;
    this.formato = new Integer(-1);
    this.descFormato = null;
    this.email = null;
    this.dataUltEsec = null;
    this.dataProxEsec = null;
    this.owner = 0;
    this.metodo = null;
    this.periodoOgni = null;
    this.esecutore = 0;
    this.noOutputVuoto = false;
    this.codiceApplicazione = null;
    this.ripetiDopoMinuti = null;
  }

  /*
   * Costruttore di un oggetto in sessione a partire dall'equivalente oggetto
   * ricevuto dalla Business Logic
   */

  public SchedRicForm(SchedRic schedRic, String metodo) {
    this.idSchedRic = schedRic.getIdSchedRic();
    this.idRicerca = schedRic.getIdRicerca();
    this.nomeRicerca = schedRic.getNomeRicerca();
    this.tipo = schedRic.getTipo();
    this.descTipo = schedRic.getDescTipo();
    this.attivo = schedRic.getAttivo();
    this.nome = schedRic.getNome();
    this.oraAvvio = new Integer(schedRic.getOraAvvio());
    if (schedRic.getOraAvvio()<10)
      this.strOraAvvio = "0" + this.oraAvvio.toString();
    else
      this.strOraAvvio = this.oraAvvio.toString();
    this.minutoAvvio = new Integer(schedRic.getMinutoAvvio());
    if (schedRic.getMinutoAvvio()<10)
      this.strMinutoAvvio = "0" + this.minutoAvvio.toString();
    else
      this.strMinutoAvvio = this.minutoAvvio.toString();
    this.dataPrimaEsec = UtilityDate.convertiData(schedRic.getDataPrimaEsec(),
        UtilityDate.FORMATO_GG_MM_AAAA);
    this.dataUltEsec = UtilityDate.convertiData(schedRic.getDataUltEsec(),
        UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
    this.dataProxEsec = UtilityDate.convertiData(schedRic.getDataProxEsec(),
        UtilityDate.FORMATO_GG_MM_AAAA);
    this.descFormato = schedRic.getDescFormato();
    
    /* L.G. 25/09/2007: modifica inizializzazione dell'attributo email dopo
     * la possibilita' di inviare la mail a piu' destinatari.
     * Su DB la lista degli indirizzi mail dei destinatari e' rappresentata da
     * un'unica stringa dove i vari valori sono separati da virgola.
     * A video la lista degli indirizzi viene presentata separata da ', ' in
     * modo da rendere piu' leggibile la lista stessa  */
    if(schedRic.getEmail() != null){
      if(schedRic.getEmail().indexOf(",") > 0){
        String[] tmpArrayMail = schedRic.getEmail().split(",");
        StringBuffer buffer = new StringBuffer("");
        for(int i=0; i < tmpArrayMail.length; i++)
          buffer.append(tmpArrayMail[i] + ", ");
        // Rimozione degli ultimi due caratteri perche' inutili
        this.email = buffer.substring(0, buffer.length() - 2);
      } else {
        this.email = schedRic.getEmail();
      }
    } else {
      this.email = null;
    }
    
    this.owner = schedRic.getOwner();
    this.esecutore = schedRic.getEsecutore();
    this.noOutputVuoto = schedRic.getNoOutputVuoto() == 1;
    this.codiceApplicazione = schedRic.getCodiceApplicazione();
    this.ripetiDopoMinuti = schedRic.getRipetiDopoMinuti();

    if ("visualizza".equalsIgnoreCase(metodo)) {
      if (CostantiSchedRic.GIORNO.equalsIgnoreCase(this.tipo)) {
        this.giorno = schedRic.getGiorno();
        if (this.giorno.intValue() == 0) {
          this.periodoOgni = "Ogni giorno";
          if (this.ripetiDopoMinuti != null) {
            Vector<Tabellato> v = WizardSchedRicAction.caricaIntervalliMinuti();
            for (Iterator<Tabellato> iterator = v.iterator(); iterator.hasNext();) {
              Tabellato tab = (Tabellato) iterator.next();
              if (tab.getTipoTabellato().equals(this.ripetiDopoMinuti.toString())) {
                this.periodoOgni += ", ad intervalli di " + tab.getDescTabellato();
              }
            }
          }
        } else {
          this.periodoOgni = "Ogni " + this.giorno + " giorni";
        }
      }
      if (CostantiSchedRic.SETTIMANA.equalsIgnoreCase(this.tipo)) {
        this.settimana = schedRic.getSettimana();
        if (this.settimana.intValue() == 1)
          this.periodoOgni = "Ogni settimana ";
        else
          this.periodoOgni = "Ogni " + this.settimana + " settimane ";
        if (schedRic.getGiorniSettimana() != null
            && schedRic.getGiorniSettimana().indexOf("|") > 0) {
          String[] listaGiorniSettimana = schedRic.getGiorniSettimana().split(
              "\\|");
          this.periodoOgni += ", di "
              + this.listaGiorniToStringGiorni(listaGiorniSettimana);
        }
      }
      if (CostantiSchedRic.MESE.equalsIgnoreCase(this.tipo)) {
        this.giorniMese = schedRic.getGiorniMese();
        this.settimana = schedRic.getSettimana();
        this.giorniSettimana = schedRic.getGiorniSettimana();
        // giorniSettimana e giorniMese sono mutuamente esclusivi...
        // se c'è uno non c'è l'altro
        if (!"".equalsIgnoreCase(this.giorniSettimana)
            && this.giorniSettimana != null) {
          int appoggio = this.settimana.intValue();
          this.periodoOgni = "Ogni "
              + CostantiSchedRic.TABELLATO_SETTIMANA[appoggio - 1];
          appoggio = new Integer(this.giorniSettimana).intValue();
          this.periodoOgni += " "
              + CostantiSchedRic.TABELLATO_GIORNI_SETTIMANA[appoggio - 1];
        }

        if (!"".equalsIgnoreCase(this.giorniMese) && this.giorniMese != null)
          this.periodoOgni = "Ogni " + this.giorniMese + " del mese";
        String[] mese = schedRic.getMese().split("\\|");
        this.periodoOgni += " dei mesi di " + this.listaMesiToStringMesi(mese);
      }
    }
    if ("modifica".equalsIgnoreCase(metodo)) {
      if (schedRic.getGiorno() == null || schedRic.getGiorno().intValue() == 0) {
        this.radioGiorno = 0;
        if (CostantiSchedRic.GIORNO.equalsIgnoreCase(schedRic.getTipo()))
          this.radioGiornoG = 0;
        //else
        //  this.radioGiornoM = 0;
      } else {
        this.radioGiorno = 1;
        if (CostantiSchedRic.GIORNO.equalsIgnoreCase(schedRic.getTipo()))
          this.radioGiornoG = 1;
        //else
        //  this.radioGiornoM = 1;
      }
      if (CostantiSchedRic.MESE.equalsIgnoreCase(schedRic.getTipo())){
        if (!"".equalsIgnoreCase(schedRic.getGiorniMese())
          && schedRic.getGiorniMese() != null)
          this.radioGiornoM = 1;
        else
          this.radioGiornoM = 0;
      }
      this.giorno = schedRic.getGiorno();
      this.settimana = schedRic.getSettimana();
      if (CostantiSchedRic.MESE.equalsIgnoreCase(this.tipo))
        this.settimanaM = schedRic.getSettimana();
      if (CostantiSchedRic.SETTIMANA.equalsIgnoreCase(this.tipo))
        this.settimanaS = schedRic.getSettimana();
      if (schedRic.getGiorniSettimana() != null
          && schedRic.getGiorniSettimana().indexOf("|") > 0) {
        String[] listaGiorniSettimana = schedRic.getGiorniSettimana().split(
            "\\|");
        this.listaGiorniToCheckGiorni(listaGiorniSettimana);
      } else {
        this.giorniSettimana = schedRic.getGiorniSettimana();
      }

      if (schedRic.getMese() != null) {
        String[] mese = schedRic.getMese().split("\\|");
        this.listaMesiToCheckMesi(mese);
      }
      this.giorniMese = schedRic.getGiorniMese();
      this.formato = schedRic.getFormato();
    }
  }

  /*
   * Ritorna l'oggetto per la Business Logic a partire dall'oggetto presente in
   * sessione.
   */
  public SchedRic getDatiPerModel() {
    SchedRic schedRic = new SchedRic();

    schedRic.setIdSchedRic(this.idSchedRic);
    schedRic.setIdRicerca(this.idRicerca);
    schedRic.setTipo(this.tipo);
    schedRic.setAttivo(this.attivo);
    schedRic.setNome(this.nome);
    schedRic.setOraAvvio(this.oraAvvio.intValue());
    schedRic.setMinutoAvvio(this.minutoAvvio.intValue());
    if (this.dataPrimaEsec != null && this.dataPrimaEsec.length() > 0)
      schedRic.setDataPrimaEsec(UtilityDate.convertiData(this.dataPrimaEsec,
          UtilityDate.FORMATO_GG_MM_AAAA));
    else
      schedRic.setDataPrimaEsec(null);
    if (this.radioGiornoG == 0 && this.radioGiorno == 0 && CostantiSchedRic.GIORNO.equals(this.tipo))
      this.giorno = new Integer(0);
    schedRic.setGiorno(this.giorno);
    // utilizzo il form anche per la modifica classica e dato che nella pagina
    // ci sono 2 campi settimana (settimanaS e settimanaM) controllo se il campo
    // è nullo (non siamo nel wizard ma siamo nella modifica della
    // pianificazione)
    // allora valorizzo il campo di db con il dato relativo
    if (this.settimana == null || this.settimana.intValue() == 0) {
      if (CostantiSchedRic.MESE.equalsIgnoreCase(this.tipo))
        schedRic.setSettimana(this.settimanaM);
      if (CostantiSchedRic.SETTIMANA.equalsIgnoreCase(this.tipo))
        schedRic.setSettimana(this.settimanaS);
      if (CostantiSchedRic.GIORNO.equalsIgnoreCase(this.tipo)
          || CostantiSchedRic.UNICA.equalsIgnoreCase(this.tipo))
        schedRic.setSettimana(this.settimana);
    } else
      schedRic.setSettimana(this.settimana);
    if (this.giorniSettimana == null || this.giorniSettimana.length() <= 0)
      schedRic.setGiorniSettimana(UtilityStringhe.convertiStringaVuotaInNull(
          checkGiorniToListaGiorni()));
    else
      schedRic.setGiorniSettimana(this.giorniSettimana);
    schedRic.setMese(UtilityStringhe.convertiStringaVuotaInNull(checkMesiToListaMesi()));
    schedRic.setGiorniMese(UtilityStringhe.convertiStringaVuotaInNull(this.giorniMese));
    schedRic.setFormato(this.formato == null || this.formato.intValue() < 0 ?
        null : this.formato);
    /* L.G. 25/09/2007: modifica dell'attributo email dopo la possibilita'
     * di inviare la mail a piu' destinatari.
     * Su DB la lista degli indirizzi mail dei destinatari deve essere sempre
     * rappresentata da un'unica stringa dove i vari valori sono separati da
     * virgola. A video la lista degli indirizzi viene presentata separata da
     * ', ' in modo da rendere piu' leggibile la lista stessa    */
    if(this.email.indexOf(",") > 0){
      String[] tmpArrayMail = this.email.split(",");
      StringBuffer buffer = new StringBuffer("");
      for(int i=0; i < tmpArrayMail.length; i++){
        buffer.append(tmpArrayMail[i].trim() + ",");
      }
      //Rimozione dell'ultimo carattere
      schedRic.setEmail(buffer.substring(0, buffer.length()-1));
    } else {
      schedRic.setEmail(this.email.trim());
    }
    if (this.dataUltEsec != null && this.dataUltEsec != "")
      schedRic.setDataUltEsec(UtilityDate.convertiData(this.dataUltEsec,
          UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    else
      schedRic.setDataUltEsec(null);
    // la data prox esec viene calcolata nel manager tramite una funzione
    // specifica!
    schedRic.setOwner(this.owner);
    schedRic.setEsecutore(this.esecutore);
    schedRic.setNoOutputVuoto(this.noOutputVuoto ? 1 : 0);
    schedRic.setCodiceApplicazione(this.codiceApplicazione);
    if (CostantiSchedRic.GIORNO.equalsIgnoreCase(this.tipo)
        && this.giorno.intValue() == 0)
      schedRic.setRipetiDopoMinuti(this.ripetiDopoMinuti);

    return schedRic;
  }

  private void listaGiorniToCheckGiorni(String[] lista) {
    for (int i = 0; i < lista.length; i++) {
      switch (new Integer(lista[i]).intValue()) {
      case 1:
        this.opzioneDomenica = true;
        break;
      case 2:
        this.opzioneLunedi = true;
        break;
      case 3:
        this.opzioneMartedi = true;
        break;
      case 4:
        this.opzioneMercoledi = true;
        break;
      case 5:
        this.opzioneGiovedi = true;
        break;
      case 6:
        this.opzioneVenerdi = true;
        break;
      case 7:
        this.opzioneSabato = true;
        break;

      }
    }
  }

  private String listaGiorniToStringGiorni(String[] lista) {
    String giorni = "";
    int appoggio;
    for (int i = 0; i < lista.length; i++) {
      if (!"".equals(giorni)) giorni += ", ";
      appoggio = new Integer(lista[i]).intValue();
      giorni += CostantiSchedRic.TABELLATO_GIORNI_SETTIMANA[appoggio - 1];
    }
    return giorni;
  }

  private String checkGiorniToListaGiorni() {

    String giorni = "";
    if (this.opzioneDomenica == true) giorni += "1|";
    if (this.opzioneLunedi == true) giorni += "2|";
    if (this.opzioneMartedi == true) giorni += "3|";
    if (this.opzioneMercoledi == true) giorni += "4|";
    if (this.opzioneGiovedi == true) giorni += "5|";
    if (this.opzioneVenerdi == true) giorni += "6|";
    if (this.opzioneSabato == true) giorni += "7|";

    return giorni;
  }

  private void listaMesiToCheckMesi(String[] lista) {
    for (int i = 0; i < lista.length; i++) {
      switch (new Integer(lista[i]).intValue()) {
      case 1:
        this.opzioneGennaio = true;
        break;
      case 2:
        this.opzioneFebbraio = true;
        break;
      case 3:
        this.opzioneMarzo = true;
        break;
      case 4:
        this.opzioneAprile = true;
        break;
      case 5:
        this.opzioneMaggio = true;
        break;
      case 6:
        this.opzioneGiugno = true;
        break;
      case 7:
        this.opzioneLuglio = true;
        break;
      case 8:
        this.opzioneAgosto = true;
        break;
      case 9:
        this.opzioneSettembre = true;
        break;
      case 10:
        this.opzioneOttobre = true;
        break;
      case 11:
        this.opzioneNovembre = true;
        break;
      case 12:
        this.opzioneDicembre = true;
        break;
      }
    }
  }

  private String listaMesiToStringMesi(String[] lista) {
    String mesi = "";
    for (int i = 0; i < lista.length; i++) {
      switch (new Integer(lista[i]).intValue()) {
      case 1:
        mesi += "Gennaio";
        break;
      case 2:
        if (!"".equals(mesi)) mesi += ", ";
        mesi += "Febbraio";
        break;
      case 3:
        if (!"".equals(mesi)) mesi += ", ";
        mesi += "Marzo";
        break;
      case 4:
        if (!"".equals(mesi)) mesi += ", ";
        mesi += "Aprile";
        break;
      case 5:
        if (!"".equals(mesi)) mesi += ", ";
        mesi += "Maggio";
        break;
      case 6:
        if (!"".equals(mesi)) mesi += ", ";
        mesi += "Giugno";
        break;
      case 7:
        if (!"".equals(mesi)) mesi += ", ";
        mesi += "Luglio";
        break;
      case 8:
        if (!"".equals(mesi)) mesi += ", ";
        mesi += "Agosto";
        break;
      case 9:
        if (!"".equals(mesi)) mesi += ", ";
        mesi += "Settembre";
        break;
      case 10:
        if (!"".equals(mesi)) mesi += ", ";
        mesi += "Ottobre";
        break;
      case 11:
        if (!"".equals(mesi)) mesi += ", ";
        mesi += "Novembre";
        break;
      case 12:
        if (!"".equals(mesi)) mesi += ", ";
        mesi += "Dicembre";
        break;
      }
    }
    return mesi;
  }

  private String checkMesiToListaMesi() {
    // Vector lista = new Vector();
    String mesi = "";
    if (this.opzioneGennaio == true) mesi += "1|";
    if (this.opzioneFebbraio == true) mesi += "2|";
    if (this.opzioneMarzo == true) mesi += "3|";
    if (this.opzioneAprile == true) mesi += "4|";
    if (this.opzioneMaggio == true) mesi += "5|";
    if (this.opzioneGiugno == true) mesi += "6|";
    if (this.opzioneLuglio == true) mesi += "7|";
    if (this.opzioneAgosto == true) mesi += "8|";
    if (this.opzioneSettembre == true) mesi += "9|";
    if (this.opzioneOttobre == true) mesi += "10|";
    if (this.opzioneNovembre == true) mesi += "11|";
    if (this.opzioneDicembre == true) mesi += "12|";

    return mesi;
  }

  /**
   * @return Returns the attivo.
   */
  public int getAttivo() {
    return attivo;
  }

  /**
   * @param attivo
   *        The attivo to set.
   */
  public void setAttivo(int attivo) {
    this.attivo = attivo;
  }

  /**
   * @return Returns the dataPrimaEsec.
   */
  public String getDataPrimaEsec() {
    return dataPrimaEsec;
  }

  /**
   * @param dataPrimaEsec
   *        The dataPrimaEsec to set.
   */
  public void setDataPrimaEsec(String dataPrimaEsec) {
    this.dataPrimaEsec = dataPrimaEsec;
  }

  /**
   * @return Returns the dataProxEsec.
   */
  public String getDataProxEsec() {
    return dataProxEsec;
  }

  /**
   * @param dataProxEsec
   *        The dataProxEsec to set.
   */
  public void setDataProxEsec(String dataProxEsec) {
    this.dataProxEsec = dataProxEsec;
  }

  /**
   * @return Returns the dataUltEsec.
   */
  public String getDataUltEsec() {
    return dataUltEsec;
  }

  /**
   * @param dataUltEsec
   *        The dataUltEsec to set.
   */
  public void setDataUltEsec(String dataUltEsec) {
    this.dataUltEsec = dataUltEsec;
  }

  /**
   * @return Returns the email.
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param email
   *        The email to set.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @return Returns the formato.
   */
  public Integer getFormato() {
    return formato;
  }

  /**
   * @param formato
   *        The formato to set.
   */
  public void setFormato(Integer formato) {
    this.formato = formato;
  }

  /**
   * @return Returns the giorniMese.
   */
  public String getGiorniMese() {
    return giorniMese;
  }

  /**
   * @param giorniMese
   *        The giorniMese to set.
   */
  public void setGiorniMese(String giorniMese) {
    this.giorniMese = giorniMese;
  }

  /**
   * @return Returns the giorno.
   */
  public Integer getGiorno() {
    return giorno;
  }

  /**
   * @param giorno
   *        The giorno to set.
   */
  public void setGiorno(Integer giorno) {
    this.giorno = giorno;
  }

  /**
   * @return Returns the idRicerca.
   */
  public int getIdRicerca() {
    return idRicerca;
  }

  /**
   * @param idRicerca
   *        The idRicerca to set.
   */
  public void setIdRicerca(int idRicerca) {
    this.idRicerca = idRicerca;
  }

  /**
   * @return Returns the idSchedRic.
   */
  public Integer getIdSchedRic() {
    return idSchedRic;
  }

  /**
   * @param idSchedRic
   *        The idSchedRic to set.
   */
  public void setIdSchedRic(Integer idSchedRic) {
    this.idSchedRic = idSchedRic;
  }

  /**
   * @return Returns the minutoAvvio.
   */
  public Integer getMinutoAvvio() {
    return minutoAvvio;
  }

  /**
   * @param minutoAvvio
   *        The minutoAvvio to set.
   */
  public void setMinutoAvvio(Integer minutoAvvio) {
    this.minutoAvvio = minutoAvvio;
  }

  /**
   * @return Returns the nome.
   */
  public String getNome() {
    return nome;
  }

  /**
   * @param nome
   *        The nome to set.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * @return Returns the oraAvvio.
   */
  public Integer getOraAvvio() {
    return oraAvvio;
  }

  /**
   * @param oraAvvio
   *        The oraAvvio to set.
   */
  public void setOraAvvio(Integer oraAvvio) {
    this.oraAvvio = oraAvvio;
  }

  /**
   * @return Returns the owner.
   */
  public int getOwner() {
    return owner;
  }

  /**
   * @param owner
   *        The owner to set.
   */
  public void setOwner(int owner) {
    this.owner = owner;
  }

  /**
   * @return Returns the settimana.
   */
  public Integer getSettimana() {
    return settimana;
  }

  /**
   * @param settimana
   *        The settimana to set.
   */
  public void setSettimana(Integer settimana) {
    this.settimana = settimana;
  }

  /**
   * @return Returns the tipo.
   */
  public String getTipo() {
    return tipo;
  }

  /**
   * @param tipo
   *        The tipo to set.
   */
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  /**
   * @return Returns the opzioneAgosto.
   */
  public boolean isOpzioneAgosto() {
    return opzioneAgosto;
  }

  /**
   * @param opzioneAgosto
   *        The opzioneAgosto to set.
   */
  public void setOpzioneAgosto(boolean opzioneAgosto) {
    this.opzioneAgosto = opzioneAgosto;
  }

  /**
   * @return Returns the opzioneAprile.
   */
  public boolean isOpzioneAprile() {
    return opzioneAprile;
  }

  /**
   * @param opzioneAprile
   *        The opzioneAprile to set.
   */
  public void setOpzioneAprile(boolean opzioneAprile) {
    this.opzioneAprile = opzioneAprile;
  }

  /**
   * @return Returns the opzioneDicembre.
   */
  public boolean isOpzioneDicembre() {
    return opzioneDicembre;
  }

  /**
   * @param opzioneDicembre
   *        The opzioneDicembre to set.
   */
  public void setOpzioneDicembre(boolean opzioneDicembre) {
    this.opzioneDicembre = opzioneDicembre;
  }

  /**
   * @return Returns the opzioneDomenica.
   */
  public boolean isOpzioneDomenica() {
    return opzioneDomenica;
  }

  /**
   * @param opzioneDomenica
   *        The opzioneDomenica to set.
   */
  public void setOpzioneDomenica(boolean opzioneDomenica) {
    this.opzioneDomenica = opzioneDomenica;
  }

  /**
   * @return Returns the opzioneFebbraio.
   */
  public boolean isOpzioneFebbraio() {
    return opzioneFebbraio;
  }

  /**
   * @param opzioneFebbraio
   *        The opzioneFebbraio to set.
   */
  public void setOpzioneFebbraio(boolean opzioneFebbraio) {
    this.opzioneFebbraio = opzioneFebbraio;
  }

  /**
   * @return Returns the opzioneGennaio.
   */
  public boolean isOpzioneGennaio() {
    return opzioneGennaio;
  }

  /**
   * @param opzioneGennaio
   *        The opzioneGennaio to set.
   */
  public void setOpzioneGennaio(boolean opzioneGennaio) {
    this.opzioneGennaio = opzioneGennaio;
  }

  /**
   * @return Returns the opzioneGiovedi.
   */
  public boolean isOpzioneGiovedi() {
    return opzioneGiovedi;
  }

  /**
   * @param opzioneGiovedi
   *        The opzioneGiovedi to set.
   */
  public void setOpzioneGiovedi(boolean opzioneGiovedi) {
    this.opzioneGiovedi = opzioneGiovedi;
  }

  /**
   * @return Returns the opzioneGiugno.
   */
  public boolean isOpzioneGiugno() {
    return opzioneGiugno;
  }

  /**
   * @param opzioneGiugno
   *        The opzioneGiugno to set.
   */
  public void setOpzioneGiugno(boolean opzioneGiugno) {
    this.opzioneGiugno = opzioneGiugno;
  }

  /**
   * @return Returns the opzioneLuglio.
   */
  public boolean isOpzioneLuglio() {
    return opzioneLuglio;
  }

  /**
   * @param opzioneLuglio
   *        The opzioneLuglio to set.
   */
  public void setOpzioneLuglio(boolean opzioneLuglio) {
    this.opzioneLuglio = opzioneLuglio;
  }

  /**
   * @return Returns the opzioneLunedi.
   */
  public boolean isOpzioneLunedi() {
    return opzioneLunedi;
  }

  /**
   * @param opzioneLunedi
   *        The opzioneLunedi to set.
   */
  public void setOpzioneLunedi(boolean opzioneLunedi) {
    this.opzioneLunedi = opzioneLunedi;
  }

  /**
   * @return Returns the opzioneMaggio.
   */
  public boolean isOpzioneMaggio() {
    return opzioneMaggio;
  }

  /**
   * @param opzioneMaggio
   *        The opzioneMaggio to set.
   */
  public void setOpzioneMaggio(boolean opzioneMaggio) {
    this.opzioneMaggio = opzioneMaggio;
  }

  /**
   * @return Returns the opzioneMartedi.
   */
  public boolean isOpzioneMartedi() {
    return opzioneMartedi;
  }

  /**
   * @param opzioneMartedi
   *        The opzioneMartedi to set.
   */
  public void setOpzioneMartedi(boolean opzioneMartedi) {
    this.opzioneMartedi = opzioneMartedi;
  }

  /**
   * @return Returns the opzioneMarzo.
   */
  public boolean isOpzioneMarzo() {
    return opzioneMarzo;
  }

  /**
   * @param opzioneMarzo
   *        The opzioneMarzo to set.
   */
  public void setOpzioneMarzo(boolean opzioneMarzo) {
    this.opzioneMarzo = opzioneMarzo;
  }

  /**
   * @return Returns the opzioneMercoledi.
   */
  public boolean isOpzioneMercoledi() {
    return opzioneMercoledi;
  }

  /**
   * @param opzioneMercoledi
   *        The opzioneMercoledi to set.
   */
  public void setOpzioneMercoledi(boolean opzioneMercoledi) {
    this.opzioneMercoledi = opzioneMercoledi;
  }

  /**
   * @return Returns the opzioneNovembre.
   */
  public boolean isOpzioneNovembre() {
    return opzioneNovembre;
  }

  /**
   * @param opzioneNovembre
   *        The opzioneNovembre to set.
   */
  public void setOpzioneNovembre(boolean opzioneNovembre) {
    this.opzioneNovembre = opzioneNovembre;
  }

  /**
   * @return Returns the opzioneOttobre.
   */
  public boolean isOpzioneOttobre() {
    return opzioneOttobre;
  }

  /**
   * @param opzioneOttobre
   *        The opzioneOttobre to set.
   */
  public void setOpzioneOttobre(boolean opzioneOttobre) {
    this.opzioneOttobre = opzioneOttobre;
  }

  /**
   * @return Returns the opzioneSabato.
   */
  public boolean isOpzioneSabato() {
    return opzioneSabato;
  }

  /**
   * @param opzioneSabato
   *        The opzioneSabato to set.
   */
  public void setOpzioneSabato(boolean opzioneSabato) {
    this.opzioneSabato = opzioneSabato;
  }

  /**
   * @return Returns the opzioneSettembre.
   */
  public boolean isOpzioneSettembre() {
    return opzioneSettembre;
  }

  /**
   * @param opzioneSettembre
   *        The opzioneSettembre to set.
   */
  public void setOpzioneSettembre(boolean opzioneSettembre) {
    this.opzioneSettembre = opzioneSettembre;
  }

  /**
   * @return Returns the opzioneVenerdi.
   */
  public boolean isOpzioneVenerdi() {
    return opzioneVenerdi;
  }

  /**
   * @param opzioneVenerdi
   *        The opzioneVenerdi to set.
   */
  public void setOpzioneVenerdi(boolean opzioneVenerdi) {
    this.opzioneVenerdi = opzioneVenerdi;
  }

  /**
   * @return Returns the radioGiorno.
   */
  public int getRadioGiorno() {
    return radioGiorno;
  }

  /**
   * @param radioGiorno
   *        The radioGiorno to set.
   */
  public void setRadioGiorno(int radioGiorno) {
    this.radioGiorno = radioGiorno;
  }

  /**
   * @return Returns the giorniSettimana.
   */
  public String getGiorniSettimana() {
    return giorniSettimana;
  }

  /**
   * @param giorniSettimana
   *        The giorniSettimana to set.
   */
  public void setGiorniSettimana(String giorniSettimana) {
    this.giorniSettimana = giorniSettimana;
  }

  /**
   * @return Returns the descFormato.
   */
  public String getDescFormato() {
    return descFormato;
  }

  /**
   * @param descFormato
   *        The descFormato to set.
   */
  public void setDescFormato(String descFormato) {
    this.descFormato = descFormato;
  }

  /**
   * @return Returns the nomeRicerca.
   */
  public String getNomeRicerca() {
    return nomeRicerca;
  }

  /**
   * @param nomeRicerca
   *        The nomeRicerca to set.
   */
  public void setNomeRicerca(String nomeRicerca) {
    this.nomeRicerca = nomeRicerca;
  }

  /**
   * @return Returns the settimanaM.
   */
  public Integer getSettimanaM() {
    return settimanaM;
  }

  /**
   * @param settimanaM
   *        The settimanaM to set.
   */
  public void setSettimanaM(Integer settimanaM) {
    this.settimanaM = settimanaM;
  }

  /**
   * @return Returns the settimanaS.
   */
  public Integer getSettimanaS() {
    return settimanaS;
  }

  /**
   * @param settimanaS
   *        The settimanaS to set.
   */
  public void setSettimanaS(Integer settimanaS) {
    this.settimanaS = settimanaS;
  }

  /**
   * @return Returns the radioGiornoG.
   */
  public int getRadioGiornoG() {
    return radioGiornoG;
  }

  /**
   * @param radioGiornoG
   *        The radioGiornoG to set.
   */
  public void setRadioGiornoG(int radioGiornoG) {
    this.radioGiornoG = radioGiornoG;
  }

  /**
   * @return Returns the radioGiornoM.
   */
  public int getRadioGiornoM() {
    return radioGiornoM;
  }

  /**
   * @param radioGiornoM
   *        The radioGiornoS to set.
   */
  public void setRadioGiornoM(int radioGiornoM) {
    this.radioGiornoM = radioGiornoM;
  }

  
  /**
   * @return Returns the strMinutoAvvio.
   */
  public String getStrMinutoAvvio() {
    return strMinutoAvvio;
  }

  
  /**
   * @param strMinutoAvvio The strMinutoAvvio to set.
   */
  public void setStrMinutoAvvio(String strMinutoAvvio) {
    this.strMinutoAvvio = strMinutoAvvio;
  }

  
  /**
   * @return Returns the strOraAvvio.
   */
  public String getStrOraAvvio() {
    return strOraAvvio;
  }

  
  /**
   * @param strOraAvvio The strOraAvvio to set.
   */
  public void setStrOraAvvio(String strOraAvvio) {
    this.strOraAvvio = strOraAvvio;
  }

  
  /**
   * @return Returns the esecutore.
   */
  public int getEsecutore() {
    return esecutore;
  }

  
  /**
   * @param esecutore The esecutore to set.
   */
  public void setEsecutore(int esecutore) {
    this.esecutore = esecutore;
  }

  
  /**
   * @return Ritorna noOutputVuoto.
   */
  public boolean getNoOutputVuoto() {
    return noOutputVuoto;
  }

  
  /**
   * @param noOutputVuoto noOutputVuoto da settare internamente alla classe.
   */
  public void setNoOutputVuoto(boolean noOutputVuoto) {
    this.noOutputVuoto = noOutputVuoto;
  }
  
  /**
   * @return Ritorna codiceApplicazione.
   */
  public String getCodiceApplicazione() {
    return codiceApplicazione;
  }
  
  /**
   * @param codiceApplicazione codiceApplicazione da settare internamente alla classe.
   */
  public void setCodiceApplicazione(String codiceApplicazione) {
    this.codiceApplicazione = codiceApplicazione;
  }
  
  /**
   * @return Ritorna ripetiDopoMinuti.
   */
  public Integer getRipetiDopoMinuti() {
    return ripetiDopoMinuti;
  }
  
  /**
   * @param ripetiDopoMinuti ripetiDopoMinuti da settare internamente alla classe.
   */
  public void setRipetiDopoMinuti(Integer ripetiDopoMinuti) {
    this.ripetiDopoMinuti = ripetiDopoMinuti;
  }

}