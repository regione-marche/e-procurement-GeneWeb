
<%
	/*
	 * Created on 04-Nov-2008
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */

	// Scheda degli intestatari della concessione stradale
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W_APPLICATION_KEYS-Scheda" schema="GENEWEB">
	<c:set var="entita" value="W_APPLICATION_KEYS" />
	<gene:setString name="titoloMaschera" value='Credenziali servizi di pubblicazione'/>

	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="W_APPLICATION_KEYS" gestisciProtezioni="true" gestore="it.eldasoft.gene.tags.gestori.submit.GestoreW_APPLICATION_KEYS">
			<gene:campoScheda campo="CLIENTID" modificabile='${modoAperturaScheda eq "NUOVO"}' obbligatorio="true"/>
			<gene:campoScheda campo="CHIAVE" visibile='${modoAperturaScheda eq "VISUALIZZA"}'/>
			<gene:campoScheda title="Chiave per l'autenticazione ai servizi di pubblicazione" obbligatorio="true" campoFittizio="true" campo="KEY_DECRYPT" definizione="T40" visibile='${modoAperturaScheda ne "VISUALIZZA"}'/>
			<gene:campoScheda campo="NOTE"/>
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>

		</gene:formScheda>
	</gene:redefineInsert>

</gene:template>
