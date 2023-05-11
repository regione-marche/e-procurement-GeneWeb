<%
/*
 * Created on: 29-01-2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Popup Attivazione */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.operazioneEseguita and requestScope.operazioneEseguita eq "1"}' >
<script type="text/javascript">
	window.opener.document.forms[0].pgSort.value = "";
	window.opener.document.forms[0].pgLastSort.value = "";
	window.opener.document.forms[0].pgLastValori.value = "";
	window.opener.bloccaRichiesteServer();
	window.opener.listaVaiAPagina(0);
	window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:choose>
	<c:when test='${not empty param.codice}'>
		<c:set var="codice" value="${param.codice}" />
	</c:when>
	<c:otherwise>
		<c:set var="codice" value="${codice}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.operazione}'>
		<c:set var="operazione" value="${param.operazione}" />
	</c:when>
	<c:otherwise>
		<c:set var="operazione" value="${operazione}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${operazione eq "1"}'>
		<c:set var="messaggio" value="Attivazione dell'ufficio intestatario" />
	</c:when>
	<c:otherwise>
		<c:set var="messaggio" value="Disattivazione dell'ufficio intestatario" />
		<c:set var="numUtenti" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetNumUtemtiCollegatiUffintFunction", pageContext, codice)}'/>
	</c:otherwise>
</c:choose>

<gene:setString name="titoloMaschera" value="${messaggio }" />

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="UFFINT" gestisciProtezioni="false" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestorePopupAttivazioneUffint">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${requestScope.erroreOperazione eq "1"}'>
					Ci sono stati degli errori durante l'operazione di ${messaggio }.
				</c:when>
				<c:otherwise>
					<c:if test="${!empty numUtenti }">
						L'ufficio intestatario che si intende disattivare è collegato a ${numUtenti} utenti applicativo.<br>
					</c:if>
					Vuoi procedere con l'operazione di ${messaggio }?
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
		
		<input type="hidden" name="codice" id="codice" value="${codice}" />
		<input type="hidden" name="operazione" id="operazione" value="${operazione}" />
		
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${requestScope.errori eq "1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
	</gene:redefineInsert>
</c:if>
	
	<gene:javaScript>
		
		function conferma() {
			document.forms[0].jspPathTo.value="gene/uffint/popupAttivazione.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
		/*
		function chiudi(){
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();
		}
		*/
		
	

	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>