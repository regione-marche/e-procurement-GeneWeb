/*
 * Created on 08/giu/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.genric;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.GestoreProfili;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.properties.ConfigManager;

/**
 * Gestore per la verifica della visibilit&agrave; nelle ricerche/modelli dei
 * dati (entit&agrave; e campi) a seconda del profilo o delle configurazioni
 * definite in DYNENT/DYNCAM
 * 
 * @author Stefano.Sabbadin
 * @since 1.5.1
 */
public class GestoreVisibilitaDati {

  /** Logger Log4J di classe */
  static Logger            logger            = Logger.getLogger(GestoreVisibilitaDati.class);

  protected ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  private GeneManager      geneManager;

  /**
   * Set di entit&agrave; dinamiche gestite nella DYNENT mediante generatore
   * attributi, figlie 1:N, archivi esterni...
   */
  private Set<String>      listaEntitaDinamiche;

  /**
   * Subset di listaEntitaDinamiche di entit&agrave; gestite mediante il solo
   * generatore attributi
   */
  private Set<String>      listaEntitaGenAttributi;

  public GestoreVisibilitaDati(GeneManager geneManager) {
    this.geneManager = geneManager;
    this.listaEntitaDinamiche = null;
    this.listaEntitaGenAttributi = null;
  }

  /**
   * Effettua il test per un'entit&agrave; in modo da verificare se &egrave;
   * visibile o meno nelle ricerche, utilizzando sempre per le entit&agrave;
   * standard la gestione via profilo, mentre per le entit&agrave; dinamiche di
   * default sono sempre visibili a meno di avvio dell'applicativo con la
   * configurazione della visibilit&agrave; dell'entit&agrave; mediante profilo.
   * 
   * @param tabella
   *        tabella
   * @param codProfilo
   *        codice del profilo in uso dall'utente
   * @return true se l'entit&agrave; &egrave; visibile, false altrimenti
   */
  public boolean checkEntitaVisibile(Tabella tabella, String codProfilo) {
    boolean esito = false;

    if (tabella.isVisibileRicerche()
        || tabella.getNomeSchema().equals(
            ConfigManager.getValore(CostantiGenerali.PROP_SCHEMA_VISTE_REPORT_BASE))) {
      String propGenAttributi = ConfigManager.getValore("it.eldasoft.genAttributi.controllo.usaProfilo");

      // la gestione via profilo si usa per:
      // - le entità standard non dinamiche
      // - le entità del generatore attributi sempre che non sia impostata la
      // property per il controllo via profilo
      //
      // tutte le altre entità dinamiche sono abilitate
      boolean usaProfilo = true;
      this.getListaEntitaDinamiche();
      if (listaEntitaDinamiche.contains(tabella.getNomeTabella())) {
        if (listaEntitaGenAttributi.contains(tabella.getNomeTabella())) {
          if (!"1".equals(propGenAttributi)) usaProfilo = false;
        } else
          usaProfilo = false;
      }

      if (usaProfilo) {
        GestoreProfili gestoreProfili = this.geneManager.getProfili();
        esito = gestoreProfili.checkProtec(codProfilo, "TABS", "VIS",
            tabella.getNomeSchema().concat(
                CostantiGenRicerche.SEPARATORE_SCHEMA_TABELLA_CAMPO).concat(
                tabella.getNomeTabella()));
      } else {
        esito = true;
      }
    }

    return esito;
  }

