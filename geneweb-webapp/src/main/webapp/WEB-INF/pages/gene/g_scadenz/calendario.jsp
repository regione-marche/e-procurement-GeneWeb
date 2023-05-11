<%--
/*
 * Created on: 09-mag-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
Calendario di un'entita'

--%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="G_SCADENZ-calendario">

	<c:if test="${fn:contains(header['user-agent'], 'MSIE')}">
		<gene:redefineInsert name="doctype">
<!DOCTYPE html>
		</gene:redefineInsert>
	</c:if>
	
	<gene:redefineInsert name="addHistory">
			<gene:historyAdd titolo='Calendario attivit&agrave;' id='CAL-SCADENZ' replaceParam="" />
	</gene:redefineInsert>
	
	<gene:redefineInsert name="documentiAzioni"></gene:redefineInsert>

	<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.fullcalendar.min.js"></script>
	<link rel="STYLESHEET" type="text/css" href="${pageContext.request.contextPath}/css/jquery/fullcalendar/${applicationScope.pathCss}fullcalendar.css">
	<style type="text/css">
		.running {background-color: yellow; color: black; font-size:1em;}
		.expired {background-color: red; font-size:1em;}
		.completed {background-color: green; font-size:1em;} 
		.tooltip {font-size:1em; max-width:300px; padding:2px;}
		 
	</style>
	</gene:redefineInsert>

	<gene:setString name="titoloMaschera" value="Calendario attivit&agrave;" />

	<gene:redefineInsert name="corpo">
	<table class="dettaglio-notab">
		<tr>
			<td><div id='calendario'></div></td>
		</tr>
	</table>
	
	<gene:javaScript>
	$(document).ready(function() {
		$('#calendario').fullCalendar({
			theme : true,
			firstDay: 1,
			monthNames : ['Gennaio', 'Febbraio', 'Marzo', 'Aprile', 'Maggio', 'Giugno', 'Luglio', 'Agosto', 'Settembre', 'Ottobre', 'Novembre', 'Dicembre'],
			dayNamesShort : ['Dom', 'Lun', 'Mar', 'Mer', 'Gio', 'Ven', 'Sab'],
			buttonText: {
				today: 'oggi'
			},
			events: function(start, end, callback) {
				$.ajax({
					url: '${pageContext.request.contextPath}/GetAttivitaCalendario.do',
					type: 'POST',
					dataType: 'json',
					data: {
						start: Math.round(start.getTime() / 1000),
						end: Math.round(end.getTime() / 1000),
						entita: '${param.entita}',
						<c:if test="${! empty param.chiave}">chiave: '${param.chiave}',</c:if>
						metodo: <c:if test="${! empty param.chiave}">'singolo'</c:if><c:if test="${empty param.chiave}">'globale'</c:if>
					},
					success: function(msg) {
						var events = [];
						var classe = '';
						$(msg).each(function() {
							var stato = $(this).attr('stato');
							if (stato == 1) classe = 'running';
							else if (stato == 2) classe = 'completed';
							else if (stato == 3) classe = 'expired';
                    		events.push({
								title: $(this).attr('titolo'),
								start: $(this).attr('scadenza'),
								className: classe,
								// altri attributi ad uso del tooltip
								descrizione: $(this).attr('descrizione'),
								inizio: $(this).attr('inizio'),
								durata: $(this).attr('durata'),
								fine: $(this).attr('fine'),
								consuntivo: $(this).attr('consuntivo'),
								scadenza: $(this).attr('scadenza'),
								stato: (stato == 1 ? 'Da svolgere' : (stato == 2 ? 'Completata' : 'Da svolgere ma scaduto il termine'))
		                    });
        		        });
						callback(events);
					},
					error: function() {
						alert('Errori durante la lettura delle attività in scadenza');
					}
				});
			},
			eventMouseover: function(calEvent, jsEvent) {
				var tooltip = '<div id="tooltipevent" class="fc-event tooltip" style="position:absolute;z-index:10001;"><b>' 
					+  calEvent.descrizione + '</b></br>'
					+  (calEvent.durata > 0 ? ('Data inizio: ' + formatData(calEvent.inizio) + '</br>Durata in giorni: ' + calEvent.durata+ '</br>') : '')
					+ 'Data scadenza prevista: ' + formatData(calEvent.fine) + '</br>'
					+ 'Data scadenza aggiornata: ' + formatData(calEvent.scadenza) + '</br>'
					+ 'Data scadenza effettiva: ' + (calEvent.consuntivo != null ? formatData(calEvent.consuntivo) : 'non valorizzata') + '</br>'
					+ 'Stato: ' + calEvent.stato + '</br>'
					+ '</div>';
				$("body").append(tooltip);
				$(this).mouseover(function(e) {
					$(this).css('z-index', 10000);
					$('.tooltipevent').fadeIn('500');
					$('.tooltipevent').fadeTo('10', 1.9);
					}).mousemove(function(e) {
						$('#tooltipevent').css('top', e.pageY + 10);
						$('#tooltipevent').css('left', e.pageX + 20);
				});
			},
			eventMouseout: function(calEvent, jsEvent) {
				$('#tooltipevent').remove();
			}
		})
	});
	
	function formatData(data) {
		return data.substring(8,10)+'/'+data.substring(5,7)+'/'+data.substring(0,4);
	}
	</gene:javaScript>
	</gene:redefineInsert>
</gene:template>
