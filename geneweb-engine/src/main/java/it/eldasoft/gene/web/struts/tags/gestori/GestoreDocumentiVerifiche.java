/*
 * Created on 03/02/2020
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
import it.eldasoft.gene.bl.verifiche.VerificheInterneManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.utils.spring.UtilitySpring;
import it.maggioli.eldasoft.security.EncryptionConstants;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard delle occorrenze dell'entita IMPRDOCG presenti piu' volte
 * nella pagina "Verifica documenti richiesti"
 *
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 *
 * @author Cristian Febas
 */
public class GestoreDocumentiVerifiche extends AbstractGestoreEntita {


  @Override
  public String getEntita() {
    return "DOCUMENTI_VERIFICHE";
  }

  public GestoreDocumentiVerifiche() {
    super(false);
  }

  /**
   * @param isGestoreStandard
  */

  public GestoreDocumentiVerifiche(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }


  final double MAX_FILE_SIZE = Math.pow(2, 20) * 5;

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

      VerificheInterneManager verificheInterneManager = (VerificheInterneManager) UtilitySpring.getBean(
          "verificheInterneManager", this.getServletContext(), VerificheInterneManager.class);


        Long idDocumentiVerifiche = (Long) impl.getColumn("DOCUMENTI_VERIFICHE.ID").getValue().getValue();
        try {

          Vector datiDOC_VERIF = this.getSqlManager().getVector("select DOCUMENTI_VERIFICHE.ID_VERIFICA,DOCUMENTI_VERIFICHE.IDDOCDG,VERIFICHE.GG_VALIDITA" +
          		" from VERIFICHE,DOCUMENTI_VERIFICHE where VERIFICHE.ID=DOCUMENTI_VERIFICHE.ID_VERIFICA and DOCUMENTI_VERIFICHE.ID = ?", new Object[] {idDocumentiVerifiche});
          if (datiDOC_VERIF != null && datiDOC_VERIF.size() > 0) {
            Long idVerifica = (Long) SqlManager.getValueFromVectorParam(datiDOC_VERIF, 0).getValue();
            Long iddocdg = (Long) SqlManager.getValueFromVectorParam(datiDOC_VERIF, 1).getValue();
            if(iddocdg != null){
              this.getSqlManager().update("delete from W_DOCDIG where IDPRG = ? and IDDOCDIG = ? and digent= ?", new Object[] {"PG", iddocdg, "DOCUMENTI_VERIFICHE"});
              this.getSqlManager().update("delete from DOCUMENTI_VERIFICHE where ID = ?", new Object[] {idDocumentiVerifiche});
            }else{
              this.getSqlManager().update("delete from DOCUMENTI_VERIFICHE where ID = ?", new Object[] {idDocumentiVerifiche});
            }//iddocdig

            verificheInterneManager.calcolaScadenzeVerifiche(idVerifica.intValue(), idDocumentiVerifiche.intValue(),
                new Long(0), null, null, null, null, null, null);

          }//datiDOC_VERIF


        } catch (SQLException e) {
          throw new GestoreException("Errore nella eliminazione delle tabelle figlie di DOCUMENTI_VERIFICHE", null, e);
        }
  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    Logger       logger = Logger.getLogger(GestoreDocumentiVerifiche.class);

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

    VerificheInterneManager verificheInterneManager = (VerificheInterneManager) UtilitySpring.getBean(
        "verificheInterneManager", this.getServletContext(), VerificheInterneManager.class);

    Long idVerifica =impl.getLong("DOCUMENTI_VERIFICHE.ID_VERIFICA");
    if(idVerifica == null){
      String idVerificaStr = new String(this.getRequest().getParameter("idVerifica"));
      idVerifica = new Long(idVerificaStr);
    }
    //calcolo del progressivo di DOCUMENTI_VERIFICHE
    int idDocumentiVerifiche = genChiaviManager.getNextId("DOCUMENTI_VERIFICHE");

    impl.getColumn("DOCUMENTI_VERIFICHE.ID").setChiave(true);
    impl.setValue("DOCUMENTI_VERIFICHE.ID", new Long(idDocumentiVerifiche));

