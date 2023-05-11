/*
 * Created on 05/apr/2018
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.integrazioni;

import it.cineca.u_gov.ac.sc.ws.WSACSoggettoCollettivoService;
import it.cineca.u_gov.ac.sc.ws.Ws_002fPrivate_002fSoggettoCollettivo;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.erp.WSERPUgovAnagraficaType;
import it.maggioli.eldasoft.ws.erp.WSERP_PortType;
import it.maggioli.eldasoft.ws.erp.WSERP_ServiceLocator;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Integrazione con i servizi Kinisi' per la gestione delle verifiche
 * documentali relative all'art. 80
 *
 * @author Stefano.Cestaro
 *
 */
public class CinecaAnagraficaComuneManager {

  static Logger               logger                         = Logger.getLogger(CinecaAnagraficaComuneManager.class);

  private static final String PROP_WSERP_ERP_URL                              = "wserp.erp.url";

  private SqlManager          sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   *  Dati soggetto collettivo
   *
   * @param codimp
   * @return HashMap
   * @throws GestoreException
   */
  public HashMap<String, Object> getDatiAnagraficaMaggioli(String dittaCineca)
    throws GestoreException {


    HashMap<String, Object> datiBaseSoggettoCollettivo = new HashMap<String, Object>();

    String selectFornitore = "select codimp, tipimp, nomest, cfimp, pivimp, indimp, nciimp, locimp, proimp, capimp," +
    " telimp, faximp, nazimp, dcciaa, natgiui, emai2ip, telcel, indweb, coorba, emaiip, banapp," +
    " cnatec, dnatec, annoti, cognome, nome, sextec, tipalb, inctec, codbic " +
    " from impr where codimp = ? ";
    String selectLegaliRappresentanti = "select nomleg from impleg where codimp2 = ?" +
            " and coalesce(legini,TO_DATE('1111/01/01', 'YYYY/MM/DD')) <= ?" +
            "  and coalesce(legfin,TO_DATE('2222/02/02', 'YYYY/MM/DD')) >= ?";

    Vector<?> datiIMPR;
    try {
      datiIMPR = this.sqlManager.getVector(selectFornitore, new Object[]{dittaCineca});
      Vector<?> datiIMPLEG = this.sqlManager.getVector(selectLegaliRappresentanti, new Object[]{dittaCineca, UtilityDate.getDataOdiernaAsDate(), UtilityDate.getDataOdiernaAsDate()});
      if(datiIMPR!=null && datiIMPR.size()>0){
        String codimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 0).getValue();
        Long tipologia = (Long) SqlManager.getValueFromVectorParam(datiIMPR, 1).getValue();
        String ragioneSociale = (String) SqlManager.getValueFromVectorParam(datiIMPR, 2).getValue();
        String codiceFiscale = (String) SqlManager.getValueFromVectorParam(datiIMPR, 3).getValue();
        String partitaIva = (String) SqlManager.getValueFromVectorParam(datiIMPR, 4).getValue();
        String indirizzoPrincipale = (String) SqlManager.getValueFromVectorParam(datiIMPR, 5).getValue();
        String numCivicoPrincipale = (String) SqlManager.getValueFromVectorParam(datiIMPR, 6).getValue();
        String localitaPrincipale = (String) SqlManager.getValueFromVectorParam(datiIMPR, 7).getValue();
        String provinciaPrincipale = (String) SqlManager.getValueFromVectorParam(datiIMPR, 8).getValue();
        String capPrincipale = (String) SqlManager.getValueFromVectorParam(datiIMPR, 9).getValue();
        String telefonoPrincipale = (String) SqlManager.getValueFromVectorParam(datiIMPR, 10).getValue();
        String faxPrincipale = (String) SqlManager.getValueFromVectorParam(datiIMPR, 11).getValue();
        Long nazionePrincipale = (Long) SqlManager.getValueFromVectorParam(datiIMPR, 12).getValue();
        Date dataIscrCCIAA = (Date) SqlManager.getValueFromVectorParam(datiIMPR, 13).getValue();
        Long formaGiuridica = (Long) SqlManager.getValueFromVectorParam(datiIMPR, 14).getValue();
        String pecPrincipale = (String) SqlManager.getValueFromVectorParam(datiIMPR, 15).getValue();
        String cellPrincipale = (String) SqlManager.getValueFromVectorParam(datiIMPR, 16).getValue();
        String urlSitoWebPrincipale = (String) SqlManager.getValueFromVectorParam(datiIMPR, 17).getValue();
        String contoCorrenteDedicato = (String) SqlManager.getValueFromVectorParam(datiIMPR, 18).getValue();
        String emailPrincipale = (String) SqlManager.getValueFromVectorParam(datiIMPR, 19).getValue();
        String bancaAppoggio = (String) SqlManager.getValueFromVectorParam(datiIMPR, 20).getValue();
        String comuneNascita = (String) SqlManager.getValueFromVectorParam(datiIMPR, 21).getValue();
        Date dataNascita = (Date) SqlManager.getValueFromVectorParam(datiIMPR, 22).getValue();
        String annotazioni = (String) SqlManager.getValueFromVectorParam(datiIMPR, 23).getValue();
        String cognome = (String) SqlManager.getValueFromVectorParam(datiIMPR, 24).getValue();
        String nome = (String) SqlManager.getValueFromVectorParam(datiIMPR, 25).getValue();
        String genere = (String) SqlManager.getValueFromVectorParam(datiIMPR, 26).getValue();
        Long tipoAlboProf = (Long) SqlManager.getValueFromVectorParam(datiIMPR, 27).getValue();
        Long titoloOnorifico = (Long) SqlManager.getValueFromVectorParam(datiIMPR, 28).getValue();
        String codiceBic = (String) SqlManager.getValueFromVectorParam(datiIMPR, 29).getValue();

        //inizializzo i dati anagrafici
        datiBaseSoggettoCollettivo.put("codEsterno", codimp);
        datiBaseSoggettoCollettivo.put("tipologia", tipologia);
        datiBaseSoggettoCollettivo.put("ragioneSociale", ragioneSociale);
        if(datiIMPLEG!= null){
          String rapprLegale = (String) SqlManager.getValueFromVectorParam(datiIMPLEG, 0).getValue();
          datiBaseSoggettoCollettivo.put("rapprLegale", rapprLegale);
        }
        datiBaseSoggettoCollettivo.put("dataIscrCCIAA", dataIscrCCIAA);
        datiBaseSoggettoCollettivo.put("partitaIva", partitaIva);
        datiBaseSoggettoCollettivo.put("codiceFiscale", codiceFiscale);
        if(nazionePrincipale != null){
          String codNazioneSede = (String) this.sqlManager.getObject("select tab2tip from tab2 where tab2cod= ? and tab2d1 =? ",
              new Object[]{"UBUY1",nazionePrincipale.toString()});
          codNazioneSede = UtilityStringhe.convertiNullInStringaVuota(codNazioneSede);
          datiBaseSoggettoCollettivo.put("codNazioneSede", codNazioneSede);
          datiBaseSoggettoCollettivo.put("codNazioneDomFiscale", codNazioneSede);
        }
        if(formaGiuridica != null){
          datiBaseSoggettoCollettivo.put("formaGiuridica", formaGiuridica.toString());
          String codFormaGiuridica = "";
          if(new Long(9).equals(formaGiuridica)){
            //01/08/2018 Lelio mappiamo per covenzione la ditta individuale sempre in 33 (9->33)
            codFormaGiuridica = "33";
          }else{
            codFormaGiuridica = (String) this.sqlManager.getObject("select tab2d1 from tab2 where tab2cod= ? and tab2tip =? ",
                new Object[]{"UBUY2",formaGiuridica.toString()});
          }
          codFormaGiuridica = UtilityStringhe.convertiNullInStringaVuota(codFormaGiuridica);
          datiBaseSoggettoCollettivo.put("codFormaGiuridica", codFormaGiuridica);
        }

        datiBaseSoggettoCollettivo.put("capSede", capPrincipale);
        datiBaseSoggettoCollettivo.put("capDomFiscale", capPrincipale);
        datiBaseSoggettoCollettivo.put("indirizzoSede", indirizzoPrincipale);
        datiBaseSoggettoCollettivo.put("indirizzoDomFiscale", indirizzoPrincipale);
        datiBaseSoggettoCollettivo.put("civicoSede", numCivicoPrincipale);
        datiBaseSoggettoCollettivo.put("civicoDomFiscale", numCivicoPrincipale);
        if(localitaPrincipale != null){
          datiBaseSoggettoCollettivo.put("localitaPrincipale", localitaPrincipale);
          //prima la provo secca, altrimenti con il like
          String codComuneSede = (String) this.sqlManager.getObject("select tabcod5 from tabsche where tabcod='S2003' and tabcod1='09' and tabdesc = ? ", new Object[]{localitaPrincipale.toUpperCase()});
          codComuneSede = UtilityStringhe.convertiNullInStringaVuota(codComuneSede);
          if("".equals(codComuneSede)){
            codComuneSede = (String) this.sqlManager.getObject("select tabcod5 from tabsche where tabcod='S2003' and tabcod1='09' and tabdesc like ? ", new Object[]{"%" + localitaPrincipale.toUpperCase() + "%"});
          }
          codComuneSede = UtilityStringhe.convertiNullInStringaVuota(codComuneSede);
          if(!"".equals(codComuneSede)){
            datiBaseSoggettoCollettivo.put("codComuneSede", codComuneSede);
            datiBaseSoggettoCollettivo.put("codComuneDomFiscale", codComuneSede);

          }
        }
        datiBaseSoggettoCollettivo.put("telUfficio", telefonoPrincipale);
        datiBaseSoggettoCollettivo.put("cellUfficio", cellPrincipale);
        datiBaseSoggettoCollettivo.put("faxUfficio", faxPrincipale);
        datiBaseSoggettoCollettivo.put("pecUfficio", pecPrincipale);
        datiBaseSoggettoCollettivo.put("urlSitoWeb", urlSitoWebPrincipale);
        datiBaseSoggettoCollettivo.put("ccDedicato", contoCorrenteDedicato);
        datiBaseSoggettoCollettivo.put("emailUfficio", emailPrincipale);
        datiBaseSoggettoCollettivo.put("bic", codiceBic);

        if(comuneNascita != null){
          String codComuneNascita = (String) this.sqlManager.getObject("select tabcod5 from tabsche where tabcod='S2003' and tabcod1='09' and tabdesc = ? ", new Object[]{comuneNascita.toUpperCase()});
          codComuneNascita = UtilityStringhe.convertiNullInStringaVuota(codComuneNascita);
          if("".equals(codComuneNascita)){
            codComuneNascita = (String) this.sqlManager.getObject("select tabcod5 from tabsche where tabcod='S2003' and tabcod1='09' and tabdesc like ? ", new Object[]{"%" + comuneNascita.toUpperCase() + "%"});
          }
          codComuneNascita = UtilityStringhe.convertiNullInStringaVuota(codComuneNascita);
          if(!"".equals(codComuneNascita)){
            datiBaseSoggettoCollettivo.put("codComuneNascita", codComuneNascita);

          }
        }
        datiBaseSoggettoCollettivo.put("dataNascita", dataNascita);
        datiBaseSoggettoCollettivo.put("cognome", cognome);
        datiBaseSoggettoCollettivo.put("nome", nome);
        datiBaseSoggettoCollettivo.put("genere", genere);
        if(tipoAlboProf != null){
          String codAlboProf = (String) this.sqlManager.getObject("select tab2d1 from tab2 where tab2cod= ? and tab2tip = ? ",
              new Object[]{"UBUY4",tipoAlboProf.toString()});
          codAlboProf = UtilityStringhe.convertiNullInStringaVuota(codAlboProf);
          datiBaseSoggettoCollettivo.put("codAlboProf", codAlboProf);
        }
        if(titoloOnorifico != null){
          String codTitoloOnorifico = (String) this.sqlManager.getObject("select tab2d1 from tab2 where tab2cod= ? and tab2tip = ? ",
              new Object[]{"UBUY3",titoloOnorifico.toString()});
          codTitoloOnorifico = UtilityStringhe.convertiNullInStringaVuota(codTitoloOnorifico);
          if(!"".equals(codTitoloOnorifico)){
            datiBaseSoggettoCollettivo.put("codTitoloOnorifico", codTitoloOnorifico);
          }
        }

        datiBaseSoggettoCollettivo.put("annotazioni", annotazioni);

      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella selezione dei dati della ditta in Appalti ", null,e);
    }




    return datiBaseSoggettoCollettivo;
  }

