/*
 * Created on 13-ott-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che verifica se, tra i campi visibili di una pagina a scheda del
 * generatore attributi, esistono campi che utilizzano regole di
 * visualizzazione, ed in tal caso valida le regole in modo da determinare se
 * esiste almeno un campo da rendere visibile nella pagina.<br>
 * Questa validazione avviene esclusivamente se si parte dall'estrazione dei
 * dati del generatore attributi per un'entità esistente, e non ha senso nel
 * caso di creazione di una nuova entità.<br>
 * <br>
 * <b>NOTA BENE: l'algoritmo funziona con regole di visualizzazione definite per
 * un'entità a partire SEMPRE dallo stesso discriminante</b>
 * 
 * @author stefano.sabbadin
 */
public class RegoleVisualizzazioneAttributiFunction extends AbstractFunzioneTag {

  /**
   * Costruttore
   */
  public RegoleVisualizzazioneAttributiFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  /*
   * @see it.eldasoft.gene.tags.utils.AbstractFunzioneTag#function(javax.servlet.jsp.PageContext,
   *      java.lang.Object[])
   */
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String esito = "false";

    if (pageContext != null) {
      String chiavePadre = (String) params[2];
      // si eseguono le verifiche solo se l'entità padre esiste, ovvero NON
      // siamo in creazione
      if (!"".equals(chiavePadre)) {

        SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
            "sqlManager", pageContext, SqlManager.class);

        String entitaPadre = (String) params[1];
        String entitaFiglia = "X" + entitaPadre;

        // STEP 1: verifico se esistono per l'entità padre campi da visualizzare
        // nella scheda: se non ne esistono, allora non visualizzo la pagina
        // (esco con il default = false)
        StringBuffer sql = new StringBuffer("");
        sql.append("select count(1) ");
        sql.append("from DYNCAM ");
        sql.append("where DYNENT_NAME = ? and DYNENT_TYPE = 2 and DYNCAM_SCH = 1");
        Long numeroAttributi = null;
        try {
          numeroAttributi = (Long) sqlManager.getObject(sql.toString(),
              new Object[] { entitaFiglia });
        } catch (SQLException e) {
          throw new JspException(
              "Errore durante l'estrazione del numero di campi del generatore attributi per la tabella \""
                  + entitaFiglia
                  + "\" e visibili in una scheda", e);
        }

        if (numeroAttributi.intValue() > 0) {
          // STEP 2: se esistono elementi, verifico se alcuni di questi hanno
          // regole di visualizzazione applicate
          sql = new StringBuffer("");
          sql.append("select count(1) ");
          sql.append("from DYNCAM ");
          sql.append("where DYNENT_NAME = ? and DYNENT_TYPE = 2 and DYNCAM_SCH = 1 and DYNCAM_FNF is not null");
          Long numeroAttributiConRegole = null;
          try {
            numeroAttributiConRegole = (Long) sqlManager.getObject(
                sql.toString(), new Object[] { entitaFiglia });
          } catch (SQLException e) {
            throw new JspException(
                "Errore durante l'estrazione del numero di campi del generatore attributi per la tabella \""
                    + entitaFiglia
                    + "\", visibili in una scheda e con una regola di visualizzazione applicata",
                e);
          }

          // se ci sono attributi da visualizzare senza regole, allora
          // esiste almeno un campo visibile in modo indiscriminato, altrimenti
          // occorre procedere con ulteriori step
          if ((numeroAttributi.intValue() - numeroAttributiConRegole.intValue()) > 0)
            esito = "true";
          else {
            // STEP 3: visto che ci sono delle regole applicate, occorre capire
            // qual è il discriminante
            sql = new StringBuffer("");
            sql.append("select distinct DYNCAM_FNF ");
            sql.append("from DYNCAM ");
            sql.append("where DYNENT_NAME = ? and DYNENT_TYPE = 2 and DYNCAM_SCH = 1 and DYNCAM_FNF is not null");
            String discriminante;
            try {
              discriminante = (String) sqlManager.getObject(sql.toString(),
                  new Object[] { entitaFiglia });
            } catch (SQLException e) {
              throw new JspException(
                  "Errore durante l'estrazione del discriminante per i campi del generatore attributi per la tabella \""
                      + entitaFiglia
                      + "\"", e);
            }

            // STEP 4: si estrae il valore del discriminante per l'entità
            // principale
            DataColumnContainer dcc = new DataColumnContainer(chiavePadre);
            String nomeCampoChiave = null;
            DataColumn dc = null;
            Vector paramsStep4 = new Vector();
            boolean appendAnd = false;

            // si costruisce la query per estrarre il discriminante a partire
            // dalla chiave dell'entità principale
            sql = new StringBuffer("");
            sql.append("select ").append(discriminante).append(" ");
            sql.append("from ").append(entitaPadre).append(" ");
            sql.append("where ");
            for (Iterator it = dcc.getColonne().keySet().iterator(); it.hasNext();) {
              if (appendAnd)
                sql.append("and ");
              else
                appendAnd = true;
              nomeCampoChiave = (String) it.next();
              try {
                dc = dcc.getColumn(nomeCampoChiave);
              } catch (GestoreException e) {
              }
              sql.append(nomeCampoChiave).append(" = ? ");
              paramsStep4.add(dc.getValue().getValue());
            }
            String valoreDiscriminante = null;
            try {
              valoreDiscriminante = SqlManager.getValueFromVectorParam(
                  sqlManager.getVector(sql.toString(),
                      paramsStep4.toArray(new Object[0])), 0).getStringValue();
            } catch (SQLException e) {
              throw new JspException(
                  "Errore durante l'estrazione del valore del discriminante per i campi del generatore attributi "
                      + "per il record con chiave ("
                      + chiavePadre
                      + ") per la tabella \""
                      + entitaPadre
                      + "\"", e);
            }

            // STEP 5: si estraggono le regole di visualizzazione
            sql = new StringBuffer("");
            sql.append("select distinct DYNCAM_OP, DYNCAM_VAL, DYNCAM_VAL_S ");
            sql.append("from DYNCAM ");
            sql.append("where DYNENT_NAME = ? and DYNENT_TYPE = 2 and DYNCAM_SCH = 1 and DYNCAM_FNF is not null");
            try {
              List elencoRegole = sqlManager.getListVector(sql.toString(),
                  new Object[] { entitaFiglia });

              // STEP 6: se esiste almeno una regola di visualizzazione
              // valutata positivamente, allora si interrompono i controlli e si
              // termina con "true" che indica che esiste almeno un campo le cui
              // regole consentono la visualizzazione
              String operatore = null;
              String valoreSingolo = null;
              String valoreMultiplo = null;
              for (int i = 0; i < elencoRegole.size(); i++) {
                operatore = SqlManager.getValueFromVectorParam(
                    elencoRegole.get(i), 0).getStringValue();
                valoreSingolo = SqlManager.getValueFromVectorParam(
                    elencoRegole.get(i), 1).getStringValue();
                valoreMultiplo = SqlManager.getValueFromVectorParam(
                    elencoRegole.get(i), 2).getStringValue();

                if (isRegolaAccettata(valoreDiscriminante, operatore,
                    valoreSingolo, valoreMultiplo)) {
                  // alla prima regola accettata interrompo in quanto
                  // sicuramente ho dei campi che andranno visualizzati
                  esito = "true";
                  break;
                }

              }

            } catch (SQLException e) {
              throw new JspException(
                  "Errore durante l'estrazione delle regole di visualizzazione dei dati del generatore attributi per la tabella \""
                      + entitaFiglia
                      + "\"", e);
            }
          }
        }
      }
    }

    return esito;
  }

  /**
   * Verifica se il valore del discriminante soddisfa la regola di
   * visualizzazione individuata dall'operatore e dai suoi valori (singolo
   * oppure multiplo)<br>
   * <br>
   * Il presente algoritmo rappresenta la traduzione Java dell'algoritmo
   * Javascript realizzato nella pagina js-attributi-generici.jsp
   * 
   * @param valoreDiscriminante
   *        valore del discriminante
   * @param operatore
   *        operatore di confronto
   * @param valoreSingolo
   *        valore accettato per validare la regola
   * @param valoreMultiplo
   *        elenco di valori, separati da ",", per validare le regole IN e NOT
   *        IN
   * @return true se la regola è validata per il discriminante, false altrimenti
   */
  private boolean isRegolaAccettata(String valoreDiscriminante,
      String operatore, String valoreSingolo, String valoreMultiplo) {
    boolean esito = false;

    if (!"".equals(valoreDiscriminante)) {
      // controllo se la regola è di confronto con un valore numerico
      if ("".equals(valoreMultiplo)) {
        // Valore numerico
        if ("=".equals(operatore)) {
          esito = Integer.parseInt(valoreDiscriminante) == Integer.parseInt(valoreSingolo);
        } else if ("<>".equals(operatore)) {
          esito = Integer.parseInt(valoreDiscriminante) != Integer.parseInt(valoreSingolo);
        } else if ("<".equals(operatore)) {
          esito = Integer.parseInt(valoreDiscriminante) < Integer.parseInt(valoreSingolo);
        } else if ("<=".equals(operatore)) {
          esito = Integer.parseInt(valoreDiscriminante) <= Integer.parseInt(valoreSingolo);
        } else if (">".equals(operatore)) {
          esito = Integer.parseInt(valoreDiscriminante) > Integer.parseInt(valoreSingolo);
        } else if (">=".equals(operatore)) {
          esito = Integer.parseInt(valoreDiscriminante) >= Integer.parseInt(valoreSingolo);
        }
      } else {
        if ("IN".equals(operatore) || "NOT IN".equals(operatore)) {
          // Caso di confronto con un set di valori di un tabellato numerico
          String[] setValido = valoreMultiplo.split(",");
          int pos = -1;
          for (int i = 0; i < setValido.length; i++) {
            if (valoreDiscriminante.equals(setValido[i])) {
              pos = i;
              break;
            }
          }
          if ("IN".equals(operatore)) {
            esito = pos >= 0;
          } else {
            esito = pos < 0;
          }
        } else {
          // Valore stringa: si cerca per posizione in modo da cercare anche
          // elenchi di valori separati da ","
          int pos = valoreMultiplo.indexOf(valoreDiscriminante);
          if ("=".equals(operatore)) {
            esito = pos >= 0;
          } else {
            esito = pos < 0;
          }
        }
      }
    }

    return esito;
  }
}