
<%
	/*
	 * Created on 30/03/2020
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="scheda-template.jsp">

	<gene:redefineInsert name="head" >
		<link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery/dataTable/dataTable/jquery.dataTables.css" >
		<script type="text/javascript" src="${contextPath}/js/jquery.dataTables.min.js?v=${sessionScope.versioneModuloAttivo}"></script>
		
		<style type="text/css">
		
			TABLE.scheda {
				margin-top: 5px;
				margin-bottom: 5px;
				padding: 0px;
				font-size: 11px;
				border-collapse: collapse;
				border-left: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
				width: 100%;
			}
	
			TABLE.scheda TR.intestazione {
				background-color: #EFEFEF;
				border-bottom: 1px solid #A0AABA;
			}
			
			TABLE.scheda TR.intestazione TD, TABLE.scheda TR.intestazione TH {
				padding: 5 2 5 2;
				border-left: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-bottom: 1px solid #A0AABA;
				height: 30px;
				text-align: center;
				font-weight: bold;
			}
		
			TABLE.scheda TR.intestazione TD.archiviato, TABLE.scheda TR.intestazione TH.archiviato, TABLE.scheda TR TD.archiviato {
				width: 80px;
				text-align: center;
			}
		
			TABLE.scheda TR {
				background-color: #FFFFFF;
			}
	
			TABLE.scheda TR TD {
				padding-left: 3px;
				padding-top: 1px;
				padding-bottom: 1px;
				padding-right: 3px;
				text-align: left;
				border-left: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-bottom: 1px solid #A0AABA;
				height: 25px;
				font: 11px Verdana, Arial, Helvetica, sans-serif;
			}
			
			select.tabKynisi {
				width: 300px;
			}

				
		</style>
		

		
	</gene:redefineInsert>

	<gene:insert name="addHistory">
		<gene:historyAdd titolo="Art.80, configurazione delle codifiche relative alla forma giuridica" id="schedaConfigurazioneTabellatiFG" />
	</gene:insert>	

	<gene:setString name="titoloMaschera" value="Art.80, configurazione delle codifiche relative alla forma giuridica" />
	
	<gene:redefineInsert name="corpo">

		<table class="dettaglio-notab">
			<input id="modoapertura" type="hidden" /> 
			
			<tr>
				<td colspan="2">
					<table class="scheda" id="tableConfigurazioneTabellato">
						<thead>
							<tr class="intestazione">
								<td colspan="2">Forma giuridica (tabellato G_043)</td>
								<td rowspan="2">Forma giuridica (codifica del servizio di verifica Art.80)</td>
							</tr>
							<tr class="intestazione">
								<td>Descrizione</td>
								<td class="archiviato">Archiviata&nbsp;?</td>
							</tr>	
						</thead>
						<tbody>
						
						</tbody>
					</table>
				</td>
			</tr>
			
			<tr>
				<td class="comandi-dettaglio" colspan="2">
					<INPUT style="display: none;" type="button" id="pulsantesalvamodifiche" class="bottone-azione" value="Salva" title="Salva"/>
					<INPUT style="display: none;" type="button" id="pulsanteannullamodifiche" class="bottone-azione" value="Annulla" title="Annulla"/>
					<INPUT type="button" id="pulsantemodifica" class="bottone-azione" value="Modifica" title="Modifica"/>
					&nbsp;
				</td>	
			</tr>
		</table>
		
		<form name="listaTabellato" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
			<input type="hidden" name="href" value="gene/tab1/tab1-lista.jsp" /> 
			<input type="hidden" name="cod" value="" />
			<input type="hidden" name="titolo" value="" />
			<input type="hidden" name="metodo" value="apri" />
			<input type="hidden" name="activePage" value="0" />
		</form>
		
	</gene:redefineInsert>

	<gene:redefineInsert name="addToAzioni">
		<tr>
	        <c:if test='${isNavigazioneDisattiva ne "1"}'>
	        	<tr style="display: none;" id="menusalvamodifiche"><td class="vocemenulaterale"><a title="Salva" tabindex="1512" href="#">Salva</a></td></tr>
	          	<tr style="display: none;" id="menuannullamodifiche"><td class="vocemenulaterale"><a title="Annulla" tabindex="1512" href="#">Annulla</a></td></tr>
	          	<tr id="menumodifica"><td class="vocemenulaterale"><a title="Modifica" tabindex="1512" href="#">Modifica</a></td></tr>
	        </c:if>
		</tr>
	</gene:redefineInsert>

	<gene:redefineInsert name="documentiAssociati"></gene:redefineInsert> 
	<gene:redefineInsert name="noteAvvisi"></gene:redefineInsert>

	<gene:javaScript>
	
	
	$(window).on("load",function (){
	
		$("#modoapertura").val("VISUALIZZA");

		function _wait() {
			document.getElementById('bloccaScreen').style.visibility='visible';
			$('#bloccaScreen').css("width",$(document).width());
			$('#bloccaScreen').css("height",$(document).height());
			document.getElementById('wait').style.visibility='visible';
			$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
		}

		function _nowait() {
			document.getElementById('bloccaScreen').style.visibility='hidden';
			document.getElementById('wait').style.visibility='hidden';
		}

		_leggiConfigurazioneTabellato();
		

		function _leggiConfigurazioneTabellato() {
		
			$("#tableConfigurazioneTabellato > tbody > tr").remove();
		
			_wait();
		
			$.ajax({
				type: "POST",
				dataType: "json",
				async: false,
				beforeSend: function(x) {
					if(x && x.overrideMimeType) {
						x.overrideMimeType("application/json;charset=UTF-8");
					}
				},
				url: "ConfigurazioneTabellatiFG.do",
				data: {
					"operazione" : "GETTAB"
				},
				success: function(json){
					if (json) {
						$.map(json, function( tabG_043 ) {
							_tableConfigurazioneTabellatoAddRow(tabG_043);
						});
					}
				},
				error: function(e){
					var messaggio = "Errore durante la lettura delle configurazioni dei tabellati";
					alert(messaggio);
				},
				complete: function() {
					_nowait();	
	            }
			});
		}
	
		function _tableConfigurazioneTabellatoAddRow(tabG_043) {
			var _row = $("<tr/>");
			
			// Colonna voce del tabellato (si riporta la descrizione)
			var _colrif = $("<td/>");
			_colrif.append(tabG_043.t_tab1tip + " - " + tabG_043.t_tab1desc);
			
			// Colonna voce archiviata
			var _colrifarchiviato = $("<td/>",{"class":"archiviato"});
			if (tabG_043.t_tab1arc == "1") {
				_colrifarchiviato.append("Si");
			} else {
				_colrifarchiviato.append("No");
			}
			
			// Colonna con i valore del tabella Kynisi G_071 associato.
			// Se la maschera e' in visualizzazione si visualizza il valore associato
			// Se la maschera e' in modifica si visualizza la drop down con tutti i valori del tabellato G_071,
			// ed eventualmente, si seleziona il valore gia' associato.
			var _colvc = $("<td/>");
			if ($("#modoapertura").val() == "VISUALIZZA") {
				$.map(tabG_043.tabKynisi, function( tabKynisi ) {
					if (tabKynisi.associato == "true") {
						_colvc.append(tabKynisi.kynisi_tab1tip + " - " + tabKynisi.kynisi_tab1desc);
					}
				});
			} else {
				var _select = $("<select/>",{"class":"tabKynisi", "id": tabG_043.t_chiave});
				var _option = $("<option/>",{"text":"","value":""});
				_select.append(_option);
				$.map(tabG_043.tabKynisi, function( tabKynisi ) {
					var _option = $("<option/>",{"text":tabKynisi.kynisi_tab1tip + " - " + tabKynisi.kynisi_tab1desc,"value":tabKynisi.kynisi_tab1tip});
					_select.append(_option);
					if (tabKynisi.associato == "true") {
						_select.val(tabKynisi.kynisi_tab1tip).attr("selected", "selected");
					}
				});
				_colvc.append(_select);
			}
			
			_row.append(_colrif).append(_colrifarchiviato).append(_colvc);
			
			$("#tableConfigurazioneTabellato > tbody:last").append(_row);
		}
	
	
		function _salvaConfigurazioneTabellato() {
		
			_wait();
		
			$("#tableConfigurazioneTabellato > tbody > tr > td > select").each(function () {
				var t_chiave = $(this).attr("id");
				var kynisi_tab1tip = $(this).find('option:selected').val();
				
				$.ajax({
					type: "POST",
					dataType: "json",
					async: false,
					beforeSend: function(x) {
						if(x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
						}
					},
					url: "ConfigurazioneTabellatiFG.do",
					data: "operazione=SETCONFIG&codapp=W_&chiave=" + t_chiave + "&valore=" + kynisi_tab1tip + "&criptato=0"
				});
				


			});

			_nowait();
			
		}
		
		$('#pulsantesalvamodifiche, #menusalvamodifiche').click(function() {
			_salvaConfigurazioneTabellato();
			$("#modoapertura").val("VISUALIZZA");
			$("#pulsantesalvamodifiche").hide();
			$("#menusalvamodifiche").hide();
			$("#pulsanteannullamodifiche").hide();
			$("#menuannullamodifiche").hide();		
			$("#pulsantemodifica").show();
			$("#menumodifica").show();
			$("#alinkIndietro").show();
			_leggiConfigurazioneTabellato();
			window.scrollTo(0,0);
	    });
		
		$('#pulsanteannullamodifiche, #menuannullamodifiche').click(function() {
			$("#modoapertura").val("VISUALIZZA");
			$("#pulsantesalvamodifiche").hide();
			$("#menusalvamodifiche").hide();
			$("#pulsanteannullamodifiche").hide();
			$("#menuannullamodifiche").hide();		
			$("#pulsantemodifica").show();
			$("#menumodifica").show();
			$("#alinkIndietro").show();
			_leggiConfigurazioneTabellato();
			window.scrollTo(0,0);
	    });
		
		$('#pulsantemodifica, #menumodifica').click(function() {
			$("#modoapertura").val("MODIFICA");
			$("#pulsantesalvamodifiche").show();
			$("#menusalvamodifiche").show();
			$("#pulsanteannullamodifiche").show();
			$("#menuannullamodifiche").show();		
			$("#pulsantemodifica").hide();
			$("#menumodifica").hide();
			$("#alinkIndietro").hide();
			_leggiConfigurazioneTabellato();
			window.scrollTo(0,0);
	    });
	
	});
	
	</gene:javaScript>


</gene:template>

