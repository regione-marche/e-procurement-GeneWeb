
<%/*
   * Created on 28-set-2012
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

 // CONTIENE LA PAGINA CON LA FORM PER L'ESITO DELLA RICHIESTA DI ASSISTENZA
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="generic-template.jsp" gestisciProtezioni="false" schema="GENE">
	<gene:redefineInsert name="preTitolo">
	  <jsp:include page="/WEB-INF/pages/commons/areaPreTitolo.jsp">
		<jsp:param name="hideHistory" value="1" />
	  </jsp:include>
	</gene:redefineInsert>
	<gene:redefineInsert name="corpo">
					<div class="contenitore-arealavoro">

						<div class="titolomaschera">Richiesta di assistenza</div>
						
						<div class="margin10px">

						<c:if test="${empty requestScope.ticketId}">
							<p>Gentile utente,</p>
							<p>La informiamo che la Sua richiesta &egrave; stata inviata con successo.</p>
							<p>La Sua segnalazione verr&agrave; presa in carico nel pi&ugrave; breve tempo possibile.</p>
						</c:if>
						<c:if test="${! empty requestScope.ticketId}">
							<p>Gentile utente, </p>
							<p>La informiamo che la Sua richiesta &egrave; stata inviata con successo ed &egrave; stato attribuito il numero di ticket ${requestScope.ticketId}.</p> 
							<p>La Sua segnalazione verr&agrave; presa in carico nel pi&ugrave; breve tempo possibile.</p>
							<p>Si ricorda, nell'eventualit&agrave; di dover effettuare nuove segnalazioni collegate alla presente, di riportare nella descrizione 
							   della nuova segnalazione il numero di ticket attribuito alla presente richiesta.</p>
						</c:if>
						<br/>
						<br/>
						<p>Cordiali saluti,<br/>
						Lo Staff Tecnico</p>
						<br/>
						<br/>						
						<c:if test="${empty sessionScope.profiloUtente}">
						Torna alla pagina di <a class="link-generico" href="${pageContext.request.contextPath}/">login</a>.
						</c:if>
						
						</div>
					</div>

	</gene:redefineInsert>
</gene:template>
