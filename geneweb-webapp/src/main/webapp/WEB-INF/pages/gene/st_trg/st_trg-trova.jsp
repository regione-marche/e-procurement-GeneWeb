<%
/*
 * Created on: 30/ott/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Form di ricerca delle modifiche alla base dati */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="TrovaST_TRG" >

	<gene:redefineInsert name="addHistory">
		<c:if test='${param.metodo ne "nuova"}' >
			<gene:historyAdd titolo='${gene:getString(pageContext,"titoloMaschera","Ricerca tracciatura modifiche su DB")}' id="ricerca" />
		</c:if>
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Ricerca tracciatura modifiche su DB" />

	<gene:redefineInsert name="corpo">
  	<gene:formTrova entita="ST_TRG" gestisciProtezioni="true" >
			<gene:gruppoCampi idProtezioni="Gen">
				<tr><td colspan="3"><b>Dati generali</b></td></tr>

				<gene:campoTrova campo="ST_DATE" />
				
				<gene:campoTrova campo="ST_OPERATION" title="Tipo di operazione" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoTipoOperazione" />

						<input type="hidden" name="Campo22_computed" value="false" />
						<input type="hidden" name="Campo22_conf" value="=" />
						<input type="hidden" name="defCampo22" value="" />
				
				
				<tr id="rowCampo23">
					<td class="etichetta-dato">Entità</td>
					<td class="operatore-trova">
						<input type="hidden" name="Campo23_computed" value="false" />
						<input type="hidden" name="Campo23_conf" value="=" />
						<input type="hidden" name="defCampo23" value="" />
						&nbsp;
					</td>
					<td class="valore-dato-trova">
						<input type="text" id="Campo25" name="Campo25" title="descrizione" spellcheck="false"></input>
						<input type="hidden" id="Campo24" name="Campo24" title="chiaveAmbiguaSelezionata" spellcheck="false"></input>
						<input type="hidden" id="Campo23" name="Campo23" title="Tabella" value="" onchange="javascript:setChiaviOnChange(this.value, true);"></input>
					</td>
				</tr>
			
				<gene:campoTrova campo="ST_TABLE" />
			</gene:gruppoCampi>
			
			<gene:gruppoCampi idProtezioni="SistemaOperativo">
				<tr><td colspan="3"><b>Dati sul sistema operativo</b></td></tr>
				<gene:campoTrova campo="ST_HOST"/>
				<gene:campoTrova campo="ST_OSUSER"/>
				<gene:campoTrova campo="ST_IP_ADDRESS"/>
			</gene:gruppoCampi>

			<gene:gruppoCampi idProtezioni="Connessioni" >
				<tr><td colspan="3"><b>Dati di connessione (utente applicativo e utente database)</b></td></tr>
				<gene:campoTrova campo="ST_SYSCON"/>
				<gene:campoTrova campo="ST_SYSUTE"/>
			</gene:gruppoCampi>

			<gene:gruppoCampi idProtezioni="CampiChiave">
				<tr id="CampiChiave"><td colspan="3"><b>Dati sui campi chiave</b></td></tr>
				<gene:campoTrova campo="ST_KEY1" defaultValue="${sessionScope.trovaST_TRG.Campo8}" />
				<gene:campoTrova campo="ST_VAL1" defaultValue="${sessionScope.trovaST_TRG.Campo9}" />
				<gene:campoTrova campo="ST_KEY2" defaultValue="${sessionScope.trovaST_TRG.Campo10}" />
				<gene:campoTrova campo="ST_VAL2" defaultValue="${sessionScope.trovaST_TRG.Campo11}" />
				<gene:campoTrova campo="ST_KEY3" defaultValue="${sessionScope.trovaST_TRG.Campo12}" />
				<gene:campoTrova campo="ST_VAL3" defaultValue="${sessionScope.trovaST_TRG.Campo13}" />
				<gene:campoTrova campo="ST_KEY4" defaultValue="${sessionScope.trovaST_TRG.Campo14}" />
				<gene:campoTrova campo="ST_VAL4" defaultValue="${sessionScope.trovaST_TRG.Campo15}" />
				<gene:campoTrova campo="ST_KEY5" defaultValue="${sessionScope.trovaST_TRG.Campo16}" />
				<gene:campoTrova campo="ST_VAL5" defaultValue="${sessionScope.trovaST_TRG.Campo17}" />
				<gene:campoTrova campo="ST_KEY6" defaultValue="${sessionScope.trovaST_TRG.Campo18}" />
				<gene:campoTrova campo="ST_VAL6" defaultValue="${sessionScope.trovaST_TRG.Campo19}" />
				<gene:campoTrova campo="ST_KEY7" defaultValue="${sessionScope.trovaST_TRG.Campo20}" />
				<gene:campoTrova campo="ST_VAL7" defaultValue="${sessionScope.trovaST_TRG.Campo21}" />
			</gene:gruppoCampi>

		</gene:formTrova>
		
		<div id="dialog-form" title="Seleziona una chiave" style="display:none">
		  <p class="validateTips">Seleziona la chiave sulla quale vuoi filtrare la ricerca</p>
		 
		  <form>
			  <label for="name">Seleziona una chiave</label>
			  <select id="selectChiaveAmbigua" style="display:block" type="text" value="" >
			  </select>
			  <!-- Allow form submission with keyboard without duplicating the dialog button -->
			  <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
		  </form>
		</div>
		
	    
	</gene:redefineInsert>
	<gene:redefineInsert name="trovaCreaNuovo"/>
	
	<gene:javaScript>
	
		// Inizializzazione della drop down 'Schema'
		$(document).ready(function() {

			$( "input[name='campiCount']" ).val( eval($( "input[name='campiCount']" ).val()) + 4);
			
			showObj("rowCampo2", false);
			$( "#Campo2_conf").val('=');
			
		<c:if test='${empty sessionScope.trovaST_TRG}' >
			$( "select[id$='conf']" ).each( function( index ) {
				if (index > 6 && index <= 20) {
					$( this ).val('=');
				}
			});
		</c:if>
	
						
		<c:if test='${not empty sessionScope.trovaST_TRG.Campo24}' >
			$("#Campo24").val('${sessionScope.trovaST_TRG.Campo24}');
		</c:if>
		
		<c:if test='${not empty sessionScope.trovaST_TRG.Campo25}' >
			$("#Campo25").val(${gene:string4Js(sessionScope.trovaST_TRG.Campo25)});
		</c:if>
		
		<c:choose>
			<c:when test='${not empty sessionScope.trovaST_TRG.Campo23}' >
				setValue("Campo23", "${sessionScope.trovaST_TRG.Campo23}");
				setChiavi("${sessionScope.trovaST_TRG.Campo23}", false);
				inizializzaCampiChiaveConSessione();
			</c:when>
			<c:otherwise>
				inizializzaCampiChiave();
			</c:otherwise>
		</c:choose>
						
				
		});
		var tabelleDisponibili;
		var tabelleKey;
		showObj("CampiChiave", false);
		$.ajax({
			url: '${pageContext.request.contextPath}/GetSchemaTabellaCampiChiaveAction.do',
			type: 'POST',
			/*async: false,*/
			dataType: 'json',
			data: {
				metodo: 'getEntita'
			},
			success: function(data) {
				if (data && data.length > 0) {
					var temp = $.map(data, function(item) { return {label: item.descrizione, value: item.chiave}; });
					var size = 0;
					temp.forEach(function(element){if(element.label.length > size){size = element.label.length;}});
					$("#Campo25").attr("size",size);
					$( "#Campo25" ).autocomplete({
					minLength: 0,
					source: function(request, response) {
						var results = $.ui.autocomplete.filter(temp, request.term);
						response(results.slice(0, 20));
					},
					focus: function( event, ui ) {
						return false;
						},
					select: function( event, ui ) {
						$( "#Campo25" ).val(ui.item.label );
						$( "#Campo23" ).val(ui.item.value );
						setChiaviOnChange(ui.item.value, true);
						return false;
						}
					}).focus(function() {
					$(this).autocomplete("search", $(this).val());});
					
				}
			},
			error: function() {
				alert("Errore nel caricamento della della lista tabelle");
			}
		});

		function inizializzaCampiChiaveConSessione() {
			$( "input[id^='Campo']" ).each( function( index ) {
				if (index > 8 && index <= 22) {
					if ($( this ).val() == "" ) {
						$( this ).val('');
						$( this ).prop( "disabled", true );
						$( this ).css('background-color', 'ECECEC' );
						showObj("rowCampo" + (index-1), false);
					}
				}
			});
		}
		
		function inizializzaCampiChiave() {
			$( "input[id^='Campo']" ).each( function( index ) {
				if (index > 8 && index <= 22) {
					$( this ).val('');
					$( this ).prop( "disabled", true );
					$( this ).css('background-color', 'ECECEC' );
					showObj("rowCampo" + (index-1), false);
					showObj("CampiChiave", false);
				}
			});
		}
	
		function setTabelleAutocomplete(lettera){
			$("#Campo23").find('option').remove();
			  tabelleDisponibili.forEach(function(item, index){
				  if(item.descrizione.toUpperCase().includes(lettera.toUpperCase())){
					$("#Campo23").append($("<option/>", {value: item.chiave, text: item.descrizione }));
					
				  }
				});
			
		}
		
		function setChiaviOnChange(value, bool){
			$("#Campo24").val("");
			setChiavi(value, bool);
			
		}
		
		var listaListaChiavi = [[]];
		function setChiavi(nomeTabella, resetCampiChiave) {
			$("#selectChiaveAmbigua").empty();
			if (nomeTabella != "") {
				$.ajax({
					url: '${pageContext.request.contextPath}/GetSchemaTabellaCampiChiaveAction.do',
					type: 'POST',
					dataType: 'json',
					data: {
						tabella: '' + nomeTabella,
						metodo: 'getChiavi'
					},
					success: function(data) {
						if (resetCampiChiave) {
							inizializzaCampiChiave();
						}
						
						if (data && data.length > 0) {
							var index = 0;
							if(data.length > 1){
								if($("#Campo24").val().length > 0){
									var lista = $("#Campo24").val();
									lista = lista.split(",");
									popolaCampiChiave(lista);
								}else{
								openModal();}
								
								listaListaChiavi = [];
								$.map( data, function( list ) {
									var chiave = "";
									var listaChiavi = [];
									$.map( list, function( item ) {
										if(chiave.length > 0){chiave = chiave + " + ";}
										chiave = chiave + item.nomeCampo;
										listaChiavi.push(item.nomeCampo);
										});
									$("#selectChiaveAmbigua").append($("<option/>", {value: index, text: chiave }));
									index++;
									listaListaChiavi.push(listaChiavi);
								});
								
							}
							else{
								
								$.map( data, function( list ) {
									$.map( list, function( item ) {
										index++;
										
										showObj("rowCampo" + (6 + index*2), true);
										
										$("#Campo"+(6 + index*2)).val(item.nomeCampo);
										$("#Campo"+(6 + index*2)).prop( "disabled", true );
										$("#Campo"+(6 + index*2)).css('background-color', 'ECECEC' );
										
										showObj("rowCampo" + (6 + index*2+1), true);
										$("#Campo"+(6 + index*2 + 1)).prop( "disabled", false );
										$("#Campo"+(6 + index*2 + 1)).css('background-color', 'FFFFFF' );
										});
								});
								showObj("CampiChiave", true);
							}
							setValue("Campo2", nomeTabella);

						}
					},
					error: function() {
						alert("Errore nel caricamento del nome dei campi chiave");
					}			
				});
			} else {
				setValue("Campo2", "");
				inizializzaCampiChiave();
				
			}
			
			
		}
		
		function openModal(){
		$( "#dialog-form" ).css({
			"display":"block",
		});
		$( "#dialog-form" ).dialog({
		  resizable: false,
		  height: "auto",
		  width: 400,
		  position: { my: "center", of: ".contenitore-arealavoro"},
		  modal: true,
		  close: function() {
			$('#Campo25').autocomplete('close');
		  },
		  buttons: {
			"Conferma": function() {
			  $('#Campo25').autocomplete( "close" );
			  $('#Campo25').blur();
			  var indexKey = $("#selectChiaveAmbigua").val();
			  var Key = $("#selectChiaveAmbigua").val();
			  var lista = listaListaChiavi[indexKey];
			  popolaCampiChiave(lista);
			  $( this ).dialog( "close" );
			},
			"Annulla": function() {
				$('#Campo23').val("");
				$('#Campo24').val("");
				$('#Campo25').val("");
				
			  $( this ).dialog( "close" );
			}
		  }
		});
		}
		
		function popolaCampiChiave(lista){
			
			showObj("CampiChiave", true);
			$("#Campo24").val(lista.toString());
			var index = 0; 
			$.each(lista, function(key, value){
				index++;
				showObj("rowCampo" + (6 + index*2), true);
				
				$("#Campo"+(6 + index*2)).val(value);
				$("#Campo"+(6 + index*2)).prop( "disabled", true );
				$("#Campo"+(6 + index*2)).css('background-color', 'ECECEC' );
				
				showObj("rowCampo" + (6 + index*2+1), true);
				$("#Campo"+(6 + index*2 + 1)).prop( "disabled", false );
				$("#Campo"+(6 + index*2 + 1)).css('background-color', 'FFFFFF' );
			});
		}
		
		var trovaEseguiOrig = trovaEsegui;
		
		function newTrovaEsegui() {
			for (var i=8; i <=21; i=i+2) {
				if(getValue("Campo" + i) != "") {
					if(getValue("Campo" + (i+1)) != "") {
						$( "#Campo" + (i-1)).prop("disabled", false);
					} else {
						$( "#Campo" + i).prop("disabled", true);
					}
				} else {
					$( "#Campo" + i).val('');
				}
			}
			trovaEseguiOrig();
		}
		
		trovaEsegui = newTrovaEsegui;
	</gene:javaScript>
	
</gene:template>