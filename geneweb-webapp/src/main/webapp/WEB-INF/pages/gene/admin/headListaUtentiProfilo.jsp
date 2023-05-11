<%
/*
 * Created on 02-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA UTENTI DI
 // PROFILO (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA SEZIONE JAVASCRIPT
%>


<script type="text/javascript">
<!--
	
  function modificaAssUtentiProfilo(id){
		document.location.href='ListaAccountProfilo.do?'+csrfToken+'&metodo=editLista&codPro=' + id;
  }

<!-- Azioni invocate dal tab menu -->

	function listaAccountProfilo(id){
		document.location.href='ListaAccountProfilo.do?'+csrfToken+'&metodo=visualizza&codPro=' + id;
	}

	function dettaglioProfilo(id){
		document.location.href='DettaglioProfilo.do?'+csrfToken+'&codPro=' + id;
	}
	
	function listaGruppiProfilo(id){
		document.location.href='ListaGruppiProfilo.do?'+csrfToken+'&metodo=visualizza&codPro=' + id;
	}
	
-->
</script>