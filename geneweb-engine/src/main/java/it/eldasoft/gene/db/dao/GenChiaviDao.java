/*
 * Created on 07-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati della tabella di memorizzazione dei del
 * campo ID_**** delle tabelle relative al DB AliceWeb.
 *
 * @author Luca.Giacomazzo
 */
public interface GenChiaviDao {

  int getNextId(String tabella, int numIdAllocati) throws DataAccessException;

  public int getMaxId(String tabella,String chiave, String condizioniExtra) throws DataAccessException;
}
