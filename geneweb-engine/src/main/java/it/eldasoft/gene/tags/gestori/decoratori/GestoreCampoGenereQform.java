/*
 * Created on 24/feb/2021
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.tags.gestori.decoratori;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato;
import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.gene.web.struts.admin.AccountAction;
import it.eldasoft.utils.spring.UtilitySpring;


public class GestoreCampoGenereQform extends AbstractGestoreCampo {

	 @Override
	  public String getValore(String valore) {

	    return null;
	  }

	  @Override
	  public String getValorePerVisualizzazione(String valore) {

	    return null;
	  }

	  @Override
	  public String getValorePreUpdateDB(String valore) {

	    return null;
	  }

	  @Override
	  public String preHTML(boolean visualizzazione, boolean abilitato) {

		Boolean OPGara= (Boolean) this.getPageContext().getAttribute("OPGara",
				PageContext.REQUEST_SCOPE);
		
		Boolean OPElenco= (Boolean) this.getPageContext().getAttribute("OPElenco",
				PageContext.REQUEST_SCOPE);

	    if(!OPGara){
	    	this.getCampo().getValori().remove(1);
	    }

	    if(!OPElenco){
	    	this.getCampo().getValori().remove(2);
	    }
	    
	    return null;
	  }

	  @Override
	  public String getHTML(boolean visualizzazione, boolean abilitato) {

	    return null;
	  }

	  @Override
	  public String postHTML(boolean visualizzazione, boolean abilitato) {

	    return null;
	  }

	  @Override
	  public String getClasseEdit() {

	    return null;
	  }

	  @Override
	  public String getClasseVisua() {

	    return null;
	  }

	  @Override
	  protected void initGestore() {


	  }

	  @Override
	  public String gestisciDaTrova(Vector params, DataColumn col, String conf, SqlManager manager) {

	    return null;
	  }

}
