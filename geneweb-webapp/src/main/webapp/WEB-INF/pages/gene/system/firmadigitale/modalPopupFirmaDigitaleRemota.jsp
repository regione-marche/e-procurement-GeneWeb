<%
/*
 * Created on: 04/06/2010
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
		Finestra che visualizza la conferma per l'inserimento delle pubblicazioni bando/esito predefinite
*/
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<c:choose>
	<c:when test="${param.modalita ne 'massiva'}">
		<c:set var="title" value="Firma digitale del documento della stipula"/>
	</c:when>
	<c:otherwise>
		<c:set var="title" value="Firma digitale dei documenti"/>
	</c:otherwise>
</c:choose>

<style type="text/css">
			.ui-dialog-titlebar {
				display: none;
			}
			#sottoTitolo{
				font:11px Verdana, Arial, Helvetica, sans-serif;
			}
</style>
		
<div id="dialog-form" style="display:none">
  <b>${title}</b>
	  <br>
	  <br>
  <span id="statusKo" style="display:none">
  <br>
	Non ci sono documenti da firmare
	
	<span id="infoStatusKoMessageNumFormato" style="font-style: italic; font-size: 10px; font-weight: bold; display:none;">
		<br>
		<span id='statusKoMessageNumFormato'> </span> per cui &egrave richiesta la firma senza allegato
	</span>
	<br>
	<br>
  </span>
  <span id="statusOk" style="display:none">
	  
		<c:choose>
			<c:when test="${param.modalita ne 'massiva'}">
				<span id="sottoTitolo">Documento da firmare: <span id='nomeDocumento'> </span></span>
			</c:when>
			<c:otherwise>
				<span id="sottoTitolo">Documenti da firmare: <span id='numeroDocumenti'> </span></span>
				
				<span id="infoMessageDocDaFirmare" style="font-style: italic; font-size: 10px; font-weight: bold;">
					<br>
					<span id='numeroDocumentiFormato'> </span> per cui &egrave richiesta la firma senza allegato
				</span>
				
			</c:otherwise>
		</c:choose>
	  <br>
	  <p class="validateTips">Per procedere alla firma digitale del documento, immettere le credenziali:</p>
	  <form>
		  <table class="dati-login" style="width:auto!important;">
		  <tr>
			<td class="etichetta-dato">Alias&nbsp;&nbsp;
			</td>
			<td class="valore-dato">
				<input id="user-dialog" name="alias" title="Alias" class="testo" type="text" size="24" value="" maxlength="100"/>
				<c:choose>
					<c:when test="${param.modalita ne 'massiva' && param.firmaProvider ne 3}">
						<input type="button" class="bottone-azione" value='Richiedi OTP' title='Richiedi OTP' onclick="javascript:richiediOTP();">
					</c:when>
				</c:choose>			
			</td>
		  </tr>
		  <tr>
			<td class="etichetta-dato">Pin&nbsp;&nbsp;
			</td>
			<td class="valore-dato">
				<input id="pass-dialog" name="pin" title="Pin" class="testo" type="password" size="24" value="" maxlength="100" autocomplete="new-password"/>
			</td>
		  </tr>
		  <tr>
			<td class="etichetta-dato">OTP&nbsp;&nbsp;
			</td>
			<td class="valore-dato">
				<input id="otp-dialog" name="otp" title="otp" class="testo" type="text" size="24" value="" maxlength="100"/>
			</td>
		  </tr>
		  <tr id="trModalitaFirma">
			<td colspan="2">
			<br>
			<input type="radio" value="cades" name="modalitaFirma" id="CAdES" checked="true"/><span>Firma P7M (CAdES)&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<input type="radio" value="pades" name="modalitaFirma" id="PAdES"/><span id="padesLabel" >Firma PDF (PAdES)</span>
			</td>
		  </tr>
		  </table>
		  <input type="hidden" value="${param.modalita}" name="isMassiva" id="isMassiva"/>
		  <input type="hidden" value="${param.entitaFirma}" name="entitaFirma" id="entitaFirma"/>
		  <input type="hidden" value="${param.numeroDocumenti}" name="numeroDocumentiHidden" id="numeroDocumentiHidden"/>
		  <input type="hidden" value="${param.numeroDocumentiFormato}" name="numeroDocumentiFormatoHidden" id="numeroDocumentiFormatoHidden"/>
		  <input type="hidden" value="${param.chiaveFirma}" name="chiaveFirma" id="chiaveFirma"/>
	  </form>
	  <textarea id="errorMessage" style="color:red;resize:none;display:none" disabled name="errorMessage" cols="59" rows="7"></textarea>
	</span>
