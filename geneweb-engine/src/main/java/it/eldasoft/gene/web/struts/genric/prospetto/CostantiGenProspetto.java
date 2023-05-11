/*
 * Created on 07-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.prospetto;

/**
 * Classe di constanti per le action relative ai report con modello
 * 
 * @author Luca.Giacomazzo
 */
public class CostantiGenProspetto {

  /** Nome oggetto presente nel request e contenente l'id del modello */
  public static final String ATTRIBUTO_REQUEST_ID_PROSPETTO        = "idProspetto";

  /** Nome oggetto presente nel request e contenente il modello */
  public static final String ATTRIBUTO_REQUEST_DATI_GEN_PROSPETTO  = "datiGenProspettoForm";

  /** Nome oggetto presente nel request e contenente un parametro del modello */
  public static final String ATTRIBUTO_REQUEST_PARAMETRO_PROSPETTO = "parametroModelloForm";

  public static final String ATTRIBUTO_LISTA_TIPI_PARAMETRI        = "listaValoriTabellati";

  public static final String FORWARD_ERRORE_INSERIMENTO_PROSPETTO  =  "errorInsertProspetto";
  
  public static final String FORWARD_ERRORE_DOWNLOAD_DETTAGLIO   = "errorDownloadDaDettaglio";

  public static final String FORWARD_ERRORE_DOWNLOAD_MODIFICA    = "errorDownloadDaModifica";
  
  public static final String SUCCESS_ELIMINA                       = "lista";
  
  public static final String SUCCESS_VISUALIZZA = "successVisualizza";
  
  public static final String SUCCESS_MODIFICA   = "successModifica";
  
  /** Voci del menu a tab delle pagine di dettaglio di una ricerca con modello */
  public static final String TAB_DATI_GENERALI                     = "Dati Generali";
  public static final String TAB_GRUPPI                            = "Gruppi";
  public static final String TAB_PARAMETRI                         = "Parametri";
  
}