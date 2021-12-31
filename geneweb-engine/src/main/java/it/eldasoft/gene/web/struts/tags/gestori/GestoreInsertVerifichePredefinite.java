/*
 * Created on 29/01/2020
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
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per inserire le
 * pubblicazioni predefinite per il bando e per l'esito
 *
 * @author Cristian.Febas
 */
public class GestoreInsertVerifichePredefinite extends
    AbstractGestoreEntita {

  public GestoreInsertVerifichePredefinite() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
  }

  @Override
  public String getEntita() {
    return "IMPR";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager", this.getServletContext(),
        GenChiaviManager.class);

    String codimp = datiForm.getString("CODIMP");
    Long tipimp = new Long(1);//default tipo impresa vale 1
    String ufficioIntestatario=null;
    String cfein = null;
    HttpSession session = this.getRequest().getSession();
    if ( session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
      try {
        cfein = (String) this.geneManager.getSql().getObject(
            "select cfein from uffint where codein=?", new Object[] {ufficioIntestatario});
        cfein = UtilityStringhe.convertiNullInStringaVuota(cfein);
        if("".equals(cfein)){
          throw new GestoreException("Errore durante l'estrazione del codice fiscale dell'ufficio intestatario",null);
        }

        tipimp = (Long) this.sqlManager.getObject("select tipimp from impr where codimp = ?",new Object[] { codimp });

      } catch (SQLException e) {
        throw new GestoreException("Errore durante l'estrazione del codice fiscale dell'ufficio intestatario",null, e);
      }
    }
      //G_z24 verifiche in dipendenza dal tipo di impresa
      String sqlRCpredefinite = "select tab2cod, tab2tip, tab2d1, tab2d2 from tab2 where tab2cod = ? order by tab2tip asc";
      List listaTabellati = null;
      try {
        listaTabellati = this.sqlManager.getListVector(sqlRCpredefinite,new Object[] { "G_z24" });
        //filtro sul tipo impresa


        for (int i = 0; i < listaTabellati.size(); i++) {
          Boolean tipoImprFounded = false;
          String tipRC = SqlManager.getValueFromVectorParam(listaTabellati.get(i), 1).getStringValue();
          String tipologieImpreseStr = SqlManager.getValueFromVectorParam(listaTabellati.get(i), 2).getStringValue();
          tipologieImpreseStr = UtilityStringhe.convertiNullInStringaVuota(tipologieImpreseStr);
          if("".equals(tipologieImpreseStr)){
            tipoImprFounded = true;
          }else{
            String[] tipologieImpreseArray = tipologieImpreseStr.split(";");
            for (int k = 0; k < tipologieImpreseArray.length; k++){
              String tipologiaImpresa= tipologieImpreseArray[k];
              if(tipimp.equals(new Long(tipologiaImpresa))){
                tipoImprFounded = true;
              }
            }
          }
          if(tipoImprFounded.equals(true)){
            //ricerco i default per ciascuna RC predefinita:
            String sqlRCdefault = "select tab2d1, tab2d2 from tab2 where tab2cod = ? and tab2tip = ?";
            String defGGvaliditaStr = null;
            String defGGavvscadStr = null;
            Vector<?> rcDefaultVect = this.sqlManager.getVector(sqlRCdefault,new Object[] { "G_z25",tipRC });
            if(rcDefaultVect!=null){
              defGGvaliditaStr = ((JdbcParametro) rcDefaultVect.get(0)).getStringValue();
              defGGavvscadStr = ((JdbcParametro) rcDefaultVect.get(1)).getStringValue();
            }
            defGGvaliditaStr = UtilityStringhe.convertiNullInStringaVuota(defGGvaliditaStr);
            defGGavvscadStr = UtilityStringhe.convertiNullInStringaVuota(defGGavvscadStr);
            Vector elencoCampi = new Vector();
            int idVerifica = genChiaviManager.getNextId("VERIFICHE");
            elencoCampi.add(new DataColumn("VERIFICHE.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idVerifica)));
            elencoCampi.add(new DataColumn("VERIFICHE.CONTESTO_VERIFICA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "ART80")));
            elencoCampi.add(new DataColumn("VERIFICHE.CODIMP", new JdbcParametro(JdbcParametro.TIPO_TESTO, codimp)));
            elencoCampi.add(new DataColumn("VERIFICHE.CFEIN", new JdbcParametro(JdbcParametro.TIPO_TESTO, cfein)));
            String tipoVerificaStr= SqlManager.getValueFromVectorParam(listaTabellati.get(i), 1).getStringValue();
            elencoCampi.add(new DataColumn("VERIFICHE.TIPO_VERIFICA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,new Long(tipoVerificaStr))));
            if(!"".equals(defGGvaliditaStr)){
              elencoCampi.add(new DataColumn("VERIFICHE.GG_VALIDITA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(defGGvaliditaStr))));
            }
            if(!"".equals(defGGavvscadStr)){
              elencoCampi.add(new DataColumn("VERIFICHE.GG_AVVISO_SCADENZA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(defGGavvscadStr))));
            }
            elencoCampi.add(new DataColumn("VERIFICHE.IS_SILENZIO_ASSENSO", new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));
            elencoCampi.add(new DataColumn("VERIFICHE.STATO_VERIFICA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1))));

            DataColumnContainer container = new DataColumnContainer(elencoCampi);
            container.insert("VERIFICHE", sqlManager);

          }

        }


      } catch (SQLException e) {
            throw new GestoreException("Errore durante l'inserimento delle verifiche predefinite",null, e);
      }

    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    this.getRequest().setAttribute("verificheInserite", "1");
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
