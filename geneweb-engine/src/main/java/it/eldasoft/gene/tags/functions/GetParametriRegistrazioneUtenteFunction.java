package it.eldasoft.gene.tags.functions;
/**
 * Funzione che determina elabora la configurazione
 * per la registrazione e ne passa alla request i parametri
 *
 * @author Cristian Febas
 */

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;
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
          if(arrayProfiliDisponibili.length == 1){
            //se ce n'e' uno solo non presento la sezione nel form di registrazione
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

    }

    return null;
  }
}
