package it.eldasoft.gene.db.dao;

import it.eldasoft.gene.db.domain.admin.Tecni;

/**
 * Interfaccia per i metodi di accesso alla tabella TECNI
 * @author gabriele.nencini
 *
 */
public interface TecniciDAO {
  public Tecni getTecniFullByPK(String codtec);
}
