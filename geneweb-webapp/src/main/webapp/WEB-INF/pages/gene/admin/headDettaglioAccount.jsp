<%/*
       * Created on 20-Ott-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE LA DEFINIZIONE DELLE VOCI DEI MENU COMUNI A TUTTE LE APPLICAZIONI
      %>
<!-- Inserisco la Tag Library -->

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<script type="text/javascript">

	
	<elda:jsFunctionOpzioniListaRecord contextPath="${pageContext.request.contextPath}"/>
<%	
	//function schedaModifica(id){
		//document.location.href='DettaglioAccount.do?metodo=carica&modo=modifica&idAccount=' + id;
  	//}
%>

	function schedaModifica(id){
		document.location.href='DettaglioAccount.do?'+csrfToken+'&metodo=modifica&idAccount=' + id;
 	}
	
	function schedaAnnulla(){
		document.accountForm.metodo.value="annulla";
		bloccaRichiesteServer();
		document.accountForm.submit();
	}

	function listaGruppiAccount(id){
		document.location.href='ListaGruppiAccount.do?'+csrfToken+'&idAccount=' + id + '&metodo=visualizzaLista';
  }
  	
	function listaProfiliAccount(id){
		document.location.href='ListaProfiliAccount.do?'+csrfToken+'&idAccount=' + id + '&metodo=visualizza';
  }
    	
	function listaUfficiIntestatariAccount(id){
		document.location.href='ListaUfficiIntestatariAccount.do?'+csrfToken+'&idAccount=' + id + '&metodo=visualizza';
  }
    	
  function schedaNuovo(){
		document.location.href = 'InitCreaAccount.do?'+csrfToken;
	}
	
	function schedaTecniciAccount(id){
		var chiave= "USRSYS.SYSCON=N:" + id;
		document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gene/tecni/tecni-Account.jsp&key="+chiave;
		
	}		
</script>

