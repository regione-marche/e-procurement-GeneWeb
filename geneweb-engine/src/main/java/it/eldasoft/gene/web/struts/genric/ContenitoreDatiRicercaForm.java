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
package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.db.domain.genric.FiltroRicerca;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.gene.web.struts.genric.datigen.TestataRicercaForm;
import it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm;
import it.eldasoft.gene.web.struts.genric.giunzione.GiunzioneRicercaForm;
import it.eldasoft.gene.web.struts.genric.gruppo.GruppoForm;
import it.eldasoft.gene.web.struts.genric.ordinamento.OrdinamentoRicercaForm;
import it.eldasoft.gene.web.struts.genric.parametro.ParametroRicercaForm;
import it.eldasoft.utils.sql.comp.SqlElementoCondizione;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.io.Serializable;
import java.util.Vector;

/**
 * Contenitore di dati relativi ad una ricerca. Viene usato per raccogliere in
 * un unico oggetto tutti i dati di una ricerca, nonchè rappresenta l'oggetto
 * che viene memorizzato in sessione durante la gestione di una ricerca
 *
 * @author Stefano.Sabbadin
 */
public class ContenitoreDatiRicercaForm implements Serializable {

  /** UID */
  private static final long  serialVersionUID = -1613614683730264712L;

  private TestataRicercaForm             testata;
  private Vector<GruppoForm>             elencoGruppi;
  private Vector<TabellaRicercaForm>     elencoArgomenti;
  private Vector<CampoRicercaForm>       elencoCampi;
  private Vector<GiunzioneRicercaForm>   elencoGiunzioni;
  private Vector<ParametroRicercaForm>   elencoParametri;
  private Vector<FiltroRicercaForm>      elencoFiltri;
  private Vector<OrdinamentoRicercaForm> elencoOrdinamenti;

  /**
   * Flag settato quando si carica questo oggetto in sessione per richiedere
   * l'esecuzione di un report a partire dalla lista, e non dal dettaglio del
   * report stesso
   */
  private boolean            eseguiDaLista;

  /** Stato del report rispetto al profilo attivo */
  private boolean            statoReportNelProfiloAttivo;

  public ContenitoreDatiRicercaForm() {
    this.testata = new TestataRicercaForm();
    this.elencoGruppi = new Vector<GruppoForm>();
    this.elencoArgomenti = new Vector<TabellaRicercaForm>();
    this.elencoCampi = new Vector<CampoRicercaForm>();
    this.elencoGiunzioni = new Vector<GiunzioneRicercaForm>();
    this.elencoParametri = new Vector<ParametroRicercaForm>();
    this.elencoFiltri = new Vector<FiltroRicercaForm>();
    this.elencoOrdinamenti = new Vector<OrdinamentoRicercaForm>();
    this.statoReportNelProfiloAttivo = true;
    this.eseguiDaLista = false;
  }

  /*
   * Costruttore di un oggetto in sessione a partire dall'equivalente oggetto
   * ricevuto dalla Business Logic
   */
  public ContenitoreDatiRicercaForm(ContenitoreDatiRicerca contenitore) {
    this.testata = new TestataRicercaForm(contenitore.getDatiGenerali());
    this.elencoGruppi = new Vector<GruppoForm>();
    for (int i = 0; i < contenitore.getNumeroGruppi(); i++)
      this.aggiungiGruppo(new GruppoForm(contenitore.estraiGruppo(i)));

    this.elencoArgomenti = new Vector<TabellaRicercaForm>();
    for (int i = 0; i < contenitore.getNumeroTabelle(); i++)
      this.aggiungiArgomento(new TabellaRicercaForm(
          contenitore.estraiArgomento(i)));

    this.elencoCampi = new Vector<CampoRicercaForm>();
    for (int i = 0; i < contenitore.getNumeroCampi(); i++)
      this.aggiungiCampo(new CampoRicercaForm(contenitore.estraiCampo(i)));

    this.elencoGiunzioni = new Vector<GiunzioneRicercaForm>();
    for (int i = 0; i < contenitore.getNumeroGiunzioni(); i++)
      this.aggiungiGiunzione(new GiunzioneRicercaForm(
          contenitore.estraiGiunzione(i)));

    this.elencoParametri = new Vector<ParametroRicercaForm>();
    for (int i = 0; i < contenitore.getNumeroParametri(); i++)
      this.aggiungiParametro(new ParametroRicercaForm(
          contenitore.estraiParametro(i)));

    this.elencoFiltri = new Vector<FiltroRicercaForm>();
    for (int i = 0; i < contenitore.getNumeroFiltri(); i++)
      this.aggiungiFiltro(new FiltroRicercaForm(contenitore.estraiFiltro(i)));

    this.elencoOrdinamenti = new Vector<OrdinamentoRicercaForm>();
    for (int i = 0; i < contenitore.getNumeroOrdinamenti(); i++)
      this.aggiungiOrdinamento(new OrdinamentoRicercaForm(
          contenitore.estraiOrdinamento(i)));
    
    this.statoReportNelProfiloAttivo = true;
    this.eseguiDaLista = false;
  }

