<%
/*
 * Created on 13-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LOGIN CONTENENTE 
 // IL CODICE JAVASCRIPT
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript"
	src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript">
<!--
	function gestisciSubmit(ilForm) {
	  var esito = true;
	  if (esito && !controllaCampoInputObbligatorio(ilForm.username, 'UTENTE'))
 	    esito = false;
 	  // SS 06/11/2006: parametrizzato il controllo per consentire l'accesso anche a utenti senza password
<c:if test='${applicationScope.passwordNullable ne "1"}' >
	  if (esito && !controllaCampoInputObbligatorio(ilForm.password, 'PASSWORD'))
 	    esito = false;
</c:if>
	  return esito;	   
	}
-->
  </script>
