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
package it.eldasoft.gene.web.struts.system.assistenza;

import it.eldasoft.gene.bl.system.RichiestaAssistenzaManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBase;
import it.eldasoft.gene.db.domain.system.ConfigurazioneRichiestaAssistenza;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action per il caricamento della configurazione dei parametri per la gestione
 * delle richieste di assistenza.
 *
 * @author Marco.Perazzetta
 */
public class ConfigurazioneRichiestaAssistenzaAction extends DispatchActionBase {

	static Logger logger = Logger.getLogger(ConfigurazioneRichiestaAssistenzaAction.class);

	private static final String FORWARD_SUCCESS_VISUALIZZA = "successVisualizza";
	private static final String FORWARD_SUCCESS_MODIFICA = "successModifica";
	private static final String FORWARD_SUCCESS_MODIFICA_PASSWORD = "successModificaPassword";

	private RichiestaAssistenzaManager richiestaAssistenzaManager;

	/**
	 * @param richiestaAssistenzaManager richiestaAssistenzaManager da settare
	 * internamente alla classe.
	 */
	public void setRichiestaAssistenzaManager(RichiestaAssistenzaManager richiestaAssistenzaManager) {
		this.richiestaAssistenzaManager = richiestaAssistenzaManager;
	}

	@Override
	protected String getOpzioneAcquistata() {
		return CostantiGenerali.OPZIONE_DEFAULT;
	}

	/**
	 * Funzione che restituisce le opzioni per accedere alla action visualizza
	 *
	 * @return opzioni per accedere alla action
	 */
	public CheckOpzioniUtente getOpzioniVisualizza() {
		return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
	}

	/**
	 * In visualizzazione, alla prima apertura, si cerca di aprire la
	 * configurazione specifica se esistente e non &egrave; stata selezionata una
	 * configurazione particolare (si arriva dal menu principale).
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.io.IOException
	 * @throws javax.servlet.ServletException
	 * @see
	 * it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
	 * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward visualizza(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
		logger.debug("visualizza: inizio metodo");

		String target = FORWARD_SUCCESS_VISUALIZZA;
		String codapp = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
		target = this.getConfig(target, request, codapp);

		if (FORWARD_SUCCESS_VISUALIZZA.equals(target)) {
			ConfigurazioneRichiestaAssistenzaForm cfg = (ConfigurazioneRichiestaAssistenzaForm) request.getAttribute(CostantiGenerali.CFG_RICHIESTA_ASSISTENZA_FORM);
			String modo = (String) request.getAttribute("modo");
			if (modo != null) {
				cfg.setModo(modo);
			}
			if (StringUtils.isBlank(cfg.getOggetto()) && !"0".equals(cfg.getModo())) {
				cfg.setOggetto(CostantiGenerali.DEFAULT_PROP_RICHIESTA_ASSISTENZA_OGGETTO);
			}
			if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MODO_WS.equals(modo) && !"0".equals(cfg.getModo()) && StringUtils.isBlank(cfg.getServizioUrl())) {
				cfg.setServizioUrl(CostantiGenerali.DEFAULT_PROP_RICHIESTA_ASSISTENZA_SERVIZIO_URL);
			}
		}
		logger.debug("visualizza: fine metodo");

		return mapping.findForward(target);
	}

	/**
	 * Funzione che restituisce le opzioni per accedere alla action modifica
	 *
	 * @return opzioni per accedere alla action
	 */
	public CheckOpzioniUtente getOpzioniModifica() {
		return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
	}

	/**
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.io.IOException
	 * @throws javax.servlet.ServletException
	 * @see
	 * it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
	 * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward modifica(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
		logger.debug("modifica: inizio metodo");

		String target = FORWARD_SUCCESS_MODIFICA;
		String codapp = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
		target = this.getConfig(target, request, codapp);

		ConfigurazioneRichiestaAssistenzaForm configForm = (ConfigurazioneRichiestaAssistenzaForm) request.getAttribute(CostantiGenerali.CFG_RICHIESTA_ASSISTENZA_FORM);
		if (configForm.getModo() == null || "0".equals(configForm.getModo())) {
			configForm.setMail(null);
			configForm.setServizioUrl(null);
			configForm.setServizioUsr(null);
			configForm.setServizioPwd(null);
			configForm.setServizioFileSize(null);
			configForm.setCodapp(codapp);
		}
		String modo = (String) request.getAttribute("modo");
		if (modo != null) {
			configForm.setModo(modo);
		}
		if (StringUtils.isBlank(configForm.getOggetto()) && !"0".equals(configForm.getModo())) {
			configForm.setOggetto(CostantiGenerali.DEFAULT_PROP_RICHIESTA_ASSISTENZA_OGGETTO);
		}
		if (CostantiGenerali.PROP_RICHIESTA_ASSISTENZA_MODO_WS.equals(modo) && !"0".equals(configForm.getModo()) && StringUtils.isBlank(configForm.getServizioUrl())) {
			configForm.setServizioUrl(CostantiGenerali.DEFAULT_PROP_RICHIESTA_ASSISTENZA_SERVIZIO_URL);
		}

		request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA, CostantiGenerali.DISABILITA_NAVIGAZIONE);

		logger.debug("modifica: fine metodo");
		return mapping.findForward(target);
	}

	/**
	 * Funzione che restituisce le opzioni per accedere alla action di modifica
	 * password
	 *
	 * @return opzioni per accedere alla action
	 */
	public CheckOpzioniUtente getOpzioniModificaPassword() {
		return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
	}

	/**
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws java.io.IOException
	 * @throws javax.servlet.ServletException
	 * @see
	 * it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
	 * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward modificaPassword(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
		return mapping.findForward(FORWARD_SUCCESS_MODIFICA_PASSWORD);
	}

	/**
	 * Estrae i dati della configurazione della richiesta di assistenza e popola
	 * il request con i dati estratti.
	 *
	 * @param target target della action struts, eventualmente da modificare
	 * @param request request HTTP
	 * @param codapp codice applicazione per cui estrarre la configurazione
	 * @return target della action struts, eventualmente modificato
	 */
	private String getConfig(String target, HttpServletRequest request, String codapp) {
		String messageKey = null;
		try {
			ConfigurazioneRichiestaAssistenza cfg = richiestaAssistenzaManager.getConfigurazione(codapp);
      // indico che la password esiste, ma non la passo nel form per non
			// mandarla erroneamente nell'HTML
			if (cfg.getServizioPwd() != null) {
				cfg.setServizioPwd("IMPOSTATA");
			}
			cfg.setOldModo(cfg.getModo());
			request.setAttribute(CostantiGenerali.CFG_RICHIESTA_ASSISTENZA_FORM, new ConfigurazioneRichiestaAssistenzaForm(cfg));
		} catch (DataAccessException e) {
			target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
			messageKey = "errors.database.dataAccessException";
			logger.error(this.resBundleGenerale.getString(messageKey), e);
			this.aggiungiMessaggio(request, messageKey);
		} catch (Throwable t) {
			target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
			messageKey = "errors.applicazione.inaspettataException";
			logger.error(this.resBundleGenerale.getString(messageKey), t);
			this.aggiungiMessaggio(request, messageKey);
		}
		return target;
	}

}
