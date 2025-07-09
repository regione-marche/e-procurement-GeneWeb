package it.eldasoft.gene.bl.tasks;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import it.eldasoft.utils.mail.MailReceiver;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.AndTerm;
import javax.mail.search.HeaderTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.system.ConfigurazioneMail;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

public class RiconciliazionePECManager {

  private static final Logger LOGGER = Logger.getLogger(RiconciliazionePECManager.class);

  private static final String X_RIFERIMENTO_MESSAGE_ID_HEADER = "X-Riferimento-Message-ID";
  private static final String X_RICEVUTA_HEADER = "X-Ricevuta";
  private static final String X_VERIFICA_SICUREZZA_HEADER = "X-VerificaSicurezza";

  private static final HeaderTerm PRESA_IN_CARICO_TERM = new HeaderTerm(X_RICEVUTA_HEADER, "presa-in-carico");
  private static final HeaderTerm ACCETTAZIONE_TERM = new HeaderTerm(X_RICEVUTA_HEADER, "accettazione");
  private static final HeaderTerm PREAVVISO_ERRORE_CONSEGNA_TERM = new HeaderTerm(X_RICEVUTA_HEADER, "preavviso-errore-consegna");
  private static final HeaderTerm AVVENUTA_CONSEGNA_TERM = new HeaderTerm(X_RICEVUTA_HEADER, "avvenuta-consegna");
  private static final HeaderTerm NON_ACCETTAZIONE_TERM = new HeaderTerm(X_RICEVUTA_HEADER, "non-accettazione");
  private static final HeaderTerm ERRORE_CONSEGNA_TERM = new HeaderTerm(X_RICEVUTA_HEADER, "errore-consegna");
  private static final HeaderTerm RILEVAZIONE_VIRUS_TERM = new HeaderTerm(X_RICEVUTA_HEADER, "rilevazione-virus");
  private static final HeaderTerm ERRORE_VERIFICA_SICUREZZA_TERM = new HeaderTerm(X_VERIFICA_SICUREZZA_HEADER, "errore");
  private static final String ACCETTAZIONE_FILENAME = "accettazione.eml";
  private static final String CONSEGNA_FILENAME = "consegna.eml";

  private static final String GET_REFERENCE_ID_QUERY =
        "SELECT d.idcom, d.idcomdes, c.idcfg, d.messageid "
      + "FROM w_invcomdes d INNER JOIN w_invcom c ON (d.idprg=c.idprg AND d.idcom=c.idcom) "
      + "WHERE c.idprg = ? AND d.comtipma = 1 AND desesitopec IN ('1', '2', '3') "
      + "ORDER BY c.idcfg, d.idcom, d.idcomdes";

  private static final String UPDATE_DES_ESITO_PEC_QUERY =
        "UPDATE w_invcomdes "
      + "SET desesitopec = ?, desdatcons = ? "
      + "WHERE idprg = ? AND idcom = ? AND idcomdes = ?";

  private SqlManager sqlManager;
  private MailManager mailManager;

