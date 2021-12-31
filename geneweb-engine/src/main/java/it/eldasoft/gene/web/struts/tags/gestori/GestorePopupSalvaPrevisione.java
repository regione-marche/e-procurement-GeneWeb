/*
 * Created on 22/05/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.scadenz.ScadenzariManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di submit della popup Salva previsione
 *
 * @author Marcello Caminiti
 */
public class GestorePopupSalvaPrevisione extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "G_SCADENZ";
  }

  public GestorePopupSalvaPrevisione() {
    super(false);
  }

  public GestorePopupSalvaPrevisione(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);

    ScadenzariManager scadenzariManager = (ScadenzariManager) UtilitySpring.getBean("scadenzariManager",
        this.getServletContext(), ScadenzariManager.class);


    String entitaPartenza = UtilityStruts.getParametroString(this.getRequest(),"entitaPartenza");
    String chiave = UtilityStruts.getParametroString(this.getRequest(),"chiave");
    String codapp = (String) this.getRequest().getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    String conteggioPrevisione = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),"conteggioPrevisione"));

    String whereChiavi="";
    // si esplode la chiave del record da cui partire
    Vector<JdbcParametro> campiChiave = UtilityTags.stringParamsToVector(chiave, null);
    Object[] valoriChiave = new Object[campiChiave.size()];
    for (int i=0; i<campiChiave.size(); i++) {
      valoriChiave[i]=campiChiave.get(i).getValue();
      whereChiavi += " key" + Integer.toString(i+1) + "='" + valoriChiave[i] + "' and ";
    }

    Long countPrevisone=null;
    if(conteggioPrevisione!=null)
      countPrevisone = new Long(conteggioPrevisione);

    if(countPrevisone!=null && countPrevisone.longValue()>0){
      String delete="delete from g_scadenz where " + whereChiavi + " ent='" + entitaPartenza+ "' and prev=1";
      try {
        sqlManager.update(delete, null);
      } catch (SQLException e) {
        throw new GestoreException("Errore durante la cancellazione delle attività con prev=1 e chiavi " + whereChiavi,null, e);
      }
    }

    //Clonazione delle attività
    try {
      scadenzariManager.insertClonazioneAttivitaScadenzario(codapp, entitaPartenza, valoriChiave, valoriChiave,true);
    } catch (SQLException e) {
      throw new GestoreException("Errore nella clonazione delle attivita" ,null, e);
    }

    //Ricalcolo dell'algoritmo
    try {
      scadenzariManager.updateDateScadenzarioEntita(entitaPartenza, valoriChiave, codapp, true, null);
    } catch (SQLException e) {
      throw new GestoreException("Errore durante il ricalcolo dei termini e scadenze della lista delle attività" ,null, e);
    }

    this.getRequest().setAttribute("salvataggioPrevisoneEseguito", "1");


  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}