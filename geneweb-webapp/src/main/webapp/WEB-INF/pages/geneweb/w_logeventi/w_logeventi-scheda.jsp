<%
/*
 * Created on: 02/03/2018
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
/* Form di ricerca dell'accesso all'applicativo (W_LOGEVENTI) */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENEWEB" idMaschera="LogEventiScheda">

	<gene:redefineInsert name="schedaModifica"/>
	<gene:redefineInsert name="schedaNuovo"/>
	<gene:redefineInsert name="modelliPredisposti" />
	<gene:redefineInsert name="noteAvvisi" />
	<gene:redefineInsert name="documentiAssociati" />
	
	<gene:redefineInsert name="corpo" >
		<gene:formScheda entita="W_LOGEVENTI">
			<gene:campoScheda campo="IDEVENTO"/>
			<gene:campoScheda campo="DATAORA"/>
			<gene:campoScheda campo="LIVEVENTO"/>
			<gene:campoScheda campo="CODEVENTO"/>
			<gene:campoScheda campo="OGGEVENTO"/>
			<gene:campoScheda campo="DESCR"/>
			<gene:campoScheda campo="ERRMSG"/>
			<gene:campoScheda campo="COD_PROFILO"/>
			<gene:campoScheda campo="USRSYS.SYSUTE" entita="USRSYS" where="W_LOGEVENTI.SYSCON=USRSYS.SYSCON"/>
			<gene:campoScheda campo="IPEVENTO"/>
		</gene:formScheda>
	</gene:redefineInsert>
	
	<c:set var="idEvento" value="${datiRiga.W_LOGEVENTI_IDEVENTO}"/>
	<c:set var="descrizione" value="Accesso al dettaglio tracciatura evento"/>
	<c:if test="${param.log eq 'true'}">
		<c:set var="criterioModificato" value='${gene:callFunction4("it.eldasoft.gene.tags.functions.TracciaLogEventiFunction", pageContext, "LOGEVENTI", idEvento, descrizione)}' />
	</c:if>
	
</gene:template>