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
    //var esito = true;
    //var controllo = false; // variabile di controllo modificata se almeno un valore è scelto
    var bottone = document.formRabioBut.azione;
    for(var i=0; i < bottone.length; i++) { // uso bottone.length per sapere quanti radio button ci sono
      if(bottone[i].checked) { // scorre tutti i vari radio button
    //    controllo = true;  // confermo una scelta
       valoreAzione = bottone[i].value;// i; //bottone[i].value; // valore button scelto
       break; // esco dal cliclo
      }
    }
   //valoreAzione = document.formRabioBut.azione.value;
   
		//document.location.href='DettaglioRicerca.do?metodo=crea&famiglia='+valoreAzione;
		if (valoreAzione == '0')
			document.location.href='CreaRicercaBaseWizard.do?'+csrfToken+'&metodo=crea&famiglia='+valoreAzione;
		if (valoreAzione == '1') 
			document.location.href='CreaRicercaWizard.do?'+csrfToken+'&metodo=crea&famiglia='+valoreAzione;
		if (valoreAzione == '2')
			document.location.href='CreaRicercaProspettoWizard.do?'+csrfToken+'&metodo=crea&famiglia='+valoreAzione;
					
		
	}
	
	function annullaCreazione(){
		bloccaRichiesteServer();
		document.location.href = 'DettaglioRicerca.do?'+csrfToken+'&metodo=annullaCrea';
	}

-->
</script>