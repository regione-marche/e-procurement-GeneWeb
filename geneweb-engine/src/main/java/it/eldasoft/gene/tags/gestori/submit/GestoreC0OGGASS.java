/*
 * Created on 05/05/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.gene.tags.gestori.submit;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.springframework.transaction.TransactionStatus;

public class GestoreC0OGGASS extends AbstractGestoreChiaveNumerica {

  static Logger logger = Logger.getLogger(GestoreC0OGGASS.class);

  @Override
  public String[] getAltriCampiChiave() {
    return null;
  }

  @Override
  public String getCampoNumericoChiave() {
    return "C0ACOD";
  }

  @Override
  public String getEntita() {
    return "C0OGGASS";
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("GestoreC0OGGASS-preInsert: inizio metodo ");

    super.preInsert(status, datiForm);

    Long c0acod = datiForm.getLong("C0OGGASS.C0ACOD");
    String c0aprg = datiForm.getString("C0OGGASS.C0APRG");

    datiForm.addColumn("C0OGGASS.C0ADAT", JdbcParametro.TIPO_DATA, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()));

    if (datiForm.getString("C0OGGASS.C0AKEY1") == null) datiForm.setValue("C0OGGASS.C0AKEY1", "#");
    if (datiForm.getString("C0OGGASS.C0AKEY2") == null) datiForm.setValue("C0OGGASS.C0AKEY2", "#");
    if (datiForm.getString("C0OGGASS.C0AKEY3") == null) datiForm.setValue("C0OGGASS.C0AKEY3", "#");
    if (datiForm.getString("C0OGGASS.C0AKEY4") == null) datiForm.setValue("C0OGGASS.C0AKEY4", "#");
    if (datiForm.getString("C0OGGASS.C0AKEY5") == null) datiForm.setValue("C0OGGASS.C0AKEY5", "#");

    datiForm.addColumn("C0OGGASS.C0ADIROGG", JdbcParametro.TIPO_TESTO, "[default]");

    try {
      FormFile formFile = this.getForm().getSelezioneFile();
      if (formFile != null && !"".equals(formFile.getFileName())) {

        if (!FileAllegatoManager.isEstensioneFileAmmessa(formFile.getFileName())) {
          throw new GestoreException("Il file selezionato da caricare ha un'estensione non accettata",
              "upload.estensioneNonAmmessa", new String[]{formFile.getFileName()}, null);
        }

        AbstractGestoreChiaveNumerica gestoreW_DOCDIG = new DefaultGestoreEntitaChiaveNumerica("W_DOCDIG", "IDDOCDIG",
            new String[] { "IDPRG" }, this.getRequest());

        DataColumnContainer dccW_DOCDIG = new DataColumnContainer(new DataColumn[] { new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, c0aprg)) });
        dccW_DOCDIG.addColumn("W_DOCDIG.IDDOCDIG", JdbcParametro.TIPO_NUMERICO, null);
        dccW_DOCDIG.addColumn("W_DOCDIG.DIGENT", JdbcParametro.TIPO_TESTO, "C0OGGASS");
        dccW_DOCDIG.addColumn("W_DOCDIG.DIGKEY1", JdbcParametro.TIPO_TESTO, c0acod.toString());

        String dignomdoc = datiForm.getString("C0OGGASS.C0ANOMOGG");
        if (dignomdoc != null && dignomdoc.length() > 100) dignomdoc = dignomdoc.substring(0, 100);
        dccW_DOCDIG.addColumn("W_DOCDIG.DIGNOMDOC", JdbcParametro.TIPO_TESTO, dignomdoc);

        String digdesdoc = datiForm.getString("C0OGGASS.C0ATIT");
        dccW_DOCDIG.addColumn("W_DOCDIG.DIGDESDOC", JdbcParametro.TIPO_TESTO, digdesdoc);

        if (formFile.getFileSize() > 0) {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          baos.write(formFile.getFileData());
          dccW_DOCDIG.addColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos));
        } else {
          throw new GestoreException("Errore nell'inserimento del file \""
              + formFile.getFileName()
              + "\": non e' possibile inserire documenti di dimensione nulla", null, null);
        }

        if(datiForm.isColumn("W_DOCDIG.DIGFIRMA")){
          String digfirma =UtilityStruts.getParametroString(this.getRequest(),"richiestaFirma");
          if("on".equals(digfirma))
            digfirma="1";
          else
            digfirma=null;
          dccW_DOCDIG.addColumn("W_DOCDIG.DIGFIRMA", JdbcParametro.TIPO_TESTO, digfirma);

        }
        gestoreW_DOCDIG.inserisci(status, dccW_DOCDIG);

      }
    } catch (FileNotFoundException e) {
      throw new GestoreException("Si è verificato un problema durante il caricamento del file", null, e);
    } catch (IOException e) {
      throw new GestoreException("Si è verificato un problema durante il caricamento del file", null, e);
    }

    if (logger.isDebugEnabled()) logger.debug("GestoreC0OGGASS-preInsert: fine metodo ");

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
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    Long c0acod = datiForm.getLong("C0OGGASS.C0ACOD");
    this.getGeneManager().deleteTabelle(new String[] { "W_DOCDIG" }, "DIGENT = 'C0OGGASS' AND DIGKEY1 = ?",
        new Object[] { c0acod.toString() });
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    try {
      FormFile formFile = this.getForm().getSelezioneFile();

      if (formFile != null && !"".equals(formFile.getFileName()) || datiForm.isColumn("W_DOCDIG.DIGFIRMA")) {
        
        Long c0acod = datiForm.getLong("C0OGGASS.C0ACOD");
        String c0aprg = datiForm.getString("C0OGGASS.C0APRG");

        AbstractGestoreChiaveNumerica gestoreW_DOCDIG = new DefaultGestoreEntitaChiaveNumerica("W_DOCDIG", "IDDOCDIG",
            new String[] { "IDPRG" }, this.getRequest());

        DataColumnContainer dccW_DOCDIG = new DataColumnContainer(this.sqlManager, "W_DOCDIG",
            "select idprg, iddocdig from w_docdig where idprg = ? and digent = ? and digkey1 = ?", new Object[] { c0aprg, "C0OGGASS", c0acod.toString() });
        dccW_DOCDIG.getColumn("W_DOCDIG.IDPRG").setChiave(true);
        dccW_DOCDIG.getColumn("W_DOCDIG.IDDOCDIG").setChiave(true);
        
        if (formFile != null && !"".equals(formFile.getFileName())){
          
          if (!FileAllegatoManager.isEstensioneFileAmmessa(formFile.getFileName())) {
            throw new GestoreException("Il file selezionato da caricare ha un'estensione non accettata",
                "upload.estensioneNonAmmessa", new String[]{formFile.getFileName()}, null);
          }
  
          if (formFile != null && !"".equals(formFile.getFileName())){
            String dignomdoc = datiForm.getString("C0OGGASS.C0ANOMOGG");
            if (dignomdoc != null && dignomdoc.length() > 100) dignomdoc = dignomdoc.substring(0, 100);
            dccW_DOCDIG.addColumn("W_DOCDIG.DIGNOMDOC", JdbcParametro.TIPO_TESTO, dignomdoc);
  
            String digdesdoc = datiForm.getString("C0OGGASS.C0ATIT");
            dccW_DOCDIG.addColumn("W_DOCDIG.DIGDESDOC", JdbcParametro.TIPO_TESTO, digdesdoc);
  
  
            if (formFile.getFileSize() > 0) {
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              baos.write(formFile.getFileData());
              dccW_DOCDIG.addColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos));
            } else {
              throw new GestoreException("Errore nell'inserimento del file \""
                  + formFile.getFileName()
                  + "\": non e' possibile inserire documenti di dimensione nulla", null, null);
            }
          }
        }else{
          String dignomdoc = datiForm.getString("C0OGGASS.C0ANOMOGG");
          if(dignomdoc!=null && !"".equals(dignomdoc)){
            if (!FileAllegatoManager.isEstensioneFileAmmessa(dignomdoc)) {
              throw new GestoreException("Il file selezionato da caricare ha un'estensione non accettata",
                  "upload.estensioneNonAmmessa", new String[]{dignomdoc}, null);
            }
          }
        }
        
        if(datiForm.isColumn("W_DOCDIG.DIGFIRMA")){
          String digfirma =UtilityStruts.getParametroString(this.getRequest(),"richiestaFirma");
          if("on".equals(digfirma))
            digfirma="1";
          else
            digfirma=null;
          dccW_DOCDIG.addColumn("W_DOCDIG.DIGFIRMA", JdbcParametro.TIPO_TESTO, digfirma);
          dccW_DOCDIG.getColumn("W_DOCDIG.DIGFIRMA").setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, " "));
        }

        gestoreW_DOCDIG.update(status, dccW_DOCDIG);

      }
    } catch (FileNotFoundException e) {
      throw new GestoreException("Si è verificato un problema durante il caricamento del file", null, e);
    } catch (IOException e) {
      throw new GestoreException("Si è verificato un problema durante il caricamento del file", null, e);
    }

  }

}
