/*
 * Created on 02/nov/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.integrazioni.CinecaAnagraficaComuneManager;
import it.eldasoft.gene.bl.integrazioni.CinecaWSManager;
import it.eldasoft.gene.bl.integrazioni.CinecaWSPersoneFisicheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.tags.bl.AnagraficaManager;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.www.PortaleAlice.EsitoOutType;
import it.eldasoft.www.PortaleAlice.PortaleAliceProxy;
import it.maggioli.eldasoft.ws.erp.WSERPUgovAnagraficaType;
import it.maggioli.eldasoft.ws.erp.WSERPUgovResType;
import it.maggioli.eldasoft.ws.erp.WSERP_PortType;

public class GestoreIMPR extends AbstractGestoreEntita {

  private AnagraficaManager  anagraficaManager;

  private CinecaWSManager cinecaWSManager;

  private CinecaWSPersoneFisicheManager cinecaWSPersoneFisicheManager;

  private CinecaAnagraficaComuneManager cinecaAnagraficaComuneManager;


  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    anagraficaManager = (AnagraficaManager) UtilitySpring.getBean("anagraficaManager",
        this.getServletContext(), AnagraficaManager.class);
    String integrazioneCineca = ConfigManager.getValore("integrazioneAnagraficheUGOV");
    integrazioneCineca = UtilityStringhe.convertiNullInStringaVuota(integrazioneCineca);
    if("1".equals(integrazioneCineca)){
      cinecaWSManager = (CinecaWSManager) UtilitySpring.getBean("cinecaWSManager",
          this.getServletContext(), CinecaWSManager.class);
      cinecaWSPersoneFisicheManager = (CinecaWSPersoneFisicheManager) UtilitySpring.getBean("cinecaWSPersoneFisicheManager",
          this.getServletContext(), CinecaWSPersoneFisicheManager.class);
      cinecaAnagraficaComuneManager = (CinecaAnagraficaComuneManager) UtilitySpring.getBean("cinecaAnagraficaComuneManager",
          this.getServletContext(), CinecaAnagraficaComuneManager.class);
    }
  }

  @Override
  public String getEntita() {
    return "IMPR";
  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }

	@Override
	public void preDelete(TransactionStatus status, DataColumnContainer impl)
					throws GestoreException {

		//Prima di procedere con l'eliminazione si deve controllare
		//che non sia collegato a qualche entita
		GeneManager gene = this.getGeneManager();
		String codiceImpresa = impl.getString("IMPR.CODIMP");
		gene.checkConstraints("IMPR", new String[]{codiceImpresa});

		//si deve controllare che la ditta non faccia parte di un raggruppamento
		try {
          Long occorrenze = (Long)this.sqlManager.getObject("select count(codime9) from ragimp where coddic=?", new Object[]{codiceImpresa});
          if(occorrenze != null && occorrenze.longValue()>0){
            String msg = "Impossibile eliminare l'occorrenza (" + codiceImpresa + ") dell'entita' "
                + "'Anagrafico delle Imprese' in quanto viene utilizzata in almeno un record nell'entità 'Lista delle imprese che compongono un raggruppamento'";
            Exception e = null;
            throw new GestoreException(msg, "eliminazioneIstanza",
                new Object[]{codiceImpresa, "'Anagrafico delle Imprese'","'Lista delle imprese che compongono un raggruppamento'"}, e);
          }
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nel controllo dell'esisenza dell'impresa " + codiceImpresa +  " in RAGIMP",null, e);
        }

		String toDel[] = new String[]{"impdte", "codimp3", "impleg", "codimp2",
			"impind", "codimp5", "impazi", "codimp4", "impope", "codimp", "cate",
			"codimp1", "ragimp", "codime9", "impcase", "codimp", "impantimafia", "codimp",
			"g_impcol", "codimp", "impanno", "codimp"};
		Object params[] = new Object[]{codiceImpresa};
		// Esecuzione dell'eliminazione di tutte le tabelle collegate
		for (int i = 0; (i + 1) < toDel.length; i += 2) {
			gene.deleteTabelle(new String[]{toDel[i]}, toDel[i + 1] + " = ?", params);
		}

    //Se è abilitato OP114 si deve cancellare l'occorrenza in W_PUSER
    //e si deve eliminare la registrazione al portale alice.
   if(GeneManager.checkOP(this.getServletContext(), CostantiGenerali.OPZIONE_GESTIONE_PORTALE)){
     String select ="select USERNOME from W_PUSER where USERENT = ? and USERKEY1 = ?";
     String userent = "IMPR";
     try {
      String usernome = (String)this.getSqlManager().getObject(select, new Object[]{userent, codiceImpresa});
      if(usernome!=null && !"".equals(usernome)){

        Long count = (Long) this.getSqlManager().getObject("select count(*) from w_invcom where IDPRG = ? and COMKEY1 = ? and COMSTATO = ?", new Object[]{"PA",usernome,5});
        if(count > 0){
          String msg = "Impossibile eliminare l'impresa in quanto sono presenti offerte non ancora acquisite";
          Exception e = null;
          throw new GestoreException(msg, "eliminazioneImpresa", e);
        }
        //Eliminazione da W_PUSER
        this.getSqlManager().update(
            "delete from W_PUSER where USERENT = ? and USERKEY1 = ?",
            new Object[] { userent, codiceImpresa});


        //Eliminazione dal portale

        //Chiamata al servizio
        PortaleAliceProxy proxy = new PortaleAliceProxy();
        //indirizzo del servizio letto da properties
        String endPoint = ConfigManager.getValore(CostantiGenerali.PROP_URL_WEB_SERVICE_PORTALE_ALICE);
        proxy.setEndpoint(endPoint);
        EsitoOutType risultato = proxy.eliminaImpresa(usernome);
        if(!risultato.isEsitoOk()){
          String codiceErrore = risultato.getCodiceErrore();
          String codiceMessaggio = "eliminazioneImpresaRegistrata";
          String datiMessaggio = "";
          if (codiceErrore!=null && !"".equals(codiceErrore)){
            if(codiceErrore.indexOf("UNEXP-ERR")>=0){
              String messaggio = codiceErrore.substring(codiceErrore.indexOf(" "));
              datiMessaggio = messaggio;
            }
            if(codiceErrore.indexOf("UNKNOWN-USER")>=0){
              datiMessaggio = "L'user " + usernome + " non risulta definito";
            }

          }
          SQLException e=null;
          throw new GestoreException("Errore durante l'eliminazione dell'impresa dal portale alice",codiceMessaggio,new Object[]{datiMessaggio},e);
        }

      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'eliminazione dell'impresa dal portale alice",null, e);
    } catch (RemoteException e) {
      throw new GestoreException(
          "Errore durante l'eliminazione dell'impresa dal portale alice ",null, e);
    }
   }

   String integrazioneCineca = ConfigManager.getValore("integrazioneAnagraficheUGOV");
   integrazioneCineca = UtilityStringhe.convertiNullInStringaVuota(integrazioneCineca);

   if("1".equals(integrazioneCineca)){

    try {
    	WSERP_PortType wserp = cinecaAnagraficaComuneManager.getWSERP("WSERP");
        String[] credenziali = cinecaAnagraficaComuneManager.getWSLogin(new Long(50), "CINECA");
        String username = credenziali[0];
        String password = credenziali[1];

      Vector<?> naturaImprVect = this.sqlManager.getVector("select TIPIMP,NATGIUI from IMPR where CODIMP = ?", new Object[] { codiceImpresa });
      Long tipimp= null;
      Long natgiui = null;
      if (naturaImprVect != null && naturaImprVect.size() > 0) {
        tipimp = (Long) SqlManager.getValueFromVectorParam(naturaImprVect, 0).getValue();
        natgiui = (Long) SqlManager.getValueFromVectorParam(naturaImprVect, 1).getValue();
      }
      String[] resMsg = null;

      if(tipimp != null && new Long(6).equals(tipimp)){

        if(new Long(10).equals(natgiui)){
          resMsg = cinecaWSPersoneFisicheManager.getCinecaPersonaFisica(this.getRequest(),codiceImpresa);
          if( new Integer(resMsg[0]) > 0 ){
            String idInternoStr = resMsg[2];
            Long idInterno = null;
            if(idInternoStr != null){//dovrebbe esserlo sempre in questo caso
              idInterno = new Long(idInternoStr);
            }
            if("2".equals(resMsg[0])){
              //de-masterizzo (per la persona fisica e' una modifica):
				try {
		           	WSERPUgovAnagraficaType anagrafica = new WSERPUgovAnagraficaType();
	               	anagrafica.setIdInterno(idInterno);
		            WSERPUgovResType resPF = wserp.WSERPPersonaFisica(username,password, "MASTERIZZA", anagrafica);
		            if(!resPF.isEsito()) {
	                    UtilityStruts.addMessage(this.getRequest(), "warning",
	                            "warnings.cineca.mancataIntegrazione",
	                            new Object[] { codiceImpresa, resPF.getMessaggio()});
		            }

				} catch (RemoteException e) {
				      throw new GestoreException( "Si e' verificato un errore durante la demasterizzazione della persona fisica ",null, e);
				}
            }
          }

        }else{
          resMsg = cinecaWSManager.getCinecaDittaIndividuale(codiceImpresa);
          if( new Integer(resMsg[0]) > 0 ){
            String idInternoStr = resMsg[2];
            Long idInterno = null;
            if(idInternoStr != null){//dovrebbe esserlo sempre in questo caso
              idInterno = new Long(idInternoStr);
            }
            if("2".equals(resMsg[0])){
              //de-masterizzo (per la ditta individuale e' una modifica):
				try {
		           	WSERPUgovAnagraficaType anagrafica = new WSERPUgovAnagraficaType();
	               	anagrafica.setIdInterno(idInterno);
		            WSERPUgovResType resDI = wserp.WSERPDittaIndividuale(username,password, "MASTERIZZA", anagrafica);
		            if(!resDI.isEsito()) {
	                    UtilityStruts.addMessage(this.getRequest(), "warning",
	                            "warnings.cineca.mancataIntegrazione",
	                            new Object[] { codiceImpresa, resDI.getMessaggio()});
		            }

				} catch (RemoteException e) {
				      throw new GestoreException( "Si e' verificato un errore durante la demasterizzazione della ditta individuale ",null, e);
				}
            }
          }
        }
      }else{
        //verifico la presenza del soggetto collettivo
        resMsg = cinecaWSManager.getCinecaSoggettoCollettivo(codiceImpresa);
        if( new Integer(resMsg[0]) > 0 ){
          String idInternoStr = resMsg[2];
          Long idInterno = null;
          if(idInternoStr != null){//dovrebbe esserlo sempre in questo caso
            idInterno = new Long(idInternoStr);
          }
          HashMap<String, Object> soggettoCollettivo = cinecaWSManager.getDatiSoggettoCollettivo(codiceImpresa);
          if("2".equals(resMsg[0])){
            //de-masterizzo: si usa il masterizza
				try {
		           	WSERPUgovAnagraficaType anagrafica = new WSERPUgovAnagraficaType();
	               	anagrafica.setIdInterno(idInterno);
		            WSERPUgovResType resSC = wserp.WSERPSoggettoCollettivo(username,password, "MASTERIZZA", anagrafica);
		            if(!resSC.isEsito()) {
	                    UtilityStruts.addMessage(this.getRequest(), "warning",
	                            "warnings.cineca.mancataIntegrazione",
	                            new Object[] { codiceImpresa, resSC.getMessaggio()});
		            }
				} catch (RemoteException e) {
				      throw new GestoreException( "Si e' verificato un errore durante la demasterizzazione del soggetto collettivo ",null, e);
				}
          }
        }
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nel controllo della tipologia dell'impresa " + codiceImpresa,null, e);
    }
   }

     LogEvento logevento = LogEventiUtils.createLogEvento(this.getRequest());
     logevento.setLivEvento(1);
     logevento.setOggEvento(codiceImpresa);
     logevento.setCodEvento("DELETE_IMPR");
     logevento.setDescr("Eliminazione impresa da anagrafica");
     logevento.setErrmsg("");
     LogEventiUtils.insertLogEventi(logevento);

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    GeneManager gene = this.getGeneManager();

    // Se si ha la codifica automatica allora eseguo il ricalcolo
    if (gene.isCodificaAutomatica("IMPR", "CODIMP")) {
      // Setto il codice impresa come chiave altrimenti non ritorna sulla riga
      // giusta
      impl.getColumn("IMPR.CODIMP").setChiave(true);
      impl.setValue("IMPR.CODIMP", gene.calcolaCodificaAutomatica("IMPR",
          "CODIMP"));
    }
    String  isModificaDatiRegistrati = UtilityStruts.getParametroString(this.getRequest(),"MOD_DATI_REG");
    this.verificaCodiceFiscalePartitaIVA(impl,isModificaDatiRegistrati);

    // Gestione standard della sezione dinamica altri indirizzi
    // GestoreIMPIND.gestisciEntitaDaImpr(this.getRequest(),status, impl);
    AbstractGestoreChiaveNumerica GestoreIMPIND = new DefaultGestoreEntitaChiaveNumerica(
        "IMPIND", "INDCON", new String[] { "CODIMP5" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, impl, GestoreIMPIND,
        "AIN", new DataColumn[] { impl.getColumn("IMPR.CODIMP") }, null);

    this.updatePersonale(impl,"INS");

    //Promemoria per raggruppamenti e consorzi
    Long tipimp = impl.getLong("IMPR.TIPIMP");
    if(tipimp!= null && (tipimp.longValue()==2 || tipimp.longValue() == 3 || tipimp.longValue() == 10 || tipimp.longValue() == 11)){
      String msg="";
      String msg1="";
      if(tipimp.longValue()==2 || tipimp.longValue() == 11){
        msg="consorzio";
        msg1="Consorziate";
      }else{
        msg="raggruppamento temporaneo";
        msg1="Raggruppamento";
      }
      UtilityStruts.addMessage(this.getRequest(), "warning",
          "warnings.imprese.componentiRaggruppamento",new Object[]{msg,msg1});
    }
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    GeneManager gene = this.getGeneManager();
    String codimp = impl.getString("IMPR.CODIMP");
    impl.getColumn("IMPR.CODIMP").setChiave(true);



    // Aggiornamento delle intestazioni degli archivi in DB
    if (impl.isModifiedColumn("IMPR.NOMIMP")) {
      // Se è modificata l'intestazione chiamo la funzione d'aggiornamento
      // dell'intestazione in database
      gene.aggiornaIntestazioniInDB("IMPR", impl.getString("IMPR.NOMIMP"),
          new Object[] { codimp });
    }

    String  isModificaDatiRegistrati = UtilityStruts.getParametroString(this.getRequest(),"MOD_DATI_REG");
    this.verificaCodiceFiscalePartitaIVA(impl,isModificaDatiRegistrati);

    if(GeneManager.checkOP(this.getServletContext(), CostantiGenerali.OPZIONE_GESTIONE_PORTALE) && "true".equals(isModificaDatiRegistrati)){
      //Controllo unicità dell'indirizzo mail dell'impresa registrata
      //this.controlloUnicitaMailRegistrazione(codimp, impl);
      this.allineamentoDatiPortale(codimp, impl);
    }

    // Gestione standard della sezione dinamica altri indirizzi
    // GestoreIMPIND.gestisciEntitaDaImpr(this.getRequest(),status, impl);
    AbstractGestoreChiaveNumerica GestoreIMPIND = new DefaultGestoreEntitaChiaveNumerica(
        "IMPIND", "INDCON", new String[] { "CODIMP5" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, impl, GestoreIMPIND,
        "AIN", new DataColumn[] { impl.getColumn("IMPR.CODIMP") }, null);

    this.updatePersonale(impl,"UPD");

    //Controlli nel caso di raggruppamento
    Long tipimp = impl.getLong("IMPR.TIPIMP");
    Long natgiui = impl.getLong("IMPR.NATGIUI");
    if(tipimp!= null && (tipimp.longValue() == 3 || tipimp.longValue() == 10)){
      try {
        boolean controlloPartecipantiSuperato = this.anagraficaManager.controlloPresentaComponentiRaggruppamento(codimp);
        if(!controlloPartecipantiSuperato){
          String msg="raggruppamento temporaneo";
          String msg1="Raggruppamento";
          UtilityStruts.addMessage(this.getRequest(), "warning",
              "warnings.imprese.componentiRaggruppamento",new Object[]{msg,msg1});
        }

      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella lettura della tabella RAGIMP",null, e);
      }
    }

    String integrazioneCineca = ConfigManager.getValore("integrazioneAnagraficheUGOV");
    integrazioneCineca = UtilityStringhe.convertiNullInStringaVuota(integrazioneCineca);

    String integrazioneWSERP = ConfigManager.getValore("wserp.erp.url");
    integrazioneWSERP = UtilityStringhe.convertiNullInStringaVuota(integrazioneWSERP);

    if("1".equals(integrazioneCineca)){
      WSERP_PortType wserp = cinecaAnagraficaComuneManager.getWSERP("WSERP");
      String[] credenziali = cinecaWSManager.getWSLogin(new Long(50), "CINECA");
      String username = credenziali[0];
      String password = credenziali[1];
      String[] res = null;
      if(new Long(6).equals(tipimp)){
        if(new Long(10).equals(natgiui)){
          res = cinecaWSPersoneFisicheManager.getCinecaPersonaFisica(this.getRequest(),codimp);
        }else{
          res = cinecaWSManager.getCinecaDittaIndividuale(codimp);
        }
      }else{
        //verifico la presenza del soggetto collettivo
        res = cinecaWSManager.getCinecaSoggettoCollettivo(codimp);
      }
      if(res[0] != null && new Integer(res[0]) > 0 ){
        String idInternoStr = res[2];
        Long idInterno = null;
        if(idInternoStr != null){//dovrebbe esserlo sempre in questo caso
          idInterno = new Long(idInternoStr);
        }
        HashMap<String, Object> soggettoCollettivo = cinecaWSManager.getDatiSoggettoCollettivo(codimp);


        String codEsterno = (String) soggettoCollettivo.get("codEsterno");

        //devo popolare il soggetto  con i dati appena modificati
        Boolean modificaPrincipale = false;
        soggettoCollettivo.put("idInterno", idInterno);
        if (impl.isModifiedColumn("IMPR.PIVIMP")) {
          soggettoCollettivo.put("partitaIva", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.PIVIMP")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.CFIMP")) {
          soggettoCollettivo.put("codiceFiscale", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.CFIMP")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.NOMEST")) {
          soggettoCollettivo.put("ragioneSociale", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.NOMEST")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.NATGIUI")) {
          Long formaGiuridica = impl.getLong("IMPR.NATGIUI");
          if(formaGiuridica != null){
            String codFormaGiuridica;
            try {
              codFormaGiuridica = (String) this.sqlManager.getObject("select tab2d1 from tab2 where tab2cod= ? and tab2tip =? ",
                  new Object[]{"UBUY2",formaGiuridica.toString()});
              codFormaGiuridica = UtilityStringhe.convertiNullInStringaVuota(codFormaGiuridica);
              soggettoCollettivo.put("codFormaGiuridica", codFormaGiuridica);
              soggettoCollettivo.put("formaGiuridica", formaGiuridica.toString());
            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore durante la determinazione della tipologia dell'impresa",null, e);
            }
          }else{
            soggettoCollettivo.put("codFormaGiuridica", null);
            soggettoCollettivo.put("formaGiuridica", null);
          }
          //se trattasi di studio associato o studio di professionisti, va dettagliato su tipologia impresa
          if(new Long(7).equals(formaGiuridica)){
            Long tipologiaImpresa = impl.getLong("IMPR.TIPIMP");
            if(tipologiaImpresa != null){
              soggettoCollettivo.put("tipologiaImpresa", tipologiaImpresa);
            }else{
              soggettoCollettivo.put("tipologiaImpresa", null);
            }
          }
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.INDIMP")) {
          soggettoCollettivo.put("indirizzoSede", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.INDIMP")));
          soggettoCollettivo.put("indirizzoDomFiscale", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.INDIMP")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.NCIIMP")) {
          soggettoCollettivo.put("civicoSede", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.NCIIMP")));
          soggettoCollettivo.put("civicoDomFiscale", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.NCIIMP")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.CAPIMP")) {
          soggettoCollettivo.put("capSede", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.CAPIMP")));
          soggettoCollettivo.put("capDomFiscale", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.CAPIMP")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.COORBA")) {
          soggettoCollettivo.put("ccDedicato", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.COORBA")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.CODBIC")) {
          soggettoCollettivo.put("bic", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.CODBIC")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.SOGMOV")) {
          soggettoCollettivo.put("soggettiAbilitati", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.SOGMOV")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.PROIMP")) {
          soggettoCollettivo.put("provinciaSede", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.PROIMP")));
          soggettoCollettivo.put("provinciaDomFiscale", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.PROIMP")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.NAZIMP")) {
          Long nazionePrincipale = impl.getLong("IMPR.NAZIMP");
          if(nazionePrincipale != null){
            try {
              String codNazioneSede = (String) this.sqlManager.getObject("select tab2tip from tab2 where tab2cod= ? and tab2d1 =? ",
                  new Object[]{"UBUY1",nazionePrincipale.toString()});
              codNazioneSede = UtilityStringhe.convertiNullInStringaVuota(codNazioneSede);
              soggettoCollettivo.put("codNazioneSede", UtilityStringhe.convertiNullInStringaVuota(codNazioneSede));
              soggettoCollettivo.put("codNazioneDomFiscale", UtilityStringhe.convertiNullInStringaVuota(codNazioneSede));
            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore durante la determinazione del codice Comune dell'impresa ",null, e);
            }
          }else{
            soggettoCollettivo.put("codNazioneSede", null);
            soggettoCollettivo.put("codNazioneDomFiscale", null);

          }
          modificaPrincipale = true;
        }

        if (impl.isModifiedColumn("IMPR.DCCIAA")) {
          soggettoCollettivo.put("dataIscrCCIAA",impl.getData("IMPR.DCCIAA"));
          modificaPrincipale = true;
        }

        if (impl.isModifiedColumn("IMPR.LOCIMP")) {
          String localitaPrincipale = UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.LOCIMP"));
          Long nazionePrincipale = impl.getLong("IMPR.NAZIMP");
       	  if(nazionePrincipale != null && !Long.valueOf(1).equals(nazionePrincipale)){
       		  //straniera
       		soggettoCollettivo.put("localitaPrincipale", localitaPrincipale);
       	  }else {
       		  //italiana
              if(!"".equals(localitaPrincipale)){

                  //prima la provo secca, altrimenti con il like
                  String codComuneSede;
                  try {
                    codComuneSede = (String) this.sqlManager.getObject("select tabcod5 from tabsche where tabcod='S2003' and tabcod1='09' and tabdesc = ? ", new Object[]{localitaPrincipale});
                    codComuneSede = UtilityStringhe.convertiNullInStringaVuota(codComuneSede);
                    if("".equals(codComuneSede)){
                      codComuneSede = (String) this.sqlManager.getObject("select tabcod5 from tabsche where tabcod='S2003' and tabcod1='09' and tabdesc like ? ", new Object[]{"%" + localitaPrincipale + "%"});
                    }
                    codComuneSede = UtilityStringhe.convertiNullInStringaVuota(codComuneSede);
                    if(!"".equals(codComuneSede)){
                      soggettoCollettivo.put("codComuneSede", UtilityStringhe.convertiNullInStringaVuota(codComuneSede));
                      soggettoCollettivo.put("codComuneDomFiscale", UtilityStringhe.convertiNullInStringaVuota(codComuneSede));
                    }
                  } catch (SQLException e) {
                    throw new GestoreException(
                        "Errore durante la determinazione del codice Comune dell'impresa ",null, e);
                  }
                }else{
                  soggettoCollettivo.put("codComuneSede", null);
                  soggettoCollettivo.put("codComuneDomFiscale", null);
                }
       	  }
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.FAXIMP")) {
          soggettoCollettivo.put("faxUfficio", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.FAXIMP")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.TELIMP")) {
          soggettoCollettivo.put("telUfficio", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.TELIMP")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.TELCEL")) {
          soggettoCollettivo.put("cellUfficio", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.TELCEL")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.EMAIIP")) {
          soggettoCollettivo.put("emailUfficio", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.EMAIIP")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.EMAI2IP")) {
          soggettoCollettivo.put("pecUfficio", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.EMAI2IP")));
          modificaPrincipale = true;
        }
        if (impl.isModifiedColumn("IMPR.INDWEB")) {
          soggettoCollettivo.put("urlSitoWeb", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.INDWEB")));
          modificaPrincipale = true;
        }
        //MODIFICA E/O eventuale INSERIMENTO coordinate di pagamento
        if (impl.isModifiedColumn("IMPR.COORBA")) {
          soggettoCollettivo.put("ccDedicato", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.COORBA")));
          modificaPrincipale = true;
        }
        if(new Long(6).equals(tipimp)){
          if (impl.isModifiedColumn("IMPR.EMAIIP")) {
            soggettoCollettivo.put("emailUfficio", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.EMAIIP")));
            modificaPrincipale = true;
          }
          if (impl.isModifiedColumn("IMPR.COGNOME")) {
            soggettoCollettivo.put("cognome", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.COGNOME")));
            modificaPrincipale = true;
          }
          if (impl.isModifiedColumn("IMPR.NOME")) {
            soggettoCollettivo.put("nome", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.NOME")));
            modificaPrincipale = true;
          }
          if (impl.isModifiedColumn("IMPR.SEXTEC")) {
            soggettoCollettivo.put("genere", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.SEXTEC")));
            modificaPrincipale = true;
          }
          if (impl.isModifiedColumn("IMPR.CNATEC")) {
            String localitaNascita = UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.CNATEC"));
            if(!"".equals(localitaNascita)){
              //prima la provo secca, altrimenti con il like
              String codComuneNascita;
              try {
                codComuneNascita = (String) this.sqlManager.getObject("select tabcod5 from tabsche where tabcod='S2003' and tabcod1='09' and tabdesc = ? ", new Object[]{localitaNascita});
                codComuneNascita = UtilityStringhe.convertiNullInStringaVuota(codComuneNascita);
                if("".equals(codComuneNascita)){
                  codComuneNascita = (String) this.sqlManager.getObject("select tabcod5 from tabsche where tabcod='S2003' and tabcod1='09' and tabdesc like ? ", new Object[]{"%" + localitaNascita + "%"});
                }
                codComuneNascita = UtilityStringhe.convertiNullInStringaVuota(codComuneNascita);
                if(!"".equals(codComuneNascita)){
                  soggettoCollettivo.put("codComuneNascita", UtilityStringhe.convertiNullInStringaVuota(codComuneNascita));
                }
              } catch (SQLException e) {
                throw new GestoreException(
                    "Errore durante la determinazione del codice Comune Nascita dell'impresa ",null, e);
              }
            }
            modificaPrincipale = true;
          }
          if (impl.isModifiedColumn("IMPR.DNATEC")) {
            soggettoCollettivo.put("dataNascita",impl.getData("IMPR.DNATEC"));
            modificaPrincipale = true;
          }

          if(new Long(10).equals(natgiui)){
            if (impl.isModifiedColumn("IMPR.INCTEC")) {
              Long incaricoPrincipale = impl.getLong("IMPR.INCTEC");
              if(incaricoPrincipale != null){
                try {
                  String codTitoloOnorifico = (String) this.sqlManager.getObject("select tab2d1 from tab2 where tab2cod= ? and tab2tip = ? ",
                      new Object[]{"UBUY3",incaricoPrincipale.toString()});
                  codTitoloOnorifico = UtilityStringhe.convertiNullInStringaVuota(codTitoloOnorifico);
                  if(!"".equals(codTitoloOnorifico)){
                    soggettoCollettivo.put("codTitoloOnorifico",codTitoloOnorifico);
                  }
                } catch (SQLException e) {
                  throw new GestoreException(
                      "Errore durante la determinazione del codice titolo onorifico",null, e);
                }
              }
              modificaPrincipale = true;
            }
            if (impl.isModifiedColumn("IMPR.ANNOTI")) {
              soggettoCollettivo.put("annoti", UtilityStringhe.convertiNullInStringaVuota(impl.getString("IMPR.ANNOTI")));
              modificaPrincipale = true;
            }

          }else{
            ;
          }

        }

        if(modificaPrincipale){
          String client = null;
          if(new Long(6).equals(tipimp)){
            if(!"1".equals(res[0])){//Va masterizzata
              client = ConfigManager.getValore("cineca.ws.SoggettoCollettivo.client");
            }
          }else{
            if("1".equals(res[0])){
              //masterizzo comunque anche in assenza dei dati obbligatori
              String[] ctrlDOres = null;
              ctrlDOres = cinecaAnagraficaComuneManager.getDatiObbligatoriAnagrafica("TRACC", null, soggettoCollettivo);
              if("true".equals(ctrlDOres[0])){
				try {
	                WSERPUgovAnagraficaType anagrafica = new WSERPUgovAnagraficaType();
	                anagrafica.setIdInterno(idInterno);
	                anagrafica.setCodEsterno(codEsterno);
	                WSERPUgovResType resSC = wserp.WSERPSoggettoCollettivo(username, password, "MASTERIZZA", anagrafica);
	                if(!resSC.isEsito()) {
		                   UtilityStruts.addMessage(this.getRequest(), "warning",
		                           "warnings.cineca.mancataIntegrazione",
		                           new Object[] {codimp,resSC.getMessaggio()});
	                  	 //comporre il messaggio
	                  	 //return retMsg;
                    }
				} catch (RemoteException re) {
			        throw new GestoreException("Si e' verificato un errore durante la modifica del soggetto collettivo: " + re.getMessage(),
			                "cineca.soggettoCollettivo.remote.error", new Object[] {re.getMessage()}, re);
				}
              }else{
                //retMsg[1] = ctrlDOres[1];
              }
            }
          }
          String[] ctrlDOres = null;
          ctrlDOres = cinecaAnagraficaComuneManager.getDatiObbligatoriAnagrafica("TRACC", null, soggettoCollettivo);
          if("true".equals(ctrlDOres[0])){
            if(new Long(6).equals(tipimp)){
              if(new Long(10).equals(natgiui)){
				try {
	                WSERPUgovAnagraficaType anagrafica = cinecaAnagraficaComuneManager.setAnagraficaUgov(soggettoCollettivo);
	                WSERPUgovResType resPF = wserp.WSERPPersonaFisica(username,password, "MODIFICA", anagrafica);
		     	       if(resPF != null && resPF.isEsito()) {
		  	        	 if(Long.valueOf(-5).equals(resPF.getStato())) {
			        		 UtilityStruts.addMessage(this.getRequest(), "warning",
						                "warnings.cineca.mancataIntegrazioneCoordPag.warning",
						                new Object[] {resPF.getMessaggio()});
		  	        	 }
		     	       }else {
		                   UtilityStruts.addMessage(this.getRequest(), "warning",
		                           "warnings.cineca.mancataIntegrazione",
		                           new Object[] {codimp,resPF.getMessaggio()});
		     	       }
				} catch (RemoteException re) {
			        throw new GestoreException("Si e' verificato un errore durante la modifica della persona fisica: " + re.getMessage(),
			                "cineca.personaFisica.remote.error", new Object[] {re.getMessage()}, re);
				}
              }else{

  				try {
	                WSERPUgovAnagraficaType anagrafica = cinecaAnagraficaComuneManager.setAnagraficaUgov(soggettoCollettivo);
	                WSERPUgovResType resDI = wserp.WSERPDittaIndividuale(username,password, "MODIFICA", anagrafica);
		     	       if(resDI != null && resDI.isEsito()) {
		  	        	 if(Long.valueOf(-5).equals(resDI.getStato())) {
			        		 UtilityStruts.addMessage(this.getRequest(), "warning",
						                "warnings.cineca.mancataIntegrazioneCoordPag.warning",
						                new Object[] {resDI.getMessaggio()});
		  	        	 }
		     	       }else {
		                   UtilityStruts.addMessage(this.getRequest(), "warning",
		                           "warnings.cineca.mancataIntegrazione",
		                           new Object[] {codimp,resDI.getMessaggio()});
		     	       }
				} catch (RemoteException re) {
			        throw new GestoreException("Si e' verificato un errore durante la modifica della ditta individuale: " + re.getMessage(),
			                "cineca.dittaIndividuale.remote.error", new Object[] {re.getMessage()}, re);
				}
              }

            }else{
				try {
	                WSERPUgovAnagraficaType anagrafica = cinecaAnagraficaComuneManager.setAnagraficaUgov(soggettoCollettivo);
	                WSERPUgovResType resSC = wserp.WSERPSoggettoCollettivo(username,password, "MODIFICA", anagrafica);
		     	       if(resSC != null && resSC.isEsito()) {
		  	        	 if(Long.valueOf(-5).equals(resSC.getStato())) {
			        		 UtilityStruts.addMessage(this.getRequest(), "warning",
						                "warnings.cineca.mancataIntegrazioneCoordPag.warning",
						                new Object[] {resSC.getMessaggio()});
		  	        	 }
		     	       }else {
		                   UtilityStruts.addMessage(this.getRequest(), "warning",
		                           "warnings.cineca.mancataIntegrazione",
		                           new Object[] {codimp,resSC.getMessaggio()});
		     	       }
				} catch (RemoteException re) {
			        throw new GestoreException("Si e' verificato un errore durante la modifica del soggetto collettivo: " + re.getMessage(),
			                "cineca.soggettoCollettivo.remote.error", new Object[] {re.getMessage()}, re);
				}
            }
          }else{
            String msg = " - Non risultano valorizzati i seguenti dati obbligatori: \n" + ctrlDOres[1];
            UtilityStruts.addMessage(this.getRequest(), "warning",
                "warnings.cineca.mancataIntegrazione",
                new Object[] {codimp,msg});
          }

        }


      }else {
    	  if(res[0] != null && "-7".equals(res[0])) {
              UtilityStruts.addMessage(this.getRequest(), "warning",
                      "warnings.cineca.mancataIntegrazione",
                      new Object[] {codimp,res[1]});
    	  }

      }
    }

    if(!"".equals(integrazioneWSERP)){
      if (impl.isModifiedColumn("IMPR.PIVIMP") || impl.isModifiedColumn("IMPR.CFIMP")) {
        //In caso di integrazione WSERP annullo il Codice Fornitore ERP
        // se la prop dedicata e' valorizzata a 1
        String sbiancaCodFornitoreERP = ConfigManager.getValore("wserp.erp.sbiancaCodFornitoreERP");
        sbiancaCodFornitoreERP = UtilityStringhe.convertiNullInStringaVuota(sbiancaCodFornitoreERP);
        if("1".equals(sbiancaCodFornitoreERP)){
          try {
            this.getSqlManager().update("update impr set cgenimp = null where codimp = ?", new Object[] {codimp});
          } catch (SQLException e) {
            throw new GestoreException(
                "Errore durante l'annullamento del codice fornitore ERP!",null, e);
          }
        }
      }
    }
  }

  /**
   * Funzione che verifica i duplicati della partita iva e codice fiscale
   *
   * @param manager
   *
   * @param impl
   * @throws GestoreException
   */
  private void verificaCodiceFiscalePartitaIVA(DataColumnContainer impl,String  isModificaDatiRegistrati)
      throws GestoreException {


    String msgControlloCodFisc = null;
    boolean controlloBloccanteCodFisc = false;
    String msgControlloPiva = null;
    boolean controlloBloccantePiva = false;

    boolean controlloUnicitaAbilitato= this.anagraficaManager.getAbilitazioneControlloUnicita();

    String parametri[] = new String[5];
    parametri[0] = "IMPR";      //entita
    parametri[1] = "CODIMP";    //campo chiave
    parametri[3] = "CGENIMP";   //campo anagrafica
    parametri[4] = "NOMIMP";    //ragione sociale

    String profiloAttivo = (String) this.getRequest().getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);

    if (impl.isColumn("IMPR.CFIMP")
        && impl.getString("IMPR.CFIMP") != null
        && impl.getString("IMPR.CFIMP").length() > 0) {

      try {
        parametri[2] = "CFIMP";
        msgControlloCodFisc = this.anagraficaManager.controlloUnicitaCodiceFiscalePIVA(parametri,impl.getString("IMPR.CODIMP"),impl.getString("IMPR.CFIMP"),
            impl.getString("IMPR.CGENIMP"), (String) this.getRequest().getSession().getAttribute(CostantiGenerali.ATTR_UFFINT_ABILITATI), true);

        if(msgControlloCodFisc!=null && !"".equals(msgControlloCodFisc)){
          if(!controlloUnicitaAbilitato){
            UtilityStruts.addMessage(this.getRequest(), "warning",
                "warnings.imprese.codiceFiscaleDuplicato",
                new Object[] {msgControlloCodFisc });
          }else{
            if(this.anagraficaManager.campoVisibileModificabile("IMPR", "CFIMP", profiloAttivo))
                controlloBloccanteCodFisc = true;
          }
        }


      } catch (GestoreException e) {
        throw new GestoreException(
            "Errore durante l'estrazione dei dati per effettuare la verifica del codice fiscale",
            "checkCFePIVA", e);
      }
    }else{
      if ("true".equals(isModificaDatiRegistrati)){
        throw new GestoreException("Errore durante la variazione dei dati registrati dell'impresa: non risulta valorizzato il codice fiscale!","variazioneDatiRegistrati.noCF");
      }
    }

    if (impl.isColumn("IMPR.PIVIMP")
        && impl.getString("IMPR.PIVIMP") != null
        && impl.getString("IMPR.PIVIMP").length() > 0) {

      try {
        parametri[2] = "PIVIMP";
        msgControlloPiva = this.anagraficaManager.controlloUnicitaCodiceFiscalePIVA(parametri,impl.getString("IMPR.CODIMP"),impl.getString("IMPR.PIVIMP"),
            impl.getString("IMPR.CGENIMP"), (String) this.getRequest().getSession().getAttribute(CostantiGenerali.ATTR_UFFINT_ABILITATI), true);

        if(msgControlloPiva!=null && !"".equals(msgControlloPiva)){
          String gruppoIva = null;
          if (impl.isColumn("IMPR.ISGRUPPOIVA") && impl.getString("IMPR.ISGRUPPOIVA") != null)
            gruppoIva = impl.getString("IMPR.ISGRUPPOIVA");
          if(!"1".equals(gruppoIva)){
            if(!controlloUnicitaAbilitato){
              UtilityStruts.addMessage(this.getRequest(), "warning",
                  "warnings.imprese.partitaIvaDuplicata",
                  new Object[] {msgControlloPiva });
            }else{
              if(this.anagraficaManager.campoVisibileModificabile("IMPR", "PIVIMP", profiloAttivo))
                  controlloBloccantePiva = true;
            }
          }
        }

      } catch (GestoreException e) {
        throw new GestoreException(
            "Errore durante l'estrazione dei dati per effettuare la verifica della partita iva",
            "checkCFePIVA", e);
      }
    }else{
      boolean saltareControllo=false;
      String gruppoIva = null;
      if (impl.isColumn("IMPR.ISGRUPPOIVA") && impl.getString("IMPR.ISGRUPPOIVA") != null)
        gruppoIva = impl.getString("IMPR.ISGRUPPOIVA");
      if(!"1".equals(gruppoIva)) {
        if(impl.isColumn("IMPR.TIPIMP")){
          Long tipimp = impl.getLong("IMPR.TIPIMP");

          if(this.anagraficaManager.saltareControlloObbligPiva(tipimp))
            saltareControllo=true;

        }
      }

      if ("true".equals(isModificaDatiRegistrati) && !saltareControllo){
        throw new GestoreException("Errore durante la variazione dei dati registrati dell'impresa: non risulta valorizzata la partita iva!","variazioneDatiRegistrati.noPIVA");
      }
    }

    //Nel caso sia presente il controllo bloccante sull'unicità, si blocca
    //il salvataggio e si visualizza il relativo messaggio
    if(controlloBloccanteCodFisc && !controlloBloccantePiva){
      SQLException e = new SQLException();
      throw new GestoreException(
          "Codice fiscale duplicato",
          "imprese.codiceFiscaleDuplicato",new Object[] {msgControlloCodFisc },e);

    }else if(!controlloBloccanteCodFisc && controlloBloccantePiva){
      SQLException e = new SQLException();
      throw new GestoreException(
          "Partita I.V.A. duplicata",
          "imprese.partitaIvaDuplicata",new Object[] {msgControlloPiva },e);


    }else if(controlloBloccanteCodFisc && controlloBloccantePiva){
      SQLException e = new SQLException();
      throw new GestoreException(
          "Codice fiscale e Partita I.V.A. duplicati",
          "imprese.codiceFiscalepartitaIvaDuplicati",new Object[] {msgControlloCodFisc, msgControlloPiva },e);
    }


    if ("true".equals(isModificaDatiRegistrati)){
      this.verificaVariazioneAltriDatiRegistati(impl);
    }


  }









