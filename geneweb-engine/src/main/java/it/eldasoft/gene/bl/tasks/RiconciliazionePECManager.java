package it.eldasoft.gene.bl.tasks;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
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
import it.eldasoft.utils.mail.MailReceiver;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;

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
    // da usare ESCLUSIVAMENTE per impostare la data consegna, reperita dalla relativa ricevuta di notifica
    Timestamp dataConsegna = null;
    
    if (continua) {
    	dataRicevuta = isRicevutaPresente(mailReceiver, referenceId, AVVENUTA_CONSEGNA_TERM);
        if (dataRicevuta != null) {
			// in caso di ricevuta di consegna, si imposta lo stato finale e si predispone
			// la variabile per aggiornare il DB con la data consegna PEC
            nuovoStatoEsito = "4";
            dataConsegna = new Timestamp(dataRicevuta.getTime());
            continua = false; 
        }
    }
    
    if (continua) {
    	dataRicevuta = isRicevutaPresente(mailReceiver, referenceId, new OrTerm(ERRORE_VERIFICA_SICUREZZA_TERM, RILEVAZIONE_VIRUS_TERM));
        if (dataRicevuta != null) {
            nuovoStatoEsito = "7";
            continua = false; 
        }
    }
    
    if (continua) {
    	dataRicevuta = isRicevutaPresente(mailReceiver, referenceId, ERRORE_CONSEGNA_TERM);
        if (dataRicevuta != null) {
            nuovoStatoEsito = "6";
            continua = false; 
        }
    }

    if (continua) {
    	dataRicevuta = isRicevutaPresente(mailReceiver, referenceId, NON_ACCETTAZIONE_TERM);
        if (dataRicevuta != null) {
            nuovoStatoEsito = "5";
            continua = false; 
        }
    }
    
    if (continua) {
    	dataRicevuta = isRicevutaPresente(mailReceiver, referenceId, PREAVVISO_ERRORE_CONSEGNA_TERM);
        if (dataRicevuta != null) {
            nuovoStatoEsito = "3";
            continua = false; 
        }
    }

    if (continua) {
    	dataRicevuta = isRicevutaPresente(mailReceiver, referenceId, ACCETTAZIONE_TERM);
        if (dataRicevuta != null) {
            nuovoStatoEsito = "2";
            continua = false; 
        }
    }
    
    if (continua) {
    	dataRicevuta = isRicevutaPresente(mailReceiver, referenceId, PRESA_IN_CARICO_TERM);
        if (dataRicevuta != null) {
            nuovoStatoEsito = "1";
            continua = false; 
        }
    }

    if (!nuovoStatoEsito.equals("0")) {
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
	Date isRicevutaPresente(final MailReceiver mailReceiver, final String referenceId,
			SearchTerm filtroAggiuntivoRicevuta) throws MessagingException {
		Date dataRicevuta = null;
		boolean presente = false;
		final HeaderTerm riferimentoMessageIdTerm = new HeaderTerm(X_RIFERIMENTO_MESSAGE_ID_HEADER, referenceId);
		Message[] messaggi = mailReceiver.filterMail(new AndTerm(riferimentoMessageIdTerm, filtroAggiuntivoRicevuta));
		if (messaggi != null && messaggi.length > 0) {
			// ciclo sui messaggi che rispettano la ricerca
			for (int i = 0; i < messaggi.length && !presente; i++) {
				String[] msgReferenceIds = messaggi[i].getHeader(X_RIFERIMENTO_MESSAGE_ID_HEADER);
				if (msgReferenceIds != null) {
					// si controllano i reference id recuperati perchè il fitro estrae per match in
					// like e non per match esatto
					for (int j = 0; j < msgReferenceIds.length && !presente; j++) {
						// il match deve essere esatto
						if (msgReferenceIds[j].equals(referenceId)) {
							presente = true;
							dataRicevuta = messaggi[i].getSentDate();
						}
					}
				}
			}
		}

		return dataRicevuta;
	}

}
