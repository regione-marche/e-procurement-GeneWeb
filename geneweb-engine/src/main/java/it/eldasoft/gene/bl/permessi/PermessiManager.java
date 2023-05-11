/*
 * Created on 23-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.permessi;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.dao.PermessiDao;
import it.eldasoft.gene.db.domain.permessi.PermessoEntita;
import it.eldasoft.gene.web.struts.permessi.PermessiAccountEntitaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.sicurezza.CriptazioneException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte di gestione dei permessi di un utente su un lavoro e le relative
 * proprieta'
 *
 * @author Luca.Giacomazzo
 */
public class PermessiManager {

  private PermessiDao       permessiDao;
  private GenChiaviManager  genChiaviManager;

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(PermessiManager.class);

  /**
   * @param permessiDao
   *        permessiDao da settare internamente alla classe.
   */
  public void setPermessiDao(PermessiDao permessiDao) {
    this.permessiDao = permessiDao;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager){
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * Metodo per estrarre gli utenti associati ad un lavoro,
   * con i relativi permessi
   *
   * @param codiceLavoro
   * @return Ritorna la lista degli utenti associati ad un lavoro, con i
   *         relativi permessi
   * @throws CriptazioneException
   */
  public List<?> getListaPermessiEntita(String campoChiave, String valoreChiave)
      throws CriptazioneException {
    if (logger.isDebugEnabled()) logger.debug("getListaPermessiEntita: inizio metodo");

    List<?> listaPermessiEntita = this.permessiDao.getPermessiEntita(campoChiave,
        valoreChiave);
    //Sabbadin 15/07/2015: con l'introduzione di SYSLOGIN non serve piu'
//    PermessoEntita permessoEntita = null;
//
//    for (int i = 0; i < listaPermessiEntita.size(); i++) {
//      permessoEntita = (PermessoEntita) listaPermessiEntita.get(i);
//
//      ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(
//          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
//          permessoEntita.getLogin().getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
//      permessoEntita.setLogin(new String(decriptatore.getDatoNonCifrato()));
//
//      listaPermessiEntita.set(i, permessoEntita);
//    }
    if (logger.isDebugEnabled()) logger.debug("getListaPermessiEntita: fine metodo");

    return listaPermessiEntita;
  }

  /**
   * Ritorna l'oggetto Permesso Lavoro che rappresenta il permesso dell'utente
   * sul lavoro in analisi
   *
   * @param codiceLavoro
   * @param idAccount
   */
  public PermessoEntita getPermessoEntitaByIdAccount(String campoChiave,
      String valoreChiave, int idAccount){
    return this.permessiDao.getPermessoEntitaByIdAccount(campoChiave,
        valoreChiave, idAccount);
  }

  /**
   * @return Ritorna la lista di tutti utenti, con le informazioni relative alla
   *         ai permessi di ciascun utente sull'entita' in analisi
   *
   * @throws CriptazioneException
   */
  public List<?> getAccountConPermessiEntita(String campoChiave, String valoreChiave, int idAccount, String codiceUffint)
      throws CriptazioneException {

    if (logger.isDebugEnabled()) logger.debug("getAccountConPermessiLavoro: inizio metodo");

    // Lista dei permessi relativi algi utenti associati al lavoro in analisi.
    // Anche questa lista è ordinata per USRSYS.SYSUTE
    List<?> listaPermessiEntitaAccount = this.permessiDao.getPermessiEntitaAccount(
        campoChiave, valoreChiave, idAccount,codiceUffint);

    PermessoEntita permessoEntita = null;
    String utenteDisabilitato = null;
    String nome=null;
    for (int i = 0; i < listaPermessiEntitaAccount.size(); i++) {
      permessoEntita = (PermessoEntita)
          listaPermessiEntitaAccount.get(i);

      //Sabbadin 15/07/2015: con l'introduzione di SYSLOGIN non serve piu'
//      ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(
//          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
//          permessoEntita.getLogin().getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
//      permessoEntita.setLogin(new String(decriptatore.getDatoNonCifrato()));
      utenteDisabilitato = permessoEntita.getUtenteDisabilitato();
      if("1".equals(utenteDisabilitato)){
        nome=permessoEntita.getNome();
        permessoEntita.setNome(nome + "(disabilitato)");
      }

      permessoEntita = null;
    }
    if (logger.isDebugEnabled()) logger.debug("getAccountConPermessiLavoro: fine metodo");

    return listaPermessiEntitaAccount;
  }

  public void updateAssociazioneAccountEntita(PermessiAccountEntitaForm
      permessiAccountEntitaForm) {
    if (logger.isDebugEnabled()) logger.debug("updateAssociazioneAccountEntita: inizio metodo");

    int numeroAccount = permessiAccountEntitaForm.getIdPermesso().length;

    List<Integer> listaPermessiDaCancellare = new ArrayList<Integer>();
    List<PermessoEntita> listaPermessiDaAggiornare = new ArrayList<PermessoEntita>();
    List<PermessoEntita> listaPermessiDaInserire = new ArrayList<PermessoEntita>();

    DizionarioCampi dizCampi = DizionarioCampi.getInstance();
    Campo campodiz = null;
    short tipo = 0;
    Integer condividi = null;
    Integer idPermesso = null;
    for(int i = 0; i < numeroAccount; i++){
      idPermesso = new Integer(permessiAccountEntitaForm.getIdPermesso()[i]);
      condividi = new Integer(permessiAccountEntitaForm.getCondividiEntita()[i]);
      String nomeCampo =permessiAccountEntitaForm.getCampoChiave();
      campodiz = dizCampi.getCampoByNomeFisico("G_PERMESSI." + nomeCampo);
      tipo = campodiz.getTipoColonna(); //0: Stringa, 2:Intero, 3: Decimale

      if(condividi.intValue() == 0){
        if(idPermesso.intValue() > 0)
          // questo record è da cancellare dalla G_PERMESSI
          listaPermessiDaCancellare.add(idPermesso);
      } else {
        PermessoEntita permessoEntita = new PermessoEntita();
        if(idPermesso.intValue() > 0){
          // questo record è da aggiornare nella G_PERMESSI
          permessoEntita.setIdPermesso(idPermesso);
          permessoEntita.setIdAccount(new Integer(permessiAccountEntitaForm.getIdAccount()[i]));
          permessoEntita.setAutorizzazione(new Integer(permessiAccountEntitaForm.getAutorizzazione()[i]));
          permessoEntita.setProprietario(new Integer(permessiAccountEntitaForm.getProprietario()[i]));
          if(permessiAccountEntitaForm.getRuolo()[i]!=null && !"".equals(permessiAccountEntitaForm.getRuolo()[i]))
            permessoEntita.setRuolo(new Integer(permessiAccountEntitaForm.getRuolo()[i]));
          if(permessiAccountEntitaForm.getRuoloUsrsys()!=null)
            permessoEntita.setRuoloUsrsys(new Integer(permessiAccountEntitaForm.getRuoloUsrsys()[i]));
          permessoEntita.setCampoChiave(permessiAccountEntitaForm.getCampoChiave());
          if(tipo==2) //Il campo della G_PERMESSI è un intero
            permessoEntita.setValoreChiave(new Long(permessiAccountEntitaForm.getValoreChiave()));
          else
            permessoEntita.setValoreChiave(permessiAccountEntitaForm.getValoreChiave());
          listaPermessiDaAggiornare.add(permessoEntita);
        } else {
          // questo record è da inserire nella G_PERMESSI
          permessoEntita.setIdPermesso(null);
          permessoEntita.setIdAccount(new Integer(permessiAccountEntitaForm.getIdAccount()[i]));
          permessoEntita.setAutorizzazione(new Integer(permessiAccountEntitaForm.getAutorizzazione()[i]));
          permessoEntita.setProprietario(new Integer(permessiAccountEntitaForm.getProprietario()[i]));
          if(permessiAccountEntitaForm.getRuolo()[i]!=null && !"".equals(permessiAccountEntitaForm.getRuolo()[i]))
            permessoEntita.setRuolo(new Integer(permessiAccountEntitaForm.getRuolo()[i]));
          permessoEntita.setCampoChiave(permessiAccountEntitaForm.getCampoChiave());
          permessoEntita.setValoreChiave(permessiAccountEntitaForm.getValoreChiave());
          if(tipo==2) //Il campo della G_PERMESSI è un intero
            permessoEntita.setValoreChiave(new Long(permessiAccountEntitaForm.getValoreChiave()));
          else
            permessoEntita.setValoreChiave(permessiAccountEntitaForm.getValoreChiave());
          listaPermessiDaInserire.add(permessoEntita);
        }
      }
    }

    // Cancellazione dei permessi
    if(listaPermessiDaCancellare.size() > 0){
      this.permessiDao.deletePermessi(listaPermessiDaCancellare);
    }

    // Aggiornamento dei permessi
    if(listaPermessiDaAggiornare.size() > 0){
      for(int i=0; i < listaPermessiDaAggiornare.size(); i++)
        this.permessiDao.updatePermesso((PermessoEntita) listaPermessiDaAggiornare.get(i));
    }

    // Inserimento dei nuovi permessi
    if(listaPermessiDaInserire.size() > 0){
      int numeroPermesso = this.genChiaviManager.getMaxId("G_PERMESSI", "NUMPER");
      for(int i=0; i < listaPermessiDaInserire.size(); i++){
        PermessoEntita permessoEnt = (PermessoEntita) listaPermessiDaInserire.get(i);
        permessoEnt.setIdPermesso(new Integer(numeroPermesso + (i+1)));
        this.permessiDao.insertPermesso(permessoEnt);
      }
    }
    if (logger.isDebugEnabled()) logger.debug("updateAssociazioneAccountEntita: fine metodo");

  }

  /**
   * Metodo per definire o ridefinire i permessi predefiniti sul tipo di entita'
   * in analisi, cioè su un lavoro in Lavori o Gare o altro
   *
   * @param campoChiave
   * @param valoreChiave
   * @param utenteDiRiferimento
   * @param predefinito
   * @param listaPermessiEntita lista dei permessi di un entita' da cui creare i
   *        permessi predefiniti
   */
  public void insertPermessiPredefiniti(String campoChiave, String valoreChiave,
      Integer utenteDiRiferimento, Integer predefinito, List<?> listaPermessiEntita) {
    if(logger.isDebugEnabled())
      logger.debug("insertPermessiPredefiniti: inizio metodo");

    int numeroPermessiPredefinitiSuDb =
        this.permessiDao.getNumeroPermessiPredefiniti(utenteDiRiferimento, predefinito);

    // Cancellazione dell'eventuale configurazione predefinita dell’utente
    // corrente precedentemente definita
    if(numeroPermessiPredefinitiSuDb > 0)
      this.permessiDao.deletePermessiPredefinitiByIdAccount(utenteDiRiferimento, predefinito);

    Iterator<?> iterator = listaPermessiEntita.iterator();

    // Max id della tabella G_PERMESSI
    int numeroPermesso = this.genChiaviManager.getMaxId("G_PERMESSI", "NUMPER");

    while(iterator.hasNext()){
      PermessoEntita permessoEntita = (PermessoEntita) iterator.next();
      // Se il permesso ha come idAccount l'idAccount dell'utente di riferimento
      // non si deve inserire tale occorrenza come occorrenza predefinita
      if(permessoEntita.getIdAccount().intValue() != utenteDiRiferimento.intValue()){
        permessoEntita.setIdPermesso(new Integer(++numeroPermesso));

        /*
         if(ListaPermessiEntitaAction.LISTA_TEXT_AUTORIZZAZIONI[1].equals(permessoEntita.getAutorizzazione()))
          permessoEntitaBean.setAutorizzazione(new Integer(1));
        else
          permessoEntitaBean.setAutorizzazione(new Integer(2));
        */
        //permessoEntita
        //permessoEntitaBean.setProprietario(
        //    permessoEntita.getProprietario()? new Integer(1):new Integer(2));
        permessoEntita.setCampoChiave(campoChiave);
        permessoEntita.setValoreChiave(valoreChiave);
        permessoEntita.setPredefinito(predefinito);
        permessoEntita.setRiferimento(utenteDiRiferimento);

        // Inserimento dell'i-esimo permesso predefinito
        this.permessiDao.insertPermessoPredefinito(permessoEntita,
            utenteDiRiferimento, predefinito);
      }
    }

    if(logger.isDebugEnabled())
      logger.debug("insertPermessiPredefiniti: fine metodo");
  }

  /**
   * Metodo per determinare se l'utente ha una condivisione predefinita per i
   * l'entita' in analisi
   *
   * @param riferimento id account
   * @param predefinito valore dell'entita' in analisi (1 per i lavori, 2 per
   *        gare, ecc...)
   * @return Ritorna true se l'account ha una condivisione predefinita per l'entita
   *         il tipo di entità in analisi, false altrimenti
   */
  public boolean hasAccountCondivisionePredefinita(Integer riferimento,
      Integer predefinito){

    if(logger.isDebugEnabled())
      logger.debug("hasAccountCondivisionePredefinita: inizio metodo");

    boolean result = false;
    int numeroPermessiPredefiniti = this.permessiDao.getNumeroPermessiPredefiniti(
        riferimento, predefinito);

    if(numeroPermessiPredefiniti > 0)
      result = true;

    if(logger.isDebugEnabled())
      logger.debug("hasAccountCondivisionePredefinita: fine metodo");

    return result;
  }

}