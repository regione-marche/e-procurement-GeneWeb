/*
 * Creato 10/05/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.scadenz.ScadenzariManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
/**
 * Funzione per il ricalcolo dei termini e scadenze della lista delle attività
 *
 * @author Marcello Caminiti
 */
public class ListaAttivitaScadenzarioFunction extends AbstractFunzioneTag {

  private static final String KEY_SESSIONE_DATA_RICALCOLO_SCADENZARIO = "dataCalcoloScadenzario";
  private static final String KEY_SESSIONE_CHIAVE_SCADENZARIO         = "chiaviScadenzario";
  private static final String KEY_SESSIONE_ENTITA_SCADENZARIO         = "entitaScadenzario";
  private static final String FILTRO_ATTIVITA                         = "filtroAttivita";
  private static final int    INTERVALLO_RICALCOLO                    = 5;                       // in minuti

  public ListaAttivitaScadenzarioFunction() {
    super(4, new Class[] {PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    ScadenzariManager scadenzariManager = (ScadenzariManager) UtilitySpring.getBean("scadenzariManager",
        pageContext, ScadenzariManager.class);

    String codapp = (String) params[1];
    String ent = (String) params[2];
    String chiavi = (String) params[3];
    HttpSession sessione = pageContext.getSession();
    String entitaScadenzario = StringUtils.stripToNull((String)sessione.getAttribute(KEY_SESSIONE_ENTITA_SCADENZARIO));
    String chiaviScadenzario = StringUtils.stripToNull((String)sessione.getAttribute(KEY_SESSIONE_CHIAVE_SCADENZARIO));
    Date dataCalcoloScadenzario = (Date)sessione.getAttribute(KEY_SESSIONE_DATA_RICALCOLO_SCADENZARIO);

    // si esplode la chiave del record da cui partire
    Vector<JdbcParametro> campiChiave = UtilityTags.stringParamsToVector(chiavi, null);
    Object[] valoriChiave = new Object[campiChiave.size()];
    for (int i=0; i<campiChiave.size(); i++) {
      valoriChiave[i]=campiChiave.get(i).getValue();
    }

    // si calcola il lasso dall'ultimo ricalcolo del calendario
    long diffMinuti = INTERVALLO_RICALCOLO;
    Date dataAttuale =  new Date();
    if(dataCalcoloScadenzario!=null){
      diffMinuti = ( (dataAttuale.getTime() - dataCalcoloScadenzario.getTime())
          / (60000) );
    }

    try {
      //Nel caso in cui è cambiata l'entità o la chiave si deve annullare il valore della variabile di sessione filtroAttivita
      if (!(ent.equals(entitaScadenzario) && chiavi.equals(chiaviScadenzario)))
          sessione.removeAttribute(FILTRO_ATTIVITA);

      // si ricalcola l'algoritmo se cambia l'entita', la chiave, o e' trascorso l'intervallo massimo tra un ricalcolo e un altro
      if (!(ent.equals(entitaScadenzario) && chiavi.equals(chiaviScadenzario) && diffMinuti < INTERVALLO_RICALCOLO)) {
        scadenzariManager.updateDateScadenzarioEntita(ent, valoriChiave, codapp, false, null);
        sessione.setAttribute(KEY_SESSIONE_ENTITA_SCADENZARIO, ent);
        sessione.setAttribute(KEY_SESSIONE_CHIAVE_SCADENZARIO, chiavi);
        sessione.setAttribute(KEY_SESSIONE_DATA_RICALCOLO_SCADENZARIO, dataAttuale);
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante il ricalcolo dei termini e scadenze della lista delle attività", e);
    }

    return "";
  }

}