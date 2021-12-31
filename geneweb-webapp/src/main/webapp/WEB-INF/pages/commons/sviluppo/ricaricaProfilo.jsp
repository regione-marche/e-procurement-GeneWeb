<%
			/*
       * Created on: 17.44 26/10/2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
      /*
				Descrizione:
					Gestione dell'oggetto
				Creato da:
					Marco Franceschin
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<gene:template file="popup-template.jsp">
	
	<gene:setString name="titoloMaschera" value="RICARICA PROFILO"/>
	<gene:redefineInsert name="corpo">
		<big><big><b>Ricarica profilo con esito: ${gene:callFunction("it.eldasoft.gene.tags.functions.RicaricaProfiloFunction",pageContext)} !!!</b></big></big>
		
  </gene:redefineInsert>
	<gene:redefineInsert name="debugDefault" />
	<gene:redefineInsert name="gestioneHistory" />
</gene:template>
