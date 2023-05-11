/*
 * Created on 24/apr/2017
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.integrazioni;

import it.cineca.u_gov.ac.di.ws.ApplicationException;
import it.cineca.u_gov.ac.di.ws.WSACDitteService;
import it.cineca.u_gov.ac.di.ws.WsDitta;
import it.cineca.u_gov.ac.di.ws.WsDittaBase;
import it.cineca.u_gov.ac.di.ws.WsDittaContatti;
import it.cineca.u_gov.ac.di.ws.WsDittaEdit;
import it.cineca.u_gov.ac.di.ws.WsDittaElencaCoordPagResponse;
import it.cineca.u_gov.ac.di.ws.WsDittaEstraiRequest;
import it.cineca.u_gov.ac.di.ws.WsDittaEstraiResponse;
import it.cineca.u_gov.ac.di.ws.WsDittaIndirizzo;
import it.cineca.u_gov.ac.di.ws.WsDittaInserisciCoordPagRequest;
import it.cineca.u_gov.ac.di.ws.WsDittaInserisciCoordPagResponse;
import it.cineca.u_gov.ac.di.ws.WsDittaInserisciRequest;
import it.cineca.u_gov.ac.di.ws.WsDittaInserisciResponse;
import it.cineca.u_gov.ac.di.ws.WsDittaMessaggio;
import it.cineca.u_gov.ac.di.ws.WsDittaModificaCoordPagRequest;
import it.cineca.u_gov.ac.di.ws.WsDittaModificaCoordPagResponse;
import it.cineca.u_gov.ac.di.ws.WsDittaModificaRequest;
import it.cineca.u_gov.ac.di.ws.WsDittaModificaResponse;
import it.cineca.u_gov.ac.di.ws.WsPrivateDitteLocator;
import it.cineca.u_gov.ac.pf.ws.WsdtoPersonaFisicaResponce;
import it.cineca.u_gov.ac.sc.ws.WSACSoggettoCollettivoService;
import it.cineca.u_gov.ac.sc.ws.Ws_002fPrivate_002fSoggettoCollettivo;
import it.cineca.u_gov.ac.sc.ws.WsdtoPagamento;
import it.cineca.u_gov.ac.sc.ws.WsdtoSoggettoCollettivoRequest;
import it.cineca.u_gov.ac.sc.ws.WsdtoSoggettoCollettivoResponse;
import it.cineca.u_gov.ac.sc.ws.WsdtoSoggettoCollettivoSearch;
import it.cineca.u_gov.ac.sc.ws.WsdtoSoggettoCollettivoSearchSimple;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreIMPIND;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPUgovAnagraficaResType;
import it.maggioli.eldasoft.ws.erp.WSERPUgovAnagraficaType;
import it.maggioli.eldasoft.ws.erp.WSERPUgovResType;
import it.maggioli.eldasoft.ws.erp.WSERP_PortType;
import it.maggioli.eldasoft.ws.erp.WSERP_ServiceLocator;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.rpc.ServiceException;
import javax.xml.ws.BindingProvider;

import org.apache.axis.client.Stub;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte relativa all'integrazione con Cineca
 *
 * @author Cristian.Febas
 */

public class CinecaWSManager {

  static Logger               logger                                          = Logger.getLogger(CinecaWSManager.class);

  private static final String SERVIZIO_SOGGETTOCOLLETTIVO                    = "SOGGETTOCOLLETTIVO";
  private static final String PROP_WSSOGGETTOCOLLETTIVO_URL                  = "cineca.ws.SoggettoCollettivo.url";
  private static final String PROP_WSSOGGETTOCOLLETTIVO_CLIENT               = "cineca.ws.SoggettoCollettivo.client";

  private static final String SERVIZIO_DITTEINDIVIDUALI                      = "DITTEINDIVIDUALI";
  private static final String PROP_WSDITTEINDIVIDUALI_URL                      = "cineca.ws.DitteIndividuali.url";
  private static final String PROP_WSDITTEINDIVIDUALI_CLIENT                   = "cineca.ws.SoggettoCollettivo.client";
  
  private static final String PROP_WS_COD_EXT_COORDPAG                   	  = "cineca.ws.codEsternoCoordPag";
  private static final String PROP_WSERP_ERP_URL                              = "wserp.erp.url";


  private SqlManager          sqlManager;
  private CinecaWSPersoneFisicheManager cinecaWSPersoneFisicheManager;
  private CinecaAnagraficaComuneManager cinecaAnagraficaComuneManager;

  private WsdtoPersonaFisicaResponce retRespMsg;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setCinecaWSPersoneFisicheManager(CinecaWSPersoneFisicheManager cinecaWSPersoneFisicheManager) {
    this.cinecaWSPersoneFisicheManager = cinecaWSPersoneFisicheManager;
  }

  public void setCinecaAnagraficaComuneManager(CinecaAnagraficaComuneManager cinecaAnagraficaComuneManager) {
    this.cinecaAnagraficaComuneManager = cinecaAnagraficaComuneManager;
  }

  /**
   * Restituisce puntatore al servizio WS Soggetto Collettivo.
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
   * @throws MalformedURLException
   */
  private WSACSoggettoCollettivoService getWSSoggettoCollettivo(String servizio)
    throws GestoreException, RemoteException, ServiceException, MalformedURLException {

    String url = null;
    if (SERVIZIO_SOGGETTOCOLLETTIVO.equals(servizio)) {
      url = ConfigManager.getValore(PROP_WSSOGGETTOCOLLETTIVO_URL);
      if (url == null || "".equals(url)) {
        throw new GestoreException("L'indirizzo per la connessione al servizio dei soggetti collettivi",
            "ws.soggettocollettivo.url.error");
      }
    }

    String[] credenziali = this.getWSLogin(Long.valueOf(50), "CINECA");

    String username = credenziali[0];
    String password = credenziali[1];

    URL dynUrl =new URL(url);
    Ws_002fPrivate_002fSoggettoCollettivo ws = new Ws_002fPrivate_002fSoggettoCollettivo(dynUrl);
    WSACSoggettoCollettivoService wscineca = ws.getWSACSoggettoCollettivoServicePort();
    BindingProvider provider = (BindingProvider) wscineca;

    provider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
    provider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
    provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,url);



