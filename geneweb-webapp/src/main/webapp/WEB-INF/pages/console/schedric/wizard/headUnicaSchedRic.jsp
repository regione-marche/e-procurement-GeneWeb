<%/*
   * Created on 29-mar-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
  // DELLA PAGINA DI CREAZIONE DI UN NUOVO FILTRO DA AGGIUNGERE AD UNA RICERCA BASE
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript">
<!-- 

	function avanti(){
		var esito = true;
		
    if(document.schedRicForm.oraAvvio.value == "" || document.schedRicForm.minutoAvvio.value == ""){
 	 	  esito = false;
	 	  alert("Per proseguire impostare correttamente l'ora di avvio");
    } else if(document.schedRicForm.dataPrimaEsec.value == ""){
 	 	  esito = false;
 	 	  alert("Il campo 'Data di inizio' prevede un valore obbligatorio");
  	}
    if(esito){
    	var dieciMinuti = 600000;
    	var dataOraOdierna = new Date();
			var dataAvvio = new String(document.schedRicForm.dataPrimaEsec.value);
    	var giorno = dataAvvio.substring(0, dataAvvio.indexOf('/'));
    	dataAvvio = dataAvvio.substring(dataAvvio.indexOf('/')+1, dataAvvio.length);
    	var mese = dataAvvio.substring(0, dataAvvio.indexOf('/')) - 1;
    	var anno = dataAvvio.substring(dataAvvio.indexOf('/')+1, dataAvvio.length);
    	var dataOraAvvio = new Date(anno,mese,giorno, document.schedRicForm.oraAvvio.value, document.schedRicForm.minutoAvvio.value, 00);
	  	/*
	  	alert('Data odierna = '         + dataOraOdierna +
	  	    '\nData avvio calcolata = ' + dataOraAvvio +
	  	    '\nData odierna = ' + dataOraOdierna.getTime() + '   Data odierna + 5 min = ' + (dataOraOdierna.getTime() + 300000) +
	  	    '\nData avvio calcolata = ' + dataOraAvvio.getTime() +
	  	    '\nData avvio > data odierna = ' + (dataOraAvvio.getTime() > (dataOraOdierna.getTime() + 300000)));
 	    */
	  	if(dataOraAvvio > dataOraOdierna){
		  	if(dataOraAvvio.getTime() < (dataOraOdierna.getTime() + dieciMinuti)){
		  		if(confirm("Attenzione: la schedulazione che si sta definendo verrà eseguita tra meno di 10 minuti. Continuare?")){
				  	document.schedRicForm.submit();
				  }
				} else {
				  	document.schedRicForm.submit();
				}
			} else {
				alert("L'ora di esecuzione della schedulazione deve essere posteriore all'ora attuale");
			}
		}
	}
	
	function annulla(){
		if (confirm('<fmt:message key="info.schedRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
		  document.location.href='DettaglioSchedRic.do?'+csrfToken+'&metodo=annullaCrea';
		}
	}
	
	function indietro(){
	  document.location.href='WizardSchedRic.do?'+csrfToken+'&pageTo=FRE';
	}
	
	function controllaInputData(unCampoDiInput){
	  return isData(unCampoDiInput);
	}
	
	
-->
</script>