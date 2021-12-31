/*
 * Created on 29-0tt-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl;

import it.eldasoft.gene.bl.genric.GestoreVisibilitaDati;
import it.eldasoft.gene.db.domain.genric.CampoRicerca;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.gene.db.domain.genric.FiltroRicerca;
import it.eldasoft.gene.db.domain.genric.GiunzioneRicerca;
import it.eldasoft.gene.db.domain.genric.OrdinamentoRicerca;
import it.eldasoft.gene.db.domain.genric.ParametroRicerca;
import it.eldasoft.gene.db.domain.genric.TabellaRicerca;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;

import java.util.Vector;

/**
 * Classe per testare se un report base o avanzato e' visibile/eseguibile
 * rispetto al profilo attivo
 * 
 * @author Luca.Giacomazzo
 */
public class CheckReportPerProfilo {

  public static final int            REPORT_NON_ESEGUIBILE            = 0;
  public static final int            REPORT_ESEGUIBILE_NON_MODIFICATO = 1;
  public static final int            REPORT_ESEGUIBILE_MODIFICATO     = 2;

  private GestoreVisibilitaDati      gestoreVisibilita;
  private ContenitoreDatiRicerca     contenitore;
  private String                     profiloAttivo;
  private DizionarioTabelle          dizionarioTabelle;
  private DizionarioCampi            dizionarioCampi;

  public CheckReportPerProfilo(GestoreVisibilitaDati gestoreVisibilita,
      String profiloAttivo, ContenitoreDatiRicerca contenitore) {
    if (gestoreVisibilita != null && contenitore != null && profiloAttivo != null) {
      this.gestoreVisibilita = gestoreVisibilita;
      this.profiloAttivo = profiloAttivo;
      this.contenitore = contenitore;
      this.dizionarioTabelle = DizionarioTabelle.getInstance();
      this.dizionarioCampi = DizionarioCampi.getInstance();
    } else {
      String nomeVar = null;
      if (gestoreVisibilita == null)
        nomeVar = "gestoreVisibilita";
      else if (profiloAttivo == null)
        nomeVar = "profiloAttivo";
      else
        nomeVar = "contenitore";
      throw new NullPointerException(nomeVar);
    }
  }

  /**
   * Metodo per stabilire se un report e' eseguibile o meno nel profilo attivo.
   * Un report e' eseguibile se:
   * <ul>
   * <li>almeno una tabella e' visibile nel profilo attivo</li>
   * <li>se tutte le condizioni di filtro sono definite su campi visibili nel
   * profilo attivo (cioe' se il numero di filtri prima e dopo aver messo in
   * relazione il report con il profilo attivo e' rimasto lo stesso)</li>
   * <li>se la ricerca estrae almeno un campo fra i campi presenti nell'elenco
   * campi</li>
   * </ul>
   * 
   * @return Ritorna true se un report e' eseguibile, false altrimenti.
   */
  public boolean isReportEseguibile() {
    boolean resultArg = false;
    boolean resultFiltri = false;
    boolean resultCampi = false;
    // numero di filtri definiti nella ricerca prima di mettere in relazione la
    // ricerca con il profilo attivo
    int numeroFiltri = this.contenitore.getNumeroFiltri();

    // Controllo degli argomenti ed eventuale rimozione degli argomenti non
    // visibili nel profilo attivo
    resultArg = this.controlloElencoArgomenti();

    // Controllo dei filtri ed eventuale rimozione dei filtri basati su campi
    // non visibili nel profilo attivo
    if (this.controlloElencoFiltri()
        && numeroFiltri == this.contenitore.getNumeroFiltri()) {
      resultFiltri = true;
    }

    // Controllo dei campi da estrarre ed eventuale rimozione dei campi non
    // visibili nel profilo attivo
    resultCampi = this.controlloElencoCampiDaEstrarre();

    // Controllo degli ordinamenti ed eventuale rimozione degli ordinamenti
    // basati su campi non visibili nel profilo attivo
    this.controllaElencoOrdinamenti();

    return (resultArg && resultFiltri && resultCampi);
  }

