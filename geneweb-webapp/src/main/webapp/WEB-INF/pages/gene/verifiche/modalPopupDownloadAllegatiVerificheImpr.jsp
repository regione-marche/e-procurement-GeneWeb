<%
/*
 * Created on: 04/10/2021
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

<style type="text/css">
	.ui-dialog-titlebar {
		display: none;
	}
	span{
		font:11px Verdana, Arial, Helvetica, sans-serif;
	}
	  #avanzamento{
    position: relative;
  }
  #avanzamento-label {
    position: absolute;
    left: 50%;
    top: 4px;
    font-weight: bold;
    text-shadow: 1px 1px 0 #fff;
  }
</style>
  
<div id="dialog-form-download-allegati" style="display:none">
	<br>
	<b>Esporta documenti verifiche su file zip</b>
	<br>
	<div id="progressbardiv" style="display:none">
		<img src="${pageContext.request.contextPath}/img/${applicationScope.pathCss}progressbar.gif" alt=""/>
		<br><br>Esportazione in corso...
	</div>
	<p class="validateTips">
	<span id="tipMsg">Mediante questa funzione è possibile esportare in un unico file zip i documenti delle verifiche art.80.</span>
	<span id= "tipConfirm"><br><br>Confermi l'operazione ?</span>
	<span id="tipWaiting">Esportazione dei documenti in corso.</span>
	<br><br></p>
	<br>
	<div id="loading">
		<div id="avanzamento"><div id="avanzamento-label"></div></div>
		
	</div>
	<span id="errorMessage" style="color:red;display:none"><br></span>

</div>

<script type="text/javascript"> 



function _nowait() {
	document.getElementById('bloccaScreen').style.visibility = 'hidden';
	document.getElementById('wait').style.visibility = 'hidden';
}

var annullato = false;
var archivioCreato = "false";

function openModalDownloadDoc(codimp,contextPath){
	$("#loading").hide();
	$("#avanzamento-label").html("0%");
	annullato = false;
	archivioCreato = "false";
	$("#tipMsg").show();
	$("#tipConfirm").show();
	$("#tipWaiting").hide();
	$("#button-conferma").show();
	$( "#dialog-form-download-allegati" ).css({
		"display":"block",
	});
		
	$( "#dialog-form-download-allegati" ).dialog({
	  resizable: false,
	  height: "auto",
	  width: 500,
	  position: { my: "center", of: ".contenitore-arealavoro"},
	  modal: true,
	  close: function() {
	  },
	  buttons: [
        {
			text:"Conferma",
			id:"button-conferma",
			click: function() {
			$('#continueButton').attr("disabled", true);
			$("#loading").show();
			$( "#avanzamento" ).progressbar({
			  value: 0
			});
			$("#tipMsg").hide();
			$("#tipConfirm").hide();
			$("#tipWaiting").show();
			downloadDocumentazione(codimp);
			$("#button-conferma").hide();
			},
		
		},{
			text:"Annulla",
			click: function() {
			annullato = true;
			$( this ).dialog( "close" );
			}
		}
		]
	});
	
}


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
	
	
	function ajaxCallDownload(counter,path,maxCount,listaDoc,uffint){
		if(annullato){
			//si devono cancellare i fie temporanei
			$.ajax({
				type: "POST",
				url: "${pageContext.request.contextPath}/ScaricaTuttiAllegatiVerifiche.do",
				data: {
					path: path,
					archivioCreato: archivioCreato,
					metodo: 'cancellaFileTemporanei'
				},
				async: true,
				dataType: "json"
			});
			return;
		}
		if((!maxCount || maxCount == 0) || (counter > maxCount && archivioCreato == "false")){
			alert("Non è presente nessun documento da esportare");
			return $("#dialog-form-download-allegati").dialog( "close" );
		}else{
			if(counter > maxCount && archivioCreato == "true"){
			document.location.href='pg/ScaricaTuttiAllegatiVerifiche.do?'+csrfToken+'&metodo=download&path='+encodeURIComponent(path);
			return $("#dialog-form-download-allegati").dialog( "close" );
		}
		var allegatiVerifica = "true";
		var iddocdg = listaDoc[counter][0].value;
		var idprgDoc = listaDoc[counter][1].value;
		var tipoVer = listaDoc[counter][3].value;
		if (counter>0){
		var tipoVerPre = listaDoc[counter-1][3].value;
		}
		if((!iddocdg || !idprgDoc)){
			return ajaxCallDownload(counter+1,path,maxCount,listaDoc,uffint);
			}
		}
		
		$.ajax({
			type: "POST",
			url: "${pageContext.request.contextPath}/ScaricaTuttiAllegatiVerifiche.do",
			data: {
				path: path,
				idddocdg: iddocdg,
				idprg: idprgDoc,
				tipoVer: tipoVer,
				tipoVerPre: tipoVerPre,
				allegatiVerifica: allegatiVerifica,
				archivioCreato: archivioCreato,
				uffint: uffint,
				metodo: 'creaArchivio'
			},
			async: true,
			dataType: "json",
			success:function(data){
				archivioCreato = "true";
				counter++;
				var perc = Math.round((counter / maxCount) * 100);
				$("#avanzamento-label").html(perc + "%");
				$( "#avanzamento" ).progressbar({
				  value: perc
				});
				if (counter < maxCount){
					ajaxCallDownload(counter,path,maxCount,listaDoc,uffint);
				}else{
					document.location.href='ScaricaTuttiAllegatiVerifiche.do?'+csrfToken+'&metodo=download&path='+encodeURIComponent(path);
					return $("#dialog-form-download-allegati").dialog( "close" );
				}
			}
		});
	}
	
	function downloadDocumentazione(codimp) {
		$.ajax({
			type: "POST",
			url: "${pageContext.request.contextPath}/ScaricaTuttiAllegatiVerifiche.do",
			data: {
				codimp: codimp,
				metodo: 'getDocVerifiche'
			},
			async: true,
			dataType: "json",
			success:function(data){			
				getPath(codimp,data.maxCount,data.listaDoc);
			},
			error: function(e){
				alert("Si è verificato un problema");
			}
		});
	
	return;
	
	}
	
	function getPath(codimp,maxCount,listaDoc) {
		var documenti = {"documenti":[]};
		
		for(i=0; i < maxCount; i++){
			var iddocdg = listaDoc[i][0].value;
			var idprgDoc = listaDoc[i][1].value;	
			var nomeDoc = listaDoc[i][2].value;
			var tipoVer = listaDoc[i][3].value;		
			documenti.documenti.push({
			    "idprg": idprgDoc, 
			    "iddocdg":iddocdg, 
			    "nomeDoc":nomeDoc,
				"tipoVer":tipoVer
			  });
		}
		
		var documentiStringa = JSON.stringify(documenti);
		
		$.ajax({
			type: "POST",
			url: "${pageContext.request.contextPath}/ScaricaTuttiAllegatiVerifiche.do",
			data: {
				codimp: codimp,
				documenti: documentiStringa,
				metodo: 'getPath'
			},
			async: true,
			dataType: "json",
			success:function(data){
				var uffint= "${sessionScope.uffint}";
				ajaxCallDownload(0,data.path,maxCount,listaDoc,uffint);
			},
			error: function(e){
				alert("Si è verificato un problema");
			}
		});
	
	return;
	
	}

</script>