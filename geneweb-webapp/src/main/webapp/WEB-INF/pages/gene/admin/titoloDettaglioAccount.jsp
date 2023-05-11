<%
/*
 * Created on 02-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DEL TITOLO DELLE PAGINE DI DETTAGLIO DI GRUPPO
 // AD ECCEZIONE DELLA PAGINA DI CREAZIONE DI UN NUOVO GRUPPO
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

Dettaglio utente <c:if test='${fn:length(nomeOggetto) gt 0}' > - "${nomeOggetto}" </c:if>

<c:if test="${!empty ListaUtentiUgualeCodfisc }">
	</div><!-- Chiusura del div relativo al titolo che imposta il testo in grassetto-->
	<div>
	<!-- Viene visualizzato il messaggio informativo sulla duplicazione del codice fiscale dell'utente-->
	<div class="contenitore-errori-arealavoro">
		<div class="errori-javascript-titolo">
			<div><img src="${contextPath}/img/jsMsgOn.gif" alt="Messaggi" />Messaggi</div>
			<div class="errori-javascript-dettaglio">
				<ul>
					<li class="errori-javascript-war">ATTENZIONE:<small>
						I seguenti utenti hanno il medesimo codice fiscale: <br>
						<ul>
							<c:forEach items="${ListaUtentiUgualeCodfisc}" var="utente" varStatus="status" >
								<li style="list-style-type: disc;margin-left: 30px;" >${utente.syscon} - ${utente.sysute}</li>
							</c:forEach>
						</ul>
					</small></li>
				</ul>
			</div>
		</div>
	</div>
</c:if>