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
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.XSSRequestWrapper;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

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
        String filename = formFile.getFileName();
        if (!FileAllegatoManager.isEstensioneFileAmmessa(filename)) {
          throw new GestoreException("Il file selezionato da caricare ha un'estensione non accettata",
              "upload.estensioneNonAmmessa", new String[]{filename}, null);
        } else if(!FileAllegatoManager.checkFileContent(formFile.getFileData(), filename)){
          throw new GestoreException("Il file selezionato da caricare viene scartato in quanto ha un contenuto in formato non valido secondo le verifiche mediante la libreria Apache Tika",
              "upload.erroreVerificaContenuto", new String[]{filename}, null);
        } else if(filename.toLowerCase().endsWith(".pdf") && FileAllegatoManager.isMaliciousPdf(formFile.getFileData(), filename)) {
          throw new GestoreException("Il file selezionato da caricare viene scartato in quanto sembra codificare dei contenuti non sicuri (malware)",
                  "upload.erroreContenutoMalevolo", new String[]{filename}, null);
        }

        AbstractGestoreChiaveNumerica gestoreW_DOCDIG = new DefaultGestoreEntitaChiaveNumerica("W_DOCDIG", "IDDOCDIG",
            new String[] { "IDPRG" }, this.getRequest());

        DataColumnContainer dccW_DOCDIG = new DataColumnContainer(new DataColumn[] { new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, c0aprg)) });
        dccW_DOCDIG.addColumn("W_DOCDIG.IDDOCDIG", JdbcParametro.TIPO_NUMERICO, null);
        dccW_DOCDIG.addColumn("W_DOCDIG.DIGENT", JdbcParametro.TIPO_TESTO, "C0OGGASS");
        dccW_DOCDIG.addColumn("W_DOCDIG.DIGKEY1", JdbcParametro.TIPO_TESTO, c0acod.toString());

        // se il nome del file supera i 100 caratteri (dimensiona massima del campo nome documento in W_DOCDIG), si tronca il nome mantenendo le estensioni
        String dignomdoc = filename;
        if (filename != null && filename.length() > 100) {
          String estensioni = filename.substring(filename.indexOf('.'));
          dignomdoc = filename.substring(0, 100 - estensioni.length()) + estensioni;
        }

        // si rimuove la colonna ricevuta per il nome oggetto associato e la si ricrea con il nome del file caricato e controllato, altrimenti sarebbe manipolabile
        datiForm.removeColumns(new String[] {"C0OGGASS.C0ANOMOGG"});
        datiForm.addColumn("C0OGGASS.C0ANOMOGG", JdbcParametro.TIPO_TESTO, filename);
        dccW_DOCDIG.addColumn("W_DOCDIG.DIGNOMDOC", JdbcParametro.TIPO_TESTO, dignomdoc);

        // si risolvono problemi di XSS eventualmente su dati inseriti, convertendoli in html
        String annotazionedoc = XSSRequestWrapper.stripXSS(datiForm.getString("C0OGGASS.C0ANNOTE"));
        datiForm.getColumn("C0OGGASS.C0ANNOTE").setObjectValue(annotazionedoc);
        String digdesdoc = XSSRequestWrapper.stripXSS(datiForm.getString("C0OGGASS.C0ATIT"));
        datiForm.getColumn("C0OGGASS.C0ATIT").setObjectValue(digdesdoc);

        dccW_DOCDIG.addColumn("W_DOCDIG.DIGDESDOC", JdbcParametro.TIPO_TESTO, digdesdoc);
        
        String digtipdoc = datiForm.getString("W_DOCDIG.DIGTIPDOC");
        dccW_DOCDIG.addColumn("W_DOCDIG.DIGTIPDOC", JdbcParametro.TIPO_TESTO, digtipdoc);

        if (formFile.getFileSize() > 0) {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          baos.write(formFile.getFileData());
          dccW_DOCDIG.addColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos));
        } else {
          throw new GestoreException("Errore nell'inserimento del file \""
              + filename
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
        if(formFile.getFileSize() > 0)
          ObjectStorageUtils.addFile(dccW_DOCDIG.getColumn("W_DOCDIG.IDPRG").getValue().getStringValue(), dccW_DOCDIG.getColumn("W_DOCDIG.IDDOCDIG").getValue().longValue());
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
    try {
      Vector<?> datiPreDelete = this.sqlManager.getVector("select w.idprg, w.iddocdig, c.c0aent, c.c0akey1, c.c0akey2, c.c0akey3, c.c0akey4, c.c0akey5, w.bucket_ref, w.bucket_uuid "
          + "from w_docdig w "
          + "inner join c0oggass c on w.idprg = c.c0aprg and w.digkey1 = CAST(c.c0acod AS VARCHAR(30)) "
          + "where w.digent = 'C0OGGASS' and c.c0acod = ? ", new Object[] { c0acod });
      if(datiPreDelete!=null && !datiPreDelete.isEmpty()) {
        String idprg = SqlManager.getValueFromVectorParam(datiPreDelete, 0).getStringValue();
        Long iddocdig= (Long) SqlManager.getValueFromVectorParam(datiPreDelete, 1).getValue();
        String c0aent = SqlManager.getValueFromVectorParam(datiPreDelete, 2).getStringValue();
        String c0akey1 = SqlManager.getValueFromVectorParam(datiPreDelete, 3).getStringValue();
        String c0akey2 = SqlManager.getValueFromVectorParam(datiPreDelete, 4).getStringValue();
        String c0akey3 = SqlManager.getValueFromVectorParam(datiPreDelete, 5).getStringValue();
        String c0akey4 = SqlManager.getValueFromVectorParam(datiPreDelete, 6).getStringValue();
        String c0akey5 = SqlManager.getValueFromVectorParam(datiPreDelete, 7).getStringValue();
        datiForm.addColumn("W_DOCDIG.IDPRG", JdbcParametro.TIPO_TESTO, idprg);
        datiForm.addColumn("W_DOCDIG.IDDOCDIG", JdbcParametro.TIPO_NUMERICO, iddocdig);
        datiForm.addColumn("C0OGGASS.C0AENT", JdbcParametro.TIPO_TESTO, c0aent);
        datiForm.addColumn("C0OGGASS.C0AKEY1", JdbcParametro.TIPO_TESTO, c0akey1);
        datiForm.addColumn("C0OGGASS.C0AKEY2", JdbcParametro.TIPO_TESTO, c0akey2);
        datiForm.addColumn("C0OGGASS.C0AKEY3", JdbcParametro.TIPO_TESTO, c0akey3);
        datiForm.addColumn("C0OGGASS.C0AKEY4", JdbcParametro.TIPO_TESTO, c0akey4);
        datiForm.addColumn("C0OGGASS.C0AKEY5", JdbcParametro.TIPO_TESTO, c0akey5);
      }
    } catch (SQLException e) {
      throw new GestoreException( "Si e' verificato un errore durante la lettura dei documenti associati ",null, e);
	}
    
    this.getGeneManager().deleteTabelle(new String[] { "W_DOCDIG" }, "DIGENT = 'C0OGGASS' AND DIGKEY1 = ?",
        new Object[] { c0acod.toString() });
  }
  
  @Override
  public void afterDeleteEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    Long c0acod = datiForm.getLong("C0OGGASS.C0ACOD");
    String idprg = datiForm.getString("W_DOCDIG.IDPRG");
    Long iddocdig = datiForm.getLong("W_DOCDIG.IDDOCDIG");
    String c0aent = datiForm.getString("C0OGGASS.C0AENT");
    String c0akey1 = datiForm.getString("C0OGGASS.C0AKEY1");
    String c0akey2 = datiForm.getString("C0OGGASS.C0AKEY2");
    String c0akey3 = datiForm.getString("C0OGGASS.C0AKEY3");
    String c0akey4 = datiForm.getString("C0OGGASS.C0AKEY4");
    String c0akey5 = datiForm.getString("C0OGGASS.C0AKEY5");
    
    String descrizione = "Cancellazione documento associato";
    if(c0aent != null) {
      descrizione +=" - "+c0aent+"/"+c0akey1;
      
      //AGGIUNGO IN SUCCESSIONE TUTTE LE ALTRE CHIAVI SE QUEST'ULTIME SONO VALORIZZATE
      if(!"#".equals(c0akey2)) {
        descrizione +="/"+c0akey2;
        if(!"#".equals(c0akey3)) {
          descrizione +="/"+c0akey3;
          if(!"#".equals(c0akey4)) {
            descrizione +="/"+c0akey4;
            if(!"#".equals(c0akey5)) {
              descrizione +="/"+c0akey5;
            }
          }
        }
      }
      
      //PER CONVENZIONE AZIENDALE SONO STATI SPOSTATI ALL'ULTIMA POSIZIONE
      descrizione += "/"+idprg+"/"+iddocdig;
    }
    try {
      String codiceApplicazione = (String) this.getRequest().getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
      codiceApplicazione = UtilityStringhe.convertiNullInStringaVuota(codiceApplicazione);
      if("".equals(codiceApplicazione)){
        codiceApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
      }
      
      LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
      logEvento.setCodApplicazione(codiceApplicazione);
      logEvento.setLivEvento(1);
      logEvento.setOggEvento(c0acod.toString());
      logEvento.setCodEvento("C0OGGASS_DELETE");
      logEvento.setDescr(descrizione);
      logEvento.setErrmsg("");
      LogEventiUtils.insertLogEventi(logEvento);
    } catch (Exception le) {
      logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
    }
    
  }
  
  @Override
  public void afterUpdateEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    Long c0acod = datiForm.getLong("C0OGGASS.C0ACOD");

    try {
      Vector<?> datiAfterUpdate = this.sqlManager.getVector("select w.idprg, w.iddocdig, c.c0aent, c.c0akey1, c.c0akey2, c.c0akey3, c.c0akey4, c.c0akey5 "
          + "from w_docdig w "
          + "inner join c0oggass c on w.idprg = c.c0aprg and w.digkey1 = CAST(c.c0acod AS VARCHAR(30)) "
          + "where w.digent = 'C0OGGASS' and c.c0acod = ? ", new Object[] { c0acod });
      if(datiAfterUpdate!=null && !datiAfterUpdate.isEmpty()) {
        String idprg = SqlManager.getValueFromVectorParam(datiAfterUpdate, 0).getStringValue();
        Long iddocdig= (Long) SqlManager.getValueFromVectorParam(datiAfterUpdate, 1).getValue();
        String c0aent = SqlManager.getValueFromVectorParam(datiAfterUpdate, 2).getStringValue();
        String c0akey1 = SqlManager.getValueFromVectorParam(datiAfterUpdate, 3).getStringValue();
        String c0akey2 = SqlManager.getValueFromVectorParam(datiAfterUpdate, 4).getStringValue();
        String c0akey3 = SqlManager.getValueFromVectorParam(datiAfterUpdate, 5).getStringValue();
        String c0akey4 = SqlManager.getValueFromVectorParam(datiAfterUpdate, 6).getStringValue();
        String c0akey5 = SqlManager.getValueFromVectorParam(datiAfterUpdate, 7).getStringValue();


        String descrizione = "Aggiornamento documento associato";
        if(c0aent != null) {
          descrizione +=" - "+c0aent+"/"+c0akey1;

          //AGGIUNGO IN SUCCESSIONE TUTTE LE ALTRE CHIAVI SE QUEST'ULTIME SONO VALORIZZATE
          if(!"#".equals(c0akey2)) {
            descrizione +="/"+c0akey2;
            if(!"#".equals(c0akey3)) {
              descrizione +="/"+c0akey3;
              if(!"#".equals(c0akey4)) {
                descrizione +="/"+c0akey4;
                if(!"#".equals(c0akey5)) {
                  descrizione +="/"+c0akey5;
                }
              }
            }
          }

          //PER CONVENZIONE AZIENDALE SONO STATI SPOSTATI ALL'ULTIMA POSIZIONE
          descrizione += "/"+idprg+"/"+iddocdig;
        }

        try {
          String codiceApplicazione = (String) this.getRequest().getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
          codiceApplicazione = UtilityStringhe.convertiNullInStringaVuota(codiceApplicazione);
          if("".equals(codiceApplicazione)){
            codiceApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
          }
          
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setCodApplicazione(codiceApplicazione);
          logEvento.setLivEvento(1);
          logEvento.setOggEvento(c0acod.toString());
          logEvento.setCodEvento("C0OGGASS_EDIT");
          logEvento.setDescr(descrizione);
          logEvento.setErrmsg("");
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }

      }
    } catch (SQLException e) {
      throw new GestoreException( "Si e' verificato un errore durante la lettura dei documenti associati ",null, e);
    }
    
    
    
  }
  
  @Override
  public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    Long c0acod = datiForm.getLong("C0OGGASS.C0ACOD");

    try {
      Vector<?> datiAfterInsert = this.sqlManager.getVector("select w.idprg, w.iddocdig, c.c0aent, c.c0akey1, c.c0akey2, c.c0akey3, c.c0akey4, c.c0akey5 "
          + "from w_docdig w "
          + "inner join c0oggass c on w.idprg = c.c0aprg and w.digkey1 = CAST(c.c0acod AS VARCHAR(30)) "
          + "where w.digent = 'C0OGGASS' and c.c0acod = ? ", new Object[] { c0acod });
      if(datiAfterInsert!=null && !datiAfterInsert.isEmpty()) {
        String idprg = SqlManager.getValueFromVectorParam(datiAfterInsert, 0).getStringValue();
        Long iddocdig= (Long) SqlManager.getValueFromVectorParam(datiAfterInsert, 1).getValue();
        String c0aent = SqlManager.getValueFromVectorParam(datiAfterInsert, 2).getStringValue();
        String c0akey1 = SqlManager.getValueFromVectorParam(datiAfterInsert, 3).getStringValue();
        String c0akey2 = SqlManager.getValueFromVectorParam(datiAfterInsert, 4).getStringValue();
        String c0akey3 = SqlManager.getValueFromVectorParam(datiAfterInsert, 5).getStringValue();
        String c0akey4 = SqlManager.getValueFromVectorParam(datiAfterInsert, 6).getStringValue();
        String c0akey5 = SqlManager.getValueFromVectorParam(datiAfterInsert, 7).getStringValue();

        String descrizione = "Inserimento documento associato";
        if(c0aent != null) {
          descrizione +=" - "+c0aent+"/"+c0akey1;
          
          //AGGIUNGO IN SUCCESSIONE TUTTE LE ALTRE CHIAVI SE QUEST'ULTIME SONO VALORIZZATE
          if(!"#".equals(c0akey2)) {
            descrizione +="/"+c0akey2;
            if(!"#".equals(c0akey3)) {
              descrizione +="/"+c0akey3;
              if(!"#".equals(c0akey4)) {
                descrizione +="/"+c0akey4;
                if(!"#".equals(c0akey5)) {
                  descrizione +="/"+c0akey5;
                }
              }
            }
          }
          
          //PER CONVENZIONE AZIENDALE SONO STATI SPOSTATI ALL'ULTIMA POSIZIONE
          descrizione += "/"+idprg+"/"+iddocdig;
        }
        
        try {
          String codiceApplicazione = (String) this.getRequest().getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
          codiceApplicazione = UtilityStringhe.convertiNullInStringaVuota(codiceApplicazione);
          if("".equals(codiceApplicazione)){
            codiceApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
          }
          
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setCodApplicazione(codiceApplicazione);
          logEvento.setLivEvento(1);
          logEvento.setOggEvento(c0acod.toString());
          logEvento.setCodEvento("C0OGGASS_INSERT");
          logEvento.setDescr(descrizione);
          logEvento.setErrmsg("");
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }
    } catch (SQLException e) {
      throw new GestoreException( "Si e' verificato un errore durante la lettura dei documenti associati ",null, e);
    }
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
          String filename = formFile.getFileName();
          if (!FileAllegatoManager.isEstensioneFileAmmessa(filename)) {
            throw new GestoreException("Il file selezionato da caricare ha un'estensione non accettata",
                "upload.estensioneNonAmmessa", new String[]{filename}, null);
          } else if(!FileAllegatoManager.checkFileContent(formFile.getFileData(), filename)){
            throw new GestoreException("Il file selezionato da caricare viene scartato in quanto ha un contenuto in formato non valido secondo le verifiche mediante la libreria Apache Tika",
                "upload.erroreVerificaContenuto", new String[]{filename}, null);
          } else if(filename.toLowerCase().endsWith(".pdf") && FileAllegatoManager.isMaliciousPdf(formFile.getFileData(), filename)) {
            throw new GestoreException("Il file selezionato da caricare viene scartato in quanto sembra codificare dei contenuti non sicuri (malware)",
                    "upload.erroreContenutoMalevolo", new String[]{filename}, null);
          }

          // se il nome del file supera i 100 caratteri (dimensiona massima del campo nome documento in W_DOCDIG), si tronca il nome mantenendo le estensioni
          String dignomdoc = filename;
          if (filename != null && filename.length() > 100) {
            String estensioni = filename.substring(filename.indexOf('.'));
            dignomdoc = filename.substring(0, 100 - estensioni.length()) + estensioni;
          }

          // si rimuove la colonna ricevuta per il nome oggetto associato e la si ricrea con il nome del file caricato e controllato, altrimenti sarebbe manipolabile
          datiForm.removeColumns(new String[] {"C0OGGASS.C0ANOMOGG"});
          datiForm.addColumn("C0OGGASS.C0ANOMOGG", JdbcParametro.TIPO_TESTO, filename);
          dccW_DOCDIG.addColumn("W_DOCDIG.DIGNOMDOC", JdbcParametro.TIPO_TESTO, dignomdoc);

          // si risolvono problemi di XSS eventualmente su dati inseriti, convertendoli in html
          String annotazionedoc = XSSRequestWrapper.stripXSS(datiForm.getString("C0OGGASS.C0ANNOTE"));
          datiForm.getColumn("C0OGGASS.C0ANNOTE").setObjectValue(annotazionedoc);
          String digdesdoc = XSSRequestWrapper.stripXSS(datiForm.getString("C0OGGASS.C0ATIT"));
          datiForm.getColumn("C0OGGASS.C0ATIT").setObjectValue(digdesdoc);

          dccW_DOCDIG.addColumn("W_DOCDIG.DIGDESDOC", JdbcParametro.TIPO_TESTO, digdesdoc);
          
          String digtipdoc = datiForm.getString("W_DOCDIG.DIGTIPDOC");
          dccW_DOCDIG.addColumn("W_DOCDIG.DIGTIPDOC", JdbcParametro.TIPO_TESTO, digtipdoc);


          if (formFile.getFileSize() > 0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(formFile.getFileData());
            dccW_DOCDIG.addColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos));
          } else {
            throw new GestoreException("Errore nell'inserimento del file \""
                + filename
                + "\": non e' possibile inserire documenti di dimensione nulla", null, null);
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
        if(formFile.getFileSize() > 0)
          ObjectStorageUtils.addFile(dccW_DOCDIG.getColumn("W_DOCDIG.IDPRG").getValue().getStringValue(), dccW_DOCDIG.getColumn("W_DOCDIG.IDDOCDIG").getValue().longValue());

      }
    } catch (FileNotFoundException e) {
      throw new GestoreException("Si è verificato un problema durante il caricamento del file", null, e);
    } catch (IOException e) {
      throw new GestoreException("Si è verificato un problema durante il caricamento del file", null, e);
    }

  }

}
