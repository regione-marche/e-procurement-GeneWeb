/*
 * Created on 27/mag/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori.plugin;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.SqlSelectTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Plugin per gestire il caricamento dei dati dinamici dell'impresa
 *
 * @author Francesco.DeFilippis
 */
public class GestoreImpresa extends AbstractGestorePreload {

  // private static final String NUMERO_INDIRIZZI = "numeroIndirizzi";
  private static final String ELENCO_INDCON             = "indcon";
  private static final String ELENCO_TIPO_INDIRIZZO     = "indtip";
  private static final String ELENCO_INDIRIZZI          = "indind";
  private static final String ELENCO_NUMERI_CIVICI      = "indnc";
  private static final String ELENCO_CAP                = "indcap";
  private static final String ELENCO_PROVINCIA          = "indpro";
  private static final String ELENCO_LOCALITA           = "indloc";
  private static final String ELENCO_CODICI_ISTAT       = "codcit";
  private static final String ELENCO_NUMERI_TEL         = "indtel";
  private static final String ELENCO_NUMERI_FAX         = "indfax";
  private static final String ELENCO_NAZIONE            = "nazimp";
  private static final String ELENCO_NUMERI_REA         = "regdit";

  public GestoreImpresa(BodyTagSupportGene tag) {
    super(tag);
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload#doBeforeBodyProcessing(javax.servlet.jsp.PageContext,
   *      java.lang.String)
   *
   * Inserisce nel request una serie di attributi da utilizzare per visualizzare
   * l'impresa
   */
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {


    // carico gli indirizzi dell'impresa
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        page, TabellatiManager.class);

