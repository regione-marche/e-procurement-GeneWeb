/*
 * Created on 23-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.spring;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.util.HashMap;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * Questa classe è la base di tutte le classi SqlMap*Dao da definire nella web
 * application. E' dotata di un metodo protetto che effettua il set :
 * <ul>
 * <li>sia scaduta la sessione</li>
 * <li>non sia stata acquistata dal cliente l'opzione della funzionalità
 * associata all'azione</li>
 * <li>l'utente non risulta abilitato alla funzionalità associata all'azione</li>
 * </ul>
 * <br>
 * Nel caso in cui i controlli vadano a buon fine, la richiesta viene
 * soddisfatta. Ogni azione che estende la presente necessita l'implementazione
 * del metodo {@link #runAction()}
 *
 * @author Luca Giacomazzo
 */
public abstract class SqlMapClientDaoSupportBase extends SqlMapClientDaoSupport {

  /**
   * Valore dell'operatore nel caso il valore discriminante contenga il
   * carattere '%'
   */
  private static final String OPERATORE_LIKE     = "LIKE";

  /**
   * Valore dell'operatore nel caso il valore discriminante non contenga il
   * carattere '%'
   */
  protected static final String OPERATORE_IDENTITA = "=";

  /**
   * Effettua il set nella HashMap di ingresso dell'operatore, associandogli
   * 'nomeOperatore' come chiave, a partire dall'argomento
   * 'valoreDiscriminante'. Se il valore discriminante contiene il carattere '%'
   * setta nella HashMap l'operatore pari a 'LIKE', altrimenti lo setta pari
   * '='. Se la stringa è null l'operatore non viene settato.
   *
   * @param hash
   *        istanza della HashMap contenente i parametri da passare al framework
   *        Spring
   * @param valoreDiscriminante
   *        stringa discriminante
   * @param nomeOperatore
   *        nome da assegnare alla chiave dell'oggetto inserito nella HashMap
   */
  protected void setOperatoreMatch(HashMap<String, String> hash, String valoreDiscriminante,
      String nomeOperatore) {
    if (valoreDiscriminante != null) {
      if (valoreDiscriminante.indexOf("%") != -1)
        hash.put(nomeOperatore, SqlMapClientDaoSupportBase.OPERATORE_LIKE);
      else
        hash.put(nomeOperatore, SqlMapClientDaoSupportBase.OPERATORE_IDENTITA);
    }
  }

  /**
   * Se il valore in input contiene dei caratteri speciali, ritorna la stringa
   * opportunamente modificata anteponendo ogni carattere speciale con il
   * carattere di escape, ed inserisce un elemento nella hash con il comando di
   * escape da appendere all'sql
   *
   * @param hash
   *        hash dei parametri da passare alla query iBatis
   * @param valore
   *        valore da testare se contiene caratteri speciali
   * @param nomeAttributoComandoEscape
   *        nome dell'attributo da aggiungere alla hash, con il comando di
   *        escape
   *
   * @return la stringa stessa se non contiene caratteri di escape (in tal caso
   *         la hash conterra' un c), altrimenti la stringa con il carattere di
   *         escape prima di ogni carattere speciale (e nella hash viene
   *         aggiunto il comando di escape con il nome di attributo in input)
   * @throws RuntimeException
   *         eccezione che wrappa SqlComposerException e che si verifica quando
   *         la property che indica il tipo di DB non è configurata
   *         correttamente, il che non dovrebbe verificarsi mai
   *
   * @since 1.4.6
   */
  protected String convertiValoreConEscape(HashMap<String, Object> hash, String valore,
      String nomeAttributoComandoEscape) {
    String risultato = valore;
    String comandoEscape = null;
    if (UtilityStringhe.containsSqlWildCards(valore)) {
      risultato = UtilityStringhe.escapeSqlString(valore);
      SqlComposer composer;
      try {
        composer = SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
      } catch (SqlComposerException e) {
        throw new RuntimeException(
            "Tipo di database non configurato correttamente nel file di configurazione",
            e);
      }
      comandoEscape = composer.getEscapeSql();
    }
    hash.put(nomeAttributoComandoEscape, comandoEscape);
    return risultato;
  }

}