  /*
   * Ritorna l'oggetto per la Business Logic a partire dall'oggetto presente in
   * sessione.
   */
  public ContenitoreDatiRicerca getDatiPerModel() {
    ContenitoreDatiRicerca contenitore = new ContenitoreDatiRicerca();
    FiltroRicerca filtroRicerca = null;

    contenitore.setDatiGenerali(this.testata.getDatiPerModel());
    for (int i = 0; i < this.elencoGruppi.size(); i++)
      contenitore.aggiungiGruppo(this.estraiGruppo(i).getDatiPerModel());
    for (int i = 0; i < this.elencoArgomenti.size(); i++)
      contenitore.aggiungiTabella(this.estraiTabella(i).getDatiPerModel());
    for (int i = 0; i < this.elencoCampi.size(); i++)
      contenitore.aggiungiCampo(this.estraiCampo(i).getDatiPerModel());
    for (int i = 0; i < this.elencoGiunzioni.size(); i++)
      contenitore.aggiungiGiunzione(this.estraiGiunzione(i).getDatiPerModel());
    for (int i = 0; i < this.elencoParametri.size(); i++)
      contenitore.aggiungiParametro(this.estraiParametro(i).getDatiPerModel());
    for (int i = 0; i < this.elencoFiltri.size(); i++) {
      filtroRicerca = this.estraiFiltro(i).getDatiPerModel();
      // Nel caso di sola ricerca base con operatore LIKE (contiene), se il
      // valore
      // inserito è senza "%" da qualche parte, allora viene inserito prima e
      // dopo il valore da cercare
      if (CostantiGenRicerche.REPORT_BASE == contenitore.getDatiGenerali().getFamiglia().intValue()
          && SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MATCH.equals(filtroRicerca.getOperatore())
          && FiltroRicerca.TIPO_CONFRONTO_VALORE == filtroRicerca.getTipoConfronto().intValue()
          && filtroRicerca.getValoreConfronto().indexOf("%") == -1)
        filtroRicerca.setValoreConfronto("%"
            + filtroRicerca.getValoreConfronto()
            + "%");
      contenitore.aggiungiFiltro(filtroRicerca);
    }
    for (int i = 0; i < this.elencoOrdinamenti.size(); i++)
      contenitore.aggiungiOrdinamento(this.estraiOrdinamento(i).getDatiPerModel());

    if (this.testata.getId() != null)
      contenitore.setIdRicerca(new Integer(this.testata.getId()).intValue());

    return contenitore;
  }

  /**
   * @return nome dell'entit&agrave; principale e non l'informazione presente
   *         nella testata, che è invece il suo alias (che comunque in genere
   *         coincide)
   */
  public String getNomeEntitaPrincipale() {
    TabellaRicercaForm tabForm = null;
    String nomeTabella = null;
    for (int i = 0; i < this.getNumeroTabelle(); i++) {
      tabForm = this.getElencoArgomenti().get(i);
      if (tabForm.getAliasTabella().equals(this.getTestata().getEntPrinc())) {
        nomeTabella = tabForm.getNomeTabella();
        break;
      }
    }
    return nomeTabella;
  }

  /**
   * @return Returns the testata.
   */
  public TestataRicercaForm getTestata() {
    return testata;
  }

  /**
   * @param testata
   *        The testata to set.
   */
  public void setTestata(TestataRicercaForm testata) {
    this.testata = testata;
  }

  /**
   * @return Returns the elencoCampi.
   */
  public Vector<CampoRicercaForm> getElencoCampi() {
    return elencoCampi;
  }

  /**
   * @return Returns the elencoFiltri.
   */
  public Vector<FiltroRicercaForm> getElencoFiltri() {
    return elencoFiltri;
  }

  /**
   * @return Returns the elencoParametri.
   */
  public Vector<ParametroRicercaForm> getElencoParametri() {
    return elencoParametri;
  }

