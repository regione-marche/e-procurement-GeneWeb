/*
 * Created on 26-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao.ibatis;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.db.dao.PermessiDao;
import it.eldasoft.gene.db.domain.permessi.PermessoEntita;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.utils.properties.ConfigManager;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella G_PERMESSI tramite iBatis.
 *
 * @author Luca.Giacomazzo
 */
public class SqlMapPermessiDao extends SqlMapClientDaoSupportBase implements
    PermessiDao {

  public List<?> getPermessiEntita(String campoChiave, String valoreChiave)
      throws DataAccessException{
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("campoChiave", campoChiave);
    hash.put("valoreChiave", valoreChiave);
    return this.getSqlMapClientTemplate().queryForList("getPermessiEntita",
        hash);
  }

  public PermessoEntita getPermessoEntitaByIdAccount(String campoChiave,
      String valoreChiave, int idAccount) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("campoChiave", campoChiave);
    hash.put("valoreChiave", valoreChiave);
    hash.put("idAccount", new Integer(idAccount));
    return (PermessoEntita) this.getSqlMapClientTemplate().queryForObject(
        "getPermessoEntitaByIdAccount", hash);
  }

  public List<?> getPermessiEntitaAccount(String campoChiave,
      String valoreChiave, int idAccount, String codiceUffint) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("campoChiave", campoChiave);
    hash.put("valoreChiave", valoreChiave);
    hash.put("idAccount", new Integer(idAccount));

    // Modifica per il database DB2: la query getModelli fa la UNION di diverse
    // select ed una di esse estrae un campo settandolo sempre a null. DB2 vuole
    // che tale valore sia tipizzato anche se null. In questo caso, si esegue
    // il cast al tipo di dato della colonna che si estrae nelle altre query in
    // union.
    if(it.eldasoft.utils.sql.comp.SqlManager.DATABASE_DB2.equals(
            ConfigManager.getValore(CostantiGenerali.PROP_DATABASE)))
      hash.put("adattaQueryPerDB2", "1");

    if(codiceUffint!=null)
      hash.put("codein", codiceUffint);

    return this.getSqlMapClientTemplate().queryForList(
        "getPermessiEntitaUtenti", hash);
  }

  public int deletePermessi(List<?> listaPermessiDaCancellare) throws DataAccessException{
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("listaPermessiDaCancellare", listaPermessiDaCancellare);
    return this.getSqlMapClientTemplate().delete("deletePermessiByNumPer",
        hash);
  }

  public void updatePermesso(PermessoEntita permessoEntita) throws DataAccessException{
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idPermesso", permessoEntita.getIdPermesso());
    hash.put("autorizzazione", permessoEntita.getAutorizzazione());
    hash.put("proprietario", permessoEntita.getProprietario());
    hash.put("ruolo", permessoEntita.getRuolo());

    this.getSqlMapClientTemplate().update("updatePermessoByNumPer", hash);
  }

  public void insertPermesso(PermessoEntita permessoEntita)
      throws DataAccessException{
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idPermesso", permessoEntita.getIdPermesso());
    hash.put("idAccount", permessoEntita.getIdAccount());
    hash.put("autorizzazione", permessoEntita.getAutorizzazione());
    hash.put("proprietario", permessoEntita.getProprietario());
    hash.put("campoChiave", permessoEntita.getCampoChiave());
    hash.put("valoreChiave", permessoEntita.getValoreChiave());
    hash.put("ruolo", permessoEntita.getRuolo());

    this.getSqlMapClientTemplate().insert("insertPermesso", hash);
  }

  public int getNumeroPermessiPredefiniti(Integer riferimento,Integer predefinito)
      throws DataAccessException{
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("riferimento", riferimento);
    hash.put("predefinito", predefinito);
    Integer numeroRecord = (Integer) this.getSqlMapClientTemplate().queryForObject(
        "getNumeroPermessiPredefiniti", hash);

    if(numeroRecord != null)
      return numeroRecord.intValue();
    else
      return 0;
  }

  public void insertPermessoPredefinito(PermessoEntita permessoEntita,
      Integer riferimento, Integer predefinito) throws DataAccessException{

    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idPermesso", permessoEntita.getIdPermesso());
    hash.put("idAccount", permessoEntita.getIdAccount());
    hash.put("autorizzazione", permessoEntita.getAutorizzazione());
    hash.put("proprietario", permessoEntita.getProprietario());
    hash.put("riferimento", riferimento);
    hash.put("predefinito", predefinito);

    this.getSqlMapClientTemplate().insert("insertPermessoPredefinito", hash);
  }

  public int deletePermessiPredefinitiByIdAccount(Integer riferimento,
      Integer predefinito) throws DataAccessException{
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("riferimento", riferimento);
    hash.put("predefinito", predefinito);

    return this.getSqlMapClientTemplate().delete("deletePermessiPredefiniti", hash);
  }

}