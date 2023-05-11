
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />



<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENEWEB" idMaschera="SchedaQformlib">
	
	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${contextPath}/js/qformlibCustom.js"></script>
	</gene:redefineInsert>
	
	<% // Settaggio delle stringhe utilizzate nel template %>
	
	
	
	<gene:redefineInsert name="corpo">
			<gene:formScheda entita="QFORMLIB" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreQFORMLIB" >
			
			<c:choose>
			 <c:when test='${modo eq "NUOVO"}'>
			 	<gene:setString name="titoloMaschera" value='Nuovo modello Q-form' />
			 </c:when>
			 <c:otherwise>
			 	<gene:setString name="titoloMaschera" value='Q-form ${datiRiga.QFORMLIB_CODMODELLO}' />
			 </c:otherwise>
			</c:choose>
			
			
			<gene:redefineInsert name="schedaModifica">
				<c:if test='${datiRiga.QFORMLIB_STATO ne 2 and datiRiga.QFORMLIB_STATO ne 4 and datiRiga.QFORMLIB_MODINTERNO eq 2}'>
					<tr>
						<td class="vocemenulaterale" >
							<a href="javascript:schedaModifica();" title="Modifica"  tabindex="1501">Modifica</a>
						</td>
					</tr>
				</c:if>
				
			</gene:redefineInsert>
			
			<gene:redefineInsert name="addToAzioni" >
				<c:if test='${modo eq "VISUALIZZA"}'>
					<c:if test="${(datiRiga.QFORMLIB_STATO eq 1 or  datiRiga.QFORMLIB_STATO eq 3) and datiRiga.QFORMLIB_MODINTERNO eq 2}" >
						<tr>
						  <td class="vocemenulaterale" >
								  <a href="javascript:qformEditor(${datiRiga.QFORMLIB_ID },'QFORMLIB','false');" title="Modifica q-form" >Modifica Q-form</a>
						  </td>
					  	</tr>
					</c:if>
					<c:if test="${(datiRiga.QFORMLIB_STATO eq 2 or  datiRiga.QFORMLIB_STATO eq 4) }" >
						<tr>
						  <td class="vocemenulaterale" >
								  <a href="javascript:qformEditor(${datiRiga.QFORMLIB_ID },'QFORMLIB','true');" title="Visualizza q-form" >Visualizza Q-form</a>
						  </td>
					  	</tr>
					</c:if>
					<tr>
					  <td class="vocemenulaterale" >
							  <a href="javascript:qformAnteprima(${datiRiga.QFORMLIB_ID },'QFORMLIB');" title="Anteprima q-form" >Anteprima Q-form</a>
					  </td>
					</tr>
					<c:if test="${datiRiga.QFORMLIB_STATO eq 1 or  datiRiga.QFORMLIB_STATO eq 3}" >
						<tr>
						  <td class="vocemenulaterale" >
								  <a href="javascript:apriModaleQform(2);" title="Attiva modello" >Attiva modello</a>
						  </td>
					  	</tr>
					</c:if>
				  	<c:if test="${datiRiga.QFORMLIB_STATO eq 2}" >
					  	<tr>
						  <td class="vocemenulaterale" >
								  <a href="javascript:apriModaleQform(3);" title="Disttiva modello" >Disattiva modello</a>
						  </td>
					  	</tr>
				  	</c:if>
				  	<c:if test="${datiRiga.QFORMLIB_STATO eq 2}" >
					  	<tr>
						  <td class="vocemenulaterale" >
								  <a href="javascript:apriModaleQform(4);" title="Archivia modello" >Archivia modello</a>
						  </td>
					  	</tr>
				  	</c:if>
				</c:if>	
			</gene:redefineInsert>
		
			<gene:gruppoCampi idProtezioni="GEN" >
				<gene:campoScheda>
					<td colspan="2"><b>Dati generali</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="CODMODELLO" modificabile='false'  />
				<gene:campoScheda campo="TITOLO" obbligatorio="true"/>
				<gene:campoScheda campo="DESCRIZIONE"/>
				<gene:campoScheda campo="TIPOLOGIA" obbligatorio="true" modificabile='${ modo eq "NUOVO" }'/>
				<gene:campoScheda campo="DATAINI" />
				<gene:campoScheda campo="DATAFINE" />
				<gene:campoScheda campo="MODINTERNO" modificabile='false' defaultValue="2"/>	
				<gene:campoScheda campo="STATO" defaultValue="1" modificabile='false'/>
				<gene:campoScheda campo="ID" visibile="false"/>
				<gene:campoScheda campo="OGGETTO" visibile="false"/>
				<gene:campoScheda campo="DULTAGG" modificabile='false'/>
			</gene:gruppoCampi>
		
			<jsp:include page="qformlib-scheda-custom.jsp" />
					
			
			<gene:campoScheda>	
				<td class="comandi-dettaglio" colSpan="2">
					<gene:insert name="addPulsanti"/>
					<c:choose>
					<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
						<gene:insert name="pulsanteSalva">
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
						</gene:insert>
						<gene:insert name="pulsanteAnnulla">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
						</gene:insert>
				
					</c:when>
					<c:otherwise>
						<gene:insert name="pulsanteModifica">
							<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and (datiRiga.QFORMLIB_STATO eq 1 or datiRiga.QFORMLIB_STATO eq 3) and datiRiga.QFORMLIB_MODINTERNO eq 2}'>
								<INPUT type="button"  class="bottone-azione" value='Modifica' title='Modifica' onclick="javascript:schedaModifica()">
							</c:if>
						</gene:insert>
						<c:if test='${(datiRiga.QFORMLIB_STATO eq 1 or  datiRiga.QFORMLIB_STATO eq 3) and datiRiga.QFORMLIB_MODINTERNO eq 2}'>
							<INPUT type="button"  class="bottone-azione" value='Modifica Q-form' title='Modifica Q-form' onclick="javascript:qformEditor(${datiRiga.QFORMLIB_ID },'QFORMLIB','false');">
						</c:if>	
						<c:if test='${(datiRiga.QFORMLIB_STATO eq 2 or  datiRiga.QFORMLIB_STATO eq 4) }'>
							<INPUT type="button"  class="bottone-azione" value='Visualizza Q-form' title='Visualizza Q-form' onclick="javascript:qformEditor(${datiRiga.QFORMLIB_ID },'QFORMLIB','true');">
						</c:if>	
						<INPUT type="button"  class="bottone-azione" value='Anteprima Q-form' title='Anteprima Q-form' onclick="javascript:qformAnteprima(${datiRiga.QFORMLIB_ID },'QFORMLIB');">
						<gene:insert name="pulsanteNuovo">
							<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO") }'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovo()" id="btnNuovo">
							</c:if>
						</gene:insert>
					</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</gene:campoScheda>
			
				
			
			
		</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript>
		
		function _wait() {
			document.getElementById('bloccaScreen').style.visibility = 'visible';
			$('#bloccaScreen').css("width", $(document).width());
			$('#bloccaScreen').css("height", $(document).height());
			document.getElementById('wait').style.visibility = 'visible';
			$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200 });
		}

		/*
			* Nasconde l'immagine di attesa
			*/
		function _nowait() {
			document.getElementById('bloccaScreen').style.visibility = 'hidden';
			document.getElementById('wait').style.visibility = 'hidden';
		}
		
		function impostaStato(tipo){
			_wait();
			id = $('#QFORMLIB_ID').val();
			cod = $('#QFORMLIB_CODMODELLO').val();
			$.ajax({
				type: "GET",
				dataType: "text",
				async: false,
				beforeSend: function (x) {
					if (x && x.overrideMimeType) {
						x.overrideMimeType("application/text");
					}
				},
				url: contextPath + "/SetStatoQformlib.do",
				data: {
					id: id,
					tipo: tipo,
					cod: cod
				},
				success: function (data) {
					if (data) {
						if(data.esito="true")
							historyReload();
					}
				},
				error: function (e) {
					alert("Errore durante la modifica dello stato del qform");
				},
				complete: function () {
					_nowait();
				}
			});
		  //}
		}
		
		function apriModaleQform(tipo){
			var titolo="Attiva modello";
			if(tipo==3)
				titolo="Disattiva modello";
			else if(tipo==4)
				titolo="Archivia modello";
			var opt = {
				open: function(event, ui) { 
					$(this).parent().children().children('.ui-dialog-titlebar-close').hide();
					$(this).parent().css("border-color","#C0C0C0");
					var _divtitlebar = $(this).parent().find("div.ui-dialog-titlebar");
					_divtitlebar.css("border","0px");
					_divtitlebar.css("background","#FFFFFF");
					var _dialog_title = $(this).parent().find("span.ui-dialog-title");
					_dialog_title.css("font-size","13px");
					_dialog_title.css("font-weight","bold");
					_dialog_title.css("color","#002856");
					$(this).parent().find("div.ui-dialog-buttonpane").css("background","#FFFFFF");
				},
				autoOpen: false,
				modal: true,
				width: 550,
				height:200,
				title: titolo,
				buttons: {
					"Conferma": {
						id:"botConferma",
						text:"Conferma",
						click: function() {
							impostaStato(tipo);
						}
					 },
					"Annulla": {
						id:"botAnnulla",
						text:"Annulla",
						click:function() {
							$( this ).dialog( "close" );
							$("#trAttivazione").hide();
							$("#trDisattivazione").hide();
							$("#trArchiviazione").hide();
						}
					 }
				 }
			};
									
			$("#mascheraImpostaStato").dialog(opt).dialog("open");
			if(tipo==2){
				var oggetto=getValue("QFORMLIB_OGGETTO");
				var oggettoInizializzazione="{\"survey\": {\"surveyType\": \"1\" }}";
          		var esito = oggetto.normalize() === oggettoInizializzazione.normalize();
          		
				if(esito){
					$("#spanNoAttivazione").show();
					$("#sapnConfermaAttivazione").hide();
					$("#botConferma").hide();
				}else{
					$("#spanNoAttivazione").hide();
					$("#sapnConfermaAttivazione").show();
					$("#botConferma").show();
				}
				$("#trAttivazione").show();
			}else if(tipo==3){
				$("#trDisattivazione").show();
			}else{
				$("#trArchiviazione").show();
			}
		}
	</gene:javaScript>
</gene:template>

<div id="mascheraImpostaStato" title="Scelta modalit&agrave; inserimento documenti" style="display:none;">
	<table style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
		<tr id="trAttivazione" style="display:none;"> 
			<td colspan="2">
				Mediante tale funzione si procede all'attivazione del modello. 
				<br><br>
				<span id="spanNoAttivazione"><b>Non &egrave; possibile procedere perch&egrave; non &egrave; stato dettagliato il Q-form.</b><br>Premere il pulsante 'Modifica Q-form' per accedere al dettaglio.</span>
				<span id="sapnConfermaAttivazione">Confermi l'operazione?</span>
				
			</td>				
		</tr>
		<tr id="trDisattivazione" style="display:none;"> 
			<td colspan="2" >
				Mediante tale funzione si procede alla disattivazione del modello.
				<br><br>
				Confermi l'operazione?
			</td>				
		</tr>
		<tr id="trArchiviazione" style="display:none;"> 
			<td colspan="2">
				Mediante tale funzione si procede all'archiviazione del modello.
				<br>Si sottolinea che, una volta archiviato, il modello non pu&ograve; pi&ugrave; essere ripristinato.
				<br><br>
				Confermi l'operazione?
			</td>				
		</tr>
	</table>
	</div>