  /**
   * @return Returns the elencoGiunzioni.
   */
  public Vector<GiunzioneRicercaForm> getElencoGiunzioni() {
    return elencoGiunzioni;
  }

  /**
   * @return Returns the elencoOrdinamenti.
   */
  public Vector<OrdinamentoRicercaForm> getElencoOrdinamenti() {
    return elencoOrdinamenti;
  }

  /**
   * @return Returns the elencoArgomenti.
   */
  public Vector<TabellaRicercaForm> getElencoArgomenti() {
    return elencoArgomenti;
  }

  /**
   * @return Ritorna l'elenco delle tabelle visibili nella ricerca
   */
  public Vector<TabellaRicercaForm> getElencoArgomentiVisibili() {
    Vector<TabellaRicercaForm> elencoTabelleVisibili = new Vector<TabellaRicercaForm>();
    Vector<TabellaRicercaForm> elencoTabelle = this.elencoArgomenti;
    TabellaRicercaForm tabella = null;
    for (int i = 0; i < elencoTabelle.size(); i++) {
      tabella = elencoTabelle.get(i);
      if (tabella.getVisibile()) elencoTabelleVisibili.add(tabella);
    }
    return elencoTabelleVisibili;
  }

  /**
   * @return Returns the elencoGruppi.
   */
  public Vector<GruppoForm> getElencoGruppi() {
    return elencoGruppi;
  }

  /**
   * Imposta in tutte le informazioni all'interno del contenitore l'id della
   * ricerca
   *
   * @param id
   *        id della ricerca
   */
  public void setIdRicerca(String idRicerca) {
    this.testata.setId(idRicerca);

    for (int i = 0; i < this.elencoGruppi.size(); i++) {
      (this.elencoGruppi.elementAt(i)).setId(idRicerca);
    }
    for (int i = 0; i < this.elencoArgomenti.size(); i++) {
      (this.elencoArgomenti.elementAt(i)).setId(idRicerca);
    }
    for (int i = 0; i < this.elencoCampi.size(); i++) {
      (this.elencoCampi.elementAt(i)).setId(idRicerca);
    }
    for (int i = 0; i < this.elencoGiunzioni.size(); i++) {
      (this.elencoGiunzioni.elementAt(i)).setId(idRicerca);
    }
    for (int i = 0; i < this.elencoFiltri.size(); i++) {
      (this.elencoFiltri.elementAt(i)).setId(idRicerca);
    }
    for (int i = 0; i < this.elencoParametri.size(); i++) {
      (this.elencoParametri.elementAt(i)).setId(idRicerca);
    }
    for (int i = 0; i < this.elencoOrdinamenti.size(); i++) {
      (this.elencoOrdinamenti.elementAt(i)).setId(idRicerca);
    }
  }

  /**
   * Aggiunge un gruppo all'elenco gruppi del contenitore
   *
   * @param record
   *        gruppo da inserire
   */
  public void aggiungiGruppo(GruppoForm record) {
    record.setId(this.testata.getId());
    // per i gruppi la PK è formata dall'id della ricerca e l'id del gruppo,
    // e di conseguenza il progressivo non ha alcun senso, anche perchè
    // introdurlo nella PK vuol dire creare potenziali errori dovuti al fatto
    // che per una stessa ricerca si possa inserire più volte lo stesso gruppo
    // erroneamente.
    this.elencoGruppi.addElement(record);
  }

  /**
   * Aggiunge una tabella all'elenco tabelle del contenitore
   *
   * @param record
   *        tabella da inserire
   */
  public void aggiungiArgomento(TabellaRicercaForm record) {
    record.setId(this.testata.getId());
    record.setProgressivo(this.elencoArgomenti.size());
    this.elencoArgomenti.addElement(record);
  }

  /**
   * Aggiunge un campo all'elenco campi del contenitore
   *
   * @param record
   *        campo da inserire
   */
  public void aggiungiCampo(CampoRicercaForm record) {
    record.setId(this.testata.getId());
    record.setProgressivo(Integer.toString(this.elencoCampi.size()));
    this.elencoCampi.addElement(record);
  }

  /**
   * Aggiunge una giunzione all'elenco giunzioni del contenitore
   *
   * @param record
   *        giunzione da inserire
   */
  public void aggiungiGiunzione(GiunzioneRicercaForm record) {
    record.setId(this.testata.getId());
    record.setProgressivo(Integer.toString(this.elencoGiunzioni.size()));
    this.elencoGiunzioni.addElement(record);
  }

