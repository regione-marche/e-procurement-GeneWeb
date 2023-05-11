/*
 * Created on 11/giu/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.bl.integrazioni;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import it.eldasoft.gene.bl.SqlManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;


public class QformManager {

  private final static String selectGenereQformlib         = "select genere from qformlib where id =?";
  private final static String selectGenereQform            = "select genere from qformlib l,qform q where q.id =? and q.idmodello = l.id";

  private SqlManager          sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public HashMap<String,Object> getJsonModello(Long id, String entita, Long idPreview) throws SQLException {

    HashMap<String,Object> ret = new HashMap<String,Object>();
    String msg="";
    boolean esito = true;
    List<?> listaParametri = null;
    try {
      listaParametri = this.gelListaVariabili(id, entita, idPreview);
    } catch (SQLException e) {
      esito = false;
      msg = e.getMessage();
    }

    JSONObject jsonDatiRet = new JSONObject();
    try {
      if(listaParametri!=null && listaParametri.size()>0) {
        String  chiave = null;
        String valore = null;
        String valarray = null;
        Long numeroParametriVariabile = null;
        JSONObject jsonVariabileSingola = null;
        JSONArray jsonVariabileMultipla = null;
        JSONArray jsonVariabili = new JSONArray();
        String nomeVariabileRipetuta=null;

        int contatoreVariabileRipetuta =0;
        for(int i=0; i<listaParametri.size();i++) {
          chiave = SqlManager.getValueFromVectorParam(listaParametri.get(i), 0).getStringValue();
          valore = SqlManager.getValueFromVectorParam(listaParametri.get(i), 1).getStringValue();
          valarray = SqlManager.getValueFromVectorParam(listaParametri.get(i), 2).getStringValue();
          numeroParametriVariabile = SqlManager.getValueFromVectorParam(listaParametri.get(i), 3).longValue();

          if(new Long(1).equals(numeroParametriVariabile)) {
            nomeVariabileRipetuta=null;
            contatoreVariabileRipetuta =0;
            jsonVariabileSingola = new JSONObject();
            jsonVariabileSingola.put("name", chiave);
            jsonVariabileMultipla = new JSONArray();
            popolaValori(jsonVariabileMultipla,valore,valarray);
            jsonVariabileSingola.put("values", jsonVariabileMultipla);
            jsonVariabili.add(jsonVariabileSingola);
          }else {
            contatoreVariabileRipetuta++;
            if(contatoreVariabileRipetuta == 1) {
              nomeVariabileRipetuta = chiave;
              jsonVariabileMultipla = new JSONArray();
            }
            popolaValori(jsonVariabileMultipla,valore,valarray);
            if(contatoreVariabileRipetuta == numeroParametriVariabile.longValue()) {
              jsonVariabileSingola = new JSONObject();
              jsonVariabileSingola.put("name", nomeVariabileRipetuta);
              jsonVariabileSingola.put("values", jsonVariabileMultipla);
              jsonVariabili.add(jsonVariabileSingola);
            }
          }

        }
        jsonDatiRet.put("sysVariables", jsonVariabili);
      }
    }catch (Exception e) {
      esito = false;
      msg = e.getMessage();
    }

    //Lettura del json salvato come stringa nel campo oggetto
    String oggetto = "";
    if(esito) {
      String select="select oggetto from " + entita + " where id=?";

      try {
        oggetto=(String)this.sqlManager.getObject(select, new Object[] {id});
        if(oggetto !=null && !"".equals(oggetto)){
          //Conversione in json della stringa
          JSONObject jsonOggetto = (JSONObject)JSONSerializer.toJSON(oggetto);
          oggetto = ((JSONObject)jsonOggetto.get("survey")).toString();
          jsonDatiRet.put("survey",oggetto);
        }

       } catch (Exception e) {
        esito = false;
        msg = e.getMessage();
      }
    }
    ret.put("esito", new Boolean(esito));
    ret.put("jsonDati", jsonDatiRet);
    ret.put("messaggio", msg);
    return ret;
  }

  /**
   * Viene valorizzato il json con valore. Nel caso in cui isArray valga "1", allora la stringa valore
   * va interpretata come una stringa di valori separati da "," quindi si dovrà popolare il json con
   * i singoli valori
   * @param json
   * @param valore
   * @param isArray
   */
  private void popolaValori(JSONArray json, String valore, String isArray) {
    if("1".equals(isArray)) {
      String stringaTmp [] = valore.split(",");
      if(stringaTmp!=null && stringaTmp.length>0) {
        for(int j=0;j<stringaTmp.length;j++) {
          json.add(stringaTmp[j]);
        }
      }
    }else {
      json.add(valore);
    }

  }

  /**
   * Viene caricata la lista dei parametri.
   * Se ancora la preview non è stata creata, i parametri vengono presi da QFORMLIB, applicando il filtro per il genere del modello
   * Se la preview è già stata creata, i parametri vengono presi da QFORMCONFI
   * @param id
   * @param entita
   * @param idPreview
   * @return List<?>
   * @throws SQLException
   */
  private List<?> gelListaVariabili(Long id, String entita, Long idPreview) throws SQLException {
    List<?> listaParametri = null;
    String selectParametri = "";
    Object par[]=null;

    if(idPreview!=null) {
      selectParametri = "select q.chiave, q.valore, q.valarray,(select count(q1.chiave) from qformconfitemp q1 where q1.chiave=q.chiave and q1.idpreview=q.idpreview) "
          + "from qformconfitemp q where q.idpreview = ? order by q.chiave";
      par= new Object[] {new Long(idPreview)};
    }else {
      Long genere=this.getGenere(id, entita);

      selectParametri = "select q.chiave, q.valore, q.valarray, (select count(q1.chiave) from qformconfi q1 where q1.chiave=q.chiave) from qformconfi q "
          + "where (q.genere=? or q.genere is null) order by q.chiave";

      par=new Object[] {genere};
    }

    listaParametri = this.sqlManager.getListVector(selectParametri, par);

    return listaParametri;
  }

  /**
   * Metodo per la lettura del genere da QFORM o QFORMLIB
   * @param id
   * @param entita
   * @return Long
   * @throws SQLException
   */
  public Long getGenere(Long id, String entita) throws SQLException {
    Long genere=null;
    if("QFORMLIB".equals(entita)) {
      genere=(Long)sqlManager.getObject(selectGenereQformlib, new Object[] {id});
    }else {
      genere=(Long)sqlManager.getObject(selectGenereQform, new Object[] {id});
    }
    return genere;
  }

}
