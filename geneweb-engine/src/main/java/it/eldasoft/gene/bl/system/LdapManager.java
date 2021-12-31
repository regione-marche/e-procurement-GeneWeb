/*
 * Created on 02-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.system;

import it.eldasoft.gene.bl.PropsConfigManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.PropsConfig;
import it.eldasoft.gene.db.domain.admin.AccountLdap;
import it.eldasoft.gene.db.domain.system.ConfigurazioneLdap;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;

import java.util.Iterator;
import java.util.List;

import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.LikeFilter;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte di gestione dell'interfacciamento al server LDAP
 *
 * @author cit_defilippis
 */
public class LdapManager {

  private static final String LDAP_SERVER = "ldap://";
  private static final String LDAPS_SERVER = "ldaps://";

  private static final String CHIAVE_PREFISSO                  = "ldap.";
  private static final String CHIAVE_SERVER                    = "ldap.server";
  private static final String CHIAVE_PORTA                     = "ldap.porta";
  private static final String CHIAVE_BASE                      = "ldap.base";
  private static final String CHIAVE_DN                        = "ldap.dn";
  public static final String CHIAVE_PASSWORD                  = "ldap.password";

  private static final String CHIAVE_FILTRO_OU                 = "ldap.filtroOU";
  private static final String CHIAVE_FILTRO_UTENTI             = "ldap.filtroUtenti";
  private static final String CHIAVE_ATTRIBUTO_LOGIN           = "ldap.attributoLogin";
  private static final String CHIAVE_ATTRIBUTO_NOME            = "ldap.attributoNome";


  private PropsConfigManager  propsConfigManager;

  /**
   * @param propsConfigManager
   *        The propsConfigManager to set.
   */
  public void setPropsConfigManager(PropsConfigManager propsConfigManager) {
    this.propsConfigManager = propsConfigManager;
  }

  /**
   * @return Ritorna propsConfigManager.
   */
  public PropsConfigManager getPropsConfigManager() {
    return propsConfigManager;
  }