</div>

<script type="text/javascript"> 

document.getElementById('trModalitaFirma').style.visibility = 'hidden';

function _nowait() {
	document.getElementById('bloccaScreen').style.visibility = 'hidden';
	document.getElementById('wait').style.visibility = 'hidden';
}

var pinFirma;
var aliasFirma;

function openModal(idprg,iddocdig,nome,contextPath,coacod){
	var isNotDocumenti = ($("#numeroDocumentiHidden").val() == null || $("#numeroDocumentiHidden").val() == 0) && $("#isMassiva").val() == "massiva";
	if(!isNotDocumenti){
		
	
		$( "#nomeDocumento" ).html("<b>" + nome + "</b>");
		$( "#numeroDocumenti" ).html("<b>" + $("#numeroDocumentiHidden").val() + "</b>");
		if($("#numeroDocumentiFormatoHidden").val() > 0){
			if($("#numeroDocumentiFormatoHidden").val() == 1){
				$( "#numeroDocumentiFormato" ).html("*C'&egrave <b>" + $("#numeroDocumentiFormatoHidden").val() + "</b> documento");
			} else {
				$( "#numeroDocumentiFormato" ).html("*Ci sono <b>" + $("#numeroDocumentiFormatoHidden").val() + "</b> documenti");
			}
		} else {
			$("#infoMessageDocDaFirmare").css("display","none");
		}
		
		var vet = nome.split(".");
		var ext = vet[vet.length-1];
		ext = ext.toUpperCase();
		if(ext != 'PDF'){
			$("#PAdES").attr('disabled', true);
			$("#padesLabel").css('color', 'grey');
		}
	}
	$( "#dialog-form" ).css({
		"display":"block",
	});
	$( "#dialog-form" ).dialog({
	  resizable: false,
	  height: "auto",
	  width: 500,
	  position: { my: "center", of: ".contenitore-arealavoro"},
	  modal: true,
	  close: function() {
	  },
	  buttons: [
	  {
		text: "Conferma",
		id:"ConfermaModal",
		click: function () {
			var pin = $("#pass-dialog").val();
			var alias = $("#user-dialog").val();
			var otp = $("#otp-dialog").val();
			
			if(!pin || !alias || !otp){
				alert("I campi Alias, Pin e OTP sono obbligatori");
				return;
			}
				
			setCredenziali(idprg,iddocdig,contextPath,coacod);
		}
      },
	  {
		text: "Annulla",
		id:"AnnullaModal",
		click: function () {
			$("#pass-dialog").val("");
			$("#user-dialog").val("");
			$("#otp-dialog").val("");
			$("#errorMessage").html("");
			$("#errorMessage").css("display","none");
			$( this ).dialog( "close" );
		}
      },
	  {
		text: "Chiudi",
		id:"ChiudiModal",
		style:"display:none;",
		click: function () {
			$("#pass-dialog").val("");
			$("#user-dialog").val("");
			$("#otp-dialog").val("");
			$("#errorMessage").html("");
			$("#errorMessage").css("display","none");
			$( this ).dialog( "close" );
			historyReload();
		}
      }]
	});
	if(!isNotDocumenti){
		$("#statusOk").css("display","block");
		$("#statusKo").css("display","none");
		$("#infoStatusKoMessageNumFormato").css("display","none");
		$.ajax({
			url: contextPath+'/SetFirmaDigitaleRemota.do',
			type: 'POST',
			async: false,
			dataType: 'json',
			data: {
				metodo: 'getCredenziali'
			},
			success: function(data) {
					pinFirma = data.pin;
					aliasFirma = data.alias;
					$("#pass-dialog").val(pinFirma);
					$("#user-dialog").val(aliasFirma);
			},
			error: function() {
				_nowait();
				alert("Errore nel caricamento della tabella");
			}
		});
	} else {
		if($("#numeroDocumentiFormatoHidden").val() > 0){
			if($("#numeroDocumentiFormatoHidden").val() == 1){
				$( "#statusKoMessageNumFormato" ).html("*C'&egrave <b>" + $("#numeroDocumentiFormatoHidden").val() + "</b> documento");
			} else {
				$( "#statusKoMessageNumFormato" ).html("*Ci sono <b>" + $("#numeroDocumentiFormatoHidden").val() + "</b> documenti");
			}
			$("#infoStatusKoMessageNumFormato").css("display","block");
		} else {
			$("#infoStatusKoMessageNumFormato").css("display","none");
		}
		
		$("#statusKo").css("display","block");
		$("#statusOk").css("display","none");
		$("#ChiudiModal").css("display","block");
		$("#ConfermaModal").css("display","none");
		$("#AnnullaModal").css("display","none");
	}
}

