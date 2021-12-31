/*
 * Created on 12/04/ 2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.bl;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityFiscali;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;


/**
 * Classe che gestisce alcune funzionalita di business di base generali
 *
 * @author cit_franceschin
 */
public class AnagraficaManager {

  /** Manager per le transazioni e selezioni nel database */
  private SqlManager  sqlManager;

  /** Manager per l'interrogazione dei tabellati */
  private TabellatiManager    tabellatiManager;


  /** Manager per le transazioni e selezioni nel database */
  private GeneManager geneManager;


  /**
   * @param sqlManager
   *        sqlManager da settare internamente alla classe.
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }


  /**
   * Si controlla l'unicità del codice fiscale o partita i.v.a.
   * Qualora non vi è l'uncità, viene costruito un elenco delle occorrenze con
   * duplicati
   *
   * @param parametri
   *        contiene entita, campoChiave, campoControllo, campoAnagarafico, nome
   * @param chiave
   * @param valoreControllo
   * @param codiceAnagrafico
   * @param ufficiIntestatariAbilitati
   *
   * @throws GestoreException
   *
   * @return String
   *         true se il campo è visibile e modificabile
   *         false altrimenti
   */
  public String controlloUnicitaCodiceFiscalePIVA(String parametri[],String chiave, String valoreControllo, String codiceAnagrafico,
      String ufficiIntestatariAbilitati, Boolean isHTML) throws GestoreException{
    String ret=null;
    String entita = parametri[0];
    String campoChiave = parametri[1];
    String campoControllo = parametri[2];
    String campoAnagarafico = parametri[3];
    String nome = parametri[4];
    boolean anagraficaFiltrata = this.anagraficaFiltrata(entita);
    try {
      List<?> datiAnagrafica = null;
      if ("1".equals(ufficiIntestatariAbilitati) && anagraficaFiltrata)
        datiAnagrafica = sqlManager.getListVector(
            "select " + campoChiave + ", " + nome + " from " + entita + "  where " + campoChiave + "<> ? and " + campoControllo + " = ? and " + campoAnagarafico + " = ?",
            new Object[] { chiave,valoreControllo,codiceAnagrafico});
      else
        datiAnagrafica = sqlManager.getListVector(
            "select " + campoChiave +  ", " + nome + " from " + entita + " where " + campoChiave + " <> ? and " + campoControllo + " = ?",
            new Object[] { chiave, valoreControllo });

      if (datiAnagrafica != null && datiAnagrafica.size() > 0) {
        if(isHTML){
          StringBuffer buf = new StringBuffer("<br><ul>");
          for (int i = 0; i < datiAnagrafica.size(); i++) {
            buf.append("<li style=\"list-style-type: disc;margin-left: 30px;\" >");
            buf.append(SqlManager.getValueFromVectorParam(datiAnagrafica.get(i), 0).stringValue());
            buf.append(" - ");
            buf.append(SqlManager.getValueFromVectorParam(datiAnagrafica.get(i), 1).stringValue());
            buf.append("</li>");
          }
          buf.append("</ul>");
          ret = buf.toString();
        }else{
          StringBuffer buf = new StringBuffer("");
          for (int i = 0; i < datiAnagrafica.size(); i++) {
            if(i > 0) buf.append(" , ");
            buf.append(SqlManager.getValueFromVectorParam(datiAnagrafica.get(i), 0).stringValue());
            buf.append(" - ");
            buf.append(SqlManager.getValueFromVectorParam(datiAnagrafica.get(i), 1).stringValue());
          }
          ret = buf.toString();
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'estrazione dei dati per effettuare la verifica del codice fiscale",
          "checkCFePIVA", e);
    }


    return ret;
  }

  /**
   * Viene letto il valore del tabellato "G_012" con tab1tip=2.
   * @return boolean
   *         true se è attivo il controllo sull'unicità
   *         false altrimenti
   */
  public boolean getAbilitazioneControlloUnicita(){
    boolean ret = false;

    String descTabellato = tabellatiManager.getDescrTabellato("G_012", "2");
    if (descTabellato != null && descTabellato.length()>1)
      descTabellato = descTabellato.substring(0, 1);

    if("1".equals(descTabellato))
      ret = true;

    return ret;

  }

  /**
   * Viene determinato se il "campo" dell'entita "entita" per il profilo attivo "profiloAttivo"
   * è visibile e modificabile
   *
   * @param entita
   * @param campo
   * @param profiloAttivo
   *
   * @return boolean
   *         true se il campo è visibile e modificabile
   *         false altrimenti
   */
  public boolean campoVisibileModificabile(String entita, String campo, String  profiloAttivo){
    boolean ret = false;

    boolean campoVisibile = geneManager.getProfili().checkProtec(profiloAttivo, "COLS", "VIS", "GENE." + entita + "." + campo);
    boolean campoModificabile = geneManager.getProfili().checkProtec(profiloAttivo, "COLS", "MOD", "GENE." + entita + "." + campo);

    if(campoVisibile && campoModificabile)
      ret=true;

    return ret;
  }

  /**
   * Viene prelevata la descrizione del tabellato associato al campo NAZIMP, e si controlla
   * se questa corrisponde a ITALIA
   *
   * @param tabellatiManager
   * @param nazionalita
   *
   * @return boolean,   true ITALIA
   *                    false altrimenti
   *
   */
  public boolean isNazionalitaItalia(Long nazionalita) {
    boolean isItalia=true;
    if(nazionalita!=null){
      String descTabellato = tabellatiManager.getDescrTabellato("Ag010", nazionalita.toString());
      if(descTabellato!= null && !"ITALIA".equals(descTabellato.toUpperCase()))
        isItalia=false;
    }

    return isItalia;
  }

  /**
   * Si determina se vi sono le condizioni per saltare il controllo sull'obbligatorietà della partita iva,
   * ossia se tipimp=6 e nel tabellato G_045 per tab1tip=1 è presente il valore 1 oppure
   * se tipimp=13 e nel tabellato G_045 per tab1tip=2 è presente il valore 1
   *
   * @param tabellatiManager
   * @param tipimp
   *
   * @return boolean,   true saltare controllo
   *                    false altrimenti
   *
   */
  public boolean saltareControlloObbligPiva(Long tipimp) {
    boolean saltareControllo=false;
    String descTabellatoTipo1 = tabellatiManager.getDescrTabellato("G_045", "1");
    String descTabellatoTipo2 = tabellatiManager.getDescrTabellato("G_045", "2");
    if (descTabellatoTipo1 != null && descTabellatoTipo1.length()>1)
      descTabellatoTipo1 = descTabellatoTipo1.substring(0, 1);
    if (descTabellatoTipo2 != null && descTabellatoTipo2.length()>1)
      descTabellatoTipo2 = descTabellatoTipo2.substring(0, 1);

    if((descTabellatoTipo1!=null && "1".equals(descTabellatoTipo1) && tipimp!=null && tipimp.longValue()==6)||(descTabellatoTipo2!=null && "1".equals(descTabellatoTipo2) && tipimp!=null && tipimp.longValue()==13)){
      saltareControllo = true;
    }

    return saltareControllo;
  }

  /**
   * Metodo che controlla la validità del codice fiscale
   *
   * @param cf
   *
   * @return boolean,   true codice fiscale valido
   *                    false altrimenti
   *
   */
  public boolean controlloCF(String cf) {
    boolean ret= true;

    // Se il primo carattere e un numero si tratta di una partita iva
    if("1234567890".indexOf(cf.charAt(0))>=0)
        return UtilityFiscali.isValidPartitaIVA(cf,true);

    ret = UtilityFiscali.isValidCodiceFiscale(cf);

    return ret;
  }

  /**
   * Metodo che controlla se sono rispettate le condizioni per la registrazione di una impresa su portale
   *
   * @param codimp
   * @param messaggiHtml
   * @return String contenente i controlli non superati, altrimenti null
   * @throws SQLException
   *
   */
  public String controlliImpresaRegistrabile(String codimp, boolean messaggiHtml)
      throws SQLException {

    // String messaggio="";
    String messaggio = null;
    String controlloSuperato = "SI";
    String valore;
    String mail;
    String pec;
    StringBuffer msg = new StringBuffer("");

    if (messaggiHtml)
      messaggio = "Non è possibile procedere con la registrazione sul portale Appalti.";

    String select = "select nomest,natgiui,tipimp,cfimp,pivimp,indimp,nciimp,proimp,capimp,locimp,nazimp,mgsflg,emaiip,emai2ip"
        + " from impr where codimp = ?";
    Vector<?> datiIMPR = sqlManager.getVector(select, new Object[] { codimp });

    if (datiIMPR != null) {
      Long nazimp = (Long) ((JdbcParametro) datiIMPR.get(10)).getValue();
      Long tipimp = (Long) ((JdbcParametro) datiIMPR.get(2)).getValue();
      boolean isNazionalitaItalia = this.isNazionalitaItalia(nazimp);
      // Controllo Ragione Sociale
      valore = ((JdbcParametro) datiIMPR.get(0)).getStringValue();
      if (valore == null || "".equals(valore)) {
        controlloSuperato = "NO";
        if (messaggiHtml)
          messaggio += "<br>Il campo Ragione sociale non è valorizzato.";
        else
          msg.append("Il campo Ragione sociale non è valorizzato.\n");

      }

      // Controllo Codice fiscale
      valore = ((JdbcParametro) datiIMPR.get(3)).getStringValue();
      if (valore == null || "".equals(valore)) {
        controlloSuperato = "NO";
        if (messaggiHtml)
          messaggio += "<br>Il campo Codice fiscale non è valorizzato.";
        else
          msg.append("Il campo Codice fiscale non è valorizzato.\n");

      } else if (!this.controlloCF(valore) && isNazionalitaItalia) {
        controlloSuperato = "NO";
        if (messaggiHtml)
          messaggio += "<br>Il campo Codice fiscale presenta un valore non valido.";
        else
          msg.append("Il campo Codice fiscale presenta un valore non valido.\n");

      }

      // Controllo Partita IVA
      valore = ((JdbcParametro) datiIMPR.get(4)).getStringValue();
      if ((valore == null || "".equals(valore))
          && !this.saltareControlloObbligPiva(tipimp)) {
        controlloSuperato = "NO";
        if (messaggiHtml)
          messaggio += "<br>Il campo Partita I.V.A. o V.A.T. non è valorizzato.";
        else
          msg.append("Il campo Partita I.V.A. o V.A.T. non è valorizzato.\n");

      } else if (valore != null
          && !"".equals(valore)
          && !UtilityFiscali.isValidPartitaIVA(valore, isNazionalitaItalia)) {
        controlloSuperato = "NO";
        if (isNazionalitaItalia) {
          if (messaggiHtml)
            messaggio += "<br>Il campo Partita IVA presenta un valore non valido.";
          else
            msg.append("Il campo Partita IVA presenta un valore non valido.\n");
        }
      }

      // Controllo Pec
      pec = ((JdbcParametro) datiIMPR.get(13)).getStringValue();

      // Controllo Email
      mail = ((JdbcParametro) datiIMPR.get(12)).getStringValue();
      if ((pec == null || "".equals(pec)) && (mail == null || "".equals(mail))) {
        controlloSuperato = "NO";
        if (messaggiHtml)
          messaggio += "<br>I campi e-mail e PEC non sono valorizzati. Deve esserne valorizzato almeno uno.";
        else
          msg.append("I campi e-mail e PEC non sono valorizzati. Deve esserne valorizzato almeno uno.\n");

      }
      InternetAddress indEmail = null;
      // Controllo validità formato email
      if (mail != null && !"".equals(mail)) {
        try {
          indEmail = new InternetAddress(mail);
          indEmail.validate();
        } catch (AddressException ex) {
          if (messaggiHtml)
            messaggio += "<br>Il campo E-mail contiene il valore '"
                + mail
                + "' in formato non valido.";
          else
            msg.append("Il campo E-mail contiene il valore '"
                + mail
                + "' in formato non valido.\n");
        }
      }

      // Controllo validità formato pec
      if (pec != null && !"".equals(pec)) {
        indEmail = null;
        try {
          indEmail = new InternetAddress(pec);
          indEmail.validate();
        } catch (AddressException ex) {
          if (messaggiHtml)
            messaggio += "<br>Il campo PEC contiene il valore '"
                + pec
                + "' in formato non valido.";
          else
            msg.append("Il campo PEC contiene il valore '"
                + pec
                + "' in formato non valido.\n");
        }
      }

      //controllo nazionalità
      if(nazimp==null){
        controlloSuperato = "NO";
        if (messaggiHtml)
          messaggio += "<br>Il campo Nazione non è valorizzato.";
        else
          msg.append("Il campo Nazione non è valorizzato.\n");
      }
    }

    if ("SI".equals(controlloSuperato) && messaggiHtml)
      messaggio = null;
    else if ("NO".equals(controlloSuperato) && !messaggiHtml)
      messaggio = msg.toString();

    return messaggio;

  }

  public boolean anagraficaFiltrata(String entita){
    boolean ret=false;
    String archiviFiltrati = ConfigManager.getValore(CostantiGenerali.PROP_UFFINT_ARCHIVIFILTRATI);
    String elencoArchivi[] = archiviFiltrati.split(";");
    if(elencoArchivi.length>0){
      for(int i=0;i<elencoArchivi.length;i++){
        if(elencoArchivi[i].equals(entita.toUpperCase())){
          ret= true;
          break;
        }
      }
    }
    return ret;
  }

  /**
   * Metodo che controlla se per l'impresa in esame(Consorzio o raggruppamento) sono presenti almeno due componenti
   *
   * @param codimp
   * @return boolean esito del controllo
   * @throws SQLException
   *
   */
  public boolean controlloPresentaComponentiRaggruppamento(String codimp) throws SQLException{
    boolean ret=true;
    Long numeroComponenti = (Long) sqlManager.getObject("select count(codime9) from ragimp where codime9=?", new Object[]{codimp});
    if(numeroComponenti==null || numeroComponenti.longValue()<2)
      ret=false;
    return ret;

  }
}