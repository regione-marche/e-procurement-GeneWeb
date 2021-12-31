/*
 * Created on 01/feb/2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.system;

import it.eldasoft.gene.bl.PropsConfigManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.PropsConfig;
import it.eldasoft.gene.db.domain.system.ConfigurazioneRichiestaAssistenza;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * Manager per la business logic per l'interfacciamento con la configurazione
 * dei parametri di richiesta assistenza, presenti nella tabella W_CONFIG.
 *
 * @author Marco.Perazzetta
 */
public class RichiestaAssistenzaManager {

	PropsConfigManager propsConfigManager;

	/**
	 * @param propsConfigManager The propsConfigManager to set.
	 */
	public void setPropsConfigManager(PropsConfigManager propsConfigManager) {
		this.propsConfigManager = propsConfigManager;
	}

	/**
	 * @return Ritorna propsConfigManager.
	 */
	public PropsConfigManager getPropsConfigManager() {
		return propsConfigManager;
	}

	/**
	 * Estrae i parametri per la connessione al provider mail dalla W_CONFIG.
	 *
	 * @param codapp codice applicazione
	 * @return oggetto della classe ConfigurazioneRichiestaAssistenza contenente
	 * le informazioni relative alla configurazione della richiesta di assistenza
	 * mail e presenti nella W_CONFIG.
	 * @throws CriptazioneException eccezione ritornata nel caso di problemi di
	 * decifratura della password
	 */
	public ConfigurazioneRichiestaAssistenza getConfigurazione(String codapp)
					throws CriptazioneException {

		ConfigurazioneRichiestaAssistenza cfg = new ConfigurazioneRichiestaAssistenza();
		cfg.setCodapp(codapp);

		@SuppressWarnings("unchecked")
		List<PropsConfig> props = this.propsConfigManager.getPropertiesByPrefix(codapp, CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_PREFISSO);
		for (Iterator<PropsConfig> it = props.iterator(); it.hasNext();) {
			PropsConfig property = it.next();
			if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MODO.equals(property.getChiave())) {
				cfg.setModo(property.getValore());
			}
			if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_OGGETTO.equals(property.getChiave())) {
				cfg.setOggetto(property.getValore());
			}
			if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MAIL.equals(property.getChiave())) {
				cfg.setMail(property.getValore());
			}
			if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_URL.equals(property.getChiave())) {
				cfg.setServizioUrl(property.getValore());
			}
			if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_USR.equals(property.getChiave())) {
				cfg.setServizioUsr(property.getValore());
			}
			if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_PWD.equals(property.getChiave())) {
				cfg.setServizioPwd(property.getValore());
			}
			if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_FILE_SIZE.equals(property.getChiave())) {
				cfg.setServizioFileSize(property.getValore());
			}
		}

		// decripto la password
		if (cfg.getServizioPwd() != null) {
			ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(
							ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
							cfg.getServizioPwd().getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
			cfg.setServizioPwd(new String(decriptatore.getDatoNonCifrato()));
		}
		return cfg;
	}

	/**
	 * Estrae i parametri ATTIVI per l'invio di una richiesta di assistenza
	 * tramite mail o servizio dalla W_CONFIG estraendo quelli del codice
	 * applicazione in input, e se assenti, quelli del codice applicazione di
	 * default.
	 *
	 * @param codapp codice applicazione
	 * @return oggetto della classe ConfigurazioneMail contenente le informazioni
	 * relative alla configurazione mail e presenti nella W_CONFIG
	 * @throws CriptazioneException eccezione ritornata nel caso di problemi di
	 * decifratura della password
	 */
	public ConfigurazioneRichiestaAssistenza getConfigurazioneAttiva(String codapp)
					throws CriptazioneException {

		ConfigurazioneRichiestaAssistenza cfg = this.getConfigurazione(codapp);
		if (cfg.getOggetto() == null) {
			cfg = this.getConfigurazione(CostantiGenerali.CODICE_APPLICAZIONE_LIBRERIA_GENERALE_WEB);
		}
		return cfg;
	}

	/**
	 * Aggiorna le properties relative alle configurazioni per la richiesta di
	 * assistenza esclusa la password. Se viene variato l'url o la username del
	 * servizio, automaticamente si sbianca anche la password.
	 *
	 * @param config contenitore con i dati da salvare nel DB
	 */
	public void updateConfigurazione(ConfigurazioneRichiestaAssistenza config) {

		int numeroProperties = 6;
		// si verifica se è variata la username, ed in 
		// tal caso si svuota anche il campo password
		PropsConfig propertyUsr = this.propsConfigManager.getProperty(
						config.getCodapp(), CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_USR);
		if (propertyUsr != null && !StringUtils.equals(propertyUsr.getValore(), config.getServizioUsr())) {
			numeroProperties++;
		}

		PropsConfig[] props = new PropsConfig[numeroProperties];

		for (int i = 0; i < props.length; i++) {
			props[i] = new PropsConfig();
			props[i].setCodApp(config.getCodapp());
		}

		props[0].setChiave(CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MODO);
		props[0].setValore(config.getModo());

		props[1].setChiave(CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_OGGETTO);
		props[1].setValore(config.getOggetto());

		props[2].setChiave(CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MAIL);
		props[2].setValore(config.getMail());

		props[3].setChiave(CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_URL);
		props[3].setValore(config.getServizioUrl());

		props[4].setChiave(CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_USR);
		props[4].setValore(config.getServizioUsr());

		props[5].setChiave(CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_FILE_SIZE);
		props[5].setValore(config.getServizioFileSize());

		if (numeroProperties == 7) {
			// sono nel caso di cambio indirizzo servizio o username accesso allo
			// stesso, aggiungo la password (da sbiancare)
			props[6].setChiave(CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_PWD);
		}

		this.propsConfigManager.insertProperties(props);
	}

	/**
	 * Aggiorna la property relativa alla password del servizio di richiesta
	 * assistenza
	 *
	 * @param password la password da aggiornare nel DB
	 * @param codapp codice applicazione a cui viene riferita la password
	 * @throws CriptazioneException eccezione generata nel caso in cui la
	 * cifratura della password fallisca
	 */
	public void updatePassword(String password, String codapp)
					throws CriptazioneException {

		String passwordCifrata = null;
		if (password != null) {
			// solo nel caso di password valorizzata devo applicare la cifratura
			ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
							ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
							password.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
			passwordCifrata = new String(criptatore.getDatoCifrato());
		}

		PropsConfig[] props = {new PropsConfig()};
		props[0].setCodApp(codapp);
		props[0].setChiave(CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_SERVIZIO_PWD);
		props[0].setValore(passwordCifrata);

		this.propsConfigManager.insertProperties(props);
	}
}