  public void setSqlManager(final SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setMailManager(final MailManager mailManager) {
    this.mailManager = mailManager;
  }

  @SuppressWarnings("unchecked")
  public void aggiornaStatoRiconciliazionePEC() {
    if (WebUtilities.isAppNotReady()) { return; }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("aggiornaStatoRiconciliazionePEC: inizio metodo");
    }

    final String idprg = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
    ConfigurazioneMail config = null;
    String idcfg = null;
    String idcfgPrecedente = null;
    boolean riconciliazioneVerificabile = false;
    MailReceiver mailReceiver = null;
 
	// si estraggono le mail da riconciliare, ordinate per configurazione in modo da
	// ottimizzare la lettura delle cfg di posta
	List<Vector<JdbcParametro>> queryResult = null;	
	try {
		queryResult = sqlManager.getListVector(GET_REFERENCE_ID_QUERY, new Object[] { idprg });
	} catch (SQLException e) {
		LOGGER.error("È stato riscontrato un problema con l'interrogazione del database", e);
	}
	
	if (queryResult != null) {
		for (final Vector<JdbcParametro> result : queryResult) {
			final String idcom = SqlManager.getValueFromVectorParam(result, 0).getStringValue();
			final String idcomdes = SqlManager.getValueFromVectorParam(result, 1).getStringValue();	
			idcfg = SqlManager.getValueFromVectorParam(result, 2).getStringValue();
			String messageID = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(result, 3).getStringValue());

			try {
				if (idcfgPrecedente == null || !idcfgPrecedente.equals(idcfg)) {
					// solo nel caso di primo record oppure di ogni cambio configurazione si estrae
					// la configurazione di posta, la si analizza e si decide se proseguire con la
					// riconciliazione
					config = mailManager.getConfigurazione(idprg, idcfg);
					if (config.getServerIMAP() == null || config.getServerIMAP().isEmpty()) {
						// non presente, quindi si passa al destinatario successivo
						riconciliazioneVerificabile = false;
					} else {
						if (config.getPortaIMAP() == null || config.getPortaIMAP().isEmpty()) {
							// imposto la porta di default se non valorizzata
							config.setPortaIMAP("993");
						}
						riconciliazioneVerificabile = true;
						// una volta sola si stabilisce la connessione con il provider di posta
						// specifico
						mailReceiver = MailReceiver.getInstance(config.getMailMitt(), config.getPassword(),
								config.getServerIMAP(), config.getPortaIMAP());
						mailReceiver.connect(MailReceiver.INBOX_FOLDER_NAME, Folder.READ_ONLY);
					}

					// memorizzo la configurazione appena letta per verificare alla prossima
					// iterazione se cambia la configurazione e quindi va riletta
					idcfgPrecedente = idcfg;
				}

				if (riconciliazioneVerificabile) {
					classificaMessaggio(mailReceiver, idprg, idcom, idcomdes, messageID);
				}

			} catch (NoSuchProviderException e) {
				LOGGER.error("Non è stato trovato il provider specificato (idcfg=" + idcfg + ")", e);
			} catch (AuthenticationFailedException e) {
				LOGGER.error("Impossibile autenticarsi al provider (idcfg=" + idcfg + ")", e);
			} catch (MessagingException e) {
				LOGGER.error("Problema nel reperimento o apertura della cartella Inbox (idcfg=" + idcfg + ")", e);
			} catch (SQLException e) {
				LOGGER.error("È stato riscontrato un problema con l'interrogazione del database", e);
			} catch (CriptazioneException e) {
				LOGGER.error("Non è stato possibile recuperare le configurazioni mail (idcfg=" + idcfg + ")", e);
			}
    }
	}

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("aggiornaStatoRiconciliazionePEC: fine metodo");
    }
  }

  private void classificaMessaggio(final MailReceiver mailReceiver, final String idprg, final String idcom, final String idcomdes, String messageID)
      throws MessagingException, SQLException {
	 
  	// GENEWEB-168: di default si utilizza la nuova modalita' basata sul message id
    String referenceId = messageID;
    if (referenceId == null) {
		// altrimenti se il message id non e' valorizzato in DB, allora si utilizza la
		// vecchia modalita' basata sull'header X-Riferimento-Message-ID iniettato nella
		// mail da inviare
    	referenceId = idprg + "_" + idcom + "_" + idcomdes;
    }

    // stato iniziale: se rimane tale vuol dire che non si reperisce nessuna ricevuta
    String nuovoStatoEsito = "0";
    // sentinella per interrompere l'esecuzione
    boolean continua = true;
    // quando si reperisce una ricevuta PEC, allora la data risulta valorizzata
    Date dataRicevuta = null;
    Message messaggio = null;
    // da usare ESCLUSIVAMENTE per impostare la data consegna, reperita dalla relativa ricevuta di notifica
    Timestamp dataConsegna = null;
    
    if (continua) {
      messaggio = getMessaggioRicevutaPresente(mailReceiver, referenceId, AVVENUTA_CONSEGNA_TERM);
      dataRicevuta = getSentDate(messaggio);
        if (dataRicevuta != null) {
			// in caso di ricevuta di consegna, si imposta lo stato finale e si predispone
			// la variabile per aggiornare il DB con la data consegna PEC
            nuovoStatoEsito = "4";
            dataConsegna = new Timestamp(dataRicevuta.getTime());
            continua = false; 
        }
    }
    
    if (continua) {
      messaggio = getMessaggioRicevutaPresente(mailReceiver, referenceId, new OrTerm(ERRORE_VERIFICA_SICUREZZA_TERM, RILEVAZIONE_VIRUS_TERM));
      dataRicevuta = getSentDate(messaggio);
        if (dataRicevuta != null) {
            nuovoStatoEsito = "7";
            continua = false; 
        }
    }
    
    if (continua) {
      messaggio = getMessaggioRicevutaPresente(mailReceiver, referenceId, ERRORE_CONSEGNA_TERM);
      dataRicevuta = getSentDate(messaggio);
        if (dataRicevuta != null) {
            nuovoStatoEsito = "6";
            continua = false; 
        }
    }

    if (continua) {
      messaggio = getMessaggioRicevutaPresente(mailReceiver, referenceId, NON_ACCETTAZIONE_TERM);
      dataRicevuta = getSentDate(messaggio);
        if (dataRicevuta != null) {
            nuovoStatoEsito = "5";
            continua = false; 
        }
    }
    
    if (continua) {
      messaggio = getMessaggioRicevutaPresente(mailReceiver, referenceId, PREAVVISO_ERRORE_CONSEGNA_TERM);
      dataRicevuta = getSentDate(messaggio);
        if (dataRicevuta != null) {
            nuovoStatoEsito = "3";
            continua = false; 
        }
    }

    if (continua) {
      messaggio = getMessaggioRicevutaPresente(mailReceiver, referenceId, ACCETTAZIONE_TERM);
      dataRicevuta = getSentDate(messaggio);
        if (dataRicevuta != null) {
            nuovoStatoEsito = "2";
            continua = false; 
        }
    }
    
    if (continua) {
      messaggio = getMessaggioRicevutaPresente(mailReceiver, referenceId, PRESA_IN_CARICO_TERM);
      dataRicevuta = getSentDate(messaggio);
        if (dataRicevuta != null) {
            nuovoStatoEsito = "1";
            continua = false; 
        }
    }

    if (!nuovoStatoEsito.equals("0")) {
        try {
          getMessaggi(mailReceiver, referenceId, idprg, idcom, idcomdes);
        } catch (Exception e) {
          LOGGER.error("Errore durante l'estrazione dei file EML", e);
        }
      sqlManager.update(UPDATE_DES_ESITO_PEC_QUERY, new Object[] { nuovoStatoEsito, dataConsegna, idprg, idcom, idcomdes });
    }
  }

	/**
	 * Verifica la presenza di una precisa ricevuta PEC sulla base del reference id
	 * utilizzato come input di filtro.
	 * 
	 * @param mailReceiver
	 * @param referenceId
	 * @param filtroAggiuntivoRicevuta
	 * @return data della ricevuta se presente, null altrimenti.
	 * @throws MessagingException
	 */
	private Message getMessaggioRicevutaPresente(final MailReceiver mailReceiver, final String referenceId,
			SearchTerm filtroAggiuntivoRicevuta) throws MessagingException {
		final HeaderTerm riferimentoMessageIdTerm = new HeaderTerm(X_RIFERIMENTO_MESSAGE_ID_HEADER, referenceId);
		Message[] messaggi = mailReceiver.filterMail(new AndTerm(riferimentoMessageIdTerm, filtroAggiuntivoRicevuta));
		if (messaggi != null && messaggi.length > 0) {
			// ciclo sui messaggi che rispettano la ricerca
      for (Message message : messaggi) {
        String[] msgReferenceIds = message.getHeader(X_RIFERIMENTO_MESSAGE_ID_HEADER);
        if (msgReferenceIds != null) {
          // si controllano i reference id recuperati perchè il fitro estrae per match in
          // like e non per match esatto
          for (String msgReferenceId : msgReferenceIds) {
            // il match deve essere esatto
            if (msgReferenceId.equals(referenceId)) {
              return message;
            }
          }
        }
      }
		}
		return null;
	}

  private Date getSentDate(Message messaggio) throws MessagingException {
    Date result = null;
    if(messaggio != null){
      result=messaggio.getSentDate();
    }
    return result;
  }


  private void getMessaggi(final MailReceiver mailReceiver,final String referenceId, String idprg, String idcom, String  idcomdes) throws Exception {
    Message accettazione = getMessaggioRicevutaPresente(mailReceiver, referenceId, ACCETTAZIONE_TERM);
    saveEML(accettazione,ACCETTAZIONE_FILENAME,idprg, idcom, idcomdes);
    Message ricevuta = getMessaggioRicevutaPresente(mailReceiver, referenceId, AVVENUTA_CONSEGNA_TERM);
    saveEML(ricevuta,CONSEGNA_FILENAME,idprg, idcom, idcomdes);
  }

  private void saveEML(Message messaggio, String fileName, String idprg, String idcom, String idcomdes) throws Exception {
    if(messaggio==null)
      return;
    if(LOGGER.isDebugEnabled()) LOGGER.debug("SaveEML: inizio metodo");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    messaggio.writeTo(baos);
    Long iddocdig = (Long) sqlManager.getObject("select iddocdig from w_docdig where digent=? and digkey1=? and digkey2=? and digkey3=? and dignomdoc=?", new Object[]{"W_INVCOMDES",idprg,idcom,idcomdes,fileName});
    LobHandler lobHandler = new DefaultLobHandler();
    if(iddocdig==null) {
      Long newIddocdig = (Long) sqlManager.getObject("SELECT max(iddocdig) FROM w_docdig WHERE idprg=?", new Object[]{idprg});
      if (newIddocdig == null) {
        newIddocdig = new Long(0);
      }
      iddocdig = newIddocdig + 1;
      String insert = "INSERT INTO W_DOCDIG(IDPRG,IDDOCDIG,DIGENT,DIGKEY1,DIGKEY2,DIGKEY3,DIGNOMDOC,DIGDESDOC,DIGOGG) VALUES (?,?,?,?,?,?,?,?,?)";

      sqlManager.update(insert, new Object[]{idprg,iddocdig,"W_INVCOMDES",idprg,idcom,idcomdes,fileName,"Dati "+(CONSEGNA_FILENAME.equals(fileName)?"consegna":"accettazione")+" PEC",new SqlLobValue(baos.toByteArray(), lobHandler)});
    }else{
      String update = "UPDATE W_DOCDIG SET DIGOGG=? WHERE IDPRG=? AND IDDOCDIG=?";
      sqlManager.update(update,new Object[]{new SqlLobValue(baos.toByteArray(), lobHandler),idprg,iddocdig});
    }
    ObjectStorageUtils.addFile(idprg,iddocdig);
    if(LOGGER.isDebugEnabled()) LOGGER.debug("SaveEML: fine metodo");
  }
}
