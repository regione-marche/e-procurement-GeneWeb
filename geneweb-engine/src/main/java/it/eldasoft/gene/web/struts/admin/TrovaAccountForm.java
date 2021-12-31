/*
 * Created on 30/mar/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.gene.db.domain.admin.TrovaAccount;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form per la memorizzazione dei criteri di filtro da applicare nella ricerca
 * degli utenti
 *
 * @author Stefano.Sabbadin
 */
public class TrovaAccountForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID = -5781323251043286752L;
  private final String[]          listaValuePrivilegi = CostantiGeneraliAccount.LISTA_VALUE_PRIVILEGI;
  private final String[]          listaTextPrivilegi  = CostantiGeneraliAccount.LISTA_TEXT_PRIVILEGI;

  /** descrizione dell'utente, corrispondente al campo USRSYS.SYSUTE */
  private String            descrizione;
  /** operatore di confronto per il campo descrizione */
  private String            operatoreDescrizione;
  /** login dell'utente, corrispondente al campo USRSYS.SYSNOM */
  private String            nome;
  /** operatore di confronto per il campo nome */
  private String            operatoreNome;
  /** utente disabilitato, corrispondente al campo USRSYS.SYSDISAB */
  private String            utenteDisabilitato;
  /** utente LDAP, corrispondente al campo USRSYS.FLAG_LDAP */
  private String            utenteLDAP;
  /** numero di risultati da visualizzare per pagina */
  private String            risPerPagina;
  /** true se la ricerca non è case sensitive, false altrimenti */
  private boolean           noCaseSensitive;
  /** true se la pagina deve visualizzare gli operatori, false altrimenti */
  private boolean			visualizzazioneAvanzata;
  /** ufficio di appartenenza. */
  private String            ufficioAppartenenza;
  /** codice fiscale. */
  private String            codiceFiscale;
  /** operatore di confronto per il campo codiceFiscale */
  private String            operatoreCodiceFiscale;
  /** indirizzo e-mail. */
  private String            eMail;
  /** operatore di confronto per il campo eMail */
  private String            operatoreEMail;
  /** categoria. */
  private String            categoria;
  /** nome ufficio inserito */
  private String            uffint;
  /** operatore di confronto per il campo uffint */
  private String            operatoreUffint;
  /** utente amministratore*/
  private String            amministratore;
  /** utente con diritti gestione utenti */
  private String            gestioneUtenti;
  
  /**
   * Costruttore della classe: inizializza le variabili a vuote
   */
  public TrovaAccountForm() {
    this.inizializzaOggetto();
  }

  /**
   * Esecuzione del reset
   */
  @Override
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    this.inizializzaOggetto();
  }

  /**
   * Inizializza un oggetto vuoto
   */
  private void inizializzaOggetto() {
    // ATTENZIONE: gli operatori nella form di trova di default sono impostati a
    // "contiene" in modo da effettuare il like
    // Nel momento in cui si crea l'oggetto per il model, si va a sovrascrivere
    // l'impostazione di default degli operatori nell'oggetto del model

    this.descrizione = null;
    this.operatoreDescrizione = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.nome = null;
    this.operatoreNome = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.utenteDisabilitato = null;
    this.utenteLDAP = null;
    this.risPerPagina = "20";
    // campo collegato a una checkbox, non va va inizializzato direttamente a true ma variato prima di passarlo alla pagina in quanto il
    // setter viene richiamato solo se la checkbox e' selezionata pertanto non verrebbe mai resettato a false
    this.noCaseSensitive = false;
    this.visualizzazioneAvanzata = false;
    this.ufficioAppartenenza = null;
    this.eMail = null;
    this.operatoreEMail = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.codiceFiscale = null;
    this.operatoreCodiceFiscale = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.categoria = null;
    this.uffint = null;
    this.operatoreUffint = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.gestioneUtenti = null;
    this.amministratore = null;
  }

  /**
   * @return bean per la Business Logic a partire dall'oggetto attuale
   */
  public TrovaAccount getDatiPerModel() {
    String comandoEscape = null;
    try {
      SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
      comandoEscape = composer.getEscapeSql();
    } catch (SqlComposerException e) {
      // non si verifica mai, il caricamento metadati gia' testa che la
      // property sia settata correttamente
    }

    TrovaAccount trovaAccount = new TrovaAccount();
    
    trovaAccount.setOperatoreDescrizione(
        GestioneOperatoreConfrontoStringa.convertiOperatoreConfronto(
            this.operatoreDescrizione));
    String descrizione = this.descrizione;
    if (!"=".equals(this.operatoreDescrizione) && UtilityStringhe.containsSqlWildCards(this.descrizione)) {
      trovaAccount.setEscapeDescrizione(comandoEscape);
      descrizione = UtilityStringhe.escapeSqlString(this.descrizione);
    }
    trovaAccount.setDescrizione(
        UtilityStringhe.convertiStringaVuotaInNull(
            GestioneOperatoreConfrontoStringa.convertiStringaConfronto(
                this.operatoreDescrizione, descrizione)));

    trovaAccount.setOperatoreNome(
        GestioneOperatoreConfrontoStringa.convertiOperatoreConfronto(
            this.operatoreNome));
    String nome = this.nome;
    if (!"=".equals(this.operatoreNome) && UtilityStringhe.containsSqlWildCards(this.nome)) {
      trovaAccount.setEscapeNome(comandoEscape);
      nome= UtilityStringhe.escapeSqlString(this.nome);
    }
    trovaAccount.setNome(UtilityStringhe.convertiStringaVuotaInNull(
        GestioneOperatoreConfrontoStringa.convertiStringaConfronto(
            this.operatoreNome, nome)));

    trovaAccount.setOperatoreCodiceFiscale(
        GestioneOperatoreConfrontoStringa.convertiOperatoreConfronto(
            this.operatoreCodiceFiscale));
    String codiceFiscale = this.codiceFiscale;
    if (!"=".equals(this.operatoreCodiceFiscale) && UtilityStringhe.containsSqlWildCards(this.codiceFiscale)) {
      trovaAccount.setEscapeCodiceFiscale(comandoEscape);
      codiceFiscale= UtilityStringhe.escapeSqlString(this.codiceFiscale);
    }
    trovaAccount.setCodiceFiscale(UtilityStringhe.convertiStringaVuotaInNull(
        GestioneOperatoreConfrontoStringa.convertiStringaConfronto(
            this.operatoreCodiceFiscale, codiceFiscale)));
    
    trovaAccount.setOperatoreEMail(
        GestioneOperatoreConfrontoStringa.convertiOperatoreConfronto(
            this.operatoreEMail));
    String eMail = this.eMail;
    if (!"=".equals(this.operatoreEMail) && UtilityStringhe.containsSqlWildCards(this.eMail)) {
      trovaAccount.setEscapeEMail(comandoEscape);
      eMail= UtilityStringhe.escapeSqlString(this.eMail);
    }
    trovaAccount.seteMail(UtilityStringhe.convertiStringaVuotaInNull(
        GestioneOperatoreConfrontoStringa.convertiStringaConfronto(
            this.operatoreEMail, eMail)));
    
    trovaAccount.setOperatoreUffint(
        GestioneOperatoreConfrontoStringa.convertiOperatoreConfronto(
            this.operatoreUffint));
    String uffint = this.uffint;
    if (!"=".equals(this.operatoreUffint) && UtilityStringhe.containsSqlWildCards(this.uffint)) {
      trovaAccount.setEscapeUffint(comandoEscape);
      uffint= UtilityStringhe.escapeSqlString(this.uffint);
    }
    trovaAccount.setUffint(UtilityStringhe.convertiStringaVuotaInNull(
        GestioneOperatoreConfrontoStringa.convertiStringaConfronto(
            this.operatoreUffint, uffint)));
    
    trovaAccount.setUtenteDisabilitato(
        StringUtils.stripToNull(this.utenteDisabilitato));
    trovaAccount.setUtenteLDAP(
        StringUtils.stripToNull(this.utenteLDAP));
    trovaAccount.setGestioneUtenti(
        StringUtils.stripToNull(this.gestioneUtenti));
    trovaAccount.setAmministratore(
        StringUtils.stripToNull(this.amministratore));
    trovaAccount.setNoCaseSensitive(this.noCaseSensitive);
    trovaAccount.setUfficioAppartenenza(StringUtils.stripToNull(this.ufficioAppartenenza));
    trovaAccount.setCategoria(StringUtils.stripToNull(this.categoria));
    return trovaAccount;
  }

  /**
   * @return Ritorna descrizione.
   */
  public String getDescrizione() {
    return descrizione;
  }

  /**
   * @param descrizione
   *        descrizione da settare internamente alla classe.
   */
  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  /**
   * @return Ritorna descrizione_conf.
   */
  public String getOperatoreDescrizione() {
    return operatoreDescrizione;
  }

  /**
   * @param descrizione_conf descrizione_conf da settare internamente alla classe.
   */
  public void setOperatoreDescrizione(String descrizione_conf) {
    this.operatoreDescrizione = descrizione_conf;
  }

  /**
   * @return Ritorna nome.
   */
  public String getNome() {
    return nome;
  }

  /**
   * @param nome
   *        nome da settare internamente alla classe.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * @return Ritorna nome_conf.
   */
  public String getOperatoreNome() {
    return operatoreNome;
  }

  /**
   * @param nome_conf nome_conf da settare internamente alla classe.
   */
  public void setOperatoreNome(String nome_conf) {
    this.operatoreNome = nome_conf;
  }

  /**
   * @return Ritorna utenteDisabilitato.
   */
  public String getUtenteDisabilitato() {
    return utenteDisabilitato;
  }

  /**
   * @param utenteDisabilitato
   *        utenteDisabilitato da settare internamente alla classe.
   */
  public void setUtenteDisabilitato(String utenteDisabilitato) {
    this.utenteDisabilitato = utenteDisabilitato;
  }

  /**
   * @return Ritorna utenteLDAP.
   */
  public String getUtenteLDAP() {
    return utenteLDAP;
  }

  /**
   * @param utenteLDAP
   *        utenteLDAP da settare internamente alla classe.
   */
  public void setUtenteLDAP(String utenteLDAP) {
    this.utenteLDAP = utenteLDAP;
  }

  /**
   * @return Ritorna risPerPagina.
   */
  public String getRisPerPagina() {
    return risPerPagina;
  }

  /**
   * @param risPerPagina
   *        risPerPagina da settare internamente alla classe.
   */
  public void setRisPerPagina(String risPerPagina) {
    this.risPerPagina = risPerPagina;
  }

  /**
   * @return Ritorna noCaseSensitive.
   */
  public String getNoCaseSensitive() {
    return String.valueOf(noCaseSensitive);
  }

  /**
   * @param noCaseSensitive
   *        noCaseSensitive da settare internamente alla classe.
   */
  public void setNoCaseSensitive(String noCaseSensitive) {
    if (Boolean.parseBoolean(noCaseSensitive)) {
      this.noCaseSensitive = true;
    } else {
      this.noCaseSensitive = false;
    }
  }

  /**
   * @return Ritorna visualizzazioneAvanzata
   */
  public boolean isVisualizzazioneAvanzata() {
    return visualizzazioneAvanzata;
  }

  /**
   *
   * @param visualizzazioneAvanzata
   */
  public void setVisualizzazioneAvanzata(boolean visualizzazioneAvanzata) {
    this.visualizzazioneAvanzata = visualizzazioneAvanzata;
}

  /**
   * @return Ritorna ufficioAppartenenza.
   */
  public String getUfficioAppartenenza() {
    return ufficioAppartenenza;
  }

  /**
   * @param ufficioAppartenenza ufficioAppartenenza da settare internamente alla classe.
   */
  public void setUfficioAppartenenza(String ufficioAppartenenza) {
    this.ufficioAppartenenza = ufficioAppartenenza;
  }

  /**
   * @param codiceFiscale
   */
  public void setCodiceFiscale(String codiceFiscale) {
    this.codiceFiscale = codiceFiscale;
  }
  
  /**
   * @return Ritorna codiceFiscale.
   */
  public String getCodiceFiscale() {
    return codiceFiscale;
  }
  
  /**
   * @param operatoreCodiceFiscale_conf operatoreCodiceFiscale_conf da settare internamente alla classe.
   */
  public void setOperatoreCodiceFiscale(String operatoreCodiceFiscale_conf) {
    this.operatoreCodiceFiscale = operatoreCodiceFiscale_conf;
  }
  
  /**
   * @return Ritorna OperatoreCodiceFiscale.
   */
  public String getOperatoreCodiceFiscale() {
    return operatoreCodiceFiscale;
  }
  
  /**
   * @param eMail
   */
   public void seteMail(String eMail) {
     this.eMail = eMail;
   }
   
   /**
    * @return Ritorna eMail.
    */
   public String geteMail() {
     return eMail;
   }
  

   /**
    * @param operatoreEMail_conf operatoreEMail_conf da settare internamente alla classe.
    */
   public void setOperatoreEMail(String operatoreEMail_conf) {
     this.operatoreEMail = operatoreEMail_conf;
   }
   
   /**
    * @return Ritorna operatoreEMail.
    */
   public String getOperatoreEMail() {
     return operatoreEMail;
   }
  
  /**
   * @return Ritorna categoria.
   */
  public String getCategoria() {
    return categoria;
  }

  /**
   * @param categoria 
   */
  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }
  
  /**
   * @return Ritorna uffint.
   */
  public String getUffint() {
    return uffint;
  }
  
  /**
   * @param uffint codice ufficio intesattario 
   */
  public void setUffint(String uffint) {
    this.uffint = uffint;
  }
  
  public void setOperatoreUffint(String operatoreUffint) {
    this.operatoreUffint = operatoreUffint;
  }
  
  
  public String getOperatoreUffint() {
    return operatoreUffint;
  }

  
  public String getAmministratore() {
    return amministratore;
  }

  
  public void setAmministratore(String amministratore) {
    this.amministratore = amministratore;
  }

  
  public String getGestioneUtenti() {
    return gestioneUtenti;
  }

  
  public void setGestioneUtenti(String gestioneUtenti) {
    this.gestioneUtenti = gestioneUtenti;
  }

  public String[] getListaValuePrivilegi() {
    return listaValuePrivilegi;
  }

  public String[] getListaTextPrivilegi() {
    return listaTextPrivilegi;
  }
  

}