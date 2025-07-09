package it.eldasoft.gene.tags.functions;
/**
 * Funzione che determina elabora la configurazione
 * per la registrazione e ne passa alla request i parametri
 *
 * @author Cristian Febas
 */

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetParametriRegistrazioneUtenteFunction extends AbstractFunzioneTag {

  public GetParametriRegistrazioneUtenteFunction() {
    super(1, new Class[] {PageContext.class });
  }

  @Override
  public final String function(final PageContext pageContext, final Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    ServletContext context = pageContext.getServletContext();
    String appBloccata = (String) context.getAttribute(CostantiGenerali.SENTINELLA_BLOCCO_ATTIVAZIONE);
    appBloccata = UtilityStringhe.convertiNullInStringaVuota(appBloccata);
    if("1".equals(appBloccata)){
      //Non visualizzo il form di registrazione
      pageContext.setAttribute("appBloccata", appBloccata, PageContext.REQUEST_SCOPE);
    }else if (!"1".equals(ConfigManager.getValore(CostantiGenerali.PROP_ATTIVA_FORM_REGISTRAZIONE))){
      String msg= "Questo servizio di registrazione è dedicato alle utenze della Stazione Appaltante e non è al momento disponibile! " +
              "Richiedere al proprio referente interno alla Stazione Appaltante l'abilitazione della propria utenza.";
      UtilityStruts.addMessage(this.getRequest(), "error", "errors.generico",
              new Object[] {msg});
      String moduloAttivo= ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
      if ("PG".equals(moduloAttivo)){
        msg="Si ricorda agli Operatori Economici che possono registrarsi direttamente e autonomamente sul \"Portale Appalti\".";
        UtilityStruts.addMessage(this.getRequest(), "error", "errors.generico",
                new Object[] {msg});
      }

      pageContext.getResponse().reset();
      try {
        this.getRequest().getSession().setAttribute(CostantiGenerali.SENTINELLA_SESSION_TIMEOUT,"true");
        pageContext.forward("/Errore.do");
      } catch (ServletException | IOException e) {
        throw new JspException("Errore inaspettato durante l'inoltro alla pagina di errore generale", e);
      }
    }else{
      String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

      //Determinazione della configurazione per il Ruolo
      String ruolo = ConfigManager.getValore(CostantiGenerali.PROP_REG_RUOLO);
      ruolo = UtilityStringhe.convertiNullInStringaVuota(ruolo);
      if (!"".equals(ruolo)){
        pageContext.setAttribute("ruolo", ruolo, PageContext.REQUEST_SCOPE);
      }

      //Determinazione della configurazione per i profili disponibili
      String profiliDisponibili = ConfigManager.getValore(CostantiGenerali.PROP_REG_PROFILI_DISPONIBILI);
      profiliDisponibili = UtilityStringhe.convertiNullInStringaVuota(profiliDisponibili);
      if(!"".equals(profiliDisponibili)){
        String[] arrayProfiliDisponibili = profiliDisponibili.split(";");
        List<Object> listaProfiliDisponibili = new Vector<Object>();
        try {
          if(arrayProfiliDisponibili.length == 0){
            //se non ci sono profili disponibili non presento la sezione nel form di registrazione
            ;
          }else{
            for (int i = 0; i < arrayProfiliDisponibili.length; i++) {
              String cod_profilo = arrayProfiliDisponibili[i];
              String nome = "";
              String descrizione = "";
              Vector  datiW_PROFILI = sqlManager.getVector("select nome, descrizione from w_profili where cod_profilo = ? and codapp = ?",
                    new Object[] {cod_profilo, codapp });
              if (datiW_PROFILI != null && datiW_PROFILI.size() > 0) {
                if (((JdbcParametro) datiW_PROFILI.get(0)).getValue() != null) {
                  nome = ((JdbcParametro) datiW_PROFILI.get(0)).getStringValue();
                }
                if (((JdbcParametro) datiW_PROFILI.get(1)).getValue() != null) {
                  descrizione = ((JdbcParametro) datiW_PROFILI.get(1)).getStringValue();
                }
                listaProfiliDisponibili.add(new Object[] {cod_profilo, nome, descrizione});
              }
            }
            pageContext.setAttribute("listaProfiliDisponibili", listaProfiliDisponibili, PageContext.REQUEST_SCOPE);
          }

        } catch (SQLException e) {
          throw new JspException("Errore nella lettura della lista dei profili disponibili", e);
        }

      }

      pageContext.setAttribute("isUffintAbilitati", ConfigManager.getValore(CostantiGenerali.PROP_UFFINT_ABILITATI), PageContext.REQUEST_SCOPE);


      String indirizzoFileRelativo = ConfigManager.getValore("it.eldasoft.registrazione.fileCondizioniDUso");
      if (indirizzoFileRelativo != null && !"".equals(indirizzoFileRelativo.trim())) {
        String tomcatHome = pageContext.getServletContext().getRealPath(File.separator);
        indirizzoFileRelativo = indirizzoFileRelativo.trim();
        File modulo = new File(tomcatHome + indirizzoFileRelativo);
        if (modulo != null && modulo.length() > 0) {
          pageContext.setAttribute("moduloCondizioniDuso", indirizzoFileRelativo, PageContext.REQUEST_SCOPE);
        }
      }

      List<Object> listaValoriRuolo = new Vector<Object>();
      List<Object> listaValoriRuoloContratti = new Vector<Object>();
      listaValoriRuolo.add(new Object[] {"A", "Responsabile (accesso consentito a tutti i dati)" });
      listaValoriRuolo.add(new Object[] {"U", "Utente (accesso consentito solo ai dati assegnati)" });
      listaValoriRuoloContratti.add(new Object[] {"NDEFM", "Non definito" });
      pageContext.setAttribute("listaValoriRuolo", listaValoriRuolo, PageContext.REQUEST_SCOPE);

      String modelloFacsimile = ConfigManager.getValore(CostantiGenerali.PROP_REG_FACSIMILE);
      modelloFacsimile = UtilityStringhe.convertiNullInStringaVuota(modelloFacsimile);
      if (!"".equals(modelloFacsimile)){
        pageContext.setAttribute("isModelloFacSimile", "1", PageContext.REQUEST_SCOPE);
      }

      String isLoginCF = ConfigManager.getValore(CostantiGenerali.PROP_REG_LOGINCF);
      isLoginCF = UtilityStringhe.convertiNullInStringaVuota(isLoginCF);
      if ("1".equals(isLoginCF)){
        pageContext.setAttribute("isLoginCF", "1", PageContext.REQUEST_SCOPE);
      }
      
      String nomePagina = ConfigManager.getValore(CostantiGenerali.PROP_REGISTRAZIONE_NOME_PAGINA);
      if (nomePagina == null || nomePagina.trim().length() == 0 || "registrazione-account.jsp?modo=NUOVO".equals(nomePagina)) {
        boolean captchaIncludeNumbers=false;
        String f = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String c = "";
        for (int i = 0; i < 6; i++) {
          c += f.charAt((int) (Math.random() * (captchaIncludeNumbers ? 36 : 26)));
        }
        this.getRequest().getSession().setAttribute("captchaRegistrazione",c);
      }

    }

    return null;
  }
}
