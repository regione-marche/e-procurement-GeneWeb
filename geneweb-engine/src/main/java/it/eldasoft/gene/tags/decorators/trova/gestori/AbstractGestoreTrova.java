/*
 * Created on 21/nov/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.trova.gestori;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.decorators.trova.FormTrovaTag;
import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Classe astratta da estendere per la creazione di un gestore di ricerca.
 * 
 * @author Alvise Gorinati
 */
public abstract class AbstractGestoreTrova {

	// Costanti
	/**
	 * Numero pop-up.
	 */
	protected int popUpLevel;
	
	/**
	 * Logger della classe.
	 */
	protected final Logger LOGGER;
	
	/**
	 * Entità sulla quale si sta eseguendo la ricerca
	 */
	protected final String ENTITY;
	
	// Variabili
	/**
	 * Richiesta alla servlet
	 */
	protected HttpServletRequest request;
	
	// Costruttori
	/**
	 * Costruttore di default.
	 * 
	 * @param request
	 * 			Richesta alla servlet
	 * @param entity
	 * 			Entità sulla quale si sta eseguendo la ricerca
	 */
	public AbstractGestoreTrova(final Logger logger, final HttpServletRequest request, final String entity) {
		popUpLevel = 0;
		LOGGER = logger;
		ENTITY = entity;
		this.request = request;
	}
	
	// Metodi
	/**
	 * Esegue la ricerca componendo il filtro
	 */
	public final void trova() {
		final String filter = composeFilter();
		UtilityTags.putAttributeForSqlBuild(request.getSession(), ENTITY, popUpLevel, FormTrovaTag.CAMPO_FILTRO, filter);
	}
	
	/**
	 * Metodo da estendere per costruire il filtro di ricerca.
	 * 
	 * @param originalFilter
	 * 			Filtro originale passato dal tag "formTrova"
	 * 
	 * @return Nuovo fitro
	 */
	public abstract String composeFilter();
	
	// Getters & Setters
	public int getPopUpLevel() {
		return popUpLevel;
	}

}
