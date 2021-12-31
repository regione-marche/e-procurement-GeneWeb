<%/*
       * Created on 09-mar-2016
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W_QUARTZ-scheda" schema="GENEWEB">

<gene:redefineInsert name="head" >
	
		<style type="text/css">
		  	
		 	TABLE.quartz {
				margin: 0;
				margin-top: 5;
				margin-bottom: 5;
				padding: 0px;
				width: 100%;
				font-size: 11px;
				border-collapse: collapse;
				border-left: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
			}
		
			TABLE.quartz TR.intestazione {
				background-color: #EFEFEF;
			}
			
			TABLE.quartz TR.intestazione TD {
				text-align: center;
				font-weight: bold;
				height: 35px;
				border: #A0AABA 1px solid;
			}
			
			TABLE.quartz TR TD {
				height: 25px;
				padding: 2 5 2 5;
				text-align: left;
				border: #A0AABA 1px solid;
			}
		
		</style>
	</gene:redefineInsert>

	<c:set var="entita" value="W_QUARTZ"/>
	<gene:setString name="titoloMaschera" value='Pianificazione'/>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">
		<gene:formPagine gestisciProtezioni="true">
			<gene:pagina title="Configurazione" idProtezioni="W_QUARTZ">
				<gene:formScheda entita="W_QUARTZ" gestisciProtezioni="true" gestore="it.eldasoft.gene.tags.gestori.submit.GestoreW_QUARTZ" >
					
					<gene:campoScheda campo="CODAPP" visibile="false" />
					<gene:campoScheda campo="BEAN_ID" modificabile="false"/>
					<gene:campoScheda campo="DESCRIZIONE" modificabile="false"/>
					<gene:campoScheda campo="CRON_EXPRESSION" modificabile="false"/>
					<gene:campoScheda campo="CRON_SECONDS" obbligatorio="true"/>
					<gene:campoScheda campo="CRON_MINUTES" obbligatorio="true"/>
					<gene:campoScheda campo="CRON_HOURS" obbligatorio="true"/>
					<gene:campoScheda campo="CRON_DAY_OF_MONTH" obbligatorio="true"/>
					<gene:campoScheda campo="CRON_MONTH" obbligatorio="true"/>
					<gene:campoScheda title="Giorni della settimana" campo="CRON_DAY_OF_WEEK" obbligatorio="true"/>
					<gene:campoScheda campo="CRON_YEAR" />
					
					<gene:campoScheda>
						<td class="etichetta-dato">
							Formato, caratteri speciali ed esempi
						</td>
						<td class="valore-dato">
							<table class="quartz">
								<tr class="intestazione">
									<td colspan="5">Formato della pianificazione</td>
								</tr>
								<tr class="intestazione">
									<td>Posizione</td>
									<td>Nome del campo</td>
									<td>Obbligatorio&nbsp;?</td>
									<td>Valori possibili</td>
									<td>Caratteri speciali disponibili</td>
								</tr>
								<tr>
									<td>1</td>
									<td>Secondi</td>
									<td>Si</td>
									<td>0-59</td>
									<td>, - * /</td>
								</tr>
								<tr>
									<td>2</td>
									<td>Minuti</td>
									<td>Si</td>
									<td>0-59</td>
									<td>, - * /</td>
								</tr>								
								<tr>
									<td>3</td>
									<td>Ore</td>
									<td>Si</td>
									<td>0-23</td>
									<td>, - * /</td>
								</tr>
								<tr>
									<td>4</td>
									<td>Giorni del mese</td>
									<td>Si</td>
									<td>1-31</td>
									<td>, - * ? / L W</td>
								</tr>
								<tr>
									<td>5</td>
									<td>Mesi</td>
									<td>Si</td>
									<td>1-12 oppure JAN-DEC</td>
									<td>, - * /</td>
								</tr>
								<tr>
									<td>6</td>
									<td>Giorni della settimana</td>
									<td>Si</td>
									<td>1-7 oppure SUN-SAT</td>
									<td>, - * ? / L #</td>
								</tr>
								<tr>
									<td>7</td>
									<td>Anni</td>
									<td>No</td>
									<td>vuoto, 1970-2099</td>
									<td>, - * /</td>
								</tr>									
							</table>
							<br>
							<table class="quartz">
								<tr class="intestazione">
									<td colspan="3">Caratteri speciali</td>
								</tr>							
								<tr class="intestazione">
									<td>Carattere</td>
									<td>Descrizione</td>
									<td>Esempio</td>
								</tr>
								<tr>
									<td>*</td>
									<td>Tutti i valori, utilizzato per selezionare tutti i valori di un campo</td>
									<td>Nel campo dei minuti "*" significa "ogni minuto"</td>
								</tr>
								<tr>
									<td>?</td>
									<td>Nessun valore specifico</td>
									<td>Se indicato nel campo dei giorni del mese permette di eseguire la pianificazione ignorando il giorno del mese</td>
								</tr>
								<tr>
									<td>-</td>
									<td>Intervallo, permette di indicare un intervallo di valori</td>
									<td>Nel campo delle ore "10-12" significa "ore 10, 11 e 12"</td>
								</tr>
								<tr>
									<td>,</td>
									<td>Lista di valori, permette di specificare una lista di valori distinti </td>
									<td>Nel campo dei giorni del mese "5,15" permette l'esecuzione solo nei giorni 5 e 15.</td>
								</tr>
								<tr>
									<td>/</td>
									<td>Incrementi, permette di definire un incremento da un valore fisso</td>
									<td>Nel campo dei secondi "0/15" significa "secondi 0, 15, 30 e 45"</td>
								</tr>
							</table>
							<br>
							Per maggiori informazioni consultare la guida in linea 
							<br>
							<a class="link-generico" target="_blank" href="http://www.quartz-scheduler.org/documentation/quartz-2.2.2/tutorials/tutorial-lesson-06.html">
								http://www.quartz-scheduler.org/documentation/quartz-2.2.2/tutorials/tutorial-lesson-06.html
							</a>
							<br>
							<br>
							<table class="quartz">
								<tr class="intestazione">
									<td colspan="2">Alcuni esempi di pianificazione</td>
								</tr>
								<tr class="intestazione">
									<td>Pianificazione</td>
									<td>Significato</td>
								</tr>
								<tr>
									<td>0/30 * * ? * *</td>
									<td>Esecuzione ogni trenta secondi di ogni minuto di ogni ora di ogni giorno</td>
								<tr>
								<tr>
									<td>0 0 0 1 * ?</td>
									<td>Esecuzione alla mezzanotte del primo giorno del mese</td>
								<tr>
								<tr>
									<td>0 0 * ? * *</td>
									<td>Esecuzione ad ogni ora</td>
								<tr>
								<tr>
									<td>0 0/5 * * * ? </td>
									<td>Esecuzione ogni 5 minuti di ogni giorno (ore 0.00, 0.05, 0.10, ...) </td>
								<tr>
								<tr>
									<td>0 59 7-20/1 * * ?</td>
									<td>Esecuzione ogni giorno, ogni ora dalle 7:59 alle 20:59</td>
								<tr>																
								<tr>
									<td>0 0 0 1 1 ? 2099</td>
									<td>Esecuzione nel 2099, in pratica la pianificazione non &egrave; abilitata</td>
								<tr>	
							</table>
							<br>
						</td>
					</gene:campoScheda>
					
					<gene:campoScheda>
						<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
					</gene:campoScheda>
				</gene:formScheda>
			</gene:pagina>
		</gene:formPagine>
	</gene:redefineInsert>
	
	<gene:javaScript>
		$("#jsPopUpW_QUARTZ_CODAPP").hide();
		$("#jsPopUpW_QUARTZ_BEAN_ID").hide();
		$("#jsPopUpW_QUARTZ_DESCRIZIONE").hide();
		$("#jsPopUpW_QUARTZ_CRON_EXPRESSION").hide();
		$("#jsPopUpW_QUARTZ_CRON_SECONDS").hide();
		$("#jsPopUpW_QUARTZ_CRON_MINUTES").hide();
		$("#jsPopUpW_QUARTZ_CRON_HOURS").hide();
		$("#jsPopUpW_QUARTZ_CRON_DAY_OF_MONTH").hide();
		$("#jsPopUpW_QUARTZ_CRON_MONTH").hide();
		$("#jsPopUpW_QUARTZ_CRON_DAY_OF_WEEK").hide();
		$("#jsPopUpW_QUARTZ_CRON_YEAR").hide();
		
		function setCronExpression() {
			var seconds = $("#W_QUARTZ_CRON_SECONDS").val();
			var minutes = $("#W_QUARTZ_CRON_MINUTES").val();
			var hours = $("#W_QUARTZ_CRON_HOURS").val();
			var dayOfMonth = $("#W_QUARTZ_CRON_DAY_OF_MONTH").val();
			var month = $("#W_QUARTZ_CRON_MONTH").val();
			var dayOfWeek = $("#W_QUARTZ_CRON_DAY_OF_WEEK").val();
			var year = $("#W_QUARTZ_CRON_YEAR").val();
			
			var cronExpression = "";
			cronExpression += $.trim(seconds);
			cronExpression += " " + $.trim(minutes);
			cronExpression += " " + $.trim(hours);
			cronExpression += " " + $.trim(dayOfMonth);
			cronExpression += " " + $.trim(month);
			cronExpression += " " + $.trim(dayOfWeek);
			cronExpression += " " + $.trim(year);
			
			$("#W_QUARTZ_CRON_EXPRESSION").val(cronExpression);
			$("#W_QUARTZ_CRON_EXPRESSIONview").text(cronExpression);
		}
		
		$("#W_QUARTZ_CRON_SECONDS").on("change",function() {
		  	setCronExpression();
		});
		
		$("#W_QUARTZ_CRON_MINUTES").on("change",function() {
		  	setCronExpression();
		});
		
		$("#W_QUARTZ_CRON_HOURS").on("change",function() {
		  	setCronExpression();
		});
		
		$("#W_QUARTZ_CRON_DAY_OF_MONTH").on("change",function() {
		  	setCronExpression();
		});
		
		$("#W_QUARTZ_CRON_MONTH").on("change",function() {
		  	setCronExpression();
		});
		
		$("#W_QUARTZ_CRON_DAY_OF_WEEK").on("change",function() {
		  	setCronExpression();
		});
		
		$("#W_QUARTZ_CRON_YEAR").on("change",function() {
		  	setCronExpression();
		});				
				
		
	</gene:javaScript>
	
</gene:template>




