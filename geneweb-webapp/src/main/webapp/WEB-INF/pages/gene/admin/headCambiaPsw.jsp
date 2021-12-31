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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="scaduta" value="${passwordScaduta}"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript"
  src="${contextPath}/js/forms.js"></script>
<script type="text/javascript">
<!--
	function gestisciSubmit() {
	  var esito = true;

<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou39#") or "1" ne passwordNullable}'>
	<c:if test='${! vecchiaPasswordIsNull}'>
	  if (esito && !controllaCampoPasswordObbligatorio(cambiaPasswordForm.vecchiaPassword, 'Vecchia Password'))
 	    esito = false;
 	</c:if>
	  if (esito && !controllaCampoPasswordObbligatorio(cambiaPasswordForm.nuovaPassword, 'Nuova Password'))
 	    esito = false;
	  if (esito && !controllaCampoPasswordObbligatorio(cambiaPasswordForm.confermaNuovaPassword, 'Conferma Nuova Password'))
 	    esito = false;
  </c:if>
 	  if (esito && (cambiaPasswordForm.nuovaPassword.value != cambiaPasswordForm.confermaNuovaPassword.value)) {
 	    alert("<fmt:message key="errors.chgPsw.nuovePasswordDiverse"/>");
 	    esito = false;
 	  }
<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou39#")}'>
	<c:if test='${! vecchiaPasswordIsNull}'>
 	  if (esito && (cambiaPasswordForm.nuovaPassword.value == cambiaPasswordForm.vecchiaPassword.value)) {
 	    alert("<fmt:message key="errors.chgPsw.nuovaPasswordDiversaPrecedente"/>");
 	    esito = false;
 	  }
	</c:if> 	
	<c:choose>
	<c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou89#")}' >
	  if (esito && !controllaCampoPassword(cambiaPasswordForm.nuovaPassword, 14, true)) {
		esito = false;
	  }
	</c:when>
	<c:otherwise>
	  if (esito && !controllaCampoPassword(cambiaPasswordForm.nuovaPassword, 8, true)) {
		esito = false;
	  }
	</c:otherwise>
	</c:choose>
</c:if>

 	  if (esito) {
			bloccaRichiesteServer();
	<c:choose>
		<c:when test='${scaduta eq "scaduta"}'>
			document.cambiaPasswordForm.metodo.value = 'modificaPasswordScaduta';
		</c:when>
		<c:otherwise>
			document.cambiaPasswordForm.metodo.value = 'modifica';
		</c:otherwise>
	</c:choose>
 	  	
	    document.cambiaPasswordForm.submit();
	  }
	}
	
	function annulla(){
		<c:choose>
			<c:when test='${scaduta eq "scaduta"}'>
					if (confirm('Sei sicuro di volerti disconnettere?'))
						document.location.href='${contextPath}/Logout.do';
			</c:when>
			<c:otherwise>
				bloccaRichiesteServer();
				document.location.href='CambiaPassword.do?'+csrfToken+'&metodo=annulla';
			</c:otherwise>
		</c:choose>
	}
-->
</script>