  public void aggiungiParametro(ParametroRicercaForm record) {
    record.setId(this.testata.getId());
    record.setProgressivo(Integer.toString(this.elencoParametri.size()));
    this.elencoParametri.addElement(record);
  }

  /**
   * Aggiunge un filtro all'elenco filtri del contenitore
   *
   * @param record
   *        filtro da inserire
   */
  public void aggiungiFiltro(FiltroRicercaForm record) {
    record.setId(this.testata.getId());
    record.setProgressivo(Integer.toString(this.elencoFiltri.size()));
    this.elencoFiltri.addElement(record);
  }

  /**
   * Aggiunge un filtro all'elenco filtri del contenitore nella posizione
   * passata come argomento ed aggiorna il progressivo
   *
   * @param posizione
   * @param record
   */
  public void aggiungiFiltro(int posizione, FiltroRicercaForm record) {
    FiltroRicercaForm filtroTMP = new FiltroRicercaForm();
    record.setId(this.testata.getId());

    this.elencoFiltri.add(posizione, record);
    for (int i = posizione; i < elencoFiltri.size(); i++) {
      filtroTMP = elencoFiltri.elementAt(i);
      filtroTMP.setProgressivo("" + i);
    }
  }

  /**
   * Aggiunge un ordinamento all'elenco ordinamenti del contenitore
   *
   * @param record
   *        ordinamento da inserire
   */
  public void aggiungiOrdinamento(OrdinamentoRicercaForm record) {
    record.setId(this.testata.getId());
    record.setProgressivo(Integer.toString(this.elencoOrdinamenti.size()));
    this.elencoOrdinamenti.addElement(record);
  }

  /**
   * Elimina il gruppo individuato dalla posizione in input, dall'elenco dei
   * gruppi
   *
   * @param progressivo
   *        posizione dell'elemento da cancellare
   */
  public void eliminaGruppo(int progressivo) {
    this.elencoGruppi.removeElementAt(progressivo);
    // ovviamente, a differenza degli altri casi, il record
    // RicercaRuolo non possedendo il progressivo, l'eliminazione
    // di un elemento dall'elenco non causa la necessità di
    // reimpostare i rimanenti
  }

  /**
   * Elimina la tabella individuata dalla posizione in input, dall'elenco delle
   * tabelle
   *
   * @param progressivo
   *        posizione dell'elemento da cancellare
   */
  public void eliminaTabella(int progressivo) {
    this.elencoArgomenti.removeElementAt(progressivo);
    for (int i = 0; i < this.elencoArgomenti.size(); i++)
      (this.elencoArgomenti.elementAt(i)).setProgressivo(i);
  }

  /**
   * Elimina il campo individuato dalla posizione in input, dall'elenco dei
   * campi
   *
   * @param progressivo
   *        posizione dell'elemento da cancellare
   */
  public void eliminaCampo(int progressivo) {
    this.elencoCampi.removeElementAt(progressivo);
    for (int i = 0; i < this.elencoCampi.size(); i++)
      (this.elencoCampi.elementAt(i)).setProgressivo(Integer.toString(i));
  }

  /**
   * Elimina la giunzione individuata dalla posizione in input, dall'elenco
   * delle giunzioni
   *
   * @param progressivo
   *        posizione dell'elemento da cancellare
   */
  public void eliminaGiunzione(int progressivo) {
    this.elencoGiunzioni.removeElementAt(progressivo);
    for (int i = 0; i < this.elencoGiunzioni.size(); i++)
      (this.elencoGiunzioni.elementAt(i)).setProgressivo(Integer.toString(i));
  }

  public void eliminaParametro(int progressivo) {
    this.elencoParametri.removeElementAt(progressivo);
    for (int i = 0; i < this.elencoParametri.size(); i++)
      (this.elencoParametri.elementAt(i)).setProgressivo(Integer.toString(i));
  }

  /**
   * Elimina il filtro individuato dalla posizione in input, dall'elenco dei
   * filtri
   *
   * @param progressivo
   *        posizione dell'elemento da cancellare
   */
  public void eliminaFiltro(int progressivo) {
    this.elencoFiltri.removeElementAt(progressivo);
    for (int i = 0; i < this.elencoFiltri.size(); i++)
      (this.elencoFiltri.elementAt(i)).setProgressivo(Integer.toString(i));
  }

