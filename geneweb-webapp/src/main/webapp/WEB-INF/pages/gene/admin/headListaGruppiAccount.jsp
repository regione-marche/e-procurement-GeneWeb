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


	
	
  function modificaAssGruppiAccount(id){
		document.location.href='ListaGruppiAccount.do?'+csrfToken+'&metodo=editLista&idAccount=' + id;
  }
  
  	
	function listaProfiliAccount(id){
		document.location.href='ListaProfiliAccount.do?'+csrfToken+'&idAccount=' + id + '&metodo=visualizza';
  }
 
	function listaUfficiIntestatariAccount(id){
		document.location.href='ListaUfficiIntestatariAccount.do?'+csrfToken+'&idAccount=' + id + '&metodo=visualizza';
  }
    	
 	function schedaVisualizza(id){
		document.location.href = 'DettaglioAccount.do?'+csrfToken+'&metodo=visualizza&idAccount=' + id;
	}
	
	function schedaNuovo(){
		document.location.href = 'InitCreaAccount.do?'+csrfToken;
	}
	
	function schedaTecniciAccount(id){
		var chiave= "USRSYS.SYSCON=N:" + id;
		document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gene/tecni/tecni-Account.jsp&key="+chiave;
		
	}	
</script>

