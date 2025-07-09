<%
/*
 * Created on 14-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA UTENTI DI
 // GRUPPO (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA SEZIONE JAVASCRIPT
%>


<script type="text/javascript">
<!--

	function creaGruppo(){
		document.location.href='CreaGruppo.do';
	}

	function listaGruppi(){
		document.location.href='ListaGruppi.do';
	}
	
  function modificaAssUtentiGruppo(id){
		document.location.href='ListaUtentiGruppo.do?metodo=editLista&idGruppo=' + id;
  }

<!-- Azioni invocate dal tab menu -->

	function dettaglioGruppo(id){
		document.location.href='DettaglioGruppo.do?idGruppo=' + id;
	}

	function listaUtentiGruppo(id){
		document.location.href='ListaUtentiGruppo.do?metodo=visualizzaLista&idGruppo=' + id;
	}

	function listaRicercheGruppo(id){
		document.location.href='ListaRicercheGruppo.do?metodo=visualizzaLista&idGruppo=' + id;
	}

	function listaModelliGruppo(id){
		document.location.href='ListaModelliGruppo.do?metodo=visualizzaLista&idGruppo=' + id;
	}
	
-->
</script>