  /**
   * Elimina l'ordinamento individuato dalla posizione in input, dall'elenco
   * degli ordinamenti
   *
   * @param progressivo
   *        posizione dell'elemento da cancellare
   */
  public void eliminaOrdinamento(int progressivo) {
    this.elencoOrdinamenti.removeElementAt(progressivo);
    for (int i = 0; i < this.elencoOrdinamenti.size(); i++)
      (this.elencoOrdinamenti.elementAt(i)).setProgressivo(Integer.toString(i));
  }

  /**
   * Estrae il gruppo presente nella posizione in input
   *
   * @param progressivo
   *        posizione dell'elemento da estrarre
   * @return gruppo individuato dalla posizione in input
   */
  public GruppoForm estraiGruppo(int progressivo) {
    return this.elencoGruppi.elementAt(progressivo);
  }

  /**
   * Estrae la tabella presente nella posizione in input
   *
   * @param progressivo
   *        posizione dell'elemento da estrarre
   * @return tabella individuata dalla posizione in input
   */
  public TabellaRicercaForm estraiTabella(int progressivo) {
    return this.elencoArgomenti.elementAt(progressivo);
  }

  /**
   * Estrae il campo presente nella posizione in input
   *
   * @param progressivo
   *        posizione dell'elemento da estrarre
   * @return campo individuato dalla posizione in input
   */
  public CampoRicercaForm estraiCampo(int progressivo) {
    return this.elencoCampi.elementAt(progressivo);
  }

  /**
   * Estrae la giunzione presente nella posizione in input
   *
   * @param progressivo
   *        posizione dell'elemento da estrarre
   * @return giunzione individuato dalla posizione in input
   */
  public GiunzioneRicercaForm estraiGiunzione(int progressivo) {
    return this.elencoGiunzioni.elementAt(progressivo);
  }

  /**
   * Estrae il parametro presente nella posizione in input
   *
   * @param progressivo
   *        posizione dell'elemento da estrarre
   * @return parametro individuato dalla posizione in input
   */
  public ParametroRicercaForm estraiParametro(int progressivo) {
    return this.elencoParametri.elementAt(progressivo);
  }

  /**
   * Estrae il filtro presente nella posizione in input
   *
   * @param progressivo
   *        posizione dell'elemento da estrarre
   * @return filtro individuato dalla posizione in input
   */
  public FiltroRicercaForm estraiFiltro(int progressivo) {
    return this.elencoFiltri.elementAt(progressivo);
  }

  /**
   * Estrae l'ordinamento presente nella posizione in input
   *
   * @param progressivo
   *        posizione dell'elemento da estrarre
   * @return l'ordinamento individuato dalla posizione in input
   */
  public OrdinamentoRicercaForm estraiOrdinamento(int progressivo) {
    return this.elencoOrdinamenti.elementAt(progressivo);
  }

  /**
   * @return ritorna il numero di gruppi presenti nell'elenco
   */
  public int getNumeroGruppi() {
    return this.elencoGruppi.size();
  }

  /**
   * @return ritorna il numero di tabelle presenti nell'elenco
   */
  public int getNumeroTabelle() {
    return this.elencoArgomenti.size();
  }

  /**
   * Controlla se la tabella passata per argomento e' presente nell'elenco degli
   * argomenti o meno. Se la tabella e' presente, determina il numero di
   * ripetizioni di tale la tabella nell'elenco degli argomenti e restituisce
   * tale numero incrementato di 1. Se la tabella non e' gia' presente nella
   * lista degli arogomenti, allora ritorna 'null'
   *
   * @param newTabella
   *        Tabella da inserire nella lista degli argomenti
   * @return Ritorna il numero di presenze della tabella nell'elenco degli
   *         argomenti, se gia' presente, altrimenti ritorna 'null'
   */
  public Integer getMaxProgressivoTabella(TabellaRicercaForm newTabella) {
    Integer result = null;
    int max = 0;
    String str = null;
    for (int i = 0; i < this.getNumeroTabelle(); i++) {
      str = this.estraiTabella(i).getAliasTabella();
      int progr = -1;
      if (str.indexOf(newTabella.getNomeTabella()) == 0) {
        if (str.length() == newTabella.getNomeTabella().length())
          progr = 1;
        else if (UtilityNumeri.convertiIntero(str.substring(newTabella.getNomeTabella().length())) != null) {
          progr = Integer.parseInt(str.substring(newTabella.getNomeTabella().length()));
        }
      }
      if (progr > max) max = progr;
    }
    if (max > 0) {
      result = new Integer(max + 1);
    }
    return result;
  }

