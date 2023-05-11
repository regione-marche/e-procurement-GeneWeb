<%/*
       * Created on 02-Nov-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE LA DEFINIZIONE DELLE VOCI DEI MENU COMUNI A TUTTE LE APPLICAZIONI
%>
<!-- Inserisco la Tag Library -->
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!-- inserisco il mio tag -->
<gene:template file="popup-template.jsp">
	<!-- Settaggio delle stringhe utilizate nel template -->
	<c:set var="pratica" value="${key}" />
	 <c:set var="arraySottoelementi" value="${fn:split(pratica,':')}" />
	 <c:set var="codice" value="${arraySottoelementi[1]}" />
	<gene:setString name="titoloMaschera" value="Scheda utente: ${codice}"/>
	<gene:redefineInsert name="pulsanteNuovo" />
	<gene:redefineInsert name="corpo">
		<jsp:include page="utent-interno-scheda.jsp" />
  </gene:redefineInsert>
</gene:template>
