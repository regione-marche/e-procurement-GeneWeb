/*
 * Created on 06/mag/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.wizard;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;

/**
 * Classe contenente le costanti generali utilizzate per la gestione a wizard
 * 
 * @author Stefano.Sabbadin
 */
public class CostantiWizard {

  /**
   * Nome del parametro nel request contenente l'entità principale del wizard
   */
  public static final String HIDDEN_PARAMETRO_ENTITA_PRINCIPALE_WIZARD   = "entitaPrincipale";

  /**
   * Nome del parametro nel request contenente il gestore per il salvataggio dei
   * dati del wizard
   */
  public static final String HIDDEN_PARAMETRO_GESTORE_SALVATAGGIO_WIZARD = "gestoreSalvataggio";

  /**
   * Nome del parametro nel request contenente la url da aprire in seguito alla
   * chiusura del wizard
   */
  public static final String HIDDEN_PARAMETRO_PAGINA_FINE_WIZARD         = "hrefFineWizard";

  /** Codice identificativo di una pagina di dettaglio */
  public static final String TIPO_PAGINA_DETTAGLIO                       = "DETTAGLIO";
  /**
   * Codice identificativo di una pagina di domanda per l'inserimento di un
   * nuovo elemento in una lista
   */
  public static final String TIPO_PAGINA_LISTA_DOMANDA                   = "LISTA_DOMANDA";
  /**
   * Codice identificativo di una pagina di inserimento nuovo elemento in una
   * lista
   */
  public static final String TIPO_PAGINA_LISTA_NUOVO                     = "LISTA_NUOVO";
  /**
   * Nome parametro del form del wizard contenente la tipologia di pagina
   */
  public static final String HIDDEN_PARAMETRO_TIPO_PAGINA                = "tipoPaginaWizard";
  /**
   * Nome parametro del form del wizard contenente il progressivo di sottopagina
   * del wizard, usato ad esempio nelle pagine relative a liste (domanda +
   * inserimento)
   */
  public static final String HIDDEN_PARAMETRO_SOTTO_PAGINA               = "sottoPaginaWizard";

  /**
   * Nome parametro del form del wizard contenente il numero totale delle pagine
   * definite e visibili nella creazione guidata per l'utente (per pagine si
   * intendono le pagine evidenziate nell'avanzamento, ovvero i tag
   * paginaWizard)
   */
  public static final String HIDDEN_PARAMETRO_NUMERO_PAGINE              = "numPagineWizard";

  /**
   * Campo discriminante presente in una pagina di domanda per l'inserimento di
   * un nuovo elemento in una lista, e contiene il nome del campo presente nel
   * form che indica se procedere o meno con un nuovo inserimento
   */
  public static final String HIDDEN_CAMPO_DISCRIMINANTE_DOMANDE          = "DISCR_DOMANDA_WIZARD";

  /** Suffisso del nome dell'oggetto che contiene in sessione i dati del wizard */
  public static final String NOME_OGGETTO_WIZARD_SESSIONE                = CostantiGenerali.PREFISSO_OGGETTO_DETTAGLIO
                                                                             + "Wizard";

}
