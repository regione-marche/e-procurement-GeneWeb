/*
 * Created on 11/dic/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.utils.spring.UtilitySpring;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per il salvataggio dei dati di un'entità la cui chiave &egrave; costituita
 * da un solo campo numerico incrementante da assegnare mediante prelevamento del valore in W_GENCHIAVI.
 * In questo modo gli inserimenti in concorrenza non danno alcun problema in quanto ogni transazione utilizza un id di inserimento.
 *
 * @author Stefano.Sabbadin
 */
public abstract class AbstractGestoreChiaveIDAutoincrementante extends
    AbstractGestoreEntita {

  /**
   * @return nome del campo chiave numerica da ricalcolare in fase di
   *         inserimento
   */
  public abstract String getCampoNumericoChiave();

  /**
   * Costruttore di default
   */
  public AbstractGestoreChiaveIDAutoincrementante(){
    super();
  }

  /**
   * Prima dell'inserimento calcolo la chiave incrementante.
   */
  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);
    try {

    int id = genChiaviManager.getNextId(this.getEntita());
    // Se non esiste la colonna la inserisco
    if (!impl.isColumn(this.getEntita() + "." + this.getCampoNumericoChiave()))
      impl.addColumn(this.getEntita() + "." + this.getCampoNumericoChiave(),
          JdbcParametro.TIPO_NUMERICO);
    impl.setValue(this.getEntita() + "." + this.getCampoNumericoChiave(),
        new Long(id));

    } catch (DataAccessException e) {
      throw new GestoreException(
          "Errore nel calcolo del nuovo id da usare per l'inserimento in "
              + this.getEntita()
              + "."
              + this.getCampoNumericoChiave(), "calcoloIdAutoincrementante", e);
    }
  }

}