    return wscineca;

  }


  /**
   * Estrae un soggetto collettivo
   *
   * @param codiceFiscale
   * @return WsdtoSoggettoCollettivoResponse
   * @throws GestoreException
   */
  public WsdtoSoggettoCollettivoResponse wsCinecaEstraiSoggettoCollettivo(String cf, String piva, Long nazione)
    throws GestoreException {

    WsdtoSoggettoCollettivoResponse wsdtoSoggettoCollettivoResponse = new WsdtoSoggettoCollettivoResponse();
    WsdtoSoggettoCollettivoSearch dtoRicerca = new WsdtoSoggettoCollettivoSearch();

    try {

      Calendar cal = Calendar.getInstance();
      cal.setTime(UtilityDate.getDataOdiernaAsDate());

      GregorianCalendar gc = new GregorianCalendar();
      gc.setTime(UtilityDate.getDataOdiernaAsDate());
      XMLGregorianCalendar xmlgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

      dtoRicerca.setDataRiferimento(xmlgc);

      String cfOpiva = piva;
      cfOpiva = UtilityStringhe.convertiNullInStringaVuota(cfOpiva);
      if("".equals(cfOpiva)){
        cfOpiva = cf;
        cfOpiva = UtilityStringhe.convertiNullInStringaVuota(cfOpiva);
      }

      WSACSoggettoCollettivoService wscineca = this.getWSSoggettoCollettivo(SERVIZIO_SOGGETTOCOLLETTIVO);
      String client = ConfigManager.getValore(PROP_WSSOGGETTOCOLLETTIVO_CLIENT);
      //estraggo idInterno del principale dall'elenco
       List<WsdtoSoggettoCollettivoResponse> elencoSoggettiCollettivi = null;
      if(nazione != null && !new Long(1).equals(nazione)){
        elencoSoggettiCollettivi = wscineca.elencaSoggettiCollettivi(null, null, null, null, cfOpiva, null, null, true);
        if(elencoSoggettiCollettivi.size()==0){//ATTENZIONE: sembra che arrivi un array vuoto...controllare la dim dell'array
          elencoSoggettiCollettivi = wscineca.elencaSoggettiCollettivi(null, null, cfOpiva, null, null, null, null, true);
        }
      }else{
        if(!"".equals(cfOpiva)){
          //occorre impostare almeno un criterio di ricerca
          elencoSoggettiCollettivi = wscineca.elencaSoggettiCollettivi(null, null, null, cfOpiva, null, null, null, true);
          if(elencoSoggettiCollettivi == null){
            elencoSoggettiCollettivi = wscineca.elencaSoggettiCollettivi(null, cfOpiva, null, null, null, null, null, true);
          }
        }
      }

      if(elencoSoggettiCollettivi != null && elencoSoggettiCollettivi.size() > 0){
        //una volta ...collaudato ...il pezzo seguente puo' essere tolto
        for (int k = 0; k < elencoSoggettiCollettivi.size(); k++) {
          WsdtoSoggettoCollettivoResponse soggettoCollettivo = elencoSoggettiCollettivi.get(k);
          if(soggettoCollettivo.getIdInternoSede()!= null){
            ;
          }else{
            Long idInterno = soggettoCollettivo.getIdInterno();
            dtoRicerca.setIdInterno(idInterno);
          }
        }
        dtoRicerca.setClient(client);
        wsdtoSoggettoCollettivoResponse = wscineca.estraiSoggettoCollettivo(dtoRicerca);
      }

    } catch (Throwable t) {
      String ecpt = t.getMessage();
      if(!ecpt.contains("ERR-100")){
        throw new GestoreException("Si e' verificato un errore durante l'estrazione del soggetto collettivo: " + t.getMessage(),
            "cineca.soggettoCollettivo.remote.error", new Object[] {t.getMessage()}, t);
      }

    }


    return wsdtoSoggettoCollettivoResponse;
  }


  /**
   * Inserisce un soggetto collettivo
   *
   * @param codiceFiscale
   * @return WsdtoSoggettoCollettivoResponse
   * @throws GestoreException
   */
  public Long wsCinecaInserisciSoggettoCollettivo(HttpServletRequest request, HashMap<String,Object> datiSoggettoCollettivo)
    throws GestoreException {
    WsdtoSoggettoCollettivoResponse wsdtoSoggettoCollettivoResponse = new WsdtoSoggettoCollettivoResponse();

    WsdtoSoggettoCollettivoRequest dtoInserisci = new WsdtoSoggettoCollettivoRequest();
    Long idInterno = null;

    String codEsterno = (String) datiSoggettoCollettivo.get("codEsterno");
    Long idInternoSede = (Long) datiSoggettoCollettivo.get("idInternoSede");
    Long tipologia = (Long) datiSoggettoCollettivo.get("tipologia");
    String ragioneSociale = (String) datiSoggettoCollettivo.get("ragioneSociale");
    String rapprLegale = (String) datiSoggettoCollettivo.get("rapprLegale");
    Date dataIscrCCIAA = (Date) datiSoggettoCollettivo.get("dataIscrCCIAA");
    String partitaIva = (String) datiSoggettoCollettivo.get("partitaIva");
    String codiceFiscale = (String) datiSoggettoCollettivo.get("codiceFiscale");
    String codNazioneDomFiscale = (String) datiSoggettoCollettivo.get("codNazioneDomFiscale");
    String capDomFiscale = (String) datiSoggettoCollettivo.get("capDomFiscale");
    String indirizzoDomFiscale = (String) datiSoggettoCollettivo.get("indirizzoDomFiscale");
    String civicoDomFiscale = (String) datiSoggettoCollettivo.get("civicoDomFiscale");
    String codComuneDomFiscale = (String) datiSoggettoCollettivo.get("codComuneDomFiscale");
    String codNazioneSede = (String) datiSoggettoCollettivo.get("codNazioneSede");
    String codFormaGiuridica = (String) datiSoggettoCollettivo.get("codFormaGiuridica");
    codFormaGiuridica = UtilityStringhe.convertiNullInStringaVuota(codFormaGiuridica);
    String capSede = (String) datiSoggettoCollettivo.get("capSede");
    String indirizzoSede = (String) datiSoggettoCollettivo.get("indirizzoSede");
    String civicoSede = (String) datiSoggettoCollettivo.get("civicoSede");
    String codComuneSede = (String) datiSoggettoCollettivo.get("codComuneSede");
    String faxUfficio = (String) datiSoggettoCollettivo.get("faxUfficio");
    String telUfficio = (String) datiSoggettoCollettivo.get("telUfficio");
    String cellUfficio = (String) datiSoggettoCollettivo.get("cellUfficio");
    String pecUfficio = (String) datiSoggettoCollettivo.get("pecUfficio");
    String urlSitoWeb = (String) datiSoggettoCollettivo.get("urlSitoWeb");
    String localitaPrincipale = (String) datiSoggettoCollettivo.get("localitaPrincipale");
    String ccDedicato = (String) datiSoggettoCollettivo.get("ccDedicato");
    String bic = (String) datiSoggettoCollettivo.get("bic");

    try {


      if("".equals(codFormaGiuridica)){
        codFormaGiuridica="0";
      }
      if(codNazioneSede != null){//se ci sono indirizzi
        if(!"IT".equals(codNazioneSede)){
          dtoInserisci.setCodFiscaleEstero(codiceFiscale);
          dtoInserisci.setPartitaIvaEstero(partitaIva);
          dtoInserisci.setCapStranieroSede(capSede);
          dtoInserisci.setCapStranieroDomFiscale(capDomFiscale);
          dtoInserisci.setDescrCittaStranieraSede(localitaPrincipale);
          dtoInserisci.setDescrCittaStranieraDomFiscale(localitaPrincipale);
        }else{
          dtoInserisci.setCodFiscale(codiceFiscale);
          dtoInserisci.setPartitaIva(partitaIva);
          dtoInserisci.setCapSede(capSede);
          dtoInserisci.setCapDomFiscale(capDomFiscale);
        }
        /*
        Calendar dOdierna = Calendar.getInstance();
        dOdierna.setTime(UtilityDate.getDataOdiernaAsDate());
        */

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(UtilityDate.getDataOdiernaAsDate());
        XMLGregorianCalendar dOdierna = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

        dtoInserisci.setDataInizioValidita(dOdierna);
        dtoInserisci.setDataInizioSede(dOdierna);
        dtoInserisci.setDataInizioDomFiscale(dOdierna);
      }

      dtoInserisci.setCodAnagrafico(codEsterno);
      dtoInserisci.setIdInternoSede(idInternoSede);
      dtoInserisci.setTipologia(codFormaGiuridica);
      dtoInserisci.setRagioneSociale(ragioneSociale);
      dtoInserisci.setRappresentanteLegale(rapprLegale);
      dtoInserisci.setCodNazioneDomFiscale(codNazioneDomFiscale);
      dtoInserisci.setIndirizzoDomFiscale(indirizzoDomFiscale);
      dtoInserisci.setCivicoDomFiscale(civicoDomFiscale);
      dtoInserisci.setCodComuneDomFiscale(codComuneDomFiscale);
      dtoInserisci.setCodComuneSede(codComuneSede);
      dtoInserisci.setCodNazioneSede(codNazioneSede);
      dtoInserisci.setIndirizzoSede(indirizzoSede);
      dtoInserisci.setCivicoSede(civicoSede);
      dtoInserisci.setCodComuneSede(codComuneSede);
      if(dataIscrCCIAA != null){
        /*
        Calendar dIscrCCIAA = Calendar.getInstance();
        dIscrCCIAA.setTime(dataIscrCCIAA);
        */
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dataIscrCCIAA);
        XMLGregorianCalendar dIscrCCIAA = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        dtoInserisci.setDataIscrCCIAA(dIscrCCIAA);
      }

      dtoInserisci.setFax(faxUfficio);
      dtoInserisci.setTelUfficio(telUfficio);
      dtoInserisci.setCellUfficio(cellUfficio);
      dtoInserisci.setPostaElettronicaUfficio(pecUfficio);
      dtoInserisci.setUrlSitoWeb(urlSitoWeb);

      WSACSoggettoCollettivoService wscineca = this.getWSSoggettoCollettivo(SERVIZIO_SOGGETTOCOLLETTIVO);
      String client = ConfigManager.getValore(PROP_WSSOGGETTOCOLLETTIVO_CLIENT);
      dtoInserisci.setClient(client);
      idInterno = wscineca.inserisciSoggettoCollettivo(dtoInserisci);

      ccDedicato = UtilityStringhe.convertiNullInStringaVuota(ccDedicato);
      ccDedicato = ccDedicato.replace(" ", "");
      if(!"".equals(ccDedicato)){
        //VERIFICA MINIMA CORRETTEZZA IBAN ITA/ESTERO
        Boolean isCCvalido = true;
        String msgIban = "";
        String codNazPag = "";
        if(ccDedicato.length()>=2){
          codNazPag = ccDedicato.substring(0,2);
        }

        isCCvalido = cinecaAnagraficaComuneManager.isIbanValido(ccDedicato,codNazPag);
        if(!isCCvalido){
          msgIban = "formato IBAN non corretto";
          UtilityStruts.addMessage(request, "warning",
              "warnings.cineca.mancataIntegrazioneIban.warning",
              new Object[] {msgIban});
          request.setAttribute("MSGUGOV", "WARNING");
        }

        if(isCCvalido){
          try {

            Boolean prioritaMassima = true;
            WsdtoPagamento coordPagamento = new WsdtoPagamento();

            if(!"".equals(ccDedicato)){
              String cin = "";
              String abi = "";
              String cab = "";
              String numConto = "";


            //es IT 88 A 01234 56789 012345678901



              if(ccDedicato.length() >= 27){
                cin = ccDedicato.substring(4,5);
                abi = ccDedicato.substring(5,10);
                cab = ccDedicato.substring(10,15);
                numConto = ccDedicato.substring(15);
              }
              String codMod = null;


              if(codNazPag != null){
                if(!"IT".equals(codNazPag)){
                  codMod = "BU"; //bonifici esteri con IBAN
                  if(!"".equals(bic)){
                    coordPagamento.setBic(bic);
                  }
                }else{
                  codMod = "CC"; //bonifici nazionali con IBAN
                  coordPagamento.setCin(cin);
                  coordPagamento.setAbi(abi);
                  coordPagamento.setCab(cab);
                  coordPagamento.setNumeroConto(numConto);
                }
              }
              /*
              Calendar dOdierna = Calendar.getInstance();
              dOdierna.setTime(UtilityDate.getDataOdiernaAsDate());
              */
              GregorianCalendar gcdo = new GregorianCalendar();
              gcdo.setTime(UtilityDate.getDataOdiernaAsDate());
              XMLGregorianCalendar dOdierna = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcdo);


              //Calendar dFutura = Calendar.getInstance();

              SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
              String dateInString = "02-02-2222 00:00:00";
              Date date = sdf.parse(dateInString);
              //dFutura.setTime(date);

              GregorianCalendar gcdf = new GregorianCalendar();
              gcdf.setTime(date);
              XMLGregorianCalendar dFutura = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcdf);


              coordPagamento.setIban(ccDedicato);
              coordPagamento.setCodMod(codMod);
              coordPagamento.setCodNazione(codNazPag);
              coordPagamento.setDataInizio(dOdierna);
              coordPagamento.setDataFine(dFutura);
              coordPagamento.setIntestazioneConto(ragioneSociale);
              WsdtoSoggettoCollettivoSearch dtoRicerca = new WsdtoSoggettoCollettivoSearch();
              dtoRicerca.setIdInterno(idInterno);
              wscineca.inserisciCoordPagamento(dtoRicerca , coordPagamento, client, prioritaMassima);
            }


          } catch (Throwable t) {
            UtilityStruts.addMessage(request, "warning",
                "warnings.cineca.mancataIntegrazioneCoordPag.warning",
                new Object[] {t.getMessage()});

            request.setAttribute("MSGUGOV", "WARNING");

            return new Long(-5);

          }

        }


      }




    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante l'inserimento del soggetto collettivo: " + t.getMessage(),
          "cineca.soggettoCollettivo.remote.error", new Object[] {t.getMessage()}, t);
    }




    return idInterno;
  }


  /**
   * Modifica un soggetto collettivo
   *
   * @param HashMap
   * @return WsdtoSoggettoCollettivoResponse
   * @throws GestoreException
   */
  public WsdtoSoggettoCollettivoResponse wsCinecaModificaSoggettoCollettivo(HttpServletRequest request, HashMap<String,Object> datiSoggettoCollettivo)
    throws GestoreException {
    WsdtoSoggettoCollettivoResponse wsdtoSoggettoCollettivoResponse = new WsdtoSoggettoCollettivoResponse();

    WsdtoSoggettoCollettivoRequest dtoModifica = new WsdtoSoggettoCollettivoRequest();

    Date dataIscrCCIAA = (Date) datiSoggettoCollettivo.get("dataIscrCCIAA");
    Long idInterno = (Long) datiSoggettoCollettivo.get("idInterno");
    String codEsterno = (String) datiSoggettoCollettivo.get("codEsterno");
    String partitaIva = (String) datiSoggettoCollettivo.get("partitaIva");
    String codiceFiscale = (String) datiSoggettoCollettivo.get("codiceFiscale");
    String ragioneSociale = (String) datiSoggettoCollettivo.get("ragioneSociale");
    String rapprLegale = (String) datiSoggettoCollettivo.get("rapprLegale");
    String capDomFiscale = (String) datiSoggettoCollettivo.get("capDomFiscale");
    String capSede = (String) datiSoggettoCollettivo.get("capSede");
    String codNazioneSede = (String) datiSoggettoCollettivo.get("codNazioneSede");
    String codFormaGiuridica = (String) datiSoggettoCollettivo.get("codFormaGiuridica");
    codFormaGiuridica = UtilityStringhe.convertiNullInStringaVuota(codFormaGiuridica);
    String codNazioneDomFiscale = (String) datiSoggettoCollettivo.get("codNazioneDomFiscale");
    /*
    String capStranieroDomFiscale = (String) datiSoggettoCollettivo.get("capStranieroDomFiscale");
    String capStranieroSede = (String) datiSoggettoCollettivo.get("capStranieroSede");
    */
    String indirizzoSede = (String) datiSoggettoCollettivo.get("indirizzoSede");
    indirizzoSede = UtilityStringhe.convertiNullInStringaVuota(indirizzoSede);
    String indirizzoDomFiscale = (String) datiSoggettoCollettivo.get("indirizzoDomFiscale");
    indirizzoDomFiscale = UtilityStringhe.convertiNullInStringaVuota(indirizzoDomFiscale);
    String civicoSede = (String) datiSoggettoCollettivo.get("civicoSede");
    String civicoDomFiscale = (String) datiSoggettoCollettivo.get("civicoDomFiscale");
    String codComuneDomFiscale = (String) datiSoggettoCollettivo.get("codComuneDomFiscale");
    String codComuneSede = (String) datiSoggettoCollettivo.get("codComuneSede");
    String faxUfficio = (String) datiSoggettoCollettivo.get("faxUfficio");
    String telUfficio = (String) datiSoggettoCollettivo.get("telUfficio");
    String cellUfficio = (String) datiSoggettoCollettivo.get("cellUfficio");
    String pecUfficio = (String) datiSoggettoCollettivo.get("pecUfficio");
    String urlSitoWeb = (String) datiSoggettoCollettivo.get("urlSitoWeb");
    String ccDedicato = (String) datiSoggettoCollettivo.get("ccDedicato");
    String bic = (String) datiSoggettoCollettivo.get("bic");
    String localitaPrincipale= (String) datiSoggettoCollettivo.get("localitaPrincipale");
    try {

      Calendar dgen = Calendar.getInstance();
      if(dataIscrCCIAA != null){

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dataIscrCCIAA);
        XMLGregorianCalendar xmlgc;
          xmlgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        //dgen.setTime(dataIscrCCIAA);
        dtoModifica.setDataIscrCCIAA(xmlgc);
      }
      dtoModifica.setIdInterno(idInterno);
      dtoModifica.setCodAnagrafico(codEsterno);

      if(codNazioneSede != null && !"IT".equals(codNazioneSede)){
        dtoModifica.setCodFiscaleEstero(codiceFiscale);
        dtoModifica.setPartitaIvaEstero(partitaIva);
        dtoModifica.setCapStranieroSede(capSede);
        dtoModifica.setDescrCittaStranieraSede(localitaPrincipale);
        dtoModifica.setCapStranieroDomFiscale(capDomFiscale);
        dtoModifica.setDescrCittaStranieraDomFiscale(localitaPrincipale);
      }else{
        dtoModifica.setCodFiscale(codiceFiscale);
        dtoModifica.setPartitaIva(partitaIva);
        dtoModifica.setCapSede(capSede);
        dtoModifica.setCapDomFiscale(capDomFiscale);
      }

      dtoModifica.setRagioneSociale(ragioneSociale);
      if("".equals(codFormaGiuridica)){
        codFormaGiuridica="0";
      }
      dtoModifica.setTipologia(codFormaGiuridica);
      rapprLegale = UtilityStringhe.convertiNullInStringaVuota(rapprLegale);
      dtoModifica.setRappresentanteLegale(rapprLegale);
      dtoModifica.setCodNazioneSede(codNazioneSede);
      dtoModifica.setCodNazioneDomFiscale(codNazioneDomFiscale);
      dtoModifica.setCivicoDomFiscale(civicoDomFiscale);
      dtoModifica.setCivicoSede(civicoSede);
      dtoModifica.setCodComuneDomFiscale(codComuneDomFiscale);
      dtoModifica.setCodComuneSede(codComuneSede);
      dtoModifica.setIndirizzoSede(indirizzoSede);
      dtoModifica.setIndirizzoDomFiscale(indirizzoDomFiscale);

      /*
      Calendar dOdierna = Calendar.getInstance();
      dOdierna.setTime(UtilityDate.getDataOdiernaAsDate());
      */

      GregorianCalendar gcdo = new GregorianCalendar();
      gcdo.setTime(UtilityDate.getDataOdiernaAsDate());
      XMLGregorianCalendar dOdierna = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcdo);


      //Calendar dFutura = Calendar.getInstance();

      SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
      String dateInString = "02-02-2222 00:00:00";
        Date date = null;
        try {
          date = sdf.parse(dateInString);
        } catch (ParseException e) {
          throw new GestoreException(null,e.getMessage());
        }
        //dFutura.setTime(date);
        GregorianCalendar gcdf = new GregorianCalendar();
        gcdf.setTime(date);
        XMLGregorianCalendar dFutura = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcdf);


      if(indirizzoSede != null){
        dtoModifica.setDataInizioSede(dOdierna);
        //dtoModifica.setDataInizioSedeMod(dOdierna);
        dtoModifica.setDataFineSede(dFutura);
      }
      if(indirizzoDomFiscale != null){
        dtoModifica.setDataInizioDomFiscale(dOdierna);
        //dtoModifica.setDataInizioDomFiscaleMod(dOdierna);
        dtoModifica.setDataFineDomFiscale(dFutura);
      }

      dtoModifica.setFax(faxUfficio);
      dtoModifica.setTelUfficio(telUfficio);
      dtoModifica.setCellUfficio(cellUfficio);
      dtoModifica.setPostaElettronicaUfficio(pecUfficio);
      dtoModifica.setUrlSitoWeb(urlSitoWeb);

      WSACSoggettoCollettivoService wscineca = this.getWSSoggettoCollettivo(SERVIZIO_SOGGETTOCOLLETTIVO);
      String client = ConfigManager.getValore(PROP_WSSOGGETTOCOLLETTIVO_CLIENT);
      //if("".equals(codEsterno)){
        dtoModifica.setClient(client);
      //}
      wscineca.modificaSoggettoCollettivo(dtoModifica);


      try {

        //MODIFICA E/O eventuale INSERIMENTO coordinate di pagamento
        WsdtoSoggettoCollettivoSearch dtoRicerca = new WsdtoSoggettoCollettivoSearch();
        dtoRicerca.setIdInterno(idInterno);
        List<WsdtoPagamento> wsdtoPagamenti = wscineca.elencaCoordPagamento(dtoRicerca, true);
        ccDedicato = UtilityStringhe.convertiNullInStringaVuota(ccDedicato);
        ccDedicato = ccDedicato.replace(" ", "");
        bic = UtilityStringhe.convertiNullInStringaVuota(bic);
        if(!"".equals(ccDedicato)){
          //VERIFICA MINIMA CORRETTEZZA IBAN ITA/ESTERO
          Boolean isCCvalido = true;
          String msgIban = "";
          String codNazPag = "";
          if(ccDedicato.length()>=2){
            codNazPag = ccDedicato.substring(0,2);
          }

          isCCvalido = cinecaAnagraficaComuneManager.isIbanValido(ccDedicato,codNazPag);
          if(!isCCvalido){
            msgIban = "formato IBAN non corretto";
            UtilityStruts.addMessage(request, "warning",
                "warnings.cineca.mancataIntegrazioneIban.warning",
                new Object[] {msgIban});
            request.setAttribute("MSGUGOV", "WARNING");
          }

          if(isCCvalido){
            if(wsdtoPagamenti != null){
                ccDedicato = UtilityStringhe.convertiNullInStringaVuota(ccDedicato);
                boolean founded = false;
                for (int k = 0; k < wsdtoPagamenti.size(); k++) {
                  WsdtoPagamento wsdtoPagamento = wsdtoPagamenti.get(k);
                  String coord_iban = wsdtoPagamento.getIban();
                  coord_iban = UtilityStringhe.convertiNullInStringaVuota(coord_iban);
                  String coord_codmod = wsdtoPagamento.getCodMod();
                  coord_codmod = UtilityStringhe.convertiNullInStringaVuota(coord_codmod);
                  String coord_codnaz = wsdtoPagamento.getCodNazione();
                  coord_codnaz = UtilityStringhe.convertiNullInStringaVuota(coord_codnaz);
                  Long coord_id = wsdtoPagamento.getIdCoordPag();
                  //qui andrà discriminato per Nazione
                    ccDedicato = ccDedicato.replace(" ", "");
                  //if(("CC".equals(coord_codmod) && "IT".equals(coord_codnaz)) || ("BU".equals(coord_codmod) && !"IT".equals(coord_codnaz))){
                    if(ccDedicato.equals(coord_iban)){
                      founded = true;
                      WsdtoPagamento coordPag = new WsdtoPagamento();

                      //es IT 88 A 01234 56789 012345678901
                      if("CC".equals(coord_codmod) && "IT".equals(coord_codnaz)){
                        if(ccDedicato.length() >= 27){
                          String cin = ccDedicato.substring(4,5);
                          String abi = ccDedicato.substring(5,10);
                          String cab = ccDedicato.substring(10,15);
                          String numConto = ccDedicato.substring(15);
                          coordPag.setCin(cin);
                          coordPag.setAbi(abi);
                          coordPag.setCab(cab);
                          coordPag.setNumeroConto(numConto);
                        }else{
                          UtilityStruts.addMessage(request, "warning",
                              "warnings.cineca.ibanNonCorretto.warning.warning",
                              new Object[] {});
                        }
                      }else{
                        if(!"".equals(bic)){
                          coordPag.setBic(bic);
                        }
                      }

                      String codMod = coord_codmod;
                      String codNazione = coord_codnaz;

                      coordPag.setIban(ccDedicato);
                      coordPag.setCodMod(codMod);
                      coordPag.setCodNazione(codNazione);
                      coordPag.setDataInizio(dOdierna);
                      coordPag.setDataFine(dFutura);
                      coordPag.setIntestazioneConto(ragioneSociale);
                      //vado in modifica con priorità max
                        wscineca.modificaCoordPagamento(dtoRicerca , coord_id, null, coordPag , true);
                    }//if modificato iban

                  //}//if identifico
                }//for

                if(!founded){
                  WsdtoPagamento coordPag = new WsdtoPagamento();
                  String codMod = null;
                  if(codNazPag != null){
                    if(!"IT".equals(codNazPag)){
                      codMod = "BU"; //bonifici esteri con IBAN
                      if(!"".equals(bic)){
                        coordPag.setBic(bic);
                      }
                    }else{
                      codMod = "CC"; //bonifici nazionali con IBAN
                      codNazPag = "IT"; //bonifici nazionali ITALIA
                      String cin = ccDedicato.substring(4,5);
                      String abi = ccDedicato.substring(5,10);
                      String cab = ccDedicato.substring(10,15);
                      String numConto = ccDedicato.substring(15);
                      coordPag.setCin(cin);
                      coordPag.setAbi(abi);
                      coordPag.setCab(cab);
                      coordPag.setNumeroConto(numConto);
                    }
                  }
                  coordPag.setIban(ccDedicato);
                  coordPag.setCodMod(codMod);
                  coordPag.setCodNazione(codNazPag);
                  coordPag.setIntestazioneConto(ragioneSociale);
                  coordPag.setDataInizio(dOdierna);
                  coordPag.setDataFine(dFutura);
                  wscineca.inserisciCoordPagamento(dtoRicerca , coordPag, client, true);
                }

            }else{//else coord pag
                WsdtoPagamento coordPag = new WsdtoPagamento();
                String codMod = null;
                if(codNazPag != null){
                  if(!"IT".equals(codNazPag)){
                    codMod = "BU"; //bonifici esteri con IBAN
                    if(!"".equals(bic)){
                      coordPag.setBic(bic);
                    }
                  }else{
                    codMod = "CC"; //bonifici nazionali con IBAN
                    codNazPag = "IT"; //bonifici nazionali ITALIA
                    String cin = ccDedicato.substring(4,5);
                    String abi = ccDedicato.substring(5,10);
                    String cab = ccDedicato.substring(10,15);
                    String numConto = ccDedicato.substring(15);
                    coordPag.setCin(cin);
                    coordPag.setAbi(abi);
                    coordPag.setCab(cab);
                    coordPag.setNumeroConto(numConto);
                  }
                }

                coordPag.setIban(ccDedicato);
                coordPag.setCodMod(codMod);
                coordPag.setCodNazione(codNazPag);
                coordPag.setIntestazioneConto(ragioneSociale);
                coordPag.setDataInizio(dOdierna);
                coordPag.setDataFine(dFutura);
                wscineca.inserisciCoordPagamento(dtoRicerca , coordPag, client, true);


            }//if coord pag

          }






        }else{
          //se lo ho sbiancato elimino quello con priorità max se occorre (per ora direi di no)
          /*
          if("".equals(ccDedicato) && wsdtoPagamenti != null){
            for (int k = 0; k < wsdtoPagamenti.size(); k++) {
              WsdtoPagamento wsdtoPagamento = wsdtoPagamenti.get(k);
              Long coord_id = wsdtoPagamento.getIdCoordPag();
              wscineca.eliminaCoordPagamento(dtoRicerca, coord_id, null, client);
            }
          }
          */
        }

      } catch (Throwable t) {
        UtilityStruts.addMessage(request, "warning",
            "warnings.cineca.mancataIntegrazioneCoordPag.warning",
            new Object[] {t.getMessage()});
        request.setAttribute("MSGUGOV", "WARNING");
      }




    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la modifica del soggetto collettivo: " + t.getMessage(),
          "cineca.soggettoCollettivo.remote.error",new Object[] {t.getMessage()}, t);
    }




    return wsdtoSoggettoCollettivoResponse;
  }

  /**
   * Masterizza un soggetto collettivo
   *
   * @param HashMap
   * @return WsdtoSoggettoCollettivoResponse
   * @throws GestoreException
   */
  public WsdtoSoggettoCollettivoResponse wsCinecaMasterizzaSoggettoCollettivo(Long idInterno, String codEsterno)
    throws GestoreException {
    WsdtoSoggettoCollettivoResponse wsdtoSoggettoCollettivoResponse = new WsdtoSoggettoCollettivoResponse();

    WsdtoSoggettoCollettivoSearchSimple dtoSearchSimple = new WsdtoSoggettoCollettivoSearchSimple();

    try {

      WSACSoggettoCollettivoService wscineca = this.getWSSoggettoCollettivo(SERVIZIO_SOGGETTOCOLLETTIVO);

      dtoSearchSimple.setIdInterno(idInterno);

      wscineca.masterizzaSoggettoCollettivo(dtoSearchSimple, null, null, codEsterno);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la modifica del soggetto collettivo: " + t.getMessage(),
          "cineca.soggettoCollettivo.remote.error", new Object[] {t.getMessage()}, t);
    }




    return wsdtoSoggettoCollettivoResponse;
  }


  /**
   *  Dati soggetto collettivo
   *
   * @param codimp
   * @return HashMap
   * @throws GestoreException
   */
  public List<HashMap<String, Object>> getDatiSoggettoCollettivoConFiliali(String dittaCineca)
    throws GestoreException {


    List<HashMap<String, Object>> listaSoggettiCollettivi = new ArrayList<HashMap<String, Object>>();
    HashMap<String, Object> datiBaseSoggettoCollettivo = new HashMap<String, Object>();

    String selectFornitore = "select codimp, tipimp, nomest, cfimp, pivimp, indimp, nciimp, locimp, proimp, capimp," +
    " telimp, faximp, nazimp, dcciaa, natgiui " +
    " from impr where codimp = ? ";
    String selectIndirizziAggiuntivi = "select indind, indnc, indloc, indpro, indcap, indtel, indfax, nazimp, indtip" +
    " from impind where codimp5 = ? ";
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

        //Verifica regola del codice fiscale

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
        }
        if(formaGiuridica != null){
          String codFormaGiuridica = (String) this.sqlManager.getObject("select tab2d1 from tab2 where tab2cod= ? and tab2tip =? ",
              new Object[]{"UBUY2",formaGiuridica.toString()});
          codFormaGiuridica = UtilityStringhe.convertiNullInStringaVuota(codFormaGiuridica);
          datiBaseSoggettoCollettivo.put("codFormaGiuridica", codFormaGiuridica);
        }

        datiBaseSoggettoCollettivo.put("capSede", capPrincipale);
        datiBaseSoggettoCollettivo.put("indirizzoSede", indirizzoPrincipale);
        datiBaseSoggettoCollettivo.put("civicoSede", numCivicoPrincipale);
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
          }
        }

        List<?> indirizziAggiuntivi = this.sqlManager.getListVector(selectIndirizziAggiuntivi, new Object[]{codimp});



        if(indirizziAggiuntivi.size() > 0){
          //per ogni indirizzo
          for (int i = 0; i < indirizziAggiuntivi.size(); i++) {
            String indirizzoAggiuntivo = (String) SqlManager.getValueFromVectorParam(indirizziAggiuntivi.get(i), 0).getValue();
            indirizzoAggiuntivo = UtilityStringhe.convertiNullInStringaVuota(indirizzoAggiuntivo);
            String numCivicoAggiuntivo = (String) SqlManager.getValueFromVectorParam(indirizziAggiuntivi.get(i), 1).getValue();
            String localitaAggiuntivo = (String) SqlManager.getValueFromVectorParam(indirizziAggiuntivi.get(i), 2).getValue();
            localitaAggiuntivo = UtilityStringhe.convertiNullInStringaVuota(localitaAggiuntivo);
            String provinciaAggiuntivo = (String) SqlManager.getValueFromVectorParam(indirizziAggiuntivi.get(i), 3).getValue();
            String capAggiuntivo = (String) SqlManager.getValueFromVectorParam(indirizziAggiuntivi.get(i), 4).getValue();
            capAggiuntivo = UtilityStringhe.convertiNullInStringaVuota(capAggiuntivo);
            String telefonoAggiuntivo = (String) SqlManager.getValueFromVectorParam(indirizziAggiuntivi.get(i), 5).getValue();
            String faxAggiuntivo = (String) SqlManager.getValueFromVectorParam(indirizziAggiuntivi.get(i), 6).getValue();
            Long nazioneAggiuntivo = (Long) SqlManager.getValueFromVectorParam(indirizziAggiuntivi.get(i), 7).getValue();
            String tipoIndirizzoAggiuntivo = (String) SqlManager.getValueFromVectorParam(indirizziAggiuntivi.get(i), 8).getValue();
            //controllare meglio qui con ""
            if(!"".equals(indirizzoAggiuntivo) && !"".equals(localitaAggiuntivo) && !"".equals(capAggiuntivo) && nazioneAggiuntivo != null){
              HashMap<String, Object> datiSoggettoCollettivo = new HashMap<String, Object>();
              //inizializzo i dati anagrafici
              datiSoggettoCollettivo.put("codEsterno", codimp);
              datiSoggettoCollettivo.put("tipologia", tipologia);
              datiSoggettoCollettivo.put("ragioneSociale", ragioneSociale);
              if(datiIMPLEG!= null){
                String rapprLegale = (String) SqlManager.getValueFromVectorParam(datiIMPLEG, 0).getValue();
                datiSoggettoCollettivo.put("rapprLegale", rapprLegale);
              }
              datiSoggettoCollettivo.put("dataIscrCCIAA", dataIscrCCIAA);
              datiSoggettoCollettivo.put("partitaIva", partitaIva);
              datiSoggettoCollettivo.put("codiceFiscale", codiceFiscale);
              if(nazionePrincipale != null){
                String codNazioneSede = (String) this.sqlManager.getObject("select tab2tip from tab2 where tab2cod= ? and tab2d1 =? ",
                    new Object[]{"UBUY1",nazionePrincipale.toString()});
                codNazioneSede = UtilityStringhe.convertiNullInStringaVuota(codNazioneSede);
                datiSoggettoCollettivo.put("codNazioneSede", codNazioneSede);
              }
              if(formaGiuridica != null){
                String codFormaGiuridica = (String) this.sqlManager.getObject("select tab2d1 from tab2 where tab2cod= ? and tab2tip =? ",
                    new Object[]{"UBUY2",formaGiuridica.toString()});
                codFormaGiuridica = UtilityStringhe.convertiNullInStringaVuota(codFormaGiuridica);
                datiSoggettoCollettivo.put("codFormaGiuridica", codFormaGiuridica);
              }
              datiSoggettoCollettivo.put("capSede", capPrincipale);
              datiSoggettoCollettivo.put("indirizzoSede", indirizzoPrincipale);
              datiSoggettoCollettivo.put("civicoSede", numCivicoPrincipale);
              if(localitaPrincipale != null){
                //prima la provo secca, altrimenti con il like
                String codComuneSede = (String) this.sqlManager.getObject("select tabcod5 from tabsche where tabcod='S2003' and tabcod1='09' and tabdesc = ? ", new Object[]{localitaPrincipale.toUpperCase()});
                codComuneSede = UtilityStringhe.convertiNullInStringaVuota(codComuneSede);
                if("".equals(codComuneSede)){
                  codComuneSede = (String) this.sqlManager.getObject("select tabcod5 from tabsche where tabcod='S2003' and tabcod1='09' and tabdesc like ? ", new Object[]{"%" + localitaPrincipale.toUpperCase() + "%"});
                }
                codComuneSede = UtilityStringhe.convertiNullInStringaVuota(codComuneSede);
                if(!"".equals(codComuneSede)){
                  datiSoggettoCollettivo.put("codComuneSede", codComuneSede);
                }
              }



              if(nazioneAggiuntivo != null){
                String codNazioneDomFiscale = (String) this.sqlManager.getObject("select tab2tip from tab2 where tab2cod= ? and tab2d1 =? ",
                    new Object[]{"UBUY1",nazioneAggiuntivo.toString()});
                codNazioneDomFiscale = UtilityStringhe.convertiNullInStringaVuota(codNazioneDomFiscale);
                datiSoggettoCollettivo.put("codNazioneDomFiscale", codNazioneDomFiscale);
              }

              datiSoggettoCollettivo.put("capDomFiscale", capAggiuntivo);
              datiSoggettoCollettivo.put("indirizzoDomFiscale", indirizzoAggiuntivo);
              datiSoggettoCollettivo.put("civicoDomFiscale", numCivicoAggiuntivo);
              if(localitaPrincipale != null){
                String codComuneDomFiscale = (String) this.sqlManager.getObject("select tabcod5 from tabsche where tabdesc like ? ", new Object[]{"%" + localitaAggiuntivo + "%"});
                datiSoggettoCollettivo.put("codComuneDomFiscale", codComuneDomFiscale);
              }

              listaSoggettiCollettivi.add(datiSoggettoCollettivo);

            }

          }//for indirizzi

        }else{
          listaSoggettiCollettivi.add(datiBaseSoggettoCollettivo);
        }
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella selezione dei dati della ditta aggiudicataria ", null,e);
    }




    return listaSoggettiCollettivi;
  }


  /**
   *  Dati soggetto collettivo
   *
   * @param codimp
   * @return HashMap
   * @throws GestoreException
   */
  public HashMap<String, Object> getDatiSoggettoCollettivo(String dittaCineca)
    throws GestoreException {


    HashMap<String, Object> datiBaseSoggettoCollettivo = new HashMap<String, Object>();

    String selectFornitore = "select codimp, tipimp, nomest, cfimp, pivimp, indimp, nciimp, locimp, proimp, capimp," +
    " telimp, faximp, nazimp, dcciaa, natgiui, emai2ip, telcel, indweb, coorba, emaiip, banapp," +
    " cnatec, dnatec, annoti, cognome, nome, sextec, inctec," +
    " tipalb,albtec,datalb,proalb,sogmov,codbic" +
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
        Long titoloOnorifico = (Long) SqlManager.getValueFromVectorParam(datiIMPR, 27).getValue();
        Long tipoAlboProf = (Long) SqlManager.getValueFromVectorParam(datiIMPR, 28).getValue();
        String numIscrAlboProf = (String) SqlManager.getValueFromVectorParam(datiIMPR, 29).getValue();
        Date dataIscrAlboProf = (Date) SqlManager.getValueFromVectorParam(datiIMPR, 30).getValue();
        String provAlboProf = (String) SqlManager.getValueFromVectorParam(datiIMPR, 31).getValue();
        String soggettiAbilitati = (String) SqlManager.getValueFromVectorParam(datiIMPR, 32).getValue();
        String codiceBic = (String) SqlManager.getValueFromVectorParam(datiIMPR, 33).getValue();


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
        if(provinciaPrincipale != null){
          datiBaseSoggettoCollettivo.put("provinciaSede", provinciaPrincipale);
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
          //cerco eventualmente il cod comune nascita straniero
          if("".equals(codComuneNascita)){
        	  codComuneNascita = (String) this.sqlManager.getObject("select tabcod2 from tabsche where tabcod='S2019' and tabcod1='01' and tabdesc = ? ", new Object[]{comuneNascita.toUpperCase()});
        	  codComuneNascita = UtilityStringhe.convertiNullInStringaVuota(codComuneNascita);
        	  if(!"".equals(codComuneNascita)) {
                  String codNazioneNascita = (String) this.sqlManager.getObject("select tab2tip from tab2 where tab2cod= ? and "+ this.sqlManager.getDBFunction("UPPER")+"( tab2d2 ) = ? ",
                          new Object[]{"UBUY1",comuneNascita.toUpperCase()});
                  datiBaseSoggettoCollettivo.put("codNazioneNascita", codNazioneNascita);
        	  }
          }
          
          if(!"".equals(codComuneNascita)){
            datiBaseSoggettoCollettivo.put("codComuneNascita", codComuneNascita);

          }
        }
        datiBaseSoggettoCollettivo.put("dataNascita", dataNascita);
        datiBaseSoggettoCollettivo.put("cognome", cognome);
        datiBaseSoggettoCollettivo.put("nome", nome);

        //gestione campo genere in base al CF
        genere = StringUtils.stripToEmpty(genere);
        //Verifico che sia un codice fiscale italiano 
        if("".equals(genere) && tipologia!=null && Long.valueOf(6).equals(tipologia)) {
        	//nel caso di libero prof. calcolo di genere dal cf
        	codiceFiscale = StringUtils.stripToEmpty(codiceFiscale);
        	if(!"".equals(codiceFiscale) && codiceFiscale.length()==16) {
        		boolean isCFvalido = this.controlloCF(codiceFiscale);
				if(isCFvalido) {
            		String discr = codiceFiscale.substring(9,11);
            		discr = StringUtils.stripToEmpty(discr);
            		if(StringUtils.isNumeric(discr)){
            			int discrInt = Long.valueOf(discr).intValue();
            			if(discrInt>=1 && discrInt<=31) {
            				genere="M";
            			}
            			if(discrInt>=41 && discrInt<=71) {
            				genere="F";
            			}
            		}
				}
        	}
        }
        datiBaseSoggettoCollettivo.put("genere", genere);
        
        if(tipoAlboProf != null){
          String codAlboProf = (String) this.sqlManager.getObject("select tab2d1 from tab2 where tab2cod= ? and tab2tip = ? ",
              new Object[]{"UBUY4",tipoAlboProf.toString()});
          codAlboProf = UtilityStringhe.convertiNullInStringaVuota(codAlboProf);
          datiBaseSoggettoCollettivo.put("codAlboProf", codAlboProf);
        }
        datiBaseSoggettoCollettivo.put("numIscrAlboProf", numIscrAlboProf);
        datiBaseSoggettoCollettivo.put("dataIscrAlboProf", dataIscrAlboProf);
        datiBaseSoggettoCollettivo.put("provAlboProf", provAlboProf);

        if(titoloOnorifico != null){
          String codTitoloOnorifico = (String) this.sqlManager.getObject("select tab2d1 from tab2 where tab2cod= ? and tab2tip = ? ",
              new Object[]{"UBUY3",titoloOnorifico.toString()});
          codTitoloOnorifico = UtilityStringhe.convertiNullInStringaVuota(codTitoloOnorifico);
          if(!"".equals(codTitoloOnorifico)){
            datiBaseSoggettoCollettivo.put("codTitoloOnorifico", codTitoloOnorifico);
          }
        }

        datiBaseSoggettoCollettivo.put("annotazioni", annotazioni);
        datiBaseSoggettoCollettivo.put("soggettiAbilitati", soggettiAbilitati);
        
        //gestione del codice esterno Coordinate di Pagamento
        String codEsternoCoordPag = ConfigManager.getValore(PROP_WS_COD_EXT_COORDPAG);
        codEsternoCoordPag = StringUtils.stripToEmpty(codEsternoCoordPag);
        if(!"".equals(codEsternoCoordPag)){
        	datiBaseSoggettoCollettivo.put("codEsternoCoordPag", codEsternoCoordPag);	
        }

      }


    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella selezione dei dati della ditta in Appalti ", null,e);
    }




    return datiBaseSoggettoCollettivo;
  }




  /**
   *  esistenza soggetto collettivo
   *
   * @param codimp
   * @return res
   * -1 = non esiste
   * 1 = si ma non masterizzato
   * 2 = si e masterizzato
   * @throws GestoreException
   */
  public String[] getCinecaSoggettoCollettivo(String dittaCineca)
    throws GestoreException {

    String[] res = new String[3];
    try {
      Vector<?> datiDitta = this.sqlManager.getVector("select cfimp, pivimp, nazimp from impr where codimp = ? ", new Object[]{dittaCineca});
      if(datiDitta!=null && datiDitta.size()>0){
        String codiceFiscale = (String) SqlManager.getValueFromVectorParam(datiDitta, 0).getValue();
        String partitaIva = (String) SqlManager.getValueFromVectorParam(datiDitta, 1).getValue();
        Long nazionalita = (Long) SqlManager.getValueFromVectorParam(datiDitta, 2).getValue();
        if(nazionalita == null){
          nazionalita = new Long(1);
        }
        if(new Long(1).equals(nazionalita)){
          //Controllo su CF/PI
          if((partitaIva !=null && !controlloCF(partitaIva)) || (codiceFiscale !=null && !controlloCF(codiceFiscale))){
            res[0] = "-2";
            return res;
          }
        }
          if(codiceFiscale!=null || partitaIva!=null){
			
				WSERP_PortType wserp = cinecaAnagraficaComuneManager.getWSERP("WSERP");
				
	            String[] credenziali = this.getWSLogin(new Long(50), "CINECA");
	            String username = credenziali[0];
	            String password = credenziali[1];
				
				WSERPUgovAnagraficaType anagrafica = new WSERPUgovAnagraficaType();
				anagrafica.setCodiceFiscale(codiceFiscale);
				anagrafica.setPartitaIva(partitaIva);
				anagrafica.setNazione(nazionalita);

		            WSERPUgovResType resSC = wserp.WSERPSoggettoCollettivo(username, password, "ESTRAI", anagrafica);
		            
		            if(resSC != null && resSC.isEsito()) {
		            	WSERPUgovAnagraficaResType anagraficaRes = resSC.getAnagraficaRes();
		            	if(anagraficaRes != null) {
		                    if((new Long(1).equals(nazionalita) && (anagraficaRes.getPartitaIva() != null || anagraficaRes.getCodiceFiscale() != null))
		                            || (!new Long(1).equals(nazionalita) && anagraficaRes.getPartitaIvaEstero() != null && anagraficaRes.getPartitaIvaEstero().equals(partitaIva))){
		                    	
		                        if(anagraficaRes.getCodiceAnagrafico() != null){
		                            res[0] = "2";
		                            res[1] = anagraficaRes.getCodiceAnagrafico();
		                            Long idInterno = anagraficaRes.getIdInterno();
		                            if(idInterno != null){
		                              res[2] = idInterno.toString();
		                            }
		                            return res;
		                          }else{
		                            res[0] = "1";
		                            Long idInterno = anagraficaRes.getIdInterno();
		                            res[2] = idInterno.toString();
		                          }
		                    }else{
		                        res[0] = "-1";
		                    }
		            	}else {
		            		res[0] = "-1";
		            	}
		            	
		            	
		            }else {
		            	//cf19
		            	res[0] = "-7";
		            	res[1] = resSC.getMessaggio();
		            }

        	  

          }else{
            res[0] = "-1";
          }
      }


    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella selezione della partita IVA della ditta aggiudicataria ", null,e);
    } catch (RemoteException e) {
        throw new GestoreException(
                "Errore nella selezione della partita IVA della ditta aggiudicataria ", null,e);
	}

    return res;

  }


  /**
   *  modifica in Cineca
   *
   * @param codimp
   * @return res
   * -1 = non esiste
   * 1 = si ma non masterizzato
   * 2 = si e masterizzato
   * @throws GestoreException
   */
  public String[] setCinecaSoggettoCollettivo(HttpServletRequest request, Long idInterno, String rapprLegale, DataColumnContainer containerIMPR)
    throws GestoreException {

    HashMap<String, Object> soggettoCollettivo = new HashMap<String, Object>();

    String codEsterno = null;

    try{

      soggettoCollettivo.put("idInterno", idInterno);
      soggettoCollettivo.put("rapprLegale", rapprLegale);
      String ragioneSociale = containerIMPR.getColumn("IMPR.NOMIMP").getValue().getStringValue();
      soggettoCollettivo.put("ragioneSociale", ragioneSociale);
      if (containerIMPR.getColumn("IMPR.DCCIAA").getValue() != null) {
        Date dataIscrizione = (Date) containerIMPR.getColumn("IMPR.DCCIAA").getValue().getValue();
        soggettoCollettivo.put("dataIscrizione", dataIscrizione);
      }

      codEsterno = containerIMPR.getColumn("IMPR.CODIMP").getValue().getStringValue();
      soggettoCollettivo.put("codEsterno", codEsterno);

      String partitaIva = containerIMPR.getColumn("IMPR.PIVIMP").getValue().getStringValue();
      soggettoCollettivo.put("partitaIva", partitaIva);

      String codiceFiscale = containerIMPR.getColumn("IMPR.CFIMP").getValue().getStringValue();
      soggettoCollettivo.put("codiceFiscale", codiceFiscale);

      String cap = containerIMPR.getColumn("IMPR.CAPIMP").getValue().getStringValue();
      soggettoCollettivo.put("capSede", cap);
      soggettoCollettivo.put("capDomFiscale", cap);

      Long naz = (Long) containerIMPR.getColumn("IMPR.NAZIMP").getValue().getValue();
      if(naz != null){
        String codNazioneSede = (String) this.sqlManager.getObject("select tab2tip from tab2 where tab2cod= ? and tab2d1 =? ",
            new Object[]{"UBUY1",naz.toString()});
        codNazioneSede = UtilityStringhe.convertiNullInStringaVuota(codNazioneSede);
        soggettoCollettivo.put("codNazioneSede", codNazioneSede);
        soggettoCollettivo.put("codNazioneDomFiscale", codNazioneSede);
      }

      Long tipologiaImpresa = (Long) containerIMPR.getColumn("IMPR.TIPIMP").getValue().getValue();
      soggettoCollettivo.put("tipologiaImpresa", tipologiaImpresa);

      Long formaGiuridica = (Long) containerIMPR.getColumn("IMPR.NATGIUI").getValue().getValue();
      if(formaGiuridica != null){
        soggettoCollettivo.put("formaGiuridica", formaGiuridica.toString());
        String codFormaGiuridica = (String) this.sqlManager.getObject("select tab2d1 from tab2 where tab2cod= ? and tab2tip =? ",
            new Object[]{"UBUY2",formaGiuridica.toString()});
        codFormaGiuridica = UtilityStringhe.convertiNullInStringaVuota(codFormaGiuridica);
        soggettoCollettivo.put("codFormaGiuridica", codFormaGiuridica);
      }

      String indirizzo = containerIMPR.getColumn("IMPR.INDIMP").getValue().getStringValue();
      soggettoCollettivo.put("indirizzoSede", indirizzo);
      soggettoCollettivo.put("indirizzoDomFiscale", indirizzo);

      String civico = containerIMPR.getColumn("IMPR.NCIIMP").getValue().getStringValue();
      soggettoCollettivo.put("civicoSede", civico);
      soggettoCollettivo.put("civicoDomFiscale", civico);

      String localita = containerIMPR.getColumn("IMPR.LOCIMP").getValue().getStringValue();
      if(localita != null){
        //prima la provo secca, altrimenti con il like
        String codComuneSede = (String) this.sqlManager.getObject("select tabcod5 from tabsche where tabcod='S2003' and tabcod1='09' and tabdesc = ? ", new Object[]{localita.toUpperCase()});
        codComuneSede = UtilityStringhe.convertiNullInStringaVuota(codComuneSede);
        if("".equals(codComuneSede)){
          codComuneSede = (String) this.sqlManager.getObject("select tabcod5 from tabsche where tabcod='S2003' and tabcod1='09' and tabdesc like ? ", new Object[]{"%" + localita.toUpperCase() + "%"});
        }
        codComuneSede = UtilityStringhe.convertiNullInStringaVuota(codComuneSede);
        if(!"".equals(codComuneSede)){
          soggettoCollettivo.put("codComuneSede", codComuneSede);
          soggettoCollettivo.put("codComuneDomFiscale", codComuneSede);
        }
      }

      String faxUfficio = containerIMPR.getColumn("IMPR.FAXIMP").getValue().getStringValue();
      soggettoCollettivo.put("faxUfficio", faxUfficio);

      String cellUfficio = containerIMPR.getColumn("IMPR.TELCEL").getValue().getStringValue();
      soggettoCollettivo.put("cellUfficio", cellUfficio);

      String telUfficio = containerIMPR.getColumn("IMPR.TELIMP").getValue().getStringValue();
      soggettoCollettivo.put("telUfficio", telUfficio);

      String emailUfficio = containerIMPR.getColumn("IMPR.EMAIIP").getValue().getStringValue();
      soggettoCollettivo.put("emailUfficio", emailUfficio);

      String pecUfficio = containerIMPR.getColumn("IMPR.EMAI2IP").getValue().getStringValue();
      soggettoCollettivo.put("pecUfficio", pecUfficio);

      String urlSitoWeb = containerIMPR.getColumn("IMPR.INDWEB").getValue().getStringValue();
      soggettoCollettivo.put("urlSitoWeb", urlSitoWeb);

      String ccDedicato = containerIMPR.getColumn("IMPR.COORBA").getValue().getStringValue();
      soggettoCollettivo.put("ccDedicato", ccDedicato);

      String soggettiAbilitati = containerIMPR.getColumn("IMPR.SOGMOV").getValue().getStringValue();
      soggettoCollettivo.put("soggettiAbilitati", soggettiAbilitati);

      String codiceBic = containerIMPR.getColumn("IMPR.CODBIC").getValue().getStringValue();
      soggettoCollettivo.put("codiceBic", codiceBic);

      String provinciaSede = containerIMPR.getColumn("IMPR.PROIMP").getValue().getStringValue();
      soggettoCollettivo.put("provinciaSede", provinciaSede);

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella selezione dei dati della ditta ", null,e);
    }

    Long tipoImpresa = (Long) containerIMPR.getColumn("IMPR.TIPIMP").getValue().getValue();
    String[] res = new String[2];
    String[] ctrlDOres = new String[2];
    ctrlDOres = cinecaAnagraficaComuneManager.getDatiObbligatoriAnagrafica("TRACC", null, soggettoCollettivo);
    //ctrlDOres[0]="true";
    if("true".equals(ctrlDOres[0])){
      String client = null;
      try {
      
	      WSERP_PortType wserp = cinecaAnagraficaComuneManager.getWSERP("WSERP");
	      String[] credenziali = this.getWSLogin(new Long(50), "CINECA");
	      String username = credenziali[0];
	      String password = credenziali[1];
	      WSERPUgovAnagraficaType anagrafica = cinecaAnagraficaComuneManager.setAnagraficaUgov(soggettoCollettivo);
	      if(new Long(6).equals(tipoImpresa)){
	        Long formaGiuridica = (Long) containerIMPR.getColumn("IMPR.NATGIUI").getValue().getValue();
	        if (new Long(10).equals(formaGiuridica)) {
	          WSERPUgovResType resPF = wserp.WSERPPersonaFisica(username,password, "MODIFICA", anagrafica);
	          if(!resPF.isEsito()) {
	              String msg = resPF.getMessaggio();
	              res[0] = "-7";
	              res[1] = msg;
	          }
	        }else{
	          WSERPUgovResType resDI = wserp.WSERPDittaIndividuale(username,password, "MODIFICA", anagrafica);
	          if(!resDI.isEsito()) {
	              String msg = resDI.getMessaggio();
	              res[0] = "-7";
	              res[1] = msg;
	          }
	        }
	      }else{
	          WSERPUgovResType resSC = wserp.WSERPSoggettoCollettivo(username,password, "MODIFICA", anagrafica);
	          if(!resSC.isEsito()) {
	              String msg = resSC.getMessaggio();
	              res[0] = "-7";
	              res[1] = msg;
	          }
	          
	      }
	
	      if(res[0] != null && Integer.parseInt(res[0])<0){
	        this.setNoteAvvisi(res[1], codEsterno, "INS", new Long(50), new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), false);
	      }
	  } catch (RemoteException re) {
	      throw new GestoreException("Si e' verificato un errore durante la modifica della anagrafica U-GOV: " + re.getMessage(),
	              "cineca.anagraficaGenerica.remote.error",new Object[] {re.getMessage()}, re);
	  }
      

    }else{
      res[0] = "false";
      res[1] = ctrlDOres[1];
      return res;
    }

    res[0] = "true";
    return res;


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
  private boolean controlloCF(String cf) {
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
   * Metodo cper il recupero delle credenziali
   *
   * @param servizio (cineca)
   *
   * @return String[] (credenziali)
   * @throws GestoreException
   *
   */

  public String[] getWSLogin(Long syscon, String servizio) throws GestoreException{
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
      throw new GestoreException(null,e.getMessage());
    } catch (CriptazioneException e) {
      throw new GestoreException(null,e.getMessage());
    }

    return cred;
  }

  /**
   * CINECA
   * Funzione che verifica l'eventuale esistenza della ditta in U-GOV ed eventualmente
   * effettua le operazioni di inserimento,masterizzazione e modifica
   *
   * @param dittaCineca
   * @return esito
   * @throws RemoteException 
   * @throws GestoreException
   */
  public String[] gestioneDittaCineca(HttpServletRequest request,String dittaCineca, Long tipoImpresa, Long formaGiuridica)
  {
    String[] retMsg = new String[2];
    String[] res;
   try {
	   
		WSERP_PortType wserp = cinecaAnagraficaComuneManager.getWSERP("WSERP");
		
        String[] credenziali = this.getWSLogin(new Long(50), "CINECA");
        String username = credenziali[0];
        String password = credenziali[1];

     if(new Long(6).equals(tipoImpresa)){
       if(formaGiuridica != null && new Long(10).equals(formaGiuridica)){
         //PERSONA FISICA
         String client = null;
         res = cinecaWSPersoneFisicheManager.getCinecaPersonaFisica(request,dittaCineca);
         if( res[0] != null){
           if( new Integer(res[0]) > 0 ){
             if(!"1".equals(res[0])){//Va masterizzata
               client = ConfigManager.getValore(PROP_WSDITTEINDIVIDUALI_CLIENT);
             }
             //modifico (non esiste la masterizzazione -il cod Anagrafico risulta obbligatorio in inserimento)
             HashMap<String, Object> datiPersonaFisica = this.getDatiSoggettoCollettivo(dittaCineca);
             String idInternoStr = res[2];
             Long idInterno = new Long(idInternoStr);
             datiPersonaFisica.put("idInterno", idInterno);
             String[] ctrlDOres = null;
             ctrlDOres = cinecaAnagraficaComuneManager.getDatiObbligatoriAnagrafica("TRACC", null, datiPersonaFisica);
             if("false".equals(ctrlDOres[0])){
               retMsg[0] = "-19";
               retMsg[1] = ctrlDOres[1];
               return retMsg;
             }
             
             WSERPUgovAnagraficaType anagrafica = cinecaAnagraficaComuneManager.setAnagraficaUgov(datiPersonaFisica);
             WSERPUgovResType resPF = wserp.WSERPPersonaFisica(username,password, "MODIFICA", anagrafica);
	         if(!resPF.isEsito()) {
	        	 retMsg[0] = "-7";
	             retMsg[1] = resPF.getMessaggio();
	             return retMsg;
	         }else {
	        	 if(Long.valueOf(-5).equals(resPF.getStato())) {
	        		 UtilityStruts.addMessage(request, "warning",
				                "warnings.cineca.mancataIntegrazioneCoordPag.warning",
				                new Object[] {resPF.getMessaggio()});
	            	request.setAttribute("MSGUGOV", "WARNING");
	            	retMsg[0] = "-5";
	            	retMsg[1] = resPF.getMessaggio();
            	}else {
	                retMsg[0] = "0";
            	}
                return retMsg;
            }
           }else{
             if("-1".equals(res[0])){
               //inserisco
               HashMap<String, Object> datiPersonaFisica = this.getDatiSoggettoCollettivo(dittaCineca);
               String[] ctrlDOres = null;
               ctrlDOres = cinecaAnagraficaComuneManager.getDatiObbligatoriAnagrafica("TRACC", null, datiPersonaFisica);
               if("false".equals(ctrlDOres[0])){
                 retMsg[0] = "-19";
                 retMsg[1] = ctrlDOres[1];
                 return retMsg;
               }
               	WSERPUgovAnagraficaType anagrafica = cinecaAnagraficaComuneManager.setAnagraficaUgov(datiPersonaFisica);
	            WSERPUgovResType resPF = wserp.WSERPPersonaFisica(username,password, "INSERISCI", anagrafica);
	            if(!resPF.isEsito()) {
	        		retMsg[0] = "-7";
	                retMsg[1] = resPF.getMessaggio();
	                return retMsg;
	            }else {
	            	if(Long.valueOf(-5).equals(resPF.getStato())) {
	             		 UtilityStruts.addMessage(request, "warning",
				                  "warnings.cineca.mancataIntegrazioneCoordPag.warning",
				                  new Object[] {resPF.getMessaggio()});
	            		 request.setAttribute("MSGUGOV", "WARNING");
	            		 retMsg[0] = "-5";
	            		 retMsg[1] = resPF.getMessaggio();
	            	}else {
	            		WSERPUgovAnagraficaResType anagraficaRes = resPF.getAnagraficaRes();
	            		Long idInterno = anagraficaRes.getIdInterno();
	            		if(idInterno!=null) {
	            			retMsg[0] = idInterno.toString();
	            		}		                
	            	}
	            	
	                return retMsg;
	            }
             }else{
               retMsg[0] = res[0];
               retMsg[1] = res[1];
               return retMsg;
             }
           }
         }
       }else{
         //DITTA INDIVIDUALE
         String client = null;
         res = this.getCinecaDittaIndividuale(dittaCineca);
         if( new Integer(res[0]) > 0 ){
           if(!"1".equals(res[0])){//Va masterizzata
             client = ConfigManager.getValore(PROP_WSDITTEINDIVIDUALI_CLIENT);
           }
           //modifico (non esiste la masterizzazione -il cod Anagrafico risulta obbligatorio in inserimento)
           HashMap<String, Object> dittaIndividuale = this.getDatiSoggettoCollettivo(dittaCineca);
           String idInternoStr = res[2];
           Long idInterno = new Long(idInternoStr);
           dittaIndividuale.put("idInterno", idInterno);
           String[] ctrlDOres = null;
           ctrlDOres = cinecaAnagraficaComuneManager.getDatiObbligatoriAnagrafica("TRACC", null, dittaIndividuale);
           if("false".equals(ctrlDOres[0])){
             retMsg[0] = "-19";
             retMsg[1] = ctrlDOres[1];
             return retMsg;
           }
           	 WSERPUgovAnagraficaType anagrafica = cinecaAnagraficaComuneManager.setAnagraficaUgov(dittaIndividuale);
           	 WSERPUgovResType resDI = wserp.WSERPDittaIndividuale(username,password, "MODIFICA", anagrafica);
	         if(!resDI.isEsito()) {
	        	 retMsg[0] = "-7";
	             retMsg[1] = resDI.getMessaggio();
	             return retMsg;
	         }else {
	        	 if(Long.valueOf(-5).equals(resDI.getStato())) {
	        		 UtilityStruts.addMessage(request, "warning",
				                "warnings.cineca.mancataIntegrazioneCoordPag.warning",
				                new Object[] {resDI.getMessaggio()});
	            	request.setAttribute("MSGUGOV", "WARNING");
	            	retMsg[0] = "-5";
	            	retMsg[1] = resDI.getMessaggio();
	        	 }else {
	                retMsg[0] = "0";
	        	 }
	          return retMsg;
	         }

         }else{

           if("-1".equals(res[0])){
             //inserisco
             HashMap<String, Object> dittaIndividuale = this.getDatiSoggettoCollettivo(dittaCineca);
             String[] ctrlDOres = null;
             ctrlDOres = cinecaAnagraficaComuneManager.getDatiObbligatoriAnagrafica("TRACC", null, dittaIndividuale);
             if("false".equals(ctrlDOres[0])){
               retMsg[0] = "-19";
               retMsg[1] = ctrlDOres[1];
               return retMsg;
             }
            	WSERPUgovAnagraficaType anagrafica = cinecaAnagraficaComuneManager.setAnagraficaUgov(dittaIndividuale);
	            WSERPUgovResType resDI = wserp.WSERPDittaIndividuale(username,password, "INSERISCI", anagrafica);
	            if(!resDI.isEsito()) {
	        		retMsg[0] = "-7";
	                retMsg[1] = resDI.getMessaggio();
	                return retMsg;
	            }else {
	            	if(Long.valueOf(-5).equals(resDI.getStato())) {
	             		 UtilityStruts.addMessage(request, "warning",
				                  "warnings.cineca.mancataIntegrazioneCoordPag.warning",
				                  new Object[] {resDI.getMessaggio()});
	            		 request.setAttribute("MSGUGOV", "WARNING");
	            		 retMsg[0] = "-5";
	            		 retMsg[1] = resDI.getMessaggio();
	            	}else {
	            		WSERPUgovAnagraficaResType anagraficaRes = resDI.getAnagraficaRes();
	            		Long idInterno = anagraficaRes.getIdInterno();
	            		if(idInterno!=null) {
	            			retMsg[0] = idInterno.toString();
	            		}		                
	            	}
	            	
	                return retMsg;
	            }

           }else{
             retMsg[0] = res[0];
             retMsg[1] = res[1];
             return retMsg;
           }
         }
       }
     }else{
         //SOGGETTO COLLETTIVO
         res = this.getCinecaSoggettoCollettivo(dittaCineca);
         if( new Integer(res[0]) > 0 ){


           if("1".equals(res[0])){
             //masterizzo
                 String idInternoStr = res[2];
                 Long idInterno = new Long(idInternoStr);
                 //potrei anche farlo piu' diretto...
                 HashMap<String, Object> soggettoCollettivo = this.getDatiSoggettoCollettivo(dittaCineca);
                 String[] ctrlDOres = null;
                 ctrlDOres = cinecaAnagraficaComuneManager.getDatiObbligatoriAnagrafica("TRACC", null, soggettoCollettivo);
                 if("false".equals(ctrlDOres[0])){
                   retMsg[0] = "-19";
                   retMsg[1] = ctrlDOres[1];
                   return retMsg;
                 }
                 String codEsterno = (String) soggettoCollettivo.get("codEsterno");
                 
                 WSERPUgovAnagraficaType anagrafica = new WSERPUgovAnagraficaType();
                 anagrafica.setIdInterno(idInterno);
                 anagrafica.setCodEsterno(codEsterno);
                 WSERPUgovResType resSC = wserp.WSERPSoggettoCollettivo(username, password, "MASTERIZZA", anagrafica);
                 if(!resSC.isEsito()) {
                     retMsg[0] = "-7";
                     retMsg[1] = resSC.getMessaggio();
                     return retMsg;
                 }
           }

           //e modifico
             HashMap<String, Object> soggettoCollettivo = this.getDatiSoggettoCollettivo(dittaCineca);
             String[] ctrlDOres = null;
             ctrlDOres = cinecaAnagraficaComuneManager.getDatiObbligatoriAnagrafica("TRACC", null, soggettoCollettivo);
             if("false".equals(ctrlDOres[0])){
               retMsg[0] = "-19";
               retMsg[1] = ctrlDOres[1];
               return retMsg;
             }
             String idInternoStr = res[2];
             Long idInterno = new Long(idInternoStr);
             soggettoCollettivo.put("idInterno", idInterno);
             
             WSERPUgovAnagraficaType anagrafica = cinecaAnagraficaComuneManager.setAnagraficaUgov(soggettoCollettivo);
             WSERPUgovResType resSC = wserp.WSERPSoggettoCollettivo(username,password, "MODIFICA", anagrafica);
             if(!resSC.isEsito()) {
                 String msg = resSC.getMessaggio();
                 retMsg[0] = "-7";
                 retMsg[1] = msg;
                 return retMsg;
             }else {
	        	 if(Long.valueOf(-5).equals(resSC.getStato())) {
	            	retMsg[0] = "-5";
	            	retMsg[1] = resSC.getMessaggio();
	        	 }else {
	                retMsg[0] = "0";
	        	 }
	        	 return retMsg;
             }
             
         }else{

           if("-1".equals(res[0])){
             //inserisco
             HashMap<String, Object> soggettoCollettivo = this.getDatiSoggettoCollettivo(dittaCineca);
             String[] ctrlDOres = null;
             ctrlDOres = cinecaAnagraficaComuneManager.getDatiObbligatoriAnagrafica("TRACC", null, soggettoCollettivo);
             if("false".equals(ctrlDOres[0])){
               retMsg[0] = "-19";
               retMsg[1] = ctrlDOres[1];
               return retMsg;
             }
				WSERPUgovAnagraficaType anagrafica = cinecaAnagraficaComuneManager.setAnagraficaUgov(soggettoCollettivo);
	            WSERPUgovResType resSC = wserp.WSERPSoggettoCollettivo(username,password, "INSERISCI", anagrafica);
	            if(!resSC.isEsito()) {
	        		retMsg[0] = "-7";
	                retMsg[1] = resSC.getMessaggio();
	                return retMsg;
	            }else {
	            	if(Long.valueOf(-5).equals(resSC.getStato())) {
	            		 retMsg[0] = "-5";
	            		 retMsg[1] = resSC.getMessaggio();
	            	}else {
		                retMsg[0] = "0";
	            	}
	            	
	                return retMsg;
	            }
           }else{
             retMsg[0] = res[0];
             retMsg[1] = res[1];
             return retMsg;
           }

         }

     }

   } catch (GestoreException e) {
     String msg = e.getMessage();
     retMsg[0] = "-7";
     retMsg[1] = msg;
     return retMsg;
   
	} catch (RemoteException e) {
	     String msg = e.getMessage();
	     retMsg[0] = "-7";
	     retMsg[1] = msg;
	     return retMsg;
	}
   
     retMsg[0] = "0";
     return retMsg;
  }


  public void gestisciIMPIND(HttpServletRequest request,
      ServletContext servletContext, TransactionStatus status, DataColumnContainer impl, HashMap<String, Object> soggettoCollettivoPrincipale,Boolean modificaPrincipale)
      throws GestoreException {

    // creo un gestore per l'entità IMPIND in modo da fare gestire
    // automaticamente eliminazione e inserimento
    AbstractGestoreEntita gestoreIMPIND = new GestoreIMPIND();
    gestoreIMPIND.setRequest(request);

    String nomeCampoNumeroRecord = "NUMERO_AIN";
    String nomeCampoDelete = "DEL_AIN";
    String nomeCampoMod = "MOD_AIN";

    // gestione salvataggio ulteriori indirizzi
    Long numeroIndirizzi = impl.getLong(nomeCampoNumeroRecord);

    Boolean modificaFiliali = false;
    List<HashMap<String, Object>> listaIndirizziDomFiscale = new ArrayList<HashMap<String, Object>>();


    for (int i = 1; i <= numeroIndirizzi.intValue(); i++) {
      // carico l'array dei dati dell'indirizzo in questione
      DataColumn[] campiIndirizzi = impl.getColumnsBySuffix("_" + i, false);
      // creo un impl con i dati della riga
      DataColumnContainer newImpl = new DataColumnContainer(campiIndirizzi);

      HashMap<String, Object> indirizzoDomFiscale = new HashMap<String, Object>();
      indirizzoDomFiscale.put("cap", newImpl.getString("IMPIND.INDCAP"));

      boolean deleteOccorrenza = newImpl.isColumn(nomeCampoDelete)
        && "1".equals(newImpl.getString(nomeCampoDelete));


      boolean updateOccorrenza = newImpl.isColumn(nomeCampoMod)
        && "1".equals(newImpl.getString(nomeCampoMod));


      if(deleteOccorrenza || updateOccorrenza){
        modificaFiliali = true;
      }

      //Lista indirizzi da ciclare....................!

      listaIndirizziDomFiscale.add(indirizzoDomFiscale);
      //...
    }//for

    if(Boolean.TRUE.equals(modificaFiliali )){
      //aggiorno il principale con il dom fiscale
      ;
      //cancello e inserisco tutto
      ;
    }else{
      if(Boolean.TRUE.equals(modificaPrincipale)){

        this.wsCinecaModificaSoggettoCollettivo(request,soggettoCollettivoPrincipale);
      }
    }

  }


  /**
   * Viene inserita un'occorrenza in G_NOTEAVVISI
   *
   * @param document
   *        messaggio da inserire nella nota, può essere un'istanza di
   *        RichiestaVariazioneDocument(fornito dal Portale) oppure una stringa
   * @param codiceImpresa
   * @param modo
   * @param profilo
   * @param dataNota
   *
   * @throws GestoreException
   */
  public void setNoteAvvisi(Object document, String codiceImpresa,
      String modo, Long syscon, Date dataNota, boolean impostaStatoAperta)
      throws GestoreException {

    String errWS = "";
    Long statoNota = null;
    String titoloNota = null;
    if (document instanceof String) {
      errWS = (String) document;
    }


    String testoRichiesta= "Mancata integrazione con U-GOV: i dati risultano disallineati.\r\n" +
    		"Occorre forzare l'allineamento dal profilo dedicato:\r\n" + errWS;

      statoNota = new Long(1);
      titoloNota = "Richiesta variazione dati anagrafici dell'impresa in U-GOV";

    try {

      Long notecod = null;

      // Aggiornamento dati Impresa
      Vector<DataColumn> elencoCampiNOTEAVVISI = new Vector<DataColumn>();

      if (notecod == null) {
        notecod = (Long) this.sqlManager.getObject(
            "select max(notecod) from g_noteavvisi", null);
        if (notecod == null) {
          notecod = new Long(0);
        }
        notecod = new Long(notecod.longValue() + 1);
      }
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.NOTECOD",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, notecod)));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.NOTEPRG",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.NOTEENT",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, "IMPR")));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.NOTEKEY1",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceImpresa)));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.AUTORENOTA",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, syscon)));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.STATONOTA",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, statoNota)));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.TIPONOTA",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(3))));
        elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.DATANOTA",
            new JdbcParametro(JdbcParametro.TIPO_DATA, dataNota)));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.TITOLONOTA",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, titoloNota)));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.TESTONOTA",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, testoRichiesta)));

      DataColumnContainer containerNOTEAVVISI = new DataColumnContainer(
          elencoCampiNOTEAVVISI);

      containerNOTEAVVISI.getColumn("G_NOTEAVVISI.NOTECOD").setChiave(true);
      containerNOTEAVVISI.getColumn("G_NOTEAVVISI.NOTECOD").setObjectOriginalValue(
          notecod);

      if ("UPDATE".equals(modo)) {
        containerNOTEAVVISI.update("...Non previsto per il momento.....",
            sqlManager);
      } else {
        containerNOTEAVVISI.getColumn("G_NOTEAVVISI.NOTECOD").setObjectOriginalValue(
            "0");
        containerNOTEAVVISI.getColumn("G_NOTEAVVISI.NOTECOD").setChiave(true);
        containerNOTEAVVISI.insert("G_NOTEAVVISI", sqlManager);
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'acquisizione della variazione dati anagrafici dell'impresa",
          null, e);
    }

  }

