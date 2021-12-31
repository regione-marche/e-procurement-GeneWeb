<%
			/*
       * Created on: 12.10 01/06/2007
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
					Popup del dettaglio delle via
				Creato da:
					Marco Franceschin
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<!-- inserisco il mio tag -->
<gene:template file="popup-template.jsp">
	<!-- Settaggio delle stringhe utilizate nel template -->
	<gene:setString name="titoloMaschera" 
		value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetTitleFunction",pageContext, "ASTRA")}'/>
	<gene:redefineInsert name="corpo">
		<jsp:include page="astra-interno-scheda.jsp" />
  </gene:redefineInsert>
</gene:template>