  /**
   * @return ritorna il numero di campi presenti nell'elenco
   */
  public int getNumeroCampi() {
    return this.elencoCampi.size();
  }

  /**
   * @return ritorna il numero di giunzioni presenti nell'elenco
   */
  public int getNumeroGiunzioni() {
    return this.elencoGiunzioni.size();
  }

  /**
   * @return ritorna il numero di parametri presenti nell'elenco
   */
  public int getNumeroParametri() {
    return this.elencoParametri.size();
  }

  public int getMaxCodiceParametro() {
    ParametroRicercaForm parametro = null;
    int max = 0;
    for (int i = 0; i < elencoParametri.size(); i++) {
      parametro = this.elencoParametri.get(i);
      if (parametro.getCodiceParametro().startsWith(
          CostantiGenRicerche.CODICE_PARAMETRO)) {
        int pos = Integer.parseInt(parametro.getCodiceParametro().substring(
            CostantiGenRicerche.CODICE_PARAMETRO.length()));
        if (pos > max) max = pos;
      }
    }
    return max;
  }

  /**
   * @return ritorna il numero di filtri presenti nell'elenco
   */
  public int getNumeroFiltri() {
    return this.elencoFiltri.size();
  }

  /**
   * @return Ritorna statoReportNelProfiloAttivo.
   */
  public boolean isStatoReportNelProfiloAttivo() {
    return statoReportNelProfiloAttivo;
  }

  /**
   * @return Ritorna statoReportNelProfiloAttivo.
   */
  public boolean getStatoReportNelProfiloAttivo() {
    return statoReportNelProfiloAttivo;
  }

  /**
   * @param statoReportNelProfiloAttivo
   *        statoReportNelProfiloAttivo da settare internamente alla classe.
   */
  public void setStatoReportNelProfiloAttivo(boolean statoReportNelProfiloAttivo) {
    this.statoReportNelProfiloAttivo = statoReportNelProfiloAttivo;
  }

  /**
   * @return ritorna il numero di ordinamenti presenti nell'elenco
   */
  public int getNumeroOrdinamenti() {
    return this.elencoOrdinamenti.size();
  }

  /**
   * Sposta il campo individuato dal progressivo in input nella posizione
   * individuata dal parametro nuovaPosizione
   *
   * @param progrElementoDaSpostare
   *        posizione del campo da spostare
   * @param nuovaPosizione
   *        nuova posizione da fare assumere all'oggetto
   */
  public void spostaCampo(int progrElementoDaSpostare, int nuovaPosizione) {
    if (progrElementoDaSpostare >= 0
        && progrElementoDaSpostare < this.elencoCampi.size()
        && nuovaPosizione >= 0
        && nuovaPosizione < this.elencoCampi.size()) {
      // spostamento elemento
      CampoRicercaForm recordDaSpostare = this.elencoCampi.remove(progrElementoDaSpostare);
      this.elencoCampi.add(nuovaPosizione, recordDaSpostare);
      int indiceInferiore = (progrElementoDaSpostare < nuovaPosizione
          ? progrElementoDaSpostare
          : nuovaPosizione);
      int indiceSuperiore = (progrElementoDaSpostare < nuovaPosizione
          ? nuovaPosizione
          : progrElementoDaSpostare);
      // aggiornamento dei progressivi compresi tra gli elementi
      // traslati/spostati
      for (int i = indiceInferiore; i <= indiceSuperiore; i++)
        (this.elencoCampi.elementAt(i)).setProgressivo(Integer.toString(i));
    }
  }

  /**
   * Scambia i campi presenti nelle posizioni in input
   *
   * @param progressivo1
   *        primo elemento
   * @param progressivo2
   *        secondo elemento
   */
  public void scambiaCampi(int progressivo1, int progressivo2) {
    if (progressivo1 >= 0
        && progressivo1 < this.elencoCampi.size()
        && progressivo2 >= 0
        && progressivo2 < this.elencoCampi.size()) {
      // estrazione elementi
      CampoRicercaForm record1 = this.elencoCampi.elementAt(progressivo1);
      CampoRicercaForm record2 = this.elencoCampi.elementAt(progressivo2);
      // swap progressivi
      record1.setProgressivo(Integer.toString(progressivo2));
      record2.setProgressivo(Integer.toString(progressivo1));
      // swap elementi
      this.elencoCampi.add(progressivo1, record2);
      this.elencoCampi.remove(progressivo1 + 1);
      this.elencoCampi.add(progressivo2, record1);
      this.elencoCampi.remove(progressivo2 + 1);
    }
  }

