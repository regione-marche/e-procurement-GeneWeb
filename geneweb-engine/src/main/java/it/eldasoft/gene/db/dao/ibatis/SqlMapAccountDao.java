/*
 * Created on 13-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao.ibatis;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.AccountDao;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.admin.AccountCodFiscDuplicati;
import it.eldasoft.gene.db.domain.admin.AccountGruppo;
import it.eldasoft.gene.db.domain.admin.GruppoAccount;
import it.eldasoft.gene.db.domain.admin.GruppoConProfiloAccount;
import it.eldasoft.gene.db.domain.admin.TrovaAccount;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioAccount;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella ACCOUNT tramite iBatis.
 *
 * @author Stefano.Sabbadin
 */
public class SqlMapAccountDao extends SqlMapClientDaoSupportBase implements
    AccountDao {

  @Override
  public Account getAccountByLogin(String login) throws DataAccessException, SqlComposerException {
    SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("operatoreUpper", composer.getFunzioneUpperCase());
    hash.put("login", login.toUpperCase());
    return (Account) getSqlMapClientTemplate().queryForObject(
        "getAccountByLogin", hash);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Account> getAccountByPassword(String password) throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("password", password);
    return getSqlMapClientTemplate().queryForList("getAccountByPassword", hash);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<GruppoConProfiloAccount> getGruppiAccount(int idAccount, String codApp)
      throws DataAccessException {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("codApp", codApp);

    return getSqlMapClientTemplate().queryForList("getGruppiAccount", hash);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Integer> getGruppiAccountByCodAppCodPro(int idAccount, String codApp,
      String codProfilo) throws DataAccessException {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("codApp", codApp);
    hash.put("codiceProfilo", codProfilo);
    return getSqlMapClientTemplate().queryForList(
        "getGruppiAccountByCodAppCodPro", hash);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Account> getListaAccountByCodProfili(String[] codProfili) {
    HashMap<String, String[]> hash = new HashMap<String, String[]>();
    hash.put("codProfili", codProfili);
    return getSqlMapClientTemplate().queryForList("getListaAccountByCodProfili", hash);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<GruppoConProfiloAccount> getGruppiConProfiloByCodApp(int idAccount, String codApp)
      throws DataAccessException {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("codApp", codApp);
    return this.getSqlMapClientTemplate().queryForList(
        "getGruppiConProfiloByCodApp", hash);
  }

  @Override
  public void updatePassword(int idAccount, String vecchiaPassword,
      String nuovaPassword) throws DataAccessException {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("vecchiaPassword", vecchiaPassword);
    hash.put("nuovaPassword", nuovaPassword);
    getSqlMapClientTemplate().update("updatePassword", hash, 1);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<AccountGruppo> getUtentiDiGruppo(int idGruppo, String codiceProfilo)
      throws DataAccessException {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("codiceProfilo", codiceProfilo);
    return this.getSqlMapClientTemplate().queryForList("getAccountDiGruppo",
        hash);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Account> getAllUtenti(String codiceProfilo) throws DataAccessException {
    return this.getSqlMapClientTemplate().queryForList("getAccountByProfilo",
        codiceProfilo);
  }

  @Override
  public void insertAssociazioneAccountGruppo(int idGruppo, int idAccount)
      throws DataAccessException {
    HashMap<String, Integer> hash = new HashMap<String, Integer>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("idAccount", new Integer(idAccount));
    hash.put("priorita", new Integer(0));
    this.getSqlMapClientTemplate().insert("insertAssociazioneAccountGruppo",
        hash);
  }

  @Override
  public int deleteAccountNonAssociati(int idGruppo, List<Integer> lista)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("listaAccountAssociati", lista);
    return this.getSqlMapClientTemplate().delete("deleteAccountNonAssociati",
        hash);
  }

  @Override
  public void insertAccount(Account account) throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertAccount", account);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Account> getAccount() throws DataAccessException {
    return getSqlMapClientTemplate().queryForList("getAccountCompleta", null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Account> getAccount(TrovaAccount trovaAccount) throws DataAccessException, SqlComposerException {
    HashMap<String, String> hash = new HashMap<String, String>();
    //this.setOperatoreMatch(hash, trovaAccount.getDescrizione(), "operatoreDescrizione");
    hash.put("operatoreDescrizione", trovaAccount.getOperatoreDescrizione());
    hash.put("escapeDescrizione", trovaAccount.getEscapeDescrizione());
    hash.put("descrizione", trovaAccount.getDescrizione());
    hash.put("operatoreNome", trovaAccount.getOperatoreNome());
    hash.put("escapeNome", trovaAccount.getEscapeNome());
    hash.put("nome", trovaAccount.getNome());
    hash.put("codiceFiscale", trovaAccount.getCodiceFiscale());
    hash.put("operatoreCodiceFiscale", trovaAccount.getOperatoreCodiceFiscale());
    hash.put("escapeCodiceFiscale", trovaAccount.getEscapeCodiceFiscale());
    hash.put("eMail", trovaAccount.geteMail());
    hash.put("operatoreEMail", trovaAccount.getOperatoreEMail());
    hash.put("escapeEMail", trovaAccount.getEscapeEMail());
    hash.put("uffint", trovaAccount.getUffint());
    hash.put("operatoreUffint", trovaAccount.getOperatoreUffint());
    hash.put("escapeUffint", trovaAccount.getEscapeUffint());
    hash.put("utenteDisabilitato", trovaAccount.getUtenteDisabilitato());
    hash.put("utenteLDAP", trovaAccount.getUtenteLDAP());
    if(trovaAccount.isNoCaseSensitive()){
      SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
      hash.put("operatoreUpper", composer.getFunzioneUpperCase());
      if(trovaAccount.getDescrizione() != null)
        hash.put("descrizione", trovaAccount.getDescrizione().toUpperCase());
      else
        hash.put("descrizione", null);
      if(trovaAccount.getNome() != null)
        hash.put("nome", trovaAccount.getNome().toUpperCase());
      else
        hash.put("nome", null);
      if(trovaAccount.getCodiceFiscale() != null)
        hash.put("codiceFiscale", trovaAccount.getCodiceFiscale().toUpperCase());
      else
        hash.put("codiceFiscale", null);
      if(trovaAccount.geteMail()!= null)
        hash.put("eMail", trovaAccount.geteMail().toUpperCase());
      else
        hash.put("eMail", null);
      if(trovaAccount.getUffint()!= null)
        hash.put("uffint", trovaAccount.getUffint().toUpperCase());
      else
        hash.put("uffint", null);
    }
    hash.put("ufficioAppartenenza", trovaAccount.getUfficioAppartenenza());
    hash.put("categoria", trovaAccount.getCategoria());
    hash.put("gestioneUtenti", trovaAccount.getGestioneUtenti());
    hash.put("amministratore", trovaAccount.getAmministratore());
    return getSqlMapClientTemplate().queryForList("getAccount", hash);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Account> getAccountByCodAppCodPro(String codApp, String codPro)
      throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codApp", codApp);
    hash.put("codiceProfilo", codPro);
    return getSqlMapClientTemplate().queryForList("getAccountByCodAppCodPro",
        hash);
  }

  @Override
  public void updateAccount(Account account) throws DataAccessException {
    getSqlMapClientTemplate().update("updateAccount", account, 1);
  }

  @Override
  public Account getAccountById(Integer id) throws DataAccessException {
    return (Account) getSqlMapClientTemplate().queryForObject("getAccountById",
        id);
  }

  @Override
  public void deleteAccount(Integer id) throws DataAccessException {
    getSqlMapClientTemplate().delete("deleteAccount", id);
  }

  @Override
  public boolean isUsedLogin(String login, int id) throws DataAccessException {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
    hash.put("idAccount", new Integer(id));
    hash.put("login", login);
    Integer count = (Integer) getSqlMapClientTemplate().queryForObject(
        "getCountLogin", hash);

    boolean isUsed = false;
    if (count.intValue() > 0) isUsed = true;
    return isUsed;
  }

  @Override
  public boolean isUsedDn(String dn, int id) throws DataAccessException {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
    hash.put("idAccount", new Integer(id));
    hash.put("dn", dn);
    Integer count = (Integer) getSqlMapClientTemplate().queryForObject(
        "getCountDn", hash);

    boolean isUsed = false;
    if (count.intValue() > 0) isUsed = true;
    return isUsed;
  }

  @Override
  public boolean isUsedLoginPassword(String login, String password, int id)
      throws DataAccessException {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
    hash.put("idAccount", new Integer(id));
    hash.put("login", login);
    hash.put("password", password);
    Integer count = (Integer) getSqlMapClientTemplate().queryForObject(
        "getCountLoginPassword", hash);

    boolean isUsed = false;
    if (count.intValue() > 0) isUsed = true;
    return isUsed;
  }

  @Override
  public void deleteAccountConAssociazioneGruppo(Integer id)
      throws DataAccessException {
    getSqlMapClientTemplate().delete("deleteAccountConAssociazioneGruppo", id);
  }

  @Override
  public int deleteGruppiNonAssociatiProfilo(int idAccount, String codApp,
      String codiceProfilo, List<Integer> lista) throws DataAccessException {

    List<String> listaCodApp = new ArrayList<String>();
    if(codApp.indexOf(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE) >= 0){
      String[] arrayCodApp =
            codApp.split(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE);
      for(int i=0; i < arrayCodApp.length; i++)
      listaCodApp.add(arrayCodApp[i]);
    } else {
      listaCodApp.add(codApp);
    }

    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("listaGruppiAssociati", lista);
    hash.put("codiceProfilo", codiceProfilo);
    hash.put("listaCodApp", listaCodApp);
    return this.getSqlMapClientTemplate().delete(
        "deleteGruppiNonAssociatiProfilo", hash);
  }

  @Override
  public int deleteGruppiNonAssociatiAccountDaDettaglioAccount(int idAccount,
      String codApp, List<Integer> lista) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("listaGruppiAssociati", lista);
    hash.put("codApp", codApp);

    return this.getSqlMapClientTemplate().delete(
        "deleteGruppiNonAssociatiAccountDaDettaglioAccount", hash);
  }

  @Override
  public int deleteGruppiNonAssociatiAccountDaDettaglioProfilo(int idAccount,
      String codApp, String codiceProfilo, List<Integer> lista)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("listaGruppiAssociati", lista);
    hash.put("codApp", codApp);
    hash.put("codiceProfilo", codiceProfilo);
    return this.getSqlMapClientTemplate().delete(
        "deleteGruppiNonAssociatiAccountDaDettaglioProfilo", hash);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<GruppoAccount> getGruppiAssociatiAccountasList(int idAccount,
      String codiceProfilo) throws DataAccessException {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("codiceProfilo", codiceProfilo);
    return this.getSqlMapClientTemplate().queryForList("getGruppiDiAccount",
        hash);
  }

  @Override
  public void updatePasswordSenzaVecchia(int idAccount, String nuovaPassword)
      throws DataAccessException {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("nuovaPassword", nuovaPassword);
    getSqlMapClientTemplate().update("updatePasswordSenzaVecchia", hash, 1);
  }

  @Override
  public void insertStoriaAccount(int idAccount, String login, String loginCriptata, String password,
      Date dataInserimento) throws DataAccessException {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("login", login);
    hash.put("loginCriptata", loginCriptata);
    hash.put("password", password);
    hash.put("dataInserimento", dataInserimento);
    this.getSqlMapClientTemplate().insert("insertStoriaAccount", hash);
  }

  /*
   * public boolean isUsedStoriaLoginPassword(String login,String password, int
   * id) throws DataAccessException { Integer count = (Integer)
   * getSqlMapClientTemplate().queryForObject( "getCountStoriaLoginPassword",
   * new Integer(id));
   *
   * boolean isUsed = false; if (count.intValue() > 0) isUsed = true; return
   * isUsed; }
   */
  @Override
  public void deleteStoriaAccount(Integer id) throws DataAccessException {
    this.getSqlMapClientTemplate().delete("deleteStoriaAccount", id);
  }

  @Override
  public Date getDataUltimoCambioPsw(String login, String password)
      throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("login", login);
    hash.put("password", password);
    return (Date) this.getSqlMapClientTemplate().queryForObject(
        "getDataUltimoCambioPsw", hash);
  }

  @Override
  public void deletePermessiAccount(Integer idAccount)
      throws DataAccessException {
    this.getSqlMapClientTemplate().delete("deletePermessiDiAccount", idAccount);
  }

  @Override
  public void updateAbilitazioneUtente(int idAccount, String utenteDisabilitato)
      throws DataAccessException {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("utenteDisabilitato", utenteDisabilitato);
    if (CostantiDettaglioAccount.ABILITATO.equals(utenteDisabilitato)) {
      hash.put("ultimoAccesso", new Date());
    }
    getSqlMapClientTemplate().update("updateAbilitazioneUtente", hash, 1);
  }

  @Override
  public int getNumeroAssociazioniProfili(int idAccount, String codApp)
      throws DataAccessException {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("codApp", codApp);
    Integer numero = (Integer) getSqlMapClientTemplate().queryForObject(
        "getNumeroAssociazioniProfili", hash);
    return numero.intValue();
  }

  @Override
  public void deleteAssociazioneTecniciAccount(Integer idAccount)
    throws DataAccessException {
  this.getSqlMapClientTemplate().update("deleteAssociazioneTecniciAccount", idAccount);
  }


  @Override
  @SuppressWarnings("unchecked")
  public List<AccountCodFiscDuplicati>  getListaUtentiUgualeCodfisc(int idAccount, String codfisc)
      throws DataAccessException {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("codfisc", codfisc);
    return getSqlMapClientTemplate().queryForList("getListaUtentiUgualeCodfisc", hash);

  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Account> getListaAccountLoginNull() {
    return getSqlMapClientTemplate().queryForList("getListaAccountLoginNull", null);
 }

  @Override
  @SuppressWarnings("unchecked")
  public List<Account> getListaAccountLoginCriptataNull() {
    return getSqlMapClientTemplate().queryForList("getListaAccountLoginCriptataNull", null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Account> getListaStoriaAccountLoginNull() {
    return getSqlMapClientTemplate().queryForList("getListaStoriaAccountLoginNull", null);
  }

  @Override
  public void updateStoricoPassword(Account account) {
    this.getSqlMapClientTemplate().update("updateStoricoPassword", account);
 }

  @Override
  public void deleteCredenzialiServiziEsterniAccount(Integer id) {
    getSqlMapClientTemplate().delete("deleteCredenzialiServiziEsterniAccount", id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<String> getAccountGestoriProfilo(){
    return getSqlMapClientTemplate().queryForList("getIdGestoriUtenti");
  }

  @Override
  public Timestamp getUltimoCambioPassword(Integer id){
    return (Timestamp) getSqlMapClientTemplate().queryForObject("getUltimoCambioPassword",id);
  }

  @Override
  public void insertCancellazioneUtente( Integer id,Integer syscon, String syslogin, Date date){
    HashMap<String, Object> hash = new HashMap<String, Object>();
    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
    hash.put("id", id);
    hash.put("syscon", syscon);
    hash.put("syslogin", syslogin);
    hash.put("date",sqlDate);
    getSqlMapClientTemplate().insert("insertCancellazioneUtente",hash);
  }

  @Override
  public Date getDataCancellazione(String login){
    java.sql.Date sqlDate = (java.sql.Date) getSqlMapClientTemplate().queryForObject("getDataCancellazione",login);
    if(sqlDate == null){return null;}
    else{
    return new Date(sqlDate.getTime());}
  }

  @Override
  public String getPasswordDisallineataStorico(Integer id){
    String res = (String) getSqlMapClientTemplate().queryForObject("getPasswordDisallineataStorico",id);
    return res;
  }

  @Override
  public void updateUltimoAccesso(int idAccount, Date dataUltimoAccesso) {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idAccount", idAccount);
    hash.put("ultimoAccesso", dataUltimoAccesso);
    getSqlMapClientTemplate().update("updateUltimoAccesso", hash, 1);
  }

  @Override
  public int getNumeroLoginFallite(String username) {
    Integer numero = (Integer) getSqlMapClientTemplate().queryForObject(
        "getNumeroLoginFallite", username.toLowerCase());
    return numero.intValue();
  }

  @Override
  public Date getUltimaLoginFallita(String username) {
    Date data = (Date) getSqlMapClientTemplate().queryForObject(
        "getUltimaLoginFallita", username.toLowerCase());
    return data;
  }

  @Override
  public void insertLoginFallita(int id, String username, Date loginTime, String ipAddress) {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("id", id);
    hash.put("username", username.toLowerCase());
    hash.put("loginTime", loginTime);
    hash.put("ipAddress", ipAddress);
    getSqlMapClientTemplate().update("insertLoginFallita", hash, 1);
  }

  @Override
  public void deleteLoginFallite(String username) {
    getSqlMapClientTemplate().delete("deleteLoginFallite", username.toLowerCase());
  }

}