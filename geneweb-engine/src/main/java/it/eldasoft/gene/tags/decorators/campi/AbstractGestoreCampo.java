package it.eldasoft.gene.tags.decorators.campi;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumn;

import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;

/**
 * Classe astratta che si incarica di implementare un gestore di un campo
 * 
 * @author cit_franceschin
 * 
 */
public abstract class AbstractGestoreCampo {

  protected CampoDecoratorImpl campo;

  private ServletContext       servletContext;

  private PageContext          pageContext;

  protected ResourceBundle     resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /**
   * Gestione delle modifica del valore prima delle visualizzazione sulla
   * maschera
   * 
   * @param valore
   *        Valore da convertire
   * @return Valore convertito. null se non gestito
   */
  abstract public String getValore(String valore);

  /**
   * Funzione che da il valore da scrivere nel campo in visualizzazione
   * 
   * @param valore
   *        Valore da cambiare in visualizzazione
   * @return null se non gestito altrimenti valore da stampare nella form (può
   *         essere anche un html).
   */
  abstract public String getValorePerVisualizzazione(String valore);

  /**
   * funzione richiamata prima di eseguire il set del parametro per l'update sul
   * DB
   * 
   * @param valore
   * @return valore da settare nel database
   */
  abstract public String getValorePreUpdateDB(String valore);

  /**
   * Funzione che viene chiamata prima dell'aggiunta dell'HTML
   * 
   * @param visualizzazione
   *        flag che dice se si è in visualizzazione
   * @return
   */
  abstract public String preHTML(boolean visualizzazione, boolean abilitato);

  /**
   * Personalizzazione della creazione dell'html del campo
   * 
   * @param visualizzazione
   *        flag che dice se si è in visualizzazione
   * @return Html da mettere nella pagina. null se non viene gestito
   */
  abstract public String getHTML(boolean visualizzazione, boolean abilitato);

  /**
   * Funzione che viene chiamata dopo dell'aggiunta dell'HTML
   * 
   * @param visualizzazione
   *        flag che dice se si è in visualizzazione
   * @return
   */
  abstract public String postHTML(boolean visualizzazione, boolean abilitato);

  /**
   * Funzione che imposta la classe da aggiungere all'edit
   * 
   * @return Classe da utilizzare. null se non implementato
   */
  abstract public String getClasseEdit();

  /**
   * Classe utilizata in visualizzazione
   * 
   * @return Classe da utilizzare. null se non implementato
   */
  abstract public String getClasseVisua();

  /**
   * Funzione che esegue l'inizializzazione del gestore di campo
   */
  abstract protected void initGestore();

  /**
   * Funzione che viene chiamata dalal form di trova per gestire la where
   * specifica su un campo
   * 
   * @param params
   *        Vettore che dovrà contenere i parametri impostati nella where
   * @param col
   *        Colonna con il valore impostato
   * @param conf
   *        Stringa di confronto
   * @param manager
   *        Eventuale SqlManager
   * @return null se non gestito; Altrimenti la where si filtro sul campo
   */
  abstract public String gestisciDaTrova(Vector params,
      DataColumn col, String conf, SqlManager manager);

  public CampoDecoratorImpl getCampo() {
    return campo;
  }

  public void setCampo(CampoDecoratorImpl campo, ServletContext context) {

    this.campo = campo;
    this.setServletContext(context);
    // Chiamo l'inizializzazione del gestore
    this.initGestore();
  }

  public String getDefaultHtml(boolean keyPress) {
    return this.campo.getDefaultHtml(keyPress);
  }

  /**
   * @return Returns the servletContext.
   */
  public ServletContext getServletContext() {
    return servletContext;
  }

  /**
   * @param servletContext
   *        The servletContext to set.
   */
  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
  }

  /**
   * @return Returns the pageContext.
   */
  public PageContext getPageContext() {
    return pageContext;
  }

  /**
   * @param pageContext
   *        The pageContext to set.
   */
  public void setPageContext(PageContext pageContext) {
    this.pageContext = pageContext;
  }

}