  /**
   * Metodo per stabilire se un report e' importabile o meno nel profilo attivo.
   * Un report e' importabile se:
   * <ul>
   * <li>almeno una tabella e' visibile nel profilo attivo</li>
   * <li>se tutte le condizioni di filtro sono definite su campi visibili nel
   * profilo attivo (cioe' se il numero di filtri prima e dopo aver messo in
   * relazione il report con il profilo attivo e' rimasto lo stesso)</li>
   * <li>se la ricerca estrae almeno un campo fra i campi presenti nell'elenco
   * campi</li>
   * </ul>
   * 
   * @return Ritorna true se un report e' eseguibile, false altrimenti.
   */
  public boolean isReportImportabile() {
    return this.isReportEseguibile();
  }

  /**
   * Metodo per controllare se tutte le informazioni di definizione del report
   * sono visibili nel profilo attivo. Se si, l'oggetto
   * contenitoreDatiRicercaForm rimane inalterato, altrimenti lo stesso oggetto
   * risulta essere privo di tutte le informazioni non visibili nel profilo
   * attivo
   * 
   * @return Ritorna true se tutte le informazione di definizione del report
   *         sono visibili nel profilo attivo e l'oggetto
   *         contenitoreDatiRicerca rimane inalterato, altrimenti ritorna
   *         false e l'oggetto contenitoreDatiRicerca risulta essere privato
   *         di tutte le informazioni non visibili nel profilo attivo
   */
  public boolean checkReport() {
    boolean result = false;
    // numero degli argomenti definiti nella ricerca prima di mettere in
    // relazione la ricerca con il profilo attivo
    int numeroArgomenti = this.contenitore.getNumeroTabelle();
    // numero di campi definiti nella ricerca prima di mettere in relazione la
    // ricerca con il profilo attivo
    int numeroCampi = this.contenitore.getNumeroCampi();
    // numero di filtri definiti nella ricerca prima di mettere in relazione la
    // ricerca con il profilo attivo
    int numeroFiltri = this.contenitore.getNumeroFiltri();
    // numero di ordinamenti definiti nella ricerca prima di mettere in
    // relazione la ricerca con il profilo attivo
    int numeroOrdinamenti = this.contenitore.getNumeroOrdinamenti();

    // Controllo che tabelle, campi, filtri e ordinamenti sia definiti su entità
    // visibili nel profilo attivo. Se qualcosa non e' visibile viene rimosso
    // dal contenitore
    this.controlloElencoArgomenti();
    this.controlloElencoFiltri();
    this.controlloElencoCampiDaEstrarre();
    this.controllaElencoOrdinamenti();

    if (this.contenitore.getNumeroTabelle() == numeroArgomenti
        && this.contenitore.getNumeroCampi() == numeroCampi
        && this.contenitore.getNumeroFiltri() == numeroFiltri
        && this.contenitore.getNumeroOrdinamenti() == numeroOrdinamenti)
      result = true;

    return result;
  }

