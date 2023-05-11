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

<script type="text/javascript">
<!-- 

<c:set var="array" value='var elencoFamiglie = new Array ('/>
<c:forEach items="${listaRicerche}" var="parametro" varStatus="status">
	<c:choose>
		<c:when test="${status.last}" >
			<c:set var="array" value='${array} \'${parametro.famiglia}\''/>
		</c:when>
		<c:otherwise>
			<c:set var="array" value='${array} \'${parametro.famiglia}\','/>
		</c:otherwise>
	</c:choose>
</c:forEach>
<c:set var="array" value="${array} );" />
<c:out value="${array}" escapeXml="false" />
<c:remove var="array" />	


	function avanti(){
		var esito = true;
		
    if(document.schedRicForm.idRicerca.value == ""){
 	 	  esito = false;
 	 	  alert("Il campo 'Report' prevede un valore obbligatorio");
  	} else {
  		var idRicercaSel = document.schedRicForm.idRicerca.selectedIndex;
  		if(elencoFamiglie[idRicercaSel-1] == "Report con modello" || elencoFamiglie[idRicercaSel-1] == "2")
  			document.schedRicForm.descFormato.value = "prospetto";
  		else
	  		document.schedRicForm.descFormato.value = "";
  	}
    if(esito){
		  document.schedRicForm.submit();
		}
	}
	
	function annulla(){
		if (confirm('<fmt:message key="info.schedRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
		  document.location.href='DettaglioSchedRic.do?'+csrfToken+'&metodo=annullaCrea';
		}
	}

-->
</script>