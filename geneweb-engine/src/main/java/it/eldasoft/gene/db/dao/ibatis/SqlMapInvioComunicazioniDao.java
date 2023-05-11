/*
 * Created on 26/set/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao.ibatis;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.InvioComunicazioniDao;
import it.eldasoft.gene.db.domain.invcom.DestinatarioComunicazione;
import it.eldasoft.gene.db.domain.invcom.InvioComunicazione;
import it.eldasoft.gene.db.domain.invcom.ModelloComunicazione;
import it.eldasoft.gene.db.domain.invcom.PKInvioComunicazione;

import java.util.Date;
import java.util.HashMap;

/**
 * Classe di bridge verso ibatis per l'interfacciamento con la tabella W_INVCOM e tabelle figlie.
 *
 * @author Stefano.Sabbadin
 * @since 2.0.14
 */
public class SqlMapInvioComunicazioniDao extends SqlMapClientDaoSupportBase implements InvioComunicazioniDao {

  /**
   * @see it.eldasoft.gene.db.dao.InvioComunicazioniDao#updateStatoComunicazione(it.eldasoft.gene.db.domain.invcom.PKInvioComunicazione, java.lang.String)
   */
  public void updateStatoComunicazione(PKInvioComunicazione pk, String stato) {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idProgramma", pk.getIdProgramma());
    hash.put("idComunicazione", pk.getIdComunicazione());
    hash.put("stato", stato);
    getSqlMapClientTemplate().update("updateStatoComunicazione", hash, 1);
  }

  public void insertComunicazione(InvioComunicazione comunicazione) {
    if (comunicazione.getDataInserimento() == null) {
      comunicazione.setDataInserimento(new Date());
    }
    getSqlMapClientTemplate().insert("insertComunicazione", comunicazione);
  }

  public void insertDestinatarioComunicazione(DestinatarioComunicazione destinatario) {
    getSqlMapClientTemplate().insert("insertDestinatarioComunicazione", destinatario);
  }

  public void deleteDestinatariComunicazione(PKInvioComunicazione pk) {
    getSqlMapClientTemplate().delete("deleteDestinatariComunicazione", pk);
  }

  public ModelloComunicazione getModelloComunicazioneByGenere(int genere) {
    return (ModelloComunicazione) getSqlMapClientTemplate().queryForObject("getModelloComunicazioneByGenere", genere);
  }

}
