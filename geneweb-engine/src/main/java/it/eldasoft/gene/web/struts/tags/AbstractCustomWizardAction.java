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
package it.eldasoft.gene.web.struts.tags;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Interfaccia da estendere nel caso in cui si voglia definire un gestore di
 * navigazione sui comandi Avanti / Indietro / Fine / Salva di una pagina di un
 * wizard.<br>
 * E' importante estendere questa classe per la definizione di un gestore di
 * navigazione opportuno nel caso di inserimenti di più elementi in una stessa
 * entità (all'avanti occorre salvare in sessione i dati con un progressivo
 * opportuno e rimuovere dalla sessione stessa i dati inseriti nel form
 * ricevuto), o nel caso di modifiche allo standard di navigazione.
 * 
 * @author Stefano.Sabbadin
 */
public abstract class AbstractCustomWizardAction {

  /** Action per la gestione del wizard */
  private WizardAction action;

  /**
   * Costruttore standard in cui viene settata la action richiamante
   * 
   * @param action
   *        action del wizard richiamante
   */
  public AbstractCustomWizardAction(WizardAction action) {
    this.action = action;
  }

  /**
   * @return Ritorna action.
   */
  public WizardAction getAction() {
    return action;
  }

  /**
   * Integra l'operazione di arretramento dalla pagina attuale del wizard.<br>
   * Nell'implementazione del metodo, ritornare null se non si intende eseguire
   * alcuna operazione personalizzata.
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return forward necessario alla prosecuzione della gestione delle
   *         richiesta, null altrimenti
   * @throws IOException
   * @throws ServletException
   */
  public abstract ActionForward indietro(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException;

  /**
   * Integra l'operazione di avanzamento dalla pagina attuale del wizard.<br>
   * Nell'implementazione del metodo, ritornare null se non si intende eseguire
   * alcuna operazione personalizzata.
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return forward necessario alla prosecuzione della gestione delle
   *         richiesta, null altrimenti
   * @throws IOException
   * @throws ServletException
   */
  public abstract ActionForward avanti(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException;

  /**
   * Integra l'operazione di passaggio alla pagina finale del wizard.<br>
   * Nell'implementazione del metodo, ritornare null se non si intende eseguire
   * alcuna operazione personalizzata.
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return forward necessario alla prosecuzione della gestione delle
   *         richiesta, null altrimenti
   * @throws IOException
   * @throws ServletException
   */
  public abstract ActionForward fine(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException;

  /**
   * Integra l'operazione di salvataggio dei dati della creazione guidata.<br>
   * Nell'implementazione del metodo, ritornare null se non si intende eseguire
   * alcuna operazione personalizzata.
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return forward necessario alla prosecuzione della gestione delle
   *         richiesta, null altrimenti
   * @throws IOException
   * @throws ServletException
   */
  public abstract ActionForward salva(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException;

  /**
   * Consente la realizzazione di un'operazione speciale nella form. Questa
   * operazione viene scatenata in seguito alla chiamata del metodo extra di
   * WizardAction, ed in questo metodo va implementata anche la logica di
   * navigazione successiva oltre alla funzione specifica prevista.<br>
   * Nell'implementazione del metodo, ritornare null se non si intende eseguire
   * alcuna operazione personalizzata.
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return forward necessario alla prosecuzione della gestione delle
   *         richiesta, null altrimenti
   * @throws IOException
   * @throws ServletException
   */
  public abstract ActionForward extra(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException;
}
