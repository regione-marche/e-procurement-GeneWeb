package it.eldasoft.gene.tags.decorators.campi;

import it.eldasoft.gene.tags.js.Javascript;

/**
 * Interfaccia di base da cui i campi ereditano
 * 
 * @author cit_franceschin
 * 
 */
public interface CampoInterface {

  /**
   * @return Returns the abilitato.
   */
  public boolean isAbilitato();

  /**
   * @param abilitato
   *        The abilitato to set.
   */
  public void setAbilitato(boolean abilitato);

  /**
   * @return Returns the body.
   */
  public String getBody();

  /**
   * @param body
   *        The body to set.
   */
  public void setBody(String body);

  /**
   * @return Returns the campo.
   */
  public String getCampo();

  /**
   * @param campo
   *        The campo to set.
   */
  public void setCampo(String campo);

  /**
   * @return Returns the entita.
   */
  public String getEntita();

  /**
   * @param entita
   *        The entita to set.
   */
  public void setEntita(String entita);

  /**
   * @return Returns the formName.
   */
  public String getFormName();

  /**
   * @param formName
   *        The formName to set.
   */
  public void setFormName(String formName);

  /**
   * @return Returns the from.
   */
  public String getFrom();

  /**
   * @param from
   *        The from to set.
   */
  public void setFrom(String from);

  /**
   * @return Returns the href.
   */
  public String getHref();

  /**
   * @param href
   *        The href to set.
   */
  public void setHref(String href);

  /**
   * @return Returns the js.
   */
  public Javascript getJs();

  /**
   * @param js
   *        The js to set.
   */
  public void setJs(Javascript js);

  /**
   * @return Returns the nome.
   */
  public String getNome();

  /**
   * @param nome
   *        The nome to set.
   */
  public void setNome(String nome);

  /**
   * @return Returns the nomeFisico.
   */
  public String getNomeFisico();

  /**
   * @param nomeFisico
   *        The nomeFisico to set.
   */
  public void setNomeFisico(String nomeFisico);

  /**
   * @return Returns the tipo.
   */
  public String getTipo();

  /**
   * @param tipo
   *        The tipo to set.
   */
  public void setTipo(String tipo);

  /**
   * @return Returns the title.
   */
  public String getTitle();

  /**
   * @param title
   *        The title to set.
   */
  public void setTitle(String title);

  /**
   * @return Returns the value.
   */
  public String getValue();

  /**
   * @param value
   *        The value to set.
   */
  public void setValue(String value);

  /**
   * @return Returns the visualizzazione.
   */
  public boolean isVisualizzazione();

  /**
   * @param visualizzazione
   *        The visualizzazione to set.
   */
  public void setVisualizzazione(boolean visualizzazione);

  /**
   * @return Returns the where.
   */
  public String getWhere();

  /**
   * @param where
   *        The where to set.
   */
  public void setWhere(String where);

  /**
   * Funzione che aggiunge le''elenco on menu popup
   * 
   * @param titolo
   *        Titolo del meno
   * @param href
   *        Reference al menu
   */
  public void addPopUp(String titolo, String href);

  public boolean isActive();

  public void setActive(boolean active);

  public boolean isVisibile();

  public void setVisibile(boolean visibile);

  public String getTitleHref();

  public void setTitleHref(String titolo);

  public void setDefaultValue(String valore);

  public String getDefaultValue();

  /**
   * Estrazione della definizione del campo
   * 
   * @return
   */
  public String getDefinizione();

  /**
   * Settaffio della definizione del campo
   * 
   * @param definizione
   */
  public void setDefinizione(String definizione);

  /**
   * Funzione che setta se un determinato campo è un campo fittizio
   * 
   * @param fittizio
   */
  public void setCampoFittizio(boolean fittizio);

  /**
   * Funzione che restituisce true se il campo è fittizio
   * 
   * @return
   */
  public boolean isCampoFittizio();

  public boolean isComputed();

  public void setComputed(boolean computed);

  /**
   * Gestione delle protezioni
   * @return
   */
  public boolean isGestisciProtezioni();

  public void setGestisciProtezioni(boolean gestisciProtezioni);
  
  public String getSchema();
  
  /**
   * Restituisce true se il campo ha un menù speciale 
   * @return Speciale
   */
  public boolean isSpeciale();
  
  /**
   * Setta l'attributo "speciale"
   * @param speciale
   */
  public void setSpeciale(boolean speciale);

}