  /**
   * Sposta il parametro individuato dal progressivo in input nella posizione
   * individuata dall'argomento nuovaPosizione
   *
   * @param progrElementoDaSpostare
   *        posizione del filtro da spostare
   * @param nuovaPosizione
   *        nuova posizione da fare assumere all'oggetto
   */
  public void spostaParametro(int progrElementoDaSpostare, int nuovaPosizione) {
    if (progrElementoDaSpostare >= 0
        && progrElementoDaSpostare < this.elencoParametri.size()
        && nuovaPosizione >= 0
        && nuovaPosizione < this.elencoParametri.size()) {
      // spostamento elemento
      ParametroRicercaForm recordDaSpostare = this.elencoParametri.remove(progrElementoDaSpostare);
      this.elencoParametri.add(nuovaPosizione, recordDaSpostare);
      int indiceInferiore = (progrElementoDaSpostare < nuovaPosizione
          ? progrElementoDaSpostare
          : nuovaPosizione);
      int indiceSuperiore = (progrElementoDaSpostare < nuovaPosizione
          ? nuovaPosizione
          : progrElementoDaSpostare);
      // aggiornamento dei progressivi compresi tra gli elementi
      // traslati/spostati
      for (int i = indiceInferiore; i <= indiceSuperiore; i++)
        (this.elencoParametri.elementAt(i)).setProgressivo(Integer.toString(i));
    }
  }

  /**
   * Scambia i parametri presenti nelle posizioni in input
   *
   * @param progressivo1
   *        primo elemento
   * @param progressivo2
   *        secondo elemento
   */
  public void scambiaParametri(int progressivo1, int progressivo2) {
    if (progressivo1 >= 0
        && progressivo1 < this.elencoParametri.size()
        && progressivo2 >= 0
        && progressivo2 < this.elencoParametri.size()) {
      // estrazione elementi
      ParametroRicercaForm record1 = this.elencoParametri.elementAt(progressivo1);
      ParametroRicercaForm record2 = this.elencoParametri.elementAt(progressivo2);
      // swap progressivi
      record1.setProgressivo(Integer.toString(progressivo2));
      record2.setProgressivo(Integer.toString(progressivo1));
      // swap elementi
      this.elencoParametri.add(progressivo1, record2);
      this.elencoParametri.remove(progressivo1 + 1);
      this.elencoParametri.add(progressivo2, record1);
      this.elencoParametri.remove(progressivo2 + 1);
    }
  }

  /**
   * Sposta il filtro individuato dal progressivo in input nella posizione
   * individuata dal parametro nuovaPosizione
   *
   * @param progrElementoDaSpostare
   *        posizione del filtro da spostare
   * @param nuovaPosizione
   *        nuova posizione da fare assumere all'oggetto
   */
  public void spostaFiltro(int progrElementoDaSpostare, int nuovaPosizione) {
    if (progrElementoDaSpostare >= 0
        && progrElementoDaSpostare < this.elencoFiltri.size()
        && nuovaPosizione >= 0
        && nuovaPosizione < this.elencoFiltri.size()) {
      // spostamento elemento
      FiltroRicercaForm recordDaSpostare = this.elencoFiltri.remove(progrElementoDaSpostare);
      this.elencoFiltri.add(nuovaPosizione, recordDaSpostare);
      int indiceInferiore = (progrElementoDaSpostare < nuovaPosizione
          ? progrElementoDaSpostare
          : nuovaPosizione);
      int indiceSuperiore = (progrElementoDaSpostare < nuovaPosizione
          ? nuovaPosizione
          : progrElementoDaSpostare);
      // aggiornamento dei progressivi compresi tra gli elementi
      // traslati/spostati
      for (int i = indiceInferiore; i <= indiceSuperiore; i++)
        (this.elencoFiltri.elementAt(i)).setProgressivo(Integer.toString(i));
    }
  }

