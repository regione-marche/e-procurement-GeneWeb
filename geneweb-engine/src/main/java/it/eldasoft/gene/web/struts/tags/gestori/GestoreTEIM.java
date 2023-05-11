/*
 * Created on 29/mag/2008
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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.bl.AnagraficaManager;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore del tecnico delle imprese
 *
 * @author Luca.Giacomazzo
 */
public class GestoreTEIM extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "TEIM";
  }

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
					throws GestoreException {

		//Prima di procedere con l'eliminazione si deve controllare
		//che non sia collegato a qualche entita
		GeneManager gene = getGeneManager();
		String codiceTeim = impl.getString("TEIM.CODTIM");
		gene.checkConstraints("TEIM", new String[]{codiceTeim});
	}

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    // Se si ha la codifica automatica allora eseguo il ricalcolo
    if (this.getGeneManager().isCodificaAutomatica("TEIM", "CODTIM")) {
      // Setto il codice del tecnico delle imprese come chiave altrimenti
      // non ritorna sulla riga giusta
      impl.getColumn("TEIM.CODTIM").setChiave(true);
      impl.setValue("TEIM.CODTIM",
          this.getGeneManager().calcolaCodificaAutomatica("TEIM", "CODTIM"));
    }
    this.verificaCodiceFiscalePartitaIVA(impl);
    // Si gestiscono gli inserimenti in impleg e impdte nel preupdate perchè se
    // gestiti nel postUpdate l'inserimento delle teim verrebbe sempre ripetuto
    // (a volte anche con errori tipo chiave duplicata) con intasamento del db
    // assicurato e tante occorrenze sporche. in questa maniera se capita un
    // qualsiasi errore la transazioen è fermata e si evitano ulteriori problemi

    String provenienza = impl.getString("PROVTEIM");
    if (provenienza != null && "IMPLEG".equalsIgnoreCase(provenienza))
      this.gestisciImpleg(impl, status);
    if (provenienza != null && "IMPDTE".equalsIgnoreCase(provenienza))
      this.gestisciImpdte(impl, status);
  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {

  }

  /**
   * Funzione che gestisce l'inserimento dell'occorrenza di collegamento fra il
   * tecnico e l'impresa nel caso in cui la chiamata proviene dall'appalto
   *
   * @param impl
   * @param status
   * @throws GestoreException
   */
  private void gestisciImpleg(DataColumnContainer impl, TransactionStatus status)
      throws GestoreException {

    DataColumnContainer implLeg = new DataColumnContainer("");
    implLeg.addColumn("IMPLEG.CODIMP2", impl.getColumn("IMPRTEIM"));
    implLeg.addColumn("IMPLEG.CODLEG", impl.getColumn("TEIM.CODTIM"));
    implLeg.addColumn("IMPLEG.NOMLEG", impl.getColumn("TEIM.NOMTIM"));
   
    //Modifiche per cambiamento chiave entità IMPLEG e IMPDTE
    implLeg.addColumn("IMPLEG.ID", JdbcParametro.TIPO_NUMERICO,null);

    
    AbstractGestoreEntita gestoreIMPLEG = new GestoreIMPLEG();
    gestoreIMPLEG.setRequest(this.getRequest());
    gestoreIMPLEG.inserisci(status, implLeg);

  }

  private void gestisciImpdte(DataColumnContainer impl, TransactionStatus status)
      throws GestoreException {

    DataColumnContainer implDte = new DataColumnContainer("");
    implDte.addColumn("IMPDTE.CODIMP3", impl.getColumn("IMPRTEIM"));
    implDte.addColumn("IMPDTE.CODDTE", impl.getColumn("TEIM.CODTIM"));
    implDte.addColumn("IMPDTE.NOMDTE", impl.getColumn("TEIM.NOMTIM"));

    //Modifiche per cambiamento chiave entità IMPLEG e IMPDTE
    implDte.addColumn("IMPDTE.ID", JdbcParametro.TIPO_NUMERICO,null);

    
    AbstractGestoreEntita gestoreIMPDTE = new GestoreIMPDTE();
    gestoreIMPDTE.setRequest(this.getRequest());
    gestoreIMPDTE.inserisci(status, implDte);

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    impl.getColumn("TEIM.CODTIM").setChiave(true);
    // Update delle intestazioni del tecnico delle imprese
    if (impl.isModifiedColumn("TEIM.NOMTIM")) {
      // Se è modificata l'intestazione chiamo la funzione d'aggiornamento
      // dell'intestazione in database
      this.getGeneManager().aggiornaIntestazioniInDB("TEIM",
          impl.getString("TEIM.NOMTIM"),
          new Object[] { impl.getString("TEIM.CODTIM") });
    }
    this.verificaCodiceFiscalePartitaIVA(impl);
  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

  /**
   * Funzione che verifica i duplicati della partita iva e codice fiscale
   *
   * @param manager
   *
   * @param impl
   * @throws GestoreException
   */
  private void verificaCodiceFiscalePartitaIVA(DataColumnContainer impl)
      throws GestoreException {
    /*
     * Questo metodo e' stato copiato dalla classe GestoreUTENT e modificato per
     * la tabella TEIM
     */

    String msgControlloCodFisc = null;
    boolean controlloBloccanteCodFisc = false;
    String msgControlloPiva = null;
    boolean controlloBloccantePiva = false;
    AnagraficaManager anagraficaManager = (AnagraficaManager) UtilitySpring.getBean(
        "anagraficaManager", this.getServletContext(), AnagraficaManager.class);

    boolean controlloUnicitaAbilitato= anagraficaManager.getAbilitazioneControlloUnicita();

    String parametri[] = new String[5];
    parametri[0] = "TEIM";      //entita
    parametri[1] = "CODTIM";    //campo chiave
    parametri[3] = "CGENTIM";   //campo anagrafica
    parametri[4] = "NOMTIM";    //ragione sociale

    String profiloAttivo = (String) this.getRequest().getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);


    if (impl.isColumn("TEIM.CFTIM")
        && impl.getString("TEIM.CFTIM") != null
        && impl.getString("TEIM.CFTIM").length() > 0) {

      // Verifica che non esista gia il codice fiscale
      try {

        parametri[2] = "CFTIM";
        msgControlloCodFisc = anagraficaManager.controlloUnicitaCodiceFiscalePIVA(parametri,impl.getString("TEIM.CODTIM"),impl.getString("TEIM.CFTIM"),
            impl.getString("TEIM.CGENTIM"),(String) this.getRequest().getSession().getAttribute(CostantiGenerali.ATTR_UFFINT_ABILITATI), true);

        if(msgControlloCodFisc!=null && !"".equals(msgControlloCodFisc)){
          if(!controlloUnicitaAbilitato){
            UtilityStruts.addMessage(this.getRequest(), "warning",
                "warnings.teim.codiceFiscaleDuplicato",
                new Object[] {msgControlloCodFisc });
          }else{
            if(anagraficaManager.campoVisibileModificabile("TEIM", "CFTIM", profiloAttivo))
                controlloBloccanteCodFisc = true;
          }
        }
      } catch (GestoreException e) {
        throw new GestoreException(
            "Errore durante l'estrazione dei dati per effettuare la verifica del codice fiscale",
            "checkCFePIVA", e);
      }
    }

    if (impl.isColumn("TEIM.PIVATEI")
        && impl.getString("TEIM.PIVATEI") != null
        && impl.getString("TEIM.PIVATEI").length() > 0) {
      // Verifica che non esista gia la parita iva
      try {

        parametri[2] = "PIVATEI";
        msgControlloPiva = anagraficaManager.controlloUnicitaCodiceFiscalePIVA(parametri,impl.getString("TEIM.CODTIM"),impl.getString("TEIM.PIVATEI"),
            impl.getString("TEIM.CGENTIM"),(String) this.getRequest().getSession().getAttribute(CostantiGenerali.ATTR_UFFINT_ABILITATI), true);

        if(msgControlloPiva!=null && !"".equals(msgControlloPiva)){
          if(!controlloUnicitaAbilitato){
            UtilityStruts.addMessage(this.getRequest(), "warning",
                "warnings.teim.partitaIvaDuplicata",
                new Object[] {msgControlloPiva });
          }else{
            if(anagraficaManager.campoVisibileModificabile("TEIM", "PIVATEI", profiloAttivo))
                controlloBloccantePiva = true;
          }
        }
      } catch (GestoreException e) {
        throw new GestoreException(
            "Errore durante l'estrazione dei dati per effettuare la verifica della partita iva",
            "checkCFePIVA", e);
      }
    }

    //Nel caso sia presente il controllo bloccante sull'unicità, si blocca
    //il salvataggio e si visualizza il relativo messaggio
    if(controlloBloccanteCodFisc && !controlloBloccantePiva){
      SQLException e = new SQLException();
      throw new GestoreException(
          "Codice fiscale duplicato",
          "teim.codiceFiscaleDuplicato",new Object[] {msgControlloCodFisc },e);

    }else if(!controlloBloccanteCodFisc && controlloBloccantePiva){
      SQLException e = new SQLException();
      throw new GestoreException(
          "Partita I.V.A. duplicata",
          "teim.partitaIvaDuplicata",new Object[] {msgControlloPiva },e);


    }else if(controlloBloccanteCodFisc && controlloBloccantePiva){
      SQLException e = new SQLException();
      throw new GestoreException(
          "Codice fiscale e Partita I.V.A. duplicati",
          "teim.codiceFiscalepartitaIvaDuplicati",new Object[] {msgControlloCodFisc, msgControlloPiva },e);
    }
  }

}