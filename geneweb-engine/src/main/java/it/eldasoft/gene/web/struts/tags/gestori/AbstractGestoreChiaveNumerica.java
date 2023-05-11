/*
 * Created on 08/mar/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import java.sql.SQLException;
import java.util.Vector;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per il salvataggio dei dati di un'entità la cui chiave è costituita
 * da almeno un campo numerico ed incrementale (gestito all'atto
 * dell'inserimento mediante determinazione del MAX + 1 sulla tabella), ed
 * eventuali altri campi nel qual caso l'entità sia figlia di un'entità padre,
 * ed in tal caso all'atto dell'inserimento i valori di tali campi rimangono
 * fissi e viene incrementata solo la chiave numerica.
 * 
 * @author Marco.Franceschin
 * @author Stefano.Sabbadin
 */
public abstract class AbstractGestoreChiaveNumerica extends
    AbstractGestoreEntita {

  /**
   * @return nome del campo chiave numerica da ricalcolare in fase di
   *         inserimento
   */
  public abstract String getCampoNumericoChiave();

  /**
   * @return elenco delle eventuali altre chiavi oltre la chiave numerica che
   *         compongono la chiave dell'entità, null se la chiave è composta da
   *         un solo campo numerico incrementale
   */
  public abstract String[] getAltriCampiChiave();

  /**
   * Costruttore di default
   */
  public AbstractGestoreChiaveNumerica(){
    super();
  }
  
  /**
   * Costruttore dell'oggetto con possibilita' di scegliere se creare un gestore
   * di entita standard o meno, attraverso il parametro isGestoreStandard.
   * 
   * La differenza tra gestore standard e non sta nel fatto che il primo
   * gestisce interamente le operazioni di insert/update/delete dei dati di
   * un'entita' e dei campi del generatore attributi associati all'entita' di
   * partenza, mentre il secondo e' un gestore di appoggio usato per preparare i
   * dati presenti nelle schede con sezioni dinamiche. Tali dati preparati
   * devono essere passati poi ad un gestore standard per le operazioni di
   * insert/update/delete.
   * 
   * @param isGestoreStandard
   */
  public AbstractGestoreChiaveNumerica(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }
  
  /**
   * Prima dell'inserimento calcolo la chiave
   */
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    StringBuffer where = new StringBuffer("");
    StringBuffer select = new StringBuffer("");
    int numeroAltriCampiChiave = 0;
    if (this.getAltriCampiChiave() != null)
      numeroAltriCampiChiave = this.getAltriCampiChiave().length;
    Object param[] = new Object[numeroAltriCampiChiave];
    // Creo la where
    for (int i = 0; i < numeroAltriCampiChiave; i++) {
      if (i > 0) where.append(" and ");
      where.append(this.getAltriCampiChiave()[i]);
      where.append(" = ?");
      param[i] = impl.getObject(this.getEntita()
          + "."
          + this.getAltriCampiChiave()[i]);
    }
    select.append("select max(");
    select.append(this.getCampoNumericoChiave());
    select.append(") from ");
    select.append(this.getEntita());
    if (where.length() > 0) {
      select.append(" where ");
      select.append(where);
    }
    
    try {
      Vector ret = this.getSqlManager().getVector(select.toString(), param);
      Long max = SqlManager.getValueFromVectorParam(ret, 0).longValue();
      if (max == null) max = new Long(0);
      // Se non esiste la colonna la inserisco
      if (!impl.isColumn(this.getEntita() + "." + this.getCampoNumericoChiave()))
        impl.addColumn(this.getEntita() + "." + this.getCampoNumericoChiave(),
            JdbcParametro.TIPO_NUMERICO);
      impl.setValue(this.getEntita() + "." + this.getCampoNumericoChiave(),
          new Long(max.longValue() + 1));
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nel calcolo del massimo valore per il campo "
              + this.getEntita()
              + "."
              + this.getCampoNumericoChiave(), "calcoloNumerico", e);
    }
  }

}