  /**
   *  Metodo che controlla la validità dell'anagrafica
   *  per quanto riguarda i dati obbligatori.
   *  Distingue per tipologia di controllo ed eventualmente
   *  per tipo di anagrafica (SC,PF,DI)
   *     *
   * @param tipo Controllo,tipo Anagrafica, dati Anagrafica (dal bd)
   *
   * @return boolean,   true  valido
   *                    false altrimenti
   *
   */

  public String[] getDatiObbligatoriAnagrafica(String tipoControllo, String tipoAnagrafica, HashMap<String, Object> datiAnagrafica)
  throws GestoreException {

    String outcome = "true";
    String msgDetail = "";
    String[] res = new String[2];

    Boolean isStraniera = false;
    String nazione = (String) datiAnagrafica.get("codNazioneSede");
    nazione = UtilityStringhe.convertiNullInStringaVuota(nazione);
    if("".equals(nazione) || "IT".equals(nazione)){
      isStraniera = false;
    }else{
      isStraniera = true;
    }

      String contoCorrenteDedicato = (String) datiAnagrafica.get("ccDedicato");
      contoCorrenteDedicato = UtilityStringhe.convertiNullInStringaVuota(contoCorrenteDedicato);
      if("".equals(contoCorrenteDedicato)){
        outcome = "false";
        if(!"".equals(msgDetail)){
          msgDetail += " - ";//separatore
        }
        msgDetail += "conto corrente dedicato";
      }

    /*
    if(!isStraniera){
      String soggettiAbilitati = (String) datiAnagrafica.get("soggettiAbilitati");
      soggettiAbilitati = UtilityStringhe.convertiNullInStringaVuota(soggettiAbilitati);
      if("".equals(soggettiAbilitati)){
        if("".equals(soggettiAbilitati)){
          outcome = "false";
          if(!"".equals(msgDetail)){
            msgDetail += " - ";//separatore
          }
          msgDetail += "soggetti abilitati";
        }
      }
    }
    */

    String ragioneSociale = (String) datiAnagrafica.get("ragioneSociale");
    ragioneSociale = UtilityStringhe.convertiNullInStringaVuota(ragioneSociale);
    if("".equals(ragioneSociale)){
      outcome = "false";
      if(!"".equals(msgDetail)){
        msgDetail += " - ";//separatore
      }
      msgDetail += "ragione sociale";
    }

    //String formaGiuridica = (String) datiAnagrafica.get("codFormaGiuridica");
    //04-2019 Attualmente la mappatura non risulta completa, pertanto controllo solo il tipo non il cod
    String formaGiuridica = (String) datiAnagrafica.get("formaGiuridica");
    formaGiuridica = UtilityStringhe.convertiNullInStringaVuota(formaGiuridica);
    if("".equals(formaGiuridica)){
      outcome = "false";
      if(!"".equals(msgDetail)){
        msgDetail += " - ";//separatore
      }
      msgDetail += "forma giuridica";
    }else{
      if("7".equals(formaGiuridica)){
        Long tipologiaImpresa = (Long) datiAnagrafica.get("tipologia");
        if(tipologiaImpresa == null){
          outcome = "false";
          if(!"".equals(msgDetail)){
            msgDetail += " - ";//separatore
          }
          msgDetail += "tipologia impresa (dettagliare se studio associato o società di professionisti)";
        }
      }
    }

    String codiceFiscale = (String) datiAnagrafica.get("codiceFiscale");
    codiceFiscale = UtilityStringhe.convertiNullInStringaVuota(codiceFiscale);
    if("".equals(codiceFiscale)){
      outcome = "false";
      if(!"".equals(msgDetail)){
        msgDetail += " - ";//separatore
      }
      msgDetail += "codice fiscale";
    }

    String partitaIva = (String) datiAnagrafica.get("partitaIva");
    partitaIva = UtilityStringhe.convertiNullInStringaVuota(partitaIva);
    if("".equals(partitaIva)){
      outcome = "false";
      if(!"".equals(msgDetail)){
        msgDetail += " - ";//separatore
      }
      msgDetail += "partita iva";
    }

    String indirizzo = (String) datiAnagrafica.get("indirizzoSede");
    indirizzo = UtilityStringhe.convertiNullInStringaVuota(indirizzo);
    if("".equals(indirizzo)){
      outcome = "false";
      if(!"".equals(msgDetail)){
        msgDetail += " - ";//separatore
      }
      msgDetail += "indirizzo";
    }

    String civico = (String) datiAnagrafica.get("civicoSede");
    civico = UtilityStringhe.convertiNullInStringaVuota(civico);
    if("".equals(civico)){
      outcome = "false";
      if(!"".equals(msgDetail)){
        msgDetail += " - ";//separatore
      }
      msgDetail += "civico";
    }

    //ATTENZIONE alla provincia come arriva
    if(!isStraniera){
      String provincia = (String) datiAnagrafica.get("provinciaSede");
      provincia = UtilityStringhe.convertiNullInStringaVuota(provincia);
      if("".equals(provincia)){
        outcome = "false";
        if(!"".equals(msgDetail)){
          msgDetail += " - ";//separatore
        }
        msgDetail += "provincia";
      }
    }

    String comune = "";
    if(!isStraniera){
      comune = (String) datiAnagrafica.get("codComuneSede");
    }else{
      comune = (String) datiAnagrafica.get("localitaPrincipale");
    }
    comune = UtilityStringhe.convertiNullInStringaVuota(comune);
    if("".equals(comune)){
      outcome = "false";
      if(!"".equals(msgDetail)){
        msgDetail += " - ";//separatore
      }
      msgDetail += "comune";
    }

    if("".equals(nazione)){
      outcome = "false";
      if(!"".equals(msgDetail)){
        msgDetail += " - ";//separatore
      }
      msgDetail += "nazione";
    }

    String emailUfficio = (String) datiAnagrafica.get("emailUfficio");
    emailUfficio = UtilityStringhe.convertiNullInStringaVuota(emailUfficio);
    if("".equals(emailUfficio)){
      outcome = "false";
      if(!"".equals(msgDetail)){
        msgDetail += " - ";//separatore
      }
      msgDetail += "email";
    }

    String pecUfficio = (String) datiAnagrafica.get("pecUfficio");
    pecUfficio = UtilityStringhe.convertiNullInStringaVuota(pecUfficio);
    if("".equals(pecUfficio)){
      outcome = "false";
      if(!"".equals(msgDetail)){
        msgDetail += " - ";//separatore
      }
      msgDetail += "pec";
    }

    res[0] = outcome;
    res[1] = msgDetail;

    return res;

  }
  
