<%
/*
 * Created on 20-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA MODELLI DI
 // GRUPPO (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA SEZIONE JAVASCRIPT
%>


<script type="text/javascript">
<!--

	function creaGruppo(){
		document.location.href='CreaGruppo.do?'+csrfToken;
	}

	function listaGruppi(){
		document.location.href='ListaGruppi.do?'+csrfToken;
	}
	
	function creaNuovoModello(){
		alert('A questo punto va lanciata la Action: document.location.href=CreaModello.do, con CreaModello.do fra apici.');
	}	
	
  function modificaAssModelliGruppo(id){
		document.location.href='ListaModelliGruppo.do?'+csrfToken+'&metodo=editLista&idGruppo=' + id;
	}

<!-- Azioni invocate dal tab menu -->

	function dettaglioGruppo(id){
		document.location.href='DettaglioGruppo.do?'+csrfToken+'&idGruppo=' + id;
	}

	function listaUtentiGruppo(id){
		document.location.href='ListaUtentiGruppo.do?'+csrfToken+'&metodo=visualizzaLista&idGruppo=' + id;
	}

	function listaRicercheGruppo(id){
		document.location.href='ListaRicercheGruppo.do?'+csrfToken+'&metodo=visualizzaLista&idGruppo=' + id;
	}

	function listaModelliGruppo(id){
		document.location.href='ListaModelliGruppo.do?'+csrfToken+'&metodo=visualizzaLista&idGruppo=' + id;
	}
	
-->
</script>

