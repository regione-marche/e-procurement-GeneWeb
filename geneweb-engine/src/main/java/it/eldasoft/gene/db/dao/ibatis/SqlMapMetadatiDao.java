/*
 * Created on 28-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao.ibatis;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.MetadatiDao;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * @author Stefano.Sabbadin
 */
public class SqlMapMetadatiDao extends SqlMapClientDaoSupportBase implements
    MetadatiDao {

  /** Tabellato con l'elenco degli schemi */
  private static final String TABELLATO_SCHEMI         = "G_j00";

  /** Filtro per il tipo di riga in c0entit, che individua una tabella */
  private static final String FILTRO_TIPO_PER_RICERCHE = "E";

  /**
   * Filtro per il tipo di tabelle/campi configurabili da profili, ma non
   * visibili nelle ricerche
   */
  private static final String FILTRO_TIPO_PER_PROFILI  = "P";

  /**
   * @see it.eldasoft.gene.db.dao.MetadatiDao#getElencoSchemi()
   */
  public List<?> getElencoSchemi() throws DataAccessException {
    return getSqlMapClientTemplate().queryForList("getElencoSchemi",
        TABELLATO_SCHEMI);
  }

  /**
   * @see it.eldasoft.gene.db.dao.MetadatiDao#getElencoNomiFisiciTabelle()
   */
  public List<?> getElencoNomiFisiciTabelle() throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("filtroJoinElda", "%#%");
    ArrayList<String> listaTipi = new ArrayList<String>();
    listaTipi.add(FILTRO_TIPO_PER_RICERCHE);
    listaTipi.add(FILTRO_TIPO_PER_PROFILI);
    hash.put("listaFiltriTipo", listaTipi);

    return getSqlMapClientTemplate().queryForList("getElencoNomiFisiciTabelle",
        hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.MetadatiDao#getTabella(java.lang.String)
   */
  public Tabella getTabella(String nomeFisico) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("nomeFisicoTabella", nomeFisico);
    ArrayList<String> listaTipi = new ArrayList<String>();
    listaTipi.add(FILTRO_TIPO_PER_RICERCHE);
    listaTipi.add(FILTRO_TIPO_PER_PROFILI);
    hash.put("listaFiltriTipo", listaTipi);

    return (Tabella) getSqlMapClientTemplate().queryForObject("getTabella",
        hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.MetadatiDao#getElencoChiaviEsterneReferenti(java.lang.String)
   */
  public List<?> getElencoChiaviEsterneReferenti(String nomeFisico)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("nomeFisicoTabella", this.convertiValoreConEscape(hash,
        nomeFisico, "escapeNomeFisicoTabella")
        + "%");

    return getSqlMapClientTemplate().queryForList(
        "getElencoChiaviEsterneReferenti", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.MetadatiDao#getElencoCampiTabella(java.lang.String)
   */
  public List<?> getElencoCampiTabella(String nomeFisico)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("nomeFisico", "%."
        + this.convertiValoreConEscape(hash, nomeFisico, "escapeNomeFisico"));
    hash.put("formato1", "NO_EDIT");
    hash.put("formato2", "NOEDIT");
    ArrayList<Object> listaTipi = new ArrayList<Object>();
    listaTipi.add(FILTRO_TIPO_PER_RICERCHE);
    listaTipi.add(FILTRO_TIPO_PER_PROFILI);
    hash.put("listaFiltriTipo", listaTipi);
    return getSqlMapClientTemplate().queryForList("getElencoCampiTabella", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.MetadatiDao#getElencoMnemonici(java.lang.String,
   *      java.lang.String)
   */
  public List<?> getElencoMnemoniciPerRicerche(String mnemonico,
      String operatoreMnemonico, String descrizione, String operatoreDescrizione)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();

    String comandoEscape = null;
    try {
      SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
      comandoEscape = composer.getEscapeSql();
      hash.put("operatoreUpper", composer.getFunzioneUpperCase());
      
    } catch (SqlComposerException e) {
      // non si verifica mai, il caricamento metadati gia' testa che la
      // property sia settata correttamente
    }

    hash.put("filtroTipo", FILTRO_TIPO_PER_RICERCHE);
    hash.put("formato1", "NO_EDIT");
    hash.put("formato2", "NOEDIT");
    // this.setOperatoreMatch(hash, mnemonico, "operatoreMnemonico");
    hash.put("operatoreMnemonico", operatoreMnemonico);

    if (OPERATORE_IDENTITA.equals(operatoreMnemonico))
      hash.put("escapeMnemonico", null);
    else
      hash.put("escapeMnemonico", comandoEscape);

    if (mnemonico != null)
      hash.put("mnemonico", mnemonico.toUpperCase());
    else
      hash.put("mnemonico", null);

    // this.setOperatoreMatch(hash, descrizione, "operatoreDescrizione");
    hash.put("operatoreDescrizione", operatoreDescrizione);

    if (OPERATORE_IDENTITA.equals(operatoreDescrizione))
      hash.put("escapeDescrizione", null);
    else
      hash.put("escapeDescrizione", comandoEscape);

    if (descrizione != null)
      hash.put("descrizione", descrizione.toUpperCase());
    else
      hash.put("descrizione", null);

    return getSqlMapClientTemplate().queryForList(
        "getElencoMnemoniciPerRicerche", hash);
  }

  public String getC0eKeyById(String idC0entit) throws DataAccessException {
    Object tmp = this.getSqlMapClientTemplate().queryForObject("getC0ekeyById",
        idC0entit);
    if (tmp != null)
      return (String) tmp;
    else
      return null;
  }

}