  public WSERPUgovAnagraficaType setAnagraficaUgov(HashMap<String, Object> soggettoCollettivo)
		  throws GestoreException{
	  
		WSERPUgovAnagraficaType anagrafica = new WSERPUgovAnagraficaType();
		anagrafica.setAnnotazioni((String) soggettoCollettivo.get("annotazioni"));
		anagrafica.setBic((String) soggettoCollettivo.get("bic"));
		anagrafica.setCapDomFiscale((String) soggettoCollettivo.get("capDomFiscale"));
		anagrafica.setCapSede((String) soggettoCollettivo.get("capSede"));
		anagrafica.setCcDedicato((String) soggettoCollettivo.get("ccDedicato"));
		anagrafica.setCellUfficio((String) soggettoCollettivo.get("cellUfficio"));
		anagrafica.setCivicoDomFiscale((String) soggettoCollettivo.get("civicoDomFiscale"));
		anagrafica.setCivicoSede((String) soggettoCollettivo.get("civicoSede"));
		anagrafica.setCodAlboProf((String) soggettoCollettivo.get("codAlboProf"));
		anagrafica.setCodComuneDomFiscale((String) soggettoCollettivo.get("codComuneDomFiscale"));
		anagrafica.setCodComuneNascita((String) soggettoCollettivo.get("codComuneNascita"));
		anagrafica.setCodComuneSede((String) soggettoCollettivo.get("codComuneSede"));
		anagrafica.setCodEsterno((String) soggettoCollettivo.get("codEsterno"));
		anagrafica.setCodFormaGiuridica((String) soggettoCollettivo.get("codFormaGiuridica"));
		anagrafica.setCodiceFiscale((String) soggettoCollettivo.get("codiceFiscale"));
		anagrafica.setCodNazioneDomFiscale((String) soggettoCollettivo.get("codNazioneDomFiscale"));
		anagrafica.setCodNazioneSede((String) soggettoCollettivo.get("codNazioneSede"));
		anagrafica.setCodTitoloOnorifico((String) soggettoCollettivo.get("codTitoloOnorifico"));
		anagrafica.setCognome((String) soggettoCollettivo.get("cognome"));
		if(soggettoCollettivo.get("dataIscrCCIAA")!= null) {
			Date dataIscrCCIAA = (Date) soggettoCollettivo.get("dataIscrCCIAA");
			Calendar calIscrCCIAA = Calendar.getInstance();
			calIscrCCIAA.setTime(dataIscrCCIAA);
			anagrafica.setDataIscrCCIAA(calIscrCCIAA);
		}
		if(soggettoCollettivo.get("dataIscrAlboProf")!= null) {
			Date dataIscrAlboProf = (Date) soggettoCollettivo.get("dataIscrAlboProf");
			Calendar calIscrAlboProf = Calendar.getInstance();
			calIscrAlboProf.setTime(dataIscrAlboProf);
			anagrafica.setDataIscrAlboProf(calIscrAlboProf);
		}
		if(soggettoCollettivo.get("dataNascita")!=null) {
			Date dataNascita = (Date) soggettoCollettivo.get("dataNascita");
			Calendar calDataNascita = Calendar.getInstance();
			calDataNascita.setTime(dataNascita);
			anagrafica.setDataNascita(calDataNascita);
		}
		anagrafica.setEmailUfficio((String) soggettoCollettivo.get("emailUfficio"));
		anagrafica.setFaxUfficio((String) soggettoCollettivo.get("faxUfficio"));
		anagrafica.setFormaGiuridica((String) soggettoCollettivo.get("formaGiuridica"));
		anagrafica.setIdInterno((Long) soggettoCollettivo.get("idInterno"));
		anagrafica.setIdInternoSede((Long) soggettoCollettivo.get("idInternoSede"));
		anagrafica.setIndirizzoDomFiscale((String) soggettoCollettivo.get("indirizzoDomFiscale"));
		anagrafica.setIndirizzoSede((String) soggettoCollettivo.get("indirizzoSede"));
		anagrafica.setLocalitaPrincipale((String) soggettoCollettivo.get("localitaPrincipale"));
		anagrafica.setNazione((Long) soggettoCollettivo.get("nazione"));
		anagrafica.setNome((String) soggettoCollettivo.get("nome"));
		anagrafica.setNumIscrAlboProf((String) soggettoCollettivo.get("numIscrAlboProf"));
		anagrafica.setPartitaIva((String) soggettoCollettivo.get("partitaIva"));
		anagrafica.setPecUfficio((String) soggettoCollettivo.get("pecUfficio"));
		anagrafica.setProvAlboProf((String) soggettoCollettivo.get("cprovAlboProf"));
		anagrafica.setProvinciaSede((String) soggettoCollettivo.get("provinciaSede"));
		anagrafica.setRagioneSociale((String) soggettoCollettivo.get("ragioneSociale"));
		anagrafica.setRapprLegale((String) soggettoCollettivo.get("rapprLegale"));
		anagrafica.setSoggettiAbilitati((String) soggettoCollettivo.get("soggettiAbilitati"));
		anagrafica.setTelUfficio((String) soggettoCollettivo.get("telUfficio"));
		anagrafica.setTipo((String) soggettoCollettivo.get("tipo"));
		anagrafica.setGenere((String) soggettoCollettivo.get("genere"));
		anagrafica.setTipologia((Long) soggettoCollettivo.get("tipologia"));
		anagrafica.setUrlSitoWeb((String) soggettoCollettivo.get("urlSitoWeb"));
		anagrafica.setCodEsternoCoordPag((String) soggettoCollettivo.get("codEsternoCoordPag"));
		anagrafica.setCodNazioneNascita((String) soggettoCollettivo.get("codNazioneNascita"));
	  
		return anagrafica;
	  
  }
  
