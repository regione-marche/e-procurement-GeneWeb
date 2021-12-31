<%--
/*
 * Created on: 24-mag-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
Cronoprogramma di uno scadenzario di un'entita'

--%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="G_SCADENZ-cronoprogramma">

	<c:if test="${fn:contains(header['user-agent'], 'MSIE')}">
		<gene:redefineInsert name="doctype">
<!DOCTYPE html>
		</gene:redefineInsert>
	</c:if>

	<gene:redefineInsert name="addHistory">
		<gene:historyAdd titolo='Cronoprogramma' id='CRON-SCADENZ' replaceParam="" />
	</gene:redefineInsert>
	
	<gene:redefineInsert name="documentiAzioni"></gene:redefineInsert>

	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.fn.gantt.min.js"></script>
		<link rel="STYLESHEET" type="text/css" href="${pageContext.request.contextPath}/css/jquery/fn.gantt/style.css">
		<style type="text/css" media="screen">
			div.cronoprogramma {width: 1024px; margin-top:10px; margin-bottom:10px;}
		</style>
		<style type="text/css" media="print">
			div.cronoprogramma { margin-top:10px; margin-bottom:10px;}
			.testata, .menuprincipale, .menuazioni, .over-hidden, #footer, .navigate { display: none }
		</style>
	</gene:redefineInsert>

	<gene:setString name="titoloMaschera" value="Cronoprogramma" />

	<gene:redefineInsert name="corpo">
	<table class="dettaglio-notab">
		<tr>
			<td>
				<div>
					<input type="radio" name="filtra" value="all" checked="checked">Previsione e consuntivo</input>&nbsp;&nbsp;&nbsp;
					<input type="radio" name="filtra" value="previsione">Solo previsione</input>&nbsp;&nbsp;&nbsp;
					<input type="radio" name="filtra" value="consuntivo">Solo consuntivo</input>
				</div>
				<div id='cronoprogramma' class="cronoprogramma"></div>
			</td>
		</tr>
	</table>
	
	<gene:javaScript>
	$(document).ready(function() {
		if ($.browser.msie  && parseInt($.browser.version, 10) <= 7) {
			$("#cronoprogramma").append('<span class="info-wizard">Funzione non disponibile con Internet Explorer 7 o precedenti. Aggiornare il browser.</span>')
		} else {
			//$("#cronoprogramma").addClass("gantt");
			// si apre il cronoprogramma con tutto
			refreshGantt("all");
			
			$('input[name=filtra]').on( "click",function( event ) {
				// quando si seleziona un radio si scatena l'aggiornamento del gantt filtrando le occorrenze
				refreshGantt($(this).val());
			});
		}	
		
		$(".contenitore-dettaglio").width($("#cronoprogramma").width()); 
		
	});
	
	function refreshGantt(filtro) {
		"use strict";

		$("#cronoprogramma").gantt({
			months:  ["Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"]  ,
			dow:  ["Do", "Lu", "Ma", "Me", "Gi", "Ve", "Sa"],
			scale: "days",
			maxScale: "months",
			minScale: "days",
			itemsPerPage: 20,
			navigate: "scroll",
			
			source: '${pageContext.request.contextPath}/GetAttivitaCronoprogramma.do?entita=${param.entita}&chiave=${param.chiave}&metodo='+filtro
		});
	}
	</gene:javaScript>
	</gene:redefineInsert>
</gene:template>
