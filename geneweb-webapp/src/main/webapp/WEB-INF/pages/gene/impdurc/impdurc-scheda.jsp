<%
/*
 * Created on: 22-mag-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Scheda Durc on Line dell'impresa */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="wsUrlRichiestaDurc" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "richiesta.durc.ws.client.url")}'/>
<c:set var="wsUrlVerificaRegolarita" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "verifica.regolarita.ws.client.url")}'/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<c:choose>
	<c:when test="${!empty wsUrlRichiestaDurc && !empty wsUrlVerificaRegolarita}">
		<c:set var="integrazioneWsDurc" value='1'/>
	</c:when>
	<c:otherwise>
		<c:set var="integrazioneWsDurc" value='0'/>
	</c:otherwise>
</c:choose>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="ImpdurcScheda" >
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetTitleFunction",pageContext,"IMPDURC")}' />
	<gene:redefineInsert name="noteAvvisi" />
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="IMPDURC" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreIMPDURC">

			<c:set var="codiceImpresaPadre" value='${fn:substringAfter(keyParent, ":")}' />

			<gene:gruppoCampi >
				<gene:campoScheda campo="ID" visibile="false" />
				<gene:campoScheda campo="CODIMP" visibile="false" value="${codiceImpresaPadre}" />
				<gene:campoScheda campo="NPROTO" modificabile="${integrazioneWsDurc eq '0'}"/>
				<gene:campoScheda campo="DATRIC" modificabile="${integrazioneWsDurc eq '0'}"/>
				<gene:campoScheda campo="DATSCA" modificabile="${integrazioneWsDurc eq '0'}"/>
				<gene:campoScheda campo="NOTDUR" />
				<gene:campoScheda campo="TIPO_RICHIESTA" modificabile="false" visibile="${integrazioneWsDurc eq '1'}" />
				<gene:campoScheda campo="ID_RICHIESTA" modificabile="false" visibile="false" />
				<gene:campoScheda campo="ESITO_VERIFICA_REGOLARITA" modificabile="false" visibile="${integrazioneWsDurc eq '1' && datiRiga.IMPDURC_TIPO_RICHIESTA == 1}" />
				<gene:campoScheda campo="ESITO_RICHIESTA_DURC" modificabile="false" visibile="${integrazioneWsDurc eq '1' && datiRiga.IMPDURC_TIPO_RICHIESTA == 2}" />
				<gene:campoScheda campo="DATESITO" modificabile="false" visibile="${integrazioneWsDurc eq '1' && datiRiga.IMPDURC_TIPO_RICHIESTA == 2}" />
				<gene:campoScheda campo="ANNULLATO" modificabile="false" visibile="${integrazioneWsDurc eq '1' && datiRiga.IMPDURC_TIPO_RICHIESTA == 2}" />
				<gene:campoScheda campo="DATANNULLAMENTO" modificabile="false" visibile="${integrazioneWsDurc eq '1' && datiRiga.IMPDURC_TIPO_RICHIESTA == 2}" />
				<gene:campoScheda campo="TIPESI" modificabile="${integrazioneWsDurc eq '0'}" />
				<gene:campoScheda campo="NOTESITO" modificabile="false" visibile="${integrazioneWsDurc eq '1'}" />
			</gene:gruppoCampi>

			<c:if test='${!fn:contains(listaOpzioniUtenteAbilitate, "ou243#")}'>
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
			</c:if>

		</gene:formScheda>
		<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou243#")}'>
			<gene:redefineInsert name="schedaModifica"/>
			<gene:redefineInsert name="schedaNuovo"/>
		</c:if>
	</gene:redefineInsert>
</gene:template>

