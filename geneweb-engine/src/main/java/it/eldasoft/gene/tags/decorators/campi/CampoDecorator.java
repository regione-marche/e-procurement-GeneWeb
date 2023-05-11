package it.eldasoft.gene.tags.decorators.campi;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Classe che aggiunge dei parametri ad un campo decorator implementato per
 * poter essere gestito all'interno di un tag HTML
 *
 * @author cit_franceschin
 *
 */
public class CampoDecorator extends CampoDecoratorImpl implements
    CampoInterface {

  // ************************************************************
  // Storia Modifiche:
  // Data Utente Descrizione
  // 09/11/2006 M.F. Aggiunta delle definizione sul campo
  // ************************************************************

  /*
   * Variabili aggiunte per l'implementazione generica di tutti i campi
   */
  /** Where da aggiungere al campo */
  private String  where;

  /** Eventuale from del campo */
  private String  from;

  /** Eventuale body se implementato con un BodyTagSupport */
  private String  body;

  /** Mnemonico del campo */
  private String  mnemonico;

  /** Definizione del campo */
  private String  definizione;

  /**
   * Flag per dire che si tratta di un campo di tipo computed (quindi non è di
   * nessuna entità )
   */
  private boolean computed;

  /**
   * Flag che dice se il campo gestisce o meno le protezioni standard
   */
  private Boolean gestisciProtezioni;

  /**
   * Costruttore settaggi dei dati dal campo
   *
   */
  public CampoDecorator() {
    this.where = null;
    this.from = null;
    this.body = null;
    this.computed = false;
    this.gestisciProtezioni = null;
  }

  public CampoDecorator(boolean campoInPaginaTrova){
    this();
    this.isCampoInPaginaTrova = campoInPaginaTrova;
  }

  /**
   * Funzione che restituisce il tipo per i javascript
   *
   * @param campo
   *        Campo da cui estrarre il tipo
   * @return Tipo di campo
   */
  public String getTipoPerJS(Campo campo) {
    String lsRet = "";
    try{


	    switch (campo.getTipoColonna()) {
	    case Campo.TIPO_DATA:
        case Campo.TIPO_TIMESTAMP:
	      lsRet = "D";
	      break;
	    case Campo.TIPO_DECIMALE:
	      lsRet = "F";
	      lsRet += campo.getLunghezza();
	      if (campo.getDecimali() > 0)
	        lsRet += "." + campo.getDecimali();
	      else
	        lsRet += ".2";
	      break;
	    case Campo.TIPO_INTERO:
	      lsRet = "N";
	      lsRet += campo.getLunghezza();
	      break;
	    case Campo.TIPO_NOTA:
	      lsRet = "T";
	      lsRet += campo.getLunghezza();
	      break;
	    case Campo.TIPO_STRINGA:
	      lsRet = "T";
	      lsRet += campo.getLunghezza();
	      break;

	    }
	    // Se si tratta di un tabellato allora aggiungo E all'inizio
	    if (campo.isTab()) {
	      // Se si tratta di un tabellato verifico in funzione del tipo di
	      // tabellato
	      switch (TabellatiManager.getNumeroTabellaByCodice(campo.getCodiceTabellato())) {
	      case 1: // Tab1
	        return "EN5";
	      case 0:
	        return "ET5";
	      case 2:
	        return "ET5";
	      case 3:
	        return "ET5";
	      case 5:
	        return "ET15";
	      }
	      return "E" + lsRet;
	    }

    } catch (NullPointerException e){
		lsRet = this.definizione;
	}
    return lsRet;
  }

  /**
   * @param campo
   *        The campo to set.
   * @param context
   *        Page context per l'implementazione della lettura dei tabellati
   * @throws JspException
   *         Eccezione d'errore nel settaggio del campo
   */
  public void setCampo(Campo campo, PageContext context) throws JspException {
    if (context != null) {
      this.setJs(UtilityTags.getJavascript(context));
    }
    if (campo != null) {
      // Se è impostata la definizione le imposto
      if (this.definizione != null)
        this.setDefinizioneCampo(this.definizione, context);

      this.setNome(UtilityTags.getNomeFisicoPerHTML(campo.getNomeFisicoCampo()));
      if (this.getTipo() == null || this.getTipo().equals(""))
        this.setTipo(this.getTipoPerJS(campo));
      if (this.gestore == null) this.setDominio(campo.getDominio(), context);
      if (this.getTitle() == null) this.setTitle(campo.getDescrizioneWEB());
      // Se non è ancora settato setto il tooltip
      if (this.tooltip == null) this.setTooltip(campo.getDescrizione());
      // Setto il flag con il campo chiave
      this.setChiave(campo.isCampoChiave());
      // Setto il nome fisico del campo
      this.setNomeFisico(campo.getNomeFisicoCampo());
      this.mnemonico = campo.getCodiceMnemonico();
      if (campo.isTab() && this.getValori().size() == 0) {
        // Si tratta di un tabellato quindi devo eseguire la select sul
        // database
        TabellatiManager mm = (TabellatiManager) UtilitySpring.getBean(
            "tabellatiManager", context, TabellatiManager.class);
        List tab = mm.getTabellato(campo.getCodiceTabellato());
        // Di default aggiungo il valore vuoto
        this.addValore("", " ");
        // Scorro tutti i valori tabellati
        for (int i = 0; i < tab.size(); i++) {
          Tabellato val = (Tabellato) tab.get(i);
          if ("1".equals(val.getArcTabellato())) {
            this.addValore(val.getTipoTabellato(), val.getDescTabellato(), val.getArcTabellato());
          } else {
            this.addValore(val.getTipoTabellato(), val.getDescTabellato());
          }
        }
      }
    } else {
      if (this.definizione != null)
        this.setDefinizioneCampo(this.definizione, context);
      else
        throw new JspException(
            "Il campo \""
                + this.getEntita()
                + "."
                + this.getCampo()
                + "\" non esiste nei metadati o non è stata settata la sua definizione");
    }
  }

  /**
   * Funzione che setta la definizione del campo. La definizione del campo è
   * divisa da ;
   *
   * @param definizione
   *        Definizione così formata: </br>
   *        <ul>
   *        <li><b>[TipoCampo];[IsKey];[TAB];[Dominio];[Mnemonico]</b></li>
   *        <li><b>[TipoCampo]</b> Tipo del campo che assume i valori: <b>D</b>
   *        (data) <b>Fnn.dd</b> (Decimale) <b>Nnn</b> (Numerico) <b>Tnn</b>
   *        (Testo)</li>
   *        <li><b>[Iskey]</b> 1 se chiave; 0 se non è chiave</li>
   *        <li><b>[TAB]</b> Tabellato (se tabellato)</li>
   *        <li><b>[Dominio]</b> Eventuale dominio sul campo: <b>SN</b> Campo
   *        Si/No <b>NOTE</b> Campo note <b>MONEY</b> Campo money <b>DATA_ELDA</b>
   *        campo data <b>TIMESTAMP</b> campo data/ora (con ore minuti e secondi)</li>
   *        <li><b>[Mnemonico]</b> Eventuale mnemonico del campo</li>
   *        </ul>
   */
  public void setDefinizioneCampo(String definizione, PageContext context) {
    String tipo = "T100";
    boolean key = false;
    String mnemonico = null;
    String lTab = null;
    String dominio = null;
    String def[] = UtilityTags.stringToArray(definizione, ';');
    if (def.length > 0) tipo = def[0];
    if (def.length > 1) key = def[1].equals("1");
    if (def.length > 2) lTab = def[2];
    if (def.length > 3 && def[3].length() > 0) dominio = def[3];
    if (def.length > 4 && def[4].length() > 0) mnemonico = def[4];

    String nomeFisico = "";
    if (this.getEntita() != null && this.getEntita().length() > 0)
      nomeFisico += this.getEntita() + ".";
    if (this.getCampo() != null) nomeFisico += this.getCampo();
    this.setNome(UtilityTags.getNomeFisicoPerHTML(nomeFisico));
    this.setTipo(tipo);
    if (this.gestore == null) this.setDominio(dominio, context);
    // Setto il flag con il campo chiave
    this.setChiave(key);
    // Setto il nome fisico del campo
    this.setNomeFisico(nomeFisico);
    this.mnemonico = mnemonico;

    // SS 10-12-2008: introdotta la valorizzazione del titolo se non
    // valorizzato, a partire dalla descrizione associata al mnemonico (utile
    // per le schede multiple, in cui non si necessiterà più di definire campi
    // scheda fittizi indicando ogni volta il titolo)
    if (this.getTitle() == null && mnemonico != null) {
      Campo campo = DizionarioCampi.getInstance().get(mnemonico);
      if (campo != null) this.setTitle(campo.getDescrizioneWEB());
    }

    if (lTab != null && lTab.length() > 0) {
      // Si tratta di un tabellato quindi devo eseguire la select sul
      // database
      TabellatiManager mm = (TabellatiManager) UtilitySpring.getBean(
          "tabellatiManager", context, TabellatiManager.class);
      List tab = mm.getTabellato(lTab);
      this.addValore("", " ");
      // Scorro tutti i valori tabellati
      for (int i = 0; i < tab.size(); i++) {
        Tabellato val = (Tabellato) tab.get(i);
        if ("1".equals(val.getArcTabellato())) {
          this.addValore(val.getTipoTabellato(), val.getDescTabellato(), val.getArcTabellato());
        } else {
          this.addValore(val.getTipoTabellato(), val.getDescTabellato());
        }
      }
      // {MF 200907} Aggiungo il tipo enumerato se ha valori cosi la
      // rapptesentazione risulta corretta
      if (tipo.charAt(0) != JdbcParametro.TIPO_ENUMERATO)
        this.setTipo(JdbcParametro.TIPO_ENUMERATO + tipo);
    }

  }

  /**
   * @return Returns the from.
   */
  public String getFrom() {
    return from;
  }

  /**
   * @param from
   *        The from to set.
   */
  public void setFrom(String from) {
    this.from = from;
  }

  /**
   * @return Returns the where.
   */
  public String getWhere() {
    return where;
  }

  /**
   * @param where
   *        The where to set.
   */
  public void setWhere(String where) {
    this.where = where;
  }

  /**
   * Trasformazione in stringa
   */
  @Override
  public String toString() {
    if (this.getJs() != null) return super.toString(this.getJs());
    return super.toString();
  }

  /**
   * @return Returns the body.
   */
  public String getBody() {
    if (this.body == null) return "";
    return body;
  }

  /**
   * @param body
   *        The body to set.
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * @return Returns the mnemonico.
   */
  public String getMnemonico() {
    return mnemonico;
  }

  /**
   * @return Returns the definizione.
   */
  public String getDefinizione() {
    return definizione;
  }

  /**
   * @param definizione
   *        The definizione to set.
   */
  public void setDefinizione(String definizione) {
    this.definizione = definizione;
  }

  public boolean isComputed() {
    return computed;
  }

  public void setComputed(boolean computed) {
    this.computed = computed;
  }

  public boolean isGestisciProtezioni() {
    if (isSetGestisciProtezioni()) return gestisciProtezioni.booleanValue();
    return false;
  }

  public void setGestisciProtezioni(boolean gestisciProtezioni) {
    this.gestisciProtezioni = new Boolean(gestisciProtezioni);
  }

  /**
   * Funzione che verifica se è stato settato il campo con la gestione delle
   * proitezioni
   *
   * @return
   */
  public boolean isSetGestisciProtezioni() {
    if (this.gestisciProtezioni != null) return true;
    return false;

  }

}