/*
 *          DITTE INDIVIDUALI
 */
  /**
   * Restituisce puntatore al servizio WS Ditte Individuali.
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
   * @throws MalformedURLException
   */
  private WSACDitteService getWSDitteIndividuali(String servizio)
    throws GestoreException, ApplicationException, RemoteException, ServiceException, MalformedURLException {

    String url = null;
    if (SERVIZIO_DITTEINDIVIDUALI.equals(servizio)) {
      url = ConfigManager.getValore(PROP_WSDITTEINDIVIDUALI_URL);
      if (url == null || "".equals(url)) {
        throw new GestoreException("L'indirizzo per la connessione al servizio delle ditte individuali",
            "ws.ditteindividuali.url.error");
      }
    }

    String[] credenziali = this.getWSLogin(new Long(50), "CINECA"); //sarà lo stesso?

    String username = credenziali[0];
    String password = credenziali[1];

    WsPrivateDitteLocator ws_di_locator = new WsPrivateDitteLocator();
    ws_di_locator.setWSACDitteServicePortEndpointAddress(url);

    URL dynUrl =new URL(url);
    Remote remote = ws_di_locator.getWSACDitteServicePort(dynUrl);
    Stub axisPort = (Stub) remote;
    axisPort.setUsername(username);
    axisPort.setPassword(password);

    WSACDitteService ws_DitteIndividuali = (WSACDitteService) axisPort;


    return ws_DitteIndividuali;

  }

  /**
   *  esistenza soggetto collettivo
   *
   * @param codimp
   * @return res
   * -1 = non esiste
   * 1 = si ma non masterizzato
   * 2 = si e masterizzato
   * @throws GestoreException
   */
  public String[] getCinecaDittaIndividuale(String dittaCineca)
    throws GestoreException {

    String[] res = new String[3];
    try {
      Vector<?> datiDitta = this.sqlManager.getVector("select cfimp, pivimp, nazimp from impr where codimp = ? ", new Object[]{dittaCineca});
      if(datiDitta!=null && datiDitta.size()>0){
        String codiceFiscale = (String) SqlManager.getValueFromVectorParam(datiDitta, 0).getValue();
        String partitaIva = (String) SqlManager.getValueFromVectorParam(datiDitta, 1).getValue();
        Long nazionalita = (Long) SqlManager.getValueFromVectorParam(datiDitta, 2).getValue();
        if(nazionalita == null){
          nazionalita = new Long(1);
        }
        if(new Long(1).equals(nazionalita)){
          //Controllo su CF/PI
          if((partitaIva !=null && !controlloCF(partitaIva)) || (codiceFiscale !=null && !controlloCF(codiceFiscale))){
            res[0] = "-2";
            return res;
          }
        }

        if(codiceFiscale!=null || partitaIva!=null){
        	
			WSERP_PortType wserp = cinecaAnagraficaComuneManager.getWSERP("WSERP");
			
            String[] credenziali = this.getWSLogin(new Long(50), "CINECA");
            String username = credenziali[0];
            String password = credenziali[1];
			
			WSERPUgovAnagraficaType anagrafica = new WSERPUgovAnagraficaType();
			anagrafica.setCodiceFiscale(codiceFiscale);
			anagrafica.setCodiceFiscale(codiceFiscale);
			anagrafica.setPartitaIva(partitaIva);
			anagrafica.setNazione(nazionalita);

	       WSERPUgovResType resDI = wserp.WSERPDittaIndividuale(username, password, "ESTRAI", anagrafica);
	            
	       if(resDI != null && resDI.isEsito()) {
	    	   WSERPUgovAnagraficaResType anagraficaRes = resDI.getAnagraficaRes();
	    	   
	    		   //anagraficaRes.get
    	          //WsDittaEstraiResponse dittaIndividualeResp = this.wsCinecaEstraiDittaIndividuale(codiceFiscale,partitaIva,nazionalita);
    	          //if(dittaIndividualeResp != null && dittaIndividualeResp.getDitta() != null ){
	    	   	  if(anagraficaRes != null && anagraficaRes.getIdInterno()!=null) {
    	            //WsDitta dittaIndividuale = dittaIndividualeResp.getDitta();
    	            if((new Long(1).equals(nazionalita) && (anagraficaRes.getPartitaIva() != null || anagraficaRes.getCodiceFiscale() != null))
    	                || (!new Long(1).equals(nazionalita) && anagraficaRes.getPartitaIvaEstero() != null && anagraficaRes.getPartitaIvaEstero().equals(partitaIva))){
    	              if(anagraficaRes.getCodiceAnagrafico() != null){
    	                res[0] = "2";
    	                res[1] = anagraficaRes.getCodiceAnagrafico();
    	                Long idInterno = anagraficaRes.getIdInterno();
    	                if(idInterno != null){
    	                  res[2] = idInterno.toString();
    	                }
    	                return res;
    	              }else{
    	                res[0] = "1";
    	                Long idInterno = anagraficaRes.getIdInterno();
    	                res[2] = idInterno.toString();
    	              }
    	            }else{
    	              res[0] = "-1";
    	            }
    	          }else{
    	            res[0] = "-1";
    	          }
	       }else {
	           	//cf19
	           	res[0] = "-7";
	           	res[1] = resDI.getMessaggio();
	       }
        }else{
          res[0] = "-1";
        }

      }


    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella selezione della partita IVA della ditta individuale ", null,e);
    } catch (RemoteException e) {
        throw new GestoreException(
                "Errore nella selezione della partita IVA della ditta aggiudicataria ", null,e);
	}

    return res;

  }

  /**
   * Estrae una ditta individuale
   *
   * @param codiceFiscale
   * @return WsDittaEstraiResponseResponse
   * @throws GestoreException
   */
  public WsDittaEstraiResponse wsCinecaEstraiDittaIndividuale(String cf, String piva, Long nazione)
    throws GestoreException {

    WsDittaEstraiRequest  wsDittaEstraiRequest = new WsDittaEstraiRequest();
    WsDittaEstraiResponse wsDittaEstraiResponse = new WsDittaEstraiResponse();

    Calendar cal = Calendar.getInstance();
    cal.setTime(UtilityDate.getDataOdiernaAsDate());
    wsDittaEstraiRequest.setDataRiferimento(cal);

    String cfOpiva = piva;
    cfOpiva = UtilityStringhe.convertiNullInStringaVuota(cfOpiva);
    if("".equals(cfOpiva)){
      cfOpiva = cf;
      cfOpiva = UtilityStringhe.convertiNullInStringaVuota(cfOpiva);
    }

    try {

      WSACDitteService ws_di = this.getWSDitteIndividuali(SERVIZIO_DITTEINDIVIDUALI);
      String client = ConfigManager.getValore(PROP_WSDITTEINDIVIDUALI_CLIENT);
      //Verificare se risulta neecessaria l'estrazione di idInterno dall'elenco come per i soggetti collettivi
      WsDittaBase wsDittaBase = new WsDittaBase();
      if(nazione != null && !new Long(1).equals(nazione)){
        wsDittaBase.setPartitaIVAEstera(piva);
        wsDittaEstraiRequest.setRicerca(wsDittaBase);
        ws_di.estraiDittaIndividuale(wsDittaEstraiRequest);
      }else{
        if(!"".equals(cfOpiva)){
          wsDittaBase.setPartitaIVA(piva);
          wsDittaEstraiRequest.setRicerca(wsDittaBase);
          wsDittaEstraiResponse = ws_di.estraiDittaIndividuale(wsDittaEstraiRequest);
          if(!(wsDittaEstraiResponse!= null && wsDittaEstraiResponse.getDitta()!= null)){
            wsDittaBase.setCodFiscale(cf);
            wsDittaEstraiRequest.setRicerca(wsDittaBase);
            wsDittaEstraiResponse = ws_di.estraiDittaIndividuale(wsDittaEstraiRequest);
          }
        }
      }

    } catch (Throwable t) {
      String ecpt = t.getMessage();
      if(!ecpt.contains("ERR-100")){
        throw new GestoreException("Si e' verificato un errore durante l'estrazione della ditta individuale: " + t.getMessage(),
            "cineca.dittaIndividuale.remote.error", new Object[] {t.getMessage()}, t);
      }

    }


    return wsDittaEstraiResponse;
  }

  /**
   * Inserisce una ditta individuale
   *
   * @param
   * @return WsdtoDittaIndividualeResponse
   * @throws GestoreException
   */
  public String[] wsCinecaInserisciDittaIndividuale(HttpServletRequest request, HashMap<String,Object> datiDittaIndividuale)
    throws GestoreException {

    WsDittaInserisciRequest wsDittaInserisciRequest = new WsDittaInserisciRequest();
    String[] resMsg = new String[2];
    Long idInterno = null;

    WsDittaEdit wsDittaEdit = new WsDittaEdit();

    String codEsterno = (String) datiDittaIndividuale.get("codEsterno");
    Long tipologia = (Long) datiDittaIndividuale.get("tipologia");
    String ragioneSociale = (String) datiDittaIndividuale.get("ragioneSociale");
    String rapprLegale = (String) datiDittaIndividuale.get("rapprLegale");
    String partitaIva = (String) datiDittaIndividuale.get("partitaIva");
    String codiceFiscale = (String) datiDittaIndividuale.get("codiceFiscale");
    String codNazioneDomFiscale = (String) datiDittaIndividuale.get("codNazioneDomFiscale");
    String capDomFiscale = (String) datiDittaIndividuale.get("capDomFiscale");
    String indirizzoDomFiscale = (String) datiDittaIndividuale.get("indirizzoDomFiscale");
    String civicoDomFiscale = (String) datiDittaIndividuale.get("civicoDomFiscale");
    String codComuneDomFiscale = (String) datiDittaIndividuale.get("codComuneDomFiscale");
    String codNazioneSede = (String) datiDittaIndividuale.get("codNazioneSede");
    String codFormaGiuridica = (String) datiDittaIndividuale.get("codFormaGiuridica");
    codFormaGiuridica = UtilityStringhe.convertiNullInStringaVuota(codFormaGiuridica);
    String capSede = (String) datiDittaIndividuale.get("capSede");
    String indirizzoSede = (String) datiDittaIndividuale.get("indirizzoSede");
    String civicoSede = (String) datiDittaIndividuale.get("civicoSede");
    String codComuneSede = (String) datiDittaIndividuale.get("codComuneSede");
    String faxUfficio = (String) datiDittaIndividuale.get("faxUfficio");
    String telUfficio = (String) datiDittaIndividuale.get("telUfficio");
    String cellUfficio = (String) datiDittaIndividuale.get("cellUfficio");
    String emailUfficio = (String) datiDittaIndividuale.get("emailUfficio");
    String pecUfficio = (String) datiDittaIndividuale.get("pecUfficio");
    String urlSitoWeb = (String) datiDittaIndividuale.get("urlSitoWeb");
    String localitaPrincipale = (String) datiDittaIndividuale.get("localitaPrincipale");
    String ccDedicato = (String) datiDittaIndividuale.get("ccDedicato");
    String bic = (String) datiDittaIndividuale.get("bic");
    String codComuneNascita = (String) datiDittaIndividuale.get("codComuneNascita");
    Date dataNascita = (Date) datiDittaIndividuale.get("dataNascita");
    String cognome = (String) datiDittaIndividuale.get("cognome");
    String nome = (String) datiDittaIndividuale.get("nome");
    String genere = (String) datiDittaIndividuale.get("genere");


    if("".equals(codFormaGiuridica)){
      codFormaGiuridica="0";
    }
    if(codNazioneSede != null){
      if(!"IT".equals(codNazioneSede)){
        wsDittaEdit.setCodFiscaleEstero(codiceFiscale);
        wsDittaEdit.setPartitaIVAEstera(partitaIva);
        wsDittaEdit.setCittaStranieraNascita(localitaPrincipale);
      }else{
        wsDittaEdit.setCodFiscale(codiceFiscale);
        wsDittaEdit.setPartitaIVA(partitaIva);
      }
      Calendar dOdierna = Calendar.getInstance();
      dOdierna.setTime(UtilityDate.getDataOdiernaAsDate());
    }

    wsDittaEdit.setCodAnagrafico(codEsterno);
    wsDittaEdit.setRagioneSociale(ragioneSociale);
    wsDittaEdit.setCodNazioneNascita(codNazioneDomFiscale);
    wsDittaEdit.setCodComuneNascita(codComuneNascita);
    if(dataNascita != null){
      Calendar dNascita = Calendar.getInstance();
      dNascita.setTime(dataNascita);
      wsDittaEdit.setDataNascita(dNascita);
    }

    wsDittaEdit.setCognome(cognome);
    wsDittaEdit.setNome(nome);
    wsDittaEdit.setGenere(genere);
    wsDittaEdit.setTipoDittaIndividuale(codFormaGiuridica);

    Calendar dOdierna = Calendar.getInstance();
    dOdierna.setTime(UtilityDate.getDataOdiernaAsDate());

    WsDittaIndirizzo wsDittaIndirizzo = new WsDittaIndirizzo();

    wsDittaIndirizzo.setCodNazione(codNazioneSede);
    if(codNazioneSede != null){
      if(!"IT".equals(codNazioneSede)){
        wsDittaIndirizzo.setCapStraniero(capSede);
        wsDittaIndirizzo.setDescrCittaStraniera(localitaPrincipale);
      }else{
        wsDittaIndirizzo.setCivico(civicoSede);
        wsDittaIndirizzo.setCodComune(codComuneSede);
        wsDittaIndirizzo.setCap(capSede);
      }
      wsDittaIndirizzo.setDataInizio(dOdierna);
      wsDittaIndirizzo.setIndirizzo(indirizzoSede);
      wsDittaIndirizzo.setStoricizza(false);
    }

    wsDittaEdit.setIndirizzoFiscale(wsDittaIndirizzo);
    wsDittaEdit.setIndirizzoResidenza(wsDittaIndirizzo);

    WsDittaContatti wsDittaContatti = new WsDittaContatti();
    wsDittaContatti.setFax(faxUfficio);
    wsDittaContatti.setCellUfficio(cellUfficio);
    wsDittaContatti.setPEC(pecUfficio);
    wsDittaContatti.setPostaElettronicaUfficio(emailUfficio);
    wsDittaContatti.setTelUfficio(telUfficio);
    wsDittaContatti.setUrlSitoWeb(urlSitoWeb);
    wsDittaEdit.setContatti(wsDittaContatti);

    wsDittaInserisciRequest.setDitta(wsDittaEdit);

    /* Non vengono considerate le sezioni PERMESSO SOGGGIORNO ,IMMATRICOLAZIONE e USER */

      try {

        WSACDitteService ws_di = this.getWSDitteIndividuali(SERVIZIO_DITTEINDIVIDUALI);
        String client = ConfigManager.getValore(PROP_WSDITTEINDIVIDUALI_CLIENT);

        wsDittaInserisciRequest.setClient(client);

        WsDittaInserisciResponse wsDittaInserisciResponse = ws_di.inserisciDittaIndividuale(wsDittaInserisciRequest);

        if(wsDittaInserisciResponse.getIdInterno() != null){
          idInterno = wsDittaInserisciResponse.getIdInterno();
          resMsg[0] = idInterno.toString();

          //COORDINATE DI PAGAMENTO

          try {

            Boolean prioritaMassima = true;
            it.cineca.u_gov.ac.di.ws.WsdtoPagamento coordPagDitteInd = new it.cineca.u_gov.ac.di.ws.WsdtoPagamento();
            ccDedicato = UtilityStringhe.convertiNullInStringaVuota(ccDedicato);
            if(!"".equals(ccDedicato)){
              String cin = "";
              String abi = "";
              String cab = "";
              String numConto = "";

              ccDedicato = ccDedicato.replace(" ", "");
              //es IT 88 A 01234 56789 012345678901
              if(ccDedicato.length() >= 27){
                cin = ccDedicato.substring(4,5);
                abi = ccDedicato.substring(5,10);
                cab = ccDedicato.substring(10,15);
                numConto = ccDedicato.substring(15);
              }
              String codMod = null;
              String codNazione = null;

              if(codNazioneDomFiscale != null){
                if(!"IT".equals(codNazioneDomFiscale)){
                  codMod = "BU"; //bonifici esteri con IBAN
                  codNazione = codNazioneDomFiscale; //bonifici esteri
                  if(!"".equals(bic)){
                    coordPagDitteInd.setBic(bic);
                  }
                }else{
                  codMod = "CC"; //bonifici nazionali con IBAN
                  codNazione = "IT"; //bonifici nazionali ITALIA
                  coordPagDitteInd.setCin(cin);
                  coordPagDitteInd.setAbi(abi);
                  coordPagDitteInd.setCab(cab);
                  coordPagDitteInd.setNumeroConto(numConto);
                }
              }

              Calendar dFutura = Calendar.getInstance();

              SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
              String dateInString = "02-02-2222 00:00:00";
              Date date = sdf.parse(dateInString);
              dFutura.setTime(date);

              coordPagDitteInd.setIban(ccDedicato);
              coordPagDitteInd.setCodMod(codMod);
              coordPagDitteInd.setCodNazione(codNazione);
              coordPagDitteInd.setDataInizio(dOdierna);
              coordPagDitteInd.setDataFine(dFutura);
              coordPagDitteInd.setIntestazioneConto(ragioneSociale);

              WsDittaInserisciCoordPagRequest wsDittaInserisciCoordPagRequest = new WsDittaInserisciCoordPagRequest();
              wsDittaInserisciCoordPagRequest.setClient(client);
              WsDittaBase wsDittaBase = new WsDittaBase();
              wsDittaBase.setIdInterno(idInterno);
              wsDittaInserisciCoordPagRequest.setRicerca(wsDittaBase);
              wsDittaInserisciCoordPagRequest.setPrioritaMassima(prioritaMassima);
              wsDittaInserisciCoordPagRequest.setCoordPag(coordPagDitteInd);
              WsDittaInserisciCoordPagResponse wsDittaInserisciCoordPagResponse = ws_di.inserisciCoordPagamento(wsDittaInserisciCoordPagRequest);

              if(wsDittaInserisciCoordPagResponse != null && wsDittaInserisciCoordPagResponse.getMessaggi() != null){
                WsDittaMessaggio[] wsDittaMessaggioArray = wsDittaInserisciCoordPagResponse.getMessaggi();
                WsDittaMessaggio wsDittaMessaggio = wsDittaMessaggioArray[0];
                if("E".equals(wsDittaMessaggio.getSeverity())){
                  resMsg[0] = "-7";
                  resMsg[1] = resMsg[1] + "\r\n" +  wsDittaMessaggio.getDescrizione();
                }
              }
            }

          } catch (Throwable t) {
            UtilityStruts.addMessage(request, "warning",
                "warnings.cineca.mancataIntegrazioneCoordPag.warning",
                new Object[] {t.getMessage()});

            resMsg[0] = "-5";
            resMsg[1] =  t.getMessage();
            return resMsg;

          }


        }else{
          WsDittaMessaggio[] msgArray = wsDittaInserisciResponse.getMessaggi();
          WsDittaMessaggio wsDittaMessaggio = msgArray[0];
          if("E".equals(wsDittaMessaggio.getSeverity())){
            resMsg[0] = "-7";
            resMsg[1] = wsDittaMessaggio.getDescrizione();
          }
        }


      } catch (Throwable t) {
        throw new GestoreException("Si e' verificato un errore durante l'inserimento della ditta individuale: " + t.getMessage(),
            "cineca.dittaIndividuale.remote.error", new Object[] {t.getMessage()}, t);
      }





    return resMsg;
  }



  /**
   * Modifica una ditta individuale
   *
   * @param HashMap
   * @return WsDittaModificaResponse
   * @throws GestoreException
   */
  public String[] wsCinecaModificaDittaIndividuale(HttpServletRequest request, HashMap<String,Object> datiDittaIndividuale, String client)
    throws GestoreException {

    String[] resMsg = new String[2];
    WsDittaModificaResponse wsDittaModificaResponse = new WsDittaModificaResponse();
    WsDittaModificaRequest wsDittaModificaRequest = new WsDittaModificaRequest();

    Date dataIscrCCIAA = (Date) datiDittaIndividuale.get("dataIscrCCIAA");
    Long idInterno = (Long) datiDittaIndividuale.get("idInterno");
    String codEsterno = (String) datiDittaIndividuale.get("codEsterno");
    codEsterno = UtilityStringhe.convertiNullInStringaVuota(codEsterno);
    String partitaIva = (String) datiDittaIndividuale.get("partitaIva");
    String codiceFiscale = (String) datiDittaIndividuale.get("codiceFiscale");
    String ragioneSociale = (String) datiDittaIndividuale.get("ragioneSociale");
    String rapprLegale = (String) datiDittaIndividuale.get("rapprLegale");
    String capDomFiscale = (String) datiDittaIndividuale.get("capDomFiscale");
    String capSede = (String) datiDittaIndividuale.get("capSede");
    String codNazioneSede = (String) datiDittaIndividuale.get("codNazioneSede");
    String codFormaGiuridica = (String) datiDittaIndividuale.get("codFormaGiuridica");
    codFormaGiuridica = UtilityStringhe.convertiNullInStringaVuota(codFormaGiuridica);
    String codNazioneDomFiscale = (String) datiDittaIndividuale.get("codNazioneDomFiscale");
    /*
    String capStranieroDomFiscale = (String) datiDittaIndividuale.get("capStranieroDomFiscale");
    String capStranieroSede = (String) datiDittaIndividuale.get("capStranieroSede");
    */
    String indirizzoSede = (String) datiDittaIndividuale.get("indirizzoSede");
    String indirizzoDomFiscale = (String) datiDittaIndividuale.get("indirizzoDomFiscale");
    String civicoSede = (String) datiDittaIndividuale.get("civicoSede");
    String civicoDomFiscale = (String) datiDittaIndividuale.get("civicoDomFiscale");
    String codComuneDomFiscale = (String) datiDittaIndividuale.get("codComuneDomFiscale");
    String codComuneSede = (String) datiDittaIndividuale.get("codComuneSede");
    String faxUfficio = (String) datiDittaIndividuale.get("faxUfficio");
    String telUfficio = (String) datiDittaIndividuale.get("telUfficio");
    String cellUfficio = (String) datiDittaIndividuale.get("cellUfficio");
    String emailUfficio = (String) datiDittaIndividuale.get("emailUfficio");
    String pecUfficio = (String) datiDittaIndividuale.get("pecUfficio");
    String urlSitoWeb = (String) datiDittaIndividuale.get("urlSitoWeb");
    String ccDedicato = (String) datiDittaIndividuale.get("ccDedicato");
    String bic = (String) datiDittaIndividuale.get("bic");
    String codComuneNascita = (String) datiDittaIndividuale.get("codComuneNascita");
    Date dataNascita = (Date) datiDittaIndividuale.get("dataNascita");
    String cognome = (String) datiDittaIndividuale.get("cognome");
    String nome = (String) datiDittaIndividuale.get("nome");
    String genere = (String) datiDittaIndividuale.get("genere");
    String codAlboProf = (String) datiDittaIndividuale.get("codAlboProf");



    WsDittaBase wsDittaBase = new WsDittaBase();
    wsDittaBase.setIdInterno(idInterno);
    wsDittaModificaRequest.setRicerca(wsDittaBase);

    WsDittaEdit wsDittaEdit = new WsDittaEdit();
    wsDittaEdit.setCodAnagrafico(codEsterno);
    wsDittaEdit.setRagioneSociale(ragioneSociale);
    wsDittaEdit.setCognome(cognome);
    wsDittaEdit.setNome(nome);
    wsDittaEdit.setGenere(genere);


    if(codNazioneSede != null && !"IT".equals(codNazioneSede)){
      wsDittaEdit.setCodFiscaleEstero(codiceFiscale);
      wsDittaEdit.setPartitaIVAEstera(partitaIva);
      wsDittaEdit.setCittaStranieraNascita(codComuneNascita);
    }else{
      wsDittaEdit.setCodFiscale(codiceFiscale);
      wsDittaEdit.setPartitaIVA(partitaIva);
    }

    wsDittaEdit.setCodComuneNascita(codComuneNascita);
    wsDittaEdit.setCodNazioneNascita(codNazioneDomFiscale);
    if(dataNascita != null){
      Calendar dNascita = Calendar.getInstance();
      dNascita.setTime(dataNascita);
      wsDittaEdit.setDataNascita(dNascita);
    }

    Calendar dFutura = Calendar.getInstance();

    SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
    String dateInString = "02-02-2222 00:00:00";
    try {
      Date date = sdf.parse(dateInString);
      dFutura.setTime(date);

    } catch (ParseException e) {
      throw new GestoreException(null,e.getMessage());
    }


    WsDittaIndirizzo wsDittaIndirizzo = new WsDittaIndirizzo();
    wsDittaIndirizzo.setCap(capSede);
    wsDittaIndirizzo.setCivico(civicoSede);
    wsDittaIndirizzo.setCodComune(codComuneSede);
    wsDittaIndirizzo.setCodNazione(codNazioneSede);
    Calendar dOdierna = Calendar.getInstance();
    dOdierna.setTime(UtilityDate.getDataOdiernaAsDate());
    wsDittaIndirizzo.setDataInizio(dOdierna);
    wsDittaIndirizzo.setDataFine(dFutura);
    wsDittaIndirizzo.setIndirizzo(indirizzoSede);
    wsDittaIndirizzo.setStoricizza(false);
    wsDittaEdit.setIndirizzoFiscale(wsDittaIndirizzo);
    wsDittaEdit.setIndirizzoResidenza(wsDittaIndirizzo);

    //CONTATTI
    WsDittaContatti wsDittaContatti = new WsDittaContatti();
    wsDittaContatti.setFax(faxUfficio);
    wsDittaContatti.setCellUfficio(cellUfficio);
    wsDittaContatti.setPEC(pecUfficio);
    wsDittaContatti.setPostaElettronicaUfficio(emailUfficio);
    wsDittaContatti.setTelUfficio(telUfficio);
    wsDittaContatti.setUrlSitoWeb(urlSitoWeb);
    wsDittaEdit.setContatti(wsDittaContatti);

    if("".equals(codFormaGiuridica)){
      codFormaGiuridica="0";
    }
    wsDittaEdit.setTipoDittaIndividuale(codFormaGiuridica);

    wsDittaModificaRequest.setDitta(wsDittaEdit);


    try {

      WSACDitteService ws_di = this.getWSDitteIndividuali(SERVIZIO_DITTEINDIVIDUALI);
      //if("".equals(codEsterno)){
        wsDittaModificaRequest.setClient(client);
      //}

      wsDittaModificaResponse  = ws_di.modificaDittaIndividuale(wsDittaModificaRequest);

      if(wsDittaModificaResponse != null && wsDittaModificaResponse.getMessaggi() != null){
        WsDittaMessaggio[] wsDittaMessaggioArray = wsDittaModificaResponse.getMessaggi();
        WsDittaMessaggio wsDittaMessaggio = wsDittaMessaggioArray[0];
        if("E".equals(wsDittaMessaggio.getSeverity())){
          resMsg[0] = "-7";
          resMsg[1] = wsDittaMessaggio.getDescrizione();
        }
      }

      try {

        ccDedicato = UtilityStringhe.convertiNullInStringaVuota(ccDedicato);
        bic = UtilityStringhe.convertiNullInStringaVuota(bic);
        if(!"".equals(ccDedicato)){
          //MODIFICA E/O eventuale INSERIMENTO coordinate di pagamento
          Boolean prioritaMassima = true;

          //verifico la presenza delle coordinate:cerco nell'elenco
          WsDittaElencaCoordPagResponse wsDittaElencaCoordPagResponse = ws_di.elencaCoordPagamento(wsDittaBase);
          if(wsDittaElencaCoordPagResponse != null && wsDittaElencaCoordPagResponse.getListaCoordPagamento() != null){
             it.cineca.u_gov.ac.di.ws.WsdtoPagamento[] diWsdtoPagamentoArray = wsDittaElencaCoordPagResponse.getListaCoordPagamento();
             if(diWsdtoPagamentoArray != null){
               boolean founded = false;
               for (int k = 0; k < diWsdtoPagamentoArray.length; k++) {
                 it.cineca.u_gov.ac.di.ws.WsdtoPagamento diWsdtoPagamento = diWsdtoPagamentoArray[k];
                 String coord_iban = diWsdtoPagamento.getIban();
                 coord_iban = UtilityStringhe.convertiNullInStringaVuota(coord_iban);
                 String coord_codmod = diWsdtoPagamento.getCodMod();
                 coord_codmod = UtilityStringhe.convertiNullInStringaVuota(coord_codmod);
                 String coord_codnaz = diWsdtoPagamento.getCodNazione();
                 coord_codnaz = UtilityStringhe.convertiNullInStringaVuota(coord_codnaz);
                 Long coord_id = diWsdtoPagamento.getIdCoordPag();
                 //qui andrà discriminato per Nazione
                   ccDedicato = ccDedicato.replace(" ", "");
                 //if(("CC".equals(coord_codmod) && "IT".equals(coord_codnaz)) || ("BU".equals(coord_codmod) && !"IT".equals(coord_codnaz))){
                   //VERIFICARE BENE QUESTO FOUNDED = TRUE
                   if(ccDedicato.equals(coord_iban)){
                     founded = true;
                     it.cineca.u_gov.ac.di.ws.WsdtoPagamento coordPagDitteInd = new it.cineca.u_gov.ac.di.ws.WsdtoPagamento();
                     //es IT 88 A 01234 56789 012345678901
                     if("CC".equals(coord_codmod) && "IT".equals(coord_codnaz)){
                       String cin = ccDedicato.substring(4,5);
                       String abi = ccDedicato.substring(5,10);
                       String cab = ccDedicato.substring(10,15);
                       String numConto = ccDedicato.substring(15);
                       coordPagDitteInd.setCin(cin);
                       coordPagDitteInd.setAbi(abi);
                       coordPagDitteInd.setCab(cab);
                       coordPagDitteInd.setNumeroConto(numConto);
                     }else{
                       if(!"".equals(bic)){
                         coordPagDitteInd.setBic(bic);
                       }
                     }

                     String codMod = coord_codmod;
                     String codNazione = coord_codnaz;
                     //coordPagDitteInd.setIdCoordPag(coord_id); mettere fuori ..qui forse e' modificabile annche questo...
                     coordPagDitteInd.setIban(ccDedicato);
                     coordPagDitteInd.setCodMod(codMod);
                     coordPagDitteInd.setCodNazione(codNazione);
                     coordPagDitteInd.setDataInizio(dOdierna);
                     coordPagDitteInd.setDataFine(dFutura);
                     coordPagDitteInd.setIntestazioneConto(ragioneSociale);
                     //vado in modifica
                     WsDittaModificaCoordPagRequest wsDittaModificaCoordPagRequest = new WsDittaModificaCoordPagRequest();
                     wsDittaModificaCoordPagRequest.setClient(client);
                     wsDittaModificaCoordPagRequest.setIdCoordPag(coord_id);
                     wsDittaModificaCoordPagRequest.setCoordPag(coordPagDitteInd);
                     wsDittaModificaCoordPagRequest.setRicerca(wsDittaBase);
                     WsDittaModificaCoordPagResponse wsDittaModificaCoordPagResponse  = ws_di.modificaCoordPagamento(wsDittaModificaCoordPagRequest);
                     if(wsDittaModificaCoordPagResponse != null && wsDittaModificaCoordPagResponse.getMessaggi() != null){
                       WsDittaMessaggio[] wsDittaMessaggioArray = wsDittaModificaCoordPagResponse.getMessaggi();
                       WsDittaMessaggio wsDittaMessaggio = wsDittaMessaggioArray[0];
                       if("E".equals(wsDittaMessaggio.getSeverity())){
                         resMsg[0] = "-7";
                         resMsg[1] = resMsg[1] + "\r\n" +  wsDittaMessaggio.getDescrizione();
                       }
                     }
                   }//if modificato iban
                 //}//if identifico
               }//for


               if(!founded){
                 it.cineca.u_gov.ac.di.ws.WsdtoPagamento coordPagDitteInd = new it.cineca.u_gov.ac.di.ws.WsdtoPagamento();
                 String codMod = null;
                 String codNazione = null;
                 if(codNazioneDomFiscale != null){
                   if(!"IT".equals(codNazioneDomFiscale)){
                     codMod = "BU"; //bonifici esteri con IBAN
                     codNazione = codNazioneDomFiscale; //bonifici esteri
                     if(!"".equals(bic)){
                       coordPagDitteInd.setBic(bic);
                     }
                   }else{
                     codMod = "CC"; //bonifici nazionali con IBAN
                     codNazione = "IT"; //bonifici nazionali ITALIA
                     String cin = ccDedicato.substring(4,5);
                     String abi = ccDedicato.substring(5,10);
                     String cab = ccDedicato.substring(10,15);
                     String numConto = ccDedicato.substring(15);
                     coordPagDitteInd.setCin(cin);
                     coordPagDitteInd.setAbi(abi);
                     coordPagDitteInd.setCab(cab);
                     coordPagDitteInd.setNumeroConto(numConto);
                   }
                 }
                 coordPagDitteInd.setIban(ccDedicato);
                 coordPagDitteInd.setCodMod(codMod);
                 coordPagDitteInd.setCodNazione(codNazione);
                 coordPagDitteInd.setIntestazioneConto(ragioneSociale);
                 coordPagDitteInd.setDataInizio(dOdierna);
                 coordPagDitteInd.setDataFine(dFutura);
                 WsDittaInserisciCoordPagRequest wsDittaInserisciCoordPagRequest = new WsDittaInserisciCoordPagRequest();
                 wsDittaInserisciCoordPagRequest.setClient(client);
                 wsDittaBase.setIdInterno(idInterno);
                 wsDittaInserisciCoordPagRequest.setRicerca(wsDittaBase);
                 wsDittaInserisciCoordPagRequest.setPrioritaMassima(prioritaMassima);
                 wsDittaInserisciCoordPagRequest.setCoordPag(coordPagDitteInd);
                 WsDittaInserisciCoordPagResponse wsDittaInserisciCoordPagResponse = ws_di.inserisciCoordPagamento(wsDittaInserisciCoordPagRequest);
                 if(wsDittaInserisciCoordPagResponse != null && wsDittaInserisciCoordPagResponse.getMessaggi() != null){
                   WsDittaMessaggio[] wsDittaMessaggioArray = wsDittaInserisciCoordPagResponse.getMessaggi();
                   WsDittaMessaggio wsDittaMessaggio = wsDittaMessaggioArray[0];
                   if("E".equals(wsDittaMessaggio.getSeverity())){
                     resMsg[0] = "-7";
                     resMsg[1] = resMsg[1] + "\r\n" +  wsDittaMessaggio.getDescrizione();
                   }
                 }
               }
             }else{//else coord pag
               it.cineca.u_gov.ac.di.ws.WsdtoPagamento coordPagDitteInd = new it.cineca.u_gov.ac.di.ws.WsdtoPagamento();
               String codMod = null;
               String codNazione = null;
               if(codNazioneDomFiscale != null){
                 if(!"IT".equals(codNazioneDomFiscale)){
                   codMod = "BU"; //bonifici esteri con IBAN
                   codNazione = codNazioneDomFiscale; //bonifici esteri
                   if(!"".equals(bic)){
                     coordPagDitteInd.setBic(bic);
                   }
                 }else{
                   codMod = "CC"; //bonifici nazionali con IBAN
                   codNazione = "IT"; //bonifici nazionali ITALIA
                   String cin = ccDedicato.substring(4,5);
                   String abi = ccDedicato.substring(5,10);
                   String cab = ccDedicato.substring(10,15);
                   String numConto = ccDedicato.substring(15);
                   coordPagDitteInd.setCin(cin);
                   coordPagDitteInd.setAbi(abi);
                   coordPagDitteInd.setCab(cab);
                   coordPagDitteInd.setNumeroConto(numConto);
                 }
               }

               coordPagDitteInd.setIban(ccDedicato);
               coordPagDitteInd.setCodMod(codMod);
               coordPagDitteInd.setCodNazione(codNazione);
               coordPagDitteInd.setIntestazioneConto(ragioneSociale);
               coordPagDitteInd.setDataInizio(dOdierna);
               coordPagDitteInd.setDataFine(dFutura);
               WsDittaInserisciCoordPagRequest wsDittaInserisciCoordPagRequest = new WsDittaInserisciCoordPagRequest();
               wsDittaInserisciCoordPagRequest.setClient(client);
               wsDittaBase.setIdInterno(idInterno);
               wsDittaInserisciCoordPagRequest.setRicerca(wsDittaBase);
               wsDittaInserisciCoordPagRequest.setPrioritaMassima(prioritaMassima);
               wsDittaInserisciCoordPagRequest.setCoordPag(coordPagDitteInd);
               WsDittaInserisciCoordPagResponse wsDittaInserisciCoordPagResponse = ws_di.inserisciCoordPagamento(wsDittaInserisciCoordPagRequest);
               if(wsDittaInserisciCoordPagResponse != null && wsDittaInserisciCoordPagResponse.getMessaggi() != null){
                 WsDittaMessaggio[] wsDittaMessaggioArray = wsDittaInserisciCoordPagResponse.getMessaggi();
                 WsDittaMessaggio wsDittaMessaggio = wsDittaMessaggioArray[0];
                 if("E".equals(wsDittaMessaggio.getSeverity())){
                   resMsg[0] = "-7";
                   resMsg[1] = resMsg[1] + "\r\n" +  wsDittaMessaggio.getDescrizione();
                 }
               }

             }//if coord

          }else{
            it.cineca.u_gov.ac.di.ws.WsdtoPagamento coordPagDitteInd = new it.cineca.u_gov.ac.di.ws.WsdtoPagamento();
            String codMod = null;
            String codNazione = null;
            if(codNazioneDomFiscale != null){
              if(!"IT".equals(codNazioneDomFiscale)){
                codMod = "BU"; //bonifici esteri con IBAN
                codNazione = codNazioneDomFiscale; //bonifici esteri
                if(!"".equals(bic)){
                  coordPagDitteInd.setBic(bic);
                }
              }else{
                codMod = "CC"; //bonifici nazionali con IBAN
                codNazione = "IT"; //bonifici nazionali ITALIA
                String cin = ccDedicato.substring(4,5);
                String abi = ccDedicato.substring(5,10);
                String cab = ccDedicato.substring(10,15);
                String numConto = ccDedicato.substring(15);
                coordPagDitteInd.setCin(cin);
                coordPagDitteInd.setAbi(abi);
                coordPagDitteInd.setCab(cab);
                coordPagDitteInd.setNumeroConto(numConto);
              }
            }

            coordPagDitteInd.setIban(ccDedicato);
            coordPagDitteInd.setCodMod(codMod);
            coordPagDitteInd.setCodNazione(codNazione);
            coordPagDitteInd.setIntestazioneConto(ragioneSociale);
            coordPagDitteInd.setDataInizio(dOdierna);
            coordPagDitteInd.setDataFine(dFutura);
            WsDittaInserisciCoordPagRequest wsDittaInserisciCoordPagRequest = new WsDittaInserisciCoordPagRequest();
            wsDittaInserisciCoordPagRequest.setClient(client);
            wsDittaBase.setIdInterno(idInterno);
            wsDittaInserisciCoordPagRequest.setRicerca(wsDittaBase);
            wsDittaInserisciCoordPagRequest.setPrioritaMassima(prioritaMassima);
            wsDittaInserisciCoordPagRequest.setCoordPag(coordPagDitteInd);
            WsDittaInserisciCoordPagResponse wsDittaInserisciCoordPagResponse = ws_di.inserisciCoordPagamento(wsDittaInserisciCoordPagRequest);
            if(wsDittaInserisciCoordPagResponse != null && wsDittaInserisciCoordPagResponse.getMessaggi() != null){
              WsDittaMessaggio[] wsDittaMessaggioArray = wsDittaInserisciCoordPagResponse.getMessaggi();
              WsDittaMessaggio wsDittaMessaggio = wsDittaMessaggioArray[0];
              if("E".equals(wsDittaMessaggio.getSeverity())){
                resMsg[0] = "-7";
                resMsg[1] = resMsg[1] + "\r\n" +  wsDittaMessaggio.getDescrizione();
              }
            }

          }//if

        }


      } catch (Throwable t) {
        UtilityStruts.addMessage(request, "warning",
            "warnings.cineca.mancataIntegrazioneCoordPag.warning",
            new Object[] {t.getMessage()});
      }



    } catch (Throwable t) {
      throw new GestoreException("Si è verificato un errore durante l'integrazione U-GOV con la ditta individuale: " + t.getMessage(),
          "cineca.dittaIndividuale.remote.error",new Object[] {t.getMessage()}, t);
    }




    return resMsg;
  }


  /**
   * Modifica una ditta individuale
   *
   * @param HashMap
   * @return WsDittaModificaResponse
   * @throws GestoreException
   */
  public String[] wsCinecaModProMastDittaIndividuale(Long idInterno, String codAnagrafico)
    throws GestoreException {

    String[] resMsg = new String[2];

    WsDittaModificaResponse wsDittaModificaResponse = new WsDittaModificaResponse();
    WsDittaModificaRequest wsDittaModificaRequest = new WsDittaModificaRequest();

    WsDittaBase wsDittaBase = new WsDittaBase();
    wsDittaBase.setIdInterno(idInterno);
    wsDittaModificaRequest.setRicerca(wsDittaBase);

    WsDittaEdit wsDittaEdit = new WsDittaEdit();
    codAnagrafico=UtilityStringhe.convertiNullInStringaVuota(codAnagrafico);
    wsDittaEdit.setCodAnagrafico(codAnagrafico);

    wsDittaModificaRequest.setDitta(wsDittaEdit);

    try {

      WSACDitteService ws_di = this.getWSDitteIndividuali(SERVIZIO_DITTEINDIVIDUALI);
      String client = ConfigManager.getValore(PROP_WSDITTEINDIVIDUALI_CLIENT);
      //wsDittaModificaRequest.setClient(client);
      wsDittaModificaResponse  = ws_di.modificaDittaIndividuale(wsDittaModificaRequest);
      if(wsDittaModificaResponse != null && wsDittaModificaResponse.getMessaggi() != null){
        WsDittaMessaggio[] wsDittaMessaggioArray = wsDittaModificaResponse.getMessaggi();
        WsDittaMessaggio wsDittaMessaggio = wsDittaMessaggioArray[0];
        if("E".equals(wsDittaMessaggio.getSeverity())){
          resMsg[0] = "-7";
          resMsg[1] = wsDittaMessaggio.getDescrizione();
        }
      }

    } catch (Throwable t) {
      throw new GestoreException("Si è verificato un errore durante l'integrazione U-GOV con la ditta individuale: " + t.getMessage(),
          "cineca.dittaIndividuale.remote.error",new Object[] {t.getMessage()}, t);
    }

    return resMsg;
  }



}
