/*
 * Created on Oct 30, 2006
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
import it.eldasoft.gene.db.dao.TabellaDao;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.springframework.dao.DataAccessException;

/**
 * @author cit_defilippis
 */
public class SqlMapTabellaDao extends SqlMapClientDaoSupportBase implements
    TabellaDao {

    public void copiaRecord(String nomeEntitaSorgente, String nomeEntitaDestinazione,
            String[] valoriCampiChiaveSorgente, String[] valoriCampiChiaveDestinazione)
                    throws DataAccessException{

    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    DizionarioCampi dizCampi = DizionarioCampi.getInstance();
    Tabella tabella = dizTabelle.getDaNomeTabella(nomeEntitaDestinazione);
    List<String> mnemoniciCampi = tabella.getMnemoniciCampi();

    List<String> listaNomiCampiChiave = new ArrayList<String>();
    List<String> listaValoriCampiChiaveDestinazione = new ArrayList<String>();

    for(int i=0; i < valoriCampiChiaveDestinazione.length; i++)
        listaValoriCampiChiaveDestinazione.add(valoriCampiChiaveDestinazione[i]);

    Vector<String> listaNomiCampi = new Vector<String>();
    Campo campo = null;
    String mnemonico = null;
    // ciclo per la lista dei campi
    for (int i = 0; i < mnemoniciCampi.size(); i++) {
      mnemonico = mnemoniciCampi.get(i);
      campo = dizCampi.get(mnemonico);
      if(campo.isCampoChiave() && listaNomiCampiChiave.size() < listaValoriCampiChiaveDestinazione.size())
        listaNomiCampiChiave.add(campo.getNomeCampo());
      else
        listaNomiCampi.add(campo.getNomeCampo());
    }

    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("tabellaSorgente", nomeEntitaSorgente);
    hash.put("tabellaDestinazione", nomeEntitaDestinazione);
    hash.put("listaNomiCampiChiave", listaNomiCampiChiave);
    hash.put("listaNomiCampi", listaNomiCampi);
    hash.put("listaValoriCampiChiaveDestinazione", listaValoriCampiChiaveDestinazione);

    // nome e valore dei campi chiave sorgenti
    for(int i=0; i < valoriCampiChiaveSorgente.length; i++){
        hash.put( "campoChiave" + (i+1), listaNomiCampiChiave.get(i));
        hash.put("valoreChiave" + (i+1), valoriCampiChiaveSorgente[i]);
    }

    this.getSqlMapClientTemplate().insert("copiaRecord", hash);
    }

  public void copiaRecord(String nomeEntita, String[] valoriCampiChiaveSorgente,
        String[] valoriCampiChiaveDestinazione) throws DataAccessException{
    this.copiaRecord(nomeEntita, nomeEntita, valoriCampiChiaveSorgente,
            valoriCampiChiaveDestinazione);
  }

  public boolean isKeyUsed(String campoChiave,String valoreChiave, String tabella)
      throws DataAccessException {

    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("tabella", new String(tabella));
    hash.put("campoChiave", new String(campoChiave));
    hash.put("valoreChiave", new String(valoreChiave));
    Integer count = (Integer) getSqlMapClientTemplate().queryForObject(
        "getCountNprat", hash);

    boolean isUsed = false;
    if (count.intValue() > 0) isUsed = true;
    return isUsed;

  }

  public void updateCampo(String nomeCampo, Object valoreCampo,
        String[] valoriCampiChiave, String tabella) throws DataAccessException {

    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    DizionarioCampi dizCampi = DizionarioCampi.getInstance();
    Tabella tabella1 = dizTabelle.getDaNomeTabella(tabella);
    List<String> mnemoniciCampi = tabella1.getMnemoniciCampi();

    List<String> listaCampiChiave = new ArrayList<String>();
    Campo campo = null;
    String mnemonico = null;
    // ciclo per la lista dei campi
    for (int i = 0; i < mnemoniciCampi.size(); i++) {
      mnemonico = mnemoniciCampi.get(i);
      campo = dizCampi.get(mnemonico);
      if(campo.isCampoChiave() && listaCampiChiave.size() < valoriCampiChiave.length)
        listaCampiChiave.add(campo.getNomeCampo());
    }

    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("nomeCampo", new String(nomeCampo));
    hash.put("valoreCampo", valoreCampo);

    for(int i=0; i < listaCampiChiave.size(); i++){
        hash.put("campoChiave" + (i+1), listaCampiChiave.get(i));
        hash.put("valoreChiave" + (i+1), valoriCampiChiave[i]);
    }

    hash.put("tabella", new String(tabella));
    this.getSqlMapClientTemplate().update("updateCampo",hash);
  }

  public void deleteTabella(String tabella, String[] valoriCampiChiave,
        String condizioneSuppl) throws DataAccessException {

    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    DizionarioCampi dizCampi = DizionarioCampi.getInstance();
    Tabella tabella1 = dizTabelle.getDaNomeTabella(tabella);
    List<String> mnemoniciCampi = tabella1.getMnemoniciCampi();

    List<String> listaCampiChiave = new ArrayList<String>();
    Campo campo = null;
    String mnemonico = null;
    // ciclo per la lista dei campi
    for (int i = 0; i < mnemoniciCampi.size(); i++) {
      mnemonico = mnemoniciCampi.get(i);
      campo = dizCampi.get(mnemonico);
      if(campo.isCampoChiave() && listaCampiChiave.size() < valoriCampiChiave.length)
        listaCampiChiave.add(campo.getNomeCampo());
    }

    HashMap<String, Object> hash = new HashMap<String, Object>();
    for(int i=0; i < listaCampiChiave.size(); i++){
        hash.put("campoChiave" + (i+1), listaCampiChiave.get(i));
        hash.put("valoreChiave" + (i+1), valoriCampiChiave[i]);
    }
    hash.put("tabella", new String(tabella));
    if(condizioneSuppl != null && condizioneSuppl.length() > 0)
        hash.put("condizione", new String(condizioneSuppl));

    getSqlMapClientTemplate().delete("deleteTabella", hash);
  }

}