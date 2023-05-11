<%
/*
 * Created on 28-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO 
 // DATI GENERALI DI UNA RICERCA (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA 
 // SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>


<script type="text/javascript">
<!-- 

	// Azioni di pagina

	var msgErroreOra = "Per proseguire impostare correttamente l'ora di avvio";
	var msgErroreData = "Il campo 'Data di inizio' prevede un valore obbligatorio";
	
	function gestisciSubmit(){
	  var esito = true;
  	if(document.schedRicForm.tipo.value == "G"){
  		if(document.schedRicForm.oraAvvio.value == "" || document.schedRicForm.minutoAvvio.value == ""){
  			esito = false;
  			alert(msgErroreOra);	
	    } else if(document.getElementById("radioGiornoG1").checked && document.schedRicForm.giorno.value == ""){
	    	esito = false;
	    	alert("Il campo 'ogni ... giorni' prevede un valore obbligatorio");
	    } else if(document.schedRicForm.dataPrimaEsec.value == "" ){
    	  esito = false;
    	  alert(msgErroreData);
	  	}
  	}
  	
  	if (document.schedRicForm.tipo.value == "U") {
  		if(document.schedRicForm.oraAvvio.value == "" || document.schedRicForm.minutoAvvio.value == ""){
	 	 	  esito = false;
	 	 	  alert(msgErroreOra);
  		} else if(document.schedRicForm.dataPrimaEsec.value == ""){
	 	 	  esito = false;
	 	 	  alert(msgErroreData);
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

		  	if(dataOraAvvio > dataOraOdierna){
			  	if(dataOraAvvio.getTime() < (dataOraOdierna.getTime() + dieciMinuti)){
			  		if(confirm("Attenzione: la schedulazione in modifica verrà eseguita tra meno di 10 minuti. Continuare?")){
					  	esito = true;
					  } else {
						  esito = false;
					  }
					} else {
					  esito = true;
					}
				} else {
					alert("L'ora di esecuzione della schedulazione deve essere posteriore all'ora attuale");
					esito = false;
				}
			}	  	
  	}
  	
  	if (document.schedRicForm.tipo.value == "S") {
  		if(document.schedRicForm.oraAvvio.value == ""	|| document.schedRicForm.minutoAvvio.value == ""){
	 	 	  esito = false;
	 	 	  alert(msgErroreOra);
  		} else if(document.getElementById("settimanaS").value == ""){
	 	 	  esito = false;
	 	 	  alert("Il campo 'Esegui l\'operazione' prevede un valore obbligatorio");
  		} else if(!document.schedRicForm.opzioneLunedi.checked &&
  							!document.schedRicForm.opzioneMartedi.checked &&
  							!document.schedRicForm.opzioneMercoledi.checked &&
  							!document.schedRicForm.opzioneGiovedi.checked &&
  							!document.schedRicForm.opzioneVenerdi.checked &&
  							!document.schedRicForm.opzioneSabato.checked &&
  							!document.schedRicForm.opzioneDomenica.checked){
	 	 	  esito = false;
	 	 	  alert("Per proseguire selezionare almeno un giorno della settimana");
	  	}
	  }
		  
		  if(document.schedRicForm.tipo.value == "M") {
		 	if(document.schedRicForm.oraAvvio.value == "" || document.schedRicForm.minutoAvvio.value == ""){
		 		esito = false;
		 		alert(msgErroreOra);
		 	} else if(document.getElementById("radioGiornoM1").checked && document.schedRicForm.giorniMese.value == ""){
		 		esito = false;
		 		alert("Il campo 'giorno ...' prevede un valore obbligatorio");
		 	} else if(document.getElementById("radioGiornoM0").checked &&
		 	          (document.getElementById("settimanaM").value == "" || document.schedRicForm.giorniSettimana.value == "")){
		 		esito = false;
		 		alert("Il campo 'ogni ...' prevede due valori obbligatori");
		 	} else if(!document.schedRicForm.opzioneGennaio.checked &&
		 						!document.schedRicForm.opzioneFebbraio.checked &&
		 						!document.schedRicForm.opzioneMarzo.checked &&
		 						!document.schedRicForm.opzioneAprile.checked &&
		 						!document.schedRicForm.opzioneMaggio.checked &&
		 						!document.schedRicForm.opzioneGiugno.checked &&
		 						!document.schedRicForm.opzioneLuglio.checked &&
		 						!document.schedRicForm.opzioneAgosto.checked &&
				  	    !document.schedRicForm.opzioneSettembre.checked &&
	  			  	  !document.schedRicForm.opzioneOttobre.checked &&
	  			  	  !document.schedRicForm.opzioneNovembre.checked &&
	  			  	  !document.schedRicForm.opzioneDicembre.checked){
	 	 	  esito = false;
	 	 	  alert('Per proseguire selezionare almeno un mese dell\'anno');
	  	}
		}

		if(esito){
			bloccaRichiesteServer();
			document.schedRicForm.submit();
		}
	}
	
	function annullaModifiche(){
		bloccaRichiesteServer();
		document.location.href = 'DettaglioSchedRic.do?'+csrfToken+'&metodo=annullaCrea';
	}

	function nascondiPerTipo() {
		if (document.schedRicForm.tipo.value == "U") {
			document.getElementById("ore").style.display = "";
			document.getElementById("data").style.display = "";
			document.getElementById("giornoG").style.display = "none";
			document.schedRicForm.giorno.value = "";
			document.getElementById("giornoM").style.display = "none";
			document.schedRicForm.giorniMese.value = "";
			document.schedRicForm.giorniSettimana.value = "";
			document.getElementById("settimanaM").value = "";
			document.getElementById("settimana").style.display = "none";
			document.getElementById("settimanaS").value = "";
			document.getElementById("giorni").style.display = "none";
			sbiancaGiorni();
			document.getElementById("mesi").style.display = "none";
			sbiancaMesi();
			document.getElementById("settimanaS").value = "";
		}

		if (document.schedRicForm.tipo.value == "S") {
			document.getElementById("ore").style.display = "";
			document.getElementById("data").style.display = "none";
			document.schedRicForm.dataPrimaEsec.value = "";
			document.getElementById("giornoG").style.display = "none";
			document.schedRicForm.giorno.value = "";
			document.getElementById("giornoM").style.display = "none";
			document.schedRicForm.giorniMese.value = "";
			document.schedRicForm.giorniSettimana.value = "";
			document.getElementById("settimanaM").value = "";
			document.getElementById("settimana").style.display = "";
			document.getElementById("giorni").style.display = "";
			document.getElementById("mesi").style.display = "none";
			sbiancaMesi();
		}
		
		if (document.schedRicForm.tipo.value == "G") {
			document.getElementById("ore").style.display = "";
			document.getElementById("data").style.display = "";
			document.getElementById("giornoG").style.display = "";
			document.getElementById("giornoM").style.display = "none";
			document.schedRicForm.giorniMese.value = "";
			document.schedRicForm.giorniSettimana.value = "";
			document.getElementById("settimanaM").value = "";
			document.getElementById("settimana").style.display = "none";
			document.getElementById("settimanaS").value = "";
			document.getElementById("giorni").style.display = "none";
			sbiancaGiorni();
			document.getElementById("mesi").style.display = "none";
			sbiancaMesi();
			gestioneGiorni();
		}
		
		if (document.schedRicForm.tipo.value == "M") {
			document.getElementById("ore").style.display = "";
			document.getElementById("data").style.display = "none";
			document.schedRicForm.dataPrimaEsec.value = "";
			document.getElementById("giornoG").style.display = "none";
			document.schedRicForm.giorno.value = "";
			document.getElementById("giornoM").style.display = "";
			document.getElementById("settimana").style.display = "none";
			document.getElementById("settimanaS").value = "";
			document.getElementById("giorni").style.display = "none";
			sbiancaGiorni();
			document.getElementById("mesi").style.display = "";
			valorizzaMesi();
			gestioneGiorniMese();
		}
		
	}
	
	function sbiancaGiorni() {
		document.schedRicForm.opzioneLunedi.checked	= false;
		document.schedRicForm.opzioneMartedi.checked	= false;
		document.schedRicForm.opzioneMercoledi.checked	= false;
		document.schedRicForm.opzioneGiovedi.checked	= false;
		document.schedRicForm.opzioneVenerdi.checked	= false;
		document.schedRicForm.opzioneSabato.checked	= false;
		document.schedRicForm.opzioneDomenica.checked	= false;
	}
	
	function sbiancaMesi() {
		document.schedRicForm.opzioneGennaio.checked	= false;
		document.schedRicForm.opzioneFebbraio.checked	= false;
		document.schedRicForm.opzioneMarzo.checked	= false;
		document.schedRicForm.opzioneAprile.checked	= false;
		document.schedRicForm.opzioneMaggio.checked	= false;
		document.schedRicForm.opzioneGiugno.checked	= false;
		document.schedRicForm.opzioneLuglio.checked	= false;
		document.schedRicForm.opzioneAgosto.checked	= false;
		document.schedRicForm.opzioneSettembre.checked	= false;
		document.schedRicForm.opzioneOttobre.checked	= false;
		document.schedRicForm.opzioneNovembre.checked	= false;
		document.schedRicForm.opzioneDicembre.checked	= false;
		
	}
	
	function valorizzaMesi() {
		if (!document.schedRicForm.opzioneGennaio.checked &&
			!document.schedRicForm.opzioneFebbraio.checked  &&
			!document.schedRicForm.opzioneMarzo.checked  &&
			!document.schedRicForm.opzioneAprile.checked  &&
			!document.schedRicForm.opzioneMaggio.checked  &&
			!document.schedRicForm.opzioneGiugno.checked  &&
			!document.schedRicForm.opzioneLuglio.checked  &&
			!document.schedRicForm.opzioneAgosto.checked  &&
			!document.schedRicForm.opzioneSettembre.checked  &&
			!document.schedRicForm.opzioneOttobre.checked  &&
			!document.schedRicForm.opzioneNovembre.checked  &&
			!document.schedRicForm.opzioneDicembre.checked ){
		document.schedRicForm.opzioneGennaio.checked	= true;
		document.schedRicForm.opzioneFebbraio.checked	= true;
		document.schedRicForm.opzioneMarzo.checked	= true;
		document.schedRicForm.opzioneAprile.checked	= true;
		document.schedRicForm.opzioneMaggio.checked	= true;
		document.schedRicForm.opzioneGiugno.checked	= true;
		document.schedRicForm.opzioneLuglio.checked	= true;
		document.schedRicForm.opzioneAgosto.checked	= true;
		document.schedRicForm.opzioneSettembre.checked	= true;
		document.schedRicForm.opzioneOttobre.checked	= true;
		document.schedRicForm.opzioneNovembre.checked	= true;
		document.schedRicForm.opzioneDicembre.checked	= true;
		}
	}
	
	function gestioneGiorni() {
		if (document.getElementById('radioGiornoG1').checked) {
			document.schedRicForm.giorno.disabled = false;
			document.schedRicForm.ripetiDopoMinuti.value = "";
			document.schedRicForm.ripetiDopoMinuti.disabled = true;
		}
		
		if (document.getElementById('radioGiornoG0').checked) {
			document.schedRicForm.ripetiDopoMinuti.disabled = false;
			document.schedRicForm.giorno.value = "";
			document.schedRicForm.giorno.disabled = true;
		}
	}
	
	function gestioneGiorniMese() {
	 //alert(document.getElementById('radioGiornoM1').checked);
		if (document.getElementById('radioGiornoM0').checked) {
			document.schedRicForm.giorniMese.value = "";
			document.schedRicForm.giorniMese.disabled = true;
			document.schedRicForm.giorniSettimana.disabled = false;
			document.getElementById('settimanaM').disabled = false;
		}
		if (document.getElementById('radioGiornoM1').checked) {
			document.schedRicForm.giorniMese.disabled = false;
			document.schedRicForm.giorniSettimana.value = "";
			document.schedRicForm.giorniSettimana.disabled = true;
			document.getElementById('settimanaM').value = "";
			document.getElementById('settimanaM').disabled = true;

		}
	}
	
	function controllaInputData(unCampoDiInput){
	  return isData(unCampoDiInput);
	}
-->
</script>