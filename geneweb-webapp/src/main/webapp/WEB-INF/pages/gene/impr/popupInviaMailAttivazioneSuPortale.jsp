<%
/*
 * Created on: 15-07-2011
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
		Finestra per l'attivazione della funzione 'Invia mail attivazione utenza'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.invioMailAttivazioneEseguito and requestScope.invioMailAttivazioneEseguito eq "1"}' >
		<script type="text/javascript">
			window.opener.bloccaRichiesteServer();
			window.opener.selezionaPagina(0);
			window.close();
		</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Invio mail di attivazione utenza su portale Appalti' />

<c:choose>
	<c:when test='${not empty param.codice}'>
		<c:set var="codice" value="${param.codice}" />
	</c:when>
	<c:otherwise>
		<c:set var="codice" value="${codice}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.email}'>
		<c:set var="email" value="${param.email}" />
	</c:when>
	<c:otherwise>
		<c:set var="email" value="${email}" />
	</c:otherwise>
</c:choose>

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="IMPR" gestisciProtezioni="false" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestorePopupInviaMailAttivazioneSuPortale">
	
		<gene:campoScheda>
			<td colSpan="2">
				<br>
				<c:set var="isVisibile" value="true"/>
				<c:choose>
					<c:when test='${requestScope.mailNonValida eq "1" }'>
						Non è possibile procedere con l'invio della mail di attivazione dell'utenza sul portale Appalti.<br>
						<b>L'indirizzo mail specificato non è un indirizzo valido.</b>
						<br>Indicare un indirizzo valido.
					</c:when>
					<c:when test='${requestScope.utenteAttivo eq "1" }'>
						Non è possibile procedere con l'invio della mail di attivazione dell'utenza sul portale Appalti.<br>
						<b>L'utente '${user}' risulta già attivo.</b>
						<c:set var="isVisibile" value="false"/>
					</c:when>
					<c:when test='${requestScope.erroreServizioPortale eq "1"}'>
						Non è possibile procedere con l'invio della mail di attivazione dell'utenza sul portale Appalti.<br>
						<b>Si è verificato un errore.</b>
						<c:set var="isVisibile" value="false"/>
					</c:when>
					<c:when test='${requestScope.utenteNonDefinito eq "1"}'>
						Non è possibile procedere con l'invio della mail di attivazione dell'utenza sul portale Appalti.<br>
						<b>Utenza non più disponibile sul portale perchè non attivata nel tempo utile.
						<br>E' necessario effettuare nuovamente la registrazione sul portale.</b>
						<c:set var="isVisibile" value="false"/>
						<script type="text/javascript">
							window.onload = function () { 
								window.opener.bloccaRichiesteServer();
								window.opener.location = "${pageContext.request.contextPath}/History.do?metodo=reload";
							}
						</script>
					</c:when>
					<c:otherwise>
						Confermi l'invio della mail per l'attivazione dell'utenza sul portale Appalti?
					</c:otherwise>
				</c:choose>
				<br>&nbsp;
				<br>&nbsp;
			</td>
		</gene:campoScheda>

		<gene:campoScheda campo="CODIMP" visibile="false" defaultValue="${codice}"/>
		<gene:campoScheda campo="EMAIL" campoFittizio="true" title="Indirizzo a cui viene inviata la mail" defaultValue="${email}" obbligatorio="true" visibile="${isVisibile}" definizione="T100;0;;;"/>

	</gene:formScheda>
	
  </gene:redefineInsert>

<c:if test='${requestScope.erroreServizioPortale eq "1" or requestScope.utenteAttivo eq "1" or requestScope.utenteNonDefinito eq "1"}' >
	<gene:redefineInsert name="buttons">
		
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			
	</gene:redefineInsert>
</c:if>

	
	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gene/impr/popupInviaMailAttivazioneSuPortale.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
	

	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>