    try{
      impl.insert("DOCUMENTI_VERIFICHE", sqlManager);
      // gestione dell'allegato nella scheda
      String operazione="INSERT";
      verificheInterneManager.setDocumentoAllegato(this.getRequest(), operazione, new Long(idDocumentiVerifiche), null, impl, this.getForm());

      //calcolo della data di scadenza , poi trasferire in altra porzione di codice:
      Long tipo = null;
      if(impl.getColumn("DOCUMENTI_VERIFICHE.TIPO").getValue()!= null){
        tipo = (Long) impl.getColumn("DOCUMENTI_VERIFICHE.TIPO").getValue().getValue();
      }

     verificheInterneManager.calcolaScadenzeVerifiche(idVerifica.intValue(), idDocumentiVerifiche,
         tipo, null, null, null, null, null, null);

    }catch(SQLException e){
      throw new GestoreException("Errore nell'inserimento in DOCUMENTI_VERIFICHE", null, e);
    }

        // Se l'operazione di insert e' andata a buon fine (cioe' nessuna
    // eccezione) inserisco nel request l'attributo RISULTATO valorizzato con
    // "OK", che permettera' alla popup di inserimento documentazione di richiamare
    // il refresh della finestra padre e di chiudere se stessa

    this.getRequest().setAttribute("RISULTATO", "OK");
  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    VerificheInterneManager verificheInterneManager = (VerificheInterneManager) UtilitySpring.getBean(
        "verificheInterneManager", this.getServletContext(), VerificheInterneManager.class);

      Long iddocdg = (Long) impl.getColumn("W_DOCDIG.IDDOCDIG").getValue().getValue();

      impl.removeColumns(new String[] { "W_DOCDIG.DIGDESDOC", "W_DOCDIG.IDPRG","W_DOCDIG.IDDOCDIG","W_DOCDIG.DIGNOMDOC" });

      //calcolo della data di scadenza , poi trasferire in altra porzione di codice:
      Long idDocumentiVerifiche = (Long) impl.getColumn("DOCUMENTI_VERIFICHE.ID").getValue().getValue();
      Long idVerifica = (Long) impl.getColumn("DOCUMENTI_VERIFICHE.ID_VERIFICA").getValue().getValue();
      Long tipo = null;
      if(impl.getColumn("DOCUMENTI_VERIFICHE.TIPO").getValue()!= null){
        tipo = (Long) impl.getColumn("DOCUMENTI_VERIFICHE.TIPO").getValue().getValue();
      }

      try {
        impl.update("DOCUMENTI_VERIFICHE", sqlManager);

        String operazione=null;
        if(iddocdg!=null){
          operazione = "UPDATE";
        }else{
          operazione = "INSERT";
        }

      // gestione dell'allegato nella scheda
        verificheInterneManager.setDocumentoAllegato(this.getRequest(), operazione, idDocumentiVerifiche,
            iddocdg, impl, this.getForm());

      // gestione delle scadenze
        verificheInterneManager.calcolaScadenzeVerifiche(idVerifica.intValue(), idDocumentiVerifiche.intValue(),
            tipo, null, null, null, null, null, null);

      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento di DOCUMENTI_VERIFICHE", null, e);
      }
  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

  /**
   * Cifra la chiave di sessione e la converte in formato Base64 per gestirne
   * la memorizzazione provvisoria in DB.
   *
   * @param sessionKey
   *            chiave di sessione
   * @param username
   *            login utente
   * @return chiave di sessione cifrata e convertita in base64
   */
  private String encodeSessionKey(byte[] sessionKey, String username)
          throws GeneralSecurityException, UnsupportedEncodingException {
      String chiave = null;

      byte[] chiaveProvvisoriaDecifratura = SymmetricEncryptionUtils.fill128bitKey(username);
      IvParameterSpec iv = new IvParameterSpec(chiaveProvvisoriaDecifratura);
      SecretKeySpec skeySpec = new SecretKeySpec(
              chiaveProvvisoriaDecifratura,
              EncryptionConstants.SESSION_KEY_GEN_ALGORITHM);
      Cipher cipher = Cipher
              .getInstance(EncryptionConstants.SESSION_KEY_ENCRYPTION_TRANSFORMATION);
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
      chiave = Base64.encodeBase64String(cipher.doFinal(sessionKey));
      return chiave;
  }


}