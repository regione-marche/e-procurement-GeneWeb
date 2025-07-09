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
/* Tab raggrupamento della scheda dell'impresa */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="ImpcaseScheda" >
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetTitleFunction",pageContext,"IMPCASE")}' />
	<gene:redefineInsert name="noteAvvisi" />
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="IMPCASE" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreIMPCASE">

			<c:set var="codiceImpresaPadre" value='${fn:substringAfter(keyParent, ":")}' />

			<gene:gruppoCampi >
				<gene:campoScheda campo="CODIMP" visibile="false" value="${codiceImpresaPadre}" />
				<gene:campoScheda campo="NUMCOM" visibile="false" />
				<gene:campoScheda campo="TIPCOM" />
				<gene:campoScheda campo="DATRIC" />
				<gene:campoScheda campo="DATESI" />
				<gene:campoScheda campo="TIPESI" />
				<gene:campoScheda campo="NOTECO"/>
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

