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

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="ImpdurcScheda" >
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetTitleFunction",pageContext,"IMPDURC")}' />
	<gene:redefineInsert name="noteAvvisi" />
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="IMPDURC" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreIMPDURC">

			<c:set var="codiceImpresaPadre" value='${fn:substringAfter(keyParent, ":")}' />

			<gene:gruppoCampi >
				<gene:campoScheda campo="ID" visibile="false" />
				<gene:campoScheda campo="CODIMP" visibile="false" value="${codiceImpresaPadre}" />
				<gene:campoScheda campo="NPROTO" />
				<gene:campoScheda campo="DATRIC" />
				<gene:campoScheda campo="DATSCA" />
				<gene:campoScheda campo="TIPESI" />
				<gene:campoScheda campo="NOTDUR"/>				
			</gene:gruppoCampi>

			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>

		</gene:formScheda>
	</gene:redefineInsert>
</gene:template>

