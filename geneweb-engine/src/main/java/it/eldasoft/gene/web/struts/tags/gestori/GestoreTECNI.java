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

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.bl.AnagraficaManager;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreTECNI extends AbstractGestoreEntita {

  /**
   * Logger per tracciare messaggio di debug
   */
  static Logger               logger                    = Logger.getLogger(GestoreTECNI.class);

  @Override
  public String getEntita() {
    return "TECNI";
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
		String codiceTecni = impl.getString("TECNI.CODTEC");
		gene.checkConstraints("TECNI", new String[]{codiceTecni});

		/*String toDel[] = new String[]{
		 "impdte", "codimp3", "impleg", "codimp2", "impind", "codimp5", "impazi",
		 "codimp4", "impope", "codimp", "cate", "codimp1", "ragimp", "codime9",
		 "impcase", "codimp"};
		 Object params[] = new Object[]{
		 impl.getString("TECNI.CODIMP")};
		 // Esecuzione dell'eliminazione di tutte le tabelle collegate
		 for (int i = 0; (i + 1) < toDel.length; i += 2) {
		 gene.deleteTabelle(new String[]{toDel[i]}, toDel[i + 1] + " = ?",
		 params);
		 }*/
	}

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    GeneManager gene = this.getGeneManager();

    // Se si ha la codifica automatica allora eseguo il ricalcolo
    if (gene.isCodificaAutomatica("TECNI", "CODTEC")) {
      // Setto il codice impresa come chiave altrimenti non ritorna sulla riga
      // giusta
      impl.getColumn("TECNI.CODTEC").setChiave(true);
      impl.setValue("TECNI.CODTEC", gene.calcolaCodificaAutomatica("TECNI",
          "CODTEC"));
    }
    String modo = (String) this.getRequest().getAttribute("modo");
    if(!"REGISTRAZIONE".equals(modo)){
      this.verificaCodiceFiscalePartitaIVA(impl, null);
    }


  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    impl.getColumn("TECNI.CODTEC").setChiave(true);
    // Update delle intestazioni del tecnico
    if (impl.isModifiedColumn("TECNI.NOMTEC")) {
      // Se è modificata l'intestazione chiamo la funzione d'aggiornamento
      // dell'intestazione in database
      this.getGeneManager().aggiornaIntestazioniInDB("TECNI",
          impl.getString("TECNI.NOMTEC"),
          new Object[] { impl.getString("TECNI.CODTEC") });
    }
    String modo = (String) this.getRequest().getAttribute("modo");
    if(!"REGISTRAZIONE".equals(modo)){
      this.verificaCodiceFiscalePartitaIVA(impl, null);
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
  public void verificaCodiceFiscalePartitaIVA(DataColumnContainer impl, String[] esito)
      throws GestoreException {

    String modo = (String) this.getRequest().getAttribute("modo");
    String msg = null;
    String msgControlloCodFisc = null;
    boolean controlloBloccanteCodFisc = false;
    String msgControlloPiva = null;
    boolean controlloBloccantePiva = false;
    AnagraficaManager anagraficaManager = (AnagraficaManager) UtilitySpring.getBean(
        "anagraficaManager", this.getServletContext(), AnagraficaManager.class);

    boolean controlloUnicitaAbilitato= anagraficaManager.getAbilitazioneControlloUnicita();

    String parametri[] = new String[5];
    parametri[0] = "TECNI";   //entita
    parametri[1] = "CODTEC";  //campo chiave
    parametri[3] = "CGENTEI"; //codice anagrafico
    parametri[4] = "NOMTEC";  //Nome tecnico

    String profiloAttivo = (String) this.getRequest().getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);

    if (impl.isColumn("TECNI.CFTEC")
        && impl.getString("TECNI.CFTEC") != null
        && impl.getString("TECNI.CFTEC").length() > 0) {

      // Verifico che non esista gia
      try {

        parametri[2] = "CFTEC";
        msgControlloCodFisc = anagraficaManager.controlloUnicitaCodiceFiscalePIVA(parametri,impl.getString("TECNI.CODTEC"),impl.getString("TECNI.CFTEC"),
            impl.getString("TECNI.CGENTEI"),(String) this.getRequest().getSession().getAttribute(CostantiGenerali.ATTR_UFFINT_ABILITATI), false);

        if(msgControlloCodFisc!=null && !"".equals(msgControlloCodFisc)){
          if(!controlloUnicitaAbilitato){
            if("REGISTRAZIONE".equals(modo)){
              esito[0] = "1"; //warning
              msg = "ATTENZIONE: i tecnici " + msgControlloCodFisc + " hanno lo stesso codice fiscale del soggetto che richiede la registrazione.";
              esito[1] = msg;
              if (logger.isInfoEnabled()) logger.warn(msg);
            }else{
              UtilityStruts.addMessage(this.getRequest(), "warning",
                  "warnings.tecni.codiceFiscaleDuplicato",
                  new Object[] {msgControlloCodFisc });
            }
          }else{
            if("REGISTRAZIONE".equals(modo)){
              controlloBloccanteCodFisc = true;
            }else{
              if(anagraficaManager.campoVisibileModificabile("TECNI", "CFTEC", profiloAttivo)){
                controlloBloccanteCodFisc = true;
              }
            }
          }
        }

      } catch (GestoreException e) {
          if("REGISTRAZIONE".equals(modo)){
            esito[0] = "2"; //error
            esito[1] = this.resBundleGenerale.getString("checkCFePIVA");
          }else{
            throw new GestoreException(
                "Errore durante l'estrazione dei dati per effettuare la verifica del codice fiscale",
                "checkCFePIVA", e);
          }
      }
    }

    if (impl.isColumn("TECNI.PIVATEC")
        && impl.getString("TECNI.PIVATEC") != null
        && impl.getString("TECNI.PIVATEC").length() > 0) {

      try {

        parametri[2] = "PIVATEC";
        msgControlloPiva = anagraficaManager.controlloUnicitaCodiceFiscalePIVA(parametri,impl.getString("TECNI.CODTEC"),impl.getString("TECNI.PIVATEC"),
            impl.getString("TECNI.CGENTEI"),(String) this.getRequest().getSession().getAttribute(CostantiGenerali.ATTR_UFFINT_ABILITATI), false);

        if(msgControlloPiva!=null && !"".equals(msgControlloPiva)){
          if(!controlloUnicitaAbilitato){
            if("REGISTRAZIONE".equals(modo)){
              esito[0] = "1"; //warning
              msg = "ATTENZIONE: i tecnici " + msgControlloPiva + " hanno la stessa partita IVA del soggetto che richiede la registrazione.";
              esito[1] = msg;
              if (logger.isDebugEnabled()) logger.debug(msg);
            }else{
              UtilityStruts.addMessage(this.getRequest(), "warning",
                  "warnings.tecni.partitaIvaDuplicata",
                  new Object[] {msgControlloPiva });
            }

          }else{
            if("REGISTRAZIONE".equals(modo)){
              controlloBloccantePiva = true;
            }else{
              if(anagraficaManager.campoVisibileModificabile("TECNI", "PIVATEC", profiloAttivo)){
                controlloBloccantePiva = true;
              }
            }
          }
        }
      } catch (GestoreException e) {
          if("REGISTRAZIONE".equals(modo)){
            esito[0] = "2"; //error
            esito[1] = this.resBundleGenerale.getString("checkCFePIVA");
          }else{
            throw new GestoreException("Errore durante l'estrazione dei dati per effettuare la verifica della partita iva",
                "checkCFePIVA", e);
          }
      }
    }

    //Nel caso sia presente il controllo bloccante sull'unicità, si blocca
    //il salvataggio e si visualizza il relativo messaggio
    if(controlloBloccanteCodFisc && !controlloBloccantePiva){
      SQLException e = new SQLException();
      if("REGISTRAZIONE".equals(modo)){
        throw new GestoreException(
            "Codice fiscale già presente",
            "tecni.codiceFiscalePresente",null,e);
      }else{
        throw new GestoreException(
            "Codice fiscale duplicato",
            "tecni.codiceFiscaleDuplicato",new Object[] {msgControlloCodFisc },e);
      }

    }else if(!controlloBloccanteCodFisc && controlloBloccantePiva){
      SQLException e = new SQLException();
      if("REGISTRAZIONE".equals(modo)){
        throw new GestoreException(
            "Partita I.V.A. già presente",
            "tecni.partitaPresente",null,e);
      }else{
        throw new GestoreException(
            "Partita I.V.A. duplicata",
            "tecni.partitaIvaDuplicata",new Object[] {msgControlloPiva },e);
      }

    }else if(controlloBloccanteCodFisc && controlloBloccantePiva){
      SQLException e = new SQLException();
      if("REGISTRAZIONE".equals(modo)){
        throw new GestoreException(
            "Codice fiscale e Partita I.V.A. già presenti",
            "tecni.codiceFiscalepartitaIvaPresenti",null,e);
      }else{
        throw new GestoreException(
            "Codice fiscale e Partita I.V.A. duplicati",
            "tecni.codiceFiscalepartitaIvaPresenti",new Object[] {msgControlloCodFisc, msgControlloPiva },e);
      }
    }
  }

}