  /**
   * Metodo per testare se almeno un argomento del report e' visibile nel
   * profilo attivo. Se un argomento non e' visibile, allora: - l'argomento
   * viene rimosso dall'elenco degli argomenti - eventuali filtri definiti su
   * campi di tale argomento vengono rimossi - i campi estratti di tale
   * argomento vengono rimossi - gli ordinamenti definiti su campi di tale
   * argomento vengono rimossi
   * 
   * @param listaArgomenti
   *        lista delle argomenti in uso nella ricerca
   * @return Ritorna true se tutti gli argomenti della ricerca sono visibili nel
   *         profilo attivo
   */
  private boolean controlloElencoArgomenti() {
    boolean result = false;
    Vector<TabellaRicerca> elencoArgomenti = this.contenitore.getElencoArgomenti();
    DatiGenRicerca testata = this.contenitore.getDatiGenerali();
    TabellaRicerca tabella = null;
    for (int i = elencoArgomenti.size() - 1; i >= 0; i--) {
      tabella = (TabellaRicerca) elencoArgomenti.get(i);
      if (!this.checkTabella(dizionarioTabelle.get(tabella.getMneTabella()))) {
        this.removeJoinByArgomento(tabella);
        this.removeCampiByArgomento(tabella);
        this.removeFiltriByArgomento(tabella);
        this.removeOrdinamentiByArgomento(tabella);
        this.contenitore.eliminaTabella(i);

        if (tabella.getAliasTabella().equals(testata.getEntPrinc())
            && this.contenitore.getNumeroTabelle() > 0)
          testata.setEntPrinc(((TabellaRicerca) elencoArgomenti.get(0)).getAliasTabella());
        else
          testata.setEntPrinc(null);
      }
    }

    if (elencoArgomenti.size() > 0) result = true;

    return result;
  }

  /**
   * Metodo per testare se tutte le condizioni di filtro sono definite su campi
   * visibili nel profilo attivo
   * 
   * @return Ritorna true se tutti i filtri sono definiti su campi visibili nel
   *         profilo attivo, false altrimenti
   */
  private boolean controlloElencoFiltri() {
    boolean result = true;
    Vector<FiltroRicerca> elencoFiltri = this.contenitore.getElencoFiltri();
    for (int i = elencoFiltri.size() - 1; i >= 0; i--) {
      FiltroRicerca filtro = (FiltroRicerca) elencoFiltri.get(i);
      if (!this.checkFiltro(filtro)) {
        if (filtro.getTipoConfronto() != null
            && FiltroRicerca.TIPO_CONFRONTO_PARAMETRO == filtro.getTipoConfronto().shortValue()
            && filtro.getParametroConfronto() != null)
          removeParametroAssociatoAFiltro(filtro);

        this.contenitore.eliminaFiltro(i);
        result = false;
      }
    }
    return result;
  }

  /**
   * Metodo per testare se e quali campi da estrarre sono definite su campi
   * visibili nel profilo attivo
   * 
   * @return Ritorna true se almeno un campo da estrarre e' visibile nel profilo
   *         attivo, false altrimenti
   */
  private boolean controlloElencoCampiDaEstrarre() {
    Vector<CampoRicerca> elencoCampi = this.contenitore.getElencoCampi();
    for (int i = elencoCampi.size() - 1; i >= 0; i--) {
      CampoRicerca campoRicerca = (CampoRicerca) elencoCampi.get(i);
      Campo campo = this.dizionarioCampi.get(campoRicerca.getMneCampo());
      if (!this.checkCampo(campo)) {
        this.contenitore.eliminaCampo(i);
        this.removeOrdinamentiByCampo(campo);
      }
    }
    return elencoCampi.size() > 0;
  }

  /**
   * Metodo per testare se e quali ordinamenti sono definiti su campi visibili
   * nel profilo attivo. Qualora un ordinamento sia definito su un campo non
   * visibile nel profilo attivo, allora tale ordinamento viene rimosso
   * dall'elenco degli ordinamenti
   */
  private void controllaElencoOrdinamenti() {
    Vector<OrdinamentoRicerca> elencoOrdinamenti = this.contenitore.getElencoOrdinamenti();
    for (int i = elencoOrdinamenti.size() - 1; i >= 0; i--) {
      OrdinamentoRicerca ordinamento = (OrdinamentoRicerca) elencoOrdinamenti.get(i);
      if (!this.checkOrdinamento(ordinamento))
        elencoOrdinamenti.remove(i);
    }
  }

  /**
   * Metodo per controllare se una tabella e' visibile nel profilo attivo
   * 
   * @param tabella
   * @return Ritorna true se la tabella e' visibile nel profilo attivo, false
   *         altrimenti
   */
  private boolean checkTabella(Tabella tabella) {
    return this.gestoreVisibilita.checkEntitaVisibile(tabella, this.profiloAttivo);
  }

