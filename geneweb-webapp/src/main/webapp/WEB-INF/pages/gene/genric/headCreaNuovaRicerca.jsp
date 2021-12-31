<%
/*
 * Created on 28-feb-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI SELEZIONE 
 // DEL TIPO RICERCA DA CREARE CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript">
<!-- 

	// Azioni invocate dal menu contestuale

	function creaNuovaRicerca(){
    var valoreAzione = null;
		var href = null;
    //var esito = true;
    //var controllo = false; // variabile di controllo modificata se almeno un valore è scelto
    var bottone = document.formRabioBut.azione;

    var arrayLen = "" + bottone.length;
		if (arrayLen != 'undefined') {
      for (var i=0; i < bottone.length; i++) { // uso bottone.length per sapere quanti radio button ci sono
        if (bottone[i].checked) { // scorre tutti i vari radio button
        // controllo = true;  // confermo una scelta
         valoreAzione = bottone[i].value;// i; //bottone[i].value; // valore button scelto
         break; // esco dal cliclo
        }
      }
    } else {
      valoreAzione = bottone.value;
    }

		if (valoreAzione == '0') {
			href = 'CreaEliminaRicercaBase.do?'+csrfToken+'&metodo=crea&famiglia=' + valoreAzione;
		}
		if (valoreAzione == '1') {
			href = 'CreaEliminaRicerca.do?'+csrfToken+'&metodo=crea&famiglia=' + valoreAzione;
		}
		if (valoreAzione == '2') {
			href = 'CreaEliminaRicercaProspetto.do?'+csrfToken+'&metodo=crea&famiglia=' + valoreAzione;
		}
		if (valoreAzione == '3') {
			var objIdRicFam = document.getElementById("idRicercaFamiglia");
			if (objIdRicFam.selectedIndex != null && objIdRicFam.selectedIndex > 0){
				var idRicerca = objIdRicFam.options[objIdRicFam.selectedIndex].value;
				href = 'CaricaRicerca.do?'+csrfToken+'&idRicerca=' + idRicerca;
			} else {
				alert('Selezionare uno dei report predefiniti per continuare');
			}
		}
		if (valoreAzione == '4') {
			href = 'CreaEliminaRicercaSql.do?'+csrfToken+'&metodo=crea&famiglia=' + valoreAzione;
		}
		if (href != null) {
			document.location.href = href;
		}
	}
	
	function annullaCreazione() {
		bloccaRichiesteServer();
		document.location.href = 'DettaglioRicerca.do?'+csrfToken+'&metodo=annullaCrea';
	}

	function cambiaRadio() {
		var radioButton = document.formRabioBut.azione;
		var len = radioButton.length;
		radioButton[len-1].checked = true;
	}
-->
</script>
