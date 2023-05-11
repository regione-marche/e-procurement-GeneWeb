/*
 * Created on 23-feb-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.domain;

/**
 * Classe di costanti generali per la generazione di report excel/pfd
 *
 * @author Marco Perazzetta
 */
public class CostantiGeneraliJReports {

	/**
	 * default folder dove sono contenuti tutti i report jasper
	 */
	public static final String JR_REPORTS_SOURCE_FOLDER = "/WEB-INF/jrReport/";

	/**
	 * nome della folder che contiene le immagini dei report
	 */
	public static final String JR_REPORTS_IMAGES = "images/";

	/**
	 * nome della folder che contiene i subreport
	 */
	public static final String JR_REPORTS_SUBREPORTS = "subreports/";

	/**
	 * default folder dove sono contenute le immagini dei report jasper
	 */
	public static final String JR_REPORTS_IMAGES_FOLDER = JR_REPORTS_SOURCE_FOLDER + JR_REPORTS_IMAGES;

	/**
	 * default folder dove sono contenuti tutti i subreport che compongono i
	 * report jasper
	 */
	public static final String JR_REPORTS_SUBREPORTS_FOLDER = JR_REPORTS_SOURCE_FOLDER + JR_REPORTS_SUBREPORTS;
}