  /**
   * Metodo per controllare se in campo e' visibile nel profilo attivo
   * 
   * @param campo
   * @return Ritorna true se il campo e' visibile nel profilo attivo, false
   *         altrimenti
   */
  private boolean checkCampo(Campo campo) {
    return this.gestoreVisibilita.checkCampoVisibile(campo, this.profiloAttivo);
  }

  /**
   * Metodo per controllare se una condizione di filtro usa campi visibili dal
   * profilo attivo
   * 
   * @param filtro
   * @return Ritorna true se il filtro usa campi visibili dal profilo attivo,
   *         false altrimenti
   */
  private boolean checkFiltro(FiltroRicerca filtro) {
    boolean result = true;

    // i controlli li faccio solo su filtri in cui si usano i campi,
    // non filtri costituiti solo da 1 operatore logico o di parentesi
    if (filtro.getMnemonicoCampo() != null) {
      Campo campo = this.dizionarioCampi.get(filtro.getMnemonicoCampo());
      if (filtro.getMnemonicoCampoConfronto() != null) {
        Campo campoConfronto = this.dizionarioCampi.get(filtro.getMnemonicoCampoConfronto());
        result = this.checkCampo(campo) && this.checkCampo(campoConfronto);
      } else {
        result = this.checkCampo(campo);
      }
    }

    return result;
  }

  /**
   * Metodo per controllare se un ordinamento usa un campo visibile dal profilo
   * attivo
   * 
   * @param ordinamento
   * @return Ritorna true se l'ordinamento usa un campo visibile dal profilo
   *         attivo, false altrimenti
   */
  private boolean checkOrdinamento(OrdinamentoRicerca ordinamento) {
    Campo campo = this.dizionarioCampi.get(ordinamento.getMnemonicoCampo());
    return this.checkCampo(campo);
  }

  /**
   * Metodo per rimuovere le join, in seguito alla rimozione di una tabella
   * 
   * @param tabella
   *        tabella in cancellazione dalla definizione della ricerca
   */
  private void removeJoinByArgomento(TabellaRicerca tabella) {
    Vector<GiunzioneRicerca> elencoJoin = this.contenitore.getElencoGiunzioni();
    for (int i = elencoJoin.size() - 1; i >= 0; i--) {
      GiunzioneRicerca join = (GiunzioneRicerca) elencoJoin.get(i);
      if (tabella.getMneTabella().equals(join.getMnemonicoTabella1())
          || tabella.getMneTabella().equals(join.getMnemonicoTabella2()))
        this.contenitore.eliminaGiunzione(i);
    }
  }

  /**
   * Metodo per rimuovere i campi da estrarre dalla ricerca, in seguito alla
   * rimozione di una tabella, a partire dal nome della tabella
   * 
   * @param tabella
   *        tabella in cancellazione dalla definizione della ricerca
   */
  private void removeCampiByArgomento(TabellaRicerca tabella) {
    Vector<CampoRicerca> elencoCampi = this.contenitore.getElencoCampi();

    for (int i = elencoCampi.size() - 1; i >= 0; i--) {
      CampoRicerca campo = (CampoRicerca) elencoCampi.get(i);
      if (campo.getMneTabella().equals(tabella.getMneTabella()))
        this.contenitore.eliminaCampo(i);
    }
  }

  /**
   * Metodo per rimuovere le condizioni di filtro definite nella ricerca a
   * partire da una tabella, in seguito alla rimozione di una tabella
   * 
   * @param tabella
   *        tabella in cancellazione dalla definizione della ricerca
   */
  private void removeFiltriByArgomento(TabellaRicerca tabella) {
    Vector<FiltroRicerca> elencoFiltri = this.contenitore.getElencoFiltri();
    for (int i = elencoFiltri.size() - 1; i >= 0; i--) {
      FiltroRicerca filtro = (FiltroRicerca) elencoFiltri.get(i);
      if (tabella.getMneTabella().equals(filtro.getMnemonicoTabella())) {
        if (filtro.getTipoConfronto() != null
            && FiltroRicerca.TIPO_CONFRONTO_PARAMETRO == filtro.getTipoConfronto().shortValue()
            && filtro.getParametroConfronto() != null)
          removeParametroAssociatoAFiltro(filtro);

        this.contenitore.eliminaFiltro(i);
      }
    }
  }

