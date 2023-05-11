<%
/*
 * Created on: 13-07-2017
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
		Popup per annulare il calcolo dei punteggi tecnici o economici
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.calcoloEseguito and requestScope.calcoloEseguito eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:choose>
	<c:when test='${!empty param.tipo}'>
		<c:set var="tipo" value="${param.tipo}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="${tipo}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test="${tipo eq '2' }">
		<c:set var="msgTitolo" value ="Attiva modello" />
	</c:when>
	<c:when test="${tipo eq '3' }">
		<c:set var="msgTitolo" value ="Disattiva modello" />
	</c:when>
	<c:when test="${tipo eq '4' }">
		<c:set var="msgTitolo" value ="Archivia modello" />
	</c:when>
</c:choose>


<gene:setString name="titoloMaschera" value='${msgTitolo }' />


<c:set var="modo" value="MODIFICA" scope="request" />
	
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="QFORMLIB" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAnnullaCalcoloPunteggi">
	
		<gene:campoScheda>
			<td>
			<br>
		<c:choose>
			<c:when test="${esistonoDitteConPunteggio eq 'no'}">
				Nessun calcolo dei punteggi dei criteri di valutazione della busta ${msgTitolo} da annullare.<br>
			</c:when>
			<c:otherwise>
				Confermi l'operazione?<br>
			</c:otherwise>
		</c:choose>
			<br>
			</td>
		</gene:campoScheda>
		<gene:campoScheda campo="ID" visibile="false"/>
		<gene:campoScheda campo="STATO" visibile="false"/>
		<input type="hidden" name="tipo" value="${tipo}">
		
	</gene:formScheda>
		<c:if test="${esistonoDitteConPunteggio eq 'no' || requestScope.calcoloEseguito eq '2'}">
			<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</gene:redefineInsert>
		</c:if>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="geneweb/qformlib/popupImpostaStato.jsp";
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