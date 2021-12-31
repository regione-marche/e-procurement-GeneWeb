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

import it.cineca.u_gov.ac.pf.ws.WSACPersonaFisicaService;
import it.cineca.u_gov.ac.pf.ws.Ws_002fPrivate_002fPersonaFisica;
import it.cineca.u_gov.ac.pf.ws.WsdtoPagamento;
import it.cineca.u_gov.ac.pf.ws.WsdtoPersonaFisica;
import it.cineca.u_gov.ac.pf.ws.WsdtoPersonaFisicaResponce;
import it.cineca.u_gov.ac.pf.ws.WsdtoPersonaFisicaSearch;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.rpc.ServiceException;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

/**
 * Integrazione WS Persona Fisica Cineca
 *
 *
 * @author Cristian.Febas
 *
 */
public class CinecaWSPersoneFisicheManager {

  static Logger               logger                         = Logger.getLogger(CinecaWSPersoneFisicheManager.class);

  private static final String SERVIZIO_PERSONAFISICA                        = "PERSONAFISICA";
  private static final String PROP_WSPERSONAFISICA_URL                      = "cineca.ws.PersonaFisica.url";
  private static final String PROP_WSPERSONAFISICA_CLIENT                   = "cineca.ws.SoggettoCollettivo.client";

  private SqlManager          sqlManager;

  private CinecaAnagraficaComuneManager          cinecaAnagraficaComuneManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setCinecaAnagraficaComuneManager(CinecaAnagraficaComuneManager cinecaAnagraficaComuneManager) {
    this.cinecaAnagraficaComuneManager = cinecaAnagraficaComuneManager;
  }



