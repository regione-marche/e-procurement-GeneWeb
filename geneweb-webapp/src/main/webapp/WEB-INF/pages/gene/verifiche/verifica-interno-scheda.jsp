<%
			/*
       * Created on: 11.58 01/06/2007
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
					Interno delle scheda di astra
				Creato da:
					Marco Franceschin
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:choose>
	<c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou228#")}'>
		<c:set var="opzVerifiche" value="ou228" />
	</c:when>
	<c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou227#")}'>
		<c:set var="opzVerifiche" value="ou227" />
	</c:when>
	<c:otherwise>
		<c:set var="opzVerifiche" value="" />
	</c:otherwise>
</c:choose>

<gene:formScheda entita="VERIFICHE" gestisciProtezioni="true" >

	<c:choose>
		<c:when test='${"ou228" eq opzVerifiche}'>
		</c:when>
		<c:otherwise>
			<gene:redefineInsert name="schedaModifica" />
			<gene:redefineInsert name="pulsanteModifica" />
		</c:otherwise>
	</c:choose>
	<gene:redefineInsert name="schedaNuovo" />
	<gene:redefineInsert name="pulsanteNuovo" />

	<gene:campoScheda campo="ID" visibile="false" modificabile="false" />
	<gene:campoScheda campo="DESCR_VERIFICA" title="Descrizione verifica" />
	<gene:campoScheda campo="MODO_GESTIONE_VERIFICA" />
	<gene:campoScheda campo="ENTE_COMPETENZA" />
	<c:if test='${"ou228" eq opzVerifiche}'>
		<gene:campoScheda>
			<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
		</gene:campoScheda>
	</c:if>
</gene:formScheda>