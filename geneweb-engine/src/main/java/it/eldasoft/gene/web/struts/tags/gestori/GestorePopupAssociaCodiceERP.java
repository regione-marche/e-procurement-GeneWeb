/*
 * Created on 22/04/24
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
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


/**
 * @author Alex Mancini
 */
public class GestorePopupAssociaCodiceERP extends
    AbstractGestoreEntita {

  static Logger       logger = Logger.getLogger(GestorePopupAssociaCodiceERP.class);

  public GestorePopupAssociaCodiceERP() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;
  
  private GenChiaviManager genChiaviManager = null;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
    
    genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);
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



    // lettura dei parametri di input
    String codiceDitta = datiForm.getString("CODIMP");
    String codiceMichelangelo = datiForm.getString("CGENIMP");
    
    if (logger.isDebugEnabled())
      logger.debug("GestorePopupAssociaCodiceERP: Inizio ");

    try {
      //Aggiorno l'impresa con il codice michelangelo
      String updImpr = "update IMPR set cgenimp = ? where codimp = ?";
      this.sqlManager.update(updImpr, new Object[]{codiceMichelangelo, codiceDitta});
      
      Timestamp datains = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
      Long idG1msflussi = Long.valueOf(genChiaviManager.getNextId("G1MSFLUSSI"));
      String inserimentoFlusso = "INSERT INTO g1msflussi( "
          + " id, entita, chiave_int, chiave_ext, fonte, tipo_flusso, data_invio, data_richiesta, data_elab, stato, note) "
          + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      this.sqlManager.update(inserimentoFlusso, new Object[] { idG1msflussi, "IMPR", codiceDitta, codiceMichelangelo, "MAG", Long.valueOf(2), null, datains, null, Long.valueOf(1), null });

    } catch (Exception e) {
      this.getRequest().setAttribute("messageError", e.getMessage());
      throw new GestoreException(
          "Errore durante l'associazione del codice ERP per il codice impresa: "+codiceDitta,"", e);
    }
    this.getRequest().setAttribute("associaERPSuccess", "1");
    if (logger.isDebugEnabled())
      logger.debug("GestorepopupAssociaCodiceMichelangelo: Fine");
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