    try {

      // si ottiene la chiave dell'entità padre, ovvero la IMPR
      HashMap keyParent = UtilityTags.stringParamsToHashMap(
          (String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
              PageContext.REQUEST_SCOPE), null);
      if (!modoAperturaScheda.equalsIgnoreCase("NUOVO")) {
        String codimp = ((JdbcParametro) keyParent.get("IMPR.CODIMP")).getStringValue();

        caricaIndirizzi(page, sqlManager, codimp);

      } else {
        pulisciPageContext(page);
      }

      //Lettura del tabellato G_072 per l'abilitazione della gestione gruppo iva
      String descTab = tabellatiManager.getDescrTabellato("G_072", "1");
      if(descTab!=null && !"".equals(descTab)) {
        descTab=descTab.substring(0, 1);
        boolean gestioneGruppoIva=false;
        if("1".equals(descTab))
          gestioneGruppoIva=true;
        page.setAttribute("isAttivaGestioneGruppoIva", gestioneGruppoIva, PageContext.REQUEST_SCOPE);
      }

      //Lettura del tabellato G_z13 per l'abilitazione della società cooperativa
      descTab = (String) sqlManager.getObject(
          "select tab2d1 from tab2 where tab2cod = ? and tab2tip = ? and (tab2arc is null or tab2arc = '2')",
          new Object[] { "G_z13", "1" });
      if(descTab!=null && !"".equals(descTab)) {
        page.setAttribute("valoreSocCooperativa", descTab, PageContext.REQUEST_SCOPE);
      }

      if ("VISUALIZZA".equals(modoAperturaScheda) || "MODIFICA".equals(modoAperturaScheda)) {
        //Per una ATI, nel caso di integrazione con portale alice e di ATI si deve ricavare
        //l'email della mandataria
        String codimp = ((JdbcParametro) keyParent.get("IMPR.CODIMP")).getStringValue();
        String select = "select tipimp from impr where codimp = ?";
        Long tipimp = (Long)sqlManager.getObject(select, new Object[]{codimp});
        String tipoImpresa="";
        if(tipimp!=null && !"".equals(tipimp) ){
          tipoImpresa = tipimp.toString();
        }
        page.setAttribute("tipologiaImpresa", tipoImpresa, PageContext.REQUEST_SCOPE);
        //Nel caso di integrazione con Portale alice, se l'impresa risulta registrata
        //si deve bloccare la modifica della pagina
        if(GeneManager.checkOP(page.getServletContext(), CostantiGenerali.OPZIONE_GESTIONE_PORTALE)){
          select="select count(IDUSER) from W_PUSER where USERENT = ?  and USERKEY1 = ?";
          Long numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{"IMPR",codimp});
          if(numOccorrenze!=null && numOccorrenze.longValue()>0)
            page.setAttribute("bloccoImpresaRegistrata","1", PageContext.REQUEST_SCOPE);

          //Recupera l'info se l'utente è amministratore di gare per abilitare la funzione
          // di 'Modifica dati registrati' nel caso di impresa registrata
          ProfiloUtente profilo = (ProfiloUtente) page.getSession().getAttribute(
              CostantiGenerali.PROFILO_UTENTE_SESSIONE);
          String abilitazioneGara=profilo.getAbilitazioneGare();
          if(abilitazioneGara!=null && "A".equals(abilitazioneGara))
            page.setAttribute("isAmministratoreGare", "SI", PageContext.REQUEST_SCOPE);
        }

        Calendar calendar = GregorianCalendar.getInstance();
        int annoCorrente = calendar.get( Calendar.YEAR );
        //Lista personale dipendente
        List listaPersonale = null;
        List listaPersonaleTemp = sqlManager.getListVector("select anno, numdip from impanno where codimp=? order by anno desc", new Object[]{codimp});
        if(listaPersonaleTemp!=null && listaPersonaleTemp.size()>0){
          //Si controlla se il primo elemento nella lista è relativo all'anno corrente, altrimenti si deve eliminare
          Long anno = SqlManager.getValueFromVectorParam(listaPersonaleTemp.get(0), 0 ).longValue();
          if((new Long(annoCorrente)).equals(anno)){
            listaPersonale = new Vector<Object>();
            //listaPersonale.add(((new Object[] { new Long(annoCorrente), null })));
            for(int i=1;i<listaPersonaleTemp.size();i++){
              listaPersonale.add(((new Object[] { SqlManager.getValueFromVectorParam(listaPersonaleTemp.get(i), 0 ).longValue()
                  , SqlManager.getValueFromVectorParam(listaPersonaleTemp.get(i), 1 ).longValue() })));
            }
          }else
            listaPersonale=listaPersonaleTemp;
        }else{
        //si devono visualizzare le annualità a partire dall’anno precedente a quello corrente
          listaPersonale = new Vector<Object>();
          listaPersonale.add(((new Object[] { new Long(annoCorrente - 1), null })));
          listaPersonale.add(((new Object[] { new Long(annoCorrente - 2), null })));
          listaPersonale.add(((new Object[] { new Long(annoCorrente - 3), null })));
        }

        page.setAttribute("listaPersonale", listaPersonale, PageContext.REQUEST_SCOPE);
      }else{
        //si devono visualizzare le annualità a partire dall’anno precedente a quello corrente
        Calendar calendar = GregorianCalendar.getInstance();
        int anno = calendar.get( Calendar.YEAR );
        List<Object> listaPersonale = new Vector<Object>();
        listaPersonale.add(((new Object[] { new Long(anno - 1), null })));
        listaPersonale.add(((new Object[] { new Long(anno - 2), null })));
        listaPersonale.add(((new Object[] { new Long(anno - 3), null })));
        page.setAttribute("listaPersonale", listaPersonale, PageContext.REQUEST_SCOPE);
      }

    } catch (SQLException e) {
      throw new JspException("Errore in fase di esecuzione delle select", e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante la conversione di un dato estratto mediante select",
          e);
    }
  }

  private void caricaIndirizzi(PageContext page, SqlManager sqlManager,
      String codimp) throws SQLException, GestoreException {

    // gestione indirizzi
    // Long countNumeroIndirizzi = null;
    List indirizzi = null;
    Vector datiIndirizzo = null;
    Vector indcon = new Vector();
    Vector indtip = new Vector();
    Vector indind = new Vector();
    Vector indnc = new Vector();
    Vector indcap = new Vector();
    Vector indpro = new Vector();
    Vector indloc = new Vector();
    Vector codcit = new Vector();
    Vector indtel = new Vector();
    Vector indfax = new Vector();
    Vector nazimp =  new Vector();
    Vector regdit = new Vector();

    // si verifica se nel DB esistono indirizzi
    // countNumeroIndirizzi = (Long) sqlManager.getObject(
    // "select count(*) from IMPIND where codimp5 = ?",
    // new Object[] { codimp });

    // si estrae l'elenco delle chiavi degli indirizzi delle imprese
    indirizzi = sqlManager.getListVector(
        "select indcon from impind where codimp5 = ? ", new Object[] { codimp });

    // si cicla sugli elementi estratti per valorizzare l'elenco degli
    // indirizzi da passare alla pagina
    for (int i = 0; i < indirizzi.size(); i++) {
      Long indiceIndirizzo = SqlManager.getValueFromVectorParam(
          indirizzi.get(i), 0).longValue();
      datiIndirizzo = SqlSelectTag.convertVectorString(sqlManager.getVector(
          "select indtip , indind, indnc, indcap, indpro, indloc, codcit, indtel, indfax, nazimp, regdit from impind where codimp5 = ? and indcon = ?",
          new Object[] { codimp, indiceIndirizzo }));

      indcon.add(indiceIndirizzo);
      indtip.add(datiIndirizzo.get(0));
      indind.add(datiIndirizzo.get(1));
      indnc.add(datiIndirizzo.get(2));
      indcap.add(datiIndirizzo.get(3));
      indpro.add(datiIndirizzo.get(4));
      indloc.add(datiIndirizzo.get(5));
      codcit.add(datiIndirizzo.get(6));
      indtel.add(datiIndirizzo.get(7));
      indfax.add(datiIndirizzo.get(8));
      nazimp.add(datiIndirizzo.get(9));
      regdit.add(datiIndirizzo.get(10));
    }

    page.setAttribute(ELENCO_TIPO_INDIRIZZO, indtip, PageContext.REQUEST_SCOPE);
    page.setAttribute(ELENCO_INDCON, indcon, PageContext.REQUEST_SCOPE);
    page.setAttribute(ELENCO_INDIRIZZI, indind, PageContext.REQUEST_SCOPE);
    page.setAttribute(ELENCO_NUMERI_CIVICI, indnc, PageContext.REQUEST_SCOPE);
    page.setAttribute(ELENCO_CAP, indcap, PageContext.REQUEST_SCOPE);
    page.setAttribute(ELENCO_PROVINCIA, indpro, PageContext.REQUEST_SCOPE);
    page.setAttribute(ELENCO_LOCALITA, indloc, PageContext.REQUEST_SCOPE);
    page.setAttribute(ELENCO_CODICI_ISTAT, codcit, PageContext.REQUEST_SCOPE);
    page.setAttribute(ELENCO_NUMERI_TEL, indtel, PageContext.REQUEST_SCOPE);
    page.setAttribute(ELENCO_NUMERI_FAX, indfax, PageContext.REQUEST_SCOPE);
    page.setAttribute(ELENCO_NAZIONE, nazimp, PageContext.REQUEST_SCOPE);
    page.setAttribute(ELENCO_NUMERI_REA, regdit, PageContext.REQUEST_SCOPE);

    // page.setAttribute(NUMERO_INDIRIZZI, countNumeroIndirizzi,
    // PageContext.REQUEST_SCOPE);
  }



  private void pulisciPageContext(PageContext page) {

    page.removeAttribute(ELENCO_CAP);
    page.removeAttribute(ELENCO_CODICI_ISTAT);
    page.removeAttribute(ELENCO_INDCON);
    page.removeAttribute(ELENCO_INDIRIZZI);
    page.removeAttribute(ELENCO_LOCALITA);
    page.removeAttribute(ELENCO_NUMERI_CIVICI);
    page.removeAttribute(ELENCO_NUMERI_FAX);
    page.removeAttribute(ELENCO_NUMERI_TEL);
    page.removeAttribute(ELENCO_PROVINCIA);
    page.removeAttribute(ELENCO_TIPO_INDIRIZZO);
    page.removeAttribute(ELENCO_NAZIONE);
    page.removeAttribute(ELENCO_NUMERI_REA);

  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload#doAfterFetch(javax.servlet.jsp.PageContext,
   *      java.lang.String)
   */
  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

}