  /**
   * Restituisce puntatore al servizio WSERP.
   *
   * @param username
   * @param password
   * @param servizio
   * @return
   * @throws GestoreException
   * @throws NoSuchAlgorithmException
   * @throws NoSuchPaddingException
   * @throws InvalidKeyException
   * @throws IllegalBlockSizeException
   * @throws BadPaddingException
   * @throws ServiceException
   */
  public WSERP_PortType getWSERP(String servizio) throws GestoreException {


	  WSERP_PortType wserp = null;
		try {
		  	String url = ConfigManager.getValore(PROP_WSERP_ERP_URL);
		    WSERP_ServiceLocator wserpLocator = new WSERP_ServiceLocator();
		    wserpLocator.setWSERPImplPortEndpointAddress(url);
		    Remote remote = wserpLocator.getPort(WSERP_PortType.class);
		    Stub axisPort = (Stub) remote;
		    wserp = (WSERP_PortType) axisPort;
		    
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return wserp;
	    
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
        return controlloParIva(cf,true);

    ret = UtilityFiscali.isValidCodiceFiscale(cf);

    return ret;
  }
  /**
   * Metodo che controlla la validità della partita iva
   *
   * @param piva
   * @param nazionalitaItalia
   *
   * @return boolean,   true partita IVA valida
   *                    false altrimenti
   *
   */
  private boolean controlloParIva(String piva, boolean nazionalitaItalia){
    boolean ret= true;

    if(nazionalitaItalia){
      //Partita I.V.A. italiana
      ret = UtilityFiscali.isValidPartitaIVA(piva);
    }else{
      //Partita I.V.A. o V.A.T. straniera
      if(piva == null || piva.length() ==0)
        return true;
      if(piva.length()<8)
          return false;

      piva = piva.toUpperCase();
      String validi = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
      for( int i = 0; i < 2; i++ ){
          // Se non ? tra i caratteri validi da errore
          if( validi.indexOf( piva.charAt(i) ) == -1 )
                  return false;
      }
    }
    return ret;
  }



  /**
   * Funzione per verificare il formato
   * dell'IBAN in base alla nazione
   *
   * @param stringa da controllare
   * @param nazione
   *
   * @return boolean
   *
   */

  public static boolean isIbanValido(String iban, String nazione) {

    //In questa fase per non creare conflitti fra gli algoritmi di verifica
    //lasciamo che sia il ws ad efettuare i controlli su IBAN. Il msg viene riportato direttamente
    //in Appalti
    if(true){
      return true;
    }


    iban = UtilityStringhe.convertiNullInStringaVuota(iban);
    if(!"".equals(iban)){
      int lcc = iban.length();
      // IBAN italiano
      if("IT".equals(nazione)){

        //controllo sulla lunghezza
        if((lcc != 27)){
          return false;
        }

        String naz = iban.substring(0,2);
        if(!"IT".equals(naz)){
          return false;
        }
        //controllo da perfezionare
        String ccInt = iban.substring(2,3);
        if(!StringUtils.isNumeric(ccInt)){
          return false;
        }
        String cin = iban.substring(4,5);
        if(!StringUtils.isAlpha(cin)){
          return false;
        }
        String abi = iban.substring(5,10);
        if(!StringUtils.isNumeric(abi)){
          return false;
        }

        String cab = iban.substring(10,15);
        if(!StringUtils.isNumeric(cab)){
          return false;
        }

        String numConto = iban.substring(15);
        if(!StringUtils.isNumeric(numConto)){
          return false;
        }
      }else{
      // IBAN Straniero
        //verifico che non ci siano spazi vuoti e che non ci siano caratteri tutti uguali
        if(!(lcc > 1)){
          return false;
        }else{
          int countEqualChars = 0 ;
          String eCharPrec = iban.substring(0,1);
          for (int k = 0; k < lcc; k++){
            String eChar = iban.substring(k,k+1);
            if(eChar.equals(eCharPrec)){
              countEqualChars++;
            }
            eCharPrec = eChar;
          }
          if(countEqualChars==lcc){
            return false;
          }
        }
      }

    }


    return true;
  }






  /**
   * Metodo cper il recupero delle credenziali
   *
   * @param servizio (cineca)
   *
   * @return String[] (credenziali)
   *
   */

  public String[] getWSLogin(Long syscon, String servizio){
    String [] cred = new String [2];

    List<?> datiWSLogin;
    try {
      datiWSLogin = sqlManager.getVector(
          "select username, password, ruolo, nome, cognome, codiceuo, idutente, idutenteunop from wslogin where syscon = ? and servizio = ?", new Object[] { syscon, servizio });

      if (datiWSLogin != null && datiWSLogin.size() > 0) {
        String username = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 0).getValue();
        String password = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 1).getValue();

        String passwordDecoded = null;
        if (password != null && password.trim().length() > 0) {
          ICriptazioneByte passwordICriptazioneByte = null;
          passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
              ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), password.getBytes(),
              ICriptazioneByte.FORMATO_DATO_CIFRATO);
          passwordDecoded = new String(passwordICriptazioneByte.getDatoNonCifrato());
        }

        cred[0] = username;
        cred[1] = passwordDecoded;

      }

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (CriptazioneException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return cred;
  }

}
