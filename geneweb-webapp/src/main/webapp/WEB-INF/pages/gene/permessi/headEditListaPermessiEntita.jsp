<%
/*
 * Created on 26-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA UTENTI 
 // ASSOCIATI ALL'ENTITA IN ANALISI (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA
 // SEZIONE JAVASCRIPT
%>

<script type="text/javascript">
	
  function salvaModifiche(){
		bloccaRichiesteServer();
		document.permessiAccountEntitaForm.submit();
  }
  
	function annullaModifiche(){
		document.location.href='ListaPermessiEntita.do?'+csrfToken+'&metodo=visualizza&campoChiave=${campoChiave}&valoreChiave=${valoreChiave}&genereGara=${genereGara}';
	}
	
	function controlloRiga(idRiga){
	  var idAssocia = "associa" + idRiga;
    var idAutoriz = "autoriz" + idRiga;
    var idPropr   = "propr"   + idRiga;
		var objCheckAssocia = document.getElementById("associa"+idRiga);
		if(objCheckAssocia.checked){
      //Attivazione degli oggetti presenti nella riga
      //MOD SS131109 - Quando un utente viene abilitato, di default gli viene assegnato il privilegio di 'Sola lettura'
			document.getElementById(idAutoriz).disabled = false;
			//document.getElementById(idAutoriz).selectedIndex = 0;
			document.getElementById(idPropr).disabled = false;
			setAutorizzazione(idRiga);
			setCondividiEntita(idRiga);
		} else {
      //Disattivazione degli oggetti presenti nella riga
			document.getElementById(idAutoriz).disabled = true;
			document.getElementById(idAutoriz).selectedIndex = 0;
			document.getElementById(idPropr).checked = false;
			document.getElementById(idPropr).disabled = true;
			resetAutorizzazione(idRiga);
			resetProprietario(idRiga);
			resetCondividiEntita(idRiga);
		}
	}
	
	function setAutorizzazione(idRiga){
		var objAutoriz = document.getElementById("autoriz" + idRiga);
		var objAutorizzazione = document.getElementById("autorizzazione" + idRiga);
		objAutorizzazione.value = objAutoriz.options[objAutoriz.selectedIndex].value;
	}
	
	function resetAutorizzazione(idRiga){
		var idAutoriz = "autoriz" + idRiga;
		var objAutoriz = document.getElementById(idAutoriz);
		var objAutorizzazione = document.getElementById("autorizzazione" + idRiga);
	}

	function setProprietario(idRiga){
		if(document.getElementById("propr" + idRiga).checked)
			document.getElementById("proprietario" + idRiga).value = 1;
		else
			document.getElementById("proprietario" + idRiga).value = 2;
	}
	
	function resetProprietario(idRiga){
		document.getElementById('proprietario' + idRiga).value = 2;
	}
	
	function selezionaTutteEntita(object){
		selezionaTutti(object);
		var len = object.length;
		for(var i=0; i < len; i++){
			controlloRiga(i);
		}
	}
		
	function deselezionaTutteEntita(object){
		deselezionaTutti(object);
		var len = object.length;
		for(var i=0; i < len; i++){
			controlloRiga(i);
		}
	}
	
	function setCondividiEntita(idRiga){
		document.getElementById('condividiEntita' + idRiga).value = 1;
	}
	
	function resetCondividiEntita(idRiga){
		document.getElementById('condividiEntita' + idRiga).value = 0;
	}
	
	function init(){
		
	}
</script>

<jsp:include page="/WEB-INF/pages/gene/permessi/headEditListaPermessiPersonalizzazione.jsp" />