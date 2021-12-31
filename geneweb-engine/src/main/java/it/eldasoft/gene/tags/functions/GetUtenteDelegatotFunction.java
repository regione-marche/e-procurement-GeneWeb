/*
 * Created on 24/05/16
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 *  * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.www.PortaleAlice.PortaleAliceProxy;
import it.eldasoft.www.PortaleAlice.RisultatoStringaOutType;

import java.rmi.RemoteException;
import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

/**
 * Funzione per richiamare il servizio getUtenteDelegato del WS PortaleAliceSOAP
 *
 * @author Marcello Caminiti
 */
public class GetUtenteDelegatotFunction extends
		AbstractFunzioneTag {

    static Logger       logger = Logger.getLogger(GetUtenteDelegatotFunction.class);

	  public GetUtenteDelegatotFunction() {
		super( 3,new Class[] { PageContext.class, String.class, });
	  }

	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

	  String codice = (String) params[1];
	  String ret = null;
      String user = null;
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      String select="select USERNOME from W_PUSER where USERENT = ?  and USERKEY1 = ?";

      try {
        user = (String)sqlManager.getObject(select, new Object[]{"IMPR", codice});
      } catch (SQLException e) {
		throw new JspException("Errore nell'estrarre l'user name dell'impresa " + codice, e);
      }
      if (user != null && !"".equals(user)){
        //Chiamata al servizio
        PortaleAliceProxy proxy = new PortaleAliceProxy();
        //indirizzo del servizio letto da properties
        String endPoint = ConfigManager.getValore(CostantiGenerali.PROP_URL_WEB_SERVICE_PORTALE_ALICE);
        proxy.setEndpoint(endPoint);
        try {
          RisultatoStringaOutType risultato = proxy.getUtenteDelegatoImpresa(user);
          String codiceErrore = risultato.getCodiceErrore();
          if (codiceErrore!=null && !"".equals(codiceErrore)){
            if(codiceErrore.indexOf("UNKNOWN-USER")>=0){
              this.getRequest().setAttribute("utenteNonDefinito", "1");
            }
            logger.error("Errore durante la chiamata del servizio getUtenteDelegatoImpresa per l'impresa  " + codice + " : utente non definito");
          }else{
            ret = risultato.getRisultato();
            this.getRequest().setAttribute("username", user);
          }
        } catch (RemoteException e) {
          logger.error("Errore durante la chiamata del servizio getUtenteDelegatoImpresa per l'impresa" + codice, e);
          throw new JspException(
              "Errore nella chiamata del servizio getUtenteDelegatoImpresa del WS PortaleAliceSOAP per l'impresa" + codice, e);
        }
      }

      return ret;
	}




}