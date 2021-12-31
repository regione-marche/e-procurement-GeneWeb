package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionInterface;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore CRUD di entità
 *
 * @author cit_franceschin
 */
public abstract class AbstractGestoreEntita extends AbstractGestoreBase {

  private static Logger    logger            = Logger.getLogger(AbstractGestoreEntita.class);

  /**
   * Variabile booleana per indicare se il gestore che si va a creare è un
   * gestore standard (gestore di una entita') o un gestore di appoggio alla
   * scheda in salvataggio.
   */
  private final boolean          isGestoreStandard;

  protected SqlManager     sqlManager;

  protected GeneManager    geneManager;

  /** Numero della pagina corrente */
  private int              numPg             = -1;

  /** Flag per fermare il processo attivo */
  private boolean          stopProcess;

  private ActionInterface  action            = null;

  private UploadFileForm   form              = null;

  protected ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /**
   * Costruttore dell'oggetto come gestore standard di entita
   */
  public AbstractGestoreEntita() {
    this(true);
  }

  /**
   * Costruttore dell'oggetto con possibilita' di scegliere se creare un gestore
   * di entita standard o meno, attraverso il parametro isGestoreStandard.
   *
   * La differenza tra gestore standard e non sta nel fatto che il primo
   * gestisce interamente le operazioni di insert/update/delete dei dati di
   * un'entita' e dei campi del generatore attributi associati all'entita' di
   * partenza, mentre il secondo e' un gestore di appoggio usato per preparare i
   * dati presenti nelle schede con sezioni dinamiche. Tali dati preparati
   * devono essere passati poi ad un gestore standard per le operazioni di
   * insert/update/delete.
   *
   * Per un esempio d'uso vedere l'implementazione della pagina Categorie
   * d'iscrizione nell'archivio delle imprese (impr.categorieIscrzione.jsp) e in
   * particolare il suo gestore (GestoreCATEMultiplo.java)
   *
   * @param isGestoreStandard
   */
  public AbstractGestoreEntita(boolean isGestoreStandard) {
    this.isGestoreStandard = isGestoreStandard;
  }

  /**
   * @return ritorna l'entità principale per la quale gestire le operazioni di
   *         CRUD.
   */
  public abstract String getEntita();

