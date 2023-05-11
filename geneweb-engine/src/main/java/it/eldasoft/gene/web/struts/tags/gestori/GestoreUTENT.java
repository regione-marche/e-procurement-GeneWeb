/*
 * Created on Nov 21, 2006
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
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore dell'entità UTENT
 *
 * @author Marcello Caminiti
 */
public class GestoreUTENT extends AbstractGestoreEntita {

  /**
   * Logger per tracciare messaggio di debug
   */
  static Logger logger = Logger.getLogger(GestoreUTENT.class);

  @Override
  public String getEntita() {
    return "UTENT";
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

    // Prima di procedere con l'eliminazione si deve controllare
    // che non sia collegato a qualche entita
    GeneManager gene = getGeneManager();
    String codiceUtent = impl.getString("UTENT.CODUTE");
    gene.checkConstraints("UTENT", new String[] { codiceUtent });
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    GeneManager gene = this.getGeneManager();

    if (logger.isDebugEnabled()) logger.debug("preInsert: inizio metodo");

    if (gene.isCodificaAutomatica("UTENT", "CODUTE")) {
      impl.getColumn("UTENT.CODUTE").setChiave(true);
      impl.setValue("UTENT.CODUTE",
          gene.calcolaCodificaAutomatica("UTENT", "CODUTE"));
    }

    this.verificaCodiceFiscalePartitaIVA(impl);

    this.inserisciPermessi(impl, "CODUTE", null);

    if (logger.isDebugEnabled()) logger.debug("preInsert: fine metodo");
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("preUpdate: inizio metodo");

    Long syscon = impl.getLong("UTENT.SYSCON");
    String email = impl.getString("UTENT.EMAIL");
    String sqlUpdate = "update usrsys set email = ? where syscon = ? and sysab3 = 'U'";
    if (syscon != null) {
      try {
        this.getSqlManager().update(sqlUpdate, new Object[] { email, syscon });
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nell'aggiornamento dell'indirizzo email dell'account dell'utente "
                + syscon, "setEmail", new Object[] { syscon }, e);
      }

    }
    this.verificaCodiceFiscalePartitaIVA(impl);

    impl.getColumn("UTENT.CODUTE").setChiave(true);

    if (logger.isDebugEnabled()) logger.debug("preUpdate: fine metodo");
  }

  private void verificaCodiceFiscalePartitaIVA(DataColumnContainer impl)
      throws GestoreException {
    /*
     * metodo copiato e riadattato
     * da verificaCodiceFiscalePartitaIVA
     * di GestoreTECNI
     */
    String msgControlloCodFisc = null;
    boolean controlloBloccanteCodFisc = false;
    String msgControlloPiva = null;
    boolean controlloBloccantePiva = false;
    AnagraficaManager anagraficaManager = (AnagraficaManager) UtilitySpring.getBean(
        "anagraficaManager", this.getServletContext(), AnagraficaManager.class);

    boolean controlloUnicitaAbilitato = anagraficaManager.getAbilitazioneControlloUnicita();

    String parametri[] = new String[5];
    parametri[0] = "UTENT"; // entita
    parametri[1] = "CODUTE"; // campo chiave
    parametri[3] = "ENTEPUBB"; // codice anagrafico
    parametri[4] = "NOMUTE"; // Nome utente

    String profiloAttivo = (String) this.getRequest().getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);

    if (impl.isColumn("UTENT.CFUTE")
        && impl.getString("UTENT.CFUTE") != null
        && impl.getString("UTENT.CFUTE").length() > 0) {

      // Verifico che non esista gia
      try {

        parametri[2] = "CFUTE";
        msgControlloCodFisc = anagraficaManager.controlloUnicitaCodiceFiscalePIVA(
            parametri, impl.getString("UTENT.CODUTE"),
            impl.getString("UTENT.CFUTE"), impl.getString("UTENT.ENTEPUBB"),
            ConfigManager.getValore(CostantiGenerali.PROP_UFFINT_ABILITATI), true);

        if (msgControlloCodFisc != null && !"".equals(msgControlloCodFisc)) {
          if (!controlloUnicitaAbilitato) {
            UtilityStruts.addMessage(this.getRequest(), "warning",
                "warnings.utent.codiceFiscaleDuplicato",
                new Object[] { msgControlloCodFisc });
          } else {
            if (anagraficaManager.campoVisibileModificabile("UTENT", "CFUTE",
                profiloAttivo)) controlloBloccanteCodFisc = true;
          }
        }

      } catch (GestoreException e) {
        throw new GestoreException(
            "Errore durante l'estrazione dei dati per effettuare la verifica del codice fiscale",
            "checkCFePIVA", e);
      }
    }

    if (impl.isColumn("UTENT.PIVAUTE")
        && impl.getString("UTENT.PIVAUTE") != null
        && impl.getString("UTENT.PIVAUTE").length() > 0) {

      try {

        parametri[2] = "PIVAUTE";
        msgControlloPiva = anagraficaManager.controlloUnicitaCodiceFiscalePIVA(
            parametri, impl.getString("UTENT.CODUTE"),
            impl.getString("UTENT.PIVAUTE"), impl.getString("UTENT.ENTEPUBB"),
            ConfigManager.getValore(CostantiGenerali.PROP_UFFINT_ABILITATI), true);

        if (msgControlloPiva != null && !"".equals(msgControlloPiva)) {
          if (!controlloUnicitaAbilitato) {
            UtilityStruts.addMessage(this.getRequest(), "warning",
                "warnings.utent.partitaIvaDuplicata",
                new Object[] { msgControlloPiva });
          } else {
            if (anagraficaManager.campoVisibileModificabile("UTENT", "PIVAUTE",
                profiloAttivo)) controlloBloccantePiva = true;
          }
        }
      } catch (GestoreException e) {
        throw new GestoreException(
            "Errore durante l'estrazione dei dati per effettuare la verifica della partita iva",
            "checkCFePIVA", e);
      }
    }

    // Nel caso sia presente il controllo bloccante sull'unicità, si blocca
    // il salvataggio e si visualizza il relativo messaggio
    if (controlloBloccanteCodFisc && !controlloBloccantePiva) {
      SQLException e = new SQLException();
      throw new GestoreException("Codice fiscale duplicato",
          "utent.codiceFiscaleDuplicato", new Object[] { msgControlloCodFisc },
          e);

    } else if (!controlloBloccanteCodFisc && controlloBloccantePiva) {
      SQLException e = new SQLException();
      throw new GestoreException("Partita I.V.A. duplicata",
          "utent.partitaIvaDuplicata", new Object[] { msgControlloPiva }, e);

    } else if (controlloBloccanteCodFisc && controlloBloccantePiva) {
      SQLException e = new SQLException();
      throw new GestoreException("Codice fiscale e Partita I.V.A. duplicati",
          "utent.codiceFiscalepartitaIvaDuplicati", new Object[] {
              msgControlloCodFisc, msgControlloPiva }, e);
    }

  }

}