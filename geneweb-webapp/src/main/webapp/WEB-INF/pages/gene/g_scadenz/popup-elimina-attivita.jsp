<%
/*
 * Created on: 06-04-2013
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
		Finestra per l'eliminazione di una attività
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.eliminazioneEseguita and requestScope.eliminazioneEseguita eq "1"}' >
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
<gene:setString name="titoloMaschera" value='Eliminazione attività' />

<c:choose>
	<c:when test='${not empty param.id}'>
		<c:set var="id" value="${param.id}" />
	</c:when>
	<c:otherwise>
		<c:set var="id" value="${id}" />
	</c:otherwise>
</c:choose>

<c:set var="bloccoEliminazione" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.ControlliCancellazioneAttivitaFunction", pageContext,id)}' />

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="G_SCADENZ" gestisciProtezioni="false" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreCancellazioneAttivita">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${bloccoEliminazione eq "1"}'>
					L'attività non è eliminabile in quanto esistono attività dipendenti da essa
				</c:when>
				<c:otherwise>
					Procedere con l'eliminazione?
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
		
		<input type="hidden" name="id" id="id" value="${id}" />
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${bloccoEliminazione eq "1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:window.close();">&nbsp;
	</gene:redefineInsert>
</c:if>
	
	<gene:javaScript>
				
		function conferma() {
			document.forms[0].jspPathTo.value="gene/g_scadenz/popup-elimina-attivita.jsp";
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