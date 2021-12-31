<%
/*
 * Created on: 28-Apr-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 /* Scheda dell'utente */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="UTENT-scheda">
	<% // Settaggio delle stringhe utilizzate nel template %>
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetTitleFunction",pageContext,"UTENT")}' />
	<gene:redefineInsert name="corpo">
			<jsp:include page="utent-interno-scheda.jsp" />
	</gene:redefineInsert>
</gene:template>