  /**
   * Metodo per rimuovere il parametro associato ad un filtro in cancellazione.
   * Il parametro viene rimosso se esso e' usato una sola volta
   * 
   * @param filtro
   *        filtro in cancellazione dalla definizione della ricerca
   */
  private void removeParametroAssociatoAFiltro(FiltroRicerca filtro) {
    // eliminazione del parametro, se presente, e utilizzato solo in questo
    // filtro
    String codiceParametro = filtro.getParametroConfronto();
    short contatoreUtilizzi = 0;
    FiltroRicerca filtroTmp = null;
    // verifico quante volte viene utilizzato
    for (int j = 0; j < contenitore.getNumeroFiltri(); j++) {
      filtroTmp = contenitore.estraiFiltro(j);
      if (filtroTmp.getTipoConfronto() != null
          && FiltroRicerca.TIPO_CONFRONTO_PARAMETRO == filtroTmp.getTipoConfronto().shortValue()
          && codiceParametro.equals(filtroTmp.getParametroConfronto())) {
        contatoreUtilizzi++;
      }
    }
    // se viene utilizzato una sola volta, ovvero viene utilizzato solo
    // nel filtro da eliminare, allora elimino anche il parametro
    if (contatoreUtilizzi == 1) {
      ParametroRicerca parametro = null;
      int indiceParametroDaEliminare = 0;
      boolean trovato = false;
      while (indiceParametroDaEliminare < contenitore.getNumeroParametri()
          && !trovato) {
        parametro = contenitore.estraiParametro(indiceParametroDaEliminare);
        if (codiceParametro.equals(parametro.getCodice())) {
          trovato = true;
        } else {
          indiceParametroDaEliminare++;
        }
      }
      this.contenitore.eliminaParametro(indiceParametroDaEliminare);
    }
  }

  /**
   * Metodo per rimuovere gli ordinamenti definiti nella ricerca a partire da
   * una tabella, in seguito alla rimozione della tabella stessa
   * 
   * @param tabella
   *        tabella in cancellazione dalla definizione della ricerca
   */
  private void removeOrdinamentiByArgomento(TabellaRicerca tabella) {
    Vector<OrdinamentoRicerca> elencoOrdinamenti = this.contenitore.getElencoOrdinamenti();
    for (int i = elencoOrdinamenti.size() - 1; i >= 0; i--) {
      OrdinamentoRicerca ordinamento = (OrdinamentoRicerca) elencoOrdinamenti.get(i);
      if (ordinamento.getMnemonicoTabella().equals(
          tabella.getMneTabella()))
        this.contenitore.eliminaOrdinamento(i);
    }
  }

  /**
   * Metodo per rimuovere gli ordinamenti definiti nella ricerca a partire da un
   * campo, in seguito alla rimozione del campo stesso
   * 
   * @param campo
   *        campo da rimuovere dalla definizione della ricerca
   */
  private void removeOrdinamentiByCampo(Campo campo) {
    Vector<OrdinamentoRicerca> elencoOrdinamenti = this.contenitore.getElencoOrdinamenti();
    for (int i = elencoOrdinamenti.size() - 1; i >= 0; i--) {
      OrdinamentoRicerca ordinamento = (OrdinamentoRicerca) elencoOrdinamenti.get(i);
      if (ordinamento.getMnemonicoCampo().equals(campo.getCodiceMnemonico()))
        this.contenitore.eliminaOrdinamento(i);
    }
  }

}