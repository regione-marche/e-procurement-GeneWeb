<%
/*
 * Created on 19-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI CAMBIO PASSWORD CONTENENTE LA 
 // SEZIONE JAVASCRIPT
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

	<fmt:setBundle basename="AliceResources" />
	<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript" src="${contextPath}/js/forms.js"></script>
<script type="text/javascript">
<!--
	function gestisciSubmit() {
	  var esito = true;
	  
  <c:if test='${(controlloPasswordUtenteAttivo) or ("1" ne passwordNullable)}'>
	  if (esito && !controllaCampoPasswordObbligatorio(cambiaPasswordAdminForm.nuovaPassword, 'Nuova Password'))
 	    esito = false;
	  if (esito && !controllaCampoPasswordObbligatorio(cambiaPasswordAdminForm.confermaNuovaPassword, 'Conferma Nuova Password'))
 	    esito = false;
  </c:if>
 	  if (esito && (cambiaPasswordAdminForm.nuovaPassword.value != cambiaPasswordAdminForm.confermaNuovaPassword.value)) {
 	    alert("<fmt:message key="errors.chgPsw.nuovePasswordDiverse"/>");
 	    esito = false;
 	  }
 	 <c:if test='${controlloPasswordUtenteAttivo}' >
 	<c:choose>
 		<c:when test='${controlloAmministratoreUtenteAttivo}'>
 			if (esito && !controllaCampoPassword(cambiaPasswordAdminForm.nuovaPassword, 14, true)) {
 			esito = false;
 		}
 		</c:when>
 		<c:otherwise>
 		 	if (esito && !controllaCampoPassword(cambiaPasswordAdminForm.nuovaPassword, 8, true)) {
 		 	esito = false;
 		}
 		</c:otherwise>
 	</c:choose>	
 </c:if>
 	  if (esito) {
  		bloccaRichiesteServer();
 	  	document.cambiaPasswordAdminForm.metodo.value = 'modifica';
 	    document.cambiaPasswordAdminForm.submit();
 	  }
	}
	
	function annulla(){
		bloccaRichiesteServer();
		document.location.href='CambiaPasswordAdmin.do?'+csrfToken+'&metodo=annulla';
	}
-->
  </script>