  /**
   * Effettua il test per un campo in modo da verificare se &egrave; visibile o
   * meno nelle ricerche, utilizzando sempre per i campi di entit&agrave;
   * standard la gestione via profilo, mentre per quelli di entit&agrave;
   * dinamiche di default il campo DYNCAM.DYNCAM_SCH a meno di avvio
   * dell'applicativo con la configurazione della visibilit&agrave; dei campi
   * mediante profilo.
   * 
   * @param campo
   *        campo
   * @param codProfilo
   *        codice del profilo in uso dall'utente
   * @return true se l'entit&agrave; &egrave; visibile, false altrimenti
   */
  public boolean checkCampoVisibile(Campo campo, String codProfilo) {
    boolean esito = false;

    if (campo.isVisibileRicerche()) {
      String propGenAttributi = ConfigManager.getValore("it.eldasoft.genAttributi.controllo.usaProfilo");

      // la gestione via profilo si usa per:
      // - le entità standard non dinamiche
      // - le entità del generatore attributi sempre che non sia impostata la
      // property per il controllo via profilo
      //
      // tutte le altre entità dinamiche sono abilitate ed i campi visibili sono
      // quelli per cui DYNCAM_SCH=1
      boolean usaProfilo = true;
      this.getListaEntitaDinamiche();
      if (listaEntitaDinamiche.contains(campo.getNomeTabella())) {
        if (listaEntitaGenAttributi.contains(campo.getNomeTabella())) {
          if (!"1".equals(propGenAttributi)) usaProfilo = false;
        } else
          usaProfilo = false;
      }

      if (usaProfilo) {
        GestoreProfili gestoreProfili = this.geneManager.getProfili();
        esito = gestoreProfili.checkProtec(codProfilo, "COLS", "VIS",
            campo.getNomeSchema().concat(
                CostantiGenRicerche.SEPARATORE_SCHEMA_TABELLA_CAMPO).concat(
                campo.getNomeTabella()).concat(
                CostantiGenRicerche.SEPARATORE_SCHEMA_TABELLA_CAMPO).concat(
                campo.getNomeCampo()));
      } else {
        // siamo nella gestione di un campo di un'entita' dinamica, per cui si
        // verifica nella DYNCAM la visibilità o meno in base al campo
        // DYNCAM_SCH
        try {
          String scheda = (String) this.geneManager.getSql().getObject(
              "select dyncam_sch from dyncam where dynent_name = ? and dyncam_name = ?",
              new String[] { campo.getNomeTabella(), campo.getNomeCampo() });
          if ("1".equals(scheda))
            esito = true;
          else
            esito = false;
        } catch (SQLException e) {
          // non si dovrebbe verificare mai...
          logger.error(
              this.resBundleGenerale.getString("errors.database.dataAccessException"),
              e);
          throw new RuntimeException(e.getMessage());
        }
      }
    }

    return esito;
  }

  /**
   * Verifica la valorizzazione del set delle entit&agrave; dinamiche, e la
   * prima volta che viene verificata la non valorizzazione si procede
   * all'estrazione delle informazioni ed al popolamento del set in modo sicuro
   * e sincronizzato
   */
  private void getListaEntitaDinamiche() {
    if (listaEntitaDinamiche == null) {
      synchronized (this) {
        if (listaEntitaDinamiche == null) {
          // si estrae e si popola la lista
          listaEntitaDinamiche = new HashSet<String>();
          listaEntitaGenAttributi = new HashSet<String>();
          try {
            List<?> listaEntita = this.geneManager.getSql().getListVector(
                "select dynent_name, dynent_type from dynent", new String[] {});
            for (int i = 0; i < listaEntita.size(); i++) {
              this.listaEntitaDinamiche.add(((Vector<?>) listaEntita.get(i)).get(0).toString());
              // solo le entità di tipo 2 vanno nella lista entita del
              // generatore attributi
              if ("2".equals(((Vector<?>) listaEntita.get(i)).get(1).toString()))
                this.listaEntitaGenAttributi.add(((Vector<?>) listaEntita.get(i)).get(
                    0).toString());
            }

          } catch (SQLException e) {
            // non si dovrebbe verificare mai...
            logger.error(
                this.resBundleGenerale.getString("errors.database.dataAccessException"),
                e);
            throw new RuntimeException(e.getMessage());
          }
        }
      }
    }
  }

}