function setCredenziali(idprg,iddocdig,contextPath,coacod){
	var isMassiva = document.getElementById('isMassiva').value;
	var entitaFirma = document.getElementById('entitaFirma').value;
	var chiaveFirma = document.getElementById('chiaveFirma').value;
	document.getElementById('bloccaScreen').style.visibility='visible';
	$('#bloccaScreen').css("width",$(document).width());
	$('#bloccaScreen').css("height",$(document).height());
	document.getElementById('wait').style.visibility='visible';
	$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
	var modalitaFirma = $('input[name=modalitaFirma]:checked').val();
	if(modalitaFirma == null){alert("Selezionare una tipologia di firma");return;}
	$.ajax({
		url: contextPath+'/SetFirmaDigitaleRemota.do',
		type: 'POST',
		async: true,
		dataType: 'json',
		data: {
			metodo: 'setFirmaDigitale',
			pin: $("#pass-dialog").val(),
			alias: $("#user-dialog").val(),
			otp: $("#otp-dialog").val(),
			idprg: idprg,
			iddocdig: iddocdig,
			c0acod: coacod,
			modalitaFirma : modalitaFirma,
			isMassiva:isMassiva,
			entitaFirma:entitaFirma,
			chiaveFirma:chiaveFirma
		},
		success: function(data) {
			var response = data.message;
			if(response != "ok"){
				$("#errorMessage").html(response);
				$("#errorMessage").css("display","block");
				_nowait();
			}else{
				historyReload();
			}
			
		},
		error: function() {
			_nowait();
			alert("Errore nella richiesta di firma documenti");
		},
		complete: function() {
			$("#ChiudiModal").css("display","block");
			$("#ConfermaModal").css("display","none");
			$("#AnnullaModal").css("display","none");
			_nowait();
		}
	});
}

function richiediOTP(){
	var alias = $("#user-dialog").val();
	console.log(alias);
	if(alias == null || alias == ''){alert("Per richiedere l'OTP inserire l'alias");return;}
	document.getElementById('bloccaScreen').style.visibility='visible';
	$('#bloccaScreen').css("width",$(document).width());
	$('#bloccaScreen').css("height",$(document).height());
	document.getElementById('wait').style.visibility='visible';
	$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
	$.ajax({
		url: contextPath+'/RichiestaOTP.do',
		type: 'POST',
		async: true,
		dataType: 'json',
		data: {
			alias: alias
		},
		success: function(data) {
			var response = data.message;
			if(response != "ok"){
				$("#errorMessage").html(response);
				$("#errorMessage").css("display","block");
				_nowait();
			}
		},
		error: function() {
			_nowait();
			alert("Errore nella richiesta dell'OTP");
		},
		complete: function() {
			_nowait();
		}
	});
}
</script>



