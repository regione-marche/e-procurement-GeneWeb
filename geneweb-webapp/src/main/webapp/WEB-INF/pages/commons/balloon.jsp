<%
/*
 * Created on 24-set-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // AREA SOPRA IL TITOLO E CONTENENTE LO STORICO NAVIGAZIONE ED ALCUNE INFO/FUNZIONI UTENTE
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.BALLOONHELPPAGINA")}'>

<c:choose>
	<c:when test="${modo eq 'VISUALIZZA' }">
		<c:set var="modoVisNota" value="1"/>
	</c:when>
	<c:when test="${modo eq 'NUOVO' }">
		<c:set var="modoVisNota" value="2"/>
	</c:when>
	<c:when test="${modo eq 'MODIFICA' }">
		<c:set var="modoVisNota" value="3"/>
	</c:when>
</c:choose>

<c:choose>
	<c:when test="${empty modoVisNota}">
		<c:choose>
			<c:when test="${updateLista eq 0 }">
				<c:set var="modoVisNota" value="1"/>
			</c:when>
			<c:when test="${updateLista eq 1 }">
				<c:set var="modoVisNota" value="3"/>
			</c:when>
		</c:choose>
	
	</c:when>
</c:choose>

<c:if test="${empty  modoVisNota}">
	<c:set var="modoVisNota" value="1"/>
</c:if>

<c:set var="info" value="${gene:getInfoPagina(pageContext,modoVisNota)}" />

<c:if test="${fn:length(info) > 0}">
					<div id="balloon" class="expand" title="clicca sul riquadro per espandere la nota">
						${info}
					</div>
<script type="text/javascript">
$(document).ready(function(){
	$("#balloon").on("click",function() {
	if ($(this).attr('class') == 'expand') {
		$(this).attr('class', 'collapse');
		$(this).attr('title', 'clicca sul riquadro per comprimere la nota');
	} else {
		$(this).attr('class', 'expand');
		$(this).attr('title', 'clicca sul riquadro per espandere la nota');
	}
	});
});
</script>
</c:if>
</c:if>