  /**
   * Scambia i filtri presenti nelle posizioni in input
   *
   * @param progressivo1
   *        primo elemento
   * @param progressivo2
   *        secondo elemento
   */
  public void scambiaFiltri(int progressivo1, int progressivo2) {
    if (progressivo1 >= 0
        && progressivo1 < this.elencoFiltri.size()
        && progressivo2 >= 0
        && progressivo2 < this.elencoFiltri.size()) {
      // estrazione elementi
      FiltroRicercaForm record1 = this.elencoFiltri.elementAt(progressivo1);
      FiltroRicercaForm record2 = this.elencoFiltri.elementAt(progressivo2);
      // swap progressivi
      record1.setProgressivo(Integer.toString(progressivo2));
      record2.setProgressivo(Integer.toString(progressivo1));
      // swap elementi
      this.elencoFiltri.add(progressivo1, record2);
      this.elencoFiltri.remove(progressivo1 + 1);
      this.elencoFiltri.add(progressivo2, record1);
      this.elencoFiltri.remove(progressivo2 + 1);
    }
  }

  /**
   * Sposta l'ordinamento individuato dal progressivo in input nella posizione
   * individuata dal parametro nuovaPosizione
   *
   * @param progrElementoDaSpostare
   *        posizione dell'ordinamento da spostare
   * @param nuovaPosizione
   *        nuova posizione da fare assumere all'oggetto
   */
  public void spostaOrdinamento(int progrElementoDaSpostare, int nuovaPosizione) {
    if (progrElementoDaSpostare >= 0
        && progrElementoDaSpostare < this.elencoOrdinamenti.size()
        && nuovaPosizione >= 0
        && nuovaPosizione < this.elencoOrdinamenti.size()) {
      // spostamento elemento
      OrdinamentoRicercaForm recordDaSpostare = this.elencoOrdinamenti.remove(progrElementoDaSpostare);
      this.elencoOrdinamenti.add(nuovaPosizione, recordDaSpostare);
      int indiceInferiore = (progrElementoDaSpostare < nuovaPosizione
          ? progrElementoDaSpostare
          : nuovaPosizione);
      int indiceSuperiore = (progrElementoDaSpostare < nuovaPosizione
          ? nuovaPosizione
          : progrElementoDaSpostare);
      // aggiornamento dei progressivi compresi tra gli elementi
      // traslati/spostati
      for (int i = indiceInferiore; i <= indiceSuperiore; i++)
        (this.elencoOrdinamenti.elementAt(i)).setProgressivo(Integer.toString(i));
    }
  }

  /**
   * Scambia gli ordinamenti presenti nelle posizioni in input
   *
   * @param progressivo1
   *        primo elemento
   * @param progressivo2
   *        secondo elemento
   */
  public void scambiaOrdinamenti(int progressivo1, int progressivo2) {
    if (progressivo1 >= 0
        && progressivo1 < this.elencoOrdinamenti.size()
        && progressivo2 >= 0
        && progressivo2 < this.elencoOrdinamenti.size()) {
      // estrazione elementi
      OrdinamentoRicercaForm record1 = this.elencoOrdinamenti.elementAt(progressivo1);
      OrdinamentoRicercaForm record2 = this.elencoOrdinamenti.elementAt(progressivo2);
      // swap progressivi
      record1.setProgressivo(Integer.toString(progressivo2));
      record2.setProgressivo(Integer.toString(progressivo1));
      // swap elementi
      this.elencoOrdinamenti.add(progressivo1, record2);
      this.elencoOrdinamenti.remove(progressivo1 + 1);
      this.elencoOrdinamenti.add(progressivo2, record1);
      this.elencoOrdinamenti.remove(progressivo2 + 1);
    }
  }

  /**
   * @return Ritorna eseguiDaLista.
   */
  public boolean isEseguiDaLista() {
    return eseguiDaLista;
  }

  /**
   * @param eseguiDaLista
   *        eseguiDaLista da settare internamente alla classe.
   */
  public void setEseguiDaLista(boolean eseguiDaLista) {
    this.eseguiDaLista = eseguiDaLista;
  }

//  /**
//   * Estrae l'elemento argomento corrispondente all'argomento principale della
//   * ricerca
//   *
//   * @return argomento relativo all'entit&agrave; principale della ricerca, null
//   *         se non esistono argomenti nella ricerca
//   */
//  public TabellaRicercaForm getArgomentoPrincipale() {
//    TabellaRicercaForm argomentoPrincipale = null;
//    TabellaRicercaForm tabForm = null;
//    for (int i = 0; i < this.getNumeroTabelle(); i++) {
//      tabForm = (TabellaRicercaForm) this.getElencoArgomenti().get(i);
//      if (tabForm.getAliasTabella().equals(this.testata.getEntPrinc())) {
//        argomentoPrincipale = tabForm;
//        break;
//      }
//    }
//    return argomentoPrincipale;
//  }

}