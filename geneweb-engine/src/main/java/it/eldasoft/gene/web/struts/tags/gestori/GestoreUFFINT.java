/*
 * Created on 20/lug/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.admin.UffintManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.StoUffint;
import it.eldasoft.gene.tags.bl.AnagraficaManager;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Date;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per l'entità UFFINT
 *
 * @author Marcello Caminiti
 */
public class GestoreUFFINT extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "UFFINT";
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
		GeneManager gene = getGeneManager();
		String codUfficio = impl.getString("UFFINT.CODEIN");
		gene.checkConstraints("UFFINT", new String[]{codUfficio});

		try {
			Long numeroAccount
							= (Long) this.sqlManager.getObject(
											"SELECT COUNT(SYSCON) FROM USR_EIN WHERE CODEIN = ?",
											new String[]{codUfficio});
			if (numeroAccount.intValue() > 0) {
				throw new GestoreException(
								"Impossibile rimuovere l'ufficio intestatario in quanto sono presenti degli utenti ad esso associati",
								"deleteUffintConAssocUtenti");
			}
		} catch (SQLException e) {
			throw new GestoreException(
							"Errore durante l'estrazione del numero di account associati all'elemento da eliminare",
							"checkUffintConAssocUtenti", e);
		}
		Object params[] = new Object[]{codUfficio};
		// Esecuzione dell'eliminazione di g2funz
		gene.deleteTabelle(new String[]{"g2funz"}, "codei = ?", params);
		// Esecuzione dell'eliminazione di PUNTICON
		gene.deleteTabelle(new String[]{"PUNTICON"}, "codein = ?", params);
	}

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    GeneManager gene = this.getGeneManager();

    // Se si ha la codifica automatica allora eseguo il ricalcolo
    if (gene.isCodificaAutomatica("UFFINT", "CODEIN")) {
      // Setto il codice impresa come chiave altrimenti non ritorna sulla
      // riga giusta
      impl.getColumn("UFFINT.CODEIN").setChiave(true);
      impl.setValue("UFFINT.CODEIN", gene.calcolaCodificaAutomatica("UFFINT",
          "CODEIN"));
    }
    this.verificaCodiceFiscalePartitaIVA(impl);

    // Gestione standard della sezione dinamica altri indirizzi
    AbstractGestoreChiaveNumerica gestoreG2FUNZ = new DefaultGestoreEntitaChiaveNumerica(
        "G2FUNZ", "NUMFUN", new String[] { "CODEI" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, impl, gestoreG2FUNZ,
        "APER", new DataColumn[] { impl.getColumn("UFFINT.CODEIN") }, null);

    // Gestione standard della sezione dinamica punti di contatto
    AbstractGestoreChiaveNumerica gestorePUNTICON = new DefaultGestoreEntitaChiaveNumerica(
        "PUNTICON", "NUMPUN", new String[] { "CODEIN" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, impl, gestorePUNTICON,
        "PUNTI", new DataColumn[] { impl.getColumn("UFFINT.CODEIN") }, null);

    //Gestione sezione Settori
    AbstractGestoreChiaveIDAutoincrementante gestoreUFFSET = new DefaultGestoreEntitaChiaveIDAutoincrementante(
        "UFFSET", "ID", this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, impl,
        gestoreUFFSET, "UFFSET",
        new DataColumn[] {impl.getColumn("UFFINT.CODEIN") }, null);

    //Inserimento in USR_EIN del collegamento dell'utente corrente all'ufficio intestatario
    String codein = impl.getString("UFFINT.CODEIN");

    ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long idUtente = new Long(profilo.getId());
    try {
      this.sqlManager.update("insert into usr_ein(syscon, codein) values(?,?)", new Object[]{idUtente, codein});
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'inserimento automatico in USR_EIN del collegamento fra l'utente e l'ufficio intestatario",
          "insertUsr_ein", e);
    }

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    GeneManager gene = this.getGeneManager();
    String codimp = impl.getString("UFFINT.CODEIN");
    impl.getColumn("UFFINT.CODEIN").setChiave(true);
    // Aggiornamento delle intestazioni degli archivi in DB
    if (impl.isModifiedColumn("UFFINT.NOMEIN")) {
      // Se è modificata l'intestazione chiamo la funzione d'aggiornamento
      // dell'intestazione in database
      gene.aggiornaIntestazioniInDB("UFFINT", impl.getString("UFFINT.NOMEIN"),
          new Object[] { codimp });
    }
    this.verificaCodiceFiscalePartitaIVA(impl);

    // Gestione standard della sezione dinamica altri indirizzi
    AbstractGestoreChiaveNumerica gestoreG2FUNZ = new DefaultGestoreEntitaChiaveNumerica(
        "G2FUNZ", "NUMFUN", new String[] { "CODEI" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, impl, gestoreG2FUNZ,
        "APER", new DataColumn[] { impl.getColumn("UFFINT.CODEIN") }, null);

    // Gestione utenti associati alla stazione appaltante
    if (impl.isColumn("LISTASYSCONASS") && impl.isColumn("LISTASYSCONDIS")) {
    	UffintManager uffintManager = (UffintManager) UtilitySpring.getBean("uffintManager", this.getServletContext(), UffintManager.class);
    	uffintManager.updateAssociazioneUfficiIntestatariUtenti(codimp, impl.getColumn("LISTASYSCONASS").getValue().toString(), impl.getColumn("LISTASYSCONDIS").getValue().toString());
    }
    // Gestione standard della sezione dinamica punti di contatto
    AbstractGestoreChiaveNumerica gestorePUNTICON = new GestorePUNTICON();
		gestorePUNTICON.setRequest(this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, impl, gestorePUNTICON,
        "PUNTI", new DataColumn[] { impl.getColumn("UFFINT.CODEIN") }, null);

  //Gestione sezione Settori
    AbstractGestoreChiaveIDAutoincrementante gestoreUFFSET = new DefaultGestoreEntitaChiaveIDAutoincrementante(
        "UFFSET", "ID", this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, impl,
        gestoreUFFSET, "UFFSET",
        new DataColumn[] {impl.getColumn("UFFINT.CODEIN") }, null);
    
  //verifico se alcuni dati sono stati modificati
    String profiloAttivo = (String) this.getRequest().getSession().getAttribute(
            CostantiGenerali.PROFILO_ATTIVO);
    if (gene.getProfili().checkProtec(
    		profiloAttivo,
            "FUNZ",
            "VIS",
            "ALT.GENE.UFFINT.Storico")) {
    	this.getRequest().getSession().removeAttribute("storicoUffint");
        if (impl.isModifiedColumn("UFFINT.NOMEIN") || impl.isModifiedColumn("UFFINT.VIAEIN") || impl.isModifiedColumn("UFFINT.NCIEIN") ||
        		impl.isModifiedColumn("UFFINT.CODCIT") || impl.isModifiedColumn("UFFINT.CITEIN") || impl.isModifiedColumn("UFFINT.PROEIN") ||
        		impl.isModifiedColumn("UFFINT.CAPEIN") || impl.isModifiedColumn("UFFINT.CODNAZ") || impl.isModifiedColumn("UFFINT.TELEIN") ||
        		impl.isModifiedColumn("UFFINT.FAXEIN") || impl.isModifiedColumn("UFFINT.CFEIN") || impl.isModifiedColumn("UFFINT.TIPOIN") ||
        		impl.isModifiedColumn("UFFINT.EMAIIN") || impl.isModifiedColumn("UFFINT.EMAI2IN") || impl.isModifiedColumn("UFFINT.ISCUC") ||
        		impl.isModifiedColumn("UFFINT.CFANAC")) {
        	
        	StoUffint storicoUffint = new StoUffint();
        	storicoUffint.setDenominazione(impl.getColumn("UFFINT.NOMEIN").getOriginalValue().toString());
        	storicoUffint.setCodice(impl.getColumn("UFFINT.CODEIN").getOriginalValue().toString());
        	storicoUffint.setIndirizzo(impl.getColumn("UFFINT.VIAEIN").getOriginalValue().toString());
        	storicoUffint.setCivico(impl.getColumn("UFFINT.NCIEIN").getOriginalValue().toString());
        	storicoUffint.setCodiceIstat(impl.getColumn("UFFINT.CODCIT").getOriginalValue().toString());
        	storicoUffint.setLocalita(impl.getColumn("UFFINT.CITEIN").getOriginalValue().toString());
        	storicoUffint.setProvincia(impl.getColumn("UFFINT.PROEIN").getOriginalValue().toString());
        	storicoUffint.setCap(impl.getColumn("UFFINT.CAPEIN").getOriginalValue().toString());
        	storicoUffint.setCodiceNazione(impl.getColumn("UFFINT.CODNAZ").getOriginalValue().toString());
        	storicoUffint.setTelefono(impl.getColumn("UFFINT.TELEIN").getOriginalValue().toString());
        	storicoUffint.setFax(impl.getColumn("UFFINT.FAXEIN").getOriginalValue().toString());
        	storicoUffint.setCodiceFiscale(impl.getColumn("UFFINT.CFEIN").getOriginalValue().toString());
        	storicoUffint.setTipoAmministrazione(impl.getColumn("UFFINT.TIPOIN").getOriginalValue().toString());
        	storicoUffint.setEmail(impl.getColumn("UFFINT.EMAIIN").getOriginalValue().toString());
        	storicoUffint.setPec(impl.getColumn("UFFINT.EMAI2IN").getOriginalValue().toString());
        	storicoUffint.setIscuc(impl.getColumn("UFFINT.ISCUC").getOriginalValue().toString());
        	storicoUffint.setCfAnac(impl.getColumn("UFFINT.CFANAC").getOriginalValue().toString());  
        	storicoUffint.setDataFineValidita(new Date());
        	this.getRequest().getSession().setAttribute("storicoUffint", storicoUffint);
        }
    }
  }

  /**
   * Verifica i duplicati della partita iva e codice fiscale
   *
   * @param manager
   *
   * @param impl
   * @throws GestoreException
   */
  private void verificaCodiceFiscalePartitaIVA(DataColumnContainer impl)
      throws GestoreException {

    String msgControlloCodFisc = null;
    boolean controlloBloccanteCodFisc = false;
    String msgControlloPiva = null;
    boolean controlloBloccantePiva = false;
    AnagraficaManager anagraficaManager = (AnagraficaManager) UtilitySpring.getBean(
        "anagraficaManager", this.getServletContext(), AnagraficaManager.class);

    boolean controlloUnicitaAbilitato= anagraficaManager.getAbilitazioneControlloUnicita();

    String parametri[] = new String[5];
    parametri[0] = "UFFINT";      //entita
    parametri[1] = "CODEIN";    //campo chiave
    parametri[3] = null;   //campo anagrafica
    parametri[4] = "NOMEIN";    //ragione sociale




    if (impl.isColumn("UFFINT.CFEIN")
        && impl.getString("UFFINT.CFEIN") != null
        && impl.getString("UFFINT.CFEIN").length() > 0) {

      // Verifica che non esista gia
      try {
        parametri[2] = "CFEIN";
        msgControlloCodFisc = anagraficaManager.controlloUnicitaCodiceFiscalePIVA(parametri,impl.getString("UFFINT.CODEIN"),impl.getString("UFFINT.CFEIN"),
            null,"2", true);

        if(msgControlloCodFisc!=null && !"".equals(msgControlloCodFisc))
          UtilityStruts.addMessage(this.getRequest(), "warning",
              "warnings.uffint.codiceFiscaleDuplicato",
              new Object[] {msgControlloCodFisc });
      } catch (GestoreException e) {
        throw new GestoreException(
            "Errore durante l'estrazione dei dati per effettuare la verifica del codice fiscale",
            "checkCFePIVA", e);
      }
    }

    if (impl.isColumn("UFFINT.IVAEIN")
        && impl.getString("UFFINT.IVAEIN") != null
        && impl.getString("UFFINT.IVAEIN").length() > 0) {

      try {

        parametri[2] = "IVAEIN";
        msgControlloPiva = anagraficaManager.controlloUnicitaCodiceFiscalePIVA(parametri,impl.getString("UFFINT.CODEIN"),impl.getString("UFFINT.IVAEIN"),
            null,"2", true);

        if(msgControlloPiva!=null && !"".equals(msgControlloPiva))
          UtilityStruts.addMessage(this.getRequest(), "warning",
              "warnings.uffint.partitaIvaDuplicata",
              new Object[] {msgControlloPiva });

      } catch (GestoreException e) {
        throw new GestoreException(
            "Errore durante l'estrazione dei dati per effettuare la verifica della partita iva",
            "checkCFePIVA", e);
      }
    }

  }

}
