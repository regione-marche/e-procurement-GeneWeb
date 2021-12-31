<%
/*
 * Created on 03-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA TROVA RICERCA 
 // CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript">
<!--

	function avviaRicercaRic(){
		var operatoreDataEsec = document.trovaCodaSchedForm.operatoreDataEsec.value;
		var continua = true;
		if(operatoreDataEsec != null && operatoreDataEsec != ""){
			if(operatoreDataEsec.indexOf(".") >= 0){
				var dataFrom = document.trovaCodaSchedForm.dataEsecSuc.value;
				var dataTo   = document.trovaCodaSchedForm.dataEsecPrec.value;
				if(((dataFrom == "" || dataFrom == null ) && (dataTo != null && dataTo != "")) || ((dataFrom != null && dataFrom != "") && (dataTo == "" || dataTo == null))){
					if(dataFrom == "" || dataFrom == null){
						alert("Valorizzare il limite inferiore del filtro sulla data");
						document.trovaCodaSchedForm.dataEsecSuc.focus();
						continua = false;
					} else if(dataTo == ""){
						alert("Valorizzare il limite superiore del filtro sulla data");
						document.trovaCodaSchedForm.dataEsecPrec.focus();
						continua = false;
					}
				} else {
					if(dataFrom > dataTo){
						alert("Intervallo temporale non valido");
						document.trovaCodaSchedForm.dataEsecPrec.focus();
						continua = false;
					}
				}
			}
		}
		if(continua){
			document.trovaCodaSchedForm.metodo.value = "trovaCodaSched";
			document.trovaCodaSchedForm.submit();
		}
	}
	
	function nuovaRicerca(){
		document.trovaCodaSchedForm.metodo.value="nuovaRicerca";
		document.trovaCodaSchedForm.submit();
	}

	function resetRicerca(){
		document.trovaCodaSchedForm.reset();
	}
	
  function apriTrovaRicerche(){
		document.location.href='InitTrovaCodaSched.do?'+csrfToken;
  }

	function controllaInputData(unCampoDiInput){
	  return isData(unCampoDiInput);
	}
	
	var operatoreDataBeforeChange="${trovaCodaSchedForm.operatoreDataEsec}";

	function visualizzaDataEsecSuc(){
		var valoreConfronto = document.trovaCodaSchedForm.operatoreDataEsec.value;
		
		if(valoreConfronto.indexOf(".") >= 0){
			if("<.<" == valoreConfronto){
				document.getElementById("span_dataEsec_testo").innerHTML = "&nbsp;data&nbsp;";
			} else {
				document.getElementById("span_dataEsec_testo").innerHTML = "&nbsp;data&nbsp;";
			}
			document.getElementById("span_dataEsecSuc").style.display = "inline";
			if(operatoreDataBeforeChange.indexOf(".") < 0){
				document.trovaCodaSchedForm.dataEsecSuc.value = "";
				document.trovaCodaSchedForm.dataEsecPrec.value = "";
			}
		} else {
			document.getElementById("span_dataEsec_testo").innerHTML = "";
			document.getElementById("span_dataEsecSuc").style.display = "none";
			if(operatoreDataBeforeChange.indexOf(".") >= 0){
				document.trovaCodaSchedForm.dataEsecSuc.value = "";
				document.trovaCodaSchedForm.dataEsecPrec.value = "";
			}
		}
		operatoreDataBeforeChange = valoreConfronto;
	}

	function gestisciVisualizzazioneAvanzata() {
		var checkboxavanzate = document.getElementById("visualizzazioneAvanzata");
		var test = checkboxavanzate.checked;
		if (test) {
			trovaVisualizzazioneOperatori('visualizza');
		} else {
			nuovaRicerca();
		}
	}
-->
</script>