  /**
   * Estrae i parametri per la connessione LDAP dalla W_CONFIG
   *
   * @return oggetto della classe ServerLdap contenente le informazioni relative
   *         alla configurazione LDAP e presenti nella W_CONFIG
   * @throws CriptazioneException
   *         eccezione ritornata nel caso di problemi di decifratura della
   *         password per l'utente indicato come "dn" per accedere all'elenco
   *         dati LDAP
   */
  public ConfigurazioneLdap getConfigurazione() throws CriptazioneException {

    ConfigurazioneLdap cfg = new ConfigurazioneLdap();

//  String valore = null;

    List props = this.propsConfigManager.getPropertiesByPrefix(
        CostantiGenerali.CODICE_APPLICAZIONE_LIBRERIA_GENERALE_WEB,
        LdapManager.CHIAVE_PREFISSO);
    for (Iterator it = props.iterator(); it.hasNext();) {
      PropsConfig property = (PropsConfig) it.next();
      if (LdapManager.CHIAVE_SERVER.equals(property.getChiave()))
        cfg.setServer(property.getValore());
      if (LdapManager.CHIAVE_PORTA.equals(property.getChiave()))
        cfg.setPorta(property.getValore());
      if (LdapManager.CHIAVE_BASE.equals(property.getChiave()))
        cfg.setBase(property.getValore());
      if (LdapManager.CHIAVE_DN.equals(property.getChiave()))
        cfg.setDn(property.getValore());
      if (LdapManager.CHIAVE_PASSWORD.equals(property.getChiave()))
        cfg.setPassword(property.getValore());
      if (LdapManager.CHIAVE_FILTRO_OU.equals(property.getChiave()))
        cfg.setFiltroOU(property.getValore());
      if (LdapManager.CHIAVE_FILTRO_UTENTI.equals(property.getChiave()))
        cfg.setFiltroUtenti(property.getValore());
      if (LdapManager.CHIAVE_ATTRIBUTO_LOGIN.equals(property.getChiave()))
        cfg.setAttributoLogin(property.getValore());
      if (LdapManager.CHIAVE_ATTRIBUTO_NOME.equals(property.getChiave()))
        cfg.setAttributoNome(property.getValore());
    }

    // decripto la password
    if (cfg.getPassword() != null) {
      ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          cfg.getPassword().getBytes(),
          ICriptazioneByte.FORMATO_DATO_CIFRATO);
      cfg.setPassword(new String(decriptatore.getDatoNonCifrato()));
    }
    return cfg;
  }

  /**
   * Inserisce le properties relative alle configurazioni del server LDAP
   *
   * @param cfg
   *        contenitore con i dati da salvare nel DB
   * @throws CriptazioneException
   *         eccezione generata nel caso in cui la cifratura della password
   *         fallisca
   */
  public void updateConfigurazione(ConfigurazioneLdap cfg) throws CriptazioneException {

    int numeroProperties = 8;
    // si verifica se è variato l'indirizzo email, ed in tal caso si vuota anche
    // il campo password
    PropsConfig propertyDN = this.propsConfigManager.getProperty(
        CostantiGenerali.CODICE_APPLICAZIONE_LIBRERIA_GENERALE_WEB,
        LdapManager.CHIAVE_DN);
    if (propertyDN != null
        && !cfg.getDn().equals(propertyDN.getValore())) {
      numeroProperties++;
    }

    PropsConfig[] property = new PropsConfig[numeroProperties];

    for (int i = 0; i < property.length; i++) {
      property[i] = new PropsConfig();
      property[i].setCodApp(CostantiGenerali.CODICE_APPLICAZIONE_LIBRERIA_GENERALE_WEB);
    }

    property[0].setChiave(LdapManager.CHIAVE_SERVER);
    property[0].setValore(cfg.getServer());

    property[1].setChiave(LdapManager.CHIAVE_PORTA);
    property[1].setValore(cfg.getPorta());

    property[2].setChiave(LdapManager.CHIAVE_BASE);
    property[2].setValore(cfg.getBase());

    property[3].setChiave(LdapManager.CHIAVE_DN);
    property[3].setValore(cfg.getDn());

    property[4].setChiave(LdapManager.CHIAVE_FILTRO_OU);
    property[4].setValore(cfg.getFiltroOU());

    property[5].setChiave(LdapManager.CHIAVE_FILTRO_UTENTI);
    property[5].setValore(cfg.getFiltroUtenti());

    property[6].setChiave(LdapManager.CHIAVE_ATTRIBUTO_LOGIN);
    property[6].setValore(cfg.getAttributoLogin());

    property[7].setChiave(LdapManager.CHIAVE_ATTRIBUTO_NOME);
    property[7].setValore(cfg.getAttributoNome());

    if (numeroProperties == 9) {
      // sono nel caso di cambio DN, aggiungo la password (da
      // sbiancare)
      property[8].setChiave(LdapManager.CHIAVE_PASSWORD);
    }

    this.propsConfigManager.insertProperties(property);
  }

  /**
   * Aggiorna la property relativa alla password
   *
   * @param password
   *        la password da aggiornare nel DB
   * @throws CriptazioneException
   *         eccezione generata nel caso in cui la cifratura della password
   *         fallisca
   */
  public void updatePassword(String password) throws CriptazioneException {
    String passwordCifrata = null;
    if (password != null) {
      // solo nel caso di password valorizzata devo applicare la cifratura
      ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          password.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
      passwordCifrata = new String(criptatore.getDatoCifrato());
    }

    PropsConfig[] props = { new PropsConfig() };
    props[0].setCodApp(CostantiGenerali.CODICE_APPLICAZIONE_LIBRERIA_GENERALE_WEB);
    props[0].setChiave(LdapManager.CHIAVE_PASSWORD);
    props[0].setValore(passwordCifrata);

    this.propsConfigManager.insertProperties(props);
  }

  /**
   * Crea un filtro in AND composto dai singoli filtri passati come parametro
   * Non effettua controlli sui filtri creati e passati
   *
   * @param filtri
   * @return
   */
  private String creaFiltro(String[] filtri) {
    String filtro = "(&";
    for (int i = 0; i < filtri.length; i++) {
      // inserisco la parentesi di apertura se non c'è
      if (filtri[i].charAt(0) != '(') filtro += "(";

      filtro += filtri[i];
      // inserisco la parentesi di chiusura se non c'è
      if (filtro.charAt(filtro.length() - 1) != ')') filtro += ")";

    }
    filtro += ")";

    return filtro;
  }

  /**
   * Estrae la lista degli utenti LDAP in base al filtro settato nella pagina di
   * trova e in base al filtro utente settato nella configurazione del server
   *
   * @param ldapTemplate
   * @param filtroPagina
   * @param filtroUtente
   * @return
   * @throws CriptazioneException
   */
  private List cerca(LdapTemplate ldapTemplate, String filtroPagina)
      throws CriptazioneException {
    ConfigurazioneLdap server = this.getConfigurazione();
    // se il filtro non è stato valorizzato inserisco un filtro generico con la
    // sola wildcard
    if (filtroPagina.equals("")) filtroPagina = "*";
    Filter cnFilter = new LikeFilter("cn", filtroPagina);
    String filtro = this.creaFiltro(new String[] { cnFilter.encode(),
        server.getFiltroUtenti() });

    return ldapTemplate.search("", filtro, new AccountLdapContextMapper(
        server.getAttributoNome(), server.getAttributoLogin()));

  }

  /**
   * Estrae la lista degli utenti LDAP in base al filtro settato nella pagina di
   * trova, in base al filtro sulle unità organizzative e al filtro utente
   * settato nella configurazione del server
   *
   * @param filtroPagina
   * @param filtroOU
   * @return
   * @throws CriptazioneException
   * @throws AuthenticationException
   * @throws Exception
   */
  private List cerca(String filtroPagina, String filtroOU)
      throws CriptazioneException, AuthenticationException, Exception {

    ConfigurazioneLdap server = this.getConfigurazione();
    server.setBase(filtroOU);

    LdapTemplate ldapTemplate = this.connettiServer(server);
    // se il filtro non è stato valorizzato inserisco un filtro generico con la
    // sola wildcard
    if (filtroPagina.equals("")) filtroPagina = "*";
    Filter cnFilter = new LikeFilter("cn", filtroPagina);
    String filtro = this.creaFiltro(new String[] { cnFilter.encode(),
        server.getFiltroUtenti() });

    return ldapTemplate.search("", filtro, new AccountLdapContextMapper(
        server.getAttributoNome(), server.getAttributoLogin()));

  }

  /**
   * Estrae la lista degli utenti in base al filtro settato nella pagina di
   * trova
   *
   * @param filtroPagina
   * @return lista degli utenti in base al filtro settato nella pagina di trova
   * @throws CriptazioneException
   * @throws AuthenticationException
   * @throws Exception
   */
  public List getAccountLdap(String filtroPagina) throws CriptazioneException,
      AuthenticationException, Exception {

    LdapTemplate ldapTemplate = this.connettiServer();

    return this.cerca(ldapTemplate, filtroPagina);
  }

  /**
   * Estrae la lista degli utenti in base al filtro settato nella pagina di
   * trova e in base al filtro sulle unità organizzative
   *
   * @param filtroPagina
   * @param filtroOU
   * @return lista degli utenti in base al filtro settato nella pagina di
   * trova e in base al filtro sulle unità organizzative
   * @throws CriptazioneException
   * @throws AuthenticationException
   * @throws Exception
   */
  public List getAccountLdap(String filtroPagina, String filtroOU)
      throws CriptazioneException, AuthenticationException, Exception {

    return this.cerca(filtroPagina, filtroOU);

  }

  /**
   * Estrae un AccountLdap in base al DN
   *
   * @param dn
   * @return
   * @throws CriptazioneException
   * @throws AuthenticationException
   * @throws Exception
   */
  public AccountLdap getAccountLdapByDn(String dn) throws CriptazioneException,
      AuthenticationException, Exception {

    ConfigurazioneLdap server = this.getConfigurazione();
    // si sbianca il persorso base perchè è già incluso nel dn
    server.setBase("");
    LdapTemplate ldapTemplate = this.connettiServer(server);

    return (AccountLdap) ldapTemplate.lookup(dn, new AccountLdapContextMapper(
        server.getAttributoNome(), server.getAttributoLogin()));
  }

  /**
   * Estrae una lista di unità organizzative
   *
   * @param searchClass
   *        calsse in cui ricercare
   * @param valueClass
   *        valore da ricercare
   * @param ldapTemplate
   * @return lista di oggetti di tipo AccountLdap
   * @throws Exception
   */
  public List getOrganizationalUnit(String filtroOu) throws Exception {

    LdapTemplate template = this.connettiServer();

    return template.search("", filtroOu, new OULdapContextMapper());
  }

  /**
   * Effettua la connessione al server Ldap passato come parametro
   *
   * @param server
   *        dati del server
   * @return LdapTemplate di connessione al server
   * @throws Exception
   */
  public LdapTemplate connettiServer(ConfigurazioneLdap server) throws Exception {

    LdapContextSource contextSource = this.getContextSource(server);

    // creo il canale di collegamento
    LdapTemplate template = new LdapTemplate(contextSource);

    // viene settato questo flag per evitare che per un solo dato incompleto si
    // blocchi tutta la lista
    template.setIgnorePartialResultException(true);

    return template;
  }

  /**
   * Effettua la connessione al server Ldap configurato in db
   *
   *
   * @return LdapTemplate di connessione al server
   * @throws Exception
   */
  public LdapTemplate connettiServer() throws Exception {

    ConfigurazioneLdap server = this.getConfigurazione();

    LdapContextSource contextSource = this.getContextSource(server);

    // creo il canale di collegamento
    LdapTemplate template = new LdapTemplate(contextSource);

    // viene settato questo flag per evitare che per un solo dato incompleto si
    // blocchi tutta la lista
    template.setIgnorePartialResultException(true);

    return template;
  }

  /**
   * Crea il contextSource da cui partire per la connessione al server LDAP
   *
   * @param server
   * @return
   * @throws AuthenticationException
   * @throws Exception
   */
  private LdapContextSource getContextSource(ConfigurazioneLdap server)
      throws AuthenticationException, Exception {

    LdapContextSource contextSource = new LdapContextSource();
    String url = null;
    // url di connessione standard ldap://<indirizzo>:<porta> (oppure ldaps://<indirizzo>:<porta>)
    if (!server.getServer().toLowerCase().startsWith(LDAP_SERVER) && !server.getServer().toLowerCase().startsWith(LDAPS_SERVER)) {
      url = LDAP_SERVER + server.getServer() + ":" + server.getPorta();
    } else {
      // il protocollo e' presente nel nome server, quindi non inserisco il prefisso
      url = server.getServer() + ":" + server.getPorta();
    }
    contextSource.setUrl(url);
    contextSource.setBase(server.getBase());
    contextSource.setUserDn(server.getDn());
    contextSource.setPassword(server.getPassword());
    contextSource.setDirObjectFactory(DefaultDirObjectFactory.class);
    contextSource.setContextFactory(com.sun.jndi.ldap.LdapCtxFactory.class);

    // rendo effettive i settaggi effettuati
    contextSource.afterPropertiesSet();

    // testo la connessione
    contextSource.getReadWriteContext();

    return contextSource;

  }

  /**
   * Verifica se l'utente riesce a connettersi al server LDAP
   *
   * @param dn
   *        DistinguishedName dell'utente
   * @param password
   *        Password dell'utente
   * @throws AuthenticationException
   *         eccezione ritornata nel caso non sia possibile leggere o scrivere
   *         nella connessione, e quindi la verifica non va a buon fine
   * @throws Exception
   *         eccezione ritornata nel caso non sia stata configurato
   *         correttamente il contesto di connessione al server LDAP
   */
  public void verificaAccount(String dn, String password)
      throws AuthenticationException, Exception {

    ConfigurazioneLdap server = this.getConfigurazione();

    server.setDn(dn);
    server.setPassword(password);

    this.getContextSource(server);
  }

  class AccountLdapContextMapper implements ContextMapper {

    private String attributoLogin;
    private String attributoNome;

    public AccountLdapContextMapper(String attributoNome, String attributoLogin) {
      this.attributoNome = attributoNome;
      this.attributoLogin = attributoLogin;
    }

    public Object mapFromContext(Object ctx) {
      AccountLdap account = new AccountLdap();
      DirContextAdapter context = (DirContextAdapter) ctx;

      account.setDn(context.getNameInNamespace());
      account.setCn(context.getStringAttribute(attributoNome));
      account.setSn(context.getStringAttribute(attributoLogin));
      return account;
    }
  }

  class OULdapContextMapper implements ContextMapper {

    public Object mapFromContext(Object ctx) {
      AccountLdap account = new AccountLdap();
      DirContextAdapter context = (DirContextAdapter) ctx;

      account.setDn(context.getNameInNamespace());
      String nomeOU = account.getDn();
      if (account.getDn().indexOf(",") != -1
          && account.getDn().indexOf(",") > account.getDn().indexOf("="))
        nomeOU = account.getDn().substring(account.getDn().indexOf("=") + 1,
            account.getDn().indexOf(","));
      account.setSn(nomeOU);
      return account;
    }
  }
}