/**
 * Funzione che verifica la variazione dei dati di una impresa registrata
 *
 * @param manager
 *
 * @param impl
 * @throws GestoreException
 */
  private void verificaVariazioneAltriDatiRegistati(DataColumnContainer impl)
    throws GestoreException {


    if (impl.isColumn("IMPR.NOMEST")
        && impl.getString("IMPR.NOMEST") == null) {
        throw new GestoreException("Errore durante la variazione dei dati registrati dell'impresa: non risulta valorizzata la ragione sociale!","variazioneDatiRegistrati.noRagSoc");
    }

    if (impl.isColumn("IMPR.EMAIIP")
        && impl.getString("IMPR.EMAIIP") == null
        && impl.isColumn("IMPR.EMAI2IP")
        && impl.getString("IMPR.EMAI2IP") == null)
        {
        throw new GestoreException("Errore durante la variazione dei dati registrati dell'impresa: non risulta valorizzata la mail o pec","variazioneDatiRegistrati.noMailPec");
    }

  }

//  /**
//   * Viene controllato che non vi sia nessuna impresa registrata con lo stesso indirizzo mail
//   *
//   * @param codiceImpresa
//   * @param impl
//   *
//   * @throws GestoreException
//   */
//  private void controlloUnicitaMailRegistrazione(String codiceImpresa, DataColumnContainer impl) throws GestoreException
//  {
//    Long countMail = null;
//    String mail=impl.getString("IMPR.EMAIIP");
//    String mailPec=impl.getString("IMPR.EMAI2IP");
//    String mailRiferimento="";
//    if(mailPec != null && !"".equals(mailPec)){
//      mailRiferimento = mailPec;
//    }else{
//      mailRiferimento = mail;
//    }
//
//    try {
//     //String usernome = (String)this.getSqlManager().getObject(select, new Object[]{userent,codiceImpresa});
//     String select="select count(iduser) from w_puser where w_puser.userent = 'IMPR' and  w_puser.userkey1 <> ? and useremail = ?";
//     countMail = (Long) this.sqlManager.getObject(select, new Object[]{codiceImpresa,mailRiferimento});
//
//
//   } catch (SQLException e) {
//     throw new GestoreException(
//         "Errore durante il controllo sull'unicità della mail della ditta registrata",null, e);
//   }
//   if(countMail!= null && countMail.longValue()>0){
//     SQLException e=null;
//     throw new GestoreException("Errore durante la variazione dei dati registrati dell'impresa: esiste un'altra impresa registrata con la stessa mail o pec",
//         "variazioneDatiRegistrati.mailDuplicata",new Object[]{mailRiferimento},e);
//   }
//  }

  /**
   * Se sono stati modificati i dati relativi alla ragione sociale, o alla mail di riferimento, o
   * al codice fiscale o alla partita iva, si deve comunicare la modifica al portale tramite la
   * chiamata al servizio aggiornaImpresa
   *
   * @param codiceImpresa
   * @param impl
   *
   * @throws GestoreException
   */
  private void allineamentoDatiPortale(String codiceImpresa, DataColumnContainer impl) throws GestoreException
  {

    try {
      String select="select usernome from w_puser where userkey1 = ? and userent = 'IMPR'";
      String user;

      Vector datiUser = this.sqlManager.getVector(select, new Object[]{codiceImpresa});
      user = SqlManager.getValueFromVectorParam(datiUser, 0).getStringValue();

      if(impl.isModifiedColumn("IMPR.NOMEST")||impl.isModifiedColumn("IMPR.CFIMP")||impl.isModifiedColumn("IMPR.PIVIMP")
          ||impl.isModifiedColumn("IMPR.EMAI2IP")||impl.isModifiedColumn("IMPR.EMAIIP")){

        String nomest = impl.getString("IMPR.NOMEST");
        String codfisc = impl.getString("IMPR.CFIMP");
        String piva = impl.getString("IMPR.PIVIMP");
        String pec =  impl.getString("IMPR.EMAI2IP");
        String mail =  impl.getString("IMPR.EMAIIP");

        if(nomest.length()>120) {
          nomest = nomest.substring(0, 119);
        }

          //Si deve comunicare al portale che vi sono state delle modifiche
          PortaleAliceProxy proxy = new PortaleAliceProxy();
          //indirizzo del servizio letto da properties
          String endPoint = ConfigManager.getValore(CostantiGenerali.PROP_URL_WEB_SERVICE_PORTALE_ALICE);
          proxy.setEndpoint(endPoint);
          EsitoOutType risultato = proxy.aggiornaImpresa(user, mail, pec, nomest, codfisc, piva);
          if(!risultato.isEsitoOk()){
            String codiceErrore = risultato.getCodiceErrore();
            String codice= "";
            String dati = "";
            if (codiceErrore!=null && !"".equals(codiceErrore)){
              if(codiceErrore.indexOf("UNEXP-ERR")>=0){
                dati = codiceErrore.substring(codiceErrore.indexOf(" "));
                codice = "variazioneDatiRegistrati.UNEXP-ERR";
              }
              if(codiceErrore.indexOf("UNKNOWN-USER")>=0){
                codice = "variazioneDatiRegistrati.UNKNOWN-USER";
                dati=user;
              }
              if(codiceErrore.indexOf("EMPTY-EMAIL")>=0){
                codice = "variazioneDatiRegistrati.EMPTY-EMAIL";
                dati = "(mail " + mail + ", pec " + pec + ")";
              }
              if(codiceErrore.indexOf("DUPL-EMAIL")>=0){
                codice = "variazioneDatiRegistrati.DUPL-EMAIL";
                dati = "(mail " + mail + ", pec " + pec + ")";
              }
              if(codiceErrore.indexOf("DUPL-CF")>=0){
                codice = "variazioneDatiRegistrati.DUPL-CF";
                dati=codfisc;

              }
              if(codiceErrore.indexOf("DUPL-PI")>=0){
                codice = "variazioneDatiRegistrati.DUPL-PI";
                dati=piva ;

              }

            }
            SQLException e=null;
            throw new GestoreException("Errore durante la variazione dei dati registrati dell'impresa",codice,new Object[]{dati},e);
          }

          //Se è stata modificata la ragione sociale si deve aggiornare la W_PUSER
          if(impl.isModifiedColumn("IMPR.NOMEST")){
            select="update w_puser set userdesc = ? where userkey1 = ? and userent = 'IMPR'";
            this.getSqlManager().update(select, new Object[] { nomest, codiceImpresa});
          }
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore durante la variazione dei dati registrati dell'impresa",null,e);
      } catch (RemoteException e) {
        String codice="variazioneDatiRegistrati";
        String messaggio = e.getMessage();
        if(messaggio.indexOf("Connection refused")>0)
          codice+=".noServizio";
        if(messaggio.indexOf("Connection timed out")>0)
          codice+=".noServer";
        throw new GestoreException(
            "Errore durante la variazione dei dati registrati dell'impresa",codice, e);
      }

  }

  /**
   * Si cancellano tutte le occorrenze di IMPANNO e si reinseriscono le occorrenze della pagina
   * solo se è impostato il numero di dipendenti.
   *
   * @param impl
   * @param modalita
   *
   * @throws GestoreException
   */
  private void updatePersonale(DataColumnContainer impl, String modalita) throws GestoreException{
    if(this.getGeneManager().getProfili().checkProtec(
        (String) this.getRequest().getSession().getAttribute(
            CostantiGenerali.PROFILO_ATTIVO), "SEZ", "VIS",
        "GENE.ImprScheda.DATIGEN.PERSDIP")){
        String codimp = impl.getString("IMPR.CODIMP");
        String numElementiLista = this.getRequest().getParameter("numElementiListaPersonale");
        int numElementiListaPersonale=0;
        if(numElementiLista!=null && !"".equals(numElementiLista))
          numElementiListaPersonale = UtilityNumeri.convertiIntero(numElementiLista).intValue();
        Long anno;
        Long numdip;
        if("UPD".equals(modalita)){
          try {
            sqlManager.update("delete from impanno where codimp = ? ", new Object[] { codimp});
          }catch (SQLException e) {
            throw new GestoreException("Errore durante l'eliminazione del personale dipendente",null,e);
          }
        }
        for(int i=0; i<numElementiListaPersonale;i++){
          anno = impl.getLong("ANNO_" + i);
          numdip = impl.getLong("N_DIP_" + i);
          if(numdip!=null)
            try {
              sqlManager.update("insert into impanno(codimp,anno,numdip) values(?,?,?) ", new Object[] { codimp,anno,numdip});
            }catch (SQLException e) {
              throw new GestoreException("Errore durante l'inserimento del personale dipendente",null,e);
            }
        }
      }
  }
}