  /**
   *  esistenza persona fisica
   *
   * @param codimp
   * @return res
   * -1 = non esiste
   * 1 = si ma non masterizzato
   * 2 = si e masterizzato
   * @throws GestoreException
   */
  public String[] getCinecaPersonaFisica(HttpServletRequest request,String dittaCineca)
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
          if((partitaIva !=null && !cinecaAnagraficaComuneManager.controlloCF(partitaIva)) || (codiceFiscale !=null && !cinecaAnagraficaComuneManager.controlloCF(codiceFiscale))){
            res[0] = "-2";
            return res;
          }
        }
        if(codiceFiscale!=null || partitaIva!=null){
          WsdtoPersonaFisicaResponce personaFisicaResp = this.wsCinecaEstraiPersonaFisica(codiceFiscale,partitaIva,nazionalita);
          if(personaFisicaResp != null && personaFisicaResp.getCognome() != null ){
            if((new Long(1).equals(nazionalita) && (personaFisicaResp.getPartitaIva() != null || personaFisicaResp.getCodiceFiscale() != null))
                || (!new Long(1).equals(nazionalita) && personaFisicaResp.getPartitaIvaEstero() != null && personaFisicaResp.getPartitaIvaEstero().equals(partitaIva))){
              if(personaFisicaResp.getCodAnagrafico() != null){
                res[0] = "2";
                res[1] = personaFisicaResp.getCodAnagrafico();
                Long idInterno = personaFisicaResp.getIdInterno();
                if(idInterno != null){
                  res[2] = idInterno.toString();
                }
                return res;
              }else{
                res[0] = "1";
                Long idInterno = personaFisicaResp.getIdInterno();
                res[2] = idInterno.toString();
              }
            }else{
              res[0] = "-1";
            }
          }else{
            res[0] = "-1";
          }
        }else{
          res[0] = "-1";
        }
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella selezione di partita iva e codice fiscale della persona fisica ", null,e);
    } catch (Throwable t) {
      if(request != null){
        UtilityStruts.addMessage(request, "warning",
            "warnings.cineca.mancataIntegrazionePersonaFisica.warning",
            new Object[] {t.getMessage()});

      }
    }


    return res;

  }

  /**
   * Inserisce un soggetto collettivo
   *
   * @param codiceFiscale
   * @return WsdtoSoggettoCollettivoResponse
   * @throws GestoreException
   */
  public Long wsCinecaInserisciPerosnaFisica(HttpServletRequest request, HashMap<String,Object> datiPersonaFisica)
    throws GestoreException {


    WsdtoPersonaFisica personaFisica = new WsdtoPersonaFisica();

    Long idInterno = null;

    String codEsterno = (String) datiPersonaFisica.get("codEsterno");
    Long idInternoSede = (Long) datiPersonaFisica.get("idInternoSede");
    Long tipologia = (Long) datiPersonaFisica.get("tipologia");
    String ragioneSociale = (String) datiPersonaFisica.get("ragioneSociale");
    String rapprLegale = (String) datiPersonaFisica.get("rapprLegale");
    Date dataIscrCCIAA = (Date) datiPersonaFisica.get("dataIscrCCIAA");
    String partitaIva = (String) datiPersonaFisica.get("partitaIva");
    String codiceFiscale = (String) datiPersonaFisica.get("codiceFiscale");
    String codNazioneDomFiscale = (String) datiPersonaFisica.get("codNazioneDomFiscale");
    String capDomFiscale = (String) datiPersonaFisica.get("capDomFiscale");
    String indirizzoDomFiscale = (String) datiPersonaFisica.get("indirizzoDomFiscale");
    String civicoDomFiscale = (String) datiPersonaFisica.get("civicoDomFiscale");
    String codComuneDomFiscale = (String) datiPersonaFisica.get("codComuneDomFiscale");
    String codNazioneSede = (String) datiPersonaFisica.get("codNazioneSede");
    codNazioneSede = UtilityStringhe.convertiNullInStringaVuota(codNazioneSede);
    String codFormaGiuridica = (String) datiPersonaFisica.get("codFormaGiuridica");
    codFormaGiuridica = UtilityStringhe.convertiNullInStringaVuota(codFormaGiuridica);
    String capSede = (String) datiPersonaFisica.get("capSede");
    String indirizzoSede = (String) datiPersonaFisica.get("indirizzoSede");
    String civicoSede = (String) datiPersonaFisica.get("civicoSede");
    String codComuneSede = (String) datiPersonaFisica.get("codComuneSede");
    String faxUfficio = (String) datiPersonaFisica.get("faxUfficio");
    String telUfficio = (String) datiPersonaFisica.get("telUfficio");
    String cellUfficio = (String) datiPersonaFisica.get("cellUfficio");
    String emailUfficio = (String) datiPersonaFisica.get("emailUfficio");
    String pecUfficio = (String) datiPersonaFisica.get("pecUfficio");
    String urlSitoWeb = (String) datiPersonaFisica.get("urlSitoWeb");
    String localitaPrincipale = (String) datiPersonaFisica.get("localitaPrincipale");
    String ccDedicato = (String) datiPersonaFisica.get("ccDedicato");
    String bic = (String) datiPersonaFisica.get("bic");
    String codComuneNascita = (String) datiPersonaFisica.get("codComuneNascita");
    Date dataNascita = (Date) datiPersonaFisica.get("dataNascita");
    String annotazioni = (String) datiPersonaFisica.get("annotazioni");
    String cognome = (String) datiPersonaFisica.get("cognome");
    String nome = (String) datiPersonaFisica.get("nome");
    String genere = (String) datiPersonaFisica.get("genere");
    String codAlboProf = (String) datiPersonaFisica.get("codAlboProf");
    codAlboProf = UtilityStringhe.convertiNullInStringaVuota(codAlboProf);
    String numIscrAlboProf = (String) datiPersonaFisica.get("numIscrAlboProf");
    Date dataIscrAlboProf = (Date) datiPersonaFisica.get("dataIscrAlboProf");
    String provAlboProf = (String) datiPersonaFisica.get("provAlboProf");
    String codTitoloOnorifico = (String) datiPersonaFisica.get("codTitoloOnorifico");


    try {

      //Date
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


      //Dati anagrafici
      personaFisica.setCodAnagrafico(codEsterno);
      personaFisica.setNome(nome);
      personaFisica.setCognome(cognome);
      if(dataNascita!= null){
        GregorianCalendar gcDataNascita = new GregorianCalendar();
        gcDataNascita.setTime(dataNascita);
        XMLGregorianCalendar xmlgcDataNascita = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcDataNascita);
        personaFisica.setDataNascita(xmlgcDataNascita);
      }
      personaFisica.setGenere(genere);
      personaFisica.setCodComuneNascita(codComuneNascita);
      personaFisica.setNote(annotazioni);

      //Dati fiscali
      if("".equals(codNazioneSede)){
        codNazioneSede = "IT";
      }
      if(codNazioneSede != null){//se ci sono indirizzi
        if(!"IT".equals(codNazioneSede)){
          personaFisica.setCodiceFiscaleEstero(codiceFiscale);
          personaFisica.setPartitaIvaEstero(partitaIva);
          personaFisica.setCapStranieraResidenza(capSede);
          personaFisica.setCapStranieroDomFiscale(capDomFiscale);
          personaFisica.setDescrCittaStranieraResidenza(localitaPrincipale);
          personaFisica.setDescrCittaStranieraDomFiscale(localitaPrincipale);
        }else{
          personaFisica.setCodiceFiscale(codiceFiscale);
          personaFisica.setPartitaIva(partitaIva);
          personaFisica.setCapResidenza(capSede);
          personaFisica.setCapDomFiscale(capDomFiscale);
          personaFisica.setCodNazioneNascita("IT");
        }

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(UtilityDate.getDataOdiernaAsDate());
        personaFisica.setDataInizioResidenza(dOdierna);
        personaFisica.setDataInizioDomFiscale(dOdierna);
        personaFisica.setDataFineResidenza(dFutura);
        personaFisica.setDataFineDomFiscale(dFutura);
      }

      personaFisica.setCodNazioneResidenza(codNazioneSede);
      personaFisica.setCodNazioneFiscale(codNazioneDomFiscale);
      personaFisica.setCivicoResidenza(civicoSede);
      personaFisica.setCivicoDomFiscale(civicoDomFiscale);
      personaFisica.setIndirizzoResidenza(indirizzoSede);
      personaFisica.setIndirizzoDomFiscale(indirizzoDomFiscale);
      personaFisica.setCodComuneResidenza(codComuneSede);
      personaFisica.setCodComuneDomFiscale(codComuneDomFiscale);

      //Dati sui contatti
      personaFisica.setTelDomicilio(telUfficio);
      personaFisica.setCellPersonale(cellUfficio);
      pecUfficio = UtilityStringhe.convertiNullInStringaVuota(pecUfficio);
      if(!"".equals(pecUfficio)){
        personaFisica.setPostaElettronicaPrivata(pecUfficio);
      }else{
        personaFisica.setPostaElettronicaPrivata(emailUfficio);
      }
      personaFisica.setFax(faxUfficio);
      personaFisica.setUrlSitoWeb(urlSitoWeb);
      personaFisica.setCodOnorifico(codTitoloOnorifico);


      WSACPersonaFisicaService ws_pf = this.getWSPersonaFisica(SERVIZIO_PERSONAFISICA);
      String client = ConfigManager.getValore(PROP_WSPERSONAFISICA_CLIENT);
      personaFisica.setClient(client);
      //valutare la questione aggancia utente
      idInterno = ws_pf.inserisciPersona(personaFisica, false, "PF");


      /* Ordini Professionali sospeso
      try{

            if(!"".equals(codAlboProf)){
              WsdtoOrdineProfessionale ordineProfessionale = new WsdtoOrdineProfessionale();
              ordineProfessionale.setCodTipoOrdProf(codAlboProf);
              ordineProfessionale.setNumeroAlbo(numIscrAlboProf);
              GregorianCalendar gcdia = new GregorianCalendar();
              gcdia.setTime(dataIscrAlboProf);
              XMLGregorianCalendar xmlgcDataIscrAlboProf = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcdia);
              ordineProfessionale.setDataIstrizioneAlbo(xmlgcDataIscrAlboProf);
              //VERIFICARE IL COD da passare per la provincia
              //ordineProfessionale.setCodProv(provAlboProf);
              WsdtoPersonaFisicaSearch dtoRicerca = new WsdtoPersonaFisicaSearch();
              dtoRicerca.setIdInterno(idInterno);
              ws_pf.inserisciOrdineProf(dtoRicerca , ordineProfessionale);
            }


      } catch (Throwable t) {
        UtilityStruts.addMessage(request, "warning",
            "warnings.cineca.mancataIntegrazioneOrdProf.warning",
            new Object[] {t.getMessage()});
      }


      */

      //COORDINATE DI PAGAMENTO
      try {

        Boolean prioritaMassima = true;
        WsdtoPagamento coordPagamento = new WsdtoPagamento();
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
                coordPagamento.setBic(bic);
              }
            }else{
              codMod = "CC"; //bonifici nazionali con IBAN
              codNazione = "IT"; //bonifici nazionali ITALIA
              coordPagamento.setCin(cin);
              coordPagamento.setAbi(abi);
              coordPagamento.setCab(cab);
              coordPagamento.setNumeroConto(numConto);
            }
          }


          coordPagamento.setIban(ccDedicato);
          coordPagamento.setCodMod(codMod);
          coordPagamento.setCodNazione(codNazione);
          coordPagamento.setDataInizio(dOdierna);
          coordPagamento.setDataFine(dFutura);
          coordPagamento.setIntestazioneConto(ragioneSociale);

          String username = null;
          String userAlias = null;
          String codEsse3 = null;
          String matricola = null;
          ws_pf.inserisciCoordPagamento(idInterno, codEsterno, null, matricola, codEsse3, codiceFiscale, null, username, userAlias, coordPagamento, prioritaMassima, "PF");
        }


      } catch (Throwable t) {
        UtilityStruts.addMessage(request, "warning",
            "warnings.cineca.mancataIntegrazioneCoordPag.warning",
            new Object[] {t.getMessage()});

        return new Long(-5);

      }

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante l'inserimento della persona fisica: " + t.getMessage(),
          "cineca.personaFisica.remote.error",new Object[] {t.getMessage()}, t);
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
  public WsdtoPersonaFisicaResponce wsCinecaModificaPersonaFisica(HttpServletRequest request, HashMap<String,Object> datiPersonaFisica, String client)
    throws GestoreException {

    String[] resMsg = new String[2];
    WsdtoPersonaFisicaResponce wsdtoPersonaFisicaResponce = new WsdtoPersonaFisicaResponce();
    WsdtoPersonaFisica wsdtoPersonaFisica = new WsdtoPersonaFisica();
    WsdtoPersonaFisicaSearch dtoRicerca= new WsdtoPersonaFisicaSearch();

    Date dataIscrCCIAA = (Date) datiPersonaFisica.get("dataIscrCCIAA");
    Long idInterno = (Long) datiPersonaFisica.get("idInterno");
    String codEsterno = (String) datiPersonaFisica.get("codEsterno");
    codEsterno = UtilityStringhe.convertiNullInStringaVuota(codEsterno);
    String partitaIva = (String) datiPersonaFisica.get("partitaIva");
    String codiceFiscale = (String) datiPersonaFisica.get("codiceFiscale");
    String ragioneSociale = (String) datiPersonaFisica.get("ragioneSociale");
    String rapprLegale = (String) datiPersonaFisica.get("rapprLegale");
    String capDomFiscale = (String) datiPersonaFisica.get("capDomFiscale");
    String capSede = (String) datiPersonaFisica.get("capSede");
    String codNazioneSede = (String) datiPersonaFisica.get("codNazioneSede");
    String codFormaGiuridica = (String) datiPersonaFisica.get("codFormaGiuridica");
    codFormaGiuridica = UtilityStringhe.convertiNullInStringaVuota(codFormaGiuridica);
    String codNazioneDomFiscale = (String) datiPersonaFisica.get("codNazioneDomFiscale");
    /*
    String capStranieroDomFiscale = (String) datiPersonaFisica.get("capStranieroDomFiscale");
    String capStranieroSede = (String) datiPersonaFisica.get("capStranieroSede");
    */
    String indirizzoSede = (String) datiPersonaFisica.get("indirizzoSede");
    String indirizzoDomFiscale = (String) datiPersonaFisica.get("indirizzoDomFiscale");
    String civicoSede = (String) datiPersonaFisica.get("civicoSede");
    String civicoDomFiscale = (String) datiPersonaFisica.get("civicoDomFiscale");
    String codComuneDomFiscale = (String) datiPersonaFisica.get("codComuneDomFiscale");
    String localitaPrincipale = (String) datiPersonaFisica.get("localitaPrincipale");
    String codComuneSede = (String) datiPersonaFisica.get("codComuneSede");
    String faxUfficio = (String) datiPersonaFisica.get("faxUfficio");
    String telUfficio = (String) datiPersonaFisica.get("telUfficio");
    String cellUfficio = (String) datiPersonaFisica.get("cellUfficio");
    String emailUfficio = (String) datiPersonaFisica.get("emailUfficio");
    String pecUfficio = (String) datiPersonaFisica.get("pecUfficio");
    String urlSitoWeb = (String) datiPersonaFisica.get("urlSitoWeb");
    String ccDedicato = (String) datiPersonaFisica.get("ccDedicato");
    String bic = (String) datiPersonaFisica.get("bic");
    String codComuneNascita = (String) datiPersonaFisica.get("codComuneNascita");
    String annotazioni = (String) datiPersonaFisica.get("annotazioni");
    Date dataNascita = (Date) datiPersonaFisica.get("dataNascita");
    String cognome = (String) datiPersonaFisica.get("cognome");
    String nome = (String) datiPersonaFisica.get("nome");
    String genere = (String) datiPersonaFisica.get("genere");
    String codAlboProf = (String) datiPersonaFisica.get("codAlboProf");
    codAlboProf = UtilityStringhe.convertiNullInStringaVuota(codAlboProf);
    String numIscrAlboProf = (String) datiPersonaFisica.get("numIscrAlboProf");
    Date dataIscrAlboProf = (Date) datiPersonaFisica.get("dataIscrAlboProf");
    String provAlboProf = (String) datiPersonaFisica.get("provAlboProf");
    String codTitoloOnorifico = (String) datiPersonaFisica.get("codTitoloOnorifico");

    try {

      //Date
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


      dtoRicerca.setIdInterno(idInterno);
      //dtoRicerca.setDataRiferimento(value)

      //Dati Anagrafici
      wsdtoPersonaFisica.setCodAnagrafico(codEsterno);
      wsdtoPersonaFisica.setCognome(cognome);
      wsdtoPersonaFisica.setNome(nome);
      if(dataNascita!= null){
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dataNascita);
        XMLGregorianCalendar xmlgcDataNascita = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        wsdtoPersonaFisica.setDataNascita(xmlgcDataNascita);
      }
      wsdtoPersonaFisica.setGenere(genere);
      wsdtoPersonaFisica.setCodComuneNascita(codComuneNascita);
      wsdtoPersonaFisica.setNote(annotazioni);

      //Dati fiscali
      if(codNazioneSede != null){//se ci sono indirizzi
        if(!"IT".equals(codNazioneSede)){
          wsdtoPersonaFisica.setCodiceFiscaleEstero(codiceFiscale);
          wsdtoPersonaFisica.setPartitaIvaEstero(partitaIva);
          wsdtoPersonaFisica.setCapStranieraResidenza(capSede);
          wsdtoPersonaFisica.setCapStranieroDomFiscale(capDomFiscale);
          wsdtoPersonaFisica.setDescrCittaStranieraResidenza(localitaPrincipale);
          wsdtoPersonaFisica.setDescrCittaStranieraDomFiscale(localitaPrincipale);
        }else{
          wsdtoPersonaFisica.setCodiceFiscale(codiceFiscale);
          wsdtoPersonaFisica.setPartitaIva(partitaIva);
          wsdtoPersonaFisica.setCapResidenza(capSede);
          wsdtoPersonaFisica.setCapDomFiscale(capDomFiscale);
          wsdtoPersonaFisica.setCodNazioneNascita("IT");
        }

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(UtilityDate.getDataOdiernaAsDate());
        wsdtoPersonaFisica.setDataInizioResidenza(dOdierna);
        wsdtoPersonaFisica.setDataInizioDomFiscale(dOdierna);
        wsdtoPersonaFisica.setDataFineResidenza(dFutura);
        wsdtoPersonaFisica.setDataFineDomFiscale(dFutura);

      }

      wsdtoPersonaFisica.setCodNazioneResidenza(codNazioneSede);
      wsdtoPersonaFisica.setCodNazioneFiscale(codNazioneDomFiscale);
      wsdtoPersonaFisica.setCivicoResidenza(civicoSede);
      wsdtoPersonaFisica.setCivicoDomFiscale(civicoDomFiscale);
      wsdtoPersonaFisica.setIndirizzoResidenza(indirizzoSede);
      wsdtoPersonaFisica.setIndirizzoDomFiscale(indirizzoDomFiscale);
      wsdtoPersonaFisica.setCodComuneResidenza(codComuneSede);
      wsdtoPersonaFisica.setCodComuneDomFiscale(codComuneDomFiscale);

      //Dati sui contatti
      wsdtoPersonaFisica.setTelDomicilio(telUfficio);
      wsdtoPersonaFisica.setCellPersonale(cellUfficio);
      pecUfficio = UtilityStringhe.convertiNullInStringaVuota(pecUfficio);
      if(!"".equals(pecUfficio)){
        wsdtoPersonaFisica.setPostaElettronicaPrivata(pecUfficio);
      }else{
        wsdtoPersonaFisica.setPostaElettronicaPrivata(emailUfficio);
      }
      wsdtoPersonaFisica.setFax(faxUfficio);
      wsdtoPersonaFisica.setUrlSitoWeb(urlSitoWeb);
      wsdtoPersonaFisica.setCodOnorifico(codTitoloOnorifico);
      wsdtoPersonaFisica.setClient(client);

      WSACPersonaFisicaService ws_pf = this.getWSPersonaFisica(SERVIZIO_PERSONAFISICA);
      Boolean agganciaUtente = false;

      ws_pf.modificaPersona(dtoRicerca, wsdtoPersonaFisica, agganciaUtente, "PF");

      /* Ordini professionali sospesi
        try{

          List<WsdtoOrdineProfessionale> elencoOrdiniProf = ws_pf.elencaOrdiniProf(dtoRicerca);
          if(elencoOrdiniProf != null){
            ccDedicato = UtilityStringhe.convertiNullInStringaVuota(ccDedicato);
            boolean founded = false;
            for (int k = 0; k < elencoOrdiniProf.size(); k++) {
              WsdtoOrdineProfessionale ordineProf = elencoOrdiniProf.get(k);
              String tipoOrdineProf = ordineProf.getCodTipoOrdProf();
              tipoOrdineProf = UtilityStringhe.convertiNullInStringaVuota(tipoOrdineProf);

              if(codAlboProf.equals(tipoOrdineProf)){
                founded = true;
              }
            }
            if(!founded){
              if(!"".equals(codAlboProf)){
                WsdtoOrdineProfessionale ordineProfessionale = new WsdtoOrdineProfessionale();
                ordineProfessionale.setCodTipoOrdProf(codAlboProf);
                ordineProfessionale.setNumeroAlbo(numIscrAlboProf);
                GregorianCalendar gcdia = new GregorianCalendar();
                gcdia.setTime(dataIscrAlboProf);
                XMLGregorianCalendar xmlgcDataIscrAlboProf = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcdia);
                ordineProfessionale.setDataIstrizioneAlbo(xmlgcDataIscrAlboProf);
                //VERIFICARE IL COD da pasare
                //ordineProfessionale.setCodProv(provAlboProf);
                ws_pf.inserisciOrdineProf(dtoRicerca, ordineProfessionale);
              }
            }

          }else{
            WsdtoOrdineProfessionale ordineProfessionale = new WsdtoOrdineProfessionale();
            ordineProfessionale.setCodTipoOrdProf(codTitoloOnorifico);
            ws_pf.inserisciOrdineProf(dtoRicerca, ordineProfessionale);
          }

        } catch (Throwable t) {
          UtilityStruts.addMessage(request, "warning",
              "warnings.cineca.mancataIntegrazioneOrdProf.warning",
              new Object[] {t.getMessage()});
        }

        */

        try {

          //MODIFICA E/O eventuale INSERIMENTO coordinate di pagamento
          Boolean prioritaMassima = true;
          dtoRicerca.setIdInterno(idInterno);
          String matricola = null;
          List<WsdtoPagamento> wsdtoPagamenti = ws_pf.elencaCoordPagamento(dtoRicerca, matricola);
          ccDedicato = UtilityStringhe.convertiNullInStringaVuota(ccDedicato);
          bic = UtilityStringhe.convertiNullInStringaVuota(bic);
          if(!"".equals(ccDedicato) && ccDedicato.length() >= 27){
            ccDedicato = ccDedicato.replace(" ", "");

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
                  String coord_codExt = wsdtoPagamento.getCodEsternoCoordPag();
                  //qui andrà discriminato per Nazione
                    ccDedicato = ccDedicato.replace(" ", "");
                  //if(("CC".equals(coord_codmod) && "IT".equals(coord_codnaz)) || ("BU".equals(coord_codmod) && !"IT".equals(coord_codnaz))){
                    if(!ccDedicato.equals(coord_iban)){
                      founded = true;
                      WsdtoPagamento coordPag = new WsdtoPagamento();
                      //es IT 88 A 01234 56789 012345678901
                      if("CC".equals(coord_codmod) && "IT".equals(coord_codnaz)){
                        String cin = ccDedicato.substring(4,5);
                        String abi = ccDedicato.substring(5,10);
                        String cab = ccDedicato.substring(10,15);
                        String numConto = ccDedicato.substring(15);
                        coordPag.setCin(cin);
                        coordPag.setAbi(abi);
                        coordPag.setCab(cab);
                        coordPag.setNumeroConto(numConto);
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
                      String userAlias = null;
                      String codEsse3 = null;
                      String username = null;
                      //vado in modifica
                      ws_pf.modificaCoordPagamento(idInterno, codEsterno, null, matricola, codEsse3, codiceFiscale, null, username, userAlias, coord_id, null, null, coordPag, prioritaMassima, "PF");
                    }//if modificato iban

                  //}//if identifico
                }//for

                if(!founded){
                  WsdtoPagamento coordPag = new WsdtoPagamento();
                  String codMod = null;
                  String codNazione = null;
                  if(codNazioneDomFiscale != null){
                    if(!"IT".equals(codNazioneDomFiscale)){
                      codMod = "BU"; //bonifici esteri con IBAN
                      codNazione = codNazioneDomFiscale; //bonifici esteri
                      if(!"".equals(bic)){
                        coordPag.setBic(bic);
                      }
                    }else{
                      codMod = "CC"; //bonifici nazionali con IBAN
                      codNazione = "IT"; //bonifici nazionali ITALIA
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
                  coordPag.setCodNazione(codNazione);
                  coordPag.setIntestazioneConto(ragioneSociale);
                  coordPag.setDataInizio(dOdierna);
                  coordPag.setDataFine(dFutura);
                  String username = null;
                  String codEsse3 = null;
                  String userAlias = null;
                  ws_pf.inserisciCoordPagamento(idInterno, codEsterno, null, matricola, codEsse3, codiceFiscale, null, username, userAlias, coordPag, prioritaMassima, "PF");
                }

            }else{//else coord pag
                WsdtoPagamento coordPag = new WsdtoPagamento();
                String codMod = null;
                String codNazione = null;
                if(codNazioneDomFiscale != null){
                  if(!"IT".equals(codNazioneDomFiscale)){
                    codMod = "BU"; //bonifici esteri con IBAN
                    codNazione = codNazioneDomFiscale; //bonifici esteri
                    if(!"".equals(bic)){
                      coordPag.setBic(bic);
                    }
                  }else{
                    codMod = "CC"; //bonifici nazionali con IBAN
                    codNazione = "IT"; //bonifici nazionali ITALIA
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
                coordPag.setCodNazione(codNazione);
                coordPag.setIntestazioneConto(ragioneSociale);
                coordPag.setDataInizio(dOdierna);
                coordPag.setDataFine(dFutura);
                String username = null;
                String codEsse3 = null;
                String userAlias = null;
                ws_pf.inserisciCoordPagamento(idInterno, codEsterno, null, matricola, codEsse3, codiceFiscale, null, username, userAlias, coordPag, prioritaMassima, "PF");
            }//if coord pag

          }

        } catch (Throwable t) {
          UtilityStruts.addMessage(request, "warning",
              "warnings.cineca.mancataIntegrazioneCoordPag.warning",
              new Object[] {t.getMessage()});
        }



    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la modifica della persona fisica: " + t.getMessage(),
          "cineca.personaFisica.remote.error",new Object[] {t.getMessage()}, t);
    }

    return wsdtoPersonaFisicaResponce;
  }



  /**
   * Modifica una ditta individuale
   *
   * @param HashMap
   * @return WsDittaModificaResponse
   * @throws GestoreException
   */
  public String[] wsCinecaModProMastPersonaFisica(Long idInterno, String codAnagrafico)
    throws GestoreException {

    String[] resMsg = new String[2];

    WsdtoPersonaFisica wsdtoPersonaFisica = new WsdtoPersonaFisica();
    WsdtoPersonaFisicaSearch dtoRicerca= new WsdtoPersonaFisicaSearch();

    try {

      WSACPersonaFisicaService ws_pf = this.getWSPersonaFisica(SERVIZIO_PERSONAFISICA);
      //String client = ConfigManager.getValore(PROP_WSPERSONAFISICA_CLIENT);
      Boolean agganciaUtente = true;
      dtoRicerca.setIdInterno(idInterno);
      //qui occorre fare questa conversione per l'annullamento
      codAnagrafico = UtilityStringhe.convertiNullInStringaVuota(codAnagrafico);
      wsdtoPersonaFisica.setCodAnagrafico(codAnagrafico);
      ws_pf.modificaPersona(dtoRicerca, wsdtoPersonaFisica, agganciaUtente, "PF");

    } catch (Throwable t) {
      resMsg[0] = "-7";
      resMsg[1] = t.getMessage();
    }

    return resMsg;
  }









  /**
   * Estrae una persona fisica (ITALIA)
   *
   * @param codiceFiscale
   * @return WsDittaEstraiResponseResponse
   * @throws GestoreException
   */
  public WsdtoPersonaFisicaResponce wsCinecaEstraiPersonaFisica(String cf, String piva, Long nazione)
    throws GestoreException {

    WsdtoPersonaFisicaResponce wsdtoPersonaFisicaResponce = new WsdtoPersonaFisicaResponce();
    WsdtoPersonaFisicaSearch wsdtoPersonaFisicaSearch = new WsdtoPersonaFisicaSearch();

    try {

      Calendar cal = Calendar.getInstance();
      cal.setTime(UtilityDate.getDataOdiernaAsDate());

      GregorianCalendar gc = new GregorianCalendar();
      gc.setTime(UtilityDate.getDataOdiernaAsDate());
      XMLGregorianCalendar xmlgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

      wsdtoPersonaFisicaSearch.setDataRiferimento(xmlgc);

      cf = UtilityStringhe.convertiNullInStringaVuota(cf);


      WSACPersonaFisicaService ws_pf = this.getWSPersonaFisica(SERVIZIO_PERSONAFISICA);
      String client = ConfigManager.getValore(PROP_WSPERSONAFISICA_CLIENT);


      if(nazione != null && !new Long(1).equals(nazione)){
        //per ora e' cosi ma mi sarei aspettato il set per il cf straniero
        wsdtoPersonaFisicaSearch.setCodiceFiscale(cf);
        ws_pf.estraiPersona(wsdtoPersonaFisicaSearch);
      }else{
        if(!"".equals(cf)){
          wsdtoPersonaFisicaSearch.setCodiceFiscale(cf);
          wsdtoPersonaFisicaResponce = ws_pf.estraiPersona(wsdtoPersonaFisicaSearch);
        }
      }

    } catch (Throwable t) {
      String ecpt = t.getMessage();
      if(!ecpt.contains("ERR-100")){
        throw new GestoreException("Si e' verificato un errore durante l'estrazione della persona fisica: " + t.getMessage(),
            "cineca.personafisica.remote.error", new Object[] {t.getMessage()}, t);
      }

    }


    return wsdtoPersonaFisicaResponce;
  }

  /**
   * Restituisce un puntatore al servizio WS Persone Fisiche.
   *
   * @param username
   * @param password
   * @param servizio
   * @return
   * @throws GestoreException
   * @throws ServiceException
   * @throws MalformedURLException
   */
  private WSACPersonaFisicaService getWSPersonaFisica(String servizio)
    throws GestoreException, RemoteException, ServiceException, MalformedURLException {

    String url = null;
    if (SERVIZIO_PERSONAFISICA.equals(servizio)) {
      url = ConfigManager.getValore(PROP_WSPERSONAFISICA_URL);
      if (url == null || "".equals(url)) {
        throw new GestoreException("L'indirizzo per la connessione al servizio delle persone fisiche",
            "ws.personefisiche.url.error");
      }
    }

    String[] credenziali = this.cinecaAnagraficaComuneManager.getWSLogin(new Long(50), "CINECA"); //...

    String username = credenziali[0];
    String password = credenziali[1];

    URL dynUrl =new URL(url);
    Ws_002fPrivate_002fPersonaFisica ws_pf =  new Ws_002fPrivate_002fPersonaFisica(dynUrl);

    WSACPersonaFisicaService ws_PersonaFisica = ws_pf.getWSACPersonaFisicaServicePort();

    BindingProvider provider = (BindingProvider) ws_PersonaFisica;
    provider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
    provider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
    provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,url);


    return ws_PersonaFisica;

  }


}