  /**
   * @return Ritorna geneManager.
   */
  public GeneManager getGeneManager() {
    return geneManager;
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Aggiungo l'estrazione del numero della pagina
    String numeroPagina = UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE);
    // Aggiunta del settaggio del numero di pagina
    this.numPg = -1;
    if (numeroPagina != null && numeroPagina.length() > 0) {
      this.numPg = new Integer(numeroPagina).intValue();
    }
    // Estraggo il manager per gestire diversi SQL
    this.sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
    this.geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
        this.getServletContext(), GeneManager.class);
  }

  /**
   * Funzione che elimina l'elemento. In automatico viene richiamata la funzione
   * preDelete dello stesso elemento
   *
   * @param aStatus
   *        Se a null allora significa che deve gestire la transazione come
   *        singola altrimenti utilizza lo status per continuare una transazione
   *        gia iniziata
   * @param keys
   *        Array di tipo
   *
   * @throws GestoreException
   */
  public void elimina(TransactionStatus aStatus, DataColumnContainer impl)
      throws GestoreException {

    ProfiloUtente profiloUtente = (ProfiloUtente) this.getRequest().getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    OpzioniUtente opzioniUtente = new OpzioniUtente( profiloUtente.getFunzioniUtenteAbilitate());

    if(opzioniUtente.isOpzionePresente(
        CostantiGeneraliAccount.OPZIONI_BLOCCO_ELIMINAZIONE_ENTITA_PRINCIPALE) &&
        ConfigManager.getValore(CostantiGenerali.PROP_ENTITA_BLOCCATE_ELIMINAZIONE)!=null){
      //Si deve controllare se l'entità è bloccata in modifica
      String elencoEntita= ConfigManager.getValore(CostantiGenerali.PROP_ENTITA_BLOCCATE_ELIMINAZIONE);
      if(elencoEntita!=null){
        elencoEntita= elencoEntita.toUpperCase();
        String vettoreEntita[] = elencoEntita.split(";");
        if(vettoreEntita.length>0){
          for(int i=0;i<vettoreEntita.length;i++){
            if(vettoreEntita[i].equals(this.getEntita())){
              throw new GestoreException("Errore in eliminazione dell'entità "
                  + this.getEntita()
                  + ":"
                  + "l'entita non è eliminabile in quanto ne risulta inibita l'eliminazione per l'utente!", "entitaBloccata",new String[]{this.getEntita()}, new Exception());
            }
          }
        }
      }
    }

    // Creo la sintassi per l'eliminazione della riga
    // Come prima cosa verifico che il vettore contenga i valori delle
    // chiavi
    if (impl == null || impl.getColonne().size() == 0)
      throw new GestoreException("Errore in eliminazione dell'entità "
          + this.getEntita()
          + ":"
          + "chiavi non definite !", "elimina.nokey");
    boolean transazioneNonInizializzata = aStatus == null;
    TransactionStatus status = null;
    try {
      if (transazioneNonInizializzata)
        status = this.sqlManager.startTransaction();
      // Creo la classe che gestisce le colonne con valore
      this.stopProcess = false;
      // {MF111007} Spostato il predelete prima dell'effettiva eliminazione
      // (così posso eseguire select sulla riga prima dell'eliminazione)
      this.preDelete(status, impl);

      // Creo l'sql per l'eliminazione dell'elemento
      if (this.isGestoreStandard) {
        // {MF131107} Aggiunta delle gestione delle entità dinamiche
        this.geneManager.preDeleteGestore(this.getEntita(), impl);
        impl.delete(this.getEntita(), this.sqlManager);
        this.afterDeleteEntita(status, impl);
      }

      if (!this.stopProcess) {
        // Se devo committo la transazione
        if (transazioneNonInizializzata) {
          this.sqlManager.commitTransaction(status);
        }
        if (this.isGestoreStandard) this.postDelete(impl);
      }
    } catch (GestoreException e) {
      e.prependCodice("delete");
      logger.error(
          resBundleGenerale.getString("errors.database.dataAccessException"), e);
      try {
        this.sqlManager.rollbackTransaction(status);
      } catch (SQLException e1) {
      }
      throw e;
    } catch (Throwable t) {
      logger.error(
          resBundleGenerale.getString("errors.database.dataAccessException"), t);
      try {
        this.sqlManager.rollbackTransaction(status);
      } catch (SQLException e) {
      }
      throw new GestoreException("Errore durante l'eliminazione in "
          + this.getEntita(), "elimina", t);
    }

  }

  /**
   * Funzione che chiama l'eliminazione sul gestore passato come parametro. Se
   * non è settato ancora il request allora lo imposta
   *
   * @param aStatus
   * @param impl
   * @param gestore
   * @throws GestoreException
   */
  public void elimina(TransactionStatus aStatus, DataColumnContainer impl,
      AbstractGestoreEntita gestore) throws GestoreException {
    if (gestore.getRequest() == null) gestore.setRequest(this.getRequest());
    gestore.elimina(aStatus, impl);
  }

  /**
   * Permette di aggiungere azioni ad un'operazione di eliminazione, dentro la transazione ma dopo l'eliminazione dell'entit&agrave;
   * principale.
   *
   * @param status
   *        Stato della transazione
   * @param datiForm
   *        Elenco dei campi del form della pagina che ha inoltrato la richiesta
   * @throws GestoreException
   */
  public void afterDeleteEntita(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
  }

  /**
   * Funzione che esegue l'inserimento nella tabella partendo dalle colonne
   *
   * @param aStatus
   *        Se a null allora significa che deve gestire la transazione come
   *        singola altrimenti utilizza lo status per continuare una transazione
   *        gia iniziata
   * @param colonne
   *        Elenco colonne che devono essere inserite
   *
   * @throws GestoreException
   */
  public void inserisci(TransactionStatus aStatus, DataColumnContainer impl)
      throws GestoreException {
    // ///////////////////////////////////////////////////////////////
    // Modifiche
    // ----------------
    // 13/02/2007 M.F. Aggiunta delle gestione del settaggio dell'identificativo
    // del gruppo se la tabella ha proprietà
    // ///////////////////////////////////////////////////////////////
    TransactionStatus status = null;
    boolean transazioneNonInizializzata = aStatus == null;
    try {
      // Setto tutti i valori originali come nuovo
      for (Iterator iter = impl.getColonne().keySet().iterator(); iter.hasNext();) {
        Object obj = impl.getColonne().get(iter.next());
        if (obj instanceof DataColumn) {
          DataColumn col = (DataColumn) obj;
          // Setto a nulla il valore originale solo per l'entità in inserimento
          // (controllo che la colonna sia relativa ad una tabella reale e non
          // sia un campo fittizio, nonchè sia relativa alla tabella in uso per
          // l'inserimento)
          if (col.getTable() != null
              && this.getEntita().equalsIgnoreCase(col.getTable().getName()))
            col.setObjectOriginalValue(null);
        }
      }

      // Estraggo solo i campi della tabella voluta
      if (transazioneNonInizializzata)
        status = this.sqlManager.startTransaction();
      else
        status = aStatus;

      this.stopProcess = false;
      // Chiamo l'eventuale nuova gestione
      this.preInsert(status, impl);

      // {MF131107} Aggiunta delle gestione delle entità dinamiche
      if (!stopProcess) {
        if (this.isGestoreStandard) {
          impl.insert(this.getEntita(), this.sqlManager);
          this.geneManager.preInsertGestore(this.getEntita(), impl);
          this.afterInsertEntita(status, impl);
        }

        if (transazioneNonInizializzata)
          this.sqlManager.commitTransaction(status);

        // Chiamo il post insert
        if (this.isGestoreStandard) this.postInsert(impl);
      } else if (transazioneNonInizializzata)
        this.sqlManager.rollbackTransaction(status);

    } catch (GestoreException e) {
      logger.error(
          resBundleGenerale.getString("errors.database.dataAccessException"), e);
      try {
        if (transazioneNonInizializzata)
          this.sqlManager.rollbackTransaction(status);
      } catch (SQLException e1) {
      }
      e.prependCodice("insert");
      throw e;

    } catch (Throwable t) {
      logger.error(
          resBundleGenerale.getString("errors.database.dataAccessException"), t);
      try {
        if (transazioneNonInizializzata)
          this.sqlManager.rollbackTransaction(status);
      } catch (SQLException e) {
      }
      throw new GestoreException("Errore nell'inserimento in "
          + this.getEntita()
          + "\n"
          + t.getMessage(), "inserimento", t);
    }
  }

  /**
   * Funzione che esegue l'inserimento utilizzando un gestore precreato
   *
   * @param aStatus
   * @param impl
   * @param gestore
   * @throws GestoreException
   */
  public void inserisci(TransactionStatus aStatus, DataColumnContainer impl,
      AbstractGestoreEntita gestore) throws GestoreException {
    if (gestore.getRequest() == null) gestore.setRequest(this.getRequest());
    gestore.inserisci(aStatus, impl);
  }

  /**
   * Permette di aggiungere azioni ad un'operazione di inserimento, dentro la transazione ma dopo l'inserimento dell'entit&agrave;
   * principale.
   *
   * @param status
   *        Stato della transazione
   * @param datiForm
   *        Elenco dei campi del form della pagina che ha inoltrato la richiesta
   * @throws GestoreException
   */
  public void afterInsertEntita(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
  }

  /**
   * Funzione che si incarica si eseguire l'update in un entità
   *
   * @param aStatus
   *        Se a null allora significa che deve gestire la transazione come
   *        singola altrimenti utilizza lo status per continuare una transazione
   *        gia iniziata
   * @param colonne
   *        Elenco colonne per l'update
   * @throws GestoreException
   */
  public void update(TransactionStatus aStatus, DataColumnContainer impl)
      throws GestoreException {
    TransactionStatus status = null;
    boolean transazioneNonInizializzata = aStatus == null;
    try {
      if (transazioneNonInizializzata)
        status = this.sqlManager.startTransaction();
      else
        status = aStatus;
      // Creo il gestore delle colonne
      this.stopProcess = false;
      // Chiamo il nuovo evento di personalizzazione
      this.preUpdate(status, impl);

      if (!this.stopProcess) {
        if (this.isGestoreStandard) {
          impl.update(this.getEntita(), this.sqlManager);
          // {MF131107} Aggiunta delle gestione delle entità dinamiche
          this.geneManager.preUpdateGestore(this.getEntita(), impl);
          this.afterUpdateEntita(status, impl);
        }

        if (transazioneNonInizializzata)
          this.sqlManager.commitTransaction(status);
        if (this.isGestoreStandard) this.postUpdate(impl);

      } else if (transazioneNonInizializzata)
        this.sqlManager.rollbackTransaction(status);
    } catch (GestoreException e) {
      logger.error(
          resBundleGenerale.getString("errors.database.dataAccessException"), e);
      try {
        if (transazioneNonInizializzata)
          this.sqlManager.rollbackTransaction(status);
      } catch (SQLException e1) {
      }
      e.prependCodice("update");
      throw e;
    } catch (Throwable t) {
      logger.error(
          resBundleGenerale.getString("errors.database.dataAccessException"), t);
      try {
        if (transazioneNonInizializzata)
          this.sqlManager.rollbackTransaction(status);
      } catch (SQLException e) {
      }
      throw new GestoreException("Errore nell'update di "
          + this.getEntita()
          + "\n"
          + t.getMessage(), "update", t);
    }
  }

  /**
   * Funzione che esegue l'update utilizzando un gestore precreato
   *
   * @param aStatus
   * @param impl
   * @param gestore
   * @throws GestoreException
   */
  public void update(TransactionStatus aStatus, DataColumnContainer impl,
      AbstractGestoreEntita gestore) throws GestoreException {
    if (gestore.getRequest() == null) gestore.setRequest(this.getRequest());
    gestore.update(aStatus, impl);
  }

  /**
   * Permette di aggiungere azioni ad un'operazione di aggiornamento, dentro la transazione ma dopo l'aggiornamento dell'entit&agrave;
   * principale.
   *
   * @param status
   *        Stato della transazione
   * @param datiForm
   *        Elenco dei campi del form della pagina che ha inoltrato la richiesta
   * @throws GestoreException
   */
  public void afterUpdateEntita(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
  }

 /**
   * @return Returns the sqlManager.
   */
  public SqlManager getSqlManager() {
    return this.sqlManager;
  }

  /**
   * @return Returns the numPg.
   */
  public int getNumPg() {
    return this.numPg;
  }

  public boolean isStopProcess() {
    return this.stopProcess;
  }

  public void setStopProcess(boolean stopProcess) {
    this.stopProcess = stopProcess;
  }

  public ActionInterface getAction() {
    return this.action;
  }

  public void setAction(ActionInterface action) {
    this.action = action;
  }

  /**
   * @return Ritorna form.
   */
  public UploadFileForm getForm() {
    return form;
  }

  /**
   * @param form form da settare internamente alla classe.
   */
  public void setForm(UploadFileForm form) {
    this.form = form;
  }

  /**
   * Verifica la presenza di una configurazione di default attribuita all'utente
   * da usare in fase di creazione di un'entità, quindi crea un'occorrenza nella
   * G_PERMESSI in modo tale da rendere visibile l'entità ad altri utenti,
   * oppure crea la sola occorrenza per se stesso
   *
   * @param impl
   *        gestore delle informazioni ricevute nel request
   * @param campoChiave
   *        campo chiave dell'entità, definita anche come colonna nella
   *        G_PERMESSI
   * @param predefinita
   *        Occorrenza relativa alla configur.predefinita (1=Lavori, 2=Gare)
   *        Valorizzare con null se non necessario
   * @throws GestoreException
   */
  public void inserisciPermessi(DataColumnContainer impl, String campoChiave,
      Integer predefinita) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("inserisciPermessi(status,impl,'"
          + campoChiave
          + "'): inizio metodo");

    String valoreChiave = impl.getString(this.getEntita() + "." + campoChiave);

    ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    // INSERIMENTO UTENTE PROPRIETARIO
    this.insertPermesso(new Long(profilo.getId()), campoChiave, valoreChiave,
        new Integer(1), "1", null, null);

    // INSERIMENTO UTENTI CONFIGURATI
    // si estraggono le definizioni della configurazione
    String sql = null;
    Object[] paramSql = null;
    if (predefinita != null) {
      sql = "select syscon, autori, propri "
          + "from g_permessi "
          + "where sysrif = ? "
          + "and predef = ? "
          + "and "
          + campoChiave
          + " is null";
      paramSql = new Object[] { new Integer(profilo.getId()), predefinita };
    } else {
      sql = "select syscon, autori, propri "
          + "from g_permessi "
          + "where sysrif = ? "
          + "and predef is null "
          + "and "
          + campoChiave
          + " is null";
      paramSql = new Object[] { new Integer(profilo.getId()) };
    }
    try {
      List listaDefinizioni = this.sqlManager.getListVector(sql, paramSql);

      Long idUtente = null;
      Integer tipoAutorizzazione = null;
      String isProprietario = null;
      // si cicla sulle definizioni e si inseriscono tutte le occorrenze
      // estratte dalla configurazione come valori di istanza per l'entità
      // creata
      if (listaDefinizioni != null && listaDefinizioni.size() > 0) {
        for (int i = 0; i < listaDefinizioni.size(); i++) {
          idUtente = SqlManager.getValueFromVectorParam(
              listaDefinizioni.get(i), 0).longValue();
          tipoAutorizzazione = new Integer(SqlManager.getValueFromVectorParam(
              listaDefinizioni.get(i), 1).longValue().intValue());
          isProprietario = SqlManager.getValueFromVectorParam(
              listaDefinizioni.get(i), 2).stringValue();
          this.insertPermesso(idUtente, campoChiave, valoreChiave,
              tipoAutorizzazione, isProprietario, null, null);
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'estrazione della configurazione dei permessi "
              + "(utente="
              + profilo.getId()
              + ", chiave entita='"
              + campoChiave
              + "', valore chiave='"
              + valoreChiave
              + "')", "getPermessi", e);
    }
    if (logger.isDebugEnabled())
      logger.debug("inserisciPermessi(status,impl,'"
          + campoChiave
          + "'): fine metodo");
  }

  /**
   * Ritorna l'ultimo id generato per la tabella G_PERMESSI
   *
   * @return ultimo id generato, 0 altrimenti
   * @throws GestoreException
   */
  private long getMaxIdGPermessi() throws GestoreException {
    long id = 0;
    try {
      Vector ret = this.sqlManager.getVector(
          "select max(numper) from g_permessi", new Object[] {});
      if (ret.size() > 0) {
        Long count = SqlManager.getValueFromVectorParam(ret, 0).longValue();
        if (count != null && count.longValue() > 0) {
          id = count.longValue();
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'estrazione dell'ultimo id utilizzato nella G_PERMESSI",
          "getMaxIdPermessi", e);
    }
    return id;
  }

  /**
   * Inserisce una riga nella tabella G_PERMESSI
   *
   * @param idUtente
   *        identificativo dell'utente
   * @param campoChiave
   *        colonna che costituisce la chiave dell'entità
   * @param valoreChiave
   *        valore della chiave
   * @param tipoAutorizzazione
   *        tipo di autorizzazione (Lettura=2,Modifica=1)
   * @param isProprietario
   *        L'utente ha il privilegio di proprietario?
   * @param predefinita
   *        Occorrenza relativa alla configur.predefinita (1=Lavori, 2=Gare)
   * @param proprietarioPredefinita
   *        Codice utente proprietario della configurazione predefinita
   * @throws GestoreException
   */
  private void insertPermesso(Long idUtente, String campoChiave,
      String valoreChiave, Integer tipoAutorizzazione, String isProprietario,
      Integer predefinita, Long proprietarioPredefinita)
      throws GestoreException {
    try {
      // si inserisce l'utente solo se non esiste l'associazione nella
      // G_PERMESSI con l'entita'
      Vector ret = this.sqlManager.getVector(
          "select count(numper) from g_permessi where "
              + campoChiave
              + " = ? and syscon = ?", new Object[] { valoreChiave, idUtente });
      if (ret.size() > 0) {
        Long count = SqlManager.getValueFromVectorParam(ret, 0).longValue();
        if (count != null && count.longValue() == 0) {
          // non esiste, quindi tento l'inserimento
          long id = this.getMaxIdGPermessi() + 1;
          String sql = "insert into g_permessi (numper, syscon, "
              + campoChiave
              + ", autori, propri, predef, sysrif) values (?, ?, ?, ?, ?, ?, ?)";
          this.sqlManager.update(sql, new Object[] { new Long(id), idUtente,
              valoreChiave, tipoAutorizzazione, isProprietario, predefinita,
              proprietarioPredefinita }, 1);
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'inserimento di un'occorrenza nella G_PERMESSI",
          "insertPermesso", e);
    }
  }

  /*****************************************************************************
   * Metodi astratti da implementare nei vari gestori per gestire gli
   * aggiornamenti
   ****************************************************************************/

  /**
   * Metodo richiamato prima dell'eliminazione fisica di un elemento,
   * all'interno della stessa transazione aperta per l'eliminazione
   *
   * @param status
   *        Stato della transazione
   * @param datiForm
   *        Elenco dei campi del form della pagina che ha inoltrato la richiesta
   * @throws GestoreException
   */
  public abstract void preDelete(TransactionStatus status,
      DataColumnContainer datiForm) throws GestoreException;

  /**
   * Metodo richiamato dopo dell'eliminazione fisica di un elemento, dopo la
   * chiusura della transazione aperta per l'eliminazione.<br>
   * L'operazione non è associata ad una transazione, nel caso sia necessario va
   * aperta e chiusa all'interno del metodo stesso
   *
   * @param datiForm
   *        Elenco dei campi del form della pagina che ha inoltrato la richiesta
   * @throws GestoreException
   */
  public abstract void postDelete(DataColumnContainer datiForm)
      throws GestoreException;

  /**
   * Metodo richiamato prima dell'inserimento fisico di un elemento, all'interno
   * della stessa transazione aperta per l'inserimento
   *
   * @param status
   *        Stato della transazione
   * @param datiForm
   *        Elenco dei campi del form della pagina che ha inoltrato la richiesta
   * @throws GestoreException
   */
  public abstract void preInsert(TransactionStatus status,
      DataColumnContainer datiForm) throws GestoreException;

  /**
   * Metodo richiamato dopo l'inserimento fisico di un elemento, dopo la
   * chiusura della transazione aperta per l'inserimento.<br>
   * L'operazione non è associata ad una transazione, nel caso sia necessario va
   * aperta e chiusa all'interno del metodo stesso
   *
   * @param datiForm
   *        Elenco dei campi del form della pagina che ha inoltrato la richiesta
   * @throws GestoreException
   */
  public abstract void postInsert(DataColumnContainer datiForm)
      throws GestoreException;

  /**
   * Metodo richiamato prima dell'aggiornamento fisico di un elemento,
   * all'interno della stessa transazione aperta per l'aggiornamento
   *
   * @param status
   *        Stato della transazione
   * @param datiForm
   *        Elenco dei campi del form della pagina che ha inoltrato la richiesta
   * @throws GestoreException
   */
  public abstract void preUpdate(TransactionStatus status,
      DataColumnContainer datiForm) throws GestoreException;

  /**
   * Metodo richiamato dopo l'aggiornamento fisico di un elemento, dopo la
   * chiusura della transazione aperta per l'aggiornamento.<br>
   * L'operazione non è associata ad una transazione, nel caso sia necessario va
   * aperta e chiusa all'interno del metodo stesso
   *
   * @param datiForm
   *        Elenco dei campi del form della pagina che ha inoltrato la richiesta
   * @throws GestoreException
   */
  public abstract void postUpdate(DataColumnContainer datiForm)
      throws GestoreException;

  /**
   * Gestisce le operazioni di update, insert, delete dei dettagli dei record di
   * una scheda multipla.<br>
   * Questo codice funziona se si presuppone di avere una pagina con un elenco
   * di record a scheda multipla,
   * <ul>
   * <li>la cui chiave è una parte fissa comune più un progressivo</li>
   * <li>i dati da aggiornare sono appartenenti ad una sola entità</li>
   * </ul>
   *
   * @param status
   *        stato della transazione
   * @param dataColumnContainer
   *        container di partenza da cui filtrare i record
   * @param gestore
   *        gestore a chiave numerica per l'aggiornamento del record di una
   *        scheda multipla
   * @param suffissoContaRecord
   *        suffisso da concatenare a "NUMERO_" per ottenere il campo che indica
   *        il numero di occorrenze presenti nel container
   * @param valoreChiave
   *        parte non numerica della chiave dell'entità, per la valorizzazione
   *        in fase di inserimento se i dati non sono presenti nel container
   * @param nomeCampoDelete
   *        campo utilizzato per marcare la delete di un record della scheda
   *        multipla
   * @param campiDaNonAggiornare
   *        elenco eventuale di ulteriori campi fittizi da eliminare prima di
   *        eseguire l'aggiornamento nel DB
   *
   * @throws GestoreException
   */
  public void gestisciAggiornamentiRecordSchedaMultipla(
      TransactionStatus status, DataColumnContainer dataColumnContainer,
      AbstractGestoreChiaveNumerica gestore, String suffisso,
      DataColumn[] valoreChiave, String[] campiDaNonAggiornare)
      throws GestoreException {

    ////////////////////////////////////////////////////////////////
    // ATTENZIONE: METODO CON UTILIZZO ID MAX + 1 (TRADIZIONALE)!!!!
    ////////////////////////////////////////////////////////////////

    String nomeCampoNumeroRecord = "NUMERO_" + suffisso;
    String nomeCampoDelete = "DEL_" + suffisso;
    String nomeCampoMod = "MOD_" + suffisso;

    // Gestione delle pubblicazioni bando solo se esiste la colonna con il
    // numero di occorrenze
    if (dataColumnContainer.isColumn(nomeCampoNumeroRecord)) {

      // Estraggo dal dataColumnContainer tutte le occorrenze dei campi
      // dell'entità definita per il gestore
      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
          dataColumnContainer.getColumns(gestore.getEntita(), 0));

      int numeroRecord = dataColumnContainer.getLong(nomeCampoNumeroRecord).intValue();

      // Sabbadin 07/12/2011: spostato fuori dal ciclo questo controllo in modo
      // da fare una volta sola la verifica e l'append dell'entita' (SE
      // NECESSARIA) al nome di campo da non aggiornare
      if (campiDaNonAggiornare != null) {
        for (int j = 0; j < campiDaNonAggiornare.length; j++)
          if (campiDaNonAggiornare[j].indexOf('.') == -1)
            campiDaNonAggiornare[j] = gestore.getEntita()
                + "."
                + campiDaNonAggiornare[j];
      }

      for (int i = 1; i <= numeroRecord; i++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
            && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));
        boolean updateOccorrenza = newDataColumnContainer.isColumn(nomeCampoMod)
            && "1".equals(newDataColumnContainer.getString(nomeCampoMod));

        // Rimozione dei campi fittizi (il campo per la marcatura della delete e
        // tutti gli eventuali campi passati come argomento)
        newDataColumnContainer.removeColumns(new String[] {
            gestore.getEntita() + "." + nomeCampoDelete,
            gestore.getEntita() + "." + nomeCampoMod});

        if (campiDaNonAggiornare != null) {
          newDataColumnContainer.removeColumns(campiDaNonAggiornare);
        }

        if (deleteOccorrenza) {
          // Se è stata richiesta l'eliminazione e il campo chiave numerica e'
          // diverso da null eseguo l'effettiva eliminazione del record
          if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) != null)
            gestore.elimina(status, newDataColumnContainer);
          // altrimenti e' stato eliminato un nuovo record non ancora inserito
          // ma predisposto nel form per l'inserimento
        } else {
          if (updateOccorrenza) {
            // si settano tutti i campi chiave con i valori ereditati dal
            // chiamante
            if(gestore.getAltriCampiChiave()!=null){
              for (int z = 0; z < gestore.getAltriCampiChiave().length; z++) {
                if (newDataColumnContainer.getColumn(
                    gestore.getAltriCampiChiave()[z]).getValue().getValue() == null)
                  newDataColumnContainer.getColumn(
                      gestore.getAltriCampiChiave()[z]).setValue(
                      valoreChiave[z].getValue());
              }
            }
            if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) == null)
              gestore.inserisci(status, newDataColumnContainer);
            else
              gestore.update(status, newDataColumnContainer);
          }
        }
      }
    }
  }

  /**
   * Gestisce le operazioni di update, insert, delete dei dettagli dei record di
   * una scheda multipla per un'entit&agrave; la cui chiave &egrave; autoincrementante e gestita mediante la tabella W_GENCHIAVI.<br>
   * Questo codice funziona se si presuppone di avere una pagina con un elenco
   * di record a scheda multipla,
   * <ul>
   * <li>la cui chiave è una parte fissa comune più un progressivo</li>
   * <li>i dati da aggiornare sono appartenenti ad una sola entità</li>
   * </ul>
   *
   * @param status
   *        stato della transazione
   * @param dataColumnContainer
   *        container di partenza da cui filtrare i record
   * @param gestore
   *        gestore a chiave ID autoincrementante per l'aggiornamento del record di una
   *        scheda multipla
   * @param suffissoContaRecord
   *        suffisso da concatenare a "NUMERO_" per ottenere il campo che indica
   *        il numero di occorrenze presenti nel container
   * @param valoreChiave
   *        parte non numerica della chiave dell'entità, per la valorizzazione
   *        in fase di inserimento se i dati non sono presenti nel container
   * @param nomeCampoDelete
   *        campo utilizzato per marcare la delete di un record della scheda
   *        multipla
   * @param campiDaNonAggiornare
   *        elenco eventuale di ulteriori campi fittizi da eliminare prima di
   *        eseguire l'aggiornamento nel DB
   *
   * @throws GestoreException
   */
  public void gestisciAggiornamentiRecordSchedaMultipla(
      TransactionStatus status, DataColumnContainer dataColumnContainer,
      AbstractGestoreChiaveIDAutoincrementante gestore, String suffisso,
      DataColumn[] valoreChiave, String[] campiDaNonAggiornare)
      throws GestoreException {

    ///////////////////////////////////////////////////////////
    // ATTENZIONE: METODO CON UTILIZZO ID AUTOINCREMENTANTE!!!!
    ///////////////////////////////////////////////////////////

    String nomeCampoNumeroRecord = "NUMERO_" + suffisso;
    String nomeCampoDelete = "DEL_" + suffisso;
    String nomeCampoMod = "MOD_" + suffisso;

    // Gestione delle pubblicazioni bando solo se esiste la colonna con il
    // numero di occorrenze
    if (dataColumnContainer.isColumn(nomeCampoNumeroRecord)) {

      // Estraggo dal dataColumnContainer tutte le occorrenze dei campi
      // dell'entità definita per il gestore
      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
          dataColumnContainer.getColumns(gestore.getEntita(), 0));

      int numeroRecord = dataColumnContainer.getLong(nomeCampoNumeroRecord).intValue();

      // Sabbadin 07/12/2011: spostato fuori dal ciclo questo controllo in modo
      // da fare una volta sola la verifica e l'append dell'entita' (SE
      // NECESSARIA) al nome di campo da non aggiornare
      if (campiDaNonAggiornare != null) {
        for (int j = 0; j < campiDaNonAggiornare.length; j++)
          if (campiDaNonAggiornare[j].indexOf('.') == -1)
            campiDaNonAggiornare[j] = gestore.getEntita()
                + "."
                + campiDaNonAggiornare[j];
      }

      for (int i = 1; i <= numeroRecord; i++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
            && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));
        boolean updateOccorrenza = newDataColumnContainer.isColumn(nomeCampoMod)
            && "1".equals(newDataColumnContainer.getString(nomeCampoMod));

        // Rimozione dei campi fittizi (il campo per la marcatura della delete e
        // tutti gli eventuali campi passati come argomento)
        newDataColumnContainer.removeColumns(new String[] {
            gestore.getEntita() + "." + nomeCampoDelete,
            gestore.getEntita() + "." + nomeCampoMod});

        if (campiDaNonAggiornare != null) {
          newDataColumnContainer.removeColumns(campiDaNonAggiornare);
        }

        if (deleteOccorrenza) {
          // Se è stata richiesta l'eliminazione e il campo chiave ID incrementante e'
          // diverso da null eseguo l'effettiva eliminazione del record
          if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) != null)
            gestore.elimina(status, newDataColumnContainer);
          // altrimenti e' stato eliminato un nuovo record non ancora inserito
          // ma predisposto nel form per l'inserimento
        } else {
          if (updateOccorrenza) {
            if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) == null)
              gestore.inserisci(status, newDataColumnContainer);
            else
              gestore.update(status, newDataColumnContainer);
          }
        }
      }
    }
  }

}