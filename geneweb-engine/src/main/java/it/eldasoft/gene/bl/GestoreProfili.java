/*
 * Created on 08/ott/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.util.Hashtable;

import org.apache.log4j.Logger;

/**
 * Classe che gestisce i profili dell'applicativo e le protezioni sulla
 * profilazione
 * 
 * @author Marco.Franceschin
 * 
 */
public class GestoreProfili {

  static Logger          logger = Logger.getLogger(GestoreProfili.class);

  /**
   * Hash table con i profili caricati (dalla 1.5.1 e' stata modificata in
   * Hashtable per gestire la concorrenza)
   */
  private Hashtable<String, ConfigurazioneProfilo>   profili = new Hashtable<String, ConfigurazioneProfilo>();

  private GeneManager gene    = null;

  public GestoreProfili(GeneManager gene) {
    this.gene = gene;
  }

  /**
   * Funzione che verifica la protezione sull'azione di un oggetto di un certo
   * tipo
   * 
   * @param profilo
   *        Identificativo del profile
   * @param tipo
   *        Tipo d'oggetto
   * @param azione
   *        Azione sull'oggetto
   * @param obj
   *        Oggetto su cui verificare l'azione
   * @param defaultVal
   *        Valore di default se non presente il profilo
   * @return true se si è abilitati alla funzione/azione, false altrimenti
   */
  public boolean checkProtec(String profilo, String tipo, String azione,
      String obj, boolean defaultVal) {
    ConfigurazioneProfilo prof = this.getProfilo(profilo);
    if (prof != null)
      return prof.getProtec(tipo, azione, obj, defaultVal);
    else
      return defaultVal;
  }

  /**
   * Funzione che verifica la protezione sull'azione di un oggetto di un certo
   * tipo, nel caso di profilo attivo
   * 
   * @param profilo
   *        Identificativo del profile
   * @param tipo
   *        Tipo d'oggetto
   * @param azione
   *        Azione sull'oggetto
   * @param obj
   *        Oggetto su cui verificare l'azione
   * @return true se si è abilitati alla funzione/azione, false altrimenti
   */
  public boolean checkProtec(String profilo, String tipo, String azione,
      String obj) {
    return this.checkProtec(profilo, tipo, azione, obj, false);
  }

  /**
   * Estrae un profilo e se non ancora creato lo crea leggendo tutti i dati
   * 
   * @param idProfilo
   * @return
   */
  public ConfigurazioneProfilo getProfilo(String idProfilo) {
    ConfigurazioneProfilo profRet = null;
    if (idProfilo != null) {
      profRet = (ConfigurazioneProfilo) this.profili.get(idProfilo);
      if (profRet == null) {
        // si tenta la lettura del profilo, e se non presente si attende di
        // entrare in un blocco sincronizzato; nel momento in cui si entra si
        // ricontrolla che nel frattempo non sia stato valorizzato il profilo,
        // ed in caso di ulteriore verifica negativa si passa al caricamento
        // effettivo da DB
        synchronized (this) {
          profRet = (ConfigurazioneProfilo) this.profili.get(idProfilo);
          if (profRet == null) {
            // A questo punto carico i dati del profilo
            try {
              if (!"".equals(idProfilo)) {
                DataColumnContainer imTmp = new DataColumnContainer(
                    gene.getSql(),
                    "W_PROFILI",
                    "select COD_PROFILO, COD_PROFILO, NOME, DESCRIZIONE, CODAPP, FLAG_INTERNO, COD_CLIENTE, CRC "
                        + "from W_PROFILI where COD_PROFILO = ?",
                    new Object[] { idProfilo });
                boolean ok = !(imTmp.getLong("W_PROFILI.CRC") == null || gene.getCRCProfilo(
                    imTmp, false) != imTmp.getLong("W_PROFILI.CRC").longValue());
                // Se non è ancora creato allora ne creo uno nuovo e lo carico
                profRet = new ConfigurazioneProfilo(idProfilo,
                    imTmp.getString("W_PROFILI.NOME"), ok);
                
                if (!ok) {
                	// Profilo corrotto. Log su
                	String msgErr = "Caricamento profilo con COD_PROFILO ='" + imTmp.getString("COD_PROFILO")
        	  		+ "': profilo corrotto a causa dell'errato valore del CRC nella tabella W_PROFILI del record. "
        	  		+ " CRC su DB = " + imTmp.getLong("W_PROFILI.CRC") + "; CRC calcolato = "
        	  		+ gene.getCRCProfilo(imTmp, false) + ".";
                	logger.error(msgErr);
                }

              } else {
                // Vado a caricare un profilo fittizio che contiene tutti i valori
                // di default definiti nella tabella W_AZIONI
                profRet = new ConfigurazioneProfilo(idProfilo, "Valori default",
                    true);
              }
              // Eseguo il carico del profilo
              gene.caricaProfilo(profRet);
              this.profili.put(idProfilo, profRet);
            } catch (GestoreException e) {
              logger.error(
                  "Errore durante l'estrazione dei dati del profilo con codice "
                      + idProfilo, e);
            }
          }
        }
      }
    }
    return profRet;
  }

  /**
   * Funzione che esegue l'eliminazione del profilo che viene caricato alla
   * prima richiesta. Questa funzione deve essere lanciata dopo la modifica di
   * un profilo per ricaricare tutti i suoi dati correttamente
   * 
   * @param idProfilo
   */
  public void remove(String idProfilo) {
    this.profili.remove(idProfilo);
  }

  /**
   * Funzione che rimuove tutti i profili caricati
   * 
   */
  public void removeAll() {
    this.profili.clear();

  }

}
