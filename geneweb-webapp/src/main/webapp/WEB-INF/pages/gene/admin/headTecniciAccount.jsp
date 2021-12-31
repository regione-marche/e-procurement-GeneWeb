<%
/*
 * Created on 12-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA PROFILO 
 // DI UN UTENTE (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA SEZIONE JAVASCRIPT
%>

<script type="text/javascript">
	
   
 	function schedaVisualizza(id){
		document.location.href = 'geneAdmin/DettaglioAccount.do?'+csrfToken+'&metodo=visualizza&idAccount=' + id;
	}
	 	
	function listaGruppiAccount(id){
		document.location.href='geneAdmin/ListaGruppiAccount.do?'+csrfToken+'&idAccount=' + id + '&metodo=visualizzaLista';
  }
	 	
	function listaProfiliAccount(id){
		document.location.href='geneAdmin/ListaProfiliAccount.do?'+csrfToken+'&idAccount=' + id + '&metodo=visualizza';
  }
	
	function listaUfficiIntestatariAccount(id){
		document.location.href='geneAdmin/ListaUfficiIntestatariAccount.do?'+csrfToken+'&idAccount=' + id + '&metodo=visualizza';
  }
    	
	

</script>