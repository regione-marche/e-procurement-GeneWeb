/*
 * Created on 1-ott-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.admin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.dao.UffintDao;
import it.eldasoft.gene.db.domain.UfficioIntestatario;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.admin.UfficioIntestatarioAccount;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioAccount;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte di gestione di un ufficio intestatario, le relative proprieta' e
 * l'associazione o meno ad un account
 * 
 * @author Stefano.Sabbadin
 */
public class UffintManager {

  private UffintDao uffintDao;

  /** Logger Log4J di classe */
  static Logger     logger = Logger.getLogger(UffintManager.class);

  /** Manager per l'interrogazione della base dati. */
  private SqlManager          sqlManager;
  
  
  /** Reference alla classe di business logic per l'estrazione dell'account */
  private AccountManager      accountManager;
  
  /** Manager per l'invio mail. */
  private MailManager    mailManager;
  
  /**
   * @param uffintDao
   *        uffintDao da settare internamente alla classe.
   */
  public void setUffintDao(UffintDao uffintDao) {
    this.uffintDao = uffintDao;
  }

  /**
   * @param sqlManager
   *        sqlManager da settare internamente alla classe.
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  /**
   * @param accountManager
   *        accountManager da settare internamente alla classe.
   */
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }
  
  /**
   * @param mailManager
   *        mailManager da settare internamente alla classe.
   */
  public void setMailManager(MailManager mailManager) {
    this.mailManager = mailManager;
  }
  /**
   * Estrazione della lista degli uffici intestatari a cui un utente e'
   * associato
   * 
   * @param idAccount
   * @return Ritorna la lista degli uffici a cui un utente e' associato
   */
  public List<?> getUfficiIntestatariAccount(int idAccount) {
    return this.uffintDao.getUfficiIntestatariAccount(idAccount);
  }
  
  /**
   * Estrazione della lista di tutti gli uffici intestatari
   * 
   */
  public List<?> getUfficiIntestatari() {
    return this.uffintDao.getUfficiIntestatari();
  }

  /**
   * Estrazione dell'ufficio intestatario associato alla chiave in input
   * 
   * @param codice
   * @return Ritorna l'ufficio intestatario associato alla chiave in input
   */
  public UfficioIntestatario getUfficioIntestatarioByPK(String codice) {
    return this.uffintDao.getUfficioIntestatarioByPK(codice);
  }
  
  /**
   * Estrazione dell'ufficio intestatario associato alla chiave in input 
   * con i dati aggiuntivi di indirizzo
   * @param codice
   * @return Ritorna l'ufficio intestatario associato alla chiave in input
   */
  public UfficioIntestatario getUfficioIntestatarioByPKWithAddressAndNation(String codice) {
    return this.uffintDao.getUfficioIntestatarioByPKWithAddressAndNation(codice);
  }
  
  /**
   * Metodo per l'estrazione della lista di tutti gli uffici intestatari in cui
   * quelli associati all'utente vengono marcati
   * 
   * @param idAccount
   * @return Ritorna la lista degli uffici intestatari associati all'utente in
   *         analisi, con l'attributo 'associato' popolato
   */
  public List<UfficioIntestatarioAccount> getUfficiIntestatariConAssociazioneAccount(int idAccount) {

    List<UfficioIntestatarioAccount> listaResult = new ArrayList<UfficioIntestatarioAccount>();

    // Lista uffici a cui un utente e' associato
    List<?> listaUfficiAccount = this.uffintDao.getUfficiIntestatariAccount(idAccount);

    UfficioIntestatario ufficio = null;
    ListIterator<?> iterUffici = listaUfficiAccount.listIterator();
    HashSet<String> setAssociazioni = new HashSet<String>();
    // si popola un set con i codici degli uffici associati all'utente
    while (iterUffici.hasNext()) {
      ufficio = (UfficioIntestatario) iterUffici.next();
      setAssociazioni.add(ufficio.getCodice());
    }

    // Lista di tutti gli uffici intestatari
    List<?> listaUffici = this.uffintDao.getUfficiIntestatari();

    // Trasforma la listaUffici (contenente oggetti di tipo Uffint) in
    // una lista di oggetti di tipo UfficioIntestatarioAccount
    UfficioIntestatarioAccount ufficioAccount = null;
    for (int i = 0; i < listaUffici.size(); i++) {
      ufficioAccount = new UfficioIntestatarioAccount(
          (UfficioIntestatario) listaUffici.get(i));
      if (setAssociazioni.contains(ufficioAccount.getCodice()))
        ufficioAccount.setAssociato(true);
      else
        ufficioAccount.setAssociato(false);
      listaResult.add(ufficioAccount);
    }

    return listaResult;
  }

  /**
   * Metodo per aggiornare l'associazione uffici-utente
   * 
   * @param idAccount
   *        idAccount a cui associare i profili
   * @param elencoUffici
   *        array dei codici uffici intestatari da associare all'utente
   */
  public void updateAssociazioneUfficiIntestatariAccount(int idAccount,
      String[] elencoUffici) {

    List<String> listaUfficiAssociati = new ArrayList<String>();

    if (elencoUffici != null) {
      for (int i = 0; i < elencoUffici.length; i++){
        listaUfficiAssociati.add(elencoUffici[i]);
    }
      
      logger.debug("point 1");

    if (!listaUfficiAssociati.isEmpty()) {
      int min = 0;
      int max = 0;
      while(!(listaUfficiAssociati.size() <= max)){
        if(listaUfficiAssociati.size() >= max+100){
          min = max;
          max = max+100;
        }else{
          min = max;
          max = listaUfficiAssociati.size();
        }
        this.uffintDao.deleteUfficiNonAssociatiAccount(idAccount,
            listaUfficiAssociati.subList(min, max));
        }
      }

      List<?> listaUfficiPreAssociati = this.uffintDao.getUfficiIntestatariAccount(idAccount);
      Iterator<?> iterLista = listaUfficiPreAssociati.iterator();

      Map<String, UfficioIntestatario> mappaUfficiPreAssociati = new HashMap<String, UfficioIntestatario>();
      UfficioIntestatario ufficioAccount = null;
      while (iterLista.hasNext()) {
        ufficioAccount = (UfficioIntestatario) iterLista.next();
        mappaUfficiPreAssociati.put(ufficioAccount.getCodice(), ufficioAccount);
      }

      Iterator<?> iter = listaUfficiAssociati.iterator();
      String codUfficio = null;

      // insert degli uffici da associare all'utente in analisi
      while (iter.hasNext()) {
        codUfficio = (String) iter.next();
        if (!mappaUfficiPreAssociati.containsKey(codUfficio)) {
          this.uffintDao.insertAssociazioneAccountUfficio(codUfficio, idAccount);
        }
      }
    } else {
      // In questo caso non si deve associare alcun ufficio all'account in
      // analisi e si devono rimuovere le associazioni esistenti
      this.uffintDao.deleteUfficiNonAssociatiAccount(idAccount, null);
    }
  }

  /**
   * Metodo per aggiornare l'associazione stazione appaltante-utenti
   * 
   * @param codein
   *        codice stazione appaltante
   * @param elencoUtentiAssociati
   *        lista utenti da associare alla stazione appaltante
   * @param elencoUtentiDisabilitati
   *        lista utenti da abilitare / disabilitare
   */
  public void updateAssociazioneUfficiIntestatariUtenti(String codein, String elencoUtentiAssociati, String elencoUtentiDisabilitati) 
  throws GestoreException{

	  String elemento = null;
	  Map<String, String> utentiDisabilitati = new HashMap<String, String>();
	  Map<String, String> utentiAssociati = new HashMap<String, String>();
	  String whereOn = "";
	  String whereOff = "";
	  try {
		  // Verifico le associazioni
		  boolean onlyOne = (ConfigManager.getValore(CostantiGenerali.PROP_UFFINT_USRSYS) != null && ConfigManager.getValore(CostantiGenerali.PROP_UFFINT_USRSYS).equals("1"));
   
		  if (elencoUtentiAssociati != null && elencoUtentiAssociati.length() > 0) {
			  String[] elencoUtenti = elencoUtentiAssociati.split(";");
			  for (int i = 0; i < elencoUtenti.length; i++) {
				  elemento = elencoUtenti[i];
				  if (elemento.length() > 0) {
					  String[] riga = elemento.split(",");
					  if (riga.length == 2) {
						  if (riga[1].equals("true")) {
							  if (onlyOne) {
								  //se posso associare massimo un utente elimino tutte le associazioni esistenti
								  this.sqlManager.update("delete from usr_ein where codein = ?", new Object[] {codein});
								  onlyOne = false;
							  }
						  }
						  utentiAssociati.put(riga[0], riga[1]);
					  }
				  }
			  }
			  if (utentiAssociati.size() > 0) {
				  for (Map.Entry<String, String> entry : utentiAssociati.entrySet())
				  {
					  // Se utente associato
					  if (entry.getValue().equals("true")) {
						  this.sqlManager.update("insert into usr_ein(syscon, codein) values(?, ?)", new Object[] {new Long(entry.getKey()), codein});
					  } else {
						  // Se l'utente è da dissociare
						  whereOff += " OR syscon = " + entry.getKey();
					  }
				  }
				  // Se esistono utenti da dissociare
				  if (whereOff.length() > 0) {
					  this.sqlManager.update("delete from usr_ein where codein = ? and (1 = 0" + whereOff + ")", new Object[] {codein});
				  }
			  }
		  }

		  whereOn = "";
		  whereOff = "";
		  // Verifico gli utenti abilitati e disabilitati
		  if (elencoUtentiDisabilitati != null && elencoUtentiDisabilitati.length() > 0) {
			  String[] elencoUtenti = elencoUtentiDisabilitati.split(";");
			  for (int i = 0; i < elencoUtenti.length; i++) {
				  elemento = elencoUtenti[i];
				  if (elemento.length() > 0) {
					  String[] riga = elemento.split(",");
					  if (riga.length == 2) {
						  utentiDisabilitati.put(riga[0], riga[1]);
					  }
				  }
			  }
			  if (utentiDisabilitati.size() > 0) {
				  Account account = null;
				  for (Map.Entry<String, String> entry : utentiDisabilitati.entrySet())
				  {
					  // Se utente disabilitato
					  if (entry.getValue().equals("true")) {
						  whereOn += " OR syscon = " + entry.getKey();
					  } else {
						  // Se l'utente è abilitato
						  whereOff += " OR syscon = " + entry.getKey();
					  }
					  account = accountManager.getAccountById(Integer.parseInt(entry.getKey()));
					  inviaMail(account, (entry.getValue().equals("true"))?"1":"0");
				  }
				  // Se esistono utenti da disabilitare
				  if (whereOn.length() > 0) {
					  this.sqlManager.update("update usrsys set sysdisab = 1 where 1 = 0" + whereOn, null);
				  }
				  // Se esistono utenti da abilitare
				  if (whereOff.length() > 0) {
					  this.sqlManager.update("update usrsys set sysdisab = 0 where 1 = 0" + whereOff, null);
				  }
			  }
		  }
	  } catch (SQLException e) {
		  throw new GestoreException("Errore in updateAssociazioneUfficiIntestatariUtenti durante l'associazione tra stazione appaltante e utenti", null, e);
	  } catch (NumberFormatException e) {
		  throw new GestoreException("Errore in updateAssociazioneUfficiIntestatariUtenti durante il recupero dei dati dell'account", null, e);
	} catch (CriptazioneException e) {
		throw new GestoreException("Errore in updateAssociazioneUfficiIntestatariUtenti durante il recupero dei dati dell'account", null, e);
	}

  }
  
  private void inviaMail(Account account, String flagAbilitazione) throws CriptazioneException {

	    String nomeMittente = ConfigManager.getValore(CostantiGenerali.PROP_TITOLO_APPLICATIVO);
	    ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);
	    
	    try {
	      
	      IMailSender mailSender = MailUtils.getInstance(mailManager, ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE),CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);

	      String intestazione = account.getNome();
	      String login = account.getLogin();
	      String testoMail = null;
	      String oggettoMail = null;

	      if (!flagAbilitazione.equals(CostantiDettaglioAccount.DISABILITATO)) {
	        oggettoMail = resBundleGenerale.getString(CostantiGenerali.RESOURCE_OGGETTO_MAIL_ABILITAZIONE_UTENTE);
	        oggettoMail = UtilityStringhe.replaceParametriMessageBundle(
	            oggettoMail, new String[] { nomeMittente });

	        ICriptazioneByte decriptatorePsw = FactoryCriptazioneByte.getInstance(
	                ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
	                account.getPassword().getBytes(),
	                ICriptazioneByte.FORMATO_DATO_CIFRATO);
	        
	        testoMail = resBundleGenerale.getString(CostantiGenerali.RESOURCE_TESTO_MAIL_ABILITAZIONE_UTENTE);
	        testoMail = UtilityStringhe.replaceParametriMessageBundle(testoMail,
	            new String[] { intestazione, nomeMittente, login, new String(decriptatorePsw.getDatoNonCifrato()),
	                nomeMittente });

	      } else {
	        oggettoMail = resBundleGenerale.getString(CostantiGenerali.RESOURCE_OGGETTO_MAIL_DISABILITAZIONE_UTENTE);
	        oggettoMail = UtilityStringhe.replaceParametriMessageBundle(
	            oggettoMail, new String[] { nomeMittente });

	        testoMail = resBundleGenerale.getString(CostantiGenerali.RESOURCE_TESTO_MAIL_DISABILITAZIONE_UTENTE);
	        testoMail = UtilityStringhe.replaceParametriMessageBundle(testoMail,
	            new String[] { intestazione, nomeMittente, nomeMittente });
	      }

	      mailSender.send(account.getEmail(), oggettoMail, testoMail);

	    } catch (MailSenderException ms) {

	      String logMessageKey = ms.getChiaveResourceBundle();
	      String logMessageError = resBundleGenerale.getString(logMessageKey);
	      for (int i = 0; ms.getParametri() != null && i < ms.getParametri().length; i++)
	        logMessageError = logMessageError.replaceAll(
	            UtilityStringhe.getPatternParametroMessageBundle(i),
	            (String) ms.getParametri()[i]);
	      logger.error(logMessageError, ms);

